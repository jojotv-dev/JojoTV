package com.nuvio.tv.ui.screens.home

import android.util.Log
import android.os.SystemClock
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.data.local.EpisodeShuffleProfile
import com.nuvio.tv.data.freebox.FREEBOX_FOLDER_ID_PREFIX
import com.nuvio.tv.data.freebox.clearReplacedFreeboxProgress
import com.nuvio.tv.data.freebox.freeboxContentIdFor
import com.nuvio.tv.data.freebox.freeboxPathFromContentId
import com.nuvio.tv.core.apps.HOME_CONTINUE_WATCHING_ROW_KEY
import com.nuvio.tv.core.apps.HOME_FREEBOX_VIDEOS_ROW_KEY
import com.nuvio.tv.core.apps.isIndependentHomeCatalogKey
import com.nuvio.tv.core.apps.mergeHomeCatalogOrder
import com.nuvio.tv.core.sync.homeCatalogKey
import com.nuvio.tv.reshaped.livetv.LiveTvVodCache
import com.nuvio.tv.domain.model.ContinueWatchingSortMode
import com.nuvio.tv.domain.model.EpisodeShuffle
import com.nuvio.tv.domain.model.ShuffleSurface
import com.nuvio.tv.domain.model.Video
import com.nuvio.tv.domain.model.WatchProgress
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal data class HomeShuffleRefresh(val visit: Long = System.nanoTime(), val metadata: Int = 0)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
internal fun HomeViewModel.createShuffleHomeState(): StateFlow<HomeUiState> {
    val finished = finishedShuffleSeeds().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    viewModelScope.launch {
        finished.collectLatest { seeds -> seeds.forEach { resolveCwMeta(it) } }
    }
    val iptvVodPosterFlow = combine(
        profileManager.activeProfileId,
        watchProgressRepository.allProgress
    ) { profileId, progress ->
        profileId to progress.mapNotNull(WatchProgress::iptvVodCacheKey)
            .distinct()
            .sortedWith(compareBy({ it.first }, { it.second }))
    }.distinctUntilChanged().transformLatest { (profileId, sourceTypes) ->
        val posters = withContext(Dispatchers.IO) {
            sourceTypes.flatMap { (sourceId, type) ->
                LiveTvVodCache.read(appContext, sourceId, type)
            }.mapNotNull { item ->
                item.posterUrl.takeIf(String::isNotBlank)?.let { item.id to it }
            }.toMap()
        }
        if (profileId == profileManager.activeProfileId.value) emit(posters)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val rawFreeboxRowsFlow = freeboxRepository?.homeRows ?: flowOf(emptyList())
    viewModelScope.launch {
        rawFreeboxRowsFlow
            .map { rows ->
                rows.asSequence()
                    .flatMap { it.items.asSequence() }
                    .filter { it.id.startsWith("freebox:", ignoreCase = true) }
                    .mapNotNull { item ->
                        val path = freeboxPathFromContentId(item.id) ?: return@mapNotNull null
                        path to freeboxContentIdFor(
                            path = path,
                            sizeBytes = item.sourceFileSizeBytes,
                            lastModifiedMs = item.sourceFileLastModifiedMs,
                            fileId = item.sourceFileId,
                        )
                    }
                    .toMap()
            }
            .distinctUntilChanged()
            .collectLatest { currentContentIdsByPath ->
                if (currentContentIdsByPath.isEmpty()) return@collectLatest
                try {
                    watchProgressRepository.clearReplacedFreeboxProgress(currentContentIdsByPath)
                } catch (cancel: CancellationException) {
                    throw cancel
                } catch (error: Exception) {
                    Log.w("HomeFreebox", "Could not remove progress for replaced files", error)
                }
            }
    }
    val freeboxRowsFlow = combine(rawFreeboxRowsFlow, tmdbSettingsDataStore.settings) { rows, settings ->
        rows to settings
    }.transformLatest { (rows, settings) ->
        // Publish SMB files immediately; add verified title artwork progressively afterwards.
        emit(rows)
        if (!settings.enabled || !settings.useArtwork) return@transformLatest
        var enrichedRows = rows
        var checked = 0
        for (rowIndex in rows.indices) {
            val row = enrichedRows[rowIndex]
            var changed = false
            val items = row.items.toMutableList()
            for (itemIndex in items.indices) {
                if (checked++ >= 24) break
                val item = items[itemIndex]
                val isFreeboxFolder = item.id.startsWith(FREEBOX_FOLDER_ID_PREFIX)
                val hasManualFolderPoster = isFreeboxFolder &&
                    freeboxRepository?.folderArtwork(item.id.removePrefix(FREEBOX_FOLDER_ID_PREFIX))?.portrait != null
                if (hasManualFolderPoster || (!item.poster.isNullOrBlank() && !isFreeboxFolder)) continue
                val parentTitle = if (!isFreeboxFolder && item.id.startsWith("freebox:")) {
                    item.id.removePrefix("freebox:")
                        .substringBeforeLast('/', "")
                        .substringAfterLast('/')
                        .trim()
                        .takeIf(String::isNotBlank)
                } else null
                val numericEpisodeName = item.name.trim().matches(
                    Regex("(?i)^(?:episode\\s*)?\\d{1,4}$|^s\\d{1,3}e\\d{1,3}$")
                )
                val titleCandidates = buildList {
                    if (numericEpisodeName) parentTitle?.let { add(it) }
                    item.name.trim().takeIf(String::isNotBlank)?.let { if (it !in this) add(it) }
                    parentTitle?.let { if (it !in this) add(it) }
                }
                var poster: String? = null
                for (titleCandidate in titleCandidates) {
                    val cacheKey = "${settings.language}:${row.type}:${titleCandidate.lowercase()}"
                    poster = freeboxPosterCache[cacheKey]
                    if (poster != null) break
                    val missExpiresAt = freeboxPosterMisses[cacheKey]
                    if (missExpiresAt != null && missExpiresAt > SystemClock.elapsedRealtime()) continue
                    freeboxPosterMisses.remove(cacheKey)
                    poster = tmdbService.posterForExactTitle(
                        title = titleCandidate,
                        mediaType = row.type.toApiString(),
                        language = settings.language
                    )
                    if (poster != null) {
                        freeboxPosterCache[cacheKey] = poster
                        break
                    }
                    freeboxPosterMisses[cacheKey] =
                        SystemClock.elapsedRealtime() + FREEBOX_POSTER_MISS_TTL_MS
                }
                if (poster == null) continue
                items[itemIndex] = item.copy(poster = poster)
                changed = true
                enrichedRows = enrichedRows.toMutableList().also { updated ->
                    updated[rowIndex] = row.copy(items = items.toList())
                }
                emit(enrichedRows)
            }
            if (changed) {
                enrichedRows = enrichedRows.toMutableList().also { updated ->
                    updated[rowIndex] = row.copy(items = items.toList())
                }
            }
            if (checked >= 24) break
        }
    }
    val homeRowOrdering = combine(
        layoutPreferenceDataStore.homeCatalogOrderKeys,
        layoutPreferenceDataStore.disabledHomeCatalogKeys,
        layoutPreferenceDataStore.followAddonsOrder
    ) { orderKeys, disabledKeys, followAddonsOrder ->
        HomeRowOrdering(
            orderKeys = orderKeys,
            disabledKeys = disabledKeys.toSet() - HOME_CONTINUE_WATCHING_ROW_KEY,
            followAddonsOrder = followAddonsOrder
        )
    }
    val freeboxAugmentedHome = combine(
        _uiState,
        freeboxRowsFlow,
        iptvVodPosterFlow,
        homeRowOrdering
    ) { state, freeboxRows, iptvVodPosters, rowOrdering ->
        val freeboxPosters = freeboxRows.flatMap { it.items }.mapNotNull { item ->
            item.poster?.takeIf(String::isNotBlank)?.let { item.id to it }
        }.toMap()
        fun withHomeArtwork(item: ContinueWatchingItem): ContinueWatchingItem = when (item) {
            is ContinueWatchingItem.InProgress -> {
                val poster = freeboxPosters[item.progress.contentId] ?: iptvVodPosters[item.progress.contentId]
                if (poster == null || !item.progress.poster.isNullOrBlank()) item
                else item.copy(progress = item.progress.copy(poster = poster))
            }
            is ContinueWatchingItem.NextUp -> {
                val poster = freeboxPosters[item.info.contentId] ?: iptvVodPosters[item.info.contentId]
                if (poster == null || !item.info.poster.isNullOrBlank()) item
                else item.copy(info = item.info.copy(poster = poster))
            }
        }
        val catalogRows = state.catalogRows.filterNot { it.addonId.startsWith("freebox:") } +
            freeboxRows.filterNot { HOME_FREEBOX_VIDEOS_ROW_KEY in rowOrdering.disabledKeys }
        val nonFreeboxRows = state.homeRows.filterNot { row ->
            row is HomeRow.ContinueWatching ||
                (row is HomeRow.Catalog && row.row.addonId.startsWith("freebox:"))
        }
        val freeboxHomeRows = freeboxRows.map(HomeRow::Catalog)
        val continueWatchingHomeRow = if (
            state.continueWatchingEnabled &&
            (state.continueWatchingItems.isNotEmpty() || state.upcomingItems.isNotEmpty())
        ) listOf(HomeRow.ContinueWatching) else emptyList()
        val allHomeRows = nonFreeboxRows + continueWatchingHomeRow + freeboxHomeRows
        val pinnedRows = allHomeRows.filter { row ->
            row is HomeRow.CollectionRow && row.collection.pinToTop
        }
        val reorderableRows = allHomeRows - pinnedRows.toSet()
        fun rowKey(row: HomeRow): String = when (row) {
            is HomeRow.ContinueWatching -> HOME_CONTINUE_WATCHING_ROW_KEY
            is HomeRow.Catalog -> if (row.row.addonId.startsWith("freebox:")) {
                HOME_FREEBOX_VIDEOS_ROW_KEY
            } else {
                homeCatalogKey(row.row.addonId, row.row.apiType, row.row.catalogId)
            }
            is HomeRow.CollectionRow -> "collection_${row.collection.id}"
            is HomeRow.InstalledApps -> com.nuvio.tv.core.apps.INSTALLED_APPS_CATALOG_KEY
            is HomeRow.PlaceholderCatalog -> row.catalogKey
        }
        val rowsByKey = reorderableRows.groupBy(::rowKey)
        val availableRowKeys = reorderableRows.map(::rowKey)
        val availableIndependentKeys = availableRowKeys
            .filter { isIndependentHomeCatalogKey(it) || it == HOME_FREEBOX_VIDEOS_ROW_KEY }
            .distinct()
        val independentKeys = buildList {
            addAll(availableIndependentKeys.filter { it.startsWith("collection_") })
            addAll(availableIndependentKeys.filter { it == HOME_CONTINUE_WATCHING_ROW_KEY })
            addAll(availableIndependentKeys.filter { it.startsWith(com.nuvio.tv.core.apps.HOME_FAVORITES_ROW_KEY_PREFIX) })
            addAll(availableIndependentKeys.filter { it == com.nuvio.tv.core.apps.INSTALLED_APPS_CATALOG_KEY })
            addAll(availableIndependentKeys.filter { it == HOME_FREEBOX_VIDEOS_ROW_KEY })
        }
        val addonKeys = availableRowKeys.filterNot { it in independentKeys }.distinct()
        val effectiveOrder = mergeHomeCatalogOrder(
            addonKeys = addonKeys,
            independentKeys = independentKeys,
            savedOrderKeys = rowOrdering.orderKeys,
            followAddonsOrder = rowOrdering.followAddonsOrder,
            addonOwner = { key ->
                when (val row = rowsByKey[key]?.firstOrNull()) {
                    is HomeRow.Catalog -> row.row.addonId
                    is HomeRow.PlaceholderCatalog -> row.addonId
                    else -> null
                }
            }
        )
        val orderedHomeRows = buildList {
            addAll(pinnedRows)
            effectiveOrder.forEach { key ->
                if (key in rowOrdering.disabledKeys) return@forEach
                if (key == HOME_CONTINUE_WATCHING_ROW_KEY &&
                    (!state.continueWatchingEnabled ||
                        (state.continueWatchingItems.isEmpty() && state.upcomingItems.isEmpty()))
                ) return@forEach
                if (key == HOME_FREEBOX_VIDEOS_ROW_KEY) {
                    addAll(freeboxHomeRows)
                } else {
                    addAll(rowsByKey[key].orEmpty())
                }
            }
        }
        val gridGroups = state.gridItems
            .filterNot { item ->
                item is GridItem.Hero ||
                    gridItemHomeRowKey(item) == HOME_FREEBOX_VIDEOS_ROW_KEY ||
                    item is GridItem.ContinueWatchingRow
            }
            .groupBy(::gridItemHomeRowKey)
        val freeboxGridItems = freeboxRows.filterNot { HOME_FREEBOX_VIDEOS_ROW_KEY in rowOrdering.disabledKeys }
            .flatMap { row ->
                buildList<GridItem> {
                    add(GridItem.SectionDivider(
                        catalogName = row.catalogName,
                        catalogId = row.catalogId,
                        addonBaseUrl = row.addonBaseUrl,
                        addonId = row.addonId,
                        type = row.apiType
                    ))
                    row.items.take(24).forEach { item ->
                        add(GridItem.Content(
                            item = item,
                            addonBaseUrl = row.addonBaseUrl,
                            catalogId = row.catalogId,
                            catalogName = row.catalogName,
                            addonId = row.addonId
                        ))
                    }
                }
            }
        val orderedGridItems = buildList {
            addAll(state.gridItems.filterIsInstance<GridItem.Hero>())
            orderedHomeRows.map(::rowKey).distinct().forEach { key ->
                if (key == HOME_FREEBOX_VIDEOS_ROW_KEY) addAll(freeboxGridItems)
                else if (key == HOME_CONTINUE_WATCHING_ROW_KEY) add(GridItem.ContinueWatchingRow)
                else addAll(gridGroups[key].orEmpty())
            }
        }
        state.copy(
            catalogRows = catalogRows,
            homeRows = orderedHomeRows,
            gridItems = orderedGridItems,
            continueWatchingItems = state.continueWatchingItems.map(::withHomeArtwork),
            upcomingItems = state.upcomingItems.map(::withHomeArtwork)
        )
    }
    return combine(
        freeboxAugmentedHome,
        episodeShuffleStore.profiles,
        combine(watchProgressRepository.watchedItems, watchProgressRepository.allProgress) { watched, progress ->
            val keys = watched.mapNotNull { item ->
                item.season?.let { season -> item.episode?.let { item.contentId to (season to it) } }
            } + progress.filter { it.isCompleted() }.mapNotNull { item ->
                item.season?.let { season -> item.episode?.let { item.contentId to (season to it) } }
            }
            keys.groupBy({ it.first }, { it.second }).mapValues { it.value.toSet() }
        }.distinctUntilChanged(),
        shuffleHomeRefresh,
        finished
    ) { state, profile, watched, refresh, seeds ->
        val cards = seeds.mapNotNull { seed -> cachedCwMeta(seed.contentType, seed.contentId)?.let(seed::toShuffleCard) }
        applyHomeShuffle(state, profile, watched, episodeShuffle, refresh.visit, continueWatchingSortMode, cards) { id, type ->
            cachedCwMeta(type, id)?.videos?.map { video ->
                Video(video.id, video.title.orEmpty(), video.released, video.thumbnail,
                    season = video.season, episode = video.episode, overview = video.overview, available = video.available)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeUiState())
}

private data class HomeRowOrdering(
    val orderKeys: List<String>,
    val disabledKeys: Set<String>,
    val followAddonsOrder: Boolean
)

private fun gridItemHomeRowKey(item: GridItem): String? = when (item) {
    is GridItem.Hero -> null
    is GridItem.ContinueWatchingRow -> HOME_CONTINUE_WATCHING_ROW_KEY
    is GridItem.SectionDivider -> if (item.addonId.startsWith("freebox:")) {
        HOME_FREEBOX_VIDEOS_ROW_KEY
    } else {
        homeCatalogKey(item.addonId, item.type, item.catalogId)
    }
    is GridItem.Content -> if (item.addonId.startsWith("freebox:")) {
        HOME_FREEBOX_VIDEOS_ROW_KEY
    } else {
        homeCatalogKey(item.addonId, item.item.apiType, item.catalogId)
    }
    is GridItem.SeeAll -> homeCatalogKey(item.addonId, item.type, item.catalogId)
    is GridItem.CollectionHeader -> "collection_${item.collectionId}"
    is GridItem.CollectionFolder -> "collection_${item.collectionId}"
    is GridItem.InstalledAppsRow -> com.nuvio.tv.core.apps.INSTALLED_APPS_CATALOG_KEY
}

private const val FREEBOX_POSTER_MISS_TTL_MS = 5L * 60_000L

private fun WatchProgress.iptvVodCacheKey(): Pair<String, String>? {
    val marker = when {
        contentId.contains(":movie:") -> ":movie:"
        contentId.contains(":series:") -> ":series:"
        else -> return null
    }
    val sourceId = contentId.substringBefore(marker).takeIf(String::isNotBlank) ?: return null
    return sourceId to marker.removePrefix(":").removeSuffix(":")
}

private fun HomeViewModel.finishedShuffleSeeds() = combine(
    watchProgressRepository.observeNextUpSeeds(),
    traktSettingsDataStore.continueWatchingDaysCap,
    traktSettingsDataStore.dismissedNextUpKeys,
    episodeShuffleStore.profiles
) { seeds, daysCap, dismissed, profile ->
    val cutoff = watchProgressRepository.activeProviderContinueWatchingCutoffEpochMs(daysCap, System.currentTimeMillis())
    seeds.filter { seed ->
        profile.settings(seed.contentId, seed.contentType).enabled &&
            (seed.season ?: 0) > 0 && seed.episode != null &&
            (cutoff == null || seed.lastWatched >= cutoff) &&
            nextUpDismissKey(seed.contentId, seed.season, seed.episode) !in dismissed &&
            !watchProgressRepository.isDroppedShow(seed.contentId) &&
            shouldUseAsCompletedSeed(seed)
    }.sortedByDescending { it.lastWatched }.distinctBy { it.contentId }
}.distinctUntilChanged()

private fun HomeViewModel.cachedCwMeta(type: String, id: String) =
    cwMetaCache["$type:$id"] ?: cwMetaCache["series:$id"] ?: cwMetaCache["tv:$id"]

private fun WatchProgress.toShuffleCard(meta: CwMetaSummary) = ContinueWatchingItem.NextUp(NextUpInfo(
    contentId = contentId, contentType = contentType, name = name.ifBlank { meta.name },
    poster = poster ?: meta.poster, backdrop = backdrop ?: meta.backdropUrl, logo = logo ?: meta.logo,
    videoId = videoId, season = season ?: 1, episode = episode ?: 1, episodeTitle = null, thumbnail = null,
    lastWatched = lastWatched, sortTimestamp = lastWatched, seedSeason = season, seedEpisode = episode
))

internal fun applyHomeShuffle(
    state: HomeUiState,
    profile: EpisodeShuffleProfile,
    watched: Map<String, Set<Pair<Int, Int>>>,
    shuffle: EpisodeShuffle,
    visit: Long,
    sortMode: ContinueWatchingSortMode,
    finished: List<ContinueWatchingItem.NextUp> = emptyList(),
    videos: (String, String) -> List<Video>?
): HomeUiState {
    if (!profile.available || profile.shows.values.none { it.enabled }) return state
    val native = state.continueWatchingItems + state.upcomingItems
    val items = native + finished.filter { card ->
        profile.settings(card.info.contentId, card.info.contentType).enabled &&
            native.none { it.contentId() == card.info.contentId }
    }
    val resumable = items.filterIsInstance<ContinueWatchingItem.InProgress>()
        .filter { profile.settings(it.progress.contentId, it.progress.contentType).enabled }
        .groupBy { it.progress.contentId }
        .mapValues { (_, episodes) -> episodes.maxBy { it.progress.lastWatched } }
    val projected = items.mapNotNull { item ->
        when (item) {
            is ContinueWatchingItem.InProgress -> {
                val latest = resumable[item.progress.contentId]
                if (latest != null && latest != item) null else item.copy(shufflePlayback = latest != null)
            }
            is ContinueWatchingItem.NextUp -> {
                val info = item.info
                if (info.contentId in resumable) return@mapNotNull null
                val settings = profile.settings(info.contentId, info.contentType)
                if (!settings.enabled) item else {
                    val catalogue = videos(info.contentId, info.contentType) ?: return@mapNotNull null
                    val selected = shuffle.select(
                        profile.profileId, info.contentId, catalogue, settings.includeWatched,
                        watched[info.contentId].orEmpty(), surface = ShuffleSurface.HOME, visit = visit
                    ) ?: return@mapNotNull null
                    item.copy(shufflePlayback = true, info = info.copy(
                        videoId = selected.id, season = selected.season!!, episode = selected.episode!!,
                        episodeTitle = selected.title, episodeDescription = selected.overview,
                        thumbnail = selected.thumbnail, released = selected.released,
                        hasAired = true, airDateLabel = null, releaseTimestamp = null,
                        isReleaseAlert = false, isNewSeasonRelease = false
                    ))
                }
            }
        }
    }
    val unique = projected.distinctBy { it.shuffleFocusKey ?: continueWatchingItemKey(it) }
    val (current, upcoming) = splitUpcomingItems(unique, sortMode)
    return state.copy(continueWatchingItems = current, upcomingItems = upcoming)
}
