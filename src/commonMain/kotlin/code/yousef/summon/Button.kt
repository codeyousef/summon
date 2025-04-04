package code.yousef.summon

import kotlinx.html.TagConsumer

/**
 * A composable that displays a button with text.
 * @param label The text to display on the button
 * @param onClick The callback to invoke when the button is clicked
 * @param modifier The modifier to apply to this composable
 */
class Button(
    val label: String,
    val onClick: (Any) -> Unit = {},
    val modifier: Modifier = Modifier()
) : Composable, ClickableComponent, FocusableComponent {
    /**
     * Renders this Button composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderButton(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 