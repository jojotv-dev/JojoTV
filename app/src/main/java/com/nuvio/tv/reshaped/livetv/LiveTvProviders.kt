package com.nuvio.tv.reshaped.livetv

import android.util.Log
import android.util.JsonReader
import android.util.JsonToken
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URLEncoder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select
import org.json.JSONArray
import org.json.JSONObject

/*
 * Xtream Codes and Stalker (MAG) providers. Channel lists are read with a streaming JSON reader,
 * one entry at a time, so a 20 000 channel provider never becomes a JSON tree in memory.
 */

internal fun String.urlEncoded(): String = URLEncoder.encode(this, "UTF-8").replace("+", "%20")

private fun Map<String, String>.firstValue(vararg names: String): String? {
    names.forEach { name ->
        get(name)?.takeIf(String::isNotBlank)?.let { return it }
    }
    return entries.firstOrNull { (key, value) -> value.isNotBlank() && names.any { it.equals(key, ignoreCase = true) } }?.value
}

/** Provider portals sometimes return poster paths relative to their own base URL. */
private fun resolveVodPoster(source: LiveTvSource, raw: String?): String {
    val value = raw?.trim()?.takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
        ?: return ""
    val parsed = runCatching { java.net.URI(value) }.getOrNull()
    if (parsed?.scheme.equals("http", true) || parsed?.scheme.equals("https", true)) return value
    val base = when (source.type) {
        LiveTvSourceType.Stalker -> source.stalker.portalUrl.trim().trimEnd('/')
        LiveTvSourceType.Xtream -> source.xtream.serverUrl.trim().trimEnd('/')
        LiveTvSourceType.M3u -> source.url.trim().substringBeforeLast('/', "")
    }
    if (base.isBlank()) return ""
    return runCatching {
        val baseUri = java.net.URI(if (base.endsWith('/')) base else "$base/")
        val resolved = baseUri.resolve(value)
        resolved.takeIf { it.scheme == "http" || it.scheme == "https" }?.toString().orEmpty()
    }.getOrDefault("")
}

/** Prefer a provider's readable label when it sends both a name and a numeric category alias. */
private fun Map<String, String>.firstCategoryName(vararg names: String): String? {
    val candidates = names.mapNotNull { name ->
        get(name)?.takeIf(String::isNotBlank)
            ?: entries.firstOrNull { (key, value) -> key.equals(name, ignoreCase = true) && value.isNotBlank() }?.value
    }
    return candidates.firstOrNull { it.trim().toLongOrNull() == null }
}

/** Resolve provider category IDs first; never treat a channel's title/name as its group. */
private fun Map<String, String>.resolveCategoryName(categories: Map<String, String>, vararg idFields: String): String {
    idFields.mapNotNull { firstValue(it) }.firstNotNullOfOrNull { id ->
        categories[id]?.takeIf { it.trim().toLongOrNull() == null }
            ?: categories.entries.firstOrNull { it.key.equals(id, ignoreCase = true) }
                ?.value?.takeIf { it.trim().toLongOrNull() == null }
    }?.let { return it }
    return firstCategoryName(
        "category_name", "group_name", "genre_name", "tv_genre_name", "category_title", "group_title", "genre_title", "tv_genre_title",
        "categoryName", "groupName", "genreName", "category", "group", "genre", "tv_genre",
    )?.takeIf { it.trim().toLongOrNull() == null }.orEmpty()
}

private fun String.isHttp(): Boolean = isHttpUrl()

// region Xtream

internal fun LiveTvXtreamSettings.normalized(): LiveTvXtreamSettings = copy(
    serverUrl = serverUrl.trim().trimEnd('/').substringBefore("/player_api.php").trimEnd('/'),
    username = username.trim(),
    password = password.trim(),
)

/** A provider's channels and its categories in the provider's own order. */
internal class ProviderChannels(val channels: List<LiveTvChannel>, val groupOrder: List<String>, val incomplete: Boolean = false)

internal object LiveTvXtream {
    /** Each panel's time zone (by server URL): catch-up start times are given in it. */
    private val zones = java.util.concurrent.ConcurrentHashMap<String, java.time.ZoneId>()

    suspend fun vodCategories(source: LiveTvSource, type: String): List<LiveTvVodCategory> {
        require(type == "movie" || type == "series")
        val settings = source.xtream.normalized()
        return LiveTvHttp.stream(apiUrl(settings, if (type == "movie") "get_vod_categories" else "get_series_categories"), LIVE_TV_PLAYLIST_HEADERS) { input ->
            readObjects(input) { fields ->
                val id = fields.firstValue("category_id", "id", "categoryId") ?: return@readObjects null
                val name = fields.firstCategoryName("category_name", "name", "title", "category", "group_name", "group", "category_title", "group_title", "genre_name", "genre_title", "genre") ?: return@readObjects null
                LiveTvVodCategory(id, source.id, name)
            }
        }
    }

    suspend fun vodItems(
        source: LiveTvSource,
        type: String,
        categoryId: String = "",
        onBatch: (List<LiveTvVodItem>) -> Unit = {},
    ): List<LiveTvVodItem> {
        require(type == "movie" || type == "series")
        val settings = source.xtream.normalized()
        val action = if (type == "movie") "get_vod_streams" else "get_series"
        val url = apiUrl(settings, action) + if (categoryId.isBlank()) "" else "&category_id=${categoryId.urlEncoded()}"
        var receivedEntries = 0
        var skippedEntries = 0
        val items = LiveTvHttp.stream(
            url,
            LIVE_TV_PLAYLIST_HEADERS,
            readTimeoutSeconds = 120L,
            callTimeoutSeconds = 240L,
        ) { input ->
            readObjects(input, onBatch) { fields ->
                receivedEntries++
                val id = fields["stream_id"] ?: fields["streamid"]
                    ?: fields["movie_id"] ?: fields["movieid"]
                    ?: fields["series_id"] ?: fields["seriesid"]
                    ?: fields["vod_id"]
                    ?: fields["vodid"]
                    ?: fields["id"]
                if (id.isNullOrBlank()) {
                    skippedEntries++
                    return@readObjects null
                }
                val title = fields["name"] ?: fields["title"] ?: fields["stream_name"] ?: fields["streamname"]
                    ?: fields["series_name"] ?: fields["seriesname"] ?: fields["movie_name"] ?: fields["vod_name"]
                if (title.isNullOrBlank()) {
                    skippedEntries++
                    return@readObjects null
                }
                LiveTvVodItem(
                    id = "${source.id}:$type:$id",
                    sourceId = source.id,
                    providerId = id,
                    title = title,
                    type = type,
                    categoryId = fields.firstValue("category_id", "category", "categoryId")
                        ?.takeIf(String::isNotBlank)
                        ?: categoryId,
                    categoryName = fields.firstCategoryName(
                        "category_name", "group_name", "genre_name", "genre_title", "category_title", "group_title",
                        "categoryName", "groupName", "genreName", "genre", "category", "group",
                    ).orEmpty(),
                    posterUrl = resolveVodPoster(source, fields.firstValue(
                        "stream_icon", "cover", "cover_big", "movie_image", "movie_img", "poster", "poster_url", "image", "image_url"
                    )),
                    extension = (fields["container_extension"] ?: fields["extension"])?.takeIf(String::isNotBlank) ?: "mp4",
                    year = fields["year"]?.takeIf(String::isNotBlank) ?: fields["releasedate"]?.take(4).orEmpty(),
                    plot = fields["plot"].orEmpty(),
                )
            }
        }
        val uniqueIds = items.asSequence().map(LiveTvVodItem::id).distinct().count()
        Log.i(
            "LiveTvXtream",
            "VOD $type parsed=${items.size} unique=$uniqueIds received=$receivedEntries skipped=$skippedEntries " +
                "categoryScoped=${categoryId.isNotBlank()}"
        )
        return items
    }

