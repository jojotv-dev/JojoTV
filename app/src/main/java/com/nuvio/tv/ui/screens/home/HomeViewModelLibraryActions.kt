package com.nuvio.tv.ui.screens.home

import android.net.Uri
import android.util.Log
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.core.tracking.TrackingMembershipRemovalConfirmation
import com.nuvio.tv.core.tracking.mergeTrackingMembershipWithTabs
import com.nuvio.tv.core.tracking.toggleTrackingMembershipSelection
import com.nuvio.tv.data.repository.parseContentIds
import com.nuvio.tv.domain.model.CatalogRow
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.LibraryEntry
import com.nuvio.tv.domain.model.LibraryEntryInput
import com.nuvio.tv.domain.model.LibrarySourceMode
import com.nuvio.tv.domain.model.ListMembershipChanges
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.PosterShape
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.reshaped.livetv.LiveTvRepository
import com.nuvio.tv.reshaped.livetv.LiveTvChannel
import com.nuvio.tv.reshaped.livetv.LiveTvVodCache
import com.nuvio.tv.reshaped.livetv.LiveTvVodItem
import com.nuvio.tv.reshaped.livetv.normalizeLiveTvVodTitleForTmdb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
internal fun HomeViewModel.observeLibraryState() {
    viewModelScope.launch {
        profileManager.activeProfileId
            .collectLatest { profileId ->
                LiveTvRepository.ensureLoaded(appContext, profileId)
                var lastReadyVodFavorites = emptyList<LiveTvVodItem>()
                val iptvFavorites = LiveTvRepository.uiState
                    .map { state ->
                        Triple(state.sources, state.favoriteVodKeys, state.sourceStorageReady)
                    }
                    .distinctUntilChanged()
                    .transformLatest { (sources, favoriteKeys, storageReady) ->
                        if (!storageReady) {
                            // Keep the current Home row while the same profile's IPTV state is
                            // being reloaded. The new snapshot replaces it as soon as storage is ready.
                            emit(lastReadyVodFavorites)
                            return@transformLatest
                        }
                        val favorites = LiveTvVodCache.readFavorites(appContext, sources, favoriteKeys)
                        lastReadyVodFavorites = favorites
                        emit(favorites)
                    }

                val favoriteLiveChannels = LiveTvRepository.uiState
                    .map { state -> state.channels.filter { it.streamUrl in state.favoriteUrls } }
                    .distinctUntilChanged()

                combine(libraryRepository.libraryItems, iptvFavorites, favoriteLiveChannels) { entries, vodFavorites, liveChannels ->
                    buildFavoriteHomeCatalogRows(entries, vodFavorites, liveChannels)
                }
                    .distinctUntilChanged()
                    .collectLatest { rows ->
                        if (favoriteHomeCatalogRows.value != rows) {
                            favoriteHomeCatalogRows.value = rows
                            scheduleUpdateCatalogRows()
                            prefetchFavoriteArtworkPipeline(rows.flatMap { it.items })
                        }
                    }
            }
    }

    viewModelScope.launch {
        libraryRepository.sourceMode
            .distinctUntilChanged()
            .collectLatest { sourceMode ->
                if (sourceMode == LibrarySourceMode.LOCAL) {
                    activePosterListPickerInput = null
                    pendingPosterListPickerChanges = null
                }
                _uiState.update { state ->
                    val resetPickerState = sourceMode == LibrarySourceMode.LOCAL
                    val updatedState = state.copy(
                        librarySourceMode = sourceMode,
                        showPosterListPicker = if (resetPickerState) false else state.showPosterListPicker,
                        posterListPickerPending = if (resetPickerState) false else state.posterListPickerPending,
                        posterListPickerError = if (resetPickerState) null else state.posterListPickerError,
                        posterListPickerTitle = if (resetPickerState) null else state.posterListPickerTitle,
                        posterListPickerContentType = if (resetPickerState) null else state.posterListPickerContentType,
                        posterListPickerRemovalConfirmations = if (resetPickerState) {
                            emptyList()
                        } else {
                            state.posterListPickerRemovalConfirmations
                        },
                        posterListPickerMembership = if (resetPickerState) {
                            emptyMap()
                        } else {
                            state.posterListPickerMembership
                        }
                    )
                    if (updatedState == state) state else updatedState
                }
            }
    }

    viewModelScope.launch {
        libraryRepository.membershipListTabs
            .distinctUntilChanged()
            .collectLatest { tabs ->
                _uiState.update { state ->
                    val filteredMembership = mergeTrackingMembershipWithTabs(
                        tabs = tabs,
                        membership = state.posterListPickerMembership
                    )
                    if (
                        state.libraryListTabs == tabs &&
                        state.posterListPickerMembership == filteredMembership
                    ) {
                        state
                    } else {
                        state.copy(
                            libraryListTabs = tabs,
                            posterListPickerMembership = filteredMembership
                        )
                    }
                }
            }
    }
}

