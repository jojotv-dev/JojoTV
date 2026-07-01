package com.nuvio.tv.ui.screens.iptv.tivi

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.BuildConfig
import com.nuvio.tv.core.tmdb.TmdbService
import com.nuvio.tv.data.local.IptvSettingsDataStore
import com.nuvio.tv.domain.model.IptvPosterSize
import com.streamvault.domain.model.Category
import com.streamvault.domain.model.Channel
import com.streamvault.domain.model.ContentType
import com.streamvault.domain.model.Movie
import com.streamvault.domain.model.Program
import com.streamvault.domain.model.Provider
import com.streamvault.domain.model.Result
import com.streamvault.domain.model.Series
import com.streamvault.domain.repository.ChannelRepository
import com.streamvault.domain.repository.EpgRepository
import com.streamvault.domain.repository.FavoriteRepository
import com.streamvault.domain.repository.MovieRepository
import com.streamvault.domain.repository.ProviderRepository
import com.streamvault.domain.repository.SeriesRepository
import com.streamvault.data.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Onglets sidebar interne ──────────────────────────────────────────────────

enum class TiviTab(val label: String, val contentType: ContentType) {
    LIVE("Live TV", ContentType.LIVE),
    MOVIES("Films", ContentType.MOVIE),
    SERIES("Séries", ContentType.SERIES),
}

// ── Data classes UI ──────────────────────────────────────────────────────────

data class TiviGroup(val id: Long, val name: String)

data class TiviProviderNode(
    val provider: Provider,
    val groups: List<TiviGroup> = emptyList(),
    val isExpanded: Boolean = false,
)

data class TiviEpgRow(val channel: Channel, val programs: List<Program>)

data class TiviSeriesEpisodePlayback(
    val series: Series,
    val streamUrl: String,
    val title: String,
    val headers: Map<String, String>,
    val contentId: String,
    val videoId: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val season: Int?,
    val episode: Int?,
    val episodeTitle: String?,
)

sealed class TiviContent {
    data class LiveContent(
        val channels: List<Channel> = emptyList(),
        val focusedChannel: Channel? = null,
        val currentProgram: Program? = null,
        val nextProgram: Program? = null,
        val epgRows: List<TiviEpgRow> = emptyList(),
        val isLoading: Boolean = false,
    ) : TiviContent()
    data class MoviesContent(
        val movies: List<Movie> = emptyList(),
        val isLoading: Boolean = false,
    ) : TiviContent()
    data class SeriesContent(
        val series: List<Series> = emptyList(),
        val isLoading: Boolean = false,
    ) : TiviContent()
    object Empty : TiviContent()
}

data class TiviUiState(
    val selectedTab: TiviTab = TiviTab.LIVE,
    val providerNodes: List<TiviProviderNode> = emptyList(),
    val selectedGroupId: Long? = null,
    val selectedProviderId: Long? = null,
    val isLoadingProviders: Boolean = true,
    val content: TiviContent = TiviContent.Empty,
    val posterSize: IptvPosterSize = IptvPosterSize.DEFAULT,
)

enum class TiviVisibilityDialogKind { PROVIDER_CATEGORIES, GROUP_ITEMS }

data class TiviVisibilityItem(
    val id: Long,
    val label: String,
    val isVisible: Boolean,
)

data class TiviVisibilityDialogState(
    val kind: TiviVisibilityDialogKind,
    val providerId: Long,
    val groupId: Long?,
    val contentType: ContentType,
    val title: String,
    val items: List<TiviVisibilityItem>,
)

// ── ViewModel ────────────────────────────────────────────────────────────────

