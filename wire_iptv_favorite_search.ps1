# ============================================================================
# Cablage favori IPTV (Movie/Series) dans le menu long-press de la recherche
# Fichiers touches :
#   - PosterOptionsState.kt      : ajout champ isIptvItem
#   - PosterOptionsController.kt : import FavoriteRepository, bifurcation show(),
#                                  nouvelle methode toggleIptvFavorite()
#   - PosterOptionsDialog.kt     : nouveau parametre isIptvItem, libelle conditionnel,
#                                  branchement dans PosterOptionsHost
# Methode : backup .bak avant chaque ecriture, abort si l'ancre ne matche pas
# exactement une fois.
# ============================================================================

$root = "C:\Users\arnau\Desktop\JojoTV\app\src\main\java\com\nuvio\tv"

function Edit-FileOnce {
    param(
        [string]$Path,
        [string]$Old,
        [string]$New,
        [string]$Label
    )

    if (-not (Test-Path $Path)) {
        Write-Host "[ABORT] Fichier introuvable : $Path" -ForegroundColor Red
        exit 1
    }

    $content = Get-Content -Path $Path -Raw

    $count = ([regex]::Matches($content, [regex]::Escape($Old))).Count
    if ($count -eq 0) {
        Write-Host "[ABORT] '$Label' : ancre NON trouvee dans $Path" -ForegroundColor Red
        exit 1
    }
    if ($count -gt 1) {
        Write-Host "[ABORT] '$Label' : ancre trouvee $count fois (doit etre unique) dans $Path" -ForegroundColor Red
        exit 1
    }

    $backupPath = "$Path.bak"
    if (-not (Test-Path $backupPath)) {
        Copy-Item -Path $Path -Destination $backupPath
    }

    $updated = $content.Replace($Old, $New)
    Set-Content -Path $Path -Value $updated -NoNewline
    Write-Host "[OK] '$Label' applique sur $Path" -ForegroundColor Green
}

# ----------------------------------------------------------------------------
# 1. PosterOptionsState.kt : ajout du champ isIptvItem
# ----------------------------------------------------------------------------
$stateFile = Join-Path $root "ui\components\posteroptions\PosterOptionsState.kt"

$oldStateLines = @(
    "    val listPickerError: String? = null",
    ")"
)
$newStateLines = @(
    "    val listPickerError: String? = null,",
    "    val isIptvItem: Boolean = false",
    ")"
)
Edit-FileOnce -Path $stateFile `
    -Old ($oldStateLines -join "`r`n") `
    -New ($newStateLines -join "`r`n") `
    -Label "PosterOptionsState: ajout isIptvItem"

# ----------------------------------------------------------------------------
# 2. PosterOptionsController.kt
# ----------------------------------------------------------------------------
$controllerFile = Join-Path $root "ui\components\posteroptions\PosterOptionsController.kt"

# 2a. Imports
$oldImportsLines = @(
    "import com.nuvio.tv.domain.repository.LibraryRepository",
    "import com.nuvio.tv.domain.repository.MetaRepository",
    "import com.nuvio.tv.domain.repository.WatchProgressRepository",
    "import javax.inject.Inject"
)
$newImportsLines = @(
    "import com.nuvio.tv.domain.repository.LibraryRepository",
    "import com.nuvio.tv.domain.repository.MetaRepository",
    "import com.nuvio.tv.domain.repository.WatchProgressRepository",
    "import com.nuvio.tv.ui.screens.search.IPTV_MOVIE_SEARCH_TYPE",
    "import com.nuvio.tv.ui.screens.search.IPTV_SERIES_SEARCH_TYPE",
    "import com.nuvio.tv.ui.screens.search.parseIptvSearchItemId",
    "import com.streamvault.domain.model.ContentType as IptvContentType",
    "import com.streamvault.domain.repository.FavoriteRepository",
    "import javax.inject.Inject"
)
Edit-FileOnce -Path $controllerFile `
    -Old ($oldImportsLines -join "`r`n") `
    -New ($newImportsLines -join "`r`n") `
    -Label "PosterOptionsController: imports favoris IPTV"

