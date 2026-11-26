package com.nuvio.tv.data.repository

import com.nuvio.tv.core.auth.AuthManager
import com.nuvio.tv.core.profile.ProfileManager
import com.nuvio.tv.core.sync.WatchProgressSyncService
import com.nuvio.tv.core.sync.WatchStateMutationStore
import com.nuvio.tv.core.sync.WatchedItemsSyncService
import com.nuvio.tv.core.tmdb.TmdbService
import com.nuvio.tv.core.tracking.TrackingHistoryWriterRegistry
import com.nuvio.tv.core.tracking.TrackingProgressProviderRegistry
import com.nuvio.tv.data.local.LayoutPreferenceDataStore
import com.nuvio.tv.data.local.TraktSettingsDataStore
import com.nuvio.tv.data.local.WatchProgressPreferences
import com.nuvio.tv.data.local.WatchProgressSource
import com.nuvio.tv.data.local.WatchedItemsPreferences
import com.nuvio.tv.data.mdblist.MdbListDroppedRecord
import com.nuvio.tv.data.mdblist.MdbListIds
import com.nuvio.tv.data.mdblist.MdbListItemType
import com.nuvio.tv.data.mdblist.MdbListMedia
import com.nuvio.tv.data.mdblist.MdbListPlayback
import com.nuvio.tv.data.mdblist.MdbListProgressProjection
import com.nuvio.tv.data.mdblist.MdbListSyncSnapshot
import com.nuvio.tv.data.mdblist.MdbListWatchedRecord
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.domain.repository.MetaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackingPlaybackScenariosTest {

    @Test
    fun `scenario 1 - frequent pause and resume in long series updates episode progress without re-indexing library`() {
        // MDBList: Frequent pause/resume on S02E05
        val mdbShowIds = MdbListIds(imdb = "tt_show_mdblist")
        val mdbSnapshot = MdbListSyncSnapshot(
            accountId = 555,
            watched = listOf(
                MdbListWatchedRecord(
                    type = MdbListItemType.EPISODE,
                    media = MdbListMedia(ids = mdbShowIds, title = "MDBList Show"),
                    season = 1,
                    episode = 1,
                    watchedAt = "2026-01-01T00:00:00Z"
                )
            ),
            playback = emptyList()
        )
        var mdbProjection = MdbListProgressProjection(mdbSnapshot)

        listOf(15f, 30f, 52f, 74f).forEach { progress ->
            val newPlayback = listOf(
                MdbListPlayback(
                    id = 2002L,
                    type = MdbListItemType.EPISODE,
                    media = MdbListMedia(ids = mdbShowIds, title = "MDBList Show"),
                    season = 2,
                    episode = 5,
                    progress = progress,
                    updatedAt = "2026-01-02T12:00:00Z"
                )
            )
            val updated = mdbProjection.withUpdatedPlayback(newPlayback)

            // Verify watched collections are preserved by reference
            assertTrue(mdbProjection.watchedItems === updated.watchedItems)
            assertTrue(mdbProjection.watchedMovieIds === updated.watchedMovieIds)
            assertTrue(mdbProjection.watchedShowEpisodes === updated.watchedShowEpisodes)

            val epProgress = updated.episodeProgress("tt_show_mdblist")[2 to 5]
            assertNotNull(epProgress)
            assertEquals(progress, epProgress?.progressPercent ?: -1f, 0.01f)

            mdbProjection = updated
        }
    }

    @Test
    fun `scenario 3 - switching profiles isolates progress for the same content without cross-profile leakage`() = runTest {
        val activeProfile = MutableStateFlow(1)
        val profileManager = mockk<ProfileManager> {
            every { activeProfileId } returns activeProfile
        }
        val progressPreferences = mockk<WatchProgressPreferences>(relaxed = true)

        val profile1Progress = WatchProgress(
            contentId = "tt_shared_movie",
            contentType = "movie",
            name = "Shared Movie",
            poster = "poster.jpg",
            backdrop = null,
            logo = null,
            videoId = "tt_shared_movie",
            season = null,
            episode = null,
            episodeTitle = null,
            position = 30_000L,
            duration = 100_000L,
            lastWatched = 100L
        )

        val profile2Progress = WatchProgress(
            contentId = "tt_shared_movie",
            contentType = "movie",
            name = "Shared Movie",
            poster = "poster.jpg",
            backdrop = null,
            logo = null,
            videoId = "tt_shared_movie",
            season = null,
            episode = null,
            episodeTitle = null,
            position = 85_000L,
            duration = 100_000L,
            lastWatched = 200L
        )

        coEvery { progressPreferences.getProgress("tt_shared_movie", 1) } returns flowOf(profile1Progress)
        coEvery { progressPreferences.getProgress("tt_shared_movie", 2) } returns flowOf(profile2Progress)

        val traktSettings = mockk<TraktSettingsDataStore>(relaxed = true) {
            every { watchProgressSource } returns MutableStateFlow(WatchProgressSource.NUVIO_SYNC)
        }

        val repository = WatchProgressRepositoryImpl(
            watchProgressPreferences = progressPreferences,
            traktSettingsDataStore = traktSettings,
            layoutPreferenceDataStore = mockk<LayoutPreferenceDataStore>(relaxed = true),
            watchProgressSyncService = mockk<WatchProgressSyncService>(relaxed = true),
            watchedItemsPreferences = mockk<WatchedItemsPreferences>(relaxed = true),
            watchedItemsSyncService = mockk<WatchedItemsSyncService>(relaxed = true),
            authManager = mockk<AuthManager>(relaxed = true) { every { isAuthenticated } returns false },
            metaRepository = mockk<MetaRepository>(relaxed = true),
            tmdbService = mockk<TmdbService>(relaxed = true),
            profileManager = profileManager,
            trackingProgressProviders = TrackingProgressProviderRegistry(emptySet()),
            trackingHistoryWriters = TrackingHistoryWriterRegistry(emptySet()),
            mutationStore = mockk<WatchStateMutationStore>(relaxed = true),
            serverWatched = mockk(relaxed = true)
        )

        // Profile 1 saves its progress
        repository.saveProgress(profile1Progress, profileId = 1, syncRemote = false)
        coVerify(exactly = 1) { progressPreferences.saveProgress(profile1Progress, profileId = 1) }

        // Switch to Profile 2
        activeProfile.value = 2
        repository.saveProgress(profile2Progress, profileId = 2, syncRemote = false)
        coVerify(exactly = 1) { progressPreferences.saveProgress(profile2Progress, profileId = 2) }

        // Verify Profile 1's progress was not touched when Profile 2 saved
        coVerify(exactly = 0) { progressPreferences.saveProgress(profile2Progress, profileId = 1) }
    }

    @Test
    fun `scenario 4 - series with dropped season correctly excludes dropped season from next up candidates`() {
        val showIds = MdbListIds(imdb = "tt_dropped_series")
        val media = MdbListMedia(ids = showIds, title = "Dropped Series")

        val snapshot = MdbListSyncSnapshot(
            accountId = 777,
            watched = listOf(
                MdbListWatchedRecord(
                    type = MdbListItemType.EPISODE,
                    media = media,
                    season = 1,
                    episode = 10,
                    watchedAt = "2026-01-01T00:00:00Z"
                ),
                MdbListWatchedRecord(
                    type = MdbListItemType.EPISODE,
                    media = media,
                    season = 2,
                    episode = 5,
                    watchedAt = "2026-01-02T00:00:00Z"
                )
            ),
            // User explicitly dropped Season 2
            dropped = listOf(
                MdbListDroppedRecord(ids = showIds, season = 2)
            ),
            playback = emptyList()
        )

        val projection = MdbListProgressProjection(snapshot)

        // Verify Season 2 is hidden/dropped, Season 1 is not
        assertTrue(projection.isHidden("tt_dropped_series", season = 2))
        assertFalse(projection.isHidden("tt_dropped_series", season = 1))

        // Next-Up candidate must include Season 2 in excludedNextUpSeasons
        val nextUpSeeds = projection.nextUp(preferFurthest = true)
        val seed = nextUpSeeds.firstOrNull { it.contentId == "tt_dropped_series" }
        assertNotNull(seed)
        assertTrue(seed!!.excludedNextUpSeasons.contains(2))
        assertFalse(seed.excludedNextUpSeasons.contains(1))
    }
