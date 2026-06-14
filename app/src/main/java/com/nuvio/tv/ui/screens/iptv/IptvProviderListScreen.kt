package com.nuvio.tv.ui.screens.iptv

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.streamvault.domain.model.Provider
import com.streamvault.domain.model.ProviderStatus
import com.streamvault.domain.model.ProviderType
import com.streamvault.domain.model.SyncState
import com.nuvio.tv.ui.theme.NuvioColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.activity.compose.BackHandler

@Composable
fun IptvProviderListScreen(
    showBuiltInHeader: Boolean = true,
    onNavigateToSetup: () -> Unit,
    onNavigateToEdit: (Long, String) -> Unit = { _, _ -> },
    onNavigateToProvider: (Long, String) -> Unit,
    onBack: () -> Unit = {},
    viewModel: IptvHomeViewModel = hiltViewModel()
) {
    val providers by viewModel.providers.collectAsStateWithLifecycle()
    val syncStates by viewModel.syncStates.collectAsStateWithLifecycle()
    val providerCounts by viewModel.providerCounts.collectAsStateWithLifecycle()

    BackHandler { onBack() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NuvioColors.Background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = if (showBuiltInHeader) 48.dp else 24.dp,
                bottom = 32.dp,
                start = 48.dp,
                end = 48.dp
            )
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LiveTv,
                        contentDescription = null,
                        tint = NuvioColors.TextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "IPTV",
                        color = NuvioColors.TextPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                AddProviderButton(onClick = onNavigateToSetup)
                Spacer(Modifier.height(24.dp))
            }

            if (providers.isNotEmpty()) {
                item {
                    Text(
                        text = "Mes sources",
                        color = NuvioColors.TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            items(providers, key = { it.id }) { provider ->
                val syncState = syncStates[provider.id]
                val counts = providerCounts[provider.id]
                ProviderCard(
                    provider = provider,
                    syncState = syncState,
                    onSync = { viewModel.syncProvider(provider.id) },
                    onEdit = { onNavigateToProvider(provider.id, provider.name) },
                    onEditSettings = { onNavigateToEdit(provider.id, provider.type.name.lowercase().let {
                        when { it.contains("xtream") -> "xtream"; it.contains("m3u") -> "m3u"; else -> "stalker" }
                    }) },
                    onDelete = { viewModel.deleteProvider(provider.id) }
                )
                Spacer(Modifier.height(8.dp))
            }

            if (providers.isEmpty()) {
                item { EmptyProvidersHint() }
            }
        }
    }
}

