package com.nuvio.tv.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.core.content.ContextCompat
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.core.server.LanFileShareHttpServer
import com.nuvio.tv.core.server.LanFileShareService
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.theme.NuvioTheme
import kotlinx.coroutines.delay

@Composable
internal fun LanFileShareDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var isEnabled by remember { mutableStateOf(LanFileShareService.isEnabled(context)) }
    var isRunning by remember { mutableStateOf(LanFileShareService.isRunning()) }
    var addresses by remember { mutableStateOf(LanFileShareHttpServer.localAddresses()) }
    var startError by remember { mutableStateOf(LanFileShareService.startError()) }
    var permissionError by remember { mutableStateOf<String?>(null) }
    val isSupported = remember { LanFileShareService.isSupported() }
    val primaryActionFocusRequester = remember { FocusRequester() }
    val focusCopyAddress = isRunning && addresses.isNotEmpty()
    val allFilesSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            permissionError = null
            LanFileShareService.setEnabled(context, true)
        } else {
            permissionError = context.getString(R.string.lan_share_all_files_required)
        }
    }
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            permissionError = null
            LanFileShareService.setEnabled(context, true)
        } else {
            permissionError = context.getString(R.string.lan_share_permission_required)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            isEnabled = LanFileShareService.isEnabled(context)
            isRunning = LanFileShareService.isRunning()
            addresses = LanFileShareHttpServer.localAddresses()
            startError = LanFileShareService.startError()
            delay(1_000)
        }
    }
    LaunchedEffect(focusCopyAddress) {
        repeat(2) { withFrameNanos { } }
        runCatching { primaryActionFocusRequester.requestFocus() }
    }

    fun requestAllFilesAccess() {
        val appSettingsIntent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        try {
            allFilesSettingsLauncher.launch(appSettingsIntent)
        } catch (_: Exception) {
            try {
                allFilesSettingsLauncher.launch(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
            } catch (_: Exception) {
                permissionError = context.getString(R.string.lan_share_all_files_required)
            }
        }
    }

    fun enableSharing() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager() ->
                requestAllFilesAccess()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED ->
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            else -> LanFileShareService.setEnabled(context, true)
        }
    }

    NuvioDialog(
        onDismiss = onDismiss,
        title = stringResource(R.string.lan_share_title),
        subtitle = stringResource(R.string.lan_share_description),
    ) {
        Column(
            modifier = Modifier.heightIn(max = 560.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = when {
                    !isSupported -> stringResource(R.string.lan_share_unsupported)
                    isRunning -> stringResource(R.string.lan_share_status_running)
                    isEnabled -> stringResource(R.string.lan_share_status_starting)
                    else -> stringResource(R.string.lan_share_status_stopped)
                },
                color = NuvioTheme.colors.TextPrimary,
                fontWeight = FontWeight.SemiBold,
            )

            if (isRunning) {
                if (addresses.isEmpty()) {
                    Text(
                        text = stringResource(R.string.lan_share_waiting_network),
                        color = NuvioTheme.colors.TextSecondary,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.lan_share_addresses),
                        color = NuvioTheme.colors.TextSecondary,
                    )
                    addresses.forEach { address ->
                        Text(
                            text = "http://$address:${LanFileShareHttpServer.PORT}",
                            color = NuvioTheme.colors.TextPrimary,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    ShareDialogButton(
                        label = stringResource(R.string.lan_share_copy_address),
                        modifier = Modifier.focusRequester(primaryActionFocusRequester),
                        onClick = {
                            val urls = addresses.joinToString("\n") {
                                "http://$it:${LanFileShareHttpServer.PORT}"
                            }
                            context.copyToClipboard("JojoTV", urls)
                            context.showCopiedToast()
                        },
                    )
                }

                Text(
                    text = "${stringResource(R.string.lan_share_username)} : ${LanFileShareHttpServer.BASIC_USERNAME}",
                    color = NuvioTheme.colors.TextSecondary,
                )
                Text(
                    text = "${stringResource(R.string.lan_share_access_code)} : ${LanFileShareService.accessCode(context)}",
                    color = NuvioTheme.colors.TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                ShareDialogButton(
                    label = stringResource(R.string.lan_share_copy_code),
                    onClick = {
                        context.copyToClipboard(
                            "JojoTV",
                            LanFileShareService.accessCode(context),
                        )
                        context.showCopiedToast()
                    },
                )
                Text(
                    text = stringResource(R.string.lan_share_file_actions),
                    color = NuvioTheme.colors.TextSecondary,
                )
            }

            startError?.let { error ->
                Text(
                    text = stringResource(R.string.lan_share_error, error),
                    color = NuvioTheme.colors.TextPrimary,
                )
            }
            permissionError?.let { error ->
                Text(
                    text = error,
                    color = NuvioTheme.colors.TextPrimary,
                )
            }

            Text(
                text = stringResource(R.string.lan_share_persistent),
                color = NuvioTheme.colors.TextSecondary,
            )

            ShareDialogButton(
                label = stringResource(
                    if (isEnabled) R.string.lan_share_disable else R.string.lan_share_enable,
                ),
                modifier = if (focusCopyAddress) Modifier else Modifier.focusRequester(primaryActionFocusRequester),
                enabled = isSupported,
                onClick = {
                    if (isEnabled) {
                        LanFileShareService.setEnabled(context, false)
                    } else {
                        enableSharing()
                    }
                    isEnabled = LanFileShareService.isEnabled(context)
                    isRunning = LanFileShareService.isRunning()
                    startError = LanFileShareService.startError()
                },
            )
            ShareDialogButton(
                label = stringResource(R.string.lan_share_close),
                onClick = onDismiss,
            )
        }
    }
}

@Composable
private fun ShareDialogButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(label)
    }
}

private fun Context.copyToClipboard(label: String, value: String) {
    (getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)
        ?.setPrimaryClip(ClipData.newPlainText(label, value))
}

private fun Context.showCopiedToast() {
    Toast.makeText(this, getString(R.string.lan_share_copied), Toast.LENGTH_SHORT).show()
}
