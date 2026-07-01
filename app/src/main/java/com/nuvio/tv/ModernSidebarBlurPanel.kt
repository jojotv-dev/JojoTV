package com.nuvio.tv

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.nuvio.tv.ui.components.AutoResizeText
import com.nuvio.tv.ui.components.ProfileAvatarCircle
import com.nuvio.tv.ui.theme.NuvioColors
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

private val SidebarLeadingVisualSize = 34.dp
private val SidebarContentGap = 12.dp
private val SidebarProfileContentGap = 12.dp
private val SidebarRowHeight = 48.dp
private val SidebarHeaderHeight = 52.dp
private val SidebarHeaderGap = 8.dp

@Composable
internal fun ModernSidebarBlurPanel(
    drawerItems: List<DrawerItem>,
    selectedDrawerRoute: String?,
    keepSidebarFocusDuringCollapse: Boolean,
    sidebarLabelAlpha: Float,
    sidebarIconScale: Float,
    sidebarExpandProgress: Float,
    isSidebarExpanded: Boolean,
    sidebarCollapsePending: Boolean,
    blurEnabled: Boolean,
    sidebarHazeState: HazeState,
    panelShape: RoundedCornerShape,
    drawerItemFocusRequesters: Map<String, FocusRequester>,
    focusedItemIndex: Int,
    onDrawerItemFocused: (Int) -> Unit,
    onDrawerItemClick: (String) -> Unit,
    activeProfileName: String,
    activeProfileColorHex: String,
    activeProfileAvatarImageUrl: String?,
    showProfileInSidebar: Boolean,
    showProfileSelector: Boolean,
    onSwitchProfile: () -> Unit
) {
    val delayedBlurProgress =
        ((sidebarExpandProgress - 0.34f) / 0.66f).coerceIn(0f, 1f)
    val showPanelBlur = blurEnabled &&
        isSidebarExpanded &&
        !sidebarCollapsePending &&
        delayedBlurProgress > 0f
    val expandedPanelBlurModifier = if (showPanelBlur) {
        Modifier.hazeChild(
            state = sidebarHazeState,
            shape = panelShape,
            tint = Color.Unspecified,
            blurRadius = (26f * delayedBlurProgress).dp,
            noiseFactor = 0.04f * delayedBlurProgress
        )
    } else {
        Modifier
    }
    val bgElevated = NuvioColors.BackgroundElevated
    val bgCard = NuvioColors.BackgroundCard
    val borderBase = NuvioColors.Border
    val panelBackgroundBrush = remember(blurEnabled, bgElevated, bgCard) {
        if (blurEnabled) {
            Brush.verticalGradient(listOf(Color(0xD64A4F59), Color(0xCC3F454F), Color(0xC640474F)))
        } else {
            Brush.verticalGradient(listOf(bgElevated, bgCard))
        }
    }
    val panelBorderColor = remember(blurEnabled, borderBase) {
        if (blurEnabled) Color.White.copy(alpha = 0.14f) else borderBase.copy(alpha = 0.9f)
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .graphicsLayer {
                val p = sidebarExpandProgress
                alpha = p
                val s = 0.97f + (0.03f * p)
                scaleX = s
                scaleY = s
                transformOrigin = TransformOrigin(0f, 0f)
            }
            .then(expandedPanelBlurModifier)
            .graphicsLayer {
                shape = panelShape
                clip = true
            }
            .clip(panelShape)
            .background(brush = panelBackgroundBrush, shape = panelShape)
            .border(width = 1.dp, color = panelBorderColor, shape = panelShape)
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        val profileFocusRequester = remember { FocusRequester() }
        val firstNavRequestRoute = drawerItems.firstOrNull()?.route
        val lastNavRequestRoute = drawerItems.lastOrNull()?.route
        val firstNavRequester = firstNavRequestRoute?.let(drawerItemFocusRequesters::get)
        val lastNavRequester = lastNavRequestRoute?.let(drawerItemFocusRequesters::get)

        if (showProfileInSidebar && activeProfileName.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SidebarHeaderHeight),
                contentAlignment = Alignment.Center
            ) {
                SidebarProfileItem(
                    profileName = activeProfileName,
                    profileColorHex = activeProfileColorHex,
                    profileAvatarImageUrl = activeProfileAvatarImageUrl,
                    focusEnabled = keepSidebarFocusDuringCollapse,
                    labelAlpha = sidebarLabelAlpha,
                    onFocusChanged = { focused ->
                        if (focused) onDrawerItemFocused(0)
                    },
                    onClick = onSwitchProfile,
                    downFocusRequester = firstNavRequester,
                    upFocusRequester = lastNavRequester,
                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .height(SidebarRowHeight)
                        .focusRequester(profileFocusRequester)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SidebarHeaderHeight),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo_wordmark),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .fillMaxWidth(0.76f)
                        .height(34.dp),
                    alpha = sidebarLabelAlpha
                )
            }
        }

        Spacer(modifier = Modifier.height(SidebarHeaderGap))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type != androidx.compose.ui.input.key.KeyEventType.KeyDown) return@onKeyEvent false
                    val firstRequester = drawerItems.firstOrNull()?.route?.let(drawerItemFocusRequesters::get)
                    val lastRequester = drawerItems.lastOrNull()?.route?.let(drawerItemFocusRequesters::get)
                    when (keyEvent.key) {
                        androidx.compose.ui.input.key.Key.DirectionDown -> {
                            if (focusedItemIndex == drawerItems.lastIndex + 1) {
                                firstRequester?.requestFocus()
                                true
                            } else false
                        }
                        androidx.compose.ui.input.key.Key.DirectionUp -> {
                            if (focusedItemIndex == 1 && showProfileInSidebar && activeProfileName.isNotEmpty()) {
                                profileFocusRequester.requestFocus()
                                true
                            } else if (focusedItemIndex == 1) {
                                lastRequester?.requestFocus()
                                true
                            } else false
                        }
                        else -> false
                    }
                },
            contentPadding = PaddingValues(top = 2.dp, bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(
                items = drawerItems,
                key = { _, item -> item.route }
            ) { index, item ->
                val isFirst = index == 0
                val isLast = index == drawerItems.lastIndex
                val firstRequester = drawerItems.firstOrNull()?.route?.let(drawerItemFocusRequesters::get)
                val lastRequester = drawerItems.lastOrNull()?.route?.let(drawerItemFocusRequesters::get)
                val itemUpFocusRequester = if (isFirst && showProfileInSidebar && activeProfileName.isNotEmpty()) profileFocusRequester else null
                SidebarNavigationItem(
                    label = item.label,
                    iconRes = item.iconRes,
                    icon = item.icon,
                    selected = selectedDrawerRoute == item.route,
                    focusEnabled = keepSidebarFocusDuringCollapse,
                    labelAlpha = sidebarLabelAlpha,
                    iconScale = sidebarIconScale,
                    onFocusChanged = {
                        if (it) {
                            onDrawerItemFocused(index + 1)
                        }
                    },
                    onClick = { onDrawerItemClick(item.route) },
                    upFocusRequester = itemUpFocusRequester,
                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .height(SidebarRowHeight)
                        .focusRequester(drawerItemFocusRequesters.getValue(item.route))
                        .focusProperties {
                            if (itemUpFocusRequester != null) up = itemUpFocusRequester
                            if (isLast && firstRequester != null) down = firstRequester
                        }
                )
            }
        }
    }
}

