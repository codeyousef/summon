package codes.yousef.summon.fixture
import codes.yousef.summon.renderComposableRoot
import codes.yousef.summon.mountComposableRoot
import codes.yousef.summon.runtime.wasmGetLocationSearch
fun main() {
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
    val session = FixtureSession()
    renderComposableRoot("root") { FixtureApp(session) }
    if (wasmGetLocationSearch()?.contains("roots=two") == true) {
        val second = FixtureSession("Synthetic account B")
        renderComposableRoot("second-root") { FixtureApp(second) }
    }
}
