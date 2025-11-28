package codes.yousef.summon.hydration

/**
 * Represents a unit of work for hydrating a single component or component tree.
 *
 * HydrationTasks are scheduled and executed by the HydrationScheduler, which
 * ensures they don't block the main thread for more than 50ms at a time.
 *
 * ## Example Implementation
 *
 * ```kotlin
 * class ComponentHydrationTask(
 *     private val componentId: String,
 *     private val element: Element,
 *     override val priority: HydrationPriority = HydrationPriority.VISIBLE
 * ) : HydrationTask {
 *
 *     override val id: String = "hydrate-$componentId"
 *
 *     override fun execute(): Boolean {
 *         return try {
 *             attachEventListeners(element)
 *             restoreState(componentId)
 *             true
 *         } catch (e: Exception) {
 *             console.error("Failed to hydrate $componentId: ${e.message}")
 *             false
 *         }
 *     }
 * }
 * ```
 */
interface HydrationTask {
    /**
     * Unique identifier for this task.
     * Used for tracking, debugging, and preventing duplicate task execution.
     */
    val id: String

    /**
     * Priority level for this task.
     * Higher priority tasks (lower ordinal value) are executed first.
     */
    val priority: HydrationPriority

    /**
     * Execute the hydration work for this task.
     *
     * This method should complete quickly (ideally <10ms) to allow
     * the scheduler to yield to the browser between tasks.
     *
     * @return true if hydration succeeded, false if it failed
     */
    fun execute(): Boolean

    /**
     * Optional callback invoked when hydration completes successfully.
     * Override to replay buffered events or trigger dependent work.
     */
    fun onComplete() {}

    /**
     * Optional callback invoked when hydration fails.
     * Override to handle errors gracefully (log, retry, fallback).
     *
     * @param error The exception that caused the failure, if any
     */
    fun onError(error: Throwable?) {}
}

/**
 * Simple implementation of HydrationTask using a lambda for the work.
 *
 * @param id Unique identifier for this task
 * @param priority Priority level for scheduling
 * @param work The actual hydration work to perform
 */
class SimpleHydrationTask(
    override val id: String,
    override val priority: HydrationPriority = HydrationPriority.VISIBLE,
    private val work: () -> Boolean
) : HydrationTask {

    override fun execute(): Boolean = work()
}

/**
 * Factory function for creating simple hydration tasks.
 *
 * @param id Unique identifier for this task
 * @param priority Priority level for scheduling
 * @param work The actual hydration work to perform
 */
fun hydrationTask(
    id: String,
    priority: HydrationPriority = HydrationPriority.VISIBLE,
    work: () -> Boolean
): HydrationTask = SimpleHydrationTask(id, priority, work)
