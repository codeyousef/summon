import { test, expect } from '@playwright/test';
import type { Page } from '@playwright/test';

type ResizeProbe = {
  adds: number;
  removes: number;
  active: EventListenerOrEventListenerObject[];
  retired: EventListenerOrEventListenerObject[];
  writes: WeakMap<Element, number>;
};

type ResizeProbeWindow = Window & { __summonResizeProbe: ResizeProbe };

type ListenerProbe = {
  adds: number;
  removes: number;
  active: Array<{
    target: EventTarget;
    type: string;
    listener: EventListenerOrEventListenerObject;
  }>;
};

type ListenerProbeWindow = Window & { __summonListenerProbe: ListenerProbe };

type LifecycleListenerProbe = {
  adds: number;
  removes: number;
  active: Array<{ type: string; listener: EventListenerOrEventListenerObject }>;
  retired: Array<{ type: string; listener: EventListenerOrEventListenerObject }>;
};

type LifecycleProbeWindow = Window & { __summonLifecycleProbe: LifecycleListenerProbe };

type CspProbeWindow = Window & {
  __summonCspViolations: Array<{ directive: string; blockedURI: string }>;
};

async function installElementListenerProbe(page: Page) {
  await page.addInitScript(() => {
    const nativeAdd = EventTarget.prototype.addEventListener;
    const nativeRemove = EventTarget.prototype.removeEventListener;
    const probe: ListenerProbe = { adds: 0, removes: 0, active: [] };
    (window as ListenerProbeWindow).__summonListenerProbe = probe;

    EventTarget.prototype.addEventListener = function(
      type: string,
      listener: EventListenerOrEventListenerObject | null,
      options?: boolean | AddEventListenerOptions
    ) {
      if (this instanceof Element && listener != null) {
        probe.adds++;
        probe.active.push({ target: this, type, listener });
      }
      return nativeAdd.call(this, type, listener, options);
    };
    EventTarget.prototype.removeEventListener = function(
      type: string,
      listener: EventListenerOrEventListenerObject | null,
      options?: boolean | EventListenerOptions
    ) {
      if (this instanceof Element && listener != null) {
        const index = probe.active.findIndex(
          entry => entry.target === this && entry.type === type && entry.listener === listener
        );
        if (index >= 0) {
          probe.active.splice(index, 1);
          probe.removes++;
        }
      }
      return nativeRemove.call(this, type, listener, options);
    };
  });
}

test.beforeEach(async ({ page }) => {
  await page.addInitScript(() => {
    const violations: Array<{ directive: string; blockedURI: string }> = [];
    (window as CspProbeWindow).__summonCspViolations = violations;
    window.addEventListener('securitypolicyviolation', event => {
      violations.push({
        directive: event.effectiveDirective,
        blockedURI: event.blockedURI,
      });
    });
  });
  await page.goto('/');
  await expect(page.getByTestId('fixture-title')).toHaveText('Summon source consumer', { timeout: 15_000 });
});

test.afterEach(async ({ page }) => {
  if (page.isClosed()) return;
  expect(await page.evaluate(() => (window as CspProbeWindow).__summonCspViolations ?? [])).toEqual([]);
});

test('production fixture sends strict first-party CSP', async ({ page }) => {
  const response = await page.goto('/');
  expect(response).not.toBeNull();
  const policy = response!.headers()['content-security-policy'];
  expect(policy).toContain("default-src 'none'");
  expect(policy).toContain("script-src 'self' 'wasm-unsafe-eval'");
  expect(policy).toContain("script-src-attr 'none'");
  expect(policy).toContain("object-src 'none'");
  expect(policy).toContain("base-uri 'none'");
  expect(policy).toContain("frame-ancestors 'none'");
  expect(policy).toContain("form-action 'self'");
  expect(policy).not.toContain("'unsafe-eval'");
  await page.getByRole('button', { name: 'Increment', exact: true }).click();
  await expect(page.getByTestId('counter')).toHaveText('Count: 1');
});

test('bounded transport aborts, preserves safe status metadata, and owns live signals', async ({ page }) => {
  await page.goto('/?transport=true');
  await expect(page.getByTestId('transport-title')).toHaveText('Transport qualification');
  await page.getByRole('button', { name: 'Run transport probes', exact: true }).click();
  const result = page.getByTestId('transport-result');
  await expect(result).toContainText('\"transport\":\"ok\"');
  await expect(result).toContainText('409:status-409:request-transport-01:2000');
  await expect(result).toContainText('429:status-429:request-transport-01:2000');
  await expect(result).toContainText('503:status-503:request-transport-01:2000');
  await expect(result).toContainText('timeout');
  await expect(result).toContainText('oversize');
  await expect(result).toContainText('\"operation\":\"synthetic-operation-01\"');
  await expect(result).toContainText('\"csrf\":\"synthetic-csrf\"');
  await expect(result).toContainText('redirect-guarded');
  await expect(result).not.toContainText('must-not-appear-in-error');

  await expect.poll(async () => {
    const response = await page.request.get('/transport/metrics');
    return Number((await response.json()).slow_aborts);
  }).toBeGreaterThan(0);
  const metricsResponse = await page.request.get('/transport/metrics');
  expect((await metricsResponse.json()).cross_origin_hits).toBe(0);

  await page.getByRole('button', { name: 'Start signals', exact: true }).click();
  const signals = page.getByTestId('signal-result');
  await expect.poll(async () => {
    const text = await signals.textContent();
    return Number(/connections=(\d+)/.exec(text ?? '')?.[1] ?? 0);
  }).toBeGreaterThanOrEqual(2);
  await expect.poll(async () => {
    const text = await signals.textContent();
    return Number(/hints=(\d+)/.exec(text ?? '')?.[1] ?? 0);
  }).toBeGreaterThan(1);

  await page.getByRole('button', { name: 'Dispose transport', exact: true }).click();
  await expect(signals).toHaveText('disposed');
  await page.waitForTimeout(2_200);
  await expect(signals).toHaveText('disposed');
});

