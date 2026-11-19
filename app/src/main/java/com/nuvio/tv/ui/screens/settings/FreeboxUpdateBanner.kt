@file:OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class)

package com.nuvio.tv.ui.screens.settings

import android.text.format.Formatter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.IconButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.ui.theme.NuvioTheme

@Composable
internal fun FreeboxUpdateBannerHost(
    state: FreeboxUpdateUiState,
    visible: Boolean,
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
    onInstall: () -> Unit,
    content: @Composable () -> Unit,
) {
    var showUpdateDetails by remember { mutableStateOf(false) }
    val hasUpdate = visible && state.showBanner && state.candidate != null

    Column(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = hasUpdate,
            enter = expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(durationMillis = 300),
            ) + fadeIn(animationSpec = tween(durationMillis = 180)),
            exit = shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(durationMillis = 240),
            ) + fadeOut(animationSpec = tween(durationMillis = 150)),
        ) {
            state.candidate?.let { candidate ->
                FreeboxUpdateBanner(
                    state = state,
                    version = candidate.version,
                    sizeBytes = candidate.entry.sizeBytes,
                    onShowDetails = { showUpdateDetails = true },
                    onDownload = onDownload,
                    onInstall = onInstall,
                    onDismiss = onDismiss,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            content()
        }
    }

    if (showUpdateDetails) {
        FreeboxUpdateDialog(
            state = state,
            onDismiss = { showUpdateDetails = false },
            onDownload = onDownload,
            onInstall = onInstall,
        )
    }
}

@Composable
private fun FreeboxUpdateBanner(
    state: FreeboxUpdateUiState,
    version: String,
    sizeBytes: Long,
    onShowDetails: () -> Unit,
    onDownload: () -> Unit,
    onInstall: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val baseSubtitle = when (state.status) {
        FreeboxUpdateStatus.CHECKING -> stringResource(R.string.about_freebox_update_checking)
        FreeboxUpdateStatus.DOWNLOADING -> stringResource(R.string.about_freebox_update_downloading)
        FreeboxUpdateStatus.READY_TO_INSTALL -> stringResource(R.string.update_download_complete)
        FreeboxUpdateStatus.ERROR -> stringResource(
            R.string.about_freebox_update_error,
            state.errorMessage.orEmpty(),
        )
        else -> stringResource(R.string.about_freebox_update_banner_available)
    }
    val subtitle = if (state.status == FreeboxUpdateStatus.DOWNLOADING) {
        state.downloadProgressPercent?.let { "$baseSubtitle ($it%)" } ?: baseSubtitle
    } else {
        baseSubtitle
    }
    val versionLabel = listOfNotNull(
        version,
        sizeBytes.takeIf { it > 0L }?.let { Formatter.formatShortFileSize(context, it) },
    ).joinToString(" • ")
    val bannerBackgroundColor = NuvioTheme.colors.BackgroundElevated
    val bannerBorderColor = NuvioTheme.colors.Border

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(bannerBackgroundColor)
                drawRect(
                    color = bannerBorderColor,
                    topLeft = Offset(0f, size.height - 1.dp.toPx()),
                    size = Size(size.width, 1.dp.toPx()),
                )
            }
            .heightIn(min = 76.dp)
            .padding(horizontal = 32.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = Icons.Default.CloudDownload,
            contentDescription = null,
            tint = NuvioTheme.colors.TextPrimary,
            modifier = Modifier.size(28.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = versionLabel,
                style = MaterialTheme.typography.titleMedium.copy(textDirection = TextDirection.Ltr),
                color = NuvioTheme.colors.TextPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (state.status == FreeboxUpdateStatus.ERROR) {
                    NuvioTheme.colors.Error
                } else {
                    NuvioTheme.colors.TextSecondary
                },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        FreeboxUpdateBannerIconButton(
            icon = Icons.Default.Info,
            contentDescription = stringResource(R.string.about_freebox_update_dialog_title),
            onClick = onShowDetails,
        )

        Button(
            onClick = when (state.status) {
                FreeboxUpdateStatus.READY_TO_INSTALL -> onInstall
                FreeboxUpdateStatus.CHECKING,
                FreeboxUpdateStatus.DOWNLOADING -> ({})
                else -> onDownload
            },
            enabled = state.status != FreeboxUpdateStatus.CHECKING &&
                state.status != FreeboxUpdateStatus.DOWNLOADING,
            colors = ButtonDefaults.colors(
                containerColor = NuvioTheme.colors.Secondary,
                focusedContainerColor = NuvioTheme.colors.SecondaryVariant,
                contentColor = NuvioTheme.colors.OnSecondary,
                focusedContentColor = NuvioTheme.colors.OnSecondaryVariant,
                disabledContainerColor = NuvioTheme.colors.Secondary,
                disabledContentColor = NuvioTheme.colors.OnSecondary,
            ),
            shape = ButtonDefaults.shape(RoundedCornerShape(50)),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
        ) {
            Text(
                text = when (state.status) {
                    FreeboxUpdateStatus.CHECKING -> stringResource(R.string.about_freebox_update_checking)
                    FreeboxUpdateStatus.READY_TO_INSTALL -> stringResource(R.string.about_freebox_update_install)
                    FreeboxUpdateStatus.DOWNLOADING -> state.downloadProgressPercent?.let { percent ->
                        "${stringResource(R.string.update_downloading_ellipsis)} $percent%"
                    } ?: stringResource(R.string.update_downloading_ellipsis)
                    FreeboxUpdateStatus.ERROR -> stringResource(R.string.about_freebox_update_retry)
                    else -> stringResource(R.string.about_freebox_update_download)
                },
                fontWeight = FontWeight.SemiBold,
            )
        }

        if (state.status != FreeboxUpdateStatus.CHECKING &&
            state.status != FreeboxUpdateStatus.DOWNLOADING
        ) {
            FreeboxUpdateBannerIconButton(
                icon = Icons.Default.Close,
                contentDescription = stringResource(R.string.about_freebox_update_close),
                onClick = onDismiss,
            )
        }
    }
}

@Composable
private fun FreeboxUpdateBannerIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(44.dp),
        colors = IconButtonDefaults.colors(
            containerColor = NuvioTheme.colors.BackgroundCard,
            focusedContainerColor = NuvioTheme.colors.Secondary,
            contentColor = NuvioTheme.colors.TextPrimary,
            focusedContentColor = NuvioTheme.colors.OnSecondary,
        ),
        border = IconButtonDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(2.dp, NuvioTheme.colors.FocusRing),
                shape = CircleShape,
            ),
        ),
        shape = IconButtonDefaults.shape(shape = CircleShape),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(22.dp),
        )
    }
}
