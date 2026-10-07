import { expect, test } from '@playwright/test';

test('real semantic harness drives renderer callbacks and DOM on JS and WASM', async ({ page }) => {
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
});
