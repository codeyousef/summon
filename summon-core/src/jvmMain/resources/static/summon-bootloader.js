/**
 * Summon Hydration Bootloader
 * Handles browser detection, script loading, and hydration initialization.
 * This file is loaded with defer to avoid blocking the main thread.
 */
(function() {
    'use strict';

    // Performance monitoring
    var PerformanceTracker = {
        marks: {},
        measures: {},

        mark: function(name) {
            if (window.performance && performance.mark) {
                performance.mark(name);
                this.marks[name] = performance.now();
            }
        },

        measure: function(name, startMark, endMark) {
            if (window.performance && performance.measure) {
                try {
                    performance.measure(name, startMark, endMark);
                    var startTime = this.marks[startMark] || 0;
                    var endTime = endMark ? this.marks[endMark] : performance.now();
                    this.measures[name] = endTime - startTime;
                    return endTime - startTime;
                } catch (e) {
                    return 0;
                }
            }
            return 0;
        },

        getMetrics: function() {
            return {
                marks: this.marks,
                measures: this.measures,
                navigation: window.performance && performance.timing ? {
                    domContentLoaded: performance.timing.domContentLoadedEventEnd - performance.timing.navigationStart,
                    loadComplete: performance.timing.loadEventEnd - performance.timing.navigationStart
                } : null
            };
        }
    };

    // Browser capability detection
    var BrowserSupport = {
        wasm: false,
        wasmVersion: null,
        jsModules: false,
        serviceWorker: false,
        browserName: 'unknown',
        browserVersion: null,

        detect: function() {
            this.detectBrowser();
            this.detectWasm();
            this.detectFeatures();
        },

        detectBrowser: function() {
            var ua = navigator.userAgent;
            if (ua.indexOf('Chrome') !== -1) {
                this.browserName = 'chrome';
                var match = ua.match(/Chrome\/(\d+)/);
                this.browserVersion = match ? parseInt(match[1]) : null;
            } else if (ua.indexOf('Safari') !== -1 && ua.indexOf('Chrome') === -1) {
                this.browserName = 'safari';
                var match = ua.match(/Safari\/(\d+)/);
                this.browserVersion = match ? parseInt(match[1]) : null;
            } else if (ua.indexOf('Firefox') !== -1) {
                this.browserName = 'firefox';
                var match = ua.match(/Firefox\/(\d+)/);
                this.browserVersion = match ? parseInt(match[1]) : null;
            } else if (ua.indexOf('Edge') !== -1) {
                this.browserName = 'edge';
                var match = ua.match(/Edge\/(\d+)/);
                this.browserVersion = match ? parseInt(match[1]) : null;
            }
        },

        detectWasm: function() {
            try {
                if (typeof WebAssembly === 'object' && typeof WebAssembly.instantiate === 'function') {
                    this.wasm = true;
                    this.wasmVersion = typeof WebAssembly.Memory !== 'undefined' ? 'advanced' : 'mvp';
                    if (this.browserName === 'safari' && this.browserVersion < 15) {
                        this.wasm = false;
                    }
                }
            } catch (e) {
                this.wasm = false;
            }
        },

        detectFeatures: function() {
            this.jsModules = 'noModule' in HTMLScriptElement.prototype;
            this.serviceWorker = 'serviceWorker' in navigator;
        },

        shouldUseWasm: function() {
            return this.wasm && this.wasmVersion !== null;
        }
    };

    // Script loader with retry logic
    var ScriptLoader = {
        maxRetries: 3,
        fallbackUsed: false,

        loadScript: function(src, options) {
            options = options || {};
            return new Promise(function(resolve, reject) {
                var script = document.createElement('script');
                script.src = src;
                if (options.type) script.type = options.type;
                if (options.async !== undefined) script.async = options.async;
                if (options.defer !== undefined) script.defer = options.defer;
                script.onload = function() { resolve(script); };
                script.onerror = function() { reject(new Error('Failed to load: ' + src)); };
                document.head.appendChild(script);
            });
        },

        loadWithRetry: function(src, options) {
            var self = this;
            var attempts = 0;

            function attempt() {
                return self.loadScript(src, options).catch(function(error) {
                    attempts++;
                    if (attempts < self.maxRetries) {
                        return new Promise(function(resolve) {
                            setTimeout(resolve, Math.pow(2, attempts) * 1000);
                        }).then(attempt);
                    }
                    throw error;
                });
            }

            return attempt();
        },

        getBundleStrategy: function() {
            var useWasm = BrowserSupport.shouldUseWasm();
            return {
                useWasm: useWasm,
                bundlePath: useWasm ? '/summon-hydration.wasm.js' : '/summon-hydration.js',
                options: useWasm ? { type: 'module', async: true } : { async: true },
                fallbacks: useWasm ? [{ path: '/summon-hydration.js', options: { async: true } }] : []
            };
        },

        loadHydrationScript: function() {
            var self = this;
            // Keep in sync with SummonConstants.DEFAULT_ROOT_ELEMENT_ID in Kotlin
            var app = document.getElementById('summon-app');
            if (!app) return Promise.reject(new Error('summon-app not found'));

            PerformanceTracker.mark('hydration-loading-start');
            app.setAttribute('data-hydration-loading', 'true');

            var strategy = this.getBundleStrategy();

            return this.loadWithRetry(strategy.bundlePath, strategy.options)
                .catch(function(error) {
                    // Try fallbacks
                    var fallbackPromise = Promise.reject(error);
                    strategy.fallbacks.forEach(function(fallback) {
                        fallbackPromise = fallbackPromise.catch(function() {
                            self.fallbackUsed = true;
                            return self.loadWithRetry(fallback.path, fallback.options);
                        });
                    });
                    return fallbackPromise;
                })
                .then(function(script) {
                    var mode = self.fallbackUsed ? 'js-fallback' : (strategy.useWasm ? 'wasm' : 'js');
                    app.setAttribute('data-hydration-ready', 'true');
                    app.setAttribute('data-hydration-mode', mode);
                    app.removeAttribute('data-hydration-loading');
                    PerformanceTracker.mark('hydration-ready');

                    document.dispatchEvent(new CustomEvent('summon:hydration-ready', {
                        detail: { mode: mode, fallbackUsed: self.fallbackUsed }
                    }));

                    return script;
                })
                .catch(function(error) {
                    app.setAttribute('data-hydration-failed', 'true');
                    app.removeAttribute('data-hydration-loading');
                    document.dispatchEvent(new CustomEvent('summon:hydration-failed', {
                        detail: { error: error.message }
                    }));
                    throw error;
                });
        }
    };

    // Initialize
    function init() {
        PerformanceTracker.mark('summon-init-start');
        BrowserSupport.detect();
        PerformanceTracker.mark('browser-detection-complete');
        ScriptLoader.loadHydrationScript();
        PerformanceTracker.mark('init-complete');
    }

    // Error handling for hydration scripts
    window.addEventListener('error', function(event) {
        if (event.filename && (event.filename.indexOf('summon-hydration') !== -1 || event.filename.indexOf('wasm') !== -1)) {
            // Keep in sync with SummonConstants.DEFAULT_ROOT_ELEMENT_ID in Kotlin
            var app = document.getElementById('summon-app');
            if (app && !app.hasAttribute('data-hydration-ready')) {
                app.setAttribute('data-hydration-failed', 'true');
            }
        }
    });

    // Service Worker registration (non-blocking)
    if ('serviceWorker' in navigator) {
        window.addEventListener('load', function() {
            navigator.serviceWorker.register('/sw.js').catch(function() {});
        });
    }

    // Performance metrics event (non-blocking)
    window.addEventListener('load', function() {
        setTimeout(function() {
            document.dispatchEvent(new CustomEvent('summon:performance-metrics', {
                detail: {
                    metrics: PerformanceTracker.getMetrics(),
                    browserSupport: BrowserSupport,
                    timestamp: Date.now()
                }
            }));
        }, 1000);
    });

    // Expose for debugging
    window.SummonBrowserSupport = BrowserSupport;
    window.SummonScriptLoader = ScriptLoader;
    window.SummonPerformanceTracker = PerformanceTracker;

    // Export init function
    window.SummonBoot = { init: init };

    // Auto-initialize if DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
