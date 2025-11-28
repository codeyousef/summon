package codes.yousef.summon.hydration

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element

/**
 * Priority-based hydration queue with viewport-aware scheduling.
 *
 * This class manages component hydration order based on visibility and
 * developer-assigned priorities. Components in the viewport are hydrated
 * first, followed by nearby components, then below-the-fold content.
 *
 * ## Key Features
 *
 * - **Priority Queues**: Separate queues for CRITICAL, VISIBLE, NEAR, DEFERRED (FR-004, FR-005)
 * - **Viewport Detection**: Uses IntersectionObserver for efficient visibility tracking
 * - **Dynamic Priority**: Components can be promoted when they enter viewport
 * - **Integration**: Works with HydrationScheduler for actual task execution
 *
 * ## Priority Levels
 *
 * 1. **CRITICAL**: Developer-marked must-hydrate-first components
 * 2. **VISIBLE**: Components currently in viewport
 * 3. **NEAR**: Components within 200px of viewport
 * 4. **DEFERRED**: Below-fold components, hydrate on scroll or idle
 *
 * ## Usage
 *
 * ```kotlin
 * val queue = PriorityHydrationQueue.instance
 *
 * // Start observing elements for viewport visibility
 * queue.startObserving()
 *
 * // Enqueue a component for hydration
 * queue.enqueue("button-1", HydrationPriority.VISIBLE) {
 *     hydrateButton(element)
 *     true
 * }
 *
 * // Process queue through scheduler
 * queue.drainToScheduler(HydrationScheduler.instance)
 * ```
 */
class PriorityHydrationQueue private constructor() {

    private val criticalQueue = ArrayDeque<PendingHydration>()
    private val visibleQueue = ArrayDeque<PendingHydration>()
    private val nearQueue = ArrayDeque<PendingHydration>()
    private val deferredQueue = ArrayDeque<PendingHydration>()

    private var isObserving = false
    private val pendingElements = mutableMapOf<String, PendingHydration>()

    /**
     * Enable/disable logging.
     */
    var enableLogging = false

    /**
     * Near viewport threshold in pixels (default 200px per spec).
     */
    var nearThresholdPx: Int = 200

    /**
     * Callback when element enters viewport (for dynamic priority promotion).
     */
    var onElementVisible: ((elementId: String) -> Unit)? = null

    /**
     * Enqueue a component for hydration.
     *
     * @param elementId The DOM element ID
     * @param priority Initial priority (may be promoted later)
     * @param work The hydration work to perform
     */
    fun enqueue(elementId: String, priority: HydrationPriority = HydrationPriority.VISIBLE, work: () -> Boolean) {
        val pending = PendingHydration(elementId, priority, work)

        // Store for potential priority promotion
        pendingElements[elementId] = pending

        // Add to appropriate queue
        when (priority) {
            HydrationPriority.CRITICAL -> criticalQueue.addLast(pending)
            HydrationPriority.VISIBLE -> visibleQueue.addLast(pending)
            HydrationPriority.NEAR -> nearQueue.addLast(pending)
            HydrationPriority.DEFERRED -> deferredQueue.addLast(pending)
        }

        if (enableLogging) {
            console.log("[PriorityHydrationQueue] Enqueued $elementId with priority $priority")
        }
    }

    /**
     * Enqueue a component with automatic priority detection.
     * Uses viewport position to determine initial priority.
     */
    fun enqueueWithAutoDetect(elementId: String, work: () -> Boolean) {
        val priority = detectElementPriority(elementId)
        enqueue(elementId, priority, work)
    }

    /**
     * Detect priority based on element's viewport position.
     */
    fun detectElementPriority(elementId: String): HydrationPriority {
        val element = document.getElementById(elementId) ?: return HydrationPriority.VISIBLE

        // Check for explicit priority attribute
        val explicitPriority = element.getAttribute(HydrationPriority.ATTRIBUTE_NAME)
        if (explicitPriority != null) {
            return HydrationPriority.fromString(explicitPriority)
        }

        return detectElementPriorityFromPosition(element)
    }

    /**
     * Detect priority from element's position relative to viewport.
     */
    private fun detectElementPriorityFromPosition(element: Element): HydrationPriority {
        try {
            val rect = element.getBoundingClientRect()
            val viewportHeight = window.innerHeight.toDouble()
            val viewportWidth = window.innerWidth.toDouble()

            // Check if in viewport
            val isInViewport = rect.top < viewportHeight &&
                    rect.bottom > 0 &&
                    rect.left < viewportWidth &&
                    rect.right > 0

            if (isInViewport) {
                return HydrationPriority.VISIBLE
            }

            // Check if near viewport
            val nearThreshold = nearThresholdPx.toDouble()
            val isNearViewport = rect.top < viewportHeight + nearThreshold &&
                    rect.bottom > -nearThreshold

            if (isNearViewport) {
                return HydrationPriority.NEAR
            }

            return HydrationPriority.DEFERRED
        } catch (e: Exception) {
            return HydrationPriority.VISIBLE
        }
    }

    /**
     * Start observing elements for viewport intersection.
     * Uses scroll-based detection for element visibility.
     */
    fun startObserving() {
        if (isObserving) return
        isObserving = true

        if (enableLogging) {
            console.log("[PriorityHydrationQueue] Scroll-based observation started")
        }

        // Use scroll-based detection (simpler than IntersectionObserver interop)
        window.addEventListener("scroll", { checkVisibleElements() }, js("{ passive: true }").unsafeCast<dynamic>())
    }

