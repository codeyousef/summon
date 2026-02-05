@file:JvmName("WindowManagerJvm")

package codes.yousef.summon.desktop.window

/**
 * JVM implementation of WindowManager.
 *
 * Window management is not supported on JVM (server-side).
 * All operations return default values or no-op.
 */
actual object WindowManager {

    actual val currentWindowId: String? = null

    actual fun open(
        url: String,
        target: String,
        options: WindowOptions
    ): WindowReference? {
        // Window opening is not supported on JVM
        println("WindowManager.open() is not supported on JVM")
        return null
    }

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

    actual fun getCurrentWindowBounds(): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        return Pair(Pair(0, 0), Pair(800, 600))
    }

    actual fun moveTo(x: Int, y: Int) {
        // Not supported on JVM
    }

    actual fun resizeTo(width: Int, height: Int) {
        // Not supported on JVM
    }

    actual fun focus() {
        // Not supported on JVM
    }

    actual fun arePopupsLikelyBlocked(): Boolean {
        // On JVM, we can't create popups anyway
        return true
    }
}
