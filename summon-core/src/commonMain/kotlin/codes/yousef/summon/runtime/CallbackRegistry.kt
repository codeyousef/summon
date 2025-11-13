package codes.yousef.summon.runtime

import kotlin.random.Random

internal expect fun callbackContextKey(): Long
internal expect class CallbackRegistryLock()

internal expect fun <T> withCallbackRegistryLock(lock: CallbackRegistryLock, block: () -> T): T

/**
 * Global registry for storing callbacks that need to be invoked from client-side hydration.
 * This bridges the gap between server-side rendering and client-side interactivity.
 *
 * The registry is sharded per execution context (thread on JVM, single shared context elsewhere)
 * so concurrent requests do not interfere with one another.
 */
object CallbackRegistry {
    private const val DEFAULT_TTL_MS: Long = 5 * 60 * 1000 // 5 minutes
    private val lock = CallbackRegistryLock()
    private val registeredCallbacks = mutableMapOf<String, CallbackEntry>()
    private val renderContexts = mutableMapOf<Long, MutableSet<String>>()
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
            registeredCallbacks[id] = CallbackEntry(callback, currentTimeMillis())
            renderContexts[callbackContextKey()]?.add(id)
            id
        }
    }

    /**
     * Executes a callback by its ID. Used by the client-side hydration script.
     *
     * @param callbackId The ID of the callback to execute
     * @return true if the callback was found and executed, false otherwise
     */
    fun executeCallback(callbackId: String): Boolean {
        val entry = withLock {
            purgeExpiredLocked()
            registeredCallbacks.remove(callbackId)
        }

        return if (entry != null) {
            try {
                entry.callback.invoke()
                true
            } catch (e: Exception) {
                SummonLogger.error("Error executing callback $callbackId: ${e.message}")
                false
            }
        } else {
            SummonLogger.warn("Callback not found: $callbackId")
            false
        }
    }

    /**
     * Captures all callback IDs registered during the current render cycle.
     * The callbacks remain available for execution after this call.
     */
    fun finishRenderAndCollectCallbackIds(): Set<String> = withLock {
        renderContexts.remove(callbackContextKey())?.toSet() ?: emptySet()
    }

    /**
     * Clears all registered callbacks and render contexts. Intended for tests or application shutdown.
     */
    fun clear() {
        withLock {
            registeredCallbacks.clear()
            renderContexts.clear()
            callbackCounter = 0
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
        renderContexts[callbackContextKey()] = mutableSetOf()
        purgeExpiredLocked()
    }

    /**
     * Ends the current render cycle without returning the collected IDs.
     * Typically `finishRenderAndCollectCallbackIds` should be used instead.
     */
    fun abandonRenderContext() = withLock {
        renderContexts.remove(callbackContextKey())
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
        val counter = ++callbackCounter
        return buildString {
            append("cb-")
            append(counter.toString(16))
            append('-')
            append(Random.nextInt(1000, 9999))
        }
    }

    private data class CallbackEntry(
        val callback: () -> Unit,
        val timestamp: Long
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
