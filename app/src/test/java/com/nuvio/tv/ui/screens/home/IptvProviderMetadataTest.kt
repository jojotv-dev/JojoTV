package com.nuvio.tv.ui.screens.home

import com.streamvault.domain.model.Movie
import org.junit.Assert.assertEquals
import org.junit.Test

class IptvProviderMetadataTest {

    @Test
    fun `provider movie details supply hero metadata and duration`() {
        val movie = Movie(
            id = 42L,
            name = "Little Brother",
            backdropUrl = "provider-backdrop",
            plot = "Synopsis provider",
            genre = "Comedie",
            releaseDate = "2026-01-15",
            durationSeconds = 5_160,
            rating = 6.7f
        )

        val metadata = movie.toHomeIptvMetadata()

        assertEquals("provider-backdrop", metadata.backdropUrl)
        assertEquals("Synopsis provider", metadata.overview)
        assertEquals("2026", metadata.year)
        assertEquals(listOf("Comedie"), metadata.genres)
        assertEquals(86, metadata.runtimeMinutes)
        assertEquals(6.7, metadata.voteAverage!!, 0.001)
    }
}
