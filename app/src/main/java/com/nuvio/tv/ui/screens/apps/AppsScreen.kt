@file:OptIn(
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.tv.material3.ExperimentalTvMaterial3Api::class
)

package com.nuvio.tv.ui.screens.apps

import android.content.Intent
import android.provider.Settings
import android.view.KeyEvent as AndroidKeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.core.apps.InstalledApp
import com.nuvio.tv.core.apps.launchInstalledApp
import com.nuvio.tv.ui.components.InstalledAppArtwork
import com.nuvio.tv.ui.components.InstalledAppTileCornerRadius
import com.nuvio.tv.ui.components.rememberLauncherSetupState
import com.nuvio.tv.ui.theme.NuvioTheme
import com.nuvio.tv.ui.util.rememberLongPressKeyTracker
import android.widget.Toast

@Composable
fun AppsScreen(
    onManageHomeApps: () -> Unit = {},
    viewModel: AppsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    embedded: Boolean = false,
    contentFocusRequester: FocusRequester? = null
) {
    val appsState by viewModel.state.collectAsState()
    val context = LocalContext.current
    val launcherSetup = rememberLauncherSetupState()
    val menuViewModel: AppContextMenuViewModel = hiltViewModel()
    val firstItemFocusRequester = remember(contentFocusRequester) {
        contentFocusRequester ?: FocusRequester()
    }
    var contextMenuApp by remember { mutableStateOf<InstalledApp?>(null) }
    var showHiddenApps by remember { mutableStateOf(false) }
    val allPackages = remember(appsState.apps) { appsState.apps.map { it.packageName } }
    val displayedApps = if (showHiddenApps) appsState.apps else {
        appsState.apps.filterNot { it.packageName in appsState.hiddenFromAppsPagePackages }
    }
    val hasHiddenApps = appsState.apps.any { it.packageName in appsState.hiddenFromAppsPagePackages }
    val launchError = stringResource(R.string.installed_apps_launch_error)

    LaunchedEffect(appsState.isLoading, displayedApps.firstOrNull()?.packageName) {
        if (appsState.isLoading) return@LaunchedEffect
        repeat(2) { withFrameNanos { } }
        runCatching { firstItemFocusRequester.requestFocus() }
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalNavigateToManageHomeApps provides onManageHomeApps
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    horizontal = if (embedded) 24.dp else 48.dp,
                    vertical = if (embedded) 8.dp else NuvioTheme.spacing.xl
                )
        ) {
            if (!embedded) {
                Text(
                    text = stringResource(R.string.nav_apps),
                    style = MaterialTheme.typography.headlineMedium,
                    color = NuvioTheme.colors.TextPrimary,
                    modifier = Modifier.padding(start = 12.dp, bottom = NuvioTheme.spacing.md)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.sm + 10.dp)
            ) {
                itemsIndexed(
                    items = displayedApps,
                    key = { _, app -> "installed_${app.packageName}" }
                ) { index, app ->
                    InstalledAppGridTile(
                        app = app,
                        modifier = if (index == 0) Modifier.focusRequester(firstItemFocusRequester) else Modifier,
                        onClick = {
                            if (!launchInstalledApp(context, app)) {
                                Toast.makeText(context, launchError, Toast.LENGTH_SHORT).show()
                            }
                        },
                        onLongClick = { contextMenuApp = app }
                    )
                }

                item(key = "action_default_launcher") {
                    AppsActionTile(
                        title = stringResource(
                            if (launcherSetup.isDefaultHome) R.string.apps_default_launcher_active
                            else R.string.apps_set_default_launcher
                        ),
                        icon = Icons.Default.Home,
                        modifier = if (displayedApps.isEmpty()) {
                            Modifier.focusRequester(firstItemFocusRequester)
                        } else Modifier,
                        onClick = launcherSetup.requestDefaultHome
                    )
                }
                item(key = "action_home_override") {
                    AppsActionTile(
                        title = stringResource(
                            when {
                                launcherSetup.isAccessibilityActive -> R.string.apps_home_override_active
                                launcherSetup.isAccessibilityEnabled -> R.string.apps_home_override_pending
                                else -> R.string.apps_home_override
                            }
                        ),
                        icon = Icons.Default.Home,
                        onClick = launcherSetup.openAccessibilitySettings
                    )
                }
                item(key = "action_manage_home_row") {
                    AppsActionTile(
                        title = stringResource(R.string.apps_manage_home_row),
                        icon = Icons.Default.Tune,
                        onClick = onManageHomeApps
                    )
                }
                if (hasHiddenApps) {
                    item(key = "action_toggle_hidden_apps") {
                        AppsActionTile(
                            title = stringResource(
                                if (showHiddenApps) R.string.apps_hide_hidden else R.string.apps_show_hidden
                            ),
                            icon = if (showHiddenApps) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            onClick = { showHiddenApps = !showHiddenApps }
                        )
                    }
                }
                item(key = "action_android_settings") {
                    AppsActionTile(
                        title = stringResource(R.string.apps_android_settings),
                        icon = Icons.Default.Settings,
                        onClick = {
                            runCatching { context.startActivity(Intent(Settings.ACTION_SETTINGS)) }
                        }
                    )
                }
            }
        }

        contextMenuApp?.let { app ->
            val currentIndex = appsState.apps.indexOfFirst { it.packageName == app.packageName }
            val isHiddenFromAppsPage = app.packageName in appsState.hiddenFromAppsPagePackages
            AppContextMenuModal(
                app = app,
                onDismiss = { contextMenuApp = null },
                onMoveLeft = if (currentIndex > 0) {
                    { menuViewModel.moveApp(app.packageName, -1, allPackages) }
                } else null,
                onMoveRight = if (currentIndex in 0 until appsState.apps.lastIndex) {
                    { menuViewModel.moveApp(app.packageName, 1, allPackages) }
                } else null,
                onRemoveFromRow = { menuViewModel.removeFromRow(app.packageName, allPackages) },
                isHiddenFromAppsPage = isHiddenFromAppsPage,
                onToggleAppsPageVisibility = {
                    menuViewModel.setHiddenFromAppsPage(app.packageName, !isHiddenFromAppsPage)
                },
                onManageOrder = {
                    contextMenuApp = null
                    onManageHomeApps()
                },
                viewModel = menuViewModel
            )
        }
    }
}

