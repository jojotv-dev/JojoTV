package com.nuvio.tv.ui.screens.iptv.tivi

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.*
import coil3.compose.AsyncImage
import com.nuvio.tv.ui.theme.NuvioColors
import com.nuvio.tv.ui.screens.iptv.tivi.components.*

private val TabShape = RoundedCornerShape(10.dp)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun IptvTiviScreen(
    onBack: () -> Unit,
    initialTab: TiviTab = TiviTab.LIVE,
    viewModel: IptvTiviViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(initialTab) { viewModel.selectTab(initialTab) }
    BackHandler { onBack() }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(NuvioColors.Background),
    ) {

        // ── Colonne 1 : Provider Tree accordéon ─────────────────────────
        TiviProviderTree(
            providerNodes = uiState.providerNodes,
            selectedGroupId = uiState.selectedGroupId,
            onProviderClick = { viewModel.toggleProvider(it) },
            onGroupClick = { providerId, groupId -> viewModel.selectGroup(providerId, groupId) },
            modifier = Modifier.fillMaxHeight(),
        )

        // ── Séparateur vertical ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(NuvioColors.Border)
        )

        // ── Colonne 2 : Contenu dynamique selon le tab ───────────────────
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            when (val c = uiState.content) {
                is TiviContent.LiveContent -> {
                    if (uiState.selectedGroupId == null) {
                        TiviEmptyHint("Sélectionne un groupe Live TV")
                    } else {
                        Column(Modifier.fillMaxSize()) {
                            TiviTopPanel(
                                channel = c.focusedChannel,
                                currentProgram = c.currentProgram,
                                nextProgram = c.nextProgram,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Row(Modifier.weight(1f).fillMaxWidth()) {
                                TiviChannelList(
                                    channels = c.channels,
                                    focusedChannelId = c.focusedChannel?.id,
                                    onChannelFocused = { ch -> viewModel.focusChannel(ch.providerId, ch) },
                                    onChannelClick = { /* TODO player */ },
                                    modifier = Modifier.fillMaxHeight(),
                                )
                                TiviEpgGrid(
                                    epgRows = c.epgRows,
                                    focusedChannelId = c.focusedChannel?.id,
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                )
                            }
                        }
                    }
                }
                is TiviContent.MoviesContent -> {
                    if (uiState.selectedGroupId == null) {
                        TiviEmptyHint("Sélectionne un groupe Films")
                    } else if (c.isLoading) {
                        TiviEmptyHint("Chargement...")
                    } else {
                        TiviMovieGrid(movies = c.movies)
                    }
                }
                is TiviContent.SeriesContent -> {
                    if (uiState.selectedGroupId == null) {
                        TiviEmptyHint("Sélectionne un groupe Séries")
                    } else if (c.isLoading) {
                        TiviEmptyHint("Chargement...")
                    } else {
                        TiviSeriesGrid(series = c.series)
                    }
                }
                TiviContent.Empty -> {
                    TiviEmptyHint("Sélectionne un provider puis un groupe")
                }
            }
        }
    }
}

// ── Mini sidebar des tabs ────────────────────────────────────────────────────

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TiviTabSidebar(
    selectedTab: TiviTab,
    onTabSelected: (TiviTab) -> Unit,
) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .fillMaxHeight()
            .background(NuvioColors.BackgroundElevated)
            .padding(vertical = 20.dp, horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "IPTV",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NuvioColors.Primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        )
        TiviTab.entries.forEach { tab ->
            TiviTabItem(
                tab = tab,
                isSelected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TiviTabItem(
    tab: TiviTab,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        targetValue = when {
            isSelected -> NuvioColors.FocusBackground
            isFocused  -> NuvioColors.BackgroundCard
            else       -> Color.Transparent
        },
        label = "tabBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.FocusRing else Color.Transparent,
        label = "tabBorder"
    )
    val icon: ImageVector = when (tab) {
        TiviTab.LIVE   -> Icons.Default.Tv
        TiviTab.MOVIES -> Icons.Default.Movie
        TiviTab.SERIES -> Icons.Default.Subscriptions
    }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(1.5.dp, borderColor, TabShape)
            .onFocusChanged { isFocused = it.isFocused },
        colors = ClickableSurfaceDefaults.colors(
            containerColor = bgColor,
            focusedContainerColor = bgColor,
            pressedContainerColor = NuvioColors.FocusBackground,
        ),
        shape = ClickableSurfaceDefaults.shape(TabShape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = tab.label,
                tint = if (isSelected || isFocused) NuvioColors.Secondary
                       else NuvioColors.TextSecondary,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = tab.label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected || isFocused) NuvioColors.TextPrimary
                        else NuvioColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ── Grille Films ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TiviMovieGrid(
    movies: List<com.streamvault.domain.model.Movie>,
) {
    if (movies.isEmpty()) {
        TiviEmptyHint("Aucun film dans ce groupe")
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        modifier = Modifier.fillMaxSize().padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(movies, key = { it.id }) { movie ->
            TiviMediaCard(
                title = movie.name,
                posterUrl = movie.posterUrl,
            )
        }
    }
}

// ── Grille Séries ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TiviSeriesGrid(
    series: List<com.streamvault.domain.model.Series>,
) {
    if (series.isEmpty()) {
        TiviEmptyHint("Aucune série dans ce groupe")
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        modifier = Modifier.fillMaxSize().padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(series, key = { it.id }) { s ->
            TiviMediaCard(
                title = s.name,
                posterUrl = s.posterUrl,
            )
        }
    }
}

// ── Card média générique ──────────────────────────────────────────────────────

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TiviMediaCard(
    title: String,
    posterUrl: String?,
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.FocusRing else Color.Transparent,
        label = "cardBorder"
    )

    Column(
        modifier = Modifier
            .width(120.dp)
            .onFocusChanged { isFocused = it.isFocused },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(8.dp))
                .background(NuvioColors.BackgroundCard)
                .border(1.5.dp, borderColor, RoundedCornerShape(8.dp)),
        ) {
            if (!posterUrl.isNullOrBlank()) {
                AsyncImage(
                    model = posterUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            color = NuvioColors.TextSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// ── Hint vide ────────────────────────────────────────────────────────────────

@Composable
private fun TiviEmptyHint(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, color = NuvioColors.TextTertiary, fontSize = 13.sp)
    }
}
