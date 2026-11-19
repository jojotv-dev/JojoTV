package com.nuvio.tv.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nuvio.tv.R
import com.nuvio.tv.calendar.CalendarEntryPoint
import com.nuvio.tv.calendar.CalendarNotice
import com.nuvio.tv.calendar.CalendarSettings
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

@Composable
fun CalendarSettingsContent(
    initialFocusRequester: FocusRequester? = null
) {
    val context = LocalContext.current
    val entryPoint = remember {
        runCatching {
            EntryPointAccessors.fromApplication(context.applicationContext, CalendarEntryPoint::class.java)
        }.getOrNull()
    } ?: return
    val prefs = remember { entryPoint.calendarPreferences() }
    val repo = remember { entryPoint.calendarRepository() }
    val settings by prefs.settings.collectAsState(initial = CalendarSettings())
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val countries = listOf(
        "US" to stringResource(R.string.calendar_country_us),
        "GB" to stringResource(R.string.calendar_country_gb),
        "CA" to stringResource(R.string.calendar_country_ca),
        "AU" to stringResource(R.string.calendar_country_au),
        "DE" to stringResource(R.string.calendar_country_de),
        "FR" to stringResource(R.string.calendar_country_fr),
        "JP" to stringResource(R.string.calendar_country_jp),
        "BR" to stringResource(R.string.calendar_country_br),
        "IN" to stringResource(R.string.calendar_country_in)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SettingsDetailHeader(
            title = stringResource(R.string.calendar_title),
            subtitle = stringResource(R.string.calendar_settings_description)
        )

        SettingsGroupCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item(key = "cal_upcoming_row") {
                        SettingsToggleRow(
                            title = stringResource(R.string.calendar_settings_upcoming_row_title),
                            subtitle = stringResource(R.string.calendar_settings_upcoming_row_summary),
                            checked = settings.calendarUpcomingRow,
                            onToggle = {
                                scope.launch { prefs.updateSettings { it.copy(calendarUpcomingRow = !it.calendarUpcomingRow) } }
                            },
                            modifier = if (initialFocusRequester != null) {
                                Modifier.focusRequester(initialFocusRequester)
                            } else {
                                Modifier
                            }
                        )
                    }
                    item(key = "cal_new_ep_cards") {
                        SettingsToggleRow(
                            title = stringResource(R.string.calendar_settings_new_cards_title),
                            subtitle = stringResource(R.string.calendar_settings_new_cards_summary),
                            checked = settings.calendarNewEpisodeCards,
                            onToggle = {
                                scope.launch { prefs.updateSettings { it.copy(calendarNewEpisodeCards = !it.calendarNewEpisodeCards) } }
                            }
                        )
                    }
                    item(key = "cal_hide_spoilers") {
                        SettingsToggleRow(
                            title = stringResource(R.string.calendar_settings_hide_spoilers_title),
                            subtitle = stringResource(R.string.calendar_settings_hide_spoilers_summary),
                            checked = settings.calendarHideSpoilers,
                            onToggle = {
                                scope.launch { prefs.updateSettings { it.copy(calendarHideSpoilers = !it.calendarHideSpoilers) } }
                            }
                        )
                    }
                    item(key = "cal_hide_watched") {
                        SettingsToggleRow(
                            title = stringResource(R.string.calendar_settings_recent_unwatched_title),
                            subtitle = stringResource(R.string.calendar_settings_recent_unwatched_summary),
                            checked = settings.calendarHideWatched,
                            onToggle = {
                                scope.launch { prefs.updateSettings { it.copy(calendarHideWatched = !it.calendarHideWatched) } }
                            }
                        )
                    }
                    item(key = "cal_skip_news") {
                        SettingsToggleRow(
                            title = stringResource(R.string.calendar_settings_skip_news_title),
                            subtitle = stringResource(R.string.calendar_settings_skip_news_summary),
                            checked = settings.calendarSkipNewsTalk,
                            onToggle = {
                                scope.launch { prefs.updateSettings { it.copy(calendarSkipNewsTalk = !it.calendarSkipNewsTalk) } }
                            }
                        )
                    }
                    item(key = "cal_24h") {
                        SettingsToggleRow(
                            title = stringResource(R.string.calendar_settings_24_hour_title),
                            subtitle = stringResource(R.string.calendar_settings_24_hour_summary),
                            checked = settings.use24HourClock,
                            onToggle = {
                                scope.launch { prefs.updateSettings { it.copy(use24HourClock = !it.use24HourClock) } }
                            }
                        )
                    }
                    item(key = "cal_view") {
                        val views = listOf(
                            "LIST" to R.string.calendar_view_list,
                            "WEEK" to R.string.calendar_view_week,
                            "MONTH" to R.string.calendar_view_month,
                        )
                        val currentLabel = stringResource(
                            views.firstOrNull { it.first == settings.calendarView }?.second ?: R.string.calendar_view_list
                        )
                        SettingsActionRow(
                            title = stringResource(R.string.calendar_settings_default_view_title),
                            subtitle = stringResource(R.string.calendar_settings_default_view_summary),
                            value = currentLabel,
                            onClick = {
                                val idx = views.indexOfFirst { it.first == settings.calendarView }
                                val next = views[(idx + 1) % views.size].first
                                scope.launch { prefs.updateSettings { it.copy(calendarView = next) } }
                            }
                        )
                    }
                    item(key = "cal_country") {
                        val currentCountry = countries.firstOrNull { it.first == settings.calendarCountry }?.second ?: settings.calendarCountry
                        SettingsActionRow(
                            title = stringResource(R.string.calendar_settings_release_country_title),
                            subtitle = stringResource(R.string.calendar_settings_release_country_summary),
                            value = currentCountry,
                            onClick = {
                                val idx = countries.indexOfFirst { it.first == settings.calendarCountry }
                                val next = countries[(idx + 1) % countries.size].first
                                scope.launch { prefs.updateSettings { it.copy(calendarCountry = next) } }
                            }
                        )
                    }
                    item(key = "cal_sidebar") {
                        SettingsToggleRow(
                            title = stringResource(R.string.calendar_settings_sidebar_title),
                            subtitle = stringResource(R.string.calendar_settings_sidebar_summary),
                            checked = settings.calendarInSidebar,
                            onToggle = {
                                scope.launch { prefs.updateSettings { it.copy(calendarInSidebar = !it.calendarInSidebar) } }
                            }
                        )
                    }
                    item(key = "cal_header") {
                        SettingsToggleRow(
                            title = stringResource(R.string.calendar_settings_header_title),
                            subtitle = stringResource(R.string.calendar_settings_header_summary),
                            checked = settings.calendarInHeader,
                            onToggle = {
                                scope.launch { prefs.updateSettings { it.copy(calendarInHeader = !it.calendarInHeader) } }
                            }
                        )
                    }
                    item(key = "cal_refresh") {
                        SettingsActionRow(
                            title = stringResource(R.string.calendar_settings_refresh_title),
                            subtitle = stringResource(R.string.calendar_settings_refresh_summary),
                            onClick = {
                                repo.refresh(force = true)
                                CalendarNotice.show(context.getString(R.string.calendar_settings_refresh_notice))
                            }
                        )
                    }
                }
                SettingsVerticalScrollIndicators(state = listState)
            }
        }
    }
}
