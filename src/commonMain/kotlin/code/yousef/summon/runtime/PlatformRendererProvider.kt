// This file is intentionally deleted since PlatformRendererProvider is now defined in PlatformRenderer.kt 

package code.yousef.summon.runtime

import code.yousef.summon.core.PlatformRenderer

/**
 * Provider for accessing the platform renderer.
 * This is a utility class that provides access to the platform renderer.
 */
object PlatformRendererProvider {
    /**
     * Get the current platform renderer.
     * This is a convenience method that delegates to getPlatformRenderer().
     */
    fun get(): MigratedPlatformRenderer {
        return getPlatformRenderer()
    }
    
    /**
     * Set the platform renderer.
     * This is a convenience method that delegates to setPlatformRenderer().
     */
    fun set(renderer: MigratedPlatformRenderer) {
        setPlatformRenderer(renderer)
    }
} 
