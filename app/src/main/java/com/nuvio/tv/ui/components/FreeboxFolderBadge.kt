package com.nuvio.tv.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.nuvio.tv.ui.theme.NuvioTheme

/** Folder marker used on Freebox folders, colored with the active theme accent. */
@Composable
fun FreeboxFolderBadge(modifier: Modifier = Modifier) {
    val folderColor = NuvioTheme.colors.Secondary
    val darkColor = Color(0xFF15274A)

    Canvas(modifier = modifier.width(42.dp).aspectRatio(1.26f)) {
        val scaleX = size.width / 252f
        val scaleY = size.height / 200f

        fun point(x: Float, y: Float) = Offset(x * scaleX, y * scaleY)
        fun rect(x: Float, y: Float, width: Float, height: Float) = Size(width * scaleX, height * scaleY)

        drawRoundRect(
            color = folderColor,
            topLeft = point(90f, 40f),
            size = rect(90f, 40f, 72f, 12f),
            cornerRadius = CornerRadius(5f * scaleX, 5f * scaleY)
        )
        drawRoundRect(
            color = folderColor,
            topLeft = point(78f, 57f),
            size = rect(78f, 57f, 96f, 12f),
            cornerRadius = CornerRadius(5f * scaleX, 5f * scaleY)
        )
        drawRoundRect(
            color = folderColor,
            topLeft = point(65f, 73f),
            size = rect(65f, 73f, 122f, 87f),
            cornerRadius = CornerRadius(13f * scaleX, 13f * scaleY)
        )

        val play = Path().apply {
            moveTo(114f * scaleX, 103f * scaleY)
            lineTo(137f * scaleX, 117f * scaleY)
            lineTo(114f * scaleX, 137f * scaleY)
            close()
        }
        drawPath(path = play, color = darkColor)
    }
}
