package com.nuvio.tv.ui.reshaped.livetv

import com.nuvio.tv.reshaped.livetv.lists.ListChannels
import com.nuvio.tv.reshaped.livetv.lists.isListChannelUrl
import com.nuvio.tv.reshaped.livetv.lists.ListChannelUnavailableException
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.R
import com.nuvio.tv.core.profile.ProfileManager
import com.nuvio.tv.core.build.AppFeaturePolicy
import com.nuvio.tv.core.tmdb.TmdbEnrichment
import com.nuvio.tv.core.tmdb.TmdbEpisodeEnrichment
import com.nuvio.tv.core.tmdb.TmdbExactTitleMatch
import com.nuvio.tv.core.tmdb.TmdbMetadataService
import com.nuvio.tv.core.tmdb.TmdbService
import com.nuvio.tv.data.local.TmdbSettingsDataStore
import com.nuvio.tv.data.trailer.TrailerPlaybackSource
import com.nuvio.tv.data.trailer.TrailerService
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.Meta
import com.nuvio.tv.domain.model.MDBListRatings
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.PosterShape
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.domain.repository.WatchProgressRepository
import com.nuvio.tv.data.repository.MDBListRepository
import com.nuvio.tv.reshaped.livetv.LiveTvChannel
import com.nuvio.tv.reshaped.livetv.LiveTvClock
import com.nuvio.tv.reshaped.livetv.LiveTvProgramme
import com.nuvio.tv.reshaped.livetv.LiveTvRecentChannel
import com.nuvio.tv.reshaped.livetv.LiveTvError
import com.nuvio.tv.reshaped.livetv.LiveTvPlaybackRegistry
import com.nuvio.tv.reshaped.livetv.LiveTvRepository
import com.nuvio.tv.reshaped.livetv.LiveTvSource
import com.nuvio.tv.reshaped.livetv.LiveTvUiState
import com.nuvio.tv.reshaped.livetv.LiveTvVodItem
import com.nuvio.tv.reshaped.livetv.normalizeLiveTvVodTitleForTmdb
import com.nuvio.tv.ui.navigation.Screen
import com.nuvio.tv.ui.screens.player.PlayerMediaSourceFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Keeps [LiveTvRepository] on the active profile. The list itself lives in the repository. */
@HiltViewModel
class LiveTvScreenModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileManager: ProfileManager,
    private val tmdbService: TmdbService,
    private val tmdbMetadataService: TmdbMetadataService,
    private val trailerService: TrailerService,
    private val tmdbSettingsDataStore: TmdbSettingsDataStore,
    private val watchProgressRepository: WatchProgressRepository,
    private val mdbListRepository: MDBListRepository,
) : ViewModel() {
    private val vodPosterCache = java.util.concurrent.ConcurrentHashMap<String, String>()
    private val vodPosterMisses = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()
    private val vodPresentationCache = java.util.concurrent.ConcurrentHashMap<String, LiveTvVodPresentation>()
    private val vodPresentationMisses = java.util.concurrent.ConcurrentHashMap<String, Long>()

    suspend fun tmdbPosterForVod(title: String, type: String, year: String? = null): String? {
        val normalizedType = if (type == "episode") "series" else type
        val normalizedTitle = normalizeLiveTvVodTitleForTmdb(title).takeIf(String::isNotBlank) ?: return null
        val normalizedYear = year?.take(4)?.toIntOrNull()
        val key = "$normalizedType:${normalizedTitle.lowercase()}:$normalizedYear"
        vodPosterCache[key]?.let { return it }
        if (key in vodPosterMisses) return null
        val settings = tmdbSettingsDataStore.settings.value
        if (!settings.enabled || !settings.useArtwork) return null
        val poster = tmdbService.posterForExactTitle(normalizedTitle, normalizedType, settings.language, normalizedYear)
        if (poster == null) vodPosterMisses += key else vodPosterCache[key] = poster
        return poster
    }

    suspend fun tmdbVodPresentationForVod(
        title: String,
        type: String,
        year: String? = null,
    ): LiveTvVodPresentation? {
        val normalizedType = when (type) {
            "movie" -> "movie"
            "series" -> "series"
            else -> return null
        }
        val expectedContentType = if (normalizedType == "movie") ContentType.MOVIE else ContentType.SERIES
        val normalizedTitle = normalizeLiveTvVodTitleForTmdb(title)
            .takeIf(String::isNotBlank) ?: return null
        val normalizedYear = year?.take(4)?.toIntOrNull()
        val settings = tmdbSettingsDataStore.settings.first()
        if (!settings.enabled) return null

        val cacheKey = "${expectedContentType.name}:${normalizedTitle.lowercase()}:$normalizedYear:${settings.language}"
        vodPresentationCache[cacheKey]?.let { return it }
        vodPresentationMisses[cacheKey]?.let { missedAt ->
            if (System.currentTimeMillis() - missedAt < 15 * 60 * 1000L) return null
            vodPresentationMisses.remove(cacheKey, missedAt)
        }

        val match = try {
            tmdbService.findExactTitleMatch(
                title = normalizedTitle,
                mediaType = normalizedType,
                language = settings.language,
                year = normalizedYear,
            )?.takeIf { it.contentType == expectedContentType }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            return null
        } ?: run {
            vodPresentationMisses[cacheKey] = System.currentTimeMillis()
            return null
        }

        match.posterPath?.takeIf { it.startsWith('/') }?.let { posterPath ->
            val posterKey = "$normalizedType:${normalizedTitle.lowercase()}:$normalizedYear"
            vodPosterCache.putIfAbsent(posterKey, "https://image.tmdb.org/t/p/w500$posterPath")
            vodPosterMisses.remove(posterKey)
        }

        val metadata = try {
            tmdbMetadataService.fetchEnrichment(
                tmdbId = match.tmdbId.toString(),
                contentType = expectedContentType,
                language = settings.language,
            )
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            null
        }
        val presentation = LiveTvVodPresentation(match = match, metadata = metadata)
        if (metadata != null) vodPresentationCache[cacheKey] = presentation
        return presentation
    }

    suspend fun tmdbMovieIdForVod(title: String, year: String? = null): Int? {
        val normalizedTitle = normalizeLiveTvVodTitleForTmdb(title)
            .takeIf(String::isNotBlank) ?: return null
        val settings = tmdbSettingsDataStore.settings.first()
        if (!settings.enabled) return null
        return tmdbService.findExactTitleMatch(
            title = normalizedTitle,
            mediaType = "movie",
            language = settings.language,
            year = year?.take(4)?.toIntOrNull(),
        )?.takeIf { it.contentType == ContentType.MOVIE }?.tmdbId
    }

    suspend fun resolveVodTrailerPlaybackSource(
        item: LiveTvVodItem,
        presentation: LiveTvVodPresentation,
    ): TrailerPlaybackSource? {
        if (!AppFeaturePolicy.inAppTrailerPlaybackEnabled) return null

        val title = normalizeLiveTvVodTitleForTmdb(item.title)
            .takeIf(String::isNotBlank) ?: return null
        val year = item.year.take(4).takeIf(String::isNotBlank)
        val source = trailerService.getTrailerPlaybackSource(
            title = title,
            year = year,
            tmdbId = presentation.match.tmdbId.toString(),
            type = item.type,
        )
        source?.takeIf { it.videoUrl.isNotBlank() }?.let { return it }

        val fallbackYtId = presentation.metadata?.trailers.orEmpty()
            .firstNotNullOfOrNull { trailer -> trailer.ytId?.trim()?.takeIf(String::isNotBlank) }
        return fallbackYtId?.let { ytId ->
            trailerService.getTrailerPlaybackSourceFromYouTubeUrl(
                youtubeUrl = "https://www.youtube.com/watch?v=$ytId",
                title = title,
                year = year,
            )
        }
    }

    suspend fun tmdbSeriesPresentationForVod(
        title: String,
        seasons: List<Int>,
        onPresentation: (LiveTvSeriesPresentation) -> Unit = {},
    ): LiveTvSeriesPresentation? {
        val settings = tmdbSettingsDataStore.settings.first()
        if (!settings.enabled || title.isBlank()) return null
        val match = tmdbService.findExactTitleMatch(
            title = title,
            mediaType = "series",
            language = settings.language,
        )?.takeIf { it.contentType == ContentType.SERIES } ?: return null
        val metadata = tmdbMetadataService.fetchEnrichment(
            tmdbId = match.tmdbId.toString(),
            contentType = ContentType.SERIES,
            language = settings.language,
        )
        val partial = LiveTvSeriesPresentation(tmdbId = match.tmdbId, metadata = metadata)
        onPresentation(partial)

        val ratingsMeta = Meta(
            id = "tmdb:${match.tmdbId}",
            type = ContentType.SERIES,
            rawType = "series",
            name = metadata?.localizedTitle?.takeIf(String::isNotBlank) ?: title,
            poster = metadata?.poster,
            posterShape = PosterShape.LANDSCAPE,
            background = metadata?.backdrop,
            logo = metadata?.logo,
            description = metadata?.description,
            releaseInfo = metadata?.releaseInfo,
            status = metadata?.status,
            imdbRating = metadata?.rating?.toFloat(),
            genres = metadata?.genres.orEmpty(),
            runtime = metadata?.runtimeMinutes?.let { "$it min" },
            director = metadata?.director.orEmpty(),
            writer = metadata?.writer.orEmpty(),
            cast = metadata?.castMembers.orEmpty().map { it.name },
            castMembers = metadata?.castMembers.orEmpty(),
            videos = emptyList(),
            productionCompanies = metadata?.productionCompanies.orEmpty(),
            networks = metadata?.networks.orEmpty(),
            ageRating = metadata?.ageRating,
            country = metadata?.countries.orEmpty().joinToString(", ").ifBlank { null },
            awards = null,
            language = metadata?.language,
            links = emptyList(),
            trailers = metadata?.trailers.orEmpty(),
            trailerYtIds = metadata?.trailers.orEmpty().mapNotNull { it.ytId },
        )

        return coroutineScope {
            val seasonNumbers = seasons.filter { it > 0 }.distinct()
            val episodesDeferred = async {
                if (seasonNumbers.isEmpty()) emptyMap()
                else try {
                    tmdbMetadataService.fetchEpisodeEnrichment(
                        tmdbId = match.tmdbId.toString(),
                        seasonNumbers = seasonNumbers,
                        language = settings.language,
                    )
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    emptyMap()
                }
            }
            val ratingsDeferred = async {
                try {
                    mdbListRepository.getRatingsForMeta(
                        meta = ratingsMeta,
                        fallbackItemId = "tmdb:${match.tmdbId}",
                        fallbackItemType = "series",
                    )?.ratings
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    null
                }
            }
            val recommendationsDeferred = async {
                try {
                    tmdbMetadataService.fetchMoreLikeThis(
                        tmdbId = match.tmdbId.toString(),
                        contentType = ContentType.SERIES,
                        language = settings.language,
                        maxItems = 12,
                    )
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    emptyList()
                }
            }
            LiveTvSeriesPresentation(
                tmdbId = match.tmdbId,
                metadata = metadata,
                episodes = episodesDeferred.await(),
                mdbListRatings = ratingsDeferred.await(),
                recommendations = recommendationsDeferred.await(),
            ).also(onPresentation)
        }
    }

    init {
        viewModelScope.launch {
            profileManager.activeProfileId.collectLatest { profileId ->
                LiveTvRepository.ensureLoaded(context, profileId)
            }
        }
        // Live TV let go of its channels while unused: this model, kept on a saved back stack
        // entry, must not keep them alive. The screen filters again when it opens.
        viewModelScope.launch {
            LiveTvRepository.releases.drop(1).collect {
                filteredFor = null
                visibleChannels = emptyList()
            }
        }
    }

    val profileId: Int get() = profileManager.activeProfileId.value

    val watchProgressHistory get() = watchProgressRepository.allProgress

    fun removeIptvHistory(entries: List<WatchProgress>) {
        if (entries.isEmpty()) return
        viewModelScope.launch {
            entries.forEach { entry ->
                watchProgressRepository.removeFromHistory(
                    contentId = entry.contentId,
                    videoId = entry.videoId.takeIf(String::isNotBlank),
                    season = entry.season,
                    episode = entry.episode,
                )
            }
        }
    }

    fun markIptvHistoryWatched(entries: List<WatchProgress>) {
        if (entries.isEmpty()) return
        viewModelScope.launch {
            watchProgressRepository.markAsCompletedBatch(entries)
        }
    }

    /**
     * Called as the screen opens: Live TV lets go of its channels after a while unused, and this
     * model can outlive that (a saved back stack entry), so it loads them again when needed.
     */
    fun ensureLoaded() {
        if (LiveTvRepository.ensureLoaded(context, profileId)) {
            filteredFor = null
            visibleChannels = emptyList()
        }
    }

    /**
     * The filtered list, kept here so coming back from the player shows it at once (no empty
     * frame, no refilter) with the list where it was.
     */
    var visibleChannels by mutableStateOf<List<LiveTvChannel>>(emptyList())
        private set
    private var filteredFor: LiveTvFilterInput? = null

    fun isFilteredFor(input: LiveTvFilterInput): Boolean = filteredFor?.sameAs(input) == true

    fun setVisible(input: LiveTvFilterInput, channels: List<LiveTvChannel>) {
        filteredFor = input
        visibleChannels = channels
    }

    /** Set when a channel starts playing: on return, focus goes back to the channel last watched. */
    var restoreFocusOnReturn = false

    /** Sources whose categories are folded away in the category column. */
    val collapsedSources = androidx.compose.runtime.mutableStateMapOf<String, Boolean>()

    /** The category column's keys by item index, for bringing the selected one into view. */
    var categoryKeys: List<String> = emptyList()

    private var sectionsFor: List<Any>? = null
    private var sections: LiveTvSections? = null

    /** The sections already worked out for these inputs, or null. */
    internal fun sourceSections(state: LiveTvUiState, visibleGroups: List<String>): LiveTvSections? =
        sections.takeIf { sectionsFor?.sameAs(sectionInputs(state, visibleGroups)) == true }

    /** Works out the sections (slow for big lists: off the main thread) and keeps them. */
    internal fun computeSourceSections(state: LiveTvUiState, visibleGroups: List<String>): LiveTvSections {
        val inputs = sectionInputs(state, visibleGroups)
        val counts = HashMap<String, Int>()
        val groupsBySource = HashMap<String, HashSet<String>>()
        val shownGroups = visibleGroups.toHashSet()
        var total = 0
        state.channels.forEach { channel ->
            if (channel.group !in shownGroups || channel.hideKey in state.hiddenChannelKeys) return@forEach
            total++
            counts[channel.sourceId] = (counts[channel.sourceId] ?: 0) + 1
            groupsBySource.getOrPut(channel.sourceId) { HashSet() }.add(channel.group)
        }
        val bySource = state.sources.map { source ->
            val own = groupsBySource[source.id].orEmpty()
            LiveTvSourceSection(source, counts[source.id] ?: 0, visibleGroups.filter { it in own })
        }
        return LiveTvSections(total, bySource).also {
            sectionsFor = inputs
            sections = it
        }
    }

    private fun sectionInputs(state: LiveTvUiState, visibleGroups: List<String>): List<Any> =
        listOf(state.channels, visibleGroups, state.hiddenChannelKeys, state.sources)

    private fun List<Any>.sameAs(other: List<Any>): Boolean = size == other.size && indices.all { this[it] === other[it] }
}

