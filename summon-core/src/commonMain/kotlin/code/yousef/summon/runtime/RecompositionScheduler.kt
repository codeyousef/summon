package code.yousef.summon.runtime

/**
 * Interface for scheduling recomposition work.
 * Different platforms can provide their own implementations.
 */
interface RecompositionScheduler {
    /**
     * Schedules work to be executed asynchronously.
     * The work should be executed as soon as possible, typically
     * on the next frame or microtask.
     */
    fun scheduleRecomposition(work: () -> Unit)
}

/**
 * Default scheduler that executes work immediately.
 * This is mainly for testing and should be replaced with
 * platform-specific implementations.
 */
class ImmediateScheduler : RecompositionScheduler {
    override fun scheduleRecomposition(work: () -> Unit) {
        work()
    }
}

/**
 * Platform-specific function to create the default scheduler.
 */
expect fun createDefaultScheduler(): RecompositionScheduler