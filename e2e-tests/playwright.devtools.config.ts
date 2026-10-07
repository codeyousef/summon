import { defineConfig, devices } from '@playwright/test';
import path from 'node:path';

const root = path.resolve(__dirname);
const server = path.join(root, 'fixtures/private-suite/spa_server.py');
const fixture = path.join(root, 'fixtures/devtools');

export default defineConfig({
  testDir: './tests',
  testMatch: 'devtools-inspector.spec.ts',
  workers: 1,
  retries: 0,
  timeout: 45_000,
  expect: { timeout: 10_000 },
  reporter: [['line']],
  use: {
    trace: 'retain-on-failure',
    viewport: { width: 1280, height: 800 },
    locale: 'en-US',
    colorScheme: 'light',
    reducedMotion: 'reduce',
  },
  webServer: [
    {
      command: `SUMMON_BROWSER_PROFILE=development python3 ${server} --port 4180 --directory ${fixture}/build/dist/js/developmentExecutable`,
      url: 'http://127.0.0.1:4180/',
      reuseExistingServer: false,
    },
    {
      command: `SUMMON_BROWSER_PROFILE=development python3 ${server} --port 4181 --directory ${fixture}/build/dist/wasmJs/developmentExecutable`,
      url: 'http://127.0.0.1:4181/',
      reuseExistingServer: false,
    },
  ],
  projects: [
    { name: 'js-chromium', use: { ...devices['Desktop Chrome'], baseURL: 'http://127.0.0.1:4180' } },
    { name: 'js-firefox', use: { ...devices['Desktop Firefox'], baseURL: 'http://127.0.0.1:4180' } },
    { name: 'js-webkit', use: { ...devices['Desktop Safari'], baseURL: 'http://127.0.0.1:4180' } },
    { name: 'wasm-chromium', use: { ...devices['Desktop Chrome'], baseURL: 'http://127.0.0.1:4181' } },
    { name: 'wasm-firefox', use: { ...devices['Desktop Firefox'], baseURL: 'http://127.0.0.1:4181' } },
    { name: 'wasm-webkit', use: { ...devices['Desktop Safari'], baseURL: 'http://127.0.0.1:4181' } },
  ],
});