@Composable
private fun InstalledAppGridTile(
    app: InstalledApp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val shape = NuvioTheme.configuredShape(RoundedCornerShape(NuvioTheme.radii.lg))
    val artworkShape = NuvioTheme.configuredShape(RoundedCornerShape(InstalledAppTileCornerRadius))
    val longPressTracker = rememberLongPressKeyTracker()
    var longPressTriggered by remember(app.packageName) { mutableStateOf(false) }
    var focused by remember(app.packageName) { mutableStateOf(false) }

    Card(
        onClick = {
            if (longPressTriggered) {
                longPressTriggered = false
            } else {
                onClick()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .onPreviewKeyEvent { event ->
                longPressTracker.handle(event.nativeKeyEvent, ::isSelectKey) {
                    longPressTriggered = true
                    onLongClick()
                }
            }
            .onFocusChanged { focused = it.isFocused }
            .semantics { contentDescription = app.displayLabel },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        ),
        border = CardDefaults.border(
            border = Border.None,
            focusedBorder = Border.None
        ),
        scale = CardDefaults.scale(focusedScale = 1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 160.dp)
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentAlignment = Alignment.Center
            ) {
                val iconFrameModifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (focused) Modifier.border(
                            NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs),
                            artworkShape
                        ) else Modifier
                    )
                    .clip(artworkShape)
                InstalledAppArtwork(
                    app = app,
                    modifier = iconFrameModifier,
                    contentScale = ContentScale.Fit
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.displayLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = NuvioTheme.colors.TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Clip
                )
            }
        }
    }
}

@Composable
private fun AppsActionTile(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = NuvioTheme.configuredShape(RoundedCornerShape(NuvioTheme.radii.lg))
    val iconShape = NuvioTheme.configuredShape(RoundedCornerShape(InstalledAppTileCornerRadius))
    var focused by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused }
            .semantics { contentDescription = title },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        ),
        border = CardDefaults.border(
            border = Border.None,
            focusedBorder = Border.None
        ),
        scale = CardDefaults.scale(focusedScale = 1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 160.dp)
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(iconShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .then(
                        if (focused) Modifier.border(
                            NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs),
                            iconShape
                        ) else Modifier.border(1.dp, NuvioTheme.colors.Border, iconShape)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (focused) NuvioTheme.colors.Primary else NuvioTheme.colors.TextSecondary,
                    modifier = Modifier.size(48.dp)
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = NuvioTheme.colors.TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 4,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun isSelectKey(keyCode: Int): Boolean =
    keyCode == AndroidKeyEvent.KEYCODE_DPAD_CENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_ENTER ||
        keyCode == AndroidKeyEvent.KEYCODE_NUMPAD_ENTER
