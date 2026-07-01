# ============================================================================
# Fix compilation : ajout de la fonction d'extension manquante
# Flow<List<Channel>>.withFavoriteStateForProviders() dans ChannelRepositoryImpl.kt
#
# Contexte : cette fonction est deja appelee ligne ~307 (getChannelsByIds)
# mais n'a jamais ete definie pour Channel (seulement pour Movie et Series
# dans les fichiers respectifs). C'est un reliquat d'un travail anterieur
# inacheve, sans rapport avec le cablage favoris IPTV de la recherche.
#
# Methode : backup .bak avant ecriture, abort si l'ancre ne matche pas
# exactement une fois.
# ============================================================================

$channelRepoFile = "C:\Users\arnau\Desktop\JojoTV\streamvault-data\src\main\java\com\streamvault\data\repository\ChannelRepositoryImpl.kt"

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

$oldTailLines = @(
    "            logicalGroupId = logicalGroupId,",
    "            errorCount = errorCount",
    "        )",
    "}"
)
$newTailLines = @(
    "            logicalGroupId = logicalGroupId,",
    "            errorCount = errorCount",
    "        )",
    "",
    "    private fun Flow<List<Channel>>.withFavoriteStateForProviders(): Flow<List<Channel>> =",
    "        flatMapLatest { channels ->",
    "            val providerIds = channels.map { it.providerId }.distinct()",
    "            if (providerIds.isEmpty()) {",
    "                flowOf(channels)",
    "            } else {",
    "                favoriteDao.getGlobalByTypeForProviders(providerIds, ContentType.LIVE.name)",
    "                    .map { favorites ->",
    "                        val favoriteKeys = favorites.map { it.providerId to it.contentId }.toSet()",
    "                        channels.map { channel ->",
    "                            channel.copy(isFavorite = (channel.providerId to channel.id) in favoriteKeys)",
    "                        }",
    "                    }",
    "            }",
    "        }",
    "}"
)
Edit-FileOnce -Path $channelRepoFile `
    -Old ($oldTailLines -join "`r`n") `
    -New ($newTailLines -join "`r`n") `
    -Label "ChannelRepositoryImpl: ajout withFavoriteStateForProviders()"

Write-Host ""
Write-Host "Fix applique. Relance :" -ForegroundColor Cyan
Write-Host "  .\gradlew.bat :app:assembleFullDebug" -ForegroundColor Cyan
