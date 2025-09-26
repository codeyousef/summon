package code.yousef.summon.runtime

import code.yousef.summon.runtime.wasmConsoleLog
import code.yousef.summon.runtime.wasmConsoleWarn
import code.yousef.summon.runtime.wasmConsoleError

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

