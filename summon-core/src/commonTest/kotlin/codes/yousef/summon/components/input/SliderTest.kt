package codes.yousef.summon.components.input

// Import other types used in PlatformRenderer methods for the mock

import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.runComposableTest
import kotlin.test.*

class SliderTest {

    @Test
    fun testSliderInitializationAndInteraction() {
        val mockRenderer = MockPlatformRenderer()
        var sliderValue = 0.5f
        val valueRange = 0f..1f
        val steps = 10
        val testModifier = Modifier()
        val onValChange: (Float) -> Unit = { sliderValue = it }

        runComposableTest(mockRenderer) {
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

        runComposableTest(mockRenderer) {
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
        assertEquals(
            originalValue,
            sliderValue,
            "onValueChange callback should not be invoked when the slider is disabled"
        )
    }

    @Test
    fun testStatefulSlider() {
        val mockRenderer = MockPlatformRenderer()
        val initialValue = 0.3f
        var externalValue = -1f // Different initial value to check callback
        val valueRange = 0f..2f
        val steps = 5
        val onValChangeExternal: (Float) -> Unit = { externalValue = it }

        runComposableTest(mockRenderer) {
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
        assertNotNull(
            mockRenderer.lastSliderOnValueChangeRendered,
            "onValueChange callback should not be null for StatefulSlider"
        )

        // Simulate internal state update via renderer callback
        mockRenderer.lastSliderOnValueChangeRendered?.invoke(1.5f)

        // Check external callback was triggered
        assertEquals(1.5f, externalValue, "External onValueChange callback mismatch for StatefulSlider")
    }
}