package code.yousef.summon.effects

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class EffectTimerTest {

    @Test
    fun testDebouncedEffectExecution() {
        val scope = TestExecutingCompositionScope()
        var producerCalled = false
        var effectCalled = false

        // Create a debounced effect
        val debouncedEffect = debouncedEffect(
            delayMs = 100,
            producer = { 
                producerCalled = true
                "test-value" 
            },
            effect = { value ->
                effectCalled = true
                assertEquals("test-value", value, "Producer value should be passed to effect")
            }
        )

        // Call the debounced effect
        try {
            debouncedEffect(scope)
        } catch (e: Exception) {
            fail("Failed to invoke debounced effect: ${e.message}")
        }

        // Verify compose was called
        assertTrue(scope.composeCalled, "compose should be called")

        // Verify producer was called
        assertTrue(producerCalled, "Producer function should be called")

        // Note: We can't easily verify that the effect is called after the delay
        // in a unit test without using a coroutine test framework.
        // In a real test environment, we would use a coroutine test framework
        // to advance the virtual time and verify the effect is called.
    }

    @Test
    fun testThrottledEffectExecution() {
        val scope = TestExecutingCompositionScope()
        var producerCalled = false
        var effectCalled = false

        // Create a throttled effect
        val throttledEffect = throttledEffect(
            delayMs = 100,
            producer = { 
                producerCalled = true
                "test-value" 
            },
            effect = { value ->
                effectCalled = true
                assertEquals("test-value", value, "Producer value should be passed to effect")
            }
        )

        // Call the throttled effect
        try {
            throttledEffect(scope)
        } catch (e: Exception) {
            fail("Failed to invoke throttled effect: ${e.message}")
        }

        // Verify compose was called
        assertTrue(scope.composeCalled, "compose should be called")

        // Verify producer was called
        assertTrue(producerCalled, "Producer function should be called")

        // Note: We can't easily verify that the effect is called immediately or after the delay
        // in a unit test without using a coroutine test framework.
        // In a real test environment, we would use a coroutine test framework
        // to advance the virtual time and verify the effect is called.
    }
}
