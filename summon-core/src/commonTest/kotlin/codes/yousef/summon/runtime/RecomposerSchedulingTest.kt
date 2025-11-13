package codes.yousef.summon.runtime

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.state.mutableStateOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecomposerSchedulingTest {

    @Test
    fun testRecomposerSchedulesRecomposition() {
        val recomposer = Recomposer()
        val testScheduler = TestScheduler()
        recomposer.setScheduler(testScheduler)

        var recompositionCount = 0

        // Create a composable that tracks recomposition
        val composable: @Composable () -> Unit = {
            recompositionCount++
        }

        // Set composition root
        recomposer.setCompositionRoot(composable)

        // Initial composition
        val composer = recomposer.createComposer()
        composer.compose(composable)
        assertEquals(1, recompositionCount)

        // Schedule a recomposition
        recomposer.scheduleRecomposition(composer)

        // Verify that recomposition was scheduled but not executed yet
        assertTrue(testScheduler.hasPendingWork())
        assertEquals(1, recompositionCount)

        // Process scheduled recompositions
        testScheduler.executeAll()

        // Verify recomposition happened
        assertEquals(2, recompositionCount)
    }

    @Test
    fun testStateChangeTriggersScheduledRecomposition() {
        val recomposer = Recomposer()
        RecomposerHolder.setRecomposer(recomposer)

        val testScheduler = TestScheduler()
        recomposer.setScheduler(testScheduler)

        var compositionCount = 0
        val state = mutableStateOf(0)

        val composable: @Composable () -> Unit = {
            compositionCount++
            val value = state.value // Read state to establish dependency
            // Use value to avoid unused variable warning
            println("State value: $value")
        }

        // Set the composition root
        recomposer.setCompositionRoot(composable)

        // Initial composition
        val composer = recomposer.createComposer()
        recomposer.setActiveComposer(composer)
        composer.compose(composable)
        assertEquals(1, compositionCount)

        // Clear active composer to simulate being outside composition
        recomposer.setActiveComposer(null)

        // Change state (outside of composition)
        state.value = 1

        // Verify recomposition was scheduled
        assertTrue(testScheduler.hasPendingWork())
        assertEquals(1, compositionCount) // Not recomposed yet

        // Execute scheduled work
        testScheduler.executeAll()

        // Verify recomposition happened
        assertEquals(2, compositionCount)
    }

    @Test
    fun testMultipleStateChangesCoalesceIntoSingleRecomposition() {
        val recomposer = Recomposer()
        RecomposerHolder.setRecomposer(recomposer)

        val testScheduler = TestScheduler()
        recomposer.setScheduler(testScheduler)

        var compositionCount = 0
        val state1 = mutableStateOf(0)
        val state2 = mutableStateOf("hello")

        val composable: @Composable () -> Unit = {
            compositionCount++
            val value1 = state1.value
            val value2 = state2.value
            println("Values: $value1, $value2")
        }

        // Set the composition root
        recomposer.setCompositionRoot(composable)

        // Initial composition
        val composer = recomposer.createComposer()
        recomposer.setActiveComposer(composer)
        composer.compose(composable)
        assertEquals(1, compositionCount)

        // Clear active composer to simulate being outside composition
        recomposer.setActiveComposer(null)

        // Multiple state changes (outside of composition)
        state1.value = 1
        state2.value = "world"
        state1.value = 2

        // Should only have one scheduled recomposition
        assertEquals(1, testScheduler.pendingWorkCount())

        // Execute scheduled work
        testScheduler.executeAll()

        // Verify only one recomposition happened
        assertEquals(2, compositionCount)
    }
}

/**
 * Test scheduler for unit testing
 */
class TestScheduler : RecompositionScheduler {
    private val pendingWork = mutableListOf<() -> Unit>()

    override fun scheduleRecomposition(work: () -> Unit) {
        pendingWork.add(work)
    }

    fun hasPendingWork(): Boolean = pendingWork.isNotEmpty()

    fun pendingWorkCount(): Int = pendingWork.size

    fun executeAll() {
        val workToExecute = pendingWork.toList()
        pendingWork.clear()
        workToExecute.forEach { it() }
    }
}