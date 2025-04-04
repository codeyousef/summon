package code.yousef.summon.core

import code.yousef.summon.JvmPlatformRenderer

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