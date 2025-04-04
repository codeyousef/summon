package com.summon

import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.style

/**
 * A composable that displays a radio button input field.
 * @param selected Whether this radio button is selected
 * @param onClick Callback that is invoked when the radio button is clicked
 * @param label Optional label to display for the radio button
 * @param value The value associated with this radio button
 * @param name The name of the radio button group
 * @param modifier The modifier to apply to this composable
 * @param disabled Whether the radio button is disabled
 */
class RadioButton<T>(
    val selected: Boolean,
    val onClick: () -> Unit = {},
    val label: String? = null,
    val value: T,
    val name: String,
    val modifier: Modifier = Modifier(),
    val disabled: Boolean = false
) : Composable {
    /**
     * Renders this RadioButton composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderRadioButton(this as RadioButton<Any>, receiver as TagConsumer<T>)
        }
        return receiver
    }
}

/**
 * A composable that represents a group of radio buttons.
 * @param selectedValue The currently selected value
 * @param onSelectedChange Callback that is invoked when selection changes
 * @param options The list of options to display as radio buttons
 * @param modifier The modifier to apply to this composable
 */
class RadioGroup<V>(
    val selectedValue: MutableState<V>,
    val onSelectedChange: (V) -> Unit = {},
    val options: List<RadioOption<V>>,
    val modifier: Modifier = Modifier()
) : Composable {
    /**
     * Renders this RadioGroup composable to the HTML DOM.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val consumer = receiver as TagConsumer<T>
            val groupName = "radio-group-${this.hashCode()}"
            
            // Create a container for the radio group
            consumer.div {
                style = modifier.toStyleString()
                
                // Render each radio button option
                options.forEach { option ->
                    val isSelected = option.value == this@RadioGroup.selectedValue.value
                    val radioButton = RadioButton(
                        selected = isSelected,
                        onClick = {
                            this@RadioGroup.selectedValue.value = option.value
                            this@RadioGroup.onSelectedChange(option.value) 
                        },
                        label = option.label,
                        value = option.value,
                        name = groupName,
                        modifier = option.modifier,
                        disabled = option.disabled
                    )
                    radioButton.compose(this)
                }
            }
            
            return receiver
        }
        return receiver
    }
}

/**
 * Data class representing a radio button option.
 */
data class RadioOption<T>(
    val value: T,
    val label: String,
    val modifier: Modifier = Modifier(),
    val disabled: Boolean = false
) 