@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.nuvio.tv.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.nuvio.tv.R
import com.nuvio.tv.core.tracking.TrackingProviderId
import com.nuvio.tv.data.local.MoreLikeThisSourcePreference
import com.nuvio.tv.data.local.TraktSettingsDataStore
import com.nuvio.tv.data.local.WatchProgressSource
import com.nuvio.tv.domain.model.LibrarySourceMode
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.theme.NuvioTheme
import kotlinx.coroutines.delay

@Composable
fun TrackingSettingsScreen(
    onBackPress: () -> Unit
) {
    val initialFocusRequester = remember { FocusRequester() }

    BackHandler { onBackPress() }

    LaunchedEffect(Unit) {
        delay(160L)
        runCatching { initialFocusRequester.requestFocus() }
    }

    SettingsStandaloneScaffold(
        title = stringResource(R.string.settings_tracking_title),
        subtitle = stringResource(R.string.settings_tracking_description),
        classicContainer = false
    ) {
        TrackingSettingsContent(initialFocusRequester = initialFocusRequester)
    }
}

@Composable
fun TrackingSettingsContent(
    traktViewModel: TraktViewModel = hiltViewModel(),
    mdbListViewModel: MdbListTrackerViewModel = hiltViewModel(),
    trackingViewModel: TrackingSettingsViewModel = hiltViewModel(),
    initialFocusRequester: FocusRequester? = null
) {
    val traktState by traktViewModel.uiState.collectAsStateWithLifecycle()
    val mdbListState by mdbListViewModel.uiState.collectAsStateWithLifecycle()
    val mdbListLibraryLists by mdbListViewModel.libraryLists.collectAsStateWithLifecycle()
    val trackingState by trackingViewModel.uiState.collectAsStateWithLifecycle()
    val ownTraktFocusRequester = remember { FocusRequester() }
    val traktFocusRequester = initialFocusRequester ?: ownTraktFocusRequester
    val mdbListFocusRequester = remember { FocusRequester() }
    val libraryFocusRequester = remember { FocusRequester() }
    val watchProgressFocusRequester = remember { FocusRequester() }
    val continueWatchingFocusRequester = remember { FocusRequester() }
    val moreLikeThisFocusRequester = remember { FocusRequester() }

    var activeProvider by remember { mutableStateOf<TrackingProviderId?>(null) }
    var dismissOnConnected by remember { mutableStateOf<TrackingProviderId?>(null) }
    var disconnectProvider by remember { mutableStateOf<TrackingProviderId?>(null) }
    var restoreFocusTarget by remember { mutableStateOf<TrackingFocusTarget?>(null) }
    var showLibrarySourceDialog by remember { mutableStateOf(false) }
    var showWatchProgressDialog by remember { mutableStateOf(false) }
    var showDaysCapDialog by remember { mutableStateOf(false) }
    var showMoreLikeThisSourceDialog by remember { mutableStateOf(false) }
    var showMdbListLibraryListsDialog by remember { mutableStateOf(false) }

    val hasOverlay = activeProvider != null ||
        disconnectProvider != null ||
        showLibrarySourceDialog ||
        showWatchProgressDialog ||
        showDaysCapDialog ||
        showMoreLikeThisSourceDialog ||
        showMdbListLibraryListsDialog

    LaunchedEffect(
        activeProvider,
        dismissOnConnected,
        traktState.mode,
        mdbListState.isConnected
    ) {
        val connected = when (dismissOnConnected) {
            TrackingProviderId.TRAKT -> traktState.mode == TraktConnectionMode.CONNECTED
            TrackingProviderId.SIMKL -> false
            TrackingProviderId.MDBLIST -> mdbListState.isConnected
            null -> false
        }
        if (activeProvider == dismissOnConnected && connected) {
            activeProvider = null
            dismissOnConnected = null
        }
    }

    LaunchedEffect(hasOverlay, restoreFocusTarget) {
        val target = restoreFocusTarget ?: return@LaunchedEffect
        if (hasOverlay) return@LaunchedEffect
        delay(120L)
        runCatching {
            when (target) {
                TrackingFocusTarget.TRAKT -> traktFocusRequester.requestFocus()
                TrackingFocusTarget.MDBLIST -> mdbListFocusRequester.requestFocus()
                TrackingFocusTarget.LIBRARY -> libraryFocusRequester.requestFocus()
                TrackingFocusTarget.WATCH_PROGRESS -> watchProgressFocusRequester.requestFocus()
                TrackingFocusTarget.CONTINUE_WATCHING -> continueWatchingFocusRequester.requestFocus()
                TrackingFocusTarget.MORE_LIKE_THIS -> moreLikeThisFocusRequester.requestFocus()
            }
        }
        restoreFocusTarget = null
    }

    val openProvider: (TrackingProviderId) -> Unit = openProvider@{ provider ->
        if (provider == TrackingProviderId.SIMKL) return@openProvider
        restoreFocusTarget = when (provider) {
            TrackingProviderId.TRAKT -> TrackingFocusTarget.TRAKT
            TrackingProviderId.MDBLIST -> TrackingFocusTarget.MDBLIST
            TrackingProviderId.SIMKL -> return@openProvider
        }
        activeProvider = provider
        disconnectProvider = null
        when (provider) {
            TrackingProviderId.TRAKT -> {
                if (traktState.mode == TraktConnectionMode.CONNECTED) {
                    dismissOnConnected = null
                } else {
                    dismissOnConnected = provider
                    if (traktState.mode == TraktConnectionMode.DISCONNECTED && !traktState.isLoading) {
                        traktViewModel.onConnectClick()
                    }
                }
            }
            TrackingProviderId.MDBLIST -> {
                if (mdbListState.isConnected) {
                    dismissOnConnected = null
                    mdbListViewModel.onAccountOpened()
                } else {
                    dismissOnConnected = provider
                    mdbListViewModel.onConnect()
                }
            }
        }
    }

    TrackingSettingsOverview(
        traktState = traktState,
        mdbListState = mdbListState,
        trackingState = trackingState,
        traktFocusRequester = traktFocusRequester,
        mdbListFocusRequester = mdbListFocusRequester,
        libraryFocusRequester = libraryFocusRequester,
        watchProgressFocusRequester = watchProgressFocusRequester,
        continueWatchingFocusRequester = continueWatchingFocusRequester,
        moreLikeThisFocusRequester = moreLikeThisFocusRequester,
        onTraktClick = { openProvider(TrackingProviderId.TRAKT) },
        onMdbListClick = { openProvider(TrackingProviderId.MDBLIST) },
        onLibrarySourceClick = {
            restoreFocusTarget = TrackingFocusTarget.LIBRARY
            showLibrarySourceDialog = true
        },
        onWatchProgressClick = {
            restoreFocusTarget = TrackingFocusTarget.WATCH_PROGRESS
            showWatchProgressDialog = true
        },
        onContinueWatchingWindowClick = {
            restoreFocusTarget = TrackingFocusTarget.CONTINUE_WATCHING
            showDaysCapDialog = true
        },
        onCommentsChanged = traktViewModel::onShowMetaCommentsChanged,
        onMoreLikeThisClick = {
            restoreFocusTarget = TrackingFocusTarget.MORE_LIKE_THIS
            showMoreLikeThisSourceDialog = true
        },
        mdbListLibraryLists = mdbListLibraryLists,
        onMdbListLibraryListsClick = {
            mdbListViewModel.onLibraryListsOpened()
            showMdbListLibraryListsDialog = true
        }
    )

    when (activeProvider) {
        TrackingProviderId.TRAKT -> {
            TraktAccountDialog(
                state = traktState,
                onStartConnection = traktViewModel::onConnectClick,
                onRetryPolling = traktViewModel::onRetryPolling,
                onDisconnect = {
                    activeProvider = null
                    dismissOnConnected = null
                    disconnectProvider = TrackingProviderId.TRAKT
                },
                onDismiss = {
                    if (traktState.mode != TraktConnectionMode.CONNECTED) {
                        traktViewModel.onCancelDeviceFlow()
                    }
                    activeProvider = null
                    dismissOnConnected = null
                }
            )
        }
        TrackingProviderId.MDBLIST -> {
            MdbListAccountDialog(
                state = mdbListState,
                onStartConnection = mdbListViewModel::onConnect,
                onRetryPolling = mdbListViewModel::onRetryPolling,
                onSync = mdbListViewModel::onSyncNow,
                onDisconnect = {
                    activeProvider = null
                    dismissOnConnected = null
                    disconnectProvider = TrackingProviderId.MDBLIST
                },
                onDismiss = {
                    if (!mdbListState.isConnected) mdbListViewModel.onCancel()
                    activeProvider = null
                    dismissOnConnected = null
                }
            )
        }
        else -> Unit
    }

    disconnectProvider?.let { provider ->
        NuvioDialog(
            onDismiss = {
                disconnectProvider = null
                activeProvider = provider
            },
            title = stringResource(
                when (provider) {
                    TrackingProviderId.TRAKT -> R.string.trakt_disconnect_title
                    TrackingProviderId.MDBLIST -> R.string.mdblist_disconnect_title
                    TrackingProviderId.SIMKL -> R.string.action_cancel
                }
            ),
            subtitle = stringResource(
                when (provider) {
                    TrackingProviderId.TRAKT -> R.string.trakt_disconnect_subtitle
                    TrackingProviderId.MDBLIST -> R.string.mdblist_disconnect_subtitle
                    TrackingProviderId.SIMKL -> R.string.action_cancel
                }
            ),
            width = 520.dp,
            suppressFirstKeyUp = false
        ) {
            SettingsDialogActionRow {
                SettingsDialogActionButton(
                    text = stringResource(R.string.action_cancel),
                    onClick = {
                        disconnectProvider = null
                        activeProvider = provider
                    }
                )
                SettingsDialogActionButton(
                    text = stringResource(R.string.trakt_disconnect),
                    onClick = {
                        disconnectProvider = null
                        when (provider) {
                            TrackingProviderId.TRAKT -> traktViewModel.onDisconnectClick()
                            TrackingProviderId.MDBLIST -> mdbListViewModel.onDisconnect()
                            TrackingProviderId.SIMKL -> Unit
                        }
                    },
                    primary = true
                )
            }
        }
    }

    if (showLibrarySourceDialog) {
        SettingsSingleChoiceDialog(
            title = stringResource(R.string.trakt_library_source_dialog_title),
            subtitle = stringResource(R.string.tracking_library_source_dialog_subtitle),
            options = trackingState.availableLibrarySourceModes.map { mode ->
                SettingsPickerOption(mode, librarySourceLabel(mode))
            },
            selectedValue = trackingState.librarySourceMode,
            onOptionSelected = { mode ->
                trackingViewModel.selectLibrarySourceMode(mode)
                showLibrarySourceDialog = false
            },
            onDismiss = { showLibrarySourceDialog = false },
            width = 620.dp,
            maxHeight = 340.dp
        )
    }

    if (showWatchProgressDialog) {
        SettingsSingleChoiceDialog(
            title = stringResource(R.string.trakt_watch_progress_dialog_title),
            subtitle = stringResource(R.string.tracking_watch_progress_dialog_subtitle),
            options = trackingState.availableWatchProgressSources.map { source ->
                SettingsPickerOption(source, watchProgressSourceLabel(source))
            },
            selectedValue = trackingState.watchProgressSource,
            onOptionSelected = { source ->
                trackingViewModel.selectWatchProgressSource(source)
                showWatchProgressDialog = false
            },
            onDismiss = { showWatchProgressDialog = false },
            width = 660.dp,
            maxHeight = 360.dp
        )
    }

    if (showDaysCapDialog) {
        val options = remember {
            listOf(
                14,
                30,
                60,
                90,
                180,
                365,
                TraktSettingsDataStore.CONTINUE_WATCHING_DAYS_CAP_ALL
            )
        }
        SettingsSingleChoiceDialog(
            title = stringResource(R.string.trakt_cw_window_title),
            subtitle = stringResource(R.string.trakt_cw_window_subtitle),
            options = options.map { days ->
                SettingsPickerOption(days, continueWatchingWindowLabel(days))
            },
            selectedValue = traktState.continueWatchingDaysCap,
            onOptionSelected = { days ->
                traktViewModel.onContinueWatchingDaysCapSelected(days)
                showDaysCapDialog = false
            },
            onDismiss = { showDaysCapDialog = false },
            width = 620.dp,
            maxHeight = 420.dp
        )
    }

    if (showMoreLikeThisSourceDialog) {
        SettingsSingleChoiceDialog(
            title = stringResource(R.string.trakt_more_like_this_source_dialog_title),
            subtitle = stringResource(R.string.trakt_more_like_this_source_dialog_subtitle),
            options = listOf(
                SettingsPickerOption(
                    MoreLikeThisSourcePreference.TRAKT,
                    stringResource(R.string.trakt_more_like_this_source_trakt)
                ),
                SettingsPickerOption(
                    MoreLikeThisSourcePreference.TMDB,
                    stringResource(R.string.trakt_more_like_this_source_tmdb)
                ),
            ),
            selectedValue = traktState.moreLikeThisSource,
            onOptionSelected = { source ->
                traktViewModel.onMoreLikeThisSourceSelected(source)
                showMoreLikeThisSourceDialog = false
            },
            onDismiss = { showMoreLikeThisSourceDialog = false },
            width = 620.dp,
            maxHeight = 320.dp
        )
    }

    if (showMdbListLibraryListsDialog) {
        MdbListLibraryListsDialog(
            state = mdbListLibraryLists,
            onToggle = mdbListViewModel::onToggleLibraryList,
            onDismiss = { showMdbListLibraryListsDialog = false }
        )
    }

}

