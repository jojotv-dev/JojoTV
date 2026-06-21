package com.nuvio.tv.ui.components

import android.view.KeyEvent as AndroidKeyEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.nuvio.tv.R
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Text
import com.nuvio.tv.ui.screens.home.ContinueWatchingItem
import com.nuvio.tv.data.freebox.freeboxFileNameOnly
import com.nuvio.tv.data.freebox.freeboxDisplayName
import com.nuvio.tv.data.freebox.freeboxVideoDisplayTitle
import com.nuvio.tv.ui.theme.NuvioColors
import com.nuvio.tv.ui.theme.NuvioTheme
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.request.crossfade
import kotlin.math.roundToInt
import java.util.concurrent.TimeUnit
import com.nuvio.tv.ui.util.recompositionHighlighter
import com.nuvio.tv.ui.util.localizeEpisodeTitle
import com.nuvio.tv.ui.util.rememberLongPressKeyTracker
import com.nuvio.tv.ui.util.computeAirDateBadgeText
import androidx.compose.foundation.basicMarquee

private val CwCardShape = RoundedCornerShape(12.dp)
private val CwClipShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
private val BadgeShape = RoundedCornerShape(4.dp)
private val CwNewEpisodeBadgeColor = Color(0xFF1D4ED8)
private val CwNewSeasonBadgeColor = Color(0xFFB45309)

