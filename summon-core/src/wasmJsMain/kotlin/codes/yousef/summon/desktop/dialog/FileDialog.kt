package codes.yousef.summon.desktop.dialog

import codes.yousef.summon.components.input.FileInfo

/**
 * WASM implementation of file dialogs.
 * Currently provides stub implementations - full support requires
 * additional JS interop work.
 */

@JsFun("() => typeof window.showOpenFilePicker === 'function'")
external fun hasFileSystemAccessJs(): Boolean

actual fun isFileSystemAccessSupported(): Boolean = try {
    hasFileSystemAccessJs()
} catch (e: Exception) {
    false
}

actual suspend fun showOpenFileDialog(options: FileDialogOptions): List<FileInfo>? {
    // WASM file dialog implementation requires complex JS interop
    // This is a stub that returns null (cancelled)
    return null
}

actual suspend fun showDirectoryPicker(title: String?, startIn: String?): DirectoryHandle? {
    return null
}

actual suspend fun showSaveFileDialog(options: SaveDialogOptions): SaveDialogResult? {
    return null
}
