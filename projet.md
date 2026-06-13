# JojoTV - resume du projet

> Projet Android TV natif Kotlin + Jetpack Compose. Ancien nom interne : NuvioTV. Dossier de travail : `C:\\Users\\arnau\\Desktop\\JojoTV`.

## Identite du projet

* Nom fonctionnel actuel : **JojoTV**.
* Base technique d'origine : NuvioTV.
* Dossier principal : `C:\\Users\\arnau\\Desktop\\JojoTV`.
* Ne plus travailler dans l'ancien dossier `C:\\Users\\arnau\\Desktop\\NuvioTV`.
* Le code contient encore le namespace technique `com.nuvio.tv` : c'est normal, ne pas renommer en masse.

## Rebranding JojoTV

Assets visibles remplacés/ajoutés :

* `app/src/main/res/drawable/app\_logo\_mark.png`
* `app/src/main/res/drawable/app\_logo\_wordmark.png`
* `app/src/main/res/drawable/jojotv\_text.png`
* `app/src/main/res/drawable/tv\_banner.png`
* `app/src/main/res/drawable-nodpi/tv\_banner.png`
* `app/src/main/res/mipmap-xhdpi/banner.png`

Assets source : `C:\\Users\\arnau\\Desktop\\jojotv 1.0.0`.

Points techniques importants :

* Package Android compile en `com.jojo.tv`.
* Packages Kotlin restent majoritairement en `com.nuvio.tv`. Ne pas renommer : cela toucherait Hilt, imports, baseline profiles, navigation.
* Les noms internes `NuvioColors`, `NuvioTheme`, `NuvioNavHost`, `NuvioSync` peuvent rester tant qu'ils ne sont pas visibles dans l'UI.
* Les fichiers `baseline-prof.txt` et `startup-prof.txt` contiennent des references `com/nuvio/tv` historiques. Ne pas modifier sans regenerer les profiles.
* Le prompt de mise a jour au demarrage a ete supprime/neutralise.

## Stack technique

* Android natif Kotlin, Jetpack Compose / Android TV Compose.
* Hilt + KSP pour l'injection de dependances.
* Room pour la base IPTV StreamVault.
* DataStore pour les reglages locaux par profil.
* Supabase pour compte, profils et synchronisation cloud.
* Media3 / lecteur Nuvio pour la lecture video.
* Coil 3 pour les images (ne pas utiliser coil2).
* Gradle multi-modules.

## Architecture Gradle

|Module|Role|
|-|-|
|`:app`|Application Android TV principale, UI Compose, navigation, player, settings, synchro.|
|`:streamvault-domain`|Domaine IPTV : modeles, interfaces, use cases (remplace `:iptv-domain`).|
|`:streamvault-data`|Donnees IPTV : Room, DAO, repositories, Xtream, M3U, Stalker, sync (remplace `:iptv-data`).|

## Navigation et sidebar

Fichiers importants :

* `app/src/main/java/com/nuvio/tv/MainActivity.kt`
* `app/src/main/java/com/nuvio/tv/ModernSidebarBlurPanel.kt`
* `app/src/main/java/com/nuvio/tv/ui/navigation/Screen.kt`
* `app/src/main/java/com/nuvio/tv/ui/navigation/NuvioNavHost.kt`

Etat :

* Sidebar principale Android TV avec routes : Accueil, Recherche, Explorer, Freebox, IPTV, Parametres, Favoris.
* Sidebar compacte et scrollable. `Bibliotheque` retire, `Addons` dans les Parametres. `Photos` sous `Videos`.
* Item `Favoris` principal dans la sidebar : ouvre une page avec trois lignes Live TV / Films / Series.
* Sidebar directe optionnelle : si les flags `showLiveTvInSidebar` / `showMoviesInSidebar` / `showSeriesInSidebar` sont actives dans `IptvSettingsDataStore`, les entrees correspondantes apparaissent directement dans la sidebar.
* Icones Freebox, Explorer, Photos, Videos, Series et Enregistrements retravaillees pour lisibilite TV.
* Hovers/focus harmonises vers une couleur claire lisible.
* Attention : surveiller le glitch ancien autour du profil/Accueil si des changements sidebar sont faits.

## Freebox

Fichiers donnees :

* `app/src/main/java/com/nuvio/tv/data/freebox/FreeboxOsClient.kt`
* `app/src/main/java/com/nuvio/tv/data/freebox/FreeboxMediaDisplay.kt`
* `app/src/main/java/com/nuvio/tv/data/local/FreeboxSettingsDataStore.kt`

Fichiers UI / ViewModel :

