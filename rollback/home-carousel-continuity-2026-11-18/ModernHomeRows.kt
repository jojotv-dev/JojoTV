@file:OptIn(ExperimentalFoundationApi::class, kotlinx.coroutines.FlowPreview::class)

package com.nuvio.tv.ui.screens.home

import com.nuvio.tv.ui.theme.NuvioTheme

import androidx.hilt.navigation.compose.hiltViewModel
import com.nuvio.tv.core.apps.InstalledApp
import com.nuvio.tv.core.apps.HOME_FREEBOX_VIDEOS_ROW_KEY
import com.nuvio.tv.ui.screens.apps.AppContextMenuModal
import com.nuvio.tv.ui.screens.apps.AppContextMenuViewModel
import android.view.KeyEvent as AndroidKeyEvent
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListPrefetchStrategy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.foundation.focusGroup
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.constrain
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.tv.material3.Border
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Glow
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.request.transformations
import com.nuvio.tv.R
import com.nuvio.tv.domain.model.FocusedPosterTrailerPlaybackTarget
import com.nuvio.tv.domain.model.CardDepthSurface
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.PosterShape
import com.nuvio.tv.data.freebox.FREEBOX_FOLDER_ID_PREFIX
import com.nuvio.tv.domain.model.ContinueWatchingCardStyle
import com.nuvio.tv.domain.model.PLACEHOLDER_IMAGE_URL
import com.nuvio.tv.domain.model.isPlaceholder
import com.nuvio.tv.ui.components.ContinueWatchingCard
import com.nuvio.tv.ui.components.FreeboxFolderBadge
import com.nuvio.tv.ui.components.continueWatchingArtworkWidth
import com.nuvio.tv.ui.components.continueWatchingImageCacheKey
import com.nuvio.tv.ui.components.continueWatchingImageModel
import com.nuvio.tv.ui.components.continueWatchingShouldBlur
import com.nuvio.tv.ui.components.continueWatchingUsesEpisodeThumbnails
import com.nuvio.tv.ui.components.LocalCardDepthStyle
import com.nuvio.tv.ui.components.MonochromePosterPlaceholder
import com.nuvio.tv.ui.components.TrailerPlayer
import com.nuvio.tv.ui.components.WatchedMarker
import com.nuvio.tv.ui.components.placeholderCardShimmer
import com.nuvio.tv.ui.components.nuvioCardDepth
import com.nuvio.tv.ui.components.LightweightCard
import com.nuvio.tv.ui.components.rememberArtworkBackedFocusGlow
import com.nuvio.tv.ui.components.rememberPlaceholderShimmerOffsetState
import com.nuvio.tv.LocalSidebarExpanded
import kotlin.math.abs
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import com.nuvio.tv.ui.util.recompositionHighlighter
import com.nuvio.tv.ui.util.StableMap
import com.nuvio.tv.ui.util.StableRef
import com.nuvio.tv.ui.util.asStable
import com.nuvio.tv.ui.util.contentTextDirection
import com.nuvio.tv.ui.util.rememberLongPressKeyTracker
import com.nuvio.tv.ui.components.InProgressBadge
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.debounce

private const val MODERN_HORIZONTAL_FOCUS_DEBOUNCE_MS = 140L
private const val POSTER_PREFETCH_DISTANCE = 1
private const val NESTED_PREFETCH_COUNT = 2

internal val LocalVerticalRowsScrolling = compositionLocalOf<State<Boolean>> { mutableStateOf(false) }

/**
 * True while the user is actively "fast-scrolling" — i.e. holding DPAD_LEFT/RIGHT or
 * DPAD_UP/DOWN and the LazyColumn-level key handler has taken over to drag the list
 * programmatically instead of letting [androidx.compose.ui.focus.FocusManager.moveFocus]
 * pull focus card-by-card. Cards use this to suppress their focus chrome (border / glow /
 * GIF) during the drag; the chrome snaps back onto whichever card focus lands on when
 * the user releases the key. Defaults to `false`, so any card used outside a modern
 * home row keeps its normal focus visuals.
 */
