# Hydration Test Example

This example is specifically designed to test client-side hydration functionality across all targets.

## Components Tested

- **HamburgerMenu**: Tests the client-side toggle visibility functionality
- **Button with Counter**: Tests basic state management and onClick handlers

## Running Locally

```bash
# From the project root
./gradlew :examples:hydration-test:jsBrowserDevelopmentRun

# Or for production build
./gradlew :examples:hydration-test:jsBrowserProductionRun
```

## Running E2E Tests

```bash
# From the e2e-tests directory
cd e2e-tests
npm install
npx playwright test tests/hydration.spec.ts
```

## Testing the Hamburger Menu

The hamburger menu is responsive and only shows on mobile-sized screens. To test:

1. Open the app in a browser
2. Use DevTools to set viewport to mobile (e.g., 375x667)
3. Click the hamburger icon (☰)
4. Menu content should toggle visibility

Or run the automated E2E tests which use mobile viewport.
