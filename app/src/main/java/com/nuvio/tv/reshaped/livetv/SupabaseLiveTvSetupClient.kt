package com.nuvio.tv.reshaped.livetv

import android.os.SystemClock
import android.util.Base64
import java.io.IOException
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject

/** Short-lived, encrypted handoff from a phone browser to the TV through the Jojotv Supabase project. */
internal object SupabaseLiveTvSetupClient {
    const val PHONE_PAGE_URL =
        "https://nuvio-live-tv-setup.pages.dev/"

    private const val API_URL = "https://wchygnolhydguirzmqwb.supabase.co/functions/v1/live-tv-setup"
    private const val PUBLISHABLE_KEY = "sb_publishable_aAZuLBeKS8__8ZGETYvVfg_U07PPU3V"
    private const val API_HOST = "wchygnolhydguirzmqwb.supabase.co"
    private const val POLL_INTERVAL_MS = 2_000L
    private const val SESSION_TTL_MS = 15 * 60 * 1_000L
    private const val MAX_FORM_BYTES = 16 * 1024L
    private const val PLAYLIST_BUCKET = "nuvio-live-tv-setup-files"
    private const val HKDF_INFO = "nuvio-live-tv-setup-v1"

    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .callTimeout(7, TimeUnit.MINUTES)
        .build()

    internal data class Session(
        val phoneToken: String,
        val tvToken: String,
        val keyPair: KeyPair,
        val publicKey: String,
        val expiresAtElapsedMs: Long,
    )

    fun qrUrl(session: Session, language: String): String = buildString {
        append(PHONE_PAGE_URL)
        append("#phone=").append(session.phoneToken)
        append("&key=").append(session.publicKey)
        append("&lang=").append(if (language == "fr") "fr" else "en")
    }

    suspend fun createSession(): Session = withContext(Dispatchers.IO) {
        val phoneToken = newToken()
        val tvToken = newToken()
        val keyPair = KeyPairGenerator.getInstance("EC").run {
            initialize(ECGenParameterSpec("secp256r1"), SecureRandom())
            generateKeyPair()
        }
        val publicKey = encodeUrlSafe(keyPair.public.encoded)
        val response = post(
            JSONObject()
                .put("action", "create")
                .put("phoneToken", phoneToken)
                .put("tvToken", tvToken),
        )
        if (response.optString("status") != "created") throw IOException("relay_create_failed")
        Session(
            phoneToken = phoneToken,
            tvToken = tvToken,
            keyPair = keyPair,
            publicKey = publicKey,
            expiresAtElapsedMs = SystemClock.elapsedRealtime() + SESSION_TTL_MS,
        )
    }

    suspend fun waitForPhoneSetup(session: Session) {
        var consecutiveFailures = 0
        while (currentCoroutineContext().isActive && SystemClock.elapsedRealtime() < session.expiresAtElapsedMs) {
            delay(POLL_INTERVAL_MS)
            val response = try {
                post(JSONObject().put("action", "poll").put("tvToken", session.tvToken))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                consecutiveFailures++
                if (consecutiveFailures >= 5) throw IOException("relay_unavailable")
                continue
            }
            consecutiveFailures = 0
            when (response.optString("status")) {
                "pending" -> Unit
                "expired" -> return
                "ready" -> {
                    acceptPayload(session, response)
                    return
                }
                else -> throw IOException("relay_invalid_response")
            }
        }
    }

    suspend fun cancel(session: Session) {
        withContext(Dispatchers.IO + NonCancellable) {
            runCatching { post(JSONObject().put("action", "cancel").put("tvToken", session.tvToken)) }
        }
    }

    private suspend fun acceptPayload(session: Session, response: JSONObject) {
        val envelope = response.optJSONObject("payload") ?: throw IOException("relay_invalid_payload")
        val phonePublicKey = envelopePublicKey(envelope)
        val payload = decryptJson(session, envelope)
        when (payload.optString("type")) {
            "m3u" -> {
                val url = payload.optString("url").trim()
                if (!url.isHttpUrl()) throw IOException("relay_invalid_payload")
                withContext(Dispatchers.Main.immediate) { LiveTvRepository.loadM3uUrl(url) }
            }
            "xtream" -> {
                val settings = LiveTvXtreamSettings(
                    serverUrl = payload.optString("server"),
                    username = payload.optString("username"),
                    password = payload.optString("password"),
                ).normalized()
                if (!settings.isConfigured || !settings.serverUrl.isHttpUrl()) throw IOException("relay_invalid_payload")
                withContext(Dispatchers.Main.immediate) { LiveTvRepository.loadXtream(settings) }
            }
            "stalker" -> {
                val settings = LiveTvStalkerSettings(
                    portalUrl = payload.optString("portal"),
                    macAddress = payload.optString("mac"),
                    username = payload.optString("username"),
                    password = payload.optString("password"),
                ).normalized()
                if (!settings.isConfigured || !settings.portalUrl.isHttpUrl()) throw IOException("relay_invalid_payload")
                withContext(Dispatchers.Main.immediate) { LiveTvRepository.loadStalker(settings) }
            }
            "playlist" -> receivePlaylist(session, payload, response.optString("playlistUrl"), phonePublicKey)
            else -> throw IOException("relay_invalid_payload")
        }
    }