# 2b. Constructeur : injection FavoriteRepository
$oldCtorLines = @(
    "    private val watchedSeriesStateHolder: WatchedSeriesStateHolder,",
    "    private val tmdbService: TmdbService",
    ") {"
)
$newCtorLines = @(
    "    private val watchedSeriesStateHolder: WatchedSeriesStateHolder,",
    "    private val tmdbService: TmdbService,",
    "    private val favoriteRepository: FavoriteRepository",
    ") {"
)
Edit-FileOnce -Path $controllerFile `
    -Old ($oldCtorLines -join "`r`n") `
    -New ($newCtorLines -join "`r`n") `
    -Label "PosterOptionsController: injection FavoriteRepository"

# 2c. show() : bifurcation IPTV en tout debut de fonction
$oldShowLines = @(
    "    fun show(item: MetaPreview, addonBaseUrl: String?) {",
    "        val launchScope = this.scope ?: return",
    "        showJob?.cancel()",
    "        showJob = launchScope.launch {"
)
$newShowLines = @(
    "    fun show(item: MetaPreview, addonBaseUrl: String?) {",
    "        val launchScope = this.scope ?: return",
    "        showJob?.cancel()",
    "",
    "        if (item.rawType == IPTV_MOVIE_SEARCH_TYPE || item.rawType == IPTV_SERIES_SEARCH_TYPE) {",
    "            showJob = launchScope.launch {",
    "                val parsed = parseIptvSearchItemId(item.id)",
    "                val initialFavorite = if (parsed != null) {",
    "                    val (_, providerId, contentId) = parsed",
    "                    val contentType = if (item.rawType == IPTV_MOVIE_SEARCH_TYPE) {",
    "                        IptvContentType.MOVIE",
    "                    } else {",
    "                        IptvContentType.SERIES",
    "                    }",
    "                    runCatching {",
    "                        favoriteRepository.isFavorite(providerId, contentId, contentType)",
    "                    }.getOrDefault(false)",
    "                } else {",
    "                    false",
    "                }",
    "",
    "                _state.update { current ->",
    "                    current.copy(",
    "                        target = item,",
    "                        addonBaseUrl = addonBaseUrl.orEmpty(),",
    "                        isInLibrary = initialFavorite,",
    "                        isWatched = false,",
    "                        isLibraryPending = false,",
    "                        isWatchedPending = false,",
    "                        isIptvItem = true",
    "                    )",
    "                }",
    "                targetFlow.value = null",
    "            }",
    "            return",
    "        }",
    "",
    "        showJob = launchScope.launch {"
)
Edit-FileOnce -Path $controllerFile `
    -Old ($oldShowLines -join "`r`n") `
    -New ($newShowLines -join "`r`n") `
    -Label "PosterOptionsController: bifurcation IPTV dans show()"

# 2d. Le bloc original de show() (cas Stremio) doit repartir avec isIptvItem = false
$oldShowBodyLines = @(
    "            _state.update { current ->",
    "                current.copy(",
    "                    target = canonical,",
    "                    addonBaseUrl = addonBaseUrl.orEmpty(),",
    "                    isInLibrary = initialIsInLibrary,",
    "                    isWatched = initialIsWatched,",
    "                    isLibraryPending = false,",
    "                    isWatchedPending = false",
    "                )",
    "            }",
    "            targetFlow.value = canonical",
    "        }",
    "    }"
)
$newShowBodyLines = @(
    "            _state.update { current ->",
    "                current.copy(",
    "                    target = canonical,",
    "                    addonBaseUrl = addonBaseUrl.orEmpty(),",
    "                    isInLibrary = initialIsInLibrary,",
    "                    isWatched = initialIsWatched,",
    "                    isLibraryPending = false,",
    "                    isWatchedPending = false,",
    "                    isIptvItem = false",
    "                )",
    "            }",
    "            targetFlow.value = canonical",
    "        }",
    "    }"
)
Edit-FileOnce -Path $controllerFile `
    -Old ($oldShowBodyLines -join "`r`n") `
    -New ($newShowBodyLines -join "`r`n") `
    -Label "PosterOptionsController: isIptvItem=false sur chemin Stremio"

# 2e. dismiss() : remettre isIptvItem a false a la fermeture
$oldDismissLines = @(
    "    fun dismiss() {",
    "        showJob?.cancel()",
    "        showJob = null",
    "        targetFlow.value = null",
    "        _state.update { it.copy(target = null) }",
    "    }"
)
$newDismissLines = @(
    "    fun dismiss() {",
    "        showJob?.cancel()",
    "        showJob = null",
    "        targetFlow.value = null",
    "        _state.update { it.copy(target = null, isIptvItem = false) }",
    "    }"
)
Edit-FileOnce -Path $controllerFile `
    -Old ($oldDismissLines -join "`r`n") `
    -New ($newDismissLines -join "`r`n") `
    -Label "PosterOptionsController: reset isIptvItem dans dismiss()"

