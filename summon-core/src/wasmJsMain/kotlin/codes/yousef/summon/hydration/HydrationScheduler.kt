package codes.yousef.summon.hydration

import codes.yousef.summon.hydration.HydrationPriority
import codes.yousef.summon.hydration.HydrationTask

/**
 * Non-blocking scheduler for hydration tasks (WASM version).
 *
 * Uses `requestIdleCallback` to schedule hydration work during browser idle periods,
 * falling back to `requestAnimationFrame` with manual time slicing when idle callback
 * is not available.
 *
 * ## Key Features
 *
 * - **Non-blocking**: Never blocks main thread for more than 50ms (FR-001)
 * - **Priority-aware**: Processes higher priority tasks first (FR-004, FR-005)
 * - **Graceful degradation**: Falls back to RAF when idle callback unavailable
 * - **Error isolation**: Task failures don't affect other tasks (FR-007)
 *
 * ## Usage
 *
 * ```kotlin
 * val scheduler = HydrationScheduler.instance
 *
 * // Schedule a hydration task
 * scheduler.scheduleTask(hydrationTask("button-1", HydrationPriority.CRITICAL) {
 *     attachClickHandler(element)
 *     true
 * })
 *
 * // Start processing (if not already started)
 * scheduler.start()
 * ```
 */
class HydrationScheduler private constructor() {

    private val taskQueue = ArrayDeque<HydrationTask>()
    private var isProcessing = false
    private var currentHandle: Int? = null
    private var useIdleCallback = isIdleCallbackSupported()
    private var scrollPauseUntil: Double = 0.0
    private var scrollListenerAttached = false

    /**
     * Maximum time to spend processing tasks before yielding (in milliseconds).
     * Per FR-001: Must not block main thread for more than 50ms.
     */
    var maxBlockingTimeMs: Double = 50.0

    /**
     * Minimum time remaining in idle period to start a new task (in milliseconds).
     */
    var minTimeForNewTask: Double = 5.0

    /**
     * Timeout for idle callback when there are tasks waiting (in milliseconds).
     * Ensures tasks eventually run even if browser is busy.
     */
    var idleTimeout: Int = 1000

    /**
     * Duration to pause hydration after scroll events (in milliseconds).
     * This ensures smooth scrolling at 60fps by not competing for main thread time.
     */
    var scrollPauseDurationMs: Double = 100.0

    /**
     * Enable/disable scroll-aware yielding.
     */
    var enableScrollYielding = true

    /**
     * Enable/disable performance logging.
     */
    var enableLogging = false

    /**
     * Enable synchronous mode for testing.
     *
     * When true, tasks are executed immediately when scheduled instead of being
     * queued for idle callback processing. This is useful for testing where
     * assertions need to check state immediately after scheduling.
     *
     * Default: false (use async scheduling for production performance)
     */
    var syncMode = false

    /**
     * Callback invoked when all tasks are complete.
     */
    var onAllTasksComplete: (() -> Unit)? = null

    /**
     * Schedule a hydration task for execution.
     *
     * Tasks are added to a priority queue and processed during idle periods.
     * If the scheduler is not running, it will start automatically.
     *
     * In sync mode, tasks are executed immediately instead of being queued.
     *
     * @param task The hydration task to schedule
     */
    fun scheduleTask(task: HydrationTask) {
        if (enableLogging) {
            wasmConsoleLog("[HydrationScheduler] Scheduling task: ${task.id} (priority: ${task.priority}, syncMode: $syncMode)")
        }

        // In sync mode, execute immediately
        if (syncMode) {
            executeTask(task)
            return
        }

        // Insert task in priority order
        val insertIndex = taskQueue.indexOfFirst { it.priority.value > task.priority.value }
        if (insertIndex == -1) {
            taskQueue.addLast(task)
        } else {
            // ArrayDeque doesn't have add(index, element), so we need to rebuild
            val temp = ArrayDeque<HydrationTask>()
            taskQueue.forEachIndexed { index, existingTask ->
                if (index == insertIndex) temp.addLast(task)
                temp.addLast(existingTask)
            }
            if (insertIndex == taskQueue.size) temp.addLast(task)
            taskQueue.clear()
            temp.forEach { taskQueue.addLast(it) }
        }

        if (enableLogging) {
            wasmConsoleLog("[HydrationScheduler] Scheduled task: ${task.id} (priority: ${task.priority})")
        }

        // Start processing if not already running
        if (!isProcessing) {
            start()
        }
    }

