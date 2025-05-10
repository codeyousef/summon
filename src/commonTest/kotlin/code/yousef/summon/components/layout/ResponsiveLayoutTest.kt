package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.MockPlatformRenderer
import code.yousef.summon.util.runTestComposable
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

/**
 * Tests for the ResponsiveLayout component
 */
class ResponsiveLayoutTest {

    @Test
    fun testResponsiveLayoutWithDefaultParameters() {
        val mockRenderer = MockPlatformRenderer()
        runTestComposable(mockRenderer) {
            ResponsiveLayout(
                content = mapOf(
                    ScreenSize.SMALL to { /* Small screen content */ },
                    ScreenSize.LARGE to { /* Large screen content */ }
                ),
                defaultContent = { /* Default content */ }
            )
            assertTrue(mockRenderer.renderResponsiveLayoutCalled, "renderResponsiveLayout should have been called")

            val styles = mockRenderer.lastResponsiveLayoutModifierRendered?.styles ?: emptyMap()
            assertContains(styles, "position")
            assertEquals("relative", styles["position"])
            assertContains(styles, "__attr:data-client-detection")
            assertEquals("true", styles["__attr:data-client-detection"]) // Default is client-side detection

            assertNotNull(mockRenderer.lastResponsiveLayoutContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testResponsiveLayoutWithCustomModifier() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier().background("green")

        runTestComposable(mockRenderer) {
            ResponsiveLayout(
                content = mapOf(
                    ScreenSize.SMALL to { /* Small screen content */ },
                    ScreenSize.LARGE to { /* Large screen content */ }
                ),
                defaultContent = { /* Default content */ },
                modifier = customModifier
            )
            assertTrue(mockRenderer.renderResponsiveLayoutCalled, "renderResponsiveLayout should have been called")

            val styles = mockRenderer.lastResponsiveLayoutModifierRendered?.styles ?: emptyMap()
            assertContains(styles, "background-color")
            assertEquals("green", styles["background-color"])
            assertContains(styles, "position")
            assertEquals("relative", styles["position"])

            assertNotNull(mockRenderer.lastResponsiveLayoutContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testResponsiveLayoutWithServerSideRendering() {
        val mockRenderer = MockPlatformRenderer()

        runTestComposable(mockRenderer) {
            ResponsiveLayout(
                content = mapOf(
                    ScreenSize.SMALL to { /* Small screen content */ },
                    ScreenSize.MEDIUM to { /* Medium screen content */ }
                ),
                defaultContent = { /* Default content */ },
                detectScreenSizeClient = false,
                serverSideScreenSize = ScreenSize.MEDIUM
            )
            assertTrue(mockRenderer.renderResponsiveLayoutCalled, "renderResponsiveLayout should have been called")

            val styles = mockRenderer.lastResponsiveLayoutModifierRendered?.styles ?: emptyMap()
            assertContains(styles, "__attr:data-client-detection")
            assertEquals("false", styles["__attr:data-client-detection"])
            assertContains(styles, "__attr:data-server-size")
            assertEquals("MEDIUM", styles["__attr:data-server-size"])

            assertNotNull(mockRenderer.lastResponsiveLayoutContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testScreenSizeEnum() {
        // Test that the ScreenSize enum has the expected values
        assertEquals(4, ScreenSize.values().size, "ScreenSize should have 4 values")
        assertEquals(ScreenSize.SMALL, ScreenSize.valueOf("SMALL"))
        assertEquals(ScreenSize.MEDIUM, ScreenSize.valueOf("MEDIUM"))
        assertEquals(ScreenSize.LARGE, ScreenSize.valueOf("LARGE"))
        assertEquals(ScreenSize.XLARGE, ScreenSize.valueOf("XLARGE"))
    }

    @Test
    fun testResponsiveBreakpoints() {
        // Test that the ResponsiveBreakpoints object has the expected values
        assertEquals(600, ResponsiveBreakpoints.SMALL_BREAKPOINT)
        assertEquals(960, ResponsiveBreakpoints.MEDIUM_BREAKPOINT)
        assertEquals(1280, ResponsiveBreakpoints.LARGE_BREAKPOINT)
    }
}
