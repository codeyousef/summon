package codes.yousef.summon.hydration

import kotlin.js.JsAny
import kotlin.js.JsNumber

/**
 * External declarations for browser APIs used in hydration scheduling (WASM version).
 *
 * These APIs enable non-blocking hydration by scheduling work during
 * browser idle periods or animation frames.
 *
 * Note: WASM requires different external declaration patterns than JS.
 */

/**
 * Represents the deadline object passed to requestIdleCallback callbacks.
 */
external interface IdleDeadline : JsAny {
    /**
     * Returns the time remaining in the current idle period, in milliseconds.
     */
    fun timeRemaining(): JsNumber

    /**
     * Returns true if the callback is being invoked because the timeout expired.
     */
    val didTimeout: Boolean
}

/**
 * Get time remaining from IdleDeadline as a Double.
 */
fun IdleDeadline.timeRemainingDouble(): Double = timeRemaining().toDouble()

/**
 * Check if requestIdleCallback is available.
 */
@JsFun("() => typeof requestIdleCallback !== 'undefined'")
/**
 * Executes the WASM is idle callback supported operation.
 *
 * @return The resulting value.
 */
external fun wasmIsIdleCallbackSupported(): Boolean

/**
 * Request idle callback with optional timeout.
 */
@JsFun("(callback, timeout) => { const opts = timeout > 0 ? { timeout: timeout } : undefined; return window.requestIdleCallback ? window.requestIdleCallback(callback, opts) : window.setTimeout(() => callback({ timeRemaining: () => 50, didTimeout: false }), 1); }")
/**
 * Executes the WASM request idle callback operation.
 *
 * @param callback The callback value.
 * @param timeout Timeout in milliseconds.
 * @return The resulting value.
 */
external fun wasmRequestIdleCallback(callback: (IdleDeadline) -> Unit, timeout: Int): Int

/**
 * Cancel idle callback.
 */
@JsFun("(handle) => { if (window.cancelIdleCallback) { window.cancelIdleCallback(handle); } else { window.clearTimeout(handle); } }")
/**
 * Executes the WASM cancel idle callback operation.
 *
 * @param handle The handle value.
 */
external fun wasmCancelIdleCallback(handle: Int)

/**
 * Request animation frame.
 */
@JsFun("(callback) => window.requestAnimationFrame(callback)")
/**
 * Executes the WASM request animation frame operation.
 *
 * @param callback The callback value.
 * @return The resulting value.
 */
external fun wasmRequestAnimationFrame(callback: (JsNumber) -> Unit): Int

/**
 * Cancel animation frame.
 */
@JsFun("(handle) => window.cancelAnimationFrame(handle)")
/**
 * Executes the WASM cancel animation frame operation.
 *
 * @param handle The handle value.
 */
external fun wasmCancelAnimationFrame(handle: Int)

/**
 * Get current performance timestamp.
 */
@JsFun("() => performance.now()")
/**
 * Executes the WASM performance now operation.
 *
 * @return The resulting value.
 */
external fun wasmPerformanceNow(): Double

/**
 * Helper functions for WASM platform.
 */
fun isIdleCallbackSupported(): Boolean = wasmIsIdleCallbackSupported()

/**
 * Executes the performance now operation.
 *
 * @return The resulting value.
 */
fun performanceNow(): Double = wasmPerformanceNow()

/**
 * Add a passive scroll listener to the window.
 */
@JsFun("(callback) => window.addEventListener('scroll', callback, { passive: true })")
/**
 * Executes the WASM add scroll listener operation.
 *
 * @param callback The callback value.
 */
external fun wasmAddScrollListener(callback: () -> Unit)
