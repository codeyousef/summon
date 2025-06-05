@echo off

echo Stopping Gradle daemon...
call gradlew.bat --stop

echo Cleaning project build directory...
if exist build rmdir /s /q build
if exist .gradle rmdir /s /q .gradle

echo Cleaning Gradle cache...
if exist %USERPROFILE%\.gradle\caches rmdir /s /q %USERPROFILE%\.gradle\caches
if exist %USERPROFILE%\.gradle\daemon rmdir /s /q %USERPROFILE%\.gradle\daemon

echo Cleaning Kotlin build directories...
if exist kotlin-js-store rmdir /s /q kotlin-js-store
if exist .kotlin rmdir /s /q .kotlin

echo Cleaning all module build directories...
for /d /r . %%d in (build) do @if exist "%%d" rmdir /s /q "%%d" 2>nul

echo.
echo Gradle cache cleaned successfully!
echo.
echo Now you can run:
echo   gradlew.bat publishAllPublicationsToGitHubPackagesRepository
echo.
echo Or for Maven Central publishing:
echo   publish-to-central-portal.bat
echo.
echo For CI/CD, just push to main branch for automatic publishing.