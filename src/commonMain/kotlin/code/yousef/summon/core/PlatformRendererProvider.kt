package code.yousef.summon.core

import code.yousef.summon.runtime.MigratedPlatformRenderer
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer

/**
 * Provider for accessing the platform renderer.
 * This is a compatibility layer to help with migrating from the old
 * PlatformRendererProvider.getRenderer() pattern to the new getPlatformRenderer() function.
 */
object PlatformRendererProvider {
    /**
     * Get the current platform renderer.
     * @return The platform renderer.
     * @throws IllegalStateException if no renderer has been set.
     */
    @Deprecated("Use getPlatformRenderer() instead", ReplaceWith("getPlatformRenderer()"))
    fun getRenderer(): PlatformRenderer {
        return getPlatformRenderer()
    }
    
    /**
     * Set the platform renderer.
     * This should be called once during app initialization.
     */
    @Deprecated("Use setPlatformRenderer() instead", ReplaceWith("setPlatformRenderer(renderer)"))
    fun setRenderer(renderer: PlatformRenderer) {
        if (renderer is MigratedPlatformRenderer) {
            setPlatformRenderer(renderer)
        } else {
            throw IllegalArgumentException("Renderer must implement MigratedPlatformRenderer")
        }
    }
} 