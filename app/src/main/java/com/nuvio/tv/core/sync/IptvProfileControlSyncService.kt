package com.nuvio.tv.core.sync

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.nuvio.tv.core.auth.AuthManager
import com.nuvio.tv.core.profile.ProfileManager
import com.nuvio.tv.data.local.WatchProgressPreferences
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.domain.model.isIptvVodProgress
import com.nuvio.tv.domain.model.isIptvVodProgressKey
import com.nuvio.tv.reshaped.livetv.LiveTvRepository
import com.nuvio.tv.reshaped.livetv.LiveTvSource
import com.nuvio.tv.reshaped.livetv.LiveTvStorage
import com.nuvio.tv.reshaped.livetv.LiveTvVodCache
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

private const val IPTV_PROFILE_CONTROL_TAG = "IptvProfileControlSync"
private const val IPTV_CONFIG_DEBOUNCE_MS = 700L
private const val IPTV_CATALOG_DEBOUNCE_MS = 1_000L
private const val IPTV_PROGRESS_DEBOUNCE_MS = 1_000L
private const val IPTV_UNGROUPED_KEY = "\uE000"
private const val IPTV_CATALOG_LIMIT = 50_000
private const val IPTV_MAX_CATALOG_BYTES = 6_500_000
private val IPTV_CONFIG_FIELDS = setOf(
    "hiddenGroups",
    "hiddenVodCategoryKeys",
    "hiddenChannelKeys",
    "groupNames",
    "groupOrder",
    "categoryRailOrder",
    "favoriteChannelKeys",
    "favoriteVodKeys",
)
private val IPTV_CONFIG_PREFERENCE_FIELDS = mapOf(
    "hidden_groups" to "hiddenGroups",
    "hidden_vod_categories" to "hiddenVodCategoryKeys",
    "hidden_channel_keys_sync_marker" to "hiddenChannelKeys",
    "group_names" to "groupNames",
    "group_order" to "groupOrder",
    "category_rail_order" to "categoryRailOrder",
    "favorite_channel_keys" to "favoriteChannelKeys",
    "favorite_vod_keys" to "favoriteVodKeys",
)

