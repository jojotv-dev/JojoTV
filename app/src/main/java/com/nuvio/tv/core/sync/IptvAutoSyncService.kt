package com.nuvio.tv.core.sync

import android.os.SystemClock
import android.util.Log
import com.streamvault.domain.repository.ProviderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "IptvAutoSyncService"

/** Délai minimum entre deux syncs automatiques du même provider (4 heures). */
private const val IPTV_PROVIDER_SYNC_TTL_MS = 4 * 60 * 60 * 1000L

/** Cooldown entre deux cycles complets (évite les appels répétés sur onResume). */
private const val IPTV_CYCLE_COOLDOWN_MS = 30_000L

@Singleton
class IptvAutoSyncService @Inject constructor(
    private val providerRepository: ProviderRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile private var lastCycleAt: Long = 0L
    private var syncJob: Job? = null

    /**
     * Déclenche la sync automatique de tous les providers IPTV actifs.
     * - Ignoré si un cycle tourne déjà ou si le cooldown n'est pas écoulé.
     * - Chaque provider est synced uniquement si [lastSyncedAt] dépasse le TTL.
     * - [force] = true ignore TTL et cooldown (ex : sync manuelle).
     */
    fun requestAutoSync(force: Boolean = false) {
        val now = SystemClock.elapsedRealtime()

        if (!force) {
            if (syncJob?.isActive == true) {
                Log.d(TAG, "Auto-sync already running, skipping")
                return
            }
            if (now - lastCycleAt < IPTV_CYCLE_COOLDOWN_MS) {
                Log.d(TAG, "Auto-sync cooldown active (${now - lastCycleAt}ms < ${IPTV_CYCLE_COOLDOWN_MS}ms), skipping")
                return
            }
        }

        syncJob = scope.launch {
            lastCycleAt = SystemClock.elapsedRealtime()
            try {
                val providers = providerRepository.getProviders().first()
                if (providers.isEmpty()) {
                    Log.d(TAG, "No IPTV providers found, nothing to sync")
                    return@launch
                }

                Log.d(TAG, "Starting auto-sync cycle for ${providers.size} provider(s)")

                for (provider in providers) {
                    if (!provider.isActive && provider.lastSyncedAt > 0L) {
                        Log.d(TAG, "Provider ${provider.id} (${provider.name}) is inactive and already synced once, skipping")
                        continue
                    }

                    val ageMs = System.currentTimeMillis() - provider.lastSyncedAt
                    if (!force && ageMs < IPTV_PROVIDER_SYNC_TTL_MS) {
                        Log.d(TAG, "Provider ${provider.id} (${provider.name}) synced ${ageMs / 1000}s ago, skipping (TTL=${IPTV_PROVIDER_SYNC_TTL_MS / 3600_000}h)")
                        continue
                    }

                    Log.d(TAG, "Auto-syncing provider ${provider.id} (${provider.name}), last sync ${ageMs / 1000}s ago")
                    val result = providerRepository.refreshProviderData(
                        providerId = provider.id,
                        force = false
                    )
                    when (result) {
                        is com.streamvault.domain.model.Result.Success ->
                            Log.d(TAG, "Auto-sync OK: provider ${provider.id} (${provider.name})")
                        is com.streamvault.domain.model.Result.Error ->
                            Log.w(TAG, "Auto-sync failed: provider ${provider.id} (${provider.name}) — ${result.message}")
                        else -> Unit
                    }
                }

                Log.d(TAG, "Auto-sync cycle complete")
            } catch (e: Exception) {
                Log.e(TAG, "Auto-sync cycle error", e)
            }
        }
    }
}