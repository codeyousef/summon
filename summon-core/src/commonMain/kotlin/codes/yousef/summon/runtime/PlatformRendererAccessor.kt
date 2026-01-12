package codes.yousef.summon.runtime

import codes.yousef.summon.core.error.ComponentNotFoundException
import kotlin.js.JsName

/**
 * Utility function for accessing the current platform renderer.
 * This simplifies access to the PlatformRenderer by centralizing the access pattern.
 *
 * This function is annotated with @JsName to ensure consistent naming in minified JS builds.
 *
 * @throws ComponentNotFoundException if no renderer is set
 */
@JsName("getPlatformRenderer")
fun getPlatformRenderer(): PlatformRenderer {
    // First try the global store - this is more reliable in minified JS contexts
    val globalRenderer = PlatformRendererStore.get()
    if (globalRenderer != null) {
        return globalRenderer
    }

    // Fall back to CompositionLocal if available
    val composer = CompositionLocal.currentComposer
    if (composer != null) {
        try {
            return LocalPlatformRenderer.current
        } catch (_: IllegalStateException) {
            // Fall through to error
        }
    }

    throw ComponentNotFoundException("PlatformRenderer")
}

/**
 * Set the platform renderer.
 * This should be called once during app initialization.
 *
 * This function is annotated with @JsName to ensure consistent naming in minified JS builds.
 *
 * @param newRenderer The platform renderer to use (New PlatformRenderer type)
 */
@JsName("setPlatformRenderer")
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
 *
 * This function is annotated with @JsName to ensure consistent naming in minified JS builds.
 */
@JsName("clearPlatformRenderer")
fun clearPlatformRenderer() {
    PlatformRendererStore.clear()
}
