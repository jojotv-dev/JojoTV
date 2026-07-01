package com.nuvio.tv.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.nuvio.tv.ui.screens.settings.FreeboxBrowserViewModel
import com.nuvio.tv.ui.screens.settings.LayoutSettingsViewModel
import com.nuvio.tv.ui.screens.home.ContinueWatchingItem
import com.nuvio.tv.data.freebox.FreeboxFileEntry
import com.nuvio.tv.data.freebox.freeboxContentIdForEntry
import com.nuvio.tv.data.freebox.freeboxPathFromContentId
import com.nuvio.tv.ui.components.freeboxposter.FreeboxPosterPickerViewModel
import com.nuvio.tv.ui.components.freeboxposter.FreeboxPosterPickerDialog
import com.nuvio.tv.ui.components.freeboxposter.FreeboxPosterPickerState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.core.tmdb.TmdbPosterCandidate
import com.nuvio.tv.domain.model.HomeLayout
import com.nuvio.tv.domain.model.LibraryListTab
import com.nuvio.tv.domain.model.LibrarySourceMode
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.ThumbnailOrientation
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.ui.components.ErrorState
import com.nuvio.tv.ui.components.LoadingIndicator
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.components.PosterCardDefaults
import com.nuvio.tv.ui.components.PosterCardStyle
import androidx.compose.ui.res.stringResource
import com.nuvio.tv.R
import com.nuvio.tv.data.local.StartupAuthNotice
import com.nuvio.tv.ui.theme.NuvioColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

private data class HomePosterOptionsTarget(
    val item: MetaPreview,
    val addonBaseUrl: String
)

private const val HOME_STABLE_GATE_TIMEOUT_MS = 1_000L

