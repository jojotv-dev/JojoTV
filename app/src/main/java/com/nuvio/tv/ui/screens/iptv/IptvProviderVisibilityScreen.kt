package com.nuvio.tv.ui.screens.iptv

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.*
import coil3.compose.AsyncImage
import com.nuvio.tv.ui.theme.NuvioColors

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun IptvProviderVisibilityScreen(
    providerName: String,
    onBack: () -> Unit,
    viewModel: IptvProviderVisibilityViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    BackHandler { onBack() }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(NuvioColors.Background),
    ) {
        // ── Sidebar tabs ─────────────────────────────────────────────────
        VisibilityTabSidebar(
            providerName = providerName,
            selectedTab = uiState.tab,
            onTabSelected = { viewModel.selectTab(it) },
        )

        Box(Modifier.width(1.dp).fillMaxHeight().background(NuvioColors.Border))

        // ── Content pane ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 32.dp, vertical = 24.dp),
        ) {
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chargement...", color = NuvioColors.TextTertiary, fontSize = 14.sp)
                }
            } else {
                when (uiState.tab) {
                    VisibilityTab.LIVE -> LiveVisibilityPane(
                        uiState = uiState,
                        onToggleAllGroups = { viewModel.toggleAllGroups(it) },
                        onToggleGroup = { id, v -> viewModel.toggleGroup(id, v) },
                        onToggleAllChannels = { viewModel.toggleAllChannels(it) },
                        onToggleChannel = { id, v -> viewModel.toggleChannel(id, v) },
                    )
                    VisibilityTab.MOVIES -> CategoryVisibilityPane(
                        title = "Cat\u00e9gories Films",
                        icon = Icons.Default.Movie,
                        categories = uiState.movieCategories,
                        allVisible = uiState.allMovieCategoriesVisible,
                        onToggleAll = { viewModel.toggleAllMovieCategories(it) },
                        onToggle = { id, v -> viewModel.toggleMovieCategory(id, v) },
                    )
                    VisibilityTab.SERIES -> CategoryVisibilityPane(
                        title = "Cat\u00e9gories S\u00e9ries",
                        icon = Icons.Default.Tv,
                        categories = uiState.seriesCategories,
                        allVisible = uiState.allSeriesCategoriesVisible,
                        onToggleAll = { viewModel.toggleAllSeriesCategories(it) },
                        onToggle = { id, v -> viewModel.toggleSeriesCategory(id, v) },
                    )
                }
            }
        }
    }
}

// ── Sidebar ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun VisibilityTabSidebar(
    providerName: String,
    selectedTab: VisibilityTab,
    onTabSelected: (VisibilityTab) -> Unit,
) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight()
            .background(NuvioColors.BackgroundElevated)
            .padding(vertical = 24.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "Visibilit\u00e9",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NuvioColors.Primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
        )
        Text(
            text = providerName,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = NuvioColors.TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 4.dp, bottom = 16.dp),
        )

        val tabIcons = mapOf(
            VisibilityTab.LIVE to Icons.Default.LiveTv,
            VisibilityTab.MOVIES to Icons.Default.Movie,
            VisibilityTab.SERIES to Icons.Default.Tv,
        )
        VisibilityTab.entries.forEach { tab ->
            VisibilitySidebarTab(
                label = tab.label,
                icon = tabIcons[tab]!!,
                isSelected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun VisibilitySidebarTab(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(10.dp)
    val bg by animateColorAsState(
        targetValue = when {
            isSelected -> NuvioColors.FocusBackground
            isFocused -> NuvioColors.BackgroundCard
            else -> Color.Transparent
        }, animationSpec = tween(150), label = "tabBg"
    )
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .then(if (isFocused) Modifier.border(1.5.dp, NuvioColors.FocusRing, shape) else Modifier),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = bg,
            focusedContainerColor = bg,
            pressedContainerColor = NuvioColors.FocusBackground,
        ),
        shape = ClickableSurfaceDefaults.shape(shape),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected || isFocused) NuvioColors.Secondary else NuvioColors.TextSecondary,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected || isFocused) NuvioColors.TextPrimary else NuvioColors.TextSecondary,
            )
        }
    }
}

