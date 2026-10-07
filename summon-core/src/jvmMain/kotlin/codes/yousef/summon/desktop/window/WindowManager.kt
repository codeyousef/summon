@file:JvmName("WindowManagerJvm")

package codes.yousef.summon.desktop.window

/**
 * JVM implementation of WindowManager.
 *
 * Window management is not supported on JVM (server-side).
 * All operations return default values or no-op.
 */
actual object WindowManager {

    /** The null value. */
    actual val currentWindowId: String? = null

    /**
     * Opens the operation.
     *
     * @param url Target URL.
     * @param target The target value.
     * @param options The options value.
     * @return The resulting value.
     */
    actual fun open(
        url: String,
        target: String,
        options: WindowOptions
    ): WindowReference? {
        // Window opening is not supported on JVM
        println("WindowManager.open() is not supported on JVM")
        return null
    }

    /**
     * Returns screen info.
     *
     * @return The resulting value.
     */
    actual fun getScreenInfo(): ScreenInfo {
        // Return reasonable defaults for SSR contexts
        return ScreenInfo(
            width = 1920,
            height = 1080,
            availWidth = 1920,
            availHeight = 1040,
            colorDepth = 24,
            pixelDepth = 24,
            devicePixelRatio = 1.0
        )
    }

    /**
     * Returns current window bounds.
     *
     * @return The resulting value.
     */
    actual fun getCurrentWindowBounds(): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        return Pair(Pair(0, 0), Pair(800, 600))
    }

    /**
     * Executes the move to operation.
     *
     * @param x The x value.
     * @param y The y value.
     */
    actual fun moveTo(x: Int, y: Int) {
        // Not supported on JVM
    }

    /**
     * Executes the resize to operation.
     *
     * @param width The width value.
     * @param height The height value.
     */
    actual fun resizeTo(width: Int, height: Int) {
        // Not supported on JVM
    }

    /** Moves focus to this element. */
    actual fun focus() {
        // Not supported on JVM
    }

    /**
     * Executes the are popups likely blocked operation.
     *
     * @return The resulting value.
     */
    actual fun arePopupsLikelyBlocked(): Boolean {
        // On JVM, we can't create popups anyway
        return true
    }
}
