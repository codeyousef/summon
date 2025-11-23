import { test, expect } from '@playwright/test';

test('hamburger menu toggles content', async ({ page }) => {
  // Log console messages from the browser
  page.on('console', msg => console.log('PAGE LOG:', msg.text()));
  page.on('pageerror', exception => console.log(`PAGE ERROR: ${exception}`));

  const baseURL = process.env.BASE_URL || 'http://localhost:8080';
  console.log(`Navigating to ${baseURL}`);
  await page.goto(baseURL);
  
  // Wait for the app to load
  await expect(page.getByText('Hello, Summon!')).toBeVisible({ timeout: 10000 });
  
  // Find the hamburger button
  const button = page.locator('[data-test-id="hamburger-button"]');
  await expect(button).toBeVisible();
  
  // Verify menu content is initially hidden
  const menuContent = page.locator('[data-test-id="menu-content"]');
  await expect(menuContent).toBeHidden();
  
  // Click to open
  console.log('Clicking hamburger button...');
  await button.click();
  
  // Verify menu content is visible
  await expect(menuContent).toBeVisible();
  await expect(page.getByText('Menu Item 1')).toBeVisible();
  
  // Click to close
  console.log('Clicking hamburger button again...');
  await button.click();
  
  // Verify menu content is hidden again
  await expect(menuContent).toBeHidden();
});
