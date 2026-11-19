package com.nuvio.tv.livetv.sports

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.nuvio.tv.livetv.ui.LiveText as BaseLiveText
import com.nuvio.tv.ui.theme.NuvioTheme
import java.util.Locale

/** French labels used by the imported Sports screens. Team, league and event names stay untouched. */
private val sportsFrenchLabels = mapOf(
    "All" to "Tout",
    "All day" to "Toute la journée",
    "All Events" to "Tous les événements",
    "All events" to "Tous les événements",
    "All Events ·" to "Tous les événements ·",
    "ALL EVENTS" to "TOUS LES ÉVÉNEMENTS",
    "Add a playlist in Live TV settings to watch from here." to "Ajoutez une playlist dans les paramètres de la TV en direct pour regarder les matchs ici.",
    "American Football" to "Football américain",
    "Arrange My Sports" to "Organiser Mes sports",
    "Arrange" to "Organiser",
    "All their games" to "Tous les matchs de cette équipe",
    "AWAY" to "EXTÉRIEUR",
    "Baseball" to "Baseball",
    "BASEBALL" to "BASEBALL",
    "Back" to "Retour",
    "Cancel" to "Annuler",
    "CHANNEL" to "CHAÎNE",
    "CHOOSE LEAGUES" to "CHOISIR LES LIGUES",
    "CHANNELS FROM" to "CHAÎNES DE",
    "Close (it still switches)" to "Fermer (le changement aura quand même lieu)",
    "Channels from" to "Chaînes de",
    "Choose a game to watch here" to "Choisissez un match à regarder ici",
    "Choose Leagues  ›" to "Choisir les ligues  ›",
    "Choose a channel for…" to "Choisir une chaîne pour…",
    "Couldn't get this league's teams. Try again in a little while." to "Impossible de charger les équipes de cette ligue. Réessayez dans quelques instants.",
    "Cricket" to "Cricket",
    "Darts" to "Fléchettes",
    "Dismiss" to "Ignorer",
    "DRAW" to "NUL",
    "Done" to "Terminé",
    "END" to "FIN",
    "Ended" to "Terminé",
    "Event" to "Événement",
    "FT" to "Terminé",
    "Final/OT" to "Terminé après prolongation",
    "Final/OT2" to "Terminé après prolongation",
    "FIGHTING" to "COMBAT",
    "Finding channels for today's games…" to "Recherche des chaînes pour les matchs du jour…",
    "Final" to "Terminé",
    "FORM" to "FORME",
    "Football" to "Football",
    "FOOTBALL" to "FOOTBALL",
    "Follow Teams & Leagues" to "Suivre des équipes et des ligues",
    "Fix the channel" to "Corriger la chaîne",
    "FORM · LAST FIVE" to "FORME · CINQ DERNIERS",
    "From your guide" to "Depuis votre guide TV",
    "From start" to "Depuis le début",
    "BY HALF" to "PAR MI-TEMPS",
    "BY INNING" to "PAR MANCHE",
    "BY PERIOD" to "PAR PÉRIODE",
    "BY QUARTER" to "PAR QUARTEMPS",
    "FULL TABLE" to "CLASSEMENT COMPLET",
    "Golf" to "Golf",
    "GOLF" to "GOLF",
    "Gaelic Games" to "Sports gaéliques",
    "Getting the table…" to "Chargement du classement…",
    "Getting their games…" to "Chargement des matchs…",
    "Getting the teams…" to "Chargement des équipes…",
    "Getting the teams" to "Chargement des équipes",
    "HOME" to "DOMICILE",
    "Game info ›" to "Infos du match ›",
    "Game info" to "Infos du match",
    "Hold OK on a game, or open it and pick a channel" to "Maintenez OK sur un match, ou ouvrez-le pour choisir une chaîne",
    "Hockey" to "Hockey",
    "HOCKEY" to "HOCKEY",
    "Home" to "Domicile",
    "LAST 5" to "5 DERNIERS",
    "LAST MEETINGS" to "DERNIÈRES RENCONTRES",
    "League Networks" to "Chaînes des ligues",
    "Leagues on All Events" to "Ligues dans Tous les événements",
    "Line-ups" to "Compositions",
    "LINE-UPS" to "COMPOSITIONS",
    "Live" to "En direct",
    "Halftime" to "Mi-temps",
    "Half-time" to "Mi-temps",
    "Intermission" to "Entracte",
    "In Progress" to "En cours",
    "Half Time" to "Mi-temps",
    "Warmup" to "Échauffement",
    "Delayed Start" to "Début retardé",
    "OT" to "PROL.",
    "SO" to "TAB",
    "Abandoned" to "Interrompu",
    "LATEST SCORING" to "DERNIERS POINTS MARQUÉS",
    "Looking in your sports add-ons…" to "Recherche dans vos extensions sport…",
    "Looking through your channels…" to "Recherche dans vos chaînes…",
    "MATCH INFO" to "INFOS DU MATCH",
    "Match info" to "Infos du match",
    "More Sports" to "Autres sports",
    "My Sports" to "Mes sports",
    "No games of theirs known right now." to "Aucun match connu pour le moment.",
    "No channel names match." to "Aucun nom de chaîne ne correspond.",
    "No scoring yet" to "Aucun point marqué pour le moment",
    "Scores are hidden" to "Les scores sont masqués",
    "Scores are hidden (Sports settings)." to "Les scores sont masqués (paramètres Sports).",
    "No guide info" to "Aucune information dans le guide TV",
    "No table available" to "Aucun classement disponible",
    "Postponed" to "Reporté",
    "PPD" to "Reporté",
    "Cancelled" to "Annulé",
    "Canceled" to "Annulé",
    "Delayed" to "Retardé",
    "Suspended" to "Suspendu",
    "Scheduled" to "Programmé",
    "Time to be confirmed" to "Horaire à confirmer",
    "Couldn't get this table right now. It'll try again when you come back." to "Impossible de charger ce classement pour le moment. Réessayez en revenant sur cette page.",
    "Not found. OK to choose a channel" to "Introuvable. Appuyez sur OK pour choisir une chaîne",
    "NOW" to "EN COURS",
    "NXT" to "NXT",
    "Other copies of this channel" to "Autres versions de cette chaîne",
    "On TV" to "À la télé",
    "On TV: " to "À la télé : ",
    "Other copies…" to "Autres versions…",
    "Other copies" to "Autres versions",
    "OK on a team: its card" to "OK sur une équipe : afficher sa fiche",
    "OK turns a league on or off. Leagues you follow always show." to "Appuyez sur OK pour afficher ou masquer une ligue. Les ligues que vous suivez restent toujours visibles.",
    "OK uses that copy." to "Appuyez sur OK pour utiliser cette version.",
    "Pick a different channel…" to "Choisir une autre chaîne…",
    "Pick the right channel…" to "Choisir la bonne chaîne…",
    "RACING" to "COURSES",
    "Racing" to "Sports mécaniques",
    "PLACE" to "POS.",
    "POINTS" to "POINTS",
    "GOAL DIFF" to "DIFF. BUTS",
    "BEHIND" to "RETARD",
    "RECORD" to "BILAN",
    "STREAK" to "SÉRIE",
    "Playoffs" to "Phases finales",
    "Play-In" to "Tournoi de play-in",
    "Record" to "Bilan",
    "Record for" to "Bilan de",
    "Referee" to "Arbitre",
    "Reset to automatic" to "Rétablir le choix automatique",
    "RUGBY" to "RUGBY",
    "Rugby" to "Rugby",
    "Score" to "Score",
    "See Everything" to "Tout afficher",
    "Players" to "Joueurs",
    "SNOOKER" to "SNOOKER",
    "Snooker" to "Snooker",
    "Soccer" to "Football",
    "Sports" to "Sports",
    "Searching teams…" to "Recherche d’équipes…",
    "Show this" to "Afficher",
    "START" to "DÉBUT",
    "Starting" to "En cours",
    "Starts" to "Commence",
    "T" to "T",
    "Today" to "Aujourd’hui",
    "Tomorrow" to "Demain",
    "Yesterday" to "Hier",
    "TABLE" to "CLASSEMENT",
    "Tables" to "Classements",
    "TENNIS" to "TENNIS",
    "Tennis" to "Tennis",
    "Tennis Channel" to "Chaîne Tennis",
    "The table couldn't be loaded." to "Impossible de charger le classement.",
    "Take wrong channels off the list, or pick the right one." to "Retirez les chaînes incorrectes de la liste ou choisissez la bonne.",
    "Top performers" to "Meilleurs joueurs",
    "TOP PERFORMERS" to "MEILLEURS JOUEURS",
    "TEAM" to "ÉQUIPE",
    "TEAM STATS" to "STATISTIQUES DES ÉQUIPES",
    "Watch full screen" to "Regarder en plein écran",
    "Watch now" to "Regarder maintenant",
    "Don't switch to it" to "Ne pas basculer sur cette chaîne",
    "Open in Sports" to "Ouvrir dans Sports",
    "Switch now" to "Basculer maintenant",
    "Watching on" to "Diffusé sur",
    "WATCHING ON" to "DIFFUSÉ SUR",
    "WIN" to "VICTOIRE",
    "WIN PROBABILITY" to "PROBABILITÉ DE VICTOIRE",
    "Total Yards" to "Yards totaux",
    "Net Passing Yards" to "Yards à la passe nets",
    "Rushing Yards" to "Yards à la course",
    "Turnovers" to "Pertes de balle",
    "Possession Time" to "Temps de possession",
    "Third Down Efficiency" to "Efficacité sur troisième tentative",
    "Total Shots" to "Tirs totaux",
    "Shots on Target" to "Tirs cadrés",
    "Won Corners" to "Corners obtenus",
    "Fouls Committed" to "Fautes commises",
    "Saves" to "Arrêts",
    "Field Goal Pct" to "Pourcentage de tirs réussis",
    "Three Point Field Goal Pct" to "Pourcentage à trois points",
    "Total Rebounds" to "Rebonds totaux",
    "Assists" to "Passes décisives",
    "Steals" to "Interceptions",
    "Shots Total" to "Tirs totaux",
    "Power Play Goals" to "Buts en supériorité numérique",
    "Hits" to "Mises en échec",
    "Blocked Shots" to "Tirs bloqués",
    "Takeaways" to "Interceptions",
    "Faceoff Percent" to "Pourcentage de mises au jeu gagnées",
    "Runs" to "Points marqués",
    "Errors" to "Erreurs",
    "Home Runs" to "Coups de circuit",
    "Strikeouts" to "Retraits sur des prises",
    "Walks" to "Buts sur balles",
    "Passing Yards" to "Yards à la passe",
    "Receiving Yards" to "Yards à la réception",
    "Clear" to "Dégagé",
    "Mostly Clear" to "Dégagé en grande partie",
    "Cloudy" to "Nuageux",
    "Mostly Cloudy" to "Très nuageux",
    "Partly Cloudy" to "Partiellement nuageux",
    "Light Rain" to "Pluie faible",
    "Rain" to "Pluie",
    "Snow" to "Neige",
    "Windy" to "Venteux",
    "Sunny" to "Ensoleillé",
    "P" to "J",
    "GD" to "Diff.",
    "Pts" to "Pts",
    "W" to "V",
    "L" to "D",
    "PCT" to "%",
    "WRESTLING" to "CATCH",
    "Wrestling" to "Catch",
    "Your pick" to "Votre choix",
    "You have no teams or leagues followed yet." to "Vous ne suivez encore aucune équipe ni aucune ligue."
)

