/**
 * Summon Framework WASM Bridge
 * This file is automatically included with the WASM bundle and provides
 * the JavaScript implementations for Kotlin/WASM external functions.
 *
 * Users do not need to include this manually - it's part of the framework.
 */

(function() {
    'use strict';

    // Element storage - maps element IDs to actual DOM elements
    const elementStore = new Map();
    let elementIdCounter = 0;
    const eventHandlers = new Map();
    const eventCallbacks = new Map();
    const animationFrameCallbacks = new Map();

    // Helper function to get or create element store entry
    function getElement(elementId) {
        if (elementStore.has(elementId)) {
            return elementStore.get(elementId);
        }
        // Try to find element in DOM
        const element = document.getElementById(elementId);
        if (element) {
            elementStore.set(elementId, element);
            return element;
        }
        return null;
    }

    // Helper to store element and return its ID
    function storeElement(element) {
        const elementId = element.id || `wasm-elem-${++elementIdCounter}`;
        if (!element.id) {
            element.id = elementId;
        }
        elementStore.set(elementId, element);
        return elementId;
    }

    // Element creation and basic manipulation
    window.wasmCreateElementById = function(tagName) {
        try {
            const element = document.createElement(tagName);
            return storeElement(element);
        } catch (e) {
            console.error('[Summon WASM] createElement failed:', e);
            return '';
        }
    };

    window.wasmGetElementById = function(id) {
        const element = document.getElementById(id);
        return element ? storeElement(element) : null;
    };

    window.wasmSetElementAttribute = function(elementId, name, value) {
        try {
            const element = getElement(elementId);
            if (element) {
                element.setAttribute(name, value);
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] setAttribute failed:', e);
            return false;
        }
    };

    window.wasmGetElementAttribute = function(elementId, name) {
        try {
            const element = getElement(elementId);
            return element ? element.getAttribute(name) : null;
        } catch (e) {
            console.error('[Summon WASM] getAttribute failed:', e);
            return null;
        }
    };

    window.wasmRemoveElementAttribute = function(elementId, name) {
        try {
            const element = getElement(elementId);
            if (element) {
                element.removeAttribute(name);
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] removeAttribute failed:', e);
            return false;
        }
    };

    // Text content manipulation
    window.wasmSetElementTextContent = function(elementId, text) {
        try {
            const element = getElement(elementId);
            if (element) {
                element.textContent = text;
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] setTextContent failed:', e);
            return false;
        }
    };

    window.wasmGetElementTextContent = function(elementId) {
        try {
            const element = getElement(elementId);
            return element ? element.textContent : null;
        } catch (e) {
            console.error('[Summon WASM] getTextContent failed:', e);
            return null;
        }
    };

    window.wasmSetElementInnerHTML = function(elementId, html) {
        try {
            const element = getElement(elementId);
            if (element) {
                element.innerHTML = html;
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] setInnerHTML failed:', e);
            return false;
        }
    };

    window.wasmGetElementInnerHTML = function(elementId) {
        try {
            const element = getElement(elementId);
            return element ? element.innerHTML : null;
        } catch (e) {
            console.error('[Summon WASM] getInnerHTML failed:', e);
            return null;
        }
    };

    // DOM tree manipulation
    window.wasmAppendChildById = function(parentId, childId) {
        try {
            const parent = getElement(parentId);
            const child = getElement(childId);
            if (parent && child) {
                parent.appendChild(child);
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] appendChild failed:', e);
            return false;
        }
    };

    window.wasmRemoveChildById = function(parentId, childId) {
        try {
            const parent = getElement(parentId);
            const child = getElement(childId);
            if (parent && child && parent.contains(child)) {
                parent.removeChild(child);
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] removeChild failed:', e);
            return false;
        }
    };

    window.wasmRemoveElementById = function(elementId) {
        try {
            const element = getElement(elementId);
            if (element && element.parentNode) {
                element.parentNode.removeChild(element);
                elementStore.delete(elementId);
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] removeElement failed:', e);
            return false;
        }
    };

    window.wasmClickElement = function (elementId) {
        try {
            const element = getElement(elementId);
            if (element && typeof element.click === 'function') {
                element.click();
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] clickElement failed:', e);
            return false;
        }
    };

    // CSS class manipulation
    window.wasmAddClassToElement = function(elementId, className) {
        try {
            const element = getElement(elementId);
            if (element) {
                element.classList.add(...className.split(' '));
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] addClass failed:', e);
            return false;
        }
    };

    window.wasmRemoveClassFromElement = function(elementId, className) {
        try {
            const element = getElement(elementId);
            if (element) {
                element.classList.remove(...className.split(' '));
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] removeClass failed:', e);
            return false;
        }
    };

    window.wasmElementHasClass = function(elementId, className) {
        try {
            const element = getElement(elementId);
            return element ? element.classList.contains(className) : false;
        } catch (e) {
            console.error('[Summon WASM] hasClass failed:', e);
            return false;
        }
    };

    window.wasmGetElementClassName = function(elementId) {
        try {
            const element = getElement(elementId);
            return element ? element.className : null;
        } catch (e) {
            console.error('[Summon WASM] getClassName failed:', e);
            return null;
        }
    };

    // Element properties
    window.wasmGetElementTagName = function(elementId) {
        try {
            const element = getElement(elementId);
            return element ? element.tagName.toLowerCase() : null;
        } catch (e) {
            console.error('[Summon WASM] getTagName failed:', e);
            return null;
        }
    };

    window.wasmGetElementParent = function(elementId) {
        try {
            const element = getElement(elementId);
            if (element && element.parentElement) {
                return storeElement(element.parentElement);
            }
            return null;
        } catch (e) {
            console.error('[Summon WASM] getElementParent failed:', e);
            return null;
        }
    };

    window.wasmGetElementId = function(elementId) {
        try {
            const element = getElement(elementId);
            return element ? element.id : null;
        } catch (e) {
            console.error('[Summon WASM] getId failed:', e);
            return null;
        }
    };

    window.wasmSetElementId = function(elementId, newId) {
        try {
            const element = getElement(elementId);
            if (element) {
                element.id = newId;
                // Update the store mapping
                elementStore.delete(elementId);
                elementStore.set(newId, element);
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] setId failed:', e);
            return false;
        }
    };

    // Element hierarchy
    window.wasmGetElementParentId = function(elementId) {
        try {
            const element = getElement(elementId);
            if (element && element.parentElement) {
                return storeElement(element.parentElement);
            }
            return null;
        } catch (e) {
            console.error('[Summon WASM] getParentId failed:', e);
            return null;
        }
    };

    window.wasmGetElementChildren = function(elementId) {
        try {
            const element = getElement(elementId);
            if (element) {
                const childIds = Array.from(element.children).map(child => storeElement(child));
                return childIds.join(',');
            }
            return '';
        } catch (e) {
            console.error('[Summon WASM] getChildren failed:', e);
            return '';
        }
    };

    // Event handling
    window.wasmAddEventListenerById = function(elementId, eventType, handlerId) {
        try {
            const element = getElement(elementId);
            if (!element) {
                return false;
            }

            let entry = eventHandlers.get(handlerId);
            if (!entry) {
                entry = {elementId, eventType, listener: null, lastEvent: null};
                eventHandlers.set(handlerId, entry);
            } else if (entry.listener) {
                element.removeEventListener(eventType, entry.listener);
            }

            const listener = (event) => {
                entry.lastEvent = {
                    type: event.type,
                    targetId: elementId,
                    value: event && event.target ? (event.target.value ?? '') : '',
                    checked: event && event.target ? !!event.target.checked : false,
                    event
                };

                const callback = eventCallbacks.get(handlerId);
                console.log('[Summon WASM] Event triggered:', event.type, 'on element:', elementId, 'handler:', handlerId, 'callback found:', !!callback);
                if (callback) {
                    try {
                        callback();
                    } catch (err) {
                        console.error('[Summon WASM] Event callback failed:', err);
                    }
                } else if (typeof wasmExecuteCallback === 'function') {
                    try {
                        wasmExecuteCallback(handlerId);
                    } catch (err) {
                        console.error('[Summon WASM] wasmExecuteCallback failed:', err);
                    }
                } else {
                    console.warn('[Summon WASM] No callback registered for handler:', handlerId);
                }
            };

            entry.listener = listener;
            element.addEventListener(eventType, listener);
            return true;
        } catch (e) {
            console.error('[Summon WASM] addEventListener failed:', e);
            return false;
        }
    };

    window.wasmRemoveEventListenerById = function(elementId, eventType, handlerId) {
        try {
            const element = getElement(elementId);
            const entry = eventHandlers.get(handlerId);
            if (entry && entry.listener && element) {
                element.removeEventListener(eventType, entry.listener);
            }
            eventHandlers.delete(handlerId);
            eventCallbacks.delete(handlerId);
            return true;
        } catch (e) {
            console.error('[Summon WASM] removeEventListener failed:', e);
            eventHandlers.delete(handlerId);
            eventCallbacks.delete(handlerId);
            return false;
        }
    };

    // Event properties
    window.wasmGetEventType = function(handlerId) {
        const entry = eventHandlers.get(handlerId);
        return entry && entry.lastEvent ? entry.lastEvent.type : null;
    };

    window.wasmGetEventTargetId = function(handlerId) {
        const entry = eventHandlers.get(handlerId);
        return entry && entry.lastEvent ? entry.lastEvent.targetId : null;
    };

    window.wasmGetEventValue = function(handlerId) {
        const entry = eventHandlers.get(handlerId);
        return entry && entry.lastEvent ? entry.lastEvent.value : null;
    };

    window.wasmGetEventTargetValue = function(handlerId) {
        return wasmGetEventValue(handlerId);
    };

    window.wasmPreventEventDefault = function(handlerId) {
        const entry = eventHandlers.get(handlerId);
        if (entry && entry.lastEvent && entry.lastEvent.event) {
            entry.lastEvent.event.preventDefault();
            return true;
        }
        return false;
    };

    window.wasmStopEventPropagation = function(handlerId) {
        const entry = eventHandlers.get(handlerId);
        if (entry && entry.lastEvent && entry.lastEvent.event) {
            entry.lastEvent.event.stopPropagation();
            return true;
        }
        return false;
    };

    // Query selectors
    window.wasmQuerySelectorGetId = function(selector) {
        try {
            const element = document.querySelector(selector);
            return element ? storeElement(element) : null;
        } catch (e) {
            console.error('[Summon WASM] querySelector failed:', e);
            return null;
        }
    };

    window.wasmQuerySelectorAllGetIds = function(selector) {
        try {
            const elements = document.querySelectorAll(selector);
            const ids = Array.from(elements).map(el => storeElement(el));
            return ids.join(',');
        } catch (e) {
            console.error('[Summon WASM] querySelectorAll failed:', e);
            return '';
        }
    };

    // Form element specifics
    window.wasmGetElementValue = function(elementId) {
        try {
            const element = getElement(elementId);
            return element && 'value' in element ? element.value : null;
        } catch (e) {
            console.error('[Summon WASM] getValue failed:', e);
            return null;
        }
    };

    window.wasmSetElementValue = function(elementId, value) {
        try {
            const element = getElement(elementId);
            if (element && 'value' in element) {
                element.value = value;
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] setValue failed:', e);
            return false;
        }
    };

    window.wasmGetElementChecked = function(elementId) {
        try {
            const element = getElement(elementId);
            return element && 'checked' in element ? element.checked : false;
        } catch (e) {
            console.error('[Summon WASM] getChecked failed:', e);
            return false;
        }
    };

    window.wasmSetElementChecked = function(elementId, checked) {
        try {
            const element = getElement(elementId);
            if (element && 'checked' in element) {
                element.checked = checked;
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] setChecked failed:', e);
            return false;
        }
    };

    window.wasmGetElementDisabled = function(elementId) {
        try {
            const element = getElement(elementId);
            return element && 'disabled' in element ? element.disabled : false;
        } catch (e) {
            console.error('[Summon WASM] getDisabled failed:', e);
            return false;
        }
    };

    window.wasmSetElementDisabled = function(elementId, disabled) {
        try {
            const element = getElement(elementId);
            if (element && 'disabled' in element) {
                element.disabled = disabled;
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] setDisabled failed:', e);
            return false;
        }
    };

    // Select element specifics
    window.wasmGetSelectedIndex = function(elementId) {
        try {
            const element = getElement(elementId);
            return element && element.tagName === 'SELECT' ? element.selectedIndex : -1;
        } catch (e) {
            console.error('[Summon WASM] getSelectedIndex failed:', e);
            return -1;
        }
    };

    window.wasmSetSelectedIndex = function(elementId, index) {
        try {
            const element = getElement(elementId);
            if (element && element.tagName === 'SELECT') {
                element.selectedIndex = index;
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] setSelectedIndex failed:', e);
            return false;
        }
    };

    // Document operations
    window.wasmGetDocumentBodyId = function() {
        return document.body ? storeElement(document.body) : null;
    };

    window.wasmGetDocumentBody = function() {
        return wasmGetDocumentBodyId();
    };

    window.wasmGetDocumentHeadId = function() {
        return document.head ? storeElement(document.head) : null;
    };

    window.wasmGetDocumentHead = function() {
        return wasmGetDocumentHeadId();
    };

    window.wasmInsertHTMLIntoHead = function(html) {
        try {
            if (document.head) {
                document.head.insertAdjacentHTML('beforeend', html);
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] insertHTMLIntoHead failed:', e);
            return false;
        }
    };

    window.wasmInsertAdjacentHTML = function(elementId, position, html) {
        try {
            const element = getElement(elementId);
            if (element) {
                element.insertAdjacentHTML(position, html);
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] insertAdjacentHTML failed:', e);
            return false;
        }
    };

    window.wasmGetOuterHTML = function(elementId) {
        try {
            const element = getElement(elementId);
            return element ? element.outerHTML : null;
        } catch (e) {
            console.error('[Summon WASM] getOuterHTML failed:', e);
            return null;
        }
    };

    window.wasmSetInnerHTML = function(elementId, html) {
        return wasmSetElementInnerHTML(elementId, html);
    };

    // Browser capabilities
    window.wasmGetUserAgent = function() {
        return navigator.userAgent;
    };

    window.wasmGetLocationHref = function() {
        return window.location.href;
    };

    window.wasmGetScreenWidth = function() {
        return screen.width;
    };

    window.wasmGetScreenHeight = function() {
        return screen.height;
    };

    window.wasmGetDevicePixelRatio = function() {
        return window.devicePixelRatio || 1;
    };

    window.wasmGetColorDepth = function() {
        return screen.colorDepth || 24;
    };

    // Feature detection
    window.wasmHasTouchSupport = function() {
        return 'ontouchstart' in window || navigator.maxTouchPoints > 0;
    };

    window.wasmHasWebWorkers = function() {
        return typeof Worker !== 'undefined';
    };

    window.wasmHasModuleSupport = function() {
        return 'noModule' in document.createElement('script');
    };

    window.wasmHasDynamicImport = function() {
        try {
            new Function('import("")');
            return true;
        } catch (e) {
            return false;
        }
    };

    window.wasmHasWasmSIMD = function() {
        return WebAssembly.validate && WebAssembly.validate(new Uint8Array([0,97,115,109,1,0,0,0,1,5,1,96,0,1,123,3,2,1,0,10,10,1,8,0,65,0,253,17,253,186,1,11]));
    };

    window.wasmHasWasmThreads = function() {
        return typeof SharedArrayBuffer !== 'undefined';
    };

    window.wasmHasWebGL = function() {
        try {
            const canvas = document.createElement('canvas');
            return !!(canvas.getContext('webgl') || canvas.getContext('experimental-webgl'));
        } catch (e) {
            return false;
        }
    };

    window.wasmHasWebGL2 = function() {
        try {
            const canvas = document.createElement('canvas');
            return !!canvas.getContext('webgl2');
        } catch (e) {
            return false;
        }
    };

    // User agent testing
    window.wasmTestMobileUserAgent = function(userAgent) {
        return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(userAgent);
    };

    window.wasmTestTabletUserAgent = function(userAgent) {
        return /iPad|Android(?!.*Mobile)|Tablet/i.test(userAgent);
    };

    // Style operations
    window.wasmScrollElementIntoView = function(elementId, behavior) {
        try {
            const element = getElement(elementId);
            if (element) {
                element.scrollIntoView({ behavior: behavior || 'smooth' });
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] scrollIntoView failed:', e);
            return false;
        }
    };

    window.wasmGetComputedStyleProperty = function(elementId, property) {
        try {
            const element = getElement(elementId);
            if (element) {
                const styles = window.getComputedStyle(element);
                return styles.getPropertyValue(property);
            }
            return null;
        } catch (e) {
            console.error('[Summon WASM] getComputedStyle failed:', e);
            return null;
        }
    };

    window.wasmApplyStyleProperty = function(elementId, property, value) {
        try {
            const element = getElement(elementId);
            if (element) {
                element.style[property] = value;
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] applyStyleProperty failed:', e);
            return false;
        }
    };

    window.wasmGetElementStyle = function(elementId, property) {
        try {
            const element = getElement(elementId);
            return element ? element.style[property] : null;
        } catch (e) {
            console.error('[Summon WASM] getElementStyle failed:', e);
            return null;
        }
    };

    window.wasmSetElementStyle = function(elementId, cssText) {
        try {
            const element = getElement(elementId);
            if (element) {
                element.style.cssText = cssText;
                return true;
            }
            return false;
        } catch (e) {
            console.error('[Summon WASM] setElementStyle failed:', e);
            return false;
        }
    };

    // Performance
    window.wasmPerformanceNow = function() {
        return performance.now();
    };

    // Memory and batch operations
    let batchUpdates = [];
    let isBatching = false;

    window.wasmStartBatch = function() {
        isBatching = true;
        batchUpdates = [];
        return true;
    };

    window.wasmEndBatch = function() {
        isBatching = false;
        // Apply all batched updates
        requestAnimationFrame(() => {
            batchUpdates.forEach(update => update());
            batchUpdates = [];
        });
        return true;
    };

    // Console logging
    window.wasmConsoleLog = function(message) {
        console.log('[Summon WASM]', message);
    };

    window.wasmConsoleWarn = function(message) {
        console.warn('[Summon WASM]', message);
    };

    window.wasmConsoleError = function(message) {
        console.error('[Summon WASM]', message);
    };

    window.wasmConsoleDebug = function(message) {
        console.debug('[Summon WASM]', message);
    };

    // Helper functions
    window.wasmIsNotNull = function(value) {
        return value != null;
    };

    window.wasmAddEventHandler = function(elementId, eventType, handlerId) {
        return wasmAddEventListenerById(elementId, eventType, handlerId);
    };

    window.wasmRemoveEventHandler = function(elementId, eventType, handlerId) {
        return wasmRemoveEventListenerById(elementId, eventType, handlerId);
    };

    window.wasmCreateElementWithOptions = function(tagName, options) {
        try {
            const element = document.createElement(tagName);
            // Parse options if needed (JSON string)
            if (options) {
                try {
                    const opts = JSON.parse(options);
                    Object.assign(element, opts);
                } catch (e) {
                    // Ignore parse errors
                }
            }
            return storeElement(element);
        } catch (e) {
            console.error('[Summon WASM] createElementWithOptions failed:', e);
            return '';
        }
    };

    window.wasmCloneElement = function(sourceElementId, deep) {
        try {
            const element = getElement(sourceElementId);
            if (element) {
                const clone = element.cloneNode(deep);
                return storeElement(clone);
            }
            return null;
        } catch (e) {
            console.error('[Summon WASM] cloneElement failed:', e);
            return null;
        }
    };

    // Register WASM event callback handler
    window.registerWasmEventCallback = function(handlerId, callback) {
        console.log('[Summon WASM] Registering event callback:', handlerId);
        eventCallbacks.set(handlerId, callback);
    };

    // Animation frame management for recomposition

    window.wasmRequestAnimationFrame = function() {
        const frameId = requestAnimationFrame(() => {
            // The callback will be executed via registerWasmAnimationFrameCallback
        });
        console.log('[Summon WASM] Requested animation frame with ID:', frameId);
        return frameId;
    };

    window.wasmCancelAnimationFrame = function(frameId) {
        console.log('[Summon WASM] Cancelling animation frame:', frameId);
        cancelAnimationFrame(frameId);
        animationFrameCallbacks.delete(frameId);
        return true;
    };

    window.registerWasmAnimationFrameCallback = function(frameId, callback) {
        console.log('[Summon WASM] Registering animation frame callback for ID:', frameId);
        animationFrameCallbacks.set(frameId, callback);

        // Override the frame to execute our callback
        cancelAnimationFrame(frameId);
        const newFrameId = requestAnimationFrame(() => {
            console.log('[Summon WASM] Executing animation frame callback for ID:', frameId);
            try {
                callback();
            } catch (e) {
                console.error('[Summon WASM] Animation frame callback failed:', e);
            } finally {
                animationFrameCallbacks.delete(frameId);
            }
        });

        // Update the mapping if frameId changed
        if (newFrameId !== frameId) {
            animationFrameCallbacks.delete(frameId);
            animationFrameCallbacks.set(newFrameId, callback);
        }
    };

    // Initialize message
    console.log('[Summon WASM] Bridge initialized successfully');

    // Check if WASM is supported
    if (typeof WebAssembly === 'undefined') {
        console.error('[Summon WASM] WebAssembly is not supported in this browser');
        document.body.innerHTML = '<div style="padding: 20px; text-align: center;"><h1>WebAssembly Not Supported</h1><p>Please use a modern browser that supports WebAssembly.</p></div>';
    }
})();
