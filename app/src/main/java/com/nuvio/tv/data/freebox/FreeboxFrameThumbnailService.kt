package com.nuvio.tv.data.freebox

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.nuvio.tv.data.local.FreeboxSettingsDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FreeboxFrameThumbnailService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val freeboxOsClient: FreeboxOsClient,
    private val freeboxSettingsDataStore: FreeboxSettingsDataStore
) {
    private val extractionLocks = ConcurrentHashMap<String, Mutex>()

    suspend fun thumbnailUri(entry: FreeboxFileEntry): String? =
        thumbnailUri(entry = entry, positionUs = 10_000_000L)

    suspend fun thumbnailUris(entry: FreeboxFileEntry, positionsUs: List<Long>): List<String> =
        positionsUs
            .distinct()
            .mapNotNull { positionUs -> thumbnailUri(entry = entry, positionUs = positionUs) }
            .distinct()

    private suspend fun thumbnailUri(entry: FreeboxFileEntry, positionUs: Long): String? = withContext(Dispatchers.IO) {
        val cacheKey = "${freeboxContentIdForEntry(entry)}@$positionUs".sha256()
        val cacheDirectory = File(context.cacheDir, "freebox-video-thumbnails").apply { mkdirs() }
        val thumbnailFile = File(cacheDirectory, "$cacheKey.jpg")
        if (thumbnailFile.length() > 0L) return@withContext Uri.fromFile(thumbnailFile).toString()

        val lock = extractionLocks.getOrPut(cacheKey) { Mutex() }
        lock.withLock {
            if (thumbnailFile.length() > 0L) return@withLock Uri.fromFile(thumbnailFile).toString()

            var retriever: MediaMetadataRetriever? = null
            var frame: Bitmap? = null
            var scaledFrame: Bitmap? = null
            try {
                val settings = freeboxSettingsDataStore.settings.first()
                if (!settings.hasSavedConnection) return@withLock null
                val sessionToken = settings.sessionToken.ifBlank {
                    freeboxOsClient.openSession(settings).getOrThrow().sessionToken
                }
                val settingsWithToken = settings.copy(sessionToken = sessionToken)
                retriever = MediaMetadataRetriever()
                retriever.setDataSource(
                    freeboxOsClient.downloadUrl(settingsWithToken, entry.path, entry.encodedPath),
                    freeboxOsClient.sessionHeaders(sessionToken)
                )
                frame = retriever.getFrameAtTime(positionUs.coerceAtLeast(0L), MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    ?: retriever.frameAtTime
                    ?: return@withLock null
                scaledFrame = frame.scaleDown(maxWidth = 720)

                val temporaryFile = File(cacheDirectory, "$cacheKey.tmp")
                temporaryFile.outputStream().use { output ->
                    if (!scaledFrame.compress(Bitmap.CompressFormat.JPEG, 86, output)) {
                        return@withLock null
                    }
                }
                if (!temporaryFile.renameTo(thumbnailFile)) {
                    temporaryFile.copyTo(thumbnailFile, overwrite = true)
                    temporaryFile.delete()
                }
                Uri.fromFile(thumbnailFile).toString()
            } catch (error: Exception) {
                Log.w(TAG, "Impossible d'extraire une vignette Freebox: ${entry.path}", error)
                null
            } finally {
                if (scaledFrame !== frame) scaledFrame?.recycle()
                frame?.recycle()
                runCatching { retriever?.release() }
                extractionLocks.remove(cacheKey, lock)
            }
        }
    }

    private fun Bitmap.scaleDown(maxWidth: Int): Bitmap {
        if (width <= maxWidth) return this
        val targetHeight = (height * (maxWidth.toFloat() / width)).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(this, maxWidth, targetHeight, true)
    }

    private fun String.sha256(): String = MessageDigest.getInstance("SHA-256")
        .digest(toByteArray())
        .joinToString("") { byte -> "%02x".format(byte) }

    private companion object {
        const val TAG = "FreeboxFrameThumbnail"
    }
}
