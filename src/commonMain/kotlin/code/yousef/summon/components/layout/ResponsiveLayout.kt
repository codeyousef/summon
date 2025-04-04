package code.yousef.summon.components.layout

import code.yousef.summon.core.Composable
import code.yousef.summon.LayoutComponent
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer

/**
 * Screen size breakpoints for responsive layouts.
 */
enum class ScreenSize {
    SMALL,  // Mobile phones (< 600px)
    MEDIUM, // Tablets (600px - 959px)
    LARGE,  // Desktop (960px - 1279px)
    XLARGE  // Large desktop (>= 1280px)
}

/**
 * A layout composable that displays different content based on the screen size.
 * ResponsiveLayout allows developers to create adaptive UI layouts that respond to
 * different device sizes and orientations.
 *
 * @param content Map of screen sizes to the composables that should be displayed at those sizes
 * @param defaultContent The default composable to display if no specific content is provided for the current screen size
 * @param modifier The modifier to apply to this composable
 */
class ResponsiveLayout(
    val content: Map<ScreenSize, Composable>,
    val defaultContent: Composable,
    val modifier: Modifier = Modifier()
) : Composable, LayoutComponent {
    /**
     * Renders this ResponsiveLayout composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderResponsiveLayout(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 