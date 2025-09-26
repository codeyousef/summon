package code.yousef.summon.web

import kotlin.test.*

/**
 * Tests for browser compatibility detection across all platforms.
 */
class BrowserCompatibilityTest {

    @Test
    fun testWasmVersionOrdering() {
        // Test that WASM versions are properly ordered
        assertTrue(WasmVersion.ADVANCED > WasmVersion.MVP)
        assertTrue(WasmVersion.OPTIMIZED > WasmVersion.ADVANCED)
        assertTrue(WasmVersion.MVP > WasmVersion.NONE)
    }

    @Test
    fun testBrowserFeatureDetection() {
        val allFeatures = BrowserFeature.values()
        assertTrue(allFeatures.isNotEmpty())

        // Verify that all features are represented
        assertTrue(allFeatures.contains(BrowserFeature.MODERN_JS))
        assertTrue(allFeatures.contains(BrowserFeature.ES_MODULES))
        assertTrue(allFeatures.contains(BrowserFeature.FETCH_API))
        assertTrue(allFeatures.contains(BrowserFeature.LOCAL_STORAGE))
    }

    @Test
    fun testRenderingStrategySelection() {
        // Test optimal strategy selection logic
        val wasmOptimizedSupport = BrowserSupport(
            hasWasm = true,
            wasmVersion = WasmVersion.OPTIMIZED,
            hasModules = true,
            features = setOf(BrowserFeature.MODERN_JS, BrowserFeature.ES_MODULES)
        )

        val plan = ProgressiveEnhancement.enhance(wasmOptimizedSupport)
        assertEquals(EnhancementPlan.WASM_FULL, plan)

        val basicSupport = BrowserSupport(
            hasWasm = false,
            wasmVersion = WasmVersion.NONE,
            hasModules = false,
            features = setOf(BrowserFeature.FETCH_API)
        )

        val basicPlan = ProgressiveEnhancement.enhance(basicSupport)
        assertEquals(EnhancementPlan.JS_COMPATIBLE, basicPlan)
    }

    @Test
    fun testProgressiveEnhancement() {
        // Test WASM full enhancement
        val wasmSupport = BrowserSupport(
            hasWasm = true,
            wasmVersion = WasmVersion.OPTIMIZED,
            hasModules = true,
            features = setOf(BrowserFeature.MODERN_JS, BrowserFeature.ES_MODULES)
        )

        val wasmPlan = ProgressiveEnhancement.enhance(wasmSupport)
        assertEquals(EnhancementPlan.WASM_FULL, wasmPlan)

        // Test JS modern enhancement
        val jsModernSupport = BrowserSupport(
            hasWasm = false,
            wasmVersion = WasmVersion.NONE,
            hasModules = true,
            features = setOf(BrowserFeature.MODERN_JS, BrowserFeature.ES_MODULES)
        )

        val jsModernPlan = ProgressiveEnhancement.enhance(jsModernSupport)
        assertEquals(EnhancementPlan.JS_MODERN, jsModernPlan)

        // Test fallback to static only
        val minimalSupport = BrowserSupport(
            hasWasm = false,
            wasmVersion = WasmVersion.NONE,
            hasModules = false,
            features = emptySet()
        )

        val staticPlan = ProgressiveEnhancement.enhance(minimalSupport)
        assertEquals(EnhancementPlan.STATIC_ONLY, staticPlan)
    }

    @Test
    fun testPolyfillDetection() {
        // Test that polyfills are correctly identified for missing features
        val limitedSupport = BrowserSupport(
            hasWasm = false,
            wasmVersion = WasmVersion.NONE,
            hasModules = false,
            features = emptySet() // No modern features
        )

        val polyfills = ProgressiveEnhancement.getRequiredPolyfills(limitedSupport)
        assertTrue(polyfills.contains("fetch"))
        assertTrue(polyfills.contains("babel-polyfill"))
        assertTrue(polyfills.contains("resize-observer-polyfill"))
        assertTrue(polyfills.contains("intersection-observer"))

        // Test that polyfills are not required when features are supported
        val modernSupport = BrowserSupport(
            hasWasm = true,
            wasmVersion = WasmVersion.ADVANCED,
            hasModules = true,
            features = setOf(
                BrowserFeature.FETCH_API,
                BrowserFeature.ASYNC_AWAIT,
                BrowserFeature.RESIZE_OBSERVER,
                BrowserFeature.INTERSECTION_OBSERVER
            )
        )

        val minimalPolyfills = ProgressiveEnhancement.getRequiredPolyfills(modernSupport)
        assertFalse(minimalPolyfills.contains("fetch"))
        assertFalse(minimalPolyfills.contains("babel-polyfill"))
        assertFalse(minimalPolyfills.contains("resize-observer-polyfill"))
        assertFalse(minimalPolyfills.contains("intersection-observer"))
    }

    @Test
    fun testErrorActionTypes() {
        // Test that all error action types are properly structured
        val continueAction = ErrorAction.Continue
        val retryAction = ErrorAction.Retry
        val fallbackAction = ErrorAction.Fallback(RenderingStrategy.JS_COMPATIBLE)
        val gracefulAction = ErrorAction.Graceful("Test message")
        val fatalAction = ErrorAction.Fatal

        assertNotNull(continueAction)
        assertNotNull(retryAction)
        assertNotNull(fallbackAction)
        assertNotNull(gracefulAction)
        assertNotNull(fatalAction)

        // Test fallback action properties
        @Suppress("USELESS_IS_CHECK")
        assertTrue(fallbackAction is ErrorAction.Fallback)
        assertEquals(RenderingStrategy.JS_COMPATIBLE, fallbackAction.strategy)

        // Test graceful action properties
        @Suppress("USELESS_IS_CHECK")
        assertTrue(gracefulAction is ErrorAction.Graceful)
        assertEquals("Test message", gracefulAction.message)
    }

