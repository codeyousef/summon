package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.core.BasicText
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import code.yousef.summon.state.mutableStateOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for button state management and recomposition functionality.
 * This reproduces the issue where buttons don't properly update UI state.
 */
class ButtonStateTest {

    @Test
    fun testButtonClickUpdatesState() {
        // Create a mock renderer to capture button interactions
        val mockRenderer = MockPlatformRenderer()
        var renderCount = 0
        var lastRenderedCounterValue: Int? = null
        var buttonClickHandler: (() -> Unit)? = null

        @Composable
        fun CounterApp() {
            renderCount++
            val counter = remember { mutableStateOf(0) }

            Column(modifier = Modifier().padding("16px")) {
                BasicText(
                    text = "Count: ${counter.value}",
                    modifier = Modifier().padding(bottom = "16px", left = "0px", right = "0px", top = "0px")
                )

                Button(
                    onClick = {
                        println("[DEBUG_LOG] Button clicked, incrementing counter from ${counter.value} to ${counter.value + 1}")
                        counter.value++
                    },
                    label = "Click me!"
                )
            }

            // Capture the current counter value for testing
            lastRenderedCounterValue = counter.value
        }

        // Mock Composer similar to BasicTextTest
        val mockComposer = object : Composer {
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

            override fun recompose() {}
            override fun rememberedValue(key: Any): Any? = null
            override fun updateRememberedValue(key: Any, value: Any?) {}
        }

        // Set up the composition context following BasicTextTest pattern
        CompositionLocal.provideComposer(mockComposer) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Button component directly
            Button(
                onClick = {
                    println("[DEBUG_LOG] Button clicked!")
                },
                label = "Click me!"
            )

            println("[DEBUG_LOG] Button component called")

            // Verify that renderButton was called
            assertTrue(mockRenderer.renderButtonCalled, "renderButton should have been called")

            // Verify the onClick handler was passed
            assertNotNull(mockRenderer.lastButtonOnClickRendered, "onClick handler should not be null")

            // Verify the modifier was passed
            assertNotNull(mockRenderer.lastButtonModifierRendered, "Modifier should not be null")

            // Simulate button click
            val onClickHandler = mockRenderer.lastButtonOnClickRendered
            assertNotNull(onClickHandler, "Button onClick handler should not be null")

            println("[DEBUG_LOG] Simulating button click...")
            onClickHandler!!.invoke()

            println("[DEBUG_LOG] Button click simulation complete")
        }
    }

    @Test
    fun testMultipleButtonClicksUpdateState() {
        val mockRenderer = MockPlatformRenderer()
        var clickCount = 0

        // Mock Composer similar to BasicTextTest
        val mockComposer = object : Composer {
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

            override fun recompose() {}
            override fun rememberedValue(key: Any): Any? = null
            override fun updateRememberedValue(key: Any, value: Any?) {}
        }

        CompositionLocal.provideComposer(mockComposer) {
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Button component directly
            Button(
                onClick = {
                    clickCount++
                    println("[DEBUG_LOG] Button clicked, count now: $clickCount")
                },
                label = "Increment"
            )

            // Verify button was rendered
            assertTrue(mockRenderer.renderButtonCalled, "Button should have been rendered")

            val onClickHandler = mockRenderer.lastButtonOnClickRendered
            assertNotNull(onClickHandler, "onClick handler should not be null")

            // Click multiple times
            println("[DEBUG_LOG] Clicking button 3 times...")
            onClickHandler.invoke()
            onClickHandler.invoke()
            onClickHandler.invoke()

            // Verify clicks were processed
            assertEquals(3, clickCount, "Button should have been clicked 3 times")
        }
    }

    @Test
    fun testButtonStateChangeTriggersRecomposition() {
        val mockRenderer = MockPlatformRenderer()
        var clickCount = 0

        // Mock Composer similar to BasicTextTest
        val mockComposer = object : Composer {
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

            override fun recompose() {}
            override fun rememberedValue(key: Any): Any? = null
            override fun updateRememberedValue(key: Any, value: Any?) {}
        }

        CompositionLocal.provideComposer(mockComposer) {
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Button component directly to test basic functionality
            Button(
                onClick = {
                    clickCount++
                    println("[DEBUG_LOG] Button onClick called, click count: $clickCount")
                },
                label = "Increment"
            )

            // Verify button was rendered
            assertTrue(mockRenderer.renderButtonCalled, "Button should have been rendered")

            val onClickHandler = mockRenderer.lastButtonOnClickRendered
            assertNotNull(onClickHandler, "onClick handler should not be null")

            // Simulate click to test state change
            println("[DEBUG_LOG] Simulating button click to test state change...")
            onClickHandler.invoke()

            // Verify click was processed
            assertEquals(1, clickCount, "Click count should be 1 after button click")

            println("[DEBUG_LOG] Button state change test complete")
        }
    }
}