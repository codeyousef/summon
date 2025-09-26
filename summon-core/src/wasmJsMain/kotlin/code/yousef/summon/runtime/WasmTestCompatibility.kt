package code.yousef.summon.runtime

/**
 * Safe wrapper for wasmConsoleLog that works in both browser and Node.js environments.
 * In Node.js test environments where wasmConsoleLog might not be available, this function
 * catches any errors and silently continues.
 */
internal fun safeWasmConsoleLog(message: String) {
    try {
        wasmConsoleLog(message)
    } catch (e: Throwable) {
        // Silently ignore if wasmConsoleLog is not available (e.g., in Node.js tests)
        // This allows tests to run without browser-specific functions
    }
}

/**
 * Safe wrapper for wasmConsoleWarn that works in both browser and Node.js environments.
 */
internal fun safeWasmConsoleWarn(message: String) {
    try {
        wasmConsoleWarn(message)
    } catch (e: Throwable) {
        // Silently ignore if wasmConsoleWarn is not available
    }
}

/**
 * Safe wrapper for wasmConsoleError that works in both browser and Node.js environments.
 */
internal fun safeWasmConsoleError(message: String) {
    try {
        wasmConsoleError(message)
    } catch (e: Throwable) {
        // Silently ignore if wasmConsoleError is not available
    }
}