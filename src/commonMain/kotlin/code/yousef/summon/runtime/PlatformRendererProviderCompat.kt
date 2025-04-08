package code.yousef.summon.runtime

import code.yousef.summon.core.PlatformRenderer

/**
 * Compatibility layer for code that still uses PlatformRendererProvider.
 * This class provides static access to a PlatformRenderer instance.
 * 
 * This class will be deprecated and eventually removed once the migration to getPlatformRenderer() is complete.
 */
object PlatformRendererProviderLegacy {
    /**
     * Get the current platform renderer.
     * @return the current platform renderer
     * @throws IllegalStateException if no renderer has been set
     */
    fun getRenderer(): PlatformRenderer {
        // Convert MigratedPlatformRenderer to PlatformRenderer if necessary
        val renderer = getPlatformRenderer()
        return renderer as? PlatformRenderer ?: throw IllegalStateException(
            "Platform renderer is not compatible with expected interface"
        )
    }
    
    /**
     * Set the platform renderer.
     * @param renderer the renderer to set
     */
    fun setRenderer(renderer: PlatformRenderer) {
        // Cast to MigratedPlatformRenderer if compatible
        if (renderer is MigratedPlatformRenderer) {
            setPlatformRenderer(renderer)
        } else {
            throw IllegalArgumentException("Renderer must implement MigratedPlatformRenderer")
        }
    }
} 