private fun ContinueWatchingItem.InProgress.toFreeboxEntry(uiState: HomeUiState): FreeboxFileEntry {
    val path = freeboxPathFromContentId(progress.contentId)
    val existing = uiState.freeboxVideoEntries.firstOrNull { entry ->
        entry.path == path || freeboxPathFromContentId(freeboxContentIdForEntry(entry)) == path
    }
    return existing ?: FreeboxFileEntry(
        name = progress.name.ifBlank { path.substringAfterLast('/') },
        path = path,
        encodedPath = null,
        isDirectory = false,
        size = null,
        durationMs = progress.duration
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    freeboxViewModel: FreeboxBrowserViewModel = hiltViewModel(),
    layoutSettingsViewModel: LayoutSettingsViewModel = hiltViewModel(),
    onNavigateToDetail: (String, String, String) -> Unit,
    onContinueWatchingClick: (ContinueWatchingItem) -> Unit = { item ->
        onNavigateToDetail(
            when (item) {
                is ContinueWatchingItem.InProgress -> item.progress.contentId
                is ContinueWatchingItem.NextUp -> item.info.contentId
            },
            when (item) {
                is ContinueWatchingItem.InProgress -> item.progress.contentType
                is ContinueWatchingItem.NextUp -> item.info.contentType
            },
            ""
        )
    },
    onContinueWatchingStartFromBeginning: (ContinueWatchingItem) -> Unit = onContinueWatchingClick,
    onContinueWatchingPlayManually: (ContinueWatchingItem) -> Unit = onContinueWatchingClick,
    onSearchContinueWatchingPoster: ((ContinueWatchingItem) -> Unit)? = null,
    onNavigateToCatalogSeeAll: (String, String, String) -> Unit = { _, _, _ -> },
    onNavigateToFolderDetail: (String, String) -> Unit = { _, _ -> },
    onNavigateToFreebox: (String, String?) -> Unit = { _, _ -> },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Rafraichit la liste des videos Freebox a chaque retour sur l'ecran d'accueil,
    // pour refleter les suppressions/ajouts effectues depuis un autre appareil.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadFreeboxVideos()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val layoutUiState by layoutSettingsViewModel.uiState.collectAsStateWithLifecycle()
    val initialCwResolved by viewModel.initialCwResolved.collectAsStateWithLifecycle()
    val scrollToTopTrigger by viewModel.scrollToTopTrigger.collectAsStateWithLifecycle()
    val effectiveAutoplayEnabled by viewModel.effectiveAutoplayEnabled.collectAsStateWithLifecycle(
        initialValue = false
    )
    val freeboxPosterPickerViewModel: FreeboxPosterPickerViewModel = hiltViewModel()
    val posterOverrides by viewModel.freeboxPosterOverrideDataStore.overrides.collectAsStateWithLifecycle(emptyMap())
    val backdropOverrides by viewModel.freeboxPosterOverrideDataStore.backdropOverrides.collectAsStateWithLifecycle(emptyMap())
    var iptvPosterPickerItem by remember { mutableStateOf<ContinueWatchingItem.InProgress?>(null) }
    var iptvCatalogPosterPickerTarget by remember { mutableStateOf<HomePosterOptionsTarget?>(null) }
    var iptvPosterCandidates by remember { mutableStateOf<List<TmdbPosterCandidate>>(emptyList()) }
    var iptvPosterPickerLoading by remember { mutableStateOf(false) }
    var iptvPosterPickerSaveFailed by remember { mutableStateOf(false) }
    val defaultSearchContinueWatchingPoster = remember(freeboxPosterPickerViewModel, uiState.freeboxVideoEntries, uiState.freeboxVideoArtwork) {
        { item: ContinueWatchingItem ->
            if (item is ContinueWatchingItem.InProgress && item.progress.contentId.startsWith("freebox:")) {
                val entry = item.toFreeboxEntry(uiState)
                freeboxPosterPickerViewModel.open(
                    entry = entry,
                    currentPosterUrl = uiState.freeboxVideoArtwork[item.progress.contentId] ?: item.progress.poster,
                    currentBackdropUrl = uiState.freeboxVideoBackdrops[item.progress.contentId] ?: item.progress.backdrop
                )
            } else if (item is ContinueWatchingItem.InProgress && item.progress.isIptvPosterEditable()) {
                iptvPosterPickerItem = item
            }
        }
    }
    val onSearchContinueWatchingPosterEffective = onSearchContinueWatchingPoster ?: defaultSearchContinueWatchingPoster
    val deleteFreeboxProgressFromHome = remember(viewModel, uiState.freeboxVideoEntries) {
        { item: ContinueWatchingItem ->
            if (item is ContinueWatchingItem.InProgress && item.progress.contentId.startsWith("freebox:")) {
                viewModel.deleteFreeboxVideoFromHome(item.toFreeboxEntry(uiState))
            }
        }
    }
    val deleteFreeboxVideoFromHome = remember(viewModel) {
        { entry: FreeboxFileEntry -> viewModel.deleteFreeboxVideoFromHome(entry) }
    }
    val hasCatalogContent = uiState.catalogRows.any { it.items.isNotEmpty() }
    val hasCollectionContent = uiState.homeRows.any { it is HomeRow.CollectionRow }
    val hasHeroContent = uiState.heroItems.isNotEmpty()
    val hasFreeboxContent = uiState.freeboxVideoEntries.isNotEmpty()
    val hasModernRowsContent = uiState.modernHomePresentation.rows.list.isNotEmpty()
    val canRenderHomeWithoutCatalogAddons =
        uiState.homeLayout == HomeLayout.MODERN ||
            uiState.installedAddonsCount == 0 ||
            hasFreeboxContent ||
            hasModernRowsContent
    val hasAnyContent = uiState.catalogRows.isNotEmpty() ||
        uiState.continueWatchingItems.isNotEmpty() ||
        uiState.heroItems.isNotEmpty() ||
        hasCollectionContent ||
        hasFreeboxContent ||
        hasModernRowsContent
    val modernPresentationReady =
        uiState.homeLayout != HomeLayout.MODERN ||
            hasModernRowsContent ||
            uiState.continueWatchingItems.isNotEmpty() ||
            hasFreeboxContent ||
            (uiState.heroSectionEnabled && hasHeroContent && !hasCatalogContent && !hasCollectionContent)
    var showHomeContentWithAnimation by rememberSaveable { mutableStateOf(false) }
    var hasShownInitialHomeContent by rememberSaveable { mutableStateOf(false) }
    // Once we've shown stable home content, never go back to loading gate.
    var homeStableGateReleased by rememberSaveable { mutableStateOf(false) }
    // Track that catalog loading has started at least once (isLoading went true â€” false)
    var catalogLoadingStarted by rememberSaveable { mutableStateOf(false) }
    var posterOptionsTarget by remember { mutableStateOf<HomePosterOptionsTarget?>(null) }

    LaunchedEffect(uiState.homeLayout) {
        if (uiState.homeLayout != HomeLayout.MODERN) {
            HeroBackdropState.update(null)
        }
    }

    LaunchedEffect(iptvPosterPickerItem, iptvCatalogPosterPickerTarget) {
        val item = iptvPosterPickerItem
        val catalogTarget = iptvCatalogPosterPickerTarget
        if (item == null && catalogTarget == null) {
            iptvPosterCandidates = emptyList()
            iptvPosterPickerLoading = false
            iptvPosterPickerSaveFailed = false
            return@LaunchedEffect
        }
        iptvPosterPickerLoading = true
        iptvPosterPickerSaveFailed = false
        val (framePosters, tmdbPosters) = coroutineScope {
            val frameDeferred = async(Dispatchers.IO) {
                runCatching {
                    when {
                        item != null -> viewModel.iptvFramePosterCandidates(item.progress)
                        catalogTarget != null -> viewModel.iptvFramePosterCandidates(catalogTarget.item)
                        else -> emptyList()
                    }
                }.getOrDefault(emptyList())
            }
            val tmdbDeferred = async(Dispatchers.IO) {
                runCatching {
                    viewModel.tmdbService.fetchPosterCandidatesForTitleQuery(
                        query = cleanHomePosterSearchQuery(item?.progress?.name ?: catalogTarget?.item?.name.orEmpty()),
                        mediaTypeHint = when {
                            item?.progress?.contentType.equals("iptv_series", ignoreCase = true) -> "tv"
                            catalogTarget?.item?.rawType == "iptv_series" -> "tv"
                            else -> "movie"
                        }
                    )
                }.getOrDefault(emptyList())
            }
            frameDeferred.await() to tmdbDeferred.await()
        }
        iptvPosterCandidates = framePosters + tmdbPosters
        iptvPosterPickerLoading = false
    }

    // Notify ViewModel of locale changes after activity recreation
    LaunchedEffect(Unit) {
        viewModel.notifyLocaleChanged()
    }

    val movieWatchedStatus = uiState.movieWatchedStatus
    // Use a stable lambda whose identity NEVER changes. The lambda captures
    // movieWatchedStatus via rememberUpdatedState so it always reads the latest
    // value without forcing downstream recomposition from lambda identity change.
    val latestMovieWatchedStatus = androidx.compose.runtime.rememberUpdatedState(movieWatchedStatus)
    val isCatalogItemWatched: (MetaPreview) -> Boolean = remember {
        { item: MetaPreview ->
            if (item.rawType == "iptv_movie" || item.rawType == "iptv_series") {
                item.isFavorite
            } else {
                latestMovieWatchedStatus.value[homeItemStatusKey(item.id, item.apiType)] == true
            }
        }
    }
    val onCatalogItemLongPress: (MetaPreview, String) -> Unit = remember {
        { item, addonBaseUrl -> posterOptionsTarget = HomePosterOptionsTarget(item, addonBaseUrl) }
    }

    val onNavigateToDetailStable = remember(onNavigateToDetail) { onNavigateToDetail }
    val onContinueWatchingClickStable = remember(onContinueWatchingClick) { onContinueWatchingClick }
    val onContinueWatchingDetailsStable = remember(viewModel, onNavigateToDetailStable) {
        { item: ContinueWatchingItem ->
            viewModel.openContinueWatchingDetails(item, onNavigateToDetailStable)
        }
    }
    val onContinueWatchingStartFromBeginningStable = remember(onContinueWatchingStartFromBeginning) { onContinueWatchingStartFromBeginning }
    val onContinueWatchingPlayManuallyStable = remember(onContinueWatchingPlayManually) { onContinueWatchingPlayManually }
    val onNavigateToCatalogSeeAllStable = remember(onNavigateToCatalogSeeAll) { onNavigateToCatalogSeeAll }
    val onNavigateToFolderDetailStable = remember(onNavigateToFolderDetail) { onNavigateToFolderDetail }
    val onRemoveContinueWatchingStable = remember(viewModel) {
        { contentId: String, season: Int?, episode: Int?, isNextUp: Boolean ->
            viewModel.onEvent(HomeEvent.OnRemoveContinueWatching(contentId, season, episode, isNextUp))
        }
    }

    LaunchedEffect(
        uiState.isLoading,
        hasCatalogContent,
        hasCollectionContent,
        hasHeroContent,
        initialCwResolved,
        modernPresentationReady
    ) {
        // Track that addons are known (even if isLoading flipped too fast to catch).
        if (uiState.installedAddonsCount > 0) {
            catalogLoadingStarted = true
        }
        // As soon as there is something real to draw, reveal Home and let the
        // remaining rows update progressively instead of blocking behind a full-screen spinner.
        if (!homeStableGateReleased &&
            uiState.layoutPreferencesReady &&
            modernPresentationReady &&
            (hasAnyContent || (!uiState.isLoading && initialCwResolved && (hasCatalogContent || uiState.installedAddonsCount == 0)))
        ) {
            Log.d("HomeGate", "RELEASE: anyContent=$hasAnyContent catalogs=$hasCatalogContent cwResolved=$initialCwResolved cwItems=${uiState.continueWatchingItems.size} addons=${uiState.installedAddonsCount}")
            homeStableGateReleased = true
        }
    }

    LaunchedEffect(Unit) {
        // Safety timeout â€”
        // window, show whatever is available.  Covers edge cases like
        // clean cache (addons loading from remote sync) and users with
        // no addons at all.
        delay(HOME_STABLE_GATE_TIMEOUT_MS)
        if (!homeStableGateReleased) {
            Log.d("HomeGate", "RELEASE timeout: isLoading=${uiState.isLoading} cwResolved=$initialCwResolved catalogs=$hasCatalogContent cwItems=${uiState.continueWatchingItems.size}")
            homeStableGateReleased = true
        }
    }

    val posterCardStyle = remember(
        uiState.posterCardWidthDp,
        uiState.posterCardCornerRadiusDp
    ) {
        val computedHeightDp = (uiState.posterCardWidthDp * 1.5f).roundToInt()
        PosterCardStyle(
            width = uiState.posterCardWidthDp.dp,
            height = computedHeightDp.dp,
            cornerRadius = uiState.posterCardCornerRadiusDp.dp,
            focusedBorderWidth = PosterCardDefaults.Style.focusedBorderWidth,
            focusedScale = PosterCardDefaults.Style.focusedScale
        )
    }

    val noAddonsError = stringResource(R.string.home_error_no_addons)
    val noCatalogAddonsError = stringResource(R.string.home_error_no_catalog_addons)
    val blockingHomeError = uiState.error?.takeUnless { error ->
        error == noAddonsError || error == noCatalogAddonsError
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            !uiState.layoutPreferencesReady -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }

            uiState.isLoading && !hasAnyContent && !canRenderHomeWithoutCatalogAddons -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }

            blockingHomeError != null && uiState.catalogRows.isEmpty() -> {
                ErrorState(
                    message = blockingHomeError,
                    onRetry = { viewModel.onEvent(HomeEvent.OnRetry) }
                )
            }

            !uiState.isLoading && !hasAnyContent && !canRenderHomeWithoutCatalogAddons -> {
                // Don't show "no catalogs" until the stable gate has released â€”
                // addons may still be loading from remote after a cache clear.
                if (!homeStableGateReleased) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.web_no_catalogs),
                            style = MaterialTheme.typography.bodyLarge,
                            color = NuvioColors.TextSecondary
                        )
                    }
                }
            }

            else -> {
                // On first launch, wait for stable content before revealing home.
                // Once released, never go back to loading (homeStableGateReleased is rememberSaveable).
                if (!homeStableGateReleased) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                } else if (!modernPresentationReady) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                } else {
                    // Flip showHomeContentWithAnimation on the next frame so
                    // AnimatedVisibility can run its enter transition.
                    LaunchedEffect(Unit) {
                        if (!showHomeContentWithAnimation) {
                            kotlinx.coroutines.yield()
                            showHomeContentWithAnimation = true
                        }
                    }
                    LaunchedEffect(showHomeContentWithAnimation) {
                        if (showHomeContentWithAnimation) {
                            hasShownInitialHomeContent = true
                        }
                    }
                    // Keep loading visible during the single-frame gap before animation starts.
                    if (!showHomeContentWithAnimation) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator()
                        }
                    }
                    AnimatedVisibility(
                        visible = showHomeContentWithAnimation,
                        enter = if (hasShownInitialHomeContent) {
                            EnterTransition.None
                        } else {
                            fadeIn(animationSpec = tween(320)) +
                                slideInVertically(
                                    initialOffsetY = { it / 24 },
                                    animationSpec = tween(320)
                                )
                        }
                    ) {
                        when (uiState.homeLayout) {
                            HomeLayout.CLASSIC -> ClassicHomeRoute(
                                viewModel = viewModel,
                                freeboxViewModel = freeboxViewModel,
                                uiState = uiState,
                                continueWatchingPortraitMode =
                                    layoutUiState.continueWatchingThumbnailOrientation ==
                                        ThumbnailOrientation.PORTRAIT,
                                videoPortraitMode =
                                    layoutUiState.videoThumbnailOrientation ==
                                        ThumbnailOrientation.PORTRAIT,
                                movieFavoritesPortraitMode =
                                    layoutUiState.movieFavoritesThumbnailOrientation ==
                                        ThumbnailOrientation.PORTRAIT,
                                seriesFavoritesPortraitMode =
                                    layoutUiState.seriesFavoritesThumbnailOrientation ==
                                        ThumbnailOrientation.PORTRAIT,
                                posterCardStyle = posterCardStyle,
                                onNavigateToDetail = onNavigateToDetailStable,
                                onContinueWatchingClick = onContinueWatchingClickStable,
                                onContinueWatchingDetails = onContinueWatchingDetailsStable,
                                onContinueWatchingStartFromBeginning = onContinueWatchingStartFromBeginningStable,
                                onContinueWatchingPlayManually = onContinueWatchingPlayManuallyStable,
                                onSearchContinueWatchingPoster = onSearchContinueWatchingPosterEffective,
                                showContinueWatchingManualPlayOption = effectiveAutoplayEnabled,
                                onNavigateToCatalogSeeAll = onNavigateToCatalogSeeAllStable,
                                onNavigateToFolderDetail = onNavigateToFolderDetailStable,
                                isCatalogItemWatched = isCatalogItemWatched,
                                onCatalogItemLongPress = onCatalogItemLongPress,
                                onDeleteFreeboxProgress = deleteFreeboxProgressFromHome,
                                onDeleteFreeboxVideo = deleteFreeboxVideoFromHome,
                                onNavigateToFreebox = onNavigateToFreebox,
                            )

                            HomeLayout.GRID -> GridHomeRoute(
                                viewModel = viewModel,
                                uiState = uiState,
                                posterCardStyle = posterCardStyle,
                                onNavigateToDetail = onNavigateToDetailStable,
                                onContinueWatchingClick = onContinueWatchingClickStable,
                                onContinueWatchingDetails = onContinueWatchingDetailsStable,
                                onContinueWatchingStartFromBeginning = onContinueWatchingStartFromBeginningStable,
                                onContinueWatchingPlayManually = onContinueWatchingPlayManuallyStable,
                                onSearchContinueWatchingPoster = onSearchContinueWatchingPosterEffective,
                                showContinueWatchingManualPlayOption = effectiveAutoplayEnabled,
                                onNavigateToCatalogSeeAll = onNavigateToCatalogSeeAllStable,
                                onNavigateToFolderDetail = onNavigateToFolderDetailStable,
                                isCatalogItemWatched = isCatalogItemWatched,
                                onCatalogItemLongPress = onCatalogItemLongPress,
                                onDeleteFreeboxProgress = deleteFreeboxProgressFromHome,
                                onDeleteFreeboxVideo = deleteFreeboxVideoFromHome,
                                continueWatchingPortraitMode =
                                    layoutUiState.continueWatchingThumbnailOrientation ==
                                        ThumbnailOrientation.PORTRAIT,
                                videoPortraitMode =
                                    layoutUiState.videoThumbnailOrientation ==
                                        ThumbnailOrientation.PORTRAIT,
                                movieFavoritesPortraitMode =
                                    layoutUiState.movieFavoritesThumbnailOrientation ==
                                        ThumbnailOrientation.PORTRAIT,
                                seriesFavoritesPortraitMode =
                                    layoutUiState.seriesFavoritesThumbnailOrientation ==
                                        ThumbnailOrientation.PORTRAIT,
                                continueWatchingThumbnailSize = com.nuvio.tv.domain.model.ThumbnailSize.fromName(uiState.continueWatchingThumbnailSize.name),
                                onNavigateToFreebox = onNavigateToFreebox,
                            )
                              HomeLayout.MODERN -> ModernHomeRoute(
                                  viewModel = viewModel,
                                  uiState = uiState,
                                  onNavigateToDetail = onNavigateToDetailStable,
                                  onContinueWatchingClick = onContinueWatchingClickStable,
                                  onContinueWatchingDetails = onContinueWatchingDetailsStable,
                                  onContinueWatchingStartFromBeginning = onContinueWatchingStartFromBeginningStable,
                                  onContinueWatchingPlayManually = onContinueWatchingPlayManuallyStable,
                                  showContinueWatchingManualPlayOption = effectiveAutoplayEnabled,
                                  onSearchContinueWatchingPoster = onSearchContinueWatchingPosterEffective,
                                  onNavigateToFolderDetail = onNavigateToFolderDetailStable,
                                  isCatalogItemWatched = isCatalogItemWatched,
                                  onCatalogItemLongPress = onCatalogItemLongPress,
                                  onDeleteFreeboxProgress = deleteFreeboxProgressFromHome,
                                  onDeleteFreeboxVideo = deleteFreeboxVideoFromHome,
                                  continueWatchingPortraitMode =
                                      layoutUiState.continueWatchingThumbnailOrientation ==
                                          ThumbnailOrientation.PORTRAIT,
                                  videoPortraitMode =
                                      layoutUiState.videoThumbnailOrientation ==
                                          ThumbnailOrientation.PORTRAIT,
                                  movieFavoritesPortraitMode =
                                      layoutUiState.movieFavoritesThumbnailOrientation ==
                                          ThumbnailOrientation.PORTRAIT,
                                  seriesFavoritesPortraitMode =
                                      layoutUiState.seriesFavoritesThumbnailOrientation ==
                                          ThumbnailOrientation.PORTRAIT,
                                  continueWatchingThumbnailSize = uiState.continueWatchingThumbnailSize,
                                  onNavigateToFreebox = onNavigateToFreebox,
                              )
                        }
                    }
                }
            }
        }

        val startupAuthNotice = uiState.startupAuthNotice
        if (startupAuthNotice != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
                    .background(
                        color = Color(0xFF5A1C1C),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    text = when (startupAuthNotice) {
                        StartupAuthNotice.NUVIO -> stringResource(R.string.auth_notice_nuvio_logged_out)
                        StartupAuthNotice.TRAKT -> stringResource(R.string.auth_notice_trakt_logged_out)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = NuvioColors.TextPrimary
                )
            }
        }
    }

    val selectedPoster = posterOptionsTarget
    if (selectedPoster != null) {
        val item = selectedPoster.item
        val statusKey = homeItemStatusKey(item.id, item.apiType)
        val isIptvContent = item.rawType == "iptv_movie" || item.rawType == "iptv_series"
        val isIptvFavorite = isIptvContent && item.isFavorite
        val isMovie = item.apiType.equals("movie", ignoreCase = true)
        val isSeries = item.apiType.equals("series", ignoreCase = true) ||
            item.apiType.equals("tv", ignoreCase = true) ||
            item.apiType.equals("anime", ignoreCase = true)
        HomePosterOptionsDialog(
            title = item.name,
            isInLibrary = uiState.posterLibraryMembership[statusKey] == true,
            isLibraryPending = statusKey in uiState.posterLibraryPending,
            showManageLists = uiState.librarySourceMode == LibrarySourceMode.TRAKT,
            isMovie = isMovie,
            isSeries = isSeries,
            isWatched = movieWatchedStatus[statusKey] == true,
            isWatchedPending = statusKey in uiState.movieWatchedPending,
            isIptvContent = isIptvContent,
            isIptvFavorite = isIptvFavorite,
            onDismiss = { posterOptionsTarget = null },
            onDetails = {
                onNavigateToDetail(item.id, item.apiType, selectedPoster.addonBaseUrl)
                posterOptionsTarget = null
            },
            onRemoveIptvFavorite = {
                viewModel.removeIptvFavorite(item)
                posterOptionsTarget = null
            },
            onAddIptvFavorite = {
                viewModel.addIptvFavorite(item)
                posterOptionsTarget = null
            },
            onToggleLibrary = {
                if (uiState.librarySourceMode == LibrarySourceMode.TRAKT) {
                    viewModel.openPosterListPicker(item, selectedPoster.addonBaseUrl)
                } else {
                    viewModel.togglePosterLibrary(item, selectedPoster.addonBaseUrl)
                }
                posterOptionsTarget = null
            },
            onToggleWatched = {
                if (isMovie) {
                    viewModel.togglePosterMovieWatched(item)
                } else {
                    viewModel.togglePosterSeriesWatched(item)
                }
                posterOptionsTarget = null
            },
            onSearchPoster = if (isIptvContent) {
                {
                    iptvCatalogPosterPickerTarget = selectedPoster
                    posterOptionsTarget = null
                }
            } else {
                null
            }
        )
    }

    if (uiState.showPosterListPicker) {
        HomeLibraryListPickerDialog(
            title = uiState.posterListPickerTitle ?: stringResource(R.string.detail_lists_fallback),
            tabs = uiState.libraryListTabs,
            membership = uiState.posterListPickerMembership,
            isPending = uiState.posterListPickerPending,
            error = uiState.posterListPickerError,
            onToggle = { key -> viewModel.togglePosterListPickerMembership(key) },
            onSave = { viewModel.savePosterListPickerMembership() },
            onDismiss = { viewModel.dismissPosterListPicker() }
        )
    }

    val selectedIptvPosterItem = iptvPosterPickerItem
    val selectedIptvCatalogPosterTarget = iptvCatalogPosterPickerTarget
    if (selectedIptvPosterItem != null || selectedIptvCatalogPosterTarget != null) {
        val dialogTitle = selectedIptvPosterItem?.progress?.name
            ?: selectedIptvCatalogPosterTarget?.item?.name
            ?: ""
        val dialogContentId = selectedIptvPosterItem?.progress?.contentId
            ?: selectedIptvCatalogPosterTarget?.item?.id
            ?: ""
        val dialogPoster = selectedIptvPosterItem?.progress?.poster
            ?: posterOverrides[dialogContentId]
            ?: selectedIptvCatalogPosterTarget?.item?.poster
        val dialogBackdrop = selectedIptvPosterItem?.progress?.backdrop
            ?: backdropOverrides[dialogContentId]
            ?: selectedIptvCatalogPosterTarget?.item?.background
            ?: selectedIptvCatalogPosterTarget?.item?.landscapePoster
        FreeboxPosterPickerDialog(
            state = FreeboxPosterPickerState(
                entry = FreeboxFileEntry(
                    name = dialogTitle,
                    path = dialogContentId,
                    encodedPath = null,
                    isDirectory = false,
                    size = null,
                    durationMs = selectedIptvPosterItem?.progress?.duration
                ),
                posters = iptvPosterCandidates,
                currentPosterUrl = dialogPoster,
                currentBackdropUrl = dialogBackdrop,
                isLoading = iptvPosterPickerLoading,
                isSaving = false,
                saveFailed = iptvPosterPickerSaveFailed
            ),
            onSelect = { poster ->
                iptvPosterPickerSaveFailed = false
                if (selectedIptvPosterItem != null) {
                    viewModel.updateIptvContinueWatchingPoster(selectedIptvPosterItem, poster)
                    iptvPosterPickerItem = null
                } else if (selectedIptvCatalogPosterTarget != null) {
                    viewModel.updateIptvFavoriteArtwork(selectedIptvCatalogPosterTarget.item, poster)
                    iptvCatalogPosterPickerTarget = null
                }
            },
            onDismiss = {
                iptvPosterPickerItem = null
                iptvCatalogPosterPickerTarget = null
            }
        )
    }
}

