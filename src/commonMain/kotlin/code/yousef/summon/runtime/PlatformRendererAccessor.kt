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
 * First tries to get it from CompositionLocal, then falls back to static renderer.
 * @throws IllegalStateException if no renderer has been set.
 */
fun getPlatformRenderer(): MigratedPlatformRenderer {
    // Try to get renderer from CompositionLocal first if we're in a composition
    return try {
        // Use CompositionLocal if available (preferred approach within @Composable functions)
        if (CompositionLocal.currentComposer != null) {
            try {
                LocalPlatformRenderer.current
            } catch (e: IllegalStateException) {
                // If LocalPlatformRenderer hasn't been provided, fall back to static renderer
                renderer ?: throw IllegalStateException(
                    "PlatformRenderer not set. Call setPlatformRenderer first."
                )
            }
        } else {
            // Fall back to static renderer when outside composition
            renderer ?: throw IllegalStateException(
                "PlatformRenderer not set. Call setPlatformRenderer first."
            )
        }
    } catch (e: IllegalStateException) {
        // If CompositionLocal access fails, fall back to static renderer
        renderer ?: throw IllegalStateException(
            "PlatformRenderer not set. Call setPlatformRenderer first."
        )
    }
}

/**
 * Set the platform renderer.
 * This should be called once during app initialization.
 * 
 * @param newRenderer The platform renderer to use
 */
fun setPlatformRenderer(newRenderer: MigratedPlatformRenderer) {
    renderer = newRenderer
    
    // Update the CompositionLocal when possible
    try {
        // Provide the renderer to the CompositionLocal
        if (CompositionLocal.currentComposer != null) {
            (LocalPlatformRenderer as CompositionLocalProvider<MigratedPlatformRenderer>).provides(newRenderer)
        }
    } catch (e: Exception) {
        // If updating the CompositionLocal fails, just log it and continue
        println("Warning: Could not update LocalPlatformRenderer: ${e.message}")
    }
}

/**
 * Legacy method for compatibility with existing code.
 * @deprecated Use getPlatformRenderer() instead
 */
@Deprecated("Use getPlatformRenderer() instead", ReplaceWith("getPlatformRenderer()"))
fun getRenderer(): PlatformRenderer {
    return getPlatformRenderer()
} 