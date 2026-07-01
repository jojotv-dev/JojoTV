package com.nuvio.tv.ui.screens.home

import com.nuvio.tv.core.tmdb.FreeboxVideoMeta
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.PosterShape
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IptvTitleMetadataMergeTest {

    @Test
    fun `title metadata fills the modern hero and formats movie runtime`() {
        val current = iptvMovie(
            poster = "custom-poster",
            background = null,
            description = null,
            runtime = null,
            isFavorite = true
        )
        val metadata = FreeboxVideoMeta(
            posterUrl = "tmdb-poster",
            backdropUrl = "tmdb-backdrop",
            overview = "Synopsis TMDB",
            voteAverage = 6.7,
            year = "2026",
            genres = listOf("Comedie"),
            runtimeMinutes = 86
        )

        val merged = mergeIptvTitleMetadata(current, metadata)

        assertEquals("custom-poster", merged.poster)
        assertEquals("tmdb-backdrop", merged.background)
        assertEquals("Synopsis TMDB", merged.description)
        assertEquals("1h26", merged.runtime)
        assertEquals("2026", merged.releaseInfo)
        assertEquals(6.7f, merged.imdbRating)
        assertEquals(listOf("Comedie"), merged.genres)
        assertTrue(merged.isFavorite)
    }

    @Test
    fun `provider metadata wins when it is already available`() {
        val current = iptvMovie(
            poster = "provider-poster",
            background = "provider-backdrop",
            description = "Synopsis provider",
            runtime = "1h30",
            isFavorite = false
        )
        val metadata = FreeboxVideoMeta(
            posterUrl = "tmdb-poster",
            backdropUrl = "tmdb-backdrop",
            overview = "Synopsis TMDB",
            voteAverage = 7.0,
            year = "2026",
            genres = listOf("Drame"),
            runtimeMinutes = 86
        )

        val merged = mergeIptvTitleMetadata(current, metadata)

        assertEquals("provider-poster", merged.poster)
        assertEquals("provider-backdrop", merged.background)
        assertEquals("Synopsis provider", merged.description)
        assertEquals("1h30", merged.runtime)
    }

    private fun iptvMovie(
        poster: String?,
        background: String?,
        description: String?,
        runtime: String?,
        isFavorite: Boolean
    ) = MetaPreview(
        id = "iptv_movie:provider:42",
        type = ContentType.UNKNOWN,
        rawType = "iptv_movie",
        name = "Chers parents (2026)",
        poster = poster,
        posterShape = PosterShape.POSTER,
        background = background,
        logo = null,
        description = description,
        releaseInfo = null,
        imdbRating = null,
        genres = emptyList(),
        runtime = runtime,
        isFavorite = isFavorite
    )
}
