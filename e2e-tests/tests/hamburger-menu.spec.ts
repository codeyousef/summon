import { test, expect } from '@playwright/test';

// Use mobile viewport since hamburger menu only shows on mobile screens
test.use({
  viewport: { width: 375, height: 667 }, // iPhone SE size
});

test('hamburger menu toggles content', async ({ page }) => {
  // Log console messages from the browser
  page.on('console', msg => console.log('PAGE LOG:', msg.text()));
  page.on('pageerror', exception => console.log(`PAGE ERROR: ${exception}`));

  const baseURL = process.env.BASE_URL || 'http://localhost:8080';
  console.log(`Navigating to ${baseURL}`);
  await page.goto(baseURL);
  
  // Wait for the app to load - look for the title or any content
  await expect(page.locator('body')).not.toBeEmpty({ timeout: 10000 });
  
  // Find the hamburger button by data-test-id
  const button = page.locator('[data-test-id="hamburger-button"]');
  await expect(button).toBeVisible({ timeout: 10000 });
  
  // Get the menu content id from the button's aria-controls
  const menuContentId = await button.getAttribute('aria-controls');
  expect(menuContentId).toBeTruthy();
  console.log(`Menu content id: ${menuContentId}`);
  
  // Find menu content by id
  const menuContent = page.locator(`#${menuContentId}`);
  
  // Verify menu content exists but is initially hidden (display: none)
  await expect(menuContent).toBeAttached();
  const initialDisplay = await menuContent.evaluate(el => getComputedStyle(el).display);
  console.log(`Initial display style: ${initialDisplay}`);
  expect(initialDisplay).toBe('none');

  // Click to open
  console.log('Clicking hamburger button to open menu...');
  await button.click();
  
  // Wait a moment for the toggle to happen
  await page.waitForTimeout(100);
  
  // Verify menu content is now visible (display: block)
  const openDisplay = await menuContent.evaluate(el => getComputedStyle(el).display);
  console.log(`After open display style: ${openDisplay}`);
  expect(openDisplay).toBe('block');
  
  // Verify menu content has our test items
  await expect(page.getByText('Menu Item 1')).toBeVisible();

  // Click to close
  console.log('Clicking hamburger button to close menu...');
  await button.click();
  
  // Wait a moment for the toggle to happen
  await page.waitForTimeout(100);
  
  // Verify menu content is hidden again
  const closedDisplay = await menuContent.evaluate(el => getComputedStyle(el).display);
  console.log(`After close display style: ${closedDisplay}`);
  expect(closedDisplay).toBe('none');
});

test('hamburger menu button has correct accessibility attributes', async ({ page }) => {
  const baseURL = process.env.BASE_URL || 'http://localhost:8080';
  await page.goto(baseURL);
  
  const button = page.locator('[data-test-id="hamburger-button"]');
  await expect(button).toBeVisible({ timeout: 10000 });
  
  // Check accessibility attributes
  await expect(button).toHaveAttribute('role', 'button');
  await expect(button).toHaveAttribute('tabindex', '0');
  await expect(button).toHaveAttribute('aria-label', /Open menu|Close menu/);
  await expect(button).toHaveAttribute('aria-expanded', /true|false/);
  await expect(button).toHaveAttribute('aria-controls', /hamburger-menu-\d+/);
  
  // Check data-action contains toggle action
  const dataAction = await button.getAttribute('data-action');
  expect(dataAction).toBeTruthy();
  const action = JSON.parse(dataAction!);
  expect(action.type).toBe('toggle');
  expect(action.targetId).toMatch(/hamburger-menu-\d+/);
});
