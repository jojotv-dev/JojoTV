@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.nuvio.tv.ui.screens.home

import androidx.lifecycle.viewModelScope
import com.nuvio.tv.R
import com.nuvio.tv.core.tmdb.FreeboxVideoMeta
import com.nuvio.tv.core.tmdb.TmdbArtworkType
import com.nuvio.tv.core.tmdb.TmdbPosterCandidate
import com.nuvio.tv.domain.model.CatalogRow
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.PosterShape
import com.nuvio.tv.ui.screens.iptv.parseIptvProgressKey
import com.nuvio.tv.ui.screens.iptv.stableIptvProgressId
import com.nuvio.tv.ui.screens.iptv.IptvProgressKey
import com.streamvault.domain.model.ContentType as IptvContentType
import com.streamvault.domain.model.Favorite
import com.streamvault.domain.model.Movie
import com.streamvault.domain.model.Series
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal fun HomeViewModel.observeIptvFavoriteRows() {
    viewModelScope.launch {
        iptvProviderRepository.getProviders()
            .map { providers ->
                providers.asSequence()
                    .filter { it.isActive }
                    .map { it.id }
                    .distinct()
                    .sorted()
                    .toList()
            }
            .distinctUntilChanged()
            .flatMapLatest { providerIds ->
                if (providerIds.isEmpty()) {
                    flowOf(emptyList<HomeRow.Catalog>())
                } else {
                    combine(
                        resolveFavoriteMovies(providerIds),
                        resolveFavoriteSeries(providerIds),
                        freeboxPosterOverrideDataStore.overrides,
                        freeboxPosterOverrideDataStore.backdropOverrides
                    ) { movies, series, posterOverrides, backdropOverrides ->
                        buildList {
                            movies.toMovieFavoriteCatalogRow(
                                title = appContext.getString(R.string.home_iptv_favorite_movies),
                                posterOverrides = posterOverrides,
                                backdropOverrides = backdropOverrides
                            )?.let(::add)
                            series.toSeriesFavoriteCatalogRow(
                                title = appContext.getString(R.string.home_iptv_favorite_series),
                                posterOverrides = posterOverrides,
                                backdropOverrides = backdropOverrides
                            )?.let(::add)
                        }
                    }
                }
            }
            .collectLatest { rows ->
                if (iptvFavoriteRows != rows) {
                    iptvFavoriteRows = rows
                    scheduleUpdateCatalogRows()
                }
            }
    }
}

private fun HomeViewModel.resolveFavoriteMovies(providerIds: List<Long>): Flow<List<Movie>> =
    iptvFavoriteRepository.getFavorites(providerIds, IptvContentType.MOVIE)
        .flatMapLatest { favorites ->
            val ids = favorites.map(Favorite::contentId).distinct()
            if (ids.isEmpty()) {
                flowOf(emptyList<Movie>())
            } else {
                iptvMovieRepository.getMoviesByIds(ids).map { movies ->
                    movies.inFavoriteOrder(favorites, Movie::id, Movie::providerId)
                }
            }
        }

private fun HomeViewModel.resolveFavoriteSeries(providerIds: List<Long>): Flow<List<Series>> =
    iptvFavoriteRepository.getFavorites(providerIds, IptvContentType.SERIES)
        .flatMapLatest { favorites ->
            val ids = favorites.map(Favorite::contentId).distinct()
            if (ids.isEmpty()) {
                flowOf(emptyList<Series>())
            } else {
                iptvSeriesRepository.getSeriesByIds(ids).map { series ->
                    series.inFavoriteOrder(favorites, Series::id, Series::providerId)
                }
            }
        }

private fun <T> List<T>.inFavoriteOrder(
    favorites: List<Favorite>,
    idOf: (T) -> Long,
    providerIdOf: (T) -> Long
): List<T> {
    val byProviderAndId = associateBy { providerIdOf(it) to idOf(it) }
    return favorites
        .mapNotNull { favorite -> byProviderAndId[favorite.providerId to favorite.contentId] }
        .distinctBy { providerIdOf(it) to idOf(it) }
}

