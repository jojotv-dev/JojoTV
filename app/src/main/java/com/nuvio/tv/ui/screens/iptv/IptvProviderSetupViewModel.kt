package com.nuvio.tv.ui.screens.iptv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streamvault.data.sync.SyncProgressBus
import com.streamvault.domain.model.Provider
import com.streamvault.domain.repository.ProviderRepository
import com.streamvault.domain.sync.Section
import com.streamvault.domain.usecase.M3uProviderSetupCommand
import com.streamvault.domain.usecase.StalkerProviderSetupCommand
import com.streamvault.domain.usecase.ValidateAndAddProvider
import com.streamvault.domain.usecase.ValidateAndAddProviderResult
import com.streamvault.domain.usecase.XtreamProviderSetupCommand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IptvProviderSetupViewModel @Inject constructor(
    private val validateAndAddProvider: ValidateAndAddProvider,
    private val providerRepository: ProviderRepository,
    private val syncProgressBus: SyncProgressBus
) : ViewModel() {

    private val _importProgress = MutableStateFlow(ImportProgressState())
    val importProgress: StateFlow<ImportProgressState> = _importProgress.asStateFlow()

    // Collecte le SyncProgressBus en temps reel pour mettre a jour les compteurs
    private var syncProgressJob: kotlinx.coroutines.Job? = null

    fun resetImportProgress() {
        syncProgressJob?.cancel()
        syncProgressJob = null
        _importProgress.value = ImportProgressState()
    }

    private fun startImport() {
        syncProgressBus.reset()
        _importProgress.value = ImportProgressState(isImporting = true, currentStep = ImportStep.DOWNLOADING)
        // Abonnement au bus de progression
        syncProgressJob?.cancel()
        syncProgressJob = viewModelScope.launch {
            syncProgressBus.flow.collect { progress ->
                progress ?: return@collect
                _importProgress.update { state ->
                    when (progress.section) {
                        Section.LIVE -> state.copy(
                            currentStep = ImportStep.PARSING,
                            liveCount = progress.itemsIndexed.coerceAtLeast(state.liveCount)
                        )
                        Section.VOD -> state.copy(
                            currentStep = ImportStep.PARSING,
                            // itemsIndexed est cumulatif : VOD = total - live deja indexe
                            movieCount = (progress.itemsIndexed - state.liveCount).coerceAtLeast(state.movieCount)
                        )
                        Section.SERIES -> state.copy(
                            currentStep = ImportStep.PARSING,
                            // SERIES = total - live - movies
                            seriesCount = (progress.itemsIndexed - state.liveCount - state.movieCount).coerceAtLeast(state.seriesCount)
                        )
                    }
                }
            }
        }
    }

    // Message onProgress => label de progression (pas de compteurs ici)
    private fun handleProgressMessage(raw: String) {
        val msg = raw.trim()
        _importProgress.update { it.copy(progressMessage = msg, currentStep = ImportStep.PARSING) }
    }

    private fun handleResult(result: ValidateAndAddProviderResult) {
        syncProgressJob?.cancel()
        syncProgressJob = null
        when (result) {
            is ValidateAndAddProviderResult.Success,
            is ValidateAndAddProviderResult.SavedWithWarning ->
                _importProgress.update { it.copy(isImporting = false, isDone = true, currentStep = ImportStep.DONE, progressMessage = "") }
            is ValidateAndAddProviderResult.ValidationError ->
                _importProgress.update { it.copy(isImporting = false, currentStep = ImportStep.ERROR, errorMessage = result.message) }
            is ValidateAndAddProviderResult.Error ->
                _importProgress.update { it.copy(isImporting = false, currentStep = ImportStep.ERROR, errorMessage = result.message) }
        }
    }

    suspend fun getProvider(id: Long): Provider? = providerRepository.getProvider(id)

    suspend fun addXtream(command: XtreamProviderSetupCommand): ValidateAndAddProviderResult {
        startImport()
        return validateAndAddProvider.loginXtream(command) { handleProgressMessage(it) }.also { handleResult(it) }
    }

    suspend fun addM3u(command: M3uProviderSetupCommand): ValidateAndAddProviderResult {
        startImport()
        return validateAndAddProvider.addM3u(command) { handleProgressMessage(it) }.also { handleResult(it) }
    }

    suspend fun addStalker(command: StalkerProviderSetupCommand): ValidateAndAddProviderResult {
        startImport()
        return validateAndAddProvider.loginStalker(command) { handleProgressMessage(it) }.also { handleResult(it) }
    }

    suspend fun updateXtream(id: Long, command: XtreamProviderSetupCommand): ValidateAndAddProviderResult {
        startImport()
        return validateAndAddProvider.loginXtream(command.copy(existingProviderId = id)) { handleProgressMessage(it) }.also { handleResult(it) }
    }

    suspend fun updateM3u(id: Long, command: M3uProviderSetupCommand): ValidateAndAddProviderResult {
        startImport()
        return validateAndAddProvider.addM3u(command.copy(existingProviderId = id)) { handleProgressMessage(it) }.also { handleResult(it) }
    }

    suspend fun updateStalker(id: Long, command: StalkerProviderSetupCommand): ValidateAndAddProviderResult {
        startImport()
        return validateAndAddProvider.loginStalker(command.copy(existingProviderId = id)) { handleProgressMessage(it) }.also { handleResult(it) }
    }

    fun syncProviderInBackground(providerId: Long) {
        viewModelScope.launch {
            providerRepository.refreshProviderData(providerId = providerId, force = true, onProgress = {})
        }
    }
}
