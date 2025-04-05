# Comprehensive Testing Strategy

This document outlines how to run different test suites in this Kotlin Multiplatform project, including comprehensive JavaScript tests.

## Background

Due to differences between the JVM and JavaScript runtimes, especially concerning object creation and platform APIs, running common tests directly on the JavaScript target can lead to `ClassCastException` errors.

To address this, the standard `./gradlew check` task has been configured to **skip** JavaScript browser tests (`jsBrowserTest`). This ensures the `check` task runs reliably for JVM and common code validation during regular development and CI.

Comprehensive testing of JavaScript components, including rendering and DOM interaction, requires running tests within a browser environment, which is handled by the `jsBrowserTest` task.

## Running Test Suites

Here are the commands to run the different test suites:

### Standard Checks (JVM & Common Tests - JS Skipped)

This is the recommended command for quick validation during development. It's fast and avoids JS runtime issues.

```bash
./gradlew clean check
```

### JavaScript Browser Tests Only

To run only the comprehensive JavaScript tests located in `src/jsTest`, which test DOM rendering and interaction:

```bash
./gradlew clean jsBrowserTest
```

You can filter these further if needed:

```bash
# Example: Run only Button JS tests
./gradlew jsBrowserTest --tests="**.*ButtonJsTest*"

# Example: Run all component JS tests (if named *Component*JsTest.kt)
./gradlew jsBrowserTest --tests="**.*Component*JsTest*"
```

Alternatively, use the provided convenience scripts (ensure they point to `jsBrowserTest` or a filtered version):

*   **Windows:** `.\run-js-tests.bat`
*   **Unix/Mac:** `./run-js-tests.sh`

### JVM Tests Only

To run only the tests located in `src/jvmTest`:

```bash
./gradlew clean jvmTest
```

### Full Check (JVM, Common, AND Comprehensive JS Tests)

To run the *entire* test suite, including the potentially slower and more environment-sensitive JavaScript browser tests:

```bash
./gradlew clean fullCheck
```

This `fullCheck` task depends on both the standard `check` tasks (excluding the skipped `jsBrowserTest`) and explicitly includes the `jsBrowserTest` task. Use this command when you need to verify all aspects of the library across all configured platforms.

## Summary

*   Use `./gradlew check` for standard development and CI checks (fast, reliable, skips JS).
*   Use `./gradlew jsBrowserTest` (or scripts/filters) to specifically run and debug comprehensive JS tests.
*   Use `./gradlew fullCheck` to run everything, including the comprehensive JS tests. 