data class LiveTvSeriesPresentation(
    val metadata: TmdbEnrichment?,
    val episodes: Map<Pair<Int, Int>, TmdbEpisodeEnrichment> = emptyMap(),
    val tmdbId: Int? = null,
    val mdbListRatings: MDBListRatings? = null,
    val recommendations: List<MetaPreview> = emptyList(),
)

data class LiveTvVodPresentation(
    val match: TmdbExactTitleMatch,
    val metadata: TmdbEnrichment?,
)

/** The channels shown in all, and each source's heading: its shown channels and categories, in order. */
internal class LiveTvSections(val total: Int, val sources: List<LiveTvSourceSection>)

internal class LiveTvSourceSection(val source: LiveTvSource, val channelCount: Int, val groups: List<String>)

/** What the visible list was filtered from; lists are compared by identity, so this is cheap. */
class LiveTvFilterInput(
    val channels: List<LiveTvChannel>,
    val favoriteUrls: Set<String>,
    val channelHistory: List<LiveTvRecentChannel>,
    val hiddenGroups: Set<String>,
    val hiddenChannels: Set<Long>,
    val filterKey: String,
    val customLists: List<com.nuvio.tv.reshaped.livetv.LiveTvCustomList>,
) {
    fun sameAs(other: LiveTvFilterInput): Boolean =
        channels === other.channels && favoriteUrls === other.favoriteUrls && channelHistory === other.channelHistory &&
            hiddenGroups === other.hiddenGroups &&
            hiddenChannels === other.hiddenChannels &&
            filterKey == other.filterKey && customLists === other.customLists
}

