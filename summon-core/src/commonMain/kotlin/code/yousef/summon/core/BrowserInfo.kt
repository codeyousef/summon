package code.yousef.summon.core

/**
 * Data class containing browser information and capabilities.
 *
 * This class provides type-safe access to browser metadata and feature detection
 * results, enabling platform-specific optimizations and fallback strategies.
 *
 * @property userAgent The browser's user agent string
 * @property name The detected browser name (Chrome, Firefox, Safari, etc.)
 * @property version The browser version string
 * @property engine The rendering engine (Blink, Gecko, WebKit, etc.)
 * @property wasmSupported Whether WebAssembly is supported
 * @property wasmSIMDSupported Whether WebAssembly SIMD is supported
 * @property wasmThreadsSupported Whether WebAssembly threads are supported
 * @property moduleSupported Whether ES6 modules are supported
 * @property dynamicImportSupported Whether dynamic imports are supported
 * @property webWorkersSupported Whether Web Workers are supported
 * @property webGLSupported Whether WebGL is supported
 * @property webGL2Supported Whether WebGL 2.0 is supported
 * @property isMobile Whether the device is identified as mobile
 * @property isTablet Whether the device is identified as tablet
 * @property isDesktop Whether the device is identified as desktop
 * @property supportsTouch Whether touch events are supported
 * @property screenWidth The screen width in pixels
 * @property screenHeight The screen height in pixels
 * @property devicePixelRatio The device pixel ratio
 * @property colorDepth The color depth of the display
 * @property timestamp When this browser info was captured
 *
 * @since 0.3.3.0
 */
data class BrowserInfo(
    val userAgent: String,
    val name: String,
    val version: String,
    val engine: String,
    val wasmSupported: Boolean,
    val wasmSIMDSupported: Boolean = false,
    val wasmThreadsSupported: Boolean = false,
    val moduleSupported: Boolean,
    val dynamicImportSupported: Boolean,
    val webWorkersSupported: Boolean,
    val webGLSupported: Boolean,
    val webGL2Supported: Boolean = false,
    val isMobile: Boolean,
    val isTablet: Boolean,
    val isDesktop: Boolean,
    val supportsTouch: Boolean,
    val screenWidth: Int,
    val screenHeight: Int,
    val devicePixelRatio: Double,
    val colorDepth: Int,
    val timestamp: Long = getCurrentTimeMillis()
) {
    /**
     * Whether this browser is considered modern (supports required features).
     *
     * A browser is considered modern if it supports:
     * - ES6 modules
     * - Dynamic imports
     * - Either WebAssembly OR sufficient fallback capabilities
     */
    val isModernBrowser: Boolean
        get() = moduleSupported && dynamicImportSupported

    /**
     * Whether WASM is preferred over JavaScript for this browser.
     *
     * WASM is preferred if:
     * - WebAssembly is supported
     * - Browser is not mobile (performance considerations)
     * - SIMD support is available (performance boost)
     */
    val preferWasm: Boolean
        get() = wasmSupported && !isMobile && wasmSIMDSupported

    /**
     * Whether this browser can handle high-performance applications.
     *
     * High performance is indicated by:
     * - WebGL support
     * - Web Workers support
     * - Desktop environment
     * - High pixel ratio
     */
    val isHighPerformance: Boolean
        get() = webGLSupported && webWorkersSupported && isDesktop && devicePixelRatio >= 1.5

    /**
     * The recommended platform target for this browser.
     *
     * Returns WebAssembly if WASM is preferred and supported,
     * otherwise returns JavaScript.
     */
    val recommendedTarget: PlatformTarget
        get() = if (preferWasm) PlatformTarget.WebAssembly else PlatformTarget.JavaScript

    /**
     * Browser capability score (0-100) for feature ranking.
     *
     * Higher scores indicate better capability for running
     * complex Summon applications.
     */
    val capabilityScore: Int
        get() {
            var score = 0

            // Base features (30 points)
            if (moduleSupported) score += 15
            if (dynamicImportSupported) score += 15

            // Performance features (40 points)
            if (wasmSupported) score += 20
            if (wasmSIMDSupported) score += 10
            if (webGLSupported) score += 5
            if (webWorkersSupported) score += 5

            // Device factors (30 points)
            when {
                isDesktop -> score += 20
                isTablet -> score += 15
                isMobile -> score += 10
            }

            if (devicePixelRatio >= 2.0) score += 5
            if (colorDepth >= 24) score += 5

            return score.coerceIn(0, 100)
        }

    companion object {
        /**
         * Creates a minimal BrowserInfo for testing purposes.
         */
        fun createTestInfo(
            wasmSupported: Boolean = true,
            isModern: Boolean = true,
            isMobile: Boolean = false
        ): BrowserInfo = BrowserInfo(
            userAgent = "Test Browser/1.0",
            name = "Test Browser",
            version = "1.0.0",
            engine = "TestEngine",
            wasmSupported = wasmSupported,
            wasmSIMDSupported = wasmSupported,
            moduleSupported = isModern,
            dynamicImportSupported = isModern,
            webWorkersSupported = true,
            webGLSupported = true,
            webGL2Supported = true,
            isMobile = isMobile,
            isTablet = false,
            isDesktop = !isMobile,
            supportsTouch = isMobile,
            screenWidth = if (isMobile) 375 else 1920,
            screenHeight = if (isMobile) 667 else 1080,
            devicePixelRatio = if (isMobile) 2.0 else 1.0,
            colorDepth = 24,
            timestamp = getCurrentTimeMillis()
        )
    }
}

/**
 * Gets the current time in milliseconds.
 * This is a placeholder - actual implementation will be platform-specific.
 */
expect fun getCurrentTimeMillis(): Long