    suspend fun seriesEpisodes(source: LiveTvSource, series: LiveTvVodItem): List<LiveTvVodItem> {
        val responseText = LiveTvHttp.text(apiUrl(source.xtream.normalized(), "get_series_info") +
            "&series_id=${series.providerId.urlEncoded()}", LIVE_TV_PLAYLIST_HEADERS)

        fun parseContainer(value: Any?): Any? {
            if (value !is String) return value
            val text = value.trim()
            if (text.isEmpty()) return null
            return runCatching { JSONObject(text) }.getOrNull()
                ?: runCatching { JSONArray(text) }.getOrNull()
        }

        fun hasEntries(value: Any?): Boolean = when (val parsed = parseContainer(value)) {
            is JSONObject -> parsed.length() > 0
            is JSONArray -> parsed.length() > 0
            else -> false
        }

        fun episodePayload(value: Any?, depth: Int = 0): Any? {
            if (depth > 5) return null
            return when (val parsed = parseContainer(value)) {
                is JSONObject -> {
                    listOf("episodes", "series", "results", "items", "list").firstNotNullOfOrNull { key ->
                        parsed.opt(key)?.takeUnless { it === JSONObject.NULL }?.takeIf(::hasEntries)
                    }?.let(::parseContainer)
                        ?: listOf("js", "data", "seasons", "result", "results", "items", "list").firstNotNullOfOrNull { key ->
                            parsed.opt(key)?.let { episodePayload(it, depth + 1) }
                        }
                }
                is JSONArray -> parsed
                else -> null
            }
        }

        val root = parseContainer(responseText)
        val response = (root as? JSONObject)?.opt("js")?.let(::parseContainer) ?: root
        val rawEpisodes = episodePayload(response) ?: episodePayload(root)
            ?: throw IllegalStateException("Xtream series response did not contain an episodes list")

        data class EpisodeNode(val value: JSONObject, val season: Int, val ordinal: Int)
        val parsedEpisodes = ArrayList<EpisodeNode>()
        fun positiveInt(value: JSONObject, vararg keys: String): Int? = keys.firstNotNullOfOrNull { key ->
            value.optString(key).toIntOrNull()?.takeIf { it > 0 }
        }
        fun seasonFromKey(key: String): Int? = Regex("^(?:season|saison|s)?[ ._-]*(\\d{1,2})$", RegexOption.IGNORE_CASE)
            .matchEntire(key.trim())?.groupValues?.get(1)?.toIntOrNull()?.takeIf { it > 0 }
        fun providerId(value: JSONObject): String? = listOf("id", "episode_id", "stream_id")
            .firstNotNullOfOrNull { key ->
                value.optString(key).takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
            }

        fun collect(value: Any?, inheritedSeason: Int = 1) {
            when (val parsed = parseContainer(value)) {
                is JSONObject -> {
                    val season = positiveInt(parsed, "season", "season_number", "season_num") ?: inheritedSeason
                    val hasChildList = listOf("episodes", "series", "seasons", "data", "results", "items")
                        .any { key -> parsed.opt(key) is JSONArray || parsed.opt(key) is JSONObject }
                    val title = listOf("title", "name", "episode_name")
                        .firstNotNullOfOrNull { key -> parsed.optString(key).takeIf(String::isNotBlank) }
                    val isSeasonLabel = title?.let {
                        Regex("^(?:season|saison)\\s*\\d+$", RegexOption.IGNORE_CASE).matches(it.trim())
                    } == true
                    val looksLikeEpisode = providerId(parsed) != null && !hasChildList &&
                        !isSeasonLabel &&
                        (positiveInt(parsed, "episode_num", "episode_number", "series_number") != null ||
                            title != null)
                    if (looksLikeEpisode) {
                        parsedEpisodes += EpisodeNode(parsed, season.coerceAtLeast(1), parsedEpisodes.size + 1)
                        return
                    }
                    val keys = parsed.keys().asSequence().toList().sortedWith(
                        compareBy<String>({ it.toIntOrNull() ?: Int.MAX_VALUE }, { it })
                    )
                    keys.forEach { key ->
                        if (key !in setOf("info", "cover", "cover_big", "movie_image")) {
                            collect(parsed.opt(key), seasonFromKey(key) ?: season)
                        }
                    }
                }
                is JSONArray -> (0 until parsed.length()).forEach { index ->
                    collect(parsed.opt(index), inheritedSeason)
                }
            }
        }
        collect(rawEpisodes)
        if (parsedEpisodes.isEmpty() && when (rawEpisodes) {
                is JSONObject -> rawEpisodes.length() > 0
                is JSONArray -> rawEpisodes.length() > 0
                else -> false
            }
        ) {
            throw IllegalStateException("Xtream series response contained no parseable episodes")
        }

        return parsedEpisodes.map { node ->
            val episode = node.value
            val id = requireNotNull(providerId(episode))
            val number = positiveInt(episode, "episode_num", "episode_number", "series_number") ?: node.ordinal
            val season = positiveInt(episode, "season", "season_number", "season_num") ?: node.season
            val info = episode.optJSONObject("info")
            val poster = info?.optString("movie_image")?.takeIf(String::isNotBlank)
                ?: info?.optString("cover_big")?.takeIf(String::isNotBlank)
                ?: info?.optString("cover")?.takeIf(String::isNotBlank)
                ?: episode.optString("movie_image").takeIf(String::isNotBlank)
                ?: episode.optString("cover").takeIf(String::isNotBlank)
                ?: series.posterUrl
            LiveTvVodItem(
                id = "${source.id}:episode:$id",
                sourceId = source.id,
                providerId = id,
                title = episode.optString("title").ifBlank {
                    episode.optString("name").ifBlank {
                        episode.optString("episode_name").ifBlank { "S${season}E$number" }
                    }
                },
                type = "episode",
                posterUrl = resolveVodPoster(source, poster),
                extension = episode.optString("container_extension")
                    .ifBlank { episode.optString("extension") }
                    .ifBlank { "mp4" },
                season = season.coerceAtLeast(1),
                episode = number.coerceAtLeast(1),
                seriesId = "${series.sourceId}:series:${series.providerId}",
                seriesTitle = series.title,
            )
        }.sortedWith(compareBy(LiveTvVodItem::season, LiveTvVodItem::episode))
    }

    fun vodStreamUrl(source: LiveTvSource, item: LiveTvVodItem): String {
        val settings = source.xtream.normalized()
        val kind = if (item.type == "episode") "series" else "movie"
        return "${settings.serverUrl}/$kind/${settings.username.urlEncoded()}/" +
            "${settings.password.urlEncoded()}/${item.providerId.urlEncoded()}.${item.extension.ifBlank { "mp4" }}"
    }

    suspend fun channels(settings: LiveTvXtreamSettings): ProviderChannels {
        val categories = LiveTvHttp.stream(apiUrl(settings, "get_live_categories"), LIVE_TV_PLAYLIST_HEADERS) { input ->
            readObjects(input) { fields ->
                val id = fields.firstValue("category_id", "id", "categoryId") ?: return@readObjects null
                val name = fields.firstCategoryName("category_name", "name", "title", "category", "group_name", "group", "category_title", "group_title", "genre_name", "genre_title", "genre") ?: return@readObjects null
                id to name
            }
        }.toMap()
        val extension = liveExtension(settings)
        // One catch-up instance per archive length, shared by the channels that have it.
        val catchups = HashMap<Int, LiveTvCatchup>()
        val seen = HashSet<String>()
        val channels = LiveTvHttp.stream(apiUrl(settings, "get_live_streams"), LIVE_TV_PLAYLIST_HEADERS) { input ->
            var index = 0
            readObjects(input) { fields ->
                val position = index++
                val name = fields["name"] ?: return@readObjects null
                val streamId = fields["stream_id"] ?: fields["id"] ?: return@readObjects null
                // Always the panel's own link, as IPTV players use: "direct_source" is often the
                // panel's upstream origin, which refuses clients, and the panel redirects to it when it is meant to be used.
                val streamUrl = settings.liveStreamUrl(streamId, extension)
                if (!seen.add(streamUrl)) return@readObjects null
                LiveTvChannel(
                    id = "xtream-$streamId-$position",
                    name = name,
                    streamUrl = streamUrl,
                    tvgId = fields["epg_channel_id"] ?: fields["tvg_id"],
                    logoUrl = fields["stream_icon"] ?: fields["logo"],
                    group = fields.resolveCategoryName(categories, "category_id", "categoryId", "group_id", "genre_id"),
                    headers = LIVE_TV_STREAM_HEADERS,
                    catchup = if (fields["tv_archive"] == "1") {
                        val days = fields["tv_archive_duration"]?.toIntOrNull()?.coerceIn(1, 30) ?: 1
                        catchups.getOrPut(days) { LiveTvCatchup(LiveTvCatchup.Kind.Xtream, days) }
                    } else {
                        null
                    },
                )
            }
        }
        return ProviderChannels(channels, categories.values.map(String::trim).distinct())
    }

    /**
     * The panel's time zone, which catch-up links give their start time in: from the login's
     * `server_info.timezone`, read with the channel list (or now, once, when it was not), else the TV's.
     */
    suspend fun zone(serverUrl: String, username: String, password: String): java.time.ZoneId {
        zones[serverUrl]?.let { return it }
        // A login that failed (panel busy, timeout) is asked again, at most once a minute.
        zoneFailedAt[serverUrl]?.let { if (System.currentTimeMillis() - it < 60_000L) return java.time.ZoneId.systemDefault() }
        val settings = LiveTvXtreamSettings(serverUrl, username, password)
        val login = runCatching { JSONObject(LiveTvHttp.text(loginUrl(settings), LIVE_TV_PLAYLIST_HEADERS)) }
            .onFailure { if (it is CancellationException) throw it }
            .getOrNull()
        if (login == null) {
            zoneFailedAt[serverUrl] = System.currentTimeMillis()
            return java.time.ZoneId.systemDefault()
        }
        // A panel that gives no zone answers in the TV's own, as players assume.
        return (zoneOf(login) ?: java.time.ZoneId.systemDefault()).also { zones[serverUrl] = it }
    }

    private val zoneFailedAt = java.util.concurrent.ConcurrentHashMap<String, Long>()

    private fun zoneOf(login: JSONObject): java.time.ZoneId? =
        login.optJSONObject("server_info")?.optString("timezone")?.trim()?.takeIf(String::isNotEmpty)
            ?.let { runCatching { java.time.ZoneId.of(it) }.getOrNull() }

