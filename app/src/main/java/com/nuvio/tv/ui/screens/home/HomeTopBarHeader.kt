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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.LocalContentFocusRequester
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
    onAppsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val contentFocusRequester = LocalContentFocusRequester.current
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
            .padding(end = 48.dp, top = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (isConnected) {
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }

        // Keep Applications as the first actionable shortcut after network status.
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

        // Server navigation button, using Jojo Home's three-row server icon.
        Surface(
            onClick = onServersClick,
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
