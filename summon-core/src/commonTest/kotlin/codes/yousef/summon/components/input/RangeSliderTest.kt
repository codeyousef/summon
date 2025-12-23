package codes.yousef.summon.components.input

// Import other types used in PlatformRenderer methods for the mock

// Import MockPlatformRenderer
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.runComposableTest
import kotlin.test.*

class RangeSliderTest {

    @Test
    fun testBasicRangeSliderRendering() {
        val mockRenderer = MockPlatformRenderer() // Changed to shared MockPlatformRenderer
        var sliderRangeValue = 0.2f..0.7f
        val valueRange = 0f..1f
        val steps = 5
        val testModifier = Modifier()
        val onValChange: (ClosedFloatingPointRange<Float>) -> Unit = { sliderRangeValue = it }

        runComposableTest(mockRenderer) {
            RangeSlider(
                value = sliderRangeValue,
                onValueChange = onValChange,
                valueRange = valueRange,
                steps = steps,
                modifier = testModifier,
                enabled = true
            )
        }

        assertTrue(mockRenderer.renderRangeSliderCalled, "renderRangeSlider should be called")
        assertEquals(0.2f..0.7f, mockRenderer.lastRangeSliderValueRendered, "Initial value mismatch")
        assertEquals(valueRange, mockRenderer.lastRangeSliderValueRangeRendered, "Value range mismatch")
        assertEquals(steps, mockRenderer.lastRangeSliderStepsRendered, "Steps mismatch")
        assertEquals(true, mockRenderer.lastRangeSliderEnabledRendered, "Enabled state mismatch")
        // Assuming RangeSlider doesn't add default styles
        assertSame(testModifier, mockRenderer.lastRangeSliderModifierRendered, "Modifier mismatch")

        // Simulate value change
        assertNotNull(mockRenderer.lastRangeSliderOnValueChangeRendered, "onValueChange callback should not be null")
        val newValue = 0.3f..0.8f
        mockRenderer.lastRangeSliderOnValueChangeRendered?.invoke(newValue)
        assertEquals(newValue, sliderRangeValue, "Slider range value did not update after callback")
    }

    @Test
    fun testDisabledRangeSlider() {
        val mockRenderer = MockPlatformRenderer() // Changed to shared MockPlatformRenderer
        var sliderRangeValue = 0.2f..0.7f
        val valueRange = 0f..1f
        val steps = 5
        val onValChange: (ClosedFloatingPointRange<Float>) -> Unit = { sliderRangeValue = it }

        runComposableTest(mockRenderer) {
            RangeSlider(
                value = sliderRangeValue,
                onValueChange = onValChange,
                valueRange = valueRange,
                steps = steps,
                enabled = false, // Explicitly disable
                modifier = Modifier()
            )
        }

        assertTrue(mockRenderer.renderRangeSliderCalled, "renderRangeSlider should be called")
        assertEquals(false, mockRenderer.lastRangeSliderEnabledRendered, "Slider should be rendered as disabled")
        assertNotNull(mockRenderer.lastRangeSliderOnValueChangeRendered, "onValueChange callback should be captured")

        // Attempt to invoke the callback
        val originalValue = sliderRangeValue
        val newValue = 0.3f..0.8f
        mockRenderer.lastRangeSliderOnValueChangeRendered?.invoke(newValue)

        // Assert that the external callback variable was NOT changed
        assertEquals(
            originalValue,
            sliderRangeValue,
            "onValueChange callback should not be invoked when the slider is disabled"
        )
    }

    @Test
    fun testStatefulRangeSlider() {
        val mockRenderer = MockPlatformRenderer() // Changed to shared MockPlatformRenderer
        val initialValue = 0.1f..0.6f
        var externalValue: ClosedFloatingPointRange<Float>? = null // Initialize to null
        val valueRange = 0f..1f
        val steps = 10
        val onValChangeExternal: (ClosedFloatingPointRange<Float>) -> Unit = { externalValue = it }

        runComposableTest(mockRenderer) {
            StatefulRangeSlider(
                initialValue = initialValue,
                onValueChange = onValChangeExternal,
                valueRange = valueRange,
                steps = steps,
                isEnabled = true, // Use isEnabled parameter name from StatefulRangeSlider
                modifier = Modifier()
            )
        }

        assertTrue(mockRenderer.renderRangeSliderCalled, "renderRangeSlider should be called for StatefulRangeSlider")
        assertEquals(
            initialValue,
            mockRenderer.lastRangeSliderValueRendered,
            "Initial value for StatefulRangeSlider mismatch"
        )
        assertEquals(
            valueRange,
            mockRenderer.lastRangeSliderValueRangeRendered,
            "Value range for StatefulRangeSlider mismatch"
        )
        assertEquals(steps, mockRenderer.lastRangeSliderStepsRendered, "Steps for StatefulRangeSlider mismatch")
        assertEquals(
            true,
            mockRenderer.lastRangeSliderEnabledRendered,
            "Enabled state for StatefulRangeSlider mismatch"
        )
        assertNotNull(
            mockRenderer.lastRangeSliderOnValueChangeRendered,
            "onValueChange callback should not be null for StatefulRangeSlider"
        )

        // Simulate internal state update via renderer callback
        val newValue = 0.4f..0.9f
        mockRenderer.lastRangeSliderOnValueChangeRendered?.invoke(newValue)

        // Check external callback was triggered
        assertEquals(newValue, externalValue, "External onValueChange callback mismatch for StatefulRangeSlider")
    }
} 