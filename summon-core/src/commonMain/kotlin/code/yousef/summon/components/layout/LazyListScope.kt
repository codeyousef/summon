package codes.yousef.summon.components.layout

import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.ComposableDsl

/**
 * A scope for LazyColumn and LazyRow content.
 * Allows defining items and item content.
 */
@ComposableDsl
interface LazyListScope {
    /**
     * Add a single item to the list.
     *
     * @param key Optional stable and unique key to help with diffing
     * @param content The content of the item
     */
    fun item(key: Any? = null, content: @Composable () -> Unit)

    /**
     * Add multiple items to the list.
     *
     * @param items The list of items
     * @param key Optional function to extract a stable and unique key for each item
     * @param itemContent The content for each item
     */
    fun <T> items(
        items: List<T>,
        key: ((item: T) -> Any)? = null,
        itemContent: @Composable (item: T) -> Unit
    )

    /**
     * Add multiple indexed items to the list.
     *
     * @param items The list of items
     * @param key Optional function to extract a stable and unique key for each item
     * @param itemContent The content for each item with its index
     */
    fun <T> itemsIndexed(
        items: List<T>,
        key: ((index: Int, item: T) -> Any)? = null,
        itemContent: @Composable (index: Int, item: T) -> Unit
    )

    /**
     * Add a sticky header to the list.
     *
     * @param key Optional stable and unique key to help with diffing
     * @param content The content of the header
     */
    fun stickyHeader(key: Any? = null, content: @Composable () -> Unit)

    /**
     * Add a section divider to the list.
     *
     * @param key Optional stable and unique key to help with diffing
     * @param content The content of the divider
     */
    fun sectionDivider(key: Any? = null, content: @Composable () -> Unit)

    /**
     * Add spacing between items.
     *
     * @param height The height of the spacing for LazyColumn
     * @param width The width of the spacing for LazyRow
     */
    fun spacing(height: Float = 0f, width: Float = 0f)
}

/**
 * Internal implementation of LazyListScope.
 * Accessible within the commonMain module.
 */
internal class LazyListScopeImpl : LazyListScope {
    // Changed from val to internal val to allow access within the module
    internal val items = mutableListOf<@Composable () -> Unit>()
    internal val keys = mutableListOf<Any?>()
    internal val isHeader = mutableListOf<Boolean>()
    internal val isDivider = mutableListOf<Boolean>()
    internal val spacings = mutableListOf<Pair<Float, Float>>()

    override fun item(key: Any?, content: @Composable () -> Unit) {
        items.add(content)
        keys.add(key)
        isHeader.add(false)
        isDivider.add(false)
        spacings.add(0f to 0f)
    }

    override fun <T> items(
        items: List<T>,
        key: ((item: T) -> Any)?,
        itemContent: @Composable (item: T) -> Unit
    ) {
        for (item in items) {
            this.items.add { itemContent(item) }
            this.keys.add(key?.invoke(item))
            this.isHeader.add(false)
            this.isDivider.add(false)
            this.spacings.add(0f to 0f)
        }
    }

    override fun <T> itemsIndexed(
        items: List<T>,
        key: ((index: Int, item: T) -> Any)?,
        itemContent: @Composable (index: Int, item: T) -> Unit
    ) {
        for (i in items.indices) {
            this.items.add { itemContent(i, items[i]) }
            this.keys.add(key?.invoke(i, items[i]))
            this.isHeader.add(false)
            this.isDivider.add(false)
            this.spacings.add(0f to 0f)
        }
    }

    override fun stickyHeader(key: Any?, content: @Composable () -> Unit) {
        items.add(content)
        keys.add(key)
        isHeader.add(true)
        isDivider.add(false)
        spacings.add(0f to 0f)
    }

    override fun sectionDivider(key: Any?, content: @Composable () -> Unit) {
        items.add(content)
        keys.add(key)
        isHeader.add(false)
        isDivider.add(true)
        spacings.add(0f to 0f)
    }

    override fun spacing(height: Float, width: Float) {
        // Add an empty item with the specified spacing
        items.add { }
        keys.add(null)
        isHeader.add(false)
        isDivider.add(false)
        spacings.add(height to width)
    }
}
