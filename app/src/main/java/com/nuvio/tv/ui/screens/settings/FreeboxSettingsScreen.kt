@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.nuvio.tv.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.ui.theme.NuvioColors

@Composable
internal fun FreeboxSettingsContent(
    initialFocusRequester: FocusRequester?,
    viewModel: FreeboxSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SettingsDetailHeader(
            title = stringResource(R.string.freebox_settings_title),
            subtitle = stringResource(R.string.freebox_settings_section_subtitle)
        )

        val listState = rememberLazyListState()
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "freebox_connection_form") {
                    SettingsGroupCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.freebox_connection_form_title),
                        subtitle = stringResource(R.string.freebox_connection_form_subtitle)
                    ) {
                        FreeboxInputField(
                            value = uiState.name,
                            onValueChange = viewModel::setName,
                            placeholder = stringResource(R.string.freebox_field_name),
                            modifier = if (initialFocusRequester != null) Modifier.focusRequester(initialFocusRequester) else Modifier,
                            imeAction = ImeAction.Next
                        )
                        FreeboxInputField(
                            value = uiState.address,
                            onValueChange = viewModel::setAddress,
                            placeholder = stringResource(R.string.freebox_field_address),
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Next
                        )
                        FreeboxInputField(
                            value = uiState.username,
                            onValueChange = viewModel::setUsername,
                            placeholder = stringResource(R.string.freebox_field_username),
                            imeAction = ImeAction.Next
                        )
                        FreeboxInputField(
                            value = uiState.password,
                            onValueChange = viewModel::setPassword,
                            placeholder = stringResource(R.string.freebox_field_password),
                            isPassword = true
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FreeboxButton(
                                text = stringResource(R.string.freebox_action_save),
                                enabled = uiState.canSave && !uiState.isLoading,
                                onClick = viewModel::save
                            )
                            FreeboxButton(
                                text = stringResource(R.string.freebox_action_authorize),
                                enabled = uiState.canSave && !uiState.isLoading,
                                onClick = viewModel::requestAuthorization
                            )
                            FreeboxButton(
                                text = stringResource(R.string.freebox_action_check_authorization),
                                enabled = uiState.hasAppToken && !uiState.isLoading,
                                onClick = viewModel::refreshAuthorizationStatus
                            )
                        }

                        if (uiState.saved) {
                            Text(
                                text = stringResource(R.string.freebox_save_success),
                                style = MaterialTheme.typography.bodySmall,
                                color = NuvioColors.TextSecondary
                            )
                        }
                        uiState.statusMessage?.let { message ->
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodySmall,
                                color = NuvioColors.TextSecondary
                            )
                        }
                    }
                }

                item(key = "freebox_display_options") {
                    SettingsGroupCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.freebox_display_options_title),
                        subtitle = stringResource(R.string.freebox_display_options_subtitle)
                    ) {
                        SettingsToggleRow(
                            title = stringResource(R.string.freebox_show_in_sidebar),
                            subtitle = stringResource(R.string.freebox_show_in_sidebar_subtitle),
                            checked = uiState.showInSidebar,
                            onToggle = { viewModel.setShowInSidebar(!uiState.showInSidebar) },
                            enabled = uiState.hasSavedConnection
                        )
                        SettingsToggleRow(
                            title = stringResource(R.string.freebox_show_source_folder),
                            subtitle = stringResource(R.string.freebox_show_source_folder_subtitle),
                            checked = uiState.showSourceFolder,
                            onToggle = { viewModel.setShowSourceFolder(!uiState.showSourceFolder) }
                        )
                        SettingsToggleRow(
                            title = stringResource(R.string.freebox_show_extensions),
                            subtitle = stringResource(R.string.freebox_show_extensions_subtitle),
                            checked = uiState.showExtensions,
                            onToggle = { viewModel.setShowExtensions(!uiState.showExtensions) }
                        )
                        SettingsToggleRow(
                            title = "Afficher les fichiers cachés",
                            subtitle = "Affiche les fichiers et dossiers commençant par un point",
                            checked = uiState.showHiddenFiles,
                            onToggle = { viewModel.setShowHiddenFiles(!uiState.showHiddenFiles) }
                        )
                    }
                }

                if (uiState.serverFolders.isNotEmpty()) {
                    item(key = "freebox_server_folders") {
                        SettingsGroupCard(
                            modifier = Modifier.fillMaxWidth(),
                            title = stringResource(R.string.freebox_server_folders_title),
                            subtitle = stringResource(R.string.freebox_server_folders_subtitle)
                        ) {
                            uiState.serverFolders.forEach { folderName ->
                                val visibleInFreebox = folderName in uiState.visibleServerFolders
                                val visibleInSidebar = folderName in uiState.sidebarServerFolders
                                FreeboxFolderVisibilityRow(
                                    folderName = folderName,
                                    visibleInFreebox = visibleInFreebox,
                                    onToggleFreebox = { viewModel.setServerFolderVisible(folderName, !visibleInFreebox) },
                                    visibleInSidebar = visibleInSidebar,
                                    onToggleSidebar = { viewModel.setServerFolderInSidebar(folderName, !visibleInSidebar) },
                                    sidebarEnabled = uiState.showInSidebar
                                )
                            }
                        }
                    }
                }
            }
            SettingsVerticalScrollIndicators(state = listState)
        }
    }
}

