@echo off
setlocal EnableDelayedExpansion

set "PROJECT_DIR=C:\Users\arnau\Desktop\JojoTV"
set "DEST_DESKTOP=C:\Users\arnau\Desktop"
set "DEST_Z=Z:\"
set "ABI=armeabi-v7a"
set "APK_OUTPUT_DIR=app\build\outputs\apk\full\release"

:BUILD
cls
echo ==========================================
echo   Compilation de JojoTV - %ABI%
echo ==========================================
echo.

if not exist "%PROJECT_DIR%" (
    echo [ERREUR] Le dossier du projet est introuvable :
    echo %PROJECT_DIR%
    echo.
    echo Verifie que le dossier du projet s'appelle bien JojoTV sur le Bureau.
    echo.
    pause
    exit /b 1
)

cd /d "%PROJECT_DIR%"
if errorlevel 1 (
    echo [ERREUR] Impossible d'ouvrir le dossier projet.
    echo.
    pause
    exit /b 1
)

if not exist "gradlew.bat" (
    echo [ERREUR] gradlew.bat est introuvable dans le dossier projet.
    echo.
    pause
    exit /b 1
)

set "CI_USE_DEBUG_SIGNING=true"

if /I "%~1"=="clean" goto CLEAN_BUILD

echo Mode rapide : conservation du daemon Gradle et des caches.
echo Pour forcer un nettoyage de secours : lance ce BAT avec le parametre clean.
echo Exemple : JojoTV_Build_APK_armeabi-v7a.bat clean
goto START_GRADLE

:CLEAN_BUILD
echo Mode clean demande : arret du daemon et nettoyage des dossiers temporaires.
call gradlew.bat --stop >nul 2>nul
timeout /t 2 /nobreak >nul

echo.
echo Nettoyage des dossiers temporaires verrouillables...
call :REMOVE_DIR "app\build\kotlinToolingMetadata"
call :REMOVE_DIR "app\build\intermediates\default_proguard_files\global"
call :REMOVE_DIR "app\build\intermediates\default_proguard_files"
call :REMOVE_DIR "app\build\intermediates\incremental\fullRelease\mergeFullReleaseResources"
call :REMOVE_DIR "app\build\intermediates\incremental\fullRelease\packageFullReleaseResources"
call :REMOVE_DIR "app\build\intermediates\incremental\packageFullRelease\tmp"
call :REMOVE_DIR "app\build\intermediates\merged_res_blame_folder\fullRelease"
call :REMOVE_DIR "app\build\intermediates\merged_res\fullRelease"
call :REMOVE_DIR "app\build\intermediates\packaged_res\fullRelease"
call :REMOVE_DIR "app\build\generated\hilt\component_trees\fullRelease"

:START_GRADLE
echo.
echo Lancement de la compilation release split ABI...
echo Commande : gradlew.bat :app:assembleFullRelease --build-cache --parallel
echo.
call gradlew.bat :app:assembleFullRelease --build-cache --parallel

if errorlevel 1 (
    echo.
    echo [ERREUR] La compilation a echoue.
    echo.
    echo Si l'erreur indique encore un dossier impossible a supprimer,
    echo ferme Android Studio, l'Explorateur ouvert dans app\build et relance ce BAT.
    goto ASK_RETRY
)

echo.
echo [SUCCES] Compilation terminee.

for /f "delims=" %%a in ('powershell -NoProfile -Command "Get-Date -Format 'yyyyMMdd_HHmm'"') do set "STAMP=%%a"
set "NEW_NAME=JojoTV_%ABI%_%STAMP%.apk"
set "APK_SOURCE="

if not exist "%APK_OUTPUT_DIR%" (
    echo [ERREUR] Dossier APK introuvable :
    echo %PROJECT_DIR%\%APK_OUTPUT_DIR%
    echo.
    goto ASK_RETRY
)

for /f "delims=" %%f in ('dir /b /a:-d /o:-d "%APK_OUTPUT_DIR%\*%ABI%*.apk" 2^>nul') do (
    if not defined APK_SOURCE set "APK_SOURCE=%APK_OUTPUT_DIR%\%%f"
)

if not defined APK_SOURCE (
    echo [ERREUR] Aucun APK %ABI% trouve dans :
    echo %PROJECT_DIR%\%APK_OUTPUT_DIR%
    echo.
    echo Fichiers disponibles :
    dir /b "%APK_OUTPUT_DIR%\*.apk" 2^>nul
    echo.
    goto ASK_RETRY
)

set "DESKTOP_TARGET=%DEST_DESKTOP%\%NEW_NAME%"
set "Z_TARGET=%DEST_Z%%NEW_NAME%"

echo.
echo APK source : %PROJECT_DIR%\%APK_SOURCE%
echo Nom export : %NEW_NAME%
echo.

echo Copie sur le Bureau...
copy /Y "%APK_SOURCE%" "%DESKTOP_TARGET%" >nul
if errorlevel 1 (
    echo [ERREUR] Copie vers le Bureau echouee.
) else (
    echo [OK] Bureau : %DESKTOP_TARGET%
)

echo.
if exist "%DEST_Z%" (
    echo Copie sur Z:\...
    copy /Y "%APK_SOURCE%" "%Z_TARGET%" >nul
    if errorlevel 1 (
        echo [ERREUR] Copie vers Z:\ echouee.
    ) else (
        echo [OK] Z:\ : %Z_TARGET%
    )
) else (
    echo [INFO] Le lecteur Z:\ est introuvable, copie Z:\ ignoree.
)

echo.
echo [TERMINE] APK %ABI% compile et exporte.

:ASK_RETRY
echo.
echo [R] Relancer une compilation    [Q] Quitter
echo.
choice /C RQ /N /M "Votre choix : "
if errorlevel 2 exit /b 0
if errorlevel 1 goto BUILD

:REMOVE_DIR
if exist "%~1" (
    rmdir /S /Q "%~1" 2>nul
    if exist "%~1" (
        echo [ATTENTION] Impossible de supprimer : %~1
    ) else (
        echo [OK] Nettoye : %~1
    )
)
exit /b 0
