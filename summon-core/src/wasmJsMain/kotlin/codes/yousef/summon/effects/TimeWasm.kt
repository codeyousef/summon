package codes.yousef.summon.effects

import codes.yousef.summon.runtime.safeWasmConsoleError
import codes.yousef.summon.runtime.safeWasmConsoleLog
import codes.yousef.summon.runtime.safeWasmConsoleWarn
import kotlin.time.Clock

// Simple timeout storage for WASM implementation
private var timeoutIdCounter = 0
private val timeoutCallbacks = mutableMapOf<Int, () -> Unit>()

/**
 * Executes the current time millis operation.
 *
 * @return The resulting value.
 */
actual fun currentTimeMillis(): Long {
    return try {
        // Use kotlinx-datetime for consistent time across platforms
        Clock.System.now().toEpochMilliseconds()
    } catch (e: Exception) {
        safeWasmConsoleWarn("Failed to get current time, using fallback: ${e.message}")
        // Fallback to a reasonable timestamp
        1700000000000L // Approximate current time
    }
}

/**
 * Sets timeout.
 *
 * @param delayMs The delay ms value.
 * @param callback The callback value.
 * @return The resulting value.
 */
actual fun setTimeout(delayMs: Int, callback: () -> Unit): Int {
    return try {
        val timeoutId = ++timeoutIdCounter
        timeoutCallbacks[timeoutId] = callback

        safeWasmConsoleLog("Setting timeout for $delayMs ms with ID: $timeoutId")

        // Fallback for test environment - immediate execution for now
        // In a real WASM environment, this would integrate with the event loop
        safeWasmConsoleWarn("setTimeout executing callback immediately (WASM test environment)")
        try {
            callback()
        } catch (callbackError: Exception) {
            safeWasmConsoleError("Error executing timeout callback: ${callbackError.message}")
        }
        timeoutId
    } catch (e: Exception) {
        safeWasmConsoleError("setTimeout failed: ${e.message}")
        -1 // Error indicator
    }
}

/**
 * Clears timeout.
 *
 * @param id Stable identifier.
 */
actual fun clearTimeout(id: Int) {
    try {
        safeWasmConsoleLog("Clearing timeout with ID: $id")
        timeoutCallbacks.remove(id)

        // In WASM test environment, timeout is executed immediately
        // so clearing is just removing from our callback map
        safeWasmConsoleLog("Timeout $id cleared from callback map")
    } catch (e: Exception) {
        safeWasmConsoleError("clearTimeout failed: ${e.message}")
    }
}

