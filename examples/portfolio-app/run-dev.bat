@echo off
echo Starting Portfolio Example...
echo =============================
echo.
echo Using H2 in-memory database (no Docker required)
echo.

rem Run with H2 profile to avoid Hibernate Reactive threading issues
gradlew.bat quarkusDev -Dquarkus.profile=h2