private fun WatchProgress.isIptvPosterEditable(): Boolean =
    contentId.startsWith("iptv_movie:", ignoreCase = true) ||
        contentId.startsWith("iptv_movie_stream:", ignoreCase = true) ||
        contentId.startsWith("iptv_series:", ignoreCase = true) ||
        contentId.startsWith("iptv_series_remote:", ignoreCase = true) ||
        contentType.equals("iptv_movie", ignoreCase = true) ||
        contentType.equals("iptv_series", ignoreCase = true)

private fun cleanHomePosterSearchQuery(rawTitle: String): String {
    return rawTitle
        .replace(Regex("""\[[^\]]*]"""), " ")
        .replace(Regex("""\((?:19|20)\d{2}\)"""), " ")
        .replace(
            Regex(
                """\b(?:4k|uhd|hdr10\+?|hdr|dv|dolby\s*vision|hevc|x265|x264|h\.?264|h\.?265|multi|vostfr|vf|truefrench|french|web-?dl|bluray|brrip|webrip|hdtv|aac|ac3|eac3|atmos|dts)\b""",
                RegexOption.IGNORE_CASE
            ),
            " "
        )
        .replace(Regex("""\s+"""), " ")
        .trim()
        .ifBlank { rawTitle.trim() }
}

@Composable
private fun ClassicHomeRoute(
    viewModel: HomeViewModel,
    freeboxViewModel: FreeboxBrowserViewModel,
    uiState: HomeUiState,
    posterCardStyle: PosterCardStyle,
    onNavigateToDetail: (String, String, String) -> Unit,
    onContinueWatchingClick: (ContinueWatchingItem) -> Unit,
    onContinueWatchingDetails: (ContinueWatchingItem) -> Unit,
    onContinueWatchingStartFromBeginning: (ContinueWatchingItem) -> Unit,
    onContinueWatchingPlayManually: (ContinueWatchingItem) -> Unit,
    onSearchContinueWatchingPoster: ((ContinueWatchingItem) -> Unit)? = null,
    showContinueWatchingManualPlayOption: Boolean,
    onNavigateToCatalogSeeAll: (String, String, String) -> Unit,
    onNavigateToFolderDetail: (String, String) -> Unit = { _, _ -> },
    isCatalogItemWatched: (MetaPreview) -> Boolean,
    onCatalogItemLongPress: (MetaPreview, String) -> Unit,
    onDeleteFreeboxProgress: ((ContinueWatchingItem) -> Unit)? = null,
    onDeleteFreeboxVideo: (FreeboxFileEntry) -> Unit = {},
    onNavigateToFreebox: (String, String?) -> Unit = { _, _ -> },
    continueWatchingPortraitMode: Boolean = false,
    videoPortraitMode: Boolean = false,
    movieFavoritesPortraitMode: Boolean = true,
    seriesFavoritesPortraitMode: Boolean = true,
    continueWatchingThumbnailSize: com.nuvio.tv.domain.model.ThumbnailSize = com.nuvio.tv.domain.model.ThumbnailSize.DEFAULT
) {
    val focusState by viewModel.focusState.collectAsStateWithLifecycle()
    val scrollToTopTrigger by viewModel.scrollToTopTrigger.collectAsStateWithLifecycle()
    ClassicHomeContent(
        uiState = uiState,
        posterCardStyle = posterCardStyle,
        focusState = focusState,
        scrollToTopTrigger = scrollToTopTrigger,
        trailerPreviewUrls = viewModel.trailerPreviewUrls,
        trailerPreviewAudioUrls = viewModel.trailerPreviewAudioUrls,
        onNavigateToDetail = onNavigateToDetail,
        onContinueWatchingClick = onContinueWatchingClick,
        onContinueWatchingDetails = onContinueWatchingDetails,
        onContinueWatchingStartFromBeginning = onContinueWatchingStartFromBeginning,
        onContinueWatchingPlayManually = onContinueWatchingPlayManually,
        onSearchContinueWatchingPoster = onSearchContinueWatchingPoster,
        showContinueWatchingManualPlayOption = showContinueWatchingManualPlayOption,
        onNavigateToCatalogSeeAll = onNavigateToCatalogSeeAll,
        onNavigateToFolderDetail = onNavigateToFolderDetail,
        onRemoveContinueWatching = { contentId, season, episode, isNextUp ->
            viewModel.onEvent(HomeEvent.OnRemoveContinueWatching(contentId, season, episode, isNextUp))
        },
        isCatalogItemWatched = isCatalogItemWatched,
        onCatalogItemLongPress = onCatalogItemLongPress,
        onRequestTrailerPreview = { item ->
            viewModel.requestTrailerPreview(item)
        },
        onItemFocus = { item ->
            viewModel.onItemFocus(item)
        },
        onSaveFocusState = { vi, vo, rk, ikm, m, ri, ii ->
            viewModel.saveFocusState(vi, vo, rk, ikm, m, ri, ii)
        },
        onRequestLazyCatalogLoad = remember(viewModel) {
            { catalogKey: String -> viewModel.requestLazyCatalogLoad(catalogKey) }
        },
        continueWatchingPortraitMode = continueWatchingPortraitMode,
        videoPortraitMode = videoPortraitMode,
        movieFavoritesPortraitMode = movieFavoritesPortraitMode,
        seriesFavoritesPortraitMode = seriesFavoritesPortraitMode,
        continueWatchingThumbnailSize = continueWatchingThumbnailSize,
        onRenameFreeboxProgress = { item, newName -> viewModel.renameFreeboxContinueWatching(item, newName) },
        onMarkFreeboxUnwatched = { item -> viewModel.markFreeboxAsUnwatched(item) },
        onRenameFreeboxVideo = { entry, newName -> viewModel.renameFreeboxVideo(entry, newName) },
        onDeleteFreeboxVideo = onDeleteFreeboxVideo,
        onNavigateToFreebox = onNavigateToFreebox,
    )
}

