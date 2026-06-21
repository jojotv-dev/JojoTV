package com.nuvio.tv.ui.components.freeboxposter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.nuvio.tv.R
import com.nuvio.tv.core.tmdb.TmdbPosterCandidate
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.theme.NuvioColors

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FreeboxPosterPickerDialog(
    state: FreeboxPosterPickerState,
    onSelect: (TmdbPosterCandidate) -> Unit,
    onDismiss: () -> Unit
) {
    val entry = state.entry ?: return
    val firstPosterFocusRequester = remember { FocusRequester() }
    val closeFocusRequester = remember { FocusRequester() }

    LaunchedEffect(state.isLoading, state.posters) {
        if (!state.isLoading) {
            if (state.posters.isNotEmpty()) firstPosterFocusRequester.requestFocus()
            else closeFocusRequester.requestFocus()
        }
    }

    NuvioDialog(
        onDismiss = onDismiss,
        title = stringResource(R.string.freebox_choose_poster),
        subtitle = entry.name,
        width = 760.dp
    ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            state.posters.isEmpty() -> {
                Text(
                    text = stringResource(R.string.freebox_poster_none),
                    color = NuvioColors.TextSecondary
                )
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().focusRequester(closeFocusRequester),
                    colors = ButtonDefaults.colors(
                        containerColor = NuvioColors.BackgroundCard,
                        contentColor = NuvioColors.TextPrimary
                    )
                ) {
                    Text(stringResource(R.string.action_close))
                }
            }

            else -> {
                if (state.saveFailed) {
                    Text(
                        text = stringResource(R.string.freebox_poster_save_error),
                        color = Color(0xFFFFB6B6)
                    )
                }
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(120.dp),
                    modifier = Modifier.fillMaxWidth().height(420.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(state.posters, key = { _, poster -> poster.url }) { index, poster ->
                        PosterCandidateCard(
                            poster = poster,
                            isSelected = poster.url == state.currentPosterUrl,
                            enabled = !state.isSaving,
                            onClick = { onSelect(poster) },
                            modifier = if (index == 0) {
                                Modifier.focusRequester(firstPosterFocusRequester)
                            } else {
                                Modifier
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun PosterCandidateCard(
    poster: TmdbPosterCandidate,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(8.dp)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Card(
            onClick = { if (enabled) onClick() },
            modifier = modifier.fillMaxWidth().aspectRatio(2f / 3f),
            shape = CardDefaults.shape(shape),
            colors = CardDefaults.colors(
                containerColor = NuvioColors.BackgroundCard,
                focusedContainerColor = NuvioColors.BackgroundCard
            ),
            border = CardDefaults.border(
                border = Border(
                    border = BorderStroke(
                        if (isSelected) 2.dp else 0.dp,
                        if (isSelected) accent else Color.Transparent
                    ),
                    shape = shape
                ),
                focusedBorder = Border(
                    border = BorderStroke(2.dp, accent),
                    shape = shape
                )
            ),
            scale = CardDefaults.scale(focusedScale = 1.04f)
        ) {
            Box(modifier = Modifier.fillMaxSize().border(0.dp, Color.Transparent, shape)) {
                AsyncImage(
                    model = poster.url,
                    contentDescription = poster.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().padding(2.dp)
                )
            }
        }
        poster.title?.let { title ->
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = NuvioColors.TextPrimary,
                maxLines = 2
            )
        }
        poster.releaseDate?.take(4)?.let { year ->
            Text(
                text = year,
                style = MaterialTheme.typography.labelSmall,
                color = NuvioColors.TextSecondary,
                maxLines = 1
            )
        }
    }
}