package code.yousef.summon

/**
 * Example integrations with common JS environments.
 * This provides hooks for browser, Node.js, and other JS environments.
 */
object JsEnvironmentIntegrations {
    /**
     * Setup integration with a browser environment
     */
    fun setupBrowserIntegration() {
        try {
            // Check if browser globals are available
            if (isBrowserEnvironment()) {
                println("Browser environment detected")
                
                // Register custom hooks
                JsLifecycleOwner.addStartupHook {
                    println("Browser application started")
                }
                
                JsLifecycleOwner.addShutdownHook {
                    println("Browser application shutting down")
                }
                
                JsLifecycleOwner.addVisibilityChangeHook { isVisible ->
                    println("Browser visibility changed: ${if (isVisible) "visible" else "hidden"}")
                }
            }
        } catch (e: Throwable) {
            // Not in a browser environment or error occurred
            println("Browser environment not detected or error: ${e.message}")
        }
    }
    
    /**
     * Setup integration with a Node.js environment
     */
    fun setupNodeIntegration() {
        try {
            // Check if we're in Node.js
            if (isNodeEnvironment()) {
                println("Node.js environment detected")
                
                // Register custom hooks
                JsLifecycleOwner.addStartupHook {
                    println("Node.js application started")
                }
                
                JsLifecycleOwner.addShutdownHook {
                    println("Node.js application shutting down")
                }
            }
        } catch (e: Throwable) {
            // Not in a Node.js environment or error occurred
            println("Node.js environment not detected: ${e.message}")
        }
    }
    
    /**
     * Setup integration with a Web Worker environment
     */
    fun setupWebWorkerIntegration() {
        try {
            // Check if we're in a Web Worker
            if (isWebWorkerEnvironment()) {
                println("Web Worker environment detected")
                
                // Register custom hooks
                JsLifecycleOwner.addStartupHook {
                    println("Web Worker started")
                }
                
                JsLifecycleOwner.addShutdownHook {
                    println("Web Worker shutting down")
                }
            }
        } catch (e: Throwable) {
            // Not in a Web Worker environment or error occurred
            println("Web Worker environment not detected: ${e.message}")
        }
    }
    
    /**
     * Setup all known JS environment integrations
     */
    fun setupAll() {
        // Try all known environments - only the detected ones will be initialized
        setupBrowserIntegration()
        setupNodeIntegration()
        setupWebWorkerIntegration()
    }
    
    /**
     * Check if we're in a browser environment
     */
    private fun isBrowserEnvironment(): Boolean {
        return js("typeof window !== 'undefined'").unsafeCast<Boolean>()
    }
    
    /**
     * Check if we're in a Node.js environment
     */
    private fun isNodeEnvironment(): Boolean {
        return js("typeof process !== 'undefined' && process.versions && process.versions.node").unsafeCast<Boolean>()
    }
    
    /**
     * Check if we're in a Web Worker environment
     */
    private fun isWebWorkerEnvironment(): Boolean {
        return js("typeof self !== 'undefined' && typeof window === 'undefined' && typeof self.importScripts === 'function'").unsafeCast<Boolean>()
    }
} 