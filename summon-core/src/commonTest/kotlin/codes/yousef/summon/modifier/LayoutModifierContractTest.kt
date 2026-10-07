package codes.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LayoutModifierContractTest {
    @Test
    fun conditionalInsetsMarginsAndPaddingPreserveEveryShorthand() {
        val base = Modifier()
        assertSame(base, base.positionInset())
        assertEquals(mapOf("top" to "1", "right" to "2", "bottom" to "3", "left" to "4"), base.positionInset("1", "2", "3", "4").styles)

        assertEquals("1", base.marginOf(top = "1").styles["margin-top"])
        assertEquals("2", base.marginOf(right = "2").styles["margin-right"])
        assertEquals("3", base.marginOf(bottom = "3").styles["margin-bottom"])
        assertEquals("4", base.marginOf(left = "4").styles["margin-left"])
        assertEquals("0 0 0 0", base.marginOf().styles["margin"])
        assertEquals("1 0 3 0", base.marginOf(top = "1", bottom = "3").styles["margin"])

        assertEquals("1", base.paddingOf(top = "1").styles["padding-top"])
        assertEquals("2", base.paddingOf(right = "2").styles["padding-right"])
        assertEquals("3", base.paddingOf(bottom = "3").styles["padding-bottom"])
        assertEquals("4", base.paddingOf(left = "4").styles["padding-left"])
        assertEquals("0 0 0 0", base.paddingOf().styles["padding"])
        assertEquals("0 2 0 4", base.paddingOf(right = "2", left = "4").styles["padding"])
    }

    @Test
    fun everyNamedMarginAndPaddingCombinationUsesDirectionalOrFourSideCss() {
        for (mask in 0 until 16) {
            val values = List(4) { index -> if (mask and (1 shl index) != 0) "${index + 1}px" else null }
            val expected = values.map { it ?: "0" }.joinToString(" ")
            val margin = Modifier().marginOf(values[0], values[1], values[2], values[3]).styles
            val padding = Modifier().paddingOf(values[0], values[1], values[2], values[3]).styles

            if (values.count { it != null } == 1) {
                val side = listOf("top", "right", "bottom", "left")[values.indexOfFirst { it != null }]
                assertEquals(values.first { it != null }, margin["margin-$side"], "margin mask $mask")
                assertEquals(values.first { it != null }, padding["padding-$side"], "padding mask $mask")
            } else {
                assertEquals(expected, margin["margin"], "margin mask $mask")
                assertEquals(expected, padding["padding"], "padding mask $mask")
            }
        }
    }

    @Test
    fun ratiosCircularPlacementAndAnimationValidateBoundaries() {
        assertEquals("1.5", Modifier().aspectRatio(1.5).styles["aspect-ratio"])
        assertEquals("16 / 9", Modifier().aspectRatio(16, 9.0).styles["aspect-ratio"])
        assertEquals("1.5 / 2.25", Modifier().aspectRatio(1.5, 2.25).styles["aspect-ratio"])

        RadialAngle.entries.forEach { angle ->
            val styles = Modifier().radialPosition(angle, RadialRadius.Small).styles
            assertEquals("absolute", styles["position"])
            assertTrue(styles.getValue("left").contains("px"))
            assertTrue(styles.getValue("top").contains("px"))
        }
        val exact = Modifier().circularLayout(RadialRadius.Medium, 8, 1)
        assertEquals("absolute", exact.styles["position"])
        val fallback = Modifier().circularLayout(RadialRadius.Large, 7, 1)
        assertEquals(300.0, fallback.styles.getValue("left").removePrefix("calc(50% + ").removeSuffix("px)").toDouble())
        assertFailsWith<IllegalArgumentException> { Modifier().circularLayout(RadialRadius.Small, 0, 0) }
        assertFailsWith<IllegalArgumentException> { Modifier().circularLayout(RadialRadius.Small, 2, -1) }
        assertFailsWith<IllegalArgumentException> { Modifier().circularLayout(RadialRadius.Small, 2, 2) }

        RotationSpeed.entries.forEach { speed ->
            assertTrue(Modifier().rotatingAnimation(speed, true).styles.getValue("animation").startsWith("rotate-360 "))
            assertTrue(Modifier().rotatingAnimation(speed, false).styles.getValue("animation").contains(" reverse "))
        }
        AnimationDuration.entries.forEach { duration ->
            FloatIntensity.entries.forEach { intensity ->
                val styles = Modifier().floatingAnimation(duration, intensity).styles
                assertTrue(styles.getValue("animation").startsWith("float-${intensity.value} "))
            }
        }
    }

    @Test
    fun basicLayoutModifiersMapToStableCssProperties() {
        val styles = Modifier()
            .minWidth("1px").minHeight("2px").maxHeight("3px")
            .inset("1px").inset("1px", "2px").inset("1px", "2px", "3px", "4px")
            .padding("1px", "2px").paddingTop("1px").paddingRight("2px").paddingBottom("3px").paddingLeft("4px")
            .margin("1px", "2px").margin("1px", "2px", "3px", "4px").centerHorizontally("5px")
            .flexWrap("wrap").flexWrap(FlexWrap.WrapReverse).flexGrow(2).flexShrink(0).flexBasis("10px").flex(1, 0, "auto")
            .alignSelf("center").alignContent("space-between").justifyItems("center").justifySelf("end")
            .gridGap("1px").gridArea("main").gridColumn("1 / 2").gridRow("2")
            .zIndex("3").overflowX("auto").overflowY("hidden").visibility("hidden").visibility(Visibility.Collapse)
            .cursor(Cursor.Pointer).centerAbsolute().fixedCenter().relativeOffset(2, -3)
            .position("relative").top("1px").right("2px").bottom("3px").left("4px")
            .flexDirection("column").display("grid").gridTemplateColumns("1fr").gridColumnGap("1px")
            .gridRowGap("2px").overflow("clip").alignItems("center").justifyContent("end").gap("3px").flex("1")
            .styles
        assertEquals("1px", styles["min-width"])
        assertEquals("grid", styles["display"])
        assertEquals("1", styles["flex"])
        assertEquals("4px", styles["left"])
    }
}
