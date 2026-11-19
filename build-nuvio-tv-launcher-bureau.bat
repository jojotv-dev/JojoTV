@echo off
setlocal EnableExtensions

rem Fast development APK: fullDebug, restricted to armeabi-v7a.
set "PROJECT_DIR=%~dp0"
set "APK_SOURCE=%PROJECT_DIR%app\build\outputs\apk\full\debug\app-full-armeabi-v7a-debug.apk"
set "APK_ROOT=%PROJECT_DIR%NuvioTVLauncher_armv7a_debug.apk"
set "DEST_DIR=%USERPROFILE%\Desktop"
set "APK_DEST=%DEST_DIR%\NuvioTVLauncher_armv7a_debug.apk"
set "GRADLE_TASK=:app:assembleFullDebug"
set "FAIL_MESSAGE="
set "JDK17_HOME="

for /d %%D in ("%USERPROFILE%\.jdks\jdk-17*") do if exist "%%~fD\bin\java.exe" set "JDK17_HOME=%%~fD"
if defined JDK17_HOME (
    set "JAVA_HOME=%JDK17_HOME%"
    set "PATH=%JDK17_HOME%\bin;%PATH%"
)

:build
set "FAIL_MESSAGE="
if not exist "%DEST_DIR%" (
    mkdir "%DEST_DIR%"
    if errorlevel 1 (
        set "FAIL_MESSAGE=impossible de creer le dossier du Bureau."
        goto build_failed
    )
)

echo.
echo === Compilation rapide ARMv7 (developpement) ===
echo ABI : armeabi-v7a uniquement
pushd "%PROJECT_DIR%"
call gradlew.bat %GRADLE_TASK% --console=plain --profile -PnuvioTargetAbis=armeabi-v7a -Pdebuggable=true --build-cache
set "GRADLE_RESULT=%ERRORLEVEL%"
popd
if not "%GRADLE_RESULT%"=="0" (
    set "FAIL_MESSAGE=la compilation Gradle a echoue."
    goto build_failed
)

if not exist "%APK_SOURCE%" (
    set "FAIL_MESSAGE=APK armeabi-v7a introuvable : %APK_SOURCE%"
    goto build_failed
)

copy /Y "%APK_SOURCE%" "%APK_ROOT%" >nul
if errorlevel 1 (
    set "FAIL_MESSAGE=copie impossible vers %APK_ROOT%"
    goto build_failed
)

copy /Y "%APK_SOURCE%" "%APK_DEST%" >nul
if errorlevel 1 (
    set "FAIL_MESSAGE=copie impossible vers %APK_DEST%"
    goto build_failed
)

echo.
powershell -NoProfile -Command "$esc=[char]27; Write-Host ('{0}[92mBUILD SUCCESSFUL{0}[0m' -f $esc)"
echo APK armeabi-v7a :
echo   Projet : %APK_ROOT%
echo   Bureau : %APK_DEST%
goto menu

:build_failed
echo.
powershell -NoProfile -Command "$esc=[char]27; Write-Host ('{0}[91mBUILD FAILED{0}[0m' -f $esc)"
if defined FAIL_MESSAGE echo ECHEC : %FAIL_MESSAGE%

:menu
echo.
choice /C RQ /N /M "[R] Recommencer   [Q] Quitter"
if errorlevel 2 exit /b 0
goto build
