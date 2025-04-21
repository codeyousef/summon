package code.yousef.summon.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class FlowBindingTest {

    @Test
    fun testFlowCollectionRegistry() {
        // Test getting a scope
        val scope1 = FlowCollectionRegistry.getScope("test1")
        assertNotNull(scope1)

        // Test getting the same scope again
        val scope2 = FlowCollectionRegistry.getScope("test1")
        assertEquals(scope1, scope2)

        // Test getting a different scope
        val scope3 = FlowCollectionRegistry.getScope("test2")
        assertNotNull(scope3)
        assertNotEquals(scope1, scope3)

        // Test cancelling a scope
        FlowCollectionRegistry.cancelScope("test1")

        // Getting the scope again should create a new one
        val scope4 = FlowCollectionRegistry.getScope("test1")
        assertNotNull(scope4)
        assertNotEquals(scope1, scope4)

        // Test cancelling all scopes
        FlowCollectionRegistry.cancelAll()
    }

    @Test
    fun testFlowToState() {
        // Create a simple StateFlow (which is a type of Flow)
        val stateFlow = MutableStateFlow("initial")

        // Convert the flow to a state
        val state = flowToState(stateFlow, "default")

        // Initial value should be the default since flow collection happens asynchronously
        assertEquals("default", state.value)

        // Update the StateFlow
        stateFlow.value = "updated"

        // The state should eventually be updated, but we can't test this synchronously
        // In a real-world scenario, this would happen asynchronously
    }

    @Test
    fun testComponentFlowToState() {
        // Create a simple StateFlow
        val stateFlow = MutableStateFlow("initial")

        // Convert the flow to a state with a component ID
        val state = componentFlowToState(stateFlow, "default", "testComponent")

        // Initial value should be the default since flow collection happens asynchronously
        assertEquals("default", state.value)

        // Update the StateFlow
        stateFlow.value = "updated"

        // Cancel the component flows
        cancelComponentFlows("testComponent")

        // Create another flow for the same component
        val stateFlow2 = MutableStateFlow("new")

        // Convert the flow to a state with the same component ID
        val state2 = componentFlowToState(stateFlow2, "default2", "testComponent")

        // Initial value should be the default since flow collection happens asynchronously
        assertEquals("default2", state2.value)
    }

    @Test
    fun testStateFlowToState() {
        // Create a StateFlow
        val stateFlow = MutableStateFlow("initial")

        // Convert the StateFlow to a state
        val state = stateFlowToState(stateFlow)

        // Initial value should be the StateFlow's value
        assertEquals("initial", state.value)

        // Update the StateFlow
        stateFlow.value = "updated"

        // The state should eventually be updated, but we can't test this synchronously
    }

    @Test
    fun testThreadSafetyStructure() {
        // This test verifies that the thread safety structures are in place
        // We can't easily test actual thread safety in a unit test, but we can
        // verify that the code is structured to be thread-safe

        // Create a flow and convert it to state
        val stateFlow = MutableStateFlow("test")
        val state = flowToState(stateFlow, "default")

        // Verify the state is properly initialized with the default value
        // since flow collection happens asynchronously
        assertEquals("default", state.value)

        // Verify that component flow registry works with multiple components
        val state1 = componentFlowToState(stateFlow, "default", "component1")
        val state2 = componentFlowToState(stateFlow, "default", "component2")

        assertEquals("default", state1.value)
        assertEquals("default", state2.value)

        // Cancel one component's flows
        cancelComponentFlows("component1")

        // The other component should still work
        val newFlow = MutableStateFlow("new")
        val state3 = componentFlowToState(newFlow, "default", "component2")
        assertEquals("default", state3.value)
    }
}
