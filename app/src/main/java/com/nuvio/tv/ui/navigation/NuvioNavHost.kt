package com.nuvio.tv.ui.navigation

import android.app.Activity
import com.nuvio.tv.ui.theme.NuvioMotion
import com.nuvio.tv.ui.components.PlaybackAvailabilityProvider
import com.nuvio.tv.ui.components.LocalPlaybackAvailability
import com.nuvio.tv.ui.components.canStream
import com.nuvio.tv.ui.components.freeboxPlaybackPathOrNull
import android.widget.Toast
import android.net.Uri
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.LocalContentFocusRequester
import com.nuvio.tv.ui.theme.NuvioTheme

import androidx.compose.animation.core.tween
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.nuvio.tv.core.build.AppFeaturePolicy
import com.nuvio.tv.core.shizuku.SHIZUKU_STORAGE_PATH_PREFIX
import com.nuvio.tv.domain.model.ExperienceMode
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.data.mediaserver.ServerCatalog
import com.nuvio.tv.data.mediaserver.ServerItemRef
import com.nuvio.tv.data.freebox.FREEBOX_FOLDER_ID_PREFIX
import com.nuvio.tv.data.freebox.freeboxContentIdFor
import com.nuvio.tv.ui.screens.CatalogSeeAllScreen
import com.nuvio.tv.ui.screens.ExperienceModeSelectionScreen
import com.nuvio.tv.ui.screens.LayoutSelectionScreen
import com.nuvio.tv.ui.screens.detail.MetaDetailsScreen
import com.nuvio.tv.ui.screens.home.HomeScreen
import com.nuvio.tv.ui.screens.home.HomeTopBarHeader
import com.nuvio.tv.ui.screens.home.HomeSettingsDrawer
import com.nuvio.tv.ui.screens.home.decodeHomeIptvLiveChannelItemId
import com.nuvio.tv.ui.screens.home.HOME_IPTV_FAVORITE_ITEM_ID_PREFIX
import com.nuvio.tv.ui.screens.addon.AddonManagerScreen
import com.nuvio.tv.ui.screens.addon.CatalogOrderScreen
import com.nuvio.tv.ui.screens.apps.AppsScreen
import com.nuvio.tv.ui.screens.apps.LocalNavigateToManageHomeApps
import com.nuvio.tv.ui.screens.player.PlayerExitReason
import com.nuvio.tv.ui.screens.player.PlayerScreen
import com.nuvio.tv.ui.screens.player.PostPlayRecommendation
import com.nuvio.tv.ui.screens.player.playerBackOpensCurrentEpisodeStreams
import com.nuvio.tv.ui.screens.library.LibraryScreen
import com.nuvio.tv.ui.screens.plugin.PluginScreen
import com.nuvio.tv.ui.screens.search.DiscoverScreen
import com.nuvio.tv.ui.screens.search.SearchScreen
import com.nuvio.tv.ui.reshaped.livetv.IptvVodScreen
import com.nuvio.tv.ui.reshaped.livetv.IptvFavoritesScreen
import com.nuvio.tv.ui.reshaped.livetv.IptvHomeFavoriteScreen
import com.nuvio.tv.reshaped.livetv.LiveTvSource
import com.nuvio.tv.reshaped.livetv.LiveTvSourceType
import com.nuvio.tv.reshaped.livetv.LiveTvRepository
import com.nuvio.tv.reshaped.livetv.LiveTvVodCache
import com.nuvio.tv.reshaped.livetv.LiveTvVodItem
import com.nuvio.tv.reshaped.livetv.LiveTvVodPlayback
import com.nuvio.tv.ui.reshaped.livetv.resolveIptvVod
import com.nuvio.tv.ui.reshaped.livetv.loadIptvSeriesEpisodes
import com.nuvio.tv.ui.screens.settings.AboutScreen
import com.nuvio.tv.ui.screens.settings.LayoutSettingsScreen
import com.nuvio.tv.ui.screens.settings.LicensesAttributionsScreen
import com.nuvio.tv.ui.screens.settings.PlaybackSettingsScreen
import com.nuvio.tv.ui.screens.settings.SettingsScreen
import com.nuvio.tv.ui.screens.settings.SupportersContributorsScreen
import com.nuvio.tv.ui.screens.settings.ThemeSettingsScreen
import com.nuvio.tv.ui.screens.settings.TrackingSettingsScreen
import com.nuvio.tv.ui.screens.settings.TmdbSettingsScreen
import com.nuvio.tv.ui.screens.stream.StreamScreen
import com.nuvio.tv.ui.screens.home.ContinueWatchingItem
import com.nuvio.tv.ui.screens.account.AuthQrSignInScreen
import com.nuvio.tv.ui.screens.cast.CastDetailScreen
import com.nuvio.tv.ui.screens.freebox.FreeboxFolderScreen
import com.nuvio.tv.ui.screens.ServerHubScreen
import com.nuvio.tv.ui.screens.ServerBrowserScreen
import com.nuvio.tv.ui.screens.ServerBrowserViewModel
import com.nuvio.tv.ui.screens.ServerBrowserFileType
import com.nuvio.tv.ui.screens.ServerBrowserEntry
import com.nuvio.tv.ui.screens.ServerFileViewerScreen
import com.nuvio.tv.ui.screens.profile.ProfileSelectionMode
import com.nuvio.tv.ui.screens.profile.ProfileSelectionScreen
import com.nuvio.tv.ui.screens.tmdb.TmdbEntityBrowseScreen
import com.nuvio.tv.ui.screens.home.HeroBackdropState
import com.nuvio.tv.youtube.YouTubeChatOverlay
import com.nuvio.tv.youtube.YouTubePlayerPanel
import com.nuvio.tv.youtube.YouTubeUpNext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

private fun createFreeboxVideoRoute(item: MetaPreview, startFromBeginning: Boolean = false): String {
    val path = item.id.removePrefix("freebox:")
    val year = Regex("\\b(?:19|20)\\d{2}\\b")
        .find("${item.releaseInfo.orEmpty()} ${item.released.orEmpty()}")
        ?.value
    return Screen.Player.createRoute(
        streamUrl = "freebox://media?path=${Uri.encode(path)}",
        title = item.name,
        streamName = "Freebox",
        year = year,
        contentId = freeboxContentIdFor(
            path = path,
            sizeBytes = item.sourceFileSizeBytes,
            lastModifiedMs = item.sourceFileLastModifiedMs,
            fileId = item.sourceFileId,
        ),
        contentType = item.apiType,
        contentName = item.name,
        poster = item.poster,
        backdrop = item.backdropUrl,
        logo = item.logo,
        returnToHomeOnBack = true,
        filename = path.substringAfterLast('/'),
        startFromBeginning = startFromBeginning,
        presentation = freeboxPlayerPresentation(item),
    )
}

private fun freeboxPlayerPresentation(item: MetaPreview): String =
    org.json.JSONObject().apply {
        item.description?.takeIf(String::isNotBlank)
            ?.takeUnless { it.startsWith("Freebox ·", ignoreCase = true) }
            ?.let { put("description", it) }
        item.releaseInfo?.takeIf(String::isNotBlank)?.let { put("releaseInfo", it) }
        item.genres.filter(String::isNotBlank).takeIf { it.isNotEmpty() }
            ?.let { put("genres", org.json.JSONArray(it)) }
        item.runtime?.takeIf(String::isNotBlank)?.let { put("runtime", it) }
        item.director.filter(String::isNotBlank).takeIf { it.isNotEmpty() }
            ?.let { put("director", org.json.JSONArray(it)) }
        item.poster?.takeIf(String::isNotBlank)?.let { put("poster", it) }
        item.backdropUrl?.takeIf(String::isNotBlank)?.let { put("backdrop", it) }
        item.logo?.takeIf(String::isNotBlank)?.let { put("logo", it) }
        item.castMembers.filter { it.name.isNotBlank() }.takeIf { it.isNotEmpty() }
            ?.let { members ->
                put("castDetails", org.json.JSONArray().apply {
                    members.forEach { member ->
                        put(org.json.JSONObject().apply {
                            put("name", member.name)
                            member.character?.takeIf(String::isNotBlank)?.let { put("character", it) }
                            member.photo?.takeIf(String::isNotBlank)?.let { put("photo", it) }
                            member.tmdbId?.let { put("tmdbId", it) }
                        })
                    }
                })
            }
    }.toString()

private fun createFreeboxContinueWatchingRoute(
    progress: WatchProgress,
    path: String,
    preview: MetaPreview? = null,
    startFromBeginning: Boolean = false
): String {
    val filename = path.substringAfterLast('/')
    val title = progress.name.takeIf(String::isNotBlank)
        ?: filename.substringBeforeLast('.', filename)
    return Screen.Player.createRoute(
        streamUrl = "freebox://media?path=${Uri.encode(path)}",
        title = title,
        streamName = "Freebox",
        contentId = progress.contentId,
        contentType = progress.contentType,
        contentName = title,
        poster = progress.poster,
        backdrop = progress.backdrop,
        logo = progress.logo,
        videoId = progress.videoId.takeIf(String::isNotBlank),
        returnToHomeOnBack = true,
        filename = filename,
        startFromBeginning = startFromBeginning,
        presentation = preview?.let(::freeboxPlayerPresentation),
    )
}

/** Route OneTube videos through Nuvio's shared player and its normal back stack. */
private fun youtubePlayerRoute(
    video: com.nuvio.tv.youtube.YtPlayable,
    startFromBeginning: Boolean = false,
    returnToHomeOnBack: Boolean = false,
): String =
    Screen.Player.createRoute(
        streamUrl = video.url,
        title = video.title,
        headers = video.headers.takeIf { it.isNotEmpty() },
        contentId = com.nuvio.tv.youtube.YouTubeProgress.ID_PREFIX + video.videoId,
        contentType = "youtube",
        contentName = video.title,
        poster = video.poster,
        backdrop = video.poster,
        videoId = video.videoId,
        startFromBeginning = startFromBeginning,
        returnToHomeOnBack = returnToHomeOnBack,
        addonName = "YouTube",
        youtubeLive = video.isLive
    )

internal fun installServerApk(
    context: android.content.Context,
    name: String,
    uri: Uri,
    launchInstaller: (Intent) -> Unit,
): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !context.packageManager.canRequestPackageInstalls()) {
        Toast.makeText(context, "Autorisez l’installation d’applications pour JojoTV, puis réessayez.", Toast.LENGTH_LONG).show()
        runCatching {
            context.startActivity(
                Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))
            )
        }.onFailure {
            Toast.makeText(context, "Impossible d’ouvrir les paramètres d’installation.", Toast.LENGTH_LONG).show()
        }
        return false
    }
    val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
        .setDataAndType(uri, "application/vnd.android.package-archive")
        .putExtra(Intent.EXTRA_RETURN_RESULT, true)
    intent.setClipData(android.content.ClipData.newRawUri(name, uri))
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    return runCatching {
        launchInstaller(intent)
        true
    }
        .onFailure { error ->
            val detail = error.localizedMessage?.takeIf(String::isNotBlank)
            val message = detail?.let { "Impossible de lancer l’installation de cet APK : $it" }
                ?: "Impossible de lancer l’installation de cet APK."
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
        .getOrDefault(false)
}

internal fun showApkInstallResult(context: android.content.Context, name: String, resultCode: Int) {
    val message = when (resultCode) {
        Activity.RESULT_OK -> "Installation terminée : $name"
        Activity.RESULT_FIRST_USER -> "Échec de l’installation de $name."
        else -> null
    } ?: return
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

private fun openServerFileExternally(
    context: android.content.Context,
    name: String,
    mimeType: String,
    uri: Uri,
) {
    val intent = Intent(Intent.ACTION_VIEW)
        .setDataAndType(uri, mimeType)
    intent.setClipData(android.content.ClipData.newRawUri(name, uri))
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    runCatching { context.startActivity(intent) }
        .onFailure { Toast.makeText(context, "Aucune application ne peut ouvrir ce fichier.", Toast.LENGTH_LONG).show() }
}

@Composable
fun NuvioNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,
    hideBuiltInHeaders: Boolean = false
) {
    PlaybackAvailabilityProvider {
        // The NavController keeps its back stack ViewModels in the store of the owner it is
        // composed under, so use the session store when MainActivity provides one.
        val navViewModelStoreOwner = LocalNavViewModelStoreOwner.current
            ?: checkNotNull(LocalViewModelStoreOwner.current)
        CompositionLocalProvider(
            LocalViewModelStoreOwner provides navViewModelStoreOwner,
            LocalNavigateToManageHomeApps provides { navController.navigate(Screen.InstalledAppsOrder.route) }
        ) {
            PlaybackNavHost(navController, startDestination, hideBuiltInHeaders)
        }
    }
    com.nuvio.tv.livetv.reminders.LiveTvReminderHost(navController)
    com.nuvio.tv.livetv.sync.CloudSyncHost()
    com.nuvio.tv.livetv.ui.LiveTvLoadingHost(navController)
    com.nuvio.tv.livetv.sports.SportsHost(navController)
    com.nuvio.tv.livetv.sports.SportsPinsHost(navController)
}

