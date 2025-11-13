package codes.yousef.summon.runtime

/**
 * WebAssembly implementation of console logging.
 */
actual object console {
    actual fun log(message: Any?) {
        wasmConsoleLog(message.toString())
    }

    actual fun warn(message: Any?) {
        wasmConsoleWarn(message.toString())
    }

    actual fun error(message: Any?) {
        wasmConsoleError(message.toString())
    }

    actual fun info(message: Any?) {
        wasmConsoleLog(message.toString()) // info maps to log
    }

    actual fun debug(message: Any?) {
        wasmConsoleDebug(message.toString())
    }
}