internal fun sportsFrench(text: String): String {
    sportsFrenchLabels[text]?.let { return it }
    sportsFrenchLabels.entries.firstOrNull { it.key.equals(text, ignoreCase = true) }?.value?.let { return it }
    val trimmed = text.trim()
    val lower = trimmed.lowercase(Locale.ROOT)
    Regex("""^Recording \"(.+)\" now$""").matchEntire(trimmed)?.let {
        return "Enregistrement de « ${it.groupValues[1]} » en cours"
    }
    Regex("""^That show has already been on$""").matchEntire(trimmed)?.let {
        return "Cette émission est déjà passée"
    }
    Regex("""^Saving \"(.+)\" from catch-up$""").matchEntire(trimmed)?.let {
        return "Enregistrement de « ${it.groupValues[1]} » depuis le replay"
    }
    Regex("""^Set to record \"(.+)\" \((.+)\)\. It overlaps \"(.+)\"; your account may only allow one at a time\.$""").matchEntire(trimmed)?.let {
        return "Enregistrement programmé : « ${it.groupValues[1]} » (${it.groupValues[2]}). Il chevauche « ${it.groupValues[3]} » ; votre compte peut limiter les enregistrements à un seul à la fois."
    }
    Regex("""^Set to record \"(.+)\" \((.+)\)$""").matchEntire(trimmed)?.let {
        return "Enregistrement programmé : « ${it.groupValues[1]} » (${it.groupValues[2]})"
    }
    Regex("""^(\d+)(st|nd|rd|th) in (.+)$""", RegexOption.IGNORE_CASE).matchEntire(trimmed)?.let {
        val place = it.groupValues[1].toInt()
        val ordinal = if (place == 1) "1re" else "${place}e"
        return "$ordinal dans ${it.groupValues[3]}"
    }
    Regex("""^(.+) after (\d+)(st|nd|rd|th)$""", RegexOption.IGNORE_CASE).matchEntire(trimmed)?.let {
        return "${sportsFrench(it.groupValues[1])} après la ${sportsFrench(it.groupValues[2] + it.groupValues[3])} place"
    }
    Regex("""^(\d+)(st|nd|rd|th) (Half|Period|Quarter|Inning|Set|Overtime)$""", RegexOption.IGNORE_CASE).matchEntire(trimmed)?.let {
        val number = it.groupValues[1].toInt()
        val unit = when (it.groupValues[3].lowercase(Locale.ROOT)) {
            "half" -> "mi-temps"
            "period" -> "période"
            "quarter" -> "quart-temps"
            "inning" -> "manche"
            "set" -> "set"
            else -> "prolongation"
        }
        val ordinal = if (number == 1 && unit in setOf("mi-temps", "période", "manche")) "1re" else if (number == 1) "1er" else "${number}e"
        return "$ordinal $unit"
    }
    Regex("""^(Top|Bottom|Mid) (\d+)(?:st|nd|rd|th)?$""", RegexOption.IGNORE_CASE).matchEntire(trimmed)?.let {
        val side = when (it.groupValues[1].lowercase(Locale.ROOT)) {
            "top" -> "Haut"
            "bottom" -> "Bas"
            else -> "Milieu"
        }
        val inning = it.groupValues[2].toInt()
        return "$side de la ${if (inning == 1) "1re" else "${inning}e"} manche"
    }
    var translatedStats = trimmed
    translatedStats = Regex("""\b(\d+) won\b""", RegexOption.IGNORE_CASE).replace(translatedStats) {
        val count = it.groupValues[1].toInt()
        "$count ${if (count == 1) "victoire" else "victoires"}"
    }
    translatedStats = Regex("""\b(\d+) drawn\b""", RegexOption.IGNORE_CASE).replace(translatedStats) {
        val count = it.groupValues[1].toInt()
        "$count ${if (count == 1) "match nul" else "matchs nuls"}"
    }
    translatedStats = Regex("""\b(\d+) lost\b""", RegexOption.IGNORE_CASE).replace(translatedStats) {
        val count = it.groupValues[1].toInt()
        "$count ${if (count == 1) "défaite" else "défaites"}"
    }
    translatedStats = Regex("""goal difference ([+-]?\d+)""", RegexOption.IGNORE_CASE).replace(translatedStats) { "différence de buts ${it.groupValues[1]}" }
    translatedStats = Regex("""\b(\d+) wins in a row\b""", RegexOption.IGNORE_CASE).replace(translatedStats) { "${it.groupValues[1]} victoires consécutives" }
    translatedStats = Regex("""\b(\d+) losses in a row\b""", RegexOption.IGNORE_CASE).replace(translatedStats) { "${it.groupValues[1]} défaites consécutives" }
    translatedStats = Regex("""\b(\d+) games behind\b""", RegexOption.IGNORE_CASE).replace(translatedStats) {
        val count = it.groupValues[1].toInt()
        "$count ${if (count == 1) "match" else "matchs"} de retard"
    }
    translatedStats = Regex("""\b([+-]?\d+(?:\.\d+)?) behind the leader\b""", RegexOption.IGNORE_CASE).replace(translatedStats) { "${it.groupValues[1]} de retard sur le leader" }
    if (translatedStats != trimmed) return translatedStats
    return when {
        lower.matches(Regex("in \\d+ min")) -> "dans ${trimmed.substringAfter(' ')}"
        lower.matches(Regex("in \\d+ h( \\d+)?")) -> "dans ${trimmed.substringAfter(' ')}"
        trimmed.startsWith("‹  ") -> "‹  ${sportsFrench(trimmed.removePrefix("‹  "))}"
        trimmed.startsWith("▶  ") -> "▶  ${sportsFrench(trimmed.removePrefix("▶  "))}"
        trimmed.startsWith("⏮  ") -> "⏮  ${sportsFrench(trimmed.removePrefix("⏮  "))}"
        trimmed.startsWith("↕  ") -> "↕  Organiser"
        trimmed.startsWith("✓  ") -> "✓  Terminé"
        trimmed.startsWith("✎  ") && trimmed.endsWith("Follow Teams & Leagues") -> "✎  Suivre des équipes et des ligues"
        lower.startsWith("live ") -> "En direct ${sportsFrench(trimmed.substringAfter(' '))}"
        lower.startsWith("today ") -> "Aujourd’hui ${trimmed.substringAfter(' ')}"
        lower.startsWith("tomorrow ") -> "Demain ${trimmed.substringAfter(' ')}"
        trimmed.startsWith("Game info ") -> "Infos du match ${trimmed.removePrefix("Game info ")}"
        trimmed.startsWith("OK · FULL TABLE") -> "OK · CLASSEMENT COMPLET ${trimmed.substringAfter("TABLE", "›")}"
        trimmed.startsWith("Started ") -> "Commencé à ${trimmed.removePrefix("Started ")}"
        trimmed.startsWith("Last met ") -> "Dernière rencontre : ${trimmed.removePrefix("Last met ")}"
        trimmed.startsWith("Next: ") -> "Prochain : ${trimmed.removePrefix("Next: ")}"
        trimmed.startsWith("Round ") -> "Manche ${trimmed.removePrefix("Round ")}"
        trimmed.startsWith("Race · Live") -> "Course · En direct${trimmed.removePrefix("Race · Live")}"
        trimmed.startsWith("Race") -> "Course${trimmed.removePrefix("Race")}"
        trimmed.startsWith("Overtime") -> "Prolongation${trimmed.removePrefix("Overtime")}"
        lower.startsWith("starting now") -> "Commence maintenant${trimmed.substring("Starting now".length)}"
        lower.startsWith("starting in a minute") -> "Commence dans une minute${trimmed.substring("Starting in a minute".length)}"
        lower.startsWith("starting in ") -> "Commence dans ${trimmed.substring("Starting in ".length).substringBefore(' ')} minutes"
        trimmed.startsWith("SWITCHING IN ") -> "CHANGEMENT DANS ${trimmed.removePrefix("SWITCHING IN ")}"
        trimmed.startsWith("Switches to ") && trimmed.contains(" by itself at ") -> {
            val channelAndTime = trimmed.removePrefix("Switches to ").removeSuffix(".").split(" by itself at ", limit = 2)
            if (channelAndTime.size == 2) "Basculera automatiquement sur ${channelAndTime[0]} à ${channelAndTime[1]}." else text
        }
        trimmed.startsWith("On TV: ") -> "À la télé : ${trimmed.removePrefix("On TV: ")}"
        trimmed.startsWith("Referee: ") -> "Arbitre : ${trimmed.removePrefix("Referee: ")}"
        trimmed.startsWith("Attendance: ") -> "Affluence : ${trimmed.removePrefix("Attendance: ")}"
        lower.startsWith("attendance ") -> "Affluence : ${trimmed.substringAfter(' ')}"
        Regex("^\\d+(st|nd|rd|th)(.*)$", RegexOption.IGNORE_CASE).matches(trimmed) -> {
            val match = Regex("^(\\d+)(st|nd|rd|th)(.*)$", RegexOption.IGNORE_CASE).matchEntire(trimmed)!!
            val number = match.groupValues[1].toInt()
            "${number}${if (number == 1) "er" else "e"}${match.groupValues[3]}"
        }
        Regex("^All (\\d+) games$", RegexOption.IGNORE_CASE).matches(trimmed) ->
            "Tous les ${Regex("^All (\\d+) games$", RegexOption.IGNORE_CASE).matchEntire(trimmed)!!.groupValues[1]} matchs"
        trimmed.startsWith("All ") && trimmed.endsWith(" games") -> "Tous les matchs de ${trimmed.removePrefix("All ").removeSuffix(" games")}"
        lower.matches(Regex("\\d+ games?")) -> {
            val count = trimmed.substringBefore(' ').toInt()
            "$count match${if (count == 1) "" else "s"}"
        }
        trimmed.startsWith("Other copies of ") -> "Autres versions de ${trimmed.removePrefix("Other copies of ")}"
        trimmed.startsWith("Pick the channel for ") -> "Choisir la chaîne pour ${trimmed.removePrefix("Pick the channel for ")}"
        trimmed.startsWith("Searching teams…") -> "Recherche d’équipes…"
        trimmed.startsWith("Searching teams...") -> "Recherche d’équipes…"
        trimmed.startsWith("No channel found for ") -> "Aucune chaîne trouvée pour ${trimmed.removePrefix("No channel found for ")}"
        trimmed.startsWith("Watch on ") -> trimmed.replace("Watch on ", "Regarder sur ").replace(" now", " maintenant")
        trimmed.startsWith("Use ") && trimmed.endsWith(" for more games?") ->
            "Utiliser ${trimmed.removePrefix("Use ").removeSuffix(" for more games?")} pour voir plus de matchs ?"
        trimmed.startsWith("All Events · ") && trimmed.endsWith(" live") ->
            "Tous les événements · ${trimmed.removePrefix("All Events · ").removeSuffix(" live")} en direct"
        trimmed.startsWith("+") && trimmed.endsWith(" more") -> "+${trimmed.removePrefix("+").removeSuffix(" more")} de plus"
        trimmed.endsWith(" · the next 7 days") -> "${trimmed.removeSuffix(" · the next 7 days")} · les 7 prochains jours"
        trimmed.startsWith("FOLLOWING · ") -> "SUIVIS · ${trimmed.removePrefix("FOLLOWING · ")}"
        else -> text
    }
}

@Composable
internal fun LiveText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = NuvioTheme.colors.TextPrimary,
    size: TextUnit = 16.sp,
    weight: FontWeight = FontWeight.Normal,
    maxLines: Int = 1,
    align: TextAlign? = null,
    marquee: Boolean = false,
    shadow: Boolean = false
) {
    BaseLiveText(
        text = sportsFrench(text),
        modifier = modifier,
        color = color,
        size = size,
        weight = weight,
        maxLines = maxLines,
        align = align,
        marquee = marquee,
        shadow = shadow
    )
}
