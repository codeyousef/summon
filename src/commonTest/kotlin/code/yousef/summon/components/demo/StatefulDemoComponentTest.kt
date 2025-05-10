package code.yousef.summon.components.demo

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo as ExpectFileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for the StatefulDemoComponent, StatefulCounter, and ToggleDemo
 */
class StatefulDemoComponentTest {

    @Test
    fun testStatefulDemoComponent() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)
            mockRenderer.reset() // Reset before calling the component

            // Call the StatefulDemoComponent
            StatefulDemoComponent()

            // Verify that renderBox was called
            assertTrue(mockRenderer.renderBoxCalled, "renderBox should have been called")

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify that renderButton was called
            assertTrue(mockRenderer.renderButtonCalled, "renderButton should have been called")

            // Verify the initial text content
            assertNotNull(mockRenderer.lastTextRendered, "Text content should not be null")
            assertEquals("Initial Text", mockRenderer.lastTextRendered, "Initial text should be 'Initial Text'")

            // We can't verify the exact button label or onClick directly with simple flags
            // but we know renderButton was called.
        }
    }

    @Test
    fun testStatefulDemoComponentWithParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Create a custom modifier
        val customModifier = Modifier().background("blue")

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)
            mockRenderer.reset()

            // Call the StatefulDemoComponent with custom parameters
            StatefulDemoComponent(
                initialValue = "Custom Text",
                modifier = customModifier
            )

            // Verify that renderBox was called with the correct parameters
            assertTrue(mockRenderer.renderBoxCalled, "renderBox should have been called")
            // Verify that the custom modifier was passed to the Box
            val boxModifier = mockRenderer.boxModifierUsed
            assertNotNull(boxModifier, "Box modifier should not be null")
            val styles = boxModifier.styles
            assertTrue(styles.containsKey("background-color"), "background style should be present")
            assertEquals("blue", styles["background-color"], "background should be set to blue")

            // Check text content if StatefulDemoComponent uses initialValue for a Text element inside the Box
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called if text is rendered")
            assertEquals("Custom Text", mockRenderer.lastTextRendered, "Text should be 'Custom Text'")
        }
    }

    @Test
    fun testStatefulCounter() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)
            mockRenderer.reset()

            // Call the StatefulCounter
            StatefulCounter()

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify that renderButton was called
            assertTrue(mockRenderer.renderButtonCalled, "renderButton should have been called")

            // Verify the initial text content
            assertNotNull(mockRenderer.lastTextRendered, "Text content should not be null")
            assertEquals("Clicks: 0", mockRenderer.lastTextRendered, "Initial text should show 0 clicks")

            // onClick handler verification would require more advanced mocking/capturing
        }
    }

    @Test
    fun testToggleDemo() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)
            mockRenderer.reset()

            // Call the ToggleDemo
            ToggleDemo()

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify that renderButton was called
            assertTrue(mockRenderer.renderButtonCalled, "renderButton should have been called")

            // Verify the initial text content
            assertNotNull(mockRenderer.lastTextRendered, "Text content should not be null")
            assertEquals("OFF", mockRenderer.lastTextRendered, "Initial text should show OFF")

            // onClick handler verification would require more advanced mocking/capturing
        }
    }
}

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