    /**
     * Check which observed elements are now visible and promote them.
     */
    private fun checkVisibleElements() {
        observedElementIds.toList().forEach { elementId ->
            val element = document.getElementById(elementId) ?: return@forEach

            if (isElementInViewport(element)) {
                promoteToVisible(elementId)
                observedElementIds.remove(elementId)
                onElementVisible?.invoke(elementId)
            }
        }
    }

    /**
     * Check if element is in viewport.
     */
    private fun isElementInViewport(element: Element): Boolean {
        return try {
            val rect = element.getBoundingClientRect()
            val viewportHeight = window.innerHeight.toDouble()
            val viewportWidth = window.innerWidth.toDouble()

            rect.top < viewportHeight &&
                    rect.bottom > 0 &&
                    rect.left < viewportWidth &&
                    rect.right > 0
        } catch (e: Exception) {
            false
        }
    }

    private val observedElementIds = mutableSetOf<String>()

    /**
     * Observe an element for viewport intersection.
     */
    fun observeElement(elementId: String) {
        observedElementIds.add(elementId)
    }

    /**
     * Stop observing an element.
     */
    fun unobserveElement(elementId: String) {
        observedElementIds.remove(elementId)
    }

    /**
     * Promote an element to VISIBLE priority.
     * Removes from current queue and adds to visible queue.
     */
    fun promoteToVisible(elementId: String) {
        val pending = pendingElements[elementId] ?: return

        // Only promote if currently lower priority
        if (pending.priority.value <= HydrationPriority.VISIBLE.value) {
            return
        }

        // Remove from current queue
        when (pending.priority) {
            HydrationPriority.NEAR -> nearQueue.removeAll { it.elementId == elementId }
            HydrationPriority.DEFERRED -> deferredQueue.removeAll { it.elementId == elementId }
            else -> return
        }

        // Update priority and add to visible queue
        val promoted = pending.copy(priority = HydrationPriority.VISIBLE)
        pendingElements[elementId] = promoted
        visibleQueue.addLast(promoted)

        if (enableLogging) {
            console.log("[PriorityHydrationQueue] Promoted $elementId to VISIBLE")
        }
    }

    /**
     * Drain all queues to the scheduler in priority order.
     */
    fun drainToScheduler(scheduler: HydrationScheduler) {
        var count = 0

        // Drain in priority order: CRITICAL -> VISIBLE -> NEAR -> DEFERRED
        while (criticalQueue.isNotEmpty()) {
            val pending = criticalQueue.removeFirst()
            scheduleTask(scheduler, pending)
            count++
        }

        while (visibleQueue.isNotEmpty()) {
            val pending = visibleQueue.removeFirst()
            scheduleTask(scheduler, pending)
            count++
        }

        while (nearQueue.isNotEmpty()) {
            val pending = nearQueue.removeFirst()
            scheduleTask(scheduler, pending)
            count++
        }

        while (deferredQueue.isNotEmpty()) {
            val pending = deferredQueue.removeFirst()
            scheduleTask(scheduler, pending)
            count++
        }

        if (enableLogging && count > 0) {
            console.log("[PriorityHydrationQueue] Drained $count tasks to scheduler")
        }

        // Clear pending elements map
        pendingElements.clear()
    }

    /**
     * Schedule a single pending hydration task.
     */
    private fun scheduleTask(scheduler: HydrationScheduler, pending: PendingHydration) {
        scheduler.scheduleTask(SimpleHydrationTask(
            id = "component-${pending.elementId}",
            priority = pending.priority,
            work = pending.work
        ))
    }

    /**
     * Get total count of pending hydrations.
     */
    fun totalPendingCount(): Int {
        return criticalQueue.size + visibleQueue.size + nearQueue.size + deferredQueue.size
    }

    /**
     * Get counts by priority.
     */
    fun pendingCountsByPriority(): Map<HydrationPriority, Int> {
        return mapOf(
            HydrationPriority.CRITICAL to criticalQueue.size,
            HydrationPriority.VISIBLE to visibleQueue.size,
            HydrationPriority.NEAR to nearQueue.size,
            HydrationPriority.DEFERRED to deferredQueue.size
        )
    }

    /**
     * Clear all queues.
     */
    fun clear() {
        criticalQueue.clear()
        visibleQueue.clear()
        nearQueue.clear()
        deferredQueue.clear()
        pendingElements.clear()
    }

    /**
     * Stop observation and clean up.
     */
    fun stopObserving() {
        isObserving = false
        observedElementIds.clear()
    }

    companion object {
        /**
         * Singleton instance.
         */
        val instance: PriorityHydrationQueue by lazy { PriorityHydrationQueue() }

        /**
         * Create a new instance (useful for testing).
         */
        fun create(): PriorityHydrationQueue = PriorityHydrationQueue()
    }
}

/**
 * Data class for pending hydration work.
 */
private data class PendingHydration(
    val elementId: String,
    val priority: HydrationPriority,
    val work: () -> Boolean
)

/**
 * External console for logging.
 */
private external val console: PriorityQueueConsole

private external interface PriorityQueueConsole {
    fun log(message: String)
    fun error(message: String)
}