    /**
     * The live format this account may use: MPEG-TS, as IPTV players prefer, unless the account only
     * allows HLS ("allowed_output_formats" in the login reply); a TS link then fails on every channel.
     */
    private suspend fun liveExtension(settings: LiveTvXtreamSettings): String {
        val formats = try {
            val login = JSONObject(LiveTvHttp.text(loginUrl(settings), LIVE_TV_PLAYLIST_HEADERS))
            zoneOf(login)?.let { zones[settings.serverUrl] = it }
            val allowed = login.optJSONObject("user_info")?.optJSONArray("allowed_output_formats")
            if (allowed == null) emptyList() else List(allowed.length()) { allowed.optString(it).trim().lowercase() }
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (error: Exception) {
            Log.w("LiveTvXtream", "Could not read the account's formats; using TS", error)
            emptyList()
        }
        return if (formats.isEmpty() || "ts" in formats || "m3u8" !in formats) "ts" else "m3u8"
    }

    private fun loginUrl(settings: LiveTvXtreamSettings): String =
        "${settings.serverUrl}/player_api.php?username=${settings.username.urlEncoded()}" +
            "&password=${settings.password.urlEncoded()}"

    private fun apiUrl(settings: LiveTvXtreamSettings, action: String): String =
        "${settings.serverUrl}/player_api.php?username=${settings.username.urlEncoded()}" +
            "&password=${settings.password.urlEncoded()}&action=${action.urlEncoded()}"

    private fun LiveTvXtreamSettings.liveStreamUrl(streamId: String, extension: String): String =
        "$serverUrl/live/${username.urlEncoded()}/${password.urlEncoded()}/${streamId.urlEncoded()}.$extension"
}

// endregion

// region Stalker

internal fun LiveTvStalkerSettings.normalized(): LiveTvStalkerSettings = copy(
    portalUrl = portalUrl.trim().trimEnd('/'),
    macAddress = macAddress.trim().uppercase(),
    username = username.trim(),
    password = password.trim(),
    serialNumber = serialNumber.trim(),
    deviceId = deviceId.trim(),
    deviceId2 = deviceId2.trim(),
    deviceVersion = deviceVersion.trim(),
    deviceModel = deviceModel.trim(),
    timezone = timezone.trim(),
    httpProxy = httpProxy.trim(),
    httpHeaders = httpHeaders.trim(),
    httpCookie = httpCookie.trim(),
    stbUserAgent = stbUserAgent.trim(),
    playerUserAgent = playerUserAgent.trim(),
    requestMethod = if (requestMethod.equals("POST", ignoreCase = true)) "POST" else "GET",
)

internal data class StalkerChannels(val channels: List<LiveTvChannel>, val incomplete: Boolean, val groupOrder: List<String> = emptyList())

private class StalkerSession(val settings: LiveTvStalkerSettings, val token: String?) {
    /** Built once per session and shared by every channel, not copied into each. */
    var playbackHeaders: Map<String, String>? = null
}

private suspend fun <T> withStalkerRetry(operation: String, request: suspend () -> T): T {
    try {
        return request()
    } catch (firstFailure: Exception) {
        if (firstFailure is CancellationException) throw firstFailure
        Log.w("LiveTvStalker", "$operation failed; retrying once", firstFailure)
        try {
            return request()
        } catch (retryFailure: Exception) {
            if (retryFailure is CancellationException) throw retryFailure
            Log.w("LiveTvStalker", "$operation failed after retry", retryFailure)
            throw retryFailure
        }
    }
}

private fun stalkerChannelKey(channel: LiveTvChannel): String =
    channel.id.takeUnless { it.startsWith("stalker-") }.orEmpty().ifBlank { channel.streamUrl }

internal object LiveTvStalker {
    private const val MAX_PAGES = 500
    private const val PARALLEL_PAGES = 4

    /** One session per portal login, so several Stalker sources do not keep renewing each other's. */
    private val sessions = java.util.concurrent.ConcurrentHashMap<LiveTvStalkerSettings, StalkerSession>()

    fun clearSession() {
        sessions.clear()
    }

    suspend fun vodCategories(source: LiveTvSource, type: String): List<LiveTvVodCategory> {
        require(type == "movie" || type == "series")
        return withSession(source.stalker.normalized()) { session ->
            withStalkerRetry("VOD $type categories") {
                dataObjects(
                    session,
                    if (type == "movie") "vod" else "series",
                    "get_categories",
                    failOnUnmappedEntries = true,
                ) { fields ->
                    val id = fields.firstValue("id", "category_id", "alias") ?: return@dataObjects null
                    val name = fields.firstCategoryName(
                        "title", "name", "category_name", "group_name", "genre_name", "tv_genre_name",
                        "category_title", "group_title", "genre_title", "tv_genre_title", "categoryName", "groupName",
                        "genreName", "category", "group", "genre",
                    ) ?: return@dataObjects null
                    LiveTvVodCategory(id, source.id, name)
                }
            }
        }
    }

    suspend fun vodItems(
        source: LiveTvSource,
        type: String,
        categoryId: String = "",
        onBatch: (List<LiveTvVodItem>) -> Unit = {},
    ): List<LiveTvVodItem> {
        require(type == "movie" || type == "series")
        return withSession(source.stalker.normalized()) { session ->
            val result = ArrayList<LiveTvVodItem>()
            val seenIds = HashSet<String>()
            var reportedTotal: Int? = null
            var receivedEntries = 0
            var skippedEntries = 0
            var pagesRead = 0
            var reportedTotalExceeded = false
            var noProgressPages = 0
            suspend fun readPage(
                pageNumber: Int,
                initialProbe: Boolean = false,
            ): StalkerPage<LiveTvVodItem> = withStalkerRetry("VOD $type page $pageNumber") {
                    LiveTvHttp.stream(
                        url(session.settings, session.token, if (type == "movie") "vod" else "series", "get_ordered_list",
                            buildMap {
                                put("category", categoryId.ifBlank { "*" })
                                put("sortby", "added")
                                put("p", pageNumber.toString())
                                if (type == "movie") put("genre", "0")
                            }),
                        baseHeaders(session.settings) + tokenHeader(session.token),
                        // The first pages decide how the portal numbers its pages. Keep these
                        // probes bounded so an unsupported p=0 cannot hold the import for minutes.
                        readTimeoutSeconds = if (initialProbe) 30L else 120L,
                        callTimeoutSeconds = if (initialProbe) 45L else 240L,
                        httpMethod = session.settings.requestMethod,
                        proxyUrl = session.settings.httpProxy,
                    ) { input ->
                        readStalkerPage(input) { fields ->
                            val id = fields.firstValue(
                                "id", "movie_id", "vod_id", "series_id", "stream_id", "video_id",
                            ) ?: return@readStalkerPage null
                            val title = fields.firstValue(
                                "name", "title", "movie_name", "series_name", "stream_name", "o_name",
                            ) ?: return@readStalkerPage null
                            LiveTvVodItem(
                                id = "${source.id}:$type:$id", sourceId = source.id, providerId = id,
                                title = title, type = type,
                                categoryId = fields.firstValue("category", "category_id", "genre_id")
                                    ?.takeIf(String::isNotBlank)
                                    ?: categoryId,
                                categoryName = fields.firstCategoryName(
                                    "category_name", "group_name", "genre_name", "genre_title", "category_title",
                                    "group_title", "category", "group", "genre",
                                ).orEmpty(),
                                posterUrl = resolveVodPoster(source, fields.firstValue(
                                    "screenshot_uri", "cover", "cover_big", "movie_image", "poster", "poster_url", "image", "logo"
                                )),
                                year = fields.firstValue("year", "release_year").orEmpty(),
                                plot = fields.firstValue("description", "plot", "plot_outline").orEmpty(),
                                command = fields.firstValue("cmd", "command", "url").orEmpty(),
                            )
                        }
                    }
                }

            // Probe p=0, p=1 and p=2 concurrently: portals disagree about the first page, and an
            // unsupported index can take minutes to time out. Publish each page as it arrives so
            // a slow p=0 probe cannot hold back the usual p=1 and p=2 catalogue pages.
            suspend fun readPageResult(pageNumber: Int): Result<StalkerPage<LiveTvVodItem>> = try {
                Result.success(readPage(pageNumber, initialProbe = true))
            } catch (failure: CancellationException) {
                throw failure
            } catch (failure: Exception) {
                Result.failure(failure)
            }

            val emittedProbeIds = HashSet<String>()
            val pageResults = coroutineScope {
                val pending = linkedMapOf(
                    0 to async { readPageResult(0) },
                    1 to async { readPageResult(1) },
                    2 to async { readPageResult(2) },
                )
                val completed = LinkedHashMap<Int, Result<StalkerPage<LiveTvVodItem>>>()
                while (pending.isNotEmpty()) {
                    val response = select<Pair<Int, Result<StalkerPage<LiveTvVodItem>>>> {
                        pending.forEach { (number, deferred) ->
                            deferred.onAwait { number to it }
                        }
                    }
                    pending.remove(response.first)
                    completed[response.first] = response.second
                    response.second.getOrNull()?.entries
                        ?.filter { emittedProbeIds.add(it.id) }
                        ?.takeIf { it.isNotEmpty() }
                        ?.let(onBatch)
                }
                completed
            }
            val zeroResult = pageResults.getValue(0)
            val oneResult = pageResults.getValue(1)
            val zeroPage = zeroResult.getOrNull()
            val onePage = oneResult.getOrNull()
            if (zeroPage == null && onePage == null) {
                throw oneResult.exceptionOrNull() ?: zeroResult.exceptionOrNull()
                    ?: java.io.IOException("Stalker $type first pages could not be loaded")
            }
            if (onePage == null && zeroPage?.entries.isNullOrEmpty()) {
                throw oneResult.exceptionOrNull() ?: java.io.IOException("Stalker $type page 1 could not be loaded")
            }
            val zeroBased = zeroPage?.entries?.isNotEmpty() == true &&
                (onePage == null || onePage.entries.isEmpty() ||
                    zeroPage.entries.map(LiveTvVodItem::id) != onePage.entries.map(LiveTvVodItem::id))
            val initialPageNumbers = if (zeroBased) listOf(0, 1, 2) else listOf(1, 2)
            val initialPages = initialPageNumbers.mapNotNull { number ->
                pageResults[number]?.getOrNull()?.let { number to it }
            }.toMap()
            var pageNumber = if (zeroBased) 0 else 1
            var page = initialPages[pageNumber]
                ?: throw pageResults[pageNumber]?.exceptionOrNull()
                ?: java.io.IOException("Stalker $type page $pageNumber could not be loaded")
            suspend fun advancePage() {
                pageNumber++
                if (pageNumber <= MAX_PAGES) {
                    page = initialPages[pageNumber]
                        ?: pageResults[pageNumber]?.getOrThrow()
                        ?: readPage(pageNumber)
                }
            }
            Log.i("LiveTvStalker", "VOD $type pagination starts at p=$pageNumber")

            while (pageNumber <= MAX_PAGES) {
                pagesRead++
                receivedEntries += page.receivedEntries
                skippedEntries += page.skippedEntries
                page.totalItems?.takeIf { it >= 0 }?.let { reportedTotal = it }
                if (page.entries.isEmpty()) {
                    val expected = reportedTotal
                    if (expected != null && result.size < expected) {
                        val message = "Stalker $type catalogue ended at ${result.size} of $expected items"
                        Log.w("LiveTvStalker", message)
                        throw java.io.IOException(message)
                    }
                    break
                }
                val fresh = page.entries.filter { seenIds.add(it.id) }
                if (fresh.isEmpty()) {
                    noProgressPages++
                    Log.w("LiveTvStalker", "VOD $type pagination repeated items at page $pageNumber")
                    if (noProgressPages < 2) {
                        if (pagesRead == MAX_PAGES) {
                            throw java.io.IOException("Stalker $type catalogue exceeded $MAX_PAGES pages")
                        }
                        advancePage()
                        continue
                    }
                    throw java.io.IOException("Stalker $type pagination repeated pages after ${result.size} items")
                }
                noProgressPages = 0
                result += fresh
                val notYetEmitted = fresh.filter { emittedProbeIds.add(it.id) }
                if (notYetEmitted.isNotEmpty()) onBatch(notYetEmitted)
                val expected = reportedTotal
                if (!reportedTotalExceeded && expected != null && result.size > expected) {
                    reportedTotalExceeded = true
                    Log.w("LiveTvStalker", "VOD $type returned ${result.size} items beyond portal total $expected; continuing")
                }
                if (pagesRead == MAX_PAGES) {
                    throw java.io.IOException("Stalker $type catalogue exceeded $MAX_PAGES pages")
                }
                advancePage()
            }
            Log.i(
                "LiveTvStalker",
                "VOD $type parsed=${result.size} received=$receivedEntries skipped=$skippedEntries " +
                    "pages=$pagesRead reportedTotal=${reportedTotal ?: -1} categoryScoped=${categoryId.isNotBlank()}"
            )
            result
        }
    }

