@file:JvmName("FileDialogJvm")

package codes.yousef.summon.desktop.dialog

import codes.yousef.summon.components.input.FileInfo

/**
 * JVM implementation of file dialogs.
 * File dialogs are not supported on server-side JVM.
 */

actual fun isFileSystemAccessSupported(): Boolean = false

/**
 * Executes the show open file dialog operation.
 *
 * @param options The options value.
 * @return The resulting value.
 */
actual suspend fun showOpenFileDialog(options: FileDialogOptions): List<FileInfo>? {
    println("showOpenFileDialog is not supported on JVM")
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
    println("showDirectoryPicker is not supported on JVM")
    return null
}

/**
 * Executes the show save file dialog operation.
 *
 * @param options The options value.
 * @return The resulting value.
 */
actual suspend fun showSaveFileDialog(options: SaveDialogOptions): SaveDialogResult? {
    println("showSaveFileDialog is not supported on JVM")
    return null
}
