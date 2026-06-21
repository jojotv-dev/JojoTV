package com.nuvio.tv.ui.screens.iptv.tivi.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import com.nuvio.tv.ui.theme.NuvioColors
import com.nuvio.tv.ui.screens.iptv.tivi.TiviProviderNode

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TiviProviderTree(
    providerNodes: List<TiviProviderNode>,
    selectedGroupId: Long?,
    selectedProviderId: Long?,
    onProviderClick: (Long) -> Unit,
    onProviderFocus: (Long) -> Unit,
    onGroupClick: (Long, Long) -> Unit,
    onGroupFocus: (Long, Long) -> Unit,
    onProviderLongClick: (Long) -> Unit,
    onGroupLongClick: (Long, Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .width(230.dp)
            .fillMaxHeight()
            .background(NuvioColors.Background),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        providerNodes.forEach { node ->
            item(key = "provider_${node.provider.id}") {
                TiviProviderHeader(
                    label = node.provider.name,
                    isExpanded = node.isExpanded,
                    isSelected = node.provider.id == selectedProviderId,
                    onClick = { onProviderClick(node.provider.id) },
                    onFocus = { onProviderFocus(node.provider.id) },
                    onLongClick = { onProviderLongClick(node.provider.id) },
                )
            }
            if (node.isExpanded) {
                items(node.groups, key = { "group_${node.provider.id}_${it.id}" }) { group ->
                    TiviGroupItem(
                        label = group.name,
                        isSelected = group.id == selectedGroupId,
                        onClick = { onGroupClick(node.provider.id, group.id) },
                        onFocus = { onGroupFocus(node.provider.id, group.id) },
                        onLongClick = { onGroupLongClick(node.provider.id, group.id) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TiviProviderHeader(
    label: String,
    isExpanded: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onFocus: () -> Unit,
    onLongClick: () -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "chevron"
    )
    val accent = MaterialTheme.colorScheme.primary
    val selectedBackground = accent.copy(alpha = 0.14f)
    val shape = RoundedCornerShape(8.dp)

    Surface(
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isFocused) Modifier.border(2.dp, accent, shape) else Modifier)
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) onFocus()
            },
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (isSelected) selectedBackground else TIVI_EPG_BACKGROUND,
            focusedContainerColor = TIVI_EPG_BACKGROUND,
            pressedContainerColor = accent.copy(alpha = 0.12f),
        ),
        shape = ClickableSurfaceDefaults.shape(shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isFocused || isSelected) NuvioColors.TextPrimary else NuvioColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).then(if (isFocused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = if (isFocused || isSelected) NuvioColors.Secondary else NuvioColors.TextTertiary,
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(rotation),
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TiviGroupItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onFocus: () -> Unit,
    onLongClick: () -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    val accent = MaterialTheme.colorScheme.primary
    val selectedBackground = accent.copy(alpha = 0.14f)
    val shape = RoundedCornerShape(8.dp)

    Surface(
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isFocused) Modifier.border(2.dp, accent, shape) else Modifier)
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) onFocus()
            },
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (isSelected) selectedBackground else TIVI_EPG_BACKGROUND,
            focusedContainerColor = TIVI_EPG_BACKGROUND,
            pressedContainerColor = accent.copy(alpha = 0.12f),
        ),
        shape = ClickableSurfaceDefaults.shape(shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 16.dp, top = 9.dp, bottom = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(14.dp)
                        .background(accent, RoundedCornerShape(2.dp))
                )
                Spacer(Modifier.width(8.dp))
            } else {
                Spacer(Modifier.width(11.dp))
            }
            Text(
                text = label,
                fontSize = 12.sp,
                color = when {
                    isSelected -> NuvioColors.TextPrimary
                    isFocused  -> NuvioColors.TextSecondary
                    else       -> NuvioColors.TextTertiary
                },
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).then(if (isFocused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier),
            )
        }
    }
}
