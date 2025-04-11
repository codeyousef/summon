package code.yousef.summon.effects

/**
 * Platform-independent way to get current time in milliseconds.
 * Each platform provides its own implementation.
 */
expect fun getCurrentTimeMillis(): Long

/**
 * Get the current time in milliseconds
 */
expect fun currentTimeMillis(): Long

/**
 * Set a timeout to execute a function after a delay
 * @param delayMs The delay in milliseconds
 * @param callback The function to execute
 * @return An ID that can be used to cancel the timeout
 */
expect fun setTimeout(delayMs: Int, callback: () -> Unit): Int

/**
 * Clear a timeout
 * @param id The timeout ID to clear
 */
expect fun clearTimeout(id: Int) 