package com.nuvio.tv.ui.screens.iptv.tivi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.streamvault.domain.model.Program
import com.nuvio.tv.ui.screens.iptv.tivi.TiviEpgRow
import com.nuvio.tv.ui.theme.NuvioColors
import java.text.SimpleDateFormat
import java.util.*

private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
private val SLOT_WIDTH_DP = 160.dp
private val ROW_HEIGHT = 44.dp
private val LOGO_WIDTH = 56.dp
private val SLOT_MINUTES = 30L

@Composable
fun TiviEpgGrid(
    epgRows: List<TiviEpgRow>,
    focusedChannelId: Long?,
    modifier: Modifier = Modifier,
) {
    val sharedScroll = rememberScrollState()

    val now = remember { System.currentTimeMillis() }
    val slotStart = remember {
        Calendar.getInstance().apply {
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.HOUR_OF_DAY, -1)
        }.timeInMillis
    }
    val slots = remember {
        (0..7).map { i ->
            val t = slotStart + i * SLOT_MINUTES * 60_000
            timeFmt.format(Date(t))
        }
    }

    Column(modifier = modifier.background(NuvioColors.Background)) {

        // ── Time ruler ───────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .background(NuvioColors.BackgroundElevated)
                .horizontalScroll(sharedScroll),
        ) {
            Spacer(Modifier.width(LOGO_WIDTH))
            slots.forEach { slot ->
                Box(
                    modifier = Modifier
                        .width(SLOT_WIDTH_DP)
                        .fillMaxHeight()
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(slot, fontSize = 10.sp, color = NuvioColors.TextTertiary, fontWeight = FontWeight.Medium)
                }
            }
        }

        // ── Rows ─────────────────────────────────────────────────────────
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(epgRows, key = { it.channel.id }) { row ->
                TiviEpgChannelRow(
                    row = row,
                    isFocused = row.channel.id == focusedChannelId,
                    sharedScroll = sharedScroll,
                    slotStart = slotStart,
                    now = now,
                )
            }
        }
    }
}

@Composable
private fun TiviEpgChannelRow(
    row: TiviEpgRow,
    isFocused: Boolean,
    sharedScroll: androidx.compose.foundation.ScrollState,
    slotStart: Long,
    now: Long,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ROW_HEIGHT)
            .background(
                if (isFocused) NuvioColors.FocusBackground else Color.Transparent
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Logo colonne fixe
        Box(
            modifier = Modifier
                .width(LOGO_WIDTH)
                .fillMaxHeight()
                .background(NuvioColors.BackgroundElevated),
            contentAlignment = Alignment.Center,
        ) {
            if (!row.channel.logoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = row.channel.logoUrl,
                    contentDescription = row.channel.name,
                    modifier = Modifier.size(width = 42.dp, height = 22.dp),
                )
            } else {
                Text(
                    text = row.channel.name.take(4),
                    fontSize = 9.sp,
                    color = NuvioColors.TextTertiary,
                )
            }
        }

        // Programme blocks scrollables
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .horizontalScroll(sharedScroll),
        ) {
            if (row.programs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .width(SLOT_WIDTH_DP * 2)
                        .fillMaxHeight()
                        .padding(2.dp)
                        .background(NuvioColors.BackgroundCard, RoundedCornerShape(3.dp)),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        "No information",
                        fontSize = 10.sp,
                        color = NuvioColors.TextDisabled,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
            } else {
                row.programs.forEach { prog ->
                    TiviProgramBlock(
                        program = prog,
                        slotStart = slotStart,
                        now = now,
                        isFocusedRow = isFocused,
                    )
                }
            }
        }
    }
}

@Composable
private fun TiviProgramBlock(
    program: Program,
    slotStart: Long,
    now: Long,
    isFocusedRow: Boolean,
) {
    val msPerDp = (SLOT_MINUTES * 60_000).toFloat() / SLOT_WIDTH_DP.value
    val durationMs = (program.endTime - program.startTime).coerceAtLeast(1L)
    val blockWidthDp = (durationMs / msPerDp).dp.coerceAtLeast(60.dp)
    val isNow = program.startTime <= now && program.endTime > now

    Box(
        modifier = Modifier
            .width(blockWidthDp)
            .fillMaxHeight()
            .padding(horizontal = 1.dp, vertical = 3.dp)
            .background(
                when {
                    isNow && isFocusedRow -> NuvioColors.FocusBackground
                    isNow -> NuvioColors.BackgroundCard
                    isFocusedRow -> NuvioColors.BackgroundElevated
                    else -> NuvioColors.Background
                },
                RoundedCornerShape(3.dp)
            )
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = program.title,
            fontSize = 11.sp,
            color = if (isNow) NuvioColors.TextPrimary else NuvioColors.TextSecondary,
            fontWeight = if (isNow) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
