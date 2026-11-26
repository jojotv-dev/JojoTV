package com.nuvio.tv.ui.screens.settings

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nuvio.tv.R
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.theme.NuvioTheme
import com.nuvio.vpn.android.R as VpnR
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private data class VpnPickerStorage(
    val directory: File,
    val label: String,
    val isPrimary: Boolean,
)

private data class VpnPickerRow(
    val key: String,
    val title: String,
    val subtitle: String,
    val action: Action,
    val file: File? = null,
    val storage: VpnPickerStorage? = null,
) {
    enum class Action { STORAGE, CHANGE_STORAGE, PARENT, DIRECTORY, FILE }
}

/** In-app TV file browser for Fire TV focus issues or devices without a system document picker. */
@Composable
internal fun VpnProfileFilePicker(
    onDismiss: () -> Unit,
    onFileSelected: (File) -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var storage by remember { mutableStateOf<VpnPickerStorage?>(null) }
    var directory by remember { mutableStateOf<File?>(null) }
    var storages by remember { mutableStateOf<List<VpnPickerStorage>>(emptyList()) }
    var files by remember { mutableStateOf<List<File>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var readError by remember { mutableStateOf(false) }
    val firstRowRequester = remember { FocusRequester() }

    LaunchedEffect(storage, directory) {
        loading = true
        readError = false
        try {
            if (storage == null) {
                storages = withContext(Dispatchers.IO) { mountedVpnPickerStorages(context) }
                files = emptyList()
            } else {
                val root = storage?.directory ?: return@LaunchedEffect
                val target = directory ?: root
                files = withContext(Dispatchers.IO) { listVpnPickerFiles(target, root) }
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            readError = true
            files = emptyList()
        } finally {
            loading = false
        }
    }

    val internalStorageLabel = stringResource(VpnR.string.vpn_picker_internal_storage)
    val removableStorageLabel = stringResource(VpnR.string.vpn_picker_removable_storage)
    val changeStorageLabel = stringResource(VpnR.string.vpn_picker_change_storage)
    val parentFolderLabel = stringResource(VpnR.string.vpn_picker_parent_folder)
    val folderLabel = stringResource(VpnR.string.vpn_picker_folder)
    val fileLabel = stringResource(VpnR.string.vpn_picker_file)
    val rows = if (storage == null) {
        storages.map { mounted ->
            VpnPickerRow(
                key = "storage:" + mounted.directory.path,
                title = if (mounted.isPrimary) internalStorageLabel else removableStorageLabel + ": " + mounted.label,
                subtitle = mounted.directory.path,
                action = VpnPickerRow.Action.STORAGE,
                storage = mounted,
            )
        }
    } else {
        buildList {
            add(
                VpnPickerRow(
                    key = "change-storage",
                    title = changeStorageLabel,
                    subtitle = "",
                    action = VpnPickerRow.Action.CHANGE_STORAGE,
                )
            )
            val root = storage?.directory
            val current = directory ?: root
            if (root != null && current != null && !samePath(root, current)) {
                add(
                    VpnPickerRow(
                        key = "parent:" + current.parent,
                        title = parentFolderLabel,
                        subtitle = current.parent ?: root.path,
                        action = VpnPickerRow.Action.PARENT,
                    )
                )
            }
            files.forEach { file ->
                val isDirectory = file.isDirectory
                add(
                    VpnPickerRow(
                        key = "entry:" + file.path,
                        title = file.name,
                        subtitle = if (isDirectory) folderLabel else fileLabel,
                        action = if (isDirectory) VpnPickerRow.Action.DIRECTORY else VpnPickerRow.Action.FILE,
                        file = file,
                    )
                )
            }
        }
    }
    val locationSubtitle = directory?.path
        ?: storage?.directory?.path
        ?: stringResource(VpnR.string.vpn_picker_choose_storage)

    LaunchedEffect(storage, directory, loading, rows.firstOrNull()?.key) {
        if (!loading && rows.isNotEmpty()) {
            withFrameNanos { }
            runCatching { firstRowRequester.requestFocus() }
        }
    }

    BackHandler {
        val root = storage?.directory
        val current = directory
        if (storage != null && root != null && current != null && !samePath(root, current)) {
            directory = current.parentFile ?: root
        } else if (storage != null) {
            storage = null
            directory = null
        } else {
            onDismiss()
        }
    }

    NuvioDialog(
        onDismiss = onDismiss,
        title = stringResource(VpnR.string.vpn_picker_title),
        subtitle = locationSubtitle,
        width = 720.dp,
    ) {
        when {
            loading -> SettingsNote(text = stringResource(VpnR.string.vpn_picker_loading))
            readError -> SettingsNote(
                text = stringResource(VpnR.string.vpn_picker_unavailable),
                tone = SettingsNoteTone.Danger,
            )
            rows.isEmpty() -> SettingsNote(
                text = stringResource(
                    if (storage == null) VpnR.string.vpn_picker_no_storage else VpnR.string.vpn_picker_empty
                )
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 430.dp),
                verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.xs),
            ) {
                itemsIndexed(rows, key = { _, row -> row.key }) { index, row ->
                    SettingsActionRow(
                        title = row.title,
                        subtitle = row.subtitle.takeIf(String::isNotBlank),
                        onClick = {
                            when (row.action) {
                                VpnPickerRow.Action.STORAGE -> {
                                    storage = row.storage
                                    directory = row.storage?.directory
                                }
                                VpnPickerRow.Action.CHANGE_STORAGE -> {
                                    storage = null
                                    directory = null
                                }
                                VpnPickerRow.Action.PARENT -> {
                                    val root = storage?.directory
                                    val parent = directory?.parentFile
                                    if (root != null && parent != null && isWithinStorage(parent, root)) {
                                        directory = parent
                                    }
                                }
                                VpnPickerRow.Action.DIRECTORY -> directory = row.file
                                VpnPickerRow.Action.FILE -> row.file?.let(onFileSelected)
                            }
                        },
                        modifier = if (index == 0) Modifier.focusRequester(firstRowRequester) else Modifier,
                    )
                }
            }
        }
        SettingsDialogActionRow {
            SettingsDialogActionButton(
                text = stringResource(R.string.action_cancel),
                onClick = onDismiss,
            )
        }
    }
}

