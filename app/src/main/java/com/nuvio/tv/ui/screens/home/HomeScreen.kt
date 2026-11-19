package com.nuvio.tv.ui.screens.home

import com.nuvio.tv.ui.theme.NuvioTheme

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.zIndex
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import android.net.Uri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button as TvMaterialButton
import com.nuvio.tv.ui.components.NuvioButton as Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.nuvio.tv.data.freebox.FREEBOX_FOLDER_ID_PREFIX
import com.nuvio.tv.domain.model.HomeLayout
import com.nuvio.tv.domain.model.LibraryListTab
import com.nuvio.tv.domain.model.localizedMembershipTitle
import com.nuvio.tv.domain.model.LibrarySourceMode
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.ui.components.ErrorState
import com.nuvio.tv.ui.components.FreeboxVideoOptionsDialog
import com.nuvio.tv.ui.components.LoadingIndicator
import com.nuvio.tv.ui.components.LocalStartupLoadingState
import com.nuvio.tv.ui.components.LocalStartupSplashEnabled
import com.nuvio.tv.ui.components.shouldShowHomeStartupLoader
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.components.PosterCardDefaults
import com.nuvio.tv.ui.components.PosterCardStyle
import androidx.compose.ui.res.stringResource
import com.nuvio.tv.R
import com.nuvio.tv.core.tmdb.TmdbArtworkCandidate
import com.nuvio.tv.core.tracking.LOCAL_LIBRARY_LIST_KEY
import com.nuvio.tv.core.tracking.supportsMembershipFor
import com.nuvio.tv.data.local.StartupAuthNotice
import com.nuvio.tv.reshaped.livetv.LiveTvRepository
import com.nuvio.tv.ui.components.posteroptions.TrackingRemovalConfirmationDialog
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private data class HomePosterOptionsTarget(
    val item: MetaPreview,
    val addonBaseUrl: String,
    val isFavoriteHomeRow: Boolean = false
)

private data class HomeFreeboxFolderArtworkTarget(
    val folder: HomePosterOptionsTarget,
    val portrait: Boolean,
)

private data class HomeFavoriteArtworkTarget(
    val item: MetaPreview,
    val portrait: Boolean,
)

private fun MetaPreview.freeboxRelativePath(): String? = when {
    id.startsWith(FREEBOX_FOLDER_ID_PREFIX) -> id.removePrefix(FREEBOX_FOLDER_ID_PREFIX)
    id.startsWith("freebox:", ignoreCase = true) -> id.substringAfter(':')
    else -> null
}