@Composable
private fun GridHomeRoute(
    viewModel: HomeViewModel,
    uiState: HomeUiState,
    posterCardStyle: PosterCardStyle,
    onNavigateToDetail: (String, String, String) -> Unit,
    onContinueWatchingClick: (ContinueWatchingItem) -> Unit,
    onContinueWatchingDetails: (ContinueWatchingItem) -> Unit,
    onContinueWatchingStartFromBeginning: (ContinueWatchingItem) -> Unit,
    onContinueWatchingPlayManually: (ContinueWatchingItem) -> Unit,
    showContinueWatchingManualPlayOption: Boolean,
    onSearchContinueWatchingPoster: ((ContinueWatchingItem) -> Unit)? = null,
    onNavigateToCatalogSeeAll: (String, String, String) -> Unit,
    onNavigateToFolderDetail: (String, String) -> Unit = { _, _ -> },
    isCatalogItemWatched: (MetaPreview) -> Boolean,
    onCatalogItemLongPress: (MetaPreview, String) -> Unit,
    onDeleteFreeboxProgress: ((ContinueWatchingItem) -> Unit)? = null,
    onDeleteFreeboxVideo: (FreeboxFileEntry) -> Unit = {},
    onNavigateToFreebox: (String, String?) -> Unit = { _, _ -> },
    continueWatchingThumbnailSize: com.nuvio.tv.domain.model.ThumbnailSize = com.nuvio.tv.domain.model.ThumbnailSize.DEFAULT,
    continueWatchingPortraitMode: Boolean = false,
    videoPortraitMode: Boolean = false,
    movieFavoritesPortraitMode: Boolean = true,
    seriesFavoritesPortraitMode: Boolean = true
) {
    val gridFocusState by viewModel.gridFocusState.collectAsStateWithLifecycle()
    val scrollToTopTrigger by viewModel.scrollToTopTrigger.collectAsStateWithLifecycle()
    GridHomeContent(
        uiState = uiState,
        posterCardStyle = posterCardStyle,
        gridFocusState = gridFocusState,
        scrollToTopTrigger = scrollToTopTrigger,
        onNavigateToDetail = onNavigateToDetail,
        onContinueWatchingClick = onContinueWatchingClick,
        onContinueWatchingDetails = onContinueWatchingDetails,
        onContinueWatchingStartFromBeginning = onContinueWatchingStartFromBeginning,
        onContinueWatchingPlayManually = onContinueWatchingPlayManually,
        showContinueWatchingManualPlayOption = showContinueWatchingManualPlayOption,
        onSearchContinueWatchingPoster = onSearchContinueWatchingPoster,
        onNavigateToCatalogSeeAll = onNavigateToCatalogSeeAll,
        onNavigateToFolderDetail = onNavigateToFolderDetail,
        onRemoveContinueWatching = remember(viewModel) {
            { contentId, season, episode, isNextUp ->
                viewModel.onEvent(HomeEvent.OnRemoveContinueWatching(contentId, season, episode, isNextUp))
            }
        },
        isCatalogItemWatched = isCatalogItemWatched,
        onCatalogItemLongPress = onCatalogItemLongPress,
            onDeleteFromFreebox = onDeleteFreeboxProgress,
            continueWatchingThumbnailSize = continueWatchingThumbnailSize,
            continueWatchingPortraitMode = continueWatchingPortraitMode,
            videoPortraitMode = videoPortraitMode,
        movieFavoritesPortraitMode = movieFavoritesPortraitMode,
        seriesFavoritesPortraitMode = seriesFavoritesPortraitMode,
            onItemFocus = remember(viewModel) {
            { item ->
                viewModel.onItemFocus(item)
            }
        },
        onSaveGridFocusState = remember(viewModel) {
            { vi, vo, key ->
                viewModel.saveGridFocusState(vi, vo, focusedItemKey = key)
            }
        },
        onRenameFreeboxProgress = { item, newName -> viewModel.renameFreeboxContinueWatching(item, newName) },
        onMarkFreeboxUnwatched = { item -> viewModel.markFreeboxAsUnwatched(item) },
        onRenameFreeboxVideo = { entry, newName -> viewModel.renameFreeboxVideo(entry, newName) },
        onDeleteFreeboxVideo = onDeleteFreeboxVideo,
        onNavigateToFreebox = onNavigateToFreebox,
    )
}

