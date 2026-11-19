package com.nuvio.tv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.nuvio.tv.ui.theme.NuvioTheme

/**
 * Returns the source indices needed to keep a cyclic rail visually filled after its last item.
 * Widths omit inter-card spacing, so the result intentionally errs on the side of adding one
 * extra thumbnail instead of leaving a gap.
 */
internal fun cyclicThumbnailTailIndices(
    itemWidths: List<Dp>,
    viewportWidth: Dp
): List<Int> {
    if (itemWidths.isEmpty()) return emptyList()

    val safeWidths = itemWidths.map { it.takeIf { width -> width.value.isFinite() && width > 0.dp } ?: 1.dp }
    val targetWidth = viewportWidth.takeIf { it.value.isFinite() && it > 0.dp } ?: safeWidths.first()
    val indices = ArrayList<Int>()
    var coveredWidth = 0.dp
    while (coveredWidth < targetWidth && indices.size < 128) {
        val sourceIndex = indices.size % safeWidths.size
        indices += sourceIndex
        coveredWidth += safeWidths[sourceIndex]
    }
    return indices
}

/** The first card repeated at the end of a home rail, optionally focusable and clickable. */
@Composable
internal fun HomeCyclicThumbnailTail(
    imageUrl: String?,
    title: String,
    width: Dp,
    height: Dp,
    cornerRadius: Dp,
    showTitle: Boolean = true,
    focused: Boolean = false,
    showFreeboxFolderBadge: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val shape = NuvioTheme.configuredShape(RoundedCornerShape(cornerRadius))
    val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    val focusBorder = if (focused) {
        Modifier.border(NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs), shape)
    } else {
        Modifier
    }
    Column(Modifier.width(width).then(modifier).then(clickModifier)) {
        Box(
            Modifier.width(width)
                .height(height)
                .clip(shape)
                .background(
                    if (showFreeboxFolderBadge) androidx.compose.ui.graphics.Color.Transparent
                    else NuvioTheme.colors.BackgroundCard
                )
                .then(focusBorder),
        ) {
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(shape),
                )
            }
            if (showFreeboxFolderBadge) {
                FreeboxFolderBadge(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 4.dp, bottom = 4.dp)
                )
            }
        }
        if (showTitle) {
            Text(
                text = title,
                color = NuvioTheme.colors.TextPrimary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(width),
            )
        }
    }
}
