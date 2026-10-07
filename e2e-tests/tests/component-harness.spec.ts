import { expect, test } from '@playwright/test';

test('real semantic harness drives renderer callbacks and DOM on JS and WASM', async ({ page }, testInfo) => {
  await page.goto('/', { waitUntil: 'commit' });
  await expect(page.getByTestId('harness-result')).toHaveText('PASS clicks=1 inputs=1 cycles=100');
  await expect(page.locator('[data-summon-test-tag="arabic"]')).toHaveText('مرحبا <&> "Summon"');
  await expect(page.locator('[data-summon-test-tag="count"]')).toHaveText('Count: 1');
  await expect(page.locator('[data-summon-test-tag="count"]')).toHaveAttribute('data-summon-test-state-value', '1');
  await expect(page.locator('[data-summon-test-tag="input"]')).toHaveValue('سلام');
  await expect(page.locator('[data-summon-test-tag="mirror"]')).toHaveText('سلام');
  await expect(page.locator('[data-summon-test-tag="hidden"]')).toBeHidden();
  await expect(page.locator('[data-summon-test-tag="disabled"]')).toBeDisabled();
  await expect(page.locator('[data-summon-test-tag="target"]')).toHaveCount(0);
  await expect(page.locator('[data-harness-cycle-root]')).toHaveCount(0);
  await page.evaluate(() => document.fonts.ready);
  await expect(page.locator('[data-summon-test-tag="app"]')).toHaveScreenshot(
    `component-harness-${testInfo.project.name}.png`,
    { animations: 'disabled', caret: 'hide', maxDiffPixels: 0, threshold: 0 },
  );
});

test('visual comparator rejects a controlled change or missing baseline', async ({ page }, testInfo) => {
  const visualCase = process.env.SUMMON_VISUAL_FAILURE_CASE;
  test.skip(visualCase !== 'changed' && visualCase !== 'missing', 'explicit negative visual verification only');
  await page.goto('/', { waitUntil: 'commit' });
  await expect(page.getByTestId('harness-result')).toHaveText('PASS clicks=1 inputs=1 cycles=100');
  await page.evaluate(() => document.fonts.ready);
  const component = page.locator('[data-summon-test-tag="app"]');
  if (visualCase === 'changed') {
    await component.evaluate((element) => {
      (element as HTMLElement).style.backgroundColor = 'rgb(255, 0, 0)';
    });
  }
  const snapshot = visualCase === 'missing'
    ? `missing-baseline-${testInfo.project.name}.png`
    : `component-harness-${testInfo.project.name}.png`;
  await expect(component).toHaveScreenshot(
    snapshot,
    { animations: 'disabled', caret: 'hide', maxDiffPixels: 0, threshold: 0 },
  );
});
