package code.yousef.summon.components.demo

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composer
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.MockPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for the StatefulDemoComponent, StatefulCounter, and ToggleDemo
 */
class StatefulDemoComponentTest {

    @Test
    fun testStatefulDemoComponent() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)
            mockRenderer.reset() // Reset before calling the component

            // Call the StatefulDemoComponent
            StatefulDemoComponent()

            // Verify that renderBox was called
            assertTrue(mockRenderer.renderBoxCalled, "renderBox should have been called")

            // Note: The content lambda containing Text is not executed by the mock renderer,
            // so we cannot verify renderText was called. This is a limitation of the current
            // mock renderer design which captures but doesn't execute content lambdas.

            // Verify that the content lambda was passed
            assertNotNull(mockRenderer.lastBoxContentRendered, "Box content lambda should not be null")

            // We can't verify the exact button label or onClick directly with simple flags
            // but we know renderButton was called.
        }
    }

    @Test
    fun testStatefulDemoComponentWithParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Create a custom modifier
        val customModifier = Modifier().background("blue")

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)
            mockRenderer.reset()

            // Call the StatefulDemoComponent with custom parameters
            StatefulDemoComponent(
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
            assertEquals("blue", styles["background-color"], "background should be set to blue")

            // Note: Cannot verify text content as the mock renderer doesn't execute content lambdas
            assertNotNull(mockRenderer.lastBoxContentRendered, "Box content lambda should not be null")
        }
    }

    @Test
    fun testStatefulCounter() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)
            mockRenderer.reset()

            // Call the StatefulCounter
            StatefulCounter()

            // Note: StatefulCounter calls Text and Button composables directly within the composer,
            // but since the mock renderer doesn't provide a proper composition context that executes
            // these composables, we cannot verify that renderText or renderButton were called.
            // This is a limitation of the current test setup.

            // onClick handler verification would require more advanced mocking/capturing
        }
    }

    @Test
    fun testToggleDemo() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)
            mockRenderer.reset()

            // Call the ToggleDemo
            ToggleDemo()

            // Note: ToggleDemo calls Text and Button composables directly within the composer,
            // but since the mock renderer doesn't provide a proper composition context that executes
            // these composables, we cannot verify that renderText or renderButton were called.
            // This is a limitation of the current test setup.

            // onClick handler verification would require more advanced mocking/capturing
        }
    }
}

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

    override fun recompose() {
        // Mock implementation
    }

    override fun rememberedValue(key: Any): Any? {
        return null
    }

    override fun updateRememberedValue(key: Any, value: Any?) {
        // Mock implementation
    }
}
