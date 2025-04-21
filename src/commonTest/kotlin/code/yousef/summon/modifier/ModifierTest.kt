package code.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for the Modifier class
 */
class ModifierTest {

    @Test
    fun testEmptyModifier() {
        val modifier = Modifier()
        assertEquals(emptyMap(), modifier.styles, "Empty modifier should have no styles")
        assertEquals("", modifier.toStyleString(), "Empty modifier should produce empty style string")
    }

    @Test
    fun testCompanionCreateMethod() {
        val modifier = Modifier.create()
        assertEquals(emptyMap(), modifier.styles, "Modifier.create() should create an empty modifier")
    }

    @Test
    fun testStyleMethod() {
        val modifier = Modifier().style("color", "red")
        assertEquals(mapOf("color" to "red"), modifier.styles, "style() should add the property to styles map")
        assertEquals("color:red", modifier.toStyleString(), "toStyleString() should format the style correctly")
    }

    @Test
    fun testStylesMethod() {
        val properties = mapOf("color" to "red", "font-size" to "16px")
        val modifier = Modifier().styles(properties)
        assertEquals(properties, modifier.styles, "styles() should add all properties to styles map")
        assertTrue(modifier.toStyleString().contains("color:red"), "toStyleString() should include all styles")
        assertTrue(modifier.toStyleString().contains("font-size:16px"), "toStyleString() should include all styles")
    }

    @Test
    fun testBackgroundMethod() {
        val modifier = Modifier().background("blue")
        assertEquals(mapOf("background-color" to "blue"), modifier.styles, "background() should set background-color")
    }

    @Test
    fun testBackgroundColorMethod() {
        val modifier = Modifier().backgroundColor("#FF0000")
        assertEquals(mapOf("background-color" to "#FF0000"), modifier.styles, "backgroundColor() should set background-color")
    }

    @Test
    fun testPaddingMethod() {
        // Test single value padding
        val modifier1 = Modifier().padding("10px")
        assertEquals(mapOf("padding" to "10px"), modifier1.styles, "padding() should set padding property")

        // Test multi-value padding
        val modifier2 = Modifier().padding("10px", "20px", "30px", "40px")
        assertEquals(mapOf("padding" to "10px 20px 30px 40px"), modifier2.styles, "padding() with multiple values should format correctly")
    }

    @Test
    fun testWidthAndHeightMethods() {
        val modifier1 = Modifier().width("100px")
        assertEquals(mapOf("width" to "100px"), modifier1.styles, "width() should set width property")

        val modifier2 = Modifier().height("200px")
        assertEquals(mapOf("height" to "200px"), modifier2.styles, "height() should set height property")

        val modifier3 = Modifier().maxWidth("300px")
        assertEquals(mapOf("max-width" to "300px"), modifier3.styles, "maxWidth() should set max-width property")
    }

    @Test
    fun testSizeMethods() {
        // Test size with different width and height
        val modifier1 = Modifier().size("100px", "200px")
        assertEquals(mapOf("width" to "100px", "height" to "200px"), modifier1.styles, "size() should set both width and height")

        // Test size with same width and height
        val modifier2 = Modifier().size("150px")
        assertEquals(mapOf("width" to "150px", "height" to "150px"), modifier2.styles, "size() with one value should set equal width and height")
    }

    @Test
    fun testBorderMethods() {
        // Test border with string style
        val modifier1 = Modifier().border("1px", "solid", "black")
        assertEquals(mapOf("border" to "1px solid black"), modifier1.styles, "border() should set border property")

        // Test border with enum style
        val modifier2 = Modifier().border("2px", BorderStyle.Dashed, "red")
        assertEquals(mapOf("border" to "2px dashed red"), modifier2.styles, "border() with BorderStyle enum should work correctly")

        // Test borderRadius
        val modifier3 = Modifier().borderRadius("5px")
        assertEquals(mapOf("border-radius" to "5px"), modifier3.styles, "borderRadius() should set border-radius property")
    }

    @Test
    fun testColorAndFontMethods() {
        val modifier1 = Modifier().color("blue")
        assertEquals(mapOf("color" to "blue"), modifier1.styles, "color() should set color property")

        val modifier2 = Modifier().fontSize("18px")
        assertEquals(mapOf("font-size" to "18px"), modifier2.styles, "fontSize() should set font-size property")

        val modifier3 = Modifier().fontFamily("Arial, sans-serif")
        assertEquals(mapOf("font-family" to "Arial, sans-serif"), modifier3.styles, "fontFamily() should set font-family property")
    }

