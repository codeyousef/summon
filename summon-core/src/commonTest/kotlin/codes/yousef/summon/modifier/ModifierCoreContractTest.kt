package codes.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ModifierCoreContractTest {
    private val foreign = object : Modifier {
        override infix fun then(other: Modifier): Modifier = other
    }

    @Test
    fun foreignAndIdentityModifiersUseSafeFallbackRepresentations() {
        val base = Modifier().style("color", "red")
        assertSame(base, base then Modifier)
        assertSame(base, base then foreign)
        assertEquals("blue", foreign.style("color", "blue").styles["color"])
        assertEquals("1px", foreign.withStyles(mapOf("gap" to "1px")).styles["gap"])
        assertEquals("value", foreign.attribute("name", "value").attributes["name"])
        assertEquals("value", foreign.attributes(mapOf("name" to "value")).attributes["name"])
        assertEquals("", foreign.toStyleString())
        assertEquals("", foreign.toStyleStringKebabCase())
        assertFalse(foreign.hasStyle("color", "red"))
        assertEquals("new", foreign.addClass(" new ").attributes["class"])
    }

    @Test
    fun positioningStylesClassesHoverAndBulkDataCoverBoundaryInputs() {
        val positioned = Modifier().absolutePosition("1px", "2px", "3px", "4px")
        assertEquals(setOf("position", "top", "right", "bottom", "left"), positioned.styles.keys)
        assertEquals("1.0", Modifier().opacity(-0.1f).styles["opacity"])
        assertEquals("1.0", Modifier().opacity(1.1f).styles["opacity"])
        assertEquals("0.5", Modifier().opacity(0.5f).styles["opacity"])

        val hover = Modifier(attributes = mapOf("data-hover-styles" to "color:red;invalid;transform:scale(1)"))
            .hover(mapOf("color" to "blue"))
        assertContainsPair(hover.attributes.getValue("data-hover-styles"), "color:blue")
        assertContainsPair(hover.attributes.getValue("data-hover-styles"), "transform:scale(1)")
        assertEquals("", Modifier().toStyleString())
        assertEquals("font-size: 12px;", Modifier().style("font-size", "12px").toStyleString())
        assertEquals("font-size:12px", Modifier().style("fontSize", "12px").toStyleStringKebabCase())
        assertTrue(Modifier().style("color", "red").hasStyle("color", "red"))
        assertFalse(Modifier().style("color", "red").hasStyle("color", "blue"))

        val classes = Modifier().addClass("first").addClass("first").addClass(" second ").addClass(" ")
        assertEquals(setOf("first", "second"), classes.attributes.getValue("class").split(' ').toSet())
        val data = Modifier().dataAttributes(mapOf("" to "ignored", " plain " to "one", "data-ready" to "yes"))
        assertFalse(data.attributes.containsKey("data-"))
        assertEquals("one", data.attributes["data-plain"])
        assertEquals("yes", data.attributes["data-ready"])
    }

    private fun assertContainsPair(value: String, expected: String) = assertTrue(value.split(';').contains(expected))
}
