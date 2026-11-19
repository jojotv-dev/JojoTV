package com.nuvio.tv.data.mediaserver.smb

import android.net.Uri
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
import com.hierynomus.smbj.session.Session
import com.nuvio.tv.core.tracking.TrackingExternalIds
import com.nuvio.tv.data.mediaserver.ServerCandidate
import com.nuvio.tv.data.mediaserver.ServerCapability
import com.nuvio.tv.data.mediaserver.ServerException
import com.nuvio.tv.data.mediaserver.ServerFailure
import com.nuvio.tv.data.mediaserver.ServerItemDetails
import com.nuvio.tv.data.mediaserver.ServerItemRef
import com.nuvio.tv.data.mediaserver.ServerLibrary
import com.nuvio.tv.data.mediaserver.ServerMediaKind
import com.nuvio.tv.data.mediaserver.ServerPage
import com.nuvio.tv.data.mediaserver.ServerPlaybackEvent
import com.nuvio.tv.data.mediaserver.ServerPlaybackEventType
import com.nuvio.tv.data.mediaserver.ServerPlaybackRequest
import com.nuvio.tv.data.mediaserver.ServerPlaybackSession
import com.nuvio.tv.data.mediaserver.ServerPlayMethod
import com.nuvio.tv.data.mediaserver.ServerBrowseEntry
import com.nuvio.tv.data.mediaserver.ServerProvider
import com.nuvio.tv.data.mediaserver.ServerSession
import com.nuvio.tv.data.mediaserver.ServerSignIn
import com.nuvio.tv.data.mediaserver.ServerTitle
import com.nuvio.tv.data.storage.SmbStorageCapacity
import com.nuvio.tv.data.storage.StorageCapacity
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.Meta
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.PosterShape
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** SMB is a file server, so each share is exposed as one browsable library. */
internal class SmbProvider : ServerProvider {
    override val id: String = "smb"
    override val displayName: String = "SMB"
    override val capabilities = emptySet<ServerCapability>()
    override val minimumVersion: String = "SMB 2.0"

    private val playbackProxies = ConcurrentHashMap<String, SmbPlaybackProxy>()

    override suspend fun signIn(address: String, username: String, password: String): ServerSignIn =
        withContext(Dispatchers.IO) {
            val target = try {
                SmbAddress.parse(address)
            } catch (error: Exception) {
                throw ServerException(ServerFailure.NOT_FOUND, error.message)
            }
            val normalizedUser = username.trim()
            try {
                SmbShareSession.open(target, normalizedUser, password).use { it.list("") }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                throw error.toServerException()
            }
            val pathName = target.pathSegments.lastOrNull()
            ServerSignIn(
                address = target.toUrl(),
                serverName = pathName ?: target.share,
                serverId = target.identity,
                userId = normalizedUser.ifBlank { "guest" },
                userName = normalizedUser.ifBlank { "Guest" },
                token = password,
            )
        }

    override suspend fun libraries(session: ServerSession): List<ServerLibrary> = withContext(Dispatchers.IO) {
        withShare(session) { share, target ->
            share.list(target.sharePath(""))
            listOf(ServerLibrary(id = SMB_LIBRARY_ID, name = target.pathSegments.lastOrNull() ?: target.share, kind = ServerMediaKind.MIXED))
        }
    }

    override suspend fun browse(session: ServerSession, relativePath: String): List<ServerBrowseEntry> =
        withContext(Dispatchers.IO) {
            withShare(session) { share, target ->
                listEntries(share, target, relativePath, includeAllFiles = true).map { entry ->
                    ServerBrowseEntry(
                        name = entry.name,
                        path = entry.path,
                        isDirectory = entry.isDirectory,
                        sizeBytes = entry.sizeBytes,
                        mimeType = if (entry.isDirectory) null else entry.name.mimeType(),
                        isPlayable = !entry.isDirectory && entry.isVideo,
                    )
                }
            }
        }

    override suspend fun storageCapacity(session: ServerSession): StorageCapacity? =
        SmbStorageCapacity.read(
            address = session.connection.address,
            username = session.connection.userName,
            password = session.token,
        )

