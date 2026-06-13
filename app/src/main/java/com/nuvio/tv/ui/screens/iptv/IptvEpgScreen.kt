package com.nuvio.tv.ui.screens.iptv

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.nuvio.tv.ui.theme.NuvioColors
import com.streamvault.domain.model.Channel
import com.streamvault.domain.model.Program
import com.streamvault.domain.repository.ChannelRepository
import com.streamvault.domain.repository.EpgRepository
import com.streamvault.domain.repository.ProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class EpgChannelRow(val channel: Channel, val programs: List<Program>)
data class EpgUiState(
    val rows: List<EpgChannelRow> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class IptvEpgViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val channelRepository: ChannelRepository,
    private val epgRepository: EpgRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EpgUiState())
    val uiState: StateFlow<EpgUiState> = _uiState.asStateFlow()

    val windowStartMs: Long get() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.HOUR_OF_DAY, -2)
        cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
    val windowEndMs: Long get() = windowStartMs + 8 * 3600_000L

    init { loadEpg() }

    fun reload() { loadEpg() }

    private fun loadEpg() {
        viewModelScope.launch {
            _uiState.value = EpgUiState(isLoading = true)
            try {
                val provider = providerRepository.getActiveProvider().first()
                if (provider == null) {
                    _uiState.value = EpgUiState(isLoading = false, error = "Aucun provider actif")
                    return@launch
                }
                val providerId = provider.id
                val channels = channelRepository.getChannelsWithoutErrorsPageOffset(
                    providerId, ChannelRepository.ALL_CHANNELS_ID, 50, 0
                )
                if (channels.isEmpty()) {
                    _uiState.value = EpgUiState(isLoading = false, error = "Aucune chaine disponible")
                    return@launch
                }
                val channelIds = channels.mapNotNull { it.epgChannelId }.distinct()
                val start = windowStartMs
                val end = windowEndMs
                val programs = if (channelIds.isNotEmpty()) {
                    epgRepository.getProgramsForChannelsSnapshot(providerId, channelIds, start, end)
                } else emptyMap()
                val rows = channels.map { ch ->
                    EpgChannelRow(ch, programs[ch.epgChannelId] ?: emptyList())
                }
                _uiState.value = EpgUiState(rows = rows, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = EpgUiState(isLoading = false, error = e.message ?: "Erreur inconnue")
            }
        }
    }
}

private val CHANNEL_COL_WIDTH = 200.dp
private val HOUR_WIDTH = 320.dp
private val ROW_HEIGHT = 64.dp
private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

@Composable
fun IptvEpgScreen(
    onBack: () -> Unit,
    viewModel: IptvEpgViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val nowMs = remember { System.currentTimeMillis() }
    val channelListState = rememberLazyListState()
    val timelineListState = rememberLazyListState()
    val timelineRowState = rememberLazyListState()

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            val totalMs = (viewModel.windowEndMs - viewModel.windowStartMs).toFloat()
            val nowOffset = ((nowMs - viewModel.windowStartMs).toFloat() / totalMs * (HOUR_WIDTH.value * 8)).toInt()
            timelineRowState.scrollToItem(0, (nowOffset - 200).coerceAtLeast(0))
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(NuvioColors.Background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Default.LiveTv, null, tint = NuvioColors.Primary, modifier = Modifier.size(28.dp))
            Text("Guide des programmes", color = NuvioColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text(
                "Aujourd'hui  \u00b7  ${timeFmt.format(Date(viewModel.windowStartMs))} - ${timeFmt.format(Date(viewModel.windowEndMs))}",
                color = NuvioColors.TextSecondary, fontSize = 13.sp
            )
        }
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chargement du guide...", color = NuvioColors.TextSecondary, fontSize = 16.sp)
            }
            uiState.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Erreur : ${uiState.error}", color = Color(0xFFFF5252), fontSize = 14.sp)
            }
            uiState.rows.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucune donnee EPG disponible", color = NuvioColors.TextSecondary, fontSize = 16.sp)
            }
            else -> EpgGrid(
                rows = uiState.rows,
                nowMs = nowMs,
                windowStartMs = viewModel.windowStartMs,
                windowEndMs = viewModel.windowEndMs,
                channelListState = channelListState,
                timelineListState = timelineListState,
                timelineRowState = timelineRowState
            )
        }
    }
}

