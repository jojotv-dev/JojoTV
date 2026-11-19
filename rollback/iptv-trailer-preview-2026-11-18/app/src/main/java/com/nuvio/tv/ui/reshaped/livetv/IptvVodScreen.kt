@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.nuvio.tv.ui.reshaped.livetv

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.tv.material3.Card
import androidx.tv.material3.CardBorder
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Border
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import android.text.format.DateUtils
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import coil3.compose.AsyncImage
import com.nuvio.tv.R
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.ui.components.EpisodeBackdropInfo
import com.nuvio.tv.ui.components.EpisodeBackdropLayout
import com.nuvio.tv.ui.components.EpisodeRailItem
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale

private enum class IptvVodPanel { Sources, Favorites, History }
private const val VOD_FOCUS_PREVIEW_DELAY_MS = 220L
private const val VOD_FOCUSED_HERO_ASPECT_RATIO = 2.25f

@Composable
private fun Modifier.navigateRightTo(
    target: FocusRequester?,
    onNavigate: () -> Unit,
): Modifier {
    if (target == null) return this
    val scope = rememberCoroutineScope()
    return focusProperties { right = target }
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
    val lifecycleOwner = LocalLifecycleOwner.current
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
    val firstSourceId = sources.firstOrNull()?.id
    var selectedSourceId by rememberSaveable(type) { mutableStateOf<String?>(null) }
    var catalogRefreshVersion by remember(type) { mutableIntStateOf(0) }
    var selectedPanel by rememberSaveable(type) { mutableStateOf(IptvVodPanel.Sources) }
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
    var categoryFallbackItemsByKey by remember(type) {
        mutableStateOf<Map<String, List<LiveTvVodItem>>>(emptyMap())
    }
    var categoryProviderOrderByKey by remember(type, catalogRefreshVersion) {
        mutableStateOf<Map<String, List<LiveTvVodItem>>>(emptyMap())
    }
    var focusedVodItem by remember(type) { mutableStateOf<LiveTvVodItem?>(null) }
    var focusedVodPresentation by remember(type) { mutableStateOf<LiveTvVodPresentation?>(null) }
    var posterLookupBatch by remember(type) { mutableIntStateOf(0) }
    val posterLookupAttemptedKeys = remember(type) { mutableSetOf<String>() }
    var isLoadingSources by remember(type) { mutableStateOf(false) }
    var isLoadingItems by remember(type) { mutableStateOf(false) }
    var loadingCategoryFallbackKey by remember(type) { mutableStateOf<String?>(null) }
    var categoryFallbackCachePendingSource by remember(type) { mutableStateOf<String?>(null) }
    val categoryFallbackAttemptedKeys = remember(type) { mutableSetOf<String>() }
    var hasCategoryLoadError by remember(type) { mutableStateOf(false) }
    var hasLoadError by remember(type) { mutableStateOf(false) }
    var hasPlaybackError by remember(type) { mutableStateOf(false) }
    var showClearHistoryDialog by remember(type) { mutableStateOf(false) }

    // Providers can change their catalog while the viewer is in another page. Reload on return
    // while still showing the cached catalog immediately; skip the observer's initial RESUME.
    DisposableEffect(lifecycleOwner, type) {
        var skipInitialResume = true
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (skipInitialResume) skipInitialResume = false else catalogRefreshVersion++
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
        val cachedCategories = sources.mapNotNull { source ->
            LiveTvVodCache.readCategories(context, source.id, type)
                .withDisplayableIptvCategoryNames()
                .takeIf { it.isNotEmpty() }?.let { source.id to it }
        }.toMap()
        categoriesBySource = cachedCategories
        if (sources.isEmpty()) {
            selectedSourceId = null
            selectedCategoryId = null
            selectedSeries = null
            episodes = emptyList()
            items = emptyList()
            isLoadingSources = false
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
        isLoadingSources = true
        hasCategoryLoadError = false
        try {
            val results = coroutineScope {
                sources.map { source -> async {
                    try {
                        source.id to loadVodCategories(source, type)
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
            mergedCategories.forEach { (sourceId, categories) ->
                LiveTvVodCache.writeCategories(context, sourceId, type, categories)
            }
            hasCategoryLoadError = successful.size != sources.size
        } catch (failure: Exception) {
            if (failure is CancellationException) throw failure
            hasCategoryLoadError = true
        } finally {
            isLoadingSources = false
        }
    }

    LaunchedEffect(sources, type, selectedSourceId, catalogRefreshVersion) {
        val source = sources.firstOrNull { it.id == selectedSourceId }
        if (source == null) {
            items = emptyList()
            return@LaunchedEffect
        }
        items = emptyList()
        items = LiveTvVodCache.read(context, source.id, type)
        cachedVodItems = mergeVodItems(cachedVodItems, items)
        isLoadingItems = true
        hasLoadError = false
        hasPlaybackError = false
        try {
            coroutineScope {
                val batches = Channel<List<LiveTvVodItem>>(Channel.UNLIMITED)
                val collector = launch {
                    for (batch in batches) {
                        val fallbackPrefix = "$type:${source.id}:"
                        val categoryFallbackItems = categoryFallbackItemsByKey
                            .filterKeys { it.startsWith(fallbackPrefix) && it.endsWith(":$catalogRefreshVersion") }
                            .values
                            .flatten()
                        val knownCategoryItems = mergeVodItems(
                            cachedVodItems.filter { it.sourceId == source.id && it.categoryId.isNotBlank() },
                            categoryFallbackItems,
                        )
                        val correctedBatch = preserveVodCategoryAssignments(batch, knownCategoryItems)
                        items = mergeVodItems(mergeVodItems(items, correctedBatch), categoryFallbackItems)
                        cachedVodItems = mergeVodItems(
                            mergeVodItems(cachedVodItems, correctedBatch), categoryFallbackItems,
                        )
                    }
                }
                var loadedItems: List<LiveTvVodItem>? = null
                var loadTimedOut = false
                try {
                    loadedItems = withTimeoutOrNull(VOD_LOAD_TIMEOUT_MS) {
                        loadVodItems(source, type, "") { batch ->
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
                    val fallbackPrefix = "$type:${source.id}:"
                    val categoryFallbackItems = categoryFallbackItemsByKey
                        .filterKeys { it.startsWith(fallbackPrefix) && it.endsWith(":$catalogRefreshVersion") }
                        .values
                        .flatten()
                    val knownCategoryItems = mergeVodItems(
                        cachedVodItems.filter { it.sourceId == source.id && it.categoryId.isNotBlank() },
                        categoryFallbackItems,
                    )
                    val assignedLoadedItems = preserveVodCategoryAssignments(loadedItems, knownCategoryItems)
                    val refreshedItems = mergeVodItems(assignedLoadedItems, categoryFallbackItems)
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
                LiveTvVodCache.write(context, source.id, type, items)
            }
        } catch (failure: Exception) {
            if (failure is CancellationException) throw failure
            hasLoadError = true
        } finally {
            isLoadingItems = false
            if (categoryFallbackCachePendingSource == source.id && selectedSourceId == source.id) {
                categoryFallbackCachePendingSource = null
                LiveTvVodCache.write(context, source.id, type, items)
            }
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
        delay(VOD_FOCUS_PREVIEW_DELAY_MS)
        val presentation = liveTvModel.tmdbVodPresentationForVod(
            title = vodPosterLookupTitle(item),
            type = item.type,
            year = item.year,
        )
        if (focusedVodItem?.let(::vodPosterLookupKey) == vodPosterLookupKey(item)) {
            focusedVodPresentation = presentation
        }
    }

    val selectedSource = sources.firstOrNull { it.id == selectedSourceId }
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
    val selectedCategory = visibleCategories.firstOrNull { it.id == selectedCategoryId }
    val selectedCategoryFallbackKey = selectedCategory?.let {
        "$type:${it.sourceId}:${it.id}:$catalogRefreshVersion"
    }
    val selectedCategoryHasItems = selectedCategory?.let { category ->
        items.any { it.sourceId == category.sourceId && it.categoryId.trim() == category.id }
    } == true
    val providerOrderedSeriesItems = if (type == "series") {
        selectedCategoryFallbackKey?.let(categoryProviderOrderByKey::get).orEmpty()
    } else {
        emptyList()
    }
    val visibleItems = (providerOrderedSeriesItems.takeIf { it.isNotEmpty() } ?: items).filter { item ->
        val hidden = item.categoryId.isNotBlank() &&
            LiveTvRepository.vodCategoryHideKey(type, item.sourceId, item.categoryId.trim()) in hiddenVodCategoryKeys
        !hidden && (selectedCategoryId == null || item.categoryId.trim() == selectedCategoryId)
    }

    // Use the provider's category list for series groups: the cached full catalogue can have a
    // different order even when it already contains the group's items.
    LaunchedEffect(
        selectedPanel,
        selectedSeries?.favoriteKey,
        selectedSourceId,
        selectedCategoryId,
        selectedCategory?.sourceId,
        selectedCategory?.id,
        type,
        catalogRefreshVersion,
    ) {
        if (selectedPanel != IptvVodPanel.Sources || selectedSeries != null) {
            return@LaunchedEffect
        }
        val category = selectedCategory ?: return@LaunchedEffect
        val source = sources.firstOrNull { it.id == category.sourceId } ?: return@LaunchedEffect
        val fallbackKey = "$type:${source.id}:${category.id}:$catalogRefreshVersion"
        if (type != "series" && selectedCategoryHasItems) return@LaunchedEffect
        if (type == "series" && categoryProviderOrderByKey[fallbackKey].orEmpty().isNotEmpty()) return@LaunchedEffect
        if (fallbackKey in categoryFallbackAttemptedKeys) return@LaunchedEffect

        try {
            delay(VOD_FOCUS_PREVIEW_DELAY_MS)
            if (type != "series" && items.any { it.sourceId == source.id && it.categoryId.trim() == category.id }) {
                return@LaunchedEffect
            }
            if (!categoryFallbackAttemptedKeys.add(fallbackKey)) return@LaunchedEffect
            if (!selectedCategoryHasItems) loadingCategoryFallbackKey = fallbackKey
            val categoryItems = coroutineScope {
                val batches = Channel<List<LiveTvVodItem>>(Channel.UNLIMITED)
                val collector = launch {
                    for (batch in batches) {
                        val assigned = batch.map { item ->
                            item.copy(
                                categoryId = category.id,
                                categoryName = item.categoryName.ifBlank { category.name },
                            )
                        }
                        categoryFallbackItemsByKey = categoryFallbackItemsByKey + (
                            fallbackKey to mergeVodItems(
                                categoryFallbackItemsByKey[fallbackKey].orEmpty(),
                                assigned,
                            )
                        )
                        items = mergeVodItems(items, assigned)
                        cachedVodItems = mergeVodItems(cachedVodItems, assigned)
                    }
                }
                try {
                    loadVodItems(source, type, category.id) { batch -> batches.trySend(batch) }
                } finally {
                    batches.close()
                    collector.join()
                }
            }.map { item ->
                item.copy(
                    categoryId = category.id,
                    categoryName = item.categoryName.ifBlank { category.name },
                )
            }
            if (categoryItems.isNotEmpty()) {
                val orderedCategoryItems = categoryItems.distinctBy(LiveTvVodItem::id)
                if (type == "series") {
                    categoryProviderOrderByKey = categoryProviderOrderByKey + (fallbackKey to orderedCategoryItems)
                }
                categoryFallbackItemsByKey = categoryFallbackItemsByKey + (
                    fallbackKey to mergeVodItems(
                        categoryFallbackItemsByKey[fallbackKey].orEmpty(),
                        categoryItems,
                    )
                )
                items = mergeVodItems(items, categoryItems)
                cachedVodItems = mergeVodItems(cachedVodItems, categoryItems)
                if (isLoadingItems) {
                    categoryFallbackCachePendingSource = source.id
                } else {
                    LiveTvVodCache.write(context, source.id, type, items)
                }
            }
        } catch (failure: Exception) {
            if (failure is CancellationException) {
                categoryFallbackAttemptedKeys.remove(fallbackKey)
                throw failure
            }
            categoryFallbackAttemptedKeys.remove(fallbackKey)
            hasLoadError = true
            Log.w("LiveTvVod", "Failed to load $type category ${category.name} from ${source.type}", failure)
        } finally {
            if (loadingCategoryFallbackKey == fallbackKey) loadingCategoryFallbackKey = null
        }
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

    fun openVodItem(item: LiveTvVodItem) {
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

    fun focusVodSource(sourceId: String) {
        val switchingSource = selectedSourceId != sourceId
        if (switchingSource) {
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

    val seriesForDetail = selectedSeries
    Column(
        modifier = Modifier.fillMaxSize()
            .background(NuvioTheme.colors.Background)
            .then(
                if (selectedSeries != null) Modifier
                else Modifier.padding(start = 30.dp, end = 30.dp, top = 0.dp, bottom = 0.dp)
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
                            seasonCount = metadata?.seasonCount ?: seriesSeasons.size.takeIf { it > 0 },
                            seriesStatus = metadata?.status,
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
                        onToggleLibrary = { LiveTvRepository.toggleVodFavorite(series) },
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
            (liveTvState.isLoading || isLoadingSources || isLoadingItems ||
                loadingCategoryFallbackKey != null && loadingCategoryFallbackKey == selectedCategoryFallbackKey)
        val hasPartialCatalogLoadError = selectedPanel == IptvVodPanel.Sources &&
            selectedSeries == null &&
            (hasCategoryLoadError || hasLoadError && items.isNotEmpty())
        val showCatalogStatusOverlay = isCatalogLoading || hasPartialCatalogLoadError
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(0.27f).fillMaxHeight().padding(top = 68.dp),
                contentPadding = PaddingValues(bottom = 12.dp, end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                item(key = "vod-favorites") {
                    VodPanelRow(
                        title = stringResource(R.string.iptv_vod_favorites),
                        selected = selectedPanel == IptvVodPanel.Favorites,
                        icon = Icons.Default.Star,
                        focusRequester = selectedVodRailFocusRequester.takeIf { selectedPanel == IptvVodPanel.Favorites },
                        rightFocusRequester = firstVodItemFocusRequester.takeIf { selectedPanel == IptvVodPanel.Favorites },
                        onFocus = { focusVodPanel(IptvVodPanel.Favorites) },
                        onClick = { focusVodPanel(IptvVodPanel.Favorites) },
                    )
                }
                item(key = "vod-history") {
                    VodPanelRow(
                        title = stringResource(R.string.iptv_vod_history),
                        selected = selectedPanel == IptvVodPanel.History,
                        icon = Icons.Default.History,
                        onFocus = { focusVodPanel(IptvVodPanel.History) },
                        onClick = { focusVodPanel(IptvVodPanel.History) },
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
                        .filterNot { category ->
                            LiveTvRepository.vodCategoryHideKey(type, source.id, category.id) in hiddenVodCategoryKeys
                        }
                    val expanded = source.id in expandedSourceIds
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        VodSourceRow(
                            source = source,
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
                                val wasSelected = selectedSourceId == source.id
                                if (!wasSelected) {
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
                                title = stringResource(R.string.iptv_vod_all),
                                selected = selectedSourceId == source.id && selectedCategoryId == null,
                                focusRequester = selectedVodRailFocusRequester.takeIf {
                                    selectedPanel == IptvVodPanel.Sources &&
                                        selectedSourceId == source.id && selectedCategoryId == null
                                },
                                rightFocusRequester = firstVodItemFocusRequester,
                                onFocus = { focusVodCategory(source.id, null) },
                                onClick = { focusVodCategory(source.id, null) },
                            )
                            categories.forEach { category ->
                                VodCategoryRow(
                                    title = category.name,
                                    selected = selectedSourceId == source.id && selectedCategoryId == category.id,
                                    focusRequester = selectedVodRailFocusRequester.takeIf {
                                        selectedPanel == IptvVodPanel.Sources &&
                                            selectedSourceId == source.id && selectedCategoryId == category.id
                                    },
                                    rightFocusRequester = firstVodItemFocusRequester,
                                    onFocus = { focusVodCategory(source.id, category.id) },
                                    onClick = { focusVodCategory(source.id, category.id) },
                                )
                            }
                        }
                    }
                }
            }
            Box(
                Modifier.fillMaxHeight().width(1.dp)
                    .background(NuvioTheme.colors.SurfaceVariant)
            )
            Box(
                modifier = Modifier.weight(0.73f).fillMaxHeight().padding(start = 22.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (hasPlaybackError) {
                        Text(
                            text = stringResource(R.string.iptv_vod_playback_error),
                            color = NuvioTheme.colors.TextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    when {
                        selectedSeries != null && isLoadingItems && episodes.isEmpty() -> CenterLabel(stringResource(R.string.iptv_vod_loading))
                        selectedSeries != null && hasLoadError && episodes.isEmpty() -> CenterLabel(stringResource(R.string.iptv_vod_error))
                        selectedSeries != null && episodes.isEmpty() -> CenterLabel(stringResource(R.string.iptv_vod_empty_episodes))
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
                        selectedPanel == IptvVodPanel.Favorites && moduleFavorites.isEmpty() -> CenterLabel(stringResource(R.string.iptv_vod_empty_favorites))
                        selectedPanel == IptvVodPanel.Favorites -> VodGridWithFocusedDetails(
                            items = moduleFavorites,
                            favoriteVodKeys = favoriteVodKeys,
                            focusedItem = focusedVodItem,
                            presentation = focusedVodPresentation,
                            onFocusedItemChanged = { focusedVodItem = it },
                            firstItemFocusRequester = firstVodItemFocusRequester,
                            leftFocusRequester = selectedVodRailFocusRequester,
                            onToggleFavorite = { item ->
                                LiveTvRepository.toggleVodFavorite(item)
                                scope.launch { LiveTvVodCache.upsert(context, item) }
                            },
                            onSelect = ::openVodItem,
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                        )
                        selectedPanel == IptvVodPanel.History && moduleHistory.isEmpty() -> CenterLabel(stringResource(R.string.iptv_vod_history_empty))
                        selectedPanel == IptvVodPanel.History -> VodHistoryList(
                            entries = moduleHistory,
                            sources = sources,
                            cachedItems = cachedVodItems,
                            type = type,
                            onSelect = ::openVodItem,
                        )
                        sources.isEmpty() && liveTvState.isLoading -> Unit
                        sources.isEmpty() -> CenterLabel(stringResource(R.string.iptv_vod_no_xtream))
                        isLoadingSources && selectedSource == null -> Unit
                        selectedSource == null -> CenterLabel(stringResource(R.string.iptv_vod_empty))
                        isLoadingItems && (if (selectedSeries == null) items.isEmpty() else episodes.isEmpty()) -> Unit
                        loadingCategoryFallbackKey != null && loadingCategoryFallbackKey == selectedCategoryFallbackKey -> Unit
                        hasLoadError && (selectedSeries == null && items.isEmpty() || selectedSeries != null && episodes.isEmpty()) ->
                            CenterLabel(stringResource(R.string.iptv_vod_error))
                        visibleItems.isEmpty() -> CenterLabel(stringResource(R.string.iptv_vod_empty))
                        else -> VodGridWithFocusedDetails(
                            items = visibleItems,
                            favoriteVodKeys = favoriteVodKeys,
                            focusedItem = focusedVodItem,
                            presentation = focusedVodPresentation,
                            onFocusedItemChanged = { focusedVodItem = it },
                            firstItemFocusRequester = firstVodItemFocusRequester,
                            leftFocusRequester = selectedVodRailFocusRequester,
                            onToggleFavorite = { item ->
                                LiveTvRepository.toggleVodFavorite(item)
                                scope.launch { LiveTvVodCache.upsert(context, item) }
                            },
                            onSelect = ::openVodItem,
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                        )
                    }
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
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(0.27f)
                .height(68.dp)
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
                if (selectedPanel == IptvVodPanel.Sources && selectedSource != null) {
                    Text(
                        text = selectedSource.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = NuvioTheme.colors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (selectedPanel == IptvVodPanel.History) {
                Card(
                    onClick = { if (moduleHistory.isNotEmpty()) showClearHistoryDialog = true },
                    modifier = Modifier.size(40.dp),
                    colors = CardDefaults.colors(
                        containerColor = NuvioTheme.colors.SurfaceVariant,
                        focusedContainerColor = NuvioTheme.colors.FocusBackground,
                    ),
                    shape = CardDefaults.shape(shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))),
                    border = vodAccentBorder(selected = false),
                    scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.iptv_vod_history_clear),
                            modifier = Modifier.size(18.dp),
                            tint = NuvioTheme.colors.TextPrimary,
                        )
                    }
                }
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
    categoryId: String,
    onBatch: (List<LiveTvVodItem>) -> Unit,
): List<LiveTvVodItem> = when (source.type) {
    LiveTvSourceType.Xtream -> LiveTvXtream.vodItems(source, type, categoryId, onBatch)
    LiveTvSourceType.Stalker -> LiveTvStalker.vodItems(source, type, categoryId, onBatch)
    LiveTvSourceType.M3u -> emptyList()
}

private const val VOD_LOAD_TIMEOUT_MS = 180_000L

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
            containerColor = if (selected) NuvioTheme.colors.FocusBackground else NuvioTheme.colors.SurfaceVariant,
            focusedContainerColor = NuvioTheme.colors.FocusBackground,
        ),
        shape = CardDefaults.shape(shape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp))),
        border = vodAccentBorder(selected),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = source.label,
                modifier = Modifier.weight(1f)
                    .then(if (focused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier),
                color = NuvioTheme.colors.TextPrimary,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            )
            if (count != null) {
                Text(count.toString(), color = NuvioTheme.colors.TextSecondary, style = MaterialTheme.typography.labelSmall)
            }
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = NuvioTheme.colors.TextSecondary,
            )
        }
    }
}

@Composable
private fun VodPanelRow(
    title: String,
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier.weight(1f)
                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                .navigateRightTo(rightFocusRequester, onFocus)
                .onFocusChanged { focused = it.isFocused },
            colors = CardDefaults.colors(
                containerColor = if (selected) NuvioTheme.colors.FocusBackground else NuvioTheme.colors.SurfaceVariant,
                focusedContainerColor = NuvioTheme.colors.FocusBackground,
            ),
            shape = CardDefaults.shape(shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))),
            border = vodAccentBorder(selected),
            scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(15.dp), tint = NuvioTheme.colors.TextPrimary)
                Text(
                    text = title,
                    modifier = Modifier.weight(1f)
                        .then(if (focused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier),
                    color = NuvioTheme.colors.TextPrimary,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun VodCategoryRow(
    title: String,
    selected: Boolean,
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
            containerColor = if (selected) NuvioTheme.colors.FocusBackground else NuvioTheme.colors.Background,
            focusedContainerColor = NuvioTheme.colors.FocusBackground,
        ),
        shape = CardDefaults.shape(shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))),
        border = vodAccentBorder(selected),
    ) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)
                .then(if (focused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier),
            color = NuvioTheme.colors.TextPrimary,
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
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
    onSelect: (LiveTvVodItem) -> Unit,
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(entries, key = { "${it.contentId}:${it.videoId}:${it.lastWatched}" }) { entry ->
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
                    .focusProperties { canFocus = item != null }
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
    onFocusedItemChanged: (LiveTvVodItem?) -> Unit,
    firstItemFocusRequester: FocusRequester,
    leftFocusRequester: FocusRequester,
    onToggleFavorite: ((LiveTvVodItem) -> Unit)? = null,
    onSelect: (LiveTvVodItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val heroItem = focusedItem?.let { focused ->
        items.firstOrNull { it.favoriteKey == focused.favoriteKey }
    }
    BoxWithConstraints(modifier = modifier) {
        val heroHeight = minOf(maxWidth / VOD_FOCUSED_HERO_ASPECT_RATIO, maxHeight * 0.52f)
        heroItem?.let { item ->
            VodFocusedDetailsHero(
                item = item,
                presentation = presentation.takeIf { focusedItem?.favoriteKey == item.favoriteKey },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(heroHeight),
            )
        }
        VodGrid(
            items = items,
            favoriteVodKeys = favoriteVodKeys,
            onToggleFavorite = onToggleFavorite,
            onSelect = onSelect,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(if (heroItem == null) 1f else 0.52f),
            onFocusedItemChanged = onFocusedItemChanged,
            firstItemFocusRequester = firstItemFocusRequester,
            leftFocusRequester = leftFocusRequester,
        )
    }
}

@Composable
private fun VodFocusedDetailsHero(
    item: LiveTvVodItem,
    presentation: LiveTvVodPresentation?,
    modifier: Modifier = Modifier,
) {
    val metadata = presentation?.metadata
    val title = metadata?.localizedTitle?.takeIf(String::isNotBlank)
        ?: normalizeLiveTvVodTitleForTmdb(item.title)
    val artwork = metadata?.backdrop?.takeIf(String::isNotBlank)
        ?: tmdbVodImageUrl(presentation?.match?.backdropPath, "w1280")
        ?: item.posterUrl.takeIf(String::isNotBlank)
        ?: metadata?.poster
        ?: tmdbVodImageUrl(presentation?.match?.posterPath, "w500")
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

    Box(
        modifier = modifier
            .clip(NuvioTheme.configuredShape(RoundedCornerShape(12.dp)))
            .background(Color.Black),
    ) {
        if (!artwork.isNullOrBlank()) {
            AsyncImage(
                model = artwork,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Box(
            Modifier.fillMaxSize().background(
                Brush.horizontalGradient(
                    0f to Color.Black.copy(alpha = 0.96f),
                    0.58f to Color.Black.copy(alpha = 0.78f),
                    1f to Color.Transparent,
                )
            )
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.08f),
                    0.55f to Color.Transparent,
                    1f to Color.Black.copy(alpha = 0.62f),
                )
            )
        )
        Column(
            modifier = Modifier.align(Alignment.TopStart)
                .fillMaxWidth(0.64f)
                .fillMaxHeight()
                .padding(start = 18.dp, top = 12.dp, end = 14.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (!metadata?.logo.isNullOrBlank()) {
                AsyncImage(
                    model = metadata?.logo,
                    contentDescription = title,
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.CenterStart,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
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
            if (!synopsis.isNullOrBlank()) {
                Text(
                    text = synopsis,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = Int.MAX_VALUE,
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
internal fun VodGrid(
    items: List<LiveTvVodItem>,
    favoriteVodKeys: Set<String> = emptySet(),
    onToggleFavorite: ((LiveTvVodItem) -> Unit)? = null,
    onSelect: (LiveTvVodItem) -> Unit,
    modifier: Modifier = Modifier,
    onFocusedItemChanged: ((LiveTvVodItem?) -> Unit)? = null,
    firstItemFocusRequester: FocusRequester? = null,
    leftFocusRequester: FocusRequester? = null,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val contentWidth = (maxWidth - 8.dp).coerceAtLeast(0.dp)
        val columns = ((contentWidth.value + 10f) / 100f).toInt().coerceAtLeast(1)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 90.dp),
            modifier = Modifier.fillMaxSize().onFocusChanged { state ->
                if (!state.hasFocus) onFocusedItemChanged?.invoke(null)
            },
            contentPadding = PaddingValues(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(items, key = { _, item -> "${item.sourceId}:${item.id}" }) { index, item ->
                var focused by remember { mutableStateOf(false) }
                val isFavorite = item.favoriteKey in favoriteVodKeys
                Card(
                    onClick = { onSelect(item) },
                    onLongClick = onToggleFavorite?.let { toggle -> ({ toggle(item) }) },
                    modifier = Modifier.fillMaxWidth()
                        .then(if (index == 0 && firstItemFocusRequester != null) Modifier.focusRequester(firstItemFocusRequester) else Modifier)
                        .then(if (index % columns == 0 && leftFocusRequester != null) Modifier.focusProperties { left = leftFocusRequester } else Modifier)
                        .onFocusChanged {
                            focused = it.isFocused
                            if (it.isFocused) onFocusedItemChanged?.invoke(item)
                        },
                    colors = CardDefaults.colors(
                        containerColor = NuvioTheme.colors.SurfaceVariant,
                        focusedContainerColor = NuvioTheme.colors.FocusBackground,
                    ),
                    shape = CardDefaults.shape(shape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp))),
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
                                    .clip(NuvioTheme.configuredShape(RoundedCornerShape(10.dp))),
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
        }
    }
}

@Composable
private fun CenterLabel(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, color = NuvioTheme.colors.TextSecondary)
    }
}
