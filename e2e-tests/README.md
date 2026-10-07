# E2E Tests for Summon Framework

This directory contains end-to-end tests for the Summon framework using Playwright.

## Setup

```bash
# Install dependencies
npm install

# Install browser binaries
npm run install:browsers
```

## Running Tests

### Quick Start (Manual)

1. Generate and start a test project:
```bash
# Generate a JS project using the CLI
summon new test-app --template js
cd test-app
./gradlew jsBrowserDevelopmentRun
```

2. In a new terminal, run the tests:
```bash
npm run test:smoke
```

### Automated E2E Runner

This runs all configured projects with their test suites:
```bash
npm run test:all
```

### Private-suite browser qualification

Build the standalone source consumer before running the matrix:

```bash
./gradlew -p fixtures/private-suite jsBrowserDistribution wasmJsBrowserDistribution
./run-private-suite-container.sh
```

The container run is pinned to Playwright 1.56.1 and an image digest, uses no
container network, one worker, no retries, and independent JS/WASM projects for
Chromium and Firefox. Production also runs automated WebKit; WebKit is not evidence of physical Safari.
The command fails if the image or browser binaries are unavailable; it does not
convert missing dependencies into a skipped success.

Development and production bundles are separate evidence levels. Build the
development executables, then select that profile explicitly:

```bash
./gradlew -p fixtures/private-suite jsBrowserDevelopmentExecutableDistribution wasmJsBrowserDevelopmentExecutableDistribution
SUMMON_BROWSER_PROFILE=development ./run-private-suite-container.sh
```

Each run writes a source/image/lock manifest, browser versions, Playwright JSON,
container cgroup high-water metrics, the line log, and failure traces under
`.gradle/private-suite/browser-artifacts/matrix[-development]/`. These artifacts
contain synthetic fixture data only. Kotlin/JS and Kotlin/Wasm browser test tasks
are also runnable; absent local browser dependencies fail rather than reporting a
disabled task as successful. Node tests remain the fast simulated-DOM tier.

## Test Suites

| Test File                | Description                                             |
|--------------------------|---------------------------------------------------------|
| `hydration.spec.ts`      | Comprehensive hydration tests (desktop, mobile, tablet) |
| `hamburger-menu.spec.ts` | Focused hamburger menu toggle tests                     |
| `smoke.spec.ts`          | Basic smoke tests for CLI-generated projects            |

## Available npm Scripts

| Command | Description |
|---------|-------------|
| `npm test` | Run all tests |
| `npm run test:hydration` | Run hydration tests (requires server on port 8080) |
| `npm run test:hamburger` | Run hamburger menu tests (requires server on port 8080) |
| `npm run test:smoke` | Run smoke tests (requires server on port 8080) |
| `npm run test:all` | Automated runner that starts servers and runs tests |
| `npm run test:report` | Open Playwright HTML report |
| `npm run test:private-suite:container` | Run the pinned production JS/WASM browser matrix |
| `npm run test:private-suite:development` | Run the pinned development JS/WASM browser matrix |
| `npm run install:browsers` | Install Playwright browsers |

## Test Configuration

Tests are configured in `playwright.config.ts`:
- **Browser**: Chromium
- **Headless**: Yes (can be changed for debugging)
- **Reporter**: HTML report

### Mobile Viewport Tests

Hamburger menu tests use mobile viewport (375x667) since the hamburger menu is only visible on mobile-sized screens.

## Debugging Failed Tests

1. View the HTML report:
```bash
npm run test:report
```

2. Run tests with visible browser:
```bash
npx playwright test --headed
```

3. Run tests with step-through debugging:
```bash
npx playwright test --debug
```

## Example Project Structure

The tests are designed to work with CLI-generated projects. Use the Summon CLI to generate test applications:

```bash
summon new my-test-app --template hydration
```

## CI Integration

For CI environments:
```bash
# Install dependencies
npm ci
npx playwright install --with-deps chromium

# Run tests
npm run test:all
```
