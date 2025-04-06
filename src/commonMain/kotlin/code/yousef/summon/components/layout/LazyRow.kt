package code.yousef.summon.components.layout

import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.ComposableDsl

/**
 * A layout composable that displays a horizontally scrolling list.
 * It only composes and lays out the currently visible items.
 *
 * @param modifier The modifier to apply to this layout.
 * @param state The state object to manage scroll position.
 * @param contentPadding Padding around the content area.
 * @param reverseLayout Reverse the direction of scrolling and layout.
 * @param horizontalArrangement Horizontal arrangement of items when content width is smaller than the Row.
 * @param verticalAlignment Vertical alignment of items within the LazyRow.
 * @param content The lambda block defining the content of the list using `LazyListScope`.
 */
@Composable
fun LazyRow(
    modifier: Modifier = Modifier(),
    content: LazyListScope.() -> Unit
) {
    val finalModifier = modifier
        .style("overflow-x", "auto") // Basic horizontal scrolling
        .style("display", "flex")    // Needed for row arrangement
        .style("flex-direction", "row")

    val renderer = PlatformRendererProvider.getRenderer()

    renderer.renderLazyRow(modifier = finalModifier)

    val scope = LazyListScopeImpl()
    scope.content()

    scope.itemsContent.forEach { itemContent ->
        LazyItemScopeInstance.itemContent()
    }
} 