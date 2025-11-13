package codes.yousef.summon.components.layout

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.runComposableTest
import kotlin.test.*

/**
 * Tests for the ExpansionPanel component
 */
class ExpansionPanelTest {

    @Test
    fun testExpansionPanelWithDefaultModifier() {
        val mockRenderer = MockPlatformRenderer()
        runComposableTest(mockRenderer) {
            ExpansionPanel(
                title = "Test Panel",
                content = {
                    // Empty content
                }
            )
            assertTrue(mockRenderer.renderExpansionPanelCalled, "renderExpansionPanel should have been called")
            assertEquals(Modifier(), mockRenderer.lastExpansionPanelModifierRendered, "Modifier should be the default")
            assertNotNull(mockRenderer.lastExpansionPanelContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testExpansionPanelWithCustomModifier() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier().background("red")
        runComposableTest(mockRenderer) {
            ExpansionPanel(
                title = "Test Panel",
                modifier = customModifier,
                content = {
                    // Empty content
                }
            )
            assertTrue(mockRenderer.renderExpansionPanelCalled, "renderExpansionPanel should have been called")
            assertSame(
                customModifier,
                mockRenderer.lastExpansionPanelModifierRendered,
                "Modifier should be the custom one"
            )
            assertNotNull(mockRenderer.lastExpansionPanelContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testExpansionPanelWithCustomParameters() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier().background("blue")
        runComposableTest(mockRenderer) {
            var toggleCalled = false
            val onToggle = { toggleCalled = true }

            ExpansionPanel(
                title = "Custom Panel",
                isExpanded = true,
                onToggle = onToggle,
                icon = { /* Custom icon */ },
                modifier = customModifier,
                content = {
                    // Custom content
                }
            )
            assertTrue(mockRenderer.renderExpansionPanelCalled, "renderExpansionPanel should have been called")
            assertSame(
                customModifier,
                mockRenderer.lastExpansionPanelModifierRendered,
                "Modifier should be the custom one"
            )
            assertNotNull(mockRenderer.lastExpansionPanelContentRendered, "Content should not be null")

            // Note: Currently, the ExpansionPanel implementation doesn't directly use the title, isExpanded, onToggle, or icon parameters
            // in a way that changes the arguments to renderExpansionPanel. These are typically handled internally by the component.
            // The primary check here is that renderExpansionPanel is called with the given modifier and content.
        }
    }
}