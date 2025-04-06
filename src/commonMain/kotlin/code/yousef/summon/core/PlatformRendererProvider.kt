package code.yousef.summon.core

/**
 * Helper for managing platform renderers internally.
 */
internal object PlatformRendererHelper {
    /**
     * Platform-specific implementation of the renderer.
     */
    private var platformRenderer: PlatformRenderer? = null

    /**
     * Sets the platform-specific renderer implementation.
     *
     * @param renderer The platform-specific renderer implementation.
     */
    fun setPlatformRenderer(renderer: PlatformRenderer) {
        platformRenderer = renderer
    }

    /**
     * Gets the current platform-specific renderer.
     */
    fun getRenderer(): PlatformRenderer {
        return platformRenderer ?: error("PlatformRenderer has not been initialized. Make sure to call setPlatformRenderer() during application initialization.")
    }
}

/**
 * Gets the platform-specific renderer from the composition context if available,
 * otherwise falls back to the PlatformRendererProvider.
 * 
 * @return The platform-specific renderer.
 */
fun getPlatformRenderer(): PlatformRenderer {
    // In a full implementation, this would check CompositionLocal for a locally provided renderer
    // and fall back to the global PlatformRendererProvider if none is available.
    return PlatformRendererProvider.getRenderer()
} 