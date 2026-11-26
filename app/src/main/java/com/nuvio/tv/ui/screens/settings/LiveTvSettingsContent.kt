package com.nuvio.tv.ui.screens.settings

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.R
import com.nuvio.tv.domain.model.Addon
import com.nuvio.tv.domain.model.CatalogDescriptor
import com.nuvio.tv.livetv.model.AddonChannelSource
import com.nuvio.tv.livetv.model.ChannelSort
import com.nuvio.tv.livetv.model.EpgSource
import com.nuvio.tv.livetv.model.LiveTvSettings
import com.nuvio.tv.livetv.model.PlaylistSource
import com.nuvio.tv.livetv.model.ZapMode
import com.nuvio.tv.livetv.ui.LiveTvSettingsViewModel
import com.nuvio.tv.livetv.ui.TextInputDialog
import com.nuvio.tv.ui.components.NuvioDialog
import com.nuvio.tv.livetv.model.effectiveGuideSize
import com.nuvio.tv.ui.screens.collection.NuvioTextField
import com.nuvio.tv.ui.theme.NuvioTheme
import kotlinx.coroutines.delay
import java.text.DateFormat
import java.util.Date

/**
 * Live TV settings are one place in Settings with a row of tabs across the top of it (Setup,
 * Guide, Playback, Integration): [HUB] is that tabbed page. [ALL] is the old single
 * long list, kept for the guide's long-press menu. The others are the tabs themselves.
 */
internal enum class LiveTvPage { ALL, HUB, MAIN, GUIDE, PLAYBACK, ON_DEMAND, INTEGRATION }

private fun LiveTvPage.titleResource(): Int = when (this) {
    LiveTvPage.ON_DEMAND -> R.string.live_tv_settings_on_demand_title
    else -> R.string.settings_live_tv_title
}

private fun LiveTvPage.subtitleResource(): Int = when (this) {
    LiveTvPage.ALL, LiveTvPage.HUB -> R.string.live_tv_settings_overview_subtitle
    LiveTvPage.MAIN -> R.string.live_tv_settings_setup_subtitle
    LiveTvPage.GUIDE -> R.string.live_tv_settings_guide_subtitle
    LiveTvPage.PLAYBACK -> R.string.live_tv_settings_playback_subtitle
    LiveTvPage.ON_DEMAND -> R.string.live_tv_settings_on_demand_subtitle
    LiveTvPage.INTEGRATION -> R.string.live_tv_settings_integration_subtitle
}

private fun LiveTvPage.tabResource(): Int = when (this) {
    LiveTvPage.MAIN -> R.string.live_tv_settings_tab_setup
    LiveTvPage.GUIDE -> R.string.live_tv_settings_tab_guide
    LiveTvPage.PLAYBACK -> R.string.live_tv_settings_tab_playback
    LiveTvPage.INTEGRATION -> R.string.live_tv_settings_tab_integration
    else -> error("Cette page de TV en direct ne comporte aucun onglet")
}

private val LIVE_TABS = listOf(LiveTvPage.MAIN, LiveTvPage.GUIDE, LiveTvPage.PLAYBACK, LiveTvPage.INTEGRATION)

/** The row of tabs across the top of Live TV settings; moving onto one shows it. */
@Composable
@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
private fun LiveTabRow(selected: LiveTvPage, first: FocusRequester?, onSelect: (LiveTvPage) -> Unit) {
    // Coming up from the page below lands on the tab you're on, not whichever one is nearest.
    // Only the tab you're on can take focus from outside the row; once focus is in the row, all can.
    var rowHasFocus by remember { mutableStateOf(false) }
    androidx.compose.foundation.layout.Row(
        Modifier.fillMaxWidth()
            .onFocusChanged { rowHasFocus = it.hasFocus }
            .focusGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LIVE_TABS.forEach { tab ->
            var focused by remember { mutableStateOf(false) }
            val on = tab == selected
            val shape = NuvioTheme.configuredShape(RoundedCornerShape(50))
            Text(
                stringResource(tab.tabResource()),
                color = if (focused) NuvioTheme.colors.OnSecondary else if (on) NuvioTheme.colors.TextPrimary else NuvioTheme.colors.TextSecondary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .then(if (on && first != null) Modifier.focusRequester(first) else Modifier)
                    .focusProperties { canFocus = on || rowHasFocus }
                    .clip(shape)
                    .background(
                        when {
                            focused -> NuvioTheme.colors.Secondary
                            on -> androidx.compose.ui.graphics.Color.White.copy(alpha = 0.14f)
                            else -> androidx.compose.ui.graphics.Color.White.copy(alpha = 0.06f)
                        }
                    )
                    .onFocusChanged { focused = it.isFocused; if (it.isFocused) onSelect(tab) }
                    .clickable { onSelect(tab) }
                    .padding(horizontal = 18.dp, vertical = 9.dp)
            )
        }
    }
}


/** On the four sub-tabs the sections sit open (there are only a few per tab), not folded. */
private val LocalLiveFlat = androidx.compose.runtime.compositionLocalOf { false }

/** Standalone version, opened from the guide's long-press menu. */
@Composable
fun LiveTvSettingsScreen(
    onBack: () -> Unit = {},
    viewModel: LiveTvSettingsViewModel = hiltViewModel()
) {
    SettingsStandaloneScaffold(
        title = stringResource(R.string.settings_live_tv_title),
        subtitle = stringResource(R.string.live_tv_settings_overview_subtitle),
    ) {
        LiveTvSettingsContent(viewModel = viewModel, page = LiveTvPage.ALL)
    }
}

/** Match Frame Rate's choices, worded as in Nuvio's own Playback settings. */
private val FRAME_RATE_MODES = listOf(
    Triple("OFF", "Désactivé", "Ne modifie pas la fréquence de rafraîchissement de l’écran."),
    Triple("START", "Au démarrage", "Change la fréquence au début de la lecture."),
    Triple("START_STOP", "Au démarrage et à l’arrêt", "Change la fréquence au début de la lecture et la rétablit à l’arrêt.")
)

private val CLOCK_MODES = listOf(
    "always" to "Toujours",
    "panel" to "Avec les commandes du lecteur",
    "never" to "Jamais"
)
private val CLOCK_POSITIONS = listOf(
    "top_left" to "En haut à gauche",
    "top_right" to "En haut à droite",
    "bottom_left" to "En bas à gauche",
    "bottom_right" to "En bas à droite"
)
private fun padLabel(minutes: Int): String = if (minutes <= 0) "Désactivé" else "$minutes min"

private val CLOCK_SIZES = listOf("small" to "Petit", "medium" to "Moyen", "large" to "Grand")

private sealed interface LiveDialog {
    data object AddM3u : LiveDialog
    data object AddXtream : LiveDialog
    data object AddEpg : LiveDialog
    data object AddonChannels : LiveDialog
    data class PlaylistActions(val source: PlaylistSource) : LiveDialog
    data class CatchupZone(val source: PlaylistSource) : LiveDialog
    data class EditPlaylist(val source: PlaylistSource) : LiveDialog
    data class EpgActions(val source: EpgSource) : LiveDialog
    data class EditEpg(val source: EpgSource) : LiveDialog
    data object HiddenChannels : LiveDialog
    data object HiddenGroups : LiveDialog
    data class PlaylistGuideShift(val source: PlaylistSource) : LiveDialog
    data class PlaylistCatchupShift(val source: PlaylistSource) : LiveDialog
    data class EpgSourceShift(val source: EpgSource) : LiveDialog
    data object ClockMode : LiveDialog
    data object FrameRate : LiveDialog
    data object RecordPre : LiveDialog
    data object RecordPost : LiveDialog
    data object ClockPosition : LiveDialog
    data object ClockSize : LiveDialog
    data object ClockOpacity : LiveDialog
    data object ZapModeChoice : LiveDialog
    data object BannerTime : LiveDialog
    data object PlaylistRefresh : LiveDialog
    data object EpgRefresh : LiveDialog
    data object FutureDays : LiveDialog
    data object PastHours : LiveDialog
    data object SortChoice : LiveDialog
    data object GroupSortChoice : LiveDialog
    data object HighlightChoice : LiveDialog
    data object CustomGroups : LiveDialog
    data object ChannelRow : LiveDialog
    data object ConfirmRestore : LiveDialog
    data object PinCheck : LiveDialog
    data object PinNew : LiveDialog
    data object PinRemove : LiveDialog
    data object Reminders : LiveDialog
    data object StartPage : LiveDialog
    data object NameEditor : LiveDialog
    data object PosterTest : LiveDialog
    data object ButtonTester : LiveDialog
    data object LoadReport : LiveDialog
    data object HomeRowSource : LiveDialog
    data object HomeRowTitle : LiveDialog
    data class ConfirmDeletePlaylist(val source: PlaylistSource) : LiveDialog
    data class ConfirmDeleteEpg(val source: EpgSource) : LiveDialog
    /** Confirmation demandée avant une réinitialisation, un effacement ou une action similaire. */
    data class Confirm(val title: String, val message: String, val label: String, val action: () -> Unit) : LiveDialog
}

private val EPG_OFFSETS = (-24..24).map { it * 30 }
private val REFRESH_HOURS = listOf(3, 6, 12, 24, 48, 72, 168)
private val BANNER_SECONDS = listOf(3, 5, 8, 10, 15)
private val FUTURE_DAYS = listOf(1, 2, 3, 5, 7, 10, 14)
private val PAST_HOURS = listOf(6, 12, 24, 48, 72, 168)

/** Time zones offered for a playlist's catch-up (the common ones IPTV servers run on). */
private val CATCHUP_ZONES = listOf(
    "UTC" to "UTC",
    "Europe/London" to "Royaume-Uni et Irlande (Londres)",
    "Europe/Amsterdam" to "Europe centrale (Amsterdam, Paris, Berlin)",
    "Europe/Athens" to "Europe de l’Est (Athènes, Bucarest)",
    "Europe/Istanbul" to "Turquie (Istanbul)",
    "Europe/Moscow" to "Moscou",
    "Asia/Dubai" to "Golfe (Dubaï)",
    "Asia/Kolkata" to "Inde",
    "America/New_York" to "États-Unis, côte Est (New York)",
    "America/Chicago" to "États-Unis, centre (Chicago)",
    "America/Denver" to "États-Unis, Rocheuses (Denver)",
    "America/Los_Angeles" to "États-Unis, côte Ouest (Los Angeles)"
)

/** A zone id in words: one of the list's names, else as found (e.g. "GMT+01:00"). */
private fun zoneName(id: String): String =
    CATCHUP_ZONES.firstOrNull { it.first == id }?.second ?: id

/** The time on that zone's clock now, to match against the provider's. */
private fun zoneClock(id: String): String =
    java.text.SimpleDateFormat("HH:mm", java.util.Locale.US)
        .apply { timeZone = java.util.TimeZone.getTimeZone(id) }
        .format(java.util.Date())

private fun catchupZoneLabel(p: PlaylistSource): String = when {
    p.catchupTimezone.isNotBlank() -> zoneName(p.catchupTimezone)
    p.serverTimezone.isNotBlank() -> "Automatique (${zoneName(p.serverTimezone)})"
    else -> "Automatique"
}

private fun offsetLabel(min: Int): String {
    if (min == 0) return "Aucun"
    val sign = if (min > 0) "+" else "−"
    val a = kotlin.math.abs(min)
    return if (a % 60 == 0) "$sign${a / 60} h" else "$sign${a / 60} h ${a % 60} min"
}

private fun hoursLabel(h: Int): String = when {
    h >= 24 && h % 24 == 0 -> if (h == 24) "1 jour" else "${h / 24} jours"
    else -> "$h h"
}

/** Typed into the channel name editor, this turns the hidden developer tools on or off. */
private const val DEVELOPER_WORD = "#nuviodev"

private fun openOnLabel(page: String): String = when (page) {
    "TV en direct" -> "TV en direct"
    "ON_DEMAND" -> "À la demande"
    "Sports" -> "Sports"
    else -> "Accueil"
}

private fun sourceStatus(enabled: Boolean, updated: Long, error: String?, count: String): String {
    if (!enabled) return "Désactivé"
    val parts = mutableListOf<String>()
    if (error != null) parts += "Erreur : $error"
    if (updated > 0) {
        parts += count
        parts += "Mis à jour le " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(updated))
    } else if (error == null) {
        parts += "Pas encore téléchargé"
    }
    return parts.joinToString(" · ")
}