@Composable
private fun EpgGrid(
    rows: List<EpgChannelRow>,
    nowMs: Long,
    windowStartMs: Long,
    windowEndMs: Long,
    channelListState: androidx.compose.foundation.lazy.LazyListState,
    timelineListState: androidx.compose.foundation.lazy.LazyListState,
    timelineRowState: androidx.compose.foundation.lazy.LazyListState
) {
    val totalMs = (windowEndMs - windowStartMs).toFloat()
    val totalWidthDp = HOUR_WIDTH * 8

    LaunchedEffect(channelListState.firstVisibleItemIndex, channelListState.firstVisibleItemScrollOffset) {
        if (!timelineListState.isScrollInProgress) {
            timelineListState.scrollToItem(channelListState.firstVisibleItemIndex, channelListState.firstVisibleItemScrollOffset)
        }
    }
    LaunchedEffect(timelineListState.firstVisibleItemIndex, timelineListState.firstVisibleItemScrollOffset) {
        if (!channelListState.isScrollInProgress) {
            channelListState.scrollToItem(timelineListState.firstVisibleItemIndex, timelineListState.firstVisibleItemScrollOffset)
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = channelListState,
            modifier = Modifier.width(CHANNEL_COL_WIDTH).fillMaxHeight(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { Spacer(Modifier.height(40.dp)) }
            items(rows, key = { it.channel.id }) { row -> EpgChannelCell(row.channel) }
        }
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            LazyRow(
                state = timelineRowState,
                modifier = Modifier.fillMaxWidth().height(40.dp).background(NuvioColors.BackgroundElevated),
                contentPadding = PaddingValues(end = 48.dp)
            ) {
                items(8) { h ->
                    Box(
                        modifier = Modifier.width(HOUR_WIDTH).fillMaxHeight().padding(start = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            timeFmt.format(Date(windowStartMs + h * 3600_000L)),
                            color = NuvioColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            LazyColumn(
                state = timelineListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(rows, key = { it.channel.id }) { row ->
                    EpgProgramRow(row.programs, nowMs, windowStartMs, totalMs, totalWidthDp, timelineRowState)
                }
            }
        }
    }
}

@Composable
private fun EpgChannelCell(channel: Channel) {
    Row(
        modifier = Modifier
            .width(CHANNEL_COL_WIDTH)
            .height(ROW_HEIGHT)
            .background(NuvioColors.BackgroundCard)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (channel.logoUrl != null) {
            AsyncImage(
                model = channel.logoUrl,
                contentDescription = null,
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(6.dp))
            )
        } else {
            Box(
                modifier = Modifier.size(36.dp).background(NuvioColors.BackgroundElevated, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(channel.name.take(2).uppercase(), color = NuvioColors.TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Column {
            Text(channel.name, color = NuvioColors.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            channel.currentProgram?.let {
                Text(it.title, color = NuvioColors.TextSecondary, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun EpgProgramRow(
    programs: List<Program>,
    nowMs: Long,
    windowStartMs: Long,
    totalMs: Float,
    totalWidthDp: androidx.compose.ui.unit.Dp,
    timelineRowState: androidx.compose.foundation.lazy.LazyListState
) {
    val scope = rememberCoroutineScope()
    val visible = remember(programs, windowStartMs, totalMs) {
        programs.filter { p ->
            val ef = (p.endTime - windowStartMs) / totalMs
            val sf = (p.startTime - windowStartMs) / totalMs
            ef > 0f && sf < 1f
        }
    }
    Box(modifier = Modifier.height(ROW_HEIGHT).width(totalWidthDp).background(NuvioColors.Background)) {
        // Ligne "maintenant"
        val nowFraction = ((nowMs - windowStartMs) / totalMs).coerceIn(0f, 1f)
        Box(modifier = Modifier.fillMaxHeight().width(2.dp).offset(x = totalWidthDp * nowFraction).background(NuvioColors.Primary.copy(alpha = 0.8f)))
        if (visible.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), contentAlignment = Alignment.CenterStart) {
                Text("Aucune donnee", color = NuvioColors.TextTertiary, fontSize = 11.sp)
            }
        } else {
            visible.forEach { program ->
                val startFraction = ((program.startTime - windowStartMs) / totalMs).coerceIn(0f, 1f)
                val endFraction = ((program.endTime - windowStartMs) / totalMs).coerceIn(0f, 1f)
                val widthDp = (totalWidthDp * (endFraction - startFraction) - 2.dp).coerceAtLeast(2.dp)
                val offsetDp = totalWidthDp * startFraction
                val isNow = nowMs in program.startTime..program.endTime
                androidx.tv.material3.Card(
                    onClick = {},
                    modifier = Modifier
                        .offset(x = offsetDp + 1.dp, y = 2.dp)
                        .width(widthDp)
                        .height(ROW_HEIGHT - 4.dp)
                        .onFocusChanged { fs ->
                            if (fs.hasFocus) scope.launch {
                                timelineRowState.scrollToItem(0, (offsetDp.value - 200f).toInt().coerceAtLeast(0))
                            }
                        },
                    colors = androidx.tv.material3.CardDefaults.colors(
                        containerColor = if (isNow) NuvioColors.Primary.copy(alpha = 0.25f) else NuvioColors.BackgroundCard,
                        focusedContainerColor = NuvioColors.Primary
                    ),
                    shape = androidx.tv.material3.CardDefaults.shape(RoundedCornerShape(6.dp))
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), contentAlignment = Alignment.CenterStart) {
                        Column {
                            Text(
                                program.title,
                                color = NuvioColors.TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isNow) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (widthDp > 100.dp) {
                                Text(
                                    "${timeFmt.format(Date(program.startTime))} \u00b7 ${program.durationMinutes}min",
                                    color = NuvioColors.TextSecondary,
                                    fontSize = 10.sp, maxLines = 1
                                )
                            }
                        }
                        if (isNow) {
                            Box(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth(program.progressPercent(nowMs)).height(2.dp).background(NuvioColors.Primary))
                        }
                    }
                }
            }
        }
    }
}
