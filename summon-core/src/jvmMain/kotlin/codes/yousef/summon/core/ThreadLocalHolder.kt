package codes.yousef.summon.core

/**
 * JVM implementation of ThreadLocalHolder using Java's ThreadLocal.
 */
actual class ThreadLocalHolder<T> {
    private val threadLocal = java.lang.ThreadLocal<T>()

    /**
     * Returns the operation.
     *
     * @return The resulting value.
     */
    actual fun get(): T? = threadLocal.get()

    /**
     * Sets the operation.
     *
     * @param value Value to process.
     */
    actual fun set(value: T?) {
        if (value == null) {
            threadLocal.remove()
        } else {
            threadLocal.set(value)
        }
    }
} 