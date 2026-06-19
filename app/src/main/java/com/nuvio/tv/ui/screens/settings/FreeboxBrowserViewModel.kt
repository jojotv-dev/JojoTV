package com.nuvio.tv.ui.screens.settings

import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.core.tmdb.TmdbService
import com.nuvio.tv.core.tmdb.FreeboxVideoMeta
import com.nuvio.tv.data.freebox.FreeboxFileEntry
import com.nuvio.tv.data.freebox.FreeboxOsClient
import com.nuvio.tv.data.freebox.freeboxContentIdForEntry
import com.nuvio.tv.data.freebox.freeboxContentIdForPath
import com.nuvio.tv.data.freebox.freeboxDisplayName
import com.nuvio.tv.data.freebox.freeboxFileNameOnly
import com.nuvio.tv.data.freebox.freeboxPathFromContentId
import com.nuvio.tv.data.freebox.freeboxTmdbSearchQuery
import com.nuvio.tv.data.local.FreeboxConnectionSettings
import com.nuvio.tv.data.local.FreeboxSettingsDataStore
import com.nuvio.tv.data.local.browserSortModeFor
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.domain.repository.WatchProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

data class FreeboxBrowserUiState(
    val entries: List<FreeboxFileEntry> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val knownPositions: Map<String, Long> = emptyMap(),
    val currentPath: String = "",
    val breadcrumb: List<String> = emptyList(),
    val hasSession: Boolean = false,
    val isAtRoot: Boolean = true,
    val knownVideoDurations: Map<String, Long> = emptyMap(),
    val videoArtwork: Map<String, String> = emptyMap(),
    val sortMode: String = "NAME_ASC",
    val viewMode: String = "LIST",
    val videoMetadata: Map<String, com.nuvio.tv.core.tmdb.FreeboxVideoMeta> = emptyMap(),
    val showExtensions: Boolean = false,
    val showHiddenFiles: Boolean = false
)
data class FreeboxPlaybackRequest(
    val streamUrl: String,
    val title: String,
    val headers: Map<String, String>,
    val videoId: String,
    val videoSize: Long?,
    val durationMs: Long?,
    val artworkUrl: String? = null
)

data class FreeboxPhotoRequest(
    val photoUrl: String,
    val title: String,
    val headers: Map<String, String>,
    val photoId: String
)

data class FreeboxFileOpenRequest(
    val file: File,
    val title: String,
    val mimeType: String,
    val isApk: Boolean
)

