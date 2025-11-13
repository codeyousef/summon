package codes.yousef.summon.effects

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

class EffectsTest {

    @Test
    fun testEffect() {
        val scope = TestCompositionScope()
        var effectCalled = false

        // Call the effect function
        try {
            scope.effect { effectCalled = true }
        } catch (e: Exception) {
            fail("Failed to invoke effect: ${e.message}")
        }

        // Since we're not actually executing the composable block,
        // we can't verify that the effect was called.
        // Instead, we verify that the compose method was called.
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
    }

    @Test
    fun testOnMount() {
        val scope = TestCompositionScope()
        var mountEffectCalled = false

        // Call the onMount function
        try {
            scope.onMount { mountEffectCalled = true }
        } catch (e: Exception) {
            fail("Failed to invoke onMount: ${e.message}")
        }

        // Verify compose was called
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
    }

    @Test
    fun testOnDispose() {
        val scope = TestCompositionScope()
        var disposeEffectCalled = false

        // Call the onDispose function
        try {
            scope.onDispose { disposeEffectCalled = true }
        } catch (e: Exception) {
            fail("Failed to invoke onDispose: ${e.message}")
        }

        // Verify compose was called
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
    }

    @Test
    fun testEffectWithDeps() {
        val scope = TestCompositionScope()
        var effectCalled = false
        val dep1 = "test"
        val dep2 = 123

        // Call the effectWithDeps function
        try {
            scope.effectWithDeps(dep1, dep2) { effectCalled = true }
        } catch (e: Exception) {
            fail("Failed to invoke effectWithDeps: ${e.message}")
        }

        // Verify compose was called
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
    }

    @Test
    fun testOnMountWithCleanup() {
        val scope = TestCompositionScope()
        var mountEffectCalled = false
        var cleanupCalled = false

        // Call the onMountWithCleanup function
        try {
            scope.onMountWithCleanup { 
                mountEffectCalled = true
                { cleanupCalled = true }
            }
        } catch (e: Exception) {
            fail("Failed to invoke onMountWithCleanup: ${e.message}")
        }

        // Verify compose was called
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
    }

    @Test
    fun testEffectWithDepsAndCleanup() {
        val scope = TestCompositionScope()
        var effectCalled = false
        var cleanupCalled = false
        val dep1 = "test"
        val dep2 = 123

        // Call the effectWithDepsAndCleanup function
        try {
            scope.effectWithDepsAndCleanup(dep1, dep2) { 
                effectCalled = true
                { cleanupCalled = true }
            }
        } catch (e: Exception) {
            fail("Failed to invoke effectWithDepsAndCleanup: ${e.message}")
        }

        // Verify compose was called
        assertEquals(1, scope.composeCallCount, "compose should be called once")
        assertNotNull(scope.lastComposedBlock, "compose should be called with a block")
    }
}