# 2f. Nouvelle methode toggleIptvFavorite(), ajoutee juste avant toggleLibrary()
$oldToggleLibLines = @(
    "    fun toggleLibrary() {",
    "        val state = _state.value",
    "        if (state.target == null) return"
)
$newToggleLibLines = @(
    "    fun toggleIptvFavorite() {",
    "        val state = _state.value",
    "        val item = state.target ?: return",
    "        if (!state.isIptvItem) return",
    "        if (state.isLibraryPending) return",
    "        val scope = this.scope ?: return",
    "",
    "        val parsed = parseIptvSearchItemId(item.id) ?: return",
    "        val (_, providerId, contentId) = parsed",
    "        val contentType = if (item.rawType == IPTV_MOVIE_SEARCH_TYPE) {",
    "            IptvContentType.MOVIE",
    "        } else {",
    "            IptvContentType.SERIES",
    "        }",
    "",
    "        _state.update { it.copy(isLibraryPending = true) }",
    "        scope.launch {",
    "            val currentlyFavorite = _state.value.isInLibrary",
    "            runCatching {",
    "                if (currentlyFavorite) {",
    "                    favoriteRepository.removeFavorite(providerId, contentId, contentType)",
    "                } else {",
    "                    favoriteRepository.addFavorite(providerId, contentId, contentType)",
    "                }",
    '            }.onFailure { error ->',
    '                Log.w(TAG, "Failed to toggle IPTV favorite for $contentId: ${error.message}")',
    '            }',
    "            _state.update { it.copy(isLibraryPending = false, isInLibrary = !currentlyFavorite) }",
    "        }",
    "    }",
    "",
    "    fun toggleLibrary() {",
    "        val state = _state.value",
    "        if (state.target == null) return"
)
Edit-FileOnce -Path $controllerFile `
    -Old ($oldToggleLibLines -join "`r`n") `
    -New ($newToggleLibLines -join "`r`n") `
    -Label "PosterOptionsController: ajout toggleIptvFavorite()"

# ----------------------------------------------------------------------------
# 3. PosterOptionsDialog.kt
# ----------------------------------------------------------------------------
$dialogFile = Join-Path $root "ui\components\posteroptions\PosterOptionsDialog.kt"

# 3a. Nouveau parametre isIptvItem sur PosterOptionsDialog
$oldDialogSigLines = @(
    "    showManageLists: Boolean,",
    "    isMovie: Boolean,",
    "    isSeries: Boolean = false,"
)
$newDialogSigLines = @(
    "    showManageLists: Boolean,",
    "    isMovie: Boolean,",
    "    isSeries: Boolean = false,",
    "    isIptvItem: Boolean = false,"
)
Edit-FileOnce -Path $dialogFile `
    -Old ($oldDialogSigLines -join "`r`n") `
    -New ($newDialogSigLines -join "`r`n") `
    -Label "PosterOptionsDialog: parametre isIptvItem"

