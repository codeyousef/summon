#!/usr/bin/env fish
# Local E2E test runner for hydration tests
# Usage: ./run-hydration-tests.fish [--wasm]
#
# NOTE: This script expects a CLI-generated project to exist.
# Generate one first with: summon new hydration-test --template js

set -l ROOT_DIR (dirname (status -f))/..
set -l TARGET "js"
set -l TEST_PROJECT_DIR "$ROOT_DIR/e2e-tests/temp/hydration-test"

# Parse arguments
for arg in $argv
    switch $arg
        case --wasm -w
            set TARGET "wasm"
        case --help -h
            echo "Usage: ./run-hydration-tests.fish [--wasm]"
            echo "  --wasm, -w  Run WASM target instead of JS"
            echo ""
            echo "Note: Generate a test project first:"
            echo "  summon new e2e-tests/temp/hydration-test --template js"
            exit 0
    end
end

echo "🧪 Running Hydration E2E Tests (Target: $TARGET)"
echo "================================================"

# Check if test project exists
if not test -d "$TEST_PROJECT_DIR"
    echo "❌ Test project not found at $TEST_PROJECT_DIR"
    echo "Generate one first with:"
    echo "  summon new e2e-tests/temp/hydration-test --template js"
    exit 1
end

# Kill anything on port 8080
echo "Cleaning up port 8080..."
lsof -ti:8080 | xargs kill -9 2>/dev/null; or true

# Build and start the server
echo ""
echo "Starting test project..."

if test "$TARGET" = "wasm"
    echo "WASM target not yet configured"
    exit 1
else
    set -l GRADLE_CMD "./gradlew jsBrowserDevelopmentRun"
    echo "Running: $GRADLE_CMD"
    
    # Start server in background
    cd $TEST_PROJECT_DIR
    $GRADLE_CMD &
    set -l SERVER_PID $last_pid
    
    # Wait for server to be ready
    echo "Waiting for server at http://localhost:8080..."
    set -l TIMEOUT 180
    set -l ELAPSED 0
    
    while test $ELAPSED -lt $TIMEOUT
        if curl -s http://localhost:8080 > /dev/null 2>&1
            echo "✓ Server is ready!"
            break
        end
        sleep 2
        set ELAPSED (math $ELAPSED + 2)
        echo "  Still waiting... ($ELAPSED seconds)"
    end
    
    if test $ELAPSED -ge $TIMEOUT
        echo "❌ Timeout waiting for server"
        kill $SERVER_PID 2>/dev/null; or true
        exit 1
    end
    
    # Run Playwright tests
    echo ""
    echo "Running Playwright tests..."
    cd $ROOT_DIR/e2e-tests
    
    # Ensure dependencies are installed
    if not test -d node_modules
        echo "Installing npm dependencies..."
        npm install
    end
    
    # Install Playwright browsers if needed
    npx playwright install chromium
    
    # Run the hydration tests
    set -l TEST_EXIT_CODE 0
    BASE_URL=http://localhost:8080 npx playwright test tests/hydration.spec.ts --reporter=list; or set TEST_EXIT_CODE $status
    
    # Cleanup
    echo ""
    echo "Cleaning up..."
    kill $SERVER_PID 2>/dev/null; or true
    lsof -ti:8080 | xargs kill -9 2>/dev/null; or true
    
    if test $TEST_EXIT_CODE -eq 0
        echo ""
        echo "✅ All hydration tests passed!"
    else
        echo ""
        echo "❌ Some tests failed (exit code: $TEST_EXIT_CODE)"
        exit $TEST_EXIT_CODE
    end
end
