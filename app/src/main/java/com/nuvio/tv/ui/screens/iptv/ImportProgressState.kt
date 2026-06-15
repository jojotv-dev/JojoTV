package com.nuvio.tv.ui.screens.iptv

data class ImportProgressState(
    val isImporting: Boolean = false,
    val isDone: Boolean = false,
    val liveCount: Int = 0,
    val movieCount: Int = 0,
    val seriesCount: Int = 0,
    val currentStep: ImportStep = ImportStep.IDLE,
    val progressMessage: String = "",
    val errorMessage: String? = null
)

enum class ImportStep {
    IDLE, DOWNLOADING, PARSING, SAVING, DONE, ERROR
}
