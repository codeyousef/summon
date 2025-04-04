package code.yousef.summon

/**
 * JVM implementation of the PlatformRendererProvider.
 */
actual object PlatformRendererProvider {
    /**
     * The singleton JVM platform renderer instance.
     */
    private val renderer = JvmPlatformRenderer()
    
    /**
     * Gets the JVM platform renderer instance.
     */
    actual fun getRenderer(): PlatformRenderer = renderer
} 