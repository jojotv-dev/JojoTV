package com.nuvio.tv.reshaped.livetv

import android.content.Context
import android.util.JsonReader
import android.util.JsonToken
import java.io.File
import java.security.MessageDigest
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

private const val VOD_CACHE_BATCH_FLUSH_DELAY_MS = 15_000L
private const val VOD_CACHE_BATCH_FLUSH_ITEM_LIMIT = 5_000

/** A compact on-disk copy of the last IPTV movie/series catalogue, so it is available on restart. */
internal object LiveTvVodCache {
    @Volatile
    var onCacheChanged: (() -> Unit)? = null

    private val mutableCacheRevision = MutableStateFlow(0L)
    val cacheRevision = mutableCacheRevision.asStateFlow()

    private data class VodFileStamp(
        val sourceId: String,
        val type: String,
        val path: String,
        val exists: Boolean,
        val length: Long,
        val lastModified: Long,
    )

    private data class FavoriteSnapshotKey(
        val generation: Long,
        val files: List<VodFileStamp>,
        val favoriteKeys: Set<String>,
    )

    private data class FavoriteSnapshot(
        val key: FavoriteSnapshotKey,
        val items: List<LiveTvVodItem>,
    )

    private data class VodCatalogState(
        val sourceFingerprint: String = "",
        val catalogComplete: Boolean = false,
        val categoryListComplete: Boolean = false,
        val completedCategoryIds: Set<String> = emptySet(),
    )

    private val favoriteReadMutex = Mutex()
    private val vodWriteMutex = Mutex()
    private val catalogStateMutex = Mutex()
    private val vodCacheGeneration = AtomicLong(0L)

    @Volatile
    private var favoriteSnapshot: FavoriteSnapshot? = null

    class BatchWriter internal constructor(
        private val context: Context,
        private val sourceId: String,
        private val type: String,
    ) {
        private val flushScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private val pendingMutex = Mutex()
        private val flushMutex = Mutex()
        private val pending = LinkedHashMap<String, LiveTvVodItem>()
        private var flushJob: Job? = null
        private var lastFlushAtMs = 0L

        suspend fun append(items: List<LiveTvVodItem>) {
            val validItems = items.asSequence()
                .filter { it.sourceId == sourceId && it.type == type && it.id.isNotBlank() }
                .toList()
            if (validItems.isEmpty()) return

            val flushNow = pendingMutex.withLock {
                validItems.forEach { pending[it.id] = it }
                if (lastFlushAtMs == 0L || pending.size >= VOD_CACHE_BATCH_FLUSH_ITEM_LIMIT) {
                    true
                } else {
                    if (flushJob?.isActive != true) {
                        flushJob = flushScope.launch {
                            delay(VOD_CACHE_BATCH_FLUSH_DELAY_MS)
                            flush()
                        }
                    }
                    false
                }
            }
            if (flushNow) flush()
        }

        suspend fun finish() {
            try {
                withContext(NonCancellable) { flush() }
            } finally {
                flushScope.cancel()
            }
        }

        private suspend fun flush() {
            flushMutex.withLock {
                val currentJob = currentCoroutineContext()[Job]
                val batch = pendingMutex.withLock {
                    flushJob?.takeIf { it != currentJob }?.cancel()
                    flushJob = null
                    if (pending.isEmpty()) {
                        emptyList()
                    } else {
                        pending.values.toList().also {
                            pending.clear()
                            lastFlushAtMs = System.nanoTime() / 1_000_000L
                        }
                    }
                }
                if (batch.isNotEmpty()) {
                    try {
                        LiveTvVodCache.merge(context, sourceId, type, batch)
                    } catch (failure: CancellationException) {
                        restorePending(batch)
                        throw failure
                    } catch (failure: Exception) {
                        restorePending(batch)
                        throw failure
                    }
                }
            }
        }

        private suspend fun restorePending(batch: List<LiveTvVodItem>) {
            withContext(NonCancellable) {
                pendingMutex.withLock {
                    batch.forEach { pending.putIfAbsent(it.id, it) }
                    lastFlushAtMs = 0L
                }
            }
        }
    }

    fun batchWriter(context: Context, sourceId: String, type: String): BatchWriter =
        BatchWriter(context.applicationContext, sourceId, type)

    suspend fun upsert(context: Context, item: LiveTvVodItem) {
        merge(context, item.sourceId, item.type, listOf(item))
    }

