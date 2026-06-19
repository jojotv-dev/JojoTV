package com.nuvio.tv.ui.screens.iptv
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.core.sync.ProfileSettingsSyncService
import com.streamvault.data.local.dao.ChannelDao
import com.streamvault.data.local.dao.MovieDao
import com.streamvault.data.local.dao.SeriesDao
import com.streamvault.domain.model.Provider
import com.streamvault.domain.model.SyncState
import com.streamvault.domain.repository.ProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "IptvHomeViewModel"

data class ProviderCounts(
    val channels: Int = 0,
    val movies: Int = 0,
    val series: Int = 0
)

@HiltViewModel
class IptvHomeViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val profileSettingsSyncService: ProfileSettingsSyncService,
    private val channelDao: ChannelDao,
    private val movieDao: MovieDao,
    private val seriesDao: SeriesDao
) : ViewModel() {
    val providers: StateFlow<List<Provider>> = providerRepository.getProviders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val providerCounts: StateFlow<Map<Long, ProviderCounts>> = providers
        .flatMapLatest { providerList ->
            if (providerList.isEmpty()) return@flatMapLatest flowOf(emptyMap())
            val flows = providerList.map { provider ->
                combine(
                    channelDao.getCount(provider.id),
                    movieDao.getCount(provider.id),
                    seriesDao.getCount(provider.id)
                ) { ch, mv, sr -> provider.id to ProviderCounts(ch, mv, sr) }
            }
            combine(flows) { it.toMap() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    private val _syncStates = MutableStateFlow<Map<Long, SyncState>>(emptyMap())
    val syncStates: StateFlow<Map<Long, SyncState>> = _syncStates.asStateFlow()
    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError.asStateFlow()

    fun syncProvider(providerId: Long) {
        viewModelScope.launch {
            _syncStates.value = _syncStates.value + (providerId to SyncState.Syncing("Synchronisation..."))
            val result = providerRepository.refreshProviderData(
                providerId = providerId,
                force = true,
                onProgress = { phase ->
                    _syncStates.value = _syncStates.value + (providerId to SyncState.Syncing(phase))
                }
            )
            val newState = when (result) {
                is com.streamvault.domain.model.Result.Success -> SyncState.Success()
                is com.streamvault.domain.model.Result.Error -> SyncState.Error(result.message)
                else -> SyncState.Error("Erreur inconnue")
            }
            _syncStates.value = _syncStates.value + (providerId to newState)
        }
    }

    private val _deleteRequest = MutableStateFlow<Provider?>(null)
    val deleteRequest: StateFlow<Provider?> = _deleteRequest.asStateFlow()

    fun requestDeleteProvider(provider: Provider) {
        _deleteRequest.value = provider
    }

    fun cancelDeleteProvider() {
        _deleteRequest.value = null
    }

    fun confirmDeleteProvider(providerId: Long) {
        _deleteRequest.value = null
        deleteProvider(providerId)
    }

    fun deleteProvider(providerId: Long) {
        viewModelScope.launch {
            val result = providerRepository.deleteProvider(providerId)
            if (result is com.streamvault.domain.model.Result.Error) {
                _deleteError.value = result.message
            } else {
                profileSettingsSyncService.pushCurrentProfileToRemote()
                    .onFailure { error ->
                        Log.w(TAG, "Provider $providerId deleted locally but remote profile sync failed", error)
                    }
            }
        }
    }

    fun clearDeleteError() {
        _deleteError.value = null
    }
}
