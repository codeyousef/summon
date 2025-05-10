package code.yousef.summon.components.display

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import code.yousef.summon.runtime.MockPlatformRenderer
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

/**
 * Tests for the Icon component
 */
class IconTest {

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
    }

    @Test
    fun testIconWithDefaultParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Icon component with default parameters
            Icon(name = "test-icon")

            // Verify that renderIcon was called
            assertTrue(mockRenderer.renderIconCalled, "renderIcon should have been called")

            // Verify the icon name
            assertEquals("test-icon", mockRenderer.lastIconNameRendered, "Icon name should be 'test-icon'")

            // Verify the icon type
            assertEquals(IconType.SVG, mockRenderer.lastIconTypeRendered, "Icon type should be SVG by default")

            // Verify the modifier
            assertNotNull(mockRenderer.lastIconModifierRendered, "Modifier should not be null")
            assertEquals(Modifier().styles, mockRenderer.lastIconModifierRendered!!.styles, "Modifier should be the default")

            // Verify that onClick is null
            assertEquals(null, mockRenderer.lastIconOnClickRendered, "onClick should be null")

            // Verify that svgContent is null
            assertEquals(null, mockRenderer.lastIconSvgContentRendered, "svgContent should be null")
        }
    }

    @Test
    fun testIconWithCustomModifier() {
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

            // Call the Icon component with custom modifier
            Icon(
                name = "custom-icon",
                modifier = customModifier
            )

            // Verify that renderIcon was called
            assertTrue(mockRenderer.renderIconCalled, "renderIcon should have been called")

            // Verify the icon name
            assertEquals("custom-icon", mockRenderer.lastIconNameRendered, "Icon name should be 'custom-icon'")

            // Verify the modifier
            assertNotNull(mockRenderer.lastIconModifierRendered, "Modifier should not be null")
            val styles = mockRenderer.lastIconModifierRendered!!.styles

            assertEquals("10px", styles["padding"], "padding should be '10px'")
            assertEquals("#EFEFEF", styles["background-color"], "background-color should be '#EFEFEF'")
            assertEquals("1px solid black", styles["border"], "border should be '1px solid black'")
            assertEquals("5px", styles["border-radius"], "border-radius should be '5px'")
        }
    }

    @Test
    fun testIconWithSizeAndColor() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Icon component with size and color
            Icon(
                name = "sized-icon",
                size = "32px",
                color = "#FF0000"
            )

            // Verify that renderIcon was called
            assertTrue(mockRenderer.renderIconCalled, "renderIcon should have been called")

            // Verify the icon name
            assertEquals("sized-icon", mockRenderer.lastIconNameRendered, "Icon name should be 'sized-icon'")

            // Verify the modifier
            assertNotNull(mockRenderer.lastIconModifierRendered, "Modifier should not be null")
            val styles = mockRenderer.lastIconModifierRendered!!.styles

            assertEquals("32px", styles["width"], "width should be '32px'")
            assertEquals("32px", styles["height"], "height should be '32px'")
            assertEquals("#FF0000", styles["color"], "color should be '#FF0000'")
        }
    }

    @Test
    fun testIconWithOnClick() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Variable to track if onClick was called
            var onClickCalled = false

            // Call the Icon component with onClick
            Icon(
                name = "clickable-icon",
                onClick = { onClickCalled = true }
            )

            // Verify that renderIcon was called
            assertTrue(mockRenderer.renderIconCalled, "renderIcon should have been called")

            // Verify the icon name
            assertEquals("clickable-icon", mockRenderer.lastIconNameRendered, "Icon name should be 'clickable-icon'")

            // Verify the onClick handler
            assertNotNull(mockRenderer.lastIconOnClickRendered, "onClick should not be null")

            // Verify the modifier
            assertNotNull(mockRenderer.lastIconModifierRendered, "Modifier should not be null")
            val styles = mockRenderer.lastIconModifierRendered!!.styles

            assertEquals("pointer", styles["cursor"], "cursor should be 'pointer'")
            assertEquals("button", styles["__attr:role"], "role should be 'button'")

            // Call the onClick handler and verify it works
            mockRenderer.lastIconOnClickRendered?.invoke()
            assertTrue(onClickCalled, "onClick should have been called")
        }
    }

    @Test
    fun testIconWithAriaLabel() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Icon component with ariaLabel
            Icon(
                name = "accessible-icon",
                ariaLabel = "Accessible Icon"
            )

            // Verify that renderIcon was called
            assertTrue(mockRenderer.renderIconCalled, "renderIcon should have been called")

            // Verify the icon name
            assertEquals("accessible-icon", mockRenderer.lastIconNameRendered, "Icon name should be 'accessible-icon'")

            // Verify the modifier
            assertNotNull(mockRenderer.lastIconModifierRendered, "Modifier should not be null")
            val styles = mockRenderer.lastIconModifierRendered!!.styles

            assertEquals("Accessible Icon", styles["__attr:aria-label"], "aria-label should be 'Accessible Icon'")
            assertEquals("img", styles["__attr:role"], "role should be 'img'")
        }
    }

    @Test
    fun testMaterialIcon() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the MaterialIcon component
            MaterialIcon(
                name = "info",
                size = "24px",
                color = "#0000FF"
            )

            // Verify that renderIcon was called
            assertTrue(mockRenderer.renderIconCalled, "renderIcon should have been called")

            // Verify the icon name
            assertEquals("info", mockRenderer.lastIconNameRendered, "Icon name should be 'info'")

            // Verify the icon type
            assertEquals(IconType.FONT, mockRenderer.lastIconTypeRendered, "Icon type should be FONT")

            // Verify the modifier
            assertNotNull(mockRenderer.lastIconModifierRendered, "Modifier should not be null")
            val styles = mockRenderer.lastIconModifierRendered!!.styles

            assertEquals("24px", styles["width"], "width should be '24px'")
            assertEquals("24px", styles["height"], "height should be '24px'")
            assertEquals("#0000FF", styles["color"], "color should be '#0000FF'")
            assertEquals("Material Icons", styles["font-family"], "font-family should be 'Material Icons'")
        }
    }

    @Test
    fun testSvgIcon() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Sample SVG content
            val svgContent = "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'><path d='M12 2L2 22h20L12 2z'/></svg>"

            // Call the SvgIcon component
            SvgIcon(
                svgContent = svgContent,
                ariaLabel = "Warning Icon"
            )

            // Verify that renderIcon was called
            assertTrue(mockRenderer.renderIconCalled, "renderIcon should have been called")

            // Verify the icon name (should use ariaLabel as fallback)
            assertEquals("Warning Icon", mockRenderer.lastIconNameRendered, "Icon name should be 'Warning Icon'")

            // Verify the icon type
            assertEquals(IconType.SVG, mockRenderer.lastIconTypeRendered, "Icon type should be SVG")

            // Verify the SVG content
            assertEquals(svgContent, mockRenderer.lastIconSvgContentRendered, "SVG content should match")

            // Verify the modifier
            assertNotNull(mockRenderer.lastIconModifierRendered, "Modifier should not be null")
            val styles = mockRenderer.lastIconModifierRendered!!.styles

            assertEquals("Warning Icon", styles["__attr:aria-label"], "aria-label should be 'Warning Icon'")
            assertEquals("img", styles["__attr:role"], "role should be 'img'")
        }
    }
}