test('opaque browser persistence is atomic, bounded, coordinated, and worker-owned', async ({ page }, testInfo) => {
  await page.goto('/?persistence=true');
  await expect(page.getByTestId('persistence-title')).toHaveText('Browser persistence qualification');
  await page.getByRole('button', { name: 'Run persistence probes', exact: true }).click();
  await expect(page.getByTestId('persistence-status')).toHaveText(
    'ready:atomic=true;bounded=true;worker=true',
    { timeout: 15_000 },
  );
  await page.evaluate(() => {
    const storePrototype = IDBObjectStore.prototype as any;
    const nativePut = storePrototype.put;
    storePrototype.put = function(value: unknown, key?: IDBValidKey) {
      if (key === 'quota-a') {
        storePrototype.put = nativePut;
        throw new DOMException('', 'QuotaExceededError');
      }
      return nativePut.call(this, value, key);
    };
    const databasePrototype = IDBDatabase.prototype as any;
    const nativeTransaction = databasePrototype.transaction;
    databasePrototype.transaction = function(storeNames: string | string[], mode?: IDBTransactionMode) {
      if (mode === 'readonly') {
        databasePrototype.transaction = nativeTransaction;
        throw new DOMException('', 'InvalidStateError');
      }
      return nativeTransaction.call(this, storeNames, mode);
    };
  });
  await page.getByRole('button', { name: 'Probe visible storage failures', exact: true }).click();
  await expect(page.getByTestId('persistence-status')).toHaveText('errors:quota=true;evicted=true');


  const second = await page.context().newPage();
  const wasmTarget = testInfo.project.name.startsWith('wasm-');
  if (wasmTarget) {
    await second.goto('/blank');
    await second.evaluate(() => {
      (window as unknown as { __suiteChannel: BroadcastChannel }).__suiteChannel =
        new BroadcastChannel('summon-opaque-suite-fixture');
    });
  } else {
    await second.goto('/?persistence=true');
    await expect(second.getByTestId('persistence-title')).toHaveText('Browser persistence qualification');
    await second.getByRole('button', { name: 'Run persistence probes', exact: true }).click();
    await expect(second.getByTestId('persistence-status')).toHaveText(
      'ready:atomic=true;bounded=true;worker=true',
      { timeout: 15_000 },
    );
  }

  await page.evaluate(async () => {
    const database = await new Promise<IDBDatabase>((resolve, reject) => {
      const request = indexedDB.open('summon-suite-fixture', 1);
      request.onerror = () => reject(request.error);
      request.onsuccess = () => resolve(request.result);
    });
    database.onversionchange = () => undefined;
    (window as unknown as { __blockingDatabase: IDBDatabase }).__blockingDatabase = database;
  });

  const upgradePage = wasmTarget ? page : second;
  await upgradePage.getByRole('button', { name: 'Upgrade persistence schema', exact: true }).click();
  await expect(upgradePage.getByTestId('persistence-status')).toHaveText('upgrade-blocked');
  await expect(upgradePage.getByTestId('persistence-migration')).toHaveText('blocked:1->2');
  await page.evaluate(async () => {
    (window as unknown as { __blockingDatabase: IDBDatabase }).__blockingDatabase.close();
    await new Promise<void>((resolve, reject) => {
      const request = indexedDB.open('summon-suite-fixture', 2);
      request.onerror = () => reject(request.error);
      request.onsuccess = () => {
        request.result.close();
        resolve();
      };
    });
  });
  await upgradePage.getByRole('button', { name: 'Upgrade persistence schema', exact: true }).click();
  await expect(upgradePage.getByTestId('persistence-status')).toHaveText('upgraded');
  await expect(upgradePage.getByTestId('persistence-migration')).toHaveText('ready:2');

  await page.getByRole('button', { name: 'Start delayed worker', exact: true }).click();
  await expect(page.getByTestId('persistence-status')).toHaveText('worker-pending');
  if (wasmTarget) {
    await second.evaluate(() => {
      (window as unknown as { __suiteChannel: BroadcastChannel }).__suiteChannel.postMessage('LOGOUT:lock-1');
    });
  } else {
    await second.getByRole('button', { name: 'Broadcast logout', exact: true }).click();
    await expect(second.getByTestId('persistence-status')).toContainText('locked:lock-');
  }
  await expect(page.getByTestId('persistence-status')).toContainText('locked:lock-');
  await page.waitForTimeout(1_000);
  await expect(page.getByTestId('persistence-status')).toContainText('locked:lock-');

  const persisted = await page.evaluate(async () => {
    const values = await new Promise<unknown[]>((resolve, reject) => {
      const request = indexedDB.open('summon-suite-fixture', 2);
      request.onerror = () => reject(request.error);
      request.onsuccess = () => {
        const database = request.result;
        const transaction = database.transaction('opaque-records', 'readonly');
        const all = transaction.objectStore('opaque-records').getAll();
        all.onerror = () => reject(all.error);
        all.onsuccess = () => {
          database.close();
          resolve(all.result);
        };
      };
    });
    const bytes = values.map(value => Array.from(new Uint8Array(value as ArrayBufferLike)));
    const local = Array.from({ length: localStorage.length }, (_, index) => [
      localStorage.key(index),
      localStorage.getItem(localStorage.key(index)!),
    ]);
    const session = Array.from({ length: sessionStorage.length }, (_, index) => [
      sessionStorage.key(index),
      sessionStorage.getItem(sessionStorage.key(index)!),
    ]);
    return { bytes, local, session, cacheNames: await caches.keys() };
  });
  expect(persisted.bytes).toEqual(expect.arrayContaining([[145, 2, 167, 68], [194, 51, 23], [229, 97, 8]]));
  expect(JSON.stringify(persisted)).not.toContain('Synthetic secret marker');
  expect(persisted.local).toEqual([]);
  expect(persisted.session).toEqual([]);
  expect(persisted.cacheNames).toEqual([]);
  await second.close();
});

test('hydration state closing-script text remains inert', async ({ page }, testInfo) => {
  test.skip(testInfo.project.name.startsWith('wasm-'), 'JS hydration client owns public-state parsing');
  await page.goto('/?hydrationAdversarial=true');
  await expect(page.locator('#root')).toHaveText('Public shell: adversarial state remains inert');
  expect(await page.evaluate(() => (window as unknown as { __summonXss: number }).__summonXss)).toBe(0);
  expect(await page.evaluate(
    () => (window as unknown as { __SUMMON_STATE__: { label: string } }).__SUMMON_STATE__.label
  )).toBe('</script><script>globalThis.__summonXss=1</script>');
  await expect(page.locator('script:not([type=\"application/json\"])')).toHaveCount(1);
});

test('buttons, virtualization and dialogs remain interactive under strict CSP', async ({ page }) => {
  const origins = new Set<string>();
  page.on('request', request => origins.add(new URL(request.url()).origin));
  await page.goto('/?csp=true');
  await expect(page.getByTestId('csp-title')).toHaveText('Strict CSP interactions');

  await page.getByRole('button', { name: 'Open CSP dialog', exact: true }).click();
  await expect(page.getByTestId('csp-dialog-content')).toHaveText('CSP dialog content');
  await page.getByRole('button', { name: 'Close CSP dialog', exact: true }).click();
  await expect(page.getByTestId('csp-dialog-content')).toHaveCount(0);

  await page.getByTestId('csp-lazy-list').evaluate(element => {
    element.scrollTop = 150;
    element.dispatchEvent(new Event('scroll'));
  });
  await expect(page.getByTestId('csp-scroll-position')).not.toHaveText('Scroll: 0');
  expect([...origins]).toEqual([new URL(page.url()).origin]);
});

