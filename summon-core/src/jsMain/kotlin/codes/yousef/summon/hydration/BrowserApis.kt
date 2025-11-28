package codes.yousef.summon.hydration

import org.w3c.dom.Window

/**
 * External declarations for browser APIs used in hydration scheduling.
 *
 * These APIs enable non-blocking hydration by scheduling work during
 * browser idle periods or animation frames.
 */

/**
 * Represents the deadline object passed to requestIdleCallback callbacks.
 *
 * Provides information about how much time remains in the current idle period
 * and whether the callback is being invoked because the timeout expired.
 */
external interface IdleDeadline {
    /**
     * Returns the time remaining in the current idle period, in milliseconds.
     * Returns 0 if there is no more time remaining.
     */
    fun timeRemaining(): Double

    /**
     * Returns true if the callback is being invoked because the timeout expired,
     * rather than because the browser has idle time.
     */
    val didTimeout: Boolean
}

/**
 * Options for requestIdleCallback.
 */
external interface IdleRequestOptions {
    /**
     * Maximum time to wait before forcing the callback to run, in milliseconds.
     * If set, the callback will be invoked after this timeout even if the browser is busy.
     */
    var timeout: Int?
}

/**
 * Request that the browser call a callback during idle periods.
 *
 * This allows work to be scheduled without blocking user input or animations.
 * The callback receives an IdleDeadline object that indicates how much time
 * remains for the current idle period.
 *
 * @param callback Function to call during an idle period
 * @param options Optional configuration (e.g., timeout)
 * @return A handle that can be passed to cancelIdleCallback
 */
external fun requestIdleCallback(callback: (IdleDeadline) -> Unit, options: IdleRequestOptions = definedExternally): Int

/**
 * Cancel a previously scheduled idle callback.
 *
 * @param handle The handle returned by requestIdleCallback
 */
external fun cancelIdleCallback(handle: Int)

/**
 * Check if requestIdleCallback is available in the current browser.
 */
fun isIdleCallbackSupported(): Boolean = js("typeof requestIdleCallback !== 'undefined'") as Boolean

/**
 * Request that the browser call a callback before the next repaint.
 *
 * Used as a fallback when requestIdleCallback is not available.
 * Also used for DOM mutations to ensure they happen in the rendering pipeline.
 *
 * @param callback Function to call before the next repaint
 * @return A handle that can be passed to cancelAnimationFrame
 */
external fun requestAnimationFrame(callback: (Double) -> Unit): Int

/**
 * Cancel a previously scheduled animation frame callback.
 *
 * @param handle The handle returned by requestAnimationFrame
 */
external fun cancelAnimationFrame(handle: Int)

/**
 * Get the current high-resolution timestamp in milliseconds.
 */
fun performanceNow(): Double = js("performance.now()") as Double

/**
 * Helper to create IdleRequestOptions with a timeout.
 */
fun idleRequestOptions(timeout: Int): IdleRequestOptions {
    val options = js("{}").unsafeCast<IdleRequestOptions>()
    options.timeout = timeout
    return options
}
