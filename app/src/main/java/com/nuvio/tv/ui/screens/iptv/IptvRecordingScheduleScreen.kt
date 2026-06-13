package com.nuvio.tv.ui.screens.iptv

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.*
import com.streamvault.domain.model.RecordingRecurrence
import com.streamvault.domain.model.RecordingStorageConfig
import com.nuvio.tv.LocalPickFolderLauncher
import com.nuvio.tv.ui.theme.NuvioColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun IptvRecordingScheduleScreen(
    onBackPress: () -> Unit,
    viewModel: IptvRecordingViewModel = hiltViewModel()
) {
    val onPickFolder = LocalPickFolderLauncher.current
    val form         by viewModel.scheduleForm.collectAsStateWithLifecycle()
    val storage      by viewModel.storageState.collectAsStateWithLifecycle()
    val feedback     by viewModel.feedback.collectAsStateWithLifecycle()

    LaunchedEffect(feedback) {
        if (feedback != null) kotlinx.coroutines.delay(3_000)
        if (feedback != null) viewModel.clearFeedback()
    }

    Box(Modifier.fillMaxSize().background(NuvioColors.Background)) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 48.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                FocusableIconButton(Icons.Default.ArrowBack, "Retour", onClick = onBackPress)
                Spacer(Modifier.width(16.dp))
                Icon(Icons.Default.Schedule, null, tint = NuvioColors.Primary, modifier = Modifier.size(26.dp))
                Spacer(Modifier.width(10.dp))
                Text("Programmation enregistrement", color = NuvioColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            // Feedback
            feedback?.let { msg ->
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(if (msg.startsWith("Erreur")) Color(0x33EF5350) else Color(0x334CAF50))
                        .padding(12.dp)
                ) {
                    Text(msg, color = if (msg.startsWith("Erreur")) Color(0xFFEF5350) else Color(0xFF81C784), fontSize = 13.sp)
                }
            }

            // Section Chaine
            SectionTitle("Cha\u00EEne")

            ChannelSelectorCard(
                channelName = form.channelName,
                streamUrl   = form.streamUrl,
                onSelectChannel = { name, url, id, providerId ->
                    viewModel.updateForm { copy(channelName = name, streamUrl = url, channelId = id, providerId = providerId) }
                }
            )

            FocusableFormCard(
                label = "Titre du programme (optionnel)",
                value = form.programTitle,
                icon  = Icons.Default.Title
            )

            // Section Horaire
            SectionTitle("Horaire")

            DateTimeRow(
                label    = "D\u00E9but",
                calendar = form.startCalendar,
                onDateChange = { y, m, d ->
                    val cal = form.startCalendar.clone() as Calendar
                    cal.set(y, m, d)
                    viewModel.updateForm { copy(startCalendar = cal) }
                },
                onTimeChange = { h, min ->
                    val cal = form.startCalendar.clone() as Calendar
                    cal.set(Calendar.HOUR_OF_DAY, h)
                    cal.set(Calendar.MINUTE, min)
                    viewModel.updateForm { copy(startCalendar = cal) }
                }
            )

            DateTimeRow(
                label    = "Fin",
                calendar = form.endCalendar,
                onDateChange = { y, m, d ->
                    val cal = form.endCalendar.clone() as Calendar
                    cal.set(y, m, d)
                    viewModel.updateForm { copy(endCalendar = cal) }
                },
                onTimeChange = { h, min ->
                    val cal = form.endCalendar.clone() as Calendar
                    cal.set(Calendar.HOUR_OF_DAY, h)
                    cal.set(Calendar.MINUTE, min)
                    viewModel.updateForm { copy(endCalendar = cal) }
                }
            )

            // Section Recurrence
            SectionTitle("R\u00E9currence")
            RecurrencePicker(
                selected = form.recurrence,
                onSelect = { viewModel.updateForm { copy(recurrence = it) } }
            )

            // Section Stockage
            SectionTitle("Stockage")

            StorageFolderRow(
                displayName  = storage.displayName ?: storage.outputDirectory ?: "Non configur\u00E9",
                isWritable   = storage.isWritable,
                availableGb  = storage.availableBytes?.let { it / 1_073_741_824.0 },
                onPickFolder = onPickFolder
            )

            FocusableFormCard(
                label = "Nom de fichier (pattern)",
                value = storage.fileNamePattern,
                icon  = Icons.Default.TextFields
            )

            RetentionPicker(
                days     = storage.retentionDays,
                onChange = { days ->
                    viewModel.updateStorageConfig(
                        RecordingStorageConfig(
                            treeUri      = storage.treeUri,
                            displayName  = storage.displayName,
                            fileNamePattern = storage.fileNamePattern,
                            retentionDays   = days,
                            maxSimultaneousRecordings = storage.maxSimultaneousRecordings
                        )
                    )
                }
            )

            MaxSimultaneousPicker(
                count    = storage.maxSimultaneousRecordings,
                onChange = { n ->
                    viewModel.updateStorageConfig(
                        RecordingStorageConfig(
                            treeUri      = storage.treeUri,
                            displayName  = storage.displayName,
                            fileNamePattern = storage.fileNamePattern,
                            retentionDays   = storage.retentionDays,
                            maxSimultaneousRecordings = n
                        )
                    )
                }
            )

            Spacer(Modifier.height(8.dp))
            ScheduleButton(onClick = { viewModel.scheduleRecording() })
            Spacer(Modifier.height(32.dp))
        }
    }
}

