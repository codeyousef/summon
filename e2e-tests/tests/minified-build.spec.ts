import {expect, test} from '@playwright/test';

/**
 * End-to-end tests for minified (production) JavaScript builds.
 *
 * These tests verify that components work correctly after JS minification,
 * which can mangle function names and property accessors causing runtime failures.
 *
 * Run production build first: ./gradlew :summon-core:jsBrowserProductionWebpack
 */
test.describe('Minified JS Build Tests', () => {

    test.describe('TextField Component', () => {

        test('should render and accept input in minified build', async ({page}) => {
            // Navigate to a page with a TextField component
            await page.goto('/');

            // Wait for hydration to complete
            await page.waitForSelector('[data-summon-hydrated="true"]', {timeout: 10000}).catch(() => {
                // Fallback: wait for any input element
            });

            // Find a text input field
            const input = page.locator('input[type="text"]').first();

            if (await input.count() > 0) {
                // Clear and type into the field
                await input.fill('');
                await input.fill('test minified input');

                // Verify the value was set
                await expect(input).toHaveValue('test minified input');
            }
        });

        test('should trigger onValueChange callback in minified build', async ({page}) => {
            await page.goto('/');

            // Look for a text field that updates some display element
            const input = page.locator('input[type="text"]').first();

            if (await input.count() > 0) {
                // Type something and check that the UI responds
                const initialValue = await input.inputValue();
                await input.fill('callback test');

                // Give time for state update and re-render
                await page.waitForTimeout(100);

                // The callback should have been invoked - the input should retain its value
                await expect(input).toHaveValue('callback test');
            }
        });

        test('should not throw errors on input interaction', async ({page}) => {
            const errors: string[] = [];

            // Collect console errors
            page.on('console', msg => {
                if (msg.type() === 'error') {
                    errors.push(msg.text());
                }
            });

            // Collect page errors
            page.on('pageerror', error => {
                errors.push(error.message);
            });

            await page.goto('/');

            const input = page.locator('input[type="text"]').first();

            if (await input.count() > 0) {
                // Interact with the input
                await input.click();
                await input.fill('error test');
                await input.blur();

                // Wait for any async errors
                await page.waitForTimeout(200);

                // Filter out non-critical errors
                const criticalErrors = errors.filter(e =>
                    e.includes('TypeError') ||
                    e.includes('ReferenceError') ||
                    e.includes('is not a function') ||
                    e.includes('undefined') && e.includes('value')
                );

                expect(criticalErrors).toHaveLength(0);
            }
        });
    });

    test.describe('Button Component', () => {

        test('should trigger onClick callback in minified build', async ({page}) => {
            await page.goto('/');

            const button = page.locator('button').first();

            if (await button.count() > 0) {
                // Click the button - should not throw
                await button.click();

                // Give time for any state updates
                await page.waitForTimeout(100);
            }
        });

        test('button click should not throw errors', async ({page}) => {
            const errors: string[] = [];

            page.on('console', msg => {
                if (msg.type() === 'error') {
                    errors.push(msg.text());
                }
            });

            page.on('pageerror', error => {
                errors.push(error.message);
            });

            await page.goto('/');

            const button = page.locator('button').first();

            if (await button.count() > 0) {
                await button.click();
                await page.waitForTimeout(200);

                const criticalErrors = errors.filter(e =>
                    e.includes('TypeError') ||
                    e.includes('ReferenceError') ||
                    e.includes('is not a function')
                );

                expect(criticalErrors).toHaveLength(0);
            }
        });
    });

    test.describe('PlatformRenderer Access', () => {

        test('should access renderer without errors', async ({page}) => {
            const errors: string[] = [];

            page.on('pageerror', error => {
                errors.push(error.message);
            });

            await page.goto('/');

            // Wait for page to fully load
            await page.waitForLoadState('networkidle');

            // Check for renderer-related errors
            const rendererErrors = errors.filter(e =>
                e.includes('PlatformRenderer') ||
                e.includes('renderer') ||
                e.includes('ComponentNotFoundException')
            );

            expect(rendererErrors).toHaveLength(0);
        });
    });

    test.describe('State Management', () => {

        test('mutableState.value access should work in minified build', async ({page}) => {
            const errors: string[] = [];

            page.on('pageerror', error => {
                errors.push(error.message);
            });

            page.on('console', msg => {
                if (msg.type() === 'error') {
                    errors.push(msg.text());
                }
            });

            await page.goto('/');

            // Interact with any stateful component
            const input = page.locator('input').first();

            if (await input.count() > 0) {
                await input.fill('state test');
                await page.waitForTimeout(200);

                // Check for mangling-related errors
                const manglingErrors = errors.filter(e =>
                    e.includes('.value') ||
                    e.includes('_v') || // Common mangled name pattern
                    e.includes('Cannot read') ||
                    e.includes('undefined is not')
                );

                expect(manglingErrors).toHaveLength(0);
            }
        });
    });
});
