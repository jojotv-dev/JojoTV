package com.nuvio.tv.ui.screens.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.core.tmdb.TmdbArtworkCandidate
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.data.freebox.FREEBOX_FOLDER_ID_PREFIX
import com.nuvio.tv.data.freebox.freeboxContentIdFor
import com.nuvio.tv.data.freebox.freeboxPathFromContentId
import com.nuvio.tv.domain.model.WatchProgress
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Marks one Freebox file as a standalone watched video, independent of its parent folder name. */
internal fun HomeViewModel.setFreeboxHomeVideoWatched(item: MetaPreview, watched: Boolean) {
    require(item.id.startsWith("freebox:", ignoreCase = true)) {
        "Le suivi de lecture Freebox est réservé aux vidéos Freebox."
    }
    val statusKey = homeItemStatusKey(item.id, "movie")
    val contentId = freeboxContentIdFor(
        path = item.id.substringAfter(':'),
        sizeBytes = item.sourceFileSizeBytes,
        lastModifiedMs = item.sourceFileLastModifiedMs,
        fileId = item.sourceFileId,
    )
    val currentState = _uiState.value
    if (statusKey in currentState.movieWatchedPending) return
    val wasWatched = currentState.movieWatchedStatus[statusKey] == true
    _uiState.update { state ->
        state.copy(
            movieWatchedPending = state.movieWatchedPending + statusKey,
            movieWatchedStatus = state.movieWatchedStatus + (statusKey to watched),
        )
    }

    viewModelScope.launch {
        try {
            val progress = WatchProgress(
                contentId = contentId,
                contentType = "movie",
                name = item.name,
                poster = item.poster,
                backdrop = item.background,
                logo = item.logo,
                videoId = contentId,
                season = null,
                episode = null,
                episodeTitle = null,
                position = 1L,
                duration = 1L,
                lastWatched = System.currentTimeMillis(),
                addonBaseUrl = item.sourceAddonBaseUrl ?: "freebox://nas",
                progressPercent = 100f,
            )
            if (watched) {
                watchProgressRepository.markAsCompleted(progress)
            } else {
                watchProgressRepository.removeFromHistory(
                    contentId = contentId,
                    videoId = contentId,
                )
            }
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (error: Exception) {
            Log.w("FreeboxHomeVideo", "Could not update watched state for ${item.id}", error)
            _uiState.update { state ->
                state.copy(movieWatchedStatus = state.movieWatchedStatus + (statusKey to wasWatched))
            }
        } finally {
            _uiState.update { state ->
                state.copy(movieWatchedPending = state.movieWatchedPending - statusKey)
            }
        }
    }
}

internal suspend fun HomeViewModel.findFreeboxFolderArtworkCandidates(
    path: String,
    title: String,
    mediaType: String,
    portrait: Boolean,
    year: Int? = null,
): List<TmdbArtworkCandidate> {
    val parentTitle = path.replace('\\', '/').substringBeforeLast('/').substringAfterLast('/')
    val numericSeasonFolder = Regex("^(?:(?:season|saison|s)[\\s._-]*)?\\d{1,2}$", RegexOption.IGNORE_CASE)
        .matches(title.trim())
    val searchTitle = if (numericSeasonFolder && parentTitle.isNotBlank()) parentTitle else title
    return tmdbService.artworkCandidatesForExactTitleWithMatches(
        title = searchTitle,
        mediaType = mediaType,
        portrait = portrait,
        language = tmdbSettingsDataStore.settings.first().language,
        year = year,
    )
}

internal suspend fun HomeViewModel.setFreeboxFolderArtwork(
    path: String,
    portrait: Boolean,
    imageUrl: String?,
    candidate: TmdbArtworkCandidate? = null,
    item: MetaPreview? = null,
) {
    val selectedMatch = candidate
        ?.takeIf { it.imageUrl == imageUrl }
        ?.match
    require(imageUrl.isNullOrBlank() || selectedMatch != null) {
        "La vignette choisie n’est pas associée à une fiche TMDB."
    }
    checkNotNull(freeboxRepository) { "Le service Freebox n’est pas disponible." }
        .setFolderArtwork(
            relativePath = path,
            portrait = portrait,
            imageUrl = imageUrl,
            tmdbId = selectedMatch?.tmdbId,
            tmdbContentType = selectedMatch?.contentType?.toApiString(),
        )
    item?.let { refreshFreeboxMetadataAfterArtworkSelection(it) }
}

internal suspend fun HomeViewModel.removeFreeboxFolderFromHome(path: String) {
    checkNotNull(freeboxRepository) { "Le service Freebox n’est pas disponible." }
        .removeFolderFromHome(path)
}

/** Deletes the remote folder first, then clears progress entries tied to confirmed-deleted files. */
internal suspend fun HomeViewModel.deleteFreeboxHomeFolder(path: String): Int {
    val deletedVideoPaths = checkNotNull(freeboxRepository) { "Le service Freebox n’est pas disponible." }
        .deleteHomeFolder(path)
    if (deletedVideoPaths.isEmpty()) return 0

    val deletedPathKeys = deletedVideoPaths.mapNotNull(::normalizedFreeboxPath).toSet()
    val progressItems = try {
        watchProgressRepository.allProgress.first()
    } catch (cancel: CancellationException) {
        throw cancel
    } catch (error: Exception) {
        Log.w("FreeboxHomeFolder", "Folder was deleted but its progress snapshot could not be read", error)
        emptyList()
    }
    val affected = progressItems.filter { progress ->
        sequenceOf(progress.contentId, progress.videoId)
            .mapNotNull { freeboxProgressPath(it, progress.addonBaseUrl) }
            .any { candidate -> deletedPathKeys.any { it.equals(candidate, ignoreCase = true) } }
    }
    affected.forEach { progress ->
        try {
            watchProgressRepository.removeProgress(progress.contentId, progress.season, progress.episode)
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (error: Exception) {
            Log.w("FreeboxHomeFolder", "Could not clear progress for ${progress.contentId}", error)
        }
        try {
            watchProgressRepository.removeFromHistory(
                contentId = progress.contentId,
                videoId = progress.videoId,
                season = progress.season,
                episode = progress.episode,
            )
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (error: Exception) {
            Log.w("FreeboxHomeFolder", "Could not clear history for ${progress.contentId}", error)
        }
    }
    return deletedVideoPaths.size
}

/** Deletes one confirmed Freebox video and clears only history/progress that references its path. */
internal suspend fun HomeViewModel.deleteFreeboxHomeVideo(progress: WatchProgress): String {
    require(
        progress.addonBaseUrl.equals("freebox://nas", ignoreCase = true) ||
            progress.contentId.startsWith("freebox:", ignoreCase = true) ||
            progress.videoId.startsWith("freebox:", ignoreCase = true)
    ) { "La suppression depuis Continuer à regarder est réservée aux vidéos Freebox." }

    // The home continue-watching route also resolves the Freebox file from contentId.
    // Prefer that canonical path over videoId, which can hold a player-specific stream URL.
    val path = sequenceOf(progress.contentId, progress.videoId)
        .mapNotNull { freeboxProgressPath(it, progress.addonBaseUrl) }
        .firstOrNull()
        ?: error("Le chemin de cette vidéo Freebox est introuvable.")
    return deleteFreeboxHomeVideo(progress, path)
}

private suspend fun HomeViewModel.deleteFreeboxHomeVideo(
    progress: WatchProgress,
    path: String,
): String {
    val deletedPath = checkNotNull(freeboxRepository) { "Le service Freebox n’est pas disponible." }
        .deleteHomeVideo(path)
    val deletedPathKey = normalizedFreeboxPath(deletedPath)
        ?: error("Le chemin supprimé de la Freebox est invalide.")

    val progressItems = try {
        watchProgressRepository.allProgress.first()
    } catch (cancel: CancellationException) {
        throw cancel
    } catch (error: Exception) {
        Log.w("FreeboxHomeVideo", "Video was deleted but its progress snapshot could not be read", error)
        emptyList()
    }
    val affected = (progressItems + progress).distinctBy {
        listOf(it.contentId, it.videoId, it.season, it.episode).joinToString("|")
    }.filter { candidate ->
        sequenceOf(candidate.contentId, candidate.videoId)
            .mapNotNull { freeboxProgressPath(it, candidate.addonBaseUrl) }
            .any { it.equals(deletedPathKey, ignoreCase = true) }
    }
    affected.forEach { candidate ->
        try {
            watchProgressRepository.removeProgress(candidate.contentId, candidate.season, candidate.episode)
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (error: Exception) {
            Log.w("FreeboxHomeVideo", "Could not clear progress for ${candidate.contentId}", error)
        }
        try {
            watchProgressRepository.removeFromHistory(
                contentId = candidate.contentId,
                videoId = candidate.videoId,
                season = candidate.season,
                episode = candidate.episode,
            )
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (error: Exception) {
            Log.w("FreeboxHomeVideo", "Could not clear history for ${candidate.contentId}", error)
        }
    }
    return deletedPath
}

internal suspend fun HomeViewModel.deleteFreeboxHomeVideo(item: MetaPreview): String {
    require(item.id.startsWith("freebox:", ignoreCase = true)) {
        "La suppression depuis la Freebox est réservée aux vidéos Freebox."
    }
    val path = normalizedFreeboxPath(item.id.substringAfter(':'))
        ?: error("Le chemin de cette vidéo Freebox est introuvable.")
    val title = item.name
    val contentId = freeboxContentIdFor(
        path = path,
        sizeBytes = item.sourceFileSizeBytes,
        lastModifiedMs = item.sourceFileLastModifiedMs,
        fileId = item.sourceFileId,
    )
    val progress = WatchProgress(
        contentId = contentId,
        contentType = item.apiType.ifBlank { "movie" },
        name = title,
        poster = item.poster,
        backdrop = item.background,
        logo = item.logo,
        videoId = contentId,
        season = null,
        episode = null,
        episodeTitle = null,
        position = 0L,
        duration = 0L,
        lastWatched = System.currentTimeMillis(),
        addonBaseUrl = item.sourceAddonBaseUrl ?: "freebox://nas",
    )
    return deleteFreeboxHomeVideo(progress, path)
}

private fun freeboxProgressPath(value: String, addonBaseUrl: String?): String? =
    freeboxPathFromContentId(value, addonBaseUrl)

internal suspend fun HomeViewModel.refreshFreeboxHomeRows() {
    freeboxRepository?.refreshHomeRows()
}

internal fun HomeViewModel.requestFreeboxHomeRowsRefresh() {
    viewModelScope.launch { refreshFreeboxHomeRows() }
}

private fun normalizedFreeboxPath(value: String): String? {
    val parts = value.replace('\\', '/').split('/').filter(String::isNotBlank)
    if (parts.isEmpty() || parts.any { it == "." || it == ".." }) return null
    return parts.joinToString("/")
}
