package com.nuvio.tv.ui.screens.explorer

import android.view.KeyEvent as AndroidKeyEvent
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.content.FileProvider
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.ui.screens.settings.SettingsDetailHeader
import com.nuvio.tv.ui.screens.settings.SettingsGroupCard
import com.nuvio.tv.ui.screens.settings.SettingsVerticalScrollIndicators
import com.nuvio.tv.ui.theme.NuvioColors
import com.nuvio.tv.ui.util.rememberLongPressKeyTracker
import java.io.File

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ExplorerScreen(
    showBuiltInHeader: Boolean = true,
    viewModel: ExplorerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val manageStorageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            val intent = Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            manageStorageLauncher.launch(intent)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showBuiltInHeader) {
            SettingsDetailHeader(
                title = stringResource(R.string.explorer_title),
                subtitle = stringResource(R.string.explorer_subtitle)
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExplorerPanelCard(
                title = stringResource(R.string.explorer_local_panel),
                path = uiState.localPath,
                entries = uiState.localEntries,
                selectedEntry = uiState.selectedEntry,
                selectionMode = uiState.selectionMode,
                selectedEntryKeys = uiState.selectedEntryKeys,
                loading = uiState.localLoading,
                error = uiState.localError,
                active = uiState.activePanel == ExplorerPanel.LOCAL,
                panel = ExplorerPanel.LOCAL,
                modifier = Modifier.weight(1f).fillMaxSize(),
                onPanelFocused = { viewModel.setActivePanel(ExplorerPanel.LOCAL) },
                onSelected = viewModel::selectEntry,
                onToggleSelection = viewModel::toggleEntrySelection,
                onContextMenu = viewModel::openContextMenu,
                onOpen = { entry ->
                    if (!entry.isDirectory && openLocalDocumentOrApk(context, entry)) {
                        Unit
                    } else {
                        viewModel.openLocal(entry)
                    }
                },
                onUp = viewModel::localUp,
                onRefresh = viewModel::refreshLocal
            )

            ExplorerCentralMenu(
                selectionMode = uiState.selectionMode,
                selectedCount = uiState.selectedEntryKeys.size,
                clipboard = uiState.clipboard,
                onToggleSelectionMode = viewModel::toggleSelectionMode,
                onCut = viewModel::cutSelected,
                onCopy = viewModel::copySelected,
                onPaste = viewModel::pasteIntoActivePanel,
                onRename = viewModel::requestRename,
                onDelete = viewModel::deleteSelected
            )

            ExplorerPanelCard(
                title = stringResource(R.string.explorer_freebox_panel),
                path = uiState.freeboxPath,
                entries = uiState.freeboxEntries,
                selectedEntry = uiState.selectedEntry,
                selectionMode = uiState.selectionMode,
                selectedEntryKeys = uiState.selectedEntryKeys,
                loading = uiState.freeboxLoading,
                error = uiState.freeboxError,
                active = uiState.activePanel == ExplorerPanel.FREEBOX,
                panel = ExplorerPanel.FREEBOX,
                modifier = Modifier.weight(1f).fillMaxSize(),
                onPanelFocused = { viewModel.setActivePanel(ExplorerPanel.FREEBOX) },
                onSelected = viewModel::selectEntry,
                onToggleSelection = viewModel::toggleEntrySelection,
                onContextMenu = viewModel::openContextMenu,
                onOpen = viewModel::openFreebox,
                onUp = viewModel::freeboxUp,
                onRefresh = viewModel::refreshFreebox
            )
        }
    }

    uiState.contextMenuEntry?.let { entry ->
        ExplorerContextMenu(
            entryName = entry.name,
            onDismiss = viewModel::dismissContextMenu,
            onCut = viewModel::cutSelected,
            onCopy = viewModel::copySelected,
            onPaste = viewModel::pasteIntoActivePanel,
            onRename = viewModel::requestRename,
            onDelete = viewModel::deleteSelected
        )
    }

    uiState.renameEntry?.let { entry ->
        ExplorerRenameDialog(
            entryName = entry.name,
            value = uiState.renameValue,
            onValueChange = viewModel::updateRenameValue,
            onDismiss = viewModel::cancelRename,
            onConfirm = viewModel::confirmRename
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ExplorerCentralMenu(
    selectionMode: Boolean,
    selectedCount: Int,
    clipboard: ExplorerClipboard?,
    onToggleSelectionMode: () -> Unit,
    onCut: () -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    SettingsGroupCard(
        modifier = Modifier.width(312.dp).heightIn(min = 430.dp),
        title = "Actions"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            ExplorerCommandRow(
                title = if (selectionMode) "Sélection active" else "Sélectionner un fichier",
                icon = if (selectionMode) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                modifier = Modifier.fillMaxWidth(),
                onFocused = {},
                onClick = onToggleSelectionMode,
                onLongClick = onToggleSelectionMode,
                value = selectedCount.takeIf { it > 0 }?.toString()
            )
            ExplorerCommandRow("Couper", Icons.Default.ContentCut, Modifier.fillMaxWidth(), {}, onCut, onCut)
            ExplorerCommandRow("Copier", Icons.Default.ContentCopy, Modifier.fillMaxWidth(), {}, onCopy, onCopy)
            ExplorerCommandRow(
                title = "Coller",
                icon = Icons.Default.ContentPaste,
                modifier = Modifier.fillMaxWidth(),
                onFocused = {},
                onClick = onPaste,
                onLongClick = onPaste,
                value = clipboard?.let { if (it.cut) "Déplacer" else "Copier" }
            )
            ExplorerCommandRow("Renommer", Icons.Default.DriveFileRenameOutline, Modifier.fillMaxWidth(), {}, onRename, onRename)
            ExplorerCommandRow("Supprimer", Icons.Default.Delete, Modifier.fillMaxWidth(), {}, onDelete, onDelete)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ExplorerPanelCard(
    title: String,
    path: String,
    entries: List<ExplorerEntry>,
    selectedEntry: ExplorerEntry?,
    selectionMode: Boolean,
    selectedEntryKeys: Set<String>,
    loading: Boolean,
    error: String?,
    active: Boolean,
    panel: ExplorerPanel,
    modifier: Modifier,
    onPanelFocused: () -> Unit,
    onSelected: (ExplorerEntry) -> Unit,
    onToggleSelection: (ExplorerEntry) -> Unit,
    onContextMenu: (ExplorerEntry) -> Unit,
    onOpen: (ExplorerEntry) -> Unit,
    onUp: () -> Unit,
    onRefresh: () -> Unit
) {
    val listState = rememberLazyListState()
    val borderColor = if (active) NuvioColors.FocusRing else NuvioColors.Border

    SettingsGroupCard(
        modifier = modifier,
        title = title,
        subtitle = path.ifBlank { "/" }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(key = "${panel.name}_actions") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        ExplorerCommandRow(
                            title = stringResource(R.string.explorer_up),
                            icon = Icons.Default.KeyboardArrowUp,
                            modifier = Modifier.weight(1f),
                            onFocused = onPanelFocused,
                            onClick = onUp,
                            onLongClick = { },
                            compact = true
                        )
                        ExplorerCommandRow(
                            title = stringResource(R.string.explorer_refresh),
                            icon = Icons.Default.Refresh,
                            modifier = Modifier.weight(1f),
                            onFocused = onPanelFocused,
                            onClick = onRefresh,
                            onLongClick = { },
                            compact = true
                        )
                    }
                }

                if (loading) {
                    item(key = "${panel.name}_loading") {
                        ExplorerMessageRow(stringResource(R.string.explorer_loading), onPanelFocused)
                    }
                } else if (!error.isNullOrBlank()) {
                    item(key = "${panel.name}_error") {
                        ExplorerMessageRow(error, onPanelFocused, isError = true)
                    }
                } else if (entries.isEmpty()) {
                    item(key = "${panel.name}_empty") {
                        ExplorerMessageRow(stringResource(R.string.explorer_empty), onPanelFocused)
                    }
                } else {
                    items(entries, key = { it.source.name + it.path }) { entry ->
                        ExplorerEntryRow(
                            entry = entry,
                            selected = selectedEntry?.path == entry.path && selectedEntry.source == entry.source,
                            selectionMode = selectionMode,
                            checked = selectedEntryKeys.contains(entry.selectionKey),
                            activeBorderColor = borderColor,
                            onFocused = {
                                onPanelFocused()
                                onSelected(entry)
                            },
                            onClick = {
                                if (selectionMode) onToggleSelection(entry) else onOpen(entry)
                            },
                            onLongClick = { onContextMenu(entry) }
                        )
                    }
                }
            }
            SettingsVerticalScrollIndicators(state = listState)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ExplorerCommandRow(
    title: String,
    icon: ImageVector,
    modifier: Modifier,
    onFocused: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    value: String? = null,
    compact: Boolean = false
) {
    ExplorerCardRow(
        title = title,
        subtitle = null,
        value = value,
        icon = icon,
        modifier = modifier,
        compact = compact,
        onFocused = onFocused,
        onClick = onClick,
        onLongClick = onLongClick
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ExplorerEntryRow(
    entry: ExplorerEntry,
    selected: Boolean,
    selectionMode: Boolean,
    checked: Boolean,
    activeBorderColor: Color,
    onFocused: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val subtitle = if (entry.isDirectory) {
        stringResource(R.string.explorer_folder)
    } else {
        entry.size?.let(::formatBytes) ?: stringResource(R.string.explorer_file)
    }
    ExplorerCardRow(
        title = entry.name,
        subtitle = subtitle,
        value = if (entry.isDirectory) stringResource(R.string.explorer_open) else null,
        icon = if (entry.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
        leadingSelectionIcon = if (selectionMode) {
            if (checked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank
        } else {
            null
        },
        activeBorderColor = if (selected) NuvioColors.Secondary else activeBorderColor,
        onFocused = onFocused,
        onClick = onClick,
        onLongClick = onLongClick
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ExplorerMessageRow(
    message: String,
    onFocused: () -> Unit,
    isError: Boolean = false
) {
    ExplorerCardRow(
        title = message,
        subtitle = null,
        value = null,
        icon = if (isError) Icons.Default.CloudQueue else Icons.Default.Storage,
        onFocused = onFocused,
        onClick = {},
        onLongClick = {}
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ExplorerCardRow(
    title: String,
    subtitle: String?,
    value: String?,
    icon: ImageVector,
    leadingSelectionIcon: ImageVector? = null,
    modifier: Modifier = Modifier,
    activeBorderColor: Color = NuvioColors.FocusRing,
    compact: Boolean = false,
    onFocused: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    var longPressTriggered by remember { mutableStateOf(false) }
    val longPressKeyTracker = rememberLongPressKeyTracker()
    Card(
        onClick = {
            if (longPressTriggered) {
                longPressTriggered = false
            } else {
                onClick()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .onPreviewKeyEvent { event ->
                val native = event.nativeKeyEvent
                if (native.action == AndroidKeyEvent.ACTION_DOWN && native.keyCode == AndroidKeyEvent.KEYCODE_MENU) {
                    longPressTriggered = true
                    onLongClick()
                    return@onPreviewKeyEvent true
                }
                if (longPressKeyTracker.handle(native, ::isExplorerSelectKey) {
                        longPressTriggered = true
                        onLongClick()
                    }
                ) {
                    if (native.action == AndroidKeyEvent.ACTION_UP) longPressTriggered = false
                    return@onPreviewKeyEvent true
                }
                if (native.action == AndroidKeyEvent.ACTION_UP && longPressTriggered && isExplorerSelectKey(native.keyCode)) {
                    longPressTriggered = false
                    return@onPreviewKeyEvent true
                }
                false
            }
            .onFocusChanged { state ->
                if (state.isFocused && !focused) onFocused()
                focused = state.isFocused
            },
        colors = CardDefaults.colors(
            containerColor = NuvioColors.Background,
            focusedContainerColor = NuvioColors.Background
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(2.dp, activeBorderColor),
                shape = RoundedCornerShape(12.dp)
            )
        ),
        shape = CardDefaults.shape(RoundedCornerShape(12.dp)),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f)
    ) {
        if (compact) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NuvioColors.TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = NuvioColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingSelectionIcon != null) {
                    Icon(
                        imageVector = leadingSelectionIcon,
                        contentDescription = null,
                        tint = NuvioColors.FocusRing,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NuvioColors.TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = NuvioColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = NuvioColors.TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (!value.isNullOrBlank()) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodySmall,
                        color = NuvioColors.TextSecondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ExplorerContextMenu(
    entryName: String,
    onDismiss: () -> Unit,
    onCut: () -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { androidx.compose.material3.Text(entryName) },
        text = {
            Column {
                ContextAction("Couper", onCut)
                ContextAction("Copier", onCopy)
                ContextAction("Coller", onPaste)
                ContextAction("Renommer", onRename)
                ContextAction("Supprimer", onDelete)
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { androidx.compose.material3.Text("Fermer") } }
    )
}

@Composable
private fun ContextAction(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        androidx.compose.material3.Text(label)
    }
}

@Composable
private fun ExplorerRenameDialog(
    entryName: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { androidx.compose.material3.Text("Renommer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                androidx.compose.material3.Text(entryName)
                OutlinedTextField(value = value, onValueChange = onValueChange, singleLine = true)
            }
        },
        confirmButton = { TextButton(onClick = onConfirm) { androidx.compose.material3.Text("Valider") } },
        dismissButton = { TextButton(onClick = onDismiss) { androidx.compose.material3.Text("Annuler") } }
    )
}

private fun formatBytes(bytes: Long): String {
    if (bytes < 1024L) return "$bytes B"
    val units = listOf("KB", "MB", "GB", "TB")
    var value = bytes / 1024.0
    var unitIndex = 0
    while (value >= 1024.0 && unitIndex < units.lastIndex) {
        value /= 1024.0
        unitIndex++
    }
    return "%.1f %s".format(value, units[unitIndex])
}


private fun openLocalDocumentOrApk(context: android.content.Context, entry: ExplorerEntry): Boolean {
    val extension = entry.name.substringAfterLast('.', "").lowercase()
    if (extension != "pdf" && extension != "apk") return false

    val file = File(entry.path)
    if (!file.exists()) {
        Toast.makeText(context, "Fichier introuvable", Toast.LENGTH_SHORT).show()
        return true
    }

    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
    val mimeType = when (extension) {
        "apk" -> "application/vnd.android.package-archive"
        else -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "application/pdf"
    }
    val action = if (extension == "apk") Intent.ACTION_VIEW else Intent.ACTION_VIEW
    val intent = Intent(action).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    runCatching { context.startActivity(intent) }
        .onFailure { Toast.makeText(context, "Aucune application compatible", Toast.LENGTH_SHORT).show() }
    return true
}

private fun isExplorerSelectKey(keyCode: Int): Boolean =
    keyCode == AndroidKeyEvent.KEYCODE_DPAD_CENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_ENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_NUMPAD_ENTER
