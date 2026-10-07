package codes.yousef.summon.hydration

import kotlinx.html.TagConsumer
import kotlinx.html.Tag

/**
 * Represents summon tag consumer.
 *
 * @property downstream The downstream value.
 */
class SummonTagConsumer<T>(private val downstream: TagConsumer<T>) : TagConsumer<T> by downstream {
    private val idStack = ArrayDeque<String>()
    private val childCounters = ArrayDeque<MutableMap<String, Int>>()

    init {
        idStack.addLast("root")
        childCounters.addLast(mutableMapOf())
    }

    /** Starts [tag], assigning its stable hydration identifier. */
    override fun onTagStart(tag: Tag) {
        val parentId = idStack.last()
        val counters = childCounters.last()

        val tagName = tag.tagName
        val count = counters.getOrPut(tagName) { 0 } + 1
        counters[tagName] = count

        // Check for explicit key
        val key = tag.attributes["key"]
        val finalId = if (key != null) {
             "$parentId/$tagName[$key]"
        } else {
             "$parentId/$tagName-$count"
        }

        tag.attributes["data-sid"] = finalId

        idStack.addLast(finalId)
        childCounters.addLast(mutableMapOf())

        downstream.onTagStart(tag)
    }

    /** Ends [tag] and restores its parent hydration context. */
    override fun onTagEnd(tag: Tag) {
        downstream.onTagEnd(tag)
        idStack.removeLast()
        childCounters.removeLast()
    }

    /** Forwards an [attribute] update with its nullable [value] for [tag]. */
    override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        try {
            downstream.onTagAttributeChange(tag, attribute, value)
        } catch (e: IllegalStateException) {
            // Ignore if downstream complains about attribute change timing.
            // This can happen with DelayedConsumer when attributes are modified in onTagStart,
            // but the attributes are already in the map so they will be rendered correctly.
        }
    }
}
