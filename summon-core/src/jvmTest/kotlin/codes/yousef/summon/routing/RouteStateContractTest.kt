package codes.yousef.summon.routing
import codes.yousef.summon.runtime.PlatformRenderer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RouteStateContractTest {
    private fun render(path: String, block: () -> Unit) {
        val router = createRouter { route(path) { block() } }
        PlatformRenderer().renderComposableRoot { RouterComponent(router, path) }
    }

    @Test
    fun stateIsIsolatedByExactDynamicRouteAndCanBeCleared() {
        val state = RouteState("/users/{id}", "initial")
        var creates = 0
        render("/users/one") {
            assertNull(state.get())
            assertEquals("one", state.getOrCreate { creates++; "one" })
            assertEquals("one", state.getOrCreate { creates++; "replacement" })
            state.update("updated")
            assertEquals("updated", state.get())
        }
        assertEquals(1, creates)

        render("/users/two") {
            assertNull(state.get())
            assertEquals("two", state.getOrCreate { "two" })
        }
        state.clear()
        render("/users/one") { assertNull(state.get()) }
    }

    @Test
    fun nonMatchingAndUnsafePathsNeverReadOrMutateRouteState() {
        val state = RouteState("/users/{id}", "initial")
        render("/users-malicious") {
            assertEquals("initial", state.getOrCreate { "created" })
            assertNull(state.get())
            state.update("forbidden")
        }
        PlatformRenderer().renderComposableRoot {
            assertEquals("initial", state.getOrCreate { "created" })
            assertNull(state.get())
            state.update("forbidden")
        }
    }
}
