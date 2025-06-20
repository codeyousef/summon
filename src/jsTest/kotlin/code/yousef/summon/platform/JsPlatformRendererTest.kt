package code.yousef.summon.platform

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.PlatformRenderer
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
        // Test that PlatformRenderer can be created
        val renderer = PlatformRenderer()
        assertNotNull(renderer)
    }

    @Test
    fun testSetAndGetPlatformRenderer() {
        // Test setting and getting the platform renderer
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)
        val retrievedRenderer = getPlatformRenderer()

        assertNotNull(retrievedRenderer)
        // Verify we can use it as a PlatformRenderer by calling a method on it
        val result = retrievedRenderer.renderComposableRoot {
            retrievedRenderer.renderText("Test content", Modifier())
        }
        assertNotNull(result)
    }

    // Note: Removed testHeadElementManagement as it requires more complex setup for JS environment
    // Will add proper head element testing in a future update

    @Test
    fun testRenderComposableRoot() {
        // Test rendering a composable root
        val renderer = PlatformRenderer()

        // Render a composable with content
        val result = renderer.renderComposableRoot {
            // Add some content to ensure the result is not empty
            renderer.renderText("Test content", Modifier())
        }

        // Verify the result is a string (in JS, this would be the DOM element ID)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }
}
