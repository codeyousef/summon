package code.yousef.summon.runtime

/**
 * JVM implementation backed by a ThreadLocal so each request/thread gets its own renderer.
 */
internal actual object PlatformRendererStore {
    private val threadLocal = ThreadLocal<PlatformRenderer?>()

    actual fun get(): PlatformRenderer? = threadLocal.get()

    actual fun set(renderer: PlatformRenderer?) {
        if (renderer == null) {
            threadLocal.remove()
        } else {
            threadLocal.set(renderer)
        }
    }

    actual fun clear() {
        threadLocal.remove()
    }
}
