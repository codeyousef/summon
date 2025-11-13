package codes.yousef.summon.modifier

// Import necessary functions if not resolved automatically
import kotlin.test.*

class CoreModifiersTest {

    @Test
    fun testAttributesProperty() {
        // Use the .attribute() helper for attributes
        val modifier = Modifier()
            .attribute("data-test", "value1") // Use attribute() 
            .style("id", "my-id")           // Style should not be extracted by .attributes
            .style("class", "my-class")       // Style should not be extracted
            .event("click") { /*dummy*/ }    // Event should not be extracted
            .attribute("color", "red")          // Use attribute() - Treat 'color' as attribute here for test
            .style("background-color", "blue") // Style should not be extracted
            .attribute("hx-get", "/path")       // Use attribute() for htmx attr

        val extractedAttributes = modifier.attributes

        // Should only extract keys added via .attribute()
        assertEquals(3, extractedAttributes.size, "Incorrect number of attributes extracted.")
        assertEquals("value1", extractedAttributes["data-test"], "data-test attribute incorrect.")
        assertEquals("red", extractedAttributes["color"], "color attribute incorrect.")
        assertEquals("/path", extractedAttributes["hx-get"], "hx-get attribute incorrect.")
        assertFalse(extractedAttributes.containsKey("id"), "id should not be in attributes.")
        assertFalse(extractedAttributes.containsKey("class"), "class should not be in attributes.")
        assertFalse(extractedAttributes.containsKey("click"), "click event should not be in attributes.")
        assertFalse(extractedAttributes.containsKey("background-color"), "background-color style should not be in attributes.")
    }

    @Test
    fun testStylePrefixingLogic() {
        val m = Modifier()
        // Test that Modifier.style member function does NOT prefix standard CSS
        assertEquals("value", m.style("color", "value").styles["color"], "Member style function should not prefix CSS.")
        assertEquals("value", m.style("width", "value").styles["width"], "Member style function should not prefix CSS.")
        assertEquals("value", m.style("margin", "value").styles["margin"], "Member style function should not prefix CSS.")

        // Test that AttributeModifiers.attribute helper adds to attributes map
        assertEquals("value", m.attribute("data-attr", "value").attributes["data-attr"], "attribute() helper failed.")
        assertEquals("value", m.attribute("aria-label", "value").attributes["aria-label"], "attribute() helper failed.")

        // Test that PointerEventModifiers helpers write attributes
        assertEquals("value", m.onClick("value").attributes["onclick"], "onClick() helper attribute failed.")
        assertEquals(
            "value",
            m.onMouseEnter("value").attributes["onmouseenter"],
            "onMouseEnter() helper attribute failed."
        )

        // Test that CoreModifiers.event helper stores handler
        assertTrue(m.event("focus") {}.events.containsKey("focus"), "event() helper failed to register handler.")

        // Test that non-prefixed standard HTML attributes are still added directly by .style()
        assertEquals("value", m.style("id", "value").styles["id"], "id style failed.")
        assertEquals("value", m.style("class", "value").styles["class"], "class style failed.")
        assertEquals("value", m.style("style", "value").styles["style"], "style style failed.")
        assertEquals("value", m.style("hx-boost", "value").styles["hx-boost"], "hx-* style failed.")
    }

    @Test
    fun testApplyIf() {
        val base = Modifier().style("width", "100px")
        val conditional = Modifier().style("height", "50px")

        // Condition true
        val modifiedTrue = base.applyIf(true) { this.then(conditional) }
        assertEquals("100px", modifiedTrue.styles["width"])
        assertEquals("50px", modifiedTrue.styles["height"])

        // Condition false
        val modifiedFalse = base.applyIf(false) { this.then(conditional) }
        assertEquals("100px", modifiedFalse.styles["width"])
        assertFalse(modifiedFalse.styles.containsKey("height"))
    }

    @Test
    fun testClone() {
        val original = Modifier()
            .style("color", "blue")
            .style("margin", "5px")
            .event("focus") {}
        val clone = original.clone()

        assertEquals(original.styles, clone.styles, "Clone styles should match original.")
        assertNotSame(original.styles, clone.styles, "Clone styles map should be a different instance.")
        assertEquals(original.events.keys, clone.events.keys, "Clone should retain event handlers.")
        assertNotSame(original, clone, "Clone modifier should be a different instance.")
    }

    @Test
    fun testCombine() {
        val m1 = Modifier().style("color", "red").style("padding", "10px")
        val m2 = Modifier().style("color", "blue").style("margin", "5px") // Overwrites color

        val combined = m1.combine(m2)

        assertEquals("blue", combined.styles["color"], "Combine did not overwrite style.")
        assertEquals("10px", combined.styles["padding"], "Combine lost style from first modifier.")
        assertEquals("5px", combined.styles["margin"], "Combine did not add style from second modifier.")
        assertEquals(3, combined.styles.size)
    }

    @Test
    fun testEvent() {
        val m = Modifier().event("focus") { /* Handler logic */ }
        assertTrue(m.events.containsKey("focus"), "Event handler not added correctly.")
    }
} 
