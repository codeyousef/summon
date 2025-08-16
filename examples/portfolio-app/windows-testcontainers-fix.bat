@echo off
echo.
echo ================================================
echo   Portfolio Example - Testcontainers Fix
echo ================================================
echo.

echo This script configures Testcontainers for Windows/WSL2 environments
echo.

echo 1. Creating testcontainers.properties file...
(
echo # Testcontainers configuration for Windows/WSL2
echo docker.client.strategy=org.testcontainers.dockerclient.EnvironmentAndSystemPropertyClientProviderStrategy
echo testcontainers.reuse.enable=true
echo testcontainers.ryuk.disabled=false
echo # Use host networking on WSL2
echo testcontainers.host.override=localhost
) > testcontainers.properties

echo    ✓ testcontainers.properties created

echo.
echo 2. Creating .testcontainers.properties in user home...
set USER_HOME=%USERPROFILE%
copy testcontainers.properties "%USER_HOME%\.testcontainers.properties" >nul
echo    ✓ .testcontainers.properties copied to %USER_HOME%

echo.
echo 3. Testing Docker connectivity for Testcontainers...
docker info | findstr "Server Version" >nul
if %errorlevel% == 0 (
    echo    ✓ Docker daemon is accessible
) else (
    echo    ✗ Docker daemon is not accessible
    echo    Please ensure Docker Desktop is running and WSL2 integration is enabled
    pause
    exit /b 1
)

echo.
echo 4. Configuration complete! Testcontainers should now work properly.
echo.
echo   To run with PostgreSQL Dev Services:
echo     gradlew quarkusDev
echo.
echo   To run without Docker (H2 database):
echo     gradlew quarkusDev -Dquarkus.profile=h2
echo.
pause