package code.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PseudoElementModifiersTest {

    @Test
    fun beforeAddsPseudoLayer() {
        val modifier = Modifier().before {
            style("width", "120%")
                .style("height", "120%")
                .style("background", "radial-gradient(circle, rgba(255,255,255,0.2), transparent)")
        }

        assertEquals("relative", modifier.styles["position"], "Host should become relative by default")
        assertEquals(1, modifier.pseudoElements.size)
        val layer = modifier.pseudoElements.first()
        assertEquals(PseudoElement.Before, layer.element)
        assertEquals("\"\"", layer.content)
        assertEquals("120%", layer.styles["width"])
        assertTrue(layer.styles.containsKey("background"))
    }

    @Test
    fun afterAllowsCustomContent() {
        val modifier = Modifier().after(content = "\"*\"") {
            style("position", "absolute")
                .style("inset", "0")
        }

        val layer = modifier.pseudoElements.single { it.element == PseudoElement.After }
        assertEquals("\"*\"", layer.content)
        assertEquals("absolute", layer.styles["position"])
        assertEquals("0", layer.styles["inset"])
    }
}
