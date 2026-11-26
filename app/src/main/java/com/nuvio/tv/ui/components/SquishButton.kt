package com.nuvio.tv.ui.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

private val SquishButtonReleaseEasing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)

@Composable
internal fun Modifier.squishOnPress(isPressed: Boolean, label: String): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = if (isPressed) {
            tween(durationMillis = 80, easing = LinearOutSlowInEasing)
        } else {
            tween(durationMillis = 500, easing = SquishButtonReleaseEasing)
        },
        label = label,
    )
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
