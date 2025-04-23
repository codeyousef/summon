package code.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertEquals

class StylingModifierExtrasTest {

    @Test
    fun testFontStyle() {
        val value = "oblique"
        val modifier = with(StylingModifierExtras) {
            Modifier().fontStyle(value)
        }
        assertEquals(value, modifier.styles["font-style"], "StylingModifierExtras.fontStyle failed.")
    }

    @Test
    fun testTextAlign() {
        val stringValue = "justify"
        val modifierString = with(StylingModifierExtras) {
            Modifier().textAlign(stringValue)
        }
        assertEquals(stringValue, modifierString.styles["text-align"], "StylingModifierExtras.textAlign(String) failed.")

        val enumValue = TextAlign.End
        val modifierEnum = with(StylingModifierExtras) {
            Modifier().textAlign(enumValue)
        }
        assertEquals(enumValue.toString(), modifierEnum.styles["text-align"], "StylingModifierExtras.textAlign(Enum) failed.")
    }

    @Test
    fun testTextDecoration() {
        val value = "line-through"
        val modifier = with(StylingModifierExtras) {
            Modifier().textDecoration(value)
        }
        assertEquals(value, modifier.styles["text-decoration"], "StylingModifierExtras.textDecoration failed.")
    }

    @Test
    fun testBoxShadow() {
        val value = "2px 2px 5px rgba(0,0,0,0.3)"
        val modifier = with(StylingModifierExtras) {
            Modifier().boxShadow(value)
        }
        assertEquals(value, modifier.styles["box-shadow"], "StylingModifierExtras.boxShadow failed.")
    }

    @Test
    fun testTransition() {
        val value = "color 0.3s ease-in-out"
        val modifier = with(StylingModifierExtras) {
            Modifier().transition(value)
        }
        assertEquals(value, modifier.styles["transition"], "StylingModifierExtras.transition failed.")
    }

    @Test
    fun testTransform() {
        val value = "rotate(45deg)"
        val modifier = with(StylingModifierExtras) {
            Modifier().transform(value)
        }
        assertEquals(value, modifier.styles["transform"], "StylingModifierExtras.transform failed.")
    }
} 