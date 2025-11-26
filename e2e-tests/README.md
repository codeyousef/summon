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

1. Start the hydration-test example server:
```bash
cd .. && ./gradlew :examples:hydration-test:jsBrowserDevelopmentRun
```

2. In a new terminal, run the tests:
```bash
npm run test:hydration
```

### Automated E2E Runner

This runs all configured projects with their test suites:
```bash
npm run test:all
```

## Test Suites

| Test File | Description |
|-----------|-------------|
| `hydration.spec.ts` | Comprehensive hydration tests (desktop, mobile, tablet) |
| `hamburger-menu.spec.ts` | Focused hamburger menu toggle tests |
| `smoke.spec.ts` | Basic smoke tests for hello-world examples |

## Available npm Scripts

| Command | Description |
|---------|-------------|
| `npm test` | Run all tests |
| `npm run test:hydration` | Run hydration tests (requires server on port 8080) |
| `npm run test:hamburger` | Run hamburger menu tests (requires server on port 8080) |
| `npm run test:smoke` | Run smoke tests (requires server on port 8080) |
| `npm run test:all` | Automated runner that starts servers and runs tests |
| `npm run test:report` | Open Playwright HTML report |
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

The tests are designed to work with these example apps:

- `examples/hydration-test/` - Comprehensive test app with HamburgerMenu, Button, etc.
- `examples/hello-world-js/` - Basic JS example
- `examples/hello-world-wasm/` - Basic WASM example (when configured)

## CI Integration

For CI environments:
```bash
# Install dependencies
npm ci
npx playwright install --with-deps chromium

# Run tests
npm run test:all
```
