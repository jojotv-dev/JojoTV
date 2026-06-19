package com.nuvio.tv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.data.freebox.FreeboxFileEntry
import com.nuvio.tv.data.freebox.freeboxVideoDisplayTitle
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.ui.screens.home.ContinueWatchingItem
import com.nuvio.tv.ui.theme.NuvioColors

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FreeboxVideosSection(
    entries: List<FreeboxFileEntry>,
    onItemClick: (FreeboxFileEntry) -> Unit,
    artworkMap: Map<String, String> = emptyMap(),
    probedDurations: Map<String, Long> = emptyMap(),
    continueWatchingIds: Set<String> = emptySet(),
    cardWidth: Dp = 126.dp,
    imageHeight: Dp = 189.dp,
    horizontalPadding: Dp = 48.dp,
    onShowDetails: (FreeboxFileEntry) -> Unit = {},
    onDeleteFromFreebox: (FreeboxFileEntry) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val filteredEntries = remember(entries, continueWatchingIds) {
        entries.filter { entry -> "freebox:${entry.path}" !in continueWatchingIds }
    }
    if (filteredEntries.isEmpty()) return

    var optionsEntry by remember { mutableStateOf<FreeboxFileEntry?>(null) }

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
                val cwItem = remember(entry, artwork, probedDurations[entry.path]) {
                    ContinueWatchingItem.InProgress(
                        progress = WatchProgress(
                            contentId = contentId,
                            contentType = "freebox",
                            name = entry.name,
                            poster = artwork,
                            backdrop = artwork,
                            logo = null,
                            videoId = contentId,
                            season = null,
                            episode = null,
                            episodeTitle = null,
                            position = 0L,
                            duration = entry.durationMs ?: probedDurations[entry.path] ?: 0L,
                            lastWatched = 0L
                        )
                    )
                }
                ContinueWatchingCard(
                    item = cwItem,
                    onClick = { onItemClick(entry) },
                    onLongPress = { optionsEntry = entry },
                    cardWidth = cardWidth,
                    imageHeight = imageHeight,
                    showBadge = false
                )
            }
        }
    }

    val menuEntry = optionsEntry
    if (menuEntry != null) {
        FreeboxVideoOptionsDialog(
            entry = menuEntry,
            onDismiss = { optionsEntry = null },
            onShowDetails = {
                onShowDetails(menuEntry)
                optionsEntry = null
            },
            onDeleteFromFreebox = {
                onDeleteFromFreebox(menuEntry)
                optionsEntry = null
            }
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FreeboxVideoOptionsDialog(
    entry: FreeboxFileEntry,
    onDismiss: () -> Unit,
    onShowDetails: () -> Unit,
    onDeleteFromFreebox: () -> Unit
) {
    val title = remember(entry) {
        freeboxVideoDisplayTitle(entry.name, entry.durationMs)
    }

    val detailsFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        detailsFocusRequester.requestFocus()
    }

    NuvioDialog(
        onDismiss = onDismiss,
        title = title,
        subtitle = stringResource(R.string.cw_dialog_subtitle)
    ) {
        Button(
            onClick = onShowDetails,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(detailsFocusRequester),
            colors = ButtonDefaults.colors(
                containerColor = NuvioColors.BackgroundCard,
                contentColor = NuvioColors.TextPrimary
            )
        ) {
            Text(stringResource(R.string.cw_action_go_to_details))
        }

        Button(
            onClick = onDeleteFromFreebox,
            colors = ButtonDefaults.colors(
                containerColor = NuvioColors.BackgroundCard,
                contentColor = Color(0xFFFF6E6E)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.cw_action_delete_freebox))
        }
    }
}