    override suspend fun downloadFile(
        session: ServerSession,
        relativePath: String,
        destination: File,
        onProgress: (copiedBytes: Long, totalBytes: Long) -> Unit,
    ) =
        withContext(Dispatchers.IO) {
            withShare(session) { share, target ->
                val safePath = normalizeRelativePath(relativePath)
                val remote = share.openFile(
                    target.sharePath(safePath),
                    setOf(AccessMask.FILE_READ_DATA, AccessMask.FILE_READ_ATTRIBUTES),
                    setOf(FileAttributes.FILE_ATTRIBUTE_NORMAL),
                    SMB2ShareAccess.ALL,
                    SMB2CreateDisposition.FILE_OPEN,
                    setOf(SMB2CreateOptions.FILE_NON_DIRECTORY_FILE, SMB2CreateOptions.FILE_RANDOM_ACCESS),
                )
                remote.use { source ->
                    FileOutputStream(destination).buffered().use { output ->
                        val buffer = ByteArray(256 * 1024)
                        val totalBytes = source.length
                        var offset = 0L
                        onProgress(0L, totalBytes)
                        while (offset < totalBytes) {
                            val requested = minOf(buffer.size.toLong(), totalBytes - offset).toInt()
                            val count = source.read(buffer, offset, 0, requested)
                            if (count <= 0) throw IOException("Le fichier SMB a été interrompu pendant sa copie.")
                            output.write(buffer, 0, count)
                            offset += count
                            onProgress(offset, totalBytes)
                        }
                    }
                }
            }
        }

    override suspend fun createBrowserDirectory(session: ServerSession, relativePath: String) =
        withContext(Dispatchers.IO) {
            withShare(session) { share, target ->
                val safePath = normalizeRelativePath(relativePath)
                require(safePath.isNotBlank()) { "Impossible de créer un dossier à la racine du partage." }
                share.mkdir(target.sharePath(safePath))
            }
        }

