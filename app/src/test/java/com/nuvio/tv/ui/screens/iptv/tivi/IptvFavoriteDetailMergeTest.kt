package com.nuvio.tv.ui.screens.iptv.tivi

import com.streamvault.domain.model.Movie
import com.streamvault.domain.model.Series
import org.junit.Assert.assertTrue
import org.junit.Test

class IptvFavoriteDetailMergeTest {

    @Test
    fun `movie details cannot clear the favorite selected on the card`() {
        val current = Movie(id = 42L, name = "Film", isFavorite = true)
        val details = Movie(id = 42L, name = "Film enrichi", isFavorite = false)

        assertTrue(mergeMovieDetailsWithCardState(current, details).isFavorite)
    }

    @Test
    fun `series details cannot clear the favorite selected on the card`() {
        val current = Series(id = 84L, name = "Serie", isFavorite = true)
        val details = Series(id = 84L, name = "Serie enrichie", isFavorite = false)

        assertTrue(mergeSeriesDetailsWithCardState(current, details).isFavorite)
    }
}
