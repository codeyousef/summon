package codes.yousef.summon.i18n

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertEquals

class DirectionalModifiersContractTest {
    private fun directional(direction: LayoutDirection): Modifier {
        lateinit var result: Modifier
        PlatformRenderer().renderComposableRoot {
            LanguageProvider(Language(direction.name.lowercase(), direction.name, direction)) {
                result = Modifier()
                    .paddingStart("1px").paddingEnd("2px")
                    .marginStart("3px").marginEnd("4px")
                    .borderStart("1px", "solid", "red")
                    .borderEnd("2px", "dashed", "blue")
                    .flexRow().textStart().textEnd().withDirection()
            }
        }
        return result
    }

    @Test
    fun logicalEdgesResolveForLtrAndRtl() {
        val ltr = directional(LayoutDirection.LTR)
        assertEquals("1px", ltr.styles["padding-left"])
        assertEquals("2px", ltr.styles["padding-right"])
        assertEquals("3px", ltr.styles["margin-left"])
        assertEquals("4px", ltr.styles["margin-right"])
        assertEquals("1px solid red", ltr.styles["border-left"])
        assertEquals("2px dashed blue", ltr.styles["border-right"])
        assertEquals("row", ltr.styles["flex-direction"])
        assertEquals("right", ltr.styles["text-align"])
        assertEquals("ltr", ltr.attributes["dir"])

        val rtl = directional(LayoutDirection.RTL)
        assertEquals("2px", rtl.styles["padding-left"])
        assertEquals("1px", rtl.styles["padding-right"])
        assertEquals("4px", rtl.styles["margin-left"])
        assertEquals("3px", rtl.styles["margin-right"])
        assertEquals("2px dashed blue", rtl.styles["border-left"])
        assertEquals("1px solid red", rtl.styles["border-right"])
        assertEquals("row-reverse", rtl.styles["flex-direction"])
        assertEquals("left", rtl.styles["text-align"])
        assertEquals("rtl", rtl.attributes["dir"])
    }
}
