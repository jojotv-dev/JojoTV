@file:OptIn(
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.compose.ui.ExperimentalComposeUiApi::class,
    androidx.tv.material3.ExperimentalTvMaterial3Api::class
)

package com.nuvio.tv.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import android.view.KeyEvent as AndroidKeyEvent
import com.nuvio.tv.R
import com.nuvio.tv.core.apps.InstalledApp
import com.nuvio.tv.core.apps.launchInstalledApp
import com.nuvio.tv.ui.screens.apps.AppContextMenuModal
import com.nuvio.tv.ui.screens.apps.AppContextMenuViewModel
import com.nuvio.tv.ui.screens.apps.LocalNavigateToManageHomeApps
import com.nuvio.tv.ui.theme.NuvioTheme
import com.nuvio.tv.ui.util.rememberLongPressKeyTracker

val InstalledAppTileWidth = 180.dp
val InstalledAppTileCornerRadius = 12.dp

/** Artwork-only tile, 16:9 so TV banners fill it without letterboxing. */
val InstalledAppTileHeight = 101.dp

private fun isSelectKey(keyCode: Int): Boolean {
    return keyCode == AndroidKeyEvent.KEYCODE_DPAD_CENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_ENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_NUMPAD_ENTER
}

@Composable
fun InstalledAppArtwork(
    app: InstalledApp,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val artwork = app.displayIcon
    if (artwork != null) {
        Image(
            bitmap = artwork,
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        Icon(
            imageVector = Icons.Default.Android,
            contentDescription = null,
            tint = NuvioTheme.colors.TextSecondary,
            modifier = modifier
        )
    }
}

/**
 * A bare, rounded app artwork tile: no card background, no resting border and no label, so the row
 * reads as a strip of artwork. Only the focus ring is drawn, because the D-pad needs a visible target.
 */
@Composable
fun InstalledAppTile(
    app: InstalledApp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tileWidth: Dp = InstalledAppTileWidth,
    cornerRadius: Dp = InstalledAppTileCornerRadius,
    onLongClick: (() -> Unit)? = null,
    onFocused: () -> Unit = {}
) {
    val shape = NuvioTheme.configuredShape(RoundedCornerShape(cornerRadius))
    val longPressKeyTracker = rememberLongPressKeyTracker()
    var longPressTriggered by remember { mutableStateOf(false) }

    Card(
        onClick = {
            if (longPressTriggered) {
                longPressTriggered = false
            } else {
                onClick()
            }
        },
        modifier = modifier
            .width(tileWidth)
            .height(tileWidth * 9f / 16f)
            .onPreviewKeyEvent { event ->
                val native = event.nativeKeyEvent
                if (onLongClick != null &&
                    longPressKeyTracker.handle(native, ::isSelectKey) {
                        longPressTriggered = true
                        onLongClick()
                    }
                ) {
                    return@onPreviewKeyEvent true
                }
                false
            }
            .onFocusChanged { if (it.isFocused) onFocused() },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        ),
        // No resting border and a transparent container: the artwork is the card. Same pattern as
        // ContentCard, so the only chrome is the focus ring.
        border = CardDefaults.border(
            focusedBorder = Border(
                border = NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs),
                shape = shape
            )
        ),
        scale = CardDefaults.scale(focusedScale = 1.03f)
    ) {
        // The artwork carries the app name for screen readers now that the label is gone.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .semantics { contentDescription = app.displayLabel },
            contentAlignment = Alignment.Center
        ) {
            if (app.isBanner) {
                InstalledAppArtwork(
                    app = app,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(shape)
                )
            } else {
                InstalledAppArtwork(app = app, modifier = Modifier.size(64.dp))
            }
        }
    }
}

