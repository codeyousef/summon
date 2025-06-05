#!/bin/bash

# Script to verify Maven Central Portal credentials

echo "Maven Central Credential Verification"
echo "===================================="
echo ""

# Check if credentials are set
if [ -z "$MAVEN_CENTRAL_USERNAME" ] && [ -z "$ORG_GRADLE_PROJECT_mavenCentralUsername" ]; then
    echo "❌ ERROR: Maven Central username not found"
    echo "   Set either MAVEN_CENTRAL_USERNAME or ORG_GRADLE_PROJECT_mavenCentralUsername"
else
    USERNAME="${ORG_GRADLE_PROJECT_mavenCentralUsername:-$MAVEN_CENTRAL_USERNAME}"
    echo "✅ Username found: ${USERNAME:0:15}..."
fi

if [ -z "$MAVEN_CENTRAL_PASSWORD" ] && [ -z "$ORG_GRADLE_PROJECT_mavenCentralPassword" ]; then
    echo "❌ ERROR: Maven Central password not found"
    echo "   Set either MAVEN_CENTRAL_PASSWORD or ORG_GRADLE_PROJECT_mavenCentralPassword"
else
    echo "✅ Password found (hidden)"
    PASSWORD="${ORG_GRADLE_PROJECT_mavenCentralPassword:-$MAVEN_CENTRAL_PASSWORD}"
fi

echo ""
echo "Testing authentication with Maven Central Portal API..."
echo ""

# Use the correct environment variables
USERNAME="${ORG_GRADLE_PROJECT_mavenCentralUsername:-$MAVEN_CENTRAL_USERNAME}"
PASSWORD="${ORG_GRADLE_PROJECT_mavenCentralPassword:-$MAVEN_CENTRAL_PASSWORD}"

if [ -n "$USERNAME" ] && [ -n "$PASSWORD" ]; then
    # Test authentication against Central Portal API
    RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" \
        -u "$USERNAME:$PASSWORD" \
        "https://central.sonatype.com/api/v1/publisher/status")
    
    if [ "$RESPONSE" = "200" ]; then
        echo "✅ SUCCESS: Authentication verified!"
        echo ""
        echo "Your credentials are valid for the Maven Central Portal."
    elif [ "$RESPONSE" = "401" ]; then
        echo "❌ FAILED: Invalid credentials (HTTP 401)"
        echo ""
        echo "Please check:"
        echo "1. You're using Central Portal tokens (not OSSRH credentials)"
        echo "2. Your token hasn't expired"
        echo "3. Username format is correct (e.g., tokenuser_xxxxxxxx)"
        echo ""
        echo "Generate new tokens at: https://central.sonatype.com/account"
    else
        echo "❌ FAILED: Unexpected response (HTTP $RESPONSE)"
        echo ""
        echo "This might indicate a network issue or API problem."
    fi
else
    echo "❌ Cannot test - credentials not set"
fi

echo ""
echo "Credential Setup Instructions:"
echo "=============================="
echo ""
echo "1. Go to https://central.sonatype.com/"
echo "2. Sign in or create an account"
echo "3. Navigate to Account → Generate Token"
echo "4. Copy the username (starts with 'tokenuser_')"
echo "5. Copy the password (keep it secret!)"
echo ""
echo "Set credentials using one of these methods:"
echo ""
echo "Method 1 - Environment variables (for this session):"
echo "  export ORG_GRADLE_PROJECT_mavenCentralUsername='your_username'"
echo "  export ORG_GRADLE_PROJECT_mavenCentralPassword='your_password'"
echo ""
echo "Method 2 - gradle.properties (local development):"
echo "  Add to ~/.gradle/gradle.properties:"
echo "  mavenCentralUsername=your_username"
echo "  mavenCentralPassword=your_password"
echo ""
echo "Method 3 - GitHub Secrets (for CI/CD):"
echo "  Add secrets MAVEN_CENTRAL_USERNAME and MAVEN_CENTRAL_PASSWORD"