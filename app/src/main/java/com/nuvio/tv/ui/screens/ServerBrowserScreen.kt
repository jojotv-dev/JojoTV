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
import android.provider.DocumentsContract
import android.provider.Settings
import android.text.format.DateFormat
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
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.nuvio.tv.core.tmdb.TmdbArtworkCandidate
import com.nuvio.tv.ui.components.FocusMarqueeText
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.screens.home.HomeArtworkPickerDialog
import com.nuvio.tv.ui.theme.NuvioTheme
import coil3.compose.AsyncImage
import kotlinx.coroutines.CancellationException
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
    contentFocusRequester: FocusRequester? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val apkDownloadProgress by viewModel.apkDownloadProgress.collectAsStateWithLifecycle()
    val serverState by viewModel.servers.collectAsStateWithLifecycle()
    val freeboxState by viewModel.freebox.collectAsStateWithLifecycle()
    val folderCounts by viewModel.folderCounts.collectAsStateWithLifecycle()
    val videoDurations = remember(sourceId, path) {
        androidx.compose.runtime.mutableStateMapOf<String, Long>()
    }
    var videoDurationScanComplete by remember(sourceId, path) { mutableStateOf(false) }
    val videoDurationSnapshot = videoDurations.toMap()
    val displayedEntries = remember(state.entries, videoDurationSnapshot, videoDurationScanComplete) {
        if (videoDurationScanComplete) {
            sortServerBrowserEntriesByDuration(state.entries, videoDurationSnapshot)
        } else {
            state.entries
        }
    }
    val showVideoFolderBadge = sourceId == ServerBrowserViewModel.FREEBOX_SOURCE && isVideosFolderPath(path)
    val capacityState by capacityViewModel.capacityState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    val requesters = remember(path) { mutableMapOf<String, FocusRequester>() }
    var focusedPath by rememberSaveable(sourceId, path) { mutableStateOf<String?>(null) }
    var showLanFileShareDialog by remember { mutableStateOf(false) }
    var optionsEntry by remember { mutableStateOf<ServerBrowserEntry?>(null) }
    var renameEntry by remember { mutableStateOf<ServerBrowserEntry?>(null) }
    var renameValue by remember { mutableStateOf("") }
    var deleteEntries by remember { mutableStateOf<List<ServerBrowserEntry>>(emptyList()) }
    var selectionMode by rememberSaveable(sourceId, path) { mutableStateOf(false) }
    val selectedPaths = remember(sourceId, path) { mutableStateListOf<String>() }
    var autoOpenedTarget by rememberSaveable(sourceId, openPath) { mutableStateOf(false) }
    val actionScope = androidx.compose.runtime.rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var artworkTarget by remember { mutableStateOf<ServerBrowserEntry?>(null) }
    var artworkCandidates by remember { mutableStateOf<List<TmdbArtworkCandidate>>(emptyList()) }
    var isLoadingArtwork by remember { mutableStateOf(false) }
    var isSavingArtwork by remember { mutableStateOf(false) }
    var artworkError by remember { mutableStateOf<String?>(null) }
    fun selectedEntries(): List<ServerBrowserEntry> =
        state.entries.filter { it.path in selectedPaths }
    fun selectionSupportsWriteActions(): Boolean {
        val entries = selectedEntries()
        return entries.isNotEmpty() && entries.all { viewModel.supportsWriteActions(sourceId, it.path) }
    }
    fun selectionSupportsCopyActions(): Boolean {
        val entries = selectedEntries()
        return entries.isNotEmpty() && entries.all { viewModel.supportsCopyActions(sourceId, it.path) }
    }

    fun activateEntry(entry: ServerBrowserEntry) {
        if (selectionMode) {
            if (entry.path in selectedPaths) selectedPaths.remove(entry.path) else selectedPaths.add(entry.path)
        } else if (entry.isDirectory) {
            onOpenFolder(entry.path)
        } else {
            onOpenFile(entry)
        }
    }

    fun runRemoteAction(
        action: suspend () -> Unit,
        successMessage: String,
        reload: Boolean = true,
        reloadOnFailure: Boolean = false,
    ) {
        actionScope.launch {
            var actionSucceeded = false
            var actionCancelled = false
            try {
                action()
                actionSucceeded = true
                Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
            } catch (cancelled: CancellationException) {
                actionCancelled = true
                throw cancelled
            } catch (error: Exception) {
                Toast.makeText(context, error.message ?: "L’action n’a pas pu être effectuée.", Toast.LENGTH_LONG).show()
            } finally {
                if (reload && !actionCancelled && (actionSucceeded || reloadOnFailure)) viewModel.load(sourceId, path)
            }
        }
    }
    val localPicker = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = result.data?.data
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
        ActivityResultContracts.RequestMultiplePermissions(),
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
            legacyStoragePermissionLauncher.launch(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            )
        }
    }
    fun openLocalStorage() {
        when {
            hasDocumentTreePicker -> localPicker.launch(
                Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                    addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                            Intent.FLAG_GRANT_PREFIX_URI_PERMISSION,
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        state.localTreeUri?.let { putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(it)) }
                    }
                },
            )
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
    LaunchedEffect(sourceId, path, state.entries, state.isLoading) {
        if (state.isLoading) return@LaunchedEffect
        videoDurations.clear()
        videoDurationScanComplete = false
        for (entry in state.entries) {
            if (entry.fileType != ServerBrowserFileType.VIDEO) continue
            viewModel.videoDurationMs(sourceId, entry)?.let { duration ->
                videoDurations[entry.path] = duration
            }
        }
        videoDurationScanComplete = true
    }

    val localSourceEntry = moduleNavEntries.first { it.id == ServerBrowserViewModel.LOCAL_SOURCE }
    val currentRemoteSource = moduleNavEntries.firstOrNull { it.id == sourceId && it.id != ServerBrowserViewModel.LOCAL_SOURCE }
        ?: moduleNavEntries.firstOrNull { it.id == ServerBrowserViewModel.FREEBOX_SOURCE }
        ?: moduleNavEntries.firstOrNull { it.id != ServerBrowserViewModel.LOCAL_SOURCE }
    val pageTitle = if (sourceId == ServerBrowserViewModel.LOCAL_SOURCE) {
        "Stockage local et USB"
    } else {
        "Réseau SMB"
    }
    val currentSourceName = when (sourceId) {
        ServerBrowserViewModel.LOCAL_SOURCE -> "Local"
        ServerBrowserViewModel.FREEBOX_SOURCE -> "NAS"
        else -> moduleNavEntries.firstOrNull { it.id == sourceId }?.label ?: state.sourceName
    }
    val locationSummary = state.location.ifBlank { path.ifBlank { currentSourceName } }
    val capacitySummary = storageText?.let { "$currentSourceName : $it" }
        ?: if (state.isLoading) "$currentSourceName : Calcul de l’espace…"
        else "$currentSourceName : Espace indisponible"

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NuvioTheme.colors.Background)
                .padding(start = 30.dp, top = 95.dp, end = 24.dp, bottom = 24.dp),
        ) {
            Text(
                text = pageTitle,
                color = NuvioTheme.colors.TextPrimary,
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = locationSummary,
                        color = NuvioTheme.colors.TextSecondary,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        maxLines = 1,
                        overflow = if (sourceId == ServerBrowserViewModel.LOCAL_SOURCE) {
                            TextOverflow.StartEllipsis
                        } else {
                            TextOverflow.Ellipsis
                        },
                    )
                    Text(
                        text = capacitySummary,
                        color = NuvioTheme.colors.TextSecondary,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    BrowserActionCard(
                        icon = Icons.Default.Refresh,
                        label = "Actualiser le dossier",
                        onClick = { viewModel.load(sourceId, path) },
                        modifier = Modifier.size(42.dp),
                        showLabel = false,
                    )
                    if (sourceId == ServerBrowserViewModel.LOCAL_SOURCE) {
                        BrowserActionCard(
                            icon = Icons.Default.Share,
                            label = context.getString(R.string.lan_share_open_browser),
                            onClick = { showLanFileShareDialog = true },
                            modifier = Modifier.size(42.dp),
                            showLabel = false,
                        )
                    }
                    ServerBrowserSourceChip(
                        label = if (localSourceEntry.id == ServerBrowserViewModel.LOCAL_SOURCE) "Local / USB" else localSourceEntry.label,
                        selected = sourceId == localSourceEntry.id,
                        onClick = localSourceEntry.onClick,
                    )
                    currentRemoteSource?.takeIf { it.id != localSourceEntry.id }?.let { remote ->
                        ServerBrowserSourceChip(
                            label = if (remote.id == ServerBrowserViewModel.FREEBOX_SOURCE) "NAS" else remote.label,
                            selected = sourceId == remote.id,
                            onClick = remote.onClick,
                        )
                    }
                    BrowserActionCard(
                        icon = if (state.showHiddenFiles) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        label = if (state.showHiddenFiles) "Masquer les fichiers cachés" else "Afficher les fichiers cachés",
                        onClick = { viewModel.toggleShowHiddenFiles(sourceId, path) },
                        modifier = Modifier.size(42.dp),
                        showLabel = false,
                        selected = state.showHiddenFiles,
                    )
                    BrowserActionCard(
                        icon = if (state.isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                        label = if (state.isGridView) "Afficher en liste" else "Afficher en grille",
                        onClick = { viewModel.toggleGridView(sourceId, path) },
                        modifier = Modifier.size(width = 48.dp, height = 42.dp),
                        showLabel = false,
                    )
                }
            }
            if (selectionMode) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${selectedPaths.size} sélectionné(s)",
                        color = NuvioTheme.colors.TextPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    if (selectionSupportsCopyActions()) {
                        BrowserActionCard(Icons.Default.Folder, "Copier", {
                            val targets = selectedEntries()
                            viewModel.copyToClipboard(sourceId, targets, cut = false)
                            val message = if (targets.size == 1) {
                                "Élément copié dans le presse-papiers."
                            } else {
                                "${targets.size} éléments copiés dans le presse-papiers."
                            }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }, modifier = Modifier.width(112.dp).height(46.dp), showLabel = true)
                    }
                    if (selectionSupportsWriteActions()) {
                        BrowserActionCard(Icons.Default.Folder, "Couper", {
                            val targets = selectedEntries()
                            viewModel.copyToClipboard(sourceId, targets, cut = true)
                            val message = if (targets.size == 1) {
                                "Élément prêt à être déplacé."
                            } else {
                                "${targets.size} éléments prêts à être déplacés."
                            }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }, modifier = Modifier.width(112.dp).height(46.dp), showLabel = true)
                        BrowserActionCard(Icons.Default.Folder, "Supprimer", {
                            deleteEntries = selectedEntries()
                        }, modifier = Modifier.width(132.dp).height(46.dp), showLabel = true)
                    }
                    BrowserActionCard(Icons.AutoMirrored.Filled.ArrowBack, "Terminer", {
                        selectionMode = false
                        selectedPaths.clear()
                    }, modifier = Modifier.width(118.dp).height(46.dp), showLabel = true)
                }
            }

            Box(Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp)) {
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
                            onAction = { openLocalStorage() },
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
                    state.entries.isEmpty() -> BrowserMessage("Aucun fichier dans cet emplacement.", "")
                    else -> LazyVerticalGrid(
                        state = gridState,
                        columns = if (state.isGridView) GridCells.Fixed(7) else GridCells.Fixed(1),
                        modifier = Modifier.fillMaxSize().focusRestorer {
                            focusedPath?.let { requesters[it] } ?: FocusRequester.Default
                        },
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                    ) {
                        gridItems(displayedEntries, key = ServerBrowserEntry::path) { entry ->
                            val requester = requesters.getOrPut(entry.path) {
                                if (entry.path == displayedEntries.firstOrNull()?.path) {
                                    contentFocusRequester ?: FocusRequester()
                                } else {
                                    FocusRequester()
                                }
                            }
                            BrowserEntryCard(
                                entry = entry,
                                videoDurationMs = videoDurationSnapshot[entry.path],
                                showVideoFolderBadge = showVideoFolderBadge && entry.isDirectory,
                                modifier = Modifier
                                    .focusRequester(requester)
                                    .onFocusChanged { if (it.isFocused) focusedPath = entry.path },
                                isGridView = state.isGridView,
                                isFocused = focusedPath == entry.path,
                                isSelected = entry.path in selectedPaths,
                                sourceId = sourceId,
                                viewModel = viewModel,
                                artworkRevision = state.artworkRevision,
                                showHiddenFiles = state.showHiddenFiles,
                                folderCountRevision = state.folderCountRevision,
                                folderCounts = folderCounts,
                                onClick = { activateEntry(entry) },
                                onLongClick = { optionsEntry = entry },
                            )
                        }
                    }
                }
            }
        }

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

    if (showLanFileShareDialog) {
        LanFileShareDialog(onDismiss = { showLanFileShareDialog = false })
    }

    optionsEntry?.let { entry ->
        val actionEntries = if (selectionMode && entry.path in selectedPaths) {
            selectedEntries().ifEmpty { listOf(entry) }
        } else {
            listOf(entry)
        }
        ServerBrowserEntryOptionsDialog(
            entry = entry,
            isFreebox = sourceId == ServerBrowserViewModel.FREEBOX_SOURCE,
            supportsExport = sourceId != ServerBrowserViewModel.LOCAL_SOURCE,
            supportsCopy = actionEntries.all { viewModel.supportsCopyActions(sourceId, it.path) },
            supportsWrite = actionEntries.all { viewModel.supportsWriteActions(sourceId, it.path) },
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
                viewModel.copyToClipboard(sourceId, actionEntries, cut = false)
                optionsEntry = null
                val message = if (actionEntries.size == 1) {
                    "Élément copié dans le presse-papiers."
                } else {
                    "${actionEntries.size} éléments copiés dans le presse-papiers."
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            onCut = {
                viewModel.copyToClipboard(sourceId, actionEntries, cut = true)
                optionsEntry = null
                val message = if (actionEntries.size == 1) {
                    "Élément prêt à être déplacé."
                } else {
                    "${actionEntries.size} éléments prêts à être déplacés."
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            onPaste = {
                optionsEntry = null
                val clipboardIsCut = state.clipboardIsCut
                runRemoteAction({
                    val result = viewModel.pasteClipboard(sourceId, entry.path)
                    if (result.failures.isNotEmpty()) {
                        val verb = if (clipboardIsCut) "déplacé(s)" else "copié(s)"
                        val summary = if (result.completedCount == 0) {
                            "Aucun élément n’a été ${if (clipboardIsCut) "déplacé" else "copié"}."
                        } else {
                            "${result.completedCount}/${result.requestedCount} élément(s) $verb."
                        }
                        error("$summary Échecs : ${result.failures.take(3).joinToString(" ; ")}")
                    }
                }, "Collage terminé.", reloadOnFailure = true)
            },
            onChangeThumbnail = {
                optionsEntry = null
                artworkTarget = entry
                artworkCandidates = emptyList()
                artworkError = null
                isLoadingArtwork = true
                actionScope.launch {
                    try {
                        artworkCandidates = viewModel.artworkCandidates(entry, portrait = false)
                        if (artworkCandidates.isEmpty()) {
                            artworkError = context.getString(R.string.home_freebox_artwork_empty)
                        }
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (error: Exception) {
                        artworkError = error.message ?: context.getString(R.string.home_freebox_artwork_empty)
                    } finally {
                        isLoadingArtwork = false
                    }
                }
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
                deleteEntries = actionEntries
                optionsEntry = null
            },
        )
    }

    artworkTarget?.let { target ->
        HomeArtworkPickerDialog(
            title = target.name,
            portrait = false,
            candidates = artworkCandidates.map(TmdbArtworkCandidate::imageUrl),
            candidateDetails = artworkCandidates,
            isLoading = isLoadingArtwork,
            isBusy = isSavingArtwork,
            error = artworkError,
            onDismiss = { if (!isSavingArtwork) artworkTarget = null },
            onSelectWithDetails = { imageUrl, candidate ->
                isSavingArtwork = true
                artworkError = null
                actionScope.launch {
                    try {
                        viewModel.saveCustomArtwork(
                            sourceId = sourceId,
                            entry = target,
                            portrait = false,
                            imageUrl = imageUrl,
                            candidate = candidate,
                        )
                        artworkTarget = null
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (error: Exception) {
                        artworkError = error.message ?: context.getString(R.string.home_freebox_artwork_empty)
                    } finally {
                        isSavingArtwork = false
                    }
                }
            },
            onReset = {
                isSavingArtwork = true
                artworkError = null
                actionScope.launch {
                    try {
                        viewModel.saveCustomArtwork(
                            sourceId = sourceId,
                            entry = target,
                            portrait = false,
                            imageUrl = null,
                            candidate = null,
                        )
                        artworkTarget = null
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (error: Exception) {
                        artworkError = error.message ?: context.getString(R.string.home_freebox_artwork_empty)
                    } finally {
                        isSavingArtwork = false
                    }
                }
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
                runRemoteAction({
                    val result = viewModel.deleteEntries(sourceId, targets)
                    selectedPaths.clear()
                    selectionMode = false
                    if (result.failures.isNotEmpty()) {
                        val summary = if (result.completedCount == 0) {
                            "Aucun élément supprimé."
                        } else {
                            "${result.completedCount}/${result.requestedCount} élément(s) supprimé(s)."
                        }
                        error("$summary Échecs : ${result.failures.take(3).joinToString(" ; ")}")
                    }
                }, "Élément(s) supprimé(s).", reloadOnFailure = true)
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
    var focused by remember { mutableStateOf(false) }
    val shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))
    Card(
        onClick = onClick,
        modifier = modifier.onFocusChanged { focused = it.isFocused },
        colors = CardDefaults.colors(
            containerColor = if (selected) NuvioTheme.colors.Secondary else NuvioTheme.colors.Background,
            focusedContainerColor = if (selected) {
                NuvioTheme.colors.Secondary
            } else {
                NuvioTheme.colors.Secondary.copy(alpha = 0.28f)
            },
        ),
        border = CardDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, NuvioTheme.colors.Secondary.copy(alpha = 0.72f)),
                shape = shape,
            ),
            focusedBorder = Border(
                border = NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs),
                shape = shape,
            ),
        ),
        shape = CardDefaults.shape(shape = shape),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = if (showLabel) 14.dp else 0.dp),
            horizontalArrangement = if (showLabel) Arrangement.spacedBy(8.dp) else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            androidx.tv.material3.Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(if (showLabel) 24.dp else 18.dp),
                tint = when {
                    selected -> NuvioTheme.colors.OnSecondary
                    focused -> Color.White
                    else -> NuvioTheme.colors.Secondary
                },
            )
            if (showLabel) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected) NuvioTheme.colors.OnSecondary else Color.White,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun ServerBrowserSourceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    var isFocused by remember(label) { mutableStateOf(false) }
    val shape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp))
    Card(
        onClick = onClick,
        modifier = Modifier
            .widthIn(min = 96.dp, max = 160.dp)
            .height(42.dp)
            .onFocusChanged { isFocused = it.isFocused },
        colors = CardDefaults.colors(
            containerColor = if (selected) NuvioTheme.colors.Secondary else NuvioTheme.colors.Background,
            focusedContainerColor = if (selected) {
                NuvioTheme.colors.Secondary
            } else {
                NuvioTheme.colors.Secondary.copy(alpha = 0.28f)
            },
        ),
        border = CardDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, NuvioTheme.colors.Secondary.copy(alpha = 0.78f)),
                shape = shape,
            ),
            focusedBorder = Border(
                border = NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs),
                shape = shape,
            ),
        ),
        shape = CardDefaults.shape(shape = shape),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FocusMarqueeText(
                text = label,
                focused = isFocused,
                modifier = Modifier.weight(1f),
                color = if (selected) NuvioTheme.colors.OnSecondary else Color.White,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                textAlign = TextAlign.Center,
            )
            if (selected) {
                Text(
                    text = "✓",
                    modifier = Modifier.padding(start = 6.dp),
                    color = Color(0xFF3FAE58),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun BrowserEntryCard(
    entry: ServerBrowserEntry,
    videoDurationMs: Long?,
    showVideoFolderBadge: Boolean,
    modifier: Modifier,
    isGridView: Boolean,
    isFocused: Boolean,
    isSelected: Boolean,
    sourceId: String,
    viewModel: ServerBrowserViewModel,
    artworkRevision: Int,
    showHiddenFiles: Boolean,
    folderCountRevision: Int,
    folderCounts: Map<ServerBrowserFolderCountKey, ServerBrowserFolderCounts>,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
) {
    val shape = NuvioTheme.configuredShape(RoundedCornerShape(6.dp))
    val folderCountKey = remember(sourceId, entry.path, showHiddenFiles) {
        ServerBrowserFolderCountKey(sourceId, entry.path, showHiddenFiles)
    }
    val folderCount = folderCounts[folderCountKey]
    LaunchedEffect(sourceId, entry.path, showHiddenFiles, folderCountRevision) {
        if (entry.isDirectory) viewModel.requestFolderCounts(sourceId, entry.path, showHiddenFiles)
    }
    val customArtwork = viewModel.customArtworkUri(sourceId, entry)
    val homeVideoArtwork by remember(sourceId, entry.path, entry.sizeBytes, entry.lastModifiedMs, entry.fileId) {
        viewModel.observeHomeVideoArtwork(
            sourceId = sourceId,
            entry = entry,
            includeFolderArtwork = showVideoFolderBadge,
        )
    }.collectAsStateWithLifecycle(initialValue = null)
    val homeVideoArtworkUri = homeVideoArtwork?.takeIf(String::isNotBlank)?.let(Uri::parse)
    var pressedSelectKey by remember(sourceId, entry.path) { mutableStateOf<Int?>(null) }
    var pressGeneration by remember(sourceId, entry.path) {
        androidx.compose.runtime.mutableIntStateOf(0)
    }
    var longPressTriggered by remember(sourceId, entry.path) { mutableStateOf(false) }
    val currentOnLongClick = androidx.compose.runtime.rememberUpdatedState(onLongClick)
    LaunchedEffect(pressedSelectKey, pressGeneration) {
        val keyCode = pressedSelectKey ?: return@LaunchedEffect
        if (currentOnLongClick.value == null) return@LaunchedEffect
        kotlinx.coroutines.delay(1_000L)
        if (pressedSelectKey == keyCode && !longPressTriggered) {
            longPressTriggered = true
            currentOnLongClick.value?.invoke()
        }
    }
    var thumbnailUri by remember(sourceId, entry.path, entry.sizeBytes, entry.fileType, artworkRevision, homeVideoArtworkUri) {
        mutableStateOf(
            customArtwork ?: homeVideoArtworkUri
                ?: entry.uri?.takeIf { entry.fileType == ServerBrowserFileType.IMAGE }?.let(Uri::parse),
        )
    }
    LaunchedEffect(sourceId, entry.path, entry.sizeBytes, entry.fileType, artworkRevision, homeVideoArtworkUri) {
        thumbnailUri = viewModel.customArtworkUri(sourceId, entry) ?: homeVideoArtworkUri
            ?: if (entry.fileType == ServerBrowserFileType.IMAGE) viewModel.prepareThumbnailUri(sourceId, entry) else null
    }
    Card(
        onClick = onClick,
        modifier = modifier.then(
            if (isGridView) {
                Modifier.widthIn(max = 120.dp).fillMaxWidth().height(130.dp).padding(4.dp)
            } else {
                Modifier.fillMaxWidth().height(64.dp)
            },
        )
            .onPreviewKeyEvent { event ->
                val native = event.nativeKeyEvent
                if (onLongClick == null) {
                    false
                } else if (native.keyCode == AndroidKeyEvent.KEYCODE_MENU ||
                    isBrowserSelectKey(native.keyCode)
                ) {
                    when (native.action) {
                        AndroidKeyEvent.ACTION_DOWN -> {
                            if (native.repeatCount == 0 || pressedSelectKey != native.keyCode) {
                                pressedSelectKey = native.keyCode
                                longPressTriggered = false
                                pressGeneration++
                            }
                            true
                        }
                        AndroidKeyEvent.ACTION_UP -> {
                            if (pressedSelectKey != native.keyCode) {
                                native.keyCode == AndroidKeyEvent.KEYCODE_MENU
                            } else {
                                val wasLongPress = longPressTriggered
                                pressedSelectKey = null
                                longPressTriggered = false
                                if (native.keyCode != AndroidKeyEvent.KEYCODE_MENU && !wasLongPress) {
                                    onClick()
                                }
                                true
                            }
                        }
                        else -> false
                    }
                } else {
                    false
                }
            },
        colors = CardDefaults.colors(
            containerColor = if (isSelected) NuvioTheme.colors.Secondary else NuvioTheme.colors.Background,
            focusedContainerColor = if (isSelected) {
                NuvioTheme.colors.Secondary
            } else {
                NuvioTheme.colors.Secondary.copy(alpha = 0.28f)
            },
        ),
        border = CardDefaults.border(
            border = when {
                isSelected -> Border(BorderStroke(2.dp, NuvioTheme.colors.Secondary))
                else -> Border(BorderStroke(1.dp, NuvioTheme.colors.Secondary.copy(alpha = 0.55f)))
            },
            focusedBorder = Border(
                border = NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs),
                shape = shape,
            ),
        ),
        shape = CardDefaults.shape(shape = shape),
        scale = CardDefaults.scale(focusedScale = if (isGridView) 1.025f else 1f, pressedScale = 1f),
    ) {
        val entryIcon = when (entry.fileType) {
            ServerBrowserFileType.DIRECTORY -> R.drawable.ic_file_folder
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
        val entryIconColorFilter = if (entry.fileType == ServerBrowserFileType.DIRECTORY) {
            androidx.compose.ui.graphics.ColorFilter.tint(
                if (isSelected) NuvioTheme.colors.OnSecondary else NuvioTheme.colors.Secondary,
            )
        } else {
            null
        }
        val displayEntryName = if (entry.fileType == ServerBrowserFileType.VIDEO && videoDurationMs != null) {
            "${formatBrowserDuration(videoDurationMs)} ${entry.name}"
        } else {
            entry.name
        }
        val context = androidx.compose.ui.platform.LocalContext.current
        val modifiedText = entry.lastModifiedMs.takeIf { it > 0L }?.let { modifiedAt ->
            DateFormat.format("dd/MM/yy HH:mm", modifiedAt).toString()
        }
        val directoryCountText = when (val count = folderCount) {
            is ServerBrowserFolderCounts.Ready ->
                count.folders.toString() + " dossiers · " + count.files + " fichiers"
            ServerBrowserFolderCounts.Loading, null -> "Calcul du contenu…"
            ServerBrowserFolderCounts.Unavailable -> "Nombre indisponible"
        }
        val fileSummary = listOfNotNull(
            entry.fileType.label,
            entry.sizeBytes.takeIf { it > 0L }?.let(::formatBrowserSize),
        ).joinToString(" · ")
        val entryDetail = when {
            entry.isDirectory -> listOfNotNull(modifiedText, directoryCountText).joinToString("\n")
            else -> listOfNotNull(fileSummary, modifiedText).joinToString("\n")
        }
        Box(Modifier.fillMaxSize()) {
            if (isGridView) {
            Column(
                modifier = Modifier.fillMaxSize().padding(start = 5.dp, top = 4.dp, end = 5.dp, bottom = 3.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                Box(Modifier.fillMaxWidth().height(58.dp)) {
                    if (thumbnailUri != null) {
                        AsyncImage(
                            model = thumbnailUri,
                            contentDescription = entry.name,
                            contentScale = ContentScale.Crop,
                            filterQuality = FilterQuality.Medium,
                            modifier = Modifier.fillMaxSize()
                                .clip(NuvioTheme.configuredShape(RoundedCornerShape(8.dp))),
                        )
                    } else {
                        Image(
                            painter = painterResource(entryIcon),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 2.dp),
                            contentScale = ContentScale.Fit,
                            colorFilter = entryIconColorFilter,
                        )
                    }
                    if (showVideoFolderBadge) {
                        com.nuvio.tv.ui.components.FreeboxFolderBadge(
                            modifier = Modifier.align(Alignment.BottomEnd).padding(2.dp).size(22.dp),
                        )
                    }
                }
                FocusMarqueeText(
                    text = displayEntryName,
                    focused = isFocused,
                    modifier = Modifier.fillMaxWidth().height(20.dp).padding(horizontal = 2.dp, vertical = 1.dp),
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                    color = if (isSelected) NuvioTheme.colors.OnSecondary else Color.White,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = entryDetail,
                    modifier = Modifier.fillMaxWidth().height(34.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    color = if (isSelected) {
                        NuvioTheme.colors.OnSecondary.copy(alpha = 0.86f)
                    } else {
                        Color.White.copy(alpha = 0.82f)
                    },
                    textAlign = TextAlign.Center,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxSize().padding(start = 6.dp, top = 3.dp, end = 14.dp, bottom = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(43.dp)) {
                    if (thumbnailUri != null) {
                        AsyncImage(
                            model = thumbnailUri,
                            contentDescription = entry.name,
                            contentScale = ContentScale.Crop,
                            filterQuality = FilterQuality.Medium,
                            modifier = Modifier.fillMaxSize()
                                .clip(NuvioTheme.configuredShape(RoundedCornerShape(6.dp))),
                        )
                    } else {
                        Image(
                            painter = painterResource(entryIcon),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(2.dp),
                            contentScale = ContentScale.Fit,
                            colorFilter = entryIconColorFilter,
                        )
                    }
                    if (showVideoFolderBadge) {
                        com.nuvio.tv.ui.components.FreeboxFolderBadge(
                            modifier = Modifier.align(Alignment.BottomEnd).padding(1.dp).size(18.dp),
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    FocusMarqueeText(
                        text = displayEntryName,
                        focused = isFocused,
                        modifier = Modifier.fillMaxWidth().height(24.dp),
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                        color = if (isSelected) NuvioTheme.colors.OnSecondary else Color.White,
                    )
                    Text(
                        text = entryDetail,
                        modifier = Modifier.fillMaxWidth().height(30.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = if (isSelected) NuvioTheme.colors.OnSecondary.copy(alpha = 0.86f)
                        else NuvioTheme.colors.TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(19.dp)
                        .background(Color(0xFF25A85A), RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
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
    supportsCopy: Boolean,
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
            if (supportsCopy) {
                ServerBrowserMenuButton("Copier", onCopy)
            }
            if (supportsWrite) {
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
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            color = NuvioTheme.colors.TextSecondary,
            textAlign = TextAlign.Center,
        )
        if (description.isNotBlank() && description != title) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                color = NuvioTheme.colors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        if (actionLabel != null && onAction != null) {
            val shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))
            Card(
                onClick = onAction,
                modifier = Modifier.padding(top = 16.dp).widthIn(min = 280.dp, max = 330.dp).height(56.dp),
                colors = CardDefaults.colors(
                    containerColor = NuvioTheme.colors.Background,
                    focusedContainerColor = NuvioTheme.colors.FocusBackground,
                ),
                border = CardDefaults.border(
                    border = Border(
                        border = BorderStroke(1.dp, NuvioTheme.colors.Secondary.copy(alpha = 0.72f)),
                        shape = shape,
                    ),
                    focusedBorder = Border(
                        border = NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs),
                        shape = shape,
                    ),
                ),
                shape = CardDefaults.shape(shape = shape),
                scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        actionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = NuvioTheme.colors.TextPrimary,
                        maxLines = 1,
                    )
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

private fun formatBrowserDuration(durationMs: Long): String {
    val totalMinutes = ((durationMs.coerceAtLeast(0L) + 30_000L) / 60_000L).coerceAtLeast(1L)
    val hours = totalMinutes / 60L
    val minutes = totalMinutes % 60L
    return if (hours > 0L) {
        "${hours}h${minutes.toString().padStart(2, '0')}"
    } else {
        "${minutes}min"
    }
}

private fun sortServerBrowserEntriesByDuration(
    entries: List<ServerBrowserEntry>,
    videoDurations: Map<String, Long>,
): List<ServerBrowserEntry> = entries.sortedWith(
    compareBy<ServerBrowserEntry> {
        when {
            it.isDirectory -> 0
            it.fileType == ServerBrowserFileType.VIDEO -> 1
            else -> 2
        }
    }.thenBy {
        if (it.fileType == ServerBrowserFileType.VIDEO) {
            videoDurations[it.path] ?: Long.MAX_VALUE
        } else {
            0L
        }
    }.thenBy { it.name.lowercase(java.util.Locale.ROOT) }
)

private fun isVideosFolderPath(path: String): Boolean {
    val folderName = android.net.Uri.decode(
        path.replace('\\', '/').substringBefore('?').trimEnd('/').substringAfterLast('/'),
    )
    val normalizedName = java.text.Normalizer.normalize(
        folderName,
        java.text.Normalizer.Form.NFD,
    ).replace(Regex("\\p{M}+"), "").lowercase(java.util.Locale.ROOT)
    return normalizedName == "video" || normalizedName == "videos"
}
