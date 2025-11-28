import { test, expect } from '@playwright/test';
import {
  collectFCP,
  collectLCP,
  collectCLS,
  collectCoreWebVitals,
  enablePerfMetrics,
} from '../../utils/cdp-metrics';
import { THRESHOLDS, checkMetric, printResults, MetricResult } from '../../utils/thresholds';

/**
 * Core Web Vitals performance tests.
 *
 * These tests measure:
 * - FCP (First Contentful Paint)
 * - LCP (Largest Contentful Paint)
 * - CLS (Cumulative Layout Shift)
 *
 * Run with: npx playwright test --config=playwright.perf.config.ts
 */
test.describe('Core Web Vitals', () => {
  const BASE_URL = process.env.BASE_URL || 'http://localhost:8080';

  test.beforeEach(async ({ page }) => {
    // Enable Summon performance metrics before navigation
    await enablePerfMetrics(page);
  });

  test('FCP should be under threshold', async ({ page }) => {
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    const fcp = await collectFCP(page);
    console.log(`\n[FCP] First Contentful Paint: ${fcp?.toFixed(2) ?? 'N/A'}ms`);

    expect(fcp).not.toBeNull();
    expect(fcp!).toBeLessThanOrEqual(THRESHOLDS.FCP_MAX_MS);
  });

  test('LCP should be under threshold', async ({ page }) => {
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    // Wait for page to settle before measuring LCP
    await page.waitForTimeout(500);

    const lcp = await collectLCP(page);
    console.log(`\n[LCP] Largest Contentful Paint: ${lcp?.toFixed(2) ?? 'N/A'}ms`);

    expect(lcp).not.toBeNull();
    expect(lcp!).toBeLessThanOrEqual(THRESHOLDS.LCP_MAX_MS);
  });

  test('CLS should be under threshold', async ({ page }) => {
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    // Wait for page to stabilize and measure layout shifts
    const cls = await collectCLS(page);
    console.log(`\n[CLS] Cumulative Layout Shift: ${cls?.toFixed(4) ?? 'N/A'}`);

    expect(cls).not.toBeNull();
    expect(cls!).toBeLessThanOrEqual(THRESHOLDS.CLS_MAX);
  });

  test('all Core Web Vitals should pass', async ({ page }) => {
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    // Wait for measurements to settle
    await page.waitForTimeout(1000);

    const vitals = await collectCoreWebVitals(page);

    const results: MetricResult[] = [];

    if (vitals.fcp !== null) {
      results.push(checkMetric('FCP', vitals.fcp, THRESHOLDS.FCP_MAX_MS, 'ms'));
    }

    if (vitals.lcp !== null) {
      results.push(checkMetric('LCP', vitals.lcp, THRESHOLDS.LCP_MAX_MS, 'ms'));
    }

    if (vitals.cls !== null) {
      results.push(checkMetric('CLS', vitals.cls, THRESHOLDS.CLS_MAX, ''));
    }

    printResults(results);

    // All metrics should pass
    const allPassed = results.every(r => r.passed);
    expect(allPassed).toBe(true);
  });

  test('should track CLS during scroll', async ({ page }) => {
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    // Get initial CLS
    const initialCLS = await collectCLS(page);

    // Simulate scrolling which might trigger layout shifts
    await page.evaluate(() => {
      window.scrollTo(0, document.body.scrollHeight);
    });

    await page.waitForTimeout(500);

    await page.evaluate(() => {
      window.scrollTo(0, 0);
    });

    await page.waitForTimeout(500);

    // Get CLS after scroll
    const finalCLS = await collectCLS(page);

    console.log(`\n[CLS] Initial: ${initialCLS?.toFixed(4) ?? 'N/A'}`);
    console.log(`[CLS] After scroll: ${finalCLS?.toFixed(4) ?? 'N/A'}`);

    // CLS should still be under threshold after scrolling
    expect(finalCLS).not.toBeNull();
    expect(finalCLS!).toBeLessThanOrEqual(THRESHOLDS.CLS_MAX);
  });
});