@Composable
private fun ModuleTopBarHeader(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val calendarPreferences = remember(context.applicationContext) {
        dagger.hilt.android.EntryPointAccessors.fromApplication(
            context.applicationContext,
            com.nuvio.tv.calendar.CalendarEntryPoint::class.java,
        ).calendarPreferences()
    }
    val calendarSettings by calendarPreferences.settings.collectAsStateWithLifecycle(
        initialValue = com.nuvio.tv.calendar.CalendarSettings(),
    )

    HomeTopBarHeader(
        showCalendarShortcut = calendarSettings.calendarInHeader && !calendarSettings.calendarOff,
        onCalendarClick = { navController.navigate(Screen.Calendar.route) { launchSingleTop = true } },
        onServersClick = { navController.navigate(Screen.ServerHub.route) { launchSingleTop = true } },
        onAppsClick = { navController.navigate(Screen.Apps.route) { launchSingleTop = true } },
        onSettingsClick = { navController.navigate(Screen.Settings.route) { launchSingleTop = true } },
        modifier = modifier,
    )
}

@Composable
private fun ModulePageChrome(
    title: String,
    navController: NavHostController,
) {
    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 108.dp, top = 28.dp)
                .height(36.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = NuvioTheme.colors.TextPrimary,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
        }
        ModuleTopBarHeader(navController, Modifier.align(Alignment.TopEnd))
    }
}

