package com.nuvio.tv.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.core.shizuku.ShizukuStartup
import com.nuvio.tv.ui.theme.NuvioTheme
import rikka.shizuku.Shizuku

@Composable
internal fun ShizukuStartupSettingsGroup() {
    val context = LocalContext.current
    var autoStartEnabled by remember { mutableStateOf(ShizukuStartup.autoStartEnabled(context)) }
    var isConfiguring by remember { mutableStateOf(false) }
    val startupStatus by ShizukuStartup.status.collectAsState()

    LaunchedEffect(context) {
        ShizukuStartup.refreshStatus(context)
    }

    DisposableEffect(context) {
        val listener = Shizuku.OnRequestPermissionResultListener { requestCode, _ ->
            if (requestCode == ShizukuStartup.PERMISSION_REQUEST_CODE) {
                ShizukuStartup.refreshStatus(context)
            }
        }
        Shizuku.addRequestPermissionResultListener(listener)
        onDispose { Shizuku.removeRequestPermissionResultListener(listener) }
    }

    val statusText = when (startupStatus) {
        ShizukuStartup.Status.DISABLED -> stringResource(R.string.settings_shizuku_status_disabled)
        ShizukuStartup.Status.NOT_RUNNING -> stringResource(R.string.settings_shizuku_status_not_running)
        ShizukuStartup.Status.CONNECTING -> stringResource(R.string.settings_shizuku_status_connecting)
        ShizukuStartup.Status.STARTING_SERVICE -> stringResource(R.string.settings_shizuku_status_starting)
        ShizukuStartup.Status.READY -> stringResource(R.string.settings_shizuku_status_ready)
        ShizukuStartup.Status.PERMISSION_REQUIRED -> stringResource(R.string.settings_shizuku_status_permission)
        ShizukuStartup.Status.SHIZUKU_NOT_INSTALLED -> stringResource(R.string.settings_shizuku_status_missing)
        ShizukuStartup.Status.ADB_PORT_UNAVAILABLE -> stringResource(R.string.settings_shizuku_status_adb_unavailable)
        ShizukuStartup.Status.ADB_AUTHORIZATION_REQUIRED -> stringResource(R.string.settings_shizuku_status_adb_authorization)
        ShizukuStartup.Status.ADB_AUTHORIZATION_PENDING -> stringResource(R.string.settings_shizuku_status_authorization_pending)
        ShizukuStartup.Status.STARTUP_TIMEOUT -> stringResource(R.string.settings_shizuku_status_timeout)
        ShizukuStartup.Status.FAILED -> stringResource(R.string.settings_shizuku_status_failed)
    }

    SettingsGroupCard(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(R.string.settings_shizuku_group)
    ) {
        SettingsToggleRow(
            title = stringResource(R.string.settings_shizuku_autostart_title),
            subtitle = stringResource(R.string.settings_shizuku_autostart_subtitle),
            checked = autoStartEnabled,
            onToggle = {
                autoStartEnabled = !autoStartEnabled
                ShizukuStartup.setAutoStartEnabled(context, autoStartEnabled)
                if (autoStartEnabled) {
                    isConfiguring = true
                    ShizukuStartup.configure(context) { isConfiguring = false }
                }
            }
        )
        SettingsActionRow(
            title = stringResource(R.string.settings_shizuku_setup_title),
            subtitle = statusText,
            onClick = {
                isConfiguring = true
                ShizukuStartup.configure(context) { isConfiguring = false }
            },
            enabled = !isConfiguring
        )
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(R.string.settings_shizuku_setup_help),
                style = MaterialTheme.typography.bodySmall,
                color = NuvioTheme.colors.TextSecondary
            )
        }
    }
}
