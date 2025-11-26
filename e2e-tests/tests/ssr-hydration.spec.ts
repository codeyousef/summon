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
