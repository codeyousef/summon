package com.summon

import kotlinx.html.DIV
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.style

/**
 * A layout component that creates empty space.
 * @param size The size of the space (CSS value like "10px", "2rem", etc.)
 * @param isVertical Whether this spacer creates vertical (true) or horizontal (false) space
 */
data class Spacer(
    val size: String,
    val isVertical: Boolean = false
) : Composable {
    /**
     * Renders this Spacer composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return getPlatformRenderer().renderSpacer(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 