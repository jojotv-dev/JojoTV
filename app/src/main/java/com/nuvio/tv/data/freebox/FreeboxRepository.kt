package com.nuvio.tv.data.freebox

import android.content.Context
import android.media.MediaDataSource
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.msfscc.FileAttributes
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2CreateOptions
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.SmbConfig
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.connection.Connection
import com.hierynomus.smbj.share.DiskShare
import com.hierynomus.smbj.share.File as SmbjFile
import com.hierynomus.smbj.session.Session
import jcifs.CIFSContext
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbException
import jcifs.smb.SmbFile as JcifsSmbFile
import jcifs.smb.SmbRandomAccessFile
import com.nuvio.tv.core.profile.ProtectedProfilePreferences
import com.nuvio.tv.data.storage.SmbStorageCapacity
import com.nuvio.tv.data.storage.StorageCapacity
import com.nuvio.tv.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.nuvio.tv.domain.model.CatalogRow
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.PosterShape
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.TimeUnit
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.util.Properties
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToLong

data class FreeboxEntry(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val sizeBytes: Long = 0L,
    val lastModifiedMs: Long = 0L,
    val fileId: Long = 0L,
) {
    val isVideoFile: Boolean
        get() = !isDirectory && name.substringAfterLast('.', "").lowercase() in FREEBOX_VIDEO_EXTENSIONS
}

data class FreeboxVideoMetadata(
    val title: String? = null,
    val year: Int? = null,
    val genres: List<String> = emptyList(),
    val durationMinutes: Long? = null
)

internal fun parseFreeboxVideoMetadata(
    title: String?,
    date: String?,
    genre: String?,
    durationMs: Long?
): FreeboxVideoMetadata {
    val yearRegex = Regex("\\b(?:19|20)\\d{2}\\b")
    val normalizedTitle = title?.trim()?.takeIf(String::isNotBlank)
    val year = date?.let(yearRegex::find)?.value?.toIntOrNull()
        ?: normalizedTitle?.let(yearRegex::find)?.value?.toIntOrNull()
    val genres = genre.orEmpty()
        .split(Regex("[,;/|]+"))
        .map(String::trim)
        .filter(String::isNotBlank)
        .distinctBy { it.lowercase(java.util.Locale.ROOT) }
    val durationMinutes = durationMs
        ?.takeIf { it > 0L }
        ?.let { ((it + 30_000L) / 60_000L).takeIf { minutes -> minutes > 0L } }
    return FreeboxVideoMetadata(normalizedTitle, year, genres, durationMinutes)
}

@Serializable
data class FreeboxFolderArtwork(
    val portrait: String? = null,
    val landscape: String? = null,
    val tmdbId: Int? = null,
    val tmdbContentType: String? = null,
)

internal const val FREEBOX_FOLDER_ID_PREFIX = "freebox-folder:"
internal val FREEBOX_FOLDER_COVER_URI =
    "android.resource://${BuildConfig.APPLICATION_ID}/drawable/freebox_folder_cover"

private val FREEBOX_VIDEO_EXTENSIONS = setOf("mkv", "mp4", "avi", "mov", "m4v", "ts", "webm", "mpg", "mpeg", "m2ts")
private val FREEBOX_ARTWORK_EPISODE_ONLY_TITLE = Regex("(?iu)^(?:(?:episode|ep|épisode)\\s*)?\\d{1,4}$|^s\\d{1,3}e\\d{1,4}$|^e\\d{1,4}$")
private val FREEBOX_ARTWORK_SEASON_FOLDER = Regex("(?iu)^(?:s\\d{1,3}|season\\s*\\d{1,3}|saison\\s*\\d{1,3})$")
private val FREEBOX_ARTWORK_DATE_ONLY = Regex("(?iu)^(?:(?:19|20)\\d{2}[-_. ]\\d{1,2}[-_. ]\\d{1,2}|\\d{1,2}[-_. ]\\d{1,2}[-_. ](?:19|20)\\d{2}|(?:19|20)\\d{6})$")
private val FREEBOX_ARTWORK_DATE_PREFIX = Regex("(?iu)^(?:(?:19|20)\\d{2}[-_. ]\\d{1,2}[-_. ]\\d{1,2}|\\d{1,2}[-_. ]\\d{1,2}[-_. ](?:19|20)\\d{2}|(?:19|20)\\d{6})[\\s._-]+")
private val FREEBOX_ARTWORK_RECURRING_SUFFIX = Regex(
    "(?iu)[\\s._-]+(?:(?:s\\d{1,3}\\s*e\\d{1,4}|\\d{1,2}\\s*x\\s*\\d{1,4})(?:.*)|(?:season|saison)\\s*\\d{1,3}[\\s._-]*(?:episode|ep|épisode|e)\\s*\\d{1,4}(?:.*)|(?:episode|ep|épisode)\\s*\\d{1,4}(?:.*)|(?:(?:19|20)\\d{2}[-_. ]\\d{1,2}[-_. ]\\d{1,2}|\\d{1,2}[-_. ]\\d{1,2}[-_. ](?:19|20)\\d{2}|(?:19|20)\\d{6})(?:.*))$"
)
private val FREEBOX_ARTWORK_GENERIC_FOLDER_KEYS = setOf(
    "video", "videos", "film", "films", "movie", "movies", "serie", "series", "série", "séries",
    "show", "shows", "recording", "recordings", "enregistrement", "enregistrements", "tv", "media", "freebox",
)

/** An open, seekable jCIFS file handle used to read Freebox files. */
class FreeboxPlaybackFile internal constructor(
    private val readAt: (Long, ByteArray, Int, Int) -> Int,
    private val resources: List<AutoCloseable>,
    val length: Long,
) : Closeable {
    private var position = 0L

    @Synchronized
    fun seek(position: Long) {
        require(position >= 0L) { "Position SMB invalide." }
        this.position = position
    }

    @Synchronized
    fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length <= 0) return 0
        val count = readAt(position, buffer, offset, length)
        if (count > 0) position += count
        return count
    }

    override fun close() {
        resources.forEach { resource -> runCatching { resource.close() } }
    }
}

/** Keeps the authenticated SMB transport alive across reads from one Freebox file. */
class FreeboxPlaybackSession internal constructor(
    private val file: JcifsSmbFile,
    private val context: CIFSContext,
    val length: Long,
) : Closeable {
    private var closed = false

    @Synchronized
    fun openFile(): FreeboxPlaybackFile {
        check(!closed) { "La session SMB Freebox est fermée." }
        val randomAccessFile = SmbRandomAccessFile(file, "r")
        return FreeboxPlaybackFile(
            readAt = { position, buffer, offset, requestedLength ->
                randomAccessFile.seek(position)
                randomAccessFile.read(buffer, offset, requestedLength)
            },
            resources = listOf(randomAccessFile),
            length = length,
        )
    }

    @Synchronized
    override fun close() {
        if (closed) return
        closed = true
        runCatching { context.close() }
    }
}

data class FreeboxUiState(
    val address: String = "",
    val username: String = "",
    val password: String = "",
    val connected: Boolean = false,
    val currentPath: String = "",
    val entries: List<FreeboxEntry> = emptyList(),
    val selectedRoots: Set<String> = emptySet(),
    val hiddenHomePaths: Set<String> = emptySet(),
    val folderArtwork: Map<String, FreeboxFolderArtwork> = emptyMap(),
    val videoArtworkByName: Map<String, FreeboxFolderArtwork> = emptyMap(),
    val isBusy: Boolean = false,
    val error: String? = null
)

