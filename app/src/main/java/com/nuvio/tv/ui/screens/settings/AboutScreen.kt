@file:OptIn(ExperimentalTvMaterial3Api::class, ExperimentalFoundationApi::class)

package com.nuvio.tv.ui.screens.settings

import com.nuvio.tv.ui.theme.NuvioTheme

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.BuildConfig
import com.nuvio.tv.R
import com.nuvio.tv.ui.components.NuvioButton as Button
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.navigation.installServerApk
import com.nuvio.tv.ui.navigation.showApkInstallResult
import java.io.File

@Composable
fun AboutScreen(
    onNavigateToLicensesAttributions: () -> Unit = {},
    onBackPress: () -> Unit = {}
) {
    BackHandler { onBackPress() }

    SettingsStandaloneScaffold(
        title = stringResource(R.string.about_title),
        subtitle = stringResource(R.string.about_subtitle)
    ) {
        AboutSettingsContent(
            onNavigateToLicensesAttributions = onNavigateToLicensesAttributions
        )
    }
}

@Composable
fun AboutSettingsContent(
    onNavigateToLicensesAttributions: () -> Unit = {},
    initialFocusRequester: FocusRequester? = null
) {
    val context = LocalContext.current
    val updateViewModel: FreeboxUpdateViewModel = hiltViewModel(context as ComponentActivity)
    val updateState by updateViewModel.state.collectAsStateWithLifecycle()
    var showUpdateDialog by remember { mutableStateOf(false) }
    var pendingApkName by rememberSaveable { mutableStateOf<String?>(null) }
    val apkInstallLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val apkName = pendingApkName ?: "l’APK"
        pendingApkName = null
        showApkInstallResult(context, apkName, result.resultCode)
    }
    val aboutScrollState = rememberScrollState()
    var firstRowFocused by remember { mutableStateOf(false) }
    val firstRowModifier = Modifier.onFocusChanged { firstRowFocused = it.isFocused }
    val defaultBringIntoViewSpec = LocalBringIntoViewSpec.current
    val aboutBringIntoViewSpec = remember(aboutScrollState, defaultBringIntoViewSpec) {
        @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
        object : BringIntoViewSpec {
            override val scrollAnimationSpec = defaultBringIntoViewSpec.scrollAnimationSpec

            override fun calculateScrollDistance(offset: Float, size: Float, containerSize: Float): Float {
                return if (firstRowFocused && offset + aboutScrollState.value + size <= containerSize) {
                    -aboutScrollState.value.toFloat()
                } else {
                    defaultBringIntoViewSpec.calculateScrollDistance(offset, size, containerSize)
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SettingsDetailHeader(
            title = stringResource(R.string.about_title),
            subtitle = stringResource(R.string.about_subtitle)
        )

        SettingsGroupCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            title = null
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                CompositionLocalProvider(LocalBringIntoViewSpec provides aboutBringIntoViewSpec) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(aboutScrollState),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CompositionLocalProvider(LocalBringIntoViewSpec provides defaultBringIntoViewSpec) {
                            Spacer(modifier = Modifier.height(NuvioTheme.spacing.xs))

                            Text(
                                text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                                style = MaterialTheme.typography.labelSmall,
                                color = NuvioTheme.colors.TextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(NuvioTheme.spacing.xxs))

                            SettingsActionRow(
                                title = stringResource(R.string.about_check_updates),
                                subtitle = stringResource(R.string.about_check_updates_subtitle),
                                trailingIcon = Icons.Default.OpenInNew,
                                modifier = firstRowModifier.then(
                                    if (initialFocusRequester != null) Modifier.focusRequester(initialFocusRequester)
                                    else Modifier
                                ),
                                onClick = {
                                    showUpdateDialog = true
                                    updateViewModel.checkForUpdates(force = true)
                                }
                            )

                            SettingsActionRow(
                                title = stringResource(R.string.about_privacy_policy),
                                subtitle = stringResource(R.string.about_privacy_policy_subtitle),
                                trailingIcon = Icons.Default.OpenInNew,
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://nuvio.tv/privacy-policy")
                                    )
                                    context.startActivity(intent)
                                }
                            )

                            SettingsActionRow(
                                title = stringResource(R.string.about_licenses_attributions),
                                subtitle = stringResource(R.string.about_licenses_attributions_subtitle),
                                trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                onClick = onNavigateToLicensesAttributions
                            )
                        }
                    }
                }
                SettingsVerticalScrollIndicators(state = aboutScrollState)
            }
        }
    }

    if (showUpdateDialog) {
        FreeboxUpdateDialog(
            state = updateState,
            onDismiss = { showUpdateDialog = false },
            onDownload = updateViewModel::downloadUpdate,
            onInstall = {
                updateState.downloadedApk?.let { apk ->
                    pendingApkName = apk.name
                    if (!installFreeboxUpdateApk(context, apk) { intent ->
                            apkInstallLauncher.launch(intent)
                        }
                    ) {
                        pendingApkName = null
                    }
                }
            },
        )
    }
}