private fun HomeViewModel.buildFavoriteHomeCatalogRows(
    entries: List<LibraryEntry>,
    vodFavorites: List<LiveTvVodItem>,
    liveChannelFavorites: List<LiveTvChannel>
): List<CatalogRow> {
    val preferOfficialIptvArtwork = currentTmdbSettings.enabled && currentTmdbSettings.useArtwork
    val movieEntries = entries.filter { it.type.equals("movie", ignoreCase = true) }
    val seriesEntries = entries.filter {
        it.type.equals("series", ignoreCase = true) ||
            it.type.equals("tv", ignoreCase = true) ||
            it.type.equals("anime", ignoreCase = true)
    }
    val moviePreviews = (movieEntries.map { it.toMetaPreview().withoutSourceArtworkWhenTmdbPreferred(preferOfficialIptvArtwork) } +
        vodFavorites.filter { it.type.equals("movie", ignoreCase = true) }
            .map { it.toHomeFavoritePreview(showProviderPoster = !preferOfficialIptvArtwork) })
        .map { mergeCachedTmdbFavoritePreview(it) }
    val seriesPreviews = (seriesEntries.map { it.toMetaPreview().withoutSourceArtworkWhenTmdbPreferred(preferOfficialIptvArtwork) } +
        vodFavorites.filter { it.type.equals("series", ignoreCase = true) }
            .map { it.toHomeFavoritePreview(showProviderPoster = !preferOfficialIptvArtwork) })
        .map { mergeCachedTmdbFavoritePreview(it) }

    return buildList {
        favoriteHomeCatalogRow(
            catalogId = HOME_FAVORITE_MOVIES_CATALOG_ID,
            title = appContext.getString(com.nuvio.tv.R.string.home_favorite_movies_title),
            type = ContentType.MOVIE,
            rawType = "movie",
            previews = moviePreviews
        )?.let { add(it) }
        favoriteHomeCatalogRow(
            catalogId = HOME_FAVORITE_SERIES_CATALOG_ID,
            title = appContext.getString(com.nuvio.tv.R.string.home_favorite_series_title),
            type = ContentType.SERIES,
            rawType = "series",
            previews = seriesPreviews
        )?.let { add(it) }
        favoriteHomeCatalogRow(
            catalogId = HOME_FAVORITE_TV_CATALOG_ID,
            title = appContext.getString(com.nuvio.tv.R.string.live_tv_title),
            type = ContentType.CHANNEL,
            rawType = "channel",
            previews = liveChannelFavorites.map { it.toHomeLiveChannelPreview() }
        )?.let { add(it) }
    }
}

