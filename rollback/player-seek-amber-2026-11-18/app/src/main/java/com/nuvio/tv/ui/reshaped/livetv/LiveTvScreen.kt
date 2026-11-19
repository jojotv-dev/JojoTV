@file:OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.nuvio.tv.ui.reshaped.livetv

import com.nuvio.tv.reshaped.livetv.lists.ListChannels
import com.nuvio.tv.reshaped.livetv.lists.isListChannelUrl
import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.tv.material3.Border
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.foundation.focusGroup
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import com.nuvio.tv.ui.util.rememberLongPressKeyTracker
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.currentStateAsState
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.reshaped.livetv.LiveTvCatchupLinks
import com.nuvio.tv.reshaped.livetv.LIVE_TV_UNGROUPED
import com.nuvio.tv.reshaped.livetv.LiveTvChannel
import com.nuvio.tv.reshaped.livetv.LiveTvClock
import com.nuvio.tv.reshaped.livetv.LiveTvCustomList
import com.nuvio.tv.reshaped.livetv.LiveTvProgramme
import com.nuvio.tv.reshaped.livetv.LiveTvRecentChannel
import com.nuvio.tv.reshaped.livetv.LiveTvRepository
import com.nuvio.tv.reshaped.livetv.rememberLiveTvPreviewsEnabled
import com.nuvio.tv.ui.theme.NuvioTheme
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** The filter shown in the category column. */
internal sealed interface LiveTvFilter {
    data object All : LiveTvFilter
    data object Favorites : LiveTvFilter
    data object History : LiveTvFilter
    /** One of the viewer's own playlists. */
    data class Custom(val id: String) : LiveTvFilter
    data class Source(val id: String) : LiveTvFilter
    data class Group(val name: String) : LiveTvFilter
    /** One category of one source, picked under that source's heading. */
    data class SourceGroup(val id: String, val name: String) : LiveTvFilter
}

internal const val FILTER_ALL = "\u0000all"
internal const val FILTER_FAVORITES = "\u0000favorites"
internal const val FILTER_HISTORY = "\u0000history"
internal const val FILTER_SOURCE_PREFIX = "\u0000source:"
internal const val FILTER_LIST_PREFIX = "\u0000list:"
internal const val FILTER_SOURCE_GROUP_PREFIX = "\u0000sourcegroup:"

internal fun sourceGroupKey(sourceId: String, group: String): String = "$FILTER_SOURCE_GROUP_PREFIX$sourceId\u0000$group"

/**
 * The channels a filter key shows: favorites, history and custom playlists keep their entries
 * even when a category or channel is hidden. Slow for big lists: call off the main thread.
 */
internal fun filterChannels(
    channels: List<LiveTvChannel>,
    favorites: Set<String>,
    history: List<LiveTvRecentChannel>,
    hidden: Set<String>,
    hiddenChannels: Set<Long>,
    key: String,
    customLists: List<LiveTvCustomList> = emptyList(),
): List<LiveTvChannel> {
    val filter = filterFor(key)
    if (filter is LiveTvFilter.History) {
        return history.map { LiveTvRepository.channelFor(it) }
    }
    if (filter is LiveTvFilter.Custom) {
        // In the viewer's order; a channel its source no longer lists is left out.
        val urls = customLists.firstOrNull { it.id == filter.id }?.urls ?: return emptyList()
        val wanted = urls.toHashSet()
        val byUrl = HashMap<String, LiveTvChannel>(urls.size * 2)
        channels.forEach { if (it.streamUrl in wanted) byUrl.putIfAbsent(it.streamUrl, it) }
        return urls.mapNotNull(byUrl::get)
    }
    return channels.filter { channel ->
        when (filter) {
            LiveTvFilter.All -> channel.group !in hidden && channel.hideKey !in hiddenChannels
            LiveTvFilter.Favorites -> channel.streamUrl in favorites
            LiveTvFilter.History -> false
            is LiveTvFilter.Custom -> false
            is LiveTvFilter.Source -> channel.sourceId == filter.id && channel.group !in hidden && channel.hideKey !in hiddenChannels
            is LiveTvFilter.Group -> channel.group == filter.name && channel.hideKey !in hiddenChannels
            is LiveTvFilter.SourceGroup ->
                channel.sourceId == filter.id && channel.group == filter.name && channel.hideKey !in hiddenChannels
        }
    }
}

internal fun filterFor(key: String): LiveTvFilter = when {
    key == FILTER_ALL -> LiveTvFilter.All
    key == FILTER_FAVORITES -> LiveTvFilter.Favorites
    key == FILTER_HISTORY -> LiveTvFilter.History
    key.startsWith(FILTER_LIST_PREFIX) -> LiveTvFilter.Custom(key.removePrefix(FILTER_LIST_PREFIX))
    key.startsWith(FILTER_SOURCE_PREFIX) -> LiveTvFilter.Source(key.removePrefix(FILTER_SOURCE_PREFIX))
    key.startsWith(FILTER_SOURCE_GROUP_PREFIX) -> key.removePrefix(FILTER_SOURCE_GROUP_PREFIX).let {
        LiveTvFilter.SourceGroup(it.substringBefore('\u0000'), it.substringAfter('\u0000'))
    }
    else -> LiveTvFilter.Group(key)
}


/**
 * Live TV, laid out as TV channel guides are: what is selected on top (picture, channel, programme,
 * time left, description) with the live preview on the right; below, the categories on the left
 * (favorites, history, each source's categories under its own heading) and the programme guide.
 * The guide draws only the rows and programmes in view, so thousands of channels stay light.
 */
@Composable
fun LiveTvScreen(
    onPlay: (String) -> Unit,
    showBuiltInHeader: Boolean = true,
    viewModel: LiveTvScreenModel = hiltViewModel(),
) {
    IptvAmberTheme {
        LiveTvScreenContent(onPlay, showBuiltInHeader, viewModel)
    }
}

