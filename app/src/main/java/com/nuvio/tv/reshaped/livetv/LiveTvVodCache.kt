package com.nuvio.tv.reshaped.livetv

import android.content.Context
import android.util.JsonReader
import android.util.JsonToken
import java.io.File
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/** A compact on-disk copy of the last IPTV movie/series catalogue, so it is available on restart. */
internal object LiveTvVodCache {
    @Volatile
    var onCacheChanged: (() -> Unit)? = null

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

    private val favoriteReadMutex = Mutex()
    private val vodCacheGeneration = AtomicLong(0L)

    @Volatile
    private var favoriteSnapshot: FavoriteSnapshot? = null

    suspend fun upsert(context: Context, item: LiveTvVodItem) {
        val current = read(context, item.sourceId, item.type)
        val existingIndex = current.indexOfFirst { it.id == item.id }
        val updated = if (existingIndex >= 0) {
            current.toMutableList().also { it[existingIndex] = item }
        } else {
            current + item
        }
        write(context, item.sourceId, item.type, updated)
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

    suspend fun write(context: Context, sourceId: String, type: String, items: List<LiveTvVodItem>): Unit = withContext(Dispatchers.IO) {
        vodCacheGeneration.incrementAndGet()
        favoriteSnapshot = null
        try {
            val target = file(context, sourceId, type)
            if (items.isEmpty()) {
                target.delete()
                return@withContext
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
                    temp.renameTo(target)
                }
            } finally {
                temp.delete()
            }
        } catch (_: Exception) {
            // A cache write must never make a successfully loaded catalogue disappear.
        } finally {
            favoriteSnapshot = null
            vodCacheGeneration.incrementAndGet()
            onCacheChanged?.invoke()
        }
    }

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
            onCacheChanged?.invoke()
        }
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
