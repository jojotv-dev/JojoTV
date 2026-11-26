package com.nuvio.tv.domain.model

data class FreeboxPlaybackEpisode(
    val contentId: String,
    val path: String,
    val title: String,
    val season: Int,
    val episode: Int,
    val released: String? = null,
    val thumbnail: String? = null,
    val overview: String? = null,
    val runtimeMinutes: Int? = null,
)
