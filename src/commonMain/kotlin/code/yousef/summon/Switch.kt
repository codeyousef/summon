package code.yousef.summon

import kotlinx.html.TagConsumer

/**
 * A composable that displays a toggle switch control.
 * @param state The state that holds the current value of the switch
 * @param onValueChange Callback that is invoked when the switch value changes
 * @param label Optional label to display for the switch
 * @param modifier The modifier to apply to this composable
 * @param disabled Whether the switch is disabled
 */
class Switch(
    val state: MutableState<Boolean>,
    val onValueChange: (Boolean) -> Unit = {},
    val label: String? = null,
    val modifier: Modifier = Modifier(),
    val disabled: Boolean = false
) : Composable, InputComponent, FocusableComponent {
    /**
     * Renders this Switch composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderSwitch(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 