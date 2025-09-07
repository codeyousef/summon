package code.yousef.summon.effects

import kotlin.js.Date

/**
 * JavaScript implementation of currentTimeMillis using Date.now()
 */
actual fun currentTimeMillis(): Long {
    return Date().getTime().toLong()
}

actual fun setTimeout(delayMs: Int, callback: () -> Unit): Int {
    return window.setTimeout(callback, delayMs)
}

actual fun clearTimeout(id: Int) {
    window.clearTimeout(id)
}

/**
 * JavaScript window global object
 */
external object window {
    fun setTimeout(handler: () -> Unit, timeout: Int): Int
    fun clearTimeout(timeoutId: Int)
    fun addEventListener(type: String, listener: (org.w3c.dom.events.Event) -> Unit)
    fun removeEventListener(type: String, listener: (org.w3c.dom.events.Event) -> Unit)
} 