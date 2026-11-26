package com.nuvio.tv.ui.screens

import androidx.compose.ui.graphics.vector.ImageVector

internal data class ServerModuleNavEntry(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)
