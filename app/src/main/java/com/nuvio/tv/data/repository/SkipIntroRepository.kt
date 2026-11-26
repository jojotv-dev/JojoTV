package com.nuvio.tv.data.repository

import android.util.Log
import com.nuvio.tv.BuildConfig
import com.nuvio.tv.core.tmdb.TmdbService
import com.nuvio.tv.data.remote.api.IntroDbApi
import com.nuvio.tv.data.remote.api.IntroDbSegment
import com.nuvio.tv.data.remote.api.IntroDbSegmentsResponse
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException

data class SkipInterval(
    val startTime: Double, // seconds
    val endTime: Double,   // seconds
    val type: String,      // intro/op, recap, outro/ed, movie-credits, post-credits
    val provider: String   // "introdb", "chapters", "heuristic"
)

internal fun IntroDbSegmentsResponse.toSkipIntervals(movie: Boolean): List<SkipInterval> {
    if (!movie) return listOfNotNull(
        intro.toSkipIntervalOrNull("intro"),
        recap.toSkipIntervalOrNull("recap"),
        outro.toSkipIntervalOrNull("outro")
    )
    val credits = outro.toSkipIntervalOrNull("movie-credits")
    val scene = postCredits.toSkipIntervalOrNull("post-credits")
    // Skipping credits must not also skip the post-credits scene.
    val safeCredits = if (credits != null && scene != null &&
        scene.startTime < credits.endTime && scene.endTime > credits.startTime
    ) {
        credits.copy(endTime = scene.startTime).takeIf { it.endTime > it.startTime }
    } else credits
    return listOfNotNull(safeCredits, scene)
}

private fun IntroDbSegment?.toSkipIntervalOrNull(type: String): SkipInterval? {
    if (this == null) return null
    val start = startSec ?: startMs?.let { it / 1000.0 } ?: return null
    val end = endSec ?: endMs?.let { it / 1000.0 } ?: return null
    if (!start.isFinite() || !end.isFinite() || start < 0 || end <= start) return null
    return SkipInterval(startTime = start, endTime = end, type = type, provider = "introdb")
}

@Singleton
class SkipIntroRepository @Inject constructor(
    private val introDbApi: IntroDbApi,
    private val tmdbService: TmdbService
) {
    private val cache = ConcurrentHashMap<String, List<SkipInterval>>()
    private val introDbConfigured = BuildConfig.INTRODB_API_URL.isNotEmpty()
    internal val isConfigured: Boolean get() = introDbConfigured

    suspend fun getMovieSkipIntervals(contentId: String?, videoId: String? = null): List<SkipInterval> {
        if (!introDbConfigured) return emptyList()
        val ids = listOfNotNull(contentId, videoId).distinct()
        val imdbId = ids.firstNotNullOfOrNull { id ->
            id.substringBefore(':').takeIf { it.matches(Regex("tt[0-9]+")) }
        } ?: ids.firstNotNullOfOrNull { id ->
            val parts = id.split(':')
            val value = parts.getOrNull(1) ?: return@firstNotNullOfOrNull null
            when (parts[0]) {
                "tmdb" -> value.toIntOrNull()?.let { tmdbService.tmdbToImdb(it, "movie") }
                else -> null
            }
        } ?: return emptyList()
        val key = "movie:$imdbId"
        cache[key]?.let { return it }
        return fetchFromIntroDb(imdbId, isMovie = true).also { cache[key] = it }
    }

    /**
     * Standard path for IMDB-identified content.
     */
    suspend fun getSkipIntervals(imdbId: String?, season: Int, episode: Int): List<SkipInterval> {
        if (imdbId == null) return emptyList()
        val cacheKey = "$imdbId:$season:$episode"
        cache[cacheKey]?.let { return it }
        val intervals = if (introDbConfigured) fetchFromIntroDb(imdbId, season, episode) else emptyList()
        return intervals.also { cache[cacheKey] = it }
    }

    suspend fun getSkipIntervalsForMal(
        malId: String,
        episode: Int,
        imdbId: String? = null,
        imdbSeason: Int? = null,
        imdbEpisode: Int? = null
    ): List<SkipInterval> {
        val cacheKey = "mal:$malId:$episode"
        cache[cacheKey]?.let { return it }

        val resolvedImdbId = imdbId ?: return emptyList()
        val introDbSeason = imdbSeason ?: return emptyList()
        if (!introDbConfigured) return emptyList()
        return fetchFromIntroDb(resolvedImdbId, introDbSeason, imdbEpisode ?: episode)
            .also { cache[cacheKey] = it }
    }

    suspend fun getSkipIntervalsForKitsu(
        kitsuId: String,
        episode: Int,
        imdbId: String? = null,
        imdbSeason: Int? = null,
        imdbEpisode: Int? = null
    ): List<SkipInterval> {
        val cacheKey = "kitsu:$kitsuId:$episode"
        cache[cacheKey]?.let { return it }

        val resolvedImdbId = imdbId ?: return emptyList()
        val introDbSeason = imdbSeason ?: return emptyList()
        if (!introDbConfigured) return emptyList()
        return fetchFromIntroDb(resolvedImdbId, introDbSeason, imdbEpisode ?: episode)
            .also { cache[cacheKey] = it }
    }

    private suspend fun fetchFromIntroDb(
        imdbId: String,
        season: Int? = null,
        episode: Int? = null,
        isMovie: Boolean = false
    ): List<SkipInterval> {
        return try {
            val response = introDbApi.getSegments(imdbId, season, episode, true.takeIf { isMovie })
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.toSkipIntervals(isMovie)
            } else emptyList()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.d("SkipIntro", "IntroDB: no data for $imdbId S${season}E${episode}")
            emptyList()
        }
    }

}

internal fun segmentCategory(type: String): String? = when (type.lowercase()) {
    "intro", "op", "mixed-op" -> "opening"
    "outro", "ed", "mixed-ed", "credits", "ending" -> "ending"
    "recap" -> "recap"
    else -> null
}