private const val HOME_STABLE_GATE_TIMEOUT_MS = 5_000L
private const val FREEBOX_HOME_ROWS_REFRESH_INTERVAL_MS = 30_000L

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDetail: (String, String, String) -> Unit,
    onPlayFreeboxVideoFromBeginning: (MetaPreview) -> Unit = { item ->
        onNavigateToDetail(item.id, item.apiType, item.sourceAddonBaseUrl.orEmpty())
    },
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
    onNavigateToCatalogSeeAll: (String, String, String) -> Unit = { _, _, _ -> },
    onNavigateToFolderDetail: (String, String) -> Unit = { _, _ -> },
    showCalendarShortcut: Boolean = false,
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToServers: () -> Unit = {},
    onManageHomeApps: () -> Unit = {},
    onNavigateToApps: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showSettingsDrawer by rememberSaveable { mutableStateOf(false) }

    // Home was the only major screen without a lifecycle observer, so nothing ever told it to
    // look at its catalogs again.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.beginShuffleHomeVisit()
                viewModel.syncMyListsIfNeeded()
                viewModel.refreshHomeCatalogsIfStale()
                viewModel.requestFreeboxHomeRowsRefresh()
                viewModel.refreshInstalledApps()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(lifecycleOwner, viewModel) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) {
                delay(FREEBOX_HOME_ROWS_REFRESH_INTERVAL_MS)
                viewModel.refreshFreeboxHomeRows()
            }
        }
    }
    val modernPresentation by viewModel.modernHomePresentation.collectAsStateWithLifecycle()
    val initialCwResolved by viewModel.initialCwResolved.collectAsStateWithLifecycle()
    val scrollToTopTrigger by viewModel.scrollToTopTrigger.collectAsStateWithLifecycle()
    var handledDrawerDismissTrigger by rememberSaveable { mutableStateOf(scrollToTopTrigger) }
    val effectiveAutoplayEnabled by viewModel.effectiveAutoplayEnabled.collectAsStateWithLifecycle(
        initialValue = false
    )
    val hasCatalogContent = uiState.catalogRows.any { it.items.isNotEmpty() } ||
        uiState.homeRows.any { it is HomeRow.Catalog && it.row.items.isNotEmpty() }
    val hasCollectionContent = uiState.homeRows.any { it is HomeRow.CollectionRow }
    val hasInstalledAppsContent = uiState.homeRows.any { it is HomeRow.InstalledApps && it.apps.isNotEmpty() }
    val hasHeroContent = uiState.heroItems.isNotEmpty()
    val modernPresentationReady =
        uiState.homeLayout != HomeLayout.MODERN ||
            modernPresentation.rows.list.isNotEmpty() ||
            (uiState.heroSectionEnabled && hasHeroContent && !hasCatalogContent && !hasCollectionContent)
    var showHomeContentWithAnimation by rememberSaveable { mutableStateOf(false) }
    var hasShownInitialHomeContent by rememberSaveable { mutableStateOf(false) }
    // Once we've shown stable home content, never go back to loading gate.
    var homeStableGateReleased by rememberSaveable { mutableStateOf(false) }
    // Track that catalog loading has started at least once (isLoading went true→false).
    var catalogLoadingStarted by rememberSaveable { mutableStateOf(false) }
    var posterOptionsTarget by remember { mutableStateOf<HomePosterOptionsTarget?>(null) }
    var freeboxFolderOptionsTarget by remember { mutableStateOf<HomePosterOptionsTarget?>(null) }
    var freeboxVideoOptionsTarget by remember { mutableStateOf<HomePosterOptionsTarget?>(null) }
    var freeboxDeleteTarget by remember { mutableStateOf<HomePosterOptionsTarget?>(null) }
    var freeboxCatalogVideoDeleteTarget by remember { mutableStateOf<HomePosterOptionsTarget?>(null) }
    var freeboxCatalogVideoDeleteError by remember { mutableStateOf<String?>(null) }
    var isFreeboxCatalogVideoDeleteBusy by remember { mutableStateOf(false) }
    var freeboxArtworkTarget by remember { mutableStateOf<HomeFreeboxFolderArtworkTarget?>(null) }
    var freeboxArtworkCandidates by remember { mutableStateOf<List<TmdbArtworkCandidate>>(emptyList()) }
    var isLoadingFreeboxArtwork by remember { mutableStateOf(false) }
    var freeboxArtworkError by remember { mutableStateOf<String?>(null) }
    var favoriteArtworkTarget by remember { mutableStateOf<HomeFavoriteArtworkTarget?>(null) }
    var favoriteArtworkCandidates by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingFavoriteArtwork by remember { mutableStateOf(false) }
    var isFavoriteArtworkBusy by remember { mutableStateOf(false) }
    var favoriteArtworkError by remember { mutableStateOf<String?>(null) }
    var isFreeboxActionRunning by remember { mutableStateOf(false) }
    var continueWatchingArtworkTarget by remember { mutableStateOf<ContinueWatchingItem?>(null) }
    var continueWatchingArtworkCandidates by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingContinueWatchingArtwork by remember { mutableStateOf(false) }
    var continueWatchingArtworkError by remember { mutableStateOf<String?>(null) }
    var isContinueWatchingArtworkBusy by remember { mutableStateOf(false) }
    var freeboxVideoDeleteTarget by remember { mutableStateOf<ContinueWatchingItem.InProgress?>(null) }
    var freeboxVideoDeleteError by remember { mutableStateOf<String?>(null) }
    var isFreeboxVideoDeleteBusy by remember { mutableStateOf(false) }
    var freeboxDeleteError by remember { mutableStateOf<String?>(null) }
    var freeboxFolderOptionsError by remember { mutableStateOf<String?>(null) }
    val homeActionScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger != handledDrawerDismissTrigger) {
            handledDrawerDismissTrigger = scrollToTopTrigger
            showSettingsDrawer = false
            posterOptionsTarget = null
            freeboxFolderOptionsTarget = null
            freeboxVideoOptionsTarget = null
            freeboxDeleteTarget = null
            freeboxCatalogVideoDeleteTarget = null
            freeboxArtworkTarget = null
            favoriteArtworkTarget = null
            continueWatchingArtworkTarget = null
            freeboxVideoDeleteTarget = null
        }
    }

    LaunchedEffect(uiState.homeLayout) {
        if (uiState.homeLayout != HomeLayout.MODERN) {
            HeroBackdropState.update(null)
        }
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
            val itemType = if (item.id.startsWith("freebox:", ignoreCase = true)) "movie" else item.apiType
            latestMovieWatchedStatus.value[homeItemStatusKey(item.id, itemType)] == true
        }
    }
    val onCatalogItemLongPress: (MetaPreview, String, Boolean) -> Unit = remember {
        { item, addonBaseUrl, isFavoriteHomeRow ->
            if (!item.apiType.equals("channel", ignoreCase = true)) {
                val target = HomePosterOptionsTarget(item, addonBaseUrl, isFavoriteHomeRow)
                if (item.id.startsWith(FREEBOX_FOLDER_ID_PREFIX)) {
                    freeboxFolderOptionsError = null
                    freeboxFolderOptionsTarget = target
                } else if (item.id.startsWith("freebox:", ignoreCase = true)) {
                    freeboxVideoOptionsTarget = target
                } else if (item.id.startsWith(HOME_IPTV_FAVORITE_ITEM_ID_PREFIX)) {
                    posterOptionsTarget = target
                } else {
                    posterOptionsTarget = target
                    viewModel.refreshPosterLibraryStatus(item)
                }
            }
        }
    }

    val onNavigateToDetailStable = remember(onNavigateToDetail) { onNavigateToDetail }
    val onContinueWatchingClickStable = remember(onContinueWatchingClick) { onContinueWatchingClick }
    val onContinueWatchingStartFromBeginningStable = remember(onContinueWatchingStartFromBeginning) { onContinueWatchingStartFromBeginning }
    val onContinueWatchingPlayManuallyStable = remember(onContinueWatchingPlayManually) { onContinueWatchingPlayManually }
    val onNavigateToCatalogSeeAllStable = remember(onNavigateToCatalogSeeAll) { onNavigateToCatalogSeeAll }
    val onNavigateToFolderDetailStable = remember(onNavigateToFolderDetail) { onNavigateToFolderDetail }
    val onRemoveContinueWatchingStable = remember(viewModel) {
        { contentId: String, season: Int?, episode: Int?, isNextUp: Boolean ->
            viewModel.onEvent(HomeEvent.OnRemoveContinueWatching(contentId, season, episode, isNextUp))
        }
    }
    val onChangeContinueWatchingHeroArtworkStable = remember(viewModel) {
        { item: ContinueWatchingItem ->
            continueWatchingArtworkTarget = item
            continueWatchingArtworkCandidates = emptyList()
            continueWatchingArtworkError = null
            isLoadingContinueWatchingArtwork = true
            homeActionScope.launch {
                try {
                    continueWatchingArtworkCandidates = viewModel.findContinueWatchingHeroArtworkCandidates(item)
                    if (continueWatchingArtworkCandidates.isEmpty()) {
                        continueWatchingArtworkError = context.getString(R.string.cw_hero_artwork_empty)
                    }
                } catch (error: Exception) {
                    continueWatchingArtworkError = error.message ?: context.getString(R.string.cw_hero_artwork_empty)
                } finally {
                    isLoadingContinueWatchingArtwork = false
                }
            }
            Unit
        }
    }
    val onRequestFreeboxVideoDeleteStable = remember {
        { item: ContinueWatchingItem.InProgress ->
            freeboxVideoDeleteTarget = item
            freeboxVideoDeleteError = null
        }
    }
    val onMarkContinueWatchingUnwatchedStable = remember(viewModel) {
        { item: ContinueWatchingItem.InProgress ->
            viewModel.onEvent(HomeEvent.OnMarkContinueWatchingUnwatched(item))
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
        if (uiState.installedAddonsCount > 0 || hasCatalogContent || (hasInstalledAppsContent && !uiState.isLoading)) {
            catalogLoadingStarted = true
        }
        // Wait until catalog loading has completed with content AND the CW
        // pipeline has completed its first emission.
        if (!homeStableGateReleased &&
            catalogLoadingStarted &&
            !uiState.isLoading &&
            initialCwResolved &&
            modernPresentationReady &&
            // When addons are installed, require at least one catalog row.
            (hasCatalogContent || hasInstalledAppsContent || uiState.installedAddonsCount == 0)
        ) {
            Log.d("HomeGate", "RELEASE: catalogs=$hasCatalogContent cwResolved=$initialCwResolved cwItems=${uiState.continueWatchingItems.size} addons=${uiState.installedAddonsCount}")
            homeStableGateReleased = true
        }
    }

    LaunchedEffect(Unit) {
        // Safety timeout — if catalogs and CW haven't loaded within this
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

    val noAddonsError = stringResource(R.string.home_empty_no_sources_title)
    val noCatalogAddonsError = stringResource(R.string.home_error_no_catalog_addons)
    val hasAnyContent = hasCatalogContent ||
        (uiState.continueWatchingEnabled && uiState.continueWatchingItems.isNotEmpty()) ||
        uiState.heroItems.isNotEmpty() ||
        hasCollectionContent ||
        hasInstalledAppsContent
    val showStartupLoader = when {
        !uiState.layoutPreferencesReady -> true
        uiState.isLoading && !hasAnyContent -> true
        uiState.error == noAddonsError && !hasCatalogContent && !hasInstalledAppsContent && !hasCollectionContent && !hasHeroContent -> !homeStableGateReleased
        uiState.error == noCatalogAddonsError && !hasCatalogContent && !hasCollectionContent && !hasHeroContent && !hasInstalledAppsContent -> !homeStableGateReleased
        uiState.error != null && uiState.catalogRows.isEmpty() && !hasInstalledAppsContent -> false
        !uiState.isLoading && !hasAnyContent -> !homeStableGateReleased
        else -> !homeStableGateReleased || !modernPresentationReady || !showHomeContentWithAnimation
    }

    // Reports the home screen as fully drawn once it leaves the loading state so startup timing is measurable and post-launch work can be deferred.
    ReportDrawnWhen { !showStartupLoader }

    val startupLoadingState = LocalStartupLoadingState.current
    val showHomeLoader = shouldShowHomeStartupLoader(
        loading = showStartupLoader,
        sharedSplashEnabled = LocalStartupSplashEnabled.current,
        startupComplete = startupLoadingState?.complete != false
    )
    LaunchedEffect(showStartupLoader, startupLoadingState) {
        if (!showStartupLoader) {
            startupLoadingState?.complete = true
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            !uiState.layoutPreferencesReady -> {
                Unit
            }

            uiState.isLoading && !hasAnyContent -> {
                Unit
            }

            uiState.error == noAddonsError && !hasCatalogContent && !hasInstalledAppsContent && !hasCollectionContent && !hasHeroContent -> {
                if (!homeStableGateReleased) {
                    Unit
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.home_no_addons),
                            style = MaterialTheme.typography.bodyLarge,
                            color = NuvioTheme.colors.TextSecondary
                        )
                    }
                }
            }

            uiState.error == noCatalogAddonsError && !hasCatalogContent && !hasCollectionContent && !hasHeroContent && !hasInstalledAppsContent -> {
                if (!homeStableGateReleased) {
                    Unit
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.home_no_catalog_addons),
                            style = MaterialTheme.typography.bodyLarge,
                            color = NuvioTheme.colors.TextSecondary
                        )
                    }
                }
            }

            uiState.error != null && uiState.catalogRows.isEmpty() && !hasInstalledAppsContent -> {
                ErrorState(
                    message = uiState.error ?: stringResource(R.string.error_generic),
                    onRetry = { viewModel.onEvent(HomeEvent.OnRetry) }
                )
            }

            !uiState.isLoading && !hasAnyContent -> {
                // Don't show "no catalogs" until the stable gate has released —
                // addons may still be loading from remote after a cache clear.
                if (!homeStableGateReleased) {
                    Unit
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.web_no_catalogs),
                            style = MaterialTheme.typography.bodyLarge,
                            color = NuvioTheme.colors.TextSecondary
                        )
                    }
                }
            }

            else -> {
                // On first launch, wait for stable content before revealing home.
                // Once released, never go back to loading (homeStableGateReleased is rememberSaveable).
                if (!homeStableGateReleased) {
                    Unit
                } else if (!modernPresentationReady) {
                    Unit
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
                        Unit
                    }
                    AnimatedVisibility(
                        visible = showHomeContentWithAnimation,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 80.dp)
                            .clipToBounds(),
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
                                uiState = uiState,
                                posterCardStyle = posterCardStyle,
                                onNavigateToDetail = onNavigateToDetailStable,
                                onContinueWatchingClick = onContinueWatchingClickStable,
                                onContinueWatchingStartFromBeginning = onContinueWatchingStartFromBeginningStable,
                                onContinueWatchingPlayManually = onContinueWatchingPlayManuallyStable,
                                onChangeContinueWatchingHeroArtwork = onChangeContinueWatchingHeroArtworkStable,
                                onDeleteContinueWatchingFreebox = onRequestFreeboxVideoDeleteStable,
                                onMarkContinueWatchingUnwatched = onMarkContinueWatchingUnwatchedStable,
                                showContinueWatchingManualPlayOption = effectiveAutoplayEnabled,
                                onNavigateToCatalogSeeAll = onNavigateToCatalogSeeAllStable,
                                onNavigateToFolderDetail = onNavigateToFolderDetailStable,
                                isCatalogItemWatched = isCatalogItemWatched,
                                onCatalogItemLongPress = onCatalogItemLongPress
                            )

                            HomeLayout.GRID -> GridHomeRoute(
                                viewModel = viewModel,
                                uiState = uiState,
                                posterCardStyle = posterCardStyle,
                                onNavigateToDetail = onNavigateToDetailStable,
                                onContinueWatchingClick = onContinueWatchingClickStable,
                                onContinueWatchingStartFromBeginning = onContinueWatchingStartFromBeginningStable,
                                onContinueWatchingPlayManually = onContinueWatchingPlayManuallyStable,
                                onChangeContinueWatchingHeroArtwork = onChangeContinueWatchingHeroArtworkStable,
                                onDeleteContinueWatchingFreebox = onRequestFreeboxVideoDeleteStable,
                                onMarkContinueWatchingUnwatched = onMarkContinueWatchingUnwatchedStable,
                                showContinueWatchingManualPlayOption = effectiveAutoplayEnabled,
                                onNavigateToCatalogSeeAll = onNavigateToCatalogSeeAllStable,
                                onNavigateToFolderDetail = onNavigateToFolderDetailStable,
                                isCatalogItemWatched = isCatalogItemWatched,
                                onCatalogItemLongPress = onCatalogItemLongPress
                            )

                            HomeLayout.MODERN -> ModernHomeRoute(
                                viewModel = viewModel,
                                uiState = uiState,
                                onNavigateToDetail = onNavigateToDetailStable,
                                onContinueWatchingClick = onContinueWatchingClickStable,
                                onContinueWatchingStartFromBeginning = onContinueWatchingStartFromBeginningStable,
                                onContinueWatchingPlayManually = onContinueWatchingPlayManuallyStable,
                                onChangeContinueWatchingHeroArtwork = onChangeContinueWatchingHeroArtworkStable,
                                onDeleteContinueWatchingFreebox = onRequestFreeboxVideoDeleteStable,
                                onMarkContinueWatchingUnwatched = onMarkContinueWatchingUnwatchedStable,
                                showContinueWatchingManualPlayOption = effectiveAutoplayEnabled,
                                onNavigateToFolderDetail = onNavigateToFolderDetailStable,
                                isCatalogItemWatched = isCatalogItemWatched,
                                onCatalogItemLongPress = onCatalogItemLongPress
                            )
                        }
                    }
                }
            }
        }

        if (showHomeLoader) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }

        val startupAuthNotice = uiState.startupAuthNotice
        if (startupAuthNotice != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = NuvioTheme.spacing.xl)
                    .background(
                        color = Color(0xFF5A1C1C),
                        shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))
                    )
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    text = when (startupAuthNotice) {
                        StartupAuthNotice.NUVIO -> stringResource(R.string.auth_notice_nuvio_logged_out)
                        StartupAuthNotice.TRAKT -> stringResource(R.string.auth_notice_trakt_logged_out)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = NuvioTheme.colors.TextPrimary
                )
            }
        }

        if (!showStartupLoader) {
            HomeTopBarHeader(
                showCalendarShortcut = showCalendarShortcut,
                onCalendarClick = onNavigateToCalendar,
                onServersClick = onNavigateToServers,
                onAppsClick = onNavigateToApps,
                onSettingsClick = { showSettingsDrawer = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .zIndex(2f)
            )
        }

        if (showSettingsDrawer) {
            HomeSettingsDrawer(
                onDismiss = { showSettingsDrawer = false },
                onOpenNuvioSettings = onNavigateToSettings
            )
        }

    }

    val selectedFreeboxFolder = freeboxFolderOptionsTarget
    if (selectedFreeboxFolder != null) {
        FreeboxFolderOptionsDialog(
            title = selectedFreeboxFolder.item.name,
            isBusy = isFreeboxActionRunning,
            error = freeboxFolderOptionsError,
            onDismiss = { freeboxFolderOptionsTarget = null },
            onChangePortrait = {
                val target = selectedFreeboxFolder
                freeboxFolderOptionsTarget = null
                freeboxArtworkTarget = HomeFreeboxFolderArtworkTarget(target, portrait = true)
                freeboxArtworkCandidates = emptyList()
                freeboxArtworkError = null
                isLoadingFreeboxArtwork = true
                homeActionScope.launch {
                    try {
                        freeboxArtworkCandidates = viewModel.findFreeboxFolderArtworkCandidates(
                            path = target.item.id.removePrefix(FREEBOX_FOLDER_ID_PREFIX),
                            title = target.item.name,
                            mediaType = "tv",
                            portrait = true,
                            year = target.item.releaseInfo?.take(4)?.toIntOrNull(),
                        )
                        if (freeboxArtworkCandidates.isEmpty()) {
                            freeboxArtworkError = context.getString(R.string.home_freebox_artwork_empty)
                        }
                    } catch (error: Exception) {
                        freeboxArtworkError = error.message ?: context.getString(R.string.home_freebox_artwork_empty)
                    } finally {
                        isLoadingFreeboxArtwork = false
                    }
                }
            },
            onChangeLandscape = {
                val target = selectedFreeboxFolder
                freeboxFolderOptionsTarget = null
                freeboxArtworkTarget = HomeFreeboxFolderArtworkTarget(target, portrait = false)
                freeboxArtworkCandidates = emptyList()
                freeboxArtworkError = null
                isLoadingFreeboxArtwork = true
                homeActionScope.launch {
                    try {
                        freeboxArtworkCandidates = viewModel.findFreeboxFolderArtworkCandidates(
                            path = target.item.id.removePrefix(FREEBOX_FOLDER_ID_PREFIX),
                            title = target.item.name,
                            mediaType = "tv",
                            portrait = false,
                            year = target.item.releaseInfo?.take(4)?.toIntOrNull(),
                        )
                        if (freeboxArtworkCandidates.isEmpty()) {
                            freeboxArtworkError = context.getString(R.string.home_freebox_artwork_empty)
                        }
                    } catch (error: Exception) {
                        freeboxArtworkError = error.message ?: context.getString(R.string.home_freebox_artwork_empty)
                    } finally {
                        isLoadingFreeboxArtwork = false
                    }
                }
            },
            onOpen = {
                onNavigateToDetail(
                    selectedFreeboxFolder.item.id,
                    selectedFreeboxFolder.item.apiType,
                    selectedFreeboxFolder.addonBaseUrl,
                )
                freeboxFolderOptionsTarget = null
            },
            onRemoveFromHome = {
                isFreeboxActionRunning = true
                homeActionScope.launch {
                    try {
                        viewModel.removeFreeboxFolderFromHome(
                            selectedFreeboxFolder.item.id.removePrefix(FREEBOX_FOLDER_ID_PREFIX)
                        )
                        freeboxFolderOptionsTarget = null
                        android.widget.Toast.makeText(
                            context,
                            context.getString(R.string.home_freebox_remove_success),
                            android.widget.Toast.LENGTH_SHORT,
                        ).show()
                    } catch (error: Exception) {
                        freeboxFolderOptionsError = error.message
                    } finally {
                        isFreeboxActionRunning = false
                    }
                }
            },
            onDeleteFromFreebox = {
                freeboxFolderOptionsTarget = null
                freeboxDeleteError = null
                freeboxDeleteTarget = selectedFreeboxFolder
            },
        )
    }

    val selectedFreeboxVideoOptions = freeboxVideoOptionsTarget
    if (selectedFreeboxVideoOptions != null) {
        val item = selectedFreeboxVideoOptions.item
        val isWatched = isCatalogItemWatched(item)
        FreeboxVideoOptionsDialog(
            title = item.name,
            onDismiss = { freeboxVideoOptionsTarget = null },
            onMarkWatched = {
                if (!isWatched) viewModel.setFreeboxHomeVideoWatched(item, watched = true)
                freeboxVideoOptionsTarget = null
            },
            onMarkUnwatched = {
                if (isWatched) viewModel.setFreeboxHomeVideoWatched(item, watched = false)
                freeboxVideoOptionsTarget = null
            },
            onStartFromBeginning = {
                onPlayFreeboxVideoFromBeginning(item)
                freeboxVideoOptionsTarget = null
            },
            onChangePortraitArtwork = {
                val path = requireNotNull(item.freeboxRelativePath())
                freeboxVideoOptionsTarget = null
                freeboxArtworkTarget = HomeFreeboxFolderArtworkTarget(selectedFreeboxVideoOptions, portrait = true)
                freeboxArtworkCandidates = emptyList()
                freeboxArtworkError = null
                isLoadingFreeboxArtwork = true
                homeActionScope.launch {
                    try {
                        freeboxArtworkCandidates = viewModel.findFreeboxFolderArtworkCandidates(
                            path = path,
                            title = item.name,
                            mediaType = item.apiType,
                            portrait = true,
                            year = item.releaseInfo?.take(4)?.toIntOrNull(),
                        )
                        if (freeboxArtworkCandidates.isEmpty()) {
                            freeboxArtworkError = context.getString(R.string.home_freebox_artwork_empty)
                        }
                    } catch (error: Exception) {
                        freeboxArtworkError = error.message ?: context.getString(R.string.home_freebox_artwork_empty)
                    } finally {
                        isLoadingFreeboxArtwork = false
                    }
                }
            },
            onChangeLandscapeArtwork = {
                val path = requireNotNull(item.freeboxRelativePath())
                freeboxVideoOptionsTarget = null
                freeboxArtworkTarget = HomeFreeboxFolderArtworkTarget(selectedFreeboxVideoOptions, portrait = false)
                freeboxArtworkCandidates = emptyList()
                freeboxArtworkError = null
                isLoadingFreeboxArtwork = true
                homeActionScope.launch {
                    try {
                        freeboxArtworkCandidates = viewModel.findFreeboxFolderArtworkCandidates(
                            path = path,
                            title = item.name,
                            mediaType = item.apiType,
                            portrait = false,
                            year = item.releaseInfo?.take(4)?.toIntOrNull(),
                        )
                        if (freeboxArtworkCandidates.isEmpty()) {
                            freeboxArtworkError = context.getString(R.string.home_freebox_artwork_empty)
                        }
                    } catch (error: Exception) {
                        freeboxArtworkError = error.message ?: context.getString(R.string.home_freebox_artwork_empty)
                    } finally {
                        isLoadingFreeboxArtwork = false
                    }
                }
            },
            onRemoveFromContinueWatching = {
                viewModel.onEvent(HomeEvent.OnRemoveContinueWatching(item.id))
                freeboxVideoOptionsTarget = null
            },
            onDeleteFromFreebox = {
                freeboxVideoOptionsTarget = null
                freeboxCatalogVideoDeleteError = null
                freeboxCatalogVideoDeleteTarget = selectedFreeboxVideoOptions
            },
        )
    }

    val selectedFreeboxCatalogVideoForDeletion = freeboxCatalogVideoDeleteTarget
    if (selectedFreeboxCatalogVideoForDeletion != null) {
        FreeboxVideoDeleteDialog(
            title = selectedFreeboxCatalogVideoForDeletion.item.name,
            error = freeboxCatalogVideoDeleteError,
            isBusy = isFreeboxCatalogVideoDeleteBusy,
            onDismiss = { if (!isFreeboxCatalogVideoDeleteBusy) freeboxCatalogVideoDeleteTarget = null },
            onConfirm = {
                isFreeboxCatalogVideoDeleteBusy = true
                freeboxCatalogVideoDeleteError = null
                homeActionScope.launch {
                    try {
                        viewModel.deleteFreeboxHomeVideo(selectedFreeboxCatalogVideoForDeletion.item)
                        freeboxCatalogVideoDeleteTarget = null
                        android.widget.Toast.makeText(
                            context,
                            context.getString(R.string.home_freebox_delete_video_success),
                            android.widget.Toast.LENGTH_SHORT,
                        ).show()
                    } catch (error: Exception) {
                        freeboxCatalogVideoDeleteError = error.message ?: context.getString(R.string.cw_action_delete_from_freebox)
                    } finally {
                        isFreeboxCatalogVideoDeleteBusy = false
                    }
                }
            },
        )
    }

    val selectedFreeboxForDeletion = freeboxDeleteTarget
    if (selectedFreeboxForDeletion != null) {
        FreeboxFolderDeleteDialog(
            title = selectedFreeboxForDeletion.item.name,
            error = freeboxDeleteError,
            isBusy = isFreeboxActionRunning,
            onDismiss = { if (!isFreeboxActionRunning) freeboxDeleteTarget = null },
            onConfirm = {
                isFreeboxActionRunning = true
                freeboxDeleteError = null
                homeActionScope.launch {
                    try {
                        viewModel.deleteFreeboxHomeFolder(
                            selectedFreeboxForDeletion.item.id.removePrefix(FREEBOX_FOLDER_ID_PREFIX)
                        )
                        freeboxDeleteTarget = null
                        android.widget.Toast.makeText(
                            context,
                            context.getString(R.string.home_freebox_delete_success),
                            android.widget.Toast.LENGTH_SHORT,
                        ).show()
                    } catch (error: Exception) {
                        freeboxDeleteError = error.message ?: context.getString(R.string.home_freebox_delete_folder)
                    } finally {
                        isFreeboxActionRunning = false
                    }
                }
            },
        )
    }

    val selectedFreeboxArtwork = freeboxArtworkTarget
    if (selectedFreeboxArtwork != null) {
        HomeArtworkPickerDialog(
            portrait = selectedFreeboxArtwork.portrait,
            candidates = freeboxArtworkCandidates.map { it.imageUrl },
            candidateDetails = freeboxArtworkCandidates,
            isLoading = isLoadingFreeboxArtwork,
            isBusy = isFreeboxActionRunning,
            error = freeboxArtworkError,
            onDismiss = { freeboxArtworkTarget = null },
            onSelectWithDetails = { imageUrl, candidate ->
                isFreeboxActionRunning = true
                freeboxArtworkError = null
                homeActionScope.launch {
                    try {
                        viewModel.setFreeboxFolderArtwork(
                            path = requireNotNull(selectedFreeboxArtwork.folder.item.freeboxRelativePath()),
                            portrait = selectedFreeboxArtwork.portrait,
                            imageUrl = imageUrl,
                            candidate = candidate,
                            item = selectedFreeboxArtwork.folder.item,
                        )
                        freeboxArtworkTarget = null
                    } catch (error: Exception) {
                        freeboxArtworkError = error.message ?: context.getString(R.string.home_freebox_artwork_empty)
                    } finally {
                        isFreeboxActionRunning = false
                    }
                }
            },
            onReset = {
                isFreeboxActionRunning = true
                freeboxArtworkError = null
                homeActionScope.launch {
                    try {
                        viewModel.setFreeboxFolderArtwork(
                            path = requireNotNull(selectedFreeboxArtwork.folder.item.freeboxRelativePath()),
                            portrait = selectedFreeboxArtwork.portrait,
                            imageUrl = null,
                            item = selectedFreeboxArtwork.folder.item,
                        )
                        freeboxArtworkTarget = null
                    } catch (error: Exception) {
                        freeboxArtworkError = error.message ?: context.getString(R.string.home_freebox_artwork_empty)
                    } finally {
                        isFreeboxActionRunning = false
                    }
                }
            },
        )
    }

    val selectedContinueWatchingArtwork = continueWatchingArtworkTarget
    if (selectedContinueWatchingArtwork != null) {
        HomeArtworkPickerDialog(
            title = stringResource(R.string.cw_hero_artwork_title),
            subtitle = stringResource(R.string.cw_hero_artwork_subtitle),
            emptyMessage = stringResource(R.string.cw_hero_artwork_empty),
            resetLabel = stringResource(R.string.cw_hero_artwork_reset),
            portrait = false,
            candidates = continueWatchingArtworkCandidates,
            isLoading = isLoadingContinueWatchingArtwork,
            isBusy = isContinueWatchingArtworkBusy,
            error = continueWatchingArtworkError,
            onDismiss = { if (!isContinueWatchingArtworkBusy) continueWatchingArtworkTarget = null },
            onSelect = { imageUrl ->
                isContinueWatchingArtworkBusy = true
                continueWatchingArtworkError = null
                homeActionScope.launch {
                    try {
                        viewModel.setContinueWatchingHeroArtwork(selectedContinueWatchingArtwork, imageUrl)
                        continueWatchingArtworkTarget = null
                    } catch (error: Exception) {
                        continueWatchingArtworkError = error.message ?: context.getString(R.string.cw_hero_artwork_empty)
                    } finally {
                        isContinueWatchingArtworkBusy = false
                    }
                }
            },
            onReset = {
                isContinueWatchingArtworkBusy = true
                continueWatchingArtworkError = null
                homeActionScope.launch {
                    try {
                        viewModel.setContinueWatchingHeroArtwork(selectedContinueWatchingArtwork, null)
                        continueWatchingArtworkTarget = null
                    } catch (error: Exception) {
                        continueWatchingArtworkError = error.message ?: context.getString(R.string.cw_hero_artwork_empty)
                    } finally {
                        isContinueWatchingArtworkBusy = false
                    }
                }
            },
        )
    }

    val selectedFreeboxVideo = freeboxVideoDeleteTarget
    if (selectedFreeboxVideo != null) {
        FreeboxVideoDeleteDialog(
            title = selectedFreeboxVideo.progress.name,
            error = freeboxVideoDeleteError,
            isBusy = isFreeboxVideoDeleteBusy,
            onDismiss = { if (!isFreeboxVideoDeleteBusy) freeboxVideoDeleteTarget = null },
            onConfirm = {
                isFreeboxVideoDeleteBusy = true
                freeboxVideoDeleteError = null
                homeActionScope.launch {
                    try {
                        viewModel.deleteFreeboxHomeVideo(selectedFreeboxVideo.progress)
                        freeboxVideoDeleteTarget = null
                        android.widget.Toast.makeText(
                            context,
                            context.getString(R.string.home_freebox_delete_video_success),
                            android.widget.Toast.LENGTH_SHORT,
                        ).show()
                    } catch (error: Exception) {
                        freeboxVideoDeleteError = error.message ?: context.getString(R.string.cw_action_delete_from_freebox)
                    } finally {
                        isFreeboxVideoDeleteBusy = false
                    }
                }
            },
        )
    }

    val selectedPoster = posterOptionsTarget
    if (selectedPoster != null) {
        val item = selectedPoster.item
        val statusKey = homeItemStatusKey(item.id, item.apiType)
        val iptvFavoriteKey = item.id
            .takeIf { it.startsWith(HOME_IPTV_FAVORITE_ITEM_ID_PREFIX) }
            ?.removePrefix(HOME_IPTV_FAVORITE_ITEM_ID_PREFIX)
            ?.let { Uri.decode(it) }
        val isMovie = item.apiType.equals("movie", ignoreCase = true)
        val isSeries = item.apiType.equals("series", ignoreCase = true) ||
            item.apiType.equals("tv", ignoreCase = true) ||
            item.apiType.equals("anime", ignoreCase = true)
        val resumableItem = if (selectedPoster.isFavoriteHomeRow) {
            uiState.continueWatchingItems.firstOrNull { progressItem ->
                when (progressItem) {
                    is ContinueWatchingItem.InProgress -> progressItem.progress.contentId == item.id
                    is ContinueWatchingItem.NextUp -> progressItem.info.contentId == item.id
                }
            }
        } else null
        fun openFavoriteArtworkPicker(portrait: Boolean) {
            posterOptionsTarget = null
            favoriteArtworkTarget = HomeFavoriteArtworkTarget(item, portrait)
            favoriteArtworkCandidates = emptyList()
            favoriteArtworkError = null
            isLoadingFavoriteArtwork = true
            homeActionScope.launch {
                try {
                    favoriteArtworkCandidates = viewModel.findHomeFavoriteArtworkCandidates(item, portrait)
                    if (favoriteArtworkCandidates.isEmpty()) {
                        favoriteArtworkError = context.getString(R.string.home_favorite_artwork_empty)
                    }
                } catch (error: Exception) {
                    favoriteArtworkError = error.message ?: context.getString(R.string.home_favorite_artwork_empty)
                } finally {
                    isLoadingFavoriteArtwork = false
                }
            }
        }
        HomePosterOptionsDialog(
            title = item.name,
            isInLibrary = iptvFavoriteKey != null || uiState.posterLibraryMembership[statusKey] == true,
            isLibraryPending = statusKey in uiState.posterLibraryPending,
            showManageLists = !selectedPoster.isFavoriteHomeRow && iptvFavoriteKey == null &&
                uiState.librarySourceMode != LibrarySourceMode.LOCAL,
            isMovie = isMovie,
            isSeries = isSeries,
            isWatched = movieWatchedStatus[statusKey] == true,
            isWatchedPending = statusKey in uiState.movieWatchedPending,
            isFavoriteHomeRow = selectedPoster.isFavoriteHomeRow,
            hasResumeProgress = resumableItem != null,
            onDismiss = { posterOptionsTarget = null },
            onDetails = {
                onNavigateToDetail(item.id, item.apiType, selectedPoster.addonBaseUrl)
                posterOptionsTarget = null
            },
            onRestartFromBeginning = {
                resumableItem?.let(onContinueWatchingStartFromBeginningStable)
                posterOptionsTarget = null
            },
            onChangePortrait = { openFavoriteArtworkPicker(true) },
            onChangeLandscape = { openFavoriteArtworkPicker(false) },
            onToggleLibrary = {
                if (iptvFavoriteKey != null) {
                    LiveTvRepository.toggleVodFavoriteKey(iptvFavoriteKey)
                } else if (!selectedPoster.isFavoriteHomeRow && uiState.librarySourceMode != LibrarySourceMode.LOCAL) {
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
            }
        )
    }

    favoriteArtworkTarget?.let { artworkTarget ->
        HomeArtworkPickerDialog(
            title = stringResource(
                if (artworkTarget.portrait) R.string.home_favorite_artwork_portrait_title
                else R.string.home_favorite_artwork_landscape_title
            ),
            subtitle = stringResource(R.string.home_favorite_artwork_subtitle, artworkTarget.item.name),
            emptyMessage = stringResource(R.string.home_favorite_artwork_empty),
            resetLabel = stringResource(R.string.home_freebox_artwork_reset),
            portrait = artworkTarget.portrait,
            candidates = favoriteArtworkCandidates,
            isLoading = isLoadingFavoriteArtwork,
            isBusy = isFavoriteArtworkBusy,
            error = favoriteArtworkError,
            onDismiss = { favoriteArtworkTarget = null },
            onSelect = { imageUrl ->
                isFavoriteArtworkBusy = true
                homeActionScope.launch {
                    try {
                        viewModel.setHomeFavoriteArtwork(artworkTarget.item, artworkTarget.portrait, imageUrl)
                        favoriteArtworkTarget = null
                    } catch (error: Exception) {
                        favoriteArtworkError = error.message ?: context.getString(R.string.home_favorite_artwork_empty)
                    } finally {
                        isFavoriteArtworkBusy = false
                    }
                }
            },
            onReset = {
                isFavoriteArtworkBusy = true
                homeActionScope.launch {
                    try {
                        viewModel.setHomeFavoriteArtwork(artworkTarget.item, artworkTarget.portrait, null)
                        favoriteArtworkTarget = null
                    } catch (error: Exception) {
                        favoriteArtworkError = error.message ?: context.getString(R.string.home_favorite_artwork_empty)
                    } finally {
                        isFavoriteArtworkBusy = false
                    }
                }
            }
        )
    }

    if (uiState.showPosterListPicker) {
        val localTab = LibraryListTab(
            key = LOCAL_LIBRARY_LIST_KEY,
            title = stringResource(R.string.trakt_library_source_nuvio),
            type = LibraryListTab.Type.WATCHLIST
        )
        val contentType = uiState.posterListPickerContentType.orEmpty()
        val destinationTabs = uiState.libraryListTabs.filter { tab ->
            tab.supportsMembershipFor(contentType)
        }
        HomeLibraryListPickerDialog(
            title = uiState.posterListPickerTitle ?: stringResource(R.string.detail_lists_fallback),
            tabs = listOf(localTab) + destinationTabs,
            membership = uiState.posterListPickerMembership,
            isPending = uiState.posterListPickerPending,
            error = uiState.posterListPickerError,
            onToggle = { key -> viewModel.togglePosterListPickerMembership(key) },
            onSave = { viewModel.savePosterListPickerMembership() },
            onDismiss = { viewModel.dismissPosterListPicker() }
        )
    }

    if (uiState.posterListPickerRemovalConfirmations.isNotEmpty()) {
        TrackingRemovalConfirmationDialog(
            itemTitle = uiState.posterListPickerTitle.orEmpty(),
            confirmations = uiState.posterListPickerRemovalConfirmations,
            isPending = uiState.posterListPickerPending,
            onConfirm = viewModel::confirmPosterListPickerRemoval,
            onDismiss = viewModel::cancelPosterListPickerRemoval
        )
    }
}

