package com.nuvio.tv.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.livetv.record.LiveTvNotice
import com.nuvio.tv.livetv.sports.ChannelLink
import com.nuvio.tv.livetv.sports.ChannelRegions
import com.nuvio.tv.livetv.sports.League
import com.nuvio.tv.livetv.sports.SportsCatalog
import com.nuvio.tv.livetv.sports.SportsScoreDelay
import com.nuvio.tv.livetv.sports.SportsSettings
import com.nuvio.tv.livetv.sports.SportsViewModel
import com.nuvio.tv.livetv.ui.LiveDialog
import com.nuvio.tv.livetv.ui.LiveFocusRow
import com.nuvio.tv.livetv.ui.LiveText
import com.nuvio.tv.livetv.ui.MenuItem
import com.nuvio.tv.livetv.ui.focusedTextColor
import com.nuvio.tv.ui.theme.NuvioTheme
import kotlinx.coroutines.delay

private val REMIND_TIMES = listOf(0, 5, 10, 15, 30, 60)

private fun remindLabel(m: Int): String = if (m <= 0) "À son début" else "$m minutes avant"

/** Sports, in Nuvio's settings (next to Live TV). */
@Composable
fun SportsSettingsContent(
    onOpenTeams: () -> Unit = {},
    initialFocusRequester: FocusRequester? = null,
    viewModel: SportsViewModel = hiltViewModel()
) {
    val s by viewModel.settings.collectAsStateWithLifecycle()
    val f by viewModel.follows.collectAsStateWithLifecycle()
    var dialog by remember { mutableStateOf<String?>(null) }
    fun update(t: (SportsSettings) -> SportsSettings) = viewModel.repo.updateSettings(t)
    val region = SportsCatalog.regionOf(s)
    val shownCount = SportsCatalog.leagues.count { SportsCatalog.shown(it, s) }
    val links = s.networkChannels.size + s.teamChannels.size + s.leagueChannels.size + s.eventChannels.size
    val fixes = links + s.blocked.size

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md)
    ) {
        SettingsDetailHeader(
            title = "Sports",
            subtitle = "Scores, calendriers et chaînes qui diffusent chaque match"
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(NuvioTheme.spacing.md)
        ) {
            if (!s.showInSidebar) item(key = "off") {
                SettingsGroupCard {
                    SettingsActionRow(
                        title = "Sports est désactivé",
                        subtitle = "La page Sports, les rappels de match et les scores dans la barre d’informations sont désactivés. Activez Sports dans Paramètres → Menu et fonctionnalités.",
                        value = "Activer",
                        onClick = { update { it.copy(showInSidebar = true) } }
                    )
                }
            }
            item(key = "teams") {
                SettingsGroupCard(title = "Vos équipes") {
                    val names = (f.teams.map { it.name } + f.pins.mapNotNull { SportsCatalog.league(it)?.name } + f.words.map { "\"$it\"" })
                    SettingsActionRow(
                        title = "Équipes et ligues suivies",
                        subtitle = if (names.isEmpty()) "Rien pour le moment. Suivez des équipes ou des ligues pour les retrouver dans Mes sports."
                        else names.take(6).joinToString(", ") + if (names.size > 6) " and ${names.size - 6} more" else "",
                        value = if (names.isEmpty()) null else names.size.toString(),
                        leadingIcon = Icons.Default.Star,
                        onClick = onOpenTeams,
                        modifier = if (initialFocusRequester != null) Modifier.focusRequester(initialFocusRequester) else Modifier
                    )
                    SettingsActionRow(
                        title = "Organiser Mes sports",
                        subtitle = "Ordre des lignes de vos équipes et ligues dans Mes sports",
                        leadingIcon = Icons.Default.Tune,
                        onClick = { com.nuvio.tv.livetv.sports.SportsLaunch.openArrange(); onOpenTeams() }
                    )
                    SettingsActionRow(
                        title = "Votre région",
                        subtitle = "Tous les événements affiche les ligues de votre région et celles que vous suivez",
                        value = SportsCatalog.regionName(region) + if (s.region.isBlank()) " (this TV)" else "",
                        leadingIcon = Icons.Default.Public,
                        onClick = { dialog = "region" }
                    )
                }
            }
            item(key = "watching") {
                SettingsGroupCard(title = "Affichage et lecture") {
                    SettingsToggleRow(
                        "Masquer les scores",
                        "Utile pour le replay ou le différé. Les matchs et leurs horaires restent visibles, mais pas les scores",
                        s.hideScores,
                        { update { it.copy(hideScores = !it.hideScores) } }
                    )
                    if (!s.hideScores) {
                        SettingsActionRow(
                            title = "Décalage des scores",
                            subtitle = "La TV par Internet est en retard sur le direct. Les scores, chronomètres, résultats et alertes attendent ce délai pour ne pas apparaître avant l’image",
                            value = SportsScoreDelay.label(s.scoreDelaySeconds),
                            leadingIcon = Icons.Default.Timer,
                            onClick = { dialog = "scoreDelay" }
                        )
                    }
                    SettingsToggleRow(
                        "Score dans la barre d’informations",
                        "Affiche le score en direct dans la barre d’informations de la TV pendant le match",
                        s.scoreOnInfoBar,
                        { update { it.copy(scoreOnInfoBar = !it.scoreOnInfoBar) } }
                    )
                    SettingsToggleRow(
                        "Tableaux d’affichage épinglés",
                        "Épinglez jusqu’à 3 matchs en direct depuis la barre d’informations ou leur page : leurs scores restent en haut à gauche pendant la lecture. Désactivé : le bouton d’épinglage est masqué",
                        s.pinnedScores,
                        { update { it.copy(pinnedScores = !it.pinnedScores) } }
                    )
                    SettingsToggleRow(
                        "Fenêtre vidéo dans Sports",
                        "Les matchs s’affichent dans une fenêtre pendant que vous parcourez la page Sports. Désactivé : le match s’ouvre en plein écran et la page utilise toute la largeur",
                        s.videoWindow,
                        { update { it.copy(videoWindow = !it.videoWindow) } }
                    )
                    SettingsToggleRow(
                        "Matchs diffusés sur mes chaînes uniquement",
                        "Masque les matchs qu’aucune de vos chaînes ne diffuse. Les matchs des équipes suivies restent toujours visibles",
                        s.onlyOnMyChannels,
                        { update { it.copy(onlyOnMyChannels = !it.onlyOnMyChannels) } }
                    )
                    SettingsToggleRow(
                        "Meilleure chaîne en premier",
                        "Place les chaînes 4K et FHD en tête de la liste de chaque match",
                        s.bestChannelFirst,
                        { update { it.copy(bestChannelFirst = !it.bestChannelFirst) } }
                    )
                }
            }
            item(key = "reminders") {
                SettingsGroupCard(title = "Rappels") {
                    if ((f.teams.isNotEmpty() || f.leagues.isNotEmpty()) && !s.scoringAlerts) {
                        SettingsActionRow(
                            title = "Alertes de but désactivées",
                            subtitle = "Vous suivez des équipes ou des ligues, mais aucune carte ne s’affichera lorsqu’elles marquent.",
                            value = "Activer",
                            leadingIcon = Icons.Default.Notifications,
                            onClick = { update { it.copy(scoringAlerts = true) } }
                        )
                    }
                    if (s.scoringAlerts && s.hideScores) {
                        SettingsActionRow(
                            title = "Cartes de but masquées",
                            subtitle = "L’option Masquer les scores est activée ; les alertes de but ne peuvent pas apparaître.",
                            value = "Afficher",
                            leadingIcon = Icons.Default.Notifications,
                            onClick = { update { it.copy(hideScores = false) } }
                        )
                    }
                    SettingsToggleRow(
                        "Me rappeler les matchs de mes équipes",
                        "Un message s’affiche avant le match avec un bouton pour le regarder. Les matchs avec un rappel restent toujours signalés",
                        s.remindMyTeams,
                        { update { it.copy(remindMyTeams = !it.remindMyTeams) } }
                    )
                    SettingsActionRow(
                        title = "Délai du rappel",
                        subtitle = "Moment où le rappel s’affiche",
                        value = remindLabel(s.remindMinutes),
                        leadingIcon = Icons.Default.Notifications,
                        onClick = { dialog = "remind" }
                    )
                    SettingsToggleRow(
                        "Basculer automatiquement vers le match",
                        "À l’heure du rappel, JojoTV ouvre le match une minute avant le début, avec 10 secondes pour annuler. Fonctionne uniquement lorsque JojoTV est ouvert. Le réglage propre à chaque match reste prioritaire",
                        s.autoSwitch,
                        { update { it.copy(autoSwitch = !it.autoSwitch) } }
                    )
                    SettingsToggleRow(
                        "Alertes de score pour mes équipes",
                        "Une petite carte apparaît dans un coin lorsque l’une de vos équipes marque, où que vous soyez dans l’application",
                        s.scoringAlerts,
                        { update { it.copy(scoringAlerts = !it.scoringAlerts) } }
                    )
                }
            }
            item(key = "channels") {
                SettingsGroupCard(title = "Chaînes") {
                    SettingsActionRow(
                        title = "Rechercher les chaînes",
                        subtitle = "Rechercher maintenant les matchs du jour dans vos chaînes et votre guide TV",
                        leadingIcon = Icons.Default.Refresh,
                        onClick = { viewModel.repo.refreshNow(); LiveTvNotice.show("Mise à jour de Sports…") }
                    )
                    SettingsActionRow(
                        title = "Chaînes de la région",
                        subtitle = "Chaque match affiche d’abord les chaînes de cette région ou de cette langue. Les autres restent accessibles en un clic, également depuis la page du match",
                        value = ChannelRegions.shortName(s.channelRegion),
                        leadingIcon = Icons.Default.Public,
                        onClick = { dialog = "channelRegion" }
                    )
                    SettingsActionRow(
                        title = "Groupes de chaînes préférés",
                        subtitle = "Les chaînes de ces groupes apparaissent en tête de chaque liste de match. Sans groupe sélectionné, les chaînes de votre région passent en premier",
                        value = if (s.preferredGroups.isEmpty()) "Aucun" else s.preferredGroups.size.toString(),
                        leadingIcon = Icons.Default.Star,
                        onClick = { dialog = "groups" }
                    )
                    SettingsActionRow(
                        title = "Vos associations de chaînes",
                        subtitle = "Chaînes choisies avec Corriger la chaîne pour un réseau, une équipe, une ligue ou un match. Consultez-les ou supprimez-en une",
                        value = if (links == 0) "Aucune" else links.toString(),
                        leadingIcon = Icons.Default.LiveTv,
                        onClick = { dialog = "links" }
                    )
                    SettingsActionRow(
                        title = "Effacer mes associations",
                        subtitle = "Toutes les chaînes ajoutées ou retirées avec Corriger la chaîne, y compris celles associées à des réseaux entiers",
                        value = if (fixes == 0) "Aucune" else fixes.toString(),
                        leadingIcon = Icons.Default.Refresh,
                        enabled = fixes > 0,
                        onClick = { dialog = "clear" }
                    )
                }
            }
            item(key = "about") {
                Text(
                    text = "Les scores, calendriers, logos et couleurs des équipes proviennent du flux public gratuit d’ESPN. " +
                        "Certaines dates viennent de sporting-events.org ; le calendrier et les logos de catch viennent de TheSportsDB. " +
                        "Les fléchettes, le snooker, le rugby et les autres sports non couverts sont recherchés dans votre guide TV.",
                    style = MaterialTheme.typography.bodySmall,
                    color = NuvioTheme.colors.TextTertiary,
                    modifier = Modifier.padding(start = NuvioTheme.spacing.lg, end = NuvioTheme.spacing.lg)
                )
            }
        }
    }

    val close: () -> Unit = { dialog = null }
    when (dialog) {
        "region" -> SettingsSingleChoiceDialog(
            title = "Votre région",
            options = listOf(SettingsPickerOption("", "Comme ce téléviseur (${SportsCatalog.regionName(SportsCatalog.deviceRegion())})")) +
                SportsCatalog.regions.map { SettingsPickerOption(it.first, it.second) },
            selectedValue = s.region,
            onOptionSelected = { v -> update { it.copy(region = v) }; close() },
            onDismiss = close
        )
        "channelRegion" -> SettingsSingleChoiceDialog(
            title = "Chaînes de la région",
            options = ChannelRegions.all.map { SettingsPickerOption(it.first, it.second) },
            selectedValue = s.channelRegion,
            onOptionSelected = { v -> update { it.copy(channelRegion = v) }; close() },
            onDismiss = close
        )
        "scoreDelay" -> SettingsSingleChoiceDialog(
            title = "Décalage des scores",
            options = SportsScoreDelay.CHOICES.map { SettingsPickerOption(it, SportsScoreDelay.label(it)) },
            selectedValue = s.scoreDelaySeconds,
            onOptionSelected = { v -> update { it.copy(scoreDelaySeconds = v) }; close() },
            onDismiss = close
        )
        "remind" -> SettingsSingleChoiceDialog(
            title = "Me rappeler",
            options = REMIND_TIMES.map { SettingsPickerOption(it, remindLabel(it)) },
            selectedValue = s.remindMinutes,
            onOptionSelected = { v -> update { it.copy(remindMinutes = v) }; close() },
            onDismiss = close
        )
        "groups" -> GroupsDialog(
            viewModel = viewModel,
            chosen = s.preferredGroups,
            onToggle = { g ->
                val key = g.lowercase().trim()
                update { cur -> cur.copy(preferredGroups = if (key in cur.preferredGroups) cur.preferredGroups - key else cur.preferredGroups + key) }
            },
            onDismiss = close
        )
        "leagues" -> LeaguesDialog(s, onToggle = { l -> update { cur -> SportsCatalog.toggleShown(l, cur) } }, onDismiss = close)
        "links" -> LinksDialog(s, viewModel, onForgetAll = { dialog = "clear" }, onDismiss = close)
        "clear" -> {
            val first = remember { FocusRequester() }
            LiveDialog(onDismiss = close, width = 480.dp) {
                LiveText("Effacer vos associations de chaînes ?", size = 20.sp, weight = FontWeight.Bold)
                LiveText(
                    "Sports recherchera de nouveau les chaînes automatiquement. Vos équipes et vos réglages seront conservés.",
                    color = NuvioTheme.colors.TextSecondary, size = 13.sp, maxLines = 3
                )
                Spacer(Modifier.height(12.dp))
                MenuItem("Effacer") { viewModel.repo.clearFixes(); close() }
                MenuItem("Annuler") { close() }
            }
            LaunchedEffect(Unit) { delay(80); runCatching { first.requestFocus() } }
        }
    }
}

