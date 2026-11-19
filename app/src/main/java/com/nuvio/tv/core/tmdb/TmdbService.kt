package com.nuvio.tv.core.tmdb

import android.util.Log
import com.nuvio.tv.BuildConfig
import com.nuvio.tv.data.remote.api.TmdbApi
import com.nuvio.tv.data.remote.api.TmdbTitleSearchResult
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.reshaped.livetv.normalizeLiveTvVodTitleForTmdb
import retrofit2.Response
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.text.Normalizer
import kotlin.math.abs
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TmdbService"
private const val MAX_EXACT_TITLE_MATCHES_PER_TYPE = 3
private val TMDB_API_KEY = BuildConfig.TMDB_API_KEY
private val TMDB_TITLE_ALIAS_SEPARATOR = Regex("\\s*\\|\\s*")
private val TRAILING_TITLE_YEAR = Regex("""\s+(?:\((\d{4})\)|\[(\d{4})\]|(\d{4}))\s*$""")
private val TRAILING_EPISODE_SUFFIX = Regex(
    """(?i)\s+(?:(?:s|season|saison)\s*\d{1,2}\s*(?:e|episode|épisode)\s*\d{1,3}|episode\s*\d{1,3}|épisode\s*\d{1,3}|\d{1,3})\s*$"""
)

private data class ParsedTmdbSearchTitle(val title: String, val year: Int?)

private fun parseTmdbSearchTitle(title: String, year: Int?): ParsedTmdbSearchTitle {
    val trimmed = normalizeLiveTvVodTitleForTmdb(title).trim()
    val suffix = TRAILING_TITLE_YEAR.find(trimmed)
    val suffixYear = suffix?.groupValues?.drop(1)?.firstNotNullOfOrNull { it.toIntOrNull() }
    return ParsedTmdbSearchTitle(
        title = suffix?.let { trimmed.substring(0, it.range.first).trim() } ?: trimmed,
        year = year ?: suffixYear
    )
}

/**
 * Service to handle TMDB ID conversions and lookups.
 * Provides caching to avoid redundant API calls.
 */
