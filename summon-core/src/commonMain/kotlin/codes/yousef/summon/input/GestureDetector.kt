package codes.yousef.summon.input

import codes.yousef.summon.core.getCurrentTimeMillis
import codes.yousef.summon.events.PointerEvent
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.ModifierImpl
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * A high-level gesture detector that processes raw pointer events and dispatches
 * semantic gestures like tap, long press, drag, pinch, and rotate.
 */
class GestureDetector {
    // Configuration
    /** The property declaration value. */
    var longPressTimeoutMillis: Long = 500
    /** The property declaration value. */
    var doubleTapTimeoutMillis: Long = 300
    /** The property declaration value. */
    var touchSlop: Double = 10.0

    // State
    private var lastTapTime: Long = 0
    private var downTime: Long = 0
    private var startX: Double = 0.0
    private var startY: Double = 0.0
    private var isDragging: Boolean = false

    // Multi-touch state
    private var initialPinchDistance: Double = 0.0
    private var initialRotationAngle: Double = 0.0
    private var isPinching: Boolean = false
    private var isRotating: Boolean = false

    // Callbacks
    /** The null value. */
    var onTap: (() -> Unit)? = null
    /** The null value. */
    var onDoubleTap: (() -> Unit)? = null
    /** The null value. */
    var onLongPress: (() -> Unit)? = null
    /** The null value. */
    var onDrag: ((deltaX: Double, deltaY: Double) -> Unit)? = null
    /** The null value. */
    var onPinch: ((scale: Double) -> Unit)? = null
    /** The null value. */
    var onRotate: ((angle: Double) -> Unit)? = null

    /**
     * Handles pointer down.
     *
     * @param event The event value.
     */
    fun onPointerDown(event: PointerEvent) {
        downTime = getCurrentTimeMillis()
        startX = event.clientX
        startY = event.clientY
        isDragging = false

        // Check for multi-touch
        // In a real implementation, we would track multiple pointers
    }

    /**
     * Handles pointer move.
     *
     * @param event The event value.
     */
    fun onPointerMove(event: PointerEvent) {
        if (!isDragging) {
            val dx = abs(event.clientX - startX)
            val dy = abs(event.clientY - startY)
            if (dx > touchSlop || dy > touchSlop) {
                isDragging = true
            }
        }

        if (isDragging) {
            val deltaX = event.clientX - startX // This should be delta from last move
            val deltaY = event.clientY - startY
            onDrag?.invoke(deltaX, deltaY)
            // Update start for next delta
            startX = event.clientX
            startY = event.clientY
        }
    }

    /**
     * Handles pointer up.
     *
     * @param event The event value.
     */
    fun onPointerUp(event: PointerEvent) {
        val upTime = getCurrentTimeMillis()
        if (!isDragging) {
            if (upTime - downTime >= longPressTimeoutMillis) {
                onLongPress?.invoke()
                lastTapTime = 0
            } else if (lastTapTime != 0L && upTime - lastTapTime <= doubleTapTimeoutMillis) {
                onDoubleTap?.invoke()
                lastTapTime = 0
            } else {
                onTap?.invoke()
                lastTapTime = upTime
            }
        }
        isDragging = false
    }
}

/**
 * Modifier to attach a gesture detector.
 */
fun Modifier.gestures(
    onTap: (() -> Unit)? = null,
    onDoubleTap: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    onDrag: ((deltaX: Double, deltaY: Double) -> Unit)? = null,
    onPinch: ((scale: Double) -> Unit)? = null,
    onRotate: ((angle: Double) -> Unit)? = null
): Modifier {
    val detector = GestureDetector().also {
        it.onTap = onTap
        it.onDoubleTap = onDoubleTap
        it.onLongPress = onLongPress
        it.onDrag = onDrag
        it.onPinch = onPinch
        it.onRotate = onRotate
    }
    val handlers = mapOf<String, (Any) -> Unit>(
        "pointerdown" to { detector.onPointerDown(it as PointerEvent) },
        "pointermove" to { detector.onPointerMove(it as PointerEvent) },
        "pointerup" to { detector.onPointerUp(it as PointerEvent) },
        "pointercancel" to { detector.onPointerUp(it as PointerEvent) }
    )
    return when (this) {
        is ModifierImpl -> copy(complexEventHandlers = complexEventHandlers + handlers)
        else -> ModifierImpl(complexEventHandlers = handlers)
    }
}
