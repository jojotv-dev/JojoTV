@echo off
setlocal EnableExtensions

rem Fast validation APK: FullValidation variant, release signed, without R8/startup profile.
set "PROJECT_DIR=%~dp0"
set "APK_SOURCE=%PROJECT_DIR%app\build\outputs\apk\full\validation\app-full-armeabi-v7a-validation.apk"
set "DEST_DIR=%USERPROFILE%\Desktop"
set "APK_DEST="
set "APP_VERSION="
set "VERSION_FILE=%PROJECT_DIR%app\version.properties"
set "VERSION_BACKUP="
set "FAIL_MESSAGE="
set "JDK17_HOME="

for /d %%D in ("%USERPROFILE%\.jdks\jdk-17*") do if exist "%%~fD\bin\java.exe" set "JDK17_HOME=%%~fD"
if defined JDK17_HOME (
    set "JAVA_HOME=%JDK17_HOME%"
    set "PATH=%JDK17_HOME%\bin;%PATH%"
)

:build
set "APP_VERSION="
set "FAIL_MESSAGE="
if not exist "%DEST_DIR%" (
    mkdir "%DEST_DIR%"
    if errorlevel 1 (
        set "FAIL_MESSAGE=impossible de creer le dossier du Bureau."
        goto build_failed
    )
)

set "VERSION_BACKUP=%TEMP%\JojoTV-version-validation-%RANDOM%-%RANDOM%.properties"
copy /Y "%VERSION_FILE%" "%VERSION_BACKUP%" >nul
if errorlevel 1 (
    set "VERSION_BACKUP="
    set "FAIL_MESSAGE=impossible de sauvegarder app\version.properties."
    goto build_failed
)
for /f "usebackq delims=" %%V in (`powershell -NoProfile -ExecutionPolicy Bypass -File "%PROJECT_DIR%tools\Increment-App-Version.ps1"`) do set "APP_VERSION=%%V"
if not defined APP_VERSION (
    set "FAIL_MESSAGE=impossible de mettre a jour le numero de version."
    goto build_failed
)
set "APK_DEST=%DEST_DIR%\JojoTV_%APP_VERSION%.apk"

echo.
echo === Compilation rapide de validation (signature release, sans R8, profil startup isole) ===
echo Version : %APP_VERSION%
echo ABI : armeabi-v7a uniquement
echo Variante FullValidation : R8 et ressources desactives; le profil startup lourd reste reserve aux variantes release.
echo Lint vital desactive pour reduire la duree de validation.
echo La cle release est lue depuis la configuration Gradle locale ou l'environnement.
pushd "%PROJECT_DIR%"
call gradlew.bat :app:assembleFullValidation --console=rich -PnuvioTargetAbis=armeabi-v7a -PciUseDebugSigning=false --build-cache -x :app:lintVitalAnalyzeFullValidation -x :app:lintVitalReportFullValidation
set "GRADLE_RESULT=%ERRORLEVEL%"
popd
if not "%GRADLE_RESULT%"=="0" (
    set "FAIL_MESSAGE=la compilation Gradle rapide a echoue."
    goto build_failed
)

if not exist "%APK_SOURCE%" (
    set "FAIL_MESSAGE=APK de validation armeabi-v7a introuvable : %APK_SOURCE%"
    goto build_failed
)

move /Y "%APK_SOURCE%" "%APK_DEST%" >nul
if errorlevel 1 (
    set "FAIL_MESSAGE=deplacement impossible vers %APK_DEST%"
    goto build_failed
)

if defined VERSION_BACKUP del /Q "%VERSION_BACKUP%" >nul 2>&1
set "VERSION_BACKUP="

echo.
powershell -NoProfile -Command "$esc=[char]27; Write-Host ('{0}[92mBUILD SUCCESSFUL{0}[0m' -f $esc)"
echo APK rapide de validation armeabi-v7a :
echo   Bureau : %APK_DEST%
goto menu

:build_failed
if defined VERSION_BACKUP (
    copy /Y "%VERSION_BACKUP%" "%VERSION_FILE%" >nul
    if errorlevel 1 (
        echo ECHEC : restauration du numero de version impossible.
        echo Sauvegarde conservee : %VERSION_BACKUP%
    ) else (
        del /Q "%VERSION_BACKUP%" >nul 2>&1
        echo Version restauree apres l'echec.
    )
    set "VERSION_BACKUP="
)
echo.
powershell -NoProfile -Command "$esc=[char]27; Write-Host ('{0}[91mBUILD FAILED{0}[0m' -f $esc)"
if defined FAIL_MESSAGE echo ECHEC : %FAIL_MESSAGE%

:menu
echo.
choice /C RQ /N /M "[R] Recommencer   [Q] Quitter"
if errorlevel 2 exit /b 0
goto build