    @Test
    fun testMemoryInfoStructure() {
        val memoryInfo = MemoryInfo(
            usedJSHeapSize = 1024L,
            totalJSHeapSize = 2048L,
            jsHeapSizeLimit = 4096L
        )

        assertEquals(1024L, memoryInfo.usedJSHeapSize)
        assertEquals(2048L, memoryInfo.totalJSHeapSize)
        assertEquals(4096L, memoryInfo.jsHeapSizeLimit)
    }

    @Test
    fun testRenderingMetricsStructure() {
        val metrics = RenderingMetrics(
            frameRate = 60.0,
            renderTime = 16.67,
            hydrationTime = 100.0,
            scriptLoadTime = 500.0,
            domContentLoaded = 1000.0,
            firstContentfulPaint = 1200.0,
            largestContentfulPaint = 1500.0
        )

        assertEquals(60.0, metrics.frameRate)
        assertEquals(16.67, metrics.renderTime)
        assertEquals(100.0, metrics.hydrationTime)
        assertEquals(500.0, metrics.scriptLoadTime)
        assertEquals(1000.0, metrics.domContentLoaded)
        assertEquals(1200.0, metrics.firstContentfulPaint)
        assertEquals(1500.0, metrics.largestContentfulPaint)
    }

    @Test
    fun testBrowserOptimizations() {
        // Test Chrome optimizations
        val chromeOptimizations = CompatibilityUtils.getBrowserOptimizations("chrome", 119)
        assertTrue(chromeOptimizations.contains("wasm-optimized"))
        assertTrue(chromeOptimizations.contains("v8-turbofan"))
        assertTrue(chromeOptimizations.contains("modern-js"))

        // Test Safari optimizations
        val safariOptimizations = CompatibilityUtils.getBrowserOptimizations("safari", 15)
        assertTrue(safariOptimizations.contains("wasm-basic"))
        assertTrue(safariOptimizations.contains("modern-js"))

        // Test older Safari limitations
        val oldSafariOptimizations = CompatibilityUtils.getBrowserOptimizations("safari", 14)
        assertFalse(oldSafariOptimizations.contains("wasm-basic"))
        assertTrue(oldSafariOptimizations.contains("modern-js"))

        // Test Firefox optimizations
        val firefoxOptimizations = CompatibilityUtils.getBrowserOptimizations("firefox", 110)
        assertTrue(firefoxOptimizations.contains("wasm-advanced"))
        assertTrue(firefoxOptimizations.contains("modern-js"))

        // Test unknown browser
        val unknownOptimizations = CompatibilityUtils.getBrowserOptimizations("unknown", 100)
        assertTrue(unknownOptimizations.isEmpty())
    }

    @Test
    fun testBrowserWorkarounds() {
        // Test Safari workarounds
        val safariSupport = BrowserSupport(
            browserName = "safari",
            browserVersion = 14,
            hasWasm = false
        )

        val safariWorkarounds = CompatibilityUtils.applyWorkarounds(safariSupport)
        assertTrue(safariWorkarounds.contains("safari-wasm-fallback"))
        assertTrue(safariWorkarounds.contains("safari-module-fallback"))

        // Test Firefox workarounds
        val firefoxSupport = BrowserSupport(
            browserName = "firefox",
            browserVersion = 99,
            hasWasm = true
        )

        val firefoxWorkarounds = CompatibilityUtils.applyWorkarounds(firefoxSupport)
        assertTrue(firefoxWorkarounds.contains("firefox-performance-fix"))

        // Test modern browsers don't need workarounds
        val modernSupport = BrowserSupport(
            browserName = "chrome",
            browserVersion = 120,
            hasWasm = true
        )

        val modernWorkarounds = CompatibilityUtils.applyWorkarounds(modernSupport)
        assertTrue(modernWorkarounds.isEmpty())
    }

    @Test
    fun testEnhancementPlanProgression() {
        // Test that enhancement plans progress logically
        val plans = EnhancementPlan.values()

        assertTrue(plans.contains(EnhancementPlan.WASM_FULL))
        assertTrue(plans.contains(EnhancementPlan.WASM_BASIC))
        assertTrue(plans.contains(EnhancementPlan.JS_MODERN))
        assertTrue(plans.contains(EnhancementPlan.JS_COMPATIBLE))
        assertTrue(plans.contains(EnhancementPlan.STATIC_ONLY))

        // Verify logical progression order
        assertTrue(EnhancementPlan.WASM_FULL.ordinal < EnhancementPlan.STATIC_ONLY.ordinal)
        assertTrue(EnhancementPlan.JS_MODERN.ordinal < EnhancementPlan.JS_COMPATIBLE.ordinal)
    }

    @Test
    fun testFeatureDetectionCompleteness() {
        // Ensure all major browser features are covered
        val features = BrowserFeature.values().toSet()

        // Core features that should be present
        val coreFeatures = setOf(
            BrowserFeature.MODERN_JS,
            BrowserFeature.ES_MODULES,
            BrowserFeature.FETCH_API,
            BrowserFeature.LOCAL_STORAGE,
            BrowserFeature.SERVICE_WORKERS,
            BrowserFeature.WEBGL
        )

        assertTrue(features.containsAll(coreFeatures))

        // Advanced features
        val advancedFeatures = setOf(
            BrowserFeature.WEBGL2,
            BrowserFeature.WEBRTC,
            BrowserFeature.RESIZE_OBSERVER,
            BrowserFeature.INTERSECTION_OBSERVER
        )

        assertTrue(features.containsAll(advancedFeatures))
    }
}