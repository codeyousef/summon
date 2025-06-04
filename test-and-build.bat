@echo off
setlocal enabledelayedexpansion

REM Local testing script for Summon KMP project (Windows)
REM This script runs all tests and verifies the build before publishing

echo 🚀 Starting Summon KMP Testing Pipeline...

REM Check if Chrome is available for headless testing
where chrome >nul 2>nul
if %errorlevel% equ 0 (
    echo ✓ Chrome found for headless testing
    set CHROME_BIN=chrome
    goto :chrome_found
)

where google-chrome >nul 2>nul
if %errorlevel% equ 0 (
    echo ✓ Google Chrome found for headless testing
    set CHROME_BIN=google-chrome
    goto :chrome_found
)

where chromium >nul 2>nul
if %errorlevel% equ 0 (
    echo ✓ Chromium found for headless testing
    set CHROME_BIN=chromium
    goto :chrome_found
)

echo ⚠ Chrome not found. JavaScript tests may fail.
echo ⚠ Please install Chrome for JavaScript testing.
set /p continue="Continue anyway? (y/N): "
if /i not "%continue%"=="y" exit /b 1

:chrome_found

REM Clean build
echo 🧹 Cleaning previous builds...
call gradlew clean --quiet
if %errorlevel% neq 0 (
    echo ✗ Clean failed
    exit /b 1
)
echo ✓ Build cleaned

REM Run JVM tests
echo ☕ Running JVM tests...
call gradlew jvmTest --no-daemon
if %errorlevel% neq 0 (
    echo ✗ JVM tests failed
    exit /b 1
)
echo ✓ JVM tests passed

REM Run JS tests with Chrome headless
echo 🌐 Running JavaScript tests with Chrome headless...
call gradlew jsTest --no-daemon
if %errorlevel% neq 0 (
    echo ✗ JavaScript tests failed
    echo 💡 Tip: Make sure Chrome is installed and available in PATH
    exit /b 1
)
echo ✓ JavaScript tests passed

REM Run common tests
echo 🔄 Running common tests...
call gradlew cleanTest test --no-daemon
if %errorlevel% neq 0 (
    echo ✗ Common tests failed
    exit /b 1
)
echo ✓ Common tests passed

REM Build all targets
echo 🔨 Building all targets...
call gradlew build --no-daemon
if %errorlevel% neq 0 (
    echo ✗ Build failed
    exit /b 1
)
echo ✓ Build completed successfully

REM Optional: Test publishing to local repository
echo.
set /p publish="🤔 Test local publishing? (y/N): "
if /i "%publish%"=="y" (
    echo 📦 Testing local publishing...
    call gradlew publishToMavenLocal --no-daemon
    if %errorlevel% neq 0 (
        echo ✗ Local publishing failed
        exit /b 1
    )
    echo ✓ Local publishing successful
    echo 📍 Artifacts published to: %USERPROFILE%\.m2\repository\code\yousef\summon\
)

echo.
echo ✓ All tests passed! ✨
echo 🎉 Your project is ready for publishing!
echo.
echo Next steps:
echo   1. Push your changes to trigger CI/CD
echo   2. Create a release to publish to Maven repositories
echo   3. Check the GitHub Actions tab for pipeline status

pause
