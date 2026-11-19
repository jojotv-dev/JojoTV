@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.nuvio.tv.ui.reshaped.livetv

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Border
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.nuvio.tv.R
import com.nuvio.tv.reshaped.livetv.LiveTvChannel
import com.nuvio.tv.reshaped.livetv.LiveTvRepository
import com.nuvio.tv.reshaped.livetv.LiveTvSource
import com.nuvio.tv.reshaped.livetv.LiveTvVodCache
import com.nuvio.tv.reshaped.livetv.LiveTvVodItem
import com.nuvio.tv.reshaped.livetv.LiveTvVodPlayback
import com.nuvio.tv.ui.theme.NuvioTheme
import com.nuvio.tv.ui.theme.NuvioStrokes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private enum class FavoriteKind { Channels, Movies, Series }

@Composable
fun IptvFavoritesScreen(
    onOpenLibrary: () -> Unit,
    onPlayChannel: (String) -> Unit,
    onPlayVod: (LiveTvSource, LiveTvVodItem, LiveTvVodPlayback) -> Unit,
    showBuiltInHeader: Boolean = true,
) {
    val liveTvModel: LiveTvScreenModel = hiltViewModel()
    val state by LiveTvRepository.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    var kind by rememberSaveable { mutableStateOf(FavoriteKind.Channels) }
    var cachedVod by remember { mutableStateOf<List<LiveTvVodItem>>(emptyList()) }
    var cachedVodLoaded by remember { mutableStateOf(false) }
    var selectedSeries by remember { mutableStateOf<LiveTvVodItem?>(null) }
    var episodes by remember { mutableStateOf<List<LiveTvVodItem>>(emptyList()) }
    var seriesLoading by remember { mutableStateOf(false) }
    var seriesLoadError by remember { mutableStateOf(false) }
    var playbackError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { liveTvModel.ensureLoaded() }
    LaunchedEffect(state.sources, state.favoriteVodKeys, state.sourceStorageReady) {
        cachedVodLoaded = false
        if (!state.sourceStorageReady) {
            cachedVod = emptyList()
            return@LaunchedEffect
        }
        cachedVod = LiveTvVodCache.readFavorites(context, state.sources, state.favoriteVodKeys)
        cachedVodLoaded = true
    }
    LaunchedEffect(selectedSeries) {
        val series = selectedSeries ?: return@LaunchedEffect
        val source = state.sources.firstOrNull { it.id == series.sourceId } ?: return@LaunchedEffect
        seriesLoading = true
        seriesLoadError = false
        episodes = emptyList()
        try {
            episodes = loadIptvSeriesEpisodes(source, series)
        } catch (failure: Exception) {
            if (failure is CancellationException) throw failure
            seriesLoadError = true
        } finally {
            seriesLoading = false
        }
    }

    val favoriteMovies = remember(cachedVod) {
        cachedVod.filter { it.type == "movie" }
    }
    val favoriteSeries = remember(cachedVod) {
        cachedVod.filter { it.type == "series" }
    }
    val favoriteChannels = remember(state.channels, state.favoriteUrls) {
        state.channels.filter { it.streamUrl in state.favoriteUrls }
    }
    val displayedVod = when (kind) {
        FavoriteKind.Movies -> favoriteMovies
        FavoriteKind.Series -> favoriteSeries
        FavoriteKind.Channels -> emptyList()
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        if (showBuiltInHeader) {
            Text(
                text = stringResource(R.string.nav_library),
                color = NuvioTheme.colors.TextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FavoriteKind.entries.forEach { option ->
                val label = when (option) {
                    FavoriteKind.Channels -> stringResource(R.string.iptv_favorites_channels)
                    FavoriteKind.Movies -> stringResource(R.string.iptv_favorites_movies)
                    FavoriteKind.Series -> stringResource(R.string.iptv_favorites_series)
                }
                FavoriteHeaderButton(
                    label = label,
                    selected = kind == option,
                    onClick = {
                        kind = option
                        selectedSeries = null
                    },
                )
            }
            androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
            FavoriteHeaderButton(
                label = stringResource(R.string.library_title),
                onClick = onOpenLibrary,
            )
        }
        if (kind == FavoriteKind.Movies || kind == FavoriteKind.Series) {
            Text(
                stringResource(R.string.iptv_favorites_long_press_hint),
                Modifier.padding(bottom = 10.dp),
                color = NuvioTheme.colors.TextSecondary,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        when (kind) {
            FavoriteKind.Channels -> if (favoriteChannels.isEmpty()) {
                FavoritesEmpty(stringResource(if (state.isLoading) R.string.live_tv_loading else R.string.iptv_favorites_empty))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(favoriteChannels, key = { "channel:${it.streamUrl}" }) { channel ->
                        FavoriteChannelRow(
                            channel = channel,
                            onClick = {
                                scope.launch {
                                    liveTvPlayerRoute(channel, liveTvModel.profileId)?.let(onPlayChannel)
                                }
                            },
                            onRemove = { LiveTvRepository.toggleFavorite(channel) },
                        )
                    }
                }
            }
            FavoriteKind.Movies, FavoriteKind.Series -> {
                when {
                    kind == FavoriteKind.Series && selectedSeries != null -> {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Card(
                                onClick = { selectedSeries = null },
                                shape = CardDefaults.shape(shape = NuvioTheme.configuredShape(RoundedCornerShape(18.dp))),
                                scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
                            ) {
                                Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Text(stringResource(R.string.action_back), Modifier.padding(start = 8.dp))
                                }
                            }
                            Text(selectedSeries?.title.orEmpty(), Modifier.padding(start = 14.dp), color = NuvioTheme.colors.TextPrimary)
                        }
                        when {
                            seriesLoading -> FavoritesEmpty(stringResource(R.string.iptv_vod_loading))
                            seriesLoadError && episodes.isEmpty() -> FavoritesEmpty(stringResource(R.string.iptv_vod_error))
                            episodes.isEmpty() -> FavoritesEmpty(stringResource(R.string.iptv_vod_empty_episodes))
                            else -> VodGrid(items = episodes, onSelect = { episode ->
                                val source = state.sources.firstOrNull { it.id == episode.sourceId } ?: return@VodGrid
                                scope.launch {
                                    runCatching { resolveIptvVod(source, episode) }
                                        .onSuccess { onPlayVod(source, episode, it) }
                                        .onFailure { if (it is CancellationException) throw it else playbackError = true }
                                }
                            })
                        }
                    }
                    displayedVod.isEmpty() -> FavoritesEmpty(stringResource(
                        if (!cachedVodLoaded) R.string.iptv_vod_loading else R.string.iptv_favorites_empty,
                    ))
                    else -> VodGrid(
                        items = displayedVod,
                        favoriteVodKeys = state.favoriteVodKeys,
                        onToggleFavorite = { item ->
                            LiveTvRepository.toggleVodFavorite(item)
                            scope.launch { LiveTvVodCache.upsert(context, item) }
                        },
                        onSelect = { item ->
                            val source = state.sources.firstOrNull { it.id == item.sourceId } ?: return@VodGrid
                            if (item.type == "series") {
                                selectedSeries = item
                            } else {
                                scope.launch {
                                    runCatching { resolveIptvVod(source, item) }
                                        .onSuccess { onPlayVod(source, item, it) }
                                        .onFailure { if (it is CancellationException) throw it else playbackError = true }
                                }
                            }
                        },
                    )
                }
                if (playbackError) {
                    Text(
                        stringResource(R.string.iptv_vod_playback_error),
                        Modifier.padding(top = 8.dp),
                        color = NuvioTheme.colors.TextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteChannelRow(channel: LiveTvChannel, onClick: () -> Unit, onRemove: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Card(
            onClick = onClick,
            modifier = Modifier.weight(1f).onFocusChanged { focused = it.isFocused },
            colors = CardDefaults.colors(
                containerColor = NuvioTheme.colors.SurfaceVariant,
                focusedContainerColor = NuvioTheme.colors.FocusBackground,
            ),
            border = CardDefaults.border(
                border = Border.None,
                focusedBorder = Border(
                    border = BorderStroke(NuvioStrokes.tokens.focus, NuvioTheme.colors.FocusRing),
                    shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp)),
                ),
            ),
            shape = CardDefaults.shape(shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))),
            scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
        ) {
            Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = channel.logoUrl?.takeIf(String::isNotBlank),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(width = 60.dp, height = 42.dp).clip(NuvioTheme.configuredShape(RoundedCornerShape(6.dp))),
                )
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(
                        channel.name,
                        color = NuvioTheme.colors.TextPrimary,
                        maxLines = 1,
                        modifier = Modifier.then(if (focused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier),
                        fontWeight = FontWeight.Medium,
                    )
                    Text(channel.group, color = NuvioTheme.colors.TextSecondary, maxLines = 1, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Card(
            onClick = onRemove,
            colors = CardDefaults.colors(focusedContainerColor = NuvioTheme.colors.FocusBackground),
            border = CardDefaults.border(
                border = Border.None,
                focusedBorder = Border(
                    border = BorderStroke(NuvioStrokes.tokens.focus, NuvioTheme.colors.FocusRing),
                    shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp)),
                ),
            ),
            shape = CardDefaults.shape(shape = NuvioTheme.configuredShape(RoundedCornerShape(10.dp))),
            scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
        ) {
            Text(stringResource(R.string.iptv_favorites_remove), Modifier.padding(horizontal = 14.dp, vertical = 12.dp))
        }
    }
}

@Composable
private fun FavoriteHeaderButton(
    label: String,
    selected: Boolean = false,
    onClick: () -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val buttonShape = NuvioTheme.configuredShape(RoundedCornerShape(20.dp))
    Card(
        onClick = onClick,
        modifier = Modifier.onFocusChanged { focused = it.hasFocus },
        colors = CardDefaults.colors(
            containerColor = if (selected) NuvioTheme.colors.FocusBackground else NuvioTheme.colors.SurfaceVariant,
            focusedContainerColor = NuvioTheme.colors.FocusBackground,
        ),
        border = CardDefaults.border(
            border = Border.None,
            focusedBorder = Border(
                border = BorderStroke(NuvioStrokes.tokens.focus, NuvioTheme.colors.FocusRing),
                shape = buttonShape,
            ),
        ),
        shape = CardDefaults.shape(
            shape = buttonShape,
            focusedShape = buttonShape,
            pressedShape = buttonShape,
        ),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            color = if (focused) NuvioTheme.colors.FocusContent else NuvioTheme.colors.TextPrimary,
        )
    }
}

@Composable
private fun FavoritesEmpty(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = NuvioTheme.colors.TextSecondary)
    }
}