    suspend fun read(context: Context, sourceId: String, type: String): List<LiveTvVodItem> = withContext(Dispatchers.IO) {
        runCatching {
            val file = file(context, sourceId, type)
            if (!file.isFile) return@runCatching emptyList()
            GZIPInputStream(file.inputStream().buffered()).bufferedReader(Charsets.UTF_8).use { reader ->
                val array = JSONArray(reader.readText())
                (0 until array.length()).mapNotNull { index ->
                    array.optJSONObject(index)?.toVodItem()
                }
            }
        }.getOrDefault(emptyList())
    }

    /** Reads only starred items from each cached catalogue, avoiding a full in-memory parse. */
    suspend fun readFavorites(
        context: Context,
        sources: List<LiveTvSource>,
        favoriteKeys: Set<String>,
    ): List<LiveTvVodItem> = withContext(Dispatchers.IO) {
        if (favoriteKeys.isEmpty()) return@withContext emptyList()

        favoriteReadMutex.withLock {
            val eligibleSources = sources
                .filter { it.type == LiveTvSourceType.Xtream || it.type == LiveTvSourceType.Stalker }
                .distinctBy(LiveTvSource::id)
            val files = eligibleSources.flatMap { source ->
                listOf("movie", "series").map { type ->
                    val cacheFile = file(context, source.id, type)
                    VodFileStamp(
                        sourceId = source.id,
                        type = type,
                        path = cacheFile.absolutePath,
                        exists = cacheFile.isFile,
                        length = cacheFile.length(),
                        lastModified = cacheFile.lastModified(),
                    ) to cacheFile
                }
            }
            val key = FavoriteSnapshotKey(
                generation = vodCacheGeneration.get(),
                files = files.map { it.first },
                favoriteKeys = favoriteKeys.toSet(),
            )
            favoriteSnapshot?.takeIf { it.key == key }?.let { return@withLock it.items }

            val prior = favoriteSnapshot
            if (
                prior != null &&
                prior.key.generation == key.generation &&
                prior.key.files == key.files &&
                key.favoriteKeys.all(prior.key.favoriteKeys::contains)
            ) {
                val subset = prior.items.filter { it.favoriteKey in key.favoriteKeys }
                favoriteSnapshot = FavoriteSnapshot(key, subset)
                return@withLock subset
            }

            val result = buildList {
                files.forEach { (stamp, cacheFile) ->
                    if (stamp.exists) addAll(readFavoritesFromFile(cacheFile, key.favoriteKeys))
                }
            }
            if (key.generation == vodCacheGeneration.get()) {
                favoriteSnapshot = FavoriteSnapshot(key, result)
            }
            result
        }
    }

    /** Merges a provider page into the saved catalogue without discarding older pages. */
    suspend fun merge(context: Context, sourceId: String, type: String, items: List<LiveTvVodItem>): Unit =
        withContext(Dispatchers.IO) {
            if (items.isEmpty()) return@withContext
            vodWriteMutex.withLock {
                val current = read(context, sourceId, type)
                currentCoroutineContext().ensureActive()
                val merged = LinkedHashMap<String, LiveTvVodItem>(current.size + items.size)
                current.forEach { merged[it.id] = it }
                items.forEach { item ->
                    if (item.sourceId != sourceId || item.type != type || item.id.isBlank()) return@forEach
                    val existing = merged[item.id]
                    val updated = existing?.let { item.withMissingVodFieldsFrom(it) } ?: item
                    merged[item.id] = updated
                }
                val updatedItems = merged.values.toList()
                if (updatedItems != current) {
                    writeUnlocked(context, sourceId, type, updatedItems, notifyRemote = false)
                }
            }
        }

    suspend fun write(context: Context, sourceId: String, type: String, items: List<LiveTvVodItem>): Unit =
        withContext(Dispatchers.IO) {
            vodWriteMutex.withLock {
                writeUnlocked(context, sourceId, type, items, notifyRemote = true)
                Unit
            }
        }

    suspend fun writeCompleteCatalog(
        context: Context,
        source: LiveTvSource,
        type: String,
        items: List<LiveTvVodItem>,
    ): Boolean = withContext(Dispatchers.IO) {
        val sourceId = source.id
        val sourceFingerprint = source.catalogFingerprint()
        vodWriteMutex.withLock {
            if (!writeUnlocked(context, sourceId, type, items, notifyRemote = true)) {
                return@withLock false
            }
            catalogStateMutex.withLock {
                val current = readCatalogStateUnlocked(context, sourceId, type)
                val state = current.takeIf { it.sourceFingerprint == sourceFingerprint }
                    ?: VodCatalogState(sourceFingerprint = sourceFingerprint)
                writeCatalogStateUnlocked(context, sourceId, type, state.copy(catalogComplete = true))
            }
        }
    }

