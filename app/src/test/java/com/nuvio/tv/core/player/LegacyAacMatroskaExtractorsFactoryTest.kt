package com.nuvio.tv.core.player

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LegacyAacMatroskaExtractorsFactoryTest {
    @Test
    fun `detects legacy MPEG2 LC SBR codec id`() {
        val header = byteArrayOf(0x42, 0x86.toByte()) +
            "A_AAC/MPEG2/LC/SBR".encodeToByteArray() + byteArrayOf(0x00)

        assertTrue(containsLegacyHeAacCodecId(header))
    }


    @Test
    fun `detects legacy HE AAC codec id beyond first megabyte`() {
        val header = ByteArray(1024 * 1024 + 32)
        val marker = "A_AAC/MPEG2/LC/SBR".encodeToByteArray()
        marker.copyInto(header, 1024 * 1024 + 4)

        assertTrue(containsLegacyHeAacCodecId(header))
    }
    @Test
    fun `does not intercept standard AAC Matroska`() {
        assertFalse(containsLegacyHeAacCodecId("A_AAC".encodeToByteArray()))
    }
}