@Composable
private fun ModernHomeRoute(
    viewModel: HomeViewModel,
    uiState: HomeUiState,
    onNavigateToDetail: (String, String, String) -> Unit,
    onContinueWatchingClick: (ContinueWatchingItem) -> Unit,
    onContinueWatchingDetails: (ContinueWatchingItem) -> Unit,
    onContinueWatchingStartFromBeginning: (ContinueWatchingItem) -> Unit,
    onContinueWatchingPlayManually: (ContinueWatchingItem) -> Unit,
    showContinueWatchingManualPlayOption: Boolean,
    onSearchContinueWatchingPoster: ((ContinueWatchingItem) -> Unit)? = null,
    onNavigateToFolderDetail: (String, String) -> Unit = { _, _ -> },
    isCatalogItemWatched: (MetaPreview) -> Boolean,
    onCatalogItemLongPress: (MetaPreview, String) -> Unit,
    onDeleteFreeboxProgress: ((ContinueWatchingItem) -> Unit)? = null,
    onDeleteFreeboxVideo: (FreeboxFileEntry) -> Unit = {},
    onNavigateToFreebox: (String, String?) -> Unit = { _, _ -> },
    continueWatchingPortraitMode: Boolean = false,
    videoPortraitMode: Boolean = false,
    movieFavoritesPortraitMode: Boolean = true,
    seriesFavoritesPortraitMode: Boolean = true,
    continueWatchingThumbnailSize: com.nuvio.tv.domain.model.ThumbnailSize = com.nuvio.tv.domain.model.ThumbnailSize.DEFAULT
) {
    val focusState by viewModel.focusState.collectAsStateWithLifecycle()
    val scrollToTopTrigger by viewModel.scrollToTopTrigger.collectAsStateWithLifecycle()
    val enrichingItemId by viewModel.enrichingItemId.collectAsStateWithLifecycle()
    val lastEnrichedPreview by viewModel.lastEnrichedPreview.collectAsStateWithLifecycle()
    val enrichedPreviews by viewModel.enrichedPreviews.collectAsStateWithLifecycle()
    val failedEnrichmentIds by viewModel.failedEnrichmentIds.collectAsStateWithLifecycle()
    val requestTrailerPreview = remember(viewModel) {
        { itemId: String, title: String, releaseInfo: String?, apiType: String ->
            viewModel.requestTrailerPreview(itemId, title, releaseInfo, apiType)
        }
    }
    val loadMoreCatalog = remember(viewModel) {
        { catalogId: String, addonId: String, type: String ->
            viewModel.onEvent(HomeEvent.OnLoadMoreCatalog(catalogId, addonId, type))
        }
    }
    val removeContinueWatching = remember(viewModel) {
        { contentId: String, season: Int?, episode: Int?, isNextUp: Boolean ->
            viewModel.onEvent(HomeEvent.OnRemoveContinueWatching(contentId, season, episode, isNextUp))
        }
    }
    val saveModernFocusState = remember(viewModel) {
        { vi: Int, vo: Int, rk: String?, ikm: Map<String, String>, m: Map<String, Int>, ri: Int, ii: Int ->
            viewModel.saveFocusState(vi, vo, rk, ikm, m, ri, ii)
        }
    }
    val preloadAdjacentItem = remember(viewModel) {
        { item: MetaPreview ->
            viewModel.preloadAdjacentItem(item)
        }
    }
    ModernHomeContent(
        uiState = uiState,
        focusState = focusState,
        scrollToTopTrigger = scrollToTopTrigger,
        enrichingItemId = enrichingItemId,
        lastEnrichedPreview = lastEnrichedPreview,
        enrichedPreviews = enrichedPreviews,
        failedEnrichmentIds = failedEnrichmentIds,
        trailerPreviewUrls = viewModel.trailerPreviewUrls,
        trailerPreviewAudioUrls = viewModel.trailerPreviewAudioUrls,
        onNavigateToDetail = onNavigateToDetail,
        onContinueWatchingClick = onContinueWatchingClick,
        onContinueWatchingDetails = onContinueWatchingDetails,
        onContinueWatchingStartFromBeginning = onContinueWatchingStartFromBeginning,
        onContinueWatchingPlayManually = onContinueWatchingPlayManually,
        showContinueWatchingManualPlayOption = showContinueWatchingManualPlayOption,
        onSearchContinueWatchingPoster = onSearchContinueWatchingPoster,
        onRequestTrailerPreview = requestTrailerPreview,
        onLoadMoreCatalog = loadMoreCatalog,
        onRemoveContinueWatching = removeContinueWatching,
        isCatalogItemWatched = isCatalogItemWatched,
        onCatalogItemLongPress = onCatalogItemLongPress,
        onNavigateToFolderDetail = onNavigateToFolderDetail,
        onItemFocus = remember(viewModel) {
            { item -> viewModel.onItemFocus(item) }
        },
        onPreloadAdjacentItem = preloadAdjacentItem,
        onSaveFocusState = saveModernFocusState,
        continueWatchingPortraitMode = continueWatchingPortraitMode,
        videoPortraitMode = videoPortraitMode,
        movieFavoritesPortraitMode = movieFavoritesPortraitMode,
        seriesFavoritesPortraitMode = seriesFavoritesPortraitMode,
        continueWatchingThumbnailSize = continueWatchingThumbnailSize,
        onDeleteFreeboxProgress = onDeleteFreeboxProgress,
        onRenameFreeboxProgress = { item, newName -> viewModel.renameFreeboxContinueWatching(item, newName) },
        onMarkFreeboxUnwatched = { item -> viewModel.markFreeboxAsUnwatched(item) },
        onRenameFreeboxVideo = { entry, newName -> viewModel.renameFreeboxVideo(entry, newName) },
        onDeleteFreeboxVideo = onDeleteFreeboxVideo,
        onNavigateToFreebox = onNavigateToFreebox,
        onRequestLazyCatalogLoad = remember(viewModel) {
            { catalogKey: String -> viewModel.requestLazyCatalogLoad(catalogKey) }
        }
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun HomePosterOptionsDialog(
    title: String,
    isInLibrary: Boolean,
    isLibraryPending: Boolean,
    showManageLists: Boolean,
    isMovie: Boolean,
    isSeries: Boolean = false,
    isWatched: Boolean,
    isWatchedPending: Boolean,
    isIptvContent: Boolean,
    isIptvFavorite: Boolean,
    onDismiss: () -> Unit,
    onDetails: () -> Unit,
    onRemoveIptvFavorite: () -> Unit,
    onAddIptvFavorite: () -> Unit,
    onToggleLibrary: () -> Unit,
    onToggleWatched: () -> Unit,
    onSearchPoster: (() -> Unit)? = null
) {
    val primaryFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        primaryFocusRequester.requestFocus()
    }

    NuvioDialog(
        onDismiss = onDismiss,
        title = title,
        subtitle = stringResource(R.string.home_poster_dialog_subtitle)
    ) {
        Button(
            onClick = onDetails,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(primaryFocusRequester),
            colors = ButtonDefaults.colors(
                containerColor = NuvioColors.BackgroundCard,
                contentColor = NuvioColors.TextPrimary
            )
        ) {
            Text(stringResource(R.string.cw_action_go_to_details))
        }

        if (isIptvContent) {
            Button(
                onClick = if (isIptvFavorite) onRemoveIptvFavorite else onAddIptvFavorite,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.colors(
                    containerColor = NuvioColors.BackgroundCard,
                    contentColor = NuvioColors.TextPrimary
                )
            ) {
                Text(
                    stringResource(
                        if (isIptvFavorite) {
                            R.string.home_remove_from_favorites
                        } else {
                            R.string.home_add_to_favorites
                        }
                    )
                )
            }
            onSearchPoster?.let { searchPoster ->
                Button(
                    onClick = searchPoster,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.colors(
                        containerColor = NuvioColors.BackgroundCard,
                        contentColor = NuvioColors.TextPrimary
                    )
                ) {
                    Text(stringResource(R.string.freebox_choose_poster))
                }
            }
        } else {
            Button(
                onClick = onToggleLibrary,
                enabled = !isLibraryPending,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.colors(
                    containerColor = NuvioColors.BackgroundCard,
                    contentColor = NuvioColors.TextPrimary
                )
            ) {
                Text(
                    if (showManageLists) {
                        stringResource(R.string.library_manage_lists)
                    } else {
                        if (isInLibrary) {
                            stringResource(R.string.hero_remove_from_library)
                        } else {
                            stringResource(R.string.hero_add_to_library)
                        }
                    }
                )
            }

            }

        if (!isIptvContent && (isMovie || isSeries)) {
            Button(
                onClick = onToggleWatched,
                enabled = !isWatchedPending,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.colors(
                    containerColor = NuvioColors.BackgroundCard,
                    contentColor = NuvioColors.TextPrimary
                )
            ) {
                Text(
                    if (isWatched) {
                        stringResource(R.string.hero_mark_unwatched)
                    } else {
                        stringResource(R.string.hero_mark_watched)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun HomeLibraryListPickerDialog(
    title: String,
    tabs: List<LibraryListTab>,
    membership: Map<String, Boolean>,
    isPending: Boolean,
    error: String?,
    onToggle: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val primaryFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        primaryFocusRequester.requestFocus()
    }

    NuvioDialog(
        onDismiss = onDismiss,
        title = title,
        subtitle = stringResource(R.string.detail_lists_subtitle),
        width = 500.dp
    ) {
        if (!error.isNullOrBlank()) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFFFB6B6)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(tabs, key = { it.key }) { tab ->
                val selected = membership[tab.key] == true
                val titleText = if (selected) "\u2713 ${tab.title}" else tab.title
                Button(
                    onClick = { onToggle(tab.key) },
                    enabled = !isPending,
                    modifier = if (tab.key == tabs.firstOrNull()?.key) {
                        Modifier
                            .fillMaxWidth()
                            .focusRequester(primaryFocusRequester)
                    } else {
                        Modifier.fillMaxWidth()
                    },
                    colors = ButtonDefaults.colors(
                        containerColor = if (selected) NuvioColors.FocusBackground else NuvioColors.BackgroundCard,
                        contentColor = NuvioColors.TextPrimary
                    )
                ) {
                    Text(
                        text = titleText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Divider(color = NuvioColors.Border, thickness = 1.dp)

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Button(
                onClick = onSave,
                enabled = !isPending,
                colors = ButtonDefaults.colors(
                    containerColor = NuvioColors.BackgroundCard,
                    contentColor = NuvioColors.TextPrimary
                )
            ) {
                Text(if (isPending) stringResource(R.string.action_saving) else stringResource(R.string.action_save))
            }
        }
    }
}
