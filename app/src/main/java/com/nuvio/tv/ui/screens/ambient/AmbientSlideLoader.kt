package com.nuvio.tv.ui.screens.ambient

import android.content.Context
import android.net.Uri
import com.nuvio.tv.core.profile.ProfileManager
import com.nuvio.tv.core.tmdb.MetaPreviewEnricher
import com.nuvio.tv.core.tmdb.TmdbMetadataService
import com.nuvio.tv.core.tmdb.TmdbService
import com.nuvio.tv.data.local.TmdbSettingsDataStore
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.LibraryEntry
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.PosterShape
import com.nuvio.tv.domain.repository.LibraryRepository
import com.nuvio.tv.reshaped.livetv.LiveTvRepository
import com.nuvio.tv.reshaped.livetv.LiveTvSourceType
import com.nuvio.tv.reshaped.livetv.LiveTvVodCache
import com.nuvio.tv.reshaped.livetv.LiveTvVodItem
import com.nuvio.tv.ui.screens.home.readHomeFavoriteArtworkOverride
import com.nuvio.tv.ui.screens.home.HOME_IPTV_FAVORITE_ITEM_ID_PREFIX
import com.nuvio.tv.ui.util.localizedGenreLabel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_SLIDES = 30
private const val MAX_GENRES = 3
private const val AMBIENT_ENRICHMENT_CONCURRENCY = 4
private const val LIVE_TV_FAVORITES_LOAD_TIMEOUT_MS = 5_000L
private val AMBIENT_TECHNICAL_SUFFIX = Regex(
    """(?i)\s*(?:\([^)]*(?:4k|8k|2160p|1080p|720p|uhd|hdr|dolby[ .-]?vision|dv|web[ ._-]?(?:dl|rip)|blu[ .-]?ray|bluray|hevc|x26[45]|h26[45])[^)]*\)|\[[^\]]*(?:4k|8k|2160p|1080p|720p|uhd|hdr|dolby[ .-]?vision|dv|web[ ._-]?(?:dl|rip)|blu[ .-]?ray|bluray|hevc|x26[45]|h26[45])[^\]]*\])\s*$"""
)
private val AMBIENT_YEAR_SUFFIX = Regex("""\s+(?:\((?:19|20)\d{2}\)|\[(?:19|20)\d{2}\])\s*$""")