// ── Live pane (groupes + chaînes en deux sections) ───────────────────────────

@Composable
private fun LiveVisibilityPane(
    uiState: VisibilityUiState,
    onToggleAllGroups: (Boolean) -> Unit,
    onToggleGroup: (String, Boolean) -> Unit,
    onToggleAllChannels: (Boolean) -> Unit,
    onToggleChannel: (Long, Boolean) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // ── Section Groupes ───────────────────────────────────────────────
        item {
            SectionHeader(
                title = "Groupes",
                icon = Icons.Default.FolderOpen,
                count = uiState.liveGroups.size,
                allVisible = uiState.allGroupsVisible,
                onToggleAll = onToggleAllGroups,
            )
        }
        if (uiState.liveGroups.isEmpty()) {
            item { EmptyHint("Aucun groupe disponible") }
        }
        items(uiState.liveGroups, key = { "g_${it.id}" }) { group ->
            VisibilityRow(
                label = group.name,
                subtitle = if (group.count > 0) "${group.count} cha\u00eenes" else null,
                isVisible = group.isVisible,
                onToggle = { onToggleGroup(group.id, it) },
            )
        }

        item { Spacer(Modifier.height(24.dp)) }

        // ── Section Chaînes ───────────────────────────────────────────────
        item {
            SectionHeader(
                title = "Cha\u00eenes",
                icon = Icons.Default.LiveTv,
                count = uiState.liveChannels.size,
                allVisible = uiState.allChannelsVisible,
                onToggleAll = onToggleAllChannels,
            )
        }
        if (uiState.liveChannels.isEmpty()) {
            item { EmptyHint("Aucune cha\u00eene disponible") }
        }
        items(uiState.liveChannels, key = { "ch_${it.id}" }) { channel ->
            ChannelVisibilityRow(
                item = channel,
                onToggle = { onToggleChannel(channel.id, it) },
            )
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ── Generic category pane (Films / Séries) ───────────────────────────────────

@Composable
private fun CategoryVisibilityPane(
    title: String,
    icon: ImageVector,
    categories: List<CategoryVisibilityItem>,
    allVisible: Boolean,
    onToggleAll: (Boolean) -> Unit,
    onToggle: (Long, Boolean) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            SectionHeader(
                title = title,
                icon = icon,
                count = categories.size,
                allVisible = allVisible,
                onToggleAll = onToggleAll,
            )
        }
        if (categories.isEmpty()) {
            item { EmptyHint("Aucune cat\u00e9gorie disponible") }
        }
        items(categories, key = { "cat_${it.id}" }) { cat ->
            VisibilityRow(
                label = cat.name,
                subtitle = if (cat.count > 0) "${cat.count} \u00e9l\u00e9ments" else null,
                isVisible = cat.isVisible,
                onToggle = { onToggle(cat.id, it) },
            )
        }
        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ── Section header avec toggle global ────────────────────────────────────────

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    count: Int,
    allVisible: Boolean,
    onToggleAll: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(icon, null, tint = NuvioColors.Secondary, modifier = Modifier.size(18.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NuvioColors.TextPrimary)
            if (count > 0) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(NuvioColors.BackgroundElevated)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text("$count", fontSize = 11.sp, color = NuvioColors.TextTertiary)
                }
            }
        }
        // Toggle "Tout afficher / Tout masquer"
        ToggleAllButton(allVisible = allVisible, onToggle = onToggleAll)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(NuvioColors.Border)
    )
    Spacer(Modifier.height(8.dp))
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ToggleAllButton(allVisible: Boolean, onToggle: (Boolean) -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(8.dp)
    Surface(
        onClick = { onToggle(!allVisible) },
        modifier = Modifier
            .height(32.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .then(if (isFocused) Modifier.border(1.5.dp, NuvioColors.FocusRing, shape) else Modifier),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = NuvioColors.BackgroundCard,
            focusedContainerColor = NuvioColors.Secondary,
            pressedContainerColor = NuvioColors.FocusBackground,
        ),
        shape = ClickableSurfaceDefaults.shape(shape),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = if (allVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = null,
                tint = NuvioColors.TextSecondary,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = if (allVisible) "Tout masquer" else "Tout afficher",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = NuvioColors.TextSecondary,
            )
        }
    }
}