@Composable
internal fun LiveTvSettingsContent(
    viewModel: LiveTvSettingsViewModel = hiltViewModel(),
    initialFocusRequester: FocusRequester? = null,
    page: LiveTvPage = LiveTvPage.ALL
) {
    // HUB: one Live TV page with its own row of tabs; the tab picked is the page shown.
    val hub = page == LiveTvPage.HUB
    var tab by remember { mutableStateOf(LiveTvPage.MAIN) }
    @Suppress("NAME_SHADOWING") val page = if (hub) tab else page
    fun show(p: LiveTvPage) = page == LiveTvPage.ALL || page == p
    val flat = page != LiveTvPage.ALL && page != LiveTvPage.MAIN
    /** The first section takes the focus when you come in from the list on the left (the tab row does, in a hub). */
    val firstRequester = if (hub) null else initialFocusRequester
    fun firstFocus(p: LiveTvPage): FocusRequester? = firstRequester.takeIf { page == p }
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val epgs by viewModel.epgSources.collectAsStateWithLifecycle()
    val addonChannels by viewModel.addonChannelSources.collectAsStateWithLifecycle()
    val installedAddons by viewModel.installedAddons.collectAsStateWithLifecycle()
    val s by viewModel.settings.collectAsStateWithLifecycle()
    val user by viewModel.userState.collectAsStateWithLifecycle()
    val status by viewModel.status.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    var dialog by remember { mutableStateOf<LiveDialog?>(null) }
    val drive by viewModel.driveSync.state.collectAsStateWithLifecycle()
    val cloud by viewModel.cloudSync.state.collectAsStateWithLifecycle()
    val syncParts by viewModel.cloudSync.enabledParts.collectAsStateWithLifecycle()
    val developerToolsOn by com.nuvio.tv.livetv.model.DeveloperTools.enabled.collectAsStateWithLifecycle()
    val onDemandHas by viewModel.onDemand.hasContent.collectAsStateWithLifecycle()
    val onDemandStatus by viewModel.onDemand.status.collectAsStateWithLifecycle()
    var pinWrong by remember { mutableStateOf(false) }

    fun update(t: (LiveTvSettings) -> LiveTvSettings) = viewModel.update(t)

    // Every section starts folded; OK on its header opens or closes it (not remembered).
    var open by remember { mutableStateOf(setOf<String>()) }
    fun toggle(key: String) { open = if (key in open) open - key else open + key }
    /** Asks before doing something that can't simply be undone. */
    fun confirm(title: String, message: String, label: String, action: () -> Unit) {
        dialog = LiveDialog.Confirm(title, message, label, action)
    }
    val hasPin = s.parentalPin.length >= 4

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md)
    ) {
        SettingsDetailHeader(
            title = stringResource(page.titleResource()),
            subtitle = if (status.loading && !flat) {
                status.message ?: stringResource(R.string.live_tv_settings_updating)
            } else {
                stringResource(page.subtitleResource())
            }
        )
        // Live TV switched off (Settings → Menu & Features): say so, with a way to turn it back on.
        if (!s.showInSidebar) {
            SettingsGroupCard {
                SettingsActionRow(
                    title = "La TV en direct est désactivée",
                    subtitle = "Le guide, sa ligne sur l’accueil et toutes les fonctions de la TV en direct sont désactivés. Activez-les dans Paramètres → Menu et fonctionnalités.",
                    value = "Activer",
                    onClick = { update { it.copy(showInSidebar = true) } }
                )
            }
        }
        if (hub) LiveTabRow(selected = tab, first = initialFocusRequester, onSelect = { tab = it })
        androidx.compose.runtime.key(page) {
        androidx.compose.runtime.CompositionLocalProvider(LocalLiveFlat provides flat) {
        val listState = androidx.compose.foundation.lazy.rememberLazyListState()
        val settleScope = androidx.compose.runtime.rememberCoroutineScope()
        val hiddenTop = with(androidx.compose.ui.platform.LocalDensity.current) { 150.dp.toPx() }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .onPreviewKeyEvent { e ->
                    // Going up onto the first rows of a section: bring its heading into view too
                    // (it was left cut off at the top, as if behind the tabs).
                    if (e.key == Key.DirectionUp && e.type == KeyEventType.KeyDown) {
                        settleScope.launch {
                            kotlinx.coroutines.delay(90)
                            val top = listState.layoutInfo.visibleItemsInfo.firstOrNull()
                            if (top != null && top.offset < 0 && top.offset > -hiddenTop) listState.animateScrollToItem(top.index)
                        }
                    }
                    false
                },
            contentPadding = PaddingValues(bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md)
        ) {
            // ------------------------------------------------------------ Setup Sync
            if (show(LiveTvPage.MAIN) && cloud.available) item(key = "cloud") {
                CloudSyncCard(
                    modifier = if (firstRequester != null) Modifier.focusRequester(firstRequester) else Modifier,
                    state = cloud,
                    enabledParts = syncParts,
                    onTogglePart = { viewModel.cloudSync.setPart(it, it !in syncParts) },
                    expanded = "cloud" in open,
                    onToggleExpanded = { toggle("cloud") },
                    onTurnOn = { viewModel.cloudSync.turnOn() },
                    driveAvailable = drive.available,
                    onImportDrive = {
                        confirm(
                            "Importer depuis Google Drive ?",
                            if (drive.connected) "La sauvegarde Google Drive sera fusionnée avec la configuration de ce téléviseur. Rien ne sera supprimé de Google Drive."
                            else "Connectez-vous à Google avec un code sur votre téléphone. La sauvegarde Google Drive sera fusionnée avec la configuration de ce téléviseur. Rien ne sera supprimé de Google Drive et le téléviseur ne restera pas connecté.",
                            "Importer"
                        ) { viewModel.cloudSync.importFromDrive() }
                    },
                    onSyncNow = { viewModel.cloudSync.syncNow() },
                    onRestorePrevious = {
                        confirm(
                            "Restaurer la copie précédente ?",
                            "La configuration de ce téléviseur sera remplacée par la copie enregistrée avant la dernière sauvegarde.",
                            "Restaurer"
                        ) { viewModel.cloudSync.restorePrevious() }
                    },
                    onTurnOff = { viewModel.cloudSync.turnOff() },
                    onDelete = {
                        confirm(
                            "Supprimer la configuration sauvegardée ?",
                            "La copie sauvegardée sera supprimée et la synchronisation de ce téléviseur sera arrêtée. Sa configuration locale sera conservée.",
                            "Supprimer"
                        ) { viewModel.cloudSync.deleteCloudCopy() }
                    }
                )
            }

            // ------------------------------------------------------------ Backup & Sync
            if (show(LiveTvPage.MAIN)) item(key = "drive") {
                DriveSyncCard(
                    modifier = if (firstRequester != null && !cloud.available) Modifier.focusRequester(firstRequester) else Modifier,
                    state = drive,
                    expanded = "drive" in open,
                    onToggleExpanded = { toggle("drive") },
                    onConnect = { viewModel.driveSync.beginSignIn() },
                    onAutoSync = { viewModel.driveSync.setAutoSync(it) },
                    onBackUp = {
                        confirm(
                            "Sauvegarder maintenant ?",
                            "La copie de Google Drive sera remplacée par la configuration TV en direct de ce téléviseur.",
                            "Sauvegarder"
                        ) { viewModel.driveSync.backUpNow() }
                    },
                    onRestore = { dialog = LiveDialog.ConfirmRestore },
                    onDisconnect = {
                        confirm(
                            "Déconnecter Google Drive ?",
                            "La synchronisation de ce téléviseur sera arrêtée. La sauvegarde restera dans Google Drive.",
                            "Déconnecter"
                        ) { viewModel.driveSync.disconnect() }
                    }
                )
            }

            // ------------------------------------------------------------ Playlists & Guides
            if (show(LiveTvPage.MAIN)) item(key = "sources") {
                LiveSection(
                    title = stringResource(R.string.live_tv_settings_playlists_guides_title),
                    summary = if (status.loading) (status.message ?: stringResource(R.string.live_tv_settings_updating))
                    else stringResource(
                        R.string.live_tv_settings_playlist_guide_counts,
                        pluralStringResource(R.plurals.live_tv_settings_playlist_count, playlists.size, playlists.size),
                        pluralStringResource(R.plurals.live_tv_settings_guide_count, epgs.size, epgs.size),
                    ),
                    icon = Icons.AutoMirrored.Filled.PlaylistPlay,
                    open = "sources" in open,
                    onToggle = { toggle("sources") }
                ) {
                    SubHeading("Playlists")
                    if (status.loading) LoadingLine(status.message ?: "Mise à jour…")
                    playlists.forEach { pl ->
                        val account = accounts[pl.id]
                        val expiring = pl.enabled && account?.expiringSoon() == true
                        SettingsActionRow(
                            title = pl.name + if (pl.isXtream) " (Xtream)" else "",
                            subtitle = when {
                                pl.isXtream && pl.enabled && !pl.importLive && pl.importVod -> "Films et séries uniquement (chaînes TV désactivées)"
                                pl.isXtream && pl.enabled && !pl.importLive && !pl.importVod -> "Aucun contenu inclus : activez les chaînes TV ou les films et séries dans Modifier."
                                else -> sourceStatus(pl.enabled, pl.lastUpdatedMs, pl.lastError, "${pl.channelCount} chaînes") +
                                    (if (pl.isXtream && pl.importVod && pl.enabled) " · + films et séries" else "")
                            } + (account?.expiryLabel()?.let { " · Expire le $it" } ?: ""),
                            value = when {
                                !pl.enabled -> "Désactivé"
                                expiring && (account?.expiresAtMs ?: 0L) <= System.currentTimeMillis() -> "Expirée"
                                expiring -> "Expire bientôt"
                                else -> "Activé"
                            },
                            valueColor = if (expiring) NuvioTheme.colors.Error else NuvioTheme.colors.TextSecondary,
                            leadingIcon = Icons.Default.LiveTv,
                            onClick = { dialog = LiveDialog.PlaylistActions(pl) }
                        )
                    }
                    SettingsActionRow(
                        title = "Ajouter une playlist M3U",
                        subtitle = "Une liste de chaînes accessible avec le lien M3U fourni par votre fournisseur.",
                        leadingIcon = Icons.Default.Add,
                        onClick = { dialog = LiveDialog.AddM3u }
                    )
                    SettingsActionRow(
                        title = "Ajouter des identifiants Xtream Codes",
                        subtitle = "Connectez-vous avec l’adresse du serveur, le nom d’utilisateur et le mot de passe fournis. Le guide TV est inclus.",
                        leadingIcon = Icons.Default.Add,
                        onClick = { dialog = LiveDialog.AddXtream }
                    )
                    SubHeading("Chaînes des extensions")
                    addonChannels.filter { it.enabled }.forEach { src ->
                        SettingsActionRow(
                            title = "${src.addonName} · ${src.catalogName}",
                            subtitle = sourceStatus(
                                src.enabled, src.lastUpdatedMs, src.lastError,
                                "${src.channelCount} chaînes"
                            ),
                            value = "Activé",
                            leadingIcon = Icons.Default.LiveTv,
                            onClick = { dialog = LiveDialog.AddonChannels }
                        )
                    }
                    SettingsActionRow(
                        title = "Ajouter des chaînes TV depuis les extensions",
                        subtitle = "Utilisez les catalogues TV en direct de vos extensions installées. Le flux d’une chaîne est recherché lorsque vous l’ouvrez.",
                        leadingIcon = Icons.Default.Add,
                        onClick = { dialog = LiveDialog.AddonChannels }
                    )
                    SubHeading("Guides TV (EPG)")
                    epgs.forEach { e ->
                        SettingsActionRow(
                            title = e.name,
                            subtitle = sourceStatus(e.enabled, e.lastUpdatedMs, e.lastError, "${e.programCount} programmes associés"),
                            value = if (e.enabled) "Activé" else "Désactivé",
                            leadingIcon = Icons.Default.Link,
                            onClick = { dialog = LiveDialog.EpgActions(e) }
                        )
                    }
                    SettingsActionRow(
                        title = "Ajouter une source EPG",
                        subtitle = "Ajoutez des programmes TV avec un lien XMLTV (se terminant par .xml ou .xml.gz). Les guides liés à vos playlists se chargent automatiquement.",
                        leadingIcon = Icons.Default.Add,
                        onClick = { dialog = LiveDialog.AddEpg }
                    )
                    SubHeading("Mises à jour")
                    SettingsActionRow(
                        title = "Tout mettre à jour maintenant",
                        subtitle = "Télécharge les dernières chaînes et les derniers programmes sans attendre la prochaine mise à jour automatique.",
                        leadingIcon = Icons.Default.Refresh,
                        onClick = { viewModel.refreshAll() }
                    )
                    SettingsActionRow(
                        "Vérifier les playlists toutes les", "Fréquence de téléchargement des listes de chaînes depuis vos fournisseurs.",
                        hoursLabel(s.playlistRefreshHours), onClick = { dialog = LiveDialog.PlaylistRefresh }
                    )
                    SettingsActionRow(
                        "Vérifier les guides toutes les", "Fréquence de téléchargement des programmes TV.",
                        hoursLabel(s.epgRefreshHours), onClick = { dialog = LiveDialog.EpgRefresh }
                    )
                    SettingsToggleRow(
                        "Afficher les notifications de mise à jour",
                        "Affiche la progression des mises à jour des playlists, des guides EPG et du contenu à la demande. Les mises à jour continuent en arrière-plan si les notifications sont masquées.",
                        s.showImportProgress,
                        { update { it.copy(showImportProgress = !it.showImportProgress) } }
                    )
                }
            }

            // ------------------------------------------------------------ Channels
            if (show(LiveTvPage.MAIN)) item(key = "channels") {
                LiveSection(
                    title = stringResource(R.string.live_tv_settings_channels_title),
                    summary = stringResource(R.string.live_tv_settings_channels_summary),
                    icon = Icons.Default.LiveTv,
                    open = "channels" in open,
                    onToggle = { toggle("channels") }
                ) {
                    Text(
                        text = "Astuce : maintenez OK sur une chaîne du guide pour la renommer, changer son numéro, la masquer ou l’ajouter aux favoris. Faites de même sur un groupe pour le renommer, le déplacer ou le masquer.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NuvioTheme.colors.TextSecondary,
                        modifier = Modifier.padding(horizontal = NuvioTheme.spacing.lg, vertical = NuvioTheme.spacing.sm)
                    )
                    SubHeading("Noms des chaînes")
                    SettingsActionRow(
                        title = "Nettoyage des noms de chaînes",
                        subtitle = "Mots à retirer du début ou de la fin des noms de chaînes (USA, 24/7, FHD…), séparés par des virgules.",
                        value = com.nuvio.tv.livetv.model.ChannelNameEditor.terms(s.nameRemovals).size.let { if (it == 0) "Désactivé" else "$it removed" },
                        onClick = { dialog = LiveDialog.NameEditor }
                    )
                    SettingsToggleRow(
                        "Nettoyer aussi les noms des groupes",
                        "Retire aussi ces mots des noms de groupes, par exemple « | NL | Sports ».",
                        s.cleanGroupNames,
                        { update { it.copy(cleanGroupNames = !it.cleanGroupNames) } }
                    )
                    if (s.nameRemovals.isBlank()) {
                        SettingsActionRow(
                            "Ajouter les préfixes et suffixes courants",
                            "Ajoute notamment : ${com.nuvio.tv.livetv.model.ChannelNameEditor.COMMON}",
                            onClick = { update { it.copy(nameRemovals = com.nuvio.tv.livetv.model.ChannelNameEditor.COMMON) } }
                        )
                    }
                    SubHeading("Éléments masqués et vos groupes")
                    SettingsActionRow("Chaînes masquées", "Chaînes que vous avez masquées. Sélectionnez-en une pour la réafficher.", "${user.hiddenChannels.size}", leadingIcon = Icons.Default.VisibilityOff, onClick = { dialog = LiveDialog.HiddenChannels })
                    SettingsActionRow("Groupes masqués", "Groupes que vous avez masqués. Sélectionnez-en un pour le réafficher.", "${user.hiddenGroups.size}", leadingIcon = Icons.Default.VisibilityOff, onClick = { dialog = LiveDialog.HiddenGroups })
                    SettingsActionRow("Mes groupes", "Groupes que vous avez créés. Sélectionnez-en un pour le supprimer.", "${user.customGroups.size}", onClick = { dialog = LiveDialog.CustomGroups })
                    SubHeading("Effacer et réinitialiser")
                    SettingsActionRow("Effacer les favoris", "Retire toutes les chaînes des favoris.", "${user.favorites.size}", onClick = {
                        confirm("Effacer les favoris ?", "Toutes les chaînes seront retirées des favoris.", "Effacer") { viewModel.clearFavorites() }
                    })
                    SettingsActionRow("Effacer les chaînes récemment regardées", "Vide le groupe des chaînes récemment regardées.", "${user.recent.size}", onClick = {
                        confirm("Effacer les chaînes récemment regardées ?", "Le groupe des chaînes récemment regardées sera vidé.", "Effacer") { viewModel.clearRecent() }
                    })
                    SettingsActionRow(
                        "Réinitialiser les noms et numéros des chaînes", "Rétablit les noms et numéros de votre playlist.",
                        "${(user.channelNames.keys + user.channelNumbers.keys).size}",
                        onClick = {
                            confirm("Réinitialiser les noms et numéros des chaînes ?", "Les noms et numéros que vous avez modifiés seront rétablis comme dans la playlist.", "Réinitialiser") { viewModel.resetChannelEdits() }
                        }
                    )
                    SettingsActionRow("Réinitialiser les noms et l’ordre des groupes", "Rétablit les noms et l’ordre des groupes de la playlist.", onClick = {
                        confirm("Réinitialiser les noms et l’ordre des groupes ?", "Les noms des groupes et leur ordre seront rétablis comme dans la playlist.", "Réinitialiser") { viewModel.resetGroupEdits() }
                    })
                    SettingsActionRow(
                        "Réinitialiser l’ordre des chaînes", "Annule le réordonnancement des chaînes dans tous les groupes.",
                        "${user.channelOrder.size}", onClick = {
                            confirm("Réinitialiser l’ordre des chaînes ?", "L’ordre normal des chaînes sera rétabli dans chaque groupe.", "Réinitialiser") { viewModel.resetChannelOrder() }
                        }
                    )
                    SettingsActionRow(
                        "Supprimer les chaînes copiées", "Retire toutes les chaînes que vous avez copiées dans un autre groupe. Les originales sont conservées.",
                        "${user.channelCopies.values.sumOf { it.size }}", onClick = {
                            confirm("Supprimer les chaînes copiées ?", "Toutes les chaînes copiées dans un autre groupe en seront retirées. Les originales restent à leur place.", "Supprimer") { viewModel.clearChannelCopies() }
                        }
                    )
                    SettingsActionRow(
                        "Réinitialiser les associations EPG",
                        "Rétablit l’association automatique des chaînes avec les guides.",
                        "${user.epgOverrides.size}",
                        onClick = {
                            confirm("Réinitialiser les associations EPG ?", "Toutes les associations de guide que vous avez modifiées ou supprimées seront rétablies en mode automatique.", "Réinitialiser") { viewModel.resetEpgAssignments() }
                        }
                    )
                }
            }

            // ------------------------------------------------------------ Groups
            if (show(LiveTvPage.MAIN)) item(key = "groups") {
                LiveSection(
                    title = "Groupes",
                    summary = "Liste des groupes à gauche du guide.",
                    icon = Icons.Default.Folder,
                    open = "groups" in open,
                    onToggle = { toggle("groups") }
                ) {
                    SubHeading("Groupes intégrés")
                    SettingsToggleRow(
                        "Afficher les favoris",
                        "Groupe des chaînes que vous avez ajoutées aux favoris.",
                        s.showFavoritesGroup,
                        { update { it.copy(showFavoritesGroup = !it.showFavoritesGroup) } }
                    )
                    SettingsToggleRow(
                        "Afficher les chaînes récemment regardées",
                        "Groupe des dernières chaînes que vous avez regardées.",
                        s.showRecentGroup,
                        { update { it.copy(showRecentGroup = !it.showRecentGroup) } }
                    )
                    SettingsToggleRow(
                        "Afficher toutes les chaînes",
                        "Groupe contenant toutes les chaînes.",
                        s.showAllChannelsGroup,
                        { update { it.copy(showAllChannelsGroup = !it.showAllChannelsGroup) } }
                    )
                    SubHeading("Liste des groupes")
                    SettingsToggleRow(
                        "Regrouper par playlist",
                        "Avec plusieurs playlists, les groupes de chacune sont réunis sous un en-tête repliable.",
                        s.groupPlaylistHeadings,
                        { update { it.copy(groupPlaylistHeadings = !it.groupPlaylistHeadings) } }
                    )
                    SettingsToggleRow(
                        "Afficher le nombre de chaînes",
                        "Nombre de chaînes indiqué à côté de chaque groupe.",
                        s.showGroupCounts,
                        { update { it.copy(showGroupCounts = !it.showGroupCounts) } }
                    )
                    SettingsActionRow(
                        title = "Ordre des groupes",
                        subtitle = "Tri de la liste des groupes. L’ordre personnalisé respecte « Réorganiser les groupes » ; les autres groupes restent dans l’ordre de la playlist.",
                        value = when (s.groupSort) { "playlist" -> "Ordre de la playlist"; "name" -> "Nom (A–Z)"; else -> "Ordre personnalisé" },
                        onClick = { dialog = LiveDialog.GroupSortChoice }
                    )
                    SubHeading("Chaînes d’un groupe")
                    SettingsActionRow(
                        title = "Ordre des chaînes",
                        subtitle = "Tri des chaînes dans chaque groupe, favoris compris. L’ordre de la playlist conserve votre ordre personnalisé dans les favoris et vos groupes.",
                        value = when (s.channelSort) {
                            ChannelSort.PLAYLIST -> "Ordre de la playlist"
                            ChannelSort.NUMBER -> "Numéro de chaîne"
                            ChannelSort.NAME -> "Nom"
                        },
                        onClick = { dialog = LiveDialog.SortChoice }
                    )
                    SettingsToggleRow(
                        "Numéroter les chaînes 1, 2, 3… dans chaque groupe",
                        "Remplace les numéros de la playlist par le rang de chaque chaîne dans son groupe : 20 favoris deviennent les numéros 1 à 20.",
                        s.sequentialNumbers,
                        { update { it.copy(sequentialNumbers = !it.sequentialNumbers) } }
                    )
                }
            }

            // ------------------------------------------------------------ On Demand
            if (show(LiveTvPage.ON_DEMAND)) item(key = "on_demand") {
                LiveSection(
                    title = "À la demande",
                    summary = when {
                        onDemandStatus.loading -> onDemandStatus.message ?: "Importation des films et séries…"
                        !onDemandHas -> "Rien n’a encore été importé"
                        else -> "Films et séries importés" + when {
                            !s.onDemandEnabled -> " · désactivé"
                            s.onDemandInSidebar -> " · dans le menu latéral"
                            else -> ""
                        }
                    },
                    icon = Icons.Default.Movie,
                    open = "on_demand" in open,
                    onToggle = { toggle("on_demand") },
                    focusFirst = firstFocus(LiveTvPage.ON_DEMAND)
                ) {
                    Text(
                        text = "Films et séries des comptes Xtream pour lesquels l’option ci-dessous est activée.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NuvioTheme.colors.TextSecondary,
                        modifier = Modifier.padding(horizontal = NuvioTheme.spacing.lg, vertical = NuvioTheme.spacing.sm)
                    )
                    if (onDemandStatus.loading) LoadingLine(onDemandStatus.message ?: "Importation des films et séries…")
                    SubHeading("Fournisseurs")
                    SettingsActionRow(
                        "Mettre à jour les films et séries maintenant",
                        onDemandStatus.message ?: if (onDemandHas) "Importés. Mise à jour automatique une fois par jour." else "Rien n’a encore été importé",
                        onClick = { viewModel.onDemand.refreshNow() }
                    )
                    // One switch per Xtream login (logins added before On Demand start switched off).
                    playlists.filter { it.isXtream }.forEach { pl ->
                        SettingsToggleRow(
                            "Films et séries de ${pl.name}",
                            if (pl.importVod) "Inclus" else "Non inclus. Activez cette option pour importer les films et séries de ce fournisseur.",
                            pl.importVod,
                            { viewModel.savePlaylist(pl.copy(importVod = !pl.importVod)) }
                        )
                    }
                    SubHeading("Navigation")
                    var pickingSort by remember { mutableStateOf(false) }
                    val defaultSort = com.nuvio.tv.livetv.ondemand.VodSort.parse(s.vodDefaultSort)?.first
                        ?: com.nuvio.tv.livetv.ondemand.VodSort.DEFAULT
                    SettingsActionRow(
                        title = "Ordre de tri par défaut",
                        subtitle = "Tri par défaut des groupes, sauf si vous choisissez un autre tri dans « Tout voir » ou « Gérer les groupes VOD ».",
                        value = defaultSort.label,
                        onClick = { pickingSort = true }
                    )
                    if (pickingSort) com.nuvio.tv.livetv.ondemand.VodSortPicker(
                        title = "Ordre de tri par défaut",
                        current = defaultSort,
                        onPick = { picked ->
                            pickingSort = false
                            val p = picked ?: com.nuvio.tv.livetv.ondemand.VodSort.DEFAULT
                            update { it.copy(vodDefaultSort = com.nuvio.tv.livetv.ondemand.VodSort.encode(p)) }
                        },
                        onDismiss = { pickingSort = false }
                    )
                    SettingsToggleRow(
                        "Fusionner les doublons",
                        "N’affiche chaque film ou série qu’une fois, même si votre fournisseur en propose plusieurs versions (qualités, catégories ou fournisseurs différents). Le bouton Lire permet de choisir la version.",
                        s.vodMergeDuplicates,
                        { update { it.copy(vodMergeDuplicates = !it.vodMergeDuplicates) } }
                    )
                    SettingsToggleRow(
                        "Lire directement chez mon fournisseur",
                        "Lire lance directement la version de votre fournisseur depuis la section À la demande. Désactivé : Lire affiche toutes les sources, dont celle du fournisseur dans « À la demande ». Maintenez Lire pour afficher toutes les sources.",
                        s.onDemandPlayDirect,
                        { update { it.copy(onDemandPlayDirect = !it.onDemandPlayDirect) } }
                    )
                    SettingsToggleRow(
                        "Utiliser les affiches de mes extensions",
                        "Utilise les affiches de vos extensions lorsqu’elles proposent le titre. Désactivé : seules les images du fournisseur sont utilisées.",
                        s.onDemandAddonPosters,
                        { update { it.copy(onDemandAddonPosters = !it.onDemandAddonPosters) } }
                    )
                    SettingsActionRow(
                        "Afficher les catégories masquées", "Réaffiche toutes les catégories à la demande que vous avez masquées.",
                        "${user.vodHiddenCategories.size}", onClick = {
                            confirm("Afficher les catégories masquées ?", "Toutes les catégories à la demande que vous avez masquées seront réaffichées.", "Afficher") { viewModel.clearVodHidden() }
                        }
                    )
                }
            }

            // ------------------------------------------------------------ Guide Layout
            if (show(LiveTvPage.GUIDE)) item(key = "layout") {
                LiveSection(
                    title = "Présentation du guide",
                    summary = "Contenu du guide et nombre de chaînes affichées.",
                    icon = Icons.Default.GridView,
                    open = "layout" in open,
                    onToggle = { toggle("layout") },
                    focusFirst = firstFocus(LiveTvPage.GUIDE)
                ) {
                    SubHeading("En-tête du guide")
                    SettingsToggleRow(
                        "Afficher le panneau d’informations",
                        "Panneau présentant l’affiche, le titre, l’horaire et le résumé du programme sélectionné.",
                        s.showProgramDetails,
                        { update { it.copy(showProgramDetails = !it.showProgramDetails) } }
                    )
                    SettingsToggleRow(
                        "Afficher la fenêtre d’aperçu",
                        "Aperçu vidéo de la chaîne sélectionnée, dans le coin supérieur droit.",
                        s.showPreview,
                        { update { it.copy(showPreview = !it.showPreview) } }
                    )
                    SettingsToggleRow(
                        "Réduire le panneau d’informations et l’aperçu",
                        "Réduit la hauteur de l’en-tête du guide d’environ un tiers pour afficher davantage de chaînes.",
                        s.smallHeader,
                        { update { it.copy(smallHeader = !it.smallHeader) } }
                    )
                    SettingsActionRow(
                        title = "Assombrissement du guide",
                        subtitle = "Règle l’assombrissement du panneau d’informations et des barres du guide. Le mode le plus clair laisse davantage voir l’arrière-plan ; le plus sombre améliore la lisibilité.",
                        value = when (s.guideInfoBackground) { "darker" -> "Normal"; "solid" -> "Le plus sombre"; else -> "Le plus clair" },
                        onClick = {
                            update {
                                it.copy(guideInfoBackground = when (it.guideInfoBackground) { "standard" -> "darker"; "darker" -> "solid"; else -> "standard" })
                            }
                        }
                    )
                    SettingsToggleRow(
                        "Afficher les affiches",
                        "Affiches des programmes en cours, fournies par vos extensions. Désactivé : affiche uniquement le logo de la chaîne.",
                        s.showPosters,
                        { update { it.copy(showPosters = !it.showPosters) } }
                    )
                    SettingsToggleRow(
                        "Afficher le nom de la playlist dans le panneau d’informations",
                        "Playlist d’origine de la chaîne sélectionnée.",
                        s.showPlaylistInInfo,
                        { update { it.copy(showPlaylistInInfo = !it.showPlaylistInInfo) } }
                    )
                    SubHeading("Contenu du panneau d’informations")
                    SettingsToggleRow(
                        "Afficher le nom et le logo de la chaîne",
                        "Ligne de la chaîne en haut du panneau d’informations.",
                        com.nuvio.tv.livetv.ui.GuideInfoPart.CHANNEL !in com.nuvio.tv.livetv.ui.BarPart.parse(s.guideInfoHidden),
                        {
                            update {
                                val cur = com.nuvio.tv.livetv.ui.BarPart.parse(it.guideInfoHidden)
                                val key = com.nuvio.tv.livetv.ui.GuideInfoPart.CHANNEL
                                it.copy(guideInfoHidden = com.nuvio.tv.livetv.ui.BarPart.join(if (key in cur) cur - key else cur + key))
                            }
                        }
                    )
                    SettingsToggleRow(
                        "Afficher les badges DIRECT et REPLAY",
                        "Petits badges à côté du nom de la chaîne.",
                        com.nuvio.tv.livetv.ui.GuideInfoPart.TAGS !in com.nuvio.tv.livetv.ui.BarPart.parse(s.guideInfoHidden),
                        {
                            update {
                                val cur = com.nuvio.tv.livetv.ui.BarPart.parse(it.guideInfoHidden)
                                val key = com.nuvio.tv.livetv.ui.GuideInfoPart.TAGS
                                it.copy(guideInfoHidden = com.nuvio.tv.livetv.ui.BarPart.join(if (key in cur) cur - key else cur + key))
                            }
                        }
                    )
                    SettingsToggleRow(
                        "Afficher le titre",
                        "Titre du programme en grand.",
                        com.nuvio.tv.livetv.ui.GuideInfoPart.TITLE !in com.nuvio.tv.livetv.ui.BarPart.parse(s.guideInfoHidden),
                        {
                            update {
                                val cur = com.nuvio.tv.livetv.ui.BarPart.parse(it.guideInfoHidden)
                                val key = com.nuvio.tv.livetv.ui.GuideInfoPart.TITLE
                                it.copy(guideInfoHidden = com.nuvio.tv.livetv.ui.BarPart.join(if (key in cur) cur - key else cur + key))
                            }
                        }
                    )
                    SettingsToggleRow(
                        "Afficher les heures de début et de fin",
                        "Par exemple : 20:00 – 21:00.",
                        com.nuvio.tv.livetv.ui.GuideInfoPart.TIME !in com.nuvio.tv.livetv.ui.BarPart.parse(s.guideInfoHidden),
                        {
                            update {
                                val cur = com.nuvio.tv.livetv.ui.BarPart.parse(it.guideInfoHidden)
                                val key = com.nuvio.tv.livetv.ui.GuideInfoPart.TIME
                                it.copy(guideInfoHidden = com.nuvio.tv.livetv.ui.BarPart.join(if (key in cur) cur - key else cur + key))
                            }
                        }
                    )
                    SettingsToggleRow(
                        "Afficher le temps restant",
                        "Par exemple : 25 min restantes.",
                        com.nuvio.tv.livetv.ui.GuideInfoPart.LEFT !in com.nuvio.tv.livetv.ui.BarPart.parse(s.guideInfoHidden),
                        {
                            update {
                                val cur = com.nuvio.tv.livetv.ui.BarPart.parse(it.guideInfoHidden)
                                val key = com.nuvio.tv.livetv.ui.GuideInfoPart.LEFT
                                it.copy(guideInfoHidden = com.nuvio.tv.livetv.ui.BarPart.join(if (key in cur) cur - key else cur + key))
                            }
                        }
                    )
                    SettingsToggleRow(
                        "Afficher la catégorie",
                        "Par exemple : Drame ou sport.",
                        com.nuvio.tv.livetv.ui.GuideInfoPart.CATEGORY !in com.nuvio.tv.livetv.ui.BarPart.parse(s.guideInfoHidden),
                        {
                            update {
                                val cur = com.nuvio.tv.livetv.ui.BarPart.parse(it.guideInfoHidden)
                                val key = com.nuvio.tv.livetv.ui.GuideInfoPart.CATEGORY
                                it.copy(guideInfoHidden = com.nuvio.tv.livetv.ui.BarPart.join(if (key in cur) cur - key else cur + key))
                            }
                        }
                    )
                    SettingsToggleRow(
                        "Afficher la barre de progression",
                        "Progression du programme.",
                        com.nuvio.tv.livetv.ui.GuideInfoPart.PROGRESS !in com.nuvio.tv.livetv.ui.BarPart.parse(s.guideInfoHidden),
                        {
                            update {
                                val cur = com.nuvio.tv.livetv.ui.BarPart.parse(it.guideInfoHidden)
                                val key = com.nuvio.tv.livetv.ui.GuideInfoPart.PROGRESS
                                it.copy(guideInfoHidden = com.nuvio.tv.livetv.ui.BarPart.join(if (key in cur) cur - key else cur + key))
                            }
                        }
                    )
                    SettingsToggleRow(
                        "Afficher le résumé",
                        "Texte de présentation du programme.",
                        com.nuvio.tv.livetv.ui.GuideInfoPart.DESCRIPTION !in com.nuvio.tv.livetv.ui.BarPart.parse(s.guideInfoHidden),
                        {
                            update {
                                val cur = com.nuvio.tv.livetv.ui.BarPart.parse(it.guideInfoHidden)
                                val key = com.nuvio.tv.livetv.ui.GuideInfoPart.DESCRIPTION
                                it.copy(guideInfoHidden = com.nuvio.tv.livetv.ui.BarPart.join(if (key in cur) cur - key else cur + key))
                            }
                        }
                    )
                    SubHeading("Lignes des chaînes")
                    SettingsToggleRow(
                        "Afficher le nom des chaînes",
                        "Désactivé : affiche uniquement les numéros et les logos, pour laisser plus de place au programme.",
                        s.showChannelNames,
                        { update { it.copy(showChannelNames = !it.showChannelNames) } }
                    )
                    SettingsToggleRow(
                        "Afficher les numéros des chaînes",
                        "Numéro affiché devant chaque chaîne.",
                        s.showChannelNumbers,
                        { update { it.copy(showChannelNumbers = !it.showChannelNumbers) } }
                    )
                    SettingsToggleRow(
                        "Afficher les logos des chaînes",
                        "Logo affiché devant chaque nom de chaîne.",
                        s.showChannelLogos,
                        { update { it.copy(showChannelLogos = !it.showChannelLogos) } }
                    )
                    SettingsToggleRow(
                        "Encadrer la chaîne en cours",
                        "Cadre rouge autour de la chaîne que vous regardez.",
                        s.markPlayingChannel,
                        { update { it.copy(markPlayingChannel = !it.markPlayingChannel) } }
                    )
                    var pickingGuideSize by remember { mutableStateOf(false) }
                    val guideSizes = listOf(
                        Triple("COMPACT", "Compact", "Lignes plus fines et texte légèrement réduit : environ deux chaînes de plus à l’écran."),
                        Triple("STANDARD", "Normal", "Taille habituelle."),
                        Triple("LARGE", "Grand", "Texte et lignes plus grands : environ trois chaînes de moins à l’écran."),
                        Triple("XLARGE", "Très grand", "Texte et lignes les plus grands : environ cinq chaînes de moins à l’écran et 1 h 30 de programme.")
                    )
                    val currentSize = s.effectiveGuideSize
                    SettingsActionRow(
                        title = "Taille du guide",
                        subtitle = "Taille du texte et des lignes du guide.",
                        value = guideSizes.firstOrNull { it.first == currentSize }?.second ?: "Normal",
                        onClick = { pickingGuideSize = true }
                    )
                    if (pickingGuideSize) ActionListDialog(
                        title = "Taille du guide",
                        actions = guideSizes.map { (key, label, _) ->
                            (if (key == currentSize) "$label  ✓" else label) to {
                                pickingGuideSize = false
                                update { it.copy(guideSize = key, compactRows = key == "COMPACT") }
                            }
                        },
                        onDismiss = { pickingGuideSize = false }
                    )
                    SettingsToggleRow(
                        "Afficher les horaires dans le guide",
                        "Heures de début et de fin sous le titre de chaque programme (les lignes sont légèrement plus hautes).",
                        s.showProgramTimes,
                        { update { it.copy(showProgramTimes = !it.showProgramTimes) } }
                    )
                    SettingsToggleRow(
                        "Afficher les numéros des épisodes",
                        "Numéros d’épisode, par exemple « S2 E5 », après les titres dans le guide, les informations et le lecteur.",
                        s.showEpisodeNumbers,
                        { update { it.copy(showEpisodeNumbers = !it.showEpisodeNumbers) } }
                    )
                    SettingsToggleRow(
                        "Afficher le repère « Maintenant »",
                        "Ligne qui indique l’heure actuelle dans le guide.",
                        s.showNowLine,
                        { update { it.copy(showNowLine = !it.showNowLine) } }
                    )
                    SettingsToggleRow(
                        "Badges de qualité",
                        "Affiche 4K, FHD, HD ou SD à côté des chaînes que vous avez regardées.",
                        s.showQualityBadges,
                        { update { it.copy(showQualityBadges = !it.showQualityBadges) } }
                    )
                    SettingsToggleRow(
                        "Fréquence d’images dans la barre d’informations",
                        "Affiche la fluidité de la chaîne (25, 30, 50 ou 60 images/s) à côté de la résolution en plein écran. Pratique pour les événements sportifs en direct.",
                        s.showFrameRate,
                        { update { it.copy(showFrameRate = !it.showFrameRate) } }
                    )
                    SubHeading("Apparence")
                    SettingsActionRow(
                        title = "Arrière-plan du guide et de Sports",
                        subtitle = "Arrière-plan du guide TV et de Sports. Suit le thème de couleurs ou l’une des couleurs des icônes de l’application.",
                        value = com.nuvio.tv.livetv.ui.GuideBackdrops.label(s.guideBackdrop),
                        onClick = { update { it.copy(guideBackdrop = com.nuvio.tv.livetv.ui.GuideBackdrops.next(it.guideBackdrop)) } }
                    )
                    SettingsActionRow(
                        title = "Style de sélection",
                        subtitle = "Indique le programme sélectionné par un contour ou un aplat de la couleur du thème.",
                        value = if (s.solidHighlight) "Plein" else "Contour",
                        onClick = { dialog = LiveDialog.HighlightChoice }
                    )
                    SettingsToggleRow(
                        "Horloge sur 24 heures",
                        "Affiche 20:30 au lieu de 8:30 PM.",
                        s.use24HourClock,
                        { update { it.copy(use24HourClock = !it.use24HourClock) } }
                    )
                }
            }

            // ------------------------------------------------------------ Watching
            if (show(LiveTvPage.PLAYBACK)) item(key = "watching") {
                LiveSection(
                    title = "Ouverture et zapping",
                    summary = "Ouverture de la TV en direct, changement de chaîne et barre d’informations.",
                    icon = Icons.Default.PlayCircleFilled,
                    open = "watching" in open,
                    onToggle = { toggle("watching") },
                    focusFirst = firstFocus(LiveTvPage.PLAYBACK),
                ) {
                    SubHeading("Ouverture de la TV en direct")
                    SettingsToggleRow(
                        "Ouvrir sur le dernier groupe",
                        "Ouvre la TV en direct sur le dernier groupe dans lequel vous avez regardé une chaîne.",
                        s.rememberLastGroup,
                        { update { it.copy(rememberLastGroup = !it.rememberLastGroup) } }
                    )
                    SettingsToggleRow(
                        "Lire la dernière chaîne dans l’aperçu",
                        "À l’ouverture de la TV en direct, lance la dernière chaîne regardée dans la fenêtre d’aperçu.",
                        s.resumeLastInPreview,
                        { update { it.copy(resumeLastInPreview = !it.resumeLastInPreview) } }
                    )
                    SettingsToggleRow(
                        "Démarrer en plein écran",
                        "À la première ouverture de la TV en direct après le démarrage de l’application, lance la dernière chaîne en plein écran.",
                        s.autoPlayLastChannel,
                        { update { it.copy(autoPlayLastChannel = !it.autoPlayLastChannel) } }
                    )
                    SubHeading("Navigation")
                    SettingsToggleRow(
                        "OK ouvre en plein écran",
                        "OK sur un programme l’ouvre directement en plein écran, sans passer par l’aperçu.",
                        s.openFullscreenOnSelect,
                        { update { it.copy(openFullscreenOnSelect = !it.openFullscreenOnSelect) } }
                    )
                    SettingsToggleRow(
                        "Arriver sur le nom des chaînes (comme sur une box TV)",
                        "Après le choix d’un groupe, le focus arrive sur les noms des chaînes : Haut et Bas changent de chaîne, OK lance la lecture, Droite affiche le programme et Gauche revient aux groupes.",
                        s.browseByChannelName,
                        { update { it.copy(browseByChannelName = !it.browseByChannelName) } }
                    )
                    SettingsToggleRow(
                        "Mode superposé",
                        "Pendant la lecture, Gauche affiche les chaînes et les programmes par-dessus la vidéo. Désactivé : Gauche revient au guide.",
                        s.overlayMode,
                        { update { it.copy(overlayMode = !it.overlayMode) } }
                    )
                    if (s.overlayMode) {
                        SettingsToggleRow(
                            "Superposition : garder la liste ouverte au changement de chaîne",
                            "OK sur une autre chaîne la lance et laisse la liste ouverte pour continuer à parcourir les chaînes. OK sur la chaîne en cours ou Retour ferme la liste.",
                            s.overlayKeepOpen,
                            { update { it.copy(overlayKeepOpen = !it.overlayKeepOpen) } }
                        )
                        SettingsToggleRow(
                            "Superposition : afficher le programme et les détails à côté de la liste",
                            "Affiche à côté de la liste les programmes à venir et celui de la chaîne sélectionnée. Désactivé : seule la liste apparaît ; Droite ouvre toujours les détails.",
                            s.overlayShowDetails,
                            { update { it.copy(overlayShowDetails = !it.overlayShowDetails) } }
                        )
                    }
                    SettingsActionRow(
                        title = "CH+ / CH− parcourt",
                        subtitle = "Chaînes parcourues avec Haut/Bas ou CH+/CH− pendant la lecture.",
                        value = if (s.zapMode == ZapMode.GROUP) "Groupe actuel" else "Toutes les chaînes",
                        onClick = { dialog = LiveDialog.ZapModeChoice }
                    )
                    SettingsToggleRow(
                        "Zapping (parcourir avant de changer)",
                        "Haut/Bas et CH+/CH− affichent le programme des autres chaînes dans la barre d’informations sans changer de chaîne. La lecture continue jusqu’à ce que vous appuyiez sur OK sur la chaîne voulue. Retour vous ramène où vous étiez.",
                        s.channelSurf,
                        { update { it.copy(channelSurf = !it.channelSurf) } }
                    )
                    SettingsToggleRow(
                        "Inverser le sens de CH+ / CH−",
                        "Haut passe à la chaîne précédente au lieu de la suivante.",
                        s.reverseZap,
                        { update { it.copy(reverseZap = !it.reverseZap) } }
                    )
                    SettingsActionRow(
                        title = "Chaînes rapides",
                        subtitle = "Raccourcis sous la barre de lecture : appuyez sur OK pendant la lecture, puis sur Bas. Choisissez les listes : chaînes récemment regardées (les chaînes pour adultes n’apparaissent jamais), favoris et groupes. Réglage aussi disponible dans la barre de lecture : engrenage › Disposition.",
                        value = if (!com.nuvio.tv.livetv.model.PanelRowLists.shown(s)) "Désactivé" else {
                            val n = com.nuvio.tv.livetv.model.PanelRowLists.parse(s).count { it.on }
                            if (n == 1) "1 liste" else "$n listes"
                        },
                        onClick = { dialog = LiveDialog.ChannelRow }
                    )
                    SettingsActionRow(
                        title = "Arrière-plan de la barre de lecture",
                        subtitle = "Règle l’assombrissement de la barre de lecture. Les modes Normal et Le plus sombre facilitent la lecture sur les images claires ou chargées.",
                        value = when (s.panelBackground) { "darker" -> "Normal"; "solid" -> "Le plus sombre"; else -> "Le plus clair" },
                        onClick = {
                            update {
                                it.copy(panelBackground = when (it.panelBackground) { "standard" -> "darker"; "darker" -> "solid"; else -> "standard" })
                            }
                        }
                    )
                    SettingsActionRow(
                        title = "Durée d’affichage de la barre d’informations",
                        subtitle = "Durée d’affichage des informations après un changement de chaîne ou un appui sur OK.",
                        value = "${s.infoBannerSeconds} secondes",
                        onClick = { dialog = LiveDialog.BannerTime }
                    )
                }
            }

            // ------------------------------------------------------------ Picture, Sound & Clock
            if (show(LiveTvPage.PLAYBACK)) item(key = "picture") {
                LiveSection(
                    title = "Image, son et horloge",
                    summary = "Horloge, fréquence d’images, pause et replay.",
                    icon = Icons.Default.PlayCircleFilled,
                    open = "picture" in open,
                    onToggle = { toggle("picture") }
                ) {
                    SubHeading("Horloge à l’écran")
                    SettingsActionRow(
                        title = "Afficher l’horloge",
                        subtitle = "Affiche une horloge dans un coin pendant la lecture. Avec les commandes du lecteur : uniquement dans le panneau d’informations.",
                        value = CLOCK_MODES.firstOrNull { it.first == s.clockMode }?.second ?: "Avec les commandes du lecteur",
                        onClick = { dialog = LiveDialog.ClockMode }
                    )
                    if (s.clockMode == "always") {
                        SettingsActionRow(
                            title = "Clock Position",
                            subtitle = "Coin de l’écran où afficher l’horloge.",
                            value = CLOCK_POSITIONS.firstOrNull { it.first == s.clockPosition }?.second ?: "En haut à droite",
                            onClick = { dialog = LiveDialog.ClockPosition }
                        )
                        SettingsActionRow(
                            title = "Taille de l’horloge",
                            subtitle = "Taille de l’horloge.",
                            value = CLOCK_SIZES.firstOrNull { it.first == s.clockSize }?.second ?: "Moyen",
                            onClick = { dialog = LiveDialog.ClockSize }
                        )
                        SettingsActionRow(
                            title = "Clock Opacity",
                            subtitle = "Plus la valeur est basse, plus l’horloge est transparente et discrète.",
                            value = "${s.clockOpacity}%",
                            onClick = { dialog = LiveDialog.ClockOpacity }
                        )
                        SettingsToggleRow(
                            "Fond de l’horloge",
                            "Fond sombre derrière l’heure pour la rendre lisible sur les images claires.",
                            s.clockBackground,
                            { update { it.copy(clockBackground = !it.clockBackground) } }
                        )
                    }
                    SubHeading("Image et son")
                    SettingsToggleRow(
                        "Activer les sous-titres par défaut",
                        "Active les sous-titres lorsqu’ils sont disponibles. Vous pouvez toujours les désactiver avec CC.",
                        s.captionsByDefault,
                        { update { it.copy(captionsByDefault = !it.captionsByDefault) } }
                    )
                    SettingsToggleRow(
                        "Transmission audio directe",
                        "Transmet le son Dolby sans modification à votre téléviseur ou enceinte. Désactivez cette option si le son se coupe, notamment avec les enceintes Echo ou Alexa Home Theater sur Fire TV.",
                        s.audioPassthrough,
                        { update { it.copy(audioPassthrough = !it.audioPassthrough) } }
                    )
                    SettingsActionRow(
                        title = "Adapter la fréquence d’images",
                        subtitle = "Adapte la fréquence du téléviseur à celle de la vidéo (par exemple 50 Hz pour les chaînes européennes) afin d’obtenir une image plus fluide, si le téléviseur le permet.",
                        value = FRAME_RATE_MODES.firstOrNull { it.first == s.frameRateMode }?.second ?: "Désactivé",
                        onClick = { dialog = LiveDialog.FrameRate }
                    )
                    SubHeading("Pause et replay")
                    SettingsToggleRow(
                        "Mettre en pause et revenir en arrière",
                        "Sur les chaînes sans replay, la pause enregistre la chaîne sur cet appareil afin de reprendre au même endroit.",
                        s.timeshiftEnabled,
                        { update { it.copy(timeshiftEnabled = !it.timeshiftEnabled) } }
                    )
                    if (s.timeshiftEnabled) SettingsToggleRow(
                        "Revenir en arrière à tout moment",
                        "En plein écran, les chaînes sont enregistrées en continu. Vous pouvez revenir en arrière sans mettre en pause (Gauche/Droite après avoir quitté le direct, ou depuis le panneau). Le démarrage des chaînes prend une seconde de plus.",
                        s.rewindAlways,
                        { update { it.copy(rewindAlways = !it.rewindAlways) } }
                    )
                    if (s.timeshiftEnabled) {
                        // Longer buffers when recordings go to a USB drive (far more room).
                        val onDrive = s.recordingStorage.isNotEmpty()
                        // 10 minutes by default: Fire TV Sticks and similar have little free space.
                        val lengths = if (onDrive) listOf(10, 15, 30, 60, 120, 240) else listOf(10, 15, 30, 60, 120)
                        SettingsActionRow(
                            title = "Durée maximale de l’enregistrement glissant",
                            subtitle = "Durée de retour en arrière. Enregistrement stocké sur le support choisi dans « Enregistrements », puis supprimé au changement de chaîne ou à la fermeture de la TV en direct.",
                            value = "${s.timeshiftMinutes} min · about " + (s.timeshiftMinutes * 35).let { mb -> if (mb >= 1000) String.format(java.util.Locale.US, "%.1f GB", mb / 1024f) else "$mb MB" },
                            onClick = {
                                update { st ->
                                    val i = lengths.indexOf(st.timeshiftMinutes)
                                    st.copy(timeshiftMinutes = lengths[if (i < 0) 1 else (i + 1) % lengths.size])
                                }
                            }
                        )
                    }
                    SettingsToggleRow(
                        "Accélérer la navigation dans les replays",
                        "Enregistre le replay sur l’appareil pendant la lecture pour revenir en arrière ou avancer sans rechargement. Utilise environ 35 Mo par minute et est supprimé à la fin de la lecture.",
                        s.catchupCache,
                        { update { it.copy(catchupCache = !it.catchupCache) } }
                    )
                    SettingsToggleRow(
                        "Préférer HLS pour les replays",
                        "Demande d’abord les replays au format HLS (m3u8) aux fournisseurs Xtream pour permettre la navigation. Utilise le format TS si le fournisseur ne propose pas HLS.",
                        s.catchupPreferHls,
                        { update { it.copy(catchupPreferHls = !it.catchupPreferHls) } }
                    )
                    SettingsNote("Le décalage horaire des replays se règle pour chaque playlist : ouvrez « Playlists », puis choisissez-en une.")
                    SubHeading("Connexion")
                    SettingsActionRow(
                        title = "Taille du tampon",
                        subtitle = "Un tampon plus grand compense une connexion ou un fournisseur lent (moins de pauses). Un tampon plus petit accélère le changement de chaîne. Aucun : direct plus proche et changement rapide, mais le moindre ralentissement provoque une pause.",
                        value = when (s.bufferSize) { "none" -> "Aucun"; "small" -> "Petit"; "large" -> "Grand"; "xlarge" -> "Très grand"; else -> "Normal" },
                        onClick = {
                            update { st -> st.copy(bufferSize = when (st.bufferSize) { "none" -> "small"; "small" -> "normal"; "normal" -> "large"; "large" -> "xlarge"; else -> "none" }) }
                        }
                    )
                    SettingsToggleRow(
                        "Reconnexion automatique",
                        "Relance automatiquement le flux s’il se coupe ou se bloque.",
                        s.autoReconnect,
                        { update { it.copy(autoReconnect = !it.autoReconnect) } }
                    )
                    SettingsToggleRow(
                        "Basculer sur une chaîne de secours",
                        "Si une chaîne ne se lance pas, essaie une autre chaîne du même nom dans un autre groupe ou une autre playlist (par exemple « ESPN » et « ESPN Backup »)." +
                            "Pendant un match en direct, passe également à la chaîne suivante qui diffuse le match si l’image se bloque continuellement.",
                        s.autoBackupChannel,
                        { update { it.copy(autoBackupChannel = !it.autoBackupChannel) } }
                    )
                }
            }

            // ------------------------------------------------------------ Guide Listings
            if (show(LiveTvPage.GUIDE)) item(key = "listings") {
                LiveSection(
                    title = "Programmes du guide",
                    summary = "Période couverte par le guide.",
                    icon = Icons.Default.Schedule,
                    open = "listings" in open,
                    onToggle = { toggle("listings") }
                ) {
                    SettingsActionRow(
                        "Jours à venir", "Jusqu’à quelle date le guide conserve les programmes à venir.",
                        if (s.epgFutureDays == 1) "1 jour" else "${s.epgFutureDays} jours", onClick = { dialog = LiveDialog.FutureDays }
                    )
                    SettingsActionRow(
                        "Historique conservé", "Durée de conservation des anciens programmes pour le replay.",
                        hoursLabel(s.epgPastHours), onClick = { dialog = LiveDialog.PastHours }
                    )
                    SettingsNote("Le décalage du guide se règle pour chaque source : choisissez une playlist (guide intégré) ou une source EPG.")
                    val clearContext = androidx.compose.ui.platform.LocalContext.current
                    SettingsActionRow(
                        "Effacer les affiches et les informations des programmes",
                        "Supprime les affiches et informations trouvées pour la TV en direct afin de les rechercher à nouveau auprès de vos extensions.",
                        onClick = {
                            confirm(
                                "Effacer les affiches et les informations des programmes ?",
                                "Les affiches et résumés trouvés pour la TV en direct seront supprimés. Ils seront recherchés à nouveau auprès de vos extensions pendant la navigation.",
                                "Effacer"
                            ) {
                                viewModel.clearPosterData()
                                android.widget.Toast.makeText(clearContext, "Affiches et informations des programmes effacées", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            // ------------------------------------------------------------ Home Screen & Nuvio
            if (show(LiveTvPage.INTEGRATION)) item(key = "nuvio") {
                LiveSection(
                    title = "Écran d’accueil et JojoTV",
                    summary = "Ligne TV en direct sur l’accueil de JojoTV et entrée du menu.",
                    icon = Icons.Default.Home,
                    open = "nuvio" in open,
                    onToggle = { toggle("nuvio") },
                    focusFirst = firstFocus(LiveTvPage.INTEGRATION)
                ) {
                    // ---- Live TV row on the home screen
                    // On or off for the Nuvio profile in use (each profile has its own choice).
                    val homeProfileContext = androidx.compose.ui.platform.LocalContext.current
                    val homeProfileId by remember {
                        dagger.hilt.android.EntryPointAccessors.fromApplication(
                            homeProfileContext.applicationContext, com.nuvio.tv.livetv.startup.LiveTvStartupEntryPoint::class.java
                        ).profileManager()
                    }.activeProfileId.collectAsStateWithLifecycle()
                    val homeRowOn = homeProfileId !in s.homeRowHiddenProfiles
                    SubHeading("Lignes sur l’écran d’accueil du téléviseur")
                    SettingsActionRow(
                        title = "Forme des cartes",
                        subtitle = "Adapte les cartes à la ligne de votre lanceur : large pour Google TV et la plupart des lanceurs, portrait pour les affiches, carré pour les lignes carrées. Chaque carte indique la chaîne, le programme et son heure de fin.",
                        value = when (s.launcherCardShape) { "TALL" -> "Portrait"; "SQUARE" -> "Carré"; else -> "Large" },
                        onClick = {
                            update { st -> st.copy(launcherCardShape = when (st.launcherCardShape) { "WIDE" -> "TALL"; "TALL" -> "SQUARE"; else -> "WIDE" }) }
                            com.nuvio.tv.livetv.home.LiveTvLauncherRows.refresh(homeProfileContext, afterMs = 1_500)
                        }
                    )
                    SubHeading("Ligne sur l’accueil de JojoTV")
                    SettingsToggleRow(
                        "Ligne TV en direct sur l’écran d’accueil",
                        "Pour ce profil. Placée au-dessus de la première ligne : appuyez sur Haut pour l’afficher.",
                        homeRowOn,
                        {
                            update { st ->
                                st.copy(
                                    homeRowHiddenProfiles = if (homeProfileId in st.homeRowHiddenProfiles) st.homeRowHiddenProfiles - homeProfileId
                                    else st.homeRowHiddenProfiles + homeProfileId
                                )
                            }
                        }
                    )
                    if (homeRowOn) {
                        SettingsActionRow(
                            title = "Chaînes de la ligne",
                            subtitle = "Chaînes affichées : favoris, chaînes récemment regardées ou l’un de vos groupes.",
                            value = when (s.homeRowSource) {
                                "favorites", "" -> "Favoris"
                                "recent" -> "Récemment regardées"
                                else -> user.customGroups.firstOrNull { it.id == s.homeRowSource }?.name ?: "Favoris"
                            },
                            onClick = { dialog = LiveDialog.HomeRowSource }
                        )
                        SettingsActionRow(
                            title = "Titre de la ligne",
                            subtitle = "Nom affiché au-dessus de la ligne.",
                            value = s.homeRowTitle.ifBlank { "TV en direct" },
                            onClick = { dialog = LiveDialog.HomeRowTitle }
                        )
                        SettingsActionRow(
                            title = "Ordre de la ligne",
                            subtitle = "Votre ordre, le numéro de chaîne ou les programmes qui se terminent le plus tôt.",
                            value = when (s.homeRowSort) { "number" -> "Numéro de chaîne"; "ending" -> "Fin la plus proche"; else -> "Ordre personnalisé" },
                            onClick = {
                                update { st -> st.copy(homeRowSort = when (st.homeRowSort) { "yours" -> "number"; "number" -> "ending"; else -> "yours" }) }
                            }
                        )
                        SettingsActionRow(
                            title = "Chaînes de la ligne",
                            subtitle = "Nombre de chaînes affichées sur la ligne.",
                            value = if (s.homeRowLimit == 0) "Toutes" else "${s.homeRowLimit}",
                            onClick = {
                                update { st -> st.copy(homeRowLimit = when (st.homeRowLimit) { 10 -> 20; 20 -> 40; 40 -> 0; else -> 10 }) }
                            }
                        )
                        SettingsToggleRow(
                            "Masquer les chaînes sans programme",
                            "Masque les chaînes qui affichent seulement « Programme » ou aucune information.",
                            s.homeRowHidePlaceholders,
                            { update { it.copy(homeRowHidePlaceholders = !it.homeRowHidePlaceholders) } }
                        )
                        SettingsToggleRow(
                            "OK ouvre le guide",
                            "Désactivé : OK lance la chaîne en plein écran. Activé : ouvre le guide pendant la lecture.",
                            s.homeRowOpensGuide,
                            { update { it.copy(homeRowOpensGuide = !it.homeRowOpensGuide) } }
                        )
                        SettingsToggleRow(
                            "Afficher le programme suivant",
                            "Affiche sur chaque carte une ligne « À suivre : 21:00 Journal ».",
                            s.homeRowShowNext,
                            { update { it.copy(homeRowShowNext = !it.homeRowShowNext) } }
                        )
                        SettingsToggleRow(
                            "Cartes compactes",
                            "Affiche uniquement le logo et le nom du programme.",
                            s.homeRowCompact,
                            { update { it.copy(homeRowCompact = !it.homeRowCompact) } }
                        )
                    }
                }
            }

            // ------------------------------------------------------------ Search & Streams
            if (show(LiveTvPage.INTEGRATION)) item(key = "search_streams") {
                var pickingStreamsPosition by remember { mutableStateOf(false) }
                LiveSection(
                    title = "Recherche et flux",
                    summary = "Ordre de recherche · contenu à la demande dans les flux",
                    icon = Icons.Default.Search,
                    open = "search_streams" in open,
                    onToggle = { toggle("search_streams") }
                ) {
                    SubHeading("Ordre des résultats de recherche")
                    SearchOrderList(
                        order = com.nuvio.tv.livetv.ui.parseSearchOrder(s.searchOrder),
                        onChange = { newOrder -> update { it.copy(searchOrder = newOrder.joinToString(",")) } }
                    )
                    SettingsToggleRow(
                        "Prévisualiser les chaînes en arrière-plan de la recherche",
                        "OK sur une chaîne lance son flux derrière les résultats pour vérifier qu’il fonctionne et son apparence. Appuyez à nouveau sur OK pour l’ouvrir en plein écran.",
                        s.searchPreview,
                        { update { it.copy(searchPreview = !it.searchPreview) } }
                    )
                    // Search from Home and the side menu always shows everything; these set what
                    // search shows when it's opened from the guide or from On Demand.
                    SubHeading("La recherche depuis le guide affiche")
                    SettingsToggleRow(
                        "TV en direct",
                        "Chaînes et programmes du guide.",
                        s.guideSearchLive,
                        { update { it.copy(guideSearchLive = !it.guideSearchLive) } }
                    )
                    SettingsToggleRow(
                        "Films et séries",
                        "Titres de vos extensions JojoTV.",
                        s.guideSearchCatalog,
                        { update { it.copy(guideSearchCatalog = !it.guideSearchCatalog) } }
                    )
                    SettingsToggleRow(
                        "À la demande",
                        "Films et séries de votre fournisseur.",
                        s.guideSearchOnDemand,
                        { update { it.copy(guideSearchOnDemand = !it.guideSearchOnDemand) } }
                    )
                    SubHeading("La recherche depuis À la demande affiche")
                    SettingsToggleRow(
                        "TV en direct",
                        "Chaînes et programmes du guide.",
                        s.onDemandSearchLive,
                        { update { it.copy(onDemandSearchLive = !it.onDemandSearchLive) } }
                    )
                    SettingsToggleRow(
                        "Films et séries",
                        "Titres de vos extensions JojoTV.",
                        s.onDemandSearchCatalog,
                        { update { it.copy(onDemandSearchCatalog = !it.onDemandSearchCatalog) } }
                    )
                    SettingsToggleRow(
                        "À la demande",
                        "Films et séries de votre fournisseur.",
                        s.onDemandSearchOnDemand,
                        { update { it.copy(onDemandSearchOnDemand = !it.onDemandSearchOnDemand) } }
                    )
                    SubHeading("Liste des flux")
                    SettingsToggleRow(
                        "Contenu à la demande dans les flux",
                        "Version de votre fournisseur proposée avec les flux des extensions pour les films et les épisodes.",
                        s.onDemandInStreams,
                        { update { it.copy(onDemandInStreams = !it.onDemandInStreams) } }
                    )
                    SettingsActionRow(
                        title = "Position de l’onglet à la demande",
                        subtitle = "Position de son onglet dans la liste des flux, de gauche à droite après « Toutes ».",
                        value = streamsPositionLabel(s.onDemandStreamsPosition),
                        onClick = { pickingStreamsPosition = true }
                    )
                    SettingsToggleRow(
                        "Laisser la lecture automatique choisir le contenu à la demande",
                        "Uniquement si son onglet est en premier et que la lecture automatique utilise toutes les sources.",
                        s.onDemandAutoPlay,
                        { update { it.copy(onDemandAutoPlay = !it.onDemandAutoPlay) } }
                    )
                    if (pickingStreamsPosition) SettingsSingleChoiceDialog(
                        title = "Position de l’onglet à la demande",
                        options = listOf("FIRST", "AFTER_FIRST", "LAST").map { SettingsPickerOption(it, streamsPositionLabel(it)) },
                        selectedValue = s.onDemandStreamsPosition,
                        onOptionSelected = { v -> update { it.copy(onDemandStreamsPosition = v) }; pickingStreamsPosition = false },
                        onDismiss = { pickingStreamsPosition = false }
                    )
                }
            }

            // ------------------------------------------------------------ Parental Controls
            if (show(LiveTvPage.MAIN)) item(key = "parental") {
                LiveSection(
                    title = "Contrôle parental",
                    summary = if (hasPin) "Activé · ${user.lockedGroups.size} verrouillés" else "Désactivé",
                    icon = Icons.Default.Lock,
                    open = "parental" in open,
                    onToggle = { toggle("parental") }
                ) {
                    Text(
                        text = "Le code PIN est nécessaire pour ouvrir les groupes et catégories verrouillés. Ils restent masqués dans « Toutes les chaînes » et les recherches jusqu’au déverrouillage.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NuvioTheme.colors.TextSecondary,
                        modifier = Modifier.padding(horizontal = NuvioTheme.spacing.lg, vertical = NuvioTheme.spacing.sm)
                    )
                    SettingsActionRow(
                        if (hasPin) "Modifier le code PIN" else "Définir un code PIN",
                        if (hasPin) "Le contrôle parental est activé." else "Active le contrôle parental.",
                        onClick = { pinWrong = false; dialog = if (hasPin) LiveDialog.PinCheck else LiveDialog.PinNew }
                    )
                    if (hasPin) {
                        SettingsToggleRow(
                            "Verrouiller automatiquement les contenus pour adultes",
                            "Verrouille les groupes et catégories dont le nom contient « Adult », « XXX » ou « 18+ ».",
                            s.lockAdultContent,
                            { update { it.copy(lockAdultContent = !it.lockAdultContent) } }
                        )
                        SettingsActionRow(
                            "Désactiver le contrôle parental", "Supprime le code PIN. Vos groupes verrouillés sont conservés.",
                            "${user.lockedGroups.size} verrouillés",
                            onClick = { pinWrong = false; dialog = LiveDialog.PinRemove }
                        )
                    }
                }
            }

            // ------------------------------------------------------------ Reminders
            if (show(LiveTvPage.GUIDE)) item(key = "reminders") {
                LiveSection(
                    title = "Rappels",
                    summary = if (user.reminders.isEmpty()) "Aucun rappel défini" else "${user.reminders.size} à venir",
                    icon = Icons.Default.Notifications,
                    open = "reminders" in open,
                    onToggle = { toggle("reminders") }
                ) {
                    SettingsActionRow(
                        "Rappels à venir",
                        "Définis depuis les informations d’un programme dans le guide. Une notification apparaît une minute avant son début.",
                        "${user.reminders.size}",
                        onClick = { dialog = LiveDialog.Reminders }
                    )
                }
            }

            // ------------------------------------------------------------ Recordings
            if (show(LiveTvPage.PLAYBACK)) item(key = "recordings") {
                val recContext = androidx.compose.ui.platform.LocalContext.current
                // Drives come and go: read again whenever the section is opened.
                val drives = remember("recordings" in open) { com.nuvio.tv.livetv.record.RecordingStorage.choices(recContext) }
                val driveProblems = remember("recordings" in open) { com.nuvio.tv.livetv.record.RecordingStorage.problems(recContext) }
                val chosen = drives.firstOrNull { it.id == s.recordingStorage }
                fun driveLabel(c: com.nuvio.tv.livetv.record.RecordingStorage.Choice) =
                    c.label + " · " + com.nuvio.tv.livetv.record.RecordingStorage.sizeLabel(c.freeBytes) + " libres"
                var pickingDrive by remember { mutableStateOf(false) }
                LiveSection(
                    title = "Enregistrements",
                    summary = "Enregistré sur " + (chosen?.label ?: if (s.recordingStorage.isEmpty()) "Cet appareil" else "un support qui n’est pas branché"),
                    icon = Icons.Default.FiberManualRecord,
                    open = "recordings" in open,
                    onToggle = { toggle("recordings") }
                ) {
                    SettingsActionRow(
                        title = "Enregistrer les vidéos sur",
                        subtitle = if (chosen == null && s.recordingStorage.isNotEmpty())
                            "Le support choisi n’est pas branché : les enregistrements seront stockés sur cet appareil jusqu’à ce qu’il soit reconnecté."
                        else "Emplacement des nouveaux enregistrements. Pour déplacer un enregistrement existant, maintenez OK dessus dans « Enregistrements », puis choisissez « Déplacer vers ».",
                        value = chosen?.label ?: "Cet appareil",
                        onClick = { pickingDrive = true }
                    )
                    SettingsActionRow(
                        title = "Démarrer l’enregistrement en avance",
                        subtitle = "Démarre l’enregistrement avant le programme, au cas où il commencerait plus tôt. Les enregistrements déjà programmés ne changent pas.",
                        value = padLabel(s.recordPreMinutes),
                        onClick = { dialog = LiveDialog.RecordPre }
                    )
                    SettingsActionRow(
                        title = "Prolonger l’enregistrement",
                        subtitle = "Prolonge l’enregistrement après le programme, au cas où il se terminerait plus tard. Les enregistrements déjà programmés ne changent pas.",
                        value = padLabel(s.recordPostMinutes),
                        onClick = { dialog = LiveDialog.RecordPost }
                    )
                    SettingsActionRow(
                        title = "Quand l’espace de stockage manque",
                        subtitle = if (s.recordingSpaceRule == "DELETE_OLDEST") "Les plus anciens enregistrements de ce support sont supprimés pour libérer de l’espace."
                        else "L’enregistrement s’arrête (aucun fichier n’est supprimé).",
                        value = if (s.recordingSpaceRule == "DELETE_OLDEST") "Supprimer les plus anciens" else "Arrêter l’enregistrement",
                        onClick = {
                            if (s.recordingSpaceRule == "DELETE_OLDEST") update { it.copy(recordingSpaceRule = "STOP") }
                            else confirm(
                                "Supprimer les anciens enregistrements ?",
                                "Lorsque l’espace manque, les plus anciens enregistrements de ce support seront supprimés pour faire de la place aux nouveaux.",
                                "Activer"
                            ) { update { it.copy(recordingSpaceRule = "DELETE_OLDEST") } }
                        }
                    )
                    if (pickingDrive) ActionListDialog(
                        title = "Enregistrer les vidéos sur",
                        actions = drives.map { c ->
                            (driveLabel(c) + if (c.id == (chosen?.id ?: "")) "  ✓" else "") to {
                                pickingDrive = false
                                update { it.copy(recordingStorage = c.id) }
                            }
                        } + driveProblems.map { p ->
                            // Plugged in but not usable: say why instead of leaving it out.
                            "${p.label} · inutilisable" to {
                                pickingDrive = false
                                confirm("${p.label} n’est pas disponible", p.reason, "OK") { }
                            }
                        } + listOf(
                            "Le support n’apparaît pas ?" to {
                                pickingDrive = false
                                confirm(
                                    "Le support n’apparaît pas ?",
                                    "Android n’a pas détecté le support. Vérifiez qu’il apparaît dans les paramètres du boîtier (Stockage). " +
                                        "Les grands supports nécessitent souvent plus d’énergie que le port USB du boîtier TV n’en fournit. Utilisez un support avec sa propre " +
                                        "alimentation ou un hub USB alimenté. Formatez le support en exFAT (le format NTFS est en lecture seule " +
                                        "sur de nombreux boîtiers, et FAT32 ne permet pas de stocker des fichiers de plus de 4 Go). Ne le configurez pas en « stockage de l’appareil » :" +
                                        "choisissez le stockage amovible.",
                                    "OK"
                                ) { }
                            }
                        ),
                        onDismiss = { pickingDrive = false }
                    )
                }
            }

            // ------------------------------------------------------------ Help
            if (show(LiveTvPage.MAIN)) item(key = "help") {
                val crashContext = androidx.compose.ui.platform.LocalContext.current
                LiveSection(
                    title = "Aide",
                    summary = "Rapport de plantage · rapport de démarrage",
                    icon = Icons.AutoMirrored.Filled.Help,
                    open = "help" in open,
                    onToggle = { toggle("help") }
                ) {
                    SettingsActionRow(
                        "Rapport de plantage",
                        "Informations sur le dernier plantage, blocage ou arrêt de l’application. Scannez le code avec votre téléphone pour l’envoyer.",
                        onClick = { com.nuvio.tv.livetv.crash.CrashCatcher.openLastReport(crashContext) }
                    )
                    SettingsActionRow(
                        "Rapport de démarrage",
                        "Opérations effectuées par la TV en direct au chargement, durée et erreurs éventuelles.",
                        onClick = { dialog = LiveDialog.LoadReport }
                    )
                    // Hidden developer tools: only shown after typing the secret word into the
                    // channel name cleanup (see NameEditor below).
                    if (developerToolsOn) {
                        SubHeading("Outils de développement")
                        SettingsActionRow(
                            "Tester la recherche d’affiches",
                            "Résultats renvoyés par vos extensions pour un titre.",
                            onClick = { dialog = LiveDialog.PosterTest }
                        )
                        SettingsActionRow(
                            "Testeur de boutons de la télécommande",
                            "Affiche chaque bouton envoyé par la télécommande.",
                            onClick = { dialog = LiveDialog.ButtonTester }
                        )
                    }
                }
            }
        }
        } // LocalLiveFlat
        } // key(page)
    }

    drive.signIn?.let { prompt ->
        DriveSignInDialog(prompt = prompt, onCancel = { viewModel.driveSync.cancelSignIn() })
    }

    // ---------------------------------------------------------------- dialogs
    val close = { dialog = null }
    when (val d = dialog) {
        null -> Unit
        LiveDialog.AddM3u -> SourceFormDialog(
            title = "Ajouter une playlist M3U",
            fields = listOf(
                FormField("Nom", "Mon fournisseur"),
                FormField("URL de la playlist", "http://…/playlist.m3u"),
                FormField("User-Agent (facultatif)", "Leave empty for default")
            ),
            onDismiss = close,
            onSave = { v, _ -> if (v[1].isNotBlank()) viewModel.addPlaylist(v[0], v[1], v[2]); close() }
        )
        LiveDialog.AddXtream -> SourceFormDialog(
            title = "Ajouter des identifiants Xtream Codes",
            fields = listOf(
                FormField("Nom", "Mon fournisseur"),
                FormField("Serveur", "http://host:port"),
                FormField("Nom d’utilisateur", ""),
                FormField("Mot de passe", "")
            ),
            onDismiss = close,
            onSave = { v, _ -> if (v[1].isNotBlank()) viewModel.addXtream(v[0], v[1], v[2], v[3]); close() },
            toggleLive = "Inclure les chaînes TV" to true,
            toggle2 = "Inclure les films et séries (à la demande)" to true,
            onSaveAll = { v, _, vod, live ->
                if (v[1].isNotBlank()) viewModel.addXtream(v[0], v[1], v[2], v[3], importVod = vod, importLive = live)
                close()
            }
        )
        LiveDialog.AddEpg -> SourceFormDialog(
            title = "Ajouter une source EPG",
            fields = listOf(FormField("Nom", "Guide TV"), FormField("XMLTV URL", "http://…/epg.xml.gz")),
            onDismiss = close,
            onSave = { v, _ -> if (v[1].isNotBlank()) viewModel.addEpg(v[0], v[1]); close() }
        )
        LiveDialog.AddonChannels -> AddonChannelsDialog(
            addons = installedAddons,
            selected = addonChannels,
            onToggle = { addon, catalog, enabled ->
                viewModel.setAddonCatalogEnabled(addon, catalog, enabled)
            },
            onDismiss = close
        )
        is LiveDialog.PlaylistActions -> ActionListDialog(
            title = d.source.name,
            actions = listOfNotNull(
                // The account, as the provider last reported it (nothing to do: shows the details).
                accounts[d.source.id]?.let { a ->
                    val parts = listOfNotNull(
                        a.status.takeIf { it.isNotBlank() },
                        a.expiryLabel()?.let { "Expire le $it" } ?: "Aucune date d’expiration",
                        a.maxConnections.takeIf { it > 0 }?.let { "${a.activeConnections} sur $it connexions utilisées" }
                    )
                    ("Compte : " + parts.joinToString(" · ")) to { viewModel.updatePlaylistNow(d.source); close() }
                },
                "Modifier" to { dialog = LiveDialog.EditPlaylist(d.source) },
                "Mettre à jour maintenant" to { viewModel.updatePlaylistNow(d.source); close() },
                (if (d.source.enabled) "Désactiver" else "Activer") to { viewModel.setPlaylistEnabled(d.source.id, !d.source.enabled); close() },
                "Supprimer la playlist" to { dialog = LiveDialog.ConfirmDeletePlaylist(d.source) },
                // Whose channel logos to show: the playlist's own or the guide's (each falls back
                // to the other when it has none).
                (if (d.source.logoSource == "epg") "Logos : privilégier ceux du guide EPG" else "Logos : privilégier ceux de la playlist") to {
                    viewModel.setPlaylistLogoSource(d.source.id, if (d.source.logoSource == "epg") "playlist" else "epg")
                    close()
                },
                // Which stream format to ask for. Xtream: Auto follows what the account allows.
                // M3U links: Auto keeps the playlist's own links; TS / HLS switch links that can
                // be switched (Xtream-style links, Dispatcharr).
                "Format du flux : " + when (d.source.streamFormat) {
                    "ts" -> "TS"; "m3u8" -> "HLS (m3u8)"; else -> if (d.source.isXtream) "Automatique" else "Comme dans la playlist"
                } to {
                    val next = when (d.source.streamFormat) { "auto" -> "ts"; "ts" -> "m3u8"; else -> "auto" }
                    viewModel.savePlaylist(d.source.copy(streamFormat = next))
                    close()
                },
                // Which clock replay links are written in. Automatic: the one the provider's
                // server reports (or that the app worked out); pick one when that's wrong.
                "Fuseau horaire du replay : " + catchupZoneLabel(d.source) to { dialog = LiveDialog.CatchupZone(d.source) },
                // Time shifts for this playlist: its built-in guide, and its catch-up.
                ("Décalage du guide : " + offsetLabel(d.source.guideShiftMinutes)).takeIf { d.source.liveEnabled }?.let {
                    it to { dialog = LiveDialog.PlaylistGuideShift(d.source) }
                },
                ("Décalage du replay : " + offsetLabel(d.source.catchupShiftMinutes)).takeIf { d.source.liveEnabled }?.let {
                    it to { dialog = LiveDialog.PlaylistCatchupShift(d.source) }
                },
                "Déplacer vers le haut" to { viewModel.movePlaylist(d.source.id, -1); close() },
                "Déplacer vers le bas" to { viewModel.movePlaylist(d.source.id, 1); close() }
            ),
            onDismiss = close
        )
        is LiveDialog.CatchupZone -> SettingsSingleChoiceDialog(
            title = "Fuseau horaire du replay",
            subtitle = "Fuseau horaire utilisé par ${d.source.name} pour ses replays. Le mode automatique le détecte auprès du fournisseur. " +
                "Si le replay lance un autre programme, choisissez le fuseau horaire correspondant à celui du fournisseur.",
            options = listOf(
                SettingsPickerOption(
                    "", "Automatique",
                    description = d.source.serverTimezone.takeIf { it.isNotBlank() }?.let { "Détecté : ${zoneName(it)}" } ?: "Pas encore détecté"
                )
            ) + CATCHUP_ZONES.map { (id, name) -> SettingsPickerOption(id, name, trailing = zoneClock(id)) },
            selectedValue = d.source.catchupTimezone,
            onOptionSelected = { v -> viewModel.setPlaylistCatchupZone(d.source.id, v); close() },
            onDismiss = close,
            maxHeight = 420.dp
        )
        is LiveDialog.EditPlaylist -> {
            val src = d.source
            if (src.isXtream) {
                SourceFormDialog(
                    title = "Modifier les identifiants Xtream",
                    fields = listOf(
                        FormField("Nom", "", src.name),
                        FormField("Serveur", "", src.xtreamServer),
                        FormField("Nom d’utilisateur", "", src.xtreamUsername),
                        FormField("Mot de passe", "", src.xtreamPassword),
                        FormField("User-Agent (facultatif)", "", src.userAgent)
                    ),
                    toggle = "Charger le guide intégré" to src.useEmbeddedEpg,
                    toggleLive = "Inclure les chaînes TV" to src.importLive,
                    toggle2 = "Inclure les films et séries (à la demande)" to src.importVod,
                    onDismiss = close,
                    onSave = { v, t ->
                        viewModel.savePlaylist(src.copy(name = v[0], xtreamServer = v[1], xtreamUsername = v[2], xtreamPassword = v[3], userAgent = v[4], useEmbeddedEpg = t))
                        close()
                    },
                    onSaveAll = { v, t, vod, live ->
                        viewModel.savePlaylist(
                            src.copy(
                                name = v[0], xtreamServer = v[1], xtreamUsername = v[2], xtreamPassword = v[3],
                                userAgent = v[4], useEmbeddedEpg = t, importVod = vod, importLive = live
                            )
                        )
                        close()
                    }
                )
            } else {
                SourceFormDialog(
                    title = "Modifier la playlist",
                    fields = listOf(
                        FormField("Nom", "", src.name),
                        FormField("URL de la playlist", "", src.url),
                        FormField("User-Agent (facultatif)", "", src.userAgent)
                    ),
                    toggle = "Charger le guide associé à la playlist" to src.useEmbeddedEpg,
                    onDismiss = close,
                    onSave = { v, t ->
                        viewModel.savePlaylist(src.copy(name = v[0], url = v[1], userAgent = v[2], useEmbeddedEpg = t))
                        close()
                    }
                )
            }
        }
        is LiveDialog.EpgActions -> ActionListDialog(
            title = d.source.name,
            actions = listOf(
                "Modifier" to { dialog = LiveDialog.EditEpg(d.source) },
                "Mettre à jour maintenant" to { viewModel.saveEpg(d.source); close() },
                ("Décalage horaire : " + offsetLabel(d.source.shiftMinutes)) to { dialog = LiveDialog.EpgSourceShift(d.source) },
                (if (d.source.enabled) "Désactiver" else "Activer") to { viewModel.setEpgEnabled(d.source.id, !d.source.enabled); close() },
                "Supprimer la source EPG" to { dialog = LiveDialog.ConfirmDeleteEpg(d.source) }
            ),
            onDismiss = close
        )
        LiveDialog.PinCheck -> TextInputDialog(
            title = if (pinWrong) "Code PIN incorrect, réessayez" else "Saisissez votre code PIN actuel",
            initial = "", hint = "Code PIN actuel", confirmLabel = "Suivant", numeric = true,
            onDismiss = close,
            onConfirm = { if (it.trim() == s.parentalPin) { pinWrong = false; dialog = LiveDialog.PinNew } else pinWrong = true }
        )
        LiveDialog.PinNew -> TextInputDialog(
            title = if (pinWrong) "Le code PIN doit comporter entre 4 et 8 chiffres." else "Choisissez un code PIN (4 à 8 chiffres)",
            initial = "", hint = "Nouveau code PIN", confirmLabel = "Enregistrer", numeric = true,
            onDismiss = close,
            onConfirm = { pin ->
                val p = pin.trim()
                if (p.length in 4..8 && p.all { it.isDigit() }) { update { it.copy(parentalPin = p) }; close() } else pinWrong = true
            }
        )
        LiveDialog.PinRemove -> TextInputDialog(
            title = if (pinWrong) "Code PIN incorrect, réessayez" else "Saisissez votre code PIN pour désactiver le contrôle parental",
            initial = "", hint = "Code PIN", confirmLabel = "Désactiver", numeric = true,
            onDismiss = close,
            onConfirm = { if (it.trim() == s.parentalPin) { update { st -> st.copy(parentalPin = "") }; close() } else pinWrong = true }
        )
        LiveDialog.HomeRowSource -> SettingsSingleChoiceDialog(
            title = "Chaînes de la ligne",
            options = buildList {
                add(SettingsPickerOption("favorites", "Favoris"))
                add(SettingsPickerOption("recent", "Récemment regardées"))
                user.customGroups.forEach { add(SettingsPickerOption(it.id, it.name)) }
            },
            selectedValue = s.homeRowSource.ifBlank { "favorites" },
            onOptionSelected = { v -> update { it.copy(homeRowSource = v) }; close() },
            onDismiss = close
        )
        LiveDialog.HomeRowTitle -> TextInputDialog(
            title = "Titre de la ligne d’accueil",
            initial = s.homeRowTitle,
            hint = "TV en direct",
            confirmLabel = "Enregistrer",
            onDismiss = close,
            onConfirm = { t -> update { it.copy(homeRowTitle = t.trim()) }; close() }
        )
        LiveDialog.ButtonTester -> com.nuvio.tv.livetv.ui.RemoteButtonTester(onClose = close)
        LiveDialog.LoadReport -> com.nuvio.tv.livetv.ui.LoadReportDialog(onDismiss = close)
        LiveDialog.PosterTest -> {
            val result by viewModel.posterTest.collectAsStateWithLifecycle()
            if (result == null) {
                TextInputDialog(
                    title = "Tester la recherche d’affiches",
                    initial = "",
                    hint = "Par exemple : Family Guy",
                    confirmLabel = "Tester",
                    onDismiss = close,
                    onConfirm = { viewModel.testPoster(it) }
                )
            } else {
                NuvioDialog(onDismiss = { viewModel.posterTest.value = null; close() }, title = "Recherche d’affiches", width = 720.dp) {
                    Text(
                        text = result.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = NuvioTheme.colors.TextSecondary,
                        modifier = Modifier.heightIn(max = 320.dp).verticalScroll(androidx.compose.foundation.rememberScrollState())
                    )
                    SettingsDialogActionRow {
                        SettingsDialogActionButton(text = "Fermer", onClick = { viewModel.posterTest.value = null; close() }, primary = true)
                    }
                }
            }
        }
        LiveDialog.NameEditor -> TextInputDialog(
            title = "Préfixes et suffixes à retirer",
            initial = s.nameRemovals,
            hint = "Ex. : USA, US, UK, 24/7, FHD, HD",
            confirmLabel = "Enregistrer",
            onDismiss = close,
            onConfirm = { text ->
                // Secret word: turns the hidden developer tools on or off instead of saving.
                if (text.trim().equals(DEVELOPER_WORD, ignoreCase = true)) {
                    com.nuvio.tv.livetv.model.DeveloperTools.toggle() // until the app next starts
                } else {
                    update { it.copy(nameRemovals = text.trim()) }
                }
                close()
            }
        )
        LiveDialog.StartPage -> SettingsSingleChoiceDialog(
            title = "Ouvrir l’application sur",
            // Live TV while it's in the menu; On Demand once something has been imported.
            options = buildList {
                add(SettingsPickerOption("HOME", "Accueil"))
                if (s.showInSidebar) add(SettingsPickerOption("TV en direct", "TV en direct"))
                if (onDemandHas) add(SettingsPickerOption("ON_DEMAND", "À la demande"))
                add(SettingsPickerOption("Sports", "Sports"))
            },
            selectedValue = s.openOn,
            onOptionSelected = { v -> update { it.copy(openOn = v) }; close() },
            onDismiss = close
        )
        LiveDialog.Reminders -> ActionListDialog(
            title = if (user.reminders.isEmpty()) "Aucun rappel" else "Rappels (sélectionnez pour supprimer)",
            actions = user.reminders.map { r ->
                val whenText = java.text.SimpleDateFormat("EEE h:mm a", java.util.Locale.getDefault()).format(java.util.Date(r.startMs))
                "$whenText · ${r.title} · ${r.channelName}" to { viewModel.removeReminder(r.channelKey, r.startMs); Unit }
            }.ifEmpty { listOf("Fermer" to close) },
            onDismiss = close
        )
        is LiveDialog.Confirm -> ConfirmDeleteDialog(
            title = d.title,
            message = d.message,
            onDismiss = close,
            onConfirm = { d.action(); close() },
            confirmLabel = d.label
        )
        LiveDialog.ConfirmRestore -> {
            // When the saved copy was made, and on which TV: restoring an old copy puts back old
            // playlists and logins (a provider's old address, an old password).
            var loaded by remember { mutableStateOf(false) }
            var info by remember { mutableStateOf<com.nuvio.tv.livetv.sync.LiveTvDriveSync.BackupInfo?>(null) }
            LaunchedEffect(Unit) {
                info = runCatching { viewModel.driveSync.backupInfo() }.getOrNull()
                loaded = true
            }
            ConfirmDeleteDialog(
                title = "Restaurer depuis Google Drive ?",
                message = restoreMessage(loaded, info, viewModel.driveSync.localChangedAt()),
                onDismiss = close,
                onConfirm = { viewModel.driveSync.restoreNow(); close() },
                confirmLabel = "Restaurer"
            )
        }
        is LiveDialog.ConfirmDeletePlaylist -> ConfirmDeleteDialog(
            title = "Supprimer « ${d.source.name} » ?",
            message = "Ses ${d.source.channelCount} chaînes seront retirées du guide. Leurs favoris, masquages et associations EPG seront conservés si vous l’ajoutez à nouveau.",
            onDismiss = close,
            onConfirm = { viewModel.removePlaylist(d.source.id); close() }
        )
        is LiveDialog.ConfirmDeleteEpg -> ConfirmDeleteDialog(
            title = "Supprimer « ${d.source.name} » ?",
            message = "Les chaînes utilisant ce guide seront associées à un autre guide ou à la recherche automatique.",
            onDismiss = close,
            onConfirm = { viewModel.removeEpg(d.source.id); close() }
        )
        is LiveDialog.EditEpg -> SourceFormDialog(
            title = "Modifier la source EPG",
            fields = listOf(FormField("Nom", "", d.source.name), FormField("XMLTV URL", "", d.source.url)),
            onDismiss = close,
            onSave = { v, _ -> viewModel.saveEpg(d.source.copy(name = v[0], url = v[1])); close() }
        )
        LiveDialog.HiddenChannels -> UnhideDialog(
            title = "Chaînes masquées",
            entries = user.hiddenChannels.sorted().map { it to viewModel.channelLabel(it) },
            onUnhide = { viewModel.unhideChannel(it) },
            onUnhideAll = { viewModel.unhideAllChannels(); close() },
            onDismiss = close
        )
        LiveDialog.HiddenGroups -> UnhideDialog(
            title = "Groupes masqués",
            entries = user.hiddenGroups.sorted().map { it to viewModel.groupLabel(it) },
            onUnhide = { viewModel.unhideGroup(it) },
            onUnhideAll = { viewModel.unhideAllGroups(); close() },
            onDismiss = close
        )
        is LiveDialog.PlaylistGuideShift -> SettingsSingleChoiceDialog(
            title = "Décalage du guide : ${d.source.name}",
            subtitle = "Avance ou retarde le guide intégré à cette playlist lorsque les horaires ne correspondent pas au programme diffusé.",
            options = EPG_OFFSETS.map { SettingsPickerOption(it, offsetLabel(it)) },
            selectedValue = d.source.guideShiftMinutes,
            onOptionSelected = { v -> viewModel.setPlaylistGuideShift(d.source.id, v); close() },
            onDismiss = close
        )
        is LiveDialog.EpgSourceShift -> SettingsSingleChoiceDialog(
            title = "Décalage horaire : ${d.source.name}",
            subtitle = "Avance ou retarde les programmes de ce guide lorsque les horaires ne correspondent pas à la diffusion.",
            options = EPG_OFFSETS.map { SettingsPickerOption(it, offsetLabel(it)) },
            selectedValue = d.source.shiftMinutes,
            onOptionSelected = { v -> viewModel.setEpgShift(d.source.id, v); close() },
            onDismiss = close
        )
        LiveDialog.RecordPre -> SettingsSingleChoiceDialog(
            title = "Démarrer l’enregistrement en avance",
            options = listOf(0, 1, 2, 5, 10, 15).map { SettingsPickerOption(it, padLabel(it)) },
            selectedValue = s.recordPreMinutes,
            onOptionSelected = { v -> update { it.copy(recordPreMinutes = v) }; close() },
            onDismiss = close
        )
        LiveDialog.RecordPost -> SettingsSingleChoiceDialog(
            title = "Prolonger l’enregistrement",
            options = listOf(0, 2, 5, 10, 15, 30).map { SettingsPickerOption(it, padLabel(it)) },
            selectedValue = s.recordPostMinutes,
            onOptionSelected = { v -> update { it.copy(recordPostMinutes = v) }; close() },
            onDismiss = close
        )
        LiveDialog.FrameRate -> SettingsSingleChoiceDialog(
            title = "Adapter la fréquence d’images",
            options = FRAME_RATE_MODES.map { (v, t, d) -> SettingsPickerOption(v, t, description = d) },
            selectedValue = s.frameRateMode,
            onOptionSelected = { v -> update { it.copy(frameRateMode = v) }; close() },
            onDismiss = close
        )
        LiveDialog.ClockMode -> SettingsSingleChoiceDialog(
            title = "Afficher l’horloge",
            options = CLOCK_MODES.map { SettingsPickerOption(it.first, it.second) },
            selectedValue = s.clockMode,
            onOptionSelected = { v -> update { it.copy(clockMode = v) }; close() },
            onDismiss = close
        )
        LiveDialog.ClockPosition -> SettingsSingleChoiceDialog(
            title = "Position de l’horloge",
            options = CLOCK_POSITIONS.map { SettingsPickerOption(it.first, it.second) },
            selectedValue = s.clockPosition,
            onOptionSelected = { v -> update { it.copy(clockPosition = v) }; close() },
            onDismiss = close
        )
        LiveDialog.ClockSize -> SettingsSingleChoiceDialog(
            title = "Taille de l’horloge",
            options = CLOCK_SIZES.map { SettingsPickerOption(it.first, it.second) },
            selectedValue = s.clockSize,
            onOptionSelected = { v -> update { it.copy(clockSize = v) }; close() },
            onDismiss = close
        )
        LiveDialog.ClockOpacity -> SettingsSingleChoiceDialog(
            title = "Opacité de l’horloge",
            options = listOf(30, 50, 70, 100).map { SettingsPickerOption(it, "$it%") },
            selectedValue = s.clockOpacity,
            onOptionSelected = { v -> update { it.copy(clockOpacity = v) }; close() },
            onDismiss = close
        )
        is LiveDialog.PlaylistCatchupShift -> SettingsSingleChoiceDialog(
            title = "Décalage du replay : ${d.source.name}",
            subtitle = "Si le replay lance le programme trop tôt ou trop tard, corrigez son horaire de ce décalage. Exemple : s’il commence une heure trop tôt, indiquez +1 h.",
            options = EPG_OFFSETS.map { SettingsPickerOption(it, offsetLabel(it)) },
            selectedValue = d.source.catchupShiftMinutes,
            onOptionSelected = { v -> viewModel.setPlaylistCatchupShift(d.source.id, v); close() },
            onDismiss = close
        )
        LiveDialog.ZapModeChoice -> SettingsSingleChoiceDialog(
            title = "CH+ / CH− navigue dans",
            options = listOf(
                SettingsPickerOption(ZapMode.GROUP, "Groupe actuel"),
                SettingsPickerOption(ZapMode.ALL, "Toutes les chaînes")
            ),
            selectedValue = s.zapMode,
            onOptionSelected = { v -> update { it.copy(zapMode = v) }; close() },
            onDismiss = close
        )
        LiveDialog.BannerTime -> SettingsSingleChoiceDialog(
            title = "Durée d’affichage de la barre d’informations",
            options = BANNER_SECONDS.map { SettingsPickerOption(it, "$it seconds") },
            selectedValue = s.infoBannerSeconds,
            onOptionSelected = { v -> update { it.copy(infoBannerSeconds = v) }; close() },
            onDismiss = close
        )
        LiveDialog.PlaylistRefresh -> SettingsSingleChoiceDialog(
            title = "Mettre les playlists à jour toutes les",
            options = REFRESH_HOURS.map { SettingsPickerOption(it, hoursLabel(it)) },
            selectedValue = s.playlistRefreshHours,
            onOptionSelected = { v -> update { it.copy(playlistRefreshHours = v) }; close() },
            onDismiss = close
        )
        LiveDialog.EpgRefresh -> SettingsSingleChoiceDialog(
            title = "Mettre les guides à jour toutes les",
            options = REFRESH_HOURS.map { SettingsPickerOption(it, hoursLabel(it)) },
            selectedValue = s.epgRefreshHours,
            onOptionSelected = { v -> update { it.copy(epgRefreshHours = v) }; close() },
            onDismiss = close
        )
        LiveDialog.FutureDays -> SettingsSingleChoiceDialog(
            title = "Jours à venir dans le guide",
            options = FUTURE_DAYS.map { SettingsPickerOption(it, if (it == 1) "1 jour" else "$it jours") },
            selectedValue = s.epgFutureDays,
            onOptionSelected = { v -> viewModel.updateAndReloadEpg { it.copy(epgFutureDays = v) }; close() },
            onDismiss = close
        )
        LiveDialog.GroupSortChoice -> SettingsSingleChoiceDialog(
            title = "Ordre des groupes",
            options = listOf(
                SettingsPickerOption("custom", "Ordre personnalisé", "Suit l’ordre défini dans « Réorganiser les groupes » ; les autres restent dans l’ordre de la playlist."),
                SettingsPickerOption("playlist", "Ordre de la playlist", "Groupes dans l’ordre défini par la playlist."),
                SettingsPickerOption("name", "Nom (A–Z)")
            ),
            selectedValue = s.groupSort.takeIf { it == "playlist" || it == "name" } ?: "custom",
            onOptionSelected = { v -> update { it.copy(groupSort = v) }; close() },
            onDismiss = close
        )
        LiveDialog.SortChoice -> SettingsSingleChoiceDialog(
            title = "Ordre des chaînes",
            options = listOf(
                SettingsPickerOption(ChannelSort.PLAYLIST, "Ordre de la playlist"),
                SettingsPickerOption(ChannelSort.NUMBER, "Numéro de chaîne"),
                SettingsPickerOption(ChannelSort.NAME, "Nom (A–Z)")
            ),
            selectedValue = s.channelSort,
            onOptionSelected = { v -> update { it.copy(channelSort = v) }; close() },
            onDismiss = close
        )
        LiveDialog.HighlightChoice -> SettingsSingleChoiceDialog(
            title = "Style de sélection",
            options = listOf(
                SettingsPickerOption(false, "Contour", "Fond légèrement assombri et contour coloré : les logos restent visibles."),
                SettingsPickerOption(true, "Plein", "Remplissage avec la couleur d’accentuation du thème.")
            ),
            selectedValue = s.solidHighlight,
            onOptionSelected = { v -> update { it.copy(solidHighlight = v) }; close() },
            onDismiss = close
        )
        LiveDialog.CustomGroups -> UnhideDialog(
            title = "Mes groupes",
            entries = user.customGroups.map { it.id to "${it.name} (${it.channelKeys.size})" },
            onUnhide = { viewModel.deleteCustomGroup(it) },
            onUnhideAll = { user.customGroups.forEach { g -> viewModel.deleteCustomGroup(g.id) }; close() },
            onDismiss = close,
            itemAction = "Supprimer",
            allLabel = "Tout supprimer",
            emptyText = "Vous n’avez encore créé aucun groupe. Maintenez OK sur une chaîne du guide, puis choisissez « Ajouter au groupe ».",
            hintText = "Sélectionnez un groupe pour le supprimer. Les chaînes restent dans votre playlist."
        )
        LiveDialog.ChannelRow -> ChannelRowDialog(
            settings = s,
            groups = remember(playlists, user.customGroups, user.groupNames) { viewModel.rowGroupOptions() },
            onUpdate = { t -> update(t) },
            onDismiss = close
        )
        LiveDialog.PastHours -> SettingsSingleChoiceDialog(
            title = "Historique du guide conservé",
            options = PAST_HOURS.map { SettingsPickerOption(it, hoursLabel(it)) },
            selectedValue = s.epgPastHours,
            onOptionSelected = { v -> viewModel.updateAndReloadEpg { it.copy(epgPastHours = v) }; close() },
            onDismiss = close
        )
    }
}

// ==================================================================== loading line

/** A spinner and what's happening, while playlists, guides or On Demand are downloading. */
@Composable
private fun LoadingLine(message: String) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        com.nuvio.tv.livetv.ui.LoadingSpinner()
        androidx.compose.foundation.layout.Spacer(Modifier.width(10.dp))
        Text(text = message, style = MaterialTheme.typography.bodySmall, color = NuvioTheme.colors.TextSecondary)
    }
}

// ==================================================================== Google Drive sync

/** Collapsed to one line until selected; then shows sign-in, backup and restore. */
@Composable
private fun DriveSyncCard(
    modifier: Modifier = Modifier,
    state: com.nuvio.tv.livetv.sync.DriveSyncState,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onConnect: () -> Unit,
    onAutoSync: (Boolean) -> Unit,
    onBackUp: () -> Unit,
    onRestore: () -> Unit,
    onDisconnect: () -> Unit
) {
    val status = when {
        !state.available -> stringResource(R.string.live_tv_settings_unavailable)
        !state.connected -> stringResource(R.string.live_tv_settings_off)
        state.busy -> stringResource(R.string.live_tv_settings_syncing)
        state.lastSyncMs > 0 -> stringResource(
            R.string.live_tv_settings_last_synced,
            DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(state.lastSyncMs)),
        )
        else -> stringResource(R.string.live_tv_settings_on)
    }
    LiveSection(
        title = stringResource(R.string.live_tv_settings_backup_sync_title),
        summary = stringResource(R.string.live_tv_settings_drive_summary, status),
        icon = Icons.Default.Backup,
        open = expanded,
        onToggle = onToggleExpanded,
        modifier = modifier
    ) {
        run {
            when {
                !state.available -> Text(
                    text = "La synchronisation Google Drive doit être configurée par l’équipe qui compile l’application.",
                    style = MaterialTheme.typography.bodySmall,
                    color = NuvioTheme.colors.TextSecondary
                )
                !state.connected -> {
                    Text(
                        text = "Vos playlists, guides, favoris, chaînes masquées et réglages TV en direct sont sauvegardés dans votre Google Drive, dans un dossier privé accessible uniquement à l’application. Aucune donnée n’est stockée ailleurs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NuvioTheme.colors.TextSecondary
                    )
                    SettingsActionRow("Connecter Google Drive", "Retrouve la même configuration TV en direct sur tous vos téléviseurs grâce à votre Google Drive. Connectez-vous avec un code sur votre téléphone.", onClick = onConnect)
                }
                else -> {
                    SettingsActionRow("Compte", "Compte Google utilisé pour sauvegarder votre configuration TV en direct.", state.email ?: "Connecté", onClick = {})
                    SettingsToggleRow(
                        "Synchroniser automatiquement",
                        "Sauvegarde les modifications et récupère celles faites sur vos autres téléviseurs.",
                        state.autoSync,
                        { onAutoSync(!state.autoSync) }
                    )
                    SettingsActionRow("Sauvegarder maintenant", "Sauvegarde la configuration de ce téléviseur dans Google Drive.", onClick = onBackUp)
                    SettingsActionRow("Restaurer depuis Google Drive", "Remplace la configuration de ce téléviseur par la copie sauvegardée.", onClick = onRestore)
                    SettingsActionRow("Déconnecter", "Arrête la synchronisation de ce téléviseur (la sauvegarde reste dans Drive).", onClick = onDisconnect)
                }
            }
            state.message?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall, color = NuvioTheme.colors.TextSecondary)
            }
        }
    }
}

