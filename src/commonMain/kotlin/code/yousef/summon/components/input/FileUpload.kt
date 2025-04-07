package code.yousef.summon.components.input


import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

/**
 * Data class representing a file selected for upload.
 */
data class FileInfo(
    val name: String,
    val size: Long,
    val type: String,
    val lastModified: Long
)

/**
 * File upload component that allows users to select files from their device.
 * 
 * This component supports single and multiple file uploads, drag-and-drop interactions,
 * file type restrictions, and custom styling for different states.
 *
 * @param onFilesSelected Callback that's invoked when files are selected
 * @param modifier Modifier for styling and layout
 * @param multiple Whether to allow multiple file selection
 * @param accept File types to accept (e.g., ".jpg,.png")
 * @param maxSizeMB Maximum allowed file size in MB
 * @param dropLabel Label for the drop zone (when using drag and drop)
 * @param buttonText Text for the upload button
 * @param iconSrc Optional icon source URL
 * @param iconAlt Alt text for the icon
 * @param dropZoneModifier Modifier specifically for the drop zone
 */
// TODO: Implement FileUpload component
// This class needs to be implemented once we have the basic framework working
// The previous implementation had an unclosed comment that was causing compilation issues
// For now, we're leaving this as a placeholder

/**
 * Convenience overload for FileUpload using a default Button as the trigger UI.
 */
@Composable
fun FileUploadButton(
    onFilesSelected: (List<FileInfo>) -> Unit,
    modifier: Modifier = Modifier(),
    accept: String? = null,
    multiple: Boolean = false,
    enabled: Boolean = true,
    capture: String? = null,
    buttonText: String = "Choose File(s)"
) {
    FileUpload(
        onFilesSelected = onFilesSelected,
        modifier = modifier,
        accept = accept,
        multiple = multiple,
        enabled = enabled,
        capture = capture
    ) { openFileDialog ->
        Button(
            onClick = openFileDialog,
            enabled = enabled
        ) {
            Text(text = buttonText)
        }
    }
} 
