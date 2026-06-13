package com.nuvio.tv.ui.screens.explorer

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.data.freebox.FreeboxFileEntry
import com.nuvio.tv.data.freebox.FreeboxOsClient
import com.nuvio.tv.data.local.FreeboxSettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class ExplorerViewModel @Inject constructor(
    private val freeboxSettingsDataStore: FreeboxSettingsDataStore,
    private val freeboxOsClient: FreeboxOsClient
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExplorerUiState())
    val uiState: StateFlow<ExplorerUiState> = _uiState.asStateFlow()

    init {
        refreshLocal()
        refreshFreebox()
    }

    fun setActivePanel(panel: ExplorerPanel) {
        _uiState.update { it.copy(activePanel = panel) }
    }

    fun selectEntry(entry: ExplorerEntry) {
        _uiState.update { it.copy(selectedEntry = entry, activePanel = entry.panel) }
    }

    fun toggleSelectionMode() {
        _uiState.update { state ->
            val nextMode = !state.selectionMode
            state.copy(
                selectionMode = nextMode,
                selectedEntryKeys = if (nextMode) state.selectedEntryKeys else emptySet()
            )
        }
    }

    fun toggleEntrySelection(entry: ExplorerEntry) {
        _uiState.update { state ->
            val nextKeys = state.selectedEntryKeys.toMutableSet().apply {
                if (!add(entry.selectionKey)) remove(entry.selectionKey)
            }
            state.copy(
                selectedEntry = entry,
                activePanel = entry.panel,
                selectionMode = true,
                selectedEntryKeys = nextKeys
            )
        }
    }

    fun openContextMenu(entry: ExplorerEntry? = _uiState.value.selectedEntry) {
        _uiState.update { it.copy(selectedEntry = entry, contextMenuEntry = entry) }
    }

    fun dismissContextMenu() {
        _uiState.update { it.copy(contextMenuEntry = null) }
    }

    fun requestRename() {
        val target = _uiState.value.contextMenuEntry ?: _uiState.value.selectedEntry ?: return
        _uiState.update { it.copy(renameEntry = target, renameValue = target.name, contextMenuEntry = null) }
    }

    fun updateRenameValue(value: String) {
        _uiState.update { it.copy(renameValue = value) }
    }

    fun cancelRename() {
        _uiState.update { it.copy(renameEntry = null, renameValue = "") }
    }

    fun confirmRename() {
        val state = _uiState.value
        val entry = state.renameEntry ?: return
        val newName = state.renameValue.trim()
        if (newName.isBlank() || newName == entry.name) {
            cancelRename()
            return
        }
        viewModelScope.launch {
            runFileAction(entry.panel) {
                when (entry.source) {
                    ExplorerSource.LOCAL -> renameLocal(entry, newName)
                    ExplorerSource.FREEBOX -> renameFreebox(entry, newName)
                }
            }
            _uiState.update { it.copy(renameEntry = null, renameValue = "") }
            refreshPanel(entry.panel)
        }
    }

    fun copySelected() {
        val state = _uiState.value
        val entries = state.actionEntries()
        if (entries.isEmpty()) return
        _uiState.update { it.copy(clipboard = ExplorerClipboard(entries, cut = false), contextMenuEntry = null) }
    }

    fun cutSelected() {
        val state = _uiState.value
        val entries = state.actionEntries()
        if (entries.isEmpty()) return
        _uiState.update { it.copy(clipboard = ExplorerClipboard(entries, cut = true), contextMenuEntry = null) }
    }

    fun pasteIntoActivePanel() {
        val clipboard = _uiState.value.clipboard ?: return
        val targetPanel = _uiState.value.activePanel
        viewModelScope.launch {
            runFileAction(targetPanel) {
                clipboard.entries.forEach { entry ->
                    when {
                        entry.source == ExplorerSource.LOCAL && targetPanel == ExplorerPanel.LOCAL -> {
                            pasteLocal(entry, File(_uiState.value.localPath), clipboard.cut)
                        }
                        entry.source == ExplorerSource.FREEBOX && targetPanel == ExplorerPanel.FREEBOX -> {
                            pasteFreebox(clipboard.entries, _uiState.value.freeboxPath, clipboard.cut)
                            return@runFileAction
                        }
                        else -> unsupportedMixedSourceWrite()
                    }
                }
            }
            if (clipboard.cut) _uiState.update { it.copy(clipboard = null, selectedEntryKeys = emptySet()) }
            refreshPanel(targetPanel)
            if (clipboard.cut) clipboard.entries.map { it.panel }.distinct().forEach { refreshPanel(it) }
        }
    }

    fun deleteSelected() {
        val state = _uiState.value
        val entries = state.actionEntries()
        if (entries.isEmpty()) return
        viewModelScope.launch {
            entries.groupBy { it.panel }.forEach { (panel, panelEntries) ->
                runFileAction(panel) {
                    panelEntries.forEach { entry ->
                        when (entry.source) {
                            ExplorerSource.LOCAL -> deleteLocal(entry)
                            ExplorerSource.FREEBOX -> deleteFreebox(panelEntries)
                        }
                    }
                }
                refreshPanel(panel)
            }
            _uiState.update { it.copy(contextMenuEntry = null, selectedEntry = null, selectedEntryKeys = emptySet()) }
        }
    }

    fun refreshLocal() {
        viewModelScope.launch {
            val path = _uiState.value.localPath.ifBlank { defaultLocalRoot() }
            _uiState.update { it.copy(localLoading = true, localError = null, localPath = path) }
            runCatching { loadLocalEntries(path) }
                .onSuccess { entries ->
                    _uiState.update { it.copy(localLoading = false, localEntries = entries, localPath = path) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(localLoading = false, localEntries = emptyList(), localError = error.message) }
                }
        }
    }

    fun openLocal(entry: ExplorerEntry) {
        selectEntry(entry)
        if (!entry.isDirectory) return
        _uiState.update { it.copy(localPath = entry.path) }
        refreshLocal()
    }

    fun localUp() {
        val parent = File(_uiState.value.localPath).parentFile?.absolutePath ?: return
        _uiState.update { it.copy(localPath = parent) }
        refreshLocal()
    }

    fun refreshFreebox() {
        viewModelScope.launch {
            val path = _uiState.value.freeboxPath.ifBlank { "/" }
            _uiState.update { it.copy(freeboxLoading = true, freeboxError = null, freeboxPath = path) }
            val settings = freeboxSettingsDataStore.settings.first()
            if (!settings.hasSavedConnection || settings.appToken.isBlank()) {
                _uiState.update {
                    it.copy(
                        freeboxLoading = false,
                        freeboxEntries = emptyList(),
                        freeboxError = "Configure et autorise la Freebox dans les parametres."
                    )
                }
                return@launch
            }
            freeboxOsClient.listDirectory(settings, path)
                .onSuccess { entries ->
                    _uiState.update {
                        it.copy(
                            freeboxLoading = false,
                            freeboxEntries = entries.map { entry ->
                                ExplorerEntry(
                                    name = entry.name,
                                    path = entry.path,
                                    encodedPath = entry.encodedPath,
                                    isDirectory = entry.isDirectory,
                                    size = entry.size,
                                    source = ExplorerSource.FREEBOX,
                                    panel = ExplorerPanel.FREEBOX
                                )
                            },
                            freeboxPath = path
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(freeboxLoading = false, freeboxEntries = emptyList(), freeboxError = error.message) }
                }
        }
    }

    fun openFreebox(entry: ExplorerEntry) {
        selectEntry(entry)
        if (!entry.isDirectory) return
        _uiState.update { it.copy(freeboxPath = entry.path) }
        refreshFreebox()
    }

    fun freeboxUp() {
        val current = _uiState.value.freeboxPath.trimEnd('/')
        if (current.isBlank() || current == "/") return
        val parent = current.substringBeforeLast('/', missingDelimiterValue = "/").ifBlank { "/" }
        _uiState.update { it.copy(freeboxPath = parent) }
        refreshFreebox()
    }

    private suspend fun runFileAction(panel: ExplorerPanel, action: suspend () -> Unit) {
        runCatching { action() }
            .onFailure { error -> setPanelError(panel, error.message ?: "Action impossible.") }
    }

    private fun setPanelError(panel: ExplorerPanel, message: String) {
        _uiState.update {
            when (panel) {
                ExplorerPanel.LOCAL -> it.copy(localError = message)
                ExplorerPanel.FREEBOX -> it.copy(freeboxError = message)
            }
        }
    }

    private fun refreshPanel(panel: ExplorerPanel) {
        when (panel) {
            ExplorerPanel.LOCAL -> refreshLocal()
            ExplorerPanel.FREEBOX -> refreshFreebox()
        }
    }

    private suspend fun renameLocal(entry: ExplorerEntry, newName: String) = withContext(Dispatchers.IO) {
        val source = File(entry.path)
        val target = File(source.parentFile, newName)
        require(!target.exists()) { "Un fichier existe deja avec ce nom." }
        require(source.renameTo(target)) { "Renommage impossible." }
    }

    private suspend fun deleteLocal(entry: ExplorerEntry) = withContext(Dispatchers.IO) {
        val file = File(entry.path)
        val deleted = if (file.isDirectory) file.deleteRecursively() else file.delete()
        require(deleted) { "Suppression impossible." }
    }

    private suspend fun pasteLocal(entry: ExplorerEntry, targetFolder: File, cut: Boolean) = withContext(Dispatchers.IO) {
        require(targetFolder.isDirectory) { "Le dossier cible est invalide." }
        val source = File(entry.path)
        val target = uniqueTarget(targetFolder, source.name)
        if (cut) {
            require(source.renameTo(target)) { "Deplacement impossible." }
        } else if (source.isDirectory) {
            source.copyRecursively(target, overwrite = false)
        } else {
            source.copyTo(target, overwrite = false)
        }
    }

    private fun uniqueTarget(folder: File, name: String): File {
        var target = File(folder, name)
        if (!target.exists()) return target
        val base = name.substringBeforeLast('.', name)
        val ext = name.substringAfterLast('.', "").takeIf { it != name }
        var index = 1
        while (target.exists()) {
            val candidate = if (ext.isNullOrBlank()) "$base ($index)" else "$base ($index).$ext"
            target = File(folder, candidate)
            index++
        }
        return target
    }

    private suspend fun renameFreebox(entry: ExplorerEntry, newName: String) {
        val settings = freeboxSettingsDataStore.settings.first()
        freeboxOsClient.rename(settings, entry.path, entry.encodedPath, newName).getOrThrow()
    }

    private suspend fun deleteFreebox(entries: List<ExplorerEntry>) {
        val settings = freeboxSettingsDataStore.settings.first()
        freeboxOsClient.deleteFiles(settings, entries.map { it.toFreeboxFileEntry() }).getOrThrow()
    }

    private suspend fun pasteFreebox(entries: List<ExplorerEntry>, destinationPath: String, cut: Boolean) {
        val settings = freeboxSettingsDataStore.settings.first()
        val files = entries.map { it.toFreeboxFileEntry() }
        if (cut) {
            freeboxOsClient.moveFiles(settings, files, destinationPath).getOrThrow()
        } else {
            freeboxOsClient.copyFiles(settings, files, destinationPath).getOrThrow()
        }
    }

    private fun ExplorerEntry.toFreeboxFileEntry(): FreeboxFileEntry = FreeboxFileEntry(
        name = name,
        path = path,
        encodedPath = encodedPath,
        isDirectory = isDirectory,
        size = size
    )

    private fun unsupportedMixedSourceWrite(): Nothing {
        error("Copie entre stockage local et Freebox non disponible ici. Utilise Explorer local/local ou Freebox/Freebox.")
    }

    private suspend fun loadLocalEntries(path: String): List<ExplorerEntry> = withContext(Dispatchers.IO) {
        val root = File(path)
        val files = root.listFiles().orEmpty()
        files
            .filter { it.exists() && !it.isHidden }
            .map { file ->
                ExplorerEntry(
                    name = file.name.ifBlank { file.absolutePath },
                    path = file.absolutePath,
                    isDirectory = file.isDirectory,
                    size = file.takeIf { it.isFile }?.length(),
                    source = ExplorerSource.LOCAL,
                    panel = ExplorerPanel.LOCAL
                )
            }
            .sortedWith(compareByDescending<ExplorerEntry> { it.isDirectory }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.name })
    }

    private fun ExplorerUiState.actionEntries(): List<ExplorerEntry> {
        if (selectionMode && selectedEntryKeys.isNotEmpty()) {
            return (localEntries + freeboxEntries).filter { it.selectionKey in selectedEntryKeys }
        }
        return listOfNotNull(contextMenuEntry ?: selectedEntry)
    }

    private fun defaultLocalRoot(): String =
        Environment.getExternalStorageDirectory().absolutePath
}

