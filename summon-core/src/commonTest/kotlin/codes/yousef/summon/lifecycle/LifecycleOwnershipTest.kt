package codes.yousef.summon.lifecycle

import codes.yousef.summon.LifecycleAwareComponent
import codes.yousef.summon.LifecycleEffect
import codes.yousef.summon.whenActive
import codes.yousef.summon.util.runDetailedComposableTest
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

    @Test
    fun lifecycleAwareDispatchesEveryStateAndRejectsMissingOwners() {
        val owner = LifecycleOwner()
        val events = mutableListOf<String>()
        val component = codes.yousef.summon.lifecycleAware(owner) {
            onCreate { events += "create" }
            onStart { events += "start" }
            onResume { events += "resume" }
            onPause { events += "pause" }
            onStop { events += "stop" }
            onDestroy { events += "destroy" }
        }!!
        assertEquals(listOf("create", "start", "resume"), events)

        owner.currentState = LifecycleState.PAUSED
        owner.currentState = LifecycleState.STOPPED
        owner.currentState = LifecycleState.CREATED
        owner.currentState = LifecycleState.STARTED
        owner.currentState = LifecycleState.RESUMED
        owner.currentState = LifecycleState.INITIALIZED
        assertEquals(
            listOf("create", "start", "resume", "pause", "stop", "create", "start", "resume"),
            events
        )
        assertSame("receiver", component.compose("receiver"))
        component.dispose()
        owner.currentState = LifecycleState.PAUSED
        assertEquals(1, events.count { it == "destroy" })
        assertEquals(null, codes.yousef.summon.lifecycleAware(null) {})
        assertEquals(null, whenActive(null, "missing") {})
    }

    @Test
    fun lifecycleEffectOwnsObserverAndDispatchesOptionalCallbacks() {
        val owner = LifecycleOwner()
        val events = mutableListOf<String>()
        val (_, composer) = runDetailedComposableTest {
            LifecycleEffect(
                lifecycleOwner = owner,
                key = "effect",
                onCreate = { events += "create" },
                onStart = { events += "start" },
                onResume = { events += "resume" },
                onPause = { events += "pause" },
                onStop = { events += "stop" },
                onDestroy = { events += "destroy" }
            )
            LifecycleEffect(lifecycleOwner = null)
        }
        assertEquals(listOf("create", "start", "resume"), events)
        owner.currentState = LifecycleState.PAUSED
        owner.currentState = LifecycleState.STOPPED
        owner.currentState = LifecycleState.DESTROYED
        assertEquals(listOf("create", "start", "resume", "pause", "stop", "destroy"), events)
        composer.dispose()
    }

}
