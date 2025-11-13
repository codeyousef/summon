package codes.yousef.summon.ssr

import codes.yousef.summon.runtime.wasmConsoleLog

actual object FileSystemAccess {
    actual fun writeTextFile(path: String, content: String) {
        wasmConsoleLog("FileSystemAccess.writeTextFile: $path - WASM stub")
    }

    actual fun readTextFile(path: String): String {
        wasmConsoleLog("FileSystemAccess.readTextFile: $path - WASM stub")
        return ""
    }

    actual fun createDirectory(path: String) {
        wasmConsoleLog("FileSystemAccess.createDirectory: $path - WASM stub")
    }
}