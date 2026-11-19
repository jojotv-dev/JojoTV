package com.nuvio.tv.ui.screens.search

import com.nuvio.tv.domain.model.catalogRowLegacyKey
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.R
import com.nuvio.tv.core.network.NetworkResult
import com.nuvio.tv.core.profile.ProfileManager
import com.nuvio.tv.data.local.DiscoverSelectionDataStore
import com.nuvio.tv.data.local.LayoutPreferenceDataStore
import com.nuvio.tv.data.local.SearchHistoryDataStore
import com.nuvio.tv.data.mediaserver.ServerCatalog
import com.nuvio.tv.domain.model.Addon
import com.nuvio.tv.domain.model.CatalogDescriptor
import com.nuvio.tv.domain.model.CatalogRow
import com.nuvio.tv.domain.model.DiscoverLocation
import com.nuvio.tv.domain.model.catalogRowStableKey
import com.nuvio.tv.domain.model.mergeCatalogPage
import com.nuvio.tv.domain.model.nextCatalogSkip
import com.nuvio.tv.domain.model.skipStep
import com.nuvio.tv.domain.model.stableKey
import com.nuvio.tv.domain.model.supportsExtra
import com.nuvio.tv.core.util.filterReleasedItems
import com.nuvio.tv.core.util.isUnreleased
import com.nuvio.tv.domain.repository.AddonRepository
import java.time.LocalDate
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.PosterShape
import com.nuvio.tv.domain.model.LibraryEntryInput
import com.nuvio.tv.domain.model.enabledAddons
import com.nuvio.tv.domain.model.PLACEHOLDER_IMAGE_URL
import com.nuvio.tv.reshaped.livetv.LiveTvSource
import com.nuvio.tv.reshaped.livetv.LiveTvSourceType
import com.nuvio.tv.reshaped.livetv.LiveTvStorage
import com.nuvio.tv.reshaped.livetv.LiveTvChannel
import com.nuvio.tv.reshaped.livetv.LiveTvRepository
import com.nuvio.tv.reshaped.livetv.LiveTvVodCache
import com.nuvio.tv.reshaped.livetv.LiveTvVodItem
import com.nuvio.tv.reshaped.livetv.LiveTvXtream
import com.nuvio.tv.reshaped.livetv.LiveTvStalker
import com.nuvio.tv.domain.repository.CatalogRepository
import com.nuvio.tv.domain.repository.LibraryRepository
import com.nuvio.tv.data.repository.parseContentIds
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.text.Normalizer
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val addonRepository: AddonRepository,
    private val catalogRepository: CatalogRepository,
    private val metaRepository: com.nuvio.tv.domain.repository.MetaRepository,
    private val discoverSelectionDataStore: DiscoverSelectionDataStore,
    private val layoutPreferenceDataStore: LayoutPreferenceDataStore,
    private val searchHistoryDataStore: SearchHistoryDataStore,
    private val watchProgressRepository: com.nuvio.tv.domain.repository.WatchProgressRepository,
    private val libraryRepository: LibraryRepository,
    private val watchedSeriesStateHolder: com.nuvio.tv.data.local.WatchedSeriesStateHolder,
    val posterOptions: com.nuvio.tv.ui.components.posteroptions.PosterOptionsController,
    private val serverCatalog: ServerCatalog,
    private val profileManager: ProfileManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    /** Saved focus state for restoring scroll/focus position after returning from details. */
    var savedFocusRowKey: String? = null
    var savedFocusItemIndex: Int = -1
    var savedRowScrollPositions: Map<String, Pair<Int, Int>> = emptyMap()
    var hasSavedSearchFocus: Boolean = false

    private val _watchedMovieIds = MutableStateFlow<Set<String>>(emptySet())
    val watchedMovieIds: StateFlow<Set<String>> = _watchedMovieIds.asStateFlow()
    val watchedSeriesIds: StateFlow<Set<String>> = watchedSeriesStateHolder.fullyWatchedSeriesIds
    private val _favoriteItemKeys = MutableStateFlow<Set<String>>(emptySet())
    val favoriteItemKeys: StateFlow<Set<String>> = _favoriteItemKeys.asStateFlow()
    private val pendingFavoriteToggles = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

    private val catalogsMap = linkedMapOf<String, CatalogRow>()
    private val catalogOrder = mutableListOf<String>()

    private var activeSearchJobs: List<Job> = emptyList()

    /**
     * Load-more jobs, tracked separately from [activeSearchJobs] because that list is replaced
     * wholesale by each search run. Cancelled alongside it in [cancelSearchRun].
     */
    private val activeLoadMoreJobs: MutableSet<Job> =
        java.util.concurrent.ConcurrentHashMap.newKeySet()
    private var searchRunJob: Job? = null
    private var activeSearchQuery: String? = null
    private var searchGeneration = 0L
    private var discoverJob: Job? = null
    private var catalogRowsUpdateJob: Job? = null
    private var suggestionJob: Job? = null
    private var liveSearchJob: Job? = null
    private var iptvCacheJob: Job? = null
    private var iptvSearchJob: Job? = null
    private var iptvVodWarmupJob: Job? = null
    private var liveTvSearchJob: Job? = null
    private var liveTvSearchScreenActive = false
    private var iptvCacheProfileId: Int? = null
    private var iptvCacheReadyProfileId: Int? = null
    private val iptvVodWarmupAttempted = mutableSetOf<Pair<String, String>>()
    private var iptvVodItems: List<LiveTvVodItem> = emptyList()
    private var iptvSourcesById: Map<String, LiveTvSource> = emptyMap()
    private var iptvVodSearchIndex: List<IptvVodSearchRecord> = emptyList()
    private var liveTvSearchProfileId: Int? = null
    private var liveTvSearchIndex: List<LiveChannelSearchRecord> = emptyList()
    private var iptvChannelsBySearchId: Map<String, LiveTvChannel> = emptyMap()
    private var hiddenIptvVodCategoryKeys: Set<String> = emptySet()
    private var lastRequestKey: String? = null
    private var lastCompletedRequestKey: String? = null
    private var hasRenderedFirstCatalog = false
    private var pendingCatalogResponses = 0
    private var revealBatchAfterNextDiscoverFetch = false
    private var hideUnreleasedContent = false

    private companion object {
        const val DISCOVER_INITIAL_LIMIT = 100
        const val DISCOVER_SHOW_MORE_BATCH = 50
        const val SUGGESTION_DEBOUNCE_MS = 150L
        const val MAX_IPTV_SEARCH_RESULTS = 24
        const val IPTV_SEARCH_ADDON_PREFIX = "__iptv_search__:"
        private const val IPTV_LIVE_CHANNEL_ID_PREFIX = "__iptv_live_channel__:"
        private const val MIN_IPTV_SEARCH_QUERY_LENGTH = 1

        /**
         * Live search fires while typing, but each run fans out to every enabled addon catalog, so
         * it waits longer than the suggestion debounce to avoid a request storm per keystroke.
         */
        const val LIVE_SEARCH_DEBOUNCE_MS = 250L

        const val MAX_SUGGESTIONS = 8
        /** Splits titles and queries into words. */
        private val WORD_SEPARATOR = Regex("[^\\p{L}\\p{N}]+")
        const val MAX_RECENT_SEARCHES = 8
    }

    init {
        posterOptions.bind(viewModelScope)
        viewModelScope.launch {
            watchProgressRepository.observeWatchedMovieIds()
                .collect { ids -> _watchedMovieIds.value = ids }
        }
        viewModelScope.launch {
            libraryRepository.libraryItems.collectLatest { entries ->
                _favoriteItemKeys.value = entries.mapTo(mutableSetOf()) {
                    searchFavoriteKey(it.id, it.type)
                }
            }
        }
        viewModelScope.launch {
            layoutPreferenceDataStore.discoverLocation.distinctUntilChanged().collectLatest { location ->
                _uiState.update { it.copy(discoverLocation = location) }
                if (location == DiscoverLocation.OFF) {
                    discoverJob?.cancel()
                    discoverJob = null
                    revealBatchAfterNextDiscoverFetch = false
                    _uiState.update {
                        it.copy(
                            discoverInitialized = false,
                            discoverLoading = false,
                            discoverLoadingMore = false,
                            discoverCatalogs = emptyList(),
                            selectedDiscoverType = "movie",
                            selectedDiscoverCatalogKey = null,
                            selectedDiscoverGenre = null,
                            discoverResults = emptyList(),
                            pendingDiscoverResults = emptyList(),
                            discoverHasMore = true,
                            discoverPage = 1
                        )
                    }
                }
            }
        }
        // Combine all layout preference flows into a single collector to reduce coroutine overhead
        viewModelScope.launch {
            combine(
                layoutPreferenceDataStore.posterCardWidthDp,
                layoutPreferenceDataStore.posterLabelsEnabled,
                layoutPreferenceDataStore.catalogAddonNameEnabled,
                layoutPreferenceDataStore.posterCardHeightDp,
                layoutPreferenceDataStore.posterCardCornerRadiusDp
            ) { widthDp, labelsEnabled, addonNameEnabled, heightDp, cornerRadiusDp ->
                LayoutPrefs(widthDp, labelsEnabled, addonNameEnabled, heightDp, cornerRadiusDp)
            }.collectLatest { prefs ->
                _uiState.update {
                    it.copy(
                        posterCardWidthDp = prefs.widthDp,
                        posterLabelsEnabled = prefs.labelsEnabled,
                        catalogAddonNameEnabled = prefs.addonNameEnabled,
                        posterCardHeightDp = prefs.heightDp,
                        posterCardCornerRadiusDp = prefs.cornerRadiusDp
                    )
                }
            }
        }
        viewModelScope.launch {
            layoutPreferenceDataStore.catalogTypeSuffixEnabled.collectLatest { enabled ->
                _uiState.update { it.copy(catalogTypeSuffixEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            layoutPreferenceDataStore.hideUnreleasedContent.collectLatest { enabled ->
                hideUnreleasedContent = enabled
                scheduleCatalogRowsUpdate()
            }
        }
        viewModelScope.launch {
            searchHistoryDataStore.recentSearches.collectLatest { recent ->
                _uiState.update { it.copy(recentSearches = recent.take(MAX_RECENT_SEARCHES)) }
            }
        }
        viewModelScope.launch {
            profileManager.activeProfileId.collectLatest { profileId ->
                LiveTvRepository.ensureLoaded(context, profileId)
                reloadIptvVodCache(profileId)
                if (liveTvSearchScreenActive && _uiState.value.query.isNotBlank()) startLiveTvSearch(profileId)
            }
        }
    }

    private data class LayoutPrefs(
        val widthDp: Int,
        val labelsEnabled: Boolean,
        val addonNameEnabled: Boolean,
        val heightDp: Int,
        val cornerRadiusDp: Int
    )

    private data class IptvVodSearchRecord(
        val source: LiveTvSource,
        val item: LiveTvVodItem,
        val title: String,
        val titleWords: List<String>,
        val category: String,
        val sourceName: String,
        val plot: String,
    )

    private data class IptvVodSearchBatch(
        val source: LiveTvSource,
        val type: String,
        val items: List<LiveTvVodItem>,
    )

    private data class LiveChannelSearchRecord(
        val channel: LiveTvChannel,
        val searchId: String,
        val title: String,
        val tvgName: String,
        val group: String,
        val sourceLabel: String,
        val sourceName: String,
        val tvgId: String,
    )

    fun ensureDiscoverLoaded() {
        val state = _uiState.value
        if (state.discoverLocation == DiscoverLocation.OFF) return
        if (state.discoverInitialized || state.discoverLoading) return
        viewModelScope.launch { loadDiscoverCatalogs() }
    }

    /** Refresh the local IPTV catalogue after returning from its Films/Séries screens. */
    fun refreshIptvVodCache() {
        val profileId = profileManager.activeProfileId.value
        if (profileId == iptvCacheProfileId && iptvCacheJob?.isActive == true) return
        reloadIptvVodCache(profileId)
    }

    fun isIptvSearchRow(row: CatalogRow): Boolean = row.addonId.startsWith(IPTV_SEARCH_ADDON_PREFIX)

    fun isSearchItemFavorite(item: MetaPreview): Boolean =
        searchFavoriteKey(item.id, item.apiType) in _favoriteItemKeys.value

    fun favoriteKeyForSearchItem(item: MetaPreview): String =
        searchFavoriteKey(item.id, item.apiType)

    fun toggleSearchItemFavorite(item: MetaPreview, addonBaseUrl: String) {
        val key = searchFavoriteKey(item.id, item.apiType)
        if (!pendingFavoriteToggles.add(key)) return
        val wasFavorite = key in _favoriteItemKeys.value
        _favoriteItemKeys.update { current ->
            if (wasFavorite) current - key else current + key
        }
        viewModelScope.launch {
            try {
                libraryRepository.toggleDefault(item.toSearchLibraryEntryInput(addonBaseUrl))
                val isFavorite = libraryRepository.isInLibrary(item.id, item.apiType).first()
                _favoriteItemKeys.update { current ->
                    if (isFavorite) current + key else current - key
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _favoriteItemKeys.update { current ->
                    if (wasFavorite) current + key else current - key
                }
            } finally {
                pendingFavoriteToggles.remove(key)
            }
        }
    }

    fun iptvVodItem(id: String): LiveTvVodItem? = iptvVodItems.firstOrNull { it.id == id }

    fun iptvLiveChannel(id: String): LiveTvChannel? = iptvChannelsBySearchId[id]

    fun iptvSource(sourceId: String): LiveTvSource? = iptvSourcesById[sourceId]

    val activeProfileId: Int get() = profileManager.activeProfileId.value

    fun resumeLiveTvSearch() {
        liveTvSearchScreenActive = true
        startLiveTvSearch(activeProfileId)
    }

    fun pauseLiveTvSearch() {
        liveTvSearchScreenActive = false
        stopLiveTvSearch()
    }

    private fun stopLiveTvSearch() {
        liveTvSearchJob?.cancel()
        liveTvSearchJob = null
        liveTvSearchProfileId = null
        liveTvSearchIndex = emptyList()
    }

    private fun startLiveTvSearch(profileId: Int) {
        if (liveTvSearchProfileId == profileId && liveTvSearchJob?.isActive == true) return
        stopLiveTvSearch()
        liveTvSearchProfileId = profileId
        LiveTvRepository.ensureLoaded(context, profileId)
        liveTvSearchJob = viewModelScope.launch {
            var previousChannels: List<LiveTvChannel>? = null
            var previousSources: List<LiveTvSource>? = null
            LiveTvRepository.uiState.collect { state ->
                if (profileId != profileManager.activeProfileId.value) return@collect
                if (state.shownChannels === previousChannels && state.sources === previousSources) return@collect
                previousChannels = state.shownChannels
                previousSources = state.sources
                val sources = state.sources.associateBy(LiveTvSource::id)
                liveTvSearchIndex = withContext(Dispatchers.Default) {
                    state.shownChannels.mapIndexed { index, channel ->
                        LiveChannelSearchRecord(
                            channel = channel,
                            searchId = liveTvSearchResultId(index),
                            title = normalizeIptvSearch(channel.name),
                            tvgName = normalizeIptvSearch(channel.tvgName.orEmpty()),
                            group = normalizeIptvSearch(channel.group),
                            sourceLabel = sources[channel.sourceId]?.searchDisplayName().orEmpty().ifBlank { "TV" },
                            sourceName = normalizeIptvSearch(sources[channel.sourceId]?.searchDisplayName().orEmpty()),
                            tvgId = normalizeIptvSearch(channel.tvgId.orEmpty()),
                        )
                    }
                }
                updateIptvSearchRows(_uiState.value.query)
            }
        }
    }

    private fun reloadIptvVodCache(profileId: Int) {
        val profileChanged = iptvCacheProfileId != profileId
        iptvCacheProfileId = profileId
        iptvCacheJob?.cancel()
        iptvVodWarmupJob?.cancel()
        iptvVodWarmupJob = null
        iptvCacheReadyProfileId = null
        iptvVodWarmupAttempted.clear()
        if (profileChanged) {
            stopLiveTvSearch()
            iptvVodItems = emptyList()
            iptvSourcesById = emptyMap()
            iptvVodSearchIndex = emptyList()
            iptvChannelsBySearchId = emptyMap()
            hiddenIptvVodCategoryKeys = emptySet()
            iptvSearchJob?.cancel()
            _uiState.update { it.copy(iptvCatalogRows = emptyList()) }
        }
        iptvCacheJob = viewModelScope.launch {
            val (savedSources, hiddenCategories) = withContext(Dispatchers.IO) {
                LiveTvStorage(context, profileId).let { storage ->
                    storage.sources() to storage.hiddenVodCategoryKeys()
                }
            }
            val sources = savedSources
                .filter { source ->
                    (source.type == LiveTvSourceType.Xtream && source.xtream.isConfigured) ||
                        (source.type == LiveTvSourceType.Stalker && source.stalker.isConfigured)
                }
            val cachedItems = buildList {
                sources.forEach { source ->
                    addAll(LiveTvVodCache.read(context, source.id, "movie"))
                    addAll(LiveTvVodCache.read(context, source.id, "series"))
                }
            }.distinctBy { item -> Triple(item.sourceId, item.type, item.id) }
            val sourcesById = sources.associateBy(LiveTvSource::id)
            val searchIndex = withContext(Dispatchers.Default) {
                cachedItems.mapNotNull { item ->
                    val source = sourcesById[item.sourceId] ?: return@mapNotNull null
                    val title = normalizeIptvSearch(item.title)
                    IptvVodSearchRecord(
                        source = source,
                        item = item,
                        title = title,
                        titleWords = title.split(WORD_SEPARATOR).filter(String::isNotBlank),
                        category = normalizeIptvSearch(item.categoryName),
                        sourceName = normalizeIptvSearch(source.searchDisplayName()),
                        plot = normalizeIptvSearch(item.plot),
                    )
                }
            }

            if (profileId != profileManager.activeProfileId.value) return@launch
            iptvSourcesById = sourcesById
            iptvVodItems = cachedItems
            iptvVodSearchIndex = searchIndex
            hiddenIptvVodCategoryKeys = hiddenCategories
            iptvCacheReadyProfileId = profileId
            updateIptvSearchRows(_uiState.value.query)
            if (_uiState.value.query.isNotBlank()) loadMissingIptvVodCatalogs(profileId)
        }
    }

    /**
     * Warm uncached IPTV movie and series catalogues after the first query. Results are indexed
     * batch by batch, so a slow provider does not hold back results from other catalogues.
     */
    private fun loadMissingIptvVodCatalogs(profileId: Int) {
        if (profileId != profileManager.activeProfileId.value || iptvCacheReadyProfileId != profileId) return
        if (iptvVodWarmupJob?.isActive == true) return

        val cachedKeys = iptvVodItems.mapTo(mutableSetOf()) { it.sourceId to it.type }
        val missing = iptvSourcesById.values.flatMap { source ->
            listOf("movie", "series").map { type -> source to type }
        }.filter { (source, type) ->
            (source.id to type) !in cachedKeys && (source.id to type) !in iptvVodWarmupAttempted
        }
        if (missing.isEmpty()) return

        val attemptedKeys = missing.mapTo(mutableSetOf()) { (source, type) -> source.id to type }
        iptvVodWarmupAttempted += attemptedKeys
        iptvVodWarmupJob = viewModelScope.launch {
            try {
                val batches = Channel<IptvVodSearchBatch>(Channel.UNLIMITED)
                val requestSlots = Semaphore(2)
                val fetchJobs = missing.map { (source, type) ->
                    launch(Dispatchers.IO) {
                        try {
                            requestSlots.withPermit {
                                val loadedItems = when (source.type) {
                                    LiveTvSourceType.Xtream -> LiveTvXtream.vodItems(source, type) { batch ->
                                        batches.trySend(IptvVodSearchBatch(source, type, batch))
                                    }
                                    LiveTvSourceType.Stalker -> LiveTvStalker.vodItems(source, type) { batch ->
                                        batches.trySend(IptvVodSearchBatch(source, type, batch))
                                    }
                                    LiveTvSourceType.M3u -> emptyList()
                                }
                                if (loadedItems.isNotEmpty()) {
                                    LiveTvVodCache.write(context, source.id, type, loadedItems)
                                    batches.trySend(IptvVodSearchBatch(source, type, loadedItems))
                                }
                            }
                        } catch (failure: CancellationException) {
                            throw failure
                        } catch (_: Exception) {
                            // Keep any batches already received; a later explicit refresh can retry.
                        }
                    }
                }
                val closeBatches = launch {
                    fetchJobs.joinAll()
                    batches.close()
                }
                for (batch in batches) {
                    if (profileId != profileManager.activeProfileId.value) break
                    addIptvVodSearchItems(profileId, batch.source, batch.type, batch.items)
                }
                closeBatches.join()
            } finally {
                if (iptvVodWarmupJob === coroutineContext[Job]) iptvVodWarmupJob = null
            }
        }
    }

    private suspend fun addIptvVodSearchItems(
        profileId: Int,
        source: LiveTvSource,
        type: String,
        items: List<LiveTvVodItem>,
    ) {
        if (profileId != profileManager.activeProfileId.value || items.isEmpty()) return
        val existingKeys = iptvVodItems.mapTo(mutableSetOf()) { Triple(it.sourceId, it.type, it.id) }
        val additions = items.filter { item ->
            item.sourceId == source.id && item.type == type &&
                existingKeys.add(Triple(item.sourceId, item.type, item.id))
        }
        if (additions.isEmpty()) return
        iptvVodItems = iptvVodItems + additions
        val records = withContext(Dispatchers.Default) {
            additions.map { item ->
                val title = normalizeIptvSearch(item.title)
                IptvVodSearchRecord(
                    source = source,
                    item = item,
                    title = title,
                    titleWords = title.split(WORD_SEPARATOR).filter(String::isNotBlank),
                    category = normalizeIptvSearch(item.categoryName),
                    sourceName = normalizeIptvSearch(source.searchDisplayName()),
                    plot = normalizeIptvSearch(item.plot),
                )
            }
        }
        iptvVodSearchIndex = iptvVodSearchIndex + records
        updateIptvSearchRows(_uiState.value.query)
    }

    private fun updateIptvSearchRows(rawQuery: String) {
        iptvSearchJob?.cancel()
        val query = normalizeIptvSearch(rawQuery.trim())
        if (query.length < MIN_IPTV_SEARCH_QUERY_LENGTH) {
            iptvChannelsBySearchId = emptyMap()
            _uiState.update { it.copy(iptvCatalogRows = emptyList()) }
            return
        }

        val vodSearchIndex = iptvVodSearchIndex
        val channelSearchIndex = liveTvSearchIndex
        val sources = iptvSourcesById
        val hiddenCategories = hiddenIptvVodCategoryKeys
        val movieLabel = context.getString(R.string.nav_movies)
        val seriesLabel = context.getString(R.string.nav_series)
        val queryWords = query.split(WORD_SEPARATOR).filter(String::isNotBlank)
        iptvSearchJob = viewModelScope.launch {
            val (rows, channelsBySearchId) = withContext(Dispatchers.Default) {
                val coroutineContext = currentCoroutineContext()
                val vodComparator = compareBy<Triple<LiveTvSource, LiveTvVodItem, Pair<Int, Int>>>(
                    { it.third.first }, { it.third.second }
                )
                val rankedMatches = linkedMapOf<Pair<String, String>, MutableList<Triple<LiveTvSource, LiveTvVodItem, Pair<Int, Int>>>>()
                vodSearchIndex.forEachIndexed { index, record ->
                    if (index and 0xff == 0) coroutineContext.ensureActive()
                    val item = record.item
                    val categoryKey = com.nuvio.tv.reshaped.livetv.LiveTvRepository
                        .vodCategoryHideKey(item.type, item.sourceId, item.categoryId)
                    if (categoryKey in hiddenCategories) return@forEachIndexed
                    val rank = iptvMatchRank(record, query, queryWords) ?: return@forEachIndexed
                    val candidate = Triple(record.source, item, rank to index)
                    val matches = rankedMatches.getOrPut(item.sourceId to item.type) { mutableListOf() }
                    val foundAt = matches.binarySearch(candidate, vodComparator)
                    val insertAt = if (foundAt < 0) -foundAt - 1 else foundAt
                    if (insertAt < MAX_IPTV_SEARCH_RESULTS) {
                        matches.add(insertAt, candidate)
                        if (matches.size > MAX_IPTV_SEARCH_RESULTS) matches.removeAt(matches.lastIndex)
                    }
                }

                val vodRows = buildList {
                    listOf("movie" to movieLabel, "series" to seriesLabel).forEach { (type, typeLabel) ->
                        sources.values.forEach { source ->
                            val matches = rankedMatches[source.id to type]
                                .orEmpty()
                                .map { (_, item, _) -> item.toSearchPreview() }
                            if (matches.isNotEmpty()) {
                                val sourceLabel = source.searchDisplayName()
                                add(
                                    CatalogRow(
                                        addonId = "$IPTV_SEARCH_ADDON_PREFIX${source.id}",
                                        addonName = "IPTV",
                                        addonBaseUrl = "",
                                        catalogId = "search_$type",
                                        catalogName = "$typeLabel · $sourceLabel",
                                        type = ContentType.fromString(type),
                                        rawType = type,
                                        items = matches,
                                        hasMore = false,
                                        supportsSkip = false
                                    )
                                )
                            }
                        }
                    }
                }

                val channelComparator = compareBy<Pair<LiveChannelSearchRecord, Pair<Int, Int>>>(
                    { it.second.first }, { it.second.second }
                )
                val channelMatches = linkedMapOf<String, MutableList<Pair<LiveChannelSearchRecord, Pair<Int, Int>>>>()
                channelSearchIndex.forEachIndexed { index, record ->
                    if (index and 0xff == 0) coroutineContext.ensureActive()
                    val rank = liveChannelMatchRank(record, query) ?: return@forEachIndexed
                    val candidate = record to (rank to index)
                    val matches = channelMatches.getOrPut(record.channel.sourceId) { mutableListOf() }
                    val foundAt = matches.binarySearch(candidate, channelComparator)
                    val insertAt = if (foundAt < 0) -foundAt - 1 else foundAt
                    if (insertAt < MAX_IPTV_SEARCH_RESULTS) {
                        matches.add(insertAt, candidate)
                        if (matches.size > MAX_IPTV_SEARCH_RESULTS) matches.removeAt(matches.lastIndex)
                    }
                }
                val foundChannels = LinkedHashMap<String, LiveTvChannel>()
                val channelRows = buildList {
                    channelMatches.forEach { (sourceId, matches) ->
                        val items = matches
                            .map { (record, _) ->
                                foundChannels[record.searchId] = record.channel
                                record.channel.toSearchPreview(record.searchId)
                            }
                        if (items.isNotEmpty()) {
                            val sourceLabel = channelMatches[sourceId]
                                ?.firstOrNull()
                                ?.first
                                ?.sourceLabel
                                ?: "TV"
                            add(
                                CatalogRow(
                                    addonId = "${IPTV_SEARCH_ADDON_PREFIX}live:$sourceId",
                                    addonName = "IPTV",
                                    addonBaseUrl = "",
                                    catalogId = "live_channels",
                                    catalogName = "TV · $sourceLabel",
                                    type = ContentType.CHANNEL,
                                    rawType = "channel",
                                    items = items,
                                    hasMore = false,
                                    supportsSkip = false
                                )
                            )
                        }
                    }
                }
                (channelRows + vodRows) to foundChannels
            }
            if (normalizeIptvSearch(_uiState.value.query.trim()) != query) return@launch
            iptvChannelsBySearchId = channelsBySearchId
            _uiState.update { it.copy(iptvCatalogRows = rows) }
        }
    }

    private fun liveChannelMatchRank(record: LiveChannelSearchRecord, query: String): Int? {
        val title = record.title
        when {
            title == query -> return 0
            title.startsWith(query) -> return 1
            title.contains(query) -> return 2
        }
        if (record.tvgName.contains(query)) return 3
        if (record.group.contains(query)) return 4
        if (record.sourceName.contains(query)) return 5
        if (record.tvgId.contains(query)) return 6
        return null
    }

    private fun liveTvSearchResultId(index: Int): String = IPTV_LIVE_CHANNEL_ID_PREFIX + index

    private fun LiveTvChannel.toSearchPreview(searchId: String): MetaPreview = MetaPreview(
        id = searchId,
        type = ContentType.CHANNEL,
        rawType = "channel",
        name = name,
        poster = logoUrl?.takeIf(String::isNotBlank),
        posterShape = PosterShape.SQUARE,
        background = null,
        logo = null,
        description = group.takeIf(String::isNotBlank),
        releaseInfo = null,
        imdbRating = null,
        genres = emptyList(),
        rawPosterUrl = logoUrl?.takeIf(String::isNotBlank)
    )

    private fun iptvMatchRank(record: IptvVodSearchRecord, query: String, queryWords: List<String>): Int? {
        val title = record.title
        when {
            title == query -> return 0
            title.startsWith(query) -> return 1
            title.contains(query) -> return 2
        }
        if (queryWords.size > 1 && queryWords.all { word -> record.titleWords.any { it.startsWith(word) } }) return 3
        if (record.category.contains(query)) return 4
        if (record.sourceName.contains(query)) return 5
        if (record.plot.contains(query)) return 6
        return null
    }

    private fun normalizeIptvSearch(value: String): String =
        Normalizer.normalize(value, Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .lowercase(Locale.ROOT)

    private fun LiveTvSource.searchDisplayName(): String {
        name.trim().takeIf(String::isNotBlank)?.let { return it }
        val address = when (type) {
            LiveTvSourceType.M3u -> url
            LiveTvSourceType.Xtream -> xtream.serverUrl
            LiveTvSourceType.Stalker -> stalker.portalUrl
        }
        return address.substringAfter("://", address)
            .substringBefore('/')
            .substringBefore('?')
            .substringAfterLast('@')
            .ifBlank { "IPTV" }
    }

    private fun LiveTvVodItem.toSearchPreview(): MetaPreview {
        val contentType = ContentType.fromString(type)
        val poster = this.posterUrl.takeIf(String::isNotBlank)
        return MetaPreview(
            id = id,
            type = contentType,
            rawType = type,
            name = title,
            poster = poster,
            posterShape = PosterShape.POSTER,
            background = null,
            logo = null,
            description = plot.takeIf(String::isNotBlank),
            releaseInfo = year.takeIf(String::isNotBlank),
            imdbRating = null,
            genres = emptyList(),
            rawPosterUrl = poster
        )
    }

    private val metaPrefetchedIds: MutableSet<String> = java.util.concurrent.ConcurrentHashMap.newKeySet()
    private var metaPrefetchJob: Job? = null

    /**
     * Prefetch meta from addons in background when an item receives focus.
     * Warms the MetaRepository cache so the detail screen loads instantly.
     * Debounced to avoid flooding the network during rapid scrolling.
     */
    fun prefetchMetaOnFocus(id: String, type: String) {
        if (id.isBlank() || id in metaPrefetchedIds) return
        metaPrefetchJob?.cancel()
        metaPrefetchJob = viewModelScope.launch {
            delay(150)
            if (id in metaPrefetchedIds) return@launch
            metaPrefetchedIds.add(id)
            metaRepository.getMetaFromAllAddons(type = type, id = id)
                .first { it !is com.nuvio.tv.core.network.NetworkResult.Loading }
            watchProgressRepository.getAllEpisodeProgress(id.substringBefore(":")).first()
        }
    }

    /**
     * Returns the cached backdrop URL from a previously prefetched meta, or null.
     */
    fun getCachedBackdrop(id: String, type: String): String? {
        return metaRepository.getCachedMeta(type, id)?.backdropUrl
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> onQueryChanged(event.query)
            SearchEvent.SubmitSearch -> submitSearch()
            SearchEvent.RememberSearchFromTextInput -> rememberSearchFromTextInput()
            SearchEvent.ClearRecentSearches -> clearRecentSearches()
            is SearchEvent.RemoveRecentSearch -> removeRecentSearch(event.query)
            is SearchEvent.LoadMoreCatalog -> loadMoreCatalogItems(
                catalogId = event.catalogId,
                addonId = event.addonId,
                type = event.type
            )
            is SearchEvent.SelectDiscoverType -> selectDiscoverType(event.type)
            is SearchEvent.SelectDiscoverCatalog -> selectDiscoverCatalog(event.catalogKey)
            is SearchEvent.SelectDiscoverGenre -> selectDiscoverGenre(event.genre)
            SearchEvent.LoadNextDiscoverResults -> loadNextDiscoverResults()
            SearchEvent.Retry -> {
                // An explicit retry must refetch even though nothing about the request changed.
                lastRequestKey = null
                lastCompletedRequestKey = null
                cancelSearchRun()
                performSearch(uiState.value.submittedQuery.ifBlank { uiState.value.query })
            }
        }
    }

    private fun onQueryChanged(query: String) {
        _uiState.update {
            val trimmedInput = query.trim()
            // Narrow the strip to what still matches before anything is fetched, so a letter
            // that rules a title out drops it on that keystroke rather than a fetch later.
            // Narrowing does not clear the strip when every current title stops matching. The
            // fetch stays authoritative for an empty result.
            val narrowed = rankedSuggestions(it.suggestions, trimmedInput.lowercase())
            it.copy(
                query = query,
                suggestions = if (narrowed.isEmpty()) it.suggestions else narrowed,
                error = null,
                isSearching = false,
                // Keep whatever is on screen while a keystroke waits to run. Clearing here flashed
                // the no-results state on every letter, because on a remote each letter outlasts the
                // debounce. The screen renders skeleton rows for this window instead.
                catalogRows = if (trimmedInput.length < MIN_SEARCH_QUERY_LENGTH) emptyList() else it.catalogRows
            )
        }
        updateIptvSearchRows(query)
        if (query.isBlank()) {
            stopLiveTvSearch()
        } else {
            if (liveTvSearchScreenActive) startLiveTvSearch(activeProfileId)
            loadMissingIptvVodCatalogs(activeProfileId)
        }

        // Drop in-flight requests for the previous keystroke before scheduling the next run.
        cancelSearchRun()

        // Live search: results follow what you type, like mobile. Debounced because each run hits
        // every enabled addon catalog.
        liveSearchJob?.cancel()
        val trimmed = query.trim()
        if (trimmed.isNotEmpty()) {
            liveSearchJob = viewModelScope.launch {
                kotlinx.coroutines.delay(LIVE_SEARCH_DEBOUNCE_MS)
                performSearch(query, keepSuggestions = true)
            }
        } else {
            // Emptying the field has to retire the submitted query too. Leaving it set kept the
            // screen in its results state with nothing to show, instead of falling back to recent
            // searches, until the screen was rebuilt by navigating away and back.
            performSearch(query)
        }

        fetchSuggestions(trimmed)
    }

    /** Match rank for [title], lower being better, or null when it does not match [queryLower]. */
    private fun suggestionRank(title: String, queryLower: String): Int? {
        val titleLower = title.lowercase()
        if (titleLower == queryLower) return 0
        if (titleLower.startsWith(queryLower)) return 1
        if (titleLower.contains(queryLower)) return 2

        // Allow multi-word matches such as "wolf wall" -> "The Wolf of Wall Street".
        // Each query word must consume a different title word.
        val queryWords = queryLower.split(WORD_SEPARATOR).filter { it.isNotEmpty() }
        if (queryWords.size < 2) return null
        val unmatchedTitleWords = titleLower.split(WORD_SEPARATOR).filterTo(mutableListOf()) { it.isNotEmpty() }
        val everyWordMatches = queryWords.all { word ->
            val index = unmatchedTitleWords.indexOfFirst { it.startsWith(word) }
            if (index >= 0) unmatchedTitleWords.removeAt(index)
            index >= 0
        }
        return if (everyWordMatches) 3 else null
    }

    /**
     * The strip contents for [names], best match first, capped at [MAX_SUGGESTIONS].
     *
     * Titles of the same rank keep the order of [names], which is the addon's own relevance order.
     * Sorting them alphabetically put five titles ahead of "Jurassic Park" for "juras", and of
     * "Slow Horses" for "slo", past the few completions a TV keyboard shows.
     */
    private fun rankedSuggestions(names: Collection<String>, queryLower: String): List<String> =
        names
            .mapNotNull { name -> suggestionRank(name, queryLower)?.let { name to it } }
            .sortedBy { it.second }
            .map { it.first }
            .take(MAX_SUGGESTIONS)

    /**
     * Every catalog's titles, taken one position at a time: each catalog's first title, then each
     * one's second, and so on, in catalog order. Each catalog's best matches lead, and the result
     * does not depend on which catalog happened to answer first.
     */
    private fun mergedCatalogNames(namesByCatalog: Map<Int, List<String>>, catalogCount: Int): List<String> {
        val catalogs = (0 until catalogCount).mapNotNull { namesByCatalog[it] }
        val merged = LinkedHashSet<String>()
        for (position in 0 until (catalogs.maxOfOrNull { it.size } ?: 0)) {
            catalogs.forEach { names -> names.getOrNull(position)?.let(merged::add) }
        }
        return merged.toList()
    }

    private fun fetchSuggestions(query: String) {
        suggestionJob?.cancel()

        if (query.length < MIN_SEARCH_QUERY_LENGTH) {
            _uiState.update { it.copy(suggestions = emptyList()) }
            return
        }

        // Already searched, so a fetch would repeat itself and the strip is current. Leave it
        // standing: live search submits as the user types, so typing a space between words
        // trims back to the submitted query and lands here mid-query.
        if (query == _uiState.value.submittedQuery.trim() && _uiState.value.catalogRows.isNotEmpty()) {
            return
        }

        suggestionJob = viewModelScope.launch {
            kotlinx.coroutines.delay(SUGGESTION_DEBOUNCE_MS)

            val addons = try {
                searchAddons()
            } catch (_: Exception) {
                return@launch
            }

            val allTargets = buildSearchTargets(addons)
            val firstAddonId = allTargets.firstOrNull()?.first?.id
            val searchTargets = if (firstAddonId != null) allTargets.filter { it.first.id == firstAddonId } else emptyList()
            if (searchTargets.isEmpty()) {
                _uiState.update { it.copy(suggestions = emptyList()) }
                return@launch
            }

            // Each catalog's titles in the order it returned them, keyed by its place in searchTargets.
            val namesByCatalog = java.util.concurrent.ConcurrentHashMap<Int, List<String>>()
            val queryLower = query.lowercase()
            val suggestionJobs = searchTargets.mapIndexed { catalogIndex, (addon, catalog) ->
                launch {
                    try {
                        catalogRepository.getCatalog(
                            addonBaseUrl = addon.baseUrl,
                            addonId = addon.id,
                            addonName = addon.displayName,
                            catalogId = catalog.id,
                            catalogName = catalog.name,
                            type = catalog.apiType,
                            skip = 0,
                            skipStep = 100,
                            extraArgs = mapOf("search" to query),
                            supportsSkip = false,
                            posterScreen = com.nuvio.tv.core.poster.CustomPosterScreen.SEARCH
                        ).collect { result ->
                            if (result is NetworkResult.Success && _uiState.value.query.trim() == query) {
                                val names = result.data.items.map { it.name }
                                val previous = namesByCatalog.put(catalogIndex, names)
                                // Catalog results arrive independently and are merged, so a batch
                                // whose titles all fail the filter ranks to nothing while the batch
                                // holding the match is still in flight. Only the settle below may
                                // empty the strip: an empty push tells the keyboard there are no
                                // completions, and it does not always take them back when the next
                                // batch lands.
                                if (names != previous) {
                                    val ranked = rankedSuggestions(
                                        mergedCatalogNames(namesByCatalog, searchTargets.size),
                                        queryLower
                                    )
                                    if (ranked.isNotEmpty()) {
                                        _uiState.update { it.copy(suggestions = ranked) }
                                    }
                                }
                            }
                        }
                    } catch (e: CancellationException) {
                        // The settle below treats joinAll() as "collection is over".
                        throw e
                    } catch (_: Exception) {
                        // Ignore per-catalog errors for suggestions
                    }
                }
            }

            suggestionJobs.joinAll()

            // Every catalog job has completed, so this is the first point the query is known
            // to have no suggestions. Until here the strip keeps the previous query's titles
            // rather than blinking on every keystroke. It must be cleared here or it would go
            // on captioning text the field no longer contains.
            if (_uiState.value.query.trim() == query) {
                val ranked = rankedSuggestions(mergedCatalogNames(namesByCatalog, searchTargets.size), queryLower)
                _uiState.update { it.copy(suggestions = ranked) }
            }
        }
    }

    private fun submitSearch() {
        // An explicit submit just skips the remaining debounce; the live run would land anyway.
        liveSearchJob?.cancel()
        performSearch(_uiState.value.query, rememberToHistory = true)
    }

    /**
     * Moving from the text input into the results confirms the current query the same way
     * Done/submit does. Live search may already have results on screen without an explicit
     * submit; saving here
     * avoids losing useful history while still not recording every keystroke prefix.
     */
    private fun rememberSearchFromTextInput() {
        val state = _uiState.value
        val query = state.submittedQuery.trim().ifBlank { state.query.trim() }
        if (query.length < MIN_SEARCH_QUERY_LENGTH) return
        val hasRealResults = state.catalogRows.any { row ->
            row.items.any { item -> !item.id.startsWith("__placeholder_") }
        } || state.iptvCatalogRows.any { row ->
            row.items.any { item -> !item.id.startsWith("__placeholder_") }
        } || catalogsMap.values.any { row ->
            row.items.any { item -> !item.id.startsWith("__placeholder_") }
        }
        if (!hasRealResults) return
        viewModelScope.launch {
            searchHistoryDataStore.saveRecentSearch(query, MAX_RECENT_SEARCHES)
        }
    }

    private fun clearRecentSearches() {
        viewModelScope.launch {
            searchHistoryDataStore.clearRecentSearches()
        }
    }

    private fun removeRecentSearch(query: String) {
        viewModelScope.launch {
            searchHistoryDataStore.removeRecentSearch(query)
        }
    }

    private fun resetCatalogAccumulator() {
        catalogsMap.clear()
        catalogOrder.clear()
        hasRenderedFirstCatalog = false
        pendingCatalogResponses = 0
    }

    private fun cancelSearchRun() {
        searchGeneration++
        searchRunJob?.cancel()
        searchRunJob = null
        activeSearchJobs.forEach { it.cancel() }
        activeSearchJobs = emptyList()
        activeLoadMoreJobs.forEach { it.cancel() }
        activeLoadMoreJobs.clear()
        activeSearchQuery = null
    }

    /**
     * Identifies a search by everything that changes what it would return: the query, the released
     * filter, and the exact set of catalogs it would hit. Enabling an addon or flipping the filter
     * changes the key, so those still refetch.
     */
    private fun buildRequestKey(
        query: String,
        searchTargets: List<Pair<Addon, CatalogDescriptor>>
    ): String = buildString {
        append(query.lowercase())
        append('|')
        append(hideUnreleasedContent)
        append('|')
        append(
            searchTargets.joinToString(separator = "|") { (addon, catalog) ->
                "${addon.baseUrl}:${catalog.apiType}:${catalog.id}"
            }
        )
    }


    /**
     * @param keepSuggestions live search runs this on every keystroke, while the field is still
     * being typed into and the suggestion strip is the whole point. Those runs leave the strip
     * alone. A submit or a retry replaces the screen with results, which retires it.
     */
    private fun performSearch(
        rawQuery: String,
        rememberToHistory: Boolean = false,
        keepSuggestions: Boolean = false
    ) {
        val query = rawQuery.trim()
        updateIptvSearchRows(rawQuery)
        if (!keepSuggestions) {
            suggestionJob?.cancel()
        }
        _uiState.update {
            it.copy(
                submittedQuery = query,
                query = rawQuery,
                suggestions = if (keepSuggestions) it.suggestions else emptyList()
            )
        }

        if (query.isEmpty()) {
            cancelSearchRun()
            catalogRowsUpdateJob?.cancel()
            resetCatalogAccumulator()
            lastRequestKey = null
            lastCompletedRequestKey = null
            _uiState.update {
                it.copy(
                    isSearching = false,
                    error = null,
                    catalogRows = emptyList()
                )
            }
            ensureDiscoverLoaded()
            return
        }

        // Submit can immediately follow the debounced live-search launch. Reuse an active run for
        // the same query, but cancel a different query's entire scope before starting this one.
        if (activeSearchQuery == query && searchRunJob?.isActive == true) return
        cancelSearchRun()
        val generation = searchGeneration
        activeSearchQuery = query

        val job = viewModelScope.launch {
            val addons = try {
                searchAddons()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (generation == searchGeneration && activeSearchQuery == query) {
                    _uiState.update { it.copy(isSearching = false, error = e.message ?: context.getString(com.nuvio.tv.R.string.search_error_load_addons_failed)) }
                }
                return@launch
            }

            if (generation != searchGeneration || activeSearchQuery != query) return@launch

            val searchTargets = buildSearchTargets(addons)

            // Same query against the same catalogs, and that run either finished or is still
            // arriving, so there is nothing new to fetch. Without this, pressing Done after live
            // search had already run the query tore the rows down and refetched everything, and
            // deleting a letter then retyping it did the same. A run that was cancelled part way
            // is deliberately not counted, so it gets to finish rather than staying half filled.
            val requestKey = buildRequestKey(query, searchTargets)
            val alreadySatisfied = requestKey == lastRequestKey &&
                (requestKey == lastCompletedRequestKey || activeSearchJobs.any { it.isActive })
            if (alreadySatisfied) {
                // An explicit submit that lands on a query the live search already finished still
                // counts as a search the user confirmed, so remember it instead of skipping out
                // of the history save below.
                if (rememberToHistory && catalogsMap.values.any { row -> row.items.isNotEmpty() }) {
                    viewModelScope.launch {
                        searchHistoryDataStore.saveRecentSearch(query, MAX_RECENT_SEARCHES)
                    }
                }
                return@launch
            }
            lastRequestKey = requestKey

            // Committed to a new run: drop the previous query's work and accumulated rows.
            activeSearchJobs.forEach { it.cancel() }
            activeSearchJobs = emptyList()
            catalogRowsUpdateJob?.cancel()
            resetCatalogAccumulator()

            // Rows are left alone here. Clearing them produced an empty frame between the old
            // results and the placeholders below, which is the flash this screen used to show.
            _uiState.update { it.copy(isSearching = true, error = null, installedAddons = addons) }

            if (searchTargets.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        error = context.getString(R.string.search_error_no_catalogs),
                        catalogRows = emptyList()
                    )
                }
                return@launch
            }

            // Preserve addon manifest order.
            searchTargets.forEach { (addon, catalog) ->
                val key = catalogKey(
                    addonId = addon.id,
                    addonBaseUrl = addon.baseUrl,
                    type = catalog.apiType,
                    catalogId = catalog.id
                )
                if (key !in catalogOrder) {
                    catalogOrder.add(key)
                }
            }

            // Emit placeholder rows with shimmer items so the UI shows
            // skeleton rows immediately instead of a spinner.
            val placeholderRows = searchTargets.map { (addon, catalog) ->
                val key = catalogKey(
                    addonId = addon.id,
                    addonBaseUrl = addon.baseUrl,
                    type = catalog.apiType,
                    catalogId = catalog.id
                )
                val fakeItems = (0 until 8).map { i ->
                    MetaPreview(
                        id = "__placeholder_${key}_$i",
                        type = ContentType.fromString(catalog.apiType),
                        rawType = catalog.apiType,
                        name = " ",
                        poster = PLACEHOLDER_IMAGE_URL,
                        posterShape = PosterShape.POSTER,
                        background = null,
                        logo = null,
                        description = null,
                        releaseInfo = " ",
                        imdbRating = null,
                        genres = emptyList()
                    )
                }
                CatalogRow(
                    addonId = addon.id,
                    addonName = addon.displayName,
                    addonBaseUrl = addon.baseUrl,
                    catalogId = catalog.id,
                    catalogName = catalog.name,
                    type = ContentType.fromString(catalog.apiType),
                    rawType = catalog.apiType,
                    items = fakeItems,
                    isLoading = true,
                    hasMore = false,
                    currentPage = 0,
                    supportsSkip = false,
                    skipStep = 0,
                    extraArgs = emptyMap()
                )
            }
            // Only shimmer when there is nothing real to look at. If the previous query's results
            // are still up, they stay until this query's results replace them, so refining a search
            // is a single swap rather than results -> shimmer -> results on every letter.
            _uiState.update { state ->
                val showingRealRows = state.catalogRows.any { row ->
                    row.items.firstOrNull()?.id?.startsWith("__placeholder_") != true
                }
                if (showingRealRows) state else state.copy(catalogRows = placeholderRows)
            }

            val jobs = searchTargets.map { (addon, catalog) ->
                launch {
                    loadCatalog(addon, catalog, query, generation)
                }
            }
            pendingCatalogResponses = jobs.size
            activeSearchJobs = jobs

            // Wait for all jobs to complete so we can stop showing the global loading state.
            try {
                jobs.joinAll()
            } finally {
                if (
                    generation == searchGeneration &&
                    activeSearchQuery == query &&
                    uiState.value.submittedQuery.trim() == query
                ) {
                    lastCompletedRequestKey = requestKey
                    _uiState.update { it.copy(isSearching = false) }
                    // Only explicit submit (or moving into results from the text input — see
                    // rememberSearchFromTextInput)
                    // writes history. Live search fires per keystroke and would otherwise record
                    // every prefix ("do", "dog") even when the user never confirmed the search.
                    if (rememberToHistory && catalogsMap.values.any { row -> row.items.isNotEmpty() }) {
                        viewModelScope.launch {
                            searchHistoryDataStore.saveRecentSearch(query, MAX_RECENT_SEARCHES)
                        }
                    }
                }
            }
        }
        searchRunJob = job
    }

    private suspend fun loadCatalog(
        addon: Addon,
        catalog: CatalogDescriptor,
        query: String,
        generation: Long
    ) {
        val supportsSkip = catalog.supportsExtra("skip")
        val skipStep = catalog.skipStep()
        catalogRepository.getCatalog(
            addonBaseUrl = addon.baseUrl,
            addonId = addon.id,
            addonName = addon.displayName,
            catalogId = catalog.id,
            catalogName = catalog.name,
            type = catalog.apiType,
            skip = 0,
            skipStep = skipStep,
            extraArgs = mapOf("search" to query),
            supportsSkip = supportsSkip,
            posterScreen = com.nuvio.tv.core.poster.CustomPosterScreen.SEARCH
        ).collect { result ->
            when (result) {
                is NetworkResult.Success -> {
                    if (!isCurrentSearch(generation, query)) return@collect
                    val key = catalogKey(
                        addonId = addon.id,
                        addonBaseUrl = addon.baseUrl,
                        type = catalog.apiType,
                        catalogId = catalog.id
                    )
                    catalogsMap[key] = result.data
                    pendingCatalogResponses = (pendingCatalogResponses - 1).coerceAtLeast(0)
                    scheduleCatalogRowsUpdate()
                }
                is NetworkResult.Error -> {
                    if (!isCurrentSearch(generation, query)) return@collect
                    pendingCatalogResponses = (pendingCatalogResponses - 1).coerceAtLeast(0)
                    // Ignore per-catalog errors unless we have nothing to show.
                    if (catalogsMap.isEmpty()) {
                        _uiState.update { it.copy(error = result.message ?: context.getString(com.nuvio.tv.R.string.search_error_failed)) }
                    }
                    scheduleCatalogRowsUpdate()
                }
                NetworkResult.Loading -> {
                    // No-op; screen shows global loading when empty.
                }
            }
        }
    }

    private fun isCurrentSearch(generation: Long, query: String): Boolean =
        generation == searchGeneration && uiState.value.submittedQuery.trim() == query

    /**
     * Pages one row of the current search. The page belongs to the search run that created it:
     * it is keyed on [SearchUiState.submittedQuery] rather than the live field, and every
     * assignment back into [catalogsMap] is gated on that run still being current, so a late page
     * cannot merge into a different query's rows.
     */
    private fun loadMoreCatalogItems(catalogId: String, addonId: String, type: String) {
        val (key, currentRow) = catalogsMap.entries.firstOrNull { (_, row) ->
            row.addonId == addonId && row.apiType == type && row.catalogId == catalogId
        }?.let { it.key to it.value } ?: return

        if (currentRow.isLoading || !currentRow.hasMore) {
            return
        }

        val generation = searchGeneration
        val query = uiState.value.submittedQuery.trim()
        if (query.isBlank()) {
            return
        }

        catalogsMap[key] = currentRow.copy(isLoading = true)
        scheduleCatalogRowsUpdate()

        val job = viewModelScope.launch {
            val addon = uiState.value.installedAddons.find { it.id == addonId && it.baseUrl == currentRow.addonBaseUrl }
                ?: uiState.value.installedAddons.find { it.id == addonId } ?: run {
                clearRowLoading(key, generation, query)
                return@launch
            }

            val nextSkip = currentRow.nextCatalogSkip()
            catalogRepository.getCatalog(
                addonBaseUrl = addon.baseUrl,
                addonId = addon.id,
                addonName = addon.displayName,
                catalogId = catalogId,
                catalogName = currentRow.catalogName,
                type = currentRow.apiType,
                skip = nextSkip,
                skipStep = currentRow.skipStep,
                extraArgs = mapOf("search" to query),
                supportsSkip = currentRow.supportsSkip,
                posterScreen = com.nuvio.tv.core.poster.CustomPosterScreen.SEARCH
            ).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        if (!isCurrentSearch(generation, query)) return@collect
                        val latestRow = catalogsMap[key] ?: return@collect
                        catalogsMap[key] = latestRow.mergeCatalogPage(result.data)
                        scheduleCatalogRowsUpdate()
                    }
                    is NetworkResult.Error -> {
                        clearRowLoading(key, generation, query)
                    }
                    NetworkResult.Loading -> Unit
                }
            }
        }
        activeLoadMoreJobs.add(job)
        job.invokeOnCompletion { activeLoadMoreJobs.remove(job) }
    }

    /** Drops the loading flag for a row, but only while its search run is still current. */
    private fun clearRowLoading(key: String, generation: Long, query: String) {
        if (!isCurrentSearch(generation, query)) return
        val row = catalogsMap[key] ?: return
        if (!row.isLoading) return
        catalogsMap[key] = row.copy(isLoading = false)
        scheduleCatalogRowsUpdate()
    }

    private fun scheduleCatalogRowsUpdate() {
        catalogRowsUpdateJob?.cancel()
        catalogRowsUpdateJob = viewModelScope.launch {
            if (!hasRenderedFirstCatalog && catalogsMap.isNotEmpty()) {
                hasRenderedFirstCatalog = true
                updateCatalogRowsNow()
                return@launch
            }
            val debounceMs = when {
                pendingCatalogResponses > 5 -> 220L
                pendingCatalogResponses > 0 -> 140L
                else -> 90L
            }
            kotlinx.coroutines.delay(debounceMs)
            updateCatalogRowsNow()
        }
    }

    private fun updateCatalogRowsNow() {
        _uiState.update { state ->
            val orderedRows = catalogOrder.map { key ->
                catalogsMap[key]
                    ?: state.catalogRows.find {
                        it.stableKey() == key
                    }
            }.filterNotNull().filter { row ->
                // Keep placeholder rows (shimmer) and rows with real items.
                // Drop rows that came back empty from the API.
                val isPlaceholder = row.isLoading &&
                    row.items.firstOrNull()?.id?.startsWith("__placeholder_") == true
                isPlaceholder || row.items.isNotEmpty()
            }
            val filteredRows = if (hideUnreleasedContent) {
                val today = LocalDate.now()
                orderedRows.map { row ->
                    if (row.isLoading && row.items.firstOrNull()?.id?.startsWith("__placeholder_") == true) {
                        row
                    } else {
                        row.filterReleasedItems(today)
                    }
                }
            } else {
                orderedRows
            }
            state.copy(
                catalogRows = filteredRows
            )
        }
    }

    private suspend fun searchAddons(): List<Addon> =
        addonRepository.getInstalledAddons().first().enabledAddons() + serverCatalog.searchAddons.first()

    private suspend fun loadDiscoverCatalogs() {
        if (_uiState.value.discoverLocation == DiscoverLocation.OFF) return
        _uiState.update { it.copy(discoverLoading = true) }
        val addons = try {
            searchAddons()
        } catch (_: Exception) {
            _uiState.update { it.copy(discoverInitialized = true, discoverLoading = false) }
            return
        }

        val discoverCatalogs = addons.flatMap { addon ->
            addon.catalogs
                .filter { catalog ->
                    !(catalog.supportsExtra("search") &&
                        catalog.extra.any { it.name.equals("search", ignoreCase = true) && it.isRequired })
                }
                .map { catalog ->
                    val genres = catalog.extra
                        .firstOrNull { it.name.equals("genre", ignoreCase = true) }
                        ?.options
                        .orEmpty()
                    DiscoverCatalog(
                        key = catalogRowLegacyKey(addon.id, catalog.apiType, catalog.id),
                        addonId = addon.id,
                        addonName = addon.displayName,
                        addonBaseUrl = addon.baseUrl,
                        catalogId = catalog.id,
                        catalogName = catalog.name,
                        type = catalog.apiType,
                        genres = genres,
                        supportsSkip = catalog.supportsExtra("skip"),
                        skipStep = catalog.skipStep()
                    )
                }
        }

        val selectedCatalog = resolveDiscoverCatalog(
            catalogs = discoverCatalogs,
            preferredKey = discoverSelectionDataStore.getSelectedCatalogKey(),
            currentKey = _uiState.value.selectedDiscoverCatalogKey
        )
        val selectedType = selectedCatalog?.type ?: "movie"
        val selectedGenre: String? = null

        _uiState.update {
            it.copy(
                installedAddons = addons,
                discoverCatalogs = discoverCatalogs,
                selectedDiscoverType = selectedType,
                selectedDiscoverCatalogKey = selectedCatalog?.key,
                selectedDiscoverGenre = selectedGenre,
                discoverInitialized = true,
                discoverLoading = false,
                discoverResults = emptyList(),
                pendingDiscoverResults = emptyList(),
                discoverHasMore = true,
                discoverPage = 1
            )
        }
        selectedCatalog?.let { catalog ->
            viewModelScope.launch {
                discoverSelectionDataStore.setSelectedCatalogKey(catalog.key)
            }
        }
        fetchDiscoverContent(reset = true)
    }

    private fun selectDiscoverType(type: String) {
        val catalogs = _uiState.value.discoverCatalogs
        val selectedCatalog = pickDiscoverCatalog(
            catalogs = catalogs,
            selectedType = type,
            preferredKey = _uiState.value.selectedDiscoverCatalogKey
        )
        val selectedGenre: String? = null
        _uiState.update {
            it.copy(
                selectedDiscoverType = type,
                selectedDiscoverCatalogKey = selectedCatalog?.key,
                selectedDiscoverGenre = selectedGenre,
                discoverResults = emptyList(),
                pendingDiscoverResults = emptyList(),
                discoverPage = 1,
                discoverHasMore = true
            )
        }
        selectedCatalog?.let { catalog ->
            viewModelScope.launch {
                discoverSelectionDataStore.setSelectedCatalogKey(catalog.key)
            }
        }
        fetchDiscoverContent(reset = true)
    }

    private fun selectDiscoverCatalog(catalogKey: String) {
        val catalog = _uiState.value.discoverCatalogs.firstOrNull { it.key == catalogKey } ?: return
        _uiState.update {
            it.copy(
                selectedDiscoverCatalogKey = catalog.key,
                selectedDiscoverType = catalog.type,
                selectedDiscoverGenre = null,
                discoverResults = emptyList(),
                pendingDiscoverResults = emptyList(),
                discoverPage = 1,
                discoverHasMore = true
            )
        }
        viewModelScope.launch {
            discoverSelectionDataStore.setSelectedCatalogKey(catalog.key)
        }
        fetchDiscoverContent(reset = true)
    }

    private fun selectDiscoverGenre(genre: String?) {
        _uiState.update {
            it.copy(
                selectedDiscoverGenre = genre,
                discoverResults = emptyList(),
                pendingDiscoverResults = emptyList(),
                discoverPage = 1,
                discoverHasMore = true
            )
        }
        fetchDiscoverContent(reset = true)
    }

    private fun loadNextDiscoverResults() {
        if (_uiState.value.pendingDiscoverResults.isNotEmpty()) {
            showMoreDiscoverResults()
        } else {
            revealBatchAfterNextDiscoverFetch = true
            loadMoreDiscoverResults()
        }
    }

    private fun showMoreDiscoverResults() {
        val pending = _uiState.value.pendingDiscoverResults
        if (pending.isEmpty()) return
        val nextBatch = pending.take(DISCOVER_SHOW_MORE_BATCH)
        val remaining = pending.drop(DISCOVER_SHOW_MORE_BATCH)
        _uiState.update {
            it.copy(
                discoverResults = it.discoverResults + nextBatch,
                pendingDiscoverResults = remaining
            )
        }
    }

    private fun loadMoreDiscoverResults() {
        val state = _uiState.value
        if (state.query.trim().isNotEmpty()) return
        if (!state.discoverHasMore || state.discoverLoadingMore || state.pendingDiscoverResults.isNotEmpty()) return
        fetchDiscoverContent(reset = false)
    }

    private fun fetchDiscoverContent(reset: Boolean) {
        discoverJob?.cancel()
        discoverJob = viewModelScope.launch {
            val state = _uiState.value
            if (state.query.trim().isNotEmpty()) return@launch
            val selectedCatalog = state.discoverCatalogs.firstOrNull { it.key == state.selectedDiscoverCatalogKey }
                ?: return@launch

            if (reset) {
                revealBatchAfterNextDiscoverFetch = false
                _uiState.update {
                    it.copy(
                        discoverLoading = true,
                        discoverResults = emptyList(),
                        pendingDiscoverResults = emptyList(),
                        discoverPage = 1,
                        discoverHasMore = true
                    )
                }
            } else {
                _uiState.update { it.copy(discoverLoadingMore = true) }
            }

            val currentPage = if (reset) 1 else state.discoverPage + 1
            val skip = if (currentPage <= 1) 0 else (currentPage - 1) * selectedCatalog.skipStep
            val visibleCountBeforeRequest = state.discoverResults.size
            val extraArgs = buildMap<String, String> {
                state.selectedDiscoverGenre?.takeIf { it.isNotBlank() }?.let { put("genre", it) }
            }

            catalogRepository.getCatalog(
                addonBaseUrl = selectedCatalog.addonBaseUrl,
                addonId = selectedCatalog.addonId,
                addonName = selectedCatalog.addonName,
                catalogId = selectedCatalog.catalogId,
                catalogName = selectedCatalog.catalogName,
                type = selectedCatalog.type,
                skip = skip,
                skipStep = selectedCatalog.skipStep,
                extraArgs = extraArgs,
                supportsSkip = selectedCatalog.supportsSkip,
                posterScreen = com.nuvio.tv.core.poster.CustomPosterScreen.SEARCH
            ).collect { result ->
                if (_uiState.value.discoverLocation == DiscoverLocation.OFF) return@collect
                when (result) {
                    is NetworkResult.Success -> {
                        val incoming = result.data.items
                        val existing = if (reset) {
                            emptyList()
                        } else {
                            _uiState.value.discoverResults + _uiState.value.pendingDiscoverResults
                        }
                        val existingKeys = existing.asSequence()
                            .map { "${it.apiType}:${it.id}" }
                            .toSet()
                        val hasNewUniqueIncoming = incoming.any { item ->
                            "${item.apiType}:${item.id}" !in existingKeys
                        }
                        val merged = if (reset) incoming else (existing + incoming)
                        val rawDeduped = merged.distinctBy { "${it.apiType}:${it.id}" }
                        val deduped = if (hideUnreleasedContent) {
                            val today = LocalDate.now()
                            rawDeduped.filterNot { it.isUnreleased(today) }
                        } else {
                            rawDeduped
                        }
                        val shouldRevealBatch = !reset && revealBatchAfterNextDiscoverFetch
                        val visibleLimit = if (reset) {
                            DISCOVER_INITIAL_LIMIT
                        } else if (shouldRevealBatch) {
                            (visibleCountBeforeRequest + DISCOVER_SHOW_MORE_BATCH)
                                .coerceAtLeast(DISCOVER_INITIAL_LIMIT)
                        } else {
                            visibleCountBeforeRequest.coerceAtLeast(DISCOVER_INITIAL_LIMIT)
                        }
                        val visible = deduped.take(visibleLimit)
                        val pending = deduped.drop(visibleLimit)
                        val shouldStopPagination = !reset && !hasNewUniqueIncoming
                        _uiState.update {
                            it.copy(
                                discoverLoading = false,
                                discoverLoadingMore = false,
                                discoverResults = visible,
                                pendingDiscoverResults = pending,
                                discoverHasMore = if (shouldStopPagination) false else result.data.hasMore,
                                discoverPage = if (shouldStopPagination) it.discoverPage else currentPage
                            )
                        }
                        revealBatchAfterNextDiscoverFetch = false
                    }
                    is NetworkResult.Error -> {
                        revealBatchAfterNextDiscoverFetch = false
                        _uiState.update {
                            it.copy(
                                discoverLoading = false,
                                discoverLoadingMore = false,
                                discoverHasMore = false
                            )
                        }
                    }
                    NetworkResult.Loading -> Unit
                }
            }
        }
    }

    private fun pickDiscoverCatalog(
        catalogs: List<DiscoverCatalog>,
        selectedType: String,
        preferredKey: String?
    ): DiscoverCatalog? {
        val filtered = catalogs.filter { it.type == selectedType }
        return filtered.firstOrNull { it.key == preferredKey } ?: filtered.firstOrNull()
    }

    private fun buildSearchTargets(addons: List<Addon>): List<Pair<Addon, CatalogDescriptor>> {
        val allSearchTargets = addons.flatMap { addon ->
            addon.catalogs
                .filter { catalog -> catalog.isSearchable() }
                .map { catalog -> addon to catalog }
        }

        return allSearchTargets
    }

    /**
     * A catalog is only searchable if a search is all it needs. One that also requires something we
     * cannot supply, a mandatory genre for instance, answers with an error or nothing at all, so
     * querying it just costs a request per keystroke and leaves a row that never fills in.
     */
    private fun CatalogDescriptor.isSearchable(): Boolean =
        supportsExtra("search") &&
            extra.none { property ->
                property.isRequired && !property.name.equals("search", ignoreCase = true)
            }

    private fun catalogKey(addonId: String, addonBaseUrl: String, type: String, catalogId: String): String {
        return catalogRowStableKey(addonId, addonBaseUrl, type, catalogId)
    }
}

