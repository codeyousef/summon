package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertEquals

// Extension functions for testing
private fun Modifier.hasStyle(property: String, value: String): Boolean = 
    styles[property] == value

private fun Modifier.hasBackground(color: String): Boolean = 
    hasStyle("background", color) || hasStyle("background-color", color)

private fun Modifier.hasColor(color: String): Boolean = 
    hasStyle("color", color)

/**
 * Tests for the Button component
 */
class ButtonTest {

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

    // Mock FlowContent for invoking content lambdas
    private object MockFlowContent : FlowContent

    @Test
    fun testDefaultButton() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()
        var buttonClicked = false

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Button component with default parameters
            Button(
                onClick = { buttonClicked = true },
                label = "Test Button"
            )

            // Verify that renderButton was called
            assertTrue(mockRenderer.renderButtonCalled, "renderButton should have been called")

            // Verify the onClick handler was passed
            assertNotNull(mockRenderer.lastButtonOnClickRendered, "onClick handler should not be null")

            // Verify the modifier was passed
            assertNotNull(mockRenderer.lastButtonModifierRendered, "Modifier should not be null")

            // Verify the content lambda was passed
            assertNotNull(mockRenderer.lastButtonContentRendered, "Content lambda should not be null")

            // Simulate a click
            mockRenderer.lastButtonOnClickRendered?.invoke()
            assertTrue(buttonClicked, "onClick handler should have been called")

            // Verify the modifier has primary button styling
            assertNotNull(mockRenderer.lastButtonModifierRendered, "Modifier should not be null")
            val modifier = mockRenderer.lastButtonModifierRendered!!
            assertTrue(modifier.hasBackground("#0d6efd"), "Primary button should have blue background")
            assertTrue(modifier.hasColor("#ffffff"), "Primary button should have white text")
        }
    }

    @Test
    fun testButtonVariants() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Test PRIMARY variant
            Button(
                onClick = { },
                label = "Primary Button",
                variant = ButtonVariant.PRIMARY
            )
            assertTrue(mockRenderer.renderButtonCalled, "renderButton should have been called for primary button")
            assertNotNull(mockRenderer.lastButtonModifierRendered, "Modifier should not be null for primary button")
            val modifier = mockRenderer.lastButtonModifierRendered!!
            assertTrue(modifier.hasBackground("#0d6efd"), "Primary button should have blue background")
            assertTrue(modifier.hasColor("#ffffff"), "Primary button should have white text")

            // Test SECONDARY variant
            Button(
                onClick = { },
                label = "Secondary Button",
                variant = ButtonVariant.SECONDARY
            )
            assertTrue(mockRenderer.renderButtonCalled, "renderButton should have been called for secondary button")
            assertNotNull(mockRenderer.lastButtonModifierRendered, "Modifier should not be null for secondary button")
            val modifier2 = mockRenderer.lastButtonModifierRendered!!
            assertTrue(modifier2.hasBackground("#6c757d"), "Secondary button should have gray background")
            assertTrue(modifier2.hasColor("#ffffff"), "Secondary button should have white text")

            // Test DANGER variant
            Button(
                onClick = { },
                label = "Danger Button",
                variant = ButtonVariant.DANGER
            )
            assertTrue(mockRenderer.renderButtonCalled, "renderButton should have been called for danger button")
            assertNotNull(mockRenderer.lastButtonModifierRendered, "Modifier should not be null for danger button")
            val modifier3 = mockRenderer.lastButtonModifierRendered!!
            assertTrue(modifier3.hasBackground("#dc3545"), "Danger button should have red background")
            assertTrue(modifier3.hasColor("#ffffff"), "Danger button should have white text")
        }
    }

    @Test
    fun testButtonWithIconAndLabel() {
        val mockRenderer = MockPlatformRenderer()
        CompositionLocal.provideComposer(MockComposer()) {
            LocalPlatformRenderer.provides(mockRenderer)

            // Test icon at START position
            Button(
                onClick = { },
                label = "Button with Start Icon",
                iconName = "start-icon-test",
                iconPosition = IconPosition.START
            )
            assertTrue(mockRenderer.renderButtonCalled, "renderButton should have been called for start icon button")
            assertNotNull(mockRenderer.lastButtonContentRendered, "Content should not be null for start icon button")
            mockRenderer.lastButtonContentRendered?.invoke(MockFlowContent) // Invoke content
            assertEquals("start-icon-test", mockRenderer.lastIconNameRendered, "Start icon name mismatch")
            assertEquals("Button with Start Icon", mockRenderer.lastTextRendered, "Start icon button label mismatch")

            // Test icon at END position
            // Note: MockPlatformRenderer stores the *last* rendered values. Ensure this is desired or use separate mocks/resets.
            Button(
                onClick = { },
                label = "Button with End Icon",
                iconName = "end-icon-test",
                iconPosition = IconPosition.END
            )
            assertTrue(mockRenderer.renderButtonCalled, "renderButton should have been called for end icon button") // Will be true due to previous calls
            assertNotNull(mockRenderer.lastButtonContentRendered, "Content should not be null for end icon button")
            mockRenderer.lastButtonContentRendered?.invoke(MockFlowContent) // Invoke content
            assertEquals("end-icon-test", mockRenderer.lastIconNameRendered, "End icon name mismatch")
            assertEquals("Button with End Icon", mockRenderer.lastTextRendered, "End icon button label mismatch")
        }
    }

    @Test
    fun testDisabledButton() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()
        var buttonClicked = false

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            LocalPlatformRenderer.provides(mockRenderer)

            // Call the Button component with disabled=true
            Button(
                onClick = { buttonClicked = true },
                label = "Disabled Button",
                disabled = true
            )

            // Verify that renderButton was called
            assertTrue(mockRenderer.renderButtonCalled, "renderButton should have been called")

            // Verify the modifier has disabled styling
            assertNotNull(mockRenderer.lastButtonModifierRendered, "Modifier should not be null")
            val modifier = mockRenderer.lastButtonModifierRendered!!
            assertTrue(modifier.hasStyle("cursor", "not-allowed"), "Disabled button should have not-allowed cursor")
            assertTrue(modifier.hasStyle("pointer-events", "none"), "Disabled button should have pointer-events none")

            // Verify the onClick handler is effectively a no-op when disabled
            // The actual onClick passed to renderButton might still be the original one, 
            // but the Button component should internally prevent its execution or pass a no-op.
            // For this test, we assume the component correctly makes the button non-interactive.
            // If the button's internal logic changes onClick when disabled, that could be asserted here.
            // We primarily test that the passed onClick doesn't run if invoked.
            assertNotNull(mockRenderer.lastButtonOnClickRendered, "onClick handler should not be null")
            // Simulate an attempt to click - it shouldn't change buttonClicked if correctly disabled by the component's internal logic or styling effects.
            // This part of the test relies on the Button's implementation of 'disabled'.
            // If Button passes a no-op lambda, then invoking lastButtonOnClickRendered would do nothing.
            // If it passes the original lambda but relies on CSS (pointer-events:none), then this invocation would still run.
            // For a robust test of 'disabled', it's more about asserting the visual/interactive state (CSS, attributes) 
            // and that the *original* onClick logic doesn't execute upon user interaction.
            // The previous check that `buttonClicked` is false after invoking `mockRenderer.lastButtonOnClickRendered?.invoke()`
            // is correct if the Button component wraps or replaces the onClick when disabled.
            mockRenderer.lastButtonOnClickRendered?.invoke()
            assertFalse(buttonClicked, "onClick handler should not execute its original logic when button is disabled")
        }
    }
}
