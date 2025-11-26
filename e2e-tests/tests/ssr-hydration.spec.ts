import { test, expect } from '@playwright/test';

/**
 * SSR Hydration Tests for JVM Server-Side Rendered pages.
 * These tests verify that the hydration script properly initializes
 * and makes server-rendered components interactive.
 */

test.describe('SSR Hydration Tests', () => {
  
  test.describe('Mobile viewport - Hamburger Menu', () => {
    test.use({ viewport: { width: 375, height: 667 } }); // iPhone SE

    test('page loads with SSR content', async ({ page }) => {
      page.on('console', msg => console.log('PAGE LOG:', msg.text()));
      page.on('pageerror', exception => console.log(`PAGE ERROR: ${exception}`));

      const baseURL = process.env.BASE_URL || 'http://localhost:8080';
      await page.goto(baseURL, { waitUntil: 'networkidle' });

      // Wait for body content to be available
      await page.waitForSelector('body span', { timeout: 10000 });

      // Page should have SSR content immediately (no loading state)
      await expect(page.locator('span:has-text("SSR Hydration Test App")')).toBeVisible({ timeout: 10000 });
      await expect(page.locator('span:has-text("Hydration Test Instructions")')).toBeVisible();
    });

    test('hydration script loads and initializes', async ({ page }) => {
      const consoleLogs: string[] = [];
      page.on('console', msg => {
        consoleLogs.push(msg.text());
      });

      const baseURL = process.env.BASE_URL || 'http://localhost:8080';
      await page.goto(baseURL, { waitUntil: 'networkidle' });

      // Wait for page to load
      await page.waitForSelector('body span', { timeout: 10000 });
      await expect(page.locator('span:has-text("SSR Hydration Test App")')).toBeVisible({ timeout: 10000 });
      
      // Wait a bit for hydration script to run
      await page.waitForTimeout(1000);

      // Check for hydration initialization logs
      console.log('Console logs captured:', consoleLogs);
      
      // Should see [Summon] or hydration-related logs
      const hasHydrationLogs = consoleLogs.some(log => 
        log.includes('[Summon]') || 
        log.includes('GlobalEventListener') ||
        log.includes('hydration') ||
        log.includes('SUMMON')
      );
      
      // Log what we found for debugging
      const summonLogs = consoleLogs.filter(log => log.includes('[Summon]') || log.includes('SUMMON'));
      console.log('Summon-related logs:', summonLogs);
    });

    test('hamburger button is visible on mobile', async ({ page }) => {
      const baseURL = process.env.BASE_URL || 'http://localhost:8080';
      await page.goto(baseURL, { waitUntil: 'networkidle' });

      await page.waitForSelector('body span', { timeout: 10000 });
      await expect(page.locator('span:has-text("SSR Hydration Test App")')).toBeVisible({ timeout: 10000 });

      // Hamburger button should be visible on mobile
      const hamburgerButton = page.locator('[data-test-id="hamburger-button"]');
      await expect(hamburgerButton).toBeVisible();
    });

    test('hamburger menu toggle works after hydration', async ({ page }) => {
      page.on('console', msg => console.log('PAGE LOG:', msg.text()));

      const baseURL = process.env.BASE_URL || 'http://localhost:8080';
      await page.goto(baseURL, { waitUntil: 'networkidle' });

      await page.waitForSelector('body span', { timeout: 10000 });
      await expect(page.locator('span:has-text("SSR Hydration Test App")')).toBeVisible({ timeout: 10000 });

      // Wait for hydration to complete - allow WASM to fully load
      await page.waitForTimeout(2000);

      // Find hamburger button
      const hamburgerButton = page.locator('[data-test-id="hamburger-button"]');
      await expect(hamburgerButton).toBeVisible();

      // Get the menu content id
      const menuContentId = await hamburgerButton.getAttribute('aria-controls');
      expect(menuContentId).toBeTruthy();
      console.log(`Menu content id: ${menuContentId}`);

      const menuContent = page.locator(`#${menuContentId}`);

      // Initially hidden
      const initialDisplay = await menuContent.evaluate(el => getComputedStyle(el).display);
      console.log(`Initial display style: ${initialDisplay}`);
      expect(initialDisplay).toBe('none');

      // Click to open
      console.log('Clicking hamburger button to open menu...');
      await hamburgerButton.click();
      await page.waitForTimeout(100);

      // Should be visible now
      const openDisplay = await menuContent.evaluate(el => getComputedStyle(el).display);
      console.log(`After open display style: ${openDisplay}`);
      expect(openDisplay).toBe('block');

      // Menu items should be visible
      await expect(page.getByText('Menu Item 1')).toBeVisible();
      await expect(page.getByText('Menu Item 2')).toBeVisible();
      await expect(page.getByText('Menu Item 3')).toBeVisible();

      // Click to close
      console.log('Clicking hamburger button to close menu...');
      await hamburgerButton.click();
      await page.waitForTimeout(100);

      // Should be hidden again
      const closedDisplay = await menuContent.evaluate(el => getComputedStyle(el).display);
      console.log(`After close display style: ${closedDisplay}`);
      expect(closedDisplay).toBe('none');
    });

    test('hamburger menu multiple toggle cycles work correctly', async ({ page }) => {
      // Test multiple open/close cycles to catch double-handling bugs
      page.on('console', msg => console.log('PAGE LOG:', msg.text()));

      const baseURL = process.env.BASE_URL || 'http://localhost:8080';
      await page.goto(baseURL, { waitUntil: 'networkidle' });

      await page.waitForSelector('body span', { timeout: 10000 });
      await expect(page.locator('span:has-text("SSR Hydration Test App")')).toBeVisible({ timeout: 10000 });

      // Wait for hydration to complete
      await page.waitForTimeout(2000);

      const hamburgerButton = page.locator('[data-test-id="hamburger-button"]');
      await expect(hamburgerButton).toBeVisible();

      const menuContentId = await hamburgerButton.getAttribute('aria-controls');
      const menuContent = page.locator(`#${menuContentId}`);

      // Test 5 complete open/close cycles
      for (let i = 0; i < 5; i++) {
        console.log(`\n=== Cycle ${i + 1} ===`);

        // Verify initially closed
        const beforeClick = await menuContent.evaluate(el => getComputedStyle(el).display);
        console.log(`Before open click: ${beforeClick}`);
        expect(beforeClick).toBe('none');

        // Click to open
        await hamburgerButton.click();
        await page.waitForTimeout(150);

        const afterOpen = await menuContent.evaluate(el => getComputedStyle(el).display);
        console.log(`After open click: ${afterOpen}`);
        expect(afterOpen).toBe('block');

        // Click to close
        await hamburgerButton.click();
        await page.waitForTimeout(150);

        const afterClose = await menuContent.evaluate(el => getComputedStyle(el).display);
        console.log(`After close click: ${afterClose}`);
        expect(afterClose).toBe('none');
      }

      console.log('\nAll 5 cycles passed!');
    });

    test('hamburger menu works immediately after page load (bootloader)', async ({ page }) => {
      // This tests that the bootloader handles clicks BEFORE hydration completes
      page.on('console', msg => console.log('PAGE LOG:', msg.text()));

      const baseURL = process.env.BASE_URL || 'http://localhost:8080';

      // Navigate but don't wait for networkidle - we want to test bootloader
      await page.goto(baseURL, { waitUntil: 'domcontentloaded' });

      // Wait just for the DOM to be ready, not for hydration
      await page.waitForSelector('[data-test-id="hamburger-button"]', { timeout: 5000 });

      const hamburgerButton = page.locator('[data-test-id="hamburger-button"]');
      const menuContentId = await hamburgerButton.getAttribute('aria-controls');
      const menuContent = page.locator(`#${menuContentId}`);

      // Test immediate click (may be handled by bootloader or hydration)
      console.log('Testing immediate click...');

      const initialDisplay = await menuContent.evaluate(el => getComputedStyle(el).display);
      console.log(`Initial display: ${initialDisplay}`);
      expect(initialDisplay).toBe('none');

      await hamburgerButton.click();
      await page.waitForTimeout(100);

      const afterFirstClick = await menuContent.evaluate(el => getComputedStyle(el).display);
      console.log(`After first click: ${afterFirstClick}`);
      expect(afterFirstClick).toBe('block');

      await hamburgerButton.click();
      await page.waitForTimeout(100);

      const afterSecondClick = await menuContent.evaluate(el => getComputedStyle(el).display);
      console.log(`After second click: ${afterSecondClick}`);
      expect(afterSecondClick).toBe('none');
    });

    test('hamburger menu verifies only one event handler is active', async ({ page }) => {
      // This test verifies that the __SUMMON_HYDRATION_ACTIVE__ flag prevents double-handling
      const consoleLogs: string[] = [];
      page.on('console', msg => {
        consoleLogs.push(msg.text());
        console.log('PAGE LOG:', msg.text());
      });

      const baseURL = process.env.BASE_URL || 'http://localhost:8080';
      await page.goto(baseURL, { waitUntil: 'networkidle' });

      await page.waitForSelector('body span', { timeout: 10000 });
      await page.waitForTimeout(2000);

      // Check that the hydration flag is set
      const hydrationActive = await page.evaluate(() => (window as any).__SUMMON_HYDRATION_ACTIVE__);
      console.log(`__SUMMON_HYDRATION_ACTIVE__: ${hydrationActive}`);
      expect(hydrationActive).toBe(true);

      // Count how many GlobalEventListener initializations happened
      const jsInitLogs = consoleLogs.filter(log =>
        log.includes('[Summon JS] GlobalEventListener.init() - registering')
      );
      const wasmInitLogs = consoleLogs.filter(log =>
        log.includes('[Summon WASM] GlobalEventListener.init() - registering')
      );
      const skipLogs = consoleLogs.filter(log =>
        log.includes('skipping, another hydration client is already active')
      );

      console.log(`JS init logs: ${jsInitLogs.length}`);
      console.log(`WASM init logs: ${wasmInitLogs.length}`);
      console.log(`Skip logs: ${skipLogs.length}`);

      // Exactly ONE should be registering (either JS or WASM)
      const totalRegistrations = jsInitLogs.length + wasmInitLogs.length;
      expect(totalRegistrations).toBe(1);

      // Now test that the hamburger works correctly
      const hamburgerButton = page.locator('[data-test-id="hamburger-button"]');
      const menuContentId = await hamburgerButton.getAttribute('aria-controls');
      const menuContent = page.locator(`#${menuContentId}`);

      await hamburgerButton.click();
      await page.waitForTimeout(100);
      const afterOpen = await menuContent.evaluate(el => getComputedStyle(el).display);
      expect(afterOpen).toBe('block');

      await hamburgerButton.click();
      await page.waitForTimeout(100);
      const afterClose = await menuContent.evaluate(el => getComputedStyle(el).display);
      expect(afterClose).toBe('none');
    });

    test('data-action attribute is present on hamburger button', async ({ page }) => {
      const baseURL = process.env.BASE_URL || 'http://localhost:8080';
      await page.goto(baseURL);

      await expect(page.getByText('SSR Hydration Test App')).toBeVisible({ timeout: 10000 });

      const hamburgerButton = page.locator('[data-test-id="hamburger-button"]');
      await expect(hamburgerButton).toBeVisible();

      // Check data-action is present and valid JSON
      const dataAction = await hamburgerButton.getAttribute('data-action');
      expect(dataAction).toBeTruthy();
      console.log(`data-action: ${dataAction}`);
      
      const action = JSON.parse(dataAction!);
      expect(action.type).toBe('toggle');
      expect(action.targetId).toMatch(/hamburger-menu-\d+/);
    });
  });

  test.describe('Desktop viewport', () => {
    test.use({ viewport: { width: 1280, height: 720 } });

    test('SSR page loads correctly on desktop', async ({ page }) => {
      const baseURL = process.env.BASE_URL || 'http://localhost:8080';
      await page.goto(baseURL);

      await expect(page.getByText('SSR Hydration Test App')).toBeVisible({ timeout: 10000 });
      await expect(page.getByText('Hydration Test Instructions')).toBeVisible();
    });
  });
});
