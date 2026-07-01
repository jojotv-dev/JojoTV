package com.nuvio.tv.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.nuvio.tv.domain.model.IptvPosterSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.*
import com.nuvio.tv.ui.theme.NuvioColors

@Composable
fun IptvSettingsScreen(
    onNavigateToProviderSetup: () -> Unit = {},
    onNavigateToProviderList: () -> Unit = {},
    onNavigateToSchedule: () -> Unit = {},
    onNavigateToDns: () -> Unit = {},
    onNavigateToEpg: () -> Unit = {},
    viewModel: IptvSettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val vodPosterSize by viewModel.vodPosterSize.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Section : Sidebar ────────────────────────────────────────────
        item {
            IptvSettingsSectionTitle("Barre latérale")
        }
        item {
            SettingsToggleRow(
                title = "Afficher Live TV",
                subtitle = "Accès rapide Live TV dans la sidebar",
                checked = settings.showLiveTvInSidebar,
                onToggle = { viewModel.setShowLiveTv(!settings.showLiveTvInSidebar) }
            )
        }
        item {
            SettingsToggleRow(
                title = "Afficher Films",
                subtitle = "Accès rapide Films dans la sidebar",
                checked = settings.showMoviesInSidebar,
                onToggle = { viewModel.setShowMovies(!settings.showMoviesInSidebar) }
            )
        }
        item {
            SettingsToggleRow(
                title = "Afficher Séries",
                subtitle = "Accès rapide Séries dans la sidebar",
                checked = settings.showSeriesInSidebar,
                onToggle = { viewModel.setShowSeries(!settings.showSeriesInSidebar) }
            )
        }
        item {
            SettingsToggleRow(
                title = "Afficher Enregistrements",
                subtitle = "Accès rapide enregistrements dans la sidebar",
                checked = settings.showRecordingsInSidebar,
                onToggle = { viewModel.setShowRecordings(!settings.showRecordingsInSidebar) }
            )
        }

        // ── Section : Affichage ──────────────────────────────────────────
        item {
            Spacer(Modifier.height(8.dp))
            IptvSettingsSectionTitle("Affichage")
        }
        item {
            IptvPosterSizeSettingRow(
                currentSize = vodPosterSize,
                onSizeSelected = { size -> viewModel.setVodPosterSize(size) }
            )
        }

        // ── Section : Modules ────────────────────────────────────────────
        item {
            Spacer(Modifier.height(8.dp))
            IptvSettingsSectionTitle("Modules")
        }
        item {
            IptvNavTile(
                icon = Icons.Default.FolderOpen,
                title = "G\u00e9rer les providers",
                subtitle = "Modifier ou supprimer les listes de lecture existantes",
                onClick = onNavigateToProviderList
            )
        }
        item {
            IptvNavTile(
                icon = Icons.Default.Add,
                title = "Ajouter une liste de lecture",
                subtitle = "Configurer un provider IPTV (Xtream, M3U, Stalker)",
                onClick = onNavigateToProviderSetup
            )
        }
        item {
            IptvNavTile(
                icon = Icons.Default.Schedule,
                title = "Programmation",
                subtitle = "Planifier et gérer les enregistrements",
                onClick = onNavigateToSchedule
            )
        }
        item {
            IptvNavTile(
                icon = Icons.Default.Storage,
                title = "DNS",
                subtitle = "Configurer le serveur DNS IPTV",
                onClick = onNavigateToDns
            )
        }
        item {
            IptvNavTile(
                icon = Icons.Default.Tv,
                title = "Guide des programmes (EPG)",
                subtitle = "Configurer et synchroniser le guide électronique",
                onClick = onNavigateToEpg
            )
        }
    }
}

// ── Titre de section ─────────────────────────────────────────────────────────

@Composable
private fun IptvSettingsSectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = NuvioColors.Primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

// ── Reglage taille des vignettes IPTV ─────────────────────────────────────────

@Composable
private fun IptvPosterSizeSettingRow(
    currentSize: IptvPosterSize,
    onSizeSelected: (IptvPosterSize) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Taille des vignettes",
            style = MaterialTheme.typography.labelLarge,
            color = NuvioColors.TextSecondary
        )
        LazyRow(
            contentPadding = PaddingValues(end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = IptvPosterSize.entries,
                key = { it.name }
            ) { size ->
                val label = when (size) {
                    IptvPosterSize.VERY_SMALL -> "Tres petite"
                    IptvPosterSize.SMALL -> "Petite"
                    IptvPosterSize.MEDIUM -> "Moyenne"
                    IptvPosterSize.LARGE -> "Grande"
                    IptvPosterSize.VERY_LARGE -> "Tres grande"
                }
                SettingsChoiceChip(
                    label = label,
                    selected = size == currentSize,
                    onClick = { onSizeSelected(size) }
                )
            }
        }
    }
}

// ── Tuile de navigation ───────────────────────────────────────────────────────

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun IptvNavTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { state -> isFocused = state.isFocused },
        colors = CardDefaults.colors(
            containerColor = NuvioColors.Background,
            focusedContainerColor = NuvioColors.Background
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(2.dp, NuvioColors.FocusRing),
                shape = RoundedCornerShape(SettingsPillRadius)
            )
        ),
        shape = CardDefaults.shape(RoundedCornerShape(SettingsPillRadius)),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 62.dp)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NuvioColors.TextPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = NuvioColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = NuvioColors.TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = NuvioColors.TextTertiary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
