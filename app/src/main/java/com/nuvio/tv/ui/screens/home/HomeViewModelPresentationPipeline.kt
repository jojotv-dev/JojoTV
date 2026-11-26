package com.nuvio.tv.ui.screens.home

import android.util.Log
import kotlinx.coroutines.CancellationException
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.LocaleCache
import com.nuvio.tv.core.build.AppFeaturePolicy
import com.nuvio.tv.core.network.NetworkResult
import com.nuvio.tv.core.poster.CustomPosterScreen
import com.nuvio.tv.core.poster.patternForScreen
import com.nuvio.tv.core.poster.withCustomPosterUrls
import com.nuvio.tv.core.tmdb.TmdbEnrichment
import com.nuvio.tv.data.freebox.FREEBOX_FOLDER_ID_PREFIX
import com.nuvio.tv.data.mediaserver.ServerItemRef
import com.nuvio.tv.domain.model.CatalogRow
import com.nuvio.tv.domain.model.FocusedPosterTrailerPlaybackTarget
import com.nuvio.tv.domain.model.HomeImdbRatingsVisibility
import com.nuvio.tv.domain.model.DetailImdbRatingsVisibility
import com.nuvio.tv.domain.model.HomeLayout
import com.nuvio.tv.domain.model.Meta
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.TmdbSettings
import com.nuvio.tv.domain.model.stableItemKeys
import com.nuvio.tv.domain.model.stableKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

private data class CoreLayoutPrefs(
    val layout: HomeLayout,
    val heroCatalogKeys: List<String>,
    val heroSectionEnabled: Boolean,
    val posterLabelsEnabled: Boolean,
    val catalogAddonNameEnabled: Boolean,
    val catalogTypeSuffixEnabled: Boolean,
    val classicFocusGradientEnabled: Boolean,
    val hideUnreleasedContent: Boolean,
    val showFullReleaseDate: Boolean
)

private data class FocusedBackdropPrefs(
    val expandEnabled: Boolean,
    val expandDelaySeconds: Int,
    val trailerEnabled: Boolean,
    val trailerMuted: Boolean,
    val trailerPlaybackTarget: FocusedPosterTrailerPlaybackTarget
)

private data class LayoutUiPrefs(
    val layout: HomeLayout,
    val heroCatalogKeys: List<String>,
    val heroSectionEnabled: Boolean,
    val posterLabelsEnabled: Boolean,
    val catalogAddonNameEnabled: Boolean,
    val catalogTypeSuffixEnabled: Boolean,
    val classicFocusGradientEnabled: Boolean,
    val hideUnreleasedContent: Boolean,
    val showFullReleaseDate: Boolean,
    val modernLandscapePostersEnabled: Boolean,
    val alwaysShowLandscapeClearlogo: Boolean = false,
    val heroDescriptionAutoScrollEnabled: Boolean = false,
    val modernHeroFullScreenBackdropEnabled: Boolean,
    val modernContinueWatchingHeroEnabled: Boolean,
    val homeImdbRatingsVisibility: HomeImdbRatingsVisibility,
    val detailImdbRatingsVisibility: DetailImdbRatingsVisibility = DetailImdbRatingsVisibility.SHOW_ALL,
    val focusedBackdropExpandEnabled: Boolean,
    val focusedBackdropExpandDelaySeconds: Int,
    val focusedBackdropTrailerEnabled: Boolean,
    val focusedBackdropTrailerMuted: Boolean,
    val focusedBackdropTrailerPlaybackTarget: FocusedPosterTrailerPlaybackTarget,
    val posterCardWidthDp: Int,
    val installedAppTileWidthDp: Int,
    val posterCardHeightDp: Int,
    val posterCardCornerRadiusDp: Int
)

private data class PosterLayoutPrefs(
    val widthDp: Int,
    val appTileWidthDp: Int,
    val heightDp: Int,
    val cornerRadiusDp: Int
)

private data class ModernLayoutPrefs(
    val landscapePosters: Boolean,
    val fullScreenBackdrop: Boolean,
    val continueWatchingHeroEnabled: Boolean = false,
    val homeImdbRatingsVisibility: HomeImdbRatingsVisibility,
    val alwaysShowLandscapeClearlogo: Boolean,
    val detailImdbRatingsVisibility: DetailImdbRatingsVisibility = DetailImdbRatingsVisibility.SHOW_ALL,
)

