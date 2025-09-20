package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.MockPlatformRenderer
import code.yousef.summon.theme.MediaQuery
import code.yousef.summon.util.runTestComposable
import kotlin.test.*

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
            assertContains(mockRenderer.lastResponsiveLayoutModifierRendered?.attributes ?: emptyMap(), "data-client-detection")
            assertEquals("true", mockRenderer.lastResponsiveLayoutModifierRendered?.attributes?.get("data-client-detection")) // Default is client-side detection

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
            val attributes = mockRenderer.lastResponsiveLayoutModifierRendered?.attributes ?: emptyMap()
            assertContains(attributes, "data-client-detection")
            assertEquals("false", attributes["data-client-detection"])
            assertContains(attributes, "data-server-size")
            assertEquals("MEDIUM", attributes["data-server-size"])

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
        // Test that MediaQuery.Breakpoints has the expected values
        assertEquals(600, MediaQuery.Breakpoints.sm)
        assertEquals(960, MediaQuery.Breakpoints.md)
        assertEquals(1280, MediaQuery.Breakpoints.lg)
        
        // Test deprecated ResponsiveBreakpoints still works for backward compatibility
        @Suppress("DEPRECATION")
        assertEquals(600, ResponsiveBreakpoints.SMALL_BREAKPOINT)
        @Suppress("DEPRECATION")
        assertEquals(960, ResponsiveBreakpoints.MEDIUM_BREAKPOINT)
        @Suppress("DEPRECATION")
        assertEquals(1280, ResponsiveBreakpoints.LARGE_BREAKPOINT)
    }
}
