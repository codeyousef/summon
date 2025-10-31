/**
 * Test stubs for WASM external functions.
 * This file provides stub implementations for testing when running in Node.js test environment.
 */

// Check if we're in Node.js test environment
if (typeof global !== 'undefined' && typeof window === 'undefined') {
    // We're in Node.js, create global stubs
    global.wasmConsoleLog = function (message) {
        console.log('[Test WASM]', message);
    };

    global.wasmConsoleWarn = function (message) {
        console.warn('[Test WASM]', message);
    };

    global.wasmConsoleError = function (message) {
        console.error('[Test WASM]', message);
    };

    global.wasmConsoleDebug = function (message) {
        console.debug('[Test WASM]', message);
    };

    // Stub DOM functions
    global.wasmGetElementById = function (id) {
        return null; // In tests, elements don't exist
    };

    global.wasmCreateElementById = function (tagName) {
        return `test-${tagName}-${Date.now()}`;
    };

    global.wasmSetElementAttribute = function (elementId, name, value) {
        // No-op in tests
    };

    global.wasmGetElementAttribute = function (elementId, name) {
        return null;
    };

    global.wasmSetElementTextContent = function (elementId, text) {
        // No-op in tests
    };

    global.wasmGetElementTextContent = function (elementId) {
        return '';
    };

    global.wasmSetElementInnerHTML = function (elementId, html) {
        // No-op in tests
    };

    global.wasmGetElementInnerHTML = function (elementId) {
        return '';
    };

    global.wasmAppendChildById = function (parentId, childId) {
        // No-op in tests
    };

    global.wasmRemoveChildById = function (parentId, childId) {
        // No-op in tests
    };

    global.wasmSetElementValue = function (elementId, value) {
        // No-op in tests
    };

    global.wasmGetElementValue = function (elementId) {
        return '';
    };

    global.wasmClickElement = function (elementId) {
        return true;
    };

    global.__summonEnsureRoot = function (rootId) {
        if (typeof document === 'undefined') {
            return;
        }
        let root = document.getElementById(rootId);
        if (!root) {
            root = document.createElement('div');
            root.id = rootId;
            if (document.body) {
                document.body.appendChild(root);
            }
        }
    };

    global.wasmAddClassToElement = function (elementId, className) {
        // No-op in tests
    };

    global.wasmRemoveClassFromElement = function (elementId, className) {
        // No-op in tests
    };

    global.wasmAddEventListenerById = function (elementId, eventType, handlerId) {
        return true; // Simulate success
    };

    global.wasmAddEventHandler = function (elementId, eventType, handlerId) {
        return true; // Simulate success
    };

    global.wasmRemoveEventListenerById = function (elementId, eventType, handlerId) {
        // No-op in tests
    };

    global.wasmQuerySelectorGetId = function (selector) {
        return null;
    };

    global.wasmQuerySelectorAllGetIds = function (selector) {
        return '';
    };

    global.wasmGetElementParent = function (elementId) {
        return null;
    };

    global.wasmInsertBeforeById = function (parentId, newChildId, referenceId) {
        // No-op in tests
    };

    global.wasmHistoryPushState = function (state, title, url) {
        // No-op in tests
    };

    global.wasmHistoryReplaceState = function (state, title, url) {
        // No-op in tests
    };

    global.wasmGetLocationHref = function () {
        return 'http://test.local';
    };

    global.wasmSetLocationHref = function (href) {
        // No-op in tests
    };

    global.wasmGetUserAgent = function () {
        return 'TestBrowser/1.0';
    };

    global.wasmPerformanceNow = function () {
        return Date.now();
    };

    global.wasmGetCurrentTime = function () {
        return Date.now();
    };

    global.wasmGetTimestamp = function () {
        return new Date().toISOString();
    };

    global.wasmRequestAnimationFrame = function (callback) {
        // Simulate immediate callback in tests
        setTimeout(callback, 0);
        return Math.random();
    };

    global.wasmCancelAnimationFrame = function (id) {
        // No-op in tests
    };

    global.wasmScrollIntoView = function (elementId, behavior) {
        // No-op in tests
    };
}

// Also provide them on window if it exists (browser environment)
if (typeof window !== 'undefined') {
    // Copy all the stubs to window as well
    window.wasmConsoleLog = global.wasmConsoleLog || function (message) {
        console.log('[Test WASM]', message);
    };

    window.wasmConsoleWarn = global.wasmConsoleWarn || function (message) {
        console.warn('[Test WASM]', message);
    };

    window.wasmConsoleError = global.wasmConsoleError || function (message) {
        console.error('[Test WASM]', message);
    };

    window.wasmConsoleDebug = global.wasmConsoleDebug || function (message) {
        console.debug('[Test WASM]', message);
    };

    // Add other stubs as needed...
}
