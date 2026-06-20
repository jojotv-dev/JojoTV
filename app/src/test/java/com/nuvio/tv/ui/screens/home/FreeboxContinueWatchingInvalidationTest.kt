package com.nuvio.tv.ui.screens.home

import com.nuvio.tv.data.freebox.FreeboxFileEntry
import com.nuvio.tv.domain.model.WatchProgress
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FreeboxContinueWatchingInvalidationTest {
    @Test
    fun staleFreeboxProgressInCheckedDirectoryIsInvalidated() {
        val entries = listOf(
            freeboxFile("/Freebox/Videos/Existing.mkv")
        )
        val currentPaths = entries.map { it.path }.toSet()
        val checkedDirectories = freeboxCheckedDirectories(entries)

        val stale = progress("freebox:/Freebox/Videos/Deleted.mkv")

        assertTrue(stale.isStaleFreeboxProgress(currentPaths, checkedDirectories))
    }

    @Test
    fun existingFreeboxProgressIsKept() {
        val entries = listOf(
            freeboxFile("/Freebox/Videos/Existing.mkv")
        )
        val currentPaths = entries.map { it.path }.toSet()
        val checkedDirectories = freeboxCheckedDirectories(entries)

        val existing = progress("freebox:/Freebox/Videos/Existing.mkv")

        assertFalse(existing.isStaleFreeboxProgress(currentPaths, checkedDirectories))
    }

    @Test
    fun freeboxProgressInUncheckedDirectoryIsKept() {
        val entries = listOf(
            freeboxFile("/Freebox/Videos/Existing.mkv")
        )
        val currentPaths = entries.map { it.path }.toSet()
        val checkedDirectories = freeboxCheckedDirectories(entries)

        val otherDirectory = progress("freebox:/Freebox/Other/MaybeStillThere.mkv")

        assertFalse(otherDirectory.isStaleFreeboxProgress(currentPaths, checkedDirectories))
    }

    @Test
    fun nonFreeboxProgressIsKept() {
        val entries = listOf(
            freeboxFile("/Freebox/Videos/Existing.mkv")
        )
        val currentPaths = entries.map { it.path }.toSet()
        val checkedDirectories = freeboxCheckedDirectories(entries)

        val remote = progress("tt1234567")

        assertFalse(remote.isStaleFreeboxProgress(currentPaths, checkedDirectories))
    }

    @Test
    fun legacyProgressIsRejectedWhenFileWasModifiedAfterPlayback() {
        val progress = progress("freebox:/Freebox/Videos/Replaced.mkv")
        val replacement = freeboxFile(
            path = "/Freebox/Videos/Replaced.mkv",
            modifiedMs = progress.lastWatched + 120_000L
        )

        assertFalse(progress.isCompatibleLegacyFreeboxProgress(replacement))
    }

    @Test
    fun legacyProgressIsKeptWhenFilePredatesPlayback() {
        val progress = progress("freebox:/Freebox/Videos/Existing.mkv")
        val existing = freeboxFile(
            path = "/Freebox/Videos/Existing.mkv",
            modifiedMs = progress.lastWatched - 120_000L
        )

        assertTrue(progress.isCompatibleLegacyFreeboxProgress(existing))
    }

    @Test
    fun legacyProgressWithoutIdentityEvidenceIsRejected() {
        val progress = progress("freebox:/Freebox/Videos/Unknown.mkv")

        assertFalse(progress.isCompatibleLegacyFreeboxProgress(freeboxFile("/Freebox/Videos/Unknown.mkv")))
    }

    @Test
    fun legacyProgressWithDifferentDurationIsRejected() {
        val progress = progress("freebox:/Freebox/Videos/Replaced.mkv")
        val replacement = freeboxFile(
            path = "/Freebox/Videos/Replaced.mkv",
            durationMs = progress.duration + 60_000L,
            modifiedMs = progress.lastWatched - 120_000L
        )

        assertFalse(progress.isCompatibleLegacyFreeboxProgress(replacement))
    }

    private fun freeboxFile(
        path: String,
        durationMs: Long? = null,
        modifiedMs: Long? = null
    ) = FreeboxFileEntry(
        name = path.substringAfterLast("/"),
        path = path,
        isDirectory = false,
        durationMs = durationMs,
        modifiedMs = modifiedMs
    )

    private fun progress(contentId: String) = WatchProgress(
        contentId = contentId,
        contentType = "freebox",
        name = contentId.substringAfterLast("/"),
        poster = null,
        backdrop = null,
        logo = null,
        videoId = contentId,
        season = null,
        episode = null,
        episodeTitle = null,
        position = 60_000L,
        duration = 600_000L,
        lastWatched = 1_700_000_000_000L
    )
}
