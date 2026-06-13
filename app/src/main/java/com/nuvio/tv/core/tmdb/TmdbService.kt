package com.nuvio.tv.core.tmdb

import android.util.Log
import com.nuvio.tv.BuildConfig
import com.nuvio.tv.data.remote.api.TmdbApi
import com.nuvio.tv.data.remote.api.TmdbImage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TmdbService"
private val TMDB_API_KEY = BuildConfig.TMDB_API_KEY

/**
 * Service to handle TMDB ID conversions and lookups.
 * Provides caching to avoid redundant API calls.
 */
@Singleton
class TmdbService @Inject constructor(
    private val tmdbApi: TmdbApi
) {
    // Cache: IMDB ID -> TMDB ID
    private val imdbToTmdbCache = ConcurrentHashMap<String, Int>()
    
    // Cache: TMDB ID -> IMDB ID  
    private val tmdbToImdbCache = ConcurrentHashMap<Int, String>()

    private val imdbToTmdbInFlight = ConcurrentHashMap<String, CompletableDeferred<Int?>>()
    private val tmdbToImdbInFlight = ConcurrentHashMap<String, CompletableDeferred<String?>>()
    
    // Mutex for thread-safe cache operations
    private val cacheMutex = Mutex()
    
    /**
     * Convert an IMDB ID to a TMDB ID.
     * 
     * @param imdbId The IMDB ID (e.g., "tt0133093")
     * @param mediaType The media type ("movie" or "series"/"tv")
     * @return The TMDB ID, or null if not found
     */
    suspend fun imdbToTmdb(imdbId: String, mediaType: String): Int? = withContext(Dispatchers.IO) {
        // Validate IMDB ID format
        if (!imdbId.startsWith("tt")) {
            Log.w(TAG, "Invalid IMDB ID format: $imdbId")
            return@withContext null
        }
        
        // Check cache first
        imdbToTmdbCache[imdbId]?.let { cached ->
            Log.d(TAG, "Cache hit: IMDB $imdbId -> TMDB $cached")
            return@withContext cached
        }
        
        val normalizedType = normalizeMediaType(mediaType)
        val requestKey = "$imdbId:$normalizedType"
        val requestDeferred = CompletableDeferred<Int?>()
        imdbToTmdbInFlight.putIfAbsent(requestKey, requestDeferred)?.let { existing ->
            return@withContext existing.await()
        }

        try {
            Log.d(TAG, "Looking up TMDB ID for IMDB: $imdbId (type: $mediaType)")
            
            val response = tmdbApi.findByExternalId(
                externalId = imdbId,
                apiKey = TMDB_API_KEY,
                externalSource = "imdb_id"
            )
            
            if (!response.isSuccessful) {
                Log.e(TAG, "TMDB API error: ${response.code()} - ${response.message()}")
                requestDeferred.complete(null)
                return@withContext null
            }
            
            val body = response.body()
            if (body == null) {
                requestDeferred.complete(null)
                return@withContext null
            }
            
            // Determine which results to use based on media type
            val result = when (normalizedType) {
                "movie" -> body.movieResults?.firstOrNull()
                "tv", "series" -> body.tvResults?.firstOrNull()
                else -> body.movieResults?.firstOrNull() ?: body.tvResults?.firstOrNull()
            }
            
            result?.let { found ->
                Log.d(TAG, "Found TMDB ID: ${found.id} for IMDB: $imdbId")
                
                // Cache both directions
                cacheMutex.withLock {
                    imdbToTmdbCache[imdbId] = found.id
                    tmdbToImdbCache[found.id] = imdbId
                }

                requestDeferred.complete(found.id)
                 
                return@withContext found.id
            }
            
            Log.w(TAG, "No TMDB result found for IMDB: $imdbId")
            requestDeferred.complete(null)
            null
            
        } catch (e: CancellationException) {
            requestDeferred.cancel(e)
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error looking up TMDB ID for $imdbId: ${e.message}", e)
            requestDeferred.complete(null)
            null
        } finally {
            imdbToTmdbInFlight.remove(requestKey, requestDeferred)
        }
    }
    
    /**
     * Convert a TMDB ID to an IMDB ID.
     * 
     * @param tmdbId The TMDB ID
     * @param mediaType The media type ("movie" or "series"/"tv")
     * @return The IMDB ID, or null if not found
     */
    suspend fun tmdbToImdb(tmdbId: Int, mediaType: String): String? = withContext(Dispatchers.IO) {
        // Check cache first
        tmdbToImdbCache[tmdbId]?.let { cached ->
            Log.d(TAG, "Cache hit: TMDB $tmdbId -> IMDB $cached")
            return@withContext cached
        }
        
        val normalizedType = normalizeMediaType(mediaType)
        val requestKey = "$tmdbId:$normalizedType"
        val requestDeferred = CompletableDeferred<String?>()
        tmdbToImdbInFlight.putIfAbsent(requestKey, requestDeferred)?.let { existing ->
            return@withContext existing.await()
        }

        try {
            Log.d(TAG, "Looking up IMDB ID for TMDB: $tmdbId (type: $mediaType)")
            
            val response = when (normalizedType) {
                "movie" -> tmdbApi.getMovieExternalIds(tmdbId, TMDB_API_KEY)
                "tv", "series" -> tmdbApi.getTvExternalIds(tmdbId, TMDB_API_KEY)
                else -> tmdbApi.getMovieExternalIds(tmdbId, TMDB_API_KEY)
            }
            
            if (!response.isSuccessful) {
                Log.e(TAG, "TMDB API error: ${response.code()} - ${response.message()}")
                requestDeferred.complete(null)
                return@withContext null
            }
            
            val body = response.body()
            if (body == null) {
                requestDeferred.complete(null)
                return@withContext null
            }
            
            body.imdbId?.let { imdbId ->
                Log.d(TAG, "Found IMDB ID: $imdbId for TMDB: $tmdbId")
                
                // Cache both directions
                cacheMutex.withLock {
                    tmdbToImdbCache[tmdbId] = imdbId
                    imdbToTmdbCache[imdbId] = tmdbId
                }

                requestDeferred.complete(imdbId)
                 
                return@withContext imdbId
            }
            
            Log.w(TAG, "No IMDB ID found for TMDB: $tmdbId")
            requestDeferred.complete(null)
            null
            
        } catch (e: CancellationException) {
            requestDeferred.cancel(e)
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error looking up IMDB ID for $tmdbId: ${e.message}", e)
            requestDeferred.complete(null)
            null
        } finally {
            tmdbToImdbInFlight.remove(requestKey, requestDeferred)
        }
    }
    
    /**
     * Get a TMDB ID from a video ID string.
     * Handles both IMDB IDs (tt...) and TMDB IDs.
     * 
     * @param videoId The video ID (can be IMDB or TMDB format)
     * @param mediaType The media type
     * @return The TMDB ID as a string, or null if conversion failed
     */
    suspend fun ensureTmdbId(videoId: String, mediaType: String): String? {
        // Check if it's already a TMDB ID (numeric or prefixed)
        val cleanId = videoId
            .removePrefix("tmdb:")
            .removePrefix("movie:")
            .removePrefix("series:")

        // Stremio-style series ids can look like: tt1234567:season:episode
        // Plugins/TMDB lookup need the base external id only.
        val idPart = cleanId
            .substringBefore(':')
            .substringBefore('/')
            .trim()
        
        // If it's an IMDB ID, convert it
        if (idPart.startsWith("tt")) {
            val tmdbId = imdbToTmdb(idPart, normalizeMediaType(mediaType))
            return tmdbId?.toString()
        }
        
        // If it looks like a numeric ID, assume it's already a TMDB ID
        if (idPart.all { it.isDigit() }) {
            return idPart
        }
        
        // Unknown format
        Log.w(TAG, "Unknown video ID format: $videoId")
        return null
    }
    
    /**
     * Normalize media type to consistent format
     */
    private fun normalizeMediaType(mediaType: String): String {
        return when (mediaType.lowercase()) {
            "series", "tv", "show", "tvshow" -> "tv"
            "movie", "film" -> "movie"
            else -> mediaType.lowercase()
        }
    }
    
    /**
     * Clear all caches
     */

    /**
     * Recherche une jaquette TMDB depuis un titre nettoye de fichier local/Freebox.
     * Le nom visible reste celui du fichier ; TMDB ne sert ici qu'a recuperer poster/backdrop.
     */
        suspend fun fetchImagesForTitleQuery(query: String, mediaTypeHint: String = "movie"): TmdbImages? =
        withContext(Dispatchers.IO) {
            val cleanQuery = query.trim()
            if (cleanQuery.length < 2 || TMDB_API_KEY.isBlank()) return@withContext null

            fun titleScore(title: String?): Int {
                if (title.isNullOrBlank()) return 0
                val q = cleanQuery.lowercase().trim()
                val t = title.lowercase().trim()
                return when {
                    t == q -> 100
                    t.startsWith(q) || q.startsWith(t) -> 80
                    t.contains(q) || q.contains(t) -> 60
                    else -> 0
                }
            }

            suspend fun searchMovieBest(): Pair<Int, TmdbImages>? {
                val results = tmdbApi.searchMovies(TMDB_API_KEY, cleanQuery, "fr-FR", 1, false)
                    .body()?.results.orEmpty()
                    .filter { !it.posterPath.isNullOrBlank() || !it.backdropPath.isNullOrBlank() }
                val result = results.maxByOrNull { titleScore(it.title ?: it.originalTitle) } ?: return null
                val score = titleScore(result.title ?: result.originalTitle)
                if (score == 0) return null
                val frPosterUrl = runCatching {
                    tmdbApi.getMovieImages(result.id, TMDB_API_KEY, "fr,en,null")
                        .body()?.posters
                        ?.sortedWith(compareBy { when (it.iso6391) { "fr" -> 0; "en" -> 1; else -> 2 } })
                        ?.firstOrNull { !it.filePath.isNullOrBlank() }
                        ?.filePath?.let { "https://image.tmdb.org/t/p/w500$it" }
                }.getOrNull()
                return score to TmdbImages(
                    backdropUrl = result.backdropPath?.let { "https://image.tmdb.org/t/p/w1280$it" },
                    posterUrl = frPosterUrl ?: result.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                    runtimeMinutes = null
                )
            }

            suspend fun searchTvBest(): Pair<Int, TmdbImages>? {
                val results = tmdbApi.searchTv(TMDB_API_KEY, cleanQuery, "fr-FR", 1, false)
                    .body()?.results.orEmpty()
                    .filter { !it.posterPath.isNullOrBlank() || !it.backdropPath.isNullOrBlank() }
                val result = results.maxByOrNull { titleScore(it.name ?: it.originalName) } ?: return null
                val score = titleScore(result.name ?: result.originalName)
                if (score == 0) return null
                val frPosterUrl = runCatching {
                    tmdbApi.getTvImages(result.id, TMDB_API_KEY, "fr,en,null")
                        .body()?.posters
                        ?.sortedWith(compareBy { when (it.iso6391) { "fr" -> 0; "en" -> 1; else -> 2 } })
                        ?.firstOrNull { !it.filePath.isNullOrBlank() }
                        ?.filePath?.let { "https://image.tmdb.org/t/p/w500$it" }
                }.getOrNull()
                return score to TmdbImages(
                    backdropUrl = result.backdropPath?.let { "https://image.tmdb.org/t/p/w1280$it" },
                    posterUrl = frPosterUrl ?: result.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                    runtimeMinutes = null
                )
            }

            runCatching {
                val movieResult = runCatching { searchMovieBest() }.getOrNull()
                val tvResult = runCatching { searchTvBest() }.getOrNull()
                val best = listOfNotNull(movieResult, tvResult).maxByOrNull { it.first }
                best?.second ?: if (normalizeMediaType(mediaTypeHint) == "tv") tvResult?.second ?: movieResult?.second
                               else movieResult?.second ?: tvResult?.second
            }.getOrNull()
        }



suspend fun fetchMetadataForTitleQuery(query: String, mediaTypeHint: String = "movie"): FreeboxVideoMeta? =
        withContext(Dispatchers.IO) {
            val cleanQuery = query.trim()
            if (cleanQuery.length < 2 || TMDB_API_KEY.isBlank()) return@withContext null

            fun titleScore(title: String?): Int {
                if (title.isNullOrBlank()) return 0
                val q = cleanQuery.lowercase().trim()
                val t = title.lowercase().trim()
                return when {
                    t == q -> 100
                    t.startsWith(q) || q.startsWith(t) -> 80
                    t.contains(q) || q.contains(t) -> 60
                    else -> 0
                }
            }

            suspend fun searchMovieMeta(): Pair<Int, FreeboxVideoMeta>? {
                val results = tmdbApi.searchMovies(TMDB_API_KEY, cleanQuery, "fr-FR", 1, false)
                    .body()?.results.orEmpty()
                val result = results.maxByOrNull { titleScore(it.title ?: it.originalTitle) } ?: return null
                val score = titleScore(result.title ?: result.originalTitle)
                if (score == 0) return null
                val details = tmdbApi.getMovieDetails(result.id, TMDB_API_KEY, "fr-FR").body()
                val frPoster = runCatching {
                    tmdbApi.getMovieImages(result.id, TMDB_API_KEY, "fr,en,null")
                        .body()?.posters
                        ?.sortedWith(compareBy { when (it.iso6391) { "fr" -> 0; "en" -> 1; else -> 2 } })
                        ?.firstOrNull { !it.filePath.isNullOrBlank() }
                        ?.filePath?.let { "https://image.tmdb.org/t/p/w500$it" }
                }.getOrNull()
                return score to FreeboxVideoMeta(
                    posterUrl = frPoster ?: result.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                    backdropUrl = result.backdropPath?.let { "https://image.tmdb.org/t/p/w1280$it" },
                    overview = details?.overview?.takeIf { it.isNotBlank() } ?: result.overview?.takeIf { it.isNotBlank() },
                    voteAverage = details?.voteAverage ?: result.voteAverage,
                    year = (details?.releaseDate ?: result.releaseDate)?.take(4),
                    genres = details?.genres?.map { it.name } ?: emptyList()
                )
            }

            suspend fun searchTvMeta(): Pair<Int, FreeboxVideoMeta>? {
                val results = tmdbApi.searchTv(TMDB_API_KEY, cleanQuery, "fr-FR", 1, false)
                    .body()?.results.orEmpty()
                val result = results.maxByOrNull { titleScore(it.name ?: it.originalName) } ?: return null
                val score = titleScore(result.name ?: result.originalName)
                if (score == 0) return null
                val details = tmdbApi.getTvDetails(result.id, TMDB_API_KEY, "fr-FR").body()
                val frPoster = runCatching {
                    tmdbApi.getTvImages(result.id, TMDB_API_KEY, "fr,en,null")
                        .body()?.posters
                        ?.sortedWith(compareBy { when (it.iso6391) { "fr" -> 0; "en" -> 1; else -> 2 } })
                        ?.firstOrNull { !it.filePath.isNullOrBlank() }
                        ?.filePath?.let { "https://image.tmdb.org/t/p/w500$it" }
                }.getOrNull()
                return score to FreeboxVideoMeta(
                    posterUrl = frPoster ?: result.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                    backdropUrl = result.backdropPath?.let { "https://image.tmdb.org/t/p/w1280$it" },
                    overview = details?.overview?.takeIf { it.isNotBlank() } ?: result.overview?.takeIf { it.isNotBlank() },
                    voteAverage = details?.voteAverage ?: result.voteAverage,
                    year = (details?.firstAirDate ?: result.firstAirDate)?.take(4),
                    genres = details?.genres?.map { it.name } ?: emptyList()
                )
            }

            runCatching {
                val movieResult = runCatching { searchMovieMeta() }.getOrNull()
                val tvResult = runCatching { searchTvMeta() }.getOrNull()
                val best = listOfNotNull(movieResult, tvResult).maxByOrNull { it.first }
                best?.second ?: if (normalizeMediaType(mediaTypeHint) == "tv") tvResult?.second ?: movieResult?.second
                               else movieResult?.second ?: tvResult?.second
            }.getOrNull()
        }

        

        fun clearCache() {
        imdbToTmdbCache.clear()
        tmdbToImdbCache.clear()
        imdbToTmdbInFlight.clear()
        tmdbToImdbInFlight.clear()
        Log.d(TAG, "Cache cleared")
    }
    
    /**
     * Pre-populate cache with known mappings
     */
    fun preCacheMapping(imdbId: String, tmdbId: Int) {
        imdbToTmdbCache[imdbId] = tmdbId
        tmdbToImdbCache[tmdbId] = imdbId
    }

    /** Returns the cached TMDB ID for an IMDB ID without making any network call. */
    fun cachedTmdbId(imdbId: String): Int? = imdbToTmdbCache[imdbId]

    fun apiKey(): String = TMDB_API_KEY

    /**
     * Fetches backdrop and poster URLs from TMDB for the given IMDB ID.
     * Returns null if the IMDB ID doesn't start with "tt" or if TMDB has no data.
     * Results are NOT cached here ââ‚¬â€ callers should persist what they need.
     */
    suspend fun fetchImdbImages(imdbId: String, mediaType: String): TmdbImages? =
        withContext(Dispatchers.IO) {
            if (!imdbId.startsWith("tt")) return@withContext null
            val tmdbId = imdbToTmdb(imdbId, mediaType) ?: return@withContext null
            runCatching {
                val isMovie = normalizeMediaType(mediaType) == "movie"
                val response = if (isMovie)
                    tmdbApi.getMovieDetails(tmdbId, TMDB_API_KEY, language = "fr-FR")
                else
                    tmdbApi.getTvDetails(tmdbId, TMDB_API_KEY, language = "fr-FR")
                val body = response.body() ?: return@runCatching null
                TmdbImages(
                    backdropUrl = body.backdropPath?.let { "https://image.tmdb.org/t/p/w1280$it" },
                    posterUrl = body.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                    runtimeMinutes = body.runtime
                )
            }.getOrNull()
        }
}


data class FreeboxVideoMeta(
    val posterUrl: String?,
    val backdropUrl: String?,
    val overview: String?,
    val voteAverage: Double?,
    val year: String?,
    val genres: List<String>
)

data class TmdbImages(val backdropUrl: String?, val posterUrl: String?, val runtimeMinutes: Int? = null)


