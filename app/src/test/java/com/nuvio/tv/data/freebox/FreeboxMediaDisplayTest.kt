package com.nuvio.tv.data.freebox

import org.junit.Assert.assertEquals
import org.junit.Test

class FreeboxMediaDisplayTest {
    @Test
    fun `removes generated timestamp before display title`() {
        assertEquals(
            "0h57 Sept à huit",
            freeboxVideoDisplayTitle("1782067749000 Sept à huit.mkv", 57L * 60L * 1000L)
        )
    }
}
