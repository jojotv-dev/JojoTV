@echo off
setlocal EnableExtensions

rem Production APK: optimized release, restricted to armeabi-v7a.
set "PROJECT_DIR=%~dp0"
set "APK_SOURCE=%PROJECT_DIR%app\build\outputs\apk\full\release\app-full-armeabi-v7a-release.apk"
set "DEST_DIR=%USERPROFILE%\Desktop"
set "APK_DEST="
set "APP_VERSION="
set "VERSION_FILE=%PROJECT_DIR%app\version.properties"
set "VERSION_BACKUP="
set "FAIL_MESSAGE="
set "JDK17_HOME="
set "HAS_RELEASE_KEYSTORE="

for /d %%D in ("%USERPROFILE%\.jdks\jdk-17*") do if exist "%%~fD\bin\java.exe" set "JDK17_HOME=%%~fD"
if defined JDK17_HOME (
    set "JAVA_HOME=%JDK17_HOME%"
    set "PATH=%JDK17_HOME%\bin;%PATH%"
)

if exist "%PROJECT_DIR%nuviotv.jks" set "HAS_RELEASE_KEYSTORE=1"
if exist "%PROJECT_DIR%jojo-tv-release.jks" set "HAS_RELEASE_KEYSTORE=1"
if defined NUVIO_RELEASE_STORE_FILE set "HAS_RELEASE_KEYSTORE=1"
if exist "%PROJECT_DIR%local.properties" findstr /B /C:"NUVIO_RELEASE_STORE_FILE=" "%PROJECT_DIR%local.properties" >nul && set "HAS_RELEASE_KEYSTORE=1"

:build
set "APP_VERSION="
set "FAIL_MESSAGE="
if not defined HAS_RELEASE_KEYSTORE (
    set "FAIL_MESSAGE=cle release introuvable; APK final optimise annule."
    goto build_failed
)
if not exist "%DEST_DIR%" (
    mkdir "%DEST_DIR%"
    if errorlevel 1 (
        set "FAIL_MESSAGE=impossible de creer le dossier du Bureau."
        goto build_failed
    )
)

set "VERSION_BACKUP=%TEMP%\JojoTV-version-%RANDOM%-%RANDOM%.properties"
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
echo === Compilation finale optimise (release) ===
echo Version : %APP_VERSION%
echo ABI : armeabi-v7a uniquement
pushd "%PROJECT_DIR%"
call gradlew.bat :app:assembleFullRelease --console=rich --profile -x :app:lintVitalAnalyzeFullRelease -x :app:lintVitalReportFullRelease -PnuvioTargetAbis=armeabi-v7a -PciUseDebugSigning=false --build-cache
set "GRADLE_RESULT=%ERRORLEVEL%"
popd
if not "%GRADLE_RESULT%"=="0" (
    set "FAIL_MESSAGE=la compilation Gradle optimisee a echoue."
    goto build_failed
)

if not exist "%APK_SOURCE%" (
    set "FAIL_MESSAGE=APK final armeabi-v7a introuvable : %APK_SOURCE%"
    goto build_failed
)

rem Keep the increment once the optimized APK has been built successfully.
if defined VERSION_BACKUP del /Q "%VERSION_BACKUP%" >nul 2>&1
set "VERSION_BACKUP="

move /Y "%APK_SOURCE%" "%APK_DEST%" >nul
if errorlevel 1 (
    set "FAIL_MESSAGE=deplacement impossible vers %APK_DEST%"
    goto build_failed
)

echo.
powershell -NoProfile -Command "$esc=[char]27; Write-Host ('{0}[92mBUILD SUCCESSFUL{0}[0m' -f $esc)"
echo APK final optimise armeabi-v7a :
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