@Composable
private fun CloudSyncCard(
    modifier: Modifier = Modifier,
    state: com.nuvio.tv.livetv.sync.CloudSyncState,
    enabledParts: Set<com.nuvio.tv.livetv.sync.SyncPart>,
    onTogglePart: (com.nuvio.tv.livetv.sync.SyncPart) -> Unit,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onTurnOn: () -> Unit,
    driveAvailable: Boolean,
    onImportDrive: () -> Unit,
    onSyncNow: () -> Unit,
    onRestorePrevious: () -> Unit,
    onTurnOff: () -> Unit,
    onDelete: () -> Unit
) {
    val status = when {
        !state.signedIn -> stringResource(R.string.live_tv_settings_sign_in_to_sync)
        !state.enabled -> stringResource(R.string.live_tv_settings_off)
        state.busy -> stringResource(R.string.live_tv_settings_syncing)
        state.lastSyncMs > 0 -> stringResource(
            R.string.live_tv_settings_last_synced,
            DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(state.lastSyncMs)),
        )
        else -> stringResource(R.string.live_tv_settings_on)
    }
    LiveSection(
        title = stringResource(R.string.live_tv_settings_setup_sync_title),
        summary = status,
        icon = Icons.Default.Cloud,
        open = expanded,
        onToggle = onToggleExpanded,
        modifier = modifier
    ) {
        run {
            when {
                !state.signedIn -> Text(
                    text = "Connectez-vous à votre compte JojoTV (Paramètres → Compte) pour retrouver votre configuration TV en direct sur chaque téléviseur. Aucune autre configuration n’est nécessaire.",
                    style = MaterialTheme.typography.bodySmall,
                    color = NuvioTheme.colors.TextSecondary
                )
                !state.enabled -> {
                    Text(
                        text = "Vos playlists (avec leurs identifiants), guides, favoris, choix Sports et Calendrier ainsi que les réglages TV en direct sont conservés et synchronisés sur les appareils où vous êtes connecté. Vous choisissez les éléments à synchroniser.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NuvioTheme.colors.TextSecondary
                    )
                    SettingsActionRow("Activer", "Sauvegarde la configuration de ce téléviseur pour la retrouver sur tous vos appareils.", onClick = onTurnOn)
                    if (driveAvailable) SettingsActionRow("Importer depuis Google Drive", "Importe la sauvegarde Google Drive sur ce téléviseur et l’enregistre.", onClick = onImportDrive)
                }
                else -> {
                    Text(
                        text = "Conserve vos playlists et réglages sur chaque appareil où vous êtes connecté.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NuvioTheme.colors.TextSecondary
                    )
                    SettingsActionRow("Compte", "Compte JojoTV associé à cette configuration.", state.email ?: "Connecté", onClick = {})
                    SubHeading("Éléments à synchroniser")
                    com.nuvio.tv.livetv.sync.SyncPart.values().forEach { part ->
                        SettingsToggleRow(part.label, part.description, part in enabledParts, { onTogglePart(part) })
                    }
                    SettingsActionRow("Synchroniser maintenant", "Les modifications sont synchronisées automatiquement quelques secondes après leur enregistrement.", onClick = onSyncNow)
                    SettingsActionRow("Restaurer la version précédente", "Rétablit la copie sauvegardée avant la dernière.", onClick = onRestorePrevious)
                    if (driveAvailable) SettingsActionRow("Importer depuis Google Drive", "Importe la sauvegarde Google Drive sur ce téléviseur et l’enregistre.", onClick = onImportDrive)
                    SettingsActionRow("Désactiver la synchronisation", "Arrête la synchronisation de ce téléviseur (la copie sauvegardée est conservée).", onClick = onTurnOff)
                    SettingsActionRow("Supprimer la configuration sauvegardée", "Supprime la copie sauvegardée et arrête la synchronisation.", onClick = onDelete)
                }
            }
            state.message?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall, color = NuvioTheme.colors.TextSecondary)
            }
        }
    }
}

