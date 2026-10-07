package codes.yousef.summon.core

import codes.yousef.summon.runtime.*

/**
 * WebAssembly-specific implementation of platform detection.
 *
 * This implementation provides platform detection from within a WASM context,
 * using external JavaScript interop functions for capability detection.
 *
 * @since 0.3.3.0
 */

actual fun detectPlatformTarget(): PlatformTarget = PlatformTarget.WebAssembly

/**
 * Executes the detect browser info operation.
 *
 * @return The resulting value.
 */
actual fun detectBrowserInfo(): BrowserInfo? {
    return try {
        val userAgent = getUserAgent() ?: return null
        val browserDetection = detectBrowserFromUserAgent(userAgent)

        BrowserInfo(
            userAgent = userAgent,
            name = browserDetection.name,
            version = browserDetection.version,
            engine = browserDetection.engine,
            wasmSupported = true, // We're running in WASM, so it's supported
            wasmSIMDSupported = isWasmSIMDSupported(),
            wasmThreadsSupported = isWasmThreadsSupported(),
            moduleSupported = isModuleSupported(),
            dynamicImportSupported = isDynamicImportSupported(),
            webWorkersSupported = isWebWorkersSupported(),
            webGLSupported = isWebGLSupported(),
            webGL2Supported = isWebGL2Supported(),
            isMobile = isMobileDevice(),
            isTablet = isTabletDevice(),
            isDesktop = isDesktopDevice(),
            supportsTouch = isTouchSupported(),
            screenWidth = getScreenWidth(),
            screenHeight = getScreenHeight(),
            devicePixelRatio = getDevicePixelRatio(),
            colorDepth = getColorDepth()
        )
    } catch (e: Exception) {
        wasmConsoleWarn("Failed to detect browser info from WASM: ${e.message}")
        null
    }
}

/**
 * Returns whether WASM supported.
 *
 * @return The resulting value.
 */
actual fun isWasmSupported(): Boolean = true // Always true in WASM context

/**
 * Returns whether WASM simd supported.
 *
 * @return The resulting value.
 */
actual fun isWasmSIMDSupported(): Boolean {
    return try {
        wasmHasWasmSIMD()
    } catch (e: Exception) {
        false
    }
}

/**
 * Returns whether WASM threads supported.
 *
 * @return The resulting value.
 */
actual fun isWasmThreadsSupported(): Boolean {
    return try {
        wasmHasWasmThreads()
    } catch (e: Exception) {
        false
    }
}

/**
 * Returns whether module supported.
 *
 * @return The resulting value.
 */
actual fun isModuleSupported(): Boolean {
    return try {
        wasmHasModuleSupport()
    } catch (e: Exception) {
        false
    }
}

/**
 * Returns whether dynamic import supported.
 *
 * @return The resulting value.
 */
actual fun isDynamicImportSupported(): Boolean {
    return try {
        wasmHasDynamicImport()
    } catch (e: Exception) {
        false
    }
}

/**
 * Returns whether web workers supported.
 *
 * @return The resulting value.
 */
actual fun isWebWorkersSupported(): Boolean {
    return try {
        wasmHasWebWorkers()
    } catch (e: Exception) {
        false
    }
}

/**
 * Returns whether this value has DOM capabilities.
 *
 * @return The resulting value.
 */
actual fun hasDOMCapabilities(): Boolean = true

/**
 * Returns whether this value has ssr capabilities.
 *
 * @return The resulting value.
 */
actual fun hasSSRCapabilities(): Boolean = false

/**
 * Returns user agent.
 *
 * @return The resulting value.
 */
actual fun getUserAgent(): String? {
    return try {
        wasmGetUserAgent()
    } catch (e: Exception) {
        null
    }
}

/**
 * Returns current URL.
 *
 * @return The resulting value.
 */
actual fun getCurrentURL(): String? {
    return try {
        wasmGetLocationHref()
    } catch (e: Exception) {
        null
    }
}

/**
 * Returns whether mobile device.
 *
 * @return The resulting value.
 */