* `app/src/main/java/com/nuvio/tv/ui/screens/settings/FreeboxSettingsScreen.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/settings/FreeboxSettingsViewModel.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/settings/FreeboxBrowserViewModel.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/freebox/FreeboxBrowserScreen.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/freebox/FreeboxPhotoViewerScreen.kt`

Fonctionnalites en place :

* Connexion Freebox OS via app token/session token (champs : nom, adresse, utilisateur, mot de passe).
* Chargement automatique des dossiers Freebox apres connexion.
* Vraie racine `/Freebox` affichee (fichiers racine visibles, dossiers filtres par toggles).
* Double toggle par dossier : afficher dans la page Freebox / afficher dans la sidebar.
* Modes liste/grille, fil d'ariane, tris Nom/Taille/Duree/Date (persistes par profil).
* Lecture video avec headers session (`X-Fbx-App-Auth`, cookie `FreeboxOS`). Refresh token 403 automatique.
* `getOrRefreshSession` reutilise le token actif si possible.
* Affichage VOD : nom de fichier seul (pas de chemin), duree compacte (`1h33 Top chef.mkv`).
* Durees recuperees via API Freebox, `MediaMetadataRetriever`, ou cache progression.
* Miniatures TMDB si disponibles (`FreeboxMediaDisplay.kt` centralise le tout).
* Visualiseur photo avec apercu central et plein ecran.
* Fichiers `.pdf` et `.apk` supportes via Intent/cache local.
* `ContinueWatchingSection.kt` : affichage propre des progressions Freebox, poster prioritaire sur backdrop.
* `popUpTo(Screen.Player.route) { inclusive = true }` dans les blocs `onPlayFile` Freebox pour eviter le cercle infini au second lancement.

Points a retester sur la vraie Freebox :

* Connexion sur le reseau Freebox.
* Affichage videos : pas de chemin complet.
* Durees directes avant lecture.
* Jaquettes TMDB pertinentes.
* Dossiers visibles/masques selon les toggles.

## IPTV

### Architecture

