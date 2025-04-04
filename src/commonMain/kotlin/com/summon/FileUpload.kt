package com.summon

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
    val buttonLabel: String = "Choose Files"
) : Composable {
    // Internal state to track selected files
    private val selectedFiles = mutableStateOf<List<FileInfo>>(emptyList())

    /**
     * Renders this FileUpload composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderFileUpload(this, receiver as TagConsumer<T>)
        }
        return receiver
    }

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
}

/**
 * Data class representing a file selected for upload.
 */
data class FileInfo(
    val name: String,
    val size: Long,
    val type: String,
    val lastModified: Long
) 