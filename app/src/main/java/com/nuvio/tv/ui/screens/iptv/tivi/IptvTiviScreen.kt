package com.nuvio.tv.ui.screens.iptv.tivi

import androidx.activity.compose.BackHandler
import android.widget.Toast
import android.view.KeyEvent as AndroidKeyEvent
import androidx.compose.animation.animateColorAsState

import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.tv.material3.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import coil3.compose.AsyncImage
import com.nuvio.tv.ui.navigation.Screen
import com.nuvio.tv.ui.screens.iptv.IptvFocusedMediaDetails
import com.nuvio.tv.ui.screens.iptv.IptvFocusedMediaPanel
import com.nuvio.tv.ui.screens.iptv.IptvVodPosterWidth
import com.nuvio.tv.domain.model.IptvPosterSize
import com.nuvio.tv.ui.theme.NuvioColors
import com.nuvio.tv.ui.util.rememberLongPressKeyTracker
import com.nuvio.tv.ui.screens.iptv.tivi.components.*
import com.streamvault.domain.model.Result

private val TabShape = RoundedCornerShape(10.dp)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun IptvTiviScreen(
    onBack: () -> Unit,
    navController: NavController,
    initialTab: TiviTab = TiviTab.LIVE,
    viewModel: IptvTiviViewModel = hiltViewModel(),
    miniPlayerVm: TiviMiniPlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val miniState by miniPlayerVm.state.collectAsState()
    val visibilityDialogState by viewModel.visibilityDialogState.collectAsState()
    DisposableEffect(miniPlayerVm) {
        onDispose { miniPlayerVm.stop() }
    }
    val context = LocalContext.current
    val playbackScope = rememberCoroutineScope()
    val channelListState = rememberLazyListState()
    val epgListState = rememberLazyListState()
    val selectedGroupFocusRequester = remember { FocusRequester() }
    val firstContentFocusRequester = remember { FocusRequester() }
    val focusSelectedGroup = remember(selectedGroupFocusRequester) {
        { runCatching { selectedGroupFocusRequester.requestFocus() }.isSuccess }
    }
    val focusFirstContent = remember(firstContentFocusRequester) {
        { runCatching { firstContentFocusRequester.requestFocus() }.isSuccess }
    }
    LaunchedEffect(initialTab) { viewModel.selectTab(initialTab) }
    LaunchedEffect(channelListState, epgListState) {
        snapshotFlow {
            channelListState.firstVisibleItemIndex to
                channelListState.firstVisibleItemScrollOffset
        }
            .distinctUntilChanged()
            .collect { (index, offset) ->
                epgListState.scrollToItem(index, offset)
            }
    }
    LaunchedEffect(miniState.errorMessage) {
        miniState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
    BackHandler { onBack() }

    visibilityDialogState?.let { state ->
        VisibilityToggleDialog(
            state = state,
            onDismiss = viewModel::dismissVisibilityDialog,
            onChange = viewModel::updateVisibility,
        )
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(NuvioColors.Background),
    ) {

        // -- Colonne 1 : Provider Tree accordeon --------------------------
        TiviProviderTree(
            providerNodes = uiState.providerNodes,
            selectedGroupId = uiState.selectedGroupId,
            selectedProviderId = uiState.selectedProviderId,
            onProviderClick = { viewModel.toggleProvider(it) },
            onProviderFocus = { viewModel.focusProvider(it) },
            onGroupClick = { providerId, groupId -> viewModel.selectGroup(providerId, groupId) },
            onGroupFocus = { providerId, groupId -> viewModel.focusGroup(providerId, groupId) },
            onProviderLongClick = viewModel::openProviderVisibility,
            onGroupLongClick = viewModel::openGroupVisibility,
            selectedGroupFocusRequester = selectedGroupFocusRequester,
            onDirectionRight = focusFirstContent,
            modifier = Modifier.fillMaxHeight(),
        )

        // -- Separateur vertical ------------------------------------------
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(NuvioColors.Border)
        )

        // -- Colonne 2 : Contenu dynamique --------------------------------
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            when (val c = uiState.content) {
                is TiviContent.LiveContent -> {
                    if (uiState.selectedGroupId == null) {
                        TiviEmptyHint("S\u00e9lectionne un groupe Live TV")
                    } else {
                        Column(Modifier.fillMaxSize()) {
                            TiviTopPanel(
                                channel = c.focusedChannel,
                                currentProgram = c.currentProgram,
                                nextProgram = c.nextProgram,
                                exoPlayer = if (miniState.isReady) miniPlayerVm.exoPlayer else null,
                                miniPlayerActive = miniState.isReady,
                                onMiniPlayerClick = {
                                    miniState.channel?.let { ch ->
                                        miniState.streamUrl?.let { url ->
                                            miniPlayerVm.stop()
                                            navController.navigate(
                                                Screen.Player.createRoute(
                                                    streamUrl = url,
                                                    title = ch.name,
                                                    headers = miniState.headers,
                                                    iptvProviderId = ch.providerId,
                                                    iptvChannelId = ch.id,
                                                )
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )











                            Row(Modifier.weight(1f).fillMaxWidth()) {
                                TiviChannelList(
                                    channels = c.channels,
                                    focusedChannelId = c.focusedChannel?.id,
                                    onChannelFocused = { ch -> viewModel.focusChannel(ch.providerId, ch) },
                                    onChannelClick = { channel ->
                                        if (miniState.channel?.id == channel.id && miniState.isReady) {
                                            // 2e clic -> plein ecran
                                            miniPlayerVm.stop()
                                            navController.navigate(
                                                Screen.Player.createRoute(
                                                    streamUrl = miniState.streamUrl ?: channel.streamUrl,
                                                    title = channel.name,
                                                    headers = miniState.headers,
                                                    iptvProviderId = channel.providerId,
                                                    iptvChannelId = channel.id,
                                                )
                                            )
                                        } else {
                                            // 1er clic -> mini lecteur
                                            miniPlayerVm.loadChannel(channel)
                                        }
                                    },
                                    listState = channelListState,
                                    firstChannelFocusRequester = firstContentFocusRequester,
                                    onDirectionLeft = focusSelectedGroup,

                                    modifier = Modifier.weight(0.35f).fillMaxHeight(),
                                )
                                TiviEpgGrid(
                                    epgRows = c.epgRows,
                                    focusedChannelId = c.focusedChannel?.id,
                                    verticalListState = epgListState,
                                    modifier = Modifier.weight(0.65f).fillMaxHeight(),
                                )
                            }
                        }
                    }
                }
                is TiviContent.MoviesContent -> {
                    if (uiState.selectedGroupId == null) {
                        TiviEmptyHint("S\u00e9lectionne un groupe Films")
                    } else if (c.isLoading) {
                        TiviEmptyHint("Chargement...")
                    } else {
                        TiviMovieContent(
                            movies = c.movies,
                            posterSize = uiState.posterSize,
                            onMovieFocused = viewModel::enrichMovieDetails,
                            onToggleFavorite = viewModel::toggleMovieFavorite,
                            firstMovieFocusRequester = firstContentFocusRequester,
                            onDirectionLeft = focusSelectedGroup,
                            onPlayMovie = { movie ->
                                playbackScope.launch {
                                    when (val result = viewModel.resolveMovieStream(movie)) {
                                        is Result.Success -> {
                                            val (url, headers) = result.data
                                            navController.navigate(
                                                Screen.Player.createRoute(
                                                    streamUrl = url,
                                                    title = movie.name,
                                                    streamName = movie.name,
                                                    headers = headers,
                                                    contentId = "iptv_movie:${movie.providerId}:${movie.id}",
                                                    contentType = "iptv_movie",
                                                    contentName = movie.name,
                                                    videoId = movie.id.toString(),
                                                    poster = movie.posterUrl,
                                                    backdrop = movie.backdropUrl
                                                )
                                            ) { popUpTo(Screen.Player.route) { inclusive = true } }
                                        }
                                        is Result.Error -> Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                        else -> Toast.makeText(context, "Erreur de lecture", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        )
                    }
                }
                is TiviContent.SeriesContent -> {
                    if (uiState.selectedGroupId == null) {
                        TiviEmptyHint("S\u00e9lectionne un groupe S\u00e9ries")
                    } else if (c.isLoading) {
                        TiviEmptyHint("Chargement...")
                    } else {
                        TiviSeriesContent(
                            series = c.series,
                            posterSize = uiState.posterSize,
                            onSeriesFocused = viewModel::enrichSeriesDetails,
                            onToggleFavorite = viewModel::toggleSeriesFavorite,
                            firstSeriesFocusRequester = firstContentFocusRequester,
                            onDirectionLeft = focusSelectedGroup,
                            onOpenSeries = { series ->
                                val providerId = uiState.selectedProviderId ?: series.providerId
                                navController.navigate(Screen.IptvSeriesDetail.createRoute(series.id, providerId))
                            }
                        )
                    }
                }
                TiviContent.Empty -> {
                    TiviEmptyHint("S\u00e9lectionne un provider puis un groupe")
                }
            }
        }
    }
}

// -- Grille Films -------------------------------------------------------------

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TiviMovieContent(
    movies: List<com.streamvault.domain.model.Movie>,
    posterSize: IptvPosterSize,
    onMovieFocused: (com.streamvault.domain.model.Movie) -> Unit,
    onToggleFavorite: (com.streamvault.domain.model.Movie) -> Unit,
    firstMovieFocusRequester: FocusRequester? = null,
    onDirectionLeft: (() -> Boolean)? = null,
    onPlayMovie: (com.streamvault.domain.model.Movie) -> Unit,
) {
    if (movies.isEmpty()) {
        TiviEmptyHint("Aucun film dans ce groupe")
        return
    }
    var focusedMovieId by remember { mutableStateOf<Long?>(null) }
    val focusedMovie = movies.firstOrNull { it.id == focusedMovieId } ?: movies.firstOrNull()
    LaunchedEffect(focusedMovie?.id) { focusedMovie?.let(onMovieFocused) }
    Column(Modifier.fillMaxSize()) {
        focusedMovie?.let { movie ->
            IptvFocusedMediaPanel(
                details = movie.toTiviFocusedDetails(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val columns = remember(maxWidth, posterSize.cardWidth) {
                (((maxWidth - 8.dp).value / (posterSize.cardWidth + 8.dp).value).toInt()).coerceAtLeast(1)
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(posterSize.cardWidth),
                modifier = Modifier.fillMaxSize().padding(8.dp),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(movies.size, key = { index -> movies[index].id }) { index ->
                    val movie = movies[index]
                    TiviMediaCard(
                        title = movie.name,
                        posterUrl = movie.posterUrl,
                        cardWidth = posterSize.cardWidth,
                        isFavorite = movie.isFavorite,
                        focusRequester = if (index == 0) firstMovieFocusRequester else null,
                        onDirectionLeft = if (index % columns == 0) onDirectionLeft else null,
                        onLongPress = { onToggleFavorite(movie) },
                        onFocused = {
                            focusedMovieId = movie.id
                            onMovieFocused(movie)
                        },
                        onClick = { onPlayMovie(movie) },
                    )
                }
            }
        }
    }
}

// -- Grille Series ------------------------------------------------------------

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TiviSeriesContent(
    series: List<com.streamvault.domain.model.Series>,
    posterSize: IptvPosterSize,
    onSeriesFocused: (com.streamvault.domain.model.Series) -> Unit,
    onToggleFavorite: (com.streamvault.domain.model.Series) -> Unit,
    firstSeriesFocusRequester: FocusRequester? = null,
    onDirectionLeft: (() -> Boolean)? = null,
    onOpenSeries: (com.streamvault.domain.model.Series) -> Unit,
) {
    if (series.isEmpty()) {
        TiviEmptyHint("Aucune série dans ce groupe")
        return
    }
    var focusedSeriesId by remember { mutableStateOf<Long?>(null) }
    val focusedSeries = series.firstOrNull { it.id == focusedSeriesId } ?: series.firstOrNull()
    LaunchedEffect(focusedSeries?.id) { focusedSeries?.let(onSeriesFocused) }
    Column(Modifier.fillMaxSize()) {
        focusedSeries?.let { item ->
            IptvFocusedMediaPanel(
                details = item.toTiviFocusedDetails(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val columns = remember(maxWidth, posterSize.cardWidth) {
                (((maxWidth - 8.dp).value / (posterSize.cardWidth + 8.dp).value).toInt()).coerceAtLeast(1)
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(posterSize.cardWidth),
                modifier = Modifier.fillMaxSize().padding(8.dp),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(series.size, key = { index -> series[index].id }) { index ->
                    val item = series[index]
                    TiviMediaCard(
                        title = item.name,
                        posterUrl = item.posterUrl,
                        cardWidth = posterSize.cardWidth,
                        isFavorite = item.isFavorite,
                        focusRequester = if (index == 0) firstSeriesFocusRequester else null,
                        onDirectionLeft = if (index % columns == 0) onDirectionLeft else null,
                        onLongPress = { onToggleFavorite(item) },
                        onFocused = {
                            focusedSeriesId = item.id
                            onSeriesFocused(item)
                        },
                        onClick = { onOpenSeries(item) },
                    )
                }
            }
        }
    }
}

// -- Card media generique -----------------------------------------------------

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TiviMediaCard(
    title: String,
    posterUrl: String?,
    cardWidth: androidx.compose.ui.unit.Dp,
    isFavorite: Boolean,
    focusRequester: FocusRequester? = null,
    onDirectionLeft: (() -> Boolean)? = null,
    onLongPress: () -> Unit,
    onFocused: () -> Unit,
    onClick: () -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(10.dp)
    val longPressKeyTracker = rememberLongPressKeyTracker()
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.FocusRing else Color.Transparent,
        label = "cardBorder"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(cardWidth)
            .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier)
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) onFocused()
            }
            .onPreviewKeyEvent { event ->
                val native = event.nativeKeyEvent
                if (native.action == AndroidKeyEvent.ACTION_DOWN &&
                    native.keyCode == AndroidKeyEvent.KEYCODE_DPAD_LEFT &&
                    onDirectionLeft?.invoke() == true
                ) {
                    true
                } else if (native.action == AndroidKeyEvent.ACTION_DOWN &&
                    native.keyCode == AndroidKeyEvent.KEYCODE_MENU
                ) {
                    onLongPress()
                    true
                } else {
                    longPressKeyTracker.handle(
                        native,
                        { keyCode ->
                            keyCode == AndroidKeyEvent.KEYCODE_DPAD_CENTER ||
                                keyCode == AndroidKeyEvent.KEYCODE_ENTER ||
                                keyCode == AndroidKeyEvent.KEYCODE_NUMPAD_ENTER
                        },
                        onLongPress
                    )
                }
            },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(
            containerColor = NuvioColors.BackgroundCard,
            focusedContainerColor = NuvioColors.BackgroundCard,
        ),
        border = CardDefaults.border(
            border = Border(border = BorderStroke(1.dp, Color.Transparent), shape = shape),
            focusedBorder = Border(border = BorderStroke(2.dp, borderColor), shape = shape),
        ),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.97f),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                    .background(NuvioColors.BackgroundElevated),
            ) {
                if (!posterUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                if (isFavorite) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(22.dp),
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = NuvioColors.TextPrimary,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NuvioColors.Secondary.copy(alpha = 0.18f))
                    .padding(horizontal = 8.dp, vertical = 7.dp)
                    .then(
                        if (isFocused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier
                    ),
            )
        }
    }
}

private fun com.streamvault.domain.model.Movie.toTiviFocusedDetails(): IptvFocusedMediaDetails = IptvFocusedMediaDetails(
    title = name,
    year = year ?: releaseDate?.take(4),
    rating = rating,
    duration = duration ?: durationSeconds.takeIf { it > 0 }?.let(::formatTiviDurationSeconds),
    genre = genre,
    cast = cast,
    director = director,
    plot = plot,
)

private fun com.streamvault.domain.model.Series.toTiviFocusedDetails(): IptvFocusedMediaDetails = IptvFocusedMediaDetails(
    title = name,
    year = releaseDate?.take(4),
    rating = rating,
    duration = episodeRunTime,
    genre = genre,
    cast = cast,
    director = director,
    plot = plot,
)

private fun formatTiviDurationSeconds(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours > 0) "${hours}h ${minutes}min" else "${minutes}min"
}
// -- Hint vide ----------------------------------------------------------------

@Composable
private fun TiviEmptyHint(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, color = NuvioColors.TextTertiary, fontSize = 13.sp)
    }
}