private fun LiveTvChannel.toHomeLiveChannelPreview(): MetaPreview = MetaPreview(
    id = homeIptvLiveChannelItemId(sourceId, id),
    type = ContentType.CHANNEL,
    rawType = "channel",
    name = name,
    poster = logoUrl,
    posterShape = PosterShape.LANDSCAPE,
    background = null,
    logo = null,
    description = null,
    releaseInfo = group.takeIf(String::isNotBlank),
    imdbRating = null,
    genres = emptyList(),
    landscapePoster = logoUrl,
    rawPosterUrl = logoUrl
)

private fun MetaPreview.withoutSourceArtworkWhenTmdbPreferred(preferOfficialArtwork: Boolean): MetaPreview =
    if (!preferOfficialArtwork) this else copy(
        poster = null,
        background = null,
        landscapePoster = null,
        rawPosterUrl = null,
    )

private fun HomeViewModel.mergeCachedTmdbFavoritePreview(item: MetaPreview): MetaPreview {
    val cached = if (item.id in prefetchedTmdbIds) _enrichedPreviews.value[item.id] else null
    val settings = currentTmdbSettings
    var merged = item
    if (cached != null) {
        val isSeries = item.type == ContentType.SERIES || item.type == ContentType.TV ||
            item.apiType.equals("series", ignoreCase = true) || item.apiType.equals("tv", ignoreCase = true)
        val cachedHasTmdbSeriesDetails = settings.useDetails && isSeries && cached.hasTmdbSeriesDetails
        val hasTmdbSeriesDetails = isSeries && (cachedHasTmdbSeriesDetails || merged.hasTmdbSeriesDetails)
        merged = merged.copy(
            poster = if (settings.useArtwork) cached.poster ?: merged.poster else merged.poster,
            background = if (settings.useArtwork) cached.background ?: merged.background else merged.background,
            landscapePoster = if (settings.useArtwork) cached.landscapePoster ?: merged.landscapePoster else merged.landscapePoster,
            logo = if (settings.useArtwork) cached.logo ?: merged.logo else merged.logo,
            description = if (settings.useBasicInfo) cached.description ?: merged.description else merged.description,
            genres = if (settings.useBasicInfo && cached.genres.isNotEmpty()) cached.genres else merged.genres,
            releaseInfo = if (settings.useBasicInfo) cached.releaseInfo ?: merged.releaseInfo else merged.releaseInfo,
            runtime = if (settings.useDetails) cached.runtime ?: merged.runtime else merged.runtime,
            status = if (isSeries) {
                when {
                    cachedHasTmdbSeriesDetails -> cached.status
                    merged.hasTmdbSeriesDetails -> merged.status
                    else -> null
                }
            } else if (settings.useDetails) cached.status ?: merged.status else merged.status,
            currentSeasonComplete = if (settings.useDetails) {
                cached.currentSeasonComplete ?: merged.currentSeasonComplete
            } else {
                merged.currentSeasonComplete
            },
            seasonCount = if (isSeries) {
                when {
                    cachedHasTmdbSeriesDetails -> cached.seasonCount
                    merged.hasTmdbSeriesDetails -> merged.seasonCount
                    else -> null
                }
            } else if (settings.useDetails) cached.seasonCount ?: merged.seasonCount else merged.seasonCount,
            hasTmdbSeriesDetails = hasTmdbSeriesDetails
        )
    }
    val portraitOverride = homeFavoriteArtworkOverride(item.id, portrait = true)
    val landscapeOverride = homeFavoriteArtworkOverride(item.id, portrait = false)
    return merged.copy(
        poster = portraitOverride ?: merged.poster,
        posterShape = if (portraitOverride != null) PosterShape.POSTER else merged.posterShape,
        background = landscapeOverride ?: merged.background,
        landscapePoster = landscapeOverride ?: merged.landscapePoster,
    )
}

internal const val HOME_FAVORITE_ARTWORK_PREFERENCES = "home_favorite_artwork_overrides"

private fun HomeViewModel.favoriteArtworkPreferenceKey(itemId: String, portrait: Boolean, baseline: Boolean = false): String {
    return homeFavoriteArtworkPreferenceKey(profileManager.activeProfileId.value, itemId, portrait, baseline)
}

