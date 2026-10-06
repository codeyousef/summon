package codes.yousef.summon.runtime

import codes.yousef.summon.state.mutableStateOf
import kotlin.test.*

class RememberLifetimeTest {
    private data class CollidingKey(val name: String) { override fun hashCode() = 1 }

    @Test fun nullableRememberedValueIsOccupiedAcrossUnrelatedUpdates() {
        val recomposer = Recomposer()
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val tick = mutableStateOf(0)
        var calculations = 0
        val root = {
            tick.value
            assertNull(remember<String?> { calculations++; null })
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        repeat(5) { tick.value++; scheduler.executeAll() }
        assertEquals(1, calculations)
        recomposer.dispose()
    }

    @Test fun keyedNullableValueRecalculatesOnlyWhenKeysChange() {
        val recomposer = Recomposer()
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val tick = mutableStateOf(0)
        val key = mutableStateOf(0)
        var calculations = 0
        val root = {
            tick.value
            assertNull(remember<String?>(key.value, "stable") { calculations++; null })
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        tick.value++
        scheduler.executeAll()
        assertEquals(1, calculations)
        key.value++
        scheduler.executeAll()
        tick.value++
        scheduler.executeAll()
        assertEquals(2, calculations)
        recomposer.dispose()
    }

    @Test fun omittedRememberedValueInitializesAgainOnReentry() {
        val recomposer = Recomposer()
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val visible = mutableStateOf(true)
        var calculations = 0
        var observed: String? = null
        val root = {
            observed = if (visible.value) remember { "generation-${++calculations}" } else null
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        assertEquals("generation-1", observed)
        visible.value = false
        scheduler.executeAll()
        assertNull(observed)
        visible.value = true
        scheduler.executeAll()
        assertEquals("generation-2", observed)
        assertEquals(2, calculations)
        recomposer.dispose()
    }

    @Test fun namedKeysDoNotCollideWithEachOtherOrPositionalRemember() {
        val recomposer = Recomposer()
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val tick = mutableStateOf(0)
        val first = CollidingKey("first")
        val second = CollidingKey("second")
        var calculations = 0
        val root = {
            tick.value
            val remembered = remember { calculations++; "positional" }
            val composer = CompositionLocal.currentComposer!!
            composer.updateRememberedValue(first, "first named")
            composer.updateRememberedValue(second, "second named")
            assertEquals("first named", composer.rememberedValue(first))
            assertEquals("second named", composer.rememberedValue(second))
            assertEquals("positional", remembered)
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        tick.value++
        scheduler.executeAll()
        assertEquals(1, calculations)
        recomposer.dispose()
    }

    @Test fun namedNumericKeyCannotReplaceAnOwnedEffectSlot() {
        val recomposer = Recomposer()
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val tick = mutableStateOf(0)
        var setups = 0
        var cleanups = 0
        val root = {
            tick.value
            DisposableEffect(Unit) { setups++; { cleanups++ } }
            CompositionLocal.currentComposer!!.updateRememberedValue(1, "named value")
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        tick.value++
        scheduler.executeAll()
        assertEquals(1, setups)
        assertEquals(0, cleanups)
        recomposer.dispose()
        assertEquals(1, cleanups)
    }

    @Test fun commonComposerNamedCacheHasIndependentEqualityKeysAndDisposal() {
        val composer = CommonComposer()
        composer.setSlot("positional")
        val first = CollidingKey("first")
        val second = CollidingKey("second")
        composer.updateRememberedValue(first, "first named")
        composer.updateRememberedValue(second, "second named")
        composer.updateRememberedValue(0, "numeric named")
        assertEquals("positional", composer.getSlot())
        assertEquals("first named", composer.rememberedValue(first))
        assertEquals("second named", composer.rememberedValue(second))
        assertEquals("numeric named", composer.rememberedValue(0))
        composer.dispose()
        assertNull(composer.rememberedValue(first))
        assertNull(composer.rememberedValue(second))
        assertNull(composer.getSlot())
    }
}