    /**
     * Start processing scheduled tasks.
     */
    fun start() {
        if (isProcessing) return
        isProcessing = true

        // Attach scroll listener for scroll-aware yielding
        if (enableScrollYielding && !scrollListenerAttached) {
            attachScrollListener()
        }

        if (enableLogging) {
            wasmConsoleLog("[HydrationScheduler] Starting with ${taskQueue.size} tasks (idleCallback: $useIdleCallback, scrollYielding: $enableScrollYielding)")
        }

        scheduleNextBatch()
    }

    /**
     * Attach scroll listener to pause hydration during rapid scrolling.
     * This ensures smooth 60fps scrolling by yielding main thread to scroll events.
     */
    private fun attachScrollListener() {
        scrollListenerAttached = true
        wasmAddScrollListener {
            if (enableScrollYielding) {
                scrollPauseUntil = performanceNow() + scrollPauseDurationMs
                if (enableLogging) {
                    wasmConsoleLog("[HydrationScheduler] Scroll detected, pausing hydration for ${scrollPauseDurationMs.toInt()}ms")
                }
            }
        }
    }

    /**
     * Check if hydration should be paused due to recent scrolling.
     */
    private fun isScrollPaused(): Boolean {
        return enableScrollYielding && performanceNow() < scrollPauseUntil
    }

    /**
     * Stop processing and clear all pending tasks.
     */
    fun stop() {
        isProcessing = false
        currentHandle?.let {
            if (useIdleCallback) {
                wasmCancelIdleCallback(it)
            } else {
                wasmCancelAnimationFrame(it)
            }
        }
        currentHandle = null
        taskQueue.clear()
    }

    /**
     * Check if there are pending tasks.
     */
    fun hasPendingTasks(): Boolean = taskQueue.isNotEmpty()

    /**
     * Get the number of pending tasks.
     */
    fun pendingTaskCount(): Int = taskQueue.size

    private fun scheduleNextBatch() {
        if (!isProcessing || taskQueue.isEmpty()) {
            isProcessing = false
            if (enableLogging) {
                wasmConsoleLog("[HydrationScheduler] All tasks complete")
            }
            onAllTasksComplete?.invoke()
            return
        }

        if (useIdleCallback) {
            currentHandle = wasmRequestIdleCallback(
                { deadline -> processTasksUntilDeadline(deadline) },
                idleTimeout
            )
        } else {
            // Fallback to requestAnimationFrame with manual time slicing
            currentHandle = wasmRequestAnimationFrame { processTasksWithTimeSlice() }
        }
    }

    /**
     * Process tasks using the idle deadline to know when to yield.
     * Used when requestIdleCallback is available.
     */
    private fun processTasksUntilDeadline(deadline: IdleDeadline) {
        // Check if we should pause for scrolling
        if (isScrollPaused()) {
            if (enableLogging) {
                wasmConsoleLog("[HydrationScheduler] Paused for scroll, rescheduling")
            }
            scheduleNextBatch()
            return
        }

        val startTime = performanceNow()
        var tasksProcessed = 0

        while (taskQueue.isNotEmpty()) {
            val timeRemaining = deadline.timeRemainingDouble()

            // Yield if not enough time for another task
            if (timeRemaining < minTimeForNewTask) {
                if (enableLogging) {
                    wasmConsoleLog("[HydrationScheduler] Yielding (timeRemaining: ${timeRemaining}ms, processed: $tasksProcessed)")
                }
                break
            }

            // Check for scroll during processing
            if (isScrollPaused()) {
                if (enableLogging) {
                    wasmConsoleLog("[HydrationScheduler] Scroll detected mid-batch, yielding (processed: $tasksProcessed)")
                }
                break
            }

            // Process one task
            val task = taskQueue.removeFirst()
            executeTask(task)
            tasksProcessed++
        }

        val elapsed = performanceNow() - startTime
        if (enableLogging && tasksProcessed > 0) {
            wasmConsoleLog("[HydrationScheduler] Batch complete: $tasksProcessed tasks in ${elapsed.toInt()}ms")
        }

        // Schedule next batch if there are more tasks
        scheduleNextBatch()
    }

