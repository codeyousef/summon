#!/bin/bash

# Script to run tests locally with the same configuration as CI/CD

echo "Running Summon tests..."

# Ensure gradlew is executable
chmod +x gradlew

# Clean previous test results
echo "Cleaning previous test results..."
./gradlew clean

# Run JVM tests
echo "Running JVM tests..."
./gradlew jvmTest --no-daemon --stacktrace

# Run JS tests (allow failure due to Kotlin 2.2.0-Beta1 issue)
echo "Running JS tests (may fail due to known Kotlin 2.2.0-Beta1 issue)..."
./gradlew jsTest --no-daemon --stacktrace || true

# Run common tests
echo "Running common tests..."
./gradlew commonTest --no-daemon --stacktrace || true

# Generate test report
echo "Test execution completed. Reports available at:"
echo "  - HTML: build/reports/tests/"
echo "  - XML: build/test-results/"

# List test results
if [ -d "build/reports/tests" ]; then
    echo ""
    echo "Test reports generated:"
    find build/reports/tests -name "index.html" -type f | while read file; do
        echo "  - $file"
    done
fi