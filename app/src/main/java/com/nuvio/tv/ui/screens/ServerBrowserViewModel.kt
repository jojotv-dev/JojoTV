package com.nuvio.tv.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.core.tmdb.TmdbArtworkCandidate
import com.nuvio.tv.core.tmdb.TmdbService
import com.nuvio.tv.data.freebox.FreeboxRepository
import com.nuvio.tv.data.freebox.clearReplacedFreeboxProgress
import com.nuvio.tv.data.freebox.freeboxContentIdFor
import com.nuvio.tv.data.local.ServerBrowserShortcut
import com.nuvio.tv.data.local.ServerBrowserShortcutStore
import com.nuvio.tv.data.local.TmdbSettingsDataStore
import com.nuvio.tv.core.shizuku.SHIZUKU_STORAGE_PATH_PREFIX
import com.nuvio.tv.core.shizuku.ShizukuStorageBrowser
import com.nuvio.tv.data.mediaserver.ServerBrowseEntry
import com.nuvio.tv.data.mediaserver.ServerRepository
import com.nuvio.tv.data.mediaserver.ServerItemRef
import com.nuvio.tv.domain.repository.WatchProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import java.io.File
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.security.MessageDigest
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.util.concurrent.ConcurrentHashMap
import org.json.JSONArray
import org.json.JSONObject

data class ServerBrowserEntry(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val sizeBytes: Long = 0L,
    val mimeType: String? = null,
    val isPlayable: Boolean = false,
    val uri: String? = null,
    val lastModifiedMs: Long = 0L,
    val fileId: Long = 0L,
) {
    val fileType: ServerBrowserFileType
        get() = ServerBrowserFileType.from(name, mimeType, isDirectory, isPlayable)
}

data class ServerBrowserBatchActionResult(
    val requestedCount: Int,
    val completedCount: Int,
    val failures: List<String>,
)

enum class ServerBrowserFileType(val label: String) {
    DIRECTORY("Dossier"), VIDEO("Vidéo"), IMAGE("Image"), PDF("PDF"), TEXT("Texte"),
    DOCUMENT("Document"), APK("APK Android"), EXECUTABLE("Exécutable"), AUDIO("Audio"), ARCHIVE("Archive"),
    PLAYLIST("Playlist"), OTHER("Fichier");

    companion object {
        fun from(name: String, mimeType: String?, isDirectory: Boolean, isPlayable: Boolean): ServerBrowserFileType {
            if (isDirectory) return DIRECTORY
            val extension = name.substringAfterLast('.', "").lowercase()
            return when {
                extension == "apk" || mimeType == "application/vnd.android.package-archive" -> APK
                extension == "exe" -> EXECUTABLE
                isPlayable || mimeType?.startsWith("video/") == true -> VIDEO
                extension in IMAGE_EXTENSIONS || mimeType?.startsWith("image/") == true -> IMAGE
                extension == "pdf" || mimeType == "application/pdf" -> PDF
                extension in TEXT_EXTENSIONS || mimeType?.startsWith("text/") == true -> TEXT
                extension in DOCUMENT_EXTENSIONS || mimeType?.startsWith("application/vnd.openxmlformats-officedocument") == true -> DOCUMENT
                extension in AUDIO_EXTENSIONS || mimeType?.startsWith("audio/") == true -> AUDIO
                extension in ARCHIVE_EXTENSIONS -> ARCHIVE
                extension in PLAYLIST_EXTENSIONS -> PLAYLIST
                else -> OTHER
            }
        }

        private val IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif", "webp", "bmp", "heic", "heif", "tif", "tiff", "svg")
        private val TEXT_EXTENSIONS = setOf("txt", "tct", "text", "md", "log", "csv", "tsv", "ini", "conf", "cfg", "json", "xml", "html", "htm", "yaml", "yml")
        private val DOCUMENT_EXTENSIONS = setOf("rtf", "doc", "docx", "odt", "xls", "xlsx", "ods", "ppt", "pptx", "odp")
        private val AUDIO_EXTENSIONS = setOf("mp3", "aac", "flac", "wav", "m4a", "ogg", "opus", "wma")
        private val ARCHIVE_EXTENSIONS = setOf("zip", "rar", "7z", "tar", "gz", "tgz", "bz2", "xz")
        private val PLAYLIST_EXTENSIONS = setOf("m3u", "m3u8", "pls")
    }
}

data class ServerBrowserState(
    val sourceName: String = "Serveurs",
    val location: String = "",
    val entries: List<ServerBrowserEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val localTreeUri: String? = null,
    val localStorageAccessGranted: Boolean = false,
    val showHiddenFiles: Boolean = false,
    val isGridView: Boolean = true,
    val hasClipboard: Boolean = false,
    val clipboardIsCut: Boolean = false,
    val artworkRevision: Int = 0,
    val folderCountRevision: Int = 0,
)

data class ServerBrowserApkDownloadProgress(
    val fileName: String,
    val percent: Int,
)

data class ServerBrowserFolderCountKey(
    val sourceId: String,
    val path: String,
    val showHiddenFiles: Boolean,
)

sealed interface ServerBrowserFolderCounts {
    data object Loading : ServerBrowserFolderCounts
    data class Ready(val folders: Int, val files: Int) : ServerBrowserFolderCounts
    data object Unavailable : ServerBrowserFolderCounts
}

private val SERVER_BROWSER_SEASON_FOLDER = Regex(
    """^(?:season|saison|s)[\s._-]*0*\d+(?:$|[\s._-].*)$""",
    RegexOption.IGNORE_CASE,
)
private val SERVER_BROWSER_NUMBERED_EPISODE = Regex(
    """^(?:(?:episode|épisode|ep)[\s._-]*)?\d{1,4}$""",
    RegexOption.IGNORE_CASE,
)
private val SERVER_BROWSER_EPISODE_MARKER = Regex(
    """(?:^|[ ._-])(?:s\d{1,2}[ ._-]*e\d{1,3}|\d{1,2}x\d{1,3})(?:$|[ ._-])""",
    RegexOption.IGNORE_CASE,
)
private val SERVER_BROWSER_EPISODE_SUFFIX = Regex(
    """[ ._-]*(?:s\d{1,2}[ ._-]*e\d{1,3}|\d{1,2}x\d{1,3}).*$""",
    RegexOption.IGNORE_CASE,
)
private val SERVER_BROWSER_YEAR = Regex("\\b(?:19|20)\\d{2}\\b")
private val SERVER_BROWSER_RELEASE_SUFFIX = Regex(
    """\s+\b(?:2160p|1080p|720p|480p|4k|8k|web[ .-]?dl|webrip|blu[ .-]?ray|bluray|hdtv|x264|x265|h264|h265|hevc|hdr|dovi|aac|dts)\b.*$""",
    RegexOption.IGNORE_CASE,
)
private val SERVER_BROWSER_YEAR_SUFFIX = Regex("""(?:\s*\(\s*|\s+)(?:19|20)\d{2}(?:\s*\))?\s*$""")

