#!/bin/bash
# Script to prepare the library for Maven publishing
# This removes files that are not relevant for the published library

echo "Preparing Summon library for Maven publishing..."

# HTML demo files
HTML_DEMO_FILES=(
    "client.html"
    "direct-component.html"
    "direct-viewer.html"
    "my-page.html"
    "router-example.html"
    "simple-page.html"
    "standalone-viewer.html"
    "view-simple-page.html"
)

# JavaScript demo files
JS_DEMO_FILES=(
    "direct-page.js"
)

# Remove HTML demo files
for file in "${HTML_DEMO_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "Removing demo file: $file"
        rm "$file"
    fi
done

# Remove JavaScript demo files
for file in "${JS_DEMO_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "Removing demo file: $file"
        rm "$file"
    fi
done

# Clean build directory
if [ -d "build" ]; then
    echo "Cleaning build directory..."
    rm -rf "build"
fi

# Clean gradle cache directory
if [ -d ".gradle" ]; then
    echo "Cleaning Gradle cache directory..."
    rm -rf ".gradle"
fi

# Clean Kotlin JS store
if [ -d "kotlin-js-store" ]; then
    echo "Cleaning Kotlin JS store..."
    rm -rf "kotlin-js-store"
fi

# Create local.properties if it doesn't exist
LOCAL_PROPS="local.properties"
if [ ! -f "$LOCAL_PROPS" ]; then
    echo "Creating empty local.properties file..."
    echo "# Local configuration properties for Maven publishing" > "$LOCAL_PROPS"
    echo "# Add ossrhUsername and ossrhPassword here for local signing" >> "$LOCAL_PROPS"
fi

echo "Library preparation complete. You can now run:"
echo "./gradlew publish    # Publish to Maven repositories"
echo "./gradlew publishToMavenLocal    # Publish to local Maven repository" 