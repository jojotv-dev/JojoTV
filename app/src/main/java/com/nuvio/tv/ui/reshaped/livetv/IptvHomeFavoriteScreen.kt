package com.nuvio.tv.ui.reshaped.livetv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.reshaped.livetv.LiveTvRepository
import com.nuvio.tv.reshaped.livetv.LiveTvSource
import com.nuvio.tv.reshaped.livetv.LiveTvSourceType
import com.nuvio.tv.reshaped.livetv.LiveTvVodCache
import com.nuvio.tv.reshaped.livetv.LiveTvVodItem
import com.nuvio.tv.reshaped.livetv.LiveTvVodPlayback
import com.nuvio.tv.reshaped.livetv.normalizeLiveTvVodTitleForTmdb
import com.nuvio.tv.ui.components.EpisodeBackdropInfo
import com.nuvio.tv.ui.components.EpisodeBackdropLayout
import com.nuvio.tv.ui.components.EpisodeRailItem
import com.nuvio.tv.ui.theme.NuvioTheme
import coil3.compose.AsyncImage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.util.Locale

private data class HomeIptvFavoriteEntry(val source: LiveTvSource, val item: LiveTvVodItem)

/** Opens a cached IPTV home favorite directly instead of routing through the Favorites tabs. */
@Composable
fun IptvHomeFavoriteScreen(
    favoriteKey: String,
    homeLandscapeArtworkUrl: String? = null,
    onBack: () -> Unit,
    onOpenMovieDetails: (Int, String) -> Unit,
    onNavigateToDetail: (String, String, String?) -> Unit = { _, _, _ -> },
    onPlayVod: (LiveTvSource, LiveTvVodItem, LiveTvVodPlayback) -> Unit,
) {
    val liveTvModel: LiveTvScreenModel = hiltViewModel()
    val state by LiveTvRepository.uiState.collectAsStateWithLifecycle()
    val playbackHistory by liveTvModel.watchProgressHistory.collectAsStateWithLifecycle(initialValue = emptyList())
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    var entry by remember(favoriteKey) { mutableStateOf<HomeIptvFavoriteEntry?>(null) }
    var isResolvingFavorite by remember(favoriteKey) { mutableStateOf(true) }
    var episodes by remember(favoriteKey) { mutableStateOf<List<LiveTvVodItem>>(emptyList()) }
    var selectedSeason by rememberSaveable(favoriteKey) { mutableIntStateOf(1) }
    var seriesPresentation by remember(favoriteKey) { mutableStateOf<LiveTvSeriesPresentation?>(null) }
    var isLoadingEpisodes by remember(favoriteKey) { mutableStateOf(false) }
    var hasEpisodeError by remember(favoriteKey) { mutableStateOf(false) }
    var hasPlaybackError by remember(favoriteKey) { mutableStateOf(false) }
    var movieDetailNotFound by remember(favoriteKey) { mutableStateOf(false) }

    LaunchedEffect(Unit) { liveTvModel.ensureLoaded() }
    LaunchedEffect(favoriteKey, state.sources, state.isLoaded) {
        if (!state.isLoaded) return@LaunchedEffect
        isResolvingFavorite = true
        entry = null
        val sources = state.sources.filter { source ->
            (source.type == LiveTvSourceType.Xtream && source.xtream.isConfigured) ||
                (source.type == LiveTvSourceType.Stalker && source.stalker.isConfigured)
        }
        for (source in sources) {
            val movie = LiveTvVodCache.read(context, source.id, "movie")
                .firstOrNull { it.favoriteKey == favoriteKey }
            if (movie != null) {
                entry = HomeIptvFavoriteEntry(source, movie)
                break
            }
            val series = LiveTvVodCache.read(context, source.id, "series")
                .firstOrNull { it.favoriteKey == favoriteKey }
            if (series != null) {
                entry = HomeIptvFavoriteEntry(source, series)
                break
            }
        }
        isResolvingFavorite = false
    }

    LaunchedEffect(entry) {
        val current = entry ?: return@LaunchedEffect
        if (current.item.type != "series") return@LaunchedEffect
        isLoadingEpisodes = true
        hasEpisodeError = false
        seriesPresentation = null
        episodes = emptyList()
        try {
            episodes = loadIptvSeriesEpisodes(current.source, current.item)
        } catch (failure: Exception) {
            if (failure is CancellationException) throw failure
            hasEpisodeError = true
        } finally {
            isLoadingEpisodes = false
        }
    }

    val numberedEpisodes = remember(episodes) { episodes.toNumberedSeriesEpisodes() }
    val availableSeasons = remember(numberedEpisodes) {
        numberedEpisodes.map { it.season }.distinct().sorted()
    }
    LaunchedEffect(entry?.item?.favoriteKey, availableSeasons) {
        val current = entry ?: return@LaunchedEffect
        if (current.item.type != "series" || episodes.isEmpty()) {
            seriesPresentation = null
            return@LaunchedEffect
        }
        seriesPresentation = null
        if (selectedSeason !in availableSeasons && availableSeasons.isNotEmpty()) {
            selectedSeason = availableSeasons.first()
        }
        val resolved = liveTvModel.tmdbSeriesPresentationForVod(
            normalizeLiveTvVodTitleForTmdb(current.item.title),
            availableSeasons,
            onPresentation = { seriesPresentation = it },
        )
        if (resolved != null) seriesPresentation = resolved
    }

    BackHandler(enabled = entry?.item?.type == "series") { onBack() }

    LaunchedEffect(entry, favoriteKey) {
        val current = entry ?: return@LaunchedEffect
        if (current.item.type != "movie") return@LaunchedEffect
        movieDetailNotFound = false
        try {
            val tmdbId = liveTvModel.tmdbMovieIdForVod(current.item.title, current.item.year)
            if (tmdbId != null) {
                onOpenMovieDetails(tmdbId, favoriteKey)
            } else {
                movieDetailNotFound = true
            }
        } catch (failure: Exception) {
            if (failure is CancellationException) throw failure
            movieDetailNotFound = true
        }
    }

    val currentEntry = entry
    if (currentEntry?.item?.type == "series" && episodes.isNotEmpty()) {
        val visibleEpisodes = numberedEpisodes.filter { it.season == selectedSeason }
        val seriesProgress = remember(playbackHistory, currentEntry.item.id) {
            playbackHistory
                .filter {
                    it.contentId == currentEntry.item.id && it.season != null && it.episode != null
                }
                .associateBy { it.season!! to it.episode!! }
        }
        val watchedSeriesEpisodes = remember(seriesProgress) {
            seriesProgress.filterValues { it.isCompleted() }.keys
        }
        val metadata = seriesPresentation?.metadata
        val creatorRole = stringResource(R.string.episode_folder_creator)
        val episodeNumberFormat = stringResource(R.string.episode_folder_episode_number)
        EpisodeBackdropLayout(
            modifier = Modifier.fillMaxSize(),
            info = EpisodeBackdropInfo(
                title = metadata?.localizedTitle?.takeIf(String::isNotBlank)
                    ?: normalizeLiveTvVodTitleForTmdb(currentEntry.item.title),
                tmdbId = seriesPresentation?.tmdbId,
                logoUrl = metadata?.logo,
                backdropUrl = metadata?.backdrop
                    ?: metadata?.poster
                    ?: currentEntry.item.posterUrl.takeIf(String::isNotBlank),
                genres = metadata?.genres.orEmpty(),
                year = metadata?.releaseInfo?.take(4)?.takeIf(String::isNotBlank)
                    ?: currentEntry.item.year.takeIf(String::isNotBlank)?.take(4),
                releaseInfo = metadata?.releaseInfo,
                runtimeMinutes = metadata?.runtimeMinutes,
                cast = metadata?.castMembers.orEmpty().map { it.name },
                creator = metadata?.director.orEmpty(),
                writer = metadata?.writer.orEmpty(),
                people = metadata?.directorMembers.orEmpty().map { person ->
                    if (person.character.isNullOrBlank()) person.copy(character = creatorRole) else person
                } + metadata?.castMembers.orEmpty(),
                creatorMembers = (metadata?.directorMembers.orEmpty() + metadata?.writerMembers.orEmpty()).map { person ->
                    if (person.character.isNullOrBlank()) person.copy(character = creatorRole) else person
                },
                castMembers = metadata?.castMembers.orEmpty(),
                networks = metadata?.networks.orEmpty(),
                productionCompanies = metadata?.productionCompanies.orEmpty(),
                rating = metadata?.rating,
                mdbListRatings = seriesPresentation?.mdbListRatings,
                synopsis = metadata?.description?.takeIf(String::isNotBlank)
                    ?: currentEntry.item.plot.takeIf(String::isNotBlank),
                seasonCount = metadata?.seasonCount?.takeIf {
                    metadata?.hasTmdbSeriesDetails == true && it > 0
                },
                seriesStatus = null,
                currentSeasonComplete = null,
                hasTmdbSeriesDetails = metadata?.hasTmdbSeriesDetails == true,
                replaceStatusWithSeasonCount = true,
                nextEpisodeAirDate = metadata?.nextEpisodeAirDate,
                countries = metadata?.countries.orEmpty(),
                language = metadata?.language,
                ageRating = metadata?.ageRating,
                trailers = metadata?.trailers.orEmpty(),
                recommendations = seriesPresentation?.recommendations.orEmpty(),
            ),
            episodes = visibleEpisodes.map { row ->
                val episodeMetadata = seriesPresentation?.episodes?.get(row.season to row.episode)
                fun artworkIdentity(url: String): String = url.trim()
                    .substringBefore('?')
                    .substringBefore('#')
                    .replace(Regex("/(?:original|w\\d+|h\\d+)(?=/)", RegexOption.IGNORE_CASE), "")
                    .lowercase(Locale.ROOT)

                val seriesArtworkIdentities = sequenceOf(
                    currentEntry.item.posterUrl,
                    metadata?.poster,
                    metadata?.backdrop,
                )
                    .filterNotNull()
                    .filter(String::isNotBlank)
                    .map { it.trim() }
                    .map(::artworkIdentity)
                    .toSet()
                fun episodeArtworkOrNull(url: String?): String? = url
                    ?.takeIf(String::isNotBlank)
                    ?.takeUnless { artwork -> artworkIdentity(artwork) in seriesArtworkIdentities }

                val tmdbEpisodeArtwork = episodeArtworkOrNull(episodeMetadata?.thumbnail)
                val providerEpisodeArtwork = episodeArtworkOrNull(row.item.posterUrl)
                val seriesBackdropFallback = metadata?.backdrop?.takeIf(String::isNotBlank)
                    ?: metadata?.poster?.takeIf(String::isNotBlank)
                    ?: currentEntry.item.posterUrl.takeIf(String::isNotBlank)
                EpisodeRailItem(
                    id = row.item.favoriteKey,
                    title = String.format(Locale.getDefault(), episodeNumberFormat, row.episode),
                    subtitle = normalizeLiveTvVodTitleForTmdb(
                        episodeMetadata?.title?.takeIf(String::isNotBlank) ?: row.item.title,
                    ),
                    imageUrl = tmdbEpisodeArtwork
                        ?: providerEpisodeArtwork
                        ?: homeLandscapeArtworkUrl?.takeIf(String::isNotBlank)
                        ?: seriesBackdropFallback,
                    airDate = episodeMetadata?.airDate,
                    overview = episodeMetadata?.overview,
                    runtimeMinutes = episodeMetadata?.runtimeMinutes,
                    season = row.season,
                    episode = row.episode,
                )
            },
            seasons = availableSeasons,
            selectedSeason = selectedSeason,
            onSeasonSelected = { selectedSeason = it },
            episodeProgressMap = seriesProgress,
            watchedEpisodes = watchedSeriesEpisodes,
            onToggleEpisodeWatched = { railItem ->
                val numberedEpisode = visibleEpisodes.firstOrNull {
                    it.item.favoriteKey == railItem.id
                } ?: return@EpisodeBackdropLayout
                val key = numberedEpisode.season to numberedEpisode.episode
                val progress = seriesProgress[key]
                if (progress?.isCompleted() == true || key in watchedSeriesEpisodes) {
                    progress?.let { liveTvModel.removeIptvHistory(listOf(it)) }
                } else {
                    liveTvModel.markIptvHistoryWatched(
                        listOf(buildIptvEpisodeWatchedProgress(currentEntry.item, numberedEpisode)),
                    )
                }
            },
            onMarkSeasonWatched = { season ->
                val progressToMark = numberedEpisodes
                    .filter { it.season == season }
                    .filterNot { episode ->
                        seriesProgress[episode.season to episode.episode]?.isCompleted() == true
                    }
                    .map { buildIptvEpisodeWatchedProgress(currentEntry.item, it) }
                liveTvModel.markIptvHistoryWatched(progressToMark)
            },
            onMarkSeasonUnwatched = { season ->
                val progressToRemove = seriesProgress
                    .filter { (key, progress) -> key.first == season && progress.isCompleted() }
                    .values
                    .toList()
                liveTvModel.removeIptvHistory(progressToRemove)
            },
            fullSeriesDetail = true,
            richEpisodeCards = true,
            isInLibrary = currentEntry.item.favoriteKey in state.favoriteVodKeys,
            onToggleLibrary = { LiveTvRepository.toggleVodFavorite(currentEntry.item) },
            onRelatedClick = { item ->
                onNavigateToDetail(item.id, item.apiType, item.sourceAddonBaseUrl)
            },
            onEpisodeClick = { railItem ->
                val episode = visibleEpisodes.firstOrNull { it.item.favoriteKey == railItem.id }?.item
                    ?: return@EpisodeBackdropLayout
                scope.launch {
                    runCatching { resolveIptvVod(currentEntry.source, episode) }
                        .onSuccess { onPlayVod(currentEntry.source, episode, it) }
                        .onFailure { failure ->
                            if (failure is CancellationException) throw failure
                            hasPlaybackError = true
                        }
                }
            },
        )
    } else Column(
        modifier = Modifier.fillMaxSize()
            .background(NuvioTheme.colors.Background)
            .padding(horizontal = 30.dp, vertical = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Card(
                onClick = onBack,
                shape = CardDefaults.shape(shape = NuvioTheme.configuredShape(RoundedCornerShape(18.dp))),
                scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(stringResource(R.string.action_back), Modifier.padding(start = 8.dp))
                }
            }
            Column(Modifier.padding(start = 14.dp)) {
                Text(
                    text = entry?.item?.title.orEmpty(),
                    color = NuvioTheme.colors.TextPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
                Text(
                    text = entry?.source?.label.orEmpty(),
                    color = NuvioTheme.colors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                )
            }
        }

        when {
            isResolvingFavorite -> HomeFavoriteMessage(stringResource(R.string.iptv_vod_loading))
            entry == null -> HomeFavoriteMessage(stringResource(R.string.iptv_vod_empty))
            entry?.item?.type == "movie" && !movieDetailNotFound ->
                HomeFavoriteMessage(stringResource(R.string.iptv_vod_loading))
            entry?.item?.type == "movie" && movieDetailNotFound -> {
                val movie = entry!!
                HomeFavoriteMovieFallback(
                    item = movie.item,
                    sourceLabel = movie.source.label,
                    playbackError = hasPlaybackError,
                    onPlay = {
                        scope.launch {
                            hasPlaybackError = false
                            try {
                                onPlayVod(movie.source, movie.item, resolveIptvVod(movie.source, movie.item))
                            } catch (failure: Exception) {
                                if (failure is CancellationException) throw failure
                                hasPlaybackError = true
                            }
                        }
                    },
                )
            }
            isLoadingEpisodes && episodes.isEmpty() ->
                HomeFavoriteMessage(stringResource(R.string.iptv_vod_loading))
            hasEpisodeError && episodes.isEmpty() ->
                HomeFavoriteMessage(stringResource(R.string.iptv_vod_error))
            episodes.isEmpty() -> HomeFavoriteMessage(stringResource(R.string.iptv_vod_empty_episodes))
            else -> VodGrid(
                items = episodes,
                favoriteVodKeys = state.favoriteVodKeys,
                onSelect = { episode ->
                    val current = entry ?: return@VodGrid
                    scope.launch {
                        runCatching { resolveIptvVod(current.source, episode) }
                            .onSuccess { onPlayVod(current.source, episode, it) }
                            .onFailure { failure ->
                                if (failure is CancellationException) throw failure
                                hasPlaybackError = true
                            }
                    }
                },
            )
        }
        if (hasPlaybackError && entry?.item?.type == "series") {
            Text(
                stringResource(R.string.iptv_vod_playback_error),
                Modifier.padding(top = 8.dp),
                color = NuvioTheme.colors.TextSecondary,
            )
        }
    }
}

