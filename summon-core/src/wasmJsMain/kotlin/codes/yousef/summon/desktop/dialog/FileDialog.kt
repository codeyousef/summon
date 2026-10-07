package codes.yousef.summon.desktop.dialog

import codes.yousef.summon.components.input.FileInfo

/**
 * WASM implementation of file dialogs.
 * Currently provides stub implementations - full support requires
 * additional JS interop work.
 */

@JsFun("() => typeof window.showOpenFilePicker === 'function'")
/**
 * Returns whether this value has file system access js.
 *
 * @return The resulting value.
 */
external fun hasFileSystemAccessJs(): Boolean

/**
 * Returns whether file system access supported.
 *
 * @return The resulting value.
 */
actual fun isFileSystemAccessSupported(): Boolean = try {
    hasFileSystemAccessJs()
} catch (e: Exception) {
    false
}

/**
 * Executes the show open file dialog operation.
 *
 * @param options The options value.
 * @return The resulting value.
 */
actual suspend fun showOpenFileDialog(options: FileDialogOptions): List<FileInfo>? {
    // WASM file dialog implementation requires complex JS interop
    // This is a stub that returns null (cancelled)
    return null
}

/**
 * Executes the show directory picker operation.
 *
 * @param title The title value.
 * @param startIn The start in value.
 * @return The resulting value.
 */
actual suspend fun showDirectoryPicker(title: String?, startIn: String?): DirectoryHandle? {
    return null
}

/**
 * Executes the show save file dialog operation.
 *
 * @param options The options value.
 * @return The resulting value.
 */
actual suspend fun showSaveFileDialog(options: SaveDialogOptions): SaveDialogResult? {
    return null
}