/** URLs that failed to load  â€” â€™ â€” â€š â€” Â¢ â€” Â¢ â€” Â¢ â€” â€šÂ¬ â€” Â¡ â€” â€š â€” Â¬ â€” Â¢ â€” Â¢ â€” â‚¬ â€” Â¬ â€” â€š â€” Â skip them immediately on next recomposition. */
internal val brokenImageUrls = java.util.Collections.synchronizedSet(mutableSetOf<String>())

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun ContinueWatchingSection(
    items: List<ContinueWatchingItem>,
    onItemClick: (ContinueWatchingItem) -> Unit,
    onDetailsClick: (ContinueWatchingItem) -> Unit = onItemClick,
    onRemoveItem: (ContinueWatchingItem) -> Unit,
    onDeleteFromFreebox: ((ContinueWatchingItem) -> Unit)? = null,
    onRenameFreebox: ((ContinueWatchingItem, String) -> Unit)? = null,
    onMarkAsUnwatched: ((ContinueWatchingItem) -> Unit)? = null,
    onStartFromBeginning: (ContinueWatchingItem) -> Unit = {},
    showManualPlayOption: Boolean = false,
    onPlayManually: (ContinueWatchingItem) -> Unit = {},
    modifier: Modifier = Modifier,
    focusedItemIndex: Int = -1,
    onItemFocused: (itemIndex: Int) -> Unit = {},
    blurUnwatchedEpisodes: Boolean = false,
    useEpisodeThumbnails: Boolean = true,
    downFocusRequester: FocusRequester? = null,
    cardWidth: Dp = 288.dp,
    imageHeight: Dp = 162.dp
) {
    if (items.isEmpty()) return

    val focusRequesters = remember(items.size) { List(items.size) { FocusRequester() } }
    var lastFocusedIndex by remember { mutableIntStateOf(-1) }
    var lastRequestedFocusIndex by remember { mutableIntStateOf(-1) }
    var pendingFocusIndex by remember { mutableStateOf<Int?>(null) }
    var optionsItem by remember { mutableStateOf<ContinueWatchingItem?>(null) }
    var renameItem by remember { mutableStateOf<ContinueWatchingItem?>(null) }

    val listState = rememberLazyListState()

    // Restore focus to specific item if requested
    LaunchedEffect(focusedItemIndex) {
        if (focusedItemIndex >= 0 && focusedItemIndex < items.size) {
            if (lastRequestedFocusIndex == focusedItemIndex) return@LaunchedEffect
            var focused = false
            for (attempt in 0 until 3) {
                withFrameNanos { }
                focused = runCatching { focusRequesters[focusedItemIndex].requestFocus() }.isSuccess
                if (focused) break
            }
            if (focused) {
                lastRequestedFocusIndex = focusedItemIndex
            }
        } else {
            lastRequestedFocusIndex = -1
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 48.dp, end = 48.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.continue_watching),
                style = MaterialTheme.typography.headlineMedium,
                color = NuvioColors.TextPrimary
            )
        }

        val restoreFocusRequester = remember(lastFocusedIndex, focusRequesters.size) {
            val idx = if (lastFocusedIndex >= 0 && lastFocusedIndex < focusRequesters.size)
                lastFocusedIndex else 0
            focusRequesters.getOrNull(idx) ?: FocusRequester.Default
        }

        val density = LocalDensity.current
        val defaultBringIntoViewSpec = LocalBringIntoViewSpec.current
        val layoutDirection = LocalLayoutDirection.current
        val isRtl = layoutDirection == LayoutDirection.Rtl
        val horizontalBringIntoViewSpec = remember(density, defaultBringIntoViewSpec, isRtl) {
            val startPx = with(density) { 48.dp.roundToPx() }
            @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
            object : BringIntoViewSpec {
                override val scrollAnimationSpec: AnimationSpec<Float> =
                    defaultBringIntoViewSpec.scrollAnimationSpec
                override fun calculateScrollDistance(offset: Float, size: Float, containerSize: Float): Float {
                    val childSize = kotlin.math.abs(size)
                    if (isRtl) {
                        val childSmallerThanParent = childSize <= containerSize
                        val initialTarget = containerSize - startPx.toFloat()
                        val targetForTrailingEdge =
                            if (childSmallerThanParent && initialTarget < childSize) {
                                childSize
                            } else {
                                initialTarget
                            }
                        return (offset + size) - targetForTrailingEdge
                    } else {
                        val target = startPx.toFloat()
                        val space = containerSize - target
                        val leading = if (childSize <= containerSize && space < childSize) containerSize - childSize else target
                        return offset - leading
                    }
                }
            }
        }

        CompositionLocalProvider(LocalBringIntoViewSpec provides horizontalBringIntoViewSpec) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .focusRestorer(restoreFocusRequester)
                .focusGroup(),
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            state = listState
        ) {
            itemsIndexed(
                items = items,
                key = { _, progress ->
                    when (progress) {
                        is ContinueWatchingItem.InProgress ->
                            "cw_${progress.progress.contentId}_${progress.progress.videoId}_${progress.progress.season ?: -1}_${progress.progress.episode ?: -1}"
                        is ContinueWatchingItem.NextUp ->
                            "nextup_${progress.info.contentId}_${progress.info.videoId}_${progress.info.season}_${progress.info.episode}"
                    }
                }
            ) { index, progress ->
                val focusModifier = when {
                    index < focusRequesters.size -> Modifier.focusRequester(focusRequesters[index])
                    else -> Modifier
                }

                    ContinueWatchingCard(
                    item = progress,
                    onClick = { onItemClick(progress) },
                    onLongPress = { optionsItem = progress },
                    blurUnwatchedEpisodes = blurUnwatchedEpisodes,
                    useEpisodeThumbnails = useEpisodeThumbnails,
                    cardWidth = cardWidth,
                    imageHeight = imageHeight,
                    modifier = Modifier
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused && lastFocusedIndex != index) {
                                lastFocusedIndex = index
                                onItemFocused(index)
                            }
                        }
                        .then(
                            if (downFocusRequester != null) {
                                Modifier.focusProperties { down = downFocusRequester }
                            } else Modifier
                        )
                        .then(focusModifier)
                )
            }
        }
        } // CompositionLocalProvider
    }

    val menuItem = optionsItem
    if (menuItem != null) {
        val menuContentId = when (menuItem) {
            is ContinueWatchingItem.InProgress -> menuItem.progress.contentId
            is ContinueWatchingItem.NextUp -> menuItem.info.contentId
        }
        val isFreeboxItem = menuContentId.startsWith("freebox:")
        val isIptvItem = menuContentId.startsWith("iptv_")

        ContinueWatchingOptionsDialog(
            item = menuItem,
            onDismiss = { optionsItem = null },
            onDeleteFromFreebox = if (isFreeboxItem) onDeleteFromFreebox?.let { cb -> { cb(menuItem) } } else null,
            onRenameFreebox = if (onRenameFreebox != null && menuItem is ContinueWatchingItem.InProgress && menuItem.progress.contentId.startsWith("freebox:")) {
                {
                    renameItem = menuItem
                    optionsItem = null
                }
            } else null,
            onMarkAsUnwatched = onMarkAsUnwatched?.let { cb ->
                if (menuItem is ContinueWatchingItem.InProgress) {
                    {
                        val targetIndex = if (items.size <= 1) null else minOf(lastFocusedIndex, items.size - 2)
                        pendingFocusIndex = targetIndex
                        cb(menuItem)
                        optionsItem = null
                    }
                } else null
            },
            onRemove = {
                val targetIndex = if (items.size <= 1) null else minOf(lastFocusedIndex, items.size - 2)
                pendingFocusIndex = targetIndex
                onRemoveItem(menuItem)
                optionsItem = null
            },
            onDetails = {
                onDetailsClick(menuItem)
                optionsItem = null
            },
            onStartFromBeginning = {
                onStartFromBeginning(menuItem)
                optionsItem = null
            },
            showPlayManually = showManualPlayOption && !isIptvItem,
            onPlayManually = {
                onPlayManually(menuItem)
                optionsItem = null
            }
        )
    }

    val selectedRenameItem = renameItem
    if (selectedRenameItem is ContinueWatchingItem.InProgress && onRenameFreebox != null) {
        FreeboxRenameDialog(
            initialName = freeboxFileNameOnly(selectedRenameItem.progress.name.ifBlank { selectedRenameItem.progress.videoId }),
            onDismiss = { renameItem = null },
            onConfirm = { newName ->
                onRenameFreebox(selectedRenameItem, newName)
                renameItem = null
            }
        )
    }

    LaunchedEffect(items.size, pendingFocusIndex) {
        val target = pendingFocusIndex
        if (target != null && target >= 0 && target < focusRequesters.size) {
            var focused = false
            for (attempt in 0 until 3) {
                withFrameNanos { }
                focused = runCatching { focusRequesters[target].requestFocus() }.isSuccess
                if (focused) break
            }
            if (focused) {
                lastRequestedFocusIndex = target
            }
            pendingFocusIndex = null
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ContinueWatchingCard(
    item: ContinueWatchingItem,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    cardWidth: Dp = 288.dp,
    imageHeight: Dp = 162.dp,
    blurUnwatchedEpisodes: Boolean = false,
    useEpisodeThumbnails: Boolean = true,
    showBadge: Boolean = true,
    showProgressBar: Boolean = true,
    cardSizeMultiplier: Float = 1.5f,
    onDirectionLeft: (() -> Boolean)? = null
) {
    var longPressTriggered by remember { mutableStateOf(false) }
    val longPressKeyTracker = rememberLongPressKeyTracker()

    val progress = remember(item) { (item as? ContinueWatchingItem.InProgress)?.progress }
    val nextUp = remember(item) { (item as? ContinueWatchingItem.NextUp)?.info }
    val cardContext = LocalContext.current
    val episodeStr = remember(progress, nextUp, cardContext) {
        val season = progress?.season ?: nextUp?.season
        val episode = progress?.episode ?: nextUp?.episode
        if (season != null && episode != null) {
            cardContext.getString(R.string.season_episode_format, season, episode)
        } else {
            null
        }
    }
    val strUpcoming = stringResource(R.string.cw_upcoming)
    val strNextUp = stringResource(R.string.cw_next_up)
    val strNewEpisode = stringResource(R.string.cw_new_episode)
    val strNewSeason = stringResource(R.string.cw_new_season)
    val strResume = stringResource(R.string.cw_resume)
    val strPercentWatched = stringResource(R.string.cw_percent_watched)
    val strHoursMinLeft = stringResource(R.string.cw_hours_min_left)
    val strMinLeft = stringResource(R.string.cw_min_left)
    val nextUpBadgeText = nextUp?.let { info ->
        if (info.isReleaseAlert) {
            if (info.isNewSeasonRelease) strNewSeason else strNewEpisode
        } else if (!info.hasAired) {
            computeAirDateBadgeText(cardContext, info.released, info.airDateLabel) ?: strUpcoming
        } else {
            strNextUp
        }
    }
    val remainingText = progress?.let {
        remember(it.position, it.duration, it.progressPercent) {
            formatContinueWatchingProgressLabel(
                progress = it,
                resumeLabel = strResume,
                percentWatchedLabel = strPercentWatched,
                hoursMinLeftLabel = strHoursMinLeft,
                minLeftLabel = strMinLeft
            )
        }
    }
    val isFreeboxProgress = progress?.isFreeboxProgressForDisplay() == true
    val badgeText = remember(isFreeboxProgress, remainingText, nextUpBadgeText, strNextUp) {
        if (isFreeboxProgress && (progress?.duration ?: 0L) == 0L) null
        else remainingText ?: nextUpBadgeText ?: strNextUp
    }
    val progressFraction = remember(progress) { progress?.progressPercentage ?: 0f }
    val preferLandscapeArtwork = cardWidth > imageHeight
    val imageModel = remember(nextUp, progress, item, useEpisodeThumbnails, preferLandscapeArtwork) {
        fun firstNonBroken(vararg candidates: String?): String? {
            return candidates.firstOrNull { !it.isNullOrBlank() && it !in brokenImageUrls }?.trim()
        }
        when {
            nextUp != null && !nextUp.hasAired -> firstNonBroken(
                nextUp.backdrop,
                nextUp.poster,
                nextUp.thumbnail
            )
            nextUp != null && useEpisodeThumbnails -> firstNonBroken(
                nextUp.thumbnail,
                nextUp.backdrop,
                nextUp.poster
            )
            nextUp != null -> firstNonBroken(
                nextUp.backdrop,
                nextUp.poster,
                nextUp.thumbnail
            )
            useEpisodeThumbnails -> firstNonBroken(
                (item as? ContinueWatchingItem.InProgress)?.episodeThumbnail,
                if (isFreeboxProgress && !preferLandscapeArtwork) progress?.poster else progress?.backdrop,
                if (isFreeboxProgress && !preferLandscapeArtwork) progress?.backdrop else progress?.poster
            )
            else -> firstNonBroken(
                if (isFreeboxProgress && !preferLandscapeArtwork) progress?.poster else progress?.backdrop,
                if (isFreeboxProgress && !preferLandscapeArtwork) progress?.backdrop else progress?.poster,
                (item as? ContinueWatchingItem.InProgress)?.episodeThumbnail
            )
        }
    }
    val fallbackImageModel = remember(nextUp, progress, item, preferLandscapeArtwork) {
        when {
            nextUp != null -> firstNonBlank(
                nextUp.backdrop,
                nextUp.poster
            )
            else -> firstNonBlank(
                if (isFreeboxProgress && !preferLandscapeArtwork) progress?.poster else progress?.backdrop,
                if (isFreeboxProgress && !preferLandscapeArtwork) progress?.backdrop else progress?.poster
            )
        }
    }
    var usesFallbackImage by remember { mutableStateOf(false) }
    // Reset fallback state when the item changes
    LaunchedEffect(imageModel) { usesFallbackImage = false }

    val effectiveImageModel = if (usesFallbackImage) fallbackImageModel else imageModel
    val titleText = remember(progress, nextUp) {
        if (progress != null && progress.isFreeboxProgressForDisplay()) {
            freeboxVideoDisplayTitle(
                progress.name.ifBlank { progress.videoId },
                progress.duration
            )
        } else {
            progress?.name ?: nextUp?.name.orEmpty()
        }
    }
    val context = LocalContext.current
    val strAirsDateForEpisode = computeAirDateBadgeText(context, nextUp?.released, nextUp?.airDateLabel)
    val episodeTitle = remember(progress, nextUp, context, strAirsDateForEpisode) {
        when {
            progress != null -> progress.episodeTitle?.localizeEpisodeTitle(context)
            nextUp != null && !nextUp.hasAired -> nextUp.episodeTitle?.localizeEpisodeTitle(context) ?: strAirsDateForEpisode
            else -> nextUp?.episodeTitle?.localizeEpisodeTitle(context)
        }
    }
    val effectiveCardWidth = remember(cardWidth, cardSizeMultiplier) {
        (cardWidth.value * cardSizeMultiplier).roundToInt().dp
    }
    val effectiveImageHeight = remember(imageHeight, cardSizeMultiplier) {
        (imageHeight.value * cardSizeMultiplier).roundToInt().dp
    }
    val density = LocalDensity.current
    val requestWidthPx = remember(effectiveCardWidth, density) {
        with(density) { effectiveCardWidth.roundToPx() }.coerceAtLeast(1)
    }
    val requestHeightPx = remember(effectiveImageHeight, density) {
        with(density) { effectiveImageHeight.roundToPx() }.coerceAtLeast(1)
    }
    val shouldBlur = blurUnwatchedEpisodes && useEpisodeThumbnails && nextUp != null
    val imageRequest = remember(effectiveImageModel, requestWidthPx, requestHeightPx, shouldBlur) {
        ImageRequest.Builder(context)
            .data(effectiveImageModel)
            .crossfade(true)
            .memoryCacheKey("${effectiveImageModel}_${requestWidthPx}x${requestHeightPx}_blur${shouldBlur}")
            .size(width = requestWidthPx, height = requestHeightPx)
            .apply {
                if (shouldBlur) transformations(com.nuvio.tv.ui.util.BlurTransformation())
            }
            .build()
    }

    val bgColor = NuvioColors.Background
    val badgeBackground = remember(bgColor, nextUp) {
        when {
            nextUp?.isNewSeasonRelease == true -> CwNewSeasonBadgeColor
            nextUp?.isReleaseAlert == true -> CwNewEpisodeBadgeColor
            else -> bgColor.copy(alpha = 0.8f)
        }
    }

    val bgCardColor = NuvioColors.BackgroundCard
    val backgroundPainter = remember(bgCardColor) { androidx.compose.ui.graphics.painter.ColorPainter(bgCardColor) }

    Card(
        onClick = {
            if (longPressTriggered) {
                longPressTriggered = false
            } else {
                onClick()
            }
        },
        modifier = modifier
            .width(effectiveCardWidth)
            .recompositionHighlighter()
            .onPreviewKeyEvent { event ->
                val native = event.nativeKeyEvent
                if (native.action == AndroidKeyEvent.ACTION_DOWN) {
                    if (native.keyCode == AndroidKeyEvent.KEYCODE_DPAD_LEFT && onDirectionLeft?.invoke() == true) {
                        return@onPreviewKeyEvent true
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
        shape = CardDefaults.shape(shape = CwCardShape),
        colors = CardDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(2.dp, NuvioColors.FocusRing),
                shape = CwCardShape
            )
        ),
        scale = CardDefaults.scale(focusedScale = 1f)
    ) {
        Column {
            // Thumbnail with progress overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(effectiveImageHeight)
                    .clip(CwClipShape)
            ) {
                // Background image with size hints for efficient decoding
                if (effectiveImageModel.isNullOrBlank()) {
                    MonochromePosterPlaceholder()
                } else {
                    AsyncImage(
                        model = imageRequest,
                        contentDescription = titleText,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                compositingStrategy =
                                    CompositingStrategy.Offscreen
                            }
                            .clip(CwClipShape),
                        placeholder = backgroundPainter,
                        error = backgroundPainter,
                        fallback = backgroundPainter,
                        contentScale = ContentScale.Crop,
                        onError = {
                            // Primary image failed (e.g. broken thumbnail URL)  â€” â€™ â€” â€š â€” Â¢ â€” Â¢ â€” Â¢ â€” â€šÂ¬ â€” Â¡ â€” â€š â€” Â¬ â€” Â¢ â€” Â¢ â€” â‚¬ â€” Â¬ â€” â€š â€” Â remember and try fallback.
                            if (!usesFallbackImage && effectiveImageModel != null) {
                                brokenImageUrls.add(effectiveImageModel)
                                if (fallbackImageModel != null && fallbackImageModel != effectiveImageModel) {
                                    usesFallbackImage = true
                                }
                            }
                        }
                    )
                }


                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .height(64.dp)
                        .drawWithCache {
                            val gradient = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.55f)
                                ),
                                startY = 0f,
                                endY = size.height
                            )
                            onDrawBehind { drawRect(gradient) }
                        }
                )

                if (showBadge) badgeText?.let { text ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .clip(BadgeShape)
                            .background(badgeBackground)
                            .padding(horizontal = 5.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelSmall,
                            color = NuvioColors.TextPrimary,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                if (showProgressBar && progress != null && progress.duration > 0L) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(1.5.dp))
                            .height(3.dp)
                            .background(Color.Black.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressFraction)
                                .clip(RoundedCornerShape(1.5.dp))
                                .height(3.dp)
                                .background(NuvioColors.Secondary)
                        )
                    }
                }
            }

            // Title below thumbnail
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = NuvioColors.Secondary.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                // Episode info (for series)
                if (episodeStr != null) {
                    Text(
                        text = episodeStr,
                        style = MaterialTheme.typography.labelMedium,
                        color = NuvioColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleSmall,
                    color = NuvioColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                )

                // Episode title if available (not for Freebox items)
                val isFreebox = progress?.isFreeboxProgressForDisplay() == true
                if (!isFreebox) {
                    episodeTitle?.let { title ->
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodySmall,
                            color = NuvioTheme.extendedColors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ContinueWatchingOptionsDialog(
    item: ContinueWatchingItem,
    onDismiss: () -> Unit,
    onRemove: () -> Unit,
    onDetails: () -> Unit,
    onStartFromBeginning: () -> Unit = {},
    showPlayManually: Boolean = false,
    onPlayManually: () -> Unit = {},
    onRenameFreebox: (() -> Unit)? = null,
    onMarkAsUnwatched: (() -> Unit)? = null,
    onDeleteFromFreebox: (() -> Unit)? = null
) {
    val title = when (item) {
        is ContinueWatchingItem.InProgress -> if (item.progress.isFreeboxProgressForDisplay()) {
            freeboxVideoDisplayTitle(item.progress.name.ifBlank { item.progress.videoId }, item.progress.duration)
        } else {
            item.progress.name
        }
        is ContinueWatchingItem.NextUp -> item.info.name
    }

    val detailsFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        detailsFocusRequester.requestFocus()
    }

    NuvioDialog(
        onDismiss = onDismiss,
        title = title,
        subtitle = stringResource(R.string.cw_dialog_subtitle)
    ) {
        Button(
            onClick = onDetails,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(detailsFocusRequester),
            colors = ButtonDefaults.colors(
                containerColor = NuvioColors.BackgroundCard,
                contentColor = NuvioColors.TextPrimary
            )
        ) {
            Text(stringResource(R.string.cw_action_go_to_details))
        }

        if (showPlayManually) {
            Button(
                onClick = onPlayManually,
                colors = ButtonDefaults.colors(
                    containerColor = NuvioColors.BackgroundCard,
                    contentColor = NuvioColors.TextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.play_manually))
            }
        }

        if (item is ContinueWatchingItem.InProgress) {
            Button(
                onClick = onStartFromBeginning,
                colors = ButtonDefaults.colors(
                    containerColor = NuvioColors.BackgroundCard,
                    contentColor = NuvioColors.TextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.cw_action_start_from_beginning))
            }
        }

        onMarkAsUnwatched?.let { markAction ->
            Button(
                onClick = markAction,
                colors = ButtonDefaults.colors(
                    containerColor = NuvioColors.BackgroundCard,
                    contentColor = NuvioColors.TextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.cw_action_mark_unwatched))
            }
        }

        onRenameFreebox?.let { renameAction ->
            Button(
                onClick = renameAction,
                colors = ButtonDefaults.colors(
                    containerColor = NuvioColors.BackgroundCard,
                    contentColor = NuvioColors.TextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.cw_action_rename))
            }
        }

        Button(
            onClick = onRemove,
            colors = ButtonDefaults.colors(
                containerColor = NuvioColors.BackgroundCard,
                contentColor = NuvioColors.TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.cw_action_remove))
        }

        onDeleteFromFreebox?.let { deleteAction ->
            Button(
                onClick = deleteAction,
                colors = ButtonDefaults.colors(
                    containerColor = NuvioColors.BackgroundCard,
                    contentColor = Color(0xFFFF6E6E)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.cw_action_delete_freebox))
            }
        }
    }
}

