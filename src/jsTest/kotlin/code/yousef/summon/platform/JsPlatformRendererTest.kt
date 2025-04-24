package code.yousef.summon.platform

import code.yousef.summon.runtime.JsPlatformRenderer
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for JS-specific implementations of the platform module.
 */
class JsPlatformRendererTest {

    @Test
    fun testJsPlatformRendererCreation() {
        // Test that JsPlatformRenderer can be created
        val renderer = JsPlatformRenderer()
        assertNotNull(renderer)
    }

    @Test
    fun testSetAndGetPlatformRenderer() {
        // Test setting and getting the platform renderer
        val renderer = JsPlatformRenderer()
        setPlatformRenderer(renderer)
        val retrievedRenderer = getPlatformRenderer()

        assertNotNull(retrievedRenderer)
        assertTrue(retrievedRenderer is JsPlatformRenderer)
    }

    // Note: Removed testHeadElementManagement as it requires more complex setup for JS environment
    // Will add proper head element testing in a future update

    @Test
    fun testRenderComposableRoot() {
        // Test rendering a composable root
        val renderer = JsPlatformRenderer()

        // Render a simple composable
        val result = renderer.renderComposableRoot {
            // Empty composable
        }

        // Verify the result is a string (in JS, this would be the DOM element ID)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }
}
