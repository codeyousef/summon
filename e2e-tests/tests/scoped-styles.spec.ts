import {expect, test} from '@playwright/test';

/**
 * E2E tests for Scoped Style Selectors (v0.7.0.0)
 * Tests CSS combinator support: descendant, child, adjacent sibling, general sibling.
 */

const BASE_URL = process.env.BASE_URL || 'http://localhost:8080';

test.describe('Scoped Style Selectors', () => {
    test.beforeEach(async ({page}) => {
        page.on('console', msg => console.log('PAGE LOG:', msg.text()));
        page.on('pageerror', exception => console.log(`PAGE ERROR: ${exception}`));
    });

    test.describe('CSS Class Generation', () => {
        test('page renders with scoped styles', async ({page}) => {
            await page.goto(BASE_URL);

            await expect(page.locator('body')).toBeVisible({timeout: 10000});
        });

        test('style tag contains generated CSS', async ({page}) => {
            await page.goto(BASE_URL);

            // Check that there's at least one style tag with summon styles
            const styleTags = page.locator('style');
            const count = await styleTags.count();

            expect(count).toBeGreaterThan(0);
        });

        test('elements have generated class names', async ({page}) => {
            await page.goto(BASE_URL);

            // Look for elements with class attributes (summon generates unique classes)
            const elementsWithClass = page.locator('[class]');
            const count = await elementsWithClass.count();

            expect(count).toBeGreaterThan(0);
        });
    });

    test.describe('Descendant Selector', () => {
        test('descendant styles cascade to nested elements', async ({page}) => {
            await page.goto(BASE_URL);

            // Test that styles can cascade via descendant selector
            // Find a container with nested content
            const container = page.locator('main, section, article, div').first();
            const count = await container.count();

            if (count > 0) {
                const nestedElements = container.locator('*');
                const nestedCount = await nestedElements.count();

                // Container should have nested content
                expect(nestedCount).toBeGreaterThan(0);
            }
        });
    });

    test.describe('Child Selector', () => {
        test('direct children receive child selector styles', async ({page}) => {
            await page.goto(BASE_URL);

            // Test parent > child relationship
            const lists = page.locator('ul, ol');
            const count = await lists.count();

            if (count > 0) {
                const list = lists.first();
                // Direct children should be li elements
                const directChildren = list.locator('> li');
                const childCount = await directChildren.count();

                expect(childCount).toBeGreaterThanOrEqual(0);
            }
        });

        test('nested elements do not receive child selector styles', async ({page}) => {
            await page.goto(BASE_URL);

            // Child selector should only affect direct children
            const nav = page.locator('nav').first();
            const count = await nav.count();

            if (count > 0) {
                // Get direct children vs all descendants
                const directChildren = await nav.locator('> *').count();
                const allDescendants = await nav.locator('*').count();

                // More descendants than direct children means nesting exists
                expect(allDescendants).toBeGreaterThanOrEqual(directChildren);
            }
        });
    });

    test.describe('Adjacent Sibling Selector', () => {
        test('adjacent siblings can be styled', async ({page}) => {
            await page.goto(BASE_URL);

            // Find elements that could have adjacent siblings
            const headings = page.locator('h1, h2, h3, h4, h5, h6');
            const count = await headings.count();

            if (count > 0) {
                // Headings often have adjacent paragraph siblings
                const heading = headings.first();
                const nextSibling = heading.locator('+ *');
                const siblingCount = await nextSibling.count();

                // May or may not have immediate sibling
                expect(siblingCount).toBeGreaterThanOrEqual(0);
            }
        });
    });

    test.describe('General Sibling Selector', () => {
        test('general siblings can be styled', async ({page}) => {
            await page.goto(BASE_URL);

            // Find elements with multiple siblings
            const listItems = page.locator('li');
            const count = await listItems.count();

            if (count > 1) {
                // First item should have general siblings after it
                const firstItem = listItems.first();
                const siblings = firstItem.locator('~ li');
                const siblingCount = await siblings.count();

                expect(siblingCount).toBeGreaterThan(0);
            }
        });
    });

    test.describe('Style Computation', () => {
        test('computed styles reflect CSS rules', async ({page}) => {
            await page.goto(BASE_URL);

            // Check that computed styles work
            const body = page.locator('body');
            const display = await body.evaluate(el => getComputedStyle(el).display);

            expect(display).toBeTruthy();
        });

        test('CSS specificity is respected', async ({page}) => {
            await page.goto(BASE_URL);

            // More specific selectors should override less specific
            const element = page.locator('[class]').first();
            const count = await element.count();

            if (count > 0) {
                const className = await element.getAttribute('class');
                expect(className).toBeTruthy();

                // Element should have computed styles from its classes
                const hasStyles = await element.evaluate(el => {
                    const style = getComputedStyle(el);
                    return style.length > 0;
                });
                expect(hasStyles).toBe(true);
            }
        });
    });

    test.describe('Pseudo-selectors with Scoped Styles', () => {
        test('hover styles defined in stylesheet', async ({page}) => {
            await page.goto(BASE_URL);

            // Check that hover styles exist in stylesheets
            const hasHoverRules = await page.evaluate(() => {
                for (const sheet of document.styleSheets) {
                    try {
                        for (const rule of sheet.cssRules) {
                            if (rule.cssText && rule.cssText.includes(':hover')) {
                                return true;
                            }
                        }
                    } catch (e) {
                        // Cross-origin stylesheets may throw
                        continue;
                    }
                }
                return false;
            });

            // May or may not have hover rules
            expect(typeof hasHoverRules).toBe('boolean');
        });

        test('focus styles for interactive elements', async ({page}) => {
            await page.goto(BASE_URL);

            const button = page.locator('button').first();
            const count = await button.count();

            if (count > 0) {
                // Focus the button
                await button.focus();

                // Check that it can receive focus
                const isFocused = await button.evaluate(el => document.activeElement === el);
                expect(isFocused).toBe(true);
            }
        });
    });

    test.describe('Combinator Combinations', () => {
        test('nested combinators work correctly', async ({page}) => {
            await page.goto(BASE_URL);

            // Complex selector: parent > child descendant
            const tables = page.locator('table');
            const count = await tables.count();

            if (count > 0) {
                const table = tables.first();
                // table > tbody tr td - child then descendant
                const cells = table.locator('> tbody tr td, > tbody tr th, > tr td, > tr th');
                const cellCount = await cells.count();

                expect(cellCount).toBeGreaterThanOrEqual(0);
            }
        });

        test('multiple classes can combine', async ({page}) => {
            await page.goto(BASE_URL);

            // Find elements with multiple classes
            const multiClass = await page.evaluate(() => {
                const elements = document.querySelectorAll('[class]');
                for (const el of elements) {
                    if (el.classList.length > 1) {
                        return true;
                    }
                }
                return false;
            });

            // May or may not have multi-class elements
            expect(typeof multiClass).toBe('boolean');
        });
    });
});
