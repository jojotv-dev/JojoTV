package com.nuvio.tv.ui.screens.iptv.tivi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.streamvault.domain.model.Channel
import com.streamvault.domain.model.Program
import com.nuvio.tv.ui.theme.NuvioColors
import java.text.SimpleDateFormat
import java.util.*

private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

@Composable
fun TiviTopPanel(
    channel: Channel?,
    currentProgram: Program?,
    nextProgram: Program?,
    exoPlayer: ExoPlayer?,
    miniPlayerActive: Boolean,
    onMiniPlayerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var miniPlayerFocused by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(NuvioColors.BackgroundElevated)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // ── Mini Player ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .width(240.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
                .onFocusChanged { miniPlayerFocused = it.hasFocus }
                .clickable { onMiniPlayerClick() },
            contentAlignment = Alignment.Center,
        ) {
            if (miniPlayerActive && exoPlayer != null) {
                // Surface ExoPlayer
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            useController = false
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                            player = exoPlayer
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
                // Overlay focus ring
                if (miniPlayerFocused) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = NuvioColors.FocusRing.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
                // Icone play au centre pour indiquer cliquabilite
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = if (miniPlayerFocused) 0.3f else 0.0f)),
                    contentAlignment = Alignment.Center
                ) { }
            } else if (channel != null) {
                // Placeholder : logo de la chaine
                if (!channel.logoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = channel.logoUrl,
                        contentDescription = channel.name,
                        modifier = Modifier
                            .fillMaxHeight(0.6f)
                            .padding(horizontal = 16.dp),
                    )
                } else {
                    Text(
                        text = channel.name,
                        color = NuvioColors.TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            } else {
                Text(
                    text = "Aucune cha\u00eene",
                    color = NuvioColors.TextDisabled,
                    fontSize = 11.sp,
                )
            }
        }

        // ── Infos programme ──────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (channel != null) {
                Text(
                    text = channel.name,
                    fontSize = 11.sp,
                    color = NuvioColors.TextTertiary,
                    fontWeight = FontWeight.Medium,
                )
            }

            if (currentProgram != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = currentProgram.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NuvioColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = buildString {
                        append(timeFmt.format(Date(currentProgram.startTime)))
                        append(" \u2013 ")
                        append(timeFmt.format(Date(currentProgram.endTime)))
                    },
                    fontSize = 11.sp,
                    color = NuvioColors.Secondary,
                )
                if (currentProgram.description.isNotBlank()) {
                    Text(
                        text = currentProgram.description,
                        fontSize = 11.sp,
                        color = NuvioColors.TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            } else if (channel != null) {
                Text(
                    text = "Aucune info EPG",
                    fontSize = 12.sp,
                    color = NuvioColors.TextTertiary,
                )
            }

            if (nextProgram != null) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Ensuite : ${nextProgram.title}  ${timeFmt.format(Date(nextProgram.startTime))}",
                    fontSize = 10.sp,
                    color = NuvioColors.TextTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}