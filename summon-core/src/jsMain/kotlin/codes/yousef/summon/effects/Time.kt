package codes.yousef.summon.effects

import kotlin.js.Date

/**
 * JavaScript implementation of currentTimeMillis using Date.now()
 */
actual fun currentTimeMillis(): Long {
    return Date().getTime().toLong()
}

/**
 * Sets timeout.
 *
 * @param delayMs The delay ms value.
 * @param callback The callback value.
 * @return The resulting value.
 */
actual fun setTimeout(delayMs: Int, callback: () -> Unit): Int {
    return window.setTimeout(callback, delayMs)
}

/**
 * Clears timeout.
 *
 * @param id Stable identifier.
 */
actual fun clearTimeout(id: Int) {
    window.clearTimeout(id)
}

/**
 * JavaScript window global object
 */
external object window {
    /**
     * Sets timeout.
     *
     * @param handler The handler value.
     * @param timeout Timeout in milliseconds.
     * @return The resulting value.
     */
    fun setTimeout(handler: () -> Unit, timeout: Int): Int
    /**
     * Clears timeout.
     *
     * @param timeoutId The timeout id value.
     */
    fun clearTimeout(timeoutId: Int)
    /**
     * Adds event listener.
     *
     * @param type The type value.
     * @param listener The listener value.
     */
    fun addEventListener(type: String, listener: (org.w3c.dom.events.Event) -> Unit)
    /**
     * Removes event listener.
     *
     * @param type The type value.
     * @param listener The listener value.
     */
    fun removeEventListener(type: String, listener: (org.w3c.dom.events.Event) -> Unit)
} 