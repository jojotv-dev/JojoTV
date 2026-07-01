package com.nuvio.tv.ui.screens.iptv

import com.nuvio.tv.core.tmdb.TmdbService
import com.streamvault.domain.model.Movie
import com.streamvault.domain.model.Result
import com.streamvault.domain.model.Series
import com.streamvault.domain.repository.MovieRepository
import com.streamvault.domain.repository.SeriesRepository

internal const val IPTV_FOCUS_ENRICH_DEBOUNCE_MS = 220L

internal data class IptvEnrichedFocusData(
    val plot: String? = null,
    val genre: String? = null,
    val year: String? = null,
    val rating: Float? = null,
    val durationMinutes: Int? = null,
)

private fun IptvEnrichedFocusData?.needsTmdbFallback(): Boolean {
    if (this == null) return true
    return plot.isNullOrBlank() || durationMinutes == null
}

internal suspend fun enrichIptvFocusFromMovie(
    movieRepository: MovieRepository,
    tmdbService: TmdbService,
    providerId: Long,
    movie: Movie,
): IptvEnrichedFocusData? {
    val detailed = when (val result = movieRepository.getMovieDetails(providerId, movie.id)) {
        is Result.Success -> result.data
        else -> null
    }
    var enriched = detailed?.let {
        IptvEnrichedFocusData(
            plot = it.plot?.takeIf { p -> p.isNotBlank() },
            genre = it.genre?.takeIf { g -> g.isNotBlank() },
            year = it.year ?: it.releaseDate?.take(4),
            rating = it.rating.takeIf { r -> r > 0f },
            durationMinutes = it.durationSeconds.takeIf { s -> s > 0 }?.let { s -> (s + 30) / 60 },
        )
    }

    if (enriched.needsTmdbFallback()) {
        val tmdbMeta = runCatching {
            tmdbService.fetchMetadataForTitleQuery(query = movie.name, mediaTypeHint = "movie")
        }.getOrNull()
        if (tmdbMeta != null) {
            enriched = IptvEnrichedFocusData(
                plot = enriched?.plot?.takeIf { it.isNotBlank() } ?: tmdbMeta.overview?.takeIf { it.isNotBlank() },
                genre = enriched?.genre?.takeIf { it.isNotBlank() } ?: tmdbMeta.genres.firstOrNull(),
                year = enriched?.year?.takeIf { it.isNotBlank() } ?: tmdbMeta.year,
                rating = enriched?.rating ?: tmdbMeta.voteAverage?.toFloat(),
                durationMinutes = enriched?.durationMinutes ?: tmdbMeta.runtimeMinutes,
            )
        }
    }
    return enriched
}

internal suspend fun enrichIptvFocusFromSeries(
    seriesRepository: SeriesRepository,
    tmdbService: TmdbService,
    providerId: Long,
    series: Series,
): IptvEnrichedFocusData? {
    val detailed = when (val result = seriesRepository.getSeriesDetails(providerId, series.id)) {
        is Result.Success -> result.data
        else -> null
    }
    var enriched = detailed?.let {
        IptvEnrichedFocusData(
            plot = it.plot?.takeIf { p -> p.isNotBlank() },
            genre = it.genre?.takeIf { g -> g.isNotBlank() },
            year = it.releaseDate?.take(4),
            rating = it.rating.takeIf { r -> r > 0f },
            durationMinutes = it.episodeRunTime?.toIntOrNull(),
        )
    }

    if (enriched.needsTmdbFallback()) {
        val tmdbMeta = runCatching {
            tmdbService.fetchMetadataForTitleQuery(query = series.name, mediaTypeHint = "tv")
        }.getOrNull()
        if (tmdbMeta != null) {
            enriched = IptvEnrichedFocusData(
                plot = enriched?.plot?.takeIf { it.isNotBlank() } ?: tmdbMeta.overview?.takeIf { it.isNotBlank() },
                genre = enriched?.genre?.takeIf { it.isNotBlank() } ?: tmdbMeta.genres.firstOrNull(),
                year = enriched?.year?.takeIf { it.isNotBlank() } ?: tmdbMeta.year,
                rating = enriched?.rating ?: tmdbMeta.voteAverage?.toFloat(),
                durationMinutes = enriched?.durationMinutes ?: tmdbMeta.runtimeMinutes,
            )
        }
    }
    return enriched
}
