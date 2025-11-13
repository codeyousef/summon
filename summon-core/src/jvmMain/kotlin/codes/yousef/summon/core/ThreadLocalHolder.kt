package codes.yousef.summon.core

/**
 * JVM implementation of ThreadLocalHolder using Java's ThreadLocal.
 */
actual class ThreadLocalHolder<T> {
    private val threadLocal = java.lang.ThreadLocal<T>()

    actual fun get(): T? = threadLocal.get()

    actual fun set(value: T?) {
        if (value == null) {
            threadLocal.remove()
        } else {
            threadLocal.set(value)
        }
    }
} 