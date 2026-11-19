package com.nuvio.tv.ui.screens.player

import androidx.lifecycle.SavedStateHandle
import com.nuvio.tv.domain.model.MetaCastMember
import org.json.JSONObject
import java.net.URLDecoder

internal data class PlayerNavigationArgs(
    val streamUrl: String,
    val title: String,
    val streamName: String?,
    val year: String?,
    val headersJson: String?,
    val contentId: String?,
    val contentType: String?,
    val contentName: String?,
    val poster: String?,
    val backdrop: String?,
    val logo: String?,
    val videoId: String?,
    val initialSeason: Int?,
    val initialEpisode: Int?,
    val initialEpisodeTitle: String?,
    val bingeGroup: String?,
    val filename: String?,
    val videoHash: String?,
    val videoSize: Long?,
    val startFromBeginning: Boolean,
    val addonName: String?,
    val addonLogo: String?,
    val streamDescription: String?,
    val contentLanguage: String?,
    val rememberedAudioLanguage: String?,
    val rememberedAudioName: String?,
    val launchStartedAtMs: Long?,
    val profileId: Int?,
    val audioDelayMs: Int? = null,
    val audioDelayLanguage: String? = null,
    val youtubeLive: Boolean = false,
    val genres: List<String> = emptyList(),
    val runtime: String? = null,
    val initialCastMembers: List<MetaCastMember> = emptyList(),
    val initialDirector: List<String> = emptyList(),
    val initialDescription: String? = null
) {
    companion object {
        fun from(savedStateHandle: SavedStateHandle): PlayerNavigationArgs {
            fun decodedOrNull(key: String): String? {
                val value = savedStateHandle.get<String>(key) ?: return null
                if (value.isEmpty()) return null
                // Stream metadata occasionally contains stray `%` or malformed escapes
                // Fall back to the raw value rather
                // than crashing the player on launch.
                return runCatching { URLDecoder.decode(value, "UTF-8") }.getOrDefault(value)
            }
            // Navigation has already decoded the JSON route value; do not URL-decode it again.
            val presentation = runCatching {
                JSONObject(savedStateHandle.get<String>("presentation").orEmpty())
            }.getOrDefault(JSONObject())
            fun presentationText(vararg keys: String): String? = keys.asSequence()
                .map { key -> presentation.optString(key, "").trim() }
                .firstOrNull { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
            fun presentationList(key: String): List<String> {
                val values = presentation.optJSONArray(key)
                if (values != null) {
                    return (0 until values.length())
                        .map { values.optString(it).trim() }
                        .filter(String::isNotBlank)
                }
                val raw = presentationText(key) ?: return emptyList()
                return raw.split(Regex("\\s*(?:•|,|;|\\|)\\s*"))
                    .map(String::trim)
                    .filter(String::isNotBlank)
            }
            fun presentationCast(): List<MetaCastMember> {
                val values = presentation.optJSONArray("castDetails") ?: return presentationList("cast")
                    .map { name -> MetaCastMember(name = name) }
                return (0 until values.length()).mapNotNull { index ->
                    val value = values.optJSONObject(index) ?: return@mapNotNull null
                    val name = value.optString("name", "").trim()
                    if (name.isBlank()) return@mapNotNull null
                    MetaCastMember(
                        name = name,
                        character = value.optString("character").takeIf { it.isNotBlank() && it != "null" },
                        photo = value.optString("photo").takeIf { it.isNotBlank() && it != "null" },
                        tmdbId = value.optInt("tmdbId").takeIf { it > 0 }
                    )
                }
            }

            return PlayerNavigationArgs(
                streamUrl = savedStateHandle.get<String>("streamUrl") ?: "",
                title = decodedOrNull("title") ?: "",
                streamName = decodedOrNull("streamName"),
                year = presentationText("year", "releaseYear") ?: decodedOrNull("year"),
                headersJson = decodedOrNull("headers"),
                // NavController already decodes these IDs.
                contentId = savedStateHandle.get<String>("contentId")?.takeIf { it.isNotEmpty() },
                contentType = savedStateHandle.get<String>("contentType")?.takeIf { it.isNotEmpty() },
                contentName = decodedOrNull("contentName"),
                poster = presentationText("poster", "artwork") ?: decodedOrNull("poster"),
                backdrop = presentationText("backdrop") ?: decodedOrNull("backdrop"),
                logo = presentationText("logo", "titleLogo") ?: decodedOrNull("logo"),
                videoId = savedStateHandle.get<String>("videoId")?.takeIf { it.isNotEmpty() },
                initialSeason = savedStateHandle.get<String>("season")?.toIntOrNull(),
                initialEpisode = savedStateHandle.get<String>("episode")?.toIntOrNull(),
                initialEpisodeTitle = decodedOrNull("episodeTitle"),
                bingeGroup = savedStateHandle.get<String>("bingeGroup")?.takeIf { it.isNotEmpty() },
                filename = decodedOrNull("filename"),
                videoHash = savedStateHandle.get<String>("videoHash")?.takeIf { it.isNotEmpty() },
                videoSize = savedStateHandle.get<String>("videoSize")?.toLongOrNull(),
                startFromBeginning = savedStateHandle.get<String>("startFromBeginning")?.toBooleanStrictOrNull() == true,
                addonName = decodedOrNull("addonName"),
                addonLogo = decodedOrNull("addonLogo"),
                streamDescription = decodedOrNull("streamDescription"),
                contentLanguage = decodedOrNull("contentLanguage"),
                rememberedAudioLanguage = decodedOrNull("rememberedAudioLanguage"),
                rememberedAudioName = decodedOrNull("rememberedAudioName"),
                launchStartedAtMs = savedStateHandle.get<String>("launchStartedAtMs")?.toLongOrNull(),
                profileId = savedStateHandle.get<String>("profileId")?.toIntOrNull(),
                audioDelayMs = savedStateHandle.get<String>("audioDelayMs")?.toIntOrNull(),
                audioDelayLanguage = decodedOrNull("audioDelayLanguage"),
                youtubeLive = savedStateHandle.get<String>("youtubeLive")
                    ?.toBooleanStrictOrNull() == true,
                genres = presentationList("genres"),
                runtime = presentationText("runtime")
                    ?: presentation.optInt("runtimeMinutes").takeIf { it > 0 }?.let { "$it min" },
                initialCastMembers = presentationCast(),
                initialDirector = presentationList("director"),
                initialDescription = presentationText("description", "overview", "plot", "synopsis")
            )
        }
    }
}
