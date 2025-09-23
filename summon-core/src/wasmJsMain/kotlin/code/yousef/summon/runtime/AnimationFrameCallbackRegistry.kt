package code.yousef.summon.runtime

/**
 * Registry for animation frame callbacks used during recomposition.
 * Similar to CallbackRegistry but specifically for animation frame work.
 */
object AnimationFrameCallbackRegistry {
    private val callbacks = mutableMapOf<Int, () -> Unit>()

    /**
     * Registers a callback and returns its ID.
     */
    fun registerCallback(frameId: Int, callback: () -> Unit) {
        callbacks[frameId] = callback
        wasmConsoleLog("AnimationFrameCallbackRegistry: Registered callback for frame ID: $frameId")
    }

    /**
     * Executes a callback by its frame ID.
     */
    fun executeCallback(frameId: Int): Boolean {
        val callback = callbacks[frameId]
        return if (callback != null) {
            try {
                wasmConsoleLog("AnimationFrameCallbackRegistry: Executing callback for frame ID: $frameId")
                callback.invoke()
                callbacks.remove(frameId) // Remove after execution
                true
            } catch (e: Exception) {
                wasmConsoleError("AnimationFrameCallbackRegistry: Error executing callback for frame $frameId: ${e.message}")
                callbacks.remove(frameId)
                false
            }
        } else {
            wasmConsoleWarn("AnimationFrameCallbackRegistry: No callback found for frame ID: $frameId")
            false
        }
    }

    /**
     * Clears a callback without executing it.
     */
    fun clearCallback(frameId: Int) {
        callbacks.remove(frameId)
        wasmConsoleLog("AnimationFrameCallbackRegistry: Cleared callback for frame ID: $frameId")
    }
}

/**
 * Bridge function for JavaScript to execute animation frame callbacks.
 * This is exported so JavaScript can call back into WASM.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun executeAnimationFrameCallback(frameId: Int): Boolean {
    return AnimationFrameCallbackRegistry.executeCallback(frameId)
}