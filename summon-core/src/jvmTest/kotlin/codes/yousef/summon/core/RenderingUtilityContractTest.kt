package codes.yousef.summon.core

import codes.yousef.summon.core.rendering.*
import codes.yousef.summon.i18n.*
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.backgroundColor
import codes.yousef.summon.modifier.style
import codes.yousef.summon.routing.Location
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RenderingUtilityContractTest {
    private fun rtl(direction: LayoutDirection): Pair<Boolean, Modifier> {
        var isRtl = false
        lateinit var modifier: Modifier
        PlatformRenderer().renderComposableRoot {
            LanguageProvider(Language(direction.name, direction.name, direction)) {
                isRtl = RtlUtils.isRtl()
                assertEquals(if (direction == LayoutDirection.LTR) "ltr" else "rtl", RtlUtils.directionalValue("ltr", "rtl"))
                modifier = Modifier().directionalPadding("1", "2", "3", "4")
                    .directionalMargin("5", "6", "7", "8").mirrorInRtl().directionalRow()
            }
        }
        return isRtl to modifier
    }

    @Test
    fun rtlUtilitiesSwapLogicalEdgesAndMirrorOnlyRtl() {
        val (ltrFlag, ltr) = rtl(LayoutDirection.LTR)
        assertFalse(ltrFlag)
        assertEquals("2 3 4 1", ltr.styles["padding"])
        assertEquals("6 7 8 5", ltr.styles["margin"])
        assertEquals(null, ltr.styles["transform"])
        assertEquals("row", ltr.styles["flex-direction"])

        val (rtlFlag, rtl) = rtl(LayoutDirection.RTL)
        assertTrue(rtlFlag)
        assertEquals("2 1 4 3", rtl.styles["padding"])
        assertEquals("6 5 8 7", rtl.styles["margin"])
        assertEquals("scaleX(-1)", rtl.styles["transform"])
        assertEquals("row-reverse", rtl.styles["flex-direction"])
    }

    @Test
    fun renderingUtilitiesNormalizeMergeAndComposeRenderData() {
        assertEquals("background-color", RenderingUtils.toKebabCase("backgroundColor"))
        assertEquals("backgroundColor", RenderingUtils.toCamelCase("background-color"))
        assertEquals("font-size", RenderingUtils.normalizeCssProperty("font-size"))
        assertEquals("font-size", RenderingUtils.normalizeCssProperty("fontSize"))
        assertEquals("", RenderingUtils.createStyleString(emptyMap()))
        assertEquals("font-size: 12px; color: red;", RenderingUtils.createStyleString(linkedMapOf("fontSize" to "12px", "color" to "red")))
        assertEquals(mapOf("a" to "second", "b" to "third"), RenderingUtils.mergeStyles(mapOf("a" to "first"), mapOf("a" to "second", "b" to "third")))

        val modifier = Modifier().backgroundColor("black").style("color", "white")
        val data = modifier.withRenderData(
            additionalStyles = mapOf("color" to "red"),
            accessibilityAttributes = mapOf("role" to "status"),
            customAttributes = mapOf("data-id" to "one")
        )
        assertEquals(mapOf("background-color" to "black", "color" to "red"), data.getCombinedStyles())
        assertEquals(mapOf("role" to "status", "data-id" to "one"), data.getAllAttributes())
        assertTrue(data.getStyleString().contains("background-color:black"))
        assertEquals("background-color: black; color: white;", modifier.getNormalizedStyleString())
        assertEquals("", ComponentRenderData(Modifier()).getStyleString())
    }

    @Test
    fun locationParsesAndRebuildsPathQueryHashAndState() {
        val parsed = Location.fromUrl("/path?a=1&empty=#section")
        assertEquals("/path", parsed.path)
        assertEquals(mapOf("a" to "1", "empty" to ""), parsed.queryParams)
        assertEquals("section", parsed.hash)
        assertEquals("/path?a=1&empty=#section", parsed.fullPath)
        assertEquals("/next?a=1&empty=#section", parsed.withPath("/next").fullPath)
        assertEquals("/path?b=2#section", parsed.withQueryParams(mapOf("b" to "2")).fullPath)
        assertEquals("/path?a=1&empty=&b=2#section", parsed.withQueryParam("b", "2").fullPath)
        assertEquals("/path?a=1&empty=#other", parsed.withHash("other").fullPath)
        assertEquals(mapOf("key" to "value"), parsed.withState(mapOf("key" to "value")).state)
        assertEquals("/plain", Location.fromUrl("/plain").fullPath)
    }
}
