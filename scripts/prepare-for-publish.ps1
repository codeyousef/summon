#!/usr/bin/env pwsh
# Script to prepare the library for Maven publishing
# This removes files that are not relevant for the published library

Write-Host "Preparing Summon library for Maven publishing..."

# HTML demo files
$htmlDemoFiles = @(
    "client.html",
    "direct-component.html",
    "direct-viewer.html",
    "my-page.html",
    "router-example.html",
    "simple-page.html",
    "standalone-viewer.html",
    "view-simple-page.html"
)

# JavaScript demo files
$jsDemoFiles = @(
    "direct-page.js"
)

# Remove HTML demo files
foreach ($file in $htmlDemoFiles) {
    if (Test-Path $file) {
        Write-Host "Removing demo file: $file"
        Remove-Item $file
    }
}

# Remove JavaScript demo files
foreach ($file in $jsDemoFiles) {
    if (Test-Path $file) {
        Write-Host "Removing demo file: $file"
        Remove-Item $file
    }
}

# Clean build directory
if (Test-Path "build") {
    Write-Host "Cleaning build directory..."
    Remove-Item -Recurse -Force "build"
}

# Clean gradle cache directory
if (Test-Path ".gradle") {
    Write-Host "Cleaning Gradle cache directory..."
    Remove-Item -Recurse -Force ".gradle"
}

# Clean Kotlin JS store
if (Test-Path "kotlin-js-store") {
    Write-Host "Cleaning Kotlin JS store..."
    Remove-Item -Recurse -Force "kotlin-js-store"
}

# Create local.properties if it doesn't exist
$localPropsPath = "local.properties"
if (-not (Test-Path $localPropsPath)) {
    Write-Host "Creating empty local.properties file..."
    Set-Content -Path $localPropsPath -Value "# Local configuration properties for Maven publishing`n# Add ossrhUsername and ossrhPassword here for local signing"
}

Write-Host "Library preparation complete. You can now run:"
Write-Host "./gradlew publish    # Publish to Maven repositories"
Write-Host "./gradlew publishToMavenLocal    # Publish to local Maven repository" 