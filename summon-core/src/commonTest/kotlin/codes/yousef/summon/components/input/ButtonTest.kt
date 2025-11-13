package codes.yousef.summon.components.input

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.CompositionLocal
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.MockComposer
import kotlin.test.*

// Extension functions for testing
private fun Modifier.hasStyle(property: String, value: String): Boolean =
    styles[property] == value

private fun Modifier.hasBackground(color: String): Boolean =
    hasStyle("background", color) || hasStyle("background-color", color) ||
            hasStyle("background", "$color !important") || hasStyle("background-color", "$color !important")

private fun Modifier.hasColor(color: String): Boolean =
    hasStyle("color", color) || hasStyle("color", "$color !important")

/**
 * Tests for the Button component
 */
class ButtonTest {


    // Mock FlowContent for invoking content lambdas
    // Note: This is a simplified mock that doesn't implement all FlowContent methods
    // For more complex tests, consider using a full mock implementation

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
            // The content lambda should contain icon and text rendering logic

            // Reset the mock renderer to test the next button independently
            mockRenderer.reset()

            // Test icon at END position
            Button(
                onClick = { },
                label = "Button with End Icon",
                iconName = "end-icon-test",
                iconPosition = IconPosition.END
            )
            assertTrue(mockRenderer.renderButtonCalled, "renderButton should have been called for end icon button")
            assertNotNull(mockRenderer.lastButtonContentRendered, "Content should not be null for end icon button")
            // The content lambda should contain text and icon rendering logic (in that order for END position)
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

    @Test
    fun buttonSupportsDataAttributesAndNullableOnClick() {
        val mockRenderer = MockPlatformRenderer()

        CompositionLocal.provideComposer(MockComposer()) {
            LocalPlatformRenderer.provides(mockRenderer)

            Button(
                label = "Copy",
                onClick = null,
                dataAttributes = mapOf(
                    "copy" to "token",
                    "href" to "#copy"
                )
            )

            assertTrue(mockRenderer.renderButtonCalled, "renderButton should have been called")
            val modifier = mockRenderer.lastButtonModifierRendered
            assertNotNull(modifier, "Modifier should not be null")
            assertEquals("token", modifier!!.attributes["data-copy"])
            assertEquals("#copy", modifier.attributes["data-href"])
            assertNotNull(
                mockRenderer.lastButtonOnClickRendered,
                "Button should supply a no-op onClick when none provided"
            )
            // Invoking should be safe even though it is a no-op
            mockRenderer.lastButtonOnClickRendered?.invoke()
        }
    }
}
