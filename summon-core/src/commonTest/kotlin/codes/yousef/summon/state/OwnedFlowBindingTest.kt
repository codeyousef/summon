package codes.yousef.summon.state

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class OwnedFlowBindingTest {
    @Test
    fun readOnlyBindingExposesInitialValueAndStopsAtDisposal() = runTest {
        val flow = MutableStateFlow("initial")
        val binding = bindStateFlow(flow, backgroundScope)
        assertEquals("initial", binding.state.value)
        assertFalse(binding is MutableFlowBinding<*>)
        assertFalse(binding.state is SummonMutableState<*>)
        runCurrent()
        assertEquals(1, flow.subscriptionCount.value)
        flow.value = "current"
        runCurrent()
        assertEquals("current", binding.state.value)
        binding.dispose()
        binding.dispose()
        runCurrent()
        assertEquals(0, flow.subscriptionCount.value)
        flow.value = "private-late-value"
        runCurrent()
        assertEquals("current", binding.state.value)
    }

    @Test
    fun mutableBindingPropagatesBothWaysAndDetachesReverseWrites() = runTest {
        val flow = MutableStateFlow(1)
        val binding = bindMutableStateFlow(flow, backgroundScope)
        runCurrent()
        binding.state.value = 2
        assertEquals(2, flow.value)
        runCurrent()
        flow.value = 3
        runCurrent()
        assertEquals(3, binding.state.value)
        binding.state.value = 3
        runCurrent()
        assertEquals(1, flow.subscriptionCount.value)
        binding.dispose()
        binding.state.value = 4
        assertEquals(3, flow.value)
        runCurrent()
        assertEquals(0, flow.subscriptionCount.value)
    }

    @Test
    fun equalTwoWayWritesDoNotCreateFeedbackEmissions() = runTest {
        val flow = MutableStateFlow(1)
        var emissions = 0
        val collector = backgroundScope.launch {
            flow.collect { emissions++ }
        }
        val binding = bindMutableStateFlow(flow, backgroundScope)
        runCurrent()
        assertEquals(1, emissions)

        binding.state.value = 1
        runCurrent()
        assertEquals(1, emissions)
        binding.state.value = 2
        runCurrent()
        assertEquals(2, emissions)
        flow.value = 2
        runCurrent()
        assertEquals(2, emissions)

        binding.dispose()
        collector.cancel()
    }

    @Test
    fun sharedBindingUsesExplicitInitialValueAndReleasesSubscription() = runTest {
        val flow = MutableSharedFlow<String>()
        val binding = bindSharedFlow(flow, "loading", backgroundScope)
        assertEquals("loading", binding.state.value)
        runCurrent()
        flow.emit("event")
        runCurrent()
        assertEquals("event", binding.state.value)
        binding.dispose()
        runCurrent()
        assertEquals(0, flow.subscriptionCount.value)
    }

    @Test
    fun repeatedMountsLeaveNoSubscriptionsAndDoNotCancelParent() = runTest {
        val flow = MutableStateFlow(0)
        repeat(100) {
            val binding = bindMutableStateFlow(flow, backgroundScope)
            runCurrent()
            assertEquals(1, flow.subscriptionCount.value)
            binding.dispose()
            runCurrent()
            assertEquals(0, flow.subscriptionCount.value)
        }
        assertTrue(backgroundScope.coroutineContext[Job]!!.isActive)
        var unrelatedRan = false
        backgroundScope.launch { unrelatedRan = true }
        runCurrent()
        assertTrue(unrelatedRan)
    }

    @Test
    fun parentCancellationDetachesReverseWritesBeforeCollectorCleanup() = runTest {
        val parent = Job(backgroundScope.coroutineContext[Job])
        val scope = kotlinx.coroutines.CoroutineScope(backgroundScope.coroutineContext + parent)
        val flow = MutableStateFlow(1)
        val binding = bindMutableStateFlow(flow, scope)
        runCurrent()
        assertEquals(1, flow.subscriptionCount.value)
        parent.cancel()
        binding.state.value = 2
        assertEquals(1, flow.value)
        runCurrent()
        assertEquals(0, flow.subscriptionCount.value)
        assertEquals(1, binding.state.value)
    }

    @Test
    fun canceledParentCannotSubscribeOrWriteBack() = runTest {
        val parent = Job()
        parent.cancel()
        val flow = MutableStateFlow(1)
        val binding = bindMutableStateFlow(flow,
            kotlinx.coroutines.CoroutineScope(backgroundScope.coroutineContext + parent))
        binding.state.value = 2
        runCurrent()
        assertEquals(1, flow.value)
        assertEquals(0, flow.subscriptionCount.value)
        assertFalse(parent.isActive)
        binding.dispose()
    }
}
