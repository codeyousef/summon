package code.yousef.summon.runtime

/**
 * Compatibility layer for code that still uses PlatformRendererProvider.
 * This provides a backward-compatible way to access the platform renderer
 * while we migrate the codebase to use the getPlatformRenderer() function directly.
 */
object PlatformRendererProvider {
    /**
     * Get the current platform renderer.
     * Delegates to the new getPlatformRenderer() function.
     * 
     * @throws IllegalStateException if no renderer has been set.
     */
    fun getPlatformRenderer(): PlatformRenderer {
        return code.yousef.summon.runtime.getPlatformRenderer()
    }
    
    /**
     * Set the platform renderer.
     * Delegates to the new setPlatformRenderer() function.
     * This should be called once during app initialization.
     */
    fun setPlatformRenderer(newRenderer: PlatformRenderer) {
        code.yousef.summon.runtime.setPlatformRenderer(newRenderer)
    }
} 