    suspend fun isCatalogComplete(context: Context, source: LiveTvSource, type: String): Boolean =
        readCatalogState(context, source.id, type).takeIf {
            it.sourceFingerprint == source.catalogFingerprint()
        }?.catalogComplete == true

    suspend fun isCategoryListComplete(context: Context, source: LiveTvSource, type: String): Boolean {
        val state = readCatalogState(context, source.id, type).takeIf {
            it.sourceFingerprint == source.catalogFingerprint()
        } ?: return false
        return state.categoryListComplete && readCategories(context, source.id, type).isNotEmpty()
    }

    suspend fun completedCategoryIds(context: Context, source: LiveTvSource, type: String): Set<String> =
        readCatalogState(context, source.id, type).takeIf {
            it.sourceFingerprint == source.catalogFingerprint()
        }?.completedCategoryIds.orEmpty()

    suspend fun markCategoriesComplete(
        context: Context,
        source: LiveTvSource,
        type: String,
        categoryIds: Set<String>,
    ) {
        if (categoryIds.isEmpty()) return
        val fingerprint = source.catalogFingerprint()
        updateCatalogState(context, source.id, type) { current ->
            val state = current.takeIf { it.sourceFingerprint == fingerprint }
                ?: VodCatalogState(sourceFingerprint = fingerprint)
            state.copy(completedCategoryIds = state.completedCategoryIds + categoryIds)
        }
    }

    suspend fun clearCategoryCompletions(context: Context, source: LiveTvSource, type: String) {
        val fingerprint = source.catalogFingerprint()
        updateCatalogState(context, source.id, type) { current ->
            val state = current.takeIf { it.sourceFingerprint == fingerprint }
                ?: VodCatalogState(sourceFingerprint = fingerprint)
            state.copy(completedCategoryIds = emptySet())
        }
    }

    private fun writeUnlocked(
        context: Context,
        sourceId: String,
        type: String,
        items: List<LiveTvVodItem>,
        notifyRemote: Boolean,
    ): Boolean {
        try {
            val target = file(context, sourceId, type)
            if (items.isEmpty()) {
                if (target.exists() && !target.delete()) return false
                return !target.exists()
            }
            target.parentFile?.mkdirs()
            val temp = File.createTempFile(target.name, ".part", target.parentFile)
            try {
                GZIPOutputStream(temp.outputStream().buffered()).bufferedWriter(Charsets.UTF_8).use { writer ->
                    val array = JSONArray()
                    items.forEach { array.put(it.toJson()) }
                    writer.write(array.toString())
                }
                if (!temp.renameTo(target)) {
                    target.delete()
                    if (!temp.renameTo(target)) return false
                }
                return target.isFile
            } finally {
                temp.delete()
            }
        } catch (_: Exception) {
            // A cache write must never make a successfully loaded catalogue disappear.
            return false
        } finally {
            notifyCacheChanged(notifyRemote)
        }
    }

    /** Copies a same-provider cache to its account-wide source ID and leaves the old files intact. */
    suspend fun copySourceId(context: Context, oldSourceId: String, canonicalSourceId: String) {
        if (oldSourceId.isBlank() || canonicalSourceId.isBlank() || oldSourceId == canonicalSourceId) return
        for (type in listOf("movie", "series")) {
            val oldItems = read(context, oldSourceId, type)
            if (oldItems.isNotEmpty()) {
                merge(
                    context,
                    canonicalSourceId,
                    type,
                    oldItems.map { item ->
                        item.copy(
                            id = "$canonicalSourceId:$type:${item.providerId}",
                            sourceId = canonicalSourceId,
                        )
                    },
                )
            }

            val oldCategories = readCategories(context, oldSourceId, type)
            if (oldCategories.isNotEmpty()) {
                val merged = LinkedHashMap<String, LiveTvVodCategory>()
                readCategories(context, canonicalSourceId, type).forEach { merged[it.id] = it }
                oldCategories.forEach { category ->
                    merged.putIfAbsent(category.id, category.copy(sourceId = canonicalSourceId))
                }
                writeCategories(context, canonicalSourceId, type, merged.values.toList())
            }

            val oldState = readCatalogState(context, oldSourceId, type)
            if (oldState != VodCatalogState()) {
                updateCatalogState(context, canonicalSourceId, type) { current ->
                    if (current.sourceFingerprint.isNotBlank() &&
                        current.sourceFingerprint != oldState.sourceFingerprint
                    ) {
                        current
                    } else {
                        current.copy(
                            sourceFingerprint = oldState.sourceFingerprint,
                            catalogComplete = current.catalogComplete || oldState.catalogComplete,
                            categoryListComplete = current.categoryListComplete || oldState.categoryListComplete,
                            completedCategoryIds = current.completedCategoryIds + oldState.completedCategoryIds,
                        )
                    }
                }
            }
        }
    }

