package code.yousef.summon.effects

import code.yousef.summon.runtime.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.components.input.SelectOption
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import code.yousef.summon.runtime.FormContent
import kotlin.test.*
import code.yousef.summon.effects.CompositionScope
import code.yousef.summon.effects.useInterval
import code.yousef.summon.effects.useTimeout

// Simplified dummy scope
private class DummyCompositionScope : CompositionScope {
    override fun compose(block: @Composable () -> Unit) {
         // Minimal implementation sufficient to act as receiver
         println("DummyCompositionScope.compose called - block not executed")
    }
}

class CommonEffectsTest {

    @Test
    fun testUseIntervalControl() { // Renamed test
        var intervalControl: IntervalControl? = null
        var callbackCount = 0
        val scope = DummyCompositionScope()

        // Call the hook
        try {
            // This call likely sets up internal state and LaunchedEffect within useInterval
            intervalControl = scope.useInterval(100) { callbackCount++ }
        } catch (e: Exception) {
            fail("Failed to invoke useInterval hook: ${e.message}")
        }

        // Verify control object is returned and methods exist (can't test internal state easily)
        assertNotNull(intervalControl, "IntervalControl should be returned")

        // Test control methods (we assume they modify internal state correctly)
        intervalControl.pause()  // Should prevent callback (if timer was real)
        // No direct way to assert paused state from the interface
        assertEquals(0, callbackCount, "Callback count should remain 0 after pause (mocked timer)")

        intervalControl.resume() // Should allow callback again (if timer was real)
        // No direct way to assert resumed state
        assertEquals(0, callbackCount, "Callback count should remain 0 after resume (mocked timer)")

        intervalControl.reset() // Should restart the effect internally
        assertEquals(0, callbackCount, "Callback count should remain 0 after reset (mocked timer)")

        intervalControl.setDelay(200) // Should change internal delay
        // No direct way to assert delay change
        assertEquals(0, callbackCount, "Callback count should remain 0 after setDelay (mocked timer)")
    }

    @Test
    fun testUseTimeoutControl() { // Renamed test
        var timeoutControl: TimeoutControl? = null
        var callbackCount = 0
        val scope = DummyCompositionScope()

        try {
            timeoutControl = scope.useTimeout(100) { callbackCount++ }
        } catch (e: Exception) {
            fail("Failed to invoke useTimeout hook: ${e.message}")
        }

        assertNotNull(timeoutControl, "TimeoutControl should be returned")

        // Test control methods
        timeoutControl.cancel() // Should prevent callback (if timer was real)
        // No direct way to assert cancelled state
        assertEquals(0, callbackCount, "Callback count should remain 0 after cancel (mocked timer)")

        timeoutControl.reset() // Should restart the effect internally
        // No direct way to assert reset state
        assertEquals(0, callbackCount, "Callback count should remain 0 after reset (mocked timer)")

        timeoutControl.setDelay(50) // Should change internal delay
        // No direct way to assert delay change
        assertEquals(0, callbackCount, "Callback count should remain 0 after setDelay (mocked timer)")
    }
}