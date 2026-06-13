package com.nuvio.tv.ui.screens.iptv

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.nuvio.tv.ui.theme.NuvioColors

@Composable
fun IptvProviderHomeScreen(
    providerName: String,
    onNavigateToLiveTv: () -> Unit,
    onNavigateToMovies: () -> Unit,
    onNavigateToSeries: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(NuvioColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(48.dp)
        ) {
            Text(
                text = providerName,
                color = NuvioColors.TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ContentTypeCard(
                    icon = Icons.Default.LiveTv,
                    label = "Live TV",
                    subtitle = "Chaînes en direct",
                    accentColor = Color(0xFF2196F3),
                    onClick = onNavigateToLiveTv,
                    modifier = Modifier.weight(1f)
                )
                ContentTypeCard(
                    icon = Icons.Default.Movie,
                    label = "Films",
                    subtitle = "Vidéo à la demande",
                    accentColor = Color(0xFFE91E63),
                    onClick = onNavigateToMovies,
                    modifier = Modifier.weight(1f)
                )
                ContentTypeCard(
                    icon = Icons.Default.Tv,
                    label = "Séries",
                    subtitle = "Épisodes & saisons",
                    accentColor = Color(0xFF9C27B0),
                    onClick = onNavigateToSeries,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ContentTypeCard(
    icon: ImageVector,
    label: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(20.dp)
    val bgColor by animateColorAsState(
        targetValue = if (isFocused) accentColor.copy(alpha = 0.2f) else NuvioColors.BackgroundCard,
        animationSpec = tween(200), label = "cardBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) accentColor else Color.Transparent,
        animationSpec = tween(200), label = "cardBorder"
    )
    val iconTint by animateColorAsState(
        targetValue = if (isFocused) accentColor else NuvioColors.TextSecondary,
        animationSpec = tween(200), label = "iconTint"
    )
    Card(
        onClick = onClick,
        modifier = modifier
            .height(200.dp)
            
            .onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = bgColor, focusedContainerColor = bgColor),
        border = CardDefaults.border(focusedBorder = Border(BorderStroke(2.dp, NuvioColors.FocusRing), inset = 0.dp, shape = shape), border = Border(BorderStroke(0.dp, Color.Transparent), inset = 0.dp, shape = shape)),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.97f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        if (isFocused) accentColor.copy(alpha = 0.3f) else NuvioColors.BackgroundElevated,
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(32.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(label, color = NuvioColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = NuvioColors.TextSecondary, fontSize = 12.sp)
        }
    }
}
