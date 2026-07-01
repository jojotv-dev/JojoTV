package com.nuvio.tv.ui.screens.home

import com.nuvio.tv.core.tmdb.TmdbArtworkType
import com.nuvio.tv.core.tmdb.TmdbPosterCandidate
import org.junit.Assert.assertEquals
import org.junit.Test

class IptvArtworkSelectionTest {

    @Test
    fun `portrait selection keeps the existing landscape artwork`() {
        val selection = applyIptvArtworkSelection(
            currentPoster = "old-poster",
            currentBackdrop = "old-backdrop",
            candidate = candidate("new-poster", TmdbArtworkType.PORTRAIT)
        )

        assertEquals("new-poster", selection.posterUrl)
        assertEquals("old-backdrop", selection.backdropUrl)
    }

    @Test
    fun `landscape or frame selection keeps the existing portrait artwork`() {
        val selection = applyIptvArtworkSelection(
            currentPoster = "old-poster",
            currentBackdrop = "old-backdrop",
            candidate = candidate("new-backdrop", TmdbArtworkType.LANDSCAPE)
        )

        assertEquals("old-poster", selection.posterUrl)
        assertEquals("new-backdrop", selection.backdropUrl)
    }

    private fun candidate(url: String, type: TmdbArtworkType) = TmdbPosterCandidate(
        url = url,
        language = null,
        artworkType = type
    )
}
