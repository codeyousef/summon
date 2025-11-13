package codes.yousef.summon.routing

import codes.yousef.summon.runtime.Composable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RouterJvmTest {

    // --- Test Setup ---
    private var lastRendered: String? = null
    private var lastRouteParams: RouteParams? = null

    private val homeContent: @Composable (RouteParams) -> Unit = { params ->
        lastRendered = "Home"
        lastRouteParams = params
    }
    private val userContent: @Composable (RouteParams) -> Unit = { params ->
        lastRendered = "User: ${params["id"]}"
        lastRouteParams = params
    }
    private val productContent: @Composable (RouteParams) -> Unit = { params ->
        lastRendered = "Product: ${params["productId"]}"
        lastRouteParams = params
    }
    private val notFoundContent: @Composable (RouteParams) -> Unit = { params ->
        lastRendered = "NotFound: ${params["path"]}"
        lastRouteParams = params
    }

    private fun createTestRouter(): Router {
        return createRouter {
            route("/", homeContent)
            route("/users/{id}", userContent)
            route("/products/{productId}", productContent)
            setNotFound(notFoundContent)
        }
    }

    // --- Test Cases ---

    @Test
    fun testExactRouteMatching() {
        val router = createTestRouter()
        router.navigate("/", false) // Use navigate first to set path without calling composable

        assertEquals("/", router.currentPath)

        // Now call create to invoke the composable
        router.create(router.currentPath)
        assertEquals("Home", lastRendered)
        assertNotNull(lastRouteParams)
        assertEquals(emptyMap(), lastRouteParams?.asMap())
    }

    @Test
    fun testParameterizedRouteMatching() {
        val router = createTestRouter()
        router.navigate("/users/123", false)

        assertEquals("/users/123", router.currentPath)

        router.create(router.currentPath)
        assertEquals("User: 123", lastRendered)
        assertNotNull(lastRouteParams)
        assertEquals(mapOf("id" to "123"), lastRouteParams?.asMap())

        // Test another parameterized route
        router.navigate("/products/abc", false)
        assertEquals("/products/abc", router.currentPath)
        router.create(router.currentPath)
        assertEquals("Product: abc", lastRendered)
        assertNotNull(lastRouteParams)
        assertEquals(mapOf("productId" to "abc"), lastRouteParams?.asMap())
    }

    @Test
    fun testNotFoundRoute() {
        val router = createTestRouter()
        router.navigate("/nonexistent", false)

        assertEquals("/nonexistent", router.currentPath)

        router.create(router.currentPath)
        assertEquals("NotFound: /nonexistent", lastRendered)
        assertNotNull(lastRouteParams)
        assertEquals(mapOf("path" to "/nonexistent"), lastRouteParams?.asMap())
    }

    @Test
    fun testNavigateDirectlyUpdatesPathAndParams() {
        val router = createTestRouter()
        router.navigate("/users/456", false)

        assertEquals("/users/456", router.currentPath)
        // Note: We can't directly check internal 'currentParams' easily here without reflection
        // or changing JvmRouter, but we test its effect via create() in other tests.

        // Check navigation to not found path
        router.navigate("/foo/bar", false)
        assertEquals("/foo/bar", router.currentPath)
    }

    @Test
    fun testCreateSetsInitialPathAndRenders() {
        val router = createTestRouter()
        router.create("/users/789") // Call create directly with initial path

        assertEquals("/users/789", router.currentPath)
        assertEquals("User: 789", lastRendered)
        assertNotNull(lastRouteParams)
        assertEquals(mapOf("id" to "789"), lastRouteParams?.asMap())
    }

    // --- RouterContext Tests ---

    // Simple Router implementation for context testing
    class MockRouter(override val currentPath: String = "mock") : Router {
        override fun navigate(path: String, pushState: Boolean) {}

        @Composable
        override fun create(initialPath: String) {
        }
    }

    @Test
    fun testRouterContextWithRouter() {
        val initialContext = RouterContext.current
        val router1 = MockRouter("/router1")
        val router2 = MockRouter("/router2")

        assertNull(
            RouterContext.current,
            "Initial context should ideally be null outside tests, but might depend on test runner state"
        )
        RouterContext.current = null // Ensure clean state for test
        assertNull(RouterContext.current, "Context should be null before withRouter")

        val result = RouterContext.withRouter(router1) {
            assertEquals(router1, RouterContext.current, "Context should be router1 inside block")
            assertEquals("/router1", RouterContext.current?.currentPath)

            // Test nesting
            RouterContext.withRouter(router2) {
                assertEquals(router2, RouterContext.current, "Context should be router2 inside nested block")
                assertEquals("/router2", RouterContext.current?.currentPath)
            }

            assertEquals(router1, RouterContext.current, "Context should be restored to router1 after nested block")
            "BlockResult"
        }

        assertEquals("BlockResult", result, "withRouter should return the block result")
        assertNull(RouterContext.current, "Context should be restored to null after withRouter")

        // Restore initial context if it wasn't null
        RouterContext.current = initialContext
    }

    // --- RouterRegistry Tests ---

    @Test
    fun testRouterRegistry() {
        val router1 = createTestRouter().setupForServer("session1")
        val router2 = createTestRouter().setupForServer("session2")

        // Test setup and get
        assertEquals(router1, getRouterForSession("session1"), "Should retrieve router1 for session1")
        assertEquals(router2, getRouterForSession("session2"), "Should retrieve router2 for session2")
        assertNull(getRouterForSession("nonexistent"), "Should return null for nonexistent session")

        // Test setupForServer sets context (implicitly tested by getRouterForSession working)
        // We can also check RouterContext.current immediately after setupForServer
        // Note: This might interfere with other tests if not cleaned up.
        val router3 = createTestRouter() // Don't register yet
        RouterContext.current = null // Clear context
        router3.setupForServer("session3")
        assertEquals(router3, RouterContext.current, "setupForServer should set RouterContext.current")

        // Test removal
        removeRouterForSession("session1")
        assertNull(getRouterForSession("session1"), "Router1 should be removed")
        assertNotNull(getRouterForSession("session2"), "Router2 should still exist") // Ensure removal is specific

        // Clean up remaining routers
        removeRouterForSession("session2")
        removeRouterForSession("session3")
        RouterContext.current = null // Clean up context
    }
} 