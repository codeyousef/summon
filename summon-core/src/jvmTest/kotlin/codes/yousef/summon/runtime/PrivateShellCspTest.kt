package codes.yousef.summon.runtime

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.foundation.TrustedCss
import codes.yousef.summon.components.styles.GlobalStyle
import codes.yousef.summon.security.PublicHydrationState
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PrivateShellCspTest {
    @Test
    fun privateShellBindsNonceAndEmitsNoExecutableInlineScript() {
        val renderer = PlatformRenderer()
        val document = renderer.renderPrivateShell(
            publicState = PublicHydrationState("""{"value":"</script><script>alert(1)</script>"}""")
        ) {
            GlobalStyle(TrustedCss.fromAuthorCode(".private-shell { color: #123456; }"))
            Text("Public shell")
        }

        val nonce = assertNotNull(
            Regex("""<meta name="summon-style-nonce" content="([A-Za-z0-9_-]{43})">""")
                .find(document.html)
                ?.groupValues
                ?.get(1)
        )
        assertTrue(document.contentSecurityPolicy.contains("default-src 'none'"))
        assertTrue(document.contentSecurityPolicy.contains("script-src 'self' 'wasm-unsafe-eval'"))
        assertTrue(document.contentSecurityPolicy.contains("script-src-attr 'none'"))
        assertTrue(document.contentSecurityPolicy.contains("style-src 'self' 'nonce-$nonce'"))
        assertTrue(document.contentSecurityPolicy.contains("frame-ancestors 'none'"))
        assertFalse(document.contentSecurityPolicy.contains("'unsafe-eval'"))
        assertFalse(document.contentSecurityPolicy.contains("script-src 'unsafe-inline'"))
        assertTrue(document.html.contains("<style nonce=\"$nonce\">"))
        assertFalse(document.html.contains("</script><script>alert(1)</script>"))
        assertFalse(Regex("""\son[a-z]+\s*=""", RegexOption.IGNORE_CASE).containsMatchIn(document.html))

        Regex("""<script\b([^>]*)>(.*?)</script>""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .findAll(document.html)
            .forEach { match ->
                val attributes = match.groupValues[1]
                val content = match.groupValues[2].trim()
                if (!attributes.contains("src=")) {
                    assertTrue(attributes.contains("type=\"application/json"))
                    assertFalse(content.contains('<'))
                }
            }
    }
}
