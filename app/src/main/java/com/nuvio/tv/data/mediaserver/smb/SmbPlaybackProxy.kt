package com.nuvio.tv.data.mediaserver.smb

import com.hierynomus.smbj.share.File as SmbFile
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.Closeable
import java.io.ByteArrayOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.atomic.AtomicBoolean

/** Serves an authenticated SMB file to Media3 over a private loopback HTTP endpoint. */
internal class SmbPlaybackProxy(
    val connectionId: String,
    address: String,
    username: String,
    password: String,
    private val relativePath: String,
) : Closeable {
    private val smb = SmbShareSession.open(SmbAddress.parse(address), username, password)
    private val running = AtomicBoolean(true)
    private val clients = ConcurrentHashMap.newKeySet<Socket>()
    private val openFiles = ConcurrentHashMap.newKeySet<SmbFile>()
    private val workers = Executors.newFixedThreadPool(MAX_CLIENTS) { task ->
        Thread(task, "NuvioSmbHttpClient").apply { isDaemon = true }
    }
    private val tokenPath = "/${UUID.randomUUID()}/video"
    private val fileLength: Long
    private val server: ServerSocket
    private val acceptThread: Thread

    init {
        try {
            fileLength = smb.openFile(relativePath).use { it.length }
            server = ServerSocket().apply {
                reuseAddress = true
                bind(InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 12)
            }
        } catch (error: Exception) {
            smb.close()
            throw error
        }
        acceptThread = Thread(::acceptClients, "NuvioSmbHttpAccept").apply {
            isDaemon = true
            start()
        }
    }

    val url: String get() = "http://127.0.0.1:${server.localPort}$tokenPath"

    private fun acceptClients() {
        while (running.get()) {
            val client = try {
                server.accept().apply {
                    tcpNoDelay = true
                    soTimeout = CLIENT_TIMEOUT_MS
                    receiveBufferSize = BUFFER_SIZE
                    sendBufferSize = BUFFER_SIZE
                }
            } catch (_: Exception) {
                break
            }
            clients += client
            try {
                workers.execute { serve(client) }
            } catch (_: RejectedExecutionException) {
                clients -= client
                runCatching { client.close() }
            }
        }
    }

    private fun serve(client: Socket) {
        var remoteFile: SmbFile? = null
        try {
            val input = BufferedInputStream(client.getInputStream(), BUFFER_SIZE)
            val output = BufferedOutputStream(client.getOutputStream(), BUFFER_SIZE)
            val requestLine = readLine(input) ?: return
            val request = requestLine.trim().split(' ', limit = 3)
            if (request.size < 2) {
                writeError(output, 400, "Bad Request")
                return
            }
            val method = request[0].uppercase()
            val requestPath = request[1].substringBefore('?')
            val headers = mutableMapOf<String, String>()
            while (true) {
                val line = readLine(input) ?: break
                if (line.isEmpty()) break
                val separator = line.indexOf(':')
                if (separator > 0) headers[line.substring(0, separator).trim().lowercase()] = line.substring(separator + 1).trim()
            }
            if (requestPath != tokenPath) {
                writeError(output, 404, "Not Found")
                return
            }
            if (method != "GET" && method != "HEAD") {
                writeError(output, 405, "Method Not Allowed")
                return
            }

            val range = parseRange(headers["range"])
            if (range == null) {
                val response = buildString {
                    append("HTTP/1.1 416 Range Not Satisfiable\r\n")
                    append("Content-Range: bytes */").append(fileLength).append("\r\n")
                    append("Content-Length: 0\r\nConnection: close\r\n\r\n")
                }
                output.write(response.toByteArray(StandardCharsets.US_ASCII))
                output.flush()
                return
            }

            if (method == "GET") {
                remoteFile = smb.openFile(relativePath)
                openFiles += remoteFile
            }
            val contentLength = if (fileLength == 0L) 0L else range.end - range.start + 1L
            val response = buildString {
                append(if (range.partial) "HTTP/1.1 206 Partial Content\r\n" else "HTTP/1.1 200 OK\r\n")
                append("Content-Type: ").append(contentType(relativePath)).append("\r\n")
                append("Accept-Ranges: bytes\r\n")
                append("Content-Length: ").append(contentLength).append("\r\n")
                if (range.partial) {
                    append("Content-Range: bytes ").append(range.start).append('-').append(range.end)
                        .append('/').append(fileLength).append("\r\n")
                }
                append("Connection: close\r\n\r\n")
            }
            output.write(response.toByteArray(StandardCharsets.US_ASCII))
            output.flush()

            val file = remoteFile ?: return
            var position = range.start
            var remaining = contentLength
            val buffer = ByteArray(BUFFER_SIZE)
            while (remaining > 0L && running.get()) {
                val count = minOf(buffer.size.toLong(), remaining).toInt()
                val read = file.read(buffer, position, 0, count)
                if (read <= 0) break
                output.write(buffer, 0, read)
                position += read
                remaining -= read
            }
            output.flush()
        } catch (_: Exception) {
            // Media3 closes range requests when it seeks or changes tracks.
        } finally {
            remoteFile?.let { openFiles -= it; runCatching { it.close() } }
            clients -= client
            runCatching { client.close() }
        }
    }

    private fun parseRange(header: String?): ByteRange? {
        if (header.isNullOrBlank()) return ByteRange(0L, (fileLength - 1L).coerceAtLeast(0L), partial = false)
        if (fileLength <= 0L || !header.startsWith("bytes=", ignoreCase = true)) return null
        val range = header.substringAfter('=').substringBefore(',').trim()
        val separator = range.indexOf('-')
        if (separator < 0) return null
        val startText = range.substring(0, separator).trim()
        val endText = range.substring(separator + 1).trim()
        val start: Long
        val end: Long
        if (startText.isEmpty()) {
            val suffix = endText.toLongOrNull()?.takeIf { it > 0L } ?: return null
            start = (fileLength - suffix).coerceAtLeast(0L)
            end = fileLength - 1L
        } else {
            start = startText.toLongOrNull()?.takeIf { it >= 0L && it < fileLength } ?: return null
            end = endText.toLongOrNull()?.takeIf { it >= start }?.coerceAtMost(fileLength - 1L) ?: (fileLength - 1L)
        }
        return ByteRange(start, end, partial = true)
    }

    private fun contentType(path: String): String = when (path.substringAfterLast('.', "").lowercase()) {
        "mp4", "m4v" -> "video/mp4"
        "mkv" -> "video/x-matroska"
        "ts", "m2ts" -> "video/mp2t"
        "webm" -> "video/webm"
        "mov" -> "video/quicktime"
        "avi" -> "video/x-msvideo"
        else -> "application/octet-stream"
    }

    private fun readLine(input: BufferedInputStream): String? {
        val bytes = ByteArrayOutputStream()
        while (bytes.size() <= MAX_HEADER_LINE_BYTES) {
            val byte = input.read()
            if (byte < 0) return bytes.toByteArray().takeIf { it.isNotEmpty() }
                ?.let { String(it, StandardCharsets.ISO_8859_1) }
            if (byte == '\n'.code) break
            if (byte != '\r'.code) bytes.write(byte)
        }
        if (bytes.size() > MAX_HEADER_LINE_BYTES) throw IllegalArgumentException("HTTP header line is too long")
        return String(bytes.toByteArray(), StandardCharsets.ISO_8859_1)
    }

    private fun writeError(output: BufferedOutputStream, status: Int, reason: String) {
        output.write(
            "HTTP/1.1 $status $reason\r\nContent-Length: 0\r\nConnection: close\r\n\r\n"
                .toByteArray(StandardCharsets.US_ASCII),
        )
        output.flush()
    }

    override fun close() {
        if (!running.compareAndSet(true, false)) return
        runCatching { server.close() }
        clients.forEach { runCatching { it.close() } }
        openFiles.forEach { runCatching { it.close() } }
        workers.shutdownNow()
        smb.close()
    }

    private data class ByteRange(val start: Long, val end: Long, val partial: Boolean)

    private companion object {
        const val MAX_CLIENTS = 4
        const val BUFFER_SIZE = 256 * 1024
        const val CLIENT_TIMEOUT_MS = 30_000
        const val MAX_HEADER_LINE_BYTES = 16 * 1024
    }
}
