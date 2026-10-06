package codes.yousef.summon.runtime

import codes.yousef.summon.state.mutableStateOf
import kotlin.test.*

class CompositionGroupTest {
    private data class CollidingKey(val name: String) { override fun hashCode() = 1 }
    private fun <T> group(name: String, block: () -> T): T {
        val composer = CompositionLocal.currentComposer!!
        composer.startGroup(name)
        return try { block() } finally { composer.endGroup() }
    }

    @Test fun duplicateExplicitKeysRejectWithoutLeakingKeyValues() {
        val recomposer = Recomposer()
        var disposed = 0
        val error = assertFailsWith<IllegalStateException> {
            recomposer.composeInitial {
                key("secret-account") { DisposableEffect(Unit) { { disposed++ } } }
                key("secret-account") { remember { "duplicate" } }
            }
        }
        assertFalse(error.message.orEmpty().contains("secret-account"))
        assertEquals(1, disposed)
        assertNull(CompositionLocal.currentComposer)
        recomposer.dispose()
        assertEquals(1, disposed)
    }

    @Test fun nestedParentKeysScopeNamedCachesAndResetOnReplacement() {
        val recomposer = Recomposer()
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val account = mutableStateOf("a")
        val visible = mutableStateOf(true)
        var calculations = 0
        var cleanups = 0
        val root = {
            key(account.value) {
                key("child") {
                    val composer = CompositionLocal.currentComposer!!
                    if (composer.rememberedValue("value") == null) {
                        composer.updateRememberedValue("value", ++calculations)
                    }
                    if (visible.value) key("optional") {
                        DisposableEffect(Unit) { { cleanups++ } }
                    }
                }
                key("sibling") {
                    assertNull(CompositionLocal.currentComposer!!.rememberedValue("value"))
                }
            }
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        visible.value = false
        scheduler.executeAll()
        assertEquals(1, calculations)
        assertEquals(1, cleanups)
        visible.value = true
        scheduler.executeAll()
        account.value = "b"
        scheduler.executeAll()
        assertEquals(2, calculations)
        assertEquals(2, cleanups)
        account.value = "a"
        scheduler.executeAll()
        assertEquals(3, calculations)
        assertEquals(3, cleanups)
        recomposer.dispose()
        assertEquals(4, cleanups)
    }

    @Test fun caughtNestedExceptionRestoresParentCursor() {
        val recomposer = Recomposer()
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val tick = mutableStateOf(0)
        var calculations = 0
        val root = {
            tick.value
            val before = remember { "before" }
            assertFailsWith<IllegalArgumentException> {
                key("throwing") { remember { mutableStateOf(1) }; errorArgument() }
            }
            val after = remember { calculations++; "after" }
            assertEquals("before", before)
            assertEquals("after", after)
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        tick.value++
        scheduler.executeAll()
        assertEquals(1, calculations)
        assertNull(CompositionLocal.currentComposer)
        recomposer.dispose()
    }

    private fun errorArgument(): Nothing = throw IllegalArgumentException("synthetic")

    @Test fun commonComposerScopesExplicitGroupsAndPrunesRemovedResources() {
        val composer = CommonComposer()
        var cleanups = 0
        var calculations = 0
        fun render(visible: Boolean) = composer.compose {
            if (visible) key("input") {
                remember { mutableStateOf(1) }
                DisposableEffect(Unit) { { cleanups++ } }
            }
            assertEquals("parent", remember { calculations++; "parent" })
        }
        render(true)
        render(false)
        render(true)
        assertEquals(1, calculations)
        assertEquals(1, cleanups)
        composer.dispose()
        assertEquals(2, cleanups)
    }

    @Test fun conditionalChildDoesNotShiftTypedParentState() {
        val recomposer = Recomposer()
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val visible = mutableStateOf(true)
        var observed = ""
        var calculations = 0
        val root = {
            if (visible.value) group("input") { remember { mutableStateOf(42) } }
            observed = remember { calculations++; "parent" }
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        visible.value = false
        scheduler.executeAll()
        assertEquals("parent", observed)
        assertEquals(1, calculations)
        recomposer.dispose()
    }

    @Test fun siblingStateAndEffectsFollowKeysThroughInsertReorderAndRemoval() {
        val recomposer = Recomposer()
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val items = mutableStateOf(listOf("a", "b"))
        val created = mutableMapOf<String, Int>()
        val disposed = mutableMapOf<String, Int>()
        val observed = mutableMapOf<String, String>()
        val root = {
            observed.clear()
            items.value.forEach { id -> key(id) {
                observed[id] = remember { "$id:${created.getOrElse(id) { 0 }.plus(1).also { created[id] = it }}" }
                DisposableEffect(Unit) { { disposed[id] = disposed.getOrElse(id) { 0 } + 1 } }
            } }
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        items.value = listOf("b", "c", "a")
        scheduler.executeAll()
        assertEquals(mapOf("a" to "a:1", "b" to "b:1", "c" to "c:1"), observed)
        assertTrue(disposed.isEmpty())
        items.value = listOf("c", "a")
        scheduler.executeAll()
        assertEquals(mapOf("b" to 1), disposed)
        items.value = listOf("b", "a")
        scheduler.executeAll()
        assertEquals(mapOf("b" to "b:2", "a" to "a:1"), observed)
        assertEquals(mapOf("b" to 1, "c" to 1), disposed)
        recomposer.dispose()
        assertEquals(mapOf("a" to 1, "b" to 2, "c" to 1), disposed)
    }

    @Test fun composerKeyRetainsEqualityDespiteHashCollisionsAcrossPublicComposePasses() {
        val recomposer = Recomposer()
        val composer = recomposer.createComposer()
        var calculations = 0
        fun render(order: List<String>) = composer.compose {
            order.forEach { name -> composer.key(CollidingKey(name)) {
                assertEquals(name, remember { calculations++; name })
            } }
        }
        render(listOf("a", "b"))
        render(listOf("b", "a"))
        assertEquals(2, calculations)
        recomposer.dispose()
    }

    @Test fun nestedComposeDoesNotResetParentSlotsOrCommitEffectsEarly() {
        val recomposer = Recomposer()
        val composer = recomposer.createComposer()
        var calculations = 0
        var rendered = false
        repeat(2) {
            rendered = false
            composer.compose {
                key("outer") {
                    assertEquals("before", remember { calculations++; "before" })
                    composer.compose {
                        key("inner") { assertEquals(42, remember { calculations++; 42 }) }
                        SideEffect { assertTrue(rendered) }
                    }
                    assertEquals("after", remember { calculations++; "after" })
                }
                rendered = true
            }
        }
        assertEquals(3, calculations)
        recomposer.dispose()
    }

    @Test fun uncaughtNestedFailureReleasesAllGroupsAndRestoresCaller() {
        val caller = CommonComposer()
        val recomposer = Recomposer()
        var disposed = 0
        CompositionLocal.provideComposer(caller) {
            assertFailsWith<IllegalArgumentException> {
                recomposer.composeInitial {
                    key("outer") {
                        DisposableEffect(Unit) { { disposed++ } }
                        key("inner") {
                            DisposableEffect(Unit) { { disposed++ } }
                            errorArgument()
                        }
                    }
                }
            }
            assertSame(caller, CompositionLocal.currentComposer)
        }
        assertEquals(2, disposed)
        recomposer.dispose()
        assertEquals(2, disposed)
    }

    @Test fun routePathAndParametersOwnIndependentStateAndReleaseOldEffects() {
        val recomposer = Recomposer()
        val scheduler = TestScheduler()
        recomposer.setScheduler(scheduler)
        val routeId = mutableStateOf("one")
        var calculations = 0
        var cleanups = 0
        val route = codes.yousef.summon.routing.RouteDefinition("/items/{id}", { params ->
            assertEquals(params.params.getValue("id"), remember { calculations++; params.params.getValue("id") })
            DisposableEffect(Unit) { { cleanups++ } }
        })
        val root = {
            codes.yousef.summon.routing.RouteContentHandler(
                codes.yousef.summon.routing.RouteMatchResult(route, mapOf("id" to routeId.value))
            )
        }
        recomposer.setCompositionRoot(root)
        recomposer.composeInitial(root)
        routeId.value = "two"
        scheduler.executeAll()
        assertEquals(2, calculations)
        assertEquals(1, cleanups)
        routeId.value = "one"
        scheduler.executeAll()
        assertEquals(3, calculations)
        assertEquals(2, cleanups)
        recomposer.dispose()
        assertEquals(3, cleanups)
    }
}
