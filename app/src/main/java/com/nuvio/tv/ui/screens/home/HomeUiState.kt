package com.nuvio.tv.ui.screens.home

import androidx.compose.runtime.Immutable
import android.net.Uri
import android.util.Base64
import com.nuvio.tv.core.apps.InstalledApp
import com.nuvio.tv.core.tracking.TrackingMembershipRemovalConfirmation
import com.nuvio.tv.data.local.StartupAuthNotice
import com.nuvio.tv.domain.model.CatalogRow
import com.nuvio.tv.domain.model.ContinueWatchingCardStyle
import com.nuvio.tv.domain.model.Collection
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.FocusedPosterTrailerPlaybackTarget
import com.nuvio.tv.domain.model.HomeImdbRatingsVisibility
import com.nuvio.tv.domain.model.DetailImdbRatingsVisibility
import com.nuvio.tv.domain.model.HomeLayout
import com.nuvio.tv.domain.model.LibraryListTab
import com.nuvio.tv.domain.model.LibrarySourceMode
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.domain.model.WatchProgress
import java.util.Locale

internal const val HOME_FAVORITES_ADDON_ID = "nuvio:library-favorites"
internal const val HOME_FAVORITE_MOVIES_CATALOG_ID = "nuvio-library-favorite-movies"
internal const val HOME_FAVORITE_SERIES_CATALOG_ID = "nuvio-library-favorite-series"
internal const val HOME_FAVORITE_TV_CATALOG_ID = "nuvio-library-favorite-tv"
internal const val HOME_IPTV_FAVORITE_ITEM_ID_PREFIX = "nuvio-iptv-favorite:"
internal const val HOME_IPTV_LIVE_CHANNEL_ITEM_ID_PREFIX = "nuvio-iptv-live-channel:"

internal fun homeIptvLiveChannelItemId(sourceId: String, channelId: String): String {
    val identity = "$sourceId\u0000$channelId".toByteArray(Charsets.UTF_8)
    val token = Base64.encodeToString(
        identity,
        Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
    )
    return HOME_IPTV_LIVE_CHANNEL_ITEM_ID_PREFIX + token
}

