package code.yousef.summon.core

/**
 * Provider for platform-specific renderer implementations.
 * This allows platform-specific code to be injected at runtime.
 */
expect object PlatformRendererProvider {
    /**
     * Gets the current platform renderer.
     */
    fun getRenderer(): PlatformRenderer
}

/**
 * Global access point to get the platform-specific renderer.
 */
fun getPlatformRenderer(): PlatformRenderer = PlatformRendererProvider.getRenderer() 