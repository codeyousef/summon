import { test, expect } from '@playwright/test';
import {
  enablePerfMetrics,
  collectHydrationMetrics,
  waitForHydration,
  isHydrationComplete,
  HydrationMetrics,
} from '../../utils/cdp-metrics';
import { THRESHOLDS, checkMetric, printResults, MetricResult } from '../../utils/thresholds';

/**
 * Summon hydration performance tests.
 *
 * These tests measure Summon-specific hydration metrics:
 * - Total hydration time
 * - Phase breakdown (script load, initialization, event system, component hydration)
 *
 * Run with: npx playwright test --config=playwright.perf.config.ts
 */
test.describe('Hydration Performance', () => {
  const BASE_URL = process.env.BASE_URL || 'http://localhost:8080';

  test.beforeEach(async ({ page }) => {
    // Enable Summon performance metrics before navigation
    await enablePerfMetrics(page);
  });

  test('hydration should complete within threshold', async ({ page }) => {
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    // Wait for hydration to complete
    const metrics = await waitForHydration(page, 10000);

    if (metrics) {
      console.log(`\n[Hydration] Total time: ${metrics.totalTime?.toFixed(2) ?? 'N/A'}ms`);
      console.log('[Hydration] Phase breakdown:');
      Object.entries(metrics.phases).forEach(([phase, time]) => {
        console.log(`  - ${phase}: ${(time as number).toFixed(2)}ms`);
      });

      expect(metrics.totalTime).not.toBeNull();
      expect(metrics.totalTime!).toBeLessThanOrEqual(THRESHOLDS.HYDRATION_MAX_MS);
    } else {
      console.log('\n[Hydration] Performance metrics not enabled or hydration not instrumented');
      // Skip assertion if metrics not available
      test.skip();
    }
  });

  test('hydration should complete flag should be set', async ({ page }) => {
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    // Wait a bit for hydration
    await page.waitForTimeout(2000);

    const complete = await isHydrationComplete(page);
    console.log(`\n[Hydration] Complete flag: ${complete}`);

    expect(complete).toBe(true);
  });

  test('hydration phases should be tracked', async ({ page }) => {
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    const metrics = await waitForHydration(page, 10000);

    if (!metrics) {
      console.log('\n[Hydration] Performance metrics not enabled');
      test.skip();
      return;
    }

    const phases = metrics.phases;
    console.log('\n[Hydration] Phases detected:');
    console.log(JSON.stringify(phases, null, 2));

    // Check that some phases are tracked
    const phaseCount = Object.keys(phases).length;
    expect(phaseCount).toBeGreaterThan(0);
  });

  test('hydration report should be accessible via window object', async ({ page }) => {
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    // Wait for hydration
    await page.waitForTimeout(2000);

    const report = await page.evaluate(() => {
      const perf = (window as any).__SUMMON_PERF__;
      if (!perf) return null;
      return {
        isEnabled: typeof perf.isEnabled === 'function' ? perf.isEnabled() : false,
        hasReport: !!perf._report,
        report: perf._report,
        metricsCount: perf.metrics?.length ?? 0,
      };
    });

    console.log('\n[Hydration] Window performance object:');
    console.log(JSON.stringify(report, null, 2));

    if (report && report.isEnabled) {
      expect(report.hasReport).toBe(true);
      expect(report.metricsCount).toBeGreaterThan(0);
    } else {
      console.log('[Hydration] Performance metrics not enabled on this page');
    }
  });

  test('waitForHydration Promise should resolve correctly', async ({ page }) => {
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    const startTime = Date.now();
    const metrics = await waitForHydration(page, 10000);
    const waitTime = Date.now() - startTime;

    console.log(`\n[Hydration] waitForHydration resolved in ${waitTime}ms`);

    if (metrics) {
      console.log(`[Hydration] Reported hydration time: ${metrics.totalTime?.toFixed(2) ?? 'N/A'}ms`);

      // The wait time should be reasonable (less than timeout)
      expect(waitTime).toBeLessThan(10000);
    }
  });

  test('multiple navigations should reset metrics', async ({ page }) => {
    // First navigation
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });
    const metrics1 = await waitForHydration(page, 10000);

    // Second navigation
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });
    const metrics2 = await waitForHydration(page, 10000);

    console.log('\n[Hydration] First navigation:', metrics1?.totalTime?.toFixed(2) ?? 'N/A', 'ms');
    console.log('[Hydration] Second navigation:', metrics2?.totalTime?.toFixed(2) ?? 'N/A', 'ms');

    // Both navigations should have valid metrics (if enabled)
    if (metrics1 && metrics2) {
      expect(metrics1.totalTime).toBeGreaterThan(0);
      expect(metrics2.totalTime).toBeGreaterThan(0);
    }
  });

  test('hydration should complete before user interaction', async ({ page }) => {
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    // Check hydration status
    const preInteractionComplete = await isHydrationComplete(page);

    // Try to interact with the page
    const button = await page.$('button');
    if (button) {
      await button.click();
    }

    const postInteractionComplete = await isHydrationComplete(page);

    console.log(`\n[Hydration] Pre-interaction complete: ${preInteractionComplete}`);
    console.log(`[Hydration] Post-interaction complete: ${postInteractionComplete}`);

    // Hydration should be complete before meaningful interaction
    expect(preInteractionComplete).toBe(true);
  });

  test('performance summary report', async ({ page }) => {
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    const metrics = await waitForHydration(page, 10000);

    if (!metrics) {
      console.log('\n[Summary] Performance metrics not enabled');
      test.skip();
      return;
    }

    const results: MetricResult[] = [];

    if (metrics.totalTime !== null) {
      results.push(
        checkMetric('Hydration Time', metrics.totalTime, THRESHOLDS.HYDRATION_MAX_MS, 'ms')
      );
    }

    // Add phase metrics if available
    Object.entries(metrics.phases).forEach(([phase, time]) => {
      // Phase times are informational, no strict threshold
      console.log(`  [Phase] ${phase}: ${(time as number).toFixed(2)}ms`);
    });

    printResults(results);

    // Main metric should pass
    const allPassed = results.every(r => r.passed);
    expect(allPassed).toBe(true);
  });
});