@Composable
private fun ClassicHomeRoute(
    viewModel: HomeViewModel,
    uiState: HomeUiState,
    posterCardStyle: PosterCardStyle,
    onNavigateToDetail: (String, String, String) -> Unit,
    onContinueWatchingClick: (ContinueWatchingItem) -> Unit,
    onContinueWatchingStartFromBeginning: (ContinueWatchingItem) -> Unit,
    onContinueWatchingPlayManually: (ContinueWatchingItem) -> Unit,
    onChangeContinueWatchingHeroArtwork: (ContinueWatchingItem) -> Unit,
    onDeleteContinueWatchingFreebox: (ContinueWatchingItem.InProgress) -> Unit,
    onMarkContinueWatchingUnwatched: (ContinueWatchingItem.InProgress) -> Unit,
    showContinueWatchingManualPlayOption: Boolean,
    onNavigateToCatalogSeeAll: (String, String, String) -> Unit,
    onNavigateToFolderDetail: (String, String) -> Unit = { _, _ -> },
    isCatalogItemWatched: (MetaPreview) -> Boolean,
    onCatalogItemLongPress: (MetaPreview, String, Boolean) -> Unit
) {
    val focusState by viewModel.focusState.collectAsStateWithLifecycle()
    val scrollToTopTrigger by viewModel.scrollToTopTrigger.collectAsStateWithLifecycle()
    val enrichedPreviews by viewModel.enrichedPreviews.collectAsStateWithLifecycle()
    ClassicHomeContent(
        uiState = uiState,
        posterCardStyle = posterCardStyle,
        focusState = focusState,
        enrichedPreviews = enrichedPreviews,
        scrollToTopTrigger = scrollToTopTrigger,
        trailerPreviewUrls = viewModel.trailerPreviewUrls,
        trailerPreviewAudioUrls = viewModel.trailerPreviewAudioUrls,
        onNavigateToDetail = onNavigateToDetail,
        onContinueWatchingClick = onContinueWatchingClick,
        onContinueWatchingStartFromBeginning = onContinueWatchingStartFromBeginning,
        onContinueWatchingPlayManually = onContinueWatchingPlayManually,
        onChangeContinueWatchingHeroArtwork = onChangeContinueWatchingHeroArtwork,
        onDeleteContinueWatchingFreebox = onDeleteContinueWatchingFreebox,
        onMarkContinueWatchingUnwatched = onMarkContinueWatchingUnwatched,
        showContinueWatchingManualPlayOption = showContinueWatchingManualPlayOption,
        onNavigateToCatalogSeeAll = onNavigateToCatalogSeeAll,
        onNavigateToFolderDetail = onNavigateToFolderDetail,
        onMarkContinueWatchingWatched = remember(viewModel) {
            { item -> viewModel.onEvent(HomeEvent.OnMarkContinueWatchingWatched(item)) }
        },
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
        onSaveFocusState = { vi, vo, rk, ikm, m, ma, ri, ii ->
            viewModel.saveFocusState(vi, vo, rk, ikm, m, ma, ri, ii)
            // Authoritative: this is the row that actually held focus when Home went away.
            viewModel.setLiveFocusedRowKey(rk)
        },
        onFocusedRowKeyChanged = remember(viewModel) {
            { key: String? -> viewModel.setLiveFocusedRowKey(key) }
        },
        onRequestLazyCatalogLoad = remember(viewModel) {
            { catalogKey: String -> viewModel.requestLazyCatalogLoad(catalogKey) }
        }
    )
}

