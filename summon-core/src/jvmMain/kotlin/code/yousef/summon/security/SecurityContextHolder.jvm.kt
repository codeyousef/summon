package codes.yousef.summon.security

// import codes.yousef.summon.runtime.PlatformRendererProvider // Remove unused import

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
