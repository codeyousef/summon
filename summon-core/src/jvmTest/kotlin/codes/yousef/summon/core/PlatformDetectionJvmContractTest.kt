package codes.yousef.summon.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlatformDetectionJvmContractTest {
    @Test
    fun jvmCapabilitiesDegradeHonestlyWithoutBrowserState() {
        assertTrue(PlatformDetection.currentTarget is PlatformTarget.JVM)
        assertNull(PlatformDetection.browserInfo)
        assertFalse(PlatformDetection.isBrowser)
        assertTrue(PlatformDetection.isServer)
        assertFalse(PlatformDetection.isWasmSuitable)
        assertFalse(PlatformDetection.requiresJSFallback)
        assertEquals(PlatformDetection.currentTarget, PlatformDetection.recommendedTarget)
        assertEquals(85, PlatformDetection.capabilityScore)
        assertEquals(emptyList(), PlatformDetection.validatePlatformRequirements())
        assertFalse(PlatformDetection.shouldDegrade())
        assertNull(PlatformDetection.getFallbackTarget())
        assertTrue(PlatformDetection.createPlatformSummary().contains("Target: PlatformTarget.JVM"))

        assertNull(detectBrowserInfo())
        assertFalse(isWasmSupported())
        assertFalse(isWasmSIMDSupported())
        assertFalse(isModuleSupported())
        assertFalse(isDynamicImportSupported())
        assertFalse(isWebWorkersSupported())
        assertFalse(isWasmThreadsSupported())
        assertFalse(hasDOMCapabilities())
        assertTrue(hasSSRCapabilities())
        assertNull(getUserAgent())
        assertFalse(isTouchSupported())
        assertNull(getCurrentURL())
        assertFalse(isMobileDevice())
        assertEquals(-1, getScreenWidth())
        assertEquals(-1, getScreenHeight())
        assertEquals(1.0, getDevicePixelRatio())
    }
    @Test
    fun validationCoversEveryTargetAndMissingCapabilityCombination() {
        val modern = BrowserInfo.createTestInfo()
        val legacy = modern.copy(
            wasmSupported = false,
            moduleSupported = false,
            dynamicImportSupported = false
        )

        assertEquals(
            listOf("JVM target requires SSR capabilities"),
            PlatformDetection.validatePlatformRequirements(PlatformTarget.JVM, null, false)
        )
        assertEquals(
            emptyList(),
            PlatformDetection.validatePlatformRequirements(PlatformTarget.JVM, null, true)
        )
        assertEquals(
            listOf("Browser information not available for JavaScript target"),
            PlatformDetection.validatePlatformRequirements(PlatformTarget.JavaScript, null, false)
        )
        assertEquals(
            listOf(
                "ES6 modules are required for JavaScript target",
                "Dynamic imports are required for code splitting"
            ),
            PlatformDetection.validatePlatformRequirements(PlatformTarget.JavaScript, legacy, false)
        )
        assertEquals(
            listOf("Browser information not available for WASM target"),
            PlatformDetection.validatePlatformRequirements(PlatformTarget.WebAssembly, null, false)
        )
        assertEquals(
            listOf(
                "WebAssembly support is required for WASM target",
                "ES6 modules are required for WASM target"
            ),
            PlatformDetection.validatePlatformRequirements(PlatformTarget.WebAssembly, legacy, false)
        )
        assertEquals(
            emptyList(),
            PlatformDetection.validatePlatformRequirements(PlatformTarget.WebAssembly, modern, false)
        )
    }

}
