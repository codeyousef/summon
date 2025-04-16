package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember
import kotlinx.datetime.LocalTime
import code.yousef.summon.state.State

/**
 * A composable for selecting time.
 *
 * @param value The currently selected time (using kotlinx.datetime.LocalTime).
 * @param onValueChange Lambda called when the time changes.
 * @param modifier Modifier for styling and attributes.
 * @param enabled Whether the time picker is enabled.
 * @param is24Hour Hint for display format (actual display might depend on locale/browser).
 * @param label Optional label (consider using FormField).
 */
@Composable
fun TimePicker(
    value: LocalTime?,
    onValueChange: (LocalTime?) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    is24Hour: Boolean = false,
    label: String? = null
) {
    val renderer = LocalPlatformRenderer.current

    renderer.renderTimePicker(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        is24Hour = is24Hour,
        modifier = modifier
    )

    // Removed direct label rendering
    // if (label != null) { ... }
}

/**
 * Stateful version of TimePicker.
 */
@Composable
fun StatefulTimePicker(
    initialValue: LocalTime? = null,
    onValueChange: (LocalTime?) -> Unit = {},
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    is24Hour: Boolean = false,
    label: String? = null
) {
    val timeState = remember { mutableStateOf<LocalTime?>(initialValue) }

    TimePicker(
        value = timeState.value,
        onValueChange = {
            timeState.value = it
            onValueChange(it)
        },
        modifier = modifier,
        enabled = enabled,
        is24Hour = is24Hour,
        label = label
    )
}

/**
 * Helper function to parse a time string into a kotlinx.datetime.LocalTime object.
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
 * Helper function to format a kotlinx.datetime.LocalTime object as a string.
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