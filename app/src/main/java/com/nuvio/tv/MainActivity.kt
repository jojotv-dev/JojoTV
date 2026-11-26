package com.nuvio.tv

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.nuvio.tv.ui.screens.ambient.AmbientIdleController
import com.nuvio.tv.ui.screens.ambient.AmbientModeHost
import com.nuvio.tv.ui.screens.ambient.LocalAmbientIdleController
import com.nuvio.tv.ui.components.LocalStartupLoadingState
import com.nuvio.tv.ui.components.LocalStartupSplashEnabled
import com.nuvio.tv.ui.components.StartupLoadingState
import com.nuvio.tv.ui.components.StartupDestination
import com.nuvio.tv.ui.components.shouldShowStartupSplash
import com.nuvio.tv.ui.components.startupDestinationForRoute
import com.nuvio.tv.core.runtime.PluginRuntimeHooks
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.ConfigurationCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.lifecycle.lifecycleScope
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.PerformanceMetricsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import androidx.tv.material3.contentColorFor
import androidx.tv.material3.rememberDrawerState
import com.nuvio.tv.R
import com.nuvio.tv.core.auth.AuthManager
import com.nuvio.tv.core.auth.DeviceSessionRegistration
import com.nuvio.tv.core.deeplink.DeepLinkHandler
import com.nuvio.tv.core.deeplink.DeepLinkParser
import com.nuvio.tv.core.launcher.HomeForegroundDelivery
import com.nuvio.tv.core.launcher.HomeKeyGesture
import com.nuvio.tv.core.launcher.HomeKeyPress
import com.nuvio.tv.core.launcher.LauncherSetup
import com.nuvio.tv.core.launcher.LauncherHomeRequest
import com.nuvio.tv.core.shizuku.ShizukuStartup
import com.nuvio.tv.core.player.PlayerWindowBackdrop
import com.nuvio.tv.core.profile.ProfileManager
import com.nuvio.tv.core.sync.ProfileSyncService
import com.nuvio.tv.core.sync.StartupSyncService
import com.nuvio.tv.core.tracking.TrackingProgressRefreshCoordinator
import com.nuvio.tv.core.tracking.TrackingRefreshIntent
import com.nuvio.tv.data.local.AppOnboardingDataStore
import com.nuvio.tv.data.local.AuthSessionNoticeDataStore
import com.nuvio.tv.data.local.ExperienceModeDataStore
import com.nuvio.tv.data.local.LayoutPreferenceDataStore
import com.nuvio.tv.data.local.ProfileDataStore
import com.nuvio.tv.data.local.StartupAuthNotice
import com.nuvio.tv.data.local.ThemeDataStore
import com.nuvio.tv.data.repository.MemberAccessRepository
import com.nuvio.tv.data.remote.supabase.AvatarRepository
import com.nuvio.tv.domain.model.AppFont
import com.nuvio.tv.domain.model.AppTheme
import com.nuvio.tv.domain.model.CustomThemeColors
import com.nuvio.tv.domain.model.AuthState
import com.nuvio.tv.domain.model.CardDepthStyle
import com.nuvio.tv.domain.model.CosmeticEntitlement
import com.nuvio.tv.domain.model.DiscoverLocation
import com.nuvio.tv.domain.model.ExperienceMode
import com.nuvio.tv.domain.model.MemberAccess
import com.nuvio.tv.domain.model.ProfileBackgroundSelection
import com.nuvio.tv.domain.model.ProfileHomePresentation
import com.nuvio.tv.domain.model.resolveProfileBackgroundSelection
import com.nuvio.tv.domain.model.SettingsUiStyle
import com.nuvio.tv.domain.model.resolveAppTheme
import com.nuvio.tv.domain.model.resolveCustomThemeColors
import com.nuvio.tv.domain.deeplink.AppDeepLink
import com.nuvio.tv.domain.repository.AddonRepository
import com.nuvio.tv.ui.components.NuvioScrollDefaults
import com.nuvio.tv.ui.components.BrandWordmark
import com.nuvio.tv.ui.components.LocalCardDepthStyle
import com.nuvio.tv.ui.components.LocalLandscapePosterMode
import com.nuvio.tv.ui.components.LocalAlwaysBackdropWithLogo
import com.nuvio.tv.ui.components.ProfileAvatarCircle
import com.nuvio.tv.ui.navigation.LocalNavViewModelStoreOwner
import com.nuvio.tv.ui.navigation.NuvioNavHost
import com.nuvio.tv.ui.navigation.Screen
import com.nuvio.tv.ui.navigation.rememberNavSessionViewModelStoreOwner
import com.nuvio.tv.ui.navigation.showApkInstallResult
import com.nuvio.tv.ui.membership.LocalMemberAccess
import com.nuvio.tv.ui.screens.account.AuthQrSignInScreen
import com.nuvio.tv.ui.screens.addon.EssentialAddonSetupScreen
import com.nuvio.tv.ui.screens.profile.ProfileSelectionScreen
import com.nuvio.tv.ui.screens.settings.FreeboxUpdateBannerHost
import com.nuvio.tv.ui.screens.settings.FreeboxUpdateViewModel
import com.nuvio.tv.ui.screens.settings.installFreeboxUpdateApk
import com.nuvio.tv.ui.theme.NuvioComponents
import com.nuvio.tv.ui.theme.NuvioLayout
import com.nuvio.tv.ui.theme.NuvioMotion
import com.nuvio.tv.ui.theme.NuvioPrimitives
import com.nuvio.tv.ui.theme.NuvioRadii
import com.nuvio.tv.ui.theme.NuvioStrokes
import com.nuvio.tv.ui.theme.NuvioTheme
import com.nuvio.tv.ui.theme.ThemeColors
import com.nuvio.tv.ui.util.LocalFastHorizontalNavigationEnabled
import com.nuvio.tv.ui.util.LocalRecompositionHighlighterEnabled
import com.nuvio.tv.ui.util.rememberDrawerItemFocusRequesters
import com.nuvio.tv.ui.util.rememberRawSvgPainter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val LocalSidebarExpanded = compositionLocalOf { false }
val LocalOpenSidebar = compositionLocalOf<() -> Unit> { {} }
val LocalContentFocusRequester = compositionLocalOf { FocusRequester.Default }

data class SplashBackground(
    val profileColorHex: String? = null,
    val backgroundUrl: String? = null,
    val backgroundCacheKey: String? = null,
    val skipGradient: Boolean = false
)
val LocalSplashBackground = compositionLocalOf { SplashBackground() }

private const val SIDEBAR_AUTO_COLLAPSE_DELAY_MS = 3_000L

private const val MAX_SUPPORTED_FONT_SCALE = 1.15f

/** Idle time before Nuvio hands the TV back to the profile picker. */
private const val PROFILE_SWITCH_IDLE_TIMEOUT_MS = 30L * 60L * 1000L

/** How long after boot Nuvio treats a launch as "the TV was just turned on". */
private const val PROFILE_SWITCH_BOOT_WINDOW_MS = 3L * 60L * 1000L

data class DrawerItem(
    val route: String,
    val label: String,
    val iconRes: Int? = null,
    val icon: ImageVector? = null
)

private data class MainUiPrefs(
    val theme: AppTheme = AppTheme.WHITE,
    val customThemeColors: CustomThemeColors = CustomThemeColors.Default,
    val memberAccess: MemberAccess = MemberAccess.None,
    val font: AppFont = AppFont.INTER,
    val amoledMode: Boolean = false,
    val amoledSurfacesMode: Boolean = false,
    val hasChosenLayout: Boolean? = null,
    val experienceMode: ExperienceMode? = null,
    val experienceModeLoaded: Boolean = false,
    val addonSetupSkipped: Boolean = false,
    val sidebarCollapsed: Boolean = false,
    val modernSidebarEnabled: Boolean = false,
    val modernSidebarBlurPref: Boolean = false,
    val discoverLocation: DiscoverLocation? = null,
    val smoothBringIntoViewEnabled: Boolean = true,
    val fastHorizontalNavigationEnabled: Boolean = false,
    val composeHighlighterEnabled: Boolean = false,
    val settingsUiStyle: SettingsUiStyle = SettingsUiStyle.CLASSIC,
    val cardDepthStyle: CardDepthStyle = CardDepthStyle(),
    val sidebarItemCornerRadiusDp: Int = 12,
    val landscapePosterMode: Boolean = false,
    val alwaysBackdropWithLogo: Boolean = false
)

