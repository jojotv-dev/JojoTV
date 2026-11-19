package com.nuvio.tv.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.livetv.model.LiveTvSettings
import com.nuvio.tv.livetv.model.PanelRowList
import com.nuvio.tv.livetv.model.PanelRowLists
import com.nuvio.tv.livetv.ui.RowGroupOption
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.theme.NuvioTheme
import kotlinx.coroutines.delay

@Composable
internal fun ChannelRowDialog(
    settings: LiveTvSettings,
    groups: List<RowGroupOption>,
    onUpdate: ((LiveTvSettings) -> LiveTvSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    val firstFocus = remember { FocusRequester() }
    val groupById = remember(groups) { groups.associateBy(RowGroupOption::id) }
    val lists = remember(settings.panelRow, settings.panelRowLists, groups) {
        completeChannelRowLists(settings, groups)
    }

    LaunchedEffect(Unit) {
        delay(80)
        runCatching { firstFocus.requestFocus() }
    }

    NuvioDialog(
        onDismiss = onDismiss,
        title = "Chaînes rapides",
        subtitle = "Choisissez les listes affichées sous le lecteur.",
        width = 760.dp,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(max = 440.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(key = "visibility") {
                SettingsToggleRow(
                    title = "Afficher les chaînes rapides",
                    subtitle = "Afficher ou masquer la ligne sous le lecteur.",
                    checked = PanelRowLists.shown(settings),
                    onToggle = {
                        onUpdate { current ->
                            PanelRowLists.setShown(current, !PanelRowLists.shown(current))
                        }
                    },
                    modifier = Modifier.focusRequester(firstFocus),
                )
            }
            item(key = "opens_on") {
                SettingsActionRow(
                    title = "S’ouvre sur",
                    subtitle = "Liste affichée à l’ouverture du lecteur.",
                    value = if (settings.panelRowOpens == "first") "Première liste" else "Dernière utilisée",
                    onClick = {
                        onUpdate { current ->
                            current.copy(panelRowOpens = if (current.panelRowOpens == "first") "last" else "first")
                        }
                    },
                )
            }
            if (lists.isEmpty()) {
                item(key = "empty") {
                    Text(
                        text = "Aucune liste de chaînes n’est disponible.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NuvioTheme.colors.TextSecondary,
                    )
                }
            } else {
                items(lists, key = PanelRowList::id) { list ->
                    val group = groupById[list.id]
                    val (title, subtitle) = when (list.id) {
                        PanelRowLists.RECENT -> "Récemment regardées" to "Intégré"
                        PanelRowLists.FAVORITES -> "Favoris" to "Intégré"
                        else -> group?.let {
                            it.name to listOfNotNull(it.playlist, "${it.count} chaînes").joinToString(" · ")
                        } ?: ("Groupe introuvable" to "Absent des playlists actuelles")
                    }
                    SettingsToggleRow(
                        title = title,
                        subtitle = subtitle,
                        checked = list.on,
                        onToggle = {
                            onUpdate { current ->
                                val currentLists = completeChannelRowLists(current, groups)
                                val currentValue = currentLists.firstOrNull { it.id == list.id }?.on ?: false
                                PanelRowLists.withLists(
                                    current,
                                    currentLists.map { entry ->
                                        if (entry.id == list.id) entry.copy(on = !currentValue) else entry
                                    },
                                )
                            }
                        },
                    )
                }
            }
        }
        SettingsDialogActionRow {
            SettingsDialogActionButton(text = "Terminé", onClick = onDismiss, primary = true)
        }
    }
}

private fun completeChannelRowLists(
    settings: LiveTvSettings,
    groups: List<RowGroupOption>,
): List<PanelRowList> = buildList {
    val saved = PanelRowLists.parse(settings)
    addAll(saved)
    groups.forEach { group ->
        if (none { it.id == group.id }) add(PanelRowList(group.id, on = false))
    }
}
