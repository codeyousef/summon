package codes.yousef.summon.css

import codes.yousef.summon.components.foundation.TrustedCss
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TrustedCssInjectorJvmTest {
    @AfterTest
    fun clearStyles() {
        CssInjector.clear()
    }

    @Test
    fun trustedStylesRequireSafeBlockIdentifiersAndRemainVerbatim() {
        val css = TrustedCss.fromAuthorCode("body { background: url('/owned.png'); }")

        assertTrue(CssInjector.injectTrustedCss("application-theme", css))
        assertEquals(css.value, CssInjector.getTrustedCss("application-theme"))
        assertTrue(CssInjector.hasTrustedCss("application-theme"))

        assertFalse(CssInjector.injectTrustedCss("bad\" onload=alert(1)", css))
        assertNull(CssInjector.getTrustedCss("bad\" onload=alert(1)"))
        assertFalse(CssInjector.generateStyleBlocks().contains("onload"))
    }
}
