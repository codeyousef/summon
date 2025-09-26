package code.yousef.summon.components.input

// Import other types used in PlatformRenderer methods for the mock

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.pointerEvents
import code.yousef.summon.runtime.MockPlatformRenderer
import code.yousef.summon.util.runComposableTest
import kotlinx.datetime.LocalDate
import kotlin.test.*

class DatePickerTest {

    @Test
    fun testBasicDatePickerRendering() {
        val mockRenderer = MockPlatformRenderer()
        var selectedDate: LocalDate? = LocalDate(2024, 1, 15)
        val minDate = LocalDate(2024, 1, 1)
        val maxDate = LocalDate(2024, 12, 31)
        val testModifier = Modifier()
        val onValChange: (LocalDate?) -> Unit = { selectedDate = it }

        runComposableTest(mockRenderer) {
            DatePicker(
                value = selectedDate,
                onValueChange = onValChange,
                minDate = minDate,
                maxDate = maxDate,
                modifier = testModifier,
                enabled = true
            )
        }

        assertTrue(mockRenderer.renderDatePickerCalled, "renderDatePicker should be called")
        assertEquals(selectedDate, mockRenderer.lastDatePickerValueRendered, "Initial value mismatch")
        assertEquals(minDate, mockRenderer.lastDatePickerMinDateRendered, "Min date mismatch")
        assertEquals(maxDate, mockRenderer.lastDatePickerMaxDateRendered, "Max date mismatch")
        assertEquals(true, mockRenderer.lastDatePickerEnabledRendered, "Enabled state mismatch")
        
        // Check modifier - DatePicker applies opacity and cursor styles
        val expectedModifier = testModifier
            .opacity(1f) // enabled = true
            .cursor("pointer") // enabled = true
            // .pointerEvents("none") // Not applied when enabled
            .attribute("data-date-format", "yyyy-MM-dd") // Default format
            .attribute("data-min-date", minDate.toString())
            .attribute("data-max-date", maxDate.toString())
            // Add other attributes like label, initialDisplayMonth if testing them
            
        assertEquals(expectedModifier.styles, mockRenderer.lastDatePickerModifierRendered?.styles, "Modifier styles mismatch")
        assertEquals(expectedModifier.attributes, mockRenderer.lastDatePickerModifierRendered?.attributes, "Modifier attributes mismatch")

        // Simulate value change
        assertNotNull(mockRenderer.lastDatePickerOnValueChangeRendered, "onValueChange callback should not be null")
        val newDate = LocalDate(2024, 5, 20)
        mockRenderer.lastDatePickerOnValueChangeRendered?.invoke(newDate)
        assertEquals(newDate, selectedDate, "Date value did not update after callback")

        // Simulate clearing the date
        mockRenderer.lastDatePickerOnValueChangeRendered?.invoke(null)
        assertNull(selectedDate, "Date value did not clear after callback with null")
    }

    @Test
    fun testDisabledDatePicker() {
        val mockRenderer = MockPlatformRenderer()
        var selectedDate: LocalDate? = LocalDate(2024, 1, 15)
        val onValChange: (LocalDate?) -> Unit = { selectedDate = it }

        runComposableTest(mockRenderer) {
            DatePicker(
                value = selectedDate,
                onValueChange = onValChange,
                enabled = false // Explicitly disable
            )
        }

        assertTrue(mockRenderer.renderDatePickerCalled, "renderDatePicker should be called")
        assertEquals(false, mockRenderer.lastDatePickerEnabledRendered, "DatePicker should be rendered as disabled")
        assertNotNull(mockRenderer.lastDatePickerOnValueChangeRendered, "onValueChange callback should be captured")

        // Check modifier for disabled state
        val expectedModifier = Modifier()
            .opacity(0.6f) // disabled
            .cursor("default") // disabled
            .pointerEvents("none") // disabled
            .attribute("data-date-format", "yyyy-MM-dd") // Default format
        // min/max attributes not added if null
            
        assertEquals(expectedModifier.styles, mockRenderer.lastDatePickerModifierRendered?.styles, "Disabled modifier styles mismatch")
        assertEquals(expectedModifier.attributes, mockRenderer.lastDatePickerModifierRendered?.attributes, "Disabled modifier attributes mismatch")

        // Attempt to invoke the callback
        val originalValue = selectedDate
        mockRenderer.lastDatePickerOnValueChangeRendered?.invoke(LocalDate(2024, 6, 1))

        // Assert that the external callback variable was NOT changed
        assertEquals(originalValue, selectedDate, "onValueChange callback should not be invoked when the DatePicker is disabled")
    }

    @Test
    fun testDatePickerRangeValidation() {
        val mockRenderer = MockPlatformRenderer()
        var selectedDate: LocalDate? = LocalDate(2024, 3, 15)
        val minDate = LocalDate(2024, 2, 1)
        val maxDate = LocalDate(2024, 4, 30)
        val onValChange: (LocalDate?) -> Unit = { selectedDate = it }

        runComposableTest(mockRenderer) {
            DatePicker(
                value = selectedDate,
                onValueChange = onValChange,
                minDate = minDate,
                maxDate = maxDate,
                enabled = true
            )
        }

        assertTrue(mockRenderer.renderDatePickerCalled, "renderDatePicker should be called")
        assertNotNull(mockRenderer.lastDatePickerOnValueChangeRendered, "onValueChange callback should not be null")

        // 1. Try date before minDate - should not update
        val dateBeforeMin = LocalDate(2024, 1, 20)
        val valueBeforeAttempt = selectedDate
        mockRenderer.lastDatePickerOnValueChangeRendered?.invoke(dateBeforeMin)
        assertEquals(valueBeforeAttempt, selectedDate, "Date should not update when selected date is before minDate")

        // 2. Try date after maxDate - should not update
        val dateAfterMax = LocalDate(2024, 5, 5)
        mockRenderer.lastDatePickerOnValueChangeRendered?.invoke(dateAfterMax)
        assertEquals(valueBeforeAttempt, selectedDate, "Date should not update when selected date is after maxDate")

        // 3. Try date within range - should update
        val dateInRange = LocalDate(2024, 3, 10)
        mockRenderer.lastDatePickerOnValueChangeRendered?.invoke(dateInRange)
        assertEquals(dateInRange, selectedDate, "Date should update when selected date is within range")

        // 4. Try null date - should update (clear value)
        mockRenderer.lastDatePickerOnValueChangeRendered?.invoke(null)
        assertNull(selectedDate, "Date should clear when null is selected")
    }
} 