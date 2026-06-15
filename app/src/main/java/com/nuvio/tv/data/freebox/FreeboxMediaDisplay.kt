package com.nuvio.tv.data.freebox

import java.util.Locale
import java.util.concurrent.TimeUnit

private val VIDEO_FILE_EXTENSION_REGEX = Regex("\\.(3g2|3gp|avi|flv|m2ts|m4v|mkv|mov|mp4|mpeg|mpg|mts|ts|webm|wmv)$", RegexOption.IGNORE_CASE)
private val DISPLAY_FILE_EXTENSION_REGEX = Regex("\\.[A-Za-z0-9]{1,8}$")
private val RELEASE_QUALITY_REGEX = Regex("\\b(2160p|1080p|720p|480p|4k|uhd|hdr10?|dv|dolby\\s*vision|bluray|blu[- ]?ray|bdrip|webrip|web[- ]?dl|hdtv|x264|x265|h264|h265|hevc|aac|dts|truehd|atmos|multi|vostfr|vf|french|eng|extended|remux|proper|repack)\\b", RegexOption.IGNORE_CASE)
private val SEASON_EPISODE_REGEX = Regex("\\b[sS]\\d{1,2}[eE]\\d{1,2}\\b")
private val YEAR_REGEX = Regex("\\b(19\\d{2}|20\\d{2})\\b")
private val DURATION_PREFIX_REGEX = Regex("^(?:\\d+h\\s*\\d{1,2}m?|\\d+h|\\d+min)\\s+", RegexOption.IGNORE_CASE)

fun freeboxContentIdForPath(path: String): String = "freebox:${path.trim()}"

fun freeboxFileNameOnly(rawNameOrPath: String): String {
    return rawNameOrPath
        .replace('\\', '/')
        .substringAfterLast('/')
        .substringAfterLast(':')
        .trim()
        .ifBlank { rawNameOrPath.trim() }
}


fun freeboxDisplayName(rawNameOrPath: String, showExtensions: Boolean = false): String {
    val fileName = freeboxFileNameOnly(rawNameOrPath)
        .replace(DURATION_PREFIX_REGEX, "")
        .trim()
    return if (showExtensions) {
        fileName.ifBlank { rawNameOrPath.trim() }
    } else {
        fileName
            .replace(DISPLAY_FILE_EXTENSION_REGEX, "")
            .trim()
            .ifBlank { fileName }
    }
}
fun formatFreeboxDurationCompact(durationMs: Long?): String? {
    val safeMs = durationMs?.takeIf { it > 0L } ?: return null
    val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(safeMs).coerceAtLeast(1L)
    val hours = totalMinutes / 60L
    val minutes = totalMinutes % 60L
    return if (hours > 0L) {
        if (minutes > 0L) "${hours}h${minutes.toString().padStart(2, '0')}" else "${hours}h00m"
    } else {
        "0h${minutes.toString().padStart(2, '0')}m"
    }
}

fun freeboxVideoDisplayTitle(rawNameOrPath: String, durationMs: Long? = null, showExtensions: Boolean = false): String {
    val fileName = freeboxDisplayName(rawNameOrPath, showExtensions)
    val duration = formatFreeboxDurationCompact(durationMs)
    return if (duration.isNullOrBlank()) fileName else "$duration $fileName"
}

fun freeboxTmdbSearchQuery(rawNameOrPath: String): String {
    val fileName = freeboxDisplayName(rawNameOrPath)
    val withoutExtension = fileName.replace(VIDEO_FILE_EXTENSION_REGEX, "")
    val cleaned = withoutExtension
        .replace('.', ' ')
        .replace('_', ' ')
        .replace('-', ' ')
        .replace(SEASON_EPISODE_REGEX, " ")
        .replace(YEAR_REGEX, " ")
        .replace(RELEASE_QUALITY_REGEX, " ")
        .replace(Regex("\\[[^]]*]"), " ")
        .replace(Regex("\\([^)]*\\)"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()
        .replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() }
        .ifBlank { withoutExtension.trim() }
    // Retire un numero de segment isole en fin de titre (ex: "Envoye Special 3" -> "Envoye Special")
    val withoutTrailingNumber = cleaned.replace(Regex("\\s+\\d{1,3}$"), "").trim()
    return withoutTrailingNumber.ifBlank { cleaned }
}
