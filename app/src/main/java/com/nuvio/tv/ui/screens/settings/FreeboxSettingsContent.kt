package com.nuvio.tv.ui.screens.settings

import com.nuvio.tv.ui.theme.NuvioTheme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button as TvMaterialButton
import com.nuvio.tv.ui.components.NuvioButton as Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.nuvio.tv.data.freebox.FreeboxRepository
import com.nuvio.tv.data.freebox.FreeboxUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.withFrameNanos
import javax.inject.Inject

@HiltViewModel
class FreeboxSettingsViewModel @Inject constructor(
    private val repository: FreeboxRepository
) : ViewModel() {
    val state = repository.state
    init {
        viewModelScope.launch { repository.reconnectSaved() }
    }
    fun connect(address: String, username: String, password: String) {
        viewModelScope.launch { repository.connect(address, username, password) }
    }
    fun browse(path: String) { viewModelScope.launch { repository.browse(path) } }
    fun toggleRoot(path: String, selected: Boolean) {
        viewModelScope.launch { repository.toggleRoot(path, selected) }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FreeboxSettingsContent(
    initialFocusRequester: FocusRequester? = null,
    modifier: Modifier = Modifier.fillMaxSize(),
    viewModel: FreeboxSettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showHelp by remember { mutableStateOf(false) }
    var showConnectionSettings by remember { mutableStateOf(false) }
    var connectionWasBusy by remember { mutableStateOf(false) }
    val showConnectionForm = !state.connected || showConnectionSettings
    val firstFolderFocus = remember { FocusRequester() }
    val firstRootCheckboxFocus = remember { FocusRequester() }
    var address by remember(state.address) { mutableStateOf(state.address) }
    var username by remember(state.username) { mutableStateOf(state.username) }
    var password by remember(state.password) { mutableStateOf(state.password) }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = NuvioTheme.colors.TextPrimary,
        unfocusedTextColor = NuvioTheme.colors.TextPrimary,
        disabledTextColor = NuvioTheme.colors.TextDisabled,
        focusedLabelColor = NuvioTheme.colors.TextPrimary,
        unfocusedLabelColor = NuvioTheme.colors.TextSecondary,
        focusedPlaceholderColor = NuvioTheme.colors.TextSecondary,
        unfocusedPlaceholderColor = NuvioTheme.colors.TextSecondary,
        cursorColor = NuvioTheme.colors.Secondary,
        focusedBorderColor = NuvioTheme.colors.FocusRing,
        unfocusedBorderColor = NuvioTheme.colors.Border,
        focusedContainerColor = NuvioTheme.colors.Field,
        unfocusedContainerColor = NuvioTheme.colors.Field,
    )

    LaunchedEffect(initialFocusRequester, state.connected, showConnectionSettings, state.isBusy, state.currentPath, state.entries) {
        if (state.isBusy) {
            connectionWasBusy = true
        } else if (connectionWasBusy) {
            if (state.connected) showConnectionSettings = false
            connectionWasBusy = false
        }
        withFrameNanos { }
        runCatching {
            if (showConnectionForm) initialFocusRequester?.requestFocus()
            else if (state.entries.isNotEmpty() && state.currentPath.isBlank()) firstRootCheckboxFocus.requestFocus()
            else if (state.entries.isNotEmpty()) firstFolderFocus.requestFocus()
            else Unit
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (showConnectionForm) {
            item { Text("Connexion au NAS Freebox") }
            item {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Adresse SMB") },
                    placeholder = { Text("smb://192.168.1.254/Freebox") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.widthIn(max = 620.dp).fillMaxWidth().then(
                        if (initialFocusRequester != null) Modifier.focusRequester(initialFocusRequester) else Modifier
                    )
                )
            }
            item {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Login") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.widthIn(max = 620.dp).fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = fieldColors,
                    modifier = Modifier.widthIn(max = 620.dp).fillMaxWidth()
                )
            }
            item {
                Button(
                    onClick = { viewModel.connect(address, username, password) },
                    enabled = !state.isBusy && address.isNotBlank()
                ) { Text(if (state.isBusy) "Connexion…" else "Connexion") }
            }
            if (state.isBusy) item { Text("Connexion au NAS en cours…", color = NuvioTheme.colors.TextSecondary) }
            state.error?.let { message ->
                item { Text(message, color = NuvioTheme.colors.Error) }
            }
            item {
                Button(onClick = { showHelp = !showHelp }) {
                    Text(if (showHelp) "Masquer l’aide" else "Aide Freebox")
                }
            }
            if (showHelp) {
                item {
                    Text(
                        "Le partage Freebox est utilisé par défaut. Adresse complète : smb://192.168.1.254/Freebox. " +
                            "Après la connexion, descendez sur les dossiers pour les ouvrir ou les cocher.",
                        color = NuvioTheme.colors.TextSecondary,
                    )
                }
            }
        } else {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Connecté à la Freebox", color = NuvioTheme.colors.Success, modifier = Modifier.weight(1f))
                    Button(onClick = { showConnectionSettings = true }) { Text("Modifier") }
                }
            }
        }

        if (state.connected) {
            item { Text("Dossiers Freebox · cochez ceux à afficher sur l’accueil") }
            if (state.currentPath.isNotBlank()) {
                item {
                    Button(onClick = { viewModel.browse(state.currentPath.substringBeforeLast('/', "")) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                        Text("Retour")
                    }
                }
            }
            item { Text(if (state.currentPath.isBlank()) "Racine" else state.currentPath) }
            if (state.entries.isEmpty() && !state.isBusy) {
                item { Text("Aucun élément dans ce dossier.", color = NuvioTheme.colors.TextSecondary) }
            } else {
                items(state.entries, key = { it.path }) { entry ->
                    FreeboxEntryRow(
                        entry = entry,
                        state = state,
                        folderFocusRequester = firstFolderFocus.takeIf {
                            state.currentPath.isNotBlank() && entry == state.entries.firstOrNull()
                        },
                        checkboxFocusRequester = firstRootCheckboxFocus.takeIf {
                            state.currentPath.isBlank() && entry == state.entries.firstOrNull() && entry.isDirectory
                        },
                        onOpen = { viewModel.browse(entry.path) },
                        onChecked = { checked -> viewModel.toggleRoot(entry.path, checked) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FreeboxEntryRow(
    entry: com.nuvio.tv.data.freebox.FreeboxEntry,
    state: FreeboxUiState,
    folderFocusRequester: FocusRequester?,
    checkboxFocusRequester: FocusRequester?,
    onOpen: () -> Unit,
    onChecked: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onOpen, enabled = entry.isDirectory, modifier = Modifier.weight(1f).then(
            if (folderFocusRequester != null) Modifier.focusRequester(folderFocusRequester) else Modifier
        )) {
            Icon(if (entry.isDirectory) Icons.Default.Folder else Icons.Default.FolderOpen, contentDescription = null)
            Text(entry.name, modifier = Modifier.padding(start = 12.dp))
        }
        if (state.currentPath.isBlank() && entry.isDirectory) {
            val checked = entry.path in state.selectedRoots
            var focused by remember(entry.path) { mutableStateOf(false) }
            Box(
                modifier = Modifier.padding(start = 8.dp).size(48.dp)
                    .then(if (checkboxFocusRequester != null) Modifier.focusRequester(checkboxFocusRequester) else Modifier)
                    .onFocusChanged { focused = it.isFocused }
                    .clickable(role = Role.Checkbox) { onChecked(!checked) }
                    .border(
                        width = if (focused) 2.dp else 0.dp,
                        color = if (focused) NuvioTheme.colors.FocusRing else Color.Transparent,
                        shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp)),
                    )
                    .clip(NuvioTheme.configuredShape(RoundedCornerShape(10.dp))),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (checked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                    contentDescription = if (checked) "Dossier affiché sur l’accueil" else "Afficher le dossier sur l’accueil",
                    tint = when {
                        focused -> NuvioTheme.colors.FocusRing
                        checked -> NuvioTheme.colors.Secondary
                        else -> NuvioTheme.colors.TextSecondary
                    },
                )
            }
        }
    }
}
