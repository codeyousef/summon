package code.yousef.summon.annotation

import code.yousef.summon.core.SummonApp

/**
 * Registry for app entry points annotated with @App.
 * This allows applications to register their custom app entry points at compile time.
 */
object AppRegistry {
    private var appEntry: (@Composable ((@Composable () -> Unit) -> Unit))? = null
    
    /**
     * Registers an app entry point.
     * Only one app entry point can be registered at a time.
     * If multiple entries are registered, a warning will be logged and the last one will be used.
     * 
     * @param entry The app entry composable function
     */
    fun registerApp(entry: @Composable ((@Composable () -> Unit) -> Unit)) {
        if (appEntry != null) {
            console.warn("Multiple @App entries found. Only the last one will be used.")
        }
        appEntry = entry
    }
    
    /**
     * Gets the registered app entry point, or the default SummonApp if none is registered.
     * 
     * @return The app entry composable function
     */
    fun getAppEntry(): @Composable ((@Composable () -> Unit) -> Unit) {
        return appEntry ?: { content -> SummonApp(content) }
    }
    
    /**
     * Clears the registered app entry.
     * This is mainly useful for testing.
     */
    fun clear() {
        appEntry = null
    }
}