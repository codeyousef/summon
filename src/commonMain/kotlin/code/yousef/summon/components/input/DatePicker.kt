package code.yousef.summon.components.input

import code.yousef.summon.*
import code.yousef.summon.core.Composable
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer

/**
 * A composable that displays a date picker.
 * @param state The state that holds the current selected date as a string (format: YYYY-MM-DD)
 * @param onDateChange Callback that is invoked when the selected date changes
 * @param label Optional label to display for the date picker
 * @param placeholder Placeholder text to show when no date is selected
 * @param modifier The modifier to apply to this composable
 * @param min Minimum selectable date (format: YYYY-MM-DD)
 * @param max Maximum selectable date (format: YYYY-MM-DD)
 * @param disabled Whether the date picker is disabled
 * @param validators List of validators to apply to the input
 */
class DatePicker(
    val state: MutableState<String>,
    val onDateChange: (String) -> Unit = {},
    val label: String? = null,
    val placeholder: String? = null,
    val modifier: Modifier = Modifier(),
    val min: String? = null,
    val max: String? = null,
    val disabled: Boolean = false,
    val validators: List<Validator> = emptyList()
) : Composable, InputComponent, FocusableComponent {
    // Internal state to track validation errors
    private val validationErrors = mutableStateOf<List<String>>(emptyList())

    /**
     * Renders this DatePicker composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderDatePicker(this, receiver as TagConsumer<T>)
        }
        return receiver
    }

    /**
     * Validates the current selected date against all validators.
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