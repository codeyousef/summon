package code.yousef.summon.components.layout

import code.yousef.summon.core.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.PlatformRendererProviderLegacy.getRenderer
import kotlinx.html.TagConsumer

/**
 * A composable that adds space between components.
 * @param size The size of the spacer (e.g., "16px")
 * @param isVertical Whether the spacer should take up vertical space (true) or horizontal space (false)
 */
class Spacer(
    val size: String,
    val isVertical: Boolean = true,
    val modifier: Modifier = Modifier()
) : Composable {
    /**
     * Renders this Spacer composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            getRenderer().renderSpacer(modifier)
        }
        return receiver
    }
} 