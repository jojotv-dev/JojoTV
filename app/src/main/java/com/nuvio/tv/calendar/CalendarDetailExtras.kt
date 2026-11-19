package com.nuvio.tv.calendar

import android.text.format.DateFormat
import com.nuvio.tv.R
import com.nuvio.tv.ui.theme.NuvioTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuvio.tv.domain.model.Meta
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/** The bell on a show's or movie's page: on = it's on your Calendar, with new episodes on Home. */
data class CalendarBell(val on: Boolean, val toggle: () -> Unit)

private fun repoOf(context: android.content.Context): CalendarRepository? = runCatching {
    EntryPointAccessors.fromApplication(context.applicationContext, CalendarEntryPoint::class.java).calendarRepository()
}.getOrNull()

private fun Meta.isSeriesMeta() = CalendarRepository.isSeries(apiType)
private fun Meta.isMovieMeta() = apiType.equals("movie", ignoreCase = true)
private fun Meta.imdbOrNull() = imdbId?.takeIf { it.startsWith("tt") } ?: id.takeIf { it.startsWith("tt") }

@Composable
fun rememberCalendarBell(meta: Meta): CalendarBell? = when {
    meta.isSeriesMeta() -> rememberShowBell(meta.id, meta.imdbOrNull(), meta.name, meta.poster, meta.background)
    meta.isMovieMeta() -> rememberMovieBell(meta)
    else -> null
}

/**
 * The bell on a movie's page: only while it isn't out to watch at home yet (in theaters, or not
 * released). When it comes out at home, a card in Continue Watching says so.
 */
