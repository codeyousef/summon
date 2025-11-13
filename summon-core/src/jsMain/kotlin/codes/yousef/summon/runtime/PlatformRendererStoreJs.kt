package codes.yousef.summon.runtime

/**
 * JavaScript runtime executes on a single thread, so we can store the renderer globally.
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
