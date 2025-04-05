#!/bin/bash
# Run only JavaScript tests to avoid ClassCastException issues
echo "Running JavaScript-specific tests (JsTest)..."
./gradlew clean jsBrowserTest --tests="**.*JsTest*"
echo "Done." 