package com.nuvio.tv.ui.screens.library

import android.view.KeyEvent as AndroidKeyEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.focusable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button as TvMaterialButton
import com.nuvio.tv.ui.components.NuvioButton as Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.nuvio.tv.domain.model.LibraryListTab
import com.nuvio.tv.domain.model.LibrarySourceMode
import com.nuvio.tv.domain.model.PosterShape
import com.nuvio.tv.ui.components.EmptyScreenState
import com.nuvio.tv.ui.components.GridContentCard
import com.nuvio.tv.ui.screens.detail.requestFocusAfterFrames
import com.nuvio.tv.ui.screens.home.HeroBackdropState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.nuvio.tv.ui.components.PosterCardDefaults
import com.nuvio.tv.domain.model.localizedTitle
import com.nuvio.tv.ui.components.LoadingIndicator
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.theme.NuvioTheme
import com.nuvio.tv.ui.util.localizedContentType
import com.nuvio.tv.ui.util.localizedGenreLabel
import kotlinx.coroutines.delay
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.TextStyle
import com.nuvio.tv.R

private const val KEY_REPEAT_THROTTLE_MS = 80L

private enum class LibraryViewMode {
    Saved,
    Servers
}

@Composable
private fun localizedTypeLabel(key: String): String = when (key.lowercase()) {
    LibraryTypeTab.ALL_KEY -> stringResource(R.string.library_type_all)
    else -> localizedContentType(key)
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    serversViewModel: LibraryServersViewModel = hiltViewModel(),
    showBuiltInHeader: Boolean = true,
    onNavigateToDetail: (String, String, String?) -> Unit,
    onNavigateToCatalogSeeAll: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()
    val watchedMovieIds by viewModel.watchedMovieIds.collectAsState()
    val watchedSeriesIds by viewModel.watchedSeriesIds.collectAsState()
    var showDeleteConfirm by remember(uiState.showManageDialog) { mutableStateOf(false) }
    var expandedPicker by remember { mutableStateOf<String?>(null) }
    var selectedViewMode by rememberSaveable { mutableStateOf(LibraryViewMode.Saved) }
    val servers by serversViewModel.servers.collectAsState()
    val serverShelves by serversViewModel.shelves.collectAsState()
    val hasServers = servers.enabledConnections.isNotEmpty()
    val viewModes = LibraryViewMode.entries.filter { it != LibraryViewMode.Servers || hasServers }
    val viewMode = selectedViewMode.takeIf { it in viewModes } ?: LibraryViewMode.Saved
    val primaryFocusRequester = remember { FocusRequester() }
    val selectorFocusRequester = remember { FocusRequester() }
    val gridState = rememberLazyGridState()
    var pendingPrimaryFocus by remember { mutableStateOf(true) }
    var serverFocusTarget by remember {
        mutableStateOf(serversViewModel.focusedShelfKey?.let { key -> serversViewModel.focusedIndexes[key]?.let { key to it } })
    }
    var lastFocusedPosterKey by rememberSaveable { mutableStateOf<String?>(null) }
    val visibleItemKeys = remember(uiState.visibleItems) {
        uiState.visibleItems.map { "${it.type}:${it.id}" }
    }
    val visibleItemIndexByKey = remember(visibleItemKeys) {
        visibleItemKeys.withIndex().associate { (index, key) -> key to index }
    }
    val posterFocusRequesters = remember(visibleItemKeys) {
        visibleItemKeys.associateWith { FocusRequester() }
    }
    val layoutDirection = LocalLayoutDirection.current
    val firstVisiblePosterKey = visibleItemKeys.firstOrNull()
    val firstVisibleCardKey = visibleItemKeys.let { keys ->
        if (layoutDirection == LayoutDirection.Rtl) {
            keys.lastOrNull()  // Last in logical order = rightmost in RTL
        } else {
            keys.firstOrNull() // First in logical order = leftmost in LTR
        }
    }
    val posterCardStyle = PosterCardDefaults.Style.copy(
        width = uiState.posterCardWidthDp.dp,
        height = uiState.posterCardHeightDp.dp,
        cornerRadius = uiState.posterCardCornerRadiusDp.dp
    )
    val modernPortraitScale = if (uiState.useModernHomeCardSizing) 0.84f * 1.08f else 1f
    val favoritesPosterCardStyle = posterCardStyle.copy(
        width = posterCardStyle.width * modernPortraitScale,
        height = posterCardStyle.height * modernPortraitScale
    )
    val favoritesLandscapeCardWidth = if (uiState.useModernHomeCardSizing) {
        posterCardStyle.width * 1.24f * 1.34f
    } else {
        null
    }

    LaunchedEffect(viewMode, servers.revision) {
        if (viewMode == LibraryViewMode.Servers) serversViewModel.load()
    }

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            pendingPrimaryFocus = true
        }
    }

    LaunchedEffect(uiState.isLoading, uiState.sourceMode, uiState.listTabs.size) {
        if (!uiState.isLoading && pendingPrimaryFocus && viewMode == LibraryViewMode.Servers && serverFocusTarget != null) {
            pendingPrimaryFocus = false
        }
        if (!uiState.isLoading && pendingPrimaryFocus) {
            val restoreKey = lastFocusedPosterKey
            val restoreIndex = restoreKey?.let { visibleItemIndexByKey[it] }
            val restoreRequester = restoreKey?.let { posterFocusRequesters[it] }

            var focused = false
            if (restoreIndex != null && restoreRequester != null) {
                if (gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset == 0) {
                    runCatching { gridState.scrollToItem(restoreIndex) }
                }
                focused = runCatching { restoreRequester.requestFocus() }.isSuccess
                if (!focused) {
                    delay(16)
                    focused = runCatching { restoreRequester.requestFocus() }.isSuccess
                }
            }

            if (!focused) {
                focused = runCatching { primaryFocusRequester.requestFocus() }.isSuccess
            }
            if (!focused) {
                delay(16)
                runCatching { primaryFocusRequester.requestFocus() }
            }
            pendingPrimaryFocus = false
        }
    }

    LaunchedEffect(uiState.sortSelectionVersion, firstVisiblePosterKey) {
        if (uiState.sortSelectionVersion <= 0L) return@LaunchedEffect
        val targetKey = firstVisiblePosterKey ?: return@LaunchedEffect
        runCatching { gridState.scrollToItem(0) }
        var focused = false
        repeat(6) {
            focused = posterFocusRequesters[targetKey]
                ?.let { requester -> runCatching { requester.requestFocus() }.isSuccess }
                ?: false
            if (focused) return@LaunchedEffect
            delay(24)
        }
    }

    if (uiState.isLoading) {
        val loadingFocusRequester = remember { FocusRequester() }
        val loadingLabel = stringResource(R.string.library_syncing_library)
        LaunchedEffect(uiState.isLoading) {
            loadingFocusRequester.requestFocus()
        }

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(loadingFocusRequester)
                    .semantics { contentDescription = loadingLabel }
                    .focusable()
            )
            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                LoadingIndicator()
                Text(
                    text = stringResource(R.string.library_syncing_library),
                    style = MaterialTheme.typography.bodyMedium,
                    color = NuvioTheme.colors.TextSecondary
                )
            }
        }
        return
    }

    val lastKeyRepeatTime = remember { longArrayOf(0L) }

    val globalLandscape = com.nuvio.tv.ui.components.LocalLandscapePosterMode.current
    val favoriteCardMinWidth = if (globalLandscape) {
        favoritesLandscapeCardWidth ?: favoritesPosterCardStyle.height
    } else {
        favoritesPosterCardStyle.width
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(
            minSize = favoriteCardMinWidth
        ),
        state = gridState,
        modifier = Modifier
            .fillMaxSize()
            .background(NuvioTheme.colors.Background)
            .focusRestorer {
                val lastKey = lastFocusedPosterKey
                (if (lastKey != null && lastKey in posterFocusRequesters) {
                    posterFocusRequesters[lastKey]
                } else {
                    posterFocusRequesters[firstVisibleCardKey]
                }) ?: FocusRequester()
            }
            .onPreviewKeyEvent { event ->
                val native = event.nativeKeyEvent
                if (native.action == AndroidKeyEvent.ACTION_DOWN && native.repeatCount > 0) {
                    val now = System.currentTimeMillis()
                    if (now - lastKeyRepeatTime[0] < KEY_REPEAT_THROTTLE_MS) {
                        return@onPreviewKeyEvent true
                    }
                    lastKeyRepeatTime[0] = now
                }
                false
            },
        contentPadding = PaddingValues(start = NuvioTheme.spacing.xxxl, end = NuvioTheme.spacing.xxxl, top = NuvioTheme.spacing.xl, bottom = NuvioTheme.spacing.xxl),
        horizontalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.lg)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.library_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (showBuiltInHeader) NuvioTheme.colors.TextPrimary else Color.Transparent,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = when {
                        viewMode == LibraryViewMode.Servers -> stringResource(R.string.library_source_servers).uppercase()
                        uiState.sourceMode == LibrarySourceMode.TRAKT -> "TRAKT"
                        uiState.sourceMode == LibrarySourceMode.MDBLIST -> "MDBLIST"
                        uiState.isNuvioAccount -> "NUVIO"
                        else -> stringResource(R.string.library_source_local)
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = if (showBuiltInHeader) NuvioTheme.colors.TextTertiary else Color.Transparent,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            LibraryViewModeRow(
                modes = viewModes,
                selectedMode = viewMode,
                selectedTypeKey = if (viewMode == LibraryViewMode.Saved) uiState.selectedTypeTab?.key else null,
                primaryFocusRequester = primaryFocusRequester,
                onSelectedMode = { mode ->
                    selectedViewMode = mode
                    expandedPicker = null
                },
                onSelectType = { typeKey ->
                    selectedViewMode = LibraryViewMode.Saved
                    val targetTab = if (typeKey == LibraryTypeTab.ALL_KEY) {
                        uiState.availableTypeTabs.firstOrNull { it.key == LibraryTypeTab.ALL_KEY } ?: LibraryTypeTab.All
                    } else {
                        uiState.availableTypeTabs.firstOrNull { it.key.equals(typeKey, ignoreCase = true) }
                            ?: LibraryTypeTab(key = typeKey, label = typeKey)
                    }
                    viewModel.onSelectTypeTab(targetTab)
                    expandedPicker = null
                },
            )
        }

        if (viewMode == LibraryViewMode.Saved) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                LibrarySelectorsRow(
                    sourceMode = uiState.sourceMode,
                    listTabs = uiState.listTabs,
                    typeTabs = uiState.availableTypeTabs,
                    sortOptions = uiState.availableSortOptions,
                    genres = uiState.availableGenres,
                    years = uiState.availableYears,
                    selectedListKey = uiState.selectedListKey,
                    selectedTypeTab = uiState.selectedTypeTab,
                    selectedSortOption = uiState.selectedSortOption,
                    selectedGenre = uiState.selectedGenre,
                    selectedYear = uiState.selectedYear,
                    selectedWatchedFilter = uiState.selectedWatchedFilter,
                    primaryFocusRequester = selectorFocusRequester,
                    upFocusRequester = primaryFocusRequester,
                    expandedPicker = expandedPicker,
                    onExpandedChange = { picker, shouldExpand ->
                        expandedPicker = if (shouldExpand) picker else null
                    },
                    onSelectList = { key ->
                        viewModel.onSelectListTab(key)
                        expandedPicker = null
                    },
                    onSelectType = { type ->
                        viewModel.onSelectTypeTab(type)
                        expandedPicker = null
                    },
                    onSelectSort = { sort ->
                        viewModel.onSelectSortOption(sort)
                        expandedPicker = null
                    },
                    onSelectGenre = { key ->
                        viewModel.onSelectGenre(key)
                        expandedPicker = null
                    },
                    onSelectYear = { key ->
                        viewModel.onSelectYear(key)
                        expandedPicker = null
                    },
                    onSelectWatchedFilter = { filter ->
                        viewModel.onSelectWatchedFilter(filter)
                        expandedPicker = null
                    }
                )
            }

            if (uiState.listManagement != null && uiState.isTrackingAuthenticated) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LibraryActionsRow(
                        pending = uiState.pendingOperation,
                        isSyncing = uiState.isSyncing,
                        showManageLists = uiState.listManagement != null,
                        onManageLists = viewModel::onOpenManageLists,
                        onRefresh = viewModel::onRefresh
                    )
                }
            }

            if (uiState.visibleItems.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    val selectedTypeLabel = uiState.selectedTypeTab?.let { localizedTypeLabel(it.key) }?.lowercase() ?: stringResource(R.string.library_type_items)
                    val title = when {
                        uiState.sourceMode == LibrarySourceMode.TRAKT && !uiState.isTrackingAuthenticated -> stringResource(R.string.library_empty_trakt_not_auth_title)
                        uiState.sourceMode == LibrarySourceMode.TRAKT || uiState.sourceMode == LibrarySourceMode.MDBLIST -> stringResource(R.string.library_empty_trakt_title, selectedTypeLabel)
                        else -> stringResource(R.string.library_empty_local_title, selectedTypeLabel)
                    }
                    val subtitle = when {
                        uiState.sourceMode == LibrarySourceMode.TRAKT && !uiState.isTrackingAuthenticated -> stringResource(R.string.library_empty_trakt_not_auth_subtitle)
                        uiState.sourceMode == LibrarySourceMode.TRAKT || uiState.sourceMode == LibrarySourceMode.MDBLIST -> stringResource(R.string.library_empty_trakt_subtitle)
                        else -> stringResource(R.string.library_empty_local_subtitle)
                    }
                    EmptyScreenState(
                        title = title,
                        subtitle = subtitle,
                        icon = Icons.Default.BookmarkBorder
                    )
                }
            }

            items(uiState.visibleItems, key = { "${it.type}:${it.id}" }) { item ->
                val focusKey = "${item.type}:${item.id}"
                val isSeries = item.type.equals("series", ignoreCase = true) || item.type.equals("tv", ignoreCase = true)
                val homeArtworkOverrides = remember(item.id, uiState.homeArtworkOverridesVersion) {
                    viewModel.homeFavoriteArtworkOverrides(item.id)
                }
                val itemPreview = remember(item, homeArtworkOverrides) {
                    val preview = item.toMetaPreview()
                    val portraitOverride = homeArtworkOverrides.first
                    val landscapeOverride = homeArtworkOverrides.second
                    preview.copy(
                        poster = portraitOverride ?: preview.poster,
                        posterShape = if (portraitOverride != null) PosterShape.POSTER else preview.posterShape,
                        background = landscapeOverride ?: preview.background,
                        landscapePoster = landscapeOverride ?: preview.landscapePoster,
                    )
                }
                GridContentCard(
                    item = itemPreview,
                    posterCardStyle = favoritesPosterCardStyle,
                    landscapeCardWidthOverride = favoritesLandscapeCardWidth,
                    isWatched = if (isSeries) item.id in watchedSeriesIds else item.id in watchedMovieIds,
                    focusRequester = posterFocusRequesters[focusKey],
                    showLabel = true,
                    showLogo = false,
                    posterContentScaleOverride = ContentScale.Fit,
                    onFocused = {
                        lastFocusedPosterKey = focusKey
                        viewModel.prefetchMetaOnFocus(item.id, item.type)
                    },
                    onClick = {
                        lastFocusedPosterKey = focusKey
                        val backdrop = viewModel.getCachedBackdrop(item.id, item.type)
                        HeroBackdropState.update(backdrop)
                        onNavigateToDetail(item.id, item.type, item.addonBaseUrl)
                    },
                    onLongPress = {
                        lastFocusedPosterKey = focusKey
                        viewModel.posterOptions.show(itemPreview, item.addonBaseUrl)
                    }
                )
            }
        } else if (viewMode == LibraryViewMode.Servers) {
            libraryServerContent(
                shelves = serverShelves,
                posterCardStyle = posterCardStyle,
                rowInset = NuvioTheme.spacing.xxxl,
                focusTarget = serverFocusTarget,
                focusedIndexes = serversViewModel.focusedIndexes,
                onItemFocused = { shelfKey, index ->
                    val target = serverFocusTarget
                    if (target == null || target == shelfKey to index) {
                        serverFocusTarget = null
                        serversViewModel.onItemFocused(shelfKey, index)
                    }
                },
                isWatched = { item ->
                    val isSeries = item.apiType.equals("series", ignoreCase = true) || item.apiType.equals("tv", ignoreCase = true)
                    if (isSeries) item.id in watchedSeriesIds else item.id in watchedMovieIds
                },
                onItemClick = { itemId, itemType, addonBaseUrl -> onNavigateToDetail(itemId, itemType, addonBaseUrl) },
                onSeeAll = { row -> onNavigateToCatalogSeeAll(row.catalogId, row.addonId, row.apiType) },
                onItemLongPress = { item, addonBaseUrl -> viewModel.posterOptions.show(item, addonBaseUrl) }
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(NuvioTheme.spacing.sm)) }
    }

    if (uiState.showManageDialog && uiState.listManagement != null) {
        ManageListsDialog(
            capabilities = requireNotNull(uiState.listManagement),
            tabs = uiState.listTabs,
            selectedKey = uiState.manageSelectedListKey,
            errorMessage = uiState.errorMessage,
            pending = uiState.pendingOperation,
            onSelect = viewModel::onSelectManageList,
            onCreate = viewModel::onStartCreateList,
            onEdit = viewModel::onStartEditList,
            onMoveUp = viewModel::onMoveSelectedListUp,
            onMoveDown = viewModel::onMoveSelectedListDown,
            onDelete = { showDeleteConfirm = true },
            onDismiss = viewModel::onCloseManageLists
        )
    }

    if (showDeleteConfirm && uiState.showManageDialog) {
        ConfirmDeleteDialog(
            pending = uiState.pendingOperation,
            onConfirm = {
                showDeleteConfirm = false
                viewModel.onDeleteSelectedList()
            },
            onCancel = { showDeleteConfirm = false }
        )
    }

    val listEditor = uiState.listEditorState
    if (listEditor != null && uiState.showManageDialog && uiState.listManagement != null) {
        ListEditorDialog(
            capabilities = requireNotNull(uiState.listManagement),
            state = listEditor,
            pending = uiState.pendingOperation,
            onNameChanged = viewModel::onUpdateEditorName,
            onDescriptionChanged = viewModel::onUpdateEditorDescription,
            onPrivacyChanged = viewModel::onUpdateEditorPrivacy,
            onSave = viewModel::onSubmitEditor,
            onCancel = viewModel::onCancelEditor
        )
    }

    val transientMessage = uiState.transientMessage
    if (!transientMessage.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.TopCenter
        ) {
            Text(
                text = transientMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = NuvioTheme.colors.TextPrimary,
                modifier = Modifier
                    .padding(top = NuvioTheme.spacing.xl)
                    .background(NuvioTheme.colors.BackgroundElevated, NuvioTheme.configuredShape(RoundedCornerShape(10.dp)))
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            )
        }
    }

    val posterOptionsState by viewModel.posterOptions.state.collectAsState()
    com.nuvio.tv.ui.components.posteroptions.PosterOptionsHost(
        state = posterOptionsState,
        controller = viewModel.posterOptions,
        onNavigateToDetail = { id, type, addonBaseUrl ->
            onNavigateToDetail(id, type, addonBaseUrl.takeIf { it.isNotBlank() })
        }
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun LibraryViewModeRow(
    modes: List<LibraryViewMode>,
    selectedMode: LibraryViewMode,
    selectedTypeKey: String?,
    primaryFocusRequester: FocusRequester,
    onSelectedMode: (LibraryViewMode) -> Unit,
    onSelectType: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md)) {
            // 1. Saved (All)
            val isSavedSelected = selectedMode == LibraryViewMode.Saved &&
                (selectedTypeKey == null || selectedTypeKey == LibraryTypeTab.ALL_KEY)
            Button(
                onClick = {
                    onSelectedMode(LibraryViewMode.Saved)
                    onSelectType(LibraryTypeTab.ALL_KEY)
                },
                modifier = Modifier
                    .then(if (isSavedSelected) Modifier.focusRequester(primaryFocusRequester) else Modifier)
                    .semantics {
                        role = Role.Tab
                        this.selected = isSavedSelected
                    },
                colors = ButtonDefaults.colors(
                    containerColor = if (isSavedSelected) NuvioTheme.colors.FocusBackground else NuvioTheme.colors.BackgroundCard,
                    contentColor = NuvioTheme.colors.TextPrimary
                )
            ) {
                Text(text = stringResource(R.string.library_source_saved))
            }

            // 2. Movies shortcut tab
            val isMoviesSelected = selectedMode == LibraryViewMode.Saved &&
                selectedTypeKey.equals("movie", ignoreCase = true)
            Button(
                onClick = {
                    onSelectedMode(LibraryViewMode.Saved)
                    onSelectType("movie")
                },
                modifier = Modifier
                    .then(if (isMoviesSelected) Modifier.focusRequester(primaryFocusRequester) else Modifier)
                    .semantics {
                        role = Role.Tab
                        this.selected = isMoviesSelected
                    },
                colors = ButtonDefaults.colors(
                    containerColor = if (isMoviesSelected) NuvioTheme.colors.FocusBackground else NuvioTheme.colors.BackgroundCard,
                    contentColor = NuvioTheme.colors.TextPrimary
                )
            ) {
                Text(text = stringResource(R.string.type_movies))
            }

            // 3. Series shortcut tab
            val isSeriesSelected = selectedMode == LibraryViewMode.Saved &&
                (selectedTypeKey.equals("series", ignoreCase = true) ||
                    selectedTypeKey.equals("tv", ignoreCase = true) ||
                    selectedTypeKey.equals("show", ignoreCase = true))
            Button(
                onClick = {
                    onSelectedMode(LibraryViewMode.Saved)
                    onSelectType("series")
                },
                modifier = Modifier
                    .then(if (isSeriesSelected) Modifier.focusRequester(primaryFocusRequester) else Modifier)
                    .semantics {
                        role = Role.Tab
                        this.selected = isSeriesSelected
                    },
                colors = ButtonDefaults.colors(
                    containerColor = if (isSeriesSelected) NuvioTheme.colors.FocusBackground else NuvioTheme.colors.BackgroundCard,
                    contentColor = NuvioTheme.colors.TextPrimary
                )
            ) {
                Text(text = stringResource(R.string.type_series))
            }

            // 4. Other modes: Servers
            modes.filter { it != LibraryViewMode.Saved }.forEach { mode ->
                val selected = mode == selectedMode
                Button(
                    onClick = { onSelectedMode(mode) },
                    modifier = Modifier
                        .then(if (selected) Modifier.focusRequester(primaryFocusRequester) else Modifier)
                        .semantics {
                            role = Role.Tab
                            this.selected = selected
                        },
                    colors = ButtonDefaults.colors(
                        containerColor = if (selected) NuvioTheme.colors.FocusBackground else NuvioTheme.colors.BackgroundCard,
                        contentColor = NuvioTheme.colors.TextPrimary
                    )
                ) {
                    Text(
                        text = when (mode) {
                            LibraryViewMode.Saved -> stringResource(R.string.library_source_saved)
                            LibraryViewMode.Servers -> stringResource(R.string.library_source_servers)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun LibrarySelectorsRow(
    sourceMode: LibrarySourceMode,
    listTabs: List<LibraryListTab>,
    typeTabs: List<LibraryTypeTab>,
    sortOptions: List<LibrarySortOption>,
    genres: List<FilterOption>,
    years: List<FilterOption>,
    selectedListKey: String?,
    selectedTypeTab: LibraryTypeTab?,
    selectedSortOption: LibrarySortOption,
    selectedGenre: String?,
    selectedYear: String?,
    selectedWatchedFilter: LibraryWatchedFilter,
    primaryFocusRequester: FocusRequester,
    upFocusRequester: FocusRequester,
    expandedPicker: String?,
    onExpandedChange: (String, Boolean) -> Unit,
    onSelectList: (String) -> Unit,
    onSelectType: (LibraryTypeTab) -> Unit,
    onSelectSort: (LibrarySortOption) -> Unit,
    onSelectGenre: (String?) -> Unit,
    onSelectYear: (String?) -> Unit,
    onSelectWatchedFilter: (LibraryWatchedFilter) -> Unit
) {
    val selectedListLabel = listTabs.firstOrNull { it.key == selectedListKey }?.localizedTitle()
        ?: stringResource(R.string.action_select)
    val selectedTypeLabel = selectedTypeTab?.let {
        if (it.key == LibraryTypeTab.ALL_KEY) stringResource(R.string.library_type_all) else localizedTypeLabel(it.key)
    } ?: stringResource(R.string.library_type_all)
    val selectedSortLabel = stringResource(selectedSortOption.labelResId)
    val allLabel = stringResource(R.string.library_type_all)
    val selectedGenreLabel = selectedGenre?.let { localizedGenreLabel(it) } ?: allLabel
    val selectedYearLabel = selectedYear ?: allLabel

    val showListPicker = sourceMode != LibrarySourceMode.LOCAL && listTabs.size > 1
    val hasCustomTypes = typeTabs.any {
        it.key != LibraryTypeTab.ALL_KEY &&
            !it.key.equals("movie", ignoreCase = true) &&
            !it.key.equals("series", ignoreCase = true) &&
            !it.key.equals("tv", ignoreCase = true) &&
            !it.key.equals("show", ignoreCase = true)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showListPicker) {
            LibraryDropdownPicker(
                modifier = Modifier.focusRequester(primaryFocusRequester),
                upFocusRequester = upFocusRequester,
                title = stringResource(R.string.library_filter_list),
                value = selectedListLabel,
                selectedValue = selectedListKey,
                expanded = expandedPicker == "list",
                options = listTabs.map { LibraryOption(it.localizedTitle(), it.key) },
                onExpandedChange = { onExpandedChange("list", it) },
                onSelect = { onSelectList(it.value) }
            )
        }

        if (sortOptions.isNotEmpty()) {
            LibraryDropdownPicker(
                modifier = if (!showListPicker) Modifier.focusRequester(primaryFocusRequester) else Modifier,
                upFocusRequester = upFocusRequester,
                title = stringResource(R.string.library_filter_sort),
                value = selectedSortLabel,
                selectedValue = selectedSortOption.key,
                expanded = expandedPicker == "sort",
                options = sortOptions.map { LibraryOption(stringResource(it.labelResId), it.key) },
                onExpandedChange = { onExpandedChange("sort", it) },
                onSelect = { option ->
                    sortOptions.firstOrNull { it.key == option.value }?.let(onSelectSort)
                }
            )
        }

        if (genres.isNotEmpty()) {
            val genreAllOption = LibraryOption(allLabel, "__all__")
            LibraryDropdownPicker(
                upFocusRequester = upFocusRequester,
                title = stringResource(R.string.library_filter_genre),
                value = selectedGenreLabel,
                selectedValue = selectedGenre ?: "__all__",
                expanded = expandedPicker == "genre",
                options = listOf(genreAllOption) + genres.map {
                    LibraryOption("${localizedGenreLabel(it.label)} (${it.count})", it.key)
                },
                onExpandedChange = { onExpandedChange("genre", it) },
                onSelect = { option ->
                    onSelectGenre(if (option.value == "__all__") null else option.value)
                }
            )
        }

        if (years.isNotEmpty()) {
            val yearAllOption = LibraryOption(allLabel, "__all__")
            LibraryDropdownPicker(
                upFocusRequester = upFocusRequester,
                title = stringResource(R.string.library_filter_year),
                value = selectedYearLabel,
                selectedValue = selectedYear ?: "__all__",
                expanded = expandedPicker == "year",
                options = listOf(yearAllOption) + years.map {
                    LibraryOption("${it.label} (${it.count})", it.key)
                },
                onExpandedChange = { onExpandedChange("year", it) },
                onSelect = { option ->
                    onSelectYear(if (option.value == "__all__") null else option.value)
                }
            )
        }

        val watchedFilterLabel = stringResource(selectedWatchedFilter.labelResId)
        LibraryDropdownPicker(
            upFocusRequester = upFocusRequester,
            title = stringResource(R.string.library_filter_watched),
            value = watchedFilterLabel,
            selectedValue = selectedWatchedFilter.key,
            expanded = expandedPicker == "watched",
            options = LibraryWatchedFilter.entries.map {
                LibraryOption(stringResource(it.labelResId), it.key)
            },
            onExpandedChange = { onExpandedChange("watched", it) },
            onSelect = { option ->
                LibraryWatchedFilter.entries.firstOrNull { it.key == option.value }
                    ?.let(onSelectWatchedFilter)
            }
        )

        if (hasCustomTypes) {
            LibraryDropdownPicker(
                upFocusRequester = upFocusRequester,
                title = stringResource(R.string.library_filter_type),
                value = selectedTypeLabel,
                selectedValue = selectedTypeTab?.key,
                expanded = expandedPicker == "type",
                options = typeTabs.map {
                    val label = if (it.key == LibraryTypeTab.ALL_KEY) it.label else {
                        val countPart = it.label.substringAfterLast("(", "").removeSuffix(")")
                        val localizedName = localizedTypeLabel(it.key)
                        if (countPart.isNotBlank()) "$localizedName ($countPart)" else localizedName
                    }
                    LibraryOption(label, it.key)
                },
                onExpandedChange = { onExpandedChange("type", it) },
                onSelect = { option ->
                    typeTabs.firstOrNull { it.key == option.value }?.let(onSelectType)
                }
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun LibraryDropdownPicker(
    modifier: Modifier = Modifier,
    upFocusRequester: FocusRequester? = null,
    title: String,
    value: String,
    selectedValue: String?,
    expanded: Boolean,
    options: List<LibraryOption>,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (LibraryOption) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    var anchorSize by remember { mutableStateOf(IntSize.Zero) }
    // Seed focused option with the current selection so reopen highlights the right row
    // even before focus lands (fixes #2848 / incomplete #2507 race on TV).
    var focusedOptionValue by remember(expanded) {
        mutableStateOf(if (expanded) selectedValue else null)
    }
    val selectedItemFocusRequester = remember { FocusRequester() }
    val selectedBringIntoViewRequester = remember { BringIntoViewRequester() }

    // Popup content attaches focus targets a few frames after expand. A fixed 50ms
    // delay was flaky on TV and left focus on the first item for non-top selections.
    LaunchedEffect(expanded, selectedValue) {
        if (!expanded || selectedValue == null) return@LaunchedEffect
        var focused = selectedItemFocusRequester.requestFocusAfterFrames(frames = 3)
        var attempt = 0
        while (!focused && attempt < 6) {
            delay(32)
            focused = runCatching { selectedItemFocusRequester.requestFocus() }.getOrDefault(false)
            attempt++
        }
        if (!focused) return@LaunchedEffect
        runCatching { selectedBringIntoViewRequester.bringIntoView() }
        // Material DropdownMenu may still move initial focus to the first item after
        // the popup settles; re-assert once so long lists keep the real selection.
        delay(48)
        if (runCatching { selectedItemFocusRequester.requestFocus() }.getOrDefault(false)) {
            runCatching { selectedBringIntoViewRequester.bringIntoView() }
        }
    }

    Box(modifier = modifier) {
        Card(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier
                .widthIn(min = 180.dp)
                .then(
                    if (upFocusRequester != null) {
                        Modifier.focusProperties { up = upFocusRequester }
                    } else {
                        Modifier
                    }
                )
                .onSizeChanged { anchorSize = it }
                .onFocusChanged { isFocused = it.isFocused },
            shape = CardDefaults.shape(shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))),
            colors = CardDefaults.colors(
                containerColor = NuvioTheme.colors.BackgroundCard,
                focusedContainerColor = NuvioTheme.colors.FocusBackground
            ),
            border = CardDefaults.border(
                border = androidx.tv.material3.Border(
                    border = BorderStroke(NuvioTheme.spacing.hairline, NuvioTheme.colors.Border),
                    shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))
                ),
                focusedBorder = androidx.tv.material3.Border(
                    border = NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs),
                    shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))
                )
            ),
            scale = CardDefaults.scale(
                focusedScale = 1.0f,
                pressedScale = 1.0f
            )
        ) {
            Row(
                modifier = Modifier
                    .widthIn(min = 180.dp)
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = "$title:",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isFocused) NuvioTheme.colors.TextSecondary.copy(alpha = 0.9f) else NuvioTheme.colors.TextTertiary
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NuvioTheme.colors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) stringResource(R.string.cd_collapse, title) else stringResource(R.string.cd_expand, title),
                    tint = if (isFocused) NuvioTheme.colors.FocusRing else NuvioTheme.colors.TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                focusedOptionValue = null
                onExpandedChange(false)
            },
            modifier = Modifier
                .widthIn(min = with(LocalDensity.current) { maxOf(anchorSize.width.toDp(), 180.dp) })
                .heightIn(max = 320.dp),
            shape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp)),
            containerColor = NuvioTheme.colors.BackgroundCard,
            tonalElevation = NuvioTheme.spacing.none,
            shadowElevation = NuvioTheme.spacing.sm,
            border = BorderStroke(NuvioTheme.spacing.hairline, NuvioTheme.colors.Border)
        ) {
            options.forEach { option ->
                val isSelected = option.value == selectedValue
                val isOptionFocused = option.value == focusedOptionValue
                val itemTextColor = when {
                    isOptionFocused -> NuvioTheme.colors.OnSecondary
                    isSelected -> NuvioTheme.colors.TextPrimary
                    else -> NuvioTheme.colors.TextPrimary
                }
                val itemBackgroundColor = when {
                    isOptionFocused -> NuvioTheme.colors.Secondary
                    isSelected -> NuvioTheme.colors.FocusBackground
                    else -> Color.Transparent
                }

                DropdownMenuItem(
                    modifier = Modifier
                        .then(
                            if (isSelected) {
                                Modifier
                                    .focusRequester(selectedItemFocusRequester)
                                    .bringIntoViewRequester(selectedBringIntoViewRequester)
                            } else {
                                Modifier
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = NuvioTheme.spacing.xxs)
                        .background(
                            color = itemBackgroundColor,
                            shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))
                        )
                        .semantics {
                            // The current option is only distinguished by a background tint,
                            // so expose the selection state explicitly.
                            role = Role.RadioButton
                            this.selected = isSelected
                        }
                        .onFocusChanged { state ->
                            val hasFocus = state.isFocused || state.hasFocus
                            focusedOptionValue = when {
                                hasFocus -> option.value
                                focusedOptionValue == option.value -> null
                                else -> focusedOptionValue
                            }
                        },
                    text = {
                        Text(
                            text = option.label,
                            color = itemTextColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = { onSelect(option) },
                    colors = MenuDefaults.itemColors(
                        textColor = itemTextColor,
                        disabledTextColor = NuvioTheme.colors.TextDisabled
                    )
                )
            }
        }
    }
}

