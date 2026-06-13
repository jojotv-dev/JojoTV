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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.nuvio.tv.ui.theme.NuvioColors
import com.streamvault.domain.model.Category
import com.streamvault.domain.repository.SeriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class IptvSeriesCategoryViewModel @Inject constructor(
    private val seriesRepository: SeriesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val providerId: Long = savedStateHandle.get<String>("providerId")?.toLongOrNull() ?: -1L
    val providerName: String = savedStateHandle.get<String>("providerName") ?: ""

    val categories: StateFlow<List<Category>> = seriesRepository.getCategories(providerId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

@Composable
fun IptvSeriesCategoryScreen(
    onNavigateToSeries: (providerId: Long, categoryId: Long, categoryName: String) -> Unit,
    onNavigateToAllSeries: (providerId: Long) -> Unit,
    viewModel: IptvSeriesCategoryViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(NuvioColors.Background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp, vertical = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Tv, null, tint = NuvioColors.TextPrimary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.size(12.dp))
            Text(
                "Séries  ${viewModel.providerName}",
                color = NuvioColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 200.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 48.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SeriesCategoryCard(
                    name = "Toutes les séries",
                    count = null,
                    onClick = { onNavigateToAllSeries(viewModel.providerId) }
                )
            }
            items(categories, key = { it.id }) { category ->
                SeriesCategoryCard(
                    name = category.name,
                    count = category.count.takeIf { it > 0 },
                    onClick = { onNavigateToSeries(viewModel.providerId, category.id, category.name) }
                )
            }
        }
    }
}

@Composable
private fun SeriesCategoryCard(name: String, count: Int?, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(14.dp)
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            
            .onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.97f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                name,
                color = NuvioColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (count != null) {
                Spacer(Modifier.size(12.dp))
                Box(
                    modifier = Modifier
                        .background(NuvioColors.BackgroundElevated, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(count.toString(), color = NuvioColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