private fun String.toCleanAmbientTitle(): String {
    var cleaned = trim()
    repeat(2) {
        cleaned = cleaned
            .replace(AMBIENT_TECHNICAL_SUFFIX, "")
            .replace(AMBIENT_YEAR_SUFFIX, "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
    return cleaned
}

@Singleton
class AmbientSlideLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileManager: ProfileManager,
    private val libraryRepository: LibraryRepository,
    private val tmdbSettingsDataStore: TmdbSettingsDataStore,
    private val metaPreviewEnricher: MetaPreviewEnricher,
    private val tmdbService: TmdbService,
    private val tmdbMetadataService: TmdbMetadataService,
) {
    @Suppress("UNUSED_PARAMETER")
    suspend fun load(
        legacyCatalogKeys: List<String>,
        profileId: Int = profileManager.activeProfileId.value,
        onLibrarySlides: suspend (List<AmbientSlide>) -> Unit = {},
    ): List<AmbientSlide> = coroutineScope {
        val libraryFavorites = libraryRepository.libraryItems.first()
            .filter(LibraryEntry::isHomeFavoriteMovieOrSeries)
            .map(LibraryEntry::toMetaPreview)
        onLibrarySlides(libraryFavorites.take(MAX_SLIDES).toAmbientSlides(profileId))
        val iptvFavorites = loadIptvFavorites(profileId)
        val favoriteItems = (libraryFavorites + iptvFavorites)
            .distinctBy { "${it.apiType}:${it.id}" }
            .shuffled()
            .take(MAX_SLIDES)

        val tmdbSettings = tmdbSettingsDataStore.settings.value
        val enriched = if (tmdbSettings.enabled) {
            val libraryItems = favoriteItems.filterNot { it.id.startsWith(HOME_IPTV_FAVORITE_ITEM_ID_PREFIX) }
            val iptvItems = favoriteItems.filter { it.id.startsWith(HOME_IPTV_FAVORITE_ITEM_ID_PREFIX) }
            val libraryEnriched = metaPreviewEnricher.enrich(
                libraryItems,
                tmdbSettings.copy(useArtwork = true, useBasicInfo = true),
            ).let { results ->
                if (tmdbSettings.useArtwork) results
                else libraryItems.zip(results).map { (original, enriched) ->
                    enriched.copy(
                        background = original.background,
                        poster = original.poster,
                        logo = enriched.logo ?: original.logo,
                    )
                }
            }
            val iptvEnriched = enrichIptvFavorites(
                items = iptvItems,
                language = tmdbSettings.language,
                useArtwork = tmdbSettings.useArtwork,
            )
            (libraryEnriched + iptvEnriched).associateBy { it.id }
                .let { enrichedById -> favoriteItems.map { enrichedById[it.id] ?: it } }
        } else {
            favoriteItems
        }

        enriched.toAmbientSlides(profileId).shuffled()
    }

    private fun Iterable<MetaPreview>.toAmbientSlides(profileId: Int): List<AmbientSlide> =
        mapNotNull { item ->
            val selectedLandscape = readHomeFavoriteArtworkOverride(
                context = context,
                profileId = profileId,
                itemId = item.id,
                portrait = false,
            )
            val selectedPortrait = readHomeFavoriteArtworkOverride(
                context = context,
                profileId = profileId,
                itemId = item.id,
                portrait = true,
            )
            val artworkItem = when {
                selectedLandscape != null -> item.copy(
                    background = selectedLandscape,
                    landscapePoster = selectedLandscape,
                )
                selectedPortrait != null &&
                    item.background.isNullOrBlank() &&
                    item.landscapePoster.isNullOrBlank() -> item.copy(
                    background = null,
                    landscapePoster = null,
                    poster = selectedPortrait,
                )
                else -> item
            }
            artworkItem.toSlide()
        }

    private suspend fun enrichIptvFavorites(
        items: List<MetaPreview>,
        language: String,
        useArtwork: Boolean,
    ): List<MetaPreview> = coroutineScope {
        val semaphore = Semaphore(AMBIENT_ENRICHMENT_CONCURRENCY)
        items.map { item ->
            async {
                semaphore.withPermit {
                    val cleanTitle = item.name.toCleanAmbientTitle()
                    val match = try {
                        tmdbService.findExactTitleMatch(
                            title = cleanTitle,
                            mediaType = item.apiType,
                            language = language,
                            year = item.releaseInfo?.take(4)?.toIntOrNull(),
                        )
                    } catch (cancel: CancellationException) {
                        throw cancel
                    } catch (_: Exception) {
                        null
                    } ?: return@withPermit item

                    val enrichment = try {
                        tmdbMetadataService.fetchEnrichment(
                            tmdbId = match.tmdbId.toString(),
                            contentType = match.contentType,
                            language = language,
                        )
                    } catch (cancel: CancellationException) {
                        throw cancel
                    } catch (_: Exception) {
                        null
                    }
                    val backdrop = enrichment?.backdrop?.takeIf { useArtwork }
                    item.copy(
                        background = backdrop ?: item.background,
                        landscapePoster = backdrop ?: item.landscapePoster,
                        logo = enrichment?.logo ?: item.logo,
                        genres = enrichment?.genres?.takeIf { it.isNotEmpty() } ?: item.genres,
                    )
                }
            }
        }.awaitAll()
    }

    private suspend fun loadIptvFavorites(profileId: Int): List<MetaPreview> {
        return try {
            LiveTvRepository.ensureLoaded(context, profileId)
            val liveTvState = withTimeoutOrNull(LIVE_TV_FAVORITES_LOAD_TIMEOUT_MS) {
                LiveTvRepository.uiState.first { it.sourceStorageReady }
            } ?: return emptyList()

            val favoriteKeys = liveTvState.favoriteVodKeys
            if (favoriteKeys.isEmpty()) return emptyList()

            coroutineScope {
                liveTvState.sources
                    .filter { it.type == LiveTvSourceType.Xtream || it.type == LiveTvSourceType.Stalker }
                    .map { source ->
                        async {
                            LiveTvVodCache.read(context, source.id, "movie") +
                                LiveTvVodCache.read(context, source.id, "series")
                        }
                    }
                    .awaitAll()
                    .flatten()
                    .filter { item ->
                        item.favoriteKey in favoriteKeys && item.type.isHomeFavoriteMovieOrSeries()
                    }
                    .map(LiveTvVodItem::toAmbientFavoritePreview)
            }
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun MetaPreview.toSlide(): AmbientSlide? {
        val image = background?.takeIf(String::isNotBlank)
            ?: landscapePoster?.takeIf(String::isNotBlank)
            ?: poster?.takeIf(String::isNotBlank)
            ?: return null
        return AmbientSlide(
            id = "$apiType:$id",
            title = name.toCleanAmbientTitle(),
            backdropUrl = image,
            logoUrl = logo?.takeIf(String::isNotBlank),
            genres = genres.take(MAX_GENRES).map { localizedGenreLabel(context, it) }
        )
    }
}

private fun LibraryEntry.isHomeFavoriteMovieOrSeries(): Boolean = type.isHomeFavoriteMovieOrSeries()

// Tracking providers may call series "tv" or "anime"; Home groups those in its Series row.
private fun String.isHomeFavoriteMovieOrSeries(): Boolean =
    equals("movie", ignoreCase = true) ||
        equals("series", ignoreCase = true) ||
        equals("tv", ignoreCase = true) ||
        equals("anime", ignoreCase = true)

private fun LiveTvVodItem.toAmbientFavoritePreview(): MetaPreview = MetaPreview(
    id = HOME_IPTV_FAVORITE_ITEM_ID_PREFIX + Uri.encode(favoriteKey),
    type = when {
        type.equals("series", ignoreCase = true) ||
            type.equals("tv", ignoreCase = true) ||
            type.equals("anime", ignoreCase = true) -> ContentType.SERIES
        else -> ContentType.MOVIE
    },
    rawType = type,
    name = title,
    poster = posterUrl.takeIf(String::isNotBlank),
    posterShape = PosterShape.POSTER,
    background = null,
    logo = null,
    description = plot.takeIf(String::isNotBlank),
    releaseInfo = year.takeIf(String::isNotBlank),
    imdbRating = null,
    genres = emptyList(),
    rawPosterUrl = posterUrl.takeIf(String::isNotBlank)
)
