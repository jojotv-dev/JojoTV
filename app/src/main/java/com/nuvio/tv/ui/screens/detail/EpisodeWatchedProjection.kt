package com.nuvio.tv.ui.screens.detail

internal fun resolveEpisodeWatchedState(
    currentlyWatched: Boolean,
    completedByProgress: Boolean,
    optimisticallyMarked: Boolean,
    optimisticallyUnmarked: Boolean,
    watchedByVideoId: Boolean?
): Boolean {
    if (optimisticallyUnmarked) return false
    if (optimisticallyMarked) return true
    if (watchedByVideoId == true) return true
    if (
        watchedByVideoId == false &&
        currentlyWatched &&
        !completedByProgress
    ) {
        return false
    }
    return currentlyWatched || completedByProgress
}
