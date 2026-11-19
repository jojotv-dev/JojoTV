@file:OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class)

package com.nuvio.tv.ui.reshaped.livetv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.reshaped.livetv.LiveTvPreferences
import com.nuvio.tv.reshaped.livetv.LiveTvRepository
import com.nuvio.tv.reshaped.livetv.rememberLiveTvPreviewsEnabled
import com.nuvio.tv.reshaped.livetv.rememberLiveTvPreviewSoundEnabled
import com.nuvio.tv.ui.components.mylists.MyListsLinkAccountDialog
import com.nuvio.tv.ui.screens.settings.SettingsActionRow
import com.nuvio.tv.ui.theme.NuvioTheme

private enum class IptvSettingAction {
    MyListsAccount, Sources, Categories, Playlists, ListChannels, Reload, PreviewSound
}

@Composable
fun LiveTvSettingsContent(initialFocusRequester: FocusRequester? = null) {
    val state by LiveTvRepository.uiState.collectAsStateWithLifecycle()
    val previewsEnabled = rememberLiveTvPreviewsEnabled()
    val previewSound = rememberLiveTvPreviewSoundEnabled()
    val context = androidx.compose.ui.platform.LocalContext.current
    var action by remember { mutableStateOf<IptvSettingAction?>(null) }

    Column(Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 20.dp)) {
        Text("IPTV", style = MaterialTheme.typography.headlineSmall, color = NuvioTheme.colors.TextPrimary)
        Text(
            "Gérer les fournisseurs IPTV, les groupes et les listes de chaînes.",
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
            style = MaterialTheme.typography.bodySmall,
            color = NuvioTheme.colors.TextSecondary,
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(IptvSettingAction.entries.filter { previewsEnabled || it != IptvSettingAction.PreviewSound }, key = { it.name }) { item ->
                IptvSettingRow(
                    action = item,
                    description = when (item) {
                        IptvSettingAction.MyListsAccount -> "Associer votre compte My Lists Addon"
                        IptvSettingAction.Sources -> stringResource(R.string.live_tv_menu_sources_description)
                        IptvSettingAction.Categories -> stringResource(R.string.live_tv_menu_categories_description)
                        IptvSettingAction.Playlists -> stringResource(R.string.live_tv_menu_playlists_description)
                        IptvSettingAction.ListChannels -> stringResource(R.string.live_tv_menu_list_channels_description)
                        IptvSettingAction.Reload -> stringResource(if (state.isLoading) R.string.live_tv_loading else R.string.live_tv_menu_reload_description)
                        IptvSettingAction.PreviewSound -> stringResource(if (previewSound) R.string.live_tv_menu_on else R.string.live_tv_menu_off)
                    },
                    modifier = if (item == IptvSettingAction.MyListsAccount && initialFocusRequester != null) {
                        Modifier.focusRequester(initialFocusRequester)
                    } else Modifier,
                    onClick = {
                        when (item) {
                            IptvSettingAction.Reload -> if (!state.isLoading) LiveTvRepository.refresh()
                            IptvSettingAction.PreviewSound -> LiveTvPreferences.setPreviewSound(context, !previewSound)
                            else -> action = item
                        }
                    },
                )
            }
        }
    }

    when (action) {
        IptvSettingAction.MyListsAccount -> MyListsLinkAccountDialog(onDismiss = { action = null })
        IptvSettingAction.Sources -> LiveTvSourceDialog(onDismiss = { action = null })
        IptvSettingAction.Categories -> LiveTvCategoryDialog(onDismiss = { action = null })
        IptvSettingAction.Playlists -> LiveTvPlaylistsDialog(onDismiss = { action = null })
        IptvSettingAction.ListChannels -> LiveTvListChannelsDialog(onDismiss = { action = null })
        else -> Unit
    }
}

@Composable
private fun IptvSettingRow(
    action: IptvSettingAction,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val title = when (action) {
        IptvSettingAction.MyListsAccount -> stringResource(R.string.my_lists_account_title)
        IptvSettingAction.Sources -> stringResource(R.string.live_tv_menu_sources)
        IptvSettingAction.Categories -> stringResource(R.string.live_tv_menu_categories)
        IptvSettingAction.Playlists -> stringResource(R.string.live_tv_my_playlists)
        IptvSettingAction.ListChannels -> stringResource(R.string.live_tv_menu_list_channels)
        IptvSettingAction.Reload -> stringResource(R.string.live_tv_menu_reload)
        IptvSettingAction.PreviewSound -> stringResource(R.string.live_tv_menu_preview_sound)
    }
    SettingsActionRow(
        title = title,
        subtitle = description,
        onClick = onClick,
        modifier = modifier,
    )
}
