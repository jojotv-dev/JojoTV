@file:OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class)

package com.nuvio.tv.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nuvio.tv.R

@Composable
internal fun ServersSettingsContent(
    initialFocusRequester: FocusRequester?,
) {
    var selectedServerId by rememberSaveable { mutableStateOf<String?>(null) }
    var freeboxPageOpen by rememberSaveable { mutableStateOf(false) }
    val serverDetailFocusRequester = remember { FocusRequester() }
    val freeboxFocusRequester = remember { FocusRequester() }

    BackHandler(enabled = selectedServerId != null || freeboxPageOpen) {
        if (selectedServerId != null) selectedServerId = null
        else freeboxPageOpen = false
    }

    val connectionDetailsId = selectedServerId
    if (connectionDetailsId != null) {
        MediaServerSettingsContent(
            connectionId = connectionDetailsId,
            initialFocusRequester = serverDetailFocusRequester,
            onOpenServer = { selectedServerId = it },
            onBack = { selectedServerId = null },
        )
        return
    }

    if (freeboxPageOpen) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SettingsDetailHeader(
                title = stringResource(R.string.server_hub_freebox),
                subtitle = stringResource(R.string.settings_servers_freebox_subtitle),
            )
            FreeboxSettingsContent(
                initialFocusRequester = freeboxFocusRequester,
                modifier = Modifier.weight(1f),
            )
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SettingsDetailHeader(
            title = stringResource(R.string.server_hub_title),
            subtitle = stringResource(R.string.settings_media_servers_subtitle),
        )

        MediaServersSettingsContent(
            initialFocusRequester = initialFocusRequester,
            onOpenServer = { selectedServerId = it },
            modifier = Modifier.weight(1f),
            showHeader = false,
        )

        SettingsGroupCard(modifier = Modifier.fillMaxWidth()) {
            SettingsActionRow(
                title = stringResource(R.string.server_hub_freebox),
                subtitle = stringResource(R.string.settings_servers_freebox_subtitle),
                onClick = { freeboxPageOpen = true },
                leadingIcon = Icons.Default.Folder,
            )
        }
    }
}
