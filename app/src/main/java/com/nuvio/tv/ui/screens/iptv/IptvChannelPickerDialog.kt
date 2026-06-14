package com.nuvio.tv.ui.screens.iptv

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.*
import com.streamvault.domain.model.Category
import com.streamvault.domain.model.Channel
import com.streamvault.domain.model.Provider
import com.nuvio.tv.ui.theme.NuvioColors

@Composable
fun IptvChannelPickerDialog(
    onDismiss: () -> Unit,
    onChannelSelected: (name: String, url: String, id: Long, providerId: Long) -> Unit,
    viewModel: IptvChannelPickerViewModel = hiltViewModel()
) {
    val step       by viewModel.step.collectAsStateWithLifecycle()
    val providers  by viewModel.providers.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val channels   by viewModel.channels.collectAsStateWithLifecycle()
    val query      by viewModel.searchQuery.collectAsStateWithLifecycle()
    val results    by viewModel.searchResults.collectAsStateWithLifecycle()

    BackHandler {
        if (step == PickerStep.ProviderList) {
            viewModel.reset(); onDismiss()
        } else {
            viewModel.goBack()
        }
    }

    Dialog(
        onDismissRequest = { viewModel.reset(); onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xCC000000)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .fillMaxWidth(0.72f)
                    .fillMaxHeight(0.82f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(NuvioColors.BackgroundCard)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header + breadcrumb
                PickerHeader(
                    step = step,
                    onBack = {
                        if (step == PickerStep.ProviderList) { viewModel.reset(); onDismiss() }
                        else viewModel.goBack()
                    }
                )

                // Barre de recherche (visible dès qu'un provider est choisi)
                if (step !is PickerStep.ProviderList) {
                    val provider = when (val s = step) {
                        is PickerStep.CategoryList  -> s.provider
                        is PickerStep.ChannelList   -> s.provider
                        is PickerStep.SearchResults -> s.provider
                        else -> null
                    }
                    provider?.let {
                        SearchBar(
                            query = query,
                            onQuery = { q ->
                                viewModel.updateQuery(q)
                                if (q.isNotBlank()) viewModel.openSearch(it)
                            }
                        )
                    }
                }

                // Contenu selon l'étape
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        slideInHorizontally { it / 4 } + fadeIn() togetherWith
                        slideOutHorizontally { -it / 4 } + fadeOut()
                    },
                    label = "picker_content"
                ) { currentStep ->
                    when (currentStep) {
                        is PickerStep.ProviderList -> ProviderListPane(
                            providers = providers,
                            onSelect = { viewModel.selectProvider(it) }
                        )
                        is PickerStep.CategoryList -> CategoryListPane(
                            categories = categories,
                            onSelect = { viewModel.selectCategory(currentStep.provider, it) }
                        )
                        is PickerStep.ChannelList -> ChannelListPane(
                            channels = channels,
                            onSelect = { ch ->
                                onChannelSelected(ch.name, ch.streamUrl, ch.id, ch.providerId)
                                viewModel.reset(); onDismiss()
                            }
                        )
                        is PickerStep.SearchResults -> ChannelListPane(
                            channels = results,
                            onSelect = { ch ->
                                onChannelSelected(ch.name, ch.streamUrl, ch.id, ch.providerId)
                                viewModel.reset(); onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PickerHeader(step: PickerStep, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        var focused by remember { mutableStateOf(false) }
        Card(
            onClick = onBack,
            modifier = Modifier.size(36.dp).onFocusChanged { focused = it.hasFocus },
            shape = CardDefaults.shape(RoundedCornerShape(8.dp)),
            colors = CardDefaults.colors(
                containerColor = if (focused) NuvioColors.FocusBackground else Color.Transparent,
                focusedContainerColor = NuvioColors.FocusBackground
            ),
            scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.9f)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ArrowBack, null, tint = NuvioColors.TextSecondary, modifier = Modifier.size(18.dp))
            }
        }

        // Breadcrumb
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BreadcrumbChip("Providers", active = true)
            when (step) {
                is PickerStep.CategoryList -> {
                    BreadcrumbSep()
                    BreadcrumbChip(step.provider.name, active = true)
                }
                is PickerStep.ChannelList -> {
                    BreadcrumbSep(); BreadcrumbChip(step.provider.name, active = false)
                    BreadcrumbSep(); BreadcrumbChip(step.category.name, active = true)
                }
                is PickerStep.SearchResults -> {
                    BreadcrumbSep(); BreadcrumbChip(step.provider.name, active = false)
                    BreadcrumbSep(); BreadcrumbChip("Recherche", active = true)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun BreadcrumbChip(text: String, active: Boolean) {
    Text(
        text, fontSize = 12.sp,
        color = if (active) NuvioColors.Primary else NuvioColors.TextTertiary,
        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
        maxLines = 1, overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun BreadcrumbSep() {
    Text("\u203A", fontSize = 12.sp, color = NuvioColors.TextTertiary)
}

@Composable
private fun SearchBar(query: String, onQuery: (String) -> Unit) {
    // Sur TV on utilise une Card focusable qui affiche la query
    // L'édition réelle nécessiterait un clavier virtuel ou un BasicTextField
    // Pour l'instant : champ affichage + hint, éditable si un clavier est branché
    var focused by remember { mutableStateOf(false) }
    val fr = remember { FocusRequester() }
    Card(
        onClick = { fr.requestFocus() },
        modifier = Modifier.fillMaxWidth().onFocusChanged { focused = it.hasFocus },
        shape = CardDefaults.shape(RoundedCornerShape(10.dp)),
        colors = CardDefaults.colors(
            containerColor = NuvioColors.BackgroundElevated,
            focusedContainerColor = NuvioColors.BackgroundElevated
        ),
        border = CardDefaults.border(
            border = androidx.tv.material3.Border(BorderStroke(1.dp, NuvioColors.Border)),
            focusedBorder = androidx.tv.material3.Border(BorderStroke(1.5.dp, NuvioColors.Primary))
        ),
        scale = CardDefaults.scale(focusedScale = 1f)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Search, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(16.dp))
            androidx.compose.foundation.text.BasicTextField(
                value = query,
                onValueChange = onQuery,
                modifier = Modifier.weight(1f).focusRequester(fr),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = NuvioColors.TextPrimary, fontSize = 13.sp
                ),
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text("Rechercher une cha\u00EEne...", color = NuvioColors.TextTertiary, fontSize = 13.sp)
                    }
                    inner()
                },
                cursorBrush = androidx.compose.ui.graphics.SolidColor(NuvioColors.Primary)
            )
            if (query.isNotEmpty()) {
                var xFocused by remember { mutableStateOf(false) }
                Card(
                    onClick = { onQuery("") },
                    modifier = Modifier.size(20.dp).onFocusChanged { xFocused = it.hasFocus },
                    shape = CardDefaults.shape(RoundedCornerShape(10.dp)),
                    colors = CardDefaults.colors(
                        containerColor = Color.Transparent,
                        focusedContainerColor = NuvioColors.FocusBackground
                    ),
                    scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.9f)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Close, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderListPane(providers: List<Provider>, onSelect: (Provider) -> Unit) {
    if (providers.isEmpty()) {
        EmptyHint("Aucun provider configur\u00E9")
        return
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(providers, key = { it.id }) { provider ->
            PickerRow(
                title = provider.name,
                subtitle = provider.type.name,
                icon = Icons.Default.Router,
                hasArrow = true,
                onClick = { onSelect(provider) }
            )
        }
    }
}

@Composable
private fun CategoryListPane(categories: List<Category>, onSelect: (Category) -> Unit) {
    if (categories.isEmpty()) {
        EmptyHint("Chargement des groupes...")
        return
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories, key = { it.id }) { cat ->
            PickerRow(
                title = cat.name,
                subtitle = if (cat.count > 0) "${cat.count} cha\u00EEnes" else null,
                icon = Icons.Default.FolderOpen,
                hasArrow = true,
                onClick = { onSelect(cat) }
            )
        }
    }
}

@Composable
private fun ChannelListPane(channels: List<Channel>, onSelect: (Channel) -> Unit) {
    if (channels.isEmpty()) {
        EmptyHint("Aucune cha\u00EEne")
        return
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(channels, key = { it.id }) { ch ->
            PickerRow(
                title = ch.name,
                subtitle = ch.categoryName,
                icon = Icons.Default.LiveTv,
                hasArrow = false,
                onClick = { onSelect(ch) }
            )
        }
    }
}

@Composable
private fun PickerRow(
    title: String,
    subtitle: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    hasArrow: Boolean,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().onFocusChanged { focused = it.hasFocus },
        shape = CardDefaults.shape(RoundedCornerShape(10.dp)),
        colors = CardDefaults.colors(
            containerColor = NuvioColors.BackgroundElevated,
            focusedContainerColor = NuvioColors.FocusBackground
        ),
        border = CardDefaults.border(
            border = androidx.tv.material3.Border(BorderStroke(1.dp, Color.Transparent)),
            focusedBorder = androidx.tv.material3.Border(BorderStroke(1.5.dp, NuvioColors.Primary))
        ),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.98f)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, null,
                tint = if (focused) NuvioColors.Primary else NuvioColors.TextTertiary,
                modifier = Modifier.size(18.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = NuvioColors.TextPrimary, fontSize = 13.sp,
                    fontWeight = if (focused) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                subtitle?.let {
                    Text(it, color = NuvioColors.TextTertiary, fontSize = 11.sp, maxLines = 1)
                }
            }
            if (hasArrow) {
                Icon(Icons.Default.ChevronRight, null,
                    tint = NuvioColors.TextTertiary, modifier = Modifier.size(16.dp))
            } else {
                Icon(Icons.Default.Add, null,
                    tint = if (focused) NuvioColors.Primary else NuvioColors.TextTertiary,
                    modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun EmptyHint(text: String) {
    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(text, color = NuvioColors.TextTertiary, fontSize = 13.sp)
    }
}