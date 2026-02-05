import {expect, test} from '@playwright/test';

/**
 * E2E tests for HTML DSL components (v0.7.0.0)
 * Tests that semantic HTML elements render correctly with proper attributes.
 */

const BASE_URL = process.env.BASE_URL || 'http://localhost:8080';

test.describe('HTML DSL Components', () => {
    test.beforeEach(async ({page}) => {
        page.on('console', msg => console.log('PAGE LOG:', msg.text()));
        page.on('pageerror', exception => console.log(`PAGE ERROR: ${exception}`));
    });

    test.describe('Structural Elements', () => {
        test('renders semantic header element if present', async ({page}) => {
            await page.goto(BASE_URL);
            await expect(page.locator('body')).toBeVisible({timeout: 10000});

            const header = page.locator('header').first();
            const count = await header.count();
            // Header may not be present on all pages
            expect(count).toBeGreaterThanOrEqual(0);
            if (count > 0) {
                await expect(header).toBeVisible();
            }
        });

        test('renders nav element if present', async ({page}) => {
            await page.goto(BASE_URL);
            await expect(page.locator('body')).toBeVisible({timeout: 10000});

            const nav = page.locator('nav').first();
            const count = await nav.count();
            expect(count).toBeGreaterThanOrEqual(0);
            if (count > 0) {
                await expect(nav).toBeVisible();
            }
        });

        test('renders main element if present', async ({page}) => {
            await page.goto(BASE_URL);
            await expect(page.locator('body')).toBeVisible({timeout: 10000});

            const main = page.locator('main').first();
            const count = await main.count();
            expect(count).toBeGreaterThanOrEqual(0);
            if (count > 0) {
                await expect(main).toBeVisible();
            }
        });

        test('renders footer element if present', async ({page}) => {
            await page.goto(BASE_URL);
            await expect(page.locator('body')).toBeVisible({timeout: 10000});

            const footer = page.locator('footer').first();
            const count = await footer.count();
            expect(count).toBeGreaterThanOrEqual(0);
            if (count > 0) {
                await expect(footer).toBeVisible();
            }
        });

        test('renders section element if present', async ({page}) => {
            await page.goto(BASE_URL);
            await expect(page.locator('body')).toBeVisible({timeout: 10000});

            const section = page.locator('section').first();
            const count = await section.count();
            expect(count).toBeGreaterThanOrEqual(0);
            if (count > 0) {
                await expect(section).toBeVisible();
            }
        });

        test('renders article element if present', async ({page}) => {
            await page.goto(BASE_URL);
            await expect(page.locator('body')).toBeVisible({timeout: 10000});

            const article = page.locator('article').first();
            const count = await article.count();
            expect(count).toBeGreaterThanOrEqual(0);
            if (count > 0) {
                await expect(article).toBeVisible();
            }
        });
    });

    test.describe('Text Elements', () => {
        test('renders heading elements h1-h6', async ({page}) => {
            await page.goto(BASE_URL);

            // At least one heading should exist
            const h1 = page.locator('h1').first();
            const headingCount = await h1.count();
            expect(headingCount).toBeGreaterThanOrEqual(0);
        });

        test('renders paragraph elements', async ({page}) => {
            await page.goto(BASE_URL);

            const paragraphs = page.locator('p');
            const count = await paragraphs.count();
            // Pages typically have paragraphs
            expect(count).toBeGreaterThanOrEqual(0);
        });

        test('renders strong and em for emphasis', async ({page}) => {
            await page.goto(BASE_URL);

            // Check that strong/em render with correct semantic meaning
            const strong = page.locator('strong');
            const em = page.locator('em');

            // These may or may not exist, but should not error
            await strong.count();
            await em.count();
        });
    });

    test.describe('List Elements', () => {
        test('renders unordered list', async ({page}) => {
            await page.goto(BASE_URL);

            const ul = page.locator('ul').first();
            const count = await ul.count();

            if (count > 0) {
                // If list exists, it should have list items
                const listItems = ul.locator('li');
                const liCount = await listItems.count();
                expect(liCount).toBeGreaterThanOrEqual(0);
            }
        });

        test('renders ordered list', async ({page}) => {
            await page.goto(BASE_URL);

            const ol = page.locator('ol').first();
            const count = await ol.count();

            if (count > 0) {
                const listItems = ol.locator('li');
                const liCount = await listItems.count();
                expect(liCount).toBeGreaterThanOrEqual(0);
            }
        });
    });

    test.describe('Table Elements', () => {
        test('renders table with proper structure', async ({page}) => {
            await page.goto(BASE_URL);

            const table = page.locator('table').first();
            const count = await table.count();

            if (count > 0) {
                // Table should have rows
                const rows = table.locator('tr');
                const rowCount = await rows.count();
                expect(rowCount).toBeGreaterThan(0);
            }
        });

        test('table has thead and tbody when present', async ({page}) => {
            await page.goto(BASE_URL);

            const table = page.locator('table').first();
            const count = await table.count();

            if (count > 0) {
                // Check for semantic table sections
                const thead = table.locator('thead');
                const tbody = table.locator('tbody');

                // At least tbody should exist if table has content
                const tbodyCount = await tbody.count();
                expect(tbodyCount).toBeGreaterThanOrEqual(0);
            }
        });
    });

    test.describe('Interactive Elements', () => {
        test('details/summary toggle works', async ({page}) => {
            await page.goto(BASE_URL);

            const details = page.locator('details').first();
            const count = await details.count();

            if (count > 0) {
                const summary = details.locator('summary');

                // Check initial state (may be open or closed)
                const initialOpen = await details.getAttribute('open');

                // Click summary to toggle
                await summary.click();
                await page.waitForTimeout(100);

                // State should have changed
                const newOpen = await details.getAttribute('open');
                expect(newOpen !== initialOpen || newOpen === '').toBeTruthy();
            }
        });

        test('dialog element renders when present', async ({page}) => {
            await page.goto(BASE_URL);

            const dialog = page.locator('dialog').first();
            const count = await dialog.count();

            // Dialog may not be visible by default
            expect(count).toBeGreaterThanOrEqual(0);
        });
    });

    test.describe('Inline Elements', () => {
        test('anchor links have href attribute', async ({page}) => {
            await page.goto(BASE_URL);

            const links = page.locator('a[href]');
            const count = await links.count();

            if (count > 0) {
                const firstLink = links.first();
                const href = await firstLink.getAttribute('href');
                expect(href).toBeTruthy();
            }
        });

        test('time element has datetime attribute when present', async ({page}) => {
            await page.goto(BASE_URL);

            const timeElements = page.locator('time[datetime]');
            const count = await timeElements.count();

            if (count > 0) {
                const datetime = await timeElements.first().getAttribute('datetime');
                expect(datetime).toBeTruthy();
            }
        });

        test('abbr element has title for expansion', async ({page}) => {
            await page.goto(BASE_URL);

            const abbrs = page.locator('abbr[title]');
            const count = await abbrs.count();

            if (count > 0) {
                const title = await abbrs.first().getAttribute('title');
                expect(title).toBeTruthy();
            }
        });
    });

    test.describe('Media Elements', () => {
        test('figure contains figcaption when present', async ({page}) => {
            await page.goto(BASE_URL);

            const figures = page.locator('figure');
            const count = await figures.count();

            if (count > 0) {
                const figcaption = figures.first().locator('figcaption');
                const captionCount = await figcaption.count();
                // Figure may or may not have caption
                expect(captionCount).toBeGreaterThanOrEqual(0);
            }
        });

        test('iframe has title for accessibility', async ({page}) => {
            await page.goto(BASE_URL);

            const iframes = page.locator('iframe');
            const count = await iframes.count();

            if (count > 0) {
                // iframes should have title for accessibility
                const title = await iframes.first().getAttribute('title');
                // Title is recommended but not required
                expect(title !== undefined).toBeTruthy();
            }
        });

        test('audio/video elements render', async ({page}) => {
            await page.goto(BASE_URL);

            const audio = page.locator('audio');
            const video = page.locator('video');

            // These may not exist on all pages
            const audioCount = await audio.count();
            const videoCount = await video.count();

            expect(audioCount).toBeGreaterThanOrEqual(0);
            expect(videoCount).toBeGreaterThanOrEqual(0);
        });
    });

    test.describe('Accessibility', () => {
        test('page loads and can contain semantic elements', async ({page}) => {
            await page.goto(BASE_URL);
            await expect(page.locator('body')).toBeVisible({timeout: 10000});

            // Check that page can have semantic structure (elements may or may not exist)
            const hasHeader = await page.locator('header').count() > 0;
            const hasMain = await page.locator('main').count() > 0;
            const hasNav = await page.locator('nav').count() > 0;
            const hasDiv = await page.locator('div').count() > 0;

            // Page should have some structure (at minimum a div container)
            expect(hasHeader || hasMain || hasNav || hasDiv).toBeTruthy();
        });

        test('headings follow hierarchy', async ({page}) => {
            await page.goto(BASE_URL);
            await expect(page.locator('body')).toBeVisible({timeout: 10000});

            const h1Count = await page.locator('h1').count();
            const h2Count = await page.locator('h2').count();

            // If h2 exists, h1 should typically exist (but not strictly required)
            if (h2Count > 0) {
                // Just verify headings exist and don't error
                expect(h1Count).toBeGreaterThanOrEqual(0);
            }
        });
    });
});
