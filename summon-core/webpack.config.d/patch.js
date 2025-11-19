
// Disable Node.js polyfills to prevent "process.release is undefined" errors
// and other Node.js-specific issues in the browser.
config.resolve = config.resolve || {};
config.resolve.fallback = config.resolve.fallback || {};
config.resolve.fallback.fs = false;
config.resolve.fallback.path = false;
config.resolve.fallback.crypto = false;
config.resolve.fallback.process = false;
