package codes.yousef.summon.events

/**
 * Represents a drag event.
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
