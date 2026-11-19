package com.nuvio.tv.ui.screens.home

import com.nuvio.tv.core.tmdb.TmdbEnrichment
import com.nuvio.tv.core.tmdb.TmdbExactTitleMatch
import com.nuvio.tv.data.freebox.FreeboxVideoMetadata
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.MetaCastMember
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.PosterShape
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FreeboxVideoPresentationTest {
    @Test
    fun exactMatchFillsMissingFieldsButKeepsFileTagsAndIdentity() {
        val file = FreeboxVideoMetadata(
            title = "Cover Up",
            year = 1991,
            genres = listOf("Action", "Thriller"),
            durationMinutes = 113L
        )
        val local = buildFreeboxLocalPreview(
            base = preview("Cover.Up.1991.mkv", description = "previous video's synopsis"),
            fileMetadata = file,
            fallbackTitle = null,
            yearHint = 2003
        )
        val matched = mergeFreeboxVideoEnrichment(
            local = local,
            fileMetadata = file,
            yearHint = 2003,
            folderDurationMinutes = null,
            matchedTitle = "Cover Up",
            match = TmdbExactTitleMatch(123, ContentType.MOVIE),
            enrichment = enrichment(),
            artwork = null,
            useBasicInfo = true,
            useDetails = true,
            useArtwork = true,
            usedSavedArtworkMatch = false
        )

        assertEquals("freebox:Films/Cover.Up.1991.mkv", matched.id)
        assertEquals("Cover Up", matched.name)
        assertEquals("1991", matched.releaseInfo)
        assertEquals(listOf("Action", "Thriller"), matched.genres)
        assertEquals("113", matched.runtime)
        assertEquals("Synopsis for Cover Up", matched.description)
        assertEquals("https://image/logo.png", matched.logo)
        assertEquals(listOf("Director One"), matched.director)
        assertEquals("123", matched.tmdbId)
        assertNull(local.description)
    }

    @Test
    fun noMatchUsesReliableLocalFieldsAndDoesNotInventSynopsis() {
        val file = FreeboxVideoMetadata(durationMinutes = 90L)
        val local = buildFreeboxLocalPreview(
            base = preview("Mystery.File.mkv", description = "old synopsis"),
            fileMetadata = file,
            fallbackTitle = "Mystery File",
            yearHint = null
        )
        val result = mergeFreeboxVideoEnrichment(
            local = local,
            fileMetadata = file,
            yearHint = null,
            folderDurationMinutes = null,
            matchedTitle = null,
            match = null,
            enrichment = null,
            artwork = null,
            useBasicInfo = true,
            useDetails = true,
            useArtwork = true,
            usedSavedArtworkMatch = false
        )

        assertEquals("Mystery File", result.name)
        assertEquals("90", result.runtime)
        assertNull(result.description)
        assertNull(result.tmdbId)
        assertEquals(emptyList<String>(), result.genres)
    }

    private fun preview(name: String, description: String?) = MetaPreview(
        id = "freebox:Films/Cover.Up.1991.mkv",
        type = ContentType.MOVIE,
        name = name,
        poster = null,
        posterShape = PosterShape.POSTER,
        background = null,
        logo = null,
        description = description,
        releaseInfo = null,
        imdbRating = null,
        genres = emptyList(),
        sourceAddonBaseUrl = "freebox://nas"
    )

    private fun enrichment() = TmdbEnrichment(
        localizedTitle = "Cover Up",
        description = "Synopsis for Cover Up",
        genres = listOf("Drama"),
        backdrop = "https://image/backdrop.jpg",
        logo = "https://image/logo.png",
        poster = "https://image/poster.jpg",
        directorMembers = emptyList(),
        writerMembers = emptyList(),
        castMembers = listOf(MetaCastMember("Actor One")),
        releaseInfo = "2003",
        rating = null,
        runtimeMinutes = 120,
        director = listOf("Director One"),
        writer = emptyList(),
        productionCompanies = emptyList(),
        networks = emptyList(),
        ageRating = null,
        status = null,
        countries = null,
        language = null,
        collectionId = null,
        collectionName = null
    )
}
