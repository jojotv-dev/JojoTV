# JojoTV

Application Android TV native pour la consommation de mÃ©dias personnels.

## FonctionnalitÃ©s

- Streaming IPTV (playlists M3U, portails Stalker)
- Navigation EPG (Guide des programmes)
- Parcours Freebox OS (vidÃ©os locales)
- MÃ©tadonnÃ©es TMDB (affiches, synopsis en franÃ§ais)
- Continuer Ã  regarder
- Planification d'enregistrements

## Stack technique

- Kotlin + Jetpack Compose for TV (Material3)
- Media3 / ExoPlayer
- Hilt, Room, Retrofit, Moshi
- Supabase (backend/auth)
- Gradle multi-modules (`:app`, `:iptv-domain`, `:iptv-data`)

## Build

```bash
./gradlew :app:assembleFullDebug
```

## Licence

Usage personnel.
