package com.nuvio.tv.ui.screens.iptv

import com.streamvault.domain.model.Movie
import com.streamvault.domain.model.Series

internal sealed class IptvProgressKey {
    data class MovieLocal(val providerId: Long, val movieId: Long) : IptvProgressKey()
    data class MovieStream(val providerId: Long, val streamId: Long) : IptvProgressKey()
    data class SeriesLocal(val providerId: Long, val seriesId: Long) : IptvProgressKey()
    data class SeriesRemote(val providerId: Long, val providerSeriesId: String) : IptvProgressKey()
}

internal fun Movie.stableIptvProgressId(): String =
    streamId.takeIf { it > 0L }
        ?.let { "iptv_movie_stream:$providerId:$it" }
        ?: "iptv_movie:$providerId:$id"

internal fun Series.stableIptvProgressId(): String =
    providerSeriesId?.takeIf { it.isNotBlank() }
        ?.let { "iptv_series_remote:$providerId:$it" }
        ?: "iptv_series:$providerId:$id"

internal fun parseIptvProgressKey(contentId: String): IptvProgressKey? {
    val parts = contentId.split(':')
    if (parts.size < 3) return null
    val providerId = parts[1].toLongOrNull() ?: return null
    return when (parts[0].lowercase()) {
        "iptv_movie" -> IptvProgressKey.MovieLocal(
            providerId = providerId,
            movieId = parts[2].toLongOrNull() ?: return null
        )
        "iptv_movie_stream" -> IptvProgressKey.MovieStream(
            providerId = providerId,
            streamId = parts[2].toLongOrNull() ?: return null
        )
        "iptv_series" -> IptvProgressKey.SeriesLocal(
            providerId = providerId,
            seriesId = parts[2].toLongOrNull() ?: return null
        )
        "iptv_series_remote" -> IptvProgressKey.SeriesRemote(
            providerId = providerId,
            providerSeriesId = parts.drop(2).joinToString(":")
        )
        else -> null
    }
}
