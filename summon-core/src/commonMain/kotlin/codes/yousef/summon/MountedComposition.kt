package codes.yousef.summon

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.RecompositionScheduler

/** Owns a mounted browser root. Dispose when its container leaves the application. */
interface MountedComposition {
    /** Whether this owner has completed disposal. */
    val isDisposed: Boolean
    /** Cancels queued rendering and releases state, effects, DOM nodes and callbacks once. */
    fun dispose()
}

/**
 * Mounts an independently reactive browser root, replacing any previous mount in that container.
 * The requested element must exist. A supplied scheduler belongs exclusively to this mount.
 * Throws [UnsupportedOperationException] on JVM; use the server renderer for HTML there.
 */
expect fun mountComposableRoot(
    rootElementId: String,
    scheduler: RecompositionScheduler? = null,
    composable: @Composable () -> Unit
): MountedComposition
