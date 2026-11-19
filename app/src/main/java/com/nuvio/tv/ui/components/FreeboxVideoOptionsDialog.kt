package com.nuvio.tv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.Button as TvMaterialButton
import com.nuvio.tv.ui.components.NuvioButton as Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.ui.theme.NuvioTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FreeboxVideoOptionsDialog(
    title: String,
    onDismiss: () -> Unit,
    onMarkWatched: () -> Unit,
    onMarkUnwatched: () -> Unit,
    onStartFromBeginning: () -> Unit,
    onChangePortraitArtwork: () -> Unit,
    onChangeLandscapeArtwork: () -> Unit,
    onRemoveFromContinueWatching: () -> Unit,
    onDeleteFromFreebox: () -> Unit,
) {
    val firstActionFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { firstActionFocusRequester.requestFocus() } }

    NuvioDialog(onDismiss = onDismiss, title = title) {
        Column(verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.xs)) {
            FreeboxVideoOptionButton(
                label = stringResource(R.string.hero_mark_watched),
                modifier = Modifier.focusRequester(firstActionFocusRequester),
                onClick = onMarkWatched,
            )
            FreeboxVideoOptionButton(stringResource(R.string.hero_mark_unwatched), onClick = onMarkUnwatched)
            FreeboxVideoOptionButton(
                stringResource(R.string.cw_action_start_from_beginning),
                onClick = onStartFromBeginning,
            )
            FreeboxVideoOptionButton(
                stringResource(R.string.home_freebox_change_portrait),
                onClick = onChangePortraitArtwork,
            )
            FreeboxVideoOptionButton(
                stringResource(R.string.home_freebox_change_landscape),
                onClick = onChangeLandscapeArtwork,
            )
            FreeboxVideoOptionButton(
                stringResource(R.string.cw_action_remove),
                onClick = onRemoveFromContinueWatching,
            )
            FreeboxVideoOptionButton(
                stringResource(R.string.cw_action_delete_from_freebox),
                onClick = onDeleteFromFreebox,
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FreeboxVideoOptionButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.colors(
            containerColor = NuvioTheme.colors.BackgroundCard,
            contentColor = NuvioTheme.colors.TextPrimary,
        ),
    ) {
        Text(label)
    }
}