    private fun LiveTvVodItem.withMissingVodFieldsFrom(fallback: LiveTvVodItem): LiveTvVodItem = copy(
        providerId = providerId.ifBlank { fallback.providerId },
        title = title.ifBlank { fallback.title },
        categoryId = categoryId.ifBlank { fallback.categoryId },
        categoryName = categoryName.ifBlank { fallback.categoryName },
        posterUrl = posterUrl.ifBlank { fallback.posterUrl },
        extension = extension.ifBlank { fallback.extension },
        year = year.ifBlank { fallback.year },
        plot = plot.ifBlank { fallback.plot },
        season = season.takeIf { it > 0 } ?: fallback.season,
        episode = episode.takeIf { it > 0 } ?: fallback.episode,
        command = command.ifBlank { fallback.command },
        seriesId = seriesId.ifBlank { fallback.seriesId },
        seriesTitle = seriesTitle.ifBlank { fallback.seriesTitle },
    )

    private fun readFavoritesFromFile(file: File, favoriteKeys: Set<String>): List<LiveTvVodItem> = runCatching {
        GZIPInputStream(file.inputStream().buffered()).bufferedReader(Charsets.UTF_8).use { reader ->
            JsonReader(reader).use { json ->
                json.isLenient = true
                val favorites = mutableListOf<LiveTvVodItem>()
                json.beginArray()
                while (json.hasNext()) {
                    var id = ""
                    var sourceId = ""
                    var providerId = ""
                    var title = ""
                    var type = ""
                    var categoryId = ""
                    var categoryName = ""
                    var posterUrl = ""
                    var extension = "mp4"
                    var year = ""
                    var plot = ""
                    var season = 0
                    var episode = 0
                    var command = ""
                    var seriesId = ""
                    var seriesTitle = ""

                    json.beginObject()
                    while (json.hasNext()) {
                        when (json.nextName()) {
                            "id" -> id = json.nextStringValue()
                            "sourceId" -> sourceId = json.nextStringValue()
                            "providerId" -> providerId = json.nextStringValue()
                            "title" -> title = json.nextStringValue()
                            "type" -> type = json.nextStringValue()
                            "categoryId" -> categoryId = json.nextStringValue()
                            "categoryName" -> categoryName = json.nextStringValue()
                            "posterUrl" -> posterUrl = json.nextStringValue()
                            "extension" -> extension = json.nextStringValue().ifBlank { "mp4" }
                            "year" -> year = json.nextStringValue()
                            "plot" -> plot = json.nextStringValue()
                            "season" -> season = json.nextIntValue()
                            "episode" -> episode = json.nextIntValue()
                            "command" -> command = json.nextStringValue()
                            "seriesId" -> seriesId = json.nextStringValue()
                            "seriesTitle" -> seriesTitle = json.nextStringValue()
                            else -> json.skipValue()
                        }
                    }
                    json.endObject()

                    if (id.isNotBlank() && sourceId.isNotBlank() && providerId.isNotBlank() &&
                        title.isNotBlank() && "$sourceId:$type:$id" in favoriteKeys
                    ) {
                        favorites += LiveTvVodItem(
                            id = id,
                            sourceId = sourceId,
                            providerId = providerId,
                            title = title,
                            type = type,
                            categoryId = categoryId,
                            categoryName = categoryName,
                            posterUrl = posterUrl,
                            extension = extension,
                            year = year,
                            plot = plot,
                            season = season,
                            episode = episode,
                            command = command,
                            seriesId = seriesId,
                            seriesTitle = seriesTitle,
                        )
                    }
                }
                json.endArray()
                favorites
            }
        }
    }.getOrDefault(emptyList())