@Singleton
class TmdbService @Inject constructor(
    private val tmdbApi: TmdbApi
) {
    /** Adds compound localized/original titles as separate exact-search aliases. */
    private fun buildExactTitleSearchCandidates(query: String): LinkedHashMap<String, String> {
        val candidates = linkedMapOf<String, String>()
        fun addTitleCandidate(candidate: String) {
            val normalized = normalizeSearchTitle(candidate)
            if (normalized.isBlank()) return
            listOf(
                candidate.replace(Regex("[._]+"), " ").replace(Regex("\\s+"), " ").trim(),
                candidate.replace(Regex("[^\\p{L}\\p{N}]+"), " ").replace(Regex("\\s+"), " ").trim(),
            ).filter(String::isNotBlank).forEach { candidates.putIfAbsent(it, normalized) }
        }

        val aliases = query.split(TMDB_TITLE_ALIAS_SEPARATOR)
            .map { it.trim() }
            .filter(String::isNotBlank)
        val queriesToTry = linkedSetOf(query).apply { addAll(aliases) }
        TRAILING_EPISODE_SUFFIX.replaceFirst(query, "").trim()
            .takeIf { it != query }
            ?.let(queriesToTry::add)
        aliases.forEach { alias ->
            TRAILING_EPISODE_SUFFIX.replaceFirst(alias, "").trim()
                .takeIf { it != alias }
                ?.let(queriesToTry::add)
        }
        queriesToTry.forEach(::addTitleCandidate)
        return candidates
    }

    /** Finds an exact normalized title and reports which TMDB catalog contains it. */
    suspend fun findExactTitleMatch(
        title: String,
        mediaType: String,
        language: String? = null,
        year: Int? = null,
        reportHttpErrors: Boolean = false,
    ): TmdbExactTitleMatch? = withContext(Dispatchers.IO) {
        val parsedTitle = parseTmdbSearchTitle(title, year)
        val query = parsedTitle.title
        if (query.isBlank()) return@withContext null
        if (TMDB_API_KEY.isBlank()) {
            if (reportHttpErrors) throw IOException("TMDB_API_KEY is missing from the local build configuration.")
            return@withContext null
        }
        val titleCandidates = buildExactTitleSearchCandidates(query)
        if (titleCandidates.isEmpty()) return@withContext null
        val preferredType = if (normalizeMediaType(mediaType) == "tv") "tv" else "movie"

        suspend fun exactMatch(type: String): TmdbExactTitleMatch? {
            val result = titleCandidates.entries.firstNotNullOfOrNull { (searchQuery, expectedTitle) ->
                findExactTitleSearchResults(
                    type = type,
                    query = searchQuery,
                    normalizedTitle = expectedTitle,
                    year = parsedTitle.year,
                    language = language,
                    reportHttpErrors = reportHttpErrors,
                ).firstOrNull()
            } ?: return null
            return TmdbExactTitleMatch(
                tmdbId = result.id,
                contentType = if (type == "tv") ContentType.SERIES else ContentType.MOVIE,
                posterPath = result.posterPath,
                backdropPath = result.backdropPath,
                title = if (type == "tv") result.name ?: result.originalName else result.title ?: result.originalTitle,
                releaseYear = (if (type == "tv") result.firstAirDate else result.releaseDate)
                    ?.take(4)?.toIntOrNull(),
            )
        }

        exactMatch(preferredType) ?: exactMatch(if (preferredType == "tv") "movie" else "tv")
    }

    /** Finds exact title matches in both TMDB catalogs, preferring the requested type first. */
    suspend fun findExactTitleMatches(
        title: String,
        mediaType: String,
        language: String? = null,
        year: Int? = null
    ): List<TmdbExactTitleMatch> = withContext(Dispatchers.IO) {
        val parsedTitle = parseTmdbSearchTitle(title, year)
        val query = parsedTitle.title
        if (query.isBlank() || TMDB_API_KEY.isBlank()) return@withContext emptyList()
        val titleCandidates = buildExactTitleSearchCandidates(query)
        if (titleCandidates.isEmpty()) return@withContext emptyList()
        val preferredType = if (normalizeMediaType(mediaType) == "tv") "tv" else "movie"

        suspend fun exactMatchesForType(type: String): List<TmdbExactTitleMatch> {
            val results = titleCandidates.entries.firstNotNullOfOrNull { (searchQuery, expectedTitle) ->
                findExactTitleSearchResults(
                    type = type,
                    query = searchQuery,
                    normalizedTitle = expectedTitle,
                    year = parsedTitle.year,
                    language = language
                ).takeIf { it.isNotEmpty() }
            }.orEmpty()
            return results.take(MAX_EXACT_TITLE_MATCHES_PER_TYPE).map { match ->
                TmdbExactTitleMatch(
                    tmdbId = match.id,
                    contentType = if (type == "tv") ContentType.SERIES else ContentType.MOVIE,
                    posterPath = match.posterPath,
                    backdropPath = match.backdropPath,
                    title = if (type == "tv") match.name ?: match.originalName else match.title ?: match.originalTitle,
                    releaseYear = (if (type == "tv") match.firstAirDate else match.releaseDate)
                        ?.take(4)?.toIntOrNull(),
                )
            }
        }

        val matches = mutableListOf<TmdbExactTitleMatch>()
        val fallbackType = if (preferredType == "tv") "movie" else "tv"
        matches += exactMatchesForType(preferredType)
        matches += exactMatchesForType(fallbackType)
        matches.distinctBy { it.tmdbId to it.contentType }
    }

    /** Finds manual folder artwork choices only from exact title matches. */
    suspend fun artworkCandidatesForExactTitle(
        title: String,
        mediaType: String,
        portrait: Boolean,
        language: String? = null,
        year: Int? = null,
        reportHttpErrors: Boolean = false,
    ): List<String> = withContext(Dispatchers.IO) {
        if (TMDB_API_KEY.isBlank()) {
            if (reportHttpErrors) throw IOException("TMDB_API_KEY is missing from the local build configuration.")
            return@withContext emptyList()
        }
        val match = findExactTitleMatch(title, mediaType, language, year, reportHttpErrors)
            ?: return@withContext emptyList()
        val matchType = if (match.contentType == ContentType.SERIES) "tv" else "movie"

        val imageLanguages = listOfNotNull(
            language?.substringBefore('-')?.takeIf(String::isNotBlank),
            "en",
            "null",
        ).distinct().joinToString(",")
        val candidates = linkedSetOf<String>()
        val primaryImagePath = if (portrait) match.posterPath else match.backdropPath
        primaryImagePath?.takeIf { it.startsWith('/') }?.let { path ->
            candidates += if (portrait) "https://image.tmdb.org/t/p/w500$path"
            else "https://image.tmdb.org/t/p/w1280$path"
        }

        val response = try {
            if (matchType == "tv") {
                tmdbApi.getTvImages(match.tmdbId, TMDB_API_KEY, imageLanguages)
            } else {
                tmdbApi.getMovieImages(match.tmdbId, TMDB_API_KEY, imageLanguages)
            }
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (error: Exception) {
            if (reportHttpErrors) {
                throw IOException("TMDB artwork request failed (${error.javaClass.simpleName}).")
            }
            null
        }
        if (reportHttpErrors && response != null && !response.isSuccessful) {
            throw IOException("TMDB artwork request failed (HTTP ${response.code()}).")
        }
        val images = response?.takeIf { it.isSuccessful }?.body()
        val paths = if (portrait) images?.posters.orEmpty() else images?.backdrops.orEmpty()
        paths.asSequence()
            .mapNotNull { it.filePath?.takeIf(String::isNotBlank) }
            .filter { it.startsWith('/') }
            .map { path ->
                if (portrait) "https://image.tmdb.org/t/p/w500$path"
                else "https://image.tmdb.org/t/p/w1280$path"
            }
            .forEach { candidates.add(it) }
        candidates.take(20)
    }

    /** Finds landscape artwork by the stable content ID, even when its display title differs. */
    suspend fun artworkCandidatesForContentId(
        contentId: String,
        mediaType: String,
        portrait: Boolean = false,
        language: String? = null,
    ): List<String> = withContext(Dispatchers.IO) {
        if (TMDB_API_KEY.isBlank() || contentId.isBlank()) return@withContext emptyList()
        val tmdbId = ensureTmdbId(contentId, mediaType)?.toIntOrNull()
            ?: return@withContext emptyList()
        val normalizedType = normalizeMediaType(mediaType)
        val imageLanguages = listOfNotNull(
            language?.substringBefore('-')?.takeIf(String::isNotBlank),
            "en",
            "null",
        ).distinct().joinToString(",")
        val response = try {
            if (normalizedType == "tv") {
                tmdbApi.getTvImages(tmdbId, TMDB_API_KEY, imageLanguages)
            } else {
                tmdbApi.getMovieImages(tmdbId, TMDB_API_KEY, imageLanguages)
            }
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (_: Exception) {
            return@withContext emptyList()
        }
        val images = response.takeIf { it.isSuccessful }?.body()
            ?: return@withContext emptyList()
        val paths = if (portrait) images.posters.orEmpty() else images.backdrops.orEmpty()
        paths.asSequence()
            .mapNotNull { it.filePath?.takeIf(String::isNotBlank) }
            .filter { it.startsWith('/') }
            .map { path ->
                if (portrait) "https://image.tmdb.org/t/p/w500$path"
                else "https://image.tmdb.org/t/p/w1280$path"
            }
            .distinct()
            .take(20)
            .toList()
    }

    /** Returns still images for one exact TV episode, used for per-episode Freebox artwork. */
    suspend fun artworkCandidatesForTvEpisode(
        tmdbId: Int,
        seasonNumber: Int,
        episodeNumber: Int,
        language: String? = null,
    ): List<String> = withContext(Dispatchers.IO) {
        if (TMDB_API_KEY.isBlank() || tmdbId <= 0 || seasonNumber <= 0 || episodeNumber <= 0) {
            return@withContext emptyList()
        }
        val imageLanguages = listOfNotNull(
            language?.substringBefore('-')?.takeIf(String::isNotBlank),
            "en",
            "null",
        ).distinct().joinToString(",")
        val response = try {
            tmdbApi.getTvEpisodeImages(
                tvId = tmdbId,
                seasonNumber = seasonNumber,
                episodeNumber = episodeNumber,
                apiKey = TMDB_API_KEY,
                includeImageLanguage = imageLanguages,
            )
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (_: Exception) {
            return@withContext emptyList()
        }
        response.takeIf { it.isSuccessful }?.body()?.stills.orEmpty()
            .asSequence()
            .mapNotNull { it.filePath?.takeIf(String::isNotBlank) }
            .filter { it.startsWith('/') }
            .map { "https://image.tmdb.org/t/p/w1280$it" }
            .distinct()
            .take(20)
            .toList()
    }

    /** Uses TMDB's ranked title search when strict title matching has no result. */
    suspend fun artworkCandidatesForSearchTitle(
        title: String,
        mediaType: String,
        portrait: Boolean = false,
        language: String? = null,
        year: Int? = null,
    ): List<String> = withContext(Dispatchers.IO) {
        val parsedTitle = parseTmdbSearchTitle(title, year)
        if (TMDB_API_KEY.isBlank() || parsedTitle.title.isBlank()) return@withContext emptyList()
        val normalizedType = if (normalizeMediaType(mediaType) == "tv") "tv" else "movie"
        val titleQueries = buildExactTitleSearchCandidates(parsedTitle.title).keys
        if (titleQueries.isEmpty()) return@withContext emptyList()

        val searchResults = linkedMapOf<Int, TmdbTitleSearchResult>()
        for (query in titleQueries) {
            suspend fun search(searchYear: Int?) = if (normalizedType == "tv") {
                searchTitleResults(
                    request = {
                        tmdbApi.searchTvByTitle(TMDB_API_KEY, query, year = searchYear, language = language)
                    }
                )
            } else {
                searchTitleResults(
                    request = {
                        tmdbApi.searchMoviesByTitle(TMDB_API_KEY, query, year = searchYear, language = language)
                    }
                )
            }
            var results = search(parsedTitle.year)
            if (results.isEmpty() && parsedTitle.year != null) results = search(null)
            results.forEach { searchResults.putIfAbsent(it.id, it) }
            if (searchResults.size >= MAX_EXACT_TITLE_MATCHES_PER_TYPE) break
        }

        val imageLanguages = listOfNotNull(
            language?.substringBefore('-')?.takeIf(String::isNotBlank),
            "en",
            "null",
        ).distinct().joinToString(",")
        val candidates = linkedSetOf<String>()
        for (result in searchResults.values.take(MAX_EXACT_TITLE_MATCHES_PER_TYPE)) {
            val match = TmdbExactTitleMatch(
                tmdbId = result.id,
                contentType = if (normalizedType == "tv") ContentType.SERIES else ContentType.MOVIE,
                posterPath = result.posterPath,
                backdropPath = result.backdropPath,
                title = if (normalizedType == "tv") result.name ?: result.originalName
                    else result.title ?: result.originalTitle,
                releaseYear = (if (normalizedType == "tv") result.firstAirDate else result.releaseDate)
                    ?.take(4)?.toIntOrNull(),
            )
            val primaryPath = if (portrait) match.posterPath else match.backdropPath
            primaryPath?.takeIf { it.startsWith('/') }?.let { path ->
                candidates += if (portrait) "https://image.tmdb.org/t/p/w500$path"
                else "https://image.tmdb.org/t/p/w1280$path"
            }
            val response = try {
                if (normalizedType == "tv") {
                    tmdbApi.getTvImages(match.tmdbId, TMDB_API_KEY, imageLanguages)
                } else {
                    tmdbApi.getMovieImages(match.tmdbId, TMDB_API_KEY, imageLanguages)
                }
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (_: Exception) {
                null
            }
            val images = response?.takeIf { it.isSuccessful }?.body()
            val paths = if (portrait) images?.posters.orEmpty() else images?.backdrops.orEmpty()
            paths.asSequence()
                .mapNotNull { it.filePath?.takeIf(String::isNotBlank) }
                .filter { it.startsWith('/') }
                .map { path ->
                    if (portrait) "https://image.tmdb.org/t/p/w500$path"
                    else "https://image.tmdb.org/t/p/w1280$path"
                }
                .forEach(candidates::add)
            if (candidates.size >= 20) break
        }
        candidates.take(20)
    }

    /**
     * Returns artwork together with the exact TMDB item that owns each image. Keeping that
     * identity attached prevents a manual image choice from being paired with a different
     * same-title movie or series when metadata is refreshed.
     */
    suspend fun artworkCandidatesForExactTitleWithMatches(
        title: String,
        mediaType: String,
        portrait: Boolean,
        language: String? = null,
        year: Int? = null,
    ): List<TmdbArtworkCandidate> = withContext(Dispatchers.IO) {
        if (TMDB_API_KEY.isBlank()) return@withContext emptyList()
        val matches = findExactTitleMatches(title, mediaType, language, year)
        if (matches.isEmpty()) return@withContext emptyList()

        val imageLanguages = listOfNotNull(
            language?.substringBefore('-')?.takeIf(String::isNotBlank),
            "en",
            "null",
        ).distinct().joinToString(",")
        val perMatchLimit = (20 / matches.size).coerceAtLeast(1)
        val candidates = mutableListOf<TmdbArtworkCandidate>()
        for (match in matches) {
            val matchType = if (match.contentType == ContentType.SERIES) "tv" else "movie"
            val imageUrls = linkedSetOf<String>()
            val primaryImagePath = if (portrait) match.posterPath else match.backdropPath
            primaryImagePath?.takeIf { it.startsWith('/') }?.let { path ->
                imageUrls += if (portrait) "https://image.tmdb.org/t/p/w500$path"
                else "https://image.tmdb.org/t/p/w1280$path"
            }

            val response = try {
                if (matchType == "tv") {
                    tmdbApi.getTvImages(match.tmdbId, TMDB_API_KEY, imageLanguages)
                } else {
                    tmdbApi.getMovieImages(match.tmdbId, TMDB_API_KEY, imageLanguages)
                }
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (_: Exception) {
                null
            }
            val images = response?.takeIf { it.isSuccessful }?.body()
            val paths = if (portrait) images?.posters.orEmpty() else images?.backdrops.orEmpty()
            paths.asSequence()
                .mapNotNull { it.filePath?.takeIf(String::isNotBlank) }
                .filter { it.startsWith('/') }
                .map { path ->
                    if (portrait) "https://image.tmdb.org/t/p/w500$path"
                    else "https://image.tmdb.org/t/p/w1280$path"
                }
                .forEach(imageUrls::add)

            imageUrls.take(perMatchLimit).forEach { imageUrl ->
                candidates += TmdbArtworkCandidate(imageUrl = imageUrl, match = match)
            }
        }
        candidates.take(20)
    }

    /** Returns artwork only for an exact normalized title match, avoiding unrelated posters. */
    suspend fun posterForExactTitle(
        title: String,
        mediaType: String,
        language: String? = null,
        year: Int? = null
    ): String? =
        withContext(Dispatchers.IO) {
            if (TMDB_API_KEY.isBlank()) return@withContext null
            val match = findExactTitleMatch(title, mediaType, language, year) ?: return@withContext null
            match.posterPath?.takeIf { it.startsWith('/') }
                ?.let { "https://image.tmdb.org/t/p/w500$it" }
        }

    private suspend fun findExactTitleSearchResults(
        type: String,
        query: String,
        normalizedTitle: String,
        year: Int?,
        language: String?,
        reportHttpErrors: Boolean = false,
    ): List<TmdbTitleSearchResult> {
        suspend fun search(searchYear: Int?): List<TmdbTitleSearchResult> = if (type == "tv") {
            searchTitleResults(
                request = {
                    tmdbApi.searchTvByTitle(TMDB_API_KEY, query, year = searchYear, language = language)
                },
                reportHttpErrors = reportHttpErrors,
            )
        } else {
            searchTitleResults(
                request = {
                    tmdbApi.searchMoviesByTitle(TMDB_API_KEY, query, year = searchYear, language = language)
                },
                reportHttpErrors = reportHttpErrors,
            )
        }

        fun exactMatches(results: List<TmdbTitleSearchResult>) = results.filter { candidate ->
            listOfNotNull(candidate.title, candidate.name, candidate.originalTitle, candidate.originalName)
                .any { normalizeSearchTitle(it) == normalizedTitle }
        }

        val yearFiltered = exactMatches(search(year))
        val candidates = if (yearFiltered.isNotEmpty() || year == null) {
            yearFiltered
        } else {
            exactMatches(search(null))
        }
        return if (year == null) {
            candidates
        } else {
            candidates.sortedBy { candidate ->
                val candidateYear = (if (type == "tv") candidate.firstAirDate else candidate.releaseDate)
                    ?.take(4)?.toIntOrNull()
                candidateYear?.let { abs(it - year) } ?: Int.MAX_VALUE
            }
        }
    }

    private suspend fun searchTitleResults(
        request: suspend () -> Response<com.nuvio.tv.data.remote.api.TmdbTitleSearchResponse>,
        reportHttpErrors: Boolean = false,
    ): List<TmdbTitleSearchResult> {
        val response = try {
            request()
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (error: Exception) {
            if (reportHttpErrors) {
                throw IOException("TMDB title search failed (${error.javaClass.simpleName}).")
            }
            return emptyList()
        }
        if (!response.isSuccessful) {
            if (reportHttpErrors) throw IOException("TMDB title search failed (HTTP ${response.code()}).")
            return emptyList()
        }
        return response.body()?.results.orEmpty()
    }

    private fun normalizeSearchTitle(value: String): String = Normalizer
        .normalize(value, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .replace(Regex("\\([^)]*\\)|\\[[^\\]]*\\]"), " ")
        .replace(Regex("[^\\p{L}\\p{N}]+"), " ")
        .trim()
        .lowercase()

    // Cache: IMDB ID -> TMDB ID (keyed by "$imdbId:$mediaType")
    private val imdbToTmdbCache = ConcurrentHashMap<String, Int>()
    
    // Cache: TMDB ID -> IMDB ID (keyed by "$tmdbId:$mediaType")
    private val tmdbToImdbCache = ConcurrentHashMap<String, String>()

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
        
        val normalizedType = normalizeMediaType(mediaType)
        val cacheKey = "$imdbId:$normalizedType"

        // Check cache first
        imdbToTmdbCache[cacheKey]?.let { cached ->
            Log.d(TAG, "Cache hit: IMDB $imdbId ($normalizedType) -> TMDB $cached")
            return@withContext cached
        }
        
        val requestKey = cacheKey
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
                    imdbToTmdbCache[cacheKey] = found.id
                    tmdbToImdbCache["${found.id}:$normalizedType"] = imdbId
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
        val normalizedType = normalizeMediaType(mediaType)
        val cacheKey = "$tmdbId:$normalizedType"

        // Check cache first
        tmdbToImdbCache[cacheKey]?.let { cached ->
            Log.d(TAG, "Cache hit: TMDB $tmdbId ($normalizedType) -> IMDB $cached")
            return@withContext cached
        }
        
        val requestKey = cacheKey
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
                    tmdbToImdbCache[cacheKey] = imdbId
                    imdbToTmdbCache["$imdbId:$normalizedType"] = tmdbId
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
     * @param fallbackImdbId Optional IMDB ID from meta response
     * @return The TMDB ID as a string, or null if conversion failed
     */
    suspend fun ensureTmdbId(videoId: String, mediaType: String, fallbackImdbId: String? = null): String? {
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

        // Fallback: use the IMDB ID supplied by the addon's meta response
        val normalizedFallback = fallbackImdbId
            ?.trim()
            ?.substringBefore(':')
            ?.takeIf { it.startsWith("tt", ignoreCase = true) }
        if (normalizedFallback != null) {
            val tmdbId = imdbToTmdb(normalizedFallback, normalizeMediaType(mediaType))
            if (tmdbId != null) return tmdbId.toString()
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
    fun preCacheMapping(imdbId: String, tmdbId: Int, mediaType: String = "movie") {
        val normalizedType = normalizeMediaType(mediaType)
        imdbToTmdbCache["$imdbId:$normalizedType"] = tmdbId
        tmdbToImdbCache["$tmdbId:$normalizedType"] = imdbId
    }

    /** Returns the cached TMDB ID for an IMDB ID without making any network call. */
    fun cachedTmdbId(imdbId: String): Int? =
        imdbToTmdbCache["$imdbId:movie"] ?: imdbToTmdbCache["$imdbId:tv"]

    fun apiKey(): String = TMDB_API_KEY

    /**
     * Fetches backdrop and poster URLs from TMDB for the given IMDB ID.
     * Returns null if the IMDB ID doesn't start with "tt" or if TMDB has no data.
     * Results are NOT cached here — callers should persist what they need.
     */
    suspend fun fetchImdbImages(imdbId: String, mediaType: String): TmdbImages? =
        withContext(Dispatchers.IO) {
            if (!imdbId.startsWith("tt")) return@withContext null
            val tmdbId = imdbToTmdb(imdbId, mediaType) ?: return@withContext null
            runCatching {
                val isMovie = normalizeMediaType(mediaType) == "movie"
                val response = if (isMovie)
                    tmdbApi.getMovieDetails(tmdbId, TMDB_API_KEY)
                else
                    tmdbApi.getTvDetails(tmdbId, TMDB_API_KEY)
                val body = response.body() ?: return@runCatching null
                TmdbImages(
                    backdropUrl = body.backdropPath?.let { "https://image.tmdb.org/t/p/w1280$it" },
                    posterUrl = body.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                    runtimeMinutes = body.runtime
                )
            }.getOrNull()
        }
}

data class TmdbExactTitleMatch(
    val tmdbId: Int,
    val contentType: ContentType,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val title: String? = null,
    val releaseYear: Int? = null,
)

data class TmdbArtworkCandidate(
    val imageUrl: String,
    val match: TmdbExactTitleMatch,
)

data class TmdbImages(val backdropUrl: String?, val posterUrl: String?, val runtimeMinutes: Int? = null)
