package codes.yousef.summon.runtime

/**
 * Platform specific storage for the current [PlatformRenderer] instance.
 *
 * On multi-threaded runtimes (JVM) this needs to be thread-local to avoid
 * leaking renderers across requests. Single-threaded runtimes (JS/WASM) can
 * keep a single global instance.
 */
internal expect object PlatformRendererStore {
    /**
     * Returns the renderer currently associated with the active execution context.
     */
    fun get(): PlatformRenderer?

    /**
     * Associates the provided renderer with the active execution context.
     */
    fun set(renderer: PlatformRenderer?)

    /**
     * Clears the renderer for the active execution context.
     */
    fun clear()
}
