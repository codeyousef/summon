package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.MutableState
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.runtime.mutableStateOf

/**
 * A composable that displays a file upload input field.
 * 
 * @param onFilesSelected Callback that is invoked when files are selected
 * @param modifier The modifier to apply to this composable
 * @param multiple Whether multiple files can be selected
 * @param accept File types that are accepted (e.g., ".jpg, .png, image/*")
 * @param enabled Whether the file upload is enabled
 * @param capture Capture method to use for file selection on mobile devices
 * @param label Optional label to display for the file upload
 * @param buttonLabel Text to display on the upload button
 */
 * */
@Composable
fun FileUpload(
    onFilesSelected: (List<FileInfo>) -> Unit,
    modifier: Modifier = Modifier(),
    multiple: Boolean = false,
    accept: String? = null,
    enabled: Boolean = true,
    capture: String? = null,
    label: String? = null,
    buttonLabel: String = "Choose Files"
) {
    // Render the file upload using the platform renderer
    val renderer = getPlatformRenderer()
    
    // Adapter to convert List<Any> to List<FileInfo>
    val onFileSelectedAdapter: (List<Any>) -> Unit = { anyFiles ->
        val fileInfos = anyFiles.map { anyFile ->
            // Handle the conversion based on what platform provides
            // Basic implementation that works if Any is a Map with the required properties
            if (anyFile is Map<*, *>) {
                FileInfo(
                    name = (anyFile["name"] as? String) ?: "",
                    size = (anyFile["size"] as? Number)?.toLong() ?: 0L,
                    type = (anyFile["type"] as? String) ?: "",
                    lastModified = (anyFile["lastModified"] as? Number)?.toLong() ?: 0L
                )
            } else {
                // Fallback
                FileInfo("", 0L, "", 0L)
            }
        }
        onFilesSelected(fileInfos)
    }
    
    // Convert accept string to list if needed
    val acceptedFileTypes = accept?.split(",")?.map { it.trim() } ?: emptyList()
    
    // Call the renderer with the correct parameters in the order expected by the interface
    renderer.renderFileUpload(onFileSelectedAdapter, modifier, multiple, acceptedFileTypes)
    
    // TODO: Add support for label and button styling
}

/**
 * A composable that displays a file upload input field with state management.
 * 
 * @param state The state that holds the current list of selected files
 * @param onFilesSelected Callback that is invoked when files are selected
 * @param modifier The modifier to apply to this composable
 * @param multiple Whether multiple files can be selected
 * @param accept File types that are accepted (e.g., ".jpg, .png, image/*")
 * @param enabled Whether the file upload is enabled
 * @param capture Capture method to use for file selection on mobile devices
 * @param label Optional label to display for the file upload
 * @param buttonLabel Text to display on the upload button
 */
 * */
@Composable
fun FileUpload(
    state: MutableState<List<FileInfo>>,
    onFilesSelected: (List<FileInfo>) -> Unit = {},
    modifier: Modifier = Modifier(),
    multiple: Boolean = false,
    accept: String? = null,
    enabled: Boolean = true,
    capture: String? = null,
    label: String? = null,
    buttonLabel: String = "Choose Files"
) {
    FileUpload(
        onFilesSelected = { files ->
            state.value = files
            onFilesSelected(files)
        },
        modifier = modifier,
        multiple = multiple,
        accept = accept,
        enabled = enabled,
        capture = capture,
        label = label,
        buttonLabel = buttonLabel
    )
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