@Composable
private fun DriveSignInDialog(prompt: com.nuvio.tv.livetv.sync.SignInPrompt, onCancel: () -> Unit) {
    val cancelFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(80)
        runCatching { cancelFocus.requestFocus() }
    }
    // Same layout as Nuvio's other QR sign-ins: instructions, QR code, then the code.
    val qrBitmap = remember(prompt.qrUrl) {
        runCatching { com.nuvio.tv.core.qr.QrCodeGenerator.generate(prompt.qrUrl, 420, margin = 1) }.getOrNull()
    }
    val shortUrl = prompt.url.removePrefix("https://").removePrefix("http://")
    NuvioDialog(onDismiss = onCancel, title = "Connecter Google Drive", width = 560.dp) {
        Text(
            text = "Scannez le code QR avec votre téléphone ou rendez-vous sur $shortUrl, saisissez le code ci-dessous et autorisez l’accès.",
            style = MaterialTheme.typography.bodyMedium,
            color = NuvioTheme.colors.TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        if (qrBitmap != null) {
            androidx.compose.foundation.Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "Code QR",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(196.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
        }
        Text(
            text = prompt.userCode,
            style = MaterialTheme.typography.displaySmall,
            color = NuvioTheme.colors.Secondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = shortUrl,
            style = MaterialTheme.typography.titleMedium,
            color = NuvioTheme.colors.TextPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "En attente de votre autorisation… cet écran se mettra à jour automatiquement.",
            style = MaterialTheme.typography.bodySmall,
            color = NuvioTheme.colors.TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        SettingsDialogActionRow(horizontalAlignment = Alignment.CenterHorizontally) {
            androidx.compose.foundation.layout.Box(Modifier.focusRequester(cancelFocus)) {
                SettingsDialogActionButton(text = "Annuler", onClick = onCancel)
            }
        }
    }
}

// ==================================================================== dialogs

private data class FormField(val label: String, val hint: String, val initial: String = "")

@Composable
private fun SourceFormDialog(
    title: String,
    fields: List<FormField>,
    onDismiss: () -> Unit,
    onSave: (List<String>, Boolean) -> Unit,
    toggle: Pair<String, Boolean>? = null,
    /** A second switch (Xtream: import movies & series); its value goes to [onSaveBoth]. */
    toggle2: Pair<String, Boolean>? = null,
    onSaveBoth: ((List<String>, Boolean, Boolean) -> Unit)? = null,
    /** Xtream: "Inclure les chaînes TV", shown first. Its value goes to [onSaveAll]. */
    toggleLive: Pair<String, Boolean>? = null,
    onSaveAll: ((values: List<String>, guide: Boolean, vod: Boolean, live: Boolean) -> Unit)? = null
) {
    val values = remember { fields.map { mutableStateOf(it.initial) } }
    var toggleValue by remember { mutableStateOf(toggle?.second ?: true) }
    var toggle2Value by remember { mutableStateOf(toggle2?.second ?: true) }
    var toggleLiveValue by remember { mutableStateOf(toggleLive?.second ?: true) }
    val firstField = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(80)
        runCatching { firstField.requestFocus() }
    }
    NuvioDialog(onDismiss = onDismiss, title = title, width = 620.dp) {
        // The fields scroll inside the dialog, so Save and Cancel always stay on screen
        // (with a user agent field and the guide switch, they used to fall off the bottom).
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .verticalScroll(androidx.compose.foundation.rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.sm)
        ) {
        fields.forEachIndexed { i, field ->
            Column(verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.xs)) {
                Text(
                    text = field.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = NuvioTheme.colors.TextSecondary
                )
                NuvioTextField(
                    value = values[i].value,
                    onValueChange = { values[i].value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = field.hint,
                    focusRequester = if (i == 0) firstField else null
                )
            }
        }
        if (toggleLive != null) {
            SettingsToggleRow(
                title = toggleLive.first,
                subtitle = "Ajoute les chaînes en direct du fournisseur au guide TV.",
                checked = toggleLiveValue,
                onToggle = { toggleLiveValue = !toggleLiveValue }
            )
        }
        // The guide switch only matters when TV channels are included.
        if (toggle != null && (toggleLive == null || toggleLiveValue)) {
            SettingsToggleRow(
                title = toggle.first,
                subtitle = null,
                checked = toggleValue,
                onToggle = { toggleValue = !toggleValue }
            )
        }
        if (toggle2 != null) {
            SettingsToggleRow(
                title = toggle2.first,
                subtitle = "Ajoute les films et séries du fournisseur à la section À la demande et à la liste des flux JojoTV.",
                checked = toggle2Value,
                onToggle = { toggle2Value = !toggle2Value }
            )
        }
        }
        SettingsDialogActionRow {
            SettingsDialogActionButton(text = "Annuler", onClick = onDismiss)
            SettingsDialogActionButton(
                text = "Enregistrer",
                onClick = {
                    val v = values.map { it.value.trim() }
                    when {
                        onSaveAll != null -> onSaveAll(v, toggleValue, toggle2Value, toggleLiveValue)
                        onSaveBoth != null -> onSaveBoth(v, toggleValue, toggle2Value)
                        else -> onSave(v, toggleValue)
                    }
                },
                primary = true
            )
        }
    }
}