@OptIn(FlowPreview::class)
internal fun HomeViewModel.observeLayoutPreferencesPipeline() {
    val coreLayoutPrefsFlow = combine(
        combine(
            layoutPreferenceDataStore.selectedLayout,
            layoutPreferenceDataStore.heroCatalogSelections,
            layoutPreferenceDataStore.heroSectionEnabled,
            layoutPreferenceDataStore.posterLabelsEnabled,
            layoutPreferenceDataStore.catalogAddonNameEnabled
        ) { layout, heroCatalogKeys, heroSectionEnabled, posterLabelsEnabled, catalogAddonNameEnabled ->
            CoreLayoutPrefs(
                layout = layout,
                heroCatalogKeys = heroCatalogKeys,
                heroSectionEnabled = heroSectionEnabled,
                posterLabelsEnabled = posterLabelsEnabled,
                catalogAddonNameEnabled = catalogAddonNameEnabled,
                catalogTypeSuffixEnabled = true,
                classicFocusGradientEnabled = false,
                hideUnreleasedContent = false,
                showFullReleaseDate = true
            )
        },
        layoutPreferenceDataStore.catalogTypeSuffixEnabled,
        layoutPreferenceDataStore.hideUnreleasedContent,
        layoutPreferenceDataStore.showFullReleaseDate,
        layoutPreferenceDataStore.classicFocusGradientEnabled
    ) { corePrefs, catalogTypeSuffixEnabled, hideUnreleasedContent, showFullReleaseDate, classicFocusGradientEnabled ->
        corePrefs.copy(
            catalogTypeSuffixEnabled = catalogTypeSuffixEnabled,
            classicFocusGradientEnabled = classicFocusGradientEnabled,
            hideUnreleasedContent = hideUnreleasedContent,
            showFullReleaseDate = showFullReleaseDate
        )
    }

    val focusedBackdropPrefsFlow = combine(
        layoutPreferenceDataStore.focusedPosterBackdropExpandEnabled,
        layoutPreferenceDataStore.focusedPosterBackdropExpandDelaySeconds,
        layoutPreferenceDataStore.focusedPosterBackdropTrailerEnabled,
        layoutPreferenceDataStore.focusedPosterBackdropTrailerMuted,
        layoutPreferenceDataStore.focusedPosterBackdropTrailerPlaybackTarget
    ) { expandEnabled, expandDelaySeconds, trailerEnabled, trailerMuted, trailerPlaybackTarget ->
        FocusedBackdropPrefs(
            expandEnabled = expandEnabled,
            expandDelaySeconds = expandDelaySeconds,
            trailerEnabled = trailerEnabled,
            trailerMuted = trailerMuted,
            trailerPlaybackTarget = trailerPlaybackTarget
        )
    }

    val modernLayoutPrefsFlow = combine(
        combine(
            layoutPreferenceDataStore.modernLandscapePostersEnabled,
            layoutPreferenceDataStore.modernHeroFullScreenBackdropEnabled,
            layoutPreferenceDataStore.homeImdbRatingsVisibility,
            layoutPreferenceDataStore.alwaysShowLandscapeClearlogo,
            layoutPreferenceDataStore.detailImdbRatingsVisibility
        ) { landscapePosters, fullScreenBackdrop, homeImdbRatingsVisibility, alwaysShowLandscapeClearlogo, detailImdbRatingsVisibility ->
            ModernLayoutPrefs(
                landscapePosters = landscapePosters,
                fullScreenBackdrop = fullScreenBackdrop,
                homeImdbRatingsVisibility = homeImdbRatingsVisibility,
                alwaysShowLandscapeClearlogo = alwaysShowLandscapeClearlogo,
                detailImdbRatingsVisibility = detailImdbRatingsVisibility,
            )
        },
        layoutPreferenceDataStore.modernContinueWatchingHeroEnabled
    ) { prefs, continueWatchingHeroEnabled ->
        prefs.copy(continueWatchingHeroEnabled = continueWatchingHeroEnabled)
    }

    val posterLayoutPrefsFlow = combine(
        layoutPreferenceDataStore.posterCardWidthDp,
        layoutPreferenceDataStore.installedAppTileWidthDp,
        layoutPreferenceDataStore.posterCardHeightDp,
        layoutPreferenceDataStore.posterCardCornerRadiusDp
    ) { posterCardWidthDp, installedAppTileWidthDp, posterCardHeightDp, posterCardCornerRadiusDp ->
        PosterLayoutPrefs(posterCardWidthDp, installedAppTileWidthDp, posterCardHeightDp, posterCardCornerRadiusDp)
    }

    val baseLayoutUiPrefsFlow = combine(
        coreLayoutPrefsFlow,
        focusedBackdropPrefsFlow,
        posterLayoutPrefsFlow
    ) { corePrefs, focusedBackdropPrefs, posterPrefs ->
        LayoutUiPrefs(
            layout = corePrefs.layout,
            heroCatalogKeys = corePrefs.heroCatalogKeys,
            heroSectionEnabled = corePrefs.heroSectionEnabled,
            posterLabelsEnabled = corePrefs.posterLabelsEnabled,
            catalogAddonNameEnabled = corePrefs.catalogAddonNameEnabled,
            catalogTypeSuffixEnabled = corePrefs.catalogTypeSuffixEnabled,
            classicFocusGradientEnabled = corePrefs.classicFocusGradientEnabled,
            hideUnreleasedContent = corePrefs.hideUnreleasedContent,
            showFullReleaseDate = corePrefs.showFullReleaseDate,
            modernLandscapePostersEnabled = false,
            modernHeroFullScreenBackdropEnabled = false,
            modernContinueWatchingHeroEnabled = false,
            homeImdbRatingsVisibility = HomeImdbRatingsVisibility.SHOW_ALL,
            focusedBackdropExpandEnabled = focusedBackdropPrefs.expandEnabled,
            focusedBackdropExpandDelaySeconds = focusedBackdropPrefs.expandDelaySeconds,
            focusedBackdropTrailerEnabled = focusedBackdropPrefs.trailerEnabled &&
                AppFeaturePolicy.inAppTrailerPlaybackEnabled,
            focusedBackdropTrailerMuted = focusedBackdropPrefs.trailerMuted,
            focusedBackdropTrailerPlaybackTarget = focusedBackdropPrefs.trailerPlaybackTarget,
            posterCardWidthDp = posterPrefs.widthDp,
            installedAppTileWidthDp = posterPrefs.appTileWidthDp,
            posterCardHeightDp = posterPrefs.heightDp,
            posterCardCornerRadiusDp = posterPrefs.cornerRadiusDp
        )
    }

    viewModelScope.launch {
        combine(
            baseLayoutUiPrefsFlow,
            modernLayoutPrefsFlow,
            layoutPreferenceDataStore.heroDescriptionAutoScrollEnabled
        ) { basePrefs, modernPrefs, heroDescriptionAutoScrollEnabled ->
            basePrefs.copy(
                modernLandscapePostersEnabled = modernPrefs.landscapePosters,
                modernHeroFullScreenBackdropEnabled = modernPrefs.fullScreenBackdrop,
                modernContinueWatchingHeroEnabled = modernPrefs.continueWatchingHeroEnabled,
                homeImdbRatingsVisibility = modernPrefs.homeImdbRatingsVisibility,
                alwaysShowLandscapeClearlogo = modernPrefs.alwaysShowLandscapeClearlogo,
                detailImdbRatingsVisibility = modernPrefs.detailImdbRatingsVisibility,
                heroDescriptionAutoScrollEnabled = heroDescriptionAutoScrollEnabled,
            )
        }
            .distinctUntilChanged()
            .debounce(300)
            .collectLatest { prefs ->
                val effectivePosterLabelsEnabled = if (prefs.layout == HomeLayout.MODERN) {
                    false
                } else {
                    prefs.posterLabelsEnabled
                }
                val previousState = _uiState.value
                val heroKeysChanged = currentHeroCatalogKeys != prefs.heroCatalogKeys
                val shouldRefreshCatalogPresentation =
                    heroKeysChanged ||
                        previousState.heroSectionEnabled != prefs.heroSectionEnabled ||
                        previousState.homeLayout != prefs.layout ||
                        previousState.hideUnreleasedContent != prefs.hideUnreleasedContent ||
                        previousState.posterCardWidthDp != prefs.posterCardWidthDp ||
                        previousState.homeImdbRatingsVisibility != prefs.homeImdbRatingsVisibility
                currentHeroCatalogKeys = prefs.heroCatalogKeys
                // Reset focus state when layout changes so the outgoing
                // layout's onDispose doesn't poison the incoming layout
                // (e.g., Modern dispose saves hasSavedFocus=true right
                // before Classic composes, preventing hero initial focus).
                if (previousState.layoutPreferencesReady && previousState.homeLayout != prefs.layout) {
                    // Suppress the outgoing layout's onDispose from saving
                    // stale focus state before the incoming layout composes.
                    suppressFocusSave = true
                    clearFocusState()
                }
                _uiState.update {
                    it.copy(
                        layoutPreferencesReady = true,
                        homeLayout = prefs.layout,
                        heroCatalogKeys = prefs.heroCatalogKeys,
                        heroSectionEnabled = prefs.heroSectionEnabled,
                        posterLabelsEnabled = effectivePosterLabelsEnabled,
                        catalogAddonNameEnabled = prefs.catalogAddonNameEnabled,
                        catalogTypeSuffixEnabled = prefs.catalogTypeSuffixEnabled,
                        classicFocusGradientEnabled = prefs.classicFocusGradientEnabled && prefs.layout == HomeLayout.CLASSIC,
                        hideUnreleasedContent = prefs.hideUnreleasedContent,
                        showFullReleaseDate = prefs.showFullReleaseDate,
                        modernLandscapePostersEnabled = prefs.modernLandscapePostersEnabled,
                        alwaysShowLandscapeClearlogo = prefs.alwaysShowLandscapeClearlogo,
                        heroDescriptionAutoScrollEnabled = prefs.heroDescriptionAutoScrollEnabled,
                        modernHeroFullScreenBackdropEnabled = prefs.modernHeroFullScreenBackdropEnabled,
                        modernContinueWatchingHeroEnabled = prefs.modernContinueWatchingHeroEnabled,
                        homeImdbRatingsVisibility = prefs.homeImdbRatingsVisibility,
                        detailImdbRatingsVisibility = prefs.detailImdbRatingsVisibility,
                        focusedPosterBackdropExpandEnabled = prefs.focusedBackdropExpandEnabled,
                        focusedPosterBackdropExpandDelaySeconds = prefs.focusedBackdropExpandDelaySeconds,
                        focusedPosterBackdropTrailerEnabled = prefs.focusedBackdropTrailerEnabled,
                        focusedPosterBackdropTrailerMuted = prefs.focusedBackdropTrailerMuted,
                        focusedPosterBackdropTrailerPlaybackTarget = prefs.focusedBackdropTrailerPlaybackTarget,
                        posterCardWidthDp = prefs.posterCardWidthDp,
                        installedAppTileWidthDp = prefs.installedAppTileWidthDp,
                        posterCardHeightDp = prefs.posterCardHeightDp,
                        posterCardCornerRadiusDp = prefs.posterCardCornerRadiusDp
                    )
                }
                if (shouldRefreshCatalogPresentation) {
                    // When switching to GRID layout, load all pending lazy catalogs
                    // since grid doesn't support placeholder shimmer rows.
                    if (prefs.layout == HomeLayout.GRID) {
                        loadAllPendingLazyCatalogs()
                    }
                    // When hero catalog keys change, load any hero catalogs
                    // not yet in catalogsMap (e.g., after startup race or
                    // when user changes hero selection in settings).
                    if (heroKeysChanged && prefs.heroCatalogKeys.isNotEmpty()) {
                        loadHeroCatalogsPipeline()
                    } else {
                        scheduleUpdateCatalogRows()
                    }
                }
            }
    }
}

private data class ContinueWatchingPosterInput(
    val enabled: Boolean,
    val pattern: String,
    val screens: Set<CustomPosterScreen>,
    val continueWatchingItems: List<ContinueWatchingItem>,
    val upcomingItems: List<ContinueWatchingItem>,
    val artworkOverrides: Map<String, String>,
)

private data class ContinueWatchingPosterOverlay(
    val continueWatchingItems: List<ContinueWatchingItem>,
    val upcomingItems: List<ContinueWatchingItem>,
)

private data class CatalogRowPresentationIdentity(
    val key: String,
    val title: String,
    val itemKeys: List<String>,
    val isLoading: Boolean,
    val hasMore: Boolean,
)

