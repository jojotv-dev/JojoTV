package com.nuvio.tv.ui.screens.home

import com.nuvio.tv.core.tmdb.TmdbPosterCandidate
import com.nuvio.tv.core.tmdb.TmdbArtworkType
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.ui.screens.iptv.IptvProgressKey
import com.nuvio.tv.ui.screens.iptv.parseIptvProgressKey

internal suspend fun HomeViewModel.iptvFramePosterCandidate(item: MetaPreview): TmdbPosterCandidate? {
    return iptvFramePosterCandidates(item).firstOrNull()
}

internal suspend fun HomeViewModel.iptvFramePosterCandidate(progress: WatchProgress): TmdbPosterCandidate? {
    return iptvFramePosterCandidates(progress).firstOrNull()
}

internal suspend fun HomeViewModel.iptvFramePosterCandidates(item: MetaPreview): List<TmdbPosterCandidate> =
    iptvFramePosterCandidates(item.id)

internal suspend fun HomeViewModel.iptvFramePosterCandidates(progress: WatchProgress): List<TmdbPosterCandidate> =
    iptvFramePosterCandidates(progress.contentId)

private suspend fun HomeViewModel.iptvFramePosterCandidates(contentId: String): List<TmdbPosterCandidate> {
    val url = resolveIptvMovieStreamUrl(contentId) ?: return emptyList()
    val positionsUs = listOf(10_000_000L, 60_000_000L, 180_000_000L, 300_000_000L)
    return runCatching { videoFrameThumbnailService.thumbnailUris(url, positionsUs) }
        .getOrDefault(emptyList())
        .mapIndexed { index, uri -> uri.toFrameCandidate(index + 1) }
}

private suspend fun HomeViewModel.resolveIptvMovieStreamUrl(contentId: String): String? {
    val movie = when (val key = parseIptvProgressKey(contentId)) {
        is IptvProgressKey.MovieLocal -> iptvMovieRepository.getMovie(key.movieId)
        is IptvProgressKey.MovieStream -> iptvMovieRepository.getMovieByStreamId(key.providerId, key.streamId)
        else -> null
    } ?: return null
    movie.streamUrl.takeIf { it.isNotBlank() }?.let { return it }
    return when (val stream = iptvMovieRepository.getStreamInfo(movie)) {
        is com.streamvault.domain.model.Result.Success -> stream.data.url.takeIf { it.isNotBlank() }
        else -> null
    }
}

private fun String.toFrameCandidate(index: Int): TmdbPosterCandidate =
    TmdbPosterCandidate(
        url = this,
        language = null,
        title = if (index == 1) "Image extraite de la vidéo" else "Autre vignette frame $index",
        mediaType = "local",
        artworkType = TmdbArtworkType.LANDSCAPE
    )
