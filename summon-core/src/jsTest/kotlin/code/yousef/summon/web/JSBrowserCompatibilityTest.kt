package code.yousef.summon.web

import kotlin.test.*

/**
 * JavaScript-specific tests for browser compatibility detection.
 */
class JSBrowserCompatibilityTest {

    @Test
    fun testJSBrowserDetection() {
        val support = BrowserCapabilities.detect()

        assertNotNull(support)
        assertTrue(support.browserName.isNotEmpty())
        assertTrue(support.browserVersion >= 0)
    }

    @Test
    fun testJSFeatureDetection() {
        // Test that JS platform can detect its own features
        val hasModules = BrowserCapabilities.hasFeature(BrowserFeature.ES_MODULES)
        val hasFetch = BrowserCapabilities.hasFeature(BrowserFeature.FETCH_API)
        val hasLocalStorage = BrowserCapabilities.hasFeature(BrowserFeature.LOCAL_STORAGE)

        // In a modern JS environment, these should typically be true
        // Note: These tests run in Node.js, so some browser features might not be available
        assertNotNull(hasModules) // Just verify we can call the function
        assertNotNull(hasFetch)
        assertNotNull(hasLocalStorage)
    }

    @Test
    fun testJSRenderingStrategy() {
        val strategy = BrowserCapabilities.getOptimalStrategy()

        assertNotNull(strategy)
        assertTrue(
            strategy in listOf(
                RenderingStrategy.WASM_OPTIMIZED,
                RenderingStrategy.JS_MODERN,
                RenderingStrategy.JS_COMPATIBLE,
                RenderingStrategy.STATIC_FALLBACK
            )
        )
    }

    @Test
    fun testJSWasmDecision() {
        val shouldUseWasm = BrowserCapabilities.shouldUseWasm()

        // This is just to verify the function can be called
        // The actual result depends on the runtime environment
        assertNotNull(shouldUseWasm)
    }

    @Test
    fun testJSErrorBoundary() {
        val errorBoundary = JSErrorBoundary()

        // Test error handling
        val testError = RuntimeException("Test error")
        val action = errorBoundary.handleError(testError, "test-context")

        assertNotNull(action)
        assertTrue(action is ErrorAction.Retry || action is ErrorAction.Fallback)

        // Test recovery determination
        val canRecover = errorBoundary.canRecover(testError)
        assertTrue(canRecover) // RuntimeException should be recoverable

        // Test non-recoverable error
        val outOfMemoryError = Exception("No memory")
        val canRecoverOOM = errorBoundary.canRecover(outOfMemoryError)
        assertFalse(canRecoverOOM) // OutOfMemoryError should not be recoverable
    }

    @Test
    fun testJSPerformanceMonitor() {
        val monitor = JSPerformanceMonitor()

        // Test marking and measuring
        monitor.mark("test-start")

        // Simulate some work
        var sum = 0
        for (i in 1..1000) {
            sum += i
        }

        monitor.mark("test-end")
        val duration = monitor.measure("test-duration", "test-start", "test-end")

        assertTrue(duration >= 0.0)

        // Test memory usage
        val memoryInfo = monitor.getMemoryUsage()
        assertNotNull(memoryInfo)
        assertTrue(memoryInfo.usedJSHeapSize >= 0)
        assertTrue(memoryInfo.totalJSHeapSize >= 0)
        assertTrue(memoryInfo.jsHeapSizeLimit >= 0)

        // Test rendering metrics
        val renderingMetrics = monitor.getRenderingMetrics()
        assertNotNull(renderingMetrics)
        assertTrue(renderingMetrics.frameRate >= 0.0)
        assertTrue(renderingMetrics.renderTime >= 0.0)
    }

    @Test
    fun testJSFeatureDetectionSpecific() {
        // Test JS-specific feature detection
        val detection = JSFeatureDetection

        val wasmSupport = detection.detectWasmSupport()
        assertNotNull(wasmSupport)
        assertTrue(wasmSupport in WasmVersion.values())

        val moduleSupport = detection.hasModuleSupport()
        assertNotNull(moduleSupport)

        val modernJSSupport = detection.hasModernJSSupport()
        assertNotNull(modernJSSupport)
    }

    @Test
    fun testJSPolyfillRequirements() {
        // Create a mock limited support scenario
        val limitedSupport = BrowserSupport(
            hasWasm = false,
            wasmVersion = WasmVersion.NONE,
            hasModules = false,
            browserName = "oldBrowser",
            browserVersion = 10,
            features = emptySet()
        )

        val polyfills = ProgressiveEnhancement.getRequiredPolyfills(limitedSupport)

        assertTrue(polyfills.isNotEmpty())
        assertTrue(polyfills.contains("fetch"))
        assertTrue(polyfills.contains("babel-polyfill"))
    }

    @Test
    fun testJSErrorReporting() {
        val errorBoundary = JSErrorBoundary()

        // Test error reporting with metadata
        val metadata = mapOf(
            "component" to "TestComponent",
            "userId" to "123",
            "action" to "click"
        )

        val testError = IllegalStateException("Invalid state")

        // This should not throw an exception
        errorBoundary.reportError(testError, "test-reporting", metadata)
    }

    @Test
    fun testJSMetricsReporting() {
        val monitor = JSPerformanceMonitor()

        val testMetrics = mapOf(
            "loadTime" to 1234.5,
            "renderTime" to 16.7,
            "memoryUsage" to 1024000
        )

        // This should not throw an exception
        monitor.reportMetrics(testMetrics)
    }

    @Test
    fun testJSBrowserSpecificOptimizations() {
        // Test that JS implementation can handle different browser types
        val chromeOptimizations = CompatibilityUtils.getBrowserOptimizations("chrome", 119)
        assertTrue(chromeOptimizations.contains("wasm-optimized"))

        val safariOptimizations = CompatibilityUtils.getBrowserOptimizations("safari", 15)
        assertTrue(safariOptimizations.contains("wasm-basic"))

        val firefoxOptimizations = CompatibilityUtils.getBrowserOptimizations("firefox", 110)
        assertTrue(firefoxOptimizations.contains("wasm-advanced"))
    }

    @Test
    fun testJSProgressiveEnhancement() {
        // Test progressive enhancement for different JS scenarios

        // Modern browser scenario
        val modernSupport = BrowserSupport(
            hasWasm = true,
            wasmVersion = WasmVersion.OPTIMIZED,
            hasModules = true,
            browserName = "chrome",
            browserVersion = 119,
            features = setOf(
                BrowserFeature.MODERN_JS,
                BrowserFeature.ES_MODULES,
                BrowserFeature.FETCH_API,
                BrowserFeature.SERVICE_WORKERS
            )
        )

        val modernPlan = ProgressiveEnhancement.enhance(modernSupport)
        assertEquals(EnhancementPlan.WASM_FULL, modernPlan)

        // Legacy browser scenario
        val legacySupport = BrowserSupport(
            hasWasm = false,
            wasmVersion = WasmVersion.NONE,
            hasModules = false,
            browserName = "oldBrowser",
            browserVersion = 50,
            features = setOf(BrowserFeature.FETCH_API)
        )

        val legacyPlan = ProgressiveEnhancement.enhance(legacySupport)
        assertEquals(EnhancementPlan.JS_COMPATIBLE, legacyPlan)
    }
}