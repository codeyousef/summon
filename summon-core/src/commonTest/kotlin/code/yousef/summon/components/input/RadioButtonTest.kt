package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.FormContent
import code.yousef.summon.runtime.MockPlatformRenderer
import code.yousef.summon.util.runTestComposable
import code.yousef.summon.runtime.PlatformRenderer
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import code.yousef.summon.runtime.SelectOption as RendererSelectOption

class RadioButtonTest {

    @Test
    fun testBasicRadioButtonRendering() {
        val mockRenderer = MockPlatformRenderer()
        var clicked = false
        val testModifier = Modifier()
        val onClickLambda = { clicked = true }
        val expectedModifier = testModifier.cursor("pointer")

        runTestComposable(mockRenderer) {
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
        assertEquals(expectedModifier.styles, mockRenderer.lastRadioButtonModifierRendered?.styles, "Modifier styles mismatch")

        assertNotNull(mockRenderer.lastRadioButtonOnClickRendered, "onClick callback should not be null")
        mockRenderer.lastRadioButtonOnClickRendered?.invoke()
        assertTrue(clicked, "onClick callback was not invoked")
    }

    @Test
    fun testInitialSelectedState() {
        val mockRenderer = MockPlatformRenderer()
        val onClickLambda = { /* No-op */ }

        runTestComposable(mockRenderer) {
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

        runTestComposable(mockRenderer) {
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
