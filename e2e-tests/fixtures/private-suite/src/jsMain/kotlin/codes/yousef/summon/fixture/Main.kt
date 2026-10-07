package codes.yousef.summon.fixture
import codes.yousef.summon.renderComposableRoot
import codes.yousef.summon.runtime.MicrotaskScheduler
import codes.yousef.summon.runtime.SummonHydrationClient
import codes.yousef.summon.mountComposableRoot
import kotlinx.browser.window
import kotlinx.browser.document
fun main() {
    if (window.location.search.contains("hydrationAdversarial=true")) {
        window.asDynamic().__summonXss = 0
        document.getElementById("root")?.textContent = "Public shell: adversarial state remains inert"
        val publicState = document.createElement("script")
        publicState.id = "summon-state"
        publicState.setAttribute("type", "application/json")
        publicState.textContent = window.btoa(
            """{"label":"</script><script>globalThis.__summonXss=1</script>"}"""
        )
        document.body?.appendChild(publicState)
        val metadata = document.createElement("script")
        metadata.id = "summon-hydration-data"
        metadata.setAttribute("type", "application/json")
        metadata.textContent =
            """{"version":1,"callbacks":[],"callbackContext":"","timestamp":0,"renderer":"js","hydrationMarkers":true,"seoCompatible":true}"""
        document.body?.appendChild(metadata)
        SummonHydrationClient.initialize()
        return
    }
    if (window.location.search.contains("hydrationMismatch=true")) {
        document.getElementById("root")?.textContent = "Public shell: sign in to unlock"
        val metadata = document.createElement("script")
        metadata.id = "summon-hydration-data"
        metadata.setAttribute("type", "application/json")
        metadata.textContent =
            """{"version":999,"callbacks":[],"callbackContext":"","timestamp":0,"renderer":"js","hydrationMarkers":true,"seoCompatible":true}"""
        document.body?.appendChild(metadata)
        SummonHydrationClient.initialize()
        return
    }
    if (window.location.search.contains("ownership=true")) {
        val fixture = LifecycleOwnershipFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if (window.location.search.contains("identity=true")) {
        val fixture = StableRenderingFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if (window.location.search.contains("safeContent=true")) {
        val fixture = SafeContentFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if (window.location.search.contains("privateRouting=true")) {
        val fixture = PrivateRoutingFixture(window.location.pathname)
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if (window.location.search.contains("router=true")) {
        val fixture = RouterFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if (window.location.search.contains("responsive=true")) {
        val fixture = ResponsiveLifecycleFixture { session, fail ->
            mountComposableRoot("root") {
                ResponsiveContent(session, fail)
            }
        }
        fixture.replace()
        val second = FixtureSession("Synthetic account B")
        mountComposableRoot("second-root") { FixtureApp(second) }
        mountComposableRoot("controls") { fixture.Controls() }
        return
    }
    if (window.location.search.contains("failures=true")) {
        val fixture = RenderFailureFixture { content -> mountComposableRoot("root", composable = content) }
        val second = FixtureSession("Synthetic account B")
        mountComposableRoot("second-root") { FixtureApp(second) }
        mountComposableRoot("controls") { fixture.Controls() }
        return
    }
    if (window.location.search.contains("remember=true")) {
        val fixture = RememberFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if (window.location.search.contains("lifecycle=true")) {
        val fixture = RootLifecycleFixture { session, fail ->
            mountComposableRoot("root", if (window.location.search.contains("scheduler=microtask")) MicrotaskScheduler() else null) {
                FixtureApp(session)
                if (fail) error("Synthetic failed mount")
            }
        }
        fixture.replace()
        val second = FixtureSession("Synthetic account B")
        renderComposableRoot("second-root") { FixtureApp(second) }
        mountComposableRoot("controls") { fixture.Controls() }
        return
    }
    if (window.location.search.contains("transport=true")) {
        val scheme = if (window.location.protocol == "https:") "wss" else "ws"
        val fixture = TransportFixture("$scheme://${window.location.host}/signals")
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if (window.location.search.contains("csp=true")) {
        val fixture = CspInteractionFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if (window.location.search.contains("scheduler=microtask")) {
        val session = FixtureSession()
        mountComposableRoot("root", MicrotaskScheduler()) { FixtureApp(session) }
        return
    }
    val session = FixtureSession()
    renderComposableRoot("root") { FixtureApp(session) }
    if (window.location.search.contains("roots=two")) {
        val second = FixtureSession("Synthetic account B")
        renderComposableRoot("second-root") { FixtureApp(second) }
    }
}
