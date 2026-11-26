package com.nuvio.tv.data.repository

import com.nuvio.tv.core.tracking.TrackingScrobbleAction
import com.nuvio.tv.data.mdblist.MdbListIds
import com.nuvio.tv.data.mdblist.MdbListItemType
import com.nuvio.tv.data.mdblist.MdbListMedia
import com.nuvio.tv.data.mdblist.MdbListMutationTarget
import com.nuvio.tv.data.mdblist.MdbListPlayback
import com.nuvio.tv.data.mdblist.MdbListProgressProjection
import com.nuvio.tv.data.mdblist.MdbListScrobbleReceipt
import com.nuvio.tv.data.mdblist.MdbListSyncSnapshot
import com.nuvio.tv.data.mdblist.MdbListWatchedRecord
import com.nuvio.tv.data.mdblist.applyScrobble
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackingProgressOverheadTest {

    @Test
    fun `verify - mdblist withUpdatedPlayback reuses watched collections and showIdSiblings without copying`() {
        val snapshot = MdbListSyncSnapshot(
            accountId = 123,
            watched = listOf(
                MdbListWatchedRecord(
                    type = MdbListItemType.MOVIE,
                    media = MdbListMedia(ids = MdbListIds(imdb = "tt100")),
                    watchedAt = "2026-01-01T00:00:00Z"
                ),
                MdbListWatchedRecord(
                    type = MdbListItemType.EPISODE,
                    media = MdbListMedia(ids = MdbListIds(imdb = "tt300", tmdb = 300L)),
                    season = 1,
                    episode = 1,
                    watchedAt = "2026-01-01T00:00:00Z"
                )
            ),
            playback = emptyList()
        )
        val initialProjection = MdbListProgressProjection(snapshot)

        // Case 1: Known show playback reuses watchedShowIdSiblings by reference
        val knownShowPlayback = listOf(
            MdbListPlayback(
                id = 1L,
                type = MdbListItemType.EPISODE,
                media = MdbListMedia(ids = MdbListIds(imdb = "tt300", tmdb = 300L)),
                season = 1,
                episode = 2,
                progress = 45f,
                updatedAt = "2026-01-02T00:00:00Z"
            )
        )
        val knownShowProjection = initialProjection.withUpdatedPlayback(knownShowPlayback)
        assertTrue(initialProjection.watchedItems === knownShowProjection.watchedItems)
        assertTrue(initialProjection.watchedMovieIds === knownShowProjection.watchedMovieIds)
        assertTrue(initialProjection.watchedShowEpisodes === knownShowProjection.watchedShowEpisodes)
        assertTrue(initialProjection.showIdSiblings === knownShowProjection.showIdSiblings)
        assertTrue(initialProjection.mediaIndex === knownShowProjection.mediaIndex)

        // Case 2: Brand-new show playback uses overlay map and resolves both watched and new show siblings
        val newShowPlayback = listOf(
            MdbListPlayback(
                id = 2L,
                type = MdbListItemType.EPISODE,
                media = MdbListMedia(ids = MdbListIds(imdb = "tt999", tmdb = 999L)),
                season = 1,
                episode = 1,
                progress = 30f,
                updatedAt = "2026-01-02T00:00:00Z"
            )
        )
        val newShowProjection = initialProjection.withUpdatedPlayback(newShowPlayback)
        assertEquals(setOf("tt300", "imdb:tt300", "tmdb:300"), newShowProjection.showIdSiblings["tt300"])
        assertEquals(setOf("tt999", "imdb:tt999", "tmdb:999"), newShowProjection.showIdSiblings["tt999"])
    }

    @Test
    fun `verify - mdblist applyScrobble on pause and completed stop uses mediaIndex without scanning unrelated watched items`() {
        val watched = (1..2500).map { i ->
            MdbListWatchedRecord(
                type = MdbListItemType.MOVIE,
                media = MdbListMedia(ids = MdbListIds(imdb = "tt$i", tmdb = i.toLong())),
                watchedAt = "2026-01-01T00:00:00Z"
            )
        }
        val snapshot = MdbListSyncSnapshot(
            accountId = 123,
            watched = watched,
            playback = emptyList()
        )
        val projection = MdbListProgressProjection(snapshot)
        val target = MdbListMutationTarget(
            type = MdbListItemType.MOVIE,
            media = MdbListMedia(ids = MdbListIds(imdb = "tt500"))
        )
        val pauseReceipt = MdbListScrobbleReceipt(
            target = target,
            action = TrackingScrobbleAction.PAUSE,
            progress = 42f,
            timestamp = "2026-01-02T00:00:00Z",
            playbackId = 999L
        )

        val paused = snapshot.applyScrobble(pauseReceipt, projection.mediaIndex)
        assertTrue(snapshot.watched === paused.watched)
        assertEquals(1, paused.playback.size)
        assertEquals(500L, paused.playback.single().media.ids.tmdb)

        val stopReceipt = MdbListScrobbleReceipt(
            target = MdbListMutationTarget(
                type = MdbListItemType.MOVIE,
                media = MdbListMedia(title = "Enriched Movie 500", ids = MdbListIds(imdb = "tt500", trakt = 5000L))
            ),
            action = TrackingScrobbleAction.STOP,
            progress = 90f,
            timestamp = "2026-01-03T00:00:00Z",
            playbackId = 999L
        )
        val completed = paused.applyScrobble(stopReceipt, projection.mediaIndex)
        val unindexedCompleted = paused.applyScrobble(stopReceipt, mediaIndex = null)
        assertTrue(completed.playback.isEmpty())
        assertEquals(2500, completed.watched.size)
        // Indexed completed scrobble produces the exact same normalized snapshot as unindexed normalizeMedia()
        assertEquals(unindexedCompleted.watched, completed.watched)
        // Unrelated watched record is preserved by reference at its original position
        assertTrue(snapshot.watched.first() === completed.watched.first())
        // Matched watched record was enriched and placed at the end with latest watchedAt
        assertEquals("tt500", completed.watched.last().media.ids.imdb)
        assertEquals(500L, completed.watched.last().media.ids.tmdb)
        assertEquals(5000L, completed.watched.last().media.ids.trakt)
        assertEquals("2026-01-03T00:00:00Z", completed.watched.last().watchedAt)
    }
}
