@file:OptIn(ExperimentalWasmJsInterop::class)

package codes.yousef.summon.test

@Suppress("UnsafeCastFromDynamic")
private fun installNodeDomImpl(): Unit =
    js(
        """
        (function() {
      if (typeof globalThis.__summonNodeDomInstalled !== 'undefined') {
        return;
      }
      globalThis.__summonNodeDomInstalled = true;

      let req = null;
      if (typeof require === 'function') {
        req = require;
      } else if (typeof globalThis.process !== 'undefined' && globalThis.process.mainModule && globalThis.process.mainModule.require) {
        req = globalThis.process.mainModule.require.bind(globalThis.process.mainModule);
      }

      if (!req) {
        try {
          const Module = eval('module');
          if (Module && typeof Module.createRequire === 'function') {
            req = Module.createRequire(process.cwd() + '/summon-runtime.mjs');
          }
        } catch (err) {
          // Ignore
        }
      }

      if (!req) {
        console.warn('[Summon WASM] Unable to resolve CommonJS require; DOM bootstrap skipped.');
        return;
      }

      const { Window } = req('happy-dom');
      const window = new Window();

      globalThis.window = window;
      globalThis.document = window.document;
      globalThis.self = window;

      try {
        delete globalThis.navigator;
      } catch (err) {
        // ignore if deletion fails
      }
      Object.defineProperty(globalThis, 'navigator', {
        value: window.navigator,
        configurable: true,
        enumerable: false,
        writable: false
      });
      globalThis.HTMLElement = window.HTMLElement;
      globalThis.SVGElement = window.SVGElement;
      globalThis.Element = window.Element;
      globalThis.Node = window.Node;
      globalThis.Text = window.Text;
      globalThis.DOMParser = window.DOMParser;
      globalThis.Event = window.Event;
      globalThis.MouseEvent = window.MouseEvent;
      globalThis.CustomEvent = window.CustomEvent;
      globalThis.MutationObserver = window.MutationObserver;
      globalThis.performance = window.performance;
      globalThis.screen = window.screen;
      globalThis.getComputedStyle = window.getComputedStyle.bind(window);

      if (!globalThis.requestAnimationFrame) {
        globalThis.requestAnimationFrame = window.requestAnimationFrame
          ? window.requestAnimationFrame.bind(window)
          : (callback) => setTimeout(callback, 16);
      }

      if (!globalThis.cancelAnimationFrame) {
        globalThis.cancelAnimationFrame = window.cancelAnimationFrame
          ? window.cancelAnimationFrame.bind(window)
          : (handle) => clearTimeout(handle);
      }

      if (!window.crypto || !window.crypto.subtle) {
        const cryptoModule = req('crypto');
        const webcrypto = cryptoModule.webcrypto || cryptoModule;
        window.crypto = webcrypto;
        globalThis.crypto = webcrypto;
      }

      if (!globalThis.localStorage) {
        const storage = new Map();
        globalThis.localStorage = {
          getItem: key => (storage.has(key) ? storage.get(key) : null),
          setItem: (key, value) => storage.set(key, String(value)),
          removeItem: key => storage.delete(key),
          clear: () => storage.clear()
        };
      }

      if (!globalThis.sessionStorage) {
        const storage = new Map();
        globalThis.sessionStorage = {
          getItem: key => (storage.has(key) ? storage.get(key) : null),
          setItem: (key, value) => storage.set(key, String(value)),
          removeItem: key => storage.delete(key),
          clear: () => storage.clear()
        };
      }

      if (typeof globalThis.wasmGetElementById === 'undefined') {
        const fs = req('fs');
        const path = req('path');
        const vm = req('vm');
        const candidates = [
          path.resolve(process.cwd(), 'summon-core/src/wasmJsMain/resources/summon-wasm-init.js'),
          path.resolve(process.cwd(), 'src/wasmJsMain/resources/summon-wasm-init.js')
        ];

        for (const candidate of candidates) {
          if (fs.existsSync(candidate)) {
            const source = fs.readFileSync(candidate, 'utf8');
            vm.runInThisContext(source, { filename: 'summon-wasm-init.js' });
            break;
          }
        }

        if (typeof globalThis.wasmGetElementById === 'undefined') {
          console.warn('[Summon WASM] summon-wasm-init.js not loaded; bridge functions may be missing.');
        }
      }
        })()
        """
    )

private var nodeDomInstalled = false

@OptIn(ExperimentalWasmJsInterop::class)
internal fun ensureWasmNodeDom() {
    if (nodeDomInstalled) {
        return
    }
    try {
        installNodeDomImpl()
        nodeDomInstalled = true
    } catch (t: Throwable) {
        nodeDomInstalled = false
        throw t
    }
}
