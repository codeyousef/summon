@echo off
echo Configuring Docker Desktop for Testcontainers on Windows
echo ========================================================
echo.
echo Please follow these steps in Docker Desktop:
echo.
echo 1. Open Docker Desktop Settings
echo 2. Go to "General" settings
echo 3. Enable "Expose daemon on tcp://localhost:2375 without TLS"
echo 4. Click "Apply & Restart"
echo.
echo After Docker Desktop restarts, press any key to test the connection...
pause >nul

echo.
echo Testing Docker connection on TCP...
docker -H tcp://localhost:2375 version >nul 2>&1
if %errorlevel% equ 0 (
    echo SUCCESS: Docker is accessible via TCP on localhost:2375
    echo.
    echo You can now run the portfolio example with:
    echo gradlew quarkusDev
) else (
    echo ERROR: Could not connect to Docker on tcp://localhost:2375
    echo Please ensure Docker Desktop is configured correctly.
)
echo.
pause