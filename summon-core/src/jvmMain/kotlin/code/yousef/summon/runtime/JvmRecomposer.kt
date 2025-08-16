package code.yousef.summon.runtime

/**
 * JVM-specific extension methods for Recomposer to provide thread safety.
 */

/**
 * Add a composer to the pending recompositions set in a thread-safe way using JVM synchronization.
 */
actual fun Recomposer.addToPendingRecompositions(composer: Composer) {
    synchronized(this) {
        val pendingRecompositions = this.getPendingRecompositions()
        pendingRecompositions.add(composer)
    }
}

/**
 * Get a copy of the pending recompositions set and clear the original in a thread-safe way.
 */
actual fun Recomposer.getAndClearPendingRecompositions(): List<Composer> {
    synchronized(this) {
        val pendingRecompositions = this.getPendingRecompositions()
        val copy = pendingRecompositions.toList()
        pendingRecompositions.clear()
        return copy
    }
} 