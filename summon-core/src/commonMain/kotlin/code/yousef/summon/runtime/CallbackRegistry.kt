package code.yousef.summon.runtime

import kotlin.random.Random

/**
 * Global registry for storing callbacks that need to be invoked from client-side hydration.
 * This bridges the gap between server-side rendering and client-side interactivity.
 */
object CallbackRegistry {
    private val callbacks = mutableMapOf<String, () -> Unit>()
    private var callbackCounter = 0L
    
    /**
     * Registers a callback and returns a unique ID that can be used in HTML attributes.
     * 
     * @param callback The callback function to register
     * @return A unique callback ID that can be used in data attributes
     */
    fun registerCallback(callback: () -> Unit): String {
        val id = "callback-${++callbackCounter}-${Random.nextInt(1000, 9999)}"
        callbacks[id] = callback
        return id
    }
    
    /**
     * Executes a callback by its ID. Used by the client-side hydration script.
     * 
     * @param callbackId The ID of the callback to execute
     * @return true if the callback was found and executed, false otherwise
     */
    fun executeCallback(callbackId: String): Boolean {
        val callback = callbacks[callbackId]
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
     * Gets all registered callback IDs. Used for generating hydration data.
     * 
     * @return Set of all callback IDs currently registered
     */
    fun getAllCallbackIds(): Set<String> = callbacks.keys.toSet()
    
    /**
     * Clears all registered callbacks. Useful for testing or cleanup.
     */
    fun clear() {
        callbacks.clear()
        callbackCounter = 0L
    }
    
    /**
     * Gets the number of registered callbacks. Useful for debugging.
     */
    fun size(): Int = callbacks.size
    
    /**
     * Checks if a callback with the given ID exists.
     * 
     * @param callbackId The ID to check
     * @return true if the callback exists, false otherwise
     */
    fun hasCallback(callbackId: String): Boolean = callbacks.containsKey(callbackId)
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