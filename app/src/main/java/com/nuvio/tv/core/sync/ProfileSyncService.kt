package com.nuvio.tv.core.sync

import android.os.SystemClock
import android.util.Log
import com.nuvio.tv.core.auth.AuthManager
import com.nuvio.tv.core.profile.ProfileManager
import com.nuvio.tv.data.remote.supabase.AvatarRepository
import com.nuvio.tv.data.remote.supabase.SupabaseProfileLockState
import com.nuvio.tv.data.remote.supabase.SupabaseProfile
import com.nuvio.tv.data.remote.supabase.SupabaseProfilePinVerifyResult
import com.nuvio.tv.domain.model.UserProfile
import com.nuvio.tv.domain.model.AuthState
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ProfileSyncService"
private const val PROFILE_PULL_MIN_INTERVAL_MS = 10_000L
private const val BUNDLED_CUSTOM_AVATAR_ID_PREFIX = "custom_"

internal data class ProfilePullFreshness(
    val userId: String? = null,
    val pulledAtMs: Long = 0L
) {
    fun isRecent(candidateUserId: String, nowMs: Long): Boolean {
        return userId == candidateUserId &&
            pulledAtMs > 0L &&
            nowMs >= pulledAtMs &&
            nowMs - pulledAtMs < PROFILE_PULL_MIN_INTERVAL_MS
    }
}

sealed class SetProfilePinResult {
    object Success : SetProfilePinResult()
    object CurrentPinRequired : SetProfilePinResult()
    data class Failure(val throwable: Throwable) : SetProfilePinResult()
}

