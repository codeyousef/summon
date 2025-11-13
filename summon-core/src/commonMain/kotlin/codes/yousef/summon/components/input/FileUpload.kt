package codes.yousef.summon.components.input

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.mutableStateOf
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.State

/**
 * A composable function that displays a button to trigger file selection.
 * The actual file input is hidden and managed by the PlatformRenderer.
 *
 * @param onFilesSelected Callback invoked when files are selected.
 * @param modifier Modifier applied to the overall layout (e.g., the Column containing label and button).
 * @param enabled Controls the enabled state of the trigger button.
 * @param multiple Whether to allow multiple file selection.
 * @param accept Comma-separated string of accepted file types (e.g., "image/*,.pdf").
 * @param capture Specifies the input source for captured media (e.g., "user", "environment").
 * @param buttonLabel Text displayed on the trigger button.
 * @param label Optional label displayed above the button.
 * @param buttonStyle Optional modifier applied specifically to the trigger button.
*/
 * */
@Composable
fun FileUpload(
    onFilesSelected: (List<FileInfo>) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    multiple: Boolean = false,
    accept: String? = null,
    capture: String? = null,
    buttonLabel: String = "Upload File",
    label: String? = null,
    buttonStyle: Modifier = Modifier(),
) {
    val renderer = LocalPlatformRenderer.current

    val triggerFileSelection = renderer.renderFileUpload(
        onFilesSelected = onFilesSelected,
        accept = accept,
        multiple = multiple,
        enabled = enabled,
        capture = capture,
        modifier = Modifier()
    )

    Column(modifier = modifier) {
        label?.let { Text(it) }
        Button(
            onClick = triggerFileSelection,
            label = buttonLabel,
            modifier = buttonStyle,
            disabled = !enabled
        )
    }
}

/**
 * A stateful overload of FileUpload that remembers the selected files.
 *
 * @param modifier Modifier applied to the overall layout.
 * @param enabled Controls the enabled state.
 * @param multiple Whether to allow multiple file selection.
 * @param accept Accepted file types.
 * @param capture Capture source.
 * @param buttonLabel Text for the trigger button.
 * @param label Optional label.
 * @param buttonStyle Optional modifier for the button.
 * @return A State object holding the list of currently selected FileInfo objects.
 */
@Composable
fun FileUpload(
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    multiple: Boolean = false,
    accept: String? = null,
    capture: String? = null,
    buttonLabel: String = "Upload File",
    label: String? = null,
    buttonStyle: Modifier = Modifier(),
): State<List<FileInfo>> {
    val filesState = remember { mutableStateOf<List<FileInfo>>(emptyList()) }

    FileUpload(
        onFilesSelected = { filesState.value = it },
        modifier = modifier,
        enabled = enabled,
        multiple = multiple,
        accept = accept,
        capture = capture,
        buttonLabel = buttonLabel,
        label = label,
        buttonStyle = buttonStyle
    )

    return filesState
}