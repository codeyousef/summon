package code.yousef.summon.runtime

import code.yousef.summon.state.mutableStateOf

/**
 * Marks a composable function as skippable if all parameters are equal.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Skippable

/**
 * Marks a class as stable for recomposition purposes.
 * Stable classes use structural equality for comparison.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Stable

/**
 * Marks a class as immutable for recomposition purposes.
 * Immutable classes are always considered stable.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Immutable

/**
 * Extension to the Composer for advanced recomposition features.
 */
fun Composer.startRestartableGroup(key: String) {
    // Start a new group that can be restarted independently
    startGroup(key.hashCode())
}

fun Composer.endRestartableGroup() {
    endGroup()
}

/**
 * Create a unique key for the current composition.
 */
fun Composer.key(key: Any?, block: () -> Unit) {
    startGroup(key.hashCode())
    try {
        block()
    } finally {
        endGroup()
    }
}

/**
 * List implementation that tracks modifications for recomposition.
 */
fun <T> mutableStateListOf(vararg elements: T): MutableList<T> {
    return SnapshotMutableList<T>().apply {
        addAll(elements)
    }
}

private class SnapshotMutableList<T> : MutableList<T> {
    private val list = mutableListOf<T>()
    private val modification = mutableStateOf(0)

    override val size: Int get() = list.size

    override fun contains(element: T): Boolean = list.contains(element)
    override fun containsAll(elements: Collection<T>): Boolean = list.containsAll(elements)
    override fun get(index: Int): T = list[index]
    override fun indexOf(element: T): Int = list.indexOf(element)
    override fun isEmpty(): Boolean = list.isEmpty()
    override fun iterator(): MutableIterator<T> = list.iterator()
    override fun lastIndexOf(element: T): Int = list.lastIndexOf(element)
    override fun add(element: T): Boolean {
        return list.add(element).also {
            modification.value++
        }
    }

    override fun add(index: Int, element: T) {
        list.add(index, element)
        modification.value++
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        return list.addAll(index, elements).also {
            modification.value++
        }
    }

    override fun addAll(elements: Collection<T>): Boolean {
        return list.addAll(elements).also {
            modification.value++
        }
    }

    override fun clear() {
        list.clear()
        modification.value++
    }

    override fun listIterator(): MutableListIterator<T> = list.listIterator()
    override fun listIterator(index: Int): MutableListIterator<T> = list.listIterator(index)
    override fun remove(element: T): Boolean {
        return list.remove(element).also {
            if (it) modification.value++
        }
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        return list.removeAll(elements).also {
            if (it) modification.value++
        }
    }

    override fun removeAt(index: Int): T {
        return list.removeAt(index).also {
            modification.value++
        }
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        return list.retainAll(elements).also {
            if (it) modification.value++
        }
    }

    override fun set(index: Int, element: T): T {
        return list.set(index, element).also {
            modification.value++
        }
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        return list.subList(fromIndex, toIndex)
    }

    fun forEachIndexed(action: (index: Int, T) -> Unit) {
        list.forEachIndexed(action)
    }
}