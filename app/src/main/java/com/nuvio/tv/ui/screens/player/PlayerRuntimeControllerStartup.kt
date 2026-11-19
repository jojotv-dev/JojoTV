package com.nuvio.tv.ui.screens.player

import android.app.Activity
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

internal fun PlayerRuntimeController.attachHostActivity(activity: Activity?) {
    hostActivityRef = activity?.let { WeakReference(it) }
}

internal fun PlayerRuntimeController.startInitialPlaybackIfNeeded() {
    if (initialPlaybackStarted) return

    initialPlaybackStarted = true

    val bg = navigationArgs.bingeGroup
    val cid = contentId
    if (cid != null && bg != null) {
        scope.launch(kotlinx.coroutines.NonCancellable) {
            val existingFilename = bingeGroupCacheDataStore.getFilename(cid)
            bingeGroupCacheDataStore.replace(cid, bg, existingFilename)
        }
    }

    val clickElapsedMs = launchStartedAtElapsedMs
        ?.let { (SystemClock.elapsedRealtime() - it).coerceAtLeast(0L) }
        ?: -1L
    queuePlaybackRawEventLine(
        "PLAYER_START_REQUEST: clickElapsedMs=$clickElapsedMs host=${initialStreamUrl.safeStartupHost()} " +
            "contentId=${contentId ?: "n/a"} videoId=${currentVideoId ?: "n/a"} " +
            "S${currentSeason ?: "-"}E${currentEpisode ?: "-"} " +
            "startFromBeginning=${navigationArgs.startFromBeginning} streamName=${streamName ?: "n/a"}"
    )
    Log.d(
        "PlayerStartup",
        "startInitialPlayback: host=${currentStreamUrl.safeStartupHost()} " +
            "urlHash=${currentStreamUrl.hashCode().toUInt().toString(16)}"
    )
    preparePlaybackBeforeStart(
        url = currentStreamUrl,
        headers = currentHeaders,
        loadSavedProgress = !navigationArgs.startFromBeginning
    )
    recordDistinctPluginPlay(
        bingeGroup = navigationArgs.bingeGroup,
        playbackIdentity = initialStreamUrl
    )
}

internal fun PlayerRuntimeController.currentHostActivity(): Activity? {
    return hostActivityRef?.get()
}

private fun String.safeStartupHost(): String {
    return runCatching {
        android.net.Uri.parse(this).host ?: substringBefore("://").takeIf { it.isNotBlank() } ?: "unknown"
    }.getOrDefault("unknown")
}
