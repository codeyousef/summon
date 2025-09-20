package code.yousef.summon.effects

import code.yousef.summon.runtime.wasmConsoleLog
import code.yousef.summon.runtime.wasmConsoleWarn
import code.yousef.summon.runtime.wasmConsoleError

actual fun currentTimeMillis(): Long {
    wasmConsoleLog("Getting current time millis - WASM stub")
    return 1625097600000L // Fixed timestamp for WASM stub
}

actual fun setTimeout(delayMs: Int, callback: () -> Unit): Int {
    wasmConsoleLog("Setting timeout for $delayMs ms - WASM stub")
    // In a real implementation, this would schedule the callback
    return 1 // Fake timeout ID
}

actual fun clearTimeout(id: Int) {
    wasmConsoleLog("Clearing timeout with ID: $id - WASM stub")
    // In a real implementation, this would cancel the scheduled callback
}

