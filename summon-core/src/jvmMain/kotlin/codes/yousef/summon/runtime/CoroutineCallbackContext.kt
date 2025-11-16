package codes.yousef.summon.runtime

import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Thread-local storage for callback context that persists across coroutine thread switches.
 * 
 * This is critical for SSR hydration in coroutine-based frameworks like Ktor and Spring WebFlux,
 * where a single request may be processed by multiple threads due to coroutine suspension and
 * resumption on different threads.
 */
private val callbackContextThreadLocal = ThreadLocal<Long>()

/**
 * Coroutine context element that preserves callback context across thread switches.
 * 
 * When a coroutine suspends and resumes on a different thread, this element ensures that
 * the callback context ID remains consistent throughout the request lifecycle. This is essential
 * for SSR hydration to work correctly, as callbacks registered during rendering must match
 * the callback IDs in the hydration data sent to the client.
 * 
 * Usage:
 * ```kotlin
 * withContext(CallbackContextElement()) {
 *     // All callbacks registered here will share the same context ID
 *     // even if the coroutine switches threads
 * }
 * ```
 * 
 * This is automatically used by `respondSummonHydrated` in the Ktor integration.
 */
class CallbackContextElement(
    private val contextId: Long = System.nanoTime()
) : ThreadContextElement<Long?> {
    companion object Key : CoroutineContext.Key<CallbackContextElement>

    override val key: CoroutineContext.Key<*> get() = Key

    /**
     * Called when entering the coroutine context. Sets the thread-local callback context.
     */
    override fun updateThreadContext(context: CoroutineContext): Long? {
        val oldValue = callbackContextThreadLocal.get()
        callbackContextThreadLocal.set(contextId)
        return oldValue
    }

    /**
     * Called when leaving the coroutine context. Restores the previous thread-local value.
     */
    override fun restoreThreadContext(context: CoroutineContext, oldState: Long?) {
        if (oldState != null) {
            callbackContextThreadLocal.set(oldState)
        } else {
            callbackContextThreadLocal.remove()
        }
    }
}

/**
 * Gets the stable callback context key for the current execution context.
 * 
 * This function first checks if we're in a coroutine context with a [CallbackContextElement],
 * and uses that stable ID. Otherwise, it falls back to the current thread ID.
 * 
 * This ensures that callbacks registered during SSR rendering can be reliably collected
 * and included in the hydration data, even in coroutine-based frameworks where threads
 * may switch during request handling.
 */
internal fun getStableCallbackContextKey(): Long {
    // First check if we have a coroutine-local context (takes precedence)
    val threadLocalValue = callbackContextThreadLocal.get()
    if (threadLocalValue != null) {
        return threadLocalValue
    }
    // Fallback to thread ID for non-coroutine contexts
    return Thread.currentThread().id
}
