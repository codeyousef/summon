package codes.yousef.summon.security

/** Provides security context holder operations. */
actual object SecurityContextHolder {
    private var _authentication: Authentication? = null

    /**
     * Returns the operation.
     *
     * @return The resulting value.
     */
    actual fun get(): Authentication? {
        return _authentication
    }

    /**
     * Sets the operation.
     *
     * @param value Value to process.
     */
    actual fun set(value: Authentication?) {
        _authentication = value
    }

    /** Removes the operation. */
    actual fun remove() {
        _authentication = null
    }
}
