# JojoTV — Vue d'ensemble du projet

## Stack technique
- Android TV natif; pas React Native/Expo actif : aucun `app.json`, Metro ou TSX/JS applicatif; `package.json` ne contient que `repomix`.
- Kotlin + Jetpack Compose/Compose for TV, namespace `com.nuvio.tv`, applicationId `com.jojo.tv`, version `0.7.1-beta` / code `1017`.
- Gradle multi-modules : `:app`, `:baselineprofile`, `:streamvault-domain`, `:streamvault-data`; `:ffmpeg-decoder-downmix` est commenté.
- Versions : Gradle `9.4.1`, AGP `9.2.1`, Kotlin `2.3.0`, SDK `24/36`, JVM `11` app, `17` StreamVault.

## Architecture
- `MainActivity.kt` : entrée Compose, sidebar TV, profils, onboarding, sync, thème, Freebox/IPTV.
- `NuvioApplication.kt` : Hilt, hooks plugins, sync Android TV channels, Coil singleton.
- `Screen.kt` + `NuvioNavHost.kt` : routes Home, Search, Discover, Library, Explorer, Freebox, Player, IPTV, Settings, Collections, Account, Plugins.
- `streamvault-domain` : modèles/use cases IPTV; `streamvault-data` : Room, repos, parsers M3U/XMLTV, Xtream/Stalker, sync, enregistrements.
- `app/libs` : AAR ExoPlayer/UI/decoders, QuickJS, MediaInfo; `DV7`/`cpp` pour Dolby Vision/libdovi optionnel.

## Fonctionnalités implémentées
- Launcher Android TV avec `LEANBACK_LAUNCHER`, icône et banner; label `JojoTV`.
- Home moderne/classique/grille, sidebar animée/compacte, profils, thèmes, recherche, découverte, bibliothèque.
- Player Media3/forks locaux, HLS/DASH/RTSP, sous-titres, audio, post-play, enregistrement, lecture externe.
- IPTV : providers Xtream/M3U/Stalker, Live/Films/Séries, EPG, Tivi UI, visibilité providers, enregistrements.
- Freebox : paramètres, navigation fichiers, vidéos/photos, cache durée, reprise de lecture.
- Plugins CloudStream en flavor `full`, serveur NanoHTTPD, QR, Supabase auth/sync.

## Fonctionnalités en cours / TODO
- TODOs dans `core/player/dvmkv/MatroskaExtractor.java` : encodages multiples, déduplication Mp4, mimeType/initData, noms de pistes.
- Stubs CloudStream en `app/src/full/java/com/lagradost/...`; `ExternalExtensionRunner` capture `NotImplementedError`.
- Migration Room `1 -> 2` notée comme no-op stub.

## Dépendances principales
- Hilt `2.59.2`, Room `2.7.1`, Retrofit `2.9.0`, OkHttp `4.12.0`, Moshi `1.15.1`, Coroutines `1.8.1`.
- Compose BOM `2026.01.01`, TV Material `1.1.0-rc01`, Navigation Compose `2.8.8`, Coil `3.3.0`.
- Media3 `1.8.0`, Supabase `3.1.4`, Ktor `3.1.1`, WorkManager `2.9.1`, DataStore `1.1.1`.

## Scripts et outils de build
- Wrapper : `gradlew.bat` / `gradlew`; build debug attendu : `:app:assembleFullDebug`.
- BAT lus non exécutés : `JojoTV_Build_APK_armeabi-v7a bureau.bat` et variante `bureau et Z`; lancent `:app:assembleFullRelease --build-cache --parallel`, exportent l'APK `armeabi-v7a`.
- Flavors `full`/`playstore`; splits ABI release, APK universel; signing via env/local.properties ou debug CI.

## Points d'attention / problèmes connus
- README obsolète sur les modules (`:iptv-domain/:iptv-data` au lieu de `:streamvault-*`).
- `gradle.properties` définit `android.dependency.useConstraints` deux fois avec valeurs opposées.
- SDK 36 et AGP/Kotlin récents : vérifier compatibilité locale avant build.
- `local.properties` requis pour secrets/API; ne pas renommer massivement `com.nuvio.tv` sans gérer Hilt/profiles.
