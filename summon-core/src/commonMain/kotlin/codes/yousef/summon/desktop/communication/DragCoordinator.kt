/**
 * # Cross-Window Drag and Drop Coordinator
 *
 * Enables drag and drop operations between browser windows/tabs.
 * Uses BroadcastChannel for cross-window communication.
 *
 * @since 0.7.0
 */
package codes.yousef.summon.desktop.communication

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Represents data being dragged between windows.

 * @property dragId The drag id value.
 * @property dataType The data type value.
 * @property payload The payload value.
 * @property sourceWindow The source window value.
 * @property metadata The metadata value.
 */
@Serializable
data class DragData(
    /** Unique identifier for this drag operation */
    val dragId: String,
    /** The type of data being dragged (e.g., "text", "file", "custom") */
    val dataType: String,
    /** The serialized data payload */
    val payload: String,
    /** The source window ID */
    val sourceWindow: String,
    /** Optional metadata about the drag */
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Message types for drag coordination.
 */
@Serializable
sealed class DragMessage {
    /**
     * Drag-start announcement.
     *
     * @property data bounded drag metadata and payload
     */
    @Serializable
    data class DragStart(val data: DragData) : DragMessage()

    /**
     * Drag-position update.
     *
     * @property dragId operation identity
     * @property x horizontal viewport coordinate
     * @property y vertical viewport coordinate
     */
    @Serializable
    data class DragMove(val dragId: String, val x: Double, val y: Double) : DragMessage()

    /**
     * Drag-end announcement.
     *
     * @property dragId operation identity
     * @property cancelled whether no drop completed
     */
    @Serializable
    data class DragEnd(val dragId: String, val cancelled: Boolean) : DragMessage()

    /**
     * Accepted-drop announcement.
     *
     * @property dragId operation identity
     * @property targetWindow accepting window identity
     */
    @Serializable
    data class DropAccepted(val dragId: String, val targetWindow: String) : DragMessage()
}

/**
 * Callback for drag events.
 */
interface DragEventListener {
    /** Called when a drag operation starts in another window */
    fun onDragStart(data: DragData)

    /** Called when a drag position updates */
    fun onDragMove(dragId: String, x: Double, y: Double)

    /** Called when a drag operation ends */
    fun onDragEnd(dragId: String, cancelled: Boolean)

    /** Called when a drop is accepted by another window */
    fun onDropAccepted(dragId: String, targetWindow: String)
}

/**
 * Coordinates drag and drop operations between windows.
 *
 * Usage:
 * ```kotlin
 * val coordinator = DragCoordinator("my-app-dnd")
 *
 * // Start listening for drag events from other windows
 * coordinator.addListener(object : DragEventListener {
 *     override fun onDragStart(data: DragData) {
 *         // Show drop indicator
 *     }
 *     override fun onDragEnd(dragId: String, cancelled: Boolean) {
 *         // Hide drop indicator
 *     }
 *     // ... other callbacks
 * })
 *
 * // Start a drag operation
 * coordinator.startDrag(DragData(
 *     dragId = generateUuid(),
 *     dataType = "text",
 *     payload = "Hello from another window!",
 *     sourceWindow = WindowManager.currentWindowId
 * ))
 *
 * // Accept a drop
 * coordinator.acceptDrop(dragId)
 * ```
 */
expect class DragCoordinator(channelName: String) {
    /** Starts a drag operation, broadcasting to other windows */
    fun startDrag(data: DragData)

    /** Updates drag position (optional, for visual feedback) */
    fun updateDragPosition(dragId: String, x: Double, y: Double)

    /** Ends a drag operation */
    fun endDrag(dragId: String, cancelled: Boolean = false)

    /** Accepts a drop from another window */
    fun acceptDrop(dragId: String)

    /** Adds a listener for drag events */
    fun addListener(listener: DragEventListener)

    /** Removes a listener */
    fun removeListener(listener: DragEventListener)

    /** Closes the coordinator and releases resources */
    fun close()
}

/**
 * Helper to serialize drag messages.
 */
internal fun DragMessage.serialize(): String = Json.encodeToString(this)

/**
 * Helper to deserialize drag messages.
 */
internal fun deserializeDragMessage(json: String): DragMessage = Json.decodeFromString(json)
