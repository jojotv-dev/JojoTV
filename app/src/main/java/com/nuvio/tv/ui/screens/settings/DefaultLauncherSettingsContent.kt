package com.nuvio.tv.ui.screens.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.nuvio.tv.core.launcher.AccessibilityWatchdog
import com.nuvio.tv.core.launcher.FireOsHomeButtonService
import com.nuvio.tv.core.launcher.LocalAdbSetupFactory
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.nuvio.tv.R
import com.nuvio.tv.ui.components.NuvioButton
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.components.rememberLauncherSetupState
import com.nuvio.tv.ui.theme.NuvioTheme
import androidx.tv.material3.Text
import androidx.compose.ui.unit.dp

@Composable
internal fun DefaultLauncherSettingsContent(initialFocusRequester: FocusRequester? = null) {
    val setup = rememberLauncherSetupState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // Null on flavors/Android versions without the on-device ADB setup.
    val selfSetup = remember { LocalAdbSetupFactory.create(context) }
    var showSelfSetup by remember { mutableStateOf(false) }
    var setupRevision by remember { mutableStateOf(0) }
    var fireOsEnabled by remember(setupRevision) { mutableStateOf(FireOsHomeButtonService.isEnabled(context)) }
    val fireOsReadLogsGranted = remember(setupRevision) { FireOsHomeButtonService.hasReadLogsPermission(context) }
    val fireOsOperational by FireOsHomeButtonService.isOperational.collectAsState()
    var showFireOsPermissionGuide by remember { mutableStateOf(false) }
    val secureGranted = remember(setupRevision) { AccessibilityWatchdog.canWriteSecureSettings(context) }
    var keepOverride by remember(setupRevision) { mutableStateOf(AccessibilityWatchdog.isOptedIn(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                FireOsHomeButtonService.startIfConfigured(context)
                setupRevision++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md)
    ) {
        SettingsDetailHeader(
            title = stringResource(R.string.settings_default_launcher),
            subtitle = stringResource(R.string.settings_default_launcher_subtitle)
        )
        SettingsGroupCard(modifier = Modifier.fillMaxWidth()) {
            SettingsActionRow(
                title = stringResource(
                    if (setup.isDefaultHome) R.string.apps_default_launcher_active
                    else R.string.settings_make_home_app
                ),
                subtitle = stringResource(
                    if (setup.showFallback) R.string.settings_home_role_not_selected
                    else R.string.settings_make_home_app_subtitle
                ),
                onClick = setup.requestDefaultHome,
                modifier = if (initialFocusRequester != null) {
                    Modifier.focusRequester(initialFocusRequester)
                } else {
                    Modifier
                }
            )
            SettingsActionRow(
                title = stringResource(R.string.settings_open_home_settings),
                subtitle = stringResource(
                    if (setup.settingsUnavailable) R.string.settings_home_settings_unavailable
                    else R.string.settings_open_home_settings_subtitle
                ),
                onClick = setup.openHomeSettings
            )
            SettingsActionRow(
                title = stringResource(
                    if (setup.isAccessibilityActive) R.string.apps_home_override_active
                    else if (setup.isAccessibilityEnabled) R.string.apps_home_override_pending
                    else R.string.apps_home_override
                ),
                subtitle = stringResource(R.string.settings_home_override_subtitle),
                onClick = setup.openAccessibilitySettings
            )
            if (FireOsHomeButtonService.isSupported(context) && !setup.isDefaultHome) {
                SettingsToggleRow(
                    title = stringResource(R.string.settings_fire_os_home_override_title),
                    subtitle = stringResource(
                        when {
                            !fireOsReadLogsGranted -> R.string.settings_fire_os_home_override_permission_missing
                            fireOsOperational -> R.string.settings_fire_os_home_override_active
                            fireOsEnabled -> R.string.settings_fire_os_home_override_starting
                            else -> R.string.settings_fire_os_home_override_subtitle
                        }
                    ),
                    checked = fireOsEnabled,
                    onToggle = {
                        if (fireOsEnabled) {
                            FireOsHomeButtonService.setEnabled(context, false)
                            fireOsEnabled = false
                        } else if (!fireOsReadLogsGranted) {
                            showFireOsPermissionGuide = true
                        } else if (FireOsHomeButtonService.setEnabled(context, true)) {
                            fireOsEnabled = true
                        }
                    }
                )
                if (!fireOsReadLogsGranted) {
                    SettingsActionRow(
                        title = stringResource(R.string.settings_fire_os_home_permission_title),
                        subtitle = stringResource(R.string.settings_fire_os_home_permission_subtitle),
                        onClick = { showFireOsPermissionGuide = true }
                    )
                }
            }
            if (selfSetup != null) {
                SettingsActionRow(
                    title = stringResource(R.string.settings_self_setup_title),
                    subtitle = stringResource(
                        if (secureGranted) R.string.settings_self_setup_done_subtitle
                        else R.string.settings_self_setup_subtitle
                    ),
                    onClick = { showSelfSetup = true }
                )
            }
            if (secureGranted) {
                SettingsToggleRow(
                    title = stringResource(R.string.settings_keep_override_title),
                    subtitle = stringResource(R.string.settings_keep_override_subtitle),
                    checked = keepOverride,
                    onToggle = {
                        keepOverride = !keepOverride
                        AccessibilityWatchdog.setOptIn(context, keepOverride)
                    }
                )
            }
        }
        ShizukuStartupSettingsGroup()
        if (showSelfSetup && selfSetup != null) {
            LocalAdbSetupDialog(
                setup = selfSetup,
                onDismiss = {
                    showSelfSetup = false
                    setupRevision++
                },
                onFinished = {
                    showSelfSetup = false
                    setupRevision++
                }
            )
        }
        if (showFireOsPermissionGuide) {
            val command = stringResource(R.string.settings_fire_os_home_permission_command, context.packageName)
            NuvioDialog(
                onDismiss = { showFireOsPermissionGuide = false },
                title = stringResource(R.string.settings_fire_os_home_permission_dialog_title),
                subtitle = stringResource(R.string.settings_fire_os_home_permission_dialog_subtitle),
                width = 720.dp,
                suppressFirstKeyUp = false
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md)) {
                    SelectionContainer {
                        Text(
                            text = command,
                            modifier = Modifier.fillMaxWidth().padding(NuvioTheme.spacing.md),
                            color = NuvioTheme.colors.TextPrimary
                        )
                    }
                    NuvioButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("ADB command", command))
                            Toast.makeText(context, R.string.settings_fire_os_home_permission_copied, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.settings_fire_os_home_permission_copy))
                    }
                }
            }
        }
    }
}