internal fun decodeHomeIptvLiveChannelItemId(itemId: String): Pair<String, String>? {
    val token = itemId.takeIf { it.startsWith(HOME_IPTV_LIVE_CHANNEL_ITEM_ID_PREFIX) }
        ?.removePrefix(HOME_IPTV_LIVE_CHANNEL_ITEM_ID_PREFIX)
        ?.takeIf(String::isNotBlank)
        ?: return null
    val identity = runCatching {
        Base64.decode(token, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            .toString(Charsets.UTF_8)
    }.getOrNull() ?: return null
    val separator = identity.indexOf('\u0000')
    if (separator <= 0 || separator >= identity.lastIndex) return null
    return identity.substring(0, separator) to identity.substring(separator + 1)
}

@Immutable
data class HomeUiState(
    val catalogRows: List<CatalogRow> = emptyList(),
    val continueWatchingItems: List<ContinueWatchingItem> = emptyList(),
    val upcomingItems: List<ContinueWatchingItem> = emptyList(),
    val continueWatchingHeroArtworkOverrides: Map<String, String> = emptyMap(),
    val isLoading: Boolean = true,
    val layoutPreferencesReady: Boolean = false,
    val error: String? = null,
    val selectedItemId: String? = null,
    val installedAddonsCount: Int = 0,
    val homeLayout: HomeLayout = HomeLayout.MODERN,
    val modernLandscapePostersEnabled: Boolean = false,
    val alwaysShowLandscapeClearlogo: Boolean = false,
    val heroDescriptionAutoScrollEnabled: Boolean = false,
    val modernHeroFullScreenBackdropEnabled: Boolean = false,
    val modernContinueWatchingHeroEnabled: Boolean = false,
    val homeImdbRatingsVisibility: HomeImdbRatingsVisibility = HomeImdbRatingsVisibility.SHOW_ALL,
    val detailImdbRatingsVisibility: DetailImdbRatingsVisibility = DetailImdbRatingsVisibility.SHOW_ALL,
    val heroItems: List<MetaPreview> = emptyList(),
    val heroCatalogKeys: List<String> = emptyList(),
    val heroSectionEnabled: Boolean = true,
    val modernHomePresentation: ModernHomePresentationState = ModernHomePresentationState(),
    val posterLabelsEnabled: Boolean = true,
    val catalogAddonNameEnabled: Boolean = true,
    val catalogTypeSuffixEnabled: Boolean = true,
    val classicFocusGradientEnabled: Boolean = false,
    val focusedPosterBackdropExpandEnabled: Boolean = false,
    val focusedPosterBackdropExpandDelaySeconds: Int = 3,
    val focusedPosterBackdropTrailerEnabled: Boolean = false,
    val focusedPosterBackdropTrailerMuted: Boolean = true,
    val focusedPosterBackdropTrailerPlaybackTarget: FocusedPosterTrailerPlaybackTarget =
        FocusedPosterTrailerPlaybackTarget.HERO_MEDIA,
    val posterCardWidthDp: Int = 126,
    val installedAppTileWidthDp: Int = 180,
    val posterCardHeightDp: Int = 189,
    val posterCardCornerRadiusDp: Int = 12,
    val librarySourceMode: LibrarySourceMode = LibrarySourceMode.LOCAL,
    val libraryListTabs: List<LibraryListTab> = emptyList(),
    val posterLibraryMembership: Map<String, Boolean> = emptyMap(),
    val movieWatchedStatus: Map<String, Boolean> = emptyMap(),
    val posterLibraryPending: Set<String> = emptySet(),
    val movieWatchedPending: Set<String> = emptySet(),
    val showPosterListPicker: Boolean = false,
    val posterListPickerTitle: String? = null,
    val posterListPickerContentType: String? = null,
    val posterListPickerMembership: Map<String, Boolean> = emptyMap(),
    val posterListPickerPending: Boolean = false,
    val posterListPickerError: String? = null,
    val posterListPickerRemovalConfirmations: List<TrackingMembershipRemovalConfirmation> = emptyList(),
    val gridItems: List<GridItem> = emptyList(),
    val hideUnreleasedContent: Boolean = false,
    val showFullReleaseDate: Boolean = true,
    val blurUnwatchedEpisodes: Boolean = false,
    val useEpisodeThumbnailsInCw: Boolean = true,
    val continueWatchingEnabled: Boolean = true,
    val continueWatchingCardStyle: ContinueWatchingCardStyle = ContinueWatchingCardStyle.CARD,
    val heroEnrichmentEnabled: Boolean = false,
    val startupAuthNotice: StartupAuthNotice? = null,
    val homeRows: List<HomeRow> = emptyList(),
    val customPosterUrlPattern: String = "",
    val customPosterEnabledScreens: Set<com.nuvio.tv.core.poster.CustomPosterScreen> =
        com.nuvio.tv.core.poster.CustomPosterScreen.ALL,
    val mdbListShowOnHero: Boolean = false,
    val mdbListRatingOrder: List<String> = com.nuvio.tv.domain.model.MDBListSettings.DEFAULT_RATING_ORDER
)

@Immutable
sealed class ContinueWatchingItem {
    abstract val shufflePlayback: Boolean
    @Immutable
    data class InProgress(
        val progress: WatchProgress,
        val episodeDescription: String? = null,
        val seriesDescription: String? = null,
        val episodeThumbnail: String? = null,
        val episodeImdbRating: Float? = null,
        val genres: List<String> = emptyList(),
        val releaseInfo: String? = null,
        val contentLanguage: String? = null,
        override val shufflePlayback: Boolean = false,
        val originalPoster: String? = null,
        val customLandscapePoster: String? = null,
        val mdbListRatings: com.nuvio.tv.domain.model.MDBListRatings? = null,
        val homeSourcePreview: MetaPreview? = null,
        val resolvedTmdbId: String? = null
    ) : ContinueWatchingItem()

    @Immutable
    data class NextUp(
        val info: NextUpInfo,
        override val shufflePlayback: Boolean = false,
        val originalPoster: String? = null,
        val customLandscapePoster: String? = null
    ) : ContinueWatchingItem()
}

private fun continueWatchingIdentityAliases(value: String?): Sequence<String> {
    val normalized = value?.trim()?.takeIf(String::isNotBlank)?.lowercase(Locale.ROOT)
        ?: return emptySequence()
    return when {
        normalized.startsWith("tmdb:") -> sequenceOf(normalized, normalized.removePrefix("tmdb:"))
        normalized.startsWith("imdb:") -> sequenceOf(normalized, normalized.removePrefix("imdb:"))
        else -> sequenceOf(normalized)
    }
}

private val CONTINUE_WATCHING_YEAR_PATTERN = Regex("(?<!\\d)(?:19|20)\\d{2}(?!\\d)")
private val CONTINUE_WATCHING_TITLE_SEPARATORS = Regex("[^\\p{L}\\p{N}]+")

private fun continueWatchingMediaType(contentType: String?): String? =
    when (contentType?.trim()?.lowercase(Locale.ROOT)) {
        "movie", "film" -> "movie"
        "series", "tv" -> "series"
        else -> null
    }

private fun continueWatchingYear(name: String?, releaseInfo: String?): String? =
    CONTINUE_WATCHING_YEAR_PATTERN.find(releaseInfo.orEmpty())?.value
        ?: CONTINUE_WATCHING_YEAR_PATTERN.find(name.orEmpty())?.value

private fun continueWatchingNormalizedTitle(name: String): String? {
    val rawName = name.trim().takeIf(String::isNotBlank) ?: return null
    val year = CONTINUE_WATCHING_YEAR_PATTERN.find(rawName)?.value
    val nameWithoutTrailingYear = if (year == null) rawName else rawName.replace(
        Regex("\\s*\\(?$year\\)?\\s*$"),
        ""
    )
    val normalizedTitle = nameWithoutTrailingYear
        .lowercase(Locale.ROOT)
        .replace(CONTINUE_WATCHING_TITLE_SEPARATORS, " ")
        .trim()
        .takeIf(String::isNotBlank) ?: return null
    return normalizedTitle
}

private fun continueWatchingTitleAlias(name: String?, contentType: String?): String? {
    val mediaType = continueWatchingMediaType(contentType) ?: return null
    val normalizedName = name?.let(::continueWatchingNormalizedTitle) ?: return null
    return "title:$mediaType:$normalizedName"
}

private fun MetaPreview.continueWatchingMediaType(): String? = when {
    type == ContentType.MOVIE || apiType.equals("movie", ignoreCase = true) -> "movie"
    type == ContentType.SERIES || type == ContentType.TV ||
        apiType.equals("series", ignoreCase = true) || apiType.equals("tv", ignoreCase = true) -> "series"
    else -> null
}

// Keep a series badge after an episode completes while its next episode is in the same season.
private fun ContinueWatchingItem.NextUp.belongsToUnfinishedSeason(): Boolean {
    val nextUp = info
    return continueWatchingMediaType(nextUp.contentType) == "series" &&
        nextUp.seedSeason != null &&
        nextUp.seedEpisode != null &&
        nextUp.seedSeason == nextUp.season &&
        !nextUp.isNewSeasonRelease
}

private fun continueWatchingTitleYearAlias(
    name: String?,
    releaseInfo: String?,
    contentType: String?
): String? {
    val mediaType = continueWatchingMediaType(contentType) ?: return null
    val year = continueWatchingYear(name, releaseInfo) ?: return null
    val normalizedName = name?.let(::continueWatchingNormalizedTitle) ?: return null
    return "title-year:$mediaType:$year:$normalizedName"
}

internal fun MetaPreview.continueWatchingIdentityKeys(): Sequence<String> {
    // IPTV VOD favorites use a UI-only encoded ID (`nuvio-iptv-favorite:<favoriteKey>`),
    // while playback progress is stored under the source VOD ID itself.
    val iptvFavoriteKey = id
        .takeIf { it.startsWith(HOME_IPTV_FAVORITE_ITEM_ID_PREFIX) }
        ?.removePrefix(HOME_IPTV_FAVORITE_ITEM_ID_PREFIX)
        ?.let { Uri.decode(it) }

    val mediaType = continueWatchingMediaType()
    val titleYearAlias = continueWatchingTitleYearAlias(
        name = name,
        releaseInfo = releaseInfo,
        contentType = mediaType
    )
    return sequenceOf(id, tmdbId, tmdbId?.let { "tmdb:$it" }, imdbId, iptvFavoriteKey, titleYearAlias)
        .flatMap(::continueWatchingIdentityAliases)
}

internal fun continueWatchingMatchKeys(
    items: List<ContinueWatchingItem>,
    catalogItems: List<MetaPreview> = emptyList(),
    upcomingItems: List<ContinueWatchingItem> = emptyList()
): Set<String> {
    val allContinueWatchingItems = items + upcomingItems
    val matchKeys = allContinueWatchingItems.asSequence()
        .flatMap { continueWatchingItem ->
            when (continueWatchingItem) {
                is ContinueWatchingItem.InProgress -> {
                    if (!continueWatchingItem.progress.isInProgress()) {
                        emptySequence()
                    } else {
                        val sourcePreview = continueWatchingItem.homeSourcePreview
                        sequenceOf(
                            continueWatchingItem.progress.contentId,
                            continueWatchingItem.progress.videoId,
                            sourcePreview?.id,
                            sourcePreview?.tmdbId,
                            sourcePreview?.imdbId,
                            continueWatchingTitleYearAlias(
                                name = continueWatchingItem.progress.name,
                                releaseInfo = continueWatchingItem.releaseInfo ?: sourcePreview?.releaseInfo,
                                contentType = continueWatchingItem.progress.contentType
                            ),
                        ).plus(sourcePreview?.continueWatchingIdentityKeys() ?: emptySequence())
                    }
                }

                is ContinueWatchingItem.NextUp -> {
                    if (!continueWatchingItem.belongsToUnfinishedSeason()) {
                        emptySequence()
                    } else {
                        val nextUp = continueWatchingItem.info
                        sequenceOf(
                            nextUp.contentId,
                            nextUp.videoId,
                            continueWatchingTitleYearAlias(
                                name = nextUp.name,
                                releaseInfo = nextUp.releaseInfo,
                                contentType = nextUp.contentType
                            )
                        )
                    }
                }
            }
        }
        .flatMap(::continueWatchingIdentityAliases)
        .toMutableSet()

    if (catalogItems.isEmpty()) return matchKeys

    val catalogTitleYears = catalogItems.mapNotNull { preview ->
        val titleAlias = continueWatchingTitleAlias(preview.name, preview.continueWatchingMediaType())
            ?: return@mapNotNull null
        titleAlias to continueWatchingYear(preview.name, preview.releaseInfo)
    }.groupBy({ it.first }, { it.second })
        .mapValues { (_, years) -> years.filterNotNull().toSet() }

    allContinueWatchingItems.asSequence()
        .filterIsInstance<ContinueWatchingItem.InProgress>()
        .filter { it.progress.isInProgress() }
        .flatMap { item ->
            val sourcePreview = item.homeSourcePreview
            val progressReleaseInfo = item.releaseInfo ?: sourcePreview?.releaseInfo
            sequenceOf(
                Triple(
                    item.progress.name,
                    continueWatchingYear(item.progress.name, progressReleaseInfo),
                    item.progress.contentType
                ),
                Triple(
                    sourcePreview?.name,
                    continueWatchingYear(
                        sourcePreview?.name,
                        sourcePreview?.releaseInfo ?: progressReleaseInfo
                    ),
                    sourcePreview?.continueWatchingMediaType() ?: item.progress.contentType
                )
            ).mapNotNull { (name, year, contentType) ->
                continueWatchingTitleAlias(name, contentType)?.let { it to year }
            }
        }
        .distinct()
        .forEach { (titleAlias, progressYear) ->
            val catalogYears = catalogTitleYears[titleAlias] ?: return@forEach
            val unambiguousYearMatch = when {
                catalogYears.isEmpty() -> true
                catalogYears.size != 1 -> false
                progressYear == null -> true
                else -> progressYear in catalogYears
            }
            if (unambiguousYearMatch) {
                matchKeys += titleAlias
            }
        }

    if (matchKeys.isEmpty()) return matchKeys

    // One progress ID can identify a card in one catalog while another row carries
    // that same content under its IMDb/TMDB alias. Close over aliases from cards that
    // already match, so all catalog copies receive the same in-progress badge.
    val catalogIdentityKeys = catalogItems
        .map { it.continueWatchingIdentityKeys().toSet() }
        .filter { it.isNotEmpty() }
    var keysChanged: Boolean
    do {
        val previousSize = matchKeys.size
        catalogIdentityKeys
            .asSequence()
            .filter { aliases -> aliases.any(matchKeys::contains) }
            .forEach(matchKeys::addAll)
        keysChanged = matchKeys.size > previousSize
    } while (keysChanged)

    return matchKeys
}

internal fun MetaPreview.isCurrentlyInProgress(matchKeys: Set<String>): Boolean {
    return continueWatchingIdentityKeys().any(matchKeys::contains) ||
        continueWatchingTitleAlias(name, continueWatchingMediaType())?.let(matchKeys::contains) == true
}

val ContinueWatchingItem.shuffleFocusKey: String?
    get() = if (!shufflePlayback) null else when (this) {
        is ContinueWatchingItem.InProgress -> "cw_shuffle_${progress.contentId}"
        is ContinueWatchingItem.NextUp -> "cw_shuffle_${info.contentId}"
    }

@Immutable
data class NextUpInfo(
    val contentId: String,
    val contentType: String,
    val name: String,
    val poster: String?,
    val backdrop: String?,
    val logo: String?,
    val videoId: String,
    val season: Int,
    val episode: Int,
    val episodeTitle: String?,
    val episodeDescription: String? = null,
    val thumbnail: String?,
    val released: String? = null,
    val hasAired: Boolean = true,
    val airDateLabel: String? = null,
    val lastWatched: Long,
    val imdbRating: Float? = null,
    val genres: List<String> = emptyList(),
    val releaseInfo: String? = null,
    val sortTimestamp: Long,
    val releaseTimestamp: Long? = null,
    val isReleaseAlert: Boolean = false,
    val isNewSeasonRelease: Boolean = false,
    val seedSeason: Int? = null,
    val seedEpisode: Int? = null,
    val contentLanguage: String? = null,
    val mdbListRatings: com.nuvio.tv.domain.model.MDBListRatings? = null,
    val fromCalendar: Boolean = false
)

@Immutable
sealed class HomeRow {
    @Immutable
    data object ContinueWatching : HomeRow()

    @Immutable
    data class Catalog(val row: CatalogRow) : HomeRow()

    @Immutable
    data class CollectionRow(val collection: Collection) : HomeRow()

    @Immutable
    data class InstalledApps(val apps: List<InstalledApp>) : HomeRow()

    /**
     * Placeholder for a catalog row whose data hasn't been fetched yet.
     * Rendered as a shimmer/skeleton row until the user scrolls near it
     * and the actual catalog data is loaded on demand.
     */
    @Immutable
    data class PlaceholderCatalog(
        val catalogKey: String,
        val stableCatalogKey: String,
        val addonId: String,
        val addonName: String,
        val addonBaseUrl: String,
        val catalogId: String,
        val catalogName: String,
        val apiType: String,
        val displayTitle: String
    ) : HomeRow()
}

@Immutable
sealed class GridItem {
    @Immutable
    data class Hero(val items: List<MetaPreview>) : GridItem()
    @Immutable
    data object ContinueWatchingRow : GridItem()
    @Immutable
    data class SectionDivider(
        val catalogName: String,
        val catalogId: String,
        val addonBaseUrl: String,
        val addonId: String,
        val type: String
    ) : GridItem()
    @Immutable
    data class Content(
        val item: MetaPreview,
        val addonBaseUrl: String,
        val catalogId: String,
        val catalogName: String,
        /** Row this card belongs to, so the grid can report which row holds focus. */
        val addonId: String = ""
    ) : GridItem()
    @Immutable
    data class SeeAll(
        val catalogId: String,
        val addonId: String,
        val addonBaseUrl: String,
        val type: String
    ) : GridItem()
    @Immutable
    data class CollectionHeader(
        val collectionId: String,
        val title: String
    ) : GridItem()
    @Immutable
    data class InstalledAppsRow(val apps: List<InstalledApp>) : GridItem()

    @Immutable
    data class CollectionFolder(
        val collectionId: String,
        val collectionTitle: String,
        val focusGlowEnabled: Boolean,
        val folder: com.nuvio.tv.domain.model.CollectionFolder
    ) : GridItem()
}

sealed class HomeEvent {
    data class OnItemClick(val itemId: String, val itemType: String) : HomeEvent()
    data class OnLoadMoreCatalog(val catalogId: String, val addonId: String, val type: String) : HomeEvent()
    data class OnRemoveContinueWatching(
        val contentId: String,
        val season: Int? = null,
        val episode: Int? = null,
        val isNextUp: Boolean = false
    ) : HomeEvent()
    data class OnMarkContinueWatchingWatched(val item: ContinueWatchingItem) : HomeEvent()
    data class OnMarkContinueWatchingUnwatched(val item: ContinueWatchingItem.InProgress) : HomeEvent()
    data object OnRetry : HomeEvent()
}

/** Upcoming episodes stay in Next Up but cannot be marked until they air. */
internal fun ContinueWatchingItem.canMarkWatched(): Boolean = when (this) {
    is ContinueWatchingItem.InProgress -> true
    is ContinueWatchingItem.NextUp -> info.hasAired
}

internal fun ContinueWatchingItem.isFreeboxVideo(): Boolean =
    this is ContinueWatchingItem.InProgress && (
        progress.addonBaseUrl.equals("freebox://nas", ignoreCase = true) ||
            progress.contentId.startsWith("freebox:", ignoreCase = true) ||
            progress.videoId.startsWith("freebox:", ignoreCase = true)
        )

/** Same shape as the details screen builds, so tracking sync takes the same path. */
internal fun ContinueWatchingItem.toCompletedProgress(
    nowMs: Long = System.currentTimeMillis()
): WatchProgress = when (this) {
    is ContinueWatchingItem.InProgress -> {
        val duration = progress.duration.coerceAtLeast(1L)
        WatchProgress(
            contentId = progress.contentId,
            contentType = progress.contentType,
            name = progress.name,
            poster = progress.poster,
            backdrop = progress.backdrop,
            logo = progress.logo,
            videoId = progress.videoId,
            season = progress.season,
            episode = progress.episode,
            episodeTitle = progress.episodeTitle,
            position = duration,
            duration = duration,
            lastWatched = nowMs,
            progressPercent = 100f
        )
    }
    is ContinueWatchingItem.NextUp -> WatchProgress(
        contentId = info.contentId,
        contentType = info.contentType,
        name = info.name,
        poster = info.poster,
        backdrop = info.thumbnail ?: info.backdrop,
        logo = info.logo,
        videoId = info.videoId,
        season = info.season,
        episode = info.episode,
        episodeTitle = info.episodeTitle,
        position = 1L,
        duration = 1L,
        lastWatched = nowMs,
        progressPercent = 100f
    )
}

fun homeItemStatusKey(itemId: String, itemType: String): String {
    return "${itemType.lowercase()}|$itemId"
}
