package com.nuvio.tv.ui.screens.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewResponder
import androidx.compose.foundation.relocation.bringIntoViewResponder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.nuvio.tv.R
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.Meta
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.NextToWatch
import com.nuvio.tv.domain.model.PosterShape
import com.nuvio.tv.domain.model.Video
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.ui.components.EpisodeBackdropInfo
import com.nuvio.tv.ui.components.EpisodeRailItem
import com.nuvio.tv.ui.components.MDBListRatingsRow
import com.nuvio.tv.ui.screens.detail.PeopleSectionTab.CAST
import com.nuvio.tv.ui.screens.detail.PeopleSectionTab.MORE_LIKE_THIS
import com.nuvio.tv.ui.screens.detail.PeopleSectionTab.RATINGS
import com.nuvio.tv.ui.screens.detail.PeopleSectionTab.TRAILER
import com.nuvio.tv.ui.theme.NuvioTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** IPTV series use the same hero, season picker, episode cards and detail rows as Nuvio details. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun IptvSeriesDetailsLayout(
    info: EpisodeBackdropInfo,
    episodes: List<EpisodeRailItem>,
    seasons: List<Int>,
    selectedSeason: Int?,
    nextEpisodeLine: String?,
    onSeasonSelected: (Int) -> Unit,
    isInLibrary: Boolean,
    onToggleLibrary: () -> Unit,
    onRelatedClick: (MetaPreview) -> Unit,
    onEpisodeClick: (EpisodeRailItem) -> Unit,
    onEpisodeLongClick: ((EpisodeRailItem) -> Unit)? = null,
    showLibraryAction: Boolean = true,
    showEpisodeCodeBadge: Boolean = true,
    episodeProgressMap: Map<Pair<Int, Int>, WatchProgress> = emptyMap(),
    watchedEpisodes: Set<Pair<Int, Int>> = emptySet(),
    episodeWatchedPendingKeys: Set<String> = emptySet(),
    onToggleEpisodeWatched: (EpisodeRailItem) -> Unit = {},
    onMarkSeasonWatched: (Int) -> Unit = {},
    onMarkSeasonUnwatched: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    val episodeVideos = remember(episodes, selectedSeason) {
        episodes.map { episode ->
            Video(
                id = episode.id,
                title = episode.subtitle?.takeIf(String::isNotBlank) ?: episode.title,
                released = episode.airDate,
                thumbnail = episode.imageUrl,
                streams = emptyList(),
                season = episode.season ?: selectedSeason,
                episode = episode.episode,
                overview = episode.overview,
                runtime = episode.runtimeMinutes,
            )
        }
    }
    val meta = remember(info, episodeVideos) {
        Meta(
            id = info.tmdbId?.let { "tmdb:$it" } ?: "iptv:series:${info.title.trim().lowercase()}",
            type = ContentType.SERIES,
            rawType = "series",
            name = info.title,
            poster = info.backdropUrl,
            posterShape = PosterShape.LANDSCAPE,
            background = info.backdropUrl,
            logo = info.logoUrl,
            description = info.synopsis,
            releaseInfo = info.releaseInfo ?: info.year,
            status = info.seriesStatus?.takeIf { info.hasTmdbSeriesDetails },
            seasonCount = info.seasonCount?.takeIf { info.hasTmdbSeriesDetails && it > 0 },
            currentSeasonComplete = info.currentSeasonComplete,
            hasTmdbSeriesDetails = info.hasTmdbSeriesDetails,
            imdbRating = info.rating?.toFloat(),
            genres = info.genres,
            runtime = info.runtimeMinutes?.let { "$it min" },
            director = info.creator,
            writer = info.writer,
            cast = info.cast,
            castMembers = info.castMembers.ifEmpty { info.people },
            videos = episodeVideos,
            productionCompanies = info.productionCompanies,
            networks = info.networks,
            ageRating = info.ageRating,
            country = info.countries.joinToString(", ").ifBlank { null },
            awards = null,
            language = info.language,
            links = emptyList(),
            trailers = info.trailers,
            trailerYtIds = info.trailers.mapNotNull { it.ytId?.takeIf(String::isNotBlank) },
        )
    }
    val seasonCount = info.seasonCount?.takeIf { info.hasTmdbSeriesDetails && it > 0 }
    val heroMeta = if (info.replaceStatusWithSeasonCount) {
        meta.copy(status = null, seasonCount = seasonCount, currentSeasonComplete = null)
    } else {
        meta.copy(seasonCount = seasonCount, currentSeasonComplete = null)
    }
    val playEpisode = episodeVideos.firstOrNull()
    val playLabel = stringResource(R.string.detail_btn_play)
    val nextEpisodePlayLabel = playEpisode?.let { episode ->
        val season = episode.season
        val number = episode.episode
        if (season != null && number != null) {
            stringResource(R.string.detail_btn_next_episode, season, number)
        } else {
            playLabel
        }
    }
    val nextToWatch = remember(playEpisode, nextEpisodePlayLabel) {
        playEpisode?.let { episode ->
            NextToWatch(
                watchProgress = null,
                isResume = false,
                nextVideoId = episode.id,
                nextSeason = episode.season,
                nextEpisode = episode.episode,
                displayText = nextEpisodePlayLabel ?: playLabel,
            )
        }
    }
    val playFocusRequester = remember(meta.id) { FocusRequester() }
    val heroFallbackFocusRequester = remember(meta.id) { FocusRequester() }
    val heroEntryFocusRequester = if (playEpisode != null) playFocusRequester else heroFallbackFocusRequester
    val seasonFocusRequester = remember(meta.id) { FocusRequester() }
    val episodeRowFocusRequester = remember(meta.id) { FocusRequester() }
    val synopsisFocusRequester = remember(meta.id) { FocusRequester() }
    val episodeFocusRequesters = remember(meta.id) { mutableMapOf<String, FocusRequester>() }
    val castTabFocusRequester = remember(meta.id) { FocusRequester() }
    val ratingsTabFocusRequester = remember(meta.id) { FocusRequester() }
    val similarTabFocusRequester = remember(meta.id) { FocusRequester() }
    val trailerTabFocusRequester = remember(meta.id) { FocusRequester() }
    val castSectionFocusRequester = remember(meta.id) { FocusRequester() }
    val similarSectionFocusRequester = remember(meta.id) { FocusRequester() }
    val trailerSectionFocusRequester = remember(meta.id) { FocusRequester() }
    val networkSectionFocusRequester = remember(meta.id) { FocusRequester() }
    val productionSectionFocusRequester = remember(meta.id) { FocusRequester() }
    val castListState = rememberLazyListState()
    val similarListState = rememberLazyListState()
    val trailerListState = rememberLazyListState()
    val networkListState = rememberLazyListState()
    val productionListState = rememberLazyListState()
    val detailListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    // Match MetaDetailsScreen: focusing a hero action must not scroll the hero
    // out of view to satisfy bring-into-view from the nested button.
    val heroNoScrollResponder = remember {
        object : BringIntoViewResponder {
            override fun calculateRectForParent(localRect: Rect): Rect = Rect.Zero
            override suspend fun bringChildIntoView(localRect: () -> Rect?) = Unit
        }
    }

    val castLabel = stringResource(R.string.detail_tab_cast)
    val ratingsLabel = stringResource(R.string.detail_tab_ratings)
    val similarLabel = stringResource(R.string.detail_tab_more_like_this)
    val trailerLabel = stringResource(R.string.detail_tab_trailer)
    val tmdbSourceLabel = stringResource(R.string.detail_more_like_this_powered_by_tmdb)
    val networkLabel = stringResource(R.string.detail_section_network)
    val productionLabel = stringResource(R.string.detail_section_production)
    val peopleTabs = buildList {
        if (info.creatorMembers.isNotEmpty() || info.castMembers.isNotEmpty() || info.people.isNotEmpty()) {
            add(PeopleTabItem(CAST, castLabel, castTabFocusRequester))
        }
        if (info.mdbListRatings?.isEmpty() == false || info.rating != null) {
            add(PeopleTabItem(RATINGS, ratingsLabel, ratingsTabFocusRequester))
        }
        if (info.recommendations.isNotEmpty()) {
            add(PeopleTabItem(MORE_LIKE_THIS, similarLabel, similarTabFocusRequester))
        }
        if (meta.trailers.any { !it.ytId.isNullOrBlank() }) {
            add(PeopleTabItem(TRAILER, trailerLabel, trailerTabFocusRequester))
        }
    }
    val episodeIds = remember(episodeVideos) { episodeVideos.mapTo(mutableSetOf()) { it.id } }
    LaunchedEffect(episodeIds) {
        episodeFocusRequesters.keys.retainAll(episodeIds)
    }
    var lastFocusedEpisodeId by rememberSaveable(meta.id) { mutableStateOf<String?>(null) }
    var synopsisTruncated by remember(meta.id, meta.description) { mutableStateOf(false) }
    var activeTab by rememberSaveable(meta.id) {
        mutableStateOf(peopleTabs.firstOrNull()?.tab ?: CAST)
    }
    LaunchedEffect(peopleTabs) {
        if (peopleTabs.none { it.tab == activeTab }) {
            activeTab = peopleTabs.firstOrNull()?.tab ?: CAST
        }
    }
    val firstCompanyFocusRequester = when {
        info.networks.isNotEmpty() -> networkSectionFocusRequester
        info.productionCompanies.isNotEmpty() -> productionSectionFocusRequester
        else -> null
    }
    val activePeopleTabFocusRequester = if (peopleTabs.size > 1) {
        peopleTabs.firstOrNull { it.tab == activeTab }?.focusRequester
    } else {
        null
    }
    val activePeopleContentFocusRequester = when (activeTab) {
        CAST -> castSectionFocusRequester.takeIf { peopleTabs.any { it.tab == CAST } }
        MORE_LIKE_THIS -> similarSectionFocusRequester.takeIf { peopleTabs.any { it.tab == MORE_LIKE_THIS } }
        TRAILER -> trailerSectionFocusRequester.takeIf { peopleTabs.any { it.tab == TRAILER } }
        RATINGS -> null
        else -> null
    }
    val episodesPreviousFocusRequester = episodeRowFocusRequester.takeIf { episodeVideos.isNotEmpty() }
        ?: if (seasons.isNotEmpty()) seasonFocusRequester else heroEntryFocusRequester
    val nextAfterEpisodesFocusRequester = activePeopleTabFocusRequester
        ?: activePeopleContentFocusRequester
        ?: firstCompanyFocusRequester
    val seasonDownFocusRequester = episodeRowFocusRequester.takeIf { episodeVideos.isNotEmpty() }
        ?: nextAfterEpisodesFocusRequester
    val firstDetailContentFocusRequester = if (seasons.isNotEmpty()) {
        seasonFocusRequester
    } else {
        episodesPreviousFocusRequester.takeIf { episodeVideos.isNotEmpty() } ?: nextAfterEpisodesFocusRequester
    }
    var nextDetailItemIndex = 1 // The hero is item zero.
    val seasonsItemIndex = if (seasons.isNotEmpty()) nextDetailItemIndex++ else null
    val episodesItemIndex = nextDetailItemIndex++
    val peopleTabsItemIndex = if (peopleTabs.size > 1) nextDetailItemIndex++ else null
    val peopleContentItemIndex = if (peopleTabs.isNotEmpty()) nextDetailItemIndex++ else null
    val networksItemIndex = if (info.networks.isNotEmpty()) nextDetailItemIndex++ else null
    val productionItemIndex = if (info.productionCompanies.isNotEmpty()) nextDetailItemIndex++ else null
    val firstCompanyItemIndex = networksItemIndex ?: productionItemIndex
    val firstDetailItemIndex = when {
        seasons.isNotEmpty() -> seasonsItemIndex
        episodeVideos.isNotEmpty() -> episodesItemIndex
        activePeopleTabFocusRequester != null -> peopleTabsItemIndex
        activePeopleContentFocusRequester != null -> peopleContentItemIndex
        else -> firstCompanyItemIndex
    }
    val heroReturnFocusRequester = if (synopsisTruncated) synopsisFocusRequester else heroEntryFocusRequester

    fun navigateDownToFirstDetail(fromHeroAction: Boolean): Boolean {
        val goToSynopsis = fromHeroAction && synopsisTruncated
        val targetFocusRequester = if (goToSynopsis) {
            synopsisFocusRequester
        } else {
            firstDetailContentFocusRequester
        } ?: return false
        val targetItemIndex = if (goToSynopsis) 0 else firstDetailItemIndex ?: return false
        coroutineScope.launch {
            if (detailListState.firstVisibleItemIndex != targetItemIndex ||
                detailListState.firstVisibleItemScrollOffset != 0
            ) {
                detailListState.animateScrollToItem(targetItemIndex)
            }
            targetFocusRequester.requestFocusAfterFrames()
        }
        return true
    }

    fun navigateUpToHero(): Boolean {
        coroutineScope.launch {
            if (detailListState.firstVisibleItemIndex != 0 || detailListState.firstVisibleItemScrollOffset != 0) {
                detailListState.animateScrollToItem(0)
            }
            heroReturnFocusRequester.requestFocusAfterFrames()
        }
        return true
    }

    fun navigateUpFromEpisodes(): Boolean {
        val targetItemIndex = seasonsItemIndex ?: return navigateUpToHero()
        coroutineScope.launch {
            if (detailListState.firstVisibleItemIndex != targetItemIndex ||
                detailListState.firstVisibleItemScrollOffset != 0
            ) {
                detailListState.animateScrollToItem(targetItemIndex)
            }
            seasonFocusRequester.requestFocusAfterFrames()
        }
        return true
    }
    LaunchedEffect(meta.id, heroEntryFocusRequester) {
        repeat(3) {
            if (heroEntryFocusRequester.requestFocusAfterFrames()) return@LaunchedEffect
            delay(80)
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AsyncImage(
            model = meta.backdropUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.horizontalGradient(
                    0f to Color.Black.copy(alpha = 0.97f),
                    0.48f to Color.Black.copy(alpha = 0.72f),
                    1f to Color.Transparent,
                )
            )
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.08f),
                    0.55f to Color.Transparent,
                    1f to Color.Black.copy(alpha = 0.92f),
                )
            )
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize().focusGroup(),
            contentPadding = PaddingValues(bottom = NuvioTheme.spacing.xxxl),
            verticalArrangement = Arrangement.Top,
            state = detailListState,
        ) {
            item(key = "iptv_series_hero") {
                Box(Modifier.bringIntoViewResponder(heroNoScrollResponder)) {
                    HeroContentSection(
                        meta = heroMeta,
                        nextEpisode = playEpisode,
                        nextToWatch = nextToWatch ?: nextToWatchFallback(playLabel),
                        onPlayClick = { playEpisode?.let { video -> episodes.firstOrNull { it.id == video.id }?.let(onEpisodeClick) } },
                        isPlayEnabled = playEpisode != null,
                        isInLibrary = isInLibrary,
                        onToggleLibrary = onToggleLibrary,
                        onLibraryLongPress = {},
                        isMovieWatched = false,
                        isMovieWatchedPending = false,
                        onToggleMovieWatched = {},
                        showLibraryAction = showLibraryAction,
                        trailerAvailable = meta.trailers.any { !it.ytId.isNullOrBlank() },
                        onTrailerClick = {
                            meta.trailers.firstNotNullOfOrNull { it.ytId?.takeIf(String::isNotBlank) }?.let { ytId ->
                                runCatching {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$ytId")))
                                }
                            }
                        },
                        mdbListRatings = info.mdbListRatings,
                        tmdbRating = info.rating?.toFloat(),
                        playButtonFocusRequester = playFocusRequester,
                        heroEntryFocusRequester = heroEntryFocusRequester,
                        synopsisFocusRequester = synopsisFocusRequester,
                        actionDownFocusRequester = if (synopsisTruncated) synopsisFocusRequester else firstDetailContentFocusRequester,
                        synopsisDownFocusRequester = firstDetailContentFocusRequester,
                        onNavigateDown = { navigateDownToFirstDetail(fromHeroAction = true) },
                        onSynopsisNavigateDown = { navigateDownToFirstDetail(fromHeroAction = false) },
                        onTruncationChanged = { synopsisTruncated = it },
                        nextEpisodeLine = nextEpisodeLine,
                    )
                }
            }
            if (seasons.isNotEmpty()) {
                item(key = "iptv_series_seasons") {
                    SeasonTabs(
                        seasons = seasons,
                        selectedSeason = selectedSeason ?: seasons.first(),
                        onSeasonSelected = onSeasonSelected,
                        selectedTabFocusRequester = seasonFocusRequester,
                        upFocusRequester = heroReturnFocusRequester,
                        downFocusRequester = seasonDownFocusRequester,
                        onNavigateUp = { navigateUpToHero() },
                    )
                }
            }
            item(key = "iptv_series_episodes_${selectedSeason ?: 0}") {
                EpisodesRow(
                    episodes = episodeVideos,
                    episodeArtworkFallbackUrl = info.backdropUrl,
                    episodeProgressMap = episodeProgressMap,
                    watchedEpisodes = watchedEpisodes,
                    episodeWatchedPendingKeys = episodeWatchedPendingKeys,
                    onEpisodeClick = { video ->
                        episodes.firstOrNull { it.id == video.id }?.let(onEpisodeClick)
                    },
                    onToggleEpisodeWatched = { video ->
                        episodes.firstOrNull { it.id == video.id }?.let(onToggleEpisodeWatched)
                    },
                    onMarkSeasonWatched = onMarkSeasonWatched,
                    onMarkSeasonUnwatched = onMarkSeasonUnwatched,
                    isSeasonFullyWatched = episodeVideos.isNotEmpty() && episodeVideos.all { video ->
                        val season = video.season ?: return@all false
                        val episode = video.episode ?: return@all false
                        val key = season to episode
                        episodeProgressMap[key]?.isCompleted() == true || key in watchedEpisodes
                    },
                    selectedSeason = selectedSeason ?: seasons.firstOrNull() ?: 1,
                    upFocusRequester = if (seasons.isNotEmpty()) seasonFocusRequester else heroReturnFocusRequester,
                    onNavigateUp = { navigateUpFromEpisodes() },
                    downFocusRequester = nextAfterEpisodesFocusRequester,
                    episodeFocusRequesters = episodeFocusRequesters,
                    sectionFocusRequester = episodeRowFocusRequester,
                    anchorEpisodeId = lastFocusedEpisodeId,
                    onEpisodeFocused = { lastFocusedEpisodeId = it },
                    windowResetKey = meta.id,
                    onEpisodeLongPress = onEpisodeLongClick?.let { onLongClick ->
                        { video -> episodes.firstOrNull { it.id == video.id }?.let(onLongClick) }
                    },
                    showEpisodeCodeBadge = showEpisodeCodeBadge,
                )
            }
            if (peopleTabs.size > 1) {
                item(key = "iptv_series_people_tabs") {
                    PeopleSectionTabs(
                        activeTab = activeTab,
                        tabs = peopleTabs,
                        upFocusRequester = episodesPreviousFocusRequester,
                        downFocusRequesterForTab = { tab ->
                            when (tab) {
                                CAST -> castSectionFocusRequester
                                RATINGS -> firstCompanyFocusRequester
                                MORE_LIKE_THIS -> similarSectionFocusRequester
                                TRAILER -> trailerSectionFocusRequester
                                else -> null
                            }
                        },
                        onTabFocused = { activeTab = it },
                    )
                }
            }
            if (peopleTabs.isNotEmpty()) {
                item(key = "iptv_series_people_content_${activeTab.name}") {
                    val sectionTitle = if (peopleTabs.size > 1) "" else peopleTabs.first().label
                    Crossfade(targetState = activeTab, label = "iptvSeriesDetailSection") { tab ->
                        when (tab) {
                            CAST -> CastSection(
                                cast = info.castMembers.ifEmpty { info.people },
                                leadingCast = info.creatorMembers,
                                listState = castListState,
                                title = sectionTitle,
                                upFocusRequester = if (peopleTabs.size > 1) castTabFocusRequester
                                    else episodesPreviousFocusRequester,
                                downFocusRequester = firstCompanyFocusRequester,
                                sectionFocusRequester = castSectionFocusRequester,
                                windowResetKey = meta.id,
                            )
                            RATINGS -> {
                                if (info.mdbListRatings?.isEmpty() == false) {
                                    MDBListRatingsRow(
                                        ratings = info.mdbListRatings,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                } else {
                                    Text(
                                        text = "${stringResource(R.string.detail_tab_ratings)}  ${info.rating?.let { "★ ${"%.1f".format(it)}" }.orEmpty()}",
                                        modifier = Modifier.fillMaxWidth(),
                                        color = NuvioTheme.colors.TextPrimary,
                                    )
                                }
                            }
                            MORE_LIKE_THIS -> MoreLikeThisSection(
                                items = info.recommendations,
                                listState = similarListState,
                                sourceLabel = tmdbSourceLabel,
                                upFocusRequester = if (peopleTabs.size > 1) similarTabFocusRequester
                                    else episodesPreviousFocusRequester,
                                downFocusRequester = firstCompanyFocusRequester,
                                sectionFocusRequester = similarSectionFocusRequester,
                                onItemClick = onRelatedClick,
                                windowResetKey = meta.id,
                            )
                            TRAILER -> TrailerSection(
                                trailers = meta.trailers,
                                listState = trailerListState,
                                upFocusRequester = if (peopleTabs.size > 1) trailerTabFocusRequester
                                    else episodesPreviousFocusRequester,
                                downFocusRequester = firstCompanyFocusRequester,
                                sectionFocusRequester = trailerSectionFocusRequester,
                                onTrailerClick = { trailer ->
                                    trailer.ytId?.takeIf(String::isNotBlank)?.let { ytId ->
                                        runCatching {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$ytId")))
                                        }
                                    }
                                },
                                windowResetKey = meta.id,
                            )
                            else -> Unit
                        }
                    }
                }
            }
            if (info.networks.isNotEmpty()) {
                item(key = "iptv_series_networks") {
                    CompanyLogosSection(
                        title = networkLabel,
                        companies = info.networks,
                        listState = networkListState,
                        upFocusRequester = activePeopleContentFocusRequester
                            ?: activePeopleTabFocusRequester
                            ?: episodesPreviousFocusRequester,
                        downFocusRequester = if (info.productionCompanies.isNotEmpty()) {
                            productionSectionFocusRequester
                        } else {
                            null
                        },
                        sectionFocusRequester = networkSectionFocusRequester,
                        windowResetKey = meta.id,
                    )
                }
            }
            if (info.productionCompanies.isNotEmpty()) {
                item(key = "iptv_series_production") {
                    CompanyLogosSection(
                        title = productionLabel,
                        companies = info.productionCompanies,
                        listState = productionListState,
                        upFocusRequester = if (info.networks.isNotEmpty()) {
                            networkSectionFocusRequester
                        } else {
                            activePeopleContentFocusRequester
                                ?: activePeopleTabFocusRequester
                                ?: episodesPreviousFocusRequester
                        },
                        sectionFocusRequester = productionSectionFocusRequester,
                        windowResetKey = meta.id,
                    )
                }
            }
        }
    }
}

private fun nextToWatchFallback(playLabel: String): NextToWatch = NextToWatch(
    watchProgress = null,
    isResume = false,
    nextVideoId = null,
    nextSeason = null,
    nextEpisode = null,
    displayText = playLabel,
)
