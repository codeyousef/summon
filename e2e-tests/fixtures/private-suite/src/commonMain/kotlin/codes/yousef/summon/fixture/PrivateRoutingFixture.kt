package codes.yousef.summon.fixture

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.routing.GuardResult
import codes.yousef.summon.routing.NavigationDecision
import codes.yousef.summon.routing.NavigationInterceptor
import codes.yousef.summon.routing.Route
import codes.yousef.summon.routing.RouteGuard
import codes.yousef.summon.routing.RouteParams
import codes.yousef.summon.routing.RouterComponent
import codes.yousef.summon.routing.createRouter
import codes.yousef.summon.routing.navigationControl
import codes.yousef.summon.runtime.DisposableEffect
import codes.yousef.summon.state.mutableStateOf

class PrivateRoutingFixture(private val initialPath: String) {
    private val access = mutableStateOf<GuardResult>(GuardResult.Locked)
    private val activePrivateEffects = mutableStateOf(0)
    private val privateMounts = mutableStateOf(0)
    private val encryptedDraftSaved = mutableStateOf(false)
    private val draftDirty = mutableStateOf(false)
    private val showRouter = mutableStateOf(true)
    private val navigationRevision = mutableStateOf(0)

    private val guard = object : RouteGuard {
        override fun canActivate(route: Route, params: RouteParams): GuardResult = access.value
    }

    private val router = createRouter {
        listOf(
            "/mail",
            "/mail/thread/:id",
            "/mail/compose",
            "/calendar",
            "/calendar/event/:id",
            "/aliases",
            "/aliases/:id",
            "/security",
            "/security/devices",
            "/security/recovery",
            "/drive/*",
            "/attention",
            "/connectors/*",
            "/feed",
            "/people/:handle",
            "/communities/:id"
        ).forEach { pattern ->
            guardedRoute(pattern, guard) { params -> PrivateRoute(pattern, params) }
        }
        setNotFound {
            Text("Safe not found", Modifier().attribute("data-testid", "route-state"))
        }
        setGuardFallback { result ->
            Text(result.safeReason, Modifier().attribute("data-testid", "route-state"))
        }
    }
    private val navigation = router.navigationControl()

    init {
        navigation.interceptor = NavigationInterceptor { _, _ ->
            if (draftDirty.value) NavigationDecision.CANCEL else NavigationDecision.PROCEED
        }
    }

    @Composable
    private fun PrivateRoute(pattern: String, params: RouteParams) {
        DisposableEffect(pattern to params.asMap()) {
            privateMounts.value++
            activePrivateEffects.value++
            return@DisposableEffect { activePrivateEffects.value-- }
        }
        val opaque = params.asMap().values.joinToString(",")
        Text(
            if (opaque.isEmpty()) pattern else "$pattern:$opaque",
            Modifier().attribute("data-testid", "route-state")
        )
        if (pattern == "/mail/compose") {
            Button(onClick = { draftDirty.value = true }, label = "Edit encrypted draft")
            Button(onClick = {
                router.navigate("/mail")
                navigationRevision.value++
            }, label = "Leave composer")
        }
    }

    @Composable
    fun Content() {
        Column {
            navigationRevision.value
            Text("${activePrivateEffects.value}", Modifier().attribute("data-testid", "route-effects"))
            Text("${privateMounts.value}", Modifier().attribute("data-testid", "route-mounts"))
            Text(navigation.pendingPath ?: "none", Modifier().attribute("data-testid", "pending-route"))
            Text("${encryptedDraftSaved.value}", Modifier().attribute("data-testid", "encrypted-draft-saved"))
            Button(onClick = { access.value = GuardResult.Allow }, label = "Unlock routes")
            Button(onClick = { access.value = GuardResult.Locked }, label = "Lock routes")
            Button(onClick = { access.value = GuardResult.FeatureDisabled }, label = "Disable route")
            Button(onClick = { access.value = GuardResult.PermissionDenied }, label = "Deny route")
            Button(onClick = { access.value = GuardResult.Loading }, label = "Load authorization")
            Button(onClick = {
                encryptedDraftSaved.value = true
                draftDirty.value = false
                navigation.continuePending()
                navigationRevision.value++
            }, label = "Keep encrypted draft")
            Button(onClick = {
                navigation.cancelPending()
                navigationRevision.value++
            }, label = "Cancel transition")
            Button(onClick = { router.navigate("/mail") }, label = "Open mail")
            Button(onClick = { router.navigate("/calendar") }, label = "Open calendar")
            Button(onClick = { router.navigate("/mail/compose") }, label = "Open composer")
            Button(onClick = { showRouter.value = !showRouter.value }, label = "Toggle router mount")
            if (showRouter.value) RouterComponent(router, initialPath)
        }
    }
}
