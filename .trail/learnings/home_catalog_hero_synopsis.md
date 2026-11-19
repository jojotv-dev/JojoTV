# Home hero synopses follow the selected detail page

## Decided

For the Home hero shared by the Vidéos, Films, and Séries rows, show the synopsis belonging to the selected item's detail page. When metadata for the same selected item ID has been enriched, use its description before the row's original snapshot; only fall back to that same card's description.

Keep series and episode overviews separate in Continue Watching. A series hero must not fall back to the current episode's plot.

## Tried and ruled out

- Always preferring the original row snapshot can keep an older synopsis visible after the selected item's detail metadata has updated.
- Using an episode overview as a universal fallback can pair a series title with the current episode's plot.

## Watch out

- Resolve enriched metadata by the active card's own ID so another row's description cannot be borrowed.
- The shared hero resolver covers all catalog rows; fixing only Continue Watching does not satisfy the Videos, Films, and Series contract.

In the code:

- `app/src/main/java/com/nuvio/tv/ui/screens/home/ModernHomeContent.kt` resolves synopsis priority for the selected Home catalog item.
- `app/src/main/java/com/nuvio/tv/ui/screens/home/ModernContinueWatchingHero.kt` renders the separate resume hero synopsis.
