package codes.yousef.summon.runtime

import kotlinx.coroutines.CancellationException

internal expect fun callbackContextKey(): Long
internal expect class CallbackRegistryLock()
internal expect fun isCallbackDebugEnabled(): Boolean
internal expect fun generateCallbackCapability(): String

internal expect fun <T> withCallbackRegistryLock(lock: CallbackRegistryLock, block: () -> T): T

data class CallbackRenderContext(
    val callbackIds: Set<String>,
    val capability: String
)

/**
 * Global registry for storing callbacks that need to be invoked from client-side hydration.
 * This bridges the gap between server-side rendering and client-side interactivity.
 *
 * The registry is sharded per execution context (thread on JVM, single shared context elsewhere)
 * so concurrent requests do not interfere with one another.
 */
object CallbackRegistry {
    private val diagnostics = RendererDiagnostics { SummonLogger.error(it) }
    private const val DEFAULT_TTL_MS: Long = 5 * 60 * 1000 // 5 minutes
    private val lock = CallbackRegistryLock()
    private val registeredCallbacks = mutableMapOf<String, CallbackEntry>()
    private val renderContexts = mutableMapOf<Long, RenderRegistration>()

    // Store per-context counters instead of global counter to avoid mismatch
    private val contextCounters = mutableMapOf<Long, Long>()
    private var callbackCounter = 0L

    /**
     * Registers a callback and returns a unique ID that can be used in HTML attributes.
     *
     * @param callback The callback function to register
     * @return A unique callback ID that can be used in data attributes
     */
    fun registerCallback(callback: () -> Unit): String {
        return withLock {
            purgeExpiredLocked()
            val id = nextCallbackIdLocked()
            val contextKey = callbackContextKey()
            val renderContext = renderContexts[contextKey]
            registeredCallbacks[id] = CallbackEntry(callback, currentTimeMillis(), renderContext?.capability)
            val wasAdded = renderContext?.callbackIds?.add(id)
            if (isCallbackDebugEnabled()) {
                SummonLogger.log("[CallbackRegistry] Registered callback $id for context $contextKey (added to context: $wasAdded, context exists: ${renderContexts.containsKey(contextKey)})")
                // Log stack trace to see WHERE this callback is being registered from
                try {
                    throw Exception("Callback registration stack trace")
                } catch (e: Exception) {
                    SummonLogger.log("[CallbackRegistry] Registration location:\n${e.stackTraceToString().take(500)}")
                }
            }
            id
        }
    }

    /**
     * Executes an in-process callback by ID. Browser-facing endpoints must use
     * [executeRemoteCallback], which also verifies the render capability.
     */
    internal fun executeCallback(callbackId: String): Boolean {
        val entry = withLock {
            purgeExpiredLocked()
            registeredCallbacks.remove(callbackId)
        }
        return invokeEntry(entry)
    }

    /**
     * Executes a one-shot SSR callback only when the opaque capability from the
     * originating render context matches. A failed replay does not consume it.
     */
    fun executeRemoteCallback(callbackId: String, capability: String?): Boolean {
        if (callbackId.isBlank() || capability.isNullOrBlank()) return false
        val entry = withLock {
            purgeExpiredLocked()
            val candidate = registeredCallbacks[callbackId]
            if (candidate?.capability != null && constantTimeEquals(candidate.capability, capability)) {
                registeredCallbacks.remove(callbackId)
            } else {
                null
            }
        }
        return invokeEntry(entry)
    }

    private fun invokeEntry(entry: CallbackEntry?): Boolean {
        if (entry == null) return false
        return try {
            entry.callback.invoke()
            true
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            diagnostics.failure()
            false
        }
    }

    /**
     * Captures the callback IDs and opaque capability registered during this render.
     * Callbacks remain available for one successful execution until their TTL expires.
     */
    fun finishRenderAndCollectCallbacks(): CallbackRenderContext = withLock {
        val contextKey = callbackContextKey()
        val context = renderContexts.remove(contextKey)
        contextCounters.remove(contextKey)
        val result = CallbackRenderContext(
            callbackIds = context?.callbackIds?.toSet() ?: emptySet(),
            capability = context?.capability.orEmpty()
        )
        if (isCallbackDebugEnabled()) {
            SummonLogger.log("[CallbackRegistry] Collected ${result.callbackIds.size} callbacks for context $contextKey")
        }
        result
    }

    /**
     * Clears all registered callbacks and render contexts. Intended for tests or application shutdown.
     */
    fun clear() {
        withLock {
            registeredCallbacks.clear()
            renderContexts.clear()
            callbackCounter = 0
            contextCounters.clear()
        }
    }

    /**
     * Gets the number of registered callbacks. Useful for diagnostics.
     */
    fun size(): Int = withLock { registeredCallbacks.size }

    /**
     * Checks if a callback with the given ID exists in the current context.
     *
     * @param callbackId The ID to check
     * @return true if the callback exists, false otherwise
     */
    fun hasCallback(callbackId: String): Boolean = withLock {
        registeredCallbacks.containsKey(callbackId)
    }

    /**
     * Marks the beginning of a render cycle. Callbacks registered between beginRender/endRender
     * are tracked so they can be embedded into hydration metadata.
     */
    fun beginRender() = withLock {
        val contextKey = callbackContextKey()
        renderContexts[contextKey] = RenderRegistration(
            capability = generateCallbackCapability(),
            callbackIds = mutableSetOf()
        )
        contextCounters[contextKey] = 0L
        if (isCallbackDebugEnabled()) {
            SummonLogger.log("[CallbackRegistry] beginRender for context $contextKey (total contexts: ${renderContexts.size})")
        }
        purgeExpiredLocked()
    }

    /**
     * Ends the current render cycle without collecting its callback capability.
     */
    fun abandonRenderContext() = withLock {
        val contextKey = callbackContextKey()
        renderContexts.remove(contextKey)
        contextCounters.remove(contextKey)
    }

    private fun <T> withLock(block: () -> T): T = withCallbackRegistryLock(lock, block)

    private fun purgeExpiredLocked(ttlMillis: Long = DEFAULT_TTL_MS) {
        if (registeredCallbacks.isEmpty()) return
        val cutoff = currentTimeMillis() - ttlMillis
        val iterator = registeredCallbacks.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value.timestamp < cutoff) {
                iterator.remove()
            }
        }
    }

    private fun nextCallbackIdLocked(): String {
        val contextKey = callbackContextKey()
        val counter = if (contextCounters.containsKey(contextKey)) {
            val current = contextCounters[contextKey]!!
            contextCounters[contextKey] = current + 1
            current + 1
        } else {
            ++callbackCounter
        }
        return "cb-${counter.toString(16)}"
    }

    private fun constantTimeEquals(expected: String, actual: String): Boolean {
        var difference = expected.length xor actual.length
        val maxLength = maxOf(expected.length, actual.length)
        for (index in 0 until maxLength) {
            val expectedCode = if (index < expected.length) expected[index].code else 0
            val actualCode = if (index < actual.length) actual[index].code else 0
            difference = difference or (expectedCode xor actualCode)
        }
        return difference == 0
    }

    private data class RenderRegistration(
        val capability: String,
        val callbackIds: MutableSet<String>
    )

    private data class CallbackEntry(
        val callback: () -> Unit,
        val timestamp: Long,
        val capability: String?
    )
}

/**
 * Platform-specific logger object for Summon.
 * This is implemented differently on JS and JVM platforms.
 */
expect object SummonLogger {
    fun log(message: String)
    fun warn(message: String)
    fun error(message: String)
}