// Carte focusable affichage seul (non editable sur TV)
@Composable
private fun FocusableFormCard(label: String, value: String, icon: ImageVector) {
    var focused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        if (focused) NuvioColors.Primary else NuvioColors.Border, tween(150), label = "border"
    )
    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NuvioColors.BackgroundCard)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .onFocusChanged { focused = it.hasFocus }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, null, tint = if (focused) NuvioColors.Primary else NuvioColors.TextTertiary, modifier = Modifier.size(18.dp))
        Column(Modifier.weight(1f)) {
            Text(label, color = NuvioColors.TextTertiary, fontSize = 10.sp)
            Text(
                text  = if (value.isBlank()) "\u2014" else value,
                color = if (value.isBlank()) NuvioColors.TextTertiary else NuvioColors.TextPrimary,
                fontSize = 14.sp
            )
        }
    }
}

// Selecteur de chaine : Provider > Groupe > Chaine
@Composable
private fun ChannelSelectorCard(
    channelName: String,
    streamUrl: String,
    onSelectChannel: (name: String, url: String, id: Long, providerId: Long) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    var focused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        if (focused) NuvioColors.Primary else NuvioColors.Border, tween(150), label = "channelBorder"
    )
    Card(
        onClick = { showPicker = true },
        shape = CardDefaults.shape(RoundedCornerShape(12.dp)),
        colors = CardDefaults.colors(
            containerColor = NuvioColors.BackgroundCard,
            focusedContainerColor = NuvioColors.BackgroundCard
        ),
        border = CardDefaults.border(
            border = androidx.tv.material3.Border(BorderStroke(1.5.dp, NuvioColors.Border)),
            focusedBorder = androidx.tv.material3.Border(BorderStroke(1.5.dp, NuvioColors.Primary))
        ),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.99f)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.LiveTv, null,
                tint = if (channelName.isBlank()) NuvioColors.TextTertiary else NuvioColors.Primary,
                modifier = Modifier.size(20.dp))
            Column(Modifier.weight(1f)) {
                Text("Cha\u00EEne s\u00E9lectionn\u00E9e", color = NuvioColors.TextTertiary, fontSize = 10.sp)
                Text(
                    text  = if (channelName.isBlank()) "Appuyer pour choisir un provider \u2192 groupe \u2192 cha\u00EEne" else channelName,
                    color = if (channelName.isBlank()) NuvioColors.TextTertiary else NuvioColors.TextPrimary,
                    fontSize = 14.sp
                )
                if (streamUrl.isNotBlank()) {
                    Text(streamUrl, color = NuvioColors.TextTertiary, fontSize = 10.sp, maxLines = 1)
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = NuvioColors.TextSecondary, modifier = Modifier.size(18.dp))
        }
    }
    if (showPicker) {
        IptvChannelPickerDialog(
            onDismiss = { showPicker = false },
            onChannelSelected = { name, url, id, providerId ->
                onSelectChannel(name, url, id, providerId)
                showPicker = false
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = NuvioColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 4.dp))
}

