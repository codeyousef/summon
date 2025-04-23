package code.yousef.summon.modifier

// Import specific functions if needed, or rely on package import
import code.yousef.summon.modifier.AttributeModifiers.getAttribute // Needed for verification
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
    fun testOnClickModifier() {
        val handlerCode = "alert('Clicked!')"
        val modifier = Modifier().onClick(handlerCode)
        
        // Check the raw styles map for the correct __event: prefixed key
        assertEquals(handlerCode, modifier.styles["__event:click"], "onClick did not store event correctly in styles map.")
    }

    @Test
    fun testOnMouseEnterModifier() {
        val handlerCode = "this.style.color='red';"
        val modifier = Modifier().onMouseEnter(handlerCode)
        
        // Check the raw styles map for the correct __event: prefixed key
        assertEquals(handlerCode, modifier.styles["__event:mouseenter"], "onMouseEnter did not store event correctly in styles map.")
    }

    @Test
    fun testDraggableModifier() {
        val modifierTrue = Modifier().draggable(true)
        assertEquals("true", modifierTrue.getAttribute("draggable"), "draggable(true) failed.")
        assertEquals("true", modifierTrue.styles["__attr:draggable"], "draggable(true) did not store correctly.")

        val modifierFalse = Modifier().draggable(false)
        assertEquals("false", modifierFalse.getAttribute("draggable"), "draggable(false) failed.")
        assertEquals("false", modifierFalse.styles["__attr:draggable"], "draggable(false) did not store correctly.")
    }

    // Add tests for other event handlers (onMouseLeave, onTouch*, onDrag*) if desired,
    // following the same pattern as onClick/onMouseEnter.
} 