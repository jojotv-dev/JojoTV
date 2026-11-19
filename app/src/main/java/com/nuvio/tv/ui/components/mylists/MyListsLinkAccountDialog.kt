@file:OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class)

package com.nuvio.tv.ui.components.mylists

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.tv.material3.Border
import androidx.tv.material3.Button as TvMaterialButton
import com.nuvio.tv.ui.components.NuvioButton as Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.core.mylists.MyListsDefaultSeed
import com.nuvio.tv.core.mylists.MyListsDeviceCodeResponse
import com.nuvio.tv.core.mylists.MyListsTvSyncManager
import com.nuvio.tv.core.qr.QrCodeGenerator
import com.nuvio.tv.reshaped.livetv.LiveTvRepository
import com.nuvio.tv.ui.theme.NuvioColors
import com.nuvio.tv.ui.theme.NuvioTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel
class MyListsLinkAccountViewModel @Inject constructor(
    val syncManager: MyListsTvSyncManager
) : ViewModel()

@Composable
fun MyListsLinkAccountDialog(
    onDismiss: () -> Unit,
    onAuthenticated: (() -> Unit)? = null,
    viewModel: MyListsLinkAccountViewModel = hiltViewModel()
) {
    val syncManager = viewModel.syncManager
    val targetId by syncManager.targetIdFlow.collectAsState(initial = MyListsTvSyncManager.DEFAULT_TARGET_ID)
    val isLinked = targetId.isNotBlank() && targetId != MyListsTvSyncManager.DEFAULT_TARGET_ID

    var forcePairingMode by remember { mutableStateOf(false) }
    var deviceCodeState by remember { mutableStateOf<MyListsDeviceCodeResponse?>(null) }
    var isLoadingCode by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableIntStateOf(0) }
    var statusMessage by remember { mutableIntStateOf(0) }
    var isSyncing by remember { mutableStateOf(false) }
    var requestAttempt by remember { mutableIntStateOf(0) }

    val scope = rememberCoroutineScope()
    val primaryFocusRequester = remember { FocusRequester() }

    val showPairingView = !isLinked || forcePairingMode

    // Request a fresh device code whenever pairing view is active
    LaunchedEffect(showPairingView, requestAttempt) {
        if (!showPairingView) return@LaunchedEffect
        isLoadingCode = true
        errorMessage = 0
        statusMessage = 0
        val codeResponse = syncManager.requestDeviceCode().getOrNull()
        isLoadingCode = false
        if (codeResponse != null) {
            deviceCodeState = codeResponse
        } else {
            errorMessage = R.string.my_lists_pairing_error
        }
    }

    // Poll every 3 seconds while pairing view is open and we have a deviceCode
    LaunchedEffect(showPairingView, deviceCodeState?.deviceCode) {
        val currentCode = deviceCodeState?.deviceCode ?: return@LaunchedEffect
        if (!showPairingView) return@LaunchedEffect
        while (isActive) {
            delay(3_000L)
            val pollResult = syncManager.pollDeviceCode(currentCode).getOrNull() ?: continue
            when (pollResult.status.orEmpty().lowercase()) {
                "authenticated", "complete", "linked", "authorized" -> {
                    val installId = pollResult.installId?.trim().orEmpty()
                    if (installId.isNotBlank()) {
                        statusMessage = R.string.my_lists_syncing
                        syncManager.linkAccount(
                            targetId = installId,
                            manifestUrl = pollResult.manifestUrl
                        )
                        LiveTvRepository.refresh()
                        forcePairingMode = false
                        onAuthenticated?.invoke()
                        onDismiss()
                        return@LaunchedEffect
                    }
                }
                "expired" -> {
                    deviceCodeState = null
                    errorMessage = R.string.my_lists_pairing_expired
                    return@LaunchedEffect
                }
            }
        }
    }

    LaunchedEffect(showPairingView, isLoadingCode, errorMessage) {
        runCatching { primaryFocusRequester.requestFocus() }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .width(720.dp)
                .clip(NuvioTheme.configuredShape(RoundedCornerShape(20.dp))),
            shape = NuvioTheme.configuredShape(RoundedCornerShape(20.dp)),
            colors = SurfaceDefaults.colors(containerColor = NuvioColors.BackgroundCard),
            border = Border(
                border = BorderStroke(1.dp, NuvioColors.Border),
                shape = NuvioTheme.configuredShape(RoundedCornerShape(20.dp))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = stringResource(R.string.my_lists_account_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = NuvioColors.TextPrimary
                )

                if (showPairingView) {
                    Text(
                        text = stringResource(R.string.my_lists_account_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = NuvioTealOrSecondary()
                    )

                    val currentCode = deviceCodeState
                    val qrBitmap = remember(currentCode?.verificationUrl) {
                        val url = currentCode?.verificationUrl
                            ?: "${MyListsDefaultSeed.MY_LISTS_BASE_ORIGIN}/configure"
                        runCatching { QrCodeGenerator.generate(url, 420, margin = 1) }.getOrNull()
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .clip(NuvioTheme.configuredShape(RoundedCornerShape(14.dp)))
                                .background(Color.White)
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (qrBitmap != null) {
                                Image(
                                    bitmap = qrBitmap.asImageBitmap(),
                                    contentDescription = stringResource(R.string.my_lists_pairing_qr_description),
                                    modifier = Modifier.size(180.dp)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (isLoadingCode) {
                                Text(
                                    text = stringResource(R.string.my_lists_pairing_generating),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = NuvioColors.TextPrimary
                                )
                            } else if (currentCode != null) {
                                Text(
                                    text = stringResource(R.string.my_lists_pairing_code),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = NuvioColors.TextSecondary
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(NuvioTheme.configuredShape(RoundedCornerShape(10.dp)))
                                        .background(NuvioColors.BackgroundElevated)
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = currentCode.userCode.orEmpty(),
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 3.sp
                                        ),
                                        color = NuvioColors.TextPrimary
                                    )
                                }
                                Text(
                                    text = currentCode.verificationUrl.orEmpty(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NuvioColors.TextSecondary
                                )
                                Text(
                                    text = stringResource(R.string.my_lists_pairing_waiting),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NuvioColors.Secondary
                                )
                            } else if (errorMessage != 0) {
                                Text(
                                    text = stringResource(errorMessage),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFFF6B6B)
                                )
                                Text(
                                    text = stringResource(
                                        R.string.my_lists_pairing_visit,
                                        "${MyListsDefaultSeed.MY_LISTS_BASE_ORIGIN}/configure",
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NuvioColors.TextSecondary
                                )
                            }

                            if (statusMessage != 0) {
                                Text(
                                    text = stringResource(statusMessage),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NuvioColors.Secondary
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { requestAttempt++ },
                            colors = ButtonDefaults.colors(
                                containerColor = NuvioColors.BackgroundElevated,
                                contentColor = NuvioColors.TextPrimary,
                                focusedContainerColor = NuvioColors.FocusBackground,
                                focusedContentColor = NuvioColors.TextPrimary
                            )
                        ) {
                            Text(stringResource(R.string.my_lists_new_code))
                        }

                        if (isLinked) {
                            Button(
                                onClick = { forcePairingMode = false },
                                colors = ButtonDefaults.colors(
                                    containerColor = NuvioColors.BackgroundElevated,
                                    contentColor = NuvioColors.TextPrimary,
                                    focusedContainerColor = NuvioColors.FocusBackground,
                                    focusedContentColor = NuvioColors.TextPrimary
                                )
                            ) {
                                Text(stringResource(R.string.action_back))
                            }
                        }

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.focusRequester(primaryFocusRequester),
                            colors = ButtonDefaults.colors(
                                containerColor = NuvioColors.BackgroundElevated,
                                contentColor = NuvioColors.TextPrimary,
                                focusedContainerColor = NuvioColors.FocusBackground,
                                focusedContentColor = NuvioColors.TextPrimary
                            )
                        ) {
                            Text(stringResource(R.string.action_close))
                        }
                    }
                } else {
                    // Linked Account Management View
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(NuvioTheme.configuredShape(RoundedCornerShape(12.dp)))
                            .background(NuvioColors.BackgroundElevated)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.my_lists_connected_account),
                            style = MaterialTheme.typography.labelMedium,
                            color = NuvioColors.Secondary
                        )
                        Text(
                            text = stringResource(R.string.my_lists_install_id, targetId),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = NuvioColors.TextPrimary
                        )
                        Text(
                            text = stringResource(R.string.my_lists_sync_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = NuvioColors.TextSecondary
                        )
                    }

                    if (statusMessage != 0) {
                        Text(
                            text = stringResource(statusMessage),
                            style = MaterialTheme.typography.bodySmall,
                            color = NuvioColors.Secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (!isSyncing) {
                                    isSyncing = true
                                    statusMessage = R.string.my_lists_syncing
                                    scope.launch {
                                        val ok = syncManager.syncIfNeeded(force = true)
                                        LiveTvRepository.refresh()
                                        isSyncing = false
                                        statusMessage = if (ok) {
                                            R.string.my_lists_sync_success
                                        } else {
                                            R.string.my_lists_sync_failed
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.focusRequester(primaryFocusRequester),
                            colors = ButtonDefaults.colors(
                                containerColor = NuvioColors.BackgroundElevated,
                                contentColor = NuvioColors.TextPrimary,
                                focusedContainerColor = NuvioColors.FocusBackground,
                                focusedContentColor = NuvioColors.TextPrimary
                            )
                        ) {
                            Text(stringResource(if (isSyncing) R.string.my_lists_syncing_short else R.string.my_lists_sync_now))
                        }

                        Button(
                            onClick = { forcePairingMode = true },
                            colors = ButtonDefaults.colors(
                                containerColor = NuvioColors.BackgroundElevated,
                                contentColor = NuvioColors.TextPrimary,
                                focusedContainerColor = NuvioColors.FocusBackground,
                                focusedContentColor = NuvioColors.TextPrimary
                            )
                        ) {
                            Text(stringResource(R.string.my_lists_switch_account))
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    syncManager.unlinkAccount()
                                    LiveTvRepository.refresh()
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.colors(
                                containerColor = NuvioColors.BackgroundElevated,
                                contentColor = Color(0xFFFF6B6B),
                                focusedContainerColor = NuvioColors.FocusBackground,
                                focusedContentColor = Color(0xFFFF6B6B)
                            )
                        ) {
                            Text(stringResource(R.string.my_lists_unlink))
                        }

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.colors(
                                containerColor = NuvioColors.BackgroundElevated,
                                contentColor = NuvioColors.TextPrimary,
                                focusedContainerColor = NuvioColors.FocusBackground,
                                focusedContentColor = NuvioColors.TextPrimary
                            )
                        ) {
                            Text(stringResource(R.string.action_close))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NuvioTealOrSecondary(): Color = NuvioTheme.extendedColors.textSecondary
