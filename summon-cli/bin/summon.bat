@echo off
REM Summon CLI launcher script for Windows
setlocal

set SCRIPT_DIR=%~dp0
set JAR_FILE=%SCRIPT_DIR%..\build\libs\summon-cli-0.4.2.1.jar

if exist "%JAR_FILE%" (
    java -jar "%JAR_FILE%" %*
) else (
    echo Error: summon-cli JAR not found at %JAR_FILE%
    echo Run: gradlew.bat :summon-cli:shadowJar
    exit /b 1
)
