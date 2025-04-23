package code.yousef.summon.modifier

// Import necessary functions if not resolved automatically
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue
import kotlin.test.assertFalse

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

        // Test that AttributeModifiers.attribute helper DOES add __attr: prefix
        assertEquals("value", m.attribute("data-attr", "value").styles["__attr:data-attr"], "attribute() helper prefix failed.")
        assertEquals("value", m.attribute("aria-label", "value").styles["__attr:aria-label"], "attribute() helper prefix failed.")

        // Test that PointerEventModifiers helpers DO add __event: prefix
        assertEquals("value", m.onClick("value").styles["__event:click"], "onClick() helper prefix failed.")
        assertEquals("value", m.onMouseEnter("value").styles["__event:mouseenter"], "onMouseEnter() helper prefix failed.")
        
        // Test that CoreModifiers.event helper DOES add __event: prefix
        assertEquals("true", m.event("focus") {}.styles["__event:focus"], "event() helper prefix failed.")

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
        val original = Modifier().style("color", "blue").style("margin", "5px")
        val clone = original.clone()

        assertEquals(original.styles, clone.styles, "Clone styles should match original.")
        assertNotSame(original.styles, clone.styles, "Clone styles map should be a different instance.")
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
        // Verify the marker style is added
        assertEquals("true", m.styles["__event:focus"], "Event marker style not added correctly.")
    }
} 