package com.nuvio.tv.ui.screens.iptv
import androidx.compose.ui.draw.clip

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.nuvio.tv.ui.theme.NuvioColors
import com.streamvault.domain.usecase.M3uProviderSetupCommand
import com.streamvault.domain.usecase.StalkerProviderSetupCommand
import com.streamvault.domain.usecase.ValidateAndAddProviderResult
import com.streamvault.domain.usecase.XtreamProviderSetupCommand
import kotlinx.coroutines.launch

@Composable
fun IptvProviderSetupScreen(
    type: String = "xtream",
    providerId: Long? = null,
    onBackPress: () -> Unit,
    onProviderAdded: () -> Unit,
    viewModel: IptvProviderSetupViewModel = hiltViewModel()
) {
    val isEditMode = providerId != null
    val title = when (type) {
        "m3u" -> "M3U / URL"
        "stalker" -> "Stalker Portal"
        else -> "Xtream Codes"
    }

    Box(modifier = Modifier.fillMaxSize().background(NuvioColors.Background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 32.dp, bottom = 40.dp, start = 64.dp, end = 64.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 28.dp)
                ) {
                    ActionIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack, label = "Retour", onClick = onBackPress)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(title, color = NuvioColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        if (isEditMode) {
                            Text("Modification", color = NuvioColors.TextSecondary, fontSize = 13.sp)
                        }
                    }
                }
            }
            item {
                when (type) {
                    "m3u" -> M3uForm(viewModel = viewModel, providerId = providerId, onProviderAdded = onProviderAdded)
                    "stalker" -> StalkerForm(viewModel = viewModel, providerId = providerId, onProviderAdded = onProviderAdded)
                    else -> XtreamForm(viewModel = viewModel, providerId = providerId, onProviderAdded = onProviderAdded)
                }
            }
        }
    }
}

@Composable
private fun XtreamForm(viewModel: IptvProviderSetupViewModel, providerId: Long?, onProviderAdded: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var serverUrl by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingExisting by remember { mutableStateOf(providerId != null) }
    var resultMessage by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(providerId) {
        if (providerId != null) {
            val provider = viewModel.getProvider(providerId)
            if (provider != null) {
                name = provider.name
                serverUrl = provider.serverUrl
                username = provider.username
            }
            isLoadingExisting = false
        }
    }

    if (isLoadingExisting) {
        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            Text("Chargement...", color = NuvioColors.TextSecondary, fontSize = 14.sp)
        }
        return
    }

    FormColumn {
        SetupTextField("Nom", name, { name = it })
        SetupTextField("URL du serveur", serverUrl, { serverUrl = it }, "http://monserveur.com:8080", KeyboardType.Uri)
        SetupTextField("Nom d'utilisateur", username, { username = it })
        SetupTextField(
            if (providerId != null) "Mot de passe (laisser vide pour conserver)" else "Mot de passe",
            password, { password = it }, isPassword = true
        )
        Spacer(Modifier.height(8.dp))
        ResultBanner(resultMessage)
        Spacer(Modifier.height(12.dp))
        SubmitButton(
            label = if (isLoading) "Connexion..." else if (providerId != null) "Mettre a jour" else "Se connecter",
            enabled = !isLoading && name.isNotBlank() && serverUrl.isNotBlank() && username.isNotBlank()
        ) {
            scope.launch {
                isLoading = true
                resultMessage = null
                val command = XtreamProviderSetupCommand(serverUrl = serverUrl, username = username, password = password, name = name)
                val result = if (providerId != null) viewModel.updateXtream(providerId, command)
                             else viewModel.addXtream(command)
                isLoading = false
                when (result) {
                    is ValidateAndAddProviderResult.Success -> { viewModel.syncProviderInBackground(result.provider.id); onProviderAdded() }
                    is ValidateAndAddProviderResult.SavedWithWarning -> { viewModel.syncProviderInBackground(result.provider.id); onProviderAdded() }
                    is ValidateAndAddProviderResult.ValidationError -> resultMessage = false to result.message
                    is ValidateAndAddProviderResult.Error -> resultMessage = false to result.message
                }
            }
        }
    }
}

@Composable
private fun M3uForm(viewModel: IptvProviderSetupViewModel, providerId: Long?, onProviderAdded: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var url by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingExisting by remember { mutableStateOf(providerId != null) }
    var resultMessage by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(providerId) {
        if (providerId != null) {
            val provider = viewModel.getProvider(providerId)
            if (provider != null) {
                name = provider.name
                url = provider.m3uUrl
            }
            isLoadingExisting = false
        }
    }

    if (isLoadingExisting) {
        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            Text("Chargement...", color = NuvioColors.TextSecondary, fontSize = 14.sp)
        }
        return
    }

    FormColumn {
        SetupTextField("Nom", name, { name = it })
        SetupTextField("URL de la playlist M3U", url, { url = it }, "http://example.com/playlist.m3u", KeyboardType.Uri)
        Spacer(Modifier.height(8.dp))
        ResultBanner(resultMessage)
        Spacer(Modifier.height(12.dp))
        SubmitButton(
            label = if (isLoading) "Chargement..." else if (providerId != null) "Mettre a jour" else "Ajouter la playlist",
            enabled = !isLoading && name.isNotBlank() && url.isNotBlank()
        ) {
            scope.launch {
                isLoading = true
                resultMessage = null
                val command = M3uProviderSetupCommand(url = url, name = name)
                val result = if (providerId != null) viewModel.updateM3u(providerId, command)
                             else viewModel.addM3u(command)
                isLoading = false
                when (result) {
                    is ValidateAndAddProviderResult.Success -> { viewModel.syncProviderInBackground(result.provider.id); onProviderAdded() }
                    is ValidateAndAddProviderResult.SavedWithWarning -> { viewModel.syncProviderInBackground(result.provider.id); onProviderAdded() }
                    is ValidateAndAddProviderResult.ValidationError -> resultMessage = false to result.message
                    is ValidateAndAddProviderResult.Error -> resultMessage = false to result.message
                }
            }
        }
    }
}

