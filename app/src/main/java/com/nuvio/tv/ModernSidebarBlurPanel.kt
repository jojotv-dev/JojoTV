package com.nuvio.tv

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.nuvio.tv.ui.components.AutoResizeText
import com.nuvio.tv.ui.components.BrandWordmark
import com.nuvio.tv.ui.components.ProfileAvatarCircle
import com.nuvio.tv.ui.theme.NuvioComponents
import com.nuvio.tv.ui.theme.NuvioMotion
import com.nuvio.tv.ui.theme.NuvioRadii
import com.nuvio.tv.ui.theme.NuvioStrokes
import com.nuvio.tv.ui.theme.NuvioTheme
import com.nuvio.tv.ui.util.contentTextDirection
import com.nuvio.tv.ui.util.rememberRawSvgPainter
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect

private val SidebarLeadingVisualSize = NuvioComponents.tokens.sidebar.leadingVisual
private val SidebarContentGap = NuvioComponents.tokens.sidebar.contentGap
private val SidebarProfileContentGap = NuvioComponents.tokens.sidebar.contentGap + NuvioTheme.spacing.xs

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
    itemShape: RoundedCornerShape,
    drawerItemFocusRequesters: Map<String, FocusRequester>,
    profileFocusRequester: FocusRequester,
    onDrawerItemFocused: (Int) -> Unit,
    onDrawerItemClick: (String) -> Unit,
    activeProfileName: String,
    activeProfileColorHex: String,
    activeProfileAvatarImageUrl: String?,
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
        Modifier.hazeEffect(state = sidebarHazeState) {
            blurRadius = NuvioTheme.effects.blurPanel * delayedBlurProgress
            noiseFactor = 0.04f * delayedBlurProgress
            inputScale = HazeInputScale.Fixed(0.66f)
        }
    } else {
        Modifier
    }
    val colors = NuvioTheme.colors
    val bgElevated = colors.BackgroundElevated
    val bgCard = colors.BackgroundCard
    val borderBase = colors.Border
    val isAmoledBlack = bgElevated == Color.Black
    val panelBackgroundBrush = remember(blurEnabled, isAmoledBlack, bgElevated, bgCard) {
        val baseColor = if (isAmoledBlack) Color.Black else Color(0xFF161618)
        val alpha = when {
            blurEnabled -> 0.65f
            isAmoledBlack -> 1f
            else -> 0.97f
        }
        Brush.verticalGradient(listOf(
            baseColor.copy(alpha = alpha),
            baseColor.copy(alpha = alpha)
        ))
    }
    val panelBorderColor = remember(isAmoledBlack, blurEnabled, borderBase) {
        if (!blurEnabled && isAmoledBlack) borderBase.copy(alpha = 0.9f) else Color.Transparent
    }

    val isDense = drawerItems.size >= 7
    val itemSpacing = if (isDense) NuvioTheme.spacing.xs else (NuvioTheme.spacing.sm - NuvioTheme.spacing.xxs)

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
            .clip(panelShape)
            .then(expandedPanelBlurModifier)
            .background(brush = panelBackgroundBrush, shape = panelShape)
            .then(
                if (panelBorderColor != Color.Transparent) {
                    Modifier.border(width = NuvioStrokes.tokens.hairline, color = panelBorderColor, shape = panelShape)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = NuvioTheme.spacing.md, vertical = NuvioTheme.spacing.md)
    ) {
        if (showProfileSelector && activeProfileName.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = NuvioTheme.spacing.xxs),
                contentAlignment = Alignment.Center
            ) {
                SidebarProfileItem(
                    profileName = activeProfileName,
                    profileColorHex = activeProfileColorHex,
                    profileAvatarImageUrl = activeProfileAvatarImageUrl,
                    itemShape = itemShape,
                    focusEnabled = keepSidebarFocusDuringCollapse,
                    labelAlpha = sidebarLabelAlpha,
                    isDense = isDense,
                    focusRequester = profileFocusRequester,
                    onFocusChanged = { focused ->
                        if (focused) onDrawerItemFocused(drawerItems.size)
                    },
                    onClick = onSwitchProfile,
                    modifier = Modifier.fillMaxWidth(0.92f)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = NuvioTheme.spacing.xxs),
                contentAlignment = Alignment.Center
            ) {
                BrandWordmark(
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(36.dp),
                    alpha = sidebarLabelAlpha
                )
            }
        }

        Spacer(modifier = Modifier.height(if (isDense) NuvioTheme.spacing.sm else NuvioTheme.spacing.md))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(itemSpacing)
            ) {
                drawerItems.forEachIndexed { index, item ->
                    key(item.route) {
                        SidebarNavigationItem(
                            label = item.label,
                            iconRes = item.iconRes,
                            icon = item.icon,
                            selected = selectedDrawerRoute == item.route,
                            focusEnabled = keepSidebarFocusDuringCollapse,
                            labelAlpha = sidebarLabelAlpha,
                            iconScale = sidebarIconScale,
                            isDense = isDense,
                            itemShape = itemShape,
                            onFocusChanged = {
                                if (it) {
                                    onDrawerItemFocused(index)
                                }
                            },
                            onClick = { onDrawerItemClick(item.route) },
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .focusRequester(drawerItemFocusRequesters.getValue(item.route))
                        )
                    }
                }
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
    isDense: Boolean = false,
    itemShape: RoundedCornerShape,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val colors = NuvioTheme.colors
    val shape = itemShape
    val palette = NuvioTheme.palette
    val accentColor = palette.secondary
    val backgroundColorTarget = when {
        isFocused && selected -> accentColor.copy(alpha = 0.28f)
        isFocused -> Color.White.copy(alpha = 0.12f)
        selected -> accentColor.copy(alpha = 0.15f)
        else -> Color.Transparent
    }
    val animatedBackgroundColor by animateColorAsState(
        targetValue = backgroundColorTarget,
        animationSpec = tween(durationMillis = NuvioMotion.tokens.durations.fast),
        label = "sidebarItemBackground"
    )
    val backgroundColor = if (selected && !isFocused) backgroundColorTarget else animatedBackgroundColor

    val contentColorTarget = when {
        selected -> accentColor
        isFocused -> colors.TextPrimary
        else -> colors.text.onOverlay
    }
    val animatedContentColor by animateColorAsState(
        targetValue = contentColorTarget,
        animationSpec = tween(durationMillis = NuvioMotion.tokens.durations.fast),
        label = "sidebarItemContent"
    )
    val contentColor = if (selected && !isFocused) contentColorTarget else animatedContentColor

    val iconTintTarget = when {
        selected -> accentColor
        isFocused -> colors.TextPrimary
        else -> colors.text.onOverlay
    }
    val animatedIconTint by animateColorAsState(
        targetValue = iconTintTarget,
        animationSpec = tween(durationMillis = NuvioMotion.tokens.durations.fast),
        label = "sidebarItemIconTint"
    )
    val iconTint = if (selected && !isFocused) iconTintTarget else animatedIconTint
    Card(
        onClick = onClick,
        modifier = modifier
            .onFocusChanged {
                isFocused = it.hasFocus
                onFocusChanged(it.hasFocus)
            }
            .focusProperties { canFocus = focusEnabled },
        colors = CardDefaults.colors(
            containerColor = backgroundColor,
            focusedContainerColor = backgroundColor,
        ),
        border = CardDefaults.border(
            border = androidx.tv.material3.Border.None,
            focusedBorder = androidx.tv.material3.Border(
                border = androidx.compose.foundation.BorderStroke(NuvioStrokes.tokens.thin, Color.Transparent),
                shape = shape
            )
        ),
        shape = CardDefaults.shape(shape = shape),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f)
    ) {
        Row(
            modifier = Modifier
                .semantics {
                    contentDescription = label
                    role = Role.Tab
                    this.selected = selected
                }
                .fillMaxWidth()
                .padding(
                    horizontal = NuvioTheme.spacing.lg - NuvioTheme.spacing.xxs,
                    vertical = if (isDense) NuvioTheme.spacing.sm - NuvioTheme.spacing.hairline else NuvioTheme.spacing.sm + NuvioTheme.spacing.xxs
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
        Box(
            modifier = Modifier
                .size(SidebarLeadingVisualSize)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                },
            contentAlignment = Alignment.Center
        ) {
            val iconModifier = Modifier.size(NuvioComponents.tokens.sidebar.iconSize)
            when {
                icon != null -> Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = iconModifier
                )
                iconRes != null -> Icon(
                    painter = rememberRawSvgPainter(iconRes),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = iconModifier
                )
            }
        }
        Spacer(modifier = Modifier.width(SidebarContentGap))

        AutoResizeText(
            text = label,
            color = contentColor,
            style = NuvioTheme.textStyles.nav,
            minFontSize = NuvioTheme.textStyles.nav.fontSize,
            modifier = Modifier
                .weight(1f)
                .graphicsLayer { alpha = labelAlpha }
                .then(if (isFocused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier)
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
    isDense: Boolean = false,
    focusRequester: FocusRequester,
    itemShape: RoundedCornerShape,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val colors = NuvioTheme.colors
    val shape = itemShape
    val backgroundColor = if (isFocused) Color.White.copy(alpha = 0.12f) else Color.Transparent
    Card(
        onClick = onClick,
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.hasFocus
                onFocusChanged(it.hasFocus)
            }
            .focusProperties { canFocus = focusEnabled },
        colors = CardDefaults.colors(
            containerColor = backgroundColor,
            focusedContainerColor = backgroundColor,
        ),
        border = CardDefaults.border(
            border = androidx.tv.material3.Border.None,
            focusedBorder = androidx.tv.material3.Border(
                border = androidx.compose.foundation.BorderStroke(NuvioStrokes.tokens.thin, Color.Transparent),
                shape = shape
            )
        ),
        shape = CardDefaults.shape(shape = shape),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f)
    ) {
        Row(
            modifier = Modifier
                .semantics {
                    contentDescription = profileName
                    role = Role.Button
                }
                .fillMaxWidth()
                .padding(
                    horizontal = NuvioTheme.spacing.lg - NuvioTheme.spacing.xxs,
                    vertical = if (isDense) NuvioTheme.spacing.sm - NuvioTheme.spacing.hairline else NuvioTheme.spacing.sm + NuvioTheme.spacing.xxs
                ),
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
                imageCrossfade = false
            )
        }
        Spacer(modifier = Modifier.width(SidebarProfileContentGap))
        val profileLabelStyle = NuvioTheme.textStyles.nav.copy(
            fontWeight = FontWeight.SemiBold,
            textDirection = profileName.contentTextDirection()
        )
        AutoResizeText(
            text = profileName,
            color = colors.text.onOverlay,
            style = profileLabelStyle,
            minFontSize = profileLabelStyle.fontSize,
            modifier = Modifier
                .weight(1f)
                .graphicsLayer { alpha = labelAlpha }
                .then(if (isFocused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier)
        )
        }
    }
}
