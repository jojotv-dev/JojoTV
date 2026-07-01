package com.nuvio.tv.ui.screens.search

import com.nuvio.tv.domain.model.CatalogRow
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.PosterShape
import com.streamvault.domain.model.Channel
import com.streamvault.domain.model.Movie
import com.streamvault.domain.model.Series
import com.streamvault.domain.usecase.SearchContentResult

internal data class IptvSearchPlaybackRequest(
    val streamUrl: String,
    val title: String,
    val headers: Map<String, String>,
    val posterUrl: String?
)

internal fun SearchContentResult.toCatalogRows(
    providerId: Long,
    providerName: String
): List<CatalogRow> = buildList {
    channels.toLiveCatalogRow(
        addonId = "iptv_provider_$providerId",
        addonName = providerName,
        catalogId = "iptv_live_$providerId"
    )?.let(::add)
    movies.toMovieCatalogRow(providerId, providerName)?.let(::add)
    series.toSeriesCatalogRow(providerId, providerName)?.let(::add)
}

internal fun List<Channel>.toCombinedLiveCatalogRow(
    profileId: Long,
    profileName: String
): CatalogRow? = toLiveCatalogRow(
    addonId = "iptv_combined_$profileId",
    addonName = profileName,
    catalogId = "iptv_combined_live_$profileId"
)

private fun List<Channel>.toLiveCatalogRow(
    addonId: String,
    addonName: String,
    catalogId: String
): CatalogRow? {
    if (isEmpty()) return null
    return CatalogRow(
        addonId = addonId,
        addonName = addonName,
        addonBaseUrl = "iptv://search",
        catalogId = catalogId,
        catalogName = "Live TV",
        type = ContentType.UNKNOWN,
        rawType = IPTV_LIVE_SEARCH_TYPE,
        items = distinctBy { it.providerId to it.id }.map(Channel::toSearchPreview),
        hasMore = false
    )
}

private fun List<Movie>.toMovieCatalogRow(
    providerId: Long,
    providerName: String
): CatalogRow? {
    if (isEmpty()) return null
    return CatalogRow(
        addonId = "iptv_provider_$providerId",
        addonName = providerName,
        addonBaseUrl = "iptv://search",
        catalogId = "iptv_movies_$providerId",
        catalogName = "Films",
        type = ContentType.UNKNOWN,
        rawType = IPTV_MOVIE_SEARCH_TYPE,
        items = distinctBy { it.providerId to it.id }.map(Movie::toSearchPreview),
        hasMore = false
    )
}

private fun List<Series>.toSeriesCatalogRow(
    providerId: Long,
    providerName: String
): CatalogRow? {
    if (isEmpty()) return null
    return CatalogRow(
        addonId = "iptv_provider_$providerId",
        addonName = providerName,
        addonBaseUrl = "iptv://search",
        catalogId = "iptv_series_$providerId",
        catalogName = "Séries",
        type = ContentType.UNKNOWN,
        rawType = IPTV_SERIES_SEARCH_TYPE,
        items = distinctBy { it.providerId to it.id }.map(Series::toSearchPreview),
        hasMore = false
    )
}

private fun Channel.toSearchPreview() = MetaPreview(
    id = iptvSearchItemId(IPTV_LIVE_SEARCH_TYPE, providerId, id),
    type = ContentType.UNKNOWN,
    rawType = IPTV_LIVE_SEARCH_TYPE,
    name = name,
    poster = logoUrl,
    posterShape = PosterShape.SQUARE,
    background = currentProgram?.imageUrl ?: logoUrl,
    logo = logoUrl,
    description = currentProgram?.title,
    releaseInfo = categoryName ?: groupTitle,
    imdbRating = null,
    genres = listOfNotNull(categoryName ?: groupTitle),
    isFavorite = isFavorite
)

private fun Movie.toSearchPreview() = MetaPreview(
    id = iptvSearchItemId(IPTV_MOVIE_SEARCH_TYPE, providerId, id),
    type = ContentType.UNKNOWN,
    rawType = IPTV_MOVIE_SEARCH_TYPE,
    name = name,
    poster = posterUrl,
    posterShape = PosterShape.POSTER,
    background = backdropUrl,
    logo = null,
    description = plot,
    releaseInfo = year ?: releaseDate?.take(4),
    imdbRating = rating.takeIf { it > 0f },
    genres = genre.toGenreList(),
    runtime = duration,
    isFavorite = isFavorite
)

private fun Series.toSearchPreview() = MetaPreview(
    id = iptvSearchItemId(IPTV_SERIES_SEARCH_TYPE, providerId, id),
    type = ContentType.UNKNOWN,
    rawType = IPTV_SERIES_SEARCH_TYPE,
    name = name,
    poster = posterUrl,
    posterShape = PosterShape.POSTER,
    background = backdropUrl,
    logo = null,
    description = plot,
    releaseInfo = releaseDate?.take(4),
    imdbRating = rating.takeIf { it > 0f },
    genres = genre.toGenreList(),
    isFavorite = isFavorite
)

internal fun parseIptvSearchItemId(itemId: String): Triple<String, Long, Long>? {
    val parts = itemId.split(':')
    val type = parts.getOrNull(0) ?: return null
    val providerId = parts.getOrNull(1)?.toLongOrNull() ?: return null
    val contentId = parts.getOrNull(2)?.toLongOrNull() ?: return null
    return Triple(type, providerId, contentId)
}

internal fun iptvSearchItemId(type: String, providerId: Long, contentId: Long): String =
    "$type:$providerId:$contentId"

private fun String?.toGenreList(): List<String> =
    this?.split(',', '/', '|')
        ?.map(String::trim)
        ?.filter(String::isNotEmpty)
        .orEmpty()

internal const val IPTV_LIVE_SEARCH_TYPE = "iptv_live"
internal const val IPTV_MOVIE_SEARCH_TYPE = "iptv_movie"
internal const val IPTV_SERIES_SEARCH_TYPE = "iptv_series"
