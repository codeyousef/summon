package code.yousef.summon.web

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Storage

/**
 * JavaScript implementation of browser compatibility detection.
 */
actual object BrowserCapabilities {

    actual fun detect(): BrowserSupport {
        val browserInfo = detectBrowser()
        val wasmSupport = FeatureDetection.detectWasmSupport()
        val features = detectAllFeatures()

        return BrowserSupport(
            hasWasm = wasmSupport != WasmVersion.NONE,
            wasmVersion = wasmSupport,
            hasModules = FeatureDetection.hasModuleSupport(),
            hasServiceWorker = hasFeature(BrowserFeature.SERVICE_WORKERS),
            hasWebGL = hasFeature(BrowserFeature.WEBGL),
            browserName = browserInfo.first,
            browserVersion = browserInfo.second,
            isSupported = isCurrentBrowserSupported(browserInfo.first, browserInfo.second),
            features = features
        )
    }

    actual fun hasFeature(feature: BrowserFeature): Boolean {
        return when (feature) {
            BrowserFeature.MODERN_JS -> FeatureDetection.hasModernJSSupport()
            BrowserFeature.ES_MODULES -> FeatureDetection.hasModuleSupport()
            BrowserFeature.ASYNC_AWAIT -> hasAsyncAwait()
            BrowserFeature.FETCH_API -> hasFetchAPI()
            BrowserFeature.WEBSOCKETS -> hasWebSockets()
            BrowserFeature.LOCAL_STORAGE -> hasLocalStorage()
            BrowserFeature.SESSION_STORAGE -> hasSessionStorage()
            BrowserFeature.INDEXED_DB -> hasIndexedDB()
            BrowserFeature.WEB_WORKERS -> hasWebWorkers()
            BrowserFeature.SERVICE_WORKERS -> hasServiceWorkers()
            BrowserFeature.PUSH_NOTIFICATIONS -> hasPushNotifications()
            BrowserFeature.WEBGL -> hasWebGL()
            BrowserFeature.WEBGL2 -> hasWebGL2()
            BrowserFeature.WEBRTC -> hasWebRTC()
            BrowserFeature.TOUCH_EVENTS -> hasTouchEvents()
            BrowserFeature.POINTER_EVENTS -> hasPointerEvents()
            BrowserFeature.RESIZE_OBSERVER -> hasResizeObserver()
            BrowserFeature.INTERSECTION_OBSERVER -> hasIntersectionObserver()
            BrowserFeature.MUTATION_OBSERVER -> hasMutationObserver()
        }
    }

    actual fun getOptimalStrategy(): RenderingStrategy {
        val support = detect()

        return when {
            support.hasWasm && support.wasmVersion == WasmVersion.OPTIMIZED ->
                RenderingStrategy.WASM_OPTIMIZED

            support.hasWasm && support.wasmVersion >= WasmVersion.ADVANCED ->
                RenderingStrategy.WASM_OPTIMIZED

            support.hasModules && support.features.contains(BrowserFeature.MODERN_JS) ->
                RenderingStrategy.JS_MODERN

            support.features.contains(BrowserFeature.FETCH_API) ->
                RenderingStrategy.JS_COMPATIBLE

            else ->
                RenderingStrategy.STATIC_FALLBACK
        }
    }

    actual fun shouldUseWasm(): Boolean {
        val support = detect()

        // Don't use WASM on Safari < 15 due to known issues
        if (support.browserName == "safari" && support.browserVersion < 15) {
            return false
        }

        // Only use WASM if we have good support
        return support.hasWasm && support.wasmVersion >= WasmVersion.MVP
    }

    private fun detectBrowser(): Pair<String, Int> {
        val userAgent = js("navigator.userAgent") as String

        return when {
            userAgent.contains("Chrome") && !userAgent.contains("Edge") -> {
                val match = Regex("""Chrome/(\d+)""").find(userAgent)
                "chrome" to (match?.groupValues?.get(1)?.toIntOrNull() ?: 0)
            }

            userAgent.contains("Safari") && !userAgent.contains("Chrome") -> {
                val match = Regex("""Version/(\d+)""").find(userAgent)
                "safari" to (match?.groupValues?.get(1)?.toIntOrNull() ?: 0)
            }

            userAgent.contains("Firefox") -> {
                val match = Regex("""Firefox/(\d+)""").find(userAgent)
                "firefox" to (match?.groupValues?.get(1)?.toIntOrNull() ?: 0)
            }

            userAgent.contains("Edge") -> {
                val match = Regex("""Edge/(\d+)""").find(userAgent)
                "edge" to (match?.groupValues?.get(1)?.toIntOrNull() ?: 0)
            }

            else -> "unknown" to 0
        }
    }

    private fun detectAllFeatures(): Set<BrowserFeature> {
        val features = mutableSetOf<BrowserFeature>()

        BrowserFeature.values().forEach { feature ->
            if (hasFeature(feature)) {
                features.add(feature)
            }
        }

        return features
    }

    private fun isCurrentBrowserSupported(browserName: String, version: Int): Boolean {
        return when (browserName) {
            "chrome" -> version >= 90
            "safari" -> version >= 14
            "firefox" -> version >= 88
            "edge" -> version >= 90
            else -> false
        }
    }

    // Feature detection implementations

    private fun hasAsyncAwait(): Boolean {
        return js(
            """
            try {
                eval('(async function() {})');
                return true;
            } catch (e) {
                return false;
            }
        """
        ) as Boolean
    }

    private fun hasFetchAPI(): Boolean {
        return js("typeof fetch !== 'undefined'") as Boolean
    }

    private fun hasWebSockets(): Boolean {
        return js("typeof WebSocket !== 'undefined'") as Boolean
    }

    private fun hasLocalStorage(): Boolean {
        return try {
            val storage = js("window.localStorage") as? Storage
            storage?.setItem("test", "test")
            storage?.removeItem("test")
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun hasSessionStorage(): Boolean {
        return try {
            val storage = js("window.sessionStorage") as? Storage
            storage?.setItem("test", "test")
            storage?.removeItem("test")
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun hasIndexedDB(): Boolean {
        return js("typeof indexedDB !== 'undefined'") as Boolean
    }

    private fun hasWebWorkers(): Boolean {
        return js("typeof Worker !== 'undefined'") as Boolean
    }

    private fun hasServiceWorkers(): Boolean {
        return js("'serviceWorker' in navigator") as Boolean
    }

    private fun hasPushNotifications(): Boolean {
        return js("'PushManager' in window") as Boolean
    }

    private fun hasWebGL(): Boolean {
        return try {
            val canvas = document.createElement("canvas")
            val getContext = canvas.asDynamic().getContext
            if (getContext == undefined) {
                false
            } else {
                val gl = getContext.call(canvas, "webgl") ?: getContext.call(canvas, "experimental-webgl")
                gl != null
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun hasWebGL2(): Boolean {
        return try {
            val canvas = document.createElement("canvas")
            val getContext = canvas.asDynamic().getContext
            if (getContext == undefined) {
                false
            } else {
                val gl = getContext.call(canvas, "webgl2")
                gl != null
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun hasWebRTC(): Boolean {
        return js("typeof RTCPeerConnection !== 'undefined' || typeof webkitRTCPeerConnection !== 'undefined'") as Boolean
    }

    private fun hasTouchEvents(): Boolean {
        return js("'ontouchstart' in window") as Boolean
    }

    private fun hasPointerEvents(): Boolean {
        return js("typeof PointerEvent !== 'undefined'") as Boolean
    }

    private fun hasResizeObserver(): Boolean {
        return js("typeof ResizeObserver !== 'undefined'") as Boolean
    }

    private fun hasIntersectionObserver(): Boolean {
        return js("typeof IntersectionObserver !== 'undefined'") as Boolean
    }

    private fun hasMutationObserver(): Boolean {
        return js("typeof MutationObserver !== 'undefined'") as Boolean
    }
}

/**
 * JavaScript implementation of error boundary.
 */
class JSErrorBoundary : ErrorBoundary {

    override fun handleError(error: Throwable, context: String): ErrorAction {
        console.error("Error in $context:", error)

        return when (error) {
            is RuntimeException -> when {
                isMemoryError(error.message) ->
                    ErrorAction.Fallback(RenderingStrategy.STATIC_FALLBACK)

                error.message?.contains("security", ignoreCase = true) == true ->
                    ErrorAction.Graceful("Security error occurred")

                else -> ErrorAction.Retry
            }

            else -> when {
                isSecurityError(error.message) ->
                    ErrorAction.Graceful("Security error occurred")

                canRecover(error) -> ErrorAction.Retry
                else -> ErrorAction.Fallback(RenderingStrategy.JS_COMPATIBLE)
            }
        }
    }

    override fun reportError(error: Throwable, context: String, metadata: Map<String, Any>) {
        val errorReport = buildMap {
            put("error", error.message ?: "Unknown error")
            put("context", context)
            put("timestamp", js("Date.now()"))
            put("userAgent", js("navigator.userAgent"))
            put("url", window.location.href)
            putAll(metadata)
        }

        // Log locally
        console.error("Error Report:", errorReport)

        // Could send to error reporting service here
        // reportToService(errorReport)
    }

    override fun canRecover(error: Throwable): Boolean {
        return when (error) {
            is RuntimeException -> when {
                isMemoryError(error.message) -> false
                error.message?.contains("security", ignoreCase = true) == true -> false
                else -> true
            }

            else -> when {
                isSecurityError(error.message) -> false
                else -> !isMemoryError(error.message)
            }
        }
    }
}

private fun isMemoryError(message: String?): Boolean {
    if (message.isNullOrBlank()) return false
    val normalised = message.lowercase()
    return listOf("outofmemory", "no memory", "heap", "ran out of memory").any { it in normalised }
}

private fun isSecurityError(message: String?): Boolean {
    if (message.isNullOrBlank()) return false
    val normalised = message.lowercase()
    return "security" in normalised || "access denied" in normalised || "unauthorised" in normalised
}

/**
 * JavaScript implementation of performance monitor.
 */
class JSPerformanceMonitor : PerformanceMonitor {

    override fun mark(name: String) {
        if (js("'performance' in window && 'mark' in performance") as Boolean) {
            js("performance.mark(name)")
        }
    }

    override fun measure(name: String, startMark: String, endMark: String?): Double {
        if (js("'performance' in window && 'measure' in performance") as Boolean) {
            val measureResult = if (endMark != null) {
                js(
                    """
                    (function() {
                        try {
                            return performance.measure(name, startMark, endMark);
                        } catch (error) {
                            return null;
                        }
                    })()
                """
                )
            } else {
                js(
                    """
                    (function() {
                        try {
                            return performance.measure(name, startMark);
                        } catch (error) {
                            return null;
                        }
                    })()
                """
                )
            }
            val duration = measureResult?.unsafeCast<dynamic>()?.duration
            return when (duration) {
                is Number -> duration.toDouble()
                is String -> duration.toDoubleOrNull() ?: 0.0
                else -> 0.0
            }
        }
        return 0.0
    }

    override fun getMemoryUsage(): MemoryInfo {
        if (js("'performance' in window && 'memory' in performance") as Boolean) {
            val memory = js("performance.memory")
            fun dynamicToLong(value: dynamic): Long = when (value) {
                is Number -> value.toDouble().toLong()
                is String -> value.toDoubleOrNull()?.toLong() ?: 0L
                else -> 0L
            }
            return MemoryInfo(
                usedJSHeapSize = dynamicToLong(memory?.unsafeCast<dynamic>()?.usedJSHeapSize ?: 0),
                totalJSHeapSize = dynamicToLong(memory?.unsafeCast<dynamic>()?.totalJSHeapSize ?: 0),
                jsHeapSizeLimit = dynamicToLong(memory?.unsafeCast<dynamic>()?.jsHeapSizeLimit ?: 0)
            )
        }
        return MemoryInfo()
    }

    override fun getRenderingMetrics(): RenderingMetrics {
        val navigation = js("performance.navigation || {}")
        val timing = js("performance.timing || {}")

        return RenderingMetrics(
            frameRate = getFrameRate(),
            renderTime = js("timing.loadEventEnd - timing.navigationStart || 0") as Double,
            hydrationTime = getHydrationTime(),
            scriptLoadTime = js("timing.loadEventEnd - timing.domContentLoadedEventEnd || 0") as Double,
            domContentLoaded = js("timing.domContentLoadedEventEnd - timing.navigationStart || 0") as Double,
            firstContentfulPaint = getFirstContentfulPaint(),
            largestContentfulPaint = getLargestContentfulPaint()
        )
    }

    override fun reportMetrics(metrics: Map<String, Any>) {
        console.info("Performance Metrics:", metrics)

        // Could send to analytics service here
        // sendToAnalytics(metrics)
    }

    private fun getFrameRate(): Double {
        return js(
            """
            (function() {
                var lastTime = performance.now();
                var frameCount = 0;
                var fps = 0;

                function updateFPS() {
                    frameCount++;
                    var currentTime = performance.now();
                    if (currentTime >= lastTime + 1000) {
                        fps = Math.round((frameCount * 1000) / (currentTime - lastTime));
                        frameCount = 0;
                        lastTime = currentTime;
                    }
                }

                requestAnimationFrame(updateFPS);
                return fps;
            })()
        """
        ) as Double
    }

    private fun getHydrationTime(): Double {
        val app = document.getElementById("summon-app")
        return if (app?.hasAttribute("data-hydration-ready") == true) {
            js("performance.now()") as Double
        } else {
            0.0
        }
    }

    private fun getFirstContentfulPaint(): Double {
        return js(
            """
            (function() {
                if ('PerformanceObserver' in window) {
                    var observer = new PerformanceObserver(function(list) {
                        var entries = list.getEntries();
                        for (var i = 0; i < entries.length; i++) {
                            if (entries[i].name === 'first-contentful-paint') {
                                return entries[i].startTime;
                            }
                        }
                        return 0;
                    });
                    observer.observe({entryTypes: ['paint']});
                }
                return 0;
            })()
        """
        ) as Double
    }

    private fun getLargestContentfulPaint(): Double {
        return js(
            """
            (function() {
                if ('PerformanceObserver' in window) {
                    var observer = new PerformanceObserver(function(list) {
                        var entries = list.getEntries();
                        if (entries.length > 0) {
                            return entries[entries.length - 1].startTime;
                        }
                        return 0;
                    });
                    observer.observe({entryTypes: ['largest-contentful-paint']});
                }
                return 0;
            })()
        """
        ) as Double
    }
}

/**
 * Enhanced feature detection for JavaScript.
 */
object JSFeatureDetection {

    fun detectWasmSupport(): WasmVersion {
        return try {
            if (hasBasicWasm()) {
                when {
                    hasAdvancedWasm() -> {
                        // Check for browser-specific optimizations
                        val browserInfo = BrowserCapabilities.detect()
                        if (browserInfo.browserName == "chrome" && browserInfo.browserVersion >= 119) {
                            WasmVersion.OPTIMIZED
                        } else {
                            WasmVersion.ADVANCED
                        }
                    }

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
        return js(
            """
            (function() {
                try {
                    if (typeof WebAssembly === "object" && typeof WebAssembly.instantiate === "function") {
                        var module = new WebAssembly.Module(new Uint8Array([0x0, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00]));
                        if (module instanceof WebAssembly.Module) {
                            return new WebAssembly.Instance(module) instanceof WebAssembly.Instance;
                        }
                    }
                } catch (e) {}
                return false;
            })()
        """
        ) as Boolean
    }

    private fun hasAdvancedWasm(): Boolean {
        return js("typeof WebAssembly.Memory !== 'undefined'") as Boolean
    }

    fun hasModuleSupport(): Boolean {
        return js("'noModule' in HTMLScriptElement.prototype") as Boolean
    }

    fun hasModernJSSupport(): Boolean {
        return hasAsyncAwait() && hasArrowFunctions() && hasPromises()
    }

    private fun hasAsyncAwait(): Boolean {
        return js(
            """
            try {
                eval('(async function() {})');
                return true;
            } catch (e) {
                return false;
            }
        """
        ) as Boolean
    }

    private fun hasArrowFunctions(): Boolean {
        return js(
            """
            try {
                eval('(() => {})');
                return true;
            } catch (e) {
                return false;
            }
        """
        ) as Boolean
    }

    private fun hasPromises(): Boolean {
        return js("typeof Promise !== 'undefined'") as Boolean
    }
}
