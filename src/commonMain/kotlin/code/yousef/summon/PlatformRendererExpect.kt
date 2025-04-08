package code.yousef.summon

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

/**
 * Platform-specific implementation of the renderer.
 * This class must be implemented by each platform.
 */
expect class JsPlatformRenderer() : PlatformRenderer 
