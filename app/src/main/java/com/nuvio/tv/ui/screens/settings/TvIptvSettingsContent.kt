@file:OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class)

package com.nuvio.tv.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.ui.reshaped.livetv.LiveTvSettingsContent as ReshapedLiveTvSettingsContent
import com.nuvio.tv.ui.theme.NuvioTheme

private enum class TvIptvSettingsTab(val title: String) {
    IPTV("IPTV"),
    LIVE_TV("TV en direct")
}

/** One settings destination for the IPTV provider manager and the Live TV settings. */
@Composable
internal fun TvIptvSettingsContent(initialFocusRequester: FocusRequester? = null) {
    var selectedTab by remember { mutableStateOf(TvIptvSettingsTab.IPTV) }

    Column(Modifier.fillMaxSize()) {
        TvIptvSettingsTabRow(
            selected = selectedTab,
            firstFocusRequester = initialFocusRequester,
            onSelect = { selectedTab = it }
        )
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (selectedTab) {
                TvIptvSettingsTab.IPTV -> ReshapedLiveTvSettingsContent()
                TvIptvSettingsTab.LIVE_TV -> LiveTvSettingsContent(page = LiveTvPage.ALL)
            }
        }
    }
}

@Composable
private fun TvIptvSettingsTabRow(
    selected: TvIptvSettingsTab,
    firstFocusRequester: FocusRequester?,
    onSelect: (TvIptvSettingsTab) -> Unit
) {
    var rowHasFocus by remember { mutableStateOf(false) }

    Row(
        Modifier.fillMaxWidth()
            .padding(start = 28.dp, top = 8.dp, end = 28.dp)
            .onFocusChanged { rowHasFocus = it.hasFocus }
            .focusGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TvIptvSettingsTab.entries.forEach { tab ->
            var focused by remember { mutableStateOf(false) }
            val isSelected = tab == selected
            val shape = NuvioTheme.configuredShape(RoundedCornerShape(50))

            Text(
                text = tab.title,
                color = if (focused) NuvioTheme.colors.OnSecondary
                else if (isSelected) NuvioTheme.colors.TextPrimary
                else NuvioTheme.colors.TextSecondary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .then(
                        if (isSelected && firstFocusRequester != null) {
                            Modifier.focusRequester(firstFocusRequester)
                        } else Modifier
                    )
                    .focusProperties { canFocus = isSelected || rowHasFocus }
                    .clip(shape)
                    .background(
                        when {
                            focused -> NuvioTheme.colors.Secondary
                            isSelected -> androidx.compose.ui.graphics.Color.White.copy(alpha = 0.14f)
                            else -> androidx.compose.ui.graphics.Color.White.copy(alpha = 0.06f)
                        }
                    )
                    .onFocusChanged {
                        focused = it.isFocused
                        if (it.isFocused) onSelect(tab)
                    }
                    .clickable { onSelect(tab) }
                    .padding(horizontal = 18.dp, vertical = 9.dp)
            )
        }
    }
}
