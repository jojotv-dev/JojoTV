package com.nuvio.tv.ui.screens.iptv
import androidx.compose.foundation.BorderStroke
import androidx.tv.material3.Border

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.nuvio.tv.ui.theme.NuvioColors

@Composable
fun IptvHomeScreen(
    onNavigateToProviderList: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToRecordings: () -> Unit,
    onNavigateToEpg: () -> Unit,
    onNavigateToDns: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(NuvioColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(48.dp)
        ) {
            Text(
                text = "IPTV",
                color = NuvioColors.TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Ligne 1 : Listes de lecture (pleine largeur)
            IptvHubCard(
                icon = Icons.AutoMirrored.Filled.List,
                label = "Listes de lecture",
                subtitle = "Gerer vos sources IPTV",
                accentColor = Color(0xFF2196F3),
                onClick = onNavigateToProviderList,
                modifier = Modifier.fillMaxWidth().height(140.dp)
            )

            // Ligne 2+3 : 4 boutons en grille 2x2
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth().height(160.dp)
            ) {
                IptvHubCard(
                    icon = Icons.Default.Schedule,
                    label = "Programmation",
                    subtitle = "Planifier un enregistrement",
                    accentColor = Color(0xFF00BCD4),
                    onClick = onNavigateToSchedule,
                    modifier = Modifier.weight(1f)
                )
                IptvHubCard(
                    icon = Icons.Default.FiberManualRecord,
                    label = "Enregistrements",
                    subtitle = "Gerer vos enregistrements",
                    accentColor = Color(0xFFF44336),
                    onClick = onNavigateToRecordings,
                    modifier = Modifier.weight(1f)
                )
                IptvHubCard(
                    icon = Icons.Default.Tv,
                    label = "EPG",
                    subtitle = "Guide des programmes",
                    accentColor = Color(0xFF9C27B0),
                    onClick = onNavigateToEpg,
                    modifier = Modifier.weight(1f)
                )
                IptvHubCard(
                    icon = Icons.Default.Dns,
                    label = "Serveur DNS",
                    subtitle = "Bientot disponible",
                    accentColor = Color(0xFF607D8B),
                    onClick = onNavigateToDns,
                    enabled = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun IptvHubCard(
    icon: ImageVector,
    label: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(20.dp)
    val effectiveAccent = if (enabled) accentColor else NuvioColors.TextTertiary
    val bgColor by animateColorAsState(
        targetValue = if (isFocused && enabled) effectiveAccent.copy(alpha = 0.2f) else NuvioColors.BackgroundCard,
        animationSpec = tween(200), label = "hubCardBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFocused && enabled) effectiveAccent else Color.Transparent,
        animationSpec = tween(200), label = "hubCardBorder"
    )
    val iconTint by animateColorAsState(
        targetValue = if (isFocused && enabled) effectiveAccent else NuvioColors.TextSecondary,
        animationSpec = tween(200), label = "hubIconTint"
    )
    Card(
        onClick = { if (enabled) onClick() },
        modifier = modifier
            .onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(
            containerColor = bgColor,
            focusedContainerColor = bgColor
        ),
        border = CardDefaults.border(
            focusedBorder = Border(BorderStroke(1.5.dp, borderColor), inset = 0.dp, shape = shape),
            border = Border(BorderStroke(1.5.dp, Color.Transparent), inset = 0.dp, shape = shape)
        ),
        scale = CardDefaults.scale(
            focusedScale = if (enabled) 1.03f else 1f,
            pressedScale = if (enabled) 0.97f else 1f
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (isFocused && enabled) effectiveAccent.copy(alpha = 0.3f)
                        else NuvioColors.BackgroundElevated,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(
                label,
                color = if (enabled) NuvioColors.TextPrimary else NuvioColors.TextTertiary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(3.dp))
            Text(
                subtitle,
                color = NuvioColors.TextSecondary,
                fontSize = 10.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}
