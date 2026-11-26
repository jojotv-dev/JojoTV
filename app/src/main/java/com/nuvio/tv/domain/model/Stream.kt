package com.nuvio.tv.domain.model

import androidx.compose.runtime.Immutable
import com.nuvio.tv.data.mediaserver.ServerPlaybackTarget
import java.net.URI
import kotlin.math.roundToInt
import kotlinx.serialization.Serializable

/**
 * Represents a stream source from a Stremio addon
 */
@Immutable
data class Stream(
    val name: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val ytId: String?,
    val externalUrl: String?,
    val behaviorHints: StreamBehaviorHints?,
    val addonName: String,
    val addonLogo: String?,
    val quality: String? = null,
    val qualityValue: Int = -1,
    val clientResolve: StreamClientResolve? = null,
    val badges: List<StreamBadge> = emptyList(),
    val subtitles: List<Subtitle> = emptyList(),
    val serverTarget: ServerPlaybackTarget? = null,
    val pluginSourceName: String? = null
) {
    /**
     * Returns the primary stream source URL
     */
    fun getStreamUrl(): String? =
        listOfNotNull(url, externalUrl)
            .firstOrNull(String::isSupportedStreamUrl)

    fun isPlayableSource(): Boolean =
        serverTarget != null || getStreamUrl() != null || youTubeIdToResolve() != null

    /**
     * Returns true if this is a YouTube stream
     */
    fun isYouTube(): Boolean = ytId != null

    /**
     * Returns the YouTube video id when [ytId] is the only way to play this stream,
     * so the video has to be resolved on the device before playback.
     */
    fun youTubeIdToResolve(): String? =
        ytId?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.takeIf { getStreamUrl() == null }

    /**
     * Returns true if this is an external URL (opens in browser)
     */
    fun isExternal(): Boolean = externalUrl != null && url == null && getStreamUrl() == externalUrl

    /**
     * Returns a display name for the stream, or null when no field is usable.
     * UI call sites should substitute a localized fallback (R.string.stream_unknown).
     */
    fun getDisplayNameOrNull(): String? = name ?: title ?: description

    /**
     * Returns a display name for the stream
     */
    fun getDisplayName(): String = getDisplayNameOrNull() ?: "Unknown Stream"

    /**
     * Returns a display description for the stream
     */
    fun getDisplayDescription(): String? = description ?: title

    /**
     * Returns a stable key for use in LazyColumn/LazyRow.
     * Incorporates all content-identifying fields so the key doesn't change
     * when the list recomposes or items shift position. The [occurrence] parameter
     * disambiguates genuine duplicates (same addon+url+name+title).
     */
    fun stableKey(occurrence: Int = 0): String = buildString {
        append(addonName)
        append('\u0000')
        append(url ?: ytId ?: externalUrl ?: serverTarget?.key() ?: "")
        append('\u0000')
        append(name ?: "")
        append('\u0000')
        append(title ?: "")
        append('\u0000')
        append(description ?: "")
        append('\u0000')
        append(quality ?: "")
        append('\u0000')
        append(occurrence)
    }
}

@Immutable
@Serializable
data class StreamBadge(
    val name: String,
    val imageURL: String = "",
    val tagColor: String = "",
    val tagStyle: String = "",
    val textColor: String = "",
    val borderColor: String = ""
)

const val AUDIO_DELAY_MIN_MS = -60000
const val AUDIO_DELAY_MAX_MS = 60000

/** Normalizes a `behaviorHints.audioDelayMs` value: whole ms, NaN/Infinity dropped, clamped to ±60 s. */
fun Number?.toStreamAudioDelayMs(): Int? =
    this?.toDouble()
        ?.takeIf { it.isFinite() }
        ?.roundToInt()
        ?.coerceIn(AUDIO_DELAY_MIN_MS, AUDIO_DELAY_MAX_MS)

/** Normalizes a `behaviorHints.audioDelayLanguage` value: trimmed language code, blank or oversized values dropped. */
fun String?.toStreamAudioDelayLanguage(): String? =
    this?.trim()?.takeIf { it.isNotEmpty() && it.length <= 35 }

@Immutable
data class StreamBehaviorHints(
    val notWebReady: Boolean?,
    val bingeGroup: String?,
    val countryWhitelist: List<String>?,
    val proxyHeaders: ProxyHeaders?,
    val videoHash: String? = null,
    val videoSize: Long? = null,
    val filename: String? = null,
    // Extra audio delay for this stream (ms, >0 = audio later), added on top of the user's/device delay.
    val audioDelayMs: Int? = null,
    // When set, audioDelayMs only applies to audio and embedded subtitle tracks in this language.
    val audioDelayLanguage: String? = null
)

@Immutable
data class StreamClientResolve(
    val type: String?,
    val filename: String?,
    val mediaType: String?,
    val mediaId: String?,
    val mediaOnlyId: String?,
    val title: String?,
    val season: Int?,
    val episode: Int?,
    val service: String?,
    val serviceIndex: Int?,
    val serviceExtension: String?,
    val isCached: Boolean?,
    val stream: StreamClientResolveStream? = null
)

@Immutable
data class StreamClientResolveStream(
    val raw: StreamClientResolveRaw?
)

@Immutable
data class StreamClientResolveRaw(
    val filename: String?,
    val size: Long?,
    val folderSize: Long?,
    val indexer: String?,
    val network: String?,
    val parsed: StreamClientResolveParsed?
)

@Immutable
data class StreamClientResolveParsed(
    val rawTitle: String?,
    val parsedTitle: String?,
    val year: Int?,
    val resolution: String?,
    val seasons: List<Int>?,
    val episodes: List<Int>?,
    val quality: String?,
    val hdr: List<String>?,
    val codec: String?,
    val audio: List<String>?,
    val channels: List<String>?,
    val languages: List<String>?,
    val group: String?,
    val network: String?,
    val edition: String?,
    val duration: Long?,
    val bitDepth: String?,
    val extended: Boolean?,
    val theatrical: Boolean?,
    val remastered: Boolean?,
    val unrated: Boolean?
)

@Immutable
data class ProxyHeaders(
    val request: Map<String, String>?,
    val response: Map<String, String>?
)

/**
 * Represents streams grouped by addon source
 */
@Immutable
data class AddonStreams(
    val addonName: String,
    val addonLogo: String?,
    val streams: List<Stream>
)

private val supportedStreamSchemes = setOf(
    "http", "https", "rtmp", "rtmps", "rtsp", "udp", "file", "content",
    "android.resource", "smb", "freebox"
)

private fun String.isSupportedStreamUrl(): Boolean = runCatching {
    URI(trim()).scheme?.lowercase() in supportedStreamSchemes
}.getOrDefault(false)
