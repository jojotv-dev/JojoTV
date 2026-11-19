package com.nuvio.tv.core.sync

import com.nuvio.tv.data.remote.supabase.SupabaseProviderCredential
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProviderCredentialModelsTest {
    @Test
    fun completeRemoteSnapshotDoesNotRequireSeeding() {
        val snapshot = ProviderCredentialSnapshot(
            profileId = 1,
            values = listOf(
                ProviderCredentialValue("mdblist", "api_key", "local-mdblist")
            )
        )
        val rows = listOf(
            SupabaseProviderCredential(
                provider = "MDBLIST",
                credentialJson = buildJsonObject { put("api_key", "remote") }
            )
        )

        assertFalse(shouldSeedProviderCredentials(snapshot, rows))
    }

    @Test
    fun missingRemoteProviderRequiresSeeding() {
        val snapshot = ProviderCredentialSnapshot(
            profileId = 1,
            values = listOf(
                ProviderCredentialValue("mdblist", "api_key", "local-mdblist")
            )
        )
        val rows = listOf(
            SupabaseProviderCredential(
                provider = "tmdb",
                credentialJson = buildJsonObject { put("api_key", "remote") }
            )
        )

        assertTrue(shouldSeedProviderCredentials(snapshot, rows))
    }

    @Test
    fun `remote values replace only supported local providers`() {
        val local = ProviderCredentialSnapshot(
            profileId = 2,
            values = listOf(
                ProviderCredentialValue("mdblist", "api_key", "local-mdblist")
            )
        )
        val remote = listOf(
            SupabaseProviderCredential(
                provider = "mdblist",
                credentialJson = buildJsonObject { put("api_key", "remote-mdblist") }
            ),
            SupabaseProviderCredential(
                provider = "tmdb",
                credentialJson = buildJsonObject { put("api_key", "remote-tmdb") }
            )
        )

        val merged = local.mergeRemote(remote)

        assertEquals("remote-mdblist", merged.values.single().value)
    }

    @Test
    fun `blank remote value is retained as a clear tombstone`() {
        val local = ProviderCredentialSnapshot(
            profileId = 1,
            values = listOf(ProviderCredentialValue("mdblist", "api_key", "local"))
        )
        val remote = listOf(
            SupabaseProviderCredential(
                provider = "mdblist",
                credentialJson = buildJsonObject { put("api_key", "") }
            )
        )

        assertEquals("", local.mergeRemote(remote).values.single().value)
    }
}