@Composable
private fun PlaybackNavHost(
    navController: NavHostController,
    startDestination: String,
    hideBuiltInHeaders: Boolean
) {
    val playbackAvailability = LocalPlaybackAvailability.current
    val context = LocalContext.current
    var settingsOpenedFromDrawer by rememberSaveable { mutableStateOf(false) }
    fun isStreamToPlayer(from: String, to: String): Boolean {
        return from.startsWith("stream/") && to.startsWith("player/")
    }

    fun isPlayerToStream(from: String, to: String): Boolean {
        return from.startsWith("player/") && to.startsWith("stream/")
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            val from = initialState.destination.route.orEmpty()
            val to = targetState.destination.route.orEmpty()
            val isAutoPlayNav = targetState.arguments
                ?.getString("autoPlayNav")
                ?.toBooleanStrictOrNull() == true
            if (isStreamToPlayer(from, to) && isAutoPlayNav) {
                EnterTransition.None
            } else {
                fadeIn(animationSpec = tween(NuvioMotion.tokens.durations.medium))
            }
        },
        exitTransition = {
            val from = initialState.destination.route.orEmpty()
            val to = targetState.destination.route.orEmpty()
            val isAutoPlayNav = targetState.arguments
                ?.getString("autoPlayNav")
                ?.toBooleanStrictOrNull() == true
            if (isStreamToPlayer(from, to) && isAutoPlayNav) {
                ExitTransition.None
            } else {
                fadeOut(animationSpec = tween(NuvioMotion.tokens.durations.medium))
            }
        },
        popEnterTransition = {
            val from = initialState.destination.route.orEmpty()
            val to = targetState.destination.route.orEmpty()
            val isAutoPlayNav = initialState.arguments
                ?.getString("autoPlayNav")
                ?.toBooleanStrictOrNull() == true
            if (isPlayerToStream(from, to) && isAutoPlayNav) {
                EnterTransition.None
            } else {
                fadeIn(animationSpec = tween(NuvioMotion.tokens.durations.medium))
            }
        },
        popExitTransition = {
            val from = initialState.destination.route.orEmpty()
            val to = targetState.destination.route.orEmpty()
            val isAutoPlayNav = initialState.arguments
                ?.getString("autoPlayNav")
                ?.toBooleanStrictOrNull() == true
            if (isPlayerToStream(from, to) && isAutoPlayNav) {
                ExitTransition.None
            } else {
                fadeOut(animationSpec = tween(NuvioMotion.tokens.durations.medium))
            }
        }
    ) {
        composable(Screen.ExperienceModeSelection.route) {
            ExperienceModeSelectionScreen(
                onContinue = { mode ->
                    val destination = if (mode == ExperienceMode.ESSENTIAL) {
                        Screen.Home.route
                    } else {
                        Screen.LayoutSelection.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.ExperienceModeSelection.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.LayoutSelection.route) {
            LayoutSelectionScreen(
                onContinue = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.LayoutSelection.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val calendarPreferences = remember(context.applicationContext) {
                dagger.hilt.android.EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    com.nuvio.tv.calendar.CalendarEntryPoint::class.java,
                ).calendarPreferences()
            }
            val calendarSettings by calendarPreferences.settings.collectAsStateWithLifecycle(
                initialValue = com.nuvio.tv.calendar.CalendarSettings(),
            )
            val homeYouTubeViewModel =
                androidx.hilt.navigation.compose.hiltViewModel<com.nuvio.tv.youtube.YouTubeViewModel>()
            val homeViewModel = androidx.hilt.navigation.compose.hiltViewModel<com.nuvio.tv.ui.screens.home.HomeViewModel>()
            val homeScope = rememberCoroutineScope()

            fun createContinueWatchingRoute(
                item: ContinueWatchingItem,
                manualSelection: Boolean = false,
                startFromBeginning: Boolean = false
            ): String {
                val freeboxProgress = (item as? ContinueWatchingItem.InProgress)?.progress
                val freeboxPath = item.freeboxPlaybackPathOrNull()
                if (freeboxProgress != null && freeboxPath != null) {
                    return createFreeboxContinueWatchingRoute(
                        progress = freeboxProgress,
                        path = freeboxPath,
                        preview = (item as? ContinueWatchingItem.InProgress)?.homeSourcePreview,
                        startFromBeginning = startFromBeginning,
                    )
                }
                return when (item) {
                    is ContinueWatchingItem.InProgress -> Screen.Stream.createRoute(
                        videoId = item.progress.videoId,
                        contentType = item.progress.contentType,
                        title = item.progress.name,
                        poster = item.progress.poster,
                        backdrop = item.progress.backdrop,
                        logo = item.progress.logo,
                        season = item.progress.season,
                        episode = item.progress.episode,
                        episodeName = item.progress.episodeTitle,
                        genres = null,
                        year = null,
                        contentId = item.progress.contentId,
                        contentName = item.progress.name,
                        runtime = null,
                        manualSelection = manualSelection,
                        returnToDetailOnBack = item.progress.contentType.equals("series", ignoreCase = true),
                        returnToHomeOnBack = true,
                        startFromBeginning = startFromBeginning,
                        contentLanguage = item.contentLanguage
                    )
                    is ContinueWatchingItem.NextUp -> Screen.Stream.createRoute(
                        videoId = item.info.videoId,
                        contentType = item.info.contentType,
                        title = item.info.name,
                        poster = item.info.poster,
                        backdrop = item.info.backdrop,
                        logo = item.info.logo,
                        season = item.info.season,
                        episode = item.info.episode,
                        episodeName = item.info.episodeTitle,
                        genres = null,
                        year = null,
                        contentId = item.info.contentId,
                        contentName = item.info.name,
                        runtime = null,
                        manualSelection = manualSelection,
                        returnToDetailOnBack = item.info.contentType.equals("series", ignoreCase = true),
                        returnToHomeOnBack = true,
                        startFromBeginning = startFromBeginning,
                        contentLanguage = item.info.contentLanguage
                    )
                }
            }

            fun playContinueWatchingItem(
                item: ContinueWatchingItem,
                manualSelection: Boolean = false,
                startFromBeginning: Boolean = false
            ) {
                val youtubeProgress = (item as? ContinueWatchingItem.InProgress)?.progress
                    ?.takeIf { com.nuvio.tv.youtube.YouTubeProgress.isYouTubeId(it.contentId) }
                if (youtubeProgress != null) {
                    homeYouTubeViewModel.playVideoById(
                        videoId = youtubeProgress.contentId.removePrefix(
                            com.nuvio.tv.youtube.YouTubeProgress.ID_PREFIX
                        ),
                        title = youtubeProgress.name,
                        thumbnail = youtubeProgress.backdrop ?: youtubeProgress.poster,
                        onReady = { video ->
                            navController.navigate(
                                youtubePlayerRoute(
                                    video = video,
                                    startFromBeginning = startFromBeginning,
                                    returnToHomeOnBack = true,
                                )
                            )
                        },
                        onFailure = { error ->
                            Toast.makeText(
                                context,
                                error.message?.takeIf(String::isNotBlank)
                                    ?: context.getString(R.string.playback_unavailable_message),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                    return
                }

                val iptvProgress = (item as? ContinueWatchingItem.InProgress)?.progress
                val iptvType = when {
                    iptvProgress?.contentType.equals("movie", ignoreCase = true) &&
                        iptvProgress?.contentId?.contains(":movie:") == true -> "movie"
                    iptvProgress?.contentType.equals("series", ignoreCase = true) &&
                        iptvProgress?.contentId?.contains(":series:") == true -> "series"
                    else -> null
                }
                if (iptvProgress != null && iptvType != null) {
                    homeScope.launch {
                        try {
                            LiveTvRepository.ensureLoaded(
                                context,
                                homeViewModel.profileManager.activeProfileId.value,
                            )
                            val sources = withTimeoutOrNull(10_000L) {
                                LiveTvRepository.uiState.first { it.sourceStorageReady }.sources
                            }.orEmpty().filter { source ->
                                (source.type == LiveTvSourceType.Xtream && source.xtream.isConfigured) ||
                                    (source.type == LiveTvSourceType.Stalker && source.stalker.isConfigured)
                            }

                            val cachedVod = sources.firstNotNullOfOrNull { source ->
                                LiveTvVodCache.read(context, source.id, iptvType)
                                    .firstOrNull { it.id == iptvProgress.contentId }
                                    ?.let { source to it }
                            }
                            if (cachedVod == null) {
                                Toast.makeText(context, R.string.iptv_vod_playback_error, Toast.LENGTH_SHORT).show()
                                return@launch
                            }

                            val (source, cachedItem) = cachedVod
                            val itemToPlay = if (iptvType == "movie") {
                                cachedItem
                            } else {
                                val episodes = loadIptvSeriesEpisodes(source, cachedItem)
                                val byVideoId = episodes.filter {
                                    it.id == iptvProgress.videoId || it.providerId == iptvProgress.videoId
                                }.singleOrNull()
                                val byEpisodeNumber = if (iptvProgress.season != null && iptvProgress.episode != null) {
                                    episodes.filter {
                                        it.season == iptvProgress.season && it.episode == iptvProgress.episode
                                    }.singleOrNull()
                                } else {
                                    null
                                }
                                byVideoId ?: byEpisodeNumber
                            }
                            if (itemToPlay == null) {
                                Toast.makeText(context, R.string.iptv_vod_playback_error, Toast.LENGTH_SHORT).show()
                                return@launch
                            }

                            val playback = resolveIptvVod(source, itemToPlay)
                            val isSeries = iptvType == "series"
                            navController.navigate(
                                Screen.Player.createRoute(
                                    streamUrl = playback.url,
                                    title = if (isSeries) itemToPlay.title else iptvProgress.name,
                                    streamName = source.label,
                                    year = itemToPlay.year.takeIf(String::isNotBlank),
                                    headers = playback.headers,
                                    contentId = iptvProgress.contentId,
                                    contentType = iptvProgress.contentType,
                                    contentName = iptvProgress.name,
                                    poster = iptvProgress.poster ?: itemToPlay.posterUrl,
                                    backdrop = iptvProgress.backdrop,
                                    logo = iptvProgress.logo,
                                    videoId = iptvProgress.videoId
                                        .takeIf { it.isNotBlank() && it != iptvProgress.contentId },
                                    season = iptvProgress.season,
                                    episode = iptvProgress.episode,
                                    episodeTitle = iptvProgress.episodeTitle ?: itemToPlay.title.takeIf { isSeries },
                                    returnToHomeOnBack = true,
                                    startFromBeginning = startFromBeginning,
                                )
                            )
                        } catch (failure: Exception) {
                            if (failure is CancellationException) throw failure
                            Toast.makeText(context, R.string.iptv_vod_playback_error, Toast.LENGTH_SHORT).show()
                        }
                    }
                    return
                }

                if (!playbackAvailability.canStream(item)) {
                    Toast.makeText(context, R.string.playback_unavailable_message, Toast.LENGTH_SHORT).show()
                    return
                }
                navController.navigate(
                    createContinueWatchingRoute(
                        item = item,
                        manualSelection = manualSelection,
                        startFromBeginning = startFromBeginning
                    )
                )
            }

            HomeScreen(
                viewModel = homeViewModel,
                showCalendarShortcut = calendarSettings.calendarInHeader && !calendarSettings.calendarOff,
                onNavigateToCalendar = {
                    navController.navigate(Screen.Calendar.route) { launchSingleTop = true }
                },
                onNavigateToServers = { navController.navigate(Screen.ServerHub.route) },
                onManageHomeApps = { navController.navigate(Screen.InstalledAppsOrder.route) },
                onNavigateToApps = {
                    navController.navigate(Screen.Apps.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToDetail = { itemId, itemType, addonBaseUrl ->
                    val liveChannelIdentity = decodeHomeIptvLiveChannelItemId(itemId)
                    val serverFolderShortcut = com.nuvio.tv.data.local.ServerBrowserShortcutStore
                        .decodeFolderShortcutId(itemId)
                    val serverFileShortcut = com.nuvio.tv.data.local.ServerBrowserShortcutStore
                        .decodeFileShortcutId(itemId)
                    if (serverFolderShortcut != null) {
                        navController.navigate(Screen.ServerBrowser.createRoute(serverFolderShortcut.first, serverFolderShortcut.second))
                    } else if (serverFileShortcut != null) {
                        val parent = serverFileShortcut.second.substringBeforeLast('/', "")
                        navController.navigate(
                            Screen.ServerBrowser.createRoute(serverFileShortcut.first, parent, serverFileShortcut.second)
                        )
                    } else if (liveChannelIdentity != null) {
                        val channel = LiveTvRepository.uiState.value.channels.firstOrNull {
                            it.sourceId == liveChannelIdentity.first && it.id == liveChannelIdentity.second
                        }
                        if (channel == null) {
                            navController.navigate(com.nuvio.tv.reshaped.livetv.LIVE_TV_ROUTE) {
                                launchSingleTop = true
                            }
                        } else {
                            homeScope.launch {
                                val liveState = LiveTvRepository.uiState.value
                                LiveTvRepository.setZapList(
                                    liveState.channels.filter { it.streamUrl in liveState.favoriteUrls },
                                    folderKey = "favorites"
                                )
                                val route = runCatching {
                                    com.nuvio.tv.ui.reshaped.livetv.liveTvPlayerRoute(
                                        channel,
                                        homeViewModel.profileManager.activeProfileId.value
                                    )
                                }.getOrNull()
                                if (route != null) {
                                    navController.navigate(route)
                                } else {
                                    navController.navigate(com.nuvio.tv.reshaped.livetv.LIVE_TV_ROUTE) {
                                        launchSingleTop = true
                                    }
                                }
                            }
                        }
                    } else if (itemId.startsWith(HOME_IPTV_FAVORITE_ITEM_ID_PREFIX)) {
                        navController.navigate(
                            Screen.HomeIptvFavorite.createRoute(
                                Uri.decode(itemId.removePrefix(HOME_IPTV_FAVORITE_ITEM_ID_PREFIX))
                            )
                        )
                    } else if (itemId.startsWith(FREEBOX_FOLDER_ID_PREFIX)) {
                        navController.navigate(
                            Screen.FreeboxFolder.createRoute(
                                path = itemId.removePrefix(FREEBOX_FOLDER_ID_PREFIX),
                                type = itemType,
                            )
                        )
                    } else if (itemId.startsWith("freebox:")) {
                        val homeState = homeViewModel.uiState.value
                        val homeItem = (homeState.catalogRows.asSequence()
                            .flatMap { it.items.asSequence() } +
                            homeState.homeRows.asSequence()
                                .filterIsInstance<com.nuvio.tv.ui.screens.home.HomeRow.Catalog>()
                                .flatMap { it.row.items.asSequence() } +
                            homeState.heroItems.asSequence())
                            .firstOrNull { it.id == itemId }
                        val freeboxItem = homeViewModel.enrichedPreviews.value[itemId] ?: homeItem
                        if (freeboxItem != null) {
                            navController.navigate(createFreeboxVideoRoute(freeboxItem))
                        } else {
                            val path = itemId.removePrefix("freebox:")
                            val title = path.substringAfterLast('/').substringBeforeLast('.', path.substringAfterLast('/'))
                            navController.navigate(
                                Screen.Player.createRoute(
                                    streamUrl = "freebox://media?path=${Uri.encode(path)}",
                                    title = title,
                                    streamName = "Freebox",
                                    contentId = itemId,
                                    contentType = itemType,
                                    contentName = title,
                                    returnToHomeOnBack = true,
                                    filename = path.substringAfterLast('/')
                                )
                            )
                        }
                    } else {
                        val heroBackdrop = HeroBackdropState.consumeAndClear()
                        navController.navigate(
                            Screen.Detail.createRoute(
                                itemId = itemId,
                                itemType = itemType,
                                addonBaseUrl = addonBaseUrl,
                                heroBackdropUrl = heroBackdrop
                            )
                        )
                    }
                },
                onPlayFreeboxVideoFromBeginning = { item ->
                    navController.navigate(createFreeboxVideoRoute(item, startFromBeginning = true))
                },
                onContinueWatchingClick = { item ->
                    playContinueWatchingItem(item)
                },
                onContinueWatchingStartFromBeginning = { item ->
                    playContinueWatchingItem(item, startFromBeginning = true)
                },
                onContinueWatchingPlayManually = { item ->
                    playContinueWatchingItem(item, manualSelection = true)
                },
                onNavigateToCatalogSeeAll = { catalogId, addonId, type ->
                    navController.navigate(Screen.CatalogSeeAll.createRoute(catalogId, addonId, type))
                },
                onNavigateToFolderDetail = { collectionId, folderId ->
                    navController.navigate(Screen.FolderDetail.createRoute(collectionId, folderId))
                },
                onNavigateToSettings = {
                    settingsOpenedFromDrawer = true
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("itemId") { type = NavType.StringType },
                navArgument("itemType") { type = NavType.StringType },
                navArgument("addonBaseUrl") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("returnFocusSeason") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("returnFocusEpisode") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("returnToHomeOnBack") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "false"
                },
                navArgument("heroBackdropUrl") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("playOnLoad") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "false"
                },
                navArgument("manualSelection") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "false"
                },
                navArgument("iptvFavoriteKey") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val detailArgs = backStackEntry.arguments
            if (ServerCatalog.isCollection(detailArgs?.getString("itemId"), detailArgs?.getString("itemType"))) {
                ServerCatalogDestination(navController, backStackEntry)
                return@composable
            }
            val savedState = backStackEntry.savedStateHandle
            val returnToHomeOnBack = detailArgs
                ?.getString("returnToHomeOnBack")
                ?.toBooleanStrictOrNull() == true
            val returnFocusSeason by savedState.getStateFlow(
                "returnFocusSeason", detailArgs?.getString("returnFocusSeason")?.toIntOrNull()
            ).collectAsState()
            val returnFocusEpisode by savedState.getStateFlow(
                "returnFocusEpisode", detailArgs?.getString("returnFocusEpisode")?.toIntOrNull()
            ).collectAsState()
            val heroRestoreToken by savedState.getStateFlow(
                "heroRestoreToken", 0
            ).collectAsState()
            val heroBackdropUrl = detailArgs?.getString("heroBackdropUrl")?.takeIf { it.isNotBlank() }
            val playOnLoad = detailArgs?.getString("playOnLoad")?.toBooleanStrictOrNull() == true
            val manualSelection = detailArgs?.getString("manualSelection")?.toBooleanStrictOrNull() == true
            val iptvFavoriteKey = detailArgs?.getString("iptvFavoriteKey")?.takeIf { it.isNotBlank() }
            val isIptvMoviePlayback = iptvFavoriteKey != null &&
                detailArgs?.getString("itemType").equals("movie", ignoreCase = true)
            val detailScope = rememberCoroutineScope()

            fun playIptvFavoriteMovie(
                title: String,
                poster: String?,
                backdrop: String?,
                logo: String?,
                year: String?,
                contentLanguage: String?,
                startFromBeginning: Boolean = false,
            ) {
                val favoriteKey = iptvFavoriteKey ?: return
                detailScope.launch {
                    try {
                        var favorite: Pair<LiveTvSource, LiveTvVodItem>? = null
                        val sources = LiveTvRepository.uiState.value.sources.filter { source ->
                            (source.type == LiveTvSourceType.Xtream && source.xtream.isConfigured) ||
                                (source.type == LiveTvSourceType.Stalker && source.stalker.isConfigured)
                        }
                        for (source in sources) {
                            val item = LiveTvVodCache.read(context, source.id, "movie")
                                .firstOrNull { it.favoriteKey == favoriteKey }
                            if (item != null) {
                                favorite = source to item
                                break
                            }
                        }
                        val favoriteEntry = favorite
                        if (favoriteEntry == null) {
                            Toast.makeText(context, R.string.iptv_vod_playback_error, Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        val source = favoriteEntry.first
                        val item = favoriteEntry.second
                        val playback = resolveIptvVod(source, item)
                        val playbackTitle = title.ifBlank { item.title }
                        navController.navigate(
                            Screen.Player.createRoute(
                                streamUrl = playback.url,
                                title = playbackTitle,
                                streamName = source.label,
                                year = year?.takeIf(String::isNotBlank) ?: item.year,
                                headers = playback.headers,
                                contentId = item.id,
                                contentType = "movie",
                                contentName = playbackTitle,
                                poster = poster?.takeIf(String::isNotBlank) ?: item.posterUrl,
                                backdrop = backdrop,
                                logo = logo,
                                returnToDetailOnBack = true,
                                startFromBeginning = startFromBeginning,
                                contentLanguage = contentLanguage,
                            )
                        )
                    } catch (failure: Exception) {
                        if (failure is CancellationException) throw failure
                        Toast.makeText(context, R.string.iptv_vod_playback_error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            DetailChildHost(parentNavController = navController) { childNav ->
            MetaDetailsScreen(
                returnFocusSeason = returnFocusSeason,
                returnFocusEpisode = returnFocusEpisode,
                heroRestoreToken = heroRestoreToken,
                heroBackdropUrl = heroBackdropUrl,
                playOnLoad = playOnLoad,
                playOnLoadManually = manualSelection,
                onReturnFocusConsumed = {
                    savedState["returnFocusSeason"] = null
                    savedState["returnFocusEpisode"] = null
                },
                onBackPress = {
                    if (returnToHomeOnBack) {
                        val popped = navController.popBackStack(Screen.Home.route, inclusive = false)
                        if (!popped) {
                            navController.navigate(Screen.Home.route) {
                                launchSingleTop = true
                            }
                        }
                    } else {
                        navController.popBackStack()
                    }
                },
                onNavigateToCastDetail = { personId, personName, preferCrew ->
                    childNav.navigate(Screen.CastDetail.createRoute(personId, personName, preferCrew))
                },
                onNavigateToTmdbEntityBrowse = { entityKind, entityId, entityName, sourceType ->
                    childNav.navigate(
                        Screen.TmdbEntityBrowse.createRoute(
                            entityKind = entityKind,
                            entityId = entityId,
                            entityName = entityName,
                            sourceType = sourceType
                        )
                    )
                },
                onNavigateToDetail = { itemId, itemType, addonBaseUrl ->
                    childNav.navigateNestedDetail(itemId, itemType, addonBaseUrl)
                },
                onPlayClick = { videoId, contentType, contentId, title, poster, backdrop, logo, season, episode, episodeName, genres, year, runtime, contentLanguage ->
                    if (isIptvMoviePlayback) {
                        playIptvFavoriteMovie(title, poster, backdrop, logo, year, contentLanguage)
                    } else {
                        navController.navigate(
                            Screen.Stream.createRoute(
                                videoId = videoId,
                                contentType = contentType,
                                title = title,
                                poster = poster,
                                backdrop = backdrop,
                                logo = logo,
                                season = season,
                                episode = episode,
                                episodeName = episodeName,
                                genres = genres,
                                year = year,
                                contentId = contentId,
                                contentName = title,
                                runtime = runtime,
                                returnToDetailOnBack = contentType.equals("series", ignoreCase = true),
                                contentLanguage = contentLanguage
                            )
                        )
                    }
                },
                onPlayManuallyClick = { videoId, contentType, contentId, title, poster, backdrop, logo, season, episode, episodeName, genres, year, runtime, contentLanguage ->
                    if (isIptvMoviePlayback) {
                        playIptvFavoriteMovie(title, poster, backdrop, logo, year, contentLanguage)
                    } else {
                        navController.navigate(
                            Screen.Stream.createRoute(
                                videoId = videoId,
                                contentType = contentType,
                                title = title,
                                poster = poster,
                                backdrop = backdrop,
                                logo = logo,
                                season = season,
                                episode = episode,
                                episodeName = episodeName,
                                genres = genres,
                                year = year,
                                contentId = contentId,
                                contentName = title,
                                runtime = runtime,
                                manualSelection = true,
                                returnToDetailOnBack = contentType.equals("series", ignoreCase = true),
                                contentLanguage = contentLanguage
                            )
                        )
                    }
                },
                onPlayStartFromBeginningClick = { videoId, contentType, contentId, title, poster, backdrop, logo, season, episode, episodeName, genres, year, runtime, contentLanguage ->
                    if (isIptvMoviePlayback) {
                        playIptvFavoriteMovie(
                            title, poster, backdrop, logo, year, contentLanguage,
                            startFromBeginning = true,
                        )
                    } else {
                        navController.navigate(
                            Screen.Stream.createRoute(
                                videoId = videoId,
                                contentType = contentType,
                                title = title,
                                poster = poster,
                                backdrop = backdrop,
                                logo = logo,
                                season = season,
                                episode = episode,
                                episodeName = episodeName,
                                genres = genres,
                                year = year,
                                contentId = contentId,
                                contentName = title,
                                runtime = runtime,
                                startFromBeginning = true,
                                returnToDetailOnBack = contentType.equals("series", ignoreCase = true),
                                contentLanguage = contentLanguage
                            )
                        )
                    }
                },
                isIptvMoviePlayback = isIptvMoviePlayback,
            )
            }
        }

        composable(
            route = Screen.Stream.route,
            arguments = listOf(
                navArgument("videoId") { type = NavType.StringType },
                navArgument("contentType") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("poster") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("backdrop") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("logo") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("season") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("episode") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("episodeName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("genres") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("year") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("contentId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("contentName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("runtime") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("manualSelection") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "false"
                },
                navArgument("returnToDetailOnBack") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "false"
                },
                navArgument("returnToHomeOnBack") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "false"
                },
                navArgument("startFromBeginning") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "false"
                },
                navArgument("contentLanguage") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("profileId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val streamArgs = backStackEntry.arguments
            val streamSavedState = backStackEntry.savedStateHandle
            val restoreSourceSelection by streamSavedState.getStateFlow(
                SOURCE_SELECTION_RESTORE_STATE_KEY, false
            ).collectAsState()
            val returnToDetailOnBack = streamArgs
                ?.getString("returnToDetailOnBack")
                ?.toBooleanStrictOrNull() == true
            val returnToHomeOnBack = streamArgs
                ?.getString("returnToHomeOnBack")
                ?.toBooleanStrictOrNull() == true
            val startFromBeginning = streamArgs
                ?.getString("startFromBeginning")
                ?.toBooleanStrictOrNull() == true
            StreamScreen(
                startFromBeginning = startFromBeginning,
                restoreSourceSelection = restoreSourceSelection,
                onSourceSelectionRestoreHandled = {
                    streamSavedState[SOURCE_SELECTION_RESTORE_STATE_KEY] = false
                },
                onBackPress = {
                    val streamContentType = streamArgs?.getString("contentType").orEmpty()
                    val streamContentId = streamArgs?.getString("contentId").orEmpty()
                    val season = streamArgs?.getString("season")?.toIntOrNull()
                    val episode = streamArgs?.getString("episode")?.toIntOrNull()
                    if (streamContentType.equals("series", ignoreCase = true) && streamContentId.isNotBlank()) {
                        val detailEntry = runCatching { navController.getBackStackEntry(Screen.Detail.route) }.getOrNull()
                        if (detailEntry != null) {
                            detailEntry.savedStateHandle["returnFocusSeason"] = season
                            detailEntry.savedStateHandle["returnFocusEpisode"] = episode
                            navController.popBackStack(Screen.Detail.route, inclusive = false)
                        } else {
                            navController.navigate(
                                Screen.Detail.createRoute(
                                    itemId = streamContentId,
                                    itemType = streamContentType,
                                    addonBaseUrl = null,
                                    returnFocusSeason = season,
                                    returnFocusEpisode = episode,
                                    returnToHomeOnBack = returnToHomeOnBack,
                                    heroBackdropUrl = streamArgs?.getString("backdrop")
                                )
                            ) {
                                popUpTo(Screen.Stream.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    } else {
                        navController.popBackStack()
                    }
                },
                onStreamSelected = { playbackInfo ->
                    val streamUrl = playbackInfo.url
                    streamUrl?.let { url ->
                        navController.navigate(
                            Screen.Player.createRoute(
                                streamUrl = url,
                                title = playbackInfo.title,
                                streamName = playbackInfo.streamName,
                                year = playbackInfo.year,
                                headers = playbackInfo.headers,
                                contentId = playbackInfo.contentId,
                                contentType = playbackInfo.contentType,
                                contentName = playbackInfo.contentName,
                                poster = playbackInfo.poster,
                                backdrop = playbackInfo.backdrop,
                                logo = playbackInfo.logo,
                                videoId = playbackInfo.videoId,
                                season = playbackInfo.season,
                                episode = playbackInfo.episode,
                                episodeTitle = playbackInfo.episodeTitle,
                                bingeGroup = playbackInfo.bingeGroup,
                                autoPlayNav = false,
                                returnToDetailOnBack = returnToDetailOnBack,
                                returnToHomeOnBack = returnToHomeOnBack,
                                filename = playbackInfo.filename,
                                videoHash = playbackInfo.videoHash,
                                videoSize = playbackInfo.videoSize,
                                startFromBeginning = startFromBeginning,
                                addonName = playbackInfo.addonName,
                                addonLogo = playbackInfo.addonLogo,
                                streamDescription = playbackInfo.streamDescription,
                                contentLanguage = playbackInfo.contentLanguage,
                                profileId = playbackInfo.profileId,
                                audioDelayMs = playbackInfo.audioDelayMs,
                                audioDelayLanguage = playbackInfo.audioDelayLanguage
                            )
                        )
                    }
                },
                onAutoPlayResolved = { playbackInfo ->
                    val autoPlayUrl = playbackInfo.url
                    autoPlayUrl?.let { url ->
                        navController.navigate(
                            Screen.Player.createRoute(
                                streamUrl = url,
                                title = playbackInfo.title,
                                streamName = playbackInfo.streamName,
                                year = playbackInfo.year,
                                headers = playbackInfo.headers,
                                contentId = playbackInfo.contentId,
                                contentType = playbackInfo.contentType,
                                contentName = playbackInfo.contentName,
                                poster = playbackInfo.poster,
                                backdrop = playbackInfo.backdrop,
                                logo = playbackInfo.logo,
                                videoId = playbackInfo.videoId,
                                season = playbackInfo.season,
                                episode = playbackInfo.episode,
                                episodeTitle = playbackInfo.episodeTitle,
                                bingeGroup = playbackInfo.bingeGroup,
                                autoPlayNav = true,
                                returnToDetailOnBack = returnToDetailOnBack,
                                returnToHomeOnBack = returnToHomeOnBack,
                                filename = playbackInfo.filename,
                                videoHash = playbackInfo.videoHash,
                                videoSize = playbackInfo.videoSize,
                                startFromBeginning = startFromBeginning,
                                addonName = playbackInfo.addonName,
                                addonLogo = playbackInfo.addonLogo,
                                streamDescription = playbackInfo.streamDescription,
                                contentLanguage = playbackInfo.contentLanguage,
                                profileId = playbackInfo.profileId,
                                audioDelayMs = playbackInfo.audioDelayMs,
                                audioDelayLanguage = playbackInfo.audioDelayLanguage
                            )
                        ) {
                            popUpTo(Screen.Stream.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("streamUrl") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("streamName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("year") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("headers") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("contentId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("contentType") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("contentName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("poster") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("backdrop") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("logo") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("videoId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("season") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("episode") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("episodeTitle") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("bingeGroup") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("autoPlayNav") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "false"
                },
                navArgument("returnToDetailOnBack") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "false"
                },
                navArgument("returnToHomeOnBack") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "false"
                },
                navArgument("filename") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("videoHash") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("videoSize") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("startFromBeginning") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "false"
                },
                navArgument("addonName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("addonLogo") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("streamDescription") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("contentLanguage") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("launchStartedAtMs") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("profileId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("audioDelayMs") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("audioDelayLanguage") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("youtubeLive") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "false"
                },
                navArgument("presentation") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val homeBackStackEntry = remember(navController) {
                runCatching { navController.getBackStackEntry(Screen.Home.route) }.getOrNull()
            }
            val homeViewModel = homeBackStackEntry?.let { homeEntry ->
                androidx.hilt.navigation.compose.hiltViewModel<com.nuvio.tv.ui.screens.home.HomeViewModel>(homeEntry)
            }
            fun requestHomeHeroFocusAfterPlayback() {
                homeViewModel?.requestScrollToTop()
            }

            fun popBackToStream(): Boolean {
                val autoPlayNavigation = backStackEntry.arguments
                    ?.getString("autoPlayNav")
                    ?.toBooleanStrictOrNull() == true
                val restoreEntry = navController.previousBackStackEntry?.takeIf { previousEntry ->
                    shouldArmSourceSelectionRestore(
                        autoPlayNavigation = autoPlayNavigation,
                        previousRoute = previousEntry.destination.route
                    )
                }
                val returnedToStream = navController.popBackStack(Screen.Stream.route, inclusive = false)
                if (returnedToStream && restoreEntry != null) {
                    restoreEntry.savedStateHandle[SOURCE_SELECTION_RESTORE_STATE_KEY] = true
                }
                return returnedToStream
            }

            fun returnFromIptvVodPlayback(
                contentId: String,
                contentType: String,
                returnToDetailOnBack: Boolean,
                returnToHomeOnBack: Boolean,
                focusSeason: Int? = null,
                focusEpisode: Int? = null,
                restoreDetailHero: Boolean = false,
            ): Boolean {
                val isIptvVod = when {
                    contentType.equals("movie", ignoreCase = true) -> contentId.contains(":movie:")
                    contentType.equals("series", ignoreCase = true) -> contentId.contains(":series:")
                    else -> false
                }
                if (!isIptvVod) return false

                val detailEntry = if (returnToDetailOnBack) {
                    navController.currentBackStack.value.lastOrNull {
                        it.destination.route?.startsWith("detail/") == true
                    }
                } else {
                    null
                }
                if (detailEntry != null) {
                    detailEntry.savedStateHandle["returnFocusSeason"] = focusSeason
                    detailEntry.savedStateHandle["returnFocusEpisode"] = focusEpisode
                    if (restoreDetailHero) {
                        val token = (detailEntry.savedStateHandle.get<Int>("heroRestoreToken") ?: 0) + 1
                        detailEntry.savedStateHandle["heroRestoreToken"] = token
                    }
                    if (navController.popBackStack(Screen.Detail.route, inclusive = false)) return true
                }

                val shouldReturnHome = returnToHomeOnBack ||
                    navController.previousBackStackEntry?.destination?.route == Screen.Home.route
                if (shouldReturnHome) {
                    if (!navController.popBackStack(Screen.Home.route, inclusive = false)) {
                        navController.navigate(Screen.Home.route) { launchSingleTop = true }
                    }
                } else if (!navController.popBackStack()) {
                    navController.navigate(Screen.Home.route) { launchSingleTop = true }
                }
                return true
            }

            fun navigateFromPostPlay(
                recommendation: PostPlayRecommendation,
                playOnLoad: Boolean,
                manualSelection: Boolean = false
            ) {
                val returnToHomeOnBack = backStackEntry.arguments
                    ?.getString("returnToHomeOnBack")
                    ?.toBooleanStrictOrNull() == true
                val playbackRootRoute = postPlayRecommendationPopUpRoute(
                    navController.previousBackStackEntry?.destination?.route
                )
                navController.navigate(
                    Screen.Detail.createRoute(
                        itemId = recommendation.id,
                        itemType = recommendation.contentType,
                        addonBaseUrl = recommendation.sourceAddonBaseUrl,
                        returnToHomeOnBack = returnToHomeOnBack,
                        heroBackdropUrl = recommendation.backdrop,
                        playOnLoad = playOnLoad,
                        manualSelection = manualSelection
                    )
                ) {
                    popUpTo(playbackRootRoute) { inclusive = true }
                }
            }

            val youtubeContentId = backStackEntry.arguments?.getString("contentId")
                ?.takeIf { com.nuvio.tv.youtube.YouTubeProgress.isYouTubeId(it) }
            val youtubeViewModel = if (youtubeContentId != null) {
                runCatching { navController.getBackStackEntry(Screen.YouTube.route) }
                    .getOrNull()
                    ?.let { entry ->
                        androidx.hilt.navigation.compose.hiltViewModel<com.nuvio.tv.youtube.YouTubeViewModel>(entry)
                    }
            } else {
                null
            }
            val youtubePlayerViewModel: com.nuvio.tv.youtube.YouTubePlayerViewModel =
                androidx.hilt.navigation.compose.hiltViewModel()
            val youtubePlayerState by youtubePlayerViewModel.state.collectAsState()
            val youtubeChatOn = androidx.compose.runtime.remember(youtubeContentId) {
                mutableStateOf(false)
            }
            val youtubeChatSettingsOn = androidx.compose.runtime.remember(youtubeContentId) {
                mutableStateOf(false)
            }
            val youtubeChatAppearance = androidx.compose.runtime.remember(context) {
                mutableStateOf(com.nuvio.tv.youtube.loadChatAppearance(context))
            }
            androidx.compose.runtime.LaunchedEffect(youtubeContentId) {
                youtubeContentId
                    ?.removePrefix(com.nuvio.tv.youtube.YouTubeProgress.ID_PREFIX)
                    ?.let(youtubePlayerViewModel::load)
            }
            androidx.compose.runtime.DisposableEffect(youtubeContentId) {
                onDispose { youtubePlayerViewModel.stopChat() }
            }
            val playerViewModel: com.nuvio.tv.ui.screens.player.PlayerViewModel =
                androidx.hilt.navigation.compose.hiltViewModel()
            val playerTimeline by playerViewModel.playbackTimeline.collectAsState()
            val playerUiState by playerViewModel.uiState.collectAsState()
            val youtubePlayerScope = rememberCoroutineScope()
            val youtubeLive = backStackEntry.arguments?.getString("youtubeLive")
                ?.toBooleanStrictOrNull() == true

            PlayerScreen(
                viewModel = playerViewModel,
                youtubeChatButtonVisible = youtubeContentId != null &&
                    youtubePlayerState.info?.liveChatKey != null,
                onYouTubeChat = {
                    if (!youtubeChatOn.value) {
                        youtubeChatOn.value = true
                        youtubeChatSettingsOn.value = true
                        youtubePlayerViewModel.startChat()
                    } else {
                        youtubeChatOn.value = false
                        youtubeChatSettingsOn.value = false
                        youtubePlayerViewModel.stopChat()
                    }
                },
                extraOverlay = {
                    if (youtubeContentId != null && youtubeChatOn.value) {
                        YouTubeChatOverlay(
                            youtubePlayerViewModel,
                            youtubeChatAppearance.value
                        )
                    }
                    if (youtubeContentId != null && youtubeChatSettingsOn.value) {
                        com.nuvio.tv.youtube.YouTubeChatSettingsPanel(
                            appearance = youtubeChatAppearance.value,
                            onChange = { updated ->
                                youtubeChatAppearance.value = updated
                                com.nuvio.tv.youtube.saveChatAppearance(updated, context)
                            },
                            onClose = { youtubeChatSettingsOn.value = false }
                        )
                    }
                    if (youtubeContentId != null && youtubeViewModel != null) {
                        YouTubeUpNext(
                            videoId = youtubeContentId.removePrefix(com.nuvio.tv.youtube.YouTubeProgress.ID_PREFIX),
                            viewModel = youtubeViewModel,
                            timeline = playerTimeline,
                            isLiveStream = youtubeLive
                        )
                    }
                    if (
                        playerUiState.showYouTubePanel && youtubeContentId != null &&
                        youtubeViewModel != null
                    ) {
                        YouTubePlayerPanel(
                            videoId = youtubeContentId.removePrefix(com.nuvio.tv.youtube.YouTubeProgress.ID_PREFIX),
                            viewModel = youtubePlayerViewModel,
                            onClose = {
                                playerViewModel.onEvent(
                                    com.nuvio.tv.ui.screens.player.PlayerEvent.OnDismissYouTubePanel
                                )
                            },
                            onOpenChannel = { channelId, name ->
                                youtubeViewModel.openChannel(channelId, name)
                                playerViewModel.onEvent(
                                    com.nuvio.tv.ui.screens.player.PlayerEvent.OnDismissYouTubePanel
                                )
                                navController.popBackStack(Screen.YouTube.route, inclusive = false)
                            },
                            onPlayVideo = { item ->
                                youtubePlayerScope.launch {
                                    youtubeViewModel.previewVideo(item)?.let { video ->
                                    navController.navigate(
                                        youtubePlayerRoute(
                                            video = video,
                                            returnToHomeOnBack = backStackEntry.arguments
                                                ?.getString("returnToHomeOnBack")
                                                ?.toBooleanStrictOrNull() == true,
                                        )
                                    ) {
                                            popUpTo(backStackEntry.destination.id) { inclusive = true }
                                        }
                                    }
                                }
                            },
                            onSeekTo = { position ->
                                playerViewModel.onEvent(
                                    com.nuvio.tv.ui.screens.player.PlayerEvent.OnSeekTo(position)
                                )
                            }
                        )
                    }
                },
                onBackPress = { currentVideoId, currentSeason, currentEpisode, autoPlayEnabled, playbackCompleted ->
                    requestHomeHeroFocusAfterPlayback()
                    val args = backStackEntry.arguments
                    val initialSeason = args?.getString("season")?.toIntOrNull()
                    val initialEpisode = args?.getString("episode")?.toIntOrNull()
                    val episodeChangedInPlace = (currentSeason != null || currentEpisode != null) &&
                        (currentSeason != initialSeason || currentEpisode != initialEpisode)
                    val returnToDetailOnBack = args?.getString("returnToDetailOnBack")
                        ?.toBooleanStrictOrNull() == true
                    val returnToHomeOnBack = args?.getString("returnToHomeOnBack")
                        ?.toBooleanStrictOrNull() == true
                    val contentType = args?.getString("contentType").orEmpty()
                    val contentId = args?.getString("contentId").orEmpty()
                    val focusSeason = currentSeason ?: initialSeason
                    val focusEpisode = currentEpisode ?: initialEpisode
                    fun returnToDetail() {
                        val detailEntry = navController.currentBackStack.value
                            .lastOrNull {
                                val itemId = it.arguments?.getString("itemId").orEmpty()
                                val itemType = it.arguments?.getString("itemType").orEmpty()
                                it.destination.route?.startsWith("detail/") == true &&
                                    itemId == contentId &&
                                    (itemType.isBlank() || contentType.isBlank() || itemType.equals(contentType, ignoreCase = true))
                            }
                        if (detailEntry != null) {
                            detailEntry.savedStateHandle["returnFocusSeason"] = focusSeason
                            detailEntry.savedStateHandle["returnFocusEpisode"] = focusEpisode
                            navController.popBackStack(Screen.Detail.route, inclusive = false)
                        } else {
                            navController.navigate(
                                Screen.Detail.createRoute(
                                    itemId = contentId,
                                    itemType = contentType,
                                    addonBaseUrl = null,
                                    returnFocusSeason = focusSeason,
                                    returnFocusEpisode = focusEpisode,
                                    returnToHomeOnBack = returnToHomeOnBack,
                                    heroBackdropUrl = args?.getString("backdrop")
                                )
                            ) {
                                popUpTo(Screen.Player.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                    fun returnToDetailOrIptvOrigin() {
                        if (!returnFromIptvVodPlayback(
                                contentId = contentId,
                                contentType = contentType,
                                returnToDetailOnBack = returnToDetailOnBack,
                                returnToHomeOnBack = returnToHomeOnBack,
                                focusSeason = focusSeason,
                                focusEpisode = focusEpisode,
                            )
                        ) {
                            returnToDetail()
                        }
                    }

                    when {
                        playbackCompleted && contentId.isNotBlank() -> {
                            returnToDetailOrIptvOrigin()
                        }
                        episodeChangedInPlace && autoPlayEnabled -> {
                            // autoplay moved to next episode — skip Stream, go to detail
                            if (returnToDetailOnBack && contentType.equals("series", ignoreCase = true) && contentId.isNotBlank()) {
                                returnToDetailOrIptvOrigin()
                            } else if (
                                navController.previousBackStackEntry?.destination?.route
                                    ?.startsWith("stream/") == true
                            ) {
                                // Binge group reuse keeps the first episode's manual Stream screen
                                // below the player; drop it like the autoplay path does
                                navController.popBackStack(Screen.Stream.route, inclusive = true)
                                navController.currentBackStackEntry
                                    ?.takeIf { it.destination.route?.startsWith("detail/") == true }
                                    ?.savedStateHandle
                                    ?.let { state ->
                                        state["returnFocusSeason"] = focusSeason
                                        state["returnFocusEpisode"] = focusEpisode
                                    }
                            } else {
                                navController.popBackStack()
                            }
                        }
                        playerBackOpensCurrentEpisodeStreams(episodeChangedInPlace, autoPlayEnabled) -> {
                            // manual stream switch to next episode — go to Stream of current episode
                            val videoId = currentVideoId ?: args?.getString("videoId").orEmpty()
                            if (videoId.isNotBlank() && contentType.isNotBlank()) {
                                navController.navigate(
                                    Screen.Stream.createRoute(
                                        videoId = videoId,
                                        contentType = contentType,
                                        title = args?.getString("title").orEmpty(),
                                        poster = args?.getString("poster"),
                                        backdrop = args?.getString("backdrop"),
                                        logo = args?.getString("logo"),
                                        season = focusSeason,
                                        episode = focusEpisode,
                                        year = args?.getString("year"),
                                        contentId = contentId.takeIf { it.isNotBlank() },
                                        contentName = args?.getString("contentName"),
                                        manualSelection = true,
                                        returnToDetailOnBack = returnToDetailOnBack,
                                        returnToHomeOnBack = returnToHomeOnBack,
                                        profileId = args?.getString("profileId")?.toIntOrNull()
                                    )
                                ) {
                                    popUpTo(Screen.Stream.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            } else {
                                navController.popBackStack()
                            }
                        }
                        else -> {
                            val returnedToStream = popBackToStream()
                            if (!returnedToStream) {
                                if (returnToDetailOnBack && contentType.equals("series", ignoreCase = true) && contentId.isNotBlank()) {
                                    returnToDetailOrIptvOrigin()
                                } else {
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                },
                onPlaybackEnded = { nextVideoId, nextSeason, nextEpisode, exitReason ->
                    val args = backStackEntry.arguments
                    val returnToHomeOnBack = args?.getString("returnToHomeOnBack")
                        ?.toBooleanStrictOrNull() == true ||
                        navController.previousBackStackEntry?.destination?.route == Screen.Home.route
                    val endedYouTubeId = args?.getString("contentId")
                        ?.takeIf { com.nuvio.tv.youtube.YouTubeProgress.isYouTubeId(it) }
                        ?.removePrefix(com.nuvio.tv.youtube.YouTubeProgress.ID_PREFIX)
                    if (endedYouTubeId != null && exitReason == null && youtubeViewModel != null &&
                        com.nuvio.tv.youtube.YouTubeSettings.autoplayNext(context)
                    ) {
                        youtubeViewModel.playNext(
                            currentVideoId = endedYouTubeId,
                            onReady = { video ->
                                navController.navigate(
                                    youtubePlayerRoute(
                                        video = video,
                                        returnToHomeOnBack = returnToHomeOnBack,
                                    )
                                ) {
                                    popUpTo(backStackEntry.destination.id) { inclusive = true }
                                }
                            },
                            onNone = {
                                requestHomeHeroFocusAfterPlayback()
                                val returnRoute = if (returnToHomeOnBack) {
                                    Screen.Home.route
                                } else {
                                    Screen.YouTube.route
                                }
                                if (!navController.popBackStack(returnRoute, inclusive = false)) {
                                    navController.popBackStack()
                                }
                            }
                        )
                        return@PlayerScreen
                    }
                    if (endedYouTubeId != null) {
                        requestHomeHeroFocusAfterPlayback()
                        val returnRoute = if (returnToHomeOnBack) Screen.Home.route else Screen.YouTube.route
                        if (!navController.popBackStack(returnRoute, inclusive = false)) {
                            navController.popBackStack()
                        }
                        return@PlayerScreen
                    }
                    val contentType = args?.getString("contentType").orEmpty()
                    val contentId = args?.getString("contentId").orEmpty()
                    val returnToDetailOnBack = args?.getString("returnToDetailOnBack")
                        ?.toBooleanStrictOrNull() == true
                    if (nextVideoId != null && nextSeason != null && nextEpisode != null) {
                        val route = Screen.Stream.createRoute(
                            videoId = nextVideoId,
                            contentType = contentType,
                            title = args?.getString("title").orEmpty(),
                            poster = args?.getString("poster"),
                            backdrop = args?.getString("backdrop"),
                            logo = args?.getString("logo"),
                            season = nextSeason,
                            episode = nextEpisode,
                            episodeName = null,
                            genres = null,
                            year = args?.getString("year"),
                            contentId = contentId.takeIf { it.isNotBlank() },
                            contentName = args?.getString("contentName"),
                            runtime = null,
                            returnToDetailOnBack = returnToDetailOnBack,
                            returnToHomeOnBack = returnToHomeOnBack,
                            profileId = args?.getString("profileId")?.toIntOrNull()
                        )
                        navController.navigate(route) {
                            popUpTo(Screen.Player.route) { inclusive = true }
                        }
                    } else {
                        requestHomeHeroFocusAfterPlayback()
                        val focusSeason = args?.getString("season")?.toIntOrNull()
                        val focusEpisode = args?.getString("episode")?.toIntOrNull()
                        val returnedToIptvOrigin = returnFromIptvVodPlayback(
                            contentId = contentId,
                            contentType = contentType,
                            returnToDetailOnBack = returnToDetailOnBack,
                            returnToHomeOnBack = returnToHomeOnBack,
                            focusSeason = focusSeason,
                            focusEpisode = focusEpisode,
                            restoreDetailHero = exitReason == PlayerExitReason.StillWatchingPrompt,
                        )
                        if (returnedToIptvOrigin) {
                            Unit
                        } else if (exitReason == PlayerExitReason.StillWatchingPrompt) {
                            val detailEntry = navController.currentBackStack.value
                                .lastOrNull { it.destination.route?.startsWith("detail/") == true }
                            if (detailEntry != null) {
                                detailEntry.savedStateHandle["returnFocusSeason"] = null
                                detailEntry.savedStateHandle["returnFocusEpisode"] = null
                                val token = (detailEntry.savedStateHandle.get<Int>("heroRestoreToken") ?: 0) + 1
                                detailEntry.savedStateHandle["heroRestoreToken"] = token
                                navController.popBackStack(Screen.Detail.route, inclusive = false)
                            } else {
                                val contentId = args?.getString("contentId").orEmpty()
                                val contentType = args?.getString("contentType").orEmpty()
                                val returnToHomeOnBack = args?.getString("returnToHomeOnBack")
                                    ?.toBooleanStrictOrNull() == true
                                if (contentId.isNotBlank()) {
                                    navController.navigate(
                                        Screen.Detail.createRoute(
                                            itemId = contentId,
                                            itemType = contentType,
                                            addonBaseUrl = null,
                                            returnToHomeOnBack = returnToHomeOnBack,
                                            heroBackdropUrl = args?.getString("backdrop")
                                        )
                                    ) {
                                        popUpTo(Screen.Player.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                } else {
                                    val poppedToStream = navController.popBackStack(Screen.Stream.route, inclusive = true)
                                    if (!poppedToStream) {
                                        navController.popBackStack()
                                    }
                                }
                            }
                        } else {
                            if (contentId.isNotBlank()) {
                                val detailEntry = navController.currentBackStack.value
                                    .lastOrNull {
                                        val itemId = it.arguments?.getString("itemId").orEmpty()
                                        val itemType = it.arguments?.getString("itemType").orEmpty()
                                        it.destination.route?.startsWith("detail/") == true &&
                                            itemId == contentId &&
                                            (itemType.isBlank() || contentType.isBlank() || itemType.equals(contentType, ignoreCase = true))
                                    }
                                if (detailEntry != null) {
                                    detailEntry.savedStateHandle["returnFocusSeason"] = focusSeason
                                    detailEntry.savedStateHandle["returnFocusEpisode"] = focusEpisode
                                    navController.popBackStack(Screen.Detail.route, inclusive = false)
                                } else {
                                    navController.navigate(
                                        Screen.Detail.createRoute(
                                            itemId = contentId,
                                            itemType = contentType,
                                            addonBaseUrl = null,
                                            returnFocusSeason = focusSeason,
                                            returnFocusEpisode = focusEpisode,
                                            returnToHomeOnBack = returnToHomeOnBack,
                                            heroBackdropUrl = args?.getString("backdrop")
                                        )
                                    ) {
                                        popUpTo(Screen.Player.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            } else {
                                val poppedToStream = navController.popBackStack(Screen.Stream.route, inclusive = true)
                                if (!poppedToStream) {
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                },
                onPlayRecommendation = { recommendation, manualSelection ->
                    navigateFromPostPlay(
                        recommendation = recommendation,
                        playOnLoad = true,
                        manualSelection = manualSelection
                    )
                },
                onOpenRecommendationDetails = { recommendation ->
                    navigateFromPostPlay(
                        recommendation = recommendation,
                        playOnLoad = false
                    )
                },
                onPlaybackErrorBack = {
                    requestHomeHeroFocusAfterPlayback()
                    val returnedToStream = popBackToStream()
                    if (!returnedToStream) {
                        val args = backStackEntry.arguments
                        val videoId = args?.getString("videoId").orEmpty()
                        val contentType = args?.getString("contentType").orEmpty()
                        val title = args?.getString("title").orEmpty()

                        if (videoId.isBlank() || contentType.isBlank() || title.isBlank()) {
                            navController.popBackStack()
                        } else {
                            val route = Screen.Stream.createRoute(
                                videoId = videoId,
                                contentType = contentType,
                                title = title,
                                poster = args?.getString("poster"),
                                backdrop = args?.getString("backdrop"),
                                logo = args?.getString("logo"),
                                season = args?.getString("season")?.toIntOrNull(),
                                episode = args?.getString("episode")?.toIntOrNull(),
                                episodeName = args?.getString("episodeTitle"),
                                genres = null,
                                year = args?.getString("year"),
                                contentId = args?.getString("contentId"),
                                contentName = args?.getString("contentName"),
                                runtime = null,
                                manualSelection = true,
                                returnToDetailOnBack = args?.getString("returnToDetailOnBack")
                                    ?.toBooleanStrictOrNull() == true,
                                returnToHomeOnBack = args?.getString("returnToHomeOnBack")
                                    ?.toBooleanStrictOrNull() == true,
                                profileId = args?.getString("profileId")?.toIntOrNull()
                            )

                            navController.navigate(route) {
                                popUpTo(Screen.Player.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.Search.route) { backStackEntry ->
            val searchViewModel: com.nuvio.tv.ui.screens.search.SearchViewModel =
                androidx.hilt.navigation.compose.hiltViewModel(backStackEntry)
            SearchScreen(
                viewModel = searchViewModel,
                onNavigateToDetail = { itemId, itemType, addonBaseUrl ->
                    val heroBackdrop = HeroBackdropState.consumeAndClear()
                    navController.navigate(
                        Screen.Detail.createRoute(
                            itemId = itemId,
                            itemType = itemType,
                            addonBaseUrl = addonBaseUrl,
                            heroBackdropUrl = heroBackdrop
                        )
                    )
                },
                onNavigateToSeeAll = { catalogId, addonId, type ->
                    navController.navigate(
                        Screen.CatalogSeeAll.createRoute(catalogId, addonId, type, fromSearch = true)
                    )
                },
                onOpenDiscover = { navController.navigate(Screen.Discover.route) },
                onPlayLiveTv = { route -> navController.navigate(route) },
                onPlayIptvVod = { source: LiveTvSource, item: LiveTvVodItem, playback: LiveTvVodPlayback ->
                    val isSeries = item.type == "episode" || item.type == "series"
                    navController.navigate(
                        Screen.Player.createRoute(
                            streamUrl = playback.url,
                            title = item.title,
                            streamName = source.label,
                            year = item.year,
                            headers = playback.headers,
                            contentId = if (isSeries) item.seriesId.takeIf(String::isNotBlank) ?: item.id else item.id,
                            contentType = if (isSeries) "series" else "movie",
                            contentName = item.seriesTitle.ifBlank { item.title },
                            season = item.season.takeIf { isSeries && it > 0 },
                            episode = item.episode.takeIf { isSeries && it > 0 },
                        )
                    )
                }
            )
        }

        composable(Screen.Discover.route) {
            DiscoverScreen(
                showBuiltInHeader = !hideBuiltInHeaders,
                onNavigateToDetail = { itemId, itemType, addonBaseUrl ->
                    val heroBackdrop = HeroBackdropState.consumeAndClear()
                    navController.navigate(
                        Screen.Detail.createRoute(
                            itemId = itemId,
                            itemType = itemType,
                            addonBaseUrl = addonBaseUrl,
                            heroBackdropUrl = heroBackdrop
                        )
                    )
                }
            )
        }

        composable(Screen.Movies.route) {
            IptvVodScreen(
                type = "movie",
                onNavigateToDetail = { itemId, itemType, addonBaseUrl ->
                    navController.navigate(Screen.Detail.createRoute(itemId, itemType, addonBaseUrl))
                },
            ) { source, item, playback ->
                navController.navigate(
                    Screen.Player.createRoute(
                        streamUrl = playback.url,
                        title = item.title,
                        streamName = source.label,
                        year = item.year,
                        headers = playback.headers,
                        contentId = item.id,
                        contentType = "movie",
                        contentName = item.title,
                    )
                )
            }
        }

        composable(Screen.Series.route) {
            IptvVodScreen(
                type = "series",
                onNavigateToDetail = { itemId, itemType, addonBaseUrl ->
                    navController.navigate(Screen.Detail.createRoute(itemId, itemType, addonBaseUrl))
                },
            ) { source, item, playback ->
                navController.navigate(
                    Screen.Player.createRoute(
                        streamUrl = playback.url,
                        title = item.title,
                        streamName = source.label,
                        headers = playback.headers,
                        contentId = item.seriesId.takeIf { item.type == "episode" && it.isNotBlank() } ?: item.id,
                        contentType = "series",
                        contentName = item.seriesTitle.ifBlank { item.title },
                        season = item.season.takeIf { it > 0 },
                        episode = item.episode.takeIf { it > 0 },
                    )
                )
            }
        }

        composable(
            route = Screen.FreeboxFolder.route,
            arguments = listOf(
                navArgument("path") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType },
            ),
        ) { entry ->
            val path = entry.arguments?.getString("path").orEmpty()
            val type = entry.arguments?.getString("type").orEmpty()
            FreeboxFolderScreen(
                path = path,
                mediaType = type,
                onOpenFolder = { childPath, childType ->
                    navController.navigate(Screen.FreeboxFolder.createRoute(childPath, childType))
                },
                onPlayVideo = { item ->
                    navController.navigate(createFreeboxVideoRoute(item))
                },
                onPlayVideoFromBeginning = { item ->
                    navController.navigate(createFreeboxVideoRoute(item, startFromBeginning = true))
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(com.nuvio.tv.reshaped.livetv.LIVE_TV_ROUTE) { // Live TV
            com.nuvio.tv.ui.reshaped.livetv.LiveTvScreen(onPlay = { navController.navigate(it) }, showBuiltInHeader = !hideBuiltInHeaders)
        }

        composable(
            route = Screen.HomeIptvFavorite.route,
            arguments = listOf(navArgument("favoriteKey") { type = NavType.StringType }),
        ) { entry ->
            IptvHomeFavoriteScreen(
                favoriteKey = entry.arguments?.getString("favoriteKey").orEmpty(),
                onBack = { navController.popBackStack() },
                onOpenMovieDetails = { tmdbId, favoriteKey ->
                    navController.navigate(
                        Screen.Detail.createRoute(
                            itemId = "tmdb:$tmdbId",
                            itemType = "movie",
                            returnToHomeOnBack = true,
                            iptvFavoriteKey = favoriteKey,
                        )
                    )
                },
                onNavigateToDetail = { itemId, itemType, addonBaseUrl ->
                    navController.navigate(Screen.Detail.createRoute(itemId, itemType, addonBaseUrl))
                },
                onPlayVod = { source, item, playback ->
                    val isSeries = item.type == "episode" || item.type == "series"
                    navController.navigate(
                        Screen.Player.createRoute(
                            streamUrl = playback.url,
                            title = item.title,
                            streamName = source.label,
                            year = item.year,
                            headers = playback.headers,
                            contentId = if (isSeries) item.seriesId.takeIf(String::isNotBlank) ?: item.id else item.id,
                            contentType = if (isSeries) "series" else "movie",
                            contentName = item.seriesTitle.ifBlank { item.title },
                            season = item.season.takeIf { isSeries && it > 0 },
                            episode = item.episode.takeIf { isSeries && it > 0 },
                            returnToHomeOnBack = true,
                        )
                    )
                },
            )
        }

        composable(Screen.Library.route) {
            IptvFavoritesScreen(
                showBuiltInHeader = !hideBuiltInHeaders,
                onOpenLibrary = { navController.navigate(Screen.PersonalLibrary.route) },
                onPlayChannel = { navController.navigate(it) },
                onPlayVod = { source, item, playback ->
                    val isSeries = item.type == "episode" || item.type == "series"
                    navController.navigate(
                        Screen.Player.createRoute(
                            streamUrl = playback.url,
                            title = item.title,
                            streamName = source.label,
                            year = item.year,
                            headers = playback.headers,
                            contentId = if (isSeries) item.seriesId.takeIf(String::isNotBlank) ?: item.id else item.id,
                            contentType = if (isSeries) "series" else "movie",
                            contentName = item.seriesTitle.ifBlank { item.title },
                            season = item.season.takeIf { isSeries && it > 0 },
                            episode = item.episode.takeIf { isSeries && it > 0 },
                        )
                    )
                }
            )
        }

        composable(Screen.PersonalLibrary.route) {
            LibraryScreen(
                showBuiltInHeader = !hideBuiltInHeaders,
                onNavigateToDetail = { itemId, itemType, addonBaseUrl ->
                    navController.navigate(Screen.Detail.createRoute(itemId, itemType, addonBaseUrl))
                },
                onNavigateToCatalogSeeAll = { catalogId, addonId, type ->
                    navController.navigate(Screen.CatalogSeeAll.createRoute(catalogId, addonId, type))
                }
            )
        }

        composable(Screen.ServerHub.route) {
            val serverContentFocusRequester = remember { FocusRequester() }
            CompositionLocalProvider(LocalContentFocusRequester provides serverContentFocusRequester) {
                Box(Modifier.fillMaxSize()) {
                    ServerHubScreen(
                        onOpenLocal = {
                            navController.navigate(Screen.ServerBrowser.createRoute(ServerBrowserViewModel.LOCAL_SOURCE))
                        },
                        onOpenFreebox = {
                            navController.navigate(Screen.ServerBrowser.createRoute(ServerBrowserViewModel.FREEBOX_SOURCE))
                        },
                        onOpenServer = { connectionId ->
                            navController.navigate(Screen.ServerBrowser.createRoute(connectionId))
                        },
                        onManageServers = { navController.navigate(Screen.Settings.route) },
                        onBackPress = { navController.popBackStack() },
                        contentFocusRequester = serverContentFocusRequester,
                    )
                    ModulePageChrome(
                        title = stringResource(R.string.server_hub_title),
                        navController = navController,
                    )
                }
            }
        }

        composable(
            route = Screen.ServerBrowser.route,
            arguments = listOf(
                navArgument("sourceId") { type = NavType.StringType },
                navArgument("path") { type = NavType.StringType; defaultValue = "" },
                navArgument("openPath") { type = NavType.StringType; defaultValue = "" },
            ),
        ) { entry ->
            val sourceId = entry.arguments?.getString("sourceId").orEmpty()
            val path = entry.arguments?.getString("path").orEmpty()
            val openPath = entry.arguments?.getString("openPath").orEmpty()
            val browserViewModel = androidx.hilt.navigation.compose.hiltViewModel<ServerBrowserViewModel>(entry)
            val browserState by browserViewModel.state.collectAsStateWithLifecycle()
            val browserContentFocusRequester = remember { FocusRequester() }
            val fileOpenScope = rememberCoroutineScope()
            var pendingApkName by rememberSaveable { mutableStateOf<String?>(null) }
            fun openLocalVideo(file: ServerBrowserEntry, startFromBeginning: Boolean) {
                val isShizukuFile = file.path.startsWith(SHIZUKU_STORAGE_PATH_PREFIX)
                fun navigateToPlayer(streamUrl: String) {
                    val title = file.name.substringBeforeLast('.', file.name)
                    navController.navigate(
                        Screen.Player.createRoute(
                            streamUrl = streamUrl,
                            title = title,
                            streamName = "Local / USB",
                            contentId = "local:${if (isShizukuFile) file.path else streamUrl}",
                            contentType = "movie",
                            contentName = title,
                            filename = file.name,
                            videoSize = file.sizeBytes.takeIf { it > 0L },
                            startFromBeginning = startFromBeginning,
                        )
                    )
                }

                if (!isShizukuFile) {
                    navigateToPlayer(file.uri.orEmpty())
                    return
                }
                fileOpenScope.launch {
                    try {
                        val uri = browserViewModel.prepareFileUri(ServerBrowserViewModel.LOCAL_SOURCE, file)
                        navigateToPlayer(uri.toString())
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (error: Exception) {
                        Toast.makeText(
                            context,
                            error.message ?: "Impossible d’ouvrir cette vidéo.",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
            }
            val apkInstallLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
            ) { result ->
                val apkName = pendingApkName ?: "l’APK"
                pendingApkName = null
                showApkInstallResult(context, apkName, result.resultCode)
            }
            CompositionLocalProvider(LocalContentFocusRequester provides browserContentFocusRequester) {
                Box(Modifier.fillMaxSize()) {
                    ServerBrowserScreen(
                        sourceId = sourceId,
                        path = path,
                        openPath = openPath,
                        viewModel = browserViewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateHome = {
                            if (!navController.popBackStack(Screen.ServerHub.route, inclusive = false)) {
                                navController.navigate(Screen.ServerHub.route) {
                                    launchSingleTop = true
                                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                }
                            }
                        },
                        contentFocusRequester = browserContentFocusRequester,
                        onChooseSource = { nextSource ->
                            navController.navigate(Screen.ServerBrowser.createRoute(nextSource))
                        },
                        onOpenFolder = { childPath ->
                            navController.navigate(Screen.ServerBrowser.createRoute(sourceId, childPath))
                        },
                        onOpenFile = { file ->
                            when {
                                sourceId == ServerBrowserViewModel.LOCAL_SOURCE && file.isPlayable -> {
                                    openLocalVideo(file, startFromBeginning = false)
                                }
                                sourceId == ServerBrowserViewModel.FREEBOX_SOURCE && file.isPlayable -> {
                                    val title = file.name.substringBeforeLast('.', file.name)
                                    navController.navigate(
                                        Screen.Player.createRoute(
                                            streamUrl = "freebox://media?path=${Uri.encode(file.path)}",
                                            title = title,
                                            streamName = "Freebox",
                                            contentId = freeboxContentIdFor(file.path, file.sizeBytes, file.lastModifiedMs, file.fileId),
                                            contentType = "movie",
                                            contentName = title,
                                            returnToHomeOnBack = false,
                                            filename = file.name,
                                        )
                                    )
                                }
                                sourceId != ServerBrowserViewModel.FREEBOX_SOURCE &&
                                    sourceId != ServerBrowserViewModel.LOCAL_SOURCE && file.isPlayable -> {
                                    val itemId = ServerItemRef(sourceId, "file:${file.path}").encode()
                                    navController.navigate(
                                        Screen.Detail.createRoute(
                                            itemId = itemId,
                                            itemType = "movie",
                                            addonBaseUrl = ServerCatalog.baseUrl(sourceId),
                                            playOnLoad = true,
                                        )
                                    )
                                }
                                else -> fileOpenScope.launch {
                                    if (sourceId != ServerBrowserViewModel.LOCAL_SOURCE &&
                                        file.fileType != ServerBrowserFileType.APK
                                    ) {
                                        Toast.makeText(context, "Préparation du fichier…", Toast.LENGTH_SHORT).show()
                                    }
                                    try {
                                        val uri = browserViewModel.prepareFileUri(sourceId, file)
                                        when (file.fileType) {
                                            ServerBrowserFileType.IMAGE,
                                            ServerBrowserFileType.PDF,
                                            ServerBrowserFileType.TEXT -> navController.navigate(
                                                Screen.ServerFileViewer.createRoute(
                                                    uri = uri.toString(),
                                                    name = file.name,
                                                    mimeType = when (file.fileType) {
                                                        ServerBrowserFileType.IMAGE -> file.mimeType
                                                            ?.takeIf { it.startsWith("image/", ignoreCase = true) }
                                                            ?: "image/*"
                                                        ServerBrowserFileType.PDF -> "application/pdf"
                                                        ServerBrowserFileType.TEXT -> file.mimeType
                                                            ?.takeIf { it.startsWith("text/", ignoreCase = true) }
                                                            ?: "text/plain"
                                                        else -> file.mimeType ?: "application/octet-stream"
                                                    },
                                                    sourceId = sourceId.takeIf { file.fileType == ServerBrowserFileType.IMAGE }.orEmpty(),
                                                    imagePath = file.path.takeIf { file.fileType == ServerBrowserFileType.IMAGE }.orEmpty(),
                                                )
                                            )
                                            ServerBrowserFileType.APK -> {
                                                pendingApkName = file.name
                                                if (!installServerApk(context, file.name, uri) { intent ->
                                                        apkInstallLauncher.launch(intent)
                                                    }
                                                ) {
                                                    pendingApkName = null
                                                }
                                            }
                                            else -> openServerFileExternally(
                                                context = context,
                                                name = file.name,
                                                mimeType = file.mimeType ?: "*/*",
                                                uri = uri,
                                            )
                                        }
                                    } catch (cancelled: CancellationException) {
                                        throw cancelled
                                    } catch (error: Exception) {
                                        Toast.makeText(
                                            context,
                                            error.message ?: "Impossible de préparer ce fichier.",
                                            Toast.LENGTH_LONG,
                                        ).show()
                                    } finally {
                                        if (file.fileType == ServerBrowserFileType.APK) {
                                            browserViewModel.clearApkDownloadProgress()
                                        }
                                    }
                                }
                            }
                        },
                        onPlayFromBeginning = { file ->
                            when {
                                sourceId == ServerBrowserViewModel.LOCAL_SOURCE && file.isPlayable -> {
                                    openLocalVideo(file, startFromBeginning = true)
                                }
                                sourceId == ServerBrowserViewModel.FREEBOX_SOURCE && file.isPlayable -> {
                                    val title = file.name.substringBeforeLast('.', file.name)
                                    navController.navigate(
                                        Screen.Player.createRoute(
                                            streamUrl = "freebox://media?path=${Uri.encode(file.path)}",
                                            title = title,
                                            streamName = "Freebox",
                                            contentId = freeboxContentIdFor(file.path, file.sizeBytes, file.lastModifiedMs, file.fileId),
                                            contentType = "movie",
                                            contentName = title,
                                            returnToHomeOnBack = false,
                                            filename = file.name,
                                            startFromBeginning = true,
                                        )
                                    )
                                }
                                sourceId != ServerBrowserViewModel.LOCAL_SOURCE && file.isPlayable -> {
                                    fileOpenScope.launch {
                                        try {
                                            browserViewModel.clearResumeProgress(sourceId, file)
                                            val itemId = ServerItemRef(sourceId, "file:${file.path}").encode()
                                            navController.navigate(
                                                Screen.Detail.createRoute(
                                                    itemId = itemId,
                                                    itemType = "movie",
                                                    addonBaseUrl = ServerCatalog.baseUrl(sourceId),
                                                    playOnLoad = true,
                                                )
                                            )
                                        } catch (error: Exception) {
                                            Toast.makeText(context, error.message ?: "Impossible de reprendre cette vidéo au début.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }
                        },
                    )
                    ModulePageChrome(title = browserState.sourceName, navController = navController)
                }
            }
        }

        composable(
            route = Screen.ServerFileViewer.route,
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
                navArgument("mimeType") { type = NavType.StringType },
                navArgument("sourceId") { type = NavType.StringType; defaultValue = "" },
                navArgument("imagePath") { type = NavType.StringType; defaultValue = "" },
            ),
        ) { entry ->
            val sourceId = entry.arguments?.getString("sourceId").orEmpty()
            val imagePath = entry.arguments?.getString("imagePath").orEmpty()
            val playbackSettingsViewModel = androidx.hilt.navigation.compose.hiltViewModel<com.nuvio.tv.ui.screens.settings.PlaybackSettingsViewModel>(entry)
            val playbackSettings = playbackSettingsViewModel.playerSettings.collectAsStateWithLifecycle(
                initialValue = com.nuvio.tv.data.local.PlayerSettings(),
            ).value
            val browserViewModel = if (sourceId.isBlank()) {
                null
            } else {
                navController.previousBackStackEntry?.let { browserEntry ->
                    androidx.hilt.navigation.compose.hiltViewModel<ServerBrowserViewModel>(browserEntry)
                }
            }
            val browserEntries = browserViewModel?.state?.collectAsStateWithLifecycle()?.value?.entries.orEmpty()
            val imageEntries = remember(browserEntries) {
                browserEntries.filter { it.fileType == ServerBrowserFileType.IMAGE }
            }
            ServerFileViewerScreen(
                uri = entry.arguments?.getString("uri").orEmpty(),
                fileName = entry.arguments?.getString("name").orEmpty(),
                mimeType = entry.arguments?.getString("mimeType").orEmpty(),
                imageEntries = imageEntries,
                currentImagePath = imagePath,
                slideshowEnabled = playbackSettings.photoSlideshowEnabled,
                slideshowIntervalSeconds = playbackSettings.photoSlideshowIntervalSeconds,
                onPrepareImage = { image ->
                    browserViewModel?.prepareFileUri(sourceId, image)?.toString()
                        ?: error("La navigation entre les photos n’est pas disponible.")
                },
                onPrepareThumbnail = { image ->
                    browserViewModel?.prepareThumbnailUri(sourceId, image)
                        ?: image.uri?.let(Uri::parse)
                },
                showCoverflow = sourceId == ServerBrowserViewModel.FREEBOX_SOURCE,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Calendar.route) {
            val calendarContentFocusRequester = remember { FocusRequester() }
            CompositionLocalProvider(LocalContentFocusRequester provides calendarContentFocusRequester) {
                Box(Modifier.fillMaxSize()) {
                    com.nuvio.tv.calendar.CalendarScreen(
                        onOpenDetail = { id, type, addonBaseUrl, season, episode ->
                            navController.navigate(Screen.Detail.createRoute(id, type, addonBaseUrl, returnFocusSeason = season, returnFocusEpisode = episode))
                        },
                        onFindSources = { e ->
                            val videoId = e.videoId ?: return@CalendarScreen
                            navController.navigate(
                                Screen.Stream.createRoute(
                                    videoId = videoId,
                                    contentType = "series",
                                    title = e.title,
                                    poster = e.poster,
                                    backdrop = e.background,
                                    contentId = e.contentId,
                                    contentName = e.title,
                                    season = e.season,
                                    episode = e.episode,
                                    episodeName = e.episodeTitle
                                )
                            )
                        },
                        headerDownFocusRequester = calendarContentFocusRequester,
                    )
                    ModuleTopBarHeader(navController, Modifier.align(Alignment.TopEnd))
                }
            }
        }

        // This fork's original reshaped Live TV remains available on its own route. Sports uses
        // the Live TV engine bundled with the imported hub for channel matching and playback.
        composable(Screen.LiveTv.route) {
            com.nuvio.tv.livetv.ui.LiveTvGuideScreen(
                onOpenFullscreen = {
                    navController.navigate(Screen.LiveTvPlayer.route) { launchSingleTop = true }
                },
                onOpenSettings = { navController.navigate(Screen.LiveTvSettings.route) },
                onOpenRecordings = {
                    navController.navigate(Screen.Recordings.route) { launchSingleTop = true }
                },
                onFindInNuvio = {
                    navController.navigate(Screen.Search.route) { launchSingleTop = true }
                },
                onReturnToSports = {
                    navController.navigate(Screen.Sports.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onReturnTo = { route ->
                    if (!navController.popBackStack(route, inclusive = false)) {
                        navController.navigate(route) { launchSingleTop = true }
                    }
                }
            )
        }

        composable(Screen.Recordings.route) {
            com.nuvio.tv.livetv.record.RecordingsScreen(
                onPlay = { _, _, _, recordingId, fromStart ->
                    com.nuvio.tv.livetv.record.RecordingLaunch.open(
                        recordingId,
                        fromStart,
                        Screen.Recordings.route
                    )
                    navController.navigate(Screen.LiveTvPlayer.route) { launchSingleTop = true }
                }
            )
        }

        composable(Screen.YouTube.route) {
            com.nuvio.tv.youtube.YouTubeScreen(
                onOpenSettings = {
                    com.nuvio.tv.ui.screens.settings.SettingsLaunch.pendingCategory =
                        com.nuvio.tv.ui.screens.settings.SettingsCategory.YOUTUBE
                    navController.navigate(Screen.Settings.route) { launchSingleTop = true }
                },
                onPlay = { video -> navController.navigate(youtubePlayerRoute(video)) }
            )
        }

        composable(Screen.Sports.route) { sportsEntry ->
            val currentEntry by navController.currentBackStackEntryAsState()
            val onScreen = currentEntry?.id == sportsEntry.id
            var sportsShown by androidx.compose.runtime.remember {
                androidx.compose.runtime.mutableStateOf(true)
            }
            androidx.compose.runtime.LaunchedEffect(onScreen) {
                if (onScreen) {
                    sportsShown = true
                } else {
                    kotlinx.coroutines.delay(1_500)
                    sportsShown = false
                }
            }
            if (sportsShown) {
                com.nuvio.tv.livetv.sports.SportsScreen(
                    onOpenLive = {
                        com.nuvio.tv.livetv.sports.openLiveTvFullscreen(navController)
                    },
                    onReturnTo = { route ->
                        runCatching {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }

        composable(Screen.LiveTvPlayer.route) {
            androidx.compose.runtime.LaunchedEffect(Unit) {
                com.nuvio.tv.livetv.ui.LiveTvFullscreen.request()
                if (!navController.popBackStack(Screen.LiveTv.route, inclusive = false)) {
                    navController.navigate(Screen.LiveTv.route) {
                        popUpTo(Screen.LiveTvPlayer.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }

        composable(Screen.LiveTvSettings.route) {
            com.nuvio.tv.ui.screens.settings.LiveTvSettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Apps.route) {
            val appsContentFocusRequester = remember { FocusRequester() }
            var showAppsSettingsDrawer by rememberSaveable { mutableStateOf(false) }
            val calendarPreferences = remember(context.applicationContext) {
                dagger.hilt.android.EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    com.nuvio.tv.calendar.CalendarEntryPoint::class.java,
                ).calendarPreferences()
            }
            val calendarSettings by calendarPreferences.settings.collectAsStateWithLifecycle(
                initialValue = com.nuvio.tv.calendar.CalendarSettings(),
            )
            val returnToHome: () -> Unit = {
                val poppedToHome = navController.popBackStack(Screen.Home.route, inclusive = false)
                if (!poppedToHome) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Apps.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            BackHandler(onBack = returnToHome)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                CompositionLocalProvider(
                    LocalContentFocusRequester provides appsContentFocusRequester
                ) {
                    AppsScreen(
                        onManageHomeApps = { navController.navigate(Screen.InstalledAppsOrder.route) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 72.dp, top = 80.dp, end = 24.dp, bottom = 24.dp),
                        embedded = true,
                        contentFocusRequester = appsContentFocusRequester
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 108.dp, top = 28.dp)
                            .height(36.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = stringResource(R.string.nav_apps),
                            style = MaterialTheme.typography.headlineMedium,
                            color = NuvioTheme.colors.TextPrimary
                        )
                    }
                    HomeTopBarHeader(
                        showCalendarShortcut = calendarSettings.calendarInHeader && !calendarSettings.calendarOff,
                        onCalendarClick = {
                            navController.navigate(Screen.Calendar.route) { launchSingleTop = true }
                        },
                        onServersClick = { navController.navigate(Screen.ServerHub.route) },
                        onAppsClick = returnToHome,
                        onSettingsClick = { showAppsSettingsDrawer = true },
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
                if (showAppsSettingsDrawer) {
                    HomeSettingsDrawer(
                        onDismiss = { showAppsSettingsDrawer = false },
                        onOpenNuvioSettings = {
                            showAppsSettingsDrawer = false
                            settingsOpenedFromDrawer = true
                            navController.navigate(Screen.Settings.route)
                        }
                    )
                }
            }
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                showBuiltInHeader = !hideBuiltInHeaders,
                onBackPress = if (settingsOpenedFromDrawer) {
                    {
                        settingsOpenedFromDrawer = false
                        navController.popBackStack()
                    }
                } else null,
                onNavigateToAddons = { navController.navigate(Screen.AddonManager.route) },
                onNavigateToPlugins = { navController.navigate(Screen.Plugins.route) },
                onNavigateToAuthQrSignIn = { navController.navigate(Screen.AuthQrSignIn.route) },
                onNavigateToManageProfiles = { navController.navigate(Screen.ManageProfiles.route) },
                onNavigateToLicensesAttributions = {
                    navController.navigate(Screen.LicensesAttributions.route)
                },
                onNavigateToHomeRowOrder = { navController.navigate(Screen.CatalogOrder.route) },
                onNavigateToSportsTeams = {
                    com.nuvio.tv.livetv.sports.SportsLaunch.openPicker()
                    navController.navigate(Screen.Sports.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Screen.ManageProfiles.route) {
            ProfileSelectionScreen(
                onProfileSelected = {},
                screenMode = ProfileSelectionMode.Management,
                onBackPress = { navController.popBackStack() }
            )
        }

        composable(Screen.Tracking.route) {
            TrackingSettingsScreen(
                onBackPress = { navController.popBackStack() }
            )
        }

        composable(Screen.TmdbSettings.route) {
            TmdbSettingsScreen(
                onBackPress = { navController.popBackStack() }
            )
        }

        composable(Screen.ThemeSettings.route) {
            ThemeSettingsScreen(
                onBackPress = { navController.popBackStack() }
            )
        }

        composable(Screen.PlaybackSettings.route) {
            PlaybackSettingsScreen(
                onBackPress = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onBackPress = { navController.popBackStack() },
                onNavigateToLicensesAttributions = {
                    navController.navigate(Screen.LicensesAttributions.route)
                }
            )
        }

        if (AppFeaturePolicy.supportNuvioEnabled) {
            composable(Screen.SupportersContributors.route) {
                SupportersContributorsScreen(
                    onBackPress = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.LicensesAttributions.route) {
            LicensesAttributionsScreen(
                onBackPress = { navController.popBackStack() }
            )
        }

        composable(Screen.AddonManager.route) {
            AddonManagerScreen(
                showBuiltInHeader = !hideBuiltInHeaders,
                onBackPress = { navController.popBackStack() },
                onNavigateToCatalogOrder = { navController.navigate(Screen.CatalogOrder.route) },
                onNavigateToCollections = { navController.navigate(Screen.Collections.route) }
            )
        }

        composable(Screen.CatalogOrder.route) {
            CatalogOrderScreen(
                onBackPress = { navController.popBackStack() },
                onManageInstalledApps = { navController.navigate(Screen.InstalledAppsOrder.route) }
            )
        }

        composable(Screen.InstalledAppsOrder.route) {
            com.nuvio.tv.ui.screens.apps.InstalledAppsOrderScreen(
                onBackPress = { navController.popBackStack() }
            )
        }

        composable(Screen.Collections.route) {
            com.nuvio.tv.ui.screens.collection.CollectionManagementScreen(
                onNavigateToEditor = { collectionId ->
                    navController.navigate(Screen.CollectionEditor.createRoute(collectionId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CollectionEditor.route,
            arguments = listOf(
                navArgument("collectionId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            com.nuvio.tv.ui.screens.collection.CollectionEditorScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.FolderDetail.route,
            arguments = listOf(
                navArgument("collectionId") { type = NavType.StringType },
                navArgument("folderId") { type = NavType.StringType }
            )
        ) {
            com.nuvio.tv.ui.screens.collection.FolderDetailScreen(
                onNavigateToDetail = { itemId, itemType, addonBaseUrl ->
                    val heroBackdrop = HeroBackdropState.consumeAndClear()
                    navController.navigate(Screen.Detail.createRoute(itemId, itemType, addonBaseUrl, heroBackdropUrl = heroBackdrop))
                },
                onBack = { navController.popBackStack() }
            )
        }

        if (AppFeaturePolicy.pluginsEnabled) {
            composable(Screen.Plugins.route) {
                PluginScreen(
                    onBackPress = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Account.route) {
            AuthQrSignInScreen(
                onBackPress = { navController.popBackStack() }
            )
        }

        composable(Screen.AuthSignIn.route) {
            AuthQrSignInScreen(
                onBackPress = { navController.popBackStack() }
            )
        }

        composable(Screen.AuthQrSignIn.route) {
            AuthQrSignInScreen(
                onBackPress = { navController.popBackStack() }
            )
        }

        composable(Screen.LayoutSettings.route) {
            LayoutSettingsScreen(
                onBackPress = { navController.popBackStack() },
                onManageHomeRows = { navController.navigate(Screen.CatalogOrder.route) }
            )
        }

        composable(
            route = Screen.CatalogSeeAll.route,
            arguments = listOf(
                navArgument("catalogId") { type = NavType.StringType },
                navArgument("addonId") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType },
                navArgument("fromSearch") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val catalogId = backStackEntry.arguments?.getString("catalogId") ?: ""
            val addonId = backStackEntry.arguments?.getString("addonId") ?: ""
            val type = backStackEntry.arguments?.getString("type") ?: ""
            val fromSearch = backStackEntry.arguments?.getBoolean("fromSearch") ?: false
            if (ServerCatalog.isServerAddonId(addonId) && !fromSearch) {
                ServerCatalogDestination(navController, backStackEntry)
                return@composable
            }

            // When coming from search, get the SearchViewModel from the Search back stack entry
            // so we share the same data (existing results + pagination)
            val searchBackStackEntry = androidx.compose.runtime.remember(fromSearch) {
                if (fromSearch) {
                    try { navController.getBackStackEntry(Screen.Search.route) } catch (_: Exception) { null }
                } else null
            }
            val searchViewModel: com.nuvio.tv.ui.screens.search.SearchViewModel? =
                if (searchBackStackEntry != null) {
                    androidx.hilt.navigation.compose.hiltViewModel<com.nuvio.tv.ui.screens.search.SearchViewModel>(searchBackStackEntry)
                } else null
            val homeBackStackEntry = androidx.compose.runtime.remember {
                try { navController.getBackStackEntry(Screen.Home.route) } catch (_: Exception) { null }
            }
            val homeViewModel: com.nuvio.tv.ui.screens.home.HomeViewModel =
                if (homeBackStackEntry != null) {
                    androidx.hilt.navigation.compose.hiltViewModel(homeBackStackEntry)
                } else {
                    androidx.hilt.navigation.compose.hiltViewModel(backStackEntry)
                }

            CatalogSeeAllScreen(
                catalogId = catalogId,
                addonId = addonId,
                type = type,
                searchViewModel = searchViewModel,
                viewModel = homeViewModel,
                onNavigateToDetail = { itemId, itemType, addonBaseUrl ->
                    navController.navigate(Screen.Detail.createRoute(itemId, itemType, addonBaseUrl))
                },
                onBackPress = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CastDetail.route,
            arguments = listOf(
                navArgument("personId") { type = NavType.StringType },
                navArgument("personName") { type = NavType.StringType },
                navArgument("preferCrew") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            CastDetailScreen(
                onBackPress = { navController.popBackStack() },
                onNavigateToDetail = { itemId, itemType, addonBaseUrl ->
                    navController.navigate(Screen.Detail.createRoute(itemId, itemType, addonBaseUrl))
                }
            )
        }

        composable(
            route = Screen.TmdbEntityBrowse.route,
            arguments = listOf(
                navArgument("entityKind") { type = NavType.StringType },
                navArgument("entityId") { type = NavType.IntType },
                navArgument("entityName") { type = NavType.StringType },
                navArgument("sourceType") {
                    type = NavType.StringType
                    defaultValue = "tv"
                }
            )
        ) {
            TmdbEntityBrowseScreen(
                onBackPress = { navController.popBackStack() },
                onNavigateToDetail = { itemId, itemType, addonBaseUrl ->
                    navController.navigate(Screen.Detail.createRoute(itemId, itemType, addonBaseUrl))
                }
            )
        }
    }
}
