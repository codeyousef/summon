package codes.yousef.summon.events

/**
 * Represents a pointer event (mouse, touch, pen).

 * @property x The x value.
 * @property y The y value.
 * @property clientX The client x value.
 * @property clientY The client y value.
 * @property screenX The screen x value.
 * @property screenY The screen y value.
 * @property type The type value.
 * @property button The button value.
 * @property buttons The buttons value.
 * @property ctrlKey The ctrl key value.
 * @property shiftKey The shift key value.
 * @property altKey The alt key value.
 * @property metaKey The meta key value.
 * @property pressure The pressure value.
 * @property pointerType The pointer type value.
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