@Composable
private fun LeaguesDialog(s: SportsSettings, onToggle: (League) -> Unit, onDismiss: () -> Unit) {
    val first = remember { FocusRequester() }
    val leagues = remember(s.region) { SportsCatalog.ordered(s) }
    LiveDialog(onDismiss = onDismiss, width = 620.dp) {
        LiveText("Ligues affichées dans Sports", size = 20.sp, weight = FontWeight.Bold)
        LiveText(
            "Appuyez sur OK pour afficher ou masquer une ligue. Les ligues des équipes que vous suivez restent toujours visibles.",
            color = NuvioTheme.colors.TextSecondary, size = 13.sp
        )
        Spacer(Modifier.height(10.dp))
        LazyColumn(Modifier.heightIn(max = 420.dp)) {
            items(leagues, key = { it.id }) { l ->
                val on = SportsCatalog.shown(l, s)
                LiveFocusRow(
                    modifier = (if (l == leagues.first()) Modifier.focusRequester(first) else Modifier).fillMaxWidth(),
                    onClick = { onToggle(l) }
                ) { focused ->
                    LiveText(if (on) "✓" else " ", color = focusedTextColor(focused), size = 16.sp, weight = FontWeight.Bold)
                    LiveText("${l.sport.icon}  ${l.name}", color = focusedTextColor(focused), size = 15.sp, modifier = Modifier.weight(1f))
                    LiveText(
                        if (l.guideOnly) "From your guide" else l.sport.label,
                        color = if (focused) focusedTextColor(true) else NuvioTheme.colors.TextSecondary, size = 12.sp
                    )
                }
            }
        }
    }
    LaunchedEffect(Unit) { delay(80); runCatching { first.requestFocus() } }
}

