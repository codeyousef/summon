/**
 * JVM implementations of animation extensions.
 */
package code.yousef.summon.animation

import kotlinx.coroutines.delay as coroutinesDelay

/**
 * Starts an animation with the specified duration.
 * JVM implementation that calls the platform-specific startAnimation method.
 *
 * @param durationMs The duration of the animation in milliseconds
 */
actual fun AnimationController.startAnimation(durationMs: Int) {
    // Call the JVM-specific startAnimation method
    this.startAnimation(durationMs.toLong())
}

/**
 * Helper function to delay execution for a specified time.
 * JVM implementation that uses kotlinx.coroutines.delay.
 *
 * @param timeMillis The time to delay in milliseconds
 */
actual suspend fun delay(timeMillis: Long) {
    coroutinesDelay(timeMillis)
}