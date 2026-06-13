package com.nuvio.tv.ui.screens.iptv

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.Text
import com.nuvio.tv.ui.theme.NuvioColors

@Composable
fun IptvProviderTypeSelectScreen(
    onSelectXtream: () -> Unit,
    onSelectM3u: () -> Unit,
    onSelectStalker: () -> Unit,
    onBackPress: () -> Unit
) {
    val focusRing = NuvioColors.FocusRing
    val focusBg = NuvioColors.FocusBackground
    val bgCard = NuvioColors.BackgroundCard
    val bg = NuvioColors.Background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 64.dp, vertical = 48.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 48.dp)
            ) {
                IconButton(onClick = onBackPress) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = NuvioColors.TextPrimary
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    "Choisir le type de source",
                    color = NuvioColors.TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ProviderTypeCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Link,
                    title = "M3U / URL",
                    onClick = onSelectM3u
                )
                ProviderTypeCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Tv,
                    title = "Xtream Codes",
                    onClick = onSelectXtream
                )
                ProviderTypeCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Router,
                    title = "Stalker Portal",
                    onClick = onSelectStalker
                )
            }
        }
    }
}

@Composable
private fun ProviderTypeCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(16.dp)
    val focusRing = NuvioColors.FocusRing
    val focusBg = NuvioColors.FocusBackground
    val bgCard = NuvioColors.BackgroundCard
    val border = NuvioColors.Border

    Card(
        onClick = onClick,
        modifier = modifier
            .height(160.dp)
            .onFocusChanged { isFocused = it.isFocused },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(
            containerColor = if (isFocused) focusBg else bgCard,
            focusedContainerColor = focusBg
        ),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.97f),
        border = CardDefaults.border(border = androidx.tv.material3.Border(androidx.compose.foundation.BorderStroke(2.dp, border)), focusedBorder = androidx.tv.material3.Border(androidx.compose.foundation.BorderStroke(2.dp, focusRing)), pressedBorder = androidx.tv.material3.Border(androidx.compose.foundation.BorderStroke(2.dp, focusRing))),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isFocused) focusRing else NuvioColors.TextSecondary,
                modifier = Modifier.size(44.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = title,
                color = if (isFocused) NuvioColors.TextPrimary else NuvioColors.TextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