@Composable
private fun AddonChannelsDialog(
    addons: List<Addon>,
    selected: List<AddonChannelSource>,
    onToggle: (Addon, CatalogDescriptor, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val first = remember { FocusRequester() }
    val options = addons.flatMap { addon ->
        addon.catalogs
            .filter { it.rawType.equals("tv", ignoreCase = true) }
            .map { addon to it }
    }
    LaunchedEffect(Unit) {
        delay(80)
        runCatching { first.requestFocus() }
    }
    NuvioDialog(
        onDismiss = onDismiss,
        title = "Chaînes des extensions",
        subtitle = "Choisissez les catalogues TV en direct à afficher dans le guide. Le flux d’une chaîne est recherché lorsque vous l’ouvrez.",
        width = 620.dp
    ) {
        if (options.isEmpty()) {
            Text(
                "Aucune extension installée ne propose de catalogue TV en direct.",
                color = NuvioTheme.colors.TextSecondary
            )
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.xs)
            ) {
                itemsIndexed(options) { i, (addon, catalog) ->
                    val sourceId = "${addon.baseUrl}|${catalog.id}"
                    val source = selected.firstOrNull { it.id == sourceId }
                    val error = source?.lastError?.takeIf { it.isNotBlank() }
                    SettingsToggleRow(
                        title = catalog.name,
                        subtitle = buildString {
                            append(addon.displayName)
                            val count = source?.channelCount ?: 0
                            if (count > 0) append(" · ").append(count).append(" chaînes")
                            if (error != null) append(" · ").append(error)
                        },
                        checked = source?.enabled == true,
                        onToggle = { onToggle(addon, catalog, source?.enabled != true) },
                        modifier = if (i == 0) Modifier.focusRequester(first) else Modifier
                    )
                }
            }
        }
        SettingsDialogActionRow {
            SettingsDialogActionButton(text = "Terminé", onClick = onDismiss, primary = true)
        }
    }
}

