package com.nuvio.tv.data.freebox

import android.net.Uri
import com.nuvio.tv.domain.repository.WatchProgressRepository
import kotlinx.coroutines.flow.first
import java.util.Locale

/**
 * A Freebox path identifies where a file lives, but the file may be replaced at that path.
 * Include SMB's file fingerprint in the playback id so a fresh recording starts with fresh
 * progress while older progress entries can still resolve to the same path for cleanup.
 */
internal fun freeboxContentIdFor(
    path: String,
    sizeBytes: Long? = null,
    lastModifiedMs: Long? = null,
    fileId: Long? = null,
): String {
    val normalizedPath = path.replace('\\', '/').trim('/')
    val size = sizeBytes?.takeIf { it > 0L }
    val modified = lastModifiedMs?.takeIf { it > 0L }
    val smbFileId = fileId?.takeIf { it != 0L }
    if (size == null && modified == null && smbFileId == null) return "freebox:$normalizedPath"

    return Uri.Builder()
        .scheme("freebox")
        .authority("media")
        .appendQueryParameter("path", normalizedPath)
        .apply {
            if (smbFileId != null) {
                // SMB file ids stay fixed while an active recording grows or its timestamp moves.
                appendQueryParameter("fileId", smbFileId.toString())
            } else {
                size?.let { appendQueryParameter("size", it.toString()) }
                modified?.let { appendQueryParameter("modified", it.toString()) }
            }
        }
        .build()
        .toString()
}

/** Extracts the remote path from current versioned ids and legacy `freebox:<path>` ids. */
internal fun freeboxPathFromContentId(value: String, addonBaseUrl: String? = null): String? {
    val path = when {
        value.startsWith("freebox://media", ignoreCase = true) ->
            runCatching { Uri.parse(value).getQueryParameter("path") }.getOrNull()
        value.startsWith("freebox:", ignoreCase = true) -> value.substringAfter(':')
        addonBaseUrl.equals("freebox://nas", ignoreCase = true) -> value
        else -> null
    } ?: return null

    val parts = path.replace('\\', '/').split('/').filter(String::isNotBlank)
    if (parts.isEmpty() || parts.any { it == "." || it == ".." }) return null
    return parts.joinToString("/")
}

/** Removes old resume entries when a different SMB file appears at the same Freebox path. */
internal suspend fun WatchProgressRepository.clearReplacedFreeboxProgress(
    currentContentIdsByPath: Map<String, String>,
) {
    if (currentContentIdsByPath.isEmpty()) return
    val current = currentContentIdsByPath.entries.associate { (path, contentId) ->
        path.replace('\\', '/').trim('/').lowercase(Locale.ROOT) to contentId
    }
    val replaced = allProgress.first().filter { progress ->
        val newContentId = sequenceOf(progress.contentId, progress.videoId)
            .filter { it.startsWith("freebox:", ignoreCase = true) }
            .mapNotNull { freeboxPathFromContentId(it, progress.addonBaseUrl) }
            .firstNotNullOfOrNull { current[it.lowercase(Locale.ROOT)] }
            ?: return@filter false
        progress.contentId != newContentId
    }
    replaced.forEach { progress ->
        removeProgress(progress.contentId, progress.season, progress.episode)
        removeFromHistory(
            contentId = progress.contentId,
            videoId = progress.videoId,
            season = progress.season,
            episode = progress.episode,
        )
    }
}
