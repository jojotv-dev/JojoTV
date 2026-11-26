package com.nuvio.tv.reshaped.livetv

import androidx.compose.runtime.Immutable

private val IPTV_TITLE_TECHNICAL_TAG = Regex(
    """\s*[\[(][^\])]*(?:2160p?|4k|uhd|hdr(?:10\+)?|dolby|vision|\bdv\b|hevc|h\.?265|x265|1080p?|720p?|multi|vff|vfq|vostfr|h264|ac3|aac|dts)[^\])]*[\])]\s*$""",
    RegexOption.IGNORE_CASE,
)
private val IPTV_TITLE_TECHNICAL_TAIL = Regex(
    """(?:\s*(?:[-–|•·_:]+\s*)|\s+)(?:2160p?|4k(?:\s+uhd)?|uhd|hdr(?:10\+)?|dolby(?:\s+vision)?|\bdv\b|hevc|h\.?265|x265|1080p?|720p?|multi|vff|vfq|vostfr|h264|ac3|aac|dts)(?:\s+(?:hdr(?:10\+)?|dolby(?:\s+vision)?|\bdv\b|hevc|h\.?265|x265|1080p?|720p?|multi|vff|vfq|vostfr|h264|ac3|aac|dts))*\s*$""",
    RegexOption.IGNORE_CASE,
)

/** Removes common provider quality/language tags while retaining the title and its year. */
internal fun normalizeLiveTvVodTitleForTmdb(value: String): String {
    var title = value.trim()
    repeat(6) {
        val withoutBracketTag = IPTV_TITLE_TECHNICAL_TAG.replaceFirst(title, "").trimEnd()
        val cleaned = if (withoutBracketTag == title) {
            IPTV_TITLE_TECHNICAL_TAIL.replaceFirst(title, "").trimEnd(' ', '-', '–', '|', '•', '·', ':', '_')
        } else {
            withoutBracketTag
        }
        if (cleaned == title || cleaned.isBlank()) return title
        title = cleaned
    }
    return title
}

/** A channel as the list shows it. [streamUrl] is also its identity (favorites and history). */
@Immutable
data class LiveTvChannel(
    val id: String,
    val name: String,
    val streamUrl: String,
    val tvgId: String? = null,
    val logoUrl: String? = null,
    val group: String = "",
    val headers: Map<String, String> = emptyMap(),
    /** Stalker only: the command a playable link is created from, per play. */
    val stalkerCommand: String? = null,
    /** The [LiveTvSource.id] this channel was listed by. */
    val sourceId: String = "",
    /** What hiding this channel stores: see [liveTvHideKey]. */
    val hideKey: Long = 0L,
    /** What its guide is kept under: see [liveTvGuideKey]. */
    val guideKey: String = "",
    /** The playlist's tvg-name when it differs from [name]: guides often list the channel by it. */
    val tvgName: String? = null,
    /** How past programmes of this channel can be played again; null when the provider keeps none. */
    val catchup: LiveTvCatchup? = null,
)

/**
 * A channel's catch-up (archive): how its provider builds the link to a past programme, and how
 * many days back it goes. Channels of one source share one instance.
 */
@Immutable
data class LiveTvCatchup(
    val kind: Kind,
    val days: Int,
    /** M3U `catchup-source`: the link template, or what is added to the live link. */
    val template: String? = null,
) {
    enum class Kind {
        /** Xtream panels: `/timeshift/user/pass/minutes/start/id.ts`, in the panel's time zone. */
        Xtream,
        /** `catchup-source` is the whole link (or added to the live link when it is not one). */
        Default,
        /** `catchup-source` is added to the live link. */
        Append,
        /** `?utc=start&lutc=now` on the live link. */
        Shift,
        /** Flussonic servers: `index-start-duration.m3u8` / `timeshift_abs-start.ts`. */
        Flussonic,
    }
}

/** The category key of channels the playlist gives no category; the screens call it "Uncategorised". */
const val LIVE_TV_UNGROUPED = ""

/**
 * A hidden channel is kept as a 64-bit hash of its source, category and name, not its link: the
 * link can be long, carries account details, and changes when a provider rotates tokens.
 */
fun liveTvHideKey(sourceId: String, group: String, name: String): Long {
    var hash = -0x340d631b7bdddcdbL // FNV-1a 64 offset basis
    fun mix(text: String) {
        text.forEach { char ->
            hash = (hash xor char.code.toLong()) * 0x100000001b3L
        }
        hash = (hash xor 0x1fL) * 0x100000001b3L
    }
    mix(sourceId)
    mix(group)
    mix(name)
    return hash
}

