package com.nuvio.tv.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.nuvio.tv.R
import com.nuvio.tv.domain.model.UserProfile
import com.nuvio.tv.ui.components.avatarArtworkZoom

private val LauncherFocusColor = Color(0xFFFF9800)
private val LauncherCardShape = RoundedCornerShape(12.dp)
private val LauncherActionButtonSize = 44.dp
private val LauncherProfileRowGap = 12.dp
private val LauncherProfileContentPadding = 10.dp
private val LauncherAvatarSize = 66.dp
private val LauncherAvatarNameGap = 14.dp
private val LauncherProfileRowHeight = 84.dp
private val LauncherProfileCardMaxWidth = 342.dp

@Composable
internal fun LauncherProfileSelectionContent(
    profiles: List<UserProfile>,
    activeProfileId: Int,
    canAddProfile: Boolean,
    avatarImageUrlsById: Map<String, String>,
    onProfileFocused: (UserProfile?) -> Unit,
    onProfileSelected: (UserProfile) -> Unit,
    onProfileLongPress: (UserProfile) -> Unit,
    onProfileEdit: (UserProfile) -> Unit,
    onAddProfileClick: () -> Unit
) {
    val activeIndex = profiles.indexOfFirst { it.id == activeProfileId }.coerceAtLeast(0)
    val profileNameStyle = LocalTextStyle.current.copy(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
    )
    val addProfileText = stringResource(R.string.profile_add_new)
    val textMeasurer = rememberTextMeasurer()
    val longestProfileNameWidthPx = remember(profiles, profileNameStyle, textMeasurer) {
        profiles.maxOfOrNull { profile ->
            textMeasurer.measure(profile.name, style = profileNameStyle).size.width
        } ?: 0
    }
    val profileCardWidth = with(LocalDensity.current) {
        (longestProfileNameWidthPx.toDp() + LauncherProfileContentPadding * 2 +
            LauncherAvatarSize + LauncherAvatarNameGap)
            .coerceAtMost(LauncherProfileCardMaxWidth)
    }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = activeIndex)
    val profileIds = remember(profiles) { profiles.map { it.id } }
    val focusRequesters = remember(profileIds) {
        profileIds.associateWith { FocusRequester() }
    }
    val addFocusRequester = remember { FocusRequester() }

    LaunchedEffect(profileIds, activeProfileId) {
        if (profileIds.isEmpty()) return@LaunchedEffect
        val targetIndex = profileIds.indexOf(activeProfileId).coerceAtLeast(0)
        listState.scrollToItem(targetIndex)
        repeat(2) { withFrameNanos { } }
        runCatching { focusRequesters[profileIds[targetIndex]]?.requestFocus() }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(470.dp)
                .padding(start = 42.dp, end = 30.dp, top = 38.dp, bottom = 30.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(profiles, key = { it.id }) { profile ->
                    LauncherProfileRow(
                        profile = profile,
                        avatarImageUrl = profile.avatarUrl?.takeIf { it.isNotBlank() }
                            ?: profile.avatarId?.let(avatarImageUrlsById::get),
                        profileCardWidth = profileCardWidth,
                        profileNameStyle = profileNameStyle,
                        focusRequester = focusRequesters.getValue(profile.id),
                        onFocused = { onProfileFocused(profile) },
                        onSelect = { onProfileSelected(profile) },
                        onLongPress = { onProfileLongPress(profile) },
                        onEdit = { onProfileEdit(profile) }
                    )
                }
                if (canAddProfile) {
                    item(key = "add-profile") {
                        LauncherAddProfileRow(
                            focusRequester = addFocusRequester,
                            profileCardWidth = profileCardWidth,
                            contentDescription = addProfileText,
                            onClick = onAddProfileClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LauncherProfileRow(
    profile: UserProfile,
    avatarImageUrl: String?,
    profileCardWidth: Dp,
    profileNameStyle: TextStyle,
    focusRequester: FocusRequester,
    onFocused: () -> Unit,
    onSelect: () -> Unit,
    onLongPress: () -> Unit,
    onEdit: () -> Unit
) {
    var editFocused by remember { mutableStateOf(false) }
    var profileFocused by remember { mutableStateOf(false) }
    val editDescription = stringResource(R.string.profile_edit_title, profile.name)
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LauncherProfileRowGap)
    ) {
        Box(
            modifier = Modifier
                .size(LauncherActionButtonSize)
                .clip(LauncherCardShape)
                .background(if (editFocused) Color.White.copy(alpha = 0.18f) else Color.Transparent)
                .onFocusChanged {
                    editFocused = it.isFocused
                    if (it.isFocused) onFocused()
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClickLabel = editDescription,
                    onClick = onEdit
                )
                .semantics {
                    role = Role.Button
                    contentDescription = editDescription
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = if (editFocused) Color.White else Color.White.copy(alpha = 0.82f),
                modifier = Modifier.size(21.dp)
            )
        }

        Row(
            modifier = Modifier
                .width(profileCardWidth)
                .height(LauncherProfileRowHeight)
                .clip(LauncherCardShape)
                .background(if (profileFocused) Color.Black.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.24f))
                .focusRequester(focusRequester)
                .onFocusChanged {
                    profileFocused = it.isFocused
                    if (it.isFocused) onFocused()
                }
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onSelect,
                    onLongClick = onLongPress
                )
                .semantics {
                    role = Role.Button
                    contentDescription = profile.name
                }
                .padding(horizontal = LauncherProfileContentPadding, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LauncherSquareAvatar(
                profile = profile,
                avatarImageUrl = avatarImageUrl,
                isFocused = profileFocused,
                modifier = Modifier.size(LauncherAvatarSize)
            )
            Spacer(modifier = Modifier.width(LauncherAvatarNameGap))
            Text(
                text = profile.name,
                color = Color.White,
                style = profileNameStyle.copy(
                    fontWeight = if (profileFocused) FontWeight.SemiBold else FontWeight.Medium,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LauncherSquareAvatar(
    profile: UserProfile,
    avatarImageUrl: String?,
    isFocused: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    val borderWidth = if (isFocused) 2.dp else 1.dp
    val artworkZoom = remember(avatarImageUrl) { avatarArtworkZoom(avatarImageUrl) }
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color(profile.avatarColorHex.toColorIntOrDefault()))
            .border(
                width = borderWidth,
                color = if (isFocused) LauncherFocusColor else Color.White.copy(alpha = 0.22f),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = profile.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
            color = Color.White,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )
        if (!avatarImageUrl.isNullOrBlank()) {
            AsyncImage(
                model = avatarImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = artworkZoom
                        scaleY = artworkZoom
                    }
                    .clip(shape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun LauncherAddProfileRow(
    focusRequester: FocusRequester,
    profileCardWidth: Dp,
    contentDescription: String,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(LauncherProfileRowHeight)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .focusable()
            .semantics {
                role = Role.Button
                this.contentDescription = contentDescription
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(LauncherActionButtonSize + LauncherProfileRowGap))
        Box(
            modifier = Modifier.width(profileCardWidth),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(LauncherActionButtonSize)
                    .clip(LauncherCardShape)
                    .border(
                        width = if (isFocused) 2.dp else 1.dp,
                        color = if (isFocused) LauncherFocusColor else Color.White.copy(alpha = 0.22f),
                        shape = LauncherCardShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = if (isFocused) Color.White else Color.White.copy(alpha = 0.82f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

private fun String.toColorIntOrDefault(): Int =
    runCatching { android.graphics.Color.parseColor(this) }.getOrDefault(android.graphics.Color.DKGRAY)