    suspend fun seriesEpisodes(source: LiveTvSource, series: LiveTvVodItem): List<LiveTvVodItem> =
        withSession(source.stalker.normalized()) { session ->
            fun data(response: String): org.json.JSONArray {
                fun findArray(value: Any?, depth: Int = 0): org.json.JSONArray? {
                    if (depth > 6) return null
                    return when (value) {
                        is org.json.JSONArray -> value
                        is JSONObject -> {
                            val arrays = listOf("data", "episodes", "series", "results", "items", "js")
                                .mapNotNull { key ->
                                    value.opt(key)?.takeUnless { it === JSONObject.NULL }?.let { findArray(it, depth + 1) }
                                }
                            arrays.firstOrNull { it.length() > 0 } ?: arrays.firstOrNull()
                        }
                        is String -> {
                            val text = value.trim()
                            if (text.startsWith("[")) runCatching { org.json.JSONArray(text) }.getOrNull()
                            else if (text.startsWith("{")) runCatching { JSONObject(text) }.getOrNull()?.let { findArray(it, depth + 1) }
                            else null
                        }
                        else -> null
                    }
                }
                return findArray(runCatching { JSONObject(response) }.getOrNull()) ?: org.json.JSONArray()
            }
            suspend fun ordered(parameters: Map<String, String>): org.json.JSONArray {
                val entries = org.json.JSONArray()
                val seen = HashSet<String>()
                for (page in 1..MAX_PAGES) {
                    val batch = data(request(
                        session.settings,
                        session.token,
                        "series",
                        "get_ordered_list",
                        parameters + ("p" to page.toString()),
                    ))
                    var fresh = 0
                    for (index in 0 until batch.length()) {
                        val value = batch.opt(index) ?: continue
                        val key = when (value) {
                            is JSONObject -> value.optString("id")
                                .ifBlank { value.optString("season_id") }
                                .ifBlank { value.optString("episode_id") }
                                .ifBlank { value.toString() }
                            else -> value.toString()
                        }
                        if (seen.add(key)) {
                            entries.put(value)
                            fresh++
                        }
                    }
                    if (batch.length() == 0 || fresh == 0) break
                }
                return entries
            }
            // Match Jojo Home's portal contract: Stalker ignores/filters child lists when the
            // catalogue-only `category=*` parameter is sent with a season or episode request.
            val seasons = ordered(mapOf("movie_id" to series.providerId, "season_id" to "0", "episode_id" to "0"))
            val result = ArrayList<LiveTvVodItem>()

            fun appendEpisode(entry: JSONObject, seasonNumber: Int, index: Int, fallbackCommand: String = "") {
                val episodeId = entry.optString("id").takeIf(String::isNotBlank)
                    ?: entry.optString("episode_id").takeIf(String::isNotBlank)
                    ?: return
                val number = entry.optInt(
                    "series_number",
                    entry.optInt("episode_num", entry.optInt("episode_number", index + 1)),
                ).coerceAtLeast(1)
                result += LiveTvVodItem(
                    id = "${source.id}:episode:${series.providerId}:$episodeId",
                    sourceId = source.id,
                    providerId = episodeId,
                    title = entry.optString("name").ifBlank {
                        entry.optString("title").ifBlank { "S${seasonNumber}E$number" }
                    },
                    type = "episode",
                    posterUrl = resolveVodPoster(
                        source,
                        entry.optString("screenshot_uri")
                            .ifBlank { entry.optString("cover") }
                            .ifBlank { series.posterUrl },
                    ),
                    season = seasonNumber,
                    episode = number,
                    command = entry.optString("cmd").ifBlank {
                        entry.optString("command").ifBlank { fallbackCommand }
                    },
                    seriesId = "${series.sourceId}:series:${series.providerId}",
                    seriesTitle = series.title,
                )
            }

            fun appendNumberedEpisode(season: JSONObject, seasonId: String, seasonNumber: Int, number: Int) {
                if (seasonId.isBlank() || number <= 0) return
                result += LiveTvVodItem(
                    id = "${source.id}:episode:${series.providerId}:$seasonId:$number",
                    sourceId = source.id,
                    providerId = seasonId,
                    title = "${series.title} · S$seasonNumber E$number",
                    type = "episode",
                    posterUrl = resolveVodPoster(
                        source,
                        season.optString("screenshot_uri").ifBlank {
                            season.optString("cover").ifBlank { series.posterUrl }
                        },
                    ),
                    season = seasonNumber,
                    episode = number,
                    command = season.optString("cmd").ifBlank { season.optString("command") }
                        .ifBlank { "/media/file_$seasonId.mpg" },
                    seriesId = "${series.sourceId}:series:${series.providerId}",
                    seriesTitle = series.title,
                )
            }

            for (index in 0 until seasons.length()) {
                val season = seasons.optJSONObject(index) ?: continue
                val seasonId = season.optString("id").ifBlank { season.optString("season_id") }
                val seasonNumber = season.optInt("season_number", season.optInt("season", index + 1)).coerceAtLeast(1)
                val beforeSeason = result.size
                val embedded = season.optJSONArray("series") ?: season.optJSONArray("episodes")
                if (embedded != null && embedded.length() > 0) {
                    for (episodeIndex in 0 until embedded.length()) {
                        when (val value = embedded.opt(episodeIndex)) {
                            is JSONObject -> appendEpisode(value, seasonNumber, episodeIndex, season.optString("cmd"))
                            is Number -> appendNumberedEpisode(season, seasonId, seasonNumber, value.toInt())
                            is String -> value.toIntOrNull()?.let {
                                appendNumberedEpisode(season, seasonId, seasonNumber, it)
                            }
                        }
                    }
                } else if (season.has("series_number") || season.has("episode_num") || season.has("episode_number")) {
                    // Some Stalker portals return episodes directly instead of wrapping them in seasons.
                    appendEpisode(season, seasonNumber, index)
                }
                if (result.size == beforeSeason && seasonId.isNotBlank()) {
                    // Providers may return only season numbers here, or season objects whose
                    // embedded entries omit episode IDs. Fetch that season's actual episode list.
                    val episodes = ordered(mapOf("movie_id" to series.providerId, "season_id" to seasonId, "episode_id" to "0"))
                    for (episodeIndex in 0 until episodes.length()) {
                        val entry = episodes.optJSONObject(episodeIndex) ?: continue
                        appendEpisode(
                            entry,
                            seasonNumber,
                            episodeIndex,
                            season.optString("cmd").ifBlank { "/media/file_$seasonId.mpg" },
                        )
                    }
                }
            }
            result.sortedWith(compareBy(LiveTvVodItem::season, LiveTvVodItem::episode))
        }

