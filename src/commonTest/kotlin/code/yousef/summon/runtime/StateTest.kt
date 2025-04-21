package code.yousef.summon.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import code.yousef.summon.state.State
import code.yousef.summon.state.SummonMutableState

class StateTest {

    @Test
    fun testRuntimeMutableStateTypeAlias() {
        // Test that RuntimeMutableState is a type alias for SummonMutableState
        val state: RuntimeMutableState<Int> = mutableStateOf(42)
        
        // Verify that it implements SummonMutableState
        val summonState: SummonMutableState<Int> = state
        
        // Verify that the value is correctly set
        assertEquals(42, state.value, "Initial value should be set correctly")
        assertEquals(42, summonState.value, "Should be the same instance")
    }
    
    @Test
    fun testMutableStateOf() {
        // Test that mutableStateOf creates a RuntimeMutableState with the correct initial value
        val state = mutableStateOf("test")
        
        // Verify that the value is correctly set
        assertEquals("test", state.value, "Initial value should be set correctly")
        
        // Verify that the value can be updated
        state.value = "updated"
        assertEquals("updated", state.value, "Value should be updated")
    }
    
    @Test
    fun testStateInvokeOperator() {
        // Test the invoke operator extension for State
        val state: State<String> = mutableStateOf("test")
        
        // Verify that state() returns the value
        assertEquals("test", state(), "Invoke operator should return the value")
    }
    
    @Test
    fun testRuntimeMutableStateInvokeOperator() {
        // Test the invoke operator extension for RuntimeMutableState
        val state = mutableStateOf(42)
        
        // Verify that state() returns the value
        assertEquals(42, state(), "Invoke operator should return the value")
        
        // Verify that state(newValue) updates the value
        state(99)
        assertEquals(99, state.value, "Invoke operator with argument should update the value")
    }
    
    @Test
    fun testStateInteroperability() {
        // Test that RuntimeMutableState works with State functions
        val mutableState = mutableStateOf("test")
        val state: State<String> = mutableState
        
        // Verify that state() works on both
        assertEquals("test", state(), "Invoke operator should work on State")
        assertEquals("test", mutableState(), "Invoke operator should work on RuntimeMutableState")
        
        // Update the mutable state
        mutableState("updated")
        
        // Verify that the change is reflected in both
        assertEquals("updated", state(), "Change should be reflected in State")
        assertEquals("updated", mutableState(), "Change should be reflected in RuntimeMutableState")
    }
}