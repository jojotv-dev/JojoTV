package com.nuvio.tv.ui.screens.freebox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.nuvio.tv.core.tmdb.TmdbArtworkCandidate
import com.nuvio.tv.core.tmdb.TmdbEnrichment
import com.nuvio.tv.core.tmdb.TmdbEpisodeEnrichment
import com.nuvio.tv.core.tmdb.TmdbMetadataService
import com.nuvio.tv.core.tmdb.TmdbService
import com.nuvio.tv.core.tmdb.TmdbExactTitleMatch
import com.nuvio.tv.data.freebox.FREEBOX_FOLDER_COVER_URI
import com.nuvio.tv.data.freebox.FREEBOX_FOLDER_ID_PREFIX
import com.nuvio.tv.data.freebox.FreeboxFolderArtwork
import com.nuvio.tv.data.freebox.FreeboxRepository
import com.nuvio.tv.data.freebox.clearReplacedFreeboxProgress
import com.nuvio.tv.data.freebox.freeboxContentIdFor
import com.nuvio.tv.data.freebox.freeboxPathFromContentId
import com.nuvio.tv.data.local.TmdbSettingsDataStore
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.PosterShape
import com.nuvio.tv.domain.model.WatchProgress
import com.nuvio.tv.domain.repository.WatchProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.text.Normalizer
import java.util.Locale

private val NUMBER_ONLY_VIDEO_TITLE = Regex(
    """^(?:(?:episode|épisode|ep)[\s._-]*)?\d{1,4}$""",
    RegexOption.IGNORE_CASE,
)
private val FREEBOX_SEASON_FOLDER_NAME = Regex(
    """^(?:season|saison|s)[\s._-]*0*(\d+)(?:$|[\s._-].*)$""",
    RegexOption.IGNORE_CASE,
)

private fun MetaPreview.isNumberedFreeboxVideo(): Boolean =
    id.startsWith("freebox:") && NUMBER_ONLY_VIDEO_TITLE.matches(name.trim())

private fun String.lastFreeboxPathSegment(): String =
    trim().trimEnd('/', '\\').substringAfterLast('/').substringAfterLast('\\').trim()

private fun String.freeboxSeasonFolderNumber(): Int? =
    FREEBOX_SEASON_FOLDER_NAME.find(trim())?.groupValues?.getOrNull(1)?.toIntOrNull()?.takeIf { it > 0 }

data class FreeboxFolderUiState(
    val path: String = "",
    val items: List<MetaPreview> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val folderPresentation: MetaPreview? = null,
    val folderMetadata: TmdbEnrichment? = null,
    val folderTmdbId: Int? = null,
    val folderContentType: ContentType? = null,
    val episodeNumbersByItemId: Map<String, Pair<Int, Int>> = emptyMap(),
    val episodeMetadataByItemId: Map<String, TmdbEpisodeEnrichment> = emptyMap(),
    val manualArtworkByItemId: Map<String, FreeboxFolderArtwork> = emptyMap(),
)

