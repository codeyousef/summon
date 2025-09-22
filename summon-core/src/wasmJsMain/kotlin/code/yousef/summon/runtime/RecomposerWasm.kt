package code.yousef.summon.runtime

import code.yousef.summon.runtime.wasmConsoleLog

/**
 * WASM implementation of thread-safe addition to pending recompositions.
 * Since WASM is single-threaded, no synchronization is needed.
 */
actual fun Recomposer.addToPendingRecompositions(composer: Composer) {
    wasmConsoleLog("Recomposer.addToPendingRecompositions - Adding composer")
    // Access the internal pending recompositions set directly
    // Since WASM is single-threaded, we don't need synchronization
    getPendingRecompositions().add(composer)
}

/**
 * WASM implementation of thread-safe retrieval and clearing of pending recompositions.
 * Since WASM is single-threaded, no synchronization is needed.
 */
actual fun Recomposer.getAndClearPendingRecompositions(): List<Composer> {
    wasmConsoleLog("Recomposer.getAndClearPendingRecompositions - Getting pending composers")
    // Get the pending recompositions and convert to list
    val pending = getPendingRecompositions().toList()
    wasmConsoleLog("Found ${pending.size} pending composers")
    // Clear the set after getting the list
    getPendingRecompositions().clear()
    return pending
}