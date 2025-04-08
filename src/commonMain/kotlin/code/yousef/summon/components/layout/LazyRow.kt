package code.yousef.summon.components.layout

import code.yousef.summon.components.LayoutComponent
import code.yousef.summon.components.ScrollableComponent
import code.yousef.summon.core.Composable
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.PlatformRendererProviderLegacy.getRenderer
import kotlinx.html.TagConsumer

/**
 * A layout composable that displays a horizontal scrollable list with lazy loading.
 * LazyRow only renders the items that are visible in the viewport, improving performance
 * for large lists. This is similar to RecyclerView in Android or a virtualized list in JS frameworks.
 *
 * @param items The list of items to be displayed
 * @param itemContent A function that produces a Composable for each item
 * @param modifier The modifier to apply to this composable
 */
class LazyRow<T>(
    val items: List<T>,
    val itemContent: (T) -> Composable,
    val modifier: Modifier = Modifier()
) : Composable, LayoutComponent, ScrollableComponent {
    /**
     * Renders this LazyRow composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T2> compose(receiver: T2): T2 {
        if (receiver is TagConsumer<*>) {
            getRenderer().renderLazyRow(modifier)
        }
        return receiver
    }
} 