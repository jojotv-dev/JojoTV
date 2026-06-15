package com.nuvio.tv.ui.screens.iptv

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.nuvio.tv.ui.theme.NuvioColors

@Composable
fun IptvImportProgressOverlay(
    state: ImportProgressState,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = state.isImporting || state.isDone || state.errorMessage != null,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(400)),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6080C14)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(28.dp),
                modifier = Modifier.padding(horizontal = 64.dp)
            ) {
                AnimatedContent(
                    targetState = when {
                        state.errorMessage != null -> "Erreur d'import"
                        state.isDone -> "Import terminé ✓"
                        else -> "Import en cours…"
                    },
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                    label = "import_title"
                ) { title ->
                    Text(
                        text = title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            state.errorMessage != null -> Color(0xFFEF5350)
                            state.isDone -> NuvioColors.Secondary
                            else -> Color.White
                        }
                    )
                }

                if (state.isImporting) { ImportPulseBar() }

                if (state.progressMessage.isNotBlank() && !state.isDone) {
                    AnimatedContent(
                        targetState = state.progressMessage,
                        transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
                        label = "progress_msg"
                    ) { msg ->
                        Text(
                            text = msg,
                            color = Color.White.copy(alpha = 0.55f),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImportCounterCard("Chaînes", state.liveCount,  Color(0xFF4FC3F7), state.liveCount  > 0)
                    ImportCounterCard("Films",    state.movieCount,  Color(0xFFCE93D8), state.movieCount  > 0)
                    ImportCounterCard("Séries",   state.seriesCount, Color(0xFF80CBC4), state.seriesCount > 0)
                }

                if (state.isDone) {
                    val total = state.liveCount + state.movieCount + state.seriesCount
                    Text(
                        text = "${total.fmtCount()} entrées importées avec succès.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage,
                        color = Color(0xFFEF5350),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ImportPulseBar() {
    val inf = rememberInfiniteTransition(label = "pulse")
    val alpha by inf.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF4FC3F7).copy(alpha = alpha),
                        NuvioColors.Secondary.copy(alpha = alpha),
                        Color(0xFF80CBC4).copy(alpha = alpha)
                    )
                )
            )
    )
}

@Composable
private fun ImportCounterCard(label: String, count: Int, color: Color, active: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (active) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale_$label"
    )
    Column(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = if (active) 0.13f else 0.04f))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AnimatedContent(
            targetState = count,
            transitionSpec = { fadeIn(tween(120)) togetherWith fadeOut(tween(120)) },
            label = "count_$label"
        ) { v ->
            Text(
                text = v.fmtCount(),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (active) color else color.copy(alpha = 0.25f)
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = if (active) 0.7f else 0.3f),
            fontWeight = FontWeight.Medium
        )
    }
}

private fun Int.fmtCount(): String =
    "%,d".format(this).replace(',', '\u202f')
