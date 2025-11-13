package codes.yousef.summon.components.input

import codes.yousef.summon.runtime.wasmConsoleLog

actual class FileInfo {
    actual val name: String = "wasm-file-stub.txt"
    actual val size: Long = 0L
    actual val type: String = "text/plain"

    actual operator fun component1(): String = name
    actual operator fun component2(): Long = size
    actual operator fun component3(): String = type

    init {
        wasmConsoleLog("FileInfo created - WASM stub")
    }
}