internal fun HomeViewModel.homeFavoriteArtworkOverride(itemId: String, portrait: Boolean): String? =
    readHomeFavoriteArtworkOverride(appContext, profileManager.activeProfileId.value, itemId, portrait)

internal fun homeFavoriteArtworkPreferenceKey(
    profileId: Int,
    itemId: String,
    portrait: Boolean,
    baseline: Boolean = false,
): String {
    val orientation = if (portrait) "portrait" else "landscape"
    val kind = if (baseline) "base" else "selected"
    return "$profileId:${Uri.encode(itemId)}:$orientation:$kind"
}

internal fun readHomeFavoriteArtworkOverride(
    context: Context,
    profileId: Int,
    itemId: String,
    portrait: Boolean,
): String? = context.getSharedPreferences(HOME_FAVORITE_ARTWORK_PREFERENCES, Context.MODE_PRIVATE)
    .getString(homeFavoriteArtworkPreferenceKey(profileId, itemId, portrait), null)
    ?.takeIf(String::isNotBlank)

internal fun MetaPreview.homeFavoriteTmdbLookupTitle(): String = normalizeLiveTvVodTitleForTmdb(name)

internal suspend fun HomeViewModel.findHomeFavoriteArtworkCandidates(
    item: MetaPreview,
    portrait: Boolean,
): List<String> {
    val settings = tmdbSettingsDataStore.settings.first()
    if (!settings.enabled || !settings.useArtwork) return emptyList()
    return tmdbService.artworkCandidatesForExactTitle(
        title = item.homeFavoriteTmdbLookupTitle(),
        mediaType = item.apiType,
        portrait = portrait,
        language = settings.language,
        year = item.releaseInfo?.take(4)?.toIntOrNull(),
    )
}

internal suspend fun HomeViewModel.setHomeFavoriteArtwork(
    item: MetaPreview,
    portrait: Boolean,
    imageUrl: String?,
) {
    val selectedKey = favoriteArtworkPreferenceKey(item.id, portrait)
    val baselineKey = favoriteArtworkPreferenceKey(item.id, portrait, baseline = true)
    val baselineShapeKey = "$baselineKey:shape"
    val preferences = appContext.getSharedPreferences(HOME_FAVORITE_ARTWORK_PREFERENCES, Context.MODE_PRIVATE)
    val (baselineValue, baselineShapeValue) = withContext(Dispatchers.IO) {
        val editor = preferences.edit()
        val original = if (imageUrl.isNullOrBlank()) {
            preferences.getString(baselineKey, null)
        } else {
            if (!preferences.contains(baselineKey)) {
                val original = if (portrait) item.poster ?: item.rawPosterUrl else {
                    item.landscapePoster ?: item.background ?: item.backdropUrl
                }
                editor.putString(baselineKey, original.orEmpty())
                if (portrait) editor.putString(baselineShapeKey, item.posterShape.name)
            }
            null
        }
        val originalShape = if (portrait && imageUrl.isNullOrBlank()) {
            preferences.getString(baselineShapeKey, null)
                ?.let { runCatching { PosterShape.valueOf(it) }.getOrNull() }
        } else null
        if (imageUrl.isNullOrBlank()) editor.remove(selectedKey).remove(baselineKey)
        else editor.putString(selectedKey, imageUrl)
        if (imageUrl.isNullOrBlank() && portrait) editor.remove(baselineShapeKey)
        editor.apply()
        original to originalShape
    }
    val restored = baselineValue?.takeIf(String::isNotBlank)
        ?: if (portrait) _enrichedPreviews.value[item.id]?.poster ?: item.rawPosterUrl
        else _enrichedPreviews.value[item.id]?.let { it.landscapePoster ?: it.background }
    favoriteHomeCatalogRows.update { rows ->
        rows.map { row ->
            val index = row.items.indexOfFirst { it.id == item.id }
            if (index < 0) return@map row
            val current = row.items[index]
            val artwork = if (imageUrl.isNullOrBlank()) restored else imageUrl
            val updated = if (portrait) {
                current.copy(
                    poster = artwork,
                    posterShape = if (imageUrl.isNullOrBlank()) baselineShapeValue ?: item.posterShape else PosterShape.POSTER,
                )
            } else {
                current.copy(background = artwork, landscapePoster = artwork)
            }
            if (updated == current) row else row.copy(items = row.items.toMutableList().apply { set(index, updated) })
        }
    }
    scheduleUpdateCatalogRows()
}

