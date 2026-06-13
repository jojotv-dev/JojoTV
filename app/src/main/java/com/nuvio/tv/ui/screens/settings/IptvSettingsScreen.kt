package com.nuvio.tv.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun IptvSettingsScreen(
    viewModel: IptvSettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    Column(modifier = Modifier.padding(16.dp)) {
        SettingsToggleRow(
            title = "Afficher Live TV",
            subtitle = null,
            checked = settings.showLiveTvInSidebar,
            onToggle = { viewModel.setShowLiveTv(!settings.showLiveTvInSidebar) }
        )
        SettingsToggleRow(
            title = "Afficher Films",
            subtitle = null,
            checked = settings.showMoviesInSidebar,
            onToggle = { viewModel.setShowMovies(!settings.showMoviesInSidebar) }
        )
        SettingsToggleRow(
            title = "Afficher S\u00e9ries",
            subtitle = null,
            checked = settings.showSeriesInSidebar,
            onToggle = { viewModel.setShowSeries(!settings.showSeriesInSidebar) }
        )
        SettingsToggleRow(
            title = "Afficher Enregistrements",
            subtitle = null,
            checked = settings.showRecordingsInSidebar,
            onToggle = { viewModel.setShowRecordings(!settings.showRecordingsInSidebar) }
        )
    }
}