@Composable
internal fun TrackingSettingsOverview(
    traktState: TraktUiState,
    mdbListState: MdbListTrackerUiState,
    trackingState: TrackingSettingsUiState,
    traktFocusRequester: FocusRequester,
    mdbListFocusRequester: FocusRequester,
    libraryFocusRequester: FocusRequester,
    watchProgressFocusRequester: FocusRequester,
    continueWatchingFocusRequester: FocusRequester,
    moreLikeThisFocusRequester: FocusRequester,
    onTraktClick: () -> Unit,
    onMdbListClick: () -> Unit,
    onLibrarySourceClick: () -> Unit,
    onWatchProgressClick: () -> Unit,
    onContinueWatchingWindowClick: () -> Unit,
    onCommentsChanged: (Boolean) -> Unit,
    onMoreLikeThisClick: () -> Unit,
    mdbListLibraryLists: MdbListLibraryListsUiState = MdbListLibraryListsUiState(),
    onMdbListLibraryListsClick: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val traktPresentation = traktConnectionPresentation(traktState)
    val mdbListPresentation = mdbListConnectionPresentation(mdbListState)
    val traktConnected = traktState.mode == TraktConnectionMode.CONNECTED
    val traktProgressActive = trackingState.watchProgressSource == WatchProgressSource.TRAKT

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SettingsDetailHeader(
            title = stringResource(R.string.settings_tracking_title),
            subtitle = stringResource(R.string.settings_tracking_description)
        )
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(TrackingSettingsTestTags.OVERVIEW_LIST),
                contentPadding = PaddingValues(bottom = NuvioTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item(key = "tracking_accounts") {
                    SettingsGroupCard(
                        title = stringResource(R.string.tracking_accounts_title),
                        subtitle = stringResource(R.string.tracking_accounts_subtitle)
                    ) {
                        SettingsActionRow(
                            title = stringResource(R.string.trakt_name),
                            subtitle = traktPresentation.subtitle,
                            value = traktPresentation.value,
                            valueColor = traktPresentation.color,
                            leadingRawIconRes = R.raw.trakt_tv_favicon,
                            leadingArtworkSize = 40.dp,
                            onClick = onTraktClick,
                            modifier = Modifier
                                .focusRequester(traktFocusRequester)
                                .testTag(TrackingSettingsTestTags.TRAKT_PROVIDER)
                        )
                        SettingsActionRow(
                            title = stringResource(R.string.mdblist_name),
                            subtitle = mdbListPresentation.subtitle,
                            value = mdbListPresentation.value,
                            valueColor = mdbListPresentation.color,
                            leadingRawIconRes = R.raw.mdblist_logo,
                            leadingArtworkSize = 40.dp,
                            onClick = onMdbListClick,
                            modifier = Modifier
                                .focusRequester(mdbListFocusRequester)
                                .testTag(TrackingSettingsTestTags.MDBLIST_PROVIDER)
                        )
                    }
                }
                item(key = "tracking_sources") {
                    SettingsGroupCard(
                        title = stringResource(R.string.tracking_sources_title),
                        subtitle = stringResource(R.string.tracking_sources_subtitle)
                    ) {
                        SettingsActionRow(
                            title = stringResource(R.string.trakt_library_source_title),
                            subtitle = stringResource(R.string.trakt_library_source_subtitle),
                            value = librarySourceLabel(trackingState.librarySourceMode),
                            enabled = trackingState.isReady,
                            onClick = onLibrarySourceClick,
                            modifier = Modifier
                                .focusRequester(libraryFocusRequester)
                                .testTag(TrackingSettingsTestTags.LIBRARY_SOURCE)
                        )
                        SettingsActionRow(
                            title = stringResource(R.string.trakt_watch_progress_title),
                            subtitle = stringResource(R.string.trakt_watch_progress_subtitle),
                            value = watchProgressSourceLabel(trackingState.watchProgressSource),
                            enabled = trackingState.isReady,
                            onClick = onWatchProgressClick,
                            modifier = Modifier
                                .focusRequester(watchProgressFocusRequester)
                                .testTag(TrackingSettingsTestTags.WATCH_PROGRESS_SOURCE)
                        )
                        if (traktConnected) {
                            SettingsActionRow(
                                title = stringResource(R.string.trakt_more_like_this_source_title),
                                subtitle = stringResource(R.string.trakt_more_like_this_source_subtitle),
                                value = moreLikeThisSourceLabel(traktState.moreLikeThisSource),
                                enabled = true,
                                onClick = onMoreLikeThisClick,
                                modifier = Modifier
                                    .focusRequester(moreLikeThisFocusRequester)
                                    .testTag(TrackingSettingsTestTags.MORE_LIKE_THIS)
                            )
                        }
                    }
                }
                if (traktConnected) {
                    item(key = "tracking_trakt_features") {
                        SettingsGroupCard(
                            title = stringResource(R.string.tracking_trakt_features_title),
                            subtitle = stringResource(R.string.tracking_trakt_features_subtitle)
                        ) {
                            SettingsActionRow(
                                title = stringResource(R.string.trakt_continue_watching_window),
                                subtitle = if (!traktProgressActive) {
                                    stringResource(R.string.tracking_trakt_progress_required)
                                } else {
                                    stringResource(R.string.trakt_continue_watching_subtitle)
                                },
                                value = continueWatchingWindowLabel(traktState.continueWatchingDaysCap),
                                enabled = traktProgressActive,
                                onClick = onContinueWatchingWindowClick,
                                modifier = Modifier
                                    .focusRequester(continueWatchingFocusRequester)
                                    .testTag(TrackingSettingsTestTags.CONTINUE_WATCHING)
                            )
                            SettingsToggleRow(
                                title = stringResource(R.string.trakt_comments_title),
                                subtitle = stringResource(R.string.trakt_comments_subtitle),
                                checked = traktState.showMetaComments,
                                enabled = true,
                                onToggle = {
                                    onCommentsChanged(!traktState.showMetaComments)
                                },
                                modifier = Modifier.testTag(TrackingSettingsTestTags.COMMENTS)
                            )
                        }
                    }
                }
                if (mdbListState.isConnected) {
                    item(key = "tracking_mdblist_features") {
                        SettingsGroupCard(
                            title = stringResource(R.string.tracking_mdblist_features_title),
                            subtitle = stringResource(R.string.tracking_mdblist_features_subtitle)
                        ) {
                            SettingsActionRow(
                                title = stringResource(R.string.mdblist_library_lists),
                                subtitle = stringResource(R.string.mdblist_library_lists_description),
                                value = mdbListLibraryLists.lists.takeIf { it.isNotEmpty() }?.let { lists ->
                                    stringResource(R.string.mdblist_library_lists_summary, lists.count { it.visible }, lists.size)
                                },
                                onClick = onMdbListLibraryListsClick,
                                modifier = Modifier.testTag(TrackingSettingsTestTags.MDBLIST_LIBRARY_LISTS)
                            )
                        }
                    }
                }
            }
            SettingsVerticalScrollIndicators(state = listState)
        }
    }
}