/**
 * The player route for a Live TV channel: resolves its playable link, and plays it as Stremio
 * type "channel" with no content id, so Nuvio's player treats it as live and saves no progress.
 */
internal suspend fun liveTvPlayerRoute(channel: LiveTvChannel, profileId: Int): String? {
    // A channel made from lists has no stream until its addons give one for what is on now.
    val playback = try {
        LiveTvRepository.prepareForPlayback(channel)
    } catch (unavailable: ListChannelUnavailableException) {
        return null
    }
    val isListChannel = channel.streamUrl.isListChannelUrl()
    val slot = if (isListChannel) ListChannels.openedSlot(channel.streamUrl) else null
    val programmeTitle = slot?.title
        ?: LiveTvRepository.uiState.value.currentProgrammes[channel.guideKey]?.title
        ?: channel.name

    // The player keys on the URL it plays, which drops a user:password@ part into a header.
    val playerUrl = PlayerMediaSourceFactory.normalizePlaybackRequest(playback.streamUrl, playback.headers).url
    LiveTvPlaybackRegistry.register(playerUrl, listUrl = channel.streamUrl, catchup = isListChannel)
    return Screen.Player.createRoute(
        streamUrl = playback.streamUrl,
        title = programmeTitle,
        streamName = channel.name,
        headers = playback.headers,
        contentType = LIVE_TV_CONTENT_TYPE,
        contentId = null,
        contentName = slot?.title,
        poster = slot?.image,
        videoId = slot?.videoId,
        season = slot?.season,
        episode = slot?.episode,
        episodeTitle = slot?.title,
        logo = LiveTvRepository.uiState.value.logoFor(channel),
        addonName = LIVE_TV_ADDON_NAME,
        streamDescription = (liveTvGroupName(channel.group, LiveTvRepository.uiState.value.groupNames) ?: channel.group).takeIf(String::isNotBlank),
        profileId = profileId,
    )
}

