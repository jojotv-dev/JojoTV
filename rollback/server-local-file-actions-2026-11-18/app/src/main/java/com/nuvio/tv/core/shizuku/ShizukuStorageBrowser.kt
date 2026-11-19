package com.nuvio.tv.core.shizuku

import android.content.pm.PackageManager
import android.os.ParcelFileDescriptor
import moe.shizuku.server.IShizukuService
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

internal const val SHIZUKU_STORAGE_PATH_PREFIX = "shizuku-storage:"

internal data class ShizukuStorageEntry(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val sizeBytes: Long,
)

internal object ShizukuStorageBrowser {
    enum class Availability { READY, PERMISSION_REQUIRED, UNAVAILABLE }

    fun availability(): Availability = try {
        if (!Shizuku.pingBinder()) {
            Availability.UNAVAILABLE
        } else if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            Availability.READY
        } else {
            Availability.PERMISSION_REQUIRED
        }
    } catch (_: Throwable) {
        Availability.UNAVAILABLE
    }

    fun list(path: String): List<ShizukuStorageEntry> {
        val quotedPath = shellQuote(path)
        val command = "dir=$quotedPath; [ -d \"\$dir\" ] || exit 2; " +
            "for f in \"\$dir\"/* \"\$dir\"/.[!.]* \"\$dir\"/..?*; do " +
            "[ -e \"\$f\" ] || [ -L \"\$f\" ] || continue; name=\${f##*/}; " +
            "if [ -d \"\$f\" ]; then kind=d; size=0; " +
            "else kind=f; size=\$(stat -c %s \"\$f\" 2>/dev/null || echo 0); fi; " +
            "printf '%s\\t%s\\t%s\\t%s\\n' \"\$name\" \"\$f\" \"\$kind\" \"\$size\"; done"
        return runShell(command)
            .lineSequence()
            .mapNotNull { line ->
                val parts = line.split('\t', limit = 4)
                if (parts.size < 4) return@mapNotNull null
                val sizeBytes = parts[3].toLongOrNull()?.coerceAtLeast(0L) ?: 0L
                ShizukuStorageEntry(
                    name = parts[0],
                    path = parts[1],
                    isDirectory = parts[2] == "d",
                    sizeBytes = sizeBytes,
                )
            }
            .sortedWith(compareByDescending<ShizukuStorageEntry> { it.isDirectory }.thenBy { it.name.lowercase() })
            .toList()
    }

    fun copyToFile(
        path: String,
        destination: File,
        totalBytes: Long,
        onProgress: (copiedBytes: Long, totalBytes: Long) -> Unit,
    ) {
        val process = startProcess("sh", "-c", "cat -- ${shellQuote(path)}")
        try {
            val input = ParcelFileDescriptor.AutoCloseInputStream(process.inputStream)
            val error = ParcelFileDescriptor.AutoCloseInputStream(process.errorStream)
            input.use { source ->
                FileOutputStream(destination).buffered().use { output ->
                    val buffer = ByteArray(64 * 1024)
                    var copiedBytes = 0L
                    onProgress(0L, totalBytes)
                    while (true) {
                        val count = source.read(buffer)
                        if (count < 0) break
                        output.write(buffer, 0, count)
                        copiedBytes += count
                        onProgress(copiedBytes, totalBytes)
                    }
                }
            }
            val errorText = error.use(::readText)
            if (process.waitFor() != 0) {
                throw IOException(errorText.ifBlank { "Lecture Shizuku impossible." })
            }
        } catch (error: Exception) {
            destination.delete()
            throw error
        }
    }

    private fun runShell(command: String): String {
        val process = startProcess("sh", "-c", command)
        val input = ParcelFileDescriptor.AutoCloseInputStream(process.inputStream)
        val error = ParcelFileDescriptor.AutoCloseInputStream(process.errorStream)
        val stdout = input.use(::readText)
        val stderr = error.use(::readText)
        if (process.waitFor() != 0) {
            throw IOException(stderr.ifBlank { "Lecture Shizuku impossible." })
        }
        return stdout
    }

    private fun startProcess(vararg command: String) =
        when (availability()) {
            Availability.READY -> {
                val service = IShizukuService.Stub.asInterface(Shizuku.getBinder())
                    ?: throw IllegalStateException("Service Shizuku indisponible.")
                service.newProcess(command, null, null)
            }
            Availability.PERMISSION_REQUIRED -> throw IllegalStateException(
                "Accorde l’autorisation de JojoTV dans Shizuku pour lire Android/data.",
            )
            Availability.UNAVAILABLE -> throw IllegalStateException(
                "Démarre Shizuku pour lire ce dossier protégé.",
            )
        }

    private fun readText(input: InputStream): String =
        BufferedReader(InputStreamReader(input)).use { reader ->
            buildString {
                var firstLine = true
                while (true) {
                    val line = reader.readLine() ?: break
                    if (!firstLine) append('\n')
                    append(line)
                    firstLine = false
                }
            }
        }

    private fun shellQuote(value: String): String = "'${value.replace("'", "'\"'\"'")}'"
}