/** Your channel groups (most channels first, sports-looking ones at the top); OK ticks one. */
@Composable
private fun GroupsDialog(viewModel: SportsViewModel, chosen: Set<String>, onToggle: (String) -> Unit, onDismiss: () -> Unit) {
    val first = remember { FocusRequester() }
    val groups = remember {
        viewModel.repo.channelsForPicker()
            .filter { it.group.isNotBlank() }
            .groupingBy { it.group }.eachCount()
            .entries
            .sortedWith(
                compareByDescending<Map.Entry<String, Int>> { it.key.contains("sport", ignoreCase = true) }
                    .thenBy { it.key.lowercase() }
            )
            .map { it.key to it.value }
    }
    LiveDialog(onDismiss = onDismiss, width = 620.dp) {
        LiveText("Groupes de chaînes préférés", size = 20.sp, weight = FontWeight.Bold)
        LiveText(
            "Appuyez sur OK pour sélectionner un groupe. Ses chaînes apparaîtront en tête de chaque liste de match. Les chaînes choisies avec Corriger la chaîne restent prioritaires.",
            color = NuvioTheme.colors.TextSecondary, size = 13.sp, maxLines = 3
        )
        Spacer(Modifier.height(10.dp))
        if (groups.isEmpty()) LiveText("Aucun groupe de chaînes. Ajoutez d’abord une playlist dans TV en direct.", color = NuvioTheme.colors.TextSecondary, size = 13.sp)
        LazyColumn(Modifier.heightIn(max = 420.dp)) {
            items(groups, key = { it.first }) { (g, n) ->
                val on = g.lowercase().trim() in chosen
                LiveFocusRow(
                    modifier = (if (g == groups.first().first) Modifier.focusRequester(first) else Modifier).fillMaxWidth(),
                    onClick = { onToggle(g) }
                ) { focused ->
                    LiveText(if (on) "✓" else " ", color = focusedTextColor(focused), size = 16.sp, weight = FontWeight.Bold)
                    LiveText(g, color = focusedTextColor(focused), size = 15.sp, modifier = Modifier.weight(1f))
                    LiveText("$n", color = if (focused) focusedTextColor(true) else NuvioTheme.colors.TextSecondary, size = 12.sp)
                }
            }
        }
    }
    LaunchedEffect(Unit) { delay(80); runCatching { first.requestFocus() } }
}

