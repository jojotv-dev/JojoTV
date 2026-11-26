package com.nuvio.tv.calendar

import android.content.Context
import com.nuvio.tv.R
import com.nuvio.tv.core.profile.ProfileManager
import com.nuvio.tv.domain.model.LibraryEntry
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.domain.model.WatchedItem
import com.nuvio.tv.domain.repository.LibraryRepository
import com.nuvio.tv.domain.repository.WatchProgressRepository
import com.nuvio.tv.reshaped.livetv.LiveTvSourceType
import com.nuvio.tv.reshaped.livetv.LiveTvStorage
import com.nuvio.tv.reshaped.livetv.LiveTvVodCache
import com.nuvio.tv.reshaped.livetv.LiveTvVodItem
import com.nuvio.tv.reshaped.livetv.normalizeLiveTvVodTitleForTmdb
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The Calendar: when the next episodes of your shows come out (followed shows, your Library,
 * watch history and IPTV VOD favorites), and when movies on your lists come out to watch at home.
 * Also feeds Home (new-episode cards and the Upcoming row) and the show pages.
 */
@Singleton
class CalendarRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: CalendarPreferences,
    private val library: LibraryRepository,
    private val progress: WatchProgressRepository,
    private val profileManager: ProfileManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val sources = CalendarSources(context)
    private val mutex = Mutex()

    val follows: StateFlow<List<CalendarFollow>> = prefs.calendarFollows
        .map { raw -> CalendarFollow.decode(raw) }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    val hiddenShows: StateFlow<List<CalendarHidden>> = prefs.calendarHiddenShows
        .map { CalendarHidden.decode(it) }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    val settings: StateFlow<CalendarSettings> = prefs.settings
        .stateIn(scope, SharingStarted.Eagerly, CalendarSettings())

    private val _state = MutableStateFlow(CalendarState())
    val state: StateFlow<CalendarState> = _state.asStateFlow()

    /**
     * Followed, watched, library and IPTV VOD favorite shows can have episodes on the Calendar: the Calendar
     * shows up in the side menu by itself.
     */
    val hasShows: StateFlow<Boolean> = _state.map { st -> st.entries.any { it.kind == CalendarKind.EPISODE } }
        .distinctUntilChanged()
        .stateIn(scope, SharingStarted.Eagerly, false)

    private val _airing = MutableStateFlow<AiringToday>(AiringToday())
    val airing: StateFlow<AiringToday> = _airing.asStateFlow()

    @Volatile private var started = false
    private var refreshJob: Job? = null

    /** Started with the app: the first look happens once the app has settled. */
    @OptIn(FlowPreview::class)
    fun start() {
        if (started) return
        started = true
        scope.launch {
            // What's kept on the TV first (quick), then a fresh look.
            delay(8_000)
            build(fetch = false)
            delay(20_000)
            refresh(force = false)
            // Then every few hours while the app is open.
            while (true) {
                delay(3 * CalendarSources.HOUR)
                refresh(force = false)
            }
        }
        // A bell rung or a show hidden or shown again: update straight away.
        scope.launch {
            follows.drop(1).debounce(600).collect { refresh(force = false) }
        }
        scope.launch {
            hiddenShows.drop(1).debounce(300).collect { build(fetch = false) }
        }
        scope.launch {
            settings.map {
                listOf(
                    it.calendarHideSpoilers,
                    it.calendarCountry,
                    it.calendarUpcomingRow,
                    it.calendarNewEpisodeCards,
                    it.calendarOff
                )
            }.distinctUntilChanged().drop(1).collect {
                build(fetch = false)
                refresh(force = false)
            }
        }
        // IPTV favorites are stored per profile; update when Live TV changes them or the active
        // profile changes. Reading them below uses the saved VOD cache and does not load playlists.
        scope.launch {
            combine(
                profileManager.activeProfileId,
                com.nuvio.tv.reshaped.livetv.LiveTvRepository.uiState.map { it.favoriteVodKeys }
            ) { profileId, favoriteKeys -> profileId to favoriteKeys }
                .distinctUntilChanged()
                .drop(1)
                .debounce(500)
                .collect { refresh(force = false) }
        }
    }

    /** The Calendar is switched off: no look-ups, nothing on Home. */
    val isOff: Boolean get() = settings.value.calendarOff

    /** Looks your shows up again ([force]: even ones looked up recently). */
    fun refresh(force: Boolean) {
        if (isOff) return
        if (refreshJob?.isActive == true) return
        refreshJob = scope.launch { build(fetch = true, force = force) }
    }

    // ------------------------------------------------------------------ bells and hiding

    fun isFollowed(id: String): Boolean = follows.value.any { it.id == id || (it.imdbId != null && it.imdbId == id) }

    /** Rings or un-rings the bell on a show or movie. True when it's now rung. */
    suspend fun toggleFollow(follow: CalendarFollow): Boolean {
        var nowOn = false
        prefs.editCalendarFollows { raw ->
            val list = CalendarFollow.decode(raw)
            val there = list.any { it.id == follow.id }
            nowOn = !there
            CalendarFollow.encode(if (there) list.filter { it.id != follow.id } else list + follow)
        }
        // A show you rang the bell on is no longer hidden.
        if (nowOn) unhide(follow.id)
        return nowOn
    }

    suspend fun hide(id: String, name: String) {
        prefs.editCalendarHiddenShows { raw ->
            val list = CalendarHidden.decode(raw).filter { it.id != id }
            CalendarHidden.encode(list + CalendarHidden(id, name))
        }
    }

    suspend fun unhide(id: String) {
        prefs.editCalendarHiddenShows { raw -> CalendarHidden.encode(CalendarHidden.decode(raw).filter { it.id != id }) }
    }

    // ------------------------------------------------------------------ building the Calendar

    /** A show or movie on the Calendar, and why. */
    private data class Candidate(
        val id: String,
        val type: String,
        val name: String,
        val poster: String?,
        val background: String?,
        val imdb: String?,
        val tmdb: Int?,
        val addonBaseUrl: String?,
        val reasons: MutableSet<CalendarReason>,
        val year: Int?,
        /** When you last watched it (for which shows come first). */
        val lastWatched: Long = 0L
    )

    private fun dropped(id: String): Boolean = runCatching { progress.isDroppedShow(id) }.getOrDefault(false)

    /** Episodes you've seen of each show: the furthest one, and every one marked watched. */
    private data class Seen(val furthest: Pair<Int, Int>?, val watched: Set<Pair<Int, Int>>)

    private suspend fun candidates(): List<Candidate> {
        val out = LinkedHashMap<String, Candidate>()
        fun keyOf(id: String, imdb: String?) = imdb?.takeIf { it.startsWith("tt") } ?: id
        fun add(c: Candidate) {
            val k = keyOf(c.id, c.imdb)
            val old = out[k]
            if (old == null) out[k] = c
            else {
                old.reasons += c.reasons
                if (old.poster == null && c.poster != null) out[k] = old.copy(poster = c.poster, reasons = old.reasons)
            }
        }
        follows.value.forEach { f ->
            add(Candidate(f.id, f.type, f.name, f.poster, f.background, f.imdbId ?: f.id.takeIf { it.startsWith("tt") }, tmdbOf(f.id),
                f.addonBaseUrl, mutableSetOf(CalendarReason.BELL), null))
        }
        val lib: List<LibraryEntry> = withTimeoutOrNull(20_000) { library.libraryItems.first() }.orEmpty()
        lib.sortedByDescending { it.listedAt }.forEach { e ->
            val type = if (e.type.equals("movie", true)) "movie" else if (isSeries(e.type)) "series" else return@forEach
            add(Candidate(e.id, type, e.name, e.poster, e.background, e.imdbId ?: e.id.takeIf { it.startsWith("tt") },
                e.tmdbId ?: tmdbOf(e.id), e.addonBaseUrl, mutableSetOf(CalendarReason.LIBRARY), yearOf(e.releaseInfo)))
        }
        // Every show in your watch history, however long ago (including Trakt history):
        // the ones between seasons are the ones you most want to hear about. Shows you've
        // dropped are left out; ended shows simply have nothing coming.
        val prog: List<WatchProgress> = withTimeoutOrNull(20_000) { progress.allProgress.first() }.orEmpty()
        prog.filter { isSeries(it.contentType) && !dropped(it.contentId) }
            .groupBy { it.contentId }
            .forEach { (id, items) ->
                val p = items.maxBy { it.lastWatched }
                add(Candidate(id, "series", p.name, p.poster, p.backdrop, id.takeIf { it.startsWith("tt") }, tmdbOf(id), p.addonBaseUrl,
                    mutableSetOf(CalendarReason.WATCHING), null, p.lastWatched))
            }
        val history: List<WatchedItem> = withTimeoutOrNull(20_000) { progress.watchedItems.first() }.orEmpty()
        history.filter { it.season != null && !dropped(it.contentId) }
            .groupBy { it.contentId }
            .forEach { (id, items) ->
                val w = items.maxBy { it.watchedAt }
                add(Candidate(id, "series", w.title, w.poster, null, id.takeIf { it.startsWith("tt") }, tmdbOf(id), null,
                    mutableSetOf(CalendarReason.WATCHING), null, w.watchedAt))
            }
        iptvFavoriteCandidates().forEach(::add)
        val hidden = hiddenShows.value.map { it.id }.toSet()
        return out.values.filter { it.id !in hidden && (it.imdb == null || it.imdb !in hidden) }
    }

    /** Reads only saved IPTV favorites and their local VOD catalogue cache. */
    private suspend fun iptvFavoriteCandidates(): List<Candidate> = withContext(Dispatchers.IO) {
        val profileReady = withTimeoutOrNull(5_000) { profileManager.activeProfileReady.first { it } } == true
        if (!profileReady) return@withContext emptyList()
        val storage = LiveTvStorage(context, profileManager.activeProfileId.value)
        val favoriteKeys = storage.favoriteVodKeys()
        if (favoriteKeys.isEmpty()) return@withContext emptyList()
        val sources = storage.sources().filter { it.type == LiveTvSourceType.Stalker || it.type == LiveTvSourceType.Xtream }
        if (sources.isEmpty()) return@withContext emptyList()
        val cachedItems = coroutineScope {
            sources.map { source ->
                async {
                    LiveTvVodCache.read(context, source.id, "movie") + LiveTvVodCache.read(context, source.id, "series")
                }
            }.awaitAll().flatten()
        }
        cachedItems.asSequence()
            .filter { it.favoriteKey in favoriteKeys }
            .distinctBy(LiveTvVodItem::favoriteKey)
            .mapNotNull { item ->
                val type = when {
                    item.type.equals("movie", ignoreCase = true) -> "movie"
                    isSeries(item.type) -> "series"
                    else -> return@mapNotNull null
                }
                val sourceTitle = if (type == "series") item.seriesTitle.takeIf(String::isNotBlank) ?: item.title else item.title
                val cleaned = normalizeLiveTvVodTitleForTmdb(sourceTitle)
                val year = yearOf(item.year) ?: yearOf(cleaned)
                val lookupTitle = year?.let { y ->
                    cleaned.replace(Regex("\\s*(?:\\($y\\)|\\[$y\\]|[-–:]?\\s+$y)\\s*$"), "").trim()
                } ?: cleaned
                if (lookupTitle.length < 2) return@mapNotNull null
                Candidate(
                    id = "iptv:${item.favoriteKey}",
                    type = type,
                    name = lookupTitle,
                    poster = item.posterUrl.takeIf(String::isNotBlank),
                    background = null,
                    imdb = null,
                    tmdb = null,
                    addonBaseUrl = null,
                    reasons = mutableSetOf(CalendarReason.IPTV),
                    year = year
                )
            }
            .toList()
    }

    private val iptvTitleMemo = ConcurrentHashMap<String, CalendarSources.TitleHit>()

    private fun iptvLookupKey(candidate: Candidate) = "${candidate.type}|${candidate.name.lowercase(Locale.ROOT)}|${candidate.year ?: ""}"

    /** Exact title, matching media type, and release year are required before an IPTV title is linked. */
    private suspend fun resolveIptvCandidates(candidates: List<Candidate>, fetch: Boolean): List<Candidate> {
        val unique = candidates.distinctBy(::iptvLookupKey)
        val semaphore = Semaphore(4)
        return coroutineScope {
            unique.map { candidate ->
                async {
                    val hit = iptvTitleMemo[iptvLookupKey(candidate)] ?: if (fetch) {
                        runCatching {
                            semaphore.withPermit {
                                sources.findTitle(candidate.name, candidate.type, strong = true, year = candidate.year)
                            }
                        }.getOrNull()?.also { iptvTitleMemo[iptvLookupKey(candidate)] = it }
                    } else null
                    if (hit == null || hit.kind != candidate.type) null
                    else candidate.copy(
                        id = hit.imdb ?: "tmdb:${hit.tmdbId}",
                        name = hit.name,
                        poster = candidate.poster ?: hit.poster,
                        imdb = hit.imdb,
                        tmdb = hit.tmdbId
                    )
                }
            }.awaitAll().filterNotNull()
        }
    }

    private fun mergeCandidates(candidates: List<Candidate>): List<Candidate> {
        val merged = LinkedHashMap<String, Candidate>()
        candidates.forEach { candidate ->
            val key = candidate.imdb?.takeIf { it.startsWith("tt") } ?: candidate.id
            val old = merged[key]
            if (old == null) merged[key] = candidate
            else {
                old.reasons += candidate.reasons
                if (old.poster == null && candidate.poster != null) {
                    merged[key] = old.copy(poster = candidate.poster, reasons = old.reasons)
                }
            }
        }
        val hidden = hiddenShows.value.map { it.id }.toSet()
        return merged.values.filter { it.id !in hidden && (it.imdb == null || it.imdb !in hidden) }
    }

    private suspend fun seenEpisodes(): Map<String, Seen> {
        val prog: List<WatchProgress> = withTimeoutOrNull(15_000) { progress.allProgress.first() }.orEmpty()
        val watched: List<WatchedItem> = withTimeoutOrNull(15_000) { progress.watchedItems.first() }.orEmpty()
        val furthest = HashMap<String, Pair<Int, Int>>()
        val marks = HashMap<String, MutableSet<Pair<Int, Int>>>()
        fun note(id: String, s: Int?, e: Int?, done: Boolean) {
            if (s == null || e == null) return
            val p = s to e
            val f = furthest[id]
            if (f == null || compare(p, f) > 0) furthest[id] = p
            if (done) marks.getOrPut(id) { HashSet() } += p
        }
        prog.forEach { note(it.contentId, it.season, it.episode, it.isCompleted()) }
        watched.forEach { note(it.contentId, it.season, it.episode, true) }
        // Movies watched: (0, 0).
        prog.filter { it.contentType.equals("movie", true) && it.isCompleted() }.forEach { marks.getOrPut(it.contentId) { HashSet() } += (0 to 0) }
        watched.filter { it.contentType.equals("movie", true) }.forEach { marks.getOrPut(it.contentId) { HashSet() } += (0 to 0) }
        return (furthest.keys + marks.keys).associateWith { Seen(furthest[it], marks[it].orEmpty()) }
    }

    /** Shows looked up recently are left alone unless [force]. */
    private suspend fun build(fetch: Boolean, force: Boolean = false) = mutex.withLock {
        runCatching { buildLocked(fetch, force) }.onFailure {
            _state.value = _state.value.copy(loading = false, progress = null, problem = it.message)
        }
    }

    private suspend fun buildLocked(fetch: Boolean, force: Boolean) {
        val s = settings.value
        if (isOff) {
            _state.value = CalendarState()
            CalendarHomeFeed.publish(emptyList(), emptyList())
            return
        }
        val raw = candidates()
        val iptv = raw.filter { CalendarReason.IPTV in it.reasons }
        val ordinary = raw.filterNot { CalendarReason.IPTV in it.reasons }
        val resolvedIptv = resolveIptvCandidates(
            iptv.filter { it.type == "series" }.take(MAX_SHOWS) + iptv.filter { it.type == "movie" }.take(MAX_MOVIES),
            fetch
        )
        val all = mergeCandidates(ordinary + resolvedIptv)
        // Bells first, then what you're watching, then your Library (newest first). A Library of
        // hundreds of titles doesn't mean hundreds of look-ups every time.
        val shows = all.filter { it.type == "series" }
            .sortedWith(compareByDescending<Candidate> { CalendarReason.BELL in it.reasons }
                .thenByDescending { CalendarReason.WATCHING in it.reasons }
                .thenByDescending { CalendarReason.IPTV in it.reasons }
                .thenByDescending { it.lastWatched })
            .take(MAX_SHOWS)
        val thisYear = LocalDate.now().year
        val movies = all.filter { it.type == "movie" && (CalendarReason.IPTV in it.reasons || CalendarReason.BELL in it.reasons || it.year == null || it.year >= thisYear - 1) }
            .sortedWith(compareByDescending<Candidate> { CalendarReason.IPTV in it.reasons }
                .thenByDescending { CalendarReason.BELL in it.reasons }
                .thenByDescending { it.year ?: 0 })
            .take(MAX_MOVIES)
        if (fetch) _state.value = _state.value.copy(loading = true, progress = 0 to (shows.size + movies.size), problem = null)
        val seen = seenEpisodes()
        val today = LocalDate.now()
        val from = today.minusDays(30)
        val to = today.plusDays(120)
        val entries = ArrayList<CalendarEntry>()
        var done = 0
        // The same show can come in twice under different ids (your Library by its IMDb id, what
        // you're watching by another): each show is only taken once.
        val usedShows = HashSet<Int>()
        for (c in shows) {
            val tvmId = if (fetch) sources.tvmazeId(c.imdb, c.tmdb) else cachedTvmazeId(c)
            if (tvmId != null && !usedShows.add(tvmId)) { done++; continue }
            var data = tvmId?.let { sources.cachedShow(it) }
            if (fetch && tvmId != null && (force || data == null || stale(data))) {
                data = sources.fetchShow(tvmId) ?: data
            }
            done++
            if (fetch && done % 5 == 0) _state.value = _state.value.copy(progress = done to (shows.size + movies.size))
            if (data == null) continue
            entries += episodesOf(c, data, seen[c.id] ?: c.imdb?.let { seen[it] }, s, from, to)
        }
        // Shows with no wide picture (TVmaze only had a poster): TMDB's, looked up once and kept.
        if (fetch) {
            val missing = entries.filter { it.kind == CalendarKind.EPISODE && it.background == null && it.contentId.startsWith("tt") }
                .map { it.contentId }.distinct().filter { !sources.backdropLooked(it) }.take(150)
            for (id in missing) runCatching { sources.showBackdrop(id) }
            if (missing.isNotEmpty()) {
                sources.saveBackdrops()
                for (i in entries.indices) {
                    val e = entries[i]
                    if (e.background == null) sources.knownBackdrop(e.contentId)?.let { entries[i] = e.copy(background = it) }
                }
            }
        }
        for (m in movies) {
            val dates = runCatching { sources.movieDates(m.imdb, m.tmdb, s.calendarCountry.ifBlank { "US" }, cachedOnly = !fetch) }.getOrNull()
            done++
            if (dates == null) continue
            entries += moviesOf(m, dates, seen[m.id] ?: m.imdb?.let { seen[it] }, from, to)
        }
        sources.saveMovies()
        // Never the same entry twice (its reasons are put together).
        val merged = entries.groupBy { it.key }.map { (_, same) ->
            if (same.size == 1) same[0] else same[0].copy(reasons = same.flatMap { it.reasons }.toSet())
        }
        val sorted = merged.sortedWith(compareBy<CalendarEntry> { it.day }.thenBy { it.airMs ?: Long.MAX_VALUE }.thenBy { it.title })
        _state.value = CalendarState(
            entries = sorted,
            loading = false,
            progress = null,
            updatedAt = if (fetch) System.currentTimeMillis() else _state.value.updatedAt.takeIf { it > 0 } ?: System.currentTimeMillis(),
            showCount = shows.size + movies.size
        )
        publishHome()
    }

    private val tvmIdMemo = HashMap<String, Int?>()

    /** TVmaze's id from what's kept on the TV only (no network). */
    private fun cachedTvmazeId(c: Candidate): Int? {
        val key = c.imdb ?: c.tmdb?.let { "tmdb:$it" } ?: return null
        if (tvmIdMemo.containsKey(key)) return tvmIdMemo[key]
        return sources.knownTvmazeId(c.imdb, c.tmdb).also { if (it != null) tvmIdMemo[key] = it }
    }

    private fun stale(d: CalendarSources.ShowData): Boolean {
        val age = System.currentTimeMillis() - d.fetchedAt
        val ended = d.show.status.equals("Ended", ignoreCase = true)
        // An episode out in the next day or so: checked more often (times and dates move).
        val soon = d.episodes.any { e ->
            val t = e.airMs ?: e.airDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: return@any false
            kotlin.math.abs(t - System.currentTimeMillis()) < 36 * CalendarSources.HOUR
        }
        return when {
            ended -> age > 7 * CalendarSources.DAY
            soon -> age > 3 * CalendarSources.HOUR
            // Shows between seasons: once a day.
            else -> age > 20 * CalendarSources.HOUR
        }
    }

    private fun episodesOf(
        c: Candidate,
        d: CalendarSources.ShowData,
        seen: Seen?,
        s: CalendarSettings,
        from: LocalDate,
        to: LocalDate
    ): List<CalendarEntry> {
        val now = System.currentTimeMillis()
        val eps = d.episodes.filter { e -> e.airDate != null && !e.airDate.isBefore(from) && !e.airDate.isAfter(to) }
            .sortedWith(compareBy<CalendarSources.Episode> { it.season }.thenBy { it.number })
        if (eps.isEmpty()) return emptyList()
        val lastSeason = d.episodes.maxOfOrNull { it.season }
        val ended = d.show.status.equals("Ended", ignoreCase = true)
        // Several episodes out at once (a streaming season) make one entry.
        val groups = ArrayList<MutableList<CalendarSources.Episode>>()
        for (e in eps) {
            val g = groups.lastOrNull()
            val prev = g?.last()
            if (prev != null && prev.season == e.season && prev.airDate == e.airDate && prev.number + 1 == e.number &&
                (prev.airMs == e.airMs || d.show.streaming)
            ) g.add(e) else groups += mutableListOf(e)
        }
        val contentId = c.imdb ?: d.show.imdb ?: c.id
        return groups.map { g ->
            val e = g.first()
            val last = g.last()
            val tags = ArrayList<CalendarTag>()
            when {
                e.number == 1 && e.season == 1 -> tags += CalendarTag.SERIES_PREMIERE
                e.number == 1 -> tags += CalendarTag.SEASON_PREMIERE
            }
            val total = last.seasonEpisodes
            if (total != null && last.number >= total && !(e.number == 1 && g.size == 1 && total > 1)) {
                tags += if (ended && last.season == lastSeason) CalendarTag.SERIES_FINALE else CalendarTag.SEASON_FINALE
            }
            // West Coast: the delayed airing's time (see CalendarAirTimes).
            val local = CalendarAirTimes.local(e.airMs, d.show.zone, d.show.airTime, d.show.streaming)
            val aired = (local ?: e.airDate!!.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()) <= now
            val pos = e.season to e.number
            val watched = seen != null && (pos in seen.watched || (seen.furthest != null && compare(pos, seen.furthest) < 0))
            val fresh = aired && !watched && !e.airDate!!.isBefore(LocalDate.now().minusDays(1))
            if (fresh) tags.add(0, CalendarTag.NEW)
            // Ahead of where you are: no title or picture (the next one you'd watch is fine).
            val spoiler = s.calendarHideSpoilers && seen?.furthest != null && !watched && isAhead(pos, seen.furthest)
            CalendarEntry(
                key = "ep|$contentId|${e.season}|${e.number}",
                kind = CalendarKind.EPISODE,
                contentId = contentId,
                type = "series",
                title = c.name.ifBlank { d.show.name },
                poster = c.poster ?: d.show.image,
                background = c.background ?: d.show.backdrop ?: sources.knownBackdrop(contentId),
                addonBaseUrl = c.addonBaseUrl,
                season = e.season,
                episode = e.number,
                lastEpisode = last.number.takeIf { g.size > 1 },
                episodeTitle = if (g.size > 1) null else e.name,
                episodeImage = e.image,
                description = e.summary.takeIf { g.size == 1 },
                airMs = local,
                listedAirMs = e.airMs,
                plainAirMs = local,
                day = local?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() } ?: e.airDate!!,
                network = d.show.network,
                streaming = d.show.streaming || e.airMs == null,
                tags = tags,
                reasons = c.reasons.toSet(),
                watched = watched,
                spoiler = spoiler,
                runtimeMin = e.runtime
            )
        }
    }

    private fun moviesOf(
        m: Candidate,
        dates: CalendarSources.MovieDates,
        seen: Seen?,
        from: LocalDate,
        to: LocalDate
    ): List<CalendarEntry> {
        val out = ArrayList<CalendarEntry>()
        val contentId = m.imdb ?: m.id
        val watched = seen?.watched?.contains(0 to 0) == true
        fun fmt(d: LocalDate): String {
            @Suppress("DEPRECATION")
            val locale = context.resources.configuration.locale ?: Locale.getDefault()
            return d.format(java.time.format.DateTimeFormatter.ofPattern("MMM d", locale))
        }
        fun entry(day: LocalDate, tag: CalendarTag, note: String?) = CalendarEntry(
            key = "mv|$contentId|$tag", kind = CalendarKind.MOVIE, contentId = contentId, type = "movie",
            title = m.name.ifBlank { dates.title ?: "" }, poster = m.poster ?: dates.poster, background = m.background ?: dates.backdrop,
            description = dates.overview,
            addonBaseUrl = m.addonBaseUrl, day = day, tags = listOf(tag), note = note, reasons = m.reasons.toSet(),
            watched = watched
        )
        val home = dates.digital
        dates.theater?.takeIf { !it.isBefore(from) && !it.isAfter(to) }?.let { t ->
            out += entry(t, CalendarTag.IN_THEATERS, home?.let { context.getString(R.string.calendar_at_home_date, fmt(it)) }
                ?: context.getString(R.string.calendar_not_out_at_home))
        }
        home?.takeIf { !it.isBefore(from) && !it.isAfter(to) }?.let { h ->
            out += entry(h, CalendarTag.AT_HOME, dates.theater?.let { context.getString(R.string.calendar_in_theaters_date, fmt(it)) })
        }
        if (home == null) dates.disc?.takeIf { !it.isBefore(from) && !it.isAfter(to) }?.let { b ->
            out += entry(b, CalendarTag.ON_DISC, dates.theater?.let { context.getString(R.string.calendar_in_theaters_date, fmt(it)) })
        }
        return out
    }

    // ------------------------------------------------------------------ Airing Today

    data class AiringToday(
        val day: LocalDate? = null,
        val entries: List<CalendarEntry> = emptyList(),
        val loading: Boolean = false,
        val problem: String? = null
    )

    /** Every show with a new episode on [day] (TV in your country, and streaming). */
    fun loadAiring(day: LocalDate = LocalDate.now(), force: Boolean = false) {
        if (!force && _airing.value.day == day && (_airing.value.loading || _airing.value.entries.isNotEmpty())) return
        _airing.value = AiringToday(day, _airing.value.entries.takeIf { _airing.value.day == day }.orEmpty(), loading = true)
        scope.launch {
            val s = settings.value
            val list = runCatching { sources.airingOn(day, s.calendarCountry.ifBlank { "US" }) }.getOrNull()
            if (list == null) {
                _airing.value = AiringToday(day, emptyList(), false, context.getString(R.string.calendar_error_airing_connection))
                return@launch
            }
            val followed = follows.value.flatMap { listOfNotNull(it.id, it.imdbId) }.toSet()
            val skipTypes = if (s.calendarSkipNewsTalk) setOf("News", "Talk Show", "Sports", "Panel Show") else emptySet()
            val hidden = hiddenShows.value.map { it.id }.toSet()
            val entries = list.filter { (show, _) -> show.type !in skipTypes && (show.imdb == null || show.imdb !in hidden) }
                .groupBy { it.first.id }
                .mapNotNull { (_, eps) ->
                    val (show, e) = eps.first()
                    val lastNumber = eps.maxOf { it.second.number }
                    val local = CalendarAirTimes.local(e.airMs, show.zone, show.airTime, show.streaming)
                    val id = show.imdb ?: return@mapNotNull null
                    CalendarEntry(
                        key = "today|$id|${e.season}|${e.number}",
                        kind = CalendarKind.EPISODE,
                        contentId = id,
                        type = "series",
                        title = show.name,
                        poster = show.image,
                        background = show.backdrop ?: sources.knownBackdrop(id),
                        season = e.season,
                        episode = e.number,
                        lastEpisode = lastNumber.takeIf { eps.size > 1 && it > e.number },
                        episodeTitle = if (eps.size > 1) null else e.name,
                        episodeImage = e.image,
                        description = e.summary.takeIf { eps.size == 1 },
                        airMs = local,
                        listedAirMs = e.airMs,
                        plainAirMs = local,
                        day = local?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() } ?: e.airDate ?: day,
                        network = show.network,
                        streaming = show.streaming || e.airMs == null,
                        tags = buildList {
                            if (e.number == 1 && e.season == 1) add(CalendarTag.SERIES_PREMIERE)
                            else if (e.number == 1) add(CalendarTag.SEASON_PREMIERE)
                        },
                        reasons = if (id in followed) setOf(CalendarReason.AIRING_TODAY, CalendarReason.BELL) else setOf(CalendarReason.AIRING_TODAY),
                        runtimeMin = e.runtime
                    )
                }
                .distinctBy { it.key }
                .sortedWith(compareBy<CalendarEntry> { it.streaming }.thenBy { it.airMs ?: Long.MAX_VALUE }.thenBy { it.title })
            _airing.value = AiringToday(day, entries, false, null)
            // Then the wide pictures TMDB has for shows TVmaze only had a poster for, a few at a time.
            val missing = entries.filter { it.background == null && !sources.backdropLooked(it.contentId) }.map { it.contentId }.distinct()
            var found = 0
            for ((i, id) in missing.withIndex()) {
                if (runCatching { sources.showBackdrop(id) }.getOrNull() != null) found++
                if (found > 0 && (found % 8 == 0 || i == missing.lastIndex)) {
                    val a = _airing.value
                    if (a.day != day) break
                    _airing.value = a.copy(entries = a.entries.map { e ->
                        if (e.background == null) sources.knownBackdrop(e.contentId)?.let { e.copy(background = it) } ?: e else e
                    })
                }
            }
            if (missing.isNotEmpty()) sources.saveBackdrops()
        }
    }

    /**
     * One episode as the Calendar sees it (when it airs), for its menu on the show's page.
     * Null when TVmaze doesn't know the show or the episode.
     */
    suspend fun episodeEntry(videoId: String, season: Int, episode: Int): CalendarEntry? = kotlinx.coroutines.withContext(Dispatchers.IO) { episodeEntryNow(videoId, season, episode) }

    private suspend fun episodeEntryNow(videoId: String, season: Int, episode: Int): CalendarEntry? = runCatching {
        val contentId = if (videoId.startsWith("tmdb:")) videoId.split(':').take(2).joinToString(":") else videoId.substringBefore(':')
        _state.value.entries.firstOrNull {
            it.kind == CalendarKind.EPISODE && it.contentId == contentId && it.season == season &&
                it.episode != null && episode in it.episode..(it.lastEpisode ?: it.episode)
        }?.let { return@runCatching it }
        val imdb = contentId.takeIf { it.startsWith("tt") }
        val tvmId = sources.tvmazeId(imdb, tmdbOf(contentId)) ?: return@runCatching null
        val data = sources.cachedShow(tvmId)?.takeIf { d -> d.episodes.any { it.season == season && it.number == episode } }
            ?: sources.fetchShow(tvmId) ?: return@runCatching null
        val ep = data.episodes.firstOrNull { it.season == season && it.number == episode } ?: return@runCatching null
        val candidate = Candidate(contentId, "series", data.show.name, data.show.image, null, imdb ?: data.show.imdb, tmdbOf(contentId), null,
            mutableSetOf(), null)
        episodesOf(candidate, data.copy(episodes = listOf(ep)), null, settings.value, LocalDate.of(1900, 1, 1), LocalDate.of(2999, 1, 1))
            .firstOrNull()
    }.getOrNull()

    // ------------------------------------------------------------------ show pages

    private val infoMemo = HashMap<String, Pair<Long, CalendarShowInfo?>>()

    /**
     * A show's next episode (and when its coming ones air) for its details page. Uses what the
     * Calendar already knows; otherwise looks the show up (kept for later).
     */
    suspend fun showInfo(id: String, imdbId: String?, type: String): CalendarShowInfo? {
        if (!isSeries(type)) return null
        val imdb = imdbId ?: id.takeIf { it.startsWith("tt") }
        val memoKey = imdb ?: id
        infoMemo[memoKey]?.let { (at, v) -> if (System.currentTimeMillis() - at < 10 * 60_000L) return v }
        val info = runCatching {
            val tvmId = sources.tvmazeId(imdb, tmdbOf(id)) ?: return@runCatching null
            var data = sources.cachedShow(tvmId)
            if (data == null || stale(data)) data = sources.fetchShow(tvmId) ?: data
            data ?: return@runCatching null
            val now = System.currentTimeMillis()
            fun whenOf(e: CalendarSources.Episode) = e.airMs ?: e.airDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
            val upcoming = data.episodes.filter { (whenOf(it) ?: 0L) > now - 2 * CalendarSources.HOUR }
                .sortedWith(compareBy<CalendarSources.Episode> { it.season }.thenBy { it.number })
            val next = upcoming.firstOrNull { e -> (whenOf(e) ?: 0L) > now - (e.runtime ?: 30) * 60_000L }
            val candidate = Candidate(id, "series", data.show.name, data.show.image, null, imdb ?: data.show.imdb, tmdbOf(id), null,
                mutableSetOf(), null)
            val nextEntry = next?.let { n ->
                episodesOf(candidate, data.copy(episodes = data.episodes.filter { it.season == n.season && it.number >= n.number }),
                    null, settings.value, LocalDate.now().minusDays(1), LocalDate.now().plusYears(2)).firstOrNull()
            }
            val status = when {
                next != null -> null
                data.show.status.equals("Ended", true) -> context.getString(R.string.calendar_status_ended)
                data.show.status.equals("To Be Determined", true) -> context.getString(R.string.calendar_status_waiting)
                data.show.status.equals("Running", true) -> context.getString(R.string.calendar_status_no_new_episodes)
                data.show.status.equals("In Development", true) -> context.getString(R.string.calendar_status_development)
                else -> null
            }
            CalendarShowInfo(nextEntry, status, upcoming.mapNotNull { e -> whenOf(e)?.let { (e.season to e.number) to it } }.toMap())
        }.getOrNull()
        infoMemo[memoKey] = System.currentTimeMillis() to info
        return info
    }

    /** A movie's release dates (theaters, at home, disc), for its details page. */
    internal suspend fun movieDates(id: String, imdbId: String?): CalendarSources.MovieDates? = runCatching {
        sources.movieDates(imdbId ?: id.takeIf { it.startsWith("tt") }, tmdbOf(id), settings.value.calendarCountry.ifBlank { "US" })
    }.getOrNull()

    // ------------------------------------------------------------------ Home

    /** Home's new-episode cards and Upcoming row, from the Calendar. */
    private fun publishHome() {
        val s = settings.value
        val now = System.currentTimeMillis()
        val today = LocalDate.now()
        val entries = _state.value.entries.filter { it.kind == CalendarKind.EPISODE && it.contentId.startsWith("tt") }
        fun whenOf(e: CalendarEntry) = e.airMs ?: e.day.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val released = if (!s.calendarNewEpisodeCards) emptyList() else entries
            .filter { it.followed && !it.watched && whenOf(it) <= now && !it.day.isBefore(today.minusDays(7)) }
            .groupBy { it.contentId }.map { (_, l) -> l.maxBy { whenOf(it) } }
        val upcoming = if (!s.calendarUpcomingRow) emptyList() else entries
            .filter { whenOf(it) > now && !it.day.isAfter(today.plusDays(7)) }
            .groupBy { it.contentId }.map { (_, l) -> l.minBy { whenOf(it) } }
        // Movies whose bell is rung, out to watch at home in the last two weeks (and not watched).
        val movies = if (!s.calendarNewEpisodeCards) emptyList() else _state.value.entries
            .filter { it.kind == CalendarKind.MOVIE && it.followed && CalendarTag.AT_HOME in it.tags && !it.watched &&
                !it.day.isAfter(today) && !it.day.isBefore(today.minusDays(14)) }
            .distinctBy { it.contentId }
        CalendarHomeFeed.publish(released, upcoming, movies)
    }

    companion object {
        private const val MAX_SHOWS = 400
        private const val MAX_MOVIES = 200

        fun isSeries(type: String) = type.equals("series", true) || type.equals("tv", true) || type.equals("show", true) ||
            type.equals("anime", true)

        fun tmdbOf(id: String): Int? = if (id.startsWith("tmdb:")) id.removePrefix("tmdb:").substringBefore(':').toIntOrNull() else null

        fun yearOf(s: String?): Int? = s?.let { Regex("(19|20)\\d{2}").find(it)?.value?.toIntOrNull() }

        fun compare(a: Pair<Int, Int>, b: Pair<Int, Int>): Int = if (a.first != b.first) a.first.compareTo(b.first) else a.second.compareTo(b.second)

        /** Further than the next episode after [furthest] (the next season's first counts as next). */
        fun isAhead(pos: Pair<Int, Int>, furthest: Pair<Int, Int>): Boolean {
            if (compare(pos, furthest) <= 0) return false
            if (pos.first == furthest.first && pos.second == furthest.second + 1) return false
            if (pos.first == furthest.first + 1 && pos.second == 1) return false
            return true
        }

        @Suppress("unused")
        fun dayOf(ms: Long): LocalDate = Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate()
    }
}
