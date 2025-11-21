package codes.yousef.summon.hydration

import kotlinx.html.TagConsumer
import kotlinx.html.Tag

class SummonTagConsumer<T>(private val downstream: TagConsumer<T>) : TagConsumer<T> by downstream {
    private val idStack = ArrayDeque<String>()
    private val childCounters = ArrayDeque<MutableMap<String, Int>>()

    init {
        idStack.addLast("root")
        childCounters.addLast(mutableMapOf())
    }

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

    override fun onTagEnd(tag: Tag) {
        downstream.onTagEnd(tag)
        idStack.removeLast()
        childCounters.removeLast()
    }

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
