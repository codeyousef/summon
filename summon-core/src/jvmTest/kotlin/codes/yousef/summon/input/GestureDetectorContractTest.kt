package codes.yousef.summon.input

import codes.yousef.summon.events.PointerEvent
import codes.yousef.summon.modifier.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GestureDetectorContractTest {
    private fun event(x: Double, y: Double) = PointerEvent(x, y, x, y, x, y, "pointer")

    @Test
    fun tapsDoubleTapsLongPressesAndDragsAreMutuallyConsistent() {
        val detector = GestureDetector()
        var taps = 0
        var doubles = 0
        var longPresses = 0
        val drags = mutableListOf<Pair<Double, Double>>()
        detector.onTap = { taps++ }
        detector.onDoubleTap = { doubles++ }
        detector.onLongPress = { longPresses++ }
        detector.onDrag = { x, y -> drags += x to y }

        detector.onPointerDown(event(0.0, 0.0)); detector.onPointerUp(event(0.0, 0.0))
        detector.onPointerDown(event(0.0, 0.0)); detector.onPointerUp(event(0.0, 0.0))
        assertEquals(1, taps)
        assertEquals(1, doubles)

        detector.longPressTimeoutMillis = 0
        detector.onPointerDown(event(0.0, 0.0)); detector.onPointerUp(event(0.0, 0.0))
        assertEquals(1, longPresses)

        detector.longPressTimeoutMillis = Long.MAX_VALUE
        detector.touchSlop = 5.0
        detector.onPointerDown(event(1.0, 2.0))
        detector.onPointerMove(event(3.0, 4.0))
        assertTrue(drags.isEmpty())
        detector.onPointerMove(event(10.0, 12.0))
        detector.onPointerMove(event(13.0, 18.0))
        detector.onPointerUp(event(13.0, 18.0))
        assertEquals(listOf(9.0 to 10.0, 3.0 to 6.0), drags)
    }

    @Test
    fun gestureModifierInstallsOwnedPointerHandlersAndDispatches() {
        var taps = 0
        val modifier = Modifier().gestures(onTap = { taps++ })
        assertEquals(setOf("pointerdown", "pointermove", "pointerup", "pointercancel"), modifier.complexEventHandlers.keys)
        modifier.complexEventHandlers.getValue("pointerdown")(event(0.0, 0.0))
        modifier.complexEventHandlers.getValue("pointerup")(event(0.0, 0.0))
        assertEquals(1, taps)
    }
}
