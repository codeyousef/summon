package com.summon

import kotlinx.html.TagConsumer

/**
 * A layout composable that maintains a specific aspect ratio (width-to-height) for its content.
 * AspectRatio is useful for image containers, videos, and other content where
 * maintaining proportions is important.
 *
 * @param content The composable to display inside the AspectRatio container
 * @param ratio The desired aspect ratio (width / height), e.g. 16/9, 4/3, 1.0, etc.
 * @param modifier The modifier to apply to this composable
 */
class AspectRatio(
    val content: Composable,
    val ratio: Double,
    val modifier: Modifier = Modifier()
) : Composable, LayoutComponent {
    /**
     * Renders this AspectRatio composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderAspectRatio(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 