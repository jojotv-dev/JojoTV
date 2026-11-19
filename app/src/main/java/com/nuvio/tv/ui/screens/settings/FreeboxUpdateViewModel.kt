package com.nuvio.tv.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.BuildConfig
import com.nuvio.tv.data.freebox.FreeboxEntry
import com.nuvio.tv.data.freebox.FreeboxRepository
import com.nuvio.tv.updater.UpdateBannerPolicy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class FreeboxUpdateStatus {
    IDLE,
    CHECKING,
    NOT_CONFIGURED,
    UP_TO_DATE,
    AVAILABLE,
    DOWNLOADING,
    READY_TO_INSTALL,
    ERROR,
}

data class FreeboxUpdateCandidate(
    val entry: FreeboxEntry,
    val version: String,
)

data class FreeboxUpdateUiState(
    val status: FreeboxUpdateStatus = FreeboxUpdateStatus.IDLE,
    val candidate: FreeboxUpdateCandidate? = null,
    val downloadedApk: File? = null,
    val errorMessage: String? = null,
    val showBanner: Boolean = false,
    val downloadProgressPercent: Int? = null,
)

@HiltViewModel
class FreeboxUpdateViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val freeboxRepository: FreeboxRepository,
) : ViewModel() {
    private val mutableState = MutableStateFlow(FreeboxUpdateUiState())
    val state = mutableState.asStateFlow()

    init {
        checkForUpdates()
        viewModelScope.launch {
            freeboxRepository.state
                .map { it.address.trim() }
                .distinctUntilChanged()
                .filter(String::isNotBlank)
                .collect { checkForUpdates() }
        }
    }

    fun checkForUpdates(force: Boolean = false) {
        if (mutableState.value.status == FreeboxUpdateStatus.CHECKING ||
            mutableState.value.status == FreeboxUpdateStatus.DOWNLOADING
        ) return

        val previousState = mutableState.value
        mutableState.value = previousState.copy(status = FreeboxUpdateStatus.CHECKING, errorMessage = null)
        viewModelScope.launch {
            try {
                if (freeboxRepository.state.value.address.isBlank()) {
                    mutableState.value = FreeboxUpdateUiState(status = FreeboxUpdateStatus.NOT_CONFIGURED)
                    return@launch
                }

                val latest = freeboxRepository.listBrowserEntries("")
                    .asSequence()
                    .filterNot(FreeboxEntry::isDirectory)
                    .mapNotNull(::versionedApk)
                    .maxWithOrNull { first, second -> compareVersions(first.version, second.version) }

                if (latest == null || compareVersions(latest.version, BuildConfig.VERSION_NAME) <= 0) {
                    mutableState.value = FreeboxUpdateUiState(status = FreeboxUpdateStatus.UP_TO_DATE)
                } else {
                    val downloadedApk = previousState.downloadedApk
                        ?.takeIf { previousState.candidate?.version == latest.version && it.isFile && it.length() > 0L }
                        ?: File(context.cacheDir, DOWNLOADED_APK_NAME).takeIf {
                            updatePreferences.getString(DOWNLOADED_VERSION_KEY, null) == latest.version &&
                                it.isFile && it.length() > 0L
                        }
                    val dismissedVersion = updatePreferences.getString(DISMISSED_VERSION_KEY, null)
                    val shouldShowBanner = UpdateBannerPolicy.shouldShow(
                        isRemoteNewer = true,
                        force = force,
                        bannerEnabled = true,
                        dismissedTag = dismissedVersion,
                        updateTag = latest.version,
                    )
                    mutableState.value = FreeboxUpdateUiState(
                        status = if (downloadedApk != null) {
                            FreeboxUpdateStatus.READY_TO_INSTALL
                        } else {
                            FreeboxUpdateStatus.AVAILABLE
                        },
                        candidate = latest,
                        downloadedApk = downloadedApk,
                        showBanner = shouldShowBanner,
                    )
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                mutableState.value = previousState.copy(
                    status = FreeboxUpdateStatus.ERROR,
                    errorMessage = error.localizedMessage?.takeIf(String::isNotBlank),
                )
            }
        }
    }

    fun dismissBanner() {
        val current = mutableState.value
        if (current.status == FreeboxUpdateStatus.DOWNLOADING) return
        current.candidate?.version?.let { version ->
            updatePreferences.edit().putString(DISMISSED_VERSION_KEY, version).apply()
        }
        mutableState.value = current.copy(showBanner = false)
    }

    fun downloadUpdate() {
        if (mutableState.value.status != FreeboxUpdateStatus.AVAILABLE &&
            mutableState.value.status != FreeboxUpdateStatus.ERROR
        ) return
        val candidate = mutableState.value.candidate ?: return

        mutableState.value = mutableState.value.copy(
            status = FreeboxUpdateStatus.DOWNLOADING,
            downloadedApk = null,
            errorMessage = null,
            showBanner = true,
            downloadProgressPercent = candidate.entry.sizeBytes.takeIf { it > 0L }?.let { 0 },
        )
        viewModelScope.launch {
            val partialFile = File(context.cacheDir, "nuvio-freebox-update.apk.part")
            val apkFile = File(context.cacheDir, DOWNLOADED_APK_NAME)
            try {
                withContext(Dispatchers.IO) {
                    partialFile.delete()
                    var lastProgressPercent = -1
                    freeboxRepository.copyBrowserFileToCache(candidate.entry.path, partialFile) { copiedBytes, totalBytes ->
                        val progressPercent = totalBytes.takeIf { it > 0L }?.let { total ->
                            ((copiedBytes.coerceAtLeast(0L) * 100L) / total).toInt().coerceIn(0, 100)
                        }
                        if (progressPercent != null && progressPercent != lastProgressPercent) {
                            lastProgressPercent = progressPercent
                            mutableState.update { current ->
                                if (current.status == FreeboxUpdateStatus.DOWNLOADING) {
                                    current.copy(downloadProgressPercent = progressPercent)
                                } else {
                                    current
                                }
                            }
                        }
                    }
                    check(partialFile.isFile && partialFile.length() > 0L) {
                        "Le fichier téléchargé est vide."
                    }
                    if (candidate.entry.sizeBytes > 0L) {
                        check(partialFile.length() == candidate.entry.sizeBytes) {
                            "La taille du fichier téléchargé ne correspond pas à celle du fichier Freebox."
                        }
                    }
                    apkFile.delete()
                    if (!partialFile.renameTo(apkFile)) {
                        partialFile.copyTo(apkFile, overwrite = true)
                        partialFile.delete()
                    }
                }
                mutableState.value = mutableState.value.copy(
                    status = FreeboxUpdateStatus.READY_TO_INSTALL,
                    downloadedApk = apkFile,
                    showBanner = true,
                    downloadProgressPercent = null,
                )
                updatePreferences.edit()
                    .putString(DOWNLOADED_VERSION_KEY, candidate.version)
                    .apply()
            } catch (cancelled: CancellationException) {
                partialFile.delete()
                throw cancelled
            } catch (error: Exception) {
                partialFile.delete()
                mutableState.value = mutableState.value.copy(
                    status = FreeboxUpdateStatus.ERROR,
                    errorMessage = error.localizedMessage?.takeIf(String::isNotBlank),
                    showBanner = true,
                    downloadProgressPercent = null,
                )
            }
        }
    }

    private fun versionedApk(entry: FreeboxEntry): FreeboxUpdateCandidate? {
        val version = VERSIONED_APK_REGEX.matchEntire(entry.name)
            ?.groupValues
            ?.getOrNull(1)
            ?: return null
        return FreeboxUpdateCandidate(entry, version)
    }

    private fun compareVersions(first: String, second: String): Int {
        val firstParts = first.split('.')
        val secondParts = second.split('.')
        for (index in 0 until maxOf(firstParts.size, secondParts.size)) {
            val firstPart = firstParts.getOrElse(index) { "0" }.trimStart('0').ifEmpty { "0" }
            val secondPart = secondParts.getOrElse(index) { "0" }.trimStart('0').ifEmpty { "0" }
            val lengthComparison = firstPart.length.compareTo(secondPart.length)
            if (lengthComparison != 0) return lengthComparison
            val valueComparison = firstPart.compareTo(secondPart)
            if (valueComparison != 0) return valueComparison
        }
        return 0
    }

    private companion object {
        const val UPDATE_PREFERENCES_NAME = "freebox_update_preferences"
        const val DISMISSED_VERSION_KEY = "dismissed_version"
        const val DOWNLOADED_VERSION_KEY = "downloaded_version"
        const val DOWNLOADED_APK_NAME = "nuvio-freebox-update.apk"

        val VERSIONED_APK_REGEX = Regex(
            "^NuvioTVLauncher[_ -]+v?([0-9]+(?:\\.[0-9]+){1,3})\\.apk$",
            RegexOption.IGNORE_CASE,
        )
    }

    private val updatePreferences by lazy {
        context.getSharedPreferences(UPDATE_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }
}
