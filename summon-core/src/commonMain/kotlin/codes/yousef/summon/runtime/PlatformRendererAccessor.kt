package codes.yousef.summon.runtime

import codes.yousef.summon.core.error.ComponentNotFoundException

/**
 * Utility function for accessing the current platform renderer.
 * This simplifies access to the PlatformRenderer by centralizing the access pattern.
 *
 * @throws ComponentNotFoundException if no renderer is set
 */
fun getPlatformRenderer(): PlatformRenderer {
    val composer = CompositionLocal.currentComposer
    if (composer != null) {
        try {
            return LocalPlatformRenderer.current
        } catch (_: IllegalStateException) {
            // Fall through to global store fallback
        }
    }

    return PlatformRendererStore.get()
        ?: throw ComponentNotFoundException("PlatformRenderer")
}

/**
 * Set the platform renderer.
 * This should be called once during app initialization.
 *
 * @param newRenderer The platform renderer to use (New PlatformRenderer type)
 */
fun setPlatformRenderer(newRenderer: PlatformRenderer) {
    PlatformRendererStore.set(newRenderer)

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

/**
 * Clears the renderer associated with the current execution context.
 * Should be invoked after request/response rendering completes to avoid leaks.
 */
fun clearPlatformRenderer() {
    PlatformRendererStore.clear()
}