@Composable
private fun DateTimeRow(
    label: String,
    calendar: Calendar,
    onDateChange: (Int, Int, Int) -> Unit,
    onTimeChange: (Int, Int) -> Unit
) {
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault()) }
    var focused by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NuvioColors.BackgroundCard)
            .border(1.5.dp, if (focused) NuvioColors.Primary else NuvioColors.Border, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(Icons.Default.CalendarToday, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(18.dp))
        Column(Modifier.weight(1f)) {
            Text("Date et heure de $label", color = NuvioColors.TextTertiary, fontSize = 10.sp)
            Text(sdf.format(calendar.time), color = NuvioColors.TextPrimary, fontSize = 14.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            SmallAdjustButton("-1h") {
                val cal = calendar.clone() as Calendar; cal.add(Calendar.HOUR_OF_DAY, -1)
                onTimeChange(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
            }
            SmallAdjustButton("+1h") {
                val cal = calendar.clone() as Calendar; cal.add(Calendar.HOUR_OF_DAY, 1)
                onTimeChange(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
            }
            SmallAdjustButton("+1j") {
                val cal = calendar.clone() as Calendar; cal.add(Calendar.DAY_OF_MONTH, 1)
                onDateChange(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            }
        }
    }
}

@Composable
private fun SmallAdjustButton(label: String, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Card(
        onClick = onClick,
        modifier = Modifier.onFocusChanged { focused = it.hasFocus },
        shape = CardDefaults.shape(RoundedCornerShape(6.dp)),
        colors = CardDefaults.colors(
            containerColor = if (focused) NuvioColors.Primary else NuvioColors.BackgroundElevated,
            focusedContainerColor = NuvioColors.Primary
        ),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.95f)
    ) {
        Box(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(label, fontSize = 11.sp, color = if (focused) Color.Black else NuvioColors.TextSecondary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun RecurrencePicker(selected: RecordingRecurrence, onSelect: (RecordingRecurrence) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        RecordingRecurrence.values().forEach { rec ->
            val label = when (rec) {
                RecordingRecurrence.NONE   -> "Une fois"
                RecordingRecurrence.DAILY  -> "Quotidien"
                RecordingRecurrence.WEEKLY -> "Hebdo"
            }
            var focused by remember { mutableStateOf(false) }
            val isSelected = selected == rec
            Card(
                onClick = { onSelect(rec) },
                modifier = Modifier.onFocusChanged { focused = it.hasFocus },
                shape = CardDefaults.shape(RoundedCornerShape(10.dp)),
                colors = CardDefaults.colors(
                    containerColor = if (isSelected) NuvioColors.Primary else if (focused) NuvioColors.FocusBackground else NuvioColors.BackgroundCard,
                    focusedContainerColor = if (isSelected) NuvioColors.Primary else NuvioColors.FocusBackground
                ),
                border = CardDefaults.border(
                    border = androidx.tv.material3.Border(BorderStroke(1.5.dp, if (isSelected) NuvioColors.Primary else NuvioColors.Border)),
                    focusedBorder = androidx.tv.material3.Border(BorderStroke(1.5.dp, NuvioColors.Primary)),
                    pressedBorder = androidx.tv.material3.Border(BorderStroke(1.5.dp, NuvioColors.Primary))
                ),
                scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.97f)
            ) {
                Box(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text(label, fontSize = 13.sp,
                        color = if (isSelected) Color.Black else NuvioColors.TextPrimary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}

@Composable
private fun StorageFolderRow(
    displayName: String, isWritable: Boolean,
    availableGb: Double?, onPickFolder: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    Card(
        onClick = onPickFolder,
        modifier = Modifier.fillMaxWidth().onFocusChanged { focused = it.hasFocus },
        shape = CardDefaults.shape(RoundedCornerShape(12.dp)),
        colors = CardDefaults.colors(
            containerColor = if (focused) NuvioColors.FocusBackground else NuvioColors.BackgroundCard,
            focusedContainerColor = NuvioColors.Secondary
        ),
        border = CardDefaults.border(
            border = androidx.tv.material3.Border(BorderStroke(1.5.dp, if (focused) NuvioColors.Primary else NuvioColors.Border)),
            focusedBorder = androidx.tv.material3.Border(BorderStroke(1.5.dp, NuvioColors.Primary)),
            pressedBorder = androidx.tv.material3.Border(BorderStroke(1.5.dp, NuvioColors.Primary))
        ),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.98f)
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Folder, null,
                tint = if (isWritable) Color(0xFF4CAF50) else NuvioColors.Error,
                modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Dossier d\u2019enregistrement", color = NuvioColors.TextTertiary, fontSize = 10.sp)
                Text(displayName, color = NuvioColors.TextPrimary, fontSize = 13.sp, maxLines = 1)
                availableGb?.let {
                    Text("%.1f Go disponibles".format(it), color = NuvioColors.TextTertiary, fontSize = 11.sp)
                }
            }
            Icon(Icons.Default.Edit, "Changer", tint = NuvioColors.TextSecondary, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun RetentionPicker(days: Int?, onChange: (Int?) -> Unit) {
    val options = listOf(null, 7, 14, 30, 60, 90)
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.DeleteSweep, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("R\u00E9tention automatique", color = NuvioColors.TextTertiary, fontSize = 10.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            options.forEach { opt ->
                val label = opt?.let { "${it}j" } ?: "\u221E"
                val isSelected = days == opt
                var focused by remember { mutableStateOf(false) }
                Card(
                    onClick = { onChange(opt) },
                    modifier = Modifier.onFocusChanged { focused = it.hasFocus },
                    shape = CardDefaults.shape(RoundedCornerShape(8.dp)),
                    colors = CardDefaults.colors(
                        containerColor = if (isSelected) NuvioColors.Primary else if (focused) NuvioColors.FocusBackground else NuvioColors.BackgroundCard,
                        focusedContainerColor = if (isSelected) NuvioColors.Primary else NuvioColors.FocusBackground
                    ),
                    scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.95f)
                ) {
                    Box(Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        Text(label, fontSize = 12.sp, color = if (isSelected) Color.Black else NuvioColors.TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun MaxSimultaneousPicker(count: Int, onChange: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.FeaturedVideo, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("Enregistrements simultan\u00E9s max", color = NuvioColors.TextTertiary, fontSize = 10.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            (1..4).forEach { n ->
                val isSelected = count == n
                var focused by remember { mutableStateOf(false) }
                Card(
                    onClick = { onChange(n) },
                    modifier = Modifier.onFocusChanged { focused = it.hasFocus },
                    shape = CardDefaults.shape(RoundedCornerShape(8.dp)),
                    colors = CardDefaults.colors(
                        containerColor = if (isSelected) NuvioColors.Primary else if (focused) NuvioColors.FocusBackground else NuvioColors.BackgroundCard,
                        focusedContainerColor = if (isSelected) NuvioColors.Primary else NuvioColors.FocusBackground
                    ),
                    scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.95f)
                ) {
                    Box(Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        Text("$n", fontSize = 12.sp, color = if (isSelected) Color.Black else NuvioColors.TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleButton(onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp).onFocusChanged { focused = it.hasFocus },
        shape = CardDefaults.shape(RoundedCornerShape(14.dp)),
        colors = CardDefaults.colors(
            containerColor = if (focused) NuvioColors.Primary else Color(0xFF1E6B3C),
            focusedContainerColor = NuvioColors.Primary
        ),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.98f)
    ) {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(Icons.Default.Schedule, null, tint = if (focused) Color.Black else Color.White, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text("Planifier l\u2019enregistrement", color = if (focused) Color.Black else Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FocusableIconButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Card(
        onClick = onClick,
        modifier = Modifier.size(40.dp).onFocusChanged { focused = it.hasFocus },
        shape = CardDefaults.shape(RoundedCornerShape(10.dp)),
        colors = CardDefaults.colors(
            containerColor = if (focused) NuvioColors.FocusBackground else Color.Transparent,
            focusedContainerColor = NuvioColors.Secondary
        ),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.9f)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(icon, label, tint = if (focused) NuvioColors.Primary else NuvioColors.TextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}