@Composable
private fun GridHomeRoute(
    viewModel: HomeViewModel,
    uiState: HomeUiState,
    posterCardStyle: PosterCardStyle,
    onNavigateToDetail: (String, String, String) -> Unit,
    onContinueWatchingClick: (ContinueWatchingItem) -> Unit,
    onContinueWatchingStartFromBeginning: (ContinueWatchingItem) -> Unit,
    onContinueWatchingPlayManually: (ContinueWatchingItem) -> Unit,
    onChangeContinueWatchingHeroArtwork: (ContinueWatchingItem) -> Unit,
    onDeleteContinueWatchingFreebox: (ContinueWatchingItem.InProgress) -> Unit,
    onMarkContinueWatchingUnwatched: (ContinueWatchingItem.InProgress) -> Unit,
    showContinueWatchingManualPlayOption: Boolean,
    onNavigateToCatalogSeeAll: (String, String, String) -> Unit,
    onNavigateToFolderDetail: (String, String) -> Unit = { _, _ -> },
    isCatalogItemWatched: (MetaPreview) -> Boolean,
    onCatalogItemLongPress: (MetaPreview, String, Boolean) -> Unit
) {
    val gridFocusState by viewModel.gridFocusState.collectAsStateWithLifecycle()
    val scrollToTopTrigger by viewModel.scrollToTopTrigger.collectAsStateWithLifecycle()
    GridHomeContent(
        onFocusedRowKeyChanged = remember(viewModel) {
            { key: String? -> viewModel.setLiveFocusedRowKey(key) }
        },
        uiState = uiState,
        posterCardStyle = posterCardStyle,
        gridFocusState = gridFocusState,
        scrollToTopTrigger = scrollToTopTrigger,
        onNavigateToDetail = onNavigateToDetail,
        onContinueWatchingClick = onContinueWatchingClick,
        onContinueWatchingStartFromBeginning = onContinueWatchingStartFromBeginning,
        onContinueWatchingPlayManually = onContinueWatchingPlayManually,
        onChangeContinueWatchingHeroArtwork = onChangeContinueWatchingHeroArtwork,
        onDeleteContinueWatchingFreebox = onDeleteContinueWatchingFreebox,
        onMarkContinueWatchingUnwatched = onMarkContinueWatchingUnwatched,
        showContinueWatchingManualPlayOption = showContinueWatchingManualPlayOption,
        onNavigateToCatalogSeeAll = onNavigateToCatalogSeeAll,
        onNavigateToFolderDetail = onNavigateToFolderDetail,
        onMarkContinueWatchingWatched = remember(viewModel) {
            { item -> viewModel.onEvent(HomeEvent.OnMarkContinueWatchingWatched(item)) }
        },
        onRemoveContinueWatching = remember(viewModel) {
            { contentId, season, episode, isNextUp ->
                viewModel.onEvent(HomeEvent.OnRemoveContinueWatching(contentId, season, episode, isNextUp))
            }
        },
        isCatalogItemWatched = isCatalogItemWatched,
        onCatalogItemLongPress = onCatalogItemLongPress,
        onItemFocus = remember(viewModel) {
            { item ->
                viewModel.onItemFocus(item)
            }
        },
        onSaveGridFocusState = remember(viewModel) {
            { vi, vo, key ->
                viewModel.saveGridFocusState(vi, vo, focusedItemKey = key)
            }
        }
    )
}

