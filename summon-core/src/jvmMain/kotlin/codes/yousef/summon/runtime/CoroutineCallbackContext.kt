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
private val callbackContextCounter = java.util.concurrent.atomic.AtomicLong()

private fun nextCallbackContextId(): Long = callbackContextCounter.incrementAndGet()

private data class RenderingThreadState(
    val renderer: PlatformRenderer?,
    val callbackContextId: Long?
)

/**
 * Coroutine context element that preserves callback context across thread switches.
 * 
 * When a coroutine suspends and resumes on a different thread, this element ensures that
 * the callback context ID remains consistent throughout the request lifecycle. This is essential
 * for SSR hydration to work correctly, as callbacks registered during rendering must match
 * the callback IDs in the hydration data sent to the client.
 * 
 * Prefer [RenderingContextElement] for request rendering because it propagates
 * the renderer and callback namespace as one unit. This lower-level element is
 * available for callback-only coroutine work.
 */
class CallbackContextElement private constructor(
    private val contextId: Long
) : ThreadContextElement<Long?> {
    companion object Key : CoroutineContext.Key<CallbackContextElement>
    constructor() : this(nextCallbackContextId())

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
 * Propagates a request-owned renderer and callback namespace together across
 * coroutine dispatcher hops, restoring the caller's thread state on every exit.
 */
class RenderingContextElement private constructor(
    private val renderer: PlatformRenderer,
    private val contextId: Long
) : ThreadContextElement<Any?> {
    companion object Key : CoroutineContext.Key<RenderingContextElement>
    constructor(renderer: PlatformRenderer) : this(renderer, nextCallbackContextId())

    override val key: CoroutineContext.Key<*> get() = Key

    override fun updateThreadContext(context: CoroutineContext): Any? {
        val oldState = RenderingThreadState(
            renderer = PlatformRendererStore.get(),
            callbackContextId = callbackContextThreadLocal.get()
        )
        PlatformRendererStore.set(renderer)
        callbackContextThreadLocal.set(contextId)
        return oldState
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: Any?) {
        val previous = oldState as RenderingThreadState
        PlatformRendererStore.set(previous.renderer)
        if (previous.callbackContextId == null) {
            callbackContextThreadLocal.remove()
        } else {
            callbackContextThreadLocal.set(previous.callbackContextId)
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
