package codes.yousef.summon.ssr

import codes.yousef.summon.security.PublicHydrationState
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class HydrationSecurityTest {
    @Test
    fun publicStateCannotBreakOutOfInertJsonScript() {
        val markup = HydrationUtils.generateHydrationMarkup(
            publicState = PublicHydrationState("""{"label":"</script><script>alert(1)</script>"}""")
        )

        assertFalse(markup.contains("</script><script>alert(1)</script>"))
        assertContains(markup, "\\u003c/script>\\u003cscript>alert(1)\\u003c/script>")
        assertEquals(1, Regex("<script src=").findAll(markup).count())
        assertContains(markup, "<script src=\"/summon-hydration.js\" defer></script>")
    }

    @Test
    fun publicStateIsBoundedAndAsciiOnly() {
        assertFailsWith<IllegalArgumentException> { PublicHydrationState("x".repeat(65_537)) }
        assertFailsWith<IllegalArgumentException> { PublicHydrationState("é") }
    }
}
