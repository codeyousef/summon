package codes.yousef.summon.desktop.window

import kotlinx.browser.window
import org.w3c.dom.Window

/**
 * JS implementation of WindowManager using browser window APIs.
 */
actual object WindowManager {

    actual val currentWindowId: String? by lazy {
        try {
            // Try to get existing ID from sessionStorage
            val existing = window.sessionStorage.getItem("summon-window-id")
            if (existing != null) {
                existing
            } else {
                // Generate a new unique ID
                val newId = "win-${js("Date.now()")}-${(js("Math.random()") as Double * 1000000).toInt()}"
                window.sessionStorage.setItem("summon-window-id", newId)
                newId
            }
        } catch (e: Exception) {
            // SessionStorage not available
            null
        }
    }

    actual fun open(
        url: String,
        target: String,
        options: WindowOptions
    ): WindowReference? {
        val features = buildFeatures(options)
        val openedWindow = window.open(url, target, features)
        return openedWindow?.let { JsWindowReference(it) }
    }

    actual fun getScreenInfo(): ScreenInfo {
        val screen = window.screen
        return ScreenInfo(
            width = screen.width,
            height = screen.height,
            availWidth = screen.availWidth,
            availHeight = screen.availHeight,
            colorDepth = screen.colorDepth,
            pixelDepth = screen.pixelDepth,
            devicePixelRatio = window.devicePixelRatio
        )
    }

    actual fun getCurrentWindowBounds(): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val position = Pair(window.screenX, window.screenY)
        val size = Pair(window.outerWidth, window.outerHeight)
        return Pair(position, size)
    }

    actual fun moveTo(x: Int, y: Int) {
        window.moveTo(x, y)
    }

    actual fun resizeTo(width: Int, height: Int) {
        window.resizeTo(width, height)
    }

    actual fun focus() {
        window.focus()
    }

    actual fun arePopupsLikelyBlocked(): Boolean {
        // Try to open a small popup and check if it was blocked
        val testWindow = window.open("", "_blank", "width=1,height=1")
        return if (testWindow == null) {
            true
        } else {
            testWindow.close()
            false
        }
    }

    private fun buildFeatures(options: WindowOptions): String {
        val features = mutableListOf<String>()

        options.width?.let { features.add("width=$it") }
        options.height?.let { features.add("height=$it") }
        options.left?.let { features.add("left=$it") }
        options.top?.let { features.add("top=$it") }
        features.add("menubar=${if (options.menubar) "yes" else "no"}")
        features.add("toolbar=${if (options.toolbar) "yes" else "no"}")
        features.add("location=${if (options.location) "yes" else "no"}")
        features.add("status=${if (options.status) "yes" else "no"}")
        features.add("resizable=${if (options.resizable) "yes" else "no"}")
        features.add("scrollbars=${if (options.scrollbars) "yes" else "no"}")

        return features.joinToString(",")
    }
}

/**
 * JS implementation of WindowReference.
 */
private class JsWindowReference(private val jsWindow: Window) : WindowReference {

    override fun close() {
        jsWindow.close()
    }

    override fun focus() {
        jsWindow.focus()
    }

    override fun isClosed(): Boolean {
        return jsWindow.closed
    }

    override fun getLocation(): String? {
        return try {
            jsWindow.location.href
        } catch (e: Exception) {
            // Cross-origin access blocked
            null
        }
    }

    override fun navigate(url: String) {
        try {
            jsWindow.location.href = url
        } catch (e: Exception) {
            // May fail for cross-origin windows
            console.error("Failed to navigate window: ${e.message}")
        }
    }

    override fun postMessage(message: String, targetOrigin: String) {
        jsWindow.postMessage(message, targetOrigin)
    }
}
