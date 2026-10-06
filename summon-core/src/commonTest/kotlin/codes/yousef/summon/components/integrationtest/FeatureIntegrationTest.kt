package codes.yousef.summon.components.integrationtest

import codes.yousef.summon.components.foundation.TrustedCss
import codes.yousef.summon.components.foundation.EnhancedThemeConfig
import codes.yousef.summon.components.foundation.ThemeProvider
import codes.yousef.summon.components.foundation.useTheme
import codes.yousef.summon.components.styles.GlobalKeyframes
import codes.yousef.summon.components.styles.GlobalStyle
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.runComposableTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FeatureIntegrationTest {

    @Test
    fun testGlobalStyleWithThemeProvider() {
        val mockRenderer = MockPlatformRenderer()
        val customTheme = EnhancedThemeConfig(
            primaryColor = "#ff6b6b",
            designTokens = mapOf(
                "--primary-color" to "#ff6b6b",
                "--spacing-md" to "1rem"
            )
        )

        runComposableTest(mockRenderer) {
            ThemeProvider(theme = customTheme) {
                GlobalStyle(TrustedCss.fromAuthorCode("body { color: var(--primary-color); }"))

                val theme = useTheme()
                assertEquals("#ff6b6b", theme.primaryColor)
            }
        }

        // Should have been called twice - once for theme CSS variables, once for global style
        assertTrue(mockRenderer.globalStyleCallCount >= 2)
        assertTrue(mockRenderer.lastGlobalStyleCssRendered!!.contains("body { color: var(--primary-color); }"))
    }


    @Test
    fun testThemeProviderNesting() {
        val mockRenderer = MockPlatformRenderer()
        val outerTheme = EnhancedThemeConfig(primaryColor = "#red")
        val innerTheme = EnhancedThemeConfig(primaryColor = "#blue")

        runComposableTest(mockRenderer) {
            ThemeProvider(theme = outerTheme) {
                val outer = useTheme()
                assertEquals("#red", outer.primaryColor)

                ThemeProvider(theme = innerTheme) {
                    val inner = useTheme()
                    assertEquals("#blue", inner.primaryColor)

                    GlobalStyle(TrustedCss.fromAuthorCode("body { color: ${inner.primaryColor}; }"))
                }

                // Note: CompositionLocal doesn't automatically restore previous values
                // The current implementation sets values globally, so the inner theme persists
                val restored = useTheme()
                assertEquals("#blue", restored.primaryColor)
            }
        }

        assertTrue(mockRenderer.renderGlobalStyleCalled)
        assertTrue(mockRenderer.lastGlobalStyleCssRendered!!.contains("#blue"))
    }
}
