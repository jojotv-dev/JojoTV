package com.nuvio.tv.ui.screens.iptv

import androidx.lifecycle.ViewModel
import com.streamvault.domain.model.Provider
import com.streamvault.domain.repository.ProviderRepository
import com.streamvault.domain.usecase.M3uProviderSetupCommand
import com.streamvault.domain.usecase.StalkerProviderSetupCommand
import com.streamvault.domain.usecase.ValidateAndAddProvider
import com.streamvault.domain.usecase.ValidateAndAddProviderResult
import com.streamvault.domain.usecase.XtreamProviderSetupCommand
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class IptvProviderSetupViewModel @Inject constructor(
    private val validateAndAddProvider: ValidateAndAddProvider,
    private val providerRepository: ProviderRepository
) : ViewModel() {

    suspend fun getProvider(id: Long): Provider? = providerRepository.getProvider(id)

    suspend fun addXtream(command: XtreamProviderSetupCommand): ValidateAndAddProviderResult =
        validateAndAddProvider.loginXtream(command)

    suspend fun addM3u(command: M3uProviderSetupCommand): ValidateAndAddProviderResult =
        validateAndAddProvider.addM3u(command)

    suspend fun addStalker(command: StalkerProviderSetupCommand): ValidateAndAddProviderResult =
        validateAndAddProvider.loginStalker(command)

    suspend fun updateXtream(id: Long, command: XtreamProviderSetupCommand): ValidateAndAddProviderResult =
        validateAndAddProvider.loginXtream(command.copy(existingProviderId = id))

    suspend fun updateM3u(id: Long, command: M3uProviderSetupCommand): ValidateAndAddProviderResult =
        validateAndAddProvider.addM3u(command.copy(existingProviderId = id))

    suspend fun updateStalker(id: Long, command: StalkerProviderSetupCommand): ValidateAndAddProviderResult =
        validateAndAddProvider.loginStalker(command.copy(existingProviderId = id))

    fun syncProviderInBackground(providerId: Long) {
        viewModelScope.launch {
            providerRepository.refreshProviderData(
                providerId = providerId,
                force = true,
                onProgress = {}
            )
        }
    }
}
