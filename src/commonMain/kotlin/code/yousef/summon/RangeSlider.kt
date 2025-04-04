package code.yousef.summon

import kotlinx.html.TagConsumer

/**
 * A composable that displays a range slider input field.
 * @param state The state that holds the current value of the slider
 * @param onValueChange Callback that is invoked when the value changes
 * @param label Optional label to display for the slider
 * @param min Minimum value of the range
 * @param max Maximum value of the range
 * @param step Step value for the slider
 * @param modifier The modifier to apply to this composable
 * @param disabled Whether the slider is disabled
 * @param showTooltip Whether to show a tooltip with the current value
 * @param valueFormat Function to format the value for display
 */
class RangeSlider(
    val state: MutableState<Double>,
    val onValueChange: (Double) -> Unit = {},
    val label: String? = null,
    val min: Double = 0.0,
    val max: Double = 100.0,
    val step: Double = 1.0,
    val modifier: Modifier = Modifier(),
    val disabled: Boolean = false,
    val showTooltip: Boolean = false,
    val valueFormat: (Double) -> String = { it.toString() }
) : Composable, InputComponent, FocusableComponent {
    /**
     * Renders this RangeSlider composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderRangeSlider(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 