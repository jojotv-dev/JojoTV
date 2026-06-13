package com.nuvio.tv.ui.screens.freebox

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.focusable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.content.FileProvider
import androidx.tv.material3.Button
import androidx.tv.material3.Border
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.data.freebox.FreeboxFileEntry
import com.nuvio.tv.data.freebox.freeboxContentIdForPath
import com.nuvio.tv.data.freebox.freeboxDisplayName
import com.nuvio.tv.data.freebox.freeboxFileNameOnly
import com.nuvio.tv.data.freebox.freeboxVideoDisplayTitle
import com.nuvio.tv.ui.screens.settings.FreeboxBrowserViewModel
import com.nuvio.tv.ui.screens.settings.FreeboxFileOpenRequest
import com.nuvio.tv.ui.screens.settings.FreeboxPhotoRequest
import com.nuvio.tv.ui.screens.settings.FreeboxPlaybackRequest
import com.nuvio.tv.ui.screens.settings.SettingsActionRow
import com.nuvio.tv.ui.screens.settings.SettingsDetailHeader
import com.nuvio.tv.ui.screens.settings.SettingsGroupCard
import com.nuvio.tv.ui.screens.settings.SettingsVerticalScrollIndicators
import com.nuvio.tv.ui.theme.NuvioColors
import com.nuvio.tv.ui.components.NuvioDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.rememberAsyncImagePainter
import com.nuvio.tv.ui.util.rememberLongPressKeyTracker
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.icons.filled.Movie
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.basicMarquee
import androidx.compose.ui.graphics.Brush

@Composable
fun FreeboxBrowserScreen(
    showBuiltInHeader: Boolean = true,
    initialFolderName: String? = null,
    onPlayFile: (FreeboxPlaybackRequest) -> Unit = {},
    onOpenPhoto: (FreeboxPhotoRequest) -> Unit = {},
    viewModel: FreeboxBrowserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var previewPhoto by remember { mutableStateOf<FreeboxPhotoRequest?>(null) }
    var contextMenuEntry by remember { mutableStateOf<FreeboxFileEntry?>(null) }
    val sortMode = remember(uiState.sortMode) { FreeboxSortMode.fromName(uiState.sortMode) }
    val showExtensions = uiState.showExtensions
    val sortedEntries = remember(uiState.entries, uiState.knownVideoDurations, sortMode) {
        uiState.entries.sortedWith(freeboxEntryComparator(sortMode, uiState.knownVideoDurations))
    }

    LaunchedEffect(initialFolderName) {
        if (!initialFolderName.isNullOrBlank()) {
            viewModel.openRootFolder(initialFolderName)
        }
    }

    BackHandler(enabled = previewPhoto != null || viewModel.canNavigateUp) {
        if (previewPhoto != null) {
            previewPhoto = null
        } else {
            viewModel.navigateUp()
        }
    }

    contextMenuEntry?.let { entry ->
        FreeboxDeleteDialog(
            entry = entry,
            onDismiss = { contextMenuEntry = null },
            onDelete = {
                viewModel.deleteFromFreebox(entry)
                contextMenuEntry = null
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showBuiltInHeader) {
            SettingsDetailHeader(
                title = if (uiState.breadcrumb.isEmpty()) "Freebox" else uiState.breadcrumb.last(),
                subtitle = if (uiState.hasSession) {
                    stringResource(R.string.freebox_browser_connected_subtitle)
                } else {
                    stringResource(R.string.freebox_browser_not_connected_subtitle)
                }
            )
        }

        SettingsGroupCard(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.errorMessage != null -> {
                        val errorMessage = uiState.errorMessage.orEmpty()
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    uiState.entries.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.freebox_browser_empty),
                                style = MaterialTheme.typography.bodyLarge,
                                color = NuvioColors.TextSecondary
                            )
                        }
                    }

                    else -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FreeboxBrowserToolbar(
                                breadcrumb = listOf("Freebox") + uiState.breadcrumb,
                                sortMode = sortMode,
                                onSelectSortMode = { selected -> viewModel.setSortMode(selected.name) }
                            )

                            previewPhoto?.let { photo ->
                                FreeboxPhotoPreviewPanel(
                                    photo = photo,
                                    onOpenFullScreen = { onOpenPhoto(photo) },
                                    onPrevious = {
                                        coroutineScope.launch {
                                            previewPhoto = adjacentPhotoRequest(uiState.entries, photo, -1, viewModel) ?: photo
                                        }
                                    },
                                    onNext = {
                                        coroutineScope.launch {
                                            previewPhoto = adjacentPhotoRequest(uiState.entries, photo, 1, viewModel) ?: photo
                                        }
                                    }
                                )
                            }

                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (!uiState.isAtRoot) {
                                        val parentLabel = if (uiState.breadcrumb.size <= 1) "Freebox" else uiState.breadcrumb[uiState.breadcrumb.size - 2]

                                    }

                                    items(sortedEntries, key = { it.path }) { entry ->
                                        FreeboxEntryRow(
                                            entry = entry,
                                            knownDuration = uiState.knownVideoDurations[freeboxContentIdForPath(entry.path)],
                                            knownRemainingMs = uiState.knownPositions[freeboxContentIdForPath(entry.path)],
                                            artworkUrl = uiState.videoArtwork[freeboxContentIdForPath(entry.path)],
                                            meta = uiState.videoMetadata[freeboxContentIdForPath(entry.path)],
                                            showExtensions = showExtensions,
                                            onClick = {
                                                handleFreeboxEntryClick(
                                                    entry = entry,
                                                    viewModel = viewModel,
                                                    coroutineScope = coroutineScope,
                                                    currentPreview = previewPhoto,
                                                    onPreviewPhoto = { previewPhoto = it },
                                                    onOpenPhoto = onOpenPhoto,
                                                    onOpenFile = { openCachedFreeboxFile(context, it) },
                                                    onPlayFile = onPlayFile
                                                )
                                            },
                                            onLongPress = { contextMenuEntry = entry }
                                        )
                                    }
                        }
                            }
                    }
                }
            }
        }
    }
}


