@file:OptIn(
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.tv.material3.ExperimentalTvMaterial3Api::class,
)

package com.nuvio.tv.ui.reshaped.livetv

import android.util.Log
import android.content.Context
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed as gridItemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed as rowItemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Card
import androidx.tv.material3.CardBorder
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Border
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import android.text.format.DateUtils
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import coil3.BitmapImage
import coil3.compose.AsyncImage
import com.nuvio.tv.R
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.data.trailer.TrailerPlaybackSource
import com.nuvio.tv.ui.components.EpisodeBackdropInfo
import com.nuvio.tv.ui.components.EpisodeBackdropLayout
import com.nuvio.tv.ui.components.EpisodeRailItem
import com.nuvio.tv.ui.components.TrailerPlayer
import com.nuvio.tv.ui.screens.home.ModernHeroGradientLayer
import com.nuvio.tv.reshaped.livetv.LiveTvRepository
import com.nuvio.tv.reshaped.livetv.LiveTvVodCache
import com.nuvio.tv.reshaped.livetv.LiveTvSource
import com.nuvio.tv.reshaped.livetv.LiveTvSourceType
import com.nuvio.tv.reshaped.livetv.normalizeLiveTvVodTitleForTmdb
import com.nuvio.tv.reshaped.livetv.LiveTvVodCategory
import com.nuvio.tv.reshaped.livetv.LiveTvVodItem
import com.nuvio.tv.reshaped.livetv.LiveTvVodPlayback
import com.nuvio.tv.reshaped.livetv.LIVE_TV_STREAM_HEADERS
import com.nuvio.tv.reshaped.livetv.LiveTvStalker
import com.nuvio.tv.reshaped.livetv.LiveTvXtream
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.theme.NuvioTheme
import com.nuvio.tv.ui.util.logoContrastTint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale

private enum class IptvVodPanel { Sources, Favorites, History }
private const val VOD_FOCUS_PREVIEW_DELAY_MS = 220L
private const val VOD_TRAILER_PREVIEW_DELAY_MS = 5_000L
private val VOD_SINGLE_POSTER_ROW_HEIGHT = 180.dp
private const val VOD_DETAILS_PANEL_WIDTH = 0.20f
private const val VOD_HERO_PANEL_WIDTH = 0.80f
private val VOD_HERO_START_PADDING = 22.dp
private const val VOD_GRID_COLUMNS = 6
private val VOD_GRID_HORIZONTAL_CONTENT_PADDING = 4.dp
private val VOD_GRID_VERTICAL_CONTENT_PADDING = 6.dp
private val VOD_GRID_ITEM_SPACING = 10.dp
private val VOD_POSTER_LABEL_HEIGHT = 32.dp

@Composable
private fun Modifier.navigateRightTo(
    target: FocusRequester?,
    onNavigate: () -> Unit,
    upTarget: FocusRequester? = null,
): Modifier {
    if (target == null && upTarget == null) return this
    val focusModifier = focusProperties {
        if (target != null) right = target
        if (upTarget != null) up = upTarget
    }
    if (target == null) return focusModifier
    val scope = rememberCoroutineScope()
    return focusModifier
        .onPreviewKeyEvent { event ->
            if (event.type != KeyEventType.KeyDown || event.key != Key.DirectionRight) {
                return@onPreviewKeyEvent false
            }
            onNavigate()
            scope.launch {
                repeat(12) {
                    withFrameNanos { }
                    if (runCatching { target.requestFocus() }.getOrDefault(false)) return@launch
                }
            }
            true
        }
}

private val expandedSourceIdsSaver = listSaver<Set<String>, String>(
    save = { it.toList() },
    restore = { it.toSet() },
)

private fun String.displayableIptvCategoryName(): String? =
    trim().takeIf { it.isNotEmpty() && it.toLongOrNull() == null }

private fun String.isSyntheticAllIptvCategory(allLabel: String): Boolean {
    val normalizedName = trim()
    return normalizedName.equals(allLabel.trim(), ignoreCase = true) ||
        normalizedName.equals("all", ignoreCase = true) ||
        normalizedName.equals("tout", ignoreCase = true)
}

private fun List<LiveTvVodCategory>.withDisplayableIptvCategoryNames(): List<LiveTvVodCategory> =
    mapNotNull { category ->
        category.name.displayableIptvCategoryName()?.let { category.copy(name = it) }
    }

private fun vodPosterLookupTitle(item: LiveTvVodItem): String {
    val rawTitle = item.seriesTitle.takeIf { item.type == "episode" && it.isNotBlank() } ?: item.title
    return normalizeLiveTvVodTitleForTmdb(rawTitle)
}

private fun vodPosterLookupKey(item: LiveTvVodItem): String {
    val lookupType = if (item.type == "episode") "series" else item.type
    val lookupYear = item.year.takeIf { item.type != "episode" }?.take(4).orEmpty()
    return "$lookupType:${vodPosterLookupTitle(item).trim().lowercase()}:$lookupYear"
}

@Composable
fun IptvVodScreen(
    type: String,
    onNavigateToDetail: (String, String, String?) -> Unit = { _, _, _ -> },
    onPlay: (LiveTvSource, LiveTvVodItem, LiveTvVodPlayback) -> Unit,
) {
    IptvAmberTheme {
        IptvVodScreenContent(type, onNavigateToDetail, onPlay)
    }
}

