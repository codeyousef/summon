package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.span
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Tests for the JvmPlatformRenderer implementation of renderComposable.
 * These tests verify that renderComposable is properly implemented on the JVM platform.
 */
class JvmPlatformRendererTest {

    /**
     * Test that renderComposable does not throw NotImplementedError.
     * This verifies that the JVM implementation of renderComposable is properly implemented.
     */
    @Test
    fun testRenderComposableDoesNotThrowNotImplementedError() {
        // Create a JvmPlatformRenderer
        val renderer = createJvmPlatformRenderer()

        try {
            // First render a root to establish the context
            val html = renderer.renderComposableRoot {
                // Then call renderComposable within that context
                try {
                    renderer.renderComposable {
                        // Empty composable
                    }
                } catch (e: Throwable) {
                    // If an exception was thrown, the test fails
                    fail("renderComposable should not throw an exception, but it threw: ${e.message}")
                }
            }

            // Verify that the HTML was generated
            assertNotNull(html, "renderComposableRoot should return non-null HTML")
            assertTrue(html.isNotEmpty(), "renderComposableRoot should return non-empty HTML")

            // Verify that the HTML does not contain an error message
            assertFalse(html.contains("NotImplementedError"), "HTML should not contain NotImplementedError")
            assertFalse(html.contains("Platform-specific implementation required"), 
                "HTML should not contain 'Platform-specific implementation required'")
        } catch (e: Throwable) {
            // If an exception was thrown, the test fails
            fail("renderComposableRoot should not throw an exception, but it threw: ${e.message}")
        }
    }

    /**
     * Test that renderComposable correctly renders content.
     * This verifies that the JVM implementation of renderComposable actually renders the content.
     */
    @Test
    fun testRenderComposableRendersContent() {
        // Create a JvmPlatformRenderer
        val renderer = createJvmPlatformRenderer()

        // Create a marker string that we'll look for in the output
        val testMarker = "TEST_MARKER_" + System.currentTimeMillis()

        // Render a root with a composable that adds content with our marker
        val html = renderer.renderComposableRoot {
            // Use renderDiv which is a public method
            renderer.renderDiv(Modifier()) {
                // Inside this context, call renderComposable
                renderer.renderComposable {
                    // This will be rendered inside the div
                    span {
                        +testMarker
                    }
                }
            }
        }

        // Verify that the HTML contains our marker
        assertTrue(html.contains(testMarker), "HTML should contain the test marker")
    }

    /**
     * Helper method to create a JvmPlatformRenderer for testing.
     */
    private fun createJvmPlatformRenderer(): PlatformRenderer {
        return PlatformRenderer()
    }
}
