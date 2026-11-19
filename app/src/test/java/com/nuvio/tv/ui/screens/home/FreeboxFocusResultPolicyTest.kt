package com.nuvio.tv.ui.screens.home

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FreeboxFocusResultPolicyTest {
    @Test
    fun lateResultForPreviousVideoIsRejectedAfterRapidFocusChange() {
        val firstId = "freebox:Films/Cover Up (1991).mkv"
        val secondId = "freebox:Films/Cover Up (2003).mkv"

        assertFalse(isCurrentFreeboxFocusResult(firstId, 7L, secondId, 8L))
        assertTrue(isCurrentFreeboxFocusResult(secondId, 8L, secondId, 8L))
    }

    @Test
    fun previousRequestIsRejectedWhenSameVideoIsRefreshed() {
        val id = "freebox:Films/Cover Up.mkv"

        assertFalse(isCurrentFreeboxFocusResult(id, 11L, id, 12L))
        assertTrue(isCurrentFreeboxFocusResult(id, 12L, id, 12L))
    }
}
