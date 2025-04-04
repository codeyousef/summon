package code.yousef.summon

/**
 * Provides access to the platform-specific renderer.
 * Uses the expect/actual pattern to provide platform-specific implementations.
 */
expect object PlatformRendererProvider {
    /**
     * Gets the platform-specific renderer instance.
     */
    fun getRenderer(): PlatformRenderer
}

/**
 * Global access point to get the platform-specific renderer.
 * This replaces the standalone getPlatformRenderer() function.
 */
fun getPlatformRenderer(): PlatformRenderer = PlatformRendererProvider.getRenderer() 