@Composable
private fun ActionListDialog(
    title: String,
    actions: List<Pair<String, () -> Unit>>,
    onDismiss: () -> Unit
) {
    val first = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(80)
        runCatching { first.requestFocus() }
    }
    NuvioDialog(onDismiss = onDismiss, title = title, width = 460.dp) {
        // Scrollable: on a 1080p TV not every option fits, and the last ones were unreachable.
        LazyColumn(
            modifier = Modifier.heightIn(max = 380.dp),
            verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.xxs)
        ) {
            itemsIndexed(actions) { i, (label, action) ->
                SettingsActionRow(
                    title = label,
                    subtitle = null,
                    onClick = action,
                    modifier = if (i == 0) Modifier.focusRequester(first) else Modifier
                )
            }
        }
    }
}

@Composable
private fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmLabel: String = "Supprimer"
) {
    val cancelFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(80)
        runCatching { cancelFocus.requestFocus() }
    }
    NuvioDialog(onDismiss = onDismiss, title = title, subtitle = message, width = 520.dp) {
        SettingsDialogActionRow {
            // Cancel is focused first, so a stray OK press can't delete anything.
            androidx.compose.foundation.layout.Box(Modifier.focusRequester(cancelFocus)) {
                SettingsDialogActionButton(text = "Annuler", onClick = onDismiss)
            }
            SettingsDialogActionButton(text = confirmLabel, onClick = onConfirm, primary = true)
        }
    }
}

