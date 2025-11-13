package codes.yousef.summon.security

/**
 * JS implementation of SecurityContextHolder using a simple mutable variable.
 * Note: JavaScript in the browser is single-threaded, so a simple variable suffices.
 * If targeting environments like Node.js with worker threads, a more sophisticated solution might be needed.
 */
internal actual object SecurityContextHolder {
    private var holder: Authentication? = null

    actual fun get(): Authentication? {
        return holder
    }

    actual fun set(value: Authentication?) {
        holder = value
    }

    actual fun remove() {
        holder = null
    }
} 
