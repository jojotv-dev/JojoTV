package com.nuvio.tv.ui.screens.iptv.tivi.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import com.nuvio.tv.ui.screens.iptv.tivi.TiviVisibilityDialogState
import com.nuvio.tv.ui.theme.NuvioColors

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VisibilityToggleDialog(
    state: TiviVisibilityDialogState,
    onDismiss: () -> Unit,
    onChange: (Set<Long>) -> Unit,
) {
    var visibleIds by remember(state) {
        mutableStateOf(state.items.filter { it.isVisible }.mapTo(mutableSetOf()) { it.id })
    }
    fun updateVisibleIds(next: Set<Long>) {
        visibleIds = next.toMutableSet()
        onChange(next)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.width(620.dp).heightIn(max = 620.dp),
            shape = RoundedCornerShape(8.dp),
            colors = SurfaceDefaults.colors(containerColor = NuvioColors.BackgroundElevated),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(text = state.title, style = MaterialTheme.typography.headlineSmall)
                Button(
                    onClick = {
                        val allIds = state.items.mapTo(mutableSetOf()) { it.id }
                        updateVisibleIds(
                            if (visibleIds.size == state.items.size) emptySet() else allIds
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.colors(
                        containerColor = NuvioColors.BackgroundCard,
                        contentColor = NuvioColors.TextPrimary,
                    ),
                ) {
                    val allSelected = visibleIds.size == state.items.size && state.items.isNotEmpty()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            imageVector = if (allSelected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                            contentDescription = null,
                            tint = if (allSelected) MaterialTheme.colorScheme.primary else NuvioColors.TextTertiary,
                        )
                        Text(text = if (allSelected) "Tout désélectionner" else "Tout sélectionner")
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 460.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(state.items, key = { it.id }) { item ->
                        val isVisible = item.id in visibleIds
                        Button(
                            onClick = {
                                updateVisibleIds(visibleIds.toMutableSet().apply {
                                    if (isVisible) remove(item.id) else add(item.id)
                                })
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.colors(
                                containerColor = NuvioColors.BackgroundCard,
                                contentColor = NuvioColors.TextPrimary,
                            ),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Icon(
                                    imageVector = if (isVisible) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                    contentDescription = null,
                                    tint = if (isVisible) MaterialTheme.colorScheme.primary else NuvioColors.TextTertiary,
                                )
                                Text(text = item.label, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                ) {
                    Button(onClick = onDismiss) { Text("Fermer") }
                }
            }
        }
    }
}

