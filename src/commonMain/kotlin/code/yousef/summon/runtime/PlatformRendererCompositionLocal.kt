package code.yousef.summon.runtime

import code.yousef.summon.core.error.ComponentNotFoundException
import code.yousef.summon.core.error.ErrorHandler

/**
 * CompositionLocal for accessing the current platform renderer.
 * This is used by composable functions to access the renderer in a composition-aware way.
 */
val LocalPlatformRenderer = CompositionLocal.staticCompositionLocalOf<PlatformRenderer>()

/**
 * Helper function to get the current platform renderer from the CompositionLocal.
 * 
 * This will eventually replace the static getPlatformRenderer() function to provide
 * properly scoped access to the renderer within composition.
 */
fun getCurrentRenderer(): PlatformRenderer {
    // Check if we're inside a composition context with a provided renderer
    if (CompositionLocal.currentComposer == null) {
        // Not inside a composition context, throw exception
        throw ComponentNotFoundException("PlatformRenderer")
    }

    // Inside a composition context, try to get the renderer
    try {
        return LocalPlatformRenderer.current
    } catch (e: Exception) {
        // No renderer provided, throw exception
        throw ComponentNotFoundException("PlatformRenderer")
    }
}
