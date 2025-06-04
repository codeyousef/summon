#!/bin/bash

# Local testing script for Summon KMP project
# This script runs all tests and verifies the build before publishing

set -e  # Exit on any error

echo "🚀 Starting Summon KMP Testing Pipeline..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Check if Chrome is available for headless testing
check_chrome() {
    if command -v google-chrome &> /dev/null; then
        print_status "Chrome found for headless testing"
        export CHROME_BIN=$(which google-chrome)
    elif command -v chromium-browser &> /dev/null; then
        print_status "Chromium found for headless testing"
        export CHROME_BIN=$(which chromium-browser)
    elif command -v chrome &> /dev/null; then
        print_status "Chrome found for headless testing"
        export CHROME_BIN=$(which chrome)
    else
        print_warning "Chrome not found. JavaScript tests may fail."
        print_warning "Please install Chrome or Chromium for JavaScript testing."
        read -p "Continue anyway? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi
}

# Clean build
clean_build() {
    echo "🧹 Cleaning previous builds..."
    ./gradlew clean --quiet
    print_status "Build cleaned"
}

# Run JVM tests
run_jvm_tests() {
    echo "☕ Running JVM tests..."
    if ./gradlew jvmTest --no-daemon; then
        print_status "JVM tests passed"
    else
        print_error "JVM tests failed"
        exit 1
    fi
}

# Run JS tests with Chrome headless
run_js_tests() {
    echo "🌐 Running JavaScript tests with Chrome headless..."
    if ./gradlew jsTest --no-daemon; then
        print_status "JavaScript tests passed"
    else
        print_error "JavaScript tests failed"
        echo "💡 Tip: Make sure Chrome is installed and available in PATH"
        exit 1
    fi
}

# Run common tests
run_common_tests() {
    echo "🔄 Running common tests..."
    if ./gradlew cleanTest test --no-daemon; then
        print_status "Common tests passed"
    else
        print_error "Common tests failed"
        exit 1
    fi
}

# Build all targets
build_all() {
    echo "🔨 Building all targets..."
    if ./gradlew build --no-daemon; then
        print_status "Build completed successfully"
    else
        print_error "Build failed"
        exit 1
    fi
}

# Optional: Test publishing to local repository
test_local_publish() {
    echo "📦 Testing local publishing..."
    if ./gradlew publishToMavenLocal --no-daemon; then
        print_status "Local publishing successful"
        echo "📍 Artifacts published to: ~/.m2/repository/code/yousef/summon/"
    else
        print_error "Local publishing failed"
        exit 1
    fi
}

# Main execution
main() {
    echo "Starting at $(date)"
    echo "Working directory: $(pwd)"
    echo ""
    
    # Check prerequisites
    check_chrome
    
    # Make gradlew executable if needed
    chmod +x gradlew
    
    # Run the testing pipeline
    clean_build
    run_jvm_tests
    run_js_tests
    run_common_tests
    build_all
    
    # Ask if user wants to test local publishing
    echo ""
    read -p "🤔 Test local publishing? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        test_local_publish
    fi
    
    echo ""
    print_status "All tests passed! ✨"
    echo "🎉 Your project is ready for publishing!"
    echo ""
    echo "Next steps:"
    echo "  1. Push your changes to trigger CI/CD"
    echo "  2. Create a release to publish to Maven repositories"
    echo "  3. Check the GitHub Actions tab for pipeline status"
}

# Run main function
main "$@"