    private fun JsonReader.nextStringValue(): String = when (peek()) {
        JsonToken.NULL -> {
            nextNull()
            ""
        }
        JsonToken.STRING, JsonToken.NUMBER -> nextString()
        JsonToken.BOOLEAN -> nextBoolean().toString()
        else -> {
            skipValue()
            ""
        }
    }

    private fun JsonReader.nextIntValue(): Int = when (peek()) {
        JsonToken.NULL -> {
            nextNull()
            0
        }
        JsonToken.STRING -> nextString().toIntOrNull() ?: 0
        JsonToken.NUMBER -> nextInt()
        else -> {
            skipValue()
            0
        }
    }

    suspend fun readCategories(context: Context, sourceId: String, type: String): List<LiveTvVodCategory> = withContext(Dispatchers.IO) {
        runCatching {
            val file = categoryFile(context, sourceId, type)
            if (!file.isFile) return@runCatching emptyList()
            GZIPInputStream(file.inputStream().buffered()).bufferedReader(Charsets.UTF_8).use { reader ->
                val array = JSONArray(reader.readText())
                (0 until array.length()).mapNotNull { index ->
                    array.optJSONObject(index)?.let { json ->
                        val id = json.optString("id").takeIf(String::isNotBlank) ?: return@let null
                        val name = json.optString("name").takeIf(String::isNotBlank) ?: return@let null
                        LiveTvVodCategory(id, sourceId, name)
                    }
                }
            }
        }.getOrDefault(emptyList())
    }

    suspend fun writeCategories(context: Context, sourceId: String, type: String, categories: List<LiveTvVodCategory>): Unit = withContext(Dispatchers.IO) {
        try {
            val target = categoryFile(context, sourceId, type)
            if (categories.isEmpty()) {
                target.delete()
                return@withContext
            }
            target.parentFile?.mkdirs()
            val temp = File.createTempFile(target.name, ".part", target.parentFile)
            try {
                GZIPOutputStream(temp.outputStream().buffered()).bufferedWriter(Charsets.UTF_8).use { writer ->
                    val array = JSONArray()
                    categories.forEach { array.put(JSONObject().put("id", it.id).put("name", it.name)) }
                    writer.write(array.toString())
                }
                if (!temp.renameTo(target)) {
                    target.delete()
                    temp.renameTo(target)
                }
            } finally {
                temp.delete()
            }
        } catch (_: Exception) {
            // Keep the live catalogue usable when the disk cache cannot be updated.
        } finally {
            notifyCacheChanged()
        }
    }

    suspend fun writeCategoriesAndMarkComplete(
        context: Context,
        source: LiveTvSource,
        type: String,
        categories: List<LiveTvVodCategory>,
    ) {
        if (categories.isEmpty()) return
        val sourceId = source.id
        writeCategories(context, sourceId, type, categories)
        val expected = categories
            .map { it.copy(sourceId = sourceId) }
            .distinctBy(LiveTvVodCategory::id)
        if (readCategories(context, sourceId, type) != expected) return
        val sourceFingerprint = source.catalogFingerprint()
        updateCatalogState(context, sourceId, type) { current ->
            val state = current.takeIf { it.sourceFingerprint == sourceFingerprint }
                ?: VodCatalogState(sourceFingerprint = sourceFingerprint)
            state.copy(categoryListComplete = true)
        }
    }

    private fun notifyCacheChanged(notifyRemote: Boolean = true) {
        favoriteSnapshot = null
        mutableCacheRevision.value = vodCacheGeneration.incrementAndGet()
        if (notifyRemote) runCatching { onCacheChanged?.invoke() }
    }

    private fun file(context: Context, sourceId: String, type: String): File {
        val safeId = sourceId.filter { it.isLetterOrDigit() || it == '-' || it == '_' }.ifBlank { "source" }
        val safeType = if (type == "series") "series" else "movie"
        return File(File(context.applicationContext.filesDir, "iptv_vod"), "${safeId}_$safeType.json.gz")
    }

    private fun categoryFile(context: Context, sourceId: String, type: String): File {
        val base = file(context, sourceId, type)
        return File(base.parentFile, base.name.removeSuffix(".json.gz") + "_groups.json.gz")
    }

    private fun catalogStateFile(context: Context, sourceId: String, type: String): File {
        val base = file(context, sourceId, type)
        return File(base.parentFile, base.name.removeSuffix(".json.gz") + "_state.json")
    }

