// Direct WASM module loader
(async function () {
    console.log('[WASM Loader] Starting...');

    try {
        // Load the WASM module directly
        const module = await import('./kotlin/summon-examples-wasm-seo-todo.mjs');
        console.log('[WASM Loader] Module loaded, exports:', Object.keys(module));

        // Expose functions globally
        if (module.executeCallback) {
            window.executeCallback = module.executeCallback;
            window.wasmExecuteCallback = module.executeCallback;
            console.log('[WASM Loader] executeCallback exposed globally');
        }

        if (module.wasmExecuteCallback) {
            window.wasmExecuteCallback = module.wasmExecuteCallback;
            window.executeCallback = module.wasmExecuteCallback;
            console.log('[WASM Loader] wasmExecuteCallback exposed globally');
        }

        // Expose the module globally
        window.wasmModule = module;

        // Initialize if needed
        if (module._initialize) {
            console.log('[WASM Loader] Initializing module...');
            module._initialize();
        }

        // Start the app
        if (module.main) {
            console.log('[WASM Loader] Starting application...');
            module.main();

            // Hide loading indicator
            const loadingElement = document.querySelector('.loading');
            if (loadingElement) {
                loadingElement.style.display = 'none';
            }

            console.log('[WASM Loader] Application started successfully');
        } else {
            console.error('[WASM Loader] main function not found');
        }
    } catch (error) {
        console.error('[WASM Loader] Error loading module:', error);
    }
})();