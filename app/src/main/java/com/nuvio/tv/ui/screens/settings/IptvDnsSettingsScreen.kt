package com.nuvio.tv.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.core.network.IptvDnsProvider
import com.nuvio.tv.core.network.IptvDnsProviders
import com.nuvio.tv.data.local.IptvSettingsDataStore
import com.nuvio.tv.ui.theme.NuvioColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun IptvDnsSettingsScreen(
    onBackPress: () -> Unit,
    viewModel: IptvDnsSettingsViewModel = hiltViewModel()
) {
    val selectedProviderId by viewModel.selectedProviderId.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NuvioColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 64.dp, vertical = 42.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackPress) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = NuvioColors.TextPrimary
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "DNS IPTV",
                        color = NuvioColors.TextPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Choisir le serveur DNS utilise par JojoTV pour les connexions IPTV.",
                        color = NuvioColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(IptvDnsProviders.all, key = { it.id }) { provider ->
                    DnsProviderRow(
                        provider = provider,
                        selected = provider.id == selectedProviderId,
                        onConnect = {
                            viewModel.connect(provider.id)
                            Toast.makeText(
                                context,
                                "Connecté à ${provider.name}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DnsProviderRow(
    provider: IptvDnsProvider,
    selected: Boolean,
    onConnect: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(14.dp)
    Card(
        onClick = onConnect,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 94.dp)
            .onFocusChanged { focused = it.hasFocus },
        colors = CardDefaults.colors(
            containerColor = NuvioColors.BackgroundCard,
            focusedContainerColor = NuvioColors.BackgroundElevated
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(2.dp, NuvioColors.FocusRing),
                shape = shape
            )
        ),
        shape = CardDefaults.shape(shape),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.98f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = if (selected) Icons.Default.CheckCircle else Icons.Default.Dns,
                contentDescription = null,
                tint = if (selected) Color(0xFF4CAF50) else NuvioColors.TextSecondary,
                modifier = Modifier.size(26.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = provider.name,
                        color = NuvioColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (selected) {
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Connecté",
                            color = Color(0xFF81C784),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = provider.addressLine(),
                    color = NuvioColors.TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = provider.note,
                    color = NuvioColors.TextTertiary,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Button(
                onClick = onConnect,
                colors = ButtonDefaults.colors(
                    containerColor = if (selected) Color(0xFF2E7D32) else NuvioColors.Secondary,
                    focusedContainerColor = NuvioColors.FocusRing
                )
            ) {
                Text(if (selected) "Connecté" else "Se connecter")
            }
        }
    }
}

private fun IptvDnsProvider.addressLine(): String =
    if (primary.isNullOrBlank()) {
        "DNS système"
    } else {
        listOfNotNull(primary, secondary).joinToString("  /  ")
    }

@HiltViewModel
class IptvDnsSettingsViewModel @Inject constructor(
    private val dataStore: IptvSettingsDataStore
) : ViewModel() {
    val selectedProviderId = dataStore.dnsSettings
        .map { settings -> IptvDnsProviders.byId(settings.providerId).id }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = IptvDnsProviders.SYSTEM_ID
        )

    fun connect(providerId: String) {
        viewModelScope.launch {
            dataStore.setDnsProvider(IptvDnsProviders.byId(providerId).id)
        }
    }
}
