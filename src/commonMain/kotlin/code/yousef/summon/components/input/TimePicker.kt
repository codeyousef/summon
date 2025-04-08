package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.LocalTime
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember
import code.yousef.summon.validation.Validator

/**
 * A composable that displays a time picker.
 *
 * @param value The current time value (format: HH:MM or HH:MM:SS)
 * @param onValueChange Callback that is invoked when the selected time changes
 * @param modifier The modifier to apply to this composable
 * @param label Optional label to display for the time picker
 * @param placeholder Placeholder text to show when no time is selected
 * @param use24Hour Whether to use 24-hour format (true) or 12-hour format (false)
 * @param isEnabled Whether the time picker is enabled
 * @param isError Whether the time picker is in an error state
 * @param validators List of validators to apply to the input
 */
@Composable
fun TimePicker(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    label: String? = null,
    placeholder: String? = null,
    use24Hour: Boolean = true,
    isEnabled: Boolean = true,
    isError: Boolean = false,
    validators: List<Validator> = emptyList()
) {
    // Internal state to track validation errors
    val validationErrors = remember { mutableStateOf(emptyList<String>()) }

    // Validate the time
    val errors = validators.mapNotNull { validator ->
        val result = validator.validate(value)
        if (!result.isValid) result.errorMessage else null
    }
    validationErrors.value = errors

    // Parse the time value into a LocalTime object for the renderer
    val time = parseTimeString(value)

    // Get the platform renderer
    val renderer = getPlatformRenderer()

    // Render the time picker using the platform renderer
    renderer.renderTimePicker(
        time = time,
        onTimeChange = { newTime ->
            val newValue = formatLocalTime(newTime, showSeconds = value.split(":").size > 2)
            onValueChange(newValue)
        },
        modifier = modifier,
        is24Hour = use24Hour
    )

    // Optionally render a label if provided
    if (label != null) {
        // Label would be rendered here in a real implementation
    }

    // Optionally render validation errors
    if (validationErrors.value.isNotEmpty()) {
        // Errors would be displayed here in a real implementation
    }
}

/**
 * A stateful version of TimePicker that manages its own state.
 *
 * @param initialValue The initial time value (format: HH:MM or HH:MM:SS)
 * @param onValueChange Callback that is invoked when the selected time changes
 * @param modifier The modifier to apply to this composable
 * @param label Optional label to display for the time picker
 * @param placeholder Placeholder text to show when no time is selected
 * @param use24Hour Whether to use 24-hour format (true) or 12-hour format (false)
 * @param isEnabled Whether the time picker is enabled
 * @param isError Whether the time picker is in an error state
 * @param validators List of validators to apply to the input
 */
@Composable
fun StatefulTimePicker(
    initialValue: String = "",
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier(),
    label: String? = null,
    placeholder: String? = null,
    use24Hour: Boolean = true,
    isEnabled: Boolean = true,
    isError: Boolean = false,
    validators: List<Validator> = emptyList()
) {
    // Create state to store the time value
    val timeState = remember { mutableStateOf(initialValue) }

    // Use the stateless TimePicker composable
    TimePicker(
        value = timeState.value,
        onValueChange = { newValue ->
            timeState.value = newValue
            onValueChange(newValue)
        },
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        use24Hour = use24Hour,
        isEnabled = isEnabled,
        isError = isError,
        validators = validators
    )
}

/**
 * Helper function to parse a time string into a LocalTime object.
 */
private fun parseTimeString(value: String): LocalTime? {
    if (value.isBlank()) return null

    val parts = value.split(":")
    return try {
        when (parts.size) {
            2 -> LocalTime(parts[0].toInt(), parts[1].toInt(), 0)
            3 -> LocalTime(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            else -> null
        }
    } catch (e: Exception) {
        null
    }
}

/**
 * Helper function to format a LocalTime object as a string.
 */
private fun formatLocalTime(time: LocalTime?, showSeconds: Boolean): String {
    if (time == null) return ""

    val hourStr = time.hour.toString().padStart(2, '0')
    val minuteStr = time.minute.toString().padStart(2, '0')

    return if (showSeconds) {
        val secondStr = time.second.toString().padStart(2, '0')
        "$hourStr:$minuteStr:$secondStr"
    } else {
        "$hourStr:$minuteStr"
    }
} 