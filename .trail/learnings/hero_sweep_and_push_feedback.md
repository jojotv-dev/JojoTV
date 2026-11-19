# Start hero effects when their visual target is ready

## Decided

The card sweep must begin at progress zero only after the current artwork has loaded. Restart it when a focused poster expands to its landscape artwork, so the motion travels across the expanded card too. For the hero play button, observe D-pad down/up events from a parent of the TV button, then translate the face 5 dp over a 6 dp backing edge; the backing edge remains visible by 1 dp while pressed.

## Tried and ruled out

Starting the sweep as soon as focus arrives can make the reflected band appear halfway through when the image itself finishes loading later. A scale-only press feedback did not match the Push Button reference or give a clear tactile edge.

## Watch out

The parent key observer must return `false` so the TV button still receives the same key for its normal click and long-press behavior. Keep focus-ring and button colors intact; only the backing edge and vertical press travel should change.

In the code:

- `app/src/main/java/com/nuvio/tv/ui/components/ShineSweep.kt` resets before each complete sweep.
- `app/src/main/java/com/nuvio/tv/ui/screens/home/ModernHomeRows.kt` waits for `AsyncImage` success and restarts on expanded-card changes.
- `app/src/main/java/com/nuvio/tv/ui/screens/detail/HeroSection.kt` observes D-pad presses around `PlayButton` and uses a 6 dp edge with 5 dp travel.
