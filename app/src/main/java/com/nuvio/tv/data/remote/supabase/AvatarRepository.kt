package com.nuvio.tv.data.remote.supabase

import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.nuvio.tv.data.local.MemberCatalogStorage
import com.nuvio.tv.domain.model.ServerConfiguration
import com.nuvio.tv.domain.model.UserProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.days

private const val MemberAvatarBucket = "membership-profile-avatars"
private const val MemberAvatarTag = "MemberAvatars"
private const val AvatarCatalogRefreshIntervalMs = 15 * 60_000L
private const val BundledAvatarCatalogFile = "custom_avatar_catalog.json"

internal fun isAvatarCatalogRefreshDue(lastRefreshAtMs: Long, nowMs: Long): Boolean {
    return lastRefreshAtMs <= 0L ||
        nowMs < lastRefreshAtMs ||
        nowMs - lastRefreshAtMs >= AvatarCatalogRefreshIntervalMs
}

@Serializable
private data class StoredAvatarCatalogPayload(
    val standardItems: List<SupabaseAvatarCatalogItem> = emptyList(),
    val memberItems: List<SupabaseMemberAvatarCatalogItem> = emptyList(),
    val standardLoaded: Boolean = false,
    val memberLoaded: Boolean = false
)

data class AvatarCatalogItem(
    val id: String,
    val displayName: String,
    val imageUrl: String,
    val category: String,
    val sortOrder: Int,
    val bgColor: String? = null,
    val memberOnly: Boolean = false
)

