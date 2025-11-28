import { Page, CDPSession } from '@playwright/test';

/**
 * Core Web Vitals metrics.
 */
export interface CoreWebVitals {
  fcp: number | null; // First Contentful Paint
  lcp: number | null; // Largest Contentful Paint
  cls: number | null; // Cumulative Layout Shift
  tbt: number | null; // Total Blocking Time
}

/**
 * Summon hydration metrics.
 */
export interface HydrationMetrics {
  totalTime: number | null;
  phases: Record<string, number>;
}

/**
 * Frame rate metrics.
 */
export interface FrameMetrics {
  averageFps: number;
  minFps: number;
  frameCount: number;
}

/**
 * Collect First Contentful Paint from Performance API.
 */
export async function collectFCP(page: Page): Promise<number | null> {
  return await page.evaluate(() => {
    const entries = performance.getEntriesByType('paint');
    const fcp = entries.find(e => e.name === 'first-contentful-paint');
    return fcp ? fcp.startTime : null;
  });
}

/**
 * Collect Largest Contentful Paint.
 * Must be called after LCP has been observed (typically after page load).
 */
export async function collectLCP(page: Page): Promise<number | null> {
  return await page.evaluate(() => {
    return new Promise<number | null>((resolve) => {
      let lcpValue: number | null = null;

      // LCP is only available via PerformanceObserver
      const observer = new PerformanceObserver((list) => {
        const entries = list.getEntries();
        const lastEntry = entries[entries.length - 1];
        lcpValue = lastEntry ? lastEntry.startTime : null;
      });

      observer.observe({ type: 'largest-contentful-paint', buffered: true });

      // Wait a bit for LCP to settle, then return
      setTimeout(() => {
        observer.disconnect();
        resolve(lcpValue);
      }, 500);
    });
  });
}

/**
 * Collect Cumulative Layout Shift.
 */
export async function collectCLS(page: Page): Promise<number | null> {
  return await page.evaluate(() => {
    return new Promise<number | null>((resolve) => {
      let clsValue = 0;

      const observer = new PerformanceObserver((list) => {
        for (const entry of list.getEntries()) {
          // Only count shifts without recent input
          if (!(entry as any).hadRecentInput) {
            clsValue += (entry as any).value;
          }
        }
      });

      observer.observe({ type: 'layout-shift', buffered: true });

      // Wait for page to stabilize
      setTimeout(() => {
        observer.disconnect();
        resolve(clsValue);
      }, 1000);
    });
  });
}

/**
 * Collect Total Blocking Time.
 * This must be set up BEFORE navigation using addInitScript.
 */
export async function setupTBTCollection(page: Page): Promise<void> {
  await page.addInitScript(() => {
    (window as any).__SUMMON_TBT__ = 0;
    (window as any).__SUMMON_LONG_TASKS__ = [];

    const observer = new PerformanceObserver((list) => {
      for (const entry of list.getEntries()) {
        // TBT = sum of (duration - 50ms) for all long tasks > 50ms
        const blockingTime = entry.duration - 50;
        if (blockingTime > 0) {
          (window as any).__SUMMON_TBT__ += blockingTime;
          (window as any).__SUMMON_LONG_TASKS__.push({
            name: entry.name,
            duration: entry.duration,
            blockingTime: blockingTime,
            startTime: entry.startTime,
          });
        }
      }
    });

    observer.observe({ type: 'longtask', buffered: true });
  });
}

/**
 * Get collected TBT value.
 */
export async function collectTBT(page: Page): Promise<number | null> {
  return await page.evaluate(() => {
    return (window as any).__SUMMON_TBT__ ?? null;
  });
}

/**
 * Get long tasks details.
 */
export async function collectLongTasks(page: Page): Promise<any[]> {
  return await page.evaluate(() => {
    return (window as any).__SUMMON_LONG_TASKS__ ?? [];
  });
}

/**
 * Collect Summon hydration metrics.
 */
export async function collectHydrationMetrics(page: Page): Promise<HydrationMetrics | null> {
  return await page.evaluate(() => {
    const perf = (window as any).__SUMMON_PERF__;
    if (!perf || !perf._report) {
      return null;
    }
    return {
      totalTime: perf._report.totalHydrationTime,
      phases: perf._report.phases || {},
    };
  });
}

/**
 * Wait for Summon hydration to complete.
 */
export async function waitForHydration(page: Page, timeoutMs: number = 10000): Promise<HydrationMetrics | null> {
  return await page.evaluate(async (timeout) => {
    const perf = (window as any).__SUMMON_PERF__;
    if (!perf) {
      // Performance metrics not enabled
      return null;
    }

    try {
      const report = await perf.waitForHydration(timeout);
      if (!report) return null;
      // Transform to match HydrationMetrics interface
      return {
        totalTime: report.totalHydrationTime,
        phases: report.phases || {},
      };
    } catch (e) {
      return null;
    }
  }, timeoutMs);
}

/**
 * Check if hydration is complete.
 */
export async function isHydrationComplete(page: Page): Promise<boolean> {
  return await page.evaluate(() => {
    return (window as any).__SUMMON_HYDRATION_ACTIVE__ === true;
  });
}

/**
 * Enable Summon performance metrics before navigation.
 */
export async function enablePerfMetrics(page: Page): Promise<void> {
  await page.addInitScript(() => {
    (window as any).__SUMMON_PERF_ENABLED__ = true;
  });
}

/**
 * Collect all Core Web Vitals.
 */
export async function collectCoreWebVitals(page: Page): Promise<CoreWebVitals> {
  const [fcp, lcp, cls, tbt] = await Promise.all([
    collectFCP(page),
    collectLCP(page),
    collectCLS(page),
    collectTBT(page),
  ]);

  return { fcp, lcp, cls, tbt };
}

/**
 * Measure frame rate during an action.
 */
export async function measureFrameRate(
  page: Page,
  action: () => Promise<void>,
  durationMs: number = 1000
): Promise<FrameMetrics> {
  // Start frame rate measurement
  await page.evaluate(() => {
    (window as any).__SUMMON_FRAME_TIMES__ = [];
    (window as any).__SUMMON_FRAME_MEASURING__ = true;

    let lastTime = performance.now();
    function measureFrame() {
      if (!(window as any).__SUMMON_FRAME_MEASURING__) return;

      const now = performance.now();
      const delta = now - lastTime;
      (window as any).__SUMMON_FRAME_TIMES__.push(delta);
      lastTime = now;
      requestAnimationFrame(measureFrame);
    }
    requestAnimationFrame(measureFrame);
  });

  // Perform the action
  await action();

  // Wait for measurement duration
  await page.waitForTimeout(durationMs);

  // Stop measurement and collect results
  return await page.evaluate(() => {
    (window as any).__SUMMON_FRAME_MEASURING__ = false;
    const frameTimes: number[] = (window as any).__SUMMON_FRAME_TIMES__ || [];

    if (frameTimes.length < 2) {
      return { averageFps: 0, minFps: 0, frameCount: 0 };
    }

    const fps = frameTimes.map(delta => delta > 0 ? 1000 / delta : 0);
    const avgFps = fps.reduce((a, b) => a + b, 0) / fps.length;
    const minFps = Math.min(...fps);

    return {
      averageFps: avgFps,
      minFps: minFps,
      frameCount: frameTimes.length,
    };
  });
}
