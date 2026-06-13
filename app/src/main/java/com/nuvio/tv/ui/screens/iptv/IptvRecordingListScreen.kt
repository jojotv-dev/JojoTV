package com.nuvio.tv.ui.screens.iptv

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.*
import com.streamvault.domain.model.RecordingItem
import com.streamvault.domain.model.RecordingStatus
import com.nuvio.tv.ui.theme.NuvioColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun IptvRecordingListScreen(
    onPlayRecording: (streamUrl: String, title: String) -> Unit,
    onBackPress: () -> Unit,
    viewModel: IptvRecordingViewModel = hiltViewModel()
) {
    val recordings by viewModel.recordings.collectAsStateWithLifecycle()
    val storage    by viewModel.storageState.collectAsStateWithLifecycle()
    val feedback   by viewModel.feedback.collectAsStateWithLifecycle()

    LaunchedEffect(feedback) {
        if (feedback != null) kotlinx.coroutines.delay(3_000)
        if (feedback != null) viewModel.clearFeedback()
    }

    Box(Modifier.fillMaxSize().background(NuvioColors.Background)) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 48.dp, vertical = 32.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 20.dp)) {
                    var focused by remember { mutableStateOf(false) }
                    Card(
                        onClick = onBackPress,
                        modifier = Modifier.size(40.dp).onFocusChanged { focused = it.hasFocus },
                        shape = CardDefaults.shape(RoundedCornerShape(10.dp)),
                        colors = CardDefaults.colors(
                            containerColor = if (focused) NuvioColors.FocusBackground else Color.Transparent,
                            focusedContainerColor = NuvioColors.Secondary
                        ),
                        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.9f)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ArrowBack, "Retour", tint = if (focused) NuvioColors.Primary else NuvioColors.TextSecondary, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Icon(Icons.Default.VideoLibrary, null, tint = NuvioColors.Primary, modifier = Modifier.size(26.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Enregistrements", color = NuvioColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Bande d'info stockage
            item {
                StorageInfoBand(
                    folder     = storage.displayName ?: storage.outputDirectory ?: "Non configur�",
                    isWritable = storage.isWritable,
                    availableGb = storage.availableBytes?.let { it / 1_073_741_824.0 }
                )
                Spacer(Modifier.height(16.dp))
            }

            // Feedback
            feedback?.let { msg ->
                item {
                    Box(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                            .background(if (msg.startsWith("Erreur")) Color(0x33EF5350) else Color(0x334CAF50))
                            .padding(12.dp)
                    ) {
                        Text(msg, color = if (msg.startsWith("Erreur")) Color(0xFFEF5350) else Color(0xFF81C784), fontSize = 13.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            if (recordings.isEmpty()) {
                item { EmptyRecordingsHint() }
            } else {
                // Grouper par statut : en cours / planifi�s / terminés
                val active    = recordings.filter { it.status == RecordingStatus.RECORDING }
                val scheduled = recordings.filter { it.status == RecordingStatus.SCHEDULED }
                val done      = recordings.filter { it.status in listOf(RecordingStatus.COMPLETED, RecordingStatus.FAILED, RecordingStatus.CANCELLED) }
                    .sortedByDescending { it.terminalAtMs ?: it.scheduledEndMs }

                if (active.isNotEmpty()) {
                    item { GroupHeader("En cours", active.size) }
                    items(active, key = { it.id }) { rec ->
                        RecordingCard(rec,
                            onCancel = { viewModel.cancelRecording(rec.id) },
                            onDelete = null,
                            onPlay   = null
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
                if (scheduled.isNotEmpty()) {
                    item { GroupHeader("planifi�s", scheduled.size) }
                    items(scheduled, key = { it.id }) { rec ->
                        RecordingCard(rec,
                            onCancel = { viewModel.cancelRecording(rec.id) },
                            onDelete = null,
                            onPlay   = null
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
                if (done.isNotEmpty()) {
                    item { GroupHeader("Historique", done.size) }
                    items(done, key = { it.id }) { rec ->
                        RecordingCard(rec,
                            onCancel = null,
                            onDelete = { viewModel.deleteRecording(rec.id) },
                            onPlay   = if (rec.status == RecordingStatus.COMPLETED && rec.streamUrl.isNotBlank())
                                           {{ onPlayRecording(rec.streamUrl, rec.channelName) }}
                                       else null
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun StorageInfoBand(folder: String, isWritable: Boolean, availableGb: Double?) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(NuvioColors.BackgroundCard)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Default.Folder, null,
            tint = if (isWritable) Color(0xFF4CAF50) else NuvioColors.Error,
            modifier = Modifier.size(18.dp))
        Text(folder, color = NuvioColors.TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        availableGb?.let { Text("%.1f Go libres".format(it), color = NuvioColors.TextTertiary, fontSize = 11.sp) }
    }
}

@Composable
private fun GroupHeader(label: String, count: Int) {
    Row(Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = NuvioColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Box(Modifier.clip(RoundedCornerShape(4.dp)).background(NuvioColors.BackgroundElevated).padding(horizontal = 6.dp, vertical = 2.dp)) {
            Text("$count", color = NuvioColors.TextTertiary, fontSize = 10.sp)
        }
    }
}

@Composable
private fun RecordingCard(
    rec: RecordingItem,
    onCancel: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    onPlay:   (() -> Unit)?
) {
    var focused by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(if (focused) NuvioColors.FocusBackground else NuvioColors.BackgroundCard, tween(150), label = "recBg")
    Card(
        onClick = { onPlay?.invoke() ?: onCancel?.invoke() },
        modifier = Modifier.fillMaxWidth().onFocusChanged { focused = it.hasFocus },
        shape = CardDefaults.shape(RoundedCornerShape(14.dp)),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.98f)
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            // Ic�ne statut
            Icon(
                statusIcon(rec.status), null,
                tint = statusColor(rec.status),
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(rec.channelName, color = NuvioColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                rec.programTitle?.let {
                    Text(it, color = NuvioColors.TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val sdf = remember { SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()) }
                    Text(sdf.format(Date(rec.scheduledStartMs)), color = NuvioColors.TextTertiary, fontSize = 11.sp)
                    Text("→ ${sdf.format(Date(rec.scheduledEndMs))}", color = NuvioColors.TextTertiary, fontSize = 11.sp)
                    if (rec.bytesWritten > 0) {
                        Text(formatBytes(rec.bytesWritten), color = NuvioColors.TextTertiary, fontSize = 11.sp)
                    }
                }
                rec.failureReason?.let {
                    Text(it, color = NuvioColors.Error, fontSize = 10.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
            Spacer(Modifier.width(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                onPlay?.let {
                    SmallIconBtn(Icons.Default.PlayArrow, "Lire", NuvioColors.Primary, it)
                }
                onCancel?.let {
                    SmallIconBtn(Icons.Default.Stop, "Annuler", NuvioColors.TextSecondary, it)
                }
                onDelete?.let {
                    SmallIconBtn(Icons.Default.Delete, "Supprimer", NuvioColors.Error, it)
                }
            }
        }
    }
}

@Composable
private fun SmallIconBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Card(
        onClick = onClick,
        modifier = Modifier.size(36.dp).onFocusChanged { focused = it.hasFocus },
        shape = CardDefaults.shape(RoundedCornerShape(8.dp)),
        colors = CardDefaults.colors(
            containerColor = if (focused) NuvioColors.FocusBackground else Color.Transparent,
            focusedContainerColor = NuvioColors.Secondary
        ),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.9f)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(icon, label, tint = tint, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun EmptyRecordingsHint() {
    Column(
        Modifier.fillMaxWidth().padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(Icons.Default.VideoLibrary, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(48.dp))
        Text("Aucun enregistrement", color = NuvioColors.TextSecondary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Text("Planifiez un enregistrement depuis l'�cran Programmation.", color = NuvioColors.TextTertiary, fontSize = 12.sp)
    }
}

private fun statusIcon(status: RecordingStatus) = when (status) {
    RecordingStatus.RECORDING  -> Icons.Default.FiberManualRecord
    RecordingStatus.SCHEDULED  -> Icons.Default.Schedule
    RecordingStatus.COMPLETED  -> Icons.Default.CheckCircle
    RecordingStatus.FAILED     -> Icons.Default.Error
    RecordingStatus.CANCELLED  -> Icons.Default.Cancel
}

private fun statusColor(status: RecordingStatus): Color = when (status) {
    RecordingStatus.RECORDING  -> Color(0xFFEF5350)
    RecordingStatus.SCHEDULED  -> Color(0xFF64B5F6)
    RecordingStatus.COMPLETED  -> Color(0xFF4CAF50)
    RecordingStatus.FAILED     -> Color(0xFFFF7043)
    RecordingStatus.CANCELLED  -> Color(0xFF9E9E9E)
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1_073_741_824L -> "%.1f Go".format(bytes / 1_073_741_824.0)
        bytes >= 1_048_576L     -> "%.0f Mo".format(bytes / 1_048_576.0)
        else                    -> "${bytes / 1024} Ko"
    }
}