@Composable
private fun LiveTvScreenContent(
    onPlay: (String) -> Unit,
    showBuiltInHeader: Boolean,
    viewModel: LiveTvScreenModel,
) {
    val context = LocalContext.current
    // Before the state is read, so a list let go while unused shows as loading, never as empty.
    remember(viewModel) { viewModel.ensureLoaded() }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, viewModel) {
        // Back from the background after a long while: the list may have been let go meanwhile.
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_START) viewModel.ensureLoaded() }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val uiState by LiveTvRepository.uiState.collectAsStateWithLifecycle()
    // A release that raced the screen coming back leaves an empty, idle state: load again.
    LaunchedEffect(uiState.isLoaded, uiState.isLoading, uiState.hasSource) {
        if (!uiState.isLoaded && !uiState.isLoading && !uiState.hasSource) viewModel.ensureLoaded()
    }
    LaunchedEffect(Unit) {
        com.nuvio.tv.reshaped.livetv.lists.ListChannels.myListsTvSyncManager()?.requestSyncInBackground(force = true)
    }
    val scope = rememberCoroutineScope()
    var filterKey by rememberSaveable { mutableStateOf(FILTER_FAVORITES) }
    var showSourceDialog by remember { mutableStateOf(false) }
    var launching by remember { mutableStateOf(false) }
    // The first OK confirms the selected live channel for its inline preview; a second OK opens it full-screen.
    var previewConfirmedChannel by remember { mutableStateOf<String?>(null) }
    val previewsEnabled = rememberLiveTvPreviewsEnabled()
    val preview = rememberLiveTvPreviewPlayer()
    var gridFocused by remember { mutableStateOf(false) }
    // Naming a new playlist (from "+ New playlist" in the categories).
    var naming by remember { mutableStateOf(false) }
    // Tracks which side of the persistent TV layout should receive focus after dialogs.
    var categoriesOpen by remember { mutableStateOf(true) }
    var initialProviderFocusApplied by remember { mutableStateOf(false) }
    // A playlist opened to rename, reorder or delete (hold OK on it in the categories).
    var editingList by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val started = LocalLifecycleOwner.current.lifecycle.currentStateAsState().value.isAtLeast(Lifecycle.State.STARTED)
    // Leave room for the built-in app header when this screen is shown on its own.
    val topPadding = if (showBuiltInHeader) NuvioTheme.spacing.xl else 68.dp
    val gridFocus = remember { FocusRequester() }
    val categoryFocus = remember { FocusRequester() }
    val categoryList = rememberLazyListState()

    // Enter Live TV on the categories column; returning from playback still restores the guide.
    LaunchedEffect(uiState.isLoaded, uiState.hasSource, uiState.sources, viewModel.restoreFocusOnReturn) {
        if (!categoriesOpen || !uiState.isLoaded || !uiState.hasSource || initialProviderFocusApplied) {
            return@LaunchedEffect
        }
        if (viewModel.restoreFocusOnReturn) {
            initialProviderFocusApplied = true
            return@LaunchedEffect
        }
        val firstSource = uiState.sources.firstOrNull() ?: return@LaunchedEffect
        val firstSourceKey = FILTER_SOURCE_PREFIX + firstSource.id
        filterKey = firstSourceKey
        initialProviderFocusApplied = true
        withFrameNanos { }
        val index = viewModel.categoryKeys.indexOf(firstSourceKey)
        if (index >= 0) categoryList.scrollToItem((index - 3).coerceAtLeast(0))
        repeat(8) {
            withFrameNanos { }
            if (runCatching { categoryFocus.requestFocus() }.getOrDefault(false)) return@LaunchedEffect
        }
    }

    // A category that was hidden, or a source that was removed, falls back to Favorites.
    // Categories show under their source's heading only with several sources: a category picked
    // the other way follows (or falls back to Favorites when that is not possible).
    LaunchedEffect(uiState.hiddenGroups, uiState.sources, uiState.customLists) {
        val multiSource = uiState.sources.size > 1
        filterKey = when (val current = filterFor(filterKey)) {
            LiveTvFilter.All -> FILTER_FAVORITES
            is LiveTvFilter.Group -> when {
                current.name in uiState.hiddenGroups -> FILTER_FAVORITES
                multiSource -> FILTER_FAVORITES
                else -> filterKey
            }
            is LiveTvFilter.Source -> if (uiState.sources.none { it.id == current.id }) FILTER_FAVORITES else filterKey
            is LiveTvFilter.Custom -> if (uiState.customLists.none { it.id == current.id }) FILTER_FAVORITES else filterKey
            is LiveTvFilter.SourceGroup -> when {
                current.name in uiState.hiddenGroups || uiState.sources.none { it.id == current.id } -> FILTER_FAVORITES
                !multiSource -> current.name
                else -> filterKey
            }
            else -> filterKey
        }
    }

    val filter = filterFor(filterKey)
    // Filtered off the main thread: lists can hold tens of thousands of channels.
    val filterInput = LiveTvFilterInput(
        channels = uiState.channels,
        favoriteUrls = uiState.favoriteUrls,
        channelHistory = uiState.channelHistory,
        hiddenGroups = uiState.hiddenGroups,
        hiddenChannels = uiState.hiddenChannelKeys,
        filterKey = filterKey,
        customLists = uiState.customLists,
    )
    val visibleChannels = viewModel.visibleChannels
    val filtering = !viewModel.isFilteredFor(filterInput)
    LaunchedEffect(uiState.channels, uiState.favoriteUrls, uiState.channelHistory, uiState.hiddenGroups, uiState.hiddenChannelKeys, filterKey, uiState.customLists) {
        if (viewModel.isFilteredFor(filterInput)) return@LaunchedEffect
        val filtered = withContext(Dispatchers.Default) {
            filterChannels(
                filterInput.channels, filterInput.favoriteUrls, filterInput.channelHistory,
                filterInput.hiddenGroups, filterInput.hiddenChannels, filterInput.filterKey, filterInput.customLists,
            )
        }
        viewModel.setVisible(filterInput, filtered)
    }

    val minuteClock = rememberLiveTvMinuteClock()
    // Stays set until the player has taken over the screen, so the preview can't start again
    // beside it during the navigation.
    LaunchedEffect(started) { if (!started) launching = false }

    /** Plays [channel] live, or its past [programme] (catch-up) when one is given. */
    val launchPlay: (LiveTvChannel, LiveTvProgramme?) -> Unit = { channel, programme ->
        if (programme == null && previewsEnabled && previewConfirmedChannel != channel.streamUrl) {
            previewConfirmedChannel = channel.streamUrl
        } else if (!launching) {
            launching = true
            previewConfirmedChannel = null
            // The player needs the decoder and, with one-connection providers, the connection.
            preview.release()
            scope.launch {
                try {
                    val list = visibleChannels.takeIf { list -> list.any { it.streamUrl == channel.streamUrl } }.orEmpty()
                    // The player's categories list each category once: a source's category opens as that category.
                    val folder = (filterFor(filterKey) as? LiveTvFilter.SourceGroup)?.name ?: filterKey
                    LiveTvRepository.setZapList(list, folderKey = folder)
                    val route = if (programme != null) {
                        liveTvCatchupRoute(channel, programme, viewModel.profileId)
                    } else {
                        liveTvPlayerRoute(channel, viewModel.profileId)
                    }
                    if (route == null) {
                        launching = false
                        val message = if (programme == null && channel.streamUrl.isListChannelUrl()) {
                            context.getString(R.string.live_tv_list_unavailable_reason, ListChannels.lastFailure.orEmpty())
                        } else {
                            context.getString(R.string.live_tv_catchup_failed)
                        }
                        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()
                        return@launch
                    }
                    viewModel.restoreFocusOnReturn = true
                    onPlay(route)
                } catch (error: Exception) {
                    launching = false
                    throw error
                }
            }
        }
    }
    val currentLaunchPlay by rememberUpdatedState(launchPlay)

    // Focus put on a category by the guide (Back, ◀) does not pick it: only the viewer's moves do.
    val holdCategory = remember { mutableStateOf(false) }
    /** Focuses [target] in the persistent category rail. */
    val openCategories: (FocusRequester) -> Unit = { target ->
        categoriesOpen = true
        scope.launch {
            repeat(3) {
                withFrameNanos { }
                if (runCatching { target.requestFocus() }.isSuccess) return@launch
            }
            // The selected category is out of view: brought into view first.
            val index = if (target === categoryFocus) viewModel.categoryKeys.indexOf(filterKey) else 0
            if (index >= 0) categoryList.scrollToItem((index - 3).coerceAtLeast(0))
            repeat(5) {
                withFrameNanos { }
                if (runCatching { target.requestFocus() }.isSuccess) return@launch
            }
            // Never leave the panel open with the focus still in the guide.
            categoriesOpen = false
        }
    }
    val toCategories: () -> Unit = {
        holdCategory.value = true
        openCategories(categoryFocus)
    }
    val currentToCategories by rememberUpdatedState(toCategories)
    val currentOpenCategories by rememberUpdatedState(openCategories)
    val filterSourceId = when (filter) {
        is LiveTvFilter.Source -> filter.id
        is LiveTvFilter.SourceGroup -> filter.id
        else -> null
    }
    // Unfavouriting a channel in Favorites removes its row: the guide moves to the next one, or
    // to the categories when none is left.
    var keepAfterRefilter by remember { mutableStateOf<String?>(null) }
    val keepNeighbour: (LiveTvChannel) -> Unit = { channel ->
        val index = visibleChannels.indexOfFirst { it.streamUrl == channel.streamUrl }
        keepAfterRefilter = (visibleChannels.getOrNull(index + 1) ?: visibleChannels.getOrNull(index - 1))?.streamUrl
    }
    // Set once the guide exists (it is made below).
    var onGuideLongPress: (LiveTvChannel) -> Unit = {}
    val guide = remember {
        LiveTvGuideState(
            channels = emptyList(),
            startIndex = 0,
            onPlay = { channel -> currentLaunchPlay(channel, null) },
            onCatchup = { channel, programme -> currentLaunchPlay(channel, programme) },
            // Back, and ◀ from what is on now, go to the categories, as in TV guides: to the one
            // selected, or (scrolled out of view) the nearest one.
            onClose = { currentToCategories() },
            onExitLeft = { currentToCategories() },
            // ▲ from the first channel returns to the selected category.
            onExitUp = { currentOpenCategories(categoryFocus) },
            // Held OK: in a playlist of the viewer's, moves the channel; elsewhere starts picking
            // channels for favorites or a playlist.
            onLongPress = { channel -> onGuideLongPress(channel) },
            leadMs = 0L,
        )
    }
    // Entering from the category rail always starts at the first channel in that filter.
    val toGuide: () -> Boolean = {
        if (visibleChannels.isEmpty()) {
            false
        } else {
            guide.selectRow(0)
            guide.backToNow()
            runCatching { gridFocus.requestFocus() }.isSuccess
        }
    }
    val headerSourceLabel = (guide.channel?.sourceId ?: filterSourceId)
        ?.let { sourceId -> uiState.sources.firstOrNull { it.id == sourceId }?.label }
        ?: uiState.sources.singleOrNull()?.label
    onGuideLongPress = { channel ->
        val shownList = filterFor(filterKey) as? LiveTvFilter.Custom
        when {
            // Held while picking: ticks, as a press would.
            guide.selecting -> guide.togglePicked(channel)
            shownList != null -> guide.startMoving()
            else -> guide.startSelecting(channel)
        }
    }
    guide.onReorder = { channel, step ->
        (filterFor(filterKey) as? LiveTvFilter.Custom)?.let { list ->
            // Steps over the channels shown, not ones no source lists now.
            val shown = visibleChannels.mapTo(HashSet(visibleChannels.size * 2)) { it.streamUrl }
            LiveTvRepository.moveInCustomList(list.id, channel.streamUrl, step, shown::contains)
        }
    }
    LaunchedEffect(filterKey) { guide.stopMoving() }
    LaunchedEffect(guide.channel?.streamUrl) { previewConfirmedChannel = null }
    /**
     * A list chosen while picking: the picked channels go in (or, when all of them are in it
     * already, come out), and the guide is as before.
     */
    val choosePlaylist: (String) -> Unit = { key ->
        val picked = guide.picked.values.toList()
        if (key == NEW_LIST_KEY) {
            naming = true
        } else {
            val custom = filterFor(key) as? LiveTvFilter.Custom
            val inIt: Set<String> = if (custom != null) {
                uiState.customLists.firstOrNull { it.id == custom.id }?.urls?.toHashSet().orEmpty()
            } else {
                uiState.favoriteUrls
            }
            val removing = picked.isNotEmpty() && picked.all { it.streamUrl in inIt }
            // Taking the selected channel out of the list on screen: the guide moves to its neighbour.
            if (removing && key == filterKey) guide.channel?.takeIf { it.streamUrl in guide.picked }?.let(keepNeighbour)
            when {
                custom != null && removing -> LiveTvRepository.removeFromCustomList(custom.id, picked)
                custom != null -> LiveTvRepository.addToCustomList(custom.id, picked)
                removing -> LiveTvRepository.removeFavorites(picked)
                else -> LiveTvRepository.addFavorites(picked)
            }
            guide.stopSelecting()
            toGuide()
        }
    }
    // A different category starts at its first channel and at now; refiltering the same one
    // (for example after hiding a channel) keeps the current position.
    var shownFor by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(visibleChannels, filtering, filterKey) {
        if (filtering) return@LaunchedEffect
        val shownKey = filterKey
        val filterChanged = shownFor != shownKey
        val keep = when {
            keepAfterRefilter != null -> keepAfterRefilter
            filterChanged -> null
            else -> guide.channel?.streamUrl ?: uiState.recentChannel?.streamUrl
        }
        keepAfterRefilter = null
        guide.showChannels(visibleChannels, keepUrl = keep, toNow = filterChanged)
        shownFor = shownKey
        if (visibleChannels.isEmpty() && gridFocused) currentToCategories()
    }
    // What is on now stays selected as time passes, and again after the app comes back.
    LaunchedEffect(guide) { snapshotFlow { minuteClock.value }.collect { guide.followNow() } }
    LaunchedEffect(started) { if (started) guide.backToNow() }

    // Back from the player: the guide on the channel last watched, focused.
    LaunchedEffect(visibleChannels, filtering) {
        if (!viewModel.restoreFocusOnReturn || filtering || visibleChannels.isEmpty()) return@LaunchedEffect
        viewModel.restoreFocusOnReturn = false
        guide.selectRow(visibleChannels.indexOfFirst { it.streamUrl == uiState.recentChannel?.streamUrl }.coerceAtLeast(0))
        guide.backToNow()
        repeat(10) {
            withFrameNanos { }
            if (runCatching { gridFocus.requestFocus() }.isSuccess) return@LaunchedEffect
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NuvioTheme.colors.Background),
    ) {
        if (!uiState.isLoaded && !uiState.isLoading && !uiState.hasSource) {
            LiveTvEmptyState(onAddSource = { showSourceDialog = true })
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = NuvioTheme.spacing.xxl, end = NuvioTheme.spacing.xl, top = topPadding),
            ) {
                LaunchedEffect(gridFocused) { if (gridFocused) categoriesOpen = false }
                Column(modifier = Modifier.weight(0.27f).fillMaxHeight()) {
                    LiveTvModuleHeader(
                        showTitle = showBuiltInHeader,
                        sourceLabel = headerSourceLabel,
                        modifier = Modifier.fillMaxWidth().then(
                            if (showBuiltInHeader) Modifier.wrapContentHeight() else Modifier.height(HEADER_HEIGHT)
                        ),
                    )
                    Spacer(Modifier.height(16.dp))
                    LiveTvCategoryColumn(
                        uiState = uiState,
                        listState = categoryList,
                        holdSelection = holdCategory,
                        viewModel = viewModel,
                        selectedKey = filterKey,
                        selectedFocus = categoryFocus,
                        onSelect = { filterKey = it },
                        // OK on a category shows its channels.
                        onPicked = { toGuide() },
                        onNewList = { naming = true },
                        onEditList = { editingList = it },
                        pickMode = guide.selecting,
                        onChoose = choosePlaylist,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            // Back while picking stops picking, rather than leaving Live TV.
                            .onPreviewKeyEvent { event ->
                                val back = event.key == Key.Back || event.key == Key.Escape
                                if (!back || !guide.selecting) return@onPreviewKeyEvent false
                                if (event.type == KeyEventType.KeyUp) {
                                    guide.stopSelecting()
                                    toGuide()
                                }
                                true
                            }
                            // Right from the category rail moves focus to the guide.
                            .onKeyEvent { event ->
                                event.type == KeyEventType.KeyDown && event.key == Key.DirectionRight && toGuide()
                            },
                    )
                }
                Box(
                    Modifier.fillMaxHeight().width(1.dp)
                        .background(NuvioTheme.colors.SurfaceVariant)
                )
                Column(modifier = Modifier.weight(0.73f).fillMaxHeight()) {
                    LiveTvHeader(
                        guide = guide,
                        uiState = uiState,
                        gridFocused = gridFocused,
                        clock = minuteClock,
                        showTitle = false,
                        preview = if (previewsEnabled) preview else null,
                        playVideo = previewConfirmedChannel != null &&
                            previewConfirmedChannel == guide.channel?.streamUrl &&
                            (gridFocused || categoriesOpen) && started && !launching &&
                            !showSourceDialog && !naming && editingList == null,
                        modifier = Modifier.fillMaxWidth().height(HEADER_HEIGHT),
                    )
                    Spacer(Modifier.height(NuvioTheme.spacing.sm))
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth()
                            .focusRequester(gridFocus)
                            .onFocusChanged { gridFocused = it.isFocused }
                            .onPreviewKeyEvent { guide.onKey(it.nativeKeyEvent) }
                            // Focusable even when empty so focus remains here until it can return to categories.
                            .focusable(),
                    ) {
                        LiveTvGuideGrid(
                            state = guide,
                            active = gridFocused,
                            clock = minuteClock,
                            channelColumn = 224.dp,
                            rowHeight = 54.dp,
                            modifier = Modifier.fillMaxSize(),
                            corner = { LiveTvGuideDate(guide.viewStartMs, minuteClock) },
                        )
                        if (visibleChannels.isEmpty() && !filtering && uiState.isLoaded && !uiState.isLoading) {
                            Text(
                                text = when {
                                    filter == LiveTvFilter.Favorites -> stringResource(R.string.live_tv_no_favorites)
                                    filter == LiveTvFilter.History -> stringResource(R.string.live_tv_history_empty)
                                    filter is LiveTvFilter.Custom -> stringResource(R.string.live_tv_playlist_empty)
                                    else -> stringResource(R.string.live_tv_no_channels_found)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = NuvioTheme.colors.TextSecondary,
                                modifier = Modifier.padding(top = 40.dp, start = 8.dp).widthIn(max = 520.dp),
                            )
                        }
                        if (guide.selecting) {
                            LiveTvPickingBar(count = guide.picked.size, modifier = Modifier.align(Alignment.BottomCenter))
                        } else if (guide.moving) {
                            LiveTvPickingBar(count = null, modifier = Modifier.align(Alignment.BottomCenter))
                        }
                    }
                }
            }
        }
    }

    editingList?.let { id ->
        LiveTvPlaylistsDialog(onDismiss = { editingList = null }, openOnly = id)
    }
    if (naming) {
        LiveTvNewListDialog(
            // While picking, the picked channels go in it.
            channels = if (guide.selecting) guide.picked.values.toList() else emptyList(),
            onCreated = { id ->
                naming = false
                if (guide.selecting) {
                    // Back to the guide once the dialog is gone (below).
                    guide.stopSelecting()
                    categoriesOpen = false
                } else {
                    // The new, empty playlist is shown, with how to fill it.
                    filterKey = FILTER_LIST_PREFIX + id
                }
            },
            onDismiss = { naming = false },
        )
    }
    if (showSourceDialog) LiveTvSourceDialog(onDismiss = { showSourceDialog = false })
    // A closed dialog restores focus to the last active side of the TV layout.
    val anyDialog = showSourceDialog || editingList != null || naming
    var hadDialog by remember { mutableStateOf(false) }
    LaunchedEffect(anyDialog) {
        if (anyDialog) {
            hadDialog = true
            return@LaunchedEffect
        }
        if (!hadDialog) return@LaunchedEffect
        hadDialog = false
        repeat(5) {
            withFrameNanos { }
            val target = if (!categoriesOpen) gridFocus else categoryFocus
            if (runCatching { target.requestFocus() }.isSuccess) return@LaunchedEffect
        }
    }
}