/**
 * The player route for a past programme of [channel] (catch-up), with no content id so no
 * progress is saved; null when the provider does not keep it. It is routed as a channel, like
 * live ones, so ▲▼ and Back to live channels in the same player stay live; the player's
 * timeline treats catch-up links as seekable (see [LiveTvPlaybackRegistry.isCatchup]).
 */
internal suspend fun liveTvCatchupRoute(channel: LiveTvChannel, programme: LiveTvProgramme, profileId: Int): String? {
    val playback = LiveTvRepository.catchupChannel(channel, programme) ?: return null
    val playerUrl = PlayerMediaSourceFactory.normalizePlaybackRequest(playback.streamUrl, playback.headers).url
    LiveTvPlaybackRegistry.register(playerUrl, listUrl = channel.streamUrl, catchup = true)
    return Screen.Player.createRoute(
        streamUrl = playback.streamUrl,
        title = programme.title,
        streamName = channel.name,
        headers = playback.headers,
        contentType = LIVE_TV_CONTENT_TYPE,
        logo = LiveTvRepository.uiState.value.logoFor(channel),
        addonName = LIVE_TV_ADDON_NAME,
        streamDescription = "${channel.name}  ·  ${LiveTvClock.formatSpan(programme)}",
        profileId = profileId,
    )
}

