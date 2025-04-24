package code.yousef.summon.effects

import code.yousef.summon.state.mutableStateOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

class SideEffectManagementTest {

    @Test
    fun testLaunchEffect() {
        val scope = TestCompositionScope()
        var blockCalled = false

        // Call the launchEffect function
        val job = try {
            scope.launchEffect { 
                blockCalled = true 
            }
        } catch (e: Exception) {
            fail("Failed to invoke launchEffect: ${e.message}")
            null
        }

        // Verify compose was called and job was returned
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
        assertNotNull(job, "launchEffect should return a job")

        // Test job methods
        job?.cancel()
    }

    @Test
    fun testLaunchEffectWithDeps() {
        val scope = TestCompositionScope()
        var blockCalled = false
        val dep1 = "test"
        val dep2 = 123

        // Call the launchEffectWithDeps function
        val job = try {
            scope.launchEffectWithDeps(dep1, dep2) { 
                blockCalled = true 
            }
        } catch (e: Exception) {
            fail("Failed to invoke launchEffectWithDeps: ${e.message}")
            null
        }

        // Verify compose was called and job was returned
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
        assertNotNull(job, "launchEffectWithDeps should return a job")

        // Test job methods
        job?.cancel()
    }

    @Test
    fun testAsyncEffect() {
        val scope = TestCompositionScope()
        var effectCalled = false
        var cleanupCalled = false

        // Call the asyncEffect function
        try {
            scope.asyncEffect { 
                effectCalled = true
                { cleanupCalled = true }
            }
        } catch (e: Exception) {
            fail("Failed to invoke asyncEffect: ${e.message}")
        }

        // Verify compose was called
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
    }

    @Test
    fun testAsyncEffectWithDeps() {
        val scope = TestCompositionScope()
        var effectCalled = false
        var cleanupCalled = false
        val dep1 = "test"
        val dep2 = 123

        // Call the asyncEffectWithDeps function
        try {
            scope.asyncEffectWithDeps(dep1, dep2) { 
                effectCalled = true
                { cleanupCalled = true }
            }
        } catch (e: Exception) {
            fail("Failed to invoke asyncEffectWithDeps: ${e.message}")
        }

        // Verify compose was called
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
    }

    @Test
    fun testUpdateStateAsync() {
        val scope = TestCompositionScope()
        val state = mutableStateOf("initial")

        // Call the updateStateAsync function
        val job = try {
            scope.updateStateAsync(state) { 
                "updated" 
            }
        } catch (e: Exception) {
            fail("Failed to invoke updateStateAsync: ${e.message}")
            null
        }

        // Verify compose was called and job was returned
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
        assertNotNull(job, "updateStateAsync should return a job")

        // Test job methods
        job?.cancel()

        // Note: state won't be updated because we're not executing the composable block
        assertEquals("initial", state.value, "State should not be updated in test")
    }
}