private data class TrackingConnectionPresentation(
    val subtitle: String,
    val value: String,
    val color: Color
)

@Composable
private fun traktConnectionPresentation(state: TraktUiState): TrackingConnectionPresentation {
    return when {
        state.isLoading && state.mode != TraktConnectionMode.CONNECTED -> TrackingConnectionPresentation(
            subtitle = stringResource(R.string.tracking_connecting_provider, stringResource(R.string.trakt_name)),
            value = stringResource(R.string.tracking_status_connecting),
            color = NuvioTheme.colors.Info
        )
        state.mode == TraktConnectionMode.CONNECTED -> TrackingConnectionPresentation(
            subtitle = stringResource(
                R.string.trakt_connected_as,
                state.username ?: stringResource(R.string.trakt_user_fallback)
            ),
            value = stringResource(R.string.tracking_status_connected),
            color = NuvioTheme.colors.Success
        )
        state.mode == TraktConnectionMode.AWAITING_APPROVAL -> TrackingConnectionPresentation(
            subtitle = stringResource(R.string.tracking_finish_connection),
            value = stringResource(R.string.tracking_status_waiting),
            color = NuvioTheme.colors.Warning
        )
        else -> TrackingConnectionPresentation(
            subtitle = stringResource(R.string.trakt_description),
            value = stringResource(R.string.tracking_status_disconnected),
            color = NuvioTheme.colors.TextSecondary
        )
    }
}