/**
 * A playlist the viewer made from channels of any source: [urls] are [LiveTvChannel.streamUrl]s,
 * in the viewer's order. Lists sort by [id], which starts with when they were made.
 */
@Immutable
data class LiveTvCustomList(val id: String, val name: String, val urls: List<String>)

@Immutable
data class LiveTvRecentChannel(
    val streamUrl: String,
    val name: String,
    val logoUrl: String? = null,
    val group: String = "",
    val tvgId: String? = null,
) {
    val guideKey: String get() = liveTvGuideKey(tvgId, name)
}

internal const val LIVE_TV_CHANNEL_HISTORY_LIMIT = 100

@Immutable
data class LiveTvProgramme(
    val title: String,
    val startEpochMs: Long,
    val stopEpochMs: Long,
    /** The guide's description, kept only for programmes near now (see [LiveTvGuideWindow.detailsMs]). */
    val description: String? = null,
    /** The guide's picture for it (an http link), kept like [description]. */
    val image: String? = null,
)

enum class LiveTvSourceType { M3u, Stalker, Xtream }

@Immutable
data class LiveTvStalkerSettings(
    val portalUrl: String = "",
    val macAddress: String = "",
    val username: String = "",
    val password: String = "",
    val serialNumber: String = "",
    val deviceId: String = "",
    val deviceId2: String = "",
    val deviceVersion: String = "",
    val deviceModel: String = "",
    val timezone: String = "",
    val httpProxy: String = "",
    val httpHeaders: String = "",
    val httpCookie: String = "",
    val stbUserAgent: String = "",
    val playerUserAgent: String = "",
    val requestMethod: String = "GET",
) {
    val isConfigured: Boolean get() = portalUrl.isNotBlank() && macAddress.isNotBlank()
}

@Immutable
data class LiveTvXtreamSettings(
    val serverUrl: String = "",
    val username: String = "",
    val password: String = "",
) {
    val isConfigured: Boolean get() = serverUrl.isNotBlank() && username.isNotBlank() && password.isNotBlank()
}

/** A movie or series returned by an Xtream VOD catalogue. */
data class LiveTvVodItem(
    val id: String,
    val sourceId: String,
    val providerId: String,
    val title: String,
    val type: String,
    val categoryId: String = "",
    val categoryName: String = "",
    val posterUrl: String = "",
    val extension: String = "mp4",
    val year: String = "",
    val plot: String = "",
    val season: Int = 0,
    val episode: Int = 0,
    val command: String = "",
    val seriesId: String = "",
    val seriesTitle: String = "",
) {
    /** Stable per-source identity used by the IPTV favourites list. */
    val favoriteKey: String get() = "$sourceId:$type:$id"
}

data class LiveTvVodCategory(val id: String, val sourceId: String, val name: String)
data class LiveTvVodPlayback(val url: String, val headers: Map<String, String> = emptyMap())

/** One saved channel source. Several can be added; their channels show as one list. */
@Immutable
data class LiveTvSource(
    val id: String,
    val type: LiveTvSourceType,
    /** The M3U link or an imported file's name; the server or portal URL for the others. */
    val url: String = "",
    val stalker: LiveTvStalkerSettings = LiveTvStalkerSettings(),
    val xtream: LiveTvXtreamSettings = LiveTvXtreamSettings(),
    /** A guide (XMLTV) link the viewer added; read before the source's own guide. */
    val epgUrl: String = "",
    /** The name the viewer gave the source; blank shows [label]'s default. */
    val name: String = "",
) {
    /** A short name for lists: the viewer's name for it, else the host of a link, or the imported file's name. */
    val label: String
        get() = name.trim().ifBlank {
            url.substringAfter("://", url).substringBefore('/').substringBefore('?')
                .substringAfterLast('@').ifBlank { url }
        }

    /** Two sources with the same identity are one: adding it again replaces it. */
    internal val identity: String
        get() = when (type) {
            LiveTvSourceType.M3u -> "m3u|${url.lowercase()}"
            LiveTvSourceType.Xtream -> "xtream|${xtream.serverUrl.lowercase()}|${xtream.username}"
            LiveTvSourceType.Stalker -> "stalker|${stalker.portalUrl.lowercase()}|${stalker.macAddress}"
        }
}

