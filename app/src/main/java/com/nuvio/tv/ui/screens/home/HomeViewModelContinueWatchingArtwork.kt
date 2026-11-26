package com.nuvio.tv.ui.screens.home

import android.content.Context
import android.net.Uri
import com.nuvio.tv.youtube.AccountScope
import com.nuvio.tv.youtube.YouTubeAccountScopes
import com.nuvio.tv.youtube.YouTubeProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

private const val CONTINUE_WATCHING_ARTWORK_PREFERENCES = "continue_watching_hero_artwork"

internal fun ContinueWatchingItem.continueWatchingArtworkKey(): String {
    val values = when (this) {
        is ContinueWatchingItem.InProgress -> listOf(
            progress.contentId,
            progress.videoId,
            progress.season?.toString().orEmpty(),
            progress.episode?.toString().orEmpty(),
        )
        is ContinueWatchingItem.NextUp -> listOf(
            info.contentId,
            info.videoId,
            info.season.toString(),
            info.episode.toString(),
        )
    }
    return Uri.encode(values.joinToString("|"), "")
}

internal fun List<ContinueWatchingItem>.withContinueWatchingHeroArtworkOverrides(
    overrides: Map<String, String>,
): List<ContinueWatchingItem> = map { item ->
    val imageUrl = overrides[item.continueWatchingArtworkKey()] ?: return@map item
    when (item) {
        is ContinueWatchingItem.InProgress -> item.copy(customLandscapePoster = imageUrl)
        is ContinueWatchingItem.NextUp -> item.copy(customLandscapePoster = imageUrl)
    }
}

internal fun HomeViewModel.loadContinueWatchingHeroArtworkOverrides(
    profileId: Int = profileManager.activeProfileId.value,
): Map<String, String> {
    val prefix = "$profileId:"
    return appContext.getSharedPreferences(CONTINUE_WATCHING_ARTWORK_PREFERENCES, Context.MODE_PRIVATE)
        .all
        .mapNotNull { (key, value) ->
            if (key.startsWith(prefix) && value is String && value.isNotBlank()) {
                key.removePrefix(prefix) to value
            } else {
                null
            }
        }
        .toMap()
}

internal suspend fun HomeViewModel.findContinueWatchingHeroArtworkCandidates(
    item: ContinueWatchingItem,
): List<String> {
    val title: String
    val mediaType: String
    val contentId: String
    val year: Int?
    when (item) {
        is ContinueWatchingItem.InProgress -> {
            title = item.progress.name
            mediaType = if (item.progress.contentType.equals("movie", ignoreCase = true)) "movie" else "series"
            contentId = item.progress.contentId
            year = item.releaseInfo?.take(4)?.toIntOrNull()
        }
        is ContinueWatchingItem.NextUp -> {
            title = item.info.name
            mediaType = if (item.info.contentType.equals("movie", ignoreCase = true)) "movie" else "series"
            contentId = item.info.contentId
            year = item.info.releaseInfo?.take(4)?.toIntOrNull()
        }
    }

    val youtubeCandidates = if (YouTubeProgress.isYouTubeId(contentId)) {
        searchYouTubeHeroArtworkCandidates(
            title = title,
            videoId = contentId.removePrefix(YouTubeProgress.ID_PREFIX),
        )
    } else {
        emptyList()
    }
    val language = tmdbSettingsDataStore.settings.first().language
    val idCandidates = tmdbService.artworkCandidatesForContentId(
        contentId = contentId,
        mediaType = mediaType,
        portrait = false,
        language = language,
    )
    val tmdbCandidates = when {
        idCandidates.isNotEmpty() -> idCandidates
        else -> {
            val exactTitleCandidates = tmdbService.artworkCandidatesForExactTitle(
                title = title,
                mediaType = mediaType,
                portrait = false,
                language = language,
                year = year,
            )
            if (exactTitleCandidates.isNotEmpty()) {
                exactTitleCandidates
            } else {
                tmdbService.artworkCandidatesForSearchTitle(
                    title = title,
                    mediaType = mediaType,
                    portrait = false,
                    language = language,
                    year = year,
                )
            }
        }
    }

    return (youtubeCandidates + tmdbCandidates).distinct()
}

private suspend fun HomeViewModel.searchYouTubeHeroArtworkCandidates(
    title: String,
    videoId: String,
): List<String> {
    if (title.isBlank()) return emptyList()
    return withContext(Dispatchers.IO) {
        runCatching {
            YouTubeAccountScopes.engine(appContext, AccountScope.ONETUBE)
                .contentService
                .getSearchObserve(title)
                .blockingFirst()
                .asSequence()
                .flatMap { it.mediaItems.orEmpty().asSequence() }
                .filter { !it.videoId.isNullOrBlank() }
                .sortedBy { if (it.videoId == videoId) 0 else 1 }
                .mapNotNull { it.cardImageUrl?.trim()?.takeIf(String::isNotBlank) }
                .distinct()
                .take(16)
                .toList()
        }.getOrDefault(emptyList())
    }
}

internal suspend fun HomeViewModel.setContinueWatchingHeroArtwork(
    item: ContinueWatchingItem,
    imageUrl: String?,
) {
    val profileId = profileManager.activeProfileId.value
    val artworkKey = item.continueWatchingArtworkKey()
    val preferenceKey = "$profileId:$artworkKey"
    withContext(Dispatchers.IO) {
        val preferences = appContext.getSharedPreferences(CONTINUE_WATCHING_ARTWORK_PREFERENCES, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        if (imageUrl.isNullOrBlank()) editor.remove(preferenceKey) else editor.putString(preferenceKey, imageUrl)
        editor.apply()
    }

    _uiState.update { state ->
        val updatedOverrides = state.continueWatchingHeroArtworkOverrides.toMutableMap().apply {
            if (imageUrl.isNullOrBlank()) remove(artworkKey) else put(artworkKey, imageUrl)
        }
        state.copy(continueWatchingHeroArtworkOverrides = updatedOverrides)
    }
}
