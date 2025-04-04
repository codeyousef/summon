package code.yousef.summon

import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.style

/**
 * A composable that adds space between components.
 * @param size The size of the spacer (e.g., "16px")
 * @param isVertical Whether the spacer should take up vertical space (true) or horizontal space (false)
 */
class Spacer(
    val size: String,
    val isVertical: Boolean = true
) : Composable {
    /**
     * Renders this Spacer composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderSpacer(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 