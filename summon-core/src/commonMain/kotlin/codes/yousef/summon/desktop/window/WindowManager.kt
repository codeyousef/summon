/**
 * # Window Management
 *
 * Multi-window support for Summon applications in browser environments.
 * Provides APIs for opening, managing, and communicating between browser windows.
 *
 * ## Features
 *
 * - **Open Windows**: Create new browser windows/tabs programmatically
 * - **Window State**: Track and modify window position, size, and state
 * - **Screen Info**: Access screen dimensions and multi-monitor information
 * - **Focus Management**: Control window focus and activation
 *
 * ## Usage Examples
 *
 * ### Opening a New Window
 *
 * ```kotlin
 * val window = WindowManager.open(
 *     url = "/settings",
 *     options = WindowOptions(
 *         width = 800,
 *         height = 600,
 *         title = "Settings"
 *     )
 * )
 *
 * // Close it later
 * window?.close()
 * ```
 *
 * ### Getting Screen Information
 *
 * ```kotlin
 * val screenInfo = WindowManager.getScreenInfo()
 * println("Screen: ${screenInfo.width}x${screenInfo.height}")
 * println("Available: ${screenInfo.availWidth}x${screenInfo.availHeight}")
 * ```
 *
 * ## Platform Support
 *
 * | Feature | JS | WASM | JVM |
 * |---------|-----|------|-----|
 * | open() | window.open() | window.open() | Not supported |
 * | close() | window.close() | window.close() | Not supported |
 * | focus() | window.focus() | window.focus() | Not supported |
 * | Screen info | screen API | screen API | Returns defaults |
 *
 * @since 0.7.0
 */
package codes.yousef.summon.desktop.window

/**
 * Configuration options for opening a new window.
 */
data class WindowOptions(
    /** Width of the window in pixels */
    val width: Int? = null,
    /** Height of the window in pixels */
    val height: Int? = null,
    /** Left position of the window */
    val left: Int? = null,
    /** Top position of the window */
    val top: Int? = null,
    /** Window title (for popups) */
    val title: String? = null,
    /** Whether to show the menu bar */
    val menubar: Boolean = false,
    /** Whether to show the toolbar */
    val toolbar: Boolean = false,
    /** Whether to show the location/address bar */
    val location: Boolean = false,
    /** Whether to show the status bar */
    val status: Boolean = false,
    /** Whether the window is resizable */
    val resizable: Boolean = true,
    /** Whether to show scrollbars */
    val scrollbars: Boolean = true
)

/**
 * Represents information about the user's screen.
 */
data class ScreenInfo(
    /** Total screen width in pixels */
    val width: Int,
    /** Total screen height in pixels */
    val height: Int,
    /** Available screen width (excluding taskbars, etc.) */
    val availWidth: Int,
    /** Available screen height */
    val availHeight: Int,
    /** Color depth in bits per pixel */
    val colorDepth: Int,
    /** Pixel depth */
    val pixelDepth: Int,
    /** Device pixel ratio (for high-DPI displays) */
    val devicePixelRatio: Double
)

/**
 * Represents a reference to an opened window.
 */
interface WindowReference {
    /**
     * Closes this window.
     */
    fun close()

    /**
     * Brings this window to the front and gives it focus.
     */
    fun focus()

    /**
     * Checks if this window is still open.
     */
    fun isClosed(): Boolean

    /**
     * Gets the current location (URL) of the window.
     */
    fun getLocation(): String?

    /**
     * Navigates this window to a new URL.
     */
    fun navigate(url: String)

    /**
     * Posts a message to this window (for cross-window communication).
     *
     * @param message The message to send
     * @param targetOrigin The origin that should receive the message (use "*" for any)
     */
    fun postMessage(message: String, targetOrigin: String = "*")
}

/**
 * Manager for multi-window operations.
 */
expect object WindowManager {
    /**
     * A unique identifier for the current window/tab.
     * Generated on first access and stored in sessionStorage.
     */
    val currentWindowId: String?

    /**
     * Opens a new browser window or tab.
     *
     * @param url The URL to open (can be a relative path)
     * @param target Window name/target (_blank for new window, _self for current)
     * @param options Configuration options for the window
     * @return A WindowReference to the opened window, or null if blocked
     */
    fun open(
        url: String,
        target: String = "_blank",
        options: WindowOptions = WindowOptions()
    ): WindowReference?

    /**
     * Gets information about the user's screen.
     *
     * @return ScreenInfo with screen dimensions and capabilities
     */
    fun getScreenInfo(): ScreenInfo

    /**
     * Gets the current window's position and size.
     *
     * @return A pair of (x, y) position and (width, height) size
     */
    fun getCurrentWindowBounds(): Pair<Pair<Int, Int>, Pair<Int, Int>>

    /**
     * Moves the current window to the specified position.
     * Note: May be blocked by browser security restrictions.
     */
    fun moveTo(x: Int, y: Int)

    /**
     * Resizes the current window to the specified dimensions.
     * Note: May be blocked by browser security restrictions.
     */
    fun resizeTo(width: Int, height: Int)

    /**
     * Focuses the current window.
     */
    fun focus()

    /**
     * Checks if popup windows are likely blocked by the browser.
     * This is a heuristic and may not be 100% accurate.
     */
    fun arePopupsLikelyBlocked(): Boolean
}
