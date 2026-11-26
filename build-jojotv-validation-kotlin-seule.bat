@echo off
setlocal EnableExtensions

rem Compile Kotlin for FullValidation without packaging an APK.
set "PROJECT_DIR=%~dp0"
set "JDK17_HOME="

for /d %%D in ("%USERPROFILE%\.jdks\jdk-17*") do if exist "%%~fD\bin\java.exe" set "JDK17_HOME=%%~fD"
if defined JDK17_HOME (
    set "JAVA_HOME=%JDK17_HOME%"
    set "PATH=%JDK17_HOME%\bin;%PATH%"
)

:build
echo.
echo === Verification Kotlin seule (FullValidation) ===
echo Cette commande ne produit pas d'APK et ne modifie pas la version.
pushd "%PROJECT_DIR%"
call gradlew.bat :app:compileFullValidationKotlin --console=rich -PnuvioTargetAbis=armeabi-v7a -PciUseDebugSigning=false --build-cache
set "GRADLE_RESULT=%ERRORLEVEL%"
popd
if not "%GRADLE_RESULT%"=="0" goto build_failed

echo.
powershell -NoProfile -Command "$esc=[char]27; Write-Host ('{0}[92mBUILD SUCCESSFUL{0}[0m' -f $esc)"
echo Compilation Kotlin FullValidation reussie. Aucune APK n'a ete produite.
goto menu

:build_failed
echo.
powershell -NoProfile -Command "$esc=[char]27; Write-Host ('{0}[91mBUILD FAILED{0}[0m' -f $esc)"
echo ECHEC : la verification Kotlin Gradle a echoue.

:menu
echo.
choice /C RQ /N /M "[R] Recommencer   [Q] Quitter"
if errorlevel 2 exit /b 0
goto build
