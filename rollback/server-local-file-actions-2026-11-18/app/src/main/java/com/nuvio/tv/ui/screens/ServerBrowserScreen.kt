@file:OptIn(
    androidx.tv.material3.ExperimentalTvMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.compose.ui.ExperimentalComposeUiApi::class
)

package com.nuvio.tv.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.format.DateUtils
import android.view.KeyEvent as AndroidKeyEvent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.theme.NuvioTheme
import com.nuvio.tv.ui.util.rememberLongPressKeyTracker
import coil3.compose.AsyncImage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.widget.Toast

@Composable
fun ServerBrowserScreen(
    sourceId: String,
    path: String,
    viewModel: ServerBrowserViewModel = hiltViewModel(),
    capacityViewModel: ServerHubViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onChooseSource: (String) -> Unit,
    onOpenFolder: (String) -> Unit,
    onOpenFile: (ServerBrowserEntry) -> Unit,
    onPlayFromBeginning: (ServerBrowserEntry) -> Unit = onOpenFile,
    openPath: String = "",
    onManageServers: () -> Unit = onBack,
    onNavigateHome: () -> Unit = onBack,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val apkDownloadProgress by viewModel.apkDownloadProgress.collectAsStateWithLifecycle()
    val serverState by viewModel.servers.collectAsStateWithLifecycle()
    val freeboxState by viewModel.freebox.collectAsStateWithLifecycle()
    val capacityState by capacityViewModel.capacityState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    val requesters = remember(path) { mutableMapOf<String, FocusRequester>() }
    var focusedPath by rememberSaveable(sourceId, path) { mutableStateOf<String?>(null) }
    var optionsEntry by remember { mutableStateOf<ServerBrowserEntry?>(null) }
    var renameEntry by remember { mutableStateOf<ServerBrowserEntry?>(null) }
    var renameValue by remember { mutableStateOf("") }
    var deleteEntries by remember { mutableStateOf<List<ServerBrowserEntry>>(emptyList()) }
    var selectionMode by rememberSaveable(sourceId, path) { mutableStateOf(false) }
    val selectedPaths = remember(sourceId, path) { mutableStateListOf<String>() }
    var autoOpenedTarget by rememberSaveable(sourceId, openPath) { mutableStateOf(false) }
    val focusedEntry = remember(state.entries, focusedPath) {
        state.entries.firstOrNull { it.path == focusedPath } ?: state.entries.firstOrNull()
    }
    var detailPreviewUri by remember(sourceId) { mutableStateOf<String?>(null) }
    val actionScope = androidx.compose.runtime.rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var artworkTarget by remember { mutableStateOf<ServerBrowserEntry?>(null) }
    val artworkPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val target = artworkTarget
        if (uri != null && target != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            viewModel.saveCustomArtwork(sourceId, target, uri)
        }
        artworkTarget = null
    }
    fun selectedEntries(): List<ServerBrowserEntry> =
        state.entries.filter { it.path in selectedPaths }

    fun activateEntry(entry: ServerBrowserEntry) {
        if (selectionMode) {
            if (entry.path in selectedPaths) selectedPaths.remove(entry.path) else selectedPaths.add(entry.path)
        } else if (entry.isDirectory) {
            onOpenFolder(entry.path)
        } else {
            onOpenFile(entry)
        }
    }

    fun runRemoteAction(action: suspend () -> Unit, successMessage: String, reload: Boolean = true) {
        actionScope.launch {
            try {
                action()
                Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                if (reload) viewModel.load(sourceId, path)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Toast.makeText(context, error.message ?: "L’action n’a pas pu être effectuée.", Toast.LENGTH_LONG).show()
            }
        }
    }
    val localPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null && viewModel.saveLocalTree(uri)) viewModel.load(sourceId, "")
    }
    val hasDocumentTreePicker = remember(context.packageManager) {
        val activityInfo = runCatching {
            context.packageManager.resolveActivity(
                Intent(Intent.ACTION_OPEN_DOCUMENT_TREE),
                PackageManager.MATCH_DEFAULT_ONLY,
            )?.activityInfo
        }.getOrNull()
        activityInfo != null &&
            !activityInfo.packageName.contains("frameworkpackagestubs", ignoreCase = true) &&
            !activityInfo.name.contains("DocumentsStub", ignoreCase = true)
    }
    val allFilesSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        val accessGranted = viewModel.hasLocalStorageAccess()
        if (accessGranted && !hasDocumentTreePicker) viewModel.clearLocalTree()
        viewModel.load(sourceId, if (accessGranted && !hasDocumentTreePicker) "" else path)
    }
    val legacyStoragePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        val accessGranted = viewModel.hasLocalStorageAccess()
        if (accessGranted && !hasDocumentTreePicker) viewModel.clearLocalTree()
        viewModel.load(sourceId, if (accessGranted && !hasDocumentTreePicker) "" else path)
    }
    fun requestLocalStorageAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val appSettingsIntent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            try {
                allFilesSettingsLauncher.launch(appSettingsIntent)
            } catch (_: Exception) {
                try {
                    allFilesSettingsLauncher.launch(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
                } catch (_: Exception) {
                    try {
                        allFilesSettingsLauncher.launch(Intent(Settings.ACTION_SETTINGS))
                    } catch (_: Exception) {
                        Toast.makeText(
                            context,
                            "Ouvrez Paramètres Android > Applications > Accès spécial > Accès à tous les fichiers, puis autorisez JojoTV.",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
            }
        } else {
            legacyStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
    fun openLocalStorage() {
        when {
            hasDocumentTreePicker -> localPicker.launch(state.localTreeUri?.let(Uri::parse))
            state.localStorageAccessGranted -> {
                viewModel.clearLocalTree()
                viewModel.load(sourceId, "")
            }
            else -> requestLocalStorageAccess()
        }
    }

    val moduleNavEntries = buildList {
        add(
            ServerModuleNavEntry(
                id = ServerBrowserViewModel.LOCAL_SOURCE,
                label = "Local / USB",
                icon = Icons.Default.Folder,
                onClick = { onChooseSource(ServerBrowserViewModel.LOCAL_SOURCE) },
            ),
        )
        if (freeboxState.address.isNotBlank() || sourceId == ServerBrowserViewModel.FREEBOX_SOURCE) {
            add(
                ServerModuleNavEntry(
                    id = ServerBrowserViewModel.FREEBOX_SOURCE,
                    label = "Freebox",
                    icon = Icons.Default.Dns,
                    onClick = { onChooseSource(ServerBrowserViewModel.FREEBOX_SOURCE) },
                ),
            )
        }
        serverState.enabledConnections.forEach { connection ->
            add(
                ServerModuleNavEntry(
                    id = connection.id,
                    label = connection.name,
                    icon = Icons.Default.Storage,
                    onClick = { onChooseSource(connection.id) },
                ),
            )
        }
        if (none { it.id == sourceId }) {
            add(
                ServerModuleNavEntry(
                    id = sourceId,
                    label = state.sourceName,
                    icon = Icons.Default.Storage,
                    onClick = { onChooseSource(sourceId) },
                ),
            )
        }
    }
    val storageText = when (sourceId) {
        ServerBrowserViewModel.LOCAL_SOURCE -> capacityState.local
        ServerBrowserViewModel.FREEBOX_SOURCE -> capacityState.freebox
        else -> capacityState.servers[sourceId]
    }

    BackHandler(onBack = onBack)
    LaunchedEffect(sourceId) { capacityViewModel.refreshStorageCapacity() }
    LaunchedEffect(sourceId, path, openPath) {
        viewModel.load(sourceId, path)
        val loadedState = viewModel.state.first { !it.isLoading }
        if (sourceId == ServerBrowserViewModel.LOCAL_SOURCE &&
            loadedState.localTreeUri == null && !loadedState.localStorageAccessGranted
        ) return@LaunchedEffect
        if (loadedState.entries.isEmpty()) return@LaunchedEffect
        if (openPath.isNotBlank() && !autoOpenedTarget) {
            loadedState.entries.firstOrNull { it.path == openPath }?.let { target ->
                autoOpenedTarget = true
                if (target.isDirectory) onOpenFolder(target.path) else onOpenFile(target)
                return@LaunchedEffect
            }
        }
        val targetPath = focusedPath ?: loadedState.entries.first().path
        val targetIndex = loadedState.entries.indexOfFirst { it.path == targetPath }
        if (targetIndex >= 0 && gridState.layoutInfo.visibleItemsInfo.none { it.index == targetIndex }) {
            gridState.scrollToItem(targetIndex)
        }
        repeat(2) { withFrameNanos { } }
        runCatching { requesters[targetPath]?.requestFocus() }
    }

    LaunchedEffect(sourceId, focusedEntry?.path, state.artworkRevision) {
        val customArtwork = focusedEntry?.let { viewModel.customArtworkUri(sourceId, it) }
        detailPreviewUri = customArtwork?.toString()
        val entry = focusedEntry ?: return@LaunchedEffect
        if (customArtwork != null || entry.fileType != ServerBrowserFileType.IMAGE) return@LaunchedEffect
        delay(180L)
        detailPreviewUri = runCatching {
            viewModel.prepareThumbnailUri(sourceId, entry)?.toString()
        }.getOrNull()
    }

    Box(Modifier.fillMaxSize()) {
        ServerModuleScaffold(
            title = state.sourceName,
            subtitle = state.location.ifBlank { "Parcourir les dossiers et fichiers" },
            status = when {
                state.error != null -> "Indisponible"
                state.isLoading -> "Connexion…"
                state.location.isNotBlank() -> "Connecté"
                else -> "Prêt"
            },
            navEntries = moduleNavEntries,
            selectedNavId = sourceId,
            storageTitle = "Stockage ${state.sourceName}",
            storageText = storageText,
            onNavigateHome = onNavigateHome,
            onManageServers = onManageServers,
            headerActions = {
                if (sourceId == ServerBrowserViewModel.LOCAL_SOURCE) {
                    ServerModuleActionButton(
                        label = when {
                            hasDocumentTreePicker -> "Choisir un dossier"
                            state.localStorageAccessGranted -> "Choisir un stockage"
                            else -> "Autoriser l’accès local / USB"
                        },
                        icon = Icons.Default.Folder,
                        onClick = { openLocalStorage() },
                        modifier = Modifier.width(210.dp),
                    )
                }
                ServerModuleActionButton(
                    label = "Actualiser",
                    icon = Icons.Default.Refresh,
                    onClick = { viewModel.load(sourceId, path) },
                    modifier = Modifier.width(104.dp),
                )
                ServerModuleActionButton(
                    label = "Grille",
                    icon = Icons.Default.GridView,
                    onClick = { if (!state.isGridView) viewModel.toggleGridView(sourceId, path) },
                    modifier = Modifier.width(88.dp),
                    emphasized = state.isGridView,
                )
                ServerModuleActionButton(
                    label = "Liste",
                    icon = Icons.Default.ViewList,
                    onClick = { if (state.isGridView) viewModel.toggleGridView(sourceId, path) },
                    modifier = Modifier.width(84.dp),
                    emphasized = !state.isGridView,
                )
                ServerModuleActionButton(
                    label = "Fichiers cachés",
                    icon = if (state.showHiddenFiles) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    onClick = { viewModel.toggleShowHiddenFiles(sourceId, path) },
                    modifier = Modifier.width(150.dp),
                    emphasized = state.showHiddenFiles,
                )
            },
            mainContent = {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (selectionMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "${selectedPaths.size} sélectionné(s)",
                                color = NuvioTheme.colors.TextPrimary,
                                modifier = Modifier.weight(1f),
                            )
                            BrowserActionCard(Icons.Default.Folder, "Copier", {
                                if (selectedPaths.isNotEmpty()) viewModel.copyToClipboard(sourceId, selectedEntries(), cut = false)
                            }, modifier = Modifier.width(112.dp).height(46.dp), showLabel = true)
                            BrowserActionCard(Icons.Default.Folder, "Couper", {
                                if (selectedPaths.isNotEmpty()) viewModel.copyToClipboard(sourceId, selectedEntries(), cut = true)
                            }, modifier = Modifier.width(112.dp).height(46.dp), showLabel = true)
                            BrowserActionCard(Icons.Default.Folder, "Supprimer", {
                                deleteEntries = selectedEntries()
                            }, modifier = Modifier.width(132.dp).height(46.dp), showLabel = true)
                            BrowserActionCard(Icons.AutoMirrored.Filled.ArrowBack, "Terminer", {
                                selectionMode = false
                                selectedPaths.clear()
                            }, modifier = Modifier.width(118.dp).height(46.dp), showLabel = true)
                        }
                    }

                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        when {
                            sourceId == ServerBrowserViewModel.LOCAL_SOURCE &&
                                state.localTreeUri == null && !state.localStorageAccessGranted ->
                                BrowserMessage(
                                    title = if (hasDocumentTreePicker) "Parcourir un stockage local ou USB" else "Accès au stockage local / USB",
                                    description = if (hasDocumentTreePicker) {
                                        state.error ?: "Choisissez un dossier pour autoriser son accès à l’application."
                                    } else {
                                        "Ce téléviseur ne fournit pas de sélecteur de dossiers. Autorisez JojoTV dans Paramètres Android > Applications > Accès spécial > Accès à tous les fichiers pour parcourir le stockage interne et USB."
                                    },
                                    actionLabel = if (hasDocumentTreePicker) "Choisir un dossier" else "Autoriser l’accès",
                                    onAction = {
                                        if (hasDocumentTreePicker) localPicker.launch(state.localTreeUri?.let(Uri::parse))
                                        else requestLocalStorageAccess()
                                    },
                                )
                            state.isLoading && state.entries.isEmpty() ->
                                BrowserMessage("Lecture du serveur…", "Les dossiers et fichiers apparaîtront ici.")
                            state.error != null ->
                                BrowserMessage(
                                    title = "Impossible d’ouvrir ce serveur",
                                    description = state.error.orEmpty(),
                                    actionLabel = if (sourceId == ServerBrowserViewModel.LOCAL_SOURCE) {
                                        when {
                                            hasDocumentTreePicker -> "Choisir un autre dossier"
                                            state.localStorageAccessGranted -> "Rouvrir les stockages"
                                            else -> "Autoriser l’accès"
                                        }
                                    } else null,
                                    onAction = if (sourceId == ServerBrowserViewModel.LOCAL_SOURCE) ::openLocalStorage else null,
                                )
                            state.entries.isEmpty() -> BrowserMessage("Ce dossier est vide", "Aucun fichier ni sous-dossier n’a été trouvé.")
                            else -> LazyVerticalGrid(
                                state = gridState,
                                columns = if (state.isGridView) GridCells.Adaptive(142.dp) else GridCells.Fixed(1),
                                modifier = Modifier.fillMaxSize().focusRestorer {
                                    focusedPath?.let { requesters[it] } ?: FocusRequester.Default
                                },
                                contentPadding = PaddingValues(bottom = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                gridItems(state.entries, key = ServerBrowserEntry::path) { entry ->
                                    val requester = requesters.getOrPut(entry.path) { FocusRequester() }
                                    BrowserEntryCard(
                                        entry = entry,
                                        modifier = Modifier
                                            .focusRequester(requester)
                                            .onFocusChanged { if (it.isFocused) focusedPath = entry.path },
                                        isGridView = state.isGridView,
                                        isFocused = focusedPath == entry.path,
                                        isSelected = entry.path in selectedPaths,
                                        sourceId = sourceId,
                                        viewModel = viewModel,
                                        onClick = { activateEntry(entry) },
                                        onLongClick = { optionsEntry = entry },
                                    )
                                }
                            }
                        }
                    }
                }
            },
            detailsContent = {
                val entry = focusedEntry
                Text(
                    text = "DÉTAILS",
                    color = NuvioTheme.colors.TextTertiary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                if (entry == null) {
                    Text(
                        text = "Aucun élément dans ce dossier.",
                        color = NuvioTheme.colors.TextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                } else {
                    if (entry.fileType == ServerBrowserFileType.IMAGE && !detailPreviewUri.isNullOrBlank()) {
                        AsyncImage(
                            model = detailPreviewUri,
                            contentDescription = entry.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().height(138.dp)
                                .clip(NuvioTheme.configuredShape(RoundedCornerShape(12.dp))),
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(122.dp)
                                .clip(NuvioTheme.configuredShape(RoundedCornerShape(12.dp)))
                                .background(NuvioTheme.colors.SurfaceVariant),
                            contentAlignment = Alignment.Center,
                        ) {
                            androidx.tv.material3.Icon(
                                imageVector = if (entry.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                                contentDescription = null,
                                tint = NuvioTheme.colors.Secondary,
                                modifier = Modifier.size(48.dp),
                            )
                        }
                    }
                    Text(
                        text = entry.name,
                        color = NuvioTheme.colors.TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    ServerModuleInfoRow("Type", entry.fileType.label)
                    ServerModuleInfoRow("Emplacement", entry.path)
                    if (entry.sizeBytes > 0L) ServerModuleInfoRow("Taille", formatBrowserSize(entry.sizeBytes))
                    if (entry.lastModifiedMs > 0L) {
                        ServerModuleInfoRow(
                            "Modifié",
                            DateUtils.formatDateTime(
                                context,
                                entry.lastModifiedMs,
                                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_YEAR,
                            ),
                        )
                    }
                    androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                    ServerModuleActionButton(
                        label = if (entry.isDirectory) "Ouvrir le dossier" else "Ouvrir",
                        icon = Icons.Default.FolderOpen,
                        onClick = { activateEntry(entry) },
                        modifier = Modifier.fillMaxWidth(),
                        emphasized = true,
                    )
                    if (sourceId != ServerBrowserViewModel.LOCAL_SOURCE) {
                        ServerModuleActionButton(
                            label = "Plus d’actions",
                            icon = Icons.Default.MoreVert,
                            onClick = { optionsEntry = entry },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            },
        )

        apkDownloadProgress?.let { progress ->
            ApkDownloadProgressOverlay(
                progress = progress,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 22.dp)
                    .widthIn(max = 560.dp)
                    .fillMaxWidth(),
            )
        }
    }

    optionsEntry?.let { entry ->
        ServerBrowserEntryOptionsDialog(
            entry = entry,
            isFreebox = sourceId == ServerBrowserViewModel.FREEBOX_SOURCE,
            supportsExport = sourceId != ServerBrowserViewModel.LOCAL_SOURCE,
            supportsWrite = viewModel.supportsWriteActions(sourceId),
            hasClipboard = state.hasClipboard,
            clipboardIsCut = state.clipboardIsCut,
            onDismiss = { optionsEntry = null },
            onOpen = {
                optionsEntry = null
                if (entry.isDirectory) onOpenFolder(entry.path) else onOpenFile(entry)
            },
            onStartFromBeginning = {
                optionsEntry = null
                onPlayFromBeginning(entry)
            },
            onExport = {
                optionsEntry = null
                runRemoteAction({
                    val uri = viewModel.exportAsZip(sourceId, entry)
                    val share = Intent(Intent.ACTION_SEND).apply {
                        type = "application/zip"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(share, "Exporter en ZIP"))
                }, "Archive ZIP prête.", reload = false)
            },
            onCopy = {
                viewModel.copyToClipboard(sourceId, listOf(entry), cut = false)
                optionsEntry = null
                Toast.makeText(context, "Élément copié dans le presse-papiers.", Toast.LENGTH_SHORT).show()
            },
            onCut = {
                viewModel.copyToClipboard(sourceId, listOf(entry), cut = true)
                optionsEntry = null
                Toast.makeText(context, "Élément prêt à être déplacé.", Toast.LENGTH_SHORT).show()
            },
            onPaste = {
                optionsEntry = null
                runRemoteAction({ viewModel.pasteClipboard(sourceId, entry.path) }, "Collage terminé.")
            },
            onChangeThumbnail = {
                artworkTarget = entry
                optionsEntry = null
                artworkPicker.launch(arrayOf("image/*"))
            },
            onSetWallpaper = {
                optionsEntry = null
                runRemoteAction({
                    val imageUri = viewModel.prepareVideoFrameUri(sourceId, entry)
                    val wallpaperIntent = Intent(Intent.ACTION_ATTACH_DATA).apply {
                        setDataAndType(imageUri, "image/jpeg")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(wallpaperIntent, "Définir comme fond d’écran"))
                }, "Image envoyée au sélecteur de fond d’écran.", reload = false)
            },
            onPinToHome = {
                viewModel.pinToHome(sourceId, entry)
                optionsEntry = null
                Toast.makeText(context, "Ajouté à l’écran d’accueil.", Toast.LENGTH_SHORT).show()
            },
            onSelectMultiple = {
                selectionMode = true
                if (entry.path !in selectedPaths) selectedPaths.add(entry.path)
                optionsEntry = null
            },
            onRename = {
                renameValue = entry.name
                renameEntry = entry
                optionsEntry = null
            },
            onRemoveFromList = {
                optionsEntry = null
                runRemoteAction({ viewModel.removeFromList(sourceId, entry) }, "Vidéo retirée de la liste.", reload = false)
            },
            onDelete = {
                deleteEntries = listOf(entry)
                optionsEntry = null
            },
        )
    }

    renameEntry?.let { entry ->
        RenameServerBrowserEntryDialog(
            oldName = entry.name,
            value = renameValue,
            onValueChange = { renameValue = it },
            onDismiss = { renameEntry = null },
            onConfirm = {
                renameEntry = null
                runRemoteAction({ viewModel.renameEntry(sourceId, entry, renameValue) }, "Élément renommé.")
            },
        )
    }

    if (deleteEntries.isNotEmpty()) {
        ConfirmDeleteServerBrowserEntriesDialog(
            entries = deleteEntries,
            isFreebox = sourceId == ServerBrowserViewModel.FREEBOX_SOURCE,
            onDismiss = { deleteEntries = emptyList() },
            onConfirm = {
                val targets = deleteEntries
                deleteEntries = emptyList()
                runRemoteAction({ targets.forEach { viewModel.deleteEntry(sourceId, it) } }, "Élément(s) supprimé(s).")
                selectedPaths.removeAll(targets.map(ServerBrowserEntry::path).toSet())
                if (selectedPaths.isEmpty()) selectionMode = false
            },
        )
    }
}

@Composable
private fun ApkDownloadProgressOverlay(
    progress: ServerBrowserApkDownloadProgress,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .background(NuvioTheme.colors.BackgroundElevated, shape)
            .border(1.dp, NuvioTheme.colors.Secondary, shape)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Préparation de l’installation",
            style = MaterialTheme.typography.titleSmall,
            color = NuvioTheme.colors.TextPrimary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "${progress.percent} %  ·  ${progress.fileName}",
            style = MaterialTheme.typography.labelMedium,
            color = NuvioTheme.colors.TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(NuvioTheme.colors.FocusBackground),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.percent.coerceIn(0, 100) / 100f)
                    .fillMaxSize()
                    .background(NuvioTheme.colors.Secondary),
            )
        }
    }
}

@Composable
private fun BrowserActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    selected: Boolean = false,
) {
    val shape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp))
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.colors(
            containerColor = if (selected) NuvioTheme.colors.FocusBackground else NuvioTheme.colors.Secondary.copy(alpha = 0.12f),
            focusedContainerColor = NuvioTheme.colors.FocusBackground,
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs),
                shape = shape,
            ),
        ),
        shape = CardDefaults.shape(shape = shape),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            androidx.tv.material3.Icon(icon, contentDescription = label, tint = NuvioTheme.colors.Secondary)
            if (showLabel) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = NuvioTheme.colors.TextPrimary,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun BrowserEntryCard(
    entry: ServerBrowserEntry,
    modifier: Modifier,
    isGridView: Boolean,
    isFocused: Boolean,
    isSelected: Boolean,
    sourceId: String,
    viewModel: ServerBrowserViewModel,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
) {
    val shape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp))
    val artworkRevision = viewModel.state.value.artworkRevision
    val customArtwork = viewModel.customArtworkUri(sourceId, entry)
    val longPressTracker = rememberLongPressKeyTracker()
    var longPressTriggered by remember(sourceId, entry.path) { mutableStateOf(false) }
    var thumbnailUri by remember(sourceId, entry.path, entry.sizeBytes, entry.fileType, artworkRevision) {
        mutableStateOf(customArtwork ?: entry.uri?.let(Uri::parse))
    }
    LaunchedEffect(sourceId, entry.path, entry.sizeBytes, entry.fileType, artworkRevision) {
        thumbnailUri = viewModel.customArtworkUri(sourceId, entry) ?: if (entry.fileType == ServerBrowserFileType.IMAGE) {
            viewModel.prepareThumbnailUri(sourceId, entry)
        } else null
    }
    Card(
        onClick = {
            if (longPressTriggered) longPressTriggered = false else onClick()
        },
        modifier = modifier.fillMaxWidth().height(
            when {
                isGridView && thumbnailUri != null -> 174.dp
                isGridView -> 142.dp
                thumbnailUri != null -> 88.dp
                else -> 72.dp
            }
        ).onPreviewKeyEvent { event ->
            val native = event.nativeKeyEvent
            if (onLongClick == null) {
                false
            } else if (native.keyCode == AndroidKeyEvent.KEYCODE_MENU && native.action == AndroidKeyEvent.ACTION_DOWN) {
                longPressTriggered = true
                onLongClick?.invoke()
                true
            } else {
                longPressTracker.handle(native, ::isBrowserSelectKey) {
                    longPressTriggered = true
                    onLongClick?.invoke()
                }
            }
        },
        colors = CardDefaults.colors(
            containerColor = if (isSelected) NuvioTheme.colors.FocusBackground else NuvioTheme.colors.Secondary.copy(alpha = 0.12f),
            focusedContainerColor = NuvioTheme.colors.FocusBackground,
        ),
        border = CardDefaults.border(
            border = if (isSelected) Border(BorderStroke(1.dp, NuvioTheme.colors.Secondary)) else Border.None,
            focusedBorder = Border(
                border = NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs),
                shape = shape,
            ),
        ),
        shape = CardDefaults.shape(shape = shape),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
    ) {
        val entryIcon = when (entry.fileType) {
            ServerBrowserFileType.DIRECTORY -> if (isFocused) R.drawable.ic_file_folder_focused else R.drawable.ic_file_folder
            ServerBrowserFileType.VIDEO -> R.drawable.ic_file_video
            ServerBrowserFileType.IMAGE -> R.drawable.ic_file_image
            ServerBrowserFileType.PDF -> R.drawable.ic_file_pdf
            ServerBrowserFileType.TEXT -> R.drawable.ic_file_text
            ServerBrowserFileType.DOCUMENT -> R.drawable.ic_file_document
            ServerBrowserFileType.APK -> R.drawable.ic_file_apk
            ServerBrowserFileType.EXECUTABLE -> R.drawable.ic_file_exe
            ServerBrowserFileType.AUDIO -> R.drawable.ic_file_audio
            ServerBrowserFileType.ARCHIVE -> R.drawable.ic_file_archive
            ServerBrowserFileType.PLAYLIST -> R.drawable.ic_file_playlist
            ServerBrowserFileType.OTHER -> R.drawable.ic_file_generic
        }
        val iconColorFilter = if (entry.fileType == ServerBrowserFileType.DIRECTORY) {
            ColorFilter.tint(NuvioTheme.colors.Secondary)
        } else {
            null
        }
        val entryDetail = when {
            entry.isDirectory -> ServerBrowserFileType.DIRECTORY.label
            entry.sizeBytes > 0L -> "${entry.fileType.label} · ${formatBrowserSize(entry.sizeBytes)}"
            else -> entry.fileType.label
        }
        if (isGridView) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                if (thumbnailUri != null) {
                    AsyncImage(
                        model = thumbnailUri,
                        contentDescription = entry.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(84.dp).clip(NuvioTheme.configuredShape(RoundedCornerShape(8.dp))),
                    )
                } else {
                    Image(
                        painter = painterResource(entryIcon),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        colorFilter = iconColorFilter,
                    )
                }
                Text(
                    text = entry.name,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelLarge,
                    color = NuvioTheme.colors.TextPrimary,
                    textAlign = if (entry.isDirectory) TextAlign.Center else TextAlign.Start,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = entryDetail,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelSmall,
                    color = NuvioTheme.colors.TextSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (thumbnailUri != null) {
                    AsyncImage(
                        model = thumbnailUri,
                        contentDescription = entry.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(width = 76.dp, height = 56.dp)
                            .clip(NuvioTheme.configuredShape(RoundedCornerShape(8.dp))),
                    )
                } else {
                    Image(
                        painter = painterResource(entryIcon),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        colorFilter = iconColorFilter,
                    )
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = entry.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = NuvioTheme.colors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = entryDetail,
                        style = MaterialTheme.typography.labelSmall,
                        color = NuvioTheme.colors.TextSecondary,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun ServerBrowserEntryOptionsDialog(
    entry: ServerBrowserEntry,
    isFreebox: Boolean,
    supportsExport: Boolean,
    supportsWrite: Boolean,
    hasClipboard: Boolean,
    clipboardIsCut: Boolean,
    onDismiss: () -> Unit,
    onOpen: () -> Unit,
    onStartFromBeginning: () -> Unit,
    onExport: () -> Unit,
    onCopy: () -> Unit,
    onCut: () -> Unit,
    onPaste: () -> Unit,
    onChangeThumbnail: () -> Unit,
    onSetWallpaper: () -> Unit,
    onPinToHome: () -> Unit,
    onSelectMultiple: () -> Unit,
    onRename: () -> Unit,
    onRemoveFromList: () -> Unit,
    onDelete: () -> Unit,
) {
    val firstActionFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { firstActionFocus.requestFocus() }
    NuvioDialog(
        onDismiss = onDismiss,
        title = entry.name,
        subtitle = "Choisissez l’action à effectuer pour cet élément.",
    ) {
        Column(
            modifier = Modifier.heightIn(max = 520.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ServerBrowserMenuButton("Ouvrir", onOpen, Modifier.focusRequester(firstActionFocus))
            if (entry.fileType == ServerBrowserFileType.VIDEO) {
                ServerBrowserMenuButton("Reprendre au début", onStartFromBeginning)
            }
            if (supportsExport) {
                ServerBrowserMenuButton("Exporter en ZIP", onExport)
            }
            if (supportsWrite) {
                ServerBrowserMenuButton("Copier", onCopy)
                ServerBrowserMenuButton("Couper", onCut)
                if (entry.isDirectory) {
                    val pasteLabel = when {
                        !hasClipboard -> "Coller (vide)"
                        clipboardIsCut -> "Coller (déplacer)"
                        else -> "Coller (copier)"
                    }
                    ServerBrowserMenuButton(pasteLabel, onPaste, enabled = hasClipboard)
                }
            }
            if (entry.fileType == ServerBrowserFileType.VIDEO) {
                ServerBrowserMenuButton("Définir comme fond d’écran", onSetWallpaper)
            }
            if (entry.isDirectory || entry.fileType == ServerBrowserFileType.VIDEO) {
                ServerBrowserMenuButton("Changer de vignette", onChangeThumbnail)
            }
            ServerBrowserMenuButton("Placer sur l’écran d’accueil", onPinToHome)
            ServerBrowserMenuButton("Sélectionner plusieurs", onSelectMultiple)
            if (supportsWrite) {
                ServerBrowserMenuButton("Renommer", onRename)
            }
            if (entry.fileType == ServerBrowserFileType.VIDEO) {
                ServerBrowserMenuButton("Supprimer de la liste", onRemoveFromList)
            }
            if (supportsWrite) {
                ServerBrowserMenuButton(if (isFreebox) "Supprimer de la Freebox" else "Supprimer", onDelete)
            }
        }
    }
}

@Composable
private fun ServerBrowserMenuButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.colors(
            containerColor = NuvioTheme.colors.BackgroundCard,
            contentColor = NuvioTheme.colors.TextPrimary,
        ),
    ) {
        Text(label)
    }
}

@Composable
private fun RenameServerBrowserEntryDialog(
    oldName: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    NuvioDialog(onDismiss = onDismiss, title = "Renommer", subtitle = oldName) {
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { androidx.compose.material3.Text("Nouveau nom") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        ServerBrowserMenuButton("Valider", onConfirm)
        ServerBrowserMenuButton("Annuler", onDismiss)
    }
}

@Composable
private fun ConfirmDeleteServerBrowserEntriesDialog(
    entries: List<ServerBrowserEntry>,
    isFreebox: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val title = if (entries.size == 1) {
        if (isFreebox) "Supprimer de la Freebox ?" else "Supprimer cet élément ?"
    } else "Supprimer ${entries.size} éléments ?"
    NuvioDialog(
        onDismiss = onDismiss,
        title = title,
        subtitle = if (entries.size == 1) entries.single().name else "Cette suppression est définitive.",
    ) {
        ServerBrowserMenuButton("Supprimer", onConfirm)
        ServerBrowserMenuButton("Annuler", onDismiss)
    }
}

private fun isBrowserSelectKey(keyCode: Int): Boolean =
    keyCode == AndroidKeyEvent.KEYCODE_DPAD_CENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_ENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_NUMPAD_ENTER

@Composable
private fun BrowserMessage(
    title: String,
    description: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall, color = NuvioTheme.colors.TextPrimary)
        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = NuvioTheme.colors.TextSecondary,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
        )
        if (actionLabel != null && onAction != null) {
            val shape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp))
            Card(
                onClick = onAction,
                colors = CardDefaults.colors(
                    containerColor = NuvioTheme.colors.BackgroundCard,
                    focusedContainerColor = NuvioTheme.colors.FocusBackground,
                ),
                border = CardDefaults.border(
                    focusedBorder = Border(
                        border = NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs),
                        shape = shape,
                    ),
                ),
                shape = CardDefaults.shape(shape = shape),
                scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
            ) {
                Box(Modifier.padding(horizontal = 22.dp, vertical = 14.dp)) {
                    Text(actionLabel, style = MaterialTheme.typography.titleMedium, color = NuvioTheme.colors.TextPrimary)
                }
            }
        }
    }
}

private fun formatBrowserSize(sizeBytes: Long): String = when {
    sizeBytes >= 1024L * 1024L * 1024L -> String.format("%.1f Go", sizeBytes / (1024.0 * 1024.0 * 1024.0))
    sizeBytes >= 1024L * 1024L -> String.format("%.1f Mo", sizeBytes / (1024.0 * 1024.0))
    sizeBytes >= 1024L -> String.format("%.0f Ko", sizeBytes / 1024.0)
    else -> "$sizeBytes o"
}
