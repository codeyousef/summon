package codes.yousef.summon.components.input

import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.runComposableTest
import kotlin.test.*

class RadioButtonTest {

    @Test
    fun testBasicRadioButtonRendering() {
        val mockRenderer = MockPlatformRenderer()
        var clicked = false
        val testModifier = Modifier()
        val onClickLambda = { clicked = true }
        val expectedModifier = testModifier.cursor("pointer")

        runComposableTest(mockRenderer) {
            RadioButton(
                selected = false,
                onClick = onClickLambda,
                modifier = testModifier,
                enabled = true
            )
        }

        assertTrue(mockRenderer.renderRadioButtonCalled, "renderRadioButton should be called")
        assertEquals(false, mockRenderer.lastRadioButtonSelectedRendered, "Initial selected state mismatch")
        assertEquals(true, mockRenderer.lastRadioButtonEnabledRendered, "Enabled state mismatch")
        assertEquals(
            expectedModifier.styles,
            mockRenderer.lastRadioButtonModifierRendered?.styles,
            "Modifier styles mismatch"
        )

        assertNotNull(mockRenderer.lastRadioButtonOnClickRendered, "onClick callback should not be null")
        mockRenderer.lastRadioButtonOnClickRendered?.invoke()
        assertTrue(clicked, "onClick callback was not invoked")
    }

    @Test
    fun testInitialSelectedState() {
        val mockRenderer = MockPlatformRenderer()
        val onClickLambda = { /* No-op */ }

        runComposableTest(mockRenderer) {
            RadioButton(
                selected = true, // Initial state is selected
                onClick = onClickLambda,
                enabled = true,
                modifier = Modifier()
            )
        }

        assertTrue(mockRenderer.renderRadioButtonCalled, "renderRadioButton should be called")
        assertEquals(true, mockRenderer.lastRadioButtonSelectedRendered, "Initial selected state should be true")
        assertEquals(true, mockRenderer.lastRadioButtonEnabledRendered, "Enabled state mismatch")
    }

    @Test
    fun testDisabledRadioButton() {
        val mockRenderer = MockPlatformRenderer()
        var clicked = false
        val onClickLambda = { clicked = true }

        runComposableTest(mockRenderer) {
            RadioButton(
                selected = false,
                onClick = onClickLambda,
                enabled = false, // Explicitly disable the button
                modifier = Modifier()
            )
        }

        assertTrue(mockRenderer.renderRadioButtonCalled, "renderRadioButton should be called")
        assertEquals(false, mockRenderer.lastRadioButtonSelectedRendered, "Initial selected state mismatch")
        assertEquals(false, mockRenderer.lastRadioButtonEnabledRendered, "Button should be rendered as disabled")
        assertNotNull(mockRenderer.lastRadioButtonOnClickRendered, "onClick callback should be captured")

        // Attempt to invoke the callback (simulating user interaction with a disabled button)
        mockRenderer.lastRadioButtonOnClickRendered?.invoke()

        // Assert that the external callback variable was NOT changed
        assertFalse(clicked, "onClick callback should not be invoked when the button is disabled")
    }
}
