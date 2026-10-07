package codes.yousef.summon.ssr

import codes.yousef.summon.runtime.wasmConsoleLog

/** Provides file system access operations. */
actual object FileSystemAccess {
    /**
     * Executes the write text file operation.
     *
     * @param path Target path.
     * @param content Composable content emitted by this API.
     */
    actual fun writeTextFile(path: String, content: String) {
        wasmConsoleLog("FileSystemAccess.writeTextFile: $path - WASM stub")
    }

    /**
     * Executes the read text file operation.
     *
     * @param path Target path.
     * @return The resulting value.
     */
    actual fun readTextFile(path: String): String {
        wasmConsoleLog("FileSystemAccess.readTextFile: $path - WASM stub")
        return ""
    }

    /**
     * Creates directory.
     *
     * @param path Target path.
     */
    actual fun createDirectory(path: String) {
        wasmConsoleLog("FileSystemAccess.createDirectory: $path - WASM stub")
    }
}