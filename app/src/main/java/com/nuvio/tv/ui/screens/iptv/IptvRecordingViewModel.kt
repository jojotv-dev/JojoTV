package com.nuvio.tv.ui.screens.iptv

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streamvault.domain.manager.RecordingManager
import com.streamvault.domain.model.RecordingItem
import com.streamvault.domain.model.RecordingRecurrence
import com.streamvault.domain.model.RecordingRequest
import com.streamvault.domain.model.RecordingStatus
import com.streamvault.domain.model.RecordingStorageConfig
import com.streamvault.domain.model.RecordingStorageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class IptvRecordingViewModel @Inject constructor(
    private val recordingManager: RecordingManager
) : ViewModel() {

    val recordings: StateFlow<List<RecordingItem>> = recordingManager.observeRecordingItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val storageState: StateFlow<RecordingStorageState> = recordingManager.observeStorageState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RecordingStorageState())

    private val _feedback = MutableStateFlow<String?>(null)
    val feedback: StateFlow<String?> = _feedback.asStateFlow()

    // ── Schedule form state ──────────────────────────────────────────
    private val _scheduleForm = MutableStateFlow(ScheduleFormState())
    val scheduleForm: StateFlow<ScheduleFormState> = _scheduleForm.asStateFlow()

    fun updateForm(update: ScheduleFormState.() -> ScheduleFormState) {
        _scheduleForm.value = _scheduleForm.value.update()
    }

    fun scheduleRecording() {
        val form = _scheduleForm.value
            if (form.channelName.isBlank()) { _feedback.value = "Nom de cha\u00EEne requis"; return }
        if (form.streamUrl.isBlank())   { _feedback.value = "URL du flux requise"; return }
            if (form.providerId <= 0L)      { _feedback.value = "S\u00E9lectionnez un provider"; return }

        val startMs = form.startCalendar.timeInMillis
            val endMs   = form.endCalendar.timeInMillis
            if (endMs <= startMs) { _feedback.value = "Heure de fin invalide"; return }

        viewModelScope.launch {
            val result = recordingManager.scheduleRecording(
                RecordingRequest(
                    providerId       = form.providerId,
                    channelId        = form.channelId,
                    channelName      = form.channelName,
                    streamUrl        = form.streamUrl,
                    scheduledStartMs = startMs,
                    scheduledEndMs   = endMs,
                    programTitle     = form.programTitle.takeIf { it.isNotBlank() },
                    recurrence       = form.recurrence
                )
            )
            _feedback.value = when {
                result is com.streamvault.domain.model.Result.Success -> {
                    _scheduleForm.value = ScheduleFormState()
                        "Enregistrement planifi\u00E9 \u2713"
                }
                result is com.streamvault.domain.model.Result.Error -> "Erreur : ${result.message}"
                else -> "Erreur inconnue"
            }
        }
    }

    fun startManualRecording(
        providerId: Long,
        channelId: Long,
        channelName: String,
        streamUrl: String,
    ) {
        val now = System.currentTimeMillis()
        viewModelScope.launch {
            val result = recordingManager.startManualRecording(
                RecordingRequest(
                    providerId = providerId,
                    channelId = channelId,
                    channelName = channelName,
                    streamUrl = streamUrl,
                    scheduledStartMs = now,
                    scheduledEndMs = now + 12 * 60 * 60 * 1000L,
                )
            )
            _feedback.value = when (result) {
                is com.streamvault.domain.model.Result.Success -> "Enregistrement démarré ✓"
                is com.streamvault.domain.model.Result.Error -> "Erreur : ${result.message}"
                com.streamvault.domain.model.Result.Loading -> null
            }
        }
    }

    fun prepareSchedule(
        providerId: Long,
        channelId: Long,
        channelName: String,
        streamUrl: String,
    ) {
        _scheduleForm.value = ScheduleFormState(
            providerId = providerId,
            channelId = channelId,
            channelName = channelName,
            streamUrl = streamUrl,
        )
    }

    fun cancelRecording(id: String) {
        viewModelScope.launch {
            val r = recordingManager.cancelRecording(id)
            if (r is com.streamvault.domain.model.Result.Error) _feedback.value = r.message
        }
    }

    fun deleteRecording(id: String) {
        viewModelScope.launch {
            val r = recordingManager.deleteRecording(id)
            if (r is com.streamvault.domain.model.Result.Error) _feedback.value = r.message
        }
    }

    fun updateStorageFolder(treeUri: String, displayName: String) {
        viewModelScope.launch {
            recordingManager.updateStorageConfig(
                RecordingStorageConfig(
                    treeUri     = treeUri,
                    displayName = displayName,
                    fileNamePattern        = storageState.value.fileNamePattern,
                    retentionDays          = storageState.value.retentionDays,
                    maxSimultaneousRecordings = storageState.value.maxSimultaneousRecordings
                )
            )
        }
    }

    fun updateStorageConfig(config: RecordingStorageConfig) {
        viewModelScope.launch { recordingManager.updateStorageConfig(config) }
    }

    fun clearFeedback() { _feedback.value = null }
}

data class ScheduleFormState(
    val providerId     : Long   = -1L,
    val channelId      : Long   = 0L,
    val channelName    : String = "",
    val streamUrl      : String = "",
    val programTitle   : String = "",
    val startCalendar  : Calendar = Calendar.getInstance().apply { add(Calendar.MINUTE, 5) },
    val endCalendar  : Calendar = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 1) },
    val recurrence     : RecordingRecurrence = RecordingRecurrence.NONE
)