@Composable
private fun SidebarNavigationItem(
    label: String,
    iconRes: Int?,
    icon: ImageVector?,
    selected: Boolean,
    focusEnabled: Boolean,
    labelAlpha: Float,
    iconScale: Float,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    upFocusRequester: FocusRequester? = null,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(999.dp)
    val backgroundColor by animateColorAsState(
        targetValue = when {
            selected -> Color.White
            isFocused -> Color.White.copy(alpha = 0.18f)
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 180),
        label = "sidebarItemBackground"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Color.White.copy(alpha = 0.4f) else Color.Transparent,
        animationSpec = tween(durationMillis = 180),
        label = "sidebarItemBorder"
    )

    val contentColor = if (selected) Color(0xFF10151F) else Color.White
    val iconCircleColor = if (selected) Color(0xFFE7E2EF) else Color(0xFF6A6A74)
    Card(
        onClick = onClick,
        modifier = modifier
            .onFocusChanged {
                isFocused = it.hasFocus
                onFocusChanged(it.hasFocus)
            }
            .focusProperties {
                canFocus = focusEnabled
                if (upFocusRequester != null) up = upFocusRequester
            },
        colors = CardDefaults.colors(
            containerColor = backgroundColor,
            focusedContainerColor = backgroundColor,
        ),
        border = CardDefaults.border(
            border = androidx.tv.material3.Border.None,
            focusedBorder = androidx.tv.material3.Border(
                border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
                shape = shape
            )
        ),
        shape = CardDefaults.shape(shape = shape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
        Box(
            modifier = Modifier
                .size(SidebarLeadingVisualSize)
                .clip(CircleShape)
                .background(iconCircleColor)
                .padding(6.dp)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                },
            contentAlignment = Alignment.Center
        ) {
            when {
                icon != null -> Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )

                iconRes != null -> Icon(
                    painter = rememberRawSvgPainter(iconRes),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(SidebarContentGap))

        AutoResizeText(
            text = label,
            color = contentColor,
            modifier = Modifier
                .weight(1f)
                .graphicsLayer { alpha = labelAlpha },
            style = androidx.tv.material3.MaterialTheme.typography.titleLarge.copy(
                fontSize = 15.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
    }
}

@Composable
private fun SidebarProfileItem(
    profileName: String,
    profileColorHex: String,
    profileAvatarImageUrl: String?,
    focusEnabled: Boolean,
    labelAlpha: Float,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    downFocusRequester: FocusRequester? = null,
    upFocusRequester: FocusRequester? = null,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(999.dp)
    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused) Color.White.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.06f),
        animationSpec = tween(durationMillis = 180),
        label = "profileItemBackground"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Color.White.copy(alpha = 0.28f) else Color.Transparent,
        animationSpec = tween(durationMillis = 180),
        label = "profileItemBorder"
    )
    Card(
        onClick = onClick,
        modifier = modifier
            .onFocusChanged {
                isFocused = it.hasFocus
                onFocusChanged(it.hasFocus)
            }
            .focusProperties {
                canFocus = focusEnabled
                if (downFocusRequester != null) down = downFocusRequester
                if (upFocusRequester != null) up = upFocusRequester
            }
            .onPreviewKeyEvent {
                if (it.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (it.key) {
                    Key.DirectionDown -> {
                        downFocusRequester?.requestFocus()
                        true
                    }
                    Key.DirectionUp -> {
                        upFocusRequester?.requestFocus()
                        true
                    }
                    else -> false
                }
            },
        colors = CardDefaults.colors(
            containerColor = backgroundColor,
            focusedContainerColor = backgroundColor,
        ),
        border = CardDefaults.border(
            border = androidx.tv.material3.Border.None,
            focusedBorder = androidx.tv.material3.Border(
                border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
                shape = shape
            )
        ),
        shape = CardDefaults.shape(shape = shape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
        Box(
            modifier = Modifier.size(SidebarLeadingVisualSize),
            contentAlignment = Alignment.Center
        ) {
            ProfileAvatarCircle(
                name = profileName,
                colorHex = profileColorHex,
                size = SidebarLeadingVisualSize,
                avatarImageUrl = profileAvatarImageUrl,
                isSelected = isFocused
            )
        }
        Spacer(modifier = Modifier.width(SidebarProfileContentGap))
        AutoResizeText(
            text = profileName,
            color = Color.White,
            modifier = Modifier
                .weight(1f)
                .graphicsLayer { alpha = labelAlpha },
            style = androidx.tv.material3.MaterialTheme.typography.titleLarge.copy(
                fontSize = 15.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
    }
}

@Composable
private fun rememberRawSvgPainter(rawIconRes: Int): Painter {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val sizePx = with(density) { 24.dp.roundToPx() }
    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
            .data(rawIconRes)
            .size(sizePx)
            .build()
    )
}


