package code.yousef.summon

/**
 * JVM-specific implementation of LifecycleOwner for backend frameworks.
 * This simplified implementation focuses on server/backend lifecycle events
 * rather than UI frameworks.
 */
class JvmLifecycleOwner : LifecycleOwner() {
    companion object {
        val instance by lazy {
            val owner = JvmLifecycleOwner()
            // Automatically set up backend integrations
            BackendIntegrations.setupAll()
            owner
        }

        // Registry for backend lifecycle hooks
        private val startupHooks = mutableListOf<() -> Unit>()
        private val shutdownHooks = mutableListOf<() -> Unit>()

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

            // Ensure we haven't already registered the JVM shutdown hook
            if (!shutdownHookRegistered) {
                Runtime.getRuntime().addShutdownHook(Thread {
                    instance.onPause()
                    instance.onStop()
                    shutdownHooks.forEach { it() }
                    instance.onDestroy()
                })
                shutdownHookRegistered = true
            }
        }

        private var shutdownHookRegistered = false
    }

    init {
        // Initialize with CREATED state
        onCreate()
        onStart()

        // Common backend frameworks startup patterns
        setupBackendIntegration()
    }

    /**
     * Set up integration with common backend frameworks
     */
    private fun setupBackendIntegration() {
        // Execute all registered startup hooks
        startupHooks.forEach { it() }

        // Auto-transition to RESUMED state for backend applications
        onResume()
    }
}

/**
 * Gets the current JVM-specific lifecycle owner.
 */
actual fun currentLifecycleOwner(): LifecycleOwner = JvmLifecycleOwner.instance