private fun HomeViewModel.favoriteHomeCatalogRow(
    catalogId: String,
    title: String,
    type: ContentType,
    rawType: String,
    previews: List<MetaPreview>
): CatalogRow? {
    val uniquePreviews = previews.distinctBy { "${it.apiType}:${it.id}" }
    if (uniquePreviews.isEmpty()) return null

    return CatalogRow(
        addonId = HOME_FAVORITES_ADDON_ID,
        addonName = "",
        addonBaseUrl = "",
        catalogId = catalogId,
        catalogName = title,
        type = type,
        rawType = rawType,
        items = uniquePreviews,
        hasMore = false,
        supportsSkip = false
    )
}

private fun LiveTvVodItem.toHomeFavoritePreview(showProviderPoster: Boolean): MetaPreview = MetaPreview(
    id = HOME_IPTV_FAVORITE_ITEM_ID_PREFIX + Uri.encode(favoriteKey),
    type = if (type.equals("series", ignoreCase = true)) ContentType.SERIES else ContentType.MOVIE,
    rawType = type,
    name = title,
    poster = posterUrl.takeIf { showProviderPoster && it.isNotBlank() },
    posterShape = PosterShape.POSTER,
    background = null,
    logo = null,
    description = plot.takeIf(String::isNotBlank),
    releaseInfo = year.takeIf(String::isNotBlank),
    imdbRating = null,
    genres = emptyList()
)

fun HomeViewModel.refreshPosterLibraryStatus(item: MetaPreview) {
    val statusKey = homeItemStatusKey(item.id, item.apiType)
    viewModelScope.launch {
        runCatching {
            val isInLibrary = libraryRepository.isInLibrary(item.id, item.apiType).first()
            _uiState.update { state ->
                if (state.posterLibraryMembership[statusKey] == isInLibrary) state
                else state.copy(
                    posterLibraryMembership = state.posterLibraryMembership + (statusKey to isInLibrary)
                )
            }
        }
    }
}

fun HomeViewModel.togglePosterLibrary(item: MetaPreview, addonBaseUrl: String?) {
    val statusKey = homeItemStatusKey(item.id, item.apiType)
    if (statusKey in _uiState.value.posterLibraryPending) return

    _uiState.update { state ->
        state.copy(posterLibraryPending = state.posterLibraryPending + statusKey)
    }

    viewModelScope.launch {
        runCatching {
            libraryRepository.toggleDefault(item.toLibraryEntryInput(addonBaseUrl))
        }.onFailure { error ->
            Log.w(HomeViewModel.TAG, "Failed to toggle poster library for ${item.id}: ${error.message}")
        }
        runCatching {
            val isNowInLibrary = libraryRepository.isInLibrary(item.id, item.apiType).first()
            _uiState.update { state ->
                state.copy(
                    posterLibraryMembership = state.posterLibraryMembership + (statusKey to isNowInLibrary)
                )
            }
        }
        _uiState.update { state ->
            state.copy(posterLibraryPending = state.posterLibraryPending - statusKey)
        }
    }
}

