package com.nuvio.tv.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.nuvio.tv.ui.theme.NuvioPrimitives

internal fun avatarArtworkZoom(imageUrl: String?): Float {
    val pathSegments = imageUrl?.let { url ->
        runCatching { Uri.parse(url).pathSegments }.getOrDefault(emptyList())
    }.orEmpty()
    val bucketIndex = pathSegments.indexOfLast { it.equals("avatars", ignoreCase = true) }
    if (bucketIndex < 0) return 1f
    val category = pathSegments.getOrNull(bucketIndex + 1)?.trim()?.lowercase()
    return when (category) {
        "animaux", "famille", null, "" -> 1f
        else -> 1.18f
    }
}

@Composable
fun ProfileAvatarCircle(
    name: String,
    colorHex: String,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    isSelected: Boolean = false,
    avatarImageUrl: String? = null,
    imageCrossfade: Boolean = true
) {
    val avatarColor = runCatching { Color(android.graphics.Color.parseColor(colorHex)) }
        .getOrDefault(NuvioPrimitives.blue500)
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val fontSize = (size.value * 0.4f).sp
    val context = LocalContext.current
    val artworkZoom = remember(avatarImageUrl) { avatarArtworkZoom(avatarImageUrl) }
    val imageRequest = remember(context, avatarImageUrl, imageCrossfade) {
        avatarImageUrl?.let {
            ImageRequest.Builder(context)
                .data(it)
                .crossfade(imageCrossfade)
                .build()
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(avatarColor, CircleShape)
            .then(
                if (isSelected) {
                    Modifier.border(3.dp, Color.White, CircleShape)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (imageRequest != null) {
            AsyncImage(
                model = imageRequest,
                contentDescription = name,
                modifier = Modifier
                    .size(size)
                    .graphicsLayer {
                        scaleX = artworkZoom
                        scaleY = artworkZoom
                    }
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = initial,
                color = Color.White,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
