@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.nuvio.tv.ui.screens.home

import android.view.KeyEvent as AndroidKeyEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.State
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.nuvio.tv.R
import com.nuvio.tv.data.freebox.FREEBOX_FOLDER_ID_PREFIX
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.SeriesLifecycleStatus
import com.nuvio.tv.domain.model.normalizeTmdbSeriesStatus
import com.nuvio.tv.ui.components.LightweightCard
import com.nuvio.tv.ui.components.formatContinueWatchingProgressLabel
import com.nuvio.tv.ui.components.shineSweepOnFocus
import com.nuvio.tv.ui.theme.NuvioTheme
import com.nuvio.tv.ui.util.StableMap
import com.nuvio.tv.ui.util.formatHeroRuntime
import com.nuvio.tv.ui.util.localizedGenreLabel
import com.nuvio.tv.ui.util.rememberLongPressKeyTracker
import java.util.Locale
import java.util.concurrent.TimeUnit

private val HERO_EPISODE_LABEL_PATTERN = Regex("""^S(\d+)\s*E(\d+)$""", RegexOption.IGNORE_CASE)

private fun formatHeroEpisodeLabel(label: String): String {
    val match = HERO_EPISODE_LABEL_PATTERN.matchEntire(label.trim()) ?: return label
    val season = match.groupValues[1].trimStart('0').ifEmpty { "0" }.padStart(2, '0')
    val episode = match.groupValues[2].trimStart('0').ifEmpty { "0" }.padStart(2, '0')
    return "S${season}E${episode}"
}

