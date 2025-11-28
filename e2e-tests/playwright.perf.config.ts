import { defineConfig, devices } from '@playwright/test';

/**
 * Performance testing configuration for Playwright.
 *
 * Key differences from standard config:
 * - Single worker to avoid resource contention
 * - No retries - performance tests should be deterministic
 * - No trace - reduces measurement overhead
 * - Longer timeout for performance measurements
 */
export default defineConfig({
  testDir: './tests/performance',
  fullyParallel: false, // Serial execution for consistent measurements
  forbidOnly: !!process.env.CI,
  retries: 0, // No retries for performance tests
  workers: 1, // Single worker to avoid resource contention
  timeout: 60000, // Longer timeout for performance tests
  reporter: [['list']], // Console output only
  use: {
    trace: 'off', // Disable tracing to reduce overhead
    headless: true,
    launchOptions: {
      args: [
        '--disable-background-timer-throttling',
        '--disable-backgrounding-occluded-windows',
        '--disable-renderer-backgrounding',
      ],
    },
  },
  projects: [
    {
      name: 'chromium-perf',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
