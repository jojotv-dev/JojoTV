package com.nuvio.tv.ui.screens.iptv.tivi

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState

import androidx.compose.animation.core.tween
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.tv.material3.*
import coil3.compose.AsyncImage
import com.nuvio.tv.ui.navigation.Screen
import com.nuvio.tv.ui.theme.NuvioColors
import com.nuvio.tv.ui.screens.iptv.tivi.components.*

private val TabShape = RoundedCornerShape(10.dp)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun IptvTiviScreen(
    onBack: () -> Unit,
    navController: NavController,
    initialTab: TiviTab = TiviTab.LIVE,
    viewModel: IptvTiviViewModel = hiltViewModel(),
    miniPlayerVm: TiviMiniPlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val miniState by miniPlayerVm.state.collectAsState()
    LaunchedEffect(initialTab) { viewModel.selectTab(initialTab) }
    BackHandler { onBack() }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(NuvioColors.Background),
    ) {

        // -- Colonne 1 : Provider Tree accordeon --------------------------
        TiviProviderTree(
            providerNodes = uiState.providerNodes,
            selectedGroupId = uiState.selectedGroupId,
            selectedProviderId = uiState.selectedProviderId,
            onProviderClick = { viewModel.toggleProvider(it) },
            onProviderFocus = { viewModel.focusProvider(it) },
            onGroupClick = { providerId, groupId -> viewModel.selectGroup(providerId, groupId) },
            onGroupFocus = { providerId, groupId -> viewModel.focusGroup(providerId, groupId) },
            modifier = Modifier.fillMaxHeight(),
        )

        // -- Separateur vertical ------------------------------------------
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(NuvioColors.Border)
        )

        // -- Colonne 2 : Contenu dynamique --------------------------------
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            when (val c = uiState.content) {
                is TiviContent.LiveContent -> {
                    if (uiState.selectedGroupId == null) {
                        TiviEmptyHint("S\u00e9lectionne un groupe Live TV")
                    } else {
                        Column(Modifier.fillMaxSize()) {
                            TiviTopPanel(
                                channel = c.focusedChannel,
                                currentProgram = c.currentProgram,
                                nextProgram = c.nextProgram,
                                exoPlayer = if (miniState.isReady) miniPlayerVm.exoPlayer else null,
                                miniPlayerActive = miniState.isReady,
                                onMiniPlayerClick = {
                                    miniState.channel?.let { ch ->
                                        miniState.streamUrl?.let { url ->
                                            miniPlayerVm.stop()
                                            navController.navigate(
                                                Screen.Player.createRoute(
                                                    streamUrl = url,
                                                    title = ch.name,
                                                )
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )











                            Row(Modifier.weight(1f).fillMaxWidth()) {
                                TiviChannelList(
                                    channels = c.channels,
                                    focusedChannelId = c.focusedChannel?.id,
                                    onChannelFocused = { ch -> viewModel.focusChannel(ch.providerId, ch) },
                                    onChannelClick = { channel ->
                                        if (miniState.channel?.id == channel.id && miniState.isReady) {
                                            // 2e clic -> plein ecran
                                            miniPlayerVm.stop()
                                            navController.navigate(
                                                Screen.Player.createRoute(
                                                    streamUrl = channel.streamUrl,
                                                    title = channel.name,
                                                )
                                            )
                                        } else {
                                            // 1er clic -> mini lecteur
                                            miniPlayerVm.loadChannel(channel, channel.streamUrl)
                                        }
                                    },

                                    modifier = Modifier.weight(0.35f).fillMaxHeight(),
                                )
                                TiviEpgGrid(
                                    epgRows = c.epgRows,
                                    focusedChannelId = c.focusedChannel?.id,
                                    modifier = Modifier.weight(0.65f).fillMaxHeight(),
                                )
                            }
                        }
                    }
                }
                is TiviContent.MoviesContent -> {
                    if (uiState.selectedGroupId == null) {
                        TiviEmptyHint("S\u00e9lectionne un groupe Films")
                    } else if (c.isLoading) {
                        TiviEmptyHint("Chargement...")
                    } else {
                        TiviMovieGrid(movies = c.movies)
                    }
                }
                is TiviContent.SeriesContent -> {
                    if (uiState.selectedGroupId == null) {
                        TiviEmptyHint("S\u00e9lectionne un groupe S\u00e9ries")
                    } else if (c.isLoading) {
                        TiviEmptyHint("Chargement...")
                    } else {
                        TiviSeriesGrid(series = c.series)
                    }
                }
                TiviContent.Empty -> {
                    TiviEmptyHint("S\u00e9lectionne un provider puis un groupe")
                }
            }
        }
    }
}

// -- Grille Films -------------------------------------------------------------

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

// -- Grille Series ------------------------------------------------------------

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TiviSeriesGrid(
    series: List<com.streamvault.domain.model.Series>,
) {
    if (series.isEmpty()) {
        TiviEmptyHint("Aucune s\u00e9rie dans ce groupe")
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

// -- Card media generique -----------------------------------------------------

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

// -- Hint vide ----------------------------------------------------------------

@Composable
private fun TiviEmptyHint(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, color = NuvioColors.TextTertiary, fontSize = 13.sp)
    }
}
