import { defineConfig, devices } from '@playwright/test';

const externalURL = process.env.BASE_URL;
const buildProfile = process.env.SUMMON_BROWSER_PROFILE ?? 'production';
if (buildProfile !== 'production' && buildProfile !== 'development') {
  throw new Error(`Unsupported SUMMON_BROWSER_PROFILE: ${buildProfile}`);
}
const executableDirectory = buildProfile === 'production' ? 'productionExecutable' : 'developmentExecutable';
const browsers = [
  { name: 'chromium', device: devices['Desktop Chrome'] },
  { name: 'firefox', device: devices['Desktop Firefox'] },
  // Automated WebKit qualifies production output only; real Safari remains a separate release check.
  ...(buildProfile === 'production'
    ? [{ name: 'webkit', device: devices['Desktop Safari'] }]
    : []),
];
const targets = externalURL
  ? [{ name: '', url: externalURL }]
  : [{ name: 'js-', url: 'http://127.0.0.1:8877' }, { name: 'wasm-', url: 'http://127.0.0.1:8878' }];

export default defineConfig({
  testDir: './tests',
  testMatch: 'private-suite-fixture.spec.ts',
  fullyParallel: false,
  workers: 1,
  forbidOnly: true,
  retries: 0,
  reporter: [['list'], ['html', { outputFolder: 'playwright-report/private-suite', open: 'never' }]],
  use: { headless: true, trace: 'retain-on-failure' },
  webServer: externalURL ? undefined : [
    {
      command: `python3 fixtures/private-suite/spa_server.py --port 8877 --directory fixtures/private-suite/build/dist/js/${executableDirectory}`,
      url: 'http://127.0.0.1:8877', reuseExistingServer: false, timeout: 10_000,
    },
    {
      command: `python3 fixtures/private-suite/spa_server.py --port 8878 --directory fixtures/private-suite/build/dist/wasmJs/${executableDirectory}`,
      url: 'http://127.0.0.1:8878', reuseExistingServer: false, timeout: 10_000,
    },
  ],
  projects: targets.flatMap(target => browsers.map(browser => ({
    name: target.name + browser.name,
    use: { ...browser.device, baseURL: target.url },
  }))),
});