    private suspend fun readCatalogState(context: Context, sourceId: String, type: String): VodCatalogState =
        withContext(Dispatchers.IO) {
            catalogStateMutex.withLock { readCatalogStateUnlocked(context, sourceId, type) }
        }

    private suspend fun updateCatalogState(
        context: Context,
        sourceId: String,
        type: String,
        update: (VodCatalogState) -> VodCatalogState,
    ): Boolean = withContext(Dispatchers.IO) {
        catalogStateMutex.withLock {
            val current = readCatalogStateUnlocked(context, sourceId, type)
            writeCatalogStateUnlocked(context, sourceId, type, update(current))
        }
    }

    private fun readCatalogStateUnlocked(context: Context, sourceId: String, type: String): VodCatalogState =
        runCatching {
            val stateFile = catalogStateFile(context, sourceId, type)
            if (!stateFile.isFile) return@runCatching VodCatalogState()
            val json = JSONObject(stateFile.readText(Charsets.UTF_8))
            VodCatalogState(
                sourceFingerprint = json.optString("sourceFingerprint"),
                catalogComplete = json.optBoolean("catalogComplete"),
                categoryListComplete = json.optBoolean("categoryListComplete"),
                completedCategoryIds = buildSet {
                    val ids = json.optJSONArray("completedCategoryIds") ?: return@buildSet
                    for (index in 0 until ids.length()) {
                        ids.optString(index).takeIf(String::isNotBlank)?.let(::add)
                    }
                },
            )
        }.getOrDefault(VodCatalogState())

    private fun LiveTvSource.catalogFingerprint(): String {
        val providerSettings = when (type) {
            LiveTvSourceType.Xtream -> xtream.toString()
            LiveTvSourceType.Stalker -> stalker.toString()
            LiveTvSourceType.M3u -> url
        }
        val value = "${type.name}\u0000$providerSettings".toByteArray(Charsets.UTF_8)
        return MessageDigest.getInstance("SHA-256")
            .digest(value)
            .joinToString("") { byte -> (byte.toInt() and 0xff).toString(16).padStart(2, '0') }
    }

    private fun writeCatalogStateUnlocked(
        context: Context,
        sourceId: String,
        type: String,
        state: VodCatalogState,
    ): Boolean {
        return try {
            val target = catalogStateFile(context, sourceId, type)
            target.parentFile?.mkdirs()
            val temp = File.createTempFile(target.name, ".part", target.parentFile)
            try {
                val completedCategoryIds = JSONArray()
                state.completedCategoryIds.sorted().forEach(completedCategoryIds::put)
                temp.writeText(
                    JSONObject()
                        .put("sourceFingerprint", state.sourceFingerprint)
                        .put("catalogComplete", state.catalogComplete)
                        .put("categoryListComplete", state.categoryListComplete)
                        .put("completedCategoryIds", completedCategoryIds)
                        .toString(),
                    Charsets.UTF_8,
                )
                if (!temp.renameTo(target)) {
                    target.delete()
                    if (!temp.renameTo(target)) return false
                }
                target.isFile
            } finally {
                temp.delete()
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun LiveTvVodItem.toJson() = JSONObject().apply {
        put("id", id); put("sourceId", sourceId); put("providerId", providerId)
        put("title", title); put("type", type); put("categoryId", categoryId); put("categoryName", categoryName)
        put("posterUrl", posterUrl); put("extension", extension); put("year", year)
        put("plot", plot); put("season", season); put("episode", episode)
        put("command", command); put("seriesId", seriesId); put("seriesTitle", seriesTitle)
    }

    private fun JSONObject.toVodItem(): LiveTvVodItem? {
        val id = optString("id").takeIf(String::isNotBlank) ?: return null
        val sourceId = optString("sourceId").takeIf(String::isNotBlank) ?: return null
        val providerId = optString("providerId").takeIf(String::isNotBlank) ?: return null
        val title = optString("title").takeIf(String::isNotBlank) ?: return null
        return LiveTvVodItem(
            id = id, sourceId = sourceId, providerId = providerId, title = title,
            type = optString("type"), categoryId = optString("categoryId"), categoryName = optString("categoryName"), posterUrl = optString("posterUrl"),
            extension = optString("extension", "mp4"), year = optString("year"), plot = optString("plot"),
            season = optInt("season"), episode = optInt("episode"), command = optString("command"),
            seriesId = optString("seriesId"), seriesTitle = optString("seriesTitle"),
        )
    }
}
