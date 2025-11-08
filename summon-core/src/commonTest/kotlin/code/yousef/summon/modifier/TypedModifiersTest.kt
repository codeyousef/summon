package code.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("DEPRECATION")
class TypedModifiersTest {

    // --- Text Tests --- 

    @Test
    fun testFontFamily() {
        val value = "Arial, sans-serif"
        val modifier = Modifier().fontFamily(value)
        assertEquals(value, modifier.styles["font-family"])
    }

    @Test
    fun testFontStyle() {
        val value = "italic"
        val modifier = Modifier().fontStyle(value)
        assertEquals(value, modifier.styles["font-style"])
    }

    @Test
    fun testFontStyleEnum() {
        val modifier = Modifier().fontStyle(FontStyle.Oblique)
        assertEquals("oblique", modifier.styles["font-style"])
    }

    @Test
    fun testFontWeight() {
        val value = "bold"
        val modifier = Modifier().fontWeight(value)
        assertEquals(value, modifier.styles["font-weight"])
    }

    @Test
    fun testTextAlign() {
        val stringValue = "center"
        val modifierString = Modifier().textAlign(stringValue)
        assertEquals(stringValue, modifierString.styles["text-align"])

        val enumValue = TextAlign.Right
        val modifierEnum = Modifier().textAlign(enumValue)
        assertEquals(enumValue.toString(), modifierEnum.styles["text-align"])
    }

    @Test
    fun testTextDecoration() {
        val value = "underline"
        val modifier = Modifier().textDecoration(value)
        assertEquals(value, modifier.styles["text-decoration"])
    }

    @Test
    fun testTextDecorationEnumOverloads() {
        val enumModifier = Modifier().textDecoration(TextDecoration.Underline)
        assertEquals("underline", enumModifier.styles["text-decoration"])

        val combined = Modifier().textDecoration(TextDecoration.Underline, TextDecoration.LineThrough)
        assertEquals("underline line-through", combined.styles["text-decoration"])
    }

    @Test
    fun testWhiteSpace() {
        val stringModifier = Modifier().whiteSpace("nowrap")
        assertEquals("nowrap", stringModifier.styles["white-space"])

        val enumModifier = Modifier().whiteSpace(WhiteSpace.PreWrap)
        assertEquals("pre-wrap", enumModifier.styles["white-space"])
    }

    @Test
    fun testLineHeight() {
        val stringValue = "1.5"
        val modifierString = Modifier().lineHeight(stringValue)
        assertEquals(stringValue, modifierString.styles["line-height"])

        val numberValue = 2
        val modifierNumber = Modifier().lineHeight(numberValue)
        assertEquals(numberValue.toString(), modifierNumber.styles["line-height"])
    }

    @Test
    fun testLetterSpacing() {
        val stringValue = "normal"
        val modifierString = Modifier().letterSpacing(stringValue)
        assertEquals(stringValue, modifierString.styles["letter-spacing"])

        val numberValue = 3
        val modifierNumber = Modifier().letterSpacing(numberValue)
        assertEquals("${numberValue}px", modifierNumber.styles["letter-spacing"])
    }

    @Test
    fun testTextTransform() {
        val stringValue = "uppercase"
        val modifierString = Modifier().textTransform(stringValue)
        assertEquals(stringValue, modifierString.styles["text-transform"])

        val enumValue = TextTransform.Capitalize
        val modifierEnum = Modifier().textTransform(enumValue)
        assertEquals(enumValue.toString(), modifierEnum.styles["text-transform"])
    }

    // --- Media Tests ---

    @Test
    fun testObjectFit() {
        val value = "contain"
        val modifier = Modifier().objectFit(value)
        assertEquals(value, modifier.styles["object-fit"])
    }

    // --- Scrollable Tests ---

    @Test
    fun testScrollBehavior() {
        val value = "smooth"
        val modifier = Modifier().scrollBehavior(value)
        assertEquals(value, modifier.styles["scroll-behavior"])
    }

    @Test
    fun testScrollbarWidth() {
        val value = "thin"
        val modifier = Modifier().scrollbarWidth(value)
        assertEquals(value, modifier.styles["scrollbar-width"])
    }
} 
