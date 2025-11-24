import { test, expect } from '@playwright/test';

test('hamburger menu should not refresh the page', async ({ page }) => {
  const baseURL = process.env.BASE_URL || 'http://localhost:8080';
  await page.goto(baseURL);
  
  // Wait for app to load
  await expect(page.getByText('Hello, Summon!')).toBeVisible({ timeout: 10000 });
  
  // Set a marker on the window object
  await page.evaluate(() => {
    (window as any).summonTestMarker = 'persistent';
  });
  
  // Find the hamburger button
  const button = page.locator('[data-test-id="hamburger-button"]');
  await expect(button).toBeVisible();
  
  // Click the button
  await button.click();
  
  // Wait a moment to see if a refresh happens
  await page.waitForTimeout(2000);
  
  // Check if the marker still exists
  const marker = await page.evaluate(() => (window as any).summonTestMarker);
  expect(marker).toBe('persistent');
  
  // Also verify the menu opened
  const menuContent = page.locator('[data-test-id="menu-content"]');
  await expect(menuContent).toBeVisible();
});