private fun List<Movie>.toMovieFavoriteCatalogRow(
    title: String,
    posterOverrides: Map<String, String>,
    backdropOverrides: Map<String, String>
): HomeRow.Catalog? {
    if (isEmpty()) return null
    return HomeRow.Catalog(
        CatalogRow(
            addonId = HomeViewModel.IPTV_FAVORITES_ADDON_ID,
            addonName = "IPTV",
            addonBaseUrl = "iptv://favorites",
            catalogId = HomeViewModel.IPTV_MOVIE_FAVORITES_CATALOG_ID,
            catalogName = title,
            type = ContentType.UNKNOWN,
            rawType = "iptv_movie",
            items = map { it.toHomePreview(posterOverrides, backdropOverrides) },
            hasMore = false
        )
    )
}

private fun List<Series>.toSeriesFavoriteCatalogRow(
    title: String,
    posterOverrides: Map<String, String>,
    backdropOverrides: Map<String, String>
): HomeRow.Catalog? {
    if (isEmpty()) return null
    return HomeRow.Catalog(
        CatalogRow(
            addonId = HomeViewModel.IPTV_FAVORITES_ADDON_ID,
            addonName = "IPTV",
            addonBaseUrl = "iptv://favorites",
            catalogId = HomeViewModel.IPTV_SERIES_FAVORITES_CATALOG_ID,
            catalogName = title,
            type = ContentType.UNKNOWN,
            rawType = "iptv_series",
            items = map { it.toHomePreview(posterOverrides, backdropOverrides) },
            hasMore = false
        )
    )
}

private fun Movie.toHomePreview(
    posterOverrides: Map<String, String>,
    backdropOverrides: Map<String, String>
): MetaPreview {
    val previewId = stableIptvProgressId()
    val posterOverride = posterOverrides[previewId]?.takeIf { it.isNotBlank() }
    val backdropOverride = backdropOverrides[previewId]?.takeIf { it.isNotBlank() }
    val formattedDuration = formatHomeIptvDuration(durationSeconds, duration)
    return MetaPreview(
    id = previewId,
    type = ContentType.UNKNOWN,
    rawType = "iptv_movie",
    name = prefixHomeIptvMovieDuration(name, formattedDuration),
    poster = posterOverride ?: posterUrl,
    posterShape = PosterShape.POSTER,
    background = backdropOverride ?: backdropUrl,
    landscapePoster = backdropOverride ?: backdropUrl,
    rawPosterUrl = posterOverride ?: posterUrl,
    logo = null,
    description = plot,
    releaseInfo = year ?: releaseDate?.take(4),
    imdbRating = rating.takeIf { it > 0f },
    genres = genre.toGenreList(),
    runtime = formattedDuration ?: duration,
    isFavorite = isFavorite
)
}

private fun Series.toHomePreview(
    posterOverrides: Map<String, String>,
    backdropOverrides: Map<String, String>
): MetaPreview {
    val previewId = stableIptvProgressId()
    val posterOverride = posterOverrides[previewId]?.takeIf { it.isNotBlank() }
    val backdropOverride = backdropOverrides[previewId]?.takeIf { it.isNotBlank() }
    return MetaPreview(
    id = previewId,
    type = ContentType.UNKNOWN,
    rawType = "iptv_series",
    name = name,
    poster = posterOverride ?: posterUrl,
    posterShape = PosterShape.POSTER,
    background = backdropOverride ?: backdropUrl,
    landscapePoster = backdropOverride ?: backdropUrl,
    rawPosterUrl = posterOverride ?: posterUrl,
    logo = null,
    description = plot,
    releaseInfo = releaseDate?.take(4),
    imdbRating = rating.takeIf { it > 0f },
    genres = genre.toGenreList(),
    isFavorite = isFavorite
)
}

private fun String?.toGenreList(): List<String> =
    this?.split(',', '/', '|')
        ?.map(String::trim)
        ?.filter(String::isNotEmpty)
        .orEmpty()
internal fun HomeViewModel.removeIptvFavorite(item: MetaPreview) {
    viewModelScope.launch {
        val target = resolveIptvFavoriteTarget(item) ?: return@launch
        iptvFavoriteRepository.removeFavorite(target.providerId, target.contentId, target.contentType)
    }
}

internal fun HomeViewModel.addIptvFavorite(item: MetaPreview) {
    viewModelScope.launch {
        val target = resolveIptvFavoriteTarget(item) ?: return@launch
        iptvFavoriteRepository.addFavorite(target.providerId, target.contentId, target.contentType)
    }
}

internal fun HomeViewModel.updateIptvFavoriteArtwork(item: MetaPreview, posterUrl: String) {
    updateIptvFavoriteArtwork(
        item,
        TmdbPosterCandidate(url = posterUrl, language = null)
    )
}

