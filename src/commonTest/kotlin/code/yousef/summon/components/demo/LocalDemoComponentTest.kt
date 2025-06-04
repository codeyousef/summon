package code.yousef.summon.components.demo

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo as ExpectFileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.BorderStyle
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.StylingModifiers.border
import code.yousef.summon.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import code.yousef.summon.runtime.MockPlatformRenderer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for the LocalDemoComponent, HoistedDemoComponent, and LocalDemoContainer
 */
class LocalDemoComponentTest {

    // Mock implementation of Composer for testing
    private class MockComposer : Composer {
        override val inserting: Boolean = false

        override fun startNode() {}
        override fun startGroup(key: Any?) {}
        override fun endNode() {}
        override fun endGroup() {}
        override fun changed(value: Any?): Boolean = true
        override fun updateValue(value: Any?) {}
        override fun nextSlot() {}
        override fun getSlot(): Any? = null
        override fun setSlot(value: Any?) {}
        override fun recordRead(state: Any) {}
        override fun recordWrite(state: Any) {}
        override fun reportChanged() {}
        override fun registerDisposable(disposable: () -> Unit) {}
        override fun dispose() {}
        override fun startCompose() {}
        override fun endCompose() {}
        override fun <T> compose(composable: @Composable () -> T): T {
            @Suppress("UNCHECKED_CAST")
            return null as T
        }
    }

    @Test
    fun testLocalDemoComponentWithDefaultParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the LocalDemoComponent with default parameters
            LocalDemoComponent()

            // Verify that renderBox was called
            assertTrue(mockRenderer.renderBoxCalled, "renderBox should have been called")

            // Verify that the content lambda was passed
            assertNotNull(mockRenderer.boxModifierUsed, "Box modifier should not be null")
        }
    }

    @Test
    fun testLocalDemoComponentWithCustomParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Create a custom modifier
        val customModifier = Modifier().background("red")

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the LocalDemoComponent with custom parameters
            LocalDemoComponent(
                initialValue = "Custom Text",
                modifier = customModifier
            )

            // Verify that renderBox was called with the correct parameters
            assertTrue(mockRenderer.renderBoxCalled, "renderBox should have been called")

            // Verify that the custom modifier was passed to the Box
            val boxModifier = mockRenderer.boxModifierUsed
            assertNotNull(boxModifier, "Box modifier should not be null")
            val styles = boxModifier.styles
            assertTrue(styles.containsKey("background-color"), "background style should be present")
            assertEquals("red", styles["background-color"], "background should be set to red")
        }
    }

    @Test
    fun testHoistedDemoComponent() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Create a custom modifier
        val customModifier = Modifier().padding("10px")

        // Track if callback was called
        var callbackCalled = false
        val onTextChange: (String) -> Unit = { callbackCalled = true }

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the HoistedDemoComponent
            HoistedDemoComponent(
                text = "Hoisted Value",
                onTextChange = onTextChange,
                modifier = customModifier
            )

            // Verify that renderBox was called with the correct parameters
            assertTrue(mockRenderer.renderBoxCalled, "renderBox should have been called")

            // Verify that the custom modifier was passed to the Box
            val boxModifier = mockRenderer.boxModifierUsed
            assertNotNull(boxModifier, "Box modifier should not be null")
            val styles = boxModifier.styles
            assertTrue(styles.containsKey("padding"), "padding style should be present")
            assertEquals("10px", styles["padding"], "padding should be set to 10px")
        }
    }

    @Test
    fun testLocalDemoContainer() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Create a custom modifier
        val customModifier = Modifier().border("1px", BorderStyle.Solid, "black")

        // Content called flag
        var contentCalled = false

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the LocalDemoContainer
            LocalDemoContainer(
                title = "Container Title",
                modifier = customModifier,
                content = {
                    contentCalled = true
                }
            )

            // Verify that renderColumn was called with the correct parameters
            assertTrue(mockRenderer.renderColumnCalled, "renderColumn should have been called")

            // Verify that the content lambda was passed
            assertNotNull(mockRenderer.lastColumnContentRendered, "Column content should not be null")

            // Verify that the custom modifier was passed to the Column
            val columnModifier = mockRenderer.lastColumnModifierRendered
            assertNotNull(columnModifier, "Column modifier should not be null")
            val styles = columnModifier.styles
            assertTrue(styles.containsKey("border"), "border style should be present")
            assertEquals("1px solid black", styles["border"], "border should be set correctly")
        }
    }
}