@AndroidEntryPoint
open class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themeDataStore: ThemeDataStore

    @Inject
    lateinit var layoutPreferenceDataStore: LayoutPreferenceDataStore

    @Inject
    lateinit var experienceModeDataStore: ExperienceModeDataStore

    @Inject
    lateinit var memberAccessRepository: MemberAccessRepository

    @Inject
    lateinit var addonRepository: AddonRepository

    @Inject
    lateinit var trackingProgressRefreshCoordinator: TrackingProgressRefreshCoordinator

    @Inject
    lateinit var startupSyncService: StartupSyncService

    @Inject
    lateinit var myListsTvSyncManager: com.nuvio.tv.core.mylists.MyListsTvSyncManager

    @Inject
    lateinit var androidTvChannelSyncService: com.nuvio.tv.core.sync.androidtv.AndroidTvChannelSyncService

    @Inject
    lateinit var profileSyncService: ProfileSyncService

    @Inject
    lateinit var profileManager: ProfileManager

    @Inject
    lateinit var profileDataStore: ProfileDataStore

    @Inject
    lateinit var authManager: AuthManager

    @Inject
    lateinit var deviceSessionRegistration: DeviceSessionRegistration

    @Inject
    lateinit var authSessionNoticeDataStore: AuthSessionNoticeDataStore

    @Inject
    lateinit var appOnboardingDataStore: AppOnboardingDataStore

    @Inject
    lateinit var avatarRepository: AvatarRepository

    @Inject
    lateinit var profileBackgroundRepository: com.nuvio.tv.data.remote.supabase.ProfileBackgroundRepository

    @Inject
    lateinit var trailerPlayerPool: com.nuvio.tv.core.player.TrailerPlayerPool

    @Inject
    lateinit var externalPlaybackTracker: com.nuvio.tv.core.player.ExternalPlaybackTracker

    @Inject
    lateinit var deepLinkHandler: DeepLinkHandler

    @Inject
    lateinit var calendarRepository: com.nuvio.tv.calendar.CalendarRepository

    @Inject
    lateinit var calendarPreferences: com.nuvio.tv.calendar.CalendarPreferences

    @Inject
    lateinit var sportsRepository: com.nuvio.tv.livetv.sports.SportsRepository

    @Inject
    lateinit var liveTvRecordingScheduler: com.nuvio.tv.livetv.record.LiveTvRecordingScheduler

    private val pendingDeepLinkUrl = MutableStateFlow<String?>(null)
    private val pendingLaunchIntent = MutableStateFlow<Intent?>(null)

    /**
     * Incremented on every HOME request (device HOME key via [com.nuvio.tv.core.launcher.LauncherAccessibilityService]
     * or a plain HOME-category launch). Navigation observes it and returns to the top of Home.
     */
    private val pendingHomeRequestNonce = MutableStateFlow(0)

    /** Set when the user double-taps HOME; consumed once the profile switcher is shown. */
    private val pendingProfileSwitchRequest = MutableStateFlow(false)

    /** Single vs. double tap detection for HOME pressed inside the app. */
    private val homeKeyGesture = HomeKeyGesture()

    /**
     * True when HOME should be handled inside the app: either Nuvio is the resolved HOME activity, or
     * the launcher accessibility service is on (the user chose "make the Home button open the app").
     * Otherwise HOME is left alone so it still reaches the real launcher.
     */
    private var nuvioHandlesHomeKey = false

    private lateinit var jankStats: JankStats

    /** Activity-level launcher for external video players. Survives all navigation changes. */
    private val externalPlayerLauncher = registerForActivityResult(
        com.nuvio.tv.core.player.ExternalPlayerResultContract()
    ) { result ->
        Log.d("MainActivity", "External player ActivityResult: $result")
        externalPlaybackTracker.onActivityResult(result)
    }

    /** True until the first onResume after onCreate completes. */
    private var isFirstResumeAfterCreate = false

    @OptIn(ExperimentalTvMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun attachBaseContext(newBase: Context) {
        val tag = LocaleCache.localeTag.takeIf { it != LocaleCache.UNSET }

        if (!tag.isNullOrEmpty()) {
            val locale = Locale.forLanguageTag(tag)
            Locale.setDefault(locale)
            val config = Configuration(newBase.resources.configuration)
            config.setLocale(locale)
            super.attachBaseContext(newBase.createConfigurationContext(config))
        } else {
            // Cache not ready yet (very early cold start) — use system locale
            // The IO coroutine in Application.onCreate will finish before any activity
            // is usually created, but if not, we just use system locale until next launch
            super.attachBaseContext(newBase)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            runCatching { com.nuvio.tv.livetv.startup.LiveTvPreload.start(this@MainActivity) }
        }
        runCatching { liveTvRecordingScheduler.start() }
        isFirstResumeAfterCreate = true
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        // Wire the Activity-level launcher to the tracker
        externalPlaybackTracker.activityLauncher = externalPlayerLauncher

        PluginRuntimeHooks.onActivityCreate(this)
        runCatching {
            com.nuvio.tv.calendar.CalendarHomeFeed.init(this)
            calendarRepository.start()
        }

        window?.decorView?.post {
            val snapshot = com.nuvio.tv.core.player.DisplayCapabilities.detect(this)
            com.nuvio.tv.core.player.DisplayCapabilities.logSummary(snapshot)
        }

        // Extract extras set by the Continue Watching launcher channel preview programs.
        val launchContentId = intent?.getStringExtra("contentId")
        val launchContentType = intent?.getStringExtra("contentType")
        val launchMode = intent?.getStringExtra("launchMode")
        val launchVideoId = intent?.getStringExtra("videoId")
        val launchName = intent?.getStringExtra("name")
        val launchPoster = intent?.getStringExtra("poster")
        val launchBackdrop = intent?.getStringExtra("backdrop")
        val launchLogo = intent?.getStringExtra("logo")
        val launchSeason = intent?.getIntExtra("season", -1)?.takeIf { it >= 0 }
        val launchEpisode = intent?.getIntExtra("episode", -1)?.takeIf { it >= 0 }
        val launchEpisodeTitle = intent?.getStringExtra("episodeTitle")
        captureDeepLinkIntent(intent)
        if (savedInstanceState == null) {
            captureHomeRequestIntent(intent, fromNewIntent = false)
        }
        refreshHomeRole()

        setContent {
            var hasSelectedProfileThisSession by rememberSaveable { mutableStateOf(false) }
            var startupSession by remember { mutableIntStateOf(0) }
            // Keep this outside the content branch, which is disposed while the picker is shown.
            var bootSwitchHandled by rememberSaveable { mutableStateOf(false) }
            // Triggered immediately on profile click (before system confirms selection).
            var splashTriggered by remember { mutableStateOf(false) }
            // Overrides splash background with the currently focused profile
            // so the splash matches the profile selection background.
            var focusedSplashColor by remember { mutableStateOf<String?>(null) }
            var focusedSplashBgUrl by remember { mutableStateOf<String?>(null) }
            var focusedSplashCacheKey by remember { mutableStateOf<String?>(null) }
            var focusedSplashTheme by remember { mutableStateOf<AppTheme?>(null) }
            var onboardingCompletedThisSession by remember { mutableStateOf(false) }
            var onboardingProfileSyncInProgress by remember { mutableStateOf(false) }
            val hasSeenAuthQrFlow = remember(appOnboardingDataStore) {
                appOnboardingDataStore.hasSeenAuthQrOnFirstLaunch.map<Boolean, Boolean?> { it }
            }
            val hasSeenAuthQrOnFirstLaunch by hasSeenAuthQrFlow.collectAsState(initial = null)
            val authState by authManager.authState.collectAsState()
            val context = LocalContext.current
            var pendingApkInstallName by rememberSaveable { mutableStateOf<String?>(null) }
            val apkInstallLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
            ) { result ->
                val apkName = pendingApkInstallName ?: "l’APK"
                pendingApkInstallName = null
                showApkInstallResult(context, apkName, result.resultCode)
            }

            LaunchedEffect(authSessionNoticeDataStore, context) {
                authSessionNoticeDataStore.pendingNotice.collect { notice ->
                    if (notice == StartupAuthNotice.NUVIO) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.auth_notice_nuvio_logged_out),
                            Toast.LENGTH_LONG
                        ).show()
                        authSessionNoticeDataStore.consumeNotice(notice)
                    }
                }
            }

            val activeProfileId by profileManager.activeProfileId.collectAsState()
            val activeHomePresentationEntry by profileDataStore.activeProfileHomePresentation.collectAsState(
                initial = -1 to ProfileHomePresentation.ORIGINAL
            )
            val activeHomePresentation = activeHomePresentationEntry
                .takeIf { it.first == activeProfileId }
                ?.second
            val startupSplashEnabled by profileManager.startupSplashEnabled.collectAsState()
            val startupLoadingState = remember(activeProfileId, startupSession) { StartupLoadingState() }
            val profiles by profileManager.profiles.collectAsState()
            val hasEverSelectedProfile by profileManager.hasEverSelectedProfile.collectAsState()
            val rememberLastProfileEnabled by profileManager.rememberLastProfileEnabled.collectAsState()
            val activeProfile = remember(activeProfileId, profiles) {
                profiles.firstOrNull { it.id == activeProfileId }
            }
            var profilePinStates by remember { mutableStateOf<Map<Int, Boolean>>(emptyMap()) }

            LaunchedEffect(authState, profiles) {
                if (authState is AuthState.FullAccount) {
                    profileSyncService.pullProfileLockStates()
                        .onSuccess { profilePinStates = it }
                        .onFailure { profilePinStates = emptyMap() }
                } else {
                    profilePinStates = emptyMap()
                }
            }

            val activeProfileHasPin = remember(activeProfileId, profilePinStates) {
                profilePinStates[activeProfileId] == true
            }

            var profileSwitchedManually by remember { mutableStateOf(false) }
            val shouldAutoSelectProfile = activeHomePresentation == ProfileHomePresentation.ORIGINAL &&
                rememberLastProfileEnabled &&
                hasEverSelectedProfile && !activeProfileHasPin &&
                !hasSelectedProfileThisSession && !profileSwitchedManually
            if (shouldAutoSelectProfile && !splashTriggered) {
                splashTriggered = true
                hasSelectedProfileThisSession = true
            }
            LaunchedEffect(shouldAutoSelectProfile) {
                if (shouldAutoSelectProfile) {
                    if (authManager.authState.value is AuthState.FullAccount) {
                        startupSyncService.requestSyncNow()
                    }
                    // Preload profile background catalog so the splash can show it
                    activeProfile?.profileBackgroundId?.let {
                        profileBackgroundRepository.loadSelectedAndPreload(it)
                    }
                }
            }

            var avatarCatalog by remember { mutableStateOf(emptyList<com.nuvio.tv.data.remote.supabase.AvatarCatalogItem>()) }
            val avatarMemberAccess by memberAccessRepository.access.collectAsState()
            val hasProfileAvatarAccess = avatarMemberAccess.entitlements
                .includes(CosmeticEntitlement.PROFILE_AVATARS)

            LaunchedEffect(hasProfileAvatarAccess) {
                avatarCatalog = runCatching {
                    avatarRepository.getAvatarCatalog(hasProfileAvatarAccess)
                }
                    .getOrDefault(emptyList())
            }

            val activeProfileAvatarImageUrl = remember(activeProfile, avatarCatalog) {
                activeProfile?.avatarUrl?.takeIf { it.isNotBlank() }
                    ?: activeProfile?.avatarId?.let { avatarRepository.getAvatarImageUrl(it, avatarCatalog) }
            }

            val mainUiPrefsFlow = remember(
                activeProfileId,
                startupSession,
                themeDataStore,
                layoutPreferenceDataStore,
                experienceModeDataStore,
                memberAccessRepository
            ) {
                val activeThemeFlow = combine(
                    themeDataStore.themeSelection,
                    memberAccessRepository.access
                ) { selection, memberAccess ->
                    selection.copy(
                        theme = resolveAppTheme(selection.theme, memberAccess.entitlements),
                        customColors = resolveCustomThemeColors(selection.customColors, memberAccess.tier)
                    ) to memberAccess
                }
                // Group flows into two batches to reduce intermediate flow allocations.
                // Each batch uses a single combine() instead of chaining .combine() calls,
                // which avoids N intermediate flow objects and redundant emissions on startup.
                val themeAndExperienceFlow = combine(
                    activeThemeFlow,
                    themeDataStore.selectedFont,
                    themeDataStore.amoledMode,
                    themeDataStore.amoledSurfacesMode,
                    experienceModeDataStore.mode,
                ) { themeAndAccess, font, amoledMode, amoledSurfacesMode, experienceMode ->
                    MainUiPrefs(
                        theme = themeAndAccess.first.theme ?: AppTheme.WHITE,
                        customThemeColors = themeAndAccess.first.customColors,
                        memberAccess = themeAndAccess.second,
                        font = font,
                        amoledMode = amoledMode,
                        amoledSurfacesMode = amoledSurfacesMode,
                        experienceMode = experienceMode,
                        experienceModeLoaded = true,
                    )
                }
                val layoutAndFeaturesFlow = combine(
                    layoutPreferenceDataStore.hasChosenLayout,
                    layoutPreferenceDataStore.sidebarCollapsedByDefault,
                    layoutPreferenceDataStore.modernSidebarEnabled,
                    layoutPreferenceDataStore.modernSidebarBlurEnabled,
                    layoutPreferenceDataStore.discoverLocation,
                ) { hasChosenLayout, sidebarCollapsed, modernSidebarEnabled, modernSidebarBlurPref, discoverLocation ->
                    MainUiPrefs(
                        hasChosenLayout = hasChosenLayout,
                        sidebarCollapsed = sidebarCollapsed,
                        modernSidebarEnabled = modernSidebarEnabled,
                        modernSidebarBlurPref = modernSidebarBlurPref,
                        discoverLocation = discoverLocation,
                    )
                }
                val extraFeaturesFlow = combine(
                    experienceModeDataStore.addonSetupSkipped,
                    layoutPreferenceDataStore.smoothBringIntoViewEnabled,
                    layoutPreferenceDataStore.fastHorizontalNavigationEnabled,
                    layoutPreferenceDataStore.composeHighlighterEnabled,
                    themeDataStore.settingsUiStyle,
                ) { addonSetupSkipped, smoothBringIntoView, fastHorizontalNav, composeHighlighter, settingsUiStyle ->
                    MainUiPrefs(
                        addonSetupSkipped = addonSetupSkipped,
                        smoothBringIntoViewEnabled = smoothBringIntoView,
                        fastHorizontalNavigationEnabled = fastHorizontalNav,
                        composeHighlighterEnabled = composeHighlighter,
                        settingsUiStyle = settingsUiStyle,
                    )
                }
                val landscapePrefsFlow = combine(
                    layoutPreferenceDataStore.modernLandscapePostersEnabled,
                    layoutPreferenceDataStore.alwaysShowLandscapeClearlogo,
                    layoutPreferenceDataStore.posterCardCornerRadiusDp
                ) { landscape, backdropWithLogo, cornerRadius -> Triple(landscape, backdropWithLogo, cornerRadius) }

                combine(
                    themeAndExperienceFlow,
                    layoutAndFeaturesFlow,
                    extraFeaturesFlow,
                    layoutPreferenceDataStore.cardDepthStyle,
                    landscapePrefsFlow
                ) { themePrefs, layoutPrefs, extraPrefs, cardDepthStyle, (landscapePosterMode, alwaysBackdropWithLogo, sidebarItemCornerRadiusDp) ->
                    themePrefs.copy(
                        hasChosenLayout = layoutPrefs.hasChosenLayout,
                        sidebarCollapsed = layoutPrefs.sidebarCollapsed,
                        modernSidebarEnabled = layoutPrefs.modernSidebarEnabled,
                        modernSidebarBlurPref = layoutPrefs.modernSidebarBlurPref,
                        discoverLocation = layoutPrefs.discoverLocation,
                        addonSetupSkipped = extraPrefs.addonSetupSkipped,
                        smoothBringIntoViewEnabled = extraPrefs.smoothBringIntoViewEnabled,
                        fastHorizontalNavigationEnabled = extraPrefs.fastHorizontalNavigationEnabled,
                        composeHighlighterEnabled = extraPrefs.composeHighlighterEnabled,
                        settingsUiStyle = extraPrefs.settingsUiStyle,
                        cardDepthStyle = cardDepthStyle,
                        sidebarItemCornerRadiusDp = sidebarItemCornerRadiusDp,
                        landscapePosterMode = landscapePosterMode,
                        alwaysBackdropWithLogo = alwaysBackdropWithLogo
                    )
                }
            }
            val mainUiPrefs by key(activeProfileId, startupSession) {
                mainUiPrefsFlow.collectAsState(initial = MainUiPrefs(hasChosenLayout = null))
            }
            val installedAddons by key(activeProfileId, startupSession) {
                remember(addonRepository) {
                    addonRepository.getInstalledAddons()
                }.collectAsState(initial = null)
            }
            val discoverLocation = mainUiPrefs.discoverLocation

            NuvioTheme(
                appTheme = mainUiPrefs.theme,
                customThemeColors = mainUiPrefs.customThemeColors,
                appFont = mainUiPrefs.font,
                amoledMode = mainUiPrefs.amoledMode,
                amoledSurfacesMode = mainUiPrefs.amoledSurfacesMode,
                settingsUiStyle = mainUiPrefs.settingsUiStyle,
                cornerRadiusDp = mainUiPrefs.sidebarItemCornerRadiusDp
            ) {
                val defaultBringIntoViewSpec = LocalBringIntoViewSpec.current
                val bringIntoViewSpec = if (mainUiPrefs.smoothBringIntoViewEnabled) {
                    NuvioScrollDefaults.smoothScrollSpec
                } else {
                    defaultBringIntoViewSpec
                }
                val systemDensity = LocalDensity.current
                val clampedFontScaleDensity = remember(systemDensity) {
                    Density(
                        density = systemDensity.density,
                        fontScale = systemDensity.fontScale.coerceAtMost(MAX_SUPPORTED_FONT_SCALE)
                    )
                }
                val highlighterEnabled = BuildConfig.IS_DEBUG_BUILD && mainUiPrefs.composeHighlighterEnabled
                com.nuvio.tv.ui.util.RecompositionHighlighterFlag.enabled = highlighterEnabled
                val profileBgSelection = resolveProfileBackgroundSelection(
                    profile = activeProfile,
                    entitlements = mainUiPrefs.memberAccess.entitlements
                )
                val profileBgCatalog by profileBackgroundRepository.catalog.collectAsState()
                // Cache splash background in SharedPreferences so the splash
                // can show the correct background on cold start when
                // "remember last profile" skips the profile selection screen.
                val splashPrefs = remember {
                    context.getSharedPreferences("startup_splash", android.content.Context.MODE_PRIVATE)
                }
                val splashPreferencesReady = mainUiPrefs.hasChosenLayout != null && mainUiPrefs.experienceModeLoaded
                val splashBackground = remember(
                    profileBgSelection, profileBgCatalog, activeProfile, activeProfileId,
                    focusedSplashColor, focusedSplashBgUrl, focusedSplashCacheKey,
                    focusedSplashTheme, splashPreferencesReady, mainUiPrefs.theme
                ) {
                    val bgUrl = when (profileBgSelection) {
                        is ProfileBackgroundSelection.Custom -> profileBgSelection.url
                        is ProfileBackgroundSelection.Catalog -> {
                            profileBgCatalog.firstOrNull { it.id == profileBgSelection.id }
                                ?.imageFile?.toURI()?.toString()
                        }
                        else -> null
                    }
                    val colorHex = activeProfile?.avatarColorHex
                    // Only overwrite bg_url when we have a real value or when
                    // we know the profile has no background at all.
                    if (splashPreferencesReady && colorHex != null) {
                        splashPrefs.edit().putString("color_$activeProfileId", colorHex).apply()
                    }
                    if (splashPreferencesReady && bgUrl != null) {
                        splashPrefs.edit()
                            .putString("bg_url_$activeProfileId", bgUrl)
                            .apply()
                    } else if (splashPreferencesReady && profileBgSelection == null && activeProfile != null) {
                        splashPrefs.edit()
                            .remove("bg_url_$activeProfileId")
                            .apply()
                    }
                    val activeTheme = mainUiPrefs.theme
                    if (splashPreferencesReady && activeTheme != AppTheme.WHITE) {
                        splashPrefs.edit().putString("theme_$activeProfileId", activeTheme.name).apply()
                    } else if (splashPreferencesReady) {
                        splashPrefs.edit().remove("theme_$activeProfileId").apply()
                    }
                    // Fall back to cached values only on cold start (no focused override)
                    val hasFocusedOverride = focusedSplashColor != null
                    val fallbackColor = if (colorHex == null && !hasFocusedOverride) splashPrefs.getString("color_$activeProfileId", null) else null
                    val fallbackBg = if (
                        bgUrl == null && !hasFocusedOverride && (!splashPreferencesReady || profileBgSelection != null)
                    ) splashPrefs.getString("bg_url_$activeProfileId", null) else null
                    val resolvedBgUrl = if (hasFocusedOverride) focusedSplashBgUrl else (bgUrl ?: fallbackBg)
                    val skipGradient = !hasFocusedOverride && resolvedBgUrl == null &&
                        profileBgSelection is ProfileBackgroundSelection.Catalog
                    // Resolve branded logo: focused theme (profile screen) > active theme > cached theme > default
                    val resolvedTheme = focusedSplashTheme
                        ?: activeTheme.takeIf { splashPreferencesReady }
                        ?: splashPrefs.getString("theme_$activeProfileId", null)
                            ?.let { name -> AppTheme.entries.firstOrNull { it.name == name } }
                        ?: AppTheme.WHITE
                    SplashBackground(
                        profileColorHex = if (hasFocusedOverride) focusedSplashColor else (colorHex ?: fallbackColor),
                        backgroundUrl = resolvedBgUrl,
                        backgroundCacheKey = if (hasFocusedOverride) focusedSplashCacheKey else when (profileBgSelection) {
                            is ProfileBackgroundSelection.Catalog -> profileBgCatalog
                                .firstOrNull { it.id == profileBgSelection.id }
                                ?.let { "profile-background-${it.id}-v${it.assetVersion}" }
                            is ProfileBackgroundSelection.Custom -> "custom-profile-background-${profileBgSelection.url}"
                            null -> null
                        },
                        skipGradient = skipGradient
                    )
                }
                CompositionLocalProvider(
                    LocalDensity provides clampedFontScaleDensity,
                    LocalBringIntoViewSpec provides bringIntoViewSpec,
                    LocalFastHorizontalNavigationEnabled provides mainUiPrefs.fastHorizontalNavigationEnabled,
                    LocalRecompositionHighlighterEnabled provides highlighterEnabled,
                    LocalCardDepthStyle provides mainUiPrefs.cardDepthStyle,
                    LocalLandscapePosterMode provides mainUiPrefs.landscapePosterMode,
                    LocalAlwaysBackdropWithLogo provides mainUiPrefs.alwaysBackdropWithLogo,
                    LocalMemberAccess provides mainUiPrefs.memberAccess,
                    LocalAmbientIdleController provides ambientIdleController,
                    com.nuvio.tv.core.player.LocalTrailerPlayerPool provides trailerPlayerPool,
                    LocalSplashBackground provides splashBackground,
                    LocalStartupLoadingState provides startupLoadingState,
                    LocalStartupSplashEnabled provides startupSplashEnabled
                ) {
                val transparentPlayerBackdrop = PlayerWindowBackdrop.isTransparentRequested
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                    colors = SurfaceDefaults.colors(
                        containerColor = if (transparentPlayerBackdrop) {
                            Color.Transparent
                        } else {
                            NuvioTheme.colors.Background
                        },
                        contentColor = contentColorFor(NuvioTheme.colors.Background)
                    )
                ) {
                    // Wrap everything in a Box. This prevents any black flash between
                    // profile selection and the home content
                    Box(modifier = Modifier.fillMaxSize()) {

                    var startupDestination = StartupDestination.Loading
                    val surfaceContentReady = hasSeenAuthQrOnFirstLaunch != null &&
                        authState !is AuthState.Loading

                    if (!surfaceContentReady) {
                        // Still loading auth state; nothing to show yet.
                    } else if (
                        hasSeenAuthQrOnFirstLaunch == false &&
                        !onboardingCompletedThisSession
                    ) {
                        startupDestination = StartupDestination.Setup
                        AuthQrSignInScreen(
                            onBackPress = { finish() },
                            onContinue = {
                                lifecycleScope.launch {
                                    val shouldRunRemoteOnboardingSync =
                                        authManager.authState.value is AuthState.FullAccount

                                    if (shouldRunRemoteOnboardingSync) {
                                        if (onboardingProfileSyncInProgress) return@launch
                                        onboardingProfileSyncInProgress = true
                                        val maxAttempts = 3
                                        var synced = false
                                        for (attempt in 0 until maxAttempts) {
                                            val result = profileSyncService.pullFromRemote(force = true)
                                            if (result.isSuccess) {
                                                synced = true
                                                break
                                            }
                                            if (attempt < maxAttempts - 1) {
                                                delay(1_000)
                                            }
                                        }
                                        if (!synced) {
                                            android.util.Log.w(
                                                "MainActivity",
                                                "Onboarding profile sync failed after retries; continuing"
                                            )
                                        }
                                    }
                                    appOnboardingDataStore.setHasSeenAuthQrOnFirstLaunch(true)
                                    onboardingCompletedThisSession = true
                                    onboardingProfileSyncInProgress = false
                                }
                                if (authManager.authState.value is AuthState.FullAccount) {
                                    startupSyncService.requestSyncNow()
                                }
                            }
                        )
                    } else {

                    val shouldShowProfileSelection = !hasSelectedProfileThisSession && (
                        activeHomePresentation == ProfileHomePresentation.LAUNCHER ||
                            profiles.size > 1 || activeProfileHasPin || profileSwitchedManually
                        )

                    if (activeHomePresentation == null) {
                        startupDestination = StartupDestination.Loading
                    } else if (shouldShowProfileSelection) {
                        // The switcher is already on screen, so a queued double-tap HOME request is moot.
                        LaunchedEffect(Unit) { pendingProfileSwitchRequest.value = false }
                        startupDestination = if (splashTriggered) StartupDestination.Loading else StartupDestination.ProfileSelection
                        ProfileSelectionScreen(
                            launcherPresentation = activeHomePresentation == ProfileHomePresentation.LAUNCHER,
                            onProfileClicked = {
                                splashTriggered = true
                            },
                            onProfileSelectionFailed = {
                                splashTriggered = false
                            },
                            onProfileFocusChanged = { colorHex, bgUrl, cacheKey ->
                                if (!splashTriggered) {
                                    focusedSplashColor = colorHex
                                    focusedSplashBgUrl = bgUrl
                                    focusedSplashCacheKey = cacheKey
                                }
                            },
                            onProfileThemeFocused = { theme ->
                                if (!splashTriggered) {
                                    focusedSplashTheme = theme
                                }
                            },
                            // This launcher keeps Back inside Nuvio, even on the profile picker.
                            onBackPress = {},
                            onProfileSelected = {
                                bootSwitchHandled = true
                                hasSelectedProfileThisSession = true
                                // Picking a profile restarts the inactivity window, so the picker
                                // cannot reappear the moment it closes.
                                ambientIdleController.onInteraction()
                                if (authManager.authState.value is AuthState.FullAccount) {
                                    startupSyncService.requestSyncNow()
                                }
                            }
                        )
                    } else {

                    val layoutChosen = mainUiPrefs.hasChosenLayout
                    val prefsLoading = layoutChosen == null || !mainUiPrefs.experienceModeLoaded || installedAddons == null
                    if (!prefsLoading) {
                    val effectiveExperienceMode = mainUiPrefs.experienceMode
                        ?: if (layoutChosen) ExperienceMode.ADVANCED else null
                    val needsExperienceSelection = effectiveExperienceMode == null
                    val needsEssentialAddonSetup =
                        effectiveExperienceMode == ExperienceMode.ESSENTIAL &&
                            installedAddons.orEmpty().isEmpty() &&
                            !mainUiPrefs.addonSetupSkipped
                    val pendingDeepLink by pendingDeepLinkUrl.collectAsState()

                    LaunchedEffect(pendingDeepLink) {
                        val url = pendingDeepLink ?: return@LaunchedEffect
                        val deepLink = DeepLinkParser.parse(url)
                        if (deepLink is AppDeepLink.AddonInstall && (needsEssentialAddonSetup || !layoutChosen)) {
                            Toast.makeText(context, context.getString(R.string.addon_installing), Toast.LENGTH_SHORT).show()
                            val installResult = deepLinkHandler.installAddon(deepLink.manifestUrl)
                            if (pendingDeepLinkUrl.value == url) {
                                pendingDeepLinkUrl.value = null
                            }
                            Toast.makeText(context, installResult.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    if (needsEssentialAddonSetup) {
                        startupDestination = StartupDestination.Setup
                        EssentialAddonSetupScreen(
                            onSkip = {
                                lifecycleScope.launch {
                                    experienceModeDataStore.setAddonSetupSkipped(true)
                                }
                            }
                        )
                    } else {
                    val sidebarCollapsed = mainUiPrefs.sidebarCollapsed
                    val modernSidebarEnabled = mainUiPrefs.modernSidebarEnabled
                    val modernSidebarBlurEnabled =
                        mainUiPrefs.modernSidebarBlurPref && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
                    val hideBuiltInHeadersForFloatingPill = modernSidebarEnabled && !sidebarCollapsed

                    val startDestination = when {
                        needsExperienceSelection -> Screen.ExperienceModeSelection.route
                        layoutChosen -> Screen.Home.route
                        else -> Screen.LayoutSelection.route
                    }
                    val navController = rememberNavController()
                    val navViewModelStoreOwner = rememberNavSessionViewModelStoreOwner(navController)
                    var optimisticRoute by remember { mutableStateOf<String?>(null) }
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val actualRoute = navBackStackEntry?.destination?.route
                    val currentRoute = optimisticRoute ?: actualRoute
                    startupDestination = startupDestinationForRoute(actualRoute ?: startDestination)

                    LaunchedEffect(actualRoute) {
                        optimisticRoute = null
                        ambientIdleController.onInteraction()
                    }

                    // Device HOME key / HOME-category launch: always land on Home, scrolled to the top,
                    // whatever screen (tab, detail page, search, settings, player) is showing.
                    val homeRequestNonce by pendingHomeRequestNonce.collectAsState()
                    var pendingHomeScrollReset by remember { mutableStateOf(false) }
                    LaunchedEffect(homeRequestNonce, layoutChosen) {
                        if (homeRequestNonce == 0 || layoutChosen != true) return@LaunchedEffect
                        if (navController.currentDestination?.route == Screen.Home.route) {
                            requestHomeScrollToTop(navController)
                            return@LaunchedEffect
                        }
                        // Reset only once Home is actually the current destination; Home restores its
                        // own saved scroll position, so the reset has to land after that restore.
                        pendingHomeScrollReset = true
                        val popped = navController.popBackStack(Screen.Home.route, inclusive = false)
                        if (!popped) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                    LaunchedEffect(pendingHomeScrollReset, actualRoute) {
                        if (!pendingHomeScrollReset || actualRoute != Screen.Home.route) return@LaunchedEffect
                        repeat(2) { withFrameNanos { } }
                        requestHomeScrollToTop(navController)
                        pendingHomeScrollReset = false
                    }

                    // Long inactivity or a fresh power-on should hand the TV back to whoever picks
                    // up the remote, so the profile picker becomes the default screen again.
                    val profileIdleSwitchEnabled by profileManager.profileIdleSwitchEnabled.collectAsState()
                    val canSwitchProfiles = profiles.size > 1 || activeProfileHasPin
                    val interactionAt by ambientIdleController.lastInteractionAt.collectAsState()
                    val ambientShowing by ambientIdleController.isShowing.collectAsState()
                    val appLifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
                    var profileSwitchDue by remember { mutableStateOf(false) }

                    // Re-armed on every key press / tap, and only while the app is actually on screen.
                    // An already stale clock (fresh launch, or coming back after a long absence such as
                    // the TV being switched off and on) switches immediately.
                    LaunchedEffect(
                        profileIdleSwitchEnabled,
                        canSwitchProfiles,
                        hasSelectedProfileThisSession,
                        currentRoute,
                        appLifecycleState,
                        interactionAt
                    ) {
                        if (!profileIdleSwitchEnabled || !canSwitchProfiles) {
                            profileSwitchDue = false
                            return@LaunchedEffect
                        }
                        if (!hasSelectedProfileThisSession) {
                            // The picker is already on screen; nothing to request.
                            profileSwitchDue = false
                            return@LaunchedEffect
                        }
                        if (!appLifecycleState.isAtLeast(Lifecycle.State.RESUMED)) return@LaunchedEffect
                        // Never pull the picker over playback, e.g. a paused movie left overnight.
                        // Leaving the player counts as activity so it cannot fire on the way out.
                        if (currentRoute?.startsWith("player/") == true) {
                            ambientIdleController.onInteraction()
                            return@LaunchedEffect
                        }
                        val remainingMs = interactionAt + PROFILE_SWITCH_IDLE_TIMEOUT_MS - SystemClock.uptimeMillis()
                        if (remainingMs > 0) delay(remainingMs)
                        profileSwitchDue = true
                    }

                    // The ambient slideshow is the screen saver for the same idle period; leave it up
                    // and only switch profiles once the user dismisses it.
                    LaunchedEffect(profileSwitchDue, ambientShowing, hasSelectedProfileThisSession) {
                        if (!profileSwitchDue) return@LaunchedEffect
                        if (!hasSelectedProfileThisSession) {
                            profileSwitchDue = false
                            return@LaunchedEffect
                        }
                        if (ambientShowing) return@LaunchedEffect
                        profileSwitchDue = false
                        // Restart the idle clock so a dismissed request cannot fire twice.
                        ambientIdleController.onInteraction()
                        if (profiles.size > 1 || activeProfileHasPin) {
                            pendingProfileSwitchRequest.value = true
                        }
                    }

                    // A device that just booted starts on the picker even when "remember last
                    // profile" would otherwise skip it. Only once per activity instance.
                    LaunchedEffect(
                        profileIdleSwitchEnabled,
                        canSwitchProfiles,
                        hasSelectedProfileThisSession,
                        layoutChosen
                    ) {
                        if (bootSwitchHandled) return@LaunchedEffect
                        if (layoutChosen != true) return@LaunchedEffect
                        if (!hasSelectedProfileThisSession) {
                            // The picker is already the first screen; nothing to force.
                            bootSwitchHandled = true
                            return@LaunchedEffect
                        }
                        if (SystemClock.elapsedRealtime() > PROFILE_SWITCH_BOOT_WINDOW_MS) {
                            bootSwitchHandled = true
                            return@LaunchedEffect
                        }
                        if (!profileIdleSwitchEnabled || !canSwitchProfiles) return@LaunchedEffect
                        bootSwitchHandled = true
                        pendingProfileSwitchRequest.value = true
                    }

                    // Auto-play next episode for EXTERNAL players: the tracker resolves the
                    // next episode and we navigate into the same Screen.Stream auto-play route
                    // the internal onPlaybackEnded path uses. Collected from the root composable
                    // so it survives StreamScreen's self-pop and a process kill (metadata is
                    // recovered from disk and the event replayed).
                    LaunchedEffect(navController) {
                        externalPlaybackTracker.autoPlayNext.collect { next ->
                            if (!externalPlaybackTracker.claimAutoPlayNextNavigation(next)) {
                                return@collect
                            }
                            Log.d(
                                "MainActivity",
                                "autoPlayNext received: S${next.nextSeason}E${next.nextEpisode} " +
                                    "videoId=${next.nextVideoId}; navigating to Stream"
                            )
                            navController.navigate(
                                Screen.Stream.createRoute(
                                    videoId = next.nextVideoId,
                                    contentType = next.contentType,
                                    title = next.contentName,
                                    poster = next.poster,
                                    backdrop = next.backdrop,
                                    logo = next.logo,
                                    season = next.nextSeason,
                                    episode = next.nextEpisode,
                                    year = next.year,
                                    contentId = next.contentId,
                                    contentName = next.contentName,
                                    returnToDetailOnBack = next.contentType.equals("series", ignoreCase = true),
                                    profileId = next.profileId
                                )
                            ) {
                                // Replace any lingering Stream screen (e.g. the previous
                                // episode's, restored from the backstack after a process restart).
                                popUpTo(Screen.Stream.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }

                    // Navigate to content when launched from the Continue Watching channel row.
                    LaunchedEffect(navController) {
                        if (launchContentId != null && launchContentType != null && layoutChosen) {
                            if (launchMode == "stream" && launchVideoId != null && launchName != null) {
                                navController.navigate(
                                    Screen.Stream.createRoute(
                                        videoId = launchVideoId,
                                        contentType = launchContentType,
                                        title = launchName,
                                        poster = launchPoster,
                                        backdrop = launchBackdrop,
                                        logo = launchLogo,
                                        season = launchSeason,
                                        episode = launchEpisode,
                                        episodeName = launchEpisodeTitle,
                                        contentId = launchContentId,
                                        contentName = launchName,
                                        returnToDetailOnBack = launchContentType.equals("series", ignoreCase = true),
                                        returnToHomeOnBack = true
                                    )
                                )
                            } else {
                                navController.navigate(
                                    Screen.Detail.createRoute(
                                        itemId = launchContentId,
                                        itemType = launchContentType
                                    )
                                )
                            }
                        }
                    }

                    val pendingLaunch by pendingLaunchIntent.collectAsState()
                    LaunchedEffect(navController, layoutChosen, pendingLaunch) {
                        val intent = pendingLaunch ?: return@LaunchedEffect
                        if (!layoutChosen) return@LaunchedEffect
                        pendingLaunchIntent.value = null
                        val contentId = intent.getStringExtra("contentId") ?: return@LaunchedEffect
                        val contentType = intent.getStringExtra("contentType") ?: return@LaunchedEffect
                        val videoId = intent.getStringExtra("videoId")
                        val name = intent.getStringExtra("name")
                        if (videoId != null && name != null) {
                            navController.navigate(
                                Screen.Stream.createRoute(
                                    videoId = videoId,
                                    contentType = contentType,
                                    title = name,
                                    poster = intent.getStringExtra("poster"),
                                    backdrop = intent.getStringExtra("backdrop"),
                                    logo = intent.getStringExtra("logo"),
                                    season = intent.getIntExtra("season", -1).takeIf { it >= 0 },
                                    episode = intent.getIntExtra("episode", -1).takeIf { it >= 0 },
                                    episodeName = intent.getStringExtra("episodeTitle"),
                                    contentId = contentId,
                                    contentName = name,
                                    returnToDetailOnBack = contentType.equals("series", ignoreCase = true),
                                    returnToHomeOnBack = true
                                )
                            )
                        } else {
                            navController.navigate(
                                Screen.Detail.createRoute(
                                    itemId = contentId,
                                    itemType = contentType
                                )
                            )
                        }
                    }

                    LaunchedEffect(navController, layoutChosen, pendingDeepLink) {
                        val url = pendingDeepLink ?: return@LaunchedEffect
                        if (!layoutChosen) return@LaunchedEffect
                        when (val deepLink = DeepLinkParser.parse(url)) {
                            is AppDeepLink.Meta -> {
                                pendingDeepLinkUrl.value = null
                                navController.navigate(
                                    Screen.Detail.createRoute(
                                        itemId = deepLink.id,
                                        itemType = deepLink.type
                                    )
                                ) {
                                    launchSingleTop = true
                                }
                            }
                            is AppDeepLink.AddonInstall -> {
                                navController.navigate(Screen.AddonManager.route) {
                                    launchSingleTop = true
                                }
                                Toast.makeText(context, context.getString(R.string.addon_installing), Toast.LENGTH_SHORT).show()
                                val installResult = deepLinkHandler.installAddon(deepLink.manifestUrl)
                                if (pendingDeepLinkUrl.value == url) {
                                    pendingDeepLinkUrl.value = null
                                }
                                Toast.makeText(context, installResult.message, Toast.LENGTH_LONG).show()
                            }
                            null -> {
                                pendingDeepLinkUrl.value = null
                            }
                        }
                    }

                    val view = LocalView.current
                    LaunchedEffect(currentRoute) {
                        val holder = PerformanceMetricsState.getHolderForHierarchy(view)
                        if (currentRoute != null) {
                            holder.state?.putState("Screen", currentRoute)
                        }
                    }

                    LaunchedEffect(discoverLocation, currentRoute) {
                        if (discoverLocation == null) return@LaunchedEffect
                        val onDiscoverRoute = currentRoute == Screen.Discover.route ||
                            currentRoute?.startsWith("${Screen.Discover.route}/") == true
                        if (discoverLocation == DiscoverLocation.OFF && onDiscoverRoute) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = false }
                                launchSingleTop = true
                            }
                        }
                    }

                    val liveTvMenuItem = com.nuvio.tv.ui.reshaped.livetv.rememberLiveTvMenuItem(currentRoute, navController) // Live TV menu entry
                    val sportsSettings by sportsRepository.settings.collectAsState(
                        initial = com.nuvio.tv.livetv.sports.SportsSettings()
                    )
                    val calendarSettings by calendarPreferences.settings.collectAsState(initial = com.nuvio.tv.calendar.CalendarSettings())
                    val calendarInSidebar = calendarSettings.calendarInSidebar && !calendarSettings.calendarOff

                    val rootRoutes = remember(discoverLocation, liveTvMenuItem, sportsSettings.showInSidebar) {
                        buildSet {
                            liveTvMenuItem?.let { add(it.route) } // Live TV menu entry
                            if (sportsSettings.showInSidebar) add(Screen.Sports.route)
                            add(Screen.YouTube.route)
                            add(Screen.Movies.route)
                            add(Screen.Series.route)
                            add(Screen.Home.route)
                            add(Screen.Apps.route)
                            add(Screen.Search.route)
                            add(Screen.Library.route)
                            add(Screen.Calendar.route)
                            add(Screen.ServerBrowser.route)
                            add(Screen.Settings.route)
                            if (discoverLocation == DiscoverLocation.IN_SIDEBAR) {
                                add(Screen.Discover.route)
                            }
                        }
                    }

                    val strNavHome = stringResource(R.string.nav_home)
                    val strNavMovies = stringResource(R.string.nav_movies)
                    val strNavSeries = stringResource(R.string.nav_series)
                    val strNavDiscover = stringResource(R.string.nav_discover)
                    val strNavSearch = stringResource(R.string.nav_search)
                    val strNavLibrary = stringResource(R.string.nav_library)
                    val strNavSettings = stringResource(R.string.nav_settings)
                    val strCalendarTitle = stringResource(R.string.calendar_title)
                    val sidebarMoviesIcon = ImageVector.vectorResource(R.drawable.sidebar_movies)
                    val sidebarSeriesIcon = ImageVector.vectorResource(R.drawable.sidebar_series)
                    val drawerItems = remember(
                        strNavHome,
                        strNavMovies,
                        strNavSeries,
                        strNavDiscover,
                        strNavSearch,
                        strNavLibrary,
                        strNavSettings,
                        sidebarMoviesIcon,
                        sidebarSeriesIcon,
                        strCalendarTitle,
                        discoverLocation,
                        liveTvMenuItem,
                        calendarInSidebar,
                        sportsSettings.showInSidebar
                    ) {
                        buildList {
                            add(
                                DrawerItem(
                                    route = Screen.Home.route,
                                    label = strNavHome,
                                    icon = Icons.Default.Home
                                )
                            )
                            if (discoverLocation == DiscoverLocation.IN_SIDEBAR) {
                                add(
                                    DrawerItem(
                                        route = Screen.Discover.route,
                                        label = strNavDiscover,
                                        icon = Icons.Default.Explore
                                    )
                                )
                            }
                            add(
                                DrawerItem(
                                    route = Screen.Search.route,
                                    label = strNavSearch,
                                    iconRes = R.raw.sidebar_search
                                )
                            )
                            add(
                                DrawerItem(
                                    route = Screen.Library.route,
                                    label = strNavLibrary,
                                    icon = Icons.Default.Star
                                )
                            )
                            if (calendarInSidebar) {
                                add(
                                    DrawerItem(
                                        route = Screen.Calendar.route,
                                        label = strCalendarTitle,
                                        icon = androidx.compose.material.icons.Icons.Default.DateRange
                                    )
                                )
                            }
                            liveTvMenuItem?.let(::add) // Live TV menu entry
                            add(
                                DrawerItem(
                                    route = Screen.Movies.route,
                                    label = strNavMovies,
                                    icon = sidebarMoviesIcon
                                )
                            )
                            add(
                                DrawerItem(
                                    route = Screen.Series.route,
                                    label = strNavSeries,
                                    icon = sidebarSeriesIcon
                                )
                            )
                            if (sportsSettings.showInSidebar) {
                                add(
                                    DrawerItem(
                                        route = Screen.Sports.route,
                                        label = "Sports",
                                        icon = Icons.Default.EmojiEvents
                                    )
                                )
                            }
                            add(
                                DrawerItem(
                                    route = Screen.YouTube.route,
                                    label = "YouTube",
                                    iconRes = R.raw.youtube_logo
                                )
                            )
                            add(
                                DrawerItem(
                                    route = Screen.Settings.route,
                                    label = strNavSettings,
                                    icon = Icons.Default.Settings
                                )
                            )
                        }
                    }
                    val selectedDrawerRoute = drawerItems.firstOrNull { item ->
                        currentRoute == item.route || currentRoute?.startsWith("${item.route}/") == true
                    }?.route ?: Screen.Home.route.takeIf { currentRoute == Screen.Apps.route }
                    val selectedDrawerItem = drawerItems.firstOrNull { it.route == selectedDrawerRoute } ?: drawerItems.first()

                    val freeboxUpdateViewModel: FreeboxUpdateViewModel = hiltViewModel(this@MainActivity)
                    val freeboxUpdateState by freeboxUpdateViewModel.state.collectAsState()

                    FreeboxUpdateBannerHost(
                        state = freeboxUpdateState,
                        visible = currentRoute == Screen.Home.route,
                        onDismiss = freeboxUpdateViewModel::dismissBanner,
                        onDownload = freeboxUpdateViewModel::downloadUpdate,
                        onInstall = {
                            freeboxUpdateState.downloadedApk?.let { apk ->
                                pendingApkInstallName = apk.name
                                if (!installFreeboxUpdateApk(context, apk) { intent ->
                                        apkInstallLauncher.launch(intent)
                                    }
                                ) {
                                    pendingApkInstallName = null
                                }
                            }
                        },
                    ) {
                        val handleSwitchProfile = {
                            startupSession++
                            splashTriggered = false
                            profileSwitchedManually = true
                            focusedSplashColor = null
                            focusedSplashBgUrl = null
                            focusedSplashCacheKey = null
                            focusedSplashTheme = null
                            hasSelectedProfileThisSession = false
                        }

                        // Double-tapping HOME asks for the profile switcher. Consumed on read so a
                        // later recomposition (or returning from the switcher) cannot repeat it.
                        val profileSwitchRequested by pendingProfileSwitchRequest.collectAsState()
                        LaunchedEffect(profileSwitchRequested) {
                            if (!profileSwitchRequested) return@LaunchedEffect
                            pendingProfileSwitchRequest.value = false
                            if (profiles.size > 1 || activeProfileHasPin) {
                                handleSwitchProfile()
                            }
                        }
                        Box(modifier = Modifier.fillMaxSize()) {
                            CompositionLocalProvider(LocalNavViewModelStoreOwner provides navViewModelStoreOwner) {
                                if (modernSidebarEnabled) {
                                    ModernSidebarScaffold(
                                        longPressBackHeld = longPressBackHeld,
                                        navController = navController,
                                        startDestination = startDestination,
                                        currentRoute = currentRoute,
                                        rootRoutes = rootRoutes,
                                        drawerItems = drawerItems,
                                        selectedDrawerRoute = selectedDrawerRoute,
                                        selectedDrawerItem = selectedDrawerItem,
                                        sidebarCollapsed = sidebarCollapsed,
                                        sidebarItemCornerRadiusDp = mainUiPrefs.sidebarItemCornerRadiusDp,
                                        modernSidebarBlurEnabled = modernSidebarBlurEnabled,
                                        hideBuiltInHeaders = hideBuiltInHeadersForFloatingPill,
                                        activeProfileName = activeProfile?.name ?: "",
                                        activeProfileColorHex = activeProfile?.avatarColorHex ?: "#1E88E5",
                                        activeProfileAvatarImageUrl = activeProfileAvatarImageUrl,
                                        showProfileSelector = profiles.size > 1,
                                        onSwitchProfile = handleSwitchProfile,
                                        onNavigate = { optimisticRoute = it },
                                        onBackToProfiles = handleSwitchProfile
                                    )
                                } else {
                                    LegacySidebarScaffold(
                                        longPressBackHeld = longPressBackHeld,
                                        navController = navController,
                                        startDestination = startDestination,
                                        currentRoute = currentRoute,
                                        rootRoutes = rootRoutes,
                                        drawerItems = drawerItems,
                                        selectedDrawerRoute = selectedDrawerRoute,
                                        sidebarCollapsed = sidebarCollapsed,
                                        sidebarItemCornerRadiusDp = mainUiPrefs.sidebarItemCornerRadiusDp,
                                        hideBuiltInHeaders = false,
                                        activeProfileName = activeProfile?.name ?: "",
                                        activeProfileColorHex = activeProfile?.avatarColorHex ?: "#1E88E5",
                                        activeProfileAvatarImageUrl = activeProfileAvatarImageUrl,
                                        showProfileSelector = profiles.size > 1,
                                        onSwitchProfile = handleSwitchProfile,
                                        onNavigate = { optimisticRoute = it },
                                        onBackToProfiles = handleSwitchProfile
                                    )
                                }
                            }

                            val autoNextOverlay by externalPlaybackTracker.autoNextOverlay.collectAsState()
                            autoNextOverlay?.let { ov ->
                                com.nuvio.tv.ui.screens.player.LoadingOverlay(
                                    visible = true,
                                    backdropUrl = ov.backdrop,
                                    logoUrl = ov.logo,
                                    title = ov.title,
                                    message = ov.message ?: stringResource(R.string.external_auto_next_loading),
                                    progress = ov.progress,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            val isTrailerPlaying by trailerPlayerPool.isTrailerPlaying.collectAsState()
                            LaunchedEffect(isTrailerPlaying) {
                                if (!isTrailerPlaying) ambientIdleController.onInteraction()
                            }
                            AmbientModeHost(
                                controller = ambientIdleController,
                                allowed = currentRoute?.startsWith("player/") != true &&
                                    autoNextOverlay == null &&
                                    !isTrailerPlaying,
                                onShown = { trailerPlayerPool.stop() }
                            )
                            com.nuvio.tv.calendar.CalendarNoticeHost()
                        }
                        }
                    }

                    LaunchedEffect(startupDestination, startupLoadingState) {
                        if (startupDestination == StartupDestination.Content) {
                            startupLoadingState.complete = true
                        }
                    }
                    val showSplash = shouldShowStartupSplash(
                        enabled = startupSplashEnabled,
                        complete = startupLoadingState.complete,
                        destination = startupDestination
                    )
                    val animatedSplashAlpha by animateFloatAsState(
                        targetValue = if (showSplash) 1f else 0f,
                        animationSpec = tween(if (showSplash) 0 else 400),
                        label = "startupSplashAlpha"
                    )
                    val splashAlpha = if (showSplash) 1f else animatedSplashAlpha
                    LaunchedEffect(splashAlpha, startupDestination) {
                        if (splashAlpha == 0f && startupDestination != StartupDestination.ProfileSelection) {
                            focusedSplashColor = null
                            focusedSplashBgUrl = null
                            focusedSplashCacheKey = null
                            focusedSplashTheme = null
                        }
                    }
                    if (splashAlpha > 0f) {
                        com.nuvio.tv.ui.components.StartupSplashScreen(
                            profileColorHex = splashBackground.profileColorHex,
                            profileBackgroundUrl = splashBackground.backgroundUrl,
                            backgroundCacheKey = splashBackground.backgroundCacheKey,
                            skipGradient = splashBackground.skipGradient,
                            modifier = Modifier.graphicsLayer { alpha = splashAlpha }
                        )
                    }
                    }
                }
                }
            }
        }
                }
            }
        }

        jankStats = JankStats.createAndTrack(window) { frameData ->
            if (frameData.isJank) {
                Log.w(
                    "JankStats",
                    "JANK: ${frameData.frameDurationUiNanos / 1_000_000}ms | states: ${frameData.states}"
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ShizukuStartup.startIfEnabled(applicationContext)
        // Coming back after a long absence (TV switched off and on again, or another app for a
        // while) hands the TV back to the profile picker. Measured before dismiss() restarts the
        // idle clock. The consumer ignores this when the picker is already up or only one profile
        // without a PIN exists.
        val idleGapMs = SystemClock.uptimeMillis() - ambientIdleController.lastInteractionAt.value
        ambientIdleController.dismiss()
        refreshHomeRole()
        HomeForegroundDelivery.attach(this, ::handleHomeKeyPress)
        // An external player covering the app is playback, not idleness, so a long film must not
        // hand the TV back to the picker when it ends.
        if (idleGapMs >= PROFILE_SWITCH_IDLE_TIMEOUT_MS &&
            !externalPlaybackTracker.isTracking &&
            profileManager.profileIdleSwitchEnabled.value
        ) {
            pendingProfileSwitchRequest.value = true
        }
        if (::jankStats.isInitialized) jankStats.isTrackingEnabled = true
        memberAccessRepository.refreshIfStale()
        lifecycleScope.launch {
            deviceSessionRegistration.requestForegroundRegistration()
            startupSyncService.requestForegroundSync()
        }
        lifecycleScope.launch {
            val refreshIntent = if (isFirstResumeAfterCreate) {
                isFirstResumeAfterCreate = false
                TrackingRefreshIntent.INVALIDATED
            } else {
                TrackingRefreshIntent.AUTOMATIC
            }
            trackingProgressRefreshCoordinator.refreshConnected(refreshIntent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        captureDeepLinkIntent(intent)
        captureLaunchIntent(intent)
        captureHomeRequestIntent(intent)
    }

    /**
     * HOME pressed while Nuvio itself is in front. The first press returns to the top of Home, a
     * second press right after it also opens the profile switcher.
     */
    private fun handleHomeKeyPress(pressedAtMs: Long) {
        applyHomeKeyPress(homeKeyGesture.onPress(pressedAtMs))
    }

    private fun applyHomeKeyPress(press: HomeKeyPress, openProfileSelection: Boolean = false) {
        if (press == HomeKeyPress.IGNORED) return
        ambientIdleController.dismiss()
        pendingHomeRequestNonce.value += 1
        if (press == HomeKeyPress.DOUBLE || openProfileSelection) {
            pendingProfileSwitchRequest.value = true
        }
    }

    private fun refreshHomeRole() {
        nuvioHandlesHomeKey = runCatching {
            LauncherSetup.handlesHome(this)
        }.getOrDefault(false)
    }

    /**
     * Raw keys, native HOME launches, and accessibility requests all use the same tap detector.
     * A cold native HOME launch records the first press but leaves startup/onboarding alone.
     * An explicit "go Home" request still resets navigation when starting the Activity.
     */
    private fun captureHomeRequestIntent(intent: Intent?, fromNewIntent: Boolean = true) {
        val press = LauncherHomeRequest.onIntent(
            intent = intent,
            gesture = homeKeyGesture,
            nowMs = SystemClock.uptimeMillis(),
            fromNewIntent = fromNewIntent
        )
        applyHomeKeyPress(press, LauncherHomeRequest.isProfileSelectionRequested(intent))
    }

    private fun captureDeepLinkIntent(intent: Intent?) {
        val url = intent?.dataString?.trim()?.takeIf(String::isNotBlank) ?: return
        pendingDeepLinkUrl.value = url
    }

    private fun captureLaunchIntent(intent: Intent?) {
        val contentId = intent?.getStringExtra("contentId") ?: return
        val launchMode = intent.getStringExtra("launchMode") ?: return
        if (launchMode != "stream") return
        pendingLaunchIntent.value = intent
    }

    override fun onPause() {
        HomeForegroundDelivery.detach(this)
        super.onPause()
        if (::jankStats.isInitialized) jankStats.isTrackingEnabled = false
    }

    // Intercept Back at the Activity level, before any Compose BackHandler, so the auto-next loader
    // can always be dismissed. Compose back-dispatch ordering kept putting the destination screen's
    // handler above the loader's, so Back never reached it.
    // Tracks whether a long-press Back sequence is in progress. When true, all Back
    // key events are consumed at the Activity level so that repeated DOWN events from a
    // held Back key don't cascade through Compose BackHandlers (e.g. opening the sidebar
    // and then immediately exiting the app).
    val longPressBackHeld = mutableStateOf(false)

    private val ambientIdleController = AmbientIdleController()

    override fun onUserInteraction() {
        super.onUserInteraction()
        ambientIdleController.onInteraction()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // HOME is checked before the ambient handler: while the slideshow is up a HOME press should
        // end it *and* go home, instead of only dismissing the slideshow.
        if (event.keyCode == KeyEvent.KEYCODE_HOME) refreshHomeRole()
        if (event.keyCode == KeyEvent.KEYCODE_HOME && nuvioHandlesHomeKey) {
            ambientIdleController.onInteraction()
            if (event.action == KeyEvent.ACTION_DOWN && ambientIdleController.isShowing.value) {
                ambientIdleController.dismiss()
            }
            // Some firmware delivers raw HOME keys; most sends a HOME intent or lets the
            // accessibility service filter the key. Keep this as a fallback, not the only path.
            if (event.action == KeyEvent.ACTION_UP && !event.isCanceled) {
                handleHomeKeyPress(event.eventTime)
            }
            return true
        }
        if (ambientIdleController.consumeKeyEvent(event)) return true
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (longPressBackHeld.value) {
                if (event.action == KeyEvent.ACTION_UP) longPressBackHeld.value = false
                return true
            }
        }
        if (event.keyCode == KeyEvent.KEYCODE_BACK &&
            externalPlaybackTracker.autoNextOverlay.value != null
        ) {
            if (event.action == KeyEvent.ACTION_UP) {
                Log.d("ExtAutoNext", "dispatchKeyEvent BACK -> dismissAutoNextOverlay (loader showing)")
                externalPlaybackTracker.dismissAutoNextOverlay()
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onStart() {
        // Returning from an external player: raise the auto-next loader before the player's
        // result is dispatched and before the window repaints, so the transition shows the
        // loader instantly with no episode-list flash. No-op unless a series episode is being
        // tracked; onActivityResult keeps it for a completion or dismisses it otherwise.
        externalPlaybackTracker.raiseAutoNextOverlayOnReturn()
        super.onStart()
        startupSyncService.startPeriodicSurfacePulls()
        myListsTvSyncManager.startPeriodicSync()
        androidTvChannelSyncService.onForegroundChanged(true)
    }

    override fun onStop() {
        externalPlaybackTracker.onExternalPlayerCoveredApp()
        super.onStop()
        startupSyncService.stopPeriodicSurfacePulls()
        myListsTvSyncManager.stopPeriodicSync()
        // App going to background (e.g. user returning to the launcher): reconcile the
        // Continue Watching channel once so Projectivy repaints it with fresh progress.
        androidTvChannelSyncService.onForegroundChanged(false)
    }

    override fun onDestroy() {
        HomeForegroundDelivery.detach(this)
        super.onDestroy()
        PluginRuntimeHooks.onActivityDestroy()
    }
}

@Composable
private fun SidebarFocusRecoveryEffect(
    drawerItems: List<DrawerItem>,
    selectedDrawerRoute: String?,
    drawerItemFocusRequesters: Map<String, FocusRequester>,
    sidebarOwnsFocus: Boolean
) {
    LaunchedEffect(drawerItems, sidebarOwnsFocus, selectedDrawerRoute) {
        if (!sidebarOwnsFocus) {
            return@LaunchedEffect
        }
        if (selectedDrawerRoute != null && drawerItems.any { it.route == selectedDrawerRoute }) {
            return@LaunchedEffect
        }
        val fallbackRoute = drawerItems.firstOrNull()?.route ?: return@LaunchedEffect
        val requester = drawerItemFocusRequesters[fallbackRoute] ?: return@LaunchedEffect
        repeat(2) { withFrameNanos { } }
        runCatching { requester.requestFocus() }
    }
}

private fun nextSidebarFocusIndex(
    focusedIndex: Int,
    drawerItems: List<DrawerItem>,
    selectedDrawerRoute: String?,
    hasProfileItem: Boolean,
    direction: Int
): Int? {
    val focusableCount = drawerItems.size + if (hasProfileItem) 1 else 0
    if (focusableCount == 0 || direction == 0) return null

    val currentIndex = focusedIndex.takeIf { it in 0 until focusableCount }
        ?: drawerItems.indexOfFirst { it.route == selectedDrawerRoute }.takeIf { it >= 0 }
        ?: 0
    return (currentIndex + direction + focusableCount) % focusableCount
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun LegacySidebarScaffold(
    longPressBackHeld: MutableState<Boolean>,
    navController: NavHostController,
    startDestination: String,
    currentRoute: String?,
    rootRoutes: Set<String>,
    drawerItems: List<DrawerItem>,
    selectedDrawerRoute: String?,
    sidebarCollapsed: Boolean,
    sidebarItemCornerRadiusDp: Int,
    hideBuiltInHeaders: Boolean,
    activeProfileName: String,
    activeProfileColorHex: String,
    activeProfileAvatarImageUrl: String?,
    showProfileSelector: Boolean,
    onSwitchProfile: () -> Unit,
    onNavigate: (String) -> Unit,
    onBackToProfiles: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerItemFocusRequesters = rememberDrawerItemFocusRequesters(drawerItems)
    val sidebarProfileFocusRequester = remember { FocusRequester() }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    val showSidebar = currentRoute in rootRoutes
    val hasSidebarProfileItem = showProfileSelector && activeProfileName.isNotEmpty()
    val sidebarProfileFocusIndex = drawerItems.size
    var focusedDrawerIndex by remember { mutableStateOf(-1) }
    var previousRoute by remember { mutableStateOf(currentRoute) }
    var pendingSidebarFocusRequest by remember { mutableStateOf(false) }

    LaunchedEffect(currentRoute, showSidebar) {
        longPressBackHeld.value = false
        val returnedFromSidebarDestination = previousRoute?.let { previous ->
            previous != Screen.Home.route && rootRoutes.any { route ->
                previous == route || previous.startsWith("$route/")
            }
        } == true && currentRoute == Screen.Home.route
        previousRoute = currentRoute
        if (showSidebar && returnedFromSidebarDestination) {
            drawerState.setValue(DrawerValue.Open)
            pendingSidebarFocusRequest = true
        } else {
            drawerState.setValue(DrawerValue.Closed)
        }
    }

    val sidebarTokens = NuvioComponents.tokens.sidebar
    val closedDrawerWidth = if (sidebarCollapsed) NuvioTheme.spacing.none else sidebarTokens.legacyCollapsedWidth
    val openDrawerWidth = sidebarTokens.legacyExpandedWidth
    val openDrawerItemWidth = sidebarTokens.itemWidth

    val focusManager = LocalFocusManager.current
    val isRtl = androidx.compose.ui.platform.LocalLayoutDirection.current == androidx.compose.ui.unit.LayoutDirection.Rtl
    val contentFocusRequester = remember { FocusRequester() }
    var pendingContentFocusTransfer by remember { mutableStateOf(false) }
    // Bumped on every key event the drawer sees so the auto-collapse timer
    // resets while the user navigates between drawer items.
    var legacyDrawerInteractionVersion by remember { mutableStateOf(0) }

    fun requestSidebarFocusAt(index: Int) {
        focusedDrawerIndex = index
        if (hasSidebarProfileItem && index == sidebarProfileFocusIndex) {
            sidebarProfileFocusRequester.requestFocus()
        } else {
            drawerItems.getOrNull(index)?.route?.let { route ->
                drawerItemFocusRequesters[route]?.requestFocus()
            }
        }
    }


    BackHandler(enabled = currentRoute in rootRoutes && drawerState.currentValue == DrawerValue.Closed) {
        pendingSidebarFocusRequest = true
        drawerState.setValue(DrawerValue.Open)
    }

    BackHandler(enabled = currentRoute in rootRoutes && drawerState.currentValue == DrawerValue.Open) {
        if (longPressBackHeld.value) return@BackHandler
        onBackToProfiles()
    }

    LaunchedEffect(drawerState.currentValue, pendingContentFocusTransfer) {
        if (!pendingContentFocusTransfer || drawerState.currentValue != DrawerValue.Closed) {
            return@LaunchedEffect
        }
        repeat(2) { withFrameNanos { } }
        runCatching { contentFocusRequester.requestFocus() }
        pendingContentFocusTransfer = false
    }

    LaunchedEffect(drawerState.currentValue, selectedDrawerRoute, showSidebar, pendingSidebarFocusRequest) {
        if (!showSidebar || !pendingSidebarFocusRequest || drawerState.currentValue != DrawerValue.Open) {
            return@LaunchedEffect
        }
        val targetRoute = selectedDrawerRoute ?: drawerItems.firstOrNull()?.route ?: run {
            pendingSidebarFocusRequest = false
            return@LaunchedEffect
        }
        val requester = drawerItemFocusRequesters[targetRoute] ?: run {
            pendingSidebarFocusRequest = false
            return@LaunchedEffect
        }
        repeat(2) { withFrameNanos { } }
        runCatching { requester.requestFocus() }
        pendingSidebarFocusRequest = false
    }

    SidebarFocusRecoveryEffect(
        drawerItems = drawerItems,
        selectedDrawerRoute = selectedDrawerRoute,
        drawerItemFocusRequesters = drawerItemFocusRequesters,
        sidebarOwnsFocus = showSidebar && drawerState.currentValue == DrawerValue.Open
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { drawerValue ->
            if (showSidebar) {
                val drawerWidth = if (drawerValue == DrawerValue.Open) openDrawerWidth else closedDrawerWidth
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(drawerWidth)
                        .background(NuvioTheme.colors.Background)
                        .padding(NuvioTheme.spacing.card.outer)
                        .selectableGroup()
                        .onPreviewKeyEvent { keyEvent ->
                            if (keyEvent.type == KeyEventType.KeyDown) {
                                legacyDrawerInteractionVersion++
                            }
                            if (drawerValue == DrawerValue.Open && keyEvent.type == KeyEventType.KeyDown) {
                                val direction = when (keyEvent.key) {
                                    Key.DirectionUp -> -1
                                    Key.DirectionDown -> 1
                                    else -> 0
                                }
                                if (direction != 0) {
                                    val targetIndex = nextSidebarFocusIndex(
                                        focusedIndex = focusedDrawerIndex,
                                        drawerItems = drawerItems,
                                        selectedDrawerRoute = selectedDrawerRoute,
                                        hasProfileItem = hasSidebarProfileItem,
                                        direction = direction
                                    )
                                    if (targetIndex != null) requestSidebarFocusAt(targetIndex)
                                    return@onPreviewKeyEvent true
                                }
                            }
                            val closeKey = if (isRtl) Key.DirectionLeft else Key.DirectionRight
                            if (keyEvent.key == closeKey && keyEvent.type == KeyEventType.KeyDown) {
                                drawerState.setValue(DrawerValue.Closed)
                                pendingContentFocusTransfer = false
                                true
                            } else {
                                false
                            }
                        }
                ) {
                    val isExpanded = drawerValue == DrawerValue.Open
                    val isDense = drawerItems.size >= 7
                    val itemSpacing = if (isDense) 6.dp else 10.dp
                    val legacyItemHeight = if (isDense) 44.dp else sidebarTokens.itemHeight
                    val itemWidth by animateDpAsState(
                        targetValue = if (isExpanded) openDrawerItemWidth else NuvioTheme.sizes.avatars.md,
                        animationSpec = tween(durationMillis = NuvioMotion.tokens.durations.fast, easing = NuvioMotion.tokens.easings.standard),
                        label = "legacySidebarItemWidth"
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        verticalArrangement = if (isExpanded) Arrangement.Top else Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(20.dp))
                            if (hasSidebarProfileItem) {
                                var isProfileFocused by remember { mutableStateOf(false) }
                                val profileItemShape = NuvioTheme.configuredShape(RoundedCornerShape(sidebarItemCornerRadiusDp.dp))
                                val profileLeadingInset = NuvioTheme.spacing.lg + NuvioTheme.spacing.xxs
                                val profileAvatarSize = NuvioTheme.sizes.sidebar.leadingVisual
                                val profileLabelStart = 60.dp
                                val profileGapAfterAvatar =
                                    (profileLabelStart - profileLeadingInset - profileAvatarSize).coerceAtLeast(NuvioTheme.spacing.none)
                                val profileBgColor = if (isProfileFocused) NuvioTheme.colors.FocusBackground else Color.Transparent
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .width(itemWidth)
                                            .height(legacyItemHeight)
                                            .background(color = profileBgColor, shape = profileItemShape)
                                            .focusRequester(sidebarProfileFocusRequester)
                                            .onFocusChanged {
                                                isProfileFocused = it.isFocused
                                                if (it.isFocused) focusedDrawerIndex = sidebarProfileFocusIndex
                                            }
                                            .clickable {
                                                onSwitchProfile()
                                                drawerState.setValue(DrawerValue.Closed)
                                            }
                                            .semantics { role = Role.Button },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Spacer(modifier = Modifier.width(profileLeadingInset))
                                        ProfileAvatarCircle(
                                            name = activeProfileName,
                                            colorHex = activeProfileColorHex,
                                            size = profileAvatarSize,
                                            avatarImageUrl = activeProfileAvatarImageUrl,
                                            imageCrossfade = false
                                        )
                                        Spacer(modifier = Modifier.width(profileGapAfterAvatar))
                                        Text(
                                            text = activeProfileName,
                                            style = NuvioTheme.textStyles.nav.copy(
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = if (isProfileFocused) NuvioTheme.colors.TextPrimary else NuvioTheme.colors.TextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Start,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            } else {
                                BrandWordmark(
                                    contentDescription = stringResource(R.string.app_name),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(if (isExpanded) Modifier.weight(1f) else Modifier),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(itemSpacing),
                                horizontalAlignment = Alignment.Start
                            ) {
                                drawerItems.forEach { item ->
                                    key(item.route) {
                                        LegacySidebarButton(
                                            label = item.label,
                                            iconRes = item.iconRes,
                                            icon = item.icon,
                                            selected = selectedDrawerRoute == item.route,
                                            expanded = isExpanded,
                                            itemHeight = legacyItemHeight,
                                            cornerRadius = sidebarItemCornerRadiusDp.dp,
                                            onFocusChanged = { focused ->
                                                if (focused) focusedDrawerIndex = drawerItems.indexOfFirst { it.route == item.route }
                                            },
                                            onClick = {
                                                keyboardController?.hide()
                                                val navigated = navigateToDrawerRoute(
                                                    navController = navController,
                                                    targetRoute = item.route
                                                )
                                                if (navigated) onNavigate(item.route)
                                                drawerState.setValue(DrawerValue.Closed)
                                                pendingContentFocusTransfer = currentRoute == item.route
                                            },
                                            modifier = Modifier.focusRequester(
                                                drawerItemFocusRequesters.getValue(item.route)
                                            )
                                                .width(itemWidth)
                                                .offset(x = NuvioTheme.spacing.md)
                                        )
                                    }
                                }
                            }
                        }
                    }
            }
        }
        }
    ) {
        val contentStartPadding by animateDpAsState(
            targetValue = if (showSidebar && !sidebarCollapsed) {
                NuvioLayout.tokens.sidebarContentOffset
            } else {
                NuvioTheme.spacing.none
            },
            animationSpec = tween(NuvioMotion.tokens.durations.medium),
            label = "contentStartPadding"
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = contentStartPadding)
                .onPreviewKeyEvent { keyEvent ->
                    // Long-press Back on a root route directly opens the sidebar.
                    if (keyEvent.key == Key.Back) {
                        if (
                            keyEvent.type == KeyEventType.KeyDown &&
                            showSidebar &&
                            drawerState.currentValue == DrawerValue.Closed &&
                            currentRoute in rootRoutes &&
                            keyEvent.nativeKeyEvent.isLongPress
                        ) {
                            if (!longPressBackHeld.value) {
                                longPressBackHeld.value = true
                                pendingSidebarFocusRequest = true
                                drawerState.setValue(DrawerValue.Open)
                            }
                            return@onPreviewKeyEvent true
                        }
                        if (longPressBackHeld.value) {
                            if (keyEvent.type == KeyEventType.KeyUp) longPressBackHeld.value = false
                            return@onPreviewKeyEvent true
                        }
                    }
                    false
                }
                .onKeyEvent { keyEvent ->
                    val openKey = if (isRtl) Key.DirectionRight else Key.DirectionLeft
                    if (
                        showSidebar &&
                        drawerState.currentValue == DrawerValue.Closed &&
                        keyEvent.type == KeyEventType.KeyDown &&
                        keyEvent.key == openKey
                    ) {
                        if (focusManager.moveFocus(if (isRtl) FocusDirection.Right else FocusDirection.Left)) {
                            true
                        } else {
                            pendingSidebarFocusRequest = true
                            drawerState.setValue(DrawerValue.Open)
                            true
                        }
                    } else {
                        false
                    }
                }
        ) {
            CompositionLocalProvider(
                LocalSidebarExpanded provides (drawerState.currentValue == DrawerValue.Open),
                LocalOpenSidebar provides {
                    pendingSidebarFocusRequest = true
                    drawerState.setValue(DrawerValue.Open)
                },
                LocalContentFocusRequester provides contentFocusRequester
            ) {
                NuvioNavHost(
                    navController = navController,
                    startDestination = startDestination,
                    hideBuiltInHeaders = hideBuiltInHeaders
                )
            }
        }
    }
}

@Composable
private fun LegacySidebarButton(
    label: String,
    iconRes: Int?,
    icon: ImageVector?,
    selected: Boolean,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    itemHeight: androidx.compose.ui.unit.Dp = NuvioComponents.tokens.sidebar.itemHeight,
    cornerRadius: androidx.compose.ui.unit.Dp = NuvioTheme.radii.md,
    onFocusChanged: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val itemShape = NuvioTheme.configuredShape(RoundedCornerShape(cornerRadius))
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isFocused -> NuvioTheme.colors.FocusBackground
            expanded && selected -> NuvioTheme.colors.Secondary
            else -> Color.Transparent
        },
        label = "legacySidebarItemBackground"
    )
    val contentColor by animateColorAsState(
        targetValue = when {
            isFocused -> NuvioTheme.colors.TextPrimary
            expanded && selected -> NuvioTheme.colors.OnSecondary
            else -> NuvioTheme.colors.TextSecondary
        },
        label = "legacySidebarItemContent"
    )
    val iconTint by animateColorAsState(
        targetValue = when {
            isFocused -> NuvioTheme.colors.TextPrimary
            expanded && selected -> NuvioTheme.colors.OnSecondary
            selected -> NuvioTheme.colors.Secondary
            !expanded -> NuvioTheme.colors.TextTertiary
            else -> NuvioTheme.colors.TextSecondary
        },
        label = "legacySidebarItemIconTint"
    )
    Card(
        onClick = onClick,
        modifier = modifier
            .height(itemHeight)
            .focusProperties { canFocus = expanded }
            .onFocusChanged {
                isFocused = it.hasFocus
                onFocusChanged(it.hasFocus)
            },
        colors = CardDefaults.colors(
            containerColor = backgroundColor,
            focusedContainerColor = backgroundColor,
        ),
        border = CardDefaults.border(
            border = androidx.tv.material3.Border.None,
            focusedBorder = androidx.tv.material3.Border(
                border = androidx.compose.foundation.BorderStroke(NuvioStrokes.tokens.thin, Color.Transparent),
                shape = itemShape
            )
        ),
        shape = CardDefaults.shape(shape = itemShape),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .semantics {
                    contentDescription = label
                    role = Role.Tab
                    this.selected = selected
                }
        ) {
        DrawerItemIcon(
            iconRes = iconRes,
            icon = icon,
            tint = iconTint,
            modifier = Modifier
                .size(NuvioComponents.tokens.sidebar.iconSize)
                .align(Alignment.CenterStart)
                .offset(x = 13.dp)
        )
        if (expanded) {
            com.nuvio.tv.ui.components.AutoResizeText(
                text = label,
                color = contentColor,
                style = NuvioTheme.textStyles.nav.copy(fontSize = 14.sp),
                minFontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 54.dp, end = 14.dp)
            )
        }
    }
}
}

@Composable
private fun ModernSidebarScaffold(
    longPressBackHeld: MutableState<Boolean>,
    navController: NavHostController,
    startDestination: String,
    currentRoute: String?,
    rootRoutes: Set<String>,
    drawerItems: List<DrawerItem>,
    selectedDrawerRoute: String?,
    selectedDrawerItem: DrawerItem,
    sidebarCollapsed: Boolean,
    sidebarItemCornerRadiusDp: Int,
    modernSidebarBlurEnabled: Boolean,
    hideBuiltInHeaders: Boolean,
    activeProfileName: String,
    activeProfileColorHex: String,
    activeProfileAvatarImageUrl: String?,
    showProfileSelector: Boolean,
    onSwitchProfile: () -> Unit,
    onNavigate: (String) -> Unit,
    onBackToProfiles: () -> Unit
) {
    val showSidebar = currentRoute in rootRoutes
    val sidebarTokens = NuvioComponents.tokens.sidebar
    val collapsedSidebarWidth = if (sidebarCollapsed) NuvioTheme.spacing.none else sidebarTokens.collapsedWidth
    val openSidebarWidth = sidebarTokens.expandedWidth

    val focusManager = LocalFocusManager.current
    val isRtl = androidx.compose.ui.platform.LocalLayoutDirection.current == androidx.compose.ui.unit.LayoutDirection.Rtl
    val contentFocusRequester = remember { FocusRequester() }
    val drawerItemFocusRequesters = rememberDrawerItemFocusRequesters(drawerItems)
    val sidebarProfileFocusRequester = remember { FocusRequester() }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    var isSidebarExpanded by remember { mutableStateOf(false) }
    var sidebarCollapsePending by remember { mutableStateOf(false) }
    var pendingContentFocusTransfer by remember { mutableStateOf(false) }
    var pendingSidebarFocusRequest by remember { mutableStateOf(false) }
    var focusedDrawerIndex by remember { mutableStateOf(-1) }
    var previousRoute by remember { mutableStateOf(currentRoute) }
    var isFloatingPillIconOnly by remember { mutableStateOf(false) }
    var pillExpandRequestCount by remember { mutableIntStateOf(0) }
    val keepFloatingPillExpanded = false
    val keepSidebarFocusDuringCollapse =
        isSidebarExpanded || sidebarCollapsePending || pendingContentFocusTransfer
    val hasSidebarProfileItem = showProfileSelector && activeProfileName.isNotEmpty()
    val sidebarProfileFocusIndex = drawerItems.size

    LaunchedEffect(showSidebar) {
        if (!showSidebar) {
            isSidebarExpanded = false
            sidebarCollapsePending = false
            pendingContentFocusTransfer = false
            pendingSidebarFocusRequest = false
            isFloatingPillIconOnly = false
        }
    }

    // Collapse sidebar when navigating between root routes (e.g. Settings -> Home via Back)
    LaunchedEffect(currentRoute, showSidebar) {
        val returnedFromSidebarDestination = previousRoute?.let { previous ->
            previous != Screen.Home.route && rootRoutes.any { route ->
                previous == route || previous.startsWith("$route/")
            }
        } == true && currentRoute == Screen.Home.route
        previousRoute = currentRoute
        if (showSidebar && returnedFromSidebarDestination) {
            isSidebarExpanded = true
            sidebarCollapsePending = false
            pendingSidebarFocusRequest = true
        } else if (isSidebarExpanded && showSidebar) {
            sidebarCollapsePending = true
        }
    }

    LaunchedEffect(keepFloatingPillExpanded, showSidebar) {
        if (!showSidebar || keepFloatingPillExpanded) {
            isFloatingPillIconOnly = false
        }
    }

    // Expand pill label briefly after navigating to a different root route
    LaunchedEffect(selectedDrawerRoute) {
        if (showSidebar && !isSidebarExpanded) {
            isFloatingPillIconOnly = false
            pillExpandRequestCount++
        }
    }

    fun requestSidebarFocusAt(index: Int) {
        focusedDrawerIndex = index
        if (hasSidebarProfileItem && index == sidebarProfileFocusIndex) {
            sidebarProfileFocusRequester.requestFocus()
        } else {
            drawerItems.getOrNull(index)?.route?.let { route ->
                drawerItemFocusRequesters[route]?.requestFocus()
            }
        }
    }

    BackHandler(enabled = currentRoute in rootRoutes && !isSidebarExpanded && !sidebarCollapsePending) {
        isSidebarExpanded = true
        sidebarCollapsePending = false
        pendingSidebarFocusRequest = true
    }

    BackHandler(enabled = currentRoute in rootRoutes && isSidebarExpanded && !sidebarCollapsePending) {
        if (longPressBackHeld.value) return@BackHandler
        onBackToProfiles()
    }

    LaunchedEffect(sidebarCollapsePending, isSidebarExpanded, showSidebar) {
        if (!showSidebar || !sidebarCollapsePending) {
            return@LaunchedEffect
        }
        if (!isSidebarExpanded) {
            sidebarCollapsePending = false
            return@LaunchedEffect
        }
        delay(95L)
        isSidebarExpanded = false
        sidebarCollapsePending = false
    }

    // Auto-collapse the expanded sidebar after a short period of inactivity.
    // The timer resets every time focus moves between drawer items, so the

    // Auto-collapse the floating pill back to icon-only when the user reveals
    // its label (DPAD UP from content) and then leaves it idle. The DPAD DOWN
    // path already collapses it instantly, this just covers the case where the
    // user releases UP and walks away.
    LaunchedEffect(isFloatingPillIconOnly, keepFloatingPillExpanded, showSidebar, isSidebarExpanded, pillExpandRequestCount) {
        if (!showSidebar || isFloatingPillIconOnly || keepFloatingPillExpanded || isSidebarExpanded) {
            return@LaunchedEffect
        }
        delay(SIDEBAR_AUTO_COLLAPSE_DELAY_MS)
        isFloatingPillIconOnly = true
    }

    val sidebarVisible = showSidebar && (isSidebarExpanded || !sidebarCollapsed)
    val sidebarHazeState = remember { HazeState() }
    // Panel is always laid out at full expanded width; open/close is
    // purely a graphicsLayer transform (scale + alpha) so Compose never
    // re-layouts and haze doesn't re-render blur every frame.
    val sidebarWidth = if (sidebarVisible) openSidebarWidth else collapsedSidebarWidth
    val animationDuration = if (sidebarVisible) 400 else 300
    val animationEasing = if (sidebarVisible) FastOutSlowInEasing else FastOutLinearInEasing

    val sidebarSlideX = NuvioTheme.spacing.none
    val sidebarSurfaceAlpha by animateFloatAsState(
        targetValue = if (isSidebarExpanded) 1f else 0f,
        animationSpec = tween(durationMillis = if (isSidebarExpanded) 280 else 200, easing = animationEasing),
        label = "sidebarSurfaceAlpha"
    )
    val shouldApplySidebarHaze = showSidebar && modernSidebarBlurEnabled
    val sidebarTransition = updateTransition(
        targetState = isSidebarExpanded,
        label = "sidebarTransition"
    )
    // Labels and icons are always at full size — the panel is rendered
    // complete and the open/close animation is purely graphicsLayer.
    val sidebarLabelAlpha = 1f
    val sidebarExpandProgress by sidebarTransition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = NuvioMotion.tokens.durations.sidebarPanelIn, easing = FastOutSlowInEasing)
            } else {
                tween(durationMillis = NuvioMotion.tokens.durations.sidebarPanelOut, easing = LinearOutSlowInEasing)
            }
        },
        label = "sidebarExpandProgress"
    ) { expanded ->
        if (expanded) 1f else 0f
    }

    // derivedStateOf prevents per-frame recomposition — only triggers when the boolean crosses the threshold
    val sidebarBlocksContentKeys by remember { derivedStateOf { sidebarExpandProgress > 0.2f } }
    val sidebarShowExpandedPanel by remember { derivedStateOf { sidebarExpandProgress > 0.01f } }
    val sidebarShowCollapsedPill by remember { derivedStateOf { sidebarExpandProgress < 0.98f } }

    val sidebarIconScale = 1f
    val sidebarBloomScale = 1f
    val sidebarDeflateOffsetX = NuvioTheme.spacing.none
    val sidebarDeflateOffsetY = NuvioTheme.spacing.none

    LaunchedEffect(isSidebarExpanded, sidebarCollapsePending, pendingContentFocusTransfer, showSidebar) {
        if (!showSidebar || !pendingContentFocusTransfer || isSidebarExpanded || sidebarCollapsePending) {
            return@LaunchedEffect
        }
        repeat(2) { withFrameNanos { } }
        runCatching { contentFocusRequester.requestFocus() }
        pendingContentFocusTransfer = false
    }

    LaunchedEffect(isSidebarExpanded, pendingSidebarFocusRequest, showSidebar, selectedDrawerRoute) {
        if (!showSidebar || !pendingSidebarFocusRequest || !isSidebarExpanded) {
            return@LaunchedEffect
        }
        val targetRoute = selectedDrawerRoute ?: drawerItems.firstOrNull()?.route ?: run {
            pendingSidebarFocusRequest = false
            return@LaunchedEffect
        }
        val requester = drawerItemFocusRequesters[targetRoute] ?: run {
            pendingSidebarFocusRequest = false
            return@LaunchedEffect
        }
        repeat(2) { withFrameNanos { } }
        runCatching { requester.requestFocus() }
        pendingSidebarFocusRequest = false
    }

    SidebarFocusRecoveryEffect(
        drawerItems = drawerItems,
        selectedDrawerRoute = selectedDrawerRoute,
        drawerItemFocusRequesters = drawerItemFocusRequesters,
        sidebarOwnsFocus = showSidebar && isSidebarExpanded
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onPreviewKeyEvent { keyEvent ->
                // Consume all Back key events after long-press until released,
                // preventing the exit-app BackHandler from firing during the hold.
                if (longPressBackHeld.value && keyEvent.key == Key.Back) {
                    if (keyEvent.type == KeyEventType.KeyUp) longPressBackHeld.value = false
                    return@onPreviewKeyEvent true
                }
                false
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (shouldApplySidebarHaze) Modifier.hazeSource(state = sidebarHazeState)
                    else Modifier
                )
                .onPreviewKeyEvent { keyEvent ->
                    // Long-press Back on a root route directly opens the sidebar.
                    if (keyEvent.key == Key.Back) {
                        if (
                            keyEvent.type == KeyEventType.KeyDown &&
                            showSidebar &&
                            !isSidebarExpanded &&
                            !sidebarCollapsePending &&
                            currentRoute in rootRoutes &&
                            keyEvent.nativeKeyEvent.isLongPress
                        ) {
                            if (!longPressBackHeld.value) {
                                longPressBackHeld.value = true
                                isSidebarExpanded = true
                                sidebarCollapsePending = false
                                pendingSidebarFocusRequest = true
                            }
                            return@onPreviewKeyEvent true
                        }
                        if (longPressBackHeld.value) {
                            if (keyEvent.type == KeyEventType.KeyUp) longPressBackHeld.value = false
                            return@onPreviewKeyEvent true
                        }
                    }
                    if (
                        isSidebarExpanded &&
                        !sidebarCollapsePending &&
                        sidebarBlocksContentKeys &&
                        keyEvent.type == KeyEventType.KeyDown &&
                        isBlockedContentKey(keyEvent.key)
                    ) {
                        true
                    } else {
                        false
                    }
                }
                .onKeyEvent { keyEvent ->
                    if (showSidebar && !isSidebarExpanded && keyEvent.type == KeyEventType.KeyDown) {
                        if (!keepFloatingPillExpanded) {
                            when (keyEvent.key) {
                                Key.DirectionDown -> isFloatingPillIconOnly = true
                                Key.DirectionUp -> {
                                    isFloatingPillIconOnly = false
                                    pillExpandRequestCount++
                                }
                                else -> Unit
                            }
                        }
                        val openKey = if (isRtl) Key.DirectionRight else Key.DirectionLeft
                        if (keyEvent.key == openKey) {
                            if (focusManager.moveFocus(if (isRtl) FocusDirection.Right else FocusDirection.Left)) {
                                true
                            } else {
                                isSidebarExpanded = true
                                sidebarCollapsePending = false
                                pendingSidebarFocusRequest = true
                                true
                            }
                        } else {
                            false
                        }
                    } else {
                        false
                    }
                }
        ) {
            CompositionLocalProvider(
                LocalSidebarExpanded provides isSidebarExpanded,
                LocalOpenSidebar provides {
                    isSidebarExpanded = true
                    sidebarCollapsePending = false
                    pendingSidebarFocusRequest = true
                },
                LocalContentFocusRequester provides contentFocusRequester
            ) {
                NuvioNavHost(
                    navController = navController,
                    startDestination = startDestination,
                    hideBuiltInHeaders = hideBuiltInHeaders
                )
            }
        }

        if (showSidebar && (sidebarVisible || sidebarShowExpandedPanel)) {
            val panelShape = NuvioTheme.configuredShape(RoundedCornerShape(sidebarTokens.panelRadius))
            val sidebarItemShape = NuvioTheme.configuredShape(RoundedCornerShape(sidebarItemCornerRadiusDp.dp))
            val showExpandedPanel = isSidebarExpanded || sidebarShowExpandedPanel

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .width(openSidebarWidth)
                    .padding(start = NuvioTheme.spacing.lg - NuvioTheme.spacing.xxs, top = NuvioTheme.spacing.lg, bottom = NuvioTheme.spacing.md, end = NuvioTheme.spacing.sm)
                    .graphicsLayer {
                        val progress = sidebarExpandProgress
                        alpha = sidebarSurfaceAlpha
                        val s = 0.92f + 0.08f * progress
                        scaleX = s
                        scaleY = s
                        transformOrigin = TransformOrigin(0f, 0f)
                    }
                    .selectableGroup()
                    .onPreviewKeyEvent { keyEvent ->
                        if (!isSidebarExpanded || keyEvent.type != KeyEventType.KeyDown) {
                            return@onPreviewKeyEvent false
                        }
                        when (keyEvent.key) {
                            Key.DirectionUp -> {
                                val targetIndex = nextSidebarFocusIndex(
                                    focusedIndex = focusedDrawerIndex,
                                    drawerItems = drawerItems,
                                    selectedDrawerRoute = selectedDrawerRoute,
                                    hasProfileItem = hasSidebarProfileItem,
                                    direction = -1
                                )
                                if (targetIndex != null) requestSidebarFocusAt(targetIndex)
                                true
                            }

                            Key.DirectionDown -> {
                                val targetIndex = nextSidebarFocusIndex(
                                    focusedIndex = focusedDrawerIndex,
                                    drawerItems = drawerItems,
                                    selectedDrawerRoute = selectedDrawerRoute,
                                    hasProfileItem = hasSidebarProfileItem,
                                    direction = 1
                                )
                                if (targetIndex != null) requestSidebarFocusAt(targetIndex)
                                true
                            }

                            Key.DirectionRight, Key.DirectionLeft -> {
                                val collapseKey = if (isRtl) Key.DirectionLeft else Key.DirectionRight
                                if (keyEvent.key == collapseKey) {
                                    pendingContentFocusTransfer = false
                                    sidebarCollapsePending = true
                                    true
                                } else {
                                    false
                                }
                            }

                            else -> false
                        }
                    }
            ) {
                if (showExpandedPanel) {
                    ModernSidebarBlurPanel(
                        drawerItems = drawerItems,
                        selectedDrawerRoute = selectedDrawerRoute,
                        keepSidebarFocusDuringCollapse = keepSidebarFocusDuringCollapse,
                        sidebarLabelAlpha = sidebarLabelAlpha,
                        sidebarIconScale = sidebarIconScale,
                        sidebarExpandProgress = sidebarExpandProgress,
                        isSidebarExpanded = isSidebarExpanded,
                        sidebarCollapsePending = sidebarCollapsePending,
                        blurEnabled = modernSidebarBlurEnabled,
                        sidebarHazeState = sidebarHazeState,
                        panelShape = panelShape,
                        itemShape = sidebarItemShape,
                        drawerItemFocusRequesters = drawerItemFocusRequesters,
                        profileFocusRequester = sidebarProfileFocusRequester,
                        onDrawerItemFocused = { focusedDrawerIndex = it },
                        onDrawerItemClick = { targetRoute ->
                            keyboardController?.hide()
                            val navigated = navigateToDrawerRoute(
                                navController = navController,
                                targetRoute = targetRoute
                            )
                            if (navigated) onNavigate(targetRoute)
                            pendingSidebarFocusRequest = false
                            isSidebarExpanded = false
                            sidebarCollapsePending = false
                            pendingContentFocusTransfer = currentRoute == targetRoute
                        },
                        activeProfileName = activeProfileName,
                        activeProfileColorHex = activeProfileColorHex,
                        activeProfileAvatarImageUrl = activeProfileAvatarImageUrl,
                        showProfileSelector = showProfileSelector,
                        onSwitchProfile = onSwitchProfile
                    )
                }
            }

            if (
                !sidebarCollapsed &&
                sidebarShowCollapsedPill &&
                selectedDrawerRoute != Screen.Search.route &&
                currentRoute != Screen.Settings.route
            ) {
                CollapsedSidebarPill(
                    label = selectedDrawerItem.label,
                    iconRes = selectedDrawerItem.iconRes,
                    icon = selectedDrawerItem.icon,
                    iconOnly = isFloatingPillIconOnly && !keepFloatingPillExpanded,
                    blurEnabled = modernSidebarBlurEnabled,
                    hazeState = if (modernSidebarBlurEnabled) sidebarHazeState else null,
                    cornerRadius = sidebarItemCornerRadiusDp.dp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset {
                            IntOffset(
                                (NuvioTheme.spacing.lg - NuvioTheme.spacing.xxs).roundToPx(),
                                (NuvioTheme.spacing.lg + sidebarDeflateOffsetY).roundToPx()
                            )
                        }
                        .graphicsLayer {
                            val progress = sidebarExpandProgress
                            alpha = 1f - progress
                            val s = 0.9f + (0.1f * (1f - progress))
                            scaleX = s
                            scaleY = s
                            transformOrigin = TransformOrigin(0f, 0f)
                        },
                    onExpand = {
                        isSidebarExpanded = true
                        sidebarCollapsePending = false
                        pendingSidebarFocusRequest = true
                    }
                )
            }
        }
    }
}

