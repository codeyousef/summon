package code.yousef.summon.runtime

import code.yousef.summon.core.error.ComponentNotFoundException
import code.yousef.summon.core.error.ErrorHandler

// Removed import for core.PlatformRenderer
// import code.yousef.summon.core.PlatformRenderer

/**
 * Utility function for accessing the current platform renderer.
 * This simplifies access to the PlatformRenderer by centralizing the access pattern.
 *
 * @throws ComponentNotFoundException if no renderer is set
 */
private var renderer: PlatformRenderer? = null

/**
 * Get the current platform renderer.
 * First tries to get it from CompositionLocal, then falls back to static renderer.
 * @throws ComponentNotFoundException if no renderer has been set.
 */
fun getPlatformRenderer(): PlatformRenderer {
    // Try to get renderer from CompositionLocal first if we're in a composition
    return try {
        // Use CompositionLocal if available (preferred approach within @Composable functions)
        if (CompositionLocal.currentComposer != null) {
            try {
                LocalPlatformRenderer.current
            } catch (e: IllegalStateException) {
                // If LocalPlatformRenderer hasn't been provided, fall back to static renderer
                renderer ?: throw ComponentNotFoundException(
                    "PlatformRenderer"
                )
            }
        } else {
            // Fall back to static renderer when outside composition
            renderer ?: throw ComponentNotFoundException(
                "PlatformRenderer"
            )
        }
    } catch (e: IllegalStateException) {
        // If CompositionLocal access fails, fall back to static renderer
        renderer ?: throw ComponentNotFoundException(
            "PlatformRenderer"
        )
    }
}

/**
 * Set the platform renderer.
 * This should be called once during app initialization.
 * 
 * @param newRenderer The platform renderer to use (New PlatformRenderer type)
 */
fun setPlatformRenderer(newRenderer: PlatformRenderer) {
    renderer = newRenderer
    
    // Update the CompositionLocal when possible
    try {
        // Provide the renderer to the CompositionLocal
        if (CompositionLocal.currentComposer != null) {
            LocalPlatformRenderer.provides(newRenderer)
        }
    } catch (e: Exception) {
        // If updating the CompositionLocal fails, just log it and continue
        println("Warning: Could not update LocalPlatformRenderer in setPlatformRenderer: ${e.message}")
    }
}

// Remove the old getRenderer() method as it's redundant/confusing
/*
@Deprecated("Use getPlatformRenderer() instead", ReplaceWith("getPlatformRenderer()"))
fun getRenderer(): PlatformRenderer {
    return getPlatformRenderer()
}
*/ 