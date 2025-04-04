package code.yousef.summon

/**
 * JS-specific implementation of LifecycleOwner.
 * This implementation is browser agnostic and works with any JS environment.
 */
class JsLifecycleOwner : LifecycleOwner() {
    companion object {
        val instance by lazy { 
            val owner = JsLifecycleOwner()
            // Automatically detect and initialize JS environment integrations
            JsEnvironmentIntegrations.setupAll()
            owner
        }
        
        // Registry for environment lifecycle hooks
        private val startupHooks = mutableListOf<() -> Unit>()
        private val shutdownHooks = mutableListOf<() -> Unit>()
        private val visibilityChangeHooks = mutableListOf<(Boolean) -> Unit>()
        
        /**
         * Register a function to be called on application startup
         */
        fun addStartupHook(hook: () -> Unit) {
            startupHooks.add(hook)
            // If already started, execute immediately
            if (instance.state == LifecycleState.STARTED || instance.state == LifecycleState.RESUMED) {
                hook()
            }
        }
        
        /**
         * Register a function to be called on application shutdown
         */
        fun addShutdownHook(hook: () -> Unit) {
            shutdownHooks.add(hook)
        }
        
        /**
         * Register a function to be called when visibility changes
         * @param hook Function that receives true when visible, false when hidden
         */
        fun addVisibilityChangeHook(hook: (Boolean) -> Unit) {
            visibilityChangeHooks.add(hook)
        }
        
        /**
         * Notify that the application is becoming visible
         * Called by platform integrations
         */
        fun notifyVisible() {
            if (instance.state == LifecycleState.PAUSED || instance.state == LifecycleState.STOPPED) {
                instance.onResume()
                visibilityChangeHooks.forEach { it(true) }
            }
        }
        
        /**
         * Notify that the application is becoming hidden
         * Called by platform integrations
         */
        fun notifyHidden() {
            if (instance.state == LifecycleState.RESUMED) {
                instance.onPause()
                visibilityChangeHooks.forEach { it(false) }
            }
        }
        
        /**
         * Notify that the application is stopping
         * Called by platform integrations
         */
        fun notifyStopping() {
            if (instance.state != LifecycleState.STOPPED && 
                instance.state != LifecycleState.DESTROYED) {
                
                if (instance.state == LifecycleState.RESUMED) {
                    instance.onPause()
                }
                instance.onStop()
                shutdownHooks.forEach { it() }
            }
        }
        
        /**
         * Notify that the application is being destroyed
         * Called by platform integrations
         */
        fun notifyDestroyed() {
            if (instance.state != LifecycleState.DESTROYED) {
                if (instance.state == LifecycleState.RESUMED) {
                    instance.onPause()
                }
                if (instance.state == LifecycleState.STARTED) {
                    instance.onStop()
                }
                shutdownHooks.forEach { it() }
                instance.onDestroy()
            }
        }
    }
    
    init {
        // Initialize with CREATED state
        onCreate()
        onStart()
        
        // Common JS environment initialization
        setupJsEnvironment()
    }
    
    /**
     * Set up integration with common JS environments
     */
    private fun setupJsEnvironment() {
        // Execute all registered startup hooks
        startupHooks.forEach { it() }
        
        // Auto-transition to RESUMED state for JS applications
        onResume()
    }
}

/**
 * Gets the current JS-specific lifecycle owner.
 */
actual fun currentLifecycleOwner(): LifecycleOwner = JsLifecycleOwner.instance 