data class ExplorerUiState(
    val activePanel: ExplorerPanel = ExplorerPanel.LOCAL,
    val localPath: String = "",
    val localEntries: List<ExplorerEntry> = emptyList(),
    val localLoading: Boolean = false,
    val localError: String? = null,
    val freeboxPath: String = "/",
    val freeboxEntries: List<ExplorerEntry> = emptyList(),
    val freeboxLoading: Boolean = false,
    val freeboxError: String? = null,
    val selectedEntry: ExplorerEntry? = null,
    val contextMenuEntry: ExplorerEntry? = null,
    val clipboard: ExplorerClipboard? = null,
    val renameEntry: ExplorerEntry? = null,
    val renameValue: String = "",
    val selectionMode: Boolean = false,
    val selectedEntryKeys: Set<String> = emptySet()
)

data class ExplorerEntry(
    val name: String,
    val path: String,
    val encodedPath: String? = null,
    val isDirectory: Boolean,
    val size: Long? = null,
    val source: ExplorerSource,
    val panel: ExplorerPanel
) {
    val selectionKey: String get() = "${source.name}:$path"
}

data class ExplorerClipboard(
    val entries: List<ExplorerEntry>,
    val cut: Boolean
) {
    val entry: ExplorerEntry? get() = entries.firstOrNull()
}

enum class ExplorerPanel { LOCAL, FREEBOX }

enum class ExplorerSource { LOCAL, FREEBOX }
