package codes.yousef.summon.core

/**
 * JavaScript-specific implementation of platform detection.
 *
 * This implementation uses browser APIs to detect capabilities and
 * gather information about the JavaScript execution environment.
 *
 * @since 0.3.3.0
 */

actual fun detectPlatformTarget(): PlatformTarget = PlatformTarget.JavaScript

actual fun detectBrowserInfo(): BrowserInfo? {
    return try {
        val userAgent = getUserAgent() ?: return null
        val browserDetection = detectBrowserFromUserAgent(userAgent)

        BrowserInfo(
            userAgent = userAgent,
            name = browserDetection.name,
            version = browserDetection.version,
            engine = browserDetection.engine,
            wasmSupported = isWasmSupported(),
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
        console.warn("Failed to detect browser info: ${e.message}")
        null
    }
}

actual fun isWasmSupported(): Boolean {
    return try {
        js("typeof WebAssembly === 'object' && typeof WebAssembly.instantiate === 'function'") as Boolean
    } catch (e: Exception) {
        false
    }
}

actual fun isWasmSIMDSupported(): Boolean {
    return try {
        // Check for WASM SIMD support
        val wasmSupported = isWasmSupported()
        if (!wasmSupported) return false

        // Try to compile a simple SIMD module
        js(
            """
            try {
                new WebAssembly.Module(new Uint8Array([
                    0, 97, 115, 109, 1, 0, 0, 0, 1, 5, 1, 96, 0, 1, 123,
                    3, 2, 1, 0, 10, 10, 1, 8, 0, 65, 0, 253, 15, 253, 98, 11
                ]));
                return true;
            } catch (e) {
                return false;
            }
        """
        ) as Boolean
    } catch (e: Exception) {
        false
    }
}

actual fun isWasmThreadsSupported(): Boolean {
    return try {
        js("typeof SharedArrayBuffer === 'function' && typeof Atomics === 'object'") as Boolean
    } catch (e: Exception) {
        false
    }
}

actual fun isModuleSupported(): Boolean {
    return try {
        js("'noModule' in document.createElement('script')") as Boolean
    } catch (e: Exception) {
        false
    }
}

actual fun isDynamicImportSupported(): Boolean {
    return false // Simple stub - avoid JS syntax issues with import keyword
}

actual fun isWebWorkersSupported(): Boolean {
    return try {
        js("typeof Worker === 'function'") as Boolean
    } catch (e: Exception) {
        false
    }
}

actual fun hasDOMCapabilities(): Boolean = true

actual fun hasSSRCapabilities(): Boolean = false

actual fun getUserAgent(): String? {
    return try {
        js("navigator.userAgent") as? String
    } catch (e: Exception) {
        null
    }
}

actual fun getCurrentURL(): String? {
    return try {
        js("window.location.href") as? String
    } catch (e: Exception) {
        null
    }
}

actual fun isMobileDevice(): Boolean {
    return try {
        val userAgent = getUserAgent() ?: return false
        val mobileRegex = js("/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i")
        js("mobileRegex.test(userAgent)") as Boolean
    } catch (e: Exception) {
        false
    }
}

actual fun isTouchSupported(): Boolean {
    return try {
        js("'ontouchstart' in window || navigator.maxTouchPoints > 0") as Boolean
    } catch (e: Exception) {
        false
    }
}

actual fun getScreenWidth(): Int {
    return try {
        js("window.screen.width") as? Int ?: -1
    } catch (e: Exception) {
        -1
    }
}

actual fun getScreenHeight(): Int {
    return try {
        js("window.screen.height") as? Int ?: -1
    } catch (e: Exception) {
        -1
    }
}

actual fun getDevicePixelRatio(): Double {
    return try {
        js("window.devicePixelRatio || 1.0") as? Double ?: 1.0
    } catch (e: Exception) {
        1.0
    }
}

// JavaScript-specific helper functions

private fun isWebGLSupported(): Boolean {
    return try {
        val canvas = js("document.createElement('canvas')")
        val context = js("canvas.getContext('webgl') || canvas.getContext('experimental-webgl')")
        js("context !== null") as Boolean
    } catch (e: Exception) {
        false
    }
}

private fun isWebGL2Supported(): Boolean {
    return try {
        val canvas = js("document.createElement('canvas')")
        val context = js("canvas.getContext('webgl2')")
        js("context !== null") as Boolean
    } catch (e: Exception) {
        false
    }
}

private fun isTabletDevice(): Boolean {
    return try {
        val userAgent = getUserAgent() ?: return false
        val tabletRegex = js("/iPad|Android.*tablet|Windows.*touch/i")
        js("tabletRegex.test(userAgent)") as Boolean
    } catch (e: Exception) {
        false
    }
}

private fun isDesktopDevice(): Boolean {
    return !isMobileDevice() && !isTabletDevice()
}

private fun getColorDepth(): Int {
    return try {
        js("window.screen.colorDepth || 24") as? Int ?: 24
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