fun HomeViewModel.openPosterListPicker(item: MetaPreview, addonBaseUrl: String?) {
    if (_uiState.value.librarySourceMode == LibrarySourceMode.LOCAL) {
        togglePosterLibrary(item, addonBaseUrl)
        return
    }
    val input = item.toLibraryEntryInput(addonBaseUrl)
    activePosterListPickerInput = input

    _uiState.update { state ->
        state.copy(
            showPosterListPicker = true,
            posterListPickerTitle = item.name,
            posterListPickerContentType = item.apiType,
            posterListPickerPending = true,
            posterListPickerError = null,
            posterListPickerRemovalConfirmations = emptyList(),
            posterListPickerMembership = mergeTrackingMembershipWithTabs(
                tabs = state.libraryListTabs,
                membership = emptyMap()
            )
        )
    }

    viewModelScope.launch {
        runCatching {
            libraryRepository.getMembershipSnapshot(input)
        }.onSuccess { snapshot ->
            _uiState.update { state ->
                state.copy(
                    showPosterListPicker = true,
                    posterListPickerPending = false,
                    posterListPickerError = null,
                    posterListPickerMembership = mergeTrackingMembershipWithTabs(
                        tabs = state.libraryListTabs,
                        membership = snapshot.listMembership
                    )
                )
            }
        }.onFailure { error ->
            Log.w(HomeViewModel.TAG, "Failed to load poster list picker for ${item.id}: ${error.message}")
            _uiState.update { state ->
                state.copy(
                    showPosterListPicker = true,
                    posterListPickerPending = false,
                    posterListPickerError = error.message ?: appContext.getString(com.nuvio.tv.R.string.home_poster_lists_error_load_failed)
                )
            }
        }
    }
}

fun HomeViewModel.togglePosterListPickerMembership(listKey: String) {
    _uiState.update { state ->
        val updatedMembership = toggleTrackingMembershipSelection(
            tabs = state.libraryListTabs,
            membership = state.posterListPickerMembership,
            listKey = listKey,
            contentType = state.posterListPickerContentType
        ) ?: return@update state
        state.copy(
            posterListPickerMembership = updatedMembership,
            posterListPickerError = null
        )
    }
}

fun HomeViewModel.savePosterListPickerMembership() {
    if (_uiState.value.posterListPickerPending) return
    if (_uiState.value.librarySourceMode == LibrarySourceMode.LOCAL) return
    val input = activePosterListPickerInput ?: return
    val changes = ListMembershipChanges(
        desiredMembership = _uiState.value.posterListPickerMembership
    )

    viewModelScope.launch {
        _uiState.update { state ->
            state.copy(
                posterListPickerPending = true,
                posterListPickerError = null
            )
        }

        runCatching {
            libraryRepository.applyMembershipChanges(
                item = input,
                changes = changes
            )
        }.onSuccess { result ->
            if (result.requiresRemovalConfirmation) {
                pendingPosterListPickerChanges = changes
                _uiState.update { state ->
                    state.copy(
                        posterListPickerPending = false,
                        posterListPickerRemovalConfirmations = result.requiredRemovalConfirmations
                    )
                }
            } else {
                closePosterListPickerAfterSave(changes)
            }
        }.onFailure { error ->
            Log.w(HomeViewModel.TAG, "Failed to save poster list picker: ${error.message}")
            _uiState.update { state ->
                state.copy(
                    posterListPickerPending = false,
                    posterListPickerError = error.message ?: appContext.getString(com.nuvio.tv.R.string.home_poster_lists_error_update_failed)
                )
            }
        }
    }
}

