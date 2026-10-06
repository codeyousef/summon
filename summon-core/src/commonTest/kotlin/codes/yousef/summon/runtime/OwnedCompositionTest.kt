package codes.yousef.summon.runtime

import codes.yousef.summon.state.mutableStateOf
import kotlin.test.*

class OwnedCompositionTest {
    @Test fun canceledMountReleasesOwnershipBeforePropagatingOriginalCancellation() {
        val cancellation = kotlinx.coroutines.CancellationException("Synthetic private render failure")
        var released = 0
        var cleaned = 0
        assertSame(cancellation, assertFailsWith<kotlinx.coroutines.CancellationException> {
            createOwnedComposition(PlatformRenderer(), QueuedScheduler(), release = { released++ }) {
                DisposableEffect(Unit) { { cleaned++ } }
                throw cancellation
            }
        })
        assertEquals(1, released)
        assertEquals(1, cleaned)
        assertNull(CompositionLocal.currentComposer)
    }
    private class QueuedScheduler : RecompositionScheduler {
        var work: (() -> Unit)? = null
        var canceled = 0
        override fun scheduleRecomposition(work: () -> Unit) { this.work = work }
        override fun cancelPendingRecomposition() { canceled++; work = null }
    }

    @Test fun disposalDetachesQueuedAndLateStateWritesAndRunsCleanupOnce() {
        val state = mutableStateOf(0)
        val scheduler = QueuedScheduler()
        val recomposer = Recomposer()
        recomposer.setScheduler(scheduler)
        var renders = 0
        var cleaned = 0
        val root = { state.value; renders++; DisposableEffect(Unit) { { cleaned++ } } }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        state.value++
        val previouslyQueued = scheduler.work!!
        recomposer.dispose()
        recomposer.dispose()
        previouslyQueued()
        state.value++
        assertEquals(1, renders)
        assertEquals(1, cleaned)
        assertEquals(1, scheduler.canceled)
        assertNull(scheduler.work)
        assertFailsWith<IllegalStateException> { recomposer.createComposer() }
    }

    @Test fun schedulerCleanupFailureDoesNotPreventEffectCleanup() {
        val recomposer = Recomposer()
        recomposer.setScheduler(object : RecompositionScheduler {
            override fun scheduleRecomposition(work: () -> Unit) = Unit
            override fun cancelPendingRecomposition() { error("synthetic scheduler failure") }
        })
        var cleaned = 0
        recomposer.composeInitial { DisposableEffect(Unit) { { cleaned++ } } }
        assertFailsWith<IllegalStateException> { recomposer.dispose() }
        assertEquals(1, cleaned)
        recomposer.dispose()
    }

    @Test fun nestedComposerAndLocalContextRestoreAfterSuccessAndFailure() {
        val local = CompositionLocal.compositionLocalOf<String?>("default")
        val outer = Recomposer()
        val inner = Recomposer()
        withCompositionLocal(local, "outer") {
            outer.composeInitial {
                val outerComposer = CompositionLocal.currentComposer
                assertSame(outer, RecomposerHolder.current())
                withCompositionLocal(local, null) {
                    inner.composeInitial {
                        assertNull(local.current)
                        assertSame(inner, RecomposerHolder.current())
                        assertNotSame(outerComposer, CompositionLocal.currentComposer)
                    }
                }
                assertFalse(inner.isComposing())
                assertTrue(outer.isComposing())
                assertEquals("outer", local.current)
                assertSame(outerComposer, CompositionLocal.currentComposer)
                assertFailsWith<IllegalStateException> {
                    withCompositionLocal(local, "inner") { Recomposer().composeInitial { error("synthetic failure") } }
                }
                assertSame(outerComposer, CompositionLocal.currentComposer)
                assertSame(outer, RecomposerHolder.current())
                assertEquals("outer", local.current)
            }
        }
        assertNull(CompositionLocal.currentComposer)
        assertEquals("default", local.current)
        outer.dispose()
        inner.dispose()
    }

    @Test fun failedOwnedRootReleasesResourcesAndRestoresItsCallerContext() {
        val renderer = PlatformRenderer()
        var detached = 0
        var released = 0
        var effectsCleaned = 0
        val caller = Recomposer()
        caller.composeInitial {
            val composer = CompositionLocal.currentComposer
            val previousRenderer = PlatformRendererStore.get()
            assertFailsWith<IllegalStateException> {
                createOwnedComposition(renderer, QueuedScheduler(), release = { released++ },
                    beforeDispose = { detached++ }) {
                    DisposableEffect(Unit) { { effectsCleaned++ } }
                    error("synthetic initial failure")
                }
            }
            assertSame(composer, CompositionLocal.currentComposer)
            assertSame(caller, RecomposerHolder.current())
            assertSame(previousRenderer, PlatformRendererStore.get())
        }
        assertEquals(1, detached)
        assertEquals(1, released)
        assertEquals(1, effectsCleaned)
        caller.dispose()
    }

    @Test fun failedRecompositionDisposesTheOwnedHandleAndCancelsLateWrites() {
        val scheduler = QueuedScheduler()
        val state = mutableStateOf(false)
        var released = 0
        var cleaned = 0
        val owner = createOwnedComposition(PlatformRenderer(), scheduler, release = { released++ }) {
            DisposableEffect(Unit) { { cleaned++ } }
            if (state.value) error("synthetic recompose failure")
        }
        state.value = true
        assertFailsWith<IllegalStateException> { scheduler.work!!() }
        assertTrue(owner.isDisposed)
        assertEquals(1, released)
        assertEquals(1, cleaned)
        state.value = false
        assertNull(scheduler.work)
        owner.dispose()
        assertEquals(1, released)
    }

}
