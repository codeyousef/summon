package code.yousef.summon.components.input

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.mutableStateOf

/**
 * A composable function that allows the user to upload files.
 *
 * @param onFilesSelected Callback that is invoked when files are selected by the user.
 * @param modifier Modifier to be applied to the layout.
 * @param enabled Whether the component is enabled or not.
 * @param multiple Whether to allow multiple file selection.
 * @param accept File types that are accepted (e.g. ".jpg,.png").
 * @param buttonLabel Text displayed on the button.
 * @param label Optional label to display above the component.
 * @param buttonStyle Optional modifier applied to the button.
 */
@Composable
fun FileUpload(
    onFilesSelected: (List<FileInfo>) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    multiple: Boolean = false,
    accept: String? = null,
    buttonLabel: String = "Upload Files",
    label: String? = null,
    buttonStyle: Modifier = Modifier(),
) {
    val renderer = getPlatformRenderer()

    renderer.renderFileUpload(
        onFilesSelected = onFilesSelected,
        accept = accept,
        multiple = multiple,
        enabled = enabled,
        capture = null,
        modifier = modifier
    )
}

/**
 * A composable function that allows the user to upload files with state management.
 *
 * @param onFilesSelected Callback that is invoked when files are selected by the user.
 * @param modifier Modifier to be applied to the layout.
 * @param enabled Whether the component is enabled or not.
 * @param multiple Whether to allow multiple file selection.
 * @param accept File types that are accepted (e.g. ".jpg,.png").
 * @param buttonLabel Text displayed on the button.
 * @param label Optional label to display above the component.
 * @param buttonStyle Optional modifier applied to the button.
 * @return The currently selected files.
 */
@Composable
fun FileUpload(
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    multiple: Boolean = false,
    accept: String? = null,
    buttonLabel: String = "Upload Files",
    label: String? = null,
    buttonStyle: Modifier = Modifier(),
): List<FileInfo> {
    val files = remember { mutableStateOf<List<FileInfo>>(emptyList()) }

    FileUpload(
        onFilesSelected = { files.value = it },
        modifier = modifier,
        enabled = enabled,
        multiple = multiple,
        accept = accept,
        buttonLabel = buttonLabel,
        label = label,
        buttonStyle = buttonStyle
    )

    return files.value
} 