@Serializable
private data class StoredFreebox(
    val address: String = "",
    val username: String = "",
    val password: String = "",
    val selectedRoots: Set<String> = emptySet(),
    val hiddenHomePaths: Set<String> = emptySet(),
    val folderArtwork: Map<String, FreeboxFolderArtwork> = emptyMap(),
    val videoArtworkByName: Map<String, FreeboxFolderArtwork> = emptyMap(),
)

@Singleton
class FreeboxRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val preferences = ProtectedProfilePreferences(
        context,
        "freebox_smb",
        "com.nuvio.tv.freebox.v1"
    )
    private val json = Json { ignoreUnknownKeys = true }
    private val mutableState = MutableStateFlow(loadState())
    val state: StateFlow<FreeboxUiState> = mutableState.asStateFlow()
    private val mutableHomeRows = MutableStateFlow<List<CatalogRow>>(emptyList())
    val homeRows: StateFlow<List<CatalogRow>> = mutableHomeRows.asStateFlow()

    /** Freebox presentation settings can follow the account; SMB credentials remain on this TV. */
    fun profileSettingsSyncSnapshot(): String {
        val stored = mutableState.value.toStored()
        val shareable = stored.copy(
            username = "",
            password = "",
            selectedRoots = stored.selectedRoots.toSortedSet(),
            hiddenHomePaths = stored.hiddenHomePaths.toSortedSet(),
            folderArtwork = stored.folderArtwork.toSortedMap(),
            videoArtworkByName = stored.videoArtworkByName.toSortedMap()
        )
        return json.encodeToString(StoredFreebox.serializer(), shareable)
    }

    suspend fun applyProfileSettingsSyncSnapshot(snapshot: String) = withContext(Dispatchers.IO) {
        val remote = runCatching { json.decodeFromString<StoredFreebox>(snapshot) }.getOrNull()
            ?: return@withContext
        val current = mutableState.value
        val local = current.toStored()
        val merged = remote.copy(username = local.username, password = local.password)
        val addressChanged = merged.address != local.address
        save(merged)
        mutableState.value = current.copy(
            address = merged.address,
            connected = if (addressChanged) false else current.connected,
            currentPath = if (addressChanged) "" else current.currentPath,
            entries = if (addressChanged) emptyList() else current.entries,
            selectedRoots = merged.selectedRoots,
            hiddenHomePaths = merged.hiddenHomePaths,
            folderArtwork = merged.folderArtwork,
            videoArtworkByName = merged.videoArtworkByName,
            error = null
        )
        refreshHomeRows()
    }

    private val homeRowsRefreshMutex = Mutex()
    private val playbackProxyRegistryLock = Any()
    private val playbackProxyClosersByPath = mutableMapOf<String, MutableSet<() -> Unit>>()
    private val videoDurationMinutesCache = ConcurrentHashMap<String, Long>()
    private val videoMetadataCache = ConcurrentHashMap<String, FreeboxVideoMetadata>()
    private val homeVideoPresentations = ConcurrentHashMap<String, MetaPreview>()
    private val homeVideoPresentationFlows = ConcurrentHashMap<String, MutableStateFlow<MetaPreview?>>()
    private val homeVideoPresentationsByPath = ConcurrentHashMap<String, MetaPreview>()
    private val homeVideoPresentationPathFlows = ConcurrentHashMap<String, MutableStateFlow<MetaPreview?>>()

    internal fun registerPlaybackProxy(relativePath: String, closeAndAwait: () -> Unit): Closeable {
        val key = playbackProxyPathKey(relativePath)
        synchronized(playbackProxyRegistryLock) {
            playbackProxyClosersByPath.getOrPut(key) { linkedSetOf() }.add(closeAndAwait)
        }
        return Closeable {
            synchronized(playbackProxyRegistryLock) {
                val callbacks = playbackProxyClosersByPath[key] ?: return@synchronized
                callbacks.remove(closeAndAwait)
                if (callbacks.isEmpty()) playbackProxyClosersByPath.remove(key)
            }
        }
    }

    private fun closePlaybackProxiesAndAwait(relativePath: String) {
        val key = playbackProxyPathKey(relativePath)
        val prefix = "$key/"
        val callbacks = synchronized(playbackProxyRegistryLock) {
            playbackProxyClosersByPath.asSequence()
                .filter { (registeredPath, _) -> registeredPath == key || registeredPath.startsWith(prefix) }
                .flatMap { (_, closers) -> closers.asSequence() }
                .toList()
        }
        var firstFailure: Exception? = null
        callbacks.forEach { closeAndAwait ->
            try {
                closeAndAwait()
            } catch (error: Exception) {
                if (firstFailure == null) firstFailure = error
            }
        }
        firstFailure?.let { error ->
            throw IOException("Impossible de fermer le lecteur Freebox avant la suppression.", error)
        }
    }

    private fun playbackProxyPathKey(relativePath: String): String =
        relativePath.replace('\\', '/').trim('/').lowercase(java.util.Locale.ROOT)

    suspend fun connect(address: String, username: String, password: String) = withContext(Dispatchers.IO) {
        mutableState.value = mutableState.value.copy(isBusy = true, error = null)
        val config = mutableState.value.toStored().copy(
            address = address.trim(),
            username = username.trim(),
            password = password,
        )
        try {
            val folders = list(config, "").filter { it.isDirectory }
            save(config)
            mutableState.value = FreeboxUiState(
                address = config.address,
                username = config.username,
                password = config.password,
                connected = true,
                entries = folders,
                selectedRoots = config.selectedRoots,
                hiddenHomePaths = config.hiddenHomePaths,
                folderArtwork = config.folderArtwork,
                videoArtworkByName = config.videoArtworkByName,
            )
            mutableHomeRows.value = selectedRows(config)
        } catch (error: Exception) {
            runCatching { save(config) }
            mutableState.value = mutableState.value.copy(
                address = config.address,
                username = config.username,
                password = config.password,
                isBusy = false,
                connected = false,
                error = error.message ?: "Connexion SMB impossible"
            )
        }
    }

    suspend fun browse(path: String) = withContext(Dispatchers.IO) {
        val current = mutableState.value
        val config = current.toStored()
        mutableState.value = current.copy(isBusy = true, error = null)
        try {
            val entries = list(config, path)
            mutableState.value = current.copy(isBusy = false, connected = true, currentPath = path, entries = entries, error = null)
        } catch (error: Exception) {
            mutableState.value = current.copy(isBusy = false, error = error.message ?: "Lecture du dossier impossible")
        }
    }

    suspend fun toggleRoot(path: String, selected: Boolean) = withContext(Dispatchers.IO) {
        val current = mutableState.value
        val roots = if (selected) current.selectedRoots + path else current.selectedRoots - path
        val hiddenHomePaths = if (selected) {
            current.hiddenHomePaths.filterNot { hidden -> isSameOrDescendant(hidden, path) }.toSet()
        } else {
            current.hiddenHomePaths
        }
        val updated = current.copy(selectedRoots = roots, hiddenHomePaths = hiddenHomePaths)
        save(updated.toStored())
        mutableState.value = updated
        mutableHomeRows.value = selectedRows(updated.toStored())
    }

    suspend fun removeFolderFromHome(relativePath: String) = withContext(Dispatchers.IO) {
        val path = normalizeRelativePath(relativePath)
        val current = mutableState.value
        val config = current.toStored()
        val hidden = config.hiddenHomePaths + path
        val updated = config.copy(hiddenHomePaths = hidden)
        save(updated)
        mutableState.value = current.copy(hiddenHomePaths = hidden)
        mutableHomeRows.value = selectedRows(updated)
    }

    suspend fun setFolderArtwork(
        relativePath: String,
        portrait: Boolean,
        imageUrl: String?,
        tmdbId: Int? = null,
        tmdbContentType: String? = null,
        matchVideosByName: Boolean = true,
    ) =
        withContext(Dispatchers.IO) {
            val path = normalizeRelativePath(relativePath)
            val normalizedUrl = imageUrl?.trim()?.takeIf(String::isNotBlank)
            require(normalizedUrl == null || Uri.parse(normalizedUrl).scheme == "https") {
                "L’adresse de la vignette doit être une URL HTTPS."
            }

            val current = mutableState.value
            val config = current.toStored()
            val anyVideoNameKey = videoArtworkNameKey(path)
            val videoNameKey = anyVideoNameKey.takeIf { matchVideosByName }
            val old = if (matchVideosByName) {
                findVideoArtworkByName(config.videoArtworkByName, anyVideoNameKey) ?: config.folderArtwork[path]
            } else {
                config.folderArtwork[path] ?: findVideoArtworkByName(config.videoArtworkByName, anyVideoNameKey)
            }
                ?: FreeboxFolderArtwork()
            val updatedArt = if (portrait) old.copy(portrait = normalizedUrl)
                else old.copy(landscape = normalizedUrl)
            val replacement = when {
                normalizedUrl != null && tmdbId != null -> updatedArt.copy(
                    tmdbId = tmdbId,
                    tmdbContentType = tmdbContentType,
                )
                updatedArt.portrait == null && updatedArt.landscape == null -> updatedArt.copy(
                    tmdbId = null,
                    tmdbContentType = null,
                )
                else -> updatedArt
            }
            val artwork: Map<String, FreeboxFolderArtwork>
            val videoArtworkByName: Map<String, FreeboxFolderArtwork>
            if (videoNameKey != null) {
                // Keep recurring programme art under one stable key and retire old episode-specific keys.
                artwork = config.folderArtwork
                val legacyKeys = config.videoArtworkByName.keys
                    .filter { normalizeFreeboxArtworkTitle(it) == videoNameKey }
                videoArtworkByName = if (replacement.portrait == null && replacement.landscape == null) {
                    config.videoArtworkByName - legacyKeys.toSet()
                } else {
                    (config.videoArtworkByName - legacyKeys.toSet()) + (videoNameKey to replacement)
                }
            } else {
                val isPathScopedVideoOverride = anyVideoNameKey != null && !matchVideosByName
                artwork = when {
                    replacement.portrait != null || replacement.landscape != null ->
                        config.folderArtwork + (path to replacement)
                    isPathScopedVideoOverride ->
                        // An empty entry masks name-based artwork for this episode after reset.
                        config.folderArtwork + (path to replacement)
                    else -> config.folderArtwork - path
                }
                videoArtworkByName = config.videoArtworkByName
            }
            val updated = config.copy(folderArtwork = artwork, videoArtworkByName = videoArtworkByName)
            save(updated)
            mutableState.value = current.copy(
                folderArtwork = artwork,
                videoArtworkByName = videoArtworkByName,
            )
            mutableHomeRows.value = selectedRows(updated)
        }

    /** Delete a folder below a selected Freebox root, then remove its Home metadata. */
    suspend fun deleteHomeFolder(relativePath: String): List<String> = withContext(Dispatchers.IO) {
        homeRowsRefreshMutex.withLock {
            val path = normalizeRelativePath(relativePath)
            val current = mutableState.value
            val config = current.toStored()
            require(config.selectedRoots.any { root ->
                val normalizedRoot = normalizeRelativePath(root)
                !path.equals(normalizedRoot, ignoreCase = true) && isSameOrDescendant(path, normalizedRoot)
            }) { "La suppression est limitée aux dossiers affichés depuis l’accueil." }
            require(config.address.isNotBlank()) { "Connectez-vous d’abord à la Freebox." }

            val target = SmbAddress.parse(config.address)
            val sharePath = listOf(target.path, path)
                .filter(String::isNotBlank)
                .joinToString("\\") { it.replace('/', '\\') }
            val deletedVideoPaths = mutableListOf<String>()
            var deletionFailure: Exception? = null
            val client = SMBClient(
                SmbConfig.builder()
                    .withTimeout(SMB_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .withSoTimeout(SMB_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .build()
            )
            try {
                val connection = client.connect(target.host)
                try {
                    val session = connection.authenticate(
                        AuthenticationContext(config.username, config.password.toCharArray(), null)
                    )
                    try {
                        val share = session.connectShare(target.share) as DiskShare
                        try {
                            require(share.folderExists(sharePath)) { "Le dossier Freebox n’existe plus." }

                            fun collectVideoPaths(folderSharePath: String, folderRelativePath: String) {
                                share.list(folderSharePath).forEach { entry ->
                                    val childName = entry.fileName.trimEnd('/', '\\')
                                    if (childName.isBlank() || childName == "." || childName == "..") return@forEach
                                    val childSharePath = listOf(folderSharePath, childName)
                                        .filter(String::isNotBlank)
                                        .joinToString("\\")
                                    val childRelativePath = listOf(folderRelativePath, childName)
                                        .filter(String::isNotBlank)
                                        .joinToString("/")
                                    val isDirectory = entry.fileAttributes and
                                        FileAttributes.FILE_ATTRIBUTE_DIRECTORY.value != 0L
                                    if (isDirectory) {
                                        collectVideoPaths(childSharePath, childRelativePath)
                                    } else if (
                                        childName.substringAfterLast('.', "").lowercase() in FREEBOX_VIDEO_EXTENSIONS
                                    ) {
                                        deletedVideoPaths += childRelativePath
                                    }
                                }
                            }

                            collectVideoPaths(sharePath, path)
                            closePlaybackProxiesAndAwait(path)
                            try {
                                share.rmdir(sharePath, true)
                            } catch (error: Exception) {
                                deletionFailure = error
                            }
                        } finally {
                            // Removal is verified below; stale logoff errors must not mask that result.
                            runCatching { share.close() }
                        }
                    } finally {
                        runCatching { session.close() }
                    }
                } finally {
                    runCatching { connection.close() }
                }
            } finally {
                runCatching { client.close() }
            }
            val deletionConfirmed = try {
                !homeVideoExistsOnFreebox(config, target, sharePath)
            } catch (error: Exception) {
                deletionFailure?.takeIf { it !== error }?.let(error::addSuppressed)
                throw IOException("Impossible de vérifier la suppression sur la Freebox.", error)
            }
            if (!deletionConfirmed) {
                throw IOException("La Freebox n’a pas confirmé la suppression du dossier.", deletionFailure)
            }

            val selectedRoots = config.selectedRoots.filterNot { root ->
                isSameOrDescendant(normalizeRelativePath(root), path)
            }.toSet()
            val hiddenHomePaths = config.hiddenHomePaths.filterNot { hidden ->
                isSameOrDescendant(normalizeRelativePath(hidden), path)
            }.toSet()
            val folderArtwork = config.folderArtwork.filterKeys { artworkPath ->
                !isSameOrDescendant(normalizeRelativePath(artworkPath), path)
            }
            val updated = config.copy(
                selectedRoots = selectedRoots,
                hiddenHomePaths = hiddenHomePaths,
                folderArtwork = folderArtwork,
            )
            save(updated)
            mutableState.value = current.copy(
                selectedRoots = selectedRoots,
                hiddenHomePaths = hiddenHomePaths,
                folderArtwork = folderArtwork,
            )
            mutableHomeRows.value = selectedRows(updated)
            deletedVideoPaths
        }
    }

    /** Delete one video below a Freebox root selected on Home; never accepts folders or other files. */
    suspend fun deleteHomeVideo(relativePath: String): String = withContext(Dispatchers.IO) {
        homeRowsRefreshMutex.withLock {
            val path = normalizeRelativePath(relativePath)
            val config = mutableState.value.toStored()
            require(config.selectedRoots.any { root ->
                val normalizedRoot = normalizeRelativePath(root)
                !path.equals(normalizedRoot, ignoreCase = true) && isSameOrDescendant(path, normalizedRoot)
            }) { "La suppression est limitée aux vidéos affichées depuis l’accueil." }
            require(path.substringAfterLast('/').substringAfterLast('.', "").lowercase() in FREEBOX_VIDEO_EXTENSIONS) {
                "Ce fichier n’est pas une vidéo prise en charge."
            }
            require(config.address.isNotBlank()) { "Connectez-vous d’abord à la Freebox." }

            closePlaybackProxiesAndAwait(path)
            val target = SmbAddress.parse(config.address)
            val sharePath = listOf(target.path, path)
                .filter(String::isNotBlank)
                .joinToString("\\") { it.replace('/', '\\') }
            val deletionFailureMessage =
                "La vidéo n'a pas pu être supprimée de la Freebox. Elle est peut-être occupée ; réessayez plus tard."
            var deletionFailure: Exception? = null
            try {
                SMBClient(
                    SmbConfig.builder()
                        .withTimeout(SMB_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .withSoTimeout(SMB_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .build()
                ).use { client ->
                    client.connect(target.host).use { connection ->
                        val session = connection.authenticate(
                            AuthenticationContext(config.username, config.password.toCharArray(), null)
                        )
                        (session.connectShare(target.share) as DiskShare).use { share ->
                            require(share.fileExists(sharePath) && !share.folderExists(sharePath)) {
                                "La vidéo Freebox n’existe plus."
                            }
                            share.rm(sharePath)
                        }
                        runCatching { session.close() }
                    }
                }
            } catch (error: Exception) {
                deletionFailure = error
            }

            var deletionConfirmed = try {
                confirmHomeVideoRemoved(config, target, sharePath)
            } catch (error: Exception) {
                deletionFailure?.takeIf { it !== error }?.let(error::addSuppressed)
                deletionFailure = error
                false
            }
            if (!deletionConfirmed) {
                // A positive existence check means the first delete was not enough; try the
                // alternate SMB client, then require a fresh check that the file is absent.
                try {
                    deleteHomeVideoViaJcifs(config, target, path)
                } catch (error: Exception) {
                    val previousFailure = deletionFailure
                    if (previousFailure == null) deletionFailure = error
                    else previousFailure.addSuppressed(error)
                }
                deletionConfirmed = try {
                    confirmHomeVideoRemoved(config, target, sharePath)
                } catch (error: Exception) {
                    deletionFailure?.takeIf { it !== error }?.let(error::addSuppressed)
                    throw IOException("Impossible de vérifier la suppression sur la Freebox.", error)
                }
            }
            if (!deletionConfirmed) {
                throw IOException(deletionFailureMessage, deletionFailure)
            }

            mutableHomeRows.value = selectedRows(config)
            path
        }
    }

    private fun homeVideoExistsOnFreebox(
        config: StoredFreebox,
        target: SmbAddress,
        sharePath: String,
    ): Boolean {
        val client = SMBClient(
            SmbConfig.builder()
                .withTimeout(SMB_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .withSoTimeout(SMB_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
        )
        try {
            val connection = client.connect(target.host)
            try {
                val session = connection.authenticate(
                    AuthenticationContext(config.username, config.password.toCharArray(), null)
                )
                try {
                    val share = session.connectShare(target.share) as DiskShare
                    try {
                        return share.fileExists(sharePath) || share.folderExists(sharePath)
                    } finally {
                        // Preserve the existence result if teardown reports an already-closed session.
                        runCatching { share.close() }
                    }
                } finally {
                    runCatching { session.close() }
                }
            } finally {
                runCatching { connection.close() }
            }
        } finally {
            runCatching { client.close() }
        }
    }

    private fun confirmHomeVideoRemoved(
        config: StoredFreebox,
        target: SmbAddress,
        sharePath: String,
    ): Boolean {
        for (attempt in 0..2) {
            if (!homeVideoExistsOnFreebox(config, target, sharePath)) return true
            if (attempt < 2) {
                try {
                    Thread.sleep(250L)
                } catch (interrupted: InterruptedException) {
                    Thread.currentThread().interrupt()
                    throw interrupted
                }
            }
        }
        return false
    }

    private fun deleteHomeVideoViaJcifs(
        config: StoredFreebox,
        target: SmbAddress,
        relativePath: String,
    ) {
        ensureJcifsCryptoProvider()
        val smbPath = listOf(target.share) +
            target.path.split('\\').filter(String::isNotBlank) +
            relativePath.split('/').filter(String::isNotBlank)
        val smbUrl = "smb://${target.host}/" + smbPath.joinToString("/") { Uri.encode(it) }
        val cifsContext = createPlaybackCifsContext(config)
        try {
            val file = JcifsSmbFile(smbUrl, cifsContext)
            if (!file.exists()) return
            require(!file.isDirectory) { "La cible Freebox est un dossier, pas une vidéo." }
            file.delete()
        } finally {
            runCatching { cifsContext.close() }
        }
    }

    suspend fun reconnectSaved() {
        val config = mutableState.value.toStored()
        if (config.address.isNotBlank()) connect(config.address, config.username, config.password)
    }

    suspend fun refreshHomeRows() = withContext(Dispatchers.IO) {
        homeRowsRefreshMutex.withLock {
            mutableHomeRows.value = selectedRows(
                config = mutableState.value.toStored(),
                retainCachedRowsOnReadFailure = true,
            )
        }
    }

    suspend fun listMediaEntries(relativePath: String): List<FreeboxEntry> = withContext(Dispatchers.IO) {
        val parts = relativePath.replace('\\', '/').split('/').filter(String::isNotBlank)
        require(parts.none { it == "." || it == ".." }) {
            "Chemin de dossier Freebox invalide."
        }
        list(mutableState.value.toStored(), parts.joinToString("/"))
            .filter { it.isDirectory || it.isVideoFile }
    }

    suspend fun listBrowserEntries(relativePath: String): List<FreeboxEntry> = withContext(Dispatchers.IO) {
        val parts = relativePath.replace('\\', '/').split('/').filter(String::isNotBlank)
        require(parts.none { it == "." || it == ".." }) { "Chemin de dossier Freebox invalide." }
        list(mutableState.value.toStored(), parts.joinToString("/"))
    }

    suspend fun createBrowserDirectory(relativePath: String) = withContext(Dispatchers.IO) {
        val path = normalizeRelativePath(relativePath)
        withBrowserShare { share, target -> share.mkdir(browserSharePath(target, path)) }
    }

    suspend fun renameBrowserEntry(relativePath: String, newName: String, isDirectory: Boolean) =
        withContext(Dispatchers.IO) {
            val path = normalizeRelativePath(relativePath)
            val cleanName = newName.trim()
            require(cleanName.isNotBlank() && cleanName !in setOf(".", "..") && '/' !in cleanName && '\\' !in cleanName) {
                "Le nom de fichier n’est pas valide."
            }
            val parent = path.substringBeforeLast('/', "")
            val targetPath = listOf(parent, cleanName).filter(String::isNotBlank).joinToString("/")
            withBrowserShare { share, target ->
                val oldSmbPath = browserSharePath(target, path)
                val newSmbPath = browserSharePath(target, targetPath)
                if (isDirectory) {
                    share.openDirectory(
                        oldSmbPath,
                        setOf(AccessMask.DELETE, AccessMask.GENERIC_WRITE),
                        setOf(FileAttributes.FILE_ATTRIBUTE_DIRECTORY),
                        SMB2ShareAccess.ALL,
                        SMB2CreateDisposition.FILE_OPEN,
                        setOf(SMB2CreateOptions.FILE_DIRECTORY_FILE),
                    ).use { it.rename(newSmbPath) }
                } else {
                    share.openFile(
                        oldSmbPath,
                        setOf(AccessMask.DELETE, AccessMask.GENERIC_WRITE),
                        setOf(FileAttributes.FILE_ATTRIBUTE_NORMAL),
                        SMB2ShareAccess.ALL,
                        SMB2CreateDisposition.FILE_OPEN,
                        setOf(SMB2CreateOptions.FILE_NON_DIRECTORY_FILE),
                    ).use { it.rename(newSmbPath) }
                }
            }
        }

    suspend fun deleteBrowserEntry(relativePath: String, isDirectory: Boolean) =
        withContext(Dispatchers.IO) {
            val path = normalizeRelativePath(relativePath)
            withBrowserShare { share, target ->
                val smbPath = browserSharePath(target, path)
                if (isDirectory) share.rmdir(smbPath, true) else share.rm(smbPath)
            }
        }

    suspend fun uploadBrowserFile(relativePath: String, source: File) = withContext(Dispatchers.IO) {
        require(source.isFile) { "Le fichier local à envoyer est introuvable." }
        val path = normalizeRelativePath(relativePath)
        withBrowserShare { share, target ->
            share.openFile(
                browserSharePath(target, path),
                setOf(AccessMask.FILE_WRITE_DATA, AccessMask.FILE_WRITE_ATTRIBUTES),
                setOf(FileAttributes.FILE_ATTRIBUTE_NORMAL),
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_CREATE,
                setOf(SMB2CreateOptions.FILE_NON_DIRECTORY_FILE),
            ).use { destination ->
                source.inputStream().buffered().use { input ->
                    val buffer = ByteArray(256 * 1024)
                    var offset = 0L
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        destination.write(buffer, offset, 0, count)
                        offset += count
                    }
                }
            }
        }
    }

    suspend fun storageCapacity(): StorageCapacity? {
        val config = mutableState.value.toStored()
        if (config.address.isBlank()) return null
        ensureJcifsCryptoProvider()
        return SmbStorageCapacity.read(config.address, config.username, config.password)
    }

    fun folderArtwork(
        relativePath: String,
        preferPathScopedVideoArtwork: Boolean = false,
    ): FreeboxFolderArtwork? = runCatching {
        val path = normalizeRelativePath(relativePath)
        val current = mutableState.value
        val pathArtwork = current.folderArtwork[path]
        val nameArtwork = findVideoArtworkByName(current.videoArtworkByName, videoArtworkNameKey(path))
        if (preferPathScopedVideoArtwork) pathArtwork ?: nameArtwork else nameArtwork ?: pathArtwork
    }.getOrNull()

    /** Reads the embedded title, year, genres, and duration from this exact SMB file. */
    suspend fun videoMetadata(relativePath: String, sourceFileSizeBytes: Long? = null): FreeboxVideoMetadata? = withContext(Dispatchers.IO) {
        val path = normalizeRelativePath(relativePath)
        val config = mutableState.value.toStored()
        val cacheKey = "${config.address}:$path:${sourceFileSizeBytes ?: "unknown"}"
        videoMetadataCache[cacheKey]?.let { return@withContext it }
        val metadata = runCatching { readVideoMetadata(path) }.getOrNull()
            ?: return@withContext null
        videoMetadataCache[cacheKey] = metadata
        if (videoMetadataCache.size > 128) {
            videoMetadataCache.keys.firstOrNull { it != cacheKey }?.let(videoMetadataCache::remove)
        }
        metadata
    }

    fun rememberHomeVideoPresentation(item: MetaPreview) {
        val isFolderPresentation = item.id.startsWith(FREEBOX_FOLDER_ID_PREFIX, ignoreCase = true)
        if (!item.id.startsWith("freebox:", ignoreCase = true) && !isFolderPresentation) return
        homeVideoPresentations[item.id] = item
        homeVideoPresentationFlows[item.id]?.value = item
        homeVideoPresentationFlows.forEach { (requestedId, flow) ->
            if (item.matchesHomeVideoContentId(requestedId)) flow.value = item
        }
        if (!isFolderPresentation) {
            freeboxPathFromContentId(item.id)?.let { path ->
                homeVideoPresentationsByPath[path] = item
                homeVideoPresentationPathFlows[path]?.value = item
                if (homeVideoPresentationsByPath.size > 96) {
                    homeVideoPresentationsByPath.keys.firstOrNull { it != path }
                        ?.let(homeVideoPresentationsByPath::remove)
                }
            }
        }
        if (homeVideoPresentations.size > 96) {
            homeVideoPresentations.keys.firstOrNull { it != item.id }?.let(homeVideoPresentations::remove)
        }
    }

    fun homeVideoPresentation(contentId: String): MetaPreview? =
        homeVideoPresentations[contentId]
            ?: homeVideoPresentations.values.firstOrNull { it.matchesHomeVideoContentId(contentId) }

    fun observeHomeVideoPresentation(contentId: String): StateFlow<MetaPreview?> =
        homeVideoPresentationFlows.computeIfAbsent(contentId) {
            MutableStateFlow(homeVideoPresentation(contentId))
        }.asStateFlow()

    fun observeHomeVideoPresentationForPath(relativePath: String): StateFlow<MetaPreview?> {
        val path = normalizeRelativePath(relativePath)
        return homeVideoPresentationPathFlows.computeIfAbsent(path) {
            MutableStateFlow(homeVideoPresentationsByPath[path])
        }.asStateFlow()
    }

    private fun MetaPreview.matchesHomeVideoContentId(contentId: String): Boolean {
        if (id == contentId) return true
        if (id.startsWith(FREEBOX_FOLDER_ID_PREFIX, ignoreCase = true)) return false
        val itemPath = freeboxPathFromContentId(id) ?: return false
        val requestedPath = freeboxPathFromContentId(contentId) ?: return false
        if (itemPath != requestedPath) return false
        return contentId == freeboxContentIdFor(
            path = itemPath,
            sizeBytes = sourceFileSizeBytes,
            lastModifiedMs = sourceFileLastModifiedMs,
            fileId = sourceFileId,
        )
    }

    /** Copies a Freebox browser file to private storage for Android document/image handlers. */
    suspend fun copyBrowserFileToCache(
        relativePath: String,
        destination: File,
        onProgress: (copiedBytes: Long, totalBytes: Long) -> Unit = { _, _ -> },
    ) = withContext(Dispatchers.IO) {
        // Use the same jCIFS random-access path as Freebox playback. Some Freebox SMB shares
        // invalidate SMBJ file handles between reads, which breaks document copies mid-file.
        try {
            openPlaybackSession(relativePath).use { session ->
                copyBrowserPlaybackFileToCache(session.openFile(), destination, onProgress)
            }
        } catch (cancelled: kotlinx.coroutines.CancellationException) {
            throw cancelled
        } catch (jcifsError: Exception) {
            // The browser listing uses SMBJ paths, and some names fail to resolve through the
            // equivalent jCIFS URL. Retry the copy through the same SMBJ path as image files.
            destination.delete()
            try {
                openPlaybackFile(relativePath).use { source ->
                    copyBrowserPlaybackFileToCache(source, destination, onProgress)
                }
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                throw cancelled
            } catch (smbjError: Exception) {
                jcifsError.addSuppressed(smbjError)
                throw jcifsError
            }
        }
    }

    private fun copyBrowserPlaybackFileToCache(
        source: FreeboxPlaybackFile,
        destination: File,
        onProgress: (copiedBytes: Long, totalBytes: Long) -> Unit,
    ) = source.use { playbackFile ->
        FileOutputStream(destination).buffered().use { output ->
            val buffer = ByteArray(128 * 1024)
            val totalBytes = playbackFile.length
            var copiedBytes = 0L
            onProgress(0L, totalBytes)
            while (copiedBytes < totalBytes) {
                val requested = minOf(buffer.size.toLong(), totalBytes - copiedBytes).toInt()
                val count = playbackFile.read(buffer, 0, requested)
                if (count <= 0) throw IOException("Le fichier Freebox a été interrompu pendant sa copie.")
                output.write(buffer, 0, count)
                copiedBytes += count
                onProgress(copiedBytes, totalBytes)
            }
        }
    }

    /** Copies a browser image with the SMBJ path handling used to list the Freebox directory. */
    suspend fun copyBrowserImageToCache(
        relativePath: String,
        destination: File,
        onProgress: (copiedBytes: Long, totalBytes: Long) -> Unit = { _, _ -> },
    ) = withContext(Dispatchers.IO) {
        val source = openPlaybackFile(relativePath)
        try {
            FileOutputStream(destination).buffered().use { output ->
                val buffer = ByteArray(128 * 1024)
                val totalBytes = source.length
                var copiedBytes = 0L
                onProgress(0L, totalBytes)
                while (copiedBytes < totalBytes) {
                    val requested = minOf(buffer.size.toLong(), totalBytes - copiedBytes).toInt()
                    val count = source.read(buffer, 0, requested)
                    if (count <= 0) throw IOException("La photo Freebox a été interrompue pendant sa copie.")
                    output.write(buffer, 0, count)
                    copiedBytes += count
                    onProgress(copiedBytes, totalBytes)
                }
            }
        } finally {
            source.close()
        }
    }

    /** Opens a seekable SMBJ file using the same SMB client and paths as the browser. */
    fun openPlaybackFile(relativePath: String): FreeboxPlaybackFile {
        val config = mutableState.value.toStored()
        require(config.address.isNotBlank()) { "Connectez-vous d’abord à la Freebox." }
        val safeParts = relativePath.replace('\\', '/').split('/').filter(String::isNotBlank)
        require(safeParts.isNotEmpty() && safeParts.none { it == "." || it == ".." }) {
            "Chemin de fichier Freebox invalide."
        }
        val target = SmbAddress.parse(config.address)
        val smbClient = SMBClient(
            SmbConfig.builder()
                .withTimeout(PLAYBACK_SMB_TIMEOUT_MS.toLong(), TimeUnit.MILLISECONDS)
                .withSoTimeout(PLAYBACK_SMB_TIMEOUT_MS.toLong(), TimeUnit.MILLISECONDS)
                .build()
        )
        var connection: Connection? = null
        var session: Session? = null
        var share: DiskShare? = null
        var file: SmbjFile? = null
        try {
            val openedConnection = smbClient.connect(target.host)
            connection = openedConnection
            val openedSession = openedConnection.authenticate(
                AuthenticationContext(config.username, config.password.toCharArray(), null)
            )
            session = openedSession
            val openedShare = openedSession.connectShare(target.share) as DiskShare
            share = openedShare
            val sharePath = listOf(target.path, safeParts.joinToString("\\"))
                .filter(String::isNotBlank)
                .joinToString("\\")
            val openedFile = openedShare.openFile(
                sharePath,
                setOf(AccessMask.FILE_READ_DATA, AccessMask.FILE_READ_ATTRIBUTES),
                setOf(FileAttributes.FILE_ATTRIBUTE_NORMAL),
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                setOf(SMB2CreateOptions.FILE_NON_DIRECTORY_FILE, SMB2CreateOptions.FILE_RANDOM_ACCESS),
            )
            file = openedFile
            val length = openedFile.length
            return FreeboxPlaybackFile(
                readAt = { position, buffer, offset, requestedLength ->
                    openedFile.read(buffer, position, offset, requestedLength)
                },
                resources = listOf(openedFile, openedShare, openedSession, openedConnection, smbClient),
                length = length,
            )
        } catch (failure: Exception) {
            runCatching { file?.close() }
            runCatching { share?.close() }
            runCatching { session?.close() }
            runCatching { connection?.close() }
            runCatching { smbClient.close() }
            throw failure
        }
    }

    /** Opens a persistent authenticated CIFS context for reads from a Freebox file. */
    fun openPlaybackSession(relativePath: String): FreeboxPlaybackSession {
        val config = mutableState.value.toStored()
        require(config.address.isNotBlank()) { "Connectez-vous d’abord à la Freebox." }
        val safeParts = relativePath.replace('\\', '/').split('/').filter(String::isNotBlank)
        require(safeParts.isNotEmpty() && safeParts.none { it == "." || it == ".." }) {
            "Chemin de fichier Freebox invalide."
        }
        ensureJcifsCryptoProvider()
        val cifsContext = createPlaybackCifsContext(config)
        var stage = "parse_smb_address"
        try {
            val target = SmbAddress.parse(config.address)
            val smbPath = listOf(target.share) +
                target.path.split('\\').filter(String::isNotBlank) + safeParts
            val smbUrl = "smb://${target.host}/" + smbPath.joinToString("/") { Uri.encode(it) }
            stage = "read_file_length"
            val remoteFile = JcifsSmbFile(smbUrl, cifsContext)
            val length = remoteFile.length()
            return FreeboxPlaybackSession(remoteFile, cifsContext, length)
        } catch (failure: Exception) {
            val smbFailure = generateSequence(failure as Throwable?) { it.cause }
                .filterIsInstance<SmbException>()
                .firstOrNull()
            val status = smbFailure?.let { "0x${Integer.toHexString(it.ntStatus)}" } ?: "unknown"
            Log.w(
                "FreeboxPlaybackProxy",
                "FREEBOX_JCIFS_SESSION_OPEN_FAILED stage=$stage status=$status cause=${failure.javaClass.simpleName}"
            )
            runCatching { cifsContext.close() }
            throw failure
        }
    }

    /** Reads a file duration, or the average duration of direct video children in a folder. */
    suspend fun durationMinutes(relativePath: String, isFolder: Boolean): Long? = withContext(Dispatchers.IO) {
        val normalizedPath = normalizeRelativePath(relativePath)
        val config = mutableState.value.toStored()
        val cacheKey = "${config.address}:${if (isFolder) "folder" else "file"}:$normalizedPath"
        videoDurationMinutesCache[cacheKey]?.let { return@withContext it }

        val durationsMs = if (isFolder) {
            runCatching { list(config, normalizedPath) }
                .getOrDefault(emptyList())
                .asSequence()
                .filter(FreeboxEntry::isVideoFile)
                .mapNotNull { entry -> runCatching { readVideoDurationMillis(entry.path) }.getOrNull() }
                .toList()
        } else {
            listOfNotNull(runCatching { readVideoDurationMillis(normalizedPath) }.getOrNull())
        }
        val averageDurationMs = durationsMs.takeIf { it.isNotEmpty() }?.average()?.roundToLong()
            ?: return@withContext null
        val minutes = ((averageDurationMs + 30_000L) / 60_000L).takeIf { it > 0L }
            ?: return@withContext null
        videoDurationMinutesCache[cacheKey] = minutes
        if (videoDurationMinutesCache.size > 128) {
            videoDurationMinutesCache.keys.firstOrNull { it != cacheKey }?.let(videoDurationMinutesCache::remove)
        }
        minutes
    }

    private fun readVideoDurationMillis(relativePath: String): Long? {
        val playbackFile = openPlaybackFile(relativePath)
        val dataSource = object : MediaDataSource() {
            override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int =
                synchronized(playbackFile) {
                    if (position < 0L || position >= playbackFile.length) return@synchronized -1
                    if (size <= 0) return@synchronized 0
                    val readableSize = minOf(size.toLong(), playbackFile.length - position).toInt()
                    playbackFile.seek(position)
                    playbackFile.read(buffer, offset, readableSize)
                }

            override fun getSize(): Long = playbackFile.length

            override fun close() = playbackFile.close()
        }
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(dataSource)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()
                ?.takeIf { it > 0L }
        } catch (_: Exception) {
            null
        } finally {
            runCatching { retriever.release() }
            runCatching { dataSource.close() }
        }
    }

    private fun readVideoMetadata(relativePath: String): FreeboxVideoMetadata {
        val playbackFile = openPlaybackFile(relativePath)
        val dataSource = object : MediaDataSource() {
            override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int =
                synchronized(playbackFile) {
                    if (position < 0L || position >= playbackFile.length) return@synchronized -1
                    if (size <= 0) return@synchronized 0
                    val readableSize = minOf(size.toLong(), playbackFile.length - position).toInt()
                    playbackFile.seek(position)
                    playbackFile.read(buffer, offset, readableSize)
                }

            override fun getSize(): Long = playbackFile.length
            override fun close() = playbackFile.close()
        }
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(dataSource)
            parseFreeboxVideoMetadata(
                title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                date = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE),
                genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE),
                durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
            )
        } finally {
            runCatching { retriever.release() }
            runCatching { dataSource.close() }
        }
    }

    private fun createPlaybackCifsContext(config: StoredFreebox): CIFSContext {
        val timeoutMs = PLAYBACK_SMB_TIMEOUT_MS
        val properties = Properties().apply {
            setProperty("jcifs.smb.client.minVersion", "SMB202")
            setProperty("jcifs.smb.client.maxVersion", "SMB311")
            setProperty("jcifs.smb.client.responseTimeout", timeoutMs.toString())
            setProperty("jcifs.smb.client.soTimeout", timeoutMs.toString())
            setProperty("jcifs.smb.client.connTimeout", timeoutMs.toString())
            setProperty("jcifs.smb.client.rcv_buf_size", "262144")
            setProperty("jcifs.smb.client.snd_buf_size", "262144")
            setProperty("jcifs.smb.client.tcpNoDelay", "true")
        }
        val base = BaseContext(PropertyConfiguration(properties))
        if (config.username.isBlank()) return base

        val separator = config.username.indexOf('\\')
        val credentials = if (separator > 0) {
            ExplicitDomainAuthenticator(
                config.username.substring(0, separator),
                config.username.substring(separator + 1),
                config.password
            )
        } else {
            NtlmPasswordAuthenticator("", config.username, config.password)
        }
        return base.withCredentials(credentials)
    }

    private fun ensureJcifsCryptoProvider() {
        val provider = jcifs.util.Crypto.getProvider()
        MessageDigest.getInstance("MD4", provider)
    }

    private fun list(config: StoredFreebox, relativePath: String): List<FreeboxEntry> {
        val target = SmbAddress.parse(config.address)
        val client = SMBClient(
            SmbConfig.builder()
                .withTimeout(SMB_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .withSoTimeout(SMB_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
        )
        client.use { smbClient ->
            smbClient.connect(target.host).use { connection ->
                val session = connection.authenticate(
                    AuthenticationContext(config.username, config.password.toCharArray(), null)
                )
                (session.connectShare(target.share) as DiskShare).use { share ->
                    val sharePath = listOf(target.path, relativePath).filter { it.isNotBlank() }.joinToString("\\")
                    return share.list(sharePath).asSequence()
                        .filter { it.fileName != "." && it.fileName != ".." }
                        .map { entry ->
                            val isDirectory = entry.fileAttributes and FileAttributes.FILE_ATTRIBUTE_DIRECTORY.value != 0L
                            val childPath = listOf(relativePath, entry.fileName).filter { it.isNotBlank() }.joinToString("/")
                            FreeboxEntry(
                                name = entry.fileName,
                                path = childPath,
                                isDirectory = isDirectory,
                                sizeBytes = entry.endOfFile,
                                lastModifiedMs = entry.lastWriteTime?.toEpochMillis() ?: 0L,
                                fileId = entry.fileId,
                            )
                        }
                        .sortedWith(compareByDescending<FreeboxEntry> { it.isDirectory }.thenBy { it.name.lowercase() })
                        .toList()
                }
            }
        }
    }

    private inline fun <T> withBrowserShare(block: (DiskShare, SmbAddress) -> T): T {
        val config = mutableState.value.toStored()
        require(config.address.isNotBlank()) { "Connectez-vous d’abord à la Freebox." }
        val target = SmbAddress.parse(config.address)
        val client = SMBClient(
            SmbConfig.builder()
                .withTimeout(SMB_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .withSoTimeout(SMB_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
        )
        try {
            val connection = client.connect(target.host)
            try {
                val session = connection.authenticate(
                    AuthenticationContext(config.username, config.password.toCharArray(), null),
                )
                try {
                    val share = session.connectShare(target.share) as DiskShare
                    try {
                        return block(share, target)
                    } finally {
                        // SMB teardown can report a stale logoff even after the transfer completed.
                        runCatching { share.close() }
                    }
                } finally {
                    runCatching { session.close() }
                }
            } finally {
                runCatching { connection.close() }
            }
        } finally {
            runCatching { client.close() }
        }
    }

    private fun browserSharePath(target: SmbAddress, relativePath: String): String =
        listOf(target.path, relativePath.replace('/', '\\')).filter(String::isNotBlank).joinToString("\\")

    private fun selectedRows(
        config: StoredFreebox,
        retainCachedRowsOnReadFailure: Boolean = false,
    ): List<CatalogRow> {
        val cachedRowsByRoot: Map<String, CatalogRow> = if (retainCachedRowsOnReadFailure) {
            mutableHomeRows.value.associateBy(CatalogRow::catalogId)
        } else {
            emptyMap()
        }
        return config.selectedRoots.sorted().mapNotNull { root ->
            val listedEntries = try {
                list(config, root)
            } catch (_: Exception) {
                if (retainCachedRowsOnReadFailure) return@mapNotNull cachedRowsByRoot[root]
                emptyList()
            }
            val entries = listedEntries
                .filter { it.isDirectory || it.isVideoFile }
                .filterNot { entry -> config.hiddenHomePaths.any { hidden -> isSameOrDescendant(entry.path, hidden) } }
                .take(HOME_ENTRY_LIMIT)
            if (entries.isEmpty()) return@mapNotNull null
            val series = root.contains("serie", ignoreCase = true) || root.contains("series", ignoreCase = true) ||
                root.contains("série", ignoreCase = true) || root.contains("tv", ignoreCase = true)
            val type = if (series) ContentType.SERIES else ContentType.MOVIE
            CatalogRow(
                addonId = "freebox:$root",
                addonName = "Freebox",
                addonBaseUrl = "freebox://nas",
                catalogId = root,
                catalogName = root.substringAfterLast('/'),
                type = type,
                rawType = type.toApiString(),
                items = entries.map { entry ->
                    val isFolder = entry.isDirectory
                    val artwork = findVideoArtworkByName(config.videoArtworkByName, videoArtworkNameKey(entry.path))
                        ?: config.folderArtwork[normalizeRelativePath(entry.path)]
                    MetaPreview(
                        id = if (isFolder) "$FREEBOX_FOLDER_ID_PREFIX${entry.path}" else "freebox:${entry.path}",
                        type = type,
                        name = if (isFolder) entry.name else entry.name.substringBeforeLast('.', entry.name),
                        poster = artwork?.portrait ?: FREEBOX_FOLDER_COVER_URI.takeIf { isFolder },
                        posterShape = PosterShape.POSTER, background = artwork?.landscape, logo = null,
                        description = null,
                        releaseInfo = null,
                        imdbRating = null, genres = emptyList(), sourceAddonBaseUrl = "freebox://nas",
                        sourceFileSizeBytes = entry.sizeBytes.takeIf { it > 0L },
                        sourceFileLastModifiedMs = entry.lastModifiedMs.takeIf { it > 0L },
                        sourceFileId = entry.fileId.takeIf { it != 0L }
                    )
                },
                hasMore = false
            )
        }
    }

    private fun loadState(): FreeboxUiState {
        val stored = runCatching {
            preferences.read(1)?.let { json.decodeFromString<StoredFreebox>(it) }
        }.getOrNull() ?: StoredFreebox()
        val videoArtworkByName = stored.folderArtwork.entries.mapNotNull { (path, artwork) ->
            if (artwork.portrait == null && artwork.landscape == null) null
            else videoArtworkNameKey(path)?.let { it to artwork }
        }.toMap() + stored.videoArtworkByName
        if (videoArtworkByName != stored.videoArtworkByName) {
            runCatching { save(stored.copy(videoArtworkByName = videoArtworkByName)) }
        }
        return FreeboxUiState(
            address = stored.address,
            username = stored.username,
            password = stored.password,
            selectedRoots = stored.selectedRoots,
            hiddenHomePaths = stored.hiddenHomePaths,
            folderArtwork = stored.folderArtwork,
            videoArtworkByName = videoArtworkByName,
        )
    }

    private fun save(config: StoredFreebox) {
        preferences.write(1, json.encodeToString(StoredFreebox.serializer(), config))
    }

    private fun FreeboxUiState.toStored() = StoredFreebox(
        address = address,
        username = username,
        password = password,
        selectedRoots = selectedRoots,
        hiddenHomePaths = hiddenHomePaths,
        folderArtwork = folderArtwork,
        videoArtworkByName = videoArtworkByName,
    )

    private fun normalizeRelativePath(rawPath: String): String {
        val parts = rawPath.replace('\\', '/').split('/').filter(String::isNotBlank)
        require(parts.isNotEmpty() && parts.none { it == "." || it == ".." }) {
            "Chemin de dossier Freebox invalide."
        }
        return parts.joinToString("/")
    }

    private fun videoArtworkNameKey(rawPath: String): String? {
        val pathParts = rawPath.replace('\\', '/').split('/').filter(String::isNotBlank)
        val filename = pathParts.lastOrNull() ?: return null
        val extension = filename.substringAfterLast('.', "").lowercase(java.util.Locale.ROOT)
        if (extension !in FREEBOX_VIDEO_EXTENSIONS) return null
        val title = normalizeFreeboxArtworkTitle(filename.substringBeforeLast('.'))
        if (title != null && !FREEBOX_ARTWORK_EPISODE_ONLY_TITLE.matches(title)) return title

        for (folder in pathParts.dropLast(1).asReversed()) {
            val folderTitle = normalizeFreeboxArtworkTitle(folder) ?: continue
            if (folderTitle in FREEBOX_ARTWORK_GENERIC_FOLDER_KEYS ||
                FREEBOX_ARTWORK_EPISODE_ONLY_TITLE.matches(folderTitle) ||
                FREEBOX_ARTWORK_SEASON_FOLDER.matches(folder.trim()) ||
                FREEBOX_ARTWORK_DATE_ONLY.matches(folder.trim())
            ) continue
            return folderTitle
        }
        return null
    }

    private fun normalizeFreeboxArtworkTitle(rawTitle: String): String? {
        var title = rawTitle.trim().replace(Regex("\\s+"), " ")
        if (title.isBlank()) return null
        title = FREEBOX_ARTWORK_DATE_PREFIX.replaceFirst(title, "").trim()

        while (true) {
            val before = title
            val suffix = FREEBOX_ARTWORK_RECURRING_SUFFIX.find(title)
            if (suffix != null) title = title.substring(0, suffix.range.first)
            title = title.trim().trimEnd(' ', '-', '_', '.')
            if (title == before) break
        }

        return title
            .replace(Regex("[\\s._-]+"), " ")
            .trim()
            .lowercase(java.util.Locale.ROOT)
            .takeIf(String::isNotBlank)
    }

    private fun findVideoArtworkByName(
        artworkByName: Map<String, FreeboxFolderArtwork>,
        key: String?,
    ): FreeboxFolderArtwork? {
        if (key == null) return null
        val matches = artworkByName.entries.filter { normalizeFreeboxArtworkTitle(it.key) == key }
        if (matches.isEmpty()) return null
        val exact = artworkByName[key]
        val fallback = matches.asReversed()
        return FreeboxFolderArtwork(
            portrait = exact?.portrait ?: fallback.firstNotNullOfOrNull { it.value.portrait },
            landscape = exact?.landscape ?: fallback.firstNotNullOfOrNull { it.value.landscape },
            tmdbId = exact?.tmdbId ?: fallback.firstNotNullOfOrNull { it.value.tmdbId },
            tmdbContentType = exact?.tmdbContentType
                ?: fallback.firstNotNullOfOrNull { it.value.tmdbContentType },
        )
    }

    private fun isSameOrDescendant(path: String, parent: String): Boolean =
        path.equals(parent, ignoreCase = true) || path.startsWith("$parent/", ignoreCase = true)

    private data class SmbAddress(val host: String, val share: String, val path: String) {
        companion object {
            fun parse(value: String): SmbAddress {
                val normalized = value.trim().replace('\\', '/')
                    .replaceFirst(Regex("^smb://", RegexOption.IGNORE_CASE), "")
                    .trim('/')
                val parts = normalized.split('/').filter { it.isNotBlank() }
                require(parts.isNotEmpty()) { "Saisissez une adresse SMB, par exemple smb://192.168.1.254" }
                return SmbAddress(parts[0], parts.getOrNull(1) ?: DEFAULT_SMB_SHARE, parts.drop(2).joinToString("\\"))
            }
        }
    }

    private companion object {
        const val DEFAULT_SMB_SHARE = "Freebox"
        const val SMB_TIMEOUT_SECONDS = 20L
        const val PLAYBACK_SMB_TIMEOUT_MS = 60_000
        const val HOME_ENTRY_LIMIT = 300
    }
}

private class ExplicitDomainAuthenticator(domain: String, user: String, password: String) :
    NtlmPasswordAuthenticator(null, domain, user, password)
