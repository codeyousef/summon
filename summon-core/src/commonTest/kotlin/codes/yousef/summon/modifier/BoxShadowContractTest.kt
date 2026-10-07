package codes.yousef.summon.modifier

import codes.yousef.summon.core.style.Color
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BoxShadowContractTest {
    private val color = Color.fromHex("#112233")

    @Test
    fun allShadowOverloadsProduceStableCss() {
        val values = listOf(
            Modifier().boxShadow("raw").styles.getValue("box-shadow"),
            Modifier().boxShadow(1, 2, "3px", color).styles.getValue("box-shadow"),
            Modifier().boxShadow("1px", "2px", "3px", color).styles.getValue("box-shadow"),
            Modifier().boxShadow(1, 2, "3px", "4px", color).styles.getValue("box-shadow"),
            Modifier().boxShadow("1px", "2px", "3px", "4px", color).styles.getValue("box-shadow"),
            Modifier().boxShadow(1, 2, "3px", color, inset = true).styles.getValue("box-shadow"),
            Modifier().boxShadow(1, 2, "3px", "4px", color, inset = true).styles.getValue("box-shadow"),
            Modifier().boxShadow("1px", "2px", "3px", color, inset = true).styles.getValue("box-shadow"),
            Modifier().boxShadow("1px", "2px", "3px", "4px", color, inset = true).styles.getValue("box-shadow")
        )
        assertEquals("raw", values.first())
        assertTrue(values.takeLast(4).all { it.startsWith("inset ") })
        assertFalse(values[1].startsWith("inset "))
    }

    @Test
    fun configurationsCoverSpreadInsetGlowAndComposition() {
        val plain = ShadowConfig.create(1, 2, 3, color = color)
        val spread = ShadowConfig.create(1, 2, 3, 4, color, true)
        val glow = ShadowConfig.glow(8, color)
        val intenseGlow = ShadowConfig.glow(8, color, 2)
        val inner = ShadowConfig.innerGlow(8, color)
        assertFalse(plain.toCssString().startsWith("inset"))
        assertContains(spread.toCssString(), "inset")
        assertEquals(null, glow.spreadRadius)
        assertEquals("2px", intenseGlow.spreadRadius)
        assertTrue(inner.inset)

        val fromString = shadowConfig(1, 2, 3, color = "#112233")
        val fromColor = shadowConfig(1, 2, 3, 4, color, true)
        val multiple = Modifier().multipleShadows(fromString, fromColor)
        assertContains(multiple.styles.getValue("box-shadow"), ", ")
        assertContains(Modifier().addShadow(fromString).addShadow(fromColor).styles.getValue("box-shadow"), ", ")
        assertContains(Modifier().addShadow(1, 2, 3, color = "#112233").styles.getValue("box-shadow"), "1px")
        assertContains(Modifier().addShadow(1, 2, 3, color = color).styles.getValue("box-shadow"), "1px")
    }

    @Test
    fun glassAndGlowHelpersRetainParameters() {
        assertEquals("blur(12px) brightness(1.2)", Modifier().glassMorphism("12px", 1.2).styles["backdrop-filter"])
        assertEquals("blur(10px) brightness(1.05)", Modifier().glassMorphism(10).styles["backdrop-filter"])
        assertEquals("", Modifier().glow("#fff", intensity = 0).styles["box-shadow"])
        assertContains(Modifier().glow("#fff", intensity = 2, size = 5).styles.getValue("box-shadow"), ", ")
    }
}