@Composable
private fun FreeboxDeleteDialog(
    entry: FreeboxFileEntry,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val name = freeboxDisplayName(entry.name)
    NuvioDialog(
        onDismiss = onDismiss,
        title = name,
        subtitle = "Supprimer ce fichier de la Freebox ?"
    ) {
        val confirmFocus = remember { FocusRequester() }
        LaunchedEffect(Unit) { confirmFocus.requestFocus() }
        Button(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth().focusRequester(confirmFocus),
            colors = ButtonDefaults.colors(
                containerColor = NuvioColors.BackgroundCard,
                contentColor = Color(0xFFFF6E6E)
            )
        ) { Text("Supprimer de la Freebox") }
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.colors(
                containerColor = NuvioColors.BackgroundCard,
                contentColor = NuvioColors.TextPrimary
            )
        ) { Text("Annuler") }
    }
}

private fun formatFreeboxSize(bytes: Long): String = when {
    bytes >= 1_073_741_824L -> "%.1f Go".format(bytes / 1_073_741_824.0)
    bytes >= 1_048_576L -> "%.1f Mo".format(bytes / 1_048_576.0)
    bytes >= 1_024L -> "%.0f Ko".format(bytes / 1_024.0)
    else -> "$bytes o"
}





@Composable
private fun FreeboxBrowserToolbar(
    breadcrumb: List<String>,
    sortMode: FreeboxSortMode,
    onSelectSortMode: (FreeboxSortMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = breadcrumb.filter { it.isNotBlank() }.joinToString(" / "),
            style = MaterialTheme.typography.bodyMedium,
            color = NuvioColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        FreeboxSortDropdown(
            selectedSortMode = sortMode,
            onSelectSortMode = onSelectSortMode,
            modifier = Modifier.width(220.dp)
        )
    }
}