/** While picking channels: how many, and the keys. */
@Composable
private fun LiveTvPickingBar(count: Int?, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(bottom = NuvioTheme.spacing.md)
            .background(Color(0xF0121214), NuvioTheme.configuredShape(RoundedCornerShape(50)))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Without a count: a channel of a playlist is being moved.
        if (count != null) {
            Text(
                text = pluralStringResource(R.plurals.live_tv_picked, count, count),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = NuvioTheme.colors.TextPrimary,
                modifier = Modifier.padding(end = 16.dp),
            )
        }
        Text(
            text = stringResource(if (count != null) R.string.live_tv_picking_keys else R.string.live_tv_moving_keys),
            style = MaterialTheme.typography.bodySmall,
            color = NuvioTheme.colors.TextSecondary,
        )
    }
}

private const val NEW_LIST_KEY = "\u0000newlist"

/** TV and its selected provider stay aligned above the same column as filters and groups. */
@Composable
private fun LiveTvModuleHeader(
    showTitle: Boolean,
    sourceLabel: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (showTitle) {
            Text(
                text = stringResource(R.string.live_tv_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = NuvioTheme.colors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            sourceLabel?.takeIf(String::isNotBlank)?.let { provider ->
                Text(
                    text = provider,
                    style = MaterialTheme.typography.bodySmall,
                    color = NuvioTheme.colors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/** The selected channel/program and live preview above the guide. */
@Composable
private fun LiveTvHeader(
    guide: LiveTvGuideState,
    uiState: com.nuvio.tv.reshaped.livetv.LiveTvUiState,
    gridFocused: Boolean,
    clock: State<Long>,
    showTitle: Boolean,
    showPoster: Boolean = true,
    preview: LiveTvPreviewPlayer?,
    playVideo: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val shownChannel = guide.channel
    val shownProgramme = remember(shownChannel, guide.anchorMs, uiState.guideVersion, uiState.currentProgrammes, gridFocused) {
        // The programme selected in the guide, or what is on now on the selected channel.
        (if (gridFocused) guide.selected() else null) ?: shownChannel?.guideKey?.let(uiState.currentProgrammes::get)
    }
    val failedSource = uiState.sources.firstOrNull { it.id in uiState.sourceErrors }
    val status = when {
        uiState.isLoading -> stringResource(R.string.live_tv_loading)
        failedSource != null -> stringResource(
            R.string.live_tv_source_error,
            failedSource.label,
            uiState.sourceErrors[failedSource.id]?.message(context).orEmpty(),
        )
        uiState.error != null -> uiState.error?.message(context)
        uiState.isEpgLoading -> stringResource(R.string.live_tv_guide_loading)
        else -> null
    }
    Row(modifier = modifier) {
        LiveTvGuideInfo(
            channel = shownChannel,
            logo = shownChannel?.let(uiState::logoFor),
            sourceLabel = shownChannel?.sourceId?.let { sourceId -> uiState.sources.firstOrNull { it.id == sourceId }?.label },
            programme = shownProgramme,
            clock = clock,
            status = status,
            statusIsError = (failedSource != null || uiState.error != null) && !uiState.isLoading,
            showTitle = showTitle,
            showPoster = preview == null,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
        if (preview != null) {
            LiveTvPreviewVideo(
                preview = preview,
                channel = shownChannel,
                logo = shownChannel?.let { channel ->
                    // A list channel is not previewed live: it shows the picture of what is on.
                    (if (channel.streamUrl.startsWith(com.nuvio.tv.reshaped.livetv.lists.LIST_CHANNEL_SCHEME)) {
                        uiState.currentProgrammes[channel.guideKey]?.image
                            ?: com.nuvio.tv.reshaped.livetv.lists.ListChannels.channelPoster(channel.streamUrl)
                    } else null) ?: uiState.logoFor(channel)
                },
                playVideo = playVideo,
                modifier = Modifier.padding(start = NuvioTheme.spacing.xl).fillMaxHeight(),
            )
        }
    }
}

private val HEADER_HEIGHT = 116.dp

private const val POSTER_DELAY_MS = 250L
private val POSTER_WIDTH = HEADER_HEIGHT * 16 / 9

/** "Wed, Sep 30 · 9:43 AM": the day the guide shows, and the time now. */
@Composable
private fun LiveTvGuideDate(viewStartMs: Long, clock: State<Long>) {
    val pattern = remember { DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEMMMd") }
    val day = remember(pattern, viewStartMs / 3_600_000L) {
        java.text.SimpleDateFormat(pattern, Locale.getDefault()).format(java.util.Date(maxOf(viewStartMs, LiveTvClock.nowEpochMs())))
    }
    Text(
        text = "$day  ·  ${LiveTvClock.formatClock(clock.value)}",
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
        color = NuvioTheme.colors.TextSecondary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

/** The built-in header identifies Live TV and its source; hidden headers retain channel details. */
@Composable
private fun LiveTvGuideInfo(
    channel: LiveTvChannel?,
    logo: String?,
    sourceLabel: String?,
    programme: LiveTvProgramme?,
    clock: State<Long>,
    status: String?,
    statusIsError: Boolean,
    showTitle: Boolean,
    showPoster: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        // Loaded once the selection rests, so holding ▼ through channels starts no image loads;
        // once a guide has given a picture its place stays, so the text does not jump between rows.
        val image = programme?.image ?: channel?.let {
            if (it.streamUrl.startsWith(com.nuvio.tv.reshaped.livetv.lists.LIST_CHANNEL_SCHEME)) {
                com.nuvio.tv.reshaped.livetv.lists.ListChannels.channelPoster(it.streamUrl)
            } else null
        }
        val poster by produceState<String?>(null, image) {
            value = null
            if (image == null) return@produceState
            delay(POSTER_DELAY_MS)
            value = image
        }
        var posterSlot by remember { mutableStateOf(false) }
        LaunchedEffect(image != null) { if (image != null) posterSlot = true }
        if (showPoster && posterSlot) {
            Box(modifier = Modifier.padding(end = NuvioTheme.spacing.lg).size(POSTER_WIDTH, HEADER_HEIGHT)) {
                LiveTvPoster(url = poster, width = POSTER_WIDTH, height = HEADER_HEIGHT)
            }
        }
        // A new selection fades in instead of switching hard; the fade is drawn, not recomposed.
        val fade = remember { androidx.compose.animation.core.Animatable(1f) }
        LaunchedEffect(channel?.streamUrl, programme?.startEpochMs) {
            fade.snapTo(0.3f)
            fade.animateTo(1f, androidx.compose.animation.core.tween(220, easing = androidx.compose.animation.core.LinearOutSlowInEasing))
        }
        Column(modifier = Modifier.weight(1f).fillMaxHeight().graphicsLayer { alpha = fade.value }) {
            if (showTitle) {
                Text(
                    text = stringResource(R.string.live_tv_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NuvioTheme.colors.TextPrimary,
                )
                sourceLabel?.takeIf(String::isNotBlank)?.let { provider ->
                    Text(
                        text = provider,
                        style = MaterialTheme.typography.bodySmall,
                        color = NuvioTheme.colors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            } else if (channel != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LiveTvLogo(url = logo, name = channel.name, width = 40.dp, height = 24.dp)
                    Text(
                        text = channel.name.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        letterSpacing = 1.sp,
                        color = NuvioTheme.colors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 10.dp),
                    )
                }
                sourceLabel?.takeIf(String::isNotBlank)?.let { provider ->
                    Text(
                        text = provider,
                        style = MaterialTheme.typography.bodySmall,
                        color = NuvioTheme.colors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                Text(
                    text = programme?.title ?: channel.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = NuvioTheme.colors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            if (!showTitle && programme != null && channel != null) {
                val now = clock.value
                val timing = listOfNotNull(
                    LiveTvClock.formatSpan(programme),
                    when {
                        programme.stopEpochMs <= now -> stringResource(R.string.live_tv_guide_ended)
                        programme.startEpochMs <= now -> liveTvTimeLeft(programme, clock)
                        else -> null
                    },
                    stringResource(R.string.live_tv_catchup).takeIf {
                        LiveTvCatchupLinks.isPlayable(channel.catchup, programme, now)
                    },
                ).joinToString("  ·  ")
                Text(
                    text = timing,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NuvioTheme.colors.TextSecondary,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp),
                )
                if (programme.startEpochMs <= now && now < programme.stopEpochMs) {
                    LiveTvProgressBar(
                        programme = programme,
                        clock = clock,
                        fill = NuvioTheme.colors.TextPrimary,
                        track = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier.padding(top = 6.dp).widthIn(max = 420.dp).fillMaxWidth(),
                    )
                }
                programme.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = NuvioTheme.colors.TextTertiary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 6.dp).widthIn(max = 640.dp),
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            if (status != null) {
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (statusIsError) NuvioTheme.colors.Error else NuvioTheme.colors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/** One row of the category column. */
private class LiveTvCategoryEntry(
    val key: String,
    val label: String,
    val count: Int? = null,
    val heading: Boolean = false,
    val folded: Boolean = false,
    val indent: Boolean = false,
    val sourceId: String? = null,
    /** The id of one of the viewer's playlists: held OK opens it to edit. */
    val listId: String? = null,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    /** "+ New playlist": OK names one, focus picks nothing. */
    val action: Boolean = false,
)

/**
 * Favorites, History, then the categories; with several sources, each source's
 * categories sit under its own heading (with its channel count), which OK folds away.
 */
@Composable
private fun LiveTvCategoryColumn(
    uiState: com.nuvio.tv.reshaped.livetv.LiveTvUiState,
    listState: androidx.compose.foundation.lazy.LazyListState,
    holdSelection: androidx.compose.runtime.MutableState<Boolean>,
    viewModel: LiveTvScreenModel,
    selectedKey: String,
    selectedFocus: FocusRequester,
    onSelect: (String) -> Unit,
    onPicked: () -> Unit,
    onNewList: () -> Unit,
    onEditList: (String) -> Unit,
    modifier: Modifier = Modifier,
    /** Channels are being picked: only Favorites and the playlists, to choose where they go. */
    pickMode: Boolean = false,
    onChoose: (String) -> Unit = {},
) {
    val selectedModifier = Modifier.focusRequester(selectedFocus)
    val visibleGroups = remember(uiState.groups, uiState.hiddenGroups) { uiState.visibleGroups }
    // Which categories each source has and the channels shown: one pass over the channels, off
    // the main thread, kept in the model so coming back from the player shows them at once.
    val sections by produceState(viewModel.sourceSections(uiState, visibleGroups), uiState.channels, visibleGroups, uiState.hiddenChannelKeys, uiState.sources) {
        value = viewModel.sourceSections(uiState, visibleGroups)
            ?: withContext(Dispatchers.Default) { viewModel.computeSourceSections(uiState, visibleGroups) }
    }
    val collapsed = viewModel.collapsedSources
    val favoritesLabel = stringResource(R.string.live_tv_favorites)
    val historyLabel = stringResource(R.string.live_tv_history)
    val newListLabel = stringResource(R.string.live_tv_playlist_new)
    val uncategorised = liveTvGroupLabel(LIVE_TV_UNGROUPED)
    val customLists = uiState.customLists
    val coroutineScope = rememberCoroutineScope()
    var customOrder by remember { mutableStateOf(uiState.categoryRailOrder) }
    LaunchedEffect(uiState.categoryRailOrder) { customOrder = uiState.categoryRailOrder }
    var movingKey by remember { mutableStateOf<String?>(null) }
    fun isSyntheticLiveGroup(group: String) = group.equals("Live TV", ignoreCase = true) ||
        group.equals("List channels", ignoreCase = true) ||
        group.equals("Live channels", ignoreCase = true)

    val pickEntries = remember(newListLabel, customLists, favoritesLabel) {
        buildList {
            add(LiveTvCategoryEntry(FILTER_FAVORITES, favoritesLabel, icon = Icons.Default.Star))
            customLists.forEach { add(LiveTvCategoryEntry(FILTER_LIST_PREFIX + it.id, it.name, count = it.urls.size, listId = it.id)) }
            add(LiveTvCategoryEntry(NEW_LIST_KEY, newListLabel, action = true))
        }
    }
    val baseEntries = remember(sections, visibleGroups, uiState.groupNames, customLists, collapsed.toMap(), favoritesLabel, historyLabel, uncategorised) {
        fun label(group: String) = liveTvGroupName(group, uiState.groupNames) ?: if (group == LIVE_TV_UNGROUPED) uncategorised else group
        buildList {
            add(LiveTvCategoryEntry(FILTER_FAVORITES, favoritesLabel, icon = Icons.Default.Star))
            add(LiveTvCategoryEntry(FILTER_HISTORY, historyLabel, icon = Icons.Default.History))
            customLists.forEach { add(LiveTvCategoryEntry(FILTER_LIST_PREFIX + it.id, it.name, count = it.urls.size, listId = it.id)) }
            val bySource = sections?.sources
            if (!bySource.isNullOrEmpty()) {
                bySource.forEach { section ->
                    // A provider starts folded until the viewer explicitly expands it.
                    val folded = collapsed[section.source.id] != false
                    add(
                        LiveTvCategoryEntry(
                            key = FILTER_SOURCE_PREFIX + section.source.id,
                            label = section.source.label.uppercase(),
                            count = section.channelCount,
                            heading = true,
                            folded = folded,
                            sourceId = section.source.id,
                        ),
                    )
                    if (!folded) section.groups.filterNot(::isSyntheticLiveGroup).forEach {
                        add(LiveTvCategoryEntry(sourceGroupKey(section.source.id, it), label(it), indent = true))
                    }
                }
            } else {
                visibleGroups.filterNot(::isSyntheticLiveGroup).distinct().forEach {
                    add(LiveTvCategoryEntry(it, label(it), count = uiState.groupCounts[it]))
                }
            }
        }
    }
    val entries = remember(baseEntries, customOrder) {
        val ordered = if (customOrder.isEmpty()) {
            baseEntries
        } else {
            val orderMap = customOrder.mapIndexed { index, key -> key to index }.toMap()
            baseEntries.sortedBy { orderMap[it.key] ?: (1000 + baseEntries.indexOf(it)) }
        }
        val history = ordered.firstOrNull { it.key == FILTER_HISTORY }
        if (history == null) ordered else ordered.filterNot { it.key == FILTER_HISTORY }.toMutableList().apply {
            val favoritesIndex = indexOfFirst { it.key == FILTER_FAVORITES }
            add((favoritesIndex + 1).coerceAtLeast(0), history)
        }
    }
    // The guide's ◀ and Back find the selected category in this list.
    SideEffect { viewModel.categoryKeys = if (pickMode) emptyList() else entries.map { it.key } }
    // Choosing a playlist starts from the top, where "Choose the playlist" says what this is.
    LaunchedEffect(pickMode) { if (pickMode) listState.scrollToItem(0) }
    var previousSelectedKey by remember { mutableStateOf(selectedKey) }
    // Reveal a newly selected group, but keep a manually folded source folded on screen restore.
    LaunchedEffect(selectedKey) {
        val selectedGroup = filterFor(selectedKey) as? LiveTvFilter.SourceGroup
        if (selectedKey != previousSelectedKey) {
            // Missing entries mean folded; explicitly keep the parent expanded while its group is selected.
            selectedGroup?.let { collapsed[it.id] = false }
        }
        previousSelectedKey = selectedKey
    }
    // Glides to the focused category, as Nuvio's own lists do.
    androidx.compose.runtime.CompositionLocalProvider(
        androidx.compose.foundation.gestures.LocalBringIntoViewSpec provides com.nuvio.tv.ui.components.NuvioScrollDefaults.smoothScrollSpec,
    ) {
    LazyColumn(
        state = listState,
        modifier = modifier.focusRestorer(selectedFocus).focusGroup(),
        contentPadding = PaddingValues(top = 2.dp, bottom = NuvioTheme.spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (pickMode) {
            item(key = "\u0000choose") {
                Column(modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 6.dp, bottom = 8.dp)) {
                    Text(
                        text = stringResource(R.string.live_tv_choose_playlist),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NuvioTheme.colors.TextPrimary,
                    )
                    Text(
                        text = stringResource(R.string.live_tv_choose_playlist_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = NuvioTheme.colors.TextSecondary,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
            itemsIndexed(pickEntries, key = { _, it -> "pick" + it.key }) { index, entry ->
                LiveTvCategoryItem(
                    label = entry.label,
                    selected = false,
                    anchor = index == 0,
                    selectedModifier = selectedModifier,
                    // Consumes the guide's ◀ mark, so a later category focus picks as usual.
                    holdSelection = holdSelection,
                    count = entry.count,
                    icon = if (entry.action) Icons.Filled.Add else null,
                    onClick = { onChoose(entry.key) },
                ) {}
            }
            return@LazyColumn
        }
        items(entries, key = { it.key }, contentType = { if (it.heading) "heading" else "category" }) { entry ->
            val isMoving = movingKey == entry.key
            LiveTvCategoryItem(
                label = entry.label,
                selected = selectedKey == entry.key,
                selectedModifier = selectedModifier,
                holdSelection = holdSelection,
                count = entry.count,
                filterControl = entry.key == FILTER_FAVORITES ||
                    entry.key == FILTER_HISTORY || entry.key.startsWith(FILTER_SOURCE_PREFIX),
                heading = entry.heading,
                folded = entry.folded,
                indent = entry.indent,
                moving = isMoving,
                onMove = { step ->
                    val currentIdx = entries.indexOfFirst { it.key == entry.key }
                    val targetIdx = (currentIdx + step).coerceIn(0, entries.lastIndex)
                    if (currentIdx != targetIdx && currentIdx >= 0) {
                        val newOrder = entries.map { it.key }.toMutableList()
                        val moved = newOrder.removeAt(currentIdx)
                        newOrder.add(targetIdx, moved)
                        customOrder = newOrder
                        LiveTvRepository.saveCategoryRailOrder(newOrder)
                        coroutineScope.launch { listState.scrollToItem(targetIdx.coerceIn(0, entries.lastIndex)) }
                    }
                },
                onDrop = { movingKey = null },
                icon = entry.icon ?: if (entry.action) Icons.Filled.Add else null,
                onClick = if (isMoving) {
                    { movingKey = null }
                } else {
                    entry.sourceId?.let { id ->
                        {
                            val folding = !entry.folded
                            if (folding && (filterFor(selectedKey) as? LiveTvFilter.SourceGroup)?.id == id) {
                                // Keep selection on the provider when its selected child rows are hidden.
                                onSelect(FILTER_SOURCE_PREFIX + id)
                            }
                            collapsed[id] = folding
                        }
                    } ?: if (entry.action) onNewList else {
                        {
                            onSelect(entry.key)
                            onPicked()
                        }
                    }
                },
                onLongClick = if (entry.key == FILTER_HISTORY) null else {
                    {
                        movingKey = entry.key
                        holdSelection?.value = true
                    }
                },
            ) { if (!entry.action && !isMoving) onSelect(entry.key) }
        }
    }
    }
}

/**
 * A category: selecting happens on focus, like Netflix's genre rail, so the guide follows the
 * remote. A source heading ([heading]) folds its categories on OK ([onClick]).
 */
@Composable
private fun LiveTvCategoryItem(
    label: String,
    selected: Boolean,
    selectedModifier: Modifier,
    /** Takes [selectedModifier] without being shown as selected. */
    anchor: Boolean = false,
    count: Int? = null,
    filterControl: Boolean = false,
    heading: Boolean = false,
    folded: Boolean = false,
    indent: Boolean = false,
    moving: Boolean = false,
    onMove: ((Int) -> Unit)? = null,
    onDrop: (() -> Unit)? = null,
    holdSelection: androidx.compose.runtime.MutableState<Boolean>? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onSelect: () -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val longPressKeyTracker = rememberLongPressKeyTracker()
    var longPressTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(focused) {
        if (!focused || moving) return@LaunchedEffect
        // Focus the guide put here (Back, ◀) only shows where the viewer is.
        if (holdSelection?.value == true) {
            holdSelection.value = false
            return@LaunchedEffect
        }
        if (!selected) {
            delay(250) // passing over a category does not re-filter
            onSelect()
        }
    }
    val shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))
    Card(
        onClick = {
            if (longPressTriggered) {
                longPressTriggered = false
            } else if (moving) {
                (onDrop ?: {})()
            } else {
                (onClick ?: onSelect)()
            }
        },
        onLongClick = if (moving) null else onLongClick,
        modifier = (if (selected || anchor) selectedModifier else Modifier)
            .fillMaxWidth()
            .padding(top = if (heading) 8.dp else 0.dp)
            .onFocusChanged { focused = it.isFocused }
            .onPreviewKeyEvent { event ->
                if (moving && onMove != null && onDrop != null) {
                    if (event.type == KeyEventType.KeyDown) {
                        when (event.key) {
                            Key.DirectionUp -> {
                                onMove(-1)
                                true
                            }
                            Key.DirectionDown -> {
                                onMove(1)
                                true
                            }
                            Key.DirectionCenter, Key.Enter, Key.NumPadEnter, Key.Back, Key.Escape -> {
                                onDrop()
                                true
                            }
                            else -> false
                        }
                    } else false
                } else if (onLongClick != null &&
                    longPressKeyTracker.handle(event.nativeKeyEvent, ::isSelectKey) {
                        longPressTriggered = true
                        onLongClick()
                    }
                ) {
                    true
                } else {
                    false
                }
            },
        shape = CardDefaults.shape(shape),
        border = CardDefaults.border(
            border = if (moving) Border(BorderStroke(2.dp, NuvioTheme.colors.Primary)) else Border.None,
            focusedBorder = if (moving) Border(BorderStroke(2.dp, Color.White)) else Border.None
        ),
        colors = CardDefaults.colors(
            containerColor = when {
                moving -> NuvioTheme.colors.Primary.copy(alpha = 0.25f)
                selected -> NuvioTheme.colors.FocusBackground
                filterControl -> NuvioTheme.colors.SurfaceVariant
                else -> Color.Transparent
            },
            focusedContainerColor = if (moving) NuvioTheme.colors.Primary else NuvioTheme.colors.FocusBackground,
        ),
        scale = CardDefaults.scale(focusedScale = if (moving) 1.05f else 1.02f),
    ) {
        val content = if (focused && moving) Color.Black
            else if (focused || selected || moving) NuvioTheme.colors.TextPrimary
            else NuvioTheme.colors.TextSecondary
        Row(
            modifier = Modifier.padding(start = if (indent) 22.dp else 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (moving) {
                Icon(
                    imageVector = Icons.Filled.UnfoldMore,
                    contentDescription = null,
                    tint = content,
                    modifier = Modifier.padding(end = 6.dp).size(18.dp),
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = content,
                    modifier = Modifier.padding(end = 6.dp).size(18.dp),
                )
            }
            if (heading) {
                Icon(
                    imageVector = if (folded) Icons.AutoMirrored.Filled.KeyboardArrowRight else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = content,
                    modifier = Modifier.padding(end = 6.dp).size(18.dp),
                )
            }
            Text(
                text = label,
                style = if (heading) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected || heading || moving) FontWeight.SemiBold else FontWeight.Normal,
                letterSpacing = if (heading) 1.sp else androidx.compose.ui.unit.TextUnit.Unspecified,
                color = content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
                    .then(if (focused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier),
            )
            if (count != null && !moving) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (focused) Color.Black.copy(alpha = 0.6f) else NuvioTheme.colors.TextTertiary,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}



@Composable
private fun LiveTvEmptyState(onAddSource: () -> Unit) {
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    Column(
        modifier = Modifier.fillMaxSize().padding(NuvioTheme.spacing.xxxl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.live_tv_empty_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = NuvioTheme.colors.TextPrimary,
        )
        Text(
            text = stringResource(R.string.live_tv_empty_description),
            style = MaterialTheme.typography.bodyMedium,
            color = NuvioTheme.colors.TextSecondary,
            modifier = Modifier.widthIn(max = 520.dp).padding(top = NuvioTheme.spacing.sm, bottom = NuvioTheme.spacing.lg),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        LiveTvPillButton(
            text = stringResource(R.string.live_tv_add_source),
            onClick = onAddSource,
            modifier = Modifier.focusRequester(focus),
        )
    }
}

private fun isSelectKey(keyCode: Int): Boolean {
    return keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER ||
        keyCode == android.view.KeyEvent.KEYCODE_ENTER ||
        keyCode == android.view.KeyEvent.KEYCODE_NUMPAD_ENTER
}

