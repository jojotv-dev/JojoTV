package com.nuvio.tv.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.nuvio.tv.R
import com.nuvio.tv.domain.model.MetaCastMember
import com.nuvio.tv.ui.util.contentTextDirection
import com.nuvio.tv.ui.util.localizeEpisodeTitle
import java.util.Locale

@Composable
internal fun PlayerMetadataSummary(
    title: String,
    logo: String?,
    releaseYear: String?,
    contentType: String?,
    genres: List<String>,
    runtime: String?,
    durationMs: Long,
    isLive: Boolean,
    currentSeason: Int?,
    currentEpisode: Int?,
    episodeTitle: String?,
    description: String?,
    cast: List<MetaCastMember>,
    director: List<String> = emptyList(),
    modifier: Modifier = Modifier,
    onCastSelected: ((MetaCastMember) -> Unit)? = null
) {
    val context = LocalContext.current
    val normalizedTitle = title.replace(Regex("\\s*\\((?:19|20)\\d{2}\\)\\s*$"), "").trim()
    val visibleCast = cast
        .filter { it.name.isNotBlank() }
        .distinctBy { it.tmdbId?.toString() ?: it.name.trim().lowercase(Locale.ROOT) }
        .take(6)
    val durationLabel = if (isLive || contentType.equals("iptv", ignoreCase = true)) {
        null
    } else {
        playerMetadataRuntimeLabel(durationMs, runtime)
    }
    val metadataLine = buildList {
        genres.asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .take(3)
            .joinToString(" · ")
            .takeIf { it.isNotBlank() }
            ?.let(::add)
        releaseYear?.trim()?.takeIf { it.isNotBlank() }?.let(::add)
        durationLabel?.let(::add)
        if (currentSeason != null && currentEpisode != null) {
            add("S%02dE%02d".format(Locale.ROOT, currentSeason, currentEpisode))
        }
    }.joinToString("  •  ")

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        var logoFailed by remember(logo) { mutableStateOf(false) }
        if (!logo.isNullOrBlank() && !logoFailed) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(logo)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                contentScale = ContentScale.Fit,
                alignment = Alignment.CenterStart,
                modifier = Modifier.width(260.dp).height(62.dp),
                onError = { logoFailed = true }
            )
        } else {
            Text(
                text = normalizedTitle.ifBlank { title },
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        episodeTitle?.trim()?.takeIf { it.isNotBlank() && !it.equals(title, ignoreCase = true) }?.let {
            Text(
                text = it.localizeEpisodeTitle(context),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.92f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (metadataLine.isNotBlank()) {
            Text(
                text = metadataLine,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        director.map(String::trim).filter(String::isNotBlank).distinct().take(2)
            .takeIf { it.isNotEmpty() }?.joinToString(", ")?.let { names ->
                Text(
                    text = stringResource(R.string.hero_director, names),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.82f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

        description?.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium.copy(textDirection = it.contentTextDirection()),
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.92f),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (visibleCast.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.pause_cast_label),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                visibleCast.forEach { member ->
                    PlayerMetadataCastMember(
                        member = member,
                        onClick = onCastSelected?.let { callback -> { callback(member) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerMetadataCastMember(
    member: MetaCastMember,
    onClick: (() -> Unit)?
) {
    val context = LocalContext.current
    val clickableModifier = if (onClick == null) {
        Modifier
    } else {
        Modifier.clickable(role = Role.Button, onClick = onClick)
    }

    Column(
        modifier = clickableModifier.width(92.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val photo = member.photo?.takeIf { it.isNotBlank() }
        if (photo != null) {
            AsyncImage(
                model = ImageRequest.Builder(context).data(photo).crossfade(true).build(),
                contentDescription = member.name,
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.size(68.dp).clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = member.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.height(34.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        member.character?.takeIf { it.isNotBlank() }?.let { character ->
            Text(
                text = character,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.72f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun playerMetadataRuntimeLabel(durationMs: Long, runtime: String?): String? {
    val rawRuntime = runtime?.trim()?.lowercase(Locale.ROOT).orEmpty()
    val hours = Regex("(\\d+)\\s*h").find(rawRuntime)?.groupValues?.getOrNull(1)?.toIntOrNull()
    val minutes = Regex("(\\d+)\\s*m(?:in)?").find(rawRuntime)?.groupValues?.getOrNull(1)?.toIntOrNull()
        ?: if (hours != null) rawRuntime.substringAfter('h').filter(Char::isDigit).toIntOrNull() else null
    val runtimeMinutes = when {
        durationMs > 0L -> (durationMs / 60_000L).takeIf { it in 1L..Int.MAX_VALUE.toLong() }?.toInt()
        hours != null || minutes != null -> (hours ?: 0) * 60 + (minutes ?: 0)
        else -> rawRuntime.filter(Char::isDigit).toIntOrNull()
    }?.takeIf { it > 0 } ?: return runtime?.trim()?.takeIf { it.isNotBlank() }

    val wholeHours = runtimeMinutes / 60
    val remainingMinutes = runtimeMinutes % 60
    return when {
        wholeHours > 0 && remainingMinutes > 0 -> "${wholeHours}h${remainingMinutes.toString().padStart(2, '0')}"
        wholeHours > 0 -> "${wholeHours}h"
        else -> "$runtimeMinutes min"
    }
}