@Composable
private fun CollapsedSidebarPill(
    label: String,
    iconRes: Int?,
    icon: ImageVector?,
    iconOnly: Boolean,
    blurEnabled: Boolean,
    hazeState: HazeState? = null,
    cornerRadius: androidx.compose.ui.unit.Dp = NuvioRadii.tokens.full,
    modifier: Modifier = Modifier,
    onExpand: () -> Unit
) {
    val pillShape = NuvioTheme.configuredShape(RoundedCornerShape(cornerRadius))
    val colors = NuvioTheme.colors
    val bgElevated = colors.BackgroundElevated
    val bgCard = colors.BackgroundCard
    val borderBase = colors.Border
    val mediaColors = colors.media
    val pillBackgroundBrush = remember(blurEnabled) {
        val alpha = if (blurEnabled) 0.65f else 0.96f
        Brush.verticalGradient(listOf(
            Color(0xFF1C1C1E).copy(alpha = alpha),
            Color(0xFF1C1C1E).copy(alpha = alpha)
        ))
    }

    Row(
        modifier = modifier
            .focusProperties { canFocus = false }
            .clickable(onClick = onExpand)
            .semantics {
                role = Role.Button
                contentDescription = label
            }
            .padding(horizontal = NuvioTheme.spacing.hairline, vertical = NuvioTheme.spacing.xxs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .height(NuvioTheme.sizes.player.control)
                .clip(pillShape)
                .then(
                    if (blurEnabled && hazeState != null) {
                        Modifier.hazeEffect(state = hazeState) {
                            blurRadius = 24.dp
                            inputScale = HazeInputScale.Fixed(0.66f)
                        }
                    } else {
                        Modifier
                    }
                )
                .background(brush = pillBackgroundBrush, shape = pillShape)
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .padding(start = 5.dp, end = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(NuvioTheme.sizes.sidebar.leadingVisual),
                    contentAlignment = Alignment.Center
                ) {
                    DrawerItemIcon(
                        iconRes = iconRes,
                        icon = icon,
                        tint = NuvioTheme.colors.text.onOverlay,
                        modifier = Modifier
                            .size(NuvioTheme.sizes.sidebar.leadingVisual - NuvioTheme.spacing.md)
                            .offset(y = (-0.5).dp)
                    )
                }

                AnimatedVisibility(
                    visible = !iconOnly,
                    enter = expandHorizontally(
                            animationSpec = tween(NuvioMotion.tokens.durations.fast, easing = FastOutSlowInEasing),
                            expandFrom = Alignment.Start,
                            clip = true
                        ),
                    exit = shrinkHorizontally(
                            animationSpec = tween(NuvioMotion.tokens.durations.fast, easing = FastOutSlowInEasing),
                            shrinkTowards = Alignment.Start,
                            clip = true
                        )
                ) {
                    Text(
                        text = label,
                        color = NuvioTheme.colors.text.onOverlay,
                        style = NuvioTheme.textStyles.nav.copy(fontSize = 14.sp),
                        modifier = Modifier
                            .padding(start = 9.dp, end = NuvioTheme.spacing.md - 5.dp)
                            .offset(y = (-0.5).dp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private fun navigateToDrawerRoute(
    navController: NavHostController,
    targetRoute: String
): Boolean {
    val actualRoute = navController.currentDestination?.route
    if (actualRoute == targetRoute) {
        if (targetRoute == Screen.Home.route) {
            requestHomeScrollToTop(navController)
        }
        return true
    }
    try {
        if (targetRoute == Screen.Home.route) {
            // Home is a direct destination: discard any nested page and never restore a stale
            // destination state over the hero and its rows.
            val poppedToHome = navController.popBackStack(Screen.Home.route, inclusive = false)
            if (!poppedToHome) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = false
                    }
                    launchSingleTop = true
                    restoreState = false
                }
            }
            requestHomeScrollToTop(navController)
            return true
        }

        navController.navigate(targetRoute) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        return true
    } catch (e: IllegalArgumentException) {
        Log.w("NuvioNavigation", "Route not found in nav graph: $targetRoute", e)
        return false
    }
}

/**
 * Asks Home to drop its saved scroll/focus state and scroll back to the first row. Safe to call when
 * Home is not the current destination: the reset is stored on the Home ViewModel and applied the next
 * time its content is composed.
 */
private fun requestHomeScrollToTop(navController: NavHostController) {
    val homeEntry = try {
        navController.getBackStackEntry(Screen.Home.route)
    } catch (_: IllegalArgumentException) {
        // "home" not yet on the back stack (e.g. nav graph not fully initialized).
        return
    }
    val homeViewModel = androidx.lifecycle.ViewModelProvider(homeEntry)[com.nuvio.tv.ui.screens.home.HomeViewModel::class.java]
    homeViewModel.requestScrollToTop()
}

private fun isBlockedContentKey(key: Key): Boolean {
    return key == Key.DirectionUp ||
        key == Key.DirectionDown ||
        key == Key.DirectionLeft ||
        key == Key.DirectionRight ||
        key == Key.DirectionCenter ||
        key == Key.Enter
}

@Composable
private fun DrawerItemIcon(
    iconRes: Int?,
    icon: ImageVector?,
    modifier: Modifier = Modifier,
    tint: Color = androidx.tv.material3.LocalContentColor.current
) {
    when {
        icon != null -> Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = modifier
        )

        iconRes != null -> Icon(
            painter = rememberRawSvgPainter(iconRes),
            contentDescription = null,
            tint = tint,
            modifier = modifier
        )
    }
}

object LocaleCache {
    const val UNSET = "__UNSET__"

    @Volatile
    var localeTag: String = UNSET
}