// ── Row générique (groupe / catégorie) ───────────────────────────────────────

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun VisibilityRow(
    label: String,
    subtitle: String?,
    isVisible: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(10.dp)
    val bg by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.FocusBackground else NuvioColors.BackgroundCard,
        animationSpec = tween(120), label = "rowBg"
    )

    Surface(
        onClick = { onToggle(!isVisible) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .then(if (isFocused) Modifier.border(1.5.dp, NuvioColors.FocusRing, shape) else Modifier),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = bg,
            focusedContainerColor = bg,
            pressedContainerColor = NuvioColors.FocusBackground,
        ),
        shape = ClickableSurfaceDefaults.shape(shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    tint = if (isVisible) NuvioColors.Secondary else NuvioColors.TextTertiary,
                    modifier = Modifier.size(18.dp),
                )
                Column {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isVisible) NuvioColors.TextPrimary else NuvioColors.TextTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = NuvioColors.TextTertiary,
                        )
                    }
                }
            }
            NuvioToggle(checked = isVisible, onCheckedChange = onToggle)
        }
    }
}

// ── Row chaîne avec logo ──────────────────────────────────────────────────────

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ChannelVisibilityRow(
    item: ChannelVisibilityItem,
    onToggle: (Boolean) -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(10.dp)
    val bg by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.FocusBackground else NuvioColors.BackgroundCard,
        animationSpec = tween(120), label = "chBg"
    )

    Surface(
        onClick = { onToggle(!item.isVisible) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .then(if (isFocused) Modifier.border(1.5.dp, NuvioColors.FocusRing, shape) else Modifier),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = bg,
            focusedContainerColor = bg,
            pressedContainerColor = NuvioColors.FocusBackground,
        ),
        shape = ClickableSurfaceDefaults.shape(shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
            ) {
                // Logo ou icone fallback
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(NuvioColors.BackgroundElevated),
                    contentAlignment = Alignment.Center,
                ) {
                    if (!item.logoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = item.logoUrl,
                            contentDescription = item.name,
                            modifier = Modifier.size(28.dp),
                        )
                    } else {
                        Icon(
                            Icons.Default.Tv,
                            null,
                            tint = NuvioColors.TextTertiary,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
                Column {
                    Text(
                        text = item.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (item.isVisible) NuvioColors.TextPrimary else NuvioColors.TextTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!item.groupTitle.isNullOrBlank()) {
                        Text(
                            text = item.groupTitle,
                            fontSize = 11.sp,
                            color = NuvioColors.TextTertiary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            NuvioToggle(checked = item.isVisible, onCheckedChange = onToggle)
        }
    }
}

// ── Toggle switch custom ──────────────────────────────────────────────────────

@Composable
private fun NuvioToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) NuvioColors.Secondary else NuvioColors.BackgroundElevated,
        animationSpec = tween(200), label = "trackColor"
    )
    val thumbColor by animateColorAsState(
        targetValue = if (checked) Color.White else NuvioColors.TextTertiary,
        animationSpec = tween(200), label = "thumbColor"
    )
    Box(
        modifier = Modifier
            .width(44.dp)
            .height(24.dp)
            .clip(CircleShape)
            .background(trackColor),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 3.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(thumbColor),
        )
    }
}

// ── Hint vide ────────────────────────────────────────────────────────────────

@Composable
private fun EmptyHint(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = NuvioColors.TextTertiary, fontSize = 13.sp)
    }
}