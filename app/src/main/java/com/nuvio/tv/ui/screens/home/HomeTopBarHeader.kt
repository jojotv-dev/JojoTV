@file:OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class)

package com.nuvio.tv.ui.screens.home

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.LocalContentFocusRequester
import com.nuvio.tv.ui.screens.ServerBrowserViewModel
import com.nuvio.tv.ui.screens.ServerHubViewModel
import com.nuvio.tv.ui.components.FocusMarqueeText
import com.nuvio.tv.ui.theme.NuvioTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeTopBarHeader(
    showCalendarShortcut: Boolean,
    onCalendarClick: () -> Unit,
    onServersClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    onServerSourceSelected: (String) -> Unit = { onServersClick() },
    selectedServerId: String? = null,
    onAppsClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val contentFocusRequester = LocalContentFocusRequester.current
    val serverHubViewModel: ServerHubViewModel = hiltViewModel()
    val serverState by serverHubViewModel.servers.collectAsStateWithLifecycle()
    val freeboxState by serverHubViewModel.freebox.collectAsStateWithLifecycle()
    val capacityState by serverHubViewModel.capacityState.collectAsStateWithLifecycle()
    var serverMenuExpanded by remember { mutableStateOf(false) }
    val serverMenuFocusRequester = remember { FocusRequester() }
    val serverMenuOffset = with(LocalDensity.current) { IntOffset(0, 44.dp.roundToPx()) }

    LaunchedEffect(serverMenuExpanded, serverState.enabledConnections, freeboxState.address) {
        if (serverMenuExpanded) serverHubViewModel.refreshStorageCapacity()
    }
    LaunchedEffect(serverMenuExpanded) {
        if (serverMenuExpanded) {
            withFrameNanos { }
            runCatching { serverMenuFocusRequester.requestFocus() }
        }
    }

    val nowMillis by produceState(initialValue = System.currentTimeMillis()) {
        while (true) {
            val millisUntilNextMinute = 60_000L - (System.currentTimeMillis() % 60_000L)
            delay(millisUntilNextMinute.coerceAtLeast(250L))
            value = System.currentTimeMillis()
        }
    }
    val locale = remember(context) {
        context.resources.configuration.locales[0] ?: Locale.getDefault()
    }
    val timeText = remember(context, nowMillis) {
        android.text.format.DateFormat.getTimeFormat(context).format(Date(nowMillis))
    }
    val datePattern = remember(locale) {
        android.text.format.DateFormat.getBestDateTimePattern(locale, "EEE d MMM")
    }
    val dateText = remember(datePattern, locale, nowMillis) {
        SimpleDateFormat(datePattern, locale)
            .format(Date(nowMillis))
            .replaceFirstChar { it.titlecase(locale) }
    }

    val isConnected = remember(context) { isNetworkConnected(context) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 48.dp, top = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.weight(1f))
        Image(
            painter = painterResource(R.drawable.app_logo_wordmark_tintable),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(NuvioTheme.colors.Secondary),
            modifier = Modifier
                .width(156.dp)
                .height(60.dp)
        )
        Spacer(Modifier.weight(1f))

        if (isConnected) {
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }

        if (onAppsClick != null) {
            Surface(
                onClick = onAppsClick,
                modifier = Modifier
                    .focusProperties { down = contentFocusRequester }
                    .size(36.dp),
                shape = ClickableSurfaceDefaults.shape(NuvioTheme.configuredShape(RoundedCornerShape(10.dp))),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = Color.White.copy(alpha = 0.08f),
                    focusedContainerColor = Color.White.copy(alpha = 0.22f)
                ),
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
                border = ClickableSurfaceDefaults.border(
                    border = Border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))),
                    focusedBorder = Border(BorderStroke(2.dp, Color.White))
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = stringResource(R.string.nav_apps),
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (showCalendarShortcut) {
            Surface(
                onClick = onCalendarClick,
                modifier = Modifier
                    .focusProperties { down = contentFocusRequester }
                    .size(36.dp),
                shape = ClickableSurfaceDefaults.shape(NuvioTheme.configuredShape(RoundedCornerShape(10.dp))),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = Color.White.copy(alpha = 0.08f),
                    focusedContainerColor = Color.White.copy(alpha = 0.22f)
                ),
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
                border = ClickableSurfaceDefaults.border(
                    border = Border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))),
                    focusedBorder = Border(BorderStroke(2.dp, Color.White))
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = stringResource(R.string.calendar_title),
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // TV Sources Button
        Surface(
            onClick = { openSystemSources(context) },
            modifier = Modifier
                .focusProperties { down = contentFocusRequester }
                .size(36.dp),
            shape = ClickableSurfaceDefaults.shape(NuvioTheme.configuredShape(RoundedCornerShape(10.dp))),
            colors = ClickableSurfaceDefaults.colors(
                containerColor = Color.White.copy(alpha = 0.08f),
                focusedContainerColor = Color.White.copy(alpha = 0.22f)
            ),
            scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
            border = ClickableSurfaceDefaults.border(
                border = Border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))),
                focusedBorder = Border(BorderStroke(2.dp, Color.White))
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_sources),
                    contentDescription = stringResource(R.string.profile_tv_inputs_label),
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // The server shortcut opens the source picker; its last item opens the server manager.
        Box {
            Surface(
                onClick = { serverMenuExpanded = true },
                modifier = Modifier
                    .focusProperties { down = contentFocusRequester }
                    .size(36.dp),
                shape = ClickableSurfaceDefaults.shape(NuvioTheme.configuredShape(RoundedCornerShape(10.dp))),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = Color.White.copy(alpha = 0.08f),
                    focusedContainerColor = Color.White.copy(alpha = 0.22f)
                ),
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
                border = ClickableSurfaceDefaults.border(
                    border = Border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))),
                    focusedBorder = Border(BorderStroke(2.dp, Color.White))
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_view_list),
                        contentDescription = stringResource(R.string.top_bar_servers),
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            val menuShape = RoundedCornerShape(18.dp)
            val activeServerId = selectedServerId ?: ServerBrowserViewModel.LOCAL_SOURCE
            if (serverMenuExpanded) {
                Popup(
                    alignment = Alignment.TopEnd,
                    offset = serverMenuOffset,
                    onDismissRequest = { serverMenuExpanded = false },
                    properties = PopupProperties(
                        focusable = true,
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    ),
                ) {
                    Box(
                        modifier = Modifier
                            .width(292.dp)
                            .heightIn(max = 456.dp)
                            .clip(menuShape)
                            .background(Color(0xFF4C390E), menuShape)
                            .border(
                                BorderStroke(1.dp, NuvioTheme.colors.Secondary.copy(alpha = 0.8f)),
                                menuShape,
                            ),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 456.dp)
                                .verticalScroll(rememberScrollState())
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                    HeaderServerMenuItem(
                        title = "Local",
                        subtitle = capacityState.local,
                        icon = Icons.Default.Folder,
                        selected = activeServerId == ServerBrowserViewModel.LOCAL_SOURCE,
                        focusRequester = serverMenuFocusRequester.takeIf {
                            activeServerId == ServerBrowserViewModel.LOCAL_SOURCE
                        },
                    ) {
                        serverMenuExpanded = false
                        onServerSourceSelected(ServerBrowserViewModel.LOCAL_SOURCE)
                    }
                    HeaderServerMenuItem(
                        title = "NAS",
                        subtitle = capacityState.freebox
                            ?: if (freeboxState.address.isBlank()) "Configurer la connexion Freebox" else "Calcul de l’espace…",
                        icon = Icons.Default.Dns,
                        selected = activeServerId == ServerBrowserViewModel.FREEBOX_SOURCE,
                        focusRequester = serverMenuFocusRequester.takeIf {
                            activeServerId == ServerBrowserViewModel.FREEBOX_SOURCE
                        },
                    ) {
                        serverMenuExpanded = false
                        if (freeboxState.address.isBlank()) onServersClick()
                        else onServerSourceSelected(ServerBrowserViewModel.FREEBOX_SOURCE)
                    }
                    serverState.enabledConnections.forEach { connection ->
                        val providerName = serverHubViewModel.providerName(connection)
                        HeaderServerMenuItem(
                            title = connection.name,
                            subtitle = capacityState.servers[connection.id]
                                ?: "${providerName} · ${connection.address}",
                            icon = Icons.Default.Storage,
                            selected = activeServerId == connection.id,
                            focusRequester = serverMenuFocusRequester.takeIf {
                                activeServerId == connection.id
                            },
                        ) {
                            serverMenuExpanded = false
                            if (serverHubViewModel.hasProvider(connection)) {
                                onServerSourceSelected(connection.id)
                            } else {
                                onServersClick()
                            }
                        }
                    }

                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.16f)),
                    )
                    HeaderServerMenuItem(
                        title = "Ajouter un serveur",
                        subtitle = "Ajouter un partage réseau SMB",
                        icon = Icons.Default.Add,
                        selected = false,
                    ) {
                        serverMenuExpanded = false
                        onServersClick()
                    }
                }
            }
        }
        }
        }

        // Settings Button
        Surface(
            onClick = onSettingsClick,
            modifier = Modifier
                .focusProperties { down = contentFocusRequester }
                .size(36.dp),
            shape = ClickableSurfaceDefaults.shape(NuvioTheme.configuredShape(RoundedCornerShape(10.dp))),
            colors = ClickableSurfaceDefaults.colors(
                containerColor = Color.White.copy(alpha = 0.08f),
                focusedContainerColor = Color.White.copy(alpha = 0.22f)
            ),
            scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
            border = ClickableSurfaceDefaults.border(
                border = Border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))),
                focusedBorder = Border(BorderStroke(2.dp, Color.White))
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.top_bar_settings),
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = 2.dp)
        ) {
            Text(
                text = timeText,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 28.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White,
                maxLines = 1
            )
            Text(
                text = dateText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 14.sp
                ),
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun HeaderServerMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    focusRequester: FocusRequester? = null,
    onClick: () -> Unit,
) {
    val shape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp))
    var isFocused by remember { mutableStateOf(false) }
    val focusModifier = focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .then(focusModifier),
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (selected) NuvioTheme.colors.Secondary else Color.Transparent,
            focusedContainerColor = if (selected) {
                NuvioTheme.colors.Secondary
            } else {
                NuvioTheme.colors.Secondary.copy(alpha = 0.28f)
            },
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f, pressedScale = 1f),
        border = ClickableSurfaceDefaults.border(
            border = Border(
                border = BorderStroke(
                    1.dp,
                    if (selected) Color.White.copy(alpha = 0.48f) else Color.Transparent,
                ),
                shape = shape,
            ),
            focusedBorder = Border(
                border = NuvioTheme.focusRing.border(NuvioTheme.spacing.xxs),
                shape = shape,
            ),
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color(0xFF2F2409) else NuvioTheme.colors.Secondary,
                modifier = Modifier.size(22.dp),
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                FocusMarqueeText(
                    text = title,
                    focused = isFocused,
                    modifier = Modifier.fillMaxWidth().height(20.dp),
                    color = if (selected) NuvioTheme.colors.OnSecondary else Color.White,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                )
                FocusMarqueeText(
                    text = subtitle,
                    focused = isFocused,
                    modifier = Modifier.fillMaxWidth().height(16.dp),
                    color = if (selected) NuvioTheme.colors.OnSecondary.copy(alpha = 0.84f)
                    else Color.White.copy(alpha = 0.76f),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            if (selected) {
                Text(
                    text = "✓",
                    color = Color(0xFF225D2E),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private fun isNetworkConnected(context: Context): Boolean {
    return runCatching {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val active = cm?.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(active) ?: return false
        caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }.getOrDefault(true)
}

private fun sourceInputActions(): List<String> =
    if (isFireOsDevice()) {
        listOf("android.settings.TV_INPUT_SETTINGS", "com.android.tv.action.VIEW_INPUTS")
    } else {
        listOf("com.android.tv.action.VIEW_INPUTS", "android.settings.TV_INPUT_SETTINGS")
    }

private fun isFireOsDevice(): Boolean =
    Build.MANUFACTURER.contains("amazon", ignoreCase = true) ||
        Build.DEVICE.contains("AFT", ignoreCase = true)

private fun openSystemSources(context: Context) {
    for (action in sourceInputActions() + Settings.ACTION_SETTINGS) {
        try {
            val intent = Intent(action)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (intent.resolveActivity(context.packageManager) == null) continue
            context.startActivity(intent)
            return
        } catch (_: Exception) {
            // Try the next system input action supported by this TV.
        }
    }
    Toast.makeText(context, R.string.profile_tv_inputs_unavailable, Toast.LENGTH_SHORT).show()
}