// Track row membership and ordering while ignoring artwork and metadata enrichment updates.
private fun CatalogRow.presentationIdentity() = CatalogRowPresentationIdentity(
    key = stableKey(),
    title = catalogName,
    itemKeys = stableItemKeys(),
    isLoading = isLoading,
    hasMore = hasMore,
)

private fun ModernHomePresentationInput.continueWatchingIdentitySnapshot(): List<Pair<String, Set<String>>> =
    catalogRows.asSequence()
        .flatMap { it.items.asSequence() }
        .map { item ->
            val badgeIdentity = item.continueWatchingIdentityKeys().toMutableSet()
            listOfNotNull(item.name, item.type.name, item.apiType, item.releaseInfo)
                .forEach(badgeIdentity::add)
            item.id to badgeIdentity
        }
        .toList()

@OptIn(FlowPreview::class)
internal fun HomeViewModel.observeModernHomePresentationPipeline() {
    viewModelScope.launch {
        // Poster URLs are resolved only when the list or the pattern changes.
        // uiState also emits on focus and catalog updates; resolving there
        // reran placeholder replacement for every row on each scroll step.
        val posterOverlay = uiState
            .map { state ->
                ContinueWatchingPosterInput(
                    enabled = state.continueWatchingEnabled,
                    pattern = state.customPosterUrlPattern,
                    screens = state.customPosterEnabledScreens,
                    continueWatchingItems = state.continueWatchingItems,
                    upcomingItems = state.upcomingItems,
                    artworkOverrides = state.continueWatchingHeroArtworkOverrides,
                )
            }
            .distinctUntilChanged()
            .map { input ->
                if (!input.enabled) {
                    ContinueWatchingPosterOverlay(emptyList(), emptyList())
                } else {
                    val pattern = patternForScreen(
                        input.pattern,
                        CustomPosterScreen.CONTINUE_WATCHING,
                        input.screens,
                    )
                    ContinueWatchingPosterOverlay(
                        continueWatchingItems = input.continueWatchingItems
                            .withCustomPosterUrls(pattern)
                            .withContinueWatchingHeroArtworkOverrides(input.artworkOverrides),
                        upcomingItems = input.upcomingItems
                            .withCustomPosterUrls(pattern)
                            .withContinueWatchingHeroArtworkOverrides(input.artworkOverrides),
                    )
                }
            }
            .distinctUntilChanged()

        combine(uiState, _currentLocaleTag, posterOverlay) { state, localeTag, overlay ->
                ModernHomePresentationInput(
                    homeRows = state.homeRows,
                    catalogRows = state.catalogRows,
                    continueWatchingItems = overlay.continueWatchingItems,
                    upcomingItems = overlay.upcomingItems,
                    modernContinueWatchingHeroEnabled = state.modernContinueWatchingHeroEnabled,
                    useLandscapePosters = state.modernLandscapePostersEnabled,
                    showCatalogTypeSuffix = state.catalogTypeSuffixEnabled,
                    showFullReleaseDate = state.showFullReleaseDate,
                    showImdbRatings = state.homeImdbRatingsVisibility.showRatings,
                    detailImdbRatingsVisibility = state.detailImdbRatingsVisibility,
                    localeTag = localeTag,
                    catalogBadgeProgressItems = state.continueWatchingItems + state.upcomingItems
                )
            }
            // Ignore ordinary metadata changes so enrichment does not rebuild
            // every catalog row. While playback is in progress, also watch the
            // identity aliases used to propagate its badge across catalog rows.
            .distinctUntilChanged { old, new ->
                old.homeRows === new.homeRows
                    && old.continueWatchingItems == new.continueWatchingItems
                    && old.upcomingItems == new.upcomingItems
                    && old.catalogBadgeProgressItems == new.catalogBadgeProgressItems
                    && old.modernContinueWatchingHeroEnabled == new.modernContinueWatchingHeroEnabled
                    && old.useLandscapePosters == new.useLandscapePosters
                    && old.showCatalogTypeSuffix == new.showCatalogTypeSuffix
                    && old.showFullReleaseDate == new.showFullReleaseDate
                    && old.showImdbRatings == new.showImdbRatings
                    && old.localeTag == new.localeTag
                    && (
                        old.catalogRows === new.catalogRows ||
                            old.catalogRows.map { it.presentationIdentity() } ==
                            new.catalogRows.map { it.presentationIdentity() }
                    )
                    && (
                        (old.catalogBadgeProgressItems.isEmpty() && new.catalogBadgeProgressItems.isEmpty()) ||
                            old.continueWatchingIdentitySnapshot() == new.continueWatchingIdentitySnapshot()
                    )
            }
            .debounce {
                // Use a longer debounce while catalogs are still loading to
                // avoid repeated expensive presentation builds during the
                // initial burst of catalog arrivals.
                if (catalogsLoadInProgress) 300L else 80L
            }
            .collectLatest { input ->
                val shouldWarmStart = _modernHomePresentation.value.rows.list.isEmpty()
                val visibleCatalogRowCount = input.catalogRows.count { it.items.isNotEmpty() }
                val warmStartCatalogRowCount = if (input.continueWatchingItems.isNotEmpty()) 2 else 3

                if (shouldWarmStart && visibleCatalogRowCount > warmStartCatalogRowCount) {
                    val warmStartPresentation = withContext(Dispatchers.Default) {
                        buildModernHomePresentation(
                            input = input,
                            cache = modernCarouselRowBuildCache,
                            context = appContext,
                            maxCatalogRows = warmStartCatalogRowCount
                        )
                    }
                    if (_modernHomePresentation.value != warmStartPresentation) {
                        _modernHomePresentation.value = warmStartPresentation
                    }
                }

                val presentation = withContext(Dispatchers.Default) {
                    buildModernHomePresentation(
                        input = input,
                        cache = modernCarouselRowBuildCache,
                        context = appContext
                    )
                }
                if (_modernHomePresentation.value != presentation) {
                    _modernHomePresentation.value = presentation
                }
            }
    }
}

internal fun HomeViewModel.observeExternalMetaPrefetchPreferencePipeline() {
    viewModelScope.launch {
        layoutPreferenceDataStore.preferExternalMetaAddonDetail
            .collectLatest { enabled ->
                externalMetaPrefetchEnabled = enabled
                if (!enabled) {
                    externalMetaPrefetchJob?.cancel()
                    pendingExternalMetaPrefetchItemId = null
                    externalMetaPrefetchInFlightIds.clear()
                }
            }
    }
}

internal fun HomeViewModel.requestTrailerPreviewPipeline(item: MetaPreview) {
    requestTrailerPreviewPipeline(
        itemId = item.id,
        title = item.name,
        releaseInfo = item.releaseInfo,
        apiType = item.apiType,
        fallbackYtId = item.trailerYtIds.firstOrNull()
    )
}

