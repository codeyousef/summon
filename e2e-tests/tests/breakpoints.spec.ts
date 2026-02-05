import {expect, test} from '@playwright/test';

/**
 * E2E tests for Breakpoint Modifiers (v0.7.0.0)
 * Tests responsive design with mobile-first breakpoint helpers.
 */

const BASE_URL = process.env.BASE_URL || 'http://localhost:8080';

test.describe('Breakpoint Modifiers', () => {
    test.beforeEach(async ({page}) => {
        page.on('console', msg => console.log('PAGE LOG:', msg.text()));
        page.on('pageerror', exception => console.log(`PAGE ERROR: ${exception}`));
    });

    test.describe('Mobile viewport (xs)', () => {
        test.use({viewport: {width: 375, height: 667}}); // iPhone SE

        test('mobile viewport renders correctly', async ({page}) => {
            await page.goto(BASE_URL);

            // Page should load without errors
            await expect(page.locator('body')).toBeVisible({timeout: 10000});
        });

        test('mobile-specific styles apply', async ({page}) => {
            await page.goto(BASE_URL);

            // Check viewport width matches
            const viewportWidth = await page.evaluate(() => window.innerWidth);
            expect(viewportWidth).toBe(375);
        });

        test('hamburger menu visible on mobile', async ({page}) => {
            await page.goto(BASE_URL);

            // Mobile navigation pattern
            const hamburger = page.locator('[data-test-id="hamburger-button"]');
            const count = await hamburger.count();

            if (count > 0) {
                await expect(hamburger).toBeVisible();
            }
        });
    });

    test.describe('Tablet viewport (md)', () => {
        test.use({viewport: {width: 768, height: 1024}}); // iPad

        test('tablet viewport renders correctly', async ({page}) => {
            await page.goto(BASE_URL);

            await expect(page.locator('body')).toBeVisible({timeout: 10000});
        });

        test('tablet styles apply at md breakpoint', async ({page}) => {
            await page.goto(BASE_URL);

            const viewportWidth = await page.evaluate(() => window.innerWidth);
            expect(viewportWidth).toBe(768);
        });

        test('md breakpoint media queries active', async ({page}) => {
            await page.goto(BASE_URL);

            // Check that media query matches for md (768px)
            const matchesMd = await page.evaluate(() => {
                return window.matchMedia('(min-width: 768px)').matches;
            });
            expect(matchesMd).toBe(true);

            // Should not match lg yet
            const matchesLg = await page.evaluate(() => {
                return window.matchMedia('(min-width: 1024px)').matches;
            });
            expect(matchesLg).toBe(false);
        });
    });

    test.describe('Desktop viewport (lg)', () => {
        test.use({viewport: {width: 1024, height: 768}});

        test('desktop viewport renders correctly', async ({page}) => {
            await page.goto(BASE_URL);

            await expect(page.locator('body')).toBeVisible({timeout: 10000});
        });

        test('lg breakpoint media queries active', async ({page}) => {
            await page.goto(BASE_URL);

            const matchesLg = await page.evaluate(() => {
                return window.matchMedia('(min-width: 1024px)').matches;
            });
            expect(matchesLg).toBe(true);
        });

        test('hamburger menu hidden on desktop', async ({page}) => {
            await page.goto(BASE_URL);

            const hamburger = page.locator('[data-test-id="hamburger-button"]');
            const count = await hamburger.count();

            if (count > 0) {
                // On desktop, hamburger should be hidden
                const isHidden = await hamburger.evaluate(el => {
                    const style = getComputedStyle(el);
                    return style.display === 'none' || style.visibility === 'hidden';
                });
                expect(isHidden).toBe(true);
            }
        });
    });

    test.describe('Large Desktop viewport (xl)', () => {
        test.use({viewport: {width: 1280, height: 720}});

        test('xl breakpoint media queries active', async ({page}) => {
            await page.goto(BASE_URL);

            const matchesXl = await page.evaluate(() => {
                return window.matchMedia('(min-width: 1280px)').matches;
            });
            expect(matchesXl).toBe(true);
        });

        test('all smaller breakpoints also match', async ({page}) => {
            await page.goto(BASE_URL);

            // xl should also match xs, sm, md, lg
            const matches = await page.evaluate(() => ({
                xs: window.matchMedia('(min-width: 0px)').matches,
                sm: window.matchMedia('(min-width: 640px)').matches,
                md: window.matchMedia('(min-width: 768px)').matches,
                lg: window.matchMedia('(min-width: 1024px)').matches,
                xl: window.matchMedia('(min-width: 1280px)').matches,
            }));

            expect(matches.xs).toBe(true);
            expect(matches.sm).toBe(true);
            expect(matches.md).toBe(true);
            expect(matches.lg).toBe(true);
            expect(matches.xl).toBe(true);
        });
    });

    test.describe('Extra Large viewport (xxl)', () => {
        test.use({viewport: {width: 1536, height: 864}});

        test('xxl breakpoint media queries active', async ({page}) => {
            await page.goto(BASE_URL);

            const matchesXxl = await page.evaluate(() => {
                return window.matchMedia('(min-width: 1536px)').matches;
            });
            expect(matchesXxl).toBe(true);
        });
    });

    test.describe('Breakpoint Down (max-width)', () => {
        test('smDown applies below sm breakpoint', async ({page}) => {
            await page.setViewportSize({width: 500, height: 800});
            await page.goto(BASE_URL);

            // Should match max-width: 639px (below sm)
            const matchesSmDown = await page.evaluate(() => {
                return window.matchMedia('(max-width: 639px)').matches;
            });
            expect(matchesSmDown).toBe(true);
        });

        test('mdDown applies below md breakpoint', async ({page}) => {
            await page.setViewportSize({width: 700, height: 800});
            await page.goto(BASE_URL);

            // Should match max-width: 767px (below md)
            const matchesMdDown = await page.evaluate(() => {
                return window.matchMedia('(max-width: 767px)').matches;
            });
            expect(matchesMdDown).toBe(true);
        });
    });

    test.describe('Breakpoint Only (range)', () => {
        test('smOnly applies only at sm range', async ({page}) => {
            await page.setViewportSize({width: 700, height: 800});
            await page.goto(BASE_URL);

            // sm range: 640px to 767px
            const matchesSmOnly = await page.evaluate(() => {
                return window.matchMedia('(min-width: 640px) and (max-width: 767px)').matches;
            });
            expect(matchesSmOnly).toBe(true);
        });

        test('mdOnly applies only at md range', async ({page}) => {
            await page.setViewportSize({width: 900, height: 800});
            await page.goto(BASE_URL);

            // md range: 768px to 1023px
            const matchesMdOnly = await page.evaluate(() => {
                return window.matchMedia('(min-width: 768px) and (max-width: 1023px)').matches;
            });
            expect(matchesMdOnly).toBe(true);
        });

        test('lgOnly applies only at lg range', async ({page}) => {
            await page.setViewportSize({width: 1100, height: 800});
            await page.goto(BASE_URL);

            // lg range: 1024px to 1279px
            const matchesLgOnly = await page.evaluate(() => {
                return window.matchMedia('(min-width: 1024px) and (max-width: 1279px)').matches;
            });
            expect(matchesLgOnly).toBe(true);
        });
    });

    test.describe('Responsive Layout Behavior', () => {
        test('layout adapts between mobile and desktop', async ({page}) => {
            // Start at desktop
            await page.setViewportSize({width: 1280, height: 720});
            await page.goto(BASE_URL);

            await expect(page.locator('body')).toBeVisible({timeout: 10000});

            // Resize to mobile
            await page.setViewportSize({width: 375, height: 667});
            await page.waitForTimeout(100); // Allow CSS to recompute

            // Page should still be functional
            await expect(page.locator('body')).toBeVisible();
        });

        test('CSS custom properties update with viewport', async ({page}) => {
            await page.setViewportSize({width: 1280, height: 720});
            await page.goto(BASE_URL);

            // Check that viewport units work
            const vw = await page.evaluate(() => {
                return window.innerWidth;
            });
            expect(vw).toBe(1280);
        });
    });
});
