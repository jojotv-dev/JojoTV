package com.nuvio.tv.ui.screens.iptv

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.ui.components.NuvioInputDialog
import com.nuvio.tv.ui.theme.NuvioColors

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun IptvGroupOptionsDialog(
    groupName: String,
    onDismiss: () -> Unit,
    onHide: () -> Unit,
    onRename: (String) -> Unit
) {
    val primaryFocusRequester = remember { FocusRequester() }
    var showRenameDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { primaryFocusRequester.requestFocus() }

    if (showRenameDialog) {
        NuvioInputDialog(
            title = groupName,
            subtitle = "Nouveau nom du groupe",
            initialValue = groupName,
            onDismiss = { showRenameDialog = false },
            onConfirm = { newName ->
                onRename(newName)
                showRenameDialog = false
                onDismiss()
            }
        )
        return
    }

    NuvioDialog(
        onDismiss = onDismiss,
        title = groupName,
        subtitle = "Options du groupe"
    ) {
        Button(
            onClick = {
                onHide()
                onDismiss()
            },
            modifier = Modifier.fillMaxWidth().focusRequester(primaryFocusRequester),
            colors = ButtonDefaults.colors(
                containerColor = NuvioColors.BackgroundCard,
                contentColor = NuvioColors.TextPrimary
            )
        ) {
            Text("Masquer ce groupe")
        }
        Button(
            onClick = { showRenameDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.colors(
                containerColor = NuvioColors.BackgroundCard,
                contentColor = NuvioColors.TextPrimary
            )
        ) {
            Text("Renommer ce groupe")
        }
    }
}
