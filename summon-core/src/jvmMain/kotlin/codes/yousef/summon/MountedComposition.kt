package codes.yousef.summon

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.RecompositionScheduler

actual fun mountComposableRoot(
    rootElementId: String,
    scheduler: RecompositionScheduler?,
    composable: @Composable () -> Unit
): MountedComposition = throw UnsupportedOperationException("Browser DOM mounting is unavailable on JVM; use PlatformRenderer for SSR")
