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
import com.nuvio.tv.data.freebox.freeboxVideoDisplayTitle
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.ui.screens.home.ContinueWatchingItem
import com.nuvio.tv.ui.theme.NuvioColors

@Composable
fun FreeboxVideosSection(
    entries: List<FreeboxFileEntry>,
    onItemClick: (FreeboxFileEntry) -> Unit,
    artworkMap: Map<String, String> = emptyMap(),
    continueWatchingIds: Set<String> = emptySet(),
    cardWidth: Dp = 126.dp,
    imageHeight: Dp = 189.dp,
    horizontalPadding: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    val filteredEntries = remember(entries, continueWatchingIds) {
        entries.filter { entry -> "freebox:${entry.path}" !in continueWatchingIds }
    }

    if (filteredEntries.isEmpty()) return

    Column(modifier = modifier) {
        Text(
            text = "Vid\u00e9os",
            style = MaterialTheme.typography.headlineMedium,
            color = NuvioColors.TextPrimary,
            modifier = Modifier.padding(start = horizontalPadding, end = horizontalPadding, bottom = 16.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(filteredEntries, key = { _, e -> e.path }) { _, entry ->
                val contentId = "freebox:${entry.path}"
                val artwork = artworkMap[contentId]
                val title = remember(entry) {
                    com.nuvio.tv.data.freebox.freeboxVideoDisplayTitle(entry.name, entry.durationMs)
                }
                val cwItem = remember(entry, artwork) {
                    ContinueWatchingItem.InProgress(
                        progress = WatchProgress(
                            contentId = contentId,
                            contentType = "freebox",
                            name = title,
                            poster = artwork,
                            backdrop = artwork,
                            logo = null,
                            videoId = contentId,
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
