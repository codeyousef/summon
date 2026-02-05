/**
 * # Picture-in-Picture
 *
 * Support for the Document Picture-in-Picture API, enabling floating windows
 * that stay on top of other windows.
 *
 * @since 0.7.0
 */
package codes.yousef.summon.desktop.pip

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier

/**
 * Options for creating a Picture-in-Picture window.
 */
data class PipOptions(
    /** Initial width of the PiP window */
    val width: Int = 300,
    /** Initial height of the PiP window */
    val height: Int = 200,
    /** Whether to copy stylesheets from the main document */
    val copyStyleSheets: Boolean = true
)

/**
 * Represents a Picture-in-Picture window.
 */
interface PipWindow {
    /** The width of the PiP window */
    val width: Int

    /** The height of the PiP window */
    val height: Int

    /** Whether the PiP window is currently open */
    val isOpen: Boolean

    /** Closes the PiP window */
    fun close()

    /** Focuses the PiP window */
    fun focus()
}

/**
 * Result of requesting a PiP window.
 */
sealed class PipResult {
    /** PiP window was created successfully */
    data class Success(val window: PipWindow) : PipResult()

    /** PiP is not supported on this platform */
    object NotSupported : PipResult()

    /** User denied the PiP request */
    object UserDenied : PipResult()

    /** An error occurred */
    data class Error(val message: String) : PipResult()
}

/**
 * Checks if the Document Picture-in-Picture API is supported.
 */
expect fun isPictureInPictureSupported(): Boolean

/**
 * Requests a Picture-in-Picture window.
 *
 * This uses the Document Picture-in-Picture API which allows rendering
 * arbitrary HTML content in a floating window.
 *
 * Note: This API requires user activation (must be called in response to
 * a user gesture like a click).
 *
 * @param options Configuration options for the PiP window
 * @return PipResult indicating success or failure
 */
expect suspend fun requestPictureInPicture(
    options: PipOptions = PipOptions()
): PipResult

/**
 * Renders content in a Picture-in-Picture window when available.
 *
 * Usage:
 * ```kotlin
 * var pipWindow by remember { mutableStateOf<PipWindow?>(null) }
 *
 * Button(
 *     onClick = {
 *         scope.launch {
 *             when (val result = requestPictureInPicture()) {
 *                 is PipResult.Success -> pipWindow = result.window
 *                 else -> { /* handle error */ }
 *             }
 *         }
 *     },
 *     label = "Open PiP"
 * )
 *
 * if (pipWindow != null) {
 *     PictureInPictureContent(pipWindow!!) {
 *         // Content rendered in the PiP window
 *         Text("Hello from PiP!")
 *     }
 * }
 * ```
 *
 * @param window The PiP window to render into
 * @param modifier Styling modifier for the container
 * @param content The composable content to render
 */
@Composable
expect fun PictureInPictureContent(
    window: PipWindow,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
)