/** The restore prompt's text: what's replaced, when the saved copy was made and where, and a warning when it's old. */
private fun restoreMessage(loaded: Boolean, info: com.nuvio.tv.livetv.sync.LiveTvDriveSync.BackupInfo?, localChangedAt: Long): String {
    val base = "La configuration TV en direct de ce téléviseur (playlists, guides, favoris et réglages) sera remplacée par la copie de Google Drive."
    if (!loaded) return "$base\n\nVérification de la date de sauvegarde…"
    if (info == null || info.savedAtMs <= 0L) return base
    val now = System.currentTimeMillis()
    val days = ((now - info.savedAtMs) / (24 * 60 * 60_000L)).toInt()
    val ago = when {
        now - info.savedAtMs < 60 * 60_000L -> "il y a moins d’une heure"
        days < 1 -> "aujourd’hui"
        days == 1 -> "hier"
        days < 14 -> "il y a $days jours"
        days < 60 -> "il y a ${days / 7} semaines"
        else -> "il y a ${days / 30} mois"
    }
    val stamp = java.text.SimpleDateFormat("EEE d MMM, h:mm a", java.util.Locale.getDefault()).format(java.util.Date(info.savedAtMs))
    val where = if (info.fromThisTv) "ce téléviseur" else info.device
    val saved = "Sauvegardé le $stamp sur $where ($ago)."
    val warning = when {
        localChangedAt > info.savedAtMs + 60_000L ->
            "Ce téléviseur a été modifié depuis cette sauvegarde. La restauration rétablira les anciennes playlists et leurs identifiants. Si ce téléviseur contient la version la plus récente, choisissez plutôt « Sauvegarder maintenant »."
        days >= 7 ->
            "Cette copie date de $ago. Si vos playlists ou identifiants ont changé depuis (nouvelle adresse de serveur ou nouveau mot de passe), sauvegardez d’abord la configuration la plus récente depuis un téléviseur à jour."
        else -> null
    }
    return listOfNotNull(base, saved, warning).joinToString("\n\n")
}

