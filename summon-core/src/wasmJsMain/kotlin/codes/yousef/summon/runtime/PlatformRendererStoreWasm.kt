package codes.yousef.summon.runtime

/**
 * WASM JS currently executes on the JS main thread, so reuse the JS implementation semantics.
 */
internal actual object PlatformRendererStore {
    private var renderer: PlatformRenderer? = null

    actual fun get(): PlatformRenderer? = renderer

    actual fun set(renderer: PlatformRenderer?) {
        PlatformRendererStore.renderer = renderer
    }

    actual fun clear() {
        renderer = null
    }
}
