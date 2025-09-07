package code.yousef.summon.effects

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class TimeTest {

    @Test
    fun testCurrentTimeMillis() {
        // Test that currentTimeMillis returns a non-negative value
        val time = currentTimeMillis()
        assertTrue(time >= 0, "Current time should be non-negative")

        // Test that currentTimeMillis is monotonically increasing
        val time2 = currentTimeMillis()
        assertTrue(time2 >= time, "Current time should be monotonically increasing")
    }

    @Test
    fun testSetTimeoutAndClearTimeoutBasic() {
        var callbackCalled = false

        // Set a timeout
        val timeoutId = setTimeout(100) {
            callbackCalled = true
        }

        // Verify timeout ID is non-negative
        assertTrue(timeoutId >= 0, "Timeout ID should be non-negative")

        // Clear the timeout immediately
        clearTimeout(timeoutId)

        // Test that clearing an invalid timeout ID doesn't throw an exception
        try {
            clearTimeout(-1)
            // If we get here, no exception was thrown
            assertTrue(true, "Clearing invalid timeout ID should not throw")
        } catch (e: Exception) {
            fail("Clearing invalid timeout ID should not throw: ${e.message}")
        }
    }
}
