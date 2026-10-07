package codes.yousef.summon.core

/**
 * JS implementation of ThreadLocalHolder.
 *
 * Since JS is single-threaded, we can use a simple global variable.
 * In a more complex scenario, we might want to use WeakMap or a different approach.
 */
actual class ThreadLocalHolder<T> {
    private var value: T? = null

    /**
     * Returns the operation.
     *
     * @return The resulting value.
     */
    actual fun get(): T? = value

    /**
     * Sets the operation.
     *
     * @param value Value to process.
     */
    actual fun set(value: T?) {
        this.value = value
    }
} 