fun HomeViewModel.confirmPosterListPickerRemoval() {
    if (_uiState.value.posterListPickerPending) return
    val input = activePosterListPickerInput ?: return
    val changes = pendingPosterListPickerChanges ?: return
    val confirmations = _uiState.value.posterListPickerRemovalConfirmations
    if (confirmations.isEmpty()) return

    viewModelScope.launch {
        _uiState.update { it.copy(posterListPickerPending = true) }
        runCatching {
            libraryRepository.applyMembershipChanges(
                item = input,
                changes = changes,
                confirmedRemovalProviders = confirmations.mapTo(
                    linkedSetOf(),
                    TrackingMembershipRemovalConfirmation::providerId
                )
            )
        }.onSuccess { result ->
            if (result.requiresRemovalConfirmation) {
                _uiState.update {
                    it.copy(
                        posterListPickerPending = false,
                        posterListPickerRemovalConfirmations = result.requiredRemovalConfirmations
                    )
                }
            } else {
                closePosterListPickerAfterSave(changes)
            }
        }.onFailure { error ->
            Log.w(HomeViewModel.TAG, "Failed to confirm poster list removal: ${error.message}")
            _uiState.update {
                it.copy(
                    posterListPickerPending = false,
                    posterListPickerRemovalConfirmations = emptyList(),
                    posterListPickerError = error.message
                        ?: appContext.getString(com.nuvio.tv.R.string.home_poster_lists_error_update_failed)
                )
            }
        }
    }
}

fun HomeViewModel.cancelPosterListPickerRemoval() {
    pendingPosterListPickerChanges = null
    _uiState.update { it.copy(posterListPickerRemovalConfirmations = emptyList()) }
}

fun HomeViewModel.dismissPosterListPicker() {
    activePosterListPickerInput = null
    pendingPosterListPickerChanges = null
    _uiState.update { state ->
        state.copy(
            showPosterListPicker = false,
            posterListPickerPending = false,
            posterListPickerError = null,
            posterListPickerTitle = null,
            posterListPickerContentType = null,
            posterListPickerRemovalConfirmations = emptyList()
        )
    }
}

private fun HomeViewModel.closePosterListPickerAfterSave(changes: ListMembershipChanges) {
    val savedInput = activePosterListPickerInput
    val anyListSelected = changes.desiredMembership.values.any { it }
    activePosterListPickerInput = null
    pendingPosterListPickerChanges = null
    _uiState.update { state ->
        val updatedMembership = savedInput?.let { input ->
            state.posterLibraryMembership + (
                homeItemStatusKey(input.itemId, input.itemType) to anyListSelected
            )
        } ?: state.posterLibraryMembership
        state.copy(
            showPosterListPicker = false,
            posterListPickerPending = false,
            posterListPickerError = null,
            posterListPickerTitle = null,
            posterListPickerContentType = null,
            posterListPickerRemovalConfirmations = emptyList(),
            posterLibraryMembership = updatedMembership
        )
    }
}

fun HomeViewModel.togglePosterMovieWatched(item: MetaPreview) {
    if (!item.apiType.equals("movie", ignoreCase = true)) return
    val statusKey = homeItemStatusKey(item.id, item.apiType)
    if (statusKey in _uiState.value.movieWatchedPending) return

    _uiState.update { state ->
        state.copy(movieWatchedPending = state.movieWatchedPending + statusKey)
    }

    viewModelScope.launch {
        val currentlyWatched = _uiState.value.movieWatchedStatus[statusKey] == true
        runCatching {
            if (currentlyWatched) {
                watchProgressRepository.removeFromHistory(item.id, videoId = item.imdbId)
            } else {
                watchProgressRepository.markAsCompleted(buildCompletedMovieProgress(item))
            }
        }.onFailure { error ->
            Log.w(HomeViewModel.TAG, "Failed to toggle poster watched status for ${item.id}: ${error.message}")
        }
        _uiState.update { state ->
            state.copy(movieWatchedPending = state.movieWatchedPending - statusKey)
        }
    }
}

private fun buildCompletedMovieProgress(item: MetaPreview): WatchProgress {
    return WatchProgress(
        contentId = item.id,
        contentType = item.apiType,
        name = item.name,
        poster = item.poster,
        backdrop = item.backdropUrl,
        logo = item.logo,
        videoId = item.id,
        season = null,
        episode = null,
        episodeTitle = null,
        position = 1L,
        duration = 1L,
        lastWatched = System.currentTimeMillis(),
        progressPercent = 100f
    )
}

