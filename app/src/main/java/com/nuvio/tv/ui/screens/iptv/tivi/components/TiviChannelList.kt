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
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import coil3.compose.AsyncImage
import com.streamvault.domain.model.Channel
import com.nuvio.tv.ui.theme.NuvioColors

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TiviChannelList(
    channels: List<Channel>,
    focusedChannelId: Long?,
    onChannelFocused: (Channel) -> Unit,
    onChannelClick: (Channel) -> Unit,
    onFocusChanged: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.onFocusChanged { onFocusChanged(it.hasFocus) }) {
    LazyColumn(
        modifier = modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(NuvioColors.Background),
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
            containerColor = if (isActive) NuvioColors.FocusBackground else Color.Transparent,
            focusedContainerColor = NuvioColors.BackgroundCard,
            pressedContainerColor = NuvioColors.FocusBackground,
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
                    .background(NuvioColors.BackgroundElevated, RoundedCornerShape(4.dp)),
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
                    isFocused || isActive -> NuvioColors.TextPrimary
                    else -> NuvioColors.TextSecondary
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
                        .background(NuvioColors.Secondary, RoundedCornerShape(3.dp))
                )
            }
        }
    }
    }
}