/** Syncs IPTV viewing preferences and a display-only catalogue through the personal Supabase project. */
@Singleton
class IptvProfileControlSyncService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authManager: AuthManager,
    private val profileManager: ProfileManager,
    private val remote: IptvProfileControlRemoteDataSource,
    private val watchProgressPreferences: WatchProgressPreferences,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val preferences = context.applicationContext.getSharedPreferences(LiveTvStorage.PREFS, Context.MODE_PRIVATE)
    private val appearanceSyncPreferences = context.applicationContext.getSharedPreferences(
        "nuvio_iptv_profile_appearance_sync",
        Context.MODE_PRIVATE,
    )
    private val configSyncPreferences = context.applicationContext.getSharedPreferences(
        "nuvio_iptv_profile_control_sync",
        Context.MODE_PRIVATE,
    )
    private val progressSyncPreferences = context.applicationContext.getSharedPreferences(
        "nuvio_iptv_progress_sync",
        Context.MODE_PRIVATE,
    )
    private val syncMutex = Mutex()
    private val remoteAppliedConfigValues = ConcurrentHashMap<String, String>()
    private val configJobs = ConcurrentHashMap<Int, Job>()
    private val catalogJobs = ConcurrentHashMap<Int, Job>()
    private val progressJobs = ConcurrentHashMap<Int, Job>()

    private val preferencesListener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
        val key = changedKey ?: return@OnSharedPreferenceChangeListener
        val profileId = profileIdForConfigKey(key) ?: return@OnSharedPreferenceChangeListener
        val field = configFieldForPreferenceKey(key) ?: return@OnSharedPreferenceChangeListener
        val appliedValue = remoteAppliedConfigValues.remove(remoteAppliedKey(profileId, field))
        if (appliedValue != null && configFieldFingerprint(configFor(profileId), field) == appliedValue) {
            return@OnSharedPreferenceChangeListener
        }
        if (profileId != profileManager.activeProfileId.value) return@OnSharedPreferenceChangeListener
        markConfigFieldPending(profileId, field)
        configJobs.compute(profileId) { _, previous ->
            previous?.cancel()
            scope.launch {
                delay(IPTV_CONFIG_DEBOUNCE_MS)
                if (authManager.isAuthenticated) syncFromRemote(profileId, preferLocal = true)
            }
        }
    }

    init {
        preferences.registerOnSharedPreferenceChangeListener(preferencesListener)
        LiveTvVodCache.onCacheChanged = { requestCatalogSync() }
        scope.launch {
            profileManager.activeProfileId.collect { profileId ->
                delay(IPTV_CATALOG_DEBOUNCE_MS)
                if (authManager.isAuthenticated) syncFromRemote(profileId)
            }
        }
        scope.launch {
            LiveTvRepository.uiState
                .map { state ->
                    val profileId = profileManager.activeProfileId.value
                    val settled = LiveTvRepository.loadedStateForProfile(profileId) ?: return@map null
                    profileId to buildString {
                        settled.sources.forEach { append(it.id).append('|') }
                        settled.channels.forEach { append(it.hideKey).append(':').append(it.name).append(':').append(it.group).append('|') }
                        settled.sourceErrors.toSortedMap().forEach { (sourceId, error) -> append("error:").append(sourceId).append(':').append(error).append('|') }
                    }
                }
                .distinctUntilChanged()
                .collect { value ->
                    if (value != null) {
                        syncFromRemote(value.first)
                        requestCatalogSync(value.first)
                    }
                }
        }
    }

    suspend fun syncFromRemote(profileId: Int, preferLocal: Boolean = false): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!authManager.isAuthenticated) return@withContext Result.success(false)
        syncMutex.withLock {
            try {
                val localConfig = configFor(profileId)
                val current = withJwtRefreshRetry { remote.pull(profileId) }
                val waitForLegacyFavorites = shouldWaitForLegacyFavoriteMigration(profileId)
                if (waitForLegacyFavorites && !current.exists) {
                    Log.d(IPTV_PROFILE_CONTROL_TAG, "Waiting for IPTV channels before syncing profile $profileId")
                    return@withLock Result.success(false)
                }
                val store = LiveTvStorage(context, profileId)
                val pendingFavoriteKeyEdits = buildMap {
                    store.pendingFavoriteChannelEdit()?.let { put("favoriteChannelKeys", it) }
                    store.pendingFavoriteVodEdit()?.let { put("favoriteVodKeys", it) }
                }
                val pendingFields = pendingConfigFields(profileId) + pendingFavoriteKeyEdits.keys
                val firstSync = !hasCompletedConfigBootstrap(profileId)
                val bootstrapping = firstSync && !current.exists
                if (firstSync && current.exists) markConfigBootstrapCompleted(profileId)
                val snapshot = when {
                    bootstrapping -> {
                        val submittedConfig = JSONObject(localConfig.toString())
                        val bootstrapped = pushConfig(
                            profileId = profileId,
                            config = submittedConfig,
                            expectedRevision = current.configRevision,
                            changedFields = pendingFields,
                            bootstrapConfig = true,
                            favoriteKeyEdits = pendingFavoriteKeyEdits,
                        )
                        markConfigBootstrapCompleted(profileId)
                        clearAcceptedConfigFields(
                            profileId,
                            pendingFields,
                            snapshot = bootstrapped,
                            submittedConfig = submittedConfig,
                            pendingFavoriteKeyEdits = pendingFavoriteKeyEdits,
                        )
                        applyRemoteConfig(profileId, bootstrapped.config, fields = IPTV_CONFIG_FIELDS - pendingConfigFields(profileId))
                        bootstrapped
                    }
                    pendingFields.isNotEmpty() || preferLocal -> {
                        val fieldsToPush = pendingFields.ifEmpty { IPTV_CONFIG_FIELDS }
                        val patch = mergeConfigFields(current.config, localConfig, fieldsToPush)
                        pendingFavoriteKeyEdits.forEach { (field, edit) ->
                            if (field in fieldsToPush) applyFavoriteKeyEdit(patch, current.config, field, edit)
                        }
                        val pushed = pushConfig(
                            profileId = profileId,
                            config = patch,
                            expectedRevision = current.configRevision,
                            changedFields = fieldsToPush,
                            favoriteKeyEdits = pendingFavoriteKeyEdits,
                        )
                        clearAcceptedConfigFields(
                            profileId,
                            fieldsToPush,
                            snapshot = pushed,
                            submittedConfig = patch,
                            pendingFavoriteKeyEdits = pendingFavoriteKeyEdits,
                        )
                        applyRemoteConfig(profileId, pushed.config, fields = IPTV_CONFIG_FIELDS - pendingConfigFields(profileId))
                        pushed
                    }
                    else -> {
                        applyRemoteConfig(profileId, current.config)
                        current
                    }
                }
                val appearanceWins = hasPendingAppearanceSync(profileId) || !current.exists
                val withAppearance = syncAppearanceLocked(profileId, snapshot, preferLocal = appearanceWins)
                if (LiveTvRepository.loadedStateForProfile(profileId) != null) {
                    syncCatalogLocked(profileId, withAppearance.catalogRevision)
                }
                syncProgressLocked(profileId, withAppearance)
                if (LiveTvStorage(context, profileId).pendingFavoriteVodKeys() != null) {
                    scheduleConfigSyncRetry(profileId)
                }
                Result.success(bootstrapping || pendingFields.isNotEmpty() || preferLocal)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.w(IPTV_PROFILE_CONTROL_TAG, "IPTV profile sync failed for profile $profileId", error)
                Result.failure(error)
            }
        }
    }

    fun requestProgressSync(profileId: Int = profileManager.activeProfileId.value) {
        if (profileId !in 1..6 || !authManager.isAuthenticated) return
        progressJobs.compute(profileId) { _, previous ->
            previous?.cancel()
            scope.launch {
                delay(IPTV_PROGRESS_DEBOUNCE_MS)
                syncMutex.withLock {
                    runCatching { syncProgressLocked(profileId, null) }
                        .onFailure { Log.w(IPTV_PROFILE_CONTROL_TAG, "IPTV VOD progress sync failed for profile $profileId", it) }
                }
            }
        }
    }

    /** Persists IPTV progress edits before any network request so a later pull cannot treat them as stale cache. */
    fun queueProgressUpserts(profileId: Int, entries: Collection<WatchProgress>) {
        if (profileId !in 1..6) return
        val iptvEntries = entries.filter(WatchProgress::isIptvVodProgress)
        if (iptvEntries.isEmpty()) return
        synchronized(progressSyncPreferences) {
            val pendingKey = pendingUpsertKey(profileId)
            val pending = runCatching {
                JSONObject(progressSyncPreferences.getString(pendingKey, "{}") ?: "{}")
            }.getOrDefault(JSONObject())
            val keys = mutableSetOf<String>()
            iptvEntries.forEach { progress ->
                val key = progressKey(progress)
                keys += key
                pending.put(key, progress.toRemoteProgressJson())
            }
            val remainingDeletes = pendingProgressDeletes(profileId) - keys
            progressSyncPreferences.edit().putString(pendingKey, pending.toString()).apply {
                if (remainingDeletes.isEmpty()) remove(pendingDeleteKey(profileId))
                else putStringSet(pendingDeleteKey(profileId), remainingDeletes)
            }.apply()
        }
    }

    suspend fun deleteVodProgress(profileId: Int, progressKeys: Collection<String>): Result<Unit> = withContext(Dispatchers.IO) {
        val keys = progressKeys.map(String::trim).filter { it.isNotBlank() && isIptvVodProgressKey(it) }.distinct()
        if (keys.isEmpty()) return@withContext Result.success(Unit)
        queuePendingProgressDeletes(profileId, keys)
        if (!authManager.isAuthenticated) return@withContext Result.success(Unit)
        syncMutex.withLock {
            try {
                flushPendingProgressDeletesLocked(profileId, withJwtRefreshRetry { remote.pull(profileId) })
                Result.success(Unit)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.w(IPTV_PROFILE_CONTROL_TAG, "Could not delete IPTV VOD progress for profile $profileId", error)
                Result.failure(error)
            }
        }
    }

    suspend fun registerProfileDevice(profileId: Int, deviceId: String, deviceName: String): List<IptvProfileDevice> {
        if (!authManager.isAuthenticated) throw IOException("JojoTV account is unavailable")
        return withJwtRefreshRetry { remote.registerDevice(profileId, deviceId, deviceName) }
    }

    suspend fun pullRecordingPlan(profileId: Int, deviceId: String): IptvRecordingPlanSnapshot {
        if (!authManager.isAuthenticated) throw IOException("JojoTV account is unavailable")
        return withJwtRefreshRetry { remote.pullRecordingPlan(profileId, deviceId) }
    }

    suspend fun pushRecordingPlan(
        profileId: Int,
        deviceId: String,
        plan: JSONObject,
        expectedRevision: Long,
    ): IptvRecordingPlanSnapshot {
        if (!authManager.isAuthenticated) throw IOException("JojoTV account is unavailable")
        return withJwtRefreshRetry { remote.pushRecordingPlan(profileId, deviceId, plan, expectedRevision) }
    }

    /** Keeps avatar selection and its color on the same personal Supabase profile as IPTV controls. */
    fun markProfileAppearanceChanged(profileId: Int) {
        if (profileId in 1..6) markAppearanceSyncPending(profileId)
    }

    suspend fun syncProfileAppearance(profile: com.nuvio.tv.domain.model.UserProfile): Result<Unit> = withContext(Dispatchers.IO) {
        if (profile.id !in 1..6) return@withContext Result.failure(IllegalArgumentException("Invalid profile"))
        markAppearanceSyncPending(profile.id)
        if (!authManager.isAuthenticated) return@withContext Result.success(Unit)
        syncMutex.withLock {
            try {
                val snapshot = withJwtRefreshRetry { remote.pull(profile.id) }
                syncAppearanceLocked(profile.id, snapshot, preferLocal = true, localProfileOverride = profile)
                profileManager.updateProfile(profile)
                Result.success(Unit)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.w(IPTV_PROFILE_CONTROL_TAG, "Profile avatar sync failed for profile ${profile.id}", error)
                Result.failure(error)
            }
        }
    }

    private suspend fun syncProgressLocked(
        profileId: Int,
        initialSnapshot: IptvProfileControlSnapshot?,
    ) {
        if (!authManager.isAuthenticated) return
        var snapshot = initialSnapshot ?: withJwtRefreshRetry { remote.pull(profileId) }
        snapshot = flushPendingProgressDeletesLocked(profileId, snapshot)
        val localEntries = watchProgressPreferences.getAllRawEntries(profileId)
            .filterValues(WatchProgress::isIptvVodProgress)
        if (snapshot.progressRevision == 0L && snapshot.progress.length() == 0 &&
            snapshot.progressTombstones.length() == 0 && pendingProgressUpserts(profileId).isEmpty()
        ) {
            // Migrate legacy local-only history once, while the remote has never accepted progress.
            queueProgressUpserts(profileId, localEntries.values)
        }
        snapshot = flushPendingProgressUpsertsLocked(profileId, snapshot)

        val remoteEntries = progressFromJson(snapshot.progress)
        val tombstones = snapshot.progressTombstones.keys().asSequence().mapNotNull { key ->
            key to snapshot.progressTombstones.optLong(key).takeIf { it > 0L }
        }.toMap()
        val pendingKeys = pendingProgressUpserts(profileId).keys
        localEntries.filter { (key, progress) ->
            tombstones[key]?.let { it >= progress.lastWatched } == true ||
                (key !in remoteEntries && key !in pendingKeys)
        }.values.forEach { progress ->
            watchProgressPreferences.removeProgress(
                contentId = progress.contentId,
                season = progress.season,
                episode = progress.episode,
                profileId = profileId,
            )
        }
        val remoteWinners = remoteEntries.filter { (key, progress) ->
            (tombstones[key]?.let { it < progress.lastWatched } ?: true) && key !in pendingKeys
        }.values.toList()
        if (remoteWinners.isNotEmpty()) {
            watchProgressPreferences.saveProgressBatch(remoteWinners, profileId)
        }
    }

    private suspend fun flushPendingProgressUpsertsLocked(
        profileId: Int,
        initialSnapshot: IptvProfileControlSnapshot,
    ): IptvProfileControlSnapshot {
        var snapshot = initialSnapshot
        repeat(3) {
            val pending = pendingProgressUpserts(profileId)
            if (pending.isEmpty()) return snapshot
            val tombstones = snapshot.progressTombstones.keys().asSequence().mapNotNull { key ->
                key to snapshot.progressTombstones.optLong(key).takeIf { it > 0L }
            }.toMap()
            val blocked = pending.filter { (key, progress) ->
                tombstones[key]?.let { it >= progress.lastWatched } == true
            }
            if (blocked.isNotEmpty()) clearPendingProgressUpserts(profileId, blocked)
            val eligible = pending - blocked.keys
            if (eligible.isEmpty()) return snapshot
            snapshot = withJwtRefreshRetry {
                remote.pushProgress(
                    profileId = profileId,
                    progress = JSONArray().apply {
                        eligible.values.sortedBy(WatchProgress::lastWatched).forEach { put(it.toRemoteProgressJson()) }
                    },
                    expectedRevision = snapshot.progressRevision,
                )
            }
            if (snapshot.conflict) return@repeat
            val acceptedRemote = progressFromJson(snapshot.progress)
            val acceptedTombstones = snapshot.progressTombstones.keys().asSequence().mapNotNull { key ->
                key to snapshot.progressTombstones.optLong(key).takeIf { it > 0L }
            }.toMap()
            val accepted = eligible.filter { (key, progress) ->
                (acceptedRemote[key]?.lastWatched?.let { it >= progress.lastWatched } == true) ||
                    acceptedTombstones[key]?.let { it >= progress.lastWatched } == true
            }
            if (accepted.isNotEmpty()) clearPendingProgressUpserts(profileId, accepted)
            if (pendingProgressUpserts(profileId).isEmpty()) return snapshot
        }
        throw IOException("IPTV VOD progress changed repeatedly while syncing")
    }

    private suspend fun flushPendingProgressDeletesLocked(
        profileId: Int,
        initialSnapshot: IptvProfileControlSnapshot,
    ): IptvProfileControlSnapshot {
        val keys = pendingProgressDeletes(profileId)
        if (keys.isEmpty()) return initialSnapshot
        var snapshot = initialSnapshot
        repeat(3) {
            snapshot = withJwtRefreshRetry {
                remote.deleteProgress(profileId, keys, snapshot.progressRevision)
            }
            if (!snapshot.conflict) {
                clearPendingProgressDeletes(profileId, keys)
                return snapshot
            }
        }
        throw IOException("IPTV VOD progress changed again while deleting")
    }

    private fun pendingProgressDeletes(profileId: Int): Set<String> =
        progressSyncPreferences.getStringSet(pendingDeleteKey(profileId), emptySet()).orEmpty().toSet()

    private fun pendingProgressUpserts(profileId: Int): Map<String, WatchProgress> = synchronized(progressSyncPreferences) {
        val pendingKey = pendingUpsertKey(profileId)
        val json = runCatching {
            JSONObject(progressSyncPreferences.getString(pendingKey, "{}") ?: "{}")
        }.getOrDefault(JSONObject())
        progressFromJson(json)
    }

    private fun clearPendingProgressUpserts(profileId: Int, expected: Map<String, WatchProgress>) {
        if (expected.isEmpty()) return
        synchronized(progressSyncPreferences) {
            val pendingKey = pendingUpsertKey(profileId)
            val json = runCatching {
                JSONObject(progressSyncPreferences.getString(pendingKey, "{}") ?: "{}")
            }.getOrDefault(JSONObject())
            val current = progressFromJson(json)
            expected.forEach { (key, progress) ->
                val currentProgress = current[key] ?: return@forEach
                if (sameProgress(mapOf(key to currentProgress), mapOf(key to progress))) json.remove(key)
            }
            progressSyncPreferences.edit().apply {
                if (json.length() == 0) remove(pendingKey) else putString(pendingKey, json.toString())
            }.apply()
        }
    }

    private fun queuePendingProgressDeletes(profileId: Int, keys: Collection<String>) {
        synchronized(progressSyncPreferences) {
            val updated = pendingProgressDeletes(profileId) + keys
            val pendingKey = pendingUpsertKey(profileId)
            val pendingUpserts = runCatching {
                JSONObject(progressSyncPreferences.getString(pendingKey, "{}") ?: "{}")
            }.getOrDefault(JSONObject())
            keys.forEach(pendingUpserts::remove)
            progressSyncPreferences.edit().apply {
                if (updated.isEmpty()) remove(pendingDeleteKey(profileId))
                else putStringSet(pendingDeleteKey(profileId), updated)
                if (pendingUpserts.length() == 0) remove(pendingKey)
                else putString(pendingKey, pendingUpserts.toString())
            }.apply()
        }
    }

    private fun clearPendingProgressDeletes(profileId: Int, keys: Collection<String>) {
        synchronized(progressSyncPreferences) {
            val remaining = pendingProgressDeletes(profileId) - keys.toSet()
            progressSyncPreferences.edit().apply {
                if (remaining.isEmpty()) remove(pendingDeleteKey(profileId))
                else putStringSet(pendingDeleteKey(profileId), remaining)
            }.apply()
        }
    }

    private fun pendingDeleteKey(profileId: Int) = "pending_deletes_$profileId"
    private fun pendingUpsertKey(profileId: Int) = "pending_upserts_$profileId"

    private fun progressKey(progress: WatchProgress): String =
        if (progress.season != null && progress.episode != null) {
            "${progress.contentId}_s${progress.season}e${progress.episode}"
        } else {
            progress.contentId
        }

    private fun progressFromJson(json: JSONObject): Map<String, WatchProgress> = buildMap {
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val entry = json.optJSONObject(key) ?: continue
            val contentId = entry.optString("contentId").takeIf(String::isNotBlank) ?: continue
            val contentType = entry.optString("contentType").lowercase(Locale.ROOT)
            val season = entry.optInt("season").takeIf { entry.has("season") && it > 0 }
            val episode = entry.optInt("episode").takeIf { entry.has("episode") && it > 0 }
            val progress = WatchProgress(
                contentId = contentId,
                contentType = contentType,
                name = entry.optString("name"),
                poster = null,
                backdrop = null,
                logo = null,
                videoId = entry.optString("videoId").ifBlank { contentId },
                season = season,
                episode = episode,
                episodeTitle = entry.optString("episodeTitle").takeIf(String::isNotBlank),
                position = entry.optLong("position").coerceAtLeast(0L),
                duration = entry.optLong("duration").coerceAtLeast(0L),
                lastWatched = entry.optLong("lastWatched").coerceAtLeast(0L),
            )
            if (progress.isIptvVodProgress()) put(key, progress)
        }
    }

    private fun WatchProgress.toRemoteProgressJson(): JSONObject = JSONObject()
        .put("contentId", contentId)
        .put("contentType", contentType.lowercase(Locale.ROOT))
        .put("videoId", videoId.ifBlank { contentId })
        .put("name", name.take(512))
        .put("episodeTitle", episodeTitle.orEmpty().take(512))
        .put("season", season ?: JSONObject.NULL)
        .put("episode", episode ?: JSONObject.NULL)
        .put("position", position.coerceAtLeast(0L))
        .put("duration", duration.coerceAtLeast(0L))
        .put("lastWatched", lastWatched.coerceAtLeast(0L))

    private fun sameProgress(
        left: Map<String, WatchProgress>,
        right: Map<String, WatchProgress>,
    ): Boolean = left.size == right.size && left.all { (key, first) ->
        val second = right[key] ?: return@all false
        first.contentId == second.contentId &&
            first.contentType.equals(second.contentType, ignoreCase = true) &&
            first.videoId == second.videoId && first.season == second.season && first.episode == second.episode &&
            first.position == second.position && first.duration == second.duration &&
            first.lastWatched == second.lastWatched && first.name == second.name && first.episodeTitle == second.episodeTitle
    }

    suspend fun copyProfileConfig(sourceProfileId: Int, targetProfileId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        syncMutex.withLock {
            try {
                if (!authManager.isAuthenticated) {
                    applyRemoteConfig(targetProfileId, configFor(sourceProfileId))
                    return@withLock Result.success(true)
                }
                val source = withJwtRefreshRetry { remote.pull(sourceProfileId) }
                val config = if (source.exists) source.config else configFor(sourceProfileId)
                val target = withJwtRefreshRetry { remote.pull(targetProfileId) }
                val fieldsToCopy = IPTV_CONFIG_FIELDS.toMutableSet().apply {
                    if (config.has("profileAppearance")) add("profileAppearance")
                }
                val copied = pushConfig(
                    targetProfileId,
                    config,
                    target.configRevision,
                    changedFields = fieldsToCopy,
                )
                markConfigBootstrapCompleted(targetProfileId)
                if (profileManager.activeProfileId.value == targetProfileId) {
                    applyRemoteConfig(targetProfileId, copied.config)
                }
                Result.success(true)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.w(IPTV_PROFILE_CONTROL_TAG, "Could not copy IPTV settings from profile $sourceProfileId to $targetProfileId", error)
                Result.failure(error)
            }
        }
    }

    private suspend fun pushConfig(
        profileId: Int,
        config: JSONObject,
        expectedRevision: Long,
        changedFields: Set<String>,
        bootstrapConfig: Boolean = false,
        favoriteKeyEdits: Map<String, LiveTvStorage.PendingFavoriteKeysEdit> = emptyMap(),
    ): IptvProfileControlSnapshot {
        var result = withJwtRefreshRetry {
            remote.pushConfig(profileId, config, expectedRevision, changedFields, bootstrapConfig)
        }
        if (result.conflict) {
            val retryBootstrap = bootstrapConfig && !result.exists
            val retryConfig = if (retryBootstrap) JSONObject(config.toString())
            else mergeConfigFields(result.config, config, changedFields)
            if (!retryBootstrap) {
                favoriteKeyEdits.forEach { (field, edit) ->
                    if (field in changedFields) applyFavoriteKeyEdit(retryConfig, result.config, field, edit)
                }
            }
            if ("profileAppearance" !in changedFields && !hasPendingAppearanceSync(profileId)) {
                result.config.optJSONObject("profileAppearance")?.let { retryConfig.put("profileAppearance", JSONObject(it.toString())) }
            }
            result = withJwtRefreshRetry {
                remote.pushConfig(profileId, retryConfig, result.configRevision, changedFields, retryBootstrap)
            }
            if (result.conflict) throw IOException("IPTV profile changed again while saving")
        }
        return result
    }

    private fun applyFavoriteKeyEdit(
        targetConfig: JSONObject,
        baseConfig: JSONObject,
        field: String,
        edit: LiveTvStorage.PendingFavoriteKeysEdit,
    ) {
        val remoteKeys = stringSet(baseConfig, field)
        targetConfig.put(field, JSONArray(edit.applyTo(remoteKeys).sorted()))
    }

    private suspend fun applyRemoteConfig(profileId: Int, config: JSONObject, fields: Set<String> = IPTV_CONFIG_FIELDS) {
        val store = LiveTvStorage(context, profileId)
        val favoriteChannelKeys = config.optJSONArray("favoriteChannelKeys")
            ?.let { stringSet(config, "favoriteChannelKeys").mapNotNull(String::toLongOrNull).toSet() }
        prepareRemoteAppliedValues(profileId, config, fields)
        if ("hiddenGroups" in fields && config.optJSONArray("hiddenGroups") != null) {
            store.saveHiddenGroups(stringSet(config, "hiddenGroups").mapTo(LinkedHashSet(), ::fromControlGroupKey))
        }
        if ("hiddenVodCategoryKeys" in fields && config.optJSONArray("hiddenVodCategoryKeys") != null) {
            store.saveHiddenVodCategoryKeys(stringSet(config, "hiddenVodCategoryKeys"))
        }
        if ("hiddenChannelKeys" in fields && config.optJSONArray("hiddenChannelKeys") != null) {
            store.saveHiddenChannelKeys(stringSet(config, "hiddenChannelKeys").mapNotNull(String::toLongOrNull).toSet())
        }
        if ("groupNames" in fields && config.optJSONObject("groupNames") != null) {
            store.saveGroupNames(stringMap(config.optJSONObject("groupNames")).mapKeys { fromControlGroupKey(it.key) })
        }
        if ("groupOrder" in fields && config.optJSONArray("groupOrder") != null) {
            store.saveGroupOrder(stringList(config, "groupOrder").map(::fromControlGroupKey))
        }
        if ("categoryRailOrder" in fields && config.optJSONArray("categoryRailOrder") != null) {
            store.saveCategoryRailOrder(stringList(config, "categoryRailOrder").map(::fromControlGroupKey))
        }
        if ("favoriteChannelKeys" in fields && favoriteChannelKeys != null) {
            if (!store.applyRemoteFavoriteChannelKeys(favoriteChannelKeys)) {
                remoteAppliedConfigValues.remove(remoteAppliedKey(profileId, "favoriteChannelKeys"))
                markConfigFieldPending(profileId, "favoriteChannelKeys")
                scheduleConfigSyncRetry(profileId)
            }
        }
        if ("favoriteVodKeys" in fields && config.optJSONArray("favoriteVodKeys") != null) {
            if (!store.applyRemoteFavoriteVodKeys(stringSet(config, "favoriteVodKeys"))) {
                remoteAppliedConfigValues.remove(remoteAppliedKey(profileId, "favoriteVodKeys"))
                markConfigFieldPending(profileId, "favoriteVodKeys")
                scheduleConfigSyncRetry(profileId)
            }
        }

        LiveTvRepository.loadedStateForProfile(profileId)?.takeIf { favoriteChannelKeys != null && "favoriteChannelKeys" in fields }?.let { state ->
            store.saveFavoriteUrls(state.channels.filter { it.hideKey in favoriteChannelKeys.orEmpty() }.mapTo(LinkedHashSet()) { it.streamUrl })
        }
        LiveTvRepository.onSynchronizedPreferencesChanged(profileId)
    }

    private fun configFor(profileId: Int): JSONObject {
        val store = LiveTvStorage(context, profileId)
        val state = LiveTvRepository.loadedStateForProfile(profileId)
        val channelKeys = when {
            store.hasFavoriteChannelKeysSnapshot() -> store.favoriteChannelKeys()
            state != null && state.sourceErrors.isEmpty() -> state.favoriteChannelKeys + state.channels
                .filter { it.streamUrl in store.favoriteUrls() }
                .map { it.hideKey }
            else -> store.favoriteChannelKeys()
        }
        val config = JSONObject()
            .put("hiddenGroups", JSONArray(store.hiddenGroups().map(::toControlGroupKey)))
            .put("hiddenVodCategoryKeys", JSONArray(store.hiddenVodCategoryKeys().sorted()))
            .put("hiddenChannelKeys", JSONArray(store.hiddenChannelKeys().map(Long::toString).sorted()))
            .put("groupNames", JSONObject(store.groupNames().mapKeys { toControlGroupKey(it.key) }))
            .put("groupOrder", JSONArray(store.groupOrder().map(::toControlGroupKey)))
            .put("categoryRailOrder", JSONArray(store.categoryRailOrder().map(::toControlGroupKey)))
            .put("favoriteChannelKeys", JSONArray(channelKeys.map(Long::toString).sorted()))
            .put("favoriteVodKeys", JSONArray((store.pendingFavoriteVodKeys() ?: store.favoriteVodKeys()).sorted()))
        profileManager.profiles.value.firstOrNull { it.id == profileId }?.let { profile ->
            config.put("profileAppearance", profile.toProfileAppearanceJson())
        }
        return config
    }

    private suspend fun syncAppearanceLocked(
        profileId: Int,
        initialSnapshot: IptvProfileControlSnapshot,
        preferLocal: Boolean,
        localProfileOverride: com.nuvio.tv.domain.model.UserProfile? = null,
    ): IptvProfileControlSnapshot {
        var snapshot = initialSnapshot
        val localProfile = localProfileOverride ?: profileManager.profiles.value.firstOrNull { it.id == profileId } ?: return snapshot
        val localAppearance = localProfile.toProfileAppearanceJson()
        val pending = hasPendingAppearanceSync(profileId)
        var remoteAppearance = snapshot.config.optJSONObject("profileAppearance")

        if (!preferLocal && !pending && remoteAppearance != null) {
            applyRemoteAppearance(profileId, remoteAppearance)
            return snapshot
        }
        if (remoteAppearance?.toString() == localAppearance.toString()) {
            clearAppearanceSyncPending(profileId)
            return snapshot
        }

        repeat(3) {
            val config = if (snapshot.exists) JSONObject(snapshot.config.toString()) else configFor(profileId)
            config.put("profileAppearance", localAppearance)
            snapshot = withJwtRefreshRetry {
                remote.pushConfig(profileId, config, snapshot.configRevision, setOf("profileAppearance"))
            }
            if (!snapshot.conflict) {
                clearAppearanceSyncPending(profileId)
                return snapshot
            }
            snapshot = withJwtRefreshRetry { remote.pull(profileId) }
            remoteAppearance = snapshot.config.optJSONObject("profileAppearance")
            if (!preferLocal && !pending && remoteAppearance != null) {
                applyRemoteAppearance(profileId, remoteAppearance)
                return snapshot
            }
            val currentLocal = localProfileOverride ?: profileManager.profiles.value.firstOrNull { it.id == profileId }
                ?: return snapshot
            if (remoteAppearance?.toString() == currentLocal.toProfileAppearanceJson().toString()) {
                clearAppearanceSyncPending(profileId)
                return snapshot
            }
        }
        throw IOException("Profile appearance changed repeatedly while syncing")
    }

    private suspend fun applyRemoteAppearance(profileId: Int, appearance: JSONObject) {
        val existing = profileManager.profiles.value.firstOrNull { it.id == profileId } ?: return
        val color = appearance.optString("avatarColorHex")
            .takeIf { it.matches(Regex("#[0-9a-fA-F]{6}")) }
            ?: return
        val updated = existing.copy(
            avatarColorHex = color,
            avatarId = appearance.nullableString("avatarId"),
            avatarUrl = appearance.nullableString("avatarUrl"),
        )
        if (updated != existing) profileManager.updateProfile(updated)
    }

    private fun com.nuvio.tv.domain.model.UserProfile.toProfileAppearanceJson() = JSONObject()
        .put("avatarColorHex", avatarColorHex.takeIf { it.matches(Regex("#[0-9a-fA-F]{6}")) } ?: "#1E88E5")
        .put("avatarId", avatarId?.takeIf(String::isNotBlank) ?: JSONObject.NULL)
        .put("avatarUrl", avatarUrl?.takeIf { it.startsWith("https://", ignoreCase = true) && it.length <= 2048 } ?: JSONObject.NULL)

    private fun JSONObject.nullableString(key: String): String? =
        if (!has(key) || isNull(key)) null else optString(key).takeIf(String::isNotBlank)

    private fun hasPendingAppearanceSync(profileId: Int): Boolean =
        pendingAppearanceSyncIds().contains(profileId.toString())

    private fun pendingAppearanceSyncIds(): Set<String> = synchronized(appearanceSyncPreferences) {
        appearanceSyncPreferences.getStringSet("pending_profile_ids", emptySet()).orEmpty().toSet()
    }

    private fun markAppearanceSyncPending(profileId: Int) = synchronized(appearanceSyncPreferences) {
        appearanceSyncPreferences.edit()
            .putStringSet("pending_profile_ids", pendingAppearanceSyncIds() + profileId.toString())
            .apply()
    }

    private fun clearAppearanceSyncPending(profileId: Int) = synchronized(appearanceSyncPreferences) {
        val remaining = pendingAppearanceSyncIds() - profileId.toString()
        appearanceSyncPreferences.edit().apply {
            if (remaining.isEmpty()) remove("pending_profile_ids")
            else putStringSet("pending_profile_ids", remaining)
        }.apply()
    }

    private fun requestCatalogSync(profileId: Int = profileManager.activeProfileId.value) {
        if (profileId != profileManager.activeProfileId.value) return
        catalogJobs.compute(profileId) { _, previous ->
            previous?.cancel()
            scope.launch {
                delay(IPTV_CATALOG_DEBOUNCE_MS)
                if (authManager.isAuthenticated) {
                    syncMutex.withLock {
                        runCatching { syncCatalogLocked(profileId, null) }
                            .onFailure { Log.w(IPTV_PROFILE_CONTROL_TAG, "IPTV catalogue sync failed for profile $profileId", it) }
                    }
                }
            }
        }
    }

    private fun scheduleConfigSyncRetry(profileId: Int) {
        scope.launch {
            delay(IPTV_CONFIG_DEBOUNCE_MS)
            if (profileId == profileManager.activeProfileId.value && authManager.isAuthenticated) {
                syncFromRemote(profileId, preferLocal = true)
            }
        }
    }

    private suspend fun syncCatalogLocked(profileId: Int, knownRevision: Long?) {
        val state = LiveTvRepository.loadedStateForProfile(profileId) ?: return
        if (state.sourceErrors.isNotEmpty()) {
            Log.w(IPTV_PROFILE_CONTROL_TAG, "Skipping incomplete IPTV catalogue for profile $profileId: ${state.sourceErrors.keys}")
            return
        }
        val catalog = catalogFor(profileId, state)
        val current = withJwtRefreshRetry { remote.pull(profileId) }
        if (!current.exists) {
            if (shouldWaitForLegacyFavoriteMigration(profileId)) {
                Log.d(IPTV_PROFILE_CONTROL_TAG, "Waiting for IPTV channels before seeding profile $profileId")
                return
            }
            // Seed the preference row first; catalog writes have their own revision counter.
            val pendingFields = pendingConfigFields(profileId)
            val submittedConfig = configFor(profileId)
            val seeded = pushConfig(
                profileId,
                submittedConfig,
                current.configRevision,
                changedFields = pendingFields,
                bootstrapConfig = true,
            )
            markConfigBootstrapCompleted(profileId)
            clearAcceptedConfigFields(profileId, pendingFields, seeded, submittedConfig)
            applyRemoteConfig(profileId, seeded.config, fields = IPTV_CONFIG_FIELDS - pendingConfigFields(profileId))
            pushCatalog(profileId, catalog, seeded.catalogRevision)
            return
        }
        if (catalog.similar(current.catalog)) return
        pushCatalog(profileId, catalog, knownRevision ?: current.catalogRevision)
    }

    private suspend fun pushCatalog(profileId: Int, catalog: JSONObject, expectedRevision: Long) {
        var result = withJwtRefreshRetry { remote.pushCatalog(profileId, catalog, expectedRevision) }
        if (result.conflict) {
            val latest = withJwtRefreshRetry { remote.pull(profileId) }
            if (catalog.similar(latest.catalog)) return
            result = withJwtRefreshRetry { remote.pushCatalog(profileId, catalog, latest.catalogRevision) }
            if (result.conflict) throw IOException("IPTV catalogue changed again while saving")
        }
    }

    private fun JSONObject.similar(other: JSONObject): Boolean {
        val ownKeys = keys().asSequence().toSet()
        if (ownKeys != other.keys().asSequence().toSet()) return false
        return ownKeys.all { key -> jsonValuesSimilar(opt(key), other.opt(key)) }
    }

    private fun jsonValuesSimilar(leftValue: Any?, rightValue: Any?): Boolean {
        val left = if (leftValue === JSONObject.NULL) null else leftValue
        val right = if (rightValue === JSONObject.NULL) null else rightValue
        return when {
            left == null || right == null -> left == right
            left is JSONObject && right is JSONObject -> left.similar(right)
            left is JSONArray && right is JSONArray -> left.length() == right.length() &&
                (0 until left.length()).all { index -> jsonValuesSimilar(left.opt(index), right.opt(index)) }
            left is Number && right is Number -> {
                val leftNumber = left.toString().toBigDecimalOrNull()
                val rightNumber = right.toString().toBigDecimalOrNull()
                if (leftNumber != null && rightNumber != null) leftNumber.compareTo(rightNumber) == 0
                else left == right
            }
            else -> left == right
        }
    }

    private suspend fun catalogFor(profileId: Int, state: com.nuvio.tv.reshaped.livetv.LiveTvUiState): JSONObject {
        val store = LiveTvStorage(context, profileId)
        val synchronizedSources = store.synchronizedSources()
        val sourceIds = synchronizedSources.mapTo(HashSet(), LiveTvSource::id)
        val groups = JSONArray()
        val channels = JSONArray()
        state.groups.take(2_000).forEach { group ->
            groups.put(JSONObject()
                .put("key", toControlGroupKey(group))
                .put("kind", "tv")
                .put("sourceId", "all")
                .put("name", (state.groupNames[group]?.takeIf(String::isNotBlank) ?: group.ifBlank { "Uncategorised" }).take(180)))
        }
        state.channels.asSequence().filter { it.sourceId in sourceIds }.take(IPTV_CATALOG_LIMIT).forEach { channel ->
            channels.put(JSONObject()
                .put("key", channel.hideKey.toString())
                .put("sourceId", channel.sourceId)
                .put("name", channel.name.take(180))
                .put("group", toControlGroupKey(channel.group)))
        }

        val vodItems = JSONArray()
        val vodGroups = LinkedHashMap<String, JSONObject>()
        for (source in synchronizedSources) {
            for (type in listOf("movie", "series")) {
                LiveTvVodCache.readCategories(context, source.id, type).forEach { category ->
                    val key = LiveTvRepository.vodCategoryHideKey(type, source.id, category.id)
                    vodGroups.putIfAbsent(key, JSONObject()
                        .put("key", key).put("kind", type).put("sourceId", source.id).put("name", category.name.take(180)))
                }
                val remaining = (IPTV_CATALOG_LIMIT - vodItems.length()).coerceAtLeast(0)
                if (remaining == 0) break
                LiveTvVodCache.read(context, source.id, type).take(remaining).forEach { item ->
                    if (item.categoryId.isNotBlank()) {
                        val key = LiveTvRepository.vodCategoryHideKey(type, source.id, item.categoryId)
                        vodGroups.putIfAbsent(key, JSONObject()
                            .put("key", key).put("kind", type).put("sourceId", source.id)
                            .put("name", item.categoryName.take(180).ifBlank { "Uncategorised" }))
                    }
                    vodItems.put(JSONObject()
                        .put("key", item.favoriteKey)
                        .put("sourceId", item.sourceId)
                        .put("type", item.type)
                        .put("title", item.title.take(180))
                        .put("categoryId", item.categoryId.take(180))
                        .put("categoryName", item.categoryName.take(180))
                        .put("year", item.year.take(16)))
                }
            }
        }
        vodGroups.values.take(3_000).forEach(groups::put)
        val catalog = JSONObject().put("groups", groups).put("channels", channels).put("vodItems", vodItems)
        var catalogBytes = catalog.toString().toByteArray(Charsets.UTF_8).size
        while (catalogBytes > IPTV_MAX_CATALOG_BYTES && vodItems.length() > 0) {
            val removeCount = maxOf(1, ((vodItems.length() * (catalogBytes - IPTV_MAX_CATALOG_BYTES).toDouble() / catalogBytes).toInt() + 1))
                .coerceAtMost(vodItems.length())
            repeat(removeCount) { vodItems.remove(vodItems.length() - 1) }
            catalogBytes = catalog.toString().toByteArray(Charsets.UTF_8).size
        }
        while (catalogBytes > IPTV_MAX_CATALOG_BYTES && channels.length() > 0) {
            val removeCount = maxOf(1, ((channels.length() * (catalogBytes - IPTV_MAX_CATALOG_BYTES).toDouble() / catalogBytes).toInt() + 1))
                .coerceAtMost(channels.length())
            repeat(removeCount) { channels.remove(channels.length() - 1) }
            catalogBytes = catalog.toString().toByteArray(Charsets.UTF_8).size
        }
        return catalog
    }

    private suspend fun <T> withJwtRefreshRetry(block: suspend () -> T): T = try {
        block()
    } catch (error: Exception) {
        if (!authManager.refreshSessionIfJwtExpired(error)) throw error
        block()
    }

    private fun profileIdForConfigKey(key: String): Int? {
        val profileId = key.substringAfterLast('_').toIntOrNull() ?: return null
        if (profileId !in 1..6 || !key.endsWith("_$profileId")) return null
        return profileId.takeIf { key.removeSuffix("_$it") in IPTV_CONFIG_PREFERENCE_FIELDS }
    }

    private fun configFieldForPreferenceKey(key: String): String? {
        val profileId = profileIdForConfigKey(key) ?: return null
        return IPTV_CONFIG_PREFERENCE_FIELDS[key.removeSuffix("_$profileId")]
    }

    private fun pendingConfigFields(profileId: Int): Set<String> {
        val fields = synchronized(configSyncPreferences) {
            configSyncPreferences.getStringSet(pendingConfigFieldsKey(profileId), emptySet()).orEmpty().toSet()
        }
        return if (LiveTvStorage(context, profileId).pendingFavoriteVodKeys() != null) {
            fields + "favoriteVodKeys"
        } else {
            fields
        }
    }

    private fun markConfigFieldPending(profileId: Int, field: String) = synchronized(configSyncPreferences) {
        val key = pendingConfigFieldsKey(profileId)
        val fields = configSyncPreferences.getStringSet(key, emptySet()).orEmpty().toMutableSet()
        fields += field
        configSyncPreferences.edit().putStringSet(key, fields).apply()
    }

    private fun clearPendingConfigFields(profileId: Int, fields: Set<String>) = synchronized(configSyncPreferences) {
        if (fields.isEmpty()) return@synchronized
        val key = pendingConfigFieldsKey(profileId)
        val remaining = configSyncPreferences.getStringSet(key, emptySet()).orEmpty().toMutableSet()
        remaining.removeAll(fields)
        val editor = configSyncPreferences.edit()
        if (remaining.isEmpty()) editor.remove(key) else editor.putStringSet(key, remaining)
        editor.apply()
    }

    private fun clearAcceptedConfigFields(
        profileId: Int,
        fields: Set<String>,
        snapshot: IptvProfileControlSnapshot,
        submittedConfig: JSONObject? = null,
        pendingFavoriteKeyEdits: Map<String, LiveTvStorage.PendingFavoriteKeysEdit> = emptyMap(),
    ) {
        if (fields.isEmpty()) return
        val local = configFor(profileId)
        val store = LiveTvStorage(context, profileId)
        val accepted = fields.filterTo(mutableSetOf()) { field ->
            val pendingEdit = pendingFavoriteKeyEdits[field]
            if (pendingEdit != null) {
                pendingFavoriteKeyEdit(store, field) == pendingEdit &&
                    submittedConfig?.let { stringSet(it, field) } == stringSet(snapshot.config, field)
            } else {
                configFieldFingerprint(local, field) == configFieldFingerprint(snapshot.config, field)
            }
        }
        pendingFavoriteKeyEdits.forEach { (field, edit) ->
            if (field in accepted && !clearPendingFavoriteKeyEdit(store, field, edit)) {
                accepted.remove(field)
            }
        }
        clearPendingConfigFields(profileId, accepted)
    }

    private fun pendingFavoriteKeyEdit(
        store: LiveTvStorage,
        field: String,
    ): LiveTvStorage.PendingFavoriteKeysEdit? = when (field) {
        "favoriteChannelKeys" -> store.pendingFavoriteChannelEdit()
        "favoriteVodKeys" -> store.pendingFavoriteVodEdit()
        else -> null
    }

    private fun clearPendingFavoriteKeyEdit(
        store: LiveTvStorage,
        field: String,
        edit: LiveTvStorage.PendingFavoriteKeysEdit,
    ): Boolean = when (field) {
        "favoriteChannelKeys" -> store.clearPendingFavoriteChannelEdit(edit)
        "favoriteVodKeys" -> store.clearPendingFavoriteVodEdit(edit)
        else -> false
    }

    private fun hasCompletedConfigBootstrap(profileId: Int): Boolean =
        configSyncPreferences.getBoolean(configBootstrapKey(profileId), false)

    private fun markConfigBootstrapCompleted(profileId: Int) {
        configSyncPreferences.edit().putBoolean(configBootstrapKey(profileId), true).commit()
    }

    private fun mergeConfigFields(base: JSONObject, desired: JSONObject, fields: Set<String>): JSONObject {
        val merged = JSONObject(base.toString())
        fields.forEach { field ->
            if (!desired.has(field)) return@forEach
            val value = desired.opt(field)
            merged.put(field, when (value) {
                is JSONObject -> JSONObject(value.toString())
                is JSONArray -> JSONArray(value.toString())
                else -> value
            })
        }
        return merged
    }

    private fun configFieldFingerprint(config: JSONObject, field: String): String {
        return when (val value = config.opt(field)) {
            is JSONArray -> {
                val items = stringList(config, field)
                val normalized = if (field in setOf(
                        "hiddenGroups", "hiddenVodCategoryKeys", "hiddenChannelKeys", "favoriteChannelKeys", "favoriteVodKeys",
                    )) items.sorted() else items
                normalized.joinToString("\u0000")
            }
            is JSONObject -> stringMap(value).toSortedMap().entries.joinToString("\u0000") { "${it.key}=${it.value}" }
            null, JSONObject.NULL -> "<missing>"
            else -> value.toString()
        }
    }

    private fun remoteAppliedKey(profileId: Int, field: String) = "$profileId:$field"
    private fun pendingConfigFieldsKey(profileId: Int) = "pending_config_fields_$profileId"
    private fun configBootstrapKey(profileId: Int) = "config_bootstrap_completed_$profileId"

    private fun configFieldPreferenceSnapshot(config: JSONObject, field: String): String =
        configFieldFingerprint(config, field)

    private fun prepareRemoteAppliedValues(profileId: Int, config: JSONObject, fields: Set<String>) {
        fields.forEach { field ->
            if (config.has(field)) {
                remoteAppliedConfigValues[remoteAppliedKey(profileId, field)] = configFieldPreferenceSnapshot(config, field)
            }
        }
    }

    private fun shouldWaitForLegacyFavoriteMigration(profileId: Int): Boolean {
        val store = LiveTvStorage(context, profileId)
        if (store.hasFavoriteChannelKeysSnapshot() || store.favoriteUrls().isEmpty()) return false
        val state = LiveTvRepository.loadedStateForProfile(profileId) ?: return true
        return state.sourceErrors.isNotEmpty()
    }

    private fun stringList(json: JSONObject, key: String): List<String> {
        val array = json.optJSONArray(key) ?: return emptyList()
        return (0 until array.length()).mapNotNull { array.optString(it).takeIf(String::isNotBlank) }
    }

    private fun stringSet(json: JSONObject, key: String): Set<String> = stringList(json, key).toSet()

    private fun stringMap(json: JSONObject?): Map<String, String> {
        if (json == null) return emptyMap()
        return json.keys().asSequence().mapNotNull { key ->
            json.optString(key).takeIf(String::isNotBlank)?.let { key to it }
        }.toMap()
    }

    private fun toControlGroupKey(group: String): String = if (group.isEmpty()) IPTV_UNGROUPED_KEY else group
    private fun fromControlGroupKey(group: String): String = if (group == IPTV_UNGROUPED_KEY) "" else group
}
