package com.summon

import kotlinx.html.TagConsumer

/**
 * A composable that displays a time picker.
 * @param state The state that holds the current selected time as a string (format: HH:MM or HH:MM:SS)
 * @param onTimeChange Callback that is invoked when the selected time changes
 * @param label Optional label to display for the time picker
 * @param placeholder Placeholder text to show when no time is selected
 * @param modifier The modifier to apply to this composable
 * @param use24Hour Whether to use 24-hour format (true) or 12-hour format (false)
 * @param showSeconds Whether to display seconds input
 * @param min Minimum selectable time (format: HH:MM or HH:MM:SS)
 * @param max Maximum selectable time (format: HH:MM or HH:MM:SS)
 * @param disabled Whether the time picker is disabled
 * @param validators List of validators to apply to the input
 */
class TimePicker(
    val state: MutableState<String>,
    val onTimeChange: (String) -> Unit = {},
    val label: String? = null,
    val placeholder: String? = null,
    val modifier: Modifier = Modifier(),
    val use24Hour: Boolean = true,
    val showSeconds: Boolean = false,
    val min: String? = null,
    val max: String? = null,
    val disabled: Boolean = false,
    val validators: List<Validator> = emptyList()
) : Composable {
    // Internal state to track validation errors
    private val validationErrors = mutableStateOf<List<String>>(emptyList())

    /**
     * Renders this TimePicker composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderTimePicker(this, receiver as TagConsumer<T>)
        }
        return receiver
    }

    /**
     * Validates the current selected time against all validators.
     * @return True if validation passed, false otherwise
     */
    fun validate(): Boolean {
        val errors = validators.mapNotNull { validator ->
            if (!validator.validate(state.value)) validator.errorMessage else null
        }
        validationErrors.value = errors
        return errors.isEmpty()
    }

    /**
     * Gets the current validation errors.
     */
    fun getValidationErrors(): List<String> = validationErrors.value

    /**
     * Indicates whether the field is currently valid.
     */
    fun isValid(): Boolean = validationErrors.value.isEmpty()
} 