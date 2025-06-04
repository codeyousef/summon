@echo off
setlocal enabledelayedexpansion

echo Publishing to Maven Central Portal...

REM Check for required environment variables
if "%CENTRAL_USERNAME%"=="" (
    echo Error: CENTRAL_USERNAME must be set
    exit /b 1
)
if "%CENTRAL_PASSWORD%"=="" (
    echo Error: CENTRAL_PASSWORD must be set
    exit /b 1
)

REM Build the project first
echo Building project...
call gradlew.bat build -x test -x jsTest -x jsBrowserTest

REM Publish to local repository to generate all artifacts
echo Publishing to local repository to generate artifacts...
call gradlew.bat publishToMavenLocal

REM Create staging directory
set STAGING_DIR=build\central-portal-staging
if exist %STAGING_DIR% rmdir /s /q %STAGING_DIR%
mkdir %STAGING_DIR%

REM Copy all artifacts from local Maven repository
echo Collecting artifacts...
set LOCAL_REPO=%USERPROFILE%\.m2\repository\io\github\codeyousef\summon
set VERSION=0.2.5.1

REM Copy main module artifacts
set MODULE_DIR=%LOCAL_REPO%\%VERSION%
if exist %MODULE_DIR% (
    echo Copying summon artifacts...
    copy /y "%MODULE_DIR%\*.jar" "%STAGING_DIR%\" >nul 2>&1
    copy /y "%MODULE_DIR%\*.pom" "%STAGING_DIR%\" >nul 2>&1
    copy /y "%MODULE_DIR%\*.asc" "%STAGING_DIR%\" >nul 2>&1
    copy /y "%MODULE_DIR%\*.module" "%STAGING_DIR%\" >nul 2>&1
)

REM Copy JS module artifacts
set MODULE_DIR=%LOCAL_REPO%-js\%VERSION%
if exist %MODULE_DIR% (
    echo Copying summon-js artifacts...
    copy /y "%MODULE_DIR%\*.jar" "%STAGING_DIR%\" >nul 2>&1
    copy /y "%MODULE_DIR%\*.pom" "%STAGING_DIR%\" >nul 2>&1
    copy /y "%MODULE_DIR%\*.asc" "%STAGING_DIR%\" >nul 2>&1
    copy /y "%MODULE_DIR%\*.module" "%STAGING_DIR%\" >nul 2>&1
)

REM Copy JVM module artifacts
set MODULE_DIR=%LOCAL_REPO%-jvm\%VERSION%
if exist %MODULE_DIR% (
    echo Copying summon-jvm artifacts...
    copy /y "%MODULE_DIR%\*.jar" "%STAGING_DIR%\" >nul 2>&1
    copy /y "%MODULE_DIR%\*.pom" "%STAGING_DIR%\" >nul 2>&1
    copy /y "%MODULE_DIR%\*.asc" "%STAGING_DIR%\" >nul 2>&1
    copy /y "%MODULE_DIR%\*.module" "%STAGING_DIR%\" >nul 2>&1
)

REM Create bundle ZIP
echo Creating bundle...
set BUNDLE_FILE=build\central-bundle.zip
cd %STAGING_DIR%
powershell -command "Compress-Archive -Path * -DestinationPath ..\..\central-bundle.zip -Force"
cd ..\..\..

REM Upload bundle
echo Uploading bundle to Central Portal...
echo Please use curl or another tool to upload the bundle at build\central-bundle.zip
echo.
echo Command to run:
echo curl --request POST --header "Authorization: Bearer <token>" --form bundle=@build\central-bundle.zip https://central.sonatype.com/api/v1/publisher/upload?publishingType=AUTOMATIC
echo.
echo Where <token> is the base64 encoding of CENTRAL_USERNAME:CENTRAL_PASSWORD