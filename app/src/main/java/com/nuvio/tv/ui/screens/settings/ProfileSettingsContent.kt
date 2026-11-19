@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.nuvio.tv.ui.screens.settings

import com.nuvio.tv.ui.theme.NuvioTheme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.nuvio.tv.R
import com.nuvio.tv.domain.model.ProfileHomePresentation

@Composable
internal fun ProfileSettingsContent(
    onManageProfiles: () -> Unit,
    homePresentation: ProfileHomePresentation,
    onHomePresentationSelected: (ProfileHomePresentation) -> Unit,
    initialFocusRequester: FocusRequester? = null
) {
    var showHomePresentationDialog by remember { mutableStateOf(false) }
    val homePresentationLabel = stringResource(
        when (homePresentation) {
            ProfileHomePresentation.ORIGINAL -> R.string.profile_home_presentation_original
            ProfileHomePresentation.LAUNCHER -> R.string.profile_home_presentation_launcher
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md)
    ) {
        SettingsDetailHeader(
            title = stringResource(R.string.profile_title),
            subtitle = stringResource(R.string.profile_subtitle)
        )
        SettingsGroupCard(modifier = Modifier.fillMaxWidth()) {
            SettingsActionRow(
                title = stringResource(R.string.profile_manage_button),
                subtitle = null,
                onClick = onManageProfiles,
                modifier = if (initialFocusRequester != null) {
                    Modifier.focusRequester(initialFocusRequester)
                } else {
                    Modifier
                }
            )
            SettingsActionRow(
                title = stringResource(R.string.profile_home_presentation_title),
                subtitle = null,
                value = homePresentationLabel,
                onClick = { showHomePresentationDialog = true }
            )
        }
    }

    if (showHomePresentationDialog) {
        SettingsSingleChoiceDialog(
            title = stringResource(R.string.profile_home_presentation_title),
            options = listOf(
                SettingsPickerOption(
                    ProfileHomePresentation.ORIGINAL,
                    stringResource(R.string.profile_home_presentation_original)
                ),
                SettingsPickerOption(
                    ProfileHomePresentation.LAUNCHER,
                    stringResource(R.string.profile_home_presentation_launcher)
                )
            ),
            selectedValue = homePresentation,
            onOptionSelected = { selected ->
                onHomePresentationSelected(selected)
                showHomePresentationDialog = false
            },
            onDismiss = { showHomePresentationDialog = false },
            maxHeight = 240.dp
        )
    }
}
