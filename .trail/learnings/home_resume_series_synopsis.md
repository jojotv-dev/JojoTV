# Keep series and episode synopses separate on resume cards

## Decided

The continue-watching hero needs separate series and episode synopsis values. Keep the active episode overview for episode-level UI, while the hero for an in-progress series uses a series overview from its source preview, enriched series metadata, or TMDB.

## Tried and ruled out

- Reusing `episodeDescription` as the only hero synopsis causes the series title to be paired with the current episode's plot when episode metadata is enabled.
- Replacing `episodeDescription` globally would discard episode-level information used elsewhere.

## Watch out

The in-progress cache must preserve the separate series synopsis too; otherwise the first hero shown after a cache restore can fall back to the wrong episode plot. If no series synopsis is available, leave the series hero synopsis empty instead of showing the episode plot.

Company-logo focus also scrolls the parent detail list. On rapid DPAD_DOWN, suppress that scroll only when the company row has no overflow to reveal; otherwise focus can move below the viewport while the page stays still.

In the code:

- `app/src/main/java/com/nuvio/tv/ui/screens/home/HomeViewModelContinueWatching.kt` resolves episode and series overviews separately.
- `app/src/main/java/com/nuvio/tv/ui/screens/home/ModernContinueWatchingHero.kt` only uses series-level text for a series hero.
- `app/src/main/java/com/nuvio/tv/data/local/ContinueWatchingEnrichmentCache.kt` and `HomeViewModel.kt` persist and restore the series overview.
- `app/src/main/java/com/nuvio/tv/ui/screens/detail/MetaDetailsScreen.kt` scrolls when a focused company row overflows the viewport.
