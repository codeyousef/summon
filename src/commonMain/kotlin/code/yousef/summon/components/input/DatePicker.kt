package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.modifier.cursor
import code.yousef.summon.modifier.pointerEvents
import code.yousef.summon.modifier.attribute
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.LocalPlatformRenderer
import kotlinx.datetime.LocalDate

/**
 * A composable that allows users to select a date.
 *
 * @param value The currently selected date, or null if none selected.
 * @param onValueChange Callback invoked when the user selects a new date.
 * @param modifier Modifier applied to the date picker layout.
 * @param enabled Controls the enabled state.
 * @param label Optional label displayed for the date picker.
 * @param minDate The minimum selectable date (inclusive). Null means no lower bound.
 * @param maxDate The maximum selectable date (inclusive). Null means no upper bound.
 * @param dateFormat Format string for date display (e.g., "yyyy-MM-dd"). Platform-specific implementation.
 * @param initialDisplayMonth Initial month to display in the date picker calendar. Defaults to current month if null.
 */
@Composable
fun DatePicker(
    value: LocalDate?,
    onValueChange: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    dateFormat: String = "yyyy-MM-dd",
    initialDisplayMonth: LocalDate? = null
) {
    val composer = CompositionLocal.currentComposer
    // Apply styling directly to the element via modifier
    val finalModifier = modifier
        .opacity(if (enabled) 1f else 0.6f)
        .cursor(if (enabled) "pointer" else "default")
        .applyIf(!enabled) { pointerEvents("none") }

    composer?.startNode() // Start DatePicker node
    if (composer?.inserting == true) {
        val renderer = LocalPlatformRenderer.current
        // Pass relevant state and modifier to the renderer
        renderer.renderDatePicker(
            value = value,
            onValueChange = { newDate ->
                if (enabled) {
                    // Only allow dates within the min/max range
                    val isValid = (minDate == null || (newDate != null && newDate >= minDate)) &&
                                 (maxDate == null || (newDate != null && newDate <= maxDate))
                    
                    if (isValid || newDate == null) {
                        onValueChange(newDate)
                    }
                }
            },
            enabled = enabled,
            modifier = finalModifier.applyIf(label != null) { 
                attribute("data-label", label ?: "")
            }.applyIf(dateFormat.isNotEmpty()) {
                attribute("data-date-format", dateFormat)
            }.applyIf(minDate != null) {
                attribute("data-min-date", minDate.toString())
            }.applyIf(maxDate != null) {
                attribute("data-max-date", maxDate.toString())
            }.applyIf(initialDisplayMonth != null) {
                attribute("data-initial-month", initialDisplayMonth.toString())
            }
        )
    }
    composer?.endNode() // End DatePicker node (self-closing)
}

// The old DatePicker class and its methods are removed. 
