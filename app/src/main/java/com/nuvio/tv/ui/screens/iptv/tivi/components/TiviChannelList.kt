package com.nuvio.tv.ui.screens.iptv.tivi.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import coil3.compose.AsyncImage
import com.streamvault.domain.model.Channel
import com.nuvio.tv.ui.theme.NuvioColors
import android.view.KeyEvent as AndroidKeyEvent

private val CHANNEL_QUALITY_REGEX = Regex("\\b(HEVC|4K|FHD|HD|SD)\\b", RegexOption.IGNORE_CASE)
private val CHANNEL_COUNTRY_PREFIX_REGEX = Regex("\\bFR\\s*\\|\\s*", RegexOption.IGNORE_CASE)

internal data class TiviChannelLabel(
    val name: String,
    val quality: String?,
)

internal fun tiviChannelLabel(rawName: String): TiviChannelLabel {
    val quality = CHANNEL_QUALITY_REGEX.find(rawName)?.value?.uppercase()
    val cleanName = rawName
        .replace(CHANNEL_COUNTRY_PREFIX_REGEX, "")
        .replace(CHANNEL_QUALITY_REGEX, " ")
        .replace(Regex("[()\\[\\]]"), " ")
        .replace(Regex("\\s*/\\s*"), " ")
        .replace(Regex("\\s+"), " ")
        .trim(' ', '|', '-', '/')
        .ifBlank { rawName.trim() }
    return TiviChannelLabel(name = cleanName, quality = quality)
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TiviChannelList(
    channels: List<Channel>,
    focusedChannelId: Long?,
    onChannelFocused: (Channel) -> Unit,
    onChannelClick: (Channel) -> Unit,
    onFocusChanged: (Boolean) -> Unit = {},
    firstChannelFocusRequester: FocusRequester? = null,
    onDirectionLeft: (() -> Boolean)? = null,
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.onFocusChanged { onFocusChanged(it.hasFocus) }) {
        Column(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight()
                .background(NuvioColors.Background),
        ) {
            Spacer(Modifier.height(TIVI_EPG_HEADER_HEIGHT))
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = 6.dp,
                    vertical = TIVI_EPG_VERTICAL_PADDING,
                ),
            ) {
                items(channels, key = { it.id }) { channel ->
                    val isFirst = channels.firstOrNull()?.id == channel.id
                    TiviChannelRow(
                        channel = channel,
                        isActive = channel.id == focusedChannelId,
                        onFocused = { onChannelFocused(channel) },
                        onClick = { onChannelClick(channel) },
                        focusRequester = if (isFirst) firstChannelFocusRequester else null,
                        onDirectionLeft = onDirectionLeft,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TiviChannelRow(
    channel: Channel,
    isActive: Boolean,
    onFocused: () -> Unit,
    onClick: () -> Unit,
    focusRequester: FocusRequester? = null,
    onDirectionLeft: (() -> Boolean)? = null,
) {
    var isFocused by remember { mutableStateOf(false) }
    val label = remember(channel.name) { tiviChannelLabel(channel.name) }
    val accent = MaterialTheme.colorScheme.primary
    val selectedBackground = accent.copy(alpha = 0.14f)
    val focusScale by animateFloatAsState(
        targetValue = if (isFocused) 1.03f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "tiviChannelFocusScale",
    )
    val shape = RoundedCornerShape(8.dp)

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(TIVI_EPG_ROW_HEIGHT)
            .scale(focusScale)
            .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier)
            .then(
                if (isFocused) Modifier.border(2.dp, accent, shape) else Modifier
            )
            .onPreviewKeyEvent { event ->
                val native = event.nativeKeyEvent
                native.action == AndroidKeyEvent.ACTION_DOWN &&
                    native.keyCode == AndroidKeyEvent.KEYCODE_DPAD_LEFT &&
                    onDirectionLeft?.invoke() == true
            }
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) onFocused()
            },
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (isActive) selectedBackground else NuvioColors.Background,
            focusedContainerColor = selectedBackground,
            pressedContainerColor = accent.copy(alpha = 0.12f),
        ),
        shape = ClickableSurfaceDefaults.shape(shape),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(if (isActive) accent else Color.Transparent),
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 50.dp, height = 28.dp)
                        .background(NuvioColors.BackgroundElevated, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (!channel.logoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = channel.logoUrl,
                            contentDescription = channel.name,
                            modifier = Modifier.fillMaxSize().padding(2.dp),
                        )
                    }
                }

                label.quality?.let { quality ->
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .height(20.dp)
                            .background(accent.copy(alpha = 0.22f), RoundedCornerShape(4.dp))
                            .border(1.dp, accent, RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = quality,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = label.name,
                    fontSize = 12.sp,
                    color = if (isFocused || isActive) accent else NuvioColors.TextSecondary,
                    fontWeight = if (isFocused || isActive) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                if (channel.currentProgram != null) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(accent, RoundedCornerShape(3.dp))
                    )
                }
            }
        }
    }
}
