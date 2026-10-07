package codes.yousef.summon.desktop.window

import codes.yousef.summon.runtime.wasmConsoleError

/**
 * External declarations for Window APIs in WASM.
 */
@JsName("window")
/** Provides wasm window object operations. */
external object WasmWindowObject : JsAny {
    /**
     * Opens the operation.
     *
     * @param url Target URL.
     * @param target The target value.
     * @param features The features value.
     * @return The resulting value.
     */
    fun open(url: String, target: String, features: String): JsAny?
    /** Moves focus to this element. */
    fun focus()
    /**
     * Executes the move to operation.
     *
     * @param x The x value.
     * @param y The y value.
     */
    fun moveTo(x: Int, y: Int)
    /**
     * Executes the resize to operation.
     *
     * @param width The width value.
     * @param height The height value.
     */
    fun resizeTo(width: Int, height: Int)
    /** The property declaration value. */
    val screenX: Int
    /** The property declaration value. */
    val screenY: Int
    /** The property declaration value. */
    val outerWidth: Int
    /** The property declaration value. */
    val outerHeight: Int
    /** The property declaration value. */
    val devicePixelRatio: Double
    /** The property declaration value. */
    val screen: WasmScreen
}

/** Contract for WASM screen. */
external interface WasmScreen : JsAny {
    /** The property declaration value. */
    val width: Int
    /** The property declaration value. */
    val height: Int
    /** The property declaration value. */
    val availWidth: Int
    /** The property declaration value. */
    val availHeight: Int
    /** The property declaration value. */
    val colorDepth: Int
    /** The property declaration value. */
    val pixelDepth: Int
}

/** Contract for WASM opened window. */
external interface WasmOpenedWindow : JsAny {
    /** Closes the operation. */
    fun close()
    /** Moves focus to this element. */
    fun focus()
    /** The property declaration value. */
    val closed: Boolean
    /**
     * Executes the post message operation.
     *
     * @param message Message content.
     * @param targetOrigin The target origin value.
     */
    fun postMessage(message: JsAny?, targetOrigin: String)
}

/**
 * Returns window href.
 *
 * @param window The window value.
 * @return The resulting value.
 */
@JsFun("(w) => w.location ? w.location.href : null")
/**
 * Returns window href.
 *
 * @param window The window value.
 * @return The resulting value.
 */
external fun getWindowHref(window: JsAny): String?

/**
 * Sets window href.
 *
 * @param window The window value.
 * @param url Target URL.
 */
@JsFun("(w, url) => { if (w.location) w.location.href = url; }")
/**
 * Sets window href.
 *
 * @param window The window value.
 * @param url Target URL.
 */
external fun setWindowHref(window: JsAny, url: String)

/**
 * Executes the str to JS operation.
 *
 * @param str The str value.
 * @return The resulting value.
 */
@JsFun("(str) => str")
/**
 * Executes the str to js operation.
 *
 * @param str The str value.
 * @return The resulting value.
 */
external fun strToJs(str: String): JsAny

/**
 * Returns session storage item.
 *
 * @param key Lookup key.
 * @return The resulting value.
 */
@JsFun("(key) => window.sessionStorage ? window.sessionStorage.getItem(key) : null")
/**
 * Returns session storage item.
 *
 * @param key Lookup key.
 * @return The resulting value.
 */
external fun getSessionStorageItem(key: String): String?

/**
 * Sets session storage item.
 *
 * @param key Lookup key.
 * @param value Value to process.
 */
@JsFun("(key, value) => { if (window.sessionStorage) window.sessionStorage.setItem(key, value); }")
/**
 * Sets session storage item.
 *
 * @param key Lookup key.
 * @param value Value to process.
 */
external fun setSessionStorageItem(key: String, value: String)

/**
 * Executes the JS date now operation.
 *
 * @return The resulting value.
 */
@JsFun("() => Date.now()")
/**
 * Executes the js date now operation.
 *
 * @return The resulting value.
 */
external fun jsDateNow(): Double

/**
 * Executes the JS math random operation.
 *
 * @return The resulting value.
 */
@JsFun("() => Math.random()")
/**
 * Executes the js math random operation.
 *
 * @return The resulting value.
 */
external fun jsMathRandom(): Double

/**
 * WASM implementation of WindowManager.
 */
actual object WindowManager {

    private var _windowId: String? = null

    /** The property declaration value. */
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
        return try {
            val features = buildFeatures(options)
            val openedWindow = WasmWindowObject.open(url, target, features)
            openedWindow?.let { WasmWindowReference(it) }
        } catch (e: Exception) {
            wasmConsoleError("Failed to open window: ${e.message}")
            null
        }
    }

    /**
     * Returns screen info.
     *
     * @return The resulting value.
     */
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

    /**
     * Returns current window bounds.
     *
     * @return The resulting value.
     */
    actual fun getCurrentWindowBounds(): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        return try {
            val position = Pair(WasmWindowObject.screenX, WasmWindowObject.screenY)
            val size = Pair(WasmWindowObject.outerWidth, WasmWindowObject.outerHeight)
            Pair(position, size)
        } catch (e: Exception) {
            Pair(Pair(0, 0), Pair(800, 600))
        }
    }

    /**
     * Executes the move to operation.
     *
     * @param x The x value.
     * @param y The y value.
     */
    actual fun moveTo(x: Int, y: Int) {
        try {
            WasmWindowObject.moveTo(x, y)
        } catch (e: Exception) {
            wasmConsoleError("Failed to move window: ${e.message}")
        }
    }

    /**
     * Executes the resize to operation.
     *
     * @param width The width value.
     * @param height The height value.
     */
    actual fun resizeTo(width: Int, height: Int) {
        try {
            WasmWindowObject.resizeTo(width, height)
        } catch (e: Exception) {
            wasmConsoleError("Failed to resize window: ${e.message}")
        }
    }

    /** Moves focus to this element. */
    actual fun focus() {
        try {
            WasmWindowObject.focus()
        } catch (e: Exception) {
            wasmConsoleError("Failed to focus window: ${e.message}")
        }
    }

    /**
     * Executes the are popups likely blocked operation.
     *
     * @return The resulting value.
     */
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
