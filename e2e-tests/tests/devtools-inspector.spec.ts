import { expect, test } from '@playwright/test';
import fs from 'node:fs';
import path from 'node:path';

test.beforeEach(async ({ page }) => {
  await page.addInitScript(() => {
    const violations: string[] = [];
    Object.defineProperty(window, '__devtoolsCspViolations', { value: violations });
    window.addEventListener('securitypolicyviolation', event => {
      violations.push(`${event.violatedDirective}:${event.blockedURI}`);
    });
  });
  await page.goto('/');
  await expect(page.getByRole('dialog', { name: 'Summon component inspector' })).toBeVisible();
});

test.afterEach(async ({ page }) => {
  if (page.isClosed()) return;
  expect(await page.evaluate(() => (window as Window & { __devtoolsCspViolations?: string[] }).__devtoolsCspViolations ?? [])).toEqual([]);
});

test('actual renderer tree, isolated roots, highlighting and validated edits stay live', async ({ page }) => {
  const firstPanel = page.locator('[data-summon-inspector-panel]').first();
  await expect(firstPanel).toContainText('div[first-app]');
  await expect(firstPanel).toContainText('rootName: first');
  await expect(firstPanel).toContainText('credential: <redacted>');
  await expect(firstPanel).not.toContainText('second-app');
  await expect(firstPanel).not.toContainText('privateToken');
  await expect(page.getByTestId('redacted-getter-reads')).toHaveText('0');

  const countInput = firstPanel.getByRole('textbox', { name: 'Edit count' });
  await countInput.fill('7');
  await firstPanel.getByRole('button', { name: 'Apply', exact: true }).click();
  await expect(page.getByTestId('first-counter')).toHaveText('first count: 7');

  await countInput.fill('-1');
  await firstPanel.getByRole('button', { name: 'Apply', exact: true }).click();
  await expect(firstPanel.getByRole('status')).toContainText('nonnegative');
  await expect(page.getByTestId('first-counter')).toHaveText('first count: 7');
  await expect(firstPanel.locator('[data-summon-inspector-fields]').getByRole('textbox')).toHaveCount(1);

  await page.getByRole('button', { name: 'Reorder first children', exact: true }).click();
  const firstTree = firstPanel.locator('[data-summon-inspector-tree]');
  await expect.poll(async () => {
    const text = await firstTree.innerText();
    return text.indexOf('div[first-item-second]') < text.indexOf('div[first-item-first]');
  }).toBe(true);

  const rootItem = firstPanel.getByRole('treeitem', { name: /main/ });
  await rootItem.click();
  const highlight = page.locator('[data-summon-inspector-highlight]');
  await expect(highlight).toHaveCount(1);
  await expect(highlight).toHaveCSS('pointer-events', 'none');
  const rootBox = await page.locator('#root').boundingBox();
  const highlightBox = await highlight.boundingBox();
  expect(highlightBox).not.toBeNull();
  expect(rootBox).not.toBeNull();
  expect(Math.abs(highlightBox!.x - rootBox!.x)).toBeLessThanOrEqual(2);
  expect(Math.abs(highlightBox!.width - rootBox!.width)).toBeLessThanOrEqual(4);

  await rootItem.focus();
  await rootItem.press('ArrowRight');
  await rootItem.press('ArrowDown');
  await expect(firstPanel.getByRole('treeitem', { selected: true })).toContainText('first-app');

  await page.getByTestId('open-second-inspector').click();
  const panels = page.locator('[data-summon-inspector-panel]');
  await expect(panels).toHaveCount(2);
  const secondPanel = panels.nth(1);
  await expect(secondPanel).toContainText('div[second-app]');
  await expect(secondPanel).toContainText('rootName: second');
  await expect(secondPanel).toContainText('privateToken: <redacted>');
  await expect(secondPanel).not.toContainText('first-app');
  await expect(secondPanel).not.toContainText('credential');
});

