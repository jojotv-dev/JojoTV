package com.nuvio.tv.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.nuvio.tv.youtube.YouTubeContentRegion
import com.nuvio.tv.youtube.YouTubeSettings
import com.nuvio.tv.youtube.YtTab
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.ui.theme.NuvioTheme
import com.nuvio.tv.youtube.YouTubeAccountViewModel

/** Settings → YouTube: sign in with a code from youtube.com/activate, or sign out. */
@Composable
internal fun YouTubeSettingsContent(
    viewModel: YouTubeAccountViewModel = hiltViewModel(),
    initialFocusRequester: FocusRequester? = null
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.refresh() }

    val context = LocalContext.current
    var sponsorOn by remember { mutableStateOf(YouTubeSettings.sponsorBlockEnabled(context)) }
    var categories by remember { mutableStateOf(YouTubeSettings.sponsorCategoriesOn(context)) }
    var quality by remember { mutableStateOf(YouTubeSettings.preferredHeight(context)) }
    var hiddenTabs by remember { mutableStateOf(YouTubeSettings.hiddenTabs(context)) }
    var autoplay by remember { mutableStateOf(YouTubeSettings.autoplayNext(context)) }
    var autoSubtitles by remember { mutableStateOf(YouTubeSettings.autoSubtitles(context)) }
    var videoPreviews by remember { mutableStateOf(YouTubeSettings.videoPreviews(context)) }
    var sponsorAsk by remember { mutableStateOf(YouTubeSettings.sponsorAsk(context)) }
    var sponsorNotice by remember { mutableStateOf(YouTubeSettings.sponsorNotice(context)) }
    var deTitles by remember { mutableStateOf(YouTubeSettings.deArrowTitles(context)) }
    var tabOrder by remember { mutableStateOf(YouTubeSettings.tabOrder(context)) }
    var reordering by remember { mutableStateOf(false) }
    var rememberTab by remember { mutableStateOf(YouTubeSettings.rememberLastTab(context)) }
    var deThumbs by remember { mutableStateOf(YouTubeSettings.deArrowThumbnails(context)) }
    var hiddenWords by remember { mutableStateOf(YouTubeSettings.hiddenKeywords(context)) }
    var hiddenChannels by remember { mutableStateOf(YouTubeSettings.hiddenChannels(context)) }
    var newWord by remember { mutableStateOf("") }
    var addingWord by remember { mutableStateOf(false) }
    var hideShorts by remember { mutableStateOf(YouTubeSettings.hideShorts(context)) }
    var hideWatched by remember { mutableStateOf(YouTubeSettings.hideWatched(context)) }
    var hideUpcoming by remember { mutableStateOf(YouTubeSettings.hideUpcoming(context)) }
    var codec by remember { mutableStateOf(YouTubeSettings.codecPreference(context)) }
    var compatibility by remember { mutableStateOf(YouTubeSettings.compatibilityMode(context)) }
    var contentRegion by remember { mutableStateOf(YouTubeSettings.contentRegion(context)) }
    var showContentRegion by remember { mutableStateOf(false) }
    var posterColumns by remember { mutableStateOf(YouTubeSettings.posterColumns(context)) }
    var clockOn by remember { mutableStateOf(YouTubeSettings.showClock(context)) }
    val syncedSettingsVersion by YouTubeSettings.settingsVersion.collectAsState()

    LaunchedEffect(syncedSettingsVersion) {
        if (syncedSettingsVersion == 0) return@LaunchedEffect
        sponsorOn = YouTubeSettings.sponsorBlockEnabled(context)
        categories = YouTubeSettings.sponsorCategoriesOn(context)
        quality = YouTubeSettings.preferredHeight(context)
        hiddenTabs = YouTubeSettings.hiddenTabs(context)
        autoplay = YouTubeSettings.autoplayNext(context)
        autoSubtitles = YouTubeSettings.autoSubtitles(context)
        videoPreviews = YouTubeSettings.videoPreviews(context)
        sponsorAsk = YouTubeSettings.sponsorAsk(context)
        sponsorNotice = YouTubeSettings.sponsorNotice(context)
        deTitles = YouTubeSettings.deArrowTitles(context)
        tabOrder = YouTubeSettings.tabOrder(context)
        rememberTab = YouTubeSettings.rememberLastTab(context)
        deThumbs = YouTubeSettings.deArrowThumbnails(context)
        hiddenWords = YouTubeSettings.hiddenKeywords(context)
        hiddenChannels = YouTubeSettings.hiddenChannels(context)
        hideShorts = YouTubeSettings.hideShorts(context)
        hideWatched = YouTubeSettings.hideWatched(context)
        hideUpcoming = YouTubeSettings.hideUpcoming(context)
        codec = YouTubeSettings.codecPreference(context)
        compatibility = YouTubeSettings.compatibilityMode(context)
        contentRegion = YouTubeSettings.contentRegion(context)
        posterColumns = YouTubeSettings.posterColumns(context)
        clockOn = YouTubeSettings.showClock(context)
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md)
    ) {
        SettingsDetailHeader(
            title = "YouTube",
            subtitle = "Connectez-vous pour retrouver vos abonnements et votre historique de visionnage sur YouTube."
        )

        SettingsGroupCard(title = "Compte") {
            val first = if (initialFocusRequester != null) Modifier.focusRequester(initialFocusRequester) else Modifier
            when {
                state.code != null -> {
                    Text(
                        text = "Sur votre téléphone ou votre ordinateur, rendez-vous sur ${state.url} et saisissez le code :",
                        style = MaterialTheme.typography.bodyLarge,
                        color = NuvioTheme.colors.TextSecondary,
                        modifier = Modifier.padding(vertical = NuvioTheme.spacing.sm)
                    )
                    Text(
                        text = state.code.orEmpty(),
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                        color = NuvioTheme.colors.TextPrimary,
                        modifier = Modifier.fillMaxWidth().padding(vertical = NuvioTheme.spacing.sm)
                    )
                    SettingsActionRow(
                        title = "Annuler",
                        subtitle = "En attente de votre confirmation sur l’autre appareil",
                        onClick = viewModel::cancelSignIn,
                        modifier = first
                    )
                }
                state.signedIn -> {
                    SettingsActionRow(
                        title = "Connecté",
                        subtitle = state.accountName ?: "Votre compte YouTube est connecté",
                        onClick = {},
                        enabled = false
                    )
                    SettingsActionRow(
                        title = "Se déconnecter",
                        subtitle = "Supprime le compte de cet appareil",
                        onClick = viewModel::signOut,
                        leadingIcon = Icons.Default.Logout,
                        modifier = first
                    )
                }
                else -> {
                    SettingsActionRow(
                        title = "Se connecter",
                        subtitle = state.error ?: "Obtenez un code à saisir sur youtube.com/activate",
                        onClick = viewModel::startSignIn,
                        leadingIcon = Icons.Default.Login,
                        modifier = first
                    )
                }
            }
        }

        SettingsGroupCard(title = "En-tête") {
            SettingsToggleRow(
                title = "Horloge",
                subtitle = "Afficher l’heure dans l’en-tête YouTube",
                checked = clockOn,
                onToggle = {
                    clockOn = !clockOn
                    YouTubeSettings.setShowClock(context, clockOn)
                }
            )
        }

        SettingsGroupCard(title = "Lecture") {
            SettingsToggleRow(
                title = "Aperçus avec son",
                subtitle = "Lire un aperçu lorsque vous vous arrêtez sur une vidéo YouTube",
                checked = videoPreviews,
                onToggle = {
                    videoPreviews = !videoPreviews
                    YouTubeSettings.setVideoPreviews(context, videoPreviews)
                }
            )
            SettingsToggleRow(
                title = "Choisir automatiquement les sous-titres",
                subtitle = "Activer vos sous-titres préférés au démarrage des vidéos, s’ils sont disponibles",
                checked = autoSubtitles,
                onToggle = {
                    autoSubtitles = !autoSubtitles
                    YouTubeSettings.setAutoSubtitles(context, autoSubtitles)
                }
            )
            SettingsActionRow(
                title = "Région du contenu",
                subtitle = "Pays utilisé pour les vidéos des rubriques Actualités et En direct. Le mode automatique reprend celui de cet appareil.",
                value = contentRegionLabel(contentRegion),
                onClick = { showContentRegion = true }
            )
            SettingsActionRow(
                title = "Vignettes par ligne",
                subtitle = "Nombre de vidéos affichées sur chaque ligne ou grille YouTube",
                value = "$posterColumns",
                onClick = {
                    posterColumns = if (posterColumns >= 5) 3 else posterColumns + 1
                    YouTubeSettings.setPosterColumns(context, posterColumns)
                }
            )
            SettingsToggleRow(
                title = "Lire automatiquement la vidéo suivante",
                subtitle = "À la fin d’une vidéo, lire une vidéo similaire ou la suivante dans la liste",
                checked = autoplay,
                onToggle = {
                    autoplay = !autoplay
                    YouTubeSettings.setAutoplayNext(context, autoplay)
                }
            )
            SettingsActionRow(
                title = "Compatibilité de l’appareil",
                subtitle = YouTubeSettings.compatibilityDescription(context, compatibility),
                value = YouTubeSettings.compatibilityLabel(context, compatibility),
                onClick = {
                    val next = when (compatibility) {
                        "auto" -> "on"
                        "on" -> "off"
                        else -> "auto"
                    }
                    YouTubeSettings.setCompatibilityMode(context, next)
                    compatibility = next
                }
            )
            SettingsActionRow(
                title = "Format vidéo",
                subtitle = if (YouTubeSettings.compatibilityActive(context))
                    "Le mode automatique utilise H.264 sur cet appareil pour une lecture plus fluide. Choisissez H.264 si l’image saccade."
                else "Le mode automatique privilégie VP9, puis H.264, et AV1 en dernier. Choisissez H.264 si l’image saccade.",
                value = YouTubeSettings.codecLabel(codec),
                onClick = {
                    val before = compatibility
                    val choices = YouTubeSettings.codecChoices
                    codec = choices[(choices.indexOf(codec).coerceAtLeast(0) + 1) % choices.size]
                    YouTubeSettings.setCodecPreference(context, codec)
                    YouTubeSettings.onUserSelectedCodec(context, codec)
                    compatibility = YouTubeSettings.compatibilityMode(context)
                    if (before != "off" && compatibility == "off") {
                        Toast.makeText(
                            context,
                            "Mode de compatibilité désactivé pour profiter de toute la qualité vidéo",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            )
            SettingsActionRow(
                title = "Qualité par défaut",
                subtitle = if (quality <= 0 && YouTubeSettings.compatibilityActive(context))
                    "Le mode automatique démarre actuellement en ${YouTubeSettings.compatibilityMaxHeight(context)}p sur cet appareil. Vous pouvez toujours modifier la qualité dans le lecteur."
                else "Qualité de départ de la vidéo. Vous pouvez toujours la modifier dans le lecteur.",
                value = YouTubeSettings.qualityLabel(quality),
                onClick = {
                    val before = compatibility
                    val choices = YouTubeSettings.qualityCycle(context)
                    quality = choices[(choices.indexOf(quality).coerceAtLeast(0) + 1) % choices.size]
                    YouTubeSettings.setPreferredHeight(context, quality)
                    YouTubeSettings.onUserSelectedQuality(context, quality)
                    compatibility = YouTubeSettings.compatibilityMode(context)
                    when {
                        before != "off" && compatibility == "off" ->
                            Toast.makeText(
                                context,
                                "Mode de compatibilité désactivé pour obtenir une meilleure qualité",
                                Toast.LENGTH_SHORT,
                            ).show()
                        before == "off" && compatibility == "auto" && quality <= 0 ->
                            Toast.makeText(
                                context,
                                "Mode de compatibilité rétabli sur Automatique",
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                }
            )
        }

        SettingsGroupCard(
            title = "DeArrow",
            subtitle = "Remplace les titres racoleurs par des titres et miniatures proposés par la communauté. Le changement s’applique au prochain chargement d’une page."
        ) {
            SettingsToggleRow(
                title = "Titres améliorés",
                subtitle = "Utiliser les titres proposés par la communauté lorsqu’ils existent",
                checked = deTitles,
                onToggle = {
                    deTitles = !deTitles
                    YouTubeSettings.setDeArrowTitles(context, deTitles)
                }
            )
            SettingsToggleRow(
                title = "Miniatures améliorées",
                subtitle = "Utiliser des images extraites de la vidéo",
                checked = deThumbs,
                onToggle = {
                    deThumbs = !deThumbs
                    YouTubeSettings.setDeArrowThumbnails(context, deThumbs)
                }
            )
        }

        SettingsGroupCard(
            title = "Flux",
            subtitle = "Masquez des contenus sur la page YouTube. L’historique affiche toujours tout ce que vous avez regardé."
        ) {
            SettingsToggleRow(
                title = "Masquer les Shorts",
                subtitle = "Courtes vidéos verticales",
                checked = hideShorts,
                onToggle = {
                    hideShorts = !hideShorts
                    YouTubeSettings.setHideShorts(context, hideShorts)
                }
            )
            SettingsToggleRow(
                title = "Masquer les vidéos déjà vues",
                subtitle = "Vidéos que vous avez presque terminées, sur cet appareil ou avec votre compte",
                checked = hideWatched,
                onToggle = {
                    hideWatched = !hideWatched
                    YouTubeSettings.setHideWatched(context, hideWatched)
                }
            )
            SettingsToggleRow(
                title = "Masquer les vidéos à venir",
                subtitle = "Premières et diffusions qui n’ont pas encore commencé et ne peuvent pas être lues",
                checked = hideUpcoming,
                onToggle = {
                    hideUpcoming = !hideUpcoming
                    YouTubeSettings.setHideUpcoming(context, hideUpcoming)
                }
            )
        }

        SettingsGroupCard(
            title = "Mots et chaînes masqués",
            subtitle = "Masquer de YouTube les vidéos dont le titre ou la chaîne contient ces mots, ainsi que les chaînes sélectionnées",
        ) {
            // The keyboard only opens once you choose to type, not when you scroll past the field.
            if (!addingWord) {
                SettingsActionRow(
                    title = "Ajouter un mot ou une expression",
                    subtitle = "Appuyez sur OK pour saisir le texte",
                    onClick = { addingWord = true }
                )
            } else {
                val wordFocus = remember { FocusRequester() }
                val keyboard = LocalSoftwareKeyboardController.current
                LaunchedEffect(Unit) {
                    runCatching { wordFocus.requestFocus() }
                    keyboard?.show()
                }
                // Back puts the box away without adding anything.
                BackHandler {
                    newWord = ""
                    addingWord = false
                }
                OutlinedTextField(
                    value = newWord,
                    onValueChange = { newWord = it },
                    singleLine = true,
                    label = { Text("Saisissez un mot ou une expression, puis validez") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        YouTubeSettings.addHiddenKeyword(context, newWord)
                        hiddenWords = YouTubeSettings.hiddenKeywords(context)
                        newWord = ""
                        addingWord = false
                        keyboard?.hide()
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NuvioTheme.colors.TextPrimary,
                        unfocusedTextColor = NuvioTheme.colors.TextPrimary,
                        cursorColor = NuvioTheme.colors.FocusRing,
                        focusedBorderColor = NuvioTheme.colors.FocusRing,
                        unfocusedBorderColor = NuvioTheme.colors.Border,
                        focusedLabelColor = NuvioTheme.colors.FocusRing,
                        unfocusedLabelColor = NuvioTheme.colors.TextTertiary
                    ),
                    modifier = Modifier.fillMaxWidth().padding(vertical = NuvioTheme.spacing.xs).focusRequester(wordFocus)
                )
            }
            hiddenWords.forEach { word ->
                SettingsActionRow(
                    title = word,
                    subtitle = "Appuyez sur OK pour afficher de nouveau ce mot",
                    onClick = {
                        YouTubeSettings.removeHiddenKeyword(context, word)
                        hiddenWords = YouTubeSettings.hiddenKeywords(context)
                    }
                )
            }
            hiddenChannels.forEach { (id, name) ->
                SettingsActionRow(
                    title = name,
                    subtitle = "Chaîne masquée. Appuyez sur OK pour l’afficher de nouveau",
                    onClick = {
                        YouTubeSettings.unhideChannel(context, id)
                        hiddenChannels = YouTubeSettings.hiddenChannels(context)
                    }
                )
            }
            if (hiddenWords.isEmpty() && hiddenChannels.isEmpty()) {
                Text(
                    text = "Aucune chaîne masquée. Masquez-en une depuis le panneau YouTube du lecteur.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NuvioTheme.colors.TextSecondary,
                    modifier = Modifier.padding(vertical = NuvioTheme.spacing.sm)
                )
            }
        }

        SettingsGroupCard(
            title = "Onglets",
            subtitle = "Choisissez les onglets affichés en haut de la page YouTube"
        ) {
            SettingsToggleRow(
                title = "Ouvrir sur le dernier onglet utilisé",
                subtitle = "Désactivé : la page s’ouvre toujours sur le premier onglet",
                checked = rememberTab,
                onToggle = {
                    rememberTab = !rememberTab
                    YouTubeSettings.setRememberLastTab(context, rememberTab)
                }
            )
            SettingsActionRow(
                title = "Modifier l’ordre",
                subtitle = "Sélectionnez un onglet, puis déplacez-le vers le haut ou le bas",
                onClick = { reordering = true }
            )
            if (reordering) {
                TabOrderEditor(
                    order = tabOrder,
                    hidden = hiddenTabs,
                    onChange = {
                        tabOrder = it
                        YouTubeSettings.setTabOrder(context, it)
                    },
                    onDone = { reordering = false }
                )
            }
            // The switches show in your order, except while you're moving tabs around.
            tabOrder.filter { !reordering }.forEach { tab ->
                val shown = tab.name !in hiddenTabs
                val onlyOneLeft = shown && YtTab.entries.count { it.name !in hiddenTabs } <= 1
                SettingsToggleRow(
                    title = tab.title,
                    subtitle = when {
                        onlyOneLeft -> "Au moins un onglet doit rester affiché"
                        tab.needsSignIn -> "Nécessite un compte connecté"
                        else -> null
                    },
                    checked = shown,
                    enabled = !onlyOneLeft,
                    onToggle = {
                        YouTubeSettings.setTabVisible(context, tab, !shown)
                        hiddenTabs = YouTubeSettings.hiddenTabs(context)
                    }
                )
            }
        }

        SettingsGroupCard(
            title = "SponsorBlock",
            subtitle = "Ignore les passages signalés par les spectateurs, comme les messages publicitaires"
        ) {
            SettingsToggleRow(
                title = "Ignorer les passages signalés",
                subtitle = "Passe ces passages et indique ce qui a été ignoré",
                checked = sponsorOn,
                onToggle = {
                    sponsorOn = !sponsorOn
                    YouTubeSettings.setSponsorBlockEnabled(context, sponsorOn)
                }
            )
            if (sponsorOn) {
                SettingsToggleRow(
                    title = "Demander avant de passer",
                    subtitle = "Affiche une carte au début d’un passage signalé ; appuyez sur OK pour le passer",
                    checked = sponsorAsk,
                    onToggle = {
                        sponsorAsk = !sponsorAsk
                        YouTubeSettings.setSponsorAsk(context, sponsorAsk)
                    }
                )
                SettingsToggleRow(
                    title = "Afficher une notification lors du passage",
                    subtitle = "Affiche un court message, par exemple « Passage ignoré : publicité »",
                    checked = sponsorNotice,
                    onToggle = {
                        sponsorNotice = !sponsorNotice
                        YouTubeSettings.setSponsorNotice(context, sponsorNotice)
                    }
                )
            }
            if (sponsorOn) {
                YouTubeSettings.sponsorCategories.forEach { (key, label) ->
                    SettingsToggleRow(
                        title = label,
                        subtitle = null,
                        checked = key in categories,
                        onToggle = {
                            val on = key !in categories
                            YouTubeSettings.setSponsorCategory(context, key, on)
                            categories = YouTubeSettings.sponsorCategoriesOn(context)
                        }
                    )
                }
            }
        }
    }

    if (showContentRegion) {
        SettingsSingleChoiceDialog(
            title = "Région du contenu",
            subtitle = "Pays utilisé pour les vidéos des rubriques Actualités et En direct",
            options = contentRegionChoices(),
            selectedValue = contentRegion,
            onOptionSelected = { region ->
                contentRegion = region
                YouTubeSettings.setContentRegion(context, region)
                YouTubeContentRegion.apply(context)
                showContentRegion = false
            },
            onDismiss = { showContentRegion = false }
        )
    }
}

private fun contentRegionChoices(): List<SettingsPickerOption<String>> =
    (listOf(SettingsPickerOption("", "Automatique (appareil)")) +
        Locale.getISOCountries()
            .map { code ->
                SettingsPickerOption(code, runCatching {
                    Locale.Builder().setRegion(code).build().getDisplayCountry(Locale.getDefault())
                }.getOrNull()?.takeIf { it.isNotBlank() } ?: code)
            })
        .sortedBy { it.title }

private fun contentRegionLabel(region: String): String =
    if (region.isBlank()) "Automatique"
    else runCatching {
        Locale.Builder().setRegion(region).build().getDisplayCountry(Locale.getDefault())
    }.getOrNull()?.takeIf { it.isNotBlank() } ?: region
