package com.nuvio.tv.ui.screens.iptv

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.theme.NuvioColors

internal val IptvVodPosterWidth = 120.dp

internal data class IptvFocusedMediaDetails(
    val title: String,
    val year: String?,
    val rating: Float,
    val duration: String?,
    val genre: String?,
    val cast: String?,
    val director: String?,
    val plot: String?,
)

@Composable
internal fun IptvFocusedMediaPanel(
    details: IptvFocusedMediaDetails,
    modifier: Modifier = Modifier,
) {
    val fullPlot = details.plot?.takeIf { it.isNotBlank() }
    var showFullPlot by remember(details.title, fullPlot) { mutableStateOf(false) }
    val summaryScrollState = rememberScrollState()
    Card(
        onClick = { if (fullPlot != null) showFullPlot = true },
        modifier = modifier
            .fillMaxWidth()
            .height(198.dp)
            .padding(horizontal = 48.dp, vertical = 8.dp)
            .onFocusChanged { focusState ->
                if (focusState.isFocused && fullPlot != null) showFullPlot = true
            },
        shape = CardDefaults.shape(RoundedCornerShape(8.dp)),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundElevated, focusedContainerColor = NuvioColors.BackgroundElevated),
        scale = CardDefaults.scale(focusedScale = 1f),
    ) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            Text(buildTitle(details.title, details.year), color = NuvioColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(6.dp))
            FocusedMediaMetaRow(details)
            FocusedMediaCreditLine(label = "Acteurs", value = details.cast)
            FocusedMediaCreditLine(label = "Realisateur", value = details.director)
            Spacer(Modifier.height(4.dp))
            Text(
                text = fullPlot ?: "Aucune description disponible.",
                color = NuvioColors.TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.heightIn(max = 96.dp).verticalScroll(summaryScrollState),
            )
        }
    }
    if (showFullPlot && fullPlot != null) {
        val plotFocusRequester = remember { FocusRequester() }
        val plotScrollState = rememberScrollState()
        LaunchedEffect(Unit) { plotFocusRequester.requestFocus() }
        NuvioDialog(onDismiss = { showFullPlot = false }, title = buildTitle(details.title, details.year), width = 680.dp) {
            Text(
                text = fullPlot,
                color = NuvioColors.TextSecondary,
                fontSize = 16.sp,
                lineHeight = 23.sp,
                modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp).verticalScroll(plotScrollState).focusRequester(plotFocusRequester).focusable(),
            )
            Button(onClick = { showFullPlot = false }, modifier = Modifier.fillMaxWidth()) { Text("Fermer") }
        }
    }
}
@Composable
private fun FocusedMediaMetaRow(details: IptvFocusedMediaDetails) {
    val meta = buildList {
        if (details.rating > 0f) add("%.1f".format(details.rating))
        details.year?.takeIf { it.isNotBlank() }?.let(::add)
        details.duration?.takeIf { it.isNotBlank() }?.let(::add)
        details.genre?.takeIf { it.isNotBlank() }?.let(::add)
    }
    if (meta.isNotEmpty()) {
        Text(
            text = meta.joinToString("  •  "),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(6.dp))
    }
}

@Composable
private fun FocusedMediaCreditLine(label: String, value: String?) {
    val cleanValue = value?.takeIf { it.isNotBlank() } ?: return
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "$label :",
            color = NuvioColors.TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = cleanValue,
            color = NuvioColors.TextSecondary,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun buildTitle(title: String, year: String?): String {
    val cleanYear = year?.takeIf { it.isNotBlank() }
    return if (cleanYear != null && !title.contains(cleanYear)) "$title ($cleanYear)" else title
}
