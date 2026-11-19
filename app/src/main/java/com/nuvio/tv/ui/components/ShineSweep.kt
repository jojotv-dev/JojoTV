package com.nuvio.tv.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlinx.coroutines.delay

@Composable
fun Modifier.shineSweepOnFocus(
    isFocused: Boolean,
    restartKey: Any? = null,
    startDelayMillis: Long = 0L,
): Modifier {
    val sweepProgress = remember(restartKey) { Animatable(0f) }
    val sweepVisible = remember(restartKey) { mutableStateOf(false) }

    LaunchedEffect(isFocused, restartKey, startDelayMillis) {
        sweepVisible.value = false
        sweepProgress.snapTo(0f)
        if (isFocused) {
            if (startDelayMillis > 0L) delay(startDelayMillis)
            sweepVisible.value = true
            sweepProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 720,
                    easing = CubicBezierEasing(0.45f, 0f, 0.2f, 1f)
                )
            )
            sweepVisible.value = false
        }
    }

    return drawWithContent {
        drawContent()
        if (!sweepVisible.value || !isFocused || size.width <= 0f || size.height <= 0f) return@drawWithContent

        val bandWidth = size.width * 0.36f
        val skew = size.height * 0.364f
        val startLeft = -bandWidth - skew
        val endLeft = size.width + skew
        val left = startLeft + (endLeft - startLeft) * sweepProgress.value.coerceIn(0f, 1f)
        val band = Path().apply {
            moveTo(left, 0f)
            lineTo(left + bandWidth, 0f)
            lineTo(left + bandWidth - skew, size.height)
            lineTo(left - skew, size.height)
            close()
        }
        val brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.42f),
                Color(0xFFEDE9E0).copy(alpha = 0.62f),
                Color.White.copy(alpha = 0.42f),
                Color.Transparent
            ),
            startX = left,
            endX = left + bandWidth
        )
        drawPath(band, brush)
    }
}
