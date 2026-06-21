@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.nuvio.tv.ui.screens.home

import androidx.lifecycle.viewModelScope
import com.nuvio.tv.R
import com.nuvio.tv.domain.model.CatalogRow
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.PosterShape
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
                        resolveFavoriteSeries(providerIds)
                    ) { movies, series ->
                        buildList {
                            movies.toMovieFavoriteCatalogRow(appContext.getString(R.string.home_iptv_favorite_movies))?.let(::add)
                            series.toSeriesFavoriteCatalogRow(appContext.getString(R.string.home_iptv_favorite_series))?.let(::add)
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

private fun List<Movie>.toMovieFavoriteCatalogRow(title: String): HomeRow.Catalog? {
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
            items = map(Movie::toHomePreview),
            hasMore = false
        )
    )
}

private fun List<Series>.toSeriesFavoriteCatalogRow(title: String): HomeRow.Catalog? {
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
            items = map(Series::toHomePreview),
            hasMore = false
        )
    )
}

private fun Movie.toHomePreview() = MetaPreview(
    id = "iptv_movie:$providerId:$id",
    type = ContentType.UNKNOWN,
    rawType = "iptv_movie",
    name = name,
    poster = posterUrl,
    posterShape = PosterShape.POSTER,
    background = backdropUrl,
    logo = null,
    description = plot,
    releaseInfo = year ?: releaseDate?.take(4),
    imdbRating = rating.takeIf { it > 0f },
    genres = genre.toGenreList(),
    runtime = duration
)

private fun Series.toHomePreview() = MetaPreview(
    id = "iptv_series:$providerId:$id",
    type = ContentType.UNKNOWN,
    rawType = "iptv_series",
    name = name,
    poster = posterUrl,
    posterShape = PosterShape.POSTER,
    background = backdropUrl,
    logo = null,
    description = plot,
    releaseInfo = releaseDate?.take(4),
    imdbRating = rating.takeIf { it > 0f },
    genres = genre.toGenreList()
)

private fun String?.toGenreList(): List<String> =
    this?.split(',', '/', '|')
        ?.map(String::trim)
        ?.filter(String::isNotEmpty)
        .orEmpty()
internal fun HomeViewModel.removeIptvFavorite(item: MetaPreview) {
    val parts = item.id.split(':')
    if (parts.size != 3) return
    val providerId = parts[1].toLongOrNull() ?: return
    val contentId = parts[2].toLongOrNull() ?: return
    val contentType = when (item.rawType) {
        "iptv_movie" -> IptvContentType.MOVIE
        "iptv_series" -> IptvContentType.SERIES
        else -> return
    }
    viewModelScope.launch {
        iptvFavoriteRepository.removeFavorite(providerId, contentId, contentType)
    }
}

private fun formatHomeIptvDuration(durationSeconds: Int, duration: String?): String? {
    val seconds = durationSeconds.toLong().takeIf { it > 0L }
        ?: duration?.trim()?.toLongOrNull()?.takeIf { it > 0 }
        ?: return null
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours > 0) hours.toString() + "h" + minutes.toString().padStart(2, '0') else minutes.toString() + " min"
}
