package com.nuvio.tv.ui.screens.iptv.tivi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import coil3.compose.AsyncImage
import com.streamvault.domain.model.Channel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TiviChannelList(
    channels: List<Channel>,
    focusedChannelId: Long?,
    onChannelFocused: (Channel) -> Unit,
    onChannelClick: (Channel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(Color(0xFF0F0F22)),
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        items(channels, key = { it.id }) { channel ->
            TiviChannelRow(
                channel = channel,
                isActive = channel.id == focusedChannelId,
                onFocused = { onChannelFocused(channel) },
                onClick = { onChannelClick(channel) },
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TiviChannelRow(
    channel: Channel,
    isActive: Boolean,
    onFocused: () -> Unit,
    onClick: () -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) onFocused()
            },
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (isActive) Color(0xFF1C1C3E) else Color.Transparent,
            focusedContainerColor = Color(0xFF22225A),
            pressedContainerColor = Color(0xFF1A3A6A),
        ),
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(0.dp)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(width = 50.dp, height = 28.dp)
                    .background(Color(0xFF1A1A30), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (!channel.logoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = channel.logoUrl,
                        contentDescription = channel.name,
                        modifier = Modifier.fillMaxSize().padding(2.dp),
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            // Nom
            Text(
                text = channel.name,
                fontSize = 12.sp,
                color = when {
                    isFocused || isActive -> Color.White
                    else -> Color(0xFFAAAAAA)
                },
                fontWeight = if (isFocused || isActive) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

            // Indicateur programme en cours
            if (channel.currentProgram != null) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFF4FC3F7), RoundedCornerShape(3.dp))
                )
            }
        }
    }
}