@HiltViewModel
class FreeboxFolderViewModel @Inject constructor(
    private val freeboxRepository: FreeboxRepository,
    private val tmdbService: TmdbService,
    private val tmdbMetadataService: TmdbMetadataService,
    private val tmdbSettingsDataStore: TmdbSettingsDataStore,
    private val watchProgressRepository: WatchProgressRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(FreeboxFolderUiState())
    val uiState = mutableUiState.asStateFlow()
    private var loadJob: Job? = null
    private var folderPresentationJob: Job? = null
    private var loadedMediaType: String = "movie"

    /** Builds the rich file presentation used by the player without changing the folder grid. */
    fun playerPreview(item: MetaPreview): MetaPreview {
        val current = mutableUiState.value
        val cached = freeboxRepository.homeVideoPresentation(item.id)
            ?.takeIf { preview ->
                (item.sourceFileSizeBytes == null || item.sourceFileSizeBytes == preview.sourceFileSizeBytes) &&
                    (item.sourceFileLastModifiedMs == null ||
                        item.sourceFileLastModifiedMs == preview.sourceFileLastModifiedMs) &&
                    (item.sourceFileId == null || item.sourceFileId == preview.sourceFileId)
            }
        val base = cached ?: item
        val folder = current.folderPresentation
        val metadata = current.folderMetadata
        val episode = current.episodeMetadataByItemId[item.id]
        val contentType = current.folderContentType
            ?.takeIf { it != ContentType.UNKNOWN }
            ?: base.tmdbMediaType?.takeIf { it != ContentType.UNKNOWN }
            ?: base.type
        val isSeriesEpisode = contentType == ContentType.SERIES || contentType == ContentType.TV || episode != null ||
            current.path.lastFreeboxPathSegment().freeboxSeasonFolderNumber() != null
        val hasTmdbSeriesDetails = isSeriesEpisode && (
            metadata?.hasTmdbSeriesDetails == true ||
                base.hasTmdbSeriesDetails ||
                folder?.hasTmdbSeriesDetails == true
            )
        fun usefulDescription(value: String?): String? = value
            ?.takeIf(String::isNotBlank)
            ?.takeUnless { it.startsWith("Freebox ·", ignoreCase = true) }

        val preview = base.copy(
            type = contentType,
            rawType = contentType.toApiString(base.rawType),
            poster = current.manualArtworkByItemId[item.id]?.portrait
                ?: item.poster
                ?: base.poster
                ?: metadata?.poster
                ?: folder?.poster,
            background = current.manualArtworkByItemId[item.id]?.landscape
                ?: item.background
                ?: base.background
                ?: episode?.thumbnail
                ?: metadata?.backdrop
                ?: folder?.backdropUrl,
            logo = base.logo ?: metadata?.logo ?: folder?.logo,
            description = episode?.overview?.takeIf(String::isNotBlank)
                ?: usefulDescription(base.description)
                ?: usefulDescription(metadata?.description)
                ?: usefulDescription(folder?.description),
            releaseInfo = metadata?.releaseInfo?.takeIf(String::isNotBlank)
                ?: base.releaseInfo
                ?: folder?.releaseInfo,
            genres = base.genres.ifEmpty { metadata?.genres.orEmpty().ifEmpty { folder?.genres.orEmpty() } },
            runtime = item.runtime?.takeIf(String::isNotBlank)
                ?: base.runtime?.takeIf(String::isNotBlank)
                ?: episode?.runtimeMinutes?.takeIf { it > 0 }?.toString()
                ?: metadata?.runtimeMinutes?.takeIf { it > 0 && !isSeriesEpisode }?.toString(),
            director = base.director.ifEmpty { metadata?.director.orEmpty().ifEmpty { folder?.director.orEmpty() } },
            writer = base.writer.ifEmpty { metadata?.writer.orEmpty().ifEmpty { folder?.writer.orEmpty() } },
            castMembers = base.castMembers.ifEmpty {
                metadata?.castMembers.orEmpty().ifEmpty { folder?.castMembers.orEmpty() }
            },
            status = if (isSeriesEpisode) {
                metadata?.takeIf { it.hasTmdbSeriesDetails }?.status
                    ?: base.takeIf { it.hasTmdbSeriesDetails }?.status
                    ?: folder?.takeIf { it.hasTmdbSeriesDetails }?.status
            } else {
                base.status ?: metadata?.status ?: folder?.status
            },
            ageRating = base.ageRating ?: metadata?.ageRating ?: folder?.ageRating,
            language = base.language ?: metadata?.language ?: folder?.language,
            country = base.country ?: metadata?.countries?.firstOrNull() ?: folder?.country,
            released = base.released ?: metadata?.releaseInfo ?: folder?.released,
            seasonCount = if (isSeriesEpisode) {
                metadata?.takeIf { it.hasTmdbSeriesDetails }?.seasonCount
                    ?: base.takeIf { it.hasTmdbSeriesDetails }?.seasonCount
                    ?: folder?.takeIf { it.hasTmdbSeriesDetails }?.seasonCount
            } else {
                base.seasonCount ?: metadata?.seasonCount ?: folder?.seasonCount
            },
            imdbRating = base.imdbRating ?: metadata?.rating?.toFloat() ?: folder?.imdbRating,
            tmdbId = base.tmdbId ?: current.folderTmdbId?.toString() ?: folder?.tmdbId,
            tmdbMediaType = current.folderContentType
                ?.takeIf { it != ContentType.UNKNOWN }
                ?: base.tmdbMediaType
                ?: folder?.tmdbMediaType,
            hasTmdbSeriesDetails = hasTmdbSeriesDetails,
        )
        freeboxRepository.rememberHomeVideoPresentation(preview)
        return preview
    }

    fun load(path: String, mediaType: String, force: Boolean = false) {
        val current = mutableUiState.value
        if (!force && current.path == path && (current.isLoading || current.items.isNotEmpty())) return

        loadJob?.cancel()
        folderPresentationJob?.cancel()
        loadedMediaType = mediaType
        val folderId = "$FREEBOX_FOLDER_ID_PREFIX$path"
        val routeContentType = ContentType.fromString(mediaType)
        val initialPresentation = freeboxRepository.homeVideoPresentation(folderId)
        mutableUiState.value = FreeboxFolderUiState(
            path = path,
            isLoading = true,
            folderPresentation = initialPresentation,
            folderTmdbId = initialPresentation?.tmdbId?.toIntOrNull(),
            folderContentType = initialPresentation?.tmdbMediaType
                ?.takeIf { it != ContentType.UNKNOWN }
                ?: routeContentType.takeIf { it != ContentType.UNKNOWN },
        )
        folderPresentationJob = viewModelScope.launch {
            freeboxRepository.observeHomeVideoPresentation(folderId).collect { presentation ->
                mutableUiState.update { state ->
                    if (state.path != path) state
                    else state.copy(
                        folderPresentation = presentation,
                        folderTmdbId = presentation?.tmdbId?.toIntOrNull() ?: state.folderTmdbId,
                        folderContentType = presentation?.tmdbMediaType
                            ?.takeIf { it != ContentType.UNKNOWN }
                            ?: state.folderContentType,
                    )
                }
            }
        }
        loadJob = viewModelScope.launch {
            val entries = try {
                freeboxRepository.listMediaEntries(path)
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (failure: Exception) {
                mutableUiState.update { state ->
                    if (state.path != path) state
                    else state.copy(
                        isLoading = false,
                        error = failure.message?.takeIf(String::isNotBlank)
                            ?: "Lecture du dossier Freebox impossible.",
                    )
                }
                return@launch
            }

            val currentFreeboxContentIds = entries.asSequence()
                .filterNot { it.isDirectory }
                .associate { entry ->
                    entry.path to freeboxContentIdFor(
                        entry.path,
                        entry.sizeBytes,
                        entry.lastModifiedMs,
                        entry.fileId,
                    )
                }
            try {
                watchProgressRepository.clearReplacedFreeboxProgress(currentFreeboxContentIds)
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (error: Exception) {
                Log.w("FreeboxFolder", "Could not remove progress for replaced files", error)
            }

            val contentType = ContentType.fromString(mediaType)
            val preferPathScopedEpisodeArtwork = contentType == ContentType.SERIES ||
                mutableUiState.value.folderContentType == ContentType.SERIES ||
                path.lastFreeboxPathSegment().freeboxSeasonFolderNumber() != null
            val manualArtworkByItemId = entries.mapNotNull { entry ->
                val artwork = freeboxRepository.folderArtwork(
                    entry.path,
                    preferPathScopedVideoArtwork = preferPathScopedEpisodeArtwork && !entry.isDirectory,
                )
                    ?.takeIf { it.portrait != null || it.landscape != null }
                    ?: return@mapNotNull null
                val id = if (entry.isDirectory) "$FREEBOX_FOLDER_ID_PREFIX${entry.path}" else "freebox:${entry.path}"
                id to artwork
            }.toMap()
            val manuallySelectedPortraits = manualArtworkByItemId
                .filterValues { it.portrait != null }
                .keys
            var previews = entries.map { entry ->
                val isFolder = entry.isDirectory
                val name = if (isFolder) entry.name else entry.name.substringBeforeLast('.', entry.name)
                val artwork = freeboxRepository.folderArtwork(
                    entry.path,
                    preferPathScopedVideoArtwork = preferPathScopedEpisodeArtwork && !entry.isDirectory,
                )
                MetaPreview(
                    id = if (isFolder) "$FREEBOX_FOLDER_ID_PREFIX${entry.path}" else "freebox:${entry.path}",
                    type = contentType,
                    rawType = mediaType,
                    name = name,
                    poster = artwork?.portrait ?: FREEBOX_FOLDER_COVER_URI.takeIf { isFolder },
                    posterShape = PosterShape.POSTER,
                    background = artwork?.landscape,
                    logo = null,
                    description = if (isFolder) "Freebox · Dossier" else "Freebox · ${path}",
                    releaseInfo = null,
                    imdbRating = null,
                    genres = emptyList(),
                    sourceAddonBaseUrl = "freebox://nas",
                    sourceFileSizeBytes = entry.sizeBytes.takeIf { it > 0L },
                    sourceFileLastModifiedMs = entry.lastModifiedMs.takeIf { it > 0L },
                    sourceFileId = entry.fileId.takeIf { it != 0L },
                )
            }
            mutableUiState.update { state ->
                if (state.path != path) state
                else state.copy(
                    items = previews,
                    isLoading = false,
                    error = null,
                    manualArtworkByItemId = manualArtworkByItemId,
                )
            }
            if (previews.isEmpty()) return@launch

            if (entries.none { it.isDirectory }) {
                val entryByItemId = entries.associateBy { "freebox:${it.path}" }
                val metadataReadSemaphore = Semaphore(3)
                val embeddedMetadataByItemId = coroutineScope {
                    previews.map { item ->
                        async {
                            val entry = entryByItemId[item.id] ?: return@async null
                            val embeddedMetadata = metadataReadSemaphore.withPermit {
                                freeboxRepository.videoMetadata(entry.path, entry.sizeBytes)
                            }?.takeIf { !it.title.isNullOrBlank() || (it.durationMinutes ?: 0L) > 0L }
                            embeddedMetadata?.let { item.id to it }
                        }
                    }.awaitAll().filterNotNull().toMap()
                }
                if (embeddedMetadataByItemId.isNotEmpty()) {
                    previews = previews.map { item ->
                        val embeddedMetadata = embeddedMetadataByItemId[item.id] ?: return@map item
                        item.copy(
                            name = embeddedMetadata.title?.takeIf(String::isNotBlank) ?: item.name,
                            runtime = embeddedMetadata.durationMinutes
                                ?.takeIf { it > 0L }
                                ?.toString()
                                ?: item.runtime,
                        )
                    }
                    mutableUiState.update { current ->
                        if (current.path == path) current.copy(items = previews) else current
                    }
                }
            }

            val settings = tmdbSettingsDataStore.settings.first()
            if (!settings.enabled) return@launch

            val folderTitle = path.lastFreeboxPathSegment()
            val isFlatVideoFolder = previews.none { it.id.startsWith(FREEBOX_FOLDER_ID_PREFIX) }
            if (isFlatVideoFolder && folderTitle.isNotBlank()) {
                val seasonFolderNumber = folderTitle.freeboxSeasonFolderNumber()
                val seriesFolderPath = if (seasonFolderNumber != null) {
                    path.replace('\\', '/').trimEnd('/').substringBeforeLast('/', missingDelimiterValue = "")
                        .takeIf(String::isNotBlank)
                } else null
                val savedArtwork = listOfNotNull(seriesFolderPath, path).asSequence()
                    .distinct()
                    .mapNotNull { candidatePath ->
                        freeboxRepository.folderArtwork(candidatePath)?.takeIf { it.tmdbId != null }
                    }
                    .firstOrNull()
                val artworkForMatch = savedArtwork?.takeIf { it.tmdbId != null }
                    ?: previews.firstNotNullOfOrNull { preview ->
                        val previewPath = when {
                            preview.id.startsWith(FREEBOX_FOLDER_ID_PREFIX) -> preview.id.removePrefix(FREEBOX_FOLDER_ID_PREFIX)
                            preview.id.startsWith("freebox:", ignoreCase = true) -> preview.id.substringAfter(':')
                            else -> return@firstNotNullOfOrNull null
                        }
                        freeboxRepository.folderArtwork(previewPath)?.takeIf { it.tmdbId != null }
                    }
                val savedMatch = artworkForMatch?.tmdbId?.let { tmdbId ->
                    val contentType = ContentType.fromString(artworkForMatch.tmdbContentType.orEmpty())
                    if (contentType == ContentType.UNKNOWN) null
                    else TmdbExactTitleMatch(tmdbId = tmdbId, contentType = contentType)
                }
                val presentationMatch = mutableUiState.value.folderPresentation?.let { presentation ->
                    val tmdbId = presentation.tmdbId?.toIntOrNull()
                    val contentType = presentation.tmdbMediaType ?: presentation.type
                    if (tmdbId == null || contentType == ContentType.UNKNOWN) null
                    else TmdbExactTitleMatch(tmdbId = tmdbId, contentType = contentType)
                }
                val titleForMatch = seriesFolderPath?.lastFreeboxPathSegment()?.takeIf(String::isNotBlank)
                    ?: folderTitle.takeIf { seasonFolderNumber == null }
                val preferredMediaType = when (ContentType.fromString(mediaType)) {
                    ContentType.MOVIE -> "movie"
                    ContentType.SERIES -> "series"
                    else -> "series"
                }
                val fallbackMediaType = if (preferredMediaType == "series") "movie" else "series"
                val match = savedMatch ?: presentationMatch ?: titleForMatch?.let { title ->
                    tmdbService.findExactTitleMatch(
                        title = title,
                        mediaType = preferredMediaType,
                        language = settings.language,
                    ) ?: tmdbService.findExactTitleMatch(
                        title = title,
                        mediaType = fallbackMediaType,
                        language = settings.language,
                    )
                }
                val folderMetadata = match?.let {
                    tmdbMetadataService.fetchEnrichment(
                        tmdbId = it.tmdbId.toString(),
                        contentType = it.contentType,
                        language = settings.language,
                    )
                }
                val parsedEpisodeNumbers = if (match?.contentType == ContentType.SERIES) {
                    previews.mapNotNull { item ->
                        parseFreeboxEpisodeNumber(item.name, folderTitle)?.let { item.id to it }
                    }.toMap()
                } else emptyMap()
                val seasonNumbers = buildSet {
                    parsedEpisodeNumbers.values.forEach { (season, _) -> add(season) }
                    seasonFolderNumber?.let { add(it) }
                }.toList()
                val tmdbEpisodes = if (match?.contentType == ContentType.SERIES && seasonNumbers.isNotEmpty()) {
                    tmdbMetadataService.fetchEpisodeEnrichment(
                        tmdbId = match.tmdbId.toString(),
                        seasonNumbers = seasonNumbers,
                        language = settings.language,
                    )
                } else emptyMap()
                val resolvedEpisodeNumbers = parsedEpisodeNumbers.toMutableMap()
                if (seasonFolderNumber != null) {
                    val seriesTitleForEpisodeMatching = seriesFolderPath?.lastFreeboxPathSegment().orEmpty()
                    val tmdbEpisodesByTitle = tmdbEpisodes.entries
                        .asSequence()
                        .filter { (seasonEpisode, _) -> seasonEpisode.first == seasonFolderNumber }
                        .mapNotNull { (seasonEpisode, metadata) ->
                            val normalizedTitle = normalizeFreeboxEpisodeTitle(
                                title = metadata.title.orEmpty(),
                                seriesTitle = seriesTitleForEpisodeMatching,
                            )
                            normalizedTitle.takeIf(String::isNotBlank)?.let { it to seasonEpisode }
                        }
                        .groupBy({ it.first }, { it.second })

                    previews.forEach { item ->
                        if (item.id in resolvedEpisodeNumbers) return@forEach
                        val normalizedTitle = normalizeFreeboxEpisodeTitle(
                            title = item.name,
                            seriesTitle = seriesTitleForEpisodeMatching,
                        )
                        val matchingEpisode = tmdbEpisodesByTitle[normalizedTitle]?.singleOrNull()
                        if (matchingEpisode != null) resolvedEpisodeNumbers[item.id] = matchingEpisode
                    }
                }
                val episodeMetadataByItemId = resolvedEpisodeNumbers.mapNotNull { (itemId, seasonEpisode) ->
                    tmdbEpisodes[seasonEpisode]?.let { itemId to it }
                }.toMap()
                mutableUiState.update { current ->
                    if (current.path != path) current
                    else current.copy(
                        folderMetadata = folderMetadata,
                        folderTmdbId = match?.tmdbId,
                        folderContentType = match?.contentType,
                        episodeNumbersByItemId = resolvedEpisodeNumbers,
                        episodeMetadataByItemId = episodeMetadataByItemId,
                    )
                }
                if (mutableUiState.value.path == path &&
                    (folderMetadata != null || episodeMetadataByItemId.isNotEmpty())
                ) {
                    previews.filterNot { it.id.startsWith(FREEBOX_FOLDER_ID_PREFIX) }
                        .forEach { playerPreview(it) }
                }
            }

            if (!settings.useArtwork) return@launch

            val numberedVideos = previews.filter(MetaPreview::isNumberedFreeboxVideo)
            var inheritedPoster = if (numberedVideos.isNotEmpty()) {
                folderTitle.takeIf(String::isNotBlank)?.let {
                    tmdbService.posterForExactTitle(
                        title = it,
                        mediaType = mediaType,
                        language = settings.language,
                    )
                }
            } else {
                null
            }

            fun applyPoster(itemIds: Set<String>, poster: String) {
                mutableUiState.update { state ->
                    if (state.path != path) state
                    else state.copy(items = state.items.map { currentItem ->
                        if (currentItem.id in itemIds && currentItem.id !in manuallySelectedPortraits) {
                            currentItem.copy(poster = poster)
                        } else currentItem
                    })
                }
            }

            inheritedPoster?.takeIf(String::isNotBlank)?.let { poster ->
                applyPoster(numberedVideos.mapTo(mutableSetOf(), MetaPreview::id), poster)
            }

            var siblingPoster: String? = null
            for (item in previews
                .filterNot(MetaPreview::isNumberedFreeboxVideo)
                .take(24)) {
                val poster = tmdbService.posterForExactTitle(
                    title = item.name,
                    mediaType = mediaType,
                    language = settings.language,
                ) ?: continue
                if (siblingPoster == null && !item.id.startsWith(FREEBOX_FOLDER_ID_PREFIX)) {
                    siblingPoster = poster
                }
                applyPoster(setOf(item.id), poster)
            }

            if (inheritedPoster.isNullOrBlank()) {
                inheritedPoster = siblingPoster
                inheritedPoster?.takeIf(String::isNotBlank)?.let { poster ->
                    if (numberedVideos.isNotEmpty()) {
                        applyPoster(numberedVideos.mapTo(mutableSetOf(), MetaPreview::id), poster)
                    }
                }
            }
        }
    }

    suspend fun artworkCandidates(item: MetaPreview, portrait: Boolean): List<TmdbArtworkCandidate> {
        val settings = tmdbSettingsDataStore.settings.first()
        val itemPath = item.freeboxRelativePath()
        val parentTitle = itemPath.substringBeforeLast('/').substringAfterLast('/')
        val current = mutableUiState.value
        val isEpisodeSeriesFolder = current.folderContentType == ContentType.SERIES ||
            current.path.lastFreeboxPathSegment().freeboxSeasonFolderNumber() != null
        val seriesTmdbId = current.folderTmdbId
            ?.takeIf { isEpisodeSeriesFolder }
        if (seriesTmdbId != null && !item.id.startsWith(FREEBOX_FOLDER_ID_PREFIX)) {
            val seasonEpisode = current.episodeNumbersByItemId[item.id]
            val seriesMatch = TmdbExactTitleMatch(
                tmdbId = seriesTmdbId,
                contentType = ContentType.SERIES,
                title = current.folderMetadata?.localizedTitle?.takeIf(String::isNotBlank)
                    ?: current.folderPresentation?.name?.takeIf(String::isNotBlank)
                    ?: parentTitle,
                releaseYear = current.folderMetadata?.releaseInfo?.take(4)?.toIntOrNull(),
            )
            val episodeStills = if (!portrait && seasonEpisode != null) {
                tmdbService.artworkCandidatesForTvEpisode(
                    tmdbId = seriesTmdbId,
                    seasonNumber = seasonEpisode.first,
                    episodeNumber = seasonEpisode.second,
                    language = settings.language,
                )
            } else emptyList()
            val seriesArtwork = tmdbService.artworkCandidatesForContentId(
                contentId = seriesTmdbId.toString(),
                mediaType = "tv",
                portrait = portrait,
                language = settings.language,
            )
            val episodeArtworkCandidates = (episodeStills + seriesArtwork).distinct().take(20)
            if (episodeArtworkCandidates.isNotEmpty()) {
                return episodeArtworkCandidates.map { imageUrl ->
                    TmdbArtworkCandidate(imageUrl = imageUrl, match = seriesMatch)
                }
            }
        }

        val isNumberedVideo = NUMBER_ONLY_VIDEO_TITLE.matches(item.name.trim())
        val pathParts = itemPath.replace('\\', '/').split('/').filter(String::isNotBlank)
        val seriesFolderTitle = if (parentTitle.freeboxSeasonFolderNumber() != null) {
            pathParts.dropLast(2).lastOrNull()
        } else null
        val searchTitle = when {
            isNumberedVideo && parentTitle.isNotBlank() -> parentTitle
            !seriesFolderTitle.isNullOrBlank() -> seriesFolderTitle
            else -> item.name
        }
        return tmdbService.artworkCandidatesForExactTitleWithMatches(
            title = searchTitle,
            mediaType = if (item.id.startsWith(FREEBOX_FOLDER_ID_PREFIX)) "tv"
                else item.apiType.ifBlank { loadedMediaType },
            portrait = portrait,
            language = settings.language,
            year = item.releaseInfo?.take(4)?.toIntOrNull(),
        )
    }

    suspend fun setArtwork(
        item: MetaPreview,
        portrait: Boolean,
        imageUrl: String?,
        candidate: TmdbArtworkCandidate? = null,
    ) {
        val path = item.freeboxRelativePath()
        val currentState = mutableUiState.value
        val isEpisodeInSeriesFolder = currentState.folderContentType == ContentType.SERIES ||
            currentState.path.lastFreeboxPathSegment().freeboxSeasonFolderNumber() != null
        val selectedMatch = candidate
            ?.takeIf { it.imageUrl == imageUrl }
            ?.match
        require(imageUrl.isNullOrBlank() || selectedMatch != null) {
            "La vignette choisie n’est pas associée à une fiche TMDB."
        }
        freeboxRepository.setFolderArtwork(
            relativePath = path,
            portrait = portrait,
            imageUrl = imageUrl,
            tmdbId = selectedMatch?.tmdbId,
            tmdbContentType = selectedMatch?.contentType?.toApiString(),
            matchVideosByName = !isEpisodeInSeriesFolder,
        )
        mutableUiState.update { current ->
            current.copy(items = current.items.map { currentItem ->
                if (currentItem.id != item.id) currentItem
                else if (portrait) {
                    currentItem.copy(
                        poster = imageUrl ?: FREEBOX_FOLDER_COVER_URI.takeIf {
                            currentItem.id.startsWith(FREEBOX_FOLDER_ID_PREFIX)
                        },
                    )
                } else currentItem.copy(background = imageUrl)
            })
        }
        // Rebuild the folder fiche from the persisted TMDB identity as well as its new artwork.
        load(mutableUiState.value.path, loadedMediaType, force = true)
    }

    suspend fun markVideoWatched(item: MetaPreview, watched: Boolean) {
        val progress = item.toFreeboxWatchProgress().copy(contentType = "movie")
        if (watched) watchProgressRepository.markAsCompleted(progress)
        else watchProgressRepository.removeFromHistory(
            contentId = progress.contentId,
            videoId = progress.videoId,
            season = null,
            episode = null,
        )
    }

    suspend fun removeVideoFromContinueWatching(item: MetaPreview) {
        watchProgressRepository.removeProgress(item.toFreeboxWatchProgress().contentId)
    }

    suspend fun deleteVideo(item: MetaPreview): String = withContext(Dispatchers.IO) {
        val path = item.freeboxRelativePath()
        val deletedPath = freeboxRepository.deleteHomeVideo(path)
        val deletedPathKey = normalizeFreeboxPath(deletedPath)
            ?: error("Le chemin supprimé de la Freebox est invalide.")
        val progressItems = try {
            watchProgressRepository.allProgress.first()
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (error: Exception) {
            Log.w("FreeboxFolder", "Video was deleted but its progress snapshot could not be read", error)
            emptyList()
        }
        val synthetic = item.toFreeboxWatchProgress()
        val affected = (progressItems + synthetic).distinctBy {
            listOf(it.contentId, it.videoId, it.season, it.episode).joinToString("|")
        }.filter { progress ->
            sequenceOf(progress.contentId, progress.videoId)
                .mapNotNull { freeboxProgressPath(it, progress.addonBaseUrl) }
                .any { it.equals(deletedPathKey, ignoreCase = true) }
        }
        affected.forEach { progress ->
            try {
                watchProgressRepository.removeProgress(progress.contentId, progress.season, progress.episode)
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (error: Exception) {
                Log.w("FreeboxFolder", "Could not clear progress for ${progress.contentId}", error)
            }
            try {
                watchProgressRepository.removeFromHistory(
                    contentId = progress.contentId,
                    videoId = progress.videoId,
                    season = progress.season,
                    episode = progress.episode,
                )
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (error: Exception) {
                Log.w("FreeboxFolder", "Could not clear history for ${progress.contentId}", error)
            }
        }
        mutableUiState.update { state -> state.copy(items = state.items.filterNot { it.id == item.id }) }
        deletedPath
    }

    private fun MetaPreview.freeboxRelativePath(): String {
        require(id.startsWith("freebox:", ignoreCase = true)) { "Cette entrée n’est pas une vidéo Freebox." }
        return id.substringAfter(':')
    }

    private fun MetaPreview.toFreeboxWatchProgress(): WatchProgress {
        val contentId = freeboxContentIdFor(
            path = freeboxRelativePath(),
            sizeBytes = sourceFileSizeBytes,
            lastModifiedMs = sourceFileLastModifiedMs,
            fileId = sourceFileId,
        )
        return WatchProgress(
            contentId = contentId,
            contentType = apiType.ifBlank { loadedMediaType },
            name = name,
            poster = poster,
            backdrop = background,
            logo = logo,
            videoId = contentId,
            season = null,
            episode = null,
            episodeTitle = null,
            position = 1L,
            duration = 1L,
            lastWatched = System.currentTimeMillis(),
            addonBaseUrl = sourceAddonBaseUrl ?: "freebox://nas",
            progressPercent = 100f,
        )
    }

    private fun freeboxProgressPath(value: String, addonBaseUrl: String?): String? =
        freeboxPathFromContentId(value, addonBaseUrl)

    private fun normalizeFreeboxPath(value: String): String? {
        val parts = value.replace('\\', '/').split('/').filter(String::isNotBlank)
        if (parts.isEmpty() || parts.any { it == "." || it == ".." }) return null
        return parts.joinToString("/")
    }
}

private fun parseFreeboxEpisodeNumber(title: String, folderTitle: String): Pair<Int, Int>? {
    val seasonEpisode = Regex(
        """(?:^|[\s._-])S(\d{1,2})[\s._-]*E(\d{1,3})(?:$|[\s._-])""",
        RegexOption.IGNORE_CASE,
    ).find(title)
    if (seasonEpisode != null) {
        val season = seasonEpisode.groupValues[1].toIntOrNull()
        val episode = seasonEpisode.groupValues[2].toIntOrNull()
        if (season != null && episode != null && season > 0 && episode > 0) return season to episode
    }

    val xEpisode = Regex("""(?:^|\b)(\d{1,2})x(\d{1,3})(?:$|\b)""", RegexOption.IGNORE_CASE).find(title)
    if (xEpisode != null) {
        val season = xEpisode.groupValues[1].toIntOrNull()
        val episode = xEpisode.groupValues[2].toIntOrNull()
        if (season != null && episode != null && season > 0 && episode > 0) return season to episode
    }

    val episodeTitle = if (title.startsWith(folderTitle, ignoreCase = true)) {
        title.substring(folderTitle.length)
    } else {
        title
    }.trim().trimStart('-', '_', '.', ' ')

    // A bare trailing number is ambiguous: Windows commonly adds " 1" to duplicate filenames.
    val episodeNumber = sequenceOf(
        Regex("(?:^|[\\s._-])(?:episode|épisode|ep)[\\s._-]*0*(\\d{1,3})(?=$|[\\s._-])", RegexOption.IGNORE_CASE),
        Regex("^0*(\\d{1,3})[\\s._-]+.+$", RegexOption.IGNORE_CASE),
    ).firstNotNullOfOrNull { pattern -> pattern.find(episodeTitle)?.groupValues?.getOrNull(1)?.toIntOrNull() }
        ?: return null
    val season = folderTitle.freeboxSeasonFolderNumber() ?: 1
    return (season to episodeNumber).takeIf { episodeNumber > 0 }
}

private fun normalizeFreeboxEpisodeTitle(title: String, seriesTitle: String): String {
    var value = title.trim()
    if (seriesTitle.isNotBlank() && value.startsWith(seriesTitle, ignoreCase = true)) {
        value = value.drop(seriesTitle.length).trimStart(' ', '-', '_', '.', ':')
    }
    value = value
        .replace(
            Regex("""(?:^|[\s._-])S\d{1,2}[\s._-]*E\d{1,3}(?:$|[\s._-])""", RegexOption.IGNORE_CASE),
            " ",
        )
        .replace(Regex("""(?:^|\b)\d{1,2}x\d{1,3}(?:$|\b)""", RegexOption.IGNORE_CASE), " ")
        .replace(
            Regex("""^(?:(?:episode|épisode|ep)[\s._-]*)?0*\d{1,3}[\s._-]+""", RegexOption.IGNORE_CASE),
            "",
        )

    return Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .lowercase(Locale.ROOT)
        .replace(Regex("[^a-z0-9]+"), " ")
        .trim()
}
