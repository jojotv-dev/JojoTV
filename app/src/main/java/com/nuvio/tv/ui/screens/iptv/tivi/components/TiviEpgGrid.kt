package com.nuvio.tv.ui.screens.iptv.tivi.components

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
internal val TIVI_EPG_BACKGROUND = Color(0xFF0F0F1A)
internal val TIVI_EPG_SURFACE = Color(0xFF1A1A2E)
internal val TIVI_EPG_MUTED_TEXT = Color(0xFF4A4A6A)
internal val TIVI_EPG_ROW_HEIGHT = 56.dp
internal val TIVI_EPG_HEADER_HEIGHT = 28.dp
internal val TIVI_EPG_VERTICAL_PADDING = 4.dp
private val LOGO_WIDTH = 56.dp
private val SLOT_MINUTES = 30L

@Composable
fun TiviEpgGrid(
    epgRows: List<TiviEpgRow>,
    focusedChannelId: Long?,
    verticalListState: LazyListState = rememberLazyListState(),
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

    Column(modifier = modifier.background(TIVI_EPG_BACKGROUND)) {

        // ── Time ruler ───────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(TIVI_EPG_HEADER_HEIGHT)
                .background(TIVI_EPG_SURFACE)
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
        LazyColumn(
            state = verticalListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = TIVI_EPG_VERTICAL_PADDING),
            userScrollEnabled = false,
        ) {
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
    val accent = androidx.tv.material3.MaterialTheme.colorScheme.primary
    val selectedBackground = accent.copy(alpha = 0.14f)
    val focusedCellBackground = accent.copy(alpha = 0.12f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(TIVI_EPG_ROW_HEIGHT)
            .background(
                if (isFocused) selectedBackground else TIVI_EPG_BACKGROUND
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Logo colonne fixe
        Box(
            modifier = Modifier
                .width(LOGO_WIDTH)
                .fillMaxHeight()
                .background(if (isFocused) selectedBackground else TIVI_EPG_SURFACE),
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
                        .background(
                            if (isFocused) focusedCellBackground else TIVI_EPG_SURFACE,
                            RoundedCornerShape(3.dp),
                        )
                        .then(
                            if (isFocused) {
                                Modifier.border(2.dp, accent, RoundedCornerShape(3.dp))
                            } else {
                                Modifier
                            }
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "\u2014",
                        fontSize = 14.sp,
                        color = TIVI_EPG_MUTED_TEXT,
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
    val accent = androidx.tv.material3.MaterialTheme.colorScheme.primary
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
                    isFocusedRow -> accent.copy(alpha = 0.12f)
                    isNow -> TIVI_EPG_SURFACE
                    else -> TIVI_EPG_BACKGROUND
                },
                RoundedCornerShape(3.dp)
            )
            .then(
                if (isFocusedRow) {
                    Modifier.border(2.dp, accent, RoundedCornerShape(3.dp))
                } else {
                    Modifier
                }
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
