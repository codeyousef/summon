@file:Suppress("UnsafeCastFromDynamic")

package codes.yousef.summon.js.testutil

private fun installDomImpl() {
    js(
        """(function() {
  if (typeof globalThis.__summonJsDomInstalled !== 'undefined') { return; }
  globalThis.__summonJsDomInstalled = true;
  var req = null;
  if (typeof require === 'function') {
    req = require;
  } else if (typeof globalThis.process !== 'undefined' && globalThis.process.mainModule && globalThis.process.mainModule.require) {
    req = globalThis.process.mainModule.require.bind(globalThis.process.mainModule);
  }
  if (!req) {
    try {
      var Module = eval('module');
      if (Module && typeof Module.createRequire === 'function') {
        req = Module.createRequire(process.cwd() + '/summon-runtime.mjs');
      }
    } catch (err) {}
  }
  if (!req) {
    console.warn('[Summon JS] Unable to resolve CommonJS require; DOM bootstrap skipped.');
    return;
  }
  var Window = req('happy-dom').Window;
  var window = new Window();
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
  globalThis.performance = window.performance;
  globalThis.screen = window.screen;
  if (!globalThis.requestAnimationFrame) {
    globalThis.requestAnimationFrame = window.requestAnimationFrame ? window.requestAnimationFrame.bind(window) : function(callback) { return setTimeout(callback, 16); };
  }
  if (!globalThis.cancelAnimationFrame) {
    globalThis.cancelAnimationFrame = window.cancelAnimationFrame ? window.cancelAnimationFrame.bind(window) : function(handle) { return clearTimeout(handle); };
  }
  if (typeof globalThis.navigator === 'undefined') {
    Object.defineProperty(globalThis, 'navigator', { value: window.navigator, configurable: true, enumerable: false, writable: false });
  }
})();"""
    )
}

private var domInstalled = false

internal fun ensureJsDom() {
    if (domInstalled) return
    installDomImpl()
    domInstalled = true
}
