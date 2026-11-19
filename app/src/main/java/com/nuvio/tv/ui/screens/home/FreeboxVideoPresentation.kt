package com.nuvio.tv.ui.screens.home

import com.nuvio.tv.core.tmdb.TmdbEnrichment
import com.nuvio.tv.core.tmdb.TmdbExactTitleMatch
import com.nuvio.tv.data.freebox.FreeboxFolderArtwork
import com.nuvio.tv.data.freebox.FreeboxVideoMetadata
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.MetaPreview

internal fun buildFreeboxLocalPreview(
    base: MetaPreview,
    fileMetadata: FreeboxVideoMetadata?,
    fallbackTitle: String?,
    yearHint: Int?,
    folderDurationMinutes: Long? = null
): MetaPreview = base.copy(
    name = fileMetadata?.title?.takeIf(String::isNotBlank)
        ?: fallbackTitle?.takeIf(String::isNotBlank)
        ?: base.name,
    description = null,
    releaseInfo = (fileMetadata?.year ?: yearHint)?.toString() ?: base.releaseInfo,
    genres = fileMetadata?.genres?.takeIf { it.isNotEmpty() } ?: base.genres,
    runtime = (fileMetadata?.durationMinutes ?: folderDurationMinutes)?.toString() ?: base.runtime,
    castMembers = emptyList(),
    director = emptyList(),
    writer = emptyList(),
    tmdbId = null,
    tmdbMediaType = null
)

internal fun mergeFreeboxVideoEnrichment(
    local: MetaPreview,
    fileMetadata: FreeboxVideoMetadata?,
    yearHint: Int?,
    folderDurationMinutes: Long?,
    matchedTitle: String?,
    match: TmdbExactTitleMatch?,
    enrichment: TmdbEnrichment?,
    artwork: FreeboxFolderArtwork?,
    useBasicInfo: Boolean,
    useDetails: Boolean,
    useArtwork: Boolean,
    usedSavedArtworkMatch: Boolean
): MetaPreview {
    val matchedType = match?.contentType
    val resolvedType = matchedType ?: local.type
    val isSeries = resolvedType == ContentType.SERIES || resolvedType == ContentType.TV
    val hasTmdbSeriesDetails = isSeries && useDetails && enrichment?.hasTmdbSeriesDetails == true
    val name = matchedTitle?.let { title ->
        if (useBasicInfo) enrichment?.localizedTitle?.takeIf(String::isNotBlank) ?: title else title
    } ?: local.name
    val year = fileMetadata?.year ?: yearHint
    val duration = fileMetadata?.durationMinutes ?: folderDurationMinutes

    return local.copy(
        type = matchedType ?: local.type,
        rawType = matchedType?.toApiString() ?: local.rawType,
        name = name,
        // Only a match to this exact file may supply a synopsis. Null deliberately clears any
        // previous item's synopsis while metadata for this item is unavailable.
        description = if (match != null && (useBasicInfo || usedSavedArtworkMatch)) enrichment?.description else null,
        releaseInfo = year?.toString()
            ?: if (match != null && useBasicInfo) enrichment?.releaseInfo else local.releaseInfo,
        genres = fileMetadata?.genres?.takeIf { it.isNotEmpty() }
            ?: if (match != null && useBasicInfo) enrichment?.genres.orEmpty() else local.genres,
        runtime = duration?.toString()
            ?: if (match != null && useDetails) enrichment?.runtimeMinutes?.toString() else local.runtime,
        status = if (isSeries) enrichment?.status.takeIf { hasTmdbSeriesDetails }
        else if (useDetails && match != null) enrichment?.status else local.status,
        seasonCount = if (isSeries) enrichment?.seasonCount.takeIf { hasTmdbSeriesDetails }
        else local.seasonCount,
        poster = if (useArtwork) artwork?.portrait ?: enrichment?.poster ?: local.poster else local.poster,
        background = if (useArtwork) artwork?.landscape ?: enrichment?.backdrop ?: local.background else local.background,
        logo = if (useArtwork) enrichment?.logo ?: local.logo else local.logo,
        director = if (useBasicInfo && match != null) enrichment?.director.orEmpty() else local.director,
        writer = if (useBasicInfo && match != null) enrichment?.writer.orEmpty() else local.writer,
        castMembers = if (useBasicInfo && match != null) enrichment?.castMembers.orEmpty() else local.castMembers,
        tmdbId = match?.tmdbId?.toString(),
        tmdbMediaType = matchedType,
        hasTmdbSeriesDetails = hasTmdbSeriesDetails
    )
}
