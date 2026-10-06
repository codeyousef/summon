package codes.yousef.summon.runtime

import codes.yousef.summon.MountedComposition
import codes.yousef.summon.annotation.Composable

/** One owner per browser root; cleanup detaches the registry before invoking user effects. */
internal fun createOwnedComposition(
    renderer: PlatformRenderer,
    scheduler: RecompositionScheduler?,
    release: () -> Unit,
    beforeDispose: () -> Unit = {},
    root: @Composable () -> Unit
): MountedComposition {
    val recomposer = Recomposer()
    if (scheduler != null) recomposer.setScheduler(scheduler)
    val owner = OwnedComposition(recomposer, renderer, beforeDispose, release)
    val guardedRoot: @Composable () -> Unit = {
        try { root() } catch (error: Throwable) {
            try { owner.dispose() } catch (cleanupError: Throwable) { error.addSuppressed(cleanupError) }
            throw error
        }
    }
    try {
        withPlatformRenderer(renderer) {
            recomposer.setCompositionRoot(guardedRoot)
            recomposer.composeInitial(guardedRoot)
        }
    } catch (error: Throwable) {
        try { owner.dispose() } catch (cleanupError: Throwable) { error.addSuppressed(cleanupError) }
        throw error
    }
    return owner
}

/** Retaining a disposed handle must not retain its container, renderer or application closures. */
private class OwnedComposition(
    private var recomposer: Recomposer?,
    private var renderer: PlatformRenderer?,
    private var beforeDispose: (() -> Unit)?,
    private var release: (() -> Unit)?
) : MountedComposition {
    override var isDisposed = false
        private set

    override fun dispose() {
        if (isDisposed) return
        isDisposed = true
        val ownedRecomposer = recomposer!!
        val ownedRenderer = renderer!!
        val detach = beforeDispose!!
        val cleanup = release!!
        recomposer = null
        renderer = null
        beforeDispose = null
        release = null
        var failure: Throwable? = null
        try { detach() } catch (error: Throwable) { failure = error }
        try {
            withPlatformRenderer(ownedRenderer) {
                RecomposerHolder.withRecomposer(ownedRecomposer) {
                    val previous = CompositionLocal.currentComposer
                    CompositionLocal.setCurrentComposer(null)
                    try { ownedRecomposer.dispose() } finally { CompositionLocal.setCurrentComposer(previous) }
                }
            }
        } catch (error: Throwable) {
            if (failure == null) failure = error else failure.addSuppressed(error)
        }
        try { cleanup() } catch (error: Throwable) {
            if (failure == null) failure = error else failure.addSuppressed(error)
        }
        failure?.let { throw it }
    }
}