internal fun HomeViewModel.updateIptvFavoriteArtwork(
    item: MetaPreview,
    candidate: TmdbPosterCandidate
) {
    viewModelScope.launch {
        val target = resolveIptvFavoriteTarget(item) ?: return@launch
        when (target.contentType) {
            IptvContentType.MOVIE -> {
                val current = iptvMovieRepository.getMovie(target.contentId)
                val artwork = applyIptvArtworkSelection(
                    currentPoster = current?.posterUrl ?: item.poster,
                    currentBackdrop = current?.backdropUrl ?: item.background,
                    candidate = candidate
                )
                iptvMovieRepository.updateMovieArtwork(
                    movieId = target.contentId,
                    posterUrl = artwork.posterUrl,
                    backdropUrl = artwork.backdropUrl
                )
            }
            IptvContentType.SERIES -> {
                val current = iptvSeriesRepository.getSeriesById(target.contentId)
                val artwork = applyIptvArtworkSelection(
                    currentPoster = current?.posterUrl ?: item.poster,
                    currentBackdrop = current?.backdropUrl ?: item.background,
                    candidate = candidate
                )
                iptvSeriesRepository.updateSeriesArtwork(
                    seriesId = target.contentId,
                    posterUrl = artwork.posterUrl,
                    backdropUrl = artwork.backdropUrl
                )
            }
            else -> return@launch
        }
        if (candidate.artworkType == TmdbArtworkType.LANDSCAPE) {
            freeboxPosterOverrideDataStore.setBackdrop(item.id, candidate.url)
        } else {
            freeboxPosterOverrideDataStore.set(item.id, candidate.url)
        }
        applyIptvArtworkToHome(item.id, candidate)
        clearModernIptvArtworkCache(item.id)
        scheduleUpdateCatalogRows()
    }
}

private data class HomeIptvFavoriteTarget(
    val providerId: Long,
    val contentId: Long,
    val contentType: IptvContentType
)

private suspend fun HomeViewModel.resolveIptvFavoriteTarget(item: MetaPreview): HomeIptvFavoriteTarget? {
    val key = parseIptvProgressKey(item.id) ?: return null
    val providerId = when (key) {
        is IptvProgressKey.MovieLocal -> key.providerId
        is IptvProgressKey.MovieStream -> key.providerId
        is IptvProgressKey.SeriesLocal -> key.providerId
        is IptvProgressKey.SeriesRemote -> key.providerId
    }
    val contentType = when (item.rawType) {
        "iptv_movie" -> IptvContentType.MOVIE
        "iptv_series" -> IptvContentType.SERIES
        else -> return null
    }
    val contentId = when (key) {
        is IptvProgressKey.MovieLocal -> key.movieId
        is IptvProgressKey.MovieStream -> iptvMovieRepository.getMovieByStreamId(key.providerId, key.streamId)?.id
        is IptvProgressKey.SeriesLocal -> key.seriesId
        is IptvProgressKey.SeriesRemote ->
            iptvSeriesRepository.getSeriesByProviderSeriesId(key.providerId, key.providerSeriesId)?.id
    } ?: return null
    return HomeIptvFavoriteTarget(providerId, contentId, contentType)
}

internal data class IptvArtworkSelection(
    val posterUrl: String?,
    val backdropUrl: String?
)

internal fun applyIptvArtworkSelection(
    currentPoster: String?,
    currentBackdrop: String?,
    candidate: TmdbPosterCandidate
): IptvArtworkSelection = when (candidate.artworkType) {
    TmdbArtworkType.PORTRAIT -> IptvArtworkSelection(
        posterUrl = candidate.url,
        backdropUrl = currentBackdrop
    )
    TmdbArtworkType.LANDSCAPE -> IptvArtworkSelection(
        posterUrl = currentPoster,
        backdropUrl = candidate.url
    )
}

