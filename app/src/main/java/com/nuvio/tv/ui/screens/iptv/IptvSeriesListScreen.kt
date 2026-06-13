package com.nuvio.tv.ui.screens.iptv

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.streamvault.domain.model.Series
import com.streamvault.domain.repository.SeriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class IptvSeriesListViewModel @Inject constructor(
    private val seriesRepository: SeriesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val providerId: Long = savedStateHandle.get<String>("providerId")?.toLongOrNull() ?: -1L
    val categoryId: Long? = savedStateHandle.get<String>("categoryId")?.toLongOrNull()
    val categoryName: String = savedStateHandle.get<String>("categoryName") ?: "Toutes les series"

    val series: StateFlow<List<Series>> = (
        if (categoryId == null) seriesRepository.getSeries(providerId)
        else seriesRepository.getSeriesByCategory(providerId, categoryId)
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

@Composable
fun IptvSeriesListScreen(
    onOpenSeries: (seriesId: Long) -> Unit,
    viewModel: IptvSeriesListViewModel = hiltViewModel()
) {
    val series by viewModel.series.collectAsStateWithLifecycle()
    var viewMode by remember { mutableStateOf(IptvViewMode.GRID) }

    Column(modifier = Modifier.fillMaxSize().background(NuvioColors.Background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp, vertical = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Tv, null, tint = NuvioColors.TextPrimary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.size(10.dp))
            Text(
                viewModel.categoryName,
                color = NuvioColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IptvViewModeToggle(viewMode = viewMode, onToggle = {
                viewMode = if (viewMode == IptvViewMode.GRID) IptvViewMode.LIST else IptvViewMode.GRID
            })
        }

        if (series.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.Tv, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(48.dp))
                    Text("Aucune serie", color = NuvioColors.TextSecondary, fontSize = 15.sp)
                }
            }
        } else if (viewMode == IptvViewMode.GRID) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(series, key = { it.id }) { s ->
                    SeriesCard(series = s, onClick = { onOpenSeries(s.id) })
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(series, key = { it.id }) { s ->
                    SeriesListRow(series = s, onClick = { onOpenSeries(s.id) })
                }
            }
        }
    }
}

@Composable
private fun SeriesListRow(series: Series, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(72.dp).onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.97f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.width(48.dp).height(64.dp).background(NuvioColors.BackgroundElevated, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (series.posterUrl != null) {
                    AsyncImage(
                        model = series.posterUrl,
                        contentDescription = series.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Tv, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(20.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = series.name,
                    color = NuvioColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = if (isFocused) Modifier.basicMarquee() else Modifier
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    series.releaseDate?.let { releaseDate ->
                        Text(releaseDate.take(4), color = NuvioColors.TextTertiary, fontSize = 11.sp)
                    }
                    if (series.seasons.isNotEmpty()) {
                        Text("${series.seasons.size} saison(s)", color = NuvioColors.TextTertiary, fontSize = 11.sp)
                    }
                }
            }
            if (series.rating > 0f) {
                Text(
                    "%.1f".format(series.rating),
                    color = Color(0xFFFFD700),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (isFocused) {
                Icon(Icons.Default.PlayArrow, null, tint = NuvioColors.Primary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun SeriesCard(series: Series, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(10.dp)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.97f)
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f).background(NuvioColors.BackgroundElevated),
                contentAlignment = Alignment.Center
            ) {
                if (series.posterUrl != null) {
                    AsyncImage(model = series.posterUrl, contentDescription = series.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.Tv, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(32.dp))
                }
                if (isFocused) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(0.3f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
                if (series.rating > 0f) {
                    Box(Modifier.align(Alignment.TopEnd).padding(6.dp).background(Color.Black.copy(0.7f), RoundedCornerShape(4.dp)).padding(horizontal = 5.dp, vertical = 2.dp)) {
                        Text("%.1f".format(series.rating), color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                if (series.seasons.isNotEmpty()) {
                    Box(Modifier.align(Alignment.BottomStart).padding(6.dp).background(Color.Black.copy(0.7f), RoundedCornerShape(4.dp)).padding(horizontal = 5.dp, vertical = 2.dp)) {
                        Text("${series.seasons.size} S", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column(Modifier.padding(8.dp)) {
                Text(
                    series.name,
                    color = NuvioColors.TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp,
                    modifier = if (isFocused) Modifier.basicMarquee() else Modifier
                )
                series.releaseDate?.let { releaseDate ->
                    Spacer(Modifier.height(2.dp))
                    Text(releaseDate.take(4), color = NuvioColors.TextTertiary, fontSize = 10.sp)
                }
            }
        }
    }
}
