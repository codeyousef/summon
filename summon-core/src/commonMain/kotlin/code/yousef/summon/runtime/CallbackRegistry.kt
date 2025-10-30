package code.yousef.summon.runtime

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
    private val lock = CallbackRegistryLock()
    private val callbacksByContext = mutableMapOf<Long, MutableMap<String, () -> Unit>>()
    private var callbackCounter = 0L

    /**
     * Registers a callback and returns a unique ID that can be used in HTML attributes.
     *
     * @param callback The callback function to register
     * @return A unique callback ID that can be used in data attributes
     */
    fun registerCallback(callback: () -> Unit): String {
        return withCallbackRegistryLock(lock) {
            val contextCallbacks = callbacksByContext.getOrPut(callbackContextKey()) { mutableMapOf() }
            val counter = ++callbackCounter
            val id = buildString {
                append("cb-")
                append(callbackContextKey().toString(16))
                append('-')
                append(counter.toString(16))
                append('-')
                append(Random.nextInt(1000, 9999))
            }
            contextCallbacks[id] = callback
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
        val callback = withCallbackRegistryLock(lock) {
            var emptiedContext: Long? = null
            val callback = callbacksByContext.entries.firstNotNullOfOrNull { (context, callbacks) ->
                val candidate = callbacks.remove(callbackId)
                if (candidate != null && callbacks.isEmpty()) {
                    emptiedContext = context
                }
                candidate
            }
            if (emptiedContext != null) {
                callbacksByContext.remove(emptiedContext)
            }
            callback
        }

        return if (callback != null) {
            try {
                callback.invoke()
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
     * Gets all registered callback IDs for the current execution context.
     *
     * @return Set of callback IDs currently registered in this context
     */
    fun getAllCallbackIds(): Set<String> = withCallbackRegistryLock(lock) {
        callbacksByContext[callbackContextKey()]?.keys?.toSet() ?: emptySet()
    }

    /**
     * Clears all registered callbacks for the current execution context.
     */
    fun clear() {
        withCallbackRegistryLock(lock) {
            callbacksByContext.remove(callbackContextKey())
        }
    }

    /**
     * Gets the number of registered callbacks for the current context. Useful for debugging.
     */
    fun size(): Int = withCallbackRegistryLock(lock) {
        callbacksByContext[callbackContextKey()]?.size ?: 0
    }

    /**
     * Checks if a callback with the given ID exists in the current context.
     *
     * @param callbackId The ID to check
     * @return true if the callback exists, false otherwise
     */
    fun hasCallback(callbackId: String): Boolean = withCallbackRegistryLock(lock) {
        callbacksByContext.values.any { callbackId in it }
    }
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
