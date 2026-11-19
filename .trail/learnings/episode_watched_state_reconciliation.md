## Decided

An explicit episode or season completion must remain visible while the tracking provider catches up. A provider lookup returning no matching video ID is not enough to erase a mark made moments earlier in the current details session. Roll back the optimistic state and report an error only when the local repository write fails.

## Tried and ruled out

The episode and season callbacks are wired through the details screen, and the season action is enabled by default. The failure came from watched-state projection and provider reconciliation, rather than a missing click callback.

## Watch out

Keep provider-based cleanup for stale local marks, but exempt episode keys with an active optimistic mark. When implementing batch actions, use the episodes shown for the selected season and do not display success after a failed write.

## In the code

`MetaDetailsViewModel.kt` owns watched episode projection, provider revalidation, and batch season actions. `EpisodeWatchedProjection.kt` resolves provider and local watched signals.
