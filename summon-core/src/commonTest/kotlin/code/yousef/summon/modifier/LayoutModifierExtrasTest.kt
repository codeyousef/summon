package codes.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertEquals

class LayoutModifierExtrasTest {

    private inline fun <T> testModifier(block: LayoutModifiers.() -> T): T = with(LayoutModifiers, block)

    @Test
    fun testMaxWidth() {
        val value = "500px"
        val modifier = testModifier { Modifier().maxWidth(value) }
        assertEquals(value, modifier.styles["max-width"])
    }

    @Test
    fun testWidth() {
        val value = "100%"
        val modifier = testModifier { Modifier().width(value) }
        assertEquals(value, modifier.styles["width"])
    }

    @Test
    fun testHeight() {
        val value = "250px"
        val modifier = testModifier { Modifier().height(value) }
        assertEquals(value, modifier.styles["height"])
    }

    @Test
    fun testPosition() {
        val value = "absolute"
        val modifier = testModifier { Modifier().position(value) }
        assertEquals(value, modifier.styles["position"])
    }

    @Test
    fun testTop() {
        val value = "10px"
        val modifier = testModifier { Modifier().top(value) }
        assertEquals(value, modifier.styles["top"])
    }

    @Test
    fun testRight() {
        val value = "5px"
        val modifier = testModifier { Modifier().right(value) }
        assertEquals(value, modifier.styles["right"])
    }

    @Test
    fun testBottom() {
        val value = "15px"
        val modifier = testModifier { Modifier().bottom(value) }
        assertEquals(value, modifier.styles["bottom"])
    }

    @Test
    fun testLeft() {
        val value = "20px"
        val modifier = testModifier { Modifier().left(value) }
        assertEquals(value, modifier.styles["left"])
    }

    @Test
    fun testFlex() {
        val value = "1 1 auto"
        val modifier = testModifier { Modifier().flex(value) }
        assertEquals(value, modifier.styles["flex"])
    }

    @Test
    fun testFlexDirection() {
        val value = "column"
        val modifier = testModifier { Modifier().flexDirection(value) }
        assertEquals(value, modifier.styles["flex-direction"])
    }

    @Test
    fun testDisplay() {
        val value = "inline-block"
        val modifier = testModifier { Modifier().display(value) }
        assertEquals(value, modifier.styles["display"])
    }

    @Test
    fun testGridTemplateColumns() {
        val value = "repeat(auto-fit, minmax(100px, 1fr))"
        val modifier = testModifier { Modifier().gridTemplateColumns(value) }
        assertEquals(value, modifier.styles["grid-template-columns"])
    }

    @Test
    fun testGridColumnGap() {
        val value = "1rem"
        val modifier = testModifier { Modifier().gridColumnGap(value) }
        assertEquals(value, modifier.styles["grid-column-gap"])
    }

    @Test
    fun testGridRowGap() {
        val value = "15px"
        val modifier = testModifier { Modifier().gridRowGap(value) }
        assertEquals(value, modifier.styles["grid-row-gap"])
    }

    @Test
    fun testOverflow() {
        val value = "scroll"
        val modifier = testModifier { Modifier().overflow(value) }
        assertEquals(value, modifier.styles["overflow"])
    }

    @Test
    fun testBorder() {
        val width = "2px"
        val style = "dotted"
        val color = "green"
        val modifier = testModifier { Modifier().border(width, style, color) }
        assertEquals("$width $style $color", modifier.styles["border"])
    }

    @Test
    fun testBorderRadius() {
        val value = "8px"
        val modifier = testModifier { Modifier().borderRadius(value) }
        assertEquals(value, modifier.styles["border-radius"])
    }

    @Test
    fun testFontWeight() {
        val value = "700"
        val modifier = testModifier { Modifier().fontWeight(value) }
        assertEquals(value, modifier.styles["font-weight"])
    }

    @Test
    fun testColor() {
        val value = "rgb(255, 0, 0)"
        val modifier = testModifier { Modifier().color(value) }
        assertEquals(value, modifier.styles["color"])
    }
} 
