package code.yousef.summon.components.feedback

import code.yousef.summon.runtime.*
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.feedback.ProgressBar // Import the component
import code.yousef.summon.components.feedback.ProgressType // Import ProgressType
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.util.runTestComposable
import code.yousef.summon.util.TestFileInfo // Import for consistency in mock renderer boilerplate

// Import other types used in PlatformRenderer methods for the mock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.LocalDate
import kotlinx.html.FlowContent
import code.yousef.summon.runtime.SelectOption as RendererSelectOption
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.components.input.FileInfo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertNull
import kotlin.test.assertSame

// Import the shared MockPlatformRenderer
import code.yousef.summon.runtime.MockPlatformRenderer

class ProgressBarTest {

    @Test
    fun testProgressBarWithSpecificValue() {
        val mockRenderer = MockPlatformRenderer() // Changed to shared MockPlatformRenderer
        val testProgress = 0.75f
        val testModifier = Modifier().margin("5px") // Example custom modifier

        runTestComposable(mockRenderer) {
            ProgressBar(
                progress = testProgress,
                modifier = testModifier
            )
        }

        assertTrue(mockRenderer.renderProgressCalled, "renderProgress should be called")
        assertEquals(testProgress, mockRenderer.lastProgressValueRendered, "Progress value mismatch") // Updated property
        assertEquals(ProgressType.LINEAR, mockRenderer.lastProgressTypeRendered, "Progress type should be LINEAR") // Updated property
        assertNotNull(mockRenderer.lastProgressModifierRendered, "Modifier should not be null") // Updated property

        // Verify default styles are applied ON TOP of the passed modifier
        val capturedModifier = mockRenderer.lastProgressModifierRendered!! // Updated property
        // Check custom style from testModifier
        assertEquals("5px", capturedModifier.styles["margin"], "Custom modifier style (margin) not found")
        // Check default styles applied by ProgressBar
        assertEquals("8px", capturedModifier.styles["height"], "Default height style not applied/overridden")
        assertEquals("#f0f0f0", capturedModifier.styles["background-color"], "Default background style not applied/overridden")
        assertEquals("4px", capturedModifier.styles["border-radius"], "Default border-radius style not applied/overridden")
    }

    @Test
    fun testProgressBarIndeterminate() {
        val mockRenderer = MockPlatformRenderer() // Changed to shared MockPlatformRenderer

        runTestComposable(mockRenderer) {
            ProgressBar(
                progress = null // Indeterminate state
            )
        }

        assertTrue(mockRenderer.renderProgressCalled, "renderProgress should be called for indeterminate")
        assertNull(mockRenderer.lastProgressValueRendered, "Progress value should be null for indeterminate") // Updated property
        assertEquals(ProgressType.LINEAR, mockRenderer.lastProgressTypeRendered, "Progress type should be LINEAR for indeterminate") // Updated property
        assertNotNull(mockRenderer.lastProgressModifierRendered, "Modifier should not be null for indeterminate") // Updated property

        // Verify default styles are applied (no custom modifier passed)
        val capturedModifier = mockRenderer.lastProgressModifierRendered!! // Updated property
        assertEquals("8px", capturedModifier.styles["height"], "Default height style not applied")
        assertEquals("#f0f0f0", capturedModifier.styles["background-color"], "Default background style not applied")
        assertEquals("4px", capturedModifier.styles["border-radius"], "Default border-radius style not applied")
    }
} 