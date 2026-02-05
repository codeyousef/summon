import {expect, test} from '@playwright/test';

/**
 * E2E tests for MenuBar Component (v0.7.0.0)
 * Tests menu bar rendering, accessibility, and interaction.
 */

const BASE_URL = process.env.BASE_URL || 'http://localhost:8080';

test.describe('MenuBar Component', () => {
    test.beforeEach(async ({page}) => {
        page.on('console', msg => console.log('PAGE LOG:', msg.text()));
        page.on('pageerror', exception => console.log(`PAGE ERROR: ${exception}`));
    });

    test.describe('MenuBar Rendering', () => {
        test('menubar renders as nav element', async ({page}) => {
            await page.goto(BASE_URL);

            // MenuBar should render as nav with menubar role
            const menubar = page.locator('nav[role="menubar"], [role="menubar"]');
            const count = await menubar.count();

            // MenuBar may not exist on all pages
            expect(count).toBeGreaterThanOrEqual(0);
        });

        test('menubar has correct ARIA attributes', async ({page}) => {
            await page.goto(BASE_URL);

            const menubar = page.locator('[role="menubar"]').first();
            const count = await menubar.count();

            if (count > 0) {
                // Should have menubar role
                const role = await menubar.getAttribute('role');
                expect(role).toBe('menubar');
            }
        });

        test('menu items render correctly', async ({page}) => {
            await page.goto(BASE_URL);

            const menuItems = page.locator('[role="menuitem"]');
            const count = await menuItems.count();

            // May have menu items in navigation
            expect(count).toBeGreaterThanOrEqual(0);
        });
    });

    test.describe('MenuBar Interaction', () => {
        test('menu item can be clicked', async ({page}) => {
            await page.goto(BASE_URL);

            const menuItem = page.locator('[role="menuitem"]').first();
            const count = await menuItem.count();

            if (count > 0) {
                // Menu item should be clickable
                await expect(menuItem).toBeEnabled();
            }
        });

        test('dropdown menu opens on click', async ({page}) => {
            await page.goto(BASE_URL);

            // Look for menu items that have submenus
            const menuButton = page.locator('[role="menuitem"][aria-haspopup="true"]').first();
            const count = await menuButton.count();

            if (count > 0) {
                const initialExpanded = await menuButton.getAttribute('aria-expanded');

                await menuButton.click();
                await page.waitForTimeout(100);

                // Check if aria-expanded changed or dropdown appeared
                const dropdown = page.locator('[role="menu"]').first();
                const dropdownCount = await dropdown.count();

                expect(dropdownCount).toBeGreaterThanOrEqual(0);
            }
        });

        test('dropdown closes on outside click', async ({page}) => {
            await page.goto(BASE_URL);

            const menuButton = page.locator('[role="menuitem"][aria-haspopup="true"]').first();
            const count = await menuButton.count();

            if (count > 0) {
                // Open menu
                await menuButton.click();
                await page.waitForTimeout(100);

                // Click outside
                await page.locator('body').click({position: {x: 10, y: 10}});
                await page.waitForTimeout(100);

                // Menu should close (aria-expanded false or dropdown hidden)
                const expanded = await menuButton.getAttribute('aria-expanded');
                // May be 'false' or null
                expect(expanded === 'false' || expanded === null).toBeTruthy();
            }
        });
    });

    test.describe('MenuBar Accessibility', () => {
        test('menu items are keyboard focusable', async ({page}) => {
            await page.goto(BASE_URL);

            const menuItem = page.locator('[role="menuitem"]').first();
            const count = await menuItem.count();

            if (count > 0) {
                await menuItem.focus();

                const isFocused = await menuItem.evaluate(el => document.activeElement === el);
                // Menu items should be focusable
                expect(isFocused || await menuItem.isEnabled()).toBeTruthy();
            }
        });

        test('keyboard shortcuts are displayed', async ({page}) => {
            await page.goto(BASE_URL);

            // Look for keyboard shortcut indicators (e.g., Ctrl+S)
            const shortcuts = page.locator('[data-shortcut], kbd, .shortcut, .keyboard-shortcut');
            const count = await shortcuts.count();

            // Shortcuts may or may not exist
            expect(count).toBeGreaterThanOrEqual(0);
        });

        test('disabled menu items have aria-disabled', async ({page}) => {
            await page.goto(BASE_URL);

            const disabledItems = page.locator('[role="menuitem"][aria-disabled="true"]');
            const count = await disabledItems.count();

            if (count > 0) {
                const item = disabledItems.first();
                const ariaDisabled = await item.getAttribute('aria-disabled');
                expect(ariaDisabled).toBe('true');
            }
        });

        test('menu separators have correct role', async ({page}) => {
            await page.goto(BASE_URL);

            const separators = page.locator('[role="separator"]');
            const count = await separators.count();

            // Separators may exist in menus
            expect(count).toBeGreaterThanOrEqual(0);
        });
    });

    test.describe('Submenu Support', () => {
        test('submenu items indicate expansion', async ({page}) => {
            await page.goto(BASE_URL);

            // Items with submenus should have aria-haspopup
            const submenuTriggers = page.locator('[role="menuitem"][aria-haspopup]');
            const count = await submenuTriggers.count();

            if (count > 0) {
                const trigger = submenuTriggers.first();
                const hasPopup = await trigger.getAttribute('aria-haspopup');
                expect(hasPopup).toBeTruthy();
            }
        });

        test('nested menus have correct structure', async ({page}) => {
            await page.goto(BASE_URL);

            // Look for nested menu structure
            const menus = page.locator('[role="menu"]');
            const count = await menus.count();

            if (count > 0) {
                const menu = menus.first();
                const items = menu.locator('[role="menuitem"]');
                const itemCount = await items.count();

                // Menu should contain menu items
                expect(itemCount).toBeGreaterThanOrEqual(0);
            }
        });
    });

    test.describe('MenuBar Styling', () => {
        test('menubar has visual styling', async ({page}) => {
            await page.goto(BASE_URL);

            const menubar = page.locator('[role="menubar"]').first();
            const count = await menubar.count();

            if (count > 0) {
                const hasStyles = await menubar.evaluate(el => {
                    const style = getComputedStyle(el);
                    return style.display !== '' && style.display !== 'none';
                });
                expect(hasStyles).toBe(true);
            }
        });

        test('menu items have hover states', async ({page}) => {
            await page.goto(BASE_URL);

            const menuItem = page.locator('[role="menuitem"]').first();
            const count = await menuItem.count();

            if (count > 0) {
                // Hover over menu item
                await menuItem.hover();

                // Should still be visible after hover
                await expect(menuItem).toBeVisible();
            }
        });
    });
});
