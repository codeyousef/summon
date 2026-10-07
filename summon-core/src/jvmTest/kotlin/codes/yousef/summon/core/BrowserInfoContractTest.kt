package codes.yousef.summon.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BrowserInfoContractTest {
    private fun info(
        wasm: Boolean = false,
        simd: Boolean = false,
        modules: Boolean = false,
        dynamicImports: Boolean = false,
        workers: Boolean = false,
        webGl: Boolean = false,
        mobile: Boolean = false,
        tablet: Boolean = false,
        desktop: Boolean = false,
        pixelRatio: Double = 1.0,
        colorDepth: Int = 16
    ) = BrowserInfo(
        userAgent = "contract", name = "Contract", version = "1", engine = "Test",
        wasmSupported = wasm, wasmSIMDSupported = simd, moduleSupported = modules,
        dynamicImportSupported = dynamicImports, webWorkersSupported = workers,
        webGLSupported = webGl, isMobile = mobile, isTablet = tablet, isDesktop = desktop,
        supportsTouch = mobile || tablet, screenWidth = 100, screenHeight = 100,
        devicePixelRatio = pixelRatio, colorDepth = colorDepth, timestamp = 1L
    )

    @Test
    fun browserRecommendationsRequireCompleteCapabilities() {
        val capable = info(
            wasm = true, simd = true, modules = true, dynamicImports = true,
            workers = true, webGl = true, desktop = true, pixelRatio = 2.0, colorDepth = 24
        )
        assertTrue(capable.isModernBrowser)
        assertTrue(capable.preferWasm)
        assertTrue(capable.isHighPerformance)
        assertEquals(PlatformTarget.WebAssembly, capable.recommendedTarget)
        assertEquals(100, capable.capabilityScore)

        val mobile = info(
            wasm = true, simd = true, modules = true, dynamicImports = true,
            workers = true, webGl = true, mobile = true, pixelRatio = 2.0, colorDepth = 24
        )
        assertFalse(mobile.preferWasm)
        assertFalse(mobile.isHighPerformance)
        assertEquals(PlatformTarget.JavaScript, mobile.recommendedTarget)
        assertEquals(90, mobile.capabilityScore)

        val tablet = info(tablet = true)
        assertEquals(15, tablet.capabilityScore)
        val minimal = info()
        assertEquals(0, minimal.capabilityScore)
        assertFalse(minimal.isModernBrowser)
        assertFalse(minimal.preferWasm)
        assertFalse(minimal.isHighPerformance)
    }

    @Test
    fun testFactoryAndJvmPlatformReportConsistentCapabilities() {
        val desktop = BrowserInfo.createTestInfo()
        assertTrue(desktop.isModernBrowser)
        assertTrue(desktop.preferWasm)
        val mobile = BrowserInfo.createTestInfo(wasmSupported = false, isModern = false, isMobile = true)
        assertFalse(mobile.isModernBrowser)
        assertFalse(mobile.preferWasm)

        assertEquals(PlatformTarget.JVM, detectPlatformTarget())
        assertNull(detectBrowserInfo())
        assertFalse(isWasmSupported())
        assertFalse(isWasmSIMDSupported())
        assertFalse(isWasmThreadsSupported())
        assertFalse(isModuleSupported())
        assertFalse(isDynamicImportSupported())
        assertFalse(isWebWorkersSupported())
        assertFalse(hasDOMCapabilities())
        assertTrue(hasSSRCapabilities())
        assertNull(getUserAgent())
        assertNull(getCurrentURL())
        assertFalse(isMobileDevice())
        assertFalse(isTouchSupported())
        assertEquals(-1, getScreenWidth())
        assertEquals(-1, getScreenHeight())
        assertEquals(1.0, getDevicePixelRatio())

        assertEquals(PlatformTarget.JVM, PlatformDetection.currentTarget)
        assertFalse(PlatformDetection.isBrowser)
        assertTrue(PlatformDetection.isServer)
        assertFalse(PlatformDetection.isWasmSuitable)
        assertFalse(PlatformDetection.requiresJSFallback)
        assertEquals(PlatformTarget.JVM, PlatformDetection.recommendedTarget)
        assertEquals(85, PlatformDetection.capabilityScore)
        assertTrue(PlatformDetection.validatePlatformRequirements().isEmpty())
        assertFalse(PlatformDetection.shouldDegrade())
        assertNull(PlatformDetection.getFallbackTarget())
        assertTrue(PlatformDetection.createPlatformSummary().contains("Target: PlatformTarget.JVM"))
        assertTrue(getCurrentTimeMillis() > 0)
    }
}
