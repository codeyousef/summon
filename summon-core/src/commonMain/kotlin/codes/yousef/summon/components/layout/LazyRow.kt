package codes.yousef.summon.components.layout

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.ariaAttribute
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.modifier.dataAttribute
import codes.yousef.summon.modifier.dataAttributes
import codes.yousef.summon.modifier.role
import codes.yousef.summon.modifier.style
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.key
import codes.yousef.summon.runtime.remember

/** A horizontally virtualized list with real scroll geometry and bounded item collection. */
@Composable
fun LazyRow(
    modifier: Modifier = Modifier,
    state: LazyListState = remember { LazyListState() },
    content: LazyListScope.() -> Unit
) {
    val scope = LazyListScopeImpl().also(content)
    val layout = state.layout(state.containerSize, scope.itemCount)
    val finalModifier = modifier then Modifier
        .style("overflow-x", "auto")
        .style("display", "flex")
        .style("flex-direction", "row")
        .style("max-width", "100%")
        .dataAttribute("direction", "row")
        .dataAttributes(state.getDataAttributes(scope.itemCount))
        .role("list")

    val renderer = LocalPlatformRenderer.current
    renderer.renderLazyRow(
        modifier = finalModifier,
        scrollPosition = state.scrollPosition,
        scrollRevision = state.scrollRevision,
        onViewportChanged = { scrollPosition, containerSize ->
            state.setContainerSize(containerSize)
            state.updateViewportScrollPosition(scrollPosition)
        },
        onItemMeasured = state::updateMeasuredItem,
        content = {
            renderLazySpacer(renderer, layout.leadingExtent, vertical = false, leading = true)
            val visibleKeys = mutableSetOf<Any>()
            for (index in layout.range) {
                val metadata = scope.metadata(index)
                val stableKey = state.stableKey(index, metadata.key)
                check(visibleKeys.add(stableKey)) { "Duplicate visible lazy list key" }
                state.prepareItem(index, stableKey)
                val rendererToken = state.rendererToken(stableKey)
                key(stableKey) {
                    var itemModifier = Modifier
                        .attribute("key", rendererToken)
                        .dataAttribute("lazy-item", "true")
                        .dataAttribute("item-index", index.toString())
                        .style("box-sizing", "border-box")
                        .style("flex-shrink", "0")
                        .style("min-width", "${state.itemSize}px")
                        .style("margin-top", "${metadata.spacingHeight}px")
                        .style("margin-left", "${metadata.spacingWidth}px")
                        .role("listitem")
                        .ariaAttribute("posinset", (index + 1).toString())
                    if (state.exposeItemCountToAccessibility) {
                        itemModifier = itemModifier.ariaAttribute("setsize", scope.itemCount.toString())
                    }
                    if (metadata.isHeader) {
                        itemModifier = itemModifier
                            .style("position", "sticky")
                            .style("left", "0")
                            .style("z-index", "1")
                            .style("background-color", "inherit")
                    }
                    if (metadata.isDivider) {
                        itemModifier = itemModifier.dataAttribute("section-divider", "true")
                    }
                    renderer.renderDiv(itemModifier) { scope.render(index) }
                }
            }
            state.retainRendererTokens(visibleKeys)
            renderLazySpacer(renderer, layout.trailingExtent, vertical = false, leading = false)
        }
    )
}
