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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.LocalSidebarFocusHandler
import com.nuvio.tv.R
import com.nuvio.tv.data.freebox.FreeboxFileEntry
import com.nuvio.tv.data.freebox.freeboxContentIdForEntry
import com.nuvio.tv.data.freebox.freeboxFileNameOnly
import com.nuvio.tv.data.freebox.freeboxVideoDisplayTitle
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.ui.navigation.Screen
import com.nuvio.tv.ui.screens.home.ContinueWatchingItem
import com.nuvio.tv.ui.theme.NuvioColors
import com.nuvio.tv.ui.components.freeboxposter.FreeboxPosterPickerDialog
import com.nuvio.tv.ui.components.freeboxposter.FreeboxPosterPickerViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FreeboxVideosSection(
    entries: List<FreeboxFileEntry>,
    onItemClick: (FreeboxFileEntry) -> Unit,
    artworkMap: Map<String, String> = emptyMap(),
    backdropMap: Map<String, String> = emptyMap(),
    probedDurations: Map<String, Long> = emptyMap(),
    continueWatchingIds: Set<String> = emptySet(),
    cardWidth: Dp = 126.dp,
    imageHeight: Dp = 189.dp,
    horizontalPadding: Dp = 48.dp,
    itemSpacing: Dp = 16.dp,
    focusBorderPadding: Dp = 0.dp,
    onShowDetails: (FreeboxFileEntry) -> Unit = {},
    onRenameFreebox: (FreeboxFileEntry, String) -> Unit = { _, _ -> },
    onDeleteFromFreebox: (FreeboxFileEntry) -> Unit = {},
    modifier: Modifier = Modifier,
    posterPickerViewModel: FreeboxPosterPickerViewModel = hiltViewModel()
) {
    val requestSidebarFocus = LocalSidebarFocusHandler.current
    val posterPickerState by posterPickerViewModel.state.collectAsStateWithLifecycle()
    val filteredEntries = remember(entries, continueWatchingIds, probedDurations) {
        entries
            .filter { entry -> freeboxContentIdForEntry(entry) !in continueWatchingIds }
            .sortedWith(
                compareBy<FreeboxFileEntry> { entry ->
                    entry.durationMs ?: probedDurations[entry.path] ?: Long.MAX_VALUE
                }.thenBy { entry -> entry.name.lowercase() }
            )
    }
    if (filteredEntries.isEmpty()) return

    var optionsEntry by remember { mutableStateOf<FreeboxFileEntry?>(null) }
    var renameEntry by remember { mutableStateOf<FreeboxFileEntry?>(null) }

    Column(modifier = modifier) {
        Text(
            text = "Vid\u00e9os",
            style = MaterialTheme.typography.headlineMedium,
            color = NuvioColors.TextPrimary,
            modifier = Modifier.padding(start = horizontalPadding, end = horizontalPadding, bottom = 16.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = horizontalPadding + focusBorderPadding,
                end = horizontalPadding + focusBorderPadding,
                top = focusBorderPadding,
                bottom = focusBorderPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing)
        ) {
            itemsIndexed(filteredEntries, key = { _, e -> e.path }) { index, entry ->
                val contentId = freeboxContentIdForEntry(entry)
                val artwork = artworkMap[contentId]
                val backdrop = backdropMap[contentId] ?: artwork
                val cwItem = remember(entry, artwork, backdrop, probedDurations[entry.path]) {
                    ContinueWatchingItem.InProgress(
                        progress = WatchProgress(
                            contentId = contentId,
                            contentType = "freebox",
                            name = entry.name,
                            poster = artwork,
                            backdrop = backdrop,
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
                    modifier = Modifier,
                    cardWidth = cardWidth,
                    imageHeight = imageHeight,
                    showBadge = false,
                    showProgressBar = false,
                    cardSizeMultiplier = 1.5f,
                    onDirectionLeft = if (index == 0) {
                        { requestSidebarFocus(Screen.Freebox.route) }
                    } else {
                        null
                    }
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
            onChoosePoster = {
                posterPickerViewModel.open(
                    entry = menuEntry,
                    currentPosterUrl = artworkMap[freeboxContentIdForEntry(menuEntry)]
                )
                optionsEntry = null
            },
            onRenameFreebox = {
                renameEntry = menuEntry
                optionsEntry = null
            },
            onDeleteFromFreebox = {
                onDeleteFromFreebox(menuEntry)
                optionsEntry = null
            }
        )
    }

    val selectedRenameEntry = renameEntry
    if (selectedRenameEntry != null) {
        FreeboxRenameDialog(
            initialName = freeboxFileNameOnly(selectedRenameEntry.name),
            onDismiss = { renameEntry = null },
            onConfirm = { newName ->
                onRenameFreebox(selectedRenameEntry, newName)
                renameEntry = null
            }
        )
    }
    FreeboxPosterPickerDialog(
        state = posterPickerState,
        onSelect = posterPickerViewModel::select,
        onDismiss = posterPickerViewModel::dismiss
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FreeboxVideoOptionsDialog(
    entry: FreeboxFileEntry,
    onDismiss: () -> Unit,
    onShowDetails: () -> Unit,
    onChoosePoster: () -> Unit,
    onRenameFreebox: () -> Unit,
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
            onClick = onChoosePoster,
            colors = ButtonDefaults.colors(
                containerColor = NuvioColors.BackgroundCard,
                contentColor = NuvioColors.TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.freebox_choose_poster))
        }

        Button(
            onClick = onRenameFreebox,
            colors = ButtonDefaults.colors(
                containerColor = NuvioColors.BackgroundCard,
                contentColor = NuvioColors.TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.cw_action_rename))
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