    /**
     * Process tasks using manual time slicing.
     * Used as fallback when requestIdleCallback is not available.
     */
    private fun processTasksWithTimeSlice() {
        // Check if we should pause for scrolling
        if (isScrollPaused()) {
            if (enableLogging) {
                wasmConsoleLog("[HydrationScheduler] Paused for scroll, rescheduling")
            }
            scheduleNextBatch()
            return
        }

        val startTime = performanceNow()
        var tasksProcessed = 0

        while (taskQueue.isNotEmpty()) {
            val elapsed = performanceNow() - startTime

            // Yield if we've used our time budget
            if (elapsed >= maxBlockingTimeMs) {
                if (enableLogging) {
                    wasmConsoleLog("[HydrationScheduler] Yielding (elapsed: ${elapsed.toInt()}ms, processed: $tasksProcessed)")
                }
                break
            }

            // Check for scroll during processing
            if (isScrollPaused()) {
                if (enableLogging) {
                    wasmConsoleLog("[HydrationScheduler] Scroll detected mid-batch, yielding (processed: $tasksProcessed)")
                }
                break
            }

            // Process one task
            val task = taskQueue.removeFirst()
            executeTask(task)
            tasksProcessed++
        }

        val elapsed = performanceNow() - startTime
        if (enableLogging && tasksProcessed > 0) {
            wasmConsoleLog("[HydrationScheduler] Batch complete: $tasksProcessed tasks in ${elapsed.toInt()}ms")
        }

        // Schedule next batch if there are more tasks
        scheduleNextBatch()
    }

    /**
     * Execute a single task with error handling and performance timing.
     * Marks the target element as hydrated after successful completion.
     */
    private fun executeTask(task: HydrationTask) {
        val taskStart = performanceNow()
        try {
            val success = task.execute()
            val taskDuration = performanceNow() - taskStart

            if (enableLogging) {
                wasmConsoleLog("[HydrationScheduler] Task ${task.id} completed in ${taskDuration.toInt()}ms (success: $success)")
            }

            // Warn if individual task takes too long (> 10ms is concerning)
            if (taskDuration > 10.0) {
                wasmConsoleLog("[HydrationScheduler] WARNING: Task ${task.id} took ${taskDuration.toInt()}ms - consider splitting")
            }

            if (success) {
                task.onComplete()

                // Mark element as hydrated and replay buffered events
                // The task ID typically follows the pattern "component-<elementId>" or "click-handler-<elementId>"
                val elementId = extractElementIdFromTaskId(task.id)
                if (elementId != null) {
                    GlobalEventListener.markElementHydrated(elementId)
                }
            } else {
                task.onError(null)
            }
        } catch (e: Throwable) {
            val taskDuration = performanceNow() - taskStart
            wasmConsoleError("[HydrationScheduler] Task ${task.id} failed after ${taskDuration.toInt()}ms: ${e.message}")
            task.onError(e)
        }
    }

    /**
     * Extract element ID from task ID.
     * Task IDs follow patterns like "component-<elementId>" or "click-handler-<elementId>".
     */
    private fun extractElementIdFromTaskId(taskId: String): String? {
        return when {
            taskId.startsWith("component-") -> taskId.removePrefix("component-")
            taskId.startsWith("click-handler-") -> taskId.removePrefix("click-handler-")
            else -> null
        }
    }

    companion object {
        /**
         * Singleton instance of the HydrationScheduler.
         */
        val instance: HydrationScheduler by lazy { HydrationScheduler() }

        /**
         * Create a new scheduler instance (useful for testing).
         */
        fun create(): HydrationScheduler = HydrationScheduler()
    }
}

/**
 * Console logging for WASM.
 */
@JsFun("(msg) => console.log(msg)")
external fun wasmConsoleLog(message: String)

@JsFun("(msg) => console.error(msg)")
external fun wasmConsoleError(message: String)
