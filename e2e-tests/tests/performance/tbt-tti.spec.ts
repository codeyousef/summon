import { test, expect } from '@playwright/test';
import {
  setupTBTCollection,
  collectTBT,
  collectLongTasks,
  measureFrameRate,
  enablePerfMetrics,
} from '../../utils/cdp-metrics';
import { THRESHOLDS, checkMetric, printResults, MetricResult } from '../../utils/thresholds';

/**
 * Total Blocking Time and interactivity performance tests.
 *
 * These tests measure:
 * - TBT (Total Blocking Time) - sum of blocking portions of long tasks
 * - Long tasks that block the main thread
 * - Frame rate during interactions
 *
 * Run with: npx playwright test --config=playwright.perf.config.ts
 */
test.describe('Blocking Time & Interactivity', () => {
  const BASE_URL = process.env.BASE_URL || 'http://localhost:8080';

  test('TBT should be under threshold', async ({ page }) => {
    // Setup TBT collection BEFORE navigation
    await setupTBTCollection(page);
    await enablePerfMetrics(page);

    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    // Wait for page to fully settle
    await page.waitForTimeout(2000);

    const tbt = await collectTBT(page);
    console.log(`\n[TBT] Total Blocking Time: ${tbt?.toFixed(2) ?? 'N/A'}ms`);

    expect(tbt).not.toBeNull();
    expect(tbt!).toBeLessThanOrEqual(THRESHOLDS.TBT_MAX_MS);
  });

  test('long tasks should be minimal', async ({ page }) => {
    await setupTBTCollection(page);
    await enablePerfMetrics(page);

    await page.goto(BASE_URL, { waitUntil: 'networkidle' });
    await page.waitForTimeout(2000);

    const longTasks = await collectLongTasks(page);

    console.log(`\n[Long Tasks] Count: ${longTasks.length}`);
    if (longTasks.length > 0) {
      console.log('[Long Tasks] Details:');
      longTasks.forEach((task, i) => {
        console.log(`  ${i + 1}. ${task.name}: ${task.duration.toFixed(2)}ms (blocking: ${task.blockingTime.toFixed(2)}ms)`);
      });
    }

    // Calculate total blocking time from long tasks
    const totalBlocking = longTasks.reduce((sum, t) => sum + t.blockingTime, 0);
    console.log(`[Long Tasks] Total blocking time: ${totalBlocking.toFixed(2)}ms`);

    expect(totalBlocking).toBeLessThanOrEqual(THRESHOLDS.TBT_MAX_MS);
  });

  test('frame rate should be acceptable during idle', async ({ page }) => {
    await enablePerfMetrics(page);
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    // Measure frame rate during idle
    const frameMetrics = await measureFrameRate(
      page,
      async () => {
        // No action - just measure idle frame rate
      },
      1000
    );

    console.log(`\n[Frame Rate] Average FPS: ${frameMetrics.averageFps.toFixed(1)}`);
    console.log(`[Frame Rate] Min FPS: ${frameMetrics.minFps.toFixed(1)}`);
    console.log(`[Frame Rate] Frame count: ${frameMetrics.frameCount}`);

    // During idle, we should maintain good frame rate
    expect(frameMetrics.averageFps).toBeGreaterThanOrEqual(THRESHOLDS.FPS_MIN);
  });

  test('frame rate should be acceptable during scroll', async ({ page }) => {
    await enablePerfMetrics(page);
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    // Measure frame rate during scroll
    const frameMetrics = await measureFrameRate(
      page,
      async () => {
        // Perform scrolling
        for (let i = 0; i < 5; i++) {
          await page.evaluate(() => window.scrollBy(0, 100));
          await page.waitForTimeout(50);
        }
        for (let i = 0; i < 5; i++) {
          await page.evaluate(() => window.scrollBy(0, -100));
          await page.waitForTimeout(50);
        }
      },
      1500
    );

    console.log(`\n[Frame Rate - Scroll] Average FPS: ${frameMetrics.averageFps.toFixed(1)}`);
    console.log(`[Frame Rate - Scroll] Min FPS: ${frameMetrics.minFps.toFixed(1)}`);
    console.log(`[Frame Rate - Scroll] Frame count: ${frameMetrics.frameCount}`);

    // During scroll, we should still maintain acceptable frame rate
    expect(frameMetrics.averageFps).toBeGreaterThanOrEqual(THRESHOLDS.FPS_MIN);
  });

  test('frame rate should be acceptable during interaction', async ({ page }) => {
    await enablePerfMetrics(page);
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });

    // Measure frame rate during button clicks
    const frameMetrics = await measureFrameRate(
      page,
      async () => {
        const buttons = await page.$$('button');
        for (const button of buttons.slice(0, 3)) {
          try {
            await button.click({ timeout: 500 });
          } catch {
            // Ignore click errors
          }
          await page.waitForTimeout(100);
        }
      },
      2000
    );

    console.log(`\n[Frame Rate - Interaction] Average FPS: ${frameMetrics.averageFps.toFixed(1)}`);
    console.log(`[Frame Rate - Interaction] Min FPS: ${frameMetrics.minFps.toFixed(1)}`);
    console.log(`[Frame Rate - Interaction] Frame count: ${frameMetrics.frameCount}`);

    // During interaction, we should maintain acceptable frame rate
    expect(frameMetrics.averageFps).toBeGreaterThanOrEqual(THRESHOLDS.FPS_MIN);
  });

  test('main thread should not be blocked during hydration', async ({ page }) => {
    await setupTBTCollection(page);
    await enablePerfMetrics(page);

    const navigationStart = Date.now();
    await page.goto(BASE_URL, { waitUntil: 'networkidle' });
    const navigationEnd = Date.now();

    // Get long tasks that occurred during page load
    const longTasks = await collectLongTasks(page);

    // Filter tasks that occurred during navigation
    const loadTimeTasks = longTasks.filter(
      task => task.startTime < (navigationEnd - navigationStart + 1000)
    );

    console.log(`\n[Main Thread] Navigation time: ${navigationEnd - navigationStart}ms`);
    console.log(`[Main Thread] Long tasks during load: ${loadTimeTasks.length}`);

    // Calculate if main thread work is excessive
    const totalMainThreadWork = loadTimeTasks.reduce((sum, t) => sum + t.duration, 0);
    console.log(`[Main Thread] Total work: ${totalMainThreadWork.toFixed(2)}ms`);

    expect(totalMainThreadWork).toBeLessThanOrEqual(THRESHOLDS.MAIN_THREAD_MAX_MS);
  });

  test('comprehensive blocking time report', async ({ page }) => {
    await setupTBTCollection(page);
    await enablePerfMetrics(page);

    await page.goto(BASE_URL, { waitUntil: 'networkidle' });
    await page.waitForTimeout(2000);

    const tbt = await collectTBT(page);
    const longTasks = await collectLongTasks(page);

    const frameMetrics = await measureFrameRate(
      page,
      async () => {
        await page.evaluate(() => window.scrollBy(0, 200));
      },
      500
    );

    const results: MetricResult[] = [];

    if (tbt !== null) {
      results.push(checkMetric('TBT', tbt, THRESHOLDS.TBT_MAX_MS, 'ms'));
    }

    results.push(
      checkMetric('Avg FPS', frameMetrics.averageFps, THRESHOLDS.FPS_MIN, 'fps', false)
    );

    const totalMainThread = longTasks.reduce((sum, t) => sum + t.duration, 0);
    results.push(
      checkMetric('Main Thread Work', totalMainThread, THRESHOLDS.MAIN_THREAD_MAX_MS, 'ms')
    );

    console.log('\n[Long Tasks Summary]');
    console.log(`  Count: ${longTasks.length}`);
    console.log(`  Total duration: ${longTasks.reduce((s, t) => s + t.duration, 0).toFixed(2)}ms`);
    console.log(`  Total blocking: ${longTasks.reduce((s, t) => s + t.blockingTime, 0).toFixed(2)}ms`);

    printResults(results);

    const allPassed = results.every(r => r.passed);
    expect(allPassed).toBe(true);
  });
});
