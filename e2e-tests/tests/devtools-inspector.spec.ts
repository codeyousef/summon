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

test('error overlay maps Kotlin frames, keeps hostile values inert, and releases listeners', async ({ page }, testInfo) => {
  const hostileRequests: string[] = [];
  page.on('request', request => {
    if (request.url().startsWith('https://attacker.invalid/')) hostileRequests.push(request.url());
  });

  const overlay = page.locator('[data-summon-error-overlay]');
  await expect(overlay).toBeAttached();
  await expect(page.getByTestId('error-overlay-install-failure')).toHaveCount(0);
  await expect(overlay).toBeHidden();
  await page.getByTestId('report-mapped-error').click();
  await expect(overlay).toBeVisible();

  const boundaryEntry = overlay.locator('[data-summon-error-entry=\"ERROR_BOUNDARY\"]');
  await expect(boundaryEntry).toHaveCount(1);
  await expect(boundaryEntry).toContainText('Public failure <img src=x onerror=alert(1)>');
  await expect(boundaryEntry.locator('img')).toHaveCount(0);
  if (testInfo.project.name.startsWith('js-')) {
    const sourceFile = path.resolve(
      __dirname,
      '../fixtures/devtools/src/webMain/kotlin/codes/yousef/summon/devtoolsfixture/ErrorOverlayFixture.kt',
    );
    const boundaryLine = fs.readFileSync(sourceFile, 'utf8').split('\n')
      .findIndex(line => line.trim() === 'mappedFixtureFailure()') + 1;
    const fixtureLocation = boundaryEntry
      .locator('[data-summon-error-location][data-mapped="true"]')
      .filter({ hasText: 'src/webMain/kotlin/codes/yousef/summon/devtoolsfixture/ErrorOverlayFixture.kt' })
      .filter({ hasText: `:${boundaryLine}:` });
    await expect(fixtureLocation).toHaveCount(1);
    await expect(fixtureLocation).toBeVisible();
    const editorHref = await fixtureLocation.getByRole('link', { name: 'Editor' }).getAttribute('href');
    expect(editorHref).toContain('vscode://file/workspace/summon/src/webMain/kotlin/');
    expect(editorHref).toContain(`ErrorOverlayFixture.kt:${boundaryLine}:`);
    const viewerHref = await fixtureLocation.getByRole('link', { name: 'Verified source' }).getAttribute('href');
    expect(viewerHref).toContain('https://source.example/source/devtools-fixture/src/webMain/kotlin/');
    expect(viewerHref).toContain(`ErrorOverlayFixture.kt#L${boundaryLine}:C`);
  } else {
    await expect(boundaryEntry.locator('[data-summon-error-location]').first()).toBeVisible();
  }

  const scriptEventPreserved = await page.evaluate(() => {
    const dispatch = () => window.dispatchEvent(new ErrorEvent('error', {
      filename: `${window.location.origin}/fixture.js`,
      lineno: 1,
      colno: 1,
      message: 'private script body',
      cancelable: true,
    }));
    return [dispatch(), dispatch()];
  });
  expect(scriptEventPreserved).toEqual([true, true]);
  const scriptEntry = overlay.locator('[data-summon-error-entry="SCRIPT_ERROR"]');
  await expect(scriptEntry).toHaveCount(1);
  await expect(scriptEntry).toContainText('Unhandled script error');
  await expect(scriptEntry).not.toContainText('private script body');

  const rejectionObservation = await page.evaluate(() => {
    let reads = 0;
    const event = new Event('unhandledrejection', { cancelable: true });
    Object.defineProperty(event, 'reason', {
      get() {
        reads += 1;
        return { privateToken: 'must-not-be-read' };
      },
    });
    const preserved = window.dispatchEvent(event);
    return { reads, preserved };
  });
  expect(rejectionObservation).toEqual({ reads: 0, preserved: true });
  await expect(overlay.locator('[data-summon-error-entry=\"UNHANDLED_REJECTION\"]')).toHaveCount(1);
  await expect(overlay).not.toContainText('must-not-be-read');

  await page.getByTestId('report-hostile-error').dispatchEvent('click');
  const hostileEntry = overlay.locator('[data-summon-error-entry="SCRIPT_ERROR"]').filter({ hasText: 'Hostile frame' });
  await expect(hostileEntry).toContainText('Generated location 7:9 (source unavailable)');
  await expect(hostileEntry.locator('a')).toHaveCount(0);
  expect(hostileRequests).toEqual([]);

  await overlay.getByRole('button', { name: 'Close development error overlay' }).click();
  await expect(overlay).toHaveCount(0);
  await expect(page.locator('[data-summon-error-overlay-style]')).toHaveCount(0);
  await page.evaluate(() => {
    window.dispatchEvent(new Event('error'));
    window.dispatchEvent(new Event('unhandledrejection'));
  });
  await expect(overlay).toHaveCount(0);
});

test('production source-consumer bundles exclude developer and test artifacts', async ({}, testInfo) => {
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
  const forbidden = [
    'Summon Inspector',
    'data-summon-inspector-panel',
    'Summon development errors',
    'data-summon-error-overlay',
    'source.example',
    'devtools-fixture',
    'summon-devtools',
    'summon-semantic-snapshot:v1',
    'summon-test',
  ];
  for (const file of files) {
    const contents = fs.readFileSync(file);
    for (const marker of forbidden) {
      expect(contents.includes(Buffer.from(marker)), `${marker} leaked into ${file}`).toBe(false);
    }
  }
});