@Composable
private fun AddProviderButton(onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(16.dp)
    val bgColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.FocusBackground else NuvioColors.BackgroundCard,
        animationSpec = tween(150), label = "addBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.Primary else NuvioColors.Border,
        animationSpec = tween(150), label = "addBorder"
    )
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.98f),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Add, null,
                tint = if (isFocused) NuvioColors.Primary else NuvioColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Ajouter une source IPTV",
                color = if (isFocused) NuvioColors.TextPrimary else NuvioColors.TextSecondary,
                fontSize = 14.sp, fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ProviderCard(
    provider: Provider,
    syncState: SyncState?,
    counts: ProviderCounts? = null,
    onSync: () -> Unit,
    onEdit: () -> Unit,
    onEditSettings: () -> Unit = {},
    onDelete: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(NuvioColors.BackgroundCard)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Zone cliquable principale (ouvre le provider)
            Card(
                onClick = onEdit,
                modifier = Modifier.weight(1f),
                shape = CardDefaults.shape(RoundedCornerShape(10.dp)),
                colors = CardDefaults.colors(containerColor = Color.Transparent, focusedContainerColor = NuvioColors.Secondary),
                scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.98f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProviderStatusIcon(provider, syncState, Modifier.size(22.dp))
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            provider.name,
                            color = NuvioColors.TextPrimary,
                            fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(3.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            ProviderTypeBadge(provider.type)
                            Text(
                                providerStatusLabel(provider.status, syncState),
                                color = providerStatusColor(provider.status, syncState),
                                fontSize = 11.sp
                            )
                            provider.expirationDate?.let { exp ->
                                val formatted = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(exp))
                                val color = if (exp < System.currentTimeMillis()) androidx.compose.ui.graphics.Color(0xFFEF5350) else NuvioColors.TextTertiary
                                Text("• Exp. $formatted", color = color, fontSize = 11.sp)
                            }
                        }
                        if (syncState is SyncState.Syncing && syncState.phase.isNotBlank()) {
                            Spacer(Modifier.height(2.dp))
                            Text(syncState.phase, color = NuvioColors.TextTertiary, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        counts?.let { c ->
                            if (c.channels > 0 || c.movies > 0 || c.series > 0) {
                                Spacer(Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    if (c.channels > 0) Text("?? ${c.channels} cha�nes", color = NuvioColors.TextTertiary, fontSize = 10.sp)
                                    if (c.movies > 0) Text("?? ${c.movies} films", color = NuvioColors.TextTertiary, fontSize = 10.sp)
                                    if (c.series > 0) Text("?? ${c.series} s�ries", color = NuvioColors.TextTertiary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ActionIconButton(Icons.Default.Sync, "Sync", enabled = syncState !is SyncState.Syncing, onClick = onSync)
                ActionIconButton(Icons.Default.Edit, "Modifier", onClick = onEditSettings)
                ActionIconButton(Icons.Default.Delete, "Supprimer", tint = NuvioColors.Error, onClick = onDelete)
            }
        }
    }
}

@Composable
private fun ProviderStatusIcon(provider: Provider, syncState: SyncState?, modifier: Modifier = Modifier) {
    val (icon, tint) = when {
        syncState is SyncState.Syncing -> Icons.Default.Refresh to NuvioColors.Primary
        syncState is SyncState.Error -> Icons.Default.Error to NuvioColors.Error
        provider.status == ProviderStatus.ACTIVE -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
        provider.status == ProviderStatus.ERROR -> Icons.Default.Error to NuvioColors.Error
        else -> Icons.Default.LiveTv to NuvioColors.TextTertiary
    }
    Icon(icon, null, tint = tint, modifier = modifier)
}

@Composable
private fun ProviderTypeBadge(type: ProviderType) {
    val label = when (type) {
        ProviderType.XTREAM_CODES -> "Xtream"
        ProviderType.M3U -> "M3U"
        ProviderType.STALKER_PORTAL -> "Stalker"
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(NuvioColors.BackgroundElevated)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(label, color = NuvioColors.TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ActionIconButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean = true,
    tint: Color = NuvioColors.TextSecondary,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.FocusBackground else Color.Transparent,
        animationSpec = tween(120), label = "actionBg"
    )
    Card(
        onClick = onClick,
        modifier = Modifier.size(36.dp).onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(RoundedCornerShape(8.dp)),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.9f),
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(icon, label, tint = if (enabled) tint else NuvioColors.TextTertiary, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun EmptyProvidersHint() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(Icons.Default.LiveTv, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(48.dp))
        Text("Aucune source IPTV configurée", color = NuvioColors.TextSecondary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Text(
            "Ajoutez une source Xtream Codes, M3U ou Stalker\npour commencer a regarder.",
            color = NuvioColors.TextTertiary, fontSize = 12.sp, lineHeight = 18.sp
        )
    }
}

private fun providerStatusLabel(status: ProviderStatus, syncState: SyncState?): String = when {
    syncState is SyncState.Syncing -> "Sync en cours..."
    syncState is SyncState.Error -> "Erreur sync"
    syncState is SyncState.Success -> "Synchronise"
    status == ProviderStatus.ACTIVE -> "Actif"
    status == ProviderStatus.PARTIAL -> "Partiel"
    status == ProviderStatus.EXPIRED -> "Expire"
    status == ProviderStatus.ERROR -> "Erreur"
    status == ProviderStatus.DISABLED -> "Desactive"
    else -> "Inconnu"
}

private fun providerStatusColor(status: ProviderStatus, syncState: SyncState?): Color = when {
    syncState is SyncState.Syncing -> Color(0xFF64B5F6)
    syncState is SyncState.Error -> Color(0xFFEF5350)
    syncState is SyncState.Success -> Color(0xFF81C784)
    status == ProviderStatus.ACTIVE -> Color(0xFF81C784)
    status == ProviderStatus.PARTIAL -> Color(0xFFFFB74D)
    status == ProviderStatus.EXPIRED || status == ProviderStatus.ERROR -> Color(0xFFEF5350)
    else -> Color(0xFF9E9E9E)
}

@Composable
private fun ActionButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)
    val bgColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.FocusBackground else NuvioColors.BackgroundCard,
        animationSpec = tween(150), label = "actBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.Primary else NuvioColors.Border,
        animationSpec = tween(150), label = "actBorder"
    )
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp).onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.97f),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = if (isFocused) NuvioColors.Primary else NuvioColors.TextSecondary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, color = if (isFocused) NuvioColors.TextPrimary else NuvioColors.TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
