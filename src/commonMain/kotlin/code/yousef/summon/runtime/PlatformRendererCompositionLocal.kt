package code.yousef.summon.runtime

/**
 * CompositionLocal for accessing the current platform renderer.
 * This is used by composable functions to access the renderer in a composition-aware way.
 */
val LocalPlatformRenderer = CompositionLocal.staticCompositionLocalOf<MigratedPlatformRenderer>()

/**
 * Helper function to get the current platform renderer from the CompositionLocal.
 * 
 * This will eventually replace the static getPlatformRenderer() function to provide
 * properly scoped access to the renderer within composition.
 */
fun getCurrentRenderer(): MigratedPlatformRenderer {
    return LocalPlatformRenderer.current
} 