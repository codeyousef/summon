package code.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LayoutModifiersTest {

    // --- Sizing Tests --- 

    @Test
    fun testMinWidth() {
        val value = "100px"
        val modifier = Modifier().minWidth(value)
        assertEquals(value, modifier.styles["min-width"])
    }

    @Test
    fun testMinHeight() {
        val value = "50px"
        val modifier = Modifier().minHeight(value)
        assertEquals(value, modifier.styles["min-height"])
    }

    @Test
    fun testMaxHeight() {
        val value = "200px"
        val modifier = Modifier().maxHeight(value)
        assertEquals(value, modifier.styles["max-height"])
    }

    @Test
    fun testAspectRatioSingleValue() {
        val modifier = Modifier().aspectRatio(1.35f)
        assertEquals("1.35", modifier.styles["aspect-ratio"])
    }

    @Test
    fun testAspectRatioPair() {
        val modifier = Modifier().aspectRatio(16, 9)
        assertEquals("16 / 9", modifier.styles["aspect-ratio"])
    }

    @Test
    fun testFillMaxWidth() {
        val modifier = Modifier().fillMaxWidth()
        assertEquals("100%", modifier.styles["width"])
    }

    // --- Padding Tests --- 

    @Test
    fun testPaddingAll() {
        val value = "10px"
        val modifier = Modifier().padding(value)
        assertEquals(value, modifier.styles["padding"])
    }

    @Test
    fun testPaddingVerticalHorizontal() {
        val vertical = "5px"
        val horizontal = "15px"
        val modifier = Modifier().padding(vertical, horizontal)
        assertEquals("$vertical $horizontal", modifier.styles["padding"])
    }

    @Test
    fun testPaddingSides() {
        val top = "1px"
        val right = "2px"
        val bottom = "3px"
        val left = "4px"
        val modifier = Modifier()
            .paddingTop(top)
            .paddingRight(right)
            .paddingBottom(bottom)
            .paddingLeft(left)
        assertEquals(top, modifier.styles["padding-top"])
        assertEquals(right, modifier.styles["padding-right"])
        assertEquals(bottom, modifier.styles["padding-bottom"])
        assertEquals(left, modifier.styles["padding-left"])
    }
    
    @Test
    fun testPaddingOf() {
        val top = "5px"
        val left = "10px"
        val modifier = Modifier().paddingOf(top = top, left = left)
        // paddingOf sets individual sides if multiple are specified currently, let's verify that behavior
        // It actually sets the shorthand `padding` with 0 for unspecified values in the code read.
        assertEquals("$top 0 0 $left", modifier.styles["padding"])

        val modifierTopOnly = Modifier().paddingOf(top = top)
        assertEquals(top, modifierTopOnly.styles["padding-top"])
        assertNull(modifierTopOnly.styles["padding-right"])
        assertNull(modifierTopOnly.styles["padding-bottom"])
        assertNull(modifierTopOnly.styles["padding-left"])
        assertNull(modifierTopOnly.styles["padding"])
    }

    // --- Margin Tests --- 

    @Test
    fun testMarginAll() {
        val value = "8px"
        val modifier = Modifier().margin(value)
        assertEquals(value, modifier.styles["margin"])
    }

    @Test
    fun testMarginVerticalHorizontal() {
        val vertical = "4px"
        val horizontal = "12px"
        val modifier = Modifier().margin(vertical, horizontal)
        assertEquals("$vertical $horizontal", modifier.styles["margin"])
    }

    @Test
    fun testCenterHorizontally() {
        val modifier = Modifier().centerHorizontally()
        assertEquals("0 auto", modifier.styles["margin"])

        val withVertical = Modifier().centerHorizontally("24px")
        assertEquals("24px auto", withVertical.styles["margin"])
    }

    @Test
    fun testMarginFourSides() {
        val top = "1px"
        val right = "2px"
        val bottom = "3px"
        val left = "4px"
        val modifier = Modifier().margin(top, right, bottom, left)
        assertEquals("$top $right $bottom $left", modifier.styles["margin"])
    }

    @Test
    fun testMarginSides() {
        val top = "1px"
        val right = "2px"
        val bottom = "3px"
        val left = "4px"
        val modifier = Modifier()
            .marginTop(top)
            .marginRight(right)
            .marginBottom(bottom)
            .marginLeft(left)
        assertEquals(top, modifier.styles["margin-top"])
        assertEquals(right, modifier.styles["margin-right"])
        assertEquals(bottom, modifier.styles["margin-bottom"])
        assertEquals(left, modifier.styles["margin-left"])
    }
    
    @Test
    fun testMarginOf() {
        val bottom = "6px"
        val right = "9px"
        val modifier = Modifier().marginOf(bottom = bottom, right = right)
        // marginOf sets the shorthand `margin` with 0 for unspecified values.
        assertEquals("0 $right $bottom 0", modifier.styles["margin"])
        
        val modifierBottomOnly = Modifier().marginOf(bottom = bottom)
        assertEquals(bottom, modifierBottomOnly.styles["margin-bottom"])
        assertNull(modifierBottomOnly.styles["margin-top"])
        assertNull(modifierBottomOnly.styles["margin-right"])
        assertNull(modifierBottomOnly.styles["margin-left"])
        assertNull(modifierBottomOnly.styles["margin"])
    }

    @Test
    fun testInsetShorthand() {
        val modifier = Modifier().inset("0")
        assertEquals("0", modifier.styles["inset"])

        val verticalHorizontal = Modifier().inset("5vmax", "auto")
        assertEquals("5vmax auto", verticalHorizontal.styles["inset"])

        val fourSides = Modifier().inset("1px", "2px", "3px", "4px")
        assertEquals("1px 2px 3px 4px", fourSides.styles["inset"])
    }

    @Test
    fun testPositionInset() {
        val modifier = Modifier().positionInset(top = "0", left = "-20vmax")
        assertEquals("0", modifier.styles["top"])
        assertEquals("-20vmax", modifier.styles["left"])
        assertNull(modifier.styles["bottom"])
    }

    // --- Flexbox Tests --- 

    @Test
    fun testFlexWrap() {
        val stringValue = "wrap"
        val modifierString = Modifier().flexWrap(stringValue)
        assertEquals(stringValue, modifierString.styles["flex-wrap"])

        val enumValue = FlexWrap.NoWrap
        val modifierEnum = Modifier().flexWrap(enumValue)
        assertEquals(enumValue.toString(), modifierEnum.styles["flex-wrap"])
    }

    @Test
    fun testFlexGrow() {
        val value = 1
        val modifier = Modifier().flexGrow(value)
        assertEquals(value.toString(), modifier.styles["flex-grow"])
    }

    @Test
    fun testFlexShorthandHelper() {
        val modifier = Modifier().flex(1, 1, "480px")
        assertEquals("1 1 480px", modifier.styles["flex"])
    }

    @Test
    fun testFlexShrink() {
        val value = 0
        val modifier = Modifier().flexShrink(value)
        assertEquals(value.toString(), modifier.styles["flex-shrink"])
    }

    @Test
    fun testFlexBasis() {
        val value = "auto"
        val modifier = Modifier().flexBasis(value)
        assertEquals(value, modifier.styles["flex-basis"])
    }

    @Test
    fun testAlignSelf() {
        val value = "center"
        val modifier = Modifier().alignSelf(value)
        assertEquals(value, modifier.styles["align-self"])
    }
    
    @Test
    fun testAlignContent() {
        val value = "flex-start"
        val modifier = Modifier().alignContent(value)
        assertEquals(value, modifier.styles["align-content"])
    }

    // --- Grid Tests --- 

    @Test
    fun testJustifyItems() {
        val value = "center"
        val modifier = Modifier().justifyItems(value)
        assertEquals(value, modifier.styles["justify-items"])
    }

    @Test
    fun testJustifySelf() {
        val value = "end"
        val modifier = Modifier().justifySelf(value)
        assertEquals(value, modifier.styles["justify-self"])
    }

    @Test
    fun testGridGap() {
        val value = "10px"
        val modifier = Modifier().gridGap(value)
        assertEquals(value, modifier.styles["grid-gap"])
    }

    @Test
    fun testGridArea() {
        val value = "header"
        val modifier = Modifier().gridArea(value)
        assertEquals(value, modifier.styles["grid-area"])
    }

    @Test
    fun testGridColumn() {
        val value = "1 / 3"
        val modifier = Modifier().gridColumn(value)
        assertEquals(value, modifier.styles["grid-column"])
    }
} 
