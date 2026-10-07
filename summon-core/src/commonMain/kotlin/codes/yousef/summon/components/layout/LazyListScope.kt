package codes.yousef.summon.components.layout

import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.ComposableDsl

/**
 * Result exposed by a paged [LazyListDataProvider].
 *
 * Providers may expose capability states as rows without substituting stale or unauthorized data.
 */
sealed interface LazyListItemResult<out T> {
    data class Data<T>(val value: T) : LazyListItemResult<T>
    data object Loading : LazyListItemResult<Nothing>
    data object Empty : LazyListItemResult<Nothing>
    data object Locked : LazyListItemResult<Nothing>
    data object PermissionDenied : LazyListItemResult<Nothing>
    data class Error(val code: String? = null) : LazyListItemResult<Nothing>
}

/**
 * Random-access source for a virtualized list.
 *
 * [itemAt] is called only for composed indices. Implementations should load bounded page windows
 * around those requests and return an explicit [LazyListItemResult] while data is unavailable.
 */
interface LazyListDataProvider<T> {
    val itemCount: Int
    fun key(index: Int): Any = index
    fun itemAt(index: Int): LazyListItemResult<T>
}

/** Defines virtualized [LazyColumn] and [LazyRow] content. */
@ComposableDsl
interface LazyListScope {
    fun item(key: Any? = null, content: @Composable () -> Unit)

    /**
     * Adds a count-backed interval without allocating one lambda per item.
     */
    fun items(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        itemContent: @Composable (index: Int) -> Unit
    )

    fun <T> items(
        items: List<T>,
        key: ((item: T) -> Any)? = null,
        itemContent: @Composable (item: T) -> Unit
    )

    fun <T> itemsIndexed(
        items: List<T>,
        key: ((index: Int, item: T) -> Any)? = null,
        itemContent: @Composable (index: Int, item: T) -> Unit
    )

    /**
     * Adds a paged provider. Only visible and overscan indices are requested.
     */
    fun <T> items(
        provider: LazyListDataProvider<T>,
        itemContent: @Composable (index: Int, item: LazyListItemResult<T>) -> Unit
    )

    fun stickyHeader(key: Any? = null, content: @Composable () -> Unit)
    fun sectionDivider(key: Any? = null, content: @Composable () -> Unit)
    fun spacing(height: Float = 0f, width: Float = 0f)
}

internal data class LazyListItemMetadata(
    val key: Any?,
    val isHeader: Boolean = false,
    val isDivider: Boolean = false,
    val spacingHeight: Float = 0f,
    val spacingWidth: Float = 0f
)

private interface LazyListInterval {
    val count: Int
    fun metadata(localIndex: Int): LazyListItemMetadata
    @Composable
    fun render(localIndex: Int)
}

internal class LazyListScopeImpl : LazyListScope {
    private val intervals = mutableListOf<LazyListInterval>()
    internal var itemCount: Int = 0
        private set

    override fun item(key: Any?, content: @Composable () -> Unit) {
        addInterval(
            count = 1,
            metadata = { LazyListItemMetadata(key) },
            render = { content() }
        )
    }

    override fun items(
        count: Int,
        key: ((index: Int) -> Any)?,
        itemContent: @Composable (index: Int) -> Unit
    ) {
        require(count >= 0) { "Lazy list item count must be non-negative" }
        if (count == 0) return
        addInterval(
            count = count,
            metadata = { index -> LazyListItemMetadata(key?.invoke(index)) },
            render = itemContent
        )
    }

    override fun <T> items(
        items: List<T>,
        key: ((item: T) -> Any)?,
        itemContent: @Composable (item: T) -> Unit
    ) {
        if (items.isEmpty()) return
        addInterval(
            count = items.size,
            metadata = { index -> LazyListItemMetadata(key?.invoke(items[index])) },
            render = { index -> itemContent(items[index]) }
        )
    }

    override fun <T> itemsIndexed(
        items: List<T>,
        key: ((index: Int, item: T) -> Any)?,
        itemContent: @Composable (index: Int, item: T) -> Unit
    ) {
        if (items.isEmpty()) return
        addInterval(
            count = items.size,
            metadata = { index -> LazyListItemMetadata(key?.invoke(index, items[index])) },
            render = { index -> itemContent(index, items[index]) }
        )
    }

    override fun <T> items(
        provider: LazyListDataProvider<T>,
        itemContent: @Composable (index: Int, item: LazyListItemResult<T>) -> Unit
    ) {
        val count = provider.itemCount
        require(count >= 0) { "Lazy list provider item count must be non-negative" }
        if (count == 0) return
        addInterval(
            count = count,
            metadata = { index -> LazyListItemMetadata(provider.key(index)) },
            render = { index -> itemContent(index, provider.itemAt(index)) }
        )
    }

    override fun stickyHeader(key: Any?, content: @Composable () -> Unit) {
        addInterval(
            count = 1,
            metadata = { LazyListItemMetadata(key, isHeader = true) },
            render = { content() }
        )
    }

    override fun sectionDivider(key: Any?, content: @Composable () -> Unit) {
        addInterval(
            count = 1,
            metadata = { LazyListItemMetadata(key, isDivider = true) },
            render = { content() }
        )
    }

    override fun spacing(height: Float, width: Float) {
        require(height.isFinite() && height >= 0f) { "Lazy list spacing height must be finite and non-negative" }
        require(width.isFinite() && width >= 0f) { "Lazy list spacing width must be finite and non-negative" }
        addInterval(
            count = 1,
            metadata = { LazyListItemMetadata(null, spacingHeight = height, spacingWidth = width) },
            render = {}
        )
    }

    internal fun metadata(index: Int): LazyListItemMetadata {
        val (interval, localIndex) = locate(index)
        return interval.metadata(localIndex)
    }

    @Composable
    internal fun render(index: Int) {
        val (interval, localIndex) = locate(index)
        interval.render(localIndex)
    }

    private fun addInterval(
        count: Int,
        metadata: (Int) -> LazyListItemMetadata,
        render: @Composable (Int) -> Unit
    ) {
        check(itemCount <= Int.MAX_VALUE - count) { "Lazy list item count exceeds Int range" }
        intervals += object : LazyListInterval {
            override val count: Int = count
            override fun metadata(localIndex: Int): LazyListItemMetadata = metadata(localIndex)
            @Composable
            override fun render(localIndex: Int) = render(localIndex)
        }
        itemCount += count
    }

    private fun locate(index: Int): Pair<LazyListInterval, Int> {
        require(index in 0 until itemCount) { "Lazy list item index is out of range" }
        var start = 0
        for (interval in intervals) {
            val end = start + interval.count
            if (index < end) return interval to (index - start)
            start = end
        }
        error("Lazy list interval lookup failed")
    }
}