    @Test
    fun testMarginMethods() {
        // Test single value margin
        val modifier1 = Modifier().margin("10px")
        assertEquals(mapOf("margin" to "10px"), modifier1.styles, "margin() should set margin property")

        // Test multi-value margin
        val modifier2 = Modifier().margin("10px", "20px", "30px", "40px")
        assertEquals(mapOf("margin" to "10px 20px 30px 40px"), modifier2.styles, "margin() with multiple values should format correctly")

        // Test individual margin sides
        val modifier3 = Modifier().marginTop("5px")
        assertEquals(mapOf("margin-top" to "5px"), modifier3.styles, "marginTop() should set margin-top property")

        val modifier4 = Modifier().marginRight("15px")
        assertEquals(mapOf("margin-right" to "15px"), modifier4.styles, "marginRight() should set margin-right property")

        val modifier5 = Modifier().marginBottom("25px")
        assertEquals(mapOf("margin-bottom" to "25px"), modifier5.styles, "marginBottom() should set margin-bottom property")

        val modifier6 = Modifier().marginLeft("35px")
        assertEquals(mapOf("margin-left" to "35px"), modifier6.styles, "marginLeft() should set margin-left property")
    }

    @Test
    fun testFillMaxSizeMethods() {
        val modifier1 = Modifier().fillMaxWidth()
        assertEquals(mapOf("width" to "100%"), modifier1.styles, "fillMaxWidth() should set width to 100%")

        val modifier2 = Modifier().fillMaxHeight()
        assertEquals(mapOf("height" to "100%"), modifier2.styles, "fillMaxHeight() should set height to 100%")

        val modifier3 = Modifier().fillMaxSize()
        assertEquals(mapOf("width" to "100%", "height" to "100%"), modifier3.styles, "fillMaxSize() should set both width and height to 100%")
    }

    @Test
    fun testShadowMethods() {
        // Test shadow with custom values
        val modifier1 = Modifier().shadow("2px", "3px", "5px", "rgba(0,0,0,0.5)")
        assertEquals(mapOf("box-shadow" to "2px 3px 5px rgba(0,0,0,0.5)"), modifier1.styles, "shadow() should set box-shadow property")

        // Test default shadow
        val modifier2 = Modifier().shadow()
        assertEquals(mapOf("box-shadow" to "0px 2px 4px rgba(0,0,0,0.2)"), modifier2.styles, "shadow() without args should set default shadow")
    }

    @Test
    fun testAbsolutePositionMethod() {
        // Test with all sides specified
        val modifier1 = Modifier().absolutePosition("10px", "20px", "30px", "40px")
        assertEquals(
            mapOf(
                "position" to "absolute",
                "top" to "10px",
                "right" to "20px",
                "bottom" to "30px",
                "left" to "40px"
            ),
            modifier1.styles,
            "absolutePosition() should set position and all sides"
        )

        // Test with some sides specified
        val modifier2 = Modifier().absolutePosition(top = "5px", left = "15px")
        assertEquals(
            mapOf(
                "position" to "absolute",
                "top" to "5px",
                "left" to "15px"
            ),
            modifier2.styles,
            "absolutePosition() should set only specified sides"
        )
    }

    @Test
    fun testOpacityMethod() {
        val modifier = Modifier().opacity(0.5f)
        assertEquals(mapOf("opacity" to "0.5"), modifier.styles, "opacity() should set opacity property")
    }

    @Test
    fun testHoverMethod() {
        val hoverStyles = mapOf("background-color" to "blue", "color" to "white")
        val modifier = Modifier().hover(hoverStyles)
        
        // The hover styles are stored in a special key
        assertEquals(
            mapOf("__hover" to "background-color:blue;color:white"),
            modifier.styles,
            "hover() should store styles in __hover key"
        )
        
        // The hover styles should not appear in the style string
        assertEquals("", modifier.toStyleString(), "hover styles should not appear in toStyleString()")
    }

    @Test
    fun testThenMethod() {
        val modifier1 = Modifier().background("red").width("100px")
        val modifier2 = Modifier().color("blue").height("200px")
        
        val combined = modifier1.then(modifier2)
        
        assertEquals(
            mapOf(
                "background-color" to "red",
                "width" to "100px",
                "color" to "blue",
                "height" to "200px"
            ),
            combined.styles,
            "then() should combine styles from both modifiers"
        )
    }

    @Test
    fun testStyleOverriding() {
        val modifier1 = Modifier().background("red")
        val modifier2 = Modifier().background("blue")
        
        val combined = modifier1.then(modifier2)
        
        assertEquals(
            mapOf("background-color" to "blue"),
            combined.styles,
            "Styles from second modifier should override styles from first"
        )
    }

