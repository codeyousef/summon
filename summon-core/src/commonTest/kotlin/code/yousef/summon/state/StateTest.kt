package codes.yousef.summon.state

import kotlin.test.Test
import kotlin.test.assertEquals

class StateTest {
    @Test
    fun testPropertyDelegation() {
        // Test read-only property delegation
        val readOnlyState = mutableStateOf("test")
        val readOnlyProperty by readOnlyState
        assertEquals("test", readOnlyProperty)

        // Test mutable property delegation
        var mutableProperty by mutableStateOf("initial")
        assertEquals("initial", mutableProperty)

        // Test updating the property
        mutableProperty = "updated"
        assertEquals("updated", mutableProperty)
        assertEquals("updated", mutableProperty)
    }

    @Test
    fun testRememberWithPropertyDelegation() {
        // This is a simplified test that doesn't actually use the @Composable remember function
        // since we can't use @Composable functions in a regular test
        // Instead, we're simulating what remember would do - return a mutableStateOf

        // Simulate remember { mutableStateOf(true) }
        val rememberedState = simulateRemember { mutableStateOf(true) }

        // Test property delegation with the remembered state
        var isDarkTheme by rememberedState
        assertEquals(true, isDarkTheme)

        // Test updating the property
        isDarkTheme = false
        assertEquals(false, isDarkTheme)
    }

    // Helper function to simulate the remember function for testing
    private fun <T> simulateRemember(calculation: () -> T): T {
        return calculation()
    }
}
