package com.nuvio.tv.domain.model

import java.util.Locale

internal enum class SeriesLifecycleStatus {
    ENDED,
    IN_PROGRESS
}

/** Normalizes the lifecycle values returned by TMDB for a TV series. */
internal fun normalizeTmdbSeriesStatus(status: String?): SeriesLifecycleStatus? =
    when (status?.trim()?.lowercase(Locale.ROOT)) {
        "ended", "canceled", "cancelled" -> SeriesLifecycleStatus.ENDED
        "returning series", "renewed", "continuing", "in production", "planned", "pilot",
        "post production", "current" -> SeriesLifecycleStatus.IN_PROGRESS
        else -> null
    }
