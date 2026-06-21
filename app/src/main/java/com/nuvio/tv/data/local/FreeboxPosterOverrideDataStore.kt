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
private val freeboxPosterOverrideKey = stringPreferencesKey(FREEBOX_POSTER_OVERRIDE_KEY)

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

    suspend fun set(contentId: String, posterUrl: String) {
        val profileId = profileManager.activeProfileId.value
        factory.get(profileId, FREEBOX_POSTER_OVERRIDE_FEATURE).edit { preferences ->
            val updated = decodeFreeboxPosterOverrides(preferences[freeboxPosterOverrideKey]).toMutableMap()
            updated[contentId] = posterUrl
            preferences[freeboxPosterOverrideKey] = encodeFreeboxPosterOverrides(updated)
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
        }
    }

    private fun Preferences.toPosterOverrides(): Map<String, String> =
        decodeFreeboxPosterOverrides(this[freeboxPosterOverrideKey])
}