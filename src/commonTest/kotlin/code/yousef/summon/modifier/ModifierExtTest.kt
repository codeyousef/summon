package code.yousef.summon.modifier

import code.yousef.summon.modifier.LayoutModifiers.gap
import code.yousef.summon.modifier.LayoutModifiers.position
import code.yousef.summon.modifier.LayoutModifiers.top
import code.yousef.summon.modifier.LayoutModifiers.right
import code.yousef.summon.modifier.LayoutModifiers.bottom
import code.yousef.summon.modifier.LayoutModifiers.left
import code.yousef.summon.modifier.LayoutModifiers.display
import code.yousef.summon.modifier.LayoutModifiers.flexDirection
import code.yousef.summon.modifier.LayoutModifiers.justifyContent
import code.yousef.summon.modifier.LayoutModifiers.alignItems
import code.yousef.summon.modifier.LayoutModifiers.overflow
import code.yousef.summon.modifier.LayoutModifiers.flex
import code.yousef.summon.modifier.LayoutModifiers.gridTemplateColumns
import code.yousef.summon.modifier.LayoutModifiers.gridColumnGap
import code.yousef.summon.modifier.LayoutModifiers.gridRowGap
import code.yousef.summon.modifier.StylingModifiers.opacity
import code.yousef.summon.modifier.border
import code.yousef.summon.modifier.StylingModifiers.borderRadius
import code.yousef.summon.modifier.StylingModifiers.fontWeight
import code.yousef.summon.modifier.StylingModifiers.color
import code.yousef.summon.modifier.LayoutModifiers.width
import code.yousef.summon.modifier.LayoutModifiers.height
import code.yousef.summon.modifier.AttributeModifiers.attribute
import code.yousef.summon.modifier.EventModifiers.onClick
import kotlin.test.Test
import kotlin.test.assertEquals

class ModifierExtTest {

    @Test
    fun testGap() {
        val value = "10px"
        val modifier = Modifier().gap(value)
        assertEquals(value, modifier.styles["gap"])
    }

    @Test
    fun testOpacity() {
        val floatValue = 0.5f
        val modifierFloat = Modifier().opacity(floatValue)
        assertEquals(floatValue.toString(), modifierFloat.styles["opacity"])

        val stringValue = "0.8"
        val modifierString = Modifier().opacity(stringValue)
        assertEquals(stringValue, modifierString.styles["opacity"])
    }

    @Test
    fun testBorderRadius() {
        val value = "4px"
        val modifier = Modifier().borderRadius(value)
        assertEquals(value, modifier.styles["border-radius"])
    }

    @Test
    fun testMaxWidth() {
        val value = "600px"
        val modifier = Modifier().maxWidth(value)
        assertEquals(value, modifier.styles["max-width"])
    }

    @Test
    fun testPositioning() {
        val pos = "fixed"
        val t = "0"
        val r = "10px"
        val b = "auto"
        val l = "10px"
        val modifier = Modifier()
            .position(pos)
            .top(t)
            .right(r)
            .bottom(b)
            .left(l)
        assertEquals(pos, modifier.styles["position"])
        assertEquals(t, modifier.styles["top"])
        assertEquals(r, modifier.styles["right"])
        assertEquals(b, modifier.styles["bottom"])
        assertEquals(l, modifier.styles["left"])
    }

    @Test
    fun testDisplay() {
        val value = "grid"
        val modifier = Modifier().display(value)
        assertEquals(value, modifier.styles["display"])
    }

    @Test
    fun testFlexBox() {
        val direction = "row"
        val justify = "space-around"
        val align = "stretch"
        val modifier = Modifier()
            .flexDirection(direction)
            .justifyContent(justify)
            .alignItems(align)
        assertEquals(direction, modifier.styles["flex-direction"])
        assertEquals(justify, modifier.styles["justify-content"])
        assertEquals(align, modifier.styles["align-items"])
    }

    @Test
    fun testBorder() {
        val width = "1px"
        val style = "dashed"
        val color = "red"
        val modifier = Modifier().border(width, style, color)
        assertEquals("$width $style $color", modifier.styles["border"])
    }

    @Test
    fun testOverflow() {
        val value = "auto"
        val modifier = Modifier().overflow(value)
        assertEquals(value, modifier.styles["overflow"])
    }

    @Test
    fun testFlex() {
        val value = "2"
        val modifier = Modifier().flex(value)
        assertEquals(value, modifier.styles["flex"])
    }

    @Test
    fun testGrid() {
        val cols = "1fr 2fr"
        val colGap = "5px"
        val rowGap = "10px"
        val modifier = Modifier()
            .gridTemplateColumns(cols)
            .gridColumnGap(colGap)
            .gridRowGap(rowGap)
        assertEquals(cols, modifier.styles["grid-template-columns"])
        assertEquals(colGap, modifier.styles["grid-column-gap"])
        assertEquals(rowGap, modifier.styles["grid-row-gap"])
    }

    @Test
    fun testFontWeight() {
        val value = "lighter"
        val modifier = Modifier().fontWeight(value)
        assertEquals(value, modifier.styles["font-weight"])
    }

    @Test
    fun testColor() {
        val value = "blue"
        val modifier = Modifier().color(value)
        assertEquals(value, modifier.styles["color"])
    }

    @Test
    fun testSizing() {
        val w = "50%"
        val h = "auto"
        val modifier = Modifier().width(w).height(h)
        assertEquals(w, modifier.styles["width"])
        assertEquals(h, modifier.styles["height"])
    }

    @Test
    fun testAttribute() {
        val name = "role"
        val value = "button"
        val modifier = Modifier().attribute(name, value)
        // Testing the specific implementation in ModifierExt.kt
        assertEquals(value, modifier.attributes[name])
    }

    @Test
    fun testOnClick() {
        val handler = { /* do nothing */ }
        val modifier = Modifier().onClick(handler)
        // Testing the specific implementation in ModifierExt.kt (style-based)
        assertEquals(handler.toString(), modifier.styles["onclick"])
    }
} 