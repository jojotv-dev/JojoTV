package com.nuvio.tv.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.edit
import com.nuvio.tv.core.profile.ProfileManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class IptvSidebarSettings(
    val showLiveTvInSidebar: Boolean = false,
    val showMoviesInSidebar: Boolean = false,
    val showSeriesInSidebar: Boolean = false,
    val showRecordingsInSidebar: Boolean = false
)

data class IptvVisibilitySettings(
    val hiddenGroupIds: Set<String> = emptySet(),
    val hiddenChannelIds: Set<String> = emptySet()
)

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class IptvSettingsDataStore @Inject constructor(
    private val factory: ProfileDataStoreFactory,
    private val profileManager: ProfileManager
) {
    companion object {
        private const val FEATURE = "iptv_settings"
    }

    private fun store(profileId: Int = profileManager.activeProfileId.value) =
        factory.get(profileId, FEATURE)

    private val showLiveTvKey = booleanPreferencesKey("iptv_show_livetv_in_sidebar")
    private val showMoviesKey = booleanPreferencesKey("iptv_show_movies_in_sidebar")
    private val showSeriesKey = booleanPreferencesKey("iptv_show_series_in_sidebar")
    private val showRecordingsKey = booleanPreferencesKey("iptv_show_recordings_in_sidebar")

    private fun hiddenGroupsKey(providerId: Long) = stringSetPreferencesKey("iptv_hidden_groups_$providerId")
    private fun hiddenChannelsKey(providerId: Long) = stringSetPreferencesKey("iptv_hidden_channels_$providerId")
    private fun groupRenamesKey(providerId: Long) = stringSetPreferencesKey("iptv_group_renames_$providerId")

    val settings: Flow<IptvSidebarSettings> = profileManager.activeProfileId.flatMapLatest { pid ->
        factory.get(pid, FEATURE).data.map { prefs ->
            IptvSidebarSettings(
                showLiveTvInSidebar = prefs[showLiveTvKey] ?: false,
                showMoviesInSidebar = prefs[showMoviesKey] ?: false,
                showSeriesInSidebar = prefs[showSeriesKey] ?: false,
                showRecordingsInSidebar = prefs[showRecordingsKey] ?: false
            )
        }
    }

    fun visibilitySettings(providerId: Long): Flow<IptvVisibilitySettings> =
        profileManager.activeProfileId.flatMapLatest { pid ->
            factory.get(pid, FEATURE).data.map { prefs ->
                IptvVisibilitySettings(
                    hiddenGroupIds = prefs[hiddenGroupsKey(providerId)] ?: emptySet(),
                    hiddenChannelIds = prefs[hiddenChannelsKey(providerId)] ?: emptySet()
                )
            }
        }

    fun groupRenames(providerId: Long): Flow<Map<String, String>> =
        profileManager.activeProfileId.flatMapLatest { pid ->
            factory.get(pid, FEATURE).data.map { prefs ->
                (prefs[groupRenamesKey(providerId)] ?: emptySet())
                    .mapNotNull { entry ->
                        val idx = entry.indexOf('=')
                        if (idx > 0) entry.substring(0, idx) to entry.substring(idx + 1) else null
                    }.toMap()
            }
        }

    suspend fun setGroupVisible(providerId: Long, groupId: String, visible: Boolean) {
        if (groupId.isBlank()) return
        store().edit { prefs ->
            val key = hiddenGroupsKey(providerId)
            val updated = (prefs[key] ?: emptySet()).toMutableSet()
            if (visible) updated.remove(groupId) else updated.add(groupId)
            prefs[key] = updated
        }
    }

    suspend fun setChannelVisible(providerId: Long, channelId: Long, visible: Boolean) {
        val keyValue = channelId.toString()
        store().edit { prefs ->
            val key = hiddenChannelsKey(providerId)
            val updated = (prefs[key] ?: emptySet()).toMutableSet()
            if (visible) updated.remove(keyValue) else updated.add(keyValue)
            prefs[key] = updated
        }
    }

    suspend fun renameGroup(providerId: Long, groupId: String, newName: String) {
        if (groupId.isBlank()) return
        store().edit { prefs ->
            val key = groupRenamesKey(providerId)
            val updated = (prefs[key] ?: emptySet()).toMutableSet()
            updated.removeAll { it.startsWith("$groupId=") }
            if (newName.isNotBlank()) updated.add("$groupId=$newName")
            prefs[key] = updated
        }
    }

    suspend fun setShowLiveTv(enabled: Boolean) {
        store().edit { it[showLiveTvKey] = enabled }
    }

    suspend fun setShowMovies(enabled: Boolean) {
        store().edit { it[showMoviesKey] = enabled }
    }

    suspend fun setShowSeries(enabled: Boolean) {
        store().edit { it[showSeriesKey] = enabled }
    }

    suspend fun setShowRecordings(enabled: Boolean) {
        store().edit { it[showRecordingsKey] = enabled }
    }
}