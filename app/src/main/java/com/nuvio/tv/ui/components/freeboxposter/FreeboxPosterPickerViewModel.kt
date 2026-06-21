package com.nuvio.tv.ui.components.freeboxposter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.core.tmdb.TmdbPosterCandidate
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

    fun open(entry: FreeboxFileEntry, currentPosterUrl: String?) {
        loadJob?.cancel()
        _state.value = FreeboxPosterPickerState(
            entry = entry,
            currentPosterUrl = currentPosterUrl,
            isLoading = true
        )
        val contentId = freeboxContentIdForEntry(entry)
        loadJob = viewModelScope.launch {
            val tmdbPosters = tmdbService.fetchPosterCandidatesForTitleQuery(
                query = freeboxTmdbSearchQuery(entry.name)
            )
            val posters = if (tmdbPosters.isNotEmpty()) {
                tmdbPosters
            } else {
                listOfNotNull(
                    frameThumbnailService.thumbnailUri(entry)?.let { uri ->
                        TmdbPosterCandidate(
                            url = uri,
                            language = null,
                            title = "Image extraite de la vidéo",
                            mediaType = "local"
                        )
                    }
                )
            }
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
                posterOverrideDataStore.set(freeboxContentIdForEntry(entry), poster.url)
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