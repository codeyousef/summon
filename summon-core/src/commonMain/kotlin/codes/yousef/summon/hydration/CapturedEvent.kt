package codes.yousef.summon.hydration

/**
 * Represents a user event captured during hydration for later replay.
 *
 * When a user interacts with a component that hasn't been hydrated yet,
 * the event is captured and stored in the EventBuffer. Once the component
 * is hydrated, these events are replayed to ensure no user interactions are lost.
 *
 * ## Captured Event Data
 *
 * - **type**: The event type (click, input, change, submit, etc.)
 * - **targetId**: The ID or selector of the event target element
 * - **timestamp**: When the event occurred (for ordering and staleness checks)
 * - **data**: Additional event-specific data needed for replay
 *
 * ## Example Usage
 *
 * ```kotlin
 * // Capturing an event during hydration
 * val captured = CapturedEvent(
 *     type = "click",
 *     targetId = "checkout-button",
 *     timestamp = currentTimeMillis(),
 *     data = mapOf("clientX" to 100, "clientY" to 200)
 * )
 *
 * // Later, when component is hydrated, replay the event
 * EventBuffer.replayEventsFor("checkout-button") { event ->
 *     handleClick(event)
 * }
 * ```
 */
data class CapturedEvent(
    /**
     * The type of event (e.g., "click", "input", "change", "submit").
     */
    val type: String,

    /**
     * The ID or data-sid of the target element.
     * Used to match events to their target components after hydration.
     */
    val targetId: String,

    /**
     * Timestamp when the event was captured (milliseconds since epoch).
     * Used for ordering events and detecting stale events.
     */
    val timestamp: Long,

    /**
     * Additional event-specific data needed for replay.
     * For click events: coordinates, button, modifiers
     * For input events: value, selectionStart, selectionEnd
     * For form events: form data
     */
    val data: Map<String, Any?> = emptyMap()
) {
    /**
     * Check if this event is stale (captured too long ago).
     *
     * @param maxAgeMs Maximum age in milliseconds before event is considered stale
     * @param currentTime Current time in milliseconds
     * @return true if the event is older than maxAgeMs
     */
    fun isStale(maxAgeMs: Long = DEFAULT_MAX_AGE_MS, currentTime: Long): Boolean {
        return (currentTime - timestamp) > maxAgeMs
    }

    companion object {
        /**
         * Default maximum age for captured events (5 seconds).
         * Events older than this are considered stale and may be discarded.
         */
        const val DEFAULT_MAX_AGE_MS = 5000L

        /**
         * Event type constants for consistency.
         */
        const val TYPE_CLICK = "click"
        const val TYPE_INPUT = "input"
        const val TYPE_CHANGE = "change"
        const val TYPE_SUBMIT = "submit"
        const val TYPE_FOCUS = "focus"
        const val TYPE_BLUR = "blur"

        /**
         * Data keys for event-specific information.
         */
        const val DATA_CLIENT_X = "clientX"
        const val DATA_CLIENT_Y = "clientY"
        const val DATA_VALUE = "value"
        const val DATA_CHECKED = "checked"
        const val DATA_BUTTON = "button"
        const val DATA_SHIFT_KEY = "shiftKey"
        const val DATA_CTRL_KEY = "ctrlKey"
        const val DATA_ALT_KEY = "altKey"
        const val DATA_META_KEY = "metaKey"
    }
}

/**
 * Result of attempting to replay a captured event.
 */
enum class EventReplayResult {
    /** Event was successfully replayed. */
    SUCCESS,

    /** Event was stale and discarded. */
    STALE,

    /** Target element not found. */
    TARGET_NOT_FOUND,

    /** Replay failed due to an error. */
    ERROR
}
