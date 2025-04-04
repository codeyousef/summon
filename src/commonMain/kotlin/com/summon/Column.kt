package com.summon

import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.style

/**
 * A layout composable that places its children in a vertical sequence.
 * @param content The composables to display inside the column
 * @param modifier The modifier to apply to this composable
 */
class Column(
    val content: List<Composable>,
    val modifier: Modifier = Modifier()
) : Composable {
    /**
     * Renders this Column composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderColumn(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 