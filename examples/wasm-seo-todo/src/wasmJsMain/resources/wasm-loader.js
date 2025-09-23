// Direct WASM module loader
(async function () {
    try {
        // Load the WASM module directly
        const module = await import('./kotlin/summon-examples-wasm-seo-todo.mjs');

        // Expose functions globally
        if (module.executeCallback) {
            window.executeCallback = module.executeCallback;
            window.wasmExecuteCallback = module.executeCallback;
        }

        if (module.wasmExecuteCallback) {
            window.wasmExecuteCallback = module.wasmExecuteCallback;
            window.executeCallback = module.wasmExecuteCallback;
        }

        // Expose the module globally
        window.wasmModule = module;

        // Initialize if needed
        if (module._initialize) {
            module._initialize();
        }

        // Start the app
        if (module.main) {
            module.main();

            // Hide loading indicator
            const loadingElement = document.querySelector('.loading');
            if (loadingElement) {
                loadingElement.style.display = 'none';
            }
        }
    } catch (error) {
        // Silently handle errors
    }
})();