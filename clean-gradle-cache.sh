#!/bin/bash

echo "Stopping Gradle daemon..."
./gradlew --stop

echo "Cleaning project build directory..."
rm -rf build/
rm -rf .gradle/

echo "Cleaning Gradle cache..."
rm -rf ~/.gradle/caches/
rm -rf ~/.gradle/daemon/

echo "Cleaning Kotlin build directories..."
rm -rf kotlin-js-store/
rm -rf .kotlin/

echo "Cleaning all module build directories..."
find . -type d -name "build" -exec rm -rf {} + 2>/dev/null || true

echo "Gradle cache cleaned successfully!"
echo ""
echo "Now you can run:"
echo "  ./gradlew publishAllPublicationsToGitHubPackagesRepository"
echo ""
echo "Or for Maven Central publishing:"
echo "  ./publish-to-central-portal.sh"
echo ""
echo "For CI/CD, just push to main branch for automatic publishing."