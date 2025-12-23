package codes.yousef.summon.runtime

import codes.yousef.summon.modifier.*
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertSame

/**
 * Tests for the PlatformRendererAccessor utility functions
 */
class PlatformRendererAccessorTest {

    @AfterTest
    fun tearDown() {
        CallbackRegistry.clear()
        clearPlatformRenderer()
    }

    @Test
    fun testSetAndGetPlatformRenderer() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set the platform renderer
        setPlatformRenderer(mockRenderer)

        // Get the platform renderer and verify it's the same instance
        val retrievedRenderer = getPlatformRenderer()
        assertSame(mockRenderer, retrievedRenderer, "getPlatformRenderer should return the same instance that was set")

        // Test that we can use the retrieved renderer
        retrievedRenderer.renderText("Test", Modifier())
    }

    // Note: We can't easily reset the renderer to null for testing in Kotlin Multiplatform
    // since reflection works differently across platforms.
    // Instead, we'll test the behavior when changing renderers.

    @Test
    fun testSetPlatformRendererMultipleTimes() {
        // Create two mock renderers
        val mockRenderer1 = MockPlatformRenderer()
        val mockRenderer2 = MockPlatformRenderer()

        // Set the first renderer
        setPlatformRenderer(mockRenderer1)

        // Verify the first renderer is returned
        val retrievedRenderer1 = getPlatformRenderer()
        assertSame(mockRenderer1, retrievedRenderer1, "First renderer should be returned")

        // Set the second renderer
        setPlatformRenderer(mockRenderer2)

        // Verify the second renderer is now returned
        val retrievedRenderer2 = getPlatformRenderer()
        assertSame(mockRenderer2, retrievedRenderer2, "Second renderer should be returned after setting it")
    }

    @Test
    fun testRendererFunctionality() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set the platform renderer
        setPlatformRenderer(mockRenderer)

        // Get the platform renderer
        val retrievedRenderer = getPlatformRenderer()

        // Test multiple renderer methods
        retrievedRenderer.renderText("Hello", Modifier())

        // Test again with different text
        retrievedRenderer.renderText("World", Modifier())
    }
}
