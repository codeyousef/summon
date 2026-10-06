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

test('source consumer mounts and navigates the real browser router', async ({ page }, testInfo) => {
  test.skip(testInfo.project.name.startsWith('wasm-'), 'WASM browser history qualification belongs to SU-03');
  await page.goto('/?router=true');
  await expect(page.getByTestId('route-value')).toHaveText('Fixture route');
  await page.getByRole('button', { name: 'Open fixture item', exact: true }).click();
  await expect(page).toHaveURL(/\/fixture\/item\/42$/);
  await expect(page.getByTestId('route-value')).toHaveText('Fixture item 42');
  await page.getByRole('button', { name: 'Return to fixture route', exact: true }).click();
  await expect(page).toHaveURL(/\/fixture$/);
  await expect(page.getByTestId('route-value')).toHaveText('Fixture route');
});

test('responsive listeners are owned across recomposition, failure, replacement and disposal', async ({ page }) => {
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
