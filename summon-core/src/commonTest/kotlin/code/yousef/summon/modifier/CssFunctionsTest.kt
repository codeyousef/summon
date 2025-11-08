package code.yousef.summon.modifier

import code.yousef.summon.extensions.px
import code.yousef.summon.extensions.vw
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CssFunctionsTest {

    @Test
    fun cssMinBuildsFunction() {
        assertEquals("min(1200px, 92vw)", cssMin(1200.px, 92.vw))
    }

    @Test
    fun cssMaxBuildsFunction() {
        assertEquals("max(48rem, 60vw, 900px)", cssMax("48rem", "60vw", "900px"))
    }

    @Test
    fun cssClampBuildsFunction() {
        assertEquals("clamp(22px, 4vw, 48px)", cssClamp(22.px, 4.vw, 48.px))
    }

    @Test
    fun cssMinRequiresAtLeastTwoValues() {
        assertFailsWith<IllegalArgumentException> {
            cssMin("100px")
        }
    }
}
