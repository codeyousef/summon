package com.summon

import kotlinx.html.TagConsumer
import kotlinx.html.button
import kotlinx.html.style

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
) : Composable {
    /**
     * Renders this Button composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return getPlatformRenderer().renderButton(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 