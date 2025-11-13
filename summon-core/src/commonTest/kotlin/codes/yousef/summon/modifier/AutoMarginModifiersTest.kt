package codes.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertEquals

class AutoMarginModifiersTest {

    @Test
    fun testMarginHorizontalAuto_String() {
        // Test default (explicitly providing default String is not needed as it's unambiguous with named param)
        val modifierStrDefault =
            Modifier().marginHorizontalAuto(vertical = "0px") // Explicitly call String version default
        assertEquals("0px auto", modifierStrDefault.styles["margin"], "HorizontalAuto String default failed.")

        // Test with value
        val modifierStrValue = Modifier().marginHorizontalAuto(vertical = "10px")
        assertEquals("10px auto", modifierStrValue.styles["margin"], "HorizontalAuto String value failed.")
    }

    @Test
    fun testMarginHorizontalAuto_Number() {
        // Test default
        val modifierNumDefault = Modifier().marginHorizontalAuto(vertical = 0) // Explicitly call Number version default
        assertEquals("0px auto", modifierNumDefault.styles["margin"], "HorizontalAuto Number default failed.")

        // Test with value
        val modifierNumValue = Modifier().marginHorizontalAuto(vertical = 5)
        assertEquals("5px auto", modifierNumValue.styles["margin"], "HorizontalAuto Number value failed.")
    }

    @Test
    fun testMarginHorizontalAutoZero() {
        val modifier = Modifier().marginHorizontalAutoZero()
        assertEquals("0px auto", modifier.styles["margin"], "HorizontalAutoZero failed.")
    }

    @Test
    fun testMarginVerticalAuto_String() {
        // Test default
        val modifierStrDefault =
            Modifier().marginVerticalAuto(horizontal = "0px") // Explicitly call String version default
        assertEquals("auto 0px", modifierStrDefault.styles["margin"], "VerticalAuto String default failed.")

        // Test with value
        val modifierStrValue = Modifier().marginVerticalAuto(horizontal = "20px")
        assertEquals("auto 20px", modifierStrValue.styles["margin"], "VerticalAuto String value failed.")
    }

    @Test
    fun testMarginVerticalAuto_Number() {
        // Test default
        val modifierNumDefault = Modifier().marginVerticalAuto(horizontal = 0) // Explicitly call Number version default
        assertEquals("auto 0px", modifierNumDefault.styles["margin"], "VerticalAuto Number default failed.")

        // Test with value
        val modifierNumValue = Modifier().marginVerticalAuto(horizontal = 8)
        assertEquals("auto 8px", modifierNumValue.styles["margin"], "VerticalAuto Number value failed.")
    }

    @Test
    fun testMarginVerticalAutoZero() {
        val modifier = Modifier().marginVerticalAutoZero()
        assertEquals("auto 0px", modifier.styles["margin"], "VerticalAutoZero failed.")
    }

    @Test
    fun testMarginAuto() {
        val modifier = Modifier().marginAuto()
        assertEquals("auto", modifier.styles["margin"], "MarginAuto failed.")
    }
} 