@file:JvmName("PictureInPictureJvm")

package codes.yousef.summon.desktop.pip

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier

/**
 * JVM implementation of PictureInPicture.
 * PiP is not supported on server-side JVM.
 */

actual fun isPictureInPictureSupported(): Boolean = false

actual suspend fun requestPictureInPicture(
    options: PipOptions
): PipResult {
    println("Picture-in-Picture is not supported on JVM")
    return PipResult.NotSupported
}

@Composable
actual fun PictureInPictureContent(
    window: PipWindow,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    // No-op on JVM - PiP not supported
}
