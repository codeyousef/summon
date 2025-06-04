@echo off

REM Script to run tests locally with the same configuration as CI/CD

echo Running Summon tests...

REM Clean previous test results
echo Cleaning previous test results...
call gradlew.bat clean

REM Run JVM tests
echo Running JVM tests...
call gradlew.bat jvmTest --no-daemon --stacktrace

REM Run JS tests (allow failure due to Kotlin 2.2.0-Beta1 issue)
echo Running JS tests (may fail due to known Kotlin 2.2.0-Beta1 issue)...
call gradlew.bat jsTest --no-daemon --stacktrace 2>nul

REM Run common tests
echo Running common tests...
call gradlew.bat commonTest --no-daemon --stacktrace 2>nul

REM Generate test report
echo Test execution completed. Reports available at:
echo   - HTML: build\reports\tests\
echo   - XML: build\test-results\

REM List test results
if exist "build\reports\tests" (
    echo.
    echo Test reports generated:
    for /r build\reports\tests %%f in (index.html) do echo   - %%f
)