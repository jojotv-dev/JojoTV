# Series episode badges follow the home status

## Decided
Series episode details should use the home badge format: season count followed by the status, such as `N SAISONS | EN COURS`. Map `Returning Series`/`Renewed` to the in-progress label instead of exposing the raw “Renouvelée” badge.

## Tried and ruled out
Using the raw provider status in the detail hero does not match the home row. Replacing that status with the season count alone also drops the in-progress state needed by the series row.

## Watch out
Propagate both `seasonCount` and `currentSeasonComplete` from TMDB enrichment into detail metadata. Keep the Freebox Videos folder override count-only; it has a separate display requirement. For returning series, the detail badge prioritizes the requested in-progress label even if the provider marks the season complete.

In the code: `MetaDetailsViewModel` maps `TmdbEnrichment` into `Meta`; `HeroSection` builds the combined badge; `IptvSeriesDetailsLayout` preserves the Freebox count-only override through `replaceStatusWithSeasonCount`.
