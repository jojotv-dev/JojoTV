package com.nuvio.tv.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nuvio.tv.domain.model.IptvPosterSize
import androidx.datastore.preferences.core.edit
import com.nuvio.tv.core.profile.ProfileManager
import com.streamvault.domain.model.ContentType
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

data class IptvDnsSettings(
    val providerId: String = "system"
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
    private val vodPosterSizeKey = stringPreferencesKey("iptv_vod_poster_size")
    private val dnsProviderKey = stringPreferencesKey("iptv_dns_provider")

    private fun legacyHiddenGroupsKey(providerId: Long) = stringSetPreferencesKey("iptv_hidden_groups_$providerId")
    private fun hiddenGroupsKey(providerId: Long, contentType: ContentType) =
        stringSetPreferencesKey("iptv_hidden_groups_${providerId}_${contentType.name}")
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
        visibilitySettings(providerId, ContentType.LIVE)

    fun visibilitySettings(providerId: Long, contentType: ContentType): Flow<IptvVisibilitySettings> =
        profileManager.activeProfileId.flatMapLatest { pid ->
            factory.get(pid, FEATURE).data.map { prefs ->
                val legacyHiddenGroups = prefs[legacyHiddenGroupsKey(providerId)] ?: emptySet()
                val typedHiddenGroups = prefs[hiddenGroupsKey(providerId, contentType)] ?: emptySet()
                IptvVisibilitySettings(
                    hiddenGroupIds = legacyHiddenGroups + typedHiddenGroups,
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
        setGroupVisible(providerId, ContentType.LIVE, groupId, visible)
    }

    suspend fun setGroupVisible(providerId: Long, contentType: ContentType, groupId: String, visible: Boolean) {
        if (groupId.isBlank()) return
        store().edit { prefs ->
            val legacyKey = legacyHiddenGroupsKey(providerId)
            val legacyUpdated = (prefs[legacyKey] ?: emptySet()).toMutableSet()
            legacyUpdated.remove(groupId)
            if (legacyUpdated.isEmpty()) prefs.remove(legacyKey) else prefs[legacyKey] = legacyUpdated

            val key = hiddenGroupsKey(providerId, contentType)
            val updated = (prefs[key] ?: emptySet()).toMutableSet()
            if (visible) updated.remove(groupId) else updated.add(groupId)
            if (updated.isEmpty()) prefs.remove(key) else prefs[key] = updated
        }
    }

    suspend fun setHiddenGroupIds(
        providerId: Long,
        contentType: ContentType,
        hiddenGroupIds: Set<String>,
        replaceGroupIds: Set<String> = hiddenGroupIds
    ) {
        store().edit { prefs ->
            val key = hiddenGroupsKey(providerId, contentType)
            if (hiddenGroupIds.isEmpty()) prefs.remove(key) else prefs[key] = hiddenGroupIds

            val legacyKey = legacyHiddenGroupsKey(providerId)
            val legacyUpdated = (prefs[legacyKey] ?: emptySet())
                .filterNot { it in replaceGroupIds }
                .toSet()
            if (legacyUpdated.isEmpty()) prefs.remove(legacyKey) else prefs[legacyKey] = legacyUpdated
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

    val vodPosterSize: Flow<IptvPosterSize> = profileManager.activeProfileId.flatMapLatest { pid ->
        factory.get(pid, FEATURE).data.map { prefs ->
            IptvPosterSize.fromName(prefs[vodPosterSizeKey])
        }
    }

    suspend fun setVodPosterSize(size: IptvPosterSize) {
        store().edit { it[vodPosterSizeKey] = size.name }
    }

    val dnsSettings: Flow<IptvDnsSettings> = profileManager.activeProfileId.flatMapLatest { pid ->
        factory.get(pid, FEATURE).data.map { prefs ->
            IptvDnsSettings(
                providerId = prefs[dnsProviderKey]?.takeIf { it.isNotBlank() } ?: "system"
            )
        }
    }

    suspend fun setDnsProvider(providerId: String) {
        store().edit { prefs ->
            if (providerId.isBlank() || providerId == "system") {
                prefs.remove(dnsProviderKey)
            } else {
                prefs[dnsProviderKey] = providerId
            }
        }
    }
}
