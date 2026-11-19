@file:OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class)

package com.nuvio.tv.ui.components

import com.nuvio.tv.domain.model.SeriesLifecycleStatus
import com.nuvio.tv.domain.model.normalizeTmdbSeriesStatus
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.nuvio.tv.R
import com.nuvio.tv.domain.model.MDBListRatings
import com.nuvio.tv.domain.model.MetaCastMember
import com.nuvio.tv.domain.model.MetaCompany
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.MetaTrailer
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.ui.screens.detail.IptvSeriesDetailsLayout
import com.nuvio.tv.ui.theme.NuvioTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class EpisodeBackdropInfo(
    val title: String,
    val tmdbId: Int? = null,
    val logoUrl: String? = null,
    val backdropUrl: String? = null,
    val genres: List<String> = emptyList(),
    val year: String? = null,
    val releaseInfo: String? = null,
    val runtimeMinutes: Int? = null,
    val cast: List<String> = emptyList(),
    val director: List<String> = emptyList(),
    val creator: List<String> = emptyList(),
    val writer: List<String> = emptyList(),
    val people: List<MetaCastMember> = emptyList(),
    val creatorMembers: List<MetaCastMember> = emptyList(),
    val castMembers: List<MetaCastMember> = emptyList(),
    val networks: List<MetaCompany> = emptyList(),
    val productionCompanies: List<MetaCompany> = emptyList(),
    val rating: Double? = null,
    val mdbListRatings: MDBListRatings? = null,
    val synopsis: String? = null,
    val seasonCount: Int? = null,
    val seriesStatus: String? = null,
    val currentSeasonComplete: Boolean? = null,
    val replaceStatusWithSeasonCount: Boolean = false,
    val nextEpisodeAirDate: String? = null,
    val countries: List<String> = emptyList(),
    val language: String? = null,
    val ageRating: String? = null,
    val trailers: List<MetaTrailer> = emptyList(),
    val recommendations: List<MetaPreview> = emptyList(),
    val hasTmdbSeriesDetails: Boolean = false,
)

data class EpisodeRailItem(
    val id: String,
    val title: String,
    val imageUrl: String? = null,
    val airDate: String? = null,
    val subtitle: String? = null,
    val overview: String? = null,
    val runtimeMinutes: Int? = null,
    val season: Int? = null,
    val episode: Int? = null,
)

