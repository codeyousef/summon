@echo off
echo.
echo =====================================
echo   Portfolio Example - Docker Fix
echo =====================================
echo.

echo 1. Checking Docker Desktop status...
tasklist | findstr "Docker Desktop" >nul
if %errorlevel% == 0 (
    echo    ✓ Docker Desktop is running
) else (
    echo    ✗ Docker Desktop is not running
    echo    Please start Docker Desktop and wait for it to fully initialize
    pause
    exit /b 1
)

echo.
echo 2. Checking WSL2 distributions...
wsl --list --verbose
echo.

echo 3. Testing Docker connectivity...
docker --version
if %errorlevel% == 0 (
    echo    ✓ Docker CLI is accessible
) else (
    echo    ✗ Docker CLI is not accessible
    echo    Please check WSL2 integration in Docker Desktop settings
    pause
    exit /b 1
)

echo.
echo 4. Testing PostgreSQL container...
docker run --rm postgres:15-alpine echo "PostgreSQL container test successful"
if %errorlevel% == 0 (
    echo    ✓ PostgreSQL container works
) else (
    echo    ✗ PostgreSQL container failed
    echo    Please check Docker Hub connectivity and image availability
    pause
    exit /b 1
)

echo.
echo 5. All Docker checks passed! You can now run:
echo    gradlew quarkusDev
echo.
pause