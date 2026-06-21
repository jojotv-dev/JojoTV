package com.nuvio.tv.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import coil3.imageLoader
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import com.nuvio.tv.data.freebox.freeboxContentIdForEntry
import com.nuvio.tv.domain.model.FocusedPosterTrailerPlaybackTarget
import com.nuvio.tv.ui.components.FreeboxVideosSection
import com.nuvio.tv.data.freebox.FreeboxFileEntry
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.ui.util.StableList
import com.nuvio.tv.ui.util.StableMap
import com.nuvio.tv.ui.util.StableRef
import com.nuvio.tv.ui.util.dpadVerticalFastScroll
import com.nuvio.tv.ui.util.recompositionHighlighter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext

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
    isFastScrolling: State<Boolean>,
    onFastScrollingChanged: (Boolean) -> Unit,
    contentFocusRequester: FocusRequester,
    rowsViewportHeight: Dp,
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
    onCatalogItemLongPress: (MetaPreview, String) -> Unit,
    onItemFocus: (MetaPreview) -> Unit,
    onPreloadAdjacentItem: (MetaPreview) -> Unit,
    enrichedPreviews: State<StableMap<String, MetaPreview>>,
    trailerPreviewUrls: StableMap<String, String>,
    trailerPreviewAudioUrls: StableMap<String, String>,
    useLandscapePosters: Boolean,
    showLabels: Boolean,
    posterCardCornerRadius: Dp,
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
    videoCardWidth: Dp,
    videoCardHeight: Dp,
    movieFavoritesPortraitMode: Boolean,
    seriesFavoritesPortraitMode: Boolean,
    blurUnwatchedEpisodes: Boolean,
    useEpisodeThumbnails: Boolean,
    pendingRowFocusKey: State<String?>,
    pendingRowFocusIndex: State<Int?>,
    pendingRowFocusNonce: State<Int>,
    onPendingRowFocusCleared: () -> Unit,
    onActiveRowKeyChange: (String?) -> Unit,
    onActiveItemIndexChange: (Int) -> Unit,
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
    freeboxVideoEntries: List<com.nuvio.tv.data.freebox.FreeboxFileEntry> = emptyList(),
    continueWatchingContentIds: Set<String> = emptySet(),
    onNavigateToFreebox: (String, String?) -> Unit = { _, _ -> },
    freeboxVideoArtwork: Map<String, String> = emptyMap(),
    freeboxVideoBackdrops: Map<String, String> = emptyMap(),
    freeboxVideoProbedDurations: Map<String, Long> = emptyMap(),
    onDeleteFreeboxVideo: (com.nuvio.tv.data.freebox.FreeboxFileEntry) -> Unit = {},
    onRenameFreeboxVideo: (com.nuvio.tv.data.freebox.FreeboxFileEntry, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    // Unwrap StableRef wrappers for internal use (not passed to child composables)
    val focusedItemByRowMap = focusedItemByRow.value
    val rowListStatesMap = rowListStates.value
    val loadMoreRequestedTotalsMap = loadMoreRequestedTotals.value

    val latestOnActiveRowKeyChange = rememberUpdatedState(onActiveRowKeyChange)
    val latestOnActiveItemIndexChange = rememberUpdatedState(onActiveItemIndexChange)

    val rowFocusRequesters = remember { mutableMapOf<String, FocusRequester>() }
    val stableItemFocusRequestersByRow = remember { mutableMapOf<String, StableRef<MutableMap<Int, FocusRequester>>>() }

    val density = LocalDensity.current
    val context = LocalContext.current
    val verticalPrefetchImageLoader = context.imageLoader
    val movieFavoritesRowSuffix = "_${HomeViewModel.IPTV_MOVIE_FAVORITES_CATALOG_ID}"
    val seriesFavoritesRowSuffix = "_${HomeViewModel.IPTV_SERIES_FAVORITES_CATALOG_ID}"
    val orderedRows = remember(carouselRows, freeboxVideoEntries) {
        val rows = carouselRows.list
        val continueWatchingRows = rows.filter { it.key == MODERN_CONTINUE_WATCHING_ROW_KEY }
        val movieFavoriteRows = rows.filter { it.key.endsWith(movieFavoritesRowSuffix) }
        val seriesFavoriteRows = rows.filter { it.key.endsWith(seriesFavoritesRowSuffix) }
        val prioritizedKeys = (continueWatchingRows + movieFavoriteRows + seriesFavoriteRows)
            .mapTo(mutableSetOf()) { it.key }

        buildList<Pair<String, HeroCarouselRow?>> {
            continueWatchingRows.forEach { add(it.key to it) }
            if (freeboxVideoEntries.isNotEmpty()) add("freebox_videos" to null)
            movieFavoriteRows.forEach { add(it.key to it) }
            seriesFavoriteRows.forEach { add(it.key to it) }
            rows.filterNot { it.key in prioritizedKeys }.forEach { add(it.key to it) }
        }
    }
    val latestOrderedRowsForLazy = rememberUpdatedState(orderedRows)

    LaunchedEffect(verticalPrefetchImageLoader, density) {
        val prefetchAheadRows = 1
        val prefetchItemsPerRow = 1
        snapshotFlow {
            verticalRowListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
        }
            .distinctUntilChanged()
            .debounce(120L) // VERTICAL_PREFETCH_DEBOUNCE_MS
            .collect { lastVisibleRowIndex ->
                withContext(Dispatchers.IO) {
                    for (rowOffset in 1..prefetchAheadRows) {
                        val row = latestOrderedRowsForLazy.value.getOrNull(lastVisibleRowIndex + rowOffset)?.second ?: continue
                        for (i in 0 until minOf(prefetchItemsPerRow, row.items.list.size)) {
                            val item = row.items.list[i]
                            val url = item.imageUrl ?: continue
                            val metrics = item.catalogCardRequestMetrics(
                                useLandscapePosters = when {
                                    row.key.endsWith(movieFavoritesRowSuffix) -> !movieFavoritesPortraitMode
                                    row.key.endsWith(seriesFavoritesRowSuffix) -> !seriesFavoritesPortraitMode
                                    else -> useLandscapePosters
                                },
                                portraitCardWidth = portraitCatalogCardWidth,
                                portraitCardHeight = portraitCatalogCardHeight,
                                landscapeCardWidth = landscapeCatalogCardWidth,
                                landscapeCardHeight = landscapeCatalogCardHeight,
                                expandEnabled = effectiveExpandEnabled
                            )
                            val wPx = with(density) { metrics.width.roundToPx() }
                            val hPx = with(density) { metrics.height.roundToPx() }
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
    LaunchedEffect(verticalRowListState) {
        val prefetchAheadForLazy = 1
        snapshotFlow {
            val scrolling = verticalRowListState.isScrollInProgress
            val info = verticalRowListState.layoutInfo
            val firstVisible = info.visibleItemsInfo.firstOrNull()?.index ?: -1
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            Triple(scrolling, firstVisible, lastVisible)
        }.collect { (scrolling, firstVisible, lastVisible) ->
            if (scrolling || lastVisible < 0) return@collect
            delay(150)
            if (verticalRowListState.isScrollInProgress) return@collect
            val rows = latestOrderedRowsForLazy.value
            for (idx in firstVisible.coerceAtLeast(0)..(lastVisible + prefetchAheadForLazy)) {
                val row = rows.getOrNull(idx)?.second ?: continue
                if (row.isLoading && row.items.list.firstOrNull()?.imageUrl == "placeholder://empty") {
                    latestOnRequestLazyCatalogLoad.value(row.key)
                }
            }
        }
    }

    // Secondary trigger: when scroll settles after focus-driven BringIntoView,
    // check again for placeholder rows that need loading. The primary snapshotFlow
    // above may miss this if visible indices didn't change.
    LaunchedEffect(verticalRowListState) {
        snapshotFlow { verticalRowListState.isScrollInProgress }
            .collect { scrolling ->
                if (scrolling) return@collect
                delay(200)
                if (verticalRowListState.isScrollInProgress) return@collect
                val rows = latestOrderedRowsForLazy.value
                val info = verticalRowListState.layoutInfo
                val firstVisible = info.visibleItemsInfo.firstOrNull()?.index ?: return@collect
                val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: return@collect
                for (idx in firstVisible.coerceAtLeast(0)..(lastVisible + 1)) {
                    val row = rows.getOrNull(idx)?.second ?: continue
                    if (row.isLoading && row.items.list.firstOrNull()?.imageUrl == "placeholder://empty") {
                        latestOnRequestLazyCatalogLoad.value(row.key)
                    }
                }
            }
    }

    val focusRestorerRequester = remember(activeRowKey) {
        {
            activeRowKey.value?.let { rowFocusRequesters[it] } ?: FocusRequester.Default
        }
    }

    val defaultBringIntoViewSpec = LocalBringIntoViewSpec.current

    CompositionLocalProvider(
        LocalBringIntoViewSpec provides verticalRowBringIntoViewSpec,
        LocalFastScrollActive provides isFastScrolling,
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
                .focusRestorer(focusRestorerRequester)
                .dpadVerticalFastScroll(
                    scrollableState = verticalRowListState,
                    onFastScrollingChanged = onFastScrollingChanged,
                    shouldHaltForward = {
                        val info = verticalRowListState.layoutInfo
                        val lastIdx = orderedRows.size - 1
                        val lastVisible = info.visibleItemsInfo.lastOrNull { it.index == lastIdx }
                        lastIdx >= 0 && lastVisible != null &&
                            lastVisible.offset + lastVisible.size <= info.viewportEndOffset
                    },
                    resolveVerticalLanding = { sign ->
                        val layoutInfo = verticalRowListState.layoutInfo
                        val visibleItems = layoutInfo.visibleItemsInfo
                        val lastIdx = orderedRows.size - 1
                        val viewportEnd = layoutInfo.viewportEndOffset
                        val lastRowAtBottom = lastIdx >= 0 &&
                            visibleItems.lastOrNull { it.index == lastIdx }?.let {
                                it.offset + it.size <= viewportEnd
                            } == true
                        val upwardTopRow: LazyListItemInfo? = if (sign < 0) {
                            visibleItems.firstOrNull()?.takeIf {
                                it.offset > -it.size / 2
                            }
                        } else null
                        val targetRowIndex = when {
                            lastRowAtBottom -> lastIdx
                            upwardTopRow != null -> upwardTopRow.index
                            else ->
                                visibleItems.firstOrNull { it.offset >= 0 }?.index
                                    ?: visibleItems.firstOrNull()?.index
                                    ?: verticalRowListState.firstVisibleItemIndex
                        }
                        val targetRow = orderedRows.getOrNull(targetRowIndex)?.second
                        if (targetRow == null) null
                        else {
                            val savedIdx = (focusedItemByRowMap[targetRow.key] ?: 0)
                                .coerceIn(0, (targetRow.items.list.size - 1).coerceAtLeast(0))
                            latestOnActiveRowKeyChange.value(targetRow.key)
                            latestOnActiveItemIndexChange.value(savedIdx)

                            val targetItemKey = targetRow.items.list.getOrNull(savedIdx)?.key
                                ?: "${targetRow.key}_$savedIdx"
                            targetItemKey
                        }
                    },
                ),
            contentPadding = PaddingValues(bottom = rowsViewportHeight),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            itemsIndexed(
                items = orderedRows,
                key = { _, entry -> entry.first },
                contentType = { _, entry -> entry.second?.apiType ?: "freebox_videos" }
            ) { _, entry ->
                val row = entry.second
                if (row == null) {
                    FreeboxVideosSection(
                        entries = freeboxVideoEntries,
                        probedDurations = freeboxVideoProbedDurations,
                        onItemClick = { video ->
                            val contentId = freeboxContentIdForEntry(video)
                            onNavigateToFreebox(contentId, freeboxVideoArtwork[contentId])
                        },
                        artworkMap = freeboxVideoArtwork,
                        backdropMap = freeboxVideoBackdrops,
                        continueWatchingIds = continueWatchingContentIds,
                        cardWidth = videoCardWidth,
                        imageHeight = videoCardHeight,
                        horizontalPadding = 52.dp,
                        itemSpacing = 12.dp,
                        onShowDetails = { video ->
                            onNavigateToDetail(freeboxContentIdForEntry(video), "freebox", "")
                        },
                        onDeleteFromFreebox = { video -> onDeleteFreeboxVideo(video) },
                        onRenameFreebox = onRenameFreeboxVideo,
                    )
                    return@itemsIndexed
                }
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
                    isActiveRow = isActiveRowLambda,
                    rowFocusRequester = rowFocusRequesters.getOrPut(row.key) { FocusRequester() },
                    rowTitleBottom = 14.dp, // rowTitleBottom
                    defaultBringIntoViewSpec = defaultBringIntoViewSpec,
                    focusStateCatalogRowScrollIndex = focusState.catalogRowScrollStates[row.key] ?: 0,
                    focusedItemByRow = focusedItemByRow,
                    rowListStates = rowListStates,
                    loadMoreRequestedTotals = loadMoreRequestedTotals,
                    pendingRowFocusKey = pendingRowFocusKey,
                    pendingRowFocusIndex = pendingRowFocusIndex,
                    pendingRowFocusNonce = pendingRowFocusNonce,
                    onPendingRowFocusCleared = onPendingRowFocusCleared,
                    onRowItemFocused = stableOnRowItemFocused,
                    useLandscapePosters = when {
                        row.key.endsWith(movieFavoritesRowSuffix) -> !movieFavoritesPortraitMode
                        row.key.endsWith(seriesFavoritesRowSuffix) -> !seriesFavoritesPortraitMode
                        else -> useLandscapePosters
                    },
                    showLabels = showLabels,
                    posterCardCornerRadius = posterCardCornerRadius,
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
                    isVerticalRowsScrollingState = isVerticalRowsScrollingState,
                    itemFocusRequesters = stableItemFocusRequestersByRow.getOrPut(row.key) {
                        StableRef(mutableMapOf())
                    }
                )
            }

        }
    }
}