fun HomeViewModel.togglePosterSeriesWatched(item: MetaPreview) {
    val isSeries = item.apiType.equals("series", ignoreCase = true) ||
        item.apiType.equals("tv", ignoreCase = true)
    if (!isSeries) return
    val statusKey = homeItemStatusKey(item.id, item.apiType)
    if (statusKey in _uiState.value.movieWatchedPending) return

    val currentlyWatched = _uiState.value.movieWatchedStatus[statusKey] == true

    // Optimistically update the UI immediately
    _uiState.update { state ->
        state.copy(
            movieWatchedPending = state.movieWatchedPending + statusKey,
            movieWatchedStatus = state.movieWatchedStatus + (statusKey to !currentlyWatched)
        )
    }

    viewModelScope.launch {
        runCatching {
            if (currentlyWatched) {
                unmarkSeriesWatched(item)
            } else {
                markSeriesWatched(item)
            }
        }.onFailure { error ->
            Log.w(HomeViewModel.TAG, "Failed to toggle series watched for ${item.id}: ${error.message}")
            // Revert optimistic update on failure
            _uiState.update { state ->
                state.copy(
                    movieWatchedStatus = state.movieWatchedStatus + (statusKey to currentlyWatched)
                )
            }
        }
        _uiState.update { state ->
            state.copy(movieWatchedPending = state.movieWatchedPending - statusKey)
        }
    }
}

private suspend fun HomeViewModel.markSeriesWatched(item: MetaPreview) {
    val episodes = fetchSeriesEpisodes(item)
    // Without episodes the only mark left is a show-level one, which trackers answer by marking
    // every episode watched. Fail instead so the optimistic badge is reverted.
    check(episodes.isNotEmpty()) { "No episodes found for ${item.id}" }

    val progressList = episodes.map { video ->
        WatchProgress(
            contentId = item.id,
            contentType = item.apiType,
            name = item.name,
            poster = item.poster,
            backdrop = item.backdropUrl,
            logo = item.logo,
            videoId = video.id,
            season = video.season,
            episode = video.episode,
            episodeTitle = video.title,
            position = 1L,
            duration = 1L,
            lastWatched = System.currentTimeMillis(),
            progressPercent = 100f
        )
    }
    watchProgressRepository.markAsCompletedBatch(progressList)
    fullyWatchedSeriesIds.updateWithValidation(
        fullyWatchedSeriesIds.fullyWatchedSeriesIds.value + item.id,
        setOf(item.id)
    )
}

private suspend fun HomeViewModel.unmarkSeriesWatched(item: MetaPreview) {
    val episodes = fetchSeriesEpisodes(item)
    if (episodes.isEmpty()) {
        watchProgressRepository.removeFromHistory(item.id, videoId = item.imdbId)
        return
    }

    watchProgressRepository.removeFromHistoryBatch(
        contentId = item.id,
        videoId = item.imdbId,
        episodes = episodes.map { Triple(it.season!!, it.episode!!, it.id) }
    )
    fullyWatchedSeriesIds.updateWithValidation(
        fullyWatchedSeriesIds.fullyWatchedSeriesIds.value - item.id,
        setOf(item.id)
    )
}

private suspend fun HomeViewModel.fetchSeriesEpisodes(item: MetaPreview): List<com.nuvio.tv.domain.model.Video> {
    val type = if (item.apiType.equals("tv", ignoreCase = true)) "series" else item.apiType
    var episodes: List<com.nuvio.tv.domain.model.Video> = emptyList()
    metaRepository.getMetaFromPrimaryAddon(type, item.id)
        .collect { networkResult ->
            if (networkResult is com.nuvio.tv.core.network.NetworkResult.Success) {
                episodes = networkResult.data.watchableEpisodes()
            }
        }
    return episodes
}

private fun MetaPreview.toLibraryEntryInput(addonBaseUrl: String?): LibraryEntryInput {
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