    private suspend fun receivePlaylist(
        session: Session,
        payload: JSONObject,
        signedUrl: String,
        phonePublicKey: ByteArray,
    ) {
        val url = signedUrl.toHttpUrlOrNull()
            ?: throw IOException("relay_invalid_download")
        if (url.scheme != "https" || url.host != API_HOST ||
            !url.encodedPath.startsWith("/storage/v1/object/sign/$PLAYLIST_BUCKET/")
        ) throw IOException("relay_invalid_download")

        try {
            withContext(Dispatchers.IO) {
                http.newCall(Request.Builder().url(url).get().build()).await().use { response ->
                    if (!response.isSuccessful) throw IOException("relay_download_failed")
                    val body = response.body ?: throw IOException("relay_empty_download")
                    val key = deriveKey(session, phonePublicKey)
                    val iv = decodeUrlSafe(payload.getString("fileIv"))
                    val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
                        init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
                    }
                    CipherInputStream(body.byteStream(), cipher).use { input ->
                        val fileName = payload.optString("fileName").substringAfterLast('/').take(80)
                        if (!LiveTvRepository.importPlaylist(fileName, input, LiveTvSetupServer.MAX_PLAYLIST_BYTES)) {
                            throw IOException("relay_playlist_import_failed")
                        }
                    }
                }
            }
        } finally {
            withContext(Dispatchers.IO + NonCancellable) {
                runCatching { post(JSONObject().put("action", "complete").put("tvToken", session.tvToken)) }
            }
        }
    }

    private fun decryptJson(session: Session, envelope: JSONObject): JSONObject {
        val key = deriveKey(session, envelopePublicKey(envelope))
        val iv = decodeUrlSafe(envelope.getString("iv"))
        val ciphertext = decodeUrlSafe(envelope.getString("ciphertext"))
        if (iv.size != 12 || ciphertext.size < 16 || ciphertext.size > MAX_FORM_BYTES) {
            throw IOException("relay_invalid_payload")
        }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        }
        val plaintext = cipher.doFinal(ciphertext)
        return try {
            JSONObject(plaintext.toString(Charsets.UTF_8))
        } finally {
            plaintext.fill(0)
        }
    }

    private fun envelopePublicKey(envelope: JSONObject): ByteArray {
        val encoded = decodeUrlSafe(envelope.getString("publicKey"))
        if (encoded.size !in 80..256) throw IOException("relay_invalid_payload")
        return encoded
    }

    private fun deriveKey(session: Session, phonePublicKey: ByteArray): SecretKeySpec {
        val publicKey = java.security.KeyFactory.getInstance("EC")
            .generatePublic(X509EncodedKeySpec(phonePublicKey))
        val agreement = KeyAgreement.getInstance("ECDH").apply {
            init(session.keyPair.private)
            doPhase(publicKey, true)
        }
        val sharedSecret = agreement.generateSecret()
        val salt = decodeUrlSafe(session.phoneToken)
        val hmac = Mac.getInstance("HmacSHA256")
        val pseudoRandomKey = hmac.run {
            init(SecretKeySpec(salt, "HmacSHA256"))
            doFinal(sharedSecret)
        }
        val output = hmac.run {
            init(SecretKeySpec(pseudoRandomKey, "HmacSHA256"))
            update(HKDF_INFO.toByteArray(Charsets.UTF_8))
            doFinal(byteArrayOf(1))
        }
        sharedSecret.fill(0)
        pseudoRandomKey.fill(0)
        salt.fill(0)
        return SecretKeySpec(output.copyOf(32), "AES").also { output.fill(0) }
    }

    private suspend fun post(json: JSONObject): JSONObject = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(API_URL)
            .header("apikey", PUBLISHABLE_KEY)
            .header("Cache-Control", "no-store")
            .post(json.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()
        http.newCall(request).apply { timeout().timeout(25, TimeUnit.SECONDS) }.await().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) throw IOException("relay_http_${response.code}")
            JSONObject(body)
        }
    }

    private fun newToken(): String {
        val bytes = ByteArray(32).also(SecureRandom()::nextBytes)
        return encodeUrlSafe(bytes).also { bytes.fill(0) }
    }

    private fun encodeUrlSafe(value: ByteArray): String =
        Base64.encodeToString(value, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)

    private fun decodeUrlSafe(value: String): ByteArray =
        Base64.decode(value, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)

    private suspend fun okhttp3.Call.await(): okhttp3.Response = suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation { cancel() }
        enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, error: IOException) {
                if (continuation.isActive) continuation.resumeWith(Result.failure(error))
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!continuation.isActive) {
                    response.close()
                } else {
                    runCatching { continuation.resumeWith(Result.success(response)) }
                        .onFailure { response.close() }
                }
            }
        })
    }
}
