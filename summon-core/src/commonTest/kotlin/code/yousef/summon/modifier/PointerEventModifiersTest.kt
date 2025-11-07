package code.yousef.summon.modifier

// Import specific functions if needed, or rely on package import
import code.yousef.summon.modifier.AttributeModifiers.getAttribute
import kotlin.test.Test
import kotlin.test.assertEquals

class PointerEventModifiersTest {

    @Test
    fun testDisablePointerEvents() {
        val modifier = Modifier().disablePointerEvents()
        assertEquals("none", modifier.styles["pointer-events"], "disablePointerEvents failed.")
    }

    @Test
    fun testEnablePointerEvents() {
        // Start with disabled to ensure enable overrides
        val modifier = Modifier().disablePointerEvents().enablePointerEvents()
        assertEquals("auto", modifier.styles["pointer-events"], "enablePointerEvents failed.")
    }

    @Test
    fun testPointerEventsEnumModifier() {
        val modifier = Modifier().pointerEvents(PointerEvents.VisiblePainted)
        assertEquals("visiblePainted", modifier.styles["pointer-events"])
    }

    @Test
    fun testOnClickModifier() {
        val handlerCode = "alert('Clicked!')"
        val modifier = Modifier().onClick(handlerCode)

        // Check that handler is stored as attribute
        assertEquals(
            handlerCode,
            modifier.attributes["onclick"],
            "onClick did not store event correctly in attributes map."
        )
    }

    @Test
    fun testOnMouseEnterModifier() {
        val handlerCode = "this.style.color='red';"
        val modifier = Modifier().onMouseEnter(handlerCode)

        // Check that handler is stored as attribute
        assertEquals(
            handlerCode,
            modifier.attributes["onmouseenter"],
            "onMouseEnter did not store event correctly in attributes map."
        )
    }

    @Test
    fun testDraggableModifier() {
        val modifierTrue = Modifier().draggable(true)
        assertEquals("true", modifierTrue.getAttribute("draggable"), "draggable(true) failed.")
        assertEquals("true", modifierTrue.attributes["draggable"], "draggable(true) did not store correctly.")

        val modifierFalse = Modifier().draggable(false)
        assertEquals("false", modifierFalse.getAttribute("draggable"), "draggable(false) failed.")
        assertEquals("false", modifierFalse.attributes["draggable"], "draggable(false) did not store correctly.")
    }

    // Add tests for other event handlers (onMouseLeave, onTouch*, onDrag*) if desired,
    // following the same pattern as onClick/onMouseEnter.
} 