@Composable
private fun UnhideDialog(
    title: String,
    entries: List<Pair<String, String>>,
    onUnhide: (String) -> Unit,
    onUnhideAll: () -> Unit,
    onDismiss: () -> Unit,
    itemAction: String = "Afficher",
    allLabel: String = "Tout afficher",
    emptyText: String = "Aucun élément n’est masqué.",
    hintText: String = "Sélectionnez un élément pour le réafficher."
) {
    val first = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(80)
        runCatching { first.requestFocus() }
    }
    NuvioDialog(
        onDismiss = onDismiss,
        title = title,
        subtitle = if (entries.isEmpty()) emptyText else hintText,
        width = 560.dp
    ) {
        if (entries.isEmpty()) {
            SettingsDialogActionRow {
                SettingsDialogActionButton(text = "Fermer", onClick = onDismiss, primary = true)
            }
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.xxs)
            ) {
                item(key = "__all__") {
                    SettingsActionRow(
                        title = allLabel,
                        subtitle = null,
                        value = "${entries.size}",
                        onClick = onUnhideAll,
                        modifier = Modifier.focusRequester(first)
                    )
                }
                items(entries, key = { it.first }) { (key, label) ->
                    SettingsActionRow(title = label, subtitle = null, value = itemAction, onClick = { onUnhide(key) })
                }
            }
        }
    }
}

/**
 * One folding section of Live TV settings, in Nuvio's own settings style: a header row with the
 * section's icon, name and a one-line summary; OK opens or closes it.
 */
@Composable
private fun LiveSection(
    title: String,
    summary: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    open: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    /** Flat tabs: the first section takes the focus coming in (its first row). */
    focusFirst: FocusRequester? = null,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    if (LocalLiveFlat.current) {
        // A sub-tab: sections sit open under a plain heading (nothing to fold, only a few per tab).
        Column(
            if (focusFirst != null) Modifier.focusRequester(focusFirst).focusGroup() else Modifier
        ) {
            SettingsGroupCard {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.padding(start = NuvioTheme.spacing.lg, top = NuvioTheme.spacing.sm, end = NuvioTheme.spacing.lg, bottom = NuvioTheme.spacing.xxs),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    androidx.tv.material3.Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = NuvioTheme.colors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(text = title, style = MaterialTheme.typography.titleMedium, color = NuvioTheme.colors.TextPrimary)
                        Text(text = summary, style = MaterialTheme.typography.bodySmall, color = NuvioTheme.colors.TextSecondary)
                    }
                }
                content()
            }
        }
        return
    }
    SettingsGroupCard {
        SettingsActionRow(
            title = title,
            subtitle = summary,
            leadingIcon = icon,
            trailingIcon = if (open) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            onClick = onToggle,
            modifier = modifier
        )
        if (open) content()
    }
}

/** A small label inside an open section ("En-tête du guide"), like Nuvio's group titles. */
@Composable
private fun SettingsNote(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = NuvioTheme.colors.TextSecondary,
        modifier = Modifier.padding(horizontal = NuvioTheme.spacing.lg, vertical = NuvioTheme.spacing.sm)
    )
}

@Composable
private fun SubHeading(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = NuvioTheme.colors.TextTertiary,
        modifier = Modifier.padding(start = NuvioTheme.spacing.lg, top = NuvioTheme.spacing.sm, bottom = NuvioTheme.spacing.xxs)
    )
}

private fun streamsPositionLabel(v: String): String = when (v) {
    "FIRST" -> "Premier"
    "AFTER_FIRST" -> "Après la première extension"
    else -> "Dernier"
}

private fun searchSectionLabel(kind: String): String = when (kind) {
    "live" -> "Chaînes TV en direct"
    "ondemand" -> "À la demande (votre fournisseur)"
    else -> "Catalogues JojoTV (extensions)"
}

/**
 * The search sections in order. OK picks one up (it's marked), Up and Down move it, OK again
 * puts it down: the same way Reorder groups works in the guide.
 */
@Composable
private fun SearchOrderList(order: List<String>, onChange: (List<String>) -> Unit) {
    var moving by remember { mutableStateOf<String?>(null) }
    val requesters = remember { order.associateWith { FocusRequester() } }
    order.forEachIndexed { i, kind -> androidx.compose.runtime.key(kind) {
        val isMoving = moving == kind
        SettingsActionRow(
            title = "${i + 1}   ${searchSectionLabel(kind)}",
            subtitle = if (isMoving) "Haut/Bas pour déplacer, OK pour déposer ici" else null,
            value = if (isMoving) "Déplacement" else null,
            onClick = { moving = if (isMoving) null else kind },
            modifier = Modifier
                .focusRequester(requesters.getValue(kind))
                .onPreviewKeyEvent { e ->
                    if (!isMoving || e.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    val step = when (e.key) {
                        Key.DirectionUp -> -1
                        Key.DirectionDown -> 1
                        else -> return@onPreviewKeyEvent false
                    }
                    val to = i + step
                    if (to in order.indices) {
                        onChange(order.toMutableList().apply { add(to, removeAt(i)) })
                    }
                    true // stays on the row being moved
                }
        )
    } }
    // The moved row keeps the highlight in its new place.
    LaunchedEffect(order, moving) {
        moving?.let { runCatching { requesters.getValue(it).requestFocus() } }
    }
}

