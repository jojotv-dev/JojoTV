package com.nuvio.tv.core.sync

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileSettingsIptvSyncTest {
    @Test
    fun `remaps provider scoped IPTV preference keys`() {
        val source = buildJsonObject {
            put("iptv_hidden_groups_42", buildJsonObject { put("type", "string_set") })
            put("iptv_group_renames_42", buildJsonObject { put("type", "string_set") })
            put("iptv_show_movies_in_sidebar", buildJsonObject { put("type", "boolean") })
        }

        val remapped = remapIptvPreferenceProviderIds(source, mapOf(42L to 7L))

        assertEquals(
            setOf(
                "iptv_hidden_groups_7",
                "iptv_group_renames_7",
                "iptv_show_movies_in_sidebar"
            ),
            remapped.keys
        )
    }
}
