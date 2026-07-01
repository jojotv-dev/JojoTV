package com.nuvio.tv.ui.screens.iptv

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuvio.tv.ui.util.rememberLongPressKeyTracker
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.nuvio.tv.data.local.IptvSettingsDataStore
import com.nuvio.tv.data.local.IptvVisibilitySettings
import com.nuvio.tv.ui.theme.NuvioColors
import com.nuvio.tv.ui.screens.iptv.IptvGroupOptionsDialog
import com.streamvault.domain.model.Category
import com.streamvault.domain.model.ContentType
import com.streamvault.domain.repository.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryWithVisibility(
    val category: Category,
    val isVisible: Boolean,
    val displayName: String = category.name
)

@HiltViewModel
class IptvLiveTvGroupViewModel @Inject constructor(
    private val channelRepository: ChannelRepository,
    private val iptvSettingsDataStore: IptvSettingsDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val providerId: Long = savedStateHandle.get<String>("providerId")?.toLongOrNull() ?: -1L
    val providerName: String = savedStateHandle.get<String>("providerName") ?: ""

    val categoriesWithVisibility: StateFlow<List<CategoryWithVisibility>> =
        combine(
            combine(
                channelRepository.getCategories(providerId),
                iptvSettingsDataStore.visibilitySettings(providerId, ContentType.LIVE)
            ) { categories, visibility ->
                categories.map { cat ->
                    CategoryWithVisibility(
                        category = cat,
                        isVisible = cat.id.toString() !in visibility.hiddenGroupIds,
                        displayName = cat.name
                    )
                }
            },
            iptvSettingsDataStore.groupRenames(providerId)
        ) { items, renames ->
            items.map { it.copy(displayName = renames[it.category.id.toString()] ?: it.category.name) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleGroupVisibility(categoryId: Long, currentlyVisible: Boolean) {
        viewModelScope.launch {
            iptvSettingsDataStore.setGroupVisible(providerId, ContentType.LIVE, categoryId.toString(), !currentlyVisible)
        }
    }

    fun renameGroup(categoryId: Long, newName: String) {
        viewModelScope.launch {
            iptvSettingsDataStore.renameGroup(providerId, categoryId.toString(), newName)
        }
    }
}

@Composable
fun IptvLiveTvGroupScreen(
    onNavigateToChannels: (providerId: Long, categoryId: Long, categoryName: String) -> Unit,
    onNavigateToAllChannels: (providerId: Long) -> Unit,
    viewModel: IptvLiveTvGroupViewModel = hiltViewModel()
) {
    val categoriesWithVisibility by viewModel.categoriesWithVisibility.collectAsStateWithLifecycle()
    var editMode by remember { mutableStateOf(false) }
    var viewMode by remember { mutableStateOf(IptvViewMode.GRID) }
    var optionsItem by remember { mutableStateOf<CategoryWithVisibility?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(NuvioColors.Background)) {
        optionsItem?.let { item ->
            IptvGroupOptionsDialog(
                groupName = item.displayName,
                onDismiss = { optionsItem = null },
                onHide = { viewModel.toggleGroupVisibility(item.category.id, true) },
                onRename = { newName -> viewModel.renameGroup(item.category.id, newName) }
            )
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp, vertical = 28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LiveTv, null, tint = NuvioColors.TextPrimary, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    "Live TV — ${viewModel.providerName}",
                    color = NuvioColors.TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IptvViewModeToggle(viewMode = viewMode, onToggle = {
                    viewMode = if (viewMode == IptvViewMode.GRID) IptvViewMode.LIST else IptvViewMode.GRID
                })
                Spacer(Modifier.width(8.dp))
                ToggleEditButton(editMode = editMode, onClick = { editMode = !editMode })
            }

            if (editMode) {
                Text(
                    "Cochez les groupes a afficher",
                    color = NuvioColors.TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 48.dp).padding(bottom = 8.dp)
                )
            }

            if (viewMode == IptvViewMode.GRID) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 200.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 48.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!editMode) {
                        item {
                            GroupCard(
                                name = "Toutes les chaines",
                                count = null,
                                isVisible = true,
                                editMode = false,
                                onToggle = {},
                                onClick = { onNavigateToAllChannels(viewModel.providerId) }
                            )
                        }
                    }
                    items(if (editMode) categoriesWithVisibility else categoriesWithVisibility.filter { it.isVisible }, key = { it.category.id }) { item ->
                        GroupCard(
                            name = item.displayName,
                            count = item.category.count.takeIf { it > 0 },
                            isVisible = item.isVisible,
                            editMode = editMode,
                            onToggle = { viewModel.toggleGroupVisibility(item.category.id, item.isVisible) },
                            onClick = {
                                if (!editMode) onNavigateToChannels(viewModel.providerId, item.category.id, item.category.name)
                            }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 48.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!editMode) {
                        item {
                            GroupListRow(
                                name = "Toutes les chaines",
                                count = null,
                                isVisible = true,
                                editMode = false,
                                onToggle = {},
                                onClick = { onNavigateToAllChannels(viewModel.providerId) }
                            )
                        }
                    }
                    items(if (editMode) categoriesWithVisibility else categoriesWithVisibility.filter { it.isVisible }, key = { it.category.id }) { item ->
                        GroupListRow(
                            name = item.displayName,
                            count = item.category.count.takeIf { it > 0 },
                            isVisible = item.isVisible,
                            editMode = editMode,
                            onToggle = { viewModel.toggleGroupVisibility(item.category.id, item.isVisible) },
                            onClick = {
                                if (!editMode) onNavigateToChannels(viewModel.providerId, item.category.id, item.category.name)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToggleEditButton(editMode: Boolean, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        targetValue = when {
            editMode -> NuvioColors.Primary.copy(alpha = 0.2f)
            isFocused -> NuvioColors.FocusBackground
            else -> NuvioColors.BackgroundCard
        },
        animationSpec = tween(150), label = "editBg"
    )
    Card(
        onClick = onClick,
        modifier = Modifier.size(36.dp).onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(RoundedCornerShape(8.dp)),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), inset = 0.dp, shape = RoundedCornerShape(8.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.92f)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                Icons.Default.Settings, "Filtrer",
                tint = if (editMode) NuvioColors.Primary else NuvioColors.TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun GroupListRow(
    name: String,
    count: Int?,
    isVisible: Boolean,
    editMode: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onLongPress: () -> Unit = {}
) {
    var isFocused by remember { mutableStateOf(false) }
    val longPressTracker = rememberLongPressKeyTracker()
    val shape = RoundedCornerShape(12.dp)
    val bgColor by animateColorAsState(
        targetValue = when {
            editMode && !isVisible -> NuvioColors.BackgroundCard.copy(alpha = 0.5f)
            isFocused -> NuvioColors.FocusBackground
            else -> NuvioColors.BackgroundCard
        },
        animationSpec = tween(150), label = "bg"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            editMode && isVisible -> NuvioColors.Primary
            isFocused -> NuvioColors.Primary
            else -> Color.Transparent
        },
        animationSpec = tween(150), label = "border"
    )
    Card(
        onClick = if (editMode) onToggle else onClick,
        modifier = Modifier.fillMaxWidth().height(64.dp).onFocusChanged { isFocused = it.hasFocus }.onPreviewKeyEvent { event -> longPressTracker.handle(event.nativeKeyEvent, { kc -> kc == android.view.KeyEvent.KEYCODE_DPAD_CENTER || kc == android.view.KeyEvent.KEYCODE_ENTER }) { onLongPress() } },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), inset = 0.dp, shape = shape)),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.97f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (editMode) {
                Icon(
                    if (isVisible) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                    null,
                    tint = if (isVisible) NuvioColors.Primary else NuvioColors.TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = name,
                color = if (editMode && !isVisible) NuvioColors.TextTertiary else NuvioColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .then(if (isFocused) Modifier.basicMarquee() else Modifier)
            )
            if (count != null) {
                Box(
                    modifier = Modifier
                        .background(NuvioColors.BackgroundElevated, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(count.toString(), color = NuvioColors.TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun GroupCard(
    name: String,
    count: Int?,
    isVisible: Boolean,
    editMode: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onLongPress: () -> Unit = {}
) {
    var isFocused by remember { mutableStateOf(false) }
    val longPressTracker = rememberLongPressKeyTracker()
    val shape = RoundedCornerShape(14.dp)
    val bgColor by animateColorAsState(
        targetValue = when {
            editMode && !isVisible -> NuvioColors.BackgroundCard.copy(alpha = 0.5f)
            isFocused -> NuvioColors.FocusBackground
            else -> NuvioColors.BackgroundCard
        },
        animationSpec = tween(150), label = "bg"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            editMode && isVisible -> NuvioColors.Primary
            isFocused -> NuvioColors.Primary
            else -> Color.Transparent
        },
        animationSpec = tween(150), label = "border"
    )
    Card(
        onClick = if (editMode) onToggle else onClick,
        modifier = Modifier.fillMaxWidth().height(80.dp).onFocusChanged { isFocused = it.hasFocus }.onPreviewKeyEvent { event -> longPressTracker.handle(event.nativeKeyEvent, { kc -> kc == android.view.KeyEvent.KEYCODE_DPAD_CENTER || kc == android.view.KeyEvent.KEYCODE_ENTER }) { onLongPress() } },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), inset = 0.dp, shape = shape)),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.97f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (editMode) {
                Icon(
                    if (isVisible) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                    null,
                    tint = if (isVisible) NuvioColors.Primary else NuvioColors.TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
            }
            Text(
                text = name,
                color = if (editMode && !isVisible) NuvioColors.TextTertiary else NuvioColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .then(if (isFocused) Modifier.basicMarquee() else Modifier)
            )
            if (count != null) {
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .background(NuvioColors.BackgroundElevated, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(count.toString(), color = NuvioColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