@Composable
private fun mdbListConnectionPresentation(state: MdbListTrackerUiState): TrackingConnectionPresentation = when {
    state.isLoading && !state.isConnected -> TrackingConnectionPresentation(
        stringResource(R.string.tracking_connecting_provider, stringResource(R.string.mdblist_name)),
        stringResource(R.string.tracking_status_connecting), NuvioTheme.colors.Info
    )
    state.isConnected -> TrackingConnectionPresentation(
        stringResource(R.string.mdblist_connected_as, state.username ?: stringResource(R.string.mdblist_account_fallback)),
        stringResource(R.string.tracking_status_connected), NuvioTheme.colors.Success
    )
    state.session != null -> TrackingConnectionPresentation(
        state.errorMessage ?: stringResource(R.string.tracking_finish_connection),
        stringResource(R.string.tracking_status_waiting), NuvioTheme.colors.Warning
    )
    else -> TrackingConnectionPresentation(
        state.errorMessage ?: stringResource(R.string.mdblist_tracking_description),
        stringResource(R.string.tracking_status_disconnected), NuvioTheme.colors.TextSecondary
    )
}

@Composable
private fun watchProgressSourceLabel(source: WatchProgressSource): String = when (source) {
    WatchProgressSource.TRAKT -> stringResource(R.string.trakt_name)
    WatchProgressSource.SIMKL -> stringResource(R.string.trakt_watch_progress_source_nuvio)
    WatchProgressSource.MDBLIST -> stringResource(R.string.mdblist_name)
    WatchProgressSource.NUVIO_SYNC -> stringResource(R.string.trakt_watch_progress_source_nuvio)
}

