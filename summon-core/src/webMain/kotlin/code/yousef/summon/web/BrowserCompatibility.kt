package codes.yousef.summon.web

/**
 * Browser compatibility utilities shared between JS and WASM implementations.
 *
 * Provides feature detection, capability assessment, and graceful degradation
 * strategies that work across all web platforms supported by Summon.
 */

/**
 * Browser support matrix for Summon features.
 */
data class BrowserSupport(
    val hasWasm: Boolean = false,
    val wasmVersion: WasmVersion = WasmVersion.NONE,
    val hasModules: Boolean = false,
    val hasServiceWorker: Boolean = false,
    val hasWebGL: Boolean = false,
    val browserName: String = "unknown",
    val browserVersion: Int = 0,
    val isSupported: Boolean = true,
    val features: Set<BrowserFeature> = emptySet()
)

/**
 * WASM capability levels.
 */
enum class WasmVersion {
    NONE,           // No WASM support
    MVP,            // Minimum Viable Product
    ADVANCED,       // Memory support
    OPTIMIZED       // Latest optimizations
}

/**
 * Supported browser features.
 */
enum class BrowserFeature {
    MODERN_JS,
    ES_MODULES,
    ASYNC_AWAIT,
    FETCH_API,
    WEBSOCKETS,
    LOCAL_STORAGE,
    SESSION_STORAGE,
    INDEXED_DB,
    WEB_WORKERS,
    SERVICE_WORKERS,
    PUSH_NOTIFICATIONS,
    WEBGL,
    WEBGL2,
    WEBRTC,
    TOUCH_EVENTS,
    POINTER_EVENTS,
    RESIZE_OBSERVER,
    INTERSECTION_OBSERVER,
    MUTATION_OBSERVER
}

/**
 * Platform-specific browser support detection.
 */
expect object BrowserCapabilities {
    /**
     * Detects current browser support capabilities.
     */
    fun detect(): BrowserSupport

    /**
     * Checks if a specific feature is supported.
     */
    fun hasFeature(feature: BrowserFeature): Boolean

    /**
     * Gets the optimal rendering strategy for the current browser.
     */
    fun getOptimalStrategy(): RenderingStrategy

    /**
     * Checks if WASM should be used over JS.
     */
    fun shouldUseWasm(): Boolean
}

/**
 * Rendering strategies based on browser capabilities.
 */
enum class RenderingStrategy {
    WASM_OPTIMIZED,     // Use WASM for maximum performance
    JS_MODERN,          // Use modern JS features
    JS_COMPATIBLE,      // Use compatible JS for older browsers
    STATIC_FALLBACK     // Server-rendered content only
}

/**
 * Error boundary handling for different platforms.
 */
interface ErrorBoundary {
    /**
     * Handles errors gracefully with appropriate fallback strategies.
     */
    fun handleError(error: Throwable, context: String): ErrorAction

    /**
     * Reports errors for monitoring and debugging.
     */
    fun reportError(error: Throwable, context: String, metadata: Map<String, Any> = emptyMap())

    /**
     * Checks if the system can recover from the error.
     */
    fun canRecover(error: Throwable): Boolean
}

/**
 * Actions to take when an error occurs.
 */
sealed class ErrorAction {
    object Continue : ErrorAction()
    object Retry : ErrorAction()
    data class Fallback(val strategy: RenderingStrategy) : ErrorAction()
    data class Graceful(val message: String) : ErrorAction()
    object Fatal : ErrorAction()
}

/**
 * Performance monitoring for web platforms.
 */
interface PerformanceMonitor {
    /**
     * Marks the start of a performance measurement.
     */
    fun mark(name: String)

    /**
     * Measures the time between two marks.
     */
    fun measure(name: String, startMark: String, endMark: String? = null): Double

    /**
     * Gets current memory usage information.
     */
    fun getMemoryUsage(): MemoryInfo

    /**
     * Gets rendering performance metrics.
     */
    fun getRenderingMetrics(): RenderingMetrics

    /**
     * Reports performance data for analysis.
     */
    fun reportMetrics(metrics: Map<String, Any>)
}

/**
 * Memory usage information.
 */
data class MemoryInfo(
    val usedJSHeapSize: Long = 0,
    val totalJSHeapSize: Long = 0,
    val jsHeapSizeLimit: Long = 0
)

/**
 * Rendering performance metrics.
 */
data class RenderingMetrics(
    val frameRate: Double = 0.0,
    val renderTime: Double = 0.0,
    val hydrationTime: Double = 0.0,
    val scriptLoadTime: Double = 0.0,
    val domContentLoaded: Double = 0.0,
    val firstContentfulPaint: Double = 0.0,
    val largestContentfulPaint: Double = 0.0
)

/**
 * Progressive enhancement utilities.
 */
object ProgressiveEnhancement {

    /**
     * Applies progressive enhancement based on browser capabilities.
     */
    fun enhance(support: BrowserSupport): EnhancementPlan {
        return when {
            support.hasWasm && support.wasmVersion == WasmVersion.OPTIMIZED ->
                EnhancementPlan.WASM_FULL

            support.hasWasm && support.wasmVersion >= WasmVersion.ADVANCED ->
                EnhancementPlan.WASM_BASIC

            support.hasModules && support.features.contains(BrowserFeature.MODERN_JS) ->
                EnhancementPlan.JS_MODERN

            support.features.contains(BrowserFeature.FETCH_API) ->
                EnhancementPlan.JS_COMPATIBLE

            else ->
                EnhancementPlan.STATIC_ONLY
        }
    }

