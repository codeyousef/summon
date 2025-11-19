package codes.yousef.summon.web

import codes.yousef.summon.runtime.*

/**
 * WebAssembly implementation of browser compatibility detection.
 * Uses external JavaScript functions to access browser APIs.
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
            BrowserFeature.ASYNC_AWAIT -> wasmHasAsyncAwait()
            BrowserFeature.FETCH_API -> wasmHasFetchAPI()
            BrowserFeature.WEBSOCKETS -> wasmHasWebSockets()
            BrowserFeature.LOCAL_STORAGE -> wasmHasLocalStorage()
            BrowserFeature.SESSION_STORAGE -> wasmHasSessionStorage()
            BrowserFeature.INDEXED_DB -> wasmHasIndexedDB()
            BrowserFeature.WEB_WORKERS -> wasmHasWebWorkers()
            BrowserFeature.SERVICE_WORKERS -> wasmHasServiceWorkers()
            BrowserFeature.PUSH_NOTIFICATIONS -> wasmHasPushNotifications()
            BrowserFeature.WEBGL -> wasmHasWebGL()
            BrowserFeature.WEBGL2 -> wasmHasWebGL2()
            BrowserFeature.WEBRTC -> wasmHasWebRTC()
            BrowserFeature.TOUCH_EVENTS -> wasmHasTouchEvents()
            BrowserFeature.POINTER_EVENTS -> wasmHasPointerEvents()
            BrowserFeature.RESIZE_OBSERVER -> wasmHasResizeObserver()
            BrowserFeature.INTERSECTION_OBSERVER -> wasmHasIntersectionObserver()
            BrowserFeature.MUTATION_OBSERVER -> wasmHasMutationObserver()
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
        val userAgent = wasmGetUserAgent()

        return when {
            userAgent.contains("Chrome") && !userAgent.contains("Edge") -> {
                val version = wasmExtractBrowserVersion(userAgent, "Chrome")
                "chrome" to version
            }

            userAgent.contains("Safari") && !userAgent.contains("Chrome") -> {
                val version = wasmExtractBrowserVersion(userAgent, "Version")
                "safari" to version
            }

            userAgent.contains("Firefox") -> {
                val version = wasmExtractBrowserVersion(userAgent, "Firefox")
                "firefox" to version
            }

            userAgent.contains("Edge") -> {
                val version = wasmExtractBrowserVersion(userAgent, "Edge")
                "edge" to version
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
}

/**
 * WebAssembly implementation of error boundary.
 */
class WasmErrorBoundary : ErrorBoundary {

    override fun handleError(error: Throwable, context: String): ErrorAction {
        wasmConsoleError("Error in $context: ${error.message}")

        return when (error) {
            is kotlin.OutOfMemoryError -> ErrorAction.Fallback(RenderingStrategy.STATIC_FALLBACK)
            is RuntimeException -> if (error.message?.contains("security", ignoreCase = true) == true) {
                ErrorAction.Graceful("Security error occurred")
            } else {
                ErrorAction.Retry
            }

            else -> when {
                canRecover(error) -> ErrorAction.Retry
                else -> ErrorAction.Fallback(RenderingStrategy.JS_COMPATIBLE)
            }
        }
    }

    override fun reportError(error: Throwable, context: String, metadata: Map<String, Any>) {
        val errorMessage = "Error Report: ${error.message} in $context"
        wasmConsoleError(errorMessage)

        // Build error report data as string (since WASM can't pass complex objects directly)
        val reportData = buildString {
            append("error=${error.message ?: "Unknown"};")
            append("context=$context;")
            append("timestamp=${wasmGetTimestamp()};")
            append("userAgent=${wasmGetUserAgent()};")
            append("url=${wasmGetLocationHref()};")
            metadata.forEach { (key, value) ->
                append("$key=$value;")
            }
        }

        wasmReportError(reportData)
    }

    override fun canRecover(error: Throwable): Boolean {
        return when (error) {
            is kotlin.OutOfMemoryError -> false
            is RuntimeException -> !(error.message?.contains("security", ignoreCase = true) == true)
            else -> true
        }
    }
}

/**
 * WebAssembly implementation of performance monitor.
 */
class WasmPerformanceMonitor : PerformanceMonitor {

    override fun mark(name: String) {
        wasmPerformanceMark(name)
    }

    override fun measure(name: String, startMark: String, endMark: String?): Double {
        return if (endMark != null) {
            wasmPerformanceMeasure(name, startMark, endMark)
        } else {
            wasmPerformanceMeasureToNow(name, startMark)
        }
    }

    override fun getMemoryUsage(): MemoryInfo {
        return MemoryInfo(
            usedJSHeapSize = wasmGetUsedHeapSize(),
            totalJSHeapSize = wasmGetTotalHeapSize(),
            jsHeapSizeLimit = wasmGetHeapSizeLimit()
        )
    }

    override fun getRenderingMetrics(): RenderingMetrics {
        return RenderingMetrics(
            frameRate = wasmGetFrameRate(),
            renderTime = wasmGetRenderTime(),
            hydrationTime = wasmGetHydrationTime(),
            scriptLoadTime = wasmGetScriptLoadTime(),
            domContentLoaded = wasmGetDOMContentLoadedTime(),
            firstContentfulPaint = wasmGetFirstContentfulPaint(),
            largestContentfulPaint = wasmGetLargestContentfulPaint()
        )
    }

    override fun reportMetrics(metrics: Map<String, Any>) {
        val metricsString = metrics.entries.joinToString(";") { "${it.key}=${it.value}" }
        wasmReportMetrics(metricsString)
    }
}

/**
 * Enhanced feature detection for WebAssembly.
 */
object WasmFeatureDetection {

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
        return wasmTestBasicWasmSupport()
    }

    private fun hasAdvancedWasm(): Boolean {
        return wasmTestAdvancedWasmSupport()
    }

    fun hasModuleSupport(): Boolean {
        return wasmHasModuleSupport()
    }

    fun hasModernJSSupport(): Boolean {
        return hasAsyncAwait() && hasArrowFunctions() && hasPromises()
    }

    private fun hasAsyncAwait(): Boolean {
        return wasmHasAsyncAwait()
    }

    private fun hasArrowFunctions(): Boolean {
        return wasmHasArrowFunctions()
    }

    private fun hasPromises(): Boolean {
        return wasmHasPromises()
    }
}