internal fun HomeViewModel.requestTrailerPreviewPipeline(
    itemId: String,
    title: String,
    releaseInfo: String?,
    apiType: String,
    fallbackYtId: String? = null
) {
    if (!AppFeaturePolicy.inAppTrailerPlaybackEnabled) return
    if (startupGracePeriodActive) return
    if (ServerItemRef.isServerId(itemId)) return

    // Resolve fallbackYtId from catalog item if not provided
    val resolvedFallbackYtId = fallbackYtId ?: findCatalogItemById(itemId)?.trailerYtIds?.firstOrNull()

    // Always bump version — only the latest request (highest version) will proceed after debounce
    activeTrailerPreviewItemId = itemId
    trailerPreviewRequestVersion++
    val requestVersion = trailerPreviewRequestVersion

    if (trailerPreviewNegativeCache.contains(itemId)) return
    if (trailerPreviewUrlsState.containsKey(itemId)) return
    if (!trailerPreviewLoadingIds.add(itemId)) return

    trailerPreviewJob?.cancel()
    trailerPreviewJob = viewModelScope.launch(Dispatchers.IO) {
        try {
            // Debounce: wait for focus to settle before hitting network
            delay(180)

            // Only the LATEST request proceeds — all earlier ones are stale
            if (trailerPreviewRequestVersion != requestVersion) {
                return@launch
            }

            val tmdbId = try {
                tmdbService.ensureTmdbId(itemId, apiType)
            } catch (_: Exception) {
                null
            }

            val trailerSource = trailerService.getTrailerPlaybackSource(
                title = title,
                year = extractYear(releaseInfo),
                tmdbId = tmdbId,
                type = apiType
            )

            withContext(Dispatchers.Main) {
                if (trailerSource?.videoUrl.isNullOrBlank()) {
                    val fallbackSource = resolvedFallbackYtId?.let { ytId ->
                        trailerService.getTrailerPlaybackSourceFromYouTubeUrl(
                            youtubeUrl = "https://www.youtube.com/watch?v=$ytId",
                            title = title,
                            year = extractYear(releaseInfo)
                        )
                    }
                    if (fallbackSource?.videoUrl != null) {
                        if (trailerPreviewUrlsState[itemId] != fallbackSource.videoUrl) {
                            trailerPreviewUrlsState[itemId] = fallbackSource.videoUrl
                        }
                        val fallbackAudio = fallbackSource.audioUrl
                        if (fallbackAudio.isNullOrBlank()) {
                            trailerPreviewAudioUrlsState.remove(itemId)
                        } else if (trailerPreviewAudioUrlsState[itemId] != fallbackAudio) {
                            trailerPreviewAudioUrlsState[itemId] = fallbackAudio
                        }
                    } else {
                        // While YouTube is rate limiting, a miss isn't remembered, so the
                        // trailer comes back on a later focus once the pause is over.
                        if (!trailerService.isRateLimited()) {
                            trailerPreviewNegativeCache.add(itemId)
                        }
                        trailerPreviewUrlsState.remove(itemId)
                        trailerPreviewAudioUrlsState.remove(itemId)
                    }
                } else {
                    val videoUrl = trailerSource.videoUrl
                    if (trailerPreviewUrlsState[itemId] != videoUrl) {
                        trailerPreviewUrlsState[itemId] = videoUrl
                    }
                    val audioUrl = trailerSource.audioUrl
                    if (audioUrl.isNullOrBlank()) {
                        trailerPreviewAudioUrlsState.remove(itemId)
                    } else if (trailerPreviewAudioUrlsState[itemId] != audioUrl) {
                        trailerPreviewAudioUrlsState[itemId] = audioUrl
                    }
                }
            }
        } finally {
            trailerPreviewLoadingIds.remove(itemId)
        }
    }
}

/**
 * What an external meta prefetch produced. A failed fetch must be distinguishable from an addon
 * that answered with nothing to add: the first is retried on the next focus, the second is not.
 * Both used to collapse to null, so one unreachable addon suppressed enrichment for the session.
 *
 * Callers own the in-flight id: they claim it before launching and release it on completion.
 */
private sealed interface ExternalMetaOutcome {
    data class Resolved(val meta: Meta) : ExternalMetaOutcome
    /**
     * The addons answered and there is nothing more to fetch: either the catalog item is already
     * sufficient, or no addon carries this item. Both are final, so neither is retried.
     */
    object Final : ExternalMetaOutcome
    object Failed : ExternalMetaOutcome
}

/**
 * Whether an external meta fetch is still outstanding. A TMDB success must not stand in for one
 * that has not resolved, and both gates ask this rather than keeping their own copy of the rule.
 */
private fun HomeViewModel.externalEnrichmentOutstanding(itemId: String): Boolean =
    externalMetaPrefetchEnabled && itemId !in prefetchedExternalMetaIds

