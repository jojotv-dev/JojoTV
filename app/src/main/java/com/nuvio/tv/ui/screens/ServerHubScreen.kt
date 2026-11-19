package com.nuvio.tv.ui.screens

import android.content.Context
import android.os.StatFs
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Storage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.tv.material3.Card
import androidx.tv.material3.Border
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
    val localCapacity: StorageCapacity? = null,
    val freeboxCapacity: StorageCapacity? = null,
    val serverCapacities: Map<String, StorageCapacity?> = emptyMap(),
)

private data class ServerHubLocation(
    val id: String,
    val title: String,
    val subtitle: String,
    val typeLabel: String,
    val storageText: String?,
    val storageCapacity: StorageCapacity?,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit,
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
    val locations = buildList {
        add(
            ServerHubLocation(
                id = ServerBrowserViewModel.LOCAL_SOURCE,
                title = "Local / USB",
                subtitle = "Parcourir le stockage de l’appareil ou un disque USB.",
                typeLabel = "Stockage local",
                storageText = capacityState.local,
                storageCapacity = capacityState.localCapacity,
                icon = Icons.Default.Folder,
                onClick = onOpenLocal,
            ),
        )
        add(
            ServerHubLocation(
                id = ServerBrowserViewModel.FREEBOX_SOURCE,
                title = stringResource(R.string.server_hub_freebox),
                subtitle = if (freeboxState.address.isNotBlank()) {
                    "Parcourir les dossiers et fichiers de la Freebox."
                } else {
                    "Configurer la connexion Freebox dans les paramètres."
                },
                typeLabel = if (freeboxState.address.isNotBlank()) "Freebox" else "À configurer",
                storageText = capacityState.freebox,
                storageCapacity = capacityState.freeboxCapacity,
                icon = Icons.Default.Dns,
                onClick = if (freeboxState.address.isNotBlank()) onOpenFreebox else onManageServers,
            ),
        )
        connections.forEach { connection ->
            add(
                ServerHubLocation(
                    id = connection.id,
                    title = connection.name,
                    subtitle = "${viewModel.providerName(connection)} · ${connection.address}",
                    typeLabel = viewModel.providerName(connection),
                    storageText = capacityState.servers[connection.id],
                    storageCapacity = capacityState.serverCapacities[connection.id],
                    icon = Icons.Default.Storage,
                    onClick = {
                        if (viewModel.hasProvider(connection)) onOpenServer(connection.id)
                        else onManageServers()
                    },
                ),
            )
        }
    }
    var focusedLocationId by remember(freeboxState.address.isNotBlank()) {
        mutableStateOf(
            if (freeboxState.address.isNotBlank()) ServerBrowserViewModel.FREEBOX_SOURCE
            else ServerBrowserViewModel.LOCAL_SOURCE,
        )
    }
    val focusedLocation = locations.firstOrNull { it.id == focusedLocationId } ?: locations.first()
    val sidebarItems = locations.map { location ->
        ServerModuleNavEntry(
            id = location.id,
            label = location.title,
            icon = location.icon,
            onClick = location.onClick,
        )
    }

    BackHandler(onBack = onBackPress)
    androidx.compose.runtime.LaunchedEffect(capacityConnectionsKey, freeboxState) {
        viewModel.refreshStorageCapacity()
    }

    ServerModuleScaffold(
        navEntries = sidebarItems,
        selectedNavId = "home",
        storageTitle = "Stockage ${focusedLocation.title}",
        storageText = focusedLocation.storageText,
        storageCapacity = focusedLocation.storageCapacity,
        onNavigateHome = {},
        sidebarDetailsContent = {
            Text(
                text = "EMPLACEMENT",
                color = NuvioTheme.colors.TextTertiary,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = focusedLocation.title,
                color = NuvioTheme.colors.TextPrimary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
            Text(
                text = focusedLocation.subtitle,
                color = NuvioTheme.colors.TextSecondary,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
            ServerModuleInfoRow("Type", focusedLocation.typeLabel)
        },
        mainContent = {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Emplacements",
                        style = MaterialTheme.typography.titleLarge,
                        color = NuvioTheme.colors.TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "${locations.size} disponibles",
                        style = MaterialTheme.typography.bodySmall,
                        color = NuvioTheme.colors.TextSecondary,
                    )
                    ServerModuleActionButton(
                        label = "Ajouter un serveur",
                        icon = Icons.Default.Add,
                        onClick = {
                            val smbProvider = serverViewModel.providers.firstOrNull { it.id == "smb" }
                                ?: serverViewModel.providers.firstOrNull()
                            if (smbProvider != null) signIn = ServerSignInRequest(smbProvider)
                        },
                        modifier = Modifier.width(178.dp),
                        emphasized = true,
                    )
                }
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 168.dp),
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(locations, key = ServerHubLocation::id) { location ->
                        ServerHubLocationCard(
                            location = location,
                            selected = focusedLocation.id == location.id,
                            focusRequester = contentFocusRequester.takeIf { location.id == locations.first().id },
                            onFocused = { focusedLocationId = location.id },
                            onClick = {
                                focusedLocationId = location.id
                                location.onClick()
                            },
                        )
                    }
                }
            }
        },
    )

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
private fun ServerHubLocationCard(
    location: ServerHubLocation,
    selected: Boolean,
    focusRequester: FocusRequester?,
    onFocused: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = NuvioTheme.configuredShape(RoundedCornerShape(16.dp))
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(190.dp)
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .onFocusChanged {
                if (it.isFocused) onFocused()
            },
        colors = CardDefaults.colors(
            containerColor = NuvioTheme.colors.Secondary,
            focusedContainerColor = NuvioTheme.colors.SecondaryVariant,
        ),
        border = CardDefaults.border(
            border = if (selected) Border(BorderStroke(2.dp, NuvioTheme.colors.FocusRing)) else Border.None,
            focusedBorder = Border(BorderStroke(2.dp, NuvioTheme.colors.FocusRing)),
        ),
        shape = CardDefaults.shape(shape = shape),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(NuvioTheme.configuredShape(RoundedCornerShape(14.dp)))
                    .background(NuvioTheme.colors.OnSecondary.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                androidx.tv.material3.Icon(
                    imageVector = location.icon,
                    contentDescription = null,
                    tint = NuvioTheme.colors.OnSecondary,
                    modifier = Modifier.size(32.dp),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = location.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = NuvioTheme.colors.OnSecondary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                Text(
                    text = location.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = NuvioTheme.colors.OnSecondary.copy(alpha = 0.86f),
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                location.storageText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = NuvioTheme.colors.OnSecondary.copy(alpha = 0.9f),
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
    val used = format((totalBytes - freeBytes).coerceAtLeast(0L))
    return "$used / ${format(totalBytes)}"
}
