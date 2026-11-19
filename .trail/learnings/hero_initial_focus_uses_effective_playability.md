# Choose the hero entry focus from the button's effective enabled state

## Decided

The initial focus target must use the same playability condition as the hero button. The Play button is enabled when direct playback is available or when an empty shuffle pool makes it open the episode chooser. Before playback availability finishes loading, do not send initial focus to a fallback action based on a temporary unavailable state; reevaluate the target when loading completes.

## Tried and ruled out

Using only the raw playback-availability result can disagree with the effective button state and can request focus on a different hero action while the Play button is enabled.

## Watch out

Do not steal focus after the initial hero action has already received focus. Keep restore focus and trailer focus paths separate from initial entry focus.

In the code:

- `app/src/main/java/com/nuvio/tv/ui/screens/detail/MetaDetailsScreen.kt` derives `heroPlayIsEntryTarget` from `isPlayEnabled || shufflePoolEmpty` and keys the initial request to playback-loading state.
- `app/src/main/java/com/nuvio/tv/ui/screens/detail/HeroSection.kt` attaches the play requester to the actual TV button.