/** Your channel links, by kind; OK removes one. */
@Composable
private fun LinksDialog(s: SportsSettings, viewModel: SportsViewModel, onForgetAll: () -> Unit, onDismiss: () -> Unit) {
    val links = remember(s.networkChannels, s.teamChannels, s.leagueChannels, s.eventChannels) { viewModel.repo.channelLinks() }
    val requesters = remember(links) { links.map { FocusRequester() } }
    val forgetAll = remember { FocusRequester() }
    // Where focus goes after a removal (the row that took its place), or -1 on opening.
    var focusAt by remember { mutableStateOf(-1) }
    LiveDialog(onDismiss = onDismiss, width = 620.dp) {
        LiveText("Vos associations de chaînes", size = 20.sp, weight = FontWeight.Bold)
        LiveText(
            "Appuyez sur OK pour supprimer une association. Les matchs concernés rechercheront de nouveau une chaîne automatiquement.",
            color = NuvioTheme.colors.TextSecondary, size = 13.sp, maxLines = 2
        )
        Spacer(Modifier.height(10.dp))
        if (links.isEmpty()) LiveText(
            "Aucune association pour le moment. Sur un match, maintenez OK → Corriger la chaîne, choisissez la bonne chaîne, puis associez-la à un réseau ou à une équipe.",
            color = NuvioTheme.colors.TextSecondary, size = 13.sp, maxLines = 3
        )
        LazyColumn(Modifier.heightIn(max = 420.dp)) {
            links.forEachIndexed { i, l ->
                if (i == 0 || links[i - 1].kind != l.kind) item(key = "h:" + l.kind.name) {
                    LiveText(
                        l.kind.heading.uppercase(), color = NuvioTheme.colors.TextSecondary, size = 11.sp, weight = FontWeight.Bold,
                        modifier = Modifier.padding(top = if (i == 0) 0.dp else 10.dp, bottom = 4.dp)
                    )
                }
                item(key = l.kind.name + ":" + l.key) {
                    LiveFocusRow(
                        modifier = Modifier.focusRequester(requesters[i]).fillMaxWidth(),
                        onClick = {
                            viewModel.repo.removeLink(l)
                            LiveTvNotice.show("Association supprimée : ${l.label}")
                            focusAt = i
                        }
                    ) { focused ->
                        LiveText(l.label, color = focusedTextColor(focused), size = 15.sp, maxLines = 1, modifier = Modifier.weight(1f))
                        LiveText(
                            l.channelName ?: "Chaîne absente de vos playlists",
                            color = if (focused) focusedTextColor(true) else NuvioTheme.colors.TextSecondary, size = 12.sp, maxLines = 1
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        MenuItem("Effacer toutes les associations", Modifier.focusRequester(forgetAll)) { onForgetAll() }
    }
    LaunchedEffect(links) {
        delay(80)
        val target = when {
            links.isEmpty() -> forgetAll
            focusAt < 0 -> requesters.first()
            else -> requesters[focusAt.coerceAtMost(links.size - 1)]
        }
        runCatching { target.requestFocus() }
    }
}
