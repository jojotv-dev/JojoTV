package com.nuvio.tv.data.local

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nuvio.tv.core.profile.ProfileManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class CachedNextUpItem(
    val contentId: String,
    val contentType: String,
    val name: String,
    val poster: String?,
    val backdrop: String?,
    val logo: String?,
    val videoId: String,
    val season: Int,
    val episode: Int,
    val episodeTitle: String?,
    val episodeDescription: String? = null,
    val thumbnail: String?,
    val released: String? = null,
    val hasAired: Boolean = true,
    val airDateLabel: String? = null,
    val lastWatched: Long,
    val imdbRating: Float? = null,
    val genres: List<String> = emptyList(),
    val releaseInfo: String? = null,
    val sortTimestamp: Long,
    val releaseTimestamp: Long? = null,
    val isReleaseAlert: Boolean = false,
    val isNewSeasonRelease: Boolean = false,
    val seedSeason: Int? = null,
    val seedEpisode: Int? = null,
    val contentLanguage: String? = null,
    val ratingVisible: Boolean = true
)

data class CachedInProgressItem(
    val contentId: String,
    val contentType: String,
    val name: String,
    val poster: String?,
    val backdrop: String?,
    val logo: String?,
    val videoId: String,
    val season: Int?,
    val episode: Int?,
    val episodeTitle: String?,
    val position: Long,
    val duration: Long,
    val lastWatched: Long,
    val progressPercent: Float?,
    val episodeThumbnail: String? = null,
    val episodeDescription: String? = null,
    val seriesDescription: String? = null,
    val episodeImdbRating: Float? = null,
    val genres: List<String> = emptyList(),
    val releaseInfo: String? = null,
    val contentLanguage: String? = null,
    val ratingVisible: Boolean = true
)

private data class ContinueWatchingDeviceSyncSnapshot(
    val version: Int = 1,
    val inProgress: List<CachedInProgressItem>? = emptyList(),
    val nextUp: List<CachedNextUpItem>? = emptyList()
)

