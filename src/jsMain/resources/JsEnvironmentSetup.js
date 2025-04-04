// JavaScript implementation for environment-specific event setup

// Browser environment setup
function setupBrowserEvents(lifecycleOwner) {
    if (typeof document !== 'undefined') {
        document.addEventListener('visibilitychange', function() {
            if (document.visibilityState === 'visible') {
                // Application becoming visible
                lifecycleOwner.notifyVisible();
            } else {
                // Application being hidden
                lifecycleOwner.notifyHidden();
            }
        });
    }
    
    if (typeof window !== 'undefined') {
        window.addEventListener('beforeunload', function() {
            // Application being unloaded
            lifecycleOwner.notifyStopping();
        });
    }
}

// Node.js environment setup
function setupNodeEvents(lifecycleOwner) {
    if (typeof process !== 'undefined') {
        process.on('exit', function() {
            lifecycleOwner.notifyDestroyed();
        });
        
        process.on('SIGINT', function() {
            lifecycleOwner.notifyStopping();
        });
        
        process.on('SIGTERM', function() {
            lifecycleOwner.notifyStopping();
        });
    }
}

// Web Worker environment setup
function setupWebWorkerEvents(lifecycleOwner) {
    if (typeof self !== 'undefined' && typeof self.addEventListener === 'function') {
        self.addEventListener('message', function(e) {
            // Handle custom lifecycle messages
            if (e.data && e.data.type === 'lifecycle') {
                if (e.data.action === 'pause') {
                    lifecycleOwner.notifyHidden();
                } else if (e.data.action === 'resume') {
                    lifecycleOwner.notifyVisible();
                } else if (e.data.action === 'terminate') {
                    lifecycleOwner.notifyStopping();
                }
            }
        });
    }
}

// Export the functions
module.exports = {
    setupBrowserEvents: setupBrowserEvents,
    setupNodeEvents: setupNodeEvents,
    setupWebWorkerEvents: setupWebWorkerEvents
}; 