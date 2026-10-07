package codes.yousef.summon.runtime

/**
 * WebAssembly implementation of console logging.
 */
actual object console {
    /**
     * Executes the log operation.
     *
     * @param message Message content.
     */
    actual fun log(message: Any?) {
        wasmConsoleLog(message.toString())
    }

    /**
     * Executes the warn operation.
     *
     * @param message Message content.
     */
    actual fun warn(message: Any?) {
        wasmConsoleWarn(message.toString())
    }

    /**
     * Executes the error operation.
     *
     * @param message Message content.
     */
    actual fun error(message: Any?) {
        wasmConsoleError(message.toString())
    }

    /**
     * Executes the info operation.
     *
     * @param message Message content.
     */
    actual fun info(message: Any?) {
        wasmConsoleLog(message.toString()) // info maps to log
    }

    /**
     * Executes the debug operation.
     *
     * @param message Message content.
     */
    actual fun debug(message: Any?) {
        wasmConsoleDebug(message.toString())
    }
}