internal const val LIVE_TV_CONTENT_TYPE = "channel"
internal const val LIVE_TV_ADDON_NAME = "Live TV"

internal fun LiveTvError.message(context: Context): String = context.getString(
    when (this) {
        LiveTvError.InvalidUrl -> R.string.live_tv_error_invalid_url
        LiveTvError.NoChannels -> R.string.live_tv_error_no_channels
        LiveTvError.LoadFailed -> R.string.live_tv_error_load_failed
        LiveTvError.FileEmpty -> R.string.live_tv_error_file_empty
        LiveTvError.FileNoChannels -> R.string.live_tv_error_file_no_channels
        LiveTvError.StalkerRequired -> R.string.live_tv_error_stalker_required
        LiveTvError.StalkerInvalidUrl -> R.string.live_tv_error_stalker_invalid_url
        LiveTvError.StalkerNoChannels -> R.string.live_tv_error_stalker_no_channels
        LiveTvError.StalkerFailed -> R.string.live_tv_error_stalker_failed
        LiveTvError.StalkerToken -> R.string.live_tv_error_stalker_token
        LiveTvError.StalkerIncomplete -> R.string.live_tv_error_stalker_incomplete
        LiveTvError.XtreamRequired -> R.string.live_tv_error_xtream_required
        LiveTvError.XtreamInvalidUrl -> R.string.live_tv_error_xtream_invalid_url
        LiveTvError.XtreamNoChannels -> R.string.live_tv_error_xtream_no_channels
        LiveTvError.XtreamFailed -> R.string.live_tv_error_xtream_failed
        LiveTvError.GuideInvalidUrl -> R.string.live_tv_error_guide_url
    },
)
