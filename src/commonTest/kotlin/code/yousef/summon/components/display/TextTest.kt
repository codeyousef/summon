package code.yousef.summon.components.display

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import code.yousef.summon.runtime.MockPlatformRenderer
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

// Extension functions for testing
private fun Modifier.hasStyle(property: String, value: String): Boolean =
    styles[property] == value

private fun Modifier.hasAttribute(name: String, value: String): Boolean =
    styles["__attr:$name"] == value

/**
 * Tests for the Text component
 */
class TextTest {

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
    }

    @Test
    fun testTextWithDefaultParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Text component with default parameters
            Text(text = "Hello, World!")

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
    fun testTextWithStyling() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Text component with styling parameters
            Text(
                text = "Styled Text",
                overflow = "ellipsis",
                lineHeight = "1.5",
                textAlign = "center",
                fontFamily = "Arial, sans-serif",
                textDecoration = "underline",
                textTransform = "uppercase",
                letterSpacing = "0.5px",
                whiteSpace = "nowrap",
                wordBreak = "break-word",
                wordSpacing = "2px",
                textShadow = "1px 1px 2px black"
            )

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Styled Text", mockRenderer.lastTextRendered, "Text should be 'Styled Text'")

            // Verify the styling
            assertNotNull(mockRenderer.lastModifierRendered, "Modifier should not be null")
            assertTrue(
                mockRenderer.lastModifierRendered!!.hasStyle("font-family", "Arial, sans-serif"),
                "Modifier should have font-family style"
            )
            assertTrue(
                mockRenderer.lastModifierRendered!!.hasStyle("font-size", "16px"),
                "Modifier should have font-size style"
            )
            assertTrue(
                mockRenderer.lastModifierRendered!!.hasStyle("color", "#FF0000"),
                "Modifier should have color style"
            )
        }
    }

    @Test
    fun testTextWithMaxLines() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Text component with maxLines parameter
            Text(
                text = "Text with max lines",
                maxLines = 2
            )

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Text with max lines", mockRenderer.lastTextRendered, "Text should be 'Text with max lines'")

            // Verify the maxLines styling
            assertNotNull(mockRenderer.lastModifierRendered, "Modifier should not be null")
            assertTrue(
                mockRenderer.lastModifierRendered!!.hasStyle("display", "-webkit-box"),
                "Modifier should have display style"
            )
            assertTrue(
                mockRenderer.lastModifierRendered!!.hasStyle("-webkit-line-clamp", "2"),
                "Modifier should have -webkit-line-clamp style"
            )
            assertTrue(
                mockRenderer.lastModifierRendered!!.hasStyle("-webkit-box-orient", "vertical"),
                "Modifier should have -webkit-box-orient style"
            )
            assertTrue(
                mockRenderer.lastModifierRendered!!.hasStyle("overflow", "hidden"),
                "Modifier should have overflow style"
            )
        }
    }

    @Test
    fun testTextWithAccessibility() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Text component with accessibility parameters
            Text(
                text = "Accessible Text",
                role = "heading",
                ariaLabel = "Heading Label",
                ariaDescribedBy = "description-id",
                semantic = "heading"
            )

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Accessible Text", mockRenderer.lastTextRendered, "Text should be 'Accessible Text'")

            // Verify the accessibility attributes
            assertNotNull(mockRenderer.lastModifierRendered, "Modifier should not be null")
            assertTrue(
                mockRenderer.lastModifierRendered!!.hasAttribute("role", "heading"),
                "Modifier should have role attribute"
            )
            assertTrue(
                mockRenderer.lastModifierRendered!!.hasAttribute("aria-label", "Heading Label"),
                "Modifier should have aria-label attribute"
            )
            assertTrue(
                mockRenderer.lastModifierRendered!!.hasAttribute("aria-describedby", "description-id"),
                "Modifier should have aria-describedby attribute"
            )
            assertTrue(
                mockRenderer.lastModifierRendered!!.hasAttribute("data-semantic", "heading"),
                "Modifier should have data-semantic attribute"
            )
        }
    }

    @Test
    fun testLabel() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Label component
            Label(
                text = "Username",
                forElement = "username-input"
            )

            // Verify that renderLabel was called
            assertTrue(mockRenderer.renderLabelCalled, "renderLabel should have been called")

            // Verify the text
            assertEquals("Username", mockRenderer.lastLabelTextRendered, "Text should be 'Username'")

            // Verify the forElement parameter
            assertEquals("username-input", mockRenderer.lastLabelForElementRendered, "forElement should be 'username-input'")
        }
    }

    @Test
    fun testTextComponent() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Create a TextComponent and render it
            val textComponent = TextComponent(
                text = "Component Text",
                textAlign = "right",
                fontFamily = "Roboto"
            )
            textComponent.render()

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Component Text", mockRenderer.lastTextRendered, "Text should be 'Component Text'")

            // Verify the styling
            assertNotNull(mockRenderer.lastModifierRendered, "Modifier should not be null")
            val styles = mockRenderer.lastModifierRendered!!.styles

            assertEquals("right", styles["text-align"], "text-align should be 'right'")
            assertEquals("Roboto", styles["font-family"], "font-family should be 'Roboto'")
        }
    }
}