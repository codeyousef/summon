package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.mutableStateOf

/**
 * State holder for lazy lists that tracks scroll position and visible items.
 */
class LazyListState {
    // Current scroll position in pixels
    private val _scrollPosition = mutableStateOf(0f)
    val scrollPosition: Float
        get() = _scrollPosition.value

    // Estimated height/width of each item in pixels (can be refined later)
    private val _itemSize = mutableStateOf(50f)
    val itemSize: Float
        get() = _itemSize.value

    // Number of items to render above and below the visible area for smooth scrolling
    private val _overscrollItems = mutableStateOf(2)
    val overscrollItems: Int
        get() = _overscrollItems.value

    // Container size (height for column, width for row)
    private val _containerSize = mutableStateOf(600f)
    val containerSize: Float
        get() = _containerSize.value

    // Calculate which items should be visible based on container size and scroll position
    fun getVisibleItemRange(containerSize: Float, totalItems: Int): IntRange {
        val visibleItemCount = (containerSize / itemSize).toInt() + 1 // +1 to handle partial items
        val firstVisibleItem = (scrollPosition / itemSize).toInt() - overscrollItems
        val lastVisibleItem = firstVisibleItem + visibleItemCount + (2 * overscrollItems)

        return IntRange(
            firstVisibleItem.coerceAtLeast(0),
            lastVisibleItem.coerceAtMost(totalItems - 1)
        )
    }

    // Update scroll position (would be called from JS)
    fun updateScrollPosition(newPosition: Float) {
        _scrollPosition.value = newPosition
    }

    // Set item size
    fun setItemSize(size: Float) {
        _itemSize.value = size
    }

    // Set overscroll items
    fun setOverscrollItems(count: Int) {
        _overscrollItems.value = count
    }

    // Set container size
    fun setContainerSize(size: Float) {
        _containerSize.value = size
    }

    // Get data attributes for the lazy container
    fun getDataAttributes(totalItems: Int): Map<String, String> {
        // Ensure itemSize always has a decimal point for consistent representation across platforms
        val formattedItemSize = if (itemSize == itemSize.toInt().toFloat()) {
            "${itemSize.toInt()}.0"
        } else {
            itemSize.toString()
        }

        return mapOf(
            "data-total-items" to totalItems.toString(),
            "data-item-size" to formattedItemSize,
            "data-overscroll-items" to overscrollItems.toString(),
            "data-lazy-container" to "true"
        )
    }
}

/**
 * A vertically scrolling list that only composes and lays out the currently visible items.
 *
 * @param modifier The modifier to be applied to the LazyColumn
 * @param state Optional state object that controls and observes scrolling
 * @param content The content lambda defining the children, scoped to `LazyListScope`.
 */
@Composable
fun LazyColumn(
    modifier: Modifier = Modifier(),
    state: LazyListState = remember { LazyListState() },
    content: LazyListScope.() -> Unit
) {
    val composer = CompositionLocal.currentComposer

    // Create the scope first to collect item definitions
    val scope = LazyListScopeImpl()
    scope.content() // Execute user lambda to populate scope.items

    // Add data attributes and styles to the modifier
    val dataAttributes = state.getDataAttributes(scope.items.size)
    var finalModifier = modifier.then(
        Modifier()
            .style("overflow-y", "auto")
            .style("display", "flex")
            .style("flex-direction", "column")
            .style("max-height", "100%")
            .style("data-direction", "column")
    )

    // Add data attributes to the modifier
    dataAttributes.forEach { (key, value) ->
        finalModifier = finalModifier.style(key, value)
    }

    // Add a scroll handler attribute that the platform renderer can use
    // This attribute tells the renderer to update the LazyListState when scrolling
    finalModifier = finalModifier.style("__attr:data-scroll-handler", "updateLazyListState")

    // Add an onscroll event handler to update the scroll position and visible items
    finalModifier = finalModifier.style("onscroll", "window.summonHandleScroll(event, null)")

    val renderer = LocalPlatformRenderer.current

    // Calculate the visible range based on the current scroll position
    val visibleRange = state.getVisibleItemRange(state.containerSize, scope.items.size)

    renderer.renderLazyColumn(
        modifier = finalModifier,
        content = { // 'this' is FlowContent scope from the renderer
            // Only render items that are in the visible range
            for (i in visibleRange) {
                if (i >= 0 && i < scope.items.size) {
                    // Create a wrapper for the item with appropriate attributes
                    val key = scope.keys.getOrNull(i)
                    val isHeader = scope.isHeader.getOrNull(i) ?: false
                    val isDivider = scope.isDivider.getOrNull(i) ?: false
                    val spacing = scope.spacings.getOrNull(i) ?: (0f to 0f)

                    // Add data attributes for the item
                    val itemModifier = Modifier()
                        .style("data-item-index", i.toString())
                        .style("data-item-key", key?.toString() ?: "")
                        .style("margin-top", "${spacing.first}px")
                        .style("margin-left", "${spacing.second}px")

                    // Add sticky header styling if needed
                    if (isHeader) {
                        itemModifier
                            .style("position", "sticky")
                            .style("top", "0")
                            .style("z-index", "1")
                            .style("background-color", "inherit")
                    }

                    // Add section divider styling if needed
                    if (isDivider) {
                        itemModifier
                            .style("width", "100%")
                            .style("data-section-divider", "true")
                    }

                    // Render the item with its wrapper
                    renderer.renderDiv(
                        modifier = itemModifier,
                        content = {
                            // Execute the composable lambda provided for the visible item
                            scope.items[i]()
                        }
                    )
                }
            }
        }
    )
}

// The old LazyColumn class and its methods are removed. 
