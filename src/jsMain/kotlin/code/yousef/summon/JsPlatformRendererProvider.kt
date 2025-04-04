package code.yousef.summon

/**
 * JS implementation of the PlatformRendererProvider.
 */
actual object PlatformRendererProvider {
    /**
     * The singleton JS platform renderer instance.
     */
    private val renderer = JsPlatformRenderer()
    
    /**
     * Gets the JS platform renderer instance.
     */
    actual fun getRenderer(): PlatformRenderer = renderer
} 