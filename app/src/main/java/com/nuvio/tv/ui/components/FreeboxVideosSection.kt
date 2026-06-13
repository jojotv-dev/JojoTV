package com.nuvio.tv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.data.freebox.FreeboxFileEntry
import com.nuvio.tv.data.freebox.freeboxDisplayName
import com.nuvio.tv.data.freebox.freeboxVideoDisplayTitle
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.ui.screens.home.ContinueWatchingItem
import com.nuvio.tv.ui.theme.NuvioColors

@Composable
fun FreeboxVideosSection(
    entries: List<FreeboxFileEntry>,
    onItemClick: (FreeboxFileEntry) -> Unit,
    modifier: Modifier = Modifier,
    cardWidth: Dp = 288.dp,
    imageHeight: Dp = 162.dp
) {
    if (entries.isEmpty()) return

    Column(modifier = modifier) {
        Text(
            text = "Vid\u00e9os",
            style = MaterialTheme.typography.headlineMedium,
            color = NuvioColors.TextPrimary,
            modifier = Modifier.padding(start = 48.dp, end = 48.dp, bottom = 16.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(entries, key = { _, e -> e.path }) { _, entry ->
                val cwItem = remember(entry) {
                    ContinueWatchingItem.InProgress(
                        progress = WatchProgress(
                            contentId = "freebox:${entry.path}",
                            contentType = "freebox",
                            name = freeboxDisplayName(entry.name),
                            poster = null,
                            backdrop = null,
                            logo = null,
                            videoId = "freebox:${entry.path}",
                            season = null,
                            episode = null,
                            episodeTitle = null,
                            position = 0L,
                            duration = entry.durationMs ?: 0L,
                            lastWatched = 0L
                        )
                    )
                }
                ContinueWatchingCard(
                    item = cwItem,
                    onClick = { onItemClick(entry) },
                    onLongPress = {},
                    cardWidth = cardWidth,
                    imageHeight = imageHeight
                )
            }
        }
    }
}
