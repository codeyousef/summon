(function () {
    'use strict';

    function log(msg) {
        if (typeof console !== 'undefined') {
            console.log('[Summon][Hydration] ' + msg);
        }
    }

    function warn(msg) {
        if (typeof console !== 'undefined') {
            console.warn('[Summon][Hydration] ' + msg);
        }
    }

    function error(msg) {
        if (typeof console !== 'undefined') {
            console.error('[Summon][Hydration] ' + msg);
        }
    }

    // Find SSR root container emitted by Summon SSR
    function findSSRRoot() {
        var root = document.querySelector('[data-summon-hydration="root"]');
        if (!root) {
            root = document.getElementById('root');
        }
        if (!root) {
            // Fallback (shouldn't be necessary, but avoids crashes)
            root = document.body;
            warn('SSR root not found; falling back to document.body');
        }
        return root;
    }

    // Parse hydration data JSON embedded by the server
    function parseHydrationData() {
        var tag = document.getElementById('summon-hydration-data');
        if (!tag) {
            warn('No hydration data script tag found (id="summon-hydration-data")');
            return null;
        }
        try {
            var txt = tag.textContent || '';
            if (!txt.trim()) {
                warn('Hydration data tag is empty');
                return null;
            }
            return JSON.parse(txt);
        } catch (e) {
            error('Failed to parse hydration data: ' + e);
            return null;
        }
    }

    // Attach click callback behavior
    function attachClickHandlers(root, hydration) {
        // Prefer explicit callback registry if provided; otherwise, use data attributes
        var callbackMap = hydration && hydration.callbacks ? hydration.callbacks : null;
        // Convert array to set for faster lookup
        var callbackSet = null;
        if (callbackMap) {
            if (Array.isArray(callbackMap)) {
                callbackSet = new Set(callbackMap);
            } else {
                // If it's an object, use it as-is
                callbackSet = callbackMap;
            }
        }
        var clickable = root.querySelectorAll('[data-onclick-action="true"][data-onclick-id]');
        var count = 0;
        clickable.forEach(function (el) {
            var id = el.getAttribute('data-onclick-id');
            if (!id) return;
            // Check if callback ID exists in the registry
            if (callbackSet) {
                var exists = (callbackSet instanceof Set) ? callbackSet.has(id) : callbackSet[id];
                if (!exists) {
                    // If a registry is present and this id is missing, skip to avoid invalid calls
                    warn('Callback ID not found in hydration data: ' + id);
                    return;
                }
            }
            // Prevent double-binding
            if (el.__summon_hydrated_click__) return;
            el.addEventListener('click', function (ev) {
                try {
                    ev.preventDefault();
                } catch (_) {
                }
                // POST to server endpoint to execute callback; reload on success
                try {
                    var body = JSON.stringify({callbackId: id});
                    fetch('/summon/callback', {
                        method: 'POST',
                        headers: {'Content-Type': 'application/json'},
                        body: body,
                        credentials: 'same-origin'
                    }).then(function (res) {
                        if (res && res.ok) {
                            log('Callback executed: ' + id);
                            // Reload to reflect server-driven state updates
                            try {
                                window.location.reload();
                            } catch (_) {
                            }
                        } else {
                            warn('Callback failed: ' + id + (res ? (' (' + res.status + ')') : ''));
                        }
                    }).catch(function (err) {
                        error('Callback error for ' + id + ': ' + err);
                    });
                } catch (e) {
                    error('Callback dispatch threw for ' + id + ': ' + e);
                }
            }, {passive: true});
            el.__summon_hydrated_click__ = true;
            count++;
        });
        log('Attached ' + count + ' click handler(s)');
    }

    function hydrate() {
        var root = findSSRRoot();
        // Never clear or re-render here — only hydrate existing DOM
        var hydration = parseHydrationData();
        if (!hydration) {
            warn('Proceeding with minimal hydration (no callback registry)');
        }

        // Attach known event handlers
        attachClickHandlers(root, hydration);

        // Signal completion
        try {
            document.dispatchEvent(new CustomEvent('summon:hydration-complete'));
        } catch (_) {
        }
    }

    // Expose API and auto-run when DOM is ready
    if (!window.SummonHydration) {
        window.SummonHydration = {};
    }
    window.SummonHydration.hydrate = hydrate;

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function () {
            hydrate();
        });
    } else {
        // Already loaded
        hydrate();
    }
})();


