package com.nuvio.tv.ui.screens.iptv

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.*
import com.nuvio.tv.ui.theme.NuvioColors

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun DeleteProviderConfirmDialog(
    providerName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(NuvioColors.BackgroundElevated)
                .padding(32.dp)
                .width(420.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Icone warning
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(NuvioColors.Error.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = NuvioColors.Error,
                        modifier = Modifier.size(28.dp),
                    )
                }

                Text(
                    text = "Supprimer la source",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NuvioColors.TextPrimary,
                )

                Text(
                    text = "Voulez-vous vraiment supprimer \u00ab\u00a0$providerName\u00a0\u00bb ?\nToutes les cha\u00eenes, films et s\u00e9ries associ\u00e9s seront supprim\u00e9s.",
                    fontSize = 13.sp,
                    color = NuvioColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // Bouton Annuler
                    var cancelFocused by remember { mutableStateOf(false) }
                    Surface(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ClickableSurfaceDefaults.colors(
                            containerColor = NuvioColors.BackgroundCard,
                            focusedContainerColor = NuvioColors.BackgroundCard,
                        ),
                        border = ClickableSurfaceDefaults.border(
                            focusedBorder = Border(BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp)),
                            border = Border(BorderStroke(1.dp, NuvioColors.Border), shape = RoundedCornerShape(12.dp)),
                        ),
                        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(12.dp)),
                        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.02f),
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "Annuler",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NuvioColors.TextPrimary,
                            )
                        }
                    }

                    // Bouton Supprimer
                    Surface(
                        onClick = { onConfirm(); onDismiss() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ClickableSurfaceDefaults.colors(
                            containerColor = NuvioColors.Error.copy(alpha = 0.85f),
                            focusedContainerColor = NuvioColors.Error,
                        ),
                        border = ClickableSurfaceDefaults.border(
                            focusedBorder = Border(BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp)),
                        ),
                        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(12.dp)),
                        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.02f),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(Icons.Default.Delete, null, tint = NuvioColors.TextPrimary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Supprimer",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NuvioColors.TextPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
}