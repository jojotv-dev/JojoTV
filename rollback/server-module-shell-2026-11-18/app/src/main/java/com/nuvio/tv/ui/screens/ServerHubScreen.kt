package com.nuvio.tv.ui.screens

import android.content.Context
import android.os.StatFs
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Storage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.data.freebox.FreeboxRepository
import com.nuvio.tv.data.mediaserver.ServerConnection
import com.nuvio.tv.data.mediaserver.ServerRepository
import com.nuvio.tv.data.storage.StorageCapacity
import com.nuvio.tv.ui.screens.settings.MediaServersViewModel
import com.nuvio.tv.ui.screens.settings.ServerSignInDialog
import com.nuvio.tv.ui.screens.settings.ServerSignInRequest
import com.nuvio.tv.ui.theme.NuvioTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
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
            val localLabel = withContext(Dispatchers.IO) { localStorageCapacityLabel() }
            mutableCapacityState.update { it.copy(local = localLabel) }

            coroutineScope {
                if (freeboxConfigured) launch {
                    val label = safeCapacityLabel { freeboxRepository.storageCapacity() }
                    mutableCapacityState.update { it.copy(freebox = label) }
                }
                connections.forEach { connection ->
                    launch {
                        val label = safeCapacityLabel { serverRepository.storageCapacity(connection.id) }
                        mutableCapacityState.update { current ->
                            current.copy(servers = current.servers + (connection.id to label))
                        }
                    }
                }
            }
        }
    }

    private fun localStorageCapacityLabel(): String = runCatching {
        val stats = StatFs(context.filesDir.absolutePath)
        formatStorageCapacity(StorageCapacity(stats.availableBytes, stats.totalBytes))
    }.getOrDefault("Espace indisponible")

    private suspend fun safeCapacityLabel(read: suspend () -> StorageCapacity?): String = try {
        read()?.let(::formatStorageCapacity) ?: "Espace indisponible"
    } catch (error: CancellationException) {
        throw error
    } catch (_: Exception) {
        "Espace indisponible"
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ServerHubScreen(
    viewModel: ServerHubViewModel = hiltViewModel(),
    serverViewModel: MediaServersViewModel = hiltViewModel(),
    onOpenLocal: () -> Unit,
    onOpenFreebox: () -> Unit,
    onOpenServer: (connectionId: String) -> Unit,
    onManageServers: () -> Unit,
    onBackPress: () -> Unit,
    contentFocusRequester: FocusRequester? = null,
) {
    val serverState by viewModel.servers.collectAsStateWithLifecycle()
    val freeboxState by viewModel.freebox.collectAsStateWithLifecycle()
    val capacityState by viewModel.capacityState.collectAsStateWithLifecycle()
    var signIn by remember { mutableStateOf<ServerSignInRequest?>(null) }
    val connections = remember(serverState) { serverState.enabledConnections }
    val capacityConnectionsKey = remember(connections) {
        connections.joinToString("|") { "${it.id}:${it.providerId}:${it.address}" }
    }

    BackHandler(onBack = onBackPress)
    androidx.compose.runtime.LaunchedEffect(capacityConnectionsKey, freeboxState) {
        viewModel.refreshStorageCapacity()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(NuvioTheme.colors.Background),
        contentPadding = PaddingValues(horizontal = 48.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = stringResource(R.string.server_hub_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = NuvioTheme.colors.TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Parcourez le stockage local, la Freebox et vos partages réseau.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NuvioTheme.colors.TextSecondary,
                )
            }
        }

        item(key = "add_server") {
            ServerHubCard(
                title = "Ajouter un serveur",
                subtitle = "Connecter un partage réseau SMB.",
                icon = Icons.Default.Dns,
                modifier = contentFocusRequester?.let { Modifier.focusRequester(it) } ?: Modifier,
                onClick = {
                    val smbProvider = serverViewModel.providers.firstOrNull { it.id == "smb" }
                        ?: serverViewModel.providers.firstOrNull()
                    if (smbProvider != null) signIn = ServerSignInRequest(smbProvider)
                },
            )
        }

        item(key = "local_storage") {
            ServerHubCard(
                title = "Local / USB",
                subtitle = "Parcourir un dossier de l’appareil ou d’un stockage USB.",
                storageText = capacityState.local,
                icon = Icons.Default.Folder,
                onClick = onOpenLocal,
            )
        }

        item(key = "freebox") {
            ServerHubCard(
                title = stringResource(R.string.server_hub_freebox),
                subtitle = if (freeboxState.address.isNotBlank()) {
                    "Parcourir les dossiers et fichiers de la Freebox."
                } else {
                    "Configurer la connexion Freebox dans les paramètres."
                },
                storageText = capacityState.freebox,
                icon = Icons.Default.Dns,
                onClick = if (freeboxState.address.isNotBlank()) onOpenFreebox else onManageServers,
            )
        }

        items(connections, key = { "server_${it.id}" }) { connection ->
            ServerHubCard(
                title = connection.name,
                subtitle = "${viewModel.providerName(connection)} · ${connection.address}",
                storageText = capacityState.servers[connection.id],
                icon = Icons.Default.Storage,
                onClick = { if (viewModel.hasProvider(connection)) onOpenServer(connection.id) else onManageServers() },
            )
        }
    }

    signIn?.let { request ->
        ServerSignInDialog(
            request = request,
            viewModel = serverViewModel,
            onConnected = { connection ->
                signIn = null
                onOpenServer(connection.id)
            },
            onDismiss = { signIn = null },
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ServerHubCard(
    title: String,
    subtitle: String,
    storageText: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(88.dp),
        colors = CardDefaults.colors(
            containerColor = NuvioTheme.colors.BackgroundCard,
            focusedContainerColor = NuvioTheme.colors.FocusBackground,
        ),
        shape = CardDefaults.shape(shape = NuvioTheme.configuredShape(RoundedCornerShape(14.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            androidx.tv.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NuvioTheme.colors.Secondary,
                modifier = Modifier.size(26.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = NuvioTheme.colors.TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = NuvioTheme.colors.TextSecondary,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                storageText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = NuvioTheme.colors.TextTertiary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

private fun formatStorageCapacity(capacity: StorageCapacity): String {
    fun format(bytes: Long): String = when {
        bytes >= 1024L * 1024L * 1024L * 1024L ->
            String.format(Locale.FRANCE, "%.1f To", bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0))
        bytes >= 1024L * 1024L * 1024L ->
            String.format(Locale.FRANCE, "%.1f Go", bytes / (1024.0 * 1024.0 * 1024.0))
        bytes >= 1024L * 1024L ->
            String.format(Locale.FRANCE, "%.0f Mo", bytes / (1024.0 * 1024.0))
        bytes >= 1024L -> String.format(Locale.FRANCE, "%.0f Ko", bytes / 1024.0)
        else -> "$bytes o"
    }
    val total = capacity.totalBytes.takeIf { it > 0L }?.let(::format) ?: "capacité inconnue"
    return "${format(capacity.freeBytes.coerceAtLeast(0L))} libres sur $total"
}