@Composable
private fun HomeFavoriteMovieFallback(
    item: LiveTvVodItem,
    sourceLabel: String,
    playbackError: Boolean,
    onPlay: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize().padding(top = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(22.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = item.posterUrl.takeIf(String::isNotBlank),
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.width(190.dp).height(280.dp).clip(NuvioTheme.configuredShape(RoundedCornerShape(18.dp))),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = item.title,
                color = NuvioTheme.colors.TextPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
            )
            val facts = listOfNotNull(
                item.year.takeIf(String::isNotBlank),
                sourceLabel.takeIf(String::isNotBlank),
            ).joinToString(" · ")
            if (facts.isNotBlank()) {
                Text(facts, color = NuvioTheme.colors.TextSecondary)
            }
            item.plot.takeIf(String::isNotBlank)?.let { synopsis ->
                Text(
                    synopsis,
                    color = NuvioTheme.colors.TextPrimary,
                    maxLines = 5,
                )
            }
            Card(
                onClick = onPlay,
                shape = CardDefaults.shape(shape = CircleShape),
                scale = CardDefaults.scale(focusedScale = 1.04f, pressedScale = 0.98f),
            ) {
                Text(
                    text = stringResource(R.string.player_post_play_play),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                )
            }
            if (playbackError) {
                Text(
                    stringResource(R.string.iptv_vod_playback_error),
                    color = NuvioTheme.colors.TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun HomeFavoriteMessage(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = NuvioTheme.colors.TextSecondary)
    }
}
