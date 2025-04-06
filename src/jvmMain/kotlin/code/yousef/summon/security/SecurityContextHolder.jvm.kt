package code.yousef.summon.security

import java.lang.ThreadLocal

/**
 * JVM implementation of SecurityContextHolder using java.lang.ThreadLocal.
 */
internal actual object SecurityContextHolder {
    private val holder = ThreadLocal<Authentication?>()

    actual fun get(): Authentication? {
        return holder.get()
    }

    actual fun set(value: Authentication?) {
        holder.set(value)
    }

    actual fun remove() {
        holder.remove()
    }
} 