@Composable
private fun FreeboxFolderVisibilityRow(
    folderName: String,
    visibleInFreebox: Boolean,
    onToggleFreebox: () -> Unit,
    visibleInSidebar: Boolean,
    onToggleSidebar: () -> Unit,
    sidebarEnabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .padding(top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = folderName,
                style = MaterialTheme.typography.bodyLarge,
                color = NuvioColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.freebox_folder_visibility_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = NuvioColors.TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        FreeboxFolderToggleChip(
            label = stringResource(R.string.freebox_folder_show_in_freebox_short),
            checked = visibleInFreebox,
            onToggle = onToggleFreebox
        )
        FreeboxFolderToggleChip(
            label = stringResource(R.string.freebox_folder_show_in_sidebar_short),
            checked = visibleInSidebar,
            onToggle = onToggleSidebar,
            enabled = sidebarEnabled
        )
    }
}

@Composable
private fun FreeboxFolderToggleChip(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit,
    enabled: Boolean = true
) {
    val contentAlpha = if (enabled) 1f else 0.4f

    Card(
        onClick = { if (enabled) onToggle() },
        modifier = Modifier
            .width(168.dp)
            .heightIn(min = 58.dp),
        colors = CardDefaults.colors(
            containerColor = NuvioColors.Background,
            focusedContainerColor = NuvioColors.Background
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(2.dp, NuvioColors.FocusRing.copy(alpha = contentAlpha)),
                shape = RoundedCornerShape(18.dp)
            )
        ),
        shape = CardDefaults.shape(RoundedCornerShape(18.dp)),
        scale = CardDefaults.scale(focusedScale = 1f, pressedScale = 1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = NuvioColors.TextPrimary.copy(alpha = contentAlpha),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            FreeboxTogglePill(
                checked = checked,
                enabled = enabled
            )
        }
    }
}

@Composable
private fun FreeboxTogglePill(
    checked: Boolean,
    enabled: Boolean
) {
    val backgroundColor = when {
        checked && enabled -> NuvioColors.FocusBackground
        checked -> NuvioColors.FocusBackground.copy(alpha = 0.45f)
        enabled -> Color.DarkGray
        else -> Color.DarkGray.copy(alpha = 0.45f)
    }

    Box(
        modifier = Modifier
            .width(48.dp)
            .height(28.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 4.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    color = NuvioColors.TextPrimary.copy(alpha = if (enabled) 1f else 0.55f),
                    shape = RoundedCornerShape(10.dp)
                )
        )
    }
}

@Composable
private fun FreeboxButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.colors(
            containerColor = NuvioColors.FocusBackground,
            contentColor = NuvioColors.TextPrimary,
            disabledContainerColor = NuvioColors.Background,
            disabledContentColor = NuvioColors.TextTertiary
        )
    ) {
        Text(text = text)
    }
}

@Composable
private fun FreeboxInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    imeAction: ImeAction = ImeAction.Done
) {
    val textFieldFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(isEditing) {
        if (isEditing) {
            textFieldFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Surface(
        onClick = { isEditing = true },
        modifier = modifier.fillMaxWidth(),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = NuvioColors.Background,
            focusedContainerColor = NuvioColors.Background
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, NuvioColors.Border),
                shape = RoundedCornerShape(12.dp)
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, NuvioColors.FocusRing),
                shape = RoundedCornerShape(12.dp)
            )
        ),
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(12.dp)),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .focusRequester(textFieldFocusRequester)
                .onFocusChanged {
                    if (!it.isFocused && isEditing) {
                        isEditing = false
                        keyboardController?.hide()
                    }
                },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    isEditing = false
                    keyboardController?.hide()
                },
                onNext = {
                    isEditing = false
                    keyboardController?.hide()
                }
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = NuvioColors.TextPrimary
            ),
            cursorBrush = SolidColor(if (isEditing) NuvioColors.Secondary else Color.Transparent),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NuvioColors.TextTertiary
                    )
                }
                innerTextField()
            }
        )
    }
}


