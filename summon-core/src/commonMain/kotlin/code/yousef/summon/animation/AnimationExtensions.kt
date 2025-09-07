/**
 * Extensions for the animation system to provide cross-platform functionality.
 */
package code.yousef.summon.animation

/**
 * Starts an animation with the specified duration.
 * This extension function works on both JVM and JS platforms.
 *
 * @param durationMs The duration of the animation in milliseconds
 */
expect fun AnimationController.startAnimation(durationMs: Int)

/**
 * Helper function to delay execution for a specified time.
 * This is useful for animations and other time-based operations.
 *
 * @param timeMillis The time to delay in milliseconds
 */
expect suspend fun delay(timeMillis: Long)