@HiltViewModel
class IptvTiviViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val channelRepository: ChannelRepository,
    private val epgRepository: EpgRepository,
    private val favoriteRepository: FavoriteRepository,
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository,
    private val preferencesRepository: PreferencesRepository,
    private val iptvSettingsDataStore: IptvSettingsDataStore,
    private val tmdbService: TmdbService,
) : ViewModel() {

    private companion object {
        const val PERF_TAG = "TiviPerf"
        const val LIVE_CONTENT_CACHE_SIZE = 8
    }

    private val _uiState = MutableStateFlow(TiviUiState())
    val uiState: StateFlow<TiviUiState> = _uiState.asStateFlow()

    private var providerFocusJob: Job? = null
    private var contentJob: Job? = null
    private var focusedEpgJob: Job? = null
    private var groupEpgJob: Job? = null
    private var visibilityJob: Job? = null
    private var movieDetailsJob: Job? = null
    private var seriesDetailsJob: Job? = null
    private val movieDetailsCache = mutableMapOf<Pair<Long, Long>, Movie>()
    private val seriesDetailsCache = mutableMapOf<Pair<Long, Long>, Series>()
    private val seriesFallbackPlots = mutableMapOf<Pair<Long, Long>, String>()
    private var liveSelectionStartedAtMs = 0L
    private val liveContentCache = linkedMapOf<Pair<Long, Long>, TiviContent.LiveContent>()

    private val _visibilityDialogState = MutableStateFlow<TiviVisibilityDialogState?>(null)
    val visibilityDialogState: StateFlow<TiviVisibilityDialogState?> = _visibilityDialogState.asStateFlow()

    init {
        loadProviders()
        viewModelScope.launch {
            iptvSettingsDataStore.vodPosterSize.collect { size ->
                _uiState.update { state -> state.copy(posterSize = size) }
            }
        }
    }

    private fun loadProviders() {
        viewModelScope.launch {
            providerRepository.getProviders()
                .catch { }
                .collect { providers ->
                    _uiState.update { state ->
                        val existingNodes = state.providerNodes.associateBy { it.provider.id }
                        state.copy(
                            providerNodes = providers.map { provider ->
                                existingNodes[provider.id]?.copy(provider = provider)
                                    ?: TiviProviderNode(provider = provider)
                            },
                            isLoadingProviders = false,
                        )
                    }
                }
        }
    }

    fun selectTab(tab: TiviTab) {
        if (_uiState.value.selectedTab == tab) return
        providerFocusJob?.cancel()
        cancelContentJobs()
        _uiState.update {
            it.copy(
                selectedTab = tab,
                providerNodes = it.providerNodes.map { n -> n.copy(isExpanded = false, groups = emptyList()) },
                selectedGroupId = null,
                selectedProviderId = null,
                content = TiviContent.Empty,
            )
        }
    }

    fun toggleProvider(providerId: Long) {
        val node = _uiState.value.providerNodes.firstOrNull { it.provider.id == providerId } ?: return
        if (!node.isExpanded) {
            viewModelScope.launch {
                val groups = loadVisibleGroups(providerId, _uiState.value.selectedTab)
                _uiState.update { state ->
                    state.copy(providerNodes = state.providerNodes.map { n ->
                        if (n.provider.id == providerId) n.copy(groups = groups, isExpanded = true) else n
                    })
                }
            }
        } else {
            _uiState.update { state ->
                state.copy(providerNodes = state.providerNodes.map { n ->
                    if (n.provider.id == providerId) n.copy(isExpanded = !n.isExpanded) else n
                })
            }
        }
    }

    /** Survol provider sans clic : charge les groupes si besoin puis affiche le 1er groupe */
    fun focusProvider(providerId: Long) {
        providerFocusJob?.cancel()
        providerFocusJob = viewModelScope.launch {
            delay(150)
            focusProviderNow(providerId)
        }
    }

    private suspend fun focusProviderNow(providerId: Long) {
        if (_uiState.value.providerNodes.none { it.provider.id == providerId }) return
        val groups = loadVisibleGroups(providerId, _uiState.value.selectedTab)
        _uiState.update { state ->
            state.copy(providerNodes = state.providerNodes.map { node ->
                if (node.provider.id == providerId) node.copy(groups = groups) else node
            })
        }
        val current = _uiState.value
        val selectedStillVisible = current.selectedProviderId == providerId &&
            groups.any { it.id == current.selectedGroupId }
        if (!selectedStillVisible) groups.firstOrNull()?.let { selectGroup(providerId, it.id) }
    }

    private suspend fun loadVisibleGroups(providerId: Long, tab: TiviTab): List<TiviGroup> {
        val categories = when (tab) {
            TiviTab.LIVE -> channelRepository.getCategories(providerId)
            TiviTab.MOVIES -> movieRepository.getCategories(providerId)
            TiviTab.SERIES -> seriesRepository.getCategories(providerId)
        }.catch { emit(emptyList()) }.first()
        val hiddenIds = hiddenCategoryIds(providerId, tab.contentType)
        return categories
            .filter { it.type == tab.contentType && it.id !in hiddenIds }
            .map { TiviGroup(it.id, it.name) }
    }
    /** Survol groupe sans clic : charge le contenu immédiatement */
    fun focusGroup(providerId: Long, groupId: Long) {
        val current = _uiState.value
        if (current.selectedGroupId == groupId && current.selectedProviderId == providerId) return
        selectGroup(providerId, groupId)
    }

    fun selectGroup(providerId: Long, groupId: Long) {
        providerFocusJob?.cancel()
        cancelContentJobs()
        liveSelectionStartedAtMs = SystemClock.elapsedRealtime()
        val cachedLiveContent = liveContentCache[providerId to groupId]
        _uiState.update {
            it.copy(
                selectedGroupId = groupId,
                selectedProviderId = providerId,
                content = when (it.selectedTab) {
                    TiviTab.LIVE   -> cachedLiveContent?.copy(isLoading = false)
                        ?: TiviContent.LiveContent(isLoading = true)
                    TiviTab.MOVIES -> TiviContent.MoviesContent(isLoading = true)
                    TiviTab.SERIES -> TiviContent.SeriesContent(isLoading = true)
                }
            )
        }
        if (_uiState.value.selectedTab == TiviTab.LIVE && cachedLiveContent != null) {
            logLiveRender(providerId, groupId, cachedLiveContent.channels.size, "memory")
        }
        when (_uiState.value.selectedTab) {
            TiviTab.LIVE   -> loadChannels(providerId, groupId)
            TiviTab.MOVIES -> loadMovies(providerId, groupId)
            TiviTab.SERIES -> loadSeries(providerId, groupId)
        }
    }

    // ── Live ────────────────────────────────────────────────────────────────

    private fun loadChannels(providerId: Long, groupId: Long) {
        contentJob = viewModelScope.launch {
            channelRepository.getChannelsByCategory(providerId, groupId)
                .catch { }
                .collect { channels ->
                    if (!isCurrentSelection(providerId, groupId)) return@collect
                    _uiState.update { state ->
                        val current = state.content as? TiviContent.LiveContent ?: TiviContent.LiveContent()
                        val channelIds = channels.mapTo(mutableSetOf()) { it.id }
                        val updated = current.copy(
                            channels = channels,
                            focusedChannel = current.focusedChannel?.takeIf { it.id in channelIds },
                            epgRows = current.epgRows.takeIf { rows ->
                                rows.size == channels.size && rows.all { it.channel.id in channelIds }
                            }.orEmpty(),
                            isLoading = false
                        )
                        state.copy(content = updated)
                    }
                    cacheCurrentLiveContent()
                    logLiveRender(providerId, groupId, channels.size, "room")
                    if (channels.isNotEmpty()) {
                        val ch = channels.first()
                        focusChannel(providerId, ch)
                        loadEpgForGroup(providerId, channels)
                    }
                }
        }
    }

    fun focusChannel(providerId: Long, channel: Channel) {
        _uiState.update { state ->
            val current = state.content as? TiviContent.LiveContent ?: return@update state
            state.copy(content = current.copy(focusedChannel = channel))
        }
        cacheCurrentLiveContent()
        focusedEpgJob?.cancel()
        focusedEpgJob = viewModelScope.launch {
            delay(120)
            val epgId = channel.epgChannelId ?: channel.id.toString()
            epgRepository.getNowAndNext(providerId, epgId).catch { }.collect { (now, next) ->
                if ((_uiState.value.content as? TiviContent.LiveContent)?.focusedChannel?.id != channel.id) {
                    return@collect
                }
                _uiState.update { state ->
                    val current = state.content as? TiviContent.LiveContent ?: return@update state
                    state.copy(content = current.copy(currentProgram = now, nextProgram = next))
                }
                cacheCurrentLiveContent()
            }
        }
    }

    private fun loadEpgForGroup(providerId: Long, channels: List<Channel>) {
        if (channels.isEmpty()) return
        groupEpgJob?.cancel()
        groupEpgJob = viewModelScope.launch {
            val now = System.currentTimeMillis()
            val end = now + 4 * 60 * 60 * 1000L
            val epgIds = channels.map { it.epgChannelId ?: it.id.toString() }
            epgRepository.getProgramsForChannels(providerId, epgIds, now, end).catch { }.collect { map ->
                val rows = channels.map { ch ->
                    TiviEpgRow(ch, map[ch.epgChannelId ?: ch.id.toString()] ?: emptyList())
                }
                _uiState.update { state ->
                    val current = state.content as? TiviContent.LiveContent ?: return@update state
                    state.copy(content = current.copy(epgRows = rows))
                }
                cacheCurrentLiveContent()
            }
        }
    }

    // ── Movies ───────────────────────────────────────────────────────────────

    private fun loadMovies(providerId: Long, groupId: Long) {
        contentJob = viewModelScope.launch {
            combine(
                movieRepository.getMoviesByCategory(providerId, groupId),
                preferencesRepository.getHiddenMovieIds(providerId)
            ) { movies, hiddenIds -> movies.filterNot { it.id in hiddenIds } }
                .catch { }
                .collect { movies ->
                if (!isCurrentSelection(providerId, groupId)) return@collect
                _uiState.update { state ->
                    state.copy(content = TiviContent.MoviesContent(movies = movies, isLoading = false))
                }
            }
        }
    }

    // ── Series ───────────────────────────────────────────────────────────────

    private fun loadSeries(providerId: Long, groupId: Long) {
        contentJob = viewModelScope.launch {
            combine(
                seriesRepository.getSeriesByCategory(providerId, groupId),
                preferencesRepository.getHiddenSeriesIds(providerId)
            ) { series, hiddenIds -> series.filterNot { it.id in hiddenIds } }
                .catch { }
                .collect { series ->
                    if (!isCurrentSelection(providerId, groupId)) return@collect
                    _uiState.update { state ->
                        val previous = (state.content as? TiviContent.SeriesContent)?.series.orEmpty()
                        val previousById = previous.associateBy { it.id }
                        val byId = series.associateBy { it.id }
                        val stableSeries = if (
                            previous.isNotEmpty() && previous.map { it.id }.toSet() == byId.keys
                        ) previous.mapNotNull { old ->
                            byId[old.id]?.let { fresh ->
                                fresh.copy(
                                    posterUrl = fresh.posterUrl ?: old.posterUrl,
                                    backdropUrl = fresh.backdropUrl ?: old.backdropUrl,
                                    plot = fresh.plot?.takeIf { it.isNotBlank() } ?: old.plot,
                                    genre = fresh.genre?.takeIf { it.isNotBlank() } ?: old.genre,
                                    releaseDate = fresh.releaseDate?.takeIf { it.isNotBlank() } ?: old.releaseDate,
                                    rating = fresh.rating.takeIf { it > 0f } ?: old.rating,
                                )
                            }
                        } else series.map { fresh ->
                            val old = previousById[fresh.id] ?: return@map fresh
                            fresh.copy(
                                plot = fresh.plot?.takeIf { it.isNotBlank() } ?: old.plot,
                                backdropUrl = fresh.backdropUrl ?: old.backdropUrl,
                            )
                        }
                        state.copy(content = TiviContent.SeriesContent(series = stableSeries, isLoading = false))
                    }
                }
        }
    }
    fun toggleMovieFavorite(movie: Movie) {
        val providerId = movie.providerId.takeIf { it > 0L } ?: _uiState.value.selectedProviderId ?: return
        viewModelScope.launch {
            val isFavorite = favoriteRepository.isFavorite(providerId, movie.id, ContentType.MOVIE)
            val result = if (isFavorite) {
                favoriteRepository.removeFavorite(providerId, movie.id, ContentType.MOVIE)
            } else {
                favoriteRepository.addFavorite(providerId, movie.id, ContentType.MOVIE)
            }
            if (result is Result.Success) replaceMovie(movie.copy(isFavorite = !isFavorite))
        }
    }

    fun toggleSeriesFavorite(series: Series) {
        val providerId = series.providerId.takeIf { it > 0L } ?: _uiState.value.selectedProviderId ?: return
        viewModelScope.launch {
            val isFavorite = favoriteRepository.isFavorite(providerId, series.id, ContentType.SERIES)
            val result = if (isFavorite) {
                favoriteRepository.removeFavorite(providerId, series.id, ContentType.SERIES)
            } else {
                favoriteRepository.addFavorite(providerId, series.id, ContentType.SERIES)
            }
            if (result is Result.Success) replaceSeries(series.copy(isFavorite = !isFavorite))
        }
    }

    fun toggleChannelFavorite(channel: Channel) {
        val providerId = channel.providerId.takeIf { it > 0L } ?: _uiState.value.selectedProviderId ?: return
        viewModelScope.launch {
            val isFavorite = favoriteRepository.isFavorite(providerId, channel.id, ContentType.LIVE)
            val result = if (isFavorite) {
                favoriteRepository.removeFavorite(providerId, channel.id, ContentType.LIVE)
            } else {
                favoriteRepository.addFavorite(providerId, channel.id, ContentType.LIVE)
            }
            if (result is Result.Success) replaceChannel(channel.copy(isFavorite = !isFavorite))
        }
    }

    private fun replaceChannel(channel: Channel) {
        _uiState.update { state ->
            val content = state.content as? TiviContent.LiveContent ?: return@update state
            state.copy(content = content.copy(
                channels = content.channels.map { if (it.id == channel.id) channel else it },
                focusedChannel = content.focusedChannel?.let { if (it.id == channel.id) channel else it }
            ))
        }
    }

    suspend fun resolveMovieFromProgress(
        providerId: Long,
        movieId: Long,
        isStableStreamId: Boolean = false
    ): Result<Triple<Movie, String, Map<String, String>>> {
        val movie = if (isStableStreamId) {
            movieRepository.getMovieByStreamId(providerId, movieId)
        } else {
            movieRepository.getMovie(movieId)
                ?.takeIf { it.providerId == providerId || it.providerId == 0L }
        }
            ?: return Result.Error("Film IPTV introuvable")
        return when (val streamResult = movieRepository.getStreamInfo(movie)) {
            is Result.Success -> Result.Success(
                Triple(movie, streamResult.data.url, streamResult.data.headers)
            )
            is Result.Error -> Result.Error(streamResult.message)
            else -> Result.Error("Erreur de lecture IPTV")
        }
    }

    suspend fun resolveSeriesFromProgress(
        providerId: Long,
        seriesId: Long? = null,
        providerSeriesId: String? = null,
    ): Series? {
        return providerSeriesId
            ?.takeIf { it.isNotBlank() }
            ?.let { seriesRepository.getSeriesByProviderSeriesId(providerId, it) }
            ?: seriesId?.let { seriesRepository.getSeriesById(it) }
                ?.takeIf { it.providerId == providerId || it.providerId == 0L }
    }

    suspend fun resolveSeriesEpisodeFromProgress(
        providerId: Long,
        seriesId: Long? = null,
        providerSeriesId: String? = null,
        seasonNumber: Int?,
        episodeNumber: Int?,
        episodeVideoId: String?,
        contentId: String,
    ): Result<TiviSeriesEpisodePlayback> {
        val baseSeries = resolveSeriesFromProgress(
            providerId = providerId,
            seriesId = seriesId,
            providerSeriesId = providerSeriesId
        ) ?: return Result.Error("Serie IPTV introuvable")

        val series = when (val result = seriesRepository.getSeriesDetails(providerId, baseSeries.id)) {
            is Result.Success -> result.data
            else -> baseSeries
        }

        val episodeId = episodeVideoId?.toLongOrNull()
        val targetEpisode = series.seasons
            .asSequence()
            .flatMap { it.episodes.asSequence() }
            .firstOrNull { episode ->
                when {
                    seasonNumber != null && episodeNumber != null ->
                        episode.seasonNumber == seasonNumber && episode.episodeNumber == episodeNumber
                    episodeId != null ->
                        episode.id == episodeId || episode.episodeId == episodeId
                    else -> false
                }
            } ?: return Result.Error("Episode IPTV introuvable")

        return when (val streamResult = seriesRepository.getEpisodeStreamInfo(targetEpisode)) {
            is Result.Success -> Result.Success(
                TiviSeriesEpisodePlayback(
                    series = series,
                    streamUrl = streamResult.data.url,
                    title = "${series.name} - ${targetEpisode.title}",
                    headers = streamResult.data.headers,
                    contentId = contentId,
                    videoId = targetEpisode.episodeId.takeIf { it > 0 }?.toString() ?: targetEpisode.id.toString(),
                    posterUrl = targetEpisode.coverUrl ?: series.posterUrl,
                    backdropUrl = series.backdropUrl,
                    season = targetEpisode.seasonNumber,
                    episode = targetEpisode.episodeNumber,
                    episodeTitle = targetEpisode.title,
                )
            )
            is Result.Error -> Result.Error(streamResult.message)
            else -> Result.Error("Erreur de lecture IPTV")
        }
    }

    fun enrichMovieDetails(movie: Movie) {
        val providerId = movie.providerId.takeIf { it > 0L } ?: _uiState.value.selectedProviderId ?: return
        val cacheKey = providerId to movie.id
        val cached = movieDetailsCache[cacheKey]
        if (cached != null) {
            replaceMovie(mergeMovieDetailsWithCardState(movie, cached))
            return
        }
        movieDetailsJob?.cancel()
        movieDetailsJob = viewModelScope.launch {
            delay(120)
            when (val result = movieRepository.getMovieDetails(providerId, movie.id)) {
                is Result.Success -> {
                    movieDetailsCache[cacheKey] = result.data
                    replaceMovie(mergeMovieDetailsWithCardState(movie, result.data))
                }
                else -> Unit
            }
        }
    }

    fun enrichSeriesDetails(series: Series) {
        val providerId = series.providerId.takeIf { it > 0L } ?: _uiState.value.selectedProviderId ?: return
        val cacheKey = providerId to series.id
        val cached = seriesDetailsCache[cacheKey]
        if (cached != null) {
            replaceSeries(mergeSeriesDetailsWithCardState(series, cached))
            return
        }
        seriesDetailsJob?.cancel()
        seriesDetailsJob = viewModelScope.launch {
            delay(180)
            val providerDetails = when (val result = seriesRepository.getSeriesDetails(providerId, series.id)) {
                is Result.Success -> result.data
                else -> series
            }
            val metadata = if (providerDetails.plot.isNullOrBlank()) {
                tmdbService.fetchMetadataForTitleQuery(series.name, "tv")
            } else null
            val fallbackPlot = seriesFallbackPlots[cacheKey]
                ?: metadata?.overview
                ?: providerDetails.plot
                ?: series.plot
            val enriched = providerDetails.copy(
                posterUrl = providerDetails.posterUrl ?: metadata?.posterUrl,
                backdropUrl = providerDetails.backdropUrl ?: metadata?.backdropUrl,
                plot = providerDetails.plot?.takeIf { it.isNotBlank() } ?: fallbackPlot,
                genre = providerDetails.genre?.takeIf { it.isNotBlank() }
                    ?: metadata?.genres?.takeIf { it.isNotEmpty() }?.joinToString(", "),
                releaseDate = providerDetails.releaseDate?.takeIf { it.isNotBlank() } ?: metadata?.year,
                rating = if (providerDetails.rating > 0f) providerDetails.rating
                    else metadata?.voteAverage?.toFloat()?.coerceIn(0f, 10f) ?: 0f,
            )
            enriched.plot?.takeIf { it.isNotBlank() }?.let { seriesFallbackPlots[cacheKey] = it }
            seriesDetailsCache[cacheKey] = enriched
            replaceSeries(mergeSeriesDetailsWithCardState(series, enriched))
        }
    }
    private fun replaceMovie(movie: Movie) {
        _uiState.update { state ->
            val content = state.content as? TiviContent.MoviesContent ?: return@update state
            state.copy(content = content.copy(
                movies = content.movies.map { if (it.id == movie.id) movie else it }
            ))
        }
    }

    private fun replaceSeries(series: Series) {
        _uiState.update { state ->
            val content = state.content as? TiviContent.SeriesContent ?: return@update state
            state.copy(content = content.copy(
                series = content.series.map { if (it.id == series.id) series else it }
            ))
        }
    }
    suspend fun resolveMovieStream(movie: Movie): Result<Pair<String, Map<String, String>>> {
        return when (val result = movieRepository.getStreamInfo(movie)) {
            is Result.Success -> Result.Success(result.data.url to result.data.headers)
            is Result.Error -> Result.Error(result.message)
            else -> Result.Error("Erreur inconnue")
        }
    }

    fun openProviderVisibility(providerId: Long) {
        visibilityJob?.cancel()
        visibilityJob = viewModelScope.launch {
            val tab = _uiState.value.selectedTab
            val categories = when (tab) {
                TiviTab.LIVE -> channelRepository.getCategories(providerId)
                TiviTab.MOVIES -> movieRepository.getCategories(providerId)
                TiviTab.SERIES -> seriesRepository.getCategories(providerId)
            }.first().filter { it.type == tab.contentType }
            val hiddenIds = hiddenCategoryIds(providerId, tab.contentType)
            val providerName = _uiState.value.providerNodes
                .firstOrNull { it.provider.id == providerId }?.provider?.name.orEmpty()
            _visibilityDialogState.value = TiviVisibilityDialogState(
                kind = TiviVisibilityDialogKind.PROVIDER_CATEGORIES,
                providerId = providerId,
                groupId = null,
                contentType = tab.contentType,
                title = "Catégories — $providerName",
                items = categories.map { category ->
                    TiviVisibilityItem(category.id, category.name, category.id !in hiddenIds)
                }
            )
        }
    }

    fun openGroupVisibility(providerId: Long, groupId: Long) {
        visibilityJob?.cancel()
        visibilityJob = viewModelScope.launch {
            val tab = _uiState.value.selectedTab
            val groupName = _uiState.value.providerNodes
                .firstOrNull { it.provider.id == providerId }
                ?.groups?.firstOrNull { it.id == groupId }?.name.orEmpty()
            val items = when (tab) {
                TiviTab.LIVE -> {
                    val hiddenIds = preferencesRepository.getHiddenChannelIds(providerId).first()
                    channelRepository.getChannelsByCategoryForVisibility(providerId, groupId).first()
                        .map { TiviVisibilityItem(it.id, it.name, it.id !in hiddenIds) }
                }
                TiviTab.MOVIES -> {
                    val hiddenIds = preferencesRepository.getHiddenMovieIds(providerId).first()
                    movieRepository.getMoviesByCategory(providerId, groupId).first()
                        .map { TiviVisibilityItem(it.id, it.name, it.id !in hiddenIds) }
                }
                TiviTab.SERIES -> {
                    val hiddenIds = preferencesRepository.getHiddenSeriesIds(providerId).first()
                    seriesRepository.getSeriesByCategory(providerId, groupId).first()
                        .map { TiviVisibilityItem(it.id, it.name, it.id !in hiddenIds) }
                }
            }
            _visibilityDialogState.value = TiviVisibilityDialogState(
                kind = TiviVisibilityDialogKind.GROUP_ITEMS,
                providerId = providerId,
                groupId = groupId,
                contentType = tab.contentType,
                title = "Visibilité — $groupName",
                items = items
            )
        }
    }

    fun dismissVisibilityDialog() {
        val dialog = _visibilityDialogState.value
        visibilityJob?.cancel()
        _visibilityDialogState.value = null
        if (dialog?.kind == TiviVisibilityDialogKind.PROVIDER_CATEGORIES) {
            viewModelScope.launch {
                focusProviderNow(dialog.providerId)
            }
        }
    }

    fun updateVisibility(visibleIds: Set<Long>) {
        val dialog = _visibilityDialogState.value ?: return
        viewModelScope.launch {
            val dialogIds = dialog.items.mapTo(mutableSetOf(), TiviVisibilityItem::id)
            val hiddenInDialog = dialogIds - visibleIds

            when (dialog.kind) {
                TiviVisibilityDialogKind.PROVIDER_CATEGORIES -> {
                    val existing = preferencesRepository
                        .getHiddenCategoryIds(dialog.providerId, dialog.contentType).first()
                    preferencesRepository.setHiddenCategoryIds(
                        dialog.providerId,
                        dialog.contentType,
                        (existing - dialogIds) + hiddenInDialog
                    )
                    iptvSettingsDataStore.setHiddenGroupIds(
                        providerId = dialog.providerId,
                        contentType = dialog.contentType,
                        hiddenGroupIds = ((existing - dialogIds) + hiddenInDialog).mapTo(mutableSetOf()) { it.toString() },
                        replaceGroupIds = dialogIds.mapTo(mutableSetOf()) { it.toString() }
                    )

                    val persistedHiddenIds = hiddenCategoryIds(dialog.providerId, dialog.contentType)
                    val categories = when (dialog.contentType) {
                        ContentType.LIVE -> channelRepository.getCategories(dialog.providerId)
                        ContentType.MOVIE -> movieRepository.getCategories(dialog.providerId)
                        ContentType.SERIES -> seriesRepository.getCategories(dialog.providerId)
                        else -> flowOf(emptyList())
                    }.first()
                    val visibleGroups = categories
                        .filter { it.type == dialog.contentType && it.id !in persistedHiddenIds }
                        .map { TiviGroup(it.id, it.name) }

                    liveContentCache.keys
                        .filter { it.first == dialog.providerId }
                        .toList()
                        .forEach(liveContentCache::remove)

                    _uiState.update { state ->
                        val selectionBelongsToProvider = state.selectedProviderId == dialog.providerId
                        val selectedGroup = state.selectedGroupId
                        val selectedStillVisible = selectedGroup != null &&
                            visibleGroups.any { it.id == selectedGroup }
                        state.copy(
                            providerNodes = state.providerNodes.map { node ->
                                if (node.provider.id == dialog.providerId) {
                                    node.copy(groups = visibleGroups, isExpanded = true)
                                } else node
                            },
                            selectedProviderId = if (selectionBelongsToProvider && !selectedStillVisible) null else state.selectedProviderId,
                            selectedGroupId = if (selectionBelongsToProvider && !selectedStillVisible) null else state.selectedGroupId,
                            content = if (selectionBelongsToProvider && !selectedStillVisible) TiviContent.Empty else state.content
                        )
                    }
                }

                TiviVisibilityDialogKind.GROUP_ITEMS -> when (dialog.contentType) {
                    ContentType.LIVE -> {
                        val existing = preferencesRepository.getHiddenChannelIds(dialog.providerId).first()
                        preferencesRepository.setHiddenChannelIds(
                            dialog.providerId,
                            (existing - dialogIds) + hiddenInDialog
                        )
                        dialog.groupId?.let { liveContentCache.remove(dialog.providerId to it) }
                    }
                    ContentType.MOVIE -> {
                        val existing = preferencesRepository.getHiddenMovieIds(dialog.providerId).first()
                        preferencesRepository.setHiddenMovieIds(
                            dialog.providerId,
                            (existing - dialogIds) + hiddenInDialog
                        )
                    }
                    ContentType.SERIES -> {
                        val existing = preferencesRepository.getHiddenSeriesIds(dialog.providerId).first()
                        preferencesRepository.setHiddenSeriesIds(
                            dialog.providerId,
                            (existing - dialogIds) + hiddenInDialog
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
    private suspend fun hiddenCategoryIds(providerId: Long, contentType: ContentType): Set<Long> {
        val hiddenFromPreferences = preferencesRepository
            .getHiddenCategoryIds(providerId, contentType)
            .first()
        val hiddenFromLegacySettings = iptvSettingsDataStore
            .visibilitySettings(providerId, contentType)
            .first()
            .hiddenGroupIds
            .mapNotNull(String::toLongOrNull)
            .toSet()
        return hiddenFromPreferences + hiddenFromLegacySettings
    }
    private fun isCurrentSelection(providerId: Long, groupId: Long): Boolean =
        _uiState.value.selectedProviderId == providerId && _uiState.value.selectedGroupId == groupId

    private fun cacheCurrentLiveContent() {
        val state = _uiState.value
        val providerId = state.selectedProviderId ?: return
        val groupId = state.selectedGroupId ?: return
        val content = state.content as? TiviContent.LiveContent ?: return
        val key = providerId to groupId
        liveContentCache.remove(key)
        liveContentCache[key] = content.copy(isLoading = false)
        while (liveContentCache.size > LIVE_CONTENT_CACHE_SIZE) {
            liveContentCache.remove(liveContentCache.keys.first())
        }
    }

    private fun logLiveRender(
        providerId: Long,
        groupId: Long,
        channelCount: Int,
        source: String
    ) {
        if (!BuildConfig.DEBUG) return
        val elapsedMs = SystemClock.elapsedRealtime() - liveSelectionStartedAtMs
        Log.d(
            PERF_TAG,
            "category-render provider=$providerId group=$groupId source=$source " +
                "channels=$channelCount elapsedMs=$elapsedMs"
        )
    }

    private fun cancelContentJobs() {
        contentJob?.cancel()
        focusedEpgJob?.cancel()
        groupEpgJob?.cancel()
        contentJob = null
        focusedEpgJob = null
        groupEpgJob = null
    }
}

internal fun mergeMovieDetailsWithCardState(current: Movie, details: Movie): Movie =
    details.copy(isFavorite = current.isFavorite)

internal fun mergeSeriesDetailsWithCardState(current: Series, details: Series): Series =
    details.copy(isFavorite = current.isFavorite)
