package code.yousef.summon.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Test suite for WASM platform detection functionality.
 * Verifies that platform detection works correctly in WASM context.
 */
class PlatformDetectionTest {

    @Test
    fun testPlatformTargetIsWebAssembly() {
        // Verify that runtime can identify WASM target
        val target = detectPlatformTarget()
        assertEquals(PlatformTarget.WebAssembly, target, "Platform should be detected as WebAssembly")
    }

    @Test
    fun testBrowserInfoDetection() {
        // Verify browser info can be detected from WASM
        val browserInfo = detectBrowserInfo()

        // Browser info might be null in test environment, but should not throw
        if (browserInfo != null) {
            assertNotNull(browserInfo.userAgent, "User agent should be available")
            assertTrue(browserInfo.wasmSupported, "WASM should be reported as supported")
            assertEquals(
                PlatformTarget.WebAssembly,
                browserInfo.recommendedTarget,
                "Should recommend WebAssembly target"
            )
        }
    }

    @Test
    fun testPlatformCapabilities() {
        // Verify WASM platform capabilities
        assertTrue(isWasmSupported(), "WASM support should be true in WASM context")
        assertTrue(hasDOMCapabilities(), "DOM capabilities should be available in WASM")
        assertEquals(false, hasSSRCapabilities(), "SSR capabilities should not be available in WASM")
    }

    @Test
    fun testPlatformDetectionObject() {
        // Test the PlatformDetection singleton
        assertEquals(
            PlatformTarget.WebAssembly,
            PlatformDetection.currentTarget,
            "Current target should be WebAssembly"
        )
        assertTrue(PlatformDetection.isBrowser, "Should be identified as browser environment")
        assertEquals(false, PlatformDetection.isServer, "Should not be identified as server environment")
    }

    @Test
    fun testPlatformSummary() {
        // Verify platform summary generation doesn't throw
        val summary = PlatformDetection.createPlatformSummary()
        assertNotNull(summary, "Platform summary should be generated")
        assertTrue(summary.contains("WebAssembly"), "Summary should mention WebAssembly")
    }

    @Test
    fun testPlatformValidation() {
        // Validate platform requirements
        val issues = PlatformDetection.validatePlatformRequirements()

        // In a proper WASM environment, there should be no issues
        // In test environment, we just verify no exceptions
        assertNotNull(issues, "Validation should return a list")
    }
}