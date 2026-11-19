package com.nuvio.tv.ui.screens.player

import android.util.Log
import com.nuvio.tv.data.freebox.FreeboxPlaybackFile
import com.nuvio.tv.data.freebox.FreeboxPlaybackSession
import com.nuvio.tv.data.freebox.FreeboxRepository
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.Closeable
import java.io.EOFException
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Exposes one authenticated SMB file as a loopback HTTP resource for Media3.
 * Each GET opens an independent random-access handle, while the authenticated CIFS
 * context is reused across ranges to avoid repeating SMB negotiation and login.
 */
internal class FreeboxPlaybackHttpProxy(
    private val repository: FreeboxRepository,
    private val smbPath: String,
) : Closeable {
    private val running = AtomicBoolean(true)
    private val server = ServerSocket().apply {
        reuseAddress = true
        bind(InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 16)
    }
    private val tokenPath = "/${UUID.randomUUID()}/video"
    private val clients = ConcurrentHashMap.newKeySet<Socket>()
    private val openFiles = ConcurrentHashMap.newKeySet<FreeboxPlaybackFile>()
    private val lifecycleLock = Any()
    private val playbackSessionLock = Any()
    @Volatile private var playbackBackend: FreeboxPlaybackBackend? = null
    private val cleanupCompleted = CountDownLatch(1)
    @Volatile private var cleanupFailure: Throwable? = null
    // Do not queue range requests behind reads that Media3 may already have cancelled. A
    // handoff queue lets a new seek get its own worker while bounding stale SMB reads.
    private val workers = ThreadPoolExecutor(
        0,
        MAX_CLIENTS,
        WORKER_KEEP_ALIVE_SECONDS,
        TimeUnit.SECONDS,
        SynchronousQueue(),
    ) { task -> Thread(task, "NuvioFreeboxHttpClient").apply { isDaemon = true } }
    private val playbackProxyRegistration = repository.registerPlaybackProxy(smbPath, ::closeAndAwait)
    private val acceptThread = Thread(::acceptClients, "NuvioFreeboxHttpAccept").apply {
        isDaemon = true
        start()
    }

    val url: String = "http://127.0.0.1:${server.localPort}$tokenPath"

    private fun getPlaybackBackend(): FreeboxPlaybackBackend {
        return synchronized(playbackSessionLock) {
            check(running.get()) { "Le proxy Freebox est fermé." }
            playbackBackend ?: run {
                val openedBackend = try {
                    JcifsPlaybackBackend(repository.openPlaybackSession(smbPath)).also {
                        Log.i(TAG, "FREEBOX_PROXY_BACKEND_SELECTED backend=jCIFS")
                    }
                } catch (jcifsError: Exception) {
                    val firstFile = repository.openPlaybackFile(smbPath)
                    Log.w(
                        TAG,
                        "FREEBOX_PROXY_SMBJ_FALLBACK cause=${jcifsError.javaClass.simpleName}"
                    )
                    SmbjPlaybackBackend(repository, smbPath, firstFile)
                }
                playbackBackend = openedBackend
                openedBackend
            }
        }
    }

    private fun acceptClients() {
        while (running.get()) {
            val client = try {
                server.accept().apply {
                    tcpNoDelay = true
                    soTimeout = CLIENT_TIMEOUT_MS
                    receiveBufferSize = BUFFER_SIZE
                    sendBufferSize = BUFFER_SIZE
                }
            } catch (error: Exception) {
                if (running.get()) Log.e(TAG, "FREEBOX_PROXY_ACCEPT_FAILED", error)
                break
            }

            clients.add(client)
            try {
                workers.execute { serve(client) }
            } catch (_: RejectedExecutionException) {
                clients.remove(client)
                Log.w(TAG, "FREEBOX_PROXY_CLIENT_LIMIT_REACHED max=$MAX_CLIENTS")
                runCatching {
                    client.getOutputStream().apply {
                        write(
                            (
                                "HTTP/1.1 503 Service Unavailable\r\n" +
                                "Retry-After: 1\r\nConnection: close\r\nContent-Length: 0\r\n\r\n"
                            ).toByteArray(StandardCharsets.US_ASCII)
                        )
                        flush()
                    }
                }
                runCatching { client.close() }
            }
        }
    }

    private fun serve(client: Socket) {
        var remote: FreeboxPlaybackFile? = null
        var readOffset = -1L
        try {
            val input = BufferedInputStream(client.getInputStream(), HEADER_BUFFER_SIZE)
            val output = BufferedOutputStream(client.getOutputStream(), BUFFER_SIZE)
            val reader = InputStreamReader(input, StandardCharsets.US_ASCII).buffered()
            val requestLine = reader.readLine() ?: return
            if (requestLine.length > MAX_HEADER_BYTES) {
                writeEmptyResponse(output, 431, "Request Header Fields Too Large")
                return
            }
            val requestParts = requestLine.split(' ', limit = 3)
            if (requestParts.size < 2) {
                writeEmptyResponse(output, 400, "Bad Request")
                return
            }
            val method = requestParts[0].uppercase(Locale.ROOT)
            val target = requestParts[1].substringBefore('?')
            if (target != tokenPath) {
                writeEmptyResponse(output, 404, "Not Found")
                return
            }
            if (method != "GET" && method != "HEAD") {
                output.write("HTTP/1.1 405 Method Not Allowed\r\nAllow: GET, HEAD\r\nConnection: close\r\nContent-Length: 0\r\n\r\n".toByteArray(StandardCharsets.US_ASCII))
                output.flush()
                return
            }

            var headerBytes = requestLine.length
            var rangeHeader: String? = null
            while (true) {
                val line = reader.readLine() ?: break
                headerBytes += line.length + 2
                if (headerBytes > MAX_HEADER_BYTES) {
                    writeEmptyResponse(output, 431, "Request Header Fields Too Large")
                    return
                }
                if (line.isEmpty()) break
                val separator = line.indexOf(':')
                if (separator > 0 && line.substring(0, separator).equals("Range", ignoreCase = true)) {
                    rangeHeader = line.substring(separator + 1).trim()
                }
            }

            val backend = try {
                getPlaybackBackend()
            } catch (error: Exception) {
                Log.e(TAG, "FREEBOX_PROXY_SMB_OPEN_FAILED", error)
                writeEmptyResponse(output, 502, "Bad Gateway")
                return
            }

            val fileLength = backend.length.coerceAtLeast(0L)
            val requestedRange = parseRange(rangeHeader, fileLength)
            if (requestedRange == null) {
                val response = "HTTP/1.1 416 Requested Range Not Satisfiable\r\n" +
                    "Content-Range: bytes */$fileLength\r\n" +
                    "Accept-Ranges: bytes\r\nConnection: close\r\nContent-Length: 0\r\n\r\n"
                output.write(response.toByteArray(StandardCharsets.US_ASCII))
                output.flush()
                return
            }

            val rangeFile = if (method == "GET") {
                try {
                    backend.openFile().also { openedFile ->
                        if (!running.get()) {
                            openedFile.close()
                            return
                        }
                        openFiles.add(openedFile)
                        if (!running.get()) {
                            openFiles.remove(openedFile)
                            openedFile.close()
                            return
                        }
                    }
                } catch (error: Exception) {
                    Log.e(TAG, "FREEBOX_PROXY_SMB_RANGE_OPEN_FAILED", error)
                    writeEmptyResponse(output, 502, "Bad Gateway")
                    return
                }
            } else {
                null
            }
            remote = rangeFile

            val contentLength = requestedRange.end - requestedRange.start + 1L
            val status = if (requestedRange.partial) "206 Partial Content" else "200 OK"
            val headers = StringBuilder()
                .append("HTTP/1.1 ").append(status).append("\r\n")
                .append("Content-Type: video/*\r\n")
                .append("Accept-Ranges: bytes\r\n")
                .append("Content-Length: ").append(contentLength).append("\r\n")
                .append("Connection: close\r\n")
            if (requestedRange.partial) {
                headers.append("Content-Range: bytes ")
                    .append(requestedRange.start).append('-').append(requestedRange.end)
                    .append('/').append(fileLength).append("\r\n")
            }
            headers.append("\r\n")
            output.write(headers.toString().toByteArray(StandardCharsets.US_ASCII))
            output.flush()

            Log.i(
                TAG,
                "FREEBOX_PROXY_RANGE method=$method start=${requestedRange.start} " +
                    "length=$contentLength total=$fileLength"
            )
            if (rangeFile != null) {
                rangeFile.seek(requestedRange.start)
                readOffset = requestedRange.start
                var remaining = contentLength
                val buffer = ByteArray(BUFFER_SIZE)
                while (remaining > 0L && running.get()) {
                    val count = minOf(buffer.size.toLong(), remaining).toInt()
                    val read = rangeFile.read(buffer, 0, count)
                    if (read <= 0) throw EOFException("SMB returned no bytes before the requested range ended")
                    readOffset += read
                    output.write(buffer, 0, read)
                    remaining -= read
                }
                output.flush()
                if (remaining > 0L && running.get()) {
                    throw IOException("Freebox proxy stopped before the requested range was sent")
                }
                Log.i(TAG, "FREEBOX_PROXY_RANGE_DONE start=${requestedRange.start} bytes=$contentLength")
            }
        } catch (error: Exception) {
            synchronized(lifecycleLock) {
                if (running.get()) {
                    val offset = if (readOffset >= 0L) " offset=$readOffset" else ""
                    Log.e(TAG, "FREEBOX_PROXY_REQUEST_FAILED$offset", error)
                }
            }
        } finally {
            remote?.let {
                openFiles.remove(it)
                runCatching { it.close() }
            }
            clients.remove(client)
            runCatching { client.close() }
        }
    }

    private fun parseRange(header: String?, fileLength: Long): ByteRange? {
        if (header.isNullOrBlank()) {
            return ByteRange(start = 0L, end = fileLength - 1L, partial = false)
        }
        if (!header.startsWith("bytes=", ignoreCase = true)) {
            return ByteRange(start = 0L, end = fileLength - 1L, partial = false)
        }
        if (fileLength <= 0L) return null

        val value = header.substringAfter('=').trim()
        if (value.contains(',')) return null // Media3 requests one byte range per connection.
        val bounds = value.split('-', limit = 2)
        if (bounds.size != 2) return null
        val first = bounds[0].trim()
        val last = bounds[1].trim()

        val start: Long
        val end: Long
        if (first.isEmpty()) {
            val suffixLength = last.toLongOrNull()?.takeIf { it > 0L } ?: return null
            start = (fileLength - suffixLength).coerceAtLeast(0L)
            end = fileLength - 1L
        } else {
            start = first.toLongOrNull()?.takeIf { it >= 0L } ?: return null
            if (start >= fileLength) return null
            end = if (last.isEmpty()) fileLength - 1L else {
                last.toLongOrNull()?.takeIf { it >= start }?.coerceAtMost(fileLength - 1L) ?: return null
            }
        }
        return ByteRange(start, end, partial = true)
    }

    private fun writeEmptyResponse(output: BufferedOutputStream, code: Int, reason: String) {
        output.write(
            "HTTP/1.1 $code $reason\r\nConnection: close\r\nContent-Length: 0\r\n\r\n"
                .toByteArray(StandardCharsets.US_ASCII)
        )
        output.flush()
    }

    override fun close() {
        val shouldClose = synchronized(lifecycleLock) { running.compareAndSet(true, false) }
        if (!shouldClose) return
        runCatching { server.close() }
        clients.toList().forEach { runCatching { it.close() } }
        workers.shutdownNow()
        Thread({
            val cleanupFailure = runCatching {
                // Release SMB handles immediately so a following Freebox deletion cannot race a
                // lingering range request that still has the file open.
                closeOpenFiles()
                closePlaybackBackend()
                val workersStopped = workers.awaitTermination(CLEANUP_WORKER_WAIT_MS, TimeUnit.MILLISECONDS)
                if (!workersStopped) {
                    closeOpenFiles()
                    closePlaybackBackend()
                    workers.shutdownNow()
                    workers.awaitTermination(CLEANUP_FINAL_WAIT_MS, TimeUnit.MILLISECONDS)
                }
            }.exceptionOrNull()
            runCatching { playbackProxyRegistration.close() }
            this@FreeboxPlaybackHttpProxy.cleanupFailure = cleanupFailure
            cleanupCompleted.countDown()
            if (cleanupFailure == null) {
                Log.i(TAG, "FREEBOX_PROXY_STOPPED")
            } else {
                Log.e(TAG, "FREEBOX_PROXY_CLEANUP_FAILED", cleanupFailure)
            }
        }, "NuvioFreeboxProxyCleanup").apply {
            isDaemon = true
            start()
        }
    }

    private fun closeAndAwait() {
        close()
        try {
            if (!cleanupCompleted.await(CLEANUP_WAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                throw IOException("L’arrêt du lecteur Freebox n’a pas été confirmé à temps.")
            }
        } catch (error: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IOException("L’arrêt du lecteur Freebox a été interrompu.", error)
        }
        cleanupFailure?.let { error ->
            throw IOException("Le lecteur Freebox n’a pas confirmé la fermeture de ses fichiers.", error)
        }
    }

    private fun closeOpenFiles() {
        openFiles.toList().forEach { file ->
            openFiles.remove(file)
            runCatching { file.close() }
        }
    }

    private fun closePlaybackBackend() {
        synchronized(playbackSessionLock) {
            val backend = playbackBackend
            playbackBackend = null
            backend?.close()
        }
    }

    private data class ByteRange(val start: Long, val end: Long, val partial: Boolean)

    private companion object {
        const val TAG = "FreeboxPlaybackProxy"
        const val MAX_CLIENTS = 12
        const val WORKER_KEEP_ALIVE_SECONDS = 30L
        const val MAX_HEADER_BYTES = 32 * 1024
        const val HEADER_BUFFER_SIZE = 16 * 1024
        const val BUFFER_SIZE = 256 * 1024
        const val CLIENT_TIMEOUT_MS = 120_000
        const val CLEANUP_WORKER_WAIT_MS = 2_000L
        const val CLEANUP_FINAL_WAIT_MS = 1_000L
        const val CLEANUP_WAIT_TIMEOUT_MS = 10_000L
    }
}

private sealed interface FreeboxPlaybackBackend : Closeable {
    val length: Long
    fun openFile(): FreeboxPlaybackFile
}

private class JcifsPlaybackBackend(
    private val session: FreeboxPlaybackSession,
) : FreeboxPlaybackBackend {
    override val length: Long get() = session.length

    override fun openFile(): FreeboxPlaybackFile = session.openFile()

    override fun close() = session.close()
}

private class SmbjPlaybackBackend(
    private val repository: FreeboxRepository,
    private val smbPath: String,
    firstFile: FreeboxPlaybackFile,
) : FreeboxPlaybackBackend {
    override val length: Long = firstFile.length
    private var firstFile: FreeboxPlaybackFile? = firstFile

    override fun openFile(): FreeboxPlaybackFile = synchronized(this) {
        firstFile?.also { firstFile = null } ?: repository.openPlaybackFile(smbPath)
    }

    override fun close() = synchronized(this) {
        firstFile?.close()
        firstFile = null
    }
}