@Composable
fun EpisodeBackdropLayout(
    info: EpisodeBackdropInfo,
    episodes: List<EpisodeRailItem>,
    modifier: Modifier = Modifier,
    seasons: List<Int> = emptyList(),
    selectedSeason: Int? = null,
    onSeasonSelected: (Int) -> Unit = {},
    onEpisodeClick: (EpisodeRailItem) -> Unit,
    onEpisodeLongClick: ((EpisodeRailItem) -> Unit)? = null,
    onRelatedClick: (MetaPreview) -> Unit = {},
    fullSeriesDetail: Boolean = false,
    richEpisodeCards: Boolean = false,
    showLibraryAction: Boolean = true,
    isInLibrary: Boolean = false,
    onToggleLibrary: () -> Unit = {},
    showEpisodeCodeBadge: Boolean = true,
    episodeProgressMap: Map<Pair<Int, Int>, WatchProgress> = emptyMap(),
    watchedEpisodes: Set<Pair<Int, Int>> = emptySet(),
    episodeWatchedPendingKeys: Set<String> = emptySet(),
    onToggleEpisodeWatched: (EpisodeRailItem) -> Unit = {},
    onMarkSeasonWatched: (Int) -> Unit = {},
    onMarkSeasonUnwatched: (Int) -> Unit = {},
) {
    if (fullSeriesDetail) {
        val formatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()) }
        val nextEpisodeLine = info.nextEpisodeAirDate.formatUpcomingDate(formatter)?.let { date ->
            stringResource(R.string.episode_folder_next_episode, date)
        }
        IptvSeriesDetailsLayout(
            info = info,
            episodes = episodes,
            seasons = seasons,
            selectedSeason = selectedSeason,
            nextEpisodeLine = nextEpisodeLine,
            onSeasonSelected = onSeasonSelected,
            isInLibrary = isInLibrary,
            onToggleLibrary = onToggleLibrary,
            onRelatedClick = onRelatedClick,
            onEpisodeClick = onEpisodeClick,
            onEpisodeLongClick = onEpisodeLongClick,
            showEpisodeCodeBadge = showEpisodeCodeBadge,
            showLibraryAction = showLibraryAction,
            episodeProgressMap = episodeProgressMap,
            watchedEpisodes = watchedEpisodes,
            episodeWatchedPendingKeys = episodeWatchedPendingKeys,
            onToggleEpisodeWatched = onToggleEpisodeWatched,
            onMarkSeasonWatched = onMarkSeasonWatched,
            onMarkSeasonUnwatched = onMarkSeasonUnwatched,
        )
        return
    }

    val statusLabel = if (!info.hasTmdbSeriesDetails || info.replaceStatusWithSeasonCount) {
        null
    } else {
        when (normalizeTmdbSeriesStatus(info.seriesStatus)) {
            SeriesLifecycleStatus.ENDED -> stringResource(R.string.series_status_ended)
            SeriesLifecycleStatus.IN_PROGRESS -> stringResource(R.string.series_status_continuing)
            null -> null
        }
    }
    val seasonCountLabel = info.seasonCount?.takeIf { info.hasTmdbSeriesDetails && it > 0 }?.let { count ->
        pluralStringResource(R.plurals.home_season_count, count, count)
    }
    val seasonSummary = listOfNotNull(seasonCountLabel, statusLabel).joinToString("  |  ")
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()) }
    val nextEpisodeDate = remember(info.nextEpisodeAirDate, dateFormatter) {
        info.nextEpisodeAirDate.formatUpcomingDate(dateFormatter)
    }
    val showExtendedSections = info.creator.isNotEmpty() || info.people.isNotEmpty() ||
        info.networks.isNotEmpty() || info.productionCompanies.isNotEmpty()
    val showFullSeriesDetail = fullSeriesDetail || showExtendedSections
    val showRichEpisodes = richEpisodeCards
    val detailScrollState = rememberScrollState()
    val heroFocusRequester = remember(info.title) { FocusRequester() }
    var heroFocused by remember { mutableStateOf(false) }
    var pendingHeroFocus by remember { mutableStateOf(false) }
    val episodeIds = remember(episodes) { episodes.map(EpisodeRailItem::id) }
    // Keep the rail limited to real episodes; key handling below wraps focus at each end.
    val episodeFocusIds = episodeIds
    val episodeFocusRequesters = remember(episodeFocusIds) {
        episodeFocusIds.associateWith { FocusRequester() }
    }
    val episodeListState = rememberLazyListState()
    var lastFocusedEpisodeIndex by remember(episodeFocusIds) { mutableIntStateOf(0) }
    var pendingCyclicEpisodeIndex by remember { mutableIntStateOf(-1) }
    LaunchedEffect(pendingCyclicEpisodeIndex, episodeFocusIds) {
        val targetIndex = pendingCyclicEpisodeIndex
        if (targetIndex !in episodeFocusIds.indices) return@LaunchedEffect
        episodeListState.animateScrollToItem(targetIndex)
        val requester = episodeFocusRequesters[episodeFocusIds[targetIndex]]
        repeat(8) {
            withFrameNanos { }
            if (requester != null && runCatching { requester.requestFocus() }.getOrDefault(false)) {
                pendingCyclicEpisodeIndex = -1
                return@LaunchedEffect
            }
        }
        pendingCyclicEpisodeIndex = -1
    }
    LaunchedEffect(episodeIds, episodeFocusRequesters) {
        val firstEpisodeId = episodeIds.firstOrNull() ?: return@LaunchedEffect
        withFrameNanos { }
        runCatching { episodeFocusRequesters[firstEpisodeId]?.requestFocus() }
    }
    LaunchedEffect(pendingHeroFocus) {
        if (!pendingHeroFocus) return@LaunchedEffect
        detailScrollState.animateScrollTo(0)
        repeat(8) {
            withFrameNanos { }
            if (runCatching { heroFocusRequester.requestFocus() }.getOrDefault(false)) {
                pendingHeroFocus = false
                return@LaunchedEffect
            }
        }
        pendingHeroFocus = false
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        AsyncImage(
            model = info.backdropUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.horizontalGradient(
                    0f to Color.Black.copy(alpha = 0.97f),
                    0.45f to Color.Black.copy(alpha = 0.76f),
                    1f to Color.Transparent,
                )
            )
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.14f),
                    0.55f to Color.Transparent,
                    1f to Color.Black.copy(alpha = 0.94f),
                )
            )
        )

        Column(
            modifier = Modifier.fillMaxSize().then(
                if (showFullSeriesDetail) Modifier.verticalScroll(detailScrollState) else Modifier,
            ),
            verticalArrangement = if (showFullSeriesDetail) Arrangement.Top else Arrangement.Bottom,
        ) {
            Box(
                modifier = (if (showFullSeriesDetail) Modifier.fillMaxWidth().height(390.dp)
                else Modifier.weight(1f).fillMaxWidth())
                    .focusRequester(heroFocusRequester)
                    .onFocusChanged { heroFocused = it.isFocused }
                    .focusable()
                    .border(
                        width = if (heroFocused) 2.dp else 0.dp,
                        color = if (heroFocused) MaterialTheme.colorScheme.primary else Color.Transparent,
                    ),
            ) {
            Column(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(0.55f)
                    .padding(start = 38.dp, top = 28.dp, end = 28.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
            ) {
                if (!info.logoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = info.logoUrl,
                        contentDescription = info.title,
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.CenterStart,
                        modifier = Modifier.width(340.dp).height(96.dp),
                    )
                } else {
                    Text(
                        text = info.title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                val metadata = buildList {
                    info.genres.takeIf { it.isNotEmpty() }?.let { add(it.joinToString(" · ")) }
                    info.year?.takeIf(String::isNotBlank)?.let(::add)
                    info.runtimeMinutes?.takeIf { it > 0 }?.let { add("$it min") }
                }.joinToString("  •  ")
                if (metadata.isNotBlank()) {
                    Text(
                        text = metadata,
                        color = Color.White.copy(alpha = 0.92f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                if (info.cast.isNotEmpty() && info.people.isEmpty()) {
                    Text(
                        text = "${stringResource(R.string.episode_folder_cast)}: ${info.cast.take(4).joinToString(", ")}",
                        color = Color.White.copy(alpha = 0.84f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 5.dp),
                    )
                }
                if (info.director.isNotEmpty()) {
                    Text(
                        text = "${stringResource(R.string.episode_folder_director)}: ${info.director.take(2).joinToString(", ")}",
                        color = Color.White.copy(alpha = 0.84f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 3.dp),
                    )
                }
                if (info.creator.isNotEmpty()) {
                    Text(
                        text = "${stringResource(R.string.episode_folder_creator)}: ${info.creator.take(2).joinToString(", ")}",
                        color = Color.White.copy(alpha = 0.84f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 3.dp),
                    )
                }
                info.rating?.takeIf { it > 0 }?.let { rating ->
                    Text(
                        text = "★ ${"%.1f".format(Locale.getDefault(), rating)}",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                info.synopsis?.trim()?.takeIf(String::isNotBlank)?.let { synopsis ->
                    Text(
                        text = synopsis,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 9.dp),
                    )
                }
                if (seasonSummary.isNotBlank()) {
                    Text(
                        text = seasonSummary,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 8.dp)
                            .clip(NuvioTheme.configuredShape(RoundedCornerShape(5.dp)))
                            .border(1.dp, Color.White.copy(alpha = 0.65f), NuvioTheme.configuredShape(RoundedCornerShape(5.dp)))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
                if (nextEpisodeDate != null) {
                    Text(
                        text = stringResource(R.string.episode_folder_next_episode, nextEpisodeDate),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 7.dp),
                    )
                }
            }
        }

        if (seasons.size > 1 || (fullSeriesDetail && seasons.isNotEmpty())) {
            LazyRow(
                contentPadding = PaddingValues(start = 38.dp, end = 28.dp, top = 2.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(seasons, key = { it }) { season ->
                    var focused by remember(season) { mutableStateOf(false) }
                    val selected = season == selectedSeason
                    Card(
                        onClick = { onSeasonSelected(season) },
                        modifier = Modifier.onFocusChanged { focused = it.isFocused },
                        colors = CardDefaults.colors(
                            containerColor = if (selected) NuvioTheme.colors.FocusBackground else NuvioTheme.colors.SurfaceVariant,
                            focusedContainerColor = NuvioTheme.colors.FocusBackground,
                        ),
                        shape = CardDefaults.shape(shape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp))),
                        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
                        border = CardDefaults.border(
                            focusedBorder = androidx.tv.material3.Border(
                                border = NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs),
                                shape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp)),
                            ),
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.episode_folder_season, season),
                            color = NuvioTheme.colors.TextPrimary,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        )
                    }
                }
            }
        }

        LazyRow(
            state = episodeListState,
            modifier = Modifier.fillMaxWidth().height(if (showRichEpisodes) 256.dp else 184.dp).onPreviewKeyEvent { event ->
                if (event.key == Key.DirectionUp && event.type == KeyEventType.KeyDown) {
                    if (!pendingHeroFocus) pendingHeroFocus = true
                    return@onPreviewKeyEvent true
                }
                if (event.type != KeyEventType.KeyDown || episodes.size < 2) return@onPreviewKeyEvent false
                when {
                    event.key == Key.DirectionLeft && lastFocusedEpisodeIndex == 0 -> {
                        pendingCyclicEpisodeIndex = episodes.lastIndex
                        true
                    }
                    event.key == Key.DirectionRight && lastFocusedEpisodeIndex == episodes.lastIndex -> {
                        pendingCyclicEpisodeIndex = 0
                        true
                    }
                    else -> false
                }
            },
            contentPadding = PaddingValues(start = 38.dp, end = 28.dp, top = 19.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            itemsIndexed(episodeFocusIds, key = { _, focusId -> focusId }) { index, focusId ->
                val episode = episodes[index]
                var focused by remember(focusId) { mutableStateOf(false) }
                val shape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp))
                Column(Modifier.width(if (showRichEpisodes) 300.dp else 222.dp)) {
                    Card(
                        onClick = { onEpisodeClick(episode) },
                        onLongClick = onEpisodeLongClick?.let { longClick -> ({ longClick(episode) }) },
                        modifier = Modifier.fillMaxWidth().height(if (showRichEpisodes) 190.dp else 124.dp)
                            .focusRequester(episodeFocusRequesters.getValue(focusId))
                            .onFocusChanged {
                                focused = it.isFocused
                                if (it.isFocused) lastFocusedEpisodeIndex = index
                            },
                        colors = CardDefaults.colors(containerColor = Color.Transparent),
                        shape = CardDefaults.shape(shape = shape),
                        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
                        border = CardDefaults.border(
                            focusedBorder = androidx.tv.material3.Border(
                                border = NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs),
                                shape = shape,
                            ),
                        ),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                                .clip(shape).background(NuvioTheme.colors.SurfaceVariant),
                        ) {
                            if (!episode.imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = episode.imageUrl,
                                    contentDescription = episode.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                            if (showRichEpisodes) {
                                Box(
                                    Modifier.fillMaxSize().background(
                                        Brush.verticalGradient(
                                            0f to Color.Black.copy(alpha = 0.2f),
                                            0.32f to Color.Transparent,
                                            1f to Color.Black.copy(alpha = 0.9f),
                                        ),
                                    ),
                                )
                                Text(
                                    text = episode.title,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.align(Alignment.TopStart).padding(10.dp)
                                        .clip(NuvioTheme.configuredShape(RoundedCornerShape(5.dp)))
                                        .background(Color.Black.copy(alpha = 0.62f))
                                        .padding(horizontal = 7.dp, vertical = 4.dp),
                                )
                                Column(
                                    Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(10.dp),
                                ) {
                                    episode.subtitle?.takeIf(String::isNotBlank)?.let { title ->
                                        Text(
                                            title,
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleSmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                    episode.overview?.takeIf(String::isNotBlank)?.let { overview ->
                                        Text(
                                            overview,
                                            color = Color.White.copy(alpha = 0.92f),
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(top = 3.dp),
                                        )
                                    }
                                    val detailLine = listOfNotNull(
                                        episode.runtimeMinutes?.takeIf { it > 0 }?.let(::formatRuntime),
                                        episode.airDate.formatEpisodeDate(dateFormatter),
                                    ).joinToString("  ·  ")
                                    if (detailLine.isNotBlank()) {
                                        Text(
                                            detailLine,
                                            color = Color.White.copy(alpha = 0.82f),
                                            style = MaterialTheme.typography.labelSmall,
                                            maxLines = 1,
                                            modifier = Modifier.padding(top = 4.dp),
                                        )
                                    }
                                }
                            }
                            if (!showRichEpisodes) {
                                episode.airDate.formatUpcomingDate(dateFormatter)?.let { date ->
                                    Text(
                                        text = date,
                                        color = Color.Black,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)
                                            .clip(NuvioTheme.configuredShape(RoundedCornerShape(5.dp)))
                                            .background(MaterialTheme.colorScheme.primary)
                                            .padding(horizontal = 7.dp, vertical = 4.dp),
                                    )
                                }
                            }
                        }
                    }
                    if (!showRichEpisodes) Text(
                        text = episode.title,
                        color = NuvioTheme.colors.TextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                        fontWeight = if (focused) FontWeight.SemiBold else FontWeight.Normal,
                    )
                    if (!showRichEpisodes) episode.subtitle?.takeIf(String::isNotBlank)?.let { subtitle ->
                        Text(
                            text = subtitle,
                            color = NuvioTheme.colors.TextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }

        if (info.people.isNotEmpty()) {
            DetailSectionTitle(stringResource(R.string.episode_folder_people_title))
            LazyRow(
                contentPadding = PaddingValues(start = 38.dp, end = 28.dp, top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                items(info.people.distinctBy { it.tmdbId ?: it.name }, key = { it.tmdbId ?: it.name }) { person ->
                    Column(
                        modifier = Modifier.width(120.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            Modifier.size(86.dp).clip(CircleShape).background(NuvioTheme.colors.SurfaceVariant),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (!person.photo.isNullOrBlank()) {
                                AsyncImage(
                                    model = person.photo,
                                    contentDescription = person.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                        Text(
                            person.name,
                            color = NuvioTheme.colors.TextPrimary,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        )
                        person.character?.takeIf(String::isNotBlank)?.let { role ->
                            Text(
                                role,
                                color = NuvioTheme.colors.TextSecondary,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
        if (info.networks.isNotEmpty()) {
            CompanyDetailRow(stringResource(R.string.detail_section_network), info.networks)
        }
        if (info.productionCompanies.isNotEmpty()) {
            CompanyDetailRow(stringResource(R.string.detail_section_production), info.productionCompanies)
        }
        }
    }
}

@Composable
private fun DetailSectionTitle(title: String) {
    Text(
        title,
        color = NuvioTheme.colors.TextPrimary,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 38.dp, top = 6.dp),
    )
}

@Composable
private fun CompanyDetailRow(title: String, companies: List<MetaCompany>) {
    DetailSectionTitle(title)
    LazyRow(
        contentPadding = PaddingValues(start = 38.dp, end = 28.dp, top = 8.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(companies, key = { it.tmdbId ?: it.name }) { company ->
            Box(
                Modifier.width(152.dp).height(64.dp).clip(NuvioTheme.configuredShape(RoundedCornerShape(10.dp)))
                    .background(Color.White).padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (!company.logo.isNullOrBlank()) {
                    AsyncImage(
                        model = company.logo,
                        contentDescription = company.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Text(
                        company.name,
                        color = Color.Black,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

private fun String?.formatUpcomingDate(formatter: DateTimeFormatter): String? {
    val date = this?.takeIf(String::isNotBlank)?.let { raw -> runCatching { LocalDate.parse(raw) }.getOrNull() }
        ?: return null
    if (date.isBefore(LocalDate.now())) return null
    return runCatching { date.format(formatter) }.getOrNull()
}

private fun String?.formatEpisodeDate(formatter: DateTimeFormatter): String? {
    val date = this?.takeIf(String::isNotBlank)?.let { raw -> runCatching { LocalDate.parse(raw) }.getOrNull() }
        ?: return null
    return runCatching { date.format(formatter) }.getOrNull()
}

private fun formatRuntime(minutes: Int): String = when {
    minutes >= 60 && minutes % 60 != 0 -> "${minutes / 60} h ${minutes % 60} min"
    minutes >= 60 -> "${minutes / 60} h"
    else -> "$minutes min"
}
