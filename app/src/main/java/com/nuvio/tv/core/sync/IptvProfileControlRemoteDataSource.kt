package com.nuvio.tv.core.sync

import com.nuvio.tv.BuildConfig
import io.github.jan.supabase.auth.Auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val IPTV_PROFILE_CONTROL_SCHEMA_VERSION = 2

data class IptvProfileControlSnapshot(
    val exists: Boolean,
    val config: JSONObject,
    val catalog: JSONObject,
    val progress: JSONObject,
    val progressTombstones: JSONObject,
    val configRevision: Long,
    val catalogRevision: Long,
    val progressRevision: Long,
    val updatedAt: String?,
    val catalogUpdatedAt: String?,
    val conflict: Boolean = false,
)

data class IptvProfileDevice(
    val id: String,
    val name: String,
    val firstSeenAt: String?,
    val lastSeenAt: String?,
)

data class IptvRecordingPlanSnapshot(
    val exists: Boolean,
    val plan: JSONObject,
    val revision: Long,
    val updatedAt: String?,
    val conflict: Boolean = false,
)

@Singleton
class IptvProfileControlRemoteDataSource @Inject constructor(
    private val auth: Auth,
    private val okHttpClient: OkHttpClient,
) {
    suspend fun pull(profileId: Int): IptvProfileControlSnapshot = request(
        JSONObject().put("action", "pull").put("profileId", profileId),
    )

    suspend fun pushConfig(
        profileId: Int,
        config: JSONObject,
        expectedRevision: Long,
        changedFields: Set<String>? = null,
        bootstrapConfig: Boolean = false,
    ): IptvProfileControlSnapshot {
        val body = JSONObject()
            .put("action", "push_config")
            .put("profileId", profileId)
            .put("config", config)
            .put("expectedRevision", expectedRevision)
        changedFields?.let { body.put("changedFields", JSONArray(it.sorted())) }
        if (bootstrapConfig) body.put("bootstrapConfig", true)
        return request(body)
    }

    suspend fun pushCatalog(
        profileId: Int,
        catalog: JSONObject,
        expectedRevision: Long,
    ): IptvProfileControlSnapshot = request(
        JSONObject()
            .put("action", "push_catalog")
            .put("profileId", profileId)
            .put("catalog", catalog)
            .put("expectedRevision", expectedRevision),
    )

    suspend fun pushProgress(
        profileId: Int,
        progress: JSONArray,
        expectedRevision: Long,
    ): IptvProfileControlSnapshot = request(
        JSONObject()
            .put("action", "push_progress")
            .put("profileId", profileId)
            .put("progress", progress)
            .put("expectedRevision", expectedRevision),
    )

    suspend fun deleteProgress(
        profileId: Int,
        progressKeys: Collection<String>,
        expectedRevision: Long,
    ): IptvProfileControlSnapshot = request(
        JSONObject()
            .put("action", "delete_progress")
            .put("profileId", profileId)
            .put("progressKeys", JSONArray(progressKeys))
            .put("expectedRevision", expectedRevision),
    )

    suspend fun registerDevice(profileId: Int, deviceId: String, deviceName: String): List<IptvProfileDevice> {
        val result = requestPayload(
            JSONObject()
                .put("action", "register_device")
                .put("profileId", profileId)
                .put("deviceId", deviceId)
                .put("deviceName", deviceName),
        )
        return result.optJSONArray("devices").toDeviceList()
    }

    suspend fun listDevices(profileId: Int): List<IptvProfileDevice> = requestPayload(
        JSONObject().put("action", "list_devices").put("profileId", profileId),
    ).optJSONArray("devices").toDeviceList()

    suspend fun pullRecordingPlan(profileId: Int, deviceId: String): IptvRecordingPlanSnapshot = requestPayload(
        JSONObject()
            .put("action", "pull_recording_plan")
            .put("profileId", profileId)
            .put("deviceId", deviceId),
    ).toRecordingPlanSnapshot()

    suspend fun pushRecordingPlan(
        profileId: Int,
        deviceId: String,
        plan: JSONObject,
        expectedRevision: Long,
    ): IptvRecordingPlanSnapshot = requestPayload(
        JSONObject()
            .put("action", "push_recording_plan")
            .put("profileId", profileId)
            .put("deviceId", deviceId)
            .put("plan", plan)
            .put("expectedRevision", expectedRevision),
    ).toRecordingPlanSnapshot()

    private suspend fun request(body: JSONObject): IptvProfileControlSnapshot {
        val payload = requestPayload(body)
        return payload.toSnapshot(conflict = payload.optString("error") == "revision_conflict")
    }

    private suspend fun requestPayload(body: JSONObject): JSONObject = withContext(Dispatchers.IO) {
        val accessToken = auth.currentAccessTokenOrNull()?.takeIf(String::isNotBlank)
            ?: throw IOException("JojoTV access token is unavailable")
        val url = BuildConfig.IPTV_SOURCE_SYNC_URL.trimEnd('/') + "/functions/v1/iptv-profile-control"
        val versionedBody = JSONObject(body.toString())
            .put("profileControlSchemaVersion", IPTV_PROFILE_CONTROL_SCHEMA_VERSION)
        val request = Request.Builder()
            .url(url)
            .header("apikey", BuildConfig.IPTV_SOURCE_SYNC_PUBLISHABLE_KEY)
            .header("Authorization", "Bearer $accessToken")
            .header("Accept", "application/json")
            .post(versionedBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val responseText = response.body?.string().orEmpty()
            val payload = runCatching { JSONObject(responseText) }
                .getOrElse { throw IOException("Invalid IPTV profile control response") }
            if (response.code == 409 && payload.optString("error") == "revision_conflict") {
                return@withContext payload
            }
            if (!response.isSuccessful) {
                val message = when {
                    payload.optString("error").equals("jwt expired", ignoreCase = true) -> "JWT expired"
                    payload.optString("error") == "client_upgrade_required" -> "IPTV sync requires an app update"
                    else -> "IPTV profile control sync failed"
                }
                throw IOException(message)
            }
            payload
        }
    }

    private fun JSONArray?.toDeviceList(): List<IptvProfileDevice> = this?.let { rows ->
        (0 until rows.length()).mapNotNull { index ->
            rows.optJSONObject(index)?.let { row ->
                val id = row.optString("device_id").takeIf(String::isNotBlank) ?: return@let null
                IptvProfileDevice(
                    id = id,
                    name = row.optString("device_name").ifBlank { id },
                    firstSeenAt = row.optString("first_seen_at").takeIf(String::isNotBlank),
                    lastSeenAt = row.optString("last_seen_at").takeIf(String::isNotBlank),
                )
            }
        }
    }.orEmpty()

    private fun JSONObject.toRecordingPlanSnapshot(conflict: Boolean = false) = IptvRecordingPlanSnapshot(
        exists = optBoolean("exists"),
        plan = optJSONObject("plan") ?: JSONObject(),
        revision = optLong("revision"),
        updatedAt = optString("updatedAt").takeIf(String::isNotBlank),
        conflict = conflict || optString("error") == "revision_conflict",
    )

    private fun JSONObject.toSnapshot(conflict: Boolean = false) = IptvProfileControlSnapshot(
        exists = optBoolean("exists"),
        config = optJSONObject("config") ?: JSONObject(),
        catalog = optJSONObject("catalog") ?: JSONObject(),
        progress = optJSONObject("progress") ?: JSONObject(),
        progressTombstones = optJSONObject("progressTombstones") ?: JSONObject(),
        configRevision = optLong("configRevision"),
        catalogRevision = optLong("catalogRevision"),
        progressRevision = optLong("progressRevision"),
        updatedAt = optString("updatedAt").takeIf(String::isNotBlank),
        catalogUpdatedAt = optString("catalogUpdatedAt").takeIf(String::isNotBlank),
        conflict = conflict,
    )
}
