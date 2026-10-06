package codes.yousef.summon.runtime

import codes.yousef.summon.state.mutableStateOf
import kotlin.test.*

class RecomposerLifecycleTest {
    @AfterTest
    fun resetHolder() = RecomposerHolder.setRecomposer(null)

    @Test
    fun disposedComposerCannotRunQueuedOrLateWork() {
        val recomposer = Recomposer()
        RecomposerHolder.setRecomposer(recomposer)
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val state = mutableStateOf(0)
        var renders = 0
        val root = { state.value; renders++; Unit }
        recomposer.setCompositionRoot(root)
        val composer = recomposer.createComposer()
        composer.compose(root)
        state.value = 1
        assertEquals(1, scheduler.pendingWorkCount())
        composer.dispose()
        scheduler.executeAll()
        state.value = 2
        composer.reportChanged()
        assertEquals(1, renders)
        assertFalse(scheduler.hasPendingWork())
    }

    @Test
    fun removedStateDependencyDoesNotScheduleAnotherPass() {
        val recomposer = Recomposer()
        RecomposerHolder.setRecomposer(recomposer)
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val show = mutableStateOf(true)
        val privateState = mutableStateOf("private")
        var renders = 0
        val root = { if (show.value) privateState.value; renders++; Unit }
        recomposer.setCompositionRoot(root)
        val composer = recomposer.createComposer()
        composer.compose(root)
        show.value = false
        scheduler.executeAll()
        assertEquals(2, renders)
        privateState.value = "late"
        assertFalse(scheduler.hasPendingWork())
        composer.dispose()
    }

    @Test
    fun cleanupDetachesBeforeCallbacksAndRunsEveryCallbackOnceOnFailure() {
        val recomposer = Recomposer()
        val composer = recomposer.createComposer()
        var cleanupCount = 0
        composer.registerDisposable { cleanupCount++; composer.dispose(); error("synthetic failure") }
        composer.registerDisposable { cleanupCount++ }
        assertFailsWith<IllegalStateException> { composer.dispose() }
        composer.dispose()
        assertEquals(2, cleanupCount)
        composer.registerDisposable { cleanupCount++ }
        assertEquals(3, cleanupCount)
        assertFailsWith<IllegalStateException> { composer.compose {} }
    }

    @Test
    fun independentRootsObserveOnlyTheirOwnStateAndDetachOnDisposal() {
        val first = Recomposer()
        val second = Recomposer()
        val firstScheduler = TestScheduler()
        val secondScheduler = TestScheduler()
        first.setScheduler(firstScheduler)
        second.setScheduler(secondScheduler)
        val firstState = mutableStateOf(0)
        val secondState = mutableStateOf(0)
        var firstRenders = 0
        var secondRenders = 0
        val firstRoot = { firstState.value; firstRenders++; Unit }
        val secondRoot = { secondState.value; secondRenders++; Unit }
        first.setCompositionRoot(firstRoot)
        second.setCompositionRoot(secondRoot)
        val firstComposer = first.createComposer()
        val secondComposer = second.createComposer()
        firstComposer.compose(firstRoot)
        secondComposer.compose(secondRoot)
        // The global holder can point elsewhere without redirecting root state.
        RecomposerHolder.setRecomposer(Recomposer())
        firstState.value++
        assertTrue(firstScheduler.hasPendingWork())
        assertFalse(secondScheduler.hasPendingWork())
        firstScheduler.executeAll()
        assertEquals(2, firstRenders)
        assertEquals(1, secondRenders)
        firstComposer.dispose()
        firstState.value++
        assertFalse(firstScheduler.hasPendingWork())
        secondState.value++
        secondScheduler.executeAll()
        assertEquals(2, secondRenders)
        secondComposer.dispose()
    }

    @Test
    fun listenersMayDetachAndAttachDuringNotification() {
        val state = mutableStateOf(0)
        val events = mutableListOf<String>()
        val added: (Int) -> Unit = { events.add("added:$it") }
        lateinit var original: (Int) -> Unit
        original = {
            events.add("original:$it")
            state.removeListener(original)
            state.addListener(added)
        }
        state.addListener(original)
        state.value = 1
        state.value = 2
        assertEquals(listOf("original:1", "added:2"), events)
    }

}
