package code.yousef.summon.components.style

import code.yousef.summon.runtime.MockPlatformRenderer
import code.yousef.summon.util.runComposableTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GlobalStyleTest {

    @Test
    fun testGlobalStyleWithBasicCSS() {
        val mockRenderer = MockPlatformRenderer()
        runComposableTest(mockRenderer) {
            GlobalStyle("body { margin: 0; padding: 0; }")
        }
        
        assertTrue(mockRenderer.renderGlobalStyleCalled)
        assertEquals("body { margin: 0; padding: 0; }", mockRenderer.lastGlobalStyleCssRendered)
    }

    @Test
    fun testGlobalStyleWithKeyframes() {
        val mockRenderer = MockPlatformRenderer()
        runComposableTest(mockRenderer) {
            GlobalStyle("""
                @keyframes fadeIn {
                    from { opacity: 0; }
                    to { opacity: 1; }
                }
                .fade { animation: fadeIn 0.3s ease-in-out; }
            """.trimIndent())
        }
        
        assertTrue(mockRenderer.renderGlobalStyleCalled)
        assertNotNull(mockRenderer.lastGlobalStyleCssRendered)
        assertTrue(mockRenderer.lastGlobalStyleCssRendered!!.contains("@keyframes fadeIn"))
        assertTrue(mockRenderer.lastGlobalStyleCssRendered!!.contains("animation: fadeIn"))
    }

    @Test
    fun testGlobalKeyframes() {
        val mockRenderer = MockPlatformRenderer()
        runComposableTest(mockRenderer) {
            GlobalKeyframes(
                name = "slideUp",
                keyframes = """
                    0% { transform: translateY(100%); }
                    100% { transform: translateY(0); }
                """.trimIndent()
            )
        }
        
        assertTrue(mockRenderer.renderGlobalStyleCalled)
        assertNotNull(mockRenderer.lastGlobalStyleCssRendered)
        assertTrue(mockRenderer.lastGlobalStyleCssRendered!!.contains("@keyframes slideUp"))
        assertTrue(mockRenderer.lastGlobalStyleCssRendered!!.contains("transform: translateY"))
    }

    @Test
    fun testCssVariables() {
        val mockRenderer = MockPlatformRenderer()
        runComposableTest(mockRenderer) {
            CssVariables(mapOf(
                "--primary-color" to "#007bff",
                "--border-radius" to "4px",
                "--font-size-base" to "16px"
            ))
        }
        
        assertTrue(mockRenderer.renderGlobalStyleCalled)
        assertNotNull(mockRenderer.lastGlobalStyleCssRendered)
        assertTrue(mockRenderer.lastGlobalStyleCssRendered!!.contains("--primary-color: #007bff"))
        assertTrue(mockRenderer.lastGlobalStyleCssRendered!!.contains("--border-radius: 4px"))
        assertTrue(mockRenderer.lastGlobalStyleCssRendered!!.contains("--font-size-base: 16px"))
    }

    @Test
    fun testMediaQuery() {
        val mockRenderer = MockPlatformRenderer()
        runComposableTest(mockRenderer) {
            MediaQuery(
                query = "@media (max-width: 768px)",
                css = """
                    .container { 
                        padding: 1rem; 
                        font-size: 14px; 
                    }
                """.trimIndent()
            )
        }
        
        assertTrue(mockRenderer.renderGlobalStyleCalled)
        assertNotNull(mockRenderer.lastGlobalStyleCssRendered)
        assertTrue(mockRenderer.lastGlobalStyleCssRendered!!.contains("@media (max-width: 768px)"))
        assertTrue(mockRenderer.lastGlobalStyleCssRendered!!.contains("padding: 1rem"))
    }

    @Test
    fun testMultipleGlobalStyles() {
        val mockRenderer = MockPlatformRenderer()
        runComposableTest(mockRenderer) {
            GlobalStyle("body { margin: 0; }")
            GlobalStyle(".container { max-width: 1200px; }")
            GlobalKeyframes("spin", "from { transform: rotate(0deg); } to { transform: rotate(360deg); }")
        }
        
        // Should be called 3 times
        assertEquals(3, mockRenderer.globalStyleCallCount)
    }
}