@Composable
private fun FreeboxSortDropdown(
    selectedSortMode: FreeboxSortMode,
    onSelectSortMode: (FreeboxSortMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    BackHandler(enabled = expanded) { expanded = false }

    Box(modifier = modifier) {
        FreeboxToolbarButton(
            label = "Tri: ${selectedSortMode.label}",
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(260.dp)
                .background(NuvioColors.BackgroundElevated, RoundedCornerShape(18.dp))
                .padding(8.dp),
            properties = PopupProperties(focusable = true),
            shape = RoundedCornerShape(18.dp),
            containerColor = NuvioColors.BackgroundElevated,
            tonalElevation = 0.dp,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, NuvioColors.Border)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FreeboxSortMode.entries.forEach { mode ->
                    FreeboxSortMenuItem(
                        label = mode.label,
                        selected = mode == selectedSortMode,
                        onClick = {
                            expanded = false
                            onSelectSortMode(mode)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FreeboxSortMenuItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = ButtonDefaults.shape(RoundedCornerShape(14.dp)),
        colors = ButtonDefaults.colors(
            containerColor = if (selected) NuvioColors.SurfaceVariant else Color.Transparent,
            focusedContainerColor = NuvioColors.Secondary,
            pressedContainerColor = NuvioColors.Secondary,
            contentColor = if (selected) NuvioColors.TextPrimary else NuvioColors.TextSecondary,
            focusedContentColor = NuvioColors.OnSecondary,
            pressedContentColor = NuvioColors.OnSecondary
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Unspecified,
                maxLines = 1
            )
        }
    }
}
@Composable
private fun FreeboxToolbarButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.colors(
            containerColor = NuvioColors.Surface,
            focusedContainerColor = NuvioColors.Secondary,
            contentColor = NuvioColors.TextPrimary,
            focusedContentColor = NuvioColors.OnSecondary
        )
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun FreeboxEntryRow(
    entry: FreeboxFileEntry,
    knownDuration: Long?,
    knownRemainingMs: Long?,
    artworkUrl: String?,
    meta: com.nuvio.tv.core.tmdb.FreeboxVideoMeta?,
    showExtensions: Boolean = false,
    onClick: () -> Unit,
    onLongPress: (() -> Unit)? = null
) {
    val fileName = freeboxDisplayName(entry.name, showExtensions)
    if (entry.isDirectory) {
        SettingsActionRow(
            title = fileName,
            subtitle = null,
            onClick = onClick,
            onLongClick = onLongPress,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = Icons.Default.Folder
        )
    } else {
        FreeboxVideoRow(
            title = freeboxVideoDisplayTitle(fileName, knownDuration, showExtensions),
            remainingMs = if (knownRemainingMs != null && knownRemainingMs > 0L) knownRemainingMs else null,
            artworkUrl = artworkUrl,
            meta = meta,
            onClick = onClick,
            onLongPress = onLongPress
        )
    }
}

@Composable
private fun FreeboxVideoRow(
    title: String,
    remainingMs: Long?,
    artworkUrl: String?,
    meta: com.nuvio.tv.core.tmdb.FreeboxVideoMeta?,
    onClick: () -> Unit,
    onLongPress: (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    var longPressTriggered by remember { mutableStateOf(false) }
    val longPressKeyTracker = rememberLongPressKeyTracker()
    val focusRing = NuvioColors.FocusRing
    val focusBg = NuvioColors.FocusBackground
    val secondary = NuvioColors.Secondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .onPreviewKeyEvent { event ->
                val native = event.nativeKeyEvent
                if (onLongPress != null) {
                    if (longPressKeyTracker.handle(native, { kc -> kc == android.view.KeyEvent.KEYCODE_DPAD_CENTER || kc == android.view.KeyEvent.KEYCODE_ENTER }) {
                            longPressTriggered = true
                            onLongPress()
                        }
                    ) {
                        if (native.action == android.view.KeyEvent.ACTION_UP) longPressTriggered = false
                        return@onPreviewKeyEvent true
                    }
                    if (native.action == android.view.KeyEvent.ACTION_UP && longPressTriggered) {
                        longPressTriggered = false
                        return@onPreviewKeyEvent true
                    }
                }
                false
            }
            .onFocusChanged { isFocused = it.isFocused }
            .clickable { if (!longPressTriggered) onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Poster
        Box(
            modifier = Modifier
                .width(67.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                .background(NuvioColors.BackgroundCard),
            contentAlignment = Alignment.Center
        ) {
            if (!artworkUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(artworkUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.tv.material3.Icon(
                        Icons.Default.Movie,
                        contentDescription = null,
                        tint = NuvioColors.TextTertiary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Infos
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
                .background(if (isFocused) focusBg else NuvioColors.BackgroundCard)
                .then(if (isFocused) Modifier.border(2.dp, focusRing, RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)) else Modifier)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                androidx.tv.material3.Text(
                    text = title,
                    color = if (isFocused) NuvioColors.TextPrimary else NuvioColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (meta != null) {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (meta.year != null) {
                            androidx.tv.material3.Text(
                                text = meta.year,
                                color = NuvioColors.TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        if (meta.genres.isNotEmpty()) {
                            androidx.tv.material3.Text(
                                text = meta.genres.take(2).joinToString(" ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Â ÃƒÂ¢Ã¢â€šÂ¬Ã¢â€žÂ¢ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã¢â‚¬Â¦Ãƒâ€šÃ‚Â¡ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã†â€™Ãƒâ€ Ã¢â‚¬â„¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€¦Ã‚Â¡ÃƒÆ’Ã†â€™ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¢ "),
                                color = NuvioColors.TextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    if (!meta.overview.isNullOrBlank()) {
                        Spacer(Modifier.height(4.dp))
                        androidx.tv.material3.Text(
                            text = meta.overview,
                            color = NuvioColors.TextTertiary,
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (remainingMs != null) {
                    Spacer(Modifier.height(4.dp))
                    androidx.tv.material3.Text(
                        text = formatFreeboxRemaining(remainingMs),
                        color = NuvioColors.Secondary,
                        fontSize = 11.sp
                    )
                }
            }
            if (meta?.voteAverage != null && meta.voteAverage > 0) {
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .background(NuvioColors.BackgroundElevated, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    androidx.tv.material3.Text(
                        text = "%.1f".format(meta.voteAverage),
                        color = NuvioColors.Rating,
                        fontSize = 12.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FreeboxVideoGridCard(
    title: String,
    artworkUrl: String?,
    meta: com.nuvio.tv.core.tmdb.FreeboxVideoMeta?,
    remainingMs: Long?,
    onClick: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    var longPressTriggered by remember { mutableStateOf(false) }
    val longPressKeyTracker = rememberLongPressKeyTracker()
    val focusRing = NuvioColors.FocusRing
    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .aspectRatio(4f / 3f)
            .clip(shape)
            .background(NuvioColors.BackgroundCard)
            .then(if (isFocused) Modifier.border(2.dp, focusRing, shape) else Modifier)
            .onPreviewKeyEvent { event ->
                val native = event.nativeKeyEvent
                if (onLongPress != null) {
                    if (longPressKeyTracker.handle(native, { kc -> kc == android.view.KeyEvent.KEYCODE_DPAD_CENTER || kc == android.view.KeyEvent.KEYCODE_ENTER }) {
                            longPressTriggered = true
                            onLongPress()
                        }
                    ) {
                        if (native.action == android.view.KeyEvent.ACTION_UP) longPressTriggered = false
                        return@onPreviewKeyEvent true
                    }
                    if (native.action == android.view.KeyEvent.ACTION_UP && longPressTriggered) {
                        longPressTriggered = false
                        return@onPreviewKeyEvent true
                    }
                }
                false
            }
            .onFocusChanged { isFocused = it.isFocused }
            .clickable { if (!longPressTriggered) onClick() }
    ) {
        if (!artworkUrl.isNullOrBlank()) {
            Image(
                painter = rememberAsyncImagePainter(artworkUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.tv.material3.Icon(Icons.Default.Movie, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(36.dp))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            androidx.tv.material3.Text(
                text = title,
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 11.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().basicMarquee(iterations = Int.MAX_VALUE)
            )
        }
        if (meta?.voteAverage != null && meta.voteAverage > 0) {
            Box(modifier = Modifier.padding(6.dp).background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp)).padding(horizontal = 5.dp, vertical = 2.dp).align(Alignment.TopStart)) {
                androidx.tv.material3.Text("%.1f".format(meta.voteAverage), color = NuvioColors.Rating, fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
        }
        if (remainingMs != null) {
            Box(modifier = Modifier.padding(6.dp).background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp)).padding(horizontal = 5.dp, vertical = 2.dp).align(Alignment.TopEnd)) {
                androidx.tv.material3.Text(formatFreeboxRemaining(remainingMs), color = NuvioColors.Secondary, fontSize = 10.sp)
            }
        }
    }
}

private fun handleFreeboxEntryClick(
    entry: FreeboxFileEntry,
    viewModel: FreeboxBrowserViewModel,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    currentPreview: FreeboxPhotoRequest?,
    onPreviewPhoto: (FreeboxPhotoRequest) -> Unit,
    onOpenPhoto: (FreeboxPhotoRequest) -> Unit,
    onOpenFile: (FreeboxFileOpenRequest) -> Unit,
    onPlayFile: (FreeboxPlaybackRequest) -> Unit
) {
    if (entry.isDirectory) {
        viewModel.navigateTo(entry)
    } else {
        coroutineScope.launch {
            val photoRequest = viewModel.photoRequestFor(entry)
            if (photoRequest != null) {
                if (currentPreview?.photoId == photoRequest.photoId) {
                    onOpenPhoto(photoRequest)
                } else {
                    onPreviewPhoto(photoRequest)
                }
            } else {
                val fileRequest = viewModel.fileOpenRequestFor(entry)
                if (fileRequest != null) {
                    onOpenFile(fileRequest)
                } else {
                    viewModel.playbackRequestFor(entry)?.let(onPlayFile)
                }
            }
        }
    }
}


private enum class FreeboxSortMode(val label: String) {
    NAME_ASC("Nom A-Z"),
    NAME_DESC("Nom Z-A"),
    SIZE_DESC("Taille d\u00E9croissante"),
    SIZE_ASC("Taille croissante"),
    DURATION_DESC("Dur\u00E9e d\u00E9croissante"),
    DURATION_ASC("Dur\u00E9e croissante"),
    DATE_DESC("Date r\u00E9cente"),
    DATE_ASC("Date ancienne");

    companion object {
        fun fromName(name: String): FreeboxSortMode = entries.firstOrNull { it.name == name } ?: NAME_ASC
    }
}

private fun formatFreeboxRemaining(remainingMs: Long): String {
    val totalMinutes = remainingMs / 60000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}h${minutes}m restantes"
    else "${minutes}m restantes"
}

private fun freeboxEntryComparator(
    sortMode: FreeboxSortMode,
    durations: Map<String, Long>
): Comparator<FreeboxFileEntry> {
    val base = compareByDescending<FreeboxFileEntry> { it.isDirectory }
    return when (sortMode) {
        FreeboxSortMode.NAME_ASC -> base.thenBy(String.CASE_INSENSITIVE_ORDER) { freeboxFileNameOnly(it.name) }
        FreeboxSortMode.NAME_DESC -> base.thenByDescending(String.CASE_INSENSITIVE_ORDER) { freeboxFileNameOnly(it.name) }
        FreeboxSortMode.SIZE_DESC -> base.thenByDescending { it.size ?: 0L }
        FreeboxSortMode.SIZE_ASC -> base.thenBy { it.size ?: 0L }
        FreeboxSortMode.DURATION_DESC -> base.thenByDescending { durations[freeboxContentIdForPath(it.path)] ?: it.durationMs ?: 0L }
        FreeboxSortMode.DURATION_ASC -> base.thenBy { durations[freeboxContentIdForPath(it.path)] ?: it.durationMs ?: 0L }
        FreeboxSortMode.DATE_DESC -> base.thenByDescending { it.modifiedMs ?: 0L }
        FreeboxSortMode.DATE_ASC -> base.thenBy { it.modifiedMs ?: 0L }
    }
}

@Composable
private fun FreeboxPhotoPreviewPanel(
    photo: FreeboxPhotoRequest,
    onOpenFullScreen: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val httpClient = remember { OkHttpClient() }
    val previousFocusRequester = remember { FocusRequester() }
    val photoFocusRequester = remember { FocusRequester() }
    val nextFocusRequester = remember { FocusRequester() }
    var loadState by remember(photo.photoUrl, photo.headers) { mutableStateOf<PhotoLoadState>(PhotoLoadState.Loading) }

    LaunchedEffect(photo.photoUrl, photo.headers) {
        loadState = PhotoLoadState.Loading
        loadState = loadFreeboxBitmap(httpClient, photo.photoUrl, photo.headers)
        runCatching { photoFocusRequester.requestFocus() }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(Color.Black.copy(alpha = 0.28f))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FreeboxToolbarButton(
            label = "<",
            onClick = onPrevious,
            modifier = Modifier
                .width(56.dp)
                .focusRequester(previousFocusRequester)
                .focusProperties { right = photoFocusRequester }
        )
        Card(
            onClick = onOpenFullScreen,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .focusRequester(photoFocusRequester)
                .focusProperties {
                    left = previousFocusRequester
                    right = nextFocusRequester
                },
            colors = CardDefaults.colors(
                containerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            border = CardDefaults.border(
                focusedBorder = Border(
                    border = BorderStroke(2.dp, NuvioColors.FocusRing),
                    shape = MaterialTheme.shapes.medium
                )
            ),
            scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val state = loadState) {
                    PhotoLoadState.Loading -> CircularProgressIndicator()
                    is PhotoLoadState.Error -> Text(state.message, color = NuvioColors.TextSecondary)
                    is PhotoLoadState.Ready -> Image(
                        bitmap = state.bitmap.asImageBitmap(),
                        contentDescription = photo.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        FreeboxToolbarButton(
            label = ">",
            onClick = onNext,
            modifier = Modifier
                .width(56.dp)
                .focusRequester(nextFocusRequester)
                .focusProperties { left = photoFocusRequester }
        )
    }
}
private suspend fun adjacentPhotoRequest(
    entries: List<FreeboxFileEntry>,
    current: FreeboxPhotoRequest,
    delta: Int,
    viewModel: FreeboxBrowserViewModel
): FreeboxPhotoRequest? {
    val photos = entries.filter { !it.isDirectory && it.name.substringAfterLast('.', "").lowercase() in PHOTO_EXTENSIONS }
    if (photos.isEmpty()) return null
    val currentIndex = photos.indexOfFirst { "freebox-photo:${it.path}" == current.photoId }.takeIf { it >= 0 } ?: 0
    val nextIndex = (currentIndex + delta + photos.size) % photos.size
    return viewModel.photoRequestFor(photos[nextIndex])
}

private fun openCachedFreeboxFile(context: android.content.Context, request: FreeboxFileOpenRequest) {
    val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", request.file)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, request.mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(intent) }
        .onFailure { Toast.makeText(context, "Aucune application compatible", Toast.LENGTH_SHORT).show() }
}

private val PHOTO_EXTENSIONS = setOf(
    "avif", "bmp", "gif", "heic", "heif", "jpeg", "jpg", "png", "tif", "tiff", "webp"
)


@Composable
private fun FreeboxGridCell(
    title: String,
    isDirectory: Boolean,
    artworkUrl: String?,
    onClick: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRing = NuvioColors.FocusRing
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .then(if (isFocused) Modifier.border(2.dp, focusRing, shape) else Modifier)
            .background(if (isFocused) NuvioColors.FocusBackground else NuvioColors.BackgroundCard)
            .onFocusChanged { isFocused = it.isFocused }
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(RoundedCornerShape(6.dp))
                .background(NuvioColors.BackgroundElevated),
            contentAlignment = Alignment.Center
        ) {
            if (!isDirectory && !artworkUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(artworkUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                androidx.tv.material3.Icon(
                    imageVector = if (isDirectory) Icons.Default.Folder else Icons.Default.Movie,
                    contentDescription = null,
                    tint = if (isFocused) NuvioColors.Secondary else NuvioColors.TextSecondary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        androidx.compose.material3.Text(
            text = title,
            color = if (isFocused) NuvioColors.TextPrimary else NuvioColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


