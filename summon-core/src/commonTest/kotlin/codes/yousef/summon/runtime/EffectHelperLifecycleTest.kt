package codes.yousef.summon.runtime

import codes.yousef.summon.effects.CompositionScope
import codes.yousef.summon.effects.effectWithDepsAndCleanup
import codes.yousef.summon.effects.onMount
import codes.yousef.summon.state.mutableStateOf
import kotlin.test.*

class EffectHelperLifecycleTest {
    private val scope = object : CompositionScope {
        override fun compose(block: @Composable () -> Unit) = block()
    }

    @Test
    fun effectBeforeRememberUsesDistinctSlotsAcrossUnchangedKeyPasses() {
        val recomposer = Recomposer()
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val tick = mutableStateOf(0)
        var remembered: String? = null
        var creations = 0
        var cleanups = 0
        val root = {
            tick.value
            DisposableEffect(Unit) { { cleanups++ } }
            remembered = remember { creations++; "remembered value" }
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        tick.value++
        scheduler.executeAll()
        assertEquals("remembered value", remembered)
        assertEquals(1, creations)
        assertEquals(0, cleanups)
    }

    @Test
    fun rememberBeforeEffectKeepsRememberedValueWhenEffectKeyChanges() {
        val recomposer = Recomposer()
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val key = mutableStateOf(0)
        var creations = 0
        var remembered: String? = null
        var cleanups = 0
        val root = {
            remembered = remember { creations++; "remembered value" }
            DisposableEffect(key.value) { { cleanups++ } }
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        key.value++
        scheduler.executeAll()
        assertEquals("remembered value", remembered)
        assertEquals(1, creations)
        assertEquals(1, cleanups)
    }

    @Test
    fun onMountRunsOnceUntilConditionalRemovalAndReentry() {
        val recomposer = Recomposer()
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val tick = mutableStateOf(0)
        val visible = mutableStateOf(true)
        var mounts = 0
        val root = {
            tick.value
            if (visible.value) scope.onMount { mounts++ }
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        tick.value++
        scheduler.executeAll()
        assertEquals(1, mounts)
        visible.value = false
        scheduler.executeAll()
        visible.value = true
        scheduler.executeAll()
        assertEquals(2, mounts)
    }

    @Test
    fun equalDependencyValuesDoNotReplaceCleanupEffect() {
        val recomposer = Recomposer()
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val tick = mutableStateOf(0)
        val dependency = mutableStateOf(0)
        var setups = 0
        var cleanups = 0
        val root = {
            tick.value
            scope.effectWithDepsAndCleanup(dependency.value, "stable") {
                setups++
                return@effectWithDepsAndCleanup { cleanups++ }
            }
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        tick.value++
        scheduler.executeAll()
        assertEquals(1, setups)
        assertEquals(0, cleanups)
        dependency.value++
        scheduler.executeAll()
        assertEquals(2, setups)
        assertEquals(1, cleanups)
    }

    @Test
    fun sideEffectRunsAfterSuccessfulRootRatherThanDuringIt() {
        val events = mutableListOf<String>()
        val recomposer = Recomposer()
        recomposer.composeInitial {
            events.add("start")
            SideEffect { events.add("side") }
            events.add("end")
        }
        assertEquals(listOf("start", "end", "side"), events)
    }

    @Test
    fun sideEffectDoesNotRunForFailedRoot() {
        var effects = 0
        assertFailsWith<IllegalStateException> {
            Recomposer().composeInitial {
                SideEffect { effects++ }
                error("synthetic failure")
            }
        }
        assertEquals(0, effects)
    }
}
