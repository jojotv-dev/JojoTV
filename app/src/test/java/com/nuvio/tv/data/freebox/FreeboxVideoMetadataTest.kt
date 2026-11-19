package com.nuvio.tv.data.freebox

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FreeboxVideoMetadataTest {
    @Test
    fun parsesTagsFromTheSelectedFile() {
        val metadata = parseFreeboxVideoMetadata(
            title = "Cover Up",
            date = "1991-06-14",
            genre = "Action, Thriller; Drama",
            durationMs = 113 * 60_000L
        )

        assertEquals("Cover Up", metadata.title)
        assertEquals(1991, metadata.year)
        assertEquals(listOf("Action", "Thriller", "Drama"), metadata.genres)
        assertEquals(113L, metadata.durationMinutes)
    }

    @Test
    fun similarTitlesKeepTheirOwnYears() {
        val older = parseFreeboxVideoMetadata("Cover Up", "1991", null, null)
        val newer = parseFreeboxVideoMetadata("Cover Up", "2003", null, null)

        assertEquals(1991, older.year)
        assertEquals(2003, newer.year)
        assertEquals(older.title, newer.title)
    }

    @Test
    fun incompleteTagsLeaveUnknownFieldsEmpty() {
        val metadata = parseFreeboxVideoMetadata(
            title = null,
            date = null,
            genre = " ",
            durationMs = 5_400_000L
        )

        assertNull(metadata.title)
        assertNull(metadata.year)
        assertEquals(emptyList<String>(), metadata.genres)
        assertEquals(90L, metadata.durationMinutes)
    }
}
