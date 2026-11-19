package com.nuvio.tv.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StreamYouTubeTest {

    @Test
    fun `a stream with only a ytId needs the video resolved`() {
        assertEquals("dQw4w9WgXcQ", stream(ytId = "dQw4w9WgXcQ").youTubeIdToResolve())
    }

    @Test
    fun `surrounding whitespace is trimmed from the ytId`() {
        assertEquals("dQw4w9WgXcQ", stream(ytId = "  dQw4w9WgXcQ ").youTubeIdToResolve())
    }

    @Test
    fun `a missing or blank ytId needs nothing resolved`() {
        assertNull(stream(ytId = null).youTubeIdToResolve())
        assertNull(stream(ytId = "").youTubeIdToResolve())
        assertNull(stream(ytId = "   ").youTubeIdToResolve())
    }

    @Test
    fun `a url takes precedence over the ytId`() {
        val stream = stream(ytId = "dQw4w9WgXcQ", url = "https://example.com/video.mp4")

        assertNull(stream.youTubeIdToResolve())
        assertEquals("https://example.com/video.mp4", stream.getStreamUrl())
    }

    @Test
    fun `an external url takes precedence over the ytId`() {
        val stream = stream(ytId = "dQw4w9WgXcQ", externalUrl = "https://example.com/page")

        assertNull(stream.youTubeIdToResolve())
    }

    @Test
    fun `a resolved stream is no longer treated as needing resolution`() {
        val resolved = stream(ytId = "dQw4w9WgXcQ").copy(url = "https://manifest.googlevideo.com/master.m3u8")

        assertNull(resolved.youTubeIdToResolve())
        assertEquals("https://manifest.googlevideo.com/master.m3u8", resolved.getStreamUrl())
    }

    private fun stream(
        ytId: String?,
        url: String? = null,
        externalUrl: String? = null
    ): Stream = Stream(
        name = "Video",
        title = null,
        description = null,
        url = url,
        ytId = ytId,
        externalUrl = externalUrl,
        behaviorHints = null,
        addonName = "Addon",
        addonLogo = null
    )
}
