package code.yousef.summon.components.feedback

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

/**
 * Tests for the Snackbar component and related classes
 */
class SnackbarTest {

    // Mock implementation of Composer for testing
    private class MockComposer : Composer {
        override val inserting: Boolean = true

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
    fun testSnackbarVariantColors() {
        // Test getColor method
        assertEquals("#6b7280", SnackbarVariant.DEFAULT.getColor())
        assertEquals("#0284c7", SnackbarVariant.INFO.getColor())
        assertEquals("#16a34a", SnackbarVariant.SUCCESS.getColor())
        assertEquals("#d97706", SnackbarVariant.WARNING.getColor())
        assertEquals("#dc2626", SnackbarVariant.ERROR.getColor())

        // Test getBackgroundColor method
        assertEquals("#f3f4f6", SnackbarVariant.DEFAULT.getBackgroundColor())
        assertEquals("#e0f2fe", SnackbarVariant.INFO.getBackgroundColor())
        assertEquals("#dcfce7", SnackbarVariant.SUCCESS.getBackgroundColor())
        assertEquals("#fef3c7", SnackbarVariant.WARNING.getBackgroundColor())
        assertEquals("#fee2e2", SnackbarVariant.ERROR.getBackgroundColor())
    }

    @Test
    fun testSnackbarWithDefaultParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the Snackbar composable
            Snackbar(message = "Test message")

            // Verify that renderBox was called
            assertNotNull(mockRenderer.lastBoxModifierRendered, "renderBox should have been called")

            // Verify that the modifier contains the expected styles for default Snackbar
            val styles = mockRenderer.lastBoxModifierRendered?.styles ?: emptyMap()
            assertEquals("#f3f4f6", styles["background-color"], "Background color should be default light gray")
            assertEquals("#6b7280", styles["color"], "Text color should be default gray")
        }
    }

    @Test
    fun testSnackbarWithAction() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            var actionClicked = false
            Snackbar(message = "Test message", action = "Undo", onAction = { actionClicked = true })

            // Verify that renderBox was called
            assertNotNull(mockRenderer.lastBoxModifierRendered, "renderBox should have been called")
            
            // Note: renderButton won't be called because the mock renderer doesn't execute content lambdas
            // The button is rendered inside the Box's content lambda
        }
    }

    @Test
    fun testSnackbarWithCustomVariant() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the Snackbar composable with a custom variant
            Snackbar(
                message = "Success message",
                variant = SnackbarVariant.SUCCESS
            )

            // Verify that renderBox was called
            assertNotNull(mockRenderer.lastBoxModifierRendered, "renderBox should have been called")

            // Verify that the modifier contains the expected styles for SUCCESS variant
            val styles = mockRenderer.lastBoxModifierRendered?.styles ?: emptyMap()
            assertEquals("#dcfce7", styles["background-color"], "Background color should match SUCCESS variant")
            assertEquals("#16a34a", styles["color"], "Text color should match SUCCESS variant")
        }
    }

    @Test
    fun testSnackbarWithCustomPosition() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the Snackbar composable with custom positioning
            Snackbar(
                message = "Top-right message",
                horizontalPosition = SnackbarHorizontalPosition.END,
                verticalPosition = SnackbarVerticalPosition.TOP
            )

            // Verify that renderBox was called
            assertNotNull(mockRenderer.lastBoxModifierRendered, "renderBox should have been called")

            // Verify that the modifier contains the expected positioning styles
            val styles = mockRenderer.lastBoxModifierRendered?.styles ?: emptyMap()
            assertEquals("16px", styles["right"], "Right should be 16px")
            assertEquals("16px", styles["top"], "Top should be 16px")
        }
    }

    @Test
    fun testSnackbarWithCustomDuration() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the Snackbar composable with a custom duration
            Snackbar(
                message = "Quick message",
                duration = 1000.milliseconds
            )

            // Verify that renderBox was called
            assertNotNull(mockRenderer.lastBoxModifierRendered, "renderBox should have been called")

            // We can't directly test the auto-dismissal behavior in this unit test environment,
        }
    }

    @Test
    fun testSnackbarWithCustomModifier() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Create a custom modifier
        val customModifier = Modifier().width("300px").height("50px")

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the Snackbar composable with a custom modifier
            Snackbar(
                message = "Custom styled message",
                modifier = customModifier
            )

            // Verify that renderBox was called
            assertNotNull(mockRenderer.lastBoxModifierRendered, "renderBox should have been called")

            // Verify that the custom modifier styles were preserved
            val styles = mockRenderer.lastBoxModifierRendered?.styles ?: emptyMap()
            assertEquals("300px", styles["width"], "Width should be 300px")
            assertEquals("50px", styles["height"], "Height should be 50px")
        }
    }
}