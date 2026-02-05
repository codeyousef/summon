/**
 * # File Dialogs
 *
 * Native file picker dialogs using the File System Access API.
 *
 * @since 0.7.0
 */
package codes.yousef.summon.desktop.dialog

import codes.yousef.summon.components.input.FileInfo

/**
 * Options for file open dialogs.
 */
data class FileDialogOptions(
    /** List of accepted file types (e.g., listOf(".txt", ".md")) */
    val types: List<FileTypeFilter> = emptyList(),
    /** Allow selecting multiple files */
    val multiple: Boolean = false,
    /** Dialog title (not supported in all browsers) */
    val title: String? = null,
    /** Starting directory (not supported in all browsers) */
    val startIn: String? = null
)

/**
 * Options for file save dialogs.
 */
data class SaveDialogOptions(
    /** Suggested file name */
    val suggestedName: String? = null,
    /** Accepted file types for saving */
    val types: List<FileTypeFilter> = emptyList(),
    /** Dialog title */
    val title: String? = null,
    /** Starting directory */
    val startIn: String? = null
)

/**
 * File type filter for dialogs.
 */
data class FileTypeFilter(
    /** Description shown in the dialog (e.g., "Text files") */
    val description: String,
    /** Map of MIME type to extensions (e.g., "text/plain" to listOf(".txt")) */
    val accept: Map<String, List<String>>
)

/**
 * Result from a save dialog.
 */
data class SaveDialogResult(
    /** The file handle for writing */
    val name: String,
    /** Function to write content to the file */
    val write: suspend (String) -> Boolean
)

/**
 * Shows a file open dialog.
 *
 * Uses the File System Access API when available, falls back to
 * input[type=file] on older browsers.
 *
 * @param options Configuration for the dialog
 * @return List of selected files, or null if cancelled
 */
expect suspend fun showOpenFileDialog(options: FileDialogOptions = FileDialogOptions()): List<FileInfo>?

/**
 * Shows a directory picker dialog.
 *
 * @param title Optional dialog title
 * @param startIn Optional starting directory
 * @return Directory handle, or null if cancelled
 */
expect suspend fun showDirectoryPicker(
    title: String? = null,
    startIn: String? = null
): DirectoryHandle?

/**
 * Shows a file save dialog.
 *
 * @param options Configuration for the dialog
 * @return Result with file handle, or null if cancelled
 */
expect suspend fun showSaveFileDialog(options: SaveDialogOptions = SaveDialogOptions()): SaveDialogResult?

/**
 * Represents a handle to a directory.
 */
interface DirectoryHandle {
    val name: String
    suspend fun getFile(name: String): FileInfo?
    suspend fun listFiles(): List<String>
}

/**
 * Checks if the File System Access API is available.
 */
expect fun isFileSystemAccessSupported(): Boolean
