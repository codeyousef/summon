package code.yousef.summon.runtime

/**
 * Utility function for accessing the current platform renderer.
 * This simplifies access to the PlatformRenderer by centralizing the access pattern.
 * 
 * @throws IllegalStateException if no renderer is set
 */
private var renderer: PlatformRenderer? = null

/**
 * Get the current platform renderer.
 * @throws IllegalStateException if no renderer has been set.
 */
fun getPlatformRenderer(): PlatformRenderer {
    return renderer ?: throw IllegalStateException(
        "PlatformRenderer not set. Call setPlatformRenderer first."
    )
}

/**
 * Set the platform renderer.
 * This should be called once during app initialization.
 */
fun setPlatformRenderer(newRenderer: PlatformRenderer) {
    renderer = newRenderer
} 