    @Test
    fun testCursorMethods() {
        // Test with string value
        val modifier1 = Modifier().cursor("pointer")
        assertEquals(mapOf("cursor" to "pointer"), modifier1.styles, "cursor() should set cursor property")

        // Test with enum value
        val modifier2 = Modifier().cursor(Cursor.Pointer)
        assertEquals(mapOf("cursor" to "pointer"), modifier2.styles, "cursor() with Cursor enum should work correctly")
    }

    @Test
    fun testZIndexMethod() {
        val modifier = Modifier().zIndex(999)
        assertEquals(mapOf("z-index" to "999"), modifier.styles, "zIndex() should set z-index property")
    }

    @Test
    fun testTextAlignMethods() {
        // Test with string value
        val modifier1 = Modifier().textAlign("center")
        assertEquals(mapOf("text-align" to "center"), modifier1.styles, "textAlign() should set text-align property")

        // Test with enum value
        val modifier2 = Modifier().textAlign(TextAlign.Center)
        assertEquals(mapOf("text-align" to "center"), modifier2.styles, "textAlign() with TextAlign enum should work correctly")
    }

    @Test
    fun testTextTransformMethods() {
        // Test with string value
        val modifier1 = Modifier().textTransform("uppercase")
        assertEquals(mapOf("text-transform" to "uppercase"), modifier1.styles, "textTransform() should set text-transform property")

        // Test with enum value
        val modifier2 = Modifier().textTransform(TextTransform.Uppercase)
        assertEquals(mapOf("text-transform" to "uppercase"), modifier2.styles, "textTransform() with TextTransform enum should work correctly")
    }

    @Test
    fun testLetterSpacingMethods() {
        // Test with string value
        val modifier1 = Modifier().letterSpacing("2px")
        assertEquals(mapOf("letter-spacing" to "2px"), modifier1.styles, "letterSpacing() should set letter-spacing property")

        // Test with numeric value
        val modifier2 = Modifier().letterSpacing(3)
        assertEquals(mapOf("letter-spacing" to "3px"), modifier2.styles, "letterSpacing() with numeric value should add 'px' unit")
    }

    @Test
    fun testWhiteSpaceMethod() {
        val modifier = Modifier().whiteSpace("nowrap")
        assertEquals(mapOf("white-space" to "nowrap"), modifier.styles, "whiteSpace() should set white-space property")
    }

    @Test
    fun testWordBreakMethod() {
        val modifier = Modifier().wordBreak("break-all")
        assertEquals(mapOf("word-break" to "break-all"), modifier.styles, "wordBreak() should set word-break property")
    }

    @Test
    fun testWordSpacingMethod() {
        val modifier = Modifier().wordSpacing("2px")
        assertEquals(mapOf("word-spacing" to "2px"), modifier.styles, "wordSpacing() should set word-spacing property")
    }

    @Test
    fun testTextShadowMethod() {
        val modifier = Modifier().textShadow("1px 1px 2px black")
        assertEquals(mapOf("text-shadow" to "1px 1px 2px black"), modifier.styles, "textShadow() should set text-shadow property")
    }

    @Test
    fun testLineHeightMethod() {
        val modifier = Modifier().lineHeight("1.5")
        assertEquals(mapOf("line-height" to "1.5"), modifier.styles, "lineHeight() should set line-height property")
    }

    @Test
    fun testTextOverflowMethod() {
        val modifier = Modifier().textOverflow("ellipsis")
        assertEquals(
            mapOf("overflow" to "hidden", "text-overflow" to "ellipsis"),
            modifier.styles,
            "textOverflow() should set both overflow and text-overflow properties"
        )
    }

    @Test
    fun testMaxLinesMethod() {
        val modifier = Modifier().maxLines(3)
        assertEquals(
            mapOf(
                "display" to "-webkit-box",
                "-webkit-line-clamp" to "3",
                "-webkit-box-orient" to "vertical",
                "overflow" to "hidden"
            ),
            modifier.styles,
            "maxLines() should set all required properties for line clamping"
        )
    }

    @Test
    fun testTextDecorationMethod() {
        val modifier = Modifier().textDecoration("underline")
        assertEquals(mapOf("text-decoration" to "underline"), modifier.styles, "textDecoration() should set text-decoration property")
    }

    @Test
    fun testChainedModifiers() {
        val modifier = Modifier()
            .background("red")
            .padding("10px")
            .margin("20px")
            .fontSize("16px")
            .color("white")
            .borderRadius("5px")
        
        val expected = mapOf(
            "background-color" to "red",
            "padding" to "10px",
            "margin" to "20px",
            "font-size" to "16px",
            "color" to "white",
            "border-radius" to "5px"
        )
        
        assertEquals(expected, modifier.styles, "Chained modifier methods should accumulate all styles")
    }
}