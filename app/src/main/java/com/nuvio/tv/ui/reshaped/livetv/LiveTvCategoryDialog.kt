@file:OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class)

package com.nuvio.tv.ui.reshaped.livetv

import android.view.KeyEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Border
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.reshaped.livetv.LiveTvChannel
import com.nuvio.tv.reshaped.livetv.LiveTvRepository
import com.nuvio.tv.reshaped.livetv.LiveTvSource
import com.nuvio.tv.reshaped.livetv.LiveTvSourceType
import com.nuvio.tv.reshaped.livetv.LiveTvUiState
import com.nuvio.tv.reshaped.livetv.LiveTvVodCache
import com.nuvio.tv.reshaped.livetv.LiveTvVodCategory
import com.nuvio.tv.reshaped.livetv.LiveTvStalker
import com.nuvio.tv.reshaped.livetv.LiveTvXtream
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.theme.NuvioTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Picks the categories the list shows, and their order. For people who only watch a few: Hide
 * all, then turn on the ones they want. Hidden categories leave the list, search and channel
 * switching; favorites always stay. Holding OK picks a category up, ▲▼ move it, OK drops it.
 * ▶ opens a category's channels to hide single ones.
 */
@Composable
internal fun LiveTvCategoryDialog(onDismiss: () -> Unit) {
    val uiState by LiveTvRepository.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current.applicationContext
    val firstFocus = remember { FocusRequester() }
    var moving by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var openGroup by remember { mutableStateOf<String?>(null) }
    // Coming back from a category's channels focuses that category again.
    var returnTo by remember { mutableStateOf<String?>(null) }
    val group = openGroup
    var selectedKind by remember { mutableStateOf("tv") }
    var vodCategories by remember { mutableStateOf<Map<String, List<LiveTvVodCategory>>>(emptyMap()) }
    var vodCategoriesLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.sources) {
        val sources = uiState.sources.filter {
            (it.type == LiveTvSourceType.Xtream && it.xtream.isConfigured) ||
                (it.type == LiveTvSourceType.Stalker && it.stalker.isConfigured)
        }
        val types = listOf("movie", "series")
        val cached = buildMap {
            types.forEach { type ->
                sources.forEach { source ->
                    val categories = LiveTvVodCache.readCategories(context, source.id, type)
                    if (categories.isNotEmpty()) put(categoryMapKey(type, source.id), categories)
                }
            }
        }
        vodCategories = cached
        vodCategoriesLoaded = false
        val refreshed = coroutineScope {
            types.flatMap { type ->
                sources.map { source ->
                    async {
                        val categories = try {
                            loadCategories(source, type).filter { it.name.toLongOrNull() == null }
                        } catch (failure: Exception) {
                            if (failure is CancellationException) throw failure
                            emptyList()
                        }
                        Triple(type, source, categories)
                    }
                }
            }.awaitAll()
        }
        val merged = cached.toMutableMap()
        refreshed.forEach { (type, source, categories) ->
            val result = categories.ifEmpty { cached[categoryMapKey(type, source.id)].orEmpty() }
            if (categories.isNotEmpty()) LiveTvVodCache.writeCategories(context, source.id, type, categories)
            merged[categoryMapKey(type, source.id)] = result
        }
        vodCategories = merged
        vodCategoriesLoaded = true
    }

    LaunchedEffect(selectedKind, uiState.groups.size, vodCategoriesLoaded, vodCategories.values.sumOf { it.size }) {
        val listReady = if (selectedKind == "tv") {
            uiState.groups.isNotEmpty()
        } else {
            vodCategories.values.any { it.isNotEmpty() }
        }
        if (listReady) {
            withFrameNanos { }
            runCatching { firstFocus.requestFocus() }
        }
    }

    NuvioDialog(
        onDismiss = onDismiss,
        title = when {
            group != null -> liveTvGroupLabel(group, uiState.groupNames)
            else -> stringResource(R.string.live_tv_categories_title)
        },
        subtitle = stringResource(
            when {
                group != null -> R.string.live_tv_category_channels_description
                selectedKind == "tv" -> R.string.live_tv_categories_description
                else -> R.string.live_tv_vod_categories_description
            },
        ),
        width = 640.dp,
        usePlatformDefaultWidth = false,
        contentSpacing = NuvioTheme.spacing.md,
    ) {
        if (group != null) {
            LiveTvCategoryChannels(
                group = group,
                sourceId = null,
                uiState = uiState,
                onBack = { openGroup = null },
            )
            return@NuvioDialog
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.sm),
        ) {
            LiveTvPillButton(text = stringResource(R.string.live_tv_title), selected = selectedKind == "tv", onClick = { selectedKind = "tv" })
            LiveTvPillButton(text = stringResource(R.string.nav_movies), selected = selectedKind == "movie", onClick = { selectedKind = "movie" })
            LiveTvPillButton(text = stringResource(R.string.nav_series), selected = selectedKind == "series", onClick = { selectedKind = "series" })
        }
        if (selectedKind != "tv") {
            val allCategories = remember(vodCategories, selectedKind) {
                vodCategories.filterKeys { it.startsWith("$selectedKind\u001f") }.values.flatten()
            }
            val hidden = uiState.hiddenVodCategoryKeys
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.sm),
            ) {
                Text(
                    text = stringResource(R.string.live_tv_categories_shown,
                        allCategories.count { LiveTvRepository.vodCategoryHideKey(selectedKind, it.sourceId, it.id) !in hidden },
                        allCategories.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = NuvioTheme.colors.TextSecondary,
                    modifier = Modifier.weight(1f),
                )
                LiveTvPillButton(text = stringResource(R.string.live_tv_categories_show_all), onClick = {
                    LiveTvRepository.setAllVodCategoriesHidden(allCategories, selectedKind, hidden = false)
                })
                LiveTvPillButton(text = stringResource(R.string.live_tv_categories_hide_all), onClick = {
                    LiveTvRepository.setAllVodCategoriesHidden(allCategories, selectedKind, hidden = true)
                })
                LiveTvPillButton(text = stringResource(R.string.live_tv_done), onClick = onDismiss)
            }
            if (allCategories.isEmpty()) {
                Text(
                    text = stringResource(
                        if (vodCategoriesLoaded) R.string.live_tv_vod_categories_none else R.string.iptv_vod_loading,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = NuvioTheme.colors.TextSecondary,
                )
            } else {
                val firstVodSourceId = uiState.sources.firstOrNull { source ->
                    vodCategories[categoryMapKey(selectedKind, source.id)].orEmpty().isNotEmpty()
                }?.id
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 380.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    uiState.sources.forEach { source ->
                        val sourceCategories = vodCategories[categoryMapKey(selectedKind, source.id)].orEmpty()
                        if (sourceCategories.isNotEmpty()) {
                            item(key = "source:${source.id}") {
                                Text(
                                    text = source.label,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = NuvioTheme.colors.TextSecondary,
                                    modifier = Modifier.padding(start = 10.dp, top = 8.dp, bottom = 2.dp),
                                )
                            }
                            itemsIndexed(sourceCategories, key = { _, category -> "${source.id}:${category.id}" }) { index, category ->
                                val visible = LiveTvRepository.vodCategoryHideKey(selectedKind, source.id, category.id) !in hidden
                                LiveTvCategoryToggle(
                                    label = category.name,
                                    count = null,
                                    visible = visible,
                                    onToggle = { LiveTvRepository.setVodCategoryHidden(selectedKind, source.id, category.id, hidden = visible) },
                                    modifier = if (source.id == firstVodSourceId && index == 0) Modifier.focusRequester(firstFocus) else Modifier,
                                )
                            }
                        }
                    }
                }
            }
        } else {
            val activeGroups = uiState.groups
            val shown = activeGroups.count { it !in uiState.hiddenGroups }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.sm),
            ) {
                Text(
                    text = stringResource(R.string.live_tv_categories_shown, shown, activeGroups.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = NuvioTheme.colors.TextSecondary,
                    modifier = Modifier.weight(1f),
                )
                LiveTvPillButton(text = stringResource(R.string.live_tv_categories_show_all), onClick = {
                    LiveTvRepository.setAllGroupsHidden(false)
                })
                LiveTvPillButton(text = stringResource(R.string.live_tv_categories_hide_all), onClick = {
                    LiveTvRepository.setAllGroupsHidden(true)
                })
                LiveTvPillButton(text = stringResource(R.string.live_tv_done), onClick = onDismiss)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.sm),
            ) {
                LiveTvPillButton(text = stringResource(R.string.live_tv_categories_provider_order), onClick = { LiveTvRepository.resetGroupOrder() })
                LiveTvPillButton(text = stringResource(R.string.live_tv_categories_sort), onClick = { LiveTvRepository.sortGroupsAlphabetically() })
            }
            if (activeGroups.isEmpty()) {
                Text(
                    text = stringResource(R.string.live_tv_categories_none),
                    style = MaterialTheme.typography.bodyMedium,
                    color = NuvioTheme.colors.TextSecondary,
                )
            } else {
                val returnFocus = remember { FocusRequester() }
                LaunchedEffect(Unit) {
                    if (returnTo == null) return@LaunchedEffect
                    withFrameNanos { }
                    runCatching { returnFocus.requestFocus() }
                }
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth().heightIn(max = 380.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    itemsIndexed(activeGroups, key = { _, name -> name }) { index, name ->
                        val visible = name !in uiState.hiddenGroups
                        val isMoving = moving == name
                        val sourceGroupCount = uiState.groupCounts[name] ?: 0
                        LiveTvCategoryToggle(
                            label = liveTvGroupLabel(name, uiState.groupNames),
                            count = sourceGroupCount.toString(),
                            visible = visible,
                            moving = isMoving,
                            onToggle = { if (isMoving) moving = null else LiveTvRepository.setGroupHidden(name, visible) },
                            onPickUp = { moving = if (isMoving) null else name },
                            onMove = { step ->
                                val target = index + step
                                LiveTvRepository.moveGroup(name, step)
                                // Keep the moving row in view, a little away from the edge.
                                val shownRows = listState.layoutInfo.visibleItemsInfo
                                val first = shownRows.firstOrNull()?.index ?: 0
                                val last = shownRows.lastOrNull()?.index ?: 0
                                if (target <= first || target >= last) {
                                    scope.launch { listState.scrollToItem((target - if (step < 0) 1 else shownRows.size - 2).coerceAtLeast(0)) }
                                }
                            },
                            onDrop = { moving = null },
                            onOpen = {
                                returnTo = name
                                openGroup = name
                            },
                            modifier = when {
                                name == returnTo -> Modifier.focusRequester(returnFocus)
                                name == activeGroups.firstOrNull() -> Modifier.focusRequester(firstFocus)
                                else -> Modifier
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun categoryMapKey(type: String, sourceId: String): String = "$type\u001f$sourceId"

private suspend fun loadCategories(source: LiveTvSource, type: String): List<LiveTvVodCategory> =
    when (source.type) {
        LiveTvSourceType.Xtream -> LiveTvXtream.vodCategories(source, type)
        LiveTvSourceType.Stalker -> LiveTvStalker.vodCategories(source, type)
        LiveTvSourceType.M3u -> emptyList()
    }

/** One category's channels, each shown or hidden on its own. Back returns to the categories. */
@Composable
private fun LiveTvCategoryChannels(group: String, sourceId: String?, uiState: LiveTvUiState, onBack: () -> Unit) {
    val channels by produceState(emptyList<LiveTvChannel>(), uiState.channels, group, sourceId) {
        value = withContext(Dispatchers.Default) {
            uiState.channels.filter { it.group == group && (sourceId == null || it.sourceId == sourceId) }
        }
    }
    val hidden = uiState.hiddenChannelKeys
    val firstFocus = remember { FocusRequester() }
    LaunchedEffect(channels.isNotEmpty()) {
        if (channels.isEmpty()) return@LaunchedEffect
        withFrameNanos { }
        runCatching { firstFocus.requestFocus() }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md),
        modifier = Modifier.onPreviewKeyEvent { event ->
            val native = event.nativeKeyEvent
            val back = native.keyCode == KeyEvent.KEYCODE_BACK || native.keyCode == KeyEvent.KEYCODE_ESCAPE
            if (back && native.action == KeyEvent.ACTION_UP) onBack()
            back
        },
    ) {
        // The name the list shows; empty goes back to the playlist's own.
        LiveTvTextField(
            value = uiState.groupNames[group].orEmpty(),
            onValueChange = { LiveTvRepository.renameGroup(group, it) },
            placeholder = stringResource(R.string.live_tv_category_rename_hint, liveTvGroupLabel(group)),
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth(),
        )
        val shown = channels.count { it.hideKey !in hidden }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.sm),
        ) {
            Text(
                text = stringResource(R.string.live_tv_categories_shown, shown, channels.size),
                style = MaterialTheme.typography.bodySmall,
                color = NuvioTheme.colors.TextSecondary,
                modifier = Modifier.weight(1f),
            )
            LiveTvPillButton(
                text = stringResource(R.string.live_tv_categories_show_all),
                onClick = { LiveTvRepository.setChannelsHidden(channels, hidden = false) },
            )
            LiveTvPillButton(
                text = stringResource(R.string.live_tv_categories_hide_all),
                onClick = { LiveTvRepository.setChannelsHidden(channels, hidden = true) },
            )
            LiveTvPillButton(text = stringResource(R.string.live_tv_back), onClick = onBack)
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(max = 380.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            itemsIndexed(channels, key = { _, channel -> channel.id }) { index, channel ->
                val visible = channel.hideKey !in hidden
                LiveTvCategoryToggle(
                    label = channel.name,
                    count = null,
                    visible = visible,
                    onToggle = { LiveTvRepository.setChannelsHidden(listOf(channel), hidden = visible) },
                    modifier = if (index == 0) Modifier.focusRequester(firstFocus) else Modifier,
                )
            }
        }
    }
}

@Composable
internal fun LiveTvCategoryToggle(
    label: String,
    count: String?,
    visible: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    moving: Boolean = false,
    onPickUp: (() -> Unit)? = null,
    onMove: (step: Int) -> Unit = {},
    onDrop: () -> Unit = {},
    onOpen: (() -> Unit)? = null,
    openOnly: Boolean = false,
) {
    var focused by remember { mutableStateOf(false) }
    // A picked-up row lifts a little, like a card taken off the stack.
    val lift by animateFloatAsState(if (moving) 1.04f else 1f, spring(dampingRatio = 0.6f, stiffness = 500f), label = "liveTvCategoryLift")
    val rowShape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp))
    val content = when {
        focused -> NuvioTheme.colors.FocusContent
        visible -> NuvioTheme.colors.TextPrimary
        else -> NuvioTheme.colors.TextTertiary
    }
    Card(
        onClick = if (openOnly) onOpen ?: onToggle else onToggle,
        onLongClick = onPickUp,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = lift
                scaleY = lift
            }
            .onFocusChanged {
                focused = it.isFocused
                if (!it.isFocused && moving) onDrop()
            }
            .onPreviewKeyEvent { event ->
                val native = event.nativeKeyEvent
                if (!moving) {
                    // ▶ opens a category's channels.
                    if (onOpen == null || native.keyCode != KeyEvent.KEYCODE_DPAD_RIGHT) return@onPreviewKeyEvent false
                    if (native.action == KeyEvent.ACTION_DOWN && native.repeatCount == 0) onOpen()
                    return@onPreviewKeyEvent true
                }
                when (native.keyCode) {
                    KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> {
                        if (native.action == KeyEvent.ACTION_DOWN) onMove(if (native.keyCode == KeyEvent.KEYCODE_DPAD_UP) -1 else 1)
                        true
                    }
                    KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_ESCAPE -> {
                        if (native.action == KeyEvent.ACTION_UP) onDrop()
                        true
                    }
                    // Sideways would leave the row while carrying it.
                    KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> true
                    else -> false
                }
            },
        shape = CardDefaults.shape(rowShape),
        colors = CardDefaults.colors(
            containerColor = if (visible) NuvioTheme.colors.Secondary.copy(alpha = 0.12f) else Color.Transparent,
            focusedContainerColor = NuvioTheme.colors.FocusBackground,
        ),
        border = CardDefaults.border(
            border = if (visible) Border(BorderStroke(1.dp, NuvioTheme.colors.Secondary.copy(alpha = 0.4f)), shape = rowShape) else Border.None,
            focusedBorder = Border(BorderStroke(2.dp, NuvioTheme.colors.FocusRing), shape = rowShape),
        ),
        scale = CardDefaults.scale(focusedScale = 1.02f),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (count != null) {
                Text(
                    text = count,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (focused) NuvioTheme.colors.FocusContent.copy(alpha = 0.7f) else NuvioTheme.colors.TextTertiary,
                    modifier = Modifier.padding(horizontal = NuvioTheme.spacing.sm),
                )
            }
            // Checkboxes show category visibility; moving rows use the reorder icon.
            if (openOnly) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = content,
                    modifier = Modifier.size(20.dp),
                )
            } else {
                Icon(
                    imageVector = when {
                        moving -> Icons.Filled.UnfoldMore
                        visible -> Icons.Filled.CheckBox
                        else -> Icons.Filled.CheckBoxOutlineBlank
                    },
                    contentDescription = null,
                    tint = content,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