@HiltViewModel
class ServerBrowserViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val serverRepository: ServerRepository,
    private val serverPlayback: com.nuvio.tv.data.mediaserver.ServerPlayback,
    private val freeboxRepository: FreeboxRepository,
    private val tmdbService: TmdbService,
    private val tmdbSettingsDataStore: TmdbSettingsDataStore,
    private val watchProgressRepository: WatchProgressRepository,
    private val shortcutStore: ServerBrowserShortcutStore,
) : ViewModel() {
    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    private val mutableState = MutableStateFlow(
        ServerBrowserState(
            localTreeUri = preferences.getString(LOCAL_TREE_URI, null),
            localStorageAccessGranted = hasLocalStorageAccess(),
            showHiddenFiles = preferences.getBoolean(SHOW_HIDDEN_FILES, false),
            hasClipboard = readClipboard().isNotEmpty(),
            clipboardIsCut = readClipboardIsCut(),
        )
    )
    val state: StateFlow<ServerBrowserState> = mutableState.asStateFlow()
    private val mutableApkDownloadProgress = MutableStateFlow<ServerBrowserApkDownloadProgress?>(null)
    val apkDownloadProgress: StateFlow<ServerBrowserApkDownloadProgress?> = mutableApkDownloadProgress.asStateFlow()
    val servers = serverRepository.uiState
    val freebox = freeboxRepository.state
    private var loadJob: Job? = null
    private val thumbnailSemaphore = Semaphore(3)
    private val heicThumbnailSemaphore = Semaphore(1)
    private val thumbnailCacheTrimLock = Any()
    private val lastThumbnailCacheTrimAtMs = mutableMapOf<String, Long>()
    private val videoDurationSemaphore = Semaphore(1)
    private val freeboxFolderArtworkSemaphore = Semaphore(2)
    private val videoDurationCache = ConcurrentHashMap<String, Long>()
    private val folderCountSemaphore = Semaphore(2)
    private val folderCountJobs = ConcurrentHashMap<ServerBrowserFolderCountKey, Job>()
    private val mutableFolderCounts =
        MutableStateFlow<Map<ServerBrowserFolderCountKey, ServerBrowserFolderCounts>>(emptyMap())
    val folderCounts: StateFlow<Map<ServerBrowserFolderCountKey, ServerBrowserFolderCounts>> =
        mutableFolderCounts.asStateFlow()

    init {
        serverRepository.ensureLoaded()
    }

    fun providerName(connectionId: String): String =
        serverRepository.connection(connectionId)?.let(serverRepository::provider)?.displayName ?: "SMB"

    fun clearApkDownloadProgress() {
        mutableApkDownloadProgress.value = null
    }

    private fun reportApkDownloadProgress(fileName: String, copiedBytes: Long, totalBytes: Long) {
        val percent = if (totalBytes > 0L) {
            ((copiedBytes.coerceAtLeast(0L) * 100L) / totalBytes).coerceIn(0L, 100L).toInt()
        } else {
            0
        }
        mutableApkDownloadProgress.value = ServerBrowserApkDownloadProgress(fileName, percent)
    }

    fun supportsWriteActions(sourceId: String, path: String? = null): Boolean = when {
        sourceId == LOCAL_SOURCE -> {
            path?.startsWith(SHIZUKU_STORAGE_PATH_PREFIX) != true &&
                !isLocalStorageRoot(path) && hasLocalWriteAccess()
        }
        sourceId == FREEBOX_SOURCE -> true
        else -> serverRepository.connection(sourceId)?.let { serverRepository.provider(it)?.id == "smb" } == true
    }

    fun supportsCopyActions(sourceId: String, path: String? = null): Boolean = when {
        sourceId != LOCAL_SOURCE -> true
        path?.startsWith(SHIZUKU_STORAGE_PATH_PREFIX) == true ->
            if (isAmazonFireTvStick()) {
                runCatching { (resolveLocalTarget(checkNotNull(path)) as LocalTarget.MountedFile).file.canRead() }
                    .getOrDefault(false)
            } else {
                ShizukuStorageBrowser.availability() == ShizukuStorageBrowser.Availability.READY
            }
        preferences.getString(LOCAL_TREE_URI, null) != null ->
            context.contentResolver.persistedUriPermissions.any { permission ->
                permission.uri == Uri.parse(preferences.getString(LOCAL_TREE_URI, null)) &&
                    permission.isReadPermission
            }
        else -> hasLocalStorageAccess()
    }

    fun customArtworkUri(sourceId: String, entry: ServerBrowserEntry): Uri? =
        preferences.getString(customArtworkKey(sourceId, entry.path), null)?.let(Uri::parse)

    fun observeHomeVideoArtwork(
        sourceId: String,
        entry: ServerBrowserEntry,
        includeFolderArtwork: Boolean = false,
    ): Flow<String?> {
        if (sourceId != FREEBOX_SOURCE) {
            return flowOf(null)
        }

        if (entry.fileType == ServerBrowserFileType.VIDEO) {
            return observeHomeVideoArtworkForPath(entry.path)
        }

        if (!entry.isDirectory || !includeFolderArtwork) return flowOf(null)
        return observeHomeFolderArtwork(entry.path)
    }

    private fun observeHomeVideoArtworkForPath(path: String): Flow<String?> = combine(
        freeboxRepository.observeHomeVideoPresentationForPath(path),
        freeboxRepository.state,
    ) { presentation, _ ->
        freeboxRepository.folderArtwork(path)?.landscape?.takeIf(String::isNotBlank)
            ?: presentation?.background?.takeIf(String::isNotBlank)
            ?: presentation?.poster?.takeIf(String::isNotBlank)
    }

    private fun observeHomeFolderArtwork(folderPath: String): Flow<String?> = flow {
        emit(
            freeboxRepository.folderArtwork(
                folderPath,
                preferPathScopedVideoArtwork = true,
            )?.landscape?.takeIf(String::isNotBlank),
        )
        val representativeVideo = try {
            findFirstVideoInFolder(folderPath)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            null
        }

        if (representativeVideo == null) {
            emitAll(
                freeboxRepository.state.map {
                    freeboxRepository.folderArtwork(
                        folderPath,
                        preferPathScopedVideoArtwork = true,
                    )?.landscape?.takeIf(String::isNotBlank)
                },
            )
            return@flow
        }

        emitAll(
            combine(
                freeboxRepository.observeHomeVideoPresentationForPath(representativeVideo.path),
                freeboxRepository.state,
            ) { presentation, _ ->
                freeboxRepository.folderArtwork(
                    folderPath,
                    preferPathScopedVideoArtwork = true,
                )?.landscape?.takeIf(String::isNotBlank)
                    ?: freeboxRepository.folderArtwork(representativeVideo.path)
                        ?.landscape?.takeIf(String::isNotBlank)
                    ?: presentation?.background?.takeIf(String::isNotBlank)
                    ?: presentation?.poster?.takeIf(String::isNotBlank)
            },
        )
    }

    private suspend fun findFirstVideoInFolder(folderPath: String, depth: Int = 0): ServerBrowserEntry? {
        if (depth > 8) return null
        val children = freeboxFolderArtworkSemaphore.withPermit {
            remoteEntries(FREEBOX_SOURCE, folderPath)
        }.filterNot { it.name == "." || it.name == ".." }
        children.asSequence()
            .filter { !it.isDirectory && it.fileType == ServerBrowserFileType.VIDEO }
            .sortedWith(Comparator(::compareEpisodeEntries))
            .firstOrNull()
            ?.let { return it }

        for (directory in children.asSequence()
            .filter(ServerBrowserEntry::isDirectory)
            .sortedWith(Comparator(::compareEpisodeEntries))) {
            findFirstVideoInFolder(directory.path, depth + 1)?.let { return it }
        }
        return null
    }

    private fun compareEpisodeEntries(left: ServerBrowserEntry, right: ServerBrowserEntry): Int {
        var order = videoEpisodeOrder(left.name).compareTo(videoEpisodeOrder(right.name))
        if (order != 0) return order

        val a = left.name.substringBeforeLast('.').lowercase()
        val b = right.name.substringBeforeLast('.').lowercase()
        var i = 0
        var j = 0
        while (i < a.length && j < b.length) {
            if (a[i].isDigit() && b[j].isDigit()) {
                var endA = i
                var endB = j
                while (endA < a.length && a[endA].isDigit()) endA++
                while (endB < b.length && b[endB].isDigit()) endB++
                val numberA = a.substring(i, endA).trimStart('0').ifEmpty { "0" }
                val numberB = b.substring(j, endB).trimStart('0').ifEmpty { "0" }
                order = numberA.length.compareTo(numberB.length)
                if (order == 0) order = numberA.compareTo(numberB)
                if (order != 0) return order
                i = endA
                j = endB
            } else {
                order = a[i].compareTo(b[j])
                if (order != 0) return order
                i++
                j++
            }
        }
        order = (a.length - i).compareTo(b.length - j)
        return if (order != 0) order else left.path.compareTo(right.path)
    }

    private fun videoEpisodeOrder(name: String): Long {
        val value = name.substringBeforeLast('.').lowercase()
        val seasonEpisode = Regex("(?:^|[^a-z0-9])s(\\d{1,3})[ ._-]*e(\\d{1,4})(?!\\d)")
            .find(value)
        if (seasonEpisode != null) {
            val season = seasonEpisode.groupValues[1].toLongOrNull() ?: 0L
            val episode = seasonEpisode.groupValues[2].toLongOrNull() ?: 0L
            return season * 100_000L + episode
        }
        val multiEpisode = Regex("(?:^|[^a-z0-9])(\\d{1,3})x(\\d{1,4})(?!\\d)").find(value)
        if (multiEpisode != null) {
            val season = multiEpisode.groupValues[1].toLongOrNull() ?: 0L
            val episode = multiEpisode.groupValues[2].toLongOrNull() ?: 0L
            return season * 100_000L + episode
        }
        val episode = Regex("(?:^|[^a-z0-9])(?:episode|épisode|ep|e)[ ._-]*(\\d{1,4})(?!\\d)")
            .find(value)?.groupValues?.get(1)?.toLongOrNull()
        return episode?.let { 100_000L + it } ?: Long.MAX_VALUE
    }

    suspend fun videoDurationMs(sourceId: String, entry: ServerBrowserEntry): Long? {
        if (entry.fileType != ServerBrowserFileType.VIDEO) return null
        val cacheKey = "$sourceId:${entry.path}:${entry.sizeBytes}:${entry.lastModifiedMs}"
        videoDurationCache[cacheKey]?.let { return it.takeIf { duration -> duration > 0L } }
        return videoDurationSemaphore.withPermit {
            videoDurationCache[cacheKey]?.let { return@withPermit it.takeIf { duration -> duration > 0L } }
            val duration = try {
                when (sourceId) {
                    FREEBOX_SOURCE -> freeboxRepository.videoMetadata(
                        entry.path,
                        entry.sizeBytes.takeIf { it > 0L },
                    )?.durationMinutes?.times(60_000L)
                    LOCAL_SOURCE -> withContext(Dispatchers.IO) { readLocalVideoDurationMs(entry) }
                    else -> readServerVideoDurationMs(sourceId, entry)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Log.w("ServerBrowserViewModel", "Impossible de lire la durée de ${entry.name}", error)
                null
            }
            videoDurationCache[cacheKey] = duration?.takeIf { it > 0L } ?: -1L
            if (videoDurationCache.size > 160) {
                videoDurationCache.keys.firstOrNull { it != cacheKey }?.let(videoDurationCache::remove)
            }
            duration?.takeIf { it > 0L }
        }
    }

    private fun readLocalVideoDurationMs(entry: ServerBrowserEntry): Long? {
        val retriever = MediaMetadataRetriever()
        return try {
            val uri = entry.uri?.let(Uri::parse)
            when {
                uri != null -> retriever.setDataSource(context, uri)
                File(entry.path).isFile -> retriever.setDataSource(entry.path)
                else -> return null
            }
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()
                ?.takeIf { it > 0L }
        } catch (_: Exception) {
            null
        } finally {
            runCatching { retriever.release() }
        }
    }

    private suspend fun readServerVideoDurationMs(sourceId: String, entry: ServerBrowserEntry): Long? {
        if (entry.sizeBytes <= 0L) return null
        val playback = serverPlayback.prepare(
            com.nuvio.tv.data.mediaserver.ServerPlaybackTarget(
                item = com.nuvio.tv.data.mediaserver.ServerItemRef(sourceId, "file:${entry.path}"),
                mediaSourceId = null,
            ),
        )
        return try {
            withContext(Dispatchers.IO) { readDurationFromRangeUrl(playback.url, entry.sizeBytes) }
        } finally {
            serverPlayback.stop(playback.url)
        }
    }

    private fun readDurationFromRangeUrl(url: String, fileSize: Long): Long? {
        val dataSource = object : android.media.MediaDataSource() {
            override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
                if (position < 0L || position >= fileSize) return -1
                if (size <= 0) return 0
                val requested = minOf(size.toLong(), fileSize - position).toInt()
                val connection = (java.net.URL(url).openConnection() as java.net.HttpURLConnection).apply {
                    connectTimeout = 5_000
                    readTimeout = 8_000
                    setRequestProperty("Range", "bytes=$position-${position + requested - 1L}")
                }
                return try {
                    val responseCode = connection.responseCode
                    if (responseCode != 206 && !(responseCode == 200 && position == 0L)) {
                        throw java.io.IOException("Le serveur SMB n’a pas respecté la lecture par plage.")
                    }
                    connection.inputStream.use { input ->
                        var totalRead = 0
                        while (totalRead < requested) {
                            val count = input.read(buffer, offset + totalRead, requested - totalRead)
                            if (count < 0) break
                            totalRead += count
                        }
                        if (totalRead > 0) totalRead else -1
                    }
                } finally {
                    connection.disconnect()
                }
            }

            override fun getSize(): Long = fileSize

            override fun close() = Unit
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

    suspend fun artworkCandidates(
        entry: ServerBrowserEntry,
        portrait: Boolean,
    ): List<TmdbArtworkCandidate> {
        val pathParts = entry.path.replace('\\', '/').split('/').filter(String::isNotBlank)
        val parentTitle = pathParts.dropLast(1).lastOrNull().orEmpty()
        val itemTitle = if (entry.isDirectory) entry.name else entry.name.substringBeforeLast('.', entry.name)
        val normalizedItemTitle = itemTitle.replace(Regex("[._]+"), " ").trim()
        val parentIsSeason = SERVER_BROWSER_SEASON_FOLDER.matches(parentTitle)
        val itemIsSeason = entry.isDirectory && SERVER_BROWSER_SEASON_FOLDER.matches(itemTitle)
        val isNumberedEpisode = SERVER_BROWSER_NUMBERED_EPISODE.matches(normalizedItemTitle)
        val hasEpisodeMarker = SERVER_BROWSER_EPISODE_MARKER.containsMatchIn(normalizedItemTitle)
        val fileSeriesTitle = normalizedItemTitle.replace(SERVER_BROWSER_EPISODE_SUFFIX, "").trim()
        val searchTitle = when {
            itemIsSeason -> parentTitle
            (isNumberedEpisode || hasEpisodeMarker) && parentIsSeason -> pathParts.dropLast(2).lastOrNull().orEmpty()
            hasEpisodeMarker && fileSeriesTitle.isNotBlank() -> fileSeriesTitle
            (isNumberedEpisode || hasEpisodeMarker) -> parentTitle.ifBlank { normalizedItemTitle }
            else -> normalizedItemTitle
        }
        val year = SERVER_BROWSER_YEAR.find(searchTitle)?.value?.toIntOrNull()
        val cleanTitle = searchTitle
            .replace(SERVER_BROWSER_RELEASE_SUFFIX, "")
            .replace(SERVER_BROWSER_YEAR_SUFFIX, "")
            .replace(Regex("\\s+"), " ")
            .trim()
        if (cleanTitle.isBlank()) return emptyList()

        val mediaTypes = when {
            itemIsSeason || isNumberedEpisode || hasEpisodeMarker || parentIsSeason -> listOf("tv")
            entry.isDirectory -> listOf("tv", "movie")
            else -> listOf("movie", "tv")
        }
        val language = tmdbSettingsDataStore.settings.first().language
        val perTypeLimit = if (mediaTypes.size > 1) 10 else 20
        val candidates = mutableListOf<TmdbArtworkCandidate>()
        for (mediaType in mediaTypes) {
            candidates += tmdbService.artworkCandidatesForExactTitleWithMatches(
                title = cleanTitle,
                mediaType = mediaType,
                portrait = portrait,
                language = language,
                year = year,
            ).take(perTypeLimit)
        }
        return candidates.distinctBy { "${it.match.contentType}:${it.match.tmdbId}:${it.imageUrl}" }.take(20)
    }

    suspend fun saveCustomArtwork(
        sourceId: String,
        entry: ServerBrowserEntry,
        portrait: Boolean,
        imageUrl: String?,
        candidate: TmdbArtworkCandidate?,
    ) {
        val selectedMatch = candidate?.takeIf { it.imageUrl == imageUrl }?.match
        require(imageUrl.isNullOrBlank() || selectedMatch != null) {
            "La vignette choisie n’est pas associée à une fiche TMDB."
        }
        if (sourceId == FREEBOX_SOURCE) {
            val isInSeasonFolder = entry.path.replace('\\', '/').split('/').any {
                SERVER_BROWSER_SEASON_FOLDER.matches(it)
            }
            freeboxRepository.setFolderArtwork(
                relativePath = entry.path,
                portrait = portrait,
                imageUrl = imageUrl,
                tmdbId = selectedMatch?.tmdbId,
                tmdbContentType = selectedMatch?.contentType?.toApiString(),
                matchVideosByName = !isInSeasonFolder,
            )
        }

        val artworkKey = customArtworkKey(sourceId, entry.path)
        if (imageUrl.isNullOrBlank()) {
            preferences.edit().remove(artworkKey).apply()
        } else {
            preferences.edit().putString(artworkKey, imageUrl).apply()
        }
        mutableState.value = mutableState.value.copy(artworkRevision = mutableState.value.artworkRevision + 1)
        shortcutStore.shortcuts.value.firstOrNull { it.sourceId == sourceId && it.path == entry.path }?.let { shortcut ->
            shortcutStore.add(shortcut.copy(thumbnailUri = imageUrl))
        }
    }

    fun pinToHome(sourceId: String, entry: ServerBrowserEntry) {
        shortcutStore.add(
            ServerBrowserShortcut(
                sourceId = sourceId,
                path = entry.path,
                name = entry.name,
                isDirectory = entry.isDirectory,
                isPlayable = entry.isPlayable,
                thumbnailUri = customArtworkUri(sourceId, entry)?.toString(),
            )
        )
    }

    fun copyToClipboard(sourceId: String, entries: List<ServerBrowserEntry>, cut: Boolean) {
        val targets = entries.distinctBy(ServerBrowserEntry::path)
        require(targets.isNotEmpty()) { "Sélectionnez au moins un élément." }
        require(targets.all { supportsCopyActions(sourceId, it.path) }) {
            "La lecture n’est pas disponible pour cet emplacement local. Vérifiez les autorisations de stockage."
        }
        require(!cut || targets.all { supportsWriteActions(sourceId, it.path) }) {
            "L’écriture n’est pas disponible pour cet emplacement local. Vérifiez les autorisations de stockage."
        }
        val array = JSONArray()
        targets.forEach { entry ->
            array.put(
                JSONObject()
                    .put("sourceId", sourceId)
                    .put("path", entry.path)
                    .put("name", entry.name)
                    .put("isDirectory", entry.isDirectory)
                    .put("isPlayable", entry.isPlayable)
            )
        }
        preferences.edit()
            .putString(CLIPBOARD_ENTRIES, array.toString())
            .putBoolean(CLIPBOARD_IS_CUT, cut)
            .apply()
        mutableState.value = mutableState.value.copy(hasClipboard = true, clipboardIsCut = cut)
    }

    suspend fun pasteClipboard(
        destinationSourceId: String,
        destinationPath: String,
    ): ServerBrowserBatchActionResult {
        require(supportsWriteActions(destinationSourceId, destinationPath)) {
            "Cet emplacement ne permet pas l’écriture. Vérifiez les autorisations de stockage."
        }
        val clipboard = readClipboard()
        require(clipboard.isNotEmpty()) { "Le presse-papiers est vide." }
        val cut = readClipboardIsCut()
        val remainingClipboard = clipboard.toMutableList()
        val destinationEntries = remoteEntries(destinationSourceId, destinationPath)
        val existingNames = destinationEntries.map { it.name.lowercase() }.toMutableSet()
        val failures = mutableListOf<String>()
        var completedCount = 0

        for (source in clipboard) {
            var transferTargetPath: String? = null
            var transferStarted = false
            var transferCompleted = false
            try {
                val sourceParent = parentBrowserPath(source.sourceId, source.path)
                if (cut && source.sourceId == destinationSourceId &&
                    sameBrowserPath(source.sourceId, sourceParent, destinationPath)
                ) {
                    remainingClipboard.remove(source)
                    persistClipboard(remainingClipboard)
                    completedCount++
                    continue
                }
                val destinationEntryPath = joinBrowserPath(destinationSourceId, destinationPath, source.name)
                transferTargetPath = destinationEntryPath
                require(source.name.lowercase() !in existingNames) {
                    "Un élément nommé « ${source.name} » existe déjà dans ce dossier."
                }
                if (source.isDirectory && source.sourceId == destinationSourceId) {
                    require(!sameBrowserPath(source.sourceId, destinationPath, source.path) &&
                        !isBrowserPathDescendant(source.sourceId, destinationPath, source.path)
                    ) {
                        "Impossible de copier un dossier dans lui-même."
                    }
                }
                transferStarted = true
                val createdPath = transferEntry(source, destinationSourceId, destinationEntryPath)
                transferCompleted = true
                if (cut) {
                    try {
                        deleteRemoteEntry(source.sourceId, source.path, source.isDirectory)
                    } catch (error: Exception) {
                        runCatching { deleteRemoteEntry(destinationSourceId, createdPath, source.isDirectory) }
                        throw error
                    }
                    if (source.sourceId == destinationSourceId) {
                        shortcutStore.renameTree(source.sourceId, source.path, createdPath, source.name)
                    } else {
                        val movedShortcuts = shortcutStore.shortcuts.value.filter {
                            it.sourceId == source.sourceId &&
                                (it.path == source.path || it.path.startsWith("${source.path.trimEnd('/')}/"))
                        }
                        shortcutStore.removeTree(source.sourceId, source.path)
                        movedShortcuts.forEach { shortcut ->
                            val newPath = createdPath + shortcut.path.removePrefix(source.path.trimEnd('/'))
                            shortcutStore.add(shortcut.copy(sourceId = destinationSourceId, path = newPath))
                        }
                    }
                    remainingClipboard.remove(source)
                    persistClipboard(remainingClipboard)
                }
                existingNames += source.name.lowercase()
                completedCount++
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                if (transferStarted && !transferCompleted) {
                    transferTargetPath?.let { target ->
                        runCatching { deleteRemoteEntry(destinationSourceId, target, source.isDirectory) }
                    }
                }
                failures += "${source.name}: ${error.localizedMessage?.takeIf(String::isNotBlank) ?: "échec du transfert"}"
                if (cut) {
                    // Leave failed move entries in the clipboard so they can be retried.
                    persistClipboard(remainingClipboard)
                }
            }
        }

        if (cut) persistClipboard(remainingClipboard)
        return ServerBrowserBatchActionResult(
            requestedCount = clipboard.size,
            completedCount = completedCount,
            failures = failures,
        )
    }

    suspend fun deleteEntries(
        sourceId: String,
        entries: List<ServerBrowserEntry>,
    ): ServerBrowserBatchActionResult {
        val targets = entries.distinctBy(ServerBrowserEntry::path)
        val failures = mutableListOf<String>()
        var completedCount = 0
        targets.forEach { entry ->
            try {
                deleteEntry(sourceId, entry)
                completedCount++
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                failures += "${entry.name}: ${error.localizedMessage?.takeIf(String::isNotBlank) ?: "échec de suppression"}"
            }
        }
        return ServerBrowserBatchActionResult(
            requestedCount = targets.size,
            completedCount = completedCount,
            failures = failures,
        )
    }

    suspend fun renameEntry(sourceId: String, entry: ServerBrowserEntry, newName: String) {
        require(supportsWriteActions(sourceId, entry.path)) {
            "Cet emplacement ne permet pas de renommer les éléments. Vérifiez les autorisations de stockage."
        }
        val cleanName = newName.trim()
        require(cleanName.isNotBlank() && cleanName !in setOf(".", "..") && '/' !in cleanName && '\\' !in cleanName) {
            "Le nom saisi n’est pas valide."
        }
        val newPath = when (sourceId) {
            LOCAL_SOURCE -> renameLocalEntry(entry.path, cleanName)
            FREEBOX_SOURCE -> {
                freeboxRepository.renameBrowserEntry(entry.path, cleanName, entry.isDirectory)
                joinBrowserPath(sourceId, parentBrowserPath(sourceId, entry.path), cleanName)
            }
            else -> {
                serverRepository.renameBrowserEntry(sourceId, entry.path, cleanName, entry.isDirectory)
                joinBrowserPath(sourceId, parentBrowserPath(sourceId, entry.path), cleanName)
            }
        }
        val oldArtworkKey = customArtworkKey(sourceId, entry.path)
        val artwork = preferences.getString(oldArtworkKey, null)
        preferences.edit().remove(oldArtworkKey).apply()
        if (artwork != null) preferences.edit().putString(customArtworkKey(sourceId, newPath), artwork).apply()
        shortcutStore.renameTree(sourceId, entry.path, newPath, cleanName)
    }

    suspend fun deleteEntry(sourceId: String, entry: ServerBrowserEntry) {
        require(supportsWriteActions(sourceId, entry.path)) {
            "Cet emplacement ne permet pas la suppression. Vérifiez les autorisations de stockage."
        }
        deleteRemoteEntry(sourceId, entry.path, entry.isDirectory)
        shortcutStore.removeTree(sourceId, entry.path)
    }

    suspend fun removeFromList(sourceId: String, entry: ServerBrowserEntry) {
        val contentId = playbackContentId(sourceId, entry)
        watchProgressRepository.removeProgress(contentId)
        watchProgressRepository.removeFromHistory(contentId)
    }

    suspend fun clearResumeProgress(sourceId: String, entry: ServerBrowserEntry) {
        watchProgressRepository.removeProgress(playbackContentId(sourceId, entry))
    }

    suspend fun exportAsZip(sourceId: String, entry: ServerBrowserEntry): Uri = withContext(Dispatchers.IO) {
        require(sourceId != LOCAL_SOURCE) { "L’export ZIP est réservé aux serveurs réseau." }
        val directory = File(context.cacheDir, "server_browser_exports").apply { mkdirs() }
        directory.listFiles()?.filter { it.lastModified() < System.currentTimeMillis() - FILE_CACHE_MAX_AGE_MS }?.forEach(File::delete)
        val safeName = entry.name.substringBeforeLast('.', entry.name).replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .ifBlank { "export" }
        val zip = File(directory, "${safeName}_${UUID.randomUUID()}.zip")
        try {
            ZipOutputStream(zip.outputStream().buffered()).use { output ->
                if (entry.isDirectory) {
                    addZipDirectory(sourceId, entry.path, "${entry.name.trimEnd('/')}/", output)
                } else {
                    addZipFile(sourceId, entry.path, entry.name, output)
                }
            }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", zip)
        } catch (error: Exception) {
            zip.delete()
            throw error
        }
    }

    suspend fun prepareVideoFrameUri(sourceId: String, entry: ServerBrowserEntry): Uri = withContext(Dispatchers.IO) {
        val sourceUri = prepareFileUri(sourceId, entry)
        val retriever = MediaMetadataRetriever()
        val frame = try {
            context.contentResolver.openFileDescriptor(sourceUri, "r")?.use { descriptor ->
                retriever.setDataSource(descriptor.fileDescriptor)
                retriever.getFrameAtTime(-1L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            } ?: error("Impossible d’ouvrir la vidéo pour créer son image.")
        } finally {
            runCatching { retriever.release() }
        } ?: error("Aucune image n’a pu être extraite de cette vidéo.")

        val directory = File(context.cacheDir, "server_browser_wallpaper").apply { mkdirs() }
        val image = File(directory, "frame_${UUID.randomUUID()}.jpg")
        try {
            image.outputStream().use { output ->
                check(frame.compress(Bitmap.CompressFormat.JPEG, 92, output)) { "Impossible d’enregistrer l’image de la vidéo." }
            }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", image)
        } finally {
            frame.recycle()
        }
    }

    private fun readClipboard(): List<ClipboardEntry> = runCatching {
        val json = JSONArray(preferences.getString(CLIPBOARD_ENTRIES, "[]"))
        (0 until json.length()).mapNotNull { index ->
            val item = json.optJSONObject(index) ?: return@mapNotNull null
            val sourceId = item.optString("sourceId").takeIf(String::isNotBlank) ?: return@mapNotNull null
            val path = item.optString("path").takeIf(String::isNotBlank) ?: return@mapNotNull null
            val name = item.optString("name").takeIf(String::isNotBlank) ?: return@mapNotNull null
            ClipboardEntry(
                sourceId = sourceId,
                path = path,
                name = name,
                isDirectory = item.optBoolean("isDirectory"),
                isPlayable = item.optBoolean("isPlayable"),
            )
        }
    }.getOrDefault(emptyList())

    private fun readClipboardIsCut(): Boolean = preferences.getBoolean(CLIPBOARD_IS_CUT, false)

    private fun persistClipboard(entries: List<ClipboardEntry>) {
        if (entries.isEmpty()) {
            preferences.edit().remove(CLIPBOARD_ENTRIES).remove(CLIPBOARD_IS_CUT).apply()
            mutableState.value = mutableState.value.copy(hasClipboard = false, clipboardIsCut = false)
            return
        }
        val json = JSONArray()
        entries.forEach { entry ->
            json.put(
                JSONObject()
                    .put("sourceId", entry.sourceId)
                    .put("path", entry.path)
                    .put("name", entry.name)
                    .put("isDirectory", entry.isDirectory)
                    .put("isPlayable", entry.isPlayable)
            )
        }
        preferences.edit().putString(CLIPBOARD_ENTRIES, json.toString()).putBoolean(CLIPBOARD_IS_CUT, true).apply()
        mutableState.value = mutableState.value.copy(hasClipboard = true, clipboardIsCut = true)
    }

    private fun playbackContentId(sourceId: String, entry: ServerBrowserEntry): String = when (sourceId) {
        FREEBOX_SOURCE -> freeboxContentIdFor(entry.path, entry.sizeBytes, entry.lastModifiedMs, entry.fileId)
        else -> ServerItemRef(sourceId, "file:${entry.path}").encode()
    }

    private suspend fun remoteEntries(sourceId: String, path: String): List<ServerBrowserEntry> = when (sourceId) {
        LOCAL_SOURCE -> loadLocal(path).third
        FREEBOX_SOURCE -> freeboxRepository.listBrowserEntries(path).map { entry ->
            ServerBrowserEntry(
                name = entry.name,
                path = entry.path,
                isDirectory = entry.isDirectory,
                sizeBytes = entry.sizeBytes,
                lastModifiedMs = entry.lastModifiedMs,
                fileId = entry.fileId,
                mimeType = if (entry.isDirectory) null else entry.name.guessMimeType(),
                isPlayable = entry.isVideoFile,
            )
        }
        else -> serverRepository.browse(sourceId, path).map { it.toBrowserEntry() }
    }

    fun requestFolderCounts(sourceId: String, path: String, showHiddenFiles: Boolean) {
        val key = ServerBrowserFolderCountKey(sourceId, path, showHiddenFiles)
        if (key in mutableFolderCounts.value || folderCountJobs.containsKey(key)) return

        mutableFolderCounts.value = mutableFolderCounts.value + (key to ServerBrowserFolderCounts.Loading)
        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            try {
                val children = folderCountSemaphore.withPermit { remoteEntries(sourceId, path) }
                    .filter { child ->
                        child.name != "." && child.name != ".." &&
                            (showHiddenFiles || !child.name.startsWith('.'))
                    }
                mutableFolderCounts.value = mutableFolderCounts.value + (
                    key to ServerBrowserFolderCounts.Ready(
                        folders = children.count(ServerBrowserEntry::isDirectory),
                        files = children.count { !it.isDirectory },
                    )
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableFolderCounts.value = mutableFolderCounts.value + (key to ServerBrowserFolderCounts.Unavailable)
            }
        }
        folderCountJobs[key] = job
        job.invokeOnCompletion { folderCountJobs.remove(key, job) }
        job.start()
    }

    fun invalidateFolderCounts(sourceId: String) {
        folderCountJobs.entries
            .filter { it.key.sourceId == sourceId }
            .forEach { (key, job) ->
                folderCountJobs.remove(key, job)
                job.cancel()
            }
        mutableFolderCounts.value = mutableFolderCounts.value.filterKeys { it.sourceId != sourceId }
    }

    private suspend fun transferEntry(
        source: ClipboardEntry,
        destinationSourceId: String,
        destinationPath: String,
    ): String {
        return if (source.isDirectory) {
            val actualDestinationPath = createRemoteDirectory(destinationSourceId, destinationPath)
            try {
                remoteEntries(source.sourceId, source.path).forEach { child ->
                    transferEntry(
                        ClipboardEntry(
                            sourceId = source.sourceId,
                            path = child.path,
                            name = child.name,
                            isDirectory = child.isDirectory,
                            isPlayable = child.isPlayable,
                        ),
                        destinationSourceId,
                        joinBrowserPath(destinationSourceId, actualDestinationPath, child.name),
                    )
                }
            } catch (error: Exception) {
                runCatching { deleteRemoteEntry(destinationSourceId, actualDestinationPath, isDirectory = true) }
                throw error
            }
            actualDestinationPath
        } else {
            val temporary = File(context.cacheDir, "browser_transfer_${UUID.randomUUID()}.tmp")
            try {
                downloadRemoteFile(source.sourceId, source.path, temporary)
                val expectedSize = temporary.length()
                try {
                    uploadRemoteFile(destinationSourceId, destinationPath, temporary)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (uploadError: Exception) {
                    val uploadedEntry = try {
                        remoteEntries(destinationSourceId, parentBrowserPath(destinationSourceId, destinationPath))
                            .firstOrNull { it.name.equals(source.name, ignoreCase = true) }
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (_: Exception) {
                        null
                    }
                    if (uploadedEntry == null || uploadedEntry.isDirectory || uploadedEntry.sizeBytes != expectedSize) {
                        throw uploadError
                    }
                }
                destinationPath
            } finally {
                temporary.delete()
            }
        }
    }

    private suspend fun createRemoteDirectory(sourceId: String, path: String): String = when (sourceId) {
        LOCAL_SOURCE -> createLocalDirectory(path)
        FREEBOX_SOURCE -> {
            freeboxRepository.createBrowserDirectory(path)
            path
        }
        else -> {
            serverRepository.createBrowserDirectory(sourceId, path)
            path
        }
    }

    private suspend fun deleteRemoteEntry(sourceId: String, path: String, isDirectory: Boolean) {
        when (sourceId) {
            LOCAL_SOURCE -> deleteLocalEntry(path, isDirectory)
            FREEBOX_SOURCE -> freeboxRepository.deleteBrowserEntry(path, isDirectory)
            else -> serverRepository.deleteBrowserEntry(sourceId, path, isDirectory)
        }
    }

    private suspend fun downloadRemoteFile(sourceId: String, path: String, destination: File) {
        when (sourceId) {
            LOCAL_SOURCE -> copyLocalFileToCache(path, destination)
            FREEBOX_SOURCE -> freeboxRepository.copyBrowserFileToCache(path, destination)
            else -> serverRepository.downloadFile(sourceId, path, destination)
        }
    }

    private suspend fun uploadRemoteFile(sourceId: String, path: String, source: File): String = when (sourceId) {
        LOCAL_SOURCE -> uploadLocalFile(path, source)
        FREEBOX_SOURCE -> {
            freeboxRepository.uploadBrowserFile(path, source)
            path
        }
        else -> {
            serverRepository.uploadBrowserFile(sourceId, path, source)
            path
        }
    }

    private suspend fun addZipDirectory(sourceId: String, path: String, zipPath: String, output: ZipOutputStream) {
        output.putNextEntry(ZipEntry(zipPath))
        output.closeEntry()
        remoteEntries(sourceId, path).forEach { child ->
            val childZipPath = zipPath + child.name + if (child.isDirectory) "/" else ""
            if (child.isDirectory) addZipDirectory(sourceId, child.path, childZipPath, output)
            else addZipFile(sourceId, child.path, childZipPath, output)
        }
    }

    private suspend fun addZipFile(sourceId: String, path: String, zipPath: String, output: ZipOutputStream) {
        val temporary = File(context.cacheDir, "browser_zip_${UUID.randomUUID()}.tmp")
        try {
            downloadRemoteFile(sourceId, path, temporary)
            output.putNextEntry(ZipEntry(zipPath))
            temporary.inputStream().buffered().use { input -> input.copyTo(output) }
            output.closeEntry()
        } finally {
            temporary.delete()
        }
    }

    private data class ClipboardEntry(
        val sourceId: String,
        val path: String,
        val name: String,
        val isDirectory: Boolean,
        val isPlayable: Boolean,
    )

    private sealed interface LocalTarget {
        data class TreeDocument(val uri: Uri) : LocalTarget
        data class MountedFile(val file: File) : LocalTarget
        data class ShizukuFile(val file: File) : LocalTarget
    }

    private fun resolveLocalTarget(path: String): LocalTarget {
        require(path.isNotBlank()) { "Chemin local vide." }
        if (path.startsWith(SHIZUKU_STORAGE_PATH_PREFIX)) {
            val file = canonicalMountedShizukuPath(path.removePrefix(SHIZUKU_STORAGE_PATH_PREFIX))
            if (isAmazonFireTvStick()) return LocalTarget.MountedFile(file)
            return LocalTarget.ShizukuFile(
                file,
            )
        }
        if (isAmazonFireTvStick() && path.startsWith(File.separator)) {
            return LocalTarget.MountedFile(mountedLocalFile(path))
        }
        if (preferences.getString(LOCAL_TREE_URI, null) != null) {
            return LocalTarget.TreeDocument(
                localTreeUriFor(path),
            )
        }
        return LocalTarget.MountedFile(mountedLocalFile(path))
    }

    private fun mountedLocalFile(path: String): File {
        val file = runCatching { File(path).canonicalFile }.getOrNull()
            ?: throw IOException("Chemin local invalide.")
        require(mountedLocalStorageRoots().any { isWithinStorageRoot(file, it.directory) }) {
            "Ce fichier est hors des stockages local et USB autorisés."
        }
        return file
    }

    private fun localParentPath(path: String): String = when {
        isAmazonFireTvStick() && path.startsWith(SHIZUKU_STORAGE_PATH_PREFIX) -> {
            val file = resolveLocalTarget(path) as LocalTarget.MountedFile
            file.file.parentFile?.path ?: throw IOException("Le dossier parent local est introuvable.")
        }
        path.startsWith(SHIZUKU_STORAGE_PATH_PREFIX) -> {
            val file = resolveLocalTarget(path) as LocalTarget.ShizukuFile
            SHIZUKU_STORAGE_PATH_PREFIX + (file.file.parentFile?.path
                ?: throw IOException("Le dossier parent local est introuvable."))
        }
        isAmazonFireTvStick() && path.startsWith(File.separator) -> {
            val file = resolveLocalTarget(path) as LocalTarget.MountedFile
            file.file.parentFile?.path ?: ""
        }
        preferences.getString(LOCAL_TREE_URI, null) != null -> {
            val treeUri = Uri.parse(checkNotNull(preferences.getString(LOCAL_TREE_URI, null)))
            val rootId = DocumentsContract.getTreeDocumentId(treeUri)
            when {
                path == rootId -> ""
                rootId.endsWith(":") && path.startsWith(rootId) -> {
                    val suffix = path.removePrefix(rootId)
                    if ('/' in suffix) rootId + suffix.substringBeforeLast('/') else rootId
                }
                '/' in path -> path.substringBeforeLast('/')
                else -> rootId
            }
        }
        else -> {
            val file = resolveLocalTarget(path) as LocalTarget.MountedFile
            file.file.parentFile?.path ?: ""
        }
    }

    private fun parentBrowserPath(sourceId: String, path: String): String =
        if (sourceId == LOCAL_SOURCE) localParentPath(path) else path.substringBeforeLast('/', "")

    private fun joinBrowserPath(sourceId: String, parent: String, child: String): String {
        if (sourceId != LOCAL_SOURCE) {
            return listOf(parent.trim('/'), child.trim('/'))
                .filter(String::isNotBlank)
                .joinToString("/")
        }
        require('/' !in child && '\\' !in child && child !in setOf(".", "..")) {
            "Nom de fichier local invalide."
        }
        return when {
            isAmazonFireTvStick() && parent.startsWith(SHIZUKU_STORAGE_PATH_PREFIX) -> {
                val parentFile = resolveLocalTarget(parent) as LocalTarget.MountedFile
                File(parentFile.file, child).path
            }
            parent.startsWith(SHIZUKU_STORAGE_PATH_PREFIX) -> {
                val parentFile = resolveLocalTarget(parent) as LocalTarget.ShizukuFile
                SHIZUKU_STORAGE_PATH_PREFIX + File(parentFile.file, child).path
            }
            isAmazonFireTvStick() && parent.startsWith(File.separator) -> {
                val parentFile = resolveLocalTarget(parent) as LocalTarget.MountedFile
                File(parentFile.file, child).path
            }
            preferences.getString(LOCAL_TREE_URI, null) != null -> {
                val treeUri = Uri.parse(checkNotNull(preferences.getString(LOCAL_TREE_URI, null)))
                val rootId = DocumentsContract.getTreeDocumentId(treeUri)
                if (parent.isBlank()) if (rootId.endsWith(":")) "$rootId$child" else "$rootId/$child"
                else if (parent == rootId && rootId.endsWith(":")) "$rootId$child"
                else "$parent/$child"
            }
            else -> {
                val parentFile = resolveLocalTarget(parent) as LocalTarget.MountedFile
                File(parentFile.file, child).path
            }
        }
    }

    private fun hasLocalWriteAccess(): Boolean {
        val treeUri = preferences.getString(LOCAL_TREE_URI, null)?.let(Uri::parse)
        if (treeUri != null) {
            return context.contentResolver.persistedUriPermissions.any {
                it.uri == treeUri && it.isWritePermission
            }
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun isLocalStorageRoot(path: String?): Boolean {
        if (path.isNullOrBlank() || path.startsWith(SHIZUKU_STORAGE_PATH_PREFIX)) return false
        if (isAmazonFireTvStick() && path.startsWith(File.separator)) {
            val file = runCatching { File(path).canonicalFile }.getOrNull() ?: return false
            return mountedLocalStorageRoots().any { file == it.directory }
        }
        val treeUri = preferences.getString(LOCAL_TREE_URI, null)?.let(Uri::parse)
        if (treeUri != null) {
            return runCatching { path == DocumentsContract.getTreeDocumentId(treeUri) }.getOrDefault(false)
        }
        val file = runCatching { File(path).canonicalFile }.getOrNull() ?: return false
        return mountedLocalStorageRoots().any { file == it.directory }
    }

    private fun localTreeUriFor(documentId: String): Uri {
        val treeUri = preferences.getString(LOCAL_TREE_URI, null)?.let(Uri::parse)
            ?: throw IOException("L’accès au dossier local a expiré. Sélectionnez-le à nouveau.")
        val rootId = DocumentsContract.getTreeDocumentId(treeUri)
        require(
            documentId == rootId ||
                (rootId.endsWith(":") && documentId.startsWith(rootId)) ||
                documentId.startsWith("$rootId/"),
        ) { "Ce document est en dehors du dossier local sélectionné." }
        return DocumentsContract.buildDocumentUriUsingTree(treeUri, documentId)
    }

    private fun localDocumentName(path: String): String {
        if (isAmazonFireTvStick() && path.startsWith(SHIZUKU_STORAGE_PATH_PREFIX)) {
            return (resolveLocalTarget(path) as LocalTarget.MountedFile).file.name
        }
        if (path.startsWith(SHIZUKU_STORAGE_PATH_PREFIX)) {
            return (resolveLocalTarget(path) as LocalTarget.ShizukuFile).file.name
        }
        if (isAmazonFireTvStick() && path.startsWith(File.separator)) {
            return (resolveLocalTarget(path) as LocalTarget.MountedFile).file.name
        }
        if (preferences.getString(LOCAL_TREE_URI, null) != null) {
            val treeUri = Uri.parse(checkNotNull(preferences.getString(LOCAL_TREE_URI, null)))
            val rootId = DocumentsContract.getTreeDocumentId(treeUri)
            val suffix = if (rootId.endsWith(":") && path.startsWith(rootId)) {
                path.removePrefix(rootId).trimStart('/')
            } else {
                path.removePrefix("$rootId/")
            }
            return suffix.substringAfterLast('/').ifBlank { rootId.substringAfterLast(':').substringAfterLast('/') }
        }
        return (resolveLocalTarget(path) as LocalTarget.MountedFile).file.name
    }

    private suspend fun createLocalDirectory(path: String): String = withContext(Dispatchers.IO) {
        val name = localDocumentName(path)
        require(name.isNotBlank() && name !in setOf(".", "..")) { "Nom de dossier local invalide." }
        when (val target = resolveLocalTarget(path)) {
            is LocalTarget.TreeDocument -> {
                val treeUri = Uri.parse(checkNotNull(preferences.getString(LOCAL_TREE_URI, null)))
                val parentId = localParentPath(path).ifBlank { DocumentsContract.getTreeDocumentId(treeUri) }
                val created = DocumentsContract.createDocument(
                    context.contentResolver,
                    localTreeUriFor(parentId),
                    DocumentsContract.Document.MIME_TYPE_DIR,
                    name,
                ) ?: throw IOException("Impossible de créer le dossier local « $name ».")
                DocumentsContract.getDocumentId(created)
            }
            is LocalTarget.MountedFile -> {
                require(!target.file.exists()) { "Un élément nommé « $name » existe déjà." }
                require(target.file.mkdir()) { "Impossible de créer le dossier local « $name »." }
                target.file.canonicalPath
            }
            is LocalTarget.ShizukuFile -> {
                throw IOException("L’écriture dans Android/data nécessite une opération Shizuku dédiée.")
            }
        }
    }

    private suspend fun deleteLocalEntry(path: String, isDirectory: Boolean): Unit = withContext(Dispatchers.IO) {
        val target = resolveLocalTarget(path)
        when (target) {
            is LocalTarget.TreeDocument -> {
                val treeUri = Uri.parse(checkNotNull(preferences.getString(LOCAL_TREE_URI, null)))
                require(DocumentsContract.getDocumentId(target.uri) != DocumentsContract.getTreeDocumentId(treeUri)) {
                    "La suppression de la racine du dossier sélectionné est interdite."
                }
                if (isDirectory) {
                    remoteEntries(LOCAL_SOURCE, path).forEach { child ->
                        deleteLocalEntry(child.path, child.isDirectory)
                    }
                }
                require(DocumentsContract.deleteDocument(context.contentResolver, target.uri)) {
                    "Impossible de supprimer « ${localDocumentName(path)} »."
                }
            }
            is LocalTarget.MountedFile -> {
                val root = mountedLocalStorageRoots().firstOrNull { isWithinStorageRoot(target.file, it.directory) }
                    ?: throw IOException("Ce fichier est hors des stockages local et USB autorisés.")
                require(target.file != root.directory) { "La suppression d’une racine de stockage est interdite." }
                val deleted = if (isDirectory) target.file.deleteRecursively() else target.file.delete()
                require(deleted) { "Impossible de supprimer « ${target.file.name} »." }
            }
            is LocalTarget.ShizukuFile -> {
                throw IOException("La suppression dans Android/data n’est pas disponible depuis ce menu.")
            }
        }
    }

    private suspend fun copyLocalFileToCache(path: String, destination: File) = withContext(Dispatchers.IO) {
        when (val target = resolveLocalTarget(path)) {
            is LocalTarget.TreeDocument -> {
                context.contentResolver.openInputStream(target.uri)?.use { input ->
                    destination.outputStream().buffered().use { output -> input.copyTo(output) }
                } ?: throw IOException("Impossible d’ouvrir « ${localDocumentName(path)} ».")
            }
            is LocalTarget.MountedFile -> {
                require(target.file.isFile && target.file.canRead()) { "Impossible de lire « ${target.file.name} »." }
                target.file.inputStream().buffered().use { input ->
                    destination.outputStream().buffered().use { output -> input.copyTo(output) }
                }
            }
            is LocalTarget.ShizukuFile -> ShizukuStorageBrowser.copyToFile(
                path = target.file.path,
                destination = destination,
                totalBytes = target.file.length(),
                onProgress = { _, _ -> },
            )
        }
    }

    private suspend fun uploadLocalFile(path: String, source: File): String = withContext(Dispatchers.IO) {
        val name = localDocumentName(path)
        require(name.isNotBlank() && name !in setOf(".", "..")) { "Nom de fichier local invalide." }
        when (val target = resolveLocalTarget(path)) {
            is LocalTarget.TreeDocument -> {
                val treeUri = Uri.parse(checkNotNull(preferences.getString(LOCAL_TREE_URI, null)))
                val parentId = localParentPath(path).ifBlank { DocumentsContract.getTreeDocumentId(treeUri) }
                val mimeType = name.substringAfterLast('.', "").lowercase()
                    .let(MimeTypeMap.getSingleton()::getMimeTypeFromExtension)
                    ?: "application/octet-stream"
                val created = DocumentsContract.createDocument(
                    context.contentResolver,
                    localTreeUriFor(parentId),
                    mimeType,
                    name,
                ) ?: throw IOException("Impossible de créer le fichier local « $name ».")
                try {
                    context.contentResolver.openOutputStream(created, "w")?.use { output ->
                        source.inputStream().buffered().use { input -> input.copyTo(output) }
                    } ?: throw IOException("Impossible d’écrire « $name ».")
                } catch (error: Exception) {
                    runCatching { DocumentsContract.deleteDocument(context.contentResolver, created) }
                    throw error
                }
                DocumentsContract.getDocumentId(created)
            }
            is LocalTarget.MountedFile -> {
                require(!target.file.exists()) { "Un élément nommé « $name » existe déjà." }
                val parent = target.file.parentFile ?: throw IOException("Le dossier parent local est introuvable.")
                require(parent.isDirectory && parent.canWrite()) { "Le dossier parent local n’est pas accessible en écriture." }
                source.inputStream().buffered().use { input ->
                    try {
                        target.file.outputStream().buffered().use { output -> input.copyTo(output) }
                    } catch (error: Exception) {
                        target.file.delete()
                        throw error
                    }
                }
                target.file.canonicalPath
            }
            is LocalTarget.ShizukuFile -> throw IOException("Le collage dans Android/data n’est pas disponible depuis ce menu.")
        }
    }

    private suspend fun renameLocalEntry(path: String, newName: String): String = withContext(Dispatchers.IO) {
        val target = resolveLocalTarget(path)
        when (target) {
            is LocalTarget.TreeDocument -> {
                val renamed = DocumentsContract.renameDocument(context.contentResolver, target.uri, newName)
                    ?: throw IOException("Impossible de renommer « ${localDocumentName(path)} ».")
                DocumentsContract.getDocumentId(renamed)
            }
            is LocalTarget.MountedFile -> {
                val destination = File(target.file.parentFile, newName)
                require(!destination.exists()) { "Un élément nommé « $newName » existe déjà." }
                require(target.file.renameTo(destination)) { "Impossible de renommer « ${target.file.name} »." }
                destination.canonicalPath
            }
            is LocalTarget.ShizukuFile -> {
                throw IOException("Le renommage dans Android/data n’est pas disponible depuis ce menu.")
            }
        }
    }

    private fun sameBrowserPath(sourceId: String, first: String, second: String): Boolean =
        if (sourceId == LOCAL_SOURCE && !first.startsWith(SHIZUKU_STORAGE_PATH_PREFIX) &&
            preferences.getString(LOCAL_TREE_URI, null) == null
        ) {
            runCatching { File(first).canonicalPath == File(second).canonicalPath }.getOrDefault(false)
        } else {
            first.trimEnd('/').equals(second.trimEnd('/'), ignoreCase = true)
        }

    private fun isBrowserPathDescendant(sourceId: String, path: String, parent: String): Boolean =
        if (sourceId == LOCAL_SOURCE && !path.startsWith(SHIZUKU_STORAGE_PATH_PREFIX) &&
            !parent.startsWith(SHIZUKU_STORAGE_PATH_PREFIX) && preferences.getString(LOCAL_TREE_URI, null) == null
        ) {
            runCatching {
                val canonicalPath = File(path).canonicalPath
                val canonicalParent = File(parent).canonicalPath.trimEnd(File.separatorChar)
                canonicalPath.startsWith("$canonicalParent${File.separator}", ignoreCase = true)
            }.getOrDefault(false)
        } else {
            path.startsWith("${parent.trimEnd('/')}/", ignoreCase = true)
        }

    fun saveLocalTree(uri: Uri): Boolean {
        return try {
            val readWriteFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            try {
                context.contentResolver.takePersistableUriPermission(uri, readWriteFlags)
            } catch (_: SecurityException) {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            preferences.edit().putString(LOCAL_TREE_URI, uri.toString()).apply()
            mutableState.value = mutableState.value.copy(
                localTreeUri = uri.toString(),
                localStorageAccessGranted = hasLocalStorageAccess(),
                error = null,
            )
            true
        } catch (error: Exception) {
            mutableState.value = mutableState.value.copy(
                error = error.message ?: "Impossible de conserver l’accès à ce dossier."
            )
            false
        }
    }

    fun hasLocalStorageAccess(): Boolean =
        if (isAmazonFireTvStick()) {
            true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

    fun clearLocalTree() {
        preferences.getString(LOCAL_TREE_URI, null)?.let { storedUri ->
            val uri = Uri.parse(storedUri)
            listOf(Intent.FLAG_GRANT_READ_URI_PERMISSION, Intent.FLAG_GRANT_WRITE_URI_PERMISSION).forEach { permission ->
                runCatching { context.contentResolver.releasePersistableUriPermission(uri, permission) }
            }
        }
        preferences.edit().remove(LOCAL_TREE_URI).apply()
        mutableState.value = mutableState.value.copy(localTreeUri = null, error = null)
    }

    fun toggleShowHiddenFiles(sourceId: String, path: String) {
        val showHidden = !preferences.getBoolean(SHOW_HIDDEN_FILES, false)
        preferences.edit().putBoolean(SHOW_HIDDEN_FILES, showHidden).apply()
        mutableState.value = mutableState.value.copy(showHiddenFiles = showHidden)
        load(sourceId, path)
    }

    fun toggleGridView(sourceId: String, path: String) {
        val key = viewModePreferenceKey(sourceId, path)
        val isGridView = !preferences.getBoolean(key, DEFAULT_GRID_VIEW)
        preferences.edit().putBoolean(key, isGridView).apply()
        mutableState.value = mutableState.value.copy(isGridView = isGridView)
    }

    suspend fun prepareThumbnailUri(sourceId: String, entry: ServerBrowserEntry): Uri? {
        if (entry.fileType != ServerBrowserFileType.IMAGE) return null
        if (entry.isHeicImage()) {
            val thumbnailUri = if (sourceId == LOCAL_SOURCE) {
                heicThumbnailSemaphore.withPermit {
                    prepareHeicImageUri(sourceId, entry, maxDimension = 384)
                }
            } else {
                thumbnailSemaphore.withPermit {
                    heicThumbnailSemaphore.withPermit {
                        prepareHeicImageUri(sourceId, entry, maxDimension = 384)
                    }
                }
            }
            thumbnailUri?.let { return it }
        }
        if (sourceId == LOCAL_SOURCE) return entry.uri?.let(Uri::parse)

        return thumbnailSemaphore.withPermit {
            withContext(Dispatchers.IO) {
                val directory = File(context.cacheDir, "server_browser_thumbnails").apply { mkdirs() }
                val key = thumbnailCacheKey(sourceId, entry.path, entry.sizeBytes)
                val imageExtension = entry.name.substringAfterLast('.', "").lowercase()
                    .takeIf { it.matches(Regex("[a-z0-9]{1,8}")) }
                    ?: "img"
                val cachedFile = File(directory, "$key.preview.$imageExtension")
                val pendingFile = File(directory, "$key.preview.$imageExtension.part")
                val cached = cachedFile.takeIf(File::isFile)
                if (cached != null && (entry.sizeBytes <= 0L || cached.length() >= entry.sizeBytes)) {
                    cached.setLastModified(System.currentTimeMillis())
                    return@withContext FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        cached,
                    )
                }
                cachedFile.delete()

                pendingFile.delete()
                try {
                    copyEntryToCache(sourceId, entry, pendingFile)
                    if (!pendingFile.isFile || pendingFile.length() == 0L) {
                        return@withContext null
                    }
                    if (!pendingFile.renameTo(cachedFile) && !cachedFile.isFile) {
                        throw IllegalStateException("Impossible d’enregistrer l’aperçu de cette image.")
                    }

                    trimThumbnailCache(directory)
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        cachedFile,
                    )
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Exception) {
                    Log.w("ServerBrowserViewModel", "Impossible de préparer la vignette ${entry.name}", error)
                    null
                } finally {
                    pendingFile.delete()
                }
            }
        }
    }

    private suspend fun prepareHeicImageUri(
        sourceId: String,
        entry: ServerBrowserEntry,
        maxDimension: Int,
    ): Uri? = withContext(Dispatchers.IO) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.P) return@withContext null
        val directoryName = if (maxDimension <= 512) "server_browser_thumbnails" else "server_browser_files"
        val directory = File(context.cacheDir, directoryName).apply { mkdirs() }
        val cacheKey = "${thumbnailCacheKey(sourceId, entry.path, entry.sizeBytes)}_${entry.lastModifiedMs}"
        val cachedFile = File(directory, "heic_${cacheKey}_${maxDimension}.jpg")
        if (cachedFile.isFile && cachedFile.length() > 0L) {
            cachedFile.setLastModified(System.currentTimeMillis())
            return@withContext FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cachedFile,
            )
        }

        val pendingFile = File(directory, "heic_${cacheKey}_${maxDimension}.part")
        try {
            val entryUri = entry.uri?.let(Uri::parse)
            val source = when {
                sourceId == LOCAL_SOURCE && entryUri?.scheme == "content" ->
                    android.graphics.ImageDecoder.createSource(context.contentResolver, entryUri)
                sourceId == LOCAL_SOURCE && entryUri?.scheme == "file" ->
                    android.graphics.ImageDecoder.createSource(File(entryUri.path.orEmpty()))
                sourceId == LOCAL_SOURCE && File(entry.path).isFile ->
                    android.graphics.ImageDecoder.createSource(File(entry.path))
                else -> {
                    pendingFile.delete()
                    copyEntryToCache(sourceId, entry, pendingFile)
                    if (!pendingFile.isFile || pendingFile.length() == 0L) return@withContext null
                    android.graphics.ImageDecoder.createSource(pendingFile)
                }
            }
            val bitmap = android.graphics.ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                val maxSourceDimension = maxOf(info.size.width, info.size.height).coerceAtLeast(1)
                val scale = minOf(1.0, maxDimension.toDouble() / maxSourceDimension)
                decoder.allocator = android.graphics.ImageDecoder.ALLOCATOR_SOFTWARE
                decoder.isMutableRequired = false
                decoder.setTargetSize(
                    (info.size.width * scale).toInt().coerceAtLeast(1),
                    (info.size.height * scale).toInt().coerceAtLeast(1),
                )
            }
            try {
                val temporaryJpeg = File(directory, "${cachedFile.name}.part")
                try {
                    temporaryJpeg.outputStream().use { output ->
                        check(bitmap.compress(Bitmap.CompressFormat.JPEG, 85, output)) {
                            "Impossible d’enregistrer l’image HEIC convertie."
                        }
                    }
                    if (!temporaryJpeg.renameTo(cachedFile) && (!cachedFile.isFile || cachedFile.length() == 0L)) {
                        throw IllegalStateException("Impossible de finaliser l’image HEIC convertie.")
                    }
                } finally {
                    temporaryJpeg.delete()
                }
            } finally {
                bitmap.recycle()
            }
            trimThumbnailCache(directory)
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cachedFile,
            )
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Log.w("ServerBrowserViewModel", "Impossible de convertir l’image HEIC ${entry.name}", error)
            null
        } finally {
            pendingFile.delete()
        }
    }

    private fun ServerBrowserEntry.isHeicImage(): Boolean =
        fileType == ServerBrowserFileType.IMAGE &&
            name.substringAfterLast('.', "").lowercase() in setOf("heic", "heif")

    suspend fun prepareFileUri(sourceId: String, entry: ServerBrowserEntry): Uri {
        if (entry.isHeicImage()) {
            prepareHeicImageUri(sourceId, entry, maxDimension = 3_840)?.let { return it }
        }
        val isShizukuLocalFile = sourceId == LOCAL_SOURCE && entry.path.startsWith(SHIZUKU_STORAGE_PATH_PREFIX)
        val localFileUri = if (sourceId == LOCAL_SOURCE && !isShizukuLocalFile) {
            entry.uri?.let(Uri::parse) ?: error("L’URI de ce fichier local est indisponible.")
        } else {
            null
        }
        if (sourceId == LOCAL_SOURCE && !isShizukuLocalFile) {
            if (localFileUri?.scheme != "file") {
                return localFileUri ?: error("L’URI de ce fichier local est indisponible.")
            }
        }
        if (entry.fileType == ServerBrowserFileType.APK) {
            mutableApkDownloadProgress.value = ServerBrowserApkDownloadProgress(entry.name, 0)
        }
        return withContext(Dispatchers.IO) {
            val directory = File(context.cacheDir, "server_browser_files").apply { mkdirs() }
            val staleBefore = System.currentTimeMillis() - 3L * 24L * 60L * 60L * 1000L
            directory.listFiles()?.filter { it.lastModified() < staleBefore }?.forEach { it.delete() }
            val safeName = entry.name.substringAfterLast('/').replace(Regex("[^a-zA-Z0-9._-]"), "_")
                .ifBlank { "file" }
            val destination = File(directory, "${UUID.randomUUID()}_$safeName")
            try {
                copyEntryToCache(sourceId, entry, destination) { copiedBytes, totalBytes ->
                    if (entry.fileType == ServerBrowserFileType.APK) {
                        reportApkDownloadProgress(entry.name, copiedBytes, totalBytes)
                    }
                }
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", destination)
            } catch (error: Exception) {
                destination.delete()
                if (entry.fileType == ServerBrowserFileType.APK) clearApkDownloadProgress()
                throw error
            }
        }
    }

    private suspend fun copyEntryToCache(
        sourceId: String,
        entry: ServerBrowserEntry,
        destination: File,
        onProgress: (copiedBytes: Long, totalBytes: Long) -> Unit = { _, _ -> },
    ) {
        var lastError: Exception? = null
        repeat(FILE_COPY_ATTEMPTS) { attempt ->
            destination.delete()
            try {
                if (sourceId == FREEBOX_SOURCE) {
                    if (entry.fileType == ServerBrowserFileType.IMAGE) {
                        freeboxRepository.copyBrowserImageToCache(entry.path, destination, onProgress)
                    } else {
                        freeboxRepository.copyBrowserFileToCache(entry.path, destination, onProgress)
                    }
                } else if (sourceId == LOCAL_SOURCE) {
                    if (entry.path.startsWith(SHIZUKU_STORAGE_PATH_PREFIX)) {
                        ShizukuStorageBrowser.copyToFile(
                            path = canonicalMountedShizukuPath(
                                entry.path.removePrefix(SHIZUKU_STORAGE_PATH_PREFIX),
                            ).path,
                            destination = destination,
                            totalBytes = entry.sizeBytes,
                            onProgress = onProgress,
                        )
                    } else {
                        val sourceUri = entry.uri?.let(Uri::parse)
                            ?: throw IOException("L’URI de ce fichier local est indisponible.")
                        val totalBytes = entry.sizeBytes
                        context.contentResolver.openInputStream(sourceUri)?.use { input ->
                            destination.outputStream().buffered().use { output ->
                                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                                var copiedBytes = 0L
                                while (true) {
                                    val count = input.read(buffer)
                                    if (count < 0) break
                                    output.write(buffer, 0, count)
                                    copiedBytes += count
                                    onProgress(copiedBytes, totalBytes)
                                }
                            }
                        } ?: throw IOException("Impossible d’ouvrir « ${entry.name} ».")
                    }
                } else {
                    serverRepository.downloadFile(sourceId, entry.path, destination, onProgress)
                }

                if (!destination.isFile) {
                    throw IOException("Le serveur n’a pas fourni le fichier demandé.")
                }
                val actualSize = destination.length()
                if (actualSize == 0L && (entry.fileType == ServerBrowserFileType.IMAGE || entry.fileType == ServerBrowserFileType.APK)) {
                    throw IOException("Le fichier « ${entry.name} » reçu du serveur est vide.")
                }
                val sizeMismatch = entry.sizeBytes > 0L &&
                    (if (entry.fileType == ServerBrowserFileType.APK) {
                        actualSize != entry.sizeBytes
                    } else {
                        actualSize < entry.sizeBytes
                    })
                if (sizeMismatch) {
                    throw IOException(
                        "Copie invalide de « ${entry.name} » : $actualSize octets reçus sur ${entry.sizeBytes} attendus.",
                    )
                }
                return
            } catch (cancelled: CancellationException) {
                destination.delete()
                throw cancelled
            } catch (error: Exception) {
                lastError = error
                destination.delete()
                if (attempt < FILE_COPY_ATTEMPTS - 1) delay(FILE_COPY_RETRY_DELAY_MS)
            }
        }

        val detail = lastError?.localizedMessage?.takeIf(String::isNotBlank)
        throw IOException(
            detail?.let { "Échec du transfert de « ${entry.name} » après $FILE_COPY_ATTEMPTS tentatives : $it" }
                ?: "Échec du transfert de « ${entry.name} » après $FILE_COPY_ATTEMPTS tentatives.",
            lastError,
        )
    }

    fun load(sourceId: String, path: String) {
        invalidateFolderCounts(sourceId)
        mutableState.value = mutableState.value.copy(
            isLoading = true,
            error = null,
            localStorageAccessGranted = hasLocalStorageAccess(),
            showHiddenFiles = preferences.getBoolean(SHOW_HIDDEN_FILES, false),
            isGridView = preferences.getBoolean(viewModePreferenceKey(sourceId, path), DEFAULT_GRID_VIEW),
        )
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            try {
                val (sourceName, location, entries) = when {
                    sourceId == LOCAL_SOURCE -> loadLocal(path)
                    sourceId == FREEBOX_SOURCE -> loadFreebox(path)
                    else -> loadServer(sourceId, path)
                }
                val showHiddenFiles = preferences.getBoolean(SHOW_HIDDEN_FILES, false)
                mutableState.value = mutableState.value.copy(
                    sourceName = sourceName,
                    location = location,
                    entries = entries.filter { entry ->
                        entry.name != "." && entry.name != ".." &&
                            (showHiddenFiles || !entry.name.startsWith('.'))
                    },
                    isLoading = false,
                    error = null,
                    localTreeUri = preferences.getString(LOCAL_TREE_URI, null),
                    localStorageAccessGranted = hasLocalStorageAccess(),
                    showHiddenFiles = showHiddenFiles,
                    isGridView = preferences.getBoolean(viewModePreferenceKey(sourceId, path), DEFAULT_GRID_VIEW),
                    folderCountRevision = mutableState.value.folderCountRevision + 1,
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                mutableState.value = mutableState.value.copy(
                    isLoading = false,
                    entries = emptyList(),
                    error = error.message ?: "Impossible de lire ce dossier.",
                    localTreeUri = preferences.getString(LOCAL_TREE_URI, null),
                    localStorageAccessGranted = hasLocalStorageAccess(),
                )
            }
        }
    }

    private suspend fun loadServer(connectionId: String, path: String): Triple<String, String, List<ServerBrowserEntry>> {
        val connection = serverRepository.connection(connectionId)
            ?: error("Ce serveur n’est plus enregistré.")
        val entries = serverRepository.browse(connectionId, path).map { it.toBrowserEntry() }
        val location = listOfNotNull(connection.address, path.takeIf(String::isNotBlank)).joinToString("/")
        return Triple(connection.name, location, entries)
    }

    private suspend fun loadFreebox(path: String): Triple<String, String, List<ServerBrowserEntry>> {
        val config = freeboxRepository.state.value
        if (config.address.isBlank()) error("Configurez d’abord la connexion SMB de la Freebox.")
        val freeboxEntries = freeboxRepository.listBrowserEntries(path)
        val currentContentIdsByPath = freeboxEntries.asSequence()
            .filterNot { it.isDirectory }
            .associate { entry ->
                entry.path to freeboxContentIdFor(
                    entry.path,
                    entry.sizeBytes,
                    entry.lastModifiedMs,
                    entry.fileId,
                )
            }
        try {
            watchProgressRepository.clearReplacedFreeboxProgress(currentContentIdsByPath)
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (error: Exception) {
            Log.w("ServerBrowser", "Could not remove progress for replaced Freebox files", error)
        }
        val entries = freeboxEntries.map { entry ->
            ServerBrowserEntry(
                name = entry.name,
                path = entry.path,
                isDirectory = entry.isDirectory,
                sizeBytes = entry.sizeBytes,
                lastModifiedMs = entry.lastModifiedMs,
                fileId = entry.fileId,
                mimeType = if (entry.isDirectory) null else entry.name.guessMimeType(),
                isPlayable = entry.isVideoFile,
            )
        }
        val location = listOfNotNull(config.address, path.takeIf(String::isNotBlank)).joinToString("/")
        return Triple("Freebox", location, entries)
    }

    private suspend fun loadLocal(documentId: String): Triple<String, String, List<ServerBrowserEntry>> =
        withContext(Dispatchers.IO) {
            if (documentId.startsWith(SHIZUKU_STORAGE_PATH_PREFIX)) {
                val path = documentId.removePrefix(SHIZUKU_STORAGE_PATH_PREFIX)
                if (isAmazonFireTvStick()) return@withContext loadMountedLocalStorage(path)
                return@withContext loadShizukuLocalStorage(
                    path,
                )
            }
            if (isAmazonFireTvStick() && documentId.startsWith(File.separator)) {
                return@withContext loadMountedLocalStorage(documentId)
            }
            val treeUri = preferences.getString(LOCAL_TREE_URI, null)?.let(Uri::parse)
            if (treeUri == null) {
                if (!hasLocalStorageAccess() && !isAmazonFireTvStick()) {
                    error("Autorisez JojoTV à parcourir le stockage local et USB pour commencer.")
                }
                return@withContext loadMountedLocalStorage(documentId)
            }
            val rootId = DocumentsContract.getTreeDocumentId(treeUri)
            val currentId = documentId.ifBlank { rootId }
            val localDirectory = localPathForDocumentId(currentId)
            val localRoots = mountedLocalStorageRoots()
            if (localDirectory != null && isShizukuStoragePath(localDirectory, localRoots)) {
                if (isAmazonFireTvStick()) {
                    return@withContext loadMountedLocalStorage(localDirectory.path)
                }
                return@withContext loadShizukuLocalStorage(localDirectory.path, localRoots)
            }
            val parentUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, currentId)
            val displayName = context.contentResolver.query(
                parentUri,
                arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME),
                null,
                null,
                null,
            )?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }.orEmpty().ifBlank { "Local / USB" }
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, currentId)
            val entries = mutableListOf<ServerBrowserEntry>()
            context.contentResolver.query(
                childrenUri,
                arrayOf(
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                    DocumentsContract.Document.COLUMN_MIME_TYPE,
                    DocumentsContract.Document.COLUMN_SIZE,
                ),
                null,
                null,
                null,
            )?.use { cursor ->
                val idIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val nameIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                val mimeIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
                val sizeIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_SIZE)
                while (cursor.moveToNext()) {
                    val id = cursor.getString(idIndex)
                    val name = cursor.getString(nameIndex).orEmpty()
                    val mimeType = cursor.getString(mimeIndex)
                    val isDirectory = mimeType == DocumentsContract.Document.MIME_TYPE_DIR
                    val fileUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, id)
                    entries += ServerBrowserEntry(
                        name = name,
                        path = id,
                        isDirectory = isDirectory,
                        sizeBytes = if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) cursor.getLong(sizeIndex) else 0L,
                        mimeType = if (isDirectory) null else name.guessMimeType(mimeType),
                        isPlayable = !isDirectory && (mimeType?.startsWith("video/") == true || name.isVideoFileName()),
                        uri = fileUri.toString(),
                    )
                }
            } ?: error("Le dossier sélectionné ne peut pas être lu.")
            Triple("Local / USB", displayName, entries.sortedWith(compareByDescending<ServerBrowserEntry> { it.isDirectory }.thenBy { it.name.lowercase() }))
        }

    private data class LocalStorageRoot(
        val directory: File,
        val label: String,
        val isPrimary: Boolean,
        val volumeId: String?,
    )

    private fun loadMountedLocalStorage(path: String): Triple<String, String, List<ServerBrowserEntry>> {
        val roots = mountedLocalStorageRoots()
        if (roots.isEmpty()) error("Aucun stockage local ou USB monté n’est accessible.")

        if (path.isBlank()) {
            val entries = roots.map { root ->
                ServerBrowserEntry(
                    name = root.label,
                    path = root.directory.canonicalPath,
                    isDirectory = true,
                )
            }
            return Triple("Local / USB", "Stockages disponibles", entries)
        }

        val currentDirectory = File(path).canonicalFile
        val root = roots.firstOrNull { isWithinStorageRoot(currentDirectory, it.directory) }
            ?: error("Ce dossier n’appartient pas à un stockage monté.")
        if (isShizukuStoragePath(currentDirectory, roots) && !isAmazonFireTvStick()) {
            return loadShizukuLocalStorage(currentDirectory.path, roots)
        }
        if (!currentDirectory.isDirectory || !currentDirectory.canRead()) {
            error("Ce dossier ne peut pas être lu.")
        }
        val children = currentDirectory.listFiles() ?: error("Impossible de lire le contenu de ce dossier.")
        val entries = children.mapNotNull { child ->
            val canonicalChild = runCatching { child.canonicalFile }.getOrNull() ?: return@mapNotNull null
            if (!isWithinStorageRoot(canonicalChild, root.directory) || !canonicalChild.canRead()) {
                return@mapNotNull null
            }
            val isDirectory = canonicalChild.isDirectory
            ServerBrowserEntry(
                name = canonicalChild.name,
                path = canonicalChild.path,
                isDirectory = isDirectory,
                sizeBytes = if (isDirectory) 0L else canonicalChild.length(),
                mimeType = if (isDirectory) null else canonicalChild.name.guessMimeType(),
                isPlayable = !isDirectory && canonicalChild.name.isVideoFileName(),
                uri = if (isDirectory) null else Uri.fromFile(canonicalChild).toString(),
                lastModifiedMs = canonicalChild.lastModified(),
            )
        }.sortedWith(compareByDescending<ServerBrowserEntry> { it.isDirectory }.thenBy { it.name.lowercase() })
        val relativePath = runCatching { currentDirectory.relativeTo(root.directory).path }
            .getOrDefault("")
            .takeUnless { it == "." }
            .orEmpty()
        val location = listOf(root.label, relativePath).filter(String::isNotBlank).joinToString("/")
        return Triple("Local / USB", location, entries)
    }

    private fun loadShizukuLocalStorage(
        path: String,
        roots: List<LocalStorageRoot> = mountedLocalStorageRoots(),
    ): Triple<String, String, List<ServerBrowserEntry>> {
        if (roots.isEmpty()) error("Aucun stockage local ou USB monté n’est accessible.")
        val currentDirectory = runCatching { File(path).canonicalFile }.getOrNull()
            ?: error("Chemin local invalide.")
        val root = roots.firstOrNull { isWithinStorageRoot(currentDirectory, it.directory) }
            ?: error("Ce dossier n’appartient pas à un stockage monté.")
        if (!isShizukuStoragePath(currentDirectory, roots)) {
            error("Le parcours Shizuku est réservé au dossier Android et au dossier data de la racine.")
        }

        val entries = ShizukuStorageBrowser.list(currentDirectory.path).map { child ->
            ServerBrowserEntry(
                name = child.name,
                path = SHIZUKU_STORAGE_PATH_PREFIX + child.path,
                isDirectory = child.isDirectory,
                sizeBytes = child.sizeBytes,
                mimeType = if (child.isDirectory) null else child.name.guessMimeType(),
                isPlayable = !child.isDirectory && child.name.isVideoFileName(),
            )
        }
        val relativePath = runCatching { currentDirectory.relativeTo(root.directory).path }
            .getOrDefault("")
            .takeUnless { it == "." }
            .orEmpty()
        val location = listOf(root.label, relativePath).filter(String::isNotBlank).joinToString("/")
        return Triple("Local / USB", location, entries)
    }

    private fun canonicalMountedShizukuPath(path: String): File {
        val canonicalPath = runCatching { File(path).canonicalFile }.getOrNull()
            ?: throw IOException("Chemin local invalide.")
        val roots = mountedLocalStorageRoots()
        if (roots.none { isWithinStorageRoot(canonicalPath, it.directory) } ||
            !isShizukuStoragePath(canonicalPath, roots)
        ) {
            throw IOException("Ce fichier est hors des dossiers autorisés au parcours Shizuku.")
        }
        return canonicalPath
    }

    private fun isShizukuStoragePath(directory: File, roots: List<LocalStorageRoot>): Boolean {
        val root = roots.firstOrNull { isWithinStorageRoot(directory, it.directory) } ?: return false
        val relativePath = runCatching { directory.relativeTo(root.directory).path }.getOrDefault("")
        val segments = relativePath.split(File.separatorChar).filter(String::isNotBlank)
        return segments.any { it.equals("Android", ignoreCase = true) } ||
            segments.firstOrNull()?.equals("data", ignoreCase = true) == true
    }

    private fun isAmazonFireTvStick(): Boolean =
        Build.MANUFACTURER.equals("Amazon", ignoreCase = true) &&
            (Build.MODEL.startsWith("AFT", ignoreCase = true) ||
                Build.DEVICE.contains("AFT", ignoreCase = true))

    private fun localPathForDocumentId(documentId: String): File? {
        val separator = documentId.indexOf(':')
        if (separator <= 0) return null
        val volumeId = documentId.substring(0, separator)
        val relativePath = documentId.substring(separator + 1).trimStart('/', File.separatorChar)
        val root = mountedLocalStorageRoots().firstOrNull { candidate ->
            candidate.volumeId.equals(volumeId, ignoreCase = true) ||
                (volumeId.equals("primary", ignoreCase = true) && candidate.isPrimary)
        } ?: return null
        val directory = runCatching { File(root.directory, relativePath).canonicalFile }.getOrNull() ?: return null
        return directory.takeIf { isWithinStorageRoot(it, root.directory) }
    }

    private fun mountedLocalStorageRoots(): List<LocalStorageRoot> {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as? StorageManager
        val volumes = runCatching { storageManager?.storageVolumes.orEmpty() }.getOrDefault(emptyList())
        val roots = volumes.mapNotNull { volume ->
            if (volume.state != Environment.MEDIA_MOUNTED || (!volume.isPrimary && !volume.isRemovable)) {
                return@mapNotNull null
            }
            val directory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                runCatching { volume.directory }.getOrNull()
            } else {
                legacyStorageRoot(storageManager, volume)
            }?.let { runCatching { it.canonicalFile }.getOrNull() } ?: return@mapNotNull null
            if (!directory.isDirectory || !directory.canRead()) return@mapNotNull null
            val label = if (volume.isPrimary) {
                "Stockage interne"
            } else {
                runCatching { volume.getDescription(context) }.getOrNull()?.trim()?.takeIf(String::isNotBlank)
                    ?: "Stockage USB"
            }
            LocalStorageRoot(directory, label, volume.isPrimary, volume.uuid ?: if (volume.isPrimary) "primary" else null)
        }

        val withPrimaryFallback = if (roots.none(LocalStorageRoot::isPrimary)) {
            val primary = runCatching { Environment.getExternalStorageDirectory().canonicalFile }.getOrNull()
            if (primary != null && primary.isDirectory && primary.canRead()) {
                roots + LocalStorageRoot(primary, "Stockage interne", true, "primary")
            } else {
                roots
            }
        } else {
            roots
        }
        return withPrimaryFallback
            .distinctBy { it.directory.path }
            .sortedWith(compareByDescending<LocalStorageRoot> { it.isPrimary }.thenBy { it.label.lowercase() })
    }

    private fun legacyStorageRoot(storageManager: StorageManager?, volume: android.os.storage.StorageVolume): File? {
        val appDirectory = context.getExternalFilesDirs(null)
            .filterNotNull()
            .firstOrNull { directory ->
                runCatching {
                    val mountedVolume = storageManager?.getStorageVolume(directory)
                    mountedVolume?.uuid == volume.uuid && mountedVolume?.isPrimary == volume.isPrimary
                }.getOrDefault(false)
            }
        val rootPath = appDirectory?.absolutePath
            ?.substringBefore("${File.separator}Android${File.separator}data${File.separator}")
            ?.takeIf(String::isNotBlank)
        if (rootPath != null) return File(rootPath)
        return Environment.getExternalStorageDirectory().takeIf { volume.isPrimary }
    }

    private fun isWithinStorageRoot(file: File, root: File): Boolean {
        val rootPath = root.canonicalPath
        val filePath = file.canonicalPath
        return filePath == rootPath || filePath.startsWith(
            if (rootPath.endsWith(File.separator)) rootPath else "$rootPath${File.separator}",
        )
    }

    private fun ServerBrowseEntry.toBrowserEntry() = ServerBrowserEntry(
        name = name,
        path = path,
        isDirectory = isDirectory,
        sizeBytes = sizeBytes,
        mimeType = if (isDirectory) null else name.guessMimeType(mimeType),
        isPlayable = isPlayable,
    )

    private fun String.isVideoFileName(): Boolean = substringAfterLast('.', "").lowercase() in VIDEO_EXTENSIONS

    private fun String.guessMimeType(existing: String? = null): String? {
        val extension = substringAfterLast('.', "").lowercase()
        val known = when (extension) {
            "mkv", "mp4", "m4v" -> "video/mp4"
            "avi" -> "video/x-msvideo"
            "mov" -> "video/quicktime"
            "ts", "m2ts" -> "video/mp2t"
            "webm" -> "video/webm"
            "mpg", "mpeg" -> "video/mpeg"
            "apk" -> "application/vnd.android.package-archive"
            "pdf" -> "application/pdf"
            "txt", "tct", "text", "log", "ini", "conf", "cfg" -> "text/plain"
            "md" -> "text/markdown"
            "rtf" -> "application/rtf"
            "csv" -> "text/csv"
            "tsv" -> "text/tab-separated-values"
            "json" -> "application/json"
            "xml" -> "application/xml"
            "html", "htm" -> "text/html"
            "yaml", "yml" -> "application/yaml"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "odt" -> "application/vnd.oasis.opendocument.text"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "ods" -> "application/vnd.oasis.opendocument.spreadsheet"
            "ppt" -> "application/vnd.ms-powerpoint"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            "odp" -> "application/vnd.oasis.opendocument.presentation"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "bmp" -> "image/bmp"
            "heic" -> "image/heic"
            "heif" -> "image/heif"
            "tif", "tiff" -> "image/tiff"
            "svg" -> "image/svg+xml"
            "mp3" -> "audio/mpeg"
            "aac" -> "audio/aac"
            "flac" -> "audio/flac"
            "wav" -> "audio/wav"
            "m4a" -> "audio/mp4"
            "ogg", "opus" -> "audio/ogg"
            "wma" -> "audio/x-ms-wma"
            "zip" -> "application/zip"
            "rar" -> "application/vnd.rar"
            "7z" -> "application/x-7z-compressed"
            "m3u" -> "audio/x-mpegurl"
            "m3u8" -> "application/vnd.apple.mpegurl"
            "pls" -> "audio/x-scpls"
            else -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return known ?: existing?.takeIf(String::isNotBlank)
    }

    private fun thumbnailCacheKey(sourceId: String, path: String, sizeBytes: Long): String =
        MessageDigest.getInstance("SHA-256")
            .digest("$sourceId\u0000$path\u0000$sizeBytes".toByteArray())
            .take(16)
            .joinToString("") { byte -> "%02x".format(byte.toInt() and 0xff) }

    private fun customArtworkKey(sourceId: String, path: String): String =
        "custom_artwork_${thumbnailCacheKey(sourceId, path, 0L)}"

    private fun trimThumbnailCache(directory: File) {
        val directoryKey = directory.absolutePath
        val now = System.currentTimeMillis()
        val shouldTrim = synchronized(thumbnailCacheTrimLock) {
            val lastTrimAt = lastThumbnailCacheTrimAtMs[directoryKey] ?: 0L
            if (now - lastTrimAt < THUMBNAIL_CACHE_TRIM_INTERVAL_MS) {
                false
            } else {
                lastThumbnailCacheTrimAtMs[directoryKey] = now
                true
            }
        }
        if (!shouldTrim) return

        val files = directory.listFiles()?.filter { it.isFile && !it.name.endsWith(".part") }.orEmpty()
        val staleBefore = System.currentTimeMillis() - THUMBNAIL_MAX_AGE_MS
        val retainedFiles = files.filter { file ->
            file.lastModified() >= staleBefore || !file.delete()
        }
        var totalBytes = retainedFiles.sumOf(File::length)
        if (totalBytes <= THUMBNAIL_CACHE_MAX_BYTES) return

        for (file in retainedFiles.sortedBy(File::lastModified)) {
            if (totalBytes <= THUMBNAIL_CACHE_MAX_BYTES) break
            val size = file.length()
            if (file.delete()) totalBytes -= size
        }
    }

    companion object {
        const val LOCAL_SOURCE = "local"
        const val FREEBOX_SOURCE = "freebox"
        private const val PREFERENCES = "server_file_browser"
        private const val LOCAL_TREE_URI = "local_tree_uri"
        private const val SHOW_HIDDEN_FILES = "show_hidden_files"
        private const val CLIPBOARD_ENTRIES = "clipboard_entries"
        private const val CLIPBOARD_IS_CUT = "clipboard_is_cut"
        private const val DEFAULT_GRID_VIEW = true
        private const val FILE_CACHE_MAX_AGE_MS = 3L * 24L * 60L * 60L * 1000L
        private const val THUMBNAIL_CACHE_MAX_BYTES = 64L * 1024L * 1024L
        private const val THUMBNAIL_MAX_AGE_MS = 7L * 24L * 60L * 60L * 1000L
        private const val THUMBNAIL_CACHE_TRIM_INTERVAL_MS = 30_000L
        private const val FILE_COPY_ATTEMPTS = 2
        private const val FILE_COPY_RETRY_DELAY_MS = 350L
        private val VIDEO_EXTENSIONS = setOf("mkv", "mp4", "avi", "mov", "m4v", "ts", "webm", "mpg", "mpeg", "m2ts")

        private fun viewModePreferenceKey(sourceId: String, path: String): String =
            "grid_view_${sourceId}_$path"
    }
}
