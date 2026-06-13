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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
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
import com.streamvault.domain.model.Channel
import com.streamvault.domain.model.Result
import com.streamvault.domain.repository.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IptvChannelListViewModel @Inject constructor(
    private val channelRepository: ChannelRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val providerId: Long = savedStateHandle.get<String>("providerId")?.toLongOrNull() ?: -1L
    val categoryId: Long = savedStateHandle.get<String>("categoryId")?.toLongOrNull() ?: ChannelRepository.ALL_CHANNELS_ID
    val categoryName: String = savedStateHandle.get<String>("categoryName") ?: "Toutes les chaines"

    val channels: StateFlow<List<Channel>> = if (categoryId == ChannelRepository.ALL_CHANNELS_ID) {
        channelRepository.getChannelsWithoutErrors(providerId)
    } else {
        channelRepository.getChannelsByCategory(providerId, categoryId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _loadingChannelId = MutableStateFlow<Long?>(null)
    val loadingChannelId: StateFlow<Long?> = _loadingChannelId.asStateFlow()

    private val _streamError = MutableStateFlow<String?>(null)
    val streamError: StateFlow<String?> = _streamError.asStateFlow()

    fun clearStreamError() { _streamError.value = null }

    fun prefetchVisibleChannels(channels: List<Channel>) {
        channels.take(12).forEach { channel ->
            viewModelScope.launch {
                try { channelRepository.getStreamInfo(channel) } catch (_: Exception) {}
            }
        }
    }

    suspend fun resolveStream(channel: Channel): Result<Pair<String, Map<String, String>>> {
        _loadingChannelId.value = channel.id
        val result = channelRepository.getStreamInfo(channel)
        _loadingChannelId.value = null
        return when (result) {
            is Result.Success -> Result.Success(result.data.url to result.data.headers)
            is Result.Error -> { _streamError.value = result.message; Result.Error(result.message) }
            else -> Result.Error("Erreur inconnue")
        }
    }
}

@Composable
fun IptvChannelListScreen(
    onPlayChannel: (streamUrl: String, title: String, headers: Map<String, String>, logoUrl: String?) -> Unit,
    viewModel: IptvChannelListViewModel = hiltViewModel()
) {
    val channels by viewModel.channels.collectAsStateWithLifecycle()
    val loadingChannelId by viewModel.loadingChannelId.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var viewMode by remember { mutableStateOf(IptvViewMode.GRID) }
    LaunchedEffect(channels) {
        if (channels.isNotEmpty()) viewModel.prefetchVisibleChannels(channels)
    }

    Column(modifier = Modifier.fillMaxSize().background(NuvioColors.Background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp, vertical = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LiveTv, null, tint = NuvioColors.TextPrimary, modifier = Modifier.size(22.dp))
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

        if (channels.isEmpty() && loadingChannelId == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.LiveTv, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(48.dp))
                    Text("Aucune chaine", color = NuvioColors.TextSecondary, fontSize = 15.sp)
                }
            }
        } else if (viewMode == IptvViewMode.GRID) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(channels, key = { it.id }) { channel ->
                    ChannelCard(
                        channel = channel,
                        isLoading = loadingChannelId == channel.id,
                        onClick = {
                            scope.launch {
                                val result = viewModel.resolveStream(channel)
                                if (result is Result.Success) {
                                    val (url, headers) = result.data
                                    onPlayChannel(url, channel.name, headers, channel.logoUrl)
                                }
                            }
                        }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(channels, key = { it.id }) { channel ->
                    ChannelListRow(
                        channel = channel,
                        isLoading = loadingChannelId == channel.id,
                        onClick = {
                            scope.launch {
                                val result = viewModel.resolveStream(channel)
                                if (result is Result.Success) {
                                    val (url, headers) = result.data
                                    onPlayChannel(url, channel.name, headers, channel.logoUrl)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChannelListRow(channel: Channel, isLoading: Boolean, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(64.dp).onFocusChanged { isFocused = it.hasFocus },
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
            // Logo ou icone
            Box(
                modifier = Modifier.size(40.dp).background(NuvioColors.BackgroundElevated, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (channel.logoUrl != null) {
                    AsyncImage(
                        model = channel.logoUrl,
                        contentDescription = channel.name,
                        modifier = Modifier.fillMaxSize().padding(6.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(Icons.Default.LiveTv, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(20.dp))
                }
                if (isLoading) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(0.5f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Refresh, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
            // Numero
            if (channel.number > 0) {
                Text(channel.number.toString(), color = NuvioColors.TextTertiary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            // Nom + programme
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channel.name,
                    color = NuvioColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = if (isFocused) Modifier.basicMarquee() else Modifier
                )
                val prog = channel.currentProgram
                if (prog != null) {
                    Text(
                        text = prog.title,
                        color = NuvioColors.TextTertiary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = if (isFocused) Modifier.basicMarquee() else Modifier
                    )
                }
            }
            // Indicateur lecture
            if (isFocused && !isLoading) {
                Icon(Icons.Default.PlayArrow, null, tint = NuvioColors.Primary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun ChannelCard(channel: Channel, isLoading: Boolean, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)
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
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(NuvioColors.BackgroundElevated),
                contentAlignment = Alignment.Center
            ) {
                if (channel.logoUrl != null) {
                    AsyncImage(
                        model = channel.logoUrl,
                        contentDescription = channel.name,
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(Icons.Default.LiveTv, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(32.dp))
                }
                if (isLoading) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(0.5f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Refresh, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
                if (isFocused && !isLoading) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(0.3f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
                if (channel.number > 0) {
                    Box(
                        modifier = Modifier.align(Alignment.TopStart).padding(6.dp)
                            .background(Color.Black.copy(0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(channel.number.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    channel.name,
                    color = NuvioColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = if (isFocused) Modifier.basicMarquee() else Modifier
                )
                val prog = channel.currentProgram
                if (prog != null) {
                    Spacer(Modifier.height(3.dp))
                    Text(prog.title, color = NuvioColors.TextTertiary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    val progress = prog.progressPercent()
                    if (progress > 0f) {
                        Spacer(Modifier.height(4.dp))
                        Box(Modifier.fillMaxWidth().height(2.dp).background(NuvioColors.BackgroundElevated, RoundedCornerShape(1.dp))) {
                            Box(Modifier.fillMaxWidth(progress).fillMaxHeight().background(NuvioColors.Primary))
                        }
                    }
                }
            }
        }
    }
}