Decision du 2026-06-07 : remplacement complet de l'ancien code IPTV par l'integration de **StreamVault-IPTV** (https://github.com/Davidona/StreamVault-IPTV), disponible localement : `C:\\Users\\arnau\\Desktop\\StreamVault-IPTV-master`.

* Modules `:streamvault-domain` et `:streamvault-data` integres comme modules Gradle.
* Ecrans IPTV : composables Kotlin/Compose dans `:app`, branches sur les repositories StreamVault via Hilt.
* Player JojoTV/Nuvio (Media3) conserve. Flux IPTV utilise `contentType = "iptv"` pour court-circuiter le pipeline addon.
* `PlayerRuntimeControllerStreams.kt` : guard explicite — si `contentType == "iptv"`, retour immediat sans chercher d'addons.

### Fichiers UI

* `app/src/main/java/com/nuvio/tv/ui/screens/iptv/IptvHomeScreen.kt` — liste des providers
* `app/src/main/java/com/nuvio/tv/ui/screens/iptv/IptvHomeViewModel.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/iptv/IptvProviderSetupScreen.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/iptv/IptvProviderSetupViewModel.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/iptv/IptvProviderHomeScreen.kt` — 3 cards Live TV / Films / Series
* `app/src/main/java/com/nuvio/tv/ui/screens/iptv/IptvLiveTvGroupScreen.kt` — groupes/categories Live TV (ViewModel inline)
* `app/src/main/java/com/nuvio/tv/ui/screens/iptv/IptvChannelListScreen.kt` — grille chaines + player (ViewModel inline)
* `app/src/main/java/com/nuvio/tv/ui/screens/iptv/IptvMovieCategoryScreen.kt` — categories films (ViewModel inline)
* `app/src/main/java/com/nuvio/tv/ui/screens/iptv/IptvMovieListScreen.kt` — grille films + player (ViewModel inline)
* `app/src/main/java/com/nuvio/tv/ui/screens/iptv/IptvSeriesCategoryScreen.kt` — categories series (ViewModel inline)
* `app/src/main/java/com/nuvio/tv/ui/screens/iptv/IptvSeriesListScreen.kt` — grille series (ViewModel inline)
* `app/src/main/java/com/nuvio/tv/ui/screens/iptv/IptvSeriesDetailScreen.kt` — saisons/episodes + player (ViewModel inline)

Fichiers settings IPTV :

* `app/src/main/java/com/nuvio/tv/data/local/IptvSettingsDataStore.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/settings/IptvSettingsScreen.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/settings/IptvSettingsViewModel.kt`

### Navigation IPTV

Arborescence complete :

```
Sidebar IPTV
  -> IptvHomeScreen (liste providers)
  -> IptvProviderHomeScreen (Live TV / Films / Series)
     -> IptvLiveTvGroupScreen (categories) -> IptvChannelListScreen (chaines -> player, contentType="iptv")
     -> IptvMovieCategoryScreen (categories) -> IptvMovieListScreen (films -> player, contentType="iptv")
     -> IptvSeriesCategoryScreen (categories) -> IptvSeriesListScreen -> IptvSeriesDetailScreen (episodes -> player, contentType="iptv")
```

Points techniques :

* Resolution d'URL avant navigation : `resolveStream()` dans `IptvChannelListViewModel` appelle `channelRepository.getStreamInfo(channel)` (gere `create\_link` Stalker, URLs Xtream).
* `IptvSeriesListScreen` : le `providerId` est lu depuis le `backStackEntry` pour construire la route `IptvSeriesDetail`.
* Les composables IPTV sont enregistres dans `NuvioNavHost.kt` (correction du 2026-06-07 : ils etaient manquants, causant un crash au clic sur IPTV dans la sidebar).

### Sync Live TV Stalker

Etat actuel (2026-06-07) :

* Auth Stalker OK pour `veveo.vip` (`auth=MAC\_ONLY recipe=MODULE\_GATED preset=MINISTRA\_MODERN profile=MAG322`).
* `get\_all\_channels` retourne du JSON pagine (14 items/page, total 11 663 chaines). `syncStalkerLiveCatalogStaged` : si `acceptedCount < 50`, fallback par categorie declenche.
* `syncLiveNow()` dans `SyncManager` utilise `withStalkerIndexSectionLock` (lock granulaire independant du worker MOVIE).
* `ProviderRepository.refreshLiveData()` delegue a `syncManager.syncLiveNow`.
* Le fallback se declenche bien en logcat mais `get\_ordered\_list` par categorie ne part pas encore.
* Hypothese : annulation coroutine avant que les requetes HTTP ne partent.

Fichiers concernes :

* `iptv-data/src/main/java/com/streamvault/data/sync/SyncManager.kt`
* `iptv-data/src/main/java/com/streamvault/domain/repository/ProviderRepository.kt`
* `iptv-data/src/main/java/com/streamvault/data/repository/ProviderRepositoryImpl.kt`
* `iptv-data/src/main/java/com/streamvault/data/remote/stalker/OkHttpStalkerApiService.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/iptv/IptvLiveTvGroupScreen.kt` (ViewModel inline avec `launch(Dispatchers.IO + NonCancellable)`)

## Player video

Fichiers principaux :

* `app/src/main/java/com/nuvio/tv/ui/screens/player/PlayerNavigationArgs.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/player/PlayerRuntimeController.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/player/PlayerRuntimeControllerStreams.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/player/PlayerRuntimeControllerErrorRecovery.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/player/PlayerMediaSourceFactory.kt`
* `app/src/main/java/com/nuvio/tv/ui/navigation/Screen.kt`

Points importants :

* Lecture centralisee pour films, series, IPTV et Freebox.
* `stopAndRelease()` remet `initialPlaybackStarted = false` : evite le cercle infini au second lancement.
* `LaunchedEffect(Unit)` dans `PlayerScreen.kt` force le demarrage a chaque composition.
* `popUpTo(Screen.Player.route) { inclusive = true }` sur toutes les navigations vers le player.
* Refresh token Freebox 403 automatique via `attemptFreeboxTokenRefresh()`.
* Guard IPTV dans `loadSourceStreams` : si `contentType == "iptv"`, retour immediat sans addons.
* Bouton Enregistrer dans les controles du player.
* `isFreeboxPlayback` detecte les lectures Freebox : desactive OpenSubtitles/addons, conserve timeouts tolerants et header `Accept: \*/\*`.

## Synchronisation cloud / compte

Fichier central : `app/src/main/java/com/nuvio/tv/core/sync/ProfileSettingsSyncService.kt`

Autres fichiers :

* `app/src/main/java/com/nuvio/tv/core/sync/StartupSyncService.kt`
* `app/src/main/java/com/nuvio/tv/core/auth/AuthManager.kt`
* `app/src/main/java/com/nuvio/tv/data/local/ProfileDataStoreFactory.kt`
* `iptv-data/src/main/java/com/streamvault/data/local/dao/Daos.kt`
* `iptv-data/src/main/java/com/streamvault/data/local/entity/Entities.kt`
* `iptv-data/src/main/java/com/streamvault/data/security/CredentialCrypto.kt`

Fonctionnalites :

* Sync cloud de `freebox\_settings`, `iptv\_settings`, `iptv\_providers` (Xtream, M3U, Stalker).
* Fusion a l'import pour eviter les doublons via `serverUrl + username + stalkerMacAddress`.
* Mots de passe providers : dechiffres depuis Keystore avant export, chiffres AES-GCM dans le blob cloud, rechiffres via Keystore a l'import.
* Ne pas retirer `CredentialCrypto`.

## Explorer

Fichiers :

* `app/src/main/java/com/nuvio/tv/ui/screens/explorer/ExplorerScreen.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/explorer/ExplorerViewModel.kt`

Fonctionnalites :

* Double panneau : stockage Android local / Freebox.
* Navigation D-Pad / FocusManager.
* Menu central d'actions : Couper, Copier, Coller, Renommer, Supprimer.
* Clic court = ouvrir, appui long OK = menu contextuel.
* Selection multiple avec cases a cocher.
* `MANAGE\_EXTERNAL\_STORAGE` ajoute au manifest et demande au runtime (Android 13+).
* Support PDF et APK via Intent/cache local.

## Parametres

* `app/src/main/java/com/nuvio/tv/ui/screens/settings/SettingsScreen.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/settings/SettingsDesignSystem.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/settings/FreeboxSettingsScreen.kt`
* `app/src/main/java/com/nuvio/tv/ui/screens/settings/IptvSettingsScreen.kt`

## Ressources et textes

* `app/src/main/res/values/strings.xml`
* `app/src/main/res/values-fr/strings.xml`

Note : libelles accentues (`Séries`) encodes en Unicode dans le code Kotlin pour eviter les problemes d'encodage console/editeur.

## Build

* Debug : `.\\gradlew.bat :app:assembleFullDebug` → `adb install -r app\\build\\outputs\\apk\\full\\debug\\app-full-debug.apk`
* Release : `C:\\Users\\arnau\\Desktop\\JojoTV\_Build\_APK\_armeabi-v7a.bat`
* Verification Kotlin seule : `.\\gradlew.bat --no-daemon --console=plain "-Dkotlin.compiler.execution.strategy=in-process" :app:compileFullDebugKotlin`
* Device ADB : `192.168.1.163:5555`

## Etat fonctionnel actuel

|Zone|Etat|
|-|-|
|Rebranding JojoTV|Assets visibles en place, packages Kotlin historiques conserves|
|Freebox settings|En place|
|Freebox navigation|En place : racine `/Freebox`, toggles, liste/grille, tris persistes, fil d'ariane|
|Freebox lecture video|En place : session/headers, refresh 403, popUpTo correct|
|Freebox VOD affichage|Nom de fichier propre, durees, miniatures TMDB|
|Freebox photos|Visualiseur en place|
|Sidebar|Freebox/IPTV/Explorer/Favoris branches, compacte/scrollable|
|IPTV navigation|En place : crash navhost corrige (2026-06-07), composables enregistres|
|IPTV providers|Ajout/gestion Xtream/M3U/Stalker en place|
|IPTV Live TV|Groupes + chaines, resolution URL Stalker, contentType=iptv|
|IPTV films|Categories + liste, resolution URL, contentType=iptv|
|IPTV series|Categories + liste + detail saisons/episodes, contentType=iptv|
|IPTV sync VOD Stalker|Fonctionnel (get\_ordered\_list par categorie)|
|IPTV sync Live Stalker|Partiel : fallback declenche mais get\_ordered\_list par categorie ne part pas|
|IPTV synchro cloud|Providers synchronises dans iptv\_providers|
|Synchro Freebox/IPTV settings|En place|
|Explorer|Double panneau, actions, selection multiple, MANAGE\_EXTERNAL\_STORAGE|
|Player|Lecture Freebox/IPTV, release correct, bouton Enregistrer|
|Favoris IPTV|Pages Live TV/Films/Series en place|
|Enregistrement IPTV|Base programmation ajoutee, a tester sur flux reels|

## Points de vigilance

* Dossier de travail : **`C:\\Users\\arnau\\Desktop\\JojoTV`**.
* Ne pas renommer en masse `com.nuvio.tv`, `NuvioColors`, `NuvioTheme`, `NuvioSync` ni les baseline profiles.
* Ne pas casser la synchro Supabase/StartupSyncService.
* Ne pas retirer `CredentialCrypto`.
* Ne pas utiliser coil2 : le projet utilise coil3.
* Tester Freebox uniquement sur le reseau local ou avec acces distant configure.

