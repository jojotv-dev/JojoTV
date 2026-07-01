package com.nuvio.tv.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.data.local.IptvSettingsDataStore
import com.nuvio.tv.data.local.IptvSidebarSettings
import com.nuvio.tv.domain.model.IptvPosterSize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IptvSettingsViewModel @Inject constructor(
    private val dataStore: IptvSettingsDataStore
) : ViewModel() {

    val settings = dataStore.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = IptvSidebarSettings()
    )

    fun setShowLiveTv(enabled: Boolean) {
        viewModelScope.launch { dataStore.setShowLiveTv(enabled) }
    }

    fun setShowMovies(enabled: Boolean) {
        viewModelScope.launch { dataStore.setShowMovies(enabled) }
    }

    fun setShowSeries(enabled: Boolean) {
        viewModelScope.launch { dataStore.setShowSeries(enabled) }
    }

    fun setShowRecordings(enabled: Boolean) {
        viewModelScope.launch { dataStore.setShowRecordings(enabled) }
    }
    val vodPosterSize = dataStore.vodPosterSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = IptvPosterSize.DEFAULT
    )
    fun setVodPosterSize(size: IptvPosterSize) {
        viewModelScope.launch { dataStore.setVodPosterSize(size) }
    }
}