@Singleton
class ContinueWatchingEnrichmentCache @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileManager: ProfileManager
) {
    companion object {
        private const val TAG = "CwEnrichCache"
        private const val THROTTLE_MS = 1_000L
        private const val DEVICE_SYNC_MAX_IN_PROGRESS = 80
        private const val DEVICE_SYNC_MAX_NEXT_UP = 32
    }

    private val gson = Gson()
    private val mutex = Mutex()
    @Volatile private var lastNextUpWriteMs = 0L
    @Volatile private var lastInProgressWriteMs = 0L
    @Volatile private var lastNextUpHash = 0
    @Volatile private var lastInProgressHash = 0

    /** Incremented when cache is cleared; observers can collect to trigger refresh. */
    private val _cacheCleared = kotlinx.coroutines.flow.MutableStateFlow(0)
    val cacheCleared: kotlinx.coroutines.flow.StateFlow<Int> = _cacheCleared

    /** Incremented on every successful snapshot write; channel sync observes this. */
    private val _snapshotVersion = kotlinx.coroutines.flow.MutableStateFlow(0)
    val snapshotVersion: kotlinx.coroutines.flow.StateFlow<Int> = _snapshotVersion

    // --- Next Up snapshot cache ---

    private fun nextUpFile(profileId: Int = profileManager.activeProfileId.value): File {
        val dir = File(context.filesDir, "cw_enrichment")
        dir.mkdirs()
        return File(dir, "nextup_${profileId}.json")
    }

    suspend fun getNextUpSnapshot(): List<CachedNextUpItem> =
        getNextUpSnapshot(profileManager.activeProfileId.value)

    suspend fun getNextUpSnapshot(profileId: Int): List<CachedNextUpItem> = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                val file = nextUpFile(profileId)
                if (!file.exists()) return@withContext emptyList()
                gson.fromJson(file.readText(), object : TypeToken<List<CachedNextUpItem>>() {}.type)
                    ?: emptyList()
            } catch (e: Exception) {
                Log.w(TAG, "Failed to read next-up cache: ${e.message}")
                emptyList()
            }
        }
    }

    /**
     * @param force bypass throttle and content-change check (use at end of pipeline or for clears)
     */
    suspend fun saveNextUpSnapshot(allItems: List<CachedNextUpItem>, force: Boolean = false) =
        saveNextUpSnapshot(profileManager.activeProfileId.value, allItems, force)

    suspend fun saveNextUpSnapshot(
        profileId: Int,
        allItems: List<CachedNextUpItem>,
        force: Boolean = false
    ) = withContext(Dispatchers.IO) {
        val items = allItems.filterNot { com.nuvio.tv.calendar.CalendarHomeFeed.isInjected(it.contentId, it.videoId) }
        val contentHash = items.hashCode()
        if (!force) {
            if (profileId == profileManager.activeProfileId.value && contentHash == lastNextUpHash) return@withContext
            val now = System.currentTimeMillis()
            if (now - lastNextUpWriteMs < THROTTLE_MS) return@withContext
        }
        mutex.withLock {
            try {
                val file = nextUpFile(profileId)
                atomicWrite(file, gson.toJson(items))
                if (profileId == profileManager.activeProfileId.value) {
                    lastNextUpWriteMs = System.currentTimeMillis()
                    lastNextUpHash = contentHash
                }
                _snapshotVersion.value++
            } catch (e: Exception) {
                Log.w(TAG, "Failed to write next-up cache: ${e.message}")
            }
        }
    }

    // --- In-progress snapshot cache ---

    private fun inProgressFile(profileId: Int = profileManager.activeProfileId.value): File {
        val dir = File(context.filesDir, "cw_enrichment")
        dir.mkdirs()
        return File(dir, "inprogress_${profileId}.json")
    }

    suspend fun getInProgressSnapshot(): List<CachedInProgressItem> =
        getInProgressSnapshot(profileManager.activeProfileId.value)

    suspend fun getInProgressSnapshot(profileId: Int): List<CachedInProgressItem> = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                val file = inProgressFile(profileId)
                if (!file.exists()) return@withContext emptyList()
                gson.fromJson(file.readText(), object : TypeToken<List<CachedInProgressItem>>() {}.type)
                    ?: emptyList()
            } catch (e: Exception) {
                Log.w(TAG, "Failed to read in-progress cache: ${e.message}")
                emptyList()
            }
        }
    }

    /**
     * @param force bypass throttle and content-change check (use at end of pipeline or for clears)
     */
    suspend fun saveInProgressSnapshot(allItems: List<CachedInProgressItem>, force: Boolean = false) =
        saveInProgressSnapshot(profileManager.activeProfileId.value, allItems, force)

    suspend fun saveInProgressSnapshot(
        profileId: Int,
        allItems: List<CachedInProgressItem>,
        force: Boolean = false
    ) = withContext(Dispatchers.IO) {
        val items = allItems.filterNot { com.nuvio.tv.calendar.CalendarHomeFeed.isInjectedMovie(it.contentId) }
        val contentHash = items.hashCode()
        if (!force) {
            if (profileId == profileManager.activeProfileId.value && contentHash == lastInProgressHash) return@withContext
            val now = System.currentTimeMillis()
            if (now - lastInProgressWriteMs < THROTTLE_MS) return@withContext
        }
        mutex.withLock {
            try {
                val file = inProgressFile(profileId)
                atomicWrite(file, gson.toJson(items))
                if (profileId == profileManager.activeProfileId.value) {
                    lastInProgressWriteMs = System.currentTimeMillis()
                    lastInProgressHash = contentHash
                }
                _snapshotVersion.value++
            } catch (e: Exception) {
                Log.w(TAG, "Failed to write in-progress cache: ${e.message}")
            }
        }
    }

    /** Bounded metadata snapshot used by account sync to restore artwork and episode details. */
    suspend fun exportForDeviceSync(profileId: Int): String? = withContext(Dispatchers.IO) {
        val snapshot = ContinueWatchingDeviceSyncSnapshot(
            inProgress = getInProgressSnapshot(profileId).take(DEVICE_SYNC_MAX_IN_PROGRESS),
            nextUp = getNextUpSnapshot(profileId).take(DEVICE_SYNC_MAX_NEXT_UP)
        )
        if (snapshot.inProgress.orEmpty().isEmpty() && snapshot.nextUp.orEmpty().isEmpty()) null else gson.toJson(snapshot)
    }

    /** Replaces the profile's cached enrichment with the bounded copy from the account. */
    suspend fun importForDeviceSync(profileId: Int, payload: String): Boolean = withContext(Dispatchers.IO) {
        val snapshot = runCatching {
            gson.fromJson(payload, ContinueWatchingDeviceSyncSnapshot::class.java)
        }.getOrNull() ?: return@withContext false
        if (snapshot.version != 1) return@withContext false

        // Gson can materialize these generic lists as LinkedTreeMap values in minified builds,
        // because the reflected element type is not always retained in the remote snapshot.
        // Re-decode each element with its concrete model before accessing its fields.
        val inProgress = decodeDeviceSyncItems(
            snapshot.inProgress,
            CachedInProgressItem::class.java
        )
            .filterNot { com.nuvio.tv.calendar.CalendarHomeFeed.isInjectedMovie(it.contentId) }
            .take(DEVICE_SYNC_MAX_IN_PROGRESS)
        val nextUp = decodeDeviceSyncItems(
            snapshot.nextUp,
            CachedNextUpItem::class.java
        )
            .filterNot { com.nuvio.tv.calendar.CalendarHomeFeed.isInjected(it.contentId, it.videoId) }
            .take(DEVICE_SYNC_MAX_NEXT_UP)
        if (getInProgressSnapshot(profileId) == inProgress && getNextUpSnapshot(profileId) == nextUp) {
            return@withContext false
        }

        saveInProgressSnapshot(
            profileId,
            inProgress,
            force = true
        )
        saveNextUpSnapshot(
            profileId,
            nextUp,
            force = true
        )
        true
    }

    private fun <T : Any> decodeDeviceSyncItems(items: List<*>?, itemClass: Class<T>): List<T> =
        items.orEmpty().mapNotNull { item ->
            if (item == null) return@mapNotNull null
            if (itemClass.isInstance(item)) {
                itemClass.cast(item)
            } else {
                gson.fromJson(gson.toJsonTree(item), itemClass)
                    ?: error("Could not decode ${itemClass.simpleName} from device sync")
            }
        }

    /**
     * Deletes all CW enrichment cache files for the active profile.
     */
    suspend fun clearAll() = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                nextUpFile().delete()
                inProgressFile().delete()
                lastNextUpHash = 0
                lastInProgressHash = 0
                Log.d(TAG, "Cleared CW enrichment cache for profile ${profileManager.activeProfileId.value}")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to clear CW enrichment cache: ${e.message}")
            }
        }
        _cacheCleared.value++
    }

    private fun atomicWrite(target: File, content: String) {
        val tmp = File(target.parentFile, "${target.name}.tmp")
        tmp.writeText(content)
        if (!tmp.renameTo(target)) {
            // renameTo can fail on some filesystems; fall back to copy+delete
            tmp.copyTo(target, overwrite = true)
            tmp.delete()
        }
    }

}