# 3b. Libelle conditionnel du bouton favori/library + masquage watched si IPTV
$oldButtonTextLines = @(
    "            Text(",
    "                if (showManageLists) {",
    "                    stringResource(R.string.library_manage_lists)",
    "                } else {",
    "                    if (isInLibrary) {",
    "                        stringResource(R.string.hero_remove_from_library)",
    "                    } else {",
    "                        stringResource(R.string.hero_add_to_library)",
    "                    }",
    "                }",
    "            )",
    "        }",
    "",
    "        if (isMovie || isSeries) {"
)
$newButtonTextLines = @(
    "            Text(",
    "                if (isIptvItem) {",
    "                    if (isInLibrary) {",
    "                        stringResource(R.string.hero_remove_from_favorites)",
    "                    } else {",
    "                        stringResource(R.string.hero_add_to_favorites)",
    "                    }",
    "                } else if (showManageLists) {",
    "                    stringResource(R.string.library_manage_lists)",
    "                } else {",
    "                    if (isInLibrary) {",
    "                        stringResource(R.string.hero_remove_from_library)",
    "                    } else {",
    "                        stringResource(R.string.hero_add_to_library)",
    "                    }",
    "                }",
    "            )",
    "        }",
    "",
    "        if (!isIptvItem && (isMovie || isSeries)) {"
)
Edit-FileOnce -Path $dialogFile `
    -Old ($oldButtonTextLines -join "`r`n") `
    -New ($newButtonTextLines -join "`r`n") `
    -Label "PosterOptionsDialog: libelle favori IPTV + masquage watched"

# 3c. PosterOptionsHost : propager isIptvItem et router le bon toggle
$oldHostCallLines = @(
    "        PosterOptionsDialog(",
    "            title = target.name,",
    "            isInLibrary = state.isInLibrary,",
    "            isLibraryPending = state.isLibraryPending,",
    "            showManageLists = state.librarySourceMode == LibrarySourceMode.TRAKT,",
    "            isMovie = isMovie,",
    "            isSeries = isSeries,",
    "            isWatched = state.isWatched,",
    "            isWatchedPending = state.isWatchedPending,",
    "            onDismiss = { controller.dismiss() },",
    "            onDetails = {",
    "                onNavigateToDetail(target.id, target.apiType, state.addonBaseUrl)",
    "                controller.dismiss()",
    "            },",
    "            onToggleLibrary = {",
    "                if (state.librarySourceMode == LibrarySourceMode.TRAKT) {",
    "                    controller.openListPicker()",
    "                } else {",
    "                    controller.toggleLibrary()",
    "                    controller.dismiss()",
    "                }",
    "            },"
)
$newHostCallLines = @(
    "        PosterOptionsDialog(",
    "            title = target.name,",
    "            isInLibrary = state.isInLibrary,",
    "            isLibraryPending = state.isLibraryPending,",
    "            showManageLists = state.librarySourceMode == LibrarySourceMode.TRAKT,",
    "            isMovie = isMovie,",
    "            isSeries = isSeries,",
    "            isIptvItem = state.isIptvItem,",
    "            isWatched = state.isWatched,",
    "            isWatchedPending = state.isWatchedPending,",
    "            onDismiss = { controller.dismiss() },",
    "            onDetails = {",
    "                onNavigateToDetail(target.id, target.apiType, state.addonBaseUrl)",
    "                controller.dismiss()",
    "            },",
    "            onToggleLibrary = {",
    "                if (state.isIptvItem) {",
    "                    controller.toggleIptvFavorite()",
    "                    controller.dismiss()",
    "                } else if (state.librarySourceMode == LibrarySourceMode.TRAKT) {",
    "                    controller.openListPicker()",
    "                } else {",
    "                    controller.toggleLibrary()",
    "                    controller.dismiss()",
    "                }",
    "            },"
)
Edit-FileOnce -Path $dialogFile `
    -Old ($oldHostCallLines -join "`r`n") `
    -New ($newHostCallLines -join "`r`n") `
    -Label "PosterOptionsDialog: PosterOptionsHost route favori IPTV"

Write-Host ""
Write-Host "Tout est applique avec succes." -ForegroundColor Cyan
Write-Host "Fichiers .bak crees a cote de chaque fichier modifie." -ForegroundColor Cyan
Write-Host ""
Write-Host "ATTENTION - actions manuelles restantes :" -ForegroundColor Yellow
Write-Host "  1. Ajouter ces 2 strings dans res/values/strings.xml (et traductions) :" -ForegroundColor Yellow
Write-Host "       hero_add_to_favorites" -ForegroundColor Yellow
Write-Host "       hero_remove_from_favorites" -ForegroundColor Yellow
Write-Host "  2. Compiler (:app:assembleFullDebug) pour valider qu'il n'y a pas de" -ForegroundColor Yellow
Write-Host "     collision de package ou d'import manquant." -ForegroundColor Yellow
