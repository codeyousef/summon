package code.yousef.summon.effects

/**
 * Platform-independent way to get current time in milliseconds.
 * Each platform provides its own implementation.
 */
expect fun getCurrentTimeMillis(): Long 