/** Shared by Classic and Grid; Modern uses the same tile inside its existing focus-aware row. */
@Composable
fun InstalledAppsRowSection(
    apps: List<InstalledApp>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    rowFocusRequester: FocusRequester = remember { FocusRequester() },
    focusedItemIndex: Int = -1,
    onItemFocused: (Int) -> Unit = {},
    horizontalPadding: Dp = 52.dp,
    tileWidth: Dp = InstalledAppTileWidth,
    cornerRadius: Dp = InstalledAppTileCornerRadius,
    showTitle: Boolean = true,
    cyclicTail: Boolean = false,
    focusRequesterForPackage: ((String) -> FocusRequester)? = null,
    menuViewModel: AppContextMenuViewModel = hiltViewModel(),
    focusResetToken: Int = 0,
    rowBringIntoViewSpec: BringIntoViewSpec? = null
) {
    val context = LocalContext.current
    val launchError = stringResource(R.string.installed_apps_launch_error)
    val currentOnItemFocused by rememberUpdatedState(onItemFocused)
    val navigateToManageHomeApps = LocalNavigateToManageHomeApps.current
    var lastIndex by remember { mutableIntStateOf(-1) }
    var lastFocusResetToken by remember { mutableIntStateOf(focusResetToken) }
    var contextMenuApp by remember { mutableStateOf<InstalledApp?>(null) }
    var contextMenuIndex by remember { mutableIntStateOf(-1) }
    var pendingCyclicFocusIndex by remember { mutableIntStateOf(-1) }
    val cyclicTailFocusRequester = remember(rowFocusRequester) { FocusRequester() }
    var cyclicTailFocused by remember(rowFocusRequester) { mutableStateOf(false) }
    val appRowBringIntoViewSpec = rowBringIntoViewSpec ?: LocalBringIntoViewSpec.current
    if (lastFocusResetToken != focusResetToken) {
        lastFocusResetToken = focusResetToken
        lastIndex = -1
        cyclicTailFocused = false
    }
    val viewportWidth = LocalConfiguration.current.screenWidthDp.dp
    val cyclicTailIndices = remember(apps.size, tileWidth, viewportWidth) {
        cyclicThumbnailTailIndices(List(apps.size) { tileWidth }, viewportWidth)
    }
    val requesters = remember { mutableMapOf<String, FocusRequester>() }
    LaunchedEffect(apps) { requesters.keys.retainAll(apps.map { it.packageName }.toSet()) }
    LaunchedEffect(focusedItemIndex, apps) {
        if (cyclicTailFocused) return@LaunchedEffect
        val app = apps.getOrNull(focusedItemIndex) ?: return@LaunchedEffect
        repeat(2) { withFrameNanos { } }
        runCatching {
            requesters.getOrPut(app.packageName) { FocusRequester() }.requestFocus()
        }
    }
    LaunchedEffect(pendingCyclicFocusIndex, apps.size) {
        val targetIndex = pendingCyclicFocusIndex
        if (targetIndex == apps.size && apps.size > 1) {
            listState.animateScrollToItem(targetIndex)
            var focusRequested = false
            repeat(8) {
                if (!focusRequested) {
                    withFrameNanos { }
                    focusRequested = runCatching { cyclicTailFocusRequester.requestFocus() }.getOrDefault(false)
                }
            }
        } else if (targetIndex in apps.indices) {
            listState.animateScrollToItem(targetIndex)
            val app = apps[targetIndex]
            val requester = requesters.getOrPut(app.packageName) {
                focusRequesterForPackage?.invoke(app.packageName) ?: FocusRequester()
            }
            var focusRequested = false
            repeat(8) {
                if (!focusRequested) {
                    focusRequested = runCatching { requester.requestFocus() }.getOrDefault(false)
                    if (!focusRequested) withFrameNanos { }
                }
            }
        } else {
            return@LaunchedEffect
        }
        pendingCyclicFocusIndex = -1
    }
    Column(modifier = modifier.fillMaxWidth()) {
        if (showTitle) {
            Text(
                text = stringResource(R.string.nav_apps),
                style = MaterialTheme.typography.titleMedium,
                color = NuvioTheme.colors.TextPrimary,
                modifier = Modifier.padding(start = horizontalPadding, bottom = 14.dp)
            )
        }
        CompositionLocalProvider(LocalBringIntoViewSpec provides appRowBringIntoViewSpec) {
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth().focusRequester(rowFocusRequester).focusRestorer {
                    requesters[apps.getOrNull(lastIndex.coerceAtLeast(0))?.packageName] ?: FocusRequester.Default
                }.onPreviewKeyEvent { event ->
                    if (!cyclicTail || event.type != androidx.compose.ui.input.key.KeyEventType.KeyDown || apps.size < 2) {
                        return@onPreviewKeyEvent false
                    }
                    when {
                        cyclicTailFocused && event.key == Key.DirectionLeft -> {
                            pendingCyclicFocusIndex = apps.lastIndex
                            true
                        }
                        cyclicTailFocused && event.key == Key.DirectionRight -> {
                            pendingCyclicFocusIndex = 1
                            true
                        }
                        event.key == Key.DirectionLeft && lastIndex == 0 -> {
                            pendingCyclicFocusIndex = apps.lastIndex
                            true
                        }
                        else -> false
                    }
                }.focusGroup(),
                contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md)
            ) {
                itemsIndexed(apps, key = { _, app -> app.packageName }) { index, app ->
                    val requester = focusRequesterForPackage?.invoke(app.packageName)
                        ?: requesters.getOrPut(app.packageName) { FocusRequester() }
                    LaunchedEffect(app.packageName) {
                        requesters[app.packageName] = requester
                    }
                    InstalledAppTile(
                        app = app,
                        modifier = Modifier.focusRequester(requester),
                        tileWidth = tileWidth,
                        cornerRadius = cornerRadius,
                        onFocused = {
                            lastIndex = index
                            currentOnItemFocused(index)
                        },
                        onClick = {
                            if (!launchInstalledApp(context, app)) {
                                Toast.makeText(context, launchError, Toast.LENGTH_SHORT).show()
                            }
                        },
                        onLongClick = {
                            contextMenuApp = app
                            contextMenuIndex = index
                        }
                    )
                }
                if (cyclicTail && apps.isNotEmpty()) {
                    cyclicTailIndices.forEachIndexed { tailIndex, sourceIndex ->
                        val tailApp = apps[sourceIndex]
                        val isFocusableTail = tailIndex == 0 && apps.size > 1
                        item(key = "installed_apps_cyclic_tail_${tailIndex}_${tailApp.packageName}") {
                            InstalledAppTile(
                                app = tailApp,
                                onClick = {
                                    if (isFocusableTail && !launchInstalledApp(context, tailApp)) {
                                        Toast.makeText(context, launchError, Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = if (isFocusableTail) {
                                    Modifier
                                        .focusRequester(cyclicTailFocusRequester)
                                        .onFocusChanged { focusState ->
                                            cyclicTailFocused = focusState.isFocused
                                            if (focusState.isFocused) {
                                                lastIndex = 0
                                                currentOnItemFocused(apps.lastIndex)
                                            }
                                        }
                                } else Modifier.focusProperties { canFocus = false },
                                tileWidth = tileWidth,
                                cornerRadius = cornerRadius,
                                onFocused = { if (isFocusableTail) lastIndex = 0 },
                            )
                        }
                    }
                }
            }
        }
    }

    contextMenuApp?.let { app ->
        val currentIndex = apps.indexOfFirst { it.packageName == app.packageName }
        val allPackages = remember(apps) { apps.map { it.packageName } }
        AppContextMenuModal(
            app = app,
            onDismiss = {
                contextMenuApp = null
                contextMenuIndex = -1
            },
            onMoveLeft = if (currentIndex > 0) {
                {
                    menuViewModel.moveApp(app.packageName, -1, allPackages)
                }
            } else null,
            onMoveRight = if (currentIndex in 0 until apps.size - 1) {
                {
                    menuViewModel.moveApp(app.packageName, 1, allPackages)
                }
            } else null,
            onRemoveFromRow = {
                menuViewModel.removeFromRow(app.packageName, allPackages)
            },
            onManageOrder = navigateToManageHomeApps?.let { navAction ->
                {
                    contextMenuApp = null
                    contextMenuIndex = -1
                    navAction()
                }
            },
            viewModel = menuViewModel
        )
    }
}