@Composable
private fun StalkerForm(viewModel: IptvProviderSetupViewModel, providerId: Long?, onProviderAdded: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var portalUrl by rememberSaveable { mutableStateOf("") }
    var mac by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingExisting by remember { mutableStateOf(providerId != null) }
    var resultMessage by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(providerId) {
        if (providerId != null) {
            val provider = viewModel.getProvider(providerId)
            if (provider != null) {
                name = provider.name
                portalUrl = provider.serverUrl
                mac = provider.stalkerMacAddress
            }
            isLoadingExisting = false
        }
    }

    if (isLoadingExisting) {
        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            Text("Chargement...", color = NuvioColors.TextSecondary, fontSize = 14.sp)
        }
        return
    }

    FormColumn {
        SetupTextField("Nom", name, { name = it })
        SetupTextField("URL du portail", portalUrl, { portalUrl = it }, "http://portal.example.com/c/", KeyboardType.Uri)
        SetupTextField("Adresse MAC", mac, { mac = it }, "00:1A:79:XX:XX:XX")
        Spacer(Modifier.height(8.dp))
        ResultBanner(resultMessage)
        Spacer(Modifier.height(12.dp))
        SubmitButton(
            label = if (isLoading) "Connexion..." else if (providerId != null) "Mettre a jour" else "Se connecter au portail",
            enabled = !isLoading && name.isNotBlank() && portalUrl.isNotBlank() && mac.isNotBlank()
        ) {
            scope.launch {
                isLoading = true
                resultMessage = null
                val command = StalkerProviderSetupCommand(portalUrl = portalUrl, macAddress = mac, name = name)
                val result = if (providerId != null) viewModel.updateStalker(providerId, command)
                             else viewModel.addStalker(command)
                isLoading = false
                when (result) {
                    is ValidateAndAddProviderResult.Success -> { viewModel.syncProviderInBackground(result.provider.id); onProviderAdded() }
                    is ValidateAndAddProviderResult.SavedWithWarning -> { viewModel.syncProviderInBackground(result.provider.id); onProviderAdded() }
                    is ValidateAndAddProviderResult.ValidationError -> resultMessage = false to result.message
                    is ValidateAndAddProviderResult.Error -> resultMessage = false to result.message
                }
            }
        }
    }
}

@Composable
private fun FormColumn(content: @Composable () -> Unit) {
    Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(NuvioColors.BackgroundCard, RoundedCornerShape(16.dp)).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) { content() }
}

@Composable
private fun SetupTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.Primary else NuvioColors.Border,
        animationSpec = tween(150), label = "fieldBorder"
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = NuvioColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Box(
            modifier = Modifier
                .fillMaxWidth().height(44.dp)
                .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(NuvioColors.BackgroundElevated)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth().onFocusChanged { isFocused = it.isFocused },
                textStyle = TextStyle(color = NuvioColors.TextPrimary, fontSize = 14.sp),
                cursorBrush = SolidColor(NuvioColors.Primary),
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
                singleLine = true,
                decorationBox = { inner ->
                    Box {
                        if (value.isEmpty() && placeholder.isNotEmpty()) {
                            Text(placeholder, color = NuvioColors.TextTertiary, fontSize = 13.sp)
                        }
                        inner()
                    }
                }
            )
        }
    }
}

@Composable
private fun SubmitButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)
    val bgColor by animateColorAsState(
        targetValue = when { !enabled -> NuvioColors.Primary.copy(alpha = 0.4f); isFocused -> NuvioColors.Primary.copy(alpha = 0.85f); else -> NuvioColors.Primary },
        animationSpec = tween(150), label = "submitBg"
    )
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp).onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.97f)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(label, color = if (enabled) Color.White else NuvioColors.TextTertiary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ActionIconButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        targetValue = if (isFocused) NuvioColors.FocusBackground else NuvioColors.BackgroundCard,
        animationSpec = tween(120), label = "backBtnBg"
    )
    Card(
        onClick = onClick,
        modifier = Modifier.size(40.dp).onFocusChanged { isFocused = it.hasFocus },
        shape = CardDefaults.shape(RoundedCornerShape(10.dp)),
        colors = CardDefaults.colors(containerColor = NuvioColors.BackgroundCard, focusedContainerColor = NuvioColors.Secondary),
        border = CardDefaults.border(focusedBorder = Border(border = BorderStroke(2.dp, NuvioColors.FocusRing), shape = RoundedCornerShape(12.dp))),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 0.92f)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(icon, label, tint = NuvioColors.TextSecondary, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun ResultBanner(result: Pair<Boolean, String>?) {
    AnimatedVisibility(
        visible = result != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        result?.let { (isSuccess, message) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isSuccess) Color(0xFF1B5E20).copy(alpha = 0.4f) else Color(0xFF7F1010).copy(alpha = 0.4f),
                        RoundedCornerShape(10.dp)
                    )
                    .border(1.dp, if (isSuccess) Color(0xFF388E3C) else Color(0xFFEF5350), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                    null,
                    tint = if (isSuccess) Color(0xFF81C784) else Color(0xFFEF9A9A),
                    modifier = Modifier.size(18.dp)
                )
                Text(message, color = if (isSuccess) Color(0xFF81C784) else Color(0xFFEF9A9A), fontSize = 12.sp, lineHeight = 16.sp)
            }
        }
    }
}