test('timeline records public state, scrubs DOM, and keeps import inert until apply', async ({ page }) => {
  const panel = page.locator('[data-summon-inspector-panel]').first();
  await panel.getByRole('button', { name: 'Record', exact: true }).click();
  await page.getByRole('button', { name: 'Increment first counter', exact: true }).click();
  await expect(page.getByTestId('first-counter')).toHaveText('first count: 1');
  await expect(panel.locator('[data-summon-inspector-timeline-entries]')).toContainText('1 mutations');
  await expect(panel.getByRole('button', { name: 'Restore mutation 1' })).toBeVisible();

  await panel.getByRole('button', { name: 'Restore recording start' }).click();
  await expect(page.getByTestId('first-counter')).toHaveText('first count: 0');
  await expect(panel.locator('[data-summon-inspector-timeline-entries]')).toContainText('1 mutations');

  await panel.getByRole('button', { name: 'Restore mutation 1' }).click();
  await expect(page.getByTestId('first-counter')).toHaveText('first count: 1');
  await panel.getByRole('button', { name: 'Export session', exact: true }).click();
  const sessionJson = await panel.getByRole('textbox', { name: 'Debug session JSON' }).inputValue();
  expect(JSON.parse(sessionJson)).toMatchObject({ formatVersion: 1 });
  expect(sessionJson).not.toContain('<redacted>');
  expect(sessionJson).not.toContain('credential');

  await panel.getByRole('button', { name: 'Pause', exact: true }).click();
  await page.getByRole('button', { name: 'Increment first counter', exact: true }).click();
  await expect(page.getByTestId('first-counter')).toHaveText('first count: 2');
  await panel.getByRole('textbox', { name: 'Debug session JSON' }).fill(sessionJson);
  await panel.getByRole('button', { name: 'Validate import', exact: true }).click();
  await expect(panel.getByRole('status')).toContainText('application state is unchanged');
  await expect(page.getByTestId('first-counter')).toHaveText('first count: 2');

  await panel.getByRole('button', { name: 'Apply import', exact: true }).click();
  await expect(page.getByTestId('first-counter')).toHaveText('first count: 1');
  await panel.getByRole('button', { name: 'Clear timeline', exact: true }).click();
  await expect(panel.locator('[data-summon-inspector-timeline-entries]')).toContainText('0 mutations');
});

test('one hundred panel lifetimes release panels, styles, highlights and keyboard ownership', async ({ page }) => {
  for (let cycle = 0; cycle < 100; cycle += 1) {
    const panel = page.locator('[data-summon-inspector-panel]');
    await panel.getByRole('treeitem', { name: /main/ }).click();
    await expect(page.locator('[data-summon-inspector-highlight]')).toHaveCount(1);
    await panel.getByRole('button', { name: 'Close inspector', exact: true }).click();
    await expect(panel).toHaveCount(0);
    await expect(page.locator('[data-summon-inspector-highlight]')).toHaveCount(0);
    await expect(page.locator('[data-summon-inspector-style]')).toHaveCount(0);
    await page.getByTestId('open-first-inspector').click();
    await expect(page.locator('[data-summon-inspector-panel]')).toHaveCount(1);
  }
  await page.locator('[data-summon-inspector-panel]').press('Escape');
  await expect(page.locator('[data-summon-inspector-panel]')).toHaveCount(0);
  await page.keyboard.press('ArrowDown');
  await expect(page.locator('[data-summon-inspector-panel]')).toHaveCount(0);
});

test('production source-consumer bundles exclude the devtools artifact and overlay strings', async ({}, testInfo) => {
  test.skip(testInfo.project.name !== 'js-chromium', 'One byte-level production bundle inspection is sufficient');
  const distribution = path.resolve(__dirname, '../fixtures/private-suite/build/dist');
  const files: string[] = [];
  const visit = (directory: string) => {
    for (const entry of fs.readdirSync(directory, { withFileTypes: true })) {
      const target = path.join(directory, entry.name);
      if (entry.isDirectory()) visit(target);
      else files.push(target);
    }
  };
  visit(distribution);
  const forbidden = ['Summon Inspector', 'data-summon-inspector-panel', 'summon-devtools'];
  for (const file of files) {
    const contents = fs.readFileSync(file);
    for (const marker of forbidden) {
      expect(contents.includes(Buffer.from(marker)), `${marker} leaked into ${file}`).toBe(false);
    }
  }
});
