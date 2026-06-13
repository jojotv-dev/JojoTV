package com.nuvio.tv.ui.screens.iptv

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.nuvio.tv.ui.theme.NuvioColors
import com.streamvault.domain.model.Movie
import com.streamvault.domain.model.Result
import com.streamvault.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IptvMovieListViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val providerId: Long = savedStateHandle.get<String>("providerId")?.toLongOrNull() ?: -1L
    val categoryId: Long? = savedStateHandle.get<String>("categoryId")?.toLongOrNull()
    val categoryName: String = savedStateHandle.get<String>("categoryName") ?: "Tous les films"

    val movies: StateFlow<List<Movie>> = (
        if (categoryId == null) movieRepository.getMovies(providerId)
        else movieRepository.getMoviesByCategory(providerId, categoryId)
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _loadingMovieId = MutableStateFlow<Long?>(null)
    val loadingMovieId: StateFlow<Long?> = _loadingMovieId.asStateFlow()

    private val _streamError = MutableStateFlow<String?>(null)
    val streamError: StateFlow<String?> = _streamError.asStateFlow()

    fun clearStreamError() { _streamError.value = null }

    suspend fun resolveStream(movie: Movie): Result<Pair<String, Map<String, String>>> {
        _loadingMovieId.value = movie.id
        val result = movieRepository.getStreamInfo(movie)
        _loadingMovieId.value = null
        return when (result) {
            is Result.Success -> Result.Success(result.data.url to result.data.headers)
            is Result.Error -> { _streamError.value = result.message; Result.Error(result.message) }
            else -> Result.Error("Erreur inconnue")
        }
    }
}

@Composable
fun IptvMovieListScreen(
    onPlayMovie: (streamUrl: String, title: String, headers: Map<String, String>, posterUrl: String?) -> Unit,
    viewModel: IptvMovieListViewModel = hiltViewModel()
) {
    val movies by viewModel.movies.collectAsStateWithLifecycle()
    val loadingMovieId by viewModel.loadingMovieId.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var viewMode by remember { mutableStateOf(IptvViewMode.GRID) }

    Column(modifier = Modifier.fillMaxSize().background(NuvioColors.Background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp, vertical = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Movie, null, tint = NuvioColors.TextPrimary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.size(10.dp))
            Text(
                viewModel.categoryName,
                color = NuvioColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IptvViewModeToggle(viewMode = viewMode, onToggle = {
                viewMode = if (viewMode == IptvViewMode.GRID) IptvViewMode.LIST else IptvViewMode.GRID
            })
        }

        if (movies.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.Movie, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(48.dp))
                    Text("Aucun film", color = NuvioColors.TextSecondary, fontSize = 15.sp)
                }
            }
        } else if (viewMode == IptvViewMode.GRID) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(movies, key = { it.id }) { movie ->
                    MovieCard(
                        movie = movie,
                        isLoading = loadingMovieId == movie.id,
                        onClick = {
                            scope.launch {
                                val result = viewModel.resolveStream(movie)
                                if (result is Result.Success) {
                                    val (url, headers) = result.data
                                    onPlayMovie(url, movie.name, headers, movie.posterUrl)
                                }
                            }
                        }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(movies, key = { it.id }) { movie ->
                    MovieListRow(
                        movie = movie,
                        isLoading = loadingMovieId == movie.id,
                        onClick = {
                            scope.launch {
                                val result = viewModel.resolveStream(movie)
                                if (result is Result.Success) {
                                    val (url, headers) = result.data
                                    onPlayMovie(url, movie.name, headers, movie.posterUrl)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieListRow(movie: Movie, isLoading: Boolean, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(72.dp).onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.97f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.width(48.dp).height(64.dp).background(NuvioColors.BackgroundElevated, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (movie.posterUrl != null) {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = movie.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Movie, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(20.dp))
                }
                if (isLoading) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(0.5f), RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Refresh, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.name,
                    color = NuvioColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = if (isFocused) Modifier.basicMarquee() else Modifier
                )
                movie.year?.let { movieYear ->
                    Spacer(Modifier.height(3.dp))
                    Text(movieYear, color = NuvioColors.TextTertiary, fontSize = 11.sp)
                }
            }
            if (movie.rating > 0f) {
                Text(
                    "%.1f".format(movie.rating),
                    color = Color(0xFFFFD700),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (isFocused && !isLoading) {
                Icon(Icons.Default.PlayArrow, null, tint = NuvioColors.Primary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun MovieCard(movie: Movie, isLoading: Boolean, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(10.dp)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.97f)
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f).background(NuvioColors.BackgroundElevated),
                contentAlignment = Alignment.Center
            ) {
                if (movie.posterUrl != null) {
                    AsyncImage(model = movie.posterUrl, contentDescription = movie.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.Movie, null, tint = NuvioColors.TextTertiary, modifier = Modifier.size(32.dp))
                }
                if (isLoading) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(0.5f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Refresh, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
                if (isFocused && !isLoading) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(0.3f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
                if (movie.rating > 0f) {
                    Box(Modifier.align(Alignment.TopEnd).padding(6.dp).background(Color.Black.copy(0.7f), RoundedCornerShape(4.dp)).padding(horizontal = 5.dp, vertical = 2.dp)) {
                        Text("%.1f".format(movie.rating), color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                if (movie.watchProgress > 0L && movie.durationSeconds > 0) {
                    val progress = (movie.watchProgress / 1000f / movie.durationSeconds).coerceIn(0f, 1f)
                    Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().height(3.dp).background(Color.White.copy(0.2f))) {
                        Box(Modifier.fillMaxWidth(progress).fillMaxHeight().background(NuvioColors.Primary))
                    }
                }
            }
            Column(Modifier.padding(8.dp)) {
                Text(
                    movie.name,
                    color = NuvioColors.TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp,
                    modifier = if (isFocused) Modifier.basicMarquee() else Modifier
                )
                movie.year?.let { movieYear ->
                    Spacer(Modifier.height(2.dp))
                    Text(movieYear, color = NuvioColors.TextTertiary, fontSize = 10.sp)
                }
            }
        }
    }
}
