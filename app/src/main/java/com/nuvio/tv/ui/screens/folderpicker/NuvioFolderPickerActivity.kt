package com.nuvio.tv.ui.screens.folderpicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.ui.theme.NuvioTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

class NuvioFolderPickerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.action != Intent.ACTION_OPEN_DOCUMENT_TREE) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        setContent {
            var refreshKey by remember { mutableIntStateOf(0) }
            val storageAccess = remember(refreshKey) { NuvioDocumentsProvider.hasStorageAccess(this) }
            val roots = remember(refreshKey, storageAccess) {
                if (storageAccess) NuvioDocumentsProvider.storageRoots(this) else emptyList()
            }
            val settingsLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
            ) { refreshKey++ }
            val legacyPermissionsLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(),
            ) { refreshKey++ }

            NuvioTheme {
                NuvioFolderPickerScreen(
                    roots = roots,
                    storageAccess = storageAccess,
                    onCancel = {
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    },
                    onRequestStorageAccess = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val appSettings = Intent(
                                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                Uri.parse("package:$packageName"),
                            )
                            try {
                                settingsLauncher.launch(appSettings)
                            } catch (_: Exception) {
                                try {
                                    settingsLauncher.launch(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
                                } catch (_: Exception) {
                                    Toast.makeText(this, R.string.folder_picker_storage_settings_unavailable, Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            legacyPermissionsLauncher.launch(
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            )
                        }
                    },
                    onChooseFolder = { folder -> returnFolder(folder) },
                )
            }
        }
    }

    private fun returnFolder(folder: File) {
        runCatching {
            val documentId = NuvioDocumentsProvider.documentIdFor(this, folder)
            val uri = DocumentsContract.buildTreeDocumentUri(NuvioDocumentsProvider.documentsAuthority(this), documentId)
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
            setResult(Activity.RESULT_OK, Intent().setData(uri).addFlags(flags))
            finish()
        }.onFailure { error ->
            Toast.makeText(this, error.message ?: getString(R.string.folder_picker_selection_failed), Toast.LENGTH_LONG).show()
        }
    }
}

private data class FolderPickerLocation(
    val root: NuvioStorageRoot,
    val directory: File,
)

@androidx.compose.runtime.Composable
private fun NuvioFolderPickerScreen(
    roots: List<NuvioStorageRoot>,
    storageAccess: Boolean,
    onCancel: () -> Unit,
    onRequestStorageAccess: () -> Unit,
    onChooseFolder: (File) -> Unit,
) {
    var location by remember { mutableStateOf<FolderPickerLocation?>(null) }
    var folders by remember { mutableStateOf<List<File>>(emptyList()) }
    var listingError by remember { mutableStateOf<String?>(null) }
    val confirmFocusRequester = remember { FocusRequester() }

    LaunchedEffect(location) {
        if (location != null) {
            withFrameNanos { }
            runCatching { confirmFocusRequester.requestFocus() }
        }
    }

    LaunchedEffect(location, roots) {
        val current = location
        if (current == null) {
            folders = emptyList()
            listingError = null
        } else {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    current.directory.listFiles()
                        ?.asSequence()
                        ?.mapNotNull { child -> runCatching { child.canonicalFile }.getOrNull() }
                        ?.filter { child ->
                            val rootPath = current.root.directory.canonicalPath
                            val childPath = child.canonicalPath
                            val insideRoot = childPath == rootPath || childPath.startsWith("$rootPath${File.separator}")
                            if (!insideRoot) return@filter false
                            val relative = child.relativeTo(current.root.directory).path
                                .replace('\\', '/')
                                .lowercase(Locale.ROOT)
                            val protected = relative == "android/data" || relative.startsWith("android/data/") ||
                                relative == "android/obb" || relative.startsWith("android/obb/")
                            !protected && child.isDirectory && child.canRead()
                        }
                        ?.sortedBy { it.name.lowercase(Locale.ROOT) }
                        ?.toList()
                        .orEmpty()
                }
            }
            folders = result.getOrDefault(emptyList())
            listingError = result.exceptionOrNull()?.message
        }
    }

    BackHandler {
        val current = location
        if (current == null || current.directory == current.root.directory) {
            if (current == null) onCancel() else location = null
        } else {
            val parent = current.directory.parentFile?.canonicalFile
            location = if (parent != null) FolderPickerLocation(current.root, parent) else null
        }
    }

    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 56.dp, vertical = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.folder_picker_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = location?.let { current ->
                    val relative = current.directory.relativeTo(current.root.directory).path
                        .takeUnless { it == "." }
                        .orEmpty()
                    listOf(current.root.label, relative).filter(String::isNotBlank).joinToString(" / ")
                } ?: stringResource(R.string.folder_picker_select_location),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (!storageAccess) {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.folder_picker_storage_access_required),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = stringResource(R.string.folder_picker_storage_access_description),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
                    )
                    Button(onClick = onRequestStorageAccess) {
                        Text(stringResource(R.string.folder_picker_allow_storage))
                    }
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (location == null) {
                        Button(onClick = onCancel) { Text(stringResource(R.string.folder_picker_cancel)) }
                    } else {
                        Button(onClick = {
                            val current = location ?: return@Button
                            if (current.directory == current.root.directory) location = null
                            else location = current.directory.parentFile?.canonicalFile?.let { FolderPickerLocation(current.root, it) }
                        }) {
                            Text(stringResource(R.string.folder_picker_back))
                        }
                        Button(
                            onClick = { location?.directory?.let(onChooseFolder) },
                            enabled = location?.directory?.canWrite() == true,
                            modifier = Modifier.focusRequester(confirmFocusRequester),
                        ) {
                            Text(stringResource(R.string.folder_picker_use_this_folder))
                        }
                        Button(onClick = onCancel) { Text(stringResource(R.string.folder_picker_cancel)) }
                    }
                }

                if (location == null) {
                    Text(
                        text = stringResource(R.string.folder_picker_roots_hint),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(roots, key = { it.id }) { root ->
                            FolderPickerRow(root.label) {
                                location = FolderPickerLocation(root, root.directory)
                            }
                        }
                        if (roots.isEmpty()) {
                            item {
                                Text(
                                    stringResource(R.string.folder_picker_no_storage),
                                    modifier = Modifier.padding(top = 16.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                } else {
                    if (location?.directory?.canWrite() != true) {
                        Text(
                            stringResource(R.string.folder_picker_read_only),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    if (!listingError.isNullOrBlank()) {
                        Text(listingError.orEmpty(), color = MaterialTheme.colorScheme.error)
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(folders, key = { it.absolutePath }) { folder ->
                            FolderPickerRow(folder.name) {
                                location = location?.let { FolderPickerLocation(it.root, folder) }
                            }
                        }
                        if (folders.isEmpty() && listingError.isNullOrBlank()) {
                            item {
                                Text(
                                    stringResource(R.string.folder_picker_empty),
                                    modifier = Modifier.padding(top = 16.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun FolderPickerRow(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 58.dp),
    ) {
        Text(
            text = label,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
