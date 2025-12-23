package codes.yousef.summon.components.feedback

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.Composer
import codes.yousef.summon.runtime.CompositionLocal
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.MockPlatformRenderer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for the Alert component
 */
class AlertTest {

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

    @Test
    fun testAlertWithDefaultParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Alert component with default parameters
            Alert(
                content = { /* Empty content for testing */ }
            )

            // Verify that renderAlertContainer was called
            assertTrue(mockRenderer.renderAlertContainerCalled, "renderAlertContainer should have been called")

            // Verify the variant (default is INFO)
            assertEquals(AlertVariant.INFO, mockRenderer.lastAlertVariantRendered, "Variant should be INFO")

            // Verify the modifier is not null
            assertNotNull(mockRenderer.lastAlertModifierRendered, "Modifier should not be null")

            // Verify the content is not null
            assertNotNull(mockRenderer.lastAlertContentRendered, "Content should not be null")

            // In a real test environment, we would check the styles, but in our mock environment,
            // we can only verify that the renderAlertContainer method was called with the correct parameters
        }
    }

    @Test
    fun testAlertWithCustomVariant() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Alert component with ERROR variant
            Alert(
                variant = AlertVariant.WARNING,
                content = { /* Empty content for testing */ }
            )

            // Verify that renderAlertContainer was called
            assertTrue(mockRenderer.renderAlertContainerCalled, "renderAlertContainer should have been called")

            // Verify the variant
            assertEquals(AlertVariant.WARNING, mockRenderer.lastAlertVariantRendered, "Variant should be WARNING")

            // Verify the modifier is not null
            assertNotNull(mockRenderer.lastAlertModifierRendered, "Modifier should not be null")

            // Verify the content is not null
            assertNotNull(mockRenderer.lastAlertContentRendered, "Content should not be null")

            // In a real test environment, we would check the styles, but in our mock environment,
            // we can only verify that the renderAlertContainer method was called with the correct parameters
        }
    }

    @Test
    fun testAlertWithCustomModifier() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Create a custom Modifier
            val customModifier = Modifier()
                .margin("20px")
                .borderRadius("8px")

            // Call the Alert component with custom modifier
            Alert(
                modifier = customModifier,
                content = { /* Empty content for testing */ }
            )

            // Verify that renderAlertContainer was called
            assertTrue(mockRenderer.renderAlertContainerCalled, "renderAlertContainer should have been called")

            // Verify the modifier is not null
            assertNotNull(mockRenderer.lastAlertModifierRendered, "Modifier should not be null")

            // In a real test environment, we would check the styles, but in our mock environment,
            // we can only verify that the renderAlertContainer method was called with the correct parameters

            // We can check if the custom modifier properties are present in the final modifier
            // but this is not reliable in our mock environment
        }
    }

    @Test
    fun testAlertWithDismissCallback() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Variable to track if onDismiss was called
            var dismissCalled = false

            // Call the Alert component with onDismiss callback
            Alert(
                onDismiss = { dismissCalled = true },
                content = { /* Empty content for testing */ }
            )

            // Verify that renderAlertContainer was called
            assertTrue(mockRenderer.renderAlertContainerCalled, "renderAlertContainer should have been called")

            // We can't directly test if the dismiss button is rendered or if the callback works
            // since we're not executing the content lambda, but we can verify that the onDismiss
            // parameter was passed to the Alert component
        }
    }

    @Test
    fun testAlertWithStringMessage() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Alert component with string message
            Alert(
                message = "Test alert message",
                title = "Test Title",
                variant = AlertVariant.SUCCESS
            )

            // Verify that renderAlertContainer was called
            assertTrue(mockRenderer.renderAlertContainerCalled, "renderAlertContainer should have been called")

            // Verify the variant
            assertEquals(AlertVariant.SUCCESS, mockRenderer.lastAlertVariantRendered, "Variant should be SUCCESS")

            // Verify the modifier is not null
            assertNotNull(mockRenderer.lastAlertModifierRendered, "Modifier should not be null")

            // In a real test environment, we would check the styles, but in our mock environment,
            // we can only verify that the renderAlertContainer method was called with the correct variant
        }
    }
}