@Composable
internal fun ModernContinueWatchingHero(
    items: List<ModernCarouselItem>,
    selectedIndex: Int,
    focusRequester: FocusRequester,
    height: Dp,
    onFocused: (Int) -> Unit,
    onSelectIndex: (Int) -> Unit,
    onClick: (ContinueWatchingItem) -> Unit,
    onLongClick: (ContinueWatchingItem) -> Unit,
    enrichedPreviews: State<StableMap<String, MetaPreview>>,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    val index = selectedIndex.coerceIn(0, items.lastIndex)
    val selected = items[index]
    val payload = selected.payload as? ModernPayload.ContinueWatching ?: return
    val item = payload.item
    val preview = selected.heroPreview
    val context = LocalContext.current
    val enriched = enrichedPreviews.value[item.contentId()]
    val enrichedIsSeries = enriched?.let { preview ->
        preview.type == com.nuvio.tv.domain.model.ContentType.SERIES ||
            preview.type == com.nuvio.tv.domain.model.ContentType.TV ||
            isSeriesType(preview.apiType) ||
            (preview.seasonCount ?: 0) > 0
    } == true
    val inProgress = item as? ContinueWatchingItem.InProgress
    val progress = inProgress?.progress
    val sourceHomePreview = inProgress?.homeSourcePreview
    val latestHomePreview = sourceHomePreview?.let { enrichedPreviews.value[it.id] } ?: sourceHomePreview
    val isFreeboxVideo = progress?.let {
        it.addonBaseUrl.equals("freebox://nas", ignoreCase = true) ||
            it.contentId.startsWith("freebox:", ignoreCase = true) ||
            it.videoId.startsWith("freebox:", ignoreCase = true)
    } == true || listOfNotNull(sourceHomePreview, latestHomePreview).any {
        it.id.startsWith("freebox:", ignoreCase = true) ||
            it.id.startsWith(FREEBOX_FOLDER_ID_PREFIX, ignoreCase = true)
    }
    val sourcePreviewIsSeries = latestHomePreview?.let { homePreview ->
        homePreview.type == com.nuvio.tv.domain.model.ContentType.SERIES ||
            homePreview.type == com.nuvio.tv.domain.model.ContentType.TV ||
            isSeriesType(homePreview.apiType) || (homePreview.seasonCount ?: 0) > 0
    } == true
    val isSeries = isSeriesType(progress?.contentType) || sourcePreviewIsSeries || enrichedIsSeries ||
        !inProgress?.seriesDescription.isNullOrBlank()
    val seriesPreview = latestHomePreview?.takeIf { isSeries } ?: enriched?.takeIf { enrichedIsSeries }
    val backdrop = preview.backdrop?.takeIf(String::isNotBlank)
        ?: selected.imageUrl?.takeIf(String::isNotBlank)
        ?: preview.poster
    val title = seriesPreview?.name?.takeIf(String::isNotBlank)
        ?: enriched?.name?.takeIf(String::isNotBlank)
        ?: preview.title.ifBlank { selected.title }
    val genres = (seriesPreview?.genres ?: enriched?.genres?.takeIf { it.isNotEmpty() }
        ?: preview.genres.list).take(3)
        .map { localizedGenreLabel(context, it) }
        .joinToString(" • ")
    val year = if (seriesPreview != null) {
        extractYearText(seriesPreview.type, seriesPreview.releaseInfo, seriesPreview.released, showFullDate = false)
    } else {
        enriched?.let { extractYearText(it.type, it.releaseInfo, it.released, showFullDate = false) }
            ?: preview.yearText
    }
    val seriesRuntime = formatHeroRuntime(seriesPreview?.runtime)
    val isFreeboxFolderPreview = seriesPreview?.id
        ?.startsWith(FREEBOX_FOLDER_ID_PREFIX, ignoreCase = true) == true
    val progressRuntime = progress?.duration?.takeIf { it > 0L }?.let { formatDuration(context, it) }
    // The Freebox folder preview may contain a series average; progress.duration is the actual file length.
    val duration = if (isFreeboxVideo) {
        progressRuntime ?: preview.runtimeText
            ?: seriesRuntime.takeUnless { isFreeboxFolderPreview }
    } else {
        seriesRuntime ?: if (seriesPreview != null) null else {
            formatHeroRuntime(enriched?.runtime)
                ?: preview.runtimeText
                ?: progressRuntime
        }
    }
    val durationLabel = duration?.let { value ->
        if (!isFreeboxVideo && isFreeboxFolderPreview) {
            context.getString(R.string.home_average_duration, value)
        } else value
    }
    val hasTmdbSeriesDetails = seriesPreview?.hasTmdbSeriesDetails == true
    val seasonCount = seriesPreview?.seasonCount?.takeIf {
        it > 0 && hasTmdbSeriesDetails
    }
    val seriesStatus = seriesPreview?.status
        ?.takeIf { !isFreeboxVideo && hasTmdbSeriesDetails }
        ?.let(::normalizeTmdbSeriesStatus)
        ?.let { status ->
            when (status) {
                SeriesLifecycleStatus.ENDED -> context.getString(R.string.series_status_ended)
                SeriesLifecycleStatus.IN_PROGRESS -> context.getString(R.string.series_status_continuing)
            }
        }
    val castNames = (if (isFreeboxVideo) emptyList() else seriesPreview?.castMembers.orEmpty()).asSequence()
        .map { it.name.trim() }
        .filter(String::isNotBlank)
        .distinct()
        .take(4)
        .joinToString(", ")
    val directors = (if (isFreeboxVideo) emptyList() else seriesPreview?.director.orEmpty()).asSequence()
        .map(String::trim)
        .filter(String::isNotBlank)
        .distinct()
        .take(2)
        .joinToString(", ")
    val synopsis = if (isSeries) {
        seriesPreview?.description?.takeIf(String::isNotBlank)
            ?: inProgress?.seriesDescription?.takeIf(String::isNotBlank)
    } else {
        enriched?.description?.takeIf(String::isNotBlank) ?: preview.description
    }
    val metadata = listOfNotNull(
        genres.takeIf(String::isNotBlank),
        year?.takeIf(String::isNotBlank),
        durationLabel?.takeIf(String::isNotBlank)
    ).joinToString("  •  ")
    val remainingLabel = remember(progress, context) {
        progress?.let {
            formatContinueWatchingProgressLabel(
                progress = it,
                resumeLabel = context.getString(R.string.cw_resume),
                percentWatchedLabel = context.getString(R.string.cw_percent_watched),
                hoursMinLeftLabel = context.getString(R.string.cw_hours_min_left),
                minLeftLabel = context.getString(R.string.cw_min_left),
                outNowLabel = context.getString(R.string.calendar_out_now)
            )
        }
    }
    val cardShape = NuvioTheme.configuredShape(RoundedCornerShape(26.dp))
    val resumeButtonShape = NuvioTheme.configuredShape(RoundedCornerShape(24.dp))
    val focusedBorder = remember(cardShape) {
        Border(
            border = BorderStroke(2.dp, Color.White),
            shape = cardShape
        )
    }
    var longPressTriggered by remember { mutableStateOf(false) }
    val longPressKeyTracker = rememberLongPressKeyTracker()
    var isHeroFocused by remember { mutableStateOf(false) }
    var isResumeButtonFocused by remember { mutableStateOf(false) }

    LightweightCard(
        onClick = {
            if (longPressTriggered) longPressTriggered = false else onClick(item)
        },
        modifier = modifier
            .height(height)
            .focusProperties { canFocus = false }
            .onPreviewKeyEvent { event ->
                val native = event.nativeKeyEvent
                if (native.action == AndroidKeyEvent.ACTION_DOWN && native.keyCode == AndroidKeyEvent.KEYCODE_MENU) {
                    longPressTriggered = true
                    onLongClick(item)
                    return@onPreviewKeyEvent true
                }
                if (longPressKeyTracker.handle(native, ::isHeroSelectKey) {
                        longPressTriggered = true
                        onLongClick(item)
                    }
                ) {
                    if (native.action == AndroidKeyEvent.ACTION_UP) longPressTriggered = false
                    return@onPreviewKeyEvent true
                }
                if (native.action == AndroidKeyEvent.ACTION_UP && longPressTriggered && isHeroSelectKey(native.keyCode)) {
                    longPressTriggered = false
                    return@onPreviewKeyEvent true
                }
                if (event.type == KeyEventType.KeyDown) {
                    val delta = when (event.key) {
                        Key.DirectionLeft -> -1
                        Key.DirectionRight -> 1
                        else -> 0
                    }
                    if (delta != 0) {
                        onSelectIndex((index + delta + items.size) % items.size)
                        return@onPreviewKeyEvent true
                    }
                }
                false
            },
        shape = cardShape,
        containerColor = Color.Black.copy(alpha = 0.38f),
        focusedBorder = focusedBorder
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(cardShape)
        ) {
            if (!backdrop.isNullOrBlank()) {
                AsyncImage(
                    model = backdrop,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .shineSweepOnFocus(
                            isFocused = isHeroFocused,
                            restartKey = selected.key
                        )
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            0f to Color.Black.copy(alpha = 0.86f),
                            0.55f to Color.Black.copy(alpha = 0.50f),
                            1f to Color.Black.copy(alpha = 0.06f)
                        )
                    )
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.10f),
                            0.48f to Color.Transparent,
                            1f to Color.Black.copy(alpha = 0.75f)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.68f)
                    .padding(horizontal = 34.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val titleLogo = seriesPreview?.logo?.takeIf(String::isNotBlank)
                        ?: enriched?.logo?.takeIf(String::isNotBlank)
                        ?: preview.logo
                    if (!titleLogo.isNullOrBlank()) {
                        AsyncImage(
                            model = titleLogo,
                            contentDescription = title,
                            contentScale = ContentScale.Fit,
                            alignment = Alignment.CenterStart,
                            modifier = Modifier
                                .fillMaxWidth(0.86f)
                                .height(86.dp)
                        )
                    } else {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (metadata.isNotBlank()) {
                        Text(
                            text = metadata,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (seasonCount != null || seriesStatus != null) {
                        Row(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            seasonCount?.let { count ->
                                Text(
                                    text = pluralStringResource(R.plurals.home_season_count, count, count)
                                        .uppercase(Locale.getDefault()),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                            if (seasonCount != null && seriesStatus != null) {
                                Text("|", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.72f))
                            }
                            seriesStatus?.let { status ->
                                Text(
                                    text = status.uppercase(Locale.getDefault()),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                    if (castNames.isNotBlank()) {
                        Text(
                            text = "${context.getString(R.string.pause_cast_label)}: $castNames",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.88f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (directors.isNotBlank()) {
                        Text(
                            text = stringResource(R.string.hero_director, directors),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.88f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    selected.subtitle?.takeIf {
                        !isFreeboxVideo && (isSeries || preview.isSeries) && it.isNotBlank()
                    }?.let { episode ->
                        Text(
                            text = formatHeroEpisodeLabel(episode),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.82f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    synopsis?.let { description ->
                        BoxWithConstraints(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            val maxSynopsisLines = with(LocalDensity.current) {
                                (maxHeight.toPx() / 20.sp.toPx()).toInt().coerceAtLeast(1)
                            }
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                ),
                                color = Color.White,
                                maxLines = maxSynopsisLines,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.width(IntrinsicSize.Max),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .focusProperties { canFocus = true }
                                .onFocusChanged { state ->
                                    isResumeButtonFocused = state.isFocused
                                    isHeroFocused = state.isFocused
                                    if (state.isFocused) {
                                        longPressTriggered = false
                                        onFocused(index)
                                    }
                                }
                                .focusable()
                                .clip(resumeButtonShape)
                                .background(NuvioTheme.colors.Secondary)
                                .then(
                                    if (isResumeButtonFocused) {
                                        Modifier.border(2.dp, Color.White, resumeButtonShape)
                                    } else {
                                        Modifier
                                    }
                                )
                                .padding(horizontal = 22.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "▶", color = NuvioTheme.colors.OnSecondary, fontSize = 18.sp)
                            Text(
                                text = stringResource(R.string.cw_resume),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = NuvioTheme.colors.OnSecondary
                            )
                        }
                        remainingLabel?.let { remaining ->
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = remaining,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                        }
                    }
                    progress?.let {
                        val fraction = it.progressPercentage.coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(NuvioTheme.colors.Secondary.copy(alpha = 0.42f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .fillMaxHeight()
                                    .background(NuvioTheme.colors.Secondary)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 28.dp, bottom = 26.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.indices.forEach { page ->
                    Box(
                        modifier = Modifier
                            .size(if (page == index) 16.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (page == index) NuvioTheme.colors.Secondary
                                else NuvioTheme.colors.Secondary.copy(alpha = 0.62f)
                            )
                    )
                }
            }
        }
    }
}

private fun formatDuration(context: android.content.Context, durationMs: Long): String {
    val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMs).coerceAtLeast(1L)
    val hours = (totalMinutes / 60L).toInt()
    val minutes = (totalMinutes % 60L).toInt()
    return if (hours > 0) {
        context.getString(R.string.home_hero_duration_hours_minutes, hours, minutes)
    } else {
        context.getString(R.string.home_hero_duration_minutes, totalMinutes.toInt())
    }
}

private fun isHeroSelectKey(keyCode: Int): Boolean =
    keyCode == AndroidKeyEvent.KEYCODE_DPAD_CENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_ENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_NUMPAD_ENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_BUTTON_SELECT
