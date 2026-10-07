package codes.yousef.summon.events

/**
 * Represents a drag event.

 * @property x The x value.
 * @property y The y value.
 * @property clientX The client x value.
 * @property clientY The client y value.
 * @property screenX The screen x value.
 * @property screenY The screen y value.
 * @property dataTransfer The data transfer value.
 * @property type The type value.
 */
data class DragEvent(
    val x: Double,
    val y: Double,
    val clientX: Double,
    val clientY: Double,
    val screenX: Double,
    val screenY: Double,
    val dataTransfer: DataTransfer,
    val type: String
)
