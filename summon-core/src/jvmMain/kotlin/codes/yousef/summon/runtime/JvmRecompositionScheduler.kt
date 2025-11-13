package codes.yousef.summon.runtime

import kotlinx.coroutines.*
import java.util.concurrent.Executors

/**
 * JVM implementation of the RecompositionScheduler.
 * Uses coroutines for scheduling recomposition work.
 */
class JvmRecompositionScheduler : RecompositionScheduler {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var scheduledJob: Job? = null

    override fun scheduleRecomposition(work: () -> Unit) {
        // Cancel any previously scheduled work
        scheduledJob?.cancel()

        // Schedule new work
        scheduledJob = scope.launch {
            // Yield to allow other coroutines to run first
            yield()
            work()
        }
    }
}

/**
 * Alternative scheduler using a dedicated thread pool.
 * This can be useful for server-side rendering or testing.
 */
class ThreadPoolScheduler : RecompositionScheduler {
    private val executor = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "Recomposer-Thread").apply {
            isDaemon = true
        }
    }

    override fun scheduleRecomposition(work: () -> Unit) {
        executor.execute(work)
    }

    fun shutdown() {
        executor.shutdown()
    }
}

/**
 * Creates the default scheduler for the JVM platform.
 * For now, uses immediate execution since we don't have a UI thread concept.
 * In a real UI application, this would use the UI thread dispatcher.
 */
actual fun createDefaultScheduler(): RecompositionScheduler = ImmediateScheduler()