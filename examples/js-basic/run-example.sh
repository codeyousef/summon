#!/bin/bash

# Summon JS Example Runner
# This script builds and serves the standalone JS example

set -e

echo "🚀 Starting Summon JS Example..."
echo

# Step 1: Build the project
echo "📦 Building Kotlin/JS project..."
./gradlew jsBrowserDevelopmentWebpack
echo "✅ Build completed successfully"

# Step 2: Copy compiled JavaScript
echo "📋 Copying compiled JavaScript..."
mkdir -p build/processedResources/js/main
cp build/kotlin-webpack/js/developmentExecutable/js.js build/processedResources/js/main/
echo "✅ JavaScript copied to resources"

# Step 3: Verify files exist
if [ ! -f "build/processedResources/js/main/index.html" ]; then
    echo "❌ Error: index.html not found"
    exit 1
fi

if [ ! -f "build/processedResources/js/main/js.js" ]; then
    echo "❌ Error: js.js not found"
    exit 1
fi

echo "✅ All files ready"

# Step 4: Start HTTP server
echo
echo "🌐 Starting HTTP server on port 8081..."
echo "📖 Open your browser to: http://localhost:8081"
echo "🛑 Press Ctrl+C to stop the server"
echo

cd build/processedResources/js/main

# Try different HTTP server options
if command -v python3 &> /dev/null; then
    python3 -m http.server 8081
elif command -v python &> /dev/null; then
    python -m http.server 8081
elif command -v npx &> /dev/null; then
    npx http-server -p 8081
else
    echo "❌ Error: No HTTP server available"
    echo "Please install Python 3 or Node.js to run the example"
    echo
    echo "Manual steps:"
    echo "1. Navigate to: $(pwd)"
    echo "2. Start an HTTP server on port 8081"
    echo "3. Open http://localhost:8081 in your browser"
    exit 1
fi