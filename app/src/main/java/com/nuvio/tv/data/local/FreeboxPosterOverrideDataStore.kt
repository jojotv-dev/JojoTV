package com.nuvio.tv.data.local

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nuvio.tv.core.profile.ProfileManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private const val FREEBOX_POSTER_OVERRIDE_FEATURE = "freebox_poster_overrides"
private const val FREEBOX_POSTER_OVERRIDE_KEY = "poster_overrides_json"
private const val FREEBOX_BACKDROP_OVERRIDE_KEY = "backdrop_overrides_json"
private val freeboxPosterOverrideKey = stringPreferencesKey(FREEBOX_POSTER_OVERRIDE_KEY)
private val freeboxBackdropOverrideKey = stringPreferencesKey(FREEBOX_BACKDROP_OVERRIDE_KEY)
private const val FREEBOX_OVERVIEW_OVERRIDE_KEY = "overview_overrides_json"
private val freeboxOverviewOverrideKey = stringPreferencesKey(FREEBOX_OVERVIEW_OVERRIDE_KEY)

internal fun decodeFreeboxPosterOverrides(json: String?): Map<String, String> {
    if (json.isNullOrBlank()) return emptyMap()
    return runCatching {
        val source = JSONObject(json)
        buildMap {
            source.keys().forEach { contentId ->
                source.optString(contentId).takeIf { it.isNotBlank() }?.let { put(contentId, it) }
            }
        }
    }.getOrDefault(emptyMap())
}

private fun encodeFreeboxPosterOverrides(overrides: Map<String, String>): String =
    JSONObject().apply {
        overrides.forEach { (contentId, posterUrl) -> put(contentId, posterUrl) }
    }.toString()

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class FreeboxPosterOverrideDataStore @Inject constructor(
    private val factory: ProfileDataStoreFactory,
    private val profileManager: ProfileManager
) {
    val overrides: Flow<Map<String, String>> = profileManager.activeProfileId
        .flatMapLatest { profileId ->
            factory.get(profileId, FREEBOX_POSTER_OVERRIDE_FEATURE).data
                .map { preferences -> preferences.toPosterOverrides() }
                .catch { emit(emptyMap()) }
        }

    val backdropOverrides: Flow<Map<String, String>> = profileManager.activeProfileId
        .flatMapLatest { profileId ->
            factory.get(profileId, FREEBOX_POSTER_OVERRIDE_FEATURE).data
                .map { preferences -> decodeFreeboxPosterOverrides(preferences[freeboxBackdropOverrideKey]) }
                .catch { emit(emptyMap()) }
        }

    val overviewOverrides: Flow<Map<String, String>> = profileManager.activeProfileId
        .flatMapLatest { profileId ->
            factory.get(profileId, FREEBOX_POSTER_OVERRIDE_FEATURE).data
                .map { preferences -> decodeFreeboxPosterOverrides(preferences[freeboxOverviewOverrideKey]) }
                .catch { emit(emptyMap()) }
        }

    suspend fun set(contentId: String, posterUrl: String) {
        val profileId = profileManager.activeProfileId.value
        factory.get(profileId, FREEBOX_POSTER_OVERRIDE_FEATURE).edit { preferences ->
            val updated = decodeFreeboxPosterOverrides(preferences[freeboxPosterOverrideKey]).toMutableMap()
            updated[contentId] = posterUrl
            preferences[freeboxPosterOverrideKey] = encodeFreeboxPosterOverrides(updated)
        }
    }

    suspend fun setBackdrop(contentId: String, backdropUrl: String) {
        val profileId = profileManager.activeProfileId.value
        factory.get(profileId, FREEBOX_POSTER_OVERRIDE_FEATURE).edit { preferences ->
            val updated = decodeFreeboxPosterOverrides(preferences[freeboxBackdropOverrideKey]).toMutableMap()
            updated[contentId] = backdropUrl
            preferences[freeboxBackdropOverrideKey] = encodeFreeboxPosterOverrides(updated)
        }
    }

    suspend fun setOverview(contentId: String, overview: String) {
        val profileId = profileManager.activeProfileId.value
        factory.get(profileId, FREEBOX_POSTER_OVERRIDE_FEATURE).edit { preferences ->
            val updated = decodeFreeboxPosterOverrides(preferences[freeboxOverviewOverrideKey]).toMutableMap()
            updated[contentId] = overview
            preferences[freeboxOverviewOverrideKey] = encodeFreeboxPosterOverrides(updated)
        }
    }

    suspend fun move(fromContentId: String, toContentId: String) {
        if (fromContentId == toContentId) return
        val profileId = profileManager.activeProfileId.value
        factory.get(profileId, FREEBOX_POSTER_OVERRIDE_FEATURE).edit { preferences ->
            val updated = decodeFreeboxPosterOverrides(preferences[freeboxPosterOverrideKey]).toMutableMap()
            val posterUrl = updated.remove(fromContentId) ?: return@edit
            updated[toContentId] = posterUrl
            preferences[freeboxPosterOverrideKey] = encodeFreeboxPosterOverrides(updated)

            val backdrops = decodeFreeboxPosterOverrides(preferences[freeboxBackdropOverrideKey]).toMutableMap()
            backdrops.remove(fromContentId)?.let { backdrops[toContentId] = it }
            preferences[freeboxBackdropOverrideKey] = encodeFreeboxPosterOverrides(backdrops)

            val overviews = decodeFreeboxPosterOverrides(preferences[freeboxOverviewOverrideKey]).toMutableMap()
            overviews.remove(fromContentId)?.let { overviews[toContentId] = it }
            preferences[freeboxOverviewOverrideKey] = encodeFreeboxPosterOverrides(overviews)
        }
    }

    private fun Preferences.toPosterOverrides(): Map<String, String> =
        decodeFreeboxPosterOverrides(this[freeboxPosterOverrideKey])
}
