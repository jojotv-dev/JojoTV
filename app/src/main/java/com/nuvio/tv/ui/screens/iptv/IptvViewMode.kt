package com.nuvio.tv.ui.screens.iptv

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.GridView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.nuvio.tv.ui.theme.NuvioColors

enum class IptvViewMode { GRID, LIST }

@Composable
fun IptvViewModeToggle(
    viewMode: IptvViewMode,
    onToggle: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.FocusBackground else NuvioColors.BackgroundCard,
        animationSpec = tween(150), label = "toggleBg"
    )
    Card(
        onClick = onToggle,
        modifier = Modifier.size(36.dp).onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(RoundedCornerShape(8.dp)),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.92f)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (viewMode == IptvViewMode.GRID) Icons.AutoMirrored.Filled.List else Icons.Default.GridView,
                contentDescription = if (viewMode == IptvViewMode.GRID) "Vue liste" else "Vue grille",
                tint = NuvioColors.TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun IptvListRow(
    name: String,
    count: Int? = null,
    badge: String? = null,
    isFocused: Boolean,
    onFocusChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(12.dp)
    val bgColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.FocusBackground else NuvioColors.BackgroundCard,
        animationSpec = tween(150), label = "rowBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.Primary else Color.Transparent,
        animationSpec = tween(150), label = "rowBorder"
    )
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            
            .onFocusChanged { onFocusChange(it.hasFocus) },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.97f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            leadingContent?.invoke()
            Text(
                text = name,
                color = NuvioColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .then(if (isFocused) Modifier.basicMarquee() else Modifier)
            )
            if (count != null) {
                Box(
                    modifier = Modifier
                        .background(NuvioColors.BackgroundElevated, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(count.toString(), color = NuvioColors.TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .background(NuvioColors.BackgroundElevated, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(badge, color = NuvioColors.TextSecondary, fontSize = 11.sp)
                }
            }
            trailingContent?.invoke()
        }
    }
}
