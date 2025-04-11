package code.yousef.summon.effects

import kotlin.js.Date

/**
 * JavaScript implementation of getCurrentTimeMillis using Date.now()
 */
actual fun getCurrentTimeMillis(): Long {
    return Date().getTime().toLong()
}

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
} 