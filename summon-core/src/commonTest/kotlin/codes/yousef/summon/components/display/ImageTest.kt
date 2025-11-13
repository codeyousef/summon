package codes.yousef.summon.components.display

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.Composer
import codes.yousef.summon.runtime.CompositionLocal
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.MockPlatformRenderer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for the Image component
 */
class ImageTest {

    // Mock implementation of Composer for testing
    private class MockComposer : Composer {
        override val inserting: Boolean = true

        override fun startNode() {}
        override fun startGroup(key: Any?) {}
        override fun endNode() {}
        override fun endGroup() {}
        override fun changed(value: Any?): Boolean = true
        override fun updateValue(value: Any?) {}
        override fun nextSlot() {}
        override fun getSlot(): Any? = null
        override fun setSlot(value: Any?) {}
        override fun recordRead(state: Any) {}
        override fun recordWrite(state: Any) {}
        override fun reportChanged() {}
        override fun registerDisposable(disposable: () -> Unit) {}
        override fun dispose() {}
        override fun startCompose() {}
        override fun endCompose() {}
        override fun <T> compose(composable: @Composable () -> T): T {
            @Suppress("UNCHECKED_CAST")
            return null as T
        }

        override fun recompose() {
            // Mock implementation
        }

        override fun rememberedValue(key: Any): Any? {
            return null
        }

        override fun updateRememberedValue(key: Any, value: Any?) {
            // Mock implementation
        }
    }

    @Test
    fun testImageWithDefaultParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Image component with default parameters
            Image(
                src = "https://example.com/image.jpg",
                alt = "Example image"
            )

            // Verify that renderImage was called
            assertTrue(mockRenderer.renderImageCalled, "renderImage should have been called")

            // Verify the src
            assertEquals(
                "https://example.com/image.jpg",
                mockRenderer.lastImageSrcRendered,
                "src should be 'https://example.com/image.jpg'"
            )

            // Verify the alt
            assertEquals("Example image", mockRenderer.lastImageAltRendered, "alt should be 'Example image'")

            // Verify the modifier
            assertNotNull(mockRenderer.lastImageModifierRendered, "Modifier should not be null")
            assertEquals(
                Modifier().styles,
                mockRenderer.lastImageModifierRendered!!.styles,
                "Modifier should be the default"
            )
        }
    }

    @Test
    fun testImageWithCustomModifier() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Create a custom Modifier
            val customModifier = Modifier()
                .padding("10px")
                .backgroundColor("#EFEFEF")
                .border("1px", "solid", "black")
                .borderRadius("5px")

            // Call the Image component with custom modifier
            Image(
                src = "https://example.com/custom.png",
                alt = "Custom image",
                modifier = customModifier
            )

            // Verify that renderImage was called
            assertTrue(mockRenderer.renderImageCalled, "renderImage should have been called")

            // Verify the src
            assertEquals(
                "https://example.com/custom.png",
                mockRenderer.lastImageSrcRendered,
                "src should be 'https://example.com/custom.png'"
            )

            // Verify the alt
            assertEquals("Custom image", mockRenderer.lastImageAltRendered, "alt should be 'Custom image'")

            // Verify the modifier
            assertNotNull(mockRenderer.lastImageModifierRendered, "Modifier should not be null")
            val styles = mockRenderer.lastImageModifierRendered!!.styles

            assertEquals("10px", styles["padding"], "padding should be '10px'")
        }
    }

    @Test
    fun testImageWithWidthAndHeight() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Image component with width and height
            Image(
                src = "https://example.com/image.jpg",
                alt = "Example image",
                width = "300px",
                height = "200px"
            )

            // Verify that renderImage was called
            assertTrue(mockRenderer.renderImageCalled, "renderImage should have been called")

            // Verify the src
            assertEquals(
                "https://example.com/image.jpg",
                mockRenderer.lastImageSrcRendered,
                "src should be 'https://example.com/image.jpg'"
            )

            // Verify the alt
            assertEquals("Example image", mockRenderer.lastImageAltRendered, "alt should be 'Example image'")

            // Note: In the current implementation, width and height are stored in attributes but not applied to the modifier
            // This test verifies the current behavior, but it might need to be updated if the implementation changes
        }
    }

    @Test
    fun testImageWithContentDescription() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Image component with contentDescription
            Image(
                src = "https://example.com/image.jpg",
                alt = "Example image",
                contentDescription = "A detailed description of the image content"
            )

            // Verify that renderImage was called
            assertTrue(mockRenderer.renderImageCalled, "renderImage should have been called")

            // Verify the src
            assertEquals(
                "https://example.com/image.jpg",
                mockRenderer.lastImageSrcRendered,
                "src should be 'https://example.com/image.jpg'"
            )

            // Verify the alt
            assertEquals("Example image", mockRenderer.lastImageAltRendered, "alt should be 'Example image'")

            // Note: In the current implementation, contentDescription is stored in attributes but not applied to the modifier
            // This test verifies the current behavior, but it might need to be updated if the implementation changes
        }
    }

    @Test
    fun testImageWithLoadingStrategy() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Image component with eager loading
            Image(
                src = "https://example.com/image.jpg",
                alt = "Example image",
                loading = ImageLoading.EAGER
            )

            // Verify that renderImage was called
            assertTrue(mockRenderer.renderImageCalled, "renderImage should have been called")

            // Verify the src
            assertEquals(
                "https://example.com/image.jpg",
                mockRenderer.lastImageSrcRendered,
                "src should be 'https://example.com/image.jpg'"
            )

            // Verify the alt
            assertEquals("Example image", mockRenderer.lastImageAltRendered, "alt should be 'Example image'")

            // Note: In the current implementation, loading is stored in attributes but not applied to the modifier
            // This test verifies the current behavior, but it might need to be updated if the implementation changes
        }
    }
}