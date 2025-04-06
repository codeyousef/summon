package code.yousef.summon

import code.yousef.summon.core.PlatformRenderer

/**
 * Platform-specific implementation of the renderer.
 * This class must be implemented by each platform.
 */
expect class JsPlatformRenderer() : PlatformRenderer 