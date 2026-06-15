package com.nuvio.tv.ui.screens.iptv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streamvault.domain.model.Provider
import com.streamvault.domain.repository.ProviderRepository
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
    private val providerRepository: ProviderRepository
) : ViewModel() {

    private val _importProgress = MutableStateFlow(ImportProgressState())
    val importProgress: StateFlow<ImportProgressState> = _importProgress.asStateFlow()

    fun resetImportProgress() {
        _importProgress.value = ImportProgressState()
    }

    private fun startImport() {
        _importProgress.value = ImportProgressState(isImporting = true, currentStep = ImportStep.DOWNLOADING)
    }

    // Parse les messages onProgress de StreamVault pour extraire les compteurs.
    // Exemples emis : "Loaded 11919 channels", "Loaded 25952 movies", "Loaded 8170 series"
    private fun handleProgressMessage(raw: String) {
        val msg = raw.trim()
        _importProgress.update { it.copy(progressMessage = msg, currentStep = ImportStep.PARSING) }
        val clean = msg.replace(",", "").replace(" ", "")
        // Chaines live
        Regex("(\\d+)(?:channels?|chaines?|live)", RegexOption.IGNORE_CASE)
            .find(clean)?.groupValues?.getOrNull(1)?.toIntOrNull()
            ?.let { n -> _importProgress.update { it.copy(liveCount = n) } }
        // Films
        Regex("(\\d+)(?:movies?|films?|vod)", RegexOption.IGNORE_CASE)
            .find(clean)?.groupValues?.getOrNull(1)?.toIntOrNull()
            ?.let { n -> _importProgress.update { it.copy(movieCount = n) } }
        // Series
        Regex("(\\d+)(?:series?|shows?)", RegexOption.IGNORE_CASE)
            .find(clean)?.groupValues?.getOrNull(1)?.toIntOrNull()
            ?.let { n -> _importProgress.update { it.copy(seriesCount = n) } }
    }

    private fun handleResult(result: ValidateAndAddProviderResult) {
        when (result) {
            is ValidateAndAddProviderResult.Success,
            is ValidateAndAddProviderResult.SavedWithWarning ->
                _importProgress.update { it.copy(isImporting = false, isDone = true, currentStep = ImportStep.DONE) }
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
