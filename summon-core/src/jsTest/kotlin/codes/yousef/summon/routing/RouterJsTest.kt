package codes.yousef.summon.routing

import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.util.runComposableTest
import kotlinx.browser.window
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Tests for JS-specific implementations of the routing module.
 */
class RouterJsTest {

    // --- Test Setup ---
    private var lastRendered: String? = null
    private var lastRouteParams: RouteParams? = null

    private val homeContent: @Composable (RouteParams) -> Unit = { params ->
        lastRendered = "Home"
        lastRouteParams = params
    }

    private val notFoundContent: @Composable (RouteParams) -> Unit = { params ->
        lastRendered = "NotFound: ${params["path"]}"
        lastRouteParams = params
    }

    private fun createTestRouter(): Router {
        return createRouter {
            route("/", homeContent)
            setNotFound(notFoundContent)
        }
    }

    @BeforeTest
    fun resetBrowserPath() {
        window.history.replaceState(null, "", "/")
    }
    // --- Test Cases ---

    @Test
    fun testRouterCreation() {
        val router = createTestRouter()
        assertNotNull(router, "Router should be created successfully")
    }

    // Note: Removed testRouterCurrentPath as it depends on browser environment
    // In a real browser, the initial path would be the current URL path

    @Test
    fun testRouterNavigation() {
        val router = createTestRouter()

        // Navigate to a path
        router.navigate("/test", false)

        // Current path should be updated
        assertEquals("/test", router.currentPath)
    }

    @Test
    fun testRouterCreate() {
        val router = createTestRouter()

        // Reset tracking variables
        lastRendered = null
        lastRouteParams = null

        // Create with home path
        runComposableTest { router.create("/") }

        // Should render home content
        assertEquals("Home", lastRendered)
        assertNotNull(lastRouteParams)
        assertEquals(emptyMap(), lastRouteParams?.asMap())
    }

    @Test
    fun testRouterCreateNotFound() {
        val router = createTestRouter()

        // Reset tracking variables
        lastRendered = null
        lastRouteParams = null

        // Create with non-existent path
        runComposableTest { router.create("/nonexistent") }

        // Should render not found content
        assertEquals("NotFound: null", lastRendered)
        assertNotNull(lastRouteParams)
        assertEquals(emptyMap(), lastRouteParams?.asMap())
    }
}
