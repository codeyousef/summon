package code.yousef.summon.components.input

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.PlatformRendererProvider


// Use placeholder typealias for LocalTime
// In a real scenario, this should be a proper date/time library type (e.g., kotlinx-datetime)
typealias LocalTime = Any

/**
 * A time picker component that allows users to select a time.
 * 
 * @param value The currently selected time value
 * @param onValueChange Callback when the time value changes
 * @param modifier Optional modifier for the component
 * @param enabled Whether the time picker is enabled
 * @param label Optional label text (placeholder)
 */
@Composable
fun TimePicker(
    value: LocalTime,
    onValueChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    amPmFormat: Boolean = false // 12-hour format with AM/PM vs 24-hour format
) {
    val platformRenderer = PlatformRendererProvider.getPlatformRenderer()
    
    // Format time to display
    val displayText = when (amPmFormat) {
        true -> value.format12Hour()
        false -> value.format24Hour()
    }
    
    // Render platform-specific time picker
    platformRenderer.renderTimePicker(
        value = value,
        onValueChange = { newTime -> onValueChange(newTime) },
        modifier = modifier,
        enabled = enabled,
        label = label,
        amPmFormat = amPmFormat,
        displayText = displayText
    )
}

// The old TimePicker class and its methods are removed. 