    override suspend fun renameBrowserEntry(
        session: ServerSession,
        relativePath: String,
        newName: String,
        isDirectory: Boolean,
    ) = withContext(Dispatchers.IO) {
        withShare(session) { share, target ->
            val safePath = normalizeRelativePath(relativePath)
            val cleanName = newName.trim()
            require(cleanName.isNotBlank() && cleanName !in setOf(".", "..") && '/' !in cleanName && '\\' !in cleanName) {
                "Le nom de fichier n’est pas valide."
            }
            val parent = safePath.substringBeforeLast('/', "")
            val targetPath = listOf(parent, cleanName).filter(String::isNotBlank).joinToString("/")
            val oldSmbPath = target.sharePath(safePath)
            val newSmbPath = target.sharePath(targetPath)
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

    override suspend fun deleteBrowserEntry(session: ServerSession, relativePath: String, isDirectory: Boolean) =
        withContext(Dispatchers.IO) {
            withShare(session) { share, target ->
                val safePath = normalizeRelativePath(relativePath)
                require(safePath.isNotBlank()) { "Le partage SMB ne peut pas être supprimé." }
                if (isDirectory) share.rmdir(target.sharePath(safePath), true)
                else share.rm(target.sharePath(safePath))
            }
        }

    override suspend fun uploadBrowserFile(session: ServerSession, relativePath: String, source: File) =
        withContext(Dispatchers.IO) {
            require(source.isFile) { "Le fichier local à envoyer est introuvable." }
            withShare(session) { share, target ->
                val safePath = normalizeRelativePath(relativePath)
                require(safePath.isNotBlank()) { "Le chemin du fichier de destination est invalide." }
                share.openFile(
                    target.sharePath(safePath),
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

    override suspend fun libraryPage(
        session: ServerSession,
        library: ServerLibrary,
        start: Int,
        limit: Int,
    ): ServerPage<ServerTitle> = withContext(Dispatchers.IO) {
        if (library.id != SMB_LIBRARY_ID) throw ServerException(ServerFailure.NOT_FOUND)
        val entries = withShare(session) { share, target -> listEntries(share, target, "") }
        page(entries, start, limit) { entry -> title(session.connection.id, entry) }
    }

    override suspend fun collectionPage(
        session: ServerSession,
        collectionId: String,
        start: Int,
        limit: Int,
    ): ServerPage<ServerTitle> = withContext(Dispatchers.IO) {
        val item = parseItemId(collectionId)
        if (item.kind != DIRECTORY_KIND) throw ServerException(ServerFailure.NOT_FOUND)
        val entries = withShare(session) { share, target -> listEntries(share, target, item.path) }
        page(entries, start, limit) { entry -> title(session.connection.id, entry) }
    }

    override suspend fun details(session: ServerSession, itemId: String): ServerItemDetails = withContext(Dispatchers.IO) {
        val item = parseItemId(itemId)
        val name = item.path.substringAfterLast('/').ifBlank { item.path }
        val isDirectory = item.kind == DIRECTORY_KIND
        val displayName = if (isDirectory) name else name.substringBeforeLast('.', name)
        val type = if (isDirectory) ContentType.UNKNOWN else ContentType.MOVIE
        val rawType = if (isDirectory) ServerMediaKind.COLLECTION.contentType else ServerMediaKind.MOVIE.contentType
        val ref = ServerItemRef(session.connection.id, itemId).encode()
        ServerItemDetails(
            meta = Meta(
                id = ref,
                type = type,
                rawType = rawType,
                name = displayName,
                poster = null,
                posterShape = PosterShape.POSTER,
                background = null,
                logo = null,
                description = item.path,
                releaseInfo = null,
                imdbRating = null,
                genres = emptyList(),
                runtime = null,
                director = emptyList(),
                cast = emptyList(),
                videos = emptyList(),
                country = null,
                awards = null,
                language = null,
                links = emptyList(),
            ),
            externalIds = TrackingExternalIds(),
        )
    }

    override suspend fun candidates(session: ServerSession, itemId: String): List<ServerCandidate> = withContext(Dispatchers.IO) {
        val item = parseItemId(itemId)
        if (item.kind != FILE_KIND || !item.path.isVideoPath()) return@withContext emptyList()
        val filename = item.path.substringAfterLast('/')
        listOf(
            ServerCandidate(
                target = com.nuvio.tv.data.mediaserver.ServerPlaybackTarget(
                    item = ServerItemRef(session.connection.id, itemId),
                    mediaSourceId = itemId,
                ),
                title = filename.substringBeforeLast('.', filename),
                description = item.path,
                filename = filename,
                sizeBytes = null,
            ),
        )
    }

    override suspend fun preparePlayback(session: ServerSession, request: ServerPlaybackRequest): ServerPlaybackSession =
        withContext(Dispatchers.IO) {
            val item = parseItemId(request.target.item.itemId)
            if (item.kind != FILE_KIND || !item.path.isVideoPath()) throw ServerException(ServerFailure.NOT_FOUND)
            val proxy = try {
                SmbPlaybackProxy(
                    connectionId = session.connection.id,
                    address = session.connection.address,
                    username = session.connection.userName,
                    password = session.token,
                    relativePath = item.path,
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                throw error.toServerException()
            }
            playbackProxies[proxy.url] = proxy
            ServerPlaybackSession(
                target = request.target,
                mediaSourceId = request.target.mediaSourceId ?: item.path,
                url = proxy.url,
                headers = emptyMap(),
                subtitles = emptyList(),
                playSessionId = null,
                playMethod = ServerPlayMethod.DIRECT_PLAY,
            )
        }

    override suspend fun report(session: ServerSession, playback: ServerPlaybackSession, event: ServerPlaybackEvent) {
        if (event.type == ServerPlaybackEventType.STOP) playbackProxies.remove(playback.url)?.close()
    }

    override suspend fun signOut(session: ServerSession) {
        playbackProxies.forEach { (url, proxy) ->
            if (proxy.connectionId == session.connection.id && playbackProxies.remove(url, proxy)) {
                proxy.close()
            }
        }
    }

    private suspend fun <T> withShare(session: ServerSession, block: (DiskShare, SmbAddress) -> T): T =
        withContext(Dispatchers.IO) {
            try {
                val target = SmbAddress.parse(session.connection.address)
                SmbShareSession.open(target, session.connection.userName, session.token).use { smb ->
                    block(smb.share, target)
                }
            } catch (error: ServerException) {
                throw error
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                throw error.toServerException()
            }
        }

    private fun listEntries(
        share: DiskShare,
        target: SmbAddress,
        relativePath: String,
        includeAllFiles: Boolean = false,
    ): List<SmbEntry> {
        val safePath = normalizeRelativePath(relativePath)
        return share.list(target.sharePath(safePath)).asSequence()
            .filter { it.fileName != "." && it.fileName != ".." }
            .map { info ->
                val directory = info.fileAttributes and FileAttributes.FILE_ATTRIBUTE_DIRECTORY.value != 0L
                val path = listOf(safePath, info.fileName).filter(String::isNotBlank).joinToString("/")
                SmbEntry(info.fileName, path, directory, info.endOfFile)
            }
            .filter { includeAllFiles || it.isDirectory || it.isVideo }
            .sortedWith(compareByDescending<SmbEntry> { it.isDirectory }.thenBy { it.name.lowercase() })
            .toList()
    }

    private fun title(connectionId: String, entry: SmbEntry): ServerTitle {
        val itemId = (if (entry.isDirectory) DIRECTORY_KIND else FILE_KIND) + ":" + entry.path
        val type = if (entry.isDirectory) ContentType.UNKNOWN else ContentType.MOVIE
        val rawType = if (entry.isDirectory) ServerMediaKind.COLLECTION.contentType else ServerMediaKind.MOVIE.contentType
        return ServerTitle(
            preview = MetaPreview(
                id = ServerItemRef(connectionId, itemId).encode(),
                type = type,
                rawType = rawType,
                name = if (entry.isDirectory) entry.name else entry.name.substringBeforeLast('.', entry.name),
                poster = null,
                posterShape = PosterShape.POSTER,
                background = null,
                logo = null,
                description = entry.path,
                releaseInfo = null,
                imdbRating = null,
                genres = emptyList(),
            ),
        )
    }

    private fun <T> page(entries: List<SmbEntry>, start: Int, limit: Int, transform: (SmbEntry) -> T): ServerPage<T> {
        val safeStart = start.coerceIn(0, entries.size)
        return ServerPage(entries.drop(safeStart).take(limit.coerceAtLeast(0)).map(transform), entries.size)
    }

    private fun Throwable.toServerException(): ServerException {
        val message = generateSequence(this) { it.cause }.mapNotNull { it.message }.joinToString(" ")
        val normalized = message.uppercase()
        val failure = when {
            listOf("LOGON_FAILURE", "ACCESS_DENIED", "WRONG_PASSWORD", "AUTHENTICATION").any(normalized::contains) ->
                ServerFailure.AUTH_REQUIRED
            listOf("BAD_NETWORK_NAME", "OBJECT_NAME_NOT_FOUND", "PATH_NOT_FOUND", "NO SUCH FILE").any(normalized::contains) ->
                ServerFailure.NOT_FOUND
            else -> ServerFailure.UNREACHABLE
        }
        return ServerException(failure, message.ifBlank { failure.name })
    }

    private fun parseItemId(itemId: String): SmbItemId {
        val separator = itemId.indexOf(':')
        if (separator <= 0 || separator == itemId.lastIndex) throw ServerException(ServerFailure.NOT_FOUND)
        val kind = itemId.substring(0, separator)
        if (kind != DIRECTORY_KIND && kind != FILE_KIND) throw ServerException(ServerFailure.NOT_FOUND)
        return SmbItemId(kind, normalizeRelativePath(itemId.substring(separator + 1)))
    }

    private fun normalizeRelativePath(raw: String): String {
        val parts = raw.replace('\\', '/').split('/').filter(String::isNotBlank)
        if (parts.any { it == "." || it == ".." }) throw ServerException(ServerFailure.NOT_FOUND)
        return parts.joinToString("/")
    }

    private fun String.isVideoPath(): Boolean = substringAfterLast('.', "").lowercase() in VIDEO_EXTENSIONS

    private data class SmbItemId(val kind: String, val path: String)

    private data class SmbEntry(val name: String, val path: String, val isDirectory: Boolean, val sizeBytes: Long) {
        val isVideo: Boolean get() = !isDirectory &&
            name.substringAfterLast('.', "").lowercase() in VIDEO_EXTENSIONS
    }

    private fun String.isVideoName(): Boolean = substringAfterLast('.', "").lowercase() in VIDEO_EXTENSIONS

    private fun String.mimeType(): String? = when (substringAfterLast('.', "").lowercase()) {
        "mkv", "mp4", "m4v" -> "video/mp4"
        "avi" -> "video/x-msvideo"
        "mov" -> "video/quicktime"
        "ts", "m2ts" -> "video/mp2t"
        "webm" -> "video/webm"
        "mpg", "mpeg" -> "video/mpeg"
        "pdf" -> "application/pdf"
        "apk" -> "application/vnd.android.package-archive"
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "txt" -> "text/plain"
        else -> null
    }

    companion object {
        private const val SMB_LIBRARY_ID = "share-root"
        private const val DIRECTORY_KIND = "dir"
        private const val FILE_KIND = "file"
        private val VIDEO_EXTENSIONS = setOf("mkv", "mp4", "avi", "mov", "m4v", "ts", "webm", "mpg", "mpeg", "m2ts")
    }
}

internal data class SmbAddress(
    val host: String,
    val share: String,
    val pathSegments: List<String>,
) {
    val identity: String get() = listOf(host, share, pathSegments.joinToString("/")).joinToString("/").lowercase()

    fun toUrl(): String = "smb://${urlHost(host)}/" + (listOf(share) + pathSegments).joinToString("/") { Uri.encode(it) }

    fun sharePath(relativePath: String): String =
        (pathSegments + relativePath.replace('\\', '/').split('/').filter(String::isNotBlank)).joinToString("\\")

    companion object {
        fun parse(raw: String): SmbAddress {
            val normalized = raw.trim().replace('\\', '/')
            val withScheme = if (normalized.startsWith("smb://", ignoreCase = true)) normalized else "smb://$normalized"
            val body = withScheme.replaceFirst(Regex("^smb://", RegexOption.IGNORE_CASE), "")
            val parts = body.split('/').filter(String::isNotBlank)
            require(parts.size >= 2) { "Adresse requise au format smb://serveur/partage." }
            val authority = parts.first()
            require('@' !in authority) { "Saisissez l’utilisateur et le mot de passe dans leurs champs dédiés." }
            val host = when {
                authority.startsWith("[") && "]" in authority -> authority.substringAfter('[').substringBefore(']')
                else -> authority.substringBefore(':')
            }
            val port = when {
                authority.startsWith("[") && "]:" in authority -> authority.substringAfter("]:")
                !authority.startsWith("[") && ':' in authority -> authority.substringAfter(':')
                else -> ""
            }
            require(host.isNotBlank()) { "Adresse SMB invalide." }
            require(port.isBlank() || port == "445") { "Utilisez le port SMB standard 445." }
            val decoded = parts.drop(1).map(Uri::decode)
            return SmbAddress(host, decoded.first(), decoded.drop(1))
        }

        private fun urlHost(host: String): String = if (':' in host) "[$host]" else host
    }
}

/** Authenticated share wrapper shared by browsing, probing, and metadata reads. */
internal class SmbShareSession private constructor(
    private val target: SmbAddress,
    username: String,
    password: String,
) : Closeable {
    private val client = SMBClient(
        SmbConfig.builder()
            .withTimeout(SMB_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .withSoTimeout(SMB_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build(),
    )
    private var connection: Connection? = null
    private var session: Session? = null
    private var connectedShare: DiskShare? = null

    val share: DiskShare
        get() = connectedShare ?: throw ServerException(ServerFailure.UNREACHABLE)

    init {
        try {
            connection = client.connect(target.host)
            val domainSeparator = username.indexOf('\\')
            val user = if (domainSeparator > 0) username.substring(domainSeparator + 1) else username
            val domain = if (domainSeparator > 0) username.substring(0, domainSeparator) else null
            session = connection!!.authenticate(AuthenticationContext(user, password.toCharArray(), domain))
            connectedShare = session!!.connectShare(target.share) as? DiskShare
                ?: throw ServerException(ServerFailure.NOT_FOUND, "Le partage SMB demandé n’est pas un dossier.")
        } catch (error: Exception) {
            close()
            throw error
        }
    }

    fun list(relativePath: String) = share.list(target.sharePath(relativePath))

    fun openFile(relativePath: String) = share.openFile(
        target.sharePath(relativePath),
        setOf(AccessMask.FILE_READ_DATA, AccessMask.FILE_READ_ATTRIBUTES),
        setOf(FileAttributes.FILE_ATTRIBUTE_NORMAL),
        SMB2ShareAccess.ALL,
        SMB2CreateDisposition.FILE_OPEN,
        setOf(SMB2CreateOptions.FILE_NON_DIRECTORY_FILE, SMB2CreateOptions.FILE_RANDOM_ACCESS),
    )

    override fun close() {
        runCatching { connectedShare?.close() }
        connectedShare = null
        runCatching { session?.close() }
        session = null
        runCatching { connection?.close() }
        connection = null
        runCatching { client.close() }
    }

    companion object {
        private const val SMB_TIMEOUT_SECONDS = 20L

        fun open(address: SmbAddress, username: String, password: String): SmbShareSession =
            SmbShareSession(address, username, password)
    }
}
