# TMDB is the authority for series lifecycle and season count

## Decided

Series lifecycle and total season count must come from a successfully fetched TMDB TV details response. Normalize lifecycle to only “En cours” or “Terminée”; do not surface a “Renouvelée” label or pass through unknown provider status text. Hide lifecycle status in the Videos row and episode-folder views, while retaining a positive season count when TMDB details verified it. Home series cards and the main series detail page can show both verified fields.

## Tried and ruled out

Using provider status, episode completion state, or the number of locally enumerated season folders as a substitute for TMDB series lifecycle or total season count can make the Home card and episode page disagree. Unknown TMDB lifecycle values should be hidden instead of guessed.

## Watch out

`currentSeasonComplete` describes episode availability/progress, not whether the whole series has ended. Keep it separate from the series lifecycle badge. Cached entries without the TMDB-details verification marker should not display a season count or lifecycle status until refreshed.

In the code:

- `app/src/main/java/com/nuvio/tv/domain/model/SeriesLifecycleStatus.kt` normalizes TMDB lifecycle values.
- `app/src/main/java/com/nuvio/tv/core/tmdb/TmdbMetadataService.kt` reads the TMDB season total.
- `app/src/main/java/com/nuvio/tv/ui/screens/home/HomeRowDetailsPanel.kt` and `app/src/main/java/com/nuvio/tv/ui/screens/detail/HeroSection.kt` render verified Home and detail metadata.
- `app/src/main/java/com/nuvio/tv/ui/components/EpisodeBackdropLayout.kt` and `app/src/main/java/com/nuvio/tv/ui/screens/freebox/FreeboxFolderScreen.kt` keep episode folders count-only.