private fun searchFavoriteKey(itemId: String, itemType: String): String =
    "${itemType.trim().lowercase(Locale.ROOT)}|${itemId.trim().lowercase(Locale.ROOT)}"

private fun MetaPreview.toSearchLibraryEntryInput(addonBaseUrl: String): LibraryEntryInput {
    val year = Regex("(\\d{4})").find(releaseInfo ?: "")
        ?.groupValues
        ?.getOrNull(1)
        ?.toIntOrNull()
    val parsedIds = parseContentIds(id)
    return LibraryEntryInput(
        itemId = id,
        itemType = apiType,
        title = name,
        year = year,
        traktId = parsedIds.trakt,
        simklId = parsedIds.simkl,
        imdbId = parsedIds.imdb,
        tmdbId = parsedIds.tmdb,
        poster = poster,
        posterShape = posterShape,
        background = background,
        logo = logo,
        description = description,
        releaseInfo = releaseInfo,
        imdbRating = imdbRating,
        genres = genres,
        addonBaseUrl = addonBaseUrl
    )
}

internal fun resolveDiscoverCatalog(
    catalogs: List<DiscoverCatalog>,
    preferredKey: String?,
    currentKey: String?
): DiscoverCatalog? =
    catalogs.firstOrNull { it.key == preferredKey }
        ?: catalogs.firstOrNull { it.key == currentKey }
        ?: catalogs.firstOrNull()
