import { test, expect } from '@playwright/test';

test.beforeEach(async ({ page }) => {
  await page.goto('/');
  await expect(page.getByTestId('fixture-title')).toHaveText('Summon source consumer');
});

test('one callback per click and one rendered counter after recomposition', async ({ page }) => {
  for (let count = 1; count <= 20; count++) {
    await page.getByRole('button', { name: 'Increment', exact: true }).click();
    await expect(page.getByTestId('counter')).toHaveText(`Count: ${count}`);
    await expect(page.getByTestId('counter')).toHaveCount(1);
    await expect(page.getByRole('button', { name: 'Increment', exact: true })).toHaveCount(1);
  }
});

test('controlled flow input updates without losing value', async ({ page }) => {
  const input = page.getByTestId('controlled-input');
  await input.fill('Synthetic edited value');
  await expect(input).toHaveValue('Synthetic edited value');
  await expect(page.getByTestId('account-value')).toHaveText('Synthetic edited value');
  await expect(input).toHaveCount(1);
  await expect(input).toBeFocused();
  await input.press('End');
  await input.press('!');
  await expect(input).toHaveValue('Synthetic edited value!');
  await expect(page.getByTestId('account-value')).toHaveText('Synthetic edited value!');
  await expect(input).toBeFocused();
});

test('logout removes account UI and late flow results cannot repaint it', async ({ page }) => {
  await page.getByRole('button', { name: 'Logout', exact: true }).click();
  await expect(page.getByTestId('locked')).toHaveText('Locked');
  await expect(page.getByTestId('controlled-input')).toHaveCount(0);
  await expect(page.getByTestId('account-value')).toHaveCount(0);
  await page.getByRole('button', { name: 'Emit late result', exact: true }).click();
  await page.getByRole('button', { name: 'Increment', exact: true }).click();
  await expect(page.getByTestId('counter')).toHaveText('Count: 1');
  await expect(page.getByText('Late account A result', { exact: true })).toHaveCount(0);
  await expect(page.getByTestId('account-value')).toHaveCount(0);
});

test('keyed reorder preserves each node identity and follows the requested order', async ({ page }) => {
  const original = await page.getByTestId('key-item-one').elementHandle();
  expect(original).not.toBeNull();
  for (let step = 0; step < 10; step++) {
    await page.getByRole('button', { name: 'Reverse items', exact: true }).click();
    const expected = step % 2 === 0 ? ['Item three', 'Item two', 'Item one'] : ['Item one', 'Item two', 'Item three'];
    await expect(page.locator('[data-testid^="key-item-"]')).toHaveText(expected);
    expect(await original!.evaluate(node => node === document.querySelector('[data-testid="key-item-one"]'))).toBe(true);
  }
});


test('owned effects release on key change, conditional removal and logout', async ({ page }) => {
  await expect(page.getByTestId('active-effects')).toHaveText('Active effects: 1');
  let disposed = 0;
  for (let step = 0; step < 10; step++) {
    await page.getByRole('button', { name: 'Change effect key', exact: true }).click();
    await expect(page.getByTestId('disposed-effects')).toHaveText(`Disposed effects: ${++disposed}`);
    await expect(page.getByTestId('active-effects')).toHaveText('Active effects: 1');
    await page.getByRole('button', { name: 'Toggle owned effect', exact: true }).click();
    await expect(page.getByTestId('active-effects')).toHaveText('Active effects: 0');
    await expect(page.getByTestId('disposed-effects')).toHaveText(`Disposed effects: ${++disposed}`);
    await page.getByRole('button', { name: 'Toggle owned effect', exact: true }).click();
    await expect(page.getByTestId('active-effects')).toHaveText('Active effects: 1');
  }
  await page.getByRole('button', { name: 'Logout', exact: true }).click();
  await expect(page.getByTestId('active-effects')).toHaveText('Active effects: 0');
  await expect(page.getByTestId('disposed-effects')).toHaveText(`Disposed effects: ${++disposed}`);
  await page.getByRole('button', { name: 'Change effect key', exact: true }).click();
  await expect(page.getByTestId('disposed-effects')).toHaveText(`Disposed effects: ${disposed}`);
});


test('JS microtask scheduling preserves reentrant owned effect updates', async ({ page }, testInfo) => {
  test.skip(testInfo.project.name.startsWith('wasm-'), 'MicrotaskScheduler is a JS-specific API');
  await page.goto('/?scheduler=microtask');
  await expect(page.getByTestId('active-effects')).toHaveText('Active effects: 1');
  await page.getByRole('button', { name: 'Change effect key', exact: true }).click();
  await expect(page.getByTestId('disposed-effects')).toHaveText('Disposed effects: 1');
  await page.getByRole('button', { name: 'Logout', exact: true }).click();
  await expect(page.getByTestId('active-effects')).toHaveText('Active effects: 0');
  await page.getByRole('button', { name: 'Increment', exact: true }).click();
  await expect(page.getByTestId('counter')).toHaveText('Count: 1');
});
