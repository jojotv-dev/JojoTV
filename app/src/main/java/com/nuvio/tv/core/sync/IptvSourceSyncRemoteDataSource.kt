package com.nuvio.tv.core.sync

import com.nuvio.tv.BuildConfig
import io.github.jan.supabase.auth.Auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

data class IptvSourceSyncSnapshot(
    val exists: Boolean,
    val sourcesJson: String
)

@Singleton
class IptvSourceSyncRemoteDataSource @Inject constructor(
    private val auth: Auth,
    private val okHttpClient: OkHttpClient
) {
    suspend fun pull(profileId: Int): IptvSourceSyncSnapshot =
        request(action = "pull", profileId = profileId)

    suspend fun seed(profileId: Int, sourcesJson: String): IptvSourceSyncSnapshot =
        request(action = "seed", profileId = profileId, sourcesJson = sourcesJson)

    suspend fun push(profileId: Int, sourcesJson: String) {
        request(action = "push", profileId = profileId, sourcesJson = sourcesJson)
    }

    private suspend fun request(
        action: String,
        profileId: Int,
        sourcesJson: String? = null
    ): IptvSourceSyncSnapshot = withContext(Dispatchers.IO) {
        val accessToken = auth.currentAccessTokenOrNull()?.takeIf(String::isNotBlank)
            ?: throw IOException("JojoTV access token is unavailable")
        val url = BuildConfig.IPTV_SOURCE_SYNC_URL.trimEnd('/') + "/functions/v1/iptv-source-sync"
        val requestJson = buildJsonObject {
            put("action", action)
            put("profileId", profileId)
            sourcesJson?.let { raw ->
                val sources = runCatching { Json.parseToJsonElement(raw) }
                    .getOrElse { throw IOException("Invalid local IPTV source snapshot") }
                if (sources !is JsonArray) throw IOException("Invalid local IPTV source snapshot")
                put("sources", sources)
            }
        }.toString()
        val request = Request.Builder()
            .url(url)
            .header("apikey", BuildConfig.IPTV_SOURCE_SYNC_PUBLISHABLE_KEY)
            .header("Authorization", "Bearer $accessToken")
            .header("Accept", "application/json")
            .post(requestJson.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val responseText = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val serverError = runCatching {
                    Json.parseToJsonElement(responseText).jsonObject["error"]?.jsonPrimitive?.contentOrNull
                }.getOrNull()
                val message = if (serverError.equals("jwt expired", ignoreCase = true)) {
                    "JWT expired"
                } else {
                    "IPTV source sync failed"
                }
                throw IOException(message)
            }
            val payload = runCatching { Json.parseToJsonElement(responseText).jsonObject }
                .getOrElse { throw IOException("Invalid IPTV source sync response") }
            val sources = runCatching { payload["sources"]?.jsonArray }
                .getOrElse { throw IOException("Invalid IPTV source sync response") }
                ?: throw IOException("Missing IPTV source list in sync response")
            IptvSourceSyncSnapshot(
                exists = payload["exists"]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull() == true,
                sourcesJson = sources.toString()
            )
        }
    }
}
