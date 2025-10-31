/* eslint-disable no-undef */
// Summon JS test bootstrap for Node: install a Happy DOM window before modules load.
// This script is injected via NODE_OPTIONS --require so it executes prior to Kotlin modules.

const path = require('path');
const Module = require('module');

const jsNodeModules = path.resolve(__dirname, '../../../build/js/node_modules');
const wasmNodeModules = path.resolve(__dirname, '../../../build/wasm/node_modules');
const existingNodePath = process.env.NODE_PATH ? process.env.NODE_PATH.split(path.delimiter) : [];
const augmentedNodePath = Array.from(
    new Set(
        existingNodePath
            .concat([jsNodeModules, wasmNodeModules])
            .filter(Boolean)
    )
);
process.env.NODE_PATH = augmentedNodePath.join(path.delimiter);
Module._initPaths();
[jsNodeModules, wasmNodeModules].forEach((candidate) => {
    if (candidate && !module.paths.includes(candidate)) {
        module.paths.push(candidate);
    }
    if (candidate && !Module.globalPaths.includes(candidate)) {
        Module.globalPaths.push(candidate);
    }
});

let happyDomEntry;
try {
    happyDomEntry = require.resolve('happy-dom', {
        paths: [__dirname, jsNodeModules, wasmNodeModules, process.cwd()]
    });
} catch (resolveError) {
    console.error('[Summon JS] Unable to resolve happy-dom. NODE_PATH=', process.env.NODE_PATH);
    throw resolveError;
}
const {Window} = require(happyDomEntry);

if (typeof globalThis.__summonHappyDomInstalled === 'undefined') {
    const window = new Window();
    globalThis.__summonHappyDomInstalled = true;
    globalThis.window = window;
    globalThis.document = window.document;
    globalThis.self = window;
    globalThis.HTMLElement = window.HTMLElement;
    globalThis.SVGElement = window.SVGElement;
    globalThis.Element = window.Element;
    globalThis.Node = window.Node;
    globalThis.Text = window.Text;
    globalThis.DOMParser = window.DOMParser;
    globalThis.CustomEvent = window.CustomEvent;
    globalThis.MutationObserver = window.MutationObserver;
    globalThis.navigator = window.navigator;
    globalThis.performance = window.performance;
    globalThis.location = window.location;

    Object.getOwnPropertyNames(window)
        .filter((name) => /^[A-Z]/.test(name))
        .forEach((ctorName) => {
            const value = window[ctorName];
            if (typeof value === 'function' && typeof globalThis[ctorName] === 'undefined') {
                globalThis[ctorName] = value;
            }
        });

    if (typeof globalThis.screen === 'undefined') {
        globalThis.screen = window.screen;
    }

    if (typeof globalThis.requestAnimationFrame === 'undefined') {
        globalThis.requestAnimationFrame = window.requestAnimationFrame
            ? window.requestAnimationFrame.bind(window)
            : (callback) => setTimeout(callback, 16);
    }

    if (typeof globalThis.cancelAnimationFrame === 'undefined') {
        globalThis.cancelAnimationFrame = window.cancelAnimationFrame
            ? window.cancelAnimationFrame.bind(window)
            : (handle) => clearTimeout(handle);
    }

    if (typeof globalThis.setImmediate === 'undefined') {
        globalThis.setImmediate = (callback, ...args) => setTimeout(callback, 0, ...args);
    }

    if (typeof globalThis.clearImmediate === 'undefined') {
        globalThis.clearImmediate = (handle) => clearTimeout(handle);
    }
}
