package code.yousef.summon.ssr

/**
 * JS-specific implementation uses the default TestHydrationManager
 */
actual fun createPlatformTestHydrationManager(): HydrationManager {
    return TestHydrationManager()
}