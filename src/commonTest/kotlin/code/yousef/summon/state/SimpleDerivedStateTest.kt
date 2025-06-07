package code.yousef.summon.state

import code.yousef.summon.runtime.CommonComposer
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.MockPlatformRenderer
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SimpleDerivedStateTest {

    @Test
    fun testSimpleDerivedState() {
        val state1 = mutableStateOf(1)
        val state2 = mutableStateOf(2)

        val derived = simpleDerivedStateOf {
            state1.value + state2.value
        }

        assertEquals(3, derived.value)

        state1.value = 10
        assertEquals(12, derived.value)

        state2.value = 20
        assertEquals(30, derived.value)
    }

    @Test
    fun testProduceState() = runTest {
        val renderer = MockPlatformRenderer()
        val composer = CommonComposer()

        var result: State<String>? = null

        CompositionLocal.provideComposer(composer) {
            LocalPlatformRenderer.provides(renderer)

            result = produceState(initialValue = "Initial") {
                value = "Updated"
            }
        }

        assertNotNull(result)
        assertEquals("Initial", result?.value)

        // Would need to wait for coroutine to complete
        // In real usage, the value would update asynchronously
    }

    @Test
    fun testCollectAsState() = runTest {
        val renderer = MockPlatformRenderer()
        val composer = CommonComposer()

        val flow = kotlinx.coroutines.flow.MutableStateFlow(1)
        var result: State<Int>? = null

        CompositionLocal.provideComposer(composer) {
            LocalPlatformRenderer.provides(renderer)

            result = flow.collectAsState()
        }

        assertNotNull(result)
        assertEquals(1, result?.value)
    }
}