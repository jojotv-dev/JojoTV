package com.nuvio.tv.ui.screens.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuvio.tv.R
import com.nuvio.tv.domain.model.AmbientModeSettings

@Composable
internal fun AmbientModeSettingsCard(
    viewModel: AmbientModeSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = uiState.settings
    var showTimeoutDialog by remember { mutableStateOf(false) }

    SettingsGroupCard(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(R.string.ambient_mode_title),
        subtitle = stringResource(R.string.ambient_mode_subtitle)
    ) {
        SettingsToggleRow(
            title = stringResource(R.string.ambient_mode_title),
            subtitle = stringResource(R.string.ambient_mode_enabled_subtitle),
            checked = settings.enabled,
            onToggle = { viewModel.setEnabled(!settings.enabled) }
        )
        if (settings.enabled) {
            SettingsActionRow(
                title = stringResource(R.string.ambient_mode_timeout),
                subtitle = stringResource(R.string.ambient_mode_timeout_subtitle),
                value = timeoutLabel(settings.timeoutMinutes),
                onClick = { showTimeoutDialog = true }
            )
            SettingsActionRow(
                title = stringResource(R.string.ambient_mode_source),
                subtitle = stringResource(R.string.ambient_mode_source_subtitle),
                value = stringResource(R.string.ambient_mode_source_favorites),
                enabled = false,
                onClick = {}
            )
        }
    }

    if (showTimeoutDialog) {
        SettingsSingleChoiceDialog(
            title = stringResource(R.string.ambient_mode_timeout),
            subtitle = stringResource(R.string.ambient_mode_timeout_subtitle),
            options = AmbientModeSettings.TIMEOUT_OPTIONS_MINUTES.map { minutes ->
                SettingsPickerOption(minutes, timeoutLabel(minutes))
            },
            selectedValue = settings.timeoutMinutes,
            onOptionSelected = { minutes ->
                viewModel.setTimeoutMinutes(minutes)
                showTimeoutDialog = false
            },
            onDismiss = { showTimeoutDialog = false },
            maxHeight = 360.dp
        )
    }

}

@Composable
private fun timeoutLabel(minutes: Int): String =
    pluralStringResource(R.plurals.ambient_mode_timeout_minutes, minutes, minutes)
