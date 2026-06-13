package com.nuvio.tv.ui.screens.iptv

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.nuvio.tv.ui.theme.NuvioColors
import com.streamvault.domain.model.Episode
import com.streamvault.domain.model.Result
import com.streamvault.domain.model.Season
import com.streamvault.domain.model.Series
import com.streamvault.domain.repository.SeriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IptvSeriesDetailViewModel @Inject constructor(
    private val seriesRepository: SeriesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val seriesId: Long = savedStateHandle.get<String>("seriesId")?.toLongOrNull() ?: -1L
    val providerId: Long = savedStateHandle.get<String>("providerId")?.toLongOrNull() ?: -1L

    private val _series = MutableStateFlow<Series?>(null)
    val series: StateFlow<Series?> = _series.asStateFlow()

    init {
        viewModelScope.launch {
            _series.value = seriesRepository.getSeriesById(seriesId)
        }
    }

    private val _selectedSeasonIndex = MutableStateFlow(0)
    val selectedSeasonIndex: StateFlow<Int> = _selectedSeasonIndex.asStateFlow()

    private val _loadingEpisodeId = MutableStateFlow<Long?>(null)
    val loadingEpisodeId: StateFlow<Long?> = _loadingEpisodeId.asStateFlow()

    private val _streamError = MutableStateFlow<String?>(null)
    val streamError: StateFlow<String?> = _streamError.asStateFlow()

    fun selectSeason(index: Int) { _selectedSeasonIndex.value = index }
    fun clearStreamError() { _streamError.value = null }

    suspend fun resolveEpisodeStream(episode: Episode): Result<Pair<String, Map<String, String>>> {
        _loadingEpisodeId.value = episode.id
        val result = seriesRepository.getEpisodeStreamInfo(episode)
        _loadingEpisodeId.value = null
        return when (result) {
            is Result.Success -> Result.Success(result.data.url to result.data.headers)
            is Result.Error -> { _streamError.value = result.message; Result.Error(result.message) }
            else -> Result.Error("Erreur inconnue")
        }
    }
}

@Composable
fun IptvSeriesDetailScreen(
    onPlayEpisode: (streamUrl: String, title: String, headers: Map<String, String>, posterUrl: String?) -> Unit,
    viewModel: IptvSeriesDetailViewModel = hiltViewModel()
) {
    val series by viewModel.series.collectAsStateWithLifecycle()
    val selectedSeasonIndex by viewModel.selectedSeasonIndex.collectAsStateWithLifecycle()
    val loadingEpisodeId by viewModel.loadingEpisodeId.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val s = series
    if (s == null) {
        Box(Modifier.fillMaxSize().background(NuvioColors.Background), contentAlignment = Alignment.Center) {
            Text("Chargement...", color = NuvioColors.TextSecondary)
        }
        return
    }

    Row(modifier = Modifier.fillMaxSize().background(NuvioColors.Background)) {
        // Poster + infos série
        Column(
            modifier = Modifier
                .width(260.dp)
                .fillMaxHeight()
                .background(NuvioColors.BackgroundCard)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (s.posterUrl != null) {
                AsyncImage(
                    model = s.posterUrl,
                    contentDescription = s.name,
                    modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f).background(NuvioColors.BackgroundElevated, RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    Modifier.fillMaxWidth().aspectRatio(2f / 3f)
                        .background(NuvioColors.BackgroundElevated, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Tv, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(48.dp))
                }
            }
            Text(s.name, color = NuvioColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp)
            val releaseDate = s.releaseDate
            if (releaseDate != null) {
                Text(releaseDate.take(4), color = NuvioColors.TextTertiary, fontSize = 13.sp)
            }
            if (s.rating > 0f) {
                Text("? ${"%.1f".format(s.rating)}", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            val plot = s.plot
            if (plot != null) {
                Text(plot, color = NuvioColors.TextSecondary, fontSize = 12.sp, lineHeight = 17.sp, maxLines = 6, overflow = TextOverflow.Ellipsis)
            }
        }

        // Saisons + épisodes
        Column(modifier = Modifier.weight(1f).fillMaxHeight().padding(top = 24.dp)) {
            // Sélecteur de saisons
            if (s.seasons.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(s.seasons.indices.toList()) { index ->
                        val season = s.seasons[index]
                        SeasonTab(
                            label = "Saison ${season.seasonNumber}",
                            selected = selectedSeasonIndex == index,
                            onClick = { viewModel.selectSeason(index) }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Liste des épisodes
            val currentSeason: Season? = s.seasons.getOrNull(selectedSeasonIndex)
            if (currentSeason == null || currentSeason.episodes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun �pisode", color = NuvioColors.TextSecondary, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(currentSeason.episodes, key = { it.id }) { episode ->
                        EpisodeRow(
                            episode = episode,
                            isLoading = loadingEpisodeId == episode.id,
                            onClick = {
                                scope.launch {
                                    val result = viewModel.resolveEpisodeStream(episode)
                                    if (result is Result.Success) {
                                        val (url, headers) = result.data
                                        onPlayEpisode(url, "${s.name} � ${episode.title}", headers, episode.coverUrl ?: s.posterUrl)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SeasonTab(label: String, selected: Boolean, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(8.dp)
    val bgColor by animateColorAsState(
        targetValue = when { selected -> NuvioColors.Primary; isFocused -> NuvioColors.FocusBackground; else -> NuvioColors.BackgroundCard },
        animationSpec = tween(150), label = "tabBg"
    )
    Card(
        onClick = onClick,
        modifier = Modifier.height(36.dp).onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.96f)
    ) {
        Box(Modifier.fillMaxHeight().padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
            Text(
                label,
                color = if (selected || isFocused) NuvioColors.TextPrimary else NuvioColors.TextSecondary,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun EpisodeRow(episode: Episode, isLoading: Boolean, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(10.dp)
    val bgColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.FocusBackground else NuvioColors.BackgroundCard,
        animationSpec = tween(150), label = "epBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.Primary else Color.Transparent,
        animationSpec = tween(150), label = "epBorder"
    )
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.97f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail
            Box(
                modifier = Modifier.width(120.dp).aspectRatio(16f / 9f)
                    .background(NuvioColors.BackgroundElevated, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                val thumb = episode.coverUrl
                if (thumb != null) {
                    AsyncImage(model = thumb, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.Tv, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(24.dp))
                }
                if (isLoading) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(0.5f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Refresh, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                } else if (isFocused) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(0.3f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
            }
            // Infos
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "E${episode.episodeNumber} � ${episode.title}",
                    color = NuvioColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val epPlot = episode.plot
                if (epPlot != null) {
                    Text(epPlot, color = NuvioColors.TextSecondary, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 16.sp)
                }
                if (episode.durationSeconds > 0) {
                    Text("${episode.durationSeconds / 60} min", color = NuvioColors.TextTertiary, fontSize = 11.sp)
                }
            }
        }
    }
}
