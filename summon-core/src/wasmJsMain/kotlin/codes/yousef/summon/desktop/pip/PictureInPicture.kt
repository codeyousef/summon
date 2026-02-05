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

actual suspend fun requestPictureInPicture(
    options: PipOptions
): PipResult {
    return PipResult.NotSupported
}

@Composable
actual fun PictureInPictureContent(
    window: PipWindow,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    // No-op on WASM - PiP not supported
}
