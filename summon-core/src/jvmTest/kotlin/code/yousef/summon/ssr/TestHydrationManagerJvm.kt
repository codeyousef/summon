package code.yousef.summon.ssr

/**
 * JVM-specific implementation uses the default TestHydrationManager
 */
actual fun createPlatformTestHydrationManager(): HydrationManager {
    return TestHydrationManager()
}