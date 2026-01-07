package codes.yousef.summon.components.foundation

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.Composer
import codes.yousef.summon.runtime.CompositionLocal
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.theme.TextStyle
import kotlin.test.*

/**
 * Tests for the BasicText component
 */
class BasicTextTest {

    // Mock implementation of Composer for testing
    private class MockComposer : Composer {
        override val inserting: Boolean = false

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
    fun testBasicTextWithDefaultParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the BasicText component with default parameters
            BasicText(text = "Hello, World!")

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Hello, World!", mockRenderer.lastTextRendered, "Text should be 'Hello, World!'")

            // Verify the modifier
            assertNotNull(mockRenderer.lastModifierRendered, "Modifier should not be null")
            assertTrue(mockRenderer.lastModifierRendered!!.styles.isEmpty(), "Default modifier should have no styles")
        }
    }

    @Test
    fun testBasicTextWithCustomStyle() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Create a custom TextStyle
            val customStyle = TextStyle(
                fontFamily = "Arial, sans-serif",
                fontSize = "16px",
                fontWeight = "bold",
                fontStyle = "italic",
                color = "#FF0000",
                textDecoration = "underline",
                lineHeight = "1.5",
                letterSpacing = "0.5px"
            )

            // Call the BasicText component with custom style
            BasicText(
                text = "Styled Text",
                style = customStyle
            )

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Styled Text", mockRenderer.lastTextRendered, "Text should be 'Styled Text'")

            // Verify the styling
            assertNotNull(mockRenderer.lastModifierRendered, "Modifier should not be null")
            val styles = mockRenderer.lastModifierRendered!!.styles

            assertEquals("Arial, sans-serif", styles["font-family"], "font-family should be 'Arial, sans-serif'")
            assertEquals("16px", styles["font-size"], "font-size should be '16px'")
            assertEquals("bold", styles["font-weight"], "font-weight should be 'bold'")
            assertEquals("italic", styles["font-style"], "font-style should be 'italic'")
            assertEquals("#FF0000", styles["color"], "color should be '#FF0000'")
            assertEquals("underline", styles["text-decoration"], "text-decoration should be 'underline'")
            assertEquals("1.5", styles["line-height"], "line-height should be '1.5'")
            assertEquals("0.5px", styles["letter-spacing"], "letter-spacing should be '0.5px'")
        }
    }

    @Test
    fun testBasicTextWithCustomModifier() {
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

            // Call the BasicText component with custom modifier
            BasicText(
                text = "Text with Custom Modifier",
                modifier = customModifier
            )

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals(
                "Text with Custom Modifier",
                mockRenderer.lastTextRendered,
                "Text should be 'Text with Custom Modifier'"
            )

            // Verify the modifier
            assertNotNull(mockRenderer.lastModifierRendered, "Modifier should not be null")
            val styles = mockRenderer.lastModifierRendered!!.styles

            assertEquals("10px", styles["padding"], "padding should be '10px'")
            assertEquals("#EFEFEF", styles["background-color"], "background-color should be '#EFEFEF'")
            assertEquals("1px solid black", styles["border"], "border should be '1px solid black'")
            assertEquals("5px", styles["border-radius"], "border-radius should be '5px'")
        }
    }

    @Test
    fun testBasicTextWithTextLayoutCallback() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Variable to track if callback was called
        var callbackCalled = false
        var layoutResult: TextLayoutResult? = null

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the BasicText component with onTextLayout callback
            BasicText(
                text = "Text with Layout Callback",
                onTextLayout = { result ->
                    callbackCalled = true
                    layoutResult = result
                }
            )

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals(
                "Text with Layout Callback",
                mockRenderer.lastTextRendered,
                "Text should be 'Text with Layout Callback'"
            )

            // Verify the callback was called
            assertTrue(callbackCalled, "onTextLayout callback should have been called")

            // Verify the layout result
            assertNotNull(layoutResult, "TextLayoutResult should not be null")
            assertEquals(25 * 8f, layoutResult.width, "Width should be calculated based on text length")
            assertEquals(20f, layoutResult.height, "Height should be the default value")
            assertEquals(1, layoutResult.lineCount, "Line count should be 1")
            assertFalse(layoutResult.hasVisualOverflow, "hasVisualOverflow should be false")
        }
    }
}
