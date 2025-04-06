package code.yousef.summon.components.input

import code.yousef.summon.core.getPlatformRenderer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.mutableStateOf
import kotlinx.html.TagConsumer

/**
 * A composable that displays a file upload input field.
 * @param onFilesSelected Callback that is invoked when files are selected
 * @param label Optional label to display for the file upload
 * @param accept File types that are accepted (e.g., ".jpg, .png, image/*")
 * @param multiple Whether multiple files can be selected
 * @param modifier The modifier to apply to this composable
 * @param disabled Whether the file upload is disabled
 * @param capture Capture method to use for file selection on mobile devices
 * @param buttonLabel Text to display on the upload button
 * @param onError Callback to handle errors
 * @param maxFileSize Maximum file size allowed in bytes
 * @param maxFileCount Maximum number of files allowed
*/
 * */
class FileUpload(
    val onFilesSelected: (List<FileInfo>) -> Unit = {},
    val label: String? = null,
    val accept: String? = null,
    val multiple: Boolean = false,
    val modifier: Modifier = Modifier(),
    val disabled: Boolean = false,
    val capture: String? = null,
    val buttonLabel: String = "Choose Files",
    val onError: ((String) -> Unit)? = null,
    val maxFileSize: Long? = null, // In bytes
    val maxFileCount: Int? = null
) {
    // Internal state to track selected files
    private val selectedFiles = mutableStateOf<List<FileInfo>>(emptyList())

    /**
     * Gets the currently selected files.
     */
    fun getSelectedFiles(): List<FileInfo> = selectedFiles.value

    /**
     * Updates the selected files and notifies listeners.
     */
    fun updateSelectedFiles(files: List<FileInfo>) {
        selectedFiles.value = files
        onFilesSelected(files)
    }

    /**
     * Generates a unique ID for the file input element.
     */
    fun generateUniqueId(): String {
        // Use a combination of hashCode and an incrementing counter for uniqueness
        return "file_upload_${hashCode()}_${generateCounter()}"
    }
    
    // Companion object to store a static counter for generating unique IDs
    companion object {
        private var counter = 0
        
        private fun generateCounter(): Int {
            return counter++
        }
    }
}

/**
 * Data class representing a file selected for upload.
 **/
data class FileInfo(
    val name: String,
    val size: Long,
    val type: String,
    val lastModified: Long
) 