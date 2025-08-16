package code.yousef.summon.platform

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer

/**
 * Initializes the JVM platform renderer and sets it as the current renderer.
 * This should be called once during application startup before any UI composition.
 *
 * @return The initialized PlatformRenderer instance
 */
fun initializeJvmPlatformRenderer() {
    val renderer = PlatformRenderer()
    setPlatformRenderer(renderer)
}

/**
 * Helper function to compose a UI tree with the platform renderer
 *
 * @param content The @Composable content to render
 */
@Composable
fun compose(content: @Composable () -> Unit) {
    // Platform-specific composition implementation
    content()
} 