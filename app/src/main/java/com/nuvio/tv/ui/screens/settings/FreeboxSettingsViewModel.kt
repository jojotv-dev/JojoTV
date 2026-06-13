package com.nuvio.tv.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.core.sync.ProfileSettingsSyncService
import com.nuvio.tv.data.freebox.FreeboxOsClient
import com.nuvio.tv.data.local.FreeboxConnectionSettings
import com.nuvio.tv.data.local.FreeboxSettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FreeboxSettingsViewModel @Inject constructor(
    private val dataStore: FreeboxSettingsDataStore,
    private val freeboxClient: FreeboxOsClient,
    private val profileSettingsSyncService: ProfileSettingsSyncService
) : ViewModel() {

    private val _uiState = MutableStateFlow(FreeboxSettingsUiState())
    val uiState: StateFlow<FreeboxSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.settings.collectLatest { settings ->
                _uiState.update { current ->
                    current.copy(
                        name = settings.name,
                        address = settings.address,
                        username = settings.username,
                        password = settings.password,
                        showInSidebar = settings.showInSidebar,
                        showSourceFolder = settings.showSourceFolder,
                        showExtensions = settings.showExtensions,
                        showHiddenFiles = settings.showHiddenFiles,
                        visibleServerFolders = settings.visibleServerFolders,
                        sidebarServerFolders = settings.sidebarServerFolders,
                        serverFolders = settings.serverFolders.toList().sortedWith(String.CASE_INSENSITIVE_ORDER),
                        hasSavedConnection = settings.hasSavedConnection,
                        authStatus = settings.authStatus,
                        hasAppToken = settings.appToken.isNotBlank(),
                        hasSession = settings.sessionToken.isNotBlank(),
                        appToken = settings.appToken,
                        sessionToken = settings.sessionToken,
                        authTrackId = settings.authTrackId,
                        saved = current.saved
                    )
                }
            }
        }
    }

    fun setName(value: String) {
        _uiState.update { it.copy(name = value, saved = false) }
    }

    fun setAddress(value: String) {
        _uiState.update { it.copy(address = value, saved = false) }
    }

    fun setUsername(value: String) {
        _uiState.update { it.copy(username = value, saved = false) }
    }

    fun setPassword(value: String) {
        _uiState.update { it.copy(password = value, saved = false) }
    }

    fun save() {
        val current = _uiState.value
        if (!current.canSave) return
        viewModelScope.launch {
            dataStore.save(current.toSettings())
            syncSettingsNow()
            _uiState.update { it.copy(saved = true, hasSavedConnection = true) }
        }
    }

    fun requestAuthorization() {
        val current = _uiState.value
        if (!current.canSave) return
        viewModelScope.launch {
            dataStore.save(current.toSettings())
            syncSettingsNow()
            _uiState.update { it.copy(isLoading = true, statusMessage = "Demande d'autorisation envoyee a la Freebox...") }
            freeboxClient.requestAuthorization(current.toSettings())
                .onSuccess { update ->
                    dataStore.saveAuthorization(update)
                    syncSettingsNow()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            hasAppToken = true,
                            appToken = update.appToken,
                            authTrackId = update.trackId,
                            authStatus = update.status,
                            statusMessage = "Autorisation en attente. Valide JojoTV sur l'ecran de la Freebox, puis verifie le statut."
                        )
                    }
                }
                .onFailure { error -> updateError(error) }
        }
    }

    fun refreshAuthorizationStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, statusMessage = null) }
            freeboxClient.refreshAuthorizationStatus(_uiState.value.toSettings())
                .onSuccess { status ->
                    dataStore.saveAuthStatus(status)
                    syncSettingsNow()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            authStatus = status,
                            statusMessage = if (status == "granted") {
                                "Autorisation accordee. Connexion et chargement des dossiers..."
                            } else {
                                "Statut Freebox: $status"
                            }
                        )
                    }
                    if (status == "granted") {
                        connectAndLoadFolders()
                    }
                }
                .onFailure { error -> updateError(error) }
        }
    }

    fun connectAndLoadFolders() {
        viewModelScope.launch {
            val settings = _uiState.value.toSettings()
            _uiState.update { it.copy(isLoading = true, statusMessage = "Connexion a la Freebox...") }
            val connected = freeboxClient.openSession(settings)
                .onSuccess { update ->
                    dataStore.saveSession(update)
                    syncSettingsNow()
                }
            if (connected.isFailure) {
                updateError(connected.exceptionOrNull() ?: IllegalStateException("Connexion Freebox impossible."))
                return@launch
            }
            val sessionSettings = settings.copy(sessionToken = connected.getOrThrow().sessionToken)
            freeboxClient.loadRootFolders(sessionSettings)
                .onSuccess { update ->
                    dataStore.saveServerFolders(update)
                    syncSettingsNow()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            hasSession = true,
                            sessionToken = sessionSettings.sessionToken,
                            serverFolders = update.folders.toList().sortedWith(String.CASE_INSENSITIVE_ORDER),
                            visibleServerFolders = update.visibleFolders,
                            sidebarServerFolders = update.sidebarFolders,
                            statusMessage = if (update.folders.isEmpty()) {
                                "Connexion OK, mais aucun dossier racine n'a ete retourne."
                            } else {
                                "Dossiers Freebox charges."
                            }
                        )
                    }
                }
                .onFailure { error -> updateError(error) }
        }
    }

    fun setShowInSidebar(enabled: Boolean) {
        _uiState.update { it.copy(showInSidebar = enabled, saved = false) }
        viewModelScope.launch {
            dataStore.setShowInSidebar(enabled)
            syncSettingsNow()
        }
    }

    fun setShowSourceFolder(enabled: Boolean) {
        _uiState.update { it.copy(showSourceFolder = enabled, saved = false) }
        viewModelScope.launch {
            dataStore.setShowSourceFolder(enabled)
            syncSettingsNow()
        }
    }

    fun setShowExtensions(enabled: Boolean) {
        _uiState.update { it.copy(showExtensions = enabled, saved = false) }
        viewModelScope.launch {
            dataStore.setShowExtensions(enabled)
            syncSettingsNow()
        }
    }

    fun setShowHiddenFiles(enabled: Boolean) {
        _uiState.update { it.copy(showHiddenFiles = enabled, saved = false) }
        viewModelScope.launch {
            dataStore.setShowHiddenFiles(enabled)
            syncSettingsNow()
        }
    }

    fun setServerFolderVisible(folderName: String, visible: Boolean) {
        _uiState.update { current ->
            current.copy(
                visibleServerFolders = if (visible) current.visibleServerFolders + folderName else current.visibleServerFolders - folderName,
                saved = false
            )
        }
        viewModelScope.launch {
            dataStore.setVisibleServerFolder(folderName, visible)
            syncSettingsNow()
        }
    }

    fun setServerFolderInSidebar(folderName: String, visible: Boolean) {
        _uiState.update { current ->
            current.copy(
                sidebarServerFolders = if (visible) current.sidebarServerFolders + folderName else current.sidebarServerFolders - folderName,
                saved = false
            )
        }
        viewModelScope.launch {
            dataStore.setSidebarServerFolder(folderName, visible)
            syncSettingsNow()
        }
    }

    private suspend fun syncSettingsNow() {
        profileSettingsSyncService.pushCurrentProfileToRemote()
    }

    private fun updateError(error: Throwable) {
        _uiState.update {
            it.copy(
                isLoading = false,
                statusMessage = error.message ?: "Erreur Freebox inconnue."
            )
        }
    }

    private fun FreeboxSettingsUiState.toSettings(): FreeboxConnectionSettings = FreeboxConnectionSettings(
        name = name,
        address = address,
        username = username,
        password = password,
        showInSidebar = showInSidebar,
        showSourceFolder = showSourceFolder,
        showExtensions = showExtensions,
        showHiddenFiles = showHiddenFiles,
        visibleServerFolders = visibleServerFolders,
        sidebarServerFolders = sidebarServerFolders,
        serverFoldersConfigured = serverFolders.isNotEmpty(),
        appToken = appToken,
        sessionToken = sessionToken,
        authTrackId = authTrackId,
        authStatus = authStatus,
        serverFolders = serverFolders.toSet()
    )
}

data class FreeboxSettingsUiState(
    val name: String = "",
    val address: String = "",
    val username: String = "",
    val password: String = "",
    val showInSidebar: Boolean = false,
    val showSourceFolder: Boolean = false,
    val showExtensions: Boolean = true,
    val showHiddenFiles: Boolean = false,
    val visibleServerFolders: Set<String> = emptySet(),
    val sidebarServerFolders: Set<String> = emptySet(),
    val serverFolders: List<String> = emptyList(),
    val hasSavedConnection: Boolean = false,
    val hasAppToken: Boolean = false,
    val hasSession: Boolean = false,
    val authStatus: String = "",
    val statusMessage: String? = null,
    val isLoading: Boolean = false,
    val saved: Boolean = false,
    val appToken: String = "",
    val sessionToken: String = "",
    val authTrackId: Int = -1
) {
    val canSave: Boolean
        get() = name.isNotBlank() && address.isNotBlank()
}

