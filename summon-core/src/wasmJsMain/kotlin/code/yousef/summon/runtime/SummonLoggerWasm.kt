package codes.yousef.summon.runtime

actual object SummonLogger {
    actual fun log(message: String) {
        wasmConsoleLog(message)
    }

    actual fun warn(message: String) {
        wasmConsoleWarn(message)
    }

    actual fun error(message: String) {
        wasmConsoleError(message)
    }
}

