package com.nuvio.tv.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FreeboxPosterOverrideDataStoreTest {
    @Test
    fun `decode keeps poster mapped to enriched Freebox id`() {
        val contentId = "freebox:/Freebox/Vidéos/Film.mkv#fb:1234:5678"
        val result = decodeFreeboxPosterOverrides(
            """{"$contentId":"https://image.tmdb.org/t/p/w500/poster.jpg"}"""
        )

        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", result[contentId])
    }

    @Test
    fun `decode ignores malformed or blank values`() {
        assertTrue(decodeFreeboxPosterOverrides("not-json").isEmpty())
        assertTrue(decodeFreeboxPosterOverrides("{\"freebox:test\":\"\"}").isEmpty())
    }
}