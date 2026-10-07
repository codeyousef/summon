package codes.yousef.summon.desktop.pip

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier

/**
 * WASM implementation of PictureInPicture.
 * Document PiP API requires complex JS interop - providing stubs for now.
 */

actual fun isPictureInPictureSupported(): Boolean {
    // Document PiP API requires JS interop that's complex in WASM
    return false
}

/**
 * Executes the request picture in picture operation.
 *
 * @param options The options value.
 * @return The resulting value.
 */
actual suspend fun requestPictureInPicture(
    options: PipOptions
): PipResult {
    return PipResult.NotSupported
}

/**
 * Renders picture in picture content.
 *
 * @param window The window value.
 * @param modifier Styles and attributes applied to the rendered element.
 * @param content Composable content emitted by this API.
 */
@Composable
actual fun PictureInPictureContent(
    window: PipWindow,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    // No-op on WASM - PiP not supported
}