@Singleton
class ProfileSyncService @Inject constructor(
    private val authManager: AuthManager,
    private val postgrest: Postgrest,
    private val profileManager: ProfileManager,
    private val avatarRepository: AvatarRepository,
    private val syncClientIdentity: SyncClientIdentity
) {
    // Serialize profile writes with pulls. A startup/realtime pull must not apply an old
    // snapshot after a profile editor has saved a newer avatar locally.
    private val profileSyncMutex = Mutex()
    private var pullFreshness = ProfilePullFreshness()
    private var lastPulledProfiles = emptyList<UserProfile>()

    private suspend fun <T> withJwtRefreshRetry(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            if (!authManager.refreshSessionIfJwtExpired(e)) throw e
            block()
        }
    }

    suspend fun saveProfile(profile: UserProfile): Boolean = withContext(Dispatchers.IO) {
        profileSyncMutex.withLock {
            try {
                if (!profileManager.updateProfileForRemoteSync(profile)) {
                    Log.e(TAG, "Profile ${profile.id} no longer exists; refusing to save the edit")
                    return@withLock false
                }
                profileManager.profiles.first { current -> current.any { it == profile } }

                pushToRemoteLocked().onFailure { error ->
                    // The durable local edit stays protected from remote replacement until a
                    // later push succeeds; pullFromRemote will retry it after merging.
                    Log.w(TAG, "Profile ${profile.id} was saved locally; remote sync remains pending", error)
                }
                true
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e(TAG, "Failed to save profile ${profile.id}", error)
                false
            }
        }
    }

    suspend fun pushToRemote(): Result<Unit> = withContext(Dispatchers.IO) {
        profileSyncMutex.withLock { pushToRemoteLocked() }
    }

    private suspend fun pushToRemoteLocked(): Result<Unit> {
        return try {
            val profiles = profileManager.profiles.value
            val bundledCustomAvatarUrls = mutableMapOf<Int, String>()
            for (profile in profiles) {
                if (profile.avatarId?.startsWith(BUNDLED_CUSTOM_AVATAR_ID_PREFIX) == true) {
                    avatarRepository.shareableImageUrl(profile)?.let { avatarUrl ->
                        bundledCustomAvatarUrls[profile.id] = avatarUrl
                    }
                }
            }

            val params = buildJsonObject {
                put("p_client_max_profiles", ProfileManager.MAX_PROFILES)
                put("p_profiles", buildJsonArray {
                    profiles.forEach { profile ->
                        addJsonObject {
                            val avatarUrl = bundledCustomAvatarUrls[profile.id]
                                ?: profile.avatarUrl?.takeIf { it.isNotBlank() }
                            put("profile_index", profile.id)
                            put("name", profile.name)
                            put("avatar_color_hex", profile.avatarColorHex)
                            put("uses_primary_addons", profile.usesPrimaryAddons)
                            put("uses_primary_plugins", profile.usesPrimaryPlugins)
                            put("avatar_id", if (avatarUrl == null) profile.avatarId else null)
                            put("avatar_url", avatarUrl)
                            put("profile_background_id", profile.profileBackgroundId)
                            put("profile_background_url", profile.profileBackgroundUrl?.takeIf { it.isNotBlank() })
                        }
                    }
                })
                putSyncOriginClientId(syncClientIdentity)
            }
            withJwtRefreshRetry {
                postgrest.rpc("sync_push_profiles", params)
            }

            profileManager.clearPendingRemoteProfileSyncIds()
            Log.d(TAG, "Pushed ${profiles.size} profiles to remote")
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to push profiles to remote", e)
            Result.failure(e)
        }
    }

    suspend fun pullFromRemote(force: Boolean = false): Result<List<UserProfile>> = withContext(Dispatchers.IO) {
        profileSyncMutex.withLock {
            val userId = (authManager.authState.value as? AuthState.FullAccount)?.userId
            val now = SystemClock.elapsedRealtime()
            if (!force && userId != null && pullFreshness.isRecent(userId, now)) {
                return@withLock Result.success(lastPulledProfiles)
            }
            try {
                val response = withJwtRefreshRetry {
                    postgrest.rpc("sync_pull_profiles")
                }
                val remote = response.decodeList<SupabaseProfile>()

                Log.d(TAG, "pullFromRemote: fetched ${remote.size} profiles from Supabase")

                val fetchedProfiles = remote.map { entry ->
                    UserProfile(
                        id = entry.profileIndex,
                        name = entry.name,
                        avatarColorHex = entry.avatarColorHex,
                        usesPrimaryAddons = entry.usesPrimaryAddons,
                        usesPrimaryPlugins = entry.usesPrimaryPlugins,
                        avatarId = entry.avatarId,
                        avatarUrl = entry.avatarUrl,
                        profileBackgroundId = entry.profileBackgroundId,
                        profileBackgroundUrl = entry.profileBackgroundUrl
                    )
                }

                val localProfiles = profileManager.profiles.value
                val localById = localProfiles.associateBy(UserProfile::id)
                val pendingIds = profileManager.pendingRemoteProfileSyncIds()
                val mergedProfiles = if (fetchedProfiles.isEmpty()) {
                    // Keep the existing non-empty local list if the server returns an empty snapshot.
                    localProfiles
                } else {
                    val fetchedIds = fetchedProfiles.mapTo(mutableSetOf(), UserProfile::id)
                    fetchedProfiles.map { remoteProfile ->
                        if (remoteProfile.id in pendingIds) {
                            localById[remoteProfile.id] ?: remoteProfile
                        } else {
                            remoteProfile
                        }
                    } + pendingIds
                        .filterNot(fetchedIds::contains)
                        .mapNotNull(localById::get)
                }

                if (fetchedProfiles.isNotEmpty()) {
                    profileManager.replaceAllProfiles(mergedProfiles)
                    Log.d(TAG, "Merged ${mergedProfiles.size} remote/local profiles into local store")
                }

                if (pendingIds.isNotEmpty()) {
                    profileManager.profiles.first { current ->
                        pendingIds.all { id ->
                            localById[id]?.let { local -> current.any { it == local } } ?: true
                        }
                    }
                    pushToRemoteLocked().onFailure { error ->
                        Log.w(TAG, "Retrying pending local profile edits failed", error)
                    }
                }

                val currentUserId = (authManager.authState.value as? AuthState.FullAccount)?.userId
                if (userId != null && currentUserId == userId) {
                    lastPulledProfiles = mergedProfiles
                    pullFreshness = ProfilePullFreshness(
                        userId = userId,
                        pulledAtMs = SystemClock.elapsedRealtime()
                    )
                }
                Result.success(mergedProfiles)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to pull profiles from remote", e)
                Result.failure(e)
            }
        }
    }

    suspend fun deleteProfileData(profileId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("p_profile_id", profileId)
                putSyncOriginClientId(syncClientIdentity)
            }
            withJwtRefreshRetry {
                postgrest.rpc("sync_delete_profile_data", params)
            }

            Log.d(TAG, "Deleted remote data for profile $profileId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete remote profile data for profile $profileId", e)
            Result.failure(e)
        }
    }

    suspend fun pullProfileLockStates(): Result<Map<Int, Boolean>> = withContext(Dispatchers.IO) {
        try {
            val response = withJwtRefreshRetry {
                postgrest.rpc("sync_pull_profile_locks")
            }
            val remote = response.decodeList<SupabaseProfileLockState>()
            val result = remote.associate { it.profileIndex to it.pinEnabled }
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to pull profile lock states", e)
            Result.failure(e)
        }
    }

    suspend fun setProfilePin(profileId: Int, pin: String, currentPin: String? = null): SetProfilePinResult = withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("p_profile_id", profileId)
                put("p_pin", pin)
                if (!currentPin.isNullOrBlank()) {
                    put("p_current_pin", currentPin)
                }
            }
            withJwtRefreshRetry {
                postgrest.rpc("set_profile_pin", params)
            }
            SetProfilePinResult.Success
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set profile PIN", e)
            if (isCurrentPinRequiredError(e)) {
                SetProfilePinResult.CurrentPinRequired
            } else {
                SetProfilePinResult.Failure(e)
            }
        }
    }

    private fun isCurrentPinRequiredError(e: Throwable): Boolean =
        e.message?.contains("Current PIN is required", ignoreCase = true) == true

    suspend fun clearProfilePin(profileId: Int, currentPin: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("p_profile_id", profileId)
                if (!currentPin.isNullOrBlank()) {
                    put("p_current_pin", currentPin)
                }
            }
            withJwtRefreshRetry {
                postgrest.rpc("clear_profile_pin", params)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear profile PIN", e)
            Result.failure(e)
        }
    }

    suspend fun verifyProfilePin(profileId: Int, pin: String): Result<SupabaseProfilePinVerifyResult> = withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("p_profile_id", profileId)
                put("p_pin", pin)
            }
            val response = withJwtRefreshRetry {
                postgrest.rpc("verify_profile_pin", params)
            }
            val decoded = response.decodeList<SupabaseProfilePinVerifyResult>().firstOrNull()
                ?: SupabaseProfilePinVerifyResult(unlocked = false, retryAfterSeconds = 0)
            Result.success(decoded)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to verify profile PIN", e)
            Result.failure(e)
        }
    }
}
