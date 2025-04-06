package code.yousef.summon.components.input

import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.modifier.pointerEvents

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
 * A composable that provides a trigger UI for file uploads.
 * Handles the interaction with the native file input element.
 *
 * @param onFilesSelected Callback invoked with a list of `FileInfo` when files are selected by the user.
 * @param modifier Modifier applied to the container or the trigger UI (depending on implementation).
 * @param accept Optional comma-separated string of accepted file types (e.g., ".jpg, .png, image/*").
 * @param multiple Whether multiple files can be selected.
 * @param enabled Controls the enabled state of the file upload trigger.
 * @param capture Optional capture method for mobile devices (e.g., "user", "environment").
 * @param content A composable lambda that defines the UI for triggering the file selection.
 *                It receives an `onClick` lambda which should be called by the trigger UI (e.g., a Button) 
 *                to open the native file dialog.
 */
@Composable
fun FileUpload(
    onFilesSelected: (List<FileInfo>) -> Unit,
    modifier: Modifier = Modifier(),
    accept: String? = null,
    multiple: Boolean = false,
    enabled: Boolean = true,
    capture: String? = null,
    content: @Composable (onClick: () -> Unit) -> Unit
) {
    val finalModifier = modifier
        .opacity(if (enabled) 1f else 0.6f)
        .cursor(if (enabled) "pointer" else "default")
        .applyIf(!enabled) { pointerEvents("none") }

    val renderer = PlatformRendererProvider.getRenderer()

    val openFileDialog: () -> Unit = renderer.renderFileUpload(
        onFilesSelected = { if (enabled) onFilesSelected(it) },
        accept = accept,
        multiple = multiple,
        enabled = enabled,
        capture = capture,
        modifier = finalModifier
    )

    content(onClick = openFileDialog)
}

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