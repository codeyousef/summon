package codes.yousef.summon.runtime

import codes.yousef.summon.state.mutableStateOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class EffectLifecycleTest {
    @AfterTest
    fun resetHolder() = RecomposerHolder.setRecomposer(null)

    @Test
    fun keyChangeAndConditionalRemovalReleaseExactlyOnce() {
        val recomposer = Recomposer()
        RecomposerHolder.setRecomposer(recomposer)
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val visible = mutableStateOf(true)
        val key = mutableStateOf(0)
        val mounts = mutableListOf<Int>()
        val removals = mutableListOf<Int>()
        val root = {
            if (visible.value) {
                val currentKey = key.value
                DisposableEffect(currentKey) {
                    mounts.add(currentKey)
                    return@DisposableEffect { removals.add(currentKey); Unit }
                }
            }
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        key.value = 1
        scheduler.executeAll()
        assertEquals(listOf(0, 1), mounts)
        assertEquals(listOf(0), removals)
        visible.value = false
        scheduler.executeAll()
        assertEquals(listOf(0, 1), removals)
        visible.value = true
        scheduler.executeAll()
        assertEquals(listOf(0, 1, 1), mounts)
        visible.value = false
        scheduler.executeAll()
        assertEquals(listOf(0, 1, 1), removals)
    }

    @Test
    fun removedLaunchedEffectCancelsItsSuspendedWork() = runTest {
        val recomposer = Recomposer()
        RecomposerHolder.setRecomposer(recomposer)
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val visible = mutableStateOf(true)
        val started = CompletableDeferred<Unit>()
        val cancelled = CompletableDeferred<Unit>()
        val root = {
            if (visible.value) LaunchedEffect(Unit) {
                started.complete(Unit)
                try { awaitCancellation() } finally { cancelled.complete(Unit) }
            }
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        started.await()
        visible.value = false
        scheduler.executeAll()
        cancelled.await()
        assertFalse(scheduler.hasPendingWork())
    }

    @Test
    fun effectsOutsideCompositionFailWithoutCreatingResources() {
        var allocated = false
        assertFailsWith<IllegalArgumentException> {
            DisposableEffect { allocated = true; {} }
        }
        assertFalse(allocated)
        assertFailsWith<IllegalArgumentException> { LaunchedEffect {} }
    }

    @Test
    fun failedInitialCompositionReleasesAlreadyCreatedEffects() {
        val recomposer = Recomposer()
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val state = mutableStateOf(0)
        var cleanupCount = 0
        val root = {
            state.value
            DisposableEffect(Unit) { { cleanupCount++ } }
            error("synthetic render failure")
        }
        recomposer.setCompositionRoot(root)
        assertFailsWith<IllegalStateException> { recomposer.composeInitial(root) }
        assertEquals(1, cleanupCount)
        state.value++
        assertFalse(scheduler.hasPendingWork())
    }

}