@Singleton
class AvatarRepository @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage,
    private val serverConfiguration: ServerConfiguration,
    private val memberCatalogStorage: MemberCatalogStorage,
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val catalogInitializationMutex = Mutex()
    @Volatile private var catalogInitialized = false
    private var bundledCustomAvatarItems = emptyList<SupabaseAvatarCatalogItem>()
    private var standardMetadata = emptyList<SupabaseAvatarCatalogItem>()
    private var memberMetadata = emptyList<SupabaseMemberAvatarCatalogItem>()
    private var standardCatalogLoaded = false
    private var memberCatalogLoaded = false
    private var cachedStandardCatalog: List<AvatarCatalogItem>? = null
    private var cachedMemberCatalog: List<AvatarCatalogItem>? = null
    private var standardRefreshJob: Job? = null
    private var memberRefreshJob: Job? = null
    private var lastStandardRefreshAtMs = 0L
    private var lastMemberRefreshAtMs = 0L

    // This singleton is injected during Application startup, so decode the persisted JSON off-main.
    init {
        scope.launch {
            try {
                ensureCatalogInitialized()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.w(MemberAvatarTag, "Unable to initialize cached avatar catalog", error)
            }
        }
    }

    suspend fun getAvatarCatalog(hasMemberAccess: Boolean = false): List<AvatarCatalogItem> {
        ensureCatalogInitialized()
        val hadStandardCache = cachedStandardCatalog != null
        val standardCatalog = getStandardAvatarCatalog()
        if (hadStandardCache) refreshStandardCatalogInBackground()
        if (!hasMemberAccess) return standardCatalog
        val hadMemberCache = cachedMemberCatalog != null
        val memberCatalog = getMemberAvatarCatalog()
        if (hadMemberCache) refreshMemberCatalogInBackground()
        return standardCatalog + memberCatalog
    }

    private suspend fun getStandardAvatarCatalog(): List<AvatarCatalogItem> {
        cachedStandardCatalog?.let { return it }
        return fetchStandardAvatarCatalog()
    }

    private suspend fun fetchStandardAvatarCatalog(): List<AvatarCatalogItem> {
        val response = postgrest.rpc("get_avatar_catalog")
        val remote = response.decodeList<SupabaseAvatarCatalogItem>()
        standardMetadata = (remote + bundledCustomAvatarItems).distinctBy { it.id }
        standardCatalogLoaded = true
        val catalog = standardMetadata.map(::toStandardCatalogItem)
        cachedStandardCatalog = catalog
        lastStandardRefreshAtMs = SystemClock.elapsedRealtime()
        saveStoredCatalog()
        return catalog
    }

    private suspend fun getMemberAvatarCatalog(): List<AvatarCatalogItem> {
        cachedMemberCatalog?.let { return it }
        return fetchMemberAvatarCatalog()
    }

    private suspend fun fetchMemberAvatarCatalog(): List<AvatarCatalogItem> {
        val remote = try {
            postgrest.rpc("get_member_profile_avatar_catalog")
                .decodeList<SupabaseMemberAvatarCatalogItem>()
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.w(MemberAvatarTag, "Unable to load supporter avatar catalog", error)
            return emptyList()
        }
        memberMetadata = remote
        memberCatalogLoaded = true
        lastMemberRefreshAtMs = SystemClock.elapsedRealtime()
        saveStoredCatalog()
        val downloadSemaphore = Semaphore(6)
        val catalog = coroutineScope {
            remote.map { item ->
                async {
                    downloadSemaphore.withPermit {
                        try {
                            toMemberCatalogItem(item, cacheMemberAvatar(item))
                        } catch (error: CancellationException) {
                            throw error
                        } catch (error: Exception) {
                            Log.w(MemberAvatarTag, "Unable to load supporter avatar ${item.id}", error)
                            toMemberCatalogFallbackItem(item)
                        }
                    }
                }
            }.awaitAll()
        }
        cachedMemberCatalog = catalog
        return catalog
    }

    suspend fun shareableImageUrl(profile: UserProfile): String? {
        ensureCatalogInitialized()
        val storageUrl = "${serverConfiguration.backendUrl.trimEnd('/')}/storage/v1/object/"
        profile.avatarUrl?.takeIf { it.isNotBlank() }?.let { url ->
            return url.takeIf { it.startsWith(storageUrl) }
        }
        val avatarId = profile.avatarId ?: return null
        val standard = standardMetadata.firstOrNull { it.id == avatarId }
        if (standard != null) {
            val imageUrl = avatarImageUrl(standard.storagePath)
            return imageUrl.takeIf {
                it.startsWith(storageUrl) || bundledCustomAvatarItems.any { custom -> custom.id == standard.id }
            }
        }
        val avatar = memberMetadata.firstOrNull { it.id == avatarId } ?: return null
        val sanitizedPath = avatar.storagePath.trim().trimStart('/')
        return try {
            storage[MemberAvatarBucket].createSignedUrl(sanitizedPath, 1.days)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.w(MemberAvatarTag, "Unable to share supporter avatar ${avatar.id}", error)
            null
        }
    }

    fun getAvatarImageUrl(avatarId: String, catalog: List<AvatarCatalogItem>): String? {
        return catalog.find { it.id == avatarId }?.imageUrl
            ?: latestCachedMemberAvatar(avatarId)?.toFileUriString()
            ?: memberMetadata.takeIf { catalogInitialized }
                ?.firstOrNull { it.id == avatarId }
                ?.let { toMemberCatalogFallbackItem(it).imageUrl }
    }

    fun invalidateCache() {
        standardRefreshJob?.cancel()
        standardRefreshJob = null
        memberRefreshJob?.cancel()
        memberRefreshJob = null
        cachedStandardCatalog = null
        cachedMemberCatalog = null
        standardMetadata = emptyList()
        memberMetadata = emptyList()
        standardCatalogLoaded = false
        memberCatalogLoaded = false
        lastStandardRefreshAtMs = 0L
        lastMemberRefreshAtMs = 0L
    }

    private fun refreshStandardCatalogInBackground() {
        if (standardRefreshJob?.isActive == true) return
        if (!isAvatarCatalogRefreshDue(lastStandardRefreshAtMs, SystemClock.elapsedRealtime())) return
        standardRefreshJob = scope.launch {
            try {
                fetchStandardAvatarCatalog()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.w(MemberAvatarTag, "Unable to refresh avatar catalog", error)
            }
        }
    }

    private fun refreshMemberCatalogInBackground() {
        if (memberRefreshJob?.isActive == true) return
        if (!isAvatarCatalogRefreshDue(lastMemberRefreshAtMs, SystemClock.elapsedRealtime())) return
        memberRefreshJob = scope.launch {
            try {
                fetchMemberAvatarCatalog()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.w(MemberAvatarTag, "Unable to refresh supporter avatar catalog", error)
            }
        }
    }

    private suspend fun ensureCatalogInitialized() {
        if (catalogInitialized) return
        catalogInitializationMutex.withLock {
            if (catalogInitialized) return@withLock
            withContext(Dispatchers.IO) {
                val storedCatalog = loadStoredCatalog()
                bundledCustomAvatarItems = loadBundledCustomAvatarCatalog()
                standardMetadata =
                    (storedCatalog?.standardItems.orEmpty() + bundledCustomAvatarItems).distinctBy { it.id }
                memberMetadata = storedCatalog?.memberItems.orEmpty()
                standardCatalogLoaded = storedCatalog?.standardLoaded == true
                memberCatalogLoaded = storedCatalog?.memberLoaded == true
                cachedStandardCatalog = if (standardCatalogLoaded) {
                    standardMetadata.map(::toStandardCatalogItem)
                } else {
                    null
                }
                cachedMemberCatalog = if (memberCatalogLoaded) {
                    memberMetadata.mapNotNull(::loadCachedMemberAvatar)
                } else {
                    null
                }
            }
            catalogInitialized = true
        }
    }

    private fun loadStoredCatalog(): StoredAvatarCatalogPayload? {
        val payload = memberCatalogStorage.loadAvatarCatalogPayload().orEmpty().trim()
        if (payload.isEmpty()) return null
        return runCatching {
            json.decodeFromString<StoredAvatarCatalogPayload>(payload)
        }.getOrNull()
    }

    private fun loadBundledCustomAvatarCatalog(): List<SupabaseAvatarCatalogItem> = try {
        context.assets.open(BundledAvatarCatalogFile).bufferedReader().use { reader ->
            json.decodeFromString<List<SupabaseAvatarCatalogItem>>(reader.readText())
        }
    } catch (error: Exception) {
        Log.w(MemberAvatarTag, "Unable to load bundled custom avatar catalog", error)
        emptyList()
    }

    private fun saveStoredCatalog() {
        memberCatalogStorage.saveAvatarCatalogPayload(
            json.encodeToString(
                StoredAvatarCatalogPayload(
                    standardItems = standardMetadata,
                    memberItems = memberMetadata,
                    standardLoaded = standardCatalogLoaded,
                    memberLoaded = memberCatalogLoaded
                )
            )
        )
    }

    private fun toStandardCatalogItem(item: SupabaseAvatarCatalogItem): AvatarCatalogItem =
        AvatarCatalogItem(
            id = item.id,
            displayName = item.displayName,
            imageUrl = avatarImageUrl(item.storagePath),
            category = item.category,
            sortOrder = item.sortOrder,
            bgColor = item.bgColor
        )

    private fun loadCachedMemberAvatar(item: SupabaseMemberAvatarCatalogItem): AvatarCatalogItem {
        val imageFile = memberAvatarFile(item).takeIf { it.isFile && it.length() > 0L }
        return if (imageFile != null) {
            toMemberCatalogItem(item, imageFile)
        } else {
            toMemberCatalogFallbackItem(item)
        }
    }

    private fun toMemberCatalogFallbackItem(item: SupabaseMemberAvatarCatalogItem): AvatarCatalogItem =
        AvatarCatalogItem(
            id = item.id,
            displayName = item.displayName,
            imageUrl = avatarImageUrl(item.storagePath.trim().trimStart('/')),
            category = item.category,
            sortOrder = item.sortOrder,
            bgColor = item.bgColor,
            memberOnly = true
        )

    private fun toMemberCatalogItem(item: SupabaseMemberAvatarCatalogItem, imageFile: File) =
        AvatarCatalogItem(
            id = item.id,
            displayName = item.displayName,
            imageUrl = imageFile.toFileUriString(),
            category = item.category,
            sortOrder = item.sortOrder,
            bgColor = item.bgColor,
            memberOnly = true
        )

    private fun avatarImageUrl(storagePath: String): String {
        if (storagePath.startsWith("http://") || storagePath.startsWith("https://")) return storagePath
        val baseUrl = serverConfiguration.avatarPublicBaseUrl.orEmpty().trimEnd('/')
        return if (baseUrl.isNotEmpty()) "$baseUrl/$storagePath" else storagePath
    }

    private suspend fun cacheMemberAvatar(item: SupabaseMemberAvatarCatalogItem): File = withContext(Dispatchers.IO) {
        val imageFile = memberAvatarFile(item)
        if (imageFile.isFile && imageFile.length() > 0L) return@withContext imageFile

        val sanitizedPath = item.storagePath.trim().trimStart('/')
        val directory = imageFile.parentFile ?: return@withContext imageFile
        directory.mkdirs()
        val imageBytes = storage[MemberAvatarBucket].downloadAuthenticated(sanitizedPath)
        val temporaryFile = directory.resolve(".${imageFile.name}.tmp")
        temporaryFile.writeBytes(imageBytes)
        if (!temporaryFile.renameTo(imageFile)) {
            temporaryFile.copyTo(imageFile, overwrite = true)
            temporaryFile.delete()
        }
        imageFile
    }

    private fun memberAvatarFile(item: SupabaseMemberAvatarCatalogItem): File {
        val extension = item.storagePath.substringAfterLast('.', "img")
        .takeIf { it.length in 2..5 && it.all(Char::isLetterOrDigit) }
        ?: "img"
        return context.cacheDir.resolve("member_profile_avatars/${item.id}-v${item.assetVersion}.$extension")
    }

    private fun latestCachedMemberAvatar(avatarId: String): File? {
        val prefix = "$avatarId-v"
        return context.cacheDir.resolve("member_profile_avatars").listFiles()
            ?.filter { file -> file.isFile && file.length() > 0L && file.name.startsWith(prefix) }
            ?.maxByOrNull { file ->
                file.name.removePrefix(prefix).substringBefore('.').toIntOrNull() ?: Int.MIN_VALUE
            }
    }

    private fun File.toFileUriString(): String {
        val uri = toURI().toString()
        return if (uri.startsWith("file:/") && !uri.startsWith("file:///")) {
            uri.replaceFirst("file:/", "file:///")
        } else {
            uri
        }
    }
}
