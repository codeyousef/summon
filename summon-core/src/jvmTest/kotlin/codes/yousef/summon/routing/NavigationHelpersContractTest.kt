package codes.yousef.summon.routing

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.CallbackRegistry
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.util.runComposableTest
import codes.yousef.summon.util.MockComposer
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class NavigationHelpersContractTest {
    private class RecordingRouter(
        override var currentPath: String,
        private val page: @Composable () -> Unit = {},
        private val reject: Boolean = false,
    ) : Router {
        val navigations = mutableListOf<Pair<String, Boolean>>()

        override fun navigate(path: String, pushState: Boolean) {
            if (reject) throw IllegalArgumentException("rejected")
            navigations += path to pushState
            currentPath = path
        }

        @Composable
        override fun create(initialPath: String) = page()
    }

    private class CapturingRenderer : PlatformRenderer() {
        lateinit var linkModifier: Modifier

        override fun renderEnhancedLink(
            href: String,
            target: String?,
            title: String?,
            ariaLabel: String?,
            ariaDescribedBy: String?,
            modifier: Modifier,
            content: @Composable () -> Unit,
        ) {
            linkModifier = modifier
        }
    }

    @AfterTest
    fun cleanUp() {
        RouterContext.clear()
        CallbackRegistry.clear()
    }

    @Test
    fun imperativeRedirectsRespectConditionPermanenceContextAndFailures() {
        val router = RecordingRouter("/")
        assertTrue(redirectTo("/next", router = router))
        assertTrue(redirectIf("/replace", condition = true, permanent = true, router = router))
        assertFalse(redirectIf("/ignored", condition = false, router = router))
        assertEquals(listOf("/next" to true, "/replace" to false), router.navigations)

        RouterContext.withRouter(router) { assertTrue(redirectTo("/context")) }
        assertEquals("/context", router.currentPath)
        assertFalse(redirectTo("/none"))
        assertFalse(redirectTo("/bad", router = RecordingRouter("/", reject = true)))
    }

    @Test
    fun navLinkAppliesActiveStateAndItsCallbackUsesClientSideRouterNavigation() {
        lateinit var router: RecordingRouter
        router = RecordingRouter("/users/42", page = {
            NavLink(
                to = "/users",
                activeModifier = Modifier().attribute("data-active", "yes"),
            ) { Text("Users") }
        })
        val renderer = CapturingRenderer()
        runComposableTest(renderer, MockComposer()) { RouterComponent(router, router.currentPath) }
        assertEquals("yes", renderer.linkModifier.attributes["data-active"])
        renderer.linkModifier.eventHandlers.getValue("click").invoke()
        assertEquals("/users" to true, router.navigations.single())
    }

    @Test
    fun exactAndMissingRouterLinksStayInactive() {
        lateinit var exactRouter: RecordingRouter
        exactRouter = RecordingRouter("/users/42", page = {
            NavLink(
                to = "/users",
                exact = true,
                activeModifier = Modifier().attribute("data-active", "yes"),
            ) { Text("Exact") }
        })
        val exact = PlatformRenderer().renderComposableRoot {
            RouterComponent(exactRouter, exactRouter.currentPath)
        }
        assertFalse(exact.contains("data-active=\"yes\""))

        val noRouter = PlatformRenderer().renderComposableRoot {
            NavLink("/plain", activeModifier = Modifier().attribute("data-active", "yes")) { Text("Plain") }
        }
        assertFalse(noRouter.contains("data-active=\"yes\""))
    }
}
