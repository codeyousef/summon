package codes.yousef.summon

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.RecompositionScheduler
import codes.yousef.summon.runtime.createOwnedComposition
import org.w3c.dom.HTMLElement

private val mountedRoots = mutableMapOf<HTMLElement, MountedComposition>()
private val mountingRoots = mutableSetOf<HTMLElement>()
private val mountedRenderers = mutableSetOf<PlatformRenderer>()

/** Compatibility entry point. The container owns its mount until replaced or explicitly disposed. */
fun renderComposable(renderer: PlatformRenderer, composable: @Composable () -> Unit, container: HTMLElement) {
    mountInto(renderer, container, null, composable)
}

internal fun mountInto(
    renderer: PlatformRenderer,
    container: HTMLElement,
    scheduler: RecompositionScheduler?,
    composable: @Composable () -> Unit
): MountedComposition {
    check(mountingRoots.add(container)) { "Cannot replace a root during its mounting or cleanup" }
    try {
        mountedRoots.remove(container)?.dispose()
        check(mountedRenderers.add(renderer)) { "A renderer cannot own multiple browser roots" }
        container.innerHTML = ""
        var disposalGuard = false
        var owner: MountedComposition? = null
        try {
            owner = createOwnedComposition(renderer, scheduler, release = {
                try { renderer.releaseMountedElements() } finally {
                    mountedRenderers.remove(renderer)
                    if (disposalGuard) mountingRoots.remove(container)
                }
            }, beforeDispose = {
                if (mountedRoots[container] === owner) mountedRoots.remove(container)
                disposalGuard = mountingRoots.add(container)
            }) { renderer.renderInto(container, composable) }
            mountedRoots[container] = owner
            return owner
        } catch (error: Throwable) {
            mountedRenderers.remove(renderer)
            throw error
        }
    } finally { mountingRoots.remove(container) }
}
