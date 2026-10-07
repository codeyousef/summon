package codes.yousef.summon.fixture
import codes.yousef.summon.renderComposableRoot
import codes.yousef.summon.mountComposableRoot
import codes.yousef.summon.runtime.wasmGetLocationPathname
import codes.yousef.summon.runtime.wasmGetLocationSearch
@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@JsFun("() => (location.protocol === 'https:' ? 'wss://' : 'ws://') + location.host + '/signals'")
private external fun transportSignalUrl(): String

fun main() {
    if ((wasmGetLocationSearch() ?: "").contains("ownership=true")) {
        val fixture = LifecycleOwnershipFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if ((wasmGetLocationSearch() ?: "").contains("identity=true")) {
        val fixture = StableRenderingFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if ((wasmGetLocationSearch() ?: "").contains("safeContent=true")) {
        val fixture = SafeContentFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if ((wasmGetLocationSearch() ?: "").contains("privateRouting=true")) {
        val fixture = PrivateRoutingFixture(wasmGetLocationPathname() ?: "/")
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if ((wasmGetLocationSearch() ?: "").contains("router=true")) {
        val fixture = RouterFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if ((wasmGetLocationSearch() ?: "").contains("responsive=true")) {
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
    if ((wasmGetLocationSearch() ?: "").contains("failures=true")) {
        val fixture = RenderFailureFixture { content -> mountComposableRoot("root", composable = content) }
        val second = FixtureSession("Synthetic account B")
        mountComposableRoot("second-root") { FixtureApp(second) }
        mountComposableRoot("controls") { fixture.Controls() }
        return
    }
    if ((wasmGetLocationSearch() ?: "").contains("remember=true")) {
        val fixture = RememberFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if ((wasmGetLocationSearch() ?: "").contains("lifecycle=true")) {
        val fixture = RootLifecycleFixture { session, fail ->
            mountComposableRoot("root") {
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
    if ((wasmGetLocationSearch() ?: "").contains("accessibility=true")) {
        val fixture = AccessibilityFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if ((wasmGetLocationSearch() ?: "").contains("files=true")) {
        val fixture = FileLifecycleFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if ((wasmGetLocationSearch() ?: "").contains("virtualization=true")) {
        val fixture = VirtualizationFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if ((wasmGetLocationSearch() ?: "").contains("persistence=true")) {
        val fixture = PersistenceFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if ((wasmGetLocationSearch() ?: "").contains("transport=true")) {
        val fixture = TransportFixture(transportSignalUrl())
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    if ((wasmGetLocationSearch() ?: "").contains("csp=true")) {
        val fixture = CspInteractionFixture()
        mountComposableRoot("root") { fixture.Content() }
        return
    }
    val session = FixtureSession()
    renderComposableRoot("root") { FixtureApp(session) }
    if (wasmGetLocationSearch()?.contains("roots=two") == true) {
        val second = FixtureSession("Synthetic account B")
        renderComposableRoot("second-root") { FixtureApp(second) }
    }
}
