package com.nuvio.tv.core.tmdb

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TmdbTitleMatcherTest {

    @Test
    fun `normalizes file query separators`() {
        assertEquals("brigade des moeurs", tmdbLooseTitleKey("Brigade.Des.Moeurs"))
    }

    @Test
    fun `matches tmdb ligature title with ascii file title`() {
        val score = tmdbTitleMatchScore(
            query = "Brigade Des Moeurs",
            title = "Brigade des mœurs",
            originalLanguage = "fr",
        )

        assertTrue(score >= 60)
    }

    @Test
    fun `matches accented tmdb title with unaccented file title`() {
        val score = tmdbTitleMatchScore(
            query = "Echappees Belles",
            title = "Échappées belles",
            originalLanguage = "fr",
        )

        assertTrue(score >= 60)
    }

    @Test
    fun `keeps exact ascii title matches working`() {
        val score = tmdbTitleMatchScore(
            query = "Le Grand Concours",
            title = "Le Grand Concours",
            originalLanguage = "fr",
        )

        assertTrue(score >= 60)
    }
}