@Composable
private fun librarySourceLabel(mode: LibrarySourceMode): String = when (mode) {
    LibrarySourceMode.TRAKT -> stringResource(R.string.trakt_name)
    LibrarySourceMode.SIMKL -> stringResource(R.string.trakt_library_source_nuvio)
    LibrarySourceMode.MDBLIST -> stringResource(R.string.mdblist_name)
    LibrarySourceMode.LOCAL -> stringResource(R.string.trakt_library_source_nuvio)
}

@Composable
private fun moreLikeThisSourceLabel(source: MoreLikeThisSourcePreference): String = when (source) {
    MoreLikeThisSourcePreference.TRAKT -> stringResource(R.string.trakt_name)
    MoreLikeThisSourcePreference.TMDB -> stringResource(R.string.trakt_more_like_this_source_tmdb)
}

@Composable
private fun continueWatchingWindowLabel(days: Int): String {
    return if (days == TraktSettingsDataStore.CONTINUE_WATCHING_DAYS_CAP_ALL) {
        stringResource(R.string.trakt_all_history)
    } else {
        stringResource(R.string.trakt_days_format, days)
    }
}

private enum class TrackingFocusTarget {
    TRAKT,
    MDBLIST,
    LIBRARY,
    WATCH_PROGRESS,
    CONTINUE_WATCHING,
    MORE_LIKE_THIS
}

internal object TrackingSettingsTestTags {
    const val OVERVIEW_LIST = "tracking_overview_list"
    const val TRAKT_PROVIDER = "tracking_provider_trakt"
    const val MDBLIST_PROVIDER = "tracking_provider_mdblist"
    const val MDBLIST_LIBRARY_LISTS = "tracking_mdblist_library_lists"
    const val LIBRARY_SOURCE = "tracking_source_library"
    const val WATCH_PROGRESS_SOURCE = "tracking_source_watch_progress"
    const val CONTINUE_WATCHING = "tracking_trakt_continue_watching"
    const val COMMENTS = "tracking_trakt_comments"
    const val MORE_LIKE_THIS = "tracking_trakt_more_like_this"
}
