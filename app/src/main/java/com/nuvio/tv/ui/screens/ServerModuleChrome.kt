package com.nuvio.tv.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Storage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.data.storage.StorageCapacity
import com.nuvio.tv.ui.theme.NuvioTheme

internal data class ServerModuleNavEntry(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
internal fun ServerModuleScaffold(
    navEntries: List<ServerModuleNavEntry>,
    selectedNavId: String,
    storageTitle: String,
    storageText: String?,
    storageCapacity: StorageCapacity? = null,
    onNavigateHome: () -> Unit,
    sidebarDetailsContent: @Composable ColumnScope.() -> Unit,
    mainContent: @Composable BoxScope.() -> Unit,
) {
    val panelShape = NuvioTheme.configuredShape(RoundedCornerShape(18.dp))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NuvioTheme.colors.Background)
            .padding(start = 24.dp, top = 80.dp, end = 24.dp, bottom = 24.dp),
    ) {
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier
                    .width(210.dp)
                    .fillMaxHeight()
                    .clip(panelShape)
                    .background(NuvioTheme.colors.BackgroundElevated)
                    .border(BorderStroke(1.dp, NuvioTheme.colors.SurfaceVariant), panelShape)
                    .padding(12.dp),
            ) {
                ServerModuleNavCard(
                    label = "Accueil",
                    icon = Icons.Default.Home,
                    selected = selectedNavId == "home",
                    onClick = onNavigateHome,
                )
                Spacer(Modifier.height(18.dp))
                Text(
                    text = "EMPLACEMENTS",
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = NuvioTheme.colors.TextTertiary,
                    fontWeight = FontWeight.SemiBold,
                )
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    navEntries.forEach { entry ->
                        ServerModuleNavCard(
                            label = entry.label,
                            icon = entry.icon,
                            selected = selectedNavId == entry.id,
                            onClick = entry.onClick,
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(NuvioTheme.configuredShape(RoundedCornerShape(12.dp)))
                        .background(NuvioTheme.colors.BackgroundCard)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    content = sidebarDetailsContent,
                )
                if (storageText != null || storageCapacity != null) {
                    Spacer(Modifier.height(4.dp))
                    val storageShape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(storageShape)
                            .background(NuvioTheme.colors.BackgroundCard)
                            .border(BorderStroke(1.dp, NuvioTheme.colors.SurfaceVariant), storageShape)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(9.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(NuvioTheme.configuredShape(RoundedCornerShape(10.dp)))
                                    .background(NuvioTheme.colors.Secondary.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = NuvioTheme.colors.Secondary,
                                    modifier = Modifier.size(21.dp),
                                )
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = storageTitle,
                                    color = NuvioTheme.colors.TextPrimary,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = storageText.orEmpty(),
                                    color = NuvioTheme.colors.TextSecondary,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        val progress = storageCapacity?.let { capacity ->
                            capacity.totalBytes.takeIf { it > 0L }?.let { total ->
                                ((total - capacity.freeBytes.coerceIn(0L, total)).toDouble() / total)
                                    .toFloat()
                                    .coerceIn(0f, 1f)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(NuvioTheme.colors.SurfaceVariant),
                        ) {
                            if (progress != null && progress > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(progress)
                                        .background(NuvioTheme.colors.Secondary),
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(panelShape)
                    .background(NuvioTheme.colors.BackgroundCard)
                    .border(BorderStroke(1.dp, NuvioTheme.colors.SurfaceVariant), panelShape)
                    .padding(14.dp),
                content = mainContent,
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ServerModuleNavCard(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(46.dp).onFocusChanged { focused = it.isFocused },
        colors = CardDefaults.colors(
            containerColor = if (selected) NuvioTheme.colors.Secondary.copy(alpha = 0.16f) else NuvioTheme.colors.BackgroundElevated,
            focusedContainerColor = NuvioTheme.colors.FocusBackground,
        ),
        border = CardDefaults.border(
            border = if (selected) Border(BorderStroke(1.dp, NuvioTheme.colors.Secondary.copy(alpha = 0.62f))) else Border.None,
            focusedBorder = Border(BorderStroke(2.dp, NuvioTheme.colors.FocusRing)),
        ),
        shape = CardDefaults.shape(shape = shape),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected || focused) NuvioTheme.colors.Secondary else NuvioTheme.colors.TextSecondary,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = label,
                color = if (selected || focused) NuvioTheme.colors.TextPrimary else NuvioTheme.colors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
internal fun ServerModuleActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
) {
    val shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))
    Card(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        colors = CardDefaults.colors(
            containerColor = if (emphasized) NuvioTheme.colors.Secondary else NuvioTheme.colors.Secondary.copy(alpha = 0.12f),
            focusedContainerColor = if (emphasized) NuvioTheme.colors.SecondaryVariant else NuvioTheme.colors.FocusBackground,
        ),
        border = CardDefaults.border(focusedBorder = Border(BorderStroke(2.dp, NuvioTheme.colors.FocusRing))),
        shape = CardDefaults.shape(shape = shape),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (emphasized) NuvioTheme.colors.OnSecondary else NuvioTheme.colors.TextPrimary,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = label,
                color = if (emphasized) NuvioTheme.colors.OnSecondary else NuvioTheme.colors.TextPrimary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun ServerModuleInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = NuvioTheme.colors.TextSecondary,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
        )
        Text(
            text = value,
            modifier = Modifier.weight(1.1f),
            color = NuvioTheme.colors.TextPrimary,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
