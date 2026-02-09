package codes.yousef.summon.runtime

/**
 * Shared constants for the Summon framework.
 *
 * These values are referenced by the JVM server renderer, the JS/WASM client,
 * the hydration bootloader (summon-bootloader.js), and the CLI project generator.
 * Changing a value here requires updating the JavaScript bootloader and CLI
 * templates that cannot reference Kotlin constants directly.
 */
object SummonConstants {
    /**
     * Default ID of the root DOM element used for SSR rendering and client-side hydration.
     *
     * This ID must be consistent across:
     * - JvmPlatformRenderer (server-rendered HTML)
     * - summon-bootloader.js (hydration script loader)
     * - SummonHydrationClient (client-side hydration)
     * - CLI-generated Main.kt (renderComposableRoot call)
     * - CLI-generated index.html (root element)
     */
    const val DEFAULT_ROOT_ELEMENT_ID = "summon-app"
}