@Composable
private fun ModernHomeRoute(
    viewModel: HomeViewModel,
    uiState: HomeUiState,
    onNavigateToDetail: (String, String, String) -> Unit,
    onContinueWatchingClick: (ContinueWatchingItem) -> Unit,
    onContinueWatchingStartFromBeginning: (ContinueWatchingItem) -> Unit,
    onContinueWatchingPlayManually: (ContinueWatchingItem) -> Unit,
    onChangeContinueWatchingHeroArtwork: (ContinueWatchingItem) -> Unit,
    onDeleteContinueWatchingFreebox: (ContinueWatchingItem.InProgress) -> Unit,
    onMarkContinueWatchingUnwatched: (ContinueWatchingItem.InProgress) -> Unit,
    showContinueWatchingManualPlayOption: Boolean,
    onNavigateToFolderDetail: (String, String) -> Unit = { _, _ -> },
    isCatalogItemWatched: (MetaPreview) -> Boolean,
    onCatalogItemLongPress: (MetaPreview, String, Boolean) -> Unit
) {
    val focusState by viewModel.focusState.collectAsStateWithLifecycle()
    val scrollToTopTrigger by viewModel.scrollToTopTrigger.collectAsStateWithLifecycle()
    val modernPresentation by viewModel.modernHomePresentation.collectAsStateWithLifecycle()
    // Passed down as State, not values: enrichment changes on most focus moves, and reading it
    // here recomposed all of the home content for each one.
    val enrichingItemId = viewModel.enrichingItemId.collectAsStateWithLifecycle()
    val enrichedPreviews = viewModel.enrichedPreviews.collectAsStateWithLifecycle()
    val failedEnrichmentIds = viewModel.failedEnrichmentIds.collectAsStateWithLifecycle()
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
        { vi: Int, vo: Int, rk: String?, ikm: Map<String, String>, m: Map<String, Int>, ma: Map<String, String>, ri: Int, ii: Int ->
            viewModel.saveFocusState(vi, vo, rk, ikm, m, ma, ri, ii)
            // Authoritative: this is the row that actually held focus when Home went away.
            viewModel.setLiveFocusedRowKey(rk)
        }
    }
    val preloadAdjacentItem = remember(viewModel) {
        { item: MetaPreview ->
            viewModel.preloadAdjacentItem(item)
        }
    }
    ModernHomeContent(
        uiState = uiState,
        modernPresentation = modernPresentation,
        focusState = focusState,
        scrollToTopTrigger = scrollToTopTrigger,
        enrichingItemId = enrichingItemId,
        enrichedPreviews = enrichedPreviews,
        failedEnrichmentIds = failedEnrichmentIds,
        trailerPreviewUrls = viewModel.trailerPreviewUrls,
        trailerPreviewAudioUrls = viewModel.trailerPreviewAudioUrls,
        onNavigateToDetail = onNavigateToDetail,
        onContinueWatchingClick = onContinueWatchingClick,
        onContinueWatchingStartFromBeginning = onContinueWatchingStartFromBeginning,
        onContinueWatchingPlayManually = onContinueWatchingPlayManually,
        onChangeContinueWatchingHeroArtwork = onChangeContinueWatchingHeroArtwork,
        onDeleteContinueWatchingFreebox = onDeleteContinueWatchingFreebox,
        onMarkContinueWatchingUnwatched = onMarkContinueWatchingUnwatched,
        showContinueWatchingManualPlayOption = showContinueWatchingManualPlayOption,
        onRequestTrailerPreview = requestTrailerPreview,
        onLoadMoreCatalog = loadMoreCatalog,
        onMarkContinueWatchingWatched = remember(viewModel) {
            { item -> viewModel.onEvent(HomeEvent.OnMarkContinueWatchingWatched(item)) }
        },
        onRemoveContinueWatching = removeContinueWatching,
        isCatalogItemWatched = isCatalogItemWatched,
        onCatalogItemLongPress = onCatalogItemLongPress,
        onNavigateToFolderDetail = onNavigateToFolderDetail,
        onItemFocus = remember(viewModel) {
            { item -> viewModel.onItemFocus(item) }
        },
        onFocusedHomeItemChanged = remember(viewModel) {
            { item -> viewModel.onFocusedHomeItemChanged(item) }
        },
        onPreloadAdjacentItem = preloadAdjacentItem,
        onSaveFocusState = saveModernFocusState,
        onFocusedRowKeyChanged = remember(viewModel) {
            { key: String? -> viewModel.setLiveFocusedRowKey(key) }
        },
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
    isFavoriteHomeRow: Boolean = false,
    hasResumeProgress: Boolean = false,
    onDismiss: () -> Unit,
    onDetails: () -> Unit,
    onRestartFromBeginning: () -> Unit = {},
    onChangePortrait: () -> Unit = {},
    onChangeLandscape: () -> Unit = {},
    onToggleLibrary: () -> Unit,
    onToggleWatched: () -> Unit
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
        if (isFavoriteHomeRow) {
            if (isMovie || isSeries) {
                Button(
                    onClick = onToggleWatched,
                    enabled = !isWatchedPending,
                    modifier = Modifier.fillMaxWidth().focusRequester(primaryFocusRequester),
                    colors = ButtonDefaults.colors(
                        containerColor = NuvioTheme.colors.BackgroundCard,
                        contentColor = NuvioTheme.colors.TextPrimary
                    )
                ) {
                    Text(if (isWatched) stringResource(R.string.hero_mark_unwatched) else stringResource(R.string.hero_mark_watched))
                }
            }
            if (hasResumeProgress) {
                Button(
                    onClick = onRestartFromBeginning,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.colors(
                        containerColor = NuvioTheme.colors.BackgroundCard,
                        contentColor = NuvioTheme.colors.TextPrimary
                    )
                ) { Text(stringResource(R.string.cw_action_start_from_beginning)) }
            }
            Button(
                onClick = onChangePortrait,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.colors(
                    containerColor = NuvioTheme.colors.BackgroundCard,
                    contentColor = NuvioTheme.colors.TextPrimary
                )
            ) { Text(stringResource(R.string.home_freebox_change_portrait)) }
            Button(
                onClick = onChangeLandscape,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.colors(
                    containerColor = NuvioTheme.colors.BackgroundCard,
                    contentColor = NuvioTheme.colors.TextPrimary
                )
            ) { Text(stringResource(R.string.home_freebox_change_landscape)) }
            Button(
                onClick = onToggleLibrary,
                enabled = !isLibraryPending,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.colors(
                    containerColor = NuvioTheme.colors.BackgroundCard,
                    contentColor = NuvioTheme.colors.TextPrimary
                )
            ) { Text(stringResource(R.string.home_poster_remove_from_favorites)) }
        } else {
            Button(
                onClick = onDetails,
                modifier = Modifier.fillMaxWidth().focusRequester(primaryFocusRequester),
                colors = ButtonDefaults.colors(
                    containerColor = NuvioTheme.colors.BackgroundCard,
                    contentColor = NuvioTheme.colors.TextPrimary
                )
            ) { Text(stringResource(R.string.cw_action_go_to_details)) }

            Button(
                onClick = onToggleLibrary,
                enabled = !isLibraryPending,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.colors(
                    containerColor = NuvioTheme.colors.BackgroundCard,
                    contentColor = NuvioTheme.colors.TextPrimary
                )
            ) {
                Text(
                    if (showManageLists) stringResource(R.string.library_manage_lists)
                    else if (isInLibrary) stringResource(R.string.home_poster_remove_from_favorites)
                    else stringResource(R.string.home_poster_add_to_favorites)
                )
            }

            if (isMovie || isSeries) {
                Button(
                    onClick = onToggleWatched,
                    enabled = !isWatchedPending,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.colors(
                        containerColor = NuvioTheme.colors.BackgroundCard,
                        contentColor = NuvioTheme.colors.TextPrimary
                    )
                ) {
                    Text(if (isWatched) stringResource(R.string.hero_mark_unwatched) else stringResource(R.string.hero_mark_watched))
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FreeboxFolderOptionsDialog(
    title: String,
    isBusy: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onChangePortrait: () -> Unit,
    onChangeLandscape: () -> Unit,
    onOpen: () -> Unit,
    onRemoveFromHome: () -> Unit,
    onDeleteFromFreebox: () -> Unit,
) {
    val primaryFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { primaryFocusRequester.requestFocus() } }

    NuvioDialog(
        onDismiss = onDismiss,
        title = title,
        subtitle = stringResource(R.string.home_freebox_folder_options_subtitle),
    ) {
        FreeboxFolderActionButton(
            label = stringResource(R.string.home_freebox_change_portrait),
            onClick = onChangePortrait,
            isBusy = isBusy,
            modifier = Modifier.focusRequester(primaryFocusRequester),
        )
        FreeboxFolderActionButton(stringResource(R.string.home_freebox_change_landscape), onChangeLandscape, isBusy)
        FreeboxFolderActionButton(stringResource(R.string.home_freebox_open_folder), onOpen, isBusy)
        FreeboxFolderActionButton(stringResource(R.string.home_freebox_remove_folder), onRemoveFromHome, isBusy)
        FreeboxFolderActionButton(stringResource(R.string.home_freebox_delete_folder), onDeleteFromFreebox, isBusy)
        if (!error.isNullOrBlank()) {
            Text(error, color = NuvioTheme.colors.TextSecondary)
        }
    }
}

@Composable
private fun FreeboxFolderActionButton(
    label: String,
    onClick: () -> Unit,
    isBusy: Boolean,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = !isBusy,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.colors(
            containerColor = NuvioTheme.colors.BackgroundCard,
            contentColor = NuvioTheme.colors.TextPrimary,
        ),
    ) { Text(label) }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FreeboxFolderDeleteDialog(
    title: String,
    error: String?,
    isBusy: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val cancelFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { cancelFocusRequester.requestFocus() } }

    NuvioDialog(
        onDismiss = onDismiss,
        title = stringResource(R.string.home_freebox_delete_title),
        subtitle = stringResource(R.string.home_freebox_delete_message, title),
    ) {
        if (!error.isNullOrBlank()) Text(error, color = NuvioTheme.colors.TextSecondary)
        Button(
            onClick = onDismiss,
            enabled = !isBusy,
            modifier = Modifier.fillMaxWidth().focusRequester(cancelFocusRequester),
            colors = ButtonDefaults.colors(
                containerColor = NuvioTheme.colors.BackgroundCard,
                contentColor = NuvioTheme.colors.TextPrimary,
            ),
        ) { Text(stringResource(R.string.home_freebox_cancel)) }
        Button(
            onClick = onConfirm,
            enabled = !isBusy,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.colors(
                containerColor = NuvioTheme.colors.BackgroundCard,
                contentColor = NuvioTheme.colors.TextPrimary,
            ),
        ) {
            Text(
                if (isBusy) stringResource(R.string.home_freebox_busy)
                else stringResource(R.string.home_freebox_delete_confirm)
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
internal fun FreeboxVideoDeleteDialog(
    title: String,
    error: String?,
    isBusy: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val cancelFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { cancelFocusRequester.requestFocus() } }

    NuvioDialog(
        onDismiss = onDismiss,
        title = stringResource(R.string.cw_freebox_delete_title),
        subtitle = stringResource(R.string.cw_freebox_delete_message, title),
    ) {
        if (!error.isNullOrBlank()) Text(error, color = NuvioTheme.colors.TextSecondary)
        Button(
            onClick = onDismiss,
            enabled = !isBusy,
            modifier = Modifier.fillMaxWidth().focusRequester(cancelFocusRequester),
            colors = ButtonDefaults.colors(
                containerColor = NuvioTheme.colors.BackgroundCard,
                contentColor = NuvioTheme.colors.TextPrimary,
            ),
        ) { Text(stringResource(R.string.home_freebox_cancel)) }
        Button(
            onClick = onConfirm,
            enabled = !isBusy,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.colors(
                containerColor = NuvioTheme.colors.BackgroundCard,
                contentColor = NuvioTheme.colors.TextPrimary,
            ),
        ) {
            Text(if (isBusy) stringResource(R.string.home_freebox_busy) else stringResource(R.string.cw_freebox_delete_confirm))
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
internal fun HomeArtworkPickerDialog(
    title: String? = null,
    subtitle: String? = null,
    emptyMessage: String? = null,
    resetLabel: String? = null,
    portrait: Boolean,
    candidates: List<String>,
    candidateDetails: List<TmdbArtworkCandidate> = emptyList(),
    isLoading: Boolean,
    isBusy: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSelect: ((String) -> Unit)? = null,
    onSelectWithDetails: ((String, TmdbArtworkCandidate?) -> Unit)? = null,
    onReset: () -> Unit,
) {
    val maxDialogWidth = (LocalConfiguration.current.screenWidthDp.dp - 64.dp).coerceAtLeast(480.dp)
    val artworkDialogWidth = minOf(if (portrait) 900.dp else 960.dp, maxDialogWidth)
    val dialogTitle = title ?: if (portrait) {
        stringResource(R.string.home_freebox_artwork_portrait_title)
    } else {
        stringResource(R.string.home_freebox_artwork_landscape_title)
    }
    val primaryFocusRequester = remember { FocusRequester() }
    LaunchedEffect(isLoading, candidates.size) {
        if (!isLoading && candidates.isNotEmpty()) runCatching { primaryFocusRequester.requestFocus() }
    }

    NuvioDialog(
        onDismiss = onDismiss,
        title = dialogTitle,
        subtitle = subtitle ?: stringResource(R.string.home_freebox_artwork_subtitle),
        width = artworkDialogWidth,
        usePlatformDefaultWidth = false,
    ) {
        when {
            isLoading -> LoadingIndicator()
            candidates.isEmpty() -> Text(
                error ?: emptyMessage ?: stringResource(R.string.home_freebox_artwork_empty),
                color = NuvioTheme.colors.TextSecondary,
            )
            else -> LazyRow(
                modifier = Modifier.fillMaxWidth().height(if (portrait) 244.dp else 190.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(candidates, key = { index, url -> "$index:$url" }) { index, imageUrl ->
                    var isFocused by remember(imageUrl) { mutableStateOf(false) }
                    val candidateDetail = candidateDetails.getOrNull(index)
                        ?.takeIf { it.imageUrl == imageUrl }
                    val candidateLabel = if (candidateDetail == null) {
                        stringResource(R.string.home_freebox_artwork_candidate, index + 1)
                    } else {
                        val typeLabel = stringResource(
                            if (candidateDetail.match.contentType == ContentType.SERIES) {
                                R.string.home_freebox_artwork_candidate_series
                            } else {
                                R.string.home_freebox_artwork_candidate_movie
                            }
                        )
                        val candidateTitle = candidateDetail.match.title?.takeIf(String::isNotBlank)
                        when {
                            candidateTitle == null -> stringResource(
                                R.string.home_freebox_artwork_candidate,
                                index + 1,
                            )
                            candidateDetail.match.releaseYear != null -> stringResource(
                                R.string.home_freebox_artwork_candidate_identity,
                                typeLabel,
                                candidateDetail.match.releaseYear,
                                candidateTitle,
                            )
                            else -> stringResource(
                                R.string.home_freebox_artwork_candidate_identity_no_year,
                                typeLabel,
                                candidateTitle,
                            )
                        }
                    }
                    val candidateShape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))
                    Button(
                        onClick = {
                            if (onSelectWithDetails != null) {
                                onSelectWithDetails(imageUrl, candidateDetail)
                            } else {
                                onSelect?.invoke(imageUrl)
                            }
                        },
                        enabled = !isBusy,
                        modifier = Modifier
                            .width(if (portrait) 150.dp else 220.dp)
                            .height(if (portrait) 230.dp else 180.dp)
                            .onFocusChanged { isFocused = it.isFocused }
                            .then(if (index == 0) Modifier.focusRequester(primaryFocusRequester) else Modifier),
                        shape = ButtonDefaults.shape(shape = candidateShape),
                        scale = ButtonDefaults.scale(focusedScale = 1f),
                        colors = ButtonDefaults.colors(
                            containerColor = Color.Transparent,
                            contentColor = NuvioTheme.colors.TextPrimary,
                            focusedContainerColor = Color.Transparent,
                            focusedContentColor = NuvioTheme.colors.TextPrimary,
                        ),
                        border = ButtonDefaults.border(
                            border = androidx.tv.material3.Border(
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Transparent),
                                shape = candidateShape
                            ),
                            focusedBorder = androidx.tv.material3.Border(
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Transparent),
                                shape = candidateShape
                            )
                        ),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(if (portrait) 112.dp else 188.dp)
                                    .height(if (portrait) 160.dp else 112.dp)
                            ) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = stringResource(R.string.home_freebox_artwork_candidate, index + 1),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(NuvioTheme.configuredShape(RoundedCornerShape(10.dp))),
                                )
                                if (isFocused) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .border(2.dp, Color.White, NuvioTheme.configuredShape(RoundedCornerShape(10.dp)))
                                    )
                                }
                            }
                            Text(
                                candidateLabel,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
        if (!isLoading && !error.isNullOrBlank() && candidates.isNotEmpty()) {
            Text(error, color = NuvioTheme.colors.TextSecondary)
        }
        Button(
            onClick = onReset,
            enabled = !isBusy,
            colors = ButtonDefaults.colors(
                containerColor = NuvioTheme.colors.BackgroundCard,
                contentColor = NuvioTheme.colors.TextPrimary,
            ),
        ) { Text(resetLabel ?: stringResource(R.string.home_freebox_artwork_reset)) }
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
                val titleText = if (selected) "\u2713 ${tab.localizedMembershipTitle()}" else tab.localizedMembershipTitle()
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
                        containerColor = if (selected) NuvioTheme.colors.FocusBackground else NuvioTheme.colors.BackgroundCard,
                        contentColor = NuvioTheme.colors.TextPrimary
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

        Divider(color = NuvioTheme.colors.Border, thickness = NuvioTheme.spacing.hairline)

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Button(
                onClick = onSave,
                enabled = !isPending,
                colors = ButtonDefaults.colors(
                    containerColor = NuvioTheme.colors.BackgroundCard,
                    contentColor = NuvioTheme.colors.TextPrimary
                )
            ) {
                Text(if (isPending) stringResource(R.string.action_saving) else stringResource(R.string.action_save))
            }
        }
    }
}
