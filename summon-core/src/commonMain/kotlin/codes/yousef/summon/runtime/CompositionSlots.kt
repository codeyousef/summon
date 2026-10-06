package codes.yousef.summon.runtime

/** Separate namespace for explicit identity, rather than hashes or internal group occurrences. */
internal data class ExplicitCompositionKey(val values: List<Any?>)

/** Hierarchical storage shared by the runtime composers. Child cursors never consume parent slots. */
internal class CompositionSlots {
    private data class GroupId(val key: Any?, val occurrence: Int)
    private data class Address(val path: List<GroupId>, val index: Int)
    private data class NamedAddress(val path: List<GroupId>, val key: Any)
    private class Frame(val path: List<GroupId>) {
        var index = 0
        val occurrences = mutableMapOf<Any?, Int>()
    }

    private val slots = mutableMapOf<Address, Any?>()
    private val named = mutableMapOf<NamedAddress, Any?>()
    private val frames = mutableListOf(Frame(emptyList()))
    private val visitedSlots = mutableSetOf<Address>()
    private val visitedGroups = mutableSetOf<List<GroupId>>()
    private val current get() = frames.last()
    private fun address() = Address(current.path, current.index)

    fun beginPass() {
        frames.clear()
        frames.add(Frame(emptyList()))
        visitedSlots.clear()
        visitedGroups.clear()
        visitedGroups.add(emptyList())
    }

    fun startGroup(key: Any?) {
        val parent = current
        val occurrence = parent.occurrences[key] ?: 0
        check(key !is ExplicitCompositionKey || occurrence == 0) {
            "Duplicate explicit composition key in the same parent group"
        }
        parent.occurrences[key] = occurrence + 1
        val path = parent.path + GroupId(key, occurrence)
        visitedGroups.add(path)
        frames.add(Frame(path))
    }

    fun endGroup() {
        if (frames.size > 1) frames.removeAt(frames.lastIndex)
    }

    fun nextSlot() { current.index++ }
    fun getSlot(): Any? = address().let { visitedSlots.add(it); slots[it] }
    fun setSlot(value: Any?) {
        val address = address()
        visitedSlots.add(address)
        replace(slots, address, value)
    }

    fun rememberedValue(key: Any): Any? = named[NamedAddress(current.path, key)]
    fun updateRememberedValue(key: Any, value: Any?) = replace(named, NamedAddress(current.path, key), value)

    private fun <K> replace(storage: MutableMap<K, Any?>, key: K, value: Any?) {
        val previous = storage[key]
        if (previous !== value && previous is CompositionResource) {
            storage.remove(key)
            previous.dispose()
        }
        storage[key] = value
    }

    fun endPass() {
        check(frames.size == 1) { "Unbalanced composition groups" }
        val removedSlots = slots.filterKeys { it !in visitedSlots }
        val removedNamed = named.filterKeys { it.path !in visitedGroups }
        removedSlots.keys.forEach { slots.remove(it) }
        removedNamed.keys.forEach { named.remove(it) }
        disposeResources(removedSlots.values + removedNamed.values)
    }

    fun dispose() {
        val removed = slots.values.toList() + named.values.toList()
        slots.clear()
        named.clear()
        beginPass()
        disposeResources(removed)
    }

    private fun disposeResources(values: Collection<Any?>) {
        var failure: Throwable? = null
        values.filterIsInstance<CompositionResource>().forEach { resource ->
            try { resource.dispose() } catch (error: Throwable) {
                if (failure == null) failure = error else failure.addSuppressed(error)
            }
        }
        failure?.let { throw it }
    }
}
