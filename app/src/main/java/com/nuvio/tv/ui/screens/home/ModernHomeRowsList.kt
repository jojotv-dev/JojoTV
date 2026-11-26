package com.nuvio.tv.ui.screens.home

import com.nuvio.tv.domain.model.catalogRowLegacyKey
import com.nuvio.tv.ui.theme.NuvioTheme

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.Job
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.foundation.focusGroup
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import coil3.imageLoader
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import com.nuvio.tv.domain.model.ContinueWatchingCardStyle
import com.nuvio.tv.domain.model.FocusedPosterTrailerPlaybackTarget
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.isPlaceholder
import com.nuvio.tv.ui.util.StableList
import com.nuvio.tv.ui.util.StableMap
import com.nuvio.tv.ui.util.StableRef
import com.nuvio.tv.ui.util.recompositionHighlighter
import com.nuvio.tv.ui.components.rememberPlaceholderShimmerOffsetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

internal fun HeroCarouselRow.firstMediaCatalogIndexOnReturn(): Int? {
    val isVideoCatalogRow =
        apiType.equals("video", ignoreCase = true) ||
            apiType.equals("movie", ignoreCase = true) ||
            apiType.equals("series", ignoreCase = true) ||
            apiType.equals("tv", ignoreCase = true)
    if (!isVideoCatalogRow) return null

    return continuousItemIndices().firstOrNull { index ->
        items.list.getOrNull(index)?.payload is ModernPayload.Catalog
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalTvMaterial3Api::class,
    ExperimentalComposeUiApi::class,
    kotlinx.coroutines.FlowPreview::class
)
@Composable
internal fun ModernHomeRowsList(
    isVerticalRowsScrollingState: State<Boolean>,
    carouselRows: StableList<HeroCarouselRow>,
    verticalRowListState: LazyListState,
    focusedItemByRow: StableRef<MutableMap<String, Int>>,
    rowListStates: StableRef<MutableMap<String, LazyListState>>,
    loadMoreRequestedTotals: StableRef<MutableMap<String, Int>>,
    focusState: HomeScreenFocusState,
    activeRowKey: State<String?>,
    activeItemIndex: State<Int>,
    contentFocusRequester: FocusRequester,
    rowsViewportHeight: Dp,
    modernContinueWatchingHeroEnabled: Boolean = false,
    modernContinueWatchingHeroHeight: Dp = 0.dp,
    catalogBottomPadding: Dp,
    trailerContentAlpha: () -> Float,
    verticalRowBringIntoViewSpec: BringIntoViewSpec,
    onRowItemFocusedInternal: (String, Int, Boolean) -> Unit,
    onNavigateToDetail: (String, String, String) -> Unit,
    onNavigateToFolderDetail: (String, String) -> Unit,
    onLoadMoreCatalog: (String, String, String) -> Unit,
    onContinueWatchingClick: (ContinueWatchingItem) -> Unit,
    onContinueWatchingOptions: (ContinueWatchingItem) -> Unit,
    onRequestLazyCatalogLoad: (String) -> Unit,
    onBackdropInteraction: () -> Unit,
    onExpandedCatalogFocusKeyChange: (String?) -> Unit,
    isCatalogItemWatched: (MetaPreview) -> Boolean,
    onCatalogItemLongPress: (MetaPreview, String, Boolean) -> Unit,
    onItemFocus: (MetaPreview) -> Unit,
    onPreloadAdjacentItem: (MetaPreview) -> Unit,
    enrichedPreviews: State<StableMap<String, MetaPreview>>,
    trailerPreviewUrls: StableMap<String, String>,
    trailerPreviewAudioUrls: StableMap<String, String>,
    useLandscapePosters: Boolean,
    alwaysShowLandscapeClearlogo: Boolean = false,
    showLabels: Boolean,
    posterCardCornerRadius: Dp,
    installedAppTileWidth: Dp,
    installedAppTileCornerRadius: Dp,
    focusedPosterBackdropTrailerMuted: Boolean,
    effectiveExpandEnabled: Boolean,
    effectiveAutoplayEnabled: Boolean,
    trailerPlaybackTarget: FocusedPosterTrailerPlaybackTarget,
    expandedCatalogFocusKey: State<String?>,
    expandedTrailerPreviewUrl: () -> String?,
    expandedTrailerPreviewAudioUrl: () -> String?,
    portraitCatalogCardWidth: Dp,
    portraitCatalogCardHeight: Dp,
    landscapeCatalogCardWidth: Dp,
    landscapeCatalogCardHeight: Dp,
    continueWatchingCardWidth: Dp,
    continueWatchingCardHeight: Dp,
    blurUnwatchedEpisodes: Boolean,
    useEpisodeThumbnails: Boolean,
    continueWatchingCardStyle: ContinueWatchingCardStyle,
    continueWatchingCornerRadius: Dp,
    pendingRowFocusKey: State<String?>,
    pendingRowFocusIndex: State<Int?>,
    pendingRowFocusNonce: State<Int>,
    onPendingRowFocusCleared: () -> Unit,
    onActiveRowKeyChange: (String?) -> Unit,
    onActiveItemIndexChange: (Int) -> Unit,
    onRequestRowFocus: (String, Int) -> Unit,
    lastHeroNavigationAtMs: State<Long>,
    onLastHeroNavigationAtMsChange: (Long) -> Unit,
    onHeroFocusSettleDelayChange: (Long) -> Unit,
    lastFocusedContinueWatchingIndex: State<Int>,
    onLastFocusedContinueWatchingIndexChange: (Int) -> Unit,
    focusedCatalogSelection: State<FocusedCatalogSelection?>,
    onFocusedCatalogSelectionChange: (FocusedCatalogSelection?) -> Unit,
    focusedHeroMediaNonce: State<Int>,
    onFocusedHeroMediaNonceChange: (Int) -> Unit,
    onExpansionInteractionNonceChange: (Int) -> Unit,
    blockLeftOnFirstExpandedItem: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Unwrap StableRef wrappers for internal use (not passed to child composables)
    val focusedItemByRowMap = focusedItemByRow.value
    val rowListStatesMap = rowListStates.value
    val loadMoreRequestedTotalsMap = loadMoreRequestedTotals.value
    val rowFocusResetScope = rememberCoroutineScope()
    var manualRowFocusTarget by remember { mutableStateOf<String?>(null) }
    var rowFocusTransferJob by remember { mutableStateOf<Job?>(null) }

    val latestOnActiveRowKeyChange = rememberUpdatedState(onActiveRowKeyChange)
    val latestOnActiveItemIndexChange = rememberUpdatedState(onActiveItemIndexChange)
    val latestOnRequestRowFocus = rememberUpdatedState(onRequestRowFocus)

    val rowFocusRequesters = remember { mutableMapOf<String, FocusRequester>() }
    val stableItemFocusRequestersByRow = remember { mutableMapOf<String, StableRef<MutableMap<Int, FocusRequester>>>() }

    val density = LocalDensity.current
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current
    val verticalPrefetchImageLoader = context.imageLoader
    val latestCarouselRowsForImagePrefetch = rememberUpdatedState(carouselRows)

    LaunchedEffect(
        verticalPrefetchImageLoader,
        verticalRowListState,
        density,
        useLandscapePosters,
        effectiveExpandEnabled,
        portraitCatalogCardWidth,
        portraitCatalogCardHeight,
        landscapeCatalogCardWidth,
        landscapeCatalogCardHeight
    ) {
        val prefetchAheadRows = 1
        val prefetchItemsPerRow = 1
        snapshotFlow {
            verticalRowListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
        }
            .distinctUntilChanged()
            .debounce(240L) // VERTICAL_PREFETCH_DEBOUNCE_MS
            .collect { lastVisibleRowIndex ->
                withContext(Dispatchers.IO) {
                    for (rowOffset in 1..prefetchAheadRows) {
                        val row = latestCarouselRowsForImagePrefetch.value.list
                            .getOrNull(lastVisibleRowIndex + rowOffset) ?: continue
                        for (i in 0 until minOf(prefetchItemsPerRow, row.items.list.size)) {
                            val item = row.items.list[i]
                            val url = item.imageUrl ?: continue
                            val metrics = item.catalogCardRequestMetrics(
                                useLandscapePosters = useLandscapePosters,
                                portraitCardWidth = portraitCatalogCardWidth,
                                portraitCardHeight = portraitCatalogCardHeight,
                                landscapeCardWidth = landscapeCatalogCardWidth,
                                landscapeCardHeight = landscapeCatalogCardHeight,
                                expandEnabled = effectiveExpandEnabled
                            )
                            val wPx = with(density) { metrics.width.roundToPx() }
                            val hPx = with(density) { metrics.height.roundToPx() }
                            if (wPx <= 0 || hPx <= 0) continue
                            val cacheKey = "${url}_${wPx}x${hPx}"
                            if (verticalPrefetchImageLoader.memoryCache?.get(MemoryCache.Key(cacheKey)) != null) continue
                            verticalPrefetchImageLoader.enqueue(
                                ImageRequest.Builder(context)
                                    .data(url)
                                    .memoryCacheKey(cacheKey)
                                    .size(width = wPx, height = hPx)
                                    .build()
                            )
                        }
                    }
                }
            }
    }

    val latestOnRequestLazyCatalogLoad = rememberUpdatedState(onRequestLazyCatalogLoad)
    val latestCarouselRowsForLazy = rememberUpdatedState(carouselRows)
    LaunchedEffect(verticalRowListState) {
        val prefetchAheadForLazy = 2
        snapshotFlow {
            val info = verticalRowListState.layoutInfo
            val firstVisible = info.visibleItemsInfo.firstOrNull()?.index ?: -1
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            firstVisible to lastVisible
        }.collectLatest { (firstVisible, lastVisible) ->
            if (lastVisible < 0) return@collectLatest
            // Debounce: restarts on every new emission during rapid scroll.
            // Only fires when visible indices stabilize for 240ms.
            delay(240)
            val rows = latestCarouselRowsForLazy.value
            for (idx in firstVisible.coerceAtLeast(0)..(lastVisible + prefetchAheadForLazy)) {
                val row = rows.list.getOrNull(idx) ?: continue
                if (row.isLoading && row.items.list.firstOrNull()?.imageUrl.isPlaceholder()) {
                    val legacyKey = catalogRowLegacyKey(
                        row.addonId ?: continue,
                        row.apiType ?: continue,
                        row.catalogId ?: continue
                    )
                    latestOnRequestLazyCatalogLoad.value(legacyKey)
                }
            }
        }
    }

    val focusRestorerRequester = remember(activeRowKey) {
        {
            activeRowKey.value?.let { rowFocusRequesters[it] } ?: FocusRequester.Default
        }
    }

    fun moveFocusToOrderedRow(targetPosition: Int): Boolean {
        val targetRow = carouselRows.list.getOrNull(targetPosition) ?: return false
        val targetKey = targetRow.key
        val targetLastIndex = targetRow.items.list.lastIndex
        val firstMediaItemIndex = targetRow.firstMediaCatalogIndexOnReturn()
        val requestedIndex = when {
            targetKey == MODERN_CONTINUE_WATCHING_ROW_KEY && lastFocusedContinueWatchingIndex.value >= 0 ->
                lastFocusedContinueWatchingIndex.value
            firstMediaItemIndex != null -> firstMediaItemIndex
            else -> focusedItemByRowMap[targetKey] ?: 0
        }.coerceIn(0, targetLastIndex.coerceAtLeast(0))

        rowFocusTransferJob?.cancel()
        manualRowFocusTarget = targetKey
        focusedItemByRowMap[targetKey] = requestedIndex
        latestOnActiveRowKeyChange.value(targetKey)
        latestOnActiveItemIndexChange.value(requestedIndex)
        latestOnRequestRowFocus.value(targetKey, requestedIndex)

        val targetScrollOffset = if (targetPosition == 0) {
            -with(density) { MODERN_ROW_HEADER_FOCUS_INSET.roundToPx() }
        } else {
            0
        }
        val rowLayoutInfo = verticalRowListState.layoutInfo
        val targetAlignedOffset = rowLayoutInfo.viewportStartOffset - targetScrollOffset
        // Keep every focused row at the same top anchor. A visibility threshold left the TV row
        // lower in the viewport when it was mostly visible, with part of the Series row above it.
        val targetAlreadyAligned = rowLayoutInfo.visibleItemsInfo
            .firstOrNull { it.index == targetPosition }
            ?.let { item -> kotlin.math.abs(item.offset - targetAlignedOffset) <= 1 }
            ?: false

        val targetRowFocusRequester = rowFocusRequesters.getOrPut(targetKey) { FocusRequester() }
        stableItemFocusRequestersByRow.getOrPut(targetKey) { StableRef(mutableMapOf()) }
        rowFocusTransferJob = rowFocusResetScope.launch {
            try {
                if (manualRowFocusTarget != targetKey) return@launch
                if (!targetAlreadyAligned) {
                    verticalRowListState.scrollToItem(targetPosition, targetScrollOffset)
                }
                var focusTransferred = false
                repeat(3) {
                    androidx.compose.runtime.withFrameNanos { }
                    if (manualRowFocusTarget != targetKey) return@launch
                    if (!focusTransferred) {
                        val requester = stableItemFocusRequestersByRow[targetKey]
                            ?.value
                            ?.get(requestedIndex)
                            ?: targetRowFocusRequester
                        focusTransferred = runCatching { requester.requestFocus() }.getOrDefault(false)
                    }
                }
                if (!targetAlreadyAligned && manualRowFocusTarget == targetKey) {
                    androidx.compose.runtime.withFrameNanos { }
                    verticalRowListState.scrollToItem(targetPosition, targetScrollOffset)
                }
            } finally {
                if (manualRowFocusTarget == targetKey) {
                    manualRowFocusTarget = null
                    rowFocusTransferJob = null
                }
            }
        }
        return true
    }

    // Keep the tall Continue Watching hero anchored at the start when it receives focus.
    val continueWatchingHeroIndex = carouselRows.list.indexOfFirst {
        it.key == MODERN_CONTINUE_WATCHING_ROW_KEY
    }
    val activeRowPosition = carouselRows.list.indexOfFirst { it.key == activeRowKey.value }
    LaunchedEffect(
        activeRowKey.value,
        activeRowPosition,
        modernContinueWatchingHeroEnabled,
        manualRowFocusTarget
    ) {
        if (activeRowPosition < 0 ||
            manualRowFocusTarget == activeRowKey.value ||
            (modernContinueWatchingHeroEnabled && activeRowKey.value == MODERN_CONTINUE_WATCHING_ROW_KEY)
        ) return@LaunchedEffect

        val targetScrollOffset = if (activeRowPosition == 0) {
            -with(density) { MODERN_ROW_HEADER_FOCUS_INSET.roundToPx() }
        } else {
            0
        }
        val rowLayoutInfo = verticalRowListState.layoutInfo
        val targetAlignedOffset = rowLayoutInfo.viewportStartOffset - targetScrollOffset
        val targetAlreadyAligned = rowLayoutInfo.visibleItemsInfo
            .firstOrNull { it.index == activeRowPosition }
            ?.let { item -> kotlin.math.abs(item.offset - targetAlignedOffset) <= 1 }
            ?: false

        // Also align rows restored from saved focus, where no D-pad transition runs.
        if (!targetAlreadyAligned) {
            verticalRowListState.scrollToItem(activeRowPosition, targetScrollOffset)
        }
    }
    LaunchedEffect(modernContinueWatchingHeroEnabled, activeRowKey.value, continueWatchingHeroIndex) {
        androidx.compose.runtime.withFrameNanos { }
        if (modernContinueWatchingHeroEnabled &&
            activeRowKey.value == MODERN_CONTINUE_WATCHING_ROW_KEY &&
            continueWatchingHeroIndex >= 0
        ) {
            verticalRowListState.scrollToItem(continueWatchingHeroIndex, 0)
        }
    }

    val defaultBringIntoViewSpec = LocalBringIntoViewSpec.current

    // Only run the shared shimmer while a row on screen actually draws it: rememberInfiniteTransition
    // keeps waking the Compose frame clock on every frame for as long as it is composed, even when
    // nothing reads its value. Rows below the fold stay placeholders until they are scrolled to, so
    // the check has to be on the visible rows, not on the whole list. Keyed on the list state for
    // the same reason isVerticalRowsScrollingState is in ModernHomeContent: rememberLazyListState
    // is saveable-backed and can hand back a new instance, and a derived state still holding the
    // old one would read a layout that has stopped updating.
    val needsPlaceholderShimmer by remember(verticalRowListState) {
        derivedStateOf {
            val rows = latestCarouselRowsForLazy.value.list
            verticalRowListState.layoutInfo.visibleItemsInfo.any { visibleRow ->
                rows.getOrNull(visibleRow.index)?.showsPlaceholderShimmer() == true
            }
        }
    }
    val sharedPlaceholderShimmerOffsetState = if (needsPlaceholderShimmer) {
        rememberPlaceholderShimmerOffsetState(label = "sharedRowShimmer")
    } else {
        null
    }

    CompositionLocalProvider(
        LocalBringIntoViewSpec provides verticalRowBringIntoViewSpec,
        LocalVerticalRowsScrolling provides isVerticalRowsScrollingState
    ) {
        LazyColumn(
            state = verticalRowListState,
            modifier = modifier
                .fillMaxWidth()
                .recompositionHighlighter()
                .height(rowsViewportHeight)
                .padding(bottom = catalogBottomPadding)
                .clipToBounds()
                .graphicsLayer { alpha = trailerContentAlpha() }
                .focusRequester(contentFocusRequester)
                .focusRestorer { focusRestorerRequester() }
                .onPreviewKeyEvent { event ->
                    val firstRowKey = carouselRows.list.firstOrNull()?.key
                    val lastRowKey = carouselRows.list.lastOrNull()?.key
                    if (event.type == KeyEventType.KeyDown &&
                        event.key == Key.DirectionUp &&
                        effectiveExpandEnabled &&
                        expandedCatalogFocusKey.value != null &&
                        activeRowKey.value == firstRowKey &&
                        firstRowKey != MODERN_CONTINUE_WATCHING_ROW_KEY
                    ) return@onPreviewKeyEvent true
                    if (event.type == KeyEventType.KeyDown &&
                        event.key == Key.DirectionDown &&
                        effectiveExpandEnabled &&
                        expandedCatalogFocusKey.value != null &&
                        activeRowKey.value == lastRowKey
                    ) return@onPreviewKeyEvent true
                    val blockKey = if (layoutDirection == LayoutDirection.Rtl)
                        Key.DirectionRight else Key.DirectionLeft
                    if (blockLeftOnFirstExpandedItem &&
                        event.type == KeyEventType.KeyDown &&
                        event.key == blockKey &&
                        effectiveExpandEnabled &&
                        expandedCatalogFocusKey.value != null &&
                        activeItemIndex.value == 0
                    ) return@onPreviewKeyEvent true
                    false
                },
            contentPadding = PaddingValues(
                bottom = rowsViewportHeight
            ),
            verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.xl)
        ) {
            itemsIndexed(
                items = carouselRows.list,
                key = { index, row -> "${row.key}_$index" },
                contentType = { _, row -> row.apiType ?: "modern_home_row" }
            ) { rowIndex, row ->
                val stableOnContinueWatchingOptions = remember(onContinueWatchingOptions) {
                    { item: ContinueWatchingItem -> onContinueWatchingOptions(item) }
                }
                val stableOnRowItemFocused = remember {
                    { rowKey: String, index: Int, isContinueWatchingRow: Boolean ->
                        val rowBecameActive = activeRowKey.value != rowKey
                        val itemChanged = activeItemIndex.value != index

                        if (rowBecameActive || itemChanged) {
                            val now = System.currentTimeMillis()
                            val timeSinceLastHeroNav = now - lastHeroNavigationAtMs.value
                            onHeroFocusSettleDelayChange(
                                if (lastHeroNavigationAtMs.value != 0L &&
                                    timeSinceLastHeroNav in 1 until 130L // MODERN_HERO_RAPID_NAV_THRESHOLD_MS
                                ) 400L // MODERN_HERO_RAPID_NAV_SETTLE_MS
                                else 450L // MODERN_HERO_FOCUS_DEBOUNCE_MS
                            )
                            onLastHeroNavigationAtMsChange(now)
                            onActiveRowKeyChange(rowKey)
                            onActiveItemIndexChange(index)
                        }

                        // Always keep the focusedItemByRow map in sync for ALL rows
                        if (focusedItemByRowMap[rowKey] != index) {
                            focusedItemByRowMap[rowKey] = index
                        }

                        if (rowKey == com.nuvio.tv.core.apps.INSTALLED_APPS_CATALOG_KEY) {
                            onFocusedCatalogSelectionChange(null)
                            onExpandedCatalogFocusKeyChange(null)
                        }
                        if (isContinueWatchingRow) {
                            if (lastFocusedContinueWatchingIndex.value != index) {
                                onLastFocusedContinueWatchingIndexChange(index)
                            }
                            if (focusedCatalogSelection.value != null) {
                                onFocusedCatalogSelectionChange(null)
                            }
                        }
                        onRowItemFocusedInternal(rowKey, index, isContinueWatchingRow)
                    }
                }
                val isActiveRowLambda = remember(row.key) {
                    { row.key == activeRowKey.value }
                }
                val stableOnCatalogSelectionFocused = remember {
                    { selection: FocusedCatalogSelection ->
                        val isCollectionFolder = selection.payload is ModernPayload.CollectionFolder
                        if (focusedCatalogSelection.value != selection || isCollectionFolder) {
                            onFocusedCatalogSelectionChange(selection)
                            if (isCollectionFolder) {
                                onFocusedHeroMediaNonceChange(focusedHeroMediaNonce.value + 1)
                            }
                        }
                    }
                }
                ModernRowSection(
                    row = row,
                    modernContinueWatchingHeroEnabled = modernContinueWatchingHeroEnabled,
                    modernContinueWatchingHeroHeight = modernContinueWatchingHeroHeight,
                    isActiveRow = isActiveRowLambda,
                    rowFocusRequester = rowFocusRequesters.getOrPut(row.key) { FocusRequester() },
                    onMoveUp = if (rowIndex > 0) {
                        { moveFocusToOrderedRow(rowIndex - 1) }
                    } else null,
                    onMoveDown = { moveFocusToOrderedRow(rowIndex + 1) },
                    rowTitleBottom = 14.dp, // rowTitleBottom
                    defaultBringIntoViewSpec = defaultBringIntoViewSpec,
                    focusStateCatalogRowScrollIndex = focusState.catalogRowScrollStates[row.key] ?: 0,
                    focusStateCatalogRowScrollAnchor = focusState.catalogRowScrollAnchors[row.key],
                    focusedItemByRow = focusedItemByRow,
                    rowListStates = rowListStates,
                    loadMoreRequestedTotals = loadMoreRequestedTotals,
                    pendingRowFocusKey = pendingRowFocusKey,
                    pendingRowFocusIndex = pendingRowFocusIndex,
                    pendingRowFocusNonce = pendingRowFocusNonce,
                    onPendingRowFocusCleared = onPendingRowFocusCleared,
                    onRowItemFocused = stableOnRowItemFocused,
                    useLandscapePosters = useLandscapePosters,
                    alwaysShowLandscapeClearlogo = alwaysShowLandscapeClearlogo,
                    showLabels = showLabels,
                    posterCardCornerRadius = posterCardCornerRadius,
                    installedAppTileWidth = installedAppTileWidth,
                    installedAppTileCornerRadius = installedAppTileCornerRadius,
                    focusedPosterBackdropTrailerMuted = focusedPosterBackdropTrailerMuted,
                    effectiveExpandEnabled = effectiveExpandEnabled,
                    effectiveAutoplayEnabled = effectiveAutoplayEnabled,
                    trailerPlaybackTarget = trailerPlaybackTarget,
                    expandedCatalogFocusKey = expandedCatalogFocusKey,
                    expandedTrailerPreviewUrl = expandedTrailerPreviewUrl,
                    expandedTrailerPreviewAudioUrl = expandedTrailerPreviewAudioUrl,
                    portraitCatalogCardWidth = portraitCatalogCardWidth,
                    portraitCatalogCardHeight = portraitCatalogCardHeight,
                    landscapeCatalogCardWidth = landscapeCatalogCardWidth,
                    landscapeCatalogCardHeight = landscapeCatalogCardHeight,
                    continueWatchingCardWidth = continueWatchingCardWidth,
                    continueWatchingCardHeight = continueWatchingCardHeight,
                    blurUnwatchedEpisodes = blurUnwatchedEpisodes,
                    useEpisodeThumbnails = useEpisodeThumbnails,
                    continueWatchingCardStyle = continueWatchingCardStyle,
                    continueWatchingCornerRadius = continueWatchingCornerRadius,
                    onContinueWatchingClick = onContinueWatchingClick,
                    onContinueWatchingOptions = stableOnContinueWatchingOptions,
                    isCatalogItemWatched = isCatalogItemWatched,
                    onCatalogItemLongPress = onCatalogItemLongPress,
                    onItemFocus = onItemFocus,
                    onPreloadAdjacentItem = onPreloadAdjacentItem,
                    enrichedPreviews = enrichedPreviews,
                    onCatalogSelectionFocused = stableOnCatalogSelectionFocused,
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToFolderDetail = onNavigateToFolderDetail,
                    onLoadMoreCatalog = onLoadMoreCatalog,
                    onBackdropInteraction = onBackdropInteraction,
                    onExpandedCatalogFocusKeyChange = onExpandedCatalogFocusKeyChange,
                    sharedPlaceholderShimmerOffsetState = sharedPlaceholderShimmerOffsetState,
                    isVerticalRowsScrollingState = isVerticalRowsScrollingState,
                    itemFocusRequesters = stableItemFocusRequestersByRow.getOrPut(row.key) {
                        StableRef(mutableMapOf())
                    }
                )
            }
        }
    }
}
