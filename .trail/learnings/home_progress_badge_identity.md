# Playback badges follow shared content identities across Home rows

## Decided

Home catalog cards should show the in-progress badge wherever the same content appears in the Videos, Films, or Series rows. Start with playback IDs, expand through IMDb/TMDB aliases from cards already linked to that progress item, and use an exact title plus release year and media type only as a fallback.

## Tried and ruled out

- Matching by title alone can mark a different film or series with the same name.
- Matching only the progress ID can recognize the source row while missing the same content in another row that uses a different provider ID.

## Watch out

- Recompute row badge state when the in-progress list changes and when the catalog identity aliases change.
- Keep completed progress out of the match set.
- Add title/year fallback only when both the media type and a release year are known.

In the code:

- `app/src/main/java/com/nuvio/tv/ui/screens/home/HomeUiState.kt` builds and expands progress identity keys.
- `app/src/main/java/com/nuvio/tv/ui/screens/home/ModernHomePresentation.kt`, `ClassicHomeContent.kt`, and `GridHomeContent.kt` pass catalog identities into badge matching.
- `app/src/main/java/com/nuvio/tv/ui/screens/home/HomeViewModelPresentationPipeline.kt` rebuilds the modern row presentation when relevant identity aliases change.
