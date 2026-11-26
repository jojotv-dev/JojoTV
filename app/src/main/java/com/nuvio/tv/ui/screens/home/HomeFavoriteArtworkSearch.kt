package com.nuvio.tv.ui.screens.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.Normalizer
import java.util.Locale

internal enum class HomeFavoriteArtworkSource {
    IPTV_PROVIDER,
    OFFICIAL,
    IPTV_ORG,
    TMDB
}

internal data class HomeFavoriteArtworkCandidate(
    val imageUrl: String,
    val label: String
)

/** Community channel-logo lookup. The provider feed is cached in memory to avoid downloading it per channel. */
internal object IptvOrgLogoSearch {
    private const val LOGOS_URL = "https://iptv-org.github.io/api/logos.json"
    private const val CACHE_MAX_AGE_MS = 6 * 60 * 60 * 1000L

    private val cacheMutex = Mutex()
    private var cachedLogos: List<Logo> = emptyList()
    private var cachedAtMs = 0L

    suspend fun search(channelName: String): List<HomeFavoriteArtworkCandidate> = withContext(Dispatchers.IO) {
        val query = normalizeChannelName(channelName)
        if (query.isBlank()) return@withContext emptyList()

        val logos = cacheMutex.withLock {
            if (cachedLogos.isNotEmpty() && System.currentTimeMillis() - cachedAtMs < CACHE_MAX_AGE_MS) {
                cachedLogos
            } else {
                val updated = downloadLogos()
                cachedLogos = updated
                cachedAtMs = System.currentTimeMillis()
                updated
            }
        }

        logos.mapNotNull { logo ->
            val score = matchScore(query, normalizeChannelName(logo.channel.substringBefore('.')))
                ?: return@mapNotNull null
            score to logo
        }
            .sortedWith(
                compareByDescending<Pair<Int, Logo>> { it.first }
                    .thenByDescending { it.second.inUse }
                    .thenByDescending { "horizontal" in it.second.tags || "landscape" in it.second.tags }
            )
            .distinctBy { it.second.url }
            .take(24)
            .map { (_, logo) ->
                val details = buildList {
                    add(logo.channel + (logo.feed?.let { " · $it" } ?: ""))
                    if (logo.width > 0 && logo.height > 0) add("${logo.width}×${logo.height}")
                    if (logo.tags.isNotEmpty()) add(logo.tags.joinToString(", "))
                }.joinToString(" · ")
                HomeFavoriteArtworkCandidate(logo.url, details)
            }
    }

    private fun downloadLogos(): List<Logo> {
        val connection = (URL(LOGOS_URL).openConnection() as HttpURLConnection).apply {
            connectTimeout = 12_000
            readTimeout = 18_000
            requestMethod = "GET"
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "JojoTV artwork picker")
        }
        return try {
            val responseCode = connection.responseCode
            if (responseCode !in 200..299) error("iptv-org returned HTTP $responseCode")
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val rows = JSONArray(body)
            buildList {
                for (index in 0 until rows.length()) {
                    val row = rows.optJSONObject(index) ?: continue
                    val channel = row.optString("channel").takeIf(String::isNotBlank) ?: continue
                    val imageUrl = row.optString("url").takeIf {
                        it.startsWith("https://", ignoreCase = true) || it.startsWith("http://", ignoreCase = true)
                    } ?: continue
                    val feed = row.optString("feed").takeIf { it.isNotBlank() && it != JSONObject.NULL.toString() }
                    val tagsArray = row.optJSONArray("tags") ?: JSONArray()
                    val tags = buildList {
                        for (tagIndex in 0 until tagsArray.length()) {
                            tagsArray.optString(tagIndex).takeIf(String::isNotBlank)?.let(::add)
                        }
                    }
                    add(
                        Logo(
                            channel = channel,
                            feed = feed,
                            url = imageUrl,
                            inUse = row.optBoolean("in_use", false),
                            width = row.optInt("width", 0),
                            height = row.optInt("height", 0),
                            tags = tags
                        )
                    )
                }
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun matchScore(query: String, candidate: String): Int? = when {
        candidate == query -> 100
        candidate.startsWith(query) || query.startsWith(candidate) -> 80
        candidate.contains(query) || query.contains(candidate) -> 65
        else -> {
            val queryTokens = query.split(Regex("(?=[0-9])|(?<=[0-9])")).filter(String::isNotBlank).toSet()
            val candidateTokens = candidate.split(Regex("(?=[0-9])|(?<=[0-9])")).filter(String::isNotBlank).toSet()
            val overlap = queryTokens.intersect(candidateTokens).size
            if (queryTokens.isNotEmpty() && overlap >= queryTokens.size) 50 else null
        }
    }

    private fun normalizeChannelName(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .replace(Regex("(?i)\\b(?:HD|FHD|UHD|4K|1080P?|720P?)\\b"), " ")
        .lowercase(Locale.ROOT)
        .replace(Regex("[^a-z0-9]+"), "")

    private data class Logo(
        val channel: String,
        val feed: String?,
        val url: String,
        val inUse: Boolean,
        val width: Int,
        val height: Int,
        val tags: List<String>
    )
}
