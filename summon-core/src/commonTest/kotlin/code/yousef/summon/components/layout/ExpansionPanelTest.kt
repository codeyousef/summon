package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.MockPlatformRenderer // Explicit import for shared mock
import code.yousef.summon.util.runTestComposable
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertSame
import kotlin.test.assertNotNull

/**
 * Tests for the ExpansionPanel component
 */
class ExpansionPanelTest {

    @Test
    fun testExpansionPanelWithDefaultModifier() {
        val mockRenderer = MockPlatformRenderer()
        runTestComposable(mockRenderer) {
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
        runTestComposable(mockRenderer) {
            ExpansionPanel(
                title = "Test Panel",
                modifier = customModifier,
                content = {
                    // Empty content
                }
            )
            assertTrue(mockRenderer.renderExpansionPanelCalled, "renderExpansionPanel should have been called")
            assertSame(customModifier, mockRenderer.lastExpansionPanelModifierRendered, "Modifier should be the custom one")
            assertNotNull(mockRenderer.lastExpansionPanelContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testExpansionPanelWithCustomParameters() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier().background("blue")
        runTestComposable(mockRenderer) {
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
            assertSame(customModifier, mockRenderer.lastExpansionPanelModifierRendered, "Modifier should be the custom one")
            assertNotNull(mockRenderer.lastExpansionPanelContentRendered, "Content should not be null")

            // Note: Currently, the ExpansionPanel implementation doesn't directly use the title, isExpanded, onToggle, or icon parameters
            // in a way that changes the arguments to renderExpansionPanel. These are typically handled internally by the component.
            // The primary check here is that renderExpansionPanel is called with the given modifier and content.
        }
    }
}