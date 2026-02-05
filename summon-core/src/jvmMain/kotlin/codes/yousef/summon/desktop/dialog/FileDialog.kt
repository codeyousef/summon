@file:JvmName("FileDialogJvm")

package codes.yousef.summon.desktop.dialog

import codes.yousef.summon.components.input.FileInfo

/**
 * JVM implementation of file dialogs.
 * File dialogs are not supported on server-side JVM.
 */

actual fun isFileSystemAccessSupported(): Boolean = false

actual suspend fun showOpenFileDialog(options: FileDialogOptions): List<FileInfo>? {
    println("showOpenFileDialog is not supported on JVM")
    return null
}

actual suspend fun showDirectoryPicker(title: String?, startIn: String?): DirectoryHandle? {
    println("showDirectoryPicker is not supported on JVM")
    return null
}

actual suspend fun showSaveFileDialog(options: SaveDialogOptions): SaveDialogResult? {
    println("showSaveFileDialog is not supported on JVM")
    return null
}