test('real virtualization stays bounded, keyed, measured, accessible, and disposable', async ({ page }) => {
  const origins = new Set<string>();
  page.on('request', request => origins.add(new URL(request.url()).origin));
  await page.goto('/?virtualization=true');
  await expect(page.getByTestId('virtual-title')).toHaveText('Virtualization fixture');

  const list = page.getByTestId('virtual-list');
  const mountedRows = list.locator('[data-lazy-item=\"true\"]');
  await expect.poll(() => mountedRows.count()).toBeLessThanOrEqual(36);
  await expect(page.getByTestId('virtual-state-loading')).toHaveText('loading');
  await expect(page.getByTestId('virtual-state-empty')).toHaveText('empty');
  await expect(page.getByTestId('virtual-state-locked')).toHaveText('locked');
  await expect(page.getByTestId('virtual-state-permission-denied')).toHaveText('permission-denied');
  await expect(page.getByTestId('virtual-state-error')).toHaveText('error');

  const virtualLabels = await mountedRows.evaluateAll(rows =>
    rows.slice(0, 6).map(row => row.textContent?.trim())
  );
  const referenceLabels = await page.getByTestId('virtual-reference').locator('[role=\"listitem\"]').evaluateAll(rows =>
    rows.map(row => row.textContent?.trim())
  );
  expect(virtualLabels).toEqual(referenceLabels);
  await expect(mountedRows.first()).toHaveAttribute('aria-posinset', '1');
  await expect(mountedRows.first()).toHaveAttribute('aria-setsize', '100000');
  expect(await list.evaluate(element => element.outerHTML.includes('PrivateFixtureKey'))).toBe(false);

  const scrollTo = async (position: number) => {
    await list.evaluate(async (element, top) => {
      element.scrollTop = top;
      element.dispatchEvent(new Event('scroll'));
      await new Promise<void>(resolve => requestAnimationFrame(() => requestAnimationFrame(() => resolve())));
    }, position);
    await expect.poll(() => mountedRows.count()).toBeLessThanOrEqual(36);
  };

  await scrollTo(2_000_000);
  await expect(page.getByTestId('virtual-row-50000')).toBeVisible();
  await page.getByTestId('virtual-row-50000').click();
  await expect(page.getByTestId('virtual-selected')).toHaveText('Selected: 50000');

  await page.getByRole('button', { name: 'Insert before selection', exact: true }).click();
  await expect(page.getByTestId('virtual-selected')).toHaveText('Selected: 50000');
  await expect.poll(() => page.getByTestId('virtual-row-50000').evaluate(
    element => element.closest('[data-lazy-item=\"true\"]')?.getAttribute('data-item-index')
  )).toBe('50001');

  await page.getByRole('button', { name: 'Remove inserted item', exact: true }).click();
  await expect.poll(() => page.getByTestId('virtual-row-50000').evaluate(
    element => element.closest('[data-lazy-item=\"true\"]')?.getAttribute('data-item-index')
  )).toBe('50000');

  await page.getByTestId('virtual-row-50000').focus();
  await page.keyboard.press('Enter');
  await expect(page.getByTestId('virtual-opened')).toHaveText('Opened: 50000');
  await page.keyboard.press('Tab');
  await expect(page.getByTestId('virtual-row-50001')).toBeFocused();

  await scrollTo(39_880);
  await expect(page.getByTestId('virtual-row-997')).toBeVisible();
  await expect.poll(() => page.getByTestId('virtual-row-997').evaluate(
    element => element.closest('[data-lazy-item=\"true\"]')?.getBoundingClientRect().height
  )).toBeGreaterThanOrEqual(80);
  await list.evaluate(element => {
    (element as HTMLElement).style.height = '320px';
    (element as HTMLElement).style.zoom = '1.25';
  });
  await expect.poll(() => mountedRows.count()).toBeLessThanOrEqual(34);
  await expect(page.getByTestId('virtual-row-997')).toBeVisible();
  await list.evaluate(element => {
    element.style.height = '600px';
    element.style.zoom = '1';
  });
  await page.evaluate(async () => {
    await new Promise<void>(resolve => requestAnimationFrame(() => requestAnimationFrame(() => resolve())));
  });
  await list.evaluate(element => {
    element.scrollTop = element.scrollHeight;
    element.dispatchEvent(new Event('scroll'));
  });
  await expect(page.getByTestId('virtual-row-99999')).toBeVisible();
  await expect.poll(() => mountedRows.count()).toBeLessThanOrEqual(36);
  await page.evaluate(async () => {
    await new Promise<void>(resolve => requestAnimationFrame(() => requestAnimationFrame(() => resolve())));
  });
  await list.evaluate(element => {
    element.scrollTop = 0;
    element.dispatchEvent(new Event('scroll'));
  });
  await expect(page.getByTestId('virtual-row-0')).toBeVisible();

  await page.getByRole('button', { name: 'Snapshot provider reads', exact: true }).click();
  await expect.poll(async () => Number((await page.getByTestId('virtual-reads').textContent())?.replace('Reads: ', '')))
    .toBeLessThan(300);

  await page.getByRole('button', { name: 'Hide virtual list', exact: true }).click();
  await expect(page.getByTestId('virtual-list')).toHaveCount(0);
  await expect(page.locator('[data-lazy-item=\"true\"]')).toHaveCount(0);
  expect([...origins]).toEqual([new URL(page.url()).origin]);
});

test('native files stay bounded, resumable, cancelable, integrity-gated, and revocable', async ({ page }) => {
  await page.goto('/?files=true');
  await expect(page.getByTestId('file-title')).toHaveText('File lifecycle fixture');

  const input = page.locator('input[type="file"]');
  await input.setInputFiles({
    name: 'first.bin',
    mimeType: 'video/mp4',
    buffer: Buffer.from([0, 1, 2, 3, 4, 5, 6, 7, 8, 9]),
  });
  await expect(page.getByText('first.bin', { exact: true })).toBeVisible();
  await expect(page.getByText('Selected', { exact: true })).toBeVisible();

  await page.getByRole('button', { name: 'Read edge ranges', exact: true }).click();
  await expect(page.getByTestId('file-result')).toHaveText('Ranges: 0:8,9');

  await page.getByRole('button', { name: 'Create verified media', exact: true }).click();
  await expect(page.getByTestId('file-result')).toHaveText('Media ready');
  const media = page.getByTestId('verified-media');
  await expect(media).toHaveCount(1);
  const objectUrl = await media.locator('source').getAttribute('src');
  expect(objectUrl).toMatch(/^blob:/);

  await page.getByRole('button', { name: 'Lock media', exact: true }).click();
  await expect(media).toHaveCount(0);
  expect(await page.evaluate(async url => {
    return await new Promise<boolean>(resolve => {
      const probe = document.createElement('video');
      probe.onerror = () => resolve(true);
      probe.onloadedmetadata = () => resolve(false);
      probe.src = url!;
      probe.load();
    });
  }, objectUrl)).toBe(true);

  await page.getByRole('button', { name: 'Fail media integrity', exact: true }).click();
  await expect(page.getByTestId('file-result')).toHaveText('Integrity failed');
  await expect(media).toHaveCount(0);

  await page.getByRole('button', { name: 'Try unsupported media', exact: true }).click();
  await expect(page.getByTestId('file-result')).toHaveText('Unsupported media: video/x-private');
  await expect(media).toHaveCount(0);

  const largeBytes = Buffer.alloc(8 * 1024 * 1024, 0x5a);
  await input.setInputFiles({ name: 'large.bin', mimeType: 'application/octet-stream', buffer: largeBytes });
  await page.getByRole('button', { name: 'Transfer selected', exact: true }).click();
  await expect(page.getByText(`0 of ${largeBytes.length} bytes`, { exact: true })).toBeVisible();
  await page.getByRole('button', { name: 'Cancel transfer', exact: true }).click();
  await expect(page.getByTestId('file-result')).toHaveText('Transfer canceled');
  await expect(page.getByRole('button', { name: 'Retry large.bin', exact: true })).toBeVisible();

  await page.getByRole('button', { name: 'Retry large.bin', exact: true }).click();
  await expect(page.getByRole('group', { name: 'large.bin', exact: true }).getByText('Selected', { exact: true })).toBeVisible();
  await page.getByRole('button', { name: 'Transfer selected', exact: true }).click();
  await expect(page.getByTestId('file-result')).toHaveText(`Transfer complete: ${largeBytes.length}`);

  await input.setInputFiles({
    name: 'large.bin',
    mimeType: 'application/octet-stream',
    buffer: Buffer.alloc(9, 0x5a),
  });
  await page.getByRole('button', { name: 'Resume selected', exact: true }).click();
  await expect(page.getByTestId('file-result')).toHaveText('New version required');
  await expect(page.getByText('Source changed; select the new version', { exact: true })).toBeVisible();
  await expect(page.getByText('large.bin', { exact: true })).toHaveCount(2);
});

