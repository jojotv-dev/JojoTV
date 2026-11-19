@file:OptIn(
    androidx.tv.material3.ExperimentalTvMaterial3Api::class,
    androidx.compose.ui.ExperimentalComposeUiApi::class,
)

package com.nuvio.tv.ui.screens.freebox

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.core.tmdb.TmdbArtworkCandidate
import com.nuvio.tv.data.freebox.FREEBOX_FOLDER_ID_PREFIX
import com.nuvio.tv.domain.model.ContentType
import com.nuvio.tv.domain.model.MetaPreview
import com.nuvio.tv.ui.components.EpisodeBackdropInfo
import com.nuvio.tv.ui.components.EpisodeBackdropLayout
import com.nuvio.tv.ui.components.EpisodeRailItem
import com.nuvio.tv.ui.components.GridContentCard
import com.nuvio.tv.ui.components.FreeboxVideoOptionsDialog
import com.nuvio.tv.ui.components.LoadingIndicator
import com.nuvio.tv.ui.components.PosterCardDefaults
import com.nuvio.tv.ui.theme.NuvioTheme
import com.nuvio.tv.ui.util.dpadRepeatThrottle
import com.nuvio.tv.ui.screens.home.FreeboxVideoDeleteDialog
import com.nuvio.tv.ui.screens.home.HomeArtworkPickerDialog
import kotlinx.coroutines.launch

