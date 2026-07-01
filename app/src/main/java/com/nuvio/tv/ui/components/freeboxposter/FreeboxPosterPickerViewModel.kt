package com.nuvio.tv.ui.components.freeboxposter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.core.tmdb.TmdbPosterCandidate
import com.nuvio.tv.core.tmdb.TmdbArtworkType
import com.nuvio.tv.core.tmdb.TmdbService
import com.nuvio.tv.data.freebox.FreeboxFileEntry
import com.nuvio.tv.data.freebox.FreeboxFrameThumbnailService
import com.nuvio.tv.data.freebox.freeboxContentIdForEntry
import com.nuvio.tv.data.freebox.freeboxTmdbSearchQuery
import com.nuvio.tv.data.local.FreeboxPosterOverrideDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FreeboxPosterPickerState(
    val entry: FreeboxFileEntry? = null,
    val posters: List<TmdbPosterCandidate> = emptyList(),
    val currentPosterUrl: String? = null,
    val currentBackdropUrl: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveFailed: Boolean = false
)

@HiltViewModel
class FreeboxPosterPickerViewModel @Inject constructor(
    private val tmdbService: TmdbService,
    private val frameThumbnailService: FreeboxFrameThumbnailService,
    private val posterOverrideDataStore: FreeboxPosterOverrideDataStore
) : ViewModel() {
    private val _state = MutableStateFlow(FreeboxPosterPickerState())
    val state: StateFlow<FreeboxPosterPickerState> = _state.asStateFlow()

    private var loadJob: Job? = null

    fun open(entry: FreeboxFileEntry, currentPosterUrl: String?, currentBackdropUrl: String? = null) {
        loadJob?.cancel()
        _state.value = FreeboxPosterPickerState(
            entry = entry,
            currentPosterUrl = currentPosterUrl,
            currentBackdropUrl = currentBackdropUrl,
            isLoading = true
        )
        val contentId = freeboxContentIdForEntry(entry)
        loadJob = viewModelScope.launch {
            val framePosters = runCatching {
                frameThumbnailService.thumbnailUris(
                    entry = entry,
                    positionsUs = listOf(10_000_000L, 60_000_000L, 180_000_000L, 300_000_000L)
                )
            }
                .getOrDefault(emptyList())
                .flatMapIndexed { index, uri -> uri.toFramePosterCandidates(index + 1) }
            val existingCandidates = listOfNotNull(
                currentPosterUrl?.let {
                    TmdbPosterCandidate(
                        url = it,
                        language = null,
                        title = "Image actuelle",
                        artworkType = TmdbArtworkType.PORTRAIT
                    )
                },
                currentBackdropUrl?.let {
                    TmdbPosterCandidate(
                        url = it,
                        language = null,
                        title = "Fond actuel",
                        artworkType = TmdbArtworkType.LANDSCAPE
                    )
                }
            ).distinctBy { it.artworkType to it.url }
            val initialCandidates = (existingCandidates + framePosters)
                .distinctBy { it.artworkType to it.url }
            _state.update { current ->
                if (current.entry?.let(::freeboxContentIdForEntry) != contentId) current
                else current.copy(posters = initialCandidates, isLoading = false)
            }
            val freeboxQuery = freeboxTmdbSearchQuery(entry.name)
            val moviePosters = runCatching {
                tmdbService.fetchPosterCandidatesForTitleQuery(
                    query = freeboxQuery,
                    mediaTypeHint = "movie"
                )
            }.getOrDefault(emptyList())
            val tvPosters = runCatching {
                tmdbService.fetchPosterCandidatesForTitleQuery(
                    query = freeboxQuery,
                    mediaTypeHint = "tv"
                )
            }.getOrDefault(emptyList())
            val tmdbPosters = (moviePosters + tvPosters).distinctBy { it.artworkType to it.url }
            val posters = (existingCandidates + framePosters + tmdbPosters)
                .distinctBy { it.artworkType to it.url }
            _state.update { current ->
                if (current.entry?.let(::freeboxContentIdForEntry) != contentId) current
                else current.copy(posters = posters, isLoading = false)
            }
        }
    }

    fun select(poster: TmdbPosterCandidate) {
        val entry = _state.value.entry ?: return
        if (_state.value.isSaving) return
        _state.update { it.copy(isSaving = true, saveFailed = false) }
        viewModelScope.launch {
            runCatching {
                val contentId = freeboxContentIdForEntry(entry)
                if (poster.artworkType == TmdbArtworkType.LANDSCAPE) {
                    posterOverrideDataStore.setBackdrop(contentId, poster.url)
                } else {
                    posterOverrideDataStore.set(contentId, poster.url)
                }
                poster.overview?.takeIf { it.isNotBlank() }?.let { overview ->
                    posterOverrideDataStore.setOverview(contentId, overview)
                }
            }.onSuccess {
                _state.value = FreeboxPosterPickerState()
            }.onFailure {
                _state.update { it.copy(isSaving = false, saveFailed = true) }
            }
        }
    }

    fun dismiss() {
        loadJob?.cancel()
        _state.value = FreeboxPosterPickerState()
    }
}

private fun String.toFramePosterCandidates(index: Int): List<TmdbPosterCandidate> {
    val title = if (index == 1) "Image extraite de la vidéo" else "Autre vignette frame $index"
    return listOf(
        TmdbPosterCandidate(
            url = this,
            language = null,
            title = title,
            mediaType = "local",
            artworkType = TmdbArtworkType.PORTRAIT
        ),
        TmdbPosterCandidate(
            url = this,
            language = null,
            title = title,
            mediaType = "local",
            artworkType = TmdbArtworkType.LANDSCAPE
        )
    )
}
