package codes.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StylingModifierGuardTest {
    @Test
    fun filterBuildersRejectEmptyAndRetainEveryConfiguredFunction() {
        assertFailsWith<IllegalArgumentException> { Modifier().filter {} }
        assertFailsWith<IllegalArgumentException> { Modifier().backdropFilter {} }
        val filter = Modifier().filter {
            blur(2)
            brightness(1.1)
            contrast(0.9)
            grayscale(0.2)
            hueRotate(30)
            invert(1)
            saturate(1.2)
            sepia(0.4)
            dropShadow("1px", "2px")
            raw("url(#custom)")
        }.styles.getValue("filter")
        assertContains(filter, "blur(2px)")
        assertContains(filter, "drop-shadow(1px 2px 0 currentColor)")
        assertContains(filter, "url(#custom)")
    }

    @Test
    fun multiFilterCoversAbsentAndPresentOptionsAndBlendModeGuards() {
        assertEquals("", Modifier().multiFilter().styles["filter"])
        assertEquals(
            "blur(2px) brightness(1.1) contrast(0.9) saturate(1.2)",
            Modifier().multiFilter(2, 1.1, 0.9, 1.2).styles["filter"],
        )
        assertFailsWith<IllegalArgumentException> { Modifier().backgroundBlendModes() }
        assertEquals(
            "multiply, screen",
            Modifier().backgroundBlendModes(BlendMode.Multiply, BlendMode.Screen).styles["background-blend-mode"],
        )
    }
}
