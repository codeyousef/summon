package code.yousef.summon.core

/**
 * Platform detection API for identifying runtime environment and capabilities.
 *
 * This module provides expect/actual declarations for platform-specific
 * detection logic, enabling type-safe platform identification across targets.
 *
 * @since 0.3.3.0
 */

/**
 * Detects the current platform target.
 *
 * @return The detected PlatformTarget (JVM, JavaScript, or WebAssembly)
 */
expect fun detectPlatformTarget(): PlatformTarget

/**
 * Detects browser information and capabilities (browser targets only).
 *
 * @return BrowserInfo if running in a browser, null for server targets
 */
expect fun detectBrowserInfo(): BrowserInfo?

/**
 * Checks if the current platform supports WebAssembly.
 *
 * @return true if WASM is supported, false otherwise
 */
expect fun isWasmSupported(): Boolean

/**
 * Checks if the current platform supports WASM SIMD instructions.
 *
 * @return true if WASM SIMD is supported, false otherwise
 */
expect fun isWasmSIMDSupported(): Boolean

/**
 * Checks if the current platform supports WASM threads.
 *
 * @return true if WASM threads are supported, false otherwise
 */
expect fun isWasmThreadsSupported(): Boolean

/**
 * Checks if the current platform supports ES6 modules.
 *
 * @return true if ES6 modules are supported, false otherwise
 */
expect fun isModuleSupported(): Boolean

/**
 * Checks if the current platform supports dynamic imports.
 *
 * @return true if dynamic imports are supported, false otherwise
 */
expect fun isDynamicImportSupported(): Boolean

/**
 * Checks if the current platform supports Web Workers.
 *
 * @return true if Web Workers are supported, false otherwise
 */
expect fun isWebWorkersSupported(): Boolean

/**
 * Checks if the current platform has DOM capabilities.
 *
 * @return true for browser targets, false for server targets
 */
expect fun hasDOMCapabilities(): Boolean

/**
 * Checks if the current platform has server-side rendering capabilities.
 *
 * @return true for JVM target, false for browser targets
 */
expect fun hasSSRCapabilities(): Boolean

/**
 * Gets the user agent string (browser targets only).
 *
 * @return User agent string if available, null otherwise
 */
expect fun getUserAgent(): String?

/**
 * Gets the current URL (browser targets only).
 *
 * @return Current URL if available, null otherwise
 */
expect fun getCurrentURL(): String?

/**
 * Checks if the device is mobile.
 *
 * @return true if mobile device detected, false otherwise
 */
expect fun isMobileDevice(): Boolean

/**
 * Checks if the device supports touch input.
 *
 * @return true if touch is supported, false otherwise
 */
expect fun isTouchSupported(): Boolean

/**
 * Gets the screen width in pixels.
 *
 * @return Screen width, or -1 if not available
 */
expect fun getScreenWidth(): Int

/**
 * Gets the screen height in pixels.
 *
 * @return Screen height, or -1 if not available
 */
expect fun getScreenHeight(): Int

/**
 * Gets the device pixel ratio.
 *
 * @return Device pixel ratio, or 1.0 if not available
 */
expect fun getDevicePixelRatio(): Double

/**
 * Common platform detection utilities available across all targets.
 */
object PlatformDetection {

    /**
     * Cached platform target for performance.
     */
    val currentTarget: PlatformTarget by lazy { detectPlatformTarget() }

    /**
     * Cached browser info for performance (null on server targets).
     */
    val browserInfo: BrowserInfo? by lazy { detectBrowserInfo() }

    /**
     * Whether this is a browser environment.
     */
    val isBrowser: Boolean
        get() = currentTarget.isBrowserTarget

    /**
     * Whether this is a server environment.
     */
    val isServer: Boolean
        get() = currentTarget.hasSSRCapabilities

    /**
     * Whether this platform is suitable for WASM deployment.
     */
    val isWasmSuitable: Boolean
        get() = browserInfo?.preferWasm == true

    /**
     * Whether this platform requires JavaScript fallback.
     */
    val requiresJSFallback: Boolean
        get() = isBrowser && !isWasmSuitable

    /**
     * Gets the recommended target for current platform.
     */
    val recommendedTarget: PlatformTarget
        get() = browserInfo?.recommendedTarget ?: currentTarget

    /**
     * Platform capability score (0-100).
     */
    val capabilityScore: Int
        get() = browserInfo?.capabilityScore ?: when (currentTarget) {
            is PlatformTarget.JVM -> 85 // High capability for server features
            else -> 50 // Default for unknown platforms
        }

    /**
     * Creates a platform summary for debugging.
     */
    fun createPlatformSummary(): String = buildString {
        appendLine("Platform Summary:")
        appendLine("- Target: $currentTarget")
        appendLine("- Browser: $isBrowser")
        appendLine("- Server: $isServer")
        appendLine("- WASM Suitable: $isWasmSuitable")
        appendLine("- Capability Score: $capabilityScore")

        browserInfo?.let { info ->
            appendLine("- Browser: ${info.name} ${info.version}")
            appendLine("- Engine: ${info.engine}")
            appendLine("- WASM Support: ${info.wasmSupported}")
            appendLine("- Mobile: ${info.isMobile}")
            appendLine("- Modern: ${info.isModernBrowser}")
        }
    }

    /**
     * Validates platform requirements for Summon framework.
     */
    fun validatePlatformRequirements(): List<String> {
        val issues = mutableListOf<String>()

        when (currentTarget) {
            is PlatformTarget.JVM -> {
                // JVM requirements
                if (!hasSSRCapabilities()) {
                    issues.add("JVM target requires SSR capabilities")
                }
            }

            is PlatformTarget.JavaScript -> {
                // JavaScript requirements
                browserInfo?.let { info ->
                    if (!info.moduleSupported) {
                        issues.add("ES6 modules are required for JavaScript target")
                    }
                    if (!info.dynamicImportSupported) {
                        issues.add("Dynamic imports are required for code splitting")
                    }
                } ?: issues.add("Browser information not available for JavaScript target")
            }

            is PlatformTarget.WebAssembly -> {
                // WebAssembly requirements
                browserInfo?.let { info ->
                    if (!info.wasmSupported) {
                        issues.add("WebAssembly support is required for WASM target")
                    }
                    if (!info.moduleSupported) {
                        issues.add("ES6 modules are required for WASM target")
                    }
                } ?: issues.add("Browser information not available for WASM target")
            }
        }

        return issues
    }

    /**
     * Determines if graceful degradation is needed.
     */
    fun shouldDegrade(): Boolean {
        val issues = validatePlatformRequirements()
        return issues.isNotEmpty()
    }

    /**
     * Gets the fallback target if degradation is needed.
     */
    fun getFallbackTarget(): PlatformTarget? = when {
        currentTarget is PlatformTarget.WebAssembly && requiresJSFallback -> PlatformTarget.JavaScript
        else -> null
    }
}