    suspend fun resolveVod(source: LiveTvSource, item: LiveTvVodItem): LiveTvVodPlayback =
        withSession(source.stalker.normalized()) { session ->
            val parameters = mapOf("cmd" to item.command, "forced_storage" to "undefined", "disable_ad" to "0", "download" to "0") +
                if (item.type == "episode") mapOf("series" to item.episode.toString()) else emptyMap()
            val result = JSONObject(request(session.settings, session.token, "vod", "create_link", parameters))
                .let { it.optJSONObject("js") ?: it }
            val command = result.optString("cmd").ifBlank { result.optString("url") }
            val streamUrl = Regex("https?://[^\\s]+", RegexOption.IGNORE_CASE).find(command)?.value
                ?: throw LiveTvException(LiveTvError.StalkerFailed)
            LiveTvVodPlayback(streamUrl, playbackHeaders(session))
        }

    /** The portal's channels; [StalkerChannels.incomplete] when some pages still failed after a retry. */
    suspend fun channels(settings: LiveTvStalkerSettings): StalkerChannels = withSession(settings) { session ->
        val genres = genres(session)
        var index = 0
        val toChannel: (Map<String, String>) -> LiveTvChannel? = { fields -> fields.toChannel(session, genres, index++) }
        // One request where the portal supports it, otherwise every page of the ordered list.
        val allPage = runCatching {
            withStalkerRetry("all channel list") {
                LiveTvHttp.stream(
                    url(session.settings, session.token, "itv", "get_all_channels"),
                    baseHeaders(session.settings) + tokenHeader(session.token),
                    httpMethod = session.settings.requestMethod,
                    proxyUrl = session.settings.httpProxy,
                ) { input -> readStalkerPage(input, toChannel) }
            }
        }
            .getOrElse { if (it is CancellationException) throw it else null }
        val all = allPage?.entries.orEmpty()
        val fastPathMayBeIncomplete = if (all.isEmpty()) {
            false
        } else {
            val total = allPage?.totalItems?.takeIf { it >= 0 }
            val pageSize = allPage?.maxPageItems?.takeIf { it > 0 }
            when {
                total != null && all.size < total -> true
                total != null && pageSize != null -> runCatching {
                    hasChannelsAfterReportedEnd(session, total, pageSize)
                }.getOrElse { failure ->
                    if (failure is CancellationException) throw failure
                    Log.w("LiveTv", "Stalker channel completeness check failed", failure)
                    false
                }
                else -> false
            }
        }
        val seen = HashSet<String>()
        val result = when {
            all.isEmpty() -> orderedPages(session, toChannel)
            !fastPathMayBeIncomplete -> StalkerChannels(all, incomplete = false)
            else -> {
                Log.w("LiveTv", "Stalker get_all_channels may be incomplete; merging ordered pages")
                val paged = orderedPages(session, toChannel)
                paged.copy(channels = ArrayList(all + paged.channels))
            }
        }
        result.copy(
            channels = ArrayList(result.channels.filter { seen.add(stalkerChannelKey(it)) }),
            groupOrder = genres.values.map(String::trim).distinct(),
        )
    }

    /**
     * The portal's own guide for the next [hours] hours, saved to [target] as a small gzipped
     * XMLTV file so it is read like any other guide. [channels] maps the portal's channel ids to
     * the ids the list keeps their guide under. Portals send it as one JSON answer, which is read
     * one programme at a time.
     */
    suspend fun downloadGuide(settings: LiveTvStalkerSettings, channels: Map<String, String>, hours: Int, target: java.io.File) {
        withSession(settings.normalized()) { session ->
            val programmes = LiveTvHttp.stream(
                url(session.settings, session.token, "itv", "get_epg_info", mapOf("period" to hours.toString())),
                baseHeaders(session.settings) + tokenHeader(session.token),
                LiveTvHttp.GUIDE_READ_TIMEOUT_S,
                httpMethod = session.settings.requestMethod,
                proxyUrl = session.settings.httpProxy,
            ) { input ->
                var count = 0
                LiveTvHttp.writeGzip(target) { out ->
                    java.io.BufferedWriter(java.io.OutputStreamWriter(out, Charsets.UTF_8), 64 * 1024).let { writer ->
                        count = writeStalkerGuide(input, channels, writer)
                        writer.flush()
                    }
                    // An empty answer keeps the guide saved before.
                    count > 0
                }
                count
            }
            // An expired session answers with no programmes: withSession renews it and asks once more.
            if (programmes == 0) throw java.io.IOException("Portal sent no guide")
        }
    }

    /** A playable link for a list entry: Stalker links are created per play and expire. */
    suspend fun resolve(settings: LiveTvStalkerSettings, channel: LiveTvChannel): LiveTvChannel {
        val command = channel.stalkerCommand ?: return channel
        return withSession(settings.normalized()) { session ->
            val js = JSONObject(request(session.settings, session.token, "itv", "create_link", mapOf("cmd" to command)))
                .let { it.optJSONObject("js") ?: it }
            // An expired session answers without a link: the failure renews it once (withSession).
            val link = (js.optNonBlank("cmd") ?: js.optNonBlank("url") ?: js.optNonBlank("stream_url"))
                ?.toStalkerPlayableUrl()?.takeIf(String::isNotBlank)
                ?: throw IllegalStateException("no link")
            channel.copy(streamUrl = link, headers = channel.headers + playbackHeaders(session))
        }
    }