@Composable
fun FreeboxRenameDialog(
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    NuvioInputDialog(
        title = stringResource(R.string.cw_action_rename),
        subtitle = stringResource(R.string.freebox_rename_dialog_subtitle),
        initialValue = initialName,
        placeholder = initialName,
        onDismiss = onDismiss,
        onConfirm = onConfirm
    )
}

private fun isSelectKey(keyCode: Int): Boolean {
    return keyCode == AndroidKeyEvent.KEYCODE_DPAD_CENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_ENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_NUMPAD_ENTER
}

private fun firstNonBlank(vararg candidates: String?): String? {
    return candidates.firstOrNull { !it.isNullOrBlank() }?.trim()
}

internal fun formatRemainingTime(
    remainingMs: Long,
    strHoursMinLeft: String,
    strMinLeft: String,
    strAlmostDone: String
): String {
    val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(remainingMs)
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    if (totalMinutes <= 0L) return strAlmostDone
    return "reste ${hours}h${minutes.toString().padStart(2, '0')}m"
}





private fun String.compactHoursMinutes(): String =
    replace(Regex("(\\d+)h\\s+(\\d+)m"), "$1h$2m")
private fun com.nuvio.tv.domain.model.WatchProgress.isFreeboxProgressForDisplay(): Boolean {
    return contentId.startsWith("freebox:") || videoId.startsWith("freebox:") || contentType.equals("freebox", ignoreCase = true)
}
