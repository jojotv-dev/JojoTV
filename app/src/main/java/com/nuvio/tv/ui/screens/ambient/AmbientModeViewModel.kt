package com.nuvio.tv.ui.screens.ambient

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.core.profile.ProfileManager
import com.nuvio.tv.data.local.AmbientModeDataStore
import com.nuvio.tv.domain.model.AmbientModeSettings
import com.nuvio.tv.domain.model.LibraryEntry
import com.nuvio.tv.domain.repository.LibraryRepository
import com.nuvio.tv.reshaped.livetv.LiveTvRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PRELOAD_DELAY_MS = 20_000L
private const val REFRESH_INTERVAL_MS = 30L * 60_000L

@HiltViewModel
class AmbientModeViewModel @Inject constructor(
    dataStore: AmbientModeDataStore,
    private val loader: AmbientSlideLoader,
    private val libraryRepository: LibraryRepository,
    private val profileManager: ProfileManager,
) : ViewModel() {

    val settings: StateFlow<AmbientModeSettings> = dataStore.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, AmbientModeSettings())

    private val _slides = MutableStateFlow<List<AmbientSlide>>(emptyList())
    val slides: StateFlow<List<AmbientSlide>> = _slides.asStateFlow()

    private var loadJob: Job? = null
    private var loadingKeys: List<String>? = null
    private var loadingProfileId: Int? = null
    private var loadedKeys: List<String>? = null
    private var loadedProfileId: Int? = null
    private var loadedAt = 0L

    init {
        viewModelScope.launch {
            settings
                .filter { it.enabled }
                .map { it.catalogKeys }
                .distinctUntilChanged()
                .collectLatest { keys ->
                    delay(PRELOAD_DELAY_MS)
                    refresh(keys)
                }
        }
        viewModelScope.launch {
            combine(
                libraryRepository.libraryItems.map { items ->
                    items.filter(LibraryEntry::isAmbientFavoriteContent)
                        .map { "${it.type.lowercase()}:${it.id}" }
                        .sorted()
                },
                LiveTvRepository.uiState.map { it.favoriteVodKeys.sorted() }
            ) { libraryFavorites, iptvFavorites -> libraryFavorites to iptvFavorites }
                .distinctUntilChanged()
                .collect {
                    if (settings.value.enabled && (loadedAt > 0L || loadJob?.isActive == true)) {
                        refresh(settings.value.catalogKeys, force = true)
                    }
                }
        }
    }

    fun onShown() {
        val keys = settings.value.catalogKeys
        val profileId = profileManager.activeProfileId.value
        val profileChanged = profileId != loadedProfileId ||
            (loadJob?.isActive == true && loadingProfileId != profileId)
        val stale = SystemClock.elapsedRealtime() - loadedAt > REFRESH_INTERVAL_MS
        if (profileChanged || keys != loadedKeys || stale || _slides.value.isEmpty()) {
            if (profileChanged) _slides.value = emptyList()
            refresh(keys)
        } else {
            _slides.value = _slides.value.shuffled()
        }
    }

    private fun refresh(keys: List<String>, force: Boolean = false) {
        val profileId = profileManager.activeProfileId.value
        if (!force && loadJob?.isActive == true && loadingKeys == keys && loadingProfileId == profileId) return
        loadJob?.cancel()
        loadingKeys = keys
        loadingProfileId = profileId
        loadJob = viewModelScope.launch {
            val slides = try {
                loader.load(
                    legacyCatalogKeys = keys,
                    profileId = profileId,
                    onLibrarySlides = { librarySlides -> _slides.value = librarySlides },
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                emptyList()
            }
            _slides.value = slides
            loadedKeys = keys
            loadedProfileId = profileId
            loadedAt = SystemClock.elapsedRealtime()
        }
    }
}

private fun LibraryEntry.isAmbientFavoriteContent(): Boolean =
    type.equals("movie", ignoreCase = true) ||
        type.equals("series", ignoreCase = true) ||
        type.equals("tv", ignoreCase = true) ||
        type.equals("anime", ignoreCase = true)
