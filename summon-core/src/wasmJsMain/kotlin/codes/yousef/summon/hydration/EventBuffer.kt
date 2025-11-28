package codes.yousef.summon.hydration

/**
 * Buffer for capturing user events during hydration (WASM version).
 *
 * When components are not yet hydrated, user interactions (clicks, etc.)
 * would normally be lost. EventBuffer captures these events and replays
 * them once the target component is hydrated.
 *
 * ## Key Features
 *
 * - **Event Capture**: Stores events for non-hydrated elements (FR-002)
 * - **Event Replay**: Replays buffered events after hydration
 * - **Staleness Detection**: Discards events older than configurable threshold
 * - **Per-Element Buffering**: Events are stored by target element ID
 *
 * ## Usage
 *
 * ```kotlin
 * val buffer = EventBuffer.instance
 *
 * // During hydration, capture events for non-hydrated elements
 * buffer.captureEvent(CapturedEvent(
 *     type = CapturedEvent.TYPE_CLICK,
 *     targetId = "button-1",
 *     timestamp = currentTimeMillis()
 * ))
 *
 * // After element is hydrated, replay its events
 * buffer.replayEventsFor("button-1") { event ->
 *     // Dispatch the event to the now-hydrated element
 *     dispatchEventToElement(event)
 * }
 * ```
 */
class EventBuffer private constructor() {

    private val eventsByTarget = mutableMapOf<String, ArrayDeque<CapturedEvent>>()

    /**
     * Maximum age for events before they're considered stale (in milliseconds).
     */
    var maxEventAgeMs: Long = CapturedEvent.DEFAULT_MAX_AGE_MS

    /**
     * Maximum number of events to buffer per target element.
     */
    var maxEventsPerTarget: Int = 10

    /**
     * Enable/disable logging.
     */
    var enableLogging = false

    /**
     * Capture an event for later replay.
     *
     * @param event The event to capture
     */
    fun captureEvent(event: CapturedEvent) {
        val queue = eventsByTarget.getOrPut(event.targetId) { ArrayDeque() }

        // Enforce per-target limit
        while (queue.size >= maxEventsPerTarget) {
            val removed = queue.removeFirst()
            if (enableLogging) {
                wasmConsoleLog("[EventBuffer] Dropped oldest event for ${event.targetId}: ${removed.type}")
            }
        }

        queue.addLast(event)

        if (enableLogging) {
            wasmConsoleLog("[EventBuffer] Captured ${event.type} for ${event.targetId} (queued: ${queue.size})")
        }
    }

    /**
     * Replay all buffered events for a target element.
     *
     * Stale events are automatically discarded. Events are removed from the
     * buffer after replay.
     *
     * @param targetId The element ID to replay events for
     * @param handler Function to handle each replayed event
     */
    fun replayEventsFor(targetId: String, handler: (CapturedEvent) -> Unit) {
        val queue = eventsByTarget.remove(targetId) ?: return

        val currentTime = currentTimeMillis()
        var replayedCount = 0
        var staleCount = 0

        while (queue.isNotEmpty()) {
            val event = queue.removeFirst()

            if (event.isStale(maxEventAgeMs, currentTime)) {
                staleCount++
                if (enableLogging) {
                    wasmConsoleLog("[EventBuffer] Discarded stale ${event.type} for $targetId")
                }
                continue
            }

            try {
                handler(event)
                replayedCount++
            } catch (e: Throwable) {
                wasmConsoleError("[EventBuffer] Error replaying ${event.type} for $targetId: ${e.message}")
            }
        }

        if (enableLogging && (replayedCount > 0 || staleCount > 0)) {
            wasmConsoleLog("[EventBuffer] Replayed $replayedCount events for $targetId (discarded $staleCount stale)")
        }
    }

    /**
     * Check if there are buffered events for a target element.
     */
    fun hasEventsFor(targetId: String): Boolean {
        return eventsByTarget[targetId]?.isNotEmpty() == true
    }

    /**
     * Get the count of buffered events for a target element.
     */
    fun eventCountFor(targetId: String): Int {
        return eventsByTarget[targetId]?.size ?: 0
    }

    /**
     * Get total count of all buffered events.
     */
    fun totalEventCount(): Int {
        return eventsByTarget.values.sumOf { it.size }
    }

    /**
     * Clear all buffered events for a target element.
     */
    fun clearEventsFor(targetId: String) {
        eventsByTarget.remove(targetId)
    }

    /**
     * Clear all buffered events.
     */
    fun clear() {
        eventsByTarget.clear()
    }

    /**
     * Clean up stale events across all targets.
     */
    fun pruneStaleEvents() {
        val currentTime = currentTimeMillis()
        var prunedCount = 0

        eventsByTarget.entries.toList().forEach { (targetId, queue) ->
            val originalSize = queue.size
            val fresh = ArrayDeque<CapturedEvent>()

            while (queue.isNotEmpty()) {
                val event = queue.removeFirst()
                if (!event.isStale(maxEventAgeMs, currentTime)) {
                    fresh.addLast(event)
                } else {
                    prunedCount++
                }
            }

            if (fresh.isEmpty()) {
                eventsByTarget.remove(targetId)
            } else {
                eventsByTarget[targetId] = fresh
            }
        }

        if (enableLogging && prunedCount > 0) {
            wasmConsoleLog("[EventBuffer] Pruned $prunedCount stale events")
        }
    }

    companion object {
        /**
         * Singleton instance of the EventBuffer.
         */
        val instance: EventBuffer by lazy { EventBuffer() }

        /**
         * Create a new buffer instance (useful for testing).
         */
        fun create(): EventBuffer = EventBuffer()
    }
}

/**
 * Get current time in milliseconds.
 */
private fun currentTimeMillis(): Long {
    return wasmDateNow().toLong()
}

/**
 * Get current timestamp from JavaScript Date.now().
 */
@JsFun("() => Date.now()")
private external fun wasmDateNow(): Double