internal val LocalFastScrollActive = compositionLocalOf<State<Boolean>> { mutableStateOf(false) }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ModernContinueWatchingRowItem(
    payload: ModernPayload.ContinueWatching,
    requester: FocusRequester,
    isTargetItem: Boolean = false,
    cardWidth: Dp,
    imageHeight: Dp,
    blurUnwatchedEpisodes: Boolean,
    useEpisodeThumbnails: Boolean,
    continueWatchingCardStyle: ContinueWatchingCardStyle,
    continueWatchingCornerRadius: Dp,
    onFocused: () -> Unit,
    onContinueWatchingClick: (ContinueWatchingItem) -> Unit,
    onShowOptions: (ContinueWatchingItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val item = payload.item
    val onClick = remember(item) { { onContinueWatchingClick(item) } }
    val onLongPress = remember(item) { { onShowOptions(item) } }
    var focusEventId by remember { mutableIntStateOf(0) }
    var isCardFocused by remember { mutableStateOf(false) }
    val latestOnFocused by rememberUpdatedState(onFocused)

    LaunchedEffect(focusEventId, isCardFocused) {
        if (focusEventId == 0 || !isCardFocused) return@LaunchedEffect
        
        // Update global focus state immediately so "self-claiming" logic in other items
        // knows this item is now the one in charge.
        latestOnFocused()

        val targetEventId = focusEventId
        delay(MODERN_HORIZONTAL_FOCUS_DEBOUNCE_MS)
        if (!isCardFocused || focusEventId != targetEventId) return@LaunchedEffect
    }

    LaunchedEffect(isTargetItem) {
        if (isTargetItem && !isCardFocused) {
            runCatching { requester.requestFocus() }
        }
    }

    ContinueWatchingCard(
        item = item,
        onClick = onClick,
        onLongPress = onLongPress,
        cardWidth = cardWidth,
        imageHeight = imageHeight,
        blurUnwatchedEpisodes = blurUnwatchedEpisodes,
        useEpisodeThumbnails = useEpisodeThumbnails,
        cardStyle = continueWatchingCardStyle,
        cornerRadius = continueWatchingCornerRadius,
        isFocused = isCardFocused,
        modifier = modifier
            .focusRequester(requester)
            .onFocusChanged {
                isCardFocused = it.isFocused
                if (it.isFocused) {
                    focusEventId += 1
                }
            }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ModernCatalogRowItem(
    item: ModernCarouselItem,
    payload: ModernPayload,
    requester: FocusRequester,
    isTargetItem: Boolean = false,
    useLandscapePosters: Boolean,
    alwaysShowLandscapeClearlogo: Boolean = false,
    showLabels: Boolean,
    placeholderShimmerOffsetState: State<Float>?,
    posterCardCornerRadius: Dp,
    portraitCatalogCardWidth: Dp,
    portraitCatalogCardHeight: Dp,
    landscapeCatalogCardWidth: Dp,
    landscapeCatalogCardHeight: Dp,
    focusedPosterBackdropTrailerMuted: Boolean,
    effectiveExpandEnabled: Boolean,
    effectiveAutoplayEnabled: Boolean,
    trailerPlaybackTarget: FocusedPosterTrailerPlaybackTarget,
    isBackdropExpanded: () -> Boolean,
    expandedTrailerPreviewUrl: () -> String?,
    expandedTrailerPreviewAudioUrl: () -> String?,
    isCatalogItemWatched: (MetaPreview) -> Boolean,
    isFocusTarget: Boolean = false,
    onFocused: () -> Unit,
    onItemFocus: (MetaPreview) -> Unit,
    onPreloadAdjacentItem: () -> Unit,
    onCatalogSelectionFocused: (FocusedCatalogSelection) -> Unit,
    onNavigateToDetail: (String, String, String) -> Unit,
    onNavigateToFolderDetail: (String, String) -> Unit,
    onLongPress: () -> Unit,
    onBackdropInteraction: () -> Unit,
    onExpandedCatalogFocusKeyChange: (String?) -> Unit,
    enrichedPreviews: State<StableMap<String, MetaPreview>>,
    modifier: Modifier = Modifier
) {
    val focusKey = when (payload) {
        is ModernPayload.Catalog -> payload.focusKey
        is ModernPayload.CollectionFolder -> payload.focusKey
        is ModernPayload.ContinueWatching, is ModernPayload.InstalledApp -> error("Unsupported payload for ModernCatalogRowItem")
    }

    val metaPreview = item.metaPreview
    val isWatched = metaPreview?.let { isCatalogItemWatched(it) } ?: false
    val enrichedMeta by remember {
        derivedStateOf { (payload as? ModernPayload.Catalog)?.itemId?.let { enrichedPreviews.value.map[it] } }
    }
    val enrichedLogoUrl = enrichedMeta?.logo
    val enrichedBackdropUrl = enrichedMeta?.background?.takeIf(String::isNotBlank)
        ?: enrichedMeta?.landscapePoster?.takeIf(String::isNotBlank)

    var focusEventId by remember { mutableIntStateOf(0) }
    var isCardFocused by remember { mutableStateOf(false) }
    val latestOnFocused by rememberUpdatedState(onFocused)
    val latestOnItemFocus by rememberUpdatedState(onItemFocus)
    val latestOnPreloadAdjacentItem by rememberUpdatedState(onPreloadAdjacentItem)
    val latestOnCatalogSelectionFocused by rememberUpdatedState(onCatalogSelectionFocused)

    // Bump focusEventId to re-trigger selection reporting.
    //LaunchedEffect(focusKey, isTargetItem) {
    //    if (isTargetItem && isCardFocused) {
    //        focusEventId++
    //    }
    //}

    LaunchedEffect(focusEventId, isCardFocused, focusKey) {
        if (focusEventId == 0 || !isCardFocused) {
            return@LaunchedEffect
        }

        // Update global focus state immediately so "self-claiming" logic in other items
        // knows this item is now the one in charge.
        latestOnFocused()

        val targetEventId = focusEventId
        delay(MODERN_HORIZONTAL_FOCUS_DEBOUNCE_MS)
        if (!isCardFocused || focusEventId != targetEventId) {
            return@LaunchedEffect
        }

        // Heavy "settled" work (trailers, enrichment) remains debounced.
        item.metaPreview?.let { latestOnItemFocus(it) }
        latestOnPreloadAdjacentItem()
        when (payload) {
            is ModernPayload.Catalog -> {
                if (!payload.itemId.startsWith("__placeholder_")) {
                    latestOnCatalogSelectionFocused(
                        FocusedCatalogSelection(
                            focusKey = focusKey,
                            payload = payload
                        )
                    )
                }
            }
            is ModernPayload.CollectionFolder -> {
                latestOnCatalogSelectionFocused(
                    FocusedCatalogSelection(
                        focusKey = focusKey,
                        payload = payload
                    )
                )
            }
            is ModernPayload.ContinueWatching, is ModernPayload.InstalledApp -> Unit
        }
    }

    LaunchedEffect(isTargetItem) {
        if (isTargetItem && !isCardFocused) {
            runCatching { requester.requestFocus() }
        }
    }

    val suppressCardExpansionForHeroTrailer =
        effectiveAutoplayEnabled &&
                trailerPlaybackTarget == FocusedPosterTrailerPlaybackTarget.HERO_MEDIA
    // Expansion is armed from a parent-level focusKey timer that can outlive real
    // card focus (e.g. user moves left into the sidebar). Never show the expanded
    // backdrop on a card that is not actually focused (#2815).
    val effectiveBackdropExpanded by remember(isBackdropExpanded, suppressCardExpansionForHeroTrailer) {
        derivedStateOf {
            isCardFocused && isBackdropExpanded() && !suppressCardExpansionForHeroTrailer
        }
    }

    val isSidebarExpanded = LocalSidebarExpanded.current
    val playTrailerInExpandedCard =
        effectiveAutoplayEnabled &&
            !isSidebarExpanded &&
            isCardFocused &&
            trailerPlaybackTarget == FocusedPosterTrailerPlaybackTarget.EXPANDED_CARD &&
            effectiveBackdropExpanded
    val trailerUrl = expandedTrailerPreviewUrl()
    val trailerPreviewUrl = if (playTrailerInExpandedCard) trailerUrl else null
    val trailerPreviewAudioUrl = if (playTrailerInExpandedCard) {
        expandedTrailerPreviewAudioUrl()
    } else {
        null
    }
    val cardMetrics = remember(
        item,
        useLandscapePosters,
        portraitCatalogCardWidth,
        portraitCatalogCardHeight,
        landscapeCatalogCardWidth,
        landscapeCatalogCardHeight
    ) {
        item.catalogCardMetrics(
            useLandscapePosters = useLandscapePosters,
            portraitCardWidth = portraitCatalogCardWidth,
            portraitCardHeight = portraitCatalogCardHeight,
            landscapeCardWidth = landscapeCatalogCardWidth,
            landscapeCardHeight = landscapeCatalogCardHeight
        )
    }

    val cardItem = if (metaPreview?.id?.startsWith("freebox:", ignoreCase = true) == true) {
        enrichedMeta?.poster?.takeIf(String::isNotBlank)?.let { imageUrl -> item.copy(imageUrl = imageUrl) } ?: item
    } else item
    ModernCarouselCard(
        item = cardItem,
        useLandscapeOverlayTreatment = useLandscapePosters,
        alwaysShowLandscapeClearlogo = alwaysShowLandscapeClearlogo,
        showLabels = showLabels,
        placeholderShimmerOffsetState = placeholderShimmerOffsetState,
        cardCornerRadius = posterCardCornerRadius,
        cardWidth = cardMetrics.width,
        cardHeight = cardMetrics.height,
        modifier = modifier,
        focusedPosterBackdropExpandEnabled = effectiveExpandEnabled,
        isBackdropExpanded = effectiveBackdropExpanded,
        playTrailerInExpandedCard = playTrailerInExpandedCard,
        focusedPosterBackdropTrailerMuted = focusedPosterBackdropTrailerMuted,
        trailerPreviewUrl = trailerPreviewUrl,
        trailerPreviewAudioUrl = trailerPreviewAudioUrl,
        isWatched = isWatched,
        enrichedLogoUrl = enrichedLogoUrl,
        enrichedBackdropUrl = enrichedBackdropUrl,
        focusRequester = requester,
        onFocused = {
            focusEventId += 1
        },
        onFocusStateChanged = { focused ->
            isCardFocused = focused
        },
        onClick = {
            latestOnFocused()
            item.metaPreview?.let { latestOnItemFocus(it) }
            when (payload) {
                is ModernPayload.Catalog -> {
                    if (!payload.itemId.startsWith("__placeholder_")) {
                        onNavigateToDetail(
                            payload.itemId,
                            payload.itemType,
                            payload.addonBaseUrl
                        )
                    }
                }
                is ModernPayload.CollectionFolder -> {
                    onNavigateToFolderDetail(
                        payload.collectionId,
                        payload.folderId
                    )
                }
                is ModernPayload.ContinueWatching, is ModernPayload.InstalledApp -> Unit
            }
        },
        onLongPress = onLongPress,
        onBackdropInteraction = onBackdropInteraction,
        onTrailerEnded = { onExpandedCatalogFocusKeyChange(null) }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun ModernRowSection(
    row: HeroCarouselRow,
    modernContinueWatchingHeroEnabled: Boolean = false,
    modernContinueWatchingHeroHeight: Dp = 0.dp,
    isActiveRow: () -> Boolean,
    rowFocusRequester: FocusRequester,
    isVerticalRowsScrollingState: State<Boolean>,
    rowTitleBottom: Dp,
    defaultBringIntoViewSpec: BringIntoViewSpec,
    focusStateCatalogRowScrollIndex: Int,
    focusStateCatalogRowScrollAnchor: String?,
    focusedItemByRow: StableRef<MutableMap<String, Int>>,
    rowListStates: StableRef<MutableMap<String, LazyListState>>,
    loadMoreRequestedTotals: StableRef<MutableMap<String, Int>>,
    pendingRowFocusKey: State<String?>,
    pendingRowFocusIndex: State<Int?>,
    pendingRowFocusNonce: State<Int>,
    onPendingRowFocusCleared: () -> Unit,
    onRowItemFocused: (String, Int, Boolean) -> Unit,
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
    onContinueWatchingClick: (ContinueWatchingItem) -> Unit,
    onContinueWatchingOptions: (ContinueWatchingItem) -> Unit,
    isCatalogItemWatched: (MetaPreview) -> Boolean,
    onCatalogItemLongPress: (MetaPreview, String, Boolean) -> Unit,
    onItemFocus: (MetaPreview) -> Unit,
    onPreloadAdjacentItem: (MetaPreview) -> Unit,
    enrichedPreviews: State<StableMap<String, MetaPreview>> = androidx.compose.runtime.mutableStateOf(StableMap()),
    onCatalogSelectionFocused: (FocusedCatalogSelection) -> Unit,
    onNavigateToDetail: (String, String, String) -> Unit,
    onNavigateToFolderDetail: (String, String) -> Unit,
    onLoadMoreCatalog: (String, String, String) -> Unit,
    onBackdropInteraction: () -> Unit,
    onExpandedCatalogFocusKeyChange: (String?) -> Unit,
    sharedPlaceholderShimmerOffsetState: State<Float>?,
    itemFocusRequesters: StableRef<MutableMap<Int, FocusRequester>> = StableRef(mutableMapOf())
) {
    // Unwrap StableRef wrappers
    @Suppress("NAME_SHADOWING") val focusedItemByRow = focusedItemByRow.value
    @Suppress("NAME_SHADOWING") val rowListStates = rowListStates.value
    @Suppress("NAME_SHADOWING") val loadMoreRequestedTotals = loadMoreRequestedTotals.value
    @Suppress("NAME_SHADOWING") val itemFocusRequesters = itemFocusRequesters.value
    val rowKey = row.key
    val isFreeboxVideosRow = row.key == HOME_FREEBOX_VIDEOS_ROW_KEY ||
        row.addonId?.startsWith("freebox:", ignoreCase = true) == true
    val isContinuousCarouselRow = row.isContinuousCatalogCarousel()
    val continuousItemIndices = remember(row.items.list) { row.continuousItemIndices() }
    val isInfiniteCarouselRow = isContinuousCarouselRow && continuousItemIndices.size > 1
    val isSeriesRow = isSeriesType(row.apiType)
    val isContinueWatchingRow = row.key == MODERN_CONTINUE_WATCHING_ROW_KEY || row.key == MODERN_UPCOMING_ROW_KEY
    val showsInlineMetadata = isFreeboxVideosRow ||
        row.apiType.equals("movie", ignoreCase = true) ||
        row.apiType.equals("series", ignoreCase = true)
    val rowMaskColor = NuvioTheme.colors.Background

    // Per-row derived state: only invalidates when THIS row's focused index
    // changes, not when any other row's index changes in the shared map.
    val rowFocusedIndex = remember(rowKey) {
        derivedStateOf { focusedItemByRow[rowKey] ?: 0 }
    }
    val rowHasFocus = remember(rowKey) { mutableStateOf(false) }
    val rowFocusResetNonce = remember(rowKey) { mutableIntStateOf(0) }
    val pendingLoopFocusTarget = remember(rowKey) { mutableIntStateOf(-1) }
    val focusedItemIndex = if (isFreeboxVideosRow && pendingLoopFocusTarget.intValue >= 0) {
        row.logicalItemIndexAtVirtualIndex(pendingLoopFocusTarget.intValue, continuousItemIndices)
            ?: rowFocusedIndex.value
    } else {
        rowFocusedIndex.value
    }

    if (modernContinueWatchingHeroEnabled && rowKey == MODERN_CONTINUE_WATCHING_ROW_KEY) {
        val heroItems = remember(row.items) {
            row.items.list.filter { it.payload is ModernPayload.ContinueWatching }
        }
        val selectedIndex = rowFocusedIndex.value.coerceIn(0, (heroItems.size - 1).coerceAtLeast(0))
        if (heroItems.isNotEmpty()) {
            ModernContinueWatchingHero(
                items = heroItems,
                selectedIndex = selectedIndex,
                focusRequester = rowFocusRequester,
                height = modernContinueWatchingHeroHeight,
                onFocused = { index ->
                    onRowItemFocused(rowKey, index, true)
                },
                onSelectIndex = { index ->
                    onRowItemFocused(rowKey, index, true)
                },
                onClick = onContinueWatchingClick,
                onLongClick = onContinueWatchingOptions,
                enrichedPreviews = enrichedPreviews,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 52.dp, end = 40.dp, top = 8.dp, bottom = 2.dp)
            )
        }
        return
    }

    var contextMenuApp by remember { mutableStateOf<InstalledApp?>(null) }
    var contextMenuIndex by remember { mutableIntStateOf(-1) }

    // Blocks vertical focus exit during placeholder→data transition.
    val blockingFocusExit = remember { mutableStateOf(false) }

    // Item keys carry item identity, which the placeholder the ring sits on loses when real data
    // arrives: its key changes and Compose tears the focused node down. Lend that one card a
    // positional key for as long as the ring can be on it, so the node is reused instead.
    //
    // Armed from the item id, not a loading flag: a lazily loaded catalog composes its row
    // before loading starts and would never see the flag go up.
    val firstCardKey = remember(rowKey) { "${rowKey}__first" }
    val firstIsPlaceholder = (row.items.list.firstOrNull()?.payload as? ModernPayload.Catalog)
        ?.itemId?.startsWith("__placeholder_") == true
    val pinFirstCard = remember(rowKey) { mutableStateOf(firstIsPlaceholder) }
    val pinSpent = remember(rowKey) { mutableStateOf(false) }
    // Lent to the card, not the slot: slot 0 would hand the key to whatever lands there.
    val pinnedItemKey = remember(rowKey) { mutableStateOf<String?>(null) }
    if (firstIsPlaceholder && !pinSpent.value) pinFirstCard.value = true
    if (pinFirstCard.value && !firstIsPlaceholder && pinnedItemKey.value == null) {
        pinnedItemKey.value = row.items.list.firstOrNull()?.key
    }
    // Released once the ring has left the row: swapping the node earlier is visible for nothing.
    if (pinFirstCard.value && !firstIsPlaceholder && !isActiveRow()) {
        pinFirstCard.value = false
        pinnedItemKey.value = null
        pinSpent.value = true
    }
    Column(
        modifier = Modifier.then(
            if (blockingFocusExit.value) {
                Modifier.focusProperties {
                    up = FocusRequester.Cancel
                    down = FocusRequester.Cancel
                }
            } else Modifier
        )
    ) {
        val titleMediumStyle = MaterialTheme.typography.titleMedium
        val rowTitleStyle = remember(titleMediumStyle) {
            titleMediumStyle.copy(fontWeight = FontWeight.SemiBold)
        }
        val rowTitle = row.title
        val textColor = NuvioTheme.colors.TextPrimary
        val textModifier = remember(rowTitleBottom) {
            Modifier.padding(start = 52.dp, bottom = rowTitleBottom)
        }
        Text(
            text = rowTitle,
            style = rowTitleStyle.copy(textDirection = rowTitle.contentTextDirection()),
            color = textColor,
            modifier = textModifier
        )

        val rowListState = rowListStates.getOrPut(row.key) {
            // Looping rows restore from a stable logical item key, then start far from
            // either end of the virtual rail. Other rows retain their saved list index.
            val anchorIndex = focusStateCatalogRowScrollAnchor
                ?.let { anchor -> row.items.list.indexOfFirst { it.key == anchor } }
                ?.takeIf { it >= 0 }
            val restoredIndex = if (isInfiniteCarouselRow) {
                row.virtualIndexForLogicalItem(
                    anchorIndex ?: focusedItemIndex,
                    cycleIndices = continuousItemIndices
                )
            } else {
                anchorIndex ?: focusStateCatalogRowScrollIndex.coerceIn(
                    0,
                    (row.items.list.size - 1).coerceAtLeast(0)
                )
            }
            LazyListState(
                firstVisibleItemIndex = restoredIndex,
                prefetchStrategy = LazyListPrefetchStrategy(nestedPrefetchItemCount = NESTED_PREFETCH_COUNT)
            )
        }
        val firstVisibleCycleItemIndexState = remember(rowListState, continuousItemIndices) {
            derivedStateOf {
                val cycleSize = continuousItemIndices.size
                continuousItemIndices.getOrNull(
                    if (cycleSize > 0) Math.floorMod(rowListState.firstVisibleItemIndex, cycleSize) else -1
                )
            }
        }

        val focusedLoopVirtualIndex = remember(rowKey, rowListState) {
            mutableIntStateOf(
                if (isInfiniteCarouselRow) {
                    row.virtualIndexForLogicalItem(
                        focusedItemIndex,
                        rowListState.firstVisibleItemIndex,
                        continuousItemIndices
                    )
                } else {
                    focusedItemIndex
                }
            )
        }
        val pinnedFirstVirtualIndex = remember(rowKey) { mutableIntStateOf(-1) }
        if (isInfiniteCarouselRow && pinFirstCard.value && pinnedItemKey.value != null &&
            pinnedFirstVirtualIndex.intValue < 0
        ) {
            pinnedFirstVirtualIndex.intValue = focusedLoopVirtualIndex.intValue
        }

        LaunchedEffect(isInfiniteCarouselRow, continuousItemIndices, rowFocusedIndex.value) {
            if (!isInfiniteCarouselRow) return@LaunchedEffect
            val selectedLogicalIndex = rowFocusedIndex.value
            val currentVirtualIndex = focusedLoopVirtualIndex.intValue
            val currentLogicalIndex = row.logicalItemIndexAtVirtualIndex(currentVirtualIndex, continuousItemIndices)
            val nearVirtualBoundary = currentVirtualIndex < 1_024 || currentVirtualIndex > Int.MAX_VALUE - 1_024
            if (currentLogicalIndex == selectedLogicalIndex && !nearVirtualBoundary) return@LaunchedEffect
            val currentViewportIndex = rowListState.firstVisibleItemIndex
                .takeIf { it >= 1_024 && it <= Int.MAX_VALUE - 1_024 }
            val targetVirtualIndex = row.virtualIndexForLogicalItem(
                selectedLogicalIndex,
                nearVirtualIndex = currentViewportIndex,
                cycleIndices = continuousItemIndices
            )
            if (pinFirstCard.value &&
                row.items.list.getOrNull(selectedLogicalIndex)?.key == pinnedItemKey.value
            ) {
                pinnedFirstVirtualIndex.intValue = targetVirtualIndex
            }
            focusedLoopVirtualIndex.intValue = targetVirtualIndex
            rowListState.scrollToItem(targetVirtualIndex)
            if (isActiveRow() && rowHasFocus.value) {
                val requester = itemFocusRequesters.getOrPut(targetVirtualIndex) { FocusRequester() }
                var focusRequested = false
                repeat(12) {
                    if (!focusRequested) {
                        androidx.compose.runtime.withFrameNanos { }
                        focusRequested = runCatching { requester.requestFocus() }.getOrDefault(false)
                    }
                }
            }
        }

        LaunchedEffect(rowFocusResetNonce.intValue, isInfiniteCarouselRow, continuousItemIndices) {
            if (rowFocusResetNonce.intValue == 0) return@LaunchedEffect
            val firstIndex = if (isContinuousCarouselRow) continuousItemIndices.firstOrNull() ?: 0 else 0
            if (isInfiniteCarouselRow) {
                val targetVirtualIndex = row.virtualIndexForLogicalItem(
                    logicalIndex = firstIndex,
                    nearVirtualIndex = rowListState.firstVisibleItemIndex,
                    cycleIndices = continuousItemIndices
                )
                pendingLoopFocusTarget.intValue = -1
                focusedLoopVirtualIndex.intValue = targetVirtualIndex
                rowListState.scrollToItem(targetVirtualIndex)
            } else {
                rowListState.scrollToItem(0)
            }
        }

        // Freebox videos and the physical ends of other catalog rails need an
        // explicit focus target; ordinary film and series movement stays native.
        val pendingCyclicFocusTarget = remember(rowKey) { mutableIntStateOf(-1) }
        val cyclicItemIndices = remember(row.items.list, isInfiniteCarouselRow, continuousItemIndices) {
            if (isInfiniteCarouselRow) return@remember continuousItemIndices
            row.items.list.indices.filterNot { index ->
                index > 0 && (row.items.list[index].payload as? ModernPayload.Catalog)
                    ?.itemId?.startsWith("__placeholder_") == true
            }
        }
        LaunchedEffect(pendingLoopFocusTarget.intValue, continuousItemIndices) {
            val targetVirtualIndex = pendingLoopFocusTarget.intValue
            if (!isInfiniteCarouselRow || targetVirtualIndex < 0) return@LaunchedEffect
            if (row.logicalItemIndexAtVirtualIndex(targetVirtualIndex, continuousItemIndices) == null) {
                pendingLoopFocusTarget.intValue = -1
                return@LaunchedEffect
            }
            rowListState.animateScrollToItem(targetVirtualIndex)
            val requester = itemFocusRequesters.getOrPut(targetVirtualIndex) { FocusRequester() }
            var focusRequested = false
            repeat(12) {
                if (!focusRequested) {
                    androidx.compose.runtime.withFrameNanos { }
                    focusRequested = runCatching { requester.requestFocus() }.getOrDefault(false)
                }
            }
            if (!focusRequested) {
                pendingLoopFocusTarget.intValue = -1
                row.logicalItemIndexAtVirtualIndex(focusedLoopVirtualIndex.intValue, continuousItemIndices)?.let { actualIndex ->
                    onRowItemFocused(rowKey, actualIndex, isContinueWatchingRow)
                }
            }
        }
        LaunchedEffect(isActiveRow()) {
            if (!isActiveRow()) pendingLoopFocusTarget.intValue = -1
        }

        // Non-catalog rails still wrap focus at their finite endpoints.
        LaunchedEffect(pendingCyclicFocusTarget.intValue, cyclicItemIndices) {
            val targetIndex = pendingCyclicFocusTarget.intValue
            if (targetIndex !in cyclicItemIndices) {
                pendingCyclicFocusTarget.intValue = -1
                return@LaunchedEffect
            }
            rowListState.animateScrollToItem(targetIndex)
            val requester = itemFocusRequesters.getOrPut(targetIndex) { FocusRequester() }
            var focusRequested = false
            repeat(12) {
                if (!focusRequested) {
                    androidx.compose.runtime.withFrameNanos { }
                    focusRequested = runCatching { requester.requestFocus() }.getOrDefault(false)
                }
            }
            pendingCyclicFocusTarget.intValue = -1
        }

        // Keep the selected card directly above its metadata panel. The panel is
        // anchored to the row's leading edge, so bring each newly selected card there.
        LaunchedEffect(
            row.key,
            showsInlineMetadata,
            focusedItemIndex,
            isActiveRow(),
            pendingLoopFocusTarget.intValue,
            focusedLoopVirtualIndex.intValue
        ) {
            if (!showsInlineMetadata || !isActiveRow() || row.items.list.isEmpty()) return@LaunchedEffect
            if (isFreeboxVideosRow && pendingLoopFocusTarget.intValue >= 0) return@LaunchedEffect
            val selectedIndex = if (isInfiniteCarouselRow) {
                focusedLoopVirtualIndex.intValue
            } else {
                focusedItemIndex.coerceIn(0, row.items.list.lastIndex)
            }
            if (rowListState.firstVisibleItemIndex != selectedIndex ||
                rowListState.firstVisibleItemScrollOffset != 0
            ) {
                rowListState.animateScrollToItem(selectedIndex)
            }
        }

        val firstItemKey = row.items.list.firstOrNull()?.key

        // When placeholder items are replaced by real data and this row
        // is the active row, re-request focus on the first real item.
        val firstItemImageUrl = row.items.list.firstOrNull()?.imageUrl
        val wasPlaceholderRef = remember { mutableStateOf(row.isLoading && firstItemImageUrl.isPlaceholder()) }
        val needsFocusRestore = remember { mutableStateOf(false) }
        
        LaunchedEffect(row.isLoading, firstItemImageUrl, isActiveRow) {
            val wasPlaceholder = wasPlaceholderRef.value
            val isNowReal = !row.isLoading || !firstItemImageUrl.isPlaceholder()
            if (wasPlaceholder && isNowReal && isActiveRow()) {
                needsFocusRestore.value = true
                blockingFocusExit.value = true
            }
            wasPlaceholderRef.value = row.isLoading && firstItemImageUrl.isPlaceholder()
        }

        // Restore focus after placeholder→data transition.
        LaunchedEffect(needsFocusRestore.value, row.key) {
            if (!needsFocusRestore.value) return@LaunchedEffect
            needsFocusRestore.value = false
            blockingFocusExit.value = false
        }

        val isRowScrollingState = remember(rowListState) {
            derivedStateOf { rowListState.isScrollInProgress }
        }
        val currentRowState = rememberUpdatedState(row)
        val loadMoreCatalogId = row.catalogId
        val loadMoreAddonId = row.addonId
        val loadMoreApiType = row.apiType
        val canObserveLoadMore = row.supportsSkip &&
            row.hasMore &&
            !loadMoreCatalogId.isNullOrBlank() &&
            !loadMoreAddonId.isNullOrBlank() &&
            !loadMoreApiType.isNullOrBlank()

        LaunchedEffect(row.key, pendingRowFocusNonce.value, isInfiniteCarouselRow, continuousItemIndices) {
            if (pendingRowFocusKey.value != row.key) return@LaunchedEffect
            val targetIndex = (pendingRowFocusIndex.value ?: 0)
                .coerceIn(0, (row.items.list.size - 1).coerceAtLeast(0))
            val targetVirtualIndex = if (isInfiniteCarouselRow) {
                row.virtualIndexForLogicalItem(
                    targetIndex,
                    rowListState.firstVisibleItemIndex,
                    continuousItemIndices
                )
            } else {
                targetIndex
            }
            if (!rowListState.isScrollInProgress) {
                runCatching { rowListState.scrollToItem(targetVirtualIndex) }
                if (isInfiniteCarouselRow) focusedLoopVirtualIndex.intValue = targetVirtualIndex
            }
        }

        if (canObserveLoadMore) {
            LaunchedEffect(
                row.key,
                rowListState,
                canObserveLoadMore,
                row.items.list,
                continuousItemIndices
            ) {
                snapshotFlow {
                    val layoutInfo = rowListState.layoutInfo
                    val total = row.items.list.size
                    val isNearEnd = if (isInfiniteCarouselRow) {
                        val lastNavigableItem = continuousItemIndices.lastOrNull() ?: -1
                        layoutInfo.visibleItemsInfo.any { itemInfo ->
                            (row.logicalItemIndexAtVirtualIndex(itemInfo.index, continuousItemIndices) ?: -1) >= lastNavigableItem - 4
                        }
                    } else {
                        val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                        lastVisible >= total - 4
                    }
                    isNearEnd to total
                }
                    .distinctUntilChanged()
                    .collect { (isNearEnd, total) ->
                        if (total <= 0) return@collect
                        val rowState = currentRowState.value
                        if (!isNearEnd) {
                            if (!isInfiniteCarouselRow) loadMoreRequestedTotals.remove(rowState.key)
                            return@collect
                        }
                        val lastRequestedTotal = loadMoreRequestedTotals[rowState.key]
                        if (rowState.hasMore &&
                            !rowState.isLoading &&
                            lastRequestedTotal != total
                        ) {
                            loadMoreRequestedTotals[rowState.key] = total
                            onLoadMoreCatalog(
                                loadMoreCatalogId,
                                loadMoreAddonId,
                                loadMoreApiType
                            )
                        }
                    }
            }
        }

        val density = LocalDensity.current
        val rowStartPadding = 52.dp
        // Focus scaling and the ring extend a few dp before a card's layout bounds. Keep the
        // leading-edge mask inside the empty inset so it cannot paint over the first card.
        val rowLeadingMaskInset = (rowStartPadding - 8.dp).coerceAtLeast(0.dp)
        val context = LocalContext.current
        val imageLoader = context.imageLoader

        val rowItemCount = row.items.list.size
        LaunchedEffect(
            row.key,
            isActiveRow,
            isVerticalRowsScrollingState,
            rowItemCount,
            portraitCatalogCardWidth,
            portraitCatalogCardHeight,
            landscapeCatalogCardWidth,
            landscapeCatalogCardHeight,
            continueWatchingCardWidth,
            continueWatchingCardHeight,
            useEpisodeThumbnails,
            blurUnwatchedEpisodes
        ) {
            if (!isActiveRow() || isVerticalRowsScrollingState.value) return@LaunchedEffect
            delay(150) // Wait before spamming image requests to survive rapid vertical D-pad scrolls!
            val cwWidthPx = with(density) {
                continueWatchingArtworkWidth(
                    continueWatchingCardStyle, continueWatchingCardWidth, continueWatchingCardHeight
                ).roundToPx()
            }
            val cwHeightPx = with(density) { continueWatchingCardHeight.roundToPx() }
            fun imageUrlAndKey(item: ModernCarouselItem): Pair<String, String>? {
                return when (val payload = item.payload) {
                    is ModernPayload.InstalledApp -> null
                    is ModernPayload.Catalog -> {
                        val url = item.imageUrl ?: return null
                        val metrics = item.catalogCardMetrics(
                            useLandscapePosters = useLandscapePosters,
                            portraitCardWidth = portraitCatalogCardWidth,
                            portraitCardHeight = portraitCatalogCardHeight,
                            landscapeCardWidth = landscapeCatalogCardWidth,
                            landscapeCardHeight = landscapeCatalogCardHeight
                        )
                        val widthPx = with(density) { metrics.width.roundToPx() }
                        val heightPx = with(density) { metrics.height.roundToPx() }
                        url to "${url}_${widthPx}x${heightPx}"
                    }
                    is ModernPayload.CollectionFolder -> {
                        val url = item.imageUrl ?: return null
                        val metrics = item.catalogCardMetrics(
                            useLandscapePosters = useLandscapePosters,
                            portraitCardWidth = portraitCatalogCardWidth,
                            portraitCardHeight = portraitCatalogCardHeight,
                            landscapeCardWidth = landscapeCatalogCardWidth,
                            landscapeCardHeight = landscapeCatalogCardHeight
                        )
                        val widthPx = with(density) { metrics.width.roundToPx() }
                        val heightPx = with(density) { metrics.height.roundToPx() }
                        url to "${url}_${widthPx}x${heightPx}"
                    }
                    is ModernPayload.ContinueWatching -> {
                        // Use the same model and cache key the card computes so the prefetch warms the entry the card actually reads.
                        val model = continueWatchingImageModel(
                            payload.item, continueWatchingUsesEpisodeThumbnails(continueWatchingCardStyle, useEpisodeThumbnails),
                            continueWatchingCardStyle != ContinueWatchingCardStyle.CARD
                        ) ?: return null
                        val blur = continueWatchingShouldBlur(
                            payload.item, blurUnwatchedEpisodes, continueWatchingUsesEpisodeThumbnails(continueWatchingCardStyle, useEpisodeThumbnails),
                            continueWatchingCardStyle != ContinueWatchingCardStyle.CARD
                        )
                        model to continueWatchingImageCacheKey(model, cwWidthPx, cwHeightPx, blur)
                    }
                }
            }
            fun enqueueIfNeeded(item: ModernCarouselItem, widthPx: Int, heightPx: Int) {
                if (widthPx <= 0 || heightPx <= 0) return
                val (url, cacheKey) = imageUrlAndKey(item) ?: return
                if (imageLoader.memoryCache?.get(MemoryCache.Key(cacheKey)) != null) return
                val payload = item.payload
                val blur = payload is ModernPayload.ContinueWatching &&
                    continueWatchingShouldBlur(
                        payload.item, blurUnwatchedEpisodes, continueWatchingUsesEpisodeThumbnails(continueWatchingCardStyle, useEpisodeThumbnails),
                            continueWatchingCardStyle != ContinueWatchingCardStyle.CARD
                    )
                imageLoader.enqueue(
                    ImageRequest.Builder(context)
                        .data(url)
                        .memoryCacheKey(cacheKey)
                        .size(width = widthPx, height = heightPx)
                        .apply {
                            if (blur) transformations(com.nuvio.tv.ui.util.BlurTransformation())
                        }
                        .build()
                )
            }
            // Prefetch initial visible + ahead items immediately when row appears
            val prefetchRow = currentRowState.value
            val items = prefetchRow.items.list
            val prefetchCycleIndices = if (prefetchRow.isContinuousCatalogCarousel()) {
                prefetchRow.continuousItemIndices()
            } else emptyList()
            val initialVirtualIndex = if (prefetchCycleIndices.size > 1) rowListState.firstVisibleItemIndex else 0
            withContext(Dispatchers.IO) {
                for (offset in 0 until POSTER_PREFETCH_DISTANCE) {
                    val logicalIndex = prefetchRow.logicalItemIndexAtVirtualIndex(
                        initialVirtualIndex + offset,
                        prefetchCycleIndices
                    )
                        ?: continue
                    val item = items.getOrNull(logicalIndex) ?: continue
                    val (wPx, hPx) = when (item.payload) {
                        is ModernPayload.Catalog -> {
                            val metrics = item.catalogCardMetrics(
                                useLandscapePosters = useLandscapePosters,
                                portraitCardWidth = portraitCatalogCardWidth,
                                portraitCardHeight = portraitCatalogCardHeight,
                                landscapeCardWidth = landscapeCatalogCardWidth,
                                landscapeCardHeight = landscapeCatalogCardHeight
                            )
                            with(density) { metrics.width.roundToPx() } to with(density) { metrics.height.roundToPx() }
                        }
                        is ModernPayload.CollectionFolder -> {
                            val metrics = item.catalogCardMetrics(
                                useLandscapePosters = useLandscapePosters,
                                portraitCardWidth = portraitCatalogCardWidth,
                                portraitCardHeight = portraitCatalogCardHeight,
                                landscapeCardWidth = landscapeCatalogCardWidth,
                                landscapeCardHeight = landscapeCatalogCardHeight
                            )
                            with(density) { metrics.width.roundToPx() } to with(density) { metrics.height.roundToPx() }
                        }
                        is ModernPayload.InstalledApp -> 0 to 0
                            is ModernPayload.ContinueWatching -> cwWidthPx to cwHeightPx
                    }
                    enqueueIfNeeded(item, wPx, hPx)
                }
            }

            snapshotFlow {
                rowListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            }
                .distinctUntilChanged()
                .collect { lastVisibleIndex ->
                    val prefetchRow = currentRowState.value
                    val currentItems = prefetchRow.items.list
                    val prefetchCycleIndices = if (prefetchRow.isContinuousCatalogCarousel()) {
                        prefetchRow.continuousItemIndices()
                    } else emptyList()
                    withContext(Dispatchers.IO) {
                        for (virtualIndex in (lastVisibleIndex + 1)..(lastVisibleIndex + POSTER_PREFETCH_DISTANCE)) {
                            val logicalIndex = prefetchRow.logicalItemIndexAtVirtualIndex(
                                virtualIndex,
                                prefetchCycleIndices
                            )
                                ?: continue
                            val item = currentItems.getOrNull(logicalIndex) ?: continue
                            val (wPx, hPx) = when (item.payload) {
                                is ModernPayload.Catalog -> {
                                    val metrics = item.catalogCardMetrics(
                                        useLandscapePosters = useLandscapePosters,
                                        portraitCardWidth = portraitCatalogCardWidth,
                                        portraitCardHeight = portraitCatalogCardHeight,
                                        landscapeCardWidth = landscapeCatalogCardWidth,
                                        landscapeCardHeight = landscapeCatalogCardHeight
                                    )
                                    with(density) { metrics.width.roundToPx() } to with(density) { metrics.height.roundToPx() }
                                }
                                is ModernPayload.CollectionFolder -> {
                                    val metrics = item.catalogCardMetrics(
                                        useLandscapePosters = useLandscapePosters,
                                        portraitCardWidth = portraitCatalogCardWidth,
                                        portraitCardHeight = portraitCatalogCardHeight,
                                        landscapeCardWidth = landscapeCatalogCardWidth,
                                        landscapeCardHeight = landscapeCatalogCardHeight
                                    )
                                    with(density) { metrics.width.roundToPx() } to with(density) { metrics.height.roundToPx() }
                                }
                                is ModernPayload.InstalledApp -> 0 to 0
                            is ModernPayload.ContinueWatching -> cwWidthPx to cwHeightPx
                            }
                            enqueueIfNeeded(item, wPx, hPx)
                        }
                    }
                }
        }

        val layoutDirection = LocalLayoutDirection.current
        val isRtl = layoutDirection == LayoutDirection.Rtl
        val horizontalBringIntoViewSpec = remember(density, defaultBringIntoViewSpec, rowStartPadding, isRtl) {
            val parentStartOffsetPx = with(density) { rowStartPadding.roundToPx() }
            @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
            object : BringIntoViewSpec {
                override val scrollAnimationSpec: AnimationSpec<Float> =
                    defaultBringIntoViewSpec.scrollAnimationSpec

                override fun calculateScrollDistance(
                    offset: Float,
                    size: Float,
                    containerSize: Float
                ): Float {
                    val childSize = abs(size)
                    if (isRtl) {
                        val childSmallerThanParent = childSize <= containerSize
                        val initialTarget = containerSize - parentStartOffsetPx.toFloat()
                        val targetForTrailingEdge =
                            if (childSmallerThanParent && initialTarget < childSize) {
                                childSize
                            } else {
                                initialTarget
                            }
                        return (offset + size) - targetForTrailingEdge
                    } else {
                        val childSmallerThanParent = childSize <= containerSize
                        val initialTarget = parentStartOffsetPx.toFloat()
                        val spaceAvailable = containerSize - initialTarget

                        val targetForLeadingEdge =
                            if (childSmallerThanParent && spaceAvailable < childSize) {
                                containerSize - childSize
                            } else {
                                initialTarget
                            }

                        return offset - targetForLeadingEdge
                    }
                }
            }
        }

        // When a poster in this row expands, ensure it scrolls fully into view.
        var isExpansionScrollActive by remember { mutableStateOf(false) }
        val expandedCardWidthPx = with(density) {
            if (useLandscapePosters) {
                landscapeCatalogCardWidth.roundToPx()
            } else {
                (portraitCatalogCardHeight * (16f / 9f)).roundToPx()
            }
        }
        LaunchedEffect(row.key, effectiveExpandEnabled, rowItemCount) {
            if (!effectiveExpandEnabled) return@LaunchedEffect
            snapshotFlow { expandedCatalogFocusKey.value }
                .collect { expandedKey ->
                    if (expandedKey == null) return@collect
                    // Find the index of the expanded item in this row
                    val expandedIndex = row.items.list.indexOfFirst { item ->
                        when (val p = item.payload) {
                            is ModernPayload.Catalog -> p.focusKey == expandedKey
                            is ModernPayload.CollectionFolder -> p.focusKey == expandedKey
                            else -> false
                        }
                    }
                    if (expandedIndex < 0) return@collect
                    val expandedVirtualIndex = if (isInfiniteCarouselRow) {
                        row.virtualIndexForLogicalItem(
                            expandedIndex,
                            focusedLoopVirtualIndex.intValue,
                            continuousItemIndices
                        )
                    } else {
                        expandedIndex
                    }
                    // Small delay so the item is still in visible layout info
                    delay(50)
                    if (showsInlineMetadata) {
                        // Keep the expanded item aligned with the details panel below it.
                        // The panel starts at the same 52dp inset as the rail's first card.
                        isExpansionScrollActive = true
                        try {
                            rowListState.animateScrollToItem(expandedVirtualIndex)
                        } finally {
                            isExpansionScrollActive = false
                        }
                        return@collect
                    }
                    // Calculate overshoot using the known final expanded width rather than
                    // the mid-animation layout size which underestimates the trailing edge.
                    val layoutInfo = rowListState.layoutInfo
                    val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == expandedVirtualIndex }
                        ?: return@collect
                    val viewportEnd = layoutInfo.viewportEndOffset
                    val itemEndExpanded = itemInfo.offset + expandedCardWidthPx
                    if (itemEndExpanded > viewportEnd) {
                        // Scroll just enough to reveal the trailing edge plus a small margin.
                        // Flag prevents isBackdropExpandedLambda from collapsing during this scroll.
                        val overshoot = itemEndExpanded - viewportEnd + with(density) { 15.dp.roundToPx() }
                        isExpansionScrollActive = true
                        rowListState.animateScrollBy(overshoot.toFloat())
                        isExpansionScrollActive = false
                    }
                }
        }

        CompositionLocalProvider(LocalBringIntoViewSpec provides horizontalBringIntoViewSpec) {
            val usesPlaceholderShimmer = row.showsPlaceholderShimmer()
            val placeholderShimmerOffsetState = if (usesPlaceholderShimmer) {
                sharedPlaceholderShimmerOffsetState
            } else {
                null
            }

            BoxWithConstraints(Modifier.fillMaxWidth()) {
                LazyRow(
                    state = rowListState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .recompositionHighlighter()
                        .focusRequester(rowFocusRequester)
                        .focusRestorer {
                            if (isInfiniteCarouselRow) {
                                val targetIndex = pendingLoopFocusTarget.intValue
                                    .takeIf { it >= 0 } ?: focusedLoopVirtualIndex.intValue
                                itemFocusRequesters[targetIndex] ?: FocusRequester.Default
                            } else {
                                val savedIdx = rowFocusedIndex.value
                                itemFocusRequesters[savedIdx]
                                    ?: itemFocusRequesters[0]
                                    ?: FocusRequester.Default
                            }
                        }
                        .onFocusChanged { focusState ->
                            val hadRowFocus = rowHasFocus.value
                            rowHasFocus.value = focusState.hasFocus
                            if (hadRowFocus && !focusState.hasFocus) {
                                val firstIndex = if (isContinuousCarouselRow) {
                                    continuousItemIndices.firstOrNull() ?: 0
                                } else {
                                    0
                                }
                                if (focusedItemByRow[rowKey] != firstIndex) {
                                    focusedItemByRow[rowKey] = firstIndex
                                }
                                rowFocusResetNonce.intValue++
                            }
                        }
                        .onPreviewKeyEvent { event ->
                            if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                            val step = when (event.key) {
                                Key.DirectionLeft -> -1
                                Key.DirectionRight -> 1
                                else -> return@onPreviewKeyEvent false
                            }
                            if (isInfiniteCarouselRow) {
                                val currentVirtualIndex = pendingLoopFocusTarget.intValue
                                    .takeIf { it >= 0 } ?: focusedLoopVirtualIndex.intValue
                                val rawTargetVirtualIndex = currentVirtualIndex + step
                                val isAtPhysicalBoundary = rawTargetVirtualIndex !in 0 until Int.MAX_VALUE
                                if (!isFreeboxVideosRow && !isAtPhysicalBoundary) {
                                    return@onPreviewKeyEvent false
                                }
                                val targetVirtualIndex = if (!isAtPhysicalBoundary) {
                                    rawTargetVirtualIndex
                                } else {
                                    val currentLogicalIndex =
                                        row.logicalItemIndexAtVirtualIndex(currentVirtualIndex, continuousItemIndices)
                                            ?: continuousItemIndices.first()
                                    val currentPosition = continuousItemIndices.indexOf(currentLogicalIndex)
                                        .coerceAtLeast(0)
                                    val targetPosition = Math.floorMod(
                                        currentPosition + step,
                                        continuousItemIndices.size
                                    )
                                    row.virtualIndexForLogicalItem(
                                        continuousItemIndices[targetPosition],
                                        cycleIndices = continuousItemIndices
                                    )
                                }
                                val targetIndex = row.logicalItemIndexAtVirtualIndex(targetVirtualIndex, continuousItemIndices)
                                    ?: return@onPreviewKeyEvent false
                                if (isActiveRow()) {
                                    onRowItemFocused(rowKey, targetIndex, isContinueWatchingRow)
                                }
                                focusedLoopVirtualIndex.intValue = targetVirtualIndex
                                pendingLoopFocusTarget.intValue = targetVirtualIndex
                                // Videos owns every horizontal KeyDown to update its details panel;
                                // other rows intercept only at the physical ends of the virtual rail.
                                return@onPreviewKeyEvent true
                            }
                            if (cyclicItemIndices.size < 2) return@onPreviewKeyEvent false
                            val currentPosition = cyclicItemIndices.indexOf(rowFocusedIndex.value)
                            if (currentPosition < 0) return@onPreviewKeyEvent false
                            val targetPosition = currentPosition + step
                            if (targetPosition in cyclicItemIndices.indices) {
                                // Leave ordinary movement to Compose so a failed off-screen
                                // FocusRequester cannot swallow every Left/Right press.
                                return@onPreviewKeyEvent false
                            }
                            val wrappedPosition = if (step < 0) cyclicItemIndices.lastIndex else 0
                            pendingCyclicFocusTarget.intValue = cyclicItemIndices[wrappedPosition]
                            true
                        }
                        .focusGroup()
                        .drawWithContent {
                            drawContent()
                            if (showsInlineMetadata) {
                                // Keep the focus ring visible while masking the old card in the inset.
                                val insetPx = rowLeadingMaskInset.toPx().coerceAtMost(size.width)
                                val leadingEdge = if (layoutDirection == LayoutDirection.Ltr) 0f else size.width - insetPx
                                drawRect(
                                    color = rowMaskColor,
                                    topLeft = Offset(leadingEdge, 0f),
                                    size = Size(insetPx, size.height),
                                )
                            }
                        },
                    contentPadding = PaddingValues(
                        start = rowStartPadding,
                        end = if (showsInlineMetadata) maxWidth else rowStartPadding,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md)
                ) {
                items(
                    count = if (isInfiniteCarouselRow) Int.MAX_VALUE else row.items.list.size,
                    key = { virtualIndex ->
                        val logicalIndex = row.logicalItemIndexAtVirtualIndex(virtualIndex, continuousItemIndices) ?: virtualIndex
                        val item = row.items.list.getOrNull(logicalIndex)
                        if (isInfiniteCarouselRow) {
                            if (pinFirstCard.value && item?.key == pinnedItemKey.value &&
                                virtualIndex == pinnedFirstVirtualIndex.intValue
                            ) firstCardKey else "loop:$virtualIndex:${item?.key.orEmpty()}"
                        } else {
                            if (item == null) {
                                "empty:$virtualIndex"
                            } else {
                                val pinned = pinnedItemKey.value
                                when {
                                    !pinFirstCard.value -> item.key
                                    pinned != null -> if (item.key == pinned) firstCardKey else item.key
                                    logicalIndex == 0 -> firstCardKey
                                    else -> item.key
                                }
                            }
                        }
                    },
                    contentType = { virtualIndex ->
                        val logicalIndex = row.logicalItemIndexAtVirtualIndex(virtualIndex, continuousItemIndices) ?: virtualIndex
                        when (val payload = row.items.list.getOrNull(logicalIndex)?.payload) {
                            is ModernPayload.InstalledApp -> "installed_app"
                            is ModernPayload.ContinueWatching -> "modern_cw_card"
                            is ModernPayload.Catalog -> if (payload.itemId.startsWith("__placeholder_")) "placeholder" else payload.itemType
                            is ModernPayload.CollectionFolder -> "modern_collection_folder_card"
                            null -> "empty"
                        }
                    }
                ) { virtualIndex ->
                    val index = row.logicalItemIndexAtVirtualIndex(virtualIndex, continuousItemIndices) ?: virtualIndex
                    val item = row.items.list.getOrNull(index) ?: return@items
                    val requesterIndex = if (isInfiniteCarouselRow) virtualIndex else index
                    val requester = itemFocusRequesters.getOrPut(requesterIndex) { FocusRequester() }
                    val onFocused = remember(row.key, virtualIndex, index, item.key, isContinueWatchingRow, isInfiniteCarouselRow) {
                        {
                            val isPendingLoopTarget = isInfiniteCarouselRow &&
                                pendingLoopFocusTarget.intValue == virtualIndex
                            if (isPendingLoopTarget) {
                                pendingLoopFocusTarget.intValue = -1
                            }
                            if (isInfiniteCarouselRow) {
                                focusedLoopVirtualIndex.intValue = virtualIndex
                                val keepFrom = (virtualIndex - 48).coerceAtLeast(0)
                                val keepThrough = (virtualIndex + 48).coerceAtMost(Int.MAX_VALUE - 1)
                                itemFocusRequesters.keys.removeAll { it !in keepFrom..keepThrough }
                            }
                            if (!isPendingLoopTarget) {
                                onRowItemFocused(row.key, index, isContinueWatchingRow)
                            }
                            if (pendingRowFocusKey.value == row.key && (pendingRowFocusIndex.value ?: 0) == index) {
                                onPendingRowFocusCleared()
                            }
                        }
                    }

                    // Use derivedStateOf so only the ONE item that becomes/loses
                    // target status recomposes — not all items in all visible rows.
                    val isTargetItem by remember(row.key, virtualIndex, index, isFreeboxVideosRow, isInfiniteCarouselRow) {
                        derivedStateOf {
                            val isPending = if (isInfiniteCarouselRow) {
                                pendingLoopFocusTarget.intValue == virtualIndex ||
                                    (pendingRowFocusKey.value == row.key &&
                                        (pendingRowFocusIndex.value ?: 0) == index &&
                                        row.virtualIndexForLogicalItem(
                                            index,
                                            rowListState.firstVisibleItemIndex,
                                            continuousItemIndices
                                        ) == virtualIndex)
                            } else {
                                pendingRowFocusKey.value == row.key &&
                                    (pendingRowFocusIndex.value ?: 0) == index
                            }
                            val isCurrent = if (isInfiniteCarouselRow) {
                                isActiveRow() && pendingLoopFocusTarget.intValue < 0 &&
                                    focusedLoopVirtualIndex.intValue == virtualIndex
                            } else {
                                isActiveRow() && rowFocusedIndex.value == index
                            }
                            isPending || isCurrent
                        }
                    }

                    when (val payload = item.payload) {
                        is ModernPayload.InstalledApp -> {
                            val context = LocalContext.current
                            val error = stringResource(R.string.installed_apps_launch_error)
                            LaunchedEffect(isTargetItem) {
                                if (isTargetItem) runCatching { requester.requestFocus() }
                            }
                            com.nuvio.tv.ui.components.InstalledAppTile(
                                app = payload.app,
                                tileWidth = installedAppTileWidth,
                                cornerRadius = installedAppTileCornerRadius,
                                modifier = Modifier.focusRequester(requester),
                                onFocused = onFocused,
                                onClick = {
                                    onFocused()
                                    if (!com.nuvio.tv.core.apps.launchInstalledApp(context, payload.app)) {
                                        android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onLongClick = {
                                    contextMenuApp = payload.app
                                    contextMenuIndex = index
                                }
                            )
                        }
                        is ModernPayload.ContinueWatching -> {
                            ModernContinueWatchingRowItem(
                                payload = payload,
                                requester = requester,
                                isTargetItem = isTargetItem,
                                cardWidth = continueWatchingCardWidth,
                                imageHeight = continueWatchingCardHeight,
                                blurUnwatchedEpisodes = blurUnwatchedEpisodes,
                                useEpisodeThumbnails = useEpisodeThumbnails,
                                continueWatchingCardStyle = continueWatchingCardStyle,
                                continueWatchingCornerRadius = continueWatchingCornerRadius,
                                onFocused = onFocused,
                                onContinueWatchingClick = onContinueWatchingClick,
                                onShowOptions = onContinueWatchingOptions
                            )
                        }

                        is ModernPayload.Catalog,
                        is ModernPayload.CollectionFolder -> {
                            val cyclePosition = continuousItemIndices.indexOf(index)
                            val nextCatalogItem = if (isInfiniteCarouselRow && cyclePosition >= 0) {
                                row.items.list.getOrNull(continuousItemIndices[(cyclePosition + 1) % continuousItemIndices.size])?.metaPreview
                            } else {
                                row.items.list.getOrNull(index + 1)?.metaPreview
                            }
                            val prevCatalogItem = if (isInfiniteCarouselRow && cyclePosition >= 0) {
                                row.items.list.getOrNull(continuousItemIndices[
                                    (cyclePosition - 1 + continuousItemIndices.size) % continuousItemIndices.size
                                ])?.metaPreview
                            } else {
                                row.items.list.getOrNull(index - 1)?.metaPreview
                            }
                            val metaPreview = item.metaPreview
                            val navigateToDetailForCurrentSelection: (String, String, String) -> Unit =
                                remember(row.items.list, isFreeboxVideosRow, onNavigateToDetail) {
                                    { clickedItemId: String, clickedItemType: String, clickedAddonBaseUrl: String ->
                                        val pendingVideo = if (isFreeboxVideosRow) {
                                            val pendingLogicalIndex = pendingLoopFocusTarget.intValue
                                                .takeIf { it >= 0 }
                                                ?.let { virtualIndex ->
                                                    row.logicalItemIndexAtVirtualIndex(virtualIndex, continuousItemIndices)
                                                }
                                            row.items.list.getOrNull(pendingLogicalIndex ?: -1)
                                                ?.payload as? ModernPayload.Catalog
                                        } else null
                                        if (pendingVideo != null) {
                                            onNavigateToDetail(
                                                pendingVideo.itemId,
                                                pendingVideo.itemType,
                                                pendingVideo.addonBaseUrl
                                            )
                                        } else {
                                            onNavigateToDetail(
                                                clickedItemId,
                                                clickedItemType,
                                                clickedAddonBaseUrl
                                            )
                                        }
                                    }
                                }
                            val isPlaceholder = payload is ModernPayload.Catalog &&
                                payload.itemId.startsWith("__placeholder_")
                            val onLongPress: () -> Unit = when {
                                payload is ModernPayload.Catalog && metaPreview != null -> remember(metaPreview, payload.addonBaseUrl, row.addonId) {
                                    {
                                        onCatalogItemLongPress(
                                            metaPreview,
                                            payload.addonBaseUrl,
                                            row.addonId == HOME_FAVORITES_ADDON_ID
                                        )
                                        Unit
                                    }
                                }
                                else -> remember(Unit) { {} }
                            }
                            val expandedFocusKey = when (payload) {
                                is ModernPayload.Catalog -> payload.focusKey
                                is ModernPayload.CollectionFolder -> payload.focusKey
                            }
                            val isBackdropExpandedLambda = remember(
                                effectiveExpandEnabled,
                                isRowScrollingState,
                                expandedCatalogFocusKey,
                                expandedFocusKey
                            ) {
                                {
                                    effectiveExpandEnabled &&
                                        (!isRowScrollingState.value || isExpansionScrollActive) &&
                                        expandedCatalogFocusKey.value == expandedFocusKey
                                }
                            }
                            val placeholderFocusBlock = isPlaceholder && index > 0
                            val firstContinuousItemIndex = continuousItemIndices.firstOrNull()
                            val hideRepeatedFirstVideoCard = isFreeboxVideosRow &&
                                isInfiniteCarouselRow &&
                                index == firstContinuousItemIndex &&
                                firstVisibleCycleItemIndexState.value == firstContinuousItemIndex &&
                                virtualIndex != rowListState.firstVisibleItemIndex
                            val itemVisibilityModifier = if (hideRepeatedFirstVideoCard) {
                                // Keep the virtual slot and its focus target, but hide this early repeat
                                // until the original first card has left the leading position.
                                Modifier.graphicsLayer { alpha = 0f }
                            } else {
                                Modifier
                            }
                            Box(
                                modifier = (if (placeholderFocusBlock) {
                                    Modifier.focusProperties { canFocus = false }
                                } else Modifier).then(itemVisibilityModifier)
                            ) {
                            ModernCatalogRowItem(
                                item = item,
                                payload = payload,
                                requester = requester,
                                isTargetItem = isTargetItem,
                                useLandscapePosters = useLandscapePosters,
                                alwaysShowLandscapeClearlogo = alwaysShowLandscapeClearlogo,
                                showLabels = showLabels && !showsInlineMetadata,
                                placeholderShimmerOffsetState = placeholderShimmerOffsetState,
                                posterCardCornerRadius = posterCardCornerRadius,
                                focusedPosterBackdropTrailerMuted = focusedPosterBackdropTrailerMuted,
                                effectiveExpandEnabled = effectiveExpandEnabled,
                                effectiveAutoplayEnabled = effectiveAutoplayEnabled,
                                trailerPlaybackTarget = trailerPlaybackTarget,
                                isBackdropExpanded = isBackdropExpandedLambda,
                                expandedTrailerPreviewUrl = expandedTrailerPreviewUrl,
                                expandedTrailerPreviewAudioUrl = expandedTrailerPreviewAudioUrl,
                                portraitCatalogCardWidth = portraitCatalogCardWidth,
                                portraitCatalogCardHeight = portraitCatalogCardHeight,
                                landscapeCatalogCardWidth = landscapeCatalogCardWidth,
                                landscapeCatalogCardHeight = landscapeCatalogCardHeight,
                                isCatalogItemWatched = isCatalogItemWatched,
                                onFocused = onFocused,
                                onItemFocus = onItemFocus,
                                onPreloadAdjacentItem = remember(nextCatalogItem, prevCatalogItem, onPreloadAdjacentItem) {
                                     {
                                         nextCatalogItem?.let(onPreloadAdjacentItem)
                                         prevCatalogItem?.let(onPreloadAdjacentItem)
                                     }
                                },
                                onCatalogSelectionFocused = onCatalogSelectionFocused,
                                onNavigateToDetail = navigateToDetailForCurrentSelection,
                                onNavigateToFolderDetail = onNavigateToFolderDetail,
                                onLongPress = onLongPress,
                                onBackdropInteraction = onBackdropInteraction,
                                onExpandedCatalogFocusKeyChange = onExpandedCatalogFocusKeyChange,
                                enrichedPreviews = enrichedPreviews
                            )
                            } // Box
                        }
                    }
                }
                }
            }
        }

        if (showsInlineMetadata && isActiveRow()) {
            row.items.list.getOrNull(focusedItemIndex)?.metaPreview?.let { selectedItem ->
                val selectedMetadata = enrichedPreviews.value[selectedItem.id] ?: selectedItem
                HomeRowDetailsPanel(
                    item = selectedMetadata,
                    showSeriesInfo = isSeriesRow || isSeriesType(selectedMetadata.apiType) ||
                        (selectedMetadata.seasonCount ?: 0) > 0
                )
            }
        }
    }

    contextMenuApp?.let { app ->
        val allPackages = remember(row.items.list) {
            row.items.list.mapNotNull { (it.payload as? ModernPayload.InstalledApp)?.app?.packageName }
        }
        val currentIndex = allPackages.indexOfFirst { it == app.packageName }
        val menuViewModel: AppContextMenuViewModel = hiltViewModel()
        AppContextMenuModal(
            app = app,
            onDismiss = {
                contextMenuApp = null
                contextMenuIndex = -1
            },
            onMoveLeft = if (currentIndex > 0) {
                {
                    menuViewModel.moveApp(app.packageName, -1, allPackages)
                }
            } else null,
            onMoveRight = if (currentIndex in 0 until allPackages.size - 1) {
                {
                    menuViewModel.moveApp(app.packageName, 1, allPackages)
                }
            } else null,
            onRemoveFromRow = {
                menuViewModel.removeFromRow(app.packageName, allPackages)
            },
            viewModel = menuViewModel
        )
    }
}


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ModernCarouselCard(
    item: ModernCarouselItem,
    useLandscapeOverlayTreatment: Boolean,
    alwaysShowLandscapeClearlogo: Boolean = false,
    showLabels: Boolean,
    placeholderShimmerOffsetState: State<Float>? = null,
    cardCornerRadius: Dp,
    cardWidth: Dp,
    cardHeight: Dp,
    focusedPosterBackdropExpandEnabled: Boolean,
    isBackdropExpanded: Boolean,
    playTrailerInExpandedCard: Boolean,
    focusedPosterBackdropTrailerMuted: Boolean,
    trailerPreviewUrl: String?,
    trailerPreviewAudioUrl: String?,
    isWatched: Boolean,
    enrichedLogoUrl: String? = null,
    enrichedBackdropUrl: String? = null,
    focusRequester: FocusRequester,
    onFocused: () -> Unit,
    onFocusStateChanged: (Boolean) -> Unit = {},
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    onBackdropInteraction: () -> Unit,
    onTrailerEnded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = remember(cardCornerRadius) { NuvioTheme.configuredShape(RoundedCornerShape(cardCornerRadius)) }
    val cardDepthStyle = LocalCardDepthStyle.current
    val context = LocalContext.current
    val density = LocalDensity.current
    val expandedCardWidth = if (useLandscapeOverlayTreatment) {
        cardWidth
    } else {
        cardHeight * (16f / 9f)
    }
    val targetCardWidth = if (focusedPosterBackdropExpandEnabled && isBackdropExpanded) {
        expandedCardWidth
    } else {
        cardWidth
    }
    val animatedCardWidthState = if (focusedPosterBackdropExpandEnabled) {
        animateDpAsState(
            targetValue = targetCardWidth,
            label = "modernCardWidth"
        )
    } else {
        rememberUpdatedState(cardWidth)
    }
    // Freeze the logo URL for row cards - enrichment updates must not cause flickering.
    // The first non-blank value wins and is never replaced.
    // Primary source of truth is the data-layer frozen value (survives navigation);
    // the remember-state acts as a secondary guard within the same composition.
    val dataFrozenLogo = item.heroPreview.frozenLogoUrl?.takeIf { !it.isPlaceholder() }
    val frozenLogoUrl = remember(item.key) { mutableStateOf(dataFrozenLogo ?: item.heroPreview.logo?.takeIf { !it.isPlaceholder() }) }
    if ((frozenLogoUrl.value.isNullOrBlank() || frozenLogoUrl.value.isPlaceholder()) &&
        !item.heroPreview.logo.isNullOrBlank() && !item.heroPreview.logo.isPlaceholder()) {
        frozenLogoUrl.value = item.heroPreview.logo
    }
    if (!enrichedLogoUrl.isNullOrBlank() && frozenLogoUrl.value != enrichedLogoUrl) {
        // Outside landscape we always pick up the enriched URL so manual artwork
        // updates land instantly. Inside landscape we still adopt the enriched
        // URL when there was no logo to begin with — otherwise the card would
        // permanently fall back to the title text whenever the addon manifest
        // ships items without a logo even
        // though TMDB has one. Once we have any non-blank value we keep it
        // frozen to avoid mid-scroll flicker on enrichment refresh.
        if (!useLandscapeOverlayTreatment || frozenLogoUrl.value.isNullOrBlank()) {
            frozenLogoUrl.value = enrichedLogoUrl
        }
    }
    val effectiveLogoUrl = frozenLogoUrl.value?.takeIf { !it.isPlaceholder() }
    // Freeze the backdrop URL for landscape cards - prevents image reload when enrichment updates backdrop.
    val dataFrozenBackdrop = item.heroPreview.frozenBackdropUrl?.takeIf { !it.isPlaceholder() }
    val frozenBackdropUrl = remember(item.key) { mutableStateOf(dataFrozenBackdrop ?: item.heroPreview.backdrop?.takeIf { !it.isPlaceholder() }) }
    if ((frozenBackdropUrl.value.isNullOrBlank() || frozenBackdropUrl.value.isPlaceholder()) &&
        !item.heroPreview.backdrop.isNullOrBlank() && !item.heroPreview.backdrop.isPlaceholder()) {
        frozenBackdropUrl.value = item.heroPreview.backdrop
    }
    if (!enrichedBackdropUrl.isNullOrBlank() && frozenBackdropUrl.value != enrichedBackdropUrl &&
        (!useLandscapeOverlayTreatment || frozenBackdropUrl.value.isNullOrBlank())
    ) {
        frozenBackdropUrl.value = enrichedBackdropUrl
    }
    val effectiveBackdropUrl = frozenBackdropUrl.value?.takeIf { !it.isPlaceholder() }
    var isFocused by remember { mutableStateOf(false) }
    val payload = item.payload as? ModernPayload.CollectionFolder
    val isCollectionFolder = item.payload is ModernPayload.CollectionFolder
    val effectiveIgnoreLandscapePoster = alwaysShowLandscapeClearlogo
    val useLandscapeAsExpanded = focusedPosterBackdropExpandEnabled && isBackdropExpanded &&
        !alwaysShowLandscapeClearlogo &&
        !item.metaPreview?.landscapePoster.isNullOrBlank()
    val baseImageUrl = if (focusedPosterBackdropExpandEnabled && isBackdropExpanded) {
        if (useLandscapeOverlayTreatment) {
            if (effectiveIgnoreLandscapePoster) {
                effectiveBackdropUrl ?: item.heroPreview.backdrop ?: item.imageUrl ?: item.heroPreview.poster
            } else {
                item.metaPreview?.landscapePoster ?: effectiveBackdropUrl ?: item.heroPreview.backdrop ?: item.imageUrl ?: item.heroPreview.poster
            }
        } else if (useLandscapeAsExpanded) {
            item.metaPreview?.landscapePoster
        } else {
            item.heroPreview.backdrop ?: item.imageUrl ?: item.heroPreview.poster
        }
    } else if (useLandscapeOverlayTreatment && !isCollectionFolder) {
        if (effectiveIgnoreLandscapePoster) {
            effectiveBackdropUrl ?: item.heroPreview.poster
        } else {
            item.metaPreview?.landscapePoster ?: effectiveBackdropUrl ?: item.heroPreview.poster
        }
    } else if (isCollectionFolder && !payload?.coverEmoji.isNullOrBlank()) {
        // Emoji cover folders: never fall back to backdrop for the card poster
        item.imageUrl
    } else {
        item.imageUrl ?: item.heroPreview.poster ?: item.heroPreview.backdrop
    }
    val imageUrl = when {
        payload == null -> baseImageUrl
        !payload.focusGifEnabled -> baseImageUrl
        else -> baseImageUrl
    }
    // GIF overlay: shown on top of the base image only when focused and loaded
    val focusGifUrl = when {
        payload == null -> null
        !payload.focusGifEnabled -> null
        isFocused -> payload.focusGifUrl
        else -> null
    }
    val imageContentScale = when {
        isCollectionFolder && payload?.posterShape == PosterShape.LANDSCAPE -> ContentScale.Fit
        isCollectionFolder -> ContentScale.FillBounds
        else -> ContentScale.Crop
    }
    // Keep decode target stable across expand/collapse to avoid recreating image requests/painters
    // purely due to animated width changes.
    val maxRequestCardWidth = if (focusedPosterBackdropExpandEnabled) {
        maxOf(cardWidth, expandedCardWidth)
    } else {
        cardWidth
    }
    val requestWidthPx = remember(maxRequestCardWidth, density) {
        with(density) { maxRequestCardWidth.roundToPx() }.coerceAtLeast(1)
    }
    val requestHeightPx = remember(cardHeight, density) {
        with(density) { cardHeight.roundToPx() }.coerceAtLeast(1)
    }

    val revalidationKey = com.nuvio.tv.core.image.rememberImageRevalidationKey(imageUrl)
    var customPosterLoadFailed by remember(imageUrl) { mutableStateOf(false) }
    val imageModel = remember(context, imageUrl, requestWidthPx, requestHeightPx, revalidationKey) {
        imageUrl?.let {
            val builder = ImageRequest.Builder(context)
                .data(it)
                .crossfade(true)
                .memoryCacheKey("${it}_${requestWidthPx}x${requestHeightPx}_v$revalidationKey")
                .size(width = requestWidthPx, height = requestHeightPx)
            if (revalidationKey > 0) {
                builder.placeholderMemoryCacheKey("${it}_${requestWidthPx}x${requestHeightPx}_v${revalidationKey - 1}")
            }
            val isLandscapeCustomPoster = useLandscapeOverlayTreatment && !effectiveIgnoreLandscapePoster && !item.metaPreview?.landscapePoster.isNullOrBlank()
            val fallbackUrl = if (isLandscapeCustomPoster) {
                // Landscape custom poster -> fall back to original backdrop
                item.metaPreview?.background ?: item.heroPreview.backdrop ?: item.metaPreview?.rawPosterUrl
            } else {
                item.metaPreview?.rawPosterUrl
            }
            if (!fallbackUrl.isNullOrBlank() && fallbackUrl != it) {
                builder.memoryCacheKeyExtras(
                    mapOf(com.nuvio.tv.core.image.CustomPosterFallbackInterceptor.FALLBACK_URL_KEY to fallbackUrl)
                )
            }
            builder.build()
        }
    }
    val logoHeight = cardHeight * 0.34f
    val logoHeightPx = remember(logoHeight, density) {
        with(density) { logoHeight.roundToPx() }.coerceAtLeast(1)
    }
    val maxLogoWidthPx = remember(maxRequestCardWidth, density) {
        with(density) { (maxRequestCardWidth * 0.62f).roundToPx() }.coerceAtLeast(1)
    }

    val logoModel = remember(context, effectiveLogoUrl, maxLogoWidthPx, logoHeightPx) {
        effectiveLogoUrl?.let {
            ImageRequest.Builder(context)
                .data(it)
                .crossfade(true)
                .memoryCacheKey("${it}_${maxLogoWidthPx}x${logoHeightPx}")
                .size(width = maxLogoWidthPx, height = logoHeightPx)
                .build()
        }
    }
    var landscapeLogoLoadFailed by remember(effectiveLogoUrl) { mutableStateOf(false) }
    val shouldPlayTrailerInCard = playTrailerInExpandedCard && !trailerPreviewUrl.isNullOrBlank()
    var trailerFirstFrameRendered by remember(trailerPreviewUrl) { mutableStateOf(false) }

    // Use the image model directly — Coil's memory cache handles repeated
    // requests efficiently without needing scroll-aware request swapping.
    val hasImage = !imageUrl.isNullOrBlank()
    val hasLandscapeLogo =
        showLabels && (useLandscapeOverlayTreatment || isBackdropExpanded) &&
            !isCollectionFolder &&
            !effectiveLogoUrl.isNullOrBlank() &&
            !landscapeLogoLoadFailed &&
            !(useLandscapeAsExpanded && !trailerFirstFrameRendered) &&
            (effectiveIgnoreLandscapePoster || isBackdropExpanded || item.metaPreview?.landscapePoster.isNullOrBlank() || customPosterLoadFailed)
    var longPressTriggered by remember { mutableStateOf(false) }
    val longPressKeyTracker = rememberLongPressKeyTracker()
    val backgroundCardColor = NuvioTheme.colors.BackgroundCard
    val focusRingBorder = NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs)
    val titleMedium = MaterialTheme.typography.titleMedium
    val backgroundPainter = remember(backgroundCardColor) { ColorPainter(backgroundCardColor) }
    val focusedBorder = remember(cardShape, focusRingBorder) {
        Border(
            border = focusRingBorder,
            shape = cardShape
        )
    }
    // While the user is dragging the list via held DPAD (see LazyColumn-level fast
    // scroll takeover in ModernHomeContent), hide focus chrome entirely — the list is
    // sliding like a touch swipe and showing a border / glow jittering across every
    // card the drag passes over would break that illusion. The chrome reappears the
    // moment the user releases the key, when requestFocus lands focus on whichever
    // card is visible at the leading edge.
    val isFastScrollingState = LocalFastScrollActive.current
    val isFastScrolling = isFastScrollingState.value
    val transparentFocusBorder = remember(cardShape) {
        Border(
            border = BorderStroke(NuvioTheme.spacing.none, Color.Transparent),
            shape = cardShape
        )
    }
    val effectiveFocusedBorder = if (isFastScrolling) transparentFocusBorder else focusedBorder
    val cardFocusGlow = when (payload) {
        is ModernPayload.CollectionFolder -> rememberArtworkBackedFocusGlow(
            imageUrl = imageUrl,
            fallbackSeed = "${item.title}:${payload.collectionTitle}",
            enabled = payload.focusGlowEnabled
        )
        else -> Glow.None
    }
    val effectiveFocusGlow = if (isFastScrolling) Glow.None else cardFocusGlow
    val titleStyle = remember(titleMedium) {
        titleMedium.copy(fontWeight = FontWeight.Medium)
    }

    Column(
        modifier = modifier
            .animatedWidth(animatedCardWidthState)
            .recompositionHighlighter(),
        verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.sm)
    ) {
        LightweightCard(
            onClick = {
                if (longPressTriggered) {
                    longPressTriggered = false
                } else {
                    onClick()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                    onFocusStateChanged(it.isFocused)
                    if (it.isFocused) {
                        onFocused()
                    }
                }
                .onPreviewKeyEvent { event ->
                    val native = event.nativeKeyEvent
                    if (native.action == AndroidKeyEvent.ACTION_DOWN) {
                        if (focusedPosterBackdropExpandEnabled && shouldResetBackdropTimer(event.key)) {
                            onBackdropInteraction()
                        }
                        if (native.keyCode == AndroidKeyEvent.KEYCODE_MENU) {
                            longPressTriggered = true
                            onLongPress()
                            return@onPreviewKeyEvent true
                        }
                    }
                    if (longPressKeyTracker.handle(native, ::isSelectKey) {
                            longPressTriggered = true
                            onLongPress()
                        }
                    ) {
                        if (native.action == AndroidKeyEvent.ACTION_UP) {
                            longPressTriggered = false
                        }
                        return@onPreviewKeyEvent true
                    }
                    if (native.action == AndroidKeyEvent.ACTION_UP &&
                        longPressTriggered &&
                        (isSelectKey(native.keyCode) || native.keyCode == AndroidKeyEvent.KEYCODE_MENU)
                    ) {
                        longPressTriggered = false
                        return@onPreviewKeyEvent true
                    }
                    false
                },
            shape = cardShape,
            containerColor = backgroundCardColor,
            focusedBorder = effectiveFocusedBorder,
            focusedGlow = effectiveFocusGlow
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(cardShape)
                    .nuvioCardDepth(
                        shape = cardShape,
                        surface = CardDepthSurface.POSTERS,
                        style = cardDepthStyle
                    )
            ) {
                val mediaLayerModifier = remember(hasLandscapeLogo) {
                    if (hasLandscapeLogo) {
                        Modifier
                            .fillMaxSize()
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(brush = MODERN_LANDSCAPE_LOGO_GRADIENT, size = size)
                                }
                            }
                    } else {
                        Modifier.fillMaxSize()
                    }
                }

                Box(modifier = mediaLayerModifier) {
                    val isPlaceholderItem = item.imageUrl.isPlaceholder()
                    if (isPlaceholderItem) {
                        // Horizontal sweeping shimmer for placeholder cards
                        val effectivePlaceholderShimmerOffsetState =
                            placeholderShimmerOffsetState ?: rememberPlaceholderShimmerOffsetState(
                                label = "placeholderShimmer"
                            )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .placeholderCardShimmer(effectivePlaceholderShimmerOffsetState)
                        )
                    } else if (hasImage) {
                        AsyncImage(
                            model = imageModel,
                            contentDescription = item.title,
                            modifier = Modifier.fillMaxSize(),
                            placeholder = backgroundPainter,
                            error = backgroundPainter,
                            fallback = backgroundPainter,
                            contentScale = imageContentScale,
                            onError = {
                                if (!item.metaPreview?.landscapePoster.isNullOrBlank() || !item.metaPreview?.rawPosterUrl.isNullOrBlank()) {
                                    customPosterLoadFailed = true
                                }
                            }
                        )
                    } else if (isCollectionFolder && !payload?.coverEmoji.isNullOrBlank()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = payload!!.coverEmoji!!,
                                fontSize = 48.sp
                            )
                        }
                    } else {
                        MonochromePosterPlaceholder()
                    }

                    // GIF overlay: renders on top of image or emoji, visible only once loaded
                    if (!focusGifUrl.isNullOrBlank()) {
                        val gifModel = remember(context, focusGifUrl, requestWidthPx, requestHeightPx) {
                            ImageRequest.Builder(context)
                                .data(focusGifUrl)
                                .memoryCacheKey("${focusGifUrl}_${requestWidthPx}x${requestHeightPx}")
                                .size(width = requestWidthPx, height = requestHeightPx)
                                .build()
                        }
                        var gifLoaded by remember(focusGifUrl) { mutableStateOf(false) }
                        val gifAlpha by animateFloatAsState(
                            targetValue = if (gifLoaded) 1f else 0f,
                            animationSpec = tween(durationMillis = 200),
                            label = "gifFadeIn"
                        )
                        AsyncImage(
                            model = gifModel,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { alpha = gifAlpha },
                            contentScale = imageContentScale,
                            onSuccess = { gifLoaded = true }
                        )
                    }

                    if (shouldPlayTrailerInCard) {
                        key(trailerPreviewUrl) {
                            TrailerPlayer(
                                trailerUrl = trailerPreviewUrl,
                                trailerAudioUrl = trailerPreviewAudioUrl,
                                isPlaying = true,
                                onEnded = onTrailerEnded,
                                onFirstFrameRendered = { trailerFirstFrameRendered = true },
                                muted = focusedPosterBackdropTrailerMuted,
                                cropToFill = true,
                                overscanZoom = MODERN_TRAILER_OVERSCAN_ZOOM,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                if (item.metaPreview?.id?.startsWith(FREEBOX_FOLDER_ID_PREFIX) == true) {
                    FreeboxFolderBadge(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 4.dp, bottom = 4.dp)
                    )
                }

                if (item.isInProgress) {
                    InProgressBadge(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = NuvioTheme.spacing.sm, top = NuvioTheme.spacing.sm)
                            .zIndex(1.9f)
                    )
                }

                if (showLabels && hasLandscapeLogo) {
                    AsyncImage(
                        model = logoModel,
                        contentDescription = item.title,
                        onError = { landscapeLogoLoadFailed = true },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth(0.62f)
                            .height(cardHeight * 0.34f)
                            .padding(start = 10.dp, end = 10.dp, bottom = NuvioTheme.spacing.sm),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.CenterStart
                    )
                } else if (showLabels && (useLandscapeOverlayTreatment || isBackdropExpanded) && !isCollectionFolder && !(useLandscapeAsExpanded && !trailerFirstFrameRendered) && (effectiveIgnoreLandscapePoster || item.metaPreview?.landscapePoster.isNullOrBlank() || customPosterLoadFailed)) {
                    Text(
                        text = item.title,
                        style = titleStyle.copy(
                            textDirection = item.title.contentTextDirection()
                        ),
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth(0.62f)
                            .padding(start = 10.dp, end = 10.dp, bottom = NuvioTheme.spacing.md)
                    )
                }

                if (isWatched) {
                    WatchedMarker()
                }
            }
        }

        if (showLabels && !isBackdropExpanded && item.title.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NuvioTheme.spacing.xs)
            ) {
                Text(
                    text = item.title,
                    style = titleStyle.copy(
                        textDirection = item.title.contentTextDirection()
                    ),
                    color = NuvioTheme.colors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                item.subtitle?.takeIf { it.isNotBlank() }?.let { subtitle ->
                    Spacer(modifier = Modifier.height(NuvioTheme.spacing.xxs))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium.copy(
                            textDirection = subtitle.contentTextDirection()
                        ),
                        color = NuvioTheme.colors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


/**
 * Keys that should collapse an expanded poster so navigation can re-arm expand.
 *
 * Select keys (Center / Enter) are intentionally excluded: holding them opens the
 * action menu / long-press options. Resetting the expand timer on those keys made
 * the Expanded Card collapse and re-expand (and restart trailers) while the menu
 * was opening (#2574).
 */
private fun shouldResetBackdropTimer(key: Key): Boolean {
    return when (key) {
        Key.DirectionUp,
        Key.DirectionDown,
        Key.DirectionLeft,
        Key.DirectionRight -> true
        else -> false
    }
}

private fun isSelectKey(keyCode: Int): Boolean {
    return keyCode == AndroidKeyEvent.KEYCODE_DPAD_CENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_ENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_NUMPAD_ENTER
}

/**
 * Modifier.width(width.value) that reads the width while measuring. The expand-on-focus width
 * animation then re-lays out the card on each frame instead of recomposing all of it.
 */
private fun Modifier.animatedWidth(width: State<Dp>): Modifier = this then AnimatedWidthElement(width)

private data class AnimatedWidthElement(val width: State<Dp>) : ModifierNodeElement<AnimatedWidthNode>() {
    override fun create() = AnimatedWidthNode(width)

    override fun update(node: AnimatedWidthNode) {
        node.width = width
    }
}

// Measures exactly like the node behind Modifier.width: a fixed width, coerced into the incoming
// constraints, with the incoming height constraints passed through.
private class AnimatedWidthNode(var width: State<Dp>) : LayoutModifierNode, Modifier.Node() {
    private fun Density.widthPx() = width.value.roundToPx().coerceAtLeast(0)

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints.constrain(Constraints.fixedWidth(widthPx())))
        return layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int) = widthPx()

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int) = widthPx()

    override fun IntrinsicMeasureScope.minIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int) =
        measurable.minIntrinsicHeight(width)

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int) =
        measurable.maxIntrinsicHeight(width)
}
