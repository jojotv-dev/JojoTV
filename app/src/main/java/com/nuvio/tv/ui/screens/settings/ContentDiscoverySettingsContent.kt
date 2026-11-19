@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.nuvio.tv.ui.screens.settings

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.nuvio.tv.R
import com.nuvio.tv.ui.components.mylists.MyListsLinkAccountDialog
import com.nuvio.tv.ui.theme.NuvioTheme

@Composable
internal fun ContentDiscoverySettingsContent(
    onNavigateToAddons: () -> Unit,
    onNavigateToPlugins: () -> Unit,
    showPlugins: Boolean,
    initialFocusRequester: FocusRequester?
) {
    var focusPluginsOnReturn by rememberSaveable { mutableStateOf(false) }
    ContentDiscoveryHub(
        onNavigateToAddons = {
            focusPluginsOnReturn = false
            onNavigateToAddons()
        },
        onNavigateToPlugins = {
            focusPluginsOnReturn = true
            onNavigateToPlugins()
        },
        showPlugins = showPlugins,
        initialFocusRequester = initialFocusRequester,
        focusPluginsOnReturn = focusPluginsOnReturn && showPlugins
    )
}

@Composable
private fun ContentDiscoveryHub(
    onNavigateToAddons: () -> Unit,
    onNavigateToPlugins: () -> Unit,
    showPlugins: Boolean,
    initialFocusRequester: FocusRequester?,
    focusPluginsOnReturn: Boolean
) {
    var showMyListsAccountDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md)
    ) {
        SettingsDetailHeader(
            title = stringResource(R.string.settings_content_discovery),
            subtitle = stringResource(R.string.settings_content_discovery_subtitle)
        )
        SettingsGroupCard(modifier = Modifier.fillMaxWidth()) {
            SettingsActionRow(
                title = "My Lists Addon Account",
                subtitle = "Link or manage your My Lists Addon Catalogs, Collections, and Live TV Channels",
                onClick = { showMyListsAccountDialog = true },
                leadingIcon = Icons.Default.QrCode2
            )
            SettingsActionRow(
                title = stringResource(R.string.addon_title),
                subtitle = stringResource(R.string.settings_content_discovery_addons_subtitle),
                onClick = onNavigateToAddons,
                leadingIcon = Icons.Default.Extension,
                modifier = if (initialFocusRequester != null && !focusPluginsOnReturn) {
                    Modifier.focusRequester(initialFocusRequester)
                } else {
                    Modifier
                }
            )
            if (showPlugins) {
                SettingsActionRow(
                    title = stringResource(R.string.plugin_title),
                    subtitle = stringResource(R.string.settings_content_discovery_plugins_subtitle),
                    onClick = onNavigateToPlugins,
                    leadingIcon = Icons.Default.Power,
                    modifier = if (initialFocusRequester != null && focusPluginsOnReturn) {
                        Modifier.focusRequester(initialFocusRequester)
                    } else {
                        Modifier
                    }
                )
            }
        }
        SettingsGroupCard(modifier = Modifier.fillMaxWidth()) {
            LiveTvSettingsItems() // Live TV hook
        }
    }

    if (showMyListsAccountDialog) {
        MyListsLinkAccountDialog(
            onDismiss = { showMyListsAccountDialog = false }
        )
    }
}
