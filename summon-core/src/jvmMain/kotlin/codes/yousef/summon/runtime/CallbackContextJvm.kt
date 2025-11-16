package codes.yousef.summon.runtime

/**
 * Returns the callback context key for the current execution context.
 * 
 * This uses [getStableCallbackContextKey] which checks for a coroutine-local context first,
 * then falls back to thread ID. This ensures callbacks work correctly in both:
 * - Traditional multi-threaded applications (thread ID)
 * - Coroutine-based applications (stable context across thread switches)
 */
internal actual fun callbackContextKey(): Long = getStableCallbackContextKey()
