package code.yousef.summon.web

/**
 * Shared test stubs for browser compatibility testing
 */

enum class WasmVersion { NONE, MVP, BASIC, ADVANCED, OPTIMIZED }

enum class BrowserFeature {
    MODERN_JS, ES_MODULES, ASYNC_AWAIT, FETCH_API, WEBSOCKETS,
    LOCAL_STORAGE, SESSION_STORAGE, INDEXED_DB, WEB_WORKERS,
    SERVICE_WORKERS, WEBGL, WEBGL2, RESIZE_OBSERVER,
    INTERSECTION_OBSERVER, TOUCH_EVENTS, POINTER_EVENTS, WEBRTC
}

enum class EnhancementPlan {
    WASM_FULL, WASM_BASIC, JS_MODERN, JS_COMPATIBLE, STATIC_ONLY
}

sealed class ErrorAction {
    object Continue : ErrorAction()
    object Retry : ErrorAction()
    data class Fallback(val strategy: RenderingStrategy) : ErrorAction()
    data class Graceful(val message: String) : ErrorAction()
    object Fatal : ErrorAction()
}

enum class RenderingStrategy {
    WASM_OPTIMIZED, JS_MODERN, JS_COMPATIBLE, STATIC_FALLBACK
}

data class BrowserSupport(
    val hasWasm: Boolean = false,
    val wasmVersion: WasmVersion = WasmVersion.NONE,
    val hasModules: Boolean = false,
    val hasServiceWorker: Boolean = false,
    val hasWebGL: Boolean = false,
    val browserName: String = "",
    val browserVersion: Int = 0,
    val isSupported: Boolean = false,
    val features: Set<BrowserFeature> = emptySet()
)

data class MemoryInfo(
    val usedJSHeapSize: Long,
    val totalJSHeapSize: Long,
    val jsHeapSizeLimit: Long
)

data class RenderingMetrics(
    val frameRate: Double,
    val renderTime: Double,
    val hydrationTime: Double,
    val scriptLoadTime: Double,
    val domContentLoaded: Double,
    val firstContentfulPaint: Double,
    val largestContentfulPaint: Double
)

object ProgressiveEnhancement {
    fun enhance(support: BrowserSupport): EnhancementPlan {
        return when {
            support.hasWasm && support.wasmVersion == WasmVersion.OPTIMIZED -> EnhancementPlan.WASM_FULL
            support.hasWasm && support.wasmVersion == WasmVersion.ADVANCED -> EnhancementPlan.WASM_BASIC
            support.hasModules && support.features.contains(BrowserFeature.MODERN_JS) -> EnhancementPlan.JS_MODERN
            support.features.contains(BrowserFeature.FETCH_API) -> EnhancementPlan.JS_COMPATIBLE
            else -> EnhancementPlan.STATIC_ONLY
        }
    }

    fun getRequiredPolyfills(support: BrowserSupport): Set<String> {
        val polyfills = mutableSetOf<String>()
        if (!support.features.contains(BrowserFeature.FETCH_API)) polyfills.add("fetch")
        if (!support.features.contains(BrowserFeature.RESIZE_OBSERVER)) polyfills.add("resize-observer-polyfill")
        if (!support.features.contains(BrowserFeature.INTERSECTION_OBSERVER)) polyfills.add("intersection-observer")
        if (!support.hasModules) polyfills.add("babel-polyfill")
        return polyfills
    }
}

object CompatibilityUtils {
    fun getBrowserOptimizations(browserName: String, version: Int): Set<String> {
        return when (browserName.lowercase()) {
            "chrome" -> when {
                version >= 119 -> setOf("wasm-optimized", "v8-turbofan", "modern-js")
                version >= 80 -> setOf("wasm-basic", "modern-js")
                else -> setOf("modern-js")
            }

            "firefox" -> when {
                version >= 110 -> setOf("wasm-advanced", "modern-js")
                version >= 80 -> setOf("wasm-basic", "modern-js")
                else -> setOf("modern-js")
            }

            "safari" -> when {
                version >= 15 -> setOf("modern-js")
                else -> emptySet()
            }

            else -> emptySet()
        }
    }

    fun applyWorkarounds(support: BrowserSupport): Set<String> {
        val workarounds = mutableSetOf<String>()
        if (support.browserName == "safari" && !support.hasWasm) {
            workarounds.add("safari-wasm-fallback")
            workarounds.add("safari-module-fallback")
        }
        return workarounds
    }
}