private fun mountedVpnPickerStorages(context: Context): List<VpnPickerStorage> {
    val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as? StorageManager
    val volumes = runCatching { storageManager?.storageVolumes.orEmpty() }.getOrDefault(emptyList())
    val mounted = volumes.mapNotNull { volume ->
        if (
            (volume.state != Environment.MEDIA_MOUNTED &&
                volume.state != Environment.MEDIA_MOUNTED_READ_ONLY) ||
            (!volume.isPrimary && !volume.isRemovable)
        ) {
            return@mapNotNull null
        }
        val directory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            runCatching { volume.directory }.getOrNull()
        } else {
            legacyVpnPickerStorageRoot(context, storageManager, volume)
        }?.let { runCatching { it.canonicalFile }.getOrNull() } ?: return@mapNotNull null
        if (!directory.isDirectory || !directory.canRead()) return@mapNotNull null
        val label = if (volume.isPrimary) {
            ""
        } else {
            runCatching { volume.getDescription(context) }.getOrNull()?.trim()?.takeIf(String::isNotBlank)
                ?: "USB"
        }
        VpnPickerStorage(directory, label, volume.isPrimary)
    }
    val roots = if (mounted.none(VpnPickerStorage::isPrimary)) {
        @Suppress("DEPRECATION")
        val primary = runCatching { Environment.getExternalStorageDirectory().canonicalFile }.getOrNull()
        if (primary != null && primary.isDirectory && primary.canRead()) {
            mounted + VpnPickerStorage(primary, "", true)
        } else {
            mounted
        }
    } else {
        mounted
    }
    return roots.distinctBy { it.directory.path }
        .sortedWith(compareByDescending<VpnPickerStorage> { it.isPrimary }.thenBy { it.label.lowercase() })
}

@Suppress("DEPRECATION")
private fun legacyVpnPickerStorageRoot(
    context: Context,
    storageManager: StorageManager?,
    volume: StorageVolume,
): File? {
    val appDirectory = context.getExternalFilesDirs(null)
        .filterNotNull()
        .firstOrNull { directory ->
            runCatching {
                val mountedVolume = storageManager?.getStorageVolume(directory)
                mountedVolume != null &&
                    mountedVolume.uuid == volume.uuid &&
                    mountedVolume.isPrimary == volume.isPrimary
            }.getOrDefault(false)
        }
    val marker = File.separator + "Android" + File.separator + "data" + File.separator
    val appPath = appDirectory?.absolutePath
    val rootPath = appPath?.takeIf { it.contains(marker) }?.substringBefore(marker)?.takeIf(String::isNotBlank)
    if (rootPath != null) return File(rootPath)
    return Environment.getExternalStorageDirectory().takeIf { volume.isPrimary }
}

private fun listVpnPickerFiles(directory: File, root: File): List<File> {
    val current = directory.canonicalFile
    val canonicalRoot = root.canonicalFile
    if (!isWithinStorage(current, canonicalRoot) || !current.isDirectory || !current.canRead()) {
        throw SecurityException("Storage is unavailable.")
    }
    return (current.listFiles() ?: throw SecurityException("Folder is unavailable."))
        .asSequence()
        .filterNot { it.name.startsWith(".") }
        .filter { file ->
            runCatching { isWithinStorage(file.canonicalFile, canonicalRoot) }.getOrDefault(false) &&
                (file.isDirectory || file.isFile)
        }
        .sortedWith(compareByDescending<File> { it.isDirectory }.thenBy { it.name.lowercase() })
        .toList()
}

private fun samePath(first: File, second: File): Boolean =
    runCatching { first.canonicalFile == second.canonicalFile }.getOrDefault(false)

private fun isWithinStorage(file: File, root: File): Boolean {
    val rootPath = root.canonicalPath
    val filePath = file.canonicalPath
    return filePath == rootPath || filePath.startsWith(
        if (rootPath.endsWith(File.separator)) rootPath else rootPath + File.separator,
    )
}