@Immutable
data class LiveTvUiState(
    val sources: List<LiveTvSource> = emptyList(),
    val channels: List<LiveTvChannel> = emptyList(),
    /** Sorted category names of [channels], hidden ones included, computed once per load rather than per frame. */
    val groups: List<String> = emptyList(),
    /** How many channels each category has. */
    val groupCounts: Map<String, Int> = emptyMap(),
    /** Categories the viewer chose not to see: their channels leave the list, search and zapping. */
    val hiddenGroups: Set<String> = emptySet(),
    /** Movie and series categories hidden by the viewer, keyed by type, source and provider ID. */
    val hiddenVodCategoryKeys: Set<String> = emptySet(),
    /** Names the viewer gave categories, by the playlist's name. */
    val groupNames: Map<String, String> = emptyMap(),
    /** Single channels the viewer chose not to see ([LiveTvChannel.hideKey]), inside categories that stay. */
    val hiddenChannelKeys: Set<Long> = emptySet(),
    /** [channels] without hidden categories and channels: what All channels and zapping go through. */
    val shownChannels: List<LiveTvChannel> = emptyList(),
    /** How many channels each source listed. */
    val sourceCounts: Map<String, Int> = emptyMap(),
    /** Sources whose last load failed (their earlier channels, if any, stay listed). */
    val sourceErrors: Map<String, LiveTvError> = emptyMap(),
    /** [LiveTvChannel.guideKey] to the programme on air now. */
    val currentProgrammes: Map<String, LiveTvProgramme> = emptyMap(),
    /** [LiveTvChannel.guideKey] to the guide's logo, for channels the playlist gives none. */
    val guideLogos: Map<String, String> = emptyMap(),
    /** Goes up each time the kept guide is read again, so the programme guide redraws. */
    val guideVersion: Int = 0,
    val recentChannel: LiveTvRecentChannel? = null,
    val favoriteUrls: Set<String> = emptySet(),
    /** Stable channel identities used to synchronize favourites without uploading stream URLs. */
    val favoriteChannelKeys: Set<Long> = emptySet(),
    /** Saved movie and series catalogue item keys. */
    val favoriteVodKeys: Set<String> = emptySet(),
    /** The viewer's own playlists, oldest first. */
    val customLists: List<LiveTvCustomList> = emptyList(),
    val isEpgLoading: Boolean = false,
    val isLoading: Boolean = false,
    /** Source storage is ready for adds, even if its existing channels are still loading. */
    val sourceStorageReady: Boolean = false,
    /** A provider submitted from the source form is being checked and saved. */
    val isAddingSource: Boolean = false,
    val isLoaded: Boolean = false,
    /** The last failed attempt to add a source. */
    val error: LiveTvError? = null,
    /** Goes up each time a source is added, so the Sources dialog can tell an add went through. */
    val addedCount: Int = 0,
    /** How each source's guide did, for the Sources dialog; a source missing here has not been read yet. */
    val sourceGuides: Map<String, LiveTvSourceGuide> = emptyMap(),
    /** Recently played channels, newest first. */
    val channelHistory: List<LiveTvRecentChannel> = emptyList(),
    /** Saved order of the category rail, refreshed when account settings arrive. */
    val categoryRailOrder: List<String> = emptyList(),
) {
    val hasSource: Boolean get() = sources.isNotEmpty() || channels.isNotEmpty()

    /** The channel's logo, or the guide's when the playlist has none. */
    fun logoFor(channel: LiveTvChannel): String? =
        channel.logoUrl?.takeIf(String::isNotBlank) ?: guideLogos[channel.guideKey]

    /** Categories the list shows. */
    val visibleGroups: List<String>
        get() {
            if (hiddenGroups.isEmpty()) return groups
            if (hiddenGroups.none { it.startsWith("tv\u001f") }) return groups.filterNot(hiddenGroups::contains)
            val groupsWithVisibleProviders = channels.asSequence()
                .filter { channel ->
                    channel.group !in hiddenGroups &&
                        LiveTvRepository.tvGroupHideKey(channel.sourceId, channel.group) !in hiddenGroups
                }
                .mapTo(HashSet()) { it.group }
            return groups.filter { it !in hiddenGroups && it in groupsWithVisibleProviders }
        }
}

/** How a source's guide did: none offered, loading, read for [channels] channels, or failed. */
@Immutable
data class LiveTvSourceGuide(val state: State, val channels: Int = 0) {
    enum class State { None, Loading, Loaded, Failed }

    companion object {
        val None = LiveTvSourceGuide(State.None)
        val Loading = LiveTvSourceGuide(State.Loading)
        val Failed = LiveTvSourceGuide(State.Failed)
    }
}