@HiltViewModel
class FreeboxBrowserViewModel @Inject constructor(
    private val dataStore: FreeboxSettingsDataStore,
    private val freeboxClient: FreeboxOsClient,
    private val watchProgressRepository: WatchProgressRepository,
    private val tmdbService: TmdbService,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(FreeboxBrowserUiState())
    val uiState: StateFlow<FreeboxBrowserUiState> = _uiState.asStateFlow()

    private var liveSessionToken: String? = null
    private var rootFolderOverrideRequested = false
    private val navStack = ArrayDeque<Pair<String, String>>()
    private val directVideoDurations = mutableMapOf<String, Long>()
    private val progressVideoDurations = mutableMapOf<String, Long>()
    private val videoArtworkCache = mutableMapOf<String, String>()
    private val videoMetadataCache = mutableMapOf<String, FreeboxVideoMeta>()
    private val metadataRequestsInFlight = mutableSetOf<String>()

    init {
        viewModelScope.launch {
            watchProgressRepository.allProgress.collect { progresses ->
                progressVideoDurations.clear()
                progresses
                    .filter { it.contentId.startsWith("freebox:") && it.duration > 0L }
                    .forEach { progressVideoDurations[it.contentId] = it.duration }
                  val positions = progresses
                      .filter { it.contentId.startsWith("freebox:") && it.position > 0L && it.duration > 0L && !it.isCompleted() }
                      .associate { it.contentId to it.remainingTime }
                  _uiState.update { s -> s.copy(knownPositions = positions) }
                publishKnownDurations()
            }
        }

        viewModelScope.launch {
            dataStore.settings.collect { settings ->
                _uiState.update { it.copy(
                    sortMode = settings.browserSortModeFor(it.currentPath),
                    showExtensions = settings.showExtensions,
                    showHiddenFiles = settings.showHiddenFiles
                ) }
            }
        }

        viewModelScope.launch {
            val settings = dataStore.settings.first()
            val hasConfig = settings.appToken.isNotBlank()
            _uiState.update { it.copy(hasSession = hasConfig, sortMode = settings.browserSortModeFor(FREEBOX_ROOT_PATH)) }
            if (hasConfig) {
                val token = openFreshSession(settings)
                if (token != null && !rootFolderOverrideRequested) {
                    loadRootFolders(settings)
                }
            }
        }
    }

    private suspend fun loadRootFolders(settings: FreeboxConnectionSettings) {
        val token = getOrRefreshSession(settings) ?: return
        navStack.clear()
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        listDirectoryWithRetry(settings, token, FREEBOX_ROOT_PATH)
            .onSuccess { entries ->
                val rootEntries = filterHiddenFiles(filterRootEntries(entries, settings))
                applyEntries(rootEntries) {
                    it.copy(
                        currentPath = FREEBOX_ROOT_PATH,
                        sortMode = settings.browserSortModeFor(FREEBOX_ROOT_PATH),
                        breadcrumb = emptyList(),
                        isAtRoot = true,
                        hasSession = true
                    )
                }
                enrichVisibleVideos(settings, token, rootEntries)
            }
            .onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Erreur lors du chargement de la racine Freebox.",
                        currentPath = FREEBOX_ROOT_PATH,
                        sortMode = settings.browserSortModeFor(FREEBOX_ROOT_PATH),
                        breadcrumb = emptyList(),
                        isAtRoot = true,
                        hasSession = true
                    )
                }
            }
    }

    fun navigateTo(entry: FreeboxFileEntry) {
        if (!entry.isDirectory) return
        viewModelScope.launch {
            val settings = dataStore.settings.first()
            val token = getOrRefreshSession(settings) ?: return@launch

            navStack.addLast(_uiState.value.currentPath to (_uiState.value.breadcrumb.lastOrNull() ?: "Freebox"))
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            listDirectoryWithRetry(settings, token, entry.path)
                .onSuccess { children ->
                    val sorted = sortEntries(filterHiddenFiles(children))
                    applyEntries(sorted) {
                        it.copy(
                            currentPath = entry.path,
                            sortMode = settings.browserSortModeFor(entry.path),
                            breadcrumb = it.breadcrumb + entry.name,
                            isAtRoot = false
                        )
                    }
                    enrichVisibleVideos(settings, token, sorted)
                }
                .onFailure { error ->
                    navStack.removeLastOrNull()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erreur lors de l'ouverture du dossier."
                        )
                    }
                }
        }
    }

    fun openRootFolder(folderName: String) {
        if (folderName.isBlank()) return
        rootFolderOverrideRequested = true
        viewModelScope.launch {
            val settings = dataStore.settings.first()
            val token = getOrRefreshSession(settings) ?: return@launch
            val path = "$FREEBOX_ROOT_PATH/$folderName"

            navStack.clear()
            navStack.addLast(FREEBOX_ROOT_PATH to "Freebox")
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            listDirectoryWithRetry(settings, token, path)
                .onSuccess { children ->
                    val sorted = sortEntries(filterHiddenFiles(children))
                    applyEntries(sorted) {
                        it.copy(
                            currentPath = path,
                            sortMode = settings.browserSortModeFor(path),
                            breadcrumb = listOf(folderName),
                            isAtRoot = false,
                            hasSession = true
                        )
                    }
                    enrichVisibleVideos(settings, token, sorted)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erreur lors de l'ouverture du dossier."
                        )
                    }
                }
        }
    }


    suspend fun playbackRequestForContent(
        contentIdOrVideoId: String,
        title: String,
        videoSize: Long? = null,
        durationMs: Long? = null,
        artworkUrl: String? = null
    ): FreeboxPlaybackRequest? {
        val rawId = contentIdOrVideoId.trim()
        val path = freeboxPathFromContentId(rawId).takeIf { it.isNotBlank() } ?: return null
        val settings = dataStore.settings.first()
        val token = getOrRefreshSession(settings) ?: return null
        val videoId = rawId.takeIf { it.startsWith("freebox:") } ?: freeboxContentIdForPath(path)
        return FreeboxPlaybackRequest(
            streamUrl = freeboxClient.downloadUrl(
                settings = settings.copy(sessionToken = token),
                path = path,
                encodedPath = null
            ),
            title = freeboxDisplayName(title.ifBlank { path }),
            headers = freeboxClient.sessionHeaders(token),
            videoId = videoId,
            videoSize = videoSize,
            durationMs = durationMs?.takeIf { it > 0L },
            artworkUrl = artworkUrl?.takeIf { it.isNotBlank() }
        )
    }
    suspend fun playbackRequestFor(entry: FreeboxFileEntry): FreeboxPlaybackRequest? {
        if (entry.isDirectory || !entry.isVideoFile()) return null
        val settings = dataStore.settings.first()
        val token = getOrRefreshSession(settings) ?: return null
        val videoId = freeboxContentIdForEntry(entry)
        // Artwork fetched in background - do not block playback startup
        viewModelScope.launch { artworkFor(settings, token, entry, videoId) }
        val artworkUrl = videoArtworkCache[videoId]?.takeIf { it.isNotBlank() }
        return FreeboxPlaybackRequest(
            streamUrl = freeboxClient.downloadUrl(
                settings = settings.copy(sessionToken = token),
                path = entry.path,
                encodedPath = entry.encodedPath
            ),
            title = freeboxDisplayName(entry.name),
            headers = freeboxClient.sessionHeaders(token),
            videoId = videoId,
            videoSize = entry.size,
            durationMs = knownDurationFor(videoId, entry),
            artworkUrl = artworkUrl
        )
    }


    suspend fun fileOpenRequestFor(entry: FreeboxFileEntry): FreeboxFileOpenRequest? {
        if (entry.isDirectory) return null
        val extension = entry.name.substringAfterLast('.', missingDelimiterValue = "").lowercase()
        val mimeType = when (extension) {
            "pdf" -> "application/pdf"
            "apk" -> "application/vnd.android.package-archive"
            else -> return null
        }
        val settings = dataStore.settings.first()
        val token = openFreshSession(settings) ?: return null
        val safeName = freeboxFileNameOnly(entry.name).replace(Regex("[^A-Za-z0-9._-]"), "_")
        val target = File(appContext.cacheDir, "freebox-open/$safeName")
        return freeboxClient.downloadToFile(
            settings = settings,
            sessionToken = token,
            path = entry.path,
            encodedPath = entry.encodedPath,
            destination = target
        ).fold(
            onSuccess = { file ->
                FreeboxFileOpenRequest(
                    file = file,
                    title = freeboxDisplayName(entry.name),
                    mimeType = mimeType,
                    isApk = extension == "apk"
                )
            },
            onFailure = { error ->
                _uiState.update { it.copy(errorMessage = error.message ?: "Impossible d'ouvrir la session Freebox.") }
                null
            }
        )
    }

    suspend fun photoRequestFor(entry: FreeboxFileEntry): FreeboxPhotoRequest? {
        if (entry.isDirectory || !entry.isImageFile()) return null
        val settings = dataStore.settings.first()
        val token = openFreshSession(settings) ?: return null
        return FreeboxPhotoRequest(
            photoUrl = freeboxClient.downloadUrl(
                settings = settings.copy(sessionToken = token),
                path = entry.path,
                encodedPath = entry.encodedPath
            ),
            title = freeboxDisplayName(entry.name),
            headers = freeboxClient.sessionHeaders(token),
            photoId = "freebox-photo:${entry.path}"
        )
    }

    fun navigateUp() {
        if (navStack.isEmpty()) return
        val (previousPath, _) = navStack.removeLastOrNull() ?: return

        if (navStack.isEmpty()) {
            viewModelScope.launch {
                val settings = dataStore.settings.first()
                loadRootFolders(settings)
            }
            return
        }

        viewModelScope.launch {
            val settings = dataStore.settings.first()
            val token = getOrRefreshSession(settings) ?: return@launch

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            listDirectoryWithRetry(settings, token, previousPath)
                .onSuccess { children ->
                    val sorted = sortEntries(filterHiddenFiles(children))
                    applyEntries(sorted) {
                        it.copy(
                            currentPath = previousPath,
                            sortMode = settings.browserSortModeFor(previousPath),
                            breadcrumb = it.breadcrumb.dropLast(1),
                            isAtRoot = navStack.isEmpty()
                        )
                    }
                    enrichVisibleVideos(settings, token, sorted)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erreur lors du retour arriere."
                        )
                    }
                }
        }
    }

    fun deleteFromFreebox(entry: FreeboxFileEntry) {
        viewModelScope.launch {
            val settings = dataStore.settings.first()
            val token = getOrRefreshSession(settings) ?: return@launch
            freeboxClient.deleteFiles(settings.copy(sessionToken = token), listOf(entry))
                .onSuccess {
                    _uiState.update { s ->
                        s.copy(entries = s.entries.filter { it.path != entry.path })
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Erreur lors de la suppression.") }
                }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }


    fun setSortMode(modeName: String) {
        _uiState.update { it.copy(sortMode = modeName) }
        viewModelScope.launch {
            dataStore.setBrowserSortModeForFolder(_uiState.value.currentPath, modeName)
        }
    }

    fun setViewMode(mode: String) {
        _uiState.update { it.copy(viewMode = mode) }
    }

    val canNavigateUp: Boolean
        get() = navStack.isNotEmpty()

    private fun filterRootEntries(
        entries: List<FreeboxFileEntry>,
        settings: FreeboxConnectionSettings
    ): List<FreeboxFileEntry> {
        val visibleFolders = settings.visibleServerFolders
        return sortEntries(
            entries.filter { entry ->
                !entry.isDirectory ||
                    !settings.serverFoldersConfigured ||
                    freeboxFileNameOnly(entry.name) in visibleFolders
            }
        )
    }

    private fun applyEntries(
        entries: List<FreeboxFileEntry>,
        extra: (FreeboxBrowserUiState) -> FreeboxBrowserUiState
    ) {
        entries.forEach { entry ->
            val duration = entry.durationMs?.takeIf { it > 0L } ?: return@forEach
            directVideoDurations[freeboxContentIdForEntry(entry)] = duration
        }
        viewModelScope.launch(Dispatchers.IO) { migrateRenamedFiles(entries) }
        _uiState.update { state ->
            extra(
                state.copy(
                    entries = entries,
                    isLoading = false,
                    errorMessage = null,
                    knownVideoDurations = progressVideoDurations + directVideoDurations,
                    videoArtwork = videoArtworkCache.toMap(),
                    videoMetadata = videoMetadataCache.toMap()
                )
            )
        }
    }

    private fun enrichVisibleVideos(
        settings: FreeboxConnectionSettings,
        sessionToken: String,
        entries: List<FreeboxFileEntry>
    ) {
        val videosToEnrich = entries
            .asSequence()
            .filter { !it.isDirectory && it.isVideoFile() }
            .take(40)
            .filter { metadataRequestsInFlight.add(freeboxContentIdForEntry(it)) }
            .toList()
        if (videosToEnrich.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            videosToEnrich.forEach { entry ->
                val contentId = freeboxContentIdForEntry(entry)
                try {
                    if (knownDurationFor(contentId, entry) == null) {
                        readRemoteDurationMs(settings, sessionToken, entry)?.let { duration ->
                            directVideoDurations[contentId] = duration
                            publishKnownDurations()
                        }
                    }
                    if (videoArtworkCache[contentId].isNullOrBlank()) {
                        val meta = tmdbService.fetchMetadataForTitleQuery(
                            query = freeboxTmdbSearchQuery(entry.name),
                            mediaTypeHint = "movie"
                        )
                        if (meta != null) {
                            val imageUrl = meta.posterUrl ?: meta.backdropUrl
                            if (!imageUrl.isNullOrBlank()) {
                                videoArtworkCache[contentId] = imageUrl
                                publishArtwork()
                            }
                            videoMetadataCache[contentId] = meta
                            publishMetadata()
                        }
                    }
                } finally {
                    metadataRequestsInFlight.remove(contentId)
                }
            }
        }
    }

    private suspend fun readRemoteDurationMs(
        settings: FreeboxConnectionSettings,
        sessionToken: String,
        entry: FreeboxFileEntry
    ): Long? = withContext(Dispatchers.IO) {
        runCatching {
            val retriever = MediaMetadataRetriever()
            try {
                val url = freeboxClient.downloadUrl(
                    settings = settings.copy(sessionToken = sessionToken),
                    path = entry.path,
                    encodedPath = entry.encodedPath
                )
                retriever.setDataSource(url, freeboxClient.sessionHeaders(sessionToken))
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull()
                    ?.takeIf { it > 0L }
            } finally {
                retriever.release()
            }
        }.getOrNull()
    }

    private fun publishKnownDurations() {
        _uiState.update { it.copy(knownVideoDurations = progressVideoDurations + directVideoDurations) }
    }

    private suspend fun artworkFor(
        settings: FreeboxConnectionSettings,
        sessionToken: String,
        entry: FreeboxFileEntry,
        contentId: String
    ): String? {
        videoArtworkCache[contentId]?.takeIf { it.isNotBlank() }?.let { return it }
        val images = tmdbService.fetchImagesForTitleQuery(
            query = freeboxTmdbSearchQuery(entry.name),
            mediaTypeHint = "movie"
        )
        val imageUrl = images?.posterUrl ?: images?.backdropUrl
        if (!imageUrl.isNullOrBlank()) {
            videoArtworkCache[contentId] = imageUrl
            publishArtwork()
        }
        return imageUrl
    }
    private fun publishArtwork() {
        _uiState.update { it.copy(videoArtwork = videoArtworkCache.toMap()) }
    }

    private fun publishMetadata() {
        _uiState.update { it.copy(videoMetadata = videoMetadataCache.toMap()) }
    }

    private fun knownDurationFor(contentId: String, entry: FreeboxFileEntry): Long? {
        return directVideoDurations[contentId]
            ?: progressVideoDurations[contentId]
            ?: entry.durationMs?.takeIf { it > 0L }
    }

    private suspend fun getOrRefreshSession(settings: FreeboxConnectionSettings): String? {
        return liveSessionToken ?: openFreshSession(settings)
    }

    private suspend fun openFreshSession(settings: FreeboxConnectionSettings): String? {
        return freeboxClient.openSession(settings)
            .onSuccess { session ->
                dataStore.saveSession(session)
                liveSessionToken = session.sessionToken
            }
            .getOrElse { error ->
                _uiState.update {
                    it.copy(
                        errorMessage = error.message ?: "Impossible d'ouvrir la session Freebox."
                    )
                }
                null
            }
            ?.sessionToken
    }

    private suspend fun listDirectoryWithRetry(
        settings: FreeboxConnectionSettings,
        token: String,
        path: String
    ): Result<List<FreeboxFileEntry>> {
        val firstTry = freeboxClient.listDirectory(settings.copy(sessionToken = token), path)
        if (firstTry.isFailure) {
            val msg = firstTry.exceptionOrNull()?.message.orEmpty()
            if (msg.contains("auth_required", ignoreCase = true) ||
                msg.contains("403", ignoreCase = true) ||
                msg.contains("authRequired", ignoreCase = true)
            ) {
                liveSessionToken = null
                val freshToken = openFreshSession(settings) ?: return firstTry
                return freeboxClient.listDirectory(settings.copy(sessionToken = freshToken), path)
            }
        }
        return firstTry
    }

    fun FreeboxFileEntry.isImageFile(): Boolean {
        val extension = name.substringAfterLast('.', missingDelimiterValue = "").lowercase()
        return extension in IMAGE_EXTENSIONS
    }

    private fun FreeboxFileEntry.isVideoFile(): Boolean {
        val extension = name.substringAfterLast('.', missingDelimiterValue = "").lowercase()
        return extension in VIDEO_EXTENSIONS
    }

    private fun sortEntries(entries: List<FreeboxFileEntry>): List<FreeboxFileEntry> {
        return entries.sortedWith(
            compareByDescending<FreeboxFileEntry> { it.isDirectory }
                .thenBy(String.CASE_INSENSITIVE_ORDER) { it.name }
        )
    }

    private fun filterHiddenFiles(entries: List<FreeboxFileEntry>): List<FreeboxFileEntry> {
        val showHidden = _uiState.value.showHiddenFiles
        if (showHidden) return entries
        return entries.filter { entry ->
            val name = entry.name.substringAfterLast('/').substringAfterLast('\\')
            !name.startsWith(".")
        }
    }

    private companion object {
        private const val FREEBOX_ROOT_PATH = "/Freebox"

        val IMAGE_EXTENSIONS = setOf(
            "avif", "bmp", "gif", "heic", "heif", "jpeg", "jpg", "png", "tif", "tiff", "webp"
        )

        val VIDEO_EXTENSIONS = setOf(
            "3g2", "3gp", "avi", "flv", "m2ts", "m4v", "mkv", "mov", "mp4", "mpeg",
            "mpg", "mts", "ts", "webm", "wmv"
        )
    }
    private suspend fun migrateRenamedFiles(entries: List<FreeboxFileEntry>) {
        val allProgress = watchProgressRepository.allProgress.first()
        val freeboxProgress = allProgress.filter { it.contentId.startsWith("freebox:") && it.position > 0L }
        if (freeboxProgress.isEmpty()) return
        val currentPaths = entries.filter { !it.isDirectory }.map { it.path.trim() }.toSet()
        val orphaned = freeboxProgress.filter { freeboxPathFromContentId(it.contentId) !in currentPaths }
        if (orphaned.isEmpty()) return
        val byDir = entries.filter { !it.isDirectory }.groupBy { it.path.substringBeforeLast("/") }
        orphaned.forEach { progress ->
            val pp = freeboxPathFromContentId(progress.contentId)
            val dir = pp.substringBeforeLast("/")
            val pname = pp.substringAfterLast("/").substringBeforeLast(".").lowercase().trim()
            val candidates = byDir[dir] ?: return@forEach
            val best = candidates.maxByOrNull { nameSimilarity(pname, it.path.substringAfterLast("/").substringBeforeLast(".").lowercase().trim()) } ?: return@forEach
            val bname = best.path.substringAfterLast("/").substringBeforeLast(".").lowercase().trim()
            if (nameSimilarity(pname, bname) < 0.6) return@forEach
            val newId = freeboxContentIdForEntry(best)
            if (allProgress.any { it.contentId == newId && it.position > 0L }) return@forEach
            val migrated = progress.copy(contentId = newId, videoId = newId, name = freeboxFileNameOnly(best.path))
            watchProgressRepository.saveProgress(migrated)
            watchProgressRepository.removeProgress(progress.contentId)
            progressVideoDurations.remove(progress.contentId)
            videoArtworkCache.remove(progress.contentId)
            videoMetadataCache.remove(progress.contentId)
        }
    }

    private fun nameSimilarity(a: String, b: String): Double {
        if (a == b) return 1.0
        if (a.length < 2 || b.length < 2) return 0.0
        fun bigrams(s: String) = (0 until s.length - 1).map { s.substring(it, it + 2) }
        val ba = bigrams(a); val bb = bigrams(b).toMutableList(); var inter = 0
        ba.forEach { bg -> val i = bb.indexOf(bg); if (i >= 0) { inter++; bb.removeAt(i) } }
        return 2.0 * inter / (ba.size + bigrams(b).size)
    }

}