actual fun isMobileDevice(): Boolean {
    return try {
        val userAgent = getUserAgent() ?: return false
        wasmTestMobileUserAgent(userAgent)
    } catch (e: Exception) {
        false
    }
}

/**
 * Returns whether touch supported.
 *
 * @return The resulting value.
 */
actual fun isTouchSupported(): Boolean {
    return try {
        wasmHasTouchSupport()
    } catch (e: Exception) {
        false
    }
}

/**
 * Returns screen width.
 *
 * @return The resulting value.
 */
actual fun getScreenWidth(): Int {
    return try {
        wasmGetScreenWidth()
    } catch (e: Exception) {
        -1
    }
}

/**
 * Returns screen height.
 *
 * @return The resulting value.
 */
actual fun getScreenHeight(): Int {
    return try {
        wasmGetScreenHeight()
    } catch (e: Exception) {
        -1
    }
}

/**
 * Returns device pixel ratio.
 *
 * @return The resulting value.
 */
actual fun getDevicePixelRatio(): Double {
    return try {
        wasmGetDevicePixelRatio()
    } catch (e: Exception) {
        1.0
    }
}

// WASM-specific helper functions

private fun isWebGLSupported(): Boolean {
    return try {
        wasmHasWebGL()
    } catch (e: Exception) {
        false
    }
}

private fun isWebGL2Supported(): Boolean {
    return try {
        wasmHasWebGL2()
    } catch (e: Exception) {
        false
    }
}

private fun isTabletDevice(): Boolean {
    return try {
        val userAgent = getUserAgent() ?: return false
        wasmTestTabletUserAgent(userAgent)
    } catch (e: Exception) {
        false
    }
}

private fun isDesktopDevice(): Boolean {
    return !isMobileDevice() && !isTabletDevice()
}

private fun getColorDepth(): Int {
    return try {
        wasmGetColorDepth()
    } catch (e: Exception) {
        24
    }
}

private data class BrowserDetectionResult(
    val name: String,
    val version: String,
    val engine: String
)

private fun detectBrowserFromUserAgent(userAgent: String): BrowserDetectionResult {
    return try {
        when {
            // Chrome
            userAgent.contains("Chrome") && !userAgent.contains("Edge") -> {
                val version = extractVersionFromUA(userAgent, "Chrome/")
                BrowserDetectionResult("Chrome", version, "Blink")
            }
            // Firefox
            userAgent.contains("Firefox") -> {
                val version = extractVersionFromUA(userAgent, "Firefox/")
                BrowserDetectionResult("Firefox", version, "Gecko")
            }
            // Safari
            userAgent.contains("Safari") && !userAgent.contains("Chrome") -> {
                val version = extractVersionFromUA(userAgent, "Version/")
                BrowserDetectionResult("Safari", version, "WebKit")
            }
            // Edge
            userAgent.contains("Edge") || userAgent.contains("Edg/") -> {
                val version = extractVersionFromUA(userAgent, if (userAgent.contains("Edge")) "Edge/" else "Edg/")
                BrowserDetectionResult("Edge", version, "Blink")
            }
            // Opera
            userAgent.contains("Opera") || userAgent.contains("OPR") -> {
                val version = extractVersionFromUA(userAgent, if (userAgent.contains("Opera")) "Opera/" else "OPR/")
                BrowserDetectionResult("Opera", version, "Blink")
            }

            else -> {
                BrowserDetectionResult("Unknown", "0.0.0", "Unknown")
            }
        }
    } catch (e: Exception) {
        BrowserDetectionResult("Unknown", "0.0.0", "Unknown")
    }
}

private fun extractVersionFromUA(userAgent: String, prefix: String): String {
    return try {
        val startIndex = userAgent.indexOf(prefix)
        if (startIndex == -1) return "0.0.0"

        val versionStart = startIndex + prefix.length
        val versionEnd = userAgent.indexOf(" ", versionStart).let { if (it == -1) userAgent.length else it }
        userAgent.substring(versionStart, versionEnd).split(".").take(3).joinToString(".")
    } catch (e: Exception) {
        "0.0.0"
    }
}