@Composable
private fun IptvVodScreenContent(
    type: String,
    onNavigateToDetail: (String, String, String?) -> Unit,
    onPlay: (LiveTvSource, LiveTvVodItem, LiveTvVodPlayback) -> Unit,
) {
    val liveTvModel: LiveTvScreenModel = hiltViewModel()
    val context = LocalContext.current.applicationContext
    val liveTvState by LiveTvRepository.uiState.collectAsStateWithLifecycle()
    val favoriteVodKeys = liveTvState.favoriteVodKeys
    val playbackHistory by liveTvModel.watchProgressHistory.collectAsStateWithLifecycle(initialValue = emptyList())
    val sources = remember(liveTvState.sources) {
        liveTvState.sources.filter {
            (it.type == LiveTvSourceType.Xtream && it.xtream.isConfigured) ||
                (it.type == LiveTvSourceType.Stalker && it.stalker.isConfigured)
        }
    }
    val scope = rememberCoroutineScope()
    val firstSourceFocusRequester = remember(type) { FocusRequester() }
    val firstVodItemFocusRequester = remember(type) { FocusRequester() }
    val selectedVodRailFocusRequester = remember(type) { FocusRequester() }
    val favoritesRailFocusRequester = remember(type) { FocusRequester() }
    val firstHistoryEntryFocusRequester = remember(type) { FocusRequester() }
    val historyTrashFocusRequester = remember(type) { FocusRequester() }
    val allVodCategoryLabel = stringResource(R.string.iptv_vod_all)
    val firstSourceId = sources.firstOrNull()?.id
    var selectedSourceId by rememberSaveable(type) { mutableStateOf<String?>(null) }
    var catalogRefreshVersion by remember(type) { mutableIntStateOf(0) }
    val categoryLoadKey = remember(sources, type, catalogRefreshVersion) {
        "$type:$catalogRefreshVersion:${sources.hashCode()}"
    }
    var categoriesReadyKey by remember(type) { mutableStateOf<String?>(null) }
    var selectedPanel by rememberSaveable(type) { mutableStateOf(IptvVodPanel.Sources) }
    var confirmedVodRailKey by rememberSaveable(type) { mutableStateOf<String?>(null) }
    var expandedSourceIds by rememberSaveable(type, stateSaver = expandedSourceIdsSaver) {
        mutableStateOf(emptySet<String>())
    }
    var categoriesBySource by remember(type) { mutableStateOf<Map<String, List<LiveTvVodCategory>>>(emptyMap()) }
    var selectedCategoryId by rememberSaveable(type) { mutableStateOf<String?>(null) }
    var items by remember(type) { mutableStateOf<List<LiveTvVodItem>>(emptyList()) }
    var cachedVodItems by remember(type) { mutableStateOf<List<LiveTvVodItem>>(emptyList()) }
    var selectedSeries by remember(type) { mutableStateOf<LiveTvVodItem?>(null) }
    var episodes by remember(type) { mutableStateOf<List<LiveTvVodItem>>(emptyList()) }
    var selectedSeason by rememberSaveable(type) { mutableIntStateOf(1) }
    var seriesPresentation by remember(type) { mutableStateOf<LiveTvSeriesPresentation?>(null) }
    var focusedVodItem by remember(type) { mutableStateOf<LiveTvVodItem?>(null) }
    var focusedVodPresentation by remember(type) { mutableStateOf<LiveTvVodPresentation?>(null) }
    var fallbackVodPresentation by remember(type) { mutableStateOf<LiveTvVodPresentation?>(null) }
    var vodTrailerSource by remember(type) { mutableStateOf<TrailerPlaybackSource?>(null) }
    var isVodTrailerPreviewActive by remember(type) { mutableStateOf(false) }
    var posterLookupBatch by remember(type) { mutableIntStateOf(0) }
    val posterLookupAttemptedKeys = remember(type) { mutableSetOf<String>() }
    var isLoadingSources by remember(type) { mutableStateOf(false) }
    var isLoadingItems by remember(type) { mutableStateOf(false) }
    var categoryLoadErrorSourceIds by remember(type) { mutableStateOf<Set<String>>(emptySet()) }
    var hasLoadError by remember(type) { mutableStateOf(false) }
    var hasPlaybackError by remember(type) { mutableStateOf(false) }
    var showClearHistoryDialog by remember(type) { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, type) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                selectedCategoryId = null
                focusedVodItem = null
                focusedVodPresentation = null
                isVodTrailerPreviewActive = false
                vodTrailerSource = null
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) { liveTvModel.ensureLoaded() }

    LaunchedEffect(type, firstSourceId) {
        if (firstSourceId == null) return@LaunchedEffect
        repeat(10) {
            delay(32L)
            if (runCatching { firstSourceFocusRequester.requestFocus() }.getOrDefault(false)) return@LaunchedEffect
        }
    }

    LaunchedEffect(sources, type, catalogRefreshVersion) {
        val cached = sources.flatMap { source -> LiveTvVodCache.read(context, source.id, type) }
        val sourceIds = sources.mapTo(mutableSetOf()) { it.id }
        cachedVodItems = mergeVodItems(cachedVodItems.filter { it.sourceId in sourceIds }, cached)
    }

    LaunchedEffect(sources, type, catalogRefreshVersion) {
        categoriesReadyKey = null
        isLoadingSources = sources.isNotEmpty()
        isLoadingItems = sources.isNotEmpty()
        val cachedCategories = sources.associate { source ->
            source.id to LiveTvVodCache.readCategories(context, source.id, type)
                .withDisplayableIptvCategoryNames()
        }
        val categoryRefreshRequiredSourceIds = sources
            .filter { source ->
                catalogRefreshVersion > 0 ||
                    !LiveTvVodCache.isCategoryListComplete(context, source, type)
            }
            .mapTo(mutableSetOf(), LiveTvSource::id)
        categoriesBySource = cachedCategories
        categoryLoadErrorSourceIds = emptySet()
        if (sources.isEmpty()) {
            selectedSourceId = null
            selectedCategoryId = null
            selectedSeries = null
            episodes = emptyList()
            items = emptyList()
            isLoadingSources = false
            isLoadingItems = false
            categoriesReadyKey = categoryLoadKey
            return@LaunchedEffect
        }

        val initialSourceId = selectedSourceId?.takeIf { id -> sources.any { it.id == id } }
            ?: sources.first().id
        if (initialSourceId != selectedSourceId) {
            selectedCategoryId = null
            selectedSeries = null
            episodes = emptyList()
            items = emptyList()
        }
        selectedSourceId = initialSourceId
        isLoadingSources = categoryRefreshRequiredSourceIds.isNotEmpty()
        try {
            val results = coroutineScope {
                sources.map { source -> async {
                    try {
                        if (source.id !in categoryRefreshRequiredSourceIds) {
                            source.id to cachedCategories[source.id].orEmpty()
                        } else {
                            val categories = loadVodCategories(source, type).withDisplayableIptvCategoryNames()
                            if (categories.isEmpty() && cachedCategories[source.id].orEmpty().isNotEmpty()) {
                                throw IllegalStateException("Provider returned no $type categories; keeping the cached list")
                            }
                            LiveTvVodCache.writeCategoriesAndMarkComplete(context, source, type, categories)
                            source.id to categories
                        }
                    } catch (failure: Exception) {
                        if (failure is CancellationException) throw failure
                        Log.w("LiveTvVod", "Failed to load $type categories from ${source.type}", failure)
                        null
                    }
                } }.awaitAll()
            }
            val successful = results.filterNotNull()
            val refreshed = successful.associate { (sourceId, categories) ->
                sourceId to categories.withDisplayableIptvCategoryNames()
            }
            val mergedCategories = sources.associate { source ->
                source.id to if (source.id in refreshed) refreshed[source.id].orEmpty()
                else cachedCategories[source.id].orEmpty()
            }
            categoriesBySource = mergedCategories
            val successfulSourceIds = successful.mapTo(mutableSetOf()) { it.first }
            categoryLoadErrorSourceIds = sources.mapNotNull { source ->
                source.id.takeIf { it !in successfulSourceIds }
            }.toSet()
        } catch (failure: Exception) {
            if (failure is CancellationException) throw failure
            categoryLoadErrorSourceIds = sources.mapTo(mutableSetOf()) { it.id }
        } finally {
            isLoadingSources = false
            categoriesReadyKey = categoryLoadKey
        }
    }

    LaunchedEffect(sources, type, selectedSourceId, catalogRefreshVersion, categoryLoadKey, categoriesReadyKey) {
        if (categoriesReadyKey != categoryLoadKey) return@LaunchedEffect
        val source = sources.firstOrNull { it.id == selectedSourceId }
        if (source == null) {
            items = emptyList()
            isLoadingItems = false
            return@LaunchedEffect
        }
        isLoadingItems = true
        items = emptyList()
        items = LiveTvVodCache.read(context, source.id, type)
        cachedVodItems = mergeVodItems(cachedVodItems, items)
        hasLoadError = false
        hasPlaybackError = false
        if (catalogRefreshVersion == 0 && LiveTvVodCache.isCatalogComplete(context, source, type)) {
            val categories = categoriesBySource[source.id].orEmpty()
            if (categories.isEmpty()) {
                isLoadingItems = false
                return@LaunchedEffect
            }
            isLoadingItems = true
            try {
                items = hydrateMissingVodCategories(
                    context = context,
                    source = source,
                    type = type,
                    categories = categories,
                    existingItems = items,
                    forceRefresh = false,
                    onItemsLoaded = { batch ->
                        if (selectedCategoryId != null && batch.any { it.categoryId == selectedCategoryId }) {
                            items = mergeVodItems(items, batch)
                        }
                    },
                    onLoadError = { category, failure ->
                        hasLoadError = true
                        Log.w("LiveTvVod", "Failed to hydrate $type category ${category.name}", failure)
                    },
                )
                cachedVodItems = mergeVodItems(cachedVodItems, items)
            } catch (failure: Exception) {
                if (failure is CancellationException) throw failure
                hasLoadError = true
                Log.w("LiveTvVod", "Failed to hydrate cached $type catalogue from ${source.type}", failure)
            } finally {
                isLoadingItems = false
            }
            return@LaunchedEffect
        }
        isLoadingItems = true
        try {
            coroutineScope {
                val batches = Channel<List<LiveTvVodItem>>(Channel.UNLIMITED)
                val cacheWriter = LiveTvVodCache.batchWriter(context, source.id, type)
                val collector = launch {
                    try {
                        for (batch in batches) {
                            val knownCategoryItems = cachedVodItems
                                .filter { it.sourceId == source.id && it.categoryId.isNotBlank() }
                            val correctedBatch = preserveVodCategoryAssignments(batch, knownCategoryItems)
                            items = mergeVodItems(items, correctedBatch)
                            cachedVodItems = mergeVodItems(cachedVodItems, correctedBatch)
                            cacheWriter.append(correctedBatch)
                        }
                    } finally {
                        cacheWriter.finish()
                    }
                }
                var loadedItems: List<LiveTvVodItem>? = null
                var loadTimedOut = false
                try {
                    loadedItems = withTimeoutOrNull(VOD_LOAD_TIMEOUT_MS) {
                        loadVodItems(source, type) { batch ->
                            batches.trySend(batch)
                        }
                    }
                    loadTimedOut = loadedItems == null
                } catch (failure: Exception) {
                    if (failure is CancellationException) throw failure
                    hasLoadError = true
                    Log.w("LiveTvVod", "Failed to load $type items from ${source.type}; showing ${items.size}", failure)
                } finally {
                    batches.close()
                }
                collector.join()
                if (loadTimedOut) {
                    Log.w("LiveTvVod", "Timed out loading $type from ${source.type}; showing ${items.size}")
                }
                if (loadedItems == null) {
                    hasLoadError = true
                } else {
                    val knownCategoryItems = cachedVodItems
                        .filter { it.sourceId == source.id && it.categoryId.isNotBlank() }
                    val refreshedItems = preserveVodCategoryAssignments(loadedItems, knownCategoryItems)
                    items = refreshedItems
                    cachedVodItems = mergeVodItems(
                        cachedVodItems.filterNot { it.sourceId == source.id },
                        refreshedItems,
                    )
                    val selectedSeriesId = selectedSeries
                        ?.takeIf { it.sourceId == source.id }
                        ?.id
                    if (selectedSeriesId != null && refreshedItems.none { it.id == selectedSeriesId }) {
                        selectedSeries = null
                        episodes = emptyList()
                    }
                }
                // Partial pages are merged into the cache while loading. Only a complete response
                // replaces the catalog, so a timeout cannot discard the prior catalog or new items.
                if (loadedItems != null) {
                    val catalogWritten = LiveTvVodCache.writeCompleteCatalog(context, source, type, items)
                    if (catalogWritten) {
                        val categories = categoriesBySource[source.id].orEmpty()
                        if (categories.isNotEmpty()) {
                            items = hydrateMissingVodCategories(
                                context = context,
                                source = source,
                                type = type,
                                categories = categories,
                                existingItems = items,
                                forceRefresh = catalogRefreshVersion > 0,
                                onItemsLoaded = { batch ->
                                    if (selectedCategoryId != null && batch.any { it.categoryId == selectedCategoryId }) {
                                        items = mergeVodItems(items, batch)
                                    }
                                },
                                onLoadError = { category, failure ->
                                    hasLoadError = true
                                    Log.w("LiveTvVod", "Failed to hydrate $type category ${category.name}", failure)
                                },
                            )
                            cachedVodItems = mergeVodItems(cachedVodItems, items)
                        }
                    } else {
                        hasLoadError = true
                    }
                }
            }
        } catch (failure: Exception) {
            if (failure is CancellationException) throw failure
            hasLoadError = true
        } finally {
            isLoadingItems = false
        }
    }

    LaunchedEffect(selectedSeries, sources, catalogRefreshVersion) {
        val series = selectedSeries ?: return@LaunchedEffect
        val source = sources.firstOrNull { it.id == series.sourceId }
        if (source == null) {
            episodes = emptyList()
            return@LaunchedEffect
        }
        isLoadingItems = true
        hasLoadError = false
        try {
            episodes = when (source.type) {
                LiveTvSourceType.Xtream -> LiveTvXtream.seriesEpisodes(source, series)
                LiveTvSourceType.Stalker -> LiveTvStalker.seriesEpisodes(source, series)
                LiveTvSourceType.M3u -> emptyList()
            }
        } catch (failure: Exception) {
            if (failure is CancellationException) throw failure
            hasLoadError = true
            episodes = emptyList()
        } finally {
            isLoadingItems = false
        }
    }

    LaunchedEffect(items, episodes, type, posterLookupBatch, selectedCategoryId, selectedSourceId) {
        val focusedCategoryItems = if (selectedCategoryId == null) {
            emptyList()
        } else {
            items.filter { item ->
                item.sourceId == selectedSourceId && item.categoryId.trim() == selectedCategoryId
            }
        }
        val missing = (focusedCategoryItems + items + episodes).asSequence()
            .filter { it.posterUrl.isBlank() }
            .distinctBy(::vodPosterLookupKey)
            .filter { vodPosterLookupKey(it) !in posterLookupAttemptedKeys }
            .take(12)
            .toList()
        if (missing.isEmpty()) return@LaunchedEffect
        missing.forEach { posterLookupAttemptedKeys += vodPosterLookupKey(it) }
        val posters = linkedMapOf<String, String>()
        missing.chunked(4).forEach { batch ->
            val resolved = coroutineScope {
                batch.map { item ->
                    async {
                        val year = item.year.takeIf { item.type != "episode" }
                        vodPosterLookupKey(item) to liveTvModel.tmdbPosterForVod(vodPosterLookupTitle(item), item.type, year)
                    }
                }
            }.awaitAll()
            resolved.forEach { (key, poster) -> poster?.takeIf(String::isNotBlank)?.let { posters[key] = it } }
        }
        if (posters.isNotEmpty()) {
            items = items.map { item ->
                item.copy(posterUrl = item.posterUrl.ifBlank { posters[vodPosterLookupKey(item)].orEmpty() })
            }
            episodes = episodes.map { item ->
                item.copy(posterUrl = item.posterUrl.ifBlank { posters[vodPosterLookupKey(item)].orEmpty() })
            }
        }
        if ((items + episodes).any { it.posterUrl.isBlank() && vodPosterLookupKey(it) !in posterLookupAttemptedKeys }) {
            posterLookupBatch += 1
        }
    }

    LaunchedEffect(focusedVodItem?.favoriteKey, focusedVodItem?.title, focusedVodItem?.year) {
        val item = focusedVodItem
        if (item == null || item.type !in setOf("movie", "series")) {
            focusedVodPresentation = null
            return@LaunchedEffect
        }
        focusedVodPresentation = null
        val presentation = liveTvModel.tmdbVodPresentationForVod(
            title = vodPosterLookupTitle(item),
            type = item.type,
            year = item.year,
        )
        if (focusedVodItem?.let(::vodPosterLookupKey) == vodPosterLookupKey(item)) {
            focusedVodPresentation = presentation
        }
    }

    LaunchedEffect(
        focusedVodItem?.favoriteKey,
        focusedVodItem?.title,
        focusedVodItem?.year,
        focusedVodPresentation?.match?.tmdbId,
    ) {
        vodTrailerSource = null
        val item = focusedVodItem
        val presentation = focusedVodPresentation
        if (item == null || item.type !in setOf("movie", "series") || presentation == null) {
            return@LaunchedEffect
        }
        delay(VOD_FOCUS_PREVIEW_DELAY_MS)
        val source = liveTvModel.resolveVodTrailerPlaybackSource(item, presentation)
        if (focusedVodItem?.favoriteKey == item.favoriteKey &&
            focusedVodPresentation?.match?.tmdbId == presentation.match.tmdbId
        ) {
            vodTrailerSource = source?.takeIf { it.videoUrl.isNotBlank() }
        }
    }

    LaunchedEffect(
        focusedVodItem?.favoriteKey,
        focusedVodItem?.title,
        focusedVodItem?.year,
        vodTrailerSource?.videoUrl,
    ) {
        isVodTrailerPreviewActive = false
        val item = focusedVodItem
        val source = vodTrailerSource?.takeIf { it.videoUrl.isNotBlank() }
        if (item == null || item.type !in setOf("movie", "series") || source == null) {
            return@LaunchedEffect
        }
        delay(VOD_TRAILER_PREVIEW_DELAY_MS)
        if (focusedVodItem?.favoriteKey == item.favoriteKey &&
            vodTrailerSource?.videoUrl == source.videoUrl
        ) {
            isVodTrailerPreviewActive = true
        }
    }

    val displayedSourceId = selectedSourceId?.takeIf { id -> sources.any { it.id == id } } ?: firstSourceId
    val moduleFavorites = remember(cachedVodItems, items, favoriteVodKeys, type) {
        mergeVodItems(cachedVodItems, items).filter { it.type == type && it.favoriteKey in favoriteVodKeys }
    }
    val moduleHistory = remember(playbackHistory, sources, type) {
        playbackHistory.asSequence()
            .filter { entry ->
                entry.contentType == type && sources.any { source ->
                    entry.contentId.startsWith("${source.id}:$type:")
                }
            }
            .sortedByDescending(WatchProgress::lastWatched)
            .toList()
    }
    val sourceIds = remember(sources) { sources.mapTo(mutableSetOf()) { it.id } }
    val observedCategoriesBySource = remember(cachedVodItems, items, sourceIds, type) {
        mergeVodItems(cachedVodItems, items)
            .asSequence()
            .filter { it.type == type && it.sourceId in sourceIds && it.categoryId.isNotBlank() }
            .groupBy(LiveTvVodItem::sourceId)
            .mapValues { (sourceId, sourceItems) ->
                sourceItems.asSequence()
                    .mapNotNull { item ->
                        val categoryId = item.categoryId.trim()
                        val categoryName = item.categoryName.displayableIptvCategoryName()
                        if (categoryId.isBlank() || categoryName == null) null
                        else LiveTvVodCategory(categoryId, sourceId, categoryName)
                    }
                    .distinctBy(LiveTvVodCategory::id)
                    .toList()
            }
    }
    val sourceCategories = categoriesBySource[selectedSourceId.orEmpty()].orEmpty()
    val itemCategories = observedCategoriesBySource[selectedSourceId.orEmpty()].orEmpty()
    val hiddenVodCategoryKeys = liveTvState.hiddenVodCategoryKeys
    val visibleCategories = (sourceCategories + itemCategories)
        .distinctBy { it.id }
        .filterNot { category ->
            LiveTvRepository.vodCategoryHideKey(type, category.sourceId, category.id) in hiddenVodCategoryKeys
        }
    val sourceItems = remember(cachedVodItems, items, displayedSourceId) {
        displayedSourceId?.let { sourceId ->
            mergeVodItems(
                cachedVodItems.filter { it.sourceId == sourceId },
                items.filter { it.sourceId == sourceId },
            )
        }.orEmpty()
    }
    val visibleItems = sourceItems.filter { item ->
        val hidden = item.categoryId.isNotBlank() &&
            LiveTvRepository.vodCategoryHideKey(type, item.sourceId, item.categoryId.trim()) in hiddenVodCategoryKeys
        !hidden && (selectedCategoryId == null || item.categoryId.trim() == selectedCategoryId)
    }

    val seriesEpisodeItems = remember(episodes) { episodes.toNumberedSeriesEpisodes() }
    val seriesSeasons = remember(seriesEpisodeItems) { seriesEpisodeItems.map { it.season }.distinct().sorted() }
    LaunchedEffect(selectedSeries?.favoriteKey, seriesSeasons) {
        val series = selectedSeries
        if (series == null || episodes.isEmpty()) {
            seriesPresentation = null
            return@LaunchedEffect
        }
        seriesPresentation = null
        if (selectedSeason !in seriesSeasons && seriesSeasons.isNotEmpty()) {
            selectedSeason = seriesSeasons.first()
        }
        val resolved = liveTvModel.tmdbSeriesPresentationForVod(
            normalizeLiveTvVodTitleForTmdb(series.title),
            seriesSeasons,
            onPresentation = { seriesPresentation = it },
        )
        if (resolved != null) seriesPresentation = resolved
    }

    BackHandler(enabled = selectedSeries != null) {
        selectedSeries = null
        episodes = emptyList()
        seriesPresentation = null
    }

    fun playVodItem(item: LiveTvVodItem) {
        val source = sources.firstOrNull { it.id == item.sourceId } ?: return
        if (type == "movie") {
            scope.launch {
                try {
                    hasPlaybackError = false
                    onPlay(source, item, resolveIptvVod(source, item))
                } catch (failure: Exception) {
                    if (failure is CancellationException) throw failure
                    hasPlaybackError = true
                }
            }
        } else {
            selectedSeries = item
            selectedSeason = 1
            episodes = emptyList()
            isLoadingItems = true
            hasLoadError = false
            hasPlaybackError = false
        }
    }

    fun openVodItemDetails(item: LiveTvVodItem) {
        if (item.type !in setOf("movie", "series")) return
        scope.launch {
            try {
                val focusedPresentation = focusedVodPresentation.takeIf {
                    focusedVodItem?.let(::vodPosterLookupKey) == vodPosterLookupKey(item)
                }
                val presentation = focusedPresentation ?: liveTvModel.tmdbVodPresentationForVod(
                    title = vodPosterLookupTitle(item),
                    type = item.type,
                    year = item.year,
                )
                val tmdbId = presentation?.match?.tmdbId
                if (tmdbId != null) {
                    onNavigateToDetail("tmdb:$tmdbId", item.type, "")
                } else {
                    android.widget.Toast.makeText(
                        context,
                        R.string.error_meta_not_found,
                        android.widget.Toast.LENGTH_SHORT,
                    ).show()
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                android.widget.Toast.makeText(
                    context,
                    R.string.error_meta_not_found,
                    android.widget.Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    fun focusVodSource(sourceId: String) {
        val switchingSource = selectedSourceId != sourceId
        if (switchingSource) {
            isLoadingItems = true
            selectedSourceId = sourceId
            selectedCategoryId = null
            selectedSeries = null
            episodes = emptyList()
        }
        selectedPanel = IptvVodPanel.Sources
        if (switchingSource && sourceId !in expandedSourceIds) {
            expandedSourceIds = expandedSourceIds + sourceId
        }
    }

    fun focusVodCategory(sourceId: String, categoryId: String?) {
        selectedPanel = IptvVodPanel.Sources
        selectedSourceId = sourceId
        selectedCategoryId = categoryId
        selectedSeries = null
        episodes = emptyList()
    }

    fun focusVodPanel(panel: IptvVodPanel) {
        selectedPanel = panel
        selectedSeries = null
        episodes = emptyList()
    }

    LaunchedEffect(hiddenVodCategoryKeys, selectedSourceId, selectedCategoryId, type) {
        val categoryId = selectedCategoryId ?: return@LaunchedEffect
        val sourceId = selectedSourceId ?: return@LaunchedEffect
        if (LiveTvRepository.vodCategoryHideKey(type, sourceId, categoryId) in hiddenVodCategoryKeys) {
            selectedCategoryId = null
        }
    }

    val hasFocusableHistoryEntry = remember(moduleHistory, cachedVodItems, type) {
        moduleHistory.any { entry ->
            cachedVodItems.any { item -> item.type == type && item.id == entry.contentId }
        }
    }
    val seriesForDetail = selectedSeries
    Column(
        modifier = Modifier.fillMaxSize()
            .background(NuvioTheme.colors.Background)
            .then(
                if (selectedSeries != null) Modifier
                else Modifier.padding(start = 30.dp, end = 0.dp, top = 0.dp, bottom = 0.dp)
            )
    ) {
        if (seriesForDetail != null) {
            val series = seriesForDetail
            val visibleSeriesEpisodes = seriesEpisodeItems.filter { it.season == selectedSeason }
            val seriesProgress = remember(playbackHistory, series.id) {
                playbackHistory
                    .filter { it.contentId == series.id && it.season != null && it.episode != null }
                    .associateBy { it.season!! to it.episode!! }
            }
            val watchedSeriesEpisodes = remember(seriesProgress) {
                seriesProgress.filterValues { it.isCompleted() }.keys
            }
            when {
                isLoadingItems && episodes.isEmpty() -> CenterLabel(stringResource(R.string.iptv_vod_loading))
                hasLoadError && episodes.isEmpty() -> CenterLabel(stringResource(R.string.iptv_vod_error))
                episodes.isEmpty() -> CenterLabel(stringResource(R.string.iptv_vod_empty_episodes))
                else -> {
                    val metadata = seriesPresentation?.metadata
                    val creatorRole = stringResource(R.string.episode_folder_creator)
                    val episodeNumberFormat = stringResource(R.string.episode_folder_episode_number)
                    EpisodeBackdropLayout(
                        modifier = Modifier.fillMaxSize(),
                        info = EpisodeBackdropInfo(
                            title = metadata?.localizedTitle?.takeIf(String::isNotBlank)
                                ?: normalizeLiveTvVodTitleForTmdb(series.title),
                            tmdbId = seriesPresentation?.tmdbId,
                            logoUrl = metadata?.logo,
                            backdropUrl = metadata?.backdrop ?: series.posterUrl.takeIf(String::isNotBlank),
                            genres = metadata?.genres.orEmpty(),
                            year = metadata?.releaseInfo?.take(4)
                                ?: series.year.takeIf(String::isNotBlank)?.take(4),
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
                            synopsis = metadata?.description ?: series.plot.takeIf(String::isNotBlank),
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
                        episodes = visibleSeriesEpisodes.map { row ->
                            val episodeMetadata = seriesPresentation?.episodes?.get(row.season to row.episode)
                            EpisodeRailItem(
                                id = row.item.favoriteKey,
                                title = String.format(Locale.getDefault(), episodeNumberFormat, row.episode),
                                subtitle = normalizeLiveTvVodTitleForTmdb(
                                    episodeMetadata?.title?.takeIf(String::isNotBlank) ?: row.item.title
                                ),
                                imageUrl = episodeMetadata?.thumbnail?.takeIf(String::isNotBlank)
                                    ?: row.item.posterUrl.takeIf(String::isNotBlank),
                                airDate = episodeMetadata?.airDate,
                                overview = episodeMetadata?.overview,
                                runtimeMinutes = episodeMetadata?.runtimeMinutes,
                                season = row.season,
                                episode = row.episode,
                            )
                        },
                        seasons = seriesSeasons,
                        selectedSeason = selectedSeason,
                        onSeasonSelected = { selectedSeason = it },
                        episodeProgressMap = seriesProgress,
                        watchedEpisodes = watchedSeriesEpisodes,
                        onToggleEpisodeWatched = { railItem ->
                            val numberedEpisode = visibleSeriesEpisodes.firstOrNull {
                                it.item.favoriteKey == railItem.id
                            } ?: return@EpisodeBackdropLayout
                            val key = numberedEpisode.season to numberedEpisode.episode
                            val progress = seriesProgress[key]
                            if (progress?.isCompleted() == true || key in watchedSeriesEpisodes) {
                                progress?.let { liveTvModel.removeIptvHistory(listOf(it)) }
                            } else {
                                liveTvModel.markIptvHistoryWatched(
                                    listOf(buildIptvEpisodeWatchedProgress(series, numberedEpisode)),
                                )
                            }
                        },
                        onMarkSeasonWatched = { season ->
                            val progressToMark = seriesEpisodeItems
                                .filter { it.season == season }
                                .filterNot { episode ->
                                    seriesProgress[episode.season to episode.episode]?.isCompleted() == true
                                }
                                .map { buildIptvEpisodeWatchedProgress(series, it) }
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
                        isInLibrary = series.favoriteKey in favoriteVodKeys,
                        onToggleLibrary = {
                            LiveTvRepository.toggleVodFavorite(series)
                            scope.launch { LiveTvVodCache.upsert(context, series) }
                        },
                        onRelatedClick = { item ->
                            onNavigateToDetail(item.id, item.apiType, item.sourceAddonBaseUrl)
                        },
                        onEpisodeClick = { railItem ->
                            val episode = visibleSeriesEpisodes.firstOrNull { it.item.favoriteKey == railItem.id }?.item
                            val source = episode?.let { candidate -> sources.firstOrNull { it.id == candidate.sourceId } }
                            if (episode != null && source != null) {
                                scope.launch {
                                    try {
                                        hasPlaybackError = false
                                        onPlay(source, episode, resolveIptvVod(source, episode))
                                    } catch (failure: Exception) {
                                        if (failure is CancellationException) throw failure
                                        hasPlaybackError = true
                                    }
                                }
                            }
                        },
                    )
                }
            }
        } else {
        val isCatalogLoading = selectedPanel == IptvVodPanel.Sources &&
            selectedSeries == null &&
            (liveTvState.isLoading || isLoadingSources || isLoadingItems || categoriesReadyKey != categoryLoadKey)
        val hasRelevantCategoryLoadError = if (selectedCategoryId == null) {
            categoryLoadErrorSourceIds.isNotEmpty()
        } else {
            selectedSourceId?.let { it in categoryLoadErrorSourceIds } == true
        }
        val hasPartialCatalogLoadError = selectedPanel == IptvVodPanel.Sources &&
            selectedSeries == null &&
            (hasRelevantCategoryLoadError || hasLoadError && items.isNotEmpty())
        val showCatalogStatusOverlay = (isCatalogLoading && visibleItems.isNotEmpty()) || hasPartialCatalogLoadError
        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
        val focusedDetailsItems = when (selectedPanel) {
            IptvVodPanel.Favorites -> moduleFavorites
            IptvVodPanel.Sources -> visibleItems
            IptvVodPanel.History -> emptyList()
        }
        val focusedDetailsItem = focusedVodItem?.let { focused ->
            focusedDetailsItems.firstOrNull { it.favoriteKey == focused.favoriteKey }
        } ?: focusedDetailsItems.firstOrNull()
        LaunchedEffect(focusedDetailsItem?.favoriteKey, focusedVodItem?.favoriteKey) {
            val item = focusedDetailsItem
            fallbackVodPresentation = null
            if (item == null || item.type !in setOf("movie", "series") ||
                focusedVodItem?.favoriteKey == item.favoriteKey
            ) {
                return@LaunchedEffect
            }
            val presentation = liveTvModel.tmdbVodPresentationForVod(
                title = vodPosterLookupTitle(item),
                type = item.type,
                year = item.year,
            )
            if (focusedDetailsItem?.favoriteKey == item.favoriteKey &&
                focusedVodItem?.favoriteKey != item.favoriteKey
            ) {
                fallbackVodPresentation = presentation
            }
        }
        val detailsPresentation = if (focusedVodItem?.favoriteKey == focusedDetailsItem?.favoriteKey) {
            focusedVodPresentation
        } else {
            fallbackVodPresentation
        }
        val hasFeaturedLayout = when (selectedPanel) {
            IptvVodPanel.Sources -> true // Reserve the layout as soon as the module opens.
            IptvVodPanel.Favorites -> moduleFavorites.isNotEmpty()
            IptvVodPanel.History -> false
        }
        LaunchedEffect(
            selectedPanel,
            selectedSourceId,
            selectedCategoryId,
            focusedDetailsItems,
            focusedVodItem?.favoriteKey,
        ) {
            val nextItem = focusedVodItem?.let { focused ->
                focusedDetailsItems.firstOrNull { it.favoriteKey == focused.favoriteKey }
            }
            if (focusedVodItem?.favoriteKey != nextItem?.favoriteKey) {
                focusedVodItem = nextItem
            }
        }
        // Size the featured area for one full row of six posters below it.
        val gridHorizontalInsets = VOD_GRID_HORIZONTAL_CONTENT_PADDING * 2f +
            VOD_GRID_ITEM_SPACING * (VOD_GRID_COLUMNS - 1).toFloat()
        val posterCardWidth = (
            (maxWidth * VOD_HERO_PANEL_WIDTH - VOD_HERO_START_PADDING - gridHorizontalInsets) / VOD_GRID_COLUMNS
        ).coerceAtLeast(0.dp)
        val posterRowHeight = maxOf(
            VOD_SINGLE_POSTER_ROW_HEIGHT,
            posterCardWidth * 1.5f + VOD_POSTER_LABEL_HEIGHT + VOD_GRID_VERTICAL_CONTENT_PADDING * 2f,
        )
        val posterRowsHeight = minOf(posterRowHeight, maxHeight)
        val heroHeight = if (hasFeaturedLayout) {
            (maxHeight - posterRowsHeight).coerceAtLeast(0.dp)
        } else {
            0.dp
        }
        Row(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(VOD_DETAILS_PANEL_WIDTH).fillMaxHeight().padding(top = 56.dp),
                contentPadding = PaddingValues(bottom = 12.dp, end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                item(key = "vod-favorites") {
                    VodPanelRow(
                        title = stringResource(R.string.iptv_vod_favorites),
                        selected = selectedPanel == IptvVodPanel.Favorites,
                        confirmed = confirmedVodRailKey == "panel:favorites",
                        icon = Icons.Default.Star,
                        focusRequester = when (selectedPanel) {
                            IptvVodPanel.Favorites -> selectedVodRailFocusRequester
                            IptvVodPanel.History -> favoritesRailFocusRequester
                            IptvVodPanel.Sources -> null
                        },
                        rightFocusRequester = firstVodItemFocusRequester.takeIf { selectedPanel == IptvVodPanel.Favorites },
                        onFocus = { focusVodPanel(IptvVodPanel.Favorites) },
                        onClick = {
                            confirmedVodRailKey = "panel:favorites"
                            focusVodPanel(IptvVodPanel.Favorites)
                        },
                    )
                }
                item(key = "vod-history") {
                    VodPanelRow(
                        title = stringResource(R.string.iptv_vod_history),
                        selected = selectedPanel == IptvVodPanel.History,
                        confirmed = confirmedVodRailKey == "panel:history",
                        icon = Icons.Default.History,
                        focusRequester = selectedVodRailFocusRequester.takeIf { selectedPanel == IptvVodPanel.History },
                        rightFocusRequester = historyTrashFocusRequester.takeIf {
                            selectedPanel == IptvVodPanel.History && moduleHistory.isNotEmpty()
                        },
                        upFocusRequester = favoritesRailFocusRequester.takeIf { selectedPanel == IptvVodPanel.History },
                        onFocus = { focusVodPanel(IptvVodPanel.History) },
                        onClick = {
                            confirmedVodRailKey = "panel:history"
                            focusVodPanel(IptvVodPanel.History)
                        },
                    )
                }
                item(key = "sources-heading") {
                    Text(
                        text = stringResource(R.string.live_tv_sources),
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 3.dp),
                        color = NuvioTheme.colors.TextSecondary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                items(sources, key = { "source:${it.id}" }) { source ->
                    val categories = (if (source.id == selectedSourceId) {
                        visibleCategories
                    } else {
                        categoriesBySource[source.id].orEmpty() + observedCategoriesBySource[source.id].orEmpty()
                    })
                        .distinctBy { it.id }
                        .filterNot { category -> category.name.isSyntheticAllIptvCategory(allVodCategoryLabel) }
                        .filterNot { category ->
                            LiveTvRepository.vodCategoryHideKey(type, source.id, category.id) in hiddenVodCategoryKeys
                        }
                    val expanded = source.id in expandedSourceIds
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        VodSourceRow(
                            source = source,
                            confirmed = confirmedVodRailKey == "source:${source.id}",
                            focusRequester = when {
                                selectedPanel == IptvVodPanel.Sources &&
                                    selectedSourceId == source.id && source.id !in expandedSourceIds -> selectedVodRailFocusRequester
                                source.id == firstSourceId -> firstSourceFocusRequester
                                else -> null
                            },
                            rightFocusRequester = firstVodItemFocusRequester,
                            count = categories.size.takeIf {
                                categoriesBySource.containsKey(source.id) || observedCategoriesBySource.containsKey(source.id)
                            },
                            selected = selectedSourceId == source.id,
                            expanded = expanded,
                            onFocus = { focusVodSource(source.id) },
                            onClick = {
                                confirmedVodRailKey = "source:${source.id}"
                                val wasSelected = selectedSourceId == source.id
                                if (!wasSelected) {
                                    isLoadingItems = true
                                    selectedSourceId = source.id
                                    selectedCategoryId = null
                                    selectedSeries = null
                                    episodes = emptyList()
                                }
                                selectedPanel = IptvVodPanel.Sources
                                expandedSourceIds = if (source.id in expandedSourceIds && wasSelected) {
                                    expandedSourceIds - source.id
                                } else {
                                    expandedSourceIds + source.id
                                }
                            },
                        )
                        if (expanded) {
                            VodCategoryRow(
                                title = stringResource(R.string.iptv_vod_refresh_list),
                                selected = false,
                                confirmed = confirmedVodRailKey == "refresh:${source.id}",
                                rightFocusRequester = firstVodItemFocusRequester,
                                onFocus = { focusVodCategory(source.id, null) },
                                onClick = {
                                    if (!isCatalogLoading) {
                                        confirmedVodRailKey = "refresh:${source.id}"
                                        selectedPanel = IptvVodPanel.Sources
                                        selectedSourceId = source.id
                                        selectedCategoryId = null
                                        selectedSeries = null
                                        episodes = emptyList()
                                        isLoadingItems = true
                                        catalogRefreshVersion++
                                    }
                                },
                            )
                            VodCategoryRow(
                                title = allVodCategoryLabel,
                                selected = selectedSourceId == source.id && selectedCategoryId == null,
                                confirmed = confirmedVodRailKey == "category:${source.id}:",
                                focusRequester = selectedVodRailFocusRequester.takeIf {
                                    selectedPanel == IptvVodPanel.Sources &&
                                        selectedSourceId == source.id && selectedCategoryId == null
                                },
                                rightFocusRequester = firstVodItemFocusRequester,
                                onFocus = { focusVodCategory(source.id, null) },
                                onClick = {
                                    confirmedVodRailKey = "category:${source.id}:"
                                    focusVodCategory(source.id, null)
                                },
                            )
                            categories.forEach { category ->
                                VodCategoryRow(
                                    title = category.name,
                                    selected = selectedSourceId == source.id && selectedCategoryId == category.id,
                                    confirmed = confirmedVodRailKey == "category:${source.id}:${category.id}",
                                    focusRequester = selectedVodRailFocusRequester.takeIf {
                                        selectedPanel == IptvVodPanel.Sources &&
                                            selectedSourceId == source.id && selectedCategoryId == category.id
                                    },
                                    rightFocusRequester = firstVodItemFocusRequester,
                                    onFocus = { focusVodCategory(source.id, category.id) },
                                    onClick = {
                                        confirmedVodRailKey = "category:${source.id}:${category.id}"
                                        focusVodCategory(source.id, category.id)
                                    },
                                )
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier.weight(VOD_HERO_PANEL_WIDTH).fillMaxHeight().padding(start = VOD_HERO_START_PADDING),
            ) {
                when {
                    selectedSeries != null && isLoadingItems && episodes.isEmpty() ->
                        CenterLabel(stringResource(R.string.iptv_vod_loading))
                    selectedSeries != null && hasLoadError && episodes.isEmpty() ->
                        CenterLabel(stringResource(R.string.iptv_vod_error))
                    selectedSeries != null && episodes.isEmpty() ->
                        CenterLabel(stringResource(R.string.iptv_vod_empty_episodes))
                    selectedSeries != null -> VodGrid(items = episodes, favoriteVodKeys = favoriteVodKeys, onSelect = { episode ->
                        val source = sources.firstOrNull { it.id == episode.sourceId } ?: return@VodGrid
                        scope.launch {
                            try {
                                hasPlaybackError = false
                                onPlay(source, episode, resolveIptvVod(source, episode))
                            } catch (failure: Exception) {
                                if (failure is CancellationException) throw failure
                                hasPlaybackError = true
                            }
                        }
                    })
                    selectedPanel == IptvVodPanel.History && moduleHistory.isEmpty() ->
                        CenterLabel(stringResource(R.string.iptv_vod_history_empty))
                    selectedPanel == IptvVodPanel.History -> Column(Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().height(56.dp).padding(end = 12.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Card(
                                onClick = { showClearHistoryDialog = true },
                                modifier = Modifier.size(32.dp)
                                    .focusRequester(historyTrashFocusRequester)
                                    .focusProperties {
                                        left = selectedVodRailFocusRequester
                                        down = if (hasFocusableHistoryEntry) {
                                            firstHistoryEntryFocusRequester
                                        } else {
                                            selectedVodRailFocusRequester
                                        }
                                    },
                                colors = CardDefaults.colors(
                                    containerColor = NuvioTheme.colors.SurfaceVariant,
                                    focusedContainerColor = NuvioTheme.colors.FocusBackground,
                                ),
                                shape = CardDefaults.shape(shape = NuvioTheme.shapes.iconButton),
                                border = vodAccentBorder(selected = false),
                                scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.iptv_vod_history_clear),
                                        modifier = Modifier.size(16.dp),
                                        tint = NuvioTheme.colors.TextPrimary,
                                    )
                                }
                            }
                        }
                        VodHistoryList(
                            entries = moduleHistory,
                            sources = sources,
                            cachedItems = cachedVodItems,
                            type = type,
                            firstItemFocusRequester = firstHistoryEntryFocusRequester,
                            trashFocusRequester = historyTrashFocusRequester,
                            modifier = Modifier.weight(1f),
                            onSelect = ::playVodItem,
                        )
                    }
                    else -> Spacer(Modifier.fillMaxSize())
                }
            }
        }

        val focusedGridItems = when (selectedPanel) {
            IptvVodPanel.Favorites -> moduleFavorites
            IptvVodPanel.Sources -> visibleItems
            IptvVodPanel.History -> null
        }
        if (selectedSeries == null) {
            focusedGridItems?.let { gridItems ->
                VodGridWithFocusedDetails(
                    items = gridItems,
                    favoriteVodKeys = favoriteVodKeys,
                    focusedItem = focusedDetailsItem,
                    presentation = detailsPresentation,
                    heroHeight = heroHeight,
                    trailerPreviewVideoUrl = vodTrailerSource?.videoUrl
                        ?.takeIf {
                            isVodTrailerPreviewActive && it.isNotBlank() &&
                                focusedVodItem?.favoriteKey == focusedDetailsItem?.favoriteKey
                        },
                    trailerPreviewAudioUrl = vodTrailerSource?.audioUrl
                        ?.takeIf { focusedVodItem?.favoriteKey == focusedDetailsItem?.favoriteKey },
                    onTrailerError = { isVodTrailerPreviewActive = false },
                    onTrailerEnded = { isVodTrailerPreviewActive = false },
                    onFocusedItemChanged = { focusedVodItem = it },
                    firstItemFocusRequester = firstVodItemFocusRequester,
                    leftFocusRequester = selectedVodRailFocusRequester,
                    onToggleFavorite = { item ->
                        LiveTvRepository.toggleVodFavorite(item)
                        scope.launch { LiveTvVodCache.upsert(context, item) }
                    },
                    onSelect = ::openVodItemDetails,
                    gridWidthFraction = 1f,
                    emptyMessage = when (selectedPanel) {
                        IptvVodPanel.Favorites ->
                            stringResource(R.string.iptv_vod_empty_favorites).takeIf { gridItems.isEmpty() }
                        IptvVodPanel.Sources -> when {
                            sources.isEmpty() && (liveTvState.isLoading || isCatalogLoading) ->
                                stringResource(R.string.iptv_vod_loading)
                            sources.isEmpty() -> stringResource(R.string.iptv_vod_no_xtream)
                            isCatalogLoading -> stringResource(R.string.iptv_vod_loading)
                            hasLoadError && visibleItems.isEmpty() -> stringResource(R.string.iptv_vod_error)
                            visibleItems.isEmpty() -> stringResource(R.string.iptv_vod_empty)
                            else -> null
                        }
                        IptvVodPanel.History -> null
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .fillMaxWidth(VOD_HERO_PANEL_WIDTH)
                        .padding(start = VOD_HERO_START_PADDING)
                        .fillMaxHeight(),
                )
            }
        }
        if (hasPlaybackError) {
            Text(
                text = stringResource(R.string.iptv_vod_playback_error),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = heroHeight + 8.dp, end = 12.dp),
                color = NuvioTheme.colors.TextSecondary,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        if (showCatalogStatusOverlay) {
            Text(
                text = stringResource(
                    if (isCatalogLoading) R.string.iptv_vod_loading
                    else R.string.iptv_vod_partial_error
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.Black.copy(alpha = 0.86f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(999.dp),
                    )
                    .padding(horizontal = 11.dp, vertical = 6.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(VOD_DETAILS_PANEL_WIDTH)
                .height(56.dp)
                .background(NuvioTheme.colors.Background)
                .padding(end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        when (selectedPanel) {
                            IptvVodPanel.Favorites -> R.string.iptv_vod_favorites
                            IptvVodPanel.History -> R.string.iptv_vod_history
                            IptvVodPanel.Sources -> if (type == "movie") R.string.nav_movies else R.string.nav_series
                        }
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    color = NuvioTheme.colors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        }
    }

    if (showClearHistoryDialog) {
        NuvioDialog(
            onDismiss = { showClearHistoryDialog = false },
            title = stringResource(R.string.iptv_vod_history_clear_confirm_title),
            subtitle = stringResource(
                R.string.iptv_vod_history_clear_confirm_message,
                stringResource(if (type == "movie") R.string.nav_movies else R.string.nav_series),
            ),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LiveTvPillButton(
                    text = stringResource(R.string.action_cancel),
                    onClick = { showClearHistoryDialog = false },
                )
                LiveTvPillButton(
                    text = stringResource(R.string.iptv_vod_history_clear),
                    onClick = {
                        liveTvModel.removeIptvHistory(moduleHistory)
                        showClearHistoryDialog = false
                    },
                    icon = Icons.Default.Delete,
                    selected = true,
                )
            }
        }
    }

}

}

private suspend fun loadVodCategories(source: LiveTvSource, type: String): List<LiveTvVodCategory> =
    when (source.type) {
        LiveTvSourceType.Xtream -> LiveTvXtream.vodCategories(source, type)
        LiveTvSourceType.Stalker -> LiveTvStalker.vodCategories(source, type)
        LiveTvSourceType.M3u -> emptyList()
    }

private suspend fun loadVodItems(
    source: LiveTvSource,
    type: String,
    categoryId: String = "",
    onBatch: (List<LiveTvVodItem>) -> Unit = {},
): List<LiveTvVodItem> = when (source.type) {
    LiveTvSourceType.Xtream -> LiveTvXtream.vodItems(source, type, categoryId, onBatch)
    LiveTvSourceType.Stalker -> LiveTvStalker.vodItems(source, type, categoryId, onBatch)
    LiveTvSourceType.M3u -> emptyList()
}

/**
 * Some portals omit category IDs from the full VOD response. Hydrate categories during catalogue
 * import so group selection can remain a local filter and never triggers a provider scan.
 */
private suspend fun hydrateMissingVodCategories(
    context: Context,
    source: LiveTvSource,
    type: String,
    categories: List<LiveTvVodCategory>,
    existingItems: List<LiveTvVodItem>,
    forceRefresh: Boolean,
    onItemsLoaded: (List<LiveTvVodItem>) -> Unit,
    onLoadError: (LiveTvVodCategory, Throwable) -> Unit,
): List<LiveTvVodItem> {
    val uniqueCategories = categories.distinctBy(LiveTvVodCategory::id)
    if (uniqueCategories.isEmpty()) return existingItems
    if (forceRefresh) LiveTvVodCache.clearCategoryCompletions(context, source, type)

    val completedIds = if (forceRefresh) {
        emptySet()
    } else {
        LiveTvVodCache.completedCategoryIds(context, source, type)
    }
    val categoriesToLoad = uniqueCategories.filter { category ->
        forceRefresh || category.id !in completedIds
    }
    if (categoriesToLoad.isEmpty()) return existingItems

    val semaphore = Semaphore(3)
    val cacheWriter = LiveTvVodCache.batchWriter(context, source.id, type)
    val successfulCategoryIds = mutableSetOf<String>()
    val mergedById = LinkedHashMap<String, LiveTvVodItem>(existingItems.size)
    existingItems.forEach { mergedById[it.id] = it }
    try {
        coroutineScope {
            val results = Channel<Pair<LiveTvVodCategory, Result<List<LiveTvVodItem>>>>(Channel.RENDEZVOUS)
            val workers = categoriesToLoad.map { category ->
                launch {
                    val result = try {
                        Result.success(
                            semaphore.withPermit {
                                loadVodItems(source, type, category.id).map { item ->
                                    item.copy(
                                        categoryId = category.id,
                                        categoryName = item.categoryName.ifBlank { category.name },
                                    )
                                }
                            },
                        )
                    } catch (failure: CancellationException) {
                        throw failure
                    } catch (failure: Exception) {
                        Result.failure(failure)
                    }
                    results.send(category to result)
                }
            }
            try {
                repeat(categoriesToLoad.size) {
                    val (category, result) = results.receive()
                    result.fold(
                        onSuccess = { items ->
                            successfulCategoryIds += category.id
                            if (items.isNotEmpty()) {
                                cacheWriter.append(items)
                                items.forEach { item ->
                                    val cached = mergedById[item.id]
                                    mergedById[item.id] = cached?.let(item::withMissingVodFieldsFrom) ?: item
                                }
                                onItemsLoaded(items)
                            }
                        },
                        onFailure = { failure -> onLoadError(category, failure) },
                    )
                }
            } finally {
                results.close()
                workers.forEach { it.cancel() }
            }
        }
    } finally {
        cacheWriter.finish()
    }

    val mergedItems = mergedById.values.toList()
    if (successfulCategoryIds.isNotEmpty()) {
        val cacheReady = mergedItems == existingItems ||
            LiveTvVodCache.writeCompleteCatalog(context, source, type, mergedItems)
        if (cacheReady) {
            LiveTvVodCache.markCategoriesComplete(context, source, type, successfulCategoryIds)
        } else {
            Log.w("LiveTvVod", "Could not persist hydrated $type categories from ${source.type}")
        }
    }
    return mergedItems
}

private fun LiveTvVodItem.withMissingVodFieldsFrom(fallback: LiveTvVodItem): LiveTvVodItem = copy(
    providerId = providerId.ifBlank { fallback.providerId },
    title = title.ifBlank { fallback.title },
    categoryId = categoryId.ifBlank { fallback.categoryId },
    categoryName = categoryName.ifBlank { fallback.categoryName },
    posterUrl = posterUrl.ifBlank { fallback.posterUrl },
    extension = extension.ifBlank { fallback.extension },
    year = year.ifBlank { fallback.year },
    plot = plot.ifBlank { fallback.plot },
    season = season.takeIf { it > 0 } ?: fallback.season,
    episode = episode.takeIf { it > 0 } ?: fallback.episode,
    command = command.ifBlank { fallback.command },
    seriesId = seriesId.ifBlank { fallback.seriesId },
    seriesTitle = seriesTitle.ifBlank { fallback.seriesTitle },
)

private const val VOD_LOAD_TIMEOUT_MS = 300_000L

private val SERIES_EPISODE_NUMBER_PATTERN = Regex(
    """(?:^|\b)S(\d{1,2})[ ._-]*E(\d{1,3})(?:\b|$)|(?:^|\b)(\d{1,2})x(\d{1,3})(?:\b|$)""",
    RegexOption.IGNORE_CASE,
)

internal data class NumberedIptvSeriesEpisode(
    val item: LiveTvVodItem,
    val season: Int,
    val episode: Int,
) {
    val code: String get() = "S${season.toString().padStart(2, '0')}E${episode.toString().padStart(2, '0')}"
}

internal fun List<LiveTvVodItem>.toNumberedSeriesEpisodes(): List<NumberedIptvSeriesEpisode> =
    mapIndexed { index, item ->
        val match = SERIES_EPISODE_NUMBER_PATTERN.find(item.title)
        val parsedSeason = match?.groupValues?.get(1)?.toIntOrNull()
            ?: match?.groupValues?.get(3)?.toIntOrNull()
        val parsedEpisode = match?.groupValues?.get(2)?.toIntOrNull()
            ?: match?.groupValues?.get(4)?.toIntOrNull()
        NumberedIptvSeriesEpisode(
            item = item,
            season = item.season.takeIf { it > 0 } ?: parsedSeason?.takeIf { it > 0 } ?: 1,
            episode = item.episode.takeIf { it > 0 } ?: parsedEpisode?.takeIf { it > 0 } ?: index + 1,
        )
    }.sortedWith(compareBy(NumberedIptvSeriesEpisode::season, NumberedIptvSeriesEpisode::episode, { it.item.title }))

internal fun buildIptvEpisodeWatchedProgress(
    series: LiveTvVodItem,
    episode: NumberedIptvSeriesEpisode,
): WatchProgress {
    val episodeItem = episode.item
    val parentContentId = episodeItem.seriesId.takeIf(String::isNotBlank)
        ?: "${series.sourceId}:series:${series.providerId}"
    val seriesName = episodeItem.seriesTitle.takeIf(String::isNotBlank) ?: series.title
    val seriesPoster = series.posterUrl.takeIf(String::isNotBlank)
    val episodeArtwork = episodeItem.posterUrl.takeIf(String::isNotBlank) ?: seriesPoster
    return WatchProgress(
        contentId = parentContentId,
        contentType = "series",
        name = seriesName,
        poster = seriesPoster,
        backdrop = episodeArtwork,
        logo = null,
        videoId = episodeItem.id,
        season = episode.season,
        episode = episode.episode,
        episodeTitle = episodeItem.title,
        position = 1L,
        duration = 1L,
        lastWatched = System.currentTimeMillis(),
        progressPercent = 100f,
    )
}

private fun mergeVodItems(existing: List<LiveTvVodItem>, batch: List<LiveTvVodItem>): List<LiveTvVodItem> {
    val merged = LinkedHashMap<String, LiveTvVodItem>(existing.size + batch.size)
    existing.forEach { merged[it.id] = it }
    batch.forEach { merged[it.id] = it }
    return merged.values.toList()
}

private fun preserveVodCategoryAssignments(
    incoming: List<LiveTvVodItem>,
    knownCategoryItems: List<LiveTvVodItem>,
): List<LiveTvVodItem> {
    val categoriesByItemId = knownCategoryItems
        .filter { it.categoryId.isNotBlank() }
        .associateBy(LiveTvVodItem::id)
    return incoming.map { item ->
        if (item.categoryId.isNotBlank()) item
        else categoriesByItemId[item.id]?.let { known ->
            item.copy(
                categoryId = known.categoryId,
                categoryName = item.categoryName.ifBlank { known.categoryName },
            )
        } ?: item
    }
}

internal suspend fun resolveIptvVod(source: LiveTvSource, item: LiveTvVodItem): LiveTvVodPlayback =
    when (source.type) {
        LiveTvSourceType.Xtream -> LiveTvVodPlayback(
            LiveTvXtream.vodStreamUrl(source, item),
            LIVE_TV_STREAM_HEADERS,
        )
        LiveTvSourceType.Stalker -> LiveTvStalker.resolveVod(source, item)
        LiveTvSourceType.M3u -> error("M3U sources do not have a VOD catalogue")
    }

internal suspend fun loadIptvSeriesEpisodes(source: LiveTvSource, series: LiveTvVodItem): List<LiveTvVodItem> =
    when (source.type) {
        LiveTvSourceType.Xtream -> LiveTvXtream.seriesEpisodes(source, series)
        LiveTvSourceType.Stalker -> LiveTvStalker.seriesEpisodes(source, series)
        LiveTvSourceType.M3u -> emptyList()
    }

@Composable
private fun VodSourceRow(
    source: LiveTvSource,
    confirmed: Boolean,
    focusRequester: FocusRequester? = null,
    rightFocusRequester: FocusRequester? = null,
    count: Int?,
    selected: Boolean,
    expanded: Boolean,
    onFocus: () -> Unit,
    onClick: () -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    LaunchedEffect(focused, selected) {
        // Focusing the already-selected row must not undo an explicit collapse.
        if (!focused || selected) return@LaunchedEffect
        delay(VOD_FOCUS_PREVIEW_DELAY_MS)
        if (focused) onFocus()
    }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .navigateRightTo(rightFocusRequester, onFocus)
            .onFocusChanged { focused = it.isFocused },
        colors = CardDefaults.colors(
            containerColor = if (confirmed) NuvioTheme.colors.Secondary else NuvioTheme.colors.SurfaceVariant,
            focusedContainerColor = if (confirmed) NuvioTheme.colors.Secondary else NuvioTheme.colors.SurfaceVariant,
        ),
        shape = CardDefaults.shape(shape = NuvioTheme.shapes.button),
        border = vodRailBorder(confirmed),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = source.label,
                modifier = Modifier.weight(1f)
                    .then(if (focused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier),
                color = if (confirmed) NuvioTheme.colors.OnSecondary else NuvioTheme.colors.TextPrimary,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            )
            if (count != null) {
                Text(
                    count.toString(),
                    color = if (confirmed) NuvioTheme.colors.OnSecondary else NuvioTheme.colors.TextSecondary,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (confirmed) NuvioTheme.colors.OnSecondary else NuvioTheme.colors.TextSecondary,
            )
        }
    }
}

@Composable
private fun VodPanelRow(
    title: String,
    selected: Boolean,
    confirmed: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    focusRequester: FocusRequester? = null,
    rightFocusRequester: FocusRequester? = null,
    upFocusRequester: FocusRequester? = null,
    onFocus: () -> Unit,
    onClick: () -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    LaunchedEffect(focused, selected) {
        if (!focused || selected) return@LaunchedEffect
        delay(VOD_FOCUS_PREVIEW_DELAY_MS)
        if (focused && !selected) onFocus()
    }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .navigateRightTo(rightFocusRequester, onFocus, upFocusRequester)
            .onFocusChanged { focused = it.isFocused },
        colors = CardDefaults.colors(
            containerColor = if (confirmed) NuvioTheme.colors.Secondary else NuvioTheme.colors.SurfaceVariant,
            focusedContainerColor = if (confirmed) NuvioTheme.colors.Secondary else NuvioTheme.colors.SurfaceVariant,
        ),
        shape = CardDefaults.shape(shape = NuvioTheme.shapes.button),
        border = vodRailBorder(confirmed),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (confirmed) NuvioTheme.colors.OnSecondary else NuvioTheme.colors.TextPrimary,
            )
            Text(
                text = title,
                modifier = Modifier.weight(1f)
                    .then(if (focused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier),
                color = if (confirmed) NuvioTheme.colors.OnSecondary else NuvioTheme.colors.TextPrimary,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun VodCategoryRow(
    title: String,
    selected: Boolean,
    confirmed: Boolean,
    focusRequester: FocusRequester? = null,
    rightFocusRequester: FocusRequester? = null,
    onFocus: () -> Unit,
    onClick: () -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    LaunchedEffect(focused, selected) {
        if (!focused || selected) return@LaunchedEffect
        delay(VOD_FOCUS_PREVIEW_DELAY_MS)
        if (focused && !selected) onFocus()
    }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(start = 7.dp)
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .navigateRightTo(rightFocusRequester, onFocus)
            .onFocusChanged { focused = it.isFocused },
        colors = CardDefaults.colors(
            containerColor = if (confirmed) NuvioTheme.colors.Secondary else NuvioTheme.colors.Background,
            focusedContainerColor = if (confirmed) NuvioTheme.colors.Secondary else NuvioTheme.colors.SurfaceVariant,
        ),
        shape = CardDefaults.shape(shape = NuvioTheme.shapes.button),
        border = vodRailBorder(confirmed),
    ) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 3.dp)
                .then(if (focused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier),
            color = if (confirmed) NuvioTheme.colors.OnSecondary else NuvioTheme.colors.TextPrimary,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

@Composable
private fun VodHistoryList(
    entries: List<WatchProgress>,
    sources: List<LiveTvSource>,
    cachedItems: List<LiveTvVodItem>,
    type: String,
    firstItemFocusRequester: FocusRequester,
    trashFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    onSelect: (LiveTvVodItem) -> Unit,
) {
    val context = LocalContext.current
    val firstFocusableEntryIndex = entries.indexOfFirst { entry ->
        cachedItems.any { item -> item.type == type && item.id == entry.contentId }
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        rowItemsIndexed(entries, key = { _, entry -> "${entry.contentId}:${entry.videoId}:${entry.lastWatched}" }) { index, entry ->
            val source = sources.firstOrNull { entry.contentId.startsWith("${it.id}:$type:") }
            val item = cachedItems.firstOrNull { it.type == type && it.id == entry.contentId }
            var focused by remember { mutableStateOf(false) }
            val watchedAt = remember(entry.lastWatched) {
                if (entry.lastWatched > 0L) DateUtils.getRelativeTimeSpanString(context, entry.lastWatched).toString() else ""
            }
            val sourceAndEpisode = buildString {
                source?.label?.let { append(it) }
                if (type == "series" && entry.season != null && entry.episode != null) {
                    if (isNotEmpty()) append(" · ")
                    append("S${entry.season}E${entry.episode}")
                    entry.episodeTitle?.takeIf(String::isNotBlank)?.let { append(" · ").append(it) }
                }
            }
            Card(
                onClick = { item?.let(onSelect) },
                modifier = Modifier.fillMaxWidth()
                    .then(if (index == firstFocusableEntryIndex) Modifier.focusRequester(firstItemFocusRequester) else Modifier)
                    .focusProperties {
                        canFocus = item != null
                        if (index == firstFocusableEntryIndex && item != null) up = trashFocusRequester
                    }
                    .onFocusChanged { focused = it.isFocused },
                colors = CardDefaults.colors(
                    containerColor = NuvioTheme.colors.SurfaceVariant,
                    focusedContainerColor = NuvioTheme.colors.FocusBackground,
                ),
                shape = CardDefaults.shape(shape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp))),
                border = vodAccentBorder(selected = false),
                scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val poster = entry.poster?.takeIf(String::isNotBlank) ?: item?.posterUrl?.takeIf(String::isNotBlank)
                    if (poster != null) {
                        AsyncImage(
                            model = poster,
                            contentDescription = entry.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(width = 42.dp, height = 58.dp).clip(NuvioTheme.configuredShape(RoundedCornerShape(7.dp))),
                        )
                    } else {
                        Box(
                            Modifier.size(width = 42.dp, height = 58.dp)
                                .clip(NuvioTheme.configuredShape(RoundedCornerShape(7.dp)))
                                .background(NuvioTheme.colors.Background),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = NuvioTheme.colors.TextSecondary)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.name,
                            modifier = Modifier.fillMaxWidth()
                                .then(if (focused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier),
                            color = NuvioTheme.colors.TextPrimary,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        val details = stringResource(R.string.iptv_vod_history_entry_info, sourceAndEpisode, watchedAt)
                        Text(
                            text = details,
                            color = NuvioTheme.colors.TextSecondary,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VodGridWithFocusedDetails(
    items: List<LiveTvVodItem>,
    favoriteVodKeys: Set<String>,
    focusedItem: LiveTvVodItem?,
    presentation: LiveTvVodPresentation?,
    heroHeight: androidx.compose.ui.unit.Dp,
    gridWidthFraction: Float = 1f,
    trailerPreviewVideoUrl: String?,
    trailerPreviewAudioUrl: String?,
    onTrailerError: () -> Unit,
    onTrailerEnded: () -> Unit,
    onFocusedItemChanged: (LiveTvVodItem?) -> Unit,
    firstItemFocusRequester: FocusRequester,
    leftFocusRequester: FocusRequester,
    onToggleFavorite: ((LiveTvVodItem) -> Unit)? = null,
    onSelect: (LiveTvVodItem) -> Unit,
    emptyMessage: String? = null,
    modifier: Modifier = Modifier,
) {
    val heroItem = focusedItem?.let { focused ->
        items.firstOrNull { it.favoriteKey == focused.favoriteKey }
    }
    val previewUrl = trailerPreviewVideoUrl?.takeIf(String::isNotBlank)
    BoxWithConstraints(
        modifier = modifier.onFocusChanged { state ->
            if (!state.hasFocus) onFocusedItemChanged(null)
        },
    ) {
        val visibleHeroHeight = heroHeight
        heroItem?.let { item ->
            VodFocusedDetailsHero(
                item = item,
                presentation = presentation.takeIf { focusedItem?.favoriteKey == item.favoriteKey },
                trailerVideoUrl = previewUrl,
                trailerAudioUrl = trailerPreviewAudioUrl.takeIf { previewUrl != null },
                onTrailerError = onTrailerError,
                onTrailerEnded = onTrailerEnded,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(visibleHeroHeight),
            )
        }
        if (heroItem == null && visibleHeroHeight > 0.dp) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(visibleHeroHeight)
                    .background(NuvioTheme.colors.Background),
            )
        }
        val gridHeight = (maxHeight - visibleHeroHeight).coerceAtLeast(0.dp)
        val gridAlignment = if (gridWidthFraction < 1f) Alignment.BottomEnd else Alignment.BottomCenter
        val gridStartPadding = if (gridWidthFraction < 1f) 22.dp else 0.dp
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .align(gridAlignment)
                    .fillMaxWidth(gridWidthFraction)
                    .padding(start = gridStartPadding)
                    .height(gridHeight),
                contentAlignment = Alignment.Center,
            ) {
                emptyMessage?.let { CenterLabel(it) }
            }
        } else {
            VodGrid(
                items = items,
                favoriteVodKeys = favoriteVodKeys,
                focusedItem = focusedItem,
                onToggleFavorite = onToggleFavorite,
                onSelect = onSelect,
                modifier = Modifier
                    .align(gridAlignment)
                    .fillMaxWidth(gridWidthFraction)
                    .padding(start = gridStartPadding)
                    .height(gridHeight),
                onFocusedItemChanged = onFocusedItemChanged,
                firstItemFocusRequester = firstItemFocusRequester,
                leftFocusRequester = leftFocusRequester,
            )
        }
    }
}

@Composable
private fun VodFocusedDetailsInfoOverlay(
    item: LiveTvVodItem,
    presentation: LiveTvVodPresentation?,
    showSynopsis: Boolean,
    modifier: Modifier = Modifier,
) {
    val metadata = presentation?.metadata
    val title = metadata?.localizedTitle?.takeIf(String::isNotBlank)
        ?: normalizeLiveTvVodTitleForTmdb(item.title)
    val year = metadata?.releaseInfo?.take(4)?.takeIf(String::isNotBlank)
        ?: item.year.takeIf(String::isNotBlank)?.take(4)
    val details = buildList {
        metadata?.genres?.takeIf { it.isNotEmpty() }?.let { add(it.take(3).joinToString(" · ")) }
        year?.let(::add)
        metadata?.runtimeMinutes?.takeIf { it > 0 }?.let { add("$it min") }
        metadata?.rating?.let { add("★ ${"%.1f".format(Locale.getDefault(), it)}") }
    }.joinToString("  ·  ")
    val cast = metadata?.castMembers.orEmpty()
        .asSequence()
        .mapNotNull { it.name.trim().takeIf(String::isNotBlank) }
        .take(4)
        .joinToString(", ")
    val director = metadata?.director?.firstOrNull()?.takeIf(String::isNotBlank)
    val synopsis = metadata?.description?.takeIf(String::isNotBlank)
        ?: item.plot.takeIf(String::isNotBlank)
    var logoLoadFailed by remember(item.favoriteKey, metadata?.logo) { mutableStateOf(false) }
    var logoBitmap by remember(item.favoriteKey, metadata?.logo) { mutableStateOf<Bitmap?>(null) }
    val logoBackground = NuvioTheme.colors.Background
    val logoColorFilter = remember(logoBitmap, logoBackground) {
        logoBitmap
            ?.let { logoContrastTint(it, backdrop = null, fallbackBackground = logoBackground) }
            ?.let { ColorFilter.tint(it, BlendMode.SrcIn) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        if (!metadata?.logo.isNullOrBlank() && !logoLoadFailed) {
            AsyncImage(
                model = metadata?.logo,
                contentDescription = title,
                contentScale = ContentScale.Fit,
                alignment = Alignment.CenterStart,
                modifier = Modifier
                    .height(64.dp)
                    .widthIn(max = 320.dp)
                    .fillMaxWidth(),
                colorFilter = logoColorFilter,
                onSuccess = { state ->
                    logoBitmap = (state.result.image as? BitmapImage)?.bitmap
                },
                onError = {
                    logoLoadFailed = true
                    logoBitmap = null
                },
            )
        } else {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (details.isNotBlank()) {
            Text(
                text = details,
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (cast.isNotBlank()) {
            Text(
                text = "${stringResource(R.string.episode_folder_cast)} : $cast",
                color = Color.White.copy(alpha = 0.84f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (director != null) {
            Text(
                text = stringResource(R.string.hero_director, director),
                color = Color.White.copy(alpha = 0.84f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (showSynopsis && !synopsis.isNullOrBlank()) {
            Text(
                text = synopsis,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                maxLines = Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun VodFocusedDetailsHero(
    item: LiveTvVodItem,
    presentation: LiveTvVodPresentation?,
    trailerVideoUrl: String?,
    trailerAudioUrl: String?,
    onTrailerError: () -> Unit,
    onTrailerEnded: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val metadata = presentation?.metadata
    val artwork = metadata?.backdrop?.takeIf(String::isNotBlank)
        ?: tmdbVodImageUrl(presentation?.match?.backdropPath, "w1280")
        ?: item.posterUrl.takeIf(String::isNotBlank)
        ?: metadata?.poster
        ?: tmdbVodImageUrl(presentation?.match?.posterPath, "w500")
    val isTrailerPreviewPlaying = !trailerVideoUrl.isNullOrBlank()

    Box(
        modifier = modifier
            .clip(NuvioTheme.configuredShape(RoundedCornerShape(12.dp)))
            .background(NuvioTheme.colors.Background),
    ) {
        Row(Modifier.fillMaxSize()) {
            VodFocusedDetailsInfoOverlay(
                item = item,
                presentation = presentation,
                showSynopsis = !isTrailerPreviewPlaying,
                modifier = Modifier
                    .weight(0.42f)
                    .fillMaxHeight()
                    .background(NuvioTheme.colors.Background)
                    .padding(start = 22.dp, end = 16.dp, top = 18.dp, bottom = 18.dp),
            )
            Box(
                modifier = Modifier
                    .weight(0.58f)
                    .fillMaxHeight(),
            ) {
                if (!isTrailerPreviewPlaying && !artwork.isNullOrBlank()) {
                    AsyncImage(
                        model = artwork,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                TrailerPlayer(
                    trailerUrl = trailerVideoUrl,
                    trailerAudioUrl = trailerAudioUrl,
                    isPlaying = !trailerVideoUrl.isNullOrBlank(),
                    onEnded = onTrailerEnded,
                    onError = onTrailerError,
                    muted = false,
                    cropToFill = true,
                    modifier = Modifier.fillMaxSize(),
                )
                ModernHeroGradientLayer(
                    bgColor = NuvioTheme.colors.Background,
                    isFullScreen = { false },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

private fun tmdbVodImageUrl(path: String?, size: String): String? =
    path?.takeIf { it.startsWith('/') }
        ?.let { "https://image.tmdb.org/t/p/$size$it" }

@Composable
private fun vodAccentBorder(selected: Boolean): CardBorder = CardDefaults.border(
    border = if (selected) {
        Border(BorderStroke(1.dp, NuvioTheme.colors.Secondary.copy(alpha = 0.72f)))
    } else {
        Border.None
    },
    focusedBorder = Border(BorderStroke(2.dp, NuvioTheme.colors.FocusRing)),
)

@Composable
private fun vodRailBorder(confirmed: Boolean): CardBorder {
    val amber = Border(BorderStroke(2.dp, NuvioTheme.colors.Secondary))
    return CardDefaults.border(
        border = if (confirmed) amber else Border.None,
        focusedBorder = if (confirmed) amber else Border(
            BorderStroke(2.dp, NuvioTheme.colors.TextPrimary),
        ),
    )
}

@Composable
internal fun VodGrid(
    items: List<LiveTvVodItem>,
    favoriteVodKeys: Set<String> = emptySet(),
    focusedItem: LiveTvVodItem? = null,
    onToggleFavorite: ((LiveTvVodItem) -> Unit)? = null,
    onSelect: (LiveTvVodItem) -> Unit,
    modifier: Modifier = Modifier,
    onFocusedItemChanged: ((LiveTvVodItem?) -> Unit)? = null,
    firstItemFocusRequester: FocusRequester? = null,
    leftFocusRequester: FocusRequester? = null,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val columns = VOD_GRID_COLUMNS
        val focusedIndex = focusedItem?.let { focused ->
            items.indexOfFirst { it.favoriteKey == focused.favoriteKey }
        } ?: -1
        val gridState = rememberLazyGridState()
        LaunchedEffect(focusedIndex, columns) {
            if (focusedIndex >= 0) {
                val rowStartIndex = focusedIndex - focusedIndex % columns
                // Snap each focused row below the hero instead of leaving the previous
                // row's title strip at the top edge while the next posters are clipped.
                gridState.scrollToItem(rowStartIndex)
            }
        }
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(VOD_GRID_COLUMNS),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                vertical = VOD_GRID_VERTICAL_CONTENT_PADDING,
                horizontal = VOD_GRID_HORIZONTAL_CONTENT_PADDING,
            ),
            horizontalArrangement = Arrangement.spacedBy(VOD_GRID_ITEM_SPACING),
            verticalArrangement = Arrangement.spacedBy(VOD_GRID_ITEM_SPACING),
        ) {
            gridItemsIndexed(items, key = { _, item -> "${item.sourceId}:${item.id}" }) { index, item ->
                VodGridCard(
                    item = item,
                    index = index,
                    favoriteVodKeys = favoriteVodKeys,
                    onToggleFavorite = onToggleFavorite,
                    onSelect = onSelect,
                    onFocusedItemChanged = onFocusedItemChanged,
                    firstItemFocusRequester = firstItemFocusRequester,
                    leftFocusRequester = leftFocusRequester,
                )
            }
        }
    }
}

@Composable
private fun VodGridCard(
    item: LiveTvVodItem,
    index: Int,
    favoriteVodKeys: Set<String>,
    onToggleFavorite: ((LiveTvVodItem) -> Unit)?,
    onSelect: (LiveTvVodItem) -> Unit,
    onFocusedItemChanged: ((LiveTvVodItem?) -> Unit)?,
    firstItemFocusRequester: FocusRequester?,
    leftFocusRequester: FocusRequester?,
) {
    var focused by remember { mutableStateOf(false) }
    val isFavorite = item.favoriteKey in favoriteVodKeys
    val isFirstInRow = index % VOD_GRID_COLUMNS == 0
    Card(
        onClick = { onSelect(item) },
        onLongClick = onToggleFavorite?.let { toggle -> ({ toggle(item) }) },
        modifier = Modifier.fillMaxWidth()
            .then(if (index == 0 && firstItemFocusRequester != null) Modifier.focusRequester(firstItemFocusRequester) else Modifier)
            .then(
                if (isFirstInRow && leftFocusRequester != null) {
                    Modifier.focusProperties {
                        leftFocusRequester?.let { left = it }
                    }
                } else Modifier,
            )
            .onFocusChanged {
                focused = it.isFocused
                if (it.isFocused) onFocusedItemChanged?.invoke(item)
            },
        colors = CardDefaults.colors(
            containerColor = NuvioTheme.colors.SurfaceVariant,
            focusedContainerColor = NuvioTheme.colors.FocusBackground,
        ),
        shape = CardDefaults.shape(shape = NuvioTheme.shapes.posterCard),
        border = vodAccentBorder(selected = false),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
    ) {
        Column {
            Box {
                AsyncImage(
                    model = item.posterUrl.takeIf(String::isNotBlank),
                    contentDescription = normalizeLiveTvVodTitleForTmdb(item.title),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f)
                        .background(Color.Black)
                        .clip(NuvioTheme.configuredShape(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))),
                )
                if (isFavorite) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = NuvioTheme.colors.Secondary,
                        modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(18.dp),
                    )
                }
            }
            Text(
                text = normalizeLiveTvVodTitleForTmdb(item.title),
                modifier = Modifier.padding(start = 6.dp, end = 6.dp, top = 5.dp, bottom = 7.dp)
                    .then(if (focused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier),
                color = NuvioTheme.colors.TextPrimary,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun CenterLabel(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, color = NuvioTheme.colors.TextSecondary)
    }
}