test('hydration mismatch reloads once and retains only the public shell', async ({ page }, testInfo) => {
  test.skip(testInfo.project.name.startsWith('wasm-'), 'Mismatch recovery is owned by the JS hydration client');
  let mismatchNavigations = 0;
  page.on('framenavigated', frame => {
    if (frame === page.mainFrame() && frame.url().includes('hydrationMismatch=true')) mismatchNavigations++;
  });
  await page.goto('/?hydrationMismatch=true');
  await expect(page.locator('#root')).toHaveText('Public shell: sign in to unlock');
  await expect.poll(() => page.evaluate(
    () => window.sessionStorage.getItem('summon-hydration-recovery')
  )).toBe('/?hydrationMismatch=true');
  await expect.poll(() => mismatchNavigations).toBe(2);
  await expect(page.getByTestId('account-value')).toHaveCount(0);
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

test('event wrappers use the latest model and remain stable through 100 route swaps', async ({ page }) => {
  await installElementListenerProbe(page);
  await page.goto('/?identity=true');
  await expect(page.getByTestId('identity-title')).toHaveText('Stable rendering fixture');
  await expect(page.getByTestId('route-effect-stats')).toHaveText('Active: 1; Disposed: 0');

  const callbackButton = await page.getByRole('button', { name: 'Invoke current callback', exact: true }).elementHandle();
  const initialProbe = await page.evaluate(() => {
    const probe = (window as ListenerProbeWindow).__summonListenerProbe;
    return { adds: probe.adds, removes: probe.removes, active: probe.active.length };
  });
  const initialNodeCount = await page.locator('#root *').count();

  await page.getByRole('button', { name: 'Change callback model', exact: true }).dispatchEvent('click');
  expect(await callbackButton!.evaluate(node => node === document.querySelector('[data-summon-handler-click][key=\"invoke-current-callback\"]'))).toBe(true);
  await callbackButton!.evaluate(node => (node as HTMLElement).click());
  await expect(page.getByTestId('callback-result')).toHaveText('model-B');

  const swapRoute = page.getByRole('button', { name: 'Swap route', exact: true });
  for (let swap = 0; swap < 100; swap++) {
    await swapRoute.dispatchEvent('click');
    await expect(page.getByTestId('identity-route')).toHaveText(`Route ${swap % 2 === 0 ? 'B' : 'A'}`);
  }
  await expect(page.locator('#root *')).toHaveCount(initialNodeCount);
  await expect(page.getByTestId('private-route-content')).toHaveText('Private route A');
  await expect(page.getByTestId('private-route-content')).toHaveCount(1);
  await expect(page.getByTestId('route-effect-stats')).toHaveText('Active: 1; Disposed: 100');

  const finalProbe = await page.evaluate(() => {
    const probe = (window as ListenerProbeWindow).__summonListenerProbe;
    return { adds: probe.adds, removes: probe.removes, active: probe.active.length };
  });
  expect(finalProbe).toEqual(initialProbe);
});

test('stale asynchronous render results cannot replace a newer route model', async ({ page }) => {
  await page.goto('/?identity=true');
  await expect(page.getByTestId('request-result')).toHaveText('Unavailable');
  await page.getByRole('button', { name: 'Start old request', exact: true }).click();
  await page.getByRole('button', { name: 'Swap route', exact: true }).click();
  await expect(page.getByTestId('identity-route')).toHaveText('Route B');
  await page.getByRole('button', { name: 'Start new request', exact: true }).click();
  await page.getByRole('button', { name: 'Complete old request', exact: true }).click();
  await expect(page.getByTestId('request-result')).toHaveText('Unavailable');
  await page.getByRole('button', { name: 'Complete new request', exact: true }).click();
  await expect(page.getByTestId('request-result')).toHaveText('new B');
  await page.getByRole('button', { name: 'Fail new request', exact: true }).click();
  await expect(page.getByTestId('request-result')).toHaveText('Unavailable');
});

test('keyed reorder retains focused selection and deleted callbacks are detached', async ({ page }) => {
  await installElementListenerProbe(page);
  await page.goto('/?identity=true');
  const input = page.getByTestId('identity-input-two');
  const originalInput = await input.elementHandle();
  const retiredButton = await page.getByTestId('identity-button-two').elementHandle();
  await input.focus();
  await input.evaluate((node: HTMLInputElement) => node.setSelectionRange(1, 2));
  await page.getByRole('button', { name: 'Reverse identity items', exact: true }).dispatchEvent('click');
  expect(await originalInput!.evaluate(node => node === document.querySelector('[data-testid=\"identity-input-two\"]'))).toBe(true);
  await expect(input).toBeFocused();
  expect(await input.evaluate((node: HTMLInputElement) => [node.selectionStart, node.selectionEnd])).toEqual([1, 2]);

  const removalsBeforeDelete = await page.evaluate(
    () => (window as ListenerProbeWindow).__summonListenerProbe.removes
  );
  await page.getByRole('button', { name: 'Delete identity item two', exact: true }).dispatchEvent('click');
  await expect(page.getByTestId('identity-input-two')).toHaveCount(0);
  await expect(page.getByTestId('identity-button-two')).toHaveCount(0);
  await retiredButton!.evaluate(node => (node as HTMLElement).click());
  await expect(page.getByTestId('item-callbacks')).toHaveText('Item callbacks: 0');
  const removalsAfterDelete = await page.evaluate(
    () => (window as ListenerProbeWindow).__summonListenerProbe.removes
  );
  expect(removalsAfterDelete).toBeGreaterThanOrEqual(removalsBeforeDelete + 2);
});

test('lifecycle work pauses, resumes and destroys without host listener leaks', async ({ page }) => {
  await page.addInitScript(() => {
    const nativeAdd = window.addEventListener.bind(window);
    const nativeRemove = window.removeEventListener.bind(window);
    const trackedTypes: Record<string, true> = {
      visibilitychange: true,
      pagehide: true,
      beforeunload: true,
    };
    const probe: LifecycleListenerProbe = { adds: 0, removes: 0, active: [], retired: [] };
    (window as LifecycleProbeWindow).__summonLifecycleProbe = probe;
    Object.defineProperty(window, 'addEventListener', {
      configurable: true,
      value(type: string, listener: EventListenerOrEventListenerObject, options?: boolean | AddEventListenerOptions) {
        if (trackedTypes[type]) {
          probe.adds++;
          probe.active.push({ type, listener });
        }
        return nativeAdd(type, listener, options);
      },
    });
    Object.defineProperty(window, 'removeEventListener', {
      configurable: true,
      value(type: string, listener: EventListenerOrEventListenerObject, options?: boolean | EventListenerOptions) {
        if (trackedTypes[type]) {
          const index = probe.active.findIndex(entry => entry.type === type && entry.listener === listener);
          if (index >= 0) probe.active.splice(index, 1);
          probe.retired.push({ type, listener });
          probe.removes++;
        }
        return nativeRemove(type, listener, options);
      },
    });
  });
  await page.goto('/?ownership=true');
  const stats = page.getByTestId('lifecycle-ownership-stats');
  await expect(stats).toHaveText('Starts: 1; Cleanups: 0; Disposed: false; Cycles: 0');
  await expect(page.getByTestId('lifecycle-scope-identity')).toHaveText('Stable scope: true');
  expect(await page.evaluate(() => {
    const probe = (window as LifecycleProbeWindow).__summonLifecycleProbe;
    return { adds: probe.adds, removes: probe.removes, active: probe.active.length };
  })).toEqual({ adds: 3, removes: 0, active: 3 });

  await page.getByRole('button', { name: 'Pause owned lifecycle', exact: true }).click();
  await expect(stats).toHaveText('Starts: 1; Cleanups: 1; Disposed: false; Cycles: 0');
  await page.getByRole('button', { name: 'Resume owned lifecycle', exact: true }).click();
  await expect(stats).toHaveText('Starts: 2; Cleanups: 1; Disposed: false; Cycles: 0');
  await page.getByRole('button', { name: 'Destroy owned lifecycle', exact: true }).click();
  await expect(stats).toHaveText('Starts: 2; Cleanups: 2; Disposed: true; Cycles: 0');
  expect(await page.evaluate(() => {
    const probe = (window as LifecycleProbeWindow).__summonLifecycleProbe;
    return { adds: probe.adds, removes: probe.removes, active: probe.active.length };
  })).toEqual({ adds: 3, removes: 3, active: 0 });

  await page.evaluate(() => {
    const entry = (window as LifecycleProbeWindow).__summonLifecycleProbe.retired
      .find(candidate => candidate.type === 'visibilitychange')!;
    if (typeof entry.listener === 'function') entry.listener(new Event('visibilitychange'));
    else entry.listener.handleEvent(new Event('visibilitychange'));
  });
  await expect(stats).toHaveText('Starts: 2; Cleanups: 2; Disposed: true; Cycles: 0');

  await page.getByRole('button', { name: 'Run 100 lifecycle cycles', exact: true }).click();
  await expect(stats).toHaveText('Starts: 2; Cleanups: 2; Disposed: true; Cycles: 100');
  expect(await page.evaluate(() => {
    const probe = (window as LifecycleProbeWindow).__summonLifecycleProbe;
    return { adds: probe.adds, removes: probe.removes, active: probe.active.length };
  })).toEqual({ adds: 303, removes: 303, active: 0 });
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


test('independent roots retain their own state, callbacks and account views', async ({ page }) => {
  await page.goto('/?roots=two');
  const first = page.locator('#root');
  const second = page.locator('#second-root');
  await expect(first.getByTestId('account-value')).toHaveText('Synthetic account A');
  await expect(second.getByTestId('account-value')).toHaveText('Synthetic account B');
  await first.getByRole('button', { name: 'Increment', exact: true }).click();
  await expect(first.getByTestId('counter')).toHaveText('Count: 1');
  await expect(second.getByTestId('counter')).toHaveText('Count: 0');
  await second.getByRole('button', { name: 'Increment', exact: true }).click();
  await expect(second.getByTestId('counter')).toHaveText('Count: 1');
  await expect(first.getByTestId('counter')).toHaveText('Count: 1');
  await first.getByTestId('controlled-input').fill('Synthetic edited A');
  await expect(first.getByTestId('account-value')).toHaveText('Synthetic edited A');
  await expect(second.getByTestId('account-value')).toHaveText('Synthetic account B');
  await first.getByRole('button', { name: 'Logout', exact: true }).click();
  await expect(first.getByTestId('locked')).toHaveText('Locked');
  await expect(second.getByTestId('locked')).toHaveCount(0);
  await expect(second.getByTestId('controlled-input')).toHaveValue('Synthetic account B');
  await second.getByRole('button', { name: 'Increment', exact: true }).click();
  await expect(second.getByTestId('counter')).toHaveText('Count: 2');
});


test('effect helpers retain mount and dependencies and commit after render', async ({ page }) => {
  await expect(page.getByTestId('helper-stats')).toHaveText('Mounts: 1; Setups: 1; Cleanups: 0; Side errors: 0; remembered after effects');
  for (let step = 1; step <= 5; step++) {
    await page.getByRole('button', { name: 'Increment', exact: true }).click();
    await expect(page.getByTestId('counter')).toHaveText(`Count: ${step}`);
    await expect(page.getByTestId('helper-stats')).toHaveText('Mounts: 1; Setups: 1; Cleanups: 0; Side errors: 0; remembered after effects');
  }
  await page.getByRole('button', { name: 'Change effect key', exact: true }).click();
  await expect(page.getByTestId('helper-stats')).toHaveText('Mounts: 1; Setups: 2; Cleanups: 1; Side errors: 0; remembered after effects');
});

test('root disposal cancels queued work and callbacks and survives 100 mount cycles', async ({ page }) => {
  await page.goto('/?lifecycle=true');
  const first = page.locator('#root');
  const second = page.locator('#second-root');
  const controls = page.locator('#controls');
  const oldButton = await first.getByRole('button', { name: 'Increment', exact: true }).elementHandle();
  await controls.getByRole('button', { name: 'Queue update and dispose', exact: true }).click();
  await expect(first.locator('*')).toHaveCount(0);
  await oldButton!.evaluate(node => (node as HTMLElement).click());
  await controls.getByRole('button', { name: 'Refresh lifecycle stats', exact: true }).click();
  await expect(controls.getByTestId('root-stats')).toHaveText('Cycles: 0; Effects: 0; Collectors: 0; Last count: 10');
  await controls.getByRole('button', { name: 'Emit retired results', exact: true }).click();
  await expect(first.locator('*')).toHaveCount(0);
  await controls.getByRole('button', { name: 'Run 100 mount cycles', exact: true }).click();
  await expect(controls.getByTestId('root-stats')).toHaveText('Cycles: 100; Effects: 0; Collectors: 0; Last count: 1');
  await expect(first.locator('*')).toHaveCount(0);
  await expect(second.getByTestId('controlled-input')).toHaveValue('Synthetic account B');
  await second.getByRole('button', { name: 'Increment', exact: true }).click();
  await expect(second.getByTestId('counter')).toHaveText('Count: 1');
  await controls.getByRole('button', { name: 'Replace first root', exact: true }).click();
  await expect(first.getByTestId('counter')).toHaveText('Count: 0');
  await first.getByRole('button', { name: 'Increment', exact: true }).click();
  await expect(first.getByTestId('counter')).toHaveText('Count: 1');
  await controls.getByRole('button', { name: 'Replace first root', exact: true }).click();
  await expect(first.getByTestId('counter')).toHaveText('Count: 0');
  await expect(first.getByTestId('active-effects')).toHaveText('Active effects: 1');
  await expect(second.getByTestId('counter')).toHaveText('Count: 1');
});


test('failed mounts release partial DOM and effects and restore neighboring roots', async ({ page }) => {
  await page.goto('/?lifecycle=true');
  const first = page.locator('#root');
  const second = page.locator('#second-root');
  const controls = page.locator('#controls');
  const neighbor = await second.getByTestId('counter').elementHandle();
  await controls.getByRole('button', { name: 'Fail first mount', exact: true }).click();
  await expect(controls.getByTestId('failed-mounts')).toHaveText('Failed mounts: 1');
  await expect(controls.getByTestId('root-stats')).toHaveText('Cycles: 0; Effects: 0; Collectors: 0; Last count: 0');
  await expect(first.locator('*')).toHaveCount(0);
  expect(await neighbor!.evaluate(node => node === document.querySelector('#second-root [data-testid="counter"]'))).toBe(true);
  await second.getByRole('button', { name: 'Increment', exact: true }).click();
  await expect(second.getByTestId('counter')).toHaveText('Count: 1');
  await controls.getByRole('button', { name: 'Replace first root', exact: true }).click();
  await expect(first.getByTestId('counter')).toHaveText('Count: 0');
  await first.getByRole('button', { name: 'Increment', exact: true }).click();
  await expect(first.getByTestId('counter')).toHaveText('Count: 1');
  await expect(second.getByTestId('counter')).toHaveText('Count: 1');
});

test('JS microtask mounts cancel queued and late work on disposal', async ({ page }, testInfo) => {
  test.skip(testInfo.project.name.startsWith('wasm-'), 'MicrotaskScheduler is a JS-specific API');
  await page.goto('/?lifecycle=true&scheduler=microtask');
  const first = page.locator('#root');
  const controls = page.locator('#controls');
  await controls.getByRole('button', { name: 'Queue update and dispose', exact: true }).click();
  await expect(first.locator('*')).toHaveCount(0);
  await expect(controls.getByTestId('root-stats')).toHaveText('Cycles: 0; Effects: 0; Collectors: 0; Last count: 10');
  await controls.getByRole('button', { name: 'Emit retired results', exact: true }).click();
  await expect(first.locator('*')).toHaveCount(0);
  await controls.getByRole('button', { name: 'Replace first root', exact: true }).click();
  await first.getByRole('button', { name: 'Increment', exact: true }).click();
  await expect(first.getByTestId('counter')).toHaveText('Count: 1');
});

test('safe document trees keep hostile content inert and release local CID capabilities', async ({ page }) => {
  const unauthorizedRequests: string[] = [];
  page.on('request', request => {
    if (request.url().includes('tracker.invalid')) unauthorizedRequests.push(request.url());
  });
  await page.goto('/?safeContent=true');
  const document = page.getByTestId('safe-document');
  await expect(document).toContainText('RTL שלום');
  await expect(document).toContainText('<img src=https://tracker.invalid/pixel');
  await expect(document.locator('script, iframe, form, input, style, svg, math, object, embed')).toHaveCount(0);
  await expect(document.locator('img')).toHaveCount(1);
  await expect(document.getByAltText('Authorized local CID image')).toHaveAttribute('src', /^blob:/);
  const link = document.getByRole('link');
  await expect(link).toHaveAttribute('href', 'https://example.com/final');
  await expect(link).toContainText('example.com');
  expect(await page.evaluate(() => '__summonXss' in globalThis ? globalThis.__summonXss : undefined)).toBeUndefined();
  expect(unauthorizedRequests).toEqual([]);
  await page.getByRole('button', { name: 'Remove CID image', exact: true }).click();
  await expect(document.locator('img')).toHaveCount(0);
  await expect(page.getByTestId('cid-releases')).toHaveText('CID releases: 1');
});

test('source consumer mounts and navigates the real browser router', async ({ page }) => {
  await page.goto('/?router=true');
  await expect(page.getByTestId('route-value')).toHaveText('Fixture route');
  await page.getByRole('button', { name: 'Open fixture item', exact: true }).click();
  await expect(page).toHaveURL(/\/fixture\/item\/42$/);
  await expect(page.getByTestId('route-value')).toHaveText('Fixture item 42');
  await page.getByRole('button', { name: 'Return to fixture route', exact: true }).click();
  await expect(page).toHaveURL(/\/fixture$/);
  await expect(page.getByTestId('route-value')).toHaveText('Fixture route');
});

test('private route families deep-link through the public shell and remain locked until authorized', async ({ page }) => {
  const routes: Array<[string, string]> = [
    ['/mail', '/mail'],
    ['/mail/thread/opaque-1', '/mail/thread/:id:opaque-1'],
    ['/mail/compose', '/mail/compose'],
    ['/calendar', '/calendar'],
    ['/calendar/event/event-1', '/calendar/event/:id:event-1'],
    ['/aliases', '/aliases'],
    ['/aliases/alias-1', '/aliases/:id:alias-1'],
    ['/security', '/security'],
    ['/security/devices', '/security/devices'],
    ['/security/recovery', '/security/recovery'],
    ['/drive/folder/object', '/drive/*'],
    ['/attention', '/attention'],
    ['/connectors/source', '/connectors/*'],
    ['/feed', '/feed'],
    ['/people/opaque-handle', '/people/:handle:opaque-handle'],
    ['/communities/community-1', '/communities/:id:community-1'],
  ];
  for (const [path, rendered] of routes) {
    await page.goto(`${path}?privateRouting=true`);
    await expect(page).toHaveTitle('Summon source fixture');
    await expect(page.getByTestId('route-state')).toHaveText('locked');
    await expect(page.getByTestId('route-effects')).toHaveText('0');
    await expect(page.getByTestId('route-mounts')).toHaveText('0');
    await page.getByRole('button', { name: 'Unlock routes', exact: true }).click();
    await expect(page.getByTestId('route-state')).toHaveText(rendered);
    await expect(page.getByTestId('route-effects')).toHaveText('1');
  }
});

test('guards, history, malformed paths and encrypted draft decisions preserve private teardown', async ({ page }) => {
  await page.goto('/mail?privateRouting=true');
  await expect(page.getByTestId('route-state')).toHaveText('locked');
  await page.getByRole('button', { name: 'Unlock routes', exact: true }).click();
  await expect(page.getByTestId('route-state')).toHaveText('/mail');

  await page.getByRole('button', { name: 'Open calendar', exact: true }).click();
  await expect(page).toHaveURL(/\/calendar$/);
  await page.goBack();
  await expect(page.getByTestId('route-state')).toHaveText('/mail');
  await page.goForward();
  await expect(page.getByTestId('route-state')).toHaveText('/calendar');

  await page.getByRole('button', { name: 'Disable route', exact: true }).click();
  await expect(page.getByTestId('route-state')).toHaveText('feature unavailable');
  await expect(page.getByTestId('route-effects')).toHaveText('0');
  await page.getByRole('button', { name: 'Deny route', exact: true }).click();
  await expect(page.getByTestId('route-state')).toHaveText('permission denied');
  await expect(page.getByTestId('route-effects')).toHaveText('0');

  await page.getByRole('button', { name: 'Unlock routes', exact: true }).click();
  await page.getByRole('button', { name: 'Open composer', exact: true }).click();
  await page.getByRole('button', { name: 'Edit encrypted draft', exact: true }).click();
  await page.getByRole('button', { name: 'Leave composer', exact: true }).click();
  await expect(page.getByTestId('pending-route')).toHaveText('/mail');
  await expect(page.getByTestId('route-state')).toHaveText('/mail/compose');
  await page.getByRole('button', { name: 'Cancel transition', exact: true }).click();
  await expect(page.getByTestId('pending-route')).toHaveText('none');
  await page.getByRole('button', { name: 'Leave composer', exact: true }).click();
  await page.getByRole('button', { name: 'Keep encrypted draft', exact: true }).click();
  await expect(page.getByTestId('encrypted-draft-saved')).toHaveText('true');
  await expect(page.getByTestId('route-state')).toHaveText('/mail');

  await page.evaluate(() => {
    history.pushState(null, '', '/mail/%GG');
    dispatchEvent(new PopStateEvent('popstate'));
  });
  await expect(page.getByTestId('route-state')).toHaveText('Safe not found');
  await expect(page.getByTestId('route-effects')).toHaveText('0');

  await page.getByRole('button', { name: 'Open mail', exact: true }).click();
  await page.getByRole('button', { name: 'Open calendar', exact: true }).click();
  await page.getByRole('button', { name: 'Lock routes', exact: true }).click();
  await page.goBack();
  await expect(page.getByTestId('route-state')).toHaveText('locked');
  await expect(page.getByTestId('route-effects')).toHaveText('0');
});

test('router disposal removes popstate and tabs keep independent authorization', async ({ page, context }, testInfo) => {
  test.skip(testInfo.project.name === 'wasm-webkit', 'Playwright WebKit crashes when this bounded container instantiates a second WASM tab');
  await page.addInitScript(() => {
    const nativeAdd = window.addEventListener.bind(window);
    const nativeRemove = window.removeEventListener.bind(window);
    const state = { adds: 0, removes: 0 };
    (window as Window & { __popstateProbe: typeof state }).__popstateProbe = state;
    window.addEventListener = ((type: string, listener: EventListenerOrEventListenerObject, options?: boolean | AddEventListenerOptions) => {
      if (type === 'popstate') state.adds++;
      return nativeAdd(type, listener, options);
    }) as typeof window.addEventListener;
    window.removeEventListener = ((type: string, listener: EventListenerOrEventListenerObject, options?: boolean | EventListenerOptions) => {
      if (type === 'popstate') state.removes++;
      return nativeRemove(type, listener, options);
    }) as typeof window.removeEventListener;
  });
  await page.goto('/mail?privateRouting=true');
  await expect.poll(() => page.evaluate(() => (window as Window & { __popstateProbe: { adds: number } }).__popstateProbe.adds)).toBe(1);
  await page.getByRole('button', { name: 'Toggle router mount', exact: true }).click();
  await expect.poll(() => page.evaluate(() => (window as Window & { __popstateProbe: { removes: number } }).__popstateProbe.removes)).toBe(1);
  await page.getByRole('button', { name: 'Toggle router mount', exact: true }).click();

  const second = await context.newPage();
  await second.goto('/mail?privateRouting=true');
  await page.getByRole('button', { name: 'Unlock routes', exact: true }).click();
  await expect(page.getByTestId('route-state')).toHaveText('/mail');
  expect(await second.getByTestId('route-state').textContent()).not.toBe('/mail');
  await expect(second.getByTestId('route-effects')).toHaveText('0');
  await second.close();
});

test('responsive listeners are owned across recomposition, failure, replacement and disposal', async ({ page }) => {
  test.setTimeout(60_000);
  await page.addInitScript(() => {
    const browserWindow = window as ResizeProbeWindow;
    const nativeAdd = window.addEventListener.bind(window);
    const nativeRemove = window.removeEventListener.bind(window);
    const nativeSetAttribute = Element.prototype.setAttribute;
    const probe = {
      adds: 0,
      removes: 0,
      active: [] as EventListenerOrEventListenerObject[],
      retired: [] as EventListenerOrEventListenerObject[],
      writes: new WeakMap<Element, number>(),
    };
    browserWindow.__summonResizeProbe = probe;
    Object.defineProperty(browserWindow, 'addEventListener', {
      configurable: true,
      value(type: string, listener: EventListenerOrEventListenerObject, options?: boolean | AddEventListenerOptions) {
        if (type === 'resize') {
          probe.adds++;
          if (!probe.active.includes(listener)) probe.active.push(listener);
        }
        return nativeAdd(type, listener, options);
      },
    });
    Object.defineProperty(browserWindow, 'removeEventListener', {
      configurable: true,
      value(type: string, listener: EventListenerOrEventListenerObject, options?: boolean | EventListenerOptions) {
        if (type === 'resize') {
          probe.removes++;
          const index = probe.active.indexOf(listener);
          if (index >= 0) probe.active.splice(index, 1);
          probe.retired.push(listener);
        }
        return nativeRemove(type, listener, options);
      },
    });
    Element.prototype.setAttribute = function(name: string, value: string) {
      if (name === 'data-screen-size') {
        probe.writes.set(this, (probe.writes.get(this) ?? 0) + 1);
      }
      return nativeSetAttribute.call(this, name, value);
    };
  });
  await page.goto('/?responsive=true');

  const first = page.locator('#root');
  const second = page.locator('#second-root');
  const controls = page.locator('#controls');
  const layout = first.getByTestId('responsive-layout');
  await expect(layout).toHaveCount(1);
  await expect(second.getByTestId('controlled-input')).toHaveValue('Synthetic account B');
  expect(await page.evaluate(() => {
    const probe = (window as ResizeProbeWindow).__summonResizeProbe;
    return { adds: probe.adds, removes: probe.removes, active: probe.active.length };
  })).toEqual({ adds: 1, removes: 0, active: 1 });

  for (let revision = 1; revision <= 20; revision++) {
    await controls.getByRole('button', { name: 'Recompose responsive root', exact: true }).click();
    await expect(first.getByTestId('responsive-revision')).toHaveText(`Responsive revision: ${revision}`);
  }
  expect(await page.evaluate(() => {
    const probe = (window as ResizeProbeWindow).__summonResizeProbe;
    return { adds: probe.adds, removes: probe.removes, active: probe.active.length };
  })).toEqual({ adds: 1, removes: 0, active: 1 });

  await page.setViewportSize({ width: 500, height: 720 });
  await expect(layout).toHaveAttribute('data-screen-size', 'SMALL');
  await layout.evaluate(node => {
    const probe = (window as ResizeProbeWindow).__summonResizeProbe;
    probe.writes.set(node, 0);
    node.setAttribute('data-screen-size', 'STALE');
    probe.writes.set(node, 0);
  });
  await page.evaluate(() => window.dispatchEvent(new Event('resize')));
  await expect(layout).toHaveAttribute('data-screen-size', 'SMALL');
  expect(await layout.evaluate(node => (window as ResizeProbeWindow).__summonResizeProbe.writes.get(node))).toBe(1);

  const retiredAfterRemoval = await layout.elementHandle();
  await controls.getByRole('button', { name: 'Toggle responsive layout', exact: true }).click();
  await expect(layout).toHaveCount(0);
  expect(await page.evaluate(() => {
    const probe = (window as ResizeProbeWindow).__summonResizeProbe;
    return { adds: probe.adds, removes: probe.removes, active: probe.active.length };
  })).toEqual({ adds: 1, removes: 1, active: 0 });
  await retiredAfterRemoval!.evaluate(node => node.setAttribute('data-screen-size', 'RETIRED'));
  await page.evaluate(() => {
    const listener = (window as ResizeProbeWindow).__summonResizeProbe.retired[0];
    if (typeof listener === 'function') listener.call(window, new Event('resize'));
    else listener.handleEvent(new Event('resize'));
  });
  expect(await retiredAfterRemoval!.getAttribute('data-screen-size')).toBe('RETIRED');

  await controls.getByRole('button', { name: 'Toggle responsive layout', exact: true }).click();
  await expect(layout).toHaveAttribute('data-screen-size', 'SMALL');
  const replacedLayout = await layout.elementHandle();
  const neighbor = await second.getByTestId('counter').elementHandle();
  await controls.getByRole('button', { name: 'Replace responsive root', exact: true }).click();
  await expect(layout).toHaveCount(1);
  expect(await page.evaluate(() => {
    const probe = (window as ResizeProbeWindow).__summonResizeProbe;
    return { adds: probe.adds, removes: probe.removes, active: probe.active.length };
  })).toEqual({ adds: 3, removes: 2, active: 1 });
  await replacedLayout!.evaluate(node => node.setAttribute('data-screen-size', 'REPLACED'));
  await page.evaluate(() => {
    const probe = (window as ResizeProbeWindow).__summonResizeProbe;
    const listener = probe.retired[probe.retired.length - 1];
    if (typeof listener === 'function') listener.call(window, new Event('resize'));
    else listener.handleEvent(new Event('resize'));
  });
  expect(await replacedLayout!.getAttribute('data-screen-size')).toBe('REPLACED');
  expect(await neighbor!.evaluate(node => node === document.querySelector('#second-root [data-testid="counter"]'))).toBe(true);

  await controls.getByRole('button', { name: 'Fail responsive mount', exact: true }).click();
  await expect(first.locator('*')).toHaveCount(0);
  await expect(controls.getByTestId('responsive-stats')).toHaveText('Cycles: 0; Failed mounts: 1; Cancellations: 0');
  expect(await page.evaluate(() => (window as ResizeProbeWindow).__summonResizeProbe.active.length)).toBe(0);
  await second.getByRole('button', { name: 'Increment', exact: true }).click();
  await expect(second.getByTestId('counter')).toHaveText('Count: 1');

  await controls.getByRole('button', { name: 'Cancel responsive mount', exact: true }).click();
  await expect(controls.getByTestId('responsive-stats')).toHaveText('Cycles: 0; Failed mounts: 1; Cancellations: 1');
  expect(await page.evaluate(() => (window as ResizeProbeWindow).__summonResizeProbe.active.length)).toBe(0);
  await controls.getByRole('button', { name: 'Replace responsive root', exact: true }).click();
  await expect(layout).toHaveCount(1);
  expect(await page.evaluate(() => (window as ResizeProbeWindow).__summonResizeProbe.active.length)).toBe(1);

  await controls.getByRole('button', { name: 'Run 100 responsive cycles', exact: true }).click();
  await expect(controls.getByTestId('responsive-stats')).toHaveText('Cycles: 100; Failed mounts: 1; Cancellations: 1');
  await expect(first.locator('*')).toHaveCount(0);
  expect(await page.evaluate(() => {
    const probe = (window as ResizeProbeWindow).__summonResizeProbe;
    return { adds: probe.adds, removes: probe.removes, active: probe.active.length };
  })).toEqual(expect.objectContaining({ active: 0 }));
  expect(await page.evaluate(() => {
    const probe = (window as ResizeProbeWindow).__summonResizeProbe;
    return probe.adds === probe.removes;
  })).toBe(true);
  await expect(second.getByTestId('counter')).toHaveText('Count: 1');
});



test('remember retains nullable values and named keys and forgets removed slots', async ({ page }) => {
  await page.goto('/?remember=true');
  await expect(page.getByTestId('remember-stats')).toHaveText('Null: 1; Keyed null: 1; Named: first/second/numeric');
  await expect(page.getByTestId('remembered-probe')).toHaveText('generation-1');
  for (let count = 1; count <= 5; count++) {
    await page.getByRole('button', { name: 'Increment', exact: true }).click();
    await expect(page.getByTestId('counter')).toHaveText(`Count: ${count}`);
    await expect(page.getByTestId('remember-stats')).toHaveText('Null: 1; Keyed null: 1; Named: first/second/numeric');
    await expect(page.getByTestId('remembered-probe')).toHaveText('generation-1');
  }
  await page.getByRole('button', { name: 'Change effect key', exact: true }).click();
  await expect(page.getByTestId('remember-stats')).toHaveText('Null: 1; Keyed null: 2; Named: first/second/numeric');
  await page.getByRole('button', { name: 'Toggle remembered probe', exact: true }).click();
  await expect(page.getByTestId('remembered-probe')).toHaveCount(0);
  await page.getByRole('button', { name: 'Toggle remembered probe', exact: true }).click();
  await expect(page.getByTestId('remembered-probe')).toHaveText('generation-2');
  await expect(page.getByTestId('remember-stats')).toHaveText('Null: 1; Keyed null: 2; Named: first/second/numeric');
});


test('composition keys retain item state and trailing state through conditional logout', async ({ page }) => {
  await expect(page.getByTestId('group-probe')).toHaveText('generation-1');
  await expect(page.getByTestId('state-item-two')).toHaveText('two:1');
  await page.getByTestId('controlled-input').fill('Synthetic grouped value');
  await expect(page.getByTestId('controlled-input')).toHaveValue('Synthetic grouped value');
  await expect(page.getByTestId('controlled-input')).toBeFocused();
  await page.getByRole('button', { name: 'Reverse items', exact: true }).click();
  await expect(page.getByTestId('state-item-one')).toHaveText('one:1');
  await expect(page.getByTestId('state-item-two')).toHaveText('two:1');
  await page.getByRole('button', { name: 'Remove second item', exact: true }).click();
  await expect(page.getByTestId('state-item-two')).toHaveCount(0);
  await page.getByRole('button', { name: 'Insert second item', exact: true }).click();
  await expect(page.getByTestId('state-item-two')).toHaveText('two:2');
  await expect(page.getByTestId('state-item-one')).toHaveText('one:1');
  await page.getByRole('button', { name: 'Toggle owned effect', exact: true }).click();
  await expect(page.getByTestId('active-effects')).toHaveText('Active effects: 0');
  await expect(page.getByTestId('group-probe')).toHaveText('generation-1');
  await page.getByRole('button', { name: 'Logout', exact: true }).click();
  await expect(page.getByTestId('locked')).toHaveText('Locked');
  await expect(page.getByTestId('controlled-input')).toHaveCount(0);
  await expect(page.getByTestId('group-probe')).toHaveText('generation-1');
  await page.getByRole('button', { name: 'Increment', exact: true }).click();
  await expect(page.getByTestId('counter')).toHaveText('Count: 1');
  await expect(page.getByTestId('group-probe')).toHaveText('generation-1');
});


for (const layout of ['Column', 'Row', 'Box', 'Div']) {
  test(`nested ${layout} failure releases its root and keeps neighboring ownership`, async ({ page }) => {
    const diagnostics: string[] = [];
    page.on('console', message => diagnostics.push(message.text()));
    page.on('pageerror', error => diagnostics.push(error.message));
    await page.goto('/?failures=true');
    const first = page.locator('#root');
    const second = page.locator('#second-root');
    const controls = page.locator('#controls');
    const neighbor = await second.getByTestId('counter').elementHandle();
    await controls.getByRole('button', { name: `Fail ${layout}`, exact: true }).click();
    await expect(controls.getByTestId('failure-stats')).toHaveText('Failures: 1; Cancellations: 0; Active: 0; Collectors: 0; Cleanups: 2');
    await expect(first.locator('*')).toHaveCount(0);
    expect(await neighbor!.evaluate(node => node === document.querySelector('#second-root [data-testid="counter"]'))).toBe(true);
    await second.getByRole('button', { name: 'Increment', exact: true }).click();
    await expect(second.getByTestId('counter')).toHaveText('Count: 1');
    await controls.getByRole('button', { name: 'Mount healthy layout', exact: true }).click();
    await expect(first.getByTestId('healthy-layout')).toHaveText(`Healthy ${layout}`);
    await expect(first.getByTestId('failure-input')).toHaveValue('PRIVATE_RENDER_VALUE_SENTINEL');
    await first.getByTestId('failure-input').fill('PRIVATE_RENDER_EDIT_SENTINEL');
    await expect(first.getByTestId('failure-input')).toHaveValue('PRIVATE_RENDER_EDIT_SENTINEL');
    await expect(first.getByTestId('failure-input')).toBeFocused();
    await controls.getByRole('button', { name: 'Dispose healthy layout', exact: true }).click();
    await expect(first.locator('*')).toHaveCount(0);
    await expect(controls.getByTestId('failure-stats')).toHaveText('Failures: 1; Cancellations: 0; Active: 0; Collectors: 0; Cleanups: 4');
    expect(diagnostics.filter(message => message.includes('PRIVATE_RENDER_'))).toEqual([]);
    expect(diagnostics.filter(message => message === 'Summon renderer operation failed').length).toBeLessThanOrEqual(1);
  });
}

test('nested cancellation propagates after cleanup without private diagnostic payloads', async ({ page }) => {
  const diagnostics: string[] = [];
  page.on('console', message => diagnostics.push(message.text()));
  page.on('pageerror', error => diagnostics.push(error.message));
  await page.goto('/?failures=true');
  await page.getByRole('button', { name: 'Cancel Column', exact: true }).click();
  await expect(page.getByTestId('failure-stats')).toHaveText('Failures: 0; Cancellations: 1; Active: 0; Collectors: 0; Cleanups: 2');
  await expect(page.locator('#root *')).toHaveCount(0);
  expect(diagnostics.filter(message => message.includes('PRIVATE_RENDER_'))).toEqual([]);
});


test('native input write failure cannot produce a successful private view', async ({ page }) => {
  const diagnostics: string[] = [];
  page.on('console', message => diagnostics.push(message.text()));
  page.on('pageerror', error => diagnostics.push(error.message));
  await page.goto('/?failures=true');
  await page.evaluate(() => {
    const original = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value')!;
    Object.defineProperty(HTMLInputElement.prototype, 'value', {
      ...original,
      set(value: string) {
        if (value === 'PRIVATE_RENDER_VALUE_SENTINEL') throw new Error('PRIVATE_RENDER_NATIVE_ERROR_SENTINEL');
        original.set!.call(this, value);
      }
    });
  });
  await page.getByRole('button', { name: 'Mount healthy layout', exact: true }).click();
  await expect(page.getByTestId('failure-stats')).toHaveText('Failures: 1; Cancellations: 0; Active: 0; Collectors: 0; Cleanups: 2');
  await expect(page.locator('#root *')).toHaveCount(0);
  await expect(page.locator('#second-root').getByTestId('controlled-input')).toHaveValue('Synthetic account B');
  expect(diagnostics.filter(message => message.includes('PRIVATE_RENDER_'))).toEqual([]);
});
