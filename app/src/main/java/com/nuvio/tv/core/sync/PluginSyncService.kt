package com.nuvio.tv.core.sync

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.nuvio.tv.core.auth.AuthManager
import com.nuvio.tv.core.profile.ProfileManager
import com.nuvio.tv.data.local.PluginDataStore
import com.nuvio.tv.data.local.PluginSyncProfile
import com.nuvio.tv.data.local.effectivePluginProfileId
import com.nuvio.tv.data.remote.supabase.SupabasePlugin
import com.nuvio.tv.domain.model.RemotePluginInfo
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.addJsonObject
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PluginSyncService"
private const val PLUGIN_SETTINGS_SYNC_DEBOUNCE_MS = 1500L

@Singleton
@OptIn(FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class PluginSyncService @Inject constructor(
    private val postgrest: Postgrest,
    private val authManager: AuthManager,
    private val pluginDataStore: PluginDataStore,
    private val profileManager: ProfileManager,
    private val syncClientIdentity: SyncClientIdentity
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        scope.launch {
            profileManager.activeProfileId
                .flatMapLatest { activeProfileId ->
                    pluginDataStore.repositories
                        .map { repositories -> activeProfileId to repositories }
                        .distinctUntilChanged()
                        // Don't push a profile's cached local state merely because it became active.
                        .drop(1)
                }
                .debounce(PLUGIN_SETTINGS_SYNC_DEBOUNCE_MS)
                .collect { (profileId, _) ->
                    val profile = profileManager.profiles.value.firstOrNull { it.id == profileId }
                    if (
                        authManager.isAuthenticated &&
                        !(profile != null && !profile.isPrimary && profile.usesPrimaryPlugins)
                    ) {
                        pushToRemote()
                    }
                }
        }
    }

    private suspend fun <T> withJwtRefreshRetry(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            if (!authManager.refreshSessionIfJwtExpired(e)) throw e
            block()
        }
    }

    /**
     * Push local plugin repository URLs to Supabase via RPC.
     * Uses a SECURITY DEFINER function to handle RLS for linked devices.
     */
    private fun pluginProfileId(): Int {
        PluginSyncProfile.current()?.let { return it }
        val activeProfile = profileManager.activeProfile
        return effectivePluginProfileId(
            usesPrimaryPlugins = activeProfile?.usesPrimaryPlugins == true,
            activeProfileId = profileManager.activeProfileId.value
        )
    }

    suspend fun pushToRemote(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val activeProfile = profileManager.activeProfile
            val profileId = pluginProfileId()
            Log.d(TAG, "pushToRemote: activeProfile=${activeProfile?.id} isPrimary=${activeProfile?.isPrimary} usesPrimaryPlugins=${activeProfile?.usesPrimaryPlugins} profileId=$profileId")

            if (PluginSyncProfile.current() == null &&
                activeProfile != null &&
                !activeProfile.isPrimary &&
                activeProfile.usesPrimaryPlugins
            ) {
                Log.d(TAG, "Profile ${activeProfile.id} uses primary plugins, skipping push")
                return@withContext Result.success(Unit)
            }

            val localRepos = pluginDataStore.repositories.first()
            Log.d(TAG, "pushToRemote: localRepos count=${localRepos.size} for profile $profileId")

            val params = buildJsonObject {
                put("p_plugins", buildJsonArray {
                    localRepos.forEachIndexed { index, repo ->
                        addJsonObject {
                            put("url", repo.url)
                            put("name", repo.name)
                            put("enabled", repo.enabled)
                            put("sort_order", index)
                            put("repo_type", repo.type.name)
                        }
                    }
                })
                put("p_profile_id", profileId)
                putSyncOriginClientId(syncClientIdentity)
            }
            Log.d(TAG, "pushToRemote: calling RPC sync_push_plugins with profile_id=$profileId")
            withJwtRefreshRetry {
                postgrest.rpc("sync_push_plugins", params)
            }

            Log.d(TAG, "Pushed ${localRepos.size} plugin repos to remote for profile $profileId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to push plugins to remote", e)
            Result.failure(e)
        }
    }

    suspend fun getRemoteRepoUrls(): Result<List<RemotePluginInfo>> = withContext(Dispatchers.IO) {
        try {
            val effectiveUserId = authManager.getEffectiveUserId(fallbackToOwnIdOnFailure = false)
                ?: return@withContext Result.failure(
                    IllegalStateException("Unable to resolve sync owner for plugin sync")
                )

            val profileId = pluginProfileId()

            val remotePlugins = withJwtRefreshRetry {
                postgrest.from("plugins")
                    .select { filter {
                        eq("user_id", effectiveUserId)
                        eq("profile_id", profileId)
                    } }
                    .decodeList<SupabasePlugin>()
            }

            Result.success(
                remotePlugins
                .sortedBy { it.sortOrder }
                .map { RemotePluginInfo(url = it.url, repoType = it.repoType) }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get remote repo URLs", e)
            Result.failure(e)
        }
    }
}