    /**
     * Runs [block] with a portal session. Portals expire sessions without notice, so a failure
     * with a cached session is retried once after a fresh handshake.
     */
    private suspend fun <T> withSession(settingsInput: LiveTvStalkerSettings, block: suspend (StalkerSession) -> T): T {
        val settings = settingsInput.normalized()
        val cached = sessions[settings]
        if (cached != null) {
            try {
                return block(cached)
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (_: Exception) {
                sessions.remove(settings, cached)
            }
        }
        val session = handshake(settings)
        sessions[settings] = session
        return block(session)
    }

    private suspend fun handshake(settings: LiveTvStalkerSettings): StalkerSession {
        var lastFailure: Exception? = null
        val endpoints = stalkerEndpointCandidates(settings)
        for ((index, endpoint) in endpoints.withIndex()) {
            val endpointSettings = settings.copy(portalUrl = endpoint)

            // IPTVnator first probes the lightweight genre action. Some Stalker-compatible
            // panels expose content without a MAG session; do not force a token on those.
            try {
                val probe = request(endpointSettings, null, "itv", "get_genres")
                if (isStalkerListResponse(probe)) {
                    Log.i("LiveTvStalker", "Using token-free Stalker endpoint candidate ${index + 1}")
                    return StalkerSession(endpointSettings, null)
                }
            } catch (failure: Exception) {
                if (failure is CancellationException) throw failure
                lastFailure = failure
            }

            try {
                val device = profileParameters(endpointSettings)
                val handshakeResponse = JSONObject(request(endpointSettings, null, "stb", "handshake", device))
                val handshakeJs = handshakeResponse.optJSONObject("js") ?: handshakeResponse
                val token = handshakeJs.optNonBlank("token")
                    ?: throw LiveTvException(LiveTvError.StalkerToken)

                // A full portal may require an account login after issuing its MAG token.
                val profileResponse = try {
                    request(endpointSettings, token, "stb", "get_profile", device)
                } catch (failure: Exception) {
                    if (failure is CancellationException) throw failure
                    null
                }
                val profile = profileResponse?.let(::stalkerResponseJs)
                val status = profile?.optString("status").orEmpty()
                if (status == "2") {
                    if (endpointSettings.username.isBlank() || endpointSettings.password.isBlank()) {
                        throw java.io.IOException(
                            profile?.optNonBlank("msg")
                                ?: profile?.optNonBlank("block_msg")
                                ?: "Stalker portal requires a login and password",
                        )
                    }
                    val authResponse = JSONObject(
                        request(
                            endpointSettings,
                            token,
                            "stb",
                            "do_auth",
                            device + mapOf(
                                "login" to endpointSettings.username,
                                "password" to endpointSettings.password,
                            ),
                        ),
                    )
                    val authResult = authResponse.opt("js")
                    if (authResult != true && authResult?.toString() != "true") {
                        throw java.io.IOException("Stalker portal rejected the account login")
                    }
                    val secondProfile = JSONObject(
                        request(
                            endpointSettings,
                            token,
                            "stb",
                            "get_profile",
                            device + mapOf("auth_second_step" to "1"),
                        ),
                    )
                    val secondProfileJs = secondProfile.optJSONObject("js") ?: secondProfile
                    if (secondProfileJs.optString("status") == "1" || secondProfileJs.optString("status") == "2") {
                        throw java.io.IOException(
                            secondProfileJs.optNonBlank("msg")
                                ?: secondProfileJs.optNonBlank("block_msg")
                                ?: "Stalker portal did not confirm the account session",
                        )
                    }
                } else if (status == "1") {
                    throw java.io.IOException(
                        profile?.optNonBlank("msg")
                            ?: profile?.optNonBlank("block_msg")
                            ?: "Stalker portal refused the device session",
                    )
                }

                val probe = request(endpointSettings, token, "itv", "get_genres")
                if (!isStalkerListResponse(probe)) {
                    throw java.io.IOException("Stalker endpoint did not return a genre list")
                }
                Log.i("LiveTvStalker", "Using authenticated Stalker endpoint candidate ${index + 1}")
                return StalkerSession(endpointSettings, token)
            } catch (failure: Exception) {
                if (failure is CancellationException) throw failure
                lastFailure = failure
                Log.w("LiveTvStalker", "Stalker endpoint candidate ${index + 1} was not usable", failure)
            }
        }
        throw lastFailure ?: LiveTvException(LiveTvError.StalkerToken)
    }

    private fun stalkerResponseJs(response: String): JSONObject? =
        runCatching { JSONObject(response).optJSONObject("js") ?: JSONObject(response) }.getOrNull()

    private fun isStalkerListResponse(response: String): Boolean {
        val root = runCatching { JSONObject(response) }.getOrNull()
            ?: return runCatching { JSONArray(response) }.isSuccess
        val js = root.opt("js")
        if (js is JSONArray) return true
        if (js is JSONObject) return !js.has("error") && js.opt("data") is JSONArray
        return !root.has("error") && root.opt("data") is JSONArray
    }

    private suspend fun genres(session: StalkerSession): Map<String, String> =
        withStalkerRetry("channel genres") {
            dataObjects(session, "itv", "get_genres") { fields ->
                val id = fields.firstValue("id", "genre_id", "tv_genre_id", "category_id", "group_id", "alias")
                val title = fields.firstCategoryName("title", "name", "genre_name", "genre_title", "tv_genre_name", "tv_genre_title", "genre", "category_name", "category_title", "group_name", "group")
                if (id == null || title == null) null else id to title
            }.toMap()
        }

    private suspend fun hasChannelsAfterReportedEnd(
        session: StalkerSession,
        totalItems: Int,
        pageSize: Int,
    ): Boolean {
        val reportedPages = ((totalItems.toLong() + pageSize - 1L) / pageSize)
            .coerceAtLeast(1L)
            .coerceAtMost(MAX_PAGES.toLong())
        if (reportedPages >= MAX_PAGES) return false
        val pageNumber = reportedPages + 1L
        return withStalkerRetry("channel tail page $pageNumber") {
            LiveTvHttp.stream(
                url(session.settings, session.token, "itv", "get_ordered_list", mapOf("p" to pageNumber.toString())),
                baseHeaders(session.settings) + tokenHeader(session.token),
                httpMethod = session.settings.requestMethod,
                proxyUrl = session.settings.httpProxy,
            ) { input -> readStalkerPage(input) { Unit }.entries.isNotEmpty() }
        }
    }

    private suspend fun orderedPages(
        session: StalkerSession,
        toChannel: (Map<String, String>) -> LiveTvChannel?,
    ): StalkerChannels {
        // Pages load a few at a time, so the mapper is shared across threads.
        val mapper: (Map<String, String>) -> LiveTvChannel? = { synchronized(this) { toChannel(it) } }
        suspend fun page(number: Int): StalkerPage<LiveTvChannel> =
            withStalkerRetry("channel page $number") {
                LiveTvHttp.stream(
                    url(session.settings, session.token, "itv", "get_ordered_list", mapOf("p" to number.toString())),
                    baseHeaders(session.settings) + tokenHeader(session.token),
                    httpMethod = session.settings.requestMethod,
                    proxyUrl = session.settings.httpProxy,
                ) { input -> readStalkerPage(input, mapper) }
            }

        val first = page(1)
        if (first.entries.isEmpty()) return StalkerChannels(emptyList(), incomplete = false)
        var failedPages = 0
        val entries = ArrayList(first.entries)
        val perPage = first.maxPageItems?.takeIf { it > 0 } ?: first.entries.size
        val total = first.totalItems?.takeIf { it >= 0 }
        var lastReportedPage = 1
        if (total != null && perPage > 0) {
            val totalPages = ((total.toLong() + perPage - 1L) / perPage).coerceAtLeast(1L)
            val lastPage = totalPages.coerceAtMost(MAX_PAGES.toLong()).toInt()
            lastReportedPage = lastPage
            if (totalPages > MAX_PAGES.toLong()) {
                failedPages++
                Log.w("LiveTv", "Stalker channel catalogue exceeds $MAX_PAGES pages")
            }
            (2..lastPage).chunked(PARALLEL_PAGES).forEach { numbers ->
                coroutineScope {
                    numbers.map { number ->
                        async {
                            runCatching { page(number).entries }.getOrElse { failure ->
                                if (failure is CancellationException) throw failure
                                synchronized(entries) { failedPages++ }
                                Log.w("LiveTv", "Stalker channel page $number failed after retry", failure)
                                emptyList()
                            }
                        }
                    }.awaitAll()
                }.forEach(entries::addAll)
            }
        }

        // Verify past the portal's count: some Stalker panels under-report total_items. Also page
        // until empty when the portal omits its total, while stopping if it repeats a page.
        val seenChannels = entries.mapTo(HashSet<String>(), ::stalkerChannelKey)
        var pageNumber = lastReportedPage + 1
        var consecutiveFailedPages = 0
        var noProgressPages = 0
        while (pageNumber <= MAX_PAGES) {
            val data = try {
                page(pageNumber).entries
            } catch (failure: Exception) {
                if (failure is CancellationException) throw failure
                failedPages++
                consecutiveFailedPages++
                Log.w("LiveTv", "Stalker channel page $pageNumber failed after retry", failure)
                if (consecutiveFailedPages >= 3) break
                pageNumber++
                continue
            }
            consecutiveFailedPages = 0
            if (data.isEmpty()) {
                if (total != null && entries.size < total) {
                    failedPages++
                    Log.w("LiveTv", "Stalker channel catalogue ended at ${entries.size} of $total items")
                }
                break
            }
            val fresh = data.filter { seenChannels.add(stalkerChannelKey(it)) }
            if (fresh.isEmpty()) {
                noProgressPages++
                if (noProgressPages < 2) {
                    pageNumber++
                    continue
                }
                failedPages++
                Log.w("LiveTv", "Stalker channel pagination repeated pages after ${entries.size} items")
                break
            }
            noProgressPages = 0
            entries += fresh
            if (total != null && entries.size > total) {
                Log.w("LiveTv", "Stalker returned ${entries.size} channels beyond portal total $total; continuing")
            }
            if (pageNumber == MAX_PAGES) {
                failedPages++
                Log.w("LiveTv", "Stalker channel catalogue reached the $MAX_PAGES-page limit")
                break
            }
            pageNumber++
        }
        if (failedPages > 0) Log.w("LiveTv", "Stalker: $failedPages pages could not be loaded")
        return StalkerChannels(entries, incomplete = failedPages > 0)
    }

    private fun Map<String, String>.toChannel(
        session: StalkerSession,
        genres: Map<String, String>,
        index: Int,
    ): LiveTvChannel? {
        val name = this["name"] ?: this["title"] ?: return null
        val command = this["cmd"] ?: this["mc_cmd"] ?: this["url"] ?: return null
        val streamUrl = command.toStalkerPlayableUrl().takeIf(String::isNotBlank) ?: return null
        return LiveTvChannel(
            id = this["id"] ?: "stalker-$index-${streamUrl.hashCode()}",
            name = name,
            streamUrl = streamUrl,
            tvgId = this["xmltv_id"] ?: this["tvg_id"],
            logoUrl = (this["logo"] ?: this["logo_url"])?.let { session.settings.stalkerLogoUrl(it) },
            group = resolveCategoryName(genres, "tv_genre_id", "genre_id", "category_id", "group_id"),
            headers = playbackHeaders(session),
            stalkerCommand = command,
        )
    }

    /** The `data` array of a `{js:{data:[...]}}` answer, each entry mapped from its plain fields. */
    private suspend fun <T : Any> dataObjects(
        session: StalkerSession,
        type: String,
        action: String,
        failOnUnmappedEntries: Boolean = false,
        map: (Map<String, String>) -> T?,
    ): List<T> =
        LiveTvHttp.stream(
            url(session.settings, session.token, type, action),
            baseHeaders(session.settings) + tokenHeader(session.token),
            httpMethod = session.settings.requestMethod,
            proxyUrl = session.settings.httpProxy,
        ) { input ->
            val page = readStalkerPage(input, map)
            if (failOnUnmappedEntries && (
                    page.skippedEntries > 0 ||
                        (page.entries.isEmpty() && (page.totalItems ?: 0) > 0)
                )
            ) {
                Log.w(
                    "LiveTvStalker",
                    "Rejected category response type=$type action=$action parsed=${page.entries.size} " +
                        "received=${page.receivedEntries} skipped=${page.skippedEntries} total=${page.totalItems}",
                )
                throw java.io.IOException(
                    "Stalker $type/$action returned ${page.totalItems ?: page.receivedEntries} category entries but parsed ${page.entries.size}",
                )
            }
            page.entries
        }

    private suspend fun request(
        settings: LiveTvStalkerSettings,
        token: String?,
        type: String,
        action: String,
        extra: Map<String, String> = emptyMap(),
    ): String = LiveTvHttp.text(
        url(settings, token, type, action, extra),
        baseHeaders(settings) + tokenHeader(token),
        httpMethod = settings.requestMethod,
        proxyUrl = settings.httpProxy,
    )

    private fun url(
        settings: LiveTvStalkerSettings,
        token: String?,
        type: String,
        action: String,
        extra: Map<String, String> = emptyMap(),
    ): String {
        val parameters = buildMap {
            put("type", type)
            put("action", action)
            put("JsHttpRequest", "1-xml")
            if (!token.isNullOrBlank()) put("token", token)
            putAll(extra)
        }
        val endpoint = settings.portalEndpoint()
        return endpoint + parameters.entries.joinToString("&", prefix = if ('?' in endpoint) "&" else "?") { (key, value) ->
            "${key.urlEncoded()}=${value.urlEncoded()}"
        }
    }

    private fun playbackHeaders(session: StalkerSession): Map<String, String> =
        session.playbackHeaders ?: (baseHeaders(session.settings) + tokenHeader(session.token) +
            session.settings.playerUserAgent.takeIf(String::isNotBlank)?.let { mapOf("User-Agent" to it) }.orEmpty())
            .also { session.playbackHeaders = it }

    private fun baseHeaders(settings: LiveTvStalkerSettings): Map<String, String> {
        val model = settings.deviceModel.ifBlank { "MAG254" }
        val timezone = settings.timezone.ifBlank { java.util.TimeZone.getDefault().id }
        val headers = linkedMapOf(
            "User-Agent" to settings.stbUserAgent.ifBlank { DEFAULT_STALKER_STB_USER_AGENT },
            "X-User-Agent" to "Model: $model; Link: Ethernet",
            "Referer" to settings.stalkerPortalReferer(),
            "Cookie" to "mac=${settings.macAddress}; stb_lang=en; timezone=${timezone.urlEncoded()}",
        )
        val custom = parseStalkerHeaders(settings.httpHeaders)
        val extraCookies = listOfNotNull(
            settings.httpCookie.takeIf(String::isNotBlank),
            custom.entries.firstOrNull { it.key.equals("Cookie", ignoreCase = true) }?.value,
        ).joinToString("; ")
        custom.keys.filter { it.equals("Cookie", ignoreCase = true) }.forEach { custom.remove(it) }
        headers.putAll(custom)
        if (extraCookies.isNotBlank()) headers["Cookie"] = headers.getValue("Cookie") + "; " + extraCookies
        return headers
    }

    private fun profileParameters(settings: LiveTvStalkerSettings): Map<String, String> = buildMap {
        settings.serialNumber.takeIf(String::isNotBlank)?.let { put("sn", it) }
        settings.deviceId.takeIf(String::isNotBlank)?.let { put("device_id", it) }
        settings.deviceId2.takeIf(String::isNotBlank)?.let { put("device_id2", it) }
        settings.deviceVersion.takeIf(String::isNotBlank)?.let { put("ver", it) }
        settings.deviceModel.takeIf(String::isNotBlank)?.let { put("stb_type", it) }
        settings.timezone.takeIf(String::isNotBlank)?.let { put("timezone", it) }
    }

    private fun parseStalkerHeaders(raw: String): MutableMap<String, String> = buildMap {
        raw.split(',', '\n').forEach { part ->
            val separator = part.indexOf(':')
            if (separator <= 0) return@forEach
            val name = part.substring(0, separator).trim().trim('"', '\'')
            val value = part.substring(separator + 1).trim().trim('"', '\'')
            if (name.isNotBlank() && value.isNotBlank() && name.none { it.code < 0x20 || it.code == 0x7f }) put(name, value)
        }
    }.toMutableMap()

    private const val DEFAULT_STALKER_STB_USER_AGENT =
        "Mozilla/5.0 (QtEmbedded; U; Linux; MAG254; en) AppleWebKit/533.3 (KHTML, like Gecko) MAG200 stbapp ver: 4 rev: 2721 Mobile Safari/533.3"

    private fun tokenHeader(token: String?): Map<String, String> =
        if (token.isNullOrBlank()) emptyMap() else mapOf("Authorization" to "Bearer $token")

    private fun LiveTvStalkerSettings.portalEndpoint(): String {
        val normalized = portalUrl.trim().substringBefore('?').substringBefore('#').trimEnd('/')
        return when {
            normalized.endsWith("portal.php", ignoreCase = true) -> normalized
            normalized.endsWith("/server/load.php", ignoreCase = true) -> normalized
            normalized.endsWith("/c", ignoreCase = true) && normalized.contains("/stalker_portal", ignoreCase = true) ->
                normalized.dropLast(2) + "/server/load.php"
            normalized.endsWith("/c", ignoreCase = true) -> normalized.dropLast(2) + "/portal.php"
            else -> "$normalized/portal.php"
        }
    }

    private fun LiveTvStalkerSettings.stalkerPortalReferer(): String {
        val normalized = portalUrl.trim().substringBefore('?').substringBefore('#').trimEnd('/')
        val base = when {
            normalized.endsWith("/server/load.php", ignoreCase = true) -> normalized.removeSuffixIgnoreCase("/server/load.php")
            normalized.endsWith("/portal.php", ignoreCase = true) -> normalized.removeSuffixIgnoreCase("/portal.php")
            normalized.endsWith("/c", ignoreCase = true) -> normalized.dropLast(2)
            else -> normalized
        }
        return "$base/c/"
    }

    /** Probe the endpoint forms used by MAG/Stalker portals instead of assuming portal.php. */
    private fun stalkerEndpointCandidates(settings: LiveTvStalkerSettings): List<String> {
        val uri = runCatching { java.net.URI(settings.portalUrl.trim()) }.getOrNull()
            ?: return listOf(settings.portalEndpoint())
        val scheme = uri.scheme?.takeIf(String::isNotBlank) ?: return listOf(settings.portalEndpoint())
        val authority = uri.rawAuthority?.takeIf(String::isNotBlank) ?: return listOf(settings.portalEndpoint())
        val path = uri.rawPath.orEmpty().trimEnd('/')
        val origin = "$scheme://$authority"
        val candidates = mutableListOf<String>()
        val pathIsPhp = path.substringAfterLast('/').endsWith(".php", ignoreCase = true)
        if (pathIsPhp) candidates += "$origin$path"
        val base = when {
            path.endsWith("/server/load.php", ignoreCase = true) -> path.removeSuffixIgnoreCase("/server/load.php")
            path.endsWith("/portal.php", ignoreCase = true) -> path.removeSuffixIgnoreCase("/portal.php")
            pathIsPhp -> path.substringBeforeLast('/')
            path.endsWith("/c", ignoreCase = true) -> path.dropLast(2)
            else -> path
        }.trimEnd('/')
        fun add(suffix: String) {
            val candidatePath = if (base.isBlank()) "/$suffix" else "$base/$suffix"
            candidates += "$origin$candidatePath"
        }
        add("portal.php")
        add("server/load.php")
        if (!base.endsWith("/stalker_portal", ignoreCase = true)) {
            add("stalker_portal/server/load.php")
        }
        return candidates.distinct().ifEmpty { listOf(settings.portalEndpoint()) }
    }

    private fun String.removeSuffixIgnoreCase(suffix: String): String =
        if (endsWith(suffix, ignoreCase = true)) dropLast(suffix.length) else this

    /**
     * A channel logo as a link. Many portals give only the file name ("1234.png"), served from
     * the portal's own logo folder.
     */
    private fun LiveTvStalkerSettings.stalkerLogoUrl(value: String): String? {
        val logo = value.trim()
        return when {
            logo.isEmpty() -> null
            logo.isHttp() -> logo
            logo.startsWith("//") -> "http:$logo"
            logo.startsWith("/") -> portalOrigin()?.let { it + logo }
            logo.contains("://") -> null
            else -> portalRoot()?.let { "$it/misc/logos/320/$logo" }
        }
    }

    /** `http://host:port` of the portal. */
    private fun LiveTvStalkerSettings.portalOrigin(): String? {
        val url = portalUrl.trim()
        val scheme = url.indexOf("://").takeIf { it > 0 } ?: return null
        val pathStart = url.indexOf('/', scheme + 3)
        return if (pathStart < 0) url.trimEnd('/') else url.substring(0, pathStart)
    }

    /** The portal's folder ("…/stalker_portal"), which holds its logos. */
    private fun LiveTvStalkerSettings.portalRoot(): String? {
        val url = portalUrl.trim().substringBefore('?')
        val marker = url.indexOf("/stalker_portal", ignoreCase = true)
        if (marker >= 0) return url.substring(0, marker + "/stalker_portal".length)
        return portalOrigin()?.let { "$it/stalker_portal" }
    }

    private fun String.toStalkerPlayableUrl(): String =
        trim().removePrefix("ffmpeg ").removePrefix("auto ").substringBefore(' ').trim()

    private fun JSONObject.optNonBlank(name: String): String? =
        if (isNull(name)) null else optString(name).trim().takeIf(String::isNotBlank)
}

/**
 * Copies a Stalker `get_epg_info` answer (`{"js":{"data":{"<ch_id>":[{..},..]}}}`, or `data` as
 * one array of programmes with their `ch_id`) to [out] as XMLTV, keeping the channels in
 * [channels]. Returns how many programmes were written.
 */
private fun writeStalkerGuide(input: InputStream, channels: Map<String, String>, out: java.io.Writer): Int {
    var count = 0
    out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tv>\n")
    JsonReader(InputStreamReader(input, Charsets.UTF_8)).use { reader ->
        reader.isLenient = true
        fun programme(channelId: String?) {
            var chId = channelId
            var title: String? = null
            var description: String? = null
            var start: Long? = null
            var stop: Long? = null
            var startText: String? = null
            var stopText: String? = null
            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    "ch_id" -> reader.nextScalar()?.let { if (chId == null) chId = it }
                    "name" -> title = reader.nextScalar()
                    "descr" -> description = reader.nextScalar()?.trim()?.takeIf(String::isNotEmpty)
                    "start_timestamp" -> start = reader.nextScalar()?.toLongOrNull()
                    "stop_timestamp" -> stop = reader.nextScalar()?.toLongOrNull()
                    "time" -> startText = reader.nextScalar()
                    "time_to" -> stopText = reader.nextScalar()
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
            val guideId = chId?.let(channels::get) ?: return
            // Timestamps are seconds; the text times are in the time zone the portal was sent (the TV's).
            val startSeconds = start ?: startText?.let(::stalkerLocalSeconds) ?: return
            val stopSeconds = stop ?: stopText?.let(::stalkerLocalSeconds) ?: return
            val name = title ?: return
            if (stopSeconds <= startSeconds) return
            out.write("<programme start=\"")
            out.write(xmlTvTime(startSeconds))
            out.write("\" stop=\"")
            out.write(xmlTvTime(stopSeconds))
            out.write("\" channel=\"")
            out.write(xmlEscaped(guideId))
            out.write("\"><title>")
            out.write(xmlEscaped(name))
            out.write("</title>")
            description?.let {
                out.write("<desc>")
                out.write(xmlEscaped(it))
                out.write("</desc>")
            }
            out.write("</programme>\n")
            count++
        }
        fun programmes(channelId: String?) {
            reader.beginArray()
            while (reader.hasNext()) {
                if (reader.peek() == JsonToken.BEGIN_OBJECT) programme(channelId) else reader.skipValue()
            }
            reader.endArray()
        }
        fun data() {
            when (reader.peek()) {
                JsonToken.BEGIN_OBJECT -> {
                    reader.beginObject()
                    while (reader.hasNext()) {
                        val channelId = reader.nextName()
                        if (channelId in channels && reader.peek() == JsonToken.BEGIN_ARRAY) programmes(channelId) else reader.skipValue()
                    }
                    reader.endObject()
                }
                JsonToken.BEGIN_ARRAY -> programmes(null)
                else -> reader.skipValue()
            }
        }
        fun body() {
            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    "js" -> if (reader.peek() == JsonToken.BEGIN_OBJECT) body() else reader.skipValue()
                    "data" -> data()
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
        }
        if (reader.peek() == JsonToken.BEGIN_OBJECT) body()
    }
    out.write("</tv>\n")
    return count
}

/** `2026-09-29 21:00:00` in the TV's time zone, as epoch seconds. */
private fun stalkerLocalSeconds(text: String): Long? = runCatching {
    java.time.LocalDateTime.parse(text.trim().replace(' ', 'T'))
        .atZone(java.time.ZoneId.systemDefault()).toEpochSecond()
}.getOrNull()

/** Epoch seconds as XMLTV `yyyyMMddHHmmss +0000`. */
private fun xmlTvTime(epochSeconds: Long): String {
    val time = java.time.LocalDateTime.ofEpochSecond(epochSeconds, 0, java.time.ZoneOffset.UTC)
    return buildString(20) {
        append(time.year.toString().padStart(4, '0'))
        fun two(value: Int) { if (value < 10) append('0'); append(value) }
        two(time.monthValue); two(time.dayOfMonth); two(time.hour); two(time.minute); two(time.second)
        append(" +0000")
    }
}

/** Text safe inside XML: markup characters escaped, characters XML does not allow left out. */
private fun xmlEscaped(text: String): String {
    if (text.none { it == '&' || it == '<' || it == '>' || it == '"' || it < ' ' }) return text
    return buildString(text.length + 16) {
        text.forEach { char ->
            when {
                char == '&' -> append("&amp;")
                char == '<' -> append("&lt;")
                char == '>' -> append("&gt;")
                char == '"' -> append("&quot;")
                char < ' ' && char != '\t' && char != '\n' && char != '\r' -> Unit
                else -> append(char)
            }
        }
    }
}

private class StalkerPage<T>(
    val entries: List<T>,
    val totalItems: Int?,
    val maxPageItems: Int?,
    val receivedEntries: Int = entries.size,
    val skippedEntries: Int = 0,
)

/** Streams Stalker answers with either a `js` array or a `js` object containing `data`. */
private fun <T : Any> readStalkerPage(input: InputStream, map: (Map<String, String>) -> T?): StalkerPage<T> {
    var entries: List<T> = emptyList()
    var total: Int? = null
    var perPage: Int? = null
    var receivedEntries = 0
    var skippedEntries = 0
    var hasEntryArray = false
    var errorMessage: String? = null
    JsonReader(InputStreamReader(input, Charsets.UTF_8)).use { reader ->
        reader.isLenient = true
        fun readEntries(): List<T> {
            hasEntryArray = true
            return reader.readObjectArray(map = { fields ->
                receivedEntries++
                map(fields).also { if (it == null) skippedEntries++ }
            })
        }
        fun readBody() {
            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName().lowercase()) {
                    "js" -> when (reader.peek()) {
                        JsonToken.BEGIN_OBJECT -> readBody()
                        JsonToken.BEGIN_ARRAY -> entries = readEntries()
                        else -> reader.skipValue()
                    }
                    "data", "categories", "items", "results" -> entries = if (reader.peek() == JsonToken.BEGIN_ARRAY) readEntries() else {
                        reader.skipValue(); emptyList()
                    }
                    "total_items" -> total = reader.nextScalar()?.toIntOrNull()
                    "max_page_items" -> perPage = reader.nextScalar()?.toIntOrNull()
                    "error", "message", "msg" -> errorMessage = reader.nextScalar()?.takeIf(String::isNotBlank) ?: errorMessage
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
        }
        when (reader.peek()) {
            JsonToken.BEGIN_OBJECT -> readBody()
            JsonToken.BEGIN_ARRAY -> entries = readEntries()
            else -> Unit
        }
    }
    if (entries.isEmpty() && !errorMessage.isNullOrBlank()) {
        throw java.io.IOException(errorMessage ?: "Stalker portal returned an error")
    }
    if (!hasEntryArray && total != 0) throw java.io.IOException("Stalker response did not contain a content list")
    return StalkerPage(entries, total, perPage, receivedEntries, skippedEntries)
}

// endregion

// region Streaming JSON helpers

/** Reads common Xtream array responses and their wrappers, mapping each object's plain fields. */
private fun <T : Any> readObjects(
    input: InputStream,
    onBatch: ((List<T>) -> Unit)? = null,
    map: (Map<String, String>) -> T?,
): List<T> =
    JsonReader(InputStreamReader(input, Charsets.UTF_8)).use { reader ->
        reader.isLenient = true
        when (reader.peek()) {
            JsonToken.BEGIN_ARRAY -> reader.readObjectArray(map, onBatch)
            JsonToken.BEGIN_OBJECT -> {
                var result: List<T> = emptyList()
                var hasArrayPayload = false
                var hasErrorStatus = false
                var errorMessage: String? = null
                reader.beginObject()
                while (reader.hasNext()) {
                    when (reader.nextName().lowercase()) {
                        "status" -> {
                            val status = reader.nextScalar().orEmpty()
                            hasErrorStatus = status.equals("error", ignoreCase = true) || status.equals("false", ignoreCase = true)
                        }
                        "error", "message", "msg" -> {
                            reader.nextScalar()?.let { errorMessage = it }
                        }
                        in XTREAM_ARRAY_WRAPPERS -> {
                            if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                                hasArrayPayload = true
                                result = reader.readObjectArray(map, onBatch)
                            } else {
                                reader.skipValue()
                            }
                        }
                        else -> reader.skipValue()
                    }
                }
                reader.endObject()
                if (hasErrorStatus || (!hasArrayPayload && errorMessage != null)) {
                    throw java.io.IOException(errorMessage ?: "Le fournisseur IPTV a refusé la requête VOD.")
                }
                if (!hasArrayPayload) {
                    throw java.io.IOException("Réponse VOD du fournisseur IPTV non reconnue.")
                }
                result
            }
            else -> throw java.io.IOException("Réponse VOD du fournisseur IPTV non reconnue.")
        }
    }

private val XTREAM_ARRAY_WRAPPERS = setOf(
    "data", "categories", "live_categories", "vod_categories", "streams", "live_streams", "vod_streams", "series", "movies", "items", "results", "result"
)

private fun <T : Any> JsonReader.readObjectArray(
    map: (Map<String, String>) -> T?,
    onBatch: ((List<T>) -> Unit)? = null,
): List<T> {
    val result = ArrayList<T>()
    val batch = if (onBatch == null) null else ArrayList<T>(VOD_BATCH_SIZE)
    val fields = HashMap<String, String>(16)
    beginArray()
    while (hasNext()) {
        if (peek() != JsonToken.BEGIN_OBJECT) {
            skipValue()
            continue
        }
        fields.clear()
        beginObject()
        while (hasNext()) {
            val name = nextName().lowercase()
            nextScalar()?.let { fields[name] = it }
        }
        endObject()
        // The map is reused for the next entry: [map] must not keep it.
        map(fields)?.let { item ->
            result.add(item)
            batch?.add(item)
            if (batch?.size == VOD_BATCH_SIZE) {
                onBatch?.invoke(batch.toList())
                batch.clear()
            }
        }
    }
    endArray()
    if (batch?.isNotEmpty() == true) onBatch?.invoke(batch.toList())
    return result
}

private const val VOD_BATCH_SIZE = 100

/** A string, number or boolean as trimmed text; null (and skipped) for blanks, nulls and nested values. */
private fun JsonReader.nextScalar(): String? = when (peek()) {
    JsonToken.STRING, JsonToken.NUMBER -> nextString().trim().takeIf(String::isNotBlank)
    JsonToken.BOOLEAN -> nextBoolean().toString()
    else -> {
        skipValue()
        null
    }
}

// endregion

/** A load failure the screen shows as a translated message. */
enum class LiveTvError {
    InvalidUrl, NoChannels, LoadFailed, FileEmpty, FileNoChannels,
    StalkerRequired, StalkerInvalidUrl, StalkerNoChannels, StalkerFailed, StalkerToken,
    /** The list loaded, but some of the portal's pages did not: a notice, not a failed load. */
    StalkerIncomplete,
    XtreamRequired, XtreamInvalidUrl, XtreamNoChannels, XtreamFailed,
    /** The guide link given with a source is not a web link. */
    GuideInvalidUrl,
}

internal class LiveTvException(val error: LiveTvError) : Exception(error.name)
