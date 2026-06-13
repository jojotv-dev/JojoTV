package com.nuvio.tv.domain.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tailles de vignettes pour la section "Continuer à regarder".
 * MEDIUM = référence visuelle (équivalent au mode Classic actuel).
 */
enum class ThumbnailSize(
    val cardWidth: Dp,
    val imageHeight: Dp
) {
    SMALL(cardWidth = 160.dp, imageHeight = 90.dp),
    MEDIUM(cardWidth = 220.dp, imageHeight = 124.dp),
    LARGE(cardWidth = 288.dp, imageHeight = 162.dp);

    companion object {
        val DEFAULT = MEDIUM
        fun fromName(name: String) = entries.firstOrNull { it.name == name } ?: DEFAULT
    }
}
