package codes.yousef.summon

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.hydration.GlobalEventListener
import codes.yousef.summon.runtime.*

/**
 * Renders a composable function to the DOM element with the specified ID.
 * This is the main entry point for WASM applications.
 */
fun renderComposableRoot(rootElementId: String, composable: @Composable () -> Unit) {
    GlobalEventListener.init()
    // Use the renderer's owned root pass, including placement tracking and child
    // reconciliation, instead of composing directly outside its container context.
    PlatformRenderer().mountComposableRoot(rootElementId, composable)
}

/**
 * Hydrates a composable function to the DOM element with the specified ID.
 * This is used when the HTML has been pre-rendered by the server.
 * Also initializes the GlobalEventListener for handling data-action
 * events like HamburgerMenu toggle.
 */
fun hydrateComposableRoot(rootElementId: String, composable: @Composable () -> Unit) {
    wasmConsoleLog("hydrateComposableRoot called for element: $rootElementId")
    
    try {
        // Initialize global event listener for data-action handling
        GlobalEventListener.init()
        
        val renderer = PlatformRenderer()
        renderer.hydrateComposableRoot(rootElementId, composable)
    } catch (e: Exception) {
        wasmConsoleError("Error in hydrateComposableRoot: ${e.message}")
        // Fallback to client-side rendering
        renderComposableRoot(rootElementId, composable)
    }
}

private val mountedRoots = mutableMapOf<String, MountedComposition>()
private val mountingRoots = mutableSetOf<String>()
private val mountedRenderers = mutableSetOf<PlatformRenderer>()

actual fun mountComposableRoot(
    rootElementId: String,
    scheduler: RecompositionScheduler?,
    composable: @Composable () -> Unit
): MountedComposition {
    GlobalEventListener.init()
    return mountWasmRoot(PlatformRenderer(), rootElementId, scheduler, composable)
}

internal fun mountWasmRoot(
    renderer: PlatformRenderer,
    rootElementId: String,
    scheduler: RecompositionScheduler?,
    composable: @Composable () -> Unit
): MountedComposition {
    val nativeId = wasmGetElementById(rootElementId)
        ?: throw IllegalArgumentException("Root element not found: $rootElementId")
    check(mountingRoots.add(nativeId)) { "Cannot replace a root during its mounting or cleanup" }
    try {
        mountedRoots.remove(nativeId)?.dispose()
        check(mountedRenderers.add(renderer)) { "A renderer cannot own multiple browser roots" }
        var disposalGuard = false
        var owner: MountedComposition? = null
        try {
            owner = createOwnedComposition(renderer, scheduler, release = {
                try { renderer.releaseMountedElements() } finally {
                    mountedRenderers.remove(renderer)
                    if (disposalGuard) mountingRoots.remove(nativeId)
                }
            }, beforeDispose = {
                if (mountedRoots[nativeId] === owner) mountedRoots.remove(nativeId)
                disposalGuard = mountingRoots.add(nativeId)
            }, root = renderer.mountedRoot(rootElementId, composable))
            mountedRoots[nativeId] = owner
            return owner
        } catch (error: Throwable) {
            mountedRenderers.remove(renderer)
            throw error
        }
    } finally { mountingRoots.remove(nativeId) }
}