private fun HomeViewModel.applyIptvArtworkToHome(
    itemId: String,
    candidate: TmdbPosterCandidate
) {
    fun merge(current: MetaPreview): MetaPreview {
        val artwork = applyIptvArtworkSelection(
            currentPoster = current.poster,
            currentBackdrop = current.background,
            candidate = candidate
        )
        return current.copy(
            poster = artwork.posterUrl,
            background = artwork.backdropUrl,
            landscapePoster = artwork.backdropUrl,
            rawPosterUrl = artwork.posterUrl
        )
    }

    updateIndexedCatalogItem(itemId, ::merge)
    iptvFavoriteRows = iptvFavoriteRows.map { homeRow ->
        val index = homeRow.row.items.indexOfFirst { it.id == itemId }
        if (index < 0) {
            homeRow
        } else {
            homeRow.copy(
                row = homeRow.row.copy(
                    items = homeRow.row.items.toMutableList().apply {
                        this[index] = merge(this[index])
                    }
                )
            )
        }
    }
    _uiState.update { state ->
        val rows = state.catalogRows.map { row ->
            val index = row.items.indexOfFirst { it.id == itemId }
            if (index < 0) row
            else row.copy(items = row.items.toMutableList().apply { this[index] = merge(this[index]) })
        }
        state.copy(catalogRows = rows)
    }
    _enrichedPreviews.update { previews ->
        val source = previews[itemId]
            ?: findCatalogItemById(itemId)
            ?: return@update previews
        previews + (itemId to merge(source))
    }
    _lastEnrichedPreview.value
        ?.takeIf { it.id == itemId }
        ?.let { _lastEnrichedPreview.value = merge(it) }
}

private fun HomeViewModel.clearModernIptvArtworkCache(itemId: String) {
    modernCarouselRowBuildCache.catalogItemCache.values.forEach { rowCache ->
        rowCache.keys
            .filter { key -> key == itemId || key.startsWith("${itemId}_") }
            .forEach(rowCache::remove)
    }
    modernCarouselRowBuildCache.catalogRows.clear()
}

internal fun Movie.toHomeIptvMetadata(): FreeboxVideoMeta = FreeboxVideoMeta(
    tmdbId = tmdbId?.toInt(),
    mediaType = "movie",
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    overview = plot?.takeIf { it.isNotBlank() },
    voteAverage = rating.takeIf { it > 0f }?.toDouble(),
    year = year ?: releaseDate?.take(4),
    genres = genre.toGenreList(),
    runtimeMinutes = durationSeconds.takeIf { it > 0 }?.let { (it + 30) / 60 }
        ?: duration?.let(::parseIptvDurationSeconds)?.let { ((it + 30) / 60).toInt() }
)

internal fun Series.toHomeIptvMetadata(): FreeboxVideoMeta = FreeboxVideoMeta(
    tmdbId = tmdbId?.toInt(),
    mediaType = "series",
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    overview = plot?.takeIf { it.isNotBlank() },
    voteAverage = rating.takeIf { it > 0f }?.toDouble(),
    year = releaseDate?.take(4),
    genres = genre.toGenreList(),
    runtimeMinutes = episodeRunTime?.let(::parseIptvDurationSeconds)?.let { ((it + 30) / 60).toInt() }
)

private fun formatHomeIptvDuration(durationSeconds: Int, duration: String?): String? {
    val seconds = durationSeconds.toLong().takeIf { it > 0L }
        ?: duration?.trim()?.let(::parseIptvDurationSeconds)?.takeIf { it > 0L }
        ?: return null
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours > 0) hours.toString() + "h" + minutes.toString().padStart(2, '0') else minutes.toString() + " min"
}

internal fun prefixHomeIptvMovieDuration(title: String, formattedDuration: String?): String {
    val duration = formattedDuration?.takeIf { it.isNotBlank() } ?: return title
    return if (title.startsWith("$duration ")) title else "$duration $title"
}

private fun parseIptvDurationSeconds(rawDuration: String): Long? {
    val normalized = rawDuration.trim().lowercase().takeIf { it.isNotBlank() } ?: return null
    val clockParts = normalized.split(':').mapNotNull { it.toLongOrNull() }
    if (clockParts.size == 3) {
        return clockParts[0] * 3600 + clockParts[1] * 60 + clockParts[2]
    }
    if (clockParts.size == 2) {
        return clockParts[0] * 3600 + clockParts[1] * 60
    }
    if ("h" in normalized || "m" in normalized) {
        val hours = Regex("""(\d+)\s*h""").find(normalized)?.groupValues?.getOrNull(1)?.toLongOrNull() ?: 0L
        val minutes = Regex("""(\d+)\s*m(?:in)?""").find(normalized)?.groupValues?.getOrNull(1)?.toLongOrNull() ?: 0L
        return (hours * 3600 + minutes * 60).takeIf { it > 0L }
    }
    val value = normalized.filter(Char::isDigit).toLongOrNull() ?: return null
    return if (value > 300L) value else value * 60
}
