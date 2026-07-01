package com.nuvio.tv.domain.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tailles de vignettes (posters portrait 2:3) pour les ecrans IPTV Films/Series/Tivi.
 * Echelle calquee sur ThumbnailSize (largeurs identiques) pour rester coherente.
 */
enum class IptvPosterSize(
    val cardWidth: Dp
) {
    VERY_SMALL(cardWidth = 84.dp),
    SMALL(cardWidth = 116.dp),
    MEDIUM(cardWidth = 160.dp),
    LARGE(cardWidth = 220.dp),
    VERY_LARGE(cardWidth = 288.dp);

    companion object {
        val DEFAULT = MEDIUM
        fun fromName(name: String?) = entries.firstOrNull { it.name == name } ?: DEFAULT
    }
}