@Composable
private fun rememberMovieBell(meta: Meta): CalendarBell? {
    val context = LocalContext.current
    val repo = remember { repoOf(context) } ?: return null
    val follows by repo.follows.collectAsState()
    val settings by repo.settings.collectAsState()
    if (settings.calendarOff) return null
    val imdb = meta.imdbOrNull()
    val existing = follows.firstOrNull { it.type.equals("movie", ignoreCase = true) && (it.id == meta.id || (imdb != null && (it.imdbId == imdb || it.id == imdb))) }
    var notOut by remember(meta.id) { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(meta.id) {
        val today = LocalDate.now()
        val year = CalendarRepository.yearOf(meta.releaseInfo) ?: CalendarRepository.yearOf(meta.released)
        val d = repo.movieDates(meta.id, imdb)
        notOut = when {
            d == null -> year != null && year >= today.year
            d.digital != null -> d.digital.isAfter(today)
            // In theaters (or coming), with no date yet to watch at home.
            d.theater != null -> !d.theater.isBefore(today.minusDays(240))
            else -> year != null && year >= today.year
        }
    }
    // Already out at home: nothing to wait for (unless its bell was rung before: it can be turned off).
    if (existing == null && notOut != true) return null
    val scope = rememberCoroutineScope()
    return CalendarBell(on = existing != null, toggle = {
        val follow = existing ?: CalendarFollow(
            id = imdb ?: meta.id,
            type = "movie",
            name = meta.name,
            poster = meta.poster,
            background = meta.background,
            imdbId = imdb
        )
        scope.launch {
            val on = repo.toggleFollow(follow)
            CalendarNotice.show(
                if (on) context.getString(R.string.calendar_notice_movie_on, meta.name)
                else context.getString(R.string.calendar_notice_bell_off, meta.name)
            )
        }
    })
}

/**
 * The bell for a TV show (its page, a search result's hold-OK menu): on = followed, its new
 * episodes on the Calendar and on Home. Null when the Calendar is switched off.
 */
@Composable
fun rememberShowBell(id: String, imdbId: String?, name: String, poster: String?, background: String?): CalendarBell? {
    val context = LocalContext.current
    val repo = remember { repoOf(context) } ?: return null
    val follows by repo.follows.collectAsState()
    val settings by repo.settings.collectAsState()
    if (settings.calendarOff) return null
    val imdb = imdbId?.takeIf { it.startsWith("tt") } ?: id.takeIf { it.startsWith("tt") }
    val existing = follows.firstOrNull { it.id == id || (imdb != null && (it.imdbId == imdb || it.id == imdb)) }
    val scope = rememberCoroutineScope()
    return CalendarBell(on = existing != null, toggle = {
        val follow = existing ?: CalendarFollow(id = imdb ?: id, type = "series", name = name, poster = poster, background = background, imdbId = imdb)
        scope.launch {
            val on = repo.toggleFollow(follow)
            CalendarNotice.show(
                if (on) context.getString(R.string.calendar_notice_series_on, name)
                else context.getString(R.string.calendar_notice_bell_off, name)
            )
        }
    })
}

/**
 * Under a show's buttons: when its next episode airs ("Next episode: S2 · E5 · Fri 9:00 PM · FX").
 * Movies: in theaters and at home dates.
 */
@Composable
fun CalendarNextLine(
    meta: Meta,
    @Suppress("UNUSED_PARAMETER") onFocused: () -> Unit = {},
    fallbackText: String? = null,
) {
    val context = LocalContext.current
    val entryPoint = remember {
        runCatching { EntryPointAccessors.fromApplication(context.applicationContext, CalendarEntryPoint::class.java) }.getOrNull()
    }
    if (entryPoint == null) {
        CalendarNextLineContent(fallbackText)
        return
    }
    val repo = remember { entryPoint.calendarRepository() }
    val settings by repo.settings.collectAsState()
    if (settings.calendarOff) {
        CalendarNextLineContent(fallbackText)
        return
    }
    val series = meta.isSeriesMeta()
    if (!series && !meta.isMovieMeta()) {
        CalendarNextLineContent(fallbackText)
        return
    }
    var line by remember(meta.id) { mutableStateOf<String?>(null) }
    LaunchedEffect(meta.id) {
        val imdb = meta.imdbOrNull()
        if (series) {
            val info = repo.showInfo(meta.id, imdb, meta.apiType) ?: return@LaunchedEffect
            val next = info.next
            line = next?.let { describe(it, context) } ?: info.status
        } else {
            val d = repo.movieDates(meta.id, imdb) ?: return@LaunchedEffect
            val today = LocalDate.now()
            @Suppress("DEPRECATION")
            val locale = context.resources.configuration.locale ?: Locale.getDefault()
            fun fmt(x: LocalDate): String {
                val skeleton = if (x.year == today.year) "dMMM" else "dMMMy"
                val pattern = DateFormat.getBestDateTimePattern(locale, skeleton)
                return x.format(DateTimeFormatter.ofPattern(pattern, locale))
            }
            val parts = buildList {
                d.theater?.let { date ->
                    val label = if (date.isAfter(today)) {
                        com.nuvio.tv.R.string.detail_release_theater_future
                    } else {
                        com.nuvio.tv.R.string.detail_release_theater_past
                    }
                    add(context.getString(label, fmt(date)))
                }
                when {
                    d.digital != null -> {
                        val label = if (d.digital.isAfter(today)) {
                            com.nuvio.tv.R.string.detail_release_home_future
                        } else {
                            com.nuvio.tv.R.string.detail_release_home_past
                        }
                        add(context.getString(label, fmt(d.digital)))
                    }
                    d.disc != null -> {
                        val label = if (d.disc.isAfter(today)) {
                            com.nuvio.tv.R.string.detail_release_disc_future
                        } else {
                            com.nuvio.tv.R.string.detail_release_disc_past
                        }
                        add(context.getString(label, fmt(d.disc)))
                    }
                    d.theater != null && !d.theater.isBefore(today.minusDays(120)) ->
                        add(context.getString(com.nuvio.tv.R.string.detail_release_home_unknown))
                }
            }
            // Only worth a line while it's new.
            val recent = listOfNotNull(d.theater, d.digital, d.disc).any { !it.isBefore(today.minusDays(45)) }
            line = if (recent && parts.isNotEmpty()) parts.joinToString(" · ") else null
        }
    }
    CalendarNextLineContent(line ?: fallbackText)
}

@Composable
private fun CalendarNextLineContent(text: String?) {
    val content = text?.takeIf(String::isNotBlank) ?: return
    // On a dark backing of its own: it sits over the show's picture or trailer, which can be anything.
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 10.dp)
            .clip(NuvioTheme.configuredShape(RoundedCornerShape(50)))
            .background(Color.Black.copy(alpha = 0.62f))
            .padding(start = 12.dp, end = 14.dp, top = 5.dp, bottom = 5.dp)
    ) {
        androidx.tv.material3.Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        androidx.tv.material3.Text(content, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

private fun describe(e: CalendarEntry, context: android.content.Context): String {
    val today = LocalDate.now()
    @Suppress("DEPRECATION")
    val locale = context.resources.configuration.locale ?: Locale.getDefault()
    val day = when (ChronoUnit.DAYS.between(today, e.day)) {
        0L -> context.getString(R.string.calendar_today)
        1L -> context.getString(R.string.calendar_tomorrow)
        in 2L..6L -> e.day.format(DateTimeFormatter.ofPattern("EEEE", locale))
        else -> e.day.format(DateTimeFormatter.ofPattern(if (e.day.year == today.year) "EEE MMM d" else "EEE MMM d, yyyy", locale))
    }
    val time = e.airMs?.takeIf { !e.streaming }?.let {
        DateTimeFormatter.ofPattern("h:mm a", locale).format(Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()))
    }
    val lead = when {
        CalendarTag.SERIES_PREMIERE in e.tags -> context.getString(R.string.calendar_series_premiere)
        CalendarTag.SEASON_PREMIERE in e.tags -> context.getString(R.string.calendar_season_premiere, e.season ?: 1)
        CalendarTag.SEASON_FINALE in e.tags || CalendarTag.SERIES_FINALE in e.tags -> context.getString(R.string.calendar_episode_finale)
        else -> context.getString(R.string.calendar_episode_next)
    }
    return buildString {
        append(lead).append(": ")
        if (CalendarTag.SEASON_PREMIERE !in e.tags) e.episodeCode?.let { append(it).append(" · ") }
        append(day)
        time?.let { append(", ").append(it) }
        e.network?.let { append(" · ").append(it) }
    }
}
