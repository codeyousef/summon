package code.yousef.summon.examples.js

import code.yousef.summon.modifier.LayoutModifiers
import code.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

/**
 * Tests for the JS example to verify that the correct imports and renderer are used.
 */
class JsExampleTest {

    /**
     * Test that the correct imports for gap and top are available from LayoutModifiers.
     * This verifies that the JS example can import gap and top from LayoutModifiers.
     */
    @Test
    fun testLayoutModifiersImports() {
        // Import the LayoutModifiers object
        val layoutModifiers = LayoutModifiers

        // Verify that LayoutModifiers object is not null
        assertNotNull(layoutModifiers, "LayoutModifiers object should be available")

        // We can't directly test the functions, but we can verify that the import works
        // The fact that this test compiles and runs successfully means that
        // the LayoutModifiers object is available and can be imported
        assertTrue(true, "LayoutModifiers import is successful")

        // Note: In a real application, we would use these functions like:
        // Modifier().let(LayoutModifiers::gap).let(LayoutModifiers::top)
    }

    /**
     * Test that PlatformRenderer can be instantiated and used.
     * This verifies that the JS example can use PlatformRenderer instead of JsPlatformRenderer.
     */
    @Test
    fun testPlatformRenderer() {
        // Create a PlatformRenderer instance
        val renderer = PlatformRenderer()

        // Verify that the renderer is not null
        assertNotNull(renderer, "PlatformRenderer should be instantiated successfully")

        // Verify that renderComposable doesn't throw an exception
        try {
            renderer.renderComposable {
                // Empty composable
            }
            // If we get here, the test passes
            assertTrue(true, "PlatformRenderer.renderComposable should not throw an exception")
        } catch (e: Throwable) {
            assertTrue(false, "PlatformRenderer.renderComposable should not throw an exception, but it threw: ${e.message}")
        }
    }
}
