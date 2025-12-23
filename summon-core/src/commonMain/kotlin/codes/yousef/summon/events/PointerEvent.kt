package codes.yousef.summon.events

/**
 * Represents a pointer event (mouse, touch, pen).
 */
data class PointerEvent(
    val x: Double,
    val y: Double,
    val clientX: Double,
    val clientY: Double,
    val screenX: Double,
    val screenY: Double,
    val type: String,
    val button: Int = 0,
    val buttons: Int = 0,
    val ctrlKey: Boolean = false,
    val shiftKey: Boolean = false,
    val altKey: Boolean = false,
    val metaKey: Boolean = false,
    val pressure: Float = 0f,
    val pointerType: String = "mouse" // mouse, touch, pen
)
