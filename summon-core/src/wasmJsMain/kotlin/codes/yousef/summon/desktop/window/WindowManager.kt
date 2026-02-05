package codes.yousef.summon.desktop.window

import codes.yousef.summon.runtime.wasmConsoleError

/**
 * External declarations for Window APIs in WASM.
 */
@JsName("window")
external object WasmWindowObject : JsAny {
    fun open(url: String, target: String, features: String): JsAny?
    fun focus()
    fun moveTo(x: Int, y: Int)
    fun resizeTo(width: Int, height: Int)
    val screenX: Int
    val screenY: Int
    val outerWidth: Int
    val outerHeight: Int
    val devicePixelRatio: Double
    val screen: WasmScreen
}

external interface WasmScreen : JsAny {
    val width: Int
    val height: Int
    val availWidth: Int
    val availHeight: Int
    val colorDepth: Int
    val pixelDepth: Int
}

external interface WasmOpenedWindow : JsAny {
    fun close()
    fun focus()
    val closed: Boolean
    fun postMessage(message: JsAny?, targetOrigin: String)
}

@JsFun("(w) => w.location ? w.location.href : null")
external fun getWindowHref(window: JsAny): String?

@JsFun("(w, url) => { if (w.location) w.location.href = url; }")
external fun setWindowHref(window: JsAny, url: String)

@JsFun("(str) => str")
external fun strToJs(str: String): JsAny

@JsFun("(key) => window.sessionStorage ? window.sessionStorage.getItem(key) : null")
external fun getSessionStorageItem(key: String): String?

@JsFun("(key, value) => { if (window.sessionStorage) window.sessionStorage.setItem(key, value); }")
external fun setSessionStorageItem(key: String, value: String)

@JsFun("() => Date.now()")
external fun jsDateNow(): Double

@JsFun("() => Math.random()")
external fun jsMathRandom(): Double

/**
 * WASM implementation of WindowManager.
 */
actual object WindowManager {

    private var _windowId: String? = null

    actual val currentWindowId: String?
        get() {
            if (_windowId != null) return _windowId
            return try {
                val existing = getSessionStorageItem("summon-window-id")
                if (existing != null) {
                    _windowId = existing
                    existing
                } else {
                    val newId = "win-${jsDateNow().toLong()}-${(jsMathRandom() * 1000000).toInt()}"
                    setSessionStorageItem("summon-window-id", newId)
                    _windowId = newId
                    newId
                }
            } catch (e: Exception) {
                null
            }
        }

    actual fun open(
        url: String,
        target: String,
        options: WindowOptions
    ): WindowReference? {
        return try {
            val features = buildFeatures(options)
            val openedWindow = WasmWindowObject.open(url, target, features)
            openedWindow?.let { WasmWindowReference(it) }
        } catch (e: Exception) {
            wasmConsoleError("Failed to open window: ${e.message}")
            null
        }
    }

    actual fun getScreenInfo(): ScreenInfo {
        return try {
            val screen = WasmWindowObject.screen
            ScreenInfo(
                width = screen.width,
                height = screen.height,
                availWidth = screen.availWidth,
                availHeight = screen.availHeight,
                colorDepth = screen.colorDepth,
                pixelDepth = screen.pixelDepth,
                devicePixelRatio = WasmWindowObject.devicePixelRatio
            )
        } catch (e: Exception) {
            // Return default values if screen API not available
            ScreenInfo(1920, 1080, 1920, 1040, 24, 24, 1.0)
        }
    }

    actual fun getCurrentWindowBounds(): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        return try {
            val position = Pair(WasmWindowObject.screenX, WasmWindowObject.screenY)
            val size = Pair(WasmWindowObject.outerWidth, WasmWindowObject.outerHeight)
            Pair(position, size)
        } catch (e: Exception) {
            Pair(Pair(0, 0), Pair(800, 600))
        }
    }

    actual fun moveTo(x: Int, y: Int) {
        try {
            WasmWindowObject.moveTo(x, y)
        } catch (e: Exception) {
            wasmConsoleError("Failed to move window: ${e.message}")
        }
    }

    actual fun resizeTo(width: Int, height: Int) {
        try {
            WasmWindowObject.resizeTo(width, height)
        } catch (e: Exception) {
            wasmConsoleError("Failed to resize window: ${e.message}")
        }
    }

    actual fun focus() {
        try {
            WasmWindowObject.focus()
        } catch (e: Exception) {
            wasmConsoleError("Failed to focus window: ${e.message}")
        }
    }

    actual fun arePopupsLikelyBlocked(): Boolean {
        return try {
            val testWindow = WasmWindowObject.open("", "_blank", "width=1,height=1")
            if (testWindow == null) {
                true
            } else {
                testWindow.unsafeCast<WasmOpenedWindow>().close()
                false
            }
        } catch (e: Exception) {
            true
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
 * WASM implementation of WindowReference.
 */
private class WasmWindowReference(private val jsWindow: JsAny) : WindowReference {

    override fun close() {
        try {
            jsWindow.unsafeCast<WasmOpenedWindow>().close()
        } catch (e: Exception) {
            wasmConsoleError("Failed to close window: ${e.message}")
        }
    }

    override fun focus() {
        try {
            jsWindow.unsafeCast<WasmOpenedWindow>().focus()
        } catch (e: Exception) {
            wasmConsoleError("Failed to focus window: ${e.message}")
        }
    }

    override fun isClosed(): Boolean {
        return try {
            jsWindow.unsafeCast<WasmOpenedWindow>().closed
        } catch (e: Exception) {
            true
        }
    }

    override fun getLocation(): String? {
        return try {
            getWindowHref(jsWindow)
        } catch (e: Exception) {
            null
        }
    }

    override fun navigate(url: String) {
        try {
            setWindowHref(jsWindow, url)
        } catch (e: Exception) {
            wasmConsoleError("Failed to navigate window: ${e.message}")
        }
    }

    override fun postMessage(message: String, targetOrigin: String) {
        try {
            jsWindow.unsafeCast<WasmOpenedWindow>().postMessage(strToJs(message), targetOrigin)
        } catch (e: Exception) {
            wasmConsoleError("Failed to post message: ${e.message}")
        }
    }
}
