package codes.yousef.summon

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.RecompositionScheduler

/**
 * Executes the mount composable root operation.
 *
 * @param rootElementId The root element id value.
 * @param scheduler The scheduler value.
 * @param composable The composable value.
 * @return The resulting value.
 */
actual fun mountComposableRoot(
    rootElementId: String,
    scheduler: RecompositionScheduler?,
    composable: @Composable () -> Unit
): MountedComposition = throw UnsupportedOperationException("Browser DOM mounting is unavailable on JVM; use PlatformRenderer for SSR")
