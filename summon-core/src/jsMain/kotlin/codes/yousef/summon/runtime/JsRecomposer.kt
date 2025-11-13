package codes.yousef.summon.runtime

/**
 * JS-specific extension methods for Recomposer to provide thread safety.
 * JS is single-threaded, so we don't need actual synchronization.
 */

/**
 * Add a composer to the pending recompositions set.
 * Since JS is single-threaded, we don't need synchronization.
 */
actual fun Recomposer.addToPendingRecompositions(composer: Composer) {
    val pendingRecompositions = this.getPendingRecompositions()
    pendingRecompositions.add(composer)
}

/**
 * Get a copy of the pending recompositions set and clear the original.
 * Since JS is single-threaded, we don't need synchronization.
 */
actual fun Recomposer.getAndClearPendingRecompositions(): List<Composer> {
    val pendingRecompositions = this.getPendingRecompositions()
    val copy = pendingRecompositions.toList()
    pendingRecompositions.clear()
    return copy
} 