private data class LibraryOption(
    val label: String,
    val value: String
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun LibraryActionsRow(
    pending: Boolean,
    isSyncing: Boolean,
    showManageLists: Boolean,
    onManageLists: () -> Unit,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md)
    ) {
        if (showManageLists) {
            Button(
                onClick = onManageLists,
                enabled = !pending && !isSyncing,
                colors = ButtonDefaults.colors(
                    containerColor = NuvioTheme.colors.BackgroundCard,
                    contentColor = NuvioTheme.colors.TextPrimary
                )
            ) {
                Text(stringResource(R.string.library_manage_lists))
            }
        }
        Button(
            onClick = onRefresh,
            enabled = !pending && !isSyncing,
            colors = ButtonDefaults.colors(
                containerColor = NuvioTheme.colors.BackgroundCard,
                contentColor = NuvioTheme.colors.TextPrimary
            )
        ) {
            Text(if (isSyncing) stringResource(R.string.library_syncing_btn) else stringResource(R.string.library_sync_btn))
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ConfirmDeleteDialog(
    pending: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    NuvioDialog(
        onDismiss = onCancel,
        title = stringResource(R.string.library_delete_title),
        subtitle = stringResource(R.string.library_delete_subtitle),
        width = 420.dp
    ) {
        Button(
            onClick = onConfirm,
            enabled = !pending,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.colors(
                containerColor = Color(0xFF4A2323),
                contentColor = NuvioTheme.colors.TextPrimary
            )
        ) {
            Text(stringResource(R.string.library_list_delete))
        }
    }
}