@Composable
fun FreeboxFolderScreen(
    path: String,
    mediaType: String,
    viewModel: FreeboxFolderViewModel = hiltViewModel(),
    onOpenFolder: (String, String) -> Unit,
    onPlayVideo: (MetaPreview) -> Unit,
    onBack: () -> Unit,
    onPlayVideoFromBeginning: (MetaPreview) -> Unit = onPlayVideo,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val defaultTitle = stringResource(R.string.freebox_folder_title)
    val title = path.substringAfterLast('/').ifBlank { defaultTitle }
    val gridState = rememberLazyGridState()
    val focusRequesters = remember(path) { mutableMapOf<String, FocusRequester>() }
    var lastFocusedItemId by rememberSaveable(path) { mutableStateOf<String?>(null) }
    var videoOptionsItem by remember { mutableStateOf<MetaPreview?>(null) }
    var artworkTarget by remember { mutableStateOf<Pair<MetaPreview, Boolean>?>(null) }
    var artworkCandidates by remember { mutableStateOf<List<TmdbArtworkCandidate>>(emptyList()) }
    var artworkError by remember { mutableStateOf<String?>(null) }
    var isLoadingArtwork by remember { mutableStateOf(false) }
    var isSavingArtwork by remember { mutableStateOf(false) }
    var videoDeleteTarget by remember { mutableStateOf<MetaPreview?>(null) }
    var videoDeleteError by remember { mutableStateOf<String?>(null) }
    var isDeletingVideo by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isEpisodeFolder = remember(state.items) {
        state.items.isNotEmpty() && state.items.none { it.id.startsWith(FREEBOX_FOLDER_ID_PREFIX) }
    }
    var selectedSeason by rememberSaveable(path) { mutableStateOf<Int?>(null) }

    BackHandler(onBack = onBack)
    LaunchedEffect(path, mediaType) { viewModel.load(path, mediaType) }
    LaunchedEffect(path, state.isLoading, state.items.size) {
        if (isEpisodeFolder || state.isLoading || state.items.isEmpty()) return@LaunchedEffect
        val targetId = lastFocusedItemId ?: state.items.first().id
        val targetIndex = state.items.indexOfFirst { it.id == targetId }
        if (targetIndex >= 0 && gridState.layoutInfo.visibleItemsInfo.none { it.index == targetIndex }) {
            gridState.scrollToItem(targetIndex)
        }
        val requester = focusRequesters.getOrPut(targetId) { FocusRequester() }
        repeat(2) { withFrameNanos { } }
        runCatching { requester.requestFocus() }
    }

    if (isEpisodeFolder) {
        val metadata = state.folderMetadata
        val presentation = state.folderPresentation
        val presentationContentType = presentation?.tmdbMediaType ?: presentation?.type
        val seriesMetadata = state.folderContentType == ContentType.SERIES ||
            state.folderContentType == ContentType.TV ||
            presentationContentType == ContentType.SERIES ||
            presentationContentType == ContentType.TV
        val creatorRole = stringResource(R.string.episode_folder_creator)
        val presentationCast = metadata?.castMembers.orEmpty().ifEmpty { presentation?.castMembers.orEmpty() }
        val presentationDirectors = metadata?.director.orEmpty().ifEmpty { presentation?.director.orEmpty() }
        val presentationWriters = metadata?.writer.orEmpty().ifEmpty { presentation?.writer.orEmpty() }
        val presentationDirectorMembers = metadata?.directorMembers.orEmpty()
        val presentationDescription = metadata?.description?.takeIf(String::isNotBlank)
            ?: presentation?.description?.takeIf(String::isNotBlank)
        val presentationGenres = metadata?.genres?.takeIf { it.isNotEmpty() }
            ?: presentation?.genres.orEmpty()
        val seasons = remember(state.episodeNumbersByItemId) {
            state.episodeNumbersByItemId.values.map { it.first }.distinct().sorted()
        }
        val activeSeason = selectedSeason?.takeIf(seasons::contains) ?: seasons.firstOrNull()
        val visibleEpisodeItems = remember(state.items, state.episodeNumbersByItemId, activeSeason, seasons) {
            val seasonItems = if (activeSeason == null) state.items
            else state.items.filter { item ->
                val itemSeason = state.episodeNumbersByItemId[item.id]?.first
                itemSeason == activeSeason || (itemSeason == null && activeSeason == seasons.firstOrNull())
            }
            seasonItems.sortedWith(
                compareBy<MetaPreview>(
                    { state.episodeNumbersByItemId[it.id]?.first ?: Int.MAX_VALUE },
                    { state.episodeNumbersByItemId[it.id]?.second ?: Int.MAX_VALUE },
                    { it.name },
                ),
            )
        }
        val sharedEpisodeArtwork = state.items.firstNotNullOfOrNull { item ->
            item.background?.takeIf(String::isNotBlank)
                ?: item.poster?.takeIf(String::isNotBlank)
        }
        EpisodeBackdropLayout(
            info = EpisodeBackdropInfo(
                title = metadata?.localizedTitle?.takeIf(String::isNotBlank)
                    ?: presentation?.name?.takeIf(String::isNotBlank)
                    ?: title,
                tmdbId = state.folderTmdbId ?: presentation?.tmdbId?.toIntOrNull(),
                logoUrl = metadata?.logo?.takeIf(String::isNotBlank)
                    ?: presentation?.logo?.takeIf(String::isNotBlank),
                backdropUrl = metadata?.backdrop
                    ?: metadata?.poster
                    ?: presentation?.background?.takeIf(String::isNotBlank)
                    ?: presentation?.landscapePoster?.takeIf(String::isNotBlank)
                    ?: presentation?.poster?.takeIf(String::isNotBlank)
                    ?: state.items.firstNotNullOfOrNull(MetaPreview::background)
                    ?: state.items.firstNotNullOfOrNull(MetaPreview::poster),
                genres = presentationGenres,
                year = (metadata?.releaseInfo ?: presentation?.releaseInfo)?.take(4),
                releaseInfo = metadata?.releaseInfo ?: presentation?.releaseInfo,
                runtimeMinutes = metadata?.runtimeMinutes ?: presentation?.runtime?.toIntOrNull(),
                cast = presentationCast.map { it.name },
                director = presentationDirectors,
                creator = presentationDirectors,
                writer = presentationWriters,
                people = presentationDirectorMembers.map { person ->
                    if (person.character.isNullOrBlank()) person.copy(character = creatorRole) else person
                } + presentationCast,
                creatorMembers = (metadata?.directorMembers.orEmpty() + metadata?.writerMembers.orEmpty()).map { person ->
                    if (person.character.isNullOrBlank()) person.copy(character = creatorRole) else person
                },
                castMembers = presentationCast,
                networks = metadata?.networks.orEmpty(),
                productionCompanies = metadata?.productionCompanies.orEmpty(),
                rating = metadata?.rating ?: presentation?.imdbRating?.toDouble(),
                synopsis = presentationDescription,
                seasonCount = metadata?.seasonCount?.takeIf {
                    metadata?.hasTmdbSeriesDetails == true && it > 0
                } ?: presentation?.seasonCount?.takeIf {
                    presentation?.hasTmdbSeriesDetails == true && it > 0
                },
                seriesStatus = null,
                currentSeasonComplete = null,
                hasTmdbSeriesDetails = metadata?.hasTmdbSeriesDetails == true ||
                    presentation?.hasTmdbSeriesDetails == true,
                replaceStatusWithSeasonCount = seriesMetadata,
                nextEpisodeAirDate = metadata?.nextEpisodeAirDate,
                countries = metadata?.countries.orEmpty().ifEmpty { listOfNotNull(presentation?.country) },
                language = metadata?.language ?: presentation?.language,
                ageRating = metadata?.ageRating ?: presentation?.ageRating,
                trailers = metadata?.trailers.orEmpty().ifEmpty { presentation?.trailers.orEmpty() },
            ),
            episodes = visibleEpisodeItems.map { item ->
                val episode = state.episodeMetadataByItemId[item.id]
                val seasonEpisode = state.episodeNumbersByItemId[item.id]
                EpisodeRailItem(
                    id = item.id,
                    title = item.name,
                    subtitle = episode?.title?.takeIf(String::isNotBlank) ?: item.name,
                    imageUrl = state.manualArtworkByItemId[item.id]?.landscape?.takeIf(String::isNotBlank)
                        ?: state.manualArtworkByItemId[item.id]?.portrait?.takeIf(String::isNotBlank)
                        ?: episode?.thumbnail?.takeIf(String::isNotBlank)
                        ?: item.background?.takeIf(String::isNotBlank)
                        ?: item.poster?.takeIf(String::isNotBlank)
                        ?: sharedEpisodeArtwork,
                    airDate = episode?.airDate,
                    overview = episode?.overview,
                    runtimeMinutes = episode?.runtimeMinutes?.takeIf { it > 0 }
                        ?: item.runtime?.toIntOrNull()?.takeIf { it > 0 },
                    season = seasonEpisode?.first,
                    episode = seasonEpisode?.second,
                )
            },
            seasons = seasons,
            selectedSeason = activeSeason,
            onSeasonSelected = { selectedSeason = it },
            onEpisodeClick = { railItem ->
                state.items.firstOrNull { it.id == railItem.id }
                    ?.let(viewModel::playerPreview)
                    ?.let(onPlayVideo)
            },
            onEpisodeLongClick = { railItem ->
                state.items.firstOrNull { it.id == railItem.id }?.let { videoOptionsItem = it }
            },
            fullSeriesDetail = seriesMetadata,
            richEpisodeCards = seriesMetadata,
            showEpisodeCodeBadge = false,
            showLibraryAction = false,
        )
    } else {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Card(
                onClick = onBack,
                modifier = Modifier.size(48.dp),
                colors = CardDefaults.colors(
                    containerColor = NuvioTheme.colors.SurfaceVariant,
                    focusedContainerColor = NuvioTheme.colors.FocusBackground,
                ),
                shape = CardDefaults.shape(shape = NuvioTheme.configuredShape(RoundedCornerShape(12.dp))),
                scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back),
                        tint = NuvioTheme.colors.TextPrimary,
                    )
                }
            }
            Column(Modifier.weight(1f).padding(start = 16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = NuvioTheme.colors.TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(path, style = MaterialTheme.typography.bodySmall, color = NuvioTheme.colors.TextSecondary)
            }
        }

        when {
            state.isLoading && state.items.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
            state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.error.orEmpty(), color = NuvioTheme.colors.TextSecondary)
            }
            state.items.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.freebox_folder_empty), color = NuvioTheme.colors.TextSecondary)
            }
            else -> LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(PosterCardDefaults.Style.width),
                modifier = Modifier.fillMaxSize()
                    .focusRestorer {
                        lastFocusedItemId?.let { focusRequesters[it] } ?: FocusRequester.Default
                    }
                    .dpadRepeatThrottle(),
                contentPadding = PaddingValues(bottom = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                items(state.items, key = MetaPreview::id) { item ->
                    val requester = focusRequesters.getOrPut(item.id) { FocusRequester() }
                    GridContentCard(
                        item = item,
                        focusRequester = requester,
                        onFocused = { lastFocusedItemId = item.id },
                        onLongPress = { if (!item.id.startsWith(FREEBOX_FOLDER_ID_PREFIX)) videoOptionsItem = item },
                        onClick = {
                            if (item.id.startsWith(FREEBOX_FOLDER_ID_PREFIX)) {
                                onOpenFolder(item.id.removePrefix(FREEBOX_FOLDER_ID_PREFIX), mediaType)
                            } else {
                                onPlayVideo(viewModel.playerPreview(item))
                            }
                        },
                    )
                }
            }
        }
    }
    }

    val selectedVideoOptions = videoOptionsItem
    if (selectedVideoOptions != null) {
        FreeboxVideoOptionsDialog(
            title = selectedVideoOptions.name,
            onDismiss = { videoOptionsItem = null },
            onMarkWatched = {
                scope.launch { viewModel.markVideoWatched(selectedVideoOptions, watched = true) }
                videoOptionsItem = null
            },
            onMarkUnwatched = {
                scope.launch { viewModel.markVideoWatched(selectedVideoOptions, watched = false) }
                videoOptionsItem = null
            },
            onStartFromBeginning = {
                onPlayVideoFromBeginning(viewModel.playerPreview(selectedVideoOptions))
                videoOptionsItem = null
            },
            onChangePortraitArtwork = {
                videoOptionsItem = null
                artworkTarget = selectedVideoOptions to true
                artworkCandidates = emptyList()
                artworkError = null
                isLoadingArtwork = true
                scope.launch {
                    try {
                        artworkCandidates = viewModel.artworkCandidates(selectedVideoOptions, portrait = true)
                        if (artworkCandidates.isEmpty()) artworkError = context.getString(R.string.home_freebox_artwork_empty)
                    } catch (error: Exception) {
                        artworkError = error.message ?: context.getString(R.string.home_freebox_artwork_empty)
                    } finally {
                        isLoadingArtwork = false
                    }
                }
            },
            onChangeLandscapeArtwork = {
                videoOptionsItem = null
                artworkTarget = selectedVideoOptions to false
                artworkCandidates = emptyList()
                artworkError = null
                isLoadingArtwork = true
                scope.launch {
                    try {
                        artworkCandidates = viewModel.artworkCandidates(selectedVideoOptions, portrait = false)
                        if (artworkCandidates.isEmpty()) artworkError = context.getString(R.string.home_freebox_artwork_empty)
                    } catch (error: Exception) {
                        artworkError = error.message ?: context.getString(R.string.home_freebox_artwork_empty)
                    } finally {
                        isLoadingArtwork = false
                    }
                }
            },
            onRemoveFromContinueWatching = {
                scope.launch { viewModel.removeVideoFromContinueWatching(selectedVideoOptions) }
                videoOptionsItem = null
            },
            onDeleteFromFreebox = {
                videoOptionsItem = null
                videoDeleteTarget = selectedVideoOptions
                videoDeleteError = null
            },
        )
    }

    val selectedArtwork = artworkTarget
    if (selectedArtwork != null) {
        HomeArtworkPickerDialog(
            portrait = selectedArtwork.second,
            candidates = artworkCandidates.map { it.imageUrl },
            candidateDetails = artworkCandidates,
            isLoading = isLoadingArtwork,
            isBusy = isSavingArtwork,
            error = artworkError,
            onDismiss = { if (!isSavingArtwork) artworkTarget = null },
            onSelectWithDetails = { imageUrl, candidate ->
                isSavingArtwork = true
                artworkError = null
                scope.launch {
                    try {
                        viewModel.setArtwork(
                            selectedArtwork.first,
                            selectedArtwork.second,
                            imageUrl,
                            candidate,
                        )
                        artworkTarget = null
                    } catch (error: Exception) {
                        artworkError = error.message ?: context.getString(R.string.home_freebox_artwork_empty)
                    } finally {
                        isSavingArtwork = false
                    }
                }
            },
            onReset = {
                isSavingArtwork = true
                artworkError = null
                scope.launch {
                    try {
                        viewModel.setArtwork(selectedArtwork.first, selectedArtwork.second, null)
                        artworkTarget = null
                    } catch (error: Exception) {
                        artworkError = error.message ?: context.getString(R.string.home_freebox_artwork_empty)
                    } finally {
                        isSavingArtwork = false
                    }
                }
            },
        )
    }

    val selectedVideoForDeletion = videoDeleteTarget
    if (selectedVideoForDeletion != null) {
        FreeboxVideoDeleteDialog(
            title = selectedVideoForDeletion.name,
            error = videoDeleteError,
            isBusy = isDeletingVideo,
            onDismiss = { if (!isDeletingVideo) videoDeleteTarget = null },
            onConfirm = {
                isDeletingVideo = true
                videoDeleteError = null
                scope.launch {
                    try {
                        viewModel.deleteVideo(selectedVideoForDeletion)
                        videoDeleteTarget = null
                        android.widget.Toast.makeText(
                            context,
                            context.getString(R.string.home_freebox_delete_video_success),
                            android.widget.Toast.LENGTH_SHORT,
                        ).show()
                    } catch (error: Exception) {
                        videoDeleteError = error.message ?: context.getString(R.string.cw_action_delete_from_freebox)
                    } finally {
                        isDeletingVideo = false
                    }
                }
            },
        )
    }
}
