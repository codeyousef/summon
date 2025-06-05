#!/bin/bash

# Script to publish to the new Maven Central Portal
# Requires CENTRAL_USERNAME and CENTRAL_PASSWORD environment variables

set -e

echo "==============================================="
echo "Publishing to Maven Central Portal..."
echo "==============================================="

# Check for required environment variables
if [ -z "$CENTRAL_USERNAME" ] || [ -z "$CENTRAL_PASSWORD" ]; then
    echo "Error: CENTRAL_USERNAME and CENTRAL_PASSWORD must be set"
    exit 1
fi

# Build the project first
echo "Building project..."
./gradlew build -x test -x jsTest -x jsBrowserTest

# Publish to local repository to generate all artifacts
echo "Publishing to local repository to generate artifacts..."
./gradlew publishToMavenLocal

# Create staging directory
STAGING_DIR="build/central-portal-staging"
rm -rf $STAGING_DIR
mkdir -p $STAGING_DIR

# Copy all artifacts from local Maven repository
echo "Collecting artifacts..."
LOCAL_REPO="$HOME/.m2/repository/io/github/codeyousef/summon"
VERSION="0.2.5.1"

# Copy all module artifacts (excluding .klib files for Central Portal)
for module in "" "-js" "-jvm"; do
    MODULE_NAME="summon${module}"
    MODULE_DIR="$LOCAL_REPO${module}/$VERSION"
    
    if [ -d "$MODULE_DIR" ]; then
        echo "Copying $MODULE_NAME artifacts..."
        # Copy JAR files (not KLIB)
        cp -v "$MODULE_DIR"/*.jar "$STAGING_DIR/" 2>/dev/null || true
        cp -v "$MODULE_DIR"/*.pom "$STAGING_DIR/" 2>/dev/null || true
        cp -v "$MODULE_DIR"/*.asc "$STAGING_DIR/" 2>/dev/null || true
        # Skip .klib files as Maven Central doesn't accept them
        # Skip .module files as they reference the .klib files
    fi
done

# Create bundle ZIP
BUNDLE_FILE="build/central-bundle.zip"
echo "Creating bundle..."
cd $STAGING_DIR
zip -r "../../central-bundle.zip" *
cd ../../..

# Calculate auth token
AUTH_TOKEN=$(printf "%s:%s" "$CENTRAL_USERNAME" "$CENTRAL_PASSWORD" | base64 | tr -d '\n')

# Upload bundle
echo "Uploading bundle to Central Portal..."
RESPONSE=$(curl --request POST \
  --silent \
  --write-out "\n%{http_code}" \
  --header "Authorization: Bearer $AUTH_TOKEN" \
  --form bundle=@$BUNDLE_FILE \
  "https://central.sonatype.com/api/v1/publisher/upload?publishingType=AUTOMATIC")

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" = "201" ] || [ "$HTTP_CODE" = "200" ]; then
    echo "Successfully uploaded to Central Portal!"
    echo "Response: $BODY"
    echo ""
    echo "The deployment will be automatically published to Maven Central after validation."
    echo "You can monitor the status at https://central.sonatype.com"
else
    echo "Upload failed with status code: $HTTP_CODE"
    echo "Response: $BODY"
    exit 1
fi