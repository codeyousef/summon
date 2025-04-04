package com.summon

import kotlinx.html.TagConsumer
import kotlinx.html.span
import kotlinx.html.style

/**
 * A composable that displays text.
 * @param text The text content to display
 * @param modifier The modifier to apply to this composable
 */
data class Text(
    val text: String,
    val modifier: Modifier = Modifier()
) : Composable {
    /**
     * Renders this Text composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return getPlatformRenderer().renderText(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 