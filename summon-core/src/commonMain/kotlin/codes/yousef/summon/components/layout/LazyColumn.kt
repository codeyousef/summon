package codes.yousef.summon.components.layout

import codes.yousef.summon.core.mapOfCompat
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
import codes.yousef.summon.state.mutableStateOf
import kotlin.math.abs
import kotlin.math.floor

private const val MAX_RETAINED_LAZY_MEASUREMENTS = 2_048

private data class DefaultLazyItemKey(val index: Int)

internal data class LazyListLayout(
    val range: IntRange,
    val leadingExtent: Float,
    val trailingExtent: Float,
    val totalExtent: Float
)

/**
 * Scroll, viewport and measured-row state shared by [LazyColumn] and [LazyRow].
 *
 * [itemSize] is the expected row extent. Measured browser rows refine offsets without retaining
 * item content. Measurement history is bounded.
 */
class LazyListState {
    private val _scrollPosition = mutableStateOf(0f)
    private val _itemSize = mutableStateOf(50f)
    private val _overscrollItems = mutableStateOf(2)
    private val _containerSize = mutableStateOf(600f)
    private val _exposeItemCount = mutableStateOf(true)
    private val _measurementRevision = mutableStateOf(0)
    private val measuredSizes = mutableMapOf<Int, Float>()
    private val measuredKeys = mutableMapOf<Int, Any>()
    private val rendererTokens = mutableMapOf<Any, String>()
    private var nextRendererToken = 0L
    private var firstVisibleIndex = 0
    private var lastItemCount = 0
    private var scrollCommandRevision = 0

    /** Current requested scroll offset in pixels. */
    val scrollPosition: Float get() = _scrollPosition.value
    /** Expected unmeasured item extent in pixels. */
    val itemSize: Float get() = _itemSize.value
    /** Rows composed before and after the visible range. */
    val overscrollItems: Int get() = _overscrollItems.value
    /** Current viewport extent in pixels. */
    val containerSize: Float get() = _containerSize.value
    /** Whether accessibility metadata exposes total row count. */
    val exposeItemCountToAccessibility: Boolean get() = _exposeItemCount.value
    internal val scrollRevision: Int get() = scrollCommandRevision

    /** Computes the visible and overscan indices for [containerSize] and [totalItems]. */
    fun getVisibleItemRange(containerSize: Float, totalItems: Int): IntRange =
        layout(containerSize, totalItems).range

    /** Requests finite [newPosition], clamped to known content bounds. */
    fun updateScrollPosition(newPosition: Float) {
        require(newPosition.isFinite()) { "Lazy list scroll position must be finite" }
        val maxScroll = if (lastItemCount == 0) {
            Float.MAX_VALUE
        } else {
            (totalExtent(lastItemCount) - containerSize).coerceAtLeast(0f)
        }
        requestScrollPosition(newPosition.coerceIn(0f, maxScroll))
    }

    /** Sets the positive finite fallback row [size]. */
    fun setItemSize(size: Float) {
        require(size.isFinite() && size > 0f) { "Lazy list item size must be finite and positive" }
        if (_itemSize.value != size) {
            _itemSize.value = size
            measuredSizes.clear()
            measuredKeys.clear()
            bumpMeasurementRevision()
        }
    }

    /** Sets the non-negative overscan row [count]. */
    fun setOverscrollItems(count: Int) {
        require(count >= 0) { "Lazy list overscan must be non-negative" }
        _overscrollItems.value = count
    }

    /** Sets the finite non-negative viewport [size]. */
    fun setContainerSize(size: Float) {
        require(size.isFinite() && size >= 0f) { "Lazy list viewport size must be finite and non-negative" }
        _containerSize.value = size
    }

    /** Controls whether total item count is emitted for assistive technology. */
    fun setExposeItemCountToAccessibility(expose: Boolean) {
        _exposeItemCount.value = expose
    }

    /**
     * Moves the scroll anchor only when [index] is outside the viewport.
     */
    fun ensureItemVisible(index: Int, totalItems: Int) {
        require(index in 0 until totalItems) { "Lazy list item index is out of range" }
        lastItemCount = totalItems
        val start = offsetFor(index)
        val end = start + extentFor(index)
        val viewportStart = scrollPosition
        val viewportEnd = viewportStart + containerSize
        when {
            start < viewportStart -> requestScrollPosition(start)
            end > viewportEnd -> requestScrollPosition(
                (end - containerSize).coerceAtMost((totalExtent(totalItems) - containerSize).coerceAtLeast(0f))
            )
        }
    }

    /** Returns stable renderer metadata for [totalItems]. */
    fun getDataAttributes(totalItems: Int): Map<String, String> {
        val formattedItemSize = if (itemSize == itemSize.toInt().toFloat()) {
            "${itemSize.toInt()}.0"
        } else {
            itemSize.toString()
        }
        return mapOfCompat(
            "data-total-items" to totalItems.toString(),
            "data-item-size" to formattedItemSize,
            "data-overscroll-items" to overscrollItems.toString(),
            "data-lazy-container" to "true"
        )
    }

    internal fun layout(containerSize: Float, totalItems: Int): LazyListLayout {
        require(containerSize.isFinite() && containerSize >= 0f) {
            "Lazy list viewport size must be finite and non-negative"
        }
        require(totalItems >= 0) { "Lazy list item count must be non-negative" }
        _measurementRevision.value
        lastItemCount = totalItems
        measuredSizes.keys.removeAll { it >= totalItems }
        measuredKeys.keys.removeAll { it >= totalItems }
        if (totalItems == 0) {
            firstVisibleIndex = 0
            return LazyListLayout(IntRange.EMPTY, 0f, 0f, 0f)
        }

        val maximumScroll = (totalExtent(totalItems) - containerSize).coerceAtLeast(0f)
        val viewportStart = scrollPosition.coerceIn(0f, maximumScroll)
        if (viewportStart != scrollPosition) requestScrollPosition(viewportStart)

        var first = floor(viewportStart / itemSize).toInt().coerceIn(0, totalItems - 1)
        while (first > 0 && offsetFor(first) > viewportStart) first--
        while (first < totalItems - 1 && offsetFor(first + 1) <= viewportStart) first++
        firstVisibleIndex = first

        val viewportEnd = viewportStart + containerSize
        var last = first
        while (last < totalItems - 1 && offsetFor(last + 1) <= viewportEnd) last++

        val rangeStart = (first - overscrollItems).coerceAtLeast(0)
        val rangeEnd = (last + overscrollItems).coerceAtMost(totalItems - 1)
        val total = totalExtent(totalItems)
        val leading = offsetFor(rangeStart)
        val trailing = (total - offsetFor(rangeEnd + 1)).coerceAtLeast(0f)
        return LazyListLayout(rangeStart..rangeEnd, leading, trailing, total)
    }

    internal fun prepareItem(index: Int, stableKey: Any) {
        val prior = measuredKeys.put(index, stableKey)
        if (prior != null && prior != stableKey && measuredSizes.remove(index) != null) {
            bumpMeasurementRevision()
        }
    }

    internal fun updateMeasuredItem(index: Int, size: Float) {
        if (index !in 0 until lastItemCount || !size.isFinite() || size <= 0f) return
        val prior = measuredSizes[index] ?: itemSize
        if (abs(prior - size) < 0.5f) return
        if (index !in measuredSizes && measuredSizes.size >= MAX_RETAINED_LAZY_MEASUREMENTS) {
            val eviction = measuredSizes.keys.maxByOrNull { abs(it - firstVisibleIndex) }
            if (eviction != null) {
                measuredSizes.remove(eviction)
                measuredKeys.remove(eviction)
            }
        }
        measuredSizes[index] = size
        if (index < firstVisibleIndex) {
            requestScrollPosition((scrollPosition + size - prior).coerceAtLeast(0f))
        }
        bumpMeasurementRevision()
    }

    internal fun stableKey(index: Int, key: Any?): Any = key ?: DefaultLazyItemKey(index)

    internal fun rendererToken(stableKey: Any): String =
        rendererTokens.getOrPut(stableKey) { "lazy-${nextRendererToken++}" }

    internal fun retainRendererTokens(visibleKeys: Set<Any>) {
        rendererTokens.keys.retainAll(visibleKeys)
    }

    private fun extentFor(index: Int): Float = measuredSizes[index] ?: itemSize

    private fun offsetFor(index: Int): Float {
        var offset = index * itemSize
        for ((measuredIndex, measuredSize) in measuredSizes) {
            if (measuredIndex < index) offset += measuredSize - itemSize
        }
        return offset.coerceAtLeast(0f)
    }

    private fun totalExtent(totalItems: Int): Float = offsetFor(totalItems)

    internal fun updateViewportScrollPosition(newPosition: Float) {
        if (newPosition.isFinite()) _scrollPosition.value = newPosition.coerceAtLeast(0f)
    }

    private fun requestScrollPosition(newPosition: Float) {
        if (_scrollPosition.value != newPosition) {
            _scrollPosition.value = newPosition
            scrollCommandRevision = (scrollCommandRevision + 1) and Int.MAX_VALUE
        }
    }

    private fun bumpMeasurementRevision() {
        _measurementRevision.value = _measurementRevision.value + 1
    }
}

/**
 * A vertically virtualized list with real scroll geometry and bounded item collection.
 */
@Composable
fun LazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = remember { LazyListState() },
    content: LazyListScope.() -> Unit
) {
    val scope = LazyListScopeImpl().also(content)
    val layout = state.layout(state.containerSize, scope.itemCount)
    val finalModifier = modifier then Modifier
        .style("overflow-y", "auto")
        .style("display", "flex")
        .style("flex-direction", "column")
        .style("max-height", "100%")
        .dataAttribute("direction", "column")
        .dataAttributes(state.getDataAttributes(scope.itemCount))
        .role("list")

    val renderer = LocalPlatformRenderer.current
    renderer.renderLazyColumn(
        modifier = finalModifier,
        scrollPosition = state.scrollPosition,
        scrollRevision = state.scrollRevision,
        onViewportChanged = { scrollPosition, containerSize ->
            state.setContainerSize(containerSize)
            state.updateViewportScrollPosition(scrollPosition)
        },
        onItemMeasured = state::updateMeasuredItem,
        content = {
            renderLazySpacer(renderer, layout.leadingExtent, vertical = true, leading = true)
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
                        .style("min-height", "${state.itemSize}px")
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
                            .style("top", "0")
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
            renderLazySpacer(renderer, layout.trailingExtent, vertical = true, leading = false)
        }
    )
}

@Composable
internal fun renderLazySpacer(
    renderer: codes.yousef.summon.runtime.PlatformRenderer,
    extent: Float,
    vertical: Boolean,
    leading: Boolean
) {
    val axis = if (vertical) "height" else "width"
    renderer.renderDiv(
        Modifier
            .attribute("key", if (leading) "lazy-leading-spacer" else "lazy-trailing-spacer")
            .dataAttribute("lazy-spacer", if (leading) "leading" else "trailing")
            .style(axis, "${extent.coerceAtLeast(0f)}px")
            .style("flex", "0 0 ${extent.coerceAtLeast(0f)}px")
            .attribute("aria-hidden", "true")
    ) {}
}