@Composable
internal fun FreeboxUpdateDialog(
    state: FreeboxUpdateUiState,
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
    onInstall: () -> Unit,
) {
    val subtitle = when (state.status) {
        FreeboxUpdateStatus.IDLE -> stringResource(R.string.about_freebox_update_checking)
        FreeboxUpdateStatus.CHECKING -> stringResource(R.string.about_freebox_update_checking)
        FreeboxUpdateStatus.NOT_CONFIGURED -> stringResource(R.string.about_freebox_update_not_configured)
        FreeboxUpdateStatus.UP_TO_DATE -> stringResource(R.string.about_freebox_update_up_to_date)
        FreeboxUpdateStatus.AVAILABLE -> state.candidate?.let { candidate ->
            stringResource(
                R.string.about_freebox_update_available,
                BuildConfig.VERSION_NAME,
                candidate.version,
            )
        } ?: stringResource(R.string.about_freebox_update_checking)
        FreeboxUpdateStatus.DOWNLOADING -> state.downloadProgressPercent?.let { percent ->
            "${stringResource(R.string.about_freebox_update_downloading)} ($percent%)"
        } ?: stringResource(R.string.about_freebox_update_downloading)
        FreeboxUpdateStatus.READY_TO_INSTALL -> stringResource(R.string.about_freebox_update_ready)
        FreeboxUpdateStatus.ERROR -> stringResource(
            R.string.about_freebox_update_error,
            state.errorMessage.orEmpty(),
        )
    }

    NuvioDialog(
        onDismiss = onDismiss,
        title = stringResource(R.string.about_freebox_update_dialog_title),
        subtitle = subtitle,
        suppressFirstKeyUp = false,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.xs)) {
            when (state.status) {
                FreeboxUpdateStatus.AVAILABLE -> Button(
                    onClick = onDownload,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.about_freebox_update_download))
                }
                FreeboxUpdateStatus.READY_TO_INSTALL -> Button(
                    onClick = onInstall,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.about_freebox_update_install))
                }
                FreeboxUpdateStatus.ERROR -> if (state.candidate != null) {
                    Button(
                        onClick = onDownload,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.about_freebox_update_retry))
                    }
                }
                else -> Unit
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.colors(
                    containerColor = NuvioTheme.colors.BackgroundCard,
                    contentColor = NuvioTheme.colors.TextPrimary,
                ),
            ) {
                Text(stringResource(R.string.about_freebox_update_close))
            }
        }
    }
}

internal fun installFreeboxUpdateApk(
    context: Context,
    apk: File,
    launchInstaller: (Intent) -> Unit,
): Boolean = runCatching {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apk,
        )
        installServerApk(context, apk.name, uri, launchInstaller)
    }.onFailure { error ->
        Toast.makeText(
            context,
            error.localizedMessage ?: context.getString(R.string.about_freebox_update_install_error),
            Toast.LENGTH_LONG,
        ).show()
    }
        .getOrDefault(false)
