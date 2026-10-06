package codes.yousef.summon.lifecycle

import codes.yousef.summon.LifecycleAwareComponent
import codes.yousef.summon.whenActive
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class LifecycleOwnershipTest {
    @Test
    fun activeWorkPausesResumesAndStopsExactlyOnce() = runTest {
        val owner = LifecycleOwner()
        val scope = owner.lifecycleScope(backgroundScope.coroutineContext)
        var starts = 0
        var cleanups = 0

        val component = whenActive(owner, key = "account-A") {
            starts++
            try {
                awaitCancellation()
            } finally {
                cleanups++
            }
        }!!

        runCurrent()
        assertEquals(1, starts)
        assertSame(scope, owner.lifecycleScope)

        owner.currentState = LifecycleState.PAUSED
        runCurrent()
        assertEquals(1, cleanups)

        owner.currentState = LifecycleState.STARTED
        owner.currentState = LifecycleState.RESUMED
        runCurrent()
        assertEquals(2, starts)

        component.dispose()
        component.dispose()
        runCurrent()
        assertEquals(2, cleanups)
        assertFalse(scope.isDisposed)

        owner.currentState = LifecycleState.DESTROYED
        runCurrent()
        owner.currentState = LifecycleState.RESUMED
        assertEquals(LifecycleState.DESTROYED, owner.currentState)
        assertTrue(scope.isDisposed)
        assertFalse(scope.coroutineContext[Job]!!.isActive)
        assertSame(scope, owner.lifecycleScope)
    }

    @Test
    fun parentCancellationDisposesTheRegisteredScopeWithoutReplacement() = runTest {
        val owner = LifecycleOwner()
        val parent = Job(backgroundScope.coroutineContext[Job])
        val scope = owner.lifecycleScope(backgroundScope.coroutineContext + parent)

        assertTrue(scope.coroutineContext[Job]!!.isActive)
        parent.cancel()
        runCurrent()

        assertTrue(scope.isDisposed)
        assertFalse(scope.coroutineContext[Job]!!.isActive)
        assertSame(scope, owner.lifecycleScope)
        owner.currentState = LifecycleState.DESTROYED
    }

    @Test
    fun observerFailureStillRemovesDestroyedOwnership() {
        val owner = LifecycleOwner()
        var destroyCalls = 0
        val component = LifecycleAwareComponent(
            lifecycleOwner = owner,
            onDestroy = {
                destroyCalls++
                error("synthetic lifecycle failure")
            }
        )

        assertFailsWith<IllegalStateException> {
            owner.currentState = LifecycleState.DESTROYED
        }
        assertEquals(1, destroyCalls)
        component.dispose()
        assertEquals(1, destroyCalls)
    }
}
