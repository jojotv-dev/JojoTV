package com.nuvio.tv.ui.screens.home

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.nuvio.tv.R
import com.nuvio.tv.data.freebox.FREEBOX_FOLDER_ID_PREFIX
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.SeriesLifecycleStatus
import com.nuvio.tv.domain.model.normalizeTmdbSeriesStatus
import com.nuvio.tv.reshaped.livetv.normalizeLiveTvVodTitleForTmdb
import com.nuvio.tv.ui.theme.NuvioTheme
import com.nuvio.tv.ui.util.formatHeroRuntime
import com.nuvio.tv.ui.util.localizedGenreLabel
import java.util.Locale

private val TRAILING_HOME_TITLE_YEAR = Regex(
    """\s*(?:\((?:19|20)\d{2}\)|\[(?:19|20)\d{2}\]|(?:19|20)\d{2})\s*$"""
)

@Composable
internal fun HomeRowDetailsPanel(
    item: MetaPreview,
    showSeriesInfo: Boolean? = null,
    showSeriesStatus: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isFolder = item.id.startsWith(FREEBOX_FOLDER_ID_PREFIX)
    val normalizedTitle = normalizeLiveTvVodTitleForTmdb(item.name)
    val isMovieOrSeries = item.type == ContentType.MOVIE || item.type == ContentType.SERIES ||
        item.apiType.equals("movie", ignoreCase = true) ||
        item.apiType.equals("series", ignoreCase = true)
    val title = if (isMovieOrSeries && !isFolder) {
        TRAILING_HOME_TITLE_YEAR.replaceFirst(normalizedTitle, "").trimEnd()
    } else {
        normalizedTitle
    }
    val genres = item.genres
        .asSequence()
        .map { it.trim() }
        .filter(String::isNotBlank)
        .distinct()
        .take(3)
        .map { localizedGenreLabel(context, it) }
        .toList()
    val year = extractYearOrRange(item.releaseInfo)
        ?: extractYear(item.released)
    val runtime = formatHeroRuntime(item.runtime)
    val durationText = when {
        runtime.isNullOrBlank() -> null
        isFolder -> context.getString(R.string.home_average_duration, runtime)
        else -> runtime
    }
    val metadata = buildList {
        addAll(genres)
        year?.takeIf(String::isNotBlank)?.let(::add)
        durationText?.let(::add)
    }.joinToString("  •  ")
    val isSeries = showSeriesInfo ?: (
        item.type == ContentType.SERIES || item.type == ContentType.TV ||
            item.apiType.equals("series", ignoreCase = true) || item.apiType.equals("tv", ignoreCase = true) ||
            (item.seasonCount ?: 0) > 0
        )
    val hasTmdbSeriesDetails = isSeries && item.hasTmdbSeriesDetails
    val seasonCount = item.seasonCount?.takeIf { hasTmdbSeriesDetails && it > 0 }
    val seriesStatus = if (hasTmdbSeriesDetails && showSeriesStatus) {
        seriesStatusLabel(context, item.status)
    } else null
    val synopsis = item.description?.trim()?.takeIf { description ->
        description.isNotBlank() && !description.startsWith("Freebox ·", ignoreCase = true)
    }
    val castNames = item.castMembers.asSequence()
        .map { it.name.trim() }
        .filter(String::isNotBlank)
        .distinct()
        .take(4)
        .joinToString(", ")
    val directors = item.director.asSequence()
        .map(String::trim)
        .filter(String::isNotBlank)
        .distinct()
        .take(2)
        .joinToString(", ")
    val credits = buildList {
        castNames.takeIf(String::isNotBlank)?.let { names ->
            add("${context.getString(R.string.pause_cast_label)}: $names")
        }
        directors.takeIf(String::isNotBlank)?.let { names ->
            add(context.getString(R.string.hero_director, names))
        }
    }
    var logoFailed by remember(item.id, item.logo) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 52.dp, end = 48.dp, top = 10.dp, bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (!item.logo.isNullOrBlank() && !logoFailed) {
            AsyncImage(
                model = ImageRequest.Builder(context).data(item.logo).crossfade(true).build(),
                contentDescription = title,
                contentScale = ContentScale.Fit,
                alignment = Alignment.CenterStart,
                modifier = Modifier.height(48.dp).fillMaxWidth(0.55f),
                onError = { logoFailed = true }
            )
        } else {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = NuvioTheme.colors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (metadata.isNotBlank() || seasonCount != null || seriesStatus != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (metadata.isNotBlank()) {
                    Text(
                        text = metadata,
                        modifier = Modifier.weight(1f, fill = false),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = NuvioTheme.colors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (seasonCount != null || seriesStatus != null) {
                    Row(
                        modifier = Modifier.border(
                            width = 1.dp,
                            color = NuvioTheme.colors.TextSecondary.copy(alpha = 0.65f),
                            shape = NuvioTheme.configuredShape(RoundedCornerShape(4.dp))
                        ).padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        seasonCount?.let { count ->
                            Text(
                                text = pluralStringResource(R.plurals.home_season_count, count, count)
                                    .uppercase(Locale.getDefault()),
                                style = MaterialTheme.typography.labelSmall,
                                color = NuvioTheme.colors.TextPrimary,
                                maxLines = 1
                            )
                        }
                        if (seasonCount != null && seriesStatus != null) {
                            Text(
                                text = "|",
                                style = MaterialTheme.typography.labelSmall,
                                color = NuvioTheme.colors.TextSecondary
                            )
                        }
                        seriesStatus?.let { status ->
                            Text(
                                text = status.uppercase(Locale.getDefault()),
                                style = MaterialTheme.typography.labelSmall,
                                color = NuvioTheme.colors.TextPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        credits.forEach { credit ->
            Text(
                text = credit,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = NuvioTheme.colors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        synopsis?.let { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 18.sp),
                color = NuvioTheme.colors.TextPrimary.copy(alpha = 0.92f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun seriesStatusLabel(context: Context, status: String?): String? {
    return when (normalizeTmdbSeriesStatus(status)) {
        SeriesLifecycleStatus.ENDED -> context.getString(R.string.series_status_ended)
        SeriesLifecycleStatus.IN_PROGRESS -> context.getString(R.string.series_status_continuing)
        null -> null
    }
}