    /**
     * Gets the list of polyfills needed for the current browser.
     */
    fun getRequiredPolyfills(support: BrowserSupport): List<String> {
        val polyfills = mutableListOf<String>()

        if (!support.features.contains(BrowserFeature.FETCH_API)) {
            polyfills.add("fetch")
        }

        if (!support.features.contains(BrowserFeature.ASYNC_AWAIT)) {
            polyfills.add("babel-polyfill")
        }

        if (!support.features.contains(BrowserFeature.RESIZE_OBSERVER)) {
            polyfills.add("resize-observer-polyfill")
        }

        if (!support.features.contains(BrowserFeature.INTERSECTION_OBSERVER)) {
            polyfills.add("intersection-observer")
        }

        return polyfills
    }
}

/**
 * Enhancement plans based on browser capabilities.
 */
enum class EnhancementPlan {
    WASM_FULL,          // Full WASM with all optimizations
    WASM_BASIC,         // Basic WASM support
    JS_MODERN,          // Modern JavaScript features
    JS_COMPATIBLE,      // Compatible JavaScript
    STATIC_ONLY         // Server-rendered content only
}

/**
 * Feature detection utilities.
 */
object FeatureDetection {

    /**
     * Detects WASM support level.
     */
    fun detectWasmSupport(): WasmVersion {
        return try {
            if (hasBasicWasm()) {
                when {
                    hasAdvancedWasm() -> WasmVersion.ADVANCED
                    else -> WasmVersion.MVP
                }
            } else {
                WasmVersion.NONE
            }
        } catch (e: Exception) {
            WasmVersion.NONE
        }
    }

    private fun hasBasicWasm(): Boolean {
        // Platform-specific implementation will be provided by expect/actual
        return false
    }

    private fun hasAdvancedWasm(): Boolean {
        // Platform-specific implementation will be provided by expect/actual
        return false
    }

    /**
     * Detects JavaScript module support.
     */
    fun hasModuleSupport(): Boolean {
        // Platform-specific implementation
        return false
    }

    /**
     * Detects modern JavaScript feature support.
     */
    fun hasModernJSSupport(): Boolean {
        return hasAsyncAwait() && hasArrowFunctions() && hasPromises()
    }

    private fun hasAsyncAwait(): Boolean = false
    private fun hasArrowFunctions(): Boolean = false
    private fun hasPromises(): Boolean = false

    /**
     * Detects storage capabilities.
     */
    fun detectStorageSupport(): Set<BrowserFeature> {
        val features = mutableSetOf<BrowserFeature>()

        if (hasLocalStorage()) features.add(BrowserFeature.LOCAL_STORAGE)
        if (hasSessionStorage()) features.add(BrowserFeature.SESSION_STORAGE)
        if (hasIndexedDB()) features.add(BrowserFeature.INDEXED_DB)

        return features
    }

    private fun hasLocalStorage(): Boolean = false
    private fun hasSessionStorage(): Boolean = false
    private fun hasIndexedDB(): Boolean = false
}

/**
 * Compatibility utilities for different browsers.
 */
object CompatibilityUtils {

    /**
     * Gets browser-specific optimizations.
     */
    fun getBrowserOptimizations(browserName: String, version: Int): List<String> {
        return when (browserName.lowercase()) {
            "chrome" -> getChromeOptimizations(version)
            "safari" -> getSafariOptimizations(version)
            "firefox" -> getFirefoxOptimizations(version)
            "edge" -> getEdgeOptimizations(version)
            else -> emptyList()
        }
    }

    private fun getChromeOptimizations(version: Int): List<String> {
        val optimizations = mutableListOf<String>()

        if (version >= 119) {
            optimizations.add("wasm-optimized")
            optimizations.add("v8-turbofan")
        }

        if (version >= 100) {
            optimizations.add("modern-js")
            optimizations.add("es-modules")
        }

        return optimizations
    }

    private fun getSafariOptimizations(version: Int): List<String> {
        val optimizations = mutableListOf<String>()

        if (version >= 15) {
            optimizations.add("wasm-basic")
        }

        if (version >= 14) {
            optimizations.add("modern-js")
        }

        return optimizations
    }

    private fun getFirefoxOptimizations(version: Int): List<String> {
        val optimizations = mutableListOf<String>()

        if (version >= 110) {
            optimizations.add("wasm-advanced")
        }

        if (version >= 100) {
            optimizations.add("modern-js")
        }

        return optimizations
    }

    private fun getEdgeOptimizations(version: Int): List<String> {
        val optimizations = mutableListOf<String>()

        if (version >= 110) {
            optimizations.add("wasm-basic")
            optimizations.add("modern-js")
        }

        return optimizations
    }

    /**
     * Applies browser-specific workarounds.
     */
    fun applyWorkarounds(support: BrowserSupport): List<String> {
        val workarounds = mutableListOf<String>()

        // Safari-specific workarounds
        if (support.browserName == "safari" && support.browserVersion < 15) {
            workarounds.add("safari-wasm-fallback")
            workarounds.add("safari-module-fallback")
        }

        // Firefox-specific workarounds
        if (support.browserName == "firefox" && support.browserVersion < 100) {
            workarounds.add("firefox-performance-fix")
        }

        return workarounds
    }
}