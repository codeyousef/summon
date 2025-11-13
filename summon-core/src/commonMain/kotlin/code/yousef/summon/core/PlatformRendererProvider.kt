package codes.yousef.summon.core

import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.getPlatformRenderer
import codes.yousef.summon.runtime.setPlatformRenderer

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
    @Deprecated("Use getPlatformRenderer() directly", ReplaceWith("getPlatformRenderer()"))
    fun getRenderer(): PlatformRenderer {
        return getPlatformRenderer()
    }
    
    /**
     * Set the platform renderer.
     * This should be called once during app initialization.
     */
    @Deprecated("Use setPlatformRenderer() directly", ReplaceWith("setPlatformRenderer(renderer)"))
    fun setRenderer(renderer: PlatformRenderer) {
        setPlatformRenderer(renderer)
    }
} 