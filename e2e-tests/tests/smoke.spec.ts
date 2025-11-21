import { test, expect } from '@playwright/test';

test('smoke test', async ({ page }) => {
  page.on('console', msg => console.log('PAGE LOG:', msg.text()));
  page.on('pageerror', exception => console.log(`PAGE ERROR: ${exception}`));

  const baseURL = process.env.BASE_URL || 'http://localhost:8080';
  console.log(`Navigating to ${baseURL}`);
  await page.goto(baseURL);
  
  // Wait for content to load
  await expect(page.getByText(/Welcome to|Hello,/)).toBeVisible({ timeout: 10000 });
  
  // Find the button
  const button = page.getByRole('button', { name: /Click me|Add one/i });
  await expect(button).toBeVisible();
  
  // Click it
  await button.click();
  
  // Verify state change
  try {
      await expect(page.getByText(/Count: 1|clicked 1 time/)).toBeVisible({ timeout: 30000 });
  } catch (e) {
      console.log('Page content:', await page.content());
      throw e;
  }
});
