/**
 * JS implementations of animation extensions.
 */
package codes.yousef.summon.animation

import kotlin.coroutines.suspendCoroutine

/**
 * Starts an animation with the specified duration.
 * JS implementation that calls the window.animationController.start method.
 *
 * @param durationMs The duration of the animation in milliseconds
 */
actual fun AnimationController.startAnimation(durationMs: Int) {
    // Call the JS-specific start method on window.animationController
    js("if (window.animationController) window.animationController.start(durationMs)")
}

/**
 * Helper function to delay execution for a specified time.
 * JS implementation that uses setTimeout.
 *
 * @param timeMillis The time to delay in milliseconds
 */
actual suspend fun delay(timeMillis: Long) = suspendCoroutine<Unit> { continuation ->
    js("setTimeout(function() { continuation.resume(null); }, timeMillis)")
}

// Helper function to use JS setTimeout
private fun setTimeout(callback: () -> Unit, delay: Long) {
    js("setTimeout(callback, delay)")
}