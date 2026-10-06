package codes.yousef.summon.routing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PrivateRouterContractTest {
    @Test
    fun guardedPrivateRouteNeverMountsUntilAllowed() {
        var access: GuardResult = GuardResult.Loading
        var privateMounts = 0
        val fallbackStates = mutableListOf<String>()
        val guard = object : RouteGuard {
            override fun canActivate(route: Route, params: RouteParams): GuardResult = access
        }
        val router = createRouter {
            guardedRoute("/mail/thread/:id", guard) { privateMounts++ }
            setGuardFallback { fallbackStates += it.safeReason }
        }

        router.create("/mail/thread/opaque-1")
        access = GuardResult.Locked
        router.create("/mail/thread/opaque-1")
        access = GuardResult.FeatureDisabled
        router.create("/mail/thread/opaque-1")
        access = GuardResult.PermissionDenied
        router.create("/mail/thread/opaque-1")

        assertEquals(0, privateMounts)
        assertEquals(listOf("loading", "locked", "feature unavailable", "permission denied"), fallbackStates)

        access = GuardResult.Allow
        router.create("/mail/thread/opaque-1")
        assertEquals(1, privateMounts)
    }

    @Test
    fun canceledDraftNavigationStaysPendingUntilEncryptedSaveCompletes() {
        val router = createRouter {
            route("/mail/compose") { }
            route("/mail") { }
        }
        router.navigate("/mail/compose", false)
        val control = router.navigationControl()
        control.interceptor = NavigationInterceptor { _, _ -> NavigationDecision.CANCEL }

        router.navigate("/mail")
        assertEquals("/mail/compose", router.currentPath)
        assertEquals("/mail", control.pendingPath)

        control.continuePending()
        assertEquals("/mail", router.currentPath)
        assertEquals(null, control.pendingPath)
    }

    @Test
    fun invalidNavigationDoesNotEchoPrivateInput() {
        val router = createRouter { route("/mail") { } }
        val privateMarker = "private-message-body"
        val error = assertFailsWith<IllegalArgumentException> {
            router.navigate("/mail?body=$privateMarker")
        }
        assertEquals(false, error.message.orEmpty().contains(privateMarker))
        assertEquals("", router.currentPath)
    }
}
