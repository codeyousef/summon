package code.yousef.summon.runtime

import code.yousef.summon.core.PlatformRenderer

/**
 * Utility function for accessing the current platform renderer.
 * This simplifies access to the PlatformRenderer by centralizing the access pattern.
 *
 * @throws IllegalStateException if no renderer is set
 */
private var renderer: MigratedPlatformRenderer? = null

/**
 * Get the current platform renderer.
 * @throws IllegalStateException if no renderer has been set.
 */
fun getPlatformRenderer(): MigratedPlatformRenderer {
    return renderer ?: throw IllegalStateException(
        "PlatformRenderer not set. Call setPlatformRenderer first."
    )
}

/**
 * Set the platform renderer.
 * This should be called once during app initialization.
 */
fun setPlatformRenderer(newRenderer: MigratedPlatformRenderer) {
    renderer = newRenderer
}

/**
 * Legacy method for compatibility with existing code.
 * @deprecated Use getPlatformRenderer() instead
 */
@Deprecated("Use getPlatformRenderer() instead", ReplaceWith("getPlatformRenderer()"))
fun getRenderer(): PlatformRenderer {
    return getPlatformRenderer()
} 