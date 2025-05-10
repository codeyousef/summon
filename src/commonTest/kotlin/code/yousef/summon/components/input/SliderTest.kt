package code.yousef.summon.components.input

import code.yousef.summon.runtime.*
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.input.Slider // Import the component
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.util.TestComposer
import code.yousef.summon.util.runTestComposable

// Import other types used in PlatformRenderer methods for the mock
import code.yousef.summon.runtime.SelectOption as RendererSelectOption
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertSame

class SliderTest {

    @Test
    fun testSliderInitializationAndInteraction() {
        val mockRenderer = MockPlatformRenderer()
        var sliderValue = 0.5f
        val valueRange = 0f..1f
        val steps = 10
        val testModifier = Modifier()
        val onValChange: (Float) -> Unit = { sliderValue = it }

        runTestComposable(mockRenderer) {
            Slider(
                value = sliderValue,
                onValueChange = onValChange,
                valueRange = valueRange,
                steps = steps,
                modifier = testModifier,
                enabled = true
            )
        }

        assertTrue(mockRenderer.renderSliderCalled, "renderSlider should be called")
        assertEquals(0.5f, mockRenderer.lastSliderValueRendered, "Initial value mismatch")
        assertEquals(valueRange, mockRenderer.lastSliderValueRangeRendered, "Value range mismatch")
        assertEquals(steps, mockRenderer.lastSliderStepsRendered, "Steps mismatch")
        assertEquals(true, mockRenderer.lastSliderEnabledRendered, "Enabled state mismatch")
        assertSame(testModifier, mockRenderer.lastSliderModifierRendered, "Modifier mismatch")

        // Simulate value change
        assertNotNull(mockRenderer.lastSliderOnValueChangeRendered, "onValueChange callback should not be null")
        mockRenderer.lastSliderOnValueChangeRendered?.invoke(0.8f)
        assertEquals(0.8f, sliderValue, "Slider value did not update after callback")
    }

    @Test
    fun testDisabledSlider() {
        val mockRenderer = MockPlatformRenderer()
        var sliderValue = 0.5f
        val valueRange = 0f..1f
        val steps = 10
        val onValChange: (Float) -> Unit = { sliderValue = it }

        runTestComposable(mockRenderer) {
            Slider(
                value = sliderValue,
                onValueChange = onValChange,
                valueRange = valueRange,
                steps = steps,
                enabled = false, // Explicitly disable
                modifier = Modifier()
            )
        }

        assertTrue(mockRenderer.renderSliderCalled, "renderSlider should be called")
        assertEquals(false, mockRenderer.lastSliderEnabledRendered, "Slider should be rendered as disabled")
        assertNotNull(mockRenderer.lastSliderOnValueChangeRendered, "onValueChange callback should be captured")

        // Attempt to invoke the callback (simulating user interaction with a disabled slider)
        val originalValue = sliderValue
        mockRenderer.lastSliderOnValueChangeRendered?.invoke(0.9f)

        // Assert that the external callback variable was NOT changed
        assertEquals(originalValue, sliderValue, "onValueChange callback should not be invoked when the slider is disabled")
    }

    @Test
    fun testStatefulSlider() {
        val mockRenderer = MockPlatformRenderer()
        val initialValue = 0.3f
        var externalValue = -1f // Different initial value to check callback
        val valueRange = 0f..2f
        val steps = 5
        val onValChangeExternal: (Float) -> Unit = { externalValue = it }

        runTestComposable(mockRenderer) {
            StatefulSlider(
                initialValue = initialValue,
                onValueChange = onValChangeExternal,
                valueRange = valueRange,
                steps = steps,
                enabled = true,
                modifier = Modifier()
            )
        }

        assertTrue(mockRenderer.renderSliderCalled, "renderSlider should be called for StatefulSlider")
        assertEquals(initialValue, mockRenderer.lastSliderValueRendered, "Initial value for StatefulSlider mismatch")
        assertEquals(valueRange, mockRenderer.lastSliderValueRangeRendered, "Value range for StatefulSlider mismatch")
        assertEquals(steps, mockRenderer.lastSliderStepsRendered, "Steps for StatefulSlider mismatch")
        assertEquals(true, mockRenderer.lastSliderEnabledRendered, "Enabled state for StatefulSlider mismatch")
        assertNotNull(mockRenderer.lastSliderOnValueChangeRendered, "onValueChange callback should not be null for StatefulSlider")

        // Simulate internal state update via renderer callback
        mockRenderer.lastSliderOnValueChangeRendered?.invoke(1.5f)

        // Check external callback was triggered
        assertEquals(1.5f, externalValue, "External onValueChange callback mismatch for StatefulSlider")
    }
}