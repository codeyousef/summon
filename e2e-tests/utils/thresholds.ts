/**
 * Performance threshold constants for Summon hydration benchmarks.
 *
 * These thresholds are based on:
 * - Web Vitals recommendations
 * - Lighthouse performance scoring
 * - Summon hydration optimization spec targets
 */
export const THRESHOLDS = {
  // Core Web Vitals thresholds (in milliseconds unless noted)

  /**
   * Total Blocking Time - sum of blocking portions of long tasks.
   * Target: <300ms (from spec, originally ~27,000ms)
   */
  TBT_MAX_MS: 300,

  /**
   * Time to Interactive - when page becomes reliably interactive.
   * Target: <3.5s (from spec)
   */
  TTI_MAX_MS: 3500,

  /**
   * First Contentful Paint - time to first content render.
   * Good: <1.8s, Needs Improvement: 1.8-3s, Poor: >3s
   */
  FCP_MAX_MS: 1800,

  /**
   * Largest Contentful Paint - time to largest element render.
   * Good: <2.5s, Needs Improvement: 2.5-4s, Poor: >4s
   */
  LCP_MAX_MS: 2500,

  /**
   * Cumulative Layout Shift - visual stability score.
   * Good: <0.1, Needs Improvement: 0.1-0.25, Poor: >0.25
   */
  CLS_MAX: 0.1,

  // Summon-specific thresholds

  /**
   * Total hydration time (from script load to hydration complete).
   * Target: <1000ms
   */
  HYDRATION_MAX_MS: 1000,

  /**
   * Minimum acceptable frame rate during interactions.
   * Target: 50+ fps (from spec)
   */
  FPS_MIN: 50,

  /**
   * Maximum main thread work time.
   * Target: <4s (from spec)
   */
  MAIN_THREAD_MAX_MS: 4000,
};

/**
 * Metric result with pass/fail status.
 */
export interface MetricResult {
  name: string;
  value: number;
  threshold: number;
  unit: string;
  passed: boolean;
}

/**
 * Check if a metric passes its threshold.
 */
export function checkMetric(
  name: string,
  value: number,
  threshold: number,
  unit: string = 'ms',
  isMaxThreshold: boolean = true
): MetricResult {
  const passed = isMaxThreshold ? value <= threshold : value >= threshold;
  return { name, value, threshold, unit, passed };
}

/**
 * Print metric result to console.
 */
export function printMetric(result: MetricResult): void {
  const status = result.passed ? '✓' : '✗';
  const operator = result.unit === 'fps' ? '>=' : '<=';
  console.log(
    `  ${status} ${result.name}: ${result.value.toFixed(2)}${result.unit} (${operator} ${result.threshold}${result.unit})`
  );
}

/**
 * Print all metric results.
 */
export function printResults(results: MetricResult[]): void {
  console.log('\n=== Performance Results ===');
  results.forEach(printMetric);
  const passed = results.filter(r => r.passed).length;
  console.log(`\n${passed}/${results.length} metrics passed\n`);
}