private suspend fun HomeViewModel.fetchExternalMetaOutcome(item: MetaPreview): ExternalMetaOutcome =
    try {
        val result = metaRepository.getMetaFromAllAddons(item.apiType, item.id, item.sourceAddonBaseUrl)
            .first { it is NetworkResult.Success || it is NetworkResult.Error }
        when {
            result is NetworkResult.Success -> ExternalMetaOutcome.Resolved(result.data)
            result is NetworkResult.Error &&
                (result.code == NetworkResult.SOURCE_SUFFICIENT_CODE ||
                    result.code == NetworkResult.META_NOT_FOUND_CODE) ->
                ExternalMetaOutcome.Final
            else -> ExternalMetaOutcome.Failed
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // The repository reports request failures as NetworkResult.Error, so this is the path it
        // does not promise: anything thrown outside its own per-addon handling. It used to leave
        // the launched enrichment coroutine to fail with it, which loses the focus pipeline for
        // that item. Treating it as Failed keeps the outcome exhaustive and lets the next focus
        // retry, which is what any other failure does.
        Log.w(HomeViewModel.TAG, "External meta fetch threw for ${item.id}: ${e.message}")
        ExternalMetaOutcome.Failed
    }

internal fun HomeViewModel.onItemFocusPipeline(item: MetaPreview) {
    if (item.id.startsWith(FREEBOX_FOLDER_ID_PREFIX) || item.id.startsWith("freebox:")) {
        onFreeboxItemFocusPipeline(item)
        return
    }
    if (startupGracePeriodActive) {
        deferredEnrichItem = item
        return
    }
    if (item.id in prefetchedTmdbIds || item.id in prefetchedExternalMetaIds) {
        // Only external enrichment re-opens this gate, so a cached external result still shuts out
        // an unresolved TMDB fetch. That is unchanged from before and deliberate: TMDB is never
        // marked prefetched when an item has no TMDB match, so gating on it too would re-enter on
        // every focus and call ensureTmdbId each time. Fixing it needs a terminal marker for TMDB
        // first. The fetches below are gated per source, so re-entering issues only the external
        // request.
        if (!externalEnrichmentOutstanding(item.id)) {
            // Ensure enrichedPreviews contains this item so the UI can display
            // hero data immediately (e.g. when adjacent prefetch resolved it
            // before the user focused on it).
            if (item.id !in _enrichedPreviews.value) {
                var enriched = findCatalogItemById(item.id) ?: item
                // Preserve MDBList ratings from batch prefetch if the catalog
                // snapshot lost them (e.g. row rebuild between batch and focus).
                if (enriched.mdbListRatings == null) {
                    val existing = _enrichedPreviews.value[item.id]
                    if (existing?.mdbListRatings != null) {
                        enriched = enriched.copy(
                            mdbListRatings = existing.mdbListRatings,
                            mdbListRatingOrder = existing.mdbListRatingOrder,
                            imdbRating = existing.mdbListRatings.imdb?.toFloat() ?: enriched.imdbRating
                        )
                    }
                }
                addEnrichedPreview(item.id, enriched)
            }
            if (_enrichingItemId.value == item.id) setEnrichingItemId(null)
            // Still prefetch full meta in background for instant detail screen.
            if (item.id !in backgroundMetaPrefetchedIds) {
                backgroundMetaPrefetchedIds.add(item.id)
                viewModelScope.launch {
                    metaRepository.getMetaFromAllAddons(
                        type = item.apiType,
                        id = item.id
                    ).first { it !is NetworkResult.Loading }
                }
            }
            return
        }
    }
    if (pendingTmdbEnrichItemId == item.id) return

    // Clear enriching for previous item immediately when focus moves away
    if (_enrichingItemId.value != null && _enrichingItemId.value != item.id) {
        setEnrichingItemId(null)
    }

    val tmdbEnabledForCurrentLayout = currentTmdbSettings.enabled &&
        (_uiState.value.homeLayout != HomeLayout.MODERN || currentTmdbSettings.modernHomeEnabled)
    val willEnrich = tmdbEnabledForCurrentLayout || externalMetaPrefetchEnabled

    pendingTmdbEnrichItemId = item.id
    tmdbEnrichFocusJob?.cancel()
    tmdbEnrichFocusJob = viewModelScope.launch(Dispatchers.IO) {
        delay(HomeViewModel.EXTERNAL_META_PREFETCH_FOCUS_DEBOUNCE_MS)
        if (pendingTmdbEnrichItemId != item.id) {
            if (_enrichingItemId.value == item.id) setEnrichingItemId(null)
            return@launch
        }
        // Don't hide the hero if we already have enriched data for this item.
        // The enrichment can proceed in the background without visual disruption.
        val alreadyEnriched = item.id in _enrichedPreviews.value
        if (willEnrich && !alreadyEnriched) setEnrichingItemId(item.id)
        if (item.id in prefetchedTmdbIds || item.id in prefetchedExternalMetaIds) {
            if (!externalEnrichmentOutstanding(item.id)) {
                if (_enrichingItemId.value == item.id) setEnrichingItemId(null)
                // Still prefetch full meta in background for instant detail screen.
                if (item.id !in backgroundMetaPrefetchedIds) {
                    backgroundMetaPrefetchedIds.add(item.id)
                    launch {
                        metaRepository.getMetaFromAllAddons(
                            type = item.apiType,
                            id = item.id
                        ).first { it !is NetworkResult.Loading }
                    }
                }
                return@launch
            }
        }

        try {
            // Launch TMDB and external meta addon fetch in parallel.
            // Which sources are used depends on settings:
            // - tmdbEnabledForCurrentLayout: controls TMDB enrichment
            // - externalMetaPrefetchEnabled: controls external meta addon fetch
            val tmdbDeferred = if (tmdbEnabledForCurrentLayout && item.id !in prefetchedTmdbIds) {
                async { fetchHomeTmdbEnrichment(item) }
            } else null


            val externalMetaDeferred = if (externalMetaPrefetchEnabled &&
                item.id !in prefetchedExternalMetaIds &&
                externalMetaPrefetchInFlightIds.add(item.id)
            ) {
                // The id is claimed here, in the enclosing coroutine, but released when the
                // deferred completes rather than inside its body. A focus that moves on during
                // the debounce cancels this job before the body runs, and a body that never runs
                // never reaches its own finally, which would strand the id in the in-flight set
                // and block every later fetch for that item.
                async { fetchExternalMetaOutcome(item) }
                    .also { d -> d.invokeOnCompletion { externalMetaPrefetchInFlightIds.remove(item.id) } }
            } else null

            // Await both results
            val tmdbEnrichment = tmdbDeferred?.await()
            val externalOutcome = externalMetaDeferred?.await()
            val externalMeta = (externalOutcome as? ExternalMetaOutcome.Resolved)?.meta

            // Mark as prefetched
            if (tmdbEnrichment != null) prefetchedTmdbIds.add(item.id)
            if (externalOutcome != null && externalOutcome != ExternalMetaOutcome.Failed) {
                prefetchedExternalMetaIds.add(item.id)
            }

            // Merge results: apply external meta first (base layer), then TMDB on top
            // respecting which TMDB settings are enabled.
            if (externalMeta != null) {
                updateCatalogItemWithMeta(item.id, externalMeta)
            }
            if (tmdbEnrichment != null) {
                updateCatalogItemWithTmdb(item.id, tmdbEnrichment)
            }

            // If neither source produced anything, mark enrichment in previews
            // so UI doesn't keep showing spinner. Skip titles a merge already reached: a row
            // loaded after that merge carries raw data, and publishing it would downgrade them.
            if (tmdbEnrichment == null && externalMeta == null && item.id !in enrichmentMergedIds) {
                var preview = findCatalogItemById(item.id) ?: item
                // Preserve MDBList ratings from batch prefetch.
                if (preview.mdbListRatings == null) {
                    val existing = _enrichedPreviews.value[item.id]
                    if (existing?.mdbListRatings != null) {
                        preview = preview.copy(
                            mdbListRatings = existing.mdbListRatings,
                            mdbListRatingOrder = existing.mdbListRatingOrder,
                            imdbRating = existing.mdbListRatings.imdb?.toFloat() ?: preview.imdbRating
                        )
                    }
                }
                if (_enrichedPreviews.value[item.id] != preview) addEnrichedPreview(item.id, preview)
            }

            // Always prefetch full meta in background for instant detail screen loading.
            if (item.id !in backgroundMetaPrefetchedIds) {
                backgroundMetaPrefetchedIds.add(item.id)
                viewModelScope.launch {
                    metaRepository.getMetaFromAllAddons(
                        type = item.apiType,
                        id = item.id
                    ).first { it !is NetworkResult.Loading }
                }
            }

            // Warm up watch progress pipeline so detail screen reads are fast.
            viewModelScope.launch {
                watchProgressRepository.getAllEpisodeProgress(item.id.substringBefore(":")).first()
            }

        } finally {
            // Release the claim only if it is still ours: a later focus may have claimed another item.
            if (pendingTmdbEnrichItemId == item.id) pendingTmdbEnrichItemId = null
            if (_enrichingItemId.value == item.id) {
                setEnrichingItemId(null)
                // If enrichment completed but no enriched data exists for this item,
                // mark it as failed so the UI can show addon data immediately.
                if (item.id !in _enrichedPreviews.value &&
                    item.id !in prefetchedExternalMetaIds &&
                    item.id !in prefetchedTmdbIds) {
                    markEnrichmentFailed(item.id)
                }
            }
        }
    }
}

private fun HomeViewModel.onFreeboxItemFocusPipeline(item: MetaPreview) {
    if (focusedFreeboxItemId != item.id) onFocusedHomeItemChanged(item)
    val baseItemForHandoff = findCatalogItemById(item.id) ?: item
    val existingHandoff = freeboxRepository?.homeVideoPresentation(item.id)
    if (existingHandoff == null || existingHandoff.sourceFileSizeBytes != item.sourceFileSizeBytes) {
        freeboxRepository?.rememberHomeVideoPresentation(
            baseItemForHandoff.copy(
                description = null,
                tmdbId = null,
                tmdbMediaType = null,
                castMembers = emptyList(),
                director = emptyList()
            )
        )
    }
    if (startupGracePeriodActive) {
        deferredEnrichItem = item
        return
    }
    if (item.id in prefetchedFreeboxMetadataIds) {
        val cached = _enrichedPreviews.value[item.id] ?: findCatalogItemById(item.id) ?: item
        if (item.id !in _enrichedPreviews.value) addEnrichedPreview(item.id, cached)
        if (focusedFreeboxItemId == item.id) _lastEnrichedPreview.value = cached
        if (_enrichingItemId.value == item.id) setEnrichingItemId(null)
        return
    }
    if (pendingTmdbEnrichItemId == item.id) return

    if (_enrichingItemId.value != null && _enrichingItemId.value != item.id) {
        setEnrichingItemId(null)
    }
    freeboxFocusGeneration++
    val requestGeneration = freeboxFocusGeneration
    pendingTmdbEnrichItemId = item.id
    tmdbEnrichFocusJob?.cancel()

    val settings = currentTmdbSettings
    val tmdbEnabled = settings.enabled &&
        (_uiState.value.homeLayout != HomeLayout.MODERN || settings.modernHomeEnabled) &&
        (settings.useArtwork || settings.useBasicInfo || settings.useDetails)
    val hasFreeboxReader = freeboxRepository != null
    tmdbEnrichFocusJob = viewModelScope.launch(Dispatchers.IO) {
        delay(HomeViewModel.EXTERNAL_META_PREFETCH_FOCUS_DEBOUNCE_MS)
        if (pendingTmdbEnrichItemId != item.id) return@launch

        if ((tmdbEnabled || hasFreeboxReader) && item.id !in _enrichedPreviews.value) {
            setEnrichingItemId(item.id)
        }

        try {
            val isFolder = item.id.startsWith(FREEBOX_FOLDER_ID_PREFIX)
            val relativePath = if (isFolder) {
                item.id.removePrefix(FREEBOX_FOLDER_ID_PREFIX)
            } else {
                item.id.removePrefix("freebox:")
            }
            val fileMetadataDeferred = async {
                if (isFolder) null else freeboxRepository?.videoMetadata(relativePath, item.sourceFileSizeBytes)
            }
            val folderDurationDeferred = async {
                if (isFolder) freeboxRepository?.durationMinutes(relativePath, true) else null
            }

            val baseItem = findCatalogItemById(item.id) ?: item
            val fileMetadata = fileMetadataDeferred.await()
            val folderDurationMinutes = folderDurationDeferred.await()

            val parentTitle = if (!isFolder) {
                relativePath.substringBeforeLast('/', "").substringAfterLast('/').trim()
                    .takeIf(String::isNotBlank)
            } else null
            val numericEpisodeName = item.name.trim().matches(
                Regex("(?i)^(?:episode\\s*)?\\d{1,4}$|^s\\d{1,3}e\\d{1,3}$")
            )
            val fileTitle = fileMetadata?.title?.takeIf(String::isNotBlank)
            val fileYear = fileMetadata?.year ?: item.name
                .let { Regex("\\b(?:19|20)\\d{2}\\b").find(it)?.value?.toIntOrNull() }
            val fallbackFileTitle = if (numericEpisodeName) parentTitle else null
            val titleCandidates = buildList {
                if (numericEpisodeName) parentTitle?.let { add(it) }
                fileTitle?.let { if (it !in this) add(it) }
                item.name.trim().takeIf(String::isNotBlank)?.let { if (it !in this) add(it) }
                parentTitle?.let { if (it !in this) add(it) }
            }
            val localPreview = buildFreeboxLocalPreview(
                base = baseItem,
                fileMetadata = fileMetadata,
                fallbackTitle = fallbackFileTitle,
                yearHint = fileYear,
                folderDurationMinutes = folderDurationMinutes
            )
            if (!isCurrentFreeboxFocusResult(
                    requestedId = item.id,
                    requestedGeneration = requestGeneration,
                    focusedId = focusedFreeboxItemId,
                    currentGeneration = freeboxFocusGeneration
                )) return@launch
            addEnrichedPreview(item.id, localPreview)
            freeboxRepository?.rememberHomeVideoPresentation(localPreview)

            var tmdbMatch: com.nuvio.tv.core.tmdb.TmdbExactTitleMatch? = null
            var matchedTitle: String? = null
            var usedSavedArtworkMatch = false
            if (tmdbEnabled) {
                val savedArtwork = freeboxRepository?.folderArtwork(relativePath)
                val savedArtworkMatch = savedArtwork?.tmdbId?.let { tmdbId ->
                    val contentType = com.nuvio.tv.domain.model.ContentType.fromString(
                        savedArtwork.tmdbContentType.orEmpty()
                    )
                    if (contentType == com.nuvio.tv.domain.model.ContentType.UNKNOWN) null
                    else com.nuvio.tv.core.tmdb.TmdbExactTitleMatch(tmdbId, contentType)
                }
                if (savedArtworkMatch != null) {
                    usedSavedArtworkMatch = true
                    tmdbMatch = savedArtworkMatch
                    matchedTitle = fileTitle ?: item.name
                } else {
                    val tmdbMediaTypes = if (!isFolder) {
                        listOf(item.apiType)
                    } else when (item.type) {
                        com.nuvio.tv.domain.model.ContentType.MOVIE -> listOf("movie", "series")
                        com.nuvio.tv.domain.model.ContentType.SERIES -> listOf("series", "movie")
                        else -> listOf("series", "movie")
                    }
                    for (candidate in titleCandidates) {
                        for (candidateMediaType in tmdbMediaTypes) {
                            val match = try {
                                tmdbService.findExactTitleMatch(
                                    title = candidate,
                                    mediaType = candidateMediaType,
                                    language = settings.language,
                                    year = fileYear
                                )
                            } catch (cancelled: CancellationException) {
                                throw cancelled
                            } catch (_: Exception) {
                                null
                            }
                            if (match != null) {
                                tmdbMatch = match
                                matchedTitle = candidate
                                break
                            }
                        }
                        if (tmdbMatch != null) break
                    }
                }
            }
            val enrichment = tmdbMatch?.let { match ->
                try {
                    tmdbMetadataService.fetchEnrichment(
                        tmdbId = match.tmdbId.toString(),
                        contentType = match.contentType,
                        language = settings.language
                    )
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    null
                }
            }

            if (!isCurrentFreeboxFocusResult(
                    requestedId = item.id,
                    requestedGeneration = requestGeneration,
                    focusedId = focusedFreeboxItemId,
                    currentGeneration = freeboxFocusGeneration
                )) return@launch

            val artwork = freeboxRepository?.folderArtwork(relativePath)
            val enrichedItem = mergeFreeboxVideoEnrichment(
                local = localPreview,
                fileMetadata = fileMetadata,
                yearHint = fileYear,
                folderDurationMinutes = folderDurationMinutes,
                matchedTitle = matchedTitle,
                match = tmdbMatch,
                enrichment = enrichment,
                artwork = artwork,
                useBasicInfo = settings.useBasicInfo,
                useDetails = settings.useDetails,
                useArtwork = settings.useArtwork,
                usedSavedArtworkMatch = usedSavedArtworkMatch
            )
            addEnrichedPreview(item.id, enrichedItem)
            freeboxRepository?.rememberHomeVideoPresentation(enrichedItem)
            if (focusedFreeboxItemId == item.id && requestGeneration == freeboxFocusGeneration) {
                _lastEnrichedPreview.value = enrichedItem
            }
            prefetchedFreeboxMetadataIds.add(item.id)
            clearEnrichmentFailure(item.id)
        } finally {
            if (requestGeneration == freeboxFocusGeneration && pendingTmdbEnrichItemId == item.id) {
                pendingTmdbEnrichItemId = null
            }
            if (requestGeneration == freeboxFocusGeneration && _enrichingItemId.value == item.id) {
                setEnrichingItemId(null)
            }
        }
    }
}

internal fun isCurrentFreeboxFocusResult(
    requestedId: String,
    requestedGeneration: Long,
    focusedId: String?,
    currentGeneration: Long
): Boolean = requestedId == focusedId && requestedGeneration == currentGeneration

internal fun HomeViewModel.refreshFreeboxMetadataAfterArtworkSelection(item: MetaPreview) {
    prefetchedFreeboxMetadataIds.remove(item.id)
    _enrichedPreviews.update { it - item.id }
    if (_lastEnrichedPreview.value?.id == item.id) _lastEnrichedPreview.value = null
    if (pendingTmdbEnrichItemId == item.id) {
        tmdbEnrichFocusJob?.cancel()
        pendingTmdbEnrichItemId = null
    }
    onFreeboxItemFocusPipeline(item)
}

/**
 * Shares the fetch with the focused path but deliberately not its gate: an item whose TMDB fetch
 * succeeded is skipped here even when external metadata is outstanding. Adjacent prefetch is
 * opportunistic and the focused path retries anyway, so aligning the two would only add requests.
 */
internal fun HomeViewModel.preloadAdjacentItemPipeline(item: MetaPreview) {
    if (startupGracePeriodActive) return
    if (item.id in prefetchedTmdbIds || item.id in prefetchedExternalMetaIds) return
    if (pendingTmdbEnrichItemId == item.id || pendingAdjacentPrefetchItemId == item.id) return

    pendingAdjacentPrefetchItemId = item.id
    adjacentItemPrefetchJob?.cancel()
    adjacentItemPrefetchJob = viewModelScope.launch(Dispatchers.IO) {
        val tmdbEnabledForCurrentLayout = currentTmdbSettings.enabled &&
            (_uiState.value.homeLayout != HomeLayout.MODERN || currentTmdbSettings.modernHomeEnabled)
        delay(HomeViewModel.EXTERNAL_META_PREFETCH_ADJACENT_DEBOUNCE_MS)
        if (pendingAdjacentPrefetchItemId != item.id) return@launch

        if (item.id in prefetchedTmdbIds || item.id in prefetchedExternalMetaIds) return@launch

        try {
            // Launch TMDB and external meta addon fetch in parallel (same as focused pipeline).
            val tmdbDeferred = if (tmdbEnabledForCurrentLayout) {
                async { fetchHomeTmdbEnrichment(item) }
            } else null

            val externalMetaDeferred = if (externalMetaPrefetchEnabled &&
                item.id !in prefetchedExternalMetaIds &&
                externalMetaPrefetchInFlightIds.add(item.id)
            ) {
                // The id is claimed here, in the enclosing coroutine, but released when the
                // deferred completes rather than inside its body. A focus that moves on during
                // the debounce cancels this job before the body runs, and a body that never runs
                // never reaches its own finally, which would strand the id in the in-flight set
                // and block every later fetch for that item.
                async { fetchExternalMetaOutcome(item) }
                    .also { d -> d.invokeOnCompletion { externalMetaPrefetchInFlightIds.remove(item.id) } }
            } else null

            val tmdbEnrichment = tmdbDeferred?.await()
            val externalOutcome = externalMetaDeferred?.await()
            val externalMeta = (externalOutcome as? ExternalMetaOutcome.Resolved)?.meta

            if (tmdbEnrichment != null) prefetchedTmdbIds.add(item.id)
            if (externalOutcome != null && externalOutcome != ExternalMetaOutcome.Failed) {
                prefetchedExternalMetaIds.add(item.id)
            }

            if (externalMeta != null) {
                updateCatalogItemWithMeta(item.id, externalMeta)
            }
            if (tmdbEnrichment != null) {
                updateCatalogItemWithTmdb(item.id, tmdbEnrichment)
            }

            // MDBList ratings are handled by batch row prefetch
            // (see HomeViewModelMdbListBatchPrefetch.kt).

            if (tmdbEnrichment == null && externalMeta == null) {
                addEnrichedPreview(item.id, item)
            }

            // Background prefetch for detail screen cache.
            if (item.id !in backgroundMetaPrefetchedIds) {
                backgroundMetaPrefetchedIds.add(item.id)
                viewModelScope.launch {
                    metaRepository.getMetaFromAllAddons(
                        type = item.apiType,
                        id = item.id
                    ).first { it !is NetworkResult.Loading }
                }
            }

        } finally {
            if (pendingAdjacentPrefetchItemId == item.id) {
                pendingAdjacentPrefetchItemId = null
            }
        }
    }
}

/** Enriches home-library favorites even when the user does not focus each card individually. */
internal fun HomeViewModel.prefetchFavoriteArtworkPipeline(items: List<MetaPreview>) {
    val settings = currentTmdbSettings
    if (!settings.enabled || !settings.useArtwork) return

    items.distinctBy { it.id }.forEach { item ->
        if (item.id in prefetchedTmdbIds || !favoriteArtworkEnrichmentInFlightIds.add(item.id)) {
            return@forEach
        }
        viewModelScope.launch {
            try {
                favoriteArtworkEnrichmentSemaphore.withPermit {
                    if (item.id in prefetchedTmdbIds) return@withPermit
                    val enrichment = fetchHomeTmdbEnrichment(item, preferExactTitle = true) ?: return@withPermit
                    val stillFavorite = favoriteHomeCatalogRows.value.any { row ->
                        row.addonId == HOME_FAVORITES_ADDON_ID && row.items.any { it.id == item.id }
                    }
                    val currentSettings = currentTmdbSettings
                    val stillEnabled = currentSettings.enabled && currentSettings.useArtwork
                    if (!stillFavorite || !stillEnabled) return@withPermit
                    prefetchedTmdbIds.add(item.id)
                    updateCatalogItemWithTmdb(item.id, enrichment, favoriteRowsOnly = true)
                }
            } finally {
                favoriteArtworkEnrichmentInFlightIds.remove(item.id)
            }
        }
    }
}

/** Resolves TMDB IDs from provider identifiers first, then from an exact title match. */
private suspend fun HomeViewModel.fetchHomeTmdbEnrichment(
    item: MetaPreview,
    preferExactTitle: Boolean = false
): TmdbEnrichment? {
    suspend fun fetch(tmdbId: String, contentType: com.nuvio.tv.domain.model.ContentType) =
        runCatching {
            tmdbMetadataService.fetchEnrichment(
                tmdbId = tmdbId,
                contentType = contentType,
                language = currentTmdbSettings.language
            )
        }.getOrNull()

    suspend fun fetchByExactTitle(): TmdbEnrichment? {
        val titleMatch = runCatching {
            tmdbService.findExactTitleMatch(
                title = if (preferExactTitle) item.homeFavoriteTmdbLookupTitle() else item.name,
                mediaType = item.apiType,
                language = currentTmdbSettings.language,
                year = item.releaseInfo?.take(4)?.toIntOrNull()
            )
        }.getOrNull() ?: return null
        return fetch(titleMatch.tmdbId.toString(), titleMatch.contentType)
    }

    if (preferExactTitle) fetchByExactTitle()?.let { return it }
    val directId = runCatching { tmdbService.ensureTmdbId(item.id, item.apiType) }.getOrNull()
    if (directId != null) {
        fetch(directId, item.type)?.let { return it }
    }
    if (preferExactTitle) return null
    val titleMatch = runCatching {
        tmdbService.findExactTitleMatch(
            title = item.name,
            mediaType = item.apiType,
            language = currentTmdbSettings.language,
            year = item.releaseInfo?.take(4)?.toIntOrNull()
        )
    }.getOrNull() ?: return null
    val matchedTmdbId = titleMatch.tmdbId.toString()
    if (directId == matchedTmdbId && titleMatch.contentType == item.type) return null
    return fetch(matchedTmdbId, titleMatch.contentType)
}

/**
 * Applies enrichment to the collections consumed by the non-modern layouts.
 *
 * [transform] must be pure and idempotent: _uiState.update can retry, and the item is merged in
 * each collection independently.
 */
private fun HomeViewModel.applyEnrichmentToDisplayedRows(
    itemId: String,
    transform: (MetaPreview) -> MetaPreview
) {
    _uiState.update { state ->
        if (state.homeLayout == HomeLayout.MODERN) return@update state
        var changed = false

        fun patch(row: com.nuvio.tv.domain.model.CatalogRow): com.nuvio.tv.domain.model.CatalogRow {
            val index = row.items.indexOfFirst { it.id == itemId }
            if (index < 0) return row
            val merged = transform(row.items[index])
            if (merged == row.items[index]) return row
            changed = true
            return row.copy(items = row.items.toMutableList().apply { set(index, merged) })
        }

        val updatedCatalogRows = state.catalogRows.map(::patch)
        val updatedHomeRows = state.homeRows.map { homeRow ->
            if (homeRow is HomeRow.Catalog) {
                val patched = patch(homeRow.row)
                if (patched === homeRow.row) homeRow else HomeRow.Catalog(patched)
            } else {
                homeRow
            }
        }
        val updatedGridItems = state.gridItems.map { gridItem ->
            if (gridItem is GridItem.Content && gridItem.item.id == itemId) {
                val merged = transform(gridItem.item)
                if (merged == gridItem.item) {
                    gridItem
                } else {
                    changed = true
                    gridItem.copy(item = merged)
                }
            } else {
                gridItem
            }
        }

        if (changed) {
            state.copy(
                catalogRows = updatedCatalogRows,
                homeRows = updatedHomeRows,
                gridItems = updatedGridItems
            )
        } else {
            state
        }
    }
}

private fun HomeViewModel.updateCatalogItemWithTmdb(
    itemId: String,
    enrichment: TmdbEnrichment,
    favoriteRowsOnly: Boolean = false
) {
    enrichmentMergedIds.add(itemId)
    val isModernLayout = _uiState.value.homeLayout == HomeLayout.MODERN
    val isHomeFavorite = favoriteHomeCatalogRows.value.any { row ->
        row.addonId == HOME_FAVORITES_ADDON_ID && row.items.any { it.id == itemId }
    }
    fun mergeItem(currentItem: MetaPreview): MetaPreview {
        var merged = currentItem
        if (currentTmdbSettings.useBasicInfo) {
            merged = merged.copy(
                name = if (isModernLayout) enrichment.localizedTitle ?: merged.name else merged.name,
                description = enrichment.description ?: merged.description,
                genres = if (enrichment.genres.isNotEmpty()) enrichment.genres else merged.genres,
                releaseInfo = enrichment.releaseInfo ?: merged.releaseInfo
            )
        }
        if (currentTmdbSettings.useArtwork) {
            val favoritePortraitOverride = if (isHomeFavorite) homeFavoriteArtworkOverride(itemId, portrait = true) else null
            val favoriteLandscapeOverride = if (isHomeFavorite) homeFavoriteArtworkOverride(itemId, portrait = false) else null
            merged = merged.copy(
                background = favoriteLandscapeOverride ?: enrichment.backdrop ?: merged.background,
                poster = if (isHomeFavorite) favoritePortraitOverride ?: enrichment.poster ?: merged.poster else merged.poster,
                landscapePoster = if (isHomeFavorite) {
                    favoriteLandscapeOverride ?: enrichment.backdrop ?: merged.landscapePoster
                } else {
                    merged.landscapePoster
                },
                logo = enrichment.logo ?: merged.logo
            )
        }
        if (currentTmdbSettings.useDetails) {
            val isSeries = isSeriesType(currentItem.apiType)
            val hasTmdbSeriesDetails = isSeries && enrichment.hasTmdbSeriesDetails
            merged = merged.copy(
                runtime = enrichment.runtimeMinutes?.toString() ?: merged.runtime,
                ageRating = enrichment.ageRating ?: merged.ageRating,
                status = if (isSeries) enrichment.status.takeIf { hasTmdbSeriesDetails }
                else enrichment.status ?: merged.status,
                currentSeasonComplete = enrichment.currentSeasonComplete ?: merged.currentSeasonComplete,
                seasonCount = if (isSeries) enrichment.seasonCount.takeIf { hasTmdbSeriesDetails }
                else enrichment.seasonCount ?: merged.seasonCount,
                hasTmdbSeriesDetails = hasTmdbSeriesDetails
            )
        }
        return merged
    }

    var favoriteRowsChanged = false
    if (favoriteRowsOnly) {
        favoriteHomeCatalogRows.update { rows ->
            var changed = false
            val updatedRows = rows.map { row ->
                val itemIndex = row.items.indexOfFirst { it.id == itemId }
                if (itemIndex < 0) return@map row
                val current = row.items[itemIndex]
                val updated = mergeItem(current)
                if (updated == current) return@map row
                changed = true
                row.copy(items = row.items.toMutableList().apply { set(itemIndex, updated) })
            }
            favoriteRowsChanged = changed
            if (changed) updatedRows else rows
        }
    } else {
        updateIndexedCatalogItem(itemId, ::mergeItem)
    }
    clearEnrichmentFailure(itemId)

    if (favoriteRowsOnly) {
        if (favoriteRowsChanged) scheduleUpdateCatalogRows()
    } else {
        applyEnrichmentToDisplayedRows(itemId, ::mergeItem)
        if (isHomeFavorite) scheduleUpdateCatalogRows()
    }

    val enrichedItem = if (favoriteRowsOnly) {
        favoriteHomeCatalogRows.value.firstNotNullOfOrNull { row -> row.items.firstOrNull { it.id == itemId } }
    } else {
        findCatalogItemById(itemId)
    }
    enrichedItem?.let { enriched ->
        if (!favoriteRowsOnly) _lastEnrichedPreview.value = enriched
        addEnrichedPreview(itemId, enriched)
    }
}

internal fun HomeViewModel.updateCatalogItemMdbListRatings(
    itemId: String,
    ratings: com.nuvio.tv.domain.model.MDBListRatings
) {
    val order = currentMdbListSettings.ratingOrder
    fun mergeItem(currentItem: MetaPreview): MetaPreview =
        currentItem.copy(
            mdbListRatings = ratings,
            mdbListRatingOrder = order,
            imdbRating = ratings.imdb?.toFloat() ?: currentItem.imdbRating
        )

    updateIndexedCatalogItem(itemId, ::mergeItem)
    // Apply to _uiState.catalogRows for ALL layouts including MODERN.
    // applyEnrichmentToDisplayedRows skips MODERN, but MDB ratings must
    // reach catalogRows so the carousel presentation rebuild picks them up.
    _uiState.update { state ->
        var changed = false
        val updatedRows = state.catalogRows.map { row ->
            val idx = row.items.indexOfFirst { it.id == itemId }
            if (idx < 0) row
            else {
                val merged = mergeItem(row.items[idx])
                if (merged == row.items[idx]) row
                else {
                    changed = true
                    row.copy(items = row.items.toMutableList().apply { set(idx, merged) })
                }
            }
        }
        if (changed) state.copy(catalogRows = updatedRows) else state
    }

    val existing = _enrichedPreviews.value[itemId]
    if (existing != null) {
        val enriched = findCatalogItemById(itemId)
        if (enriched != null && enriched != existing) {
            _lastEnrichedPreview.value = enriched
            addEnrichedPreview(itemId, enriched)
        }
    }
}

internal fun HomeViewModel.updateCatalogItemImdbRating(itemId: String, rating: Float) {
    updateIndexedCatalogItem(itemId) { currentItem ->
        currentItem.copy(imdbRating = rating)
    }
    _uiState.update { state ->
        var changed = false
        val updatedRows = state.catalogRows.map { row ->
            val idx = row.items.indexOfFirst { it.id == itemId }
            if (idx < 0) row
            else {
                val updated = row.items[idx].copy(imdbRating = rating)
                if (updated == row.items[idx]) row
                else {
                    changed = true
                    val mutableItems = row.items.toMutableList()
                    mutableItems[idx] = updated
                    row.copy(items = mutableItems)
                }
            }
        }
        if (changed) state.copy(catalogRows = updatedRows) else state
    }
}

private fun HomeViewModel.updateCatalogItemWithMeta(itemId: String, meta: Meta) {
    enrichmentMergedIds.add(itemId)
    val incomingTrailerYtIds = meta.trailerYtIds
    val isSeries = isSeriesType(meta.apiType)

    fun mergeItem(currentItem: MetaPreview): MetaPreview {
        val hasTmdbSeriesDetails = isSeries &&
            (meta.hasTmdbSeriesDetails || currentItem.hasTmdbSeriesDetails)
        val trustedStatus = when {
            !isSeries -> meta.status ?: currentItem.status
            meta.hasTmdbSeriesDetails -> meta.status
            currentItem.hasTmdbSeriesDetails -> currentItem.status
            else -> null
        }
        val trustedSeasonCount = when {
            !isSeries -> currentItem.seasonCount
            meta.hasTmdbSeriesDetails -> meta.seasonCount?.takeIf { it > 0 }
            currentItem.hasTmdbSeriesDetails -> currentItem.seasonCount?.takeIf { it > 0 }
            else -> null
        }
        return currentItem.copy(
            background = meta.backdropUrl ?: currentItem.backdropUrl,
            logo = meta.logo ?: currentItem.logo,
            description = meta.description ?: currentItem.description,
            imdbRating = meta.imdbRating ?: currentItem.imdbRating,
            genres = if (meta.genres.isNotEmpty()) meta.genres else currentItem.genres,
            runtime = meta.runtime ?: currentItem.runtime,
            status = trustedStatus,
            ageRating = meta.ageRating ?: currentItem.ageRating,
            language = meta.language ?: currentItem.language,
            country = meta.country ?: currentItem.country,
            seasonCount = trustedSeasonCount,
            hasTmdbSeriesDetails = hasTmdbSeriesDetails,
            trailerYtIds = if (incomingTrailerYtIds.isNotEmpty()) incomingTrailerYtIds else currentItem.trailerYtIds
        )
    }

    updateIndexedCatalogItem(itemId, ::mergeItem)
    clearEnrichmentFailure(itemId)

    applyEnrichmentToDisplayedRows(itemId, ::mergeItem)
    findCatalogItemById(itemId)?.let { enriched ->
        _lastEnrichedPreview.value = enriched
        addEnrichedPreview(itemId, enriched)
    }

    // If external meta brought new trailerYtIds and the item has no trailer resolved yet, retry.
    // Only retry if this item is currently focused — avoid prefetching trailers for adjacent items.
    if (incomingTrailerYtIds.isNotEmpty() && !trailerPreviewUrlsState.containsKey(itemId) && activeTrailerPreviewItemId == itemId) {
        trailerPreviewNegativeCache.remove(itemId)
        trailerPreviewLoadingIds.remove(itemId)
        // Bump version so any in-flight pipeline for this item treats itself as stale
        // and won't overwrite the retry result with a negative cache entry.
        trailerPreviewRequestVersion++
        val currentItem = findCatalogItemById(itemId) ?: return
        requestTrailerPreviewPipeline(currentItem)
    }
}

private fun HomeViewModel.updateCatalogItemArtworkOnly(itemId: String, meta: Meta) {
    fun mergeItem(currentItem: MetaPreview): MetaPreview = currentItem.copy(
        background = meta.backdropUrl ?: currentItem.backdropUrl,
        logo = meta.logo ?: currentItem.logo
    )

    updateIndexedCatalogItem(itemId, ::mergeItem)

    _uiState.update { state ->
        var changed = false
        val updatedRows = state.catalogRows.map { row ->
            val itemIndex = row.items.indexOfFirst { it.id == itemId }
            if (itemIndex < 0) {
                row
            } else {
                val mergedItem = mergeItem(row.items[itemIndex])
                if (mergedItem == row.items[itemIndex]) {
                    row
                } else {
                    changed = true
                    val mutableItems = row.items.toMutableList()
                    mutableItems[itemIndex] = mergedItem
                    row.copy(items = mutableItems)
                }
            }
        }
        if (changed) state.copy(catalogRows = updatedRows) else state
    }
    findCatalogItemById(itemId)?.let { enriched ->
        _lastEnrichedPreview.value = enriched
        addEnrichedPreview(itemId, enriched)
    }
}

internal suspend fun HomeViewModel.enrichHeroItemsPipeline(
    items: List<MetaPreview>,
    settings: TmdbSettings
): List<MetaPreview> = metaPreviewEnricher.enrich(items, settings, currentMdbListSettings)

internal fun HomeViewModel.replaceGridHeroItemsPipeline(
    gridItems: List<GridItem>,
    heroItems: List<MetaPreview>
): List<GridItem> {
    if (gridItems.isEmpty()) return gridItems
    return gridItems.map { item ->
        if (item is GridItem.Hero) {
            item.copy(items = heroItems)
        } else {
            item
        }
    }
}

internal fun HomeViewModel.heroEnrichmentSignaturePipeline(
    items: List<MetaPreview>,
    settings: TmdbSettings
): String {
    val itemSignature = items.joinToString(separator = "|") { item ->
        "${item.id}:${item.apiType}:${item.name}:${item.backdropUrl}:${item.logo}:${item.poster}"
    }
    return buildString {
        append(settings.enabled)
        append(':')
        append(settings.language)
        append(':')
        append(settings.useArtwork)
        append(':')
        append(settings.useBasicInfo)
        append(':')
        append(settings.useDetails)
        append("::")
        append(itemSignature)
    }
}
