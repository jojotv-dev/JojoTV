package com.nuvio.tv.data.freebox



import android.util.Base64
import android.util.Log

import com.nuvio.tv.data.local.FreeboxAuthUpdate

import com.nuvio.tv.data.local.FreeboxConnectionSettings

import com.nuvio.tv.data.local.FreeboxFolderUpdate

import com.nuvio.tv.data.local.FreeboxSessionUpdate

import javax.crypto.Mac

import javax.crypto.spec.SecretKeySpec

import javax.inject.Inject

import javax.inject.Singleton

import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.withContext

import okhttp3.MediaType.Companion.toMediaType

import okhttp3.OkHttpClient

import okhttp3.Request

import okhttp3.RequestBody.Companion.toRequestBody

import org.json.JSONArray

import org.json.JSONObject

import java.io.File

import java.net.URLEncoder



data class FreeboxFileEntry(

    val name: String,

    val path: String,

    val encodedPath: String? = null,

    val isDirectory: Boolean,

    val size: Long? = null,

    val durationMs: Long? = null,

    val modifiedMs: Long? = null

)



@Singleton

class FreeboxOsClient @Inject constructor(

    private val httpClient: OkHttpClient

) {

    suspend fun requestAuthorization(settings: FreeboxConnectionSettings): Result<FreeboxAuthUpdate> = withContext(Dispatchers.IO) {

        runCatching {

            val body = JSONObject()

                .put("app_id", APP_ID)

                .put("app_name", APP_NAME)

                .put("app_version", APP_VERSION)

                .put("device_name", settings.username.ifBlank { "JojoTV" })

                .toString()

                .toRequestBody(JSON_MEDIA_TYPE)

            val json = executeJson(

                Request.Builder()

                    .url(apiUrl(settings.address, "login/authorize/"))

                    .post(body)

                    .build()

            )

            val result = json.getJSONObject("result")

            FreeboxAuthUpdate(

                appToken = result.getString("app_token"),

                trackId = result.getInt("track_id"),

                status = "pending"

            )

        }

    }



    suspend fun refreshAuthorizationStatus(settings: FreeboxConnectionSettings): Result<String> = withContext(Dispatchers.IO) {

        runCatching {

            require(settings.authTrackId >= 0) { "Aucune demande d'autorisation Freebox en cours." }

            val json = executeJson(

                Request.Builder()

                    .url(apiUrl(settings.address, "login/authorize/${settings.authTrackId}"))

                    .get()

                    .build()

            )

            json.getJSONObject("result").getString("status")

        }

    }



    suspend fun openSession(settings: FreeboxConnectionSettings): Result<FreeboxSessionUpdate> = withContext(Dispatchers.IO) {

        runCatching {

            require(settings.appToken.isNotBlank()) { "Autorisez d'abord JojoTV sur la Freebox." }

            val challenge = executeJson(

                Request.Builder()

                    .url(apiUrl(settings.address, "login/"))

                    .get()

                    .build()

            ).getJSONObject("result").getString("challenge")

            val password = hmacSha1Hex(settings.appToken, challenge)

            val body = JSONObject()

                .put("app_id", APP_ID)

                .put("app_version", APP_VERSION)

                .put("password", password)

                .toString()

                .toRequestBody(JSON_MEDIA_TYPE)

            val json = executeJson(

                Request.Builder()

                    .url(apiUrl(settings.address, "login/session/"))

                    .post(body)

                    .build()

            )

            val result = json.getJSONObject("result")

            FreeboxSessionUpdate(

                sessionToken = result.getString("session_token"),

                status = "connected"

            )

        }

    }



    suspend fun loadRootFolders(settings: FreeboxConnectionSettings): Result<FreeboxFolderUpdate> = withContext(Dispatchers.IO) {

        runCatching {

            val sessionToken = settings.sessionToken.ifBlank {

                openSession(settings).getOrThrow().sessionToken

            }

            val folders = listDirectory(settings.address, sessionToken, FREEBOX_ROOT)

                .filter { it.isDirectory }

                .map { it.name }

                .toSet()

            val visible = if (settings.serverFoldersConfigured) {

                settings.visibleServerFolders.intersect(folders)

            } else {

                folders

            }

            FreeboxFolderUpdate(

                folders = folders,

                visibleFolders = visible,

                sidebarFolders = settings.sidebarServerFolders.intersect(folders)

            )

        }

    }



    suspend fun listDirectory(settings: FreeboxConnectionSettings, path: String): Result<List<FreeboxFileEntry>> = withContext(Dispatchers.IO) {

        runCatching {

            val sessionToken = settings.sessionToken.ifBlank {

                openSession(settings).getOrThrow().sessionToken

            }

            listDirectory(settings.address, sessionToken, path)

        }

    }




    suspend fun probeDurationMs(settings: FreeboxConnectionSettings, entry: FreeboxFileEntry): Long? = withContext(Dispatchers.IO) {
        var retriever: android.media.MediaMetadataRetriever? = null
        try {
            val sessionToken = settings.sessionToken.ifBlank {
                openSession(settings).getOrThrow().sessionToken
            }
            val url = downloadUrl(settings.copy(sessionToken = sessionToken), entry.path, entry.encodedPath)
            retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(url, sessionHeaders(sessionToken))
            val durationStr = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)
            durationStr?.toLongOrNull()?.takeIf { it > 0L }.also { duration ->
                if (duration == null) {
                    Log.w(TAG, "Duree Freebox absente des metadonnees: ${entry.path}")
                }
            }
        } catch (error: Exception) {
            Log.w(TAG, "Echec du sondage de duree Freebox: ${entry.path}", error)
            null
        } finally {
            try { retriever?.release() } catch (_: Exception) {}
        }
    }

    fun downloadUrl(settings: FreeboxConnectionSettings, path: String, encodedPath: String? = null): String =

        apiUrl(settings.address, "dl/${encodedPath?.let(::encodePathSegment) ?: encodeFreeboxPath(path)}")


    suspend fun rename(settings: FreeboxConnectionSettings, path: String, encodedPath: String?, newName: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val sessionToken = settings.sessionToken.ifBlank { openSession(settings).getOrThrow().sessionToken }
            val body = JSONObject()
                .put("src", freeboxApiPath(path, encodedPath))
                .put("dst", newName)
                .toString()
                .toRequestBody(JSON_MEDIA_TYPE)
            executeJson(
                Request.Builder()
                    .url(apiUrl(settings.address, "fs/rename/"))
                    .header("X-Fbx-App-Auth", sessionToken)
                    .post(body)
                    .build()
            )
            Unit
        }
    }

    suspend fun deleteFiles(settings: FreeboxConnectionSettings, files: List<FreeboxFileEntry>): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val sessionToken = settings.sessionToken.ifBlank { openSession(settings).getOrThrow().sessionToken }
            val body = JSONObject()
                .put("files", JSONArray(files.map { freeboxApiPath(it.path, it.encodedPath) }))
                .toString()
                .toRequestBody(JSON_MEDIA_TYPE)
            executeJson(
                Request.Builder()
                    .url(apiUrl(settings.address, "fs/rm/"))
                    .header("X-Fbx-App-Auth", sessionToken)
                    .post(body)
                    .build()
            )
            Unit
        }
    }

    suspend fun copyFiles(settings: FreeboxConnectionSettings, files: List<FreeboxFileEntry>, destinationPath: String): Result<Unit> =
        runFileTask(settings, "fs/cp/", files, destinationPath)

    suspend fun moveFiles(settings: FreeboxConnectionSettings, files: List<FreeboxFileEntry>, destinationPath: String): Result<Unit> =
        runFileTask(settings, "fs/mv/", files, destinationPath)

    private suspend fun runFileTask(
        settings: FreeboxConnectionSettings,
        endpoint: String,
        files: List<FreeboxFileEntry>,
        destinationPath: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val sessionToken = settings.sessionToken.ifBlank { openSession(settings).getOrThrow().sessionToken }
            val body = JSONObject()
                .put("files", JSONArray(files.map { freeboxApiPath(it.path, it.encodedPath) }))
                .put("dst", encodeFreeboxPath(destinationPath))
                .put("mode", "both")
                .toString()
                .toRequestBody(JSON_MEDIA_TYPE)
            executeJson(
                Request.Builder()
                    .url(apiUrl(settings.address, endpoint))
                    .header("X-Fbx-App-Auth", sessionToken)
                    .post(body)
                    .build()
            )
            Unit
        }
    }



    fun sessionHeaders(sessionToken: String): Map<String, String> =

        mapOf(

            "X-Fbx-App-Auth" to sessionToken,

            "Cookie" to "FreeboxOS=$sessionToken"

        )



    suspend fun downloadToFile(

        settings: FreeboxConnectionSettings,

        sessionToken: String,

        path: String,

        encodedPath: String?,

        destination: File

    ): Result<File> = withContext(Dispatchers.IO) {

        runCatching {

            destination.parentFile?.mkdirs()

            val requestBuilder = Request.Builder()

                .url(downloadUrl(settings.copy(sessionToken = sessionToken), path, encodedPath))

                .get()

            sessionHeaders(sessionToken).forEach { (name, value) ->

                requestBuilder.header(name, value)

            }

            httpClient.newCall(requestBuilder.build()).execute().use { response ->

                if (!response.isSuccessful) {

                    error("Freebox HTTP ${response.code}: ${response.message}")

                }

                response.body?.byteStream()?.use { input ->

                    destination.outputStream().use { output -> input.copyTo(output) }

                }

            }

            destination

        }

    }



    private fun listDirectory(address: String, sessionToken: String, path: String): List<FreeboxFileEntry> {

        val normalizedPath = normalizeFreeboxPath(path)

        val json = executeJson(

            Request.Builder()

                .url(apiUrl(address, "fs/ls/${encodeFreeboxPath(normalizedPath)}"))

                .header("X-Fbx-App-Auth", sessionToken)

                .get()

                .build()

        )

        return parseEntries(json.opt("result"), normalizedPath)

            .sortedWith(compareByDescending<FreeboxFileEntry> { it.isDirectory }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.name })

    }



    private fun executeJson(request: Request): JSONObject {

        httpClient.newCall(request).execute().use { response ->

            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {

                error("Freebox HTTP ${response.code}: ${body.ifBlank { response.message }}")

            }

            val json = JSONObject(body)

            if (!json.optBoolean("success", false)) {

                val errorCode = json.optString("error_code", "unknown_error")

                val message = json.optString("msg", errorCode)

                error(message.ifBlank { errorCode })

            }

            return json

        }

    }



    private fun parseEntries(result: Any?, parentPath: String): List<FreeboxFileEntry> {

        val array = when (result) {

            is JSONArray -> result

            is JSONObject -> result.optJSONArray("entries") ?: result.optJSONArray("files") ?: JSONArray()

            else -> JSONArray()

        }

        return buildList {

            for (index in 0 until array.length()) {

                val item = array.optJSONObject(index) ?: continue

                val type = item.optString("type")

                val mimetype = item.optString("mimetype")

                val isDirectory = type.equals("dir", true) ||

                    type.equals("directory", true) ||

                    mimetype.equals("inode/directory", true)

                val name = item.optString("name").ifBlank { item.optString("path").substringAfterLast('/') }

                if (name.isBlank() || name == "." || name == "..") continue

                val rawPath = item.optString("path")

                val decodedPath = decodeFreeboxPath(rawPath)

                val path = decodedPath ?: rawPath.takeIf { it.startsWith("/") } ?: joinFreeboxPath(parentPath, name)

                val encodedPath = when {

                    rawPath.isBlank() -> null

                    decodedPath != null -> rawPath

                    rawPath.startsWith("/") -> null

                    else -> rawPath

                }

                add(

                    FreeboxFileEntry(

                        name = name,

                        path = normalizeFreeboxPath(path),

                        encodedPath = encodedPath,

                        isDirectory = isDirectory,

                        size = parseFileSize(item),

                        durationMs = parseDurationMs(item),

                        modifiedMs = parseModifiedMs(item)

                    )

                )

            }

        }

    }



    private fun parseFileSize(item: JSONObject): Long? {
        val sizeKeys = listOf("size", "file_size", "filesize", "bytes")
        sizeKeys.firstNotNullOfOrNull { key ->
            item.optLong(key).takeIf { it > 0L }
        }?.let { return it }
        val nested = listOf("stat", "metadata", "file").mapNotNull { item.optJSONObject(it) }
        return nested.firstNotNullOfOrNull { obj ->
            sizeKeys.firstNotNullOfOrNull { key -> obj.optLong(key).takeIf { it > 0L } }
        }
    }

    private fun parseModifiedMs(item: JSONObject): Long? {
        val secondsKeys = listOf("mtime", "modification", "modified", "created")
        secondsKeys.firstNotNullOfOrNull { key ->
            item.optLong(key).takeIf { it > 0L }?.let { raw -> if (raw < 10_000_000_000L) raw * 1000L else raw }
        }?.let { return it }
        val nested = listOf("stat", "metadata", "file").mapNotNull { item.optJSONObject(it) }
        return nested.firstNotNullOfOrNull { obj ->
            secondsKeys.firstNotNullOfOrNull { key ->
                obj.optLong(key).takeIf { it > 0L }?.let { raw -> if (raw < 10_000_000_000L) raw * 1000L else raw }
            }
        }
    }

    private fun parseDurationMs(item: JSONObject): Long? {

        val directKeys = listOf("duration_ms", "durationMillis", "duration_millis", "duration")

        directKeys.firstNotNullOfOrNull { key -> item.optDurationValue(key) }?.let { return it }



        val nestedKeys = listOf("media_info", "mediaInfo", "video_info", "videoInfo", "metadata", "metadatas")

        nestedKeys.forEach { key ->

            val nested = item.optJSONObject(key) ?: return@forEach

            directKeys.firstNotNullOfOrNull { durationKey -> nested.optDurationValue(durationKey) }?.let { return it }

        }

        return null

    }



    private fun JSONObject.optDurationValue(key: String): Long? {

        if (!has(key) || isNull(key)) return null

        val raw = opt(key) ?: return null

        val value = when (raw) {

            is Number -> raw.toDouble()

            is String -> raw.trim().toDoubleOrNull()

            else -> null

        } ?: return null

        if (value <= 0.0) return null

        return if (key.contains("ms", ignoreCase = true) || value > 10_000.0) {

            value.toLong()

        } else {

            (value * 1000.0).toLong()

        }.takeIf { it > 0L }

    }

    private fun apiUrl(address: String, endpoint: String): String {

        val base = normalizeBaseUrl(address)

        return "$base/api/$API_VERSION/$endpoint"

    }



    private fun normalizeBaseUrl(address: String): String {

        val trimmed = address.trim().trimEnd('/')

        val withScheme = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {

            trimmed

        } else {

            "http://$trimmed"

        }

        return withScheme.replace(Regex("/api/v\\d+$"), "")

    }



    private fun normalizeFreeboxPath(path: String): String {

        val trimmed = path.trim().replace('\\', '/').trimEnd('/')

        if (trimmed.isBlank() || trimmed == "/") return FREEBOX_ROOT

        return if (trimmed.startsWith('/')) trimmed else "/$trimmed"

    }



    private fun encodeFreeboxPath(path: String): String =

        encodePathSegment(Base64.encodeToString(normalizeFreeboxPath(path).toByteArray(Charsets.UTF_8), Base64.NO_WRAP))


    private fun freeboxApiPath(path: String, encodedPath: String?): String =
        encodedPath ?: encodeFreeboxPath(path)



    private fun encodePathSegment(value: String): String =

        URLEncoder.encode(value, "UTF-8")



    private fun decodeFreeboxPath(path: String): String? {

        if (path.isBlank() || path.startsWith("/")) return null

        return runCatching {

            String(Base64.decode(path, Base64.DEFAULT), Charsets.UTF_8)

                .takeIf { it.startsWith("/") }

        }.getOrNull()

    }



    private fun joinFreeboxPath(parentPath: String, name: String): String {

        val parent = normalizeFreeboxPath(parentPath).trimEnd('/')

        return "$parent/$name"

    }



    private fun hmacSha1Hex(secret: String, challenge: String): String {

        val mac = Mac.getInstance("HmacSHA1")

        mac.init(SecretKeySpec(secret.toByteArray(Charsets.UTF_8), "HmacSHA1"))

        return mac.doFinal(challenge.toByteArray(Charsets.UTF_8)).joinToString(separator = "") { byte ->

            "%02x".format(byte)

        }

    }



    companion object {

        private const val TAG = "FreeboxOsClient"

        private const val API_VERSION = "v15"

        private const val FREEBOX_ROOT = "/Freebox"

        private const val APP_ID = "com.nuvio.tv.freebox"

        private const val APP_NAME = "JojoTV"

        private const val APP_VERSION = "1.0"

        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    }

}

