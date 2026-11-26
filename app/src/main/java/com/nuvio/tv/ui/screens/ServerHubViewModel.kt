package com.nuvio.tv.ui.screens

import android.content.Context
import android.os.StatFs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuvio.tv.data.freebox.FreeboxRepository
import com.nuvio.tv.data.mediaserver.ServerConnection
import com.nuvio.tv.data.mediaserver.ServerRepository
import com.nuvio.tv.data.storage.StorageCapacity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

data class ServerHubCapacityState(
    val local: String = "Calcul de l’espace…",
    val freebox: String? = null,
    val servers: Map<String, String?> = emptyMap(),
    val localCapacity: StorageCapacity? = null,
    val freeboxCapacity: StorageCapacity? = null,
    val serverCapacities: Map<String, StorageCapacity?> = emptyMap(),
)

@HiltViewModel
class ServerHubViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val serverRepository: ServerRepository,
    private val freeboxRepository: FreeboxRepository,
) : ViewModel() {
    val servers = serverRepository.uiState
    val freebox = freeboxRepository.state
    private val mutableCapacityState = MutableStateFlow(ServerHubCapacityState())
    val capacityState: StateFlow<ServerHubCapacityState> = mutableCapacityState.asStateFlow()
    private var capacityJob: Job? = null

    init {
        serverRepository.ensureLoaded()
    }

    fun providerName(connection: ServerConnection): String =
        serverRepository.provider(connection)?.displayName ?: connection.providerId

    fun hasProvider(connection: ServerConnection): Boolean =
        serverRepository.provider(connection) != null

    fun refreshStorageCapacity() {
        val connections = serverRepository.enabledConnections()
        val freeboxConfigured = freeboxRepository.state.value.address.isNotBlank()
        mutableCapacityState.value = ServerHubCapacityState(
            local = "Calcul de l’espace…",
            freebox = if (freeboxConfigured) "Calcul de l’espace…" else null,
            servers = connections.associate { it.id to "Calcul de l’espace…" },
        )
        capacityJob?.cancel()
        capacityJob = viewModelScope.launch {
            val localCapacity = withContext(Dispatchers.IO) { localStorageCapacity() }
            mutableCapacityState.update {
                it.copy(
                    local = localCapacity?.let(::formatStorageCapacity) ?: "Espace indisponible",
                    localCapacity = localCapacity,
                )
            }

            coroutineScope {
                if (freeboxConfigured) launch {
                    val capacity = safeStorageCapacity { freeboxRepository.storageCapacity() }
                    mutableCapacityState.update {
                        it.copy(
                            freebox = capacity?.let(::formatStorageCapacity) ?: "Espace indisponible",
                            freeboxCapacity = capacity,
                        )
                    }
                }
                connections.forEach { connection ->
                    launch {
                        val capacity = safeStorageCapacity { serverRepository.storageCapacity(connection.id) }
                        mutableCapacityState.update { current ->
                            current.copy(
                                servers = current.servers + (
                                    connection.id to (capacity?.let(::formatStorageCapacity) ?: "Espace indisponible")
                                ),
                                serverCapacities = current.serverCapacities + (connection.id to capacity),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun localStorageCapacity(): StorageCapacity? = runCatching {
        val stats = StatFs(context.filesDir.absolutePath)
        StorageCapacity(stats.availableBytes, stats.totalBytes)
    }.getOrNull()

    private suspend fun safeStorageCapacity(read: suspend () -> StorageCapacity?): StorageCapacity? = try {
        read()
    } catch (error: CancellationException) {
        throw error
    } catch (_: Exception) {
        null
    }
}

private fun formatStorageCapacity(capacity: StorageCapacity): String {
    fun format(bytes: Long): String = when {
        bytes >= 1024L * 1024L * 1024L * 1024L -> {
            val value = String.format(Locale.FRANCE, "%.2f", bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0))
                .trimEnd('0')
                .trimEnd(',')
            "$value To"
        }
        bytes >= 1024L * 1024L * 1024L ->
            String.format(Locale.FRANCE, "%.1f Go", bytes / (1024.0 * 1024.0 * 1024.0))
        bytes >= 1024L * 1024L ->
            String.format(Locale.FRANCE, "%.0f Mo", bytes / (1024.0 * 1024.0))
        bytes >= 1024L -> String.format(Locale.FRANCE, "%.0f Ko", bytes / 1024.0)
        else -> "$bytes o"
    }
    val totalBytes = capacity.totalBytes.takeIf { it > 0L } ?: return "Capacité inconnue"
    val freeBytes = capacity.freeBytes.coerceIn(0L, totalBytes)
    return format(freeBytes) + " libres / " + format(totalBytes)
}
