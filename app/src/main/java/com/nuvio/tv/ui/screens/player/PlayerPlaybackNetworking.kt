package com.nuvio.tv.ui.screens.player

import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import com.nuvio.tv.core.network.IPv4FirstDns
import okhttp3.OkHttpClient
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

internal object PlayerPlaybackNetworking {
    private val trustAllManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit

        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    }

    private val playbackHostnameVerifier = HostnameVerifier { _, _ -> true }

    private val sslContext: SSLContext by lazy {
        SSLContext.getInstance("TLS").apply {
            init(null, arrayOf<TrustManager>(trustAllManager), SecureRandom())
        }
    }

    private val playbackHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .dns(IPv4FirstDns())
            .sslSocketFactory(sslContext.socketFactory, trustAllManager)
            .hostnameVerifier(playbackHostnameVerifier)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build()
    }

    @OptIn(UnstableApi::class)
    fun createHttpDataSourceFactory(defaultHeaders: Map<String, String> = emptyMap()): DataSource.Factory {
        val stickyHeaders = defaultHeaders.filterKeys { key ->
            key.equals("Authorization", ignoreCase = true) ||
                key.equals("X-Fbx-App-Auth", ignoreCase = true) ||
                key.equals("Cookie", ignoreCase = true)
        }
        val client = if (stickyHeaders.isNotEmpty()) {
            // Some servers/redirects drop auth/session headers. Reapply the
            // sensitive playback headers to every network request, which is
            // closer to Kodi/mpv behavior for direct NAS/Freebox playback.
            playbackHttpClient.newBuilder()
                .addNetworkInterceptor { chain ->
                    val request = chain.request()
                    val builder = request.newBuilder()
                    stickyHeaders.forEach { (name, value) ->
                        if (request.header(name) == null) {
                            builder.header(name, value)
                        }
                    }
                    chain.proceed(builder.build())
                }
                .build()
        } else {
            playbackHttpClient
        }
        return OkHttpDataSource.Factory(client).apply {
            setDefaultRequestProperties(defaultHeaders + defaultPlaybackHeaders(defaultHeaders))
            if (defaultHeaders.none { it.key.equals("User-Agent", ignoreCase = true) }) {
                setUserAgent(PlayerMediaSourceFactory.DEFAULT_USER_AGENT)
            }
        }
    }

    private fun defaultPlaybackHeaders(headers: Map<String, String>): Map<String, String> {
        val result = LinkedHashMap<String, String>()
        if (headers.none { it.key.equals("Accept", ignoreCase = true) }) result["Accept"] = "*/*"
        if (headers.none { it.key.equals("Connection", ignoreCase = true) }) result["Connection"] = "keep-alive"
        return result
    }
    @OptIn(UnstableApi::class)
    fun createDataSourceFactory(
        context: android.content.Context,
        defaultHeaders: Map<String, String> = emptyMap()
    ): DataSource.Factory {
        return DefaultDataSource.Factory(context, createHttpDataSourceFactory(defaultHeaders))
    }

    fun openConnection(
        url: String,
        headers: Map<String, String>,
        method: String,
        connectTimeoutMs: Int,
        readTimeoutMs: Int,
        range: String? = null
    ): HttpURLConnection {
        return (URL(url).openConnection() as HttpURLConnection).apply {
            if (this is HttpsURLConnection) {
                sslSocketFactory = sslContext.socketFactory
                hostnameVerifier = playbackHostnameVerifier
            }
            instanceFollowRedirects = true
            connectTimeout = connectTimeoutMs
            readTimeout = readTimeoutMs
            requestMethod = method
            setRequestProperty("User-Agent", headers["User-Agent"] ?: PlayerMediaSourceFactory.DEFAULT_USER_AGENT)
            headers.forEach { (key, value) ->
                if (key.equals("Range", ignoreCase = true)) return@forEach
                if (key.equals("User-Agent", ignoreCase = true)) return@forEach
                setRequestProperty(key, value)
            }
            range?.let { setRequestProperty("Range", it) }
        }
    }
}