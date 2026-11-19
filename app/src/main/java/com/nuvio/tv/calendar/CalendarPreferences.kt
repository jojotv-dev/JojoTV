package com.nuvio.tv.calendar

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nuvio.tv.core.profile.ProfileManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.calendarDataStore: DataStore<Preferences> by preferencesDataStore(name = "calendar_preferences")

data class CalendarSettings(
    val calendarInSidebar: Boolean = true,
    val calendarInHeader: Boolean = true,
    val calendarOff: Boolean = false,
    val calendarUpcomingRow: Boolean = true,
    val calendarNewEpisodeCards: Boolean = true,
    val calendarHideSpoilers: Boolean = true,
    val calendarCountry: String = "US",
    val calendarSkipNewsTalk: Boolean = true,
    val calendarView: String = "MONTH",
    val calendarHideWatched: Boolean = false,
    val use24HourClock: Boolean = false
)

data class CalendarProfileSyncSnapshot(
    val settings: CalendarSettings,
    val follows: String,
    val hiddenShows: String
)

@Singleton
class CalendarPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileManager: ProfileManager
) {
    private val store = context.calendarDataStore

    private object Keys {
        val calendarInSidebar = booleanPreferencesKey("calendar_in_sidebar")
        val calendarInHeader = booleanPreferencesKey("calendar_in_header")
        val legacyCalendarInMenu = booleanPreferencesKey("calendar_in_menu")
        val calendarOff = booleanPreferencesKey("calendar_off")
        val calendarUpcomingRow = booleanPreferencesKey("calendar_upcoming_row")
        val calendarNewEpisodeCards = booleanPreferencesKey("calendar_new_episode_cards")
        val calendarHideSpoilers = booleanPreferencesKey("calendar_hide_spoilers")
        val calendarCountry = stringPreferencesKey("calendar_country")
        val calendarSkipNewsTalk = booleanPreferencesKey("calendar_skip_news_talk")
        val calendarView = stringPreferencesKey("calendar_view")
        val calendarHideWatched = booleanPreferencesKey("calendar_hide_watched")
        val use24HourClock = booleanPreferencesKey("use_24h_clock")
        val calendarFollows = stringPreferencesKey("calendar_follows")
        val calendarHiddenShows = stringPreferencesKey("calendar_hidden_shows")
    }

    private fun profileStringKey(base: Preferences.Key<String>, profileId: Int): Preferences.Key<String> =
        if (profileId <= 1) base else stringPreferencesKey("${base.name}_p$profileId")

    private fun profileKey(base: Preferences.Key<Boolean>, profileId: Int): Preferences.Key<Boolean> =
        if (profileId <= 1) base else booleanPreferencesKey("${base.name}_p$profileId")

    private fun settingsFrom(p: Preferences, profileId: Int): CalendarSettings {
        val d = CalendarSettings()
        val legacyCalendarInMenu = p[profileKey(Keys.legacyCalendarInMenu, profileId)]
            ?: p[Keys.legacyCalendarInMenu]
        return CalendarSettings(
            calendarInSidebar = p[profileKey(Keys.calendarInSidebar, profileId)]
                ?: legacyCalendarInMenu
                ?: d.calendarInSidebar,
            calendarInHeader = p[profileKey(Keys.calendarInHeader, profileId)]
                ?: legacyCalendarInMenu
                ?: d.calendarInHeader,
            calendarOff = p[profileKey(Keys.calendarOff, profileId)] ?: p[Keys.calendarOff] ?: d.calendarOff,
            calendarUpcomingRow = p[profileKey(Keys.calendarUpcomingRow, profileId)] ?: p[Keys.calendarUpcomingRow] ?: d.calendarUpcomingRow,
            calendarNewEpisodeCards = p[profileKey(Keys.calendarNewEpisodeCards, profileId)] ?: p[Keys.calendarNewEpisodeCards] ?: d.calendarNewEpisodeCards,
            calendarHideSpoilers = p[profileKey(Keys.calendarHideSpoilers, profileId)] ?: p[Keys.calendarHideSpoilers] ?: d.calendarHideSpoilers,
            calendarCountry = p[profileStringKey(Keys.calendarCountry, profileId)] ?: p[Keys.calendarCountry] ?: d.calendarCountry,
            calendarSkipNewsTalk = p[profileKey(Keys.calendarSkipNewsTalk, profileId)] ?: p[Keys.calendarSkipNewsTalk] ?: d.calendarSkipNewsTalk,
            calendarView = p[profileStringKey(Keys.calendarView, profileId)] ?: p[Keys.calendarView] ?: d.calendarView,
            calendarHideWatched = p[profileKey(Keys.calendarHideWatched, profileId)] ?: p[Keys.calendarHideWatched] ?: d.calendarHideWatched,
            use24HourClock = p[profileKey(Keys.use24HourClock, profileId)] ?: p[Keys.use24HourClock] ?: d.use24HourClock
        )
    }

    val settings: Flow<CalendarSettings> = combine(store.data, profileManager.activeProfileId) { p, profileId ->
        settingsFrom(p, profileId)
    }
        .distinctUntilChanged()

    /** Snapshot used by the account settings sync; follows and hidden shows stay profile scoped. */
    fun profileSyncSnapshot(profileId: Int): Flow<CalendarProfileSyncSnapshot> = store.data
        .map { p ->
            CalendarProfileSyncSnapshot(
                settings = settingsFrom(p, profileId),
                follows = p[profileStringKey(Keys.calendarFollows, profileId)] ?: p[Keys.calendarFollows] ?: "",
                hiddenShows = p[profileStringKey(Keys.calendarHiddenShows, profileId)] ?: p[Keys.calendarHiddenShows] ?: ""
            )
        }
        .distinctUntilChanged()

    suspend fun applyProfileSyncSnapshot(profileId: Int, snapshot: CalendarProfileSyncSnapshot) {
        store.edit { p ->
            val settings = snapshot.settings
            p[profileKey(Keys.calendarInSidebar, profileId)] = settings.calendarInSidebar
            p[profileKey(Keys.calendarInHeader, profileId)] = settings.calendarInHeader
            p[profileKey(Keys.legacyCalendarInMenu, profileId)] = settings.calendarInSidebar
            p[profileKey(Keys.calendarOff, profileId)] = settings.calendarOff
            p[profileKey(Keys.calendarUpcomingRow, profileId)] = settings.calendarUpcomingRow
            p[profileKey(Keys.calendarNewEpisodeCards, profileId)] = settings.calendarNewEpisodeCards
            p[profileKey(Keys.calendarHideSpoilers, profileId)] = settings.calendarHideSpoilers
            p[profileStringKey(Keys.calendarCountry, profileId)] = settings.calendarCountry
            p[profileKey(Keys.calendarSkipNewsTalk, profileId)] = settings.calendarSkipNewsTalk
            p[profileStringKey(Keys.calendarView, profileId)] = settings.calendarView
            p[profileKey(Keys.calendarHideWatched, profileId)] = settings.calendarHideWatched
            p[profileKey(Keys.use24HourClock, profileId)] = settings.use24HourClock
            p[profileStringKey(Keys.calendarFollows, profileId)] = snapshot.follows
            p[profileStringKey(Keys.calendarHiddenShows, profileId)] = snapshot.hiddenShows
        }
    }

    suspend fun updateSettings(transform: (CalendarSettings) -> CalendarSettings) {
        val profileId = profileManager.activeProfileId.value
        store.edit { p ->
            val current = settingsFrom(p, profileId)
            val next = transform(current)
            p[profileKey(Keys.calendarInSidebar, profileId)] = next.calendarInSidebar
            p[profileKey(Keys.calendarInHeader, profileId)] = next.calendarInHeader
            p[profileKey(Keys.legacyCalendarInMenu, profileId)] = next.calendarInSidebar
            p[profileKey(Keys.calendarOff, profileId)] = next.calendarOff
            p[profileKey(Keys.calendarUpcomingRow, profileId)] = next.calendarUpcomingRow
            p[profileKey(Keys.calendarNewEpisodeCards, profileId)] = next.calendarNewEpisodeCards
            p[profileKey(Keys.calendarHideSpoilers, profileId)] = next.calendarHideSpoilers
            p[profileStringKey(Keys.calendarCountry, profileId)] = next.calendarCountry
            p[profileKey(Keys.calendarSkipNewsTalk, profileId)] = next.calendarSkipNewsTalk
            p[profileStringKey(Keys.calendarView, profileId)] = next.calendarView
            p[profileKey(Keys.calendarHideWatched, profileId)] = next.calendarHideWatched
            p[profileKey(Keys.use24HourClock, profileId)] = next.use24HourClock
        }
    }

    val calendarFollows: Flow<String> = combine(store.data, profileManager.activeProfileId) { p, id ->
        p[profileStringKey(Keys.calendarFollows, id)] ?: p[Keys.calendarFollows] ?: ""
    }.distinctUntilChanged()

    suspend fun editCalendarFollows(transform: (String) -> String) {
        val id = profileManager.activeProfileId.value
        val key = profileStringKey(Keys.calendarFollows, id)
        store.edit { p -> p[key] = transform(p[key] ?: p[Keys.calendarFollows] ?: "") }
    }

    val calendarHiddenShows: Flow<String> = combine(store.data, profileManager.activeProfileId) { p, id ->
        p[profileStringKey(Keys.calendarHiddenShows, id)] ?: p[Keys.calendarHiddenShows] ?: ""
    }.distinctUntilChanged()

    suspend fun editCalendarHiddenShows(transform: (String) -> String) {
        val id = profileManager.activeProfileId.value
        val key = profileStringKey(Keys.calendarHiddenShows, id)
        store.edit { p -> p[key] = transform(p[key] ?: p[Keys.calendarHiddenShows] ?: "") }
    }
}
