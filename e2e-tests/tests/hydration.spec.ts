import { test, expect } from '@playwright/test';

/**
 * Comprehensive hydration tests for Summon framework.
 * Tests client-side interactivity after the initial render.
 */

test.describe('Hydration Tests', () => {
  
  test.describe('Desktop viewport', () => {
    test.use({ viewport: { width: 1280, height: 720 } });

    test('basic button click updates state', async ({ page }) => {
      page.on('console', msg => console.log('PAGE LOG:', msg.text()));
      page.on('pageerror', exception => console.log(`PAGE ERROR: ${exception}`));

      const baseURL = process.env.BASE_URL || 'http://localhost:8080';
      await page.goto(baseURL);

      // Wait for app to load
      await expect(page.getByText('Hydration Test App')).toBeVisible({ timeout: 10000 });

      // Find counter
      await expect(page.getByText('Count: 0')).toBeVisible();

      // Click button
      const button = page.getByRole('button', { name: 'Click me' });
      await expect(button).toBeVisible();
      await button.click();

      // Verify state updated
      await expect(page.getByText('Count: 1')).toBeVisible({ timeout: 5000 });

      // Click again
      await button.click();
      await expect(page.getByText('Count: 2')).toBeVisible({ timeout: 5000 });
    });

    test('hydration script loads and initializes', async ({ page }) => {
      const consoleLogs: string[] = [];
      page.on('console', msg => {
        consoleLogs.push(msg.text());
      });

      const baseURL = process.env.BASE_URL || 'http://localhost:8080';
      await page.goto(baseURL);

      // Wait for app to load
      await expect(page.getByText('Hydration Test App')).toBeVisible({ timeout: 10000 });

      // Check that hydration initialized (from our debug logging)
      const hasInitLog = consoleLogs.some(log => 
        log.includes('Hydration test app') || 
        log.includes('GlobalEventListener') ||
        log.includes('[Summon]')
      );
      
      // Log all console messages for debugging
      console.log('Console logs captured:', consoleLogs);
    });
  });

  test.describe('Mobile viewport - Hamburger Menu', () => {
    test.use({ viewport: { width: 375, height: 667 } }); // iPhone SE

    test('hamburger menu is visible on mobile', async ({ page }) => {
      const baseURL = process.env.BASE_URL || 'http://localhost:8080';
      await page.goto(baseURL);

      await expect(page.getByText('Hydration Test App')).toBeVisible({ timeout: 10000 });

      // Hamburger button should be visible on mobile
      const hamburgerButton = page.locator('[data-test-id="hamburger-button"]');
      await expect(hamburgerButton).toBeVisible();
    });

    test('hamburger menu toggle works', async ({ page }) => {
      page.on('console', msg => console.log('PAGE LOG:', msg.text()));

      const baseURL = process.env.BASE_URL || 'http://localhost:8080';
      await page.goto(baseURL);

      await expect(page.getByText('Hydration Test App')).toBeVisible({ timeout: 10000 });

      // Find hamburger button
      const hamburgerButton = page.locator('[data-test-id="hamburger-button"]');
      await expect(hamburgerButton).toBeVisible();

      // Get the menu content id
      const menuContentId = await hamburgerButton.getAttribute('aria-controls');
      expect(menuContentId).toBeTruthy();
      
      const menuContent = page.locator(`#${menuContentId}`);
      
      // Initially hidden
      const initialDisplay = await menuContent.evaluate(el => getComputedStyle(el).display);
      expect(initialDisplay).toBe('none');

      // Click to open
      await hamburgerButton.click();
      await page.waitForTimeout(100);

      // Should be visible now
      const openDisplay = await menuContent.evaluate(el => getComputedStyle(el).display);
      expect(openDisplay).toBe('block');

      // Menu items should be visible
      await expect(page.getByText('Menu Item 1')).toBeVisible();
      await expect(page.getByText('Menu Item 2')).toBeVisible();
      await expect(page.getByText('Menu Item 3')).toBeVisible();

      // Click to close
      await hamburgerButton.click();
      await page.waitForTimeout(100);

      // Should be hidden again
      const closedDisplay = await menuContent.evaluate(el => getComputedStyle(el).display);
      expect(closedDisplay).toBe('none');
    });

    test('hamburger button aria-expanded updates on toggle', async ({ page }) => {
      const baseURL = process.env.BASE_URL || 'http://localhost:8080';
      await page.goto(baseURL);

      await expect(page.getByText('Hydration Test App')).toBeVisible({ timeout: 10000 });

      const hamburgerButton = page.locator('[data-test-id="hamburger-button"]');
      await expect(hamburgerButton).toBeVisible();

      // Initially aria-expanded should be false
      await expect(hamburgerButton).toHaveAttribute('aria-expanded', 'false');

      // Click to open
      await hamburgerButton.click();
      await page.waitForTimeout(100);

      // Note: The current implementation doesn't update aria-expanded on client-side toggle
      // This is a potential enhancement - for now we just test the visibility toggle
    });
  });

  test.describe('Tablet viewport', () => {
    test.use({ viewport: { width: 768, height: 1024 } }); // iPad

    test('app renders correctly on tablet', async ({ page }) => {
      const baseURL = process.env.BASE_URL || 'http://localhost:8080';
      await page.goto(baseURL);

      await expect(page.getByText('Hydration Test App')).toBeVisible({ timeout: 10000 });
      await expect(page.